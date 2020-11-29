package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchMarkStatusListTrans.java</p>
 * <p>Description:展示打分状态交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 09:41:14</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchMarkStatusListTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		String checkPlanId=(String)this.getFormHM().get("checkPlanId");    //考核计划
		MarkStatusBo markStatusBo=new MarkStatusBo(this.getFrameconn(),this.userView);
		boolean isPerformanceManager=this.getUserView().haveTheRoleProperty("4"); //是否是绩效主管
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String checkall=(String)this.getFormHM().get("checkall"); 
		if(hm.get("opt")!=null)
		{
			if("all".equalsIgnoreCase((String)hm.get("opt")))
				checkall="1";
			else
				checkall="0";
			hm.remove("opt");
		}
		
		String selectFashion=(String)this.getFormHM().get("selectFashion");  // 查询方式 1:按考核主体  2:考核对象
		String department=(String)this.getFormHM().get("department");         //部门
		String name=(String)this.getFormHM().get("name");
		
		String busitype = "";
		if(hm.get("busitype")!=null)
		{
			busitype=(String)hm.get("busitype");
		//	hm.remove("busitype");
		}
		if("-1".equalsIgnoreCase(busitype))
			busitype = "";
		
		if(hm.get("checkPlanId")!=null)
		{
			checkPlanId=(String)hm.get("checkPlanId");
			hm.remove("checkPlanId");
		}
		String consoleType="2";
		if(hm.get("consoleType")!=null)
		{
			consoleType=(String)hm.get("consoleType");
			hm.remove("consoleType");
			checkall="0";
		}
		else
		{
			if(this.getFormHM().get("consoleType")!=null)
			{
				consoleType=(String)this.getFormHM().get("consoleType");
			}
		}
		String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测
		this.getFormHM().put("model",model);
		ArrayList checkPlanList=markStatusBo.getCheckPlanList(this.getUserView(),isPerformanceManager,model,consoleType,busitype);		   //考核计划列表
		//默认选第一条记录
		if((checkPlanId==null|| "".equals(checkPlanId))&&checkPlanList.size()>0)
			checkPlanId=markStatusBo.getFirstRecordID(checkPlanList);
		name=PubFunc.getStr(name);
		
		String isflag="0";
		if("2".equals(selectFashion))
			isflag=markStatusBo.getPlanFlag(PubFunc.decrypt(checkPlanId));  //0:无目标,无报告 1:有报告无目标 2有目标 有报告
		
		 Hashtable htxml=new Hashtable();
		 LoadXml loadxml=new LoadXml(this.frameconn,PubFunc.decrypt(checkPlanId));
		 htxml=loadxml.getDegreeWhole();
		
		if("2".equals(isflag) && ("True".equalsIgnoreCase((String)htxml.get("relatingTargetCard")) || "2".equalsIgnoreCase((String)htxml.get("relatingTargetCard"))) && "2".equals(selectFashion))
			isflag="1";
		
		ArrayList markStatusList=markStatusBo.getMarkStatusList(PubFunc.decrypt(checkPlanId),this.getUserView(),isPerformanceManager,selectFashion,department,name,isflag);
		this.getFormHM().put("object_type",String.valueOf(markStatusBo.getObjectType(PubFunc.decrypt(checkPlanId))));
		this.getFormHM().put("markStatusList",markStatusList);
		this.getFormHM().put("checkPlanList",checkPlanList);
		this.getFormHM().put("checkPlanId",checkPlanId);
		this.getFormHM().put("fashionList", getFashionList());
		this.getFormHM().put("departmentList",markStatusBo.getDepartMentList(PubFunc.decrypt(checkPlanId),selectFashion));
		this.getFormHM().put("isFlag",isflag);
        this.getFormHM().put("selectFashion", selectFashion);
        this.getFormHM().put("consoleType", consoleType);
        
        this.getFormHM().put("checkall",checkall);
	}

	public ArrayList getFashionList()
	{
		//1:按考核主体  2:考核对象
		ArrayList list=new ArrayList();
		CommonData data=new CommonData("1",ResourceFactory.getProperty("lable.performance.perMainBody"));
		CommonData data1=new CommonData("2",ResourceFactory.getProperty("lable.performance.perObject"));
		list.add(data);
		list.add(data1);
		return list;
	}
}
