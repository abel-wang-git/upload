package com.company.client;

import org.apache.log4j.Logger;

import javax.imageio.stream.FileImageInputStream;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by wanghuiwen on 17-3-13.
 *
 */
class FileRead {
    private static Logger logger = Logger.getLogger(FileRead.class);
    /**
     * 扫描目录
     */
    public static File scanDir(String baseDir) {
        File file = new File(baseDir);
        if (file == null) {
            return null;
        }
        if (file.isDirectory()) {
            File[] fileArr = orderByDate(file.listFiles());
            if (fileArr.length == 0) {
                return null;
            }
            for (File f : fileArr) {
                if (f.isDirectory()) {
                    File filetem = scanDir(f.getPath());
                    if (filetem != null) {
                        return filetem;
                    }
                } else {
                    if (f.getName().endsWith(".dat")) {
                        if (!f.renameTo(f)) {
                            logger.error(" file not "+f.getName());
                            continue;
                        }
                        return f;
                    }
                }
            }
        }
        return null;
    }

    //时间排序
    private static File[] orderByDate(File[] fs) {
        Arrays.sort(fs, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff < 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }
            public boolean equals(Object obj) {
                return true;
            }
        });
        return fs;
    }

    static byte[] readFile(File file) {
        byte[] result = null;
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            byte[] tem = new byte[1024];
            while (fi.read(tem, 0, 1024) != -1) {
                bos.write(tem);
            }
            result = dataFormat(bos.toByteArray(), file);
            bos.flush();
            bos.close();
            fi.close();
            File backup=new File(Upload.propertie.getMoveTo() + file.getParent());
            if(!backup.exists()&&!backup.isDirectory()) backup.mkdirs();
            file.renameTo(new File(backup+"/"+file.getName()));
            file.delete();
        } catch (FileNotFoundException e1) {
            logger.error("文件打开出错" +e1.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            try {
                if(fi!=null){
                    fi.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * 处理数据
     */
    private static byte[] dataFormat(byte[] data, File file) {
        //图片
        File pic = null;
        byte[] result = null;
        try {
            byte[] datas = new byte[100];
            //0-4
            String kkbs = "kkbs";
            for (int i = 0; i < kkbs.getBytes("GBK").length; i++) {
                datas[i] = kkbs.getBytes("GBK")[i];
            }
            //卡口编号4-8
            byte[] id = Upload.propertie.getBayonetId().getBytes("GBK");
            System.arraycopy(id, 0, datas, 8, id.length);
            //车牌 36开始
            String chePai;
            if (data[61] != 78 && data[61] != 65) {
                chePai = byteToString(data, 60, 8);
            } else {
                chePai = "-";
            }
            for (int i = 0; i < chePai.getBytes("GBK").length; i++) {
                datas[40 + i] = chePai.getBytes("GBK")[i];
            }
            //过车时间52开始
            String date = file.getName().split("_")[0];
            StringBuilder sb = dateFamart(date);
            for (int i = 0; i < sb.toString().getBytes("GBK").length; i++) {
                datas[54 + i] = sb.toString().getBytes("GBK")[i];
            }
            //方向
            int num = (byteToInt2(data, 224));
            datas[38] = (byte) num;

            //车道号
            int num2 = (byteToInt2(data, 16));
            datas[39] = (byte) num2;
            //号牌种类
            String typeStr;
            //限速
            int cloro=byteToInt2(data,112);
            int xiansu=0;
            if(cloro==1){
                cloro=2;
                typeStr = "01";
                 xiansu = (byteToInt2(data, 136));
            }else
            if(cloro==4){
                cloro=1;
                typeStr = "02";
                xiansu = (byteToInt2(data, 140));
            }else{
                cloro=9;
                typeStr="44";
            }
            for (int i = 0; i<typeStr.getBytes().length; i++) {
                datas[50 + i] = typeStr.getBytes("GBK")[i];
            }
            datas[53] = (byte) cloro;
            datas[79] = (byte) xiansu;
            //车速
            int speed = (byteToInt2(data, 20));
            datas[78] = (byte) speed;
            //处理图片
            result = getBytes(data, file, result, datas);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }catch (Exception e){
            result=null;
        }
        return result;
    }
    //图片处理
    private static byte[] getBytes(byte[] data, File file, byte[] result, byte[] datas) {
        File pic;
        pic = new File(file.getPath().replace("_0.dat", "_1.jpg").replace("new","old"));
        if (pic != null && pic.renameTo(pic)) {
            byte[] picByte = image2byte(pic);
            int length = picByte.length + datas.length;
            System.arraycopy(intToBytes2(length), 0, datas, 4, intToBytes2(length).length);
            result = mergeArray(datas, picByte);
        } else {
            logger.error("图片出错"+pic.getPath());
            result = null;
        }
        return result;
    }

    private static StringBuilder dateFamart(String date) {
        StringBuilder sb = new StringBuilder();
        sb.append(date.substring(0, 4)).append("-")
                .append(date.substring(4, 6)).append("-")
                .append(date.substring(6, 8)).append(" ")
                .append(date.substring(8, 10)).append(":")
                .append(date.substring(10, 12)).append(":")
                .append(date.substring(12, 14)).append(".")
                .append(date.substring(14, 17));
        return sb;
    }

    /**
     * 图片转byte
     */

    private static byte[] image2byte(File file) {
        byte[] data = null;
        FileImageInputStream input ;
        try {
            input = new FileImageInputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[(int) file.length()];
            int numBytesRead ;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            logger.error(ex1.getMessage());
        } catch (IOException ex1) {
            logger.error(ex1.getMessage());
        }
        return data;
    }

    /***
     * 合并字节数组
     *
     */
    private static byte[] mergeArray(byte[]... a) {
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
    private static int byteToInt2(byte[] b, int offset) {
        int value;
        value = (int) ((b[offset] & 0xFF)
                | ((b[offset + 1] & 0xFF) << 8)
                | ((b[offset + 2] & 0xFF) << 16)
                | ((b[offset + 3] & 0xFF) << 24));
        return value;
    }

    //byte转换为int
    private static String byteToString(byte[] b, int offset, int lenght) {
        byte[] tem = new byte[lenght];
        System.arraycopy(b, offset + 0, tem, 0, tem.length);
        String s = null;
        try {
            s = new String(tem, "GBK");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return s;
    }

    private static byte[] intToBytes2(int n) {
        byte[] b = new byte[4];

        for (int i = 0; i < 4; i++) {
            b[3 - i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }
}
