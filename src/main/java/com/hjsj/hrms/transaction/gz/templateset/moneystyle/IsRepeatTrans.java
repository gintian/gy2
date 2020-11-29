package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class IsRepeatTrans extends IBusiness{

	public void execute() throws GeneralException {
      String nstyleid=(String)this.getFormHM().get("nstyleid");
      String nitemid=(String)this.getFormHM().get("nitemid");
      String before=(String)this.getFormHM().get("before");
      String isVisable=(String)this.getFormHM().get("isVisable");
      int flag=isHave(nstyleid,nitemid,isVisable,before);
	  this.getFormHM().put("flag",String.valueOf(flag));
	  this.getFormHM().put("nitemid",nitemid);
      
	}
	public int isHave(String nstyleid,String nitemid,String isVisable,String before)
	{
		int flag=0;
		String sql="";
		if("new".equalsIgnoreCase(isVisable))
		{
		     sql = "select * from moneyitem where nstyleid='"+nstyleid+"' and nitemid='"+nitemid+"'";
		}
		else
		{
			sql="select * from moneyitem where nstyleid='"+nstyleid+"' and nitemid='"+nitemid+"' and nitemid<>'"+before+"'";
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		try
		{
			rs=dao.search(sql);
			while(rs.next())
			{
				flag=1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
