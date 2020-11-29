package com.hjsj.hrms.transaction.hire.employSummarise.hireSummarise;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hjsj.hrms.businessobject.hire.EmploySummarise;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
public class InitHireSummariseTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String operate=(String)hm.get("operate");
		String planID=(String)hm.get("planID");
		if("null".equalsIgnoreCase(planID))
		    planID = "";
		String viewType=(String)this.getFormHM().get("viewType");  //1:用工需求  2：招聘计划
		ArrayList columnsList=new ArrayList();
		ArrayList list=new ArrayList();
		String returnflag="";
		if(hm.get("returnflag")!=null)
			returnflag=(String)hm.get("returnflag");
		else
			returnflag=(String)this.getFormHM().get("returnflag");
		this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
		if(planID!=null&&planID.trim().length()>0)
		{
			viewType="1";
		}
		
		if("2".equals(viewType))
		{
			list=DataDictionary.getFieldList("Z01",Constant.USED_FIELD_SET);
			
		}
		else if("1".equals(viewType))
		{
			ArrayList list0=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			list= getFildList(list0);
		}
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
		HashMap map=xmlBo.getAttributeValues();
		String schoolPosition="";
		if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0)
			schoolPosition=(String)map.get("schoolPosition");
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean abean=new LazyDynaBean();
			FieldItem item=(FieldItem)list.get(i);
			String itemid=item.getItemid();
			String itemdesc=item.getItemdesc();
			
			if("z0311".equalsIgnoreCase(itemid))
			{
				if(schoolPosition!=null&&schoolPosition.length()>0)
				{
					itemdesc=ResourceFactory.getProperty("e01a1.major.label");
				}
				else
				{
					itemdesc=ResourceFactory.getProperty("e01a1.label");
				}
				//itemdesc+="|"+ResourceFactory.getProperty("hire.employActualize.interviewProfessional");
			}
			if("0".equals(item.getState())||("z0101".equalsIgnoreCase(itemid)&& "2".equals(viewType)))
			{
				continue;
			}
			abean.set("itemtype",item.getItemtype());
			abean.set("itemid",itemid);
			abean.set("itemdesc",itemdesc);
			columnsList.add(abean);
		}
			
		String extendWhereSql=PubFunc.decrypt((String)this.getFormHM().get("extendWhereSql"));
		String orderSql=PubFunc.decrypt((String)this.getFormHM().get("orderSql"));
		if(operate!=null&& "init".equals(operate))
		{
			extendWhereSql="";
			if("1".equals(viewType))
				orderSql=" order by z0319";
			else 
				orderSql="";
			hm.remove("operate");
			hm.remove("planID");
		}

		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();  //应用库前缀	
		EmploySummarise employSummarise=new EmploySummarise(this.getFrameconn());
	
		
		ArrayList planList=employSummarise.getEngagePlanList(list,dbname,extendWhereSql,orderSql,this.getUserView(),viewType,planID);

		this.getFormHM().put("username",this.getUserView().getUserName());
		this.getFormHM().put("dbName",dbname);
		this.getFormHM().put("extendWhereSql",PubFunc.encrypt(extendWhereSql));
		this.getFormHM().put("orderSql",PubFunc.encrypt(orderSql));
		this.getFormHM().put("columnsList",columnsList);
		this.getFormHM().put("planList",planList);
		this.getFormHM().put("viewType",viewType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	
	
	public ArrayList getFildList(ArrayList list)
	{
		
		ArrayList fieldList=new ArrayList();
		
		for(int i=0;i<list.size();i++)
		{
			FieldItem item=(FieldItem)list.get(i);				
			if("M".equals(item.getItemtype())|| "z0303".equalsIgnoreCase(item.getItemid())|| "z0305".equalsIgnoreCase(item.getItemid()))
			{
				continue;
			}
			fieldList.add(item);
			if("z0311".equalsIgnoreCase(item.getItemid()))
			{
				FieldItem a_item=new FieldItem();
				a_item.setItemid("employedcount");
				a_item.setItemdesc("实招人数");
				a_item.setItemtype("N");
				a_item.setState("1");
				fieldList.add(a_item);
			}
			
			
			
		}
		return fieldList;
		
	}
}
