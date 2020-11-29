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
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.math.BigDecimal;
import java.util.*;

public class GetEndDateTrans extends IBusiness {

	private String rest_overtime_time = "0";

	private String err_message = "";// 错误信息

	private String pro_message = "";

	private boolean coDay_flag = false;// 选择的班次是否 跨天

	public void execute() throws GeneralException {

		String radio = (String) this.formHM.get("radio"); // 选择天或小时的 标志
		String date_count = (String) this.formHM.get("date_count"); // 天数整数位
		String mess = (String) this.formHM.get("mess"); // 选择的请假或加班类型
		String start_time_h = (String) this.formHM.get("start_time_h"); // 开始时间的 小时数				
		String start_time_m = (String) this.formHM.get("start_time_m"); // 开始时间的 分钟数
				
		String start_date = (String) this.formHM.get("start_date"); // 开始日期
		String hr_count = (String) this.formHM.get("hr_count"); // 小时数整数位
		String infoStr = (String) this.formHM.get("infoStr");// 用户信息字符串
		infoStr = PubFunc.keyWord_reback(infoStr);
		String table = (String) this.formHM.get("table");// 表名
		//String nbase = "";// 人员库
		
        float times =0;
        
		Date startDate = null;
		Date endDate = null; // 申请日期型日期
	     try { 
		// KqUtilsClass 常用考勤工具类
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());

		// 判断节假日 可以申请 一次 还是多次加班
		this.rest_overtime_time = KqParam.getInstance().getRestOvertimeTimes();

		ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.frameconn);
		GetValiateEndDate ve = new GetValiateEndDate(this.userView,	this.frameconn);
		boolean is_overtime_op = validateAppOper.is_OVERTIME_TYPE();

		// 判断是否选择了班次 如果没有选择（“#”） 则设置成为默认班次
		// if (mess1.equals("#")) {
		// mess1 = kqUtilsClass.getKqParameterContent("DEFAULT_REST_KQCLASS",
		// "UN");
		// }

