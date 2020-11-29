package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 
 * <p>Title:GetEngagePlanListTrans.java</p>
 * <p>Description:取得招聘计划列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 26, 2006 1:09:14 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class GetEngagePlanListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
     try
     {
		ArrayList fieldList=DataDictionary.getFieldList("z01",Constant.USED_FIELD_SET);
		StringBuffer columnName=new StringBuffer("z0101");
		StringBuffer str_sql=new StringBuffer("select z0101");
		StringBuffer str_whl=new StringBuffer(" from z01,codeitem where z01.z0129=codeitem.codeitemid and codeitem.codesetid='23' ");
		
		ArrayList planFieldList=new ArrayList();
		//planFieldList.add(getLazyDynaBean("z0101","0","A"));
		ArrayList tableHeadList=new ArrayList();
		tableHeadList.add("选择");
		
		
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);		
			String itemid=item.getItemid();
			String state=item.getState();//0隐藏  1显示
			if("z0101".equalsIgnoreCase(itemid))
				continue;
			if("1".equals(state)){
				if("z0129".equalsIgnoreCase(itemid))
				{
					str_sql.append(",codeitem.codeitemdesc az0129");
					str_sql.append(",z0129");
					columnName.append(",az0129");
					planFieldList.add(getLazyDynaBean("az0129","0","A"));
					tableHeadList.add("状态");
				}
				else
				{
					str_sql.append(","+itemid);
					columnName.append(","+itemid);
					planFieldList.add(getLazyDynaBean(itemid,item.getCodesetid(),item.getItemtype()));
					tableHeadList.add(item.getItemdesc());
				}
			}else{//为了 使审批状态指标设置为隐藏后 前台也能发布
				if("z0129".equalsIgnoreCase(itemid)){
					str_sql.append(",codeitem.codeitemdesc az0129");
					str_sql.append(",z0129");
					columnName.append(",az0129");
				}
			}
			
		}
		/*
		String str_sql="select z0101,codeitem.codeitemdesc  az0129,z0129,z0101,z0103,z0105,z0107,z0109,z0111,z0115,z0119";
		String str_whl=" from z01,codeitem where z01.z0129=codeitem.codeitemid and codeitem.codesetid='23' ";
		*/
		
		PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
		String unitcode="";
		if(!this.getUserView().isSuper_admin())
		{	
			ArrayList unitcodeList=positionDemand.getUnitIDList(this.getUserView());
			if(unitcodeList.size()==0)
				str_whl.append(" and 1=2 ");
			else
			{
				StringBuffer temp=new StringBuffer("");
				for(int i=0;i<unitcodeList.size();i++)
					temp.append(" or z01.z0105 like '"+(String)unitcodeList.get(i)+"%'");
				
				str_whl.append(" and ( "+temp.substring(3)+" )");
			}
		}
		this.getFormHM().put("order_by", " order by z0101 desc");
	    this.getFormHM().put("unitID",unitcode);
		this.getFormHM().put("str_sql",str_sql.toString());
		this.getFormHM().put("str_whl",str_whl.toString());
		this.getFormHM().put("planFieldList",planFieldList);
		this.getFormHM().put("tableHeadList",tableHeadList);
		this.getFormHM().put("columnName",columnName.toString());
     }catch(Exception e)
     {
    	 e.printStackTrace();
    	 throw GeneralExceptionHandler.Handle(e);
     }
	}

	
	private LazyDynaBean getLazyDynaBean(String id,String setType,String type)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("id",id);
		abean.set("setType",setType);
		abean.set("type",type);
		return abean;
	}
	
	
}
