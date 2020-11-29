package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant.Cycle;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BusinessSummaryTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			// 团队工作总结 or 下属工作总结
			String type = (String) this.getFormHM().get("type0");
			// 第几页
			int pagenum = Integer.parseInt((String) this.getFormHM().get("pagenum"));
			if (pagenum == 0) {
				pagenum = 1;
			}
			int hr_pagesize = Integer.parseInt((String) this.getFormHM().get("hr_pagesize"));
			String summaryCycle = (String) this.getFormHM().get("cycle");
			String summaryYear = (String) this.getFormHM().get("year");
			String summaryMonth = (String) this.getFormHM().get("month");
			String summaryWeek = (String) this.getFormHM().get("week");

			String para = (String) this.getFormHM().get("querypara");
			para = (para == null) ? "" : para;
			para = SafeCode.decode(para);
			// 联合查询条件
			String commonpara = (String) this.getFormHM().get("commonpara");
			commonpara = (commonpara == null) ? "" : commonpara;
			commonpara = SafeCode.decode(commonpara);
			commonpara = commonpara.replace('＝', '=');
			
			//需要显示的总结类型列表 haosl  start
			WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(frameconn,userView);
            List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
            this.getFormHM().put("summaryTypeJson",JSONArray.fromObject(configList).toString());
            ////需要显示的总结类型列表 haosl  end
            boolean flag = true;
            String defaultCycle = "";
            String monThcycle = "";
            String defaultMonth = String.valueOf(DateUtils.getMonth(new Date()));
            //启用的总结
            StringBuffer realSummaryCycle = new StringBuffer();
            for(HashMap<String, HashMap<String, String>> map : configList){
        		if(map.get("s0")!=null){//年总结
        			if(flag) {
	        			flag = false;
	        			defaultCycle = WorkPlanConstant.SummaryCycle.YEAR;
        			}
        			realSummaryCycle.append(WorkPlanConstant.SummaryCycle.YEAR+",");
        		}else if(map.get("s1")!=null){//半年总结
        			if(flag) {
	        			flag = false;
	        			defaultCycle = WorkPlanConstant.SummaryCycle.HALFYEAR;
        			}
        			realSummaryCycle.append(WorkPlanConstant.SummaryCycle.HALFYEAR+",");
        		}else if(map.get("s2")!=null){//季度总结
        			if(flag) {
	        			flag = false;
	        			defaultCycle = WorkPlanConstant.SummaryCycle.QUARTER;
        			}
        			realSummaryCycle.append(WorkPlanConstant.SummaryCycle.QUARTER+",");
        		}else if(map.get("s3")!=null){//月度总结
					HashMap temp = map.get("s3");
					if(flag) {
						flag = false;
						defaultCycle = WorkPlanConstant.SummaryCycle.MONTH;
					}
					monThcycle =(String)temp.get("cycle");
					realSummaryCycle.append(WorkPlanConstant.SummaryCycle.MONTH+",");
				}else if(map.get("s4")!=null){//周总结
					if(flag) {
						flag = false;
						defaultCycle = WorkPlanConstant.SummaryCycle.WEEK;
					}
					realSummaryCycle.append(WorkPlanConstant.SummaryCycle.WEEK+",");
				}
            }
            //增加条件，如果前台传过来的总结类型并没有启用，则默认显示启用的总结的第一个类型  haosl 2018-3-21
			if (StringUtils.isBlank(summaryCycle) || realSummaryCycle.indexOf(summaryCycle)==-1) {
				summaryCycle = defaultCycle;
			}
			
			if(StringUtils.isBlank(summaryCycle)){//程序执行到这，如果summaryCycle为空，那么提示用户启用工作总结
				throw new Exception("未启用任何类型的工作总结，暂无法查看！");
			}
			if(WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle)){//月报需要重新定位月份
				String currentMonth = DateUtils.getMonth(new Date())+"";
				if(( ","+monThcycle+",").indexOf(","+summaryMonth+",")>-1){

				}else if(( ","+monThcycle+",").indexOf(","+currentMonth+",")>-1){
					summaryMonth = currentMonth;
				}else{
					String[] cycleArr = monThcycle.split(",");
					if(cycleArr.length>0)
						summaryMonth = cycleArr[0];
				}

			}
			if(StringUtils.isBlank(summaryMonth)){
				summaryMonth = defaultMonth;
			}
			if(StringUtils.isBlank(summaryYear))
				summaryYear = String.valueOf(DateUtils.getYear(new Date()));
			if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle))
				this.getFormHM().put("typetitle", "工作周报");
			else if (WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle))
				this.getFormHM().put("typetitle", "工作月报");
			else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(summaryCycle))
				this.getFormHM().put("typetitle", "季度总结");
			else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(summaryCycle))
				this.getFormHM().put("typetitle", "半年总结");
			else if (WorkPlanConstant.SummaryCycle.YEAR.equals(summaryCycle))
				this.getFormHM().put("typetitle", "年度总结");

			// 判断，已提交，未提交，以打分
			String stateSign = (String) this.getFormHM().get("stateSign");
			stateSign = stateSign == null ? "" : stateSign;
			String monthOrWeek = (String) this.getFormHM().get("monthOrWeek");
			monthOrWeek = monthOrWeek == null ? "" : monthOrWeek;
			String nbase = this.userView.getDbname();
			String a0100 = this.userView.getA0100();

			WorkPlanUtil pb = new WorkPlanUtil(getFrameconn(), getUserView());
			WorkPlanSummaryBo wpsBo = new WorkPlanSummaryBo();
			WorkSummaryMethodBo wsmBo = new WorkSummaryMethodBo(this.userView, this.getFrameconn());
			// 计划周期 与 工作总结周期 进行转换
			String[] planCycles = { Cycle.Day, Cycle.WEEK, Cycle.MONTH, Cycle.QUARTER, Cycle.YEAR, Cycle.HALFYEAR };
			int cycle = Integer.parseInt(planCycles[Integer.parseInt(summaryCycle)]);
			if(!"".equals(monthOrWeek)){
				int[] weeks = pb.getLocationPeriod(cycle + "", Integer.parseInt(summaryYear), Integer.parseInt(summaryMonth));
				if (WorkPlanConstant.Cycle.WEEK.equals(cycle + "") && "week".equals(monthOrWeek)) {
					summaryMonth = String.valueOf(weeks[1]);
					if (null == summaryWeek || "".equals(summaryWeek.trim()))
						summaryWeek = String.valueOf(weeks[2]);
				}
//				else if (WorkPlanConstant.Cycle.HALFYEAR.equals(cycle + "") || WorkPlanConstant.Cycle.QUARTER.equals(cycle + "") || (WorkPlanConstant.Cycle.MONTH.equals(cycle + "") && "month".equals(monthOrWeek))) {
//					summaryMonth = String.valueOf(weeks[1]);
//					
//				}
			}
			// WorkPlanUtil workPlanUtil = new WorkPlanUtil(getFrameconn(),
			// this.userView);

			// 已批准
			int approveList = 0;
			// 未批准
			int notApproveList = 0;
			// 已打分
			int scoreList = 0;

			if (null == type || "".equals(type.trim())) {
				HashMap urlParam = (HashMap) this.getFormHM().get("requestPamaHM");
				type = (String) urlParam.get("type");

				if (null == type || "".equals(type.trim()))
					type = "team";
			}

			if (null == summaryWeek || "".equals(summaryWeek))
				summaryWeek = wpsBo.getCurCycleIndex(summaryCycle, summaryYear, summaryMonth, "1");

			String[] summaryDates = wpsBo.getSummaryDates(summaryCycle, summaryYear, summaryMonth, Integer.parseInt(summaryWeek));
			String startTime = summaryDates[0];
			String endTime = summaryDates[1];
			boolean iscommon = false;
			boolean isstatecommon = true;
			//if (commonpara != null && !"".equals(commonpara)) {
			if (!"".equals(commonpara)) {
				ArrayList paramList = wsmBo.setQueryText(commonpara);
				for (int i = 0; i < paramList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) paramList.get(i);
					String name = (String) bean.get("name");
					String value = (String) bean.get("value");
					if ("plan_status".equalsIgnoreCase(name)) {
						if (!"".equals(stateSign) && !stateSign.equals(value)) {
							isstatecommon = false;
						}
						else {
							stateSign = value;
							iscommon = true;
						}
						break;
					}
				}
			}
			ArrayList list = new ArrayList();
			//联合查询已经查询了状态时，再点不同的状态是没有查询的意义的，人数肯定是0
			if (isstatecommon) {
				if ("team".equalsIgnoreCase(type)) {
					if(nbase!=null){	//haosl
						list = wsmBo.selectTeamWeekly(nbase, a0100, startTime, endTime, summaryCycle, stateSign, para, commonpara, true, "");
					}
				}
				else if ("sub_org".equalsIgnoreCase(type)) {
					ArrayList e01a1list = new ArrayList();
					e01a1list = wsmBo.getHre01a1list(para, commonpara);
					list = wsmBo.getMySubDeptPerson(e01a1list, startTime, endTime, summaryCycle, stateSign, true);
				}
				this.getFormHM().put("DbNameMsg",wsmBo.getDbNameMsg());	//向前台发送人员库的设置信息		haosl 20160630
			}

			// （以提交，未提交，以打分）各自的人数
			if ("".equals(stateSign) || iscommon) {

				for (int j = 0; j < list.size(); j++) {
					HashMap map = new HashMap();
					map = (HashMap) list.get(j);

					if ("02".equals(map.get("p0115"))) {
						approveList += 1;
					}
					// 已批准
					if ("03".equals(map.get("p0115"))) {
						scoreList += 1;
					}

				}
				stateSign = "sign";
			}
			this.getFormHM().put("totalPeopleNumber", list.size() + "");
			this.getFormHM().put("approvePeopleNumber",approveList+scoreList+"");
			this.getFormHM().put("scorePeopleNumber", scoreList + "");

			this.getFormHM().put("stateSign", stateSign);

			if ((pagenum - 1) * hr_pagesize - list.size() == 0 || (pagenum - 1) * hr_pagesize > list.size()) {
				if (pagenum > 1)
					pagenum = pagenum - 1;
			}

			int toIndex = pagenum * hr_pagesize;
			if (toIndex > list.size())
				toIndex = list.size();
			this.getFormHM().put("list", list.subList((pagenum - 1) * hr_pagesize, toIndex));
			// 获取该月有几周
			int weeknum = wpsBo.getSummaryNum(summaryCycle, summaryYear, summaryMonth);
			this.getFormHM().put("weeknum", weeknum + "");
			if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
				this.getFormHM().put("weekstart", startTime);
				this.getFormHM().put("weekend", endTime);
			}
			ArrayList yearList = new ArrayList();
			for (int i = Integer.parseInt(summaryYear) - 1; i <= Integer.parseInt(summaryYear) + 1; i++) {
				yearList.add(i + "");
			}
			this.getFormHM().put("yearList", yearList + "");
			this.getFormHM().put("pagenum", pagenum + "");
			this.getFormHM().put("type", type);
			this.getFormHM().put("cycle", summaryCycle);
			this.getFormHM().put("year", summaryYear);
			this.getFormHM().put("month", summaryMonth);
			this.getFormHM().put("week", summaryWeek);

		}
		catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
