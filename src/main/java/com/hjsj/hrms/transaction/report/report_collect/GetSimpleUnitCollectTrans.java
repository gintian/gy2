package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title:</p>
 * <p>Description:获得满足简单条件的基层单位信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 14, 2006:3:35:38 PM</p>
 * @author lu
 * @version 1.0
 *
 */
public class GetSimpleUnitCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		String paramename=(String)this.getFormHM().get("codeset");
		ArrayList paramValueList=(ArrayList)this.getFormHM().get("values");
		String unitcode=(String)this.getFormHM().get("unitcode");
		ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		ArrayList    list=reportCollectBo.getSimpleConditionUnit(paramename,paramValueList,unitcode);
		this.getFormHM().put("unitcodeList",list);
		
		

	}

}
