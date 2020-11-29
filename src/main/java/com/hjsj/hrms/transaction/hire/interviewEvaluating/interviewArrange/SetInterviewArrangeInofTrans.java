package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hjsj.hrms.businessobject.hire.InterviewEvaluatingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetInterviewArrangeInofTrans extends IBusiness {

	public void execute() throws GeneralException {
		String id=(String)this.getFormHM().get("id");
		//2014.10.15 xiexd 员工录用报到时间进入，未加密，进行判断后是否解密  长度应该小于2 xcs 2014-10-20
		if(id.split("/").length<2){
			id = PubFunc.decrypt(id);
		}
		String value=(String)this.getFormHM().get("value");
		String type=(String)this.getFormHM().get("type");
		String[] ids=id.split("/");
		
		InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
		interviewEvaluatingBo.saveInterviewArrangeInfo(ids[0],ids[1],value,type);
		
		
	}

}
