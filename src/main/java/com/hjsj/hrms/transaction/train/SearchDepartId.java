package com.hjsj.hrms.transaction.train;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-18:18:48:33</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchDepartId extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		String temp="0";
	
		temp=this.userView.getUserOrgId(); 
		if("".equals(temp) || temp==null)
		{
			temp="0";
		}
		this.getFormHM().put("depid",temp);
	}

}
