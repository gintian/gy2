/*
 * Created on 2005-11-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.hirelogin;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HireLoginForm extends ActionForm {

	
	  private String userName;    //用户名
	  private String passWord;    //用户密码
	  private String okpassWord;    //用户密码
	  private String messageReturn;//申请返回的提示信息
	  private String messageLogin;//登入的提示信息
	/**
	 * @return Returns the messageReturn.
	 */
	public String getMessageReturn() {
		return messageReturn;
	}
	/**
	 * @param messageReturn The messageReturn to set.
	 */
	public void setMessageReturn(String messageReturn) {
		this.messageReturn = messageReturn;
	}
	 /**
	 * @return Returns the passWord.
	 */
	public String getPassWord() {
		return passWord;
	}
	/**
	 * @param passWord The passWord to set.
	 */
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	  public ActionErrors validate(ActionMapping actionMapping,
	                               HttpServletRequest request) {
	    ActionErrors errors = new ActionErrors();
	    return errors;
	  }	 
	/**
	 * @return Returns the okpassWord.
	 */
	public String getOkpassWord() {
		return okpassWord;
	}
	/**
	 * @param okpassWord The okpassWord to set.
	 */
	public void setOkpassWord(String okpassWord) {
		this.okpassWord = okpassWord;
	}
	/**
	 * @return Returns the messageLogin.
	 */
	public String getMessageLogin() {
		return messageLogin;
	}
	/**
	 * @param messageLogin The messageLogin to set.
	 */
	public void setMessageLogin(String messageLogin) {
		this.messageLogin = messageLogin;
	}
}
