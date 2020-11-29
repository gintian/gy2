package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckCardEmpTreeTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	String id=(String)this.getFormHM().get("id");
    	String flag="0";
    	String sql="select 1 from kq_shift_group";
    	if(id==null||id.length()<=0|| "root".equals(id))
    	{
    		id=RegisterInitInfoData.getKqPrivCode(userView);
    	}
    	if(id!=null&&id.length()>0)
    	{
    		sql=sql+" where org_id='"+id+"'";
    	}
    	
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		this.frowset=dao.search(sql);
    		if(this.frowset.next())
    			flag="1";
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	this.getFormHM().put("flag", flag);
	}
}
