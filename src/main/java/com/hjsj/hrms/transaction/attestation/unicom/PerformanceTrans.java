package com.hjsj.hrms.transaction.attestation.unicom;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PerformanceTrans extends IBusiness {

	public void execute() throws GeneralException {
		BatchGradeBo bo=new BatchGradeBo(this.getFrameconn());
		String performanceType=SystemConfig.getPropertyValue("performanceType");
		 HashMap map=null;
		 //#4.0登录后首页，是否显示绩效面板
		 if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("performancePanel")))
		 {
			 performanceType="employ";
			 map=bo.getCanOperatorPlan(this.getUserView());
		     HashMap mm=bo.get_LT_plansMap(this.userView);
		     map.putAll(mm);
		 }
		 else
		 {
	    	if(performanceType==null|| "".equals(performanceType)|| "leader".equalsIgnoreCase(performanceType))
	    	{
	    		performanceType="leader";
	    	    map=bo.get_LT_plansMap(this.userView);
	    	}
	    	else if("employ".equalsIgnoreCase(performanceType))
	    	{
	    		map=bo.getCanOperatorPlan(this.getUserView());
		    	HashMap mm=bo.get_LT_plansMap(this.userView);
		    	map.putAll(mm);
	    	}
		 }
		 String status="0";
		 if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
		 {
	       CommendTableBo ctb = new CommendTableBo(this.getFrameconn(),this.getUserView());
	       ctb.createNewLeaderResultTable();
	       if(ctb.isHaveLeader())
			   status="1";
		 }
		 this.execuFillInfo();
	   this.getFormHM().put("newLeaderStatus", status);
	   this.getFormHM().put("plansMap", map);
	   this.getFormHM().put("performanceType", performanceType);
	}
	public void execuFillInfo()
	{
		try
		{
    		String isFillInfo="1";//默认填写过
    		String items=SystemConfig.getPropertyValue("bjga_items");
    		if(items.length()>0)
	    	{
	    		StringBuffer where = new StringBuffer("");
	     		String[] arr=items.split(",");
	    		for(int i=0;i<arr.length;i++)
	    		{
		    		if(arr[i]==null|| "".equals(arr[i]))
			    		continue;
			    	FieldItem fieldItem = DataDictionary.getFieldItem(arr[i].toLowerCase());
			    	if(fieldItem==null||!"A01".equalsIgnoreCase(fieldItem.getFieldsetid())|| "M".equals(fieldItem.getItemtype()))//只支持主集
			    		continue;
			    	where.append(" or ("+arr[i]+" is null or "+arr[i]+"='')");
	    		}
	    		if(where.length()>0)
	    		{
		    		String sql = " select * from USRA01 where ("+where.toString().substring(3)+") and a0100='"+this.userView.getA0100()+"'";
		    		ContentDAO dao = new ContentDAO(this.getFrameconn());
		    		this.frowset=dao.search(sql);
				    while(this.frowset.next())
				    {
				    	isFillInfo="0";
				    }
				
	    		}
    		}
    		this.getFormHM().put("isFillInfo", isFillInfo);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

}
