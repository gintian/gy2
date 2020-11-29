package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.ScoreDetailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ScoreDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			//获得链接中的参数
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String object_id = PubFunc.decryption((String) hm.get("recheckObjectid")); //获得考核对象
			String showWays = (String)hm.get("showWays");
			hm.remove("object_id");
			hm.remove("showWays");
			String plan_id = (String) this.getFormHM().get("planid");//考核计划号
			String method = (String) this.getFormHM().get("method");//考核方法。    360为1，目标管理为2
			if(method==null || "".equals(method))
				method="1";
			String template_id = (String)this.getFormHM().get("templateid");
			String object_type = (String)this.getFormHM().get("object_type");//考核对象类型  1:部门 2:人员 3:单位 4.部门
			
			ScoreDetailBo sdBo = new ScoreDetailBo(this.getFrameconn(),this.userView,plan_id,method,template_id,object_type);
			
			//通过计划号获取计划名称
			String plan_name = sdBo.getPlanName();
			//通过考核对象编号获得考核对象名字
			String objectName = sdBo.getObjectName(object_id);
			/*画表头**/
			
			//得到项目的总的list
			ArrayList itemTotalList = new ArrayList();
			itemTotalList = sdBo.getPerformanceStencilList(template_id,object_id);
			
			//得到指标总的list
			ArrayList pointTotalList = new ArrayList();
			pointTotalList = sdBo.getPerPointList(template_id,plan_id,object_id);
			
			//先把表头画出来
			String theadHtml = ""; 
			theadHtml = sdBo.getTableHeadHtml(itemTotalList,pointTotalList);
			
			/*画表体**/
			
			LinkedHashMap totalMap = new LinkedHashMap();
			totalMap = sdBo.getTotalMap(object_id);
			//再把数据画出来
			String dataHtml = "";
			dataHtml = sdBo.getDataHtml(totalMap,pointTotalList,Integer.parseInt(showWays));
			
			StringBuffer tableHtml = new StringBuffer();
			tableHtml.append(theadHtml+dataHtml);
			//put出去
			this.getFormHM().put("plan_name", plan_name);
			this.getFormHM().put("objectName", objectName);
			this.getFormHM().put("tableHtml", tableHtml.toString());
			this.getFormHM().put("recheckObjectid", PubFunc.encryption(object_id));//为了分数  标度 百分制
			this.getFormHM().put("showWays", showWays);//为了分数  标度 百分制
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
