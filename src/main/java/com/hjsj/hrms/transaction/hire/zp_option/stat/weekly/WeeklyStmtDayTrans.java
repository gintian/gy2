package com.hjsj.hrms.transaction.hire.zp_option.stat.weekly;

import com.hjsj.hrms.businessobject.hire.zp_options.stat.weekly.WeeklyStmtSQLStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${2007.04.26}:${time}</p> 
 *@author ${lilinbing}
 *@version 4.0
  */
public class WeeklyStmtDayTrans extends IBusiness {

	public void execute() throws GeneralException {
	    
	    try {
    		HashMap hm=this.getFormHM();
    		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
    		
    		String id= PubFunc.decrypt((String) reqhm.get("id"));
    		id = id.substring(id.indexOf("-")+1,id.length());
    		
    		String theweekday = (String) reqhm.get("theweekday");
    		theweekday = PubFunc.hireKeyWord_filter_reback(theweekday);
    		
    		reqhm.remove("id");
    		reqhm.remove("theweekday");
    		
    		String date_start = theweekday.substring(theweekday.indexOf("/")-10,theweekday.indexOf("/"));
    		
    		String date_end = theweekday.substring(theweekday.indexOf("/")+1,theweekday.indexOf("/")+11);
    		
    		Connection conn = null;
    		conn=this.getFrameconn();
    		ContentDAO dao=new ContentDAO(conn);
    		WeeklyStmtSQLStr weeklystmt = new WeeklyStmtSQLStr();
    		ArrayList theweekcount = (ArrayList)weeklystmt.getDateSQL(dao,id,date_start,date_end);
    		String[] b_count = (String[])theweekcount.get(0);
    		String[] a_count = (String[])theweekcount.get(1);
    
    		theweekday ="第" + theweekday.replaceAll("%20","周");
    		//hm.put("dayView",jobDayView(b_count));
    		//hm.put("dayApp",jobDayApp(a_count));
    		ArrayList allList = new ArrayList();
    		allList.add(jobDayView(b_count));
    		allList.add(jobDayApp(a_count));
    		HashMap allMap=this.allDayView(b_count, a_count);
    		this.getFormHM().put("dayApp", allMap);
    		this.getFormHM().put("allList", allList);
    		hm.put("theweekday",theweekday);
    		
    		
    		int view_sum = 0;
    		for(int i=0;i<7;i++){
    			view_sum+=Integer.parseInt(b_count[i]);
    		}
    							
    		hm.put("view_monday",b_count[0]);
    		hm.put("view_tuesday",b_count[1]);
    		hm.put("view_wednesday",b_count[2]);
    		hm.put("view_thursday",b_count[3]);
    		hm.put("view_friday",b_count[4]);
    		hm.put("view_saturday",b_count[5]);
    		hm.put("view_sunday",b_count[6]);
    		hm.put("sum_view",view_sum+"");
    		
    		int app_sum = 0;
    		for(int i=0;i<7;i++){
    			app_sum+=Integer.parseInt(a_count[i]);
    		}
    		
    		hm.put("app_monday",a_count[0]);
    		hm.put("app_tuesday",a_count[1]);
    		hm.put("app_wednesday",a_count[2]);
    		hm.put("app_thursday",a_count[3]);
    		hm.put("app_friday",a_count[4]);
    		hm.put("app_saturday",a_count[5]);
    		hm.put("app_sunday",a_count[6]);
    		hm.put("sum_app",app_sum+"");		
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
	}
	private HashMap allDayView(String[] b_count,String[] a_count)
	{
		HashMap dayView = new HashMap();
		try
		{
			ArrayList listApp = new ArrayList();
			String[] week2 = {ResourceFactory.getProperty("label.sys.warn.freq.week.monday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.tuesday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.wednesday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.thursday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.friday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.saturday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.sunday")};
			for(int i=0;i<7;i++){
				CommonData job=new CommonData();
				job.setDataName(week2[i]);
				job.setDataValue(String.valueOf(a_count[i]));
				listApp.add(job);
			}
			dayView.put("申请人数",listApp);
			ArrayList listView = new ArrayList();
			String[] week = {ResourceFactory.getProperty("label.sys.warn.freq.week.monday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.tuesday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.wednesday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.thursday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.friday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.saturday"),
					ResourceFactory.getProperty("label.sys.warn.freq.week.sunday")};
			for(int i=0;i<7;i++){
				CommonData vo =new CommonData();
				vo.setDataName(week[i]);
				vo.setDataValue(String.valueOf(b_count[i]));
				listView.add(vo);
				
			}
			dayView.put("浏览人数",listView);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dayView;
	}
	/**
	 * 获取页面生成图表标签所要的HashMap
	 * @param  String[] b_count 一个星期中每天的浏览数
	 * @return HashMap 生成浏览人数折线图所需的HashMap
	 */
	public HashMap jobDayView(String[] b_count){
		HashMap dayView = new HashMap();
		ArrayList listView = new ArrayList();
		String[] week = {ResourceFactory.getProperty("label.sys.warn.freq.week.monday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.tuesday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.wednesday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.thursday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.friday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.saturday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.sunday")};
		for(int i=0;i<7;i++){
			CommonData vo =new CommonData();
			vo.setDataName(week[i]);
			vo.setDataValue(String.valueOf(b_count[i]));
			listView.add(vo);
			
		}
		dayView.put("浏览人数",listView);
		
		return dayView;
	}
	/**
	 * 获取页面生成图表标签所要的HashMap
	 * @param  String[] a_count  一个星期中每天的申请数
	 * @return HashMap 生成申请人数折线图所需的HashMap
	 */
	public HashMap jobDayApp(String[] a_count){
		HashMap dayApp = new HashMap();
		ArrayList listApp = new ArrayList();
		String[] week = {ResourceFactory.getProperty("label.sys.warn.freq.week.monday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.tuesday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.wednesday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.thursday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.friday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.saturday"),
				ResourceFactory.getProperty("label.sys.warn.freq.week.sunday")};
		for(int i=0;i<7;i++){
			CommonData job=new CommonData();
			job.setDataName(week[i]);
			job.setDataValue(String.valueOf(a_count[i]));
			listApp.add(job);
		}
		dayApp.put("申请人数",listApp);
		
		return dayApp;
	}

}
