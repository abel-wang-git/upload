package com.company.client;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wanghuiwen on 17-3-13.
 *
 */
public class Upload extends Thread {
    public static Propertie propertie = new Propertie();
    @Override
    public void run() {
        {
            Properties p = new Properties();
            InputStream fin = Upload.class.getClassLoader().
                    getResourceAsStream("upload.properties");
            //初始化配置
            InputStreamReader br =null;
            try {
                br =new InputStreamReader(fin,"GBK");
            } catch (UnsupportedEncodingException e) {
                System.out.print("编码出错");
            }
            try {
                p.load(br);
                propertie.setBaseDir(p.getProperty("baseDir"));
                propertie.setSuffix(p.getProperty("suffix"));
                propertie.setBayonetId(p.getProperty("bayonetId"));
                propertie.setOrientation(p.getProperty("orientation"));
                propertie.setLane(p.getProperty("lane"));
                propertie.setMoveTo(p.getProperty("moveTo"));
                propertie.setIP(p.getProperty("IP"));
                propertie.setPort(Integer.parseInt(p.getProperty("port")));
                fin.close();
            } catch (IOException e) {
                System.out.println("配置文件读取出错");
            }
            //线程池
            ExecutorService pool = Executors.newFixedThreadPool(3);
            while (true) {
                //扫描文件
                File file = FileRead.scanDir(propertie.getBaseDir());
                if (file != null) {
                    System.out.println("------开始上传文件" + file.getName());
                    byte[] content = FileRead.readFile(file);
                    if(content!=null){
                        UploadRun upload = new UploadRun(file, content);
                        pool.execute(upload);
                    }
                }
            }
        }
    }
}
