package com.hjsj.hrms.actionform.hire.zp_option.stat.weekly;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

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
public class WeeklyStmtForm extends FrameForm {
	
	  private PaginationForm pageListForm = new PaginationForm();
	  private String flag;
	
	  
	  private String id;
	  
	  private String depid;
	  private String dep;
	  private String jobid;
	  private String job;
	  
	  private String start_date;
	  private String end_date;
	  
	  private String view_sum;
	  private String app_sum;
	  private String apps_sum;
	  
	  private ArrayList listView = new ArrayList();
	  private ArrayList listApp = new ArrayList();
	  private ArrayList weeklystmt = new ArrayList();
	  
	  private String view_title = ResourceFactory.getProperty("hire.zp_option.weekly.view");
	  private String app_title = ResourceFactory.getProperty("hire.zp_option.weekly.app");
	  
	  private String theweekday ;
	  private String[] b_count = new String[7];
	  private String[] a_count = new String[7];
		
		private String view_monday ;
		private String view_tuesday ;
		private String view_wednesday;
		private String view_thursday ;
		private String view_friday;
		private String view_saturday ;
		private String view_sunday ;
		private String sum_view ;
		

		private String app_monday ;
		private String app_tuesday ;
		private String app_wednesday;
		private String app_thursday ;
		private String app_friday ;
		private String app_saturday ;
		private String app_sunday;
		private String sum_app ;
		
		private String title_view = ResourceFactory.getProperty("hire.zp_option.weekly.viewday");
		private String title_app = ResourceFactory.getProperty("hire.zp_option.weekly.appday");
		  
		private HashMap dayView = new HashMap();
		private HashMap dayApp = new HashMap();	  
		private ArrayList allList = new ArrayList();
		private ArrayList jobidlist = new ArrayList();
		private ArrayList hirePathList=new ArrayList();
		private String hirePath="";
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setFlag((String)hm.get("flag"));
		this.setId((String)hm.get("id"));
		this.setDep((String)hm.get("dep"));
		this.setDepid((String)hm.get("depid"));
		this.setJob((String)hm.get("job"));
		this.setJobid((String)hm.get("jobid"));
		this.setStart_date((String)hm.get("start_date"));
		this.setEnd_date((String)hm.get("end_date"));
		this.setListView((ArrayList)hm.get("listView"));
		this.setListApp((ArrayList)hm.get("listApp"));
		this.setWeeklystmt((ArrayList)hm.get("weeklystmt"));
		this.setView_sum((String)hm.get("view_sum"));
		this.setApp_sum((String)hm.get("app_sum"));
		this.setApps_sum((String)hm.get("apps_sum"));
		
		this.setTheweekday((String)hm.get("theweekday"));
		this.setB_count((String[])hm.get("b_count"));
		this.setA_count((String[])hm.get("a_count"));
		this.setDayView((HashMap)hm.get("dayView"));
		this.setDayApp((HashMap)hm.get("dayApp"));
		this.setView_monday((String)hm.get("view_monday"));
		this.setView_tuesday((String)hm.get("view_tuesday"));
		this.setView_wednesday((String)hm.get("view_wednesday"));
		this.setView_thursday((String)hm.get("view_thursday"));
		this.setView_friday((String)hm.get("view_friday"));
		this.setView_saturday((String)hm.get("view_saturday"));
		this.setView_sunday((String)hm.get("view_sunday"));
		this.setSum_view((String)hm.get("sum_view"));
		this.setApp_monday((String)hm.get("app_monday"));
		this.setApp_tuesday((String)hm.get("app_tuesday"));
		this.setApp_wednesday((String)hm.get("app_wednesday"));
		this.setApp_thursday((String)hm.get("app_thursday"));
		this.setApp_friday((String)hm.get("app_friday"));
		this.setApp_saturday((String)hm.get("app_saturday"));
		this.setApp_sunday((String)hm.get("app_sunday"));
		this.setSum_app((String)hm.get("sum_app"));
		this.setAllList((ArrayList)hm.get("allList"));
		this.setJobidlist((ArrayList)hm.get("jobidlist"));
		this.setHirePath((String)this.getFormHM().get("hirePath"));
		this.setHirePathList((ArrayList)this.getFormHM().get("hirePathList"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		if(this.getPagination()!=null)
			this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
		hm.put("view_title",view_title);
		hm.put("app_title",app_title);
		hm.put("start_date",this.getStart_date());
		hm.put("end_date",this.getEnd_date());
		hm.put("jobid",this.getJobid());
		hm.put("title_view",title_view);
		hm.put("title_app",title_app);
		hm.put("listApp", this.getListApp());
		hm.put("dayApp", this.getDayApp());
		hm.put("hirePath", this.getHirePath());
		hm.put("depid", this.getDepid());
		hm.put("dep", this.getDep());
	}
	
	/**
	 * @return Returns the .
	 */

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}
	public String getDepid() {
		return depid;
	}

