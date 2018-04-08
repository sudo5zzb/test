package com.bonc.usdp.zzb.test.classEncrypt;

import java.util.concurrent.TimeUnit;

public class loopJava {
    public static void main(String[] args) {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("running->>>");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
