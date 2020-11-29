package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchTargetCardSetTrans.java
 * <p>Description:考核实施/目标卡制定
 * <p>Company:hjsj
 * <p>create time:2010-11-01 10:25:56
 * <p>@author JinChunhai
 * <p>@version 5.0
 */

public class SearchTargetCardSetTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		String planid = (String) this.getFormHM().get("planid");		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String objCode = (String) hm.get("codeid");
		hm.remove("codeid");
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"1",objCode,planid,"targetCard");	
			String object_id = "";
			//没有记录就插入该考核对象类别的全部记录	
			String sql = "select count(*) from p04  where plan_id=" + planid;	
			String tempCode = objCode.substring(0, 1);
			if("p".equalsIgnoreCase(tempCode))
			{
				sql+=" and a0100='"+objCode.substring(1)+"'";
				object_id=objCode.substring(1);
			}				
			else if("u".equalsIgnoreCase(tempCode))
			{
				sql+=" and b0110='"+objCode.substring(2)+"'";
				object_id=objCode.substring(2);
			}
			this.frowset = dao.search(sql);
			if (this.frowset.next())
			{
				if(this.frowset.getInt(1)==0)			
					bo.insertObjTarget_commonPoint(object_id);			
			}
			
			bo = new KhTemplateBo(this.getFrameconn(),"1",objCode,planid,"targetCard");	
			String html = bo.getTargetCardHtml();
			this.getFormHM().put("targetCardHtml",html);
			this.getFormHM().put("objCode",objCode);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
