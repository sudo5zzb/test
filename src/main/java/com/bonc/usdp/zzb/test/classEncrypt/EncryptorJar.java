package com.bonc.usdp.zzb.test.classEncrypt;

import com.bonc.usdp.odk.common.object.ObjectUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class EncryptorJar {

    private static byte[] flags = "blockchain".getBytes();


    public static void main(String[] args) {
        String filePath = "D:\\mvnRepo\\org\\springframework\\spring-core\\4.3.9.RELEASE\\spring-core-4.3.9.RELEASE.jar";

        try {
            ByteArrayOutputStream tmpBaos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            File srcFile = new File(filePath);
            File dstFile = new File(filePath.substring(0, filePath.lastIndexOf(".")) + "_encrypted.jar");
            FileOutputStream dstFos = new FileOutputStream(dstFile);
            JarOutputStream dstJos = new JarOutputStream(dstFos);
            JarFile srcJar = new JarFile(srcFile);

            for (Enumeration<JarEntry> enumeration = srcJar.entries(); enumeration.hasMoreElements(); ) {
                JarEntry entry = enumeration.nextElement();
                InputStream is = srcJar.getInputStream(entry);
                int len;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    tmpBaos.write(buf, 0, len);
                }
                byte[] bytes = tmpBaos.toByteArray();
                String name = entry.getName();
                if (needTobeEncrypted(entry)) {
                    bytes = encrypt(bytes);
                }
                JarEntry ne = new JarEntry(name);
                dstJos.putNextEntry(ne);
                dstJos.write(bytes);
                tmpBaos.reset();
            }
            srcJar.close();
            dstJos.close();
            dstFos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean needTobeEncrypted(JarEntry entry) {
        boolean result = false;
        String name = entry.getName();
        if (entry != null && name.endsWith(".class")) {
            //判断注解
            if (name.contains("org/springframework/asm/ClassReader")) {
                result = true;
            }
        }
        return result;
    }

    private static byte[] encrypt(byte[] bytes) {
        //加密
        if (ObjectUtil.isEmpty(bytes) || bytes.length <= 8) {
            return null;
        }
        byte[] encryptedBytes = new byte[bytes.length + 10];
        //保留魔数
        for (int i = 0; i < 8; i++) {
            encryptedBytes[i] = bytes[i];
        }
        //插入分隔符
        for (int i = 0; i < flags.length; i++) {
            encryptedBytes[8 + i] = flags[i];
        }
        //字符加密

        for (int i = 8; i < bytes.length; i++) {
            encryptedBytes[10 + i] = (byte) (bytes[i] ^ 0x23);
        }
        return encryptedBytes;
    }


}
