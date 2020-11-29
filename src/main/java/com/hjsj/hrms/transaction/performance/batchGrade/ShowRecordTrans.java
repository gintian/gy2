package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ShowRecordTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String plan_id = (String) hm.get("planid");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
		boolean flag = _bo.isPlanIdPriv(plan_id);
		if(!flag){
			return;
		}
		LoadXml loadxml = new LoadXml(this.getFrameconn(), plan_id);
		Hashtable params = loadxml.getDegreeWhole();
		String showDayWeekMonth = (String)params.get("ShowDayWeekMonth");
		String showDay = "";
		String showWeek = "";
		String showMonth = "";
		if(showDayWeekMonth !=null && !"".equals(showDayWeekMonth)) {
			String[] empRecordType = showDayWeekMonth.split(",");
			for(int i=0;i<empRecordType.length;i++){
				if("1".equals(empRecordType[i]))
					showDay = empRecordType[i];
				if("2".equals(empRecordType[i]))
					showWeek = empRecordType[i];
				if("3".equals(empRecordType[i]))
					showMonth = empRecordType[i];
			}
		}
		
		/* 得到某计划某人的考评对象集合 */
		ArrayList objectList = this.getObjectList(this.getFrameconn(),
				this.userView, plan_id);
		ArrayList dataList = new ArrayList();
		HashMap dataMap = new HashMap();
		for (int i = 0; i < objectList.size(); i++) {
			ArrayList temp = getRecordMessage(this.getFrameconn(), (String) objectList.get(i),
					plan_id, showDay, showWeek, showMonth);
			dataMap.put(String.valueOf(i), temp);
		}
		this.getFormHM().put("dataMap", dataMap);
		this.getFormHM().put("showDay", showDay);
		this.getFormHM().put("showWeek", showWeek);
		this.getFormHM().put("showMonth", showMonth);
	}

	// 查询员工日志填报情况
	public ArrayList getRecordMessage(Connection conn, String objectid,
			String planid, String showDay, String showWeek, String showMonth) {
		ArrayList recordList = new ArrayList();
		ArrayList dayList = new ArrayList();
		ArrayList weekList = new ArrayList();
		ArrayList monthList = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;
		StringBuffer str = new StringBuffer();
		str.append("select per_plan.theyear,per_plan.themonth,per_plan.thequarter,per_plan.start_date,per_plan.end_date,per_object.a0101,per_plan.cycle from per_plan ,per_object where per_plan.plan_id=per_object.plan_id and per_object.object_id='"+objectid+"' and per_plan.plan_id=" + planid + " ");
		String cycle = "";// 计划类型0|1|2|3|7 年度|半年|季度|月度|不定期
		String theyear="";
		String themonth="";
		String thequarter="";
		Date start_date = new Date();
		Date end_date = new Date();
		String kh_object = "";
		try {
			rs = dao.search(str.toString());
			while (rs.next()) {
				cycle = rs.getString("cycle");
				theyear = rs.getString("theyear");
				themonth = rs.getString("themonth");
				thequarter = rs.getString("thequarter");
				start_date = rs.getDate("start_date");
				end_date = rs.getDate("end_date");
				kh_object = rs.getString("a0101");
			}
			WeekUtils weekutils = new WeekUtils();
			String startdate = weekutils.numWeekStr(Integer.parseInt(theyear),Integer.parseInt(themonth),1,1);//第一周的第一天
			int num=weekutils.totalWeek(Integer.parseInt(theyear),Integer.parseInt(themonth));//本月有几周
			String enddate = weekutils.numWeekStr(Integer.parseInt(theyear),Integer.parseInt(themonth),num,1);//最后一周的第一天
			str.setLength(0);
			if ("0".equals(cycle))
				str.append("select p01.state,p0115 from p01 left join per_plan on "
						+ Sql_switcher.floatToChar(Sql_switcher.year("p01.p0104"))
						+ " =per_plan.theyear "
						+ " where per_plan.plan_id="
						+ planid + " and p01.a0100='" + objectid + "' ");
			if ("1".equals(cycle)) {
				int halfyear = (Integer.parseInt(thequarter)-1)*6+1;
				str.append("select p01.state,p0115 from p01 left join per_plan on "
						+ Sql_switcher.floatToChar(Sql_switcher.year("p01.p0104"))
						+ "=per_plan.theyear and "
						+ Sql_switcher.month("p01.p0104")
						+ "between "+halfyear+" and "+(halfyear+6)+"  where per_plan.plan_id="
						+ planid + " and p01.a0100='" + objectid + "' ");
			}
			if ("2".equals(cycle)) {
				int quarteryear = (Integer.parseInt(thequarter)-1)*3+1;
				str.append("select p01.state,p0115 from p01 left join per_plan on "
						+ Sql_switcher.floatToChar(Sql_switcher.year("p01.p0104"))
						+ "=per_plan.theyear and "
						+ Sql_switcher.month("p01.p0104")
						+ "between "+quarteryear+" and "+(quarteryear+3)+" where per_plan.plan_id="
						+ planid + " and p01.a0100='" + objectid + "' ");
			}
			if ("3".equals(cycle))
				str.append("select p01.state,p0115 from p01 where p0104 between "+Sql_switcher.dateValue(startdate)+" and "+Sql_switcher.dateValue(enddate)+"  and p01.a0100='" + objectid + "' ");//根据年月获取当月的第一周的第一天和最后一周的第一天   判断p0104在这个时间段的即为本月的周报  zhaoxg 2014-4-19
////						+ Sql_switcher.floatToChar(Sql_switcher.year("p01.p0104"))
////						+ "=per_plan.theyear and "
////						+ Sql_switcher.floatToChar(Sql_switcher.month("p01.p0104"))
////						+ "=per_plan.themonth where per_plan.plan_id="
//						+ planid + " and p01.a0100='" + objectid + "' ");
			if ("7".equals(cycle))
				str.append("select p01.state,p0115 from p01 left join per_plan on "
						+ " p01.p0104 between per_plan.start_date and per_plan.end_date "
						+ " where per_plan.plan_id="
						+ planid + " and p01.a0100='" + objectid + "' ");
			String state = "";// 日志类型0:日报1:周报2:月报
			String p0115 = "";//01，起草 02，报批 03，已批 07，驳回
			rs = dao.search(str.toString());
			int dayRecord[] = new int[4];
			int weekRecord[] = new int[4];
			int monthRecord[] = new int[4];
			while (rs.next()) {
				state = rs.getString("state");
				p0115 = rs.getString("p0115");
				if ("0".equals(state) && "1".equals(showDay)) {
					if("02".equals(p0115)){
						dayRecord[0]++;
					}
					else if("03".equals(p0115)){
						dayRecord[1]++;
					}
					else if("07".equals(p0115)){
						dayRecord[2]++;
					}
				}
				if ("1".equals(state) && "2".equals(showWeek)) {
					if("02".equals(p0115)){
						weekRecord[0]++;
					}
					else if("03".equals(p0115)){
						weekRecord[1]++;
					}
					else if("07".equals(p0115)){
						weekRecord[2]++;
					}
				}
				if ("2".equals(state) && "3".equals(showMonth)) {
					if("02".equals(p0115)){
						monthRecord[0]++;
					}
					else if("03".equals(p0115)){
						monthRecord[1]++;
					}
					else if("07".equals(p0115)){
						monthRecord[2]++;
					}
				}
			}

			boolean flag = isYunNian(Integer.parseInt(theyear));
			Calendar calendar = Calendar.getInstance();
			if ("0".equals(cycle)){//年度计划
				int day_year=365;
				if(flag)
					day_year=366;
				int numweek = 0;
				for(int i=1;i<13;i++){
					numweek += weekutils.totalWeek(Integer.parseInt(theyear),i);
				}
				//int week_year = day_year/7;
				//week_year = day_year%7 > 0 ? week_year+1 :week_year;
				if("1".equals(showDay))
					dayRecord[3] = day_year-dayRecord[0]-dayRecord[1]-dayRecord[2];
				if("2".equals(showWeek))
					weekRecord[3]=numweek-weekRecord[0]-weekRecord[1]-weekRecord[2];
				if("3".equals(showMonth))
					monthRecord[3]=12-monthRecord[0]-monthRecord[1]-monthRecord[2];
			}
			if ("1".equals(cycle)){
				int day_halfyear=184;
				if(!flag && "1".equals(thequarter))
					day_halfyear=181;
				else if(flag && "1".equals(thequarter))
					day_halfyear=182;
				int numweek = 0;
				if("1".equals(thequarter)) {
					for(int i=1;i<7;i++){
						numweek += weekutils.totalWeek(Integer.parseInt(theyear),i);
					}
				}
				else {
					for(int i=7;i<13;i++){
						numweek += weekutils.totalWeek(Integer.parseInt(theyear),i);
					}
				}
				//int week_halfyear = day_halfyear/7;
				//week_halfyear = day_halfyear%7 > 0 ? week_halfyear+1 :week_halfyear;
				if("1".equals(showDay))
					dayRecord[3] = day_halfyear-dayRecord[0]-dayRecord[1]-dayRecord[2];
				if("2".equals(showWeek))
					weekRecord[3]=numweek-weekRecord[0]-weekRecord[1]-weekRecord[2];
				if("3".equals(showMonth))
					monthRecord[3]=6-monthRecord[0]-monthRecord[1]-monthRecord[2];
			}
			if ("2".equals(cycle)){
				int day_quarteryear=92;
				if(!flag && "01".equals(thequarter))
					day_quarteryear=90;
				else if((flag && "01".equals(thequarter)) || "02".equals(thequarter))
					day_quarteryear=91;
				int numweek = 0;
				if("01".equals(thequarter)) {
					for(int i=1;i<4;i++){
						numweek += weekutils.totalWeek(Integer.parseInt(theyear),i);
					}
				}
				else if("02".equals(thequarter)) {
					for(int i=4;i<7;i++){
						numweek += weekutils.totalWeek(Integer.parseInt(theyear),i);
					}
				}
				else if("03".equals(thequarter)) {
					for(int i=7;i<10;i++){
						numweek += weekutils.totalWeek(Integer.parseInt(theyear),i);
					}
				}
				else if("04".equals(thequarter)) {
					for(int i=10;i<13;i++){
						numweek += weekutils.totalWeek(Integer.parseInt(theyear),i);
					}
				}
				//int week_quarteryear = day_quarteryear/7;
				//week_quarteryear = day_quarteryear%7 > 0 ? week_quarteryear+1 :week_quarteryear;
				if("1".equals(showDay))
					dayRecord[3] = day_quarteryear-dayRecord[0]-dayRecord[1]-dayRecord[2];
				if("2".equals(showWeek))
					weekRecord[3]=numweek-weekRecord[0]-weekRecord[1]-weekRecord[2];
				if("3".equals(showMonth))
					monthRecord[3]=3-monthRecord[0]-monthRecord[1]-monthRecord[2];
			}
			if ("3".equals(cycle)){
				calendar.set(Calendar.YEAR, Integer.parseInt(theyear));  
				calendar.set(Calendar.MONTH, Integer.parseInt(themonth)-1);
				int day_month = calendar.getActualMaximum(Calendar.DATE);
				//System.out.println("天数：" + calendar.getActualMaximum(Calendar.DAY_OF_YEAR));  
				//System.out.println("天数：" + calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  
		        //System.out.println("周数：" + calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));
				int numweek = 0;
				numweek += weekutils.totalWeek(Integer.parseInt(theyear),Integer.parseInt(themonth));
				if("1".equals(showDay))
					dayRecord[3] = day_month-dayRecord[0]-dayRecord[1]-dayRecord[2];
				if("2".equals(showWeek))
					weekRecord[3]=numweek-weekRecord[0]-weekRecord[1]-weekRecord[2];
				if("3".equals(showMonth))
					monthRecord[3]=1-monthRecord[0]-monthRecord[1]-monthRecord[2];
			}
			if ("7".equals(cycle)){
				long start = start_date.getTime();
				long end = end_date.getTime();
				int day = (int) ((end-start)/(1000*60*60*24));
				int week = day/7;
				week = day%7 > 0 ? week+1 :week;
				if("1".equals(showDay))
					dayRecord[3] = day+1-dayRecord[0]-dayRecord[1]-dayRecord[2];
				if("2".equals(showWeek))
					weekRecord[3]=week-weekRecord[0]-weekRecord[1]-weekRecord[2];
				if("3".equals(showMonth))
					monthRecord[3]=(end_date.getYear()-start_date.getYear())*12+end_date.getMonth()-start_date.getMonth()+1-monthRecord[0]-monthRecord[1]-monthRecord[2];
			}
			HashMap temp = null;
			for(int i=0;i<4;i++){
				temp = new HashMap();
				temp.put(String.valueOf(i), zeroToNull(dayRecord[i]));
				dayList.add(temp);
			}
			for(int i=0;i<4;i++){
				temp = new HashMap();
				temp.put(String.valueOf(i), zeroToNull(weekRecord[i]));
				weekList.add(temp);
			}
			for(int i=0;i<4;i++){
				temp = new HashMap();
				temp.put(String.valueOf(i), zeroToNull(monthRecord[i]));
				monthList.add(temp);
			}
			recordList.add(kh_object);
			recordList.add(dayList);
			recordList.add(weekList);
			recordList.add(monthList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return recordList;
	}

	public ArrayList getObjectList(Connection conn, UserView userView,
			String planid) {
		ArrayList objectList = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer str = new StringBuffer("");
		str.append(" select pm.object_id from per_mainbody pm,per_object po,per_plan pp where pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=pp.plan_id and pp.plan_id="
				+ planid + " and pm.mainbody_id='" + userView.getA0100() + "' ");
		ResultSet rs = null;
		try {
			rs = dao.search(str.toString());
			while (rs.next()) {
				objectList.add(rs.getString("object_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return objectList;
	}
	
	public String zeroToNull(int num){
		if(num==0)
			return "";
		else
			return num+"";
	}
	
	public boolean isYunNian(int year){
		if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0){
			return true;
		} else {
			return false;
		}
	}
}
