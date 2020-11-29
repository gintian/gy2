package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 生成登记表
 *<p>Title:CreateDatumTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 29, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class CreateYkcardTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		String unit_card=leadarParamXML.getTextValue(leadarParamXML.UNIT_CARD);
		if(unit_card==null||unit_card.length()<=0)
		{
              this.getFormHM().put("is_view_card","false");
		}else
		{
			this.getFormHM().put("is_view_card","true");
		}
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String card_id=(String)hm.get("card_id");
	    if(card_id==null)
	    {
	    	if(unit_card!=null&&unit_card.length()>0)
	    	{
	    		String[] unit_cards=unit_card.split(",");
				ArrayList cardidlist=leadarParamXML.getUnit_card(unit_cards);
				
					if(cardidlist!=null&&cardidlist.size()>0)
					{
						CommonData da=(CommonData)cardidlist.get(0);
						card_id=da.getDataValue();
					}
	    	}
	    }
		this.getFormHM().put("card_id",card_id);
		String code=(String)this.getFormHM().get("code");
		if(code==null||code.length()<=0)
			code=this.userView.getUserOrgId();
		this.getFormHM().put("code",code);		
	}
	
    
   
}