	public void setDepid(String depid) {
		this.depid = depid;
	}
	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}
	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	public ArrayList getListView() {
		return listView;
	}

	public void setListView(ArrayList listView) {
		this.listView = listView;
	}
	public ArrayList getListApp() {
		return listApp;
	}

	public void setListApp(ArrayList listApp) {
		this.listApp = listApp;
	}
	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	
	public String getView_title() {
		return view_title;
	}

	public void setView_title(String view_title) {
		this.view_title = view_title;
	}
	public String getApp_title() {
		return app_title;
	}

	public void setApp_title(String app_title) {
		this.app_title = app_title;
	}
	public ArrayList getWeeklystmt() {
		return weeklystmt;
	}

	public void setWeeklystmt(ArrayList weeklystmt) {
		this.weeklystmt = weeklystmt;
	}
	public String getView_sum() {
		return view_sum;
	}

	public void setView_sum(String view_sum) {
		this.view_sum = view_sum;
	}
	public String getApp_sum() {
		return app_sum;
	}

	public void setApp_sum(String app_sum) {
		this.app_sum = app_sum;
	}
	public String getApps_sum() {
		return apps_sum;
	}
	public void setApps_sum(String apps_sum) {
		this.apps_sum = apps_sum;
	}
	
	public void setTheweekday(String theweekday) {
		this.theweekday = theweekday;
	}
	public String getTheweekday() {
		return theweekday;
	}
	
	public void setB_count(String[] b_count) {
		this.b_count = b_count;
	}
	public String[] getB_count() {
		return b_count;
	}

	public void setA_count(String[] a_count) {
		this.a_count = a_count;
	}
	public String[] getA_count() {
		return a_count;
	}
	public void setDayView(HashMap dayView) {
		this.dayView = dayView;
	}
	public HashMap getDayView() {
		return dayView;
	}

	public void setDayApp(HashMap dayApp) {
		this.dayApp = dayApp;
	}
	public HashMap getDayApp() {
		return dayApp;
	}

	public String getTitle_view() {
		return title_view;
	}

	public void setTitle_view(String title_view) {
		this.title_view = title_view;
	}
	public String getTitle_app() {
		return title_app;
	}

	public void setTitle_app(String title_app) {
		this.title_app = title_app;
	}
	
	public String getView_monday() {
		return view_monday;
	}

	public void setView_monday(String view_monday) {
		this.view_monday = view_monday;
	}
	public String getView_tuesday() {
		return view_tuesday;
	}

	public void setView_tuesday(String view_tuesday) {
		this.view_tuesday = view_tuesday;
	}
	public String getView_wednesday() {
		return view_wednesday;
	}

	public void setView_wednesday(String view_wednesday) {
		this.view_wednesday = view_wednesday;
	}
	public String getView_thursday() {
		return view_thursday;
	}

	public void setView_thursday(String view_thursday) {
		this.view_thursday = view_thursday;
	}
	public String getView_friday() {
		return view_friday;
	}

	public void setView_friday(String view_friday) {
		this.view_friday = view_friday;
	}
	public String getView_saturday() {
		return view_saturday;
	}

	public void setView_saturday(String view_saturday) {
		this.view_saturday = view_saturday;
	}
	public String getView_sunday() {
		return view_sunday;
	}

	public void setView_sunday(String view_sunday) {
		this.view_sunday = view_sunday;
	}
	public String getSum_view() {
		return sum_view;
	}

	public void setSum_view(String sum_view) {
		this.sum_view = sum_view;
	}
	
	public String getApp_monday() {
		return app_monday;
	}

	public void setApp_monday(String app_monday) {
		this.app_monday = app_monday;
	}
	public String getApp_tuesday() {
		return app_tuesday;
	}

	public void setApp_tuesday(String app_tuesday) {
		this.app_tuesday = app_tuesday;
	}
	public String getApp_wednesday() {
		return app_wednesday;
	}

	public void setApp_wednesday(String app_wednesday) {
		this.app_wednesday = app_wednesday;
	}
	public String getApp_thursday() {
		return app_thursday;
	}

	public void setApp_thursday(String app_thursday) {
		this.app_thursday = app_thursday;
	}
	public String getApp_friday() {
		return app_friday;
	}

	public void setApp_friday(String app_friday) {
		this.app_friday = app_friday;
	}
	public String getApp_saturday() {
		return app_saturday;
	}

	public void setApp_saturday(String app_saturday) {
		this.app_saturday = app_saturday;
	}
	public String getApp_sunday() {
		return app_sunday;
	}

	public void setApp_sunday(String app_sunday) {
		this.app_sunday = app_sunday;
	}
	public String getSum_app() {
		return sum_app;
	}

	public void setSum_app(String sum_app) {
		this.sum_app = sum_app;
	}

	public ArrayList getAllList() {
		return allList;
	}

	public void setAllList(ArrayList allList) {
		this.allList = allList;
	}

	public ArrayList getJobidlist() {
		return jobidlist;
	}

	public void setJobidlist(ArrayList jobidlist) {
		this.jobidlist = jobidlist;
	}
	public ArrayList getHirePathList() {
		return hirePathList;
	}

	public void setHirePathList(ArrayList hirePathList) {
		this.hirePathList = hirePathList;
	}
	public String getHirePath() {
		return hirePath;
	}

	public void setHirePath(String hirePath) {
		this.hirePath = hirePath;
	}
}
