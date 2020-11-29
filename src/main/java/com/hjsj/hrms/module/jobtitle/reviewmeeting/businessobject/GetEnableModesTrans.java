package com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 获得配置的第三方平台
 * @author Administrator
 *
 */
public class GetEnableModesTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ReviewMeetingBo bo =  new ReviewMeetingBo(this.getFrameconn(),this.getUserView());
		try {
			HashMap<String, Boolean> enableModes = bo.getEnableModes();
			this.getFormHM().put("enableModes", enableModes);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}

}
