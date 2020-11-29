package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:QuerySparePositionTrans.java</p>
 * <p>Description:列出已审批，但未引用的需求以及已引入本计划中的需求</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 27, 2006 10:33:55 AM</p>
 * @author dengcan
 * @version 1.0
 * 
 */
public class QuerySparePositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String operate=(String)hm.get("operate");
			/**安全问题修改,前面已经将z0101加密了,所以这里解密回来**/
			String z0101=PubFunc.decrypt((String)hm.get("z0101"));
			ArrayList list0=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			ArrayList list= getFildList(list0);
			if(operate!=null)
			{
				ArrayList tempList=getList(list);
				list.clear();
				list=tempList;
			}
			ArrayList tableHeadNameList=getTableHeadNameList(list);
			tableHeadNameList.add(0,"查阅");
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			if(vo==null)
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			String dbname=vo.getString("str_value");
			
			PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
			ArrayList dataList=positionDemand.getDataList2(list,z0101,operate,dbname,this.getUserView());
			hm.remove("operate");
			hm.remove("z0101");
			this.getFormHM().put("dateList",dataList);
			this.getFormHM().put("fieldList",list);
			this.getFormHM().put("tableHeadNameList",tableHeadNameList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	
	
	public ArrayList getList(ArrayList list)
	{
		ArrayList aList=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			FieldItem item=(FieldItem)list.get(i);
			aList.add(item);
			if("z0315".equalsIgnoreCase(item.getItemid()))
			{
				FieldItem a_item=new FieldItem();
				a_item.setItemid("actualNum");
				a_item.setItemdesc(ResourceFactory.getProperty("hire.personnelEmploy.actualEmployNum"));
				a_item.setCodesetid("0");
				a_item.setItemtype("N");
				aList.add(a_item);
			}
		}
		return aList;
	}
	
	
	
	public ArrayList getFildList(ArrayList list)
	{
		
		ArrayList fieldList=new ArrayList();
		
		for(int i=0;i<list.size();i++)
		{
			FieldItem item=(FieldItem)list.get(i);				
			if("M".equals(item.getItemtype())|| "z0303".equalsIgnoreCase(item.getItemid())|| "z0305".equalsIgnoreCase(item.getItemid()))
				continue;
			fieldList.add(item);
			
		}
		return fieldList;
		
	}
	
	/**
	 * 取得表头名称列表
	 * @param fieldList
	 * @return
	 */
	public ArrayList getTableHeadNameList(ArrayList fieldList)
	{
		ArrayList tableHeadNameList=new ArrayList();
	
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);				
			tableHeadNameList.add(item.getItemdesc());			
		}
		return tableHeadNameList;
	}
	
}
