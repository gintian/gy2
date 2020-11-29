package com.hjsj.hrms.businessobject.sys;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * <p>Title: MyAuthenticator </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-10-27 下午3:41:09</p>
 * @author guodd
 * @version 1.0
 */
public class MyAuthenticator extends Authenticator {

	String password;
	String username;
    public MyAuthenticator(String username,String password){
    	    this.username = username;
    	    this.password = password;
    }
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username,password);
	}
}
