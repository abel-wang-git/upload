package com.company.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8808);
            System.out.println("服务启动成功");
            while (true) {
                Socket socket = serverSocket.accept();
                InputStreamReader ow = new InputStreamReader(socket.getInputStream(), "GBK");
                BufferedReader bw = new BufferedReader(ow);
                String data = null;
                while ((data = bw.readLine()) != null) {
                    System.out.println(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
