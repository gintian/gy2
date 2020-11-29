package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title:</p>
 * <p>Description:根据复杂条件取得基层单位编码信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 16, 2006:4:05:40 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class GetComplexConditionUnitTrans extends IBusiness {

	public void execute() throws GeneralException {
		 ArrayList relationList=(ArrayList)this.getFormHM().get("relation");
		 ArrayList paramenameList=(ArrayList)this.getFormHM().get("paramename");
		 ArrayList operateList=(ArrayList)this.getFormHM().get("operate");
		 ArrayList codeValue=(ArrayList)this.getFormHM().get("codeValue");
		 String    unitcode=(String)this.getFormHM().get("unitcode");
		
		 ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		 ArrayList list=reportCollectBo.getComplexConditionUnit(unitcode,relationList,paramenameList,operateList,codeValue);
		 this.getFormHM().put("unitList",list);
		 
	}

}
