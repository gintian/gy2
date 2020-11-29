package com.hjsj.hrms.transaction.report.report_collect;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DelSchemeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String unitcode = (String) this.getFormHM().get("unitcode");
			int secid = Integer
					.parseInt((String) this.getFormHM().get("secid"));
			String tabid = (String) this.getFormHM().get("tabid");
			String flag = (String) this.getFormHM().get("type");
			IntegrateTableBo bo = new IntegrateTableBo(this.getFrameconn());
			bo.delScheme(unitcode,tabid,secid,flag);
			
			this.getFormHM().put("secid",String.valueOf(secid));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
