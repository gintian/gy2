package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
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
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SaveSelfKQAppTrans extends IBusiness
{
    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException
    {
    	String error = "";
        try {
	    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	        
	        String sub_flag = "";
	        if(hm != null){
	        	sub_flag = (String) hm.get("sub_flag");
	        }else{
	        	sub_flag = (String)this.formHM.get("sub_flag");
	        }
	        
	        // 开始日期
	        String start_d = (String) this.formHM.get("start_d");
	        
	        // 申请方法
	        String app_way = (String) this.formHM.get("app_way");
	        
	        // 开始时间的
	        String start_time_h = (String) this.formHM.get("start_time_h");// 小时        
	        String start_time_m = (String) this.formHM.get("start_time_m");// 分钟
	        
	        // 加班，请假，公出 表
	        String table = (String) this.formHM.get("table");
	        
	        // 申请类型
	        String sels = (String) this.formHM.get("sels");
	        
	        // 选择的班次类型
	        String class_id = (String) this.formHM.get("class_id"); 
	        class_id = PubFunc.hireKeyWord_filter_reback(class_id);
	        
	        String app_reason = (String) this.formHM.get("app_reason");
	        String end_d = (String) this.formHM.get("end_d");
	        String scope_start_time = (String) this.getFormHM().get("scope_start_time");
	        String scope_end_time = (String) this.getFormHM().get("scope_end_time");
	        
			String appReaCode = "";
			String appReaField = "";
			String appReaCodesetid = (String) this.getFormHM().get("appReaCodesetid");
			KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
			if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) 
			{
				if(hm != null){
					appReaCode = (String)hm.get("appReaCode");
				}else{
					appReaCode = (String)this.getFormHM().get("appReaCode");
				}
				appReaField = kqUtilsClass.getAppReaField(new ContentDAO(frameconn)).toLowerCase();
			}
	
	        String ta = table.toLowerCase();
	        GetValiateEndDate ve = new GetValiateEndDate(this.userView, this.frameconn);
	
	        if (class_id != null && "#".equals(class_id))
	            class_id = null;
	        
	        if (!"08".equals(sub_flag) && !"03".equals(sub_flag))
	            sub_flag = "02";
	        
	        Date startDate = null;
	        Date endDate = null;
	        if ("1".equals(app_way))
	        {
	        	class_id = null;
	            startDate = OperateDate.strToDate(start_d + " " + start_time_h + ":" + start_time_m, "yyyy-MM-dd HH:mm");
	            endDate = OperateDate.strToDate(end_d, "yyyy-MM-dd HH:mm");
	        } 
	        else if ("0".equals(app_way))
	        {
	            if ("Q11".equalsIgnoreCase(table))
	            {
	                Map timeSet = kqUtilsClass.getTimeAreaInclassById(class_id); // 根据班次id号获得班次的开始时间和结束时间
	                if ("0".equals(DataDictionary.getFieldItem("q1104").getState())) {
	                	timeSet = new HashMap();
	    				timeSet.put("startTime", "00:00");
	    				timeSet.put("endTime", "23:59");
	    			}
	                startDate = OperateDate.strToDate(start_d + " " + timeSet.get("startTime"), "yyyy-MM-dd HH:mm");
	            } else
	            {
	                startDate = OperateDate.strToDate(start_d + " 00:00", "yyyy-MM-dd HH:mm");
	                Map data = ve.getTimeByDate(this.userView.getDbname(), this.userView.getA0100(), startDate);
	                if (!data.isEmpty())
	                {
	                    startDate = OperateDate.strToDate(start_d + " " + data.get("startTime"), "yyyy-MM-dd HH:mm");
	                }
	            }
	            endDate = OperateDate.strToDate(end_d, "yyyy-MM-dd HH:mm");
	        } 
	        else if ("2".equals(app_way))
	        {
	            startDate = OperateDate.strToDate(scope_start_time, "yyyy-MM-dd HH:mm");
	            endDate = OperateDate.strToDate(scope_end_time, "yyyy-MM-dd HH:mm");
	        }
	
	        RecordVo vo = new RecordVo(table);
	        if("q11".equals(ta)){
				DbWizard dbw = new DbWizard(this.frameconn);
				if (dbw.isExistField("Q11", "flag", false)) 
					vo.setString("flag", "0");//加班申请数据来源
			}
	        vo.setString("nbase", this.userView.getDbname());// 应用库前缀
	        vo.setString("a0100", this.userView.getA0100());// 人员编号
	        vo.setString("b0110", this.userView.getUserOrgId());// 单位编码
	        vo.setString("e0122", this.userView.getUserDeptId());// 部门编码
	        vo.setString("a0101", this.userView.getUserFullName()); // 姓名
	        vo.setString("e01a1", this.userView.getUserPosId());// 职务编码
	        vo.setDate(ta + "z1", startDate); // 开始日期
	        vo.setDate(ta + "z3", endDate); // 结束日期
	        vo.setDate(ta + "05", OperateDate.getDateByFormat(new Date(), "yyyy-MM-dd HH:mm")); // 申请日期
	        vo.setString(ta + "07", app_reason); // 加班事由
	        vo.setString(ta + "03", sels); // 加班类型
	
	        // if (sub_flag.equals("02")) {
	        // vo.setString(ta + "09", this.userView.getUserFullName());
	        // } else if (sub_flag.equals("03")) {
	        // vo.setString(ta + "13", this.userView.getUserFullName());
	        // }
	
	        vo.setString(ta + "01", checkAppkeyid(table));
	        vo.setString(ta + "z0", "03");
	        /** 报批标志,区分保存、报批两种状态 */
	        if (sub_flag != null && "02".equalsIgnoreCase(sub_flag))
	            vo.setString(ta + "z5", "02");
	        else if (sub_flag != null && "08".equalsIgnoreCase(sub_flag))
	            vo.setString(ta + "z5", "08");
	        else
	            vo.setString(ta + "z5", "01");
	        
	        if ("q15".equalsIgnoreCase(ta))
	        {
	            vo.setString("q1517", "0");
	            if ("01".equals(vo.getString("q15z0")) && "03".equals(vo.getString("q15z5")))
	            {
	
	                float d_Count = 0;
	                AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
	                HashMap kqItem_hash = annualApply.count_Leave(sels);
	
	                if (KqParam.getInstance().isHoliday(this.frameconn, vo.getString("b0110"), sels))
	                {
	                    float[] holiday_rules = annualApply.getHoliday_minus_rule();// 年假假期规则
	                    d_Count = annualApply.getHistoryLeaveTime(vo.getDate("q15z1"), vo.getDate("q15z3"), vo.getString("a0100"), vo.getString("nbase"), (String) vo.getString("b0110"), kqItem_hash, holiday_rules);
	                    String history = annualApply.upLeaveManage(vo.getString("a0100"), vo.getString("nbase"), vo.getString("q1503"), OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm"), OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm"), d_Count, "1", (String) vo.getString("b0110"), kqItem_hash, holiday_rules);
	                    vo.setString("history", history);
	                }
	            }
	        } 
	        else if ("q11".equalsIgnoreCase(ta))
	        {
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
		                	 timeLen = annualApply.calcOverAppTimeLen(vo.getString("nbase"), vo.getString("a0100"), startDate, endDate, kqItemHash,  Integer.MAX_VALUE);
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
	            }
				String iftoRest = "";
				if(hm != null){
					iftoRest = (String) hm.get("IftoRest");
				}else{
					iftoRest = (String) this.getFormHM().get("IftoRest");
				}
				String iftoRestField = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
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
				// linbz 增加检查调休加班限额
	            KqOverTimeForLeaveBo overTimeForLeaveBo = new KqOverTimeForLeaveBo(frameconn, userView);
	            error = overTimeForLeaveBo.checkOvertimeForLeaveMaxHour(vo);
	            if(!StringUtils.isEmpty(error)) {
	            	throw new GeneralException(error);
	            }
	        }
	        
	        if ("03".equals(sub_flag))
	        {
	            vo.setDate(ta + "z7", OperateDate.getDateByFormat(new Date(), "yyyy-MM-dd HH:mm"));// 审批时间
	        }
	        
	        if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) 
			{
				vo.setString(appReaField, appReaCode);
			}
	        
	        ContentDAO dao = new ContentDAO(this.frameconn);
	        dao.addValueObject(vo);
	        
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
        	this.getFormHM().put("error", error);
        }
    }

    private String checkAppkeyid(String table)
    {
        IDGenerator idg = new IDGenerator(2, this.frameconn);
        String insertid = "";
        try
        {
            ContentDAO dao = new ContentDAO(this.frameconn);
            boolean iscorrect = false;
            while (!iscorrect)
            {
                insertid = idg.getId((table + "." + table + "01").toUpperCase());
                iscorrect = checkAppkeyid2(table, insertid, dao);
            }
        } catch (GeneralException e)
        {
            e.printStackTrace();
        }
        return insertid;
    }

    private boolean checkAppkeyid2(String table, String id, ContentDAO dao)
    {
        boolean iscorrect = true;
        RowSet rs = null;
        try
        {
            String sql = "select 1 from " + table + " where " + table + "01='" + id + "'";
            rs = dao.search(sql);
            if (rs.next())
                iscorrect = false;
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (rs != null)
                try
                {
                    rs.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }
        return iscorrect;
    }
}
