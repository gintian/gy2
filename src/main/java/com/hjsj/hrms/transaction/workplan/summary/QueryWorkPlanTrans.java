package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant.Cycle;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant.SummaryCycle;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;

/**
 * 获取工作任务
 * 
 * <p>Title: QueryWorkPlanTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-8-11 上午09:16:07</p>
 * @author guoby,szk
 * @version 1.0
 */

public class QueryWorkPlanTrans extends IBusiness {
	
	
	public void execute() throws GeneralException {
		
		try {
			// planTaskContent为，    = 5:本周总结为空， =10:下周周报为空， =15:两个都为空
			String planTaskContent = (String) this.getFormHM().get("planTaskContent"); 
			//  工作总结周期：0 、日报，2、周，3、月，4、季度，5、年，6、半年
			int summaryCycle = Integer.parseInt((String) this.getFormHM().get("cycle"));
			int year = Integer.parseInt((String) this.getFormHM().get("selectedYear"));
			String type = (String) this.getFormHM().get("type");
			int selectedMonth = Integer.parseInt((String) this.getFormHM().get("selectMonth"));
			int selectedWeek = Integer.parseInt((String) this.getFormHM().get("week"));
			int object_type = "person".equals(type) ? 1 : 2; //object_type 对象类型  1：人员计划  2：团队计划
			String nbase = (String) this.getFormHM().get("nbase");
			nbase = WorkPlanUtil.decryption(nbase);
			if (null == nbase || "".equals(nbase.trim()))
				nbase = this.userView.getDbname();
			
			String a0100 = (String) this.getFormHM().get("a0100");
			a0100 = WorkPlanUtil.decryption(a0100);
			if (null == a0100 || "".equals(a0100.trim()))
				a0100 = this.userView.getA0100();
			
			// 任务范围    0：所有   1：我负责的任务  2：我参与的任务
			String scope = (String) this.getFormHM().get("scope");
			
			PlanTaskTreeTableBo planTaskBo = new PlanTaskTreeTableBo(this.frameconn); 
			WorkPlanSummaryBo workSummaryBo = new WorkPlanSummaryBo();
			
			// 计划周期 与 工作总结周期 进行转换
			String[] planCycles = { Cycle.Day, Cycle.WEEK, Cycle.MONTH,Cycle.QUARTER, Cycle.YEAR, Cycle.HALFYEAR };
			int cycle = Integer.parseInt(planCycles[summaryCycle]);
			String object_id =  "person".equals(type) ? nbase + a0100 : WorkPlanUtil.decryption((String) this.getFormHM().get("e0122"));
			
			if(scope == null || "".equals(scope) && planTaskContent != null){
				String p0100 = (String) this.getFormHM().get("p0100"); 
				p0100 = WorkPlanUtil.decryption(p0100);
				String thisWorkSummary = (String) this.getFormHM().get("thisWorkSummary"); 
				String nextWorkSummary = (String) this.getFormHM().get("nextWorkSummary"); 
				int signValue = Integer.parseInt(planTaskContent);
				if(signValue >= 10){ // 下周计划
					signValue = signValue -6;
					int nextMonth = selectedMonth; // 下一周的月份数
					int nextWeek = selectedWeek;// 下一周的周数
					int nextYear = year;// 下一周的周数
					//取正确的下个区间
					//周
					if ( SummaryCycle.WEEK.equals(String.valueOf(summaryCycle))) {
						int weekNum =  workSummaryBo.getWeekNum(year, selectedMonth);
						if(weekNum == selectedWeek){
							nextMonth = nextMonth + 1;
							if (nextMonth==13) {
								nextMonth=1;
								nextYear +=1;
							}
							nextWeek = 1;
						}else{
							nextWeek = nextWeek + 1;
						}
					}
					//月
					if ( SummaryCycle.MONTH.equals(String.valueOf(summaryCycle))) {
						if (selectedMonth==12) {
							nextMonth=1;
							nextYear +=1;
						}else {
							nextMonth+=1;
						}
					}
					//季度
					if ( SummaryCycle.QUARTER.equals(String.valueOf(summaryCycle))) {
						if (4 == selectedWeek) {
							nextYear += 1;
							nextWeek = 1;
						}else {
							nextWeek = nextWeek + 1;
						}
					}
					//半年
					if ( SummaryCycle.HALFYEAR.equals(String.valueOf(summaryCycle))) {
						if (2 == selectedWeek) {
							nextYear += 1;
							nextWeek = 1;
						}else {
							nextWeek = nextWeek + 1;
						}
					}
					//年
					if ( SummaryCycle.YEAR.equals(String.valueOf(summaryCycle))) {
							nextYear += 1;
					}
					// 添加下周工作计划
					String[] nextDate = workSummaryBo.getSummaryDates(summaryCycle + "", nextYear+"", nextMonth+"", nextWeek);
					ArrayList nextPlanTaskList = planTaskBo.getTaskByCycle(object_type,object_id,cycle,nextYear,nextMonth,nextWeek, 0);
					
					//周
					if (nextPlanTaskList.size() == 0 && SummaryCycle.WEEK.equals(String.valueOf(summaryCycle))) {
						summaryCycle = Integer.parseInt(SummaryCycle.MONTH);
						//nextDate = workSummaryBo.getSummaryDates(summaryCycle + "", nextYear + "", nextMonth + "", nextWeek);
						nextPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, nextDate[0], nextDate[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					//月
					if (nextPlanTaskList.size() == 0 && SummaryCycle.MONTH.equals(String.valueOf(summaryCycle))) {
						nextWeek = (int) Math.ceil((double) nextMonth / 3.0);
						summaryCycle = Integer.parseInt(SummaryCycle.QUARTER);
						//nextDate = workSummaryBo.getSummaryDates(summaryCycle + "", nextYear + "", nextMonth + "", nextWeek);
						nextPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, nextDate[0], nextDate[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					//季度
					if (nextPlanTaskList.size() == 0 && SummaryCycle.QUARTER.equals(String.valueOf(summaryCycle))) {
						nextWeek = nextMonth > 6 ? 2 : 1; // 大于6为下半年
						summaryCycle = Integer.parseInt(SummaryCycle.HALFYEAR);
						//nextDate = workSummaryBo.getSummaryDates(summaryCycle + "", nextYear + "", nextMonth + "", nextWeek);
						nextPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, nextDate[0], nextDate[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					//半年
					if (nextPlanTaskList.size() == 0 && SummaryCycle.HALFYEAR.equals(String.valueOf(summaryCycle))) {
						summaryCycle = Integer.parseInt(SummaryCycle.YEAR);
						//nextDate = workSummaryBo.getSummaryDates(summaryCycle + "", nextYear + "", nextMonth + "", nextWeek);
						nextPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, nextDate[0], nextDate[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					
					if (nextPlanTaskList.size() != 0) {
						for (int i = 0; i < nextPlanTaskList.size(); i++) {
							DynaBean bean = (DynaBean) nextPlanTaskList.get(i);
							int level = Integer.parseInt((String)bean.get("_level"));
							String tt = "";
							for (int j = 2; j <= level; j++) {
								tt += "\t";
							}
							nextWorkSummary +="\n"+tt+ (String) bean.get("p0801");
						}
					}
					this.getFormHM().put("nextPlanTaskList", nextWorkSummary);
					//初始化 区间
					summaryCycle = Integer.parseInt((String) this.getFormHM().get("cycle"));
				}
				
				if(signValue >= 5){// 本周总结
					signValue = signValue -2;
					String[] date = workSummaryBo.getSummaryDates(summaryCycle+"", year+"", selectedMonth+"", selectedWeek);
					// 添加本周工作总结
					ArrayList thisPlanTaskList = planTaskBo.getTaskByCycle(object_type,object_id,cycle,year,selectedMonth,selectedWeek, 0);
					//周
					if (thisPlanTaskList.size() == 0 && SummaryCycle.WEEK.equals(String.valueOf(summaryCycle))) {
						summaryCycle = Integer.parseInt(SummaryCycle.MONTH);
						//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
						thisPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					//月
					if (thisPlanTaskList.size() == 0 && SummaryCycle.MONTH.equals(String.valueOf(summaryCycle))) {
						selectedWeek = (int) Math.ceil((double) selectedMonth / 3.0);
						summaryCycle = Integer.parseInt(SummaryCycle.QUARTER);
						//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
						thisPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					//季度
					if (thisPlanTaskList.size() == 0 && SummaryCycle.QUARTER.equals(String.valueOf(summaryCycle))) {
						selectedWeek = selectedMonth > 6 ? 2 : 1; // 大于6为下半年
						summaryCycle = Integer.parseInt(SummaryCycle.HALFYEAR);
						//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
						thisPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					//半年
					if (thisPlanTaskList.size() == 0 && SummaryCycle.HALFYEAR.equals(String.valueOf(summaryCycle))) {
						summaryCycle = Integer.parseInt(SummaryCycle.YEAR);
						//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
						thisPlanTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), 0);
					}
					
					
					if (thisPlanTaskList.size() != 0) {
						for (int i = 0; i < thisPlanTaskList.size(); i++) {
							DynaBean bean = (DynaBean) thisPlanTaskList.get(i);
							int level = Integer.parseInt((String)bean.get("_level"));
							String tt = "";
							for (int j = 2; j <= level; j++) {
								tt += "\t";
							}
							thisWorkSummary += "\n"+tt+(String) bean.get("p0801");
						}
					}
					this.getFormHM().put("thisPlanTaskList", thisWorkSummary);
				}
				
				// 保存本周工作总结和下周工作计划
				//workSummaryMethodBo.updateWorkSummary(p0100,WorkPlanUtil.decryption((String) this.getFormHM().get("e0122")), thisWorkSummary, nextWorkSummary, 0);
				
				this.getFormHM().put("nullSign",signValue + "");
				
			}else { // 根据时间查询出，我的所有任务/我负责的任务/我参与的任务/我委托的任务
				int scopeValue = Integer.parseInt(scope);
				String[] date = workSummaryBo.getSummaryDates(summaryCycle+"", year+"", selectedMonth+"", selectedWeek);
				//本期间按区间查
				ArrayList myWorkTaskList = planTaskBo.getTaskByCycle(object_type,object_id,cycle,year,selectedMonth,selectedWeek, scopeValue);
				// 查询计划 
				/**********************************************************************
				 * 工作周报：
				 * 查月度工作计划，如有任务，则显示月工作计划与本周相关的任务，结束
				 * 如没有月计划，则查找季度计划，如有任务，则显示季度计划与本周相关的任务，结束
				 * 如没有季度计划，则查找半年计划，如有任务，则显示半年计划与本周相关的任务，就结束
				 * 如没有半年计划，则查找年度计划，如有任务，则显示年度计划与本周相关的任务，结束。
				 * 工作月报：
				 * 查找季度计划，如有任务，则显示季度计划与本月相关的任务，结束
				 * 如没有季度计划，则查找半年计划，如有任务，则显示半年计划与本月相关的任务，就结束
				 * 如没有半年计划，则查找年度计划，如有任务，则显示年度计划与本月相关的任务，结束。 
				 * 季度总结：
				 * 查找半年计划，如有任务，则显示半年计划与本季度相关的任务，就结束
				 * 如没有半年计划，则查找年度计划，如有任务，则显示年度计划与本季度相关的任务，结束。
				 * 半年总结：
				 * 查找年度计划，如有任务，则显示年度计划与当前半年相关的任务，结束。
				 **********************************************************/
				//周
				if (myWorkTaskList.size() == 0 && SummaryCycle.WEEK.equals(String.valueOf(summaryCycle))) {
					summaryCycle = Integer.parseInt(SummaryCycle.MONTH);
					//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
					myWorkTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), scopeValue);
				}
				//月
				if (myWorkTaskList.size() == 0 && SummaryCycle.MONTH.equals(String.valueOf(summaryCycle))) {
					selectedWeek = (int) Math.ceil((double) selectedMonth / 3.0);
					summaryCycle = Integer.parseInt(SummaryCycle.QUARTER);
					//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
					myWorkTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), scopeValue);
				}
				//季度
				if (myWorkTaskList.size() == 0 && SummaryCycle.QUARTER.equals(String.valueOf(summaryCycle))) {
					selectedWeek = selectedMonth > 6 ? 2 : 1; // 大于6为下半年
					summaryCycle = Integer.parseInt(SummaryCycle.HALFYEAR);
					//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
					myWorkTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), scopeValue);
				}
				//半年
				if (myWorkTaskList.size() == 0 && SummaryCycle.HALFYEAR.equals(String.valueOf(summaryCycle))) {
					summaryCycle = Integer.parseInt(SummaryCycle.YEAR);
					//date = workSummaryBo.getSummaryDates(summaryCycle + "", year + "", selectedMonth + "", selectedWeek);
					myWorkTaskList = planTaskBo.getTaskByTime(object_type, object_id, date[0], date[1], Integer.parseInt(planCycles[summaryCycle]), scopeValue);
				}
				
				if(myWorkTaskList.size() > 0){
					for (int i = 0; i < myWorkTaskList.size(); i++) {
						DynaBean bean = (DynaBean) myWorkTaskList.get(i);
						PlanTaskTreeTableBo planTasBo = new PlanTaskTreeTableBo(this.frameconn,Integer.parseInt( bean.get("p0700").toString())); 
						// 时间格式
						String dateFomat = planTasBo.getTimeArrangeText(bean);
						
						bean.set("timeHorizon", dateFomat);
						String objectid="";
						if ("1".equals(bean.get("p0723"))){
						    objectid=(String)bean.get("nbase")+(String)bean.get("a0100");						    
						}
						else {
						    objectid=(String)bean.get("p0707");						    
						}
						bean.set("link_objectid", WorkPlanUtil.encryption(objectid));
						bean.set("link_p0700", WorkPlanUtil.encryption((String)bean.get("p0700")));
						bean.set("link_p0800", WorkPlanUtil.encryption((String)bean.get("p0800")));
						bean.set("link_p0723", WorkPlanUtil.encryption((String)bean.get("p0723")));
			
					}
				}

				this.getFormHM().put("myWorkTaskList", myWorkTaskList);
			
			}
				
		} catch (Exception e) {
			 e.printStackTrace();
	         throw GeneralExceptionHandler.Handle(e);
		}finally{
			
		}
	}
	
	
}
