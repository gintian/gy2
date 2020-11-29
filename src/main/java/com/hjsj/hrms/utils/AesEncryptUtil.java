package com.hjsj.hrms.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 128bit 加密解密工具类
 * 
 * @author xiegh
 * 
 * @date 2018/1/23
 */





public class AesEncryptUtil {

    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "hjsoftjsencryptk";

    private static String IV = "hjsoftjsencryptk";


    /**
     * 加密方法
     * @param data  要加密的数据
     * @param key 加密key
     * @param iv 加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String aesEncrypt(String data, String key, String iv) {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"
            int blockSize = cipher.getBlockSize();
            //前端js 加密用的utf-8编码格式，此处需设置对应的编码格式，否则中文乱码 guodd 2019-05-07
            byte[] dataBytes = data.getBytes("utf-8");
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return new Base64().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * 解密方法
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv 解密iv
     * @param strict 是否严格模式，true:解密失败返回null；false:解密失败返回原始数据data
     * @return 解密的结果
     * @throws Exception
     */
    public static String aesDecrypt(String data, String key, String iv,boolean strict) {
    	
        try {
            byte[] encrypted1 = new Base64().decode(data);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original,"utf-8");
            return originalString.trim();//  这是空格  可以用trim()去除 奇葩吧  我也没见过
        } catch (Exception e) {
            e.printStackTrace();
            if(strict)
            	return null;
            else
            	return data;
        }
    }

    /**
     * 使用默认的key和iv加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String data) {
        return aesEncrypt(data, KEY, IV);
    }

    /**
     * 使用默认的key和iv解密，解密失败返回原数据
     * @param data
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String data) {
        return aesDecrypt(data, KEY, IV,false);
    }
    
    /**
     * 使用默认的key和iv解密,解密失败返回null
     * @param data
     * @return
     * @throws Exception
     */
    public static String aesDecryptStrict(String data) {
        return aesDecrypt(data, KEY, IV,true);
    }
}
