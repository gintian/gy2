package com.hjsj.hrms.transaction.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveSummaryTrans extends IBusiness {

	public void execute() throws GeneralException {
		String planid=(String)this.getFormHM().get("planNum");
		if(planid==null|| "".equals(planid))
			return;
		/**考评结果表*/
		String tableName = "per_result_" + planid;
		RecordVo vo=new RecordVo(tableName);
		StringBuffer strsql=new StringBuffer();
		String summary=(String)this.getFormHM().get("summary");
		if(summary==null|| "".equals(summary))
			return;
		if(vo.hasAttribute("summarize"));
		{
			strsql.append("update ");
			strsql.append(tableName);
			strsql.append(" set summarize='");
			strsql.append(summary);
			strsql.append("' where object_id='");
			strsql.append(userView.getA0100());
			strsql.append("'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				dao.update(strsql.toString());
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			    throw GeneralExceptionHandler.Handle(ee);					
			}
		}
	}

}
