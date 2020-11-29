package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainEffectEvalBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CheckEffecEvaltrans extends IBusiness {
	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

		String id = "";
		String quesJob = "";
		String quesTeacher = "";
		
		if(null != hm){
			id = (String) hm.get("id");
			quesJob = (String) hm.get("quesJob");
			quesTeacher = (String) hm.get("quesTeacher");
			hm.remove("id");
			hm.remove("flag");
			hm.remove("quesJob");
			hm.remove("quesTeacher");
		}else{
			id = (String)this.getFormHM().get("id");
			quesJob = (String)this.getFormHM().get("quesJob");
			quesTeacher = (String)this.getFormHM().get("quesTeacher");
		}
		
		TrainClassBo cbo = new TrainClassBo(frameconn);
        if(!cbo.checkClassPiv(id, this.userView))
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));

		String check = "no";
		String che = "no";
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "SELECT R3101 FROM R31 where ctrl_param like '%>"+quesJob+"</questionnaire>%'";
		try {// 查看是培训班/教师教学效果调查问卷是否使用
			this.frecset = dao.search(sql);
			if (this.frecset.next()) {
				String classid = this.frecset.getString("R3101");
				TrainEffectEvalBo bo = new TrainEffectEvalBo();
				bo = new TrainEffectEvalBo(this.frameconn, classid);
				String quesJob1 = bo.getBean("questionnaire", "job").get("text").toString();
				String quesTeacher1 = bo.getBean("questionnaire", "teacher").get("text").toString();
				if (quesJob.equals(quesJob1) && (!"".equals(quesJob) || quesJob == null)) {
					check = "yes";
				}
				if (quesTeacher.equals(quesTeacher1) && (!"".equals(quesTeacher) || quesTeacher == null)) {
					che = "yes";
				}
			}
			this.getFormHM().put("check", check);
			this.getFormHM().put("che", che);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
