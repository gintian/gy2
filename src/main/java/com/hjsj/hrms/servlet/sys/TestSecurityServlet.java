/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.utils.PubFunc;

/**
 * @author Administrator
 *
 */
public class TestSecurityServlet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(test1("wgy", ""));
	}
	
	public static String test1(String userName, String password) {
		return PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userName+","+password));
	}

}
