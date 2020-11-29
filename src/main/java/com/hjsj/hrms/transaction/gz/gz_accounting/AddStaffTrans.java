package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.GzAccountBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class AddStaffTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
		HashMap hm=this.getFormHM();
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		GzAccountBo gaccountbo = new GzAccountBo(this.frameconn,salaryid);
		
		hm.put("changeflag","1");
		hm.put("sqlstr",gaccountbo.sqlStr("1"));
		hm.put("column",gaccountbo.column("1"));
		hm.put("orderby","order by a0000");
		hm.put("where","from "+this.userView.getUserName()+"_gz_his_chg where changeflag=1");
		hm.put("tablenamelist",gaccountbo.fieldList());
		hm.put("tableidlist2",gaccountbo.changeList());
		hm.put("totallist",gaccountbo.totalValue(this.userView,"1"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
