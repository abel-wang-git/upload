package com.company.client;

import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
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

    public UploadRun(int files, byte[] content) {
        this.count = files;
        this.content = content;
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
                logger.info("end upload"+count--);
            }
        } catch (ConnectException exception) {
            logger.error(exception.getMessage()+count);
            run();
        } catch (IOException e) {
            logger.error( e.getMessage()+Upload.count);
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

