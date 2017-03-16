package com.company.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by wanghuiwen on 17-3-13.
 *
 */
public class UploadRun implements Runnable {
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
        System.out.println("启动线程"+files+" 开始上传--------------------------");
        try {
            socket = new Socket(Upload.propertie.getIP(), Upload.propertie.getPort());
            ow = new BufferedOutputStream(socket.getOutputStream());
            ow.write(content);
            ow.flush();
            System.out.println("启动线程"+files+"结束上传--------------------------");
        } catch (ConnectException exception) {
            run();
        } catch (IOException e) {
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
                e.printStackTrace();
            }
        }
    }
}

