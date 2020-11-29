package com.hjsj.hrms.transaction.general.query.quick;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SelectAllResultTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String infor=(String)this.getFormHM().get("infor");
			String sql = (String)this.getFormHM().get("sql");
			sql = PubFunc.decrypt(sql);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
		    this.frowset=dao.search(sql);
		    ArrayList list  = new ArrayList();
		    while(this.frowset.next())
		    {
		    	String objid="";
		    	if("2".equals(infor))
		    	{
		    		objid=this.frowset.getString("b0110");
		    	}else if("3".equals(infor))
		    	{
		    		objid=this.frowset.getString("e01a1");
		    	}else{
		    		objid=this.frowset.getString("dbase")+this.frowset.getString("a0100");
		    	}
		    	CommonData cd = new CommonData(objid,objid);
		    	list.add(cd);
		    }
		    this.getFormHM().put("list", list);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
