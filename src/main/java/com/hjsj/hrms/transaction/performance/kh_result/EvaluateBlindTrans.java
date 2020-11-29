package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class EvaluateBlindTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String object_id = PubFunc.decryption((String)hm.get("object_id"));
			String plan_id = PubFunc.decryption((String)hm.get("plan_id"));
			hm.remove("object_id");
			hm.remove("plan_id");
			
			double percent = 0;//取得盲点百分比（360考核或目标考核）
			ResultBo bo = new ResultBo(this.getFrameconn(),this.userView);
			int method=bo.getPlanMethod(Integer.parseInt(plan_id));
			percent = bo.getPercent(method);
			ArrayList blindList = new ArrayList();//存储优势盲点和劣势盲点  大list存放小List，小list依次存放：指标id，指标名称，自评分数，他评分数
			HashMap pointScoreMap = bo.getFieldMap(object_id,plan_id,method);//得到所有的指标的分数
			blindList = bo.getBlindList(pointScoreMap,percent);
			String blindHtml = "";
			blindHtml = bo.getBlindHtml(blindList);
			this.getFormHM().put("blindHtml", blindHtml);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
