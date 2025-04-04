package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.DateUtils;
import com.easyjava.utils.StringUtils;
import com.sun.deploy.util.ArrayUtil;

import java.io.*;
import java.util.logging.Logger;

public class BuildPo {
    private static final Logger logger=LoggerFactory.getLogger(BuildPo.class);
    public static void execute(TableInfo tableInfo){
        File folder=new File(Constants.PATH_PO);
        if (!folder.exists()){
            folder.mkdirs();
        }
//        File file =new File(folder, tableInfo.getBeanName()+".java");
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        File poFile=new File(folder,tableInfo.getBeanName()+".java");

        OutputStream out=null;
        OutputStreamWriter outw=null;
        BufferedWriter bw=null;
        try {
            out=new FileOutputStream(poFile);
            outw=new OutputStreamWriter(out,"utf8");
            bw=new BufferedWriter(outw);


            bw.write("import java.io.Serializable;");
            bw.newLine();
            bw.newLine();

            if(tableInfo.getHaveDateTime()||tableInfo.getHaveDate()){
                bw.write("import java.util.Data;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS+";");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS+";");
                bw.newLine();
            }

            //properties to be neglected
            Boolean haveIgnoreBean=false;
            for(FieldInfo field:tableInfo.getFieldList()) {
                if(ArrayUtil.contains(Constants.IGNORE_BEAN_TOJSON_FILED.split(","),field.getPropertyName())){
                    haveIgnoreBean=true;
                    break;
                }
            }
            if(haveIgnoreBean){
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS+";");
                bw.newLine();
            }


            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();

            bw.write("package "+Constants.PACKAGE_PO+";");
            bw.newLine();
            bw.newLine();

            //comment class
            BuildComment.createClassComment(bw,tableInfo.getComment());

            //BuildComment.createMethodComment();

            bw.write("public class "+tableInfo.getBeanName()+" implements Serializable {");
            bw.newLine();

            for(FieldInfo field:tableInfo.getFieldList()){
                BuildComment.createFieldComment(bw,field.getComment());

                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,field.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();

                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                }

                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,field.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();

                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                }

                if(ArrayUtil.contains(Constants.IGNORE_BEAN_TOJSON_CLASS.split(","),field.getPropertyName())){
                    bw.write("\t"+String.format(Constants.IGNORE_BEAN_TOJSON_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                }

                bw.write("\tprivate"+field.getJavaType()+" "+field.getPropertyName()+";");
                bw.newLine();
                bw.newLine();
            }

            //add set and get
            for(FieldInfo field:tableInfo.getFieldList()) {
                String tempField= StringUtils.upperCaseFirstLetter(field.getPropertyName());
                bw.write("\tpublic void set"+tempField+"("+field.getJavaType()+" "+field.getPropertyName()+"} {");
                bw.newLine();
                bw.write("\t\tthis."+field.getPropertyName()+" = "+field.getPropertyName()+";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();


                bw.write("\tpublic"+field.getJavaType()+" get"+tempField+"{} {");
                bw.newLine();
                bw.write("\t\treturn this."+field.getPropertyName()+";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }

            StringBuffer toString=new StringBuffer();
            //override tostring
            Integer index=0;
            for(FieldInfo field:tableInfo.getFieldList()) {
                index++;
                toString.append(field.getComment()+":\" + {"+field.getPropertyName()+" == null ? \"空\" : "+field.getPropertyName()+"}");
                if(index<tableInfo.getFieldList().size()){
                    toString.append(" + ").append("\",");
                }

            }
            String toStringStr=toString.toString();
            toStringStr="\""+toStringStr;

            bw.write("\t@override");
            bw.newLine();
            bw.write("\tpublic String toString {} {");
            bw.newLine();
            bw.write("\t\treturn "+toStringStr+";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.write("}");
            bw.flush();


        }catch (Exception e){
            logger.error("create po failed",e);

        }finally {
            if(bw!=null){
                try{
                    bw.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            if(outw!=null){
                try {
                    outw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
