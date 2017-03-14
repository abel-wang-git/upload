package com.company.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wanghuiwen on 17-3-13.
 *
 */
public class Upload implements Runnable  {
    public static Propertie propertie = new Propertie();

    @Override
    public void run() {
        Properties p = new Properties();
        InputStream fin = Upload.class.getClassLoader().
                getResourceAsStream("upload.properties");
        //初始化配置
        try {
            p.load(fin);
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

        int count = 0;
        //线程池
        ExecutorService pool = Executors.newFixedThreadPool(60);
        while (true) {
            try {
                //扫描文件
                File file = FileRead.scanDir(propertie.getBaseDir());
                if (file != null) {
                    //开始上传
                    System.out.println("开始上传文件" + file.getName());
                    UploadRun upload = new UploadRun(file);
                    pool.execute(upload);
                }
                System.out.println("第几次执行上传" + count);
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            count++;
        }
    }
}
