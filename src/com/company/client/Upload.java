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
public class Upload extends Thread {
    public static Propertie propertie = new Propertie();

    @Override
    public void run() {
        {
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
                //扫描文件
                File file = FileRead.scanDir(propertie.getBaseDir());
                if (file != null) {
                    File pic = new File(file.getPath().replace("_0.dat", "_1.jpg"));
                    System.out.println(count + "------开始上传文件" + file.getName());
                    byte[] content = FileRead.readFile(file);
                    if(content!=null){
                        UploadRun upload = new UploadRun(file, content);
                        pool.execute(upload);
                    }
                }
                count++;
            }
        }
    }
}
