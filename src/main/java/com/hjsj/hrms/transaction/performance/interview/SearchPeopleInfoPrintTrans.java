package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchPeopleInfoPrintTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("id");
			String plan_id=PubFunc.decrypt((String)this.getFormHM().get("plan_id"));
			String a0100=PubFunc.decrypt((String)this.getFormHM().get("a0100"));
			RecordVo vo = new RecordVo("per_plan");
			vo.setString("plan_id",plan_id);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			String template_id=vo.getString("template_id");
			String object_type=vo.getString("object_type");
			String dataflag="<CARDSTYLE>P</CARDSTYLE><TEMPLATEID>"+template_id+"</TEMPLATEID><PLANID>"+plan_id+"</PLANID>";
		    String objid="";
		    if("2".equals(object_type))
		    {
	    		String sql = "select a0100,a0101 from usra01 where a0100='"+a0100+"'";
	    	    this.frowset=dao.search(sql);
	    	    while(this.frowset.next())
		        {
		        	objid="<NBASE>USR</NBASE><ID>"+a0100+"</ID><NAME>"+this.frowset.getString("a0101")+"</NAME>";
	    	    }
		    }
		    else
		    {
		    	String sql = "select codeitemdesc,codeitemid from organization where codeitemid='"+a0100+"'";
	    	    this.frowset=dao.search(sql);
	    	    while(this.frowset.next())
	    	    {
		        	objid="<NBASE>USR</NBASE><ID>"+a0100+"</ID><NAME>"+this.frowset.getString("codeitemdesc")+"</NAME>";
		  
	    	    }
		    }
		    this.getFormHM().put("d", dataflag);
		    this.getFormHM().put("o",objid);
		    this.getFormHM().put("tabid",tabid);
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
