package com.hjsj.hrms.businessobject.sys;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


/**
 * java生成GUID随机数
 * <p>Title:GuidCreator.java</p>
 * <p>Description>:GuidCreator.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 23, 2011 10:37:24 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class GuidCreator {
	private String seedingString = "";
	private String rawGUID = "";
	private boolean bSecure = false;
	private static Random myRand;
	private static SecureRandom mySecureRand;

	private static String s_id;
    private int sublength=1;
	public static final int BeforeMD5 = 1;
	public static final int AfterMD5 = 2;
	public static final int FormatString = 3;
	private Random random=new Random();

	static {
		mySecureRand = new SecureRandom();
		long secureInitializer = mySecureRand.nextLong();
		myRand = new Random(secureInitializer);
		try {
			s_id = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Default constructor. With no specification of security option, this
	 * constructor defaults to lower security, high performance.
	 */
	public GuidCreator() {
	}

	/*
	 * Constructor with security option. Setting secure true enables each random
	 * number generated to be cryptographically strong. Secure false defaults to
	 * the standard Random function seeded with a single cryptographically
	 * strong random number.
	 */
	public GuidCreator(boolean secure) {
		bSecure = secure;
	}
	public GuidCreator(boolean secure,int sublength) {
		bSecure = secure;
		this.sublength=sublength;
	}
	/*
	 * Method to generate the random GUID
	 */
	private void getRandomGUID(boolean secure) 
	{
		MessageDigest md5 = null;
		StringBuffer sbValueBeforeMD5 = new StringBuffer();

		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error: " + e);
		}
		try {
			long time = System.currentTimeMillis();
			long rand = 0;

			if (secure) {
				rand = mySecureRand.nextLong();
			} else {
				rand = myRand.nextLong();
			}			
			sbValueBeforeMD5.append(s_id);
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(time));
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(rand));

			seedingString = sbValueBeforeMD5.toString();
			md5.update(seedingString.getBytes());

			byte[] array = md5.digest();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < array.length; ++j) {
				int b = array[j] & 0xFF;
				if (b < 0 * 10) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(b));
			}

			rawGUID = sb.toString();

		} catch (Exception e) {
			System.out.println("Error:" + e);
		}
	}

	public String createNewGuid(int nFormatType, boolean secure) {
		getRandomGUID(secure);
		String sGuid = "";
		if (BeforeMD5 == nFormatType) {
			sGuid = this.seedingString;
		} else if (AfterMD5 == nFormatType) {
			sGuid = this.rawGUID;
		} else {
			sGuid = this.toString();
		}
		return sGuid;
	}
	/**
	 * 
	 * @param nFormatType 2;3
	 * @param secure
	 * @param length  长度
	 * @return
	 */
	public String createRandomGuid() {
		getRandomGUID(this.bSecure);		
    	StringBuffer strSrc=new StringBuffer();
		strSrc.append("123456789");
		int codelen=6;
		StringBuffer checkNum=new StringBuffer();
		int index=0;
		index=random.nextInt(9);
		checkNum.append(strSrc.charAt(index));
		int start=Integer.parseInt(checkNum.toString());		
		String sGuid = rawGUID.toUpperCase();		
		if(this.sublength<20)
		{
			return sGuid.substring(start,this.sublength+start);
		}else {
			return sGuid.substring(0,this.sublength);
		}
		
	}
	public String createNewGuid(int nFormatType) {
		return this.createNewGuid(nFormatType, this.bSecure);
	}

	/*
	 * Convert to the standard format for GUID (Useful for SQL Server
	 * UniqueIdentifiers, etc.) Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
	 */
	@Override
    public String toString() {
		String raw = rawGUID.toUpperCase();
		StringBuffer sb = new StringBuffer();
		sb.append(raw.substring(0, 8));
		sb.append("-");
		sb.append(raw.substring(8, 12));
		sb.append("-");
		sb.append(raw.substring(12, 16));
		sb.append("-");
		sb.append(raw.substring(16, 20));
		sb.append("-");
		sb.append(raw.substring(20));

		return sb.toString();
	}
	public static void main(String args[])
	{ 
		for (int i=0; i< 2;i++) 
		{ 
			GuidCreator myGUID = new GuidCreator(false,8); 
			//System.out.println("Seeding String=" + myGUID.createNewGuid(GuidCreator.BeforeMD5));
	       // System.out.println("rawGUID=" + myGUID.createNewGuid(GuidCreator.AfterMD5));
	       // System.out.println("RandomGUID=" + myGUID.createNewGuid(GuidCreator.FormatString)); 
			 System.out.println("RandomGUID=" + myGUID.createRandomGuid()); 
	    } 
	}
}
