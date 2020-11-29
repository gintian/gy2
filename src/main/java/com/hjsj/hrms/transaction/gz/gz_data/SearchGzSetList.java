package com.hjsj.hrms.transaction.gz.gz_data;

import com.hjsj.hrms.businessobject.gz.SalaryDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


/**
 *<p>Title:列出权限范围内的所有共享的薪资类别</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2009-9-10:下午02:43:36</p> 
 *@author fanzhiguo
 *@version 4.0
 */
public class SearchGzSetList extends IBusiness {

	public void execute() throws GeneralException {

		try
		{
		    SalaryDataBo pgkbo=new SalaryDataBo(this.getFrameconn(),this.userView);
		ArrayList list=pgkbo.searchGzSetList();
			this.getFormHM().put("itemid","all");
			this.getFormHM().put("condid","all");
			this.getFormHM().put("proright_str","");
			this.getFormHM().put("setlist", list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}

