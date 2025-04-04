package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.BuildBase;
import com.easyjava.builder.BuildPo;
import com.easyjava.builder.BuildTable;

import java.util.List;

public class RunApplication {
    public static void main(String[] args){
        List<TableInfo> tableInfoList=BuildTable.getTables();
        BuildBase.execute();

        for(TableInfo tableInfo:tableInfoList){
            BuildPo.execute(tableInfo);

        }
    }
}
