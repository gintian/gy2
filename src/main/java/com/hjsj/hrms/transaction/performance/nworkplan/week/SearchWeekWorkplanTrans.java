package com.hjsj.hrms.transaction.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hjsj.hrms.businessobject.performance.nworkplan.week.WeekWorkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * SearchWeekWorkplanTrans.java
 * Description: 国网显示周报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 8, 2013 9:52:36 AM Jianghe created
 */
public class SearchWeekWorkplanTrans extends IBusiness{
	
	public void execute() throws GeneralException  {
		try{
			//当前周总结对应的年
			String summarizeYear = "";
			//总结指标  title,content  从工作纪实设置中获取
			String summarizeFields = "";
			//判断从哪进的入口
			String init = "";
			//页面显示的当月总结数据    bean
			ArrayList summarizeDataList=new ArrayList();
			//计划指标  title,content 从工作纪实设置中获取
			String planFields="";
			//当前时间对应的p0100
			String p0100 = "";
			//页面显示的下月计划数据    bean
			ArrayList planDataList=new ArrayList();
			//状态 01 起草,02 已报批,03 已批,07 驳回,"" 未填
			String p0115="";
			//高亮记录
			String hyperlinkRecord = "";
			String hyperlinkP0100 = "";
			//0 从人员tab进入   1 从部门tab进入
			String personPage = (String)this.getFormHM().get("personPage")==null?"0":(String)this.getFormHM().get("personPage");
			String isChuZhang= (String)this.getFormHM().get("isChuZhang")==null?"0":(String)this.getFormHM().get("isChuZhang");
			
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			ArrayList summarizeTimeList=null;
			String summarizeTime =null;
			String planYear_start="";
			String planMonth_start="";
			String planDay_start="";
			String planYear_end="";
			String planMonth_end="";
			String planDay_end="";
			String isdeptother="0";//是否取部门其他人的计划的时间周期，如果取其他人的了，要更改，全更改
			String belong_type="0";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(rMap.get("init")!=null&& "init".equals((String)rMap.get("init")))//首次进入
			{
				//从菜单进
				GregorianCalendar curcal = new GregorianCalendar(); 
				summarizeYear = curcal.get(GregorianCalendar.YEAR)+"";
				WeekWorkPlanBo wwpb = new WeekWorkPlanBo(this.getUserView(),this.getFrameconn(),"1","",personPage);
				wwpb.setBelong_type(belong_type);
				summarizeTimeList=wwpb.getPlanDateList(this.userView.getDbname()+this.userView.getA0100(), summarizeYear);

                summarizeTime=wwpb.getSummTime(this.userView.getDbname()+this.userView.getA0100(), summarizeYear,true);
                if(summarizeTime!=null&&!"".equals(summarizeTime)){//已有总结记录，根据总结找计划
                	 HashMap amap= wwpb.getInitPlanRecord("", this.userView.getDbname()+this.userView.getA0100(),summarizeTime);
     	            if(amap.get("p0100")!=null&&!"".equals((String)amap.get("p0100"))){
     	            	p0100=(String)amap.get("p0100");
     	            }
     	            if(amap.get("isdeptother")!=null&&!"".equals((String)amap.get("isdeptother"))){
     	            	isdeptother=(String)amap.get("isdeptother");
     	            }
     	            if(amap.get("start_date")!=null)
                 	{
                 		Date d1=(Date)amap.get("start_date");
                 		GregorianCalendar nextcal = new GregorianCalendar();
         				nextcal.setTime(d1);
         				planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
         				planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
         				planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
         				Date d2 = (Date)amap.get("end_date");
         				nextcal = new GregorianCalendar();
         				nextcal.setTime(d2);
         				planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
         				planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
         				planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
                 	}else{//如果没有，按自然周，找下一周时间
                 		HashMap map = wwpb.getInitMap();
	            	    Date d1=(Date)map.get("start_date");
                		GregorianCalendar nextcal = new GregorianCalendar();
    			    	nextcal.setTime(d1);
    			    	planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
    			    	planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
    			    	planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
    			    	Date d2 = (Date)map.get("end_date");
    			    	nextcal = new GregorianCalendar();
    			    	nextcal.setTime(d2);
    			    	planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
    			    	planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
    				    planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
                 	}
                }else{
                	 HashMap amap= wwpb.getInitPlanRecord("", this.userView.getDbname()+this.userView.getA0100());
                	 if(amap.get("start_date")!=null)
                  	{
                  		Date d1=(Date)amap.get("start_date");
                  		GregorianCalendar nextcal = new GregorianCalendar();
          				nextcal.setTime(d1);
          				planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
          				planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
          				planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
          				Date d2 = (Date)amap.get("end_date");
          				nextcal = new GregorianCalendar();
          				nextcal.setTime(d2);
          				planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
          				planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
          				planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
          				if(amap.get("isdeptother")!=null&&!"".equals((String)amap.get("isdeptother")))
          					isdeptother=(String)amap.get("isdeptother");
                  	}else{//如果没有，按自然周，找下一周时间
                  		HashMap map = wwpb.getInitMap();
 	            	    Date d1=(Date)map.get("start_date");
                 		GregorianCalendar nextcal = new GregorianCalendar();
     			    	nextcal.setTime(d1);
     			    	planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
     			    	planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
     			    	planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
     			    	Date d2 = (Date)map.get("end_date");
     			    	nextcal = new GregorianCalendar();
     			    	nextcal.setTime(d2);
     			    	planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
     			    	planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
     				    planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
                  	}
                }
				//把本的个人信息放到session中
				this.userView.getHm().put("a0100", this.userView.getA0100());
				this.userView.getHm().put("nbase", this.userView.getDbname());
				this.userView.getHm().put("e0122", this.userView.getUserDeptId());
				//1 本人  2领导登录
				this.userView.getHm().put("opt", "1");
				//返回的团队页面url   放到session中
				this.userView.getHm().put("returnurl", "");
				//查看还是审批 1:查看 2:审批
				this.userView.getHm().put("isRead", "");
				init = (String)rMap.get("init");
				//0 从人员tab进入   1 从部门tab进入
				personPage = (String)rMap.get("personPage");
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn());
				//当前人是否是处长  0 职员 1 处长
			    String c01sc = bo.getUserDetail(this.userView.getDbname()+this.userView.getA0100(), "c01sc");
			    //System.out.println(c01sc);
			    if("03".equals(c01sc)|| "04".equals(c01sc))
			    	isChuZhang="1";
			    else
			    	isChuZhang="0";
			    this.getFormHM().put("personPage", personPage);
			    this.getFormHM().put("isChuZhang", isChuZhang);
			}else if(rMap.get("init")!=null&& "1".equals((String)rMap.get("init"))){//切换日期
				
				String qhtype="0";
				if(rMap.get("qhtype")!=null){
					qhtype=(String)rMap.get("qhtype");
					rMap.remove("qhtype");
				}
				belong_type=(String)this.getFormHM().get("belong_type");
				summarizeYear=(String)this.getFormHM().get("summarizeYear");
				WeekWorkPlanBo wwpb = new WeekWorkPlanBo(this.getUserView(),this.getFrameconn(),this.userView.getHm().get("opt")==null?"1":(String)this.userView.getHm().get("opt"),"",personPage);
				wwpb.setBelong_type(belong_type);
				summarizeTime = (String)this.getFormHM().get("summarizeTime");
				if(summarizeTime!=null)
		    		summarizeTime=summarizeTime.replaceAll("－", "-"); 
				if("1".equals(qhtype)){
					 summarizeTime=wwpb.getSummTime((String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"), summarizeYear,false);
				}
				summarizeTimeList=wwpb.getPlanDateList((String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"), summarizeYear);
				if(summarizeTime!=null&&!"".equals(summarizeTime)){
			    	String[] temp=summarizeTime.split("--");
					Date zjS_Date=format.parse(temp[0]);
					Date zjE_Date = format.parse(temp[1]);
					HashMap amap = wwpb.getPlanDateByZJ((String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"), zjS_Date, zjE_Date);
					if(amap.get("p0100")!=null&&!"".equals((String)amap.get("p0100"))){
						p0100=(String)amap.get("p0100");
					}
					if(amap.get("start_date")!=null)
	            	{
	            		Date d1=(Date)amap.get("start_date");
	            		GregorianCalendar nextcal = new GregorianCalendar();
	    				nextcal.setTime(d1);
	    				planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
	    				planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
	    				planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
	    				
	    				Date d2 = (Date)amap.get("end_date");
	    				nextcal = new GregorianCalendar();
	    				nextcal.setTime(d2);
	    				planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
	    				planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
	    				planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
	            	}
					else{
						HashMap bMap=wwpb.getSameDept(zjE_Date, (String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"));
						if(bMap.get("start_date")!=null){
							Date d1=(Date)bMap.get("start_date");
		            		GregorianCalendar nextcal = new GregorianCalendar();
		    				nextcal.setTime(d1);
		    				planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
		    				planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
		    				planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
		    				Date d2 = (Date)bMap.get("end_date");
		    				nextcal = new GregorianCalendar();
		    				nextcal.setTime(d2);
		    				planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
		    				planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
		    				planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
						}else{
							HashMap  cmap = wwpb.getJhByZjEndDate(zjE_Date);
		    				Date d1=(Date)cmap.get("start_date");
		            		GregorianCalendar nextcal = new GregorianCalendar();
		    				nextcal.setTime(d1);
		    				planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
		    				planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
		    				planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
		    				Date d2 = (Date)cmap.get("end_date");
		    				nextcal = new GregorianCalendar();
		    				nextcal.setTime(d2);
		    				planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
		    				planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
		    				planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
						}
						if(bMap.get("isdeptother")!=null&&!"".equals((String)bMap.get("isdeptother")))
							isdeptother=(String)bMap.get("isdeptother");
					}
				}else{
					HashMap map = wwpb.getInitMap();
            	    Date d1=(Date)map.get("start_date");
            		GregorianCalendar nextcal = new GregorianCalendar();
			    	nextcal.setTime(d1);
			    	planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
			    	planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
			    	planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
			    	Date d2 = (Date)map.get("end_date");
			    	nextcal = new GregorianCalendar();
			    	nextcal.setTime(d2);
			    	planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
			    	planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
				    planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
				}
				init = (String)rMap.get("init");
			}else if(rMap.get("init")!=null&& "move".equals((String)rMap.get("init"))){
				//调整记录顺序
				summarizeYear = (String)this.getFormHM().get("summarizeYear");
				summarizeTime = (String)this.getFormHM().get("summarizeTime");
				summarizeTime=summarizeTime.replaceAll("－", "-"); 
                String theP0100 = (String)rMap.get("p0100");				
                String theRecord_num = (String)rMap.get("record_num");				
                String theSeq = (String)rMap.get("seq");
                String moveflag = (String)rMap.get("moveflag");
                String theLogtype = (String)rMap.get("log_type");
                NworkPlanBo bo = new NworkPlanBo(this.getFrameconn());
                bo.moveRecord(theP0100,theRecord_num,theSeq,moveflag,theLogtype);
                init = (String)rMap.get("init");
                hyperlinkRecord = ","+(String)this.getFormHM().get("hyperlinkRecord")+",";
                hyperlinkP0100 = (String)this.getFormHM().get("hyperlinkP0100");
                summarizeTimeList=(ArrayList)this.getFormHM().get("summarizeTimeList");
                p0100=theP0100;
                planYear_start=(String)this.getFormHM().get("planYear_start");
				planMonth_start=(String)this.getFormHM().get("planMonth_start");
				planDay_start=(String)this.getFormHM().get("planDay_start");
				planYear_end=(String)this.getFormHM().get("planYear_end");
				planMonth_end=(String)this.getFormHM().get("planMonth_end");
				planDay_end=(String)this.getFormHM().get("planDay_end");
				summarizeYear=(String)this.getFormHM().get("summarizeYear");
				isdeptother=(String)this.getFormHM().get("isdeptother");
				belong_type=(String)this.getFormHM().get("belong_type");
			}else if(rMap.get("init")!=null&& "fromquery".equals((String)rMap.get("init"))){
				//从查询页面进来
				String ap0100 = (String)rMap.get("p0100");
				p0100=ap0100;
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn());
				LazyDynaBean abean = bo.getp01Detail(ap0100);
				summarizeYear=(String)abean.get("year");
				//1 本人  2领导登录
				//this.userView.getHm().put("opt", (String)rMap.get("opt"));
				//返回的团队页面url   放到session中
				//this.userView.getHm().put("returnurl", ((String)rMap.get("returnurl")).replaceAll("`", "&"));
				//查看还是审批 1:查看 2:审批
				//this.userView.getHm().put("isRead", (String)rMap.get("isRead"));
				//员工是否是处长  0 职员 1 处长
			    String c01sc = bo.getUserDetail(this.userView.getDbname()+this.userView.getA0100(), "c01sc");
			    if("03".equals(c01sc)|| "04".equals(c01sc))
			    	isChuZhang="1";
			    else if("01".equals(c01sc)|| "02".equals(c01sc))
			    	isChuZhang="2";
			    else
			    	isChuZhang="0";
			    this.getFormHM().put("isChuZhang", isChuZhang);
			  //0 从人员tab进入   1 从部门tab进入
				personPage = (String)rMap.get("personPage");
				this.getFormHM().put("personPage", personPage);
				WeekWorkPlanBo wwpb = new WeekWorkPlanBo(this.getUserView(),this.getFrameconn(),(String)this.userView.getHm().get("opt"),"",personPage);
				wwpb.setBelong_type(belong_type);
				summarizeTimeList=wwpb.getPlanDateList((String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"),summarizeYear);
                String p0104=(String)abean.get("p0104");
                String p0106=(String)abean.get("p0106");
                Date d1=format.parse(p0104);
        		GregorianCalendar nextcal = new GregorianCalendar();
				nextcal.setTime(d1);
				planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
				planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
				planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
				Date d2 = format.parse(p0106);
				nextcal = new GregorianCalendar();
				nextcal.setTime(d2);
				planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
				planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
				planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
                HashMap amap = wwpb.getZJDateByPlan((String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"), d1, d2,c01sc);
                if(amap.get("start_date")!=null){
                	Date done = (Date)amap.get("start_date");
                	Date dtwo = (Date)amap.get("end_date");
                	summarizeTime=format.format(done)+"--"+dtwo;
                }
                belong_type=(String)this.getFormHM().get("belong_type");
			}
			if(rMap.get("opt")!=null&& "2".equals((String)rMap.get("opt"))){
				//从团队工作纪实进
				String ap0100 = (String)rMap.get("p0100");
				p0100=ap0100;
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn());
				LazyDynaBean abean = bo.getp01Detail(ap0100);
				String a0100 = (String)abean.get("a0100");
				String nbase = (String)abean.get("nbase");
				summarizeYear=(String)abean.get("year");
				String e0122=(String)abean.get("e0122");
				belong_type=(String)this.getFormHM().get("belong_type");
				//把员工的个人信息放到session中
				this.userView.getHm().put("a0100", a0100);
				this.userView.getHm().put("nbase", nbase);
				this.userView.getHm().put("e0122", e0122);
				//1 本人  2领导登录
				this.userView.getHm().put("opt", (String)rMap.get("opt"));
				//返回的团队页面url   放到session中
				this.userView.getHm().put("returnurl", ((String)rMap.get("returnurl")).replaceAll("`", "&"));
				//查看还是审批 1:查看 2:审批
				this.userView.getHm().put("isRead", (String)rMap.get("isRead"));
				//员工是否是处长  0 职员 1 处长
			    String c01sc = bo.getUserDetail(this.userView.getDbname()+this.userView.getA0100(), "c01sc");
			    if("03".equals(c01sc)|| "04".equals(c01sc))
			    	isChuZhang="1";
			    else if("01".equals(c01sc)|| "02".equals(c01sc))
			    	isChuZhang="2";
			    else
			    	isChuZhang="0";
			    this.getFormHM().put("isChuZhang", isChuZhang);
				//0 从人员tab进入   1 从部门tab进入
				personPage = (String)rMap.get("personPage");
				this.getFormHM().put("personPage", personPage);
				WeekWorkPlanBo wwpb = new WeekWorkPlanBo(this.getUserView(),this.getFrameconn(),(String)this.userView.getHm().get("opt"),"",personPage);
			    wwpb.setBelong_type(belong_type);
				summarizeTimeList=wwpb.getPlanDateList((String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"),summarizeYear);
                String p0104=(String)abean.get("p0104");
                String p0106=(String)abean.get("p0106");
                Date d1=format.parse(p0104);
        		GregorianCalendar nextcal = new GregorianCalendar();
				nextcal.setTime(d1);
				planYear_start=nextcal.get(GregorianCalendar.YEAR)+"";
				planMonth_start=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
				planDay_start=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
				Date d2 = format.parse(p0106);
				nextcal = new GregorianCalendar();
				nextcal.setTime(d2);
				planYear_end=nextcal.get(GregorianCalendar.YEAR)+"";
				planMonth_end=(nextcal.get(GregorianCalendar.MONTH)+1)+"";
				planDay_end=nextcal.get(GregorianCalendar.DAY_OF_MONTH)+"";
                HashMap amap = wwpb.getZJDateByPlan((String)this.userView.getHm().get("nbase")+(String)this.userView.getHm().get("a0100"), d1, d2,c01sc);
                if(amap.get("start_date")!=null){
                	Date done = (Date)amap.get("start_date");
                	Date dtwo = (Date)amap.get("end_date");
                	summarizeTime=format.format(done)+"--"+dtwo;
                }
                rMap.remove("opt");
			}
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			// 刚进来时，加载一次参数设置
			bo.initParam(); 
			//月总结指标
			summarizeFields = ((String)NworkPlanBo.workParametersMap.get("summarize_fields1")).toLowerCase();
			//月计划指标
			planFields = ((String)NworkPlanBo.workParametersMap.get("plan_fields1")).toLowerCase();
			//p0100 = bo.getSummarizeP0100(currentYear,currentMonth,"2",personPage,isChuZhang);
			if(p0100==null|| "".equals(p0100)){
				p0115 = "";//未填
			}else{
				p0115 = bo.getP0115(p0100);
				summarizeDataList = bo.getDataList(p0100,"2",summarizeFields);//当月总结数据   
				planDataList = bo.getDataList(p0100,"1",planFields);//下月计划数据
			}
			//总结所用指标     bean:itemid,itemtype,itemdesc,codesetid,decimalwidth
			ArrayList zongjieFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",summarizeFields);
			
			//计划所用指标     bean:itemid,itemtype,itemdesc,codesetid,decimalwidth
			ArrayList jihuaFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",planFields);
			this.getFormHM().put("summarizeYear", summarizeYear);
			this.getFormHM().put("summarizeFields", summarizeFields);
			this.getFormHM().put("planFields", planFields);
			this.getFormHM().put("p0115", p0115);
			this.getFormHM().put("init", init);
			this.getFormHM().put("p0100", p0100);
			this.getFormHM().put("state", "1");
			this.getFormHM().put("summarizeDataList", summarizeDataList);
			this.getFormHM().put("summarizeDataSize", String.valueOf(summarizeDataList.size()));
			this.getFormHM().put("planDataSize", String.valueOf(planDataList.size()));
			this.getFormHM().put("planDataList", planDataList);
			this.getFormHM().put("zongjieFieldsList", zongjieFieldsList);
			this.getFormHM().put("jihuaFieldsList", jihuaFieldsList);
			this.getFormHM().put("hyperlinkP0100", hyperlinkP0100);
            this.getFormHM().put("hyperlinkRecord", hyperlinkRecord);
			//审批关系号   null:考核关系   其他：审批关系主键    从工作纪实设置中获取
			this.getFormHM().put("sp_relation", (String)NworkPlanBo.workParametersMap.get("sp_relation"));
			this.getFormHM().put("summarizeTimeList", summarizeTimeList);
			this.getFormHM().put("summarizeTime", summarizeTime);
			this.getFormHM().put("planYear_start", planYear_start);
			this.getFormHM().put("planMonth_start", planMonth_start);
			this.getFormHM().put("planDay_start", planDay_start);
			this.getFormHM().put("planYear_end", planYear_end);
			this.getFormHM().put("planMonth_end", planMonth_end);
			this.getFormHM().put("planDay_end", planDay_end);
			this.getFormHM().put("isdeptother", isdeptother);
			this.getFormHM().put("belong_type", belong_type);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
