/*
 * Created on 2005-12-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.decryptor;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;



/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TripleDES {     
    // The length of Encryptionstring should be 8 bytes and not be
    // a weak key  //CryptionData 
    // The initialization vector should be 8 bytes
    private final byte[] EncryptionIV = {34, 92, (byte)222, (byte)131, 2, 32, 11, (byte)222};//{( byte )0x22,( byte )0x5C,( byte )222,( byte )0x83,( byte )0x02,( byte )0x20,( byte )0x0B,( byte )0xDE};
    private final static String DES = "DESede/CBC/PKCS5Padding";
    private byte[] EncryptionString = {116, 77, 107, 53, (byte)216, (byte)165, 116, (byte)174, (byte)190, 94, (byte)234, (byte)181, 78, 107, (byte)152, (byte)238, 34, 64, 120, 113, (byte)235, (byte)215, 60, 108};
     /**
     * Saving key for encryption and decryption
     * @param EncryptionString String
     */
    public TripleDES(String EncryptionString) {
        //this.EncryptionString = {116, 77, 107, 53, 216, 165, 116, 174, 190, 94, 234, 181, 78, 107, 152, 238, 34, 64, 120, 113, 235, 215, 60, 108};
    }
    /**
     * Encrypt a byte array
     * @param SourceData byte[]
     * @throws Exception
     * @return byte[]
     * @加密数组
     */
    public byte[] EncryptionByteData(byte[] SourceData) throws Exception {
        byte[] retByte = null;

        // Create SecretKey object

        byte[] EncryptionByte = EncryptionString;
        DESedeKeySpec keyspec = new DESedeKeySpec(EncryptionByte);
           SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
           
           SecretKey Key = keyfactory.generateSecret(keyspec);
           
           IvParameterSpec IV = new IvParameterSpec(EncryptionIV);
           
           Cipher cipher = Cipher.getInstance(DES);
        // Initialize Cipher object
           cipher.init(Cipher.ENCRYPT_MODE, Key, IV);

        // Encrypting data
           
        retByte = cipher.doFinal(SourceData); 
        return retByte;
    }

    /**
     * Decrypt a byte array
     * @param SourceData byte[]
     * @throws Exception
     * @return byte[]
     * @解密数组
     */
    public byte[] DecryptionByteData(byte[] SourceData) throws Exception {
        byte[] retByte = null;

        // Create SecretKey object
        byte[] EncryptionByte = EncryptionString;
        DESedeKeySpec keyspec = new DESedeKeySpec(EncryptionByte);
           SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
           
           SecretKey Key = keyfactory.generateSecret(keyspec);
           
           IvParameterSpec IV = new IvParameterSpec(EncryptionIV);
           
           Cipher cipher = Cipher.getInstance(DES);
           cipher.init(Cipher.DECRYPT_MODE, Key, IV);

        // Decrypting data
          
        retByte = cipher.doFinal(SourceData);

        return retByte;
    }

    /**
     * Encrypt a string
     * @param SourceData String
     * @throws Exception
     * @return String
     * ·加密字符串
     */
    public String EncryptionStringData(String SourceData) throws Exception {
        String retStr = null;
        byte[] retByte = null;

        // Transform SourceData to byte array
        byte[] sorData = SourceData.getBytes("UTF8");

        // Encrypte data
        retByte = EncryptionByteData(sorData);

        // Encode encryption data
        retStr = Base64.encodeBase64String(retByte);

        return retStr;
    }

    /**
     * Decrypt a string
     * @param SourceData String
     * @throws Exception
     * @return String
     * 解密字符串
     */
    public String DecryptionStringData(String SourceData) throws Exception {
        String retStr = null;
        byte[] retByte = null;

        // Decode encryption data
        
        byte[] sorData = Base64.decodeBase64(SourceData);
        // Decrypting data
        retByte = DecryptionByteData(sorData);
        retStr = new String(retByte,"UTF8");
        return retStr;
    }    
    
    public static void main(String[] str) {
    	TripleDES td = new TripleDES("");
    	System.out.println( td.EncryptionString.length);     	
    }   
}
