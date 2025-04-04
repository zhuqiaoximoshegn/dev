package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.utils.DateUtils;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class BuildComment {
    public static void createClassComment(BufferedWriter bw,String classComment) throws Exception{
        /*
         */
        bw.write("/**");
        bw.newLine();
        bw.write(" * @description:"+classComment);
        bw.newLine();
        bw.write(" * @author:"+ Constants.AUTHOR_COMMENT);
        bw.newLine();
        bw.write(" * @date:"+ DateUtils.format(new Date(),DateUtils._YYYYMMDD));
        bw.newLine();
        bw.write("*/");
        bw.newLine();
    }
    public static void createFieldComment(BufferedWriter bw,String fieldComment) throws Exception{
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * "+fieldComment==null?"":fieldComment);
        bw.newLine();

        bw.write("\t */");
        bw.newLine();
    }
    public static void createMethodComment(){

    }
}
