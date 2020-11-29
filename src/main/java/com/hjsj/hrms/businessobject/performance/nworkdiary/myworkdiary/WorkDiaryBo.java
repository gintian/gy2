package com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PageNumber;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MonthWorkBo.java
 * Description: 
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Jan 8, 2013 2:49:15 PM Jianghe created
 */
public class WorkDiaryBo {
	private Connection con;
	private UserView userView=null;
	private String nbase = "Usr";
	private String a0100 = "";
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
	private WeekUtils weekutils = new WeekUtils();
	public static HashMap workParametersMap=new HashMap();//参数采用静态变量，不用每次都查。
	ContentDAO dao ;
	public String a=null;
	Statement stat=null;	
	ResultSet resultSet=null;
	public WorkDiaryBo()
	{
		
	}
	public WorkDiaryBo(UserView userView,Connection con)
	{
		this.userView=userView;
		this.con=con;
	}
	public WorkDiaryBo(Connection con,UserView userView,String nbase,String a0100) throws GeneralException, SQLException
	{
		this.con = con;
		this.userView = userView;
		this.nbase = nbase;
		this.a0100 = a0100;
		this.dao = new ContentDAO(this.con);
		
	}
	/**
	 * Description: 获取月报页面
	 * @Version1.0 
	 * Jan 9, 2013 11:13:16 AM Jianghe created
	 * @param currentYear
	 * @param currentMonth
	 * @param thisYear
	 * @param thisMonth
	 * @param thisDay
	 * @return
	 */
	public String getTableHtml(String currentYear,String currentMonth,int thisYear,int thisMonth,int thisDay){
		String isOwner = (String)this.userView.getHm().get("isOwner");
		StringBuffer buf = new StringBuffer();
		if(Integer.parseInt(currentMonth)<10)
			currentMonth = "0"+currentMonth;
		String startime=currentYear+"-"+currentMonth+"-1";
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(weekutils.strTodate(startime));
		//与上个月差几天
		int startWeekDay = cal.get(Calendar.DAY_OF_WEEK)-1;
		String endtime=weekutils.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));	
		cal.setTime(weekutils.strTodate(endtime));
		//与下个月差几天
		int endWeekDay = 7-cal.get(Calendar.DAY_OF_WEEK);
		cal.setTime(weekutils.strTodate(endtime));
		int totalDay = cal.get(Calendar.DAY_OF_MONTH);
		ArrayList list = new ArrayList();
		if(startWeekDay>0){
			for (int i = startWeekDay; i >=1 ; i--) {
				cal = new GregorianCalendar();
				cal.setTime(weekutils.strTodate(startime));
				cal.add(GregorianCalendar.DAY_OF_MONTH, -i);
				list.add(cal);
				
			}
		}
		for (int i = 1; i <= totalDay; i++) {
			cal = new GregorianCalendar();
			cal.setTime(weekutils.strTodate(startime));
			cal.add(GregorianCalendar.DAY_OF_MONTH, i-1);
			list.add(cal);
		}
		
		if(endWeekDay>0){
			for (int i = 1; i <= endWeekDay; i++) {
				cal = new GregorianCalendar();
				cal.setTime(weekutils.strTodate(endtime));
				cal.add(GregorianCalendar.DAY_OF_MONTH, i);
				list.add(cal);
			}
		}
		HashMap dataMap = getDataMap(this.a0100,(GregorianCalendar)list.get(0),(GregorianCalendar)list.get(list.size()-1));
		
		buf.append("<div class=\"epm-j-table\">");
		buf.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
		buf.append("<tr>");
		
		
		
		for (int i = 0; i < list.size(); i++) {
			GregorianCalendar gcal = (GregorianCalendar)list.get(i);
			ArrayList dataList = (ArrayList)dataMap.get(sdf.format(gcal.getTime()));
			if(i%7==0&&i!=0){
				buf.append("</tr>");
				buf.append("<tr>");
			}
			String id=gcal.get(GregorianCalendar.YEAR)+"-"+(gcal.get(GregorianCalendar.MONTH)+1)+"-"+gcal.get(GregorianCalendar.DAY_OF_MONTH);
			buf.append("<td id=\""+id+"\"  onClick=\"getNode('"+id+"')\" width=\"14%\" ");
			//为今天添加背景颜色
			if(thisYear==gcal.get(GregorianCalendar.YEAR)&&thisMonth==(gcal.get(GregorianCalendar.MONTH)+1)&&thisDay==gcal.get(GregorianCalendar.DAY_OF_MONTH)){
				buf.append(" bgcolor=\"#FFFFFF\"");
		    }else{
			 buf.append(" bgcolor=\"#FFFFFF\"");
		    }
			if(i%7==0){
				buf.append(" class=\"epm-j-left-td\"");
			}
		    buf.append(">");
            
			if(thisYear==gcal.get(GregorianCalendar.YEAR)&&thisMonth==(gcal.get(GregorianCalendar.MONTH)+1)&&thisDay==gcal.get(GregorianCalendar.DAY_OF_MONTH)){
				      
				buf.append("<div class=\"epm-j-jintian\">");
				buf.append("<span>"+(gcal.get(GregorianCalendar.MONTH)+1)+"月"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"日"+"</span>今天");
				buf.append("</div>");
			}else{
				    //第一排
					if(i/7<=0){
						buf.append("<h3>");
						      buf.append(getWeekIndex(gcal.get(Calendar.DAY_OF_WEEK)-1)+" "+gcal.get(GregorianCalendar.DAY_OF_MONTH));
						buf.append("</h3>");
					}else{
						buf.append("<h3>");
					      buf.append(gcal.get(GregorianCalendar.DAY_OF_MONTH));
					    buf.append("</h3>");
					}
			}
			    if(dataList!=null){
					//画数据
						for (int j = 0; j < dataList.size(); j++) {
							LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
							String title = (String)bean.get("title");
							if(title.length()>8)
  				            	  title=title.substring(0,8)+"...";
							if(j<=5){
							   buf.append("<div class=\"bh-clear\"></div>");
							   buf.append("<div class=\"epm-j-jilu\" style=\"cursor:pointer;\" onclick=\"showDetail('"+isOwner+"','"+(String)bean.get("p0100")+"','"+bean.get("record_num")+"')\">");
							     buf.append("<div class=\"epm-j-yuan\">");
                               
	                               if("0".equals(bean.get("type"))){
	                            	   //buf.append("<div class=\"epm-j-yuan-left\"></div>");
	                            	   //buf.append("<a class=\"epm-j-yuan-center\"><span>●</span>"+title+"</a>");
	                            	   //buf.append("<div class=\"epm-j-yuan-right\"></div>");
	                            	   buf.append("<div class='rtop'><div class='r1'></div><div class='r2'></div><div class='r3'></div><div class='r4'></div></div>");
	                            	   buf.append("<div class='epm-gf-yuanjiao'>&nbsp;<a class='epm-j-yuan-center1'><span>●</span>"+title+"</a></div>");        
	                            	   buf.append("<div class='rbottom'><div class='r4'></div><div class='r3'></div><div class='r2'></div><div class='r1'></div></div>");   
	                               }else{
	                            	   //buf.append("<div class=\"epm-j-yuan-buyi\"><span>●</span>"+title+"</div>");
	                            	   buf.append("<div class='rtop_jh'><div class='lrunfinish_r1'></div><div class='lrunfinish_r2'></div><div class='lrunfinish_r3'></div><div class='lrunfinish_r4'></div></div>");
	                            	   buf.append("<div class='epm-jh-yuanjiao'>&nbsp;<a class='epm-j-yuan-center1'><span>●</span>"+title+"</a></div>");        
	                            	   buf.append("<div class='rbottom_jh'><div class='lrunfinish_r4'></div><div class='lrunfinish_r3'></div><div class='lrunfinish_r2'></div><div class='lrunfinish_r1'></div></div>"); 
	                               }
                                  buf.append("</div>");
                               buf.append("</div>");
							}else{
                               buf.append("<div class=\"bh-clear\"></div>");
                               buf.append("<div class=\"epm-others\">");
                               buf.append("<a href=\"###\" onclick=\"window.location.href='/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&isOwner="+isOwner+"&year="+gcal.get(GregorianCalendar.YEAR)+"&month="+(gcal.get(GregorianCalendar.MONTH)+1)+"&day="+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"&frompage=2'\">还有"+(dataList.size()-j)+"个...</a>");
                               buf.append("</div>");
                               break;
							}
						}
			    }
			buf.append("</td>"); 
		}
        buf.append("</tr>");
		buf.append("</table>");
		buf.append("</div>");
		return buf.toString();
	}
	public String getWeekIndex(int day){
		String weekindex="";
		if(day==0)
			weekindex="周日";
		if(day==1)
			weekindex="周一";
		if(day==2)
			weekindex="周二";
		if(day==3)
			weekindex="周三";
		if(day==4)
			weekindex="周四";
		if(day==5)
			weekindex="周五";
		if(day==6)
			weekindex="周六";
		return weekindex;
	}
	public String getAfterDate(String flag,String thisDate){
		String afterDate = "";
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(weekutils.strTodate(thisDate));
	    if("previous".equals(flag)){
	    	cal.add(GregorianCalendar.MONTH, -1);
        }else if("today".equals(flag)){
        	cal = new GregorianCalendar();
        }else if("next".equals(flag)){
        	cal.add(GregorianCalendar.MONTH, 1);
        }
	    afterDate = cal.get(GregorianCalendar.YEAR)+"-"+(cal.get(GregorianCalendar.MONTH)+1)+"-"+cal.get(GregorianCalendar.DAY_OF_MONTH);
	    return afterDate;
	}
	public String getAfterYearDate(String flag,String thisDate){
		String afterDate = "";
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.YEAR, Integer.parseInt(thisDate));
		if("previous".equals(flag)){
			cal.add(GregorianCalendar.YEAR, -1);
		}else if("thisyear".equals(flag)){
			cal = new GregorianCalendar();
		}else if("next".equals(flag)){
			cal.add(GregorianCalendar.YEAR, 1);
		}
		afterDate = cal.get(GregorianCalendar.YEAR)+"";
		return afterDate;
	}
	/**
	 * Description: 获取小时列表
	 * @Version1.0 
	 * Jan 10, 2013 11:24:59 AM Jianghe created
	 * @param flag
	 * @return
	 */
	public ArrayList getHourList(){
		ArrayList list = new ArrayList();
		String hourValue="";
		for (int i = 0; i < 24; i++) {
			hourValue=i+"";
			if(i<10){
				hourValue="0"+i;
			}
			list.add(new CommonData(i+"",hourValue));
		}
		return list;
	}
	public void saveContent(String title,String type,String start_time,String startHour,String end_time,String endHour,String content,String startMinute,String endMinute){
		GregorianCalendar cal_start = new GregorianCalendar(); 
		cal_start.setTime(weekutils.strTodate(start_time));
		GregorianCalendar cal_end = new GregorianCalendar(); 
		cal_end.setTime(weekutils.strTodate(end_time));
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rs = null;
		

		String startStrDate = sdf.format(cal_start.getTime());
		String endStrDate = sdf.format(cal_end.getTime());
		int betweenDays = 0;  
		GregorianCalendar c_b = new GregorianCalendar(); 
		c_b.setTime(cal_start.getTime());
		GregorianCalendar c_e = new GregorianCalendar(); 
		c_e.setTime(cal_end.getTime());
		   
		   while(c_b.before(c_e)){
			   betweenDays++;
		    c_b.add(Calendar.DAY_OF_YEAR, 1);
		   }
		int thep0100=0;
		try
		{
		    //rs = dao.search("select p0100,p0104 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"<="+endStrDate+" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+">="+startStrDate+" order by p0100 asc");
		    for (int i = 0; i <= betweenDays; i++) {
		    	GregorianCalendar gcal = new GregorianCalendar(); 
		    	gcal.setTime(cal_start.getTime());
		    	gcal.add(GregorianCalendar.DAY_OF_MONTH, i);
		    	String startDate = sdf.format(gcal.getTime());
		    	rs = dao.search("select p0100,p0104 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and a0100='"+this.a0100+"' and state=0 order by p0100 asc");
		    	if(rs.next()){
		    		
		    	}else{
		    		RecordVo vo = new RecordVo("p01");
		    		
		    		int p0100 = this.getP0100();
		    		vo.setInt("p0100", p0100);
		    		vo.setInt("state", 0);
		    		vo.setString("b0110", userView.getUserOrgId());
		    		vo.setString("e0122", userView.getUserDeptId());
		    		vo.setString("e01a1", userView.getUserPosId());
		    		vo.setString("nbase", userView.getDbname());
		    		vo.setString("a0100", userView.getA0100());
		    		vo.setString("a0101", userView.getUserFullName());
		    		vo.setDate("p0104", gcal.getTime());
		    		vo.setDate("p0106", gcal.getTime());
		    		dao.addValueObject(vo);
		    	}
			}
		    rs = dao.search("select p0100,p0104 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"<="+endStrDate+" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+">="+startStrDate+" and a0100='"+this.a0100+"' and state=0 order by p0100 desc");
	    	if(rs.next())
	    		thep0100 = rs.getInt("p0100");
		    StringBuffer insertBuf = new StringBuffer();
		    cal_start.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(startHour));
		    cal_start.set(GregorianCalendar.MINUTE, Integer.parseInt(startMinute));
		    cal_end.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(endHour));
		    cal_end.set(GregorianCalendar.MINUTE, Integer.parseInt(endMinute));
		    
		    insertBuf.append("insert into per_diary_content(p0100,record_num,b0110,e0122,e01a1,nbase,a0100,a0101,content,title,type,start_time,end_time) values ");
		    String d1 = sdf1.format(cal_start.getTime());
		    String d2 = sdf1.format(cal_end.getTime());
		    switch(Sql_switcher.searchDbServer())
		    {
				case Constant.ORACEL:
			    {
			    	d1="to_date('"+d1+"','yyyy-mm-dd hh24:mi:ss')";
			    	d2="to_date('"+d2+"','yyyy-mm-dd hh24:mi:ss')";
			    	break;
			    }
				case Constant.MSSQL:
			    {
			    	d1="'"+d1+"'";
			    	d2="'"+d2+"'";
			    	break;
			    }
			}	
		    insertBuf.append("("+thep0100+","+getMaxRecord_num(thep0100)+",'"+userView.getUserOrgId()+"','"+userView.getUserDeptId()+"','"+userView.getUserPosId()+"','"+userView.getDbname()+"','"+userView.getA0100()+"','"+userView.getUserFullName()+"','"+content+"','"+title+"',"+type+","+d1+","+d2+")");
		    dao.update(insertBuf.toString());
		    
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
	}
	public void updateContent(String p0100,String record_num,String title,String type,String start_time,String startHour,String end_time,String endHour,String content,String startMinute,String endMinute){
		GregorianCalendar cal_start = new GregorianCalendar(); 
		cal_start.setTime(weekutils.strTodate(start_time));
		GregorianCalendar cal_end = new GregorianCalendar(); 
		cal_end.setTime(weekutils.strTodate(end_time));
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rs = null;
		
		
		String startStrDate = sdf.format(cal_start.getTime());
		String endStrDate = sdf.format(cal_end.getTime());
		int betweenDays = 0;  
		GregorianCalendar c_b = new GregorianCalendar(); 
		c_b.setTime(cal_start.getTime());
		GregorianCalendar c_e = new GregorianCalendar(); 
		c_e.setTime(cal_end.getTime());
		
		while(c_b.before(c_e)){
			betweenDays++;
			c_b.add(Calendar.DAY_OF_YEAR, 1);
		}
		int thep0100=0;
		try
		{
			//rs = dao.search("select p0100,p0104 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"<="+endStrDate+" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+">="+startStrDate+" order by p0100 asc");
			for (int i = 0; i <= betweenDays; i++) {
				GregorianCalendar gcal = new GregorianCalendar(); 
				gcal.setTime(cal_start.getTime());
				gcal.add(GregorianCalendar.DAY_OF_MONTH, i);
				String startDate = sdf.format(gcal.getTime());
				rs = dao.search("select p0100,p0104 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and a0100='"+this.a0100+"' order by p0100 asc");
				if(rs.next()){
					
				}else{
					RecordVo vo = new RecordVo("p01");
					int p_p0100 = this.getP0100();
					vo.setInt("p0100", p_p0100);
					vo.setInt("state", 0);
					vo.setString("b0110", userView.getUserOrgId());
					vo.setString("e0122", userView.getUserDeptId());
					vo.setString("e01a1", userView.getUserPosId());
					vo.setString("nbase", userView.getDbname());
					vo.setString("a0100", userView.getA0100());
					vo.setString("a0101", userView.getUserFullName());
					vo.setDate("p0104", gcal.getTime());
					vo.setDate("p0106", gcal.getTime());
					dao.addValueObject(vo);
				}
			}
			rs = dao.search("select p0100,p0104 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"<="+endStrDate+" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+">="+startStrDate+" and a0100='"+this.a0100+"' order by p0100 desc");
			if(rs.next())
				thep0100 = rs.getInt("p0100");
			StringBuffer insertBuf = new StringBuffer();
			cal_start.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(startHour));
			cal_start.set(GregorianCalendar.MINUTE, Integer.parseInt(startMinute));
			cal_end.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(endHour));
			cal_end.set(GregorianCalendar.MINUTE, Integer.parseInt(endMinute));
			
			insertBuf.append("insert into per_diary_content(p0100,record_num,b0110,e0122,e01a1,nbase,a0100,a0101,content,title,type,start_time,end_time) values ");
			String d1 = sdf1.format(cal_start.getTime());
		    String d2 = sdf1.format(cal_end.getTime());
		    switch(Sql_switcher.searchDbServer())
		    {
				case Constant.ORACEL:
			    {
			    	d1="to_date('"+d1+"','yyyy-mm-dd hh24:mi:ss')";
			    	d2="to_date('"+d2+"','yyyy-mm-dd hh24:mi:ss')";
			    	break;
			    }
				case Constant.MSSQL:
			    {
			    	d1="'"+d1+"'";
			    	d2="'"+d2+"'";
			    	break;
			    }
			}	
			insertBuf.append("("+thep0100+","+record_num+",'"+userView.getUserOrgId()+"','"+userView.getUserDeptId()+"','"+userView.getUserPosId()+"','"+userView.getDbname()+"','"+userView.getA0100()+"','"+userView.getUserFullName()+"','"+content+"','"+title+"',"+type+","+d1+","+d2+")");
			String deleteSql = "delete from per_diary_content where p0100='"+p0100+"' and record_num='"+record_num+"'";
			dao.update(deleteSql);
			dao.update(insertBuf.toString());
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void deleteContent(String p0100,String record_num){
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			String deleteSql = "delete from per_diary_content where p0100='"+p0100+"' and record_num='"+record_num+"'";
			dao.update(deleteSql);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Description: 通过id查找记录
	 * @Version1.0 
	 * Jan 12, 2013 11:05:38 AM Jianghe created
	 * @param p0100
	 * @param record_num
	 * @return
	 */
	public LazyDynaBean queryById(String p0100,String record_num){
		LazyDynaBean bean = new LazyDynaBean();;
		RowSet rowSet = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			String sTime = "";
			String eTime = "";
			switch(Sql_switcher.searchDbServer())
		    {
				case Constant.ORACEL:
			    {
			    	sTime="to_char(start_time,'yyyy-MM-dd hh24:mi:ss')";
			    	eTime="to_char(end_time,'yyyy-MM-dd hh24:mi:ss')";
			    	break;
			    }
				case Constant.MSSQL:
			    {
			    	sTime="start_time";
			    	eTime="end_time";
			    	break;
			    }
			}	
			String sql = "select p0100,record_num,b0110,e0122,e01a1,nbase,a0100,a0101,content,title,type,"+sTime+" as start_time,"+eTime+" as end_time from per_diary_content  where p0100="+p0100+" and record_num="+record_num+"";
			rowSet = dao.search(sql);
		    if(rowSet.next())
	        {
		    	bean.set("p0100", rowSet.getInt("p0100")+"");
        		bean.set("record_num", rowSet.getInt("record_num")+"");
        		bean.set("b0110", rowSet.getString("b0110"));
        		bean.set("e0122", rowSet.getString("e0122"));
        		bean.set("e01a1", rowSet.getString("e01a1"));
        		bean.set("nbase", rowSet.getString("nbase"));
        		bean.set("a0100", rowSet.getString("a0100"));
        		bean.set("a0101", rowSet.getString("a0101"));
        		bean.set("content", Sql_switcher.readMemo(rowSet, "content"));
        		bean.set("title", rowSet.getString("title"));
        		bean.set("type", rowSet.getInt("type")+"");
        		switch(Sql_switcher.searchDbServer())
    		    {
    				case Constant.ORACEL:
    			    {
    			    	bean.set("start_time", sdf1.parse(rowSet.getString("start_time")));
    	        		bean.set("end_time", sdf1.parse(rowSet.getString("end_time")));
    	        		break;
    			    }
    				case Constant.MSSQL:
    			    {
    			    	bean.set("start_time", rowSet.getDate("start_time"));
    	        		bean.set("end_time", rowSet.getDate("end_time"));
    	        		break;
    			    }
    			}
	        }
		       
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
	    return  bean;
	}
	
	public LazyDynaBean getUserDetail(String a0100){
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rowSet = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			String sql = "select * from usra01 where a0100="+a0100;
			rowSet = dao.search(sql);
		    if(rowSet.next())
	        {
        		bean.set("b0110", rowSet.getString("b0110"));
        		bean.set("e0122", rowSet.getString("e0122"));
        		bean.set("e01a1", rowSet.getString("e01a1"));
        		bean.set("a0101", rowSet.getString("a0101"));
	        }
		       
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
	    return  bean;
	}
	/**
	 * 取p0100值
	 * @return
	 */
	public int getP0100()
	{
		int id=1;
		try{
			IDGenerator  idg=new IDGenerator(2,this.con);
			id=Integer.parseInt(idg.getId("P01.P0100"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}
	/**
	 * 取id值
	 * @return
	 * @throws SQLException 
	 */
	public int getMaxRecord_num(int p0100) 
	{
		int maxid=0;
		RowSet rowSet = null;
		try{
			
			ContentDAO dao = new ContentDAO(this.con);
			rowSet = dao.search("select max(record_num) from per_diary_content where p0100="+p0100);
		        while(rowSet.next())
		        {
		        	String id = rowSet.getString(1);
		        	if((id!=null) && (id.trim().length()>0))
		        	maxid=Integer.parseInt(id);	
		        }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
	    return  ++maxid;	
	}
	/**
	 * Description: 导出excel
	 * @Version1.0 
	 * Jan 25, 2013 11:28:54 AM Jianghe created
	 * @param year
	 * @param month
	 * @param day
	 * @param weekflag
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String creatExcel(String state,String syear,String smonth,String sday,String eyear,String emonth,String eday) throws GeneralException, SQLException{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		String sql = "";
		String startDate = "";
		String endDate = "";
		GregorianCalendar cal_start = new GregorianCalendar(); 
		
		GregorianCalendar cal_end = new GregorianCalendar(); 
		
		String sTime1 = "";
		String eTime1 = "";
		switch(Sql_switcher.searchDbServer())
	    {
			case Constant.ORACEL:
		    {
		    	sTime1="to_char(pdc.start_time,'yyyy-MM-dd hh24:mi:ss')";
		    	eTime1="to_char(pdc.end_time,'yyyy-MM-dd hh24:mi:ss')";
		    	break;
		    }
			case Constant.MSSQL:
		    {
		    	sTime1="pdc.start_time";
		    	eTime1="pdc.end_time";
		    	break;
		    }
		}	
		sql+="select pdc.p0100,pdc.record_num,pdc.b0110,pdc.e0122,pdc.e01a1,pdc.nbase,pdc.a0100,pdc.a0101,pdc.content,pdc.title,pdc.type,"+sTime1+" as start_time,"+eTime1+" as end_time from per_diary_content pdc where pdc.a0100='"+this.a0100+"'"+" and exists(select null from p01 where p0100=pdc.p0100 and state='0')";
		//日报
		if("1".equals(state)){
			if(Integer.parseInt(smonth)<10)
				smonth = "0"+smonth;
			if(Integer.parseInt(emonth)<10)
				emonth = "0"+emonth;
			cal_start.setTime(weekutils.strTodate(syear+"-"+smonth+"-"+sday));
			cal_end.setTime(weekutils.strTodate(eyear+"-"+emonth+"-"+eday));
		}else if("2".equals(state)){
			//周报
			if(Integer.parseInt(smonth)<10)
				smonth = "0"+smonth;
			if(Integer.parseInt(emonth)<10)
				emonth = "0"+emonth;
			cal_start.setTime(weekutils.strTodate(syear+"-"+smonth+"-"+sday));
			cal_end.setTime(weekutils.strTodate(eyear+"-"+emonth+"-"+eday));
		}else if("3".equals(state)){
			//月报
			if(Integer.parseInt(smonth)<10)
				smonth = "0"+smonth;
			if(Integer.parseInt(emonth)<10)
				emonth = "0"+emonth;
			sday = "01";
			eday = weekutils.lastMonthStr(Integer.parseInt(eyear),Integer.parseInt(emonth));
			cal_start.setTime(weekutils.strTodate(syear+"-"+smonth+"-"+sday));
			cal_end.setTime(weekutils.strTodate(eday));
		}else{
			//年报
			smonth = "01";
			sday = "01";
			emonth = "12";
			eday = "31";
			cal_start.setTime(weekutils.strTodate(syear+"-"+smonth+"-"+sday));
			cal_end.setTime(weekutils.strTodate(eyear+"-"+emonth+"-"+eday));
		}
		startDate = sdf.format(cal_start.getTime());
		endDate = sdf.format(cal_end.getTime());
		//System.out.println(startDate);
		//System.out.println(endDate);
//		if(startDate.equals(endDate)){
//			sql+=" and "+startDate+"<="+Sql_switcher.year("end_time")+"*10000+"+Sql_switcher.month("end_time")+"*100+"+Sql_switcher.day("end_time");
//			sql+=" and "+startDate+">="+Sql_switcher.year("start_time")+"*10000+"+Sql_switcher.month("start_time")+"*100+"+Sql_switcher.day("start_time");
//		}else{
			sql+=" and ( ("+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+"<="+startDate;
			sql+=" and "+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+">="+startDate+")";
			sql+=" or ("+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+"<="+endDate;
			sql+=" and "+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+">="+startDate+") )";
//		}
		sql+=" order by pdc.start_time asc";
		//System.out.println(sql);
		try{
			
			ContentDAO dao = new ContentDAO(this.con);
			rowSet = dao.search(sql);
	        while(rowSet.next())
	        {
	        	LazyDynaBean bean = new LazyDynaBean();
	        	bean.set("p0100", rowSet.getInt("p0100")+"");
        		bean.set("record_num", rowSet.getInt("record_num")+"");
        		bean.set("b0110", rowSet.getString("b0110"));
        		bean.set("e0122", rowSet.getString("e0122"));
        		bean.set("e01a1", rowSet.getString("e01a1"));
        		bean.set("nbase", rowSet.getString("nbase"));
        		bean.set("a0100", rowSet.getString("a0100"));
        		bean.set("a0101", rowSet.getString("a0101"));
        		bean.set("content", Sql_switcher.readMemo(rowSet, "content"));
        		bean.set("title", rowSet.getString("title"));
        		bean.set("type", rowSet.getInt("type")+"");
        		switch(Sql_switcher.searchDbServer())
    		    {
    				case Constant.ORACEL:
    			    {
    			    	bean.set("start_time", sdf1.parse(rowSet.getString("start_time")));
    	        		bean.set("end_time", sdf1.parse(rowSet.getString("end_time")));
    	        		break;
    			    }
    				case Constant.MSSQL:
    			    {
    			    	bean.set("start_time", rowSet.getDate("start_time"));
    	        		bean.set("end_time", rowSet.getDate("end_time"));
    	        		break;
    			    }
    			}
        		list.add(bean);
	        }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		HSSFFont font1 = wb.createFont(); //设置样式
		font1.setFontHeightInPoints((short) 20);
//		font1.setBoldweight((short) 500);
		font1.setBold(true);
		font1.setColor(HSSFFont.COLOR_NORMAL);
		HSSFFont font4 = wb.createFont(); //设置样式
		font4.setFontHeightInPoints((short) 15);
		font1.setBold(true);
		font4.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font1);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style2.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style2.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style2.setBorderBottom(BorderStyle.valueOf((short)1));
        
        
        HSSFCellStyle style3 = wb.createCellStyle();
        style3.setFont(font4);
        style3.setAlignment(HorizontalAlignment.LEFT);
        style3.setVerticalAlignment(VerticalAlignment.CENTER);
        style3.setWrapText(true);
        style3.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style3.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style3.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style3.setBorderBottom(BorderStyle.valueOf((short)1));
		// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setWrapText(true);
		style1.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style1.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style1.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style1.setBorderBottom(BorderStyle.valueOf((short)1));
        HSSFCellStyle styleContent = wb.createCellStyle();
        styleContent.setFont(font2);
        styleContent.setAlignment(HorizontalAlignment.LEFT);
        styleContent.setVerticalAlignment(VerticalAlignment.CENTER);
        styleContent.setWrapText(true);
        styleContent.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        styleContent.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        styleContent.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        styleContent.setBorderBottom(BorderStyle.valueOf((short)1));
		// style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		// styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleCol0_title = dataStyle(wb);
		styleCol0_title.setFont(font2);
		// styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0_title.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleF1 = dataStyle(wb);
		styleF1.setAlignment(HorizontalAlignment.RIGHT);
		HSSFFont font3 = wb.createFont(); //设置样式
		font3.setFontHeightInPoints((short) 3);
		styleF1.setFont(font3);
		styleF1.setWrapText(true);
		HSSFDataFormat df1 = wb.createDataFormat();
		styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

		HSSFCellStyle styleF2 = dataStyle(wb);
		styleF2.setAlignment(HorizontalAlignment.RIGHT);
		styleF2.setFont(font3);
		styleF2.setWrapText(true);
		HSSFDataFormat df2 = wb.createDataFormat();
		styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

		HSSFCellStyle styleF3 = dataStyle(wb);
		styleF3.setAlignment(HorizontalAlignment.RIGHT);
		styleF3.setFont(font3);
		styleF3.setWrapText(true);
		HSSFDataFormat df3 = wb.createDataFormat();
		styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

		HSSFCellStyle styleF4 = dataStyle(wb);
		styleF4.setAlignment(HorizontalAlignment.RIGHT);
		styleF4.setFont(font3);
		styleF4.setWrapText(true);
		HSSFDataFormat df4 = wb.createDataFormat();
		styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

		HSSFCellStyle styleF5 = dataStyle(wb);
		styleF5.setAlignment(HorizontalAlignment.RIGHT);
		styleF5.setFont(font3);
		styleF5.setWrapText(true);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
		HSSFRow row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		row.setHeight((short) 1000);
		HSSFCell cell = null;

		sheet.setColumnWidth((0), 15 * 300);	//设置列宽
		sheet.setColumnWidth((1), 15 * 600);	//设置列宽
		sheet.setColumnWidth((2), 15 * 300);    //设置列宽
		sheet.setColumnWidth((3), 15 * 300);    //设置列宽
			// 设置第一行的数据
		
		
		
		
		String date = "";
		//日报
		if("1".equals(state)){
			if(!"".equals((syear).trim())){
			    date+=syear+"年";
			}
			if(!"".equals((smonth).trim())){
				date+=smonth+"月";
			}
			if(!"".equals((sday).trim())){
				date+=sday+"日";
			}
		}else if("2".equals(state)){
			//周报
			date+=syear+"年"+smonth+"月"+sday+"日"+" 至 "+eyear+"年"+emonth+"月"+eday+"日";
		}else if("3".equals(state)){
			//月报
			if(!"".equals((syear).trim())){
			    date+=syear+"年";
			}
			if(!"".equals((smonth).trim())){
				date+=smonth+"月";
			}
		}else{
			//年报
			date+=syear+"年";
		}
		StringBuffer sb = new StringBuffer("日历   "+date);
		if (row.getRowNum() == 0) {
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			cell.setCellValue(cellStr(sb.toString()));
			cell.setCellStyle(style2);
            
			cell = row.getCell(1);
			if(cell == null){
				cell = row.createCell(1);
			}
			cell.setCellStyle(style2);
			
			cell = row.getCell(2);
			if(cell == null){
				cell = row.createCell(2);
			}
			cell.setCellStyle(style2);
			
			cell = row.getCell(3);
			if(cell == null){
				cell = row.createCell(3);
			}
			cell.setCellStyle(style2);
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 3);// 合并第一行的单元格
			
			row = sheet.createRow(row.getRowNum() + 1); // 第二行开始
			row.setHeight((short) 500);
			cell = row.getCell(0); //第二行第一列
			if(cell == null){
				cell = row.createCell(0);
			}
			LazyDynaBean abean = this.getUserDetail(this.a0100);
			String theb0110 = AdminCode.getCodeName("UN",(String)abean.get("b0110"));
			String thee0122 = AdminCode.getCodeName("UM",(String)abean.get("e0122"));
			String thea0101 = (String)abean.get("a0101");
			theb0110 = theb0110==null?"":theb0110;
			thee0122 = thee0122==null?"":thee0122;
			thea0101 = thea0101==null?"":thea0101;
			
			cell.setCellValue("单位:"+theb0110+"   "+"部门:"+thee0122+"   "+"姓名:"+thea0101);
			cell.setCellStyle(style3);
			
			cell = row.getCell(1);
			if(cell == null){
				cell = row.createCell(1);
			}
			cell.setCellStyle(style3);
			
			cell = row.getCell(2);
			if(cell == null){
				cell = row.createCell(2);
			}
			cell.setCellStyle(style3);
			
			cell = row.getCell(3);
			if(cell == null){
				cell = row.createCell(3);
			}
			cell.setCellStyle(style3);
			ExportExcelUtil.mergeCell(sheet, 1, (short) 0, 1, (short) 3);// 合并第二行的单元格
			
			row = sheet.createRow(row.getRowNum() + 1); // 第三行开始
		}
		cell = row.getCell(0); //第二行第一列
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("标题");
		cell.setCellStyle(style1);
		
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("内容");
		cell.setCellStyle(style1);

		cell = row.getCell(2);
		if(cell == null){
			cell = row.createCell(2);
		}
		cell.setCellValue("开始时间");
		cell.setCellStyle(style1);

		cell = row.getCell(3);
		if(cell == null){
			cell = row.createCell(3);
		}
		cell.setCellValue("结束时间");
		cell.setCellStyle(style1);
		
		
		
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			row = sheet.getRow(i+3);
			if (row == null) {
				row = sheet.createRow(i+3);
			}
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			//AdminCode.getCodeName("UN",(String)bean.get("b0110"))
			//AdminCode.getCodeName("UM",(String)bean.get("e0122"))
			//(String)bean.get("a0101")
			cell.setCellValue((String)bean.get("title"));
			cell.setCellStyle(style1);
			cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			cell.setCellValue((String)bean.get("content"));
			cell.setCellStyle(styleContent);
			cell = row.getCell(2);
			if (cell == null) {
				cell = row.createCell(2);
			}
            if("0".equals((String)bean.get("type"))){//跨天
            	cell.setCellValue(sdf2.format((Date)bean.get("start_time")));
            }else{
            	cell.setCellValue(sdf1.format((Date)bean.get("start_time")));
            }
			cell.setCellStyle(style1);
			cell = row.getCell(3);
			if (cell == null) {
				cell = row.createCell(3);
			}
			if("0".equals((String)bean.get("type"))){//跨天
				cell.setCellValue(sdf2.format((Date)bean.get("end_time")));
            }else{
            	cell.setCellValue(sdf1.format((Date)bean.get("end_time")));
            }
			
			cell.setCellStyle(style1);

		}
		
		String outName = new Date().getTime() + ".xls";
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outName);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}
		outName = outName.replace(".xls", "#");
		return outName;
	}
	public HSSFRichTextString cellStr(String context) {
		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	public String decimalwidth(int len) {

		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
			decimal.append(".");
		for (int i = 0; i < len; i++) {
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		// style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}
	/**
	 * Description: 获取每天所对应的记录
	 * @Version1.0 
	 * Jan 11, 2013 10:59:20 AM Jianghe created
	 * @param a0100
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public HashMap getDataMap(String a0100,GregorianCalendar startCalendar,GregorianCalendar endCalendar){
		String startDate = sdf.format(startCalendar.getTime());
		String endDate = sdf.format(endCalendar.getTime());
        HashMap map = new HashMap();
		RowSet rowSet = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);String sTime1 = "";
			String eTime1 = "";
			switch(Sql_switcher.searchDbServer())
		    {
				case Constant.ORACEL:
			    {
			    	sTime1="to_char(pdc.start_time,'yyyy-MM-dd hh24:mi:ss')";
			    	eTime1="to_char(pdc.end_time,'yyyy-MM-dd hh24:mi:ss')";
			    	break;
			    }
				case Constant.MSSQL:
			    {
			    	sTime1="pdc.start_time";
			    	eTime1="pdc.end_time";
			    	break;
			    }
			}	
			String sql = "select pdc.p0100,pdc.record_num,pdc.b0110,pdc.e0122,pdc.e01a1,pdc.nbase,pdc.a0100,pdc.a0101,pdc.content,pdc.title,pdc.type,"+sTime1+" as start_time,"+eTime1+" as end_time from per_diary_content pdc where pdc.a0100='"+a0100+"'"+" and exists(select null from p01 where p0100=pdc.p0100 and state='0')";
			sql+=" and ( ("+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+">="+startDate;
			sql+=" and "+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+"<="+endDate+")";
			sql+=" or ("+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+"<="+endDate;
			sql+=" and "+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+">="+startDate+") )";
			sql+=" order by pdc.start_time desc";
				
			rowSet = dao.search(sql);
			
			   while(rowSet.next())
		        {
					int betweenDays = 0;  
					Date date_s_time = new Date();
					Date date_e_time = new Date();
					switch(Sql_switcher.searchDbServer())
	    		    {
	    				case Constant.ORACEL:
	    			    {
	    			    	date_s_time = sdf1.parse(rowSet.getString("start_time"));
	    			    	date_e_time = sdf1.parse(rowSet.getString("end_time"));
	    	        		break;
	    			    }
	    				case Constant.MSSQL:
	    			    {
	    			    	date_s_time = rowSet.getDate("start_time");
	    			    	date_e_time = rowSet.getDate("end_time");
	    	        		break;
	    			    }
	    			}
					GregorianCalendar c_b = new GregorianCalendar(); 
					c_b.setTime(date_s_time);
					GregorianCalendar c_e = new GregorianCalendar(); 
					c_e.setTime(date_e_time);
					   
				    while(c_b.before(c_e)){
				      c_b.add(Calendar.DAY_OF_MONTH, 1);
				      betweenDays++;
				    }
		        	LazyDynaBean bean = new LazyDynaBean();
		        	
					   for (int i = 0; i <= betweenDays-1; i++) {
					    	GregorianCalendar gcal = new GregorianCalendar();
					    	switch(Sql_switcher.searchDbServer())
			    		    {
			    				case Constant.ORACEL:
			    			    {
			    	        		gcal.setTime(sdf1.parse(rowSet.getString("start_time")));
			    	        		break;
			    			    }
			    				case Constant.MSSQL:
			    			    {
			    			    	gcal.setTime(rowSet.getDate("start_time"));
			    	        		break;
			    			    }
			    			}
					    	gcal.add(GregorianCalendar.DAY_OF_MONTH, i);
					    	String key = sdf.format(gcal.getTime());
					    	if((ArrayList)map.get(key)!=null ){
					    		ArrayList list = (ArrayList)map.get(key);
					    		boolean flag = true;
					    		for (int j = 0; j < list.size(); j++) {
					    			LazyDynaBean abean = new LazyDynaBean();
					    			abean = (LazyDynaBean)list.get(j);
					    			if((String)abean.get("p0100")==String.valueOf(rowSet.getInt("p0100")) && (String)abean.get("record_num")==String.valueOf(rowSet.getInt("record_num"))){
					    				flag = false;
					    				break;
					    			}
								}
					    		if(flag){
								    bean.set("p0100", rowSet.getInt("p0100")+"");
					        		bean.set("record_num", rowSet.getInt("record_num")+"");
					        		bean.set("b0110", rowSet.getString("b0110"));
					        		bean.set("e0122", rowSet.getString("e0122"));
					        		bean.set("e01a1", rowSet.getString("e01a1"));
					        		bean.set("nbase", rowSet.getString("nbase"));
					        		bean.set("a0100", rowSet.getString("a0100"));
					        		bean.set("a0101", rowSet.getString("a0101"));
					        		bean.set("content", Sql_switcher.readMemo(rowSet, "content"));
					        		bean.set("title", rowSet.getString("title"));
					        		bean.set("type", rowSet.getInt("type")+"");
					        		switch(Sql_switcher.searchDbServer())
					    		    {
					    				case Constant.ORACEL:
					    			    {
					    			    	bean.set("start_time", sdf1.parse(rowSet.getString("start_time")));
					    	        		bean.set("end_time", sdf1.parse(rowSet.getString("end_time")));
					    	        		break;
					    			    }
					    				case Constant.MSSQL:
					    			    {
					    			    	bean.set("start_time", rowSet.getDate("start_time"));
					    	        		bean.set("end_time", rowSet.getDate("end_time"));
					    	        		break;
					    			    }
					    			}
					        		list.add(bean);
					        		map.put(key, list);
					    		}
						    }else{
							    ArrayList list = new ArrayList();
							    bean.set("p0100", rowSet.getInt("p0100")+"");
				        		bean.set("record_num", rowSet.getInt("record_num")+"");
				        		bean.set("b0110", rowSet.getString("b0110"));
				        		bean.set("e0122", rowSet.getString("e0122"));
				        		bean.set("e01a1", rowSet.getString("e01a1"));
				        		bean.set("nbase", rowSet.getString("nbase"));
				        		bean.set("a0100", rowSet.getString("a0100"));
				        		bean.set("a0101", rowSet.getString("a0101"));
				        		bean.set("content", Sql_switcher.readMemo(rowSet, "content"));
				        		bean.set("title", rowSet.getString("title"));
				        		bean.set("type", rowSet.getInt("type")+"");
				        		switch(Sql_switcher.searchDbServer())
				    		    {
				    				case Constant.ORACEL:
				    			    {
				    			    	bean.set("start_time", sdf1.parse(rowSet.getString("start_time")));
				    	        		bean.set("end_time", sdf1.parse(rowSet.getString("end_time")));
				    	        		break;
				    			    }
				    				case Constant.MSSQL:
				    			    {
				    			    	bean.set("start_time", rowSet.getDate("start_time"));
				    	        		bean.set("end_time", rowSet.getDate("end_time"));
				    	        		break;
				    			    }
				    			}
				        		list.add(bean);
				        		map.put(key, list);
						    }
					    	 
					   }
		        }
		       
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
	    return  map;	
	}
	public String getYearTableHtml(String currentYear){
		String isOwner = (String)this.userView.getHm().get("isOwner");
		StringBuffer buf = new StringBuffer();
		GregorianCalendar gcal = new GregorianCalendar();
		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(weekutils.strTodate(currentYear+"-"+"01"+"-1"));
		GregorianCalendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(weekutils.strTodate(currentYear+"-"+"12"+"-31"));
		HashMap map = getDataMap(this.a0100,startCalendar,endCalendar);
		buf.append("<table width=\"100%\" margin-top=\"-10px\" height=\"\" border=\"0\" cellspacing=\"10\"  cellpadding=\"0\" >");
		buf.append("<tr>");
		for (int i = 0; i < 12; i++) {
			if(i%4==0&&i!=0){
				buf.append("</tr>");
				buf.append("<tr>");
			}
			buf.append("<td style=\"border:1px solid #8a9aa9\" align=\"left\" width=\"25%\"  margin-right=\"10px\">");
			 buf.append("<table width=\"100%\" height=\"186\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"b5\">");
			  buf.append("<tr>");
			      if(gcal.get(GregorianCalendar.YEAR)==Integer.parseInt(currentYear) && gcal.get(GregorianCalendar.MONTH)==i){
			    	  buf.append("<td height=\"20\" colspan=\"7\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" class=\"z0 myfontstyle\"  bgcolor=\"#66A7E9\">");
			      }else{
			    	  buf.append("<td height=\"20\" colspan=\"7\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" class=\"z0\" bgcolor=\"#DFDFDF\">");
			      }
			          buf.append((i+1)+"月");
			      buf.append("</td>");
	          buf.append("</tr>");      
			buf.append("<tr>");
			if(gcal.get(GregorianCalendar.YEAR)==Integer.parseInt(currentYear) && gcal.get(GregorianCalendar.MONTH)==i){
				buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#66A7E9\" class=\"z5\">周日</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#66A7E9\" class=\"z5\">周一</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#66A7E9\" class=\"z5\">周二</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#66A7E9\" class=\"z5\">周三</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#66A7E9\" class=\"z5\">周四</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#66A7E9\" class=\"z5\">周五</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#66A7E9\" class=\"z5\">周六</td>");
			}else{
				buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#DFDFDF\" class=\"z23\">周日</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#DFDFDF\" class=\"z23\">周一</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#DFDFDF\" class=\"z23\">周二</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#DFDFDF\" class=\"z23\">周三</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#DFDFDF\" class=\"z23\">周四</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#DFDFDF\" class=\"z23\">周五</td>");
			    buf.append("<td align=\"center\" nowrap=\"nowrap\" style=\"cursor:pointer;\" onclick=\"gotoMonthWork('"+currentYear+"','"+String.valueOf(i+1)+"','"+gcal.get(GregorianCalendar.DAY_OF_MONTH)+"')\" valign=\"middle\" bgcolor=\"#DFDFDF\" class=\"z23\">周六</td>");
			}
				  
			buf.append("</tr>");
			
			String currentMonth = String.valueOf(i+1);
			if(Integer.parseInt(currentMonth)<10)
				currentMonth = "0"+currentMonth;
			String startime=currentYear+"-"+currentMonth+"-1";
			GregorianCalendar cal = new GregorianCalendar(); 
			cal.setTime(weekutils.strTodate(startime));
			//与上个月差几天
			int startWeekDay = cal.get(Calendar.DAY_OF_WEEK)-1;
			String endtime=weekutils.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));	
			cal.setTime(weekutils.strTodate(endtime));
			//与下个月差几天
			int endWeekDay = 7-cal.get(Calendar.DAY_OF_WEEK);
			cal.setTime(weekutils.strTodate(endtime));
			int totalDay = cal.get(Calendar.DAY_OF_MONTH);
			ArrayList list = new ArrayList();
			if(startWeekDay>0){
				for (int k1 = startWeekDay; k1 >=1 ; k1--) {
					cal = new GregorianCalendar();
					cal.setTime(weekutils.strTodate(startime));
					cal.add(GregorianCalendar.DAY_OF_MONTH, -k1);
					list.add(cal);
					
				}
			}
			GregorianCalendar lastcal = new GregorianCalendar();
			for (int k2 = 1; k2 <= totalDay; k2++) {
				cal = new GregorianCalendar();
				cal.setTime(weekutils.strTodate(startime));
				cal.add(GregorianCalendar.DAY_OF_MONTH, k2-1);
				list.add(cal);
				if(k2==totalDay)
					lastcal.setTime(cal.getTime());
			}
			if(endWeekDay>0){
				for (int k3 = 1; k3 <= endWeekDay; k3++) {
					cal = new GregorianCalendar();
					cal.setTime(weekutils.strTodate(endtime));
					cal.add(GregorianCalendar.DAY_OF_MONTH, k3);
					list.add(cal);
					if(k3==endWeekDay)
						lastcal.setTime(cal.getTime());
				}
			}
			if(list.size()==28){
				for (int k4 = 1; k4 <= 7; k4++) {
					cal = new GregorianCalendar();
					cal.setTime(lastcal.getTime());
					cal.add(GregorianCalendar.DAY_OF_MONTH, k4);
					list.add(cal);
					if(k4==7)
						lastcal.setTime(cal.getTime());
				}
			}
			if(list.size()==35){
				for (int k4 = 1; k4 <= 7; k4++) {
					cal = new GregorianCalendar();
					cal.setTime(lastcal.getTime());
					cal.add(GregorianCalendar.DAY_OF_MONTH, k4);
					list.add(cal);
				}
			}
			buf.append("<tr>");
			for (int k5 = 0; k5 < list.size(); k5++) {
				GregorianCalendar gcal1 = (GregorianCalendar)list.get(k5);
				//ArrayList dataList = (ArrayList)dataMap.get(sdf.format(gcal1.getTime()));
				boolean flag=false;
				if(Integer.parseInt(currentYear)!=gcal1.get(GregorianCalendar.YEAR)||Integer.parseInt(currentMonth)!=(gcal1.get(GregorianCalendar.MONTH)+1)){
					flag=true;
				}
				if(k5%7==0&&k5!=0){
					buf.append("</tr>");
					buf.append("<tr>");
				}
				buf.append("<td align=\"center\" valign=\"middle\" ");
				boolean istoday = false;
				if(gcal.get(GregorianCalendar.YEAR)==Integer.parseInt(currentYear) && gcal.get(GregorianCalendar.MONTH)==i && gcal.get(GregorianCalendar.DAY_OF_MONTH)==gcal1.get(GregorianCalendar.DAY_OF_MONTH)){
					istoday=true;
				}
				if(!flag && !istoday && map.get(sdf.format(gcal1.getTime()))!=null){
					if(((ArrayList)map.get(sdf.format(gcal1.getTime()))).size()==1){
					   buf.append(" bgcolor=\"#FFF06D\" ");
				    }else if(((ArrayList)map.get(sdf.format(gcal1.getTime()))).size()<=4 && ((ArrayList)map.get(sdf.format(gcal1.getTime()))).size()>=2){
				    	buf.append(" bgcolor=\"#FFCC00\" ");
				    }else if(((ArrayList)map.get(sdf.format(gcal1.getTime()))).size()<=7 && ((ArrayList)map.get(sdf.format(gcal1.getTime()))).size()>=5){
				    	buf.append(" bgcolor=\"#FEB198\" ");
				    }else if(((ArrayList)map.get(sdf.format(gcal1.getTime()))).size()>=8 ){
				    	buf.append(" bgcolor=\"#FE865F\" ");
				    }
				}
				
				if((k5+1)%7==0&&(k5+1)!=0){
					buf.append(" class=\"myz22\" ");
				}else{
					buf.append(" class=\"z22\" ");
				}
				if(flag){
					buf.append(" bgcolor=\"#EEEEEE\" ");
				}
				if(istoday){
					buf.append(" bgcolor=\"#66A7E9\" ");
				}
				buf.append(" style=\"cursor:pointer;\" onclick=\"window.location.href='/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&isOwner="+isOwner+"&fromyear="+currentYear+"&frommonth=&fromday=&year="+currentYear+"&month="+String.valueOf(i+1)+"&day="+gcal1.get(GregorianCalendar.DAY_OF_MONTH)+"&frompage=3'\"");
				buf.append(">");
				buf.append(gcal1.get(GregorianCalendar.DAY_OF_MONTH));
				buf.append("</td>");
		   }
			buf.append("</tr>");
			
			
			
			
			buf.append("</table>");
			buf.append("</td>");
		}
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}
	public ResultSet myQuery(String sql) throws SQLException{
		DbSecurityImpl dbS = new DbSecurityImpl();
		//结果集的游标可上下移动，但当数据库变化时，结果集不变，不能用结果集更新数据库中的表
		stat=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		//返回可滚动的结果集
		// 打开Wallet
		dbS.open(this.con, sql);
		resultSet=stat.executeQuery(sql);
		// 关闭Wallet
		dbS.close(con);
		return resultSet;
	}
	
	/**
	 * Description: 获取查询结果
	 * @Version1.0 
	 * Jan 28, 2013 9:34:54 AM Jianghe created
	 * @param queryTitle
	 * @param queryStart_time
	 * @param queryEnd_time
	 * @param queryStartHour
	 * @param queryEndHour
	 * @param queryContent
	 * @return
	 */
	public String getQueryResults(PageNumber handlePage,String fromyear,String frommonth,String fromday,String frompage,String queryTitle,String queryStart_time,String queryEnd_time,String queryStartHour,String queryEndHour,String queryContent,String queryStartMinute,String queryEndMinute){
		StringBuffer buf = new StringBuffer();
		ArrayList list = new ArrayList();
		if(handlePage==null)handlePage=new PageNumber();
		ResultSet rs = null;
		int rowCount=0;	//总的记录数
		if( !((queryTitle==null|| "".equals(queryTitle)) && (queryStart_time==null|| "".equals(queryStart_time)) && (queryEnd_time==null|| "".equals(queryEnd_time)) && (queryContent==null|| "".equals(queryContent)))){
			try{
			GregorianCalendar cal_start = new GregorianCalendar();
			GregorianCalendar cal_end = new GregorianCalendar();
			if(queryStart_time!=null && !"".equals(queryStart_time)){
				cal_start.setTime(weekutils.strTodate(queryStart_time));
				if(queryStartHour!=null && !"".equals(queryStartHour)){
					cal_start.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(queryStartHour));
					cal_start.set(GregorianCalendar.MINUTE, Integer.parseInt(queryStartMinute));
				}else{
					cal_start.set(GregorianCalendar.HOUR_OF_DAY, 0);
					cal_start.set(GregorianCalendar.MINUTE, 0);
				}
			}
			if(queryEnd_time!=null && !"".equals(queryEnd_time)){
				cal_end.setTime(weekutils.strTodate(queryEnd_time));
                if(queryEndHour!=null && !"".equals(queryEndHour)){
                	cal_end.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(queryEndHour));
                	cal_end.set(GregorianCalendar.MINUTE, Integer.parseInt(queryEndMinute));
				}else{
					cal_end.set(GregorianCalendar.HOUR_OF_DAY, 23);
					cal_end.set(GregorianCalendar.MINUTE, 59);
				}
			}
			
			String startDate = sdf.format(cal_start.getTime());
			String endDate = sdf.format(cal_end.getTime());
			String sTime1 = "";
			String eTime1 = "";
			switch(Sql_switcher.searchDbServer())
		    {
				case Constant.ORACEL:
			    {
			    	sTime1="to_char(pdc.start_time,'yyyy-MM-dd hh24:mi:ss')";
			    	eTime1="to_char(pdc.end_time,'yyyy-MM-dd hh24:mi:ss')";
			    	break;
			    }
				case Constant.MSSQL:
			    {
			    	sTime1="pdc.start_time";
			    	eTime1="pdc.end_time";
			    	break;
			    }
			}	
			String sql = "select pdc.p0100,pdc.record_num,pdc.b0110,pdc.e0122,pdc.e01a1,pdc.nbase,pdc.a0100,pdc.a0101,pdc.content,pdc.title,pdc.type,"+sTime1+" as start_time,"+eTime1+" as end_time from per_diary_content pdc where pdc.a0100='"+a0100+"'"+" and exists(select null from p01 where p0100=pdc.p0100 and state='0')";
			if(queryStart_time!=null && !"".equals(queryStart_time) && queryEnd_time!=null && !"".equals(queryEnd_time)){
				sql+=" and ( ("+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+">="+startDate;
				sql+=" and "+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+"<="+endDate+")";
				sql+=" or ("+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+"<="+endDate;
				sql+=" and "+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+">="+startDate+") )";
				
			}else if(queryStart_time!=null && !"".equals(queryStart_time) && ( queryEnd_time==null || "".equals(queryEnd_time) ) ){
				sql+=" and "+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+">="+startDate;
			}else if(queryEnd_time!=null && !"".equals(queryEnd_time) && ( queryStart_time==null || "".equals(queryStart_time) ) ){
				sql+=" and "+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+"<="+endDate;
			}
			if(queryTitle!=null && !"".equals(queryTitle))
				sql+=" and pdc.title like '%"+queryTitle+"%'";
			if(queryContent!=null && !"".equals(queryContent))
				sql+=" and pdc.content like '%"+queryContent+"%'";
			sql+=" order by pdc.start_time desc";
			//System.out.println(sql);
			try{
				 //返回可滚动的结果集
				 rs=myQuery(sql);
				//将游标移动到最后一行
				rs.last();
				//获取记录总数
				rowCount=rs.getRow();
				//设置每页显示的记录数
				handlePage.setPageSize(20);
				//计算总页数
				handlePage.setPageCount(rowCount,handlePage.getPageSize());
				
			}catch(SQLException e){
				e.printStackTrace();
			}
			if(a==null|| "".equals(a.trim())){
				 a="1";
			 }
			if("first".equals(a)){
				  handlePage.setShowPage(1);		//设置预显示页
				  rs.absolute(1);
		 	}else if("next".equals(a)){
				  int n=handlePage.getShowPage();		//获取目前的页数
				  n=(n+1);		//将页数增1
				  if(n>handlePage.getPageCount()) n=1;
				  handlePage.setShowPage(n);		//设置预显示页
				
				  //将游标移到指定的位置
				  rs.absolute((n-1)*handlePage.getPageSize()+1);
				  //显示该页的内容
				
			 }else if("previous".equals(a)){
				 int n=handlePage.getShowPage();		//获取目前的页数
				 n=n-1;		//将页数减1
				 if(n<=0) n=handlePage.getPageCount();
				 handlePage.setShowPage(n);		//设置预显示页
				
				 //将游标移到指定的位置
				 rs.absolute((n-1)*handlePage.getPageSize()+1);
				 //显示该页的内容
				 
			 }else if("last".equals(a)){
				 int n=handlePage.getShowPage();		//获取目前的页数
				 n=handlePage.getPageCount();
				 handlePage.setShowPage(n);		//设置预显示页
				
				 //将游标移到指定的位置
				 rs.absolute((n-1)*handlePage.getPageSize()+1);
				 //显示该页的内容
				 
			 }else{
				 int m=Integer.parseInt(a);		//把输入的字符转化为整型数
				 handlePage.setShowPage(m);	//设置预显示页
				
				 int n=handlePage.getShowPage();
				 //将游标移到指定的位置
				 rs.absolute((n-1)*handlePage.getPageSize()+1);
				 //显示该页的内容
				
			 }
			for (int i = 1; i <= handlePage.getPageSize(); i++) {
			    LazyDynaBean bean = new LazyDynaBean();
			    bean.set("p0100", rs.getInt("p0100")+"");
	       		bean.set("record_num", rs.getInt("record_num")+"");
	       		bean.set("b0110", rs.getString("b0110"));
	       		bean.set("e0122", rs.getString("e0122"));
	       		bean.set("e01a1", rs.getString("e01a1"));
	       		bean.set("nbase", rs.getString("nbase"));
	       		bean.set("a0100", rs.getString("a0100"));
	       		bean.set("a0101", rs.getString("a0101"));
	       		bean.set("content", Sql_switcher.readMemo(rs, "content"));
	       		bean.set("title", rs.getString("title"));
	       		bean.set("type", rs.getInt("type")+"");
	       		switch(Sql_switcher.searchDbServer())
    		    {
    				case Constant.ORACEL:
    			    {
    			    	bean.set("start_time", sdf1.parse(rs.getString("start_time")));
    	        		bean.set("end_time", sdf1.parse(rs.getString("end_time")));
    	        		break;
    			    }
    				case Constant.MSSQL:
    			    {
    			    	bean.set("start_time", rs.getDate("start_time"));
    	        		bean.set("end_time", rs.getDate("end_time"));
    	        		break;
    			    }
    			}
	       		list.add(bean);
				if (!rs.next())
					break;

			}
//		   while(rs.next())
//	        {
//			    LazyDynaBean bean = new LazyDynaBean();
//			    bean.set("p0100", rs.getInt("p0100")+"");
//	       		bean.set("record_num", rs.getInt("record_num")+"");
//	       		bean.set("b0110", rs.getString("b0110"));
//	       		bean.set("e0122", rs.getString("e0122"));
//	       		bean.set("e01a1", rs.getString("e01a1"));
//	       		bean.set("nbase", rs.getString("nbase"));
//	       		bean.set("a0100", rs.getString("a0100"));
//	       		bean.set("a0101", rs.getString("a0101"));
//	       		bean.set("content", Sql_switcher.readMemo(rs, "content"));
//	       		bean.set("title", rs.getString("title"));
//	       		bean.set("type", rs.getInt("type")+"");
//	       		switch(Sql_switcher.searchDbServer())
//    		    {
//    				case Constant.ORACEL:
//    			    {
//    			    	bean.set("start_time", sdf1.parse(rs.getString("start_time")));
//    	        		bean.set("end_time", sdf1.parse(rs.getString("end_time")));
//    	        		break;
//    			    }
//    				case Constant.MSSQL:
//    			    {
//    			    	bean.set("start_time", rs.getDate("start_time"));
//    	        		bean.set("end_time", rs.getDate("end_time"));
//    	        		break;
//    			    }
//    			}
//	       		list.add(bean);
//	        }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		this.userView.getHm().put("handlePage", handlePage);
		if(list.size()>0){
			buf.append("<table id=\"resultTable\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" width=\"100%\" class=\"ListTable\">");
			buf.append("<tr>");
			 buf.append("<td width=\"30%\" align=\"center\" class=\"epm-j-table-row\" nowrap>");
			   buf.append("标题");
			 buf.append("</td>");
			 buf.append("<td width=\"70%\" align=\"left\" class=\"epm-j-table-row\" nowrap>");
			   buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;内容");
			 buf.append("</td>");
		    buf.append("</tr>");
			for (int i = 0; i < list.size(); i++) {
				buf.append("<tr>");
				 buf.append("<td class=\"epm-j-table-row\" nowrap>");
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				String fromp0100 = (String)bean.get("p0100");
				String fromrecord_num = (String)bean.get("record_num");
				String title = (String)bean.get("title");
				String content = (String)bean.get("content");
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime((Date)bean.get("start_time"));
				String year=calendar.get(GregorianCalendar.YEAR)+"";
				String month=(calendar.get(GregorianCalendar.MONTH)+1)+"";
				String day=calendar.get(GregorianCalendar.DAY_OF_MONTH)+"";
				if(title.length()>6)
	            	  title=title.substring(0,6)+"...";
				    buf.append("<a href=\"###\" onclick=\"window.location.href='/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&combineid="+fromp0100+""+fromrecord_num+"&p01_key="+fromp0100+"&recordNum="+fromrecord_num+"&fromyear="+fromyear+"&frommonth="+frommonth+"&fromday="+fromday+"&year="+year+"&month="+month+"&day="+day+"&frompage="+frompage+"'\">"+title+"</a>");
				 buf.append("</td>");
				 buf.append("<td class=\"epm-j-table-row\" nowrap title=\""+content+"\">");
				 content = content.replaceAll("\r\n", "<br/>");
				 if(content.length()>12)
					 content=content.substring(0,12)+"...";
				    buf.append(content); 
				 buf.append("</td>");
				buf.append("</tr>");
			}
			buf.append("<tr>");
			buf.append("<td class=\"epm-j-table-row\" colspan=\"2\">");
			buf.append("当前页数:"+handlePage.getShowPage()+"/"+handlePage.getPageCount());
			buf.append("&nbsp;&nbsp;&nbsp;<a onclick=\"queryData('first');\">首页</a>");
			buf.append("&nbsp;&nbsp;<a onclick=\"queryData('previous');\">上一页</a>");
			buf.append("&nbsp;&nbsp;<a onclick=\"queryData('next');\">下一页</a>");
			buf.append("&nbsp;&nbsp;<a onclick=\"queryData('last');\">末页</a>");
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("</table>");
			}else{
				buf = new StringBuffer("没有符合查询条件的结果!");
			}
		}else{
			buf = new StringBuffer("请输入查询条件!");
		}
		return buf.toString();
	}
}
