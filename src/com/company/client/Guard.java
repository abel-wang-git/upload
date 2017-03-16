package com.company.client;

/**
 * Created by Administrator on 2017/3/14.
 */
public class Guard extends Thread {
    private Thread runnable;

    public Guard(Thread target) {
        super(target);
        this.runnable = target;
        this.setDaemon(true);
    }


    @Override
    public void run() {
        while(!runnable.isAlive()){
            System.out.println(runnable.isAlive());
            runnable.run();
        }
    }
}
