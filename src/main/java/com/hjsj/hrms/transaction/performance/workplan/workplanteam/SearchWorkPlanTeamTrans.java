package com.hjsj.hrms.transaction.performance.workplan.workplanteam;


import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workplanteam.WorkPlanTeamBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.*;



public class SearchWorkPlanTeamTrans extends IBusiness{

	public void execute() throws GeneralException  {
		try{
			String workType=(String)this.getFormHM().get("workType");
			String state=(String)this.getFormHM().get("state");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
			String a_code=(String)this.getFormHM().get("a_code");
		    String flag = (String)hm.get("flag");//1为页面事件查询
		    hm.remove("flag");
			String year="";
			String month="";
			String status="";
			String log_type="";
			String name = "";
			String season = "";
			String week = "";
			String day = "";
			String isSelectedAll="";
			String opeType="";
			
			WorkPlanTeamBo bo = new WorkPlanTeamBo(this.getFrameconn(),this.userView,workType,state);
			bo.createNewTable("per_diary_opinion");
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			//页面事件查询
            if("1".equals(flag)){
            	year = (String)this.getFormHM().get("year");
				month=(String)this.getFormHM().get("month");
				status=(String)this.getFormHM().get("status");
				log_type=(String)this.getFormHM().get("log_type");
				name=(String)this.getFormHM().get("name");
				season = (String)this.getFormHM().get("season");
				week = (String)this.getFormHM().get("week");
				day = (String)this.getFormHM().get("day");
				isSelectedAll = (String)this.getFormHM().get("isSelectedAll");
            }else{
               //点击左边链接查询
            	//首次加载
            	if(rMap.get("a_code")==null||"UN".equals(rMap.get("a_code")))
    			{
    				//初次加载
            		bo.analyseParameter();//刚进来时，加载一次参数设置
    				year=Calendar.getInstance().get(Calendar.YEAR)+"";
    				month=(Calendar.getInstance().get(Calendar.MONTH)+1)+"";
    				status="31";//默认为待批
    				log_type = "all";
    				name="";
    				isSelectedAll="0";
    				WeekUtils weekutils = new WeekUtils();
    				//当前是哪个季度
    				season = bo.getSeason(month);
    				//当前是哪年哪月第几周
    				week="1";
    				if(state!=null&&"1".equals(state)){
    					int [] dateArray = bo.getWeekWhere(new Date());
    					year = String.valueOf(dateArray[0]);
    					month = String.valueOf(dateArray[1]);
    					week = String.valueOf(dateArray[2]);
    				}
    				//当前是本月第几天
    				day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"";
    			}else {
    				year = (String)this.getFormHM().get("year");
    				month=(String)this.getFormHM().get("month");
    				status=(String)this.getFormHM().get("status");
    				name=(String)this.getFormHM().get("name");
    				season = (String)this.getFormHM().get("season");
    				week = (String)this.getFormHM().get("week");
    				day = (String)this.getFormHM().get("day");
    				log_type=(String)this.getFormHM().get("log_type");
    				isSelectedAll = (String)this.getFormHM().get("isSelectedAll");
    			}
            }
            //2016/1/20 wangjl 全总将只能按姓名查询改成可以模糊查询姓名，领导批示，工作内容等
            name = "请输入姓名、批示、内容".equals(name)?"":name;
            ArrayList yearList = new ArrayList();
            ArrayList seasonList = new ArrayList();
            ArrayList monthList = new ArrayList();
            ArrayList weekList = new ArrayList();
            ArrayList dayList = new ArrayList();
            //未填的记录
            ArrayList noFillList = new ArrayList();
            //年报
			if(state!=null&&"4".equals(state)){
				yearList = bo.getYearList("addall",state,a_code);
				if("30".equals(status)){
					noFillList = bo.getNoFillList(year,season,month,week,day,name,state,a_code,log_type);
				}
			}
			//季报
			if(state!=null&&"3".equals(state)){
				yearList = bo.getYearList(null,state,a_code);
				seasonList =WorkPlanTeamBo.getSeasonList("addall");
				if("30".equals(status)){
					noFillList = bo.getNoFillList(year,season,month,week,day,name,state,a_code,log_type);
				}
			}
			//月报
			if(state!=null&&"2".equals(state)){
				yearList = bo.getYearList(null,state,a_code);
				monthList =WorkPlanTeamBo.getMonthList("addall");
				if("30".equals(status)){
					noFillList = bo.getNoFillList(year,season,month,week,day,name,state,a_code,log_type);
				}
			}
			//周报
			if(state!=null&&"1".equals(state)){
				yearList = bo.getYearList(null,state,a_code);
				monthList =WorkPlanTeamBo.getMonthList("addall");
				weekList =WorkPlanTeamBo.getWeekList(year,month,"addall");	
				if("30".equals(status)){
					noFillList = bo.getNoFillList(year,season,month,week,day,name,state,a_code,log_type);
				}
			}
			//日报
			if(state!=null&&"0".equals(state)){
				yearList = bo.getYearList(null,state,a_code);
				monthList =WorkPlanTeamBo.getMonthList("addall");
				dayList =WorkPlanTeamBo.getDayList(year,month,"addall");
				if("30".equals(status)){
					noFillList = bo.getNoFillList(year,season,month,week,day,name,state,a_code,log_type);
				}	
			}

			ArrayList statusList =WorkPlanTeamBo.getStatusList();
			ArrayList logtypeList = WorkPlanTeamBo.getLogtypeList();
			String str_sql=" select "+Sql_switcher.year("p.p0104")+" as ayear,"+Sql_switcher.month("p.p0104")+" as amonth,"+Sql_switcher.quarter("p.p0104")+" as aquarter,"+Sql_switcher.day("p.p0104")+" as aday,p.log_type,p.a0101,p.p0104,p.p0106,p.p0115,p.p0100,p.nbase,p.a0100,state";
			String str_whl = "";
			if("31".equals(status)){
				str_whl=" from p01 p "+bo.getStr_whl_SP(workType,year,season,month,week,day,name,a_code,state,log_type);
			}else{
				str_whl=" from p01 p "+bo.getStr_whl(workType,year,season,month,week,day,status,name,a_code,state,log_type);
			}
			String order_str=" order by p.a0000 asc,p.a0100 desc,p.log_type asc,p.p0104 desc";
			String columns = "ayear,amonth,aquarter,aweek,aday,log_type,a0101,p0104,p0106,p0115,p0100,nbase,a0100,state";
			//System.out.println("查询sql:");
			//System.out.println(str_sql+str_whl+order_str);
			//可审批的记录Map(a0100,"1")
			HashMap spMap= bo.getSpMap(workType,year,season,month,week,day,name,a_code,state,log_type);
			//判断某日期所在第几周
			LinkedHashMap weekMap = new LinkedHashMap();
			if(state!=null&&"1".equals(state)){
				weekMap = bo.getWeekIndex(year,month,"0");
			}
			this.getFormHM().put("a_code", a_code);
			this.getFormHM().put("workType", workType);
			this.getFormHM().put("state", state);
			this.getFormHM().put("year", year);
			this.getFormHM().put("month", month);
			this.getFormHM().put("season", season);
			this.getFormHM().put("week", week);
			this.getFormHM().put("day", day);
			this.getFormHM().put("status", status);
			this.getFormHM().put("log_type", log_type);
			this.getFormHM().put("name", name);
			this.getFormHM().put("opeType", opeType);
			this.getFormHM().put("isSelectedAll", isSelectedAll);
			this.getFormHM().put("yearList", yearList);
			this.getFormHM().put("monthList",monthList);
			this.getFormHM().put("statusList",statusList);
			this.getFormHM().put("logtypeList",logtypeList);
			this.getFormHM().put("seasonList",seasonList);
			this.getFormHM().put("weekList",weekList);
			this.getFormHM().put("dayList",dayList);
			if("30".equals(status)){
				this.getFormHM().put("noFillList",noFillList);
			}
			this.getFormHM().put("spMap",spMap);
			this.getFormHM().put("weekMap",weekMap);
			
			this.getFormHM().put("str_sql", str_sql);
			this.getFormHM().put("str_whl", str_whl);
			this.getFormHM().put("order_str", order_str);
			this.getFormHM().put("columns", columns);
			
			
			String dbType = "1";
			switch(Sql_switcher.searchDbServer())
		    {
				case Constant.MSSQL:
			    {
			    	dbType = "1";
					break;
			    }
				case Constant.ORACEL:
				{ 
					dbType = "2";
					break;
				}
				case Constant.DB2:
				{
					dbType = "3";
					break;
				}
		    }
			
			String print_id = ""; // 打印信息登记表
			if("4".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 年计划
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id14");
    		}
    		else if("4".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 年总结
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id24");
    		}
    		else if("3".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 季计划
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id13");
    		}
    		else if("3".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 季总结
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id23");
    		}
    		else if("2".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 月计划
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id12");
    		}
    		else if("2".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 月总结
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id22");		    		    	    			
    		}
    		else if("1".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 周计划
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id11");
    		}
    		else if("1".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 周总结
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id21");
    		}
    		else if("0".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type))
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id0");
    		}
    		else if("0".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type))
    		{
    			print_id = (String)WorkPlanTeamBo.workParametersMap.get("print_id0");
    		}
			
		//	SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		//	WorkPlanViewBo wbo = new WorkPlanViewBo(this.getUserView(),this.getFrameconn(),state);
		//	String startime = format.format(wbo.getStartDateAndEndDate(year, season, month, week, day, 1));
		//	System.out.println(startime);
			
		//	this.getFormHM().put("startime",startime);
			this.getFormHM().put("print_id", print_id);
			this.getFormHM().put("dbType", dbType);
			
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
