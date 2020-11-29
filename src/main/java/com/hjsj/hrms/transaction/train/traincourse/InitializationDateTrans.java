package com.hjsj.hrms.transaction.train.traincourse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class InitializationDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String checkinfor = (String)this.getFormHM().get("checkinfor");
		checkinfor=checkinfor!=null?checkinfor:"0";
		
		if("1".equals(checkinfor)){
			String tabArr = (String)this.getFormHM().get("tabArr");
			tabArr=tabArr!=null?tabArr:"";
			
			String status = (String)this.getFormHM().get("status");
			status=status!=null&&status.trim().length()>0?status:"0";
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String arr[] = tabArr.split(",");
			for(int i=0;i<arr.length;i++){
				if(arr[i]!=null&&arr[i].trim().length()==3){
					try {
						dao.update("delete from "+arr[i]);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if ("tr_selected_course".equalsIgnoreCase(arr[i])
						|| "tr_selected_lesson".equalsIgnoreCase(arr[i])
						|| "tr_selfexam_test".equalsIgnoreCase(arr[i])
						|| "tr_exam_paper".equalsIgnoreCase(arr[i])
						|| "tr_exam_answer".equalsIgnoreCase(arr[i])
						|| "tr_lesson_paper".equalsIgnoreCase(arr[i])){
					try {
						dao.update("delete from "+arr[i]);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		Calendar now = Calendar.getInstance();
		Date cur_d=now.getTime();
		this.getFormHM().put("startime",DateUtils.format(cur_d,"yyyy-MM-dd"));
		this.getFormHM().put("endtime",DateUtils.format(cur_d,"yyyy-MM-dd"));
	}

}
