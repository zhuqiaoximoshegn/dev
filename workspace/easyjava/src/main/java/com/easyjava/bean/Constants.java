package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;


public class Constants {
    public static String AUTHOR_COMMENT;
    public static Boolean IGNORE_TABLE_PREFIX;
    public static String PATH_JAVA="java";
    public static String PATH_RESOURCE="resoures";

    public static String SUFFIX_BEAN_PARAM;

    //need to be neglected
    public static String IGNORE_BEAN_TOJSON_FILED;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    //date serialization
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;

    //date deserializatioN
    public static String BEAN_DATE_UNFORMAT_EXPRESSION;
    public static String BEAN_DATE_UNFORMAT_CLASS;

    public static String PATH_BASE;
    public static String PACKAGE_BASE;
    public static String PATH_PO;
    public static String PATH_PARAM;
    public static String PACKAGE_PO;

    static{
        AUTHOR_COMMENT=PropertiesUtils.getString("author.comment");
        IGNORE_TABLE_PREFIX= Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM=PropertiesUtils.getString("suffix.bean.param");

        IGNORE_BEAN_TOJSON_FILES=PropertiesUtils.getString("ignore.bean.tojson.filed");
        IGNORE_BEAN_TOJSON_EXPRESSION=PropertiesUtils.getString("ignore.bean.tojson.expression");

        //date serialization
        IGNORE_BEAN_TOJSON_CLASS=PropertiesUtils.getString("ignore.bean.tojson.class");
        BEAN_DATE_FORMAT_EXPRESSION=PropertiesUtils.getString("bean.date.format.expression");

        //date deserialization
        BEAN_DATE_FORMAT_CLASS=PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_UNFORMAT_EXPRESSION=PropertiesUtils.getString("bean.date.unformat.expression");
        BEAN_DATE_UNFORMAT_CLASS=PropertiesUtils.getString("bean.date.unformat.class");



        PATH_BASE=PropertiesUtils.getString("path.base");
        //PO
        PACKAGE_BASE=PropertiesUtils.getString("package.base");
        PATH_BASE=PATH_BASE+PATH_JAVA+"/"+PACKAGE_BASE;


        PATH_PO=PropertiesUtils.getString("package.po");
        PATH_PO=PATH_BASE+"/"+PATH_PO.replace(".","/");
//        PATH_PARAM=PropertiesUtils.getString("package.param");
        PACKAGE_PO=PACKAGE_BASE+"."+PATH_PO;
    }

    public final static String[] SQL_DATE_TIME_TYPES=new String[]{"datetime","timestamp"};
    public final static String[] SQL_DATE_TYPES=new String[]{"date"};
    public final static String[] SQL_DECIMAL_TYPE=new String[]{"decimal","double","float"};
    public final static String[] SQL_STRING_TYPE=new String[]{"char","varchar","text","mediumtext","longtext"};
    public final static String[] SQL_INTEGER_TYPE=new String[]{"int","tinyint"};
    public final static String[] SQL_LONG_TYPE=new String[]{"bigint"};

    public static void main(String[] args){
        System.out.println(PATH_PO);
    }
}
