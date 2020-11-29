package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.FunctionRecruitBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchRecruitMemberTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 * 获取职位有环节操作权限的招聘成员
	 */
	@Override
	public void execute() throws GeneralException {
		
		try {
			FunctionRecruitBo bo = new FunctionRecruitBo(this.frameconn, this.userView);
			//环节id
			String linkId = (String) this.formHM.get("linkId");
			//职位id
			String z0301 = (String) this.formHM.get("z0301");
			z0301 = PubFunc.decrypt(z0301);
			ArrayList<HashMap> defPerson = bo.getDefPerson(linkId, z0301);
			this.getFormHM().put("defPerson", defPerson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
