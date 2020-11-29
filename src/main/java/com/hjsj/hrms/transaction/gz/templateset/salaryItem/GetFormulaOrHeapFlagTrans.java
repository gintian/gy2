package com.hjsj.hrms.transaction.gz.templateset.salaryItem;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:GetFormulaOrHeapFlagTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 5, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class GetFormulaOrHeapFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String fieldid=(String)this.getFormHM().get("fieldid");
			String salaryid=(String)this.getFormHM().get("salaryid");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("salaryset");
			vo.setInt("salaryid",Integer.parseInt(salaryid));
			vo.setInt("fieldid",Integer.parseInt(fieldid));
			vo=dao.findByPrimaryKey(vo);
			String formual=vo.getString("formula")!=null?vo.getString("formula"):vo.getString("itemdesc");
			String heapFlag=vo.getString("heapflag")!=null?vo.getString("heapflag"):"";
			this.getFormHM().put("fieldid",fieldid);
			this.getFormHM().put("formula",SafeCode.encode(formual));
			this.getFormHM().put("heapFlag",heapFlag);
			this.getFormHM().put("itemtype",vo.getString("itemtype"));
			this.getFormHM().put("fieldsetid",vo.getString("fieldsetid"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
