package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;


public class SelectKqReportTrans extends IBusiness {

	public void execute()throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String userbase = (String) hm.get("userbase");
		String code =(String)hm.get("code");
		code=getCode(code);
		String coursedate=(String)hm.get("coursedate");
		String kind=(String)hm.get("kind");
		String relatTableid=(String)hm.get("relatTableid");
		String condition=(String)this.getFormHM().get("condition");
		String returnURL=(String)this.getFormHM().get("returnURL");
		String report_sql="Select report_id,name,flag,tab_id from kq_report";  		
		ArrayList kq_report_lsit= new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	    try
	    {
	    	this.frowset=dao.search(report_sql);
	    	while(this.frowset.next())
	    	{
	    		RecordVo vo=new RecordVo("kq_report");
	    		vo.setString("report_id",this.getFrowset().getString("report_id"));
	    		vo.setString("name",this.getFrowset().getString("name"));
	    		vo.setString("flag",this.getFrowset().getString("flag"));
	    		vo.setString("tab_id",this.getFrowset().getString("tab_id"));
	    		kq_report_lsit.add(vo);
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }	    
	    this.getFormHM().put("kq_report_lsit",kq_report_lsit);
        this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("code",code);
		this.getFormHM().put("coursedate",coursedate);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("relatTableid",relatTableid);
		this.getFormHM().put("condition",condition);
		this.getFormHM().put("returnURL",returnURL);
	}
	/**
	 *判断是否code为空 
	 * */
	public String getCode(String code)throws GeneralException
	{
		if(code==null||code.length()<=0)
		{
		   if(!userView.isSuper_admin())
		   {
			   ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
				code=managePrivCode.getPrivOrgId(); 
		   }
	   }	   
	   return code;
    }
	
}
