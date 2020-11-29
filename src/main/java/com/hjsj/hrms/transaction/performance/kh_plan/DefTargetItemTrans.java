package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:DefTargetItemTrans.java</p>
 * <p>Description:绩效计划参数定义目标指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-14 11:11:11</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class DefTargetItemTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
    	String isCheck=(String)map.get("isCheck");
	    String targetItem = (String)this.getFormHM().get("targetItem");	  
	    String planId = (String)map.get("plan_id");
		String targetTraceItem = (String)this.getFormHM().get("targetTraceItem");
		String targetCollectItem = (String)this.getFormHM().get("targetCollectItem");
		ExamPlanBo bo = new ExamPlanBo(planId,this.frameconn);
		String targetCollectItemMust = "";
        if(targetCollectItem!=null && targetCollectItem.length()>0) {
            String[] items = targetCollectItem.split(",");
            targetCollectItem="";
            for (int i = 0; i < items.length; i++){
                String[] temps = items[i].split(":");
                targetCollectItem += temps[0]+",";
                if(temps.length>1) {
                    if("1".equals(temps[1])){
                        targetCollectItemMust += temps[0]+",";
                    }
                }
            }
            bo.setTargetCollectItemMust(targetCollectItemMust);
        }
		String targetCalcItem = (String)this.getFormHM().get("targetCalcItem");
		String allowLeaderTrace = (String)this.getFormHM().get("allowLeaderTrace");
		String targetDefineItem = (String)this.getFormHM().get("targetDefineItem");
		String targetMustFillItem = (String)this.getFormHM().get("targetMustFillItem");
		String targetUsePrevious = (String)this.getFormHM().get("targetUsePrevious");
		String taskNameDesc=(String)this.getFormHM().get("taskNameDesc");
		ArrayList targetDefineItemList = bo.getTargetDefineItemList(targetDefineItem,isCheck);
		ArrayList targetCollectItemList = new ArrayList();
		ArrayList targetTraceItemList =  new ArrayList();
		ArrayList targetCalcItemList=new ArrayList();
		ArrayList targetMustFillItemList=new ArrayList();
		ArrayList targetUsePreviousList=new ArrayList();
		if(targetDefineItemList !=null){
			LazyDynaBean abean = new LazyDynaBean();
			abean.set("itemid", "rater");
			abean.set("itemdesc", "评价人");
			abean.set("selected", targetDefineItem.indexOf("rater")>-1?"1":"0");
			targetDefineItemList.add(abean);
		}
		if(targetDefineItem.trim().length()==0)
		{
			ArrayList tempList = new ArrayList();
			for(int i=0;i<targetDefineItemList.size();i++)
			{
				LazyDynaBean abean =  (LazyDynaBean)targetDefineItemList.get(i);
				if(!"RATER".equalsIgnoreCase((String)abean.get("itemid")))
					abean.set("selected","1");
				tempList.add(abean);
				targetDefineItem+=","+(String)abean.get("itemid");
			}
			targetDefineItemList=tempList;
			targetDefineItem=targetDefineItem.substring(1);
			targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItem,isCheck);
			targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetTraceItem,isCheck);
			targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItem);
			targetMustFillItemList = bo.getTargetItemList(targetDefineItem,targetMustFillItem,isCheck);
			targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,isCheck);
			
		}else if(",".equals(targetDefineItem))//客户将目标卡指标一个也不选点击了保存按钮
		{
			
			targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItem);
		}else
		{
			targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItem);
			targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItem,isCheck);
			targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetTraceItem,isCheck);
			targetMustFillItemList = bo.getTargetItemList(targetDefineItem,targetMustFillItem,isCheck);
			targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,isCheck);
		}	
		String targetComputeItem = "";
		for(int i=0;i<targetDefineItemList.size();i++)
		{
			LazyDynaBean abean =  (LazyDynaBean)targetDefineItemList.get(i);						
			targetComputeItem+=","+(String)abean.get("itemid");
		}
		if(targetComputeItem!=null && targetComputeItem.trim().length()>0)
			targetComputeItem = targetComputeItem.substring(1);
		this.getFormHM().put("taskNameDesc", taskNameDesc);
		this.getFormHM().put("calItemStr",bo.getComputeItemStr(targetComputeItem));
		this.getFormHM().put("targetDefineItemList", targetDefineItemList);
		this.getFormHM().put("targetCollectItemList", targetCollectItemList);
		this.getFormHM().put("targetCalcItemList", targetCalcItemList);
		this.getFormHM().put("targetTraceItemList", targetTraceItemList);
		this.getFormHM().put("targetMustFillItemList", targetMustFillItemList);
		this.getFormHM().put("targetUsePreviousList", targetUsePreviousList);
    }

}
