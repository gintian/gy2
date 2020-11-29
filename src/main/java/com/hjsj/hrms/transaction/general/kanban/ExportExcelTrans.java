package com.hjsj.hrms.transaction.general.kanban;

import com.hjsj.hrms.businessobject.general.kanban.KanBanBo;
import com.hjsj.hrms.businessobject.hire.ExecuteExcel;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		String strSql=(String)this.getFormHM().get("strsql");
		String strSql = (String) this.userView.getHm().get("performance_sql");
		strSql=SafeCode.decode(strSql);
		strSql=PubFunc.keyWord_reback(strSql);
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		ArrayList fieldList = new ArrayList();
		if("P05".equalsIgnoreCase(fieldsetid)){
			KanBanBo kb = new KanBanBo(this.userView,this.frameconn);
			fieldList =kb.itemList();
		}else if("P01".equalsIgnoreCase(fieldsetid)){
			WorkdiarySQLStr kb = new WorkdiarySQLStr();
			fieldList =kb.fieldList();
		}
		ExecuteExcel executeExcel = new ExecuteExcel(this.frameconn, this.getUserView(), fieldsetid);
		String outName = executeExcel.createTabExcelHt2(fieldList, strSql, "3");
//		outName = outName.replaceAll(".xls", "#");
		outName = PubFunc.encrypt(outName);
		this.getFormHM().put("outName", outName);
	}

}
