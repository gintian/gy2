package com.hjsj.hrms.transaction.performance.showkhresult;

import com.hjsj.hrms.businessobject.performance.showkhresult.DirectionAnalyseBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowDirectionAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String operate=(String)hm.get("operate");  
			hm.remove("operate");
			DirectionAnalyseBo directionAnalyseBo=new DirectionAnalyseBo(this.getFrameconn());
			ChartParameter chartParameter=new ChartParameter();	
			chartParameter.setStrokeWidth(2f);	
			if("0".equals(operate))
			{
				String  objectid=(String)this.getFormHM().get("objectid");
				String template_id=(String)this.getFormHM().get("template_id");					
				ArrayList itemLevelList=directionAnalyseBo.getItemLevelList(template_id);			
			    String itemLevelID="1";
			    String isTotalScore="0";
			    HashMap dataMap=directionAnalyseBo.getDataMap2(template_id,itemLevelID,isTotalScore,objectid,itemLevelList);	
				this.getFormHM().put("template_id",template_id);
				this.getFormHM().put("objectid",objectid);
				this.getFormHM().put("itemLevelList",itemLevelList);
				this.getFormHM().put("itemLevelID",itemLevelID);
				this.getFormHM().put("isTotalScore",isTotalScore);
				this.getFormHM().put("dataMap",dataMap);
				this.getFormHM().put("chartParameter",chartParameter);
				
			}
			else
			{
				String  objectid=(String)this.getFormHM().get("objectid");
				String template_id=(String)this.getFormHM().get("template_id");	
			    String itemLevelID=(String)this.getFormHM().get("itemLevelID");
			    String isTotalScore=(String)this.getFormHM().get("isTotalScore");
			    ArrayList itemLevelList=(ArrayList)this.getFormHM().get("itemLevelList");
			    HashMap dataMap=directionAnalyseBo.getDataMap2(template_id,itemLevelID,isTotalScore,objectid,itemLevelList);
				
			    this.getFormHM().put("template_id",template_id);
				this.getFormHM().put("objectid",objectid);
				this.getFormHM().put("itemLevelID",itemLevelID);
				this.getFormHM().put("isTotalScore",isTotalScore);
				this.getFormHM().put("dataMap",dataMap);
				this.getFormHM().put("chartParameter",chartParameter);
			}
				
				
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