		Map timeSet = new HashMap();
		String mess1 = null;
		if ("Q11".equalsIgnoreCase(table)) {
			mess1 = (String) this.formHM.get("mess1"); // 选择的班次类型
			if ("1".equals(radio))
				mess1 = "#";
			if ("0".equals(DataDictionary.getFieldItem("q1104").getState())) {
				timeSet.put("startTime", "00:00");
				timeSet.put("endTime", "23:59");
			}
			if (!"#".equals(mess1)) {
				timeSet = kqUtilsClass.getTimeAreaInclassById(mess1); // 根据班次id号获得班次的开始时间和结束时间
				/** 判断工作时间是否跨天 */
				if (((Boolean) timeSet.get("conday")).booleanValue()) {
					this.coDay_flag = true;
				}
			}
		}
		// ------------------------------计算结束日期开始---------------------------------------
		String count = "";
		ArrayList infolist = ve.userInfo(infoStr);
		if ("Q11".equalsIgnoreCase(table)) {// 加班申请
			/** 判断 选择的是按照 ‘天’radio=0 还是 ‘小时’radio=1 计算 */
			if ("0".equals(radio)) {
				startDate = OperateDate
						.strToDate(start_date + " "// 得到申请的开始时间
								+ (String) timeSet.get("startTime"),
								"yyyy-MM-dd HH:mm");
				endDate = OperateDate.strToDate(start_date + " "
						+ timeSet.get("endTime"), "yyyy-MM-dd HH:mm");
				count = date_count;
				/** 获得结束时间 根据开始日期、天数 和 班次的开始时间和结束时间 */
				endDate = ve.getEndTimetoQ11(startDate, endDate, count,
						this.coDay_flag, radio, mess1);
			} else if ("1".equals(radio)) {// 按照小时计算
				startDate = OperateDate
						.strToDate(start_date + " " + start_time_h + ":"
								+ start_time_m, "yyyy-MM-dd HH:mm");// 开始时间
				// if (mess1.equals("#")) {
				endDate = startDate;
				// } else {
				// endDate = OperateDate.strToDate(start_date + " "
				// + timeSet.get("endTime"), "yyyy-MM-dd HH:mm");
				// }

				count = hr_count;
				/** 获得结束时间 根据开始日期、天数 和 班次的开始时间和结束时间 */
				endDate = ve.getEndTimetoQ11(startDate, endDate, count,
						this.coDay_flag, radio, mess1);
			}

		} else if ("Q13".equalsIgnoreCase(table)) {// 公出申请
			Map infoMap = (Map) infolist.get(0);
			if ("0".equals(radio)) {
				startDate = OperateDate.strToDate(start_date + " 00:00",
						"yyyy-MM-dd HH:mm");
				Map data = ve.getTimeByDate((String) infoMap.get("nbase"), (String) infoMap
						.get("a0100"), startDate);
				if (!data.isEmpty()) {
					startDate = OperateDate.strToDate(start_date + " "
							+ data.get("startTime"), "yyyy-MM-dd HH:mm");
				}
				count = date_count;
			
				endDate = ve.getEndTimeToQ13(startDate, count, radio, mess,
						(String) infoMap.get("nbase"), (String) infoMap.get("a0100"));
			} else if ("1".equals(radio)) {
				startDate = OperateDate
						.strToDate(start_date + " " + start_time_h + ":"
								+ start_time_m, "yyyy-MM-dd HH:mm");// 开始时间
				count = hr_count;
				endDate = ve.getEndTimeToQ13(startDate, count, radio, mess, (String) infoMap.get("nbase"),
						(String) infoMap.get("a0100"));
			}
		} else if ("Q15".equalsIgnoreCase(table)) {// 请假申请
			if (!infolist.isEmpty()) {
				Map infoMap = (Map) infolist.get(0);
				if ("0".equals(radio)) {
					startDate = OperateDate.strToDate(start_date + " 00:00",
							"yyyy-MM-dd HH:mm");
					Map data = ve.getTimeByDate((String) infoMap.get("nbase"), (String) infoMap
							.get("a0100"), startDate);
					if (!data.isEmpty()) {
						startDate = OperateDate.strToDate(start_date + " "
								+ data.get("startTime"), "yyyy-MM-dd HH:mm");
					}
					count = date_count;
					endDate = ve.getEndTimeToQ15(startDate, count, radio, mess,
							(String) infoMap.get("nbase"), (String) infoMap.get("a0100"));
				} else if ("1".equals(radio)) {
					startDate = OperateDate.strToDate(start_date + " "
							+ start_time_h + ":" + start_time_m,
							"yyyy-MM-dd HH:mm");// 开始时间
					count = hr_count;
					endDate = ve.getEndTimeToQ15(startDate, count, radio, mess,
							(String) infoMap.get("nbase"), (String) infoMap.get("a0100"));
				}
			}
		}
		
		//直接按起止时间区间申请
		if ("2".equals(radio)) {
			String scope_start_time = (String) this.getFormHM().get("scope_start_time");
			String scope_end_time = (String) this.getFormHM().get("scope_end_time");
			startDate = OperateDate.strToDate(scope_start_time,	"yyyy-MM-dd HH:mm");
			
			if(	!"".equals(scope_end_time) ){
				endDate = OperateDate.strToDate(scope_end_time, "yyyy-MM-dd HH:mm");
				
				if (startDate.after(endDate) || startDate.equals(endDate) ) {
					this.err_message = "申请日期错误，起始日期大于或等于结束日期！";
					  this.formHM.put("njcxts", "");
	                    this.formHM.put("err_message", this.err_message);
	                    this.formHM.put("rdx_message", this.err_message);
					return;
				}
			}
			else {
				endDate = OperateDate.strToDate(scope_start_time, "yyyy-MM-dd HH:mm");
			}
		}
		
