package com.company.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wanghuiwen on 17-3-13.
 */
public class Upload {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(60);

        while (true) {
            List<File> files = new ArrayList<>();
            try {
                FileRead.scanDir("/home/wanghuiwen/资料", files);
                if (files.size() > 0) {
                    UploadRun upload = new UploadRun(files);
                    pool.submit(upload);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
