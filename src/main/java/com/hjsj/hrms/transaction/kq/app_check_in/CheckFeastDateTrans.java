package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 判断开始和结束日期是否在当前考勤期间之后
 * @author Administrator
 *
 */
public class CheckFeastDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String resultStr = "ok";
		String start = (String) this.getFormHM().get("z1");
		String end = (String) this.getFormHM().get("z3");
		String temp = "";
		
		
		this.getFormHM().put("resultStr", SafeCode.encode(resultStr));
		
	}
}
