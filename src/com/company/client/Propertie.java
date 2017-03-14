package com.company.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wanghuiwen on 17-3-14.
 */
public class Propertie {
    public static  String baseDir;
    public static String suffix;

    static {
        try {
            Properties p = new Properties();
            InputStream fin = Upload.class.getClassLoader().
                    getResourceAsStream("upload.properties");
            p.load(fin);
            baseDir=p.getProperty("basdir");
            suffix=p.getProperty("suffix");
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
