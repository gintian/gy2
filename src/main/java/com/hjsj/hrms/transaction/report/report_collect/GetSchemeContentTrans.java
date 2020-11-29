package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetSchemeContentTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String unitcode = (String) this.getFormHM().get("unitcode");
			String secid = (String) this.getFormHM().get("secid");
			String tabid = (String) this.getFormHM().get("tabid");
			String flag = (String) this.getFormHM().get("type");
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			IntegrateTableBo bo = new IntegrateTableBo(this.getFrameconn());
			String content=bo.getSchemeContent(unitcode,tabid,secid,flag);
			content = PubFunc.keyWord_reback(content);   //add by wangchaoqun on 2014-9-30
			this.getFormHM().put("content",content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
