/**
 * 
 */
package com.hjsj.hrms.utils.sys;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.Security;
/**
 *<p>Title:DES_3</p> 
 *<p>Description:加密算法,主要考虑与建银科技的统一认证接口</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-12:下午05:50:03</p> 
 *@author cmq
 *@version 4.0
 */
public class DES_3 {
	private static String Algorithm = "DESede";
	static boolean debug = false;
	static {
       Security.addProvider(new com.sun.crypto.provider.SunJCE());
	}

	public static byte[] encode(byte[] input, byte[] key) throws Exception {
		SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key, Algorithm);
		Cipher c1 = Cipher.getInstance(Algorithm);
		c1.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] cipherByte = c1.doFinal(input);
		return cipherByte;
	}
	

	public static byte[] decode(byte[] input, byte[] key) throws Exception {
		SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key, Algorithm);
		Cipher c1 = Cipher.getInstance(Algorithm);
		c1.init(Cipher.DECRYPT_MODE, deskey);
		byte[] clearByte = c1.doFinal(input);
		
		return clearByte;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		byte[] arrOut = new byte[iLen / 2];

		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	public static String EnycrptDes(String str1, byte[] key) throws Exception {

		return byte2hex(encode(str1.getBytes(), key));
	}

	public static String DeEnycrptDes(String str1, byte[] key) throws Exception {
		return (new String(decode(hexStr2ByteArr(str1), key)));
	}
	/**
	 * 中建投资单点登录
	 * @param source
	 * @return
	 */
	public static String JT_DeEncrpt(String source)
	{
		/**固定口令,双方约定*/
		byte[] key = "JkO092?#98uqp?Qsb1|a2640".getBytes();	
		String decrpt_str="";
		try
		{
			decrpt_str = DES_3.DeEnycrptDes(source, key); //解密
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return decrpt_str;
	}

	public static void main(String args[]) {
		
		String Password = "userName=su&newPassword=&loginTime=1000";
		try {
			byte[] key = "JkO092?#98uqp?Qsb1|a2640".getBytes();
			String EnyPassword = DES_3.EnycrptDes(Password, key);  //加密
			System.out.println("eny pass=" + EnyPassword);
			String clearPassword = DES_3.DeEnycrptDes(EnyPassword, key); //解密
			System.out.println("deeny=" + clearPassword); 

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
