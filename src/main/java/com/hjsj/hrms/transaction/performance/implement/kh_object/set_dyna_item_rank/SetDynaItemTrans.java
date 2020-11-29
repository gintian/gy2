package com.hjsj.hrms.transaction.performance.implement.kh_object.set_dyna_item_rank;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SetDynaItemTrans.java</p>
 * <p>Description:设置动态项目权重(分值)</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SetDynaItemTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String planid = (String) this.getFormHM().get("planid");		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String objTypeId = (String) hm.get("objTypeId");
		hm.remove("objTypeId");
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"",objTypeId,planid);		
			//没有记录就插入该考核对象类别的全部记录	
			String sql = "select count(*) from per_dyna_item t where plan_id=" + planid+" and body_id="+objTypeId;
			this.frowset = dao.search(sql);
			if (this.frowset.next())
			{
				if(this.frowset.getInt(1)==0)			
					bo.insertDynaItem("all");			
			}
			
			bo = new KhTemplateBo(this.getFrameconn(),"",objTypeId,planid);	
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
