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
public class SetValidFieldTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			String typeflag = (String)rMap.get("typeflag")==null?"":(String)rMap.get("typeflag");
			String plan_fields = "";
			String summarize_fields = "";
			if(!"".equals(typeflag)){
				 plan_fields =  (String)rMap.get("plan_fields"+typeflag);	// 工作计划定义指标	
				 summarize_fields = (String)rMap.get("summarize_fields"+typeflag); // 工作总结定义指标
			}else{
				plan_fields = (String)this.getFormHM().get("plan_fields");	// 工作计划定义指标	
				summarize_fields = (String)this.getFormHM().get("summarize_fields"); // 工作总结定义指标
			}
			if(plan_fields==null|| "".equals(plan_fields.trim())){
				plan_fields ="content";
			}
			if(summarize_fields==null|| "".equals(summarize_fields.trim())){
				summarize_fields ="content";
			}
			this.getFormHM().put("planFieldsList",getFieldsList(plan_fields));	
			this.getFormHM().put("summarizeFieldList",getFieldsList(summarize_fields));
			this.getFormHM().put("typeflag",typeflag);
			
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
	public ArrayList getFieldsList(String targetItem)
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
			//换成per_diary_content
			
			ArrayList fieldItemList = DataDictionary.getFieldList("PER_DIARY_CONTENT", Constant.USED_FIELD_SET);
			String noEditStr=",SEQ,P0100,RECORD_NUM,B0110,E0122,E01A1,NBASE,A0100,A0101,START_TIME,END_TIME,TYPE,LOG_TYPE,";
			
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


