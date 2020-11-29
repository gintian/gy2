/*
 * Created on 2005-11-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

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
public class ZpPersonEnrollForm extends ActionForm {
	  private String password;
	  private String username;
	  public void setPassword(String password) {
	    this.password = password;
	  }
	  public String getPassword() {
	    return password;
	  }
	  public void setUsername(String username) {
	    this.username = username;
	  }
	  public String getUsername() {
	    return username;
	  }
	  @Override
      public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
	    /**@todo: finish this method, this is just the skeleton.*/
	    return null;
	  }
	  @Override
      public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
	  }
}
