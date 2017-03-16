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
    @Override
    public void run() {
        {
            logger.error("��ʼ��ȡ�����ļ�");
            Properties p = new Properties();
            InputStream fin = Upload.class.getClassLoader().
                    getResourceAsStream("upload.properties");
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
                propertie.setSuffix(p.getProperty("suffix"));
                propertie.setBayonetId(p.getProperty("bayonetId"));
                propertie.setOrientation(p.getProperty("orientation"));
                propertie.setLane(p.getProperty("lane"));
                propertie.setMoveTo(p.getProperty("moveTo"));
                propertie.setIP(p.getProperty("IP"));
                propertie.setPort(Integer.parseInt(p.getProperty("port")));
                File file = new File(propertie.getMoveTo() + "/");
                if (!file.exists() && !file.isDirectory()) {
                    file.mkdir();
                }
                br.close();
                fin.close();
            } catch (IOException e) {
                logger.error("�����ļ���ȡ����"+e.getMessage());
            }
            int count=0;
            logger.error("�����ļ���ȡ�ɹ� ��ʼɨ���ļ�");
            //�̳߳�
            ExecutorService pool = Executors.newFixedThreadPool(3);
            while (true) {
                //ɨ���ļ�
                File file = FileRead.scanDir(propertie.getBaseDir());
                if (file != null) {
                    logger.error("------��ʼ�ϴ��ļ�" + file.getName());
                    byte[] content = FileRead.readFile(file);
                    if(content!=null){
                        UploadRun upload = new UploadRun(count, content);
                        pool.execute(upload);
                        count++;
                    }
                }

            }
        }
    }
}
