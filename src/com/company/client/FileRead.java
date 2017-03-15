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
                    if (f.getName().endsWith(".dat")) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    public static byte[] readFile(File file) {
        byte[] result = null;
        FileInputStream fi =null;
        try {
                fi=new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
                byte[] tem = new byte[1024];
                while (fi.read(tem, 0, 1024) != -1) {
                    bos.write(tem);
                }
                result=dataFormat(bos.toByteArray(),file);
                bos.flush();
                bos.close();
                fi.close();
                file.renameTo(new File(Upload.propertie.getMoveTo() + file.getName()));
                System.out.println("移动文件" + Upload.propertie.getMoveTo() + file.getName());
                if(file.delete()){
                    System.out.println("删除" + file.getName());
                }else{
                    System.out.println("删除失败");
                }
        }catch (FileNotFoundException e1){
            e1.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 处理数据
     */
    public static byte[] dataFormat(byte[] data, File file) {
        byte[] datas = new byte[100];
            //卡口编号
        byte[] id = Upload.propertie.getBayonetId().getBytes();
        for (int i = 0; i < id.length; i++) {
            datas[4 + i] = id[i];
            }
        //方向
        int num = (byteToInt2(data, 196));
        datas[34] = (byte) num;

        //车道号
        int num2 = (byteToInt2(data, 16));
        datas[35] = (byte) num2;
        String chepai = byteToString(data, 60, 8);
        String weishibie = "-";
        //车牌 36开始
        if (data[60] != 78 && data[61] != 65) {
            for (int i = 0; i < 8; i++) {
                datas[36 + i] = data[60 + i];
            }
        } else {
            for (int i = 0; i < weishibie.getBytes().length; i++) {
                datas[36 + i] = weishibie.getBytes()[i];
            }
        }


        //号牌种类
        int type = (byteToInt2(data, 108));
        String typestr = "";
        if (type < 10) {
            typestr = "0" + type;
        } else {
            typestr = Integer.toString(type);
        }
        for (int i = 0; i < 2; i++) {
            datas[46 + i] = typestr.getBytes()[i];
        }
        //限速
        int tpe = byteToInt2(data, 124);
        int xiansu = (byteToInt2(data, 136));
        int xiansu1 = (byteToInt2(data, 140));
        datas[73] = (byte) xiansu;
        //过车时间48开始
        String date = file.getName().split("_")[0];
        StringBuilder sb = new StringBuilder();
        sb.append(date.substring(0, 3)).append("-").append(date.substring(3, 5)).append("-").append(date.substring(5, 7)
        ).append(" ").append(date.substring(7, 9)).append(":").append(date.substring(9, 11)).append(":")
                .append(date.substring(11, 13)).append(".").append(date.substring(13, 16));
        for (int i = 0; i < sb.toString().getBytes().length; i++) {
            datas[48 + i] = sb.toString().getBytes()[i];
            }

        //车速
        int speed = (byteToInt2(data, 24));
        datas[72] = (byte) speed;

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

    //byte转换为int
    public static int byteToInt2(byte[] b, int offset) {
        int value = 0;
        value = (int) ((b[offset] & 0xFF)
                | ((b[offset + 1] & 0xFF) << 8)
                | ((b[offset + 2] & 0xFF) << 16)
                | ((b[offset + 3] & 0xFF) << 24));
        return value;
    }

    //byte转换为int
    public static String byteToString(byte[] b, int offset, int lenght) {
        byte[] tem = new byte[lenght];
        for (int i = 0; i < tem.length; i++) {
            tem[i] = b[offset + i];
        }
        String s = null;
        try {
            s = new String(tem, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }
}
