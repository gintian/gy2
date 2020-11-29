package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 14, 2006:2:35:19 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class SearchCodeItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		String codeSetid=(String)this.getFormHM().get("codeSetid");
		ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		ArrayList fieldList=reportCollectBo.getCodeItemList(codeSetid,0);
		this.getFormHM().put("fieldList",fieldList);
		

	}

}
