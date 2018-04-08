package com.bonc.usdp.zzb.common;

import com.bonc.usdp.odk.common.file.FileUtil;

import java.io.IOException;

public class byteTest {

    public static void main(String[] args) throws IOException {
        String file="D:\\gitRepo\\ClassEncrypt\\decrypt\\compiled-files\\spring-core\\ClassReader(4.3.15).class";
        byte[] bytes = FileUtil.readFile2ByteArray(file);
        for (int i = 0; i < bytes.length; i++) {
            if(i%15==0){
                System.out.print("\n");
            }
            System.out.printf("%#x,",bytes[i]);
        }

    }
}
