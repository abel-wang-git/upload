package com.company.client;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class FileRead {


    /**
     * 读取数据文件数据
     */
    public static String readFile(File file) throws IOException {
        StringBuilder data = new StringBuilder();
        FileInputStream fr = new FileInputStream(file);
        InputStreamReader br = new InputStreamReader(fr);
        BufferedReader bread = new BufferedReader(br);
        String tem = null;
        while ((tem = bread.readLine()) != null) {
            data.append(tem);
        }
        bread.close();
        br.close();
        fr.close();
        return data.toString();
    }

    /**
     * 扫描目录
     */
    public static void scanDir(String baseDir, List<File> files) throws IOException {
        File file = new File(baseDir);
        if (file.isDirectory()) {
            File[] filearr = file.listFiles();
            for (File f : filearr) {
                if (f.isDirectory()) {
                    scanDir(f.getPath(), files);
                } else {
                    if (f.getName().endsWith(".iso")) {
                        files.add(f);
                    }
                    System.out.println(f.getName() + f.isDirectory());
                }
            }
        }
    }

    /**
     * 处理数据
     */
    public String data() {
        return "";
    }

    /**
     * 拷贝文件
     */
    public void move(File file, String toPath) {
    }
    /**
     * 读取配置文件
     */
}
