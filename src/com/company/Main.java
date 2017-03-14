package com.company;

import com.company.client.Guard;
import com.company.client.Upload;

public class Main {

    public static void main(String[] args) {
        Upload up = new Upload();
        Guard guard = new Guard(up);
        guard.run();
    }
}
