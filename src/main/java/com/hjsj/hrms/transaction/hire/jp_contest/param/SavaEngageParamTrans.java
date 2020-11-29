package com.hjsj.hrms.transaction.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:SavaEngageParamTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SavaEngageParamTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String maxpos = (String)this.getFormHM().get("maxpos");
		String strTemplate = (String)this.getFormHM().get("strTemplate");
		EngageParamXML engageParamXML = new EngageParamXML(this.getFrameconn());
		engageParamXML.setTextValue(EngageParamXML.APP_COUNT,maxpos);
		engageParamXML.setTextValue(EngageParamXML.TEMPLATE,strTemplate);
		engageParamXML.saveParameter();
	}

}
