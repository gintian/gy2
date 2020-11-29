package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.daywork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.CalendarBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class CalendarDayTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			//获得链接中的参数
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");			
			String frompage = (String)hm.get("frompage");//(-2:从主页中来   -1：从菜单中来    0:从日报中来   1：从周报传来  2：从月报传来   3：从年报传来  4:从员工日志来)
			hm.remove("frompage");
			String year = "";
			String month = "";
			String day = "";
			
			
			String fromyear = "";
			String frommonth = "";
			String fromday = "";
			
			String scrollValue = "0";//记录滚动条的位置
			String p01_key = "";//默认哪条记录变蓝。和recordNum配合使用
			String recordNum = "";//默认哪条记录变蓝
			String combineid = "";
			String commentTrace = "";
			combineid = (String)hm.get("combineid")==null?"":(String)hm.get("combineid");
			hm.remove("combineid");
			
			//员工日志部分
			
			String staff_url = "";
			if(frompage==null || "0".equals(frompage)){//说明是在日报中切换的。要从form中获取frompage
				frompage = (String)this.getFormHM().get("frompage");
				year = (String)hm.get("year");
				month = (String)hm.get("month");
				day = (String)hm.get("day");
				hm.remove("year");
				hm.remove("month");
				hm.remove("day");
				if("".equals(year)){
					GregorianCalendar ca = new GregorianCalendar();
					year = String.valueOf(ca.get(Calendar.YEAR));
					month = String.valueOf(ca.get(Calendar.MONTH)+1);
					day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
				}
				scrollValue = (String)this.getFormHM().get("scrollValue");
				p01_key = (String)this.getFormHM().get("p01_key");
				recordNum = (String)this.getFormHM().get("recordNum");	
				fromyear = (String)this.getFormHM().get("fromyear");
				frommonth = (String)this.getFormHM().get("frommonth");
				fromday = (String)this.getFormHM().get("fromday");
				staff_url = (String)this.getFormHM().get("staff_url");
			}else if("-1".equals(frompage)){//如果是从菜单传递过来的
				this.userView.getHm().put("a0100", this.userView.getA0100());
				this.userView.getHm().put("nbase", this.userView.getDbname());
				this.userView.getHm().put("isOwner", "0");
				GregorianCalendar ca = new GregorianCalendar();
				year = (String)hm.get("year");
				if(year==null || "".equals(year)){
					year = String.valueOf(ca.get(Calendar.YEAR));
					month = String.valueOf(ca.get(Calendar.MONTH)+1);
					day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
				}else{
					month = (String)hm.get("month");
					day = (String)hm.get("day");
					hm.remove("year");
					hm.remove("month");
					hm.remove("day");
				}
				p01_key=(String)hm.get("p01_key");
				hm.remove("p01_key");
				recordNum = (String)hm.get("recordNum");
				hm.remove("recordNum");
				staff_url = "";
			}else if("-2".equals(frompage)){//如果是首页中传递的
				this.userView.getHm().put("a0100", this.userView.getA0100());
				this.userView.getHm().put("nbase", this.userView.getDbname());
				this.userView.getHm().put("isOwner", "0");
				year = (String)hm.get("year");
				month = (String)hm.get("month");
				if(month.startsWith("0")){
					month = month.substring(1, 2);
				}
				day = (String)hm.get("day");
				if(day.startsWith("0")){
					day = day.substring(1, 2);
				}
				hm.remove("year");
				hm.remove("month");
				hm.remove("day");
				staff_url = "";
				p01_key=(String)hm.get("p01_key");
				hm.remove("p01_key");
				recordNum = (String)hm.get("recordNum");
				hm.remove("recordNum");
			}
			else if("1".equals(frompage) || "2".equals(frompage) || "3".equals(frompage)){//从周报、月报、年报,甚至日报本身中进入日报
				year = (String)hm.get("year");
				month = (String)hm.get("month");
				day = (String)hm.get("day");
				hm.remove("year");
				hm.remove("month");
				hm.remove("day");
				
				fromyear = (String)hm.get("fromyear");
				frommonth = (String)hm.get("frommonth");
				fromday = (String)hm.get("fromday");
				hm.remove("fromyear");
				hm.remove("frommonth");
				hm.remove("fromday");
				if(fromyear==null || "".equals(fromyear)){
					fromyear = year;
				}
				if(frommonth==null || "".equals(frommonth)){
					frommonth = month;
				}
				if(fromday==null || "".equals(fromday)){
					fromday = day;
				}
				if("".equals(year)){
					GregorianCalendar ca = new GregorianCalendar();
					year = String.valueOf(ca.get(Calendar.YEAR));
					month = String.valueOf(ca.get(Calendar.MONTH)+1);
					day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
				}
				scrollValue = "0";
				p01_key=(String)hm.get("p01_key");
				hm.remove("p01_key");
				recordNum = (String)hm.get("recordNum");
				hm.remove("recordNum");
				staff_url = "";
			}else if("4".equals(frompage)){//从员工日志中返回
				String tmpa0100 = (String)hm.get("a0100");
				if(tmpa0100 == null || "".equals(tmpa0100)){
					String a0100 = (String)this.userView.getHm().get("a0100");
					String nbase = (String)this.userView.getHm().get("nbase");
					this.userView.getHm().put("a0100", a0100);
					this.userView.getHm().put("nbase", nbase);
				}else{
					this.userView.getHm().put("a0100", (String)hm.get("a0100"));
					this.userView.getHm().put("nbase", (String)hm.get("nbase"));
				}
				this.userView.getHm().put("isOwner", "1");
				year = (String)hm.get("year");
				month = (String)hm.get("month");
				day = (String)hm.get("day");
				hm.remove("year");
				hm.remove("month");
				hm.remove("day");
				hm.remove("a0100");
				hm.remove("nbase");
				staff_url = (String)this.getFormHM().get("staff_url");
				scrollValue = "0";
				p01_key=(String)hm.get("p01_key");
				hm.remove("p01_key");
				recordNum = (String)hm.get("recordNum");
				hm.remove("recordNum");
				fromyear = year;
				frommonth = month;
				fromday = day;
			}
			String a0100 = (String)this.userView.getHm().get("a0100");
			String nbase = (String)this.userView.getHm().get("nbase");
			String isowner = (String)this.userView.getHm().get("isOwner");
			CalendarBo bo = new CalendarBo(this.getFrameconn(),this.userView,a0100,nbase);
			if("1".equals(isowner)){//如果是领导进来，则插入领导痕迹
				//先生成领导查看的时间---当前时间
				GregorianCalendar ca = new GregorianCalendar();
				String currentyear = String.valueOf(ca.get(Calendar.YEAR));
				String currentmonth = String.valueOf(ca.get(Calendar.MONTH)+1);
				String currentday = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
				String currentHour = String.valueOf(ca.get(Calendar.HOUR_OF_DAY));
				String currentMinute = String.valueOf(ca.get(Calendar.MINUTE));
				String comparetime = currentyear+"-"+currentmonth+"-"+currentday;
				String starttime = currentyear+"年"+currentmonth+"月"+currentday+"日"+currentHour+"时"+currentMinute+"分";
				String leader_a0100 = this.userView.getA0100();//领导的a0100
				String leader_nbase = this.userView.getDbname();//领导的nbase
				String leader_unit = this.userView.getUserOrgId();//领导的单位
				String leader_depart = this.userView.getUserDeptId();//领导的部门
				String leader_name = this.userView.getUserFullName();//领导的姓名
				bo.insertLeaderTrace(leader_a0100,leader_nbase,leader_unit,leader_depart,leader_name,comparetime,starttime,year,month,day);
			}else{//要在底部显示领导痕迹
				commentTrace = bo.showLeaderTrace(year,month,day);
			}
			/**输出日历*/
			String calendarDayHtml = "";
			String linecount = "";
			String[] temporaryarray = bo.getCalendarDayHtml(year,month,day);
			calendarDayHtml = temporaryarray[0];
			linecount = temporaryarray[1];
			/**输出工作记录列表*/
			String workRecordHtml = "";
			workRecordHtml = bo.getWorkRecordHtml(year,month,day);
			/**输出具体的工作记录*/
			String recordShowHtml = "";//全天事件的
			String jsonstr = "";//json串，生成时间轴 如果system.properties中clientName是国网，那么jsonstr就是动态画出的时间轴了。
			String leader = "";//领导批示
			String wholeScroll = "0";//全天事件的滚动条位置
			String axle = "0";//时间轴的滚动条位置
			String[] temporary = bo.getRecordShowHtml(year,month,day,frompage,fromyear,frommonth,fromday,staff_url,isowner,combineid,linecount);
			recordShowHtml = temporary[0];
			jsonstr = temporary[1];
			leader = temporary[2];
			wholeScroll = temporary[3];
			axle = temporary[4];
			
			this.getFormHM().put("calendarDayHtml", calendarDayHtml);
			this.getFormHM().put("workRecordHtml", workRecordHtml);
			this.getFormHM().put("recordShowHtml", recordShowHtml);
			this.getFormHM().put("commentTrace", commentTrace);
			this.getFormHM().put("jsonstr", jsonstr);
			this.getFormHM().put("leader", leader);
			this.getFormHM().put("wholeScroll", wholeScroll);
			this.getFormHM().put("scrollValue", scrollValue);
			this.getFormHM().put("axle", axle);
			this.getFormHM().put("p01_key", p01_key);
			this.getFormHM().put("recordNum", recordNum);
			
			this.getFormHM().put("frompage", frompage);
			this.getFormHM().put("fromyear", fromyear);
			this.getFormHM().put("frommonth", frommonth);
			this.getFormHM().put("fromday", fromday);
			this.getFormHM().put("isowner", isowner);
			//员工日志部分
			this.getFormHM().put("a0100", a0100);
			this.getFormHM().put("nbase", nbase);
			this.getFormHM().put("staff_url", staff_url);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
