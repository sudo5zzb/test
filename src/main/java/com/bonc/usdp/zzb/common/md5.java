package com.bonc.usdp.zzb.common;

import com.bonc.usdp.odk.common.security.MD5Util;

import java.security.NoSuchAlgorithmException;

public class md5 {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String s = MD5Util.md5("123");
        System.out.println(s);
    }
}
