package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.TotalEvaluateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TotalEvaluateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String fromjs = (String)hm.get("fromjs");
			hm.remove("fromjs");
			String object_id ="";
			if(!(fromjs==null || "".equals(fromjs))){
				object_id = PubFunc.decrypt((String)hm.get("totalevaluateObject")); //从页面获得考核对象
				hm.remove("totalevaluateObject");
			}
			else
				object_id = PubFunc.decrypt((String) this.getFormHM().get("totalevaluateObject")); //从form获得考核对象
			
			//从Form中获得参数
			String plan_id = (String) this.getFormHM().get("planid");//考核计划号
			
			TotalEvaluateBo bo = new TotalEvaluateBo(this.getFrameconn(),this.userView,plan_id,object_id);
			ArrayList evaluate_object_list = new ArrayList(); //考核对象列表
			evaluate_object_list = bo.getEvaluate_object_list();
			LinkedHashMap dataMap = new LinkedHashMap();
			dataMap = bo.getDataMap();
			String evaluateHtml = "";
			evaluateHtml = bo.getEvaluateHtml(dataMap);
			this.getFormHM().put("totalevaluateObject", PubFunc.encryption(object_id));
			this.getFormHM().put("evaluate_object_list", evaluate_object_list);
			this.getFormHM().put("evaluateHtml", evaluateHtml);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
