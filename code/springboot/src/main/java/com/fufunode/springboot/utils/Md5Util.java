package com.fufunode.springboot.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * message-digest algorithm 5（信息-摘要算法）
 *
 * md5的长度，默认为128bit，也就是128个 0和1的 二进制串 。
 *
 * 128/4 = 32 换成 16进制 表示后，为32位了。
 */
public class Md5Util {

    // 测试方法
    public static void main(String[] args) {
        String pwd = "123456";
        System.out.println("加密前： " + pwd);
        System.err.println("加密后： " + Md5Util.md5(pwd));
    }

    /**
     * 生成md5
     *
     * @param message
     * @return
     */
    public static String md5(String message) {
        String md5str = "";
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2 将消息变成byte数组
            byte[] input = message.getBytes();

            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);

            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }
}
