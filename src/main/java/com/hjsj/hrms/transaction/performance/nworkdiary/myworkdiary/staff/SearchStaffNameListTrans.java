package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.staff;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.StaffDiaryBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchStaffNameListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			StaffDiaryBo bo = new StaffDiaryBo(this.getFrameconn(),this.userView);
			String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
			String a0100 = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("a0100")));
			String nbase = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("nbase")));
			String staff_year = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("staff_year")));
			String staff_month = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("staff_month")));
			String staff_week = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("staff_week")));
			String staff_day = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("staff_day")));
		    
			ArrayList userlist  = bo.getNameListByInput(name,a0100,nbase,staff_year,staff_month,staff_week,staff_day);
			this.getFormHM().put("namelist", userlist);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}	

}
