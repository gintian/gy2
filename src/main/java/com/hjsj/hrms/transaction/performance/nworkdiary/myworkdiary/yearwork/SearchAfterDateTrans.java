package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.yearwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * SearchAfterDateTrans.java
 * Description: 查找变更后的日期国网年报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Jan 9, 2013 11:06:34 AM Jianghe created
 */
public class SearchAfterDateTrans extends IBusiness{
	public void execute() throws GeneralException 
	{
		String flag = (String)this.getFormHM().get("flag");
		String thisDate = (String)this.getFormHM().get("thisDate");
		WorkDiaryBo bo = null;
		try {
			bo = new WorkDiaryBo(this.getFrameconn(),this.userView,this.userView.getDbname(),this.userView.getA0100());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String afterDate = bo.getAfterYearDate(flag,thisDate);
		this.getFormHM().put("afterDate", afterDate);
		this.getFormHM().put("flag", flag);
	}
}
