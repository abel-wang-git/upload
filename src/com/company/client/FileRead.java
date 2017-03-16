package com.company.client;

import javax.imageio.stream.FileImageInputStream;
import java.io.*;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class FileRead {

    /**
     * 扫描目录
     */
    public static File scanDir(String baseDir) {
        File file = new File(baseDir);
        if (file.isDirectory()) {
            File[] fileArr = file.listFiles();
            for (File f : fileArr) {
                if (f.isDirectory()) {
                    scanDir(f.getPath());
                } else {
                    if (f.getName().endsWith(".dat")) {
                        if(!f.renameTo(f)){
                            System.out.println("文件占用跳过");
                            continue;
                        }
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
                file.renameTo(new File(Upload.propertie.getMoveTo()+"/" + file.getName()));
                System.out.println("移动文件" + Upload.propertie.getMoveTo() +"/"+ file.getName());
                file.delete();
                System.out.println("删除" + file.getName());
        }catch (FileNotFoundException e1){
            e1.printStackTrace();
            System.out.println("文件打开出错");
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
            for (int i = 0; i < id.length; i++) {
                datas[8 + i] = id[i];
            }
            //车牌 36开始
            String chePai ;
            if (data[60] != 78 && data[61] != 65) {
                chePai= byteToString(data, 60, 8);
            } else {
                chePai="-";
            }
            for (int i = 0; i<chePai.getBytes("GBK").length; i++) {
                datas[40 + i] = chePai.getBytes("GBK")[i];
            }
            //过车时间52开始
            String date = file.getName().split("_")[0];
            StringBuilder sb = dateFamart(date);
            for (int i = 0; i < sb.toString().getBytes("GBK").length; i++) {
                datas[53 + i] = sb.toString().getBytes("GBK")[i];
            }
            //方向
            int num = (byteToInt2(data, 224));
            datas[38] = (byte) num;

            //车道号
            int num2 = (byteToInt2(data, 16));
            datas[39] = (byte) num2;

            //号牌种类
            int type = (byteToInt2(data, 108));
            String typeStr;
            if (type < 10&&type>0) {
                typeStr = "0" + type;
            }else if(type<=0){
                typeStr="01";
            }
            else {
                typeStr = Integer.toString(type);
            }
            for (int i = 0; i < 2; i++) {
                datas[50 + i] = typeStr.getBytes("GBK")[i];
            }

            //限速
            int tpe = byteToInt2(data, 124);

            int xiansu = (byteToInt2(data, 136));

            int xiansu1 = (byteToInt2(data, 140));
            datas[78] = (byte) xiansu;

            //车速
            int speed = (byteToInt2(data, 20));
            datas[77] = (byte) speed;

            //处理图片
            result = getBytes(data, file, result, datas);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    //图片处理
    private static byte[] getBytes(byte[] data, File file, byte[] result, byte[] datas) {
        File pic;
        pic = new File(file.getPath().replace("_0.dat", "_1.jpg"));
        if (pic!= null && pic.renameTo(pic)) {
            System.out.println("图片处理"+pic.getName());
            byte[] picByte = image2byte(pic);
            int length = picByte.length + datas.length;
            for (int i = 0; i < intToBytes2(length).length; i++) {
                datas[4 + i] = intToBytes2(length)[i];
            }
            result = mergeArray(datas, picByte);
        } else {
            System.out.println("图片出错");
            result =null;
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
        FileImageInputStream input = null;
        try {
            input = new FileImageInputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[(int)file.length()];
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

    public static byte[] intToBytes2(int n) {
        byte[] b = new byte[4];

        for (int i = 0; i < 4; i++) {
            b[3 - i] = (byte) (n >> (24 - i * 8));

        }
        return b;
    }
}
