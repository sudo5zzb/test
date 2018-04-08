package com.bonc.usdp.zzb.common;

import java.io.File;

public class RegexTest {
    public static void main(String[] args) {
        String file = "C:\\Users\\zzb\\Desktop\\magicube-1.0.war";
        String fileName=file.substring(file.lastIndexOf(File.separator)+1,file.lastIndexOf("."));
        String currentDir=file.substring(0, file.lastIndexOf("."));
        String encryptionDir=currentDir+File.separator+fileName+"_encryption_dir";
        System.out.println(encryptionDir);
    }
}
