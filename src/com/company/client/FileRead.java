package com.company.client;

import javax.imageio.stream.FileImageInputStream;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class FileRead {
    public static void main(String[] args) {
        try {
            scanDir("C:/18");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 扫描目录
     */
    public static File scanDir(String baseDir) throws IOException {
        File file = new File(baseDir);
        if (file.isDirectory()) {
            File[] fileArr = file.listFiles();
            for (File f : fileArr) {
                if (f.isDirectory()) {
                    scanDir(f.getPath());
                } else {
                    RandomAccessFile raf = new RandomAccessFile(f, "rw");
                    FileChannel channel = raf.getChannel();
                    FileLock lock = null;
                    try {
                        lock = channel.lock();
                    } catch (OverlappingFileLockException e) {
                        System.out.println(f.getName()+"文件被锁定 跳过");
                        continue;
                    }
                    if (lock != null) {
                        if (f.getName().endsWith(".dat")) {
                            System.out.println("扫描的文件" + f);
                            channel.close();
                            raf.close();
                            return f;
                        }

                    }

                }
            }
        }
        return null;
    }

    /**
     * 处理数据
     */
    public static byte[] dataFormat(String data, File file) {
        byte[] datas = new byte[100];

        try {
            //卡口编号
            String code = Upload.propertie.getBayonetId();
            for (int i = 0; i < code.getBytes("GBK").length; i++) {
                datas[4 + i] = code.getBytes("GBK")[i];
            }
            //方向类型
            String orientation = Upload.propertie.getOrientation();
            for (int i = 0; i < orientation.getBytes("GBK").length; i++) {
                datas[34 + i] = orientation.getBytes("GBK")[i];
            }
            //车道号
            String lane = Upload.propertie.getLane();
            for (int i = 0; i < lane.getBytes("GBK").length; i++) {
                datas[35] = lane.getBytes("GBK")[i];
            }
            //车牌 36开始
            String chepai = data.substring(60, 67);
            for (int i = 0; i < chepai.getBytes("GBK").length; i++) {
                datas[35 + i] = chepai.getBytes("GBK")[i];
            }
            //号牌种类
            //TODO
            //过车时间48开始
            String date = file.getName().split("_")[0];
            for (int i = 0; i < date.getBytes("GBK").length; i++) {
                datas[47 + i] = date.getBytes()[i];
            }
            //车速
            //TODO
            //限速
            //TODO
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //图片
        File pic = null;
        try {
            pic = new File(file.getPath().replace("_0.dat", "_1.jpg"));
            if(pic!=null){
                byte[] picbyte = image2byte(pic);
                pic.renameTo(new File(Upload.propertie.getMoveTo() + pic.getName()));
                System.out.println("移动图片"+Upload.propertie.getMoveTo() + pic.getName());
                pic.delete();
                System.out.println("删除图片"+pic.getName());
                return mergeArray(datas, picbyte);
            }
        } catch (Exception e) {
            System.out.println("找不到图片"+file.getPath().replace("_0.dat", "_1.jpg"));
        }
        return datas;
    }

    /**
     * 图片转byte
     */

    private static byte[] image2byte(File file) {
        byte[] data = null;
        FileImageInputStream input = null;
        try {
            input = new FileImageInputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

    /***
     * 合并字节数组
     *
     * @param a
     * @return
     */
    public static byte[] mergeArray(byte[]... a) {
        // 合并完之后数组的总长度
        int index = 0;
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i].length;
        }
        byte[] result = new byte[sum];
        for (int i = 0; i < a.length; i++) {
            int lengthOne = a[i].length;
            if (lengthOne == 0) {
                continue;
            }
            // 拷贝数组
            System.arraycopy(a[i], 0, result, index, lengthOne);
            index = index + lengthOne;
        }
        return result;
    }

    /**
     * 拷贝文件
     */
    public void move(File file, String toPath) {
        file.renameTo(new File(toPath));
    }
}
