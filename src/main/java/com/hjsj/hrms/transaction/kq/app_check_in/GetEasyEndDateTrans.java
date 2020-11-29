package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.app_check_in.ViewAllApp;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.*;

public class GetEasyEndDateTrans extends IBusiness {
	private static final long serialVersionUID = 1L;

	private String rest_overtime_time = "0";

	private String err_message = "";// 错误信息

	private String pro_message = "";

	private boolean coDay_flag = false;// 选择的班次是否 跨天

	public void execute() throws GeneralException {
		String app_way = (String) this.formHM.get("app_way"); // 选择天或小时的 标志
		String date_count = (String) this.formHM.get("date_count"); // 天数整数位
		String app_type = (String) this.formHM.get("app_type"); // 选择的请假或加班类型
        // 开始时间的 小时数
        String start_time_h = (String) this.formHM.get("start_time_h");
        // 开始时间的 分钟数
        String start_time_m = (String) this.formHM.get("start_time_m");
		String start_date = (String) this.formHM.get("start_d"); // 开始日期
		String hr_count = (String) this.formHM.get("hr_count"); // 小时数整数位
		List arrPer = (List) this.formHM.get("arrPer");//用户信息列表
		String table = (String) this.formHM.get("table");// 表名
		try{
			// KqUtilsClass 常用考勤工具类
			KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
			// 判断节假日 可以申请 一次 还是多次加班
			this.rest_overtime_time = KqParam.getInstance().getRestOvertimeTimes();
			float times =0;
			Date startDate = null;
			Date endDate = null; // 申请日期型日期
	
			ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.frameconn);
			GetValiateEndDate ve = new GetValiateEndDate(this.userView,	this.frameconn);
			boolean is_overtime_op = validateAppOper.is_OVERTIME_TYPE();
	
			ArrayList infolist = new ArrayList();
			if (!arrPer.isEmpty()) {
				for (int i = 0; i < arrPer.size(); i++) {
					String infoStr = (String) arrPer.get(i);
					String nbase = infoStr.substring(0, 3);
					String a0100 = infoStr.substring(3);
					infolist.add(ve.getInfoMap(nbase, a0100));
				}
			} else {
				this.err_message = "申请人员列表为空，请添加申请人员！";
				this.formHM.put("err_message", this.err_message);
				return;
			}
			// 判断是否选择了班次 如果没有选择（“#”） 则设置成为默认班次
			// if (mess1.equals("#")) {
			// mess1 = kqUtilsClass.getKqParameterContent("DEFAULT_REST_KQCLASS",
			// "UN");
			// }
			Map timeSet = new HashMap();
			String class_id = null;
			if ("Q11".equalsIgnoreCase(table)) {
				class_id = (String) this.formHM.get("class_id"); // 选择的班次类型
				if (class_id == null || "1".equals(app_way))
					class_id = "#";
				if ("0".equals(DataDictionary.getFieldItem("q1104").getState())) {
					timeSet.put("startTime", "00:00");
					timeSet.put("endTime", "23:59");
				}
				if (!"#".equals(class_id)) {
					timeSet = kqUtilsClass.getTimeAreaInclassById(class_id); // 根据班次id号获得班次的开始时间和结束时间
					/** 判断工作时间是否跨天 */
					if (((Boolean) timeSet.get("conday")).booleanValue()) {
						this.coDay_flag = true;
					}
				}
			}
			// ------------------------------计算结束日期开始---------------------------------------
			String count = "";
			if ("Q11".equalsIgnoreCase(table)) {// 加班申请
				
				/** 判断 选择的是按照 ‘天’app_way=0 还是 ‘小时’app_way=1 计算 */
				if ("0".equals(app_way)) {
				    // 得到申请的开始时间
					startDate = OperateDate.strToDate(start_date + " " + (String) timeSet.get("startTime"),	"yyyy-MM-dd HH:mm");
					endDate = OperateDate.strToDate(start_date + " " + timeSet.get("endTime"), "yyyy-MM-dd HH:mm");
					count = date_count;
					/** 获得结束时间 根据开始日期、天数 和 班次的开始时间和结束时间 */
					endDate = ve.getEndTimetoQ11(startDate, endDate, count,	this.coDay_flag, app_way, class_id);
				} else if ("1".equals(app_way)) {// 按照小时计算
					startDate = OperateDate.strToDate(start_date + " " + start_time_h + ":"	+ start_time_m, "yyyy-MM-dd HH:mm");// 开始时间
					// if (mess1.equals("#")) {
					endDate = startDate;
					// } else {
					// endDate = OperateDate.strToDate(start_date + " "
					// + timeSet.get("endTime"), "yyyy-MM-dd HH:mm");
					// }
	
					count = hr_count;
					/** 获得结束时间 根据开始日期、天数 和 班次的开始时间和结束时间 */
					endDate = ve.getEndTimetoQ11(startDate, endDate, count,
							this.coDay_flag, app_way, class_id);
				}
			} else if ("Q13".equalsIgnoreCase(table)) {// 公出申请
				Map infoMap = (Map) infolist.get(0);
				String nbase = (String) infoMap.get("nbase");
				if ("0".equals(app_way)) {
					startDate = OperateDate.strToDate(start_date + " 00:00",
							"yyyy-MM-dd HH:mm");
					Map data = ve.getTimeByDate(nbase, (String) infoMap
							.get("a0100"), startDate);
					if (!data.isEmpty()) {
						startDate = OperateDate.strToDate(start_date + " "
								+ data.get("startTime"), "yyyy-MM-dd HH:mm");
					}
					count = date_count;
					endDate = ve.getEndTimeToQ13(startDate, count, app_way,
							app_type, nbase,
							(String) infoMap.get("a0100"));
				} else if ("1".equals(app_way)) {
					startDate = OperateDate
							.strToDate(start_date + " " + start_time_h + ":"
									+ start_time_m, "yyyy-MM-dd HH:mm");// 开始时间
					count = hr_count;
					endDate = ve.getEndTimeToQ13(startDate, count, app_way,
							app_type, nbase,
							(String) infoMap.get("a0100"));
				}
			} else if ("Q15".equalsIgnoreCase(table)) {// 请假申请
				if (!infolist.isEmpty()) {
					Map infoMap = (Map) infolist.get(0);
					String nbase = (String) infoMap.get("nbase");
					if ("0".equals(app_way)) {
						startDate = OperateDate.strToDate(start_date + " 00:00",
								"yyyy-MM-dd HH:mm");
						Map data = ve.getTimeByDate(nbase, (String) infoMap
								.get("a0100"), startDate);
						if (!data.isEmpty()) {
							startDate = OperateDate.strToDate(start_date + " "
									+ data.get("startTime"), "yyyy-MM-dd HH:mm");
						}
						count = date_count;
						endDate = ve.getEndTimeToQ15(startDate, count, app_way,
								app_type, nbase, (String) infoMap.get("a0100"));
					} else if ("1".equals(app_way)) {
						startDate = OperateDate.strToDate(start_date + " "
								+ start_time_h + ":" + start_time_m,
								"yyyy-MM-dd HH:mm");// 开始时间
						count = hr_count;
						endDate = ve.getEndTimeToQ15(startDate, count, app_way,
								app_type, nbase, (String) infoMap.get("a0100"));
					}
				}
			}
			if ("2".equals(app_way)) {
				String scope_start_time = (String) this.getFormHM().get(
						"scope_start_time");
				String scope_end_time = (String) this.getFormHM().get(
						"scope_end_time");
				startDate = OperateDate.strToDate(scope_start_time,
						"yyyy-MM-dd HH:mm");
				endDate = OperateDate.strToDate(scope_end_time, "yyyy-MM-dd HH:mm");
				if (startDate.after(endDate) || startDate.equals(endDate)) {
					this.err_message = "申请日期错误，起始日期大于或等于结束日期！";
					this.formHM.put("err_message", this.err_message);
					return;
				}
			}
			this.formHM.put("startDate", OperateDate.dateToStr(startDate,
					"yyyy-MM-dd HH:mm"));
			this.formHM.put("endDate", OperateDate.dateToStr(endDate,
					"yyyy-MM-dd HH:mm"));
			AnnualApply annualApply = new AnnualApply(this.userView,this.frameconn);
			if(!annualApply.isSessionSearl(startDate,endDate))
	    	    throw GeneralExceptionHandler.Handle(new GeneralException("","该考勤期间已封存或不存在，不能做该申请操作！","",""));
			
	        Map infoMap1 = (Map) infolist.get(0);
			if(!annualApply.getKqDataState((String) infoMap1.get("nbase"),(String) infoMap1.get("a0100"),startDate,endDate))
	        {
	            throw GeneralExceptionHandler.Handle(new GeneralException("",(String) infoMap1.get("a0101")+"申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！","",""));
	        }    
			
			//reflag 0:界面中选择申请类型等ajax操作 1：申请表单提交操作（报审、报批、批准等）
			String reflag = (String) this.getFormHM().get("sub_flag");
			if (reflag == null || "0".equals(reflag) || reflag.length() < 1) {
				return;
			}
	
			/** 检查所选人员排班是否一致 */
			if (!infolist.isEmpty()) {
				Map infoMap = (Map) infolist.get(0);
				String nbase = (String) infoMap.get("nbase");
				ArrayList list = OperateDate.getDayByDate(startDate, endDate);
				if (!ve.isEvenToClass(infolist, nbase, list)) {
					this.pro_message = "所选人员不在同一个班次，所选日期可能出现误差！";
					this.formHM.put("pro_message", this.pro_message);
				}
			}
			this.formHM.put("endDate", OperateDate.dateToStr(endDate,
					"yyyy-MM-dd HH:mm"));
			// ------------------------------计算结束日期结束---------------------------------------
			// --------------------------------日期验证开始----------------------------------------
	//		if(!kqUtilsClass.isExistsKQ_duration()){
	//			this.err_message = "没有可申请的考勤期间！";
	//			this.formHM.put("err_message", this.err_message);
	//			return;
	//		}
	//		/** 是否在封存时间范围内 */
	//		if (!KqUtilsClass.comparentWithKqDuration(OperateDate.dateToStr(
	//				startDate, "yyyy-MM-dd"))) {
	//			this.err_message = "申请起始时间不能在封存考勤时间范围内！";
	//			this.formHM.put("err_message", this.err_message);
	//			return;
	//		}
			Date cEndDate = null;
			/**
			 * 判断结束时间和开始时间的大小 开始时间大 日期 减 1天
			 */
			if (this.coDay_flag && "0".equals(app_way)) {
				cEndDate = OperateDate.addDay(endDate, -1);
			} else {
				cEndDate = endDate;
			}
			if ("Q11".equalsIgnoreCase(table)) {
				/** 判断是否和选择的类型相符合 */
				if (is_overtime_op) {
					/** 判断是否是节假日 */
					if (KqAppInterface.isFeastOvertime(app_type)) {
						if (!ve.isFeastDay(startDate, cEndDate)) {
							this.err_message = ResourceFactory.getProperty("error.kq.nfeast");
							this.formHM.put("err_message", this.err_message);
							return;
						}
					}
				}
				String overtime_rule = KqParam.getInstance().getOvertimeRule(this.frameconn, userView);
				String overtime_rule_status = KqParam.getInstance().getOvertimeRuleStatus(this.frameconn, userView);
				if (overtime_rule_status != null && "1".equals(overtime_rule_status) && 
						overtime_rule != null && overtime_rule.length() > 0) {
				    int num = Integer.parseInt(overtime_rule);
					Date appDate = new Date();
					int coun = OperateDate.getDayCountByDate(startDate, appDate);
					if (coun < 0 && Math.abs(coun) > num) {
						this.err_message = annualApply.getOvertimeRuleHintInfo(num);
						this.formHM.put("err_message", this.err_message);
						return;
					}
				}
			}
			
			Iterator it = infolist.iterator();
			while (it.hasNext()) {
				Map infoMap = (Map) it.next();
				String nbase = (String) infoMap.get("nbase");
				
				// 检验在 请假Q15 公出Q13 加班Q11 是否有冲突
	            this.err_message = ve.checkAppRepeat(table, nbase, infoMap, startDate, endDate, false);
	            if (!"".equals(this.err_message)) {
	                this.formHM.put("err_message", this.err_message);
	                return;
	            }
	            
				if ("Q11".equalsIgnoreCase(table)) {
					/** 判断是否是平时加班 */
					if (KqAppInterface.isNormalOvertime(app_type)) {
						boolean conday = false;
	                	ViewAllApp viewAllApp = new ViewAllApp(frameconn);
	                	String classID = viewAllApp.getClassid((String) infoMap.get("a0100"), nbase, OperateDate.addDay(startDate, -1));//看看前一天是否是跨天班
	                	if(classID != null && classID.length() > 0 && !"0".equals(classID)){
	                		timeSet = kqUtilsClass.getTimeAreaInclassById(classID);
	                		conday = ((Boolean)timeSet.get("conday")).booleanValue();
	                	}
	                	if (is_overtime_op) {
		                    if (!validateAppOper.if_Peacetime(startDate, cEndDate, nbase, (String) infoMap.get("a0100")) && !conday) {
		                        this.err_message = "申请平时加班，申请时间段不应包含休息班次(" + OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm") 
		                        					+ "-" + OperateDate.dateToStr(cEndDate, "yyyy-MM-dd HH:mm") + ")，不允许提交加班！";
		                        this.formHM.put("err_message", this.err_message);
		                        return;
		                    }
		                    //szk
		                    if(validateAppOper.getRest_Peacetime_mess().length()>0){
		                    	throw new GeneralException(infoMap.get("a0101") + "，" + validateAppOper.getRest_Peacetime_mess());
		                    }
		                    
	                	}
					}
					if (is_overtime_op) {
						/** 判断是否是公休日加班 */
						if (KqAppInterface.isRestOvertime(app_type)) {
						    if(class_id != null && !"#".equals(class_id))
						        cEndDate = startDate;
							if (!ve.isRestOfWeekDay(startDate, cEndDate, nbase,
									(String) infoMap.get("a0100"))) {
								//申请的日期不在公休日内，请调整申请日期！
								this.err_message = infoMap.get("a0101")
										+ ResourceFactory.getProperty("error.kq.nrest");
								this.formHM.put("err_message", this.err_message);
								return;
							}
							if (ve.isFeastDay(startDate, cEndDate)) {
								//申请日期不应包含节假日，请调整申请日期！
								err_message =  infoMap.get("a0101")
										+ ResourceFactory.getProperty("error.kq.nofeast");
								this.formHM.put("err_message", this.err_message);
								return;
							}
						}
					}
					/** 当选择节假日和公休日 加班 切只能申请一次的时候 */
					if (rest_overtime_time == null
							|| "1".equals(rest_overtime_time)
							|| rest_overtime_time.length() < 1) {
						/* 判断是否是公休日 */
						if (KqAppInterface.isRestOvertime(app_type)) {
							if (ve.isRestofWork(table, nbase, (String) infoMap.get("a0100"), startDate, endDate, "10")) {
								err_message = "对不起，" + infoMap.get("a0101")
								+ "已经在这个公休日申请了加班！";
								this.formHM.put("err_message", this.err_message);
								return;
							}
						}
						/* 判断是否是节假日 */
						if (KqAppInterface.isFeastOvertime(app_type)) {
							/* 判断这个节日是否已经申请 */
							if (ve.isFeastofWork(table, nbase, (String) infoMap.get("a0100"), startDate, endDate, "11")) {
								err_message = "对不起，" + infoMap.get("a0101")
								+ "已经在这个节假日申请了加班！";
								this.formHM.put("err_message", this.err_message);
								return;
							}
						}
					}
	
					/** 判断申请时间是否在已经排的工作日中 */
					if ("#".equals(class_id)) {
						if (OperateDate.dateToStr(startDate, "yyyy-MM-dd").equals(
								OperateDate.dateToStr(endDate, "yyyy-MM-dd"))) {
							this.coDay_flag = false;
						} else {
							this.coDay_flag = true;
						}
					}
					// if (ve.isWeekDay(nbase, (String) infoMap.get("a0100"),
					// startDate, endDate, mess1, this.coDay_flag)) {
					// this.err_message = "对不起，" + infoMap.get("a0101")
					// + ",申请时间已在安排的工作时间内！";
					// this.formHM.put("err_message", this.err_message);
					// return;
					// }
	
					// if (app_way.equals("1")) {
					// if (ve.isAppTimeXWorkclass(nbase, (String) infoMap
					// .get("a0100"), startDate, endDate)) {
					// this.err_message = "对不起，" + infoMap.get("a0101")
					// + ",申请时间已在安排的工作时间内！";
					// this.formHM.put("err_message", this.err_message);
					// return;
					// }
					// }
	
				} else if ("Q13".equalsIgnoreCase(table)) {
	
					/** 请假检查 */
				} else if ("Q15".equalsIgnoreCase(table)) {
					if (!ve.isArrangedWeek(endDate, nbase, (String) infoMap.get("a0100"))) {
						this.err_message = "对不起，" + infoMap.get("a0101")
								+ "请假申请的时间还没有排班，请排完班后再申请！";
						this.formHM.put("err_message", this.err_message);
						return;
					}
					this.err_message = ve.leaveTimeApp(startDate, endDate, nbase,
							            (String) infoMap.get("a0100"), 
							            (String) infoMap.get("b0110"));
					if (!"".equals(this.err_message))
		            {
		                this.formHM.put("err_message", this.err_message);
		                return;
		            }
					/** 对管理假期进行检查 */
					this.err_message = validateAppOper.checkHoliday(startDate, endDate, infoMap, app_type, nbase);
					if (!"".equals(this.err_message)) {
					    this.formHM.put("err_message", this.err_message);
					    return;
					}
					
					/** 如果请调休假 检查调休假可用时长是否够用*/
	               String leavetime_type_used_overtime = KqParam.getInstance().getLeaveTimeTypeUsedOverTime();
	               if(app_type.equalsIgnoreCase(leavetime_type_used_overtime)){
	                     //考勤规则应取该假类自己的规则
	                    HashMap kqItemHash = annualApply.count_Leave(app_type);
	                    kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
	                    //假期时长扣减规则参数
	                    float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
	                    if (KqParam.getInstance().isHoliday(frameconn, (String)infoMap.get("b0110"), app_type))
	                        holidayRules = annualApply.getHoliday_minus_rule();
	
	                    float   timeLen = annualApply.calcLeaveAppTimeLen(nbase, (String)infoMap.get("a0100"), "", startDate, endDate, kqItemHash, holidayRules, Integer.MAX_VALUE);
	                 
	                    times = timeLen * 60;
	                  
	                    String hr_counts = String.valueOf((int)times);
	
	                    this.err_message = ve.checkUsableTime(startDate,infoMap,app_type,nbase,null,hr_counts);
	                
	                    if (!"".equals(this.err_message)) {
	                        this.formHM.put("err_message", this.err_message);
	                        return;
	                    }
	                }
				}
			}
			
		} catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    } finally {
	        // --------------------------------日期验证结束----------------------------------------
	        this.formHM.put("pro_message", this.pro_message);
	        this.formHM.put("err_message", this.err_message);
	        
	    }
	}

}
