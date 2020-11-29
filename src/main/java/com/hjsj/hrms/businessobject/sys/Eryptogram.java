package com.hjsj.hrms.businessobject.sys;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 加密
 * 解密 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class Eryptogram {
	private String  Algorithm ="DES";
    //定义 加密算法,可用 DES,DESede,Blowfish,AES,Rijndael
    private boolean  debug  = false ;
	   /**
	    * 构造子注解.
	    */
	    public Eryptogram ()
	    {
	    } 
	    /**
	     * 生成密钥
	     * @return byte[] 返回生成的密钥
	     * @throws exception 扔出异常.
	     */
	     public String getSecretKey () throws  Exception 
	    {
	         KeyGenerator  keygen  = KeyGenerator.getInstance (Algorithm );
	         SecretKey  deskey  = keygen.generateKey ();
	         if  (debug ) {
                 System.out.println ("生成密钥:"+byte2hex(deskey.getEncoded ()));
             }
	         return  new String(deskey.getEncoded());
	    }
	     /**
	　     * 字节码转换成16进制字符串
	　     * @param byte[] b 输入要转换的字节码
	　     * @return String 返回转换后的16进制字符串
	　     */
	    private String  byte2hex(byte [] b) 
	    {
	    	String  hs ="";
	        String  stmp ="";
	        for  (int  n =0 ;n <b.length ;n ++)
	        {
	            stmp =(java.lang.Integer.toHexString (b [n ] &  0XFF ));
	            if  (stmp.length ()==1 ) {
                    hs =hs +"0"+stmp ;
                } else {
                    hs =hs +stmp ;
                }
	            if  (n <b.length -1 ) {
                    hs =hs +":";
                }
	            
	        } 
	         return  hs.toUpperCase ();
	     } 
	    /**
	     * 将指定的数据根据提供的密钥进行加密
	     * @param input 需要加密的数据
	     * @param key 密钥
	     * @return byte[] 加密后的数据
	     * @throws Exception
	     */
	    public String  encryptData (String inputstr ,String keystr ) throws  Exception 
	    {
	    	 if(inputstr==null||inputstr.length()<=0) {
                 return "";
             }
	    	 if(keystr==null||keystr.length()<=0) {
                 return inputstr;
             }
	    	 byte [] input=inputstr.getBytes();
	    	 byte [] key=keystr.getBytes();
	         SecretKey  deskey  = new  javax.crypto.spec.SecretKeySpec (key ,Algorithm );
	         if(debug)
	         {
	             System.out.println ("加密前的二进串:"+byte2hex (input ));
	             System.out.println ("加密前的字符串:"+new  String (input ));
	         } 
	         Cipher  c1  =Cipher.getInstance (Algorithm );
	         c1.init (Cipher.ENCRYPT_MODE ,deskey );
	         byte [] cipherByte =c1.doFinal (input );
	         if  (debug ) {
                 System.out.println ("加密后的二进串:"+byte2hex (cipherByte));
             }
	         
	         return  new String(cipherByte) ;
	         
	    } 
	    /**
	     * 将给定的已加密的数据通过指定的密钥进行解密
	     * @param input 待解密的数据
	     * @param key 密钥
	     * @return byte[] 解密后的数据
	     * @throws Exception
	     */
	     public String decryptData (String inputstr ,String keystr) throws  Exception 
	    {
	    	 if(inputstr==null||inputstr.length()<=0) {
                 return "";
             }
	    	 if(keystr==null||keystr.length()<=0) {
                 return inputstr;
             }
	    	 byte [] input=inputstr.getBytes();
	    	 byte [] key=keystr.getBytes();
	         SecretKey  deskey  = new  javax.crypto.spec.SecretKeySpec (key ,Algorithm );
	         if  (debug ) {
                 System.out.println ("解密前的信息:"+byte2hex (input ));
             }
	         Cipher  c1  = Cipher.getInstance (Algorithm );
	         c1.init (Cipher.DECRYPT_MODE ,deskey );
	         byte [] clearByte =c1.doFinal (input );
	         if  (debug )
	        {
	             System.out.println ("解密后的二进串:"+byte2hex (clearByte ));
	             System.out.println ("解密后的字符串:"+(new  String (clearByte )));
	             
	        } return  new String(clearByte) ;
	    }
}
