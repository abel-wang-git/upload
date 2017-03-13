package com.company.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class UploadRun implements Runnable {
    private Socket socket;
    private OutputStreamWriter ow;
    private BufferedWriter bw;
    private List<File> files;

    public UploadRun(List<File> files) {
        this.files = files;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("127.0.0.1", 8808);
            ow = new OutputStreamWriter(socket.getOutputStream(), "GBK");
            bw = new BufferedWriter(ow);
            for (File f : files) {
                String data = FileRead.readFile(f);
                bw.write(data);
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                ow.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
