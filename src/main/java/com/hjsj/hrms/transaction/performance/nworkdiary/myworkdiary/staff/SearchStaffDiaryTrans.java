package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.staff;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.StaffDiaryBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
public class SearchStaffDiaryTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");	
			String fromFlag=(String)this.getFormHM().get("fromFlag");
			StaffDiaryBo bo = new StaffDiaryBo(this.getFrameconn(),this.userView);
			String staff_year="";
			String staff_month="-1";
			String staff_week="-1";
			String staff_week_show = "-1";
			String staff_day="-1";
			String staff_day_show = "-1";
			ArrayList yearList=null;
			ArrayList monthList=null;
			ArrayList weekList = null;
			ArrayList dayList = null;
			String staff_name="";
			ArrayList staff_namelist = new ArrayList();
			String a0100 = "";//为了适应从部门人员进入
			String nbase = "";
			String tmp2 = "-1";//代替staff_day。
			if("0".equals(fromFlag)){//从部门人员进入
				//从菜单进入，默认显示当年，当月，当周，当天
				Calendar calendar = Calendar.getInstance();
				staff_year = calendar.get(Calendar.YEAR)+"";
				staff_month=(calendar.get(Calendar.MONTH)+1)+"";
				staff_day=staff_year+"-"+staff_month+"-"+calendar.get(Calendar.DAY_OF_MONTH)+"";
				String[]a = staff_day.split("-");
				staff_day_show = staff_month+"月"+a[2]+"日";
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList = bo.getWeekList(staff_year, staff_month);
				staff_week = bo.getWeekStr(staff_year,staff_month,staff_day);
				String []t = staff_week.replaceAll("／", "/").split("/");
				String []t1 = t[0].split("-");
				String []t2 = t[1].split("-");
				String tmpBeginMonth = t1[1];
				String tmpBeginDay = t1[2];
				String tmpEndMonth = t2[1];
				String tmpEndDay = t2[2];
				staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
				dayList=bo.getDayList(staff_year, staff_month, staff_week);
				staff_name="";
				a0100 = (String)hm.get("a0100");
				nbase = (String)hm.get("nbase");
				hm.remove("a0100");
				hm.remove("nbase");
			}else if("1".equals(fromFlag)){//从菜单进入，默认显示当年，当月，当周，当天
				Calendar calendar = Calendar.getInstance();
				staff_year = calendar.get(Calendar.YEAR)+"";
				staff_month=(calendar.get(Calendar.MONTH)+1)+"";
				staff_day=staff_year+"-"+staff_month+"-"+calendar.get(Calendar.DAY_OF_MONTH)+"";
				String[]a = staff_day.split("-");
				staff_day_show = staff_month+"月"+a[2]+"日";
				tmp2 = a[2];
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList = bo.getWeekList(staff_year, staff_month);
				staff_week = bo.getWeekStr(staff_year,staff_month,staff_day);
				String []t = staff_week.replaceAll("／", "/").split("/");
				String []t1 = t[0].split("-");
				String []t2 = t[1].split("-");
				String tmpBeginMonth = t1[1];
				String tmpBeginDay = t1[2];
				String tmpEndMonth = t2[1];
				String tmpEndDay = t2[2];
				staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
				dayList=bo.getDayList(staff_year, staff_month, staff_week);
				staff_name="";
				a0100 = "";
				nbase = "";
			}else if("2".equals(fromFlag)){//切换年，月、周、日显示全部
				staff_year = (String)this.getFormHM().get("staff_year");
				yearList = bo.getYearList();
				staff_month="-1";
				monthList = bo.getMonthList();
				weekList=bo.getWeekList(staff_year, staff_month);
				staff_week="-1";
				staff_day="-1";
				dayList = bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)this.getFormHM().get("staff_name");//获取从页面中传递过来的数据
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("3".equals(fromFlag)){//切换月，周、日显示全部，周列表生成，但日的列表不生成
				staff_year = (String)this.getFormHM().get("staff_year");
				yearList = bo.getYearList();
				staff_month=(String)this.getFormHM().get("staff_month");
				monthList = bo.getMonthList();
				weekList=bo.getWeekList(staff_year, staff_month);
				staff_week="-1";
				staff_day="-1";
				dayList = bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)this.getFormHM().get("staff_name");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("4".equals(fromFlag)){//切换周,日显示全部，但日的列表生成
				staff_year = (String)this.getFormHM().get("staff_year");
				yearList = bo.getYearList();
				staff_month=(String)this.getFormHM().get("staff_month");
				monthList = bo.getMonthList();
				weekList=bo.getWeekList(staff_year, staff_month);
				staff_week=(String)this.getFormHM().get("staff_week");
				staff_day="-1";
				if(!"-1".equals(staff_week)){
					String []t = staff_week.replaceAll("／", "/").split("/");
					String []t1 = t[0].split("-");
					String []t2 = t[1].split("-");
					String tmpBeginMonth = t1[1];
					String tmpBeginDay = t1[2];
					String tmpEndMonth = t2[1];
					String tmpEndDay = t2[2];
					staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
				}
				dayList = bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)this.getFormHM().get("staff_name");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("5".equals(fromFlag)){//切换日
				staff_day=(String)this.getFormHM().get("staff_day");
				if("-1".equals(staff_day)){
					staff_year = (String)this.getFormHM().get("staff_year");
					staff_month=(String)this.getFormHM().get("staff_month");
				}else{
					String[] tmp = staff_day.split("-");
					staff_year = tmp[0];
					staff_month = tmp[1];
					tmp2 = tmp[2];
				}
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList=bo.getWeekList(staff_year, staff_month);
				staff_week=(String)this.getFormHM().get("staff_week");
				String []t = staff_week.replaceAll("／", "/").split("/");
				String []t1 = t[0].split("-");
				String []t2 = t[1].split("-");
				String tmpBeginMonth = t1[1];
				String tmpBeginDay = t1[2];
				String tmpEndMonth = t2[1];
				String tmpEndDay = t2[2];
				staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
				staff_day_show = staff_month+"月"+tmp2+"日";
				dayList = bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)this.getFormHM().get("staff_name");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("6".equals(fromFlag)){//从日报中返回
				staff_year = (String)hm.get("year");
				staff_month=(String)hm.get("month");
				staff_day = (String)hm.get("day");
				String[] a = staff_day.split("-");
				tmp2 = a[2];
				staff_day_show = staff_month+"月"+a[2]+"日";
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList = bo.getWeekList(staff_year, staff_month);
				staff_week = bo.getWeekStr(staff_year,staff_month,staff_day);
				String []t = staff_week.replaceAll("／", "/").split("/");
				String []t1 = t[0].split("-");
				String []t2 = t[1].split("-");
				String tmpBeginMonth = t1[1];
				String tmpBeginDay = t1[2];
				String tmpEndMonth = t2[1];
				String tmpEndDay = t2[2];
				staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
				dayList=bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)hm.get("a0101");
				staff_name = SafeCode.decode(staff_name);
				hm.remove("year");
				hm.remove("month");
				hm.remove("day");
				hm.remove("a0101");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("7".equals(fromFlag)){//从周报中返回
				staff_year = (String)hm.get("year");
				staff_month=(String)hm.get("month");
				staff_week = (String)hm.get("week");	
				staff_day = "-1";
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList = bo.getWeekList(staff_year, staff_month);
				String []t = staff_week.replaceAll("／", "/").split("/");
				String []t1 = t[0].split("-");
				String []t2 = t[1].split("-");
				String tmpBeginMonth = t1[1];
				String tmpBeginDay = t1[2];
				String tmpEndMonth = t2[1];
				String tmpEndDay = t2[2];
				staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
				dayList=bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)hm.get("a0101");
				staff_name = SafeCode.decode(staff_name);
				hm.remove("year");
				hm.remove("month");
				hm.remove("week");
				hm.remove("a0101");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("8".equals(fromFlag)){//从月报中返回
				staff_year = (String)hm.get("year");
				staff_month=(String)hm.get("month");
				staff_week = "-1";
				staff_week_show = "-1";
				staff_day = "-1";
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList = bo.getWeekList(staff_year, staff_month);
				dayList=bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)hm.get("a0101");
				hm.remove("year");
				hm.remove("month");
				hm.remove("a0101");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("9".equals(fromFlag)){//从年报中返回
				staff_year = (String)hm.get("year");
				staff_month="-1";
				staff_week = "-1";
				staff_week_show = "-1";
				staff_day = "-1";
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList = bo.getWeekList(staff_year, staff_month);
				dayList=bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)hm.get("a0101");
				hm.remove("year");
				hm.remove("a0101");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}else if("10".equals(fromFlag)){//点击查找
				staff_month=(String)this.getFormHM().get("staff_month");
				staff_week=(String)this.getFormHM().get("staff_week");
				staff_day=(String)this.getFormHM().get("staff_day");
				if("-1".equals(staff_month)){
					staff_year = (String)this.getFormHM().get("staff_year");
					staff_month="-1";
					staff_week="-1";
					staff_day="-1";
				}else if("-1".equals(staff_week)){
					staff_year = (String)this.getFormHM().get("staff_year");
					staff_month=(String)this.getFormHM().get("staff_month");
					staff_week="-1";
					staff_day="-1";
				}else if("-1".equals(staff_day)){
					staff_year = (String)this.getFormHM().get("staff_year");
					staff_month=(String)this.getFormHM().get("staff_month");
					staff_week=(String)this.getFormHM().get("staff_week");
					staff_day="-1";
					if(!"-1".equals(staff_week)){
						String []t = staff_week.replaceAll("／", "/").split("/");
						String []t1 = t[0].split("-");
						String []t2 = t[1].split("-");
						String tmpBeginMonth = t1[1];
						String tmpBeginDay = t1[2];
						String tmpEndMonth = t2[1];
						String tmpEndDay = t2[2];
						staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
					}
				}else{//如果有日
					staff_day=(String)this.getFormHM().get("staff_day");
					String[] tmp=staff_day.split("-");
					//staff_year = (String)this.getFormHM().get("staff_year");
					staff_year = tmp[0];
					//staff_month=(String)this.getFormHM().get("staff_month");
					staff_month = tmp[1];
					tmp2 = tmp[2];
					staff_week=(String)this.getFormHM().get("staff_week");//为了生成staff_week_show
					String []t = staff_week.replaceAll("／", "/").split("/");
					String []t1 = t[0].split("-");
					String []t2 = t[1].split("-");
					String tmpBeginMonth = t1[1];
					String tmpBeginDay = t1[2];
					String tmpEndMonth = t2[1];
					String tmpEndDay = t2[2];
					staff_week_show = tmpBeginMonth+"月"+tmpBeginDay+"日-"+tmpEndMonth+"月"+tmpEndDay+"日";
					staff_day_show = staff_month+"月"+tmp[2]+"日";
				}
				yearList = bo.getYearList();
				monthList = bo.getMonthList();
				weekList=bo.getWeekList(staff_year, staff_month);
				dayList = bo.getDayList(staff_year, staff_month, staff_week);
				staff_name=(String)this.getFormHM().get("staff_name");
				a0100 = (String)this.getFormHM().get("a0100");
				nbase = (String)this.getFormHM().get("nbase");
			}
			
			
			String[] str = bo.getSql_str(staff_year, staff_month, staff_week, tmp2, staff_name,a0100,nbase);//tmp2是具体的某一天。因为staff_day存储的是year-month-day
			staff_namelist = bo.getStaff_namelist(str[4]);
			this.getFormHM().put("strSelect", str[0]);
			this.getFormHM().put("strWhere", str[1]);
			this.getFormHM().put("strColumns", str[2]);
			this.getFormHM().put("strOrder", str[3]);
			this.getFormHM().put("staff_year", staff_year);
			this.getFormHM().put("staff_month", staff_month);
			this.getFormHM().put("staff_week", staff_week);
			this.getFormHM().put("staff_week_show", staff_week_show);
			this.getFormHM().put("staff_day", staff_day);
			this.getFormHM().put("staff_day_show", staff_day_show);
			this.getFormHM().put("staff_name", staff_name);
			this.getFormHM().put("staff_namelist", staff_namelist);
			this.getFormHM().put("yearList", yearList);
			this.getFormHM().put("monthList", monthList);
			this.getFormHM().put("weekList", weekList);
			this.getFormHM().put("dayList", dayList);
			
			this.getFormHM().put("fromFlag", fromFlag);
			this.getFormHM().put("a0100", a0100);
			this.getFormHM().put("nbase", nbase);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
