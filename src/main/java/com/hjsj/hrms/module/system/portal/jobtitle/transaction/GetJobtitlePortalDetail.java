package com.hjsj.hrms.module.system.portal.jobtitle.transaction;

import com.hjsj.hrms.module.system.portal.jobtitle.businessobject.JobtitlePortalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 首页-职称评审
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetJobtitlePortalDetail extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			String id = (String)this.getFormHM().get("id");//选中图标id
			
			JobtitlePortalBo jobtitlePortalBo = new JobtitlePortalBo(this.getFrameconn(), this.userView);// 工具类
			 ArrayList<HashMap<String, String>> infoList = jobtitlePortalBo.getDetailInfoList(id);
			
			this.getFormHM().put("infolist", infoList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
