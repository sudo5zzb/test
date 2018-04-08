package com.bonc.usdp.zzb.test.classEncrypt;

import com.bonc.usdp.odk.common.collection.CollectionUtil;
import com.bonc.usdp.odk.common.file.FileUtil;
import com.bonc.usdp.odk.common.object.ObjectUtil;
import com.bonc.usdp.odk.common.string.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Encryptor3 {

    private static byte[] flags = "blockchain".getBytes();


    public static void main(String[] args) {
        String classDir = "D:\\tmp\\encrypt\\WEB-INF\\classes";
        String workDir = "D:\\tmp";
        String packagePrefix = "com.bonc.usdp.magicube";
        try {
            ByteArrayOutputStream tmpBaos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            List<String> classFiles = FileUtil.getCascadeFileList(classDir, ".+.class");
            if (CollectionUtil.isEmpty(classFiles)) {
                return;
            }
            for (String classFile : classFiles) {
                String className = parseClassName(classFile);
                if (needTobeEncrypted(className)) {
                    System.out.println("Encrypt class \"" + className + "\"");
                    encrypt(classFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encrypt(String classFile) {
        if (StringUtil.isEmpty(classFile)) {
            return;
        }
        try {
            byte[] originBytes = FileUtil.readFile2ByteArray(classFile);
            if (originBytes == null || originBytes.length < 1) {
                return;
            }
            byte[] encryptedBytes = encrypt(originBytes);
            overwriteBytes(classFile, encryptedBytes);
        } catch (IOException e) {
            System.out.println("Read bytes of classFile \"" + classFile + "\" failed.");
            System.out.println(e.getMessage());
        }
    }

    private static void overwriteBytes(String classFile, byte[] encryptedBytes) {
        if (StringUtil.isEmpty(classFile) || encryptedBytes == null || encryptedBytes.length < 1) {
            return;
        }
        File file = new File(classFile);
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            return;
        }
        file.delete();
        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(encryptedBytes);
        } catch (IOException e) {
            System.out.println("OverwriteBytes of classFile \"" + classFile + "\" failed.");
            System.out.println(e.getMessage());
        }
    }

    private static String parseClassName(String classFile) {
        String className = null;
        if (StringUtil.isNotEmpty(classFile)) {
            className = classFile.substring(classFile.indexOf("WEB-INF" + File.separator + "classes" + File.separator) + 16).replace("\\", ".");
        }
        return className;
    }

    private static boolean needTobeEncrypted(String className) {
        boolean result = false;
        if (StringUtil.isNotEmpty(className) && className.contains("com.bonc.usdp.magicube")) {
            result = true;
        }
        return result;
    }

    private static byte[] encrypt(byte[] bytes) {
        //加密
        if (ObjectUtil.isEmpty(bytes) || bytes.length <= 8) {
            return null;
        }
        byte[] encryptedBytes = new byte[bytes.length];
        //保留魔数
        for (int i = 0; i < 8; i++) {
            encryptedBytes[i] = bytes[i];
        }
        //字符加密

        for (int i = 8; i < bytes.length; i++) {
            encryptedBytes[i] = (byte) (bytes[i] ^ 0x07);
        }
        return encryptedBytes;
    }


}
