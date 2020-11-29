package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

/**
 * <p>Title: SaveTemplateQuestionTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-8-28 下午5:10:58</p>
 * @author jingq
 * @version 1.0
 */
public class SaveTemplateQuestionTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		try {
			Object questionnaire =  this.getFormHM().get("questionnaire");
			String param = (String) this.getFormHM().get("param");
			boolean flag = (Boolean) this.getFormHM().get("flag");
			TemplateBo bo = new TemplateBo();
			JSONObject quesObj = JSONObject.fromObject(questionnaire);
			bo.SaveTemplate(param,quesObj,userView);
			
			if(flag)
		        this.getFormHM().put("imageSavePath", bo.getPicturePath(quesObj.getString("qnid"), this.getFrameconn()));
			if("1".equals(param)){
				bo.publishTemplate(quesObj.getString("planid"),quesObj.getString("qnid"));
				bo.updatePendingTaskPlanName(this.frameconn, quesObj.getString("planid"));
			}
			//xiegh 2017/3/15 对planid和qnname做非空判断
			if(quesObj.containsKey("qnid"))
				this.getFormHM().put("qnid", quesObj.getString("qnid"));
			if(quesObj.containsKey("planid"))
				this.getFormHM().put("planid", quesObj.getString("planid"));
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
		}
	}

}
