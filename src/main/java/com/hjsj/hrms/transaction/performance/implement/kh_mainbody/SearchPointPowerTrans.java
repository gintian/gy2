package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchPointPowerTrans.java</p>
 * <p>Description:考核主体指标权限划分</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-11-01 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 * 
 */

public class SearchPointPowerTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String objectIds = (String) hm.get("objIds");
		String plan_id = (String) hm.get("plan_id");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv(this.userView, plan_id)){	
        	return;
        } 
		String act = (String) hm.get("act");
		String power_type = (String) hm.get("power_type");//权限划分类别 point-指标权限划分 item-项目权限划分
		hm.remove("power_type");
		this.getFormHM().put("power_type", power_type);
		this.getFormHM().put("planid", plan_id);
		String object = "";
		String khObject = "";
		String khKey = "";
		PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(), this.getUserView());
		if (act != null && "init".equals(act))
		{//初次进入页面如果有多个考核对象，下拉就显示全部，如果只有一个考核对象就显示这个考核对象
		    // 获得考核对象
		    ArrayList objectList = bo.getKhObjectsList(objectIds,plan_id);
		    ArrayList objectsClassList = bo.getKhObjectsClassList(plan_id);
		    ArrayList kyeClassList = bo.getKhKyeClassList(plan_id);
		    //this.getFormHM().put("khObjectList", objectList);
		    this.getFormHM().put("khObjectClassList", objectsClassList);
		    this.getFormHM().put("khKeyClassList", kyeClassList);
		    
		    CommonData kh_object1 = (CommonData) objectsClassList.get(0);
		    CommonData objectkey = (CommonData) kyeClassList.get(0);
		    khObject=(String)kh_object1.getDataValue();
		    khKey=(String)objectkey.getDataValue();
		    this.getFormHM().put("khObject", khObject); 
		    this.getFormHM().put("khKey", khKey); 
		    	    
		}
		else if(act != null && "changeObj".equals(act))
		{
		    plan_id=(String)this.getFormHM().get("planid");	   
		    
		    khObject=(String)this.getFormHM().get("khObject");
		    khKey=(String)this.getFormHM().get("khKey");
		    if("all".equals(khKey)&&!"all".equals(khObject))
		    {
		    	object = "'"+khObject+"'";
		    }else if("all".equals(khObject)&&!"all".equals(khKey)){
		    	object = "'"+khKey+"'";
		    }
		    else if("all".equals(khObject)&& "all".equals(khKey)){
		    	object = "all";
		    }
		    else
		    	object = "'"+khKey+"','"+khObject+"'";
		    this.getFormHM().put("khObject", khObject); 
		    this.getFormHM().put("khKey", khKey); 
		}
		if("point".equalsIgnoreCase(power_type))//指标权限划分
		{
			RecordVo vo = bo.getPerPlanVo(plan_id);
			String template_id = vo.getString("template_id");
			ArrayList pointPowerHeadList = bo.getPointPowerHeadList(template_id);
			  
			ArrayList pointPowerList = bo.getPointPowerList2(object, plan_id, pointPowerHeadList,khKey,khObject);
			this.getFormHM().put("pointPowerHeadList", pointPowerHeadList);
			this.getFormHM().put("pointPowerList", pointPowerList);
		}else if("item".equalsIgnoreCase(power_type))//项目权限划分
		{
			HashMap map=bo.getItemPriv2(object,plan_id,khKey,khObject);
			ArrayList pointItemList = (ArrayList)map.get("pointItemList");
			ArrayList itemprivList = (ArrayList)map.get("itemPrivList");
			this.getFormHM().put("itemprivList",itemprivList);
			this.getFormHM().put("pointItemList",pointItemList);
		}

    }

}
