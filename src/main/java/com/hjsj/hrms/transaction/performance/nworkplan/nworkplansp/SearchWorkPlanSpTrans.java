package com.hjsj.hrms.transaction.performance.nworkplan.nworkplansp;

import com.hjsj.hrms.businessobject.performance.nworkplan.WorkPlanSpBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SearchWorkPlanSpTrans extends IBusiness {
/**
 * 国网团队工作计划与总结  当前登入用户查看范围
 */
	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String b_search=(String) hm.get("b_search");
		String content="";
		String name="";
		String sp_type="";
		String year="";
		String month="";
		String season="";
		String week="";
		Date date = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//获得系统年份
		String dat=sdf.format(date);
		String belong_type=(String)this.getFormHM().get("belong_type");//用于判断范围  1处室的 2部门的
		String state = (String)this.getFormHM().get("state");//state=0日报/=1周报/=2月报/=3季报/=4年报
		WorkPlanSpBo wpsbBo=new WorkPlanSpBo(getFrameconn(), getUserView(),belong_type,state);
		ArrayList list=new ArrayList();//用于展现员工计划/总结信息
		ArrayList sptypelist=WorkPlanSpBo.getSpList();//审批状态
		ArrayList yearlist=wpsbBo.getYearList();//年
		ArrayList seasonlist=WorkPlanSpBo.getSeasonList();//季
		ArrayList monthlist=WorkPlanSpBo.getMonthList();//月
		ArrayList weeklist=null;
		if("link".equals(b_search)){
			sp_type="all";//全部
			year = wpsbBo.getYear();
			month=dat.substring(5, 7);
			int sm=Integer.parseInt(month);
			if(sm<=3){
				season="1";
			}else if(sm<=6){
				season="2";
			}else if(sm<=9){
				season="3";
			}else if(sm<=12){
				season="4";
			}
			if("1".equals(state)){
				weeklist=wpsbBo.getWeekList(year);//周
				week=wpsbBo.getCurrWeek();
			}
		}else if("query".equals(b_search)){
			content=(String) hm.get("content");//查询内容
			content=SafeCode.decode(content);
			name=(String) hm.get("name");      //查询姓名
			name=SafeCode.decode(name);
			sp_type=(String) hm.get("sp_type");//查询状态
			year=(String) hm.get("year");//年
			season=(String) hm.get("season");//季
			month=(String) hm.get("month");//月
			week=(String) hm.get("week");//周
			week=SafeCode.decode(week);	
			week=week.replaceAll("－", "-");
			if("1".equals(state)){
				weeklist=wpsbBo.getWeekList(year);//周
			}
		}
		try {
			list=wpsbBo.getDataList(month, season, sp_type, week, content, name);
			//按审批关系判断登入用户是否还有直接领导，用于判断是报批还是批准
			String flag="0";//0批准，1报批
	       this.getFormHM().put("sptypelist", sptypelist);
	       this.getFormHM().put("yearlist", yearlist);
	       this.getFormHM().put("monthlist", monthlist);
	       this.getFormHM().put("weeklist", weeklist);
	       this.getFormHM().put("seasonlist", seasonlist);
		   this.getFormHM().put("list", list);	
		   this.getFormHM().put("state", state);
		   this.getFormHM().put("sp_type", sp_type);
		   this.getFormHM().put("name", name);
		   this.getFormHM().put("content", content);
		   this.getFormHM().put("month", month);
		   this.getFormHM().put("season", season);	 
		   this.getFormHM().put("flag", flag);
		   this.getFormHM().put("week", week);
		   this.getFormHM().put("year",year);
		} catch (Exception e) { 
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
