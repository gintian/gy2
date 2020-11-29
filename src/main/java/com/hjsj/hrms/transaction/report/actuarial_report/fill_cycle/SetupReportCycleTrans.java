package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SetupReportCycleTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("report_id");
		hm.remove("id");
		RecordVo vo = new RecordVo("tt_cycle");
		vo.setInt("id", Integer.parseInt(id));
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		EditReport editRport=new EditReport();
		try {
			vo = dao.findByPrimaryKey(vo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String pre_status=vo.getString("status");
			 
			vo.setString("status", "04");
			dao.updateValueObject(vo);
			String kmethod = vo.getString("kmethod");
			 Date bos_date=new Date();
			 Date pre_bos_date=new Date();
			 bos_date = vo.getDate("bos_date");
			 if(bos_date==null)
					return;		
			if(kmethod!=null&& "0".equals(kmethod)){
			//xgq自动引入上期数据到表二中
				String date_str=DateUtils.format(bos_date, "yyyy-MM-dd");
				String sqlstr="select id,bos_date from tt_cycle  where bos_date<"+Sql_switcher.dateValue(date_str)+" and  kmethod=0 order by bos_date desc";	
				int cycle_id=0;
				this.frowset=dao.search(sqlstr);			  
				if(this.frowset.next())
				{
						cycle_id=this.getFrowset().getInt("id");
						pre_bos_date=this.frowset.getDate("bos_date");
						
				}
				if(cycle_id!=0&& "01".equals(pre_status))
				{
					editRport.importPreDate(id,String.valueOf(cycle_id),this.getFrameconn(),pre_bos_date,bos_date);
				}
				
				Calendar cd=Calendar.getInstance();
				cd.setTime(bos_date);
				sqlstr="select id,bos_date from tt_cycle  where bos_date<"+Sql_switcher.dateValue(date_str)+" and "+Sql_switcher.year("bos_date")+"="+cd.get(Calendar.YEAR)+" and  kmethod=1 order by bos_date desc";	
				cycle_id=0;
				this.frowset=dao.search(sqlstr);			  
				if(this.frowset.next())
				{
						cycle_id=this.getFrowset().getInt("id");
						pre_bos_date=this.frowset.getDate("bos_date");
				}
				if(cycle_id!=0&& "01".equals(pre_status))
				{
					editRport.importPreRetirementDate(id,String.valueOf(cycle_id),this.getFrameconn(),bos_date,pre_bos_date);
				}
				/*
			String sql = "select distinct(unitcode) from tt_calculation_ctrl where id="+cycle_id+" and ( report_id='U02_1' or report_id='U02_2'or report_id='U02_3' or report_id='U02_4') ";
			this.frowset =dao.search(sql);
			String reportidstr="U02_1,U02_2,U02_3,U02_4";
			String reportidstr2[]= reportidstr.split(",");
			while (this.frowset.next()){
				String unitcode =this.getFrowset().getString(1);
				for(int i=0;i<reportidstr2.length;i++){
					String report_id = reportidstr2[i];
					EditReport.introduceData(unitcode,id,report_id,this.getFrameconn(),this.userView);
				}
			}*/
			
			
			
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	




}
