package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.team.KqShiftClass;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 未排班的个人排班
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 7, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SingKqShiftTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
	   
	   HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	   String a0100=(String)hm.get("a0100");	
	   a0100 = PubFunc.decrypt(a0100);
	   String nbase=(String)hm.get("dbase");
	   nbase = PubFunc.decrypt(nbase);
	   String start_date="";//(String)hm.get("start_date");
	   String end_date="";//(String)hm.get("end_date");
	   ArrayList datelist=new ArrayList();
	   if(start_date==null||start_date.length()<10||end_date==null||end_date.length()<10)
	   {
		   datelist=RegisterDate.getKqDayList(this.getFrameconn());
		   start_date=(String)datelist.get(0);
		   end_date=(String)datelist.get(datelist.size()-1);
	   }
	   start_date=start_date.replaceAll("-","\\.");
	   end_date=end_date.replaceAll("-","\\.");
	   Date s_d=DateUtils.getDate(start_date, "yyyy.MM.dd");
	   Date e_d=DateUtils.getDate(end_date, "yyyy.MM.dd");
	   int spacedate = DateUtils.dayDiff(s_d,e_d);
	   datelist=new ArrayList();
	   Date dd;
	   for (int i = 0; i <=spacedate; i++) {
		   dd=DateUtils.addDays(s_d,i);	
		   datelist.add(DateUtils.format(dd,"yyyy.MM.dd"));	
		}	
	   KqShiftClass kqShiftClass = new KqShiftClass(frameconn, userView);
	   String table_html=kqShiftClass.returnShiftHtml(datelist,"EP"+a0100,nbase);
	   this.getFormHM().put("table_html",table_html);
	   KqUtilsClass kqutilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
       this.getFormHM().put("code_mess",  kqutilsClass.getACodeDesc("EP"+a0100, nbase));  
       this.getFormHM().put("a0100", a0100);
       this.getFormHM().put("dbase", nbase);
	}
	public String returnShiftHtml(ArrayList datelist,String a_code,String nbase)throws GeneralException
	{
		StringBuffer html=new StringBuffer();
	    html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='1' class='ListTable'>");
	    html.append("<thead> <tr> ");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.sunday.short") + "</td>");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.monday.short") + "</td>");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.tuesday.short") + "</td>");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.wednesday.short") + "</td>");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.thursday.short") + "</td>");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.firday.short") + "</td>");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.Saturday.short") + "</td>");
	    html.append(" </tr></thead> ");	    
	    html.append(getDateHtml(datelist,a_code,nbase));
	    html.append("</table>");
	    return html.toString();
	}
	 private String getDateHtml(ArrayList datelist,String a_code,String nbase)throws GeneralException
	    {
	    	 StringBuffer html=new StringBuffer();
	    	 int theRows=datelist.size()/7;
	    	 int mod=datelist.size()%7;
	    	 if(mod>0)
	    	 {
	    		 theRows=theRows+1;
	    	 }
	    	 String fristday=datelist.get(0).toString();
	    	 Date date=DateUtils.getDate(fristday,"yyyy.MM.dd");
	    	 String FirstDay = KqUtilsClass.getWeekName(date);
	    	 if(datelist.size()==28)
	    	 {
	    		 if(!FirstDay.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.sunday")))
	        	 {
	    			 theRows=theRows+1;
	        	 }
	    	 }
	    	 
	    	 String rest=KQRestOper.getRestStrTurn(FirstDay);
	    	 if(rest.indexOf("7")!=-1)
	    	 {
	    		 rest="0,";
	    	 }
	    	 rest=rest.substring(0,1);
	    	 int theFirstDay=Integer.parseInt(rest);
	    	 int theMonthLen=theFirstDay+datelist.size();
	    	 if(7-theFirstDay<mod)
	    		 theRows=theRows+1;
	    	 int n=0;
	    	 int day=0;
	    	 String day_str="";
             String nbase1 = PubFunc.encrypt(nbase);
             String a_code1 = PubFunc.encrypt(a_code);
	    	 for(int i=0;i<theRows;i++)
	    	 {
	    		 html.append("<tr>");
	    		 for(int j=0;j<7;j++)
	    		 {
	    			 n++;
	    			 if(n>theFirstDay&&n<=theMonthLen)
	        		 {
	        			 day=n-theFirstDay-1;
	        			 day_str=datelist.get(day).toString();	        			
	        			 String onDblClick="onDblClick=\"javascript:editClass('"+nbase1+"','"+a_code1+"','"+day_str+"')\"";
	        			 html.append(getOneTd(day_str,onDblClick)); 
	        		 }else
	        		 {
	        			 html.append(getOneTd("&nbsp;","")); 
	        		 }
	    		 }
	    		 html.append("</tr>");
	    	 }
	    	
	    	return html.toString();
	    }    
	 /**
	     * 一个表格
	     * @param str
	     * @return
	     */
	    private String getOneTd(String str,String onDblClick_str)
	    {
	    	StringBuffer str_html=new StringBuffer();
	    	str_html.append("<td align='center' class='RecordRow3' onClick=\"javascript:tr_onclick(this,'')\" "+onDblClick_str+" nowrap>");
	    	str_html.append(str);    	
	    	str_html.append("</td>");
	    	return str_html.toString();
	    }   
}
