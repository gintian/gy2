package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
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
 * SearchMonthWorkplanTrans.java
 * Description: 国网显示月报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 1, 2013 9:47:49 AM Jianghe created
 */
public class SearchMonthWorkplanTrans extends IBusiness{
	
	public void execute() throws GeneralException  {
		try{
			//当月总结显示的时间段
			String currentTime = "";
			//当月总结所对应的 年&月
			String currentYear = "";
			String currentMonth= "";
			//总结指标  title,content  从工作纪实设置中获取
			String summarizeFields = "";
			//判断从哪进的入口
			String init = "";
			//页面显示的当月总结数据    bean
			ArrayList summarizeDataList=new ArrayList();
			//下月计划显示的时间段
			String nextTime="";
			//下月计划多对应的 年&月
			String nextYear="";
			String nextMonth="";
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
			String belong_type= (String)this.getFormHM().get("belong_type")==null?"0":(String)this.getFormHM().get("belong_type");
			
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			WeekUtils weekutils = new WeekUtils();
			GregorianCalendar cal = new GregorianCalendar(); 
			String ap0100 = null;
			if(rMap.get("init")!=null&& "init".equals((String)rMap.get("init")))
			{
				//从菜单进
				GregorianCalendar curcal = new GregorianCalendar(); 
				currentYear = curcal.get(GregorianCalendar.YEAR)+"";
				currentMonth = (curcal.get(GregorianCalendar.MONTH)+1)+"";
				
				GregorianCalendar nextcal = new GregorianCalendar();
				nextcal.setTime(curcal.getTime());
				nextcal.add(GregorianCalendar.MONTH, 1);
				nextYear = nextcal.get(GregorianCalendar.YEAR)+"";
				nextMonth = (nextcal.get(GregorianCalendar.MONTH)+1)+"";
				//把本的个人信息放到session中
				this.userView.getHm().put("a0100", this.userView.getA0100());
				this.userView.getHm().put("nbase", this.userView.getDbname());
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
				//当前人是否是处长  0 职员 1 处长 2主任
			    String c01sc = bo.getUserDetail(this.userView.getDbname()+this.userView.getA0100(), "c01sc");
			    //System.out.println(c01sc);
			    if("03".equals(c01sc)|| "04".equals(c01sc))
			    	isChuZhang="1";
			    else if("05".equals(c01sc)|| "06".equals(c01sc))
			    	isChuZhang="0";
			    else if("01".equals(c01sc)|| "02".equals(c01sc))
			    	isChuZhang="2";
			    this.getFormHM().put("personPage", personPage);
			    this.getFormHM().put("isChuZhang", isChuZhang);
			}else if(rMap.get("init")!=null&& "1".equals((String)rMap.get("init"))){
				//切换当前日期
				currentYear = (String)this.getFormHM().get("currentYear");
				currentMonth = (String)this.getFormHM().get("currentMonth");
				
				GregorianCalendar nextcal = new GregorianCalendar();
				nextcal.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
				nextcal.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
				nextcal.add(GregorianCalendar.MONTH, 1);
				nextYear = nextcal.get(GregorianCalendar.YEAR)+"";
				nextMonth = (nextcal.get(GregorianCalendar.MONTH)+1)+"";
				init = (String)rMap.get("init");
			}else if(rMap.get("init")!=null&& "move".equals((String)rMap.get("init"))){
				//调整记录顺序
				currentYear = (String)this.getFormHM().get("currentYear");
				currentMonth = (String)this.getFormHM().get("currentMonth");
				
				nextYear = (String)this.getFormHM().get("nextYear");
				nextMonth = (String)this.getFormHM().get("nextMonth");
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
			}else if(rMap.get("init")!=null&& "fromquery".equals((String)rMap.get("init"))){
				//从查询页面进来
				ap0100 = (String)rMap.get("p0100");
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn());
				LazyDynaBean abean = bo.getp01Detail(ap0100);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse((String)abean.get("p0104"));
				GregorianCalendar curcal = new GregorianCalendar(); 
				curcal.setTime(date);
				currentYear = curcal.get(GregorianCalendar.YEAR)+"";
				currentMonth = (curcal.get(GregorianCalendar.MONTH)+1)+"";
				
				GregorianCalendar nextcal = new GregorianCalendar();
				nextcal.setTime(curcal.getTime());
				nextcal.add(GregorianCalendar.MONTH, 1);
				nextYear = nextcal.get(GregorianCalendar.YEAR)+"";
				nextMonth = (nextcal.get(GregorianCalendar.MONTH)+1)+"";
			}
			if(rMap.get("opt")!=null&& "2".equals((String)rMap.get("opt"))){
				//从团队工作纪实进
				ap0100 = (String)rMap.get("p0100");
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn());
				LazyDynaBean abean = bo.getp01Detail(ap0100);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse((String)abean.get("p0104"));
				GregorianCalendar curcal = new GregorianCalendar(); 
				curcal.setTime(date);
				currentYear = curcal.get(GregorianCalendar.YEAR)+"";
				currentMonth = (curcal.get(GregorianCalendar.MONTH)+1)+"";
				
				GregorianCalendar nextcal = new GregorianCalendar();
				nextcal.setTime(curcal.getTime());
				nextcal.add(GregorianCalendar.MONTH, 1);
				nextYear = nextcal.get(GregorianCalendar.YEAR)+"";
				nextMonth = (nextcal.get(GregorianCalendar.MONTH)+1)+"";
				
				String a0100 = (String)abean.get("a0100");
				String nbase = (String)abean.get("nbase");
				//把员工的个人信息放到session中
				this.userView.getHm().put("a0100", a0100);
				this.userView.getHm().put("nbase", nbase);
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
			    else if("05".equals(c01sc)|| "06".equals(c01sc))
			    	isChuZhang="0";
			    else if("01".equals(c01sc)|| "02".equals(c01sc))
			    	isChuZhang="2";
			    this.getFormHM().put("isChuZhang", isChuZhang);
			    belong_type = (String)rMap.get("belong_type");
				//0 从人员tab进入   1 从部门tab进入
				personPage = (String)rMap.get("personPage");
				this.getFormHM().put("personPage", personPage);
				this.getFormHM().put("belong_type", belong_type);
				rMap.remove("opt");
			}
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			// 刚进来时，加载一次参数设置
			bo.initParam(); 
			cal.setTime(weekutils.strTodate(weekutils.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth))));
			String totalday1=cal.get(GregorianCalendar.DAY_OF_MONTH)+"";
			currentTime = currentYear+"年"+currentMonth+"月"+"1日"+"-"+currentMonth+"月"+totalday1+"日";
			cal.setTime(weekutils.strTodate(weekutils.lastMonthStr(Integer.parseInt(nextYear),Integer.parseInt(nextMonth))));
			String totalday2=cal.get(GregorianCalendar.DAY_OF_MONTH)+"";
			nextTime = nextYear+"年"+nextMonth+"月"+"1日"+"-"+nextMonth+"月"+totalday2+"日";
			//月总结指标
			summarizeFields = ((String)NworkPlanBo.workParametersMap.get("summarize_fields2")).toLowerCase();
			//月计划指标
			planFields = ((String)NworkPlanBo.workParametersMap.get("plan_fields2")).toLowerCase();
			if(ap0100==null)
			   p0100 = bo.getSummarizeP0100(currentYear,currentMonth,"2",personPage,isChuZhang,(String)this.userView.getHm().get("opt"),belong_type);
			else
			   p0100 = ap0100;
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
			this.getFormHM().put("currentYear", currentYear);
			this.getFormHM().put("currentMonth", currentMonth);
			this.getFormHM().put("nextYear", nextYear);
			this.getFormHM().put("nextMonth", nextMonth);
			this.getFormHM().put("currentTime", currentTime);
			this.getFormHM().put("nextTime", nextTime);
			this.getFormHM().put("summarizeFields", summarizeFields);
			this.getFormHM().put("planFields", planFields);
			this.getFormHM().put("p0115", p0115);
			this.getFormHM().put("init", init);
			this.getFormHM().put("p0100", p0100);
			this.getFormHM().put("state", "2");
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
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
