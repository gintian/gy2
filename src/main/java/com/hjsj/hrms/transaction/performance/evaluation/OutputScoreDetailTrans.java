package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.ScoreDetailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class OutputScoreDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String plan_id=SafeCode.decode((String)this.getFormHM().get("plan_id"));
			String method=SafeCode.decode((String)this.getFormHM().get("method"));
			if(method==null || "".equals(method))
				method="1";
			String template_id=SafeCode.decode((String)this.getFormHM().get("template_id"));
			String object_id=PubFunc.decryption(SafeCode.decode((String)this.getFormHM().get("recheckObjectid")));
			String showWays=SafeCode.decode((String)this.getFormHM().get("showWays"));
			String plan_name = SafeCode.decode((String)this.getFormHM().get("plan_name"));
			String objectName = SafeCode.decode((String)this.getFormHM().get("objectName"));
			String object_type = SafeCode.decode((String)this.getFormHM().get("object_type"));//考核对象类型  1:部门 2:人员 3:单位 4.部门
			
			ScoreDetailBo sdBo = new ScoreDetailBo(this.getFrameconn(),this.userView,plan_id,method,template_id,object_type);
			//得到项目的总的list
			ArrayList itemTotalList = new ArrayList();
			itemTotalList = sdBo.getPerformanceStencilList(template_id,object_id);
			
			//得到指标总的list
			ArrayList pointTotalList = new ArrayList();
			pointTotalList = sdBo.getPerPointList(template_id,plan_id,object_id);
			
			HashMap totalMap = new HashMap();
			totalMap = sdBo.getTotalMap(object_id);
			String fileName=sdBo.getEvaluationTableExcel(plan_name,objectName,itemTotalList,pointTotalList,totalMap,Integer.parseInt(showWays));
			fileName = PubFunc.encrypt(fileName);
			this.getFormHM().put("filename",fileName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
