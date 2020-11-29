package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveSchemeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String unitcode = (String) this.getFormHM().get("unitcode");
			int secid = Integer
					.parseInt((String) this.getFormHM().get("secid"));

			String content = (String) this.getFormHM().get("content");
			content = PubFunc.keyWord_reback(content);
			content=content.replaceAll("'","''");
			String tabid = (String) this.getFormHM().get("tabid");
			String flag = (String) this.getFormHM().get("type");
			IntegrateTableBo bo = new IntegrateTableBo(this.getFrameconn());
			bo.saveScheme(unitcode, tabid, secid, content,flag);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
