package com.hjsj.hrms.transaction.kq.kqself;

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
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetSelfEndDateTrans extends IBusiness {

    private static final long serialVersionUID   = 1L;

    private String  rest_overtime_time = "0";

    private String  err_message = "";    // 错误信息

    private String  pro_message = "";

    private boolean coDay_flag = false; // 选择的班次是否 跨天
    
    private String warnMessage = "";

    public void execute() throws GeneralException {
        
        String start_date = (String) this.formHM.get("start_d");// 开始日期
        String app_way = (String) this.formHM.get("app_way");// 申请方法
        String start_time_h = (String) this.formHM.get("start_time_h");// 开始时间的小时
        String start_time_m = (String) this.formHM.get("start_time_m");// 开始时间的 分钟
        String date_count = (String) this.formHM.get("date_count");// 申请天数
        if(StringUtils.isEmpty(date_count))
        	date_count = "0";
        String time_count = (String) this.formHM.get("time_count");// 申请小时数
        if(StringUtils.isEmpty(time_count))
        	time_count = "0";
        String table = (String) this.formHM.get("table");// 加班，请假，公出 表
        String sels = (String) this.formHM.get("sels");// 申请类型
        float times =0;
        Date startDate = null;
        Date endDate = null; // 申请日期型日期
        
        try { 
            HashMap infoMap = new HashMap();
            String a0100 = this.userView.getA0100();
            String nbase = this.userView.getDbname();
            String b0110 = this.userView.getUserOrgId();
            String a0101 = this.userView.getUserFullName();
            if (a0100 == null || a0100.length() < 1 || nbase == null || nbase.length() < 1) {
                this.formHM.put("rdx_message", "");
                this.formHM.put("njcxts", "");
                this.formHM.put("endDate", OperateDate.dateToStr(new Date(), "yyyy-MM-dd HH:mm"));
                this.err_message = "对不起！您不是自助用户，无法对该项进行申请！";
                this.formHM.put("err_message", this.err_message);
                String scope_start_time = (String) this.getFormHM().get("scope_start_time");
                String scope_end_time = (String) this.getFormHM().get("scope_end_time");
                startDate = OperateDate.strToDate(scope_start_time, "yyyy-MM-dd HH:mm");
            	
    			if(	scope_end_time.length()==0 ){
    				endDate = OperateDate.strToDate(scope_start_time, "yyyy-MM-dd HH:mm");}
    		
                return;
            }
            
            infoMap.put("a0100", a0100);
            infoMap.put("nbase", nbase);
            infoMap.put("b0110", b0110);
            infoMap.put("a0101", a0101);
            AnnualApply annualApply = new AnnualApply(this.userView, this.frameconn);
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            // 判断节假日 可以申请 一次 还是多次加班
            this.rest_overtime_time = KqParam.getInstance().getRestOvertimeTimes();

            ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.frameconn);
            GetValiateEndDate ve = new GetValiateEndDate(this.userView, this.frameconn);
            boolean is_overtime_op = validateAppOper.is_OVERTIME_TYPE();

            Map timeSet = new HashMap();
            String class_id = "";
            if ("Q11".equalsIgnoreCase(table)) {
                class_id = (String) this.formHM.get("class_id");// 参考班次
                if ("1".equals(app_way) || StringUtils.isBlank(class_id))
                    class_id = "#";
                // 未选择班次时class_id=#，需给默认起止时间
                if ("0".equals(DataDictionary.getFieldItem("q1104").getState()) || "#".equals(class_id)) {
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
            	
                /** 判断 选择的是按照 ‘天’radio=0 还是 ‘小时’radio=1 计算 */
                if ("0".equals(app_way)) {
                    startDate = OperateDate.strToDate(start_date + " "// 得到申请的开始时间
                            + (String) timeSet.get("startTime"), "yyyy-MM-dd HH:mm");
                    endDate = OperateDate.strToDate(start_date + " " + timeSet.get("endTime"), "yyyy-MM-dd HH:mm");
                    count = date_count;
                    /** 获得结束时间 根据开始日期、天数 和 班次的开始时间和结束时间 */
                    endDate = ve.getEndTimetoQ11(startDate, endDate, count, this.coDay_flag, app_way, class_id);
                } else if ("1".equals(app_way)) {// 按照小时计算
                    startDate = OperateDate.strToDate(start_date + " " + start_time_h + ":" + start_time_m, "yyyy-MM-dd HH:mm");// 开始时间
                    // if (mess1.equals("#")) {
                    endDate = startDate;
                    // } else {
                    // endDate = OperateDate.strToDate(start_date + " "
                    // + timeSet.get("endTime"), "yyyy-MM-dd HH:mm");
                    // }

                    count = time_count;
                    /** 获得结束时间 根据开始日期、天数 和 班次的开始时间和结束时间 */
                    endDate = ve.getEndTimetoQ11(startDate, endDate, count, this.coDay_flag, app_way, class_id);
                    Date cstartDate = null;
                    int diff = OperateDate.getDayCountByDate(endDate,startDate );
                    ViewAllApp viewAllApp = new ViewAllApp(frameconn);
                    for (int i = 0; i <= diff; i++)
					{
                    	cstartDate = OperateDate.addDay(startDate, i);
                    	String class_id1 = viewAllApp.getClassid(a0100, nbase, cstartDate);
//                         if(class_id ==null){
//                         	 this.err_message = "对不起，您申请的时间还没有排班，请排完班后再申请！";
//                              this.formHM.put("err_message", this.err_message);
//                              return;
//                         }
                         if(!"0".equals(class_id1)&&class_id1 !=null &&class_id1.length()>0){
                         timeSet = kqUtilsClass.getTimeAreaInclassById(class_id1);//根据班次class_id获取<申请第一天>班次的开始时间和结束时间
                         String startTime = (String) timeSet.get("startTime");
                         String endTime = (String) timeSet.get("endTime");
                         String onduty_2 = (String) timeSet.get("onduty_2");
                         Date onduty_2D =null;
                         Date offduty_1D  =null;
                         if(onduty_2 !=null && onduty_2.trim().length() > 0)
                         {
                        	 onduty_2D = OperateDate.addDay(OperateDate.strToDate(start_date + " " + onduty_2, "yyyy-MM-dd HH:mm"), i);
                         }
                         String offduty_1 = (String) timeSet.get("offduty_1");
                         if(offduty_1 !=null && offduty_1.trim().length() > 0)
						{
                        	 offduty_1D =OperateDate.addDay (OperateDate.strToDate(start_date + " " + offduty_1, "yyyy-MM-dd HH:mm"), i);
						}
                         boolean conday = ((Boolean) timeSet.get("conday")).booleanValue();
                         Date sdate = OperateDate.addDay(OperateDate.strToDate(start_date + " " + startTime, "yyyy-MM-dd HH:mm"),i);
                         Date eDate = OperateDate.addDay(OperateDate.strToDate(start_date + " " + endTime, "yyyy-MM-dd HH:mm"), i);
	                         if(conday)
	                         	eDate = OperateDate.addDay(eDate, 1);
	                        // if (startDate.getTime() < eDate.getTime() && endDate.getTime() > sdate.getTime()) 
	                         //szk判断不包含午休
		                     if(diff == 0){
		                         if (!(startDate.getTime() >= eDate.getTime() || endDate.getTime() <= sdate.getTime() 
		                         		|| ( startDate.getTime() >= offduty_1D.getTime() && endDate.getTime() <= onduty_2D.getTime() ))) 
		     					{
		                         	this.warnMessage = "申请加班时间段包含正常班次，确定继续申请？";
		     					}
		                     }
		                     else {
		                    	 //跨天申请
		                    	 if(i == 0){
			                    		 if (startDate.getTime() < eDate.getTime() ) 
			 	     					{
			 	                         	this.warnMessage = "申请加班时间段包含正常班次，确定继续申请？";
			 	                         	break;
			 	     					}
		                    	 } 
		                    	 else {
		                    		 if ( endDate.getTime() > sdate.getTime()) 
			 	     					{
			 	                         	this.warnMessage = "申请加班时间段包含正常班次，确定继续申请？";
			 	                         	break;
			 	     					}
								}
								
							}
                        }
					}
                   
                    this.getFormHM().put("warnMessage",this.warnMessage);
                    
                }
            } else if ("Q13".equalsIgnoreCase(table)) {// 公出申请
                if ("0".equals(app_way)) {
                    startDate = OperateDate.strToDate(start_date + " 00:00", "yyyy-MM-dd HH:mm");
                    Map data = ve.getTimeByDate(nbase, a0100, startDate);
                    if (!data.isEmpty()) {
                        startDate = OperateDate.strToDate(start_date + " " + data.get("startTime"), "yyyy-MM-dd HH:mm");
                    }
                    count = date_count;
                    endDate = ve.getEndTimeToQ13(startDate, count, app_way,  sels,nbase, a0100);
                } else if ("1".equals(app_way)) {
                    startDate = OperateDate.strToDate(start_date + " " + start_time_h + ":" + start_time_m, "yyyy-MM-dd HH:mm");// 开始时间
                    count = time_count;
                    endDate = ve.getEndTimeToQ13(startDate, count, app_way, sels, nbase, a0100);
                }

            } else if ("Q15".equalsIgnoreCase(table)) {// 请假申请
                if ("0".equals(app_way)) {
                    startDate = OperateDate.strToDate(start_date + " 00:00", "yyyy-MM-dd HH:mm");
                    Map data = ve.getTimeByDate(nbase, a0100, startDate);
                    if (!data.isEmpty()) {
                        startDate = OperateDate.strToDate(start_date + " " + data.get("startTime"), "yyyy-MM-dd HH:mm");
                    }
                    count = date_count;
                    endDate = ve.getEndTimeToQ15(startDate, count, app_way, sels, nbase, a0100);
                } else if ("1".equals(app_way)) {
                    startDate = OperateDate.strToDate(start_date + " " + start_time_h + ":" + start_time_m, "yyyy-MM-dd HH:mm");// 开始时间
                    count = time_count;
                    endDate = ve.getEndTimeToQ15(startDate, count, app_way, sels, nbase, a0100);
                }
            }
            
            if ("2".equals(app_way)) {
                String scope_start_time = (String) this.getFormHM().get("scope_start_time");
                String scope_end_time = (String) this.getFormHM().get("scope_end_time");
                startDate = OperateDate.strToDate(scope_start_time, "yyyy-MM-dd HH:mm");
            	
    			if(	!"".equals(scope_end_time) ){
    				endDate = OperateDate.strToDate(scope_end_time, "yyyy-MM-dd HH:mm");
	                if (startDate.after(endDate) || startDate.equals(endDate)) {
	                    this.err_message = "申请日期错误，起始日期大于或等于结束日期！";
	                    this.formHM.put("rdx_message", this.err_message);
	                    this.formHM.put("njcxts", "");
	                    this.formHM.put("err_message", this.err_message);
	                    return;
	                }
    			} else {
    				endDate = OperateDate.strToDate(scope_start_time, "yyyy-MM-dd HH:mm");
    			}
            }
            this.formHM.put("endDate", OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm"));

            // ------------------------------计算结束日期结束---------------------------------------
            // --------------------------------日期验证开始----------------------------------------

            if ("Q15".equalsIgnoreCase(table)) {
                String njcxts = "";
                if (KqParam.getInstance().isHoliday(this.frameconn, (String) infoMap.get("b0110"), sels)) {
                    HashMap kqItem_hash = annualApply.count_Leave(sels);
                    float myTime = annualApply.getMy_Time(sels, a0100, nbase, OperateDate.dateToStr(startDate, "yyyy.MM.dd HH:mm"),
                            OperateDate.dateToStr(endDate, "yyyy.MM.dd HH:mm"), b0110, kqItem_hash);
                    
                    float other_time = annualApply.othenSealTime(sels, startDate, endDate, (String) a0100, nbase, b0110, "",
                            kqItem_hash, "add", "");
                    //zxj 按考勤规则对应指标小时位保留 
                    myTime = annualApply.roundNumByItemDecimalWidth(kqItem_hash, myTime);
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

            }
            
            
            if (!annualApply.isSessionSearl(startDate, endDate))
                throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");
            
            if (!annualApply.getKqDataState((String) infoMap.get("nbase"), (String) infoMap.get("a0100"), startDate, endDate)) {
                throw new GeneralException((String) infoMap.get("a0101")
                        + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");
            }
            
            //reflag 0:界面中选择申请类型等ajax操作 1：申请表单提交操作（报审、报批、批准等）
            String reflag = (String) this.getFormHM().get("reflag");
            if (reflag == null || "0".equals(reflag) || reflag.length() < 1) {
                return;
            }

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
                    if (KqAppInterface.isFeastOvertime(sels)) {
                        if (!ve.isFeastDay(startDate, cEndDate)) {
                            this.err_message = ResourceFactory.getProperty("error.kq.nfeast");
                            this.formHM.put("err_message", this.err_message);
                            return;
                        }
                    }
                }
            }

            // 检验在 请假Q15 公出Q13 加班Q11 是否有冲突
            this.err_message = ve.checkAppRepeat(table, nbase, infoMap, startDate, endDate, true);
            if (!"".equals(this.err_message)) {
                this.formHM.put("err_message", this.err_message);
                return;
            }

            if ("Q11".equalsIgnoreCase(table)) {
                String overtime_rule_status = KqParam.getInstance().getOvertimeRuleStatus(this.frameconn, userView);
                if (overtime_rule_status != null && "1".equals(overtime_rule_status)) {
                    String overtime_rule = KqParam.getInstance().getOvertimeRule(this.frameconn, userView);
                    if (overtime_rule != null && overtime_rule.length() > 0) {
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
                
                /** 判断是否是平时加班 */
                if (KqAppInterface.isNormalOvertime(sels)) {
                	boolean conday = false;
                	ViewAllApp viewAllApp = new ViewAllApp(frameconn);
                	String classID = viewAllApp.getClassid(a0100, nbase, OperateDate.addDay(startDate, -1));//看看前一天是否是跨天班
                	if(classID != null && classID.length() > 0 && !"0".equals(classID)){
                		timeSet = kqUtilsClass.getTimeAreaInclassById(classID);
                		conday = ((Boolean)timeSet.get("conday")).booleanValue();
                	}
                	if (is_overtime_op) {
	                	if (!validateAppOper.if_Peacetime(startDate, cEndDate, nbase, a0100) && !conday) {
	                        this.err_message = "申请平时加班，申请时间段不应包含休息班次(" + OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm") 
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
                    if (KqAppInterface.isRestOvertime(sels)) {
                        if (class_id != null && !"#".equals(class_id))
                            cEndDate = startDate;
                        
                        if (!ve.isRestOfWeekDay(startDate, cEndDate, nbase, a0100)) {
                        	//申请的日期不在公休日内，请调整申请日期！
							this.err_message = a0101
									+ ResourceFactory.getProperty("error.kq.nrest");
                            this.formHM.put("err_message", this.err_message);
                            return;
                        }
                        
                        if (ve.isFeastDay(startDate, cEndDate)) {
                        	//申请日期不应包含节假日，请调整申请日期！
							err_message =  a0101
									+ ResourceFactory.getProperty("error.kq.nofeast");
                            this.formHM.put("err_message", this.err_message);
                            return;
                        }
                    }

                }
                if (rest_overtime_time == null || "1".equals(rest_overtime_time) || rest_overtime_time.length() < 1) {
                    /* 判断是否是节假日 */
                    if (KqAppInterface.isFeastOvertime(sels)) {
                        /* 判断这个节日是否已经申请 */
                        if (ve.isFeastofWork(table, nbase, a0100, startDate, endDate, "11")) {
                            err_message = "对不起，" + a0101 + "，已经在这个节假日申请了加班！";
                            this.formHM.put("err_message", this.err_message);
                            return;
                        }
                    }
                }
                /** 判断申请时间是否在已经排的工作日中 */
                if ("#".equals(class_id)) {
                    if (OperateDate.dateToStr(startDate, "yyyy-MM-dd").equals(OperateDate.dateToStr(endDate, "yyyy-MM-dd"))) {
                        this.coDay_flag = false;
                    } else {
                        this.coDay_flag = true;
                    }
                }
            } else if ("Q13".equalsIgnoreCase(table)) {

                /** 请假检查 */
            } else if ("Q15".equalsIgnoreCase(table)) {
                if (!ve.isArrangedWeek(endDate, nbase, a0100)) {
                    this.err_message = "对不起，请假申请的时间还没有排班，请排完班后再申请！";
                    this.formHM.put("err_message", this.err_message);
                    return;
                }

                this.err_message = ve.leaveTimeApp(startDate, endDate, nbase, a0100, b0110);
                if (!"".equals(this.err_message)) {
                    this.formHM.put("err_message", this.err_message);
                    return;
                }

                /** 对管理假期进行检查 */
                this.err_message = validateAppOper.checkHoliday(startDate, endDate, infoMap, sels, nbase);
                if (!"".equals(this.err_message)) {
                    this.formHM.put("err_message", this.err_message);
                    return;
                }
                
                /**调休假可用天数检查*/
				String leavetime_type_used_overtime = KqParam.getInstance().getLeaveTimeTypeUsedOverTime();
			       if(sels.equalsIgnoreCase(leavetime_type_used_overtime)){
	    				String hr_counts ="";
	    				
	    				if("0".equals(app_way)){
	    				    time_count = null;
	    	                date_count = String.valueOf(Integer.parseInt(date_count)*8*60);
	    	            } else if("1".equals(app_way)){
	    	                date_count = null;
	    	                hr_counts = String.valueOf(Float.parseFloat(time_count)*60);
	    	            } else if("2".equals(app_way)){
	    	            	 //考勤规则应取改假类自己的规则
	    	                HashMap kqItemHash = annualApply.count_Leave(sels);
	    	                kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
	    	                //假期时长扣减规则参数
	    	                float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
	    					if (KqParam.getInstance().isHoliday(frameconn, (String)infoMap.get("b0110"), sels))
	    						holidayRules = annualApply.getHoliday_minus_rule();
	    	                
	
	    					float   timeLen = annualApply.calcLeaveAppTimeLen(nbase, (String)infoMap.get("a0100"), "", startDate, endDate, kqItemHash, holidayRules, Integer.MAX_VALUE);
	    	             
	                        times = timeLen * 60;
	                      
	    	                hr_counts = String.valueOf((int)times);
	    	            }

		                this.err_message = ve.checkUsableTime(startDate,infoMap,sels,nbase,date_count,hr_counts);
	                
	    	            if (!"".equals(this.err_message)) {
	    	                this.formHM.put("err_message", this.err_message);
	    	                return;
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
            //this.formHM.put("warnMessage",this.warnMessage);
            if ( this.err_message.length()>0)
			{
            	this.formHM.put("startDate", "");
            	this.formHM.put("endDate", "");
			}
            else {
            	this.formHM.put("startDate", OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm"));
            	this.formHM.put("endDate", OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm"));
			}
            
        }
    }
}
