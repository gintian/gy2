package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CollectWorkSummaryTrans extends IBusiness {

    public void execute() throws GeneralException {

        try {
            WorkPlanSummaryBo wp = new WorkPlanSummaryBo(userView, getFrameconn());
            WorkSummaryMethodBo wsmBo = new WorkSummaryMethodBo(this.userView, this.getFrameconn());
            String type = (String) this.getFormHM().get("type");
            String thisWorkSummary = (String) this.getFormHM().get("thisWorkSummary"); 
			String nextWorkSummary = (String) this.getFormHM().get("nextWorkSummary"); 
            String e01a1 = (String) this.getFormHM().get("e01a1");
            if (e01a1 != null && e01a1.trim().length() > 0) {
            	e01a1 = WorkPlanUtil.decryption(e01a1);
            }
            String e0122 = (String) this.getFormHM().get("e0122");
            if (e0122 != null && e0122.trim().length() > 0) {
            	e0122 = WorkPlanUtil.decryption(e0122);
            }
            String belong_type = (String) this.getFormHM().get("belong_type");
            if (!"team".equals(type) && !"sub_org".equals(type))
			{
            	type = "2".equals(belong_type) ?"org" : "person";
			}
            String nbase = this.userView.getDbname(); //人员库
            String a0100 = this.userView.getA0100();
            String summaryYear = (String) this.getFormHM().get("year");
            String summaryMonth = (String) this.getFormHM().get("month");
            String select = (String) this.getFormHM().get("week"); //选择的周，季度等
            String summaryCycle = (String) this.getFormHM().get("cycle");
            String cyclenow = (String) this.getFormHM().get("cyclenow");
            StringBuffer thisPlanTaskList = new StringBuffer(thisWorkSummary); //本周计划
            //String p0120 = ""; //下周计划
            String collecttype = (String) this.getFormHM().get("collecttype"); //汇总类型
            String[] summaryDates = wp.getSummaryDates(cyclenow,summaryYear, summaryMonth, Integer.parseInt(select));
			/***********************取参数结束*******************************/
            //自己区间的汇总
            if ("time".equals(collecttype ))
			{
            	thisPlanTaskList.append(wp.CollectByTime(cyclenow, summaryCycle, summaryYear, summaryMonth, select, belong_type, type, e0122, nbase, a0100));
            	this.getFormHM().put("thisPlanTaskList", thisPlanTaskList.toString());
            	this.getFormHM().put("nextPlanTaskList", nextWorkSummary);
			}else if ("person".equals(collecttype ))
			{
				ArrayList summarylist = wsmBo.CollectByPerson(nbase,a0100,e01a1,summaryDates[0], summaryDates[1], cyclenow,"");
				this.getFormHM().put("thisPlanTaskList", thisWorkSummary+summarylist.get(0));
				this.getFormHM().put("nextPlanTaskList", nextWorkSummary+summarylist.get(1));
				this.getFormHM().put("warn", summarylist.get(2));
			}else if ("org".equals(collecttype )) {
				ArrayList summarylist = wsmBo.CollectByOrg(nbase,a0100,e01a1,summaryDates[0], summaryDates[1], cyclenow);
				this.getFormHM().put("thisPlanTaskList", thisWorkSummary+summarylist.get(0));
				this.getFormHM().put("nextPlanTaskList", nextWorkSummary+summarylist.get(1));
				this.getFormHM().put("warn", summarylist.get(2));
			}
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
