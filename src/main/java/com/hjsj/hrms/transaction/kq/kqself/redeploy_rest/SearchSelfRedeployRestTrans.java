package com.hjsj.hrms.transaction.kq.kqself.redeploy_rest;

import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.app_check_in.exchange_class.ExchangeClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 *自助调休
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 22, 2007:11:07:45 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SearchSelfRedeployRestTrans  extends IBusiness {
    private String table_set="q25";
	public void execute() throws GeneralException 
	{
		String frist = (String) this.getFormHM().get("frist_flag");
		/* 获得考勤期间列表 */
		String kq_year = RegisterDate.getNoSealMinYear(this.getFrameconn());
		String kq_duration = "";		
		ArrayList yearlist =(ArrayList)this.getFormHM().get("yearlist");
		SearchAllApp searchAllApp= new SearchAllApp(this.getFrameconn(),this.userView);
		yearlist=searchAllApp.opin_Yearlist(yearlist);
		ArrayList durationlist = new ArrayList();
		/****判断年****/
		if(yearlist.size()==0||yearlist==null)
		{
			this.getFormHM().put("durationlist", durationlist);
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.kq.please"),"",""));
		}
		this.getFormHM().put("yearlist", yearlist);
	
		this.getFormHM().put("kq_year",kq_year);
		RegisterDate registerDate=new RegisterDate();
		durationlist=registerDate.getOneYearDuration(kq_year,this.getFrameconn());
		if(durationlist.size()==0||durationlist==null)
			return;	
	    this.getFormHM().put("durationlist", durationlist);
	    
	    	ArrayList dlist=registerDate.getYearMinYearList(kq_year,this.getFrameconn());
	    	if(dlist.size()>0)
	    	  kq_duration = searchAllApp.getFirstOfList(dlist);
	    	else
	    	  kq_duration = searchAllApp.getFirstOfList(durationlist);
	    		
			if (kq_duration == null || "".equals(kq_duration))
			  return;
		
	    if(frist==null|| "".equals(frist))
			frist="";
	    if("1".equals(frist))
	    	kq_duration = searchAllApp.getFirstOfList(durationlist);
	    this.getFormHM().put("kq_duration", kq_duration);
	    String kq_start_str="";
	    String kq_end_str="";
	    if("3".equals(frist))
	    {
             String start_date=(String)this.getFormHM().get("start_date");
             kq_start_str=start_date.replaceAll("-","\\.");
             String end_date=(String)this.getFormHM().get("end_date");
             kq_end_str=end_date.replaceAll("-","\\.");
	    }else{
	    	ArrayList datelist=registerDate.getOneDurationDate(this.getFrameconn(),kq_duration);
		    kq_start_str=datelist.get(0).toString();
		    kq_end_str=datelist.get(1).toString();
		    this.getFormHM().put("start_date",kq_start_str);
		    this.getFormHM().put("end_date",kq_end_str);
	    }
	    ArrayList fieldlist=(ArrayList)this.getFormHM().get("fieldlist");
		ExchangeClass exchangeClass=new ExchangeClass();		
		if(fieldlist==null||fieldlist.size()<=0)
		{
			fieldlist = DataDictionary.getFieldList(this.table_set,Constant.USED_FIELD_SET);// 字段名	
			fieldlist = exchangeClass.getNewFiledList(fieldlist);
		}
		String column = "";//(String)this.getFormHM().get("column");
		if(column==null||column.length()<=0)
		{
			column=exchangeClass.getColumn(fieldlist);
			column=column+",name";
		}
		String a0100=this.userView.getA0100();
		String nbase=this.userView.getDbname();
		StringBuffer sql=new StringBuffer();
		sql.append("select "+column);
		sql.append(" from "+this.table_set+",kq_class");
		sql.append(" where a0100='"+a0100+"'");
		sql.append(" and nbase='"+nbase+"'");
		sql.append(" and "+this.table_set+".q25z7=kq_class.class_id");		
		sql.append(" and "+this.table_set+"z1>='"+kq_start_str+"'");
		sql.append(" and "+this.table_set+"z1<='"+kq_end_str+"'");
		this.getFormHM().put("sql",sql.toString());
		this.getFormHM().put("fieldlist", fieldlist);
		this.getFormHM().put("column", column);
	}

}
