package com.hjsj.hrms.transaction.app_news;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:ReturnAppNews.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class ReturnAppNews extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		this.getFormHM().put("type","receive");
	}

}
