/*
 * Created on 2006-1-10
 *
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
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

/**
 * @author wxh
 * 
 */
public class SaveKqAppTrans extends IBusiness {

	public void execute() throws GeneralException {

		String start_time_h = (String) this.getFormHM().get("start_time_h");
		String start_time_m = (String) this.getFormHM().get("start_time_m");
		String start_date = (String) this.getFormHM().get("app_start_date");
		String end_date = (String) this.getFormHM().get("app_end_date");
		String radio = (String) this.getFormHM().get("radio");
		String mess = (String) this.getFormHM().get("mess");//申请类型
		String mess1 = (String) this.getFormHM().get("mess1");//参考班次
		mess1 = PubFunc.keyWord_reback(mess1);
		String message = (String) this.getFormHM().get("message");
		String infoStr = (String) this.formHM.get("infoStr");// 用户信息字符串
		String table = (String) this.formHM.get("table");// 表名
		String nbase = "";// 人员库
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String sub_flag = (String) hm.get("sub_flag");
		String scope_start_time = (String) this.getFormHM().get("scope_start_time");
		String scope_end_time = (String) this.getFormHM().get("scope_end_time");
		
		String appReaCode = "";
		String appReaField = "";
		String appReaCodesetid = (String) this.getFormHM().get("appReaCodesetid");
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
		if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) 
		{
			appReaCode = (String)hm.get("appReaCode");
			appReaField = kqUtilsClass.getAppReaField(new ContentDAO(frameconn)).toLowerCase();
		}
		
		Date startDate = null;
		Date endDate = null;
		GetValiateEndDate ve = new GetValiateEndDate(this.userView,	this.frameconn);
		infoStr = PubFunc.keyWord_reback(infoStr);
		ArrayList list = ve.userInfo(infoStr);

		if (mess1 != null && "#".equals(mess1)) {
			mess1 = null;
		}
		if (!"08".equals(sub_flag) && !"03".equals(sub_flag))
			sub_flag = "02";

		if ("1".equals(radio)) {
			mess1 = null;
			startDate = OperateDate.strToDate(start_date + " " + start_time_h
					+ ":" + start_time_m, "yyyy-MM-dd HH:mm");
			endDate = OperateDate.strToDate(end_date, "yyyy-MM-dd HH:mm");
		} else if ("0".equals(radio)) {
			if ("Q11".equalsIgnoreCase(table)) {
				Map timeSet = kqUtilsClass.getTimeAreaInclassById(mess1); // 根据班次id号获得班次的开始时间和结束时间
				if ("0".equals(DataDictionary.getFieldItem("q1104").getState())) {
					timeSet = new HashMap();
					timeSet.put("startTime", "00:00");
					timeSet.put("endTime", "23:59");
				}
				startDate = OperateDate.strToDate(start_date + " " + timeSet.get("startTime"), "yyyy-MM-dd HH:mm");
			} else {
				Map infoMap = (Map) list.get(0);
				startDate = OperateDate.strToDate(start_date + " 00:00", "yyyy-MM-dd HH:mm");
				Map data = ve.getTimeByDate((String) infoMap.get("nbase"), (String) infoMap.get("a0100"), startDate);
				if (!data.isEmpty()) {
					startDate = OperateDate.strToDate(start_date + " "	+ data.get("startTime"), "yyyy-MM-dd HH:mm");
				}
			}
			endDate = OperateDate.strToDate(end_date, "yyyy-MM-dd HH:mm");
		} else if ("2".equals(radio)) {
			startDate = OperateDate.strToDate(scope_start_time,	"yyyy-MM-dd HH:mm");
			endDate = OperateDate.strToDate(scope_end_time, "yyyy-MM-dd HH:mm");
		}
		
		boolean haveOvertimeFlag = false;
		if ("Q11".equalsIgnoreCase(table)) {
        	DbWizard dbw = new DbWizard(this.frameconn);
        	haveOvertimeFlag = dbw.isExistField("Q11", "flag", false);
		}
		AnnualApply annualApply = new AnnualApply(userView,frameconn);
		ArrayList infoList = new ArrayList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Map infoMap = (Map) it.next();
			nbase = (String) infoMap.get("nbase");
			RecordVo vo = getAppVo(infoMap, mess, table, nbase, 
			        startDate, endDate, message, sub_flag, 
			        appReaField, appReaCodesetid, appReaCode, mess1, haveOvertimeFlag);
			
			infoList.add(vo);
			
