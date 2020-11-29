/*
 * Created on 2006-2-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchBrowseInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String code=(String)hm.get("code");
		String droit=(String)hm.get("droit");//权限标记,自主平台的组织机构/信息浏览不用权限过滤 
		//检查是否越权访问    gdd 2014-09-24
		if(!"0".equals(droit)&&!code.equals(new CheckPrivSafeBo(this.frameconn,userView).checkOrg(code,"4")))
			throw GeneralExceptionHandler.Handle(new Exception("您没有此机构权限！"));
		
		String kind=(String)hm.get("kind");
		String orgtype=(String)hm.get("orgtype");
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";
		this.getFormHM().put("nid",code);
		if("0".equals(kind))
			kind="3";
	    else if("1".equals(kind))
	    	kind="2";
	    else if("2".equals(kind))
	    	kind="2";	
		
		this.getFormHM().put("infokind",kind);
		this.getFormHM().put("orgtype",orgtype);
	}

}
