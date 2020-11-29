package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.mortbay.util.ajax.JSON;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title: SearchQuestionTypeTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-8-26 下午2:12:58</p>
 * @author jingq
 * @version 1.0
 */
public class SearchTemplateQuestionTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String qn_template = "";
		String questionType = "";
		try {
			String qnid = (String) this.getFormHM().get("qnid");
			String qnname = (String) this.getFormHM().get("qnname");
			qnid = qnid==null?"":qnid;
			qnname = qnname==null?"":qnname;
			Object[] objs = new Object[2];
			objs[0] = "qnid="+qnid;
			objs[1] = "qnname="+qnname;
			TemplateBo bo = new TemplateBo();
			if(qnid.length()>0){//使用模板
				qn_template = bo.getTemplate(objs,true);
			} else {//新建试卷
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("qnname", qnname);
				map.put("qnlongname", qnname);
				map.put("questionList", new ArrayList<Object>());
				qn_template = JSON.toString(map);
			}
			//问卷题型
			questionType = bo.getQuestionType(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("questionType", questionType);
		this.getFormHM().put("qn_template", qn_template);
	}

}
