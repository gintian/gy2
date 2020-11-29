package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class HasThisItemTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			String itemid = (String)this.getFormHM().get("itemid");
			String fieldset = (String)this.getFormHM().get("fieldset");
			String desc = (String)this.getFormHM().get("itemdescid");
			String msg = "0";
			boolean flag = this.isHave(itemid, fieldset,desc);
			IndexBo subset = new IndexBo(this.getFrameconn());
			/*
			 *业务字典 指标校验 不和指标体系一起校验  wangb 20180110
			if(flag&&subset.checkcode(itemid)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
			if(flag&&subset.checkname(desc)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
			if(flag&&subset.checkName("",desc)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
			*/
			if(!flag)
			{
				msg="1";
			}	
			this.getFormHM().put("msg",msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public boolean isHave(String itemid,String fieldsetid,String desc)
	{
		boolean flag = true;
		try
		{
			String sql = "select * from t_hr_busifield where UPPER(fieldsetid)='"+fieldsetid.toUpperCase()+"' and (UPPER(itemid)='"+itemid.toUpperCase()+"' or itemdesc='"+desc+"')";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			if(this.frowset.next())
			{
				flag = false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
