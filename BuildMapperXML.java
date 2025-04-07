package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import javafx.beans.binding.StringBinding;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BuildMapperXML {
    private static final Logger logger=LoggerFactory.getLogger(BuildMapperXML.class);
    private static final String BASE_COLUMN_LIST="base_column_list";
    private static final String BASE_QUERY_CONDITION="base_query_condition";
    private static final String QUERY_CONDITION="query_condition";

    public static void execute(TableInfo tableInfo){

        File folder=new File(Constants.PATH_MAPPERS_XMLS);
        if (!folder.exists()){
            folder.mkdirs();
        }
        String className=tableInfo.getBeanName()+Constants.SUFFIX_MAPPERS;

        File poFile=new File(folder,className+".xml");

        OutputStream out=null;
        OutputStreamWriter outw=null;
        BufferedWriter bw=null;
        try {
            out=new FileOutputStream(poFile);
            outw=new OutputStreamWriter(out,"utf8");
            bw=new BufferedWriter(outw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
            bw.newLine();
            bw.write("\t\t \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\""+Constants.PACKAGE_MAPPERS+"."+className+"\">");
            bw.newLine();

            bw.write("\t<!--mapper-->");
            bw.newLine();
            String poClass=Constants.PACKAGE_PO+"."+tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"base_result_map\" type=\""+poClass+"\">");

            FieldInfo idField =null;
            Map<String,List<FieldInfo>> KeyIndexMap=tableInfo.getKeyIndexMap();
            for(Map.Entry<String,List<FieldInfo>> entry:KeyIndexMap.entrySet()){
                if("PRIMARY".equals(entry.getKey())){
                    List<FieldInfo> fieldInfoList=entry.getValue();
                    if(fieldInfoList.size()==1){
                        idField=fieldInfoList.get(0);
                        break;
                    }
                }
            }


            for(FieldInfo fieldInfo:tableInfo.getFieldList()){
                bw.write("\t<!-- " +fieldInfo.getComment()+"-->");
                bw.newLine();
                String key="";
                if(idField!=null && fieldInfo.getPropertyName().equals(idField.getPropertyName())){
                    bw.write(" <id column=\"id\" property=\"id\"/>");
                    key="id";
                }else{
                    bw.write(" <result column=\"id\" property=\"id\"/>");
                    key="result";
                }
                bw.write("\t\t <"+key+" column=\""+ fieldInfo.getPropertyName()+"\" property=\""+fieldInfo.getPropertyName()+"\"/>");
                bw.newLine();
            }

            bw.write("\t</resultMap>");
            bw.newLine();

            //query column
            bw.write("\t<!--to query column generally-->");
            bw.newLine();

            bw.write("\t<sql id=\""+BASE_COLUMN_LIST+"\">");
            bw.newLine();
            StringBuilder columnBuilder=new StringBuilder();
            for (FieldInfo fieldInfo:tableInfo.getFieldList()){
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            String columnBuilderStr=columnBuilder.substring(0,columnBuilder.lastIndexOf(","));
            bw.write("\t\t"+columnBuilderStr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //basic query conditions





            bw.write("</mapper>");
            bw.flush();

        }catch (Exception e){
            logger.error("create mapper xmls failed",e);

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
