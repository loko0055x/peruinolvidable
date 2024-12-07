package com.alexanderdoma.peruinolvidable.utilies;

import java.util.ResourceBundle;

public class SQLSentencesManager {
    public static String getProperty(String key){
        ResourceBundle objResourceBundle = ResourceBundle.getBundle("keys.sql");
        return objResourceBundle.getString(key);
    }
}
