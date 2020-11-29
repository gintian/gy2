package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:ImportTemplateTrans.java</p>
 * <p>Description>:绩效考核，导出模板接口</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-7-23 上午11:38:09</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ImportTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			String filename=bo.exportDate(subsys_id, this.userView);
			filename=SafeCode.encode(PubFunc.encrypt(filename));
			this.getFormHM().put("outName",filename);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
