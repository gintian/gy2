package com.hjsj.hrms.module.system.portal.jobtitle.transaction;

import com.hjsj.hrms.module.system.portal.jobtitle.businessobject.JobtitlePortalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取首页职称评审图标
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetJobtitlePortal extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			JobtitlePortalBo jobtitlePortalBo = new JobtitlePortalBo(this.getFrameconn(), this.userView);// 工具类
			ArrayList<HashMap<String, String>> infoList = jobtitlePortalBo.getInfoList();
			
			this.getFormHM().put("infolist", infoList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
