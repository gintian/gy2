package com.hjsj.hrms.module.recruitment.headhuntermanage.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class CheckHunterUserNameTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		String checkType = (String)this.getFormHM().get("checkType");
		String desc = "用户名";
		if("email".equals(checkType))
			desc = "邮箱";
		String value = (String)this.getFormHM().get("value");
		ArrayList arr = new ArrayList();
		arr.add(value);
		try {
			this.frowset = new ContentDAO(frameconn).search("select '1' from zp_headhunter_login where "+checkType+"=?",arr);
			if(this.frowset.next())
				this.getFormHM().put("result", "此"+desc+"已被使用");
			else{
				this.getFormHM().put("result",new Boolean(true));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
