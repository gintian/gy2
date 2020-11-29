package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * DailyTargetSetTrans.java
 * Description: 国网日志指标设置
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Feb 27, 2013 3:03:43 PM Jianghe created
 */
public class DailyTargetSetTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
									
			String planTarget = (String)this.getFormHM().get("planTarget");	// 工作计划定义指标	
			String summTarget = (String)this.getFormHM().get("summTarget"); // 工作总结定义指标
																					
			this.getFormHM().put("plantargetList",getWorkTargetList(planTarget));	
			this.getFormHM().put("summtargetList",getWorkTargetList(summTarget));
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	/** 
     * 取得人员库列表
     * @return
     */
	public ArrayList getWorkTargetList(String targetItem)
	{
		ArrayList list = new ArrayList();
		try
		{			
			HashMap map = new HashMap();
			if(targetItem!=null && targetItem.trim().length()>0)
			{
				String[] items = targetItem.split(",");			
				for (int i = 0; i < items.length; i++)
				{
					map.put(items[i].toUpperCase(), "");				
				}
			}
						
			ArrayList fieldItemList = DataDictionary.getFieldList("p01", Constant.USED_FIELD_SET);
			String noEditStr=",TIME,P0100,B0110,E0122,E01A1,A0100,NBASE,A0101,P0101,P0103,P0104,P0106,P0107,P0108,P0115,P0114,P0117,P0116,P0113,";
			
			for(int i=0;i<fieldItemList.size();i++)
			{
				FieldItem fielditem = (FieldItem)fieldItemList.get(i);
				if(noEditStr.indexOf((","+fielditem.getItemid().toUpperCase()+","))!=-1)
					continue;
				if("0".equals(fielditem.getState()))
					continue;
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid",fielditem.getItemid().toUpperCase());
				bean.set("itemtype", fielditem.getItemtype());
				bean.set("codesetid",fielditem.getCodesetid());				
				bean.set("itemdesc",fielditem.getItemdesc());					
				bean.set("selected", map.get(fielditem.getItemid().toUpperCase()) == null ? "0" : "1");
				list.add(bean);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
}
