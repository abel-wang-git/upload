package com.company;

import com.company.client.Guard;
import com.company.client.Upload;

public class Main {
    public static void main(String[] args) {
        Thread one = new Upload();
        Thread guard= new Guard(one);
        guard.run();
    }
}
