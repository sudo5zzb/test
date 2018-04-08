package com.bonc.usdp.zzb.common;

import java.util.Random;
import java.util.stream.IntStream;

public class randomTime {

    private static String getRandomTime(){
        Random random=new Random();
        return new StringBuilder("2017-").append(random.nextInt(12)+1).append("-").append(random.nextInt(28)+1).toString();
    }
    public static void main(String[] args) {
        IntStream.range(0,1000).forEach(i-> System.out.println(getRandomTime()));
    }
}
