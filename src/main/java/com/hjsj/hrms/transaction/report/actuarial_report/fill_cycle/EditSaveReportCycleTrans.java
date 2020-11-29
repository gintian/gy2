package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EditSaveReportCycleTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
		String value = (String)hm.get("b_editsave");
		if("editsave".equals(value)){
		RecordVo vo = (RecordVo) this.getFormHM().get("reportcyclevo");
		if (vo == null)
			return;
		//判断是否是默认
		String date = "";
		if(this.getFormHM().get("adddate")!=null&&!"".equals(this.getFormHM().get("adddate"))){
			date =(String)this.getFormHM().get("adddate");
			this.getFormHM().remove("adddate");
		}else{
		if(vo.getString("bos_date")!=null&&!"".equals(vo.getString("bos_date"))){
			date = vo.getString("bos_date");
		}
		}
		String year ="";
		
		if(vo.getString("theyear")!=null&&!"".equals(vo.getString("theyear"))){
			year = vo.getString("theyear");
		}else{
			Calendar c =  Calendar.getInstance();
			year =String.valueOf(c.get(Calendar.YEAR));
		}
		
		
		if("".equals(date)){
		vo.setDate("bos_date", new Date());
		}else{
			vo.setDate("bos_date", date);
		}
		vo.setString("theyear", year);
		vo.setString("status", "01");
		//AdminCode.getCodeName("23","01");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.updateValueObject(vo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}else{
			
		}
	      	
	}
	




}
