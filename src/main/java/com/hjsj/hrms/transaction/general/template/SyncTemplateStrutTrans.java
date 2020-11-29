/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 24, 2008:2:32:44 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SyncTemplateStrutTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			String ins_id=(String)this.getFormHM().get("ins_id");
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			/**创建或修临时表*/
			/**发起流程时才需要创建临时表,审批环节不用创建临时表*/
			if("0".equalsIgnoreCase(ins_id))
				tablebo.createTempTemplateTable(this.userView.getUserName());	//tablebo.createTempTemplateTable(this.userView.getUserName());			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}

	}

}
