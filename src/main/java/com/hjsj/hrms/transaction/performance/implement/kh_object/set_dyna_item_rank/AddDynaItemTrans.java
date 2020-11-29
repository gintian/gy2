package com.hjsj.hrms.transaction.performance.implement.kh_object.set_dyna_item_rank;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:AddDynaItemTrans.java</p>
 * <p>Description:增加动态项目权重(分值)的任务规则</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-17 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class AddDynaItemTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String planid = (String) this.getFormHM().get("planid");	
		String objTypeId = (String) this.getFormHM().get("objTypeId");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String item_id = (String) hm.get("item_id");
		hm.remove("item_id");
		try
		{		
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"",objTypeId,planid);;			
			bo.insertDynaItem(item_id);		
			
			bo = new KhTemplateBo(this.getFrameconn(),"",objTypeId,planid);;	
			String html = bo.getTemplateHtml();
			ArrayList itemList = bo.addItemList();
			this.getFormHM().put("dynaItemHtml",html);
			this.getFormHM().put("itemList",itemList);
			this.getFormHM().put("objTypeId",objTypeId);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
