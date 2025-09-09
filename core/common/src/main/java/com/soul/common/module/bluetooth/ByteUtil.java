package com.soul.common.module.bluetooth;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.MeasureSpec;

public class ByteUtil {
    public ByteUtil() {
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src != null && src.length > 0) {
            byte[] var2 = src;
            int var3 = src.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte aSrc = var2[var4];
                int v = aSrc & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    public static String bytesToMac(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src != null && src.length > 0) {
            byte[] var2 = src;
            int var3 = src.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte aSrc = var2[var4];
                int v = aSrc & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv).append(":");
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    public static int[] bytesToDec(byte[] src) {
        int[] nb = new int[src.length];

        for(int i = 0; i < src.length; ++i) {
            int v = src[i];
            if (v >= 0) {
                nb[i] = v;
            } else {
                nb[i] = v & 255;
            }
        }

        return nb;
    }

    public static byte[] hexStringToByte(String[] src) {
        byte[] nb = new byte[src.length];

        for(int i = 0; i < src.length; ++i) {
            String v = src[i];
            nb[i] = hexToByte(v);
        }

        return nb;
    }

    public static byte[] stringToByte(String[] src) {
        byte[] nb = new byte[src.length];

        for(int i = 0; i < src.length; ++i) {
            int v = Integer.valueOf(src[i]);
            nb[i] = (byte)v;
        }

        return nb;
    }

    public static int[] hexStringToInt(String[] src) {
        int[] nb = new int[src.length];

        for(int i = 0; i < src.length; ++i) {
            String v = src[i];
            nb[i] = Integer.parseInt(v, 16);
        }

        return nb;
    }

    public static char[] hexStringToChar(String[] src) {
        char[] nb = new char[src.length];

        for(int i = 0; i < src.length; ++i) {
            String v = src[i];
            nb[i] = (char)Integer.parseInt(v, 16);
        }

        return nb;
    }

    public static byte hexToByte(String inHex) {
        return (byte)Integer.parseInt(inHex, 16);
    }

    public static char hexToChar(String inHex) {
        return (char)Integer.parseInt(inHex, 16);
    }

    @SuppressLint("WrongConstant")
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static int getStringLength(String s) {
        if (s == null) {
            return 0;
        } else {
            char[] c = s.toCharArray();
            int len = 0;

            for(int i = 0; i < c.length; ++i) {
                ++len;
                if (!isLetter(c[i])) {
                    ++len;
                }
            }

            return len;
        }
    }

    public static boolean isLetter(char c) {
        int k = 128;
        return c / k == 0;
    }
}
