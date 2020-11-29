/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:</p> 
 *<p>Description:设置计算公有效状态</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-29:下午02:24:55</p> 
 *@author cmq
 *@version 4.0
 */
public class SetFormulaValidTrans extends IBusiness {

	public void execute() throws GeneralException {
		String batch=(String)this.getFormHM().get("batch");
		String salaryid=(String)this.getFormHM().get("salaryid");
		String itemid=(String)this.getFormHM().get("itemid");
		String flag=(String)this.getFormHM().get("flag");
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("update salaryformula set useflag='");
			buf.append(flag);
			buf.append("' where salaryid=");
			buf.append(salaryid);
			/**单个设置计算公式有效*/
			if("0".equalsIgnoreCase(batch))
			{
				buf.append(" and itemid='");
				buf.append(itemid);
				buf.append("'");
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
