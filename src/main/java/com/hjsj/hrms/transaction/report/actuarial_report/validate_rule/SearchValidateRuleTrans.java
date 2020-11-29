package com.hjsj.hrms.transaction.report.actuarial_report.validate_rule;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.ValidateruleBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchValidateRuleTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			//HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			
			StringBuffer parmcopyList=new StringBuffer();
			String    tableHtml="";
			
			ValidateruleBo validateruleBo=new ValidateruleBo(this.getFrameconn());
			String theadHtml=validateruleBo.getTheadHtml(validateruleBo.getSelfSortList());
			String tabBodyHtml=validateruleBo.getTabBody(validateruleBo.getUnderUnitList(this.getFrameconn()),parmcopyList,validateruleBo.getSelfSortList());
			tableHtml=theadHtml+tabBodyHtml;
			
			String paramcopy="";
			if(parmcopyList.toString().indexOf(",")!=-1){
				paramcopy=parmcopyList.toString();
				paramcopy =paramcopy.substring(0, paramcopy.length()-1);
			}
			this.getFormHM().put("tableHtml",tableHtml);
			this.getFormHM().put("paramcopy",paramcopy);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	

}
