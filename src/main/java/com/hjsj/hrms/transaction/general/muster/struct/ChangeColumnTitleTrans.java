/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ChangeColumnTitleTrans</p>
 * <p>Description:修改列标题</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-21:9:50:06</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ChangeColumnTitleTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String setname=null;
		String title=null;
		String field_name=null;
		try
		{
			/**花名册的名称*/			
			setname=(String)this.getFormHM().get("setname");
			title=(String)this.getFormHM().get("title");
			field_name=(String)this.getFormHM().get("field_name");
			int idx=setname.indexOf("_");
			String tabid=setname.substring(1,idx);
			cat.debug("tabid="+tabid);
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			musterbo.reSetColumnTitle(tabid,field_name,title);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
