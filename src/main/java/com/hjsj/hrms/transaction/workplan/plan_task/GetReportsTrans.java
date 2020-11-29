package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:查找某个人一段时期内报告</p>
 * <p>Description:查找某个人一段时期内报告</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-8-14:下午13:49:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class GetReportsTrans extends IBusiness {

	private static final long serialVersionUID = -8651211358171550787L;

	public void execute() throws GeneralException {
		try {
			reports();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (frowset != null) {
				try {
					frowset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** 查勘报告 */
	private void reports() throws Exception {
		String p0700 = formHM.get("p0700") == null ? "" : formHM.get("p0700") + ""; // 计划id
		String p0723 = formHM.get("p0723") == null ? "" : formHM.get("p0723") + ""; // 计划类型
		String objectid = formHM.get("objectid") == null ? "" : formHM.get("objectid") + ""; // 对象id: usr00000019| 010101
		
		// 解码
		objectid = WorkPlanUtil.decryption(objectid);
		p0700 = WorkPlanUtil.decryption(p0700);
		p0723 = WorkPlanUtil.decryption(p0723);
		objectid = "".equals(objectid) ? userView.getDbname() + userView.getA0100() : objectid;//对象id如果为空，则用当前登陆人所在的库前缀和人员编号拼接成
		int action = formHM.get("action") == null ? 0 : Integer.parseInt(formHM.get("action") + ""); // -1:前一段时期, 1:后一段时期, 0:默认时期
		int state = formHM.get("cycle") == null ? 0 : Integer.parseInt(formHM.get("cycle") + ""); // 日志类型(0=日报, 1=周报, 2=月报, 3=季报, 4=年报, 5=半年报)
		int period_year = formHM.get("period_year") == null ? 0 : Integer.parseInt(formHM.get("period_year") + ""); // 计划所处年份
		int period_month=0;
		if(formHM.get("period_month") != null && !"null".equals(formHM.get("period_month")+"")&& !"".equals(formHM.get("period_month")+"")){
			 period_month=Integer.parseInt(formHM.get("period_month") + "");
		}
		int period_type = formHM.get("period_type") == null ? 0 : Integer.parseInt(formHM.get("period_type") + ""); // 计划类型
		ContentDAO dao = new ContentDAO(frameconn);
		WorkPlanUtil wputil = new WorkPlanUtil(frameconn, userView);
		PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
		WorkPlanSummaryBo wpsBo = new WorkPlanSummaryBo(this.userView,this.frameconn);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar c = Calendar.getInstance();
		String sql = "";
		/* ################################## 确定起止时间 ##################################### */
		Date start = null; // 查询时间区间的起始
		Date end = null; // 查询时间区间的结尾
		
		int year = period_year; // 计划所处的年份
		int interval = 0; // 上层时间区间: 周报时为月, 月报时为季度,...

		if (action == 0) { // 默认时间

			switch (state) {
				case 1: { // 周报
					interval=getInterval(state, year, period_type, period_month, c);
					break;
				}
				case 2: { // 月报
					interval=getInterval(state,year, period_type, period_month, c);
					break;
				}
				case 3: { // 季报
					interval = year == c.get(Calendar.YEAR) ? (int) Math.round(c.get(Calendar.MONTH) / 3 + 1) : year;
					break;
				}
				case 4: { // 年报
					interval = year == c.get(Calendar.YEAR) ? (int) Math.round(c.get(Calendar.MONTH) / 3 + 1) : year;
					break;
				}
				case 5: { // 半年报
					interval = year == c.get(Calendar.YEAR) ? (int) Math.round(c.get(Calendar.MONTH) / 3 + 1) : year;
					break;
				}
				default: throw new Exception("尚不支持该周期");
			}
		} else if (action == -1) {
			String currPeriod = formHM.get("currPeriod") + ""; // 当前已查询时间段,默认时间段的查询不关心这个参数
			year = Integer.parseInt(currPeriod.substring(0, 4));
			switch (state) {
				case 1: { // 周报:2014年8月
					interval = Integer.parseInt(currPeriod.substring(5, currPeriod.length() - 1));
					if (interval == 1) { // 已经是1月了
						interval = 12;
						year -= 1;
					} else {
						interval -= 1;
					}
					break;
				}
				case 2: { // 月报:2014年一季度
					interval = getDigit(currPeriod.substring(5, 6));
					if (interval == 1) { // 已经是1季度了
						interval = 4;
						year--;
					} else {
						interval--;
					}
					break;
				}
				case 3: { // 季报:2014年
					interval = Integer.parseInt(currPeriod.substring(0, 4)) - 1;
					year--;
					break;
				}
				case 4: { // 年报:2014年
					interval = Integer.parseInt(currPeriod.substring(0, 4)) - 1;
					year--;
					break;
				}
				case 5: { // 半年报:2014年
					interval = Integer.parseInt(currPeriod.substring(0, 4)) - 1;
					year--;
					break;
				}
				default: throw new Exception("尚不支持该周期");
			}
		} else if (action == 1) { // 后一段时期
			String currPeriod = formHM.get("currPeriod") + ""; // 当前已查询时间段,默认时间段的查询不关心这个参数:2014年8月
			year = Integer.parseInt(currPeriod.substring(0, 4));
			switch (state) {
				case 1: { // 周报:2014年8月
					interval = Integer.parseInt(currPeriod.substring(5, currPeriod.length() - 1));
					
					if (interval == 12) { // 已经是12月了
						interval = 1;
						year += 1;
					} else {
						interval += 1;
					}
					break;
				}
				case 2: { // 月报:2014年一季度
					interval = getDigit(currPeriod.substring(5, 6));
					if (interval == 4) { // 已经是4季度了
						interval = 1;
						year++;
					} else {
						interval++;
					}
					break;
				}
				case 3: { // 季报:2014年
					interval = Integer.parseInt(currPeriod.substring(0, 4)) + 1;
					year++;
					break;
				}
				case 4: { // 年报:2014年
					interval = Integer.parseInt(currPeriod.substring(0, 4)) + 1;
					year++;
					break;
				}
				case 5: { // 半年报:2014年
					interval = Integer.parseInt(currPeriod.substring(0, 4)) + 1;
					year++;
					break;
				}
				default: throw new Exception("尚不支持该周期");
			}
		}
		
		// start, end
		switch (state) {
			case 1: { // 周报:2014年8月
				int weeknum = wpsBo.getSummaryNum(WorkPlanConstant.SummaryCycle.WEEK, year + "", interval + ""); // 确定时间段包含多少周
				
				// 第一周周一
				String[] first = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.WEEK, year + "", interval + "", 1);
				start = sdf.parse(first[0]);
				
				// 最后一周周末
				String[] last = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.WEEK, year + "", interval + "", weeknum);
				end = sdf.parse(last[1]);
				break;
			}
			case 2: { // 月报:2014年一季度
				// 第一月月初
				String[] first = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.MONTH, year + "", String.valueOf(interval * 3 - 2), interval * 3 - 2);
				start = sdf.parse(first[0]);
				
				// 最后一月月末
				String[] last = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.MONTH, year + "", String.valueOf(interval * 3), interval * 3);
				end = sdf.parse(last[1]);
				break;
			}
			case 3: { // 季报:2014年
				// 第一季季初
				String[] first = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.QUARTER, year + "", interval + "", 1);
				start = sdf.parse(first[0]);
				
				// 最后一季季末
				String[] last = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.QUARTER, year + "", interval + "", 4);
				end = sdf.parse(last[1]);
				break;
			}
			case 4: { // 年报:2014年
				// 年初
				String[] first = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.YEAR, year + "", interval + "", year);
				start = sdf.parse(first[0]);
				
				// 年末
				String[] last = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.YEAR, year + "", interval + "", year);
				end = sdf.parse(last[1]);
				break;
			}
			case 5: { // 半年报:2014年
				// 上半年处
				String[] first = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.YEAR, year + "", interval + "", 1);
				start = sdf.parse(first[0]);
				
				// 下半年末
				String[] last = wpsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.YEAR, year + "", interval + "", 2);
				end = sdf.parse(last[1]);
				break;
			}
			default: throw new Exception("尚不支持该周期");
		}
		
		/** ################################## 查询 ##################################### */
		// 审批标识(p0115)：01=起草, 02=报批, 03=已批, 07=驳回
		
		if ("1".equals(p0723)) { // 个人计划查询个人周报
			sql = "SELECT * FROM P01 WHERE p0104>=? AND p0106<=? AND nbase=? AND a0100=? AND state=? AND (belong_type IS NULL OR belong_type=0) ORDER BY p0104 ASC";
			frowset = dao.search(sql, Arrays.asList(new Object[] {
				new java.sql.Date(start.getTime()),
				new java.sql.Date(end.getTime()),
				objectid.substring(0, 3),
				objectid.substring(3),
				new Integer(state)
			}));
		} else if ("2".equals(p0723)) { // 团队计划
			sql = "SELECT * FROM P01 WHERE p0104>=? AND p0106<=? AND e0122=? AND state=? AND belong_type=2 ORDER BY p0104 ASC";
			frowset = dao.search(sql, Arrays.asList(new Object[] {
				new java.sql.Date(start.getTime()),
				new java.sql.Date(end.getTime()),
				objectid,
				new Integer(state)
			}));
		}
		
		String nbase = null;
		String a0100 = null;
		if ("2".equals(p0723)) { // 团队计划查询相应团队的负责人
			String director = wputil.getFirstDeptLeaders(objectid); // 当前计划位团队计划时有效，部门负责人
			if (director == null || "".equals(director)) {
				nbase = "";
				a0100 = "";
			} else {
				nbase = director.substring(0, 3);
				a0100 = director.substring(3);
			}
		} else if ("1".equals(p0723)) { // 个人计划
			nbase = objectid.substring(0, 3);
			a0100 = objectid.substring(3);
		}
		
		SimpleDateFormat cnSdf = new SimpleDateFormat("MM月dd日");
		List reportsApproved = new ArrayList(); // 数据库中存在的已批的记录,数据库中会出现断层，即某一周的记录不存在
		while (frowset !=null && frowset.next()) {
			int p0100 = frowset.getInt("p0100"); // 主键
			if( !wpsBo.checkIsCanReadSummary(p0100)){
			    continue;
			}
			
			// 非提交的总结且查看人不是作者本人则不进行任何操作， 工作总结审批标识: 01=起草, 02=报批, 03=已批, 07=驳回
			String p0115 = WorkPlanUtil.nvl(frowset.getString("p0115"), "");
			if ("01".equals(p0115)) { // 起草状态的总结除本人外，没人看得见
				if (!(userView.getDbname() + userView.getA0100()).equals(nbase + a0100)) 
					continue;
			}
			
			Date p0104 = frowset.getDate("p0104"); // 开始时间
			Date p0106 = frowset.getDate("p0106"); // 结束时间
			String p0109 = WorkPlanUtil.nvl(frowset.getString("p0109"), ""); // 工作总结
			String p0120 = WorkPlanUtil.nvl(frowset.getString("p0120"), ""); // 下期计划
			int time = frowset.getInt("time"); // 周报时为月内的周数, 月报时为年内的月数
			int score = frowset.getInt("score"); // 分数
			
			if (c.getTime().compareTo(p0104) >= 0) { // 开始时间在今日或今日之前
				LazyDynaBean report = new LazyDynaBean();
				report.set("hasSummary", "true");
				//计划或者总结内容可能含有“\”的有特殊含义的字符，需要加密处理 haosl 2017-12-08
				report.set("summary",SafeCode.encode(p0109));
				report.set("plan", SafeCode.encode(p0120));
				report.set("score", score + "");
				report.set("typeDesc", getStateDesc(state));
				report.set("p0100", p0100 + "");
				report.set("index", time + "");
				
				// 查看的连接
				StringBuffer url = new StringBuffer();
				url.append("/workplan/work_summary.do?b_query=link");
				url.append("&cycle=").append(state);
				url.append("&year=").append(year);
				switch (state) {
					case 1: url.append("&month=").append(interval); break; // 周报
					case 2: url.append("&month=").append(time); break; // 月报
					case 3: break; // 季报
					case 4: break; // 年报
					case 5: break; // 半年报
					default: ;
				}
				url.append("&week=").append(time);
				url.append("&nbase=").append(WorkPlanUtil.encryption(nbase));
				url.append("&a0100=").append(WorkPlanUtil.encryption(a0100));
				if ("1".equals(p0723)) { // 个人计划,objectid为人员id
					url.append("&belong_type=").append("0");
					url.append("&type=").append("person");
				} else if ("2".equals(p0723)) { // 团队计划,objectid为部门id
					url.append("&belong_type=").append("2");
					url.append("&e0122=").append(WorkPlanUtil.encryption(objectid));
					url.append("&type=").append("org");
				}
				report.set("viewUrl", url.toString());
				
				// 报告所处时间的中文描述
				StringBuffer period = new StringBuffer();
				switch (state) {
					case 1: {
						period.append("第" + getChineseNumber(time) + "周");
						period.append("(").append(cnSdf.format(p0104)).append(" ~ ").append(cnSdf.format(p0106)).append(")");
						break;
					}
					case 2: period.append(time + "月"); break;
					case 3: {
						period.append("第" + getChineseNumber(time) + "季度");
						period.append("(").append(cnSdf.format(p0104)).append(" ~ ").append(cnSdf.format(p0106)).append(")");
						break;
					}
					case 4: {
						period.append(year + "年");
						break;
					}
					case 5: {
						period.append((time == 1 ? "上" : "下") + "半年");
						break;
					}
					default: ;
				}
				report.set("period", period.toString());
				
				reportsApproved.add(report);
			}
		}
		
		// 当月所有周报，包括未提交的
		List reports = new ArrayList(); // 会把数据出现断层的(中间部分时间段没有记录)报告补全
		int len = 0; // 周报时为当月周数，月报时为3(季度月数)
		switch (state) {
			case 1: { // 周报，确定时间段包含多少周
				len = wpsBo.getSummaryNum(WorkPlanConstant.SummaryCycle.WEEK, year + "", interval + "");
				break;
			}
			case 2: len = 3; break;
			case 3: len = 4; break;
			case 4: len = 1; break;
			case 5: len = 2; break;
			default: len = 4;
		}
		for (int i = 1; i <= len; i++) {
			int index = 0;
			int month = 0; // 周报、月报时用
			switch (state) {
				case 1: {
					index = i;
					month = interval;
					break; // 周
				}
				case 2: {
					index = i + 3 * (interval - 1);
					month = index;
					break; // 月
				}
				case 3: index = i; break; // 季
				case 4: index = i; break; // 年
				case 5: index = i; break; // 半年
				default: index = i;
			}
			
			LazyDynaBean bean =  get(reportsApproved, "index", index + "");
			if (bean == null) {
				String[] timeRange = wpsBo.getSummaryDates(state + "", year + "", month + "", index);
				if (sdf.parse(timeRange[0]).compareTo(c.getTime()) >=0) { // 月报开始时间在今日或今日之后
					continue;
				}
				
				LazyDynaBean report = new LazyDynaBean();
				
				report.set("hasSummary", "false");
				report.set("typeDesc", getStateDesc(state));
				report.set("index", index + "");
				
				String type = null;
				// 查看周报的连接
				StringBuffer url = new StringBuffer();
				url.append("/workplan/work_summary.do?b_query=link");
				url.append("&cycle=").append(state);
				url.append("&year=").append(year);
				switch (state) {
					case 1: { // 周
						url.append("&month=").append(month);
						url.append("&week=").append(index);
						break;
					}
					case 2: { // 月
						url.append("&month=").append(month);
						url.append("&week=").append(index);
						break;
					}
					case 3: { // 季
						url.append("&week=").append(index);
						break;
					}
					case 4: { // 年
						break;
					}
					case 5: { // 半年
						url.append("&week=").append(index);
						break;
					}
					default: ;
				}
				url.append("&nbase=").append(WorkPlanUtil.encryption(nbase));
				url.append("&a0100=").append(WorkPlanUtil.encryption(a0100));
				if ("1".equals(p0723)) { // 个人计划,objectid为人员id
					url.append("&belong_type=").append("0");
					type = "person";
				} else if ("2".equals(p0723)) { // 团队计划,objectid为部门id
					url.append("&belong_type=").append("2");
					url.append("&e0122=").append(WorkPlanUtil.encryption(objectid));
					type = "org";
				}
				url.append("&type=").append(type);
				report.set("viewUrl", url.toString());
				
				// 提醒的连接和提醒文字
				if (isMyReport(Integer.parseInt(p0723), objectid)) { // 自己查看自己
					report.set("remindText", "去写工作总结");
					report.set("remindUrl", url.toString());
					report.set("type", "url"); // 类型为链接，表示跳转至总结界面填写总结
				} else if (isMySubordinate(Integer.parseInt(p0723), objectid)) { // 上级领导查看
					StringBuffer js = new StringBuffer();
					js.append("type=").append(type);
					js.append("`cycle=").append(state);
					js.append("`year=").append(year);
					js.append("`month=").append(month);
					js.append("`week=").append(index);
					js.append("`a0100=").append(WorkPlanUtil.encryption(nbase + a0100));
					js.append("`e0122=").append("1".equals(p0723) ? WorkPlanUtil.encryption(objectid) : "");
					report.set("remindUrl", js.toString());
					report.set("remindText", "提醒写工作总结");
					report.set("type", "param"); // 类型为参数，表示需要调用发送提醒邮件函数
				} else { // 无关紧要的人查看
//					report.set("remindText", "");
//					report.set("remindUrl", "javascript:void(0)");
					continue;
				}
				
				// 报告所处时间的中文描述
				StringBuffer period = new StringBuffer();
				switch (state) {
					case 1: {
						period.append("第" + getChineseNumber(index) + "周");
						period.append("(").append(cnSdf.format(sdf.parse(timeRange[0]))).append(" ~ ").append(cnSdf.format(sdf.parse(timeRange[1]))).append(")");
						break;
					}
					case 2: period.append(index + "月"); break;
					case 3: {
						period.append("第" + getChineseNumber(index) + "季度");
						period.append("(").append(cnSdf.format(sdf.parse(timeRange[0]))).append(" ~ ").append(cnSdf.format(sdf.parse(timeRange[1]))).append(")");
						break;
					}
					case 4: {
						period.append(year + "年度");
						break;
					}
					case 5: {
						period.append((index == 1 ? "上" : "下") + "半年");
						break;
					}
					default: ;
				}
				report.set("period", period.toString());

				reports.add(report);
			} else {
				reports.add(bean);
			}
		}
		
		formHM.put("reports", reports);

		// 报告作者
		RecordVo authorVo = ptbo.getPersonByObjectId(nbase + a0100);
		if (authorVo == null) {
			throw new Exception("查询失败");
		}
		String fullName=authorVo.getString("a0101");
		String deptDesc=wputil.getOrgDesc(objectid);
		if("2".equals(p0723)){
			fullName=deptDesc+"("+fullName+")";
		}
		LazyDynaBean author = new LazyDynaBean();
		author.set("fullName", fullName);
		author.set("stateDesc", getStateDesc(state));
		author.set("period", getTimeDesc(state, year, interval));
		author.set("photo", new WorkPlanBo(frameconn, userView).getPhotoPath(authorVo.getModelName().substring(0, 3), authorVo.getString("a0100")));
		formHM.put("author", author);
		
	}
	
	/**
	 * 是不是我写报告，即读者与作者的关系
	 * @param p0723 计划类型
	 * @param objectid 
	 */
	private boolean isMyReport(int p0723, String objectid) {
		if (p0723 == 1) { // 个人计划
			return objectid.equalsIgnoreCase(userView.getDbname() + userView.getA0100());
		} else if (p0723 == 2) { // 团队计划
			List depts = new WorkPlanUtil(frameconn, userView).getDeptList(userView.getDbname(), userView.getA0100());
			return get(depts, "b0110", objectid) != null;
		}
		
		return false;
	}
	
	/** 是不是我的下属或下属负责的部门 */
	private boolean isMySubordinate(int p0723, String objectid) {
		if (p0723 == 1) { // 个人任务
			return new WorkPlanUtil(frameconn, userView).isMyTeamPeople(objectid.substring(0, 3), objectid.substring(3)); // 是不是我下属负责的计划
		} else if (p0723 == 2) { // 团队计划
			return new WorkPlanUtil(frameconn, userView).isMyTeamDept(objectid); // 是不是我下属负责的部门
		}
		
		return false;
	}
	
	/** 在LazyDynaBean的集合中查询{key:value}的bean */
	private LazyDynaBean get(List list, String key, Object value) {
		for (int i = 0, len = list.size(); i < len; i++) {
			LazyDynaBean bean = (LazyDynaBean) list.get(i);
			if (value.equals(bean.get(key))) {
				return bean;
			}
		}
		return null;
	}
	
	/** 获得日志类型的文字描述 */
	private String getStateDesc(int state) {
		switch (state) {
			case 0 : return "日";
			case 1 : return "周";
			case 2 : return "月";
			case 3 : return "季";
			case 4 : return "年";
			case 5 : return "半年";
			default : return "";
		}
	}
	
	/** 获得日志类型的文字描述
	 * @param state 报告类型
	 * @param planYear 报告所处年份
	 * @param num 报告所在周期的索引
	 * @return
	 */
	private String getTimeDesc(int state, int planYear, int num) {
		switch (state) {
			case 0 : return planYear + "年" + num + "日";
			case 1 : return planYear + "年" + num + "月";
			case 2 : return planYear + "年" + getChineseNumber(num) + "季度";
			case 3 : return planYear + "年";
			case 4 : return planYear + "年";
			case 5 : return planYear + "年";
			default : return "";
		}
	}
	
	/** 将数字转换成对应的汉字描述 */
	private String getChineseNumber(int num) {
		switch (num) {
			case 1 : return "一";
			case 2 : return "二";
			case 3 : return "三";
			case 4 : return "四";
			case 5 : return "五";
			default : return "";
		}
	}
	
	/** 将中文数字转换成阿拉伯数字 */
	private int getDigit(String cnChar) {
		if ("一".equals(cnChar)) {
			return 1;
		} else if ("二".equals(cnChar)) {
			return 2;
		} else if ("三".equals(cnChar)) {
			return 3;
		} else if ("四".equals(cnChar)) {
			return 4;
		} else {
			return 0;
		}
	}
	/**
	 * 在查看周报时获取interval
	 * @param year
	 * @param period_type
	 * @param period_month
	 * @param c
	 * @return
	 */
	private int getInterval(int state,int year,int period_type,int period_month,Calendar c ){
		int interval = 0;
		if(year >= c.get(Calendar.YEAR)){//查看当前年的各种计划
							switch (period_type){
							case 1:{
								if(state==1){
									if(state==1){
										interval = c.get(Calendar.MONTH) + 1 ;
									}else if(state==2){
										interval=(int) Math.round(c.get(Calendar.MONTH) / 3 + 1);
									}
								}else if(state==2){
									interval=(int) Math.round(c.get(Calendar.MONTH) / 3 + 1);
								}
								break;
							}//年计划
							case 2:{
								if(state==1){
									if(period_month==1){//当前是上半年计划
										if(c.get(Calendar.MONTH)<=5){//当前处于上半年
											interval = c.get(Calendar.MONTH) + 1 ;	
										}else{
											interval =6;
										}
									}else if(period_month==2){//当前是下半年计划
										if(c.get(Calendar.MONTH)>5){//当前处于下半年
											interval = c.get(Calendar.MONTH) + 1 ;	
										}else{
											interval =7;
										}
									}
								}else if(state==2){
									if(period_month==1){
										if(c.get(Calendar.MONTH)<=5){//当前处于上半年
											interval=(int) Math.round(c.get(Calendar.MONTH) / 3 + 1);
										}else{
											interval =2;
										}
									}else if(period_month==2){
										if(c.get(Calendar.MONTH)>5){//当前处于下半年
											interval = c.get(Calendar.MONTH) + 1 ;	
										}else{
											interval =3;
										}
									}
								}
								break;
								
							}//半年计划
							case 3:{
								if(state==1){
									if(period_month==1){//查看一季度计划
										if(c.get(Calendar.MONTH)<=2){
											interval =  c.get(Calendar.MONTH) + 1;
										}else{
											interval=3;
										}
									}else if(period_month==2){//二季度计划
										if(c.get(Calendar.MONTH)>2&&c.get(Calendar.MONTH)<=5){
											interval = c.get(Calendar.MONTH) + 1 ;
										}else if(c.get(Calendar.MONTH)<=3){
											interval=4;
										}else{
											interval=6;
										}
									}else if(period_month==3){//三季度计划
										if(c.get(Calendar.MONTH)<=5){
											interval=7;
										}else if(c.get(Calendar.MONTH)>5&&c.get(Calendar.MONTH)<=8){
											interval =  c.get(Calendar.MONTH) + 1 ;
										}else{
											interval=9;
										}
									}else if(period_month==4){//四季度计划
										if(c.get(Calendar.MONTH)<=8){
											interval=10;
										}else{
											interval =  c.get(Calendar.MONTH) + 1 ;
										}
									}
								}else if(state==2){
									if(period_month==1){//查看一季度计划
										interval=1;
									}else if(period_month==2){//二季度计划
										interval=2;
									}else if(period_month==3){//三季度计划
										interval=3;
									}else if(period_month==4){//四季度计划
										interval=4;
									}
								}
								break;
							}//季度计划
							case 4:{
								if(state==1){
									interval=period_month;
								}else if(state==2){
									 if(period_month<=3){
								    		interval=1;
								        }else if(period_month>3&&period_month<=6){
								        	interval=2;
								    	}else if(period_month>6&&period_month<=9){
								    		interval=3;
								    	}else{
								    		interval=4;
								    	} 
								}
								break;//月计划
							}
							case 5:{
								if(state==1){
									interval=period_month;
								}else if(state==2){
									interval=(int) Math.round(c.get(Calendar.MONTH) / 3 + 1);
								}
								break;
							}//周计划
							}
					}else if(year < c.get(Calendar.YEAR)){//查看当年以前的各种计划
						  switch (period_type){
						  case 1:{
							  if(state==1){
							       interval = 12;
							     }else if(state==2){
							       interval = 4;
							     }
							   break;//年计划
						  }
						  case 2:{
							  if(state==1){
								  if(period_month==1){//当前是上半年计划
									  interval=6;
								  }else if(period_month==2){
									  interval=12;//当前是下半年计划
								  }
							  }else if(state==2){
								  if(period_month==1){//上半年
							    		interval=2;
							    	}else if(period_month==2){//下半年
							    		interval=4;
							    	}
							  }
							  break;
						  }//半年计划
						 case 3:{
							 if(state==1){
								 if(period_month==1){
									 interval=3;
								 }else if(period_month==2){
									 interval=6;
								 }else if(period_month==3){
									 interval=9;
								 }else if(period_month==4){
									 interval=12;
								 } 
							 }else if(state==2){
								 if(period_month==1){//一季度
						    		interval=1;
						    	 }else if(period_month==2){//二季度
						    		interval=2;
						    	 }else if(period_month==3){//三季度
						    		interval=3;
						    	 }else if(period_month==4){//四季度
						    		interval=4;
						    	 }
							 }
							 break;
						 }//季度计划
						 case 4:{
							 if(state==1){
								interval=period_month;
							 }else if(state==2){
								 if(period_month<=3){
							    		interval=1;
							        }else if(period_month>3&&period_month<=6){
							        	interval=2;
							    	}else if(period_month>6&&period_month<=9){
							    		interval=3;
							    	}else{
							    		interval=4;
							    	} 
							 }
							break; 
						 }//月计划
						 case 5:{
							 if(state==1)
							  {
							  interval=period_month;
							  }
							break;
							}//周计划
					  }
					}
		return interval;
	}

}