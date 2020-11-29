package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.monthwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteWorkDiaryTrans extends IBusiness{
	public void execute() throws GeneralException  {
		try{
			String flag  =(String)this.getFormHM().get("flag");
			String p0100  =(String)this.getFormHM().get("p0100");
			String record_num  =(String)this.getFormHM().get("record_num");
			WorkDiaryBo bo = new WorkDiaryBo(this.getFrameconn(),this.userView,this.userView.getDbname(),this.userView.getA0100());
			bo.deleteContent(p0100,record_num);
			this.getFormHM().put("flag", flag);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
