package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:SearchRangTargetTrans.java</p>
 * <p>Description>:排名指标</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 11, 2011 10:10:46 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchRangTargetTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		String planid = (String) this.getFormHM().get("planid");
		LoadXml loadxml = new LoadXml(this.frameconn, planid);
		String grpMenu1Name = "";
		String grpMenu1Num = "";
		String grpMenu2Name = "";
		String grpMenu2Num = "";
		
		ArrayList formulalist1 = loadxml.getRelatePlanValue("CustomOrderGrp", "GrpMenu1");	
		
		if(formulalist1!=null && formulalist1.size()>0)
		{
			if(((String) formulalist1.get(0)).split(";").length==2)
			{
				grpMenu1Name = (((String) formulalist1.get(0)).split(";")[0]).toString();
				grpMenu1Num = (((String) formulalist1.get(0)).split(";")[1]).toString();
			}
			
		}
		ArrayList formulalist2 = loadxml.getRelatePlanValue("CustomOrderGrp", "GrpMenu2");	
		
		if(formulalist2!=null && formulalist2.size()>0)
		{
			if(((String) formulalist2.get(0)).split(";").length==2)
			{
				grpMenu2Name = (((String) formulalist2.get(0)).split(";")[0]).toString();
				grpMenu2Num = (((String) formulalist2.get(0)).split(";")[1]).toString();
			}
		}
		
		ArrayList list = new ArrayList();	
		String childrenTemp="";
		String subsetMenus = loadxml.getRelatePlanSubSetMenuValue();
		if(subsetMenus!=null&&subsetMenus.trim().length()>0)
		{
			String[] temps=subsetMenus.split(",");
			for(int j=0;j<temps.length;j++)
			{
				String temp=temps[j].trim();
				if(temp.length()==0)
					continue;
			    FieldItem fielditem = DataDictionary.getFieldItem(temp);
			    String itemType = fielditem.getItemtype();			    
			    String dainmaType=fielditem.getCodesetid();
			    String daima="0";
			    
			    int decimalwidth = fielditem.getDecimalwidth();
			    if("M".equalsIgnoreCase(itemType) && (!"0".equals(dainmaType) && dainmaType.length()>0))
			    {
			    	itemType="A";
			    	daima="1";
			    }
			    else if("N".equalsIgnoreCase(itemType) && decimalwidth==0)
			    	itemType="I";
			    
			    childrenTemp+=";"+fielditem.getItemid()+","+daima;
			    LazyDynaBean abean=new LazyDynaBean();			    
			    abean.set("Itemid", fielditem.getItemid());
			    abean.set("Itemdesc", fielditem.getItemdesc());
			    abean.set("daima", daima);
			    abean.set("Itemdesc_value", "["+fielditem.getItemid()+"]");			    
				list.add(abean);
			}
		}
		if(childrenTemp.length()<=0)		
			this.getFormHM().put("childrenTemp", childrenTemp);
		else
			this.getFormHM().put("childrenTemp", childrenTemp.substring(1));
		this.getFormHM().put("customizeGradeList", list);
		this.getFormHM().put("grpMenu1Name", grpMenu1Name);
		this.getFormHM().put("grpMenu1Num", grpMenu1Num);
		this.getFormHM().put("grpMenu2Name", grpMenu2Name);
		this.getFormHM().put("grpMenu2Num", grpMenu2Num);
	}
	public void setListItem(ArrayList list,String datavalue,String dataname)
	{
		CommonData data = new CommonData(dataname, datavalue);
		list.add(data);
	}
/*	private FieldItem  getFieldItem(String id,String desc,String type,String chr) 
	{
		FieldItem item = new FieldItem();
		item.setItemid(id);
		item.setItemdesc(desc);
		item.setItemtype(type);
		item.setCodesetid(chr);
		if(type.equalsIgnoreCase("N"))
		{
			item.setDecimalwidth(4);
			item.setItemlength(12);
		}
		else if(type.equalsIgnoreCase("I"))
		{
			item.setDecimalwidth(0);
			item.setItemlength(10);
		}
		else if(type.equalsIgnoreCase("A"))
		{
			item.setItemlength(50);
		}
		return item;
	}*/
}
