package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
//9028000407
public class SaveInterviewingRevertCodeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String a0100=(String)this.getFormHM().get("a0100");
			String interviewingRevertItemid=(String)this.getFormHM().get("interviewingRevertItemid");
			String dbName=(String)this.getFormHM().get("dbName");
			a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
			a0100=PubFunc.getReplaceStr(a0100);
			dbName=PubFunc.getReplaceStr(dbName);
			String interviewingCodeValue=(String)this.getFormHM().get("interviewingCodeValue");
			this.save(a0100, dbName, interviewingRevertItemid, interviewingCodeValue);
			this.getFormHM().put("interviewingRevertItemCodeList",EmployNetPortalBo.interviewingRevertItemCodeList);
		    this.getFormHM().put("interviewingCodeValue",interviewingCodeValue);
	    	this.getFormHM().put("interviewingRevertItemid",EmployNetPortalBo.interviewingRevertItemid);
	        this.getFormHM().put("a0100", a0100);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public synchronized void save(String a0100,String dbName,String interviewingRevertItemid,String interviewingCodeValue)
	{
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(" update "+dbName+"a01 set "+interviewingRevertItemid);
			buf.append("='"+interviewingCodeValue+"' where a0100='"+a0100+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(buf.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