		this.formHM.put("endDate", OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm"));
		String reflag = (String) this.getFormHM().get("reflag");
        Map infoMap1 = (Map) infolist.get(0);
        AnnualApply annual = new AnnualApply(this.userView, this.frameconn);
        String njcxts = "";
        if (KqParam.getInstance().isHoliday(this.frameconn, (String)infoMap1.get("b0110"), mess))
        {	
		
            HashMap kqItem_hash = annual.count_Leave(mess);
            float myTime = annual.getMy_Time(mess, (String)infoMap1.get("a0100"), (String)infoMap1.get("nbase"), OperateDate.dateToStr(startDate, "yyyy.MM.dd HH:mm"), 
                    OperateDate.dateToStr(endDate, "yyyy.MM.dd HH:mm"), (String)infoMap1.get("b0110"), kqItem_hash);
            float other_time = annual.othenSealTime(mess, startDate, endDate, (String)infoMap1.get("a0100"), (String)infoMap1.get("nbase"), (String)infoMap1.get("b0110"), "", kqItem_hash, "add", "");
            //zxj 按考勤规则对应指标小时位保留 
            myTime = annual.roundNumByItemDecimalWidth(kqItem_hash, myTime);
            if (other_time <= 0) {
                njcxts = "(可休" + myTime + "天)";
            } else {
                BigDecimal b = new BigDecimal(other_time);
                other_time = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                njcxts = "(可休" + myTime + "天，其中申请" + other_time + "天没有批准)";
            }
        }
        this.formHM.put("rdx_message", "");
        this.formHM.put("njcxts", njcxts);
        
		if(!annual.isSessionSearl(startDate,endDate))
    	    throw GeneralExceptionHandler.Handle(new GeneralException("","该考勤期间已封存或不存在，不能做该申请操作！","",""));
		
        if(!annual.getKqDataState((String) infoMap1.get("nbase"),(String) infoMap1.get("a0100"),startDate,endDate))
        {
            throw GeneralExceptionHandler.Handle(new GeneralException("",(String) infoMap1.get("a0101")+"申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！","",""));
        } 
        
        //reflag 0:界面中选择申请类型等ajax操作 1：申请表单提交操作（报审、报批、批准等）
		if (reflag == null || "0".equals(reflag) || reflag.length() < 1) {
			return;
		}
		
		// ------------------------------计算结束日期结束---------------------------------------
		// --------------------------------日期验证开始----------------------------------------
		/** 检查所选人员排班是否一致 */
		ArrayList list = OperateDate.getDayByDate(startDate, endDate);
		if (!ve.isEvenToClass(infolist, "", list)) {
			this.pro_message = "所选人员不在同一个班次，所选日期可能出现误差!";
			this.formHM.put("pro_message", this.pro_message);
		}

		Date cEndDate = null;
		/**
		 * 判断结束时间和开始时间的大小 开始时间大 日期 减 1天
		 */
		if (this.coDay_flag && "0".equals(radio)) {
			cEndDate = OperateDate.addDay(endDate, -1);
		} else {
			cEndDate = endDate;
		}
		if ("Q11".equalsIgnoreCase(table)) {
			/** 判断是否和选择的类型相符合 */
			if (is_overtime_op) {
				/** 判断是否是节假日 */
				if (KqAppInterface.isFeastOvertime(mess)) {
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
				    this.err_message = annual.getOvertimeRuleHintInfo(num);
					this.formHM.put("err_message", this.err_message);
					return;
				}
			}
		}
		
		Iterator it = infolist.iterator();
		while (it.hasNext()) {
			Map infoMap = (Map) it.next();
			// 检验在 请假Q15 公出Q13 加班Q11 是否有冲突 endDate应该是实际申请的时间
            this.err_message = ve.checkAppRepeat(table, (String)infoMap.get("nbase"), infoMap, startDate, endDate, false);
            if (!"".equals(this.err_message)) {
                this.formHM.put("err_message", this.err_message);
                return;
            }
			
			if ("Q11".equalsIgnoreCase(table)) {
				/** 判断是否是平时加班 */
				if ((mess.substring(0,2)).indexOf("12") != -1) {
					boolean conday = false;
                	ViewAllApp viewAllApp = new ViewAllApp(frameconn);
                	String classID = viewAllApp.getClassid((String) infoMap.get("a0100"), (String) infoMap.get("nbase"), OperateDate.addDay(startDate, -1));//看看前一天是否是跨天班
                	if(classID != null && classID.length() > 0 && !"0".equals(classID)){
                		timeSet = kqUtilsClass.getTimeAreaInclassById(classID);
                		conday = ((Boolean)timeSet.get("conday")).booleanValue();
                	}
                	if (is_overtime_op) {
                		if (!validateAppOper.if_Peacetime(startDate, cEndDate, (String)infoMap.get("nbase"), (String) infoMap.get("a0100")) && !conday) {
                			this.err_message = "申请平时加班，申请时间段不应包含休息班次，(" + OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm") 
                			+ "-" + OperateDate.dateToStr(cEndDate, "yyyy-MM-dd HH:mm") + ")，不允许提交加班！";
                			this.formHM.put("err_message", this.err_message);
                			return;
                		}
                		//szk解决前一天跨天的情况
	                    if(validateAppOper.getRest_Peacetime_mess().length()>0){
	                    	throw new GeneralException(infoMap.get("a0101") + "，" + validateAppOper.getRest_Peacetime_mess());
	                    }
                	}
				}
				if (is_overtime_op) {
					/** 判断是否是公休日加班 */
					if (KqAppInterface.isRestOvertime(mess)) {
					    if(mess1 != null && !"#".equals(mess1))
                            cEndDate = startDate;
						if (!ve.isRestOfWeekDay(startDate, cEndDate, (String)infoMap.get("nbase"),
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
					if (KqAppInterface.isRestOvertime(mess)) {
						if (ve.isRestofWork(table, (String)infoMap.get("nbase"), (String) infoMap.get("a0100"), startDate, endDate, "10")) {
							err_message = "对不起，" + infoMap.get("a0101") + "已经在这个公休日申请了加班！";
							this.formHM.put("err_message", this.err_message);
							return;
						}
					}
					/* 判断是否是节假日 */
					if (KqAppInterface.isFeastOvertime(mess)) {
						/* 判断这个节日是否已经申请 */
						if (ve.isFeastofWork(table, (String)infoMap.get("nbase"), (String) infoMap.get("a0100"), startDate, endDate, "11")) {
							err_message = "对不起，" + infoMap.get("a0101") + "已经在这个节假日申请了加班！";
							this.formHM.put("err_message", this.err_message);
							return;
						}
					}
				}

				/** 判断申请时间是否在已经排的工作日中 */
				if ("#".equals(mess1)) {
					if (OperateDate.dateToStr(startDate, "yyyy-MM-dd").equals(
							OperateDate.dateToStr(endDate, "yyyy-MM-dd"))) {
						this.coDay_flag = false;
					} else {
						this.coDay_flag = true;
					}
				}
			} else if ("Q13".equalsIgnoreCase(table)) {

				/** 请假检查 */
			} else if ("Q15".equalsIgnoreCase(table)) {
				if (!ve.isArrangedWeek(endDate, (String)infoMap.get("nbase"), (String) infoMap.get("a0100"))) {
					this.err_message = "对不起，" + infoMap.get("a0101")	+ "请假申请的时间还没有排班，请排完班后再申请！";
					this.formHM.put("err_message", this.err_message);
					return;
				}
				
				this.err_message = ve.leaveTimeApp(startDate, endDate, (String)infoMap.get("nbase"),
						(String) infoMap.get("a0100"), (String) infoMap.get("b0110"));
				if (!"".equals(this.err_message))
	            {
	                this.formHM.put("err_message", this.err_message);
	                return;
	            }
				
				/** 对管理假期进行检查 */
				this.err_message = validateAppOper.checkHoliday(startDate, endDate, infoMap, mess, (String)infoMap.get("nbase"));
				if (!"".equals(this.err_message)) {
					this.formHM.put("err_message", this.err_message);
					return;
				}
				
                /** 如果请调休假 检查调休假可用时长是否够用*/
                String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                if(mess.equalsIgnoreCase(leavetime_type_used_overtime)){
    				String hr_counts ="";
    				
    				if("0".equals(radio)){
    				    hr_count = null;
    	                date_count = String.valueOf(Integer.parseInt(date_count)*8*60);
    	            } else if("1".equals(radio)){
    	                date_count = null;
    	                hr_counts = String.valueOf(Float.parseFloat(hr_count)*60);
    	            } else if("2".equals(radio)){
    	            	 //考勤规则应取改假类自己的规则
    	                HashMap kqItemHash = annual.count_Leave(mess);
    	                kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
    	                //假期时长扣减规则参数
    	                float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
    	                if (KqParam.getInstance().isHoliday(frameconn, (String)infoMap.get("b0110"), mess))
    	                    holidayRules = annual.getHoliday_minus_rule();
    	                
    	                float timeLen = annual.calcLeaveAppTimeLen((String)infoMap.get("nbase"), (String)infoMap.get("a0100"), "", startDate, endDate, kqItemHash, holidayRules, Integer.MAX_VALUE);
    	                
                        times = timeLen * 60;
                      
    	                hr_counts = String.valueOf((int)times);
    	            }

	                this.err_message = ve.checkUsableTime(startDate,infoMap,mess,(String)infoMap.get("nbase"),date_count,hr_counts);
                
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
		this.formHM.put("startDate", OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm"));
		this.formHM.put("endDate", OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm"));
     }
	}
}
