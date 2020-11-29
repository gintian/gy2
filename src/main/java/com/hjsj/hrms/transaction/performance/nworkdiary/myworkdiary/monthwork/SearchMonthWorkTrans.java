package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.monthwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * SearchMonthWorkTrans.java
 * Description: 国网显示我的月报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Jan 8, 2013 2:24:53 PM Jianghe created
 */
public class SearchMonthWorkTrans extends IBusiness{

	public void execute() throws GeneralException  {
		try{
			String currentTime = "";
			String currentYear = "";
			String currentMonth= "";
			String currentDay = "";
			String tableHtml = "";
			String returnUrl = "";
			String init = "";
			int thisYear = Calendar.getInstance().get(Calendar.YEAR);
			int thisMonth = (Calendar.getInstance().get(Calendar.MONTH)+1);
			int thisDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			String startHour = 0+"";
			String endHour = 23+"";
			WorkDiaryBo bo = null;
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			String fromYearFlag = (String)rMap.get("fromYearFlag");
			if(rMap.get("init")!=null&& "init".equals((String)rMap.get("init")))
			{
				//从菜单进
				currentYear = Calendar.getInstance().get(Calendar.YEAR)+"";
				currentMonth = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";
				currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"";
				this.userView.getHm().put("a0100", this.userView.getA0100());
				this.userView.getHm().put("nbase", this.userView.getDbname());
				//0 本人  1领导登录
				this.userView.getHm().put("isOwner", "0");
				this.userView.getHm().put("initflag", (String)rMap.get("init"));
			}else if(rMap.get("init")!=null&& "1".equals((String)rMap.get("init"))){
				//切换日期
				currentYear = (String)this.getFormHM().get("currentYear");
				currentMonth = (String)this.getFormHM().get("currentMonth");
				currentDay = (String)this.getFormHM().get("currentDay");
				
			}else if(rMap.get("init")!=null&& "2".equals((String)rMap.get("init"))){
				//从年报进
				currentYear = (String)rMap.get("currentYear");
				currentMonth = (String)rMap.get("currentMonth");
				currentDay = (String)rMap.get("currentDay");
				init="2";
				returnUrl = "/performance/nworkdiary/myworkdiary/yearwork.do?b_search=link&init=2&currentYear="+currentYear;
				this.getFormHM().put("returnUrl", returnUrl);
				this.userView.getHm().put("initflag", (String)rMap.get("init"));
			}else if(rMap.get("init")!=null&& "3".equals((String)rMap.get("init"))){
				//从日报返回
				currentYear = (String)rMap.get("currentYear");
				currentMonth = (String)rMap.get("currentMonth");
				currentDay = (String)rMap.get("currentDay");
			}else if(rMap.get("init")!=null&& "4".equals((String)rMap.get("init"))){
				//从员工日志进
				currentYear = (String)rMap.get("currentYear");
				currentMonth = (String)rMap.get("currentMonth");
				currentDay = (String)rMap.get("currentDay");
				if(currentDay==null|| "".equals(currentDay.trim())){
					currentDay = "01";
				}
				returnUrl = (String)this.getFormHM().get("returnUrl");
				this.userView.getHm().put("a0100", (String)rMap.get("a0100"));
				this.userView.getHm().put("nbase", (String)rMap.get("nbase"));
				init="2";
				//0 本人  1领导登录
				this.userView.getHm().put("isOwner", "1");
				this.getFormHM().put("returnUrl", returnUrl);
				this.userView.getHm().put("initflag", (String)rMap.get("init"));
				this.userView.getHm().put("backUrl", returnUrl);
			}
			if(rMap.get("init")!=null&& "1".equals((String)rMap.get("init"))&&fromYearFlag!=null&& "1".equals(fromYearFlag)){
				currentYear = (String)this.getFormHM().get("currentYear");
				currentMonth = (String)this.getFormHM().get("currentMonth");
				currentDay = (String)this.getFormHM().get("currentDay");
				init="2";
			}
			rMap.remove("fromYearFlag");
			rMap.remove("init");
			bo = new WorkDiaryBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			currentTime = currentYear+"年"+currentMonth+"月";
			tableHtml = bo.getTableHtml(currentYear,currentMonth,thisYear,thisMonth,thisDay);
			ArrayList hourList = bo.getHourList();
			String title="";
			String type="1";
			String content="";
			this.getFormHM().put("title", title);
			this.getFormHM().put("type", type);
			this.getFormHM().put("content", content);
			this.getFormHM().put("tableHtml", tableHtml);
			this.getFormHM().put("currentYear", currentYear);
			this.getFormHM().put("currentMonth", currentMonth);
			this.getFormHM().put("currentDay", currentDay);
			this.getFormHM().put("currentTime", currentTime);
			this.getFormHM().put("hourList", hourList);
			this.getFormHM().put("startHour", startHour);
			this.getFormHM().put("endHour", endHour);
			this.getFormHM().put("init", init);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
