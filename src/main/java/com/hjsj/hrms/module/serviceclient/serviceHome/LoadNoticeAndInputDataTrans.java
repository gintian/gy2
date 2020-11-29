package com.hjsj.hrms.module.serviceclient.serviceHome;

import com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject.ServiceSettingBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;
import java.util.Map;

public class LoadNoticeAndInputDataTrans extends IBusiness {
	@Override
	public void execute() throws GeneralException {
		try {
			String serviceId = (String) this.formHM.get("serviceId");// 服务号
			ServiceSettingBo bo = new ServiceSettingBo(this.frameconn, this.userView);
			List<Map<String,String>> serviceParamData = null;
			serviceParamData = bo.getServiceParams(serviceId);
			this.formHM.put("serviceParamData", serviceParamData);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
