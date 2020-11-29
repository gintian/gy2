/*
 * Created on 2005-11-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.valueobject.common;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HireUserView implements Serializable{
  private String username;
  private String userpassword;

/**
 * @return Returns the username.
 */
public String getUsername() {
	return username;
}
/**
 * @param username The username to set.
 */
public void setUsername(String username) {
	this.username = username;
}
/**
 * @return Returns the userpassword.
 */
public String getUserpassword() {
	return userpassword;
}
/**
 * @param userpassword The userpassword to set.
 */
public void setUserpassword(String userpassword) {
	this.userpassword = userpassword;
}
}
