package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:</p>
 * <p>Description:初始化综合表条件页面</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 24, 2006:5:16:14 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class selectIntegrateTableTermTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String  unitcode=(String)hm.get("unitcode");
		String  tabid=(String)hm.get("tabid");
		String  nums=(String)hm.get("nums");
		String  cols=(String)hm.get("cols");
		IntegrateTableBo bo=new IntegrateTableBo(this.getFrameconn());
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		ArrayList provisionTermList=bo.getProvisionTermList(tabid,unitcode);
		ArrayList schemeList=bo.getSchemeList(unitcode,tabid,"0");
		ArrayList defaultItemList=bo.getLeftFields("##"+unitcode,"2");
		
		this.getFormHM().put("provisionTermList",provisionTermList);
		this.getFormHM().put("schemeList",schemeList);
		this.getFormHM().put("defaultItemList",defaultItemList);
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("unitcode",unitcode);
		this.getFormHM().put("nums",nums);
		this.getFormHM().put("cols",cols);

	}

}
