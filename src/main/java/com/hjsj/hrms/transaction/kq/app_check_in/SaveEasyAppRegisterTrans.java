package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;

public class SaveEasyAppRegisterTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {

		String app_way = (String) this.formHM.get("app_way"); // 选择天或小时的 标志
		String app_type = (String) this.formHM.get("app_type"); // 选择的请假或加班类型
		String start_time_h = (String) this.formHM // 开始时间的 小时数
				.get("start_time_h");
		String start_time_m = (String) this.formHM // 开始时间的 分钟数
				.get("start_time_m");
		String start_date = (String) this.formHM.get("start_d"); // 开始日期
		List arrPer = (List) this.formHM.get("arrPer");// 用户信息列表
		String table = (String) this.formHM.get("table");// 表名
		String class_id = (String) this.formHM.get("class_id"); // 选择的班次类型
		String sub_flag = (String) this.formHM.get("sub_flag");
		String app_reas = (String) this.formHM.get("app_reas");
		this.getFormHM().remove("app_reas");//防止事由含有回车符，去除
		
		String appReaCode = "";
		String appReaField = "";
		String appReaCodesetid = (String) this.getFormHM().get("appReaCodesetid");
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
		if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) 
		{
			appReaCode = (String)this.getFormHM().get("appReaCode");
			appReaField = kqUtilsClass.getAppReaField(new ContentDAO(frameconn)).toLowerCase();
		}
		
		
		String end_date = (String) this.formHM.get("end_d");
		String scope_start_time = (String) this.getFormHM().get(
				"scope_start_time");
		String scope_end_time = (String) this.getFormHM().get("scope_end_time");
		GetValiateEndDate ve = new GetValiateEndDate(this.userView,
				this.frameconn);
		ArrayList list = new ArrayList();
		for (int i = 0; i < arrPer.size(); i++) {
			String infoStr = (String) arrPer.get(i);
			String nbase = infoStr.substring(0, infoStr.length() - 8);
			String a0100 = infoStr.substring(infoStr.length() - 8);
			list.add(ve.getInfoMap(nbase, a0100));
		}
		String ta = table.toLowerCase();

		if (class_id != null && "#".equals(class_id)) {
			class_id = null;
		}
		if (!"08".equals(sub_flag) && !"03".equals(sub_flag))
			sub_flag = "02";

		Date startDate = null;
		Date endDate = null;
		if ("1".equals(app_way)) {
			startDate = OperateDate.strToDate(start_date + " " + start_time_h
					+ ":" + start_time_m, "yyyy-MM-dd HH:mm");
			endDate = OperateDate.strToDate(end_date, "yyyy-MM-dd HH:mm");
		} else if ("0".equals(app_way)) {
			if ("Q11".equalsIgnoreCase(table)) {
				Map timeSet = kqUtilsClass.getTimeAreaInclassById(class_id); // 根据班次id号获得班次的开始时间和结束时间
				if ("0".equals(DataDictionary.getFieldItem("q1104").getState())) {
					timeSet = new HashMap();
					timeSet.put("startTime", "00:00");
					timeSet.put("endTime", "23:59");
				}
				startDate = OperateDate.strToDate(start_date + " "
						+ timeSet.get("startTime"), "yyyy-MM-dd HH:mm");
			} else if ("Q13".equalsIgnoreCase(table) && class_id != null && !"".equals(class_id)) {
				Map timeSet = kqUtilsClass.getTimeAreaInclassById(class_id); // 根据班次id号获得班次的开始时间和结束时间
				startDate = OperateDate.strToDate(start_date + " "
						+ timeSet.get("startTime"), "yyyy-MM-dd HH:mm");
			} else {
				Map infoMap = (Map) list.get(0);
				startDate = OperateDate.strToDate(start_date + " 00:00",
						"yyyy-MM-dd HH:mm");
				Map data = ve.getTimeByDate((String) infoMap.get("nbase"),
						(String) infoMap.get("a0100"), startDate);
				if (!data.isEmpty()) {
					startDate = OperateDate.strToDate(start_date + " "
							+ data.get("startTime"), "yyyy-MM-dd HH:mm");
				}
			}
			endDate = OperateDate.strToDate(end_date, "yyyy-MM-dd HH:mm");
		} else if ("2".equals(app_way)) {
			startDate = OperateDate.strToDate(scope_start_time,
					"yyyy-MM-dd HH:mm");
			endDate = OperateDate.strToDate(scope_end_time, "yyyy-MM-dd HH:mm");
		}
		ArrayList infoList = new ArrayList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Map infoMap = (Map) it.next();
			RecordVo vo = new RecordVo(table);
			if("q11".equals(ta)){
				DbWizard dbw = new DbWizard(this.frameconn);
				if (dbw.isExistField("Q11", "flag", false)) 
					vo.setString("flag", "0");//加班申请数据来源
			}
			vo.setString("nbase", (String) infoMap.get("nbase")); // 应用库前缀
			vo.setString("a0100", (String) infoMap.get("a0100")); // 人员编号
			vo.setString("b0110", (String) infoMap.get("b0110")); // 单位编码
			vo.setString("e0122", (String) infoMap.get("e0122")); // 部门编码
			vo.setString("a0101", (String) infoMap.get("a0101")); // 姓名
			vo.setString("e01a1", (String) infoMap.get("e01a1")); // 职务编码
			vo.setDate(ta + "z1", startDate); // 开始日期
			vo.setDate(ta + "z3", endDate); // 结束日期
			vo.setDate(ta + "05", OperateDate.getDateByFormat(new Date(),
					"yyyy-MM-dd HH:mm")); // 申请日期
			vo.setString(ta + "07", app_reas); // 加班事由
			vo.setString(ta + "03", app_type); // 加班类型
			
			if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) 
				vo.setString(appReaField, appReaCode);
			
			if ("02".equals(sub_flag)) {
				vo.setString(ta + "09", this.userView.getUserFullName());
			} else if ("03".equals(sub_flag)) {
				vo.setString(ta + "13", this.userView.getUserFullName());
			}

			vo.setString(ta + "01", checkAppkeyid(table));
			if (sub_flag != null && "03".equals(sub_flag)) {
				vo.setString(ta + "z0", "01");
				vo.setString(ta + "z5", sub_flag);
			} else {
				vo.setString(ta + "z0", "03");
				vo.setString(ta + "z5", sub_flag);
			}
			if ("q15".equalsIgnoreCase(ta)) {
				vo.setString("q1517", "0");
				if ("01".equals(vo.getString("q15z0"))
						&& "03".equals(vo.getString("q15z5"))) {
					float d_Count = 0;
					AnnualApply annualApply = new AnnualApply(this.userView,
							this.getFrameconn());
					HashMap kqItem_hash = annualApply.count_Leave(vo
							.getString("q1503"));					
					if (KqParam.getInstance().isHoliday(this.frameconn, vo.getString("b0110"), app_type)) {
					    //年假都按天算
					    kqItem_hash.put("item_unit", KqConstant.Unit.DAY);
					    
						float[] holiday_rules = annualApply.getHoliday_minus_rule();//年假假期规则
						d_Count = annualApply.getHistoryLeaveTime(vo.getDate("q15z1"), 
						        vo.getDate("q15z3"), vo.getString("a0100"), vo.getString("nbase"),
								(String) vo.getString("b0110"), kqItem_hash,holiday_rules);						
						String history = annualApply.upLeaveManage(vo.getString("a0100"), vo.getString("nbase"), 
						        vo.getString("q1503"), 
						        OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm"), 
						        OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm"),
								d_Count, "1", (String) vo.getString("b0110"),
								kqItem_hash,holiday_rules);
						vo.setString("history", history);
					}
				}
				/** 审批请假单时 如果是调休假 更新调休明细表Q33*/
				String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                if("01".equals(vo.getString("q15z0"))
                        && "03".equals(vo.getString("q15z5")) &&
                        app_type.equalsIgnoreCase(leavetime_type_used_overtime)){
                    int timeCount = 0;
                	//考勤规则应取改假类自己的规则
                	AnnualApply annualApply = new AnnualApply(userView, frameconn);
                    HashMap kqItemHash = annualApply.count_Leave(vo.getString("q1503"));
                    kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
                    //假期时长扣减规则参数
                    float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
                    if (KqParam.getInstance().isHoliday(frameconn, vo.getString("b0110"), vo.getString("q1503")))
                        holidayRules = annualApply.getHoliday_minus_rule();
                    
                    float timeLen = annualApply.calcLeaveAppTimeLen(vo.getString("nbase"), vo.getString("a0100"), "", vo.getDate("q15z1"), vo.getDate("q15z3"), kqItemHash, holidayRules, Integer.MAX_VALUE);
                    
                    timeCount = (int)(timeLen * 60);
                    if(timeCount > 0) {
                    	UpdateQ33 updateq33 = new UpdateQ33(this.userView,this.frameconn);
                    	// 48612
                    	updateq33.setStartDate(vo.getDate("q15z1"));
                    	updateq33.upQ33(vo.getString("nbase"),vo.getString("a0100"),timeCount);
                    }
                }
			} else if ("q11".equalsIgnoreCase(ta) || "q13".equalsIgnoreCase(ta)) {
				vo.setString(ta + "04", class_id);// 参考班次
				if(table!=null&& "q11".equalsIgnoreCase(table))
		        {	
					//szk申请时长
	                String applytime = KqUtilsClass.getFieldByDesc("q11", ResourceFactory.getProperty("kq.class.applytime"));
	                if ( applytime!= null && applytime.trim().length() > 0 && "q11z4".equalsIgnoreCase(applytime)){
	                	AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
		                HashMap kqItemHash = annualApply.count_Leave(vo.getString("q1103"));    
		                float timeLen=0;
		                if (class_id!=null&&!"".equals(class_id) && !"0".equals(class_id))
						{
		                     HashMap classMap = (HashMap) kqUtilsClass.getTimeAreaInclassById(class_id);
		                     timeLen = new Float((String)classMap.get("work_hours")).floatValue();
		                     timeLen = annualApply.roundNumByItemDecimalWidth(kqItemHash, timeLen/60);
						}
		                else {
		                	 timeLen = annualApply.calcOverAppTimeLen(vo.getString("nbase"), (String) infoMap.get("a0100"), startDate, endDate, kqItemHash,  Integer.MAX_VALUE);
		                	 timeLen = annualApply.roundNumByItemDecimalWidth(kqItemHash,timeLen);
		                }
		                vo.setDouble(table.toLowerCase() + "z4", timeLen);// 申请时长
	                }
					
					//是否有扣除休息时间
		        	String dert_itemid=(String)this.getFormHM().get("dert_itemid");
		        	String dert_value=(String)this.getFormHM().get("dert_value");
		        	dert_value = dert_value!=null&&dert_value.length()>0?dert_value:"0";
		        	if("q11".equalsIgnoreCase(table)&&dert_itemid!=null&&dert_itemid.length()>0)
			        	vo.setString(dert_itemid,dert_value);//是否有扣除休息时间
		        	
		        	String iftoRest = (String) this.getFormHM().get("IftoRest");
		        	String iftoRestField = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
		        	String error = "";
	        		AnnualApply annualApply = new AnnualApply(userView, frameconn);
		        	if (iftoRestField != null && iftoRestField.length() > 0) 
					{
		        		vo.setString(iftoRestField, iftoRest);
		        		
		        		if ("1".equals(iftoRest)) 
		        		{
		        			error = annualApply.CheckAppTypeIsToLeave(vo.getString("q1103"));
		        			if (error.length() > 0) 
							{
								throw new GeneralException(error);
							}
		        		}
					}
		        	// 加班最大限额控制
		        	error = annualApply.checkOverTimelenMorethanLimit(vo, "1");
	                if (error.length() > 0) {
	                        throw new GeneralException(error);
	                }
	                // 增加检查调休加班限额控制
		            KqOverTimeForLeaveBo overTimeForLeaveBo = new KqOverTimeForLeaveBo(frameconn, userView);
		            error = overTimeForLeaveBo.checkOvertimeForLeaveMaxHour(vo);
		            if(!StringUtils.isEmpty(error)) {
		            	this.getFormHM().put("reflag", error);
		            	return;
		            }
		        }
				
			}
			if ("03".equals(sub_flag)) {
				vo.setDate(ta + "z7", OperateDate.getDateByFormat(new Date(),
						"yyyy-MM-dd HH:mm:ss"));// 审批时间
			}
			infoList.add(vo);

		}
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.addValueObject(infoList);
		} catch (SQLException e) {
			this.getFormHM().put("reflag", "lost");
			throw new GeneralException(e.getMessage());
		}
		this.getFormHM().put("reflag", "ok");
	}

	private String checkAppkeyid(String table) {
		IDGenerator idg = new IDGenerator(2, this.frameconn);
		String insertid = "";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			boolean iscorrect = false;
			while (!iscorrect) {
				insertid = idg
						.getId((table + "." + table + "01").toUpperCase());
				iscorrect = checkAppkeyid2(table, insertid, dao);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return insertid;
	}

	private boolean checkAppkeyid2(String table, String id, ContentDAO dao) {
		boolean iscorrect = true;
		RowSet rs = null;
		try {
			String sql = "select 1 from " + table + " where " + table + "01='"
					+ id + "'";
			rs = dao.search(sql);
			if (rs.next())
				iscorrect = false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return iscorrect;
	}

}
