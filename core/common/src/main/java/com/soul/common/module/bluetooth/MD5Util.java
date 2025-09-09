package com.soul.common.module.bluetooth;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public MD5Util() {
    }

    public static String encrypt(String sourceStr) {
        String result = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte[] b = md.digest();
            StringBuilder buf = new StringBuilder("");

            for(int offset = 0; offset < b.length; ++offset) {
                int i = b[offset];
                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }

            result = buf.toString();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        }

        return result;
    }

    public static byte[] encrypt(byte[] sourceStr) {
        byte[] result = new byte[20];

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr);
            return md.digest();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
            return result;
        }
    }
}
