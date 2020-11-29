package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveRzgisterTrans extends IBusiness {

	
	@Override
    public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			ArrayList info=(ArrayList)this.getFormHM().get("info");
			RecruitProcessBo bo = new RecruitProcessBo(this.frameconn, this.userView);
			//修改招聘人员库中人员职位信息
			bo.updateA01(info);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
