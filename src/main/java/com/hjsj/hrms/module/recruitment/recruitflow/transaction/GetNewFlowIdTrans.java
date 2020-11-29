package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

public class GetNewFlowIdTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String id = idg.getId("zp_flow_definition.flow_id");// 参数从系统管理-应用管理-参数设置-序号维护中获取

			if (StringUtils.isEmpty(id)) {
				new Exception(ResourceFactory.getProperty("error.generate.flowid.isnull"));
			} else {
				this.getFormHM().put("xjflowid", PubFunc.encrypt(id));
				this.getFormHM().remove("flowName");
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
