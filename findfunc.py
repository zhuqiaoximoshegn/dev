import os
import sys
import re
from collections import defaultdict

IGNORE_DIRS = {
    ".git", ".idea", ".vscode",
    "node_modules", "target", "dist", "build",
    "vendor", "third_party", "deps", "lib"
}

EXTENSIONS = {".ts", ".tsx", ".js", ".rs", ".py", ".c", ".cpp"}

CONTROL_KEYWORDS = {
    "if", "for", "while", "switch", "catch", "return"
}

def is_ignored(path):
    return any(p in IGNORE_DIRS for p in path.split(os.sep))

def is_code_file(fn):
    return any(fn.endswith(ext) for ext in EXTENSIONS)

# ---------- Level 1 ----------

def extract_level1_name(line):
    line = line.strip()

    # TS-style override async resolve(): Promise<void> { ... }
    # Requirement: record the whole signature BEFORE "{", not just the function name.
    m = re.search(r"\boverride\s+async\s+[^{]+(?=\{)", line)
    if m:
        return m.group(0).strip()

    patterns = [
        (r"\bclass\s+([A-Za-z_][A-Za-z0-9_]*)", 1),
        (r"\bstruct\s+([A-Za-z_][A-Za-z0-9_]*)", 1),
        (r"\bimpl\s+([A-Za-z_][A-Za-z0-9_]*)", 1),
        (r"\bfn\s+([A-Za-z_][A-Za-z0-9_]*)", 1),
        (r"\bfunction\s+([A-Za-z_][A-Za-z0-9_]*)", 1),
        (r"\bprivate\s+([A-Za-z_][A-Za-z0-9_]*)", 1),
        (r"\boverride\s+([A-Za-z_][A-Za-z0-9_]*)", 1),
    ]
    for p, gi in patterns:
        m = re.search(p, line)
        if m:
            return m.group(gi)
    return None

def extract_level1_blocks(lines):
    blocks = []
    block_stack = []  
    brace_depth = 0

    for i, line in enumerate(lines):
        # 1️⃣ 在处理 { 之前，看看这一行是不是一级结构起点
        if "{" in line:
            name = extract_level1_name(line)
            if name:
                block_stack.append({
                    "name": name,
                    "start": i,
                    "start_depth": brace_depth
                })

        # 2️⃣ 更新 brace depth（注意：可能一行多个 {）
        brace_depth += line.count("{")
        brace_depth -= line.count("}")

        # 3️⃣ 看有没有 block 在这一行结束
        #    可能一次结束多个
        while block_stack and brace_depth == block_stack[-1]["start_depth"]:
            b = block_stack.pop()
            blocks.append((b["name"], b["start"], i))

    return blocks

# ---------- Level 2 ----------

def extract_level2_functions(lines):
    funcs = []

    for line in lines:
        line = line.strip()

        m = re.match(r"([A-Za-z_][A-Za-z0-9_]*)\s*\(", line)
        if m:
            name = m.group(1)
            if name not in CONTROL_KEYWORDS:
                funcs.append(name)

    seen = set()
    result = []
    for f in funcs:
        if f not in seen:
            seen.add(f)
            result.append(f)

    return result

# ---------- Level 2 blocks (inside Level 1) ----------

def extract_level2_name(line):
    """
    Extract a "level2" function/method name from a single line.
    We treat level2 as methods/functions defined inside the current level1 block.
    """
    s = line.strip()

    # TS/JS method: async foo( ... ) { ... } / public foo( ... ) { ... } / foo( ... ) { ... }
    m = re.match(
        r"^(?:(?:public|private|protected)\s+)?(?:(?:static)\s+)?(?:(?:async)\s+)?([A-Za-z_][A-Za-z0-9_]*)\s*\(",
        s,
    )
    if m and m.group(1) not in CONTROL_KEYWORDS:
        return m.group(1)

    # JS/TS function: function foo(...) { ... }
    m = re.match(r"^function\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(", s)
    if m:
        return m.group(1)

    # Rust/Python/C-like: fn foo(...) { ... } / def foo(...):
    m = re.match(r"^(?:fn|def)\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(", s)
    if m:
        return m.group(1)

    return None


def extract_level2_blocks(lines):
    """
    Given lines for ONE level1 block, return (name, start_idx, end_idx) for each level2 block.
    Indices are relative to the given `lines` slice.
    """
    blocks = []
    block_stack = []
    brace_depth = 0

    for i, line in enumerate(lines):
        if "{" in line:
            name = extract_level2_name(line)
            if name:
                block_stack.append({
                    "name": name,
                    "start": i,
                    "start_depth": brace_depth
                })

        brace_depth += line.count("{")
        brace_depth -= line.count("}")

        while block_stack and brace_depth == block_stack[-1]["start_depth"]:
            b = block_stack.pop()
            blocks.append((b["name"], b["start"], i))

    return blocks


# ---------- Level 3 (inside Level 2) ----------

def extract_level3_functions(lines, exclude_names=None):
    """
    Extract function-like calls inside a level2 block.
    """
    exclude_names = set(exclude_names or [])
    found = []
    seen = set()

    for raw in lines:
        line = raw.strip()
        for m in re.finditer(r"\b([A-Za-z_][A-Za-z0-9_]*)\s*\(", line):
            name = m.group(1)
            if name in CONTROL_KEYWORDS:
                continue
            if name in exclude_names:
                continue
            if name not in seen:
                seen.add(name)
                found.append(name)

    return found


# ---------- Main ----------

def generate(repo):
    for dp, _, files in os.walk(repo):
        if is_ignored(dp):
            continue
        folder = os.path.relpath(dp, repo)
        for f in files:
            if not is_code_file(f):
                continue

            path = os.path.join(dp, f)
            try:
                lines = open(path, errors="ignore").readlines()
            except:
                continue

            blocks = extract_level1_blocks(lines)
            if not blocks:
                continue

            print(folder)
            print(f"  {f}")

            for name, s, e in blocks:
                print(f"    {name}")

                level1_lines = lines[s:e+1]
                level2_blocks = extract_level2_blocks(level1_lines)

                # Fallback: if we can't form level2 blocks, keep old behavior (flat scan) for level2 output.
                if not level2_blocks:
                    level2 = extract_level2_functions(level1_lines)
                    for fn in level2:
                        print(f"      {fn}")
                    continue

                for l2_name, l2_s, l2_e in level2_blocks:
                    print(f"      {l2_name}")
                    l2_lines = level1_lines[l2_s:l2_e+1]
                    level3 = extract_level3_functions(l2_lines, exclude_names={l2_name})
                    for l3 in level3:
                        print(f"        -> {l3}")
            print()

if __name__ == "__main__":
    generate(sys.argv[1])
