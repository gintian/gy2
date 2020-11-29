package com.hjsj.hrms.transaction.report.actuarial_report.validate_rule;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchTargetsortTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		//ArrayList list=DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);

		TargetsortBo to = new TargetsortBo(this.getFrameconn());
		ArrayList list =to.getTargetsortContent();
		 this.getFormHM().put("tagetsortlist", list);
	 	
	}
	
	

}
