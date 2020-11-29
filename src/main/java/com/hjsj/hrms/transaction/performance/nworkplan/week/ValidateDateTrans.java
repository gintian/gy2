package com.hjsj.hrms.transaction.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.week.WeekWorkPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ValidateDateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String planYear_start=(String)this.getFormHM().get("planYear_start");
			String planMonth_start=(String)this.getFormHM().get("planMonth_start");
			String planDay_start=(String)this.getFormHM().get("planDay_start");
			String planYear_end=(String)this.getFormHM().get("planYear_end");
			String planMonth_end=(String)this.getFormHM().get("planMonth_end");
			String planDay_end=(String)this.getFormHM().get("planDay_end");
			String summarizeYear=(String)this.getFormHM().get("summarizeYear");
			String summarizeTime=(String)this.getFormHM().get("summarizeTime");
			String log_type = (String)this.getFormHM().get("log_type");
			String type=(String)this.getFormHM().get("type");
			String record_num=(String)this.getFormHM().get("record_num");
			if(summarizeTime!=null)
		    	summarizeTime=summarizeTime.replaceAll("－", "-"); 
			String personPage = (String)this.getFormHM().get("personPage");
			String isChuZhang= (String)this.getFormHM().get("isChuZhang");
			WeekWorkPlanBo wwpb = new WeekWorkPlanBo(this.getUserView(),this.getFrameconn(),(String)this.userView.getHm().get("opt"),"",personPage);
			String message=wwpb.validateDate(planYear_start, planMonth_start, planDay_start, planYear_end, planMonth_end, planDay_end, (String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"), summarizeTime);
			String global_sYear=(String)this.getFormHM().get("global_sYear");
			String global_sMonth=(String)this.getFormHM().get("global_sMonth");
			String global_sDay=(String)this.getFormHM().get("global_sDay");
			String global_eYear=(String)this.getFormHM().get("global_eYear");
			String global_eMonth=(String)this.getFormHM().get("global_eMonth");
			String global_eDay=(String)this.getFormHM().get("global_eDay");
			String iscontinue=(String)this.getFormHM().get("iscontinue");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD");
			if("".equals(message)&& "2".equals(iscontinue)){
				String parentidString=wwpb.getParentDept();
				StringBuffer buffer = new StringBuffer("");
				Calendar d1 =Calendar.getInstance();
				Calendar d2 = Calendar.getInstance();
				d1.set(Calendar.YEAR, Integer.parseInt(planYear_start));
				d1.set(Calendar.MONTH, Integer.parseInt(planMonth_start)-1);
				d1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(planDay_start));
				d2.set(Calendar.YEAR, Integer.parseInt(planYear_end));
				d2.set(Calendar.MONTH, Integer.parseInt(planMonth_end)-1);
				d2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(planDay_end));
				//d1.setTime(format.parse(planYear_start+"-"+planMonth_start+"-"+planDay_start));
				//d2.setTime(format.parse(planYear_end+"-"+planMonth_end+"-"+planDay_end));
				buffer.append(" update p01 ");
				buffer.append(" set p0104=?,p0106=?");
			    buffer.append(" where e0122 like '"+parentidString+"%'");
			    buffer.append(" and state=1 ");
			    buffer.append(" and "+Sql_switcher.year("p0104")+"="+Integer.parseInt(global_sYear));
			    buffer.append(" and "+Sql_switcher.month("p0104")+"="+Integer.parseInt(global_sMonth));
			    buffer.append(" and "+Sql_switcher.day("p0104")+"="+Integer.parseInt(global_sDay));
			    buffer.append(" and "+Sql_switcher.year("p0106")+"="+Integer.parseInt(global_eYear));
			    buffer.append(" and "+Sql_switcher.month("p0106")+"="+Integer.parseInt(global_eMonth));
			    buffer.append(" and "+Sql_switcher.day("p0106")+"="+Integer.parseInt(global_eDay));
			    java.sql.Date date1=new java.sql.Date(d1.getTimeInMillis());
			    java.sql.Date date2=new java.sql.Date(d2.getTimeInMillis());
			    ContentDAO dao = new ContentDAO(this.getFrameconn());
			    ArrayList paramList = new ArrayList();
			    paramList.add(date1);
			    paramList.add(date2);
				dao.update(buffer.toString(),paramList);
			}
			
			
			this.getFormHM().put("message", message);
			this.getFormHM().put("planYear_start", planYear_start);
			this.getFormHM().put("planMonth_start",planMonth_start);
			this.getFormHM().put("planDay_start", planDay_start);
			this.getFormHM().put("type",type);
			this.getFormHM().put("planYear_end", planYear_end);
			this.getFormHM().put("planMonth_end", planMonth_end);
			this.getFormHM().put("planDay_end", planDay_end);
			this.getFormHM().put("record_num", record_num);
			this.getFormHM().put("log_type", log_type);
			
			
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
