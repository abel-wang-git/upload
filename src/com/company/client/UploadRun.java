package com.company.client;

import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by wanghuiwen on 17-3-13.
 *
 */
public class UploadRun implements Runnable {
    private static Logger logger = Logger.getLogger(UploadRun.class);
    private Socket socket;
    private BufferedOutputStream ow;
    private int count;
    private byte[] content;
    private File file;

    public UploadRun(int count, byte[] content,File file) {
        this.count = count;
        this.content = content;
        this.file=file;
    }
    @Override
    public void run() {
        try {
            socket = new Socket(Upload.propertie.getIP(), Upload.propertie.getPort());
            if(socket!=null){
                ow = new BufferedOutputStream(socket.getOutputStream());
                ow.write(content);
                ow.flush();
                Upload.count--;
                logger.info("upload success file [[[[[["+file.getPath()+"]]]]]]"+count++);
            }
        } catch (ConnectException exception) {
            logger.error(exception.getMessage()+count);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            run();
        } catch (IOException e) {
            logger.error( e.getMessage()+Upload.count);
            try {
                Thread.sleep(5);
            } catch (InterruptedException xe) {
                e.printStackTrace();
            }
            run();
        } finally {
            try {
                if(ow!=null){
                    ow.close();
                }
                if( socket!=null){
                    socket.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

    }
}

