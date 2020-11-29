package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Hashtable;

public class AddPointTreeTrans extends IBusiness{
	public void execute()throws GeneralException{
		try
		{
		    // 权限设置 1 表示有------------待完善的功能
			String sql="select codeitemid from organization where codeitemid=parentid";
			String codeitemid="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				codeitemid=this.frowset.getString("codeitemid");
			}
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
		    appb.init();
		    appb.setReturnHt(null);
		    Hashtable ht=appb.analyseParameterXml();
		    String pointset_menu=(String)ht.get("pointset_menu");
		    String pointcode_menu=(String)ht.get("pointcode_menu");
		    String pointname_menu=(String)ht.get("pointname_menu");
		    DbWizard dbwizard=new DbWizard(this.getFrameconn());
		    if(dbwizard.isExistTable(pointset_menu,false)){
		    	  this.getFormHM().put("orgpoint", pointset_menu);
		    }else{
		    	 this.getFormHM().put("orgpoint", "");
		    }
		    this.getFormHM().put("khpid", pointcode_menu);
		    this.getFormHM().put("khpname", pointname_menu);
			this.getFormHM().put("codeitemid", codeitemid);
		    this.getFormHM().put("priv", "1");
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}
}
