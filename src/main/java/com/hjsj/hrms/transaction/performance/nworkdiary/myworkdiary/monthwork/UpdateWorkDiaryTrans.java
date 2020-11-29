package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.monthwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class UpdateWorkDiaryTrans extends IBusiness{
	public void execute() throws GeneralException  {
		try{
			String flag  =(String)this.getFormHM().get("flag");
			String p0100  =(String)this.getFormHM().get("p0100");
			String record_num  =(String)this.getFormHM().get("record_num");
			String title  =(String)this.getFormHM().get("title");
			String type  =(String)this.getFormHM().get("type");
			String start_time  =(String)this.getFormHM().get("start_time");
			String startHour  =(String)this.getFormHM().get("startHour");
			String startMinute  =(String)this.getFormHM().get("startMinute");
			String end_time  =(String)this.getFormHM().get("end_time");
			String endHour  =(String)this.getFormHM().get("endHour");
			String endMinute  =(String)this.getFormHM().get("endMinute");
			String content  =SafeCode.decode((String)this.getFormHM().get("content"));
			WorkDiaryBo bo = new WorkDiaryBo(this.getFrameconn(),this.userView,this.userView.getDbname(),this.userView.getA0100());
			bo.updateContent(p0100,record_num,title,type,start_time,startHour,end_time,endHour,content,startMinute,endMinute);
			this.getFormHM().put("flag", flag);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
