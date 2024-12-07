package com.alexanderdoma.peruinolvidable.utilies;

import java.util.ResourceBundle;

public class DatabaseKeysManager {
    public static String getProperty(String key){
        ResourceBundle objResourceBundle = ResourceBundle.getBundle("keys.database");
        return objResourceBundle.getString(key);
    }
}
