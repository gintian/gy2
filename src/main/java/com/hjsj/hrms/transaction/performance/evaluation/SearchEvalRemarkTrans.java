package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchEvalRemarkTrans</p>
 * <p>Description:查找绩效备注</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-03-03</p>
 * @author JinChunhai
 * @version 4.2
 */

public class SearchEvalRemarkTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planid = (String) hm.get("planid");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
		boolean _flag = _bo.isHavePriv(this.userView, planid);
		if(!_flag){
			return;
		}
		hm.remove("planid");		
		String objectid = PubFunc.decrypt((String) hm.get("objectid"));
		hm.remove("objectid");		
		
		String evalRemark = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String strSql = "select evalRemark from PER_RESULT_" + planid +" where object_id = '"+objectid+"'";
			this.frowset = dao.search(strSql);
			if (this.frowset.next())
				evalRemark = this.frowset.getString("evalRemark")==null?"": this.frowset.getString("evalRemark");	
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally
		{
			this.getFormHM().put("evalRemark", evalRemark);
		}

	}
}
