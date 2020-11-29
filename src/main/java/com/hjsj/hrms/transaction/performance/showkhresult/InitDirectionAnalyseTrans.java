package com.hjsj.hrms.transaction.performance.showkhresult;

import com.hjsj.hrms.businessobject.performance.showkhresult.DirectionAnalyseBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitDirectionAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String  objectid=(String)hm.get("objectid");
		DirectionAnalyseBo directionAnalyseBo=new DirectionAnalyseBo(this.getFrameconn());
		ArrayList templateList=directionAnalyseBo.getTemplateList(PubFunc.decrypt(objectid));
		String template_id="";
	    template_id=directionAnalyseBo.getFirstValueOfList(templateList);
		ArrayList itemLevelList=directionAnalyseBo.getItemLevelList(template_id);			
	    String itemLevelID="1";
	    String isTotalScore="0";
	    HashMap dataMap=directionAnalyseBo.getDataMap2(template_id,itemLevelID,isTotalScore,PubFunc.decrypt(objectid),itemLevelList);
		
	    ChartParameter chartParameter=new ChartParameter();	
	    chartParameter.setStrokeWidth(2f);	
	    this.getFormHM().put("chartParameter",chartParameter);
		this.getFormHM().put("template_id",template_id);
		this.getFormHM().put("templateList",templateList);
		this.getFormHM().put("objectid",objectid);
		this.getFormHM().put("itemLevelList",itemLevelList);
		this.getFormHM().put("itemLevelID",itemLevelID);
		this.getFormHM().put("isTotalScore",isTotalScore);
		this.getFormHM().put("dataMap",dataMap);
	}

}
