package com.hjsj.hrms.transaction.report.actuarial_report.validate_rule;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchParamsetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			//HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			
//			StringBuffer parmcopyList=new StringBuffer();
//			String    tableHtml="";
			
			TargetsortBo targetsortBo=new TargetsortBo(this.getFrameconn());
			
//			String theadHtml=validateruleBo.getTheadHtml(validateruleBo.getSelfSortList());
			String tableHtml2=targetsortBo.getTableHtml();
//			tableHtml=theadHtml+tabBodyHtml;
			String paramcopy2="differ,differPercent,";
			ArrayList list = targetsortBo.getInitParam();
			for(int i=0;i<list.size();i++){
				paramcopy2+="medic_differ_"+list.get(i)+",";
				paramcopy2+="medic_differPercent_"+list.get(i)+",";
	    		if(!"4".equals(list.get(i))){
	    			paramcopy2+="other_differ_"+list.get(i)+",";
					paramcopy2+="other_differPercent_"+list.get(i)+",";
	    		}
	    		}
			paramcopy2 = paramcopy2.substring(0,paramcopy2.length()-1);
			this.getFormHM().put("tableHtml2",tableHtml2);
			this.getFormHM().put("paramcopy2",paramcopy2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	

}
