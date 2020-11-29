package com.hjsj.hrms.transaction.hire.zp_option.stat.weekly;

import com.hjsj.hrms.businessobject.hire.zp_options.stat.weekly.WeeklyStmtSQLStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.*;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${2007.04.26}:${time}</p> 
 *@author ${lilinbing}
 *@version 4.0
  */
public class WeeklyStmtWeekTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		
		String id = (String) reqhm.get("id");
		id = PubFunc.decrypt(id);
		if ("".equals(id))
		    id = PubFunc.decrypt((String)hm.get("id"));
		reqhm.remove("id");
		String hirePath =(String) hm.get("hirePath");
		String depid =(String) hm.get("depid");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		WeeklyStmtSQLStr weeklystmt = new WeeklyStmtSQLStr();
		weeklystmt.setHirepath(hirePath);
		String jobid=(id==null || "".equals(id)) ? null : (String) hm.get("jobid");
		//id= com.hjsj.hrms.utils.PubFunc.decrypt((String) reqhm.get("id"));
		
		if(id!=null&&!"".equals(id)){
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301", id);
			try {
				vo = dao.findByPrimaryKey(vo);
				hirePath = vo.getString("z0336");
				String[] listdep = weeklystmt.getDepartmentsStr(dao,id);
				depid =  vo.getString("z0325");
				if(hirePath!=null&&hirePath.length()!=0){//dml 在新建招聘需求时，如果所属部门选择的是单位，则在z0325中会是空置或null值修改为 找z0321中的值 2011-03-29
					if("01".equalsIgnoreCase(hirePath)){
						if(depid==null||depid.length()==0){
							depid=vo.getString("z0321");
						}
					}else{
						if(depid==null||depid.length()==0){
							depid=vo.getString("z0321");
						}
					}
				}
				hm.put("depid",listdep[0]);
				hm.put("dep",listdep[1]);
				
				String[] listjob = weeklystmt.getJobsStr(dao,id);
				hm.put("jobid",listjob[0]+"-"+id);
				hm.put("job",listjob[1]);
			} catch (SQLException e) {
			}
		}else{
			if(jobid!=null){
				id = jobid.substring(jobid.indexOf("-")+1,jobid.length());
			}
		}
		
		
		
		hm.put("id",com.hjsj.hrms.utils.PubFunc.encrypt(id));
		
		
		
		String start_date = (String)hm.get("start_date");
		hm.remove("start_date");
		start_date = start_date!=null?start_date:strStartDate();
		hm.put("start_date",start_date);
		
		String end_date = (String)hm.get("end_date");
		hm.remove("end_date");
		end_date = end_date!=null?end_date:strDate();
		hm.put("end_date",end_date);
		
		Date date_start = strTodate(start_date,1);
		Date date_end = strTodate(end_date,2);

		int sum_week = DateUtils.dayDiff(date_start,date_end)/7;

		ArrayList listView = new ArrayList();
		ArrayList listApp = new ArrayList();
		ArrayList weekly = new ArrayList();
		int view_sum = 0;
		int app_sum = 0;
		float apps_sum = 0;
		for(int i=0;i<sum_week;i++){
			String[] dep =  weeklystmt.getWeeklySQL(dao,id,theweek_start(date_start,i),theweek_end(date_start,i+1));
			CommonData vo =new CommonData();
			vo.setDataName(i+1+"");
			vo.setDataValue(String.valueOf(dep[0]));
			listView.add(vo);
			
			CommonData job=new CommonData();
			job.setDataName(i+1+"");
			job.setDataValue(String.valueOf(dep[1]));
			listApp.add(job);
			HashMap hashmap = new HashMap();
			float df = 0;
			if(!"0".equals(dep[0])){
				df = Float.parseFloat(dep[1])/Float.parseFloat(dep[0])*100;
			}
			String apps = df+"00";
			apps = apps.substring(0,5)+"%";
			hashmap.put("weeklyday",i+1+" ("+this.theweek_start(date_start,i)+"/"+this.theweek_end(date_start,i+1)+")");
			hashmap.put("view",dep[0]);
			hashmap.put("app",dep[1]);
			hashmap.put("apps",apps);
			weekly.add(hashmap);
			view_sum += Integer.parseInt(dep[0]);
			app_sum += Integer.parseInt(dep[1]);
		}
		if(sum_week==0)
		{
			String[] dep =  weeklystmt.getWeeklySQL(dao,id,theweek_start(date_start,0),theweek_end(date_start,0+1));
			CommonData vo =new CommonData();
			vo.setDataName(0+1+"");
			vo.setDataValue(String.valueOf(dep[0]));
			listView.add(vo);
			CommonData job=new CommonData();
			job.setDataName(0+1+"");
			job.setDataValue(String.valueOf(dep[1]));
			listApp.add(job);
			
			HashMap hashmap = new HashMap();
			
			float df = 0;
			if(!"0".equals(dep[0])){
				df = Float.parseFloat(dep[1])/Float.parseFloat(dep[0])*100;
			}
			String apps = df+"00";
			apps = apps.substring(0,5)+"%";
			
			hashmap.put("weeklyday",0+1+" ("+this.theweek_start(date_start,0)+"/"+this.theweek_end(date_start,0+1)+")");
			hashmap.put("view",dep[0]);
			hashmap.put("app",dep[1]);
			hashmap.put("apps",apps);
			
			weekly.add(hashmap);
			
			view_sum += Integer.parseInt(dep[0]);
			app_sum += Integer.parseInt(dep[1]);
		}
		String apps_sums = "";
		if(app_sum>0){
			apps_sum = Float.parseFloat(app_sum+"")/Float.parseFloat(view_sum+"")*100;
			apps_sums = apps_sum+"00";
		}else{
			apps_sums = "0.000";
		}
		
		apps_sums = apps_sums.substring(0,5)+"%";
		ArrayList allList = new ArrayList();
		allList.add(listView);
		allList.add(listApp);
		
		hm.put("listView",allList);
		hm.put("weeklystmt",weekly);
		hm.put("view_sum",view_sum+"");
		hm.put("app_sum",app_sum+"");
		hm.put("apps_sum",apps_sums);
	//	hm.put("jobidlist",new ArrayList());
		hm.put("jobidlist",weeklystmt.getJobidListModify( this.getFrameconn(), hirePath,depid,id));
		hm.put("hirePath",hirePath);
		hm.put("hirePathList",getHirePathList());
		
	}
	/**
	 * 将在原日期上往前推到下一个星期一的日期
	 * @param  String dates 当前日期
	 * @return Date 返回原日期上往前推到下一个星期一的日期
	 */
	public Date strTodate(String dates,int type) throws GeneralException{
		String[] tempvalue=dates.split("-");
		if(tempvalue.length==1){
			dates=dates+"-01-01";
		}
		if(tempvalue.length==2){
			if(tempvalue[1].length()==1){
				dates=tempvalue[0]+"-0"+tempvalue[1]+"-01";
			}else{
				dates=dates+"-01";
			}
		}
		if(tempvalue.length==3){
			if(tempvalue[1].length()==1){
				tempvalue[1]="0"+tempvalue[1];
			}
			if(tempvalue[2].length()==1){
				tempvalue[2]="0"+tempvalue[2];
			}
			dates=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
		}
		
		if(!chkDateFormat(dates)){
			dates=strDate();
		}
		
		Date start_date = new Date();
		int week_start = 0;
		int day = Integer.parseInt(dates.substring(8,10));
		int month = Integer.parseInt(dates.substring(5,7));
		int year = Integer.parseInt(dates.substring(0,4));
		
		String[] week = {ResourceFactory.getProperty("label.sys.warn.freq.week.monday"),
						ResourceFactory.getProperty("label.sys.warn.freq.week.tuesday"),
						ResourceFactory.getProperty("label.sys.warn.freq.week.wednesday"),
						ResourceFactory.getProperty("label.sys.warn.freq.week.thursday"),
						ResourceFactory.getProperty("label.sys.warn.freq.week.friday"),
						ResourceFactory.getProperty("label.sys.warn.freq.week.saturday"),
						ResourceFactory.getProperty("label.sys.warn.freq.week.sunday")};
		
		Date date = (Date)DateUtils.getDate(year,month,day);
		for(int i=1;i<8;i++){
			if(DateUtils.format(date,"E").equalsIgnoreCase(week[i-1])){
				week_start = i;
				break;
			}
		}
		/**开始时间往前推一个星期，*/
		if(type==1)
		    start_date = DateUtils.addDays(date,-(week_start-1));
		/**结束时间将往后推一个星期*/
		else
			start_date = DateUtils.addDays(date,8-week_start);
		String aa = DateUtils.format(start_date,"D");
		return start_date;
	}
	/**
	 * 当前星期天的日期
	 * @param  String date_end 当前日期
	 * @param  int week 当前日期
	 * @return String 返回当前星期天的日期
	 */
	public String theweek_end(Date date_end,int week) throws GeneralException{
		Date dateEnd = DateUtils.addDays(date_end,7*week-1);
	
		int day = Integer.parseInt(DateUtils.getDay(dateEnd)+"");
		int month = Integer.parseInt(DateUtils.getMonth(dateEnd)+"");
		int year = Integer.parseInt(DateUtils.getYear(dateEnd)+"");
		
		String day_end = "";
		String month_end = "";
		
		if(day<10){
			day_end = "0"+day;
		}else{
			day_end = day+"";
		}
		if(month<10){
			month_end = "0" + month;
		}else{
			month_end = month+"";
		}

		return year + "-" + month_end + "-" + day_end;
	}
	/**
	 * 当前星期一的日期
	 * @param  String date_end 当前日期
	 * @param  int week 当前日期
	 * @return String 返回当前星期一的日期
	 */
	public String theweek_start(Date date_start,int week) throws GeneralException{
		Date dateStart = DateUtils.addDays(date_start,7*week);
	
		int day = Integer.parseInt(DateUtils.getDay(dateStart)+"");
		int month = Integer.parseInt(DateUtils.getMonth(dateStart)+"");
		int year = Integer.parseInt(DateUtils.getYear(dateStart)+"");
		
		String day_start = "";
		String month_start = "";
		
		if(day<10){
			day_start = "0"+day;
		}else{
			day_start = day+"";
		}
		if(month<10){
			month_start = "0" + month;
		}else{
			month_start = month+"";
		}
		return year + "-" + month_start + "-" + day_start;
		
	}
	/**
	 * 获取一个月前日期字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	public String strStartDate(){
		int day = Calendar.getInstance().get(Calendar.DATE);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		String strday = day+"";
		String strmonth = month+"";
		
		if(day<10){
			strday = "0"+strday;
		}
		if(month<10){
			strmonth = "0"+strmonth;
		}	
		return year+"-"+strmonth+"-"+strday;
	}

	/**
	 * 获取当前日期字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	public String strDate(){
		int day = Calendar.getInstance().get(Calendar.DATE);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		String strday = day+"";
		String strmonth = month+"";
		
		if(day<10){
			strday = "0"+strday;
		}
		if(month<10){
			strmonth = "0"+strmonth;
		}	
		return year+"-"+strmonth+"-"+strday;
	}
	
	/**
     * 日期合法check
     * 
     * @param date 需要check的日期
     * @return 日期是否合法
     */
    public static boolean chkDateFormat(String date) {
        try {
           // 如果输入日期不是8位的,判定为false. 
            if (null == date || "".equals(date)) {
                return false;
            }
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7))-1;
            int day = Integer.parseInt(date.substring(8));
            Calendar calendar = GregorianCalendar.getInstance();
            // 当 Calendar 处于 non-lenient 模式时，如果其日历字段中存在任何不一致性，它都会抛出一个异常。
            calendar.setLenient(false);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DATE, day);
           // 如果日期错误,执行该语句,必定抛出异常.
            calendar.get(Calendar.YEAR);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    private ArrayList getHirePathList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from codeitem where codesetid='35'");
			while(this.frowset.next())
			{
				CommonData data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				list.add(data);
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
}
