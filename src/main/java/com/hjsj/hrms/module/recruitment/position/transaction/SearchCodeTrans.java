package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchCodeTrans  extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
		String codeItemId=(String) this.getFormHM().get("codeItemId");
		
		this.getFormHM().put("data", bo.getcodeItemList(codeItemId));
	}

}
