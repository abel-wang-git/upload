package com.company.client;

import java.io.*;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class UploadRun implements Runnable {
    private Socket socket;
    private BufferedOutputStream ow;
    private File files;

    public UploadRun(File files) {
        this.files = files;
    }

    @Override
    public void run() {
        System.out.println("启动线程");
        try {
            RandomAccessFile raf = new RandomAccessFile(files, "rw");
            FileChannel channel = raf.getChannel();
            try {
                if (channel.tryLock() != null) {
                    String tem ;
                    StringBuilder data = new StringBuilder();
                    while ((tem= raf.readLine())!= null) {
                        data.append(tem);
                    }
                    socket = new Socket(Upload.propertie.getIP(),Upload.propertie.getPort());
                    ow = new BufferedOutputStream(socket.getOutputStream());
                    ow.write(FileRead.dataFormat(data.toString(), files));
                    ow.flush();
                    channel.close();
                    raf.close();
                    files.renameTo(new File(Upload.propertie.getMoveTo() + files.getName()));
                    System.out.println("移动文件"+Upload.propertie.getMoveTo() + files.getName());
                    files.delete();
                    System.out.println("删除"+files.getName());

                }
            } catch (OverlappingFileLockException e) {
                System.out.println("文件已经锁定");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ow != null) {
                    ow.close();
                }
                if (socket != null) {
                    socket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
