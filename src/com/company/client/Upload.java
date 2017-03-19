package com.company.client;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wanghuiwen on 17-3-13.
 *
 */
public class Upload extends Thread {
    private static Logger logger = Logger.getLogger(Upload.class);
    public static Propertie propertie = new Propertie();
    public static  int count=0;
    @Override
    public void run() {
        {
            logger.error("��ʼ��ȡ�����ļ�");
            Properties p = new Properties();
            InputStream fin = null;
            try {
                fin = new FileInputStream("upload");
            } catch (FileNotFoundException e) {
                logger.error("�Ҳ��������ļ�");
            }
            //��ʼ������
            InputStreamReader br =null;
            try {
                br =new InputStreamReader(fin,"GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error("��ȡ�����ļ��������");
            }
            try {
                p.load(br);
                propertie.setBaseDir(p.getProperty("baseDir"));
                logger.error("baseDir="+propertie.getBaseDir());
                propertie.setSuffix(p.getProperty("suffix"));
                propertie.setBayonetId(p.getProperty("bayonetId"));
                logger.error("bayonetId="+propertie.getBayonetId());
                propertie.setOrientation(p.getProperty("orientation"));
                propertie.setLane(p.getProperty("lane"));
                propertie.setMoveTo(p.getProperty("moveTo"));
                logger.error("moveTo="+propertie.getMoveTo());
                propertie.setIP(p.getProperty("IP"));
                propertie.setPort(Integer.parseInt(p.getProperty("port")));
//                propertie.setPicture(p.getProperty("PicDir"));
                File file = new File(propertie.getMoveTo() + "/");
                if (!file.exists() && !file.isDirectory()) {
                    file.mkdir();
                }
                br.close();
                fin.close();
            } catch (IOException e) {
                logger.error("�����ļ���ȡ����"+e.getMessage());
            }
            logger.error("�����ļ���ȡ�ɹ� ��ʼɨ���ļ�");
            //�̳߳�
            ExecutorService pool = Executors.newFixedThreadPool(3);
            while (true) {
                if (count>10){
                    continue;
                }
                //ɨ���ļ�
                File file = FileRead.scanDir(propertie.getBaseDir());
                if (file != null) {
                    byte[] content = FileRead.readFile(file);
                    if(content!=null){
                        logger.info("��ʼ�ϴ��ļ�"+ file.getName()+file.lastModified());
                        count++;
                        UploadRun upload = new UploadRun(count, content);
                        pool.execute(upload);
                    }
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    run();
                }
            }
        }
    }
}
