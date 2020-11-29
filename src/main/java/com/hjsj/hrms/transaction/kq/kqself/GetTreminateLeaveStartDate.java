package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetTreminateLeaveStartDate extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String tableflag =  this.getFormHM().get("tableflag")==null ? "" : (String) this.getFormHM().get("tableflag");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
		// 若为空或没有传假单标识，暂时认为是请假自助单过来的销假单
    	if(StringUtils.isEmpty(tableflag))
    		tableflag = ((String)hm.get("table")).toLowerCase();
    	// 若为空或没有传假单标识，暂时认为是请假自助单过来的销假单
    	if(StringUtils.isEmpty(tableflag))
    		tableflag = "q15";
		
		String q1501 = (String) this.getFormHM().get("q1501");
		// =0天  =1小时  =2区间
		String app_way = (String) this.getFormHM().get("app_way");
		String q1503 = (String) this.getFormHM().get("q1503");
		String date_count = null;
		String time_count = null;
		String err = "";
		if ("0".equals(app_way)) {
			date_count = (String) this.getFormHM().get("date_count");
		} else if ("1".equals(app_way)) {
			time_count = (String) this.getFormHM().get("time_count");
		} else if ("2".equals(app_way)) {
			time_count = (String) this.getFormHM().get("time_count");
			time_count = (String) this.getFormHM().get("time_count");
		}
		ContentDAO dao = new ContentDAO(this.frameconn);
		KqUtilsClass kq = new KqUtilsClass(this.frameconn);
		RowSet rs = null;
		AnnualApply ann = new AnnualApply(this.userView, this.frameconn);
		GetValiateEndDate va = new GetValiateEndDate(this.userView,
				this.frameconn);
		Date startDate = new Date();
		Date endDate = new Date();
		Map map = KqUtilsClass.getCurrKqInfo();
		Date kq_start = OperateDate.strToDate((String) map.get("kq_start"),
				"yyyy.MM.dd");
		try {
//			String sql = "SELECT q15z1,q15z3,nbase,a0100 FROM Q15 WHERE Q1501='"+ q1501 + "'";
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT ").append(tableflag).append("Z1,").append(tableflag).append("Z3,").append("nbase,a0100 ");
			sql.append(" FROM ").append(tableflag);
			sql.append(" WHERE ").append(tableflag).append("01=?");
			
			ArrayList list01 = new ArrayList();
			list01.add(q1501);
			
			rs = dao.search(sql.toString(), list01);
			if (rs.next()) {
				Date z1 = rs.getTimestamp(tableflag+"Z1");
				if (kq_start.after(z1)) {
					z1 = kq_start;
				}
				Date z3 = rs.getTimestamp(tableflag+"Z3");

				String nbase = rs.getString("nbase");
				String A0100 = rs.getString("a0100");
				if ("1".equals(app_way)) {
					endDate = z3;
					startDate = OperateDate.addHour(endDate, -Float.parseFloat(time_count));
				} else if ("0".equals(app_way)) {
					endDate = z3;
					String class_id = getClass_idBy(nbase,A0100,z3);
					HashMap kqItem_hash = ann.count_Leave(q1503);
					// 只有请假公出需要校验是否包含节假日公休日
					if(("q15".equalsIgnoreCase(tableflag) || "q13".equalsIgnoreCase(tableflag)) 
							&& ("".equals(class_id) || "0".equals(class_id))
							&& (!"1".equals(kqItem_hash.get("has_rest"))|| !"1".equals(kqItem_hash.get("has_feast")))){
						endDate = OperateDate.addDay(endDate, -1);
					}else if(!"".equals(class_id) && !"0".equals(class_id)){
						String onduty = kq.getStartTimeclassById(class_id);
						if(!"".equals(onduty)){
							Date d1 = OperateDate.strToDate(OperateDate.dateToStr(endDate, "yyyy-MM-dd") + " " + onduty, "yyyy-MM-dd HH:mm");
							if(!d1.before(endDate)){
								endDate = OperateDate.addDay(endDate, -1);
							}
						}
					}
					
					int d_count = Integer.parseInt(date_count);
					// 天数处理
					if (d_count >= 1) {
						d_count--;
					} else if (d_count < 1) {
						d_count = 0;
					}
					startDate = OperateDate.addDay(endDate, -d_count);
				} else {
					startDate = OperateDate.strToDate((String)this.getFormHM().get("z1"), "yyyy-MM-dd HH:mm");
					endDate =  OperateDate.strToDate((String)this.getFormHM().get("z3"), "yyyy-MM-dd HH:mm");
					if(startDate.after(endDate)){
						throw new GeneralException("开始时间不能大于结束时间！");
					}
				}
				if (!"2".equals(app_way)) {
					HashMap kqItem_hash = ann.count_Leave(q1503);
					ArrayList list = new ArrayList();// 申请的日期
					ArrayList reDay = new ArrayList();
					if ("1".equals(kqItem_hash.get("has_rest"))&& "1".equals(kqItem_hash.get("has_feast"))) {
						
					} else if ("1".equals(kqItem_hash.get("has_rest"))) {
						int num = 1;
						while (num > 0) {
							list = OperateDate.getDayByDate(startDate, endDate);
							// 只有 销 请假单据时需校验是否包含休息班次
							if("q15".equalsIgnoreCase(tableflag))
								reDay = va.getWeekDateArea(list, nbase, A0100);
							num = 0;
							for (int i = 0; i < reDay.size(); i++) {
								if (va.isFeast((Date) reDay.get(i))) {
									num++;
								}
							}
							endDate = OperateDate.addDay(startDate, -1);
							startDate = OperateDate.addDay(startDate, -num);
						}
					} else if ("1".equals(kqItem_hash.get("has_feast"))) {
						int num = 1;
						while (num > 0) {
							list = OperateDate.getDayByDate(startDate, endDate);
							if("q15".equalsIgnoreCase(tableflag))
								reDay = va.getWeekDateArea(list, nbase, A0100);
							num = 0;
							for (int i = 0; i < reDay.size(); i++) {
								if (!va.isFeast((Date) reDay.get(i))) {
									num++;
								}
							}
							endDate = OperateDate.addDay(startDate, -1);
							startDate = OperateDate.addDay(startDate, -num);
						}
					} else {
						int num = 1;
						while (num > 0) {
							list = OperateDate.getDayByDate(startDate, endDate);
							if("q15".equalsIgnoreCase(tableflag))
								reDay = va.getWeekDateArea(list, nbase, A0100);
							num = reDay.size();
							endDate = OperateDate.addDay(startDate, -1);
							startDate = OperateDate.addDay(startDate, -num);
						}
					}
					endDate = z3;
					String class_id = getClass_idBy(nbase,A0100,startDate);
					if (!"1".equals(app_way)) {
						if (class_id== "" && "0".equals(class_id))
						{
							startDate = OperateDate.strToDate(OperateDate.dateToStr(startDate, "yyyy-MM-dd")+ " 00:00",
									"yyyy-MM-dd HH:mm");
						}
						else {
							Map timeMap = va.getTimeByDate(nbase, A0100, startDate);
							if (!(timeMap == null) && !"".equals(timeMap)
									&& !timeMap.isEmpty()) {
								startDate = OperateDate.strToDate(OperateDate
										.dateToStr(startDate, "yyyy-MM-dd")
										+ " " + (String) timeMap.get("startTime"),
										"yyyy-MM-dd HH:mm");
							} else {
								startDate = OperateDate.strToDate(OperateDate
										.dateToStr(startDate, "yyyy-MM-dd")
										+ " 00:00", "yyyy-MM-dd HH:mm");
							}
						}
					}
				}
				String reMsg = (String)this.getFormHM().get("reMsg");
				if(!"1".equals(reMsg)){
					/** 验证开始时间是否超出可申请范围 * */
					if (startDate.before(z1) || endDate.after(z3)) {
						// throw new GeneralException("超出了可销假范围，请重新申请！");
						err = "超出了可销假范围，请重新申请！";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException(e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		this.getFormHM().put("app_way", app_way);
		this.getFormHM().put("err", err);
		this.getFormHM().put("z1",
				OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm"));
		this.getFormHM().put("z3",
				OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm"));
	}
	
	public String getClass_idBy(String nbase,String A0100,Date d1){
		String sql = "SELECT class_id FROM kq_employ_shift WHERE nbase='" + nbase + "' AND A0100='" + A0100 + "' AND q03z0='" + OperateDate.dateToStr(d1, "yyyy.MM.dd") +"'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		String class_id = "";
		try {
			rs = dao.search(sql);
			if(rs.next()){
				class_id = rs.getString("class_id") != null? rs.getString("class_id"):"";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return class_id;
	}
}
