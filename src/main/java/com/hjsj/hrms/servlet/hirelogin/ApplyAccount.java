/*
 * Created on 2005-11-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.hirelogin;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplyAccount extends Action {	
	     public ActionForward execute(ActionMapping mapping, ActionForm form,
	                               HttpServletRequest request,
	                               HttpServletResponse response){
      	HireLoginForm applyForm = (HireLoginForm) form;
            applyForm.setMessageReturn("");
         	applyForm.setPassWord("");
         	applyForm.setUserName("");
         	return mapping.findForward("hireloginsuccess");
	  }	
}
