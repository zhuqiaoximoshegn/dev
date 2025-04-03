package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;

public class Constants {
    public static Boolean IGNORE_TABLE_PREFIX;

    public static String SUFFIX_BEAN_PARAM;
    static{
        IGNORE_TABLE_PREFIX= Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM=PropertiesUtils.getString("suffix.bean.param");
    }

    public final static String[] SQL_DATE_TIME_TYPES=new String[]{"datetime","timestamp"};
    public final static String[] SQL_DATE_TYPES=new String[]{"date"};
    public final static String[] SQL_DECIMAL_TYPE=new String[]{"decimal","double","float"};
    public final static String[] SQL_STRING_TYPE=new String[]{"char","varchar","text","mediumtext","longtext"};
    public final static String[] SQL_INTEGER_TYPE=new String[]{"int","tinyint"};
    public final static String[] SQL_LONG_TYPE=new String[]{"bigint"};

}
