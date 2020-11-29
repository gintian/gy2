package com.hjsj.hrms.module.workplan.weeklysummary.transaction;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hjsj.hrms.module.workplan.weeklysummary.businessobject.WeeklySummaryBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * 工作总结-周总结
 * @createtime dec 05, 2016 9:07:55 AM
 * @author chent
 */
@SuppressWarnings("serial")
public class WeeklySummaryTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			
			WeeklySummaryBo weeklySummaryBo = new WeeklySummaryBo(this.getFrameconn(), this.userView);// 工具类
			
			String type = (String) this.getFormHM().get("type");// 1：本期工作总结 2：本周工作日志 3：下期工作计划
			String option = (String) this.getFormHM().get("option");// 1：获取 2：添加 3：删除 4：更新
			
			/** ---------------本期工作总结---------------------- */
			if("1".equals(type)) {
				if("1".equals(option)){//get
					ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
					String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
					//31414 校验是否有p0100  
					if(StringUtils.isNotEmpty(_p0100)){
						int p0100 =  Integer.parseInt(_p0100);// 总结号
						data = weeklySummaryBo.getData_1(p0100);
					}
					
					this.getFormHM().put("data", data);
					
				} else if("2".equals(option)) {//add
					String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
					int p0100 =  Integer.parseInt(_p0100);// 总结号
					String p1901 = (String) this.getFormHM().get("p1901");// 任务类别
					String p1903 = (String) this.getFormHM().get("p1903");// 任务名称
					
					HashMap map = weeklySummaryBo.addRecord(type, p0100, p1901, p1903);
					this.getFormHM().put("errorcode", map.get("errorcode"));
					this.getFormHM().put("p1900", map.get("p1900"));
					
				} else if("3".equals(option)) {//delete
					ArrayList p1900s = (ArrayList) this.getFormHM().get("p1900s");
					
					String errorcode = "0";
					for(int i=0; i<p1900s.size(); i++){
						int p1900 = Integer.parseInt((String)p1900s.get(i));
						
						HashMap map = weeklySummaryBo.deleteRecord(p1900);
						if("1".equals((String)map.get("errorcode"))){
							errorcode = "1";
						}
					}
					this.getFormHM().put("errorcode", errorcode);
					
				} else if("4".equals(option)) {//update
					
					String p1900 = (String) this.getFormHM().get("p1900");
					String field = (String) this.getFormHM().get("field");
					String value = (String) this.getFormHM().get("value");
					value = value.replaceAll("<br>", "\n");
					
					HashMap map = weeklySummaryBo.updateRecord(Integer.parseInt(p1900), field, value);
		    		this.getFormHM().put("errorcode", map.get("errorcode"));
					
				}
				
			} 
			
			/** ---------------本周工作日志---------------------- */
			else if("2".equals(type)) {//本周工作日志
				if("1".equals(option)){//get
					ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
					String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
					if(StringUtils.isNotEmpty(_p0100)){
						int p0100 =  Integer.parseInt(_p0100);// 总结号
						data = weeklySummaryBo.getData_2(p0100);
					}
					
					this.getFormHM().put("data", data);
					
				} else if("2".equals(option)){//是否启用计时
					String nbase = WorkPlanUtil.decryption((String)this.getFormHM().get("nbase"));
					String a0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("a0100"));
					boolean isopentasktime = weeklySummaryBo.getIsOpentasktime(nbase, a0100);
					
					this.getFormHM().put("isopentasktime", isopentasktime);
				}
				
				
			} 
			
			/** ---------------下期工作计划---------------------- */
			else if("3".equals(type)) {
				if("1".equals(option)){//get
					ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
					String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
					if(StringUtils.isNotEmpty(_p0100)){
						int p0100 =  Integer.parseInt(_p0100);// 总结号
						data = weeklySummaryBo.getData_3(p0100);
					}
					this.getFormHM().put("data", data);
					
				} else if("2".equals(option)) {//add
					String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
					int p0100 =  Integer.parseInt(_p0100);// 总结号
					String p1901 = (String) this.getFormHM().get("p1901");// 任务类别
					String p1903 = (String) this.getFormHM().get("p1903");// 任务名称
					
					HashMap map = weeklySummaryBo.addRecord(type, p0100, p1901, p1903);
					this.getFormHM().put("errorcode", map.get("errorcode"));
					this.getFormHM().put("p1900", map.get("p1900"));
					
				} else if("3".equals(option)) {//delete
					ArrayList p1900s = (ArrayList) this.getFormHM().get("p1900s");
					
					String errorcode = "0";
					for(int i=0; i<p1900s.size(); i++){
						int p1900 = Integer.parseInt((String)p1900s.get(i));
						
						HashMap map = weeklySummaryBo.deleteRecord(p1900);
						if("1".equals((String)map.get("errorcode"))){
							errorcode = "1";
						}
					}
					this.getFormHM().put("errorcode", errorcode);
					
				} else if("4".equals(option)) {//update
					String p1900 = (String) this.getFormHM().get("p1900");
					String field = (String) this.getFormHM().get("field");
					String value = (String) this.getFormHM().get("value");
					
					HashMap map = weeklySummaryBo.updateRecord(Integer.parseInt(p1900), field, value);
		    		this.getFormHM().put("errorcode", map.get("errorcode"));
				}
			} 
			
			/** ---------------汇总---------------------- */
			else if("4".equals(type)) {
				
				String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
				int p0100_current_week =  Integer.parseInt(_p0100);// 总结号(周)
				
				String nbase = (String) this.getFormHM().get("nbase");//人员库
				nbase = WorkPlanUtil.decryption(nbase);
				if(StringUtils.isEmpty(nbase)){
					nbase = userView.getDbname();
				}
				String a0100 = (String) this.getFormHM().get("a0100");//人员编号
				a0100 = WorkPlanUtil.decryption(a0100);
				if(StringUtils.isEmpty(a0100)){
					a0100 = userView.getA0100();
				}
				
	            WorkPlanSummaryBo wsBo = new WorkPlanSummaryBo();
				String cycle = (String) this.getFormHM().get("cycle");//日志类型 1 周报
				int year = Integer.parseInt((String)this.getFormHM().get("year"));
	            int month = Integer.parseInt((String)this.getFormHM().get("month"));
	            int week = Integer.parseInt((String) this.getFormHM().get("week"));//第几周
	            
	            // 获取上一周
	            if(week > 1){
	            	week--;
	            } else if(week == 1){
	            	if(month > 1){
	            		month--;
	            		week = wsBo.getWeekNum(year, month);
	            	} else if(month == 1){
	            		year--;
	            		month = 12;
	            		week = wsBo.getWeekNum(year, month);
	            	}
	            }
	            
	            String[] summaryDates = wsBo.getSummaryDates(cycle, String.valueOf(year), String.valueOf(month), week);
	            String p0104 = summaryDates[0];//开始时间
	            String p0106 = summaryDates[1];//结束时间
	            
	            WorkSummaryMethodBo workSummaryMethodBo = new WorkSummaryMethodBo(this.userView, this.getFrameconn());
	            int p0100_pre_week = workSummaryMethodBo.getPeopleP0100(nbase, a0100, "", cycle, p0104, p0106, "1");
	            
	            weeklySummaryBo.collect(p0100_current_week, p0100_pre_week);
	            this.getFormHM().put("errorcode", "0");
	            
			}
			/** ---------------培训需求---------------------- */
			else if("5".equals(type)) {
				if("1".equals(option)){//get
					String value = "";
					String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
					if(StringUtils.isNotEmpty(_p0100)){
						int p0100 =  Integer.parseInt(_p0100);// 总结号
						String field = (String) this.getFormHM().get("field");
						value = weeklySummaryBo.getData_4(p0100, field);
					}
					this.getFormHM().put("data", value);
					
				} else if("2".equals(option)) {//update
					String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));
					int p0100 =  Integer.parseInt(_p0100);// 总结号
					String field = (String) this.getFormHM().get("field");
					String value = (String) this.getFormHM().get("value");
					
					HashMap map = weeklySummaryBo.updateContentValue(p0100, field, value);
		    		this.getFormHM().put("errorcode", map.get("errorcode"));
				}
				
			}
			/** ---------------工作任务子集---------------------- */
			else if("6".equals(type)) {
				String nbase = WorkPlanUtil.decryption((String)this.getFormHM().get("nbase"));
				String a0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("a0100"));
				
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				data = weeklySummaryBo.getE01a1PlanTask(nbase, a0100);
				this.getFormHM().put("data", data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
