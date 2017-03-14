package com.company.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class Server {
    public static void main(String[] args) {
        int count = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(8808);
            System.out.println("服务启动成功");
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedInputStream ow = new BufferedInputStream(socket.getInputStream());

                int data = 0;
                byte[] b = new byte[1024];
                while ((data = ow.read(b, 0, 1024)) != -1) {
                }
                System.out.println("第" + count + "次接受数据");
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
