package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.yearwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.HashMap;

/**
 * SearchYearWorkTrans.java
 * Description: 国网显示我的年报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Jan 8, 2013 2:26:18 PM Jianghe created
 */
public class SearchYearWorkTrans extends IBusiness{
	
	public void execute() throws GeneralException  {
		try{
			String currentTime = "";
			String currentYear = "";
			String tableHtml = "";
			String returnUrl = "";
			String init = "";
			WorkDiaryBo bo = null;
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			String fromYearFlag = (String)rMap.get("fromYearFlag");
			if(rMap.get("init")!=null&& "init".equals((String)rMap.get("init")))
			{
				//从菜单
				currentYear = Calendar.getInstance().get(Calendar.YEAR)+"";
				this.userView.getHm().put("a0100", this.userView.getA0100());
				this.userView.getHm().put("nbase", this.userView.getDbname());
				//0 本人  1领导登录
				this.userView.getHm().put("isOwner", "0");
				this.userView.getHm().put("initflag", (String)rMap.get("init"));
			}else if(rMap.get("init")!=null&& "1".equals((String)rMap.get("init")))
			{
				//切换日期
				currentYear = (String)this.getFormHM().get("currentYear");
			}else if(rMap.get("init")!=null&& "2".equals((String)rMap.get("init"))){
				//从月报或日报返回
				currentYear = (String)rMap.get("currentYear");
			}
			else if(rMap.get("init")!=null&& "4".equals((String)rMap.get("init"))){
				//从员工日志进
				currentYear = (String)rMap.get("currentYear");
				returnUrl = (String)this.getFormHM().get("returnUrl");
				init="4";
				this.userView.getHm().put("a0100", (String)rMap.get("a0100"));
				this.userView.getHm().put("nbase", (String)rMap.get("nbase"));
				//0 本人  1领导登录
				this.userView.getHm().put("isOwner", "1");
				this.userView.getHm().put("initflag", (String)rMap.get("init"));
				this.userView.getHm().put("backUrl", returnUrl);
			}
			if(rMap.get("init")!=null&& "1".equals((String)rMap.get("init"))&&fromYearFlag!=null&& "1".equals(fromYearFlag)){
				currentYear = (String)this.getFormHM().get("currentYear");
				init="2";
			}
			bo = new WorkDiaryBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			currentTime = currentYear+"年";
			tableHtml = bo.getYearTableHtml(currentYear);
			this.getFormHM().put("currentYear", currentYear);
			this.getFormHM().put("currentTime", currentTime);
			this.getFormHM().put("tableHtml", tableHtml);
			this.getFormHM().put("init", init);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
