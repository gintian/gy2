package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.module.questionnaire.template.businessobject.AnswerResultBo;
import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

public class SearchPreviewTemplateTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException{
		Object qnid =  this.getFormHM().get("qnid");
		String mainObject = (String)this.getFormHM().get("mainObject");
		String subObject = (String)this.getFormHM().get("subObject");
		String planid = "";
		String jsonobject= "";
		JSONObject obj = null;
		if(mainObject==null||"".equals(mainObject)){
			if(!"".equals(qnid)){
				Object[] objs = new Object[1];
				objs[0] = "qnid="+qnid;
				TemplateBo bo = new TemplateBo();
				jsonobject = bo.getTemplate(objs);
				obj = JSONObject.fromObject(jsonobject);
				obj.put("flag", "0");
			}
		}else{
			String sql = "select planId from qn_plan where qnId='"+qnid+"'";
			List childList = ExecuteSQL.executeMyQuery(sql);
			if(childList.size()>0){
				LazyDynaBean ldb = (LazyDynaBean)childList.get(0);
				Object object = ldb.get("planid");
				planid = (String)object;
			}
			mainObject = PubFunc.decrypt(mainObject);
			subObject = PubFunc.decrypt(subObject);
			Object[] objs = new Object[1];
			objs[0] = "qnid="+qnid;
			AnswerResultBo arbo = new AnswerResultBo();
			jsonobject = arbo.getTemplateResult(objs, mainObject, subObject, planid);
			obj = JSONObject.fromObject(jsonobject);
			obj.put("flag", "1");
		}
		
		this.getFormHM().put("jsonobject", obj.toString());
	}
}
