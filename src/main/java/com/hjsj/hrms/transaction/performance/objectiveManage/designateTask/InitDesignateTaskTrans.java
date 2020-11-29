package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:InitDesignateTaskTrans.java</p>
 * <p>Description>:InitDesignateTaskTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 27, 2011  4:02:55 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */

public class InitDesignateTaskTrans extends IBusiness
{
	
	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id=(String)this.getFormHM().get("plan_id");
			String objectid=(String)this.getFormHM().get("objectid");
			String returnURL=(String)this.getFormHM().get("returnURL");
			DesignateTaskBo dtb = new DesignateTaskBo(this.getFrameconn(),this.userView,plan_id,objectid); 
			
			HashMap leafItemLinkMap = dtb.getLeafItemLinkMap();  // 叶子项目对应的继承关系
			HashMap itemPointNum = dtb.getItemPointNum(); // 取得项目拥有的节点数
			int lay = dtb.getLay(); // 项目层级
			
			//dtb.createTempTable();
			ArrayList kpiList = dtb.getKPIList(plan_id, objectid);
			FieldItem fielditem2=DataDictionary.getFieldItem("p0407");
			String itemdesc="";
			LoadXml loadxml=null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
				loadxml=new LoadXml(this.getFrameconn(),plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			}
			else
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);		
			Hashtable planParam=loadxml.getDegreeWhole(); 
			if(planParam.get("TaskNameDesc")!=null&&!"".equals((String)planParam.get("TaskNameDesc")))
			{
				itemdesc=(String)planParam.get("TaskNameDesc");
			}else
			{
		    	if(fielditem2==null|| "任务内容".equalsIgnoreCase(fielditem2.getItemdesc().trim()))
		    	{
		    		if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) //中国联通
			    		itemdesc="工作目标";
			    	else
			    		itemdesc=ResourceFactory.getProperty("jx.khplan.point");
	     		}
	    		else
	    			itemdesc=fielditem2.getItemdesc();
			}
			this.getFormHM().put("itemdesc", itemdesc);
			this.getFormHM().put("kpiList", kpiList);
			this.getFormHM().put("plan_id", plan_id);
			this.getFormHM().put("objectid",objectid);
			this.getFormHM().put("returnURL", returnURL);			
			this.getFormHM().put("leafItemLinkMap", leafItemLinkMap);
			this.getFormHM().put("itemPointNum", itemPointNum);
			this.getFormHM().put("lay", String.valueOf(lay));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
