package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.*;

public class InitIndividualPerformanceListTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String  operate=(String)hm.get("operate");
		/*	String    plan_id=(String)this.getFormHM().get("dbpre");
			if(plan_id.equals("0"))
				plan_id=(String)hm.get("plan_id");
		*/
			String    plan_id=(String)hm.get("plan_id");
			
			String object_id = "";
			//单人点评第一次进入页面传入了object_id（00000231/2），而进入绩效数据通过参数又传了自己的object_id（加密的00000123），这两个object_id不一样，
			//，导致只要点击了绩效数据，在没有权限的时候，永远是后一个object_id，其他按钮获取值失败
			if(hm.get("object_id_e") == null) {
				object_id=(String)hm.get("object_id");
			}else {
				object_id=(String)hm.get("object_id_e");
			}
			
			object_id = object_id.replaceAll("／", "/");
			if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = object_id.substring(1); 
	        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
			LoadXml loadxml=new LoadXml(this.getFrameconn(),plan_id);
			Hashtable htxml=loadxml.getDegreeWhole();
			String perSet=(String)htxml.get("PerSet");
			String statCustomMode=(String)htxml.get("StatCustomMode");
			String perSetShowMode=(String)htxml.get("PerSetShowMode");; //绩效子集显示方式  1-明细项，2-合计项 或 3-两者者显
			
		//	System.out.println("="+this.getUserView().analyseTablePriv(perSet));
			if("0".equals(this.getUserView().analyseTablePriv(perSet)))
				throw GeneralExceptionHandler.Handle(new Exception("您没有查看该绩效数据子集的权限！"));
			
			ArrayList years=new ArrayList();
			ArrayList months=new ArrayList();
			ArrayList counts=new ArrayList();
			ArrayList quarters=new ArrayList();
			ArrayList halfYears=new ArrayList();
			String year="";
			String month="";
			String count="";
			String quarter="";
			String halfYear="";
			String statMethod="";    //1:按年统计 2：按月统计 3：按季度统计  4：按半年统计 9:时间段
			String perCompare="";
			
			String statStartDate=(String)this.getFormHM().get("statStartDate");  //起始时间
			String statEndDate=(String)this.getFormHM().get("statEndDate");		 //终止时间
			
			GregorianCalendar now=new GregorianCalendar();
			if("True".equalsIgnoreCase(statCustomMode))   //显示绩效子集统计自定义
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				year=(String)this.getFormHM().get("year");
				month=(String)this.getFormHM().get("month");
				count=(String)this.getFormHM().get("count");
				quarter=(String)this.getFormHM().get("quarter");
				halfYear=(String)this.getFormHM().get("halfYear");
				statMethod=(String)this.getFormHM().get("statMethod");
	
				if(statMethod==null||statMethod.trim().length()==0)
					statMethod="1";
				if(year==null||year.trim().length()==0)
					year=String.valueOf(now.get(Calendar.YEAR));
				if(month==null||month.trim().length()==0)
					month=String.valueOf(now.get(Calendar.MONTH)+1);
				if(count==null||count.trim().length()==0)
					count="1";
				perCompare=(String)this.getFormHM().get("perCompare");
				if(perCompare==null)
					perCompare="0";
				years=getYearsOrMonthsORcounts(2);
				months=getYearsOrMonthsORcounts(1);
				counts=getYearsOrMonthsORcounts(0);
				quarters=getYearsOrMonthsORcounts(3);
				halfYears=getYearsOrMonthsORcounts(4);
				
			}
			else
			{
				
				String perSetStatMode=(String)htxml.get("PerSetStatMode");; //绩效子集统计方式  1-年、2-月、3-季度、4-半年、9-时间段
				if("9".equals(perSetStatMode))
				{
					String aStartDate=(String)htxml.get("StatStartDate");
					String aEndDate=(String)htxml.get("StatEndDate");
					String[] startDate=aStartDate.split("\\.");
					String[] endDate=aEndDate.split("\\.");
					statStartDate=startDate[0]+"-"+startDate[1]+"-"+startDate[2];   //统计方式为9有效，绩效子集统计时间段
					statEndDate=endDate[0]+"-"+endDate[1]+"-"+endDate[2];	   //统计方式为9有效，绩效子集统计时间段
				}
				statMethod=perSetStatMode;
				year=String.valueOf(now.get(Calendar.YEAR));
				month=String.valueOf(now.get(Calendar.MONTH)+1);
				count="0";			
				quarter=getCurrentQuarter(Integer.parseInt(month));
				halfYear=getCurrentHalfYear(Integer.parseInt(month));
			}
			
			if(object_id!=null&&object_id.indexOf("/")!=-1)
			{
				String[] temp=object_id.split("/");
				object_id=temp[0];
			}
			
			String    mainbody_id=(String)this.getUserView().getA0100();
			String changFlag="";
			String reportTitles[]=null;
			ArrayList performanceListform=new ArrayList();
			ArrayList itemidList=new ArrayList();
			try
			{	
				SingleGradeBo singleGradeBo=new SingleGradeBo(this.getFrameconn());
				ArrayList     list=singleGradeBo.getIndividualPerformance(this.getUserView(),perSetShowMode,statMethod,year,month,count,quarter,halfYear,perCompare,object_id,mainbody_id,plan_id,perSet,statStartDate,statEndDate);
				changFlag=(String)list.get(0);//按月变化标志 0:不变化  1：按月变化 2：按年变化
				reportTitles=(String[])list.get(1);
				performanceListform=(ArrayList)list.get(2);
				itemidList=(ArrayList)list.get(3);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			this.getFormHM().put("dbpre",plan_id);
			this.getFormHM().put("perSetShowMode",perSetShowMode);
			this.getFormHM().put("operate",operate);
			this.getFormHM().put("object_id",getObject_id(object_id,plan_id));
			this.getFormHM().put("itemidList",itemidList);
			this.getFormHM().put("performanceListform",performanceListform);
			this.getFormHM().put("reportTitles",reportTitles);
			this.getFormHM().put("changFlag",changFlag);
			this.getFormHM().put("perCompare",perCompare);
			this.getFormHM().put("year",year);
			this.getFormHM().put("years",years);
			this.getFormHM().put("month",month);
			this.getFormHM().put("months",months);
			this.getFormHM().put("count",count);
			this.getFormHM().put("counts",counts);
			this.getFormHM().put("quarters",quarters); 
			this.getFormHM().put("quarter",quarter);
			this.getFormHM().put("halfYears",halfYears);
			this.getFormHM().put("halfYear",halfYear);
			this.getFormHM().put("statCustomMode",statCustomMode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	private String getCurrentQuarter(int month)
	{
		String quarter="";
		if(month>=1&&month<=3)
			quarter="1";
		else if(month>=4&&month<=6)
			quarter="2";
		else if(month>=7&&month<=9)
			quarter="3";
		else if(month>=10&&month<=12)
			quarter="4";
		return quarter;
	}
	
	private String getCurrentHalfYear(int month)
	{
		String halfYear="";
		if(month>=1&&month<=6)
			halfYear="1";
		else 
			halfYear="2";
		return halfYear;
	}
	
	
	public String getObject_id(String object_id,String plan_id)
	{
		String a_object_id="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search("select * from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+this.getUserView().getA0100()+"' and object_id='"+object_id+"'");
			if(this.frowset.next())
				a_object_id=object_id+"/"+this.frowset.getString("status");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_object_id;
	}
	
	
	public ArrayList getYearsOrMonthsORcounts(int flag)
	{
		ArrayList list=new ArrayList();
		CommonData aCommonData=null;
		if(flag==0)   //次
		{
			for(int i=1;i<21;i++)
			{
				aCommonData=new CommonData(String.valueOf(i),String.valueOf(i));
				list.add(aCommonData);
			}
			list.add(new CommonData("0","全部"));
			
		}
		else if(flag==1) //月
		{
			for(int i=1;i<13;i++)
			{
				aCommonData=new CommonData(String.valueOf(i),String.valueOf(i));
				list.add(aCommonData);
			}
		}
		else if(flag==2)  //年
		{
			GregorianCalendar now=new GregorianCalendar();
			int year=now.get(Calendar.YEAR);
			for(int i=year+5;i>year-10;i--)
			{
				aCommonData=new CommonData(String.valueOf(i),String.valueOf(i));
				list.add(aCommonData);
			}
		}	
		else if(flag==3) //季度
		{
			for(int i=1;i<5;i++)
			{
				aCommonData=new CommonData(String.valueOf(i),"第"+String.valueOf(i)+"季度");
				list.add(aCommonData);
			}
		}
		else if(flag==4) //上半年-下半年
		{
			
			CommonData aCommonData1=new CommonData("1","上半年");
			CommonData aCommonData2=new CommonData("2","下半年");
				
			list.add(aCommonData1);
			list.add(aCommonData2);
		}
		
		
		return list;
	}
	

}