			if ("q11".equalsIgnoreCase(table)) 
			{
				String iftoRest = (String) hm.get("IftoRest");
				String iftoRestField = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
				String error = "";
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
	            	throw new GeneralException(error);
	            }
			}
		}
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.addValueObject(infoList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private RecordVo getAppVo(Map infoMap, String mess, String table, String nbase,
	        Date startDate, Date endDate, String message, String sub_flag,
	        String appReaField, String appReaCodesetid, String appReaCode, String mess1,
	        boolean haveOvertimeFlag){
	    RecordVo vo = new RecordVo(table);
	    try{
            vo.setString("nbase", nbase); // 应用库前缀
            vo.setString("a0100", (String) infoMap.get("a0100")); // 人员编号
            vo.setString("b0110", (String) infoMap.get("b0110")); // 单位编码
            vo.setString("e0122", (String) infoMap.get("e0122")); // 部门编码
            vo.setString("a0101", (String) infoMap.get("a0101")); // 姓名
            vo.setString("e01a1", (String) infoMap.get("e01a1")); // 职务编码
            
            vo.setDate(table.toLowerCase() + "z1", startDate); // 开始日期
            vo.setDate(table.toLowerCase() + "z3", endDate); // 结束日期
            vo.setDate(table.toLowerCase() + "05", OperateDate.getDateByFormat(new Date(), "yyyy-MM-dd HH:mm")); // 申请日期
            vo.setString(table.toLowerCase() + "07", message); // 加班事由
            vo.setString(table.toLowerCase() + "03", mess); // 加班类型
    
            if ("02".equals(sub_flag)) {
                vo.setString(table.toLowerCase() + "09", this.userView.getUserFullName());
            } else if ("03".equals(sub_flag)) {
                vo.setString(table.toLowerCase() + "13", this.userView.getUserFullName());
            }
    
            vo.setString(table.toLowerCase() + "01", checkAppkeyid(table));
            
            if (sub_flag != null && "03".equals(sub_flag)) {
                vo.setString(table.toLowerCase() + "z0", "01");
                vo.setString(table.toLowerCase() + "z5", sub_flag);
            } else {
                vo.setString(table.toLowerCase() + "z0", "03");
                vo.setString(table.toLowerCase() + "z5", sub_flag);
            }
            
            if ("q15".equalsIgnoreCase(table)) {
                vo.setString("q1517", "0");
                if ("01".equals(vo.getString("q15z0")) && "03".equals(vo.getString("q15z5"))) {
                    float d_Count = 0;
                    AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
                    HashMap kqItem_hash = annualApply.count_Leave(vo.getString("q1503"));                   
                    
                    if(KqParam.getInstance().isHoliday(this.frameconn, vo.getString("b0110"), mess))
                    {
                         float[] holiday_rules=annualApply.getHoliday_minus_rule();//年假假期规则
                         //年假不论考勤规则按什么单位定义，此处计算时都按天来处理
                         kqItem_hash.put("item_unit", KqConstant.Unit.DAY);
                         d_Count = annualApply.getHistoryLeaveTime(vo.getDate("q15z1"), vo
                                    .getDate("q15z3"), vo.getString("a0100"), vo
                                    .getString("nbase"),
                                    (String) vo.getString("b0110"), kqItem_hash,holiday_rules);
                         String history = annualApply.upLeaveManage(vo
                                    .getString("a0100"), vo.getString("nbase"), vo
                                    .getString("q1503"), OperateDate.dateToStr(
                                    startDate, "yyyy-MM-dd HH:mm"), OperateDate
                                    .dateToStr(endDate, "yyyy-MM-dd HH:mm"), d_Count,
                                    "1", (String) vo.getString("b0110"), kqItem_hash,holiday_rules);
                         vo.setString("history", history);
                     }
                     /** 审批请假单时 如果是调休假 更新调休明细表Q33*/
                    String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                    if("01".equals(vo.getString("q15z0"))
                            && "03".equals(vo.getString("q15z5")) &&
                            mess.equalsIgnoreCase(leavetime_type_used_overtime)){
                        int timeCount = 0;
                        //考勤规则应取改假类自己的规则
                        HashMap kqItemHash = annualApply.count_Leave(mess);
                        kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
                        //假期时长扣减规则参数
                        float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
                        if (KqParam.getInstance().isHoliday(frameconn, vo.getString("b0110"), mess))
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
                }
            } else if ("q11".equalsIgnoreCase(table.toLowerCase())) {
                vo.setString(table.toLowerCase() + "04", mess1);// 参考班次
                //szk申请时长
                String applytime = KqUtilsClass.getFieldByDesc("q11", ResourceFactory.getProperty("kq.class.applytime"));
                if ( applytime!= null && applytime.trim().length() > 0 && "q11z4".equalsIgnoreCase(applytime)){
                	AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
	                HashMap kqItemHash = annualApply.count_Leave(vo.getString("q1103"));    
	                float timeLen=0;
	                if (mess1!=null&&!"".equals(mess1) && !"0".equals(mess1))
					{
	                	 KqUtilsClass kqUtilsClass = new KqUtilsClass(this.frameconn);
	                     HashMap classMap = (HashMap) kqUtilsClass.getTimeAreaInclassById(mess1);
	                     timeLen = new Float((String)classMap.get("work_hours")).floatValue();
	                     timeLen = annualApply.roundNumByItemDecimalWidth(kqItemHash, timeLen/60);
					}
	                else {
	                	 timeLen = annualApply.calcOverAppTimeLen(nbase, (String) infoMap.get("a0100"), startDate, endDate, kqItemHash,  Integer.MAX_VALUE);
	                	 timeLen = annualApply.roundNumByItemDecimalWidth(kqItemHash,timeLen);
	                }
	                vo.setDouble(table.toLowerCase() + "z4", timeLen);// 申请时长
                }
                String dert_itemid=(String)this.getFormHM().get("dert_itemid");
                String dert_value=(String)this.getFormHM().get("dert_value");
                dert_value = dert_value!=null&&dert_value.length()>0?dert_value:"0";
                if("q11".equalsIgnoreCase(table)&&dert_itemid!=null&&dert_itemid.length()>0)
                   vo.setString(dert_itemid,dert_value);//是否有扣除休息时间
                 
                if (haveOvertimeFlag) 
                    vo.setString("flag", "0");//加班申请数据来源
            }
            if ("03".equals(sub_flag)) {
                vo.setDate(table.toLowerCase() + "z7", OperateDate.getDateByFormat(new Date(),
                        "yyyy-MM-dd HH:mm"));// 审批时间
            }
            
            if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) 
            {
                vo.setString(appReaField, appReaCode);
            }
	    }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return vo;
	}

	private String checkAppkeyid(String table) {
		IDGenerator idg = new IDGenerator(2, this.frameconn);
		String insertid = "";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			boolean iscorrect = false;
			while (!iscorrect) {
				insertid = idg.getId((table + "." + table + "01").toUpperCase());
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
			KqUtilsClass.closeDBResource(rs);
		}
		return iscorrect;
	}

}
