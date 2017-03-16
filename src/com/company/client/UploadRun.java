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
    private int files;
    private byte[] content;

    public UploadRun(int files, byte[] content) {
        this.files = files;
        this.content = content;
    }
    @Override
    public void run() {
        logger.info("启动线程"+files+" 开始上传--------------------------");
        try {
            socket = new Socket(Upload.propertie.getIP(), Upload.propertie.getPort());
            ow = new BufferedOutputStream(socket.getOutputStream());
            ow.write(content);
            ow.flush();
            logger.info("启动线程"+files+"结束上传--------------------------");
        } catch (ConnectException exception) {
            logger.error(exception.getMessage()+exception.getStackTrace()+content);
            run();
        } catch (IOException e) {
            logger.error( e.getMessage()+ e.getStackTrace()+content);
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
                logger.error(e.getMessage()+e.getStackTrace());
            }
        }
    }
}

