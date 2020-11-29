package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.ShowCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ShowCardTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			//获得链接中的参数
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String scoreExplainFlag = (String)hm.get("scoreExplainFlag");//用户是否勾选了"显示得分说明"
			hm.remove("scoreExplainFlag");
			String fromjs = (String)hm.get("fromjs");//用户是否勾选了"显示得分说明"
			hm.remove("fromjs");
			String object_id ="";
			if(!(fromjs==null || "".equals(fromjs))){
				object_id = PubFunc.decrypt((String)hm.get("cardObject_id")); //从获得考核对象
				hm.remove("cardObject_id");
			}
			else{
				String temp = (String) this.getFormHM().get("cardObject_id");
				object_id = PubFunc.decrypt(temp); //获得考核对象
			}
							
			//从Form中获得参数
			String plan_id = (String) this.getFormHM().get("planid");//考核计划号
			String method = (String) this.getFormHM().get("method");//考核方法。    360为1，目标管理为2
			if(method==null || "".equals(method))
				method="1";
			String template_id = (String)this.getFormHM().get("templateid");
			String object_type = (String)this.getFormHM().get("object_type");//考核对象类型  1:部门 2:人员 3:单位 4.部门
			Hashtable plan_parameters = (Hashtable)this.getFormHM().get("planParamSet");
			ShowCardBo bo = new ShowCardBo(this.getFrameconn(),this.userView,plan_id,template_id,object_id,method,object_type,scoreExplainFlag);
			String scoreExplain = bo.getScoreExplain(plan_parameters);//0 不显示评分说明  1 显示评分说明
			ArrayList object_list = new ArrayList(); //考核对象列表
			object_list = bo.getObjectList();
			String cardHtml = "";
			if("1".equals(scoreExplainFlag)){//如果用户勾选了"显示评分说明"
				cardHtml = bo.getTableHtml(scoreExplainFlag);
			}else{
				cardHtml = bo.getTableHtml();
			}
			this.getFormHM().put("cardHtml", cardHtml);
			this.getFormHM().put("scoreExplain", scoreExplain);
			this.getFormHM().put("scoreExplainFlag", scoreExplainFlag);
			this.getFormHM().put("cardObject_id", PubFunc.encrypt(object_id));
			this.getFormHM().put("object_list", object_list);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
