package com.hjsj.hrms.test;

import com.hjsj.hrms.utils.PubFunc;

import java.text.DecimalFormat;

public class TestDecimalFormat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double ff=99999999.232;
		String pattern = "###.00";
		DecimalFormat df=new DecimalFormat(pattern);
		String value=df.format(ff);
		System.out.println("value="+value);
		System.out.println("value="+DoFormatDecimal("99999999",2));
		String ssss="hellowrolda adf adfadsfasdfasdfasdfsafdasdfasdfasdfasdfadsfadsfadfsadfasdfasdf";
		ssss=PubFunc.compressData(ssss);
		ssss=PubFunc.decompressData(ssss);
		System.out.println("after unzip="+ssss);
	}

	public static String DoFormatDecimal(String value, int decimalwidth) {
		String pattern = "";
		double fldValue = 0.0f; //float ->double chenmengqing changed at 20050807 for 99999999->100000000.00
		if (decimalwidth > 0) {
			pattern = "###.";
			for (int nI = 0; nI < decimalwidth; nI++)
				pattern += "0";
		} else {
			pattern = "###";
		}
		if (value != null && value.length() > 0) {
			System.out.println("pattern="+pattern);
			fldValue=Double.parseDouble(value);
			//fldValue = Float.parseFloat(value);
			value = new DecimalFormat(pattern).format(fldValue).trim();
		}
		String str="chen%kdj\rdkfd%dkdkd\r*";
		str=str.replaceAll("%","-");
		System.out.println("-->"+str);
		str=str.replaceAll("\\*","+");

		System.out.println("-->"+str);
		str=str.replaceAll("\\?",">");		
		System.out.println("-->"+str);
		str=str.replaceAll("#",">");		
		System.out.println("-->"+str);		
		return value;
	}	
}
