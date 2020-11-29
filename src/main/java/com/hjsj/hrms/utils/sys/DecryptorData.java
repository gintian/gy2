/*
 * Created on 2005-6-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.utils.sys;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DecryptorData {
	   // The initialization vector should be 8 bytes
    private final byte[] EncryptionIV = {34, 92, -34, -125, 2, 32, 11, -34};   //可以更改成任意的8个字节的字符
    private final static String DES = "DES/CBC/PKCS5Padding";
    private String EncryptionString =null;

    /**
     * Saving key for encryption and decryption
     * @param EncryptionString String
     */
    public DecryptorData(String  EncryptionString) {
       this.EncryptionString = EncryptionString;
    }

    /**
     * Encrypt a byte array
     * @param SourceData byte[]
     * @throws Exception
     * @return byte[]
     */
    public byte[] EncryptionByteData(byte[] SourceData) throws Exception {
        byte[] retByte = null;

        // Create SecretKey object

        byte[] EncryptionByte = EncryptionString.getBytes();
        DESKeySpec dks = new DESKeySpec(EncryptionByte);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Create IvParameterSpec object with initialization vector
        IvParameterSpec spec=new IvParameterSpec(EncryptionIV);

        // Create Cipter object
        Cipher cipher = Cipher.getInstance(DES);

        // Initialize Cipher object
        cipher.init(Cipher.ENCRYPT_MODE, securekey, spec);

        // Encrypting data
        retByte = cipher.doFinal(SourceData);
        return retByte;
    }

    /**
     * Decrypt a byte array
     * @param SourceData byte[]
     * @throws Exception
     * @return byte[]
     */
    public byte[] DecryptionByteData(byte[] SourceData) throws Exception {
        byte[] retByte = null;

        // Create SecretKey object
        byte[] EncryptionByte = EncryptionString.getBytes();
        DESKeySpec dks = new DESKeySpec(EncryptionByte);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Create IvParameterSpec object with initialization vector
        IvParameterSpec spec=new IvParameterSpec(EncryptionIV);

        // Create Cipter object
        Cipher cipher = Cipher.getInstance(DES);

        // Initialize Cipher object
        cipher.init(Cipher.DECRYPT_MODE, securekey, spec);

        // Decrypting data
        retByte = cipher.doFinal(SourceData);

        return retByte;
    }

    /**
     * Encrypt a string
     * @param SourceData String
     * @throws Exception
     * @return String
     */
    public String EncryptionStringData(String SourceData) throws Exception {
        String retStr = null;
        byte[] retByte = null;

        // Transform SourceData to byte array
        byte[] sorData = SourceData.getBytes();

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
     */
    public String DecryptionStringData(String SourceData) throws Exception {
        String retStr = null;
        byte[] retByte = null;

        // Decode encryption data

        byte[] sorData = Base64.decodeBase64(SourceData);

        // Decrypting data
        retByte = DecryptionByteData(sorData);
        retStr = new String(retByte);
        return retStr;
    }  
}
