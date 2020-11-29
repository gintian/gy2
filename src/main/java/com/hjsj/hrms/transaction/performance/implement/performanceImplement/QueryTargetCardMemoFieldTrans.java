package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:QueryTargetCardMemoFieldTrans.java</p>
 * <p>Description:考核实施/目标卡制定 查询备注型大字段的值</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-01-12 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class QueryTargetCardMemoFieldTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String p0401_value = (String) hm.get("p0401_value");
		hm.remove("p0401_value");
		String targetPointCol = (String) hm.get("targetPointCol");
		hm.remove("targetPointCol");
		
		String planid = (String) this.getFormHM().get("planid");	
		String objCode = (String) this.getFormHM().get("objCode");	
		
		try 
		{
			String sql = "select "+targetPointCol+" from  p04 where plan_id="+planid+" and upper(p0401)='"+p0401_value.toUpperCase()+"' ";
			String tempCode = objCode.substring(0, 1);
			if("p".equalsIgnoreCase(tempCode))
				sql+=" and a0100='"+objCode.substring(1)+"'";
			else if("u".equalsIgnoreCase(tempCode))
				sql+=" and b0110='"+objCode.substring(2)+"'";
			String targetMemoField = "";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				if(this.frowset.getString(1)!=null)
					targetMemoField=this.frowset.getString(1);
			}				
			
			this.getFormHM().put("targetMemoField", targetMemoField);
			this.getFormHM().put("targetPointCol", targetPointCol);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}			

	}

}
