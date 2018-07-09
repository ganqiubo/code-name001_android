package com.pojul.objectsocket.utils;

import java.security.MessageDigest;

public class EncryptionUtil {
	
	private static final String TAG = "EncryptionUtil";

	/**
     * 生成32位md5码
     * @param password
     * @return
     */
    public static String md5Encryption(String rawEncryption) {

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(rawEncryption.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (Exception e) {
        	LogUtil.i(TAG, "md5Encryption fail");
            return rawEncryption;
        }

    }
	
}
