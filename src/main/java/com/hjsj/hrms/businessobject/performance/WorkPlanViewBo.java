package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workplanteam.WorkPlanTeamBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:WorkPlanViewBo.java</p>
 * <p>Description:工作纪实</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-15 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WorkPlanViewBo 
{
	private Connection con = null;
	private UserView userView = null;
	private String nbase = "Usr";
	private String a0100 = "";
	public static HashMap workParametersMap = new HashMap();//参数采用静态变量，不用每次都查。	
	private String state;//=0 日报    =1 周报    =2 月报     =3 季报     =4 年报
	private String helpScript;//借助此写脚本
	
	public WorkPlanViewBo()
	{
		
	}
	public WorkPlanViewBo(UserView userView,Connection con)
	{
		this.userView=userView;
		this.con=con;
	}
	public WorkPlanViewBo(UserView userView,Connection con,String state)
	{
		this.userView=userView;
		this.con=con;
		this.state = state;
	}
	public WorkPlanViewBo(Connection con,UserView userView,String state,String nbase,String a0100)
	{
		this.con = con;
		this.userView = userView;
		this.state = state;
		this.nbase = nbase;
		this.a0100 = a0100;
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
	 * 取得查询年列表
	 * @return
	 */
	public ArrayList getYearList()
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
		    StringBuffer buf = new StringBuffer("");
		    buf.append(" select distinct "+Sql_switcher.year("p0104")+" as ayear from p01 ");
		    buf.append(" where ");
		    buf.append(" state="+this.state);
		    buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
		    buf.append(" and a0100='"+this.a0100+"' order by "+Sql_switcher.year("p0104")+" desc");
		    ContentDAO dao = new ContentDAO(this.con);
		    rs=dao.search(buf.toString());
//		    boolean isAddThisYear=true;
		    
		    String prior_end11 = isNullToZero((String)WorkPlanViewBo.workParametersMap.get("prior_end11"));
		    String prior_end12 = isNullToZero((String)WorkPlanViewBo.workParametersMap.get("prior_end12"));
		    String prior_end13 = isNullToZero((String)WorkPlanViewBo.workParametersMap.get("prior_end13"));
		    String prior_end14 = isNullToZero((String)WorkPlanViewBo.workParametersMap.get("prior_end14")); 
		    
		    Calendar d = Calendar.getInstance();
		    String thisYear=Calendar.getInstance().get(Calendar.YEAR)+"";
		    String nextYear=(Integer.valueOf(thisYear)+1)+"";
		    list.add(new CommonData(thisYear,thisYear));
		    //2015/12/29 wangjl 当一年剩余天数少于填写计划时年列表中增加一年
		    if((d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end11)||
	    		(d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end12)||
	    		(d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end13)||
	    		(d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end14))
		    {	
		    	nextYear=Calendar.getInstance().get(Calendar.YEAR)+1+"";
		    	list.add(new CommonData(nextYear,nextYear));
		    }
//		    String thisYear=(d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))>=Integer.parseInt(current_start14)?Calendar.getInstance().get(Calendar.YEAR)+"":Calendar.getInstance().get(Calendar.YEAR)+1+"";
//		    int n=0;
		    while(rs.next())
		    {
//		    	n++;
//		    	if(ayear!=null && ayear.trim().length()>0 && ayear.equalsIgnoreCase(thisYear))
//		    		isAddThisYear=false;
		    	String ayear=rs.getString("ayear");
		    	if(StringUtils.isNotEmpty(ayear)&&!ayear.equals(thisYear)&&!ayear.equals(nextYear)){
		    		list.add(new CommonData(ayear,ayear));
		    	}
		    }
		    
//		    if(n==0)
//		    	list.add(new CommonData(Calendar.getInstance().get(Calendar.YEAR)+"",Calendar.getInstance().get(Calendar.YEAR)+""));
		    
//		    if(isAddThisYear)
//		    	list.add(new CommonData(thisYear,thisYear));
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	public static ArrayList getMonthList()
	{
		ArrayList list = new ArrayList();
		list.add(new CommonData("1",ResourceFactory.getProperty("performance.workplan.workplanteam.january")));
		list.add(new CommonData("2",ResourceFactory.getProperty("performance.workplan.workplanteam.february")));
		list.add(new CommonData("3",ResourceFactory.getProperty("performance.workplan.workplanteam.march")));
		list.add(new CommonData("4",ResourceFactory.getProperty("performance.workplan.workplanteam.april")));
		list.add(new CommonData("5",ResourceFactory.getProperty("performance.workplan.workplanteam.may")));
		list.add(new CommonData("6",ResourceFactory.getProperty("performance.workplan.workplanteam.june")));
		list.add(new CommonData("7",ResourceFactory.getProperty("performance.workplan.workplanteam.july")));
		list.add(new CommonData("8",ResourceFactory.getProperty("performance.workplan.workplanteam.august")));
		list.add(new CommonData("9",ResourceFactory.getProperty("performance.workplan.workplanteam.september")));
		list.add(new CommonData("10",ResourceFactory.getProperty("performance.workplan.workplanteam.october")));
		list.add(new CommonData("11",ResourceFactory.getProperty("performance.workplan.workplanteam.november")));
		list.add(new CommonData("12",ResourceFactory.getProperty("performance.workplan.workplanteam.december")));
		return list;
	}
	/**
	 * 工作纪实首页
	 * @param year
	 * @param month
	 * @param status
	 * @param a0101
	 * @param searchterm 工作纪实查询条件
	 * @return
	 */
	public ArrayList getWorkList(String year,String month,String status,String a0101, String searchterm){
		this.createNewTable("per_diary_opinion");
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			// workType=1个人年工作计划总结。=2团队年工作计划总结=3个人季度工作计划总结=4团队季度工作计划总结=5个人月工作计划总结=6团队月工作计划总结=7个人日工作计划总结=8团队日工作计划总结=9个人周工作计划总结=10团队周工作计划总结
			// state=0 日报    =1 周报    =2 月报     =3 季报     =4 年报
			if("0".equals(this.state))//日报
			{
				String valid0=(String)WorkPlanViewBo.workParametersMap.get("valid0");
				//current_date=”1” time=”8:30”  current_date:（1|0）=（当日|次日）
    			String current_date=(String)WorkPlanViewBo.workParametersMap.get("current_date");
    			String time=(String)WorkPlanViewBo.workParametersMap.get("time");
    			String refer_id0=(String)WorkPlanViewBo.workParametersMap.get("refer_id0");
    			String print_id0=(String)WorkPlanViewBo.workParametersMap.get("print_id0");
    			if(valid0==null || valid0.trim().length()<=0 || "0".equals(valid0)) {
                    return list;
                }
    			HashMap jhMap = new HashMap();
				HashMap zjMap = new HashMap();
				StringBuffer buf = new StringBuffer("");
				buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.month("p0104")+" as amonth,"+Sql_switcher.day("p0104")+" as aday, p0115,log_type,p0100,curr_user from p01 p ");
				buf.append(" where ");
				buf.append(" state="+this.state);
				buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
				buf.append(" and a0100='"+this.a0100+"'");
				buf.append(" and "+Sql_switcher.year("p0104")+"="+year);
				buf.append(" and "+Sql_switcher.month("p0104")+"="+month);
				//2016/1/28 wangjl 工作纪实新增一个查询条件
				if(StringUtils.isNotEmpty(searchterm)){
					buf.append(" and ( exists (select 1 from per_diary_opinion pdd where p.p0100=pdd.p0100 and lower(p.nbase)=lower(pdd.nbase) and Description like '%"+ searchterm +"%')");
					buf.append("or  exists (select 1 from P01  where  p.p0100=P01.p0100 and lower(p.nbase)=lower(P01.nbase) and  P0103 like '%"+ searchterm +"%' )");
				    buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase) and  Content like '%"+ searchterm +"%' ))");
				}
				buf.append(" order by p0104");
				rs = dao.search(buf.toString());
				while(rs.next())
				{
					int log_type=rs.getInt("log_type");//=1计划=2总结
					String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
					String aday=rs.getString("aday");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
					bean.set("p0115",p0115);
					bean.set("p0100", rs.getString("p0100"));
					bean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base(rs.getString("p0100"))));
					if(log_type==1)
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
					    jhMap.put(aday, bean);
					}else{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						zjMap.put(aday, bean);
					}
				}
				Calendar calendar = Calendar.getInstance();
			    calendar.set(Calendar.YEAR,Integer.parseInt(year));
			    calendar.set(Calendar.MONTH,Integer.parseInt(month)-1);
				calendar.set(Calendar.DAY_OF_MONTH,1);
				int maxDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				for(int i=1;i<=maxDay;i++)
				{
					LazyDynaBean bean = new LazyDynaBean();
					ArrayList alist = new ArrayList();
					String t_time="";
					if(current_date!=null && current_date.trim().length()>0 && "0".equals(current_date))
					{
						t_time=i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.theDay");
					}else{
						if(i!=maxDay) {
                            t_time=i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.day")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.to")+"  "+(i+1)+ResourceFactory.getProperty("performance.workplan.workplanview.day")+"    "+time;
                        } else {
                            t_time=i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.day")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.to")+"  "+ResourceFactory.getProperty("performance.workplan.workplanview.nextDay")+"   "+time;
                        }
					}
					if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
						if(jhMap.get((i+""))!=null)//以填报
			    		{
			    			LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+i));
							/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
							bean.set("p0115",p0115);
							bean.set("name","工作计划");*/
			    			abean.set("time",t_time);
			    			String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
			    			String p0115=(String)abean.get("p0115");
			    			String curr_user = null;
			    			if(abean.get("curr_user")!=null) {
                                curr_user=(String)abean.get("curr_user");
                            }
			    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
			    			{
			    				opt="1";
			    			}
			    			else{
			    		    	if(this.isEditTimeJH(0, 0,Integer.parseInt(month), "0",Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
			    			}
			    			abean.set("log_type", "1");
			    			abean.set("year",year);
			    			abean.set("monthNum", i+"");
			    			abean.set("opt", opt);
			    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
							alist.add(abean);
			    		}
			    		if(zjMap.get((i+""))!=null)//以填报
			    		{
			    			LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+i));
			    			abean.set("time",t_time);
			    			String opt="0";
			    			String p0115=(String)abean.get("p0115");
			    			String curr_user = null;
			    			if(abean.get("curr_user")!=null) {
                                curr_user=(String)abean.get("curr_user");
                            }
			    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
			    			{
			    				opt="1";
			    			}else{
			    		    	if(this.isEditTimeZJ(0, 0, Integer.parseInt(month), "0", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
			    			}
			    			abean.set("log_type", "2");
			    			abean.set("year",year);
			    			abean.set("monthNum", i+"");
			    			abean.set("opt", opt);
			    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
							alist.add(abean);
			    			
			    		}
					}else{//查询条件为空
			    		if(jhMap.get((i+""))!=null)//以填报
			    		{
			    			LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+i));
							/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
							bean.set("p0115",p0115);
							bean.set("name","工作计划");*/
			    			abean.set("time",t_time);
			    			String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
			    			String p0115=(String)abean.get("p0115");
			    			String curr_user = null;
			    			if(abean.get("curr_user")!=null) {
                                curr_user=(String)abean.get("curr_user");
                            }
			    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
			    			{
			    				opt="1";
			    			}
			    			else{
			    		    	if(this.isEditTimeJH(0, 0,Integer.parseInt(month), "0",Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
			    			}
			    			abean.set("log_type", "1");
			    			abean.set("year",year);
			    			abean.set("monthNum", i+"");
			    			abean.set("opt", opt);
			    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
							alist.add(abean);
			    		}else{//未填过的
			    			LazyDynaBean abean = new LazyDynaBean();
			                abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
			                abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
			                abean.set("time",t_time);
			                abean.set("p0115","01");
			                String opt="2";
			                if(this.isEditTimeJH(0, 0,Integer.parseInt(month), "0",Integer.parseInt(year),i)) {
                                opt="1";
                            }
			                abean.set("opt", opt);
			                abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
			                abean.set("log_type", "1");
			    			abean.set("year",year);
			    			abean.set("monthNum", i+"");
			    			abean.set("p0100", "");
			    			abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
			                alist.add(abean);
			    		}
			    		if(zjMap.get((i+""))!=null)//以填报
			    		{
			    			LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+i));
			    			abean.set("time",t_time);
			    			String opt="0";
			    			String p0115=(String)abean.get("p0115");
			    			String curr_user = null;
			    			if(abean.get("curr_user")!=null) {
                                curr_user=(String)abean.get("curr_user");
                            }
			    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
			    			{
			    				opt="1";
			    			}else{
			    		    	if(this.isEditTimeZJ(0, 0, Integer.parseInt(month), "0", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
			    			}
			    			abean.set("log_type", "2");
			    			abean.set("year",year);
			    			abean.set("monthNum", i+"");
			    			abean.set("opt", opt);
			    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
							alist.add(abean);
			    			
			    		}else{
			    			LazyDynaBean abean = new LazyDynaBean();
			                abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
			                abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
			                abean.set("time",t_time);
			                abean.set("p0115","01");
			                String opt="2";
			                if(this.isEditTimeZJ(0, 0, Integer.parseInt(month), "0", Integer.parseInt(year),i)) {
                                opt="1";
                            }
			                abean.set("opt", opt);
			                abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
			                abean.set("log_type", "2");
			    			abean.set("year",year);
			    			abean.set("monthNum", i+"");
			    			abean.set("p0100", "");
			    			abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
			                alist.add(abean);
			    		}
			    		bean.set("month",i+ResourceFactory.getProperty("performance.workplan.workplanview.day"));
				    	bean.set("subList",alist);
				    	bean.set("rowspan",alist.size()+"");
				    	bean.set("year_num",year+"");
				    	bean.set("month_num",month);
				    	bean.set("day_num", i+"");
				    	list.add(bean);
					}
				}
			}
			else if("1".equals(this.state))//周报
			{
				//周计划
	    		String valid11 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid11"));
	    		String prior_end11 = isNull((String)WorkPlanViewBo.workParametersMap.get("prior_end11"));
	    		String current_start11 = isNull((String)WorkPlanViewBo.workParametersMap.get("current_start11"));
	    		String refer_id11 = isNull((String)WorkPlanViewBo.workParametersMap.get("refer_id11"));
	    		String print_id11 = isNull((String)WorkPlanViewBo.workParametersMap.get("print_id11"));
	    		//周总结
	    		String valid21 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid21"));		    		    	    		
	    		String current_end21 = isNull((String)WorkPlanViewBo.workParametersMap.get("current_end21"));
	    		String last_start21 = isNull((String)WorkPlanViewBo.workParametersMap.get("last_start21"));
	    		String refer_id21 = isNull((String)WorkPlanViewBo.workParametersMap.get("refer_id21"));
	    		String print_id21 = isNull((String)WorkPlanViewBo.workParametersMap.get("print_id21"));
	    		
	    		if("0".equals(valid11) && "0".equals(valid21)) {
                    return list;
                }
	    		
	    		WeekUtils weekutils = new WeekUtils();
				int totalweek = weekutils.totalWeek(Integer.parseInt(year),Integer.parseInt(month));				
				String startime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year),Integer.parseInt(month),1,1)); // 某年某月的第1周星期1的日期(返回字符串格式日期)
				String endtime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year),Integer.parseInt(month),totalweek,7)); // 某年某月的最后1周星期日的日期(返回字符串格式日期)				
	    		HashMap jhMap = new HashMap();
				HashMap zjMap = new HashMap();
				StringBuffer buf = new StringBuffer("");
				buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.month("p0104")+" as amonth,"+Sql_switcher.day("p0104")+" as aday, p0115,log_type,p0100,p0104,curr_user from p01 p ");
				buf.append(" where ");
				buf.append(" state="+this.state);
				buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
				buf.append(" and a0100='"+this.a0100+"'");
				//2015/12/29 wangjl 解决周计划、总结填报后仍然显示未填报
			//	buf.append(" and "+Sql_switcher.year("p0104")+"="+year);
			//	buf.append(" and "+Sql_switcher.month("p0104")+"="+month);				
				buf.append(" and p0104 between "+Sql_switcher.dateValue(startime)+ " and " +Sql_switcher.dateValue(endtime));	
				if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
					buf.append(" and ( exists (select 1 from per_diary_opinion pdd where p.p0100=pdd.p0100 and lower(p.nbase)=lower(pdd.nbase) and Description like '%"+ searchterm +"%')");
					buf.append("or  exists (select 1 from P01  where  p.p0100=P01.p0100 and lower(p.nbase)=lower(P01.nbase) and  P0103 like '%"+ searchterm +"%' )");
				    buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase) and  Content like '%"+ searchterm +"%' ))");
				}
				buf.append(" order by p0104");
				rs = dao.search(buf.toString());
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
				while(rs.next())
				{
					int log_type=rs.getInt("log_type");//=1计划=2总结
					String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
					bean.set("p0115",p0115);
					bean.set("p0100", rs.getString("p0100"));
					bean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base(rs.getString("p0100"))));
					String p0104_format=format.format(rs.getDate("p0104"));
					if(log_type==1)
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
					    jhMap.put(p0104_format+"", bean);
					}else{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						zjMap.put(p0104_format+"", bean);
					}
				}
				for(int i=1;i<=totalweek;i++)
				{
					Date startdate = weekutils.numWeek(Integer.parseInt(year),Integer.parseInt(month),i,1);
					String start_format=format.format(startdate);
					LazyDynaBean bean = new LazyDynaBean();
			    	ArrayList alist = new ArrayList();
					if(StringUtils.isNotEmpty(searchterm))
					{//查询条件不为空
						if("1".equals(valid11))//计划
				    	{
				    		String t_time="";
					    	if(i==1) {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo01")+prior_end11+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo02")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo03")+current_start11+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            } else {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+(i-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo04")+prior_end11+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo05")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo03")+current_start11+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            }
					    	if(jhMap.get((start_format+""))!=null)//以填报
				    		{
				    			LazyDynaBean abean =(LazyDynaBean)jhMap.get((start_format));
				    			abean.set("time",t_time);
				    			String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    			String p0115=(String)abean.get("p0115");
				    			//2015/12/23 wangjl 驳回状态不能继续填报
				    			String curr_user = "";
				    			if(abean.get("curr_user")!=null) {
                                    curr_user=(String)abean.get("curr_user");
                                }
				    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    			{
				    				opt="1";
				    			}
				    			else{
				    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end11), Integer.parseInt(current_start11), Integer.parseInt(month), "1", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    		    		opt="1";
				    	    		}
				    			}
				    			abean.set("log_type", "1");
				    			abean.set("year",year);
				    			abean.set("monthNum", i+"");
				    			abean.set("opt", opt);
				    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
								alist.add(abean);
				    		}
				    	}
				    	if("1".equals(valid21))// 总结
				    	{
				    		String t_time="";
					    	if(i==totalweek) {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo04")+current_end21+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo06")+last_start21+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            } else {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo04")+current_end21+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo02")+" "+(i+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo03")+last_start21+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            }
					    					    					    	
					    	if(zjMap.get((start_format+""))!=null)//以填报
				    		{
				    			LazyDynaBean abean =(LazyDynaBean)zjMap.get((start_format));
				    			abean.set("time",t_time);
				    			String opt="0";
				    			String p0115=(String)abean.get("p0115");
				    			//2015/12/23 wangjl 驳回状态不能继续填报
				    			String curr_user = "";
				    			if(abean.get("curr_user")!=null) {
                                    curr_user=(String)abean.get("curr_user");
                                }
				    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    			{
				    				opt="1";
				    			}else{
				    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end21), Integer.parseInt(last_start21), Integer.parseInt(month), "1", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    		    		opt="1";
				    	    		}
				    			}
				    			abean.set("log_type", "2");
				    			abean.set("year",year);
				    			abean.set("opt", opt);	
				    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
								alist.add(abean);
				    			
				    		}
				    	}
						
					}
					else
					{//查询条件为空
				    	if("1".equals(valid11))//计划
				    	{
				    		String t_time="";
					    	if(i==1) {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo01")+prior_end11+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo02")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo03")+current_start11+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            } else {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+(i-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo04")+prior_end11+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo05")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo03")+current_start11+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            }
					    	if(jhMap.get((start_format+""))!=null)//以填报
				    		{
				    			LazyDynaBean abean =(LazyDynaBean)jhMap.get((start_format));
								/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
								bean.set("p0115",p0115);
								bean.set("name","工作计划");*/
				    			abean.set("time",t_time);
				    			String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    			String p0115=(String)abean.get("p0115");
				    			//2015/12/23 wangjl 驳回状态不能继续填报
				    			String curr_user = "";
				    			if(abean.get("curr_user")!=null) {
                                    curr_user=(String)abean.get("curr_user");
                                }
				    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    			{
				    				opt="1";
				    			}
				    			else{
				    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end11), Integer.parseInt(current_start11), Integer.parseInt(month), "1", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    		    		opt="1";
				    	    		}
				    			}
				    			abean.set("log_type", "1");
				    			abean.set("year",year);
				    			abean.set("monthNum", i+"");
				    			abean.set("opt", opt);
				    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
								alist.add(abean);
				    		}else{//未填过的
				    			LazyDynaBean abean = new LazyDynaBean();
				                abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				                abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
				                abean.set("time",t_time);
				                abean.set("p0115","01");
				                String opt="2";
				                if(this.isEditTimeJH(Integer.parseInt(prior_end11), Integer.parseInt(current_start11), Integer.parseInt(month), "1", Integer.parseInt(year),i)) {
                                    opt="1";
                                }
				                abean.set("opt", opt);
				                abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				                abean.set("log_type", "1");
				    			abean.set("year",year);
				    			abean.set("monthNum", i+"");
				    			abean.set("p0100", "");
				    			abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				                alist.add(abean);
				    		}
				    	}
				    	if("1".equals(valid21))// 总结
				    	{
				    		String t_time="";
					    	if(i==totalweek) {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo04")+current_end21+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo06")+last_start21+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            } else {
                                t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo04")+current_end21+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo02")+" "+(i+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.weekInfo03")+last_start21+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                            }
					    					    					    	
					    	if(zjMap.get((start_format+""))!=null)//以填报
				    		{
				    			LazyDynaBean abean =(LazyDynaBean)zjMap.get((start_format));
				    			abean.set("time",t_time);
				    			String opt="0";
				    			String p0115=(String)abean.get("p0115");
				    			//2015/12/23 wangjl 驳回状态不能继续填报
				    			String curr_user = "";
				    			if(abean.get("curr_user")!=null) {
                                    curr_user=(String)abean.get("curr_user");
                                }
				    			if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    			{
				    				opt="1";
				    			}else{
				    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end21), Integer.parseInt(last_start21), Integer.parseInt(month), "1", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    		    		opt="1";
				    	    		}
				    			}
				    			abean.set("log_type", "2");
				    			abean.set("year",year);
				    			abean.set("opt", opt);	
				    			abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
								alist.add(abean);
				    			
				    		}else{
				    			LazyDynaBean abean = new LazyDynaBean();
				                abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				                abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
				                abean.set("time",t_time);
				                abean.set("p0115","01");
				                String opt="2";
				                if(this.isEditTimeZJ(Integer.parseInt(current_end21), Integer.parseInt(last_start21), Integer.parseInt(month), "1", Integer.parseInt(year),i)) {
                                    opt="1";
                                }
				                abean.set("opt", opt);
				                abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				                abean.set("log_type", "2");
				    			abean.set("year",year);
				    			abean.set("p0100", "");
				    			abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				                alist.add(abean);
				    		}
				    	}
			    	}
			    	//2015/12/24 wangjl 给每周加入时间划分
			    	bean.set("month",ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.week")+"("+DateUtils.FormatDate(weekutils.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 1),"yyyy.MM.dd")+"-"+DateUtils.FormatDate(weekutils.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 7),"yyyy.MM.dd")+")");
			    	bean.set("subList",alist);
			    	bean.set("rowspan",alist.size()+"");
			    	bean.set("year_num",year+"");
			    	bean.set("month_num",month);
			    	bean.set("day_num", "1");
			    	bean.set("week_num", i+"");
			    	list.add(bean);
			    	
				}

			}
			else if("2".equals(this.state)) // 月报
			{
				//计划参数
				String valid12="0";
				if(workParametersMap.get("valid12")!=null) {
                    valid12=(String)workParametersMap.get("valid12");
                }
				String prior_end12="";
				if(workParametersMap.get("prior_end12")!=null) {
                    prior_end12=(String)workParametersMap.get("prior_end12");
                }
				String current_start12="";
				if(workParametersMap.get("current_start12")!=null) {
                    current_start12=(String)workParametersMap.get("current_start12");
                }
				String period12="";
				if(workParametersMap.get("period12")!=null) {
                    period12=(String)workParametersMap.get("period12");
                }
				String refer_id12="";
				if(workParametersMap.get("refer_id12")!=null) {
                    refer_id12=(String)workParametersMap.get("refer_id12");
                }
				String print_id12="";
				if(workParametersMap.get("print_id12")!=null) {
                    print_id12=(String)workParametersMap.get("print_id12");
                }
				//总结参数
				String valid22="0";
				if(workParametersMap.get("valid22")!=null) {
                    valid22=(String)workParametersMap.get("valid22");
                }
				String current_end22="";
				if(workParametersMap.get("current_end22")!=null) {
                    current_end22=(String)workParametersMap.get("current_end22");
                }
				String last_start22="";
				if(workParametersMap.get("last_start22")!=null) {
                    last_start22=(String)workParametersMap.get("last_start22");
                }
                String period22="";
                if(workParametersMap.get("period22")!=null) {
                    period22=(String)workParametersMap.get("period22");
                }
    			String refer_id22="";
    			if(workParametersMap.get("refer_id22")!=null) {
                    refer_id22=(String)workParametersMap.get("refer_id22");
                }
    			String print_id22="";
    			if(workParametersMap.get("print_id22")!=null) {
                    print_id22=(String)workParametersMap.get("print_id22");
                }
    			if("0".equals(valid12)&&"0".equals(valid22)) {
                    return list;
                }
    			String t_period12=","+period12+",";
		    	String t_period22=","+period22+",";
			//	if("5".equals(this.workType))//个人月报
				{
					HashMap jhMap = new HashMap();
					HashMap zjMap = new HashMap();
					StringBuffer buf = new StringBuffer("");
					buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.month("p0104")+" as amonth,p0115,log_type,p0100,curr_user from p01 p ");
					buf.append(" where ");
					buf.append(" state="+this.state);
					buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
					buf.append(" and a0100='"+this.a0100+"'");
					buf.append(" and "+Sql_switcher.year("p0104")+"="+year);
					if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
						buf.append(" and ( exists (select 1 from per_diary_opinion pdd where p.p0100=pdd.p0100 and lower(p.nbase)=lower(pdd.nbase) and Description like '%"+ searchterm +"%')");
						buf.append("or  exists (select 1 from P01  where  p.p0100=P01.p0100 and lower(p.nbase)=lower(P01.nbase) and  P0103 like '%"+ searchterm +"%' )");
					    buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase) and  Content like '%"+ searchterm +"%' ))");
					}
					buf.append(" order by p0104");
					rs = dao.search(buf.toString());
					while(rs.next())
					{
						int log_type=rs.getInt("log_type");//=1计划=2总结
						String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
						String amonth=rs.getString("amonth");
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
						bean.set("p0115",p0115);
						bean.set("p0100", rs.getString("p0100"));
						bean.set("curr_user", isNull(rs.getString("curr_user")));
						bean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base(rs.getString("p0100"))));
						if(log_type==1)
						{
							bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						    jhMap.put(amonth, bean);
						}else{
							bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
							zjMap.put(amonth, bean);
						}
					}
				    for(int i=1;i<=12;i++)
				    {
				    	
				    	String str=","+i+",";	
				    	if(t_period12.indexOf(str)==-1&&t_period22.indexOf(str)==-1) {
                            continue;
                        }
				    	LazyDynaBean bean = new LazyDynaBean();
				    	ArrayList alist = new ArrayList();
				    	if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
				    		if("1".equals(valid12)&&t_period12.indexOf(str)!=-1)//计划
				    		{
				    			String t_time="";
				    			if(i==1) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo01")+prior_end12+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo02")+"  "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo03")+current_start12+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=(i-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo04")+prior_end12+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo02")+"  "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo03")+current_start12+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			if(jhMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+i));
				    				/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
								bean.set("p0115",p0115);
								bean.set("name","工作计划");*/
				    				abean.set("time",t_time);
				    				String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}
				    				else{
				    					if(this.isEditTimeJH(Integer.parseInt(prior_end12), Integer.parseInt(current_start12), i, "2", Integer.parseInt(year),0)&& "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "1");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    			}
				    		}
				    		if("1".equals(valid22)&&t_period22.indexOf(str)!=-1){//总结
				    			String t_time="";
				    			if(i==12) {
                                    t_time=i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo04")+current_end22+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo05")+"   "+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo06")+last_start22+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo04")+current_end22+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo05")+"   "+(i+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo07")+last_start22+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			
				    			if(zjMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+i));
				    				abean.set("time",t_time);
				    				String opt="0";
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}else{
				    					if(this.isEditTimeZJ(Integer.parseInt(current_end22), Integer.parseInt(last_start22), i, "2", Integer.parseInt(year),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "2");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    				
				    			}
				    		}

				    	}else{//查询条件为空
				    		if("1".equals(valid12)&&t_period12.indexOf(str)!=-1)//计划
				    		{
				    			String t_time="";
				    			if(i==1) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo01")+prior_end12+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo02")+"  "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo03")+current_start12+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=(i-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo04")+prior_end12+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo02")+"  "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo03")+current_start12+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			if(jhMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+i));
				    				/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
								bean.set("p0115",p0115);
								bean.set("name","工作计划");*/
				    				abean.set("time",t_time);
				    				String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}
				    				else{
				    					if(this.isEditTimeJH(Integer.parseInt(prior_end12), Integer.parseInt(current_start12), i, "2", Integer.parseInt(year),0)&& "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "1");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    			}else{//未填过的
				    				LazyDynaBean abean = new LazyDynaBean();
				    				abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				    				abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
				    				abean.set("time",t_time);
				    				abean.set("p0115","01");
				    				String opt="2";
				    				if(this.isEditTimeJH(Integer.parseInt(prior_end12), Integer.parseInt(current_start12), i, "2", Integer.parseInt(year),0)) {
                                        opt="1";
                                    }
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				abean.set("log_type", "1");
				    				abean.set("year",year);
				    				abean.set("p0100", "");
				    				abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				    				alist.add(abean);
				    			}
				    		}
				    		if("1".equals(valid22)&&t_period22.indexOf(str)!=-1){//总结
				    			String t_time="";
				    			if(i==12) {
                                    t_time=i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo04")+current_end22+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo05")+"   "+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo06")+last_start22+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo04")+current_end22+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo05")+"   "+(i+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.monthInfo07")+last_start22+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			
				    			if(zjMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+i));
				    				abean.set("time",t_time);
				    				String opt="0";
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}else{
				    					if(this.isEditTimeZJ(Integer.parseInt(current_end22), Integer.parseInt(last_start22), i, "2", Integer.parseInt(year),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "2");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    				
				    			}else{
				    				LazyDynaBean abean = new LazyDynaBean();
				    				abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				    				abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
				    				abean.set("time",t_time);
				    				abean.set("p0115","01");
				    				String opt="2";
				    				if(this.isEditTimeZJ(Integer.parseInt(current_end22), Integer.parseInt(last_start22), i, "2", Integer.parseInt(year),0)) {
                                        opt="1";
                                    }
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				abean.set("log_type", "2");
				    				abean.set("year",year);
				    				abean.set("p0100", "");
				    				abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				    				alist.add(abean);
				    			}
				    		}
				    	}
				    	bean.set("month",i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.month"));
				    	bean.set("subList",alist);
				    	bean.set("rowspan",alist.size()+"");
				    	bean.set("year_num",year+"");
				    	bean.set("month_num",i+"");
				    	bean.set("day_num", "1");
				    	list.add(bean);
				    }
				}
			//	else if("6".equals(this.workType))//团队月报
				{
					
				}
			}
			else if("3".equals(this.state)) // 季报
			{				
				// 季计划
	    		String valid13 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid13"));
	    		String prior_end13 = isNull((String)WorkPlanViewBo.workParametersMap.get("prior_end13"));
	    		String current_start13 = isNull((String)WorkPlanViewBo.workParametersMap.get("current_start13"));
	    		String refer_id13 = isNull((String)WorkPlanViewBo.workParametersMap.get("refer_id13"));
	    		String print_id13 = isNull((String)WorkPlanViewBo.workParametersMap.get("print_id13"));
	    		String period13 = isNull((String)WorkPlanViewBo.workParametersMap.get("period13"));
	    		// 季总结
	    		String valid23 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid23"));		    		    	    		
	    		String current_end23 = isNull((String)WorkPlanViewBo.workParametersMap.get("current_end23"));
	    		String last_start23 = isNull((String)WorkPlanViewBo.workParametersMap.get("last_start23"));
	    		String refer_id23 = isNull((String)WorkPlanViewBo.workParametersMap.get("refer_id23"));
	    		String print_id23 = isNull((String)WorkPlanViewBo.workParametersMap.get("print_id23"));
	    		String period23 = isNull((String)WorkPlanViewBo.workParametersMap.get("period23"));
	    		String t_period13 = ","+period13+",";
		    	String t_period23 = ","+period23+",";
		    	
		    	if("0".equals(valid13) && "0".equals(valid23)) {
                    return list;
                }
		    	
	    	//	if("3".equals(this.workType))//个人季报
				{
					HashMap jhMap = new HashMap();
					HashMap zjMap = new HashMap();
					StringBuffer buf = new StringBuffer("");
					buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.quarter("p0104")+" as aquarter,p0115,log_type,p0100,curr_user from p01 p ");
					buf.append(" where ");
					buf.append(" state="+this.state);
					buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
					buf.append(" and a0100='"+this.a0100+"'");
					buf.append(" and "+Sql_switcher.year("p0104")+"="+year);
					if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
						buf.append(" and ( exists (select 1 from per_diary_opinion pdd where p.p0100=pdd.p0100 and lower(p.nbase)=lower(pdd.nbase) and Description like '%"+ searchterm +"%')");
						buf.append("or  exists (select 1 from P01  where  p.p0100=P01.p0100 and lower(p.nbase)=lower(P01.nbase) and  P0103 like '%"+ searchterm +"%' )");
					    buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase) and  Content like '%"+ searchterm +"%' ))");
					}
					buf.append(" order by p0104");
					rs = dao.search(buf.toString());
					while(rs.next())
					{
						int log_type=rs.getInt("log_type");//=1计划=2总结
						String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
						String aquarter=rs.getString("aquarter");
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
						bean.set("p0115",p0115);
						bean.set("p0100", rs.getString("p0100"));
						bean.set("curr_user", isNull(rs.getString("curr_user")));
						bean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base(rs.getString("p0100"))));
						if(log_type==1)
						{
							bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						    jhMap.put(aquarter, bean);
						}else{
							bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
							zjMap.put(aquarter, bean);
						}
					}
					
					for(int i=1;i<=4;i++)
				    {
				    	
				    	String str=","+i+",";	
				    	if(t_period13.indexOf(str)==-1 && t_period23.indexOf(str)==-1) {
                            continue;
                        }
				    	LazyDynaBean bean = new LazyDynaBean();
				    	ArrayList alist = new ArrayList();
				    	if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
				    		if("1".equals(valid13) && t_period13.indexOf(str)!=-1)//计划
				    		{
				    			String t_time="";
				    			if(i==1) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo01")+prior_end13+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo02")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo03")+current_start13+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+(i-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo04")+prior_end13+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo05")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo03")+current_start13+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			if(jhMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+i));
				    				/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
								bean.set("p0115",p0115);
								bean.set("name","工作计划");*/
				    				abean.set("time",t_time);
				    				String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}
				    				else{
				    					if(this.isEditTimeJH(Integer.parseInt(prior_end13), Integer.parseInt(current_start13), i, "3", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "1");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    			}
				    		}
				    		if("1".equals(valid23)&&t_period23.indexOf(str)!=-1){//总结
				    			String t_time="";
				    			if(i==4) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo04")+current_end23+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo06")+"   "+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo07")+last_start23+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo04")+current_end23+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo02")+" "+(i+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo03")+last_start23+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			
				    			if(zjMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+i));
				    				abean.set("time",t_time);
				    				String opt="0";
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}else{
				    					if(this.isEditTimeZJ(Integer.parseInt(current_end23), Integer.parseInt(last_start23), i, "3", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "2");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    				
				    			}
				    		}

				    	}else{//查询条件为空
				    		
				    		if("1".equals(valid13) && t_period13.indexOf(str)!=-1)//计划
				    		{
				    			String t_time="";
				    			if(i==1) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo01")+prior_end13+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo02")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo03")+current_start13+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+(i-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo04")+prior_end13+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo05")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo03")+current_start13+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			if(jhMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+i));
				    				/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
								bean.set("p0115",p0115);
								bean.set("name","工作计划");*/
				    				abean.set("time",t_time);
				    				String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}
				    				else{
				    					if(this.isEditTimeJH(Integer.parseInt(prior_end13), Integer.parseInt(current_start13), i, "3", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "1");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    			}else{//未填过的
				    				LazyDynaBean abean = new LazyDynaBean();
				    				abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				    				abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
				    				abean.set("time",t_time);
				    				abean.set("p0115","01");
				    				String opt="2";
				    				if(this.isEditTimeJH(Integer.parseInt(prior_end13), Integer.parseInt(current_start13), i, "3", Integer.parseInt(year),i)) {
                                        opt="1";
                                    }
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				abean.set("log_type", "1");
				    				abean.set("year",year);
				    				abean.set("p0100", "");
				    				abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				    				alist.add(abean);
				    			}
				    		}
				    		if("1".equals(valid23)&&t_period23.indexOf(str)!=-1){//总结
				    			String t_time="";
				    			if(i==4) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo04")+current_end23+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo06")+"   "+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo07")+last_start23+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo04")+current_end23+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo02")+" "+(i+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarterInfo03")+last_start23+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			
				    			if(zjMap.get((i+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+i));
				    				abean.set("time",t_time);
				    				String opt="0";
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}else{
				    					if(this.isEditTimeZJ(Integer.parseInt(current_end23), Integer.parseInt(last_start23), i, "3", Integer.parseInt(year),i) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "2");
				    				abean.set("year",year);
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    				
				    			}else{
				    				LazyDynaBean abean = new LazyDynaBean();
				    				abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				    				abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
				    				abean.set("time",t_time);
				    				abean.set("p0115","01");
				    				String opt="2";
				    				if(this.isEditTimeZJ(Integer.parseInt(current_end23), Integer.parseInt(last_start23), i, "3", Integer.parseInt(year),i)) {
                                        opt="1";
                                    }
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				abean.set("log_type", "2");
				    				abean.set("year",year);
				    				abean.set("p0100", "");
				    				abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				    				alist.add(abean);
				    			}
				    		}
				    	}
				    	bean.set("month",ResourceFactory.getProperty("performance.workplan.workplanview.di")+" "+i+" "+ResourceFactory.getProperty("performance.workplan.workplanview.quarter"));
				    	bean.set("subList",alist);
				    	bean.set("rowspan",alist.size()+"");
				    	bean.set("year_num",year+"");
				    	bean.set("month_num","1");
				    	bean.set("day_num", "1");
				    	bean.set("quarter_num",i+"");
				    	list.add(bean);
				    }					
				}
				
			}
			else if("4".equals(this.state)) // 年报
			{
				// 年计划
	    		String valid14 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid14"));
	    		String prior_end14 = isNull((String)WorkPlanViewBo.workParametersMap.get("prior_end14"));
	    		String current_start14 = isNull((String)WorkPlanViewBo.workParametersMap.get("current_start14"));
	    		String refer_id14 = isNull((String)WorkPlanViewBo.workParametersMap.get("refer_id14"));
	    		String print_id14 = isNull((String)WorkPlanViewBo.workParametersMap.get("print_id14"));
	    		// 年总结
	    		String valid24 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid24"));		    		    	    		
	    		String current_end24 = isNull((String)WorkPlanViewBo.workParametersMap.get("current_end24"));
	    		String last_start24 = isNull((String)WorkPlanViewBo.workParametersMap.get("last_start24"));
	    		String refer_id24 = isNull((String)WorkPlanViewBo.workParametersMap.get("refer_id24"));
	    		String print_id24 = isNull((String)WorkPlanViewBo.workParametersMap.get("print_id24"));
				
	    		if("0".equals(valid14) && "0".equals(valid24)) {
                    return list;
                }
	    		
	    	//	if("1".equals(this.workType))//个人年报
				{
					HashMap jhMap = new HashMap();
					HashMap zjMap = new HashMap();
					StringBuffer buf = new StringBuffer("");
					buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,p0115,log_type,p0100,curr_user from p01 p ");
					buf.append(" where ");
					buf.append(" state="+this.state);
					buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
					buf.append(" and a0100='"+this.a0100+"'");	
					if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
						buf.append(" and ( exists (select 1 from per_diary_opinion pdd where p.p0100=pdd.p0100 and lower(p.nbase)=lower(pdd.nbase) and Description like '%"+ searchterm +"%')");
						buf.append("or  exists (select 1 from P01  where  p.p0100=P01.p0100 and lower(p.nbase)=lower(P01.nbase) and  P0103 like '%"+ searchterm +"%' )");
					    buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase) and  Content like '%"+ searchterm +"%' ))");
					}
					buf.append(" order by p0104");
					rs = dao.search(buf.toString());
					while(rs.next())
					{
						int log_type=rs.getInt("log_type");//=1计划=2总结
						String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
						String ayear=rs.getString("ayear");
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
						bean.set("p0115",p0115);
						bean.set("p0100", rs.getString("p0100"));
						bean.set("curr_user", isNull(rs.getString("curr_user")));
						bean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base(rs.getString("p0100"))));
						if(log_type==1)
						{
							bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						    jhMap.put(ayear, bean);
						}else{
							bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
							zjMap.put(ayear, bean);
						}
					}
					
					// 获得登录用户涉及的年度
					ArrayList yearList = getYearList();					
					for(int i=0;i<yearList.size();i++)
				    {				    							
				 		CommonData d = (CommonData)yearList.get(i);
																		    	
				    	LazyDynaBean bean = new LazyDynaBean();
				    	ArrayList alist = new ArrayList();
				    	if(StringUtils.isNotEmpty(searchterm)){//查询条件不为空
				    		if("1".equals(valid14))//计划
				    		{
				    			String t_time="";
				    			if(i==0) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo01")+prior_end14+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo02")+"   "+d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo03")+current_start14+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=(Integer.parseInt(d.getDataValue())-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo04")+prior_end14+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo02")+"   "+d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo03")+current_start14+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			if(jhMap.get((d.getDataValue()+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+d.getDataValue()));
				    				/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
								bean.set("p0115",p0115);
								bean.set("name","工作计划");*/
				    				abean.set("time",t_time);
				    				String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}
				    				else{
				    					if(this.isEditTimeJH(Integer.parseInt(prior_end14), Integer.parseInt(current_start14), 0, "4", Integer.parseInt(d.getDataValue()),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "1");
				    				abean.set("year",d.getDataValue());
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    			}
				    		}
				    		if("1".equals(valid24)){//总结
				    			String t_time="";
				    			if(Integer.parseInt(d.getDataValue())==yearList.size()-1) {
                                    t_time=d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo04")+current_end24+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo05")+"  "+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo06")+last_start24+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo04")+current_end24+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo05")+"  "+(Integer.parseInt(d.getDataValue())+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo03")+last_start24+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			
				    			if(zjMap.get((d.getDataValue()+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+d.getDataValue()));
				    				abean.set("time",t_time);
				    				String opt="0";
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}else{
				    					if(this.isEditTimeZJ(Integer.parseInt(current_end24), Integer.parseInt(last_start24), 0, "4", Integer.parseInt(d.getDataValue()),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "2");
				    				abean.set("year",d.getDataValue());
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    				
				    			}
				    		}

				    	}else{
				    		
				    		if("1".equals(valid14))//计划
				    		{
				    			String t_time="";
				    			if(i==0) {
                                    t_time=ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo01")+prior_end14+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo02")+"   "+d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo03")+current_start14+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=(Integer.parseInt(d.getDataValue())-1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo04")+prior_end14+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo02")+"   "+d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo03")+current_start14+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			if(jhMap.get((d.getDataValue()+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)jhMap.get((""+d.getDataValue()));
				    				/*bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
								bean.set("p0115",p0115);
								bean.set("name","工作计划");*/
				    				abean.set("time",t_time);
				    				String opt="0";//操作按钮：=0查看，=1可填报，=2按钮置灰
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}
				    				else{
				    					if(this.isEditTimeJH(Integer.parseInt(prior_end14), Integer.parseInt(current_start14), 0, "4", Integer.parseInt(d.getDataValue()),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "1");
				    				abean.set("year",d.getDataValue());
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    			}else{//未填过的
				    				LazyDynaBean abean = new LazyDynaBean();
				    				abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				    				abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
				    				abean.set("time",t_time);
				    				abean.set("p0115","01");
				    				String opt="2";
				    				if(this.isEditTimeJH(Integer.parseInt(prior_end14), Integer.parseInt(current_start14), 0, "4", Integer.parseInt(d.getDataValue()),0)) {
                                        opt="1";
                                    }
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				abean.set("log_type", "1");
				    				abean.set("year",d.getDataValue());
				    				abean.set("p0100", "");
				    				abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				    				alist.add(abean);
				    			}
				    		}
				    		if("1".equals(valid24)){//总结
				    			String t_time="";
				    			if(Integer.parseInt(d.getDataValue())==yearList.size()-1) {
                                    t_time=d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo04")+current_end24+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo05")+"  "+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo06")+last_start24+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                } else {
                                    t_time=d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo04")+current_end24+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo05")+"  "+(Integer.parseInt(d.getDataValue())+1)+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearInfo03")+last_start24+ResourceFactory.getProperty("performance.workplan.workplanview.tian");
                                }
				    			
				    			if(zjMap.get((d.getDataValue()+""))!=null)//以填报
				    			{
				    				LazyDynaBean abean =(LazyDynaBean)zjMap.get((""+d.getDataValue()));
				    				abean.set("time",t_time);
				    				String opt="0";
				    				String p0115=(String)abean.get("p0115");
				    				String curr_user = null;
				    				if(abean.get("curr_user")!=null) {
                                        curr_user=(String)abean.get("curr_user");
                                    }
				    				if("07".equals(p0115)&&"".equals(curr_user))//驳回状态何时都能填报
				    				{
				    					if(curr_user==null || "".equals(curr_user.trim())) {
                                            opt="1";
                                        }
				    				}else{
				    					if(this.isEditTimeZJ(Integer.parseInt(current_end24), Integer.parseInt(last_start24), 0, "4", Integer.parseInt(d.getDataValue()),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
				    						opt="1";
				    					}
				    				}
				    				abean.set("log_type", "2");
				    				abean.set("year",d.getDataValue());
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				alist.add(abean);
				    				
				    			}else{
				    				LazyDynaBean abean = new LazyDynaBean();
				    				abean.set("p0115desc",ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
				    				abean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
				    				abean.set("time",t_time);
				    				abean.set("p0115","01");
				    				String opt="2";
				    				if(this.isEditTimeZJ(Integer.parseInt(current_end24), Integer.parseInt(last_start24), 0, "4", Integer.parseInt(d.getDataValue()),0)) {
                                        opt="1";
                                    }
				    				abean.set("opt", opt);
				    				abean.set("mdopt", SafeCode.encode(PubFunc.convertTo64Base(opt)));
				    				abean.set("log_type", "2");
				    				abean.set("year",d.getDataValue());
				    				abean.set("p0100", "");
				    				abean.set("mdp0100", SafeCode.encode(PubFunc.convertTo64Base("")));
				    				alist.add(abean);
				    			}
				    		}
				    	}
				    	bean.set("month",""+d.getDataValue()+" "+ResourceFactory.getProperty("performance.workplan.workplanview.yearDu"));
				    	bean.set("subList",alist);
				    	bean.set("rowspan",alist.size()+"");
				    	bean.set("month_num","1");
				    	bean.set("day_num", "1");
				    	bean.set("year_num",d.getDataValue()+"");
				    	list.add(bean);
				    }					
				}
				
			}			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 判断当前计划是否可编辑
	 * @param oneDay 上月末XX天
	 * @param twoDay 本月初XX天
	 * @param curr_month 当前记录月份（可能别的填报周期的判断用不到这个）
	 * @param cycle 填报周期
	 * @param curr_year 当前记录年
	 * @param curr_day 当前记录日期（号）
	 * @return
	 */
	public boolean isEditTimeJH(int oneDay,int twoDay,int curr_month,String cycle,int curr_year,int curr_day)
	{
		//上月末，本月初    cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)

		boolean flag=false;
        if("0".equals(cycle))
        {
        	//current_date=”1” time=”8:30”  current_date:（1|0）=（当日|次日）
        	//当为日报时，start_date,end_date值相同
        	Calendar calendar = Calendar.getInstance();
        	String current_date=(String)WorkPlanViewBo.workParametersMap.get("current_date");
        	if(current_date!=null && current_date.trim().length()>0 && "0".equals(current_date))//当日
        	{
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		return calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR)&&calendar.get(Calendar.MONTH)==start_calendar.get(Calendar.MONTH)&&calendar.get(Calendar.DAY_OF_MONTH)==start_calendar.get(Calendar.DAY_OF_MONTH);
        	}else{//次日
        		String time = (String)WorkPlanViewBo.workParametersMap.get("time");
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		start_calendar.set(Calendar.HOUR_OF_DAY,0);
        		start_calendar.set(Calendar.MINUTE,0);
        		start_calendar.set(Calendar.SECOND,0);
        		
        		String[] hhmm = new String[2];
        		if(time!=null && time.trim().length()>0) {
                    hhmm = time.split(":");
                }
        		Calendar end_calendar=Calendar.getInstance();
        		end_calendar.set(Calendar.YEAR,curr_year);
        		end_calendar.set(Calendar.MONTH,curr_month-1);
        		end_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		end_calendar.add(Calendar.DAY_OF_MONTH,1);
        		end_calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hhmm[0]));
        		end_calendar.set(Calendar.MINUTE,Integer.parseInt(hhmm[1]));
        		end_calendar.set(Calendar.SECOND,0);
        		//System.out.println("day_num="+curr_day+"------"+start_calendar.get(Calendar.YEAR)+"-"+(start_calendar.get(Calendar.MONTH)+1)+"-"+start_calendar.get(Calendar.DAY_OF_MONTH));
        		int result=end_calendar.compareTo(calendar);
        		int result2=start_calendar.compareTo(calendar);
        		return ((result==0||result==1)&&(result2==0||result2==-1)&&calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR))?true:false;
        	}
        	
        }
        else if("1".equals(cycle))//周报
        {
        	//curr_day为当前是第几周
        	WeekUtils weekutils = new WeekUtils();
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        	Date startdate = weekutils.numWeek(curr_year,curr_month,curr_day,1);
        	String[] s_t=format.format(startdate).split("-");
			Date enddate = weekutils.numWeek(curr_year,curr_month,curr_day,7);
			String[] e_t=format.format(enddate).split("-");
			Calendar calendar=Calendar.getInstance();
			Calendar acalendar = Calendar.getInstance();
			Calendar bcalendar=Calendar.getInstance();
        	Calendar ccalendar=Calendar.getInstance();
        	
        	acalendar.set(Calendar.YEAR,Integer.parseInt(s_t[0]));
        	acalendar.set(Calendar.MONTH,Integer.parseInt(s_t[1])-1);
        	acalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(s_t[2]));
        	acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	
//        	bcalendar.set(Calendar.YEAR,Integer.parseInt(e_t[0]));
//        	bcalendar.set(Calendar.MONTH,Integer.parseInt(e_t[1])-1);
//        	bcalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(e_t[2]));
        	
        	ccalendar.set(Calendar.YEAR,Integer.parseInt(e_t[0]));
        	ccalendar.set(Calendar.MONTH,Integer.parseInt(e_t[1])-1);
        	ccalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(e_t[2]));
        	ccalendar.add(Calendar.DAY_OF_MONTH,twoDay-7);//
        	//2015/12/23 wangjl 需要比较当前时间 acalendar一周起始时间  ccalendar一周截止时间 calendar当前时间
        	//上周期的后多少天，本周期前的多少天
//        	return (ccalendar.compareTo(acalendar)>=0||acalendar.compareTo(calendar)<=0)&&ccalendar.compareTo(calendar)>=0;
        	return (acalendar.compareTo(calendar)<=0&&ccalendar.compareTo(calendar)>=0);
        	
        }
        else if("2".equals(cycle))
        {
        /*	
        	Calendar calendar = Calendar.getInstance();
        	int day=calendar.get(Calendar.DAY_OF_MONTH);
        	
        	Calendar upCalendar = Calendar.getInstance();//2012-06-27
        	//跨年？？
        	if(curr_month==1)
        	{
        		upCalendar.set(Calendar.MONTH,11);
        		upCalendar.set(Calendar.YEAR,curr_year-1);
        	}else{
            	upCalendar.set(Calendar.MONTH,curr_month-2);//2012-01-27
        	}
        	int upMaxDay=upCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        	upCalendar.set(Calendar.DAY_OF_MONTH, upMaxDay);
        	upCalendar.set(Calendar.DAY_OF_MONTH, (upMaxDay-oneDay));
           // System.out.println(upCalendar.get(Calendar.YEAR)+"-"+(upCalendar.get(Calendar.MONTH)+1)+"-"+upCalendar.get(Calendar.DAY_OF_MONTH));
            
        	Calendar dwCalendar = Calendar.getInstance();
        	dwCalendar.set(Calendar.MONTH, curr_month-1);
        	dwCalendar.set(Calendar.DAY_OF_MONTH, twoDay);
        	int dwCount=dwCalendar.get(Calendar.YEAR)*10000+(dwCalendar.get(Calendar.MONTH)+1)*100+dwCalendar.get(Calendar.DAY_OF_MONTH);
        	int upCount=upCalendar.get(Calendar.YEAR)*10000+(upCalendar.get(Calendar.MONTH)+1)*100+upCalendar.get(Calendar.DAY_OF_MONTH);
        	int cuCount=calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
        	
        	return (dwCount>=cuCount&&cuCount>=upCount);
        */
        	
        	Calendar calendar = Calendar.getInstance();
    		Calendar acalendar=Calendar.getInstance();
    		Calendar bcalendar=Calendar.getInstance();
    		acalendar.set(Calendar.YEAR,curr_year);
    		acalendar.set(Calendar.MONTH,curr_month-2);
    		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
    	    
    	    bcalendar.set(Calendar.YEAR,curr_year);
    	    bcalendar.set(Calendar.MONTH,curr_month-1);
    	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
    	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
    	    
    	//    System.out.println("------------------------------------------------------------------");
    	//    System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
    	//    System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
    	    
    	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);        	
        }
        else if("3".equals(cycle))//季报
        {
        	//curr_day为当前记录是第几季度
        	if(curr_day==1)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,-1);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,0);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==2)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,2);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,3);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);        		
        	}
        	else if(curr_day==3)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,5);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,6);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        	//  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==4)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,8);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,9);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        }
        else if("4".equals(cycle))//年报
        {
        	Calendar calendar = Calendar.getInstance();
    		Calendar acalendar=Calendar.getInstance();
    		Calendar bcalendar=Calendar.getInstance();
    		acalendar.set(Calendar.YEAR,curr_year);
    		acalendar.set(Calendar.MONTH,-1);
    		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    		acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
    		
    		bcalendar.set(Calendar.YEAR,curr_year);
    		bcalendar.set(Calendar.MONTH,0);
    		bcalendar.set(Calendar.DAY_OF_MONTH,1);
    		bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
    		
    	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
    		
    		return calendar.compareTo(acalendar)>=0&&calendar.compareTo(bcalendar)<=0;
    		
        }
		return flag;
	}
	/**
	 * 判断当前总结是否可编辑
	 * @param oneDay
	 * @param twoDay
	 * @param curr
	 * @param cycle
	 * @param curr_year
	 * @return
	 */
	public boolean isEditTimeZJ(int oneDay,int twoDay,int curr_month,String cycle,int curr_year,int curr_day)
	{
		//本月末，下月初  cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
		boolean flag = false;
		
		if("0".equals(cycle))
	    {
			//current_date=”1” time=”8:30”  current_date:（1|0）=（当日|次日）
        	//当为日报时，start_date,end_date值相同
        	Calendar calendar = Calendar.getInstance();
        	String current_date=(String)WorkPlanViewBo.workParametersMap.get("current_date");
        	if("0".equals(current_date))//当日
        	{
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		return calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR)&&calendar.get(Calendar.MONTH)==start_calendar.get(Calendar.MONTH)&&calendar.get(Calendar.DAY_OF_MONTH)==start_calendar.get(Calendar.DAY_OF_MONTH);
        	}
        	else // 次日
        	{
        		String time=(String)WorkPlanViewBo.workParametersMap.get("time");
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		start_calendar.set(Calendar.HOUR_OF_DAY,0);
        		start_calendar.set(Calendar.MINUTE,0);
        		start_calendar.set(Calendar.SECOND,0);
        		
        		String[] hhmm=time.split(":");
        		Calendar end_calendar=Calendar.getInstance();
        		end_calendar.set(Calendar.YEAR,curr_year);
        		end_calendar.set(Calendar.MONTH,curr_month-1);
        		end_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		end_calendar.add(Calendar.DAY_OF_MONTH,1);
        		end_calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hhmm[0]));
        		end_calendar.set(Calendar.MINUTE,Integer.parseInt(hhmm[1]));
        		end_calendar.set(Calendar.SECOND,0);
        		//System.out.println("day_num="+curr_day+"------"+start_calendar.get(Calendar.YEAR)+"-"+(start_calendar.get(Calendar.MONTH)+1)+"-"+start_calendar.get(Calendar.DAY_OF_MONTH));
        		int result=end_calendar.compareTo(calendar);
        		int result2=start_calendar.compareTo(calendar);
        		return ((result==0||result==1)&&(result2==0||result2==-1)&&calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR))?true:false;
        	}
			
	    }else if("1".equals(cycle))
	    {
	    	//curr_day为当前是第几周 //本月末，下月初  
	    	//curr_day为当前是第几周
        	WeekUtils weekutils = new WeekUtils();
        	Date startdate = weekutils.numWeek(curr_year,curr_month,curr_day,1);
			Date enddate = weekutils.numWeek(curr_year,curr_month,curr_day,7);
        	Calendar calendar=Calendar.getInstance();
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        	String[] e_t=format.format(enddate).split("-");
        	Calendar acalendar = Calendar.getInstance();
        	acalendar.set(Calendar.YEAR,Integer.parseInt(e_t[0]));
        	acalendar.set(Calendar.MONTH,Integer.parseInt(e_t[1])-1);
        	acalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(e_t[2]));
        	
        	acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	
        	Calendar bcalendar = Calendar.getInstance();
        	bcalendar.set(Calendar.YEAR,Integer.parseInt(e_t[0]));
        	bcalendar.set(Calendar.MONTH,Integer.parseInt(e_t[1])-1);
        	bcalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(e_t[2]));
        	bcalendar.add(Calendar.DAY_OF_MONTH,twoDay+1);
        	
        	return calendar.compareTo(acalendar)>=0&&calendar.compareTo(bcalendar)<=0;
	    }
	    else if("2".equals(cycle))
	    {
	    /*
	        Calendar calendar = Calendar.getInstance();
	        Calendar cuCalendar = Calendar.getInstance();
	        Calendar ntCalendar = Calendar.getInstance();
	        cuCalendar.set(Calendar.MONTH,curr_month-1);
	        int maxDay=cuCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	        cuCalendar.set(Calendar.DAY_OF_MONTH,maxDay-oneDay);
	        	
	        if(curr_month==12)
	        {
	        	ntCalendar.set(Calendar.YEAR,curr_year+1);
	        	ntCalendar.set(Calendar.MONTH,0);
	        }else{
	            ntCalendar.set(Calendar.MONTH,curr_month);
	        }
	        ntCalendar.set(Calendar.DAY_OF_MONTH, twoDay);
	        int ntCount=ntCalendar.get(Calendar.YEAR)*10000+(ntCalendar.get(Calendar.MONTH)+1)*100+ntCalendar.get(Calendar.DAY_OF_MONTH);
	        int cuCount=cuCalendar.get(Calendar.YEAR)*10000+(cuCalendar.get(Calendar.MONTH)+1)*100+cuCalendar.get(Calendar.DAY_OF_MONTH);
	        int count=calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
	        return (ntCount>=count&&count>=cuCount);
	    */
	    	Calendar calendar = Calendar.getInstance();
    		Calendar acalendar=Calendar.getInstance();
    		Calendar bcalendar=Calendar.getInstance();
    		acalendar.set(Calendar.YEAR,curr_year);
    		acalendar.set(Calendar.MONTH,curr_month-1);
    		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
    	    
    	    bcalendar.set(Calendar.YEAR,curr_year);
    	    bcalendar.set(Calendar.MONTH,curr_month);
    	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
    	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
    	    
    	 //   System.out.println("=======================================================");
    	 //   System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
    	 //   System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
    	    
    	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
	    }
	    else if("3".equals(cycle)) // 季报
	    {
	    	//curr_day为当前记录是第几季度
        	if(curr_day==1)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,2);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,3);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        	//  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==2)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,5);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,6);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);        		
        	}
        	else if(curr_day==3)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,8);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,9);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        	//  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==4)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,11);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,12);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}	        	
	    }
	    else if("4".equals(cycle)) // 年报
	    {
	    	Calendar calendar = Calendar.getInstance();
    		Calendar acalendar=Calendar.getInstance();
    		Calendar bcalendar=Calendar.getInstance();
    		acalendar.set(Calendar.YEAR,curr_year);
    		acalendar.set(Calendar.MONTH,11);
    		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    		acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
    		
    		bcalendar.set(Calendar.YEAR,curr_year);
    		bcalendar.set(Calendar.MONTH,12);
    		bcalendar.set(Calendar.DAY_OF_MONTH,1);
    		bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
    		
    	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
    		
    		return calendar.compareTo(acalendar)>=0&&calendar.compareTo(bcalendar)<=0;	    	
	    }
		return flag;
	}
	/**
	 * 分析设置的用于工作计划和总结的参数
	 */
	public void analyseParameter()
	{
		
		RowSet rs =null;
		try
		{
			workParametersMap.clear();
            ContentDAO dao = new ContentDAO(this.con);	
          /*  < work_records  sp_relation=”xxx” nbase="Usr" sp_level="2" record_grade="True" planTarget="P0413,P0415" summTarget="P04Aa,P04Ac" defaultLines="12"
           * 	dailyPlan_attachment=”true” dailySumm_attachment =”true” >   
           * 	//sp_relation:审批关系 nbase:人员库设置  sp_level: 审批层级 1|2 一级审批|逐级审批  record_grade: 纪实评分，True|False  defaultLines:  默认行数
           * 	planTarget: 工作计划指标 ，分隔  summTarget: 工作总结指标 ，分隔  dailyPlan_attachment:工作计划附件上传  dailySumm_attachment：工作总结附件上传
                cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
                type:(1|2)=(工作计划|工作总结)  
        		valid:是否有效（1|0）=（有效|无效）
                prior_end:上期末 current_start:本期初  current_end:本期末 
                last_start:下期初   Period:填报周期  refer_id:参考信息登记表
                print_id:打印信息登记表
        		<work_record     cycle=”3” > 
        	        <template type="2"  valid=”1”  prior_end=”3”  current_start=”5” Period=” 1,2,3,4,5,6,7,8,9,10,11,12”  refer_id=”1”  print_id=”2” >
        	    </work_record>

        	    <work_record     cycle=”4” > //日报  current_date:（1|0）=（当日|次日）
                   <template type="0"  valid=”1”  current_date=”1” time=”8:30” refer_id=”1”  print_id=”2” > 
                </work_record>
            < work_records />*/				
			rs = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if ( rs.next())
		    {
		    	String str_value = rs.getString("str_value");
		    	if (str_value == null || str_value.trim().length()<=0)
		    	{
	
		    	} else
		    	{
		    		Document doc = PubFunc.generateDom(str_value);
		    		String xpath = "//work_records";
		    		XPath xpath_ = XPath.newInstance(xpath);
		    		Element ele = (Element) xpath_.selectSingleNode(doc);
		    		if (ele != null)
		    		{
		    			String sp_relation = ele.getAttributeValue("sp_relation");
		    			workParametersMap.put("sp_relation", sp_relation);
		    			String nbase = ele.getAttributeValue("nbase");	
		    			workParametersMap.put("nbase", nbase);		    			
		    			workParametersMap.put("sp_level", ele.getAttributeValue("sp_level"));
		    			workParametersMap.put("record_grade", ele.getAttributeValue("record_grade"));
		    			workParametersMap.put("defaultLines", ele.getAttributeValue("defaultLines"));		    			
		    			workParametersMap.put("dailyPlan_attachment", ele.getAttributeValue("dailyPlan_attachment"));
		    			workParametersMap.put("dailySumm_attachment", ele.getAttributeValue("dailySumm_attachment"));
		    			workParametersMap.put("planTarget", ele.getAttributeValue("planTarget"));
		    			workParametersMap.put("summTarget", ele.getAttributeValue("summTarget"));		    			
		    			List list = (List) ele.getChildren("work_record");
	    				for (int i = 0; i < list.size(); i++)
	    				{   					
	    					Element wele = (Element) list.get(i);;     						   	    					
	    					if (wele != null)
	    		    	    {	    							    						
	    						String cycle = wele.getAttributeValue("cycle"); 
	    		    	    	
	    		    	    	/*
	    		    	    	 * cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
	    		    	    	   type:(1|2)=(工作计划|工作总结)  valid:是否有效（1|0）=（有效|无效）
 								   prior_end:上期末 current_start:本期初  current_end:本期末 
								   last_start:下期初   Period:填报周期  refer_id:参考信息登记表
								   print_id:打印信息登记表
	    		    	    	*/
	    		    	    	
	    		    	    	List dlist = (List) wele.getChildren("template");
	    		    	    	for (int k = 0; k < dlist.size(); k++)
	    		    	    	{    	    		    
	    		    	    		Element temp = (Element) dlist.get(k);
	    		    	    		if(temp!=null)
	    		    	    		{	 	    		    	    			
	    		    	    			String type = isNull(temp.getAttributeValue("type"));		    		    	    		
		    		    	    		
		    		    	    		if("4".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//年计划
		    		    	    		{
		    		    	    			String valid14 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String prior_end14 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start14 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String refer_id14 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id14 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid14", valid14);
		    		    	    			workParametersMap.put("prior_end14", prior_end14);
		    		    	    			workParametersMap.put("current_start14", current_start14);
		    		    	    			workParametersMap.put("refer_id14", refer_id14);
		    		    	    			workParametersMap.put("print_id14", print_id14);
		    		    	    		}
		    		    	    		else if("4".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//年总结
		    		    	    		{
		    		    	    			String valid24 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_end24 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start24 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String refer_id24 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id24 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid24", valid24);
		    		    	    			workParametersMap.put("current_end24", current_end24);
		    		    	    			workParametersMap.put("last_start24", last_start24);
		    		    	    			workParametersMap.put("refer_id24", refer_id24);
		    		    	    			workParametersMap.put("print_id24", print_id24);
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//季计划
		    		    	    		{
		    		    	    			String valid13 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String prior_end13 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start13 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String period13 = isNull(temp.getAttributeValue("period"));
		    		    	    			String refer_id13 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id13 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid13", valid13);
		    		    	    			workParametersMap.put("prior_end13", prior_end13);
		    		    	    			workParametersMap.put("current_start13", current_start13);
		    		    	    			workParametersMap.put("period13", period13);
		    		    	    			workParametersMap.put("refer_id13", refer_id13);
		    		    	    			workParametersMap.put("print_id13", print_id13);
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//季总结
		    		    	    		{
		    		    	    			String valid23 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_end23 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start23 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String period23 = isNull(temp.getAttributeValue("period"));	    	    			
		    		    	    			String refer_id23 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id23 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid23",valid23);
		    		    	    			workParametersMap.put("current_end23",current_end23);
		    		    	    			workParametersMap.put("last_start23",last_start23);
		    		    	    			workParametersMap.put("period23",period23);
		    		    	    			workParametersMap.put("refer_id23",refer_id23);
		    		    	    			workParametersMap.put("print_id23",print_id23);
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//月计划
		    		    	    		{
		    		    	    			String valid12 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String prior_end12 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start12 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String period12 = isNull(temp.getAttributeValue("period"));		    		    	    			
		    		    	    			String refer_id12 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id12 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid12",valid12);
		    		    	    			workParametersMap.put("prior_end12",prior_end12);
		    		    	    			workParametersMap.put("current_start12",current_start12);
		    		    	    			workParametersMap.put("period12",period12);
		    		    	    			workParametersMap.put("refer_id12",refer_id12);
		    		    	    			workParametersMap.put("print_id12",print_id12);
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//月总结
		    		    	    		{
		    		    	    			String valid22 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_end22 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start22 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String period22 = isNull(temp.getAttributeValue("period"));
		    		    	    			String refer_id22 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id22 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid22", valid22);
		    		    	    			workParametersMap.put("current_end22", current_end22);
		    		    	    			workParametersMap.put("last_start22", last_start22);
		    		    	    			workParametersMap.put("period22", period22);
		    		    	    			workParametersMap.put("refer_id22", refer_id22);
		    		    	    			workParametersMap.put("print_id22", print_id22);		    		    	    			
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//周计划
		    		    	    		{
		    		    	    			String valid11 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String prior_end11 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start11 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String refer_id11 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id11 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid11", valid11);
		    		    	    			workParametersMap.put("prior_end11", prior_end11);
		    		    	    			workParametersMap.put("current_start11", current_start11);
		    		    	    			workParametersMap.put("refer_id11", refer_id11);
		    		    	    			workParametersMap.put("print_id11", print_id11);
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//周总结
		    		    	    		{
		    		    	    			String valid21 = isNull(temp.getAttributeValue("valid"));		    		    	    		
		    		    	    			String current_end21 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start21 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String refer_id21 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id21 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid21", valid21);		    		    	    		
		    		    	    			workParametersMap.put("current_end21", current_end21);
		    		    	    			workParametersMap.put("last_start21", last_start21);
		    		    	    			workParametersMap.put("refer_id21", refer_id21);
		    		    	    			workParametersMap.put("print_id21", print_id21);
		    		    	    		}
		    		    	    		else if("0".equalsIgnoreCase(cycle))
		    		    	    		{
		    		    	    			String valid0 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_date = isNull(temp.getAttributeValue("current_date"));
		    		    	    			String time = isNull(temp.getAttributeValue("time"));
		    		    	    			String refer_id0 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id0 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid0", valid0);
		    		    	    			workParametersMap.put("current_date", current_date);
		    		    	    			workParametersMap.put("time", time);
		    		    	    			workParametersMap.put("refer_id0", refer_id0);
		    		    	    			workParametersMap.put("print_id0", print_id0);
		    		    	    			
		    		    	    		}
			    		    	    }
	    		    	    	}	    		    	    		    		    	    		    		    	    	
	    		    	    }
	    		    	}																									    			
		    		}
		    	}
		    } 
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }
	public String isNullToZero(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str)) {
            str = "0";
        }
		return str;
    }
	/**
	 * 展现新增或编辑计划时，填写计划内容出的html
	 * @param p0100
	 * @param opt =0查看，=1填写
	 * @return
	 */
	public String getPlanHtmlStr(String p0100,String opt)
	{
		StringBuffer buf = new StringBuffer("");
		StringBuffer scriptBuf = new StringBuffer("");
		RowSet rs = null;
		try
		{
			String isWrite = "";
			if("0".equalsIgnoreCase(opt) || "3".equalsIgnoreCase(opt)) // 对于计划和总结 =0查看，=1可填报，=2按钮置灰 =3审批
            {
                isWrite = "readonly=\"true\" disabled ";
            }
			
			boolean isHave = true;
			if(p0100!=null && !"".equals(p0100))
			{
		    	ContentDAO dao = new ContentDAO(this.con);
		    	rs = dao.search("select Content, Record_num from per_diary_content where p0100="+p0100+" order by Record_num");
		    	buf.append("<table width=\"450\" border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" id=\"tabPut\">");
		    	int rowNum = 0;
		    	while(rs.next())
		    	{
		    		isHave=false;
		    		String content=Sql_switcher.readMemo(rs, "content");
		    		int recordNum=rs.getInt("record_num");
		    		String[] arr = null;
		    		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                        arr = content.split("\\n");
                    } else {
                        arr = content.split("\\r\\n");
                    }
		    		for(int i=0;i<arr.length;i++)
		    		{
		    			if(i==0)
		    			{
		    				buf.append("<tr id=\""+recordNum+"_"+(i+1)+"\">");
		    				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
		    				buf.append(ResourceFactory.getProperty("performance.workplan.workplanview.di")+recordNum+ResourceFactory.getProperty("performance.workplan.workplanview.tiao"));
		    				buf.append("</td>");
		    				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
		    				buf.append("<input type=\"text\" value=\""+arr[i]+"\" class=\"TEXT_big\" size=\"120\" id=\""+recordNum+"_"+(i+1)+"_put\" name=\"name_"+recordNum+"\" onkeydown=\"scanKey(this,"+recordNum+","+(i+1)+");\" maxlength=\"132\" "+isWrite+" />");
		    				buf.append("</td>");
		    				buf.append("</tr>");
		    			}else{
		    				buf.append("<tr id=\""+recordNum+"_"+(i+1)+"\">");
		    				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
		    				buf.append("  ");
		    				buf.append("</td>");
		    				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
		    				buf.append("<input type=\"text\" value=\""+arr[i]+"\" class=\"TEXT_big\" size=\"120\" id=\""+recordNum+"_"+(i+1)+"_put\" name=\"name_"+recordNum+"\" onkeydown=\"scanKey(this,"+recordNum+","+(i+1)+");\" maxlength=\"132\" "+isWrite+" />");
		    				buf.append("</td>");
		    				buf.append("</tr>");
		    			}
		    		}
		    		if(arr.length==0)
		    		{
		    			buf.append("<tr id=\""+recordNum+"_1\">");
	    				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
	    				buf.append(ResourceFactory.getProperty("performance.workplan.workplanview.di")+recordNum+ResourceFactory.getProperty("performance.workplan.workplanview.tiao"));
	    				buf.append("</td>");
	    				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
	    				buf.append("<input type=\"text\" value=\"\" class=\"TEXT_big\" size=\"120\" id=\""+recordNum+"_1_put\" name=\"name_"+recordNum+"\" onkeydown=\"scanKey(this,"+recordNum+",1);\" maxlength=\"132\" "+isWrite+" />");
	    				buf.append("</td>");
	    				buf.append("</tr>");
		    		}
		    		scriptBuf.append(":"+(arr.length==0?1:arr.length));		    		
		    		rowNum++;
		    	}
		    	/*
		    	if(rowNum<12 && opt.equalsIgnoreCase("1"))
		    	{
		    		for(int i=(rowNum+1);i<=12;i++)
		    		{
		    			buf.append("<tr id=\""+i+"_1\">");
	    				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
	    				buf.append("第"+i+"条：");
	    				buf.append("</td>");
	    				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
	    				buf.append("<input type=\"text\" value=\"\" class=\"TEXT_big\" size=\"120\" id=\""+i+"_1_put\" name=\"name_"+i+"\" onkeydown=\"scanKey(this,"+i+",1);\" maxlength=\"132\" "+isWrite+" />");
	    				buf.append("</td>");
	    				buf.append("</tr>");	
	    				scriptBuf.append(":1");
		    		}		    		
		    	}
		    	*/
		    	buf.append("</table>");
			}
			
			if(isHave || "".equals(p0100))
			{
				buf.setLength(0);
				scriptBuf.setLength(0);
				String defaultLines = (String)WorkPlanViewBo.workParametersMap.get("defaultLines"); // 工作计划默认行数
				if(defaultLines==null || defaultLines.trim().length()<=0) {
                    defaultLines = "12";
                }
				
				buf.append("<table width=\"450\" border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" id=\"tabPut\">");
				for(int i=1;i<=Integer.parseInt(defaultLines);i++)
	    		{
	    			buf.append("<tr id=\""+i+"_1\">");
    				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
    				buf.append(ResourceFactory.getProperty("performance.workplan.workplanview.di")+i+ResourceFactory.getProperty("performance.workplan.workplanview.tiao"));
    				buf.append("</td>");
    				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
    				buf.append("<input type=\"text\" value=\"\" class=\"TEXT_big\" size=\"120\" id=\""+i+"_1_put\" name=\"name_"+i+"\" onkeydown=\"scanKey(this,"+i+",1);\" maxlength=\"132\" "+isWrite+" />");
    				buf.append("</td>");
    				buf.append("</tr>");	
    				scriptBuf.append(":1");
	    		}
				buf.append("</table>");
			/*	
				buf.append("<table width=\"450\" border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" id=\"tabPut\">");
				buf.append("<tr id=\"1_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第1条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" size=\"120\" id=\"1_1_put\" name=\"name_1\" onkeydown=\"scanKey(this,1,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"2_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第2条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"2_1_put\" name=\"name_2\" size=\"120\" onkeydown=\"scanKey(this,2,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"3_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第3条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"3_1_put\" name=\"name_3\" size=\"120\" onkeydown=\"scanKey(this,3,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"4_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第4条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"4_1_put\" name=\"name_4\" size=\"120\" onkeydown=\"scanKey(this,4,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"5_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第5条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"5_1_put\" name=\"name_5\" size=\"120\" onkeydown=\"scanKey(this,5,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"6_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第6条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"6_1_put\" name=\"name_6\" size=\"120\" onkeydown=\"scanKey(this,6,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"7_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第7条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"7_1_put\" name=\"name_7\" size=\"120\" onkeydown=\"scanKey(this,7,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"8_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第8条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"8_1_put\" name=\"name_8\" size=\"120\" onkeydown=\"scanKey(this,8,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"9_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第9条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"9_1_put\" name=\"name_9\" size=\"120\" onkeydown=\"scanKey(this,9,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"10_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第10条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"10_1_put\" name=\"name_10\" size=\"120\" onkeydown=\"scanKey(this,10,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"11_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第11条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"11_1_put\" name=\"name_11\" size=\"120\" onkeydown=\"scanKey(this,11,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr id=\"12_1\">");
				buf.append("<td align=\"right\" width=\"60\" class=\"RecordRow_big\" nowrap>");
				buf.append("第12条：");
				buf.append("</td>");
				buf.append("<td align=\"left\" width=\"120\" class=\"RecordRow_big\" >");
				buf.append("<input type=\"text\" class=\"TEXT_big\" id=\"12_1_put\" name=\"name_12\" size=\"120\" onkeydown=\"scanKey(this,12,1);\" maxlength=\"132\" "+isWrite+" />");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("</table>");
				scriptBuf.append(":1:1:1:1:1:1:1:1:1:1:1:1");
			*/
			}
			if(scriptBuf.toString().length()>0)
			{
				this.setHelpScript(scriptBuf.toString().substring(1));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		return buf.toString();
	}
	
	/**
	 * 展现新增或编辑总结时，填写总结内容处的
	 * @param p0100
	 * @param opt =0查看，=1填写
	 * @return
	 */
	public String getSummaryStr(String p0100)
	{
		String SummaryStr = "";		
		RowSet rs = null;
		try
		{						
			if(p0100!=null && !"".equals(p0100))
			{
		    	ContentDAO dao = new ContentDAO(this.con);
		    	rs = dao.search("select p0103 from p01 where p0100="+p0100+" ");
		    	while(rs.next())
		    	{
		    		SummaryStr = Sql_switcher.readMemo(rs, "p0103");		    		
		    	}		    			    	
			}						
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		return SummaryStr;
	}
	/**
	 * 保存计划
	 * @param flag       flag=1 保存 flag=2 报批
	 * @param appbody_id 审批人编号
	 * @param p0100
	 * @param planContent
	 * @param editContentList
	 * @param log_type
	 * @param copyToStr
	 * @param year_num
	 * @param quarter_num
	 * @param month_num
	 * @param week_num
	 * @param day_num
	 * @return
	 */
	public int savePlan(String addORupdate,String flag,String appbody_id,String p0100,String planContent,ArrayList editContentList,LazyDynaBean leaderCommandsBean,int log_type,String copyToStr,String year_num,String quarter_num,String month_num,String week_num,String day_num,String userStatus,String sp_level,String recordGradeName)
	{
		int pid = 0;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			
			Calendar cl = Calendar.getInstance();
    	    int year = cl.get(Calendar.YEAR);
    	    int month = cl.get(Calendar.MONTH)+1;
    	    int day = cl.get(Calendar.DAY_OF_MONTH);
    	    int hh = cl.get(Calendar.HOUR_OF_DAY);
    	    int mm = cl.get(Calendar.MINUTE);
    	    int ss = cl.get(Calendar.SECOND);
    	    String creatDate = year+"-"+month+"-"+day+" "+hh+":"+mm+":"+ss;  // 系统当前日期			
			switch(Sql_switcher.searchDbServer())
		    {
				case Constant.ORACEL:
			    {
			    	creatDate="to_date('"+creatDate+"','yyyy-mm-dd hh24:mi:ss')";
			    	break;
			    }
			}			
			
			ArrayList insertList = new ArrayList();
			StringBuffer sql = new StringBuffer("");
			
			if(p0100!=null && !"".equals(p0100)) // 修改
			{
				dao.delete("delete from per_diary_content where p0100="+p0100, new ArrayList());
			}
			
			ArrayList baseInfo = this.getBaseInfo(p0100, 2,year_num, quarter_num, month_num, week_num, day_num);
			pid=Integer.parseInt(p0100);
			RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100",pid);
			if(addORupdate!=null && "update".equalsIgnoreCase(addORupdate)) {
                vo = dao.findByPrimaryKey(vo);
            } else
			{
				vo.setInt("state",Integer.parseInt(this.state));
				vo.setInt("log_type",log_type);
				vo.setString("p0115", "01");
				vo.setString("a0100", this.a0100);
				vo.setDate("p0104",this.getStartDateAndEndDate(year_num, quarter_num, month_num, week_num, day_num, 1));
				vo.setDate("p0106", this.getStartDateAndEndDate(year_num, quarter_num, month_num, week_num, day_num, 2));
				for(int i=0;i<baseInfo.size();i++)
				{
					LazyDynaBean bean = (LazyDynaBean)baseInfo.get(i);
					String itemid=(String)bean.get("itemid");
					String value=(String)bean.get("value");
					vo.setString(itemid,value);
					
					if("a0000".equalsIgnoreCase(itemid)) // 在此把此人的最新排序号赋值给他自己
					{
						dao.update("update p01 set a0000='"+ value +"' where nbase='"+ this.nbase +"' and a0100='"+ this.a0100 +"' ");
					}
				}
			}
			
			// p01表中，可编辑的指标
			if(editContentList!=null && editContentList.size()>0)
			{
				for(int i=0;i<editContentList.size();i++)
				{
					LazyDynaBean bean = (LazyDynaBean)editContentList.get(i);
					String itemid=(String)bean.get("itemid");
					String itemtype=(String)bean.get("itemtype");
					String value=(String)bean.get("value");
					String deciwidth=(String)bean.get("deciwidth");
					if(value!=null&&!"".equals(value))
					{
						if("A".equals(itemtype)) {
                            vo.setString(itemid.toLowerCase(),value);
                        } else if("D".equals(itemtype))
    					{
    						String[] dd=value.split("-");
    						Calendar d=Calendar.getInstance();
    						d.set(Calendar.YEAR,Integer.parseInt(dd[0]));
    						d.set(Calendar.MONTH,Integer.parseInt(dd[1])-1);
    						d.set(Calendar.DATE,Integer.parseInt(dd[2]));
    						vo.setDate(itemid.toLowerCase(),d.getTime());
    					}
    					else if("M".equals(itemtype)) {
                            vo.setString(itemid.toLowerCase(),value);
                        } else if("N".equals(itemtype))
    					{
    						if("0".equals(deciwidth)) {
                                vo.setInt(itemid.toLowerCase(),Integer.parseInt(value));
                            } else {
                                vo.setDouble(itemid.toLowerCase(),Double.parseDouble(value));
                            }
    					}
					}
				}				
			}
			if(userStatus!=null && !"".equalsIgnoreCase(userStatus) && !"0".equalsIgnoreCase(userStatus)){
				//领导登录才可以保存领导批示
				// 领导批示
				if(leaderCommandsBean!=null)
				{
					String uvA0100 = userView.getA0100();
					String uvNbase = userView.getDbname();
					String uvA0101 = userView.getUserFullName();
					String uvFullName = userView.getUserFullName();
					String uvB0110 = userView.getUserOrgId();
					String uvE0122 = userView.getUserDeptId();
					String uvE01a1 = userView.getUserPosId();
					RecordVo revo = new RecordVo("per_diary_opinion");
					String value2 = "";
					if(leaderCommandsBean.get("value2")!=null){
						value2 = (String)leaderCommandsBean.get("value2");
					}
					int id = getLastEdit(Integer.parseInt(p0100), uvA0100, uvNbase);
					int saveid = id;
					if(id==0){
						saveid = this.getId();
					}
					revo.setInt("id",saveid);
					if(id!=0){
						revo = dao.findByPrimaryKey(revo);
					}
					revo.setString("p0100",p0100);
					revo.setString("b0110", uvB0110);
					revo.setString("e0122", uvE0122);
					revo.setString("e01a1", uvE01a1);
					revo.setString("nbase", uvNbase);
					revo.setString("a0100", uvA0100);
					revo.setString("a0101", uvA0101);
					String relation = "";
					int uvSpgrade = 0;
					if(this.workParametersMap.get("sp_relation")!=null){
						relation = (String)this.workParametersMap.get("sp_relation");
					}
					if(sp_level!=null && "1".equals(sp_level)){
						uvSpgrade = 9;
					}else{
						
						uvSpgrade = Integer.parseInt(!"".equals(this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao,relation ))?this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao,relation ):"0");
					}
					if(uvSpgrade==0){
						String [] strArray = this.getInfo(uvNbase, uvA0100, nbase, a0100, dao, relation);
						if(strArray!=null){
							uvSpgrade = Integer.parseInt(strArray[1]);
						}
					}
					revo.setInt("sp_grade", uvSpgrade);
					revo.setString("description", value2);
					revo.setString("pg_code", recordGradeName);
					revo.setDate("sp_date", new Date());
					if(id!=0){
						dao.updateValueObject(revo);
					}else{
						dao.addValueObject(revo);
					}
				}
			}
			
			// 保存总结条数
			if(log_type==2) {
                vo.setString("p0103",planContent);
            }
			if(addORupdate!=null && "update".equalsIgnoreCase(addORupdate)) {
                dao.updateValueObject(vo);
            } else {
                dao.addValueObject(vo);
            }
			
			// 保存计划条数
			if(log_type==1)
			{
				String[] arr = planContent.split("≡");
				for(int i=0;i<arr.length;i++)
				{
					/*if(arr[i]==null||arr[i].equals(""))
						continue;*/
					sql.setLength(0);
					ArrayList alist = new ArrayList();
					StringBuffer values=new StringBuffer("");
					sql.append("insert into per_diary_content (");
					sql.append("p0100,record_num,a0100");
					values.append("values (?,?,?");
					alist.add(pid+"");
					alist.add((i+1)+"");
					alist.add(this.a0100);
					for(int j=0;j<baseInfo.size();j++)
					{
						LazyDynaBean bean = (LazyDynaBean)baseInfo.get(j);
						String itemid=(String)bean.get("itemid");
						String value=(String)bean.get("value");					
						if("a0000".equalsIgnoreCase(itemid)) // 过滤人员顺序号
                        {
                            continue;
                        }
						
						sql.append(","+itemid);
						alist.add(value);
						values.append(",?");
					}
					sql.append(",content)");
					values.append(",?)");
					alist.add(arr[i]);
					insertList.add(alist);
					sql.append(values.toString());
				}
				dao.batchInsert(sql.toString(), insertList);
			}
			
			// 保存抄送人员
			if(copyToStr!=null&&copyToStr.length()>0)
			{
				dao.delete("delete from per_diary_actor where p0100="+p0100, new ArrayList());
				HashMap map = new HashMap();
				String[] temp=copyToStr.split("`");
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i]==null|| "".equals(temp[i])) {
                        continue;
                    }
					String nbase=temp[i].substring(0,3);
					String a0100=temp[i].substring(3);
					if(map.get(nbase.toUpperCase())!=null)
					{
						String tt=(String)map.get(nbase.toUpperCase());
						tt+=","+a0100;
						map.put(nbase.toUpperCase(),tt);
					}else
					{
						map.put(nbase.toUpperCase(),a0100);
					}
				}
				Set keySet = map.keySet();
				for(Iterator it= keySet.iterator();it.hasNext();)
				{
					String key = (String)it.next();
					String a0100=((String)map.get(key));
					StringBuffer buf = new StringBuffer("");
					buf.append("insert into per_diary_actor (p0100,a0100,nbase,b0110,e0122,e01a1,a0101,state) ");
					buf.append(" (select ");
					buf.append(p0100+",a0100,'"+key+"',b0110,e0122,e01a1,a0101,1 from ");
					buf.append(key+"A01 where a0100 in ");
					buf.append("('"+a0100.replaceAll(",","','")+"'))");
					dao.update(buf.toString());
				}
			}
			
			// 报批
			if("2".equalsIgnoreCase(flag))
			{
				String name = "";
				if(appbody_id!=null && appbody_id.trim().length()>0) {
                    name = getUsername(appbody_id);
                }
				
				if(name!=null && name.trim().length()>0)
				{
					
				    dao.update("update p01 set p0115='02',curr_user = '"+name+"' where p0100="+p0100);
				}
				else
				{

				    dao.update("update p01 set p0115='02' where p0100="+p0100);
								
				}
				if(userStatus!=null && !"".equalsIgnoreCase(userStatus) && !"0".equalsIgnoreCase(userStatus)){
					//领导登录才可以保存领导批示
					// 领导批示
					if(leaderCommandsBean!=null)
					{
						String uvA0100 = userView.getA0100();
						String uvNbase = userView.getDbname();
						String uvA0101 = userView.getUserFullName();
						String uvFullName = userView.getUserFullName();
						String uvB0110 = userView.getUserOrgId();
						String uvE0122 = userView.getUserDeptId();
						String uvE01a1 = userView.getUserPosId();
						
						
						RecordVo revo = new RecordVo("per_diary_opinion");
						String value2 = "";
						if(leaderCommandsBean.get("value2")!=null){
							value2 = (String)leaderCommandsBean.get("value2");
						}
						int id = getLastEdit(Integer.parseInt(p0100), uvA0100, uvNbase);
						int saveid = id;
						if(id==0){
							saveid = this.getId();
						}
						revo.setInt("id",saveid);
						if(id!=0){
							revo = dao.findByPrimaryKey(revo);
						}
						revo.setString("p0100",p0100);
						revo.setString("b0110", uvB0110);
						revo.setString("e0122", uvE0122);
						revo.setString("e01a1", uvE01a1);
						revo.setString("nbase", uvNbase);
						revo.setString("a0100", uvA0100);
						revo.setString("a0101", uvA0101);
						String relation = "";
						int uvSpgrade = 0;
						if(this.workParametersMap.get("sp_relation")!=null){
							relation = (String)this.workParametersMap.get("sp_relation");
						}
						if(sp_level!=null && "1".equals(sp_level)){
							uvSpgrade = 9;
						}else{
							
							uvSpgrade = Integer.parseInt(!"".equals(this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao,relation ))?this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao,relation ):"0");
						}
						if(uvSpgrade==0){
							String [] strArray = this.getInfo(uvNbase, uvA0100, nbase, a0100, dao, relation);
							if(strArray!=null){
								uvSpgrade = Integer.parseInt(strArray[1]);
							}
						}
						revo.setInt("sp_grade", uvSpgrade);
						revo.setString("description", value2);
						revo.setString("pg_code", recordGradeName);
						revo.setDate("sp_date", new Date());
						if(id!=0){
						   dao.updateValueObject(revo);
						}else{
						   dao.addValueObject(revo);	
						}			
					}
				}
			}			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pid;
	}
	public void sendPending(String p0100,String flag,ContentDAO dao) throws GeneralException, SQLException{
		WorkPlanTeamBo wptb = new WorkPlanTeamBo(this.userView,this.con);
	    StringBuffer str=new StringBuffer();
	    RecordVo cordvo = new RecordVo("P01");
	    cordvo.setInt("p0100", Integer.parseInt(p0100));
	    cordvo = dao.findByPrimaryKey(cordvo);
	    str.append(ResourceFactory.getProperty("work.diary.emial.title")+" "+cordvo.getString("a0101"));
	    Date P0104_D=cordvo.getDate("p0104");
		Date P0106_D=cordvo.getDate("p0106");
		String log_type1 = cordvo.getString("log_type");
		String p0115 = cordvo.getString("p0115");
		String mdopt = "";
		if("1".equals(flag)){
			//审阅
			mdopt = SafeCode.encode(PubFunc.convertTo64Base("0"));
		}else{
			//审批
			mdopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
		}
		String mdnbase= SafeCode.encode(PubFunc.convertTo64Base(cordvo.getString("nbase")));
	    String mda0100= SafeCode.encode(PubFunc.convertTo64Base(cordvo.getString("a0100")));
	    String mdp0100= SafeCode.encode(PubFunc.convertTo64Base(cordvo.getString("p0100")));
	    str.append(" ");
		String[] strArray = this.getTimeDescAndUrl(wptb,P0104_D,log_type1,Integer.parseInt(state),mdopt,mdnbase,mda0100,mdp0100,p0115).split("seprator");
		str.append(strArray[0]);
		String url = strArray[1];
		if(log_type1!=null && "1".equals(log_type1)){
			str.append(" 的工作计划");
		}
		if(log_type1!=null && "2".equals(log_type1)){
			str.append(" 的工作总结");
		}
		PendingTask imip=new PendingTask();
		/**
	     * 向待办库中加入新的待办
	     * @param pendingCode 待办编号
	     * @param appType 申请待办的类型
	     * @param pendingTitle  待办标题
	     * @param senderMessage 待办信息发送人usercode（发送人用户登录名）Usr000001     
	     * @param receiverMessage 待办信息接收人usercode（接收人用户登录名）Usr000001
	     * @param pendingURL  待办链接地址
	     * @param pendingStatus  待办状态（0：待办， 1：已办，2：待阅，3：已阅）
	     * @param pendingLevel  待办级别（0：非重要，1：重要）
	     * @param pendingType  待办所在应用中的类别（不同类型的绩效考核的中文名称，如个人考核，团队考核）
	     * @return
	     */
		String pendingCode = this.getPendingCode(this.userView.getA0100(), this.userView.getDbname());
		url+="&pdCode="+pendingCode;
		//System.out.println(url);
		//System.out.println(str.toString());
		imip.insertPending(pendingCode,"W",str.toString(),this.userView.getDbname()+this.userView.getA0100(),this.userView.getDbname()+this.userView.getA0100(),
				url,0,0,"",this.userView);
	}
	//发送驳回待办
	public void sendRejectPending(String p0100,String flag,ContentDAO dao) throws GeneralException, SQLException{
		WorkPlanTeamBo wptb = new WorkPlanTeamBo(this.userView,this.con);
	    StringBuffer str=new StringBuffer();
	    RecordVo cordvo = new RecordVo("P01");
	    cordvo.setInt("p0100", Integer.parseInt(p0100));
	    cordvo = dao.findByPrimaryKey(cordvo);
	    Date P0104_D=cordvo.getDate("p0104");
		Date P0106_D=cordvo.getDate("p0106");
		String log_type1 = cordvo.getString("log_type");
		String p0115 = cordvo.getString("p0115");
		String mdopt = "";
			mdopt = SafeCode.encode(PubFunc.convertTo64Base("1"));
		String mdnbase= SafeCode.encode(PubFunc.convertTo64Base(cordvo.getString("nbase")));
	    String mda0100= SafeCode.encode(PubFunc.convertTo64Base(cordvo.getString("a0100")));
	    String mdp0100= SafeCode.encode(PubFunc.convertTo64Base(cordvo.getString("p0100")));
		String[] strArray = this.getRejectTimeDescAndUrl(wptb,P0104_D,log_type1,Integer.parseInt(state),mdopt,mdnbase,mda0100,mdp0100,p0115).split("seprator");
		str.append(strArray[0]);
		String url = strArray[1];
		
		PendingTask imip=new PendingTask();
		/**
	     * 向待办库中加入新的待办
	     * @param pendingCode 待办编号
	     * @param appType 申请待办的类型
	     * @param pendingTitle  待办标题
	     * @param senderMessage 待办信息发送人usercode（发送人用户登录名）Usr000001     
	     * @param receiverMessage 待办信息接收人usercode（接收人用户登录名）Usr000001
	     * @param pendingURL  待办链接地址
	     * @param pendingStatus  待办状态（0：待办， 1：已办，2：待阅，3：已阅）
	     * @param pendingLevel  待办级别（0：非重要，1：重要）
	     * @param pendingType  待办所在应用中的类别（不同类型的绩效考核的中文名称，如个人考核，团队考核）
	     * @return
	     */
		String pendingCode = this.getPendingCode(this.userView.getA0100(), this.userView.getDbname());
		url+="&pdCode="+pendingCode;
		//System.out.println(url);
		//System.out.println(str.toString());
		imip.insertPending(pendingCode,"W",str.toString(),this.userView.getDbname()+this.userView.getA0100(),this.userView.getDbname()+this.userView.getA0100(),
				url,0,0,"",this.userView);
	}
	private String getPendingCode(String a0100,String nbase)
	{
		Date d=new Date();
		return  "HRMS-"+nbase+a0100+"-"+d.getTime()+Math.round(Math.ceil(Math.random()*10));
	}
	public String getTimeDescAndUrl(WorkPlanTeamBo wptb,Date P0104_D,String log_type,int state,String mdopt,String mdnbase,String mda0100,String mdp0100,String p0115){
		String returnUrl="/templates/index/portal.do?b_query=link";
    	String target = "_self";
		StringBuffer buf = new StringBuffer();
		int year_num = DateUtils.getYear(P0104_D);
		int month_num =DateUtils.getMonth(P0104_D);
		int quarter_num = Integer.parseInt(this.getSeason(String.valueOf(month_num)));
		//System.out.println("month_num"+month_num);
		//System.out.println("quarter_num"+quarter_num);
		int week_num =0;
		int day_num =DateUtils.getDay(P0104_D);
		if(state==4){
			buf.append(year_num+"年");
		}
		if(state==3){
			buf.append(year_num+"年第"+quarter_num+"季度");
		}
		if(state==2){
			buf.append(year_num+"年"+month_num+"月");
		}
		if(state==1){
			LinkedHashMap weekMap = wptb.getWeekIndex(String.valueOf(year_num),"all","0");
				Set keySet=weekMap.keySet();
				  java.util.Iterator t=keySet.iterator();
		
					while(t.hasNext())
					{
						String strKey = (String)t.next();  //键值	    
						String strValue = (String)weekMap.get(strKey);   //value值  
						String strDate = strKey.replace("-",".");
						if(strDate.equals(DateUtils.format(P0104_D,"yyyy.MM.dd"))){
							//2015/12/22 wangjl 【|】做分隔符，split需要加上两个斜杠【\\】
							String[] strArray = strValue.split("\\|");
							//2015/12/22 wangjl 数组越界异常
							month_num = Integer.parseInt(strArray[0]);
							week_num = Integer.parseInt(strArray[1]);
							break;
						}
					}
			buf.append(year_num+"年"+month_num+"月"+week_num+"周");
		}
		if(state==0){
			buf.append(year_num+"年"+month_num+"月"+day_num+"日");
		}
		buf.append("seprator");
		 
		buf.append("/performance/workplan/workplanview/workplan_view_list.do?b_write=write&home=5&mdopt="+mdopt+"&mdnbase="+mdnbase+"&mda0100="+mda0100+"&log_type="+log_type+"&mdp0100="+mdp0100+"&state="+state+"&p0115="+p0115+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnUrl+"&target="+target);
        
		return buf.toString();
	}
	public String getRejectTimeDescAndUrl(WorkPlanTeamBo wptb,Date P0104_D,String log_type,int state,String mdopt,String mdnbase,String mda0100,String mdp0100,String p0115){
		String returnUrl="/templates/index/portal.do?b_query=link";
		String target = "_self";
		StringBuffer buf = new StringBuffer();
		int year_num = DateUtils.getYear(P0104_D);
		int month_num =DateUtils.getMonth(P0104_D);
		int quarter_num = Integer.parseInt(this.getSeason(String.valueOf(month_num)));
		//System.out.println("month_num"+month_num);
		//System.out.println("quarter_num"+quarter_num);
		int week_num =0;
		int day_num =DateUtils.getDay(P0104_D);
		if(state==4){
			buf.append(year_num+"年");
		}
		if(state==3){
			buf.append(year_num+"年第"+quarter_num+"季度");
		}
		if(state==2){
			buf.append(year_num+"年"+month_num+"月");
		}
		if(state==1){
			LinkedHashMap weekMap = wptb.getWeekIndex(String.valueOf(year_num),"all","0");
			Set keySet=weekMap.keySet();
			java.util.Iterator t=keySet.iterator();
			
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值	    
				String strValue = (String)weekMap.get(strKey);   //value值  
				String strDate = strKey.replace("-",".");
				if(strDate.equals(DateUtils.format(P0104_D,"yyyy.MM.dd"))){
					//2015/12/22 wangjl 【|】做分隔符，split需要加上两个斜杠【\\】
					String[] strArray = strValue.split("\\|");
					//2015/12/22 wangjl 数组越界异常
					month_num = Integer.parseInt(strArray[0]);
					week_num = Integer.parseInt(strArray[1]);
					break;
				}
			}
			buf.append(year_num+"年"+month_num+"月"+week_num+"周");
		}
		if(state==0){
			buf.append(year_num+"年"+month_num+"月"+day_num+"日");
		}
		if(log_type!=null && "1".equals(log_type)){
			buf.append("工作计划  "+"驳回");
		}
		if(log_type!=null && "2".equals(log_type)){
			buf.append("工作总结  "+"驳回");
		}
		buf.append("seprator");
		
		buf.append("/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+mdopt+"&mdnbase="+mdnbase+"&mda0100="+mda0100+"&log_type="+log_type+"&mdp0100="+mdp0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnUrl+"&target="+target);
		return buf.toString();
	}
	/**
	 * Description:  本月是第几季度
	 * @Version1.0 
	 * Aug 3, 2012 3:20:14 PM Jianghe created
	 * @param month
	 * @return
	 */
	public String getSeason(String month){
		
        int ynum=Integer.parseInt(month);
		String returnValue="";
		if(ynum==1||ynum==2||ynum==3){
			returnValue = "1";
		}
		if(ynum==4||ynum==5||ynum==6){
			returnValue = "2";
		}
		if(ynum==7||ynum==8||ynum==9){
			returnValue = "3";
		}
		if(ynum==10||ynum==11||ynum==12){
			returnValue = "4";
		}
	    return returnValue;
	}
	/**
	 * 取得登录用户名
	 * @param dao
	 * @param a0100
	 * @param usernamefield
	 * @return
	 */
	public String getUsername(String a0100)
	{
		RecordVo user_vo = new RecordVo(""+(a0100.substring(0,3)).toUpperCase()+"A01");
		user_vo.setString("a0100",a0100.substring(3));
		String name = "";
		try 
		{
			ContentDAO dao = new ContentDAO(this.con);
			
			String usernamefield = ConstantParamter.getLoginUserNameField().toLowerCase();
			
			if(dao.isExistRecordVo(user_vo))
			{
				user_vo=dao.findByPrimaryKey(user_vo);
				if(user_vo!=null)
				{
					name = user_vo.getString(usernamefield);					
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * 根据当前填写的计划记录，求得该计划的起始时间和结束时间
	 * @param year_num
	 * @param quarter_num
	 * @param month_num
	 * @param week_num
	 * @param day_num
	 * @param type
	 * @return
	 */
	public Date getStartDateAndEndDate(String year_num,String quarter_num,String month_num,String week_num,String day_num,int type)
	{
		Date date = null;
		if(type==1)//求起始时间
		{			
			if("0".equals(this.state))//日计划
			{
	        	Calendar start_calendar=Calendar.getInstance();
	        	start_calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        	start_calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
	        	start_calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(day_num));
	        	start_calendar.set(Calendar.HOUR_OF_DAY,0);
	        	start_calendar.set(Calendar.MINUTE,0);
	        	start_calendar.set(Calendar.SECOND,0);
	            date = start_calendar.getTime();
			}
			else if("1".equals(this.state))// 周计划
			{
				WeekUtils weekutils = new WeekUtils();
	        	return weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),1);
			}
			else if("2".equals(this.state))//月计划
			{
				Calendar calendar=Calendar.getInstance();
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
				calendar.set(Calendar.DAY_OF_MONTH,1);
				date = calendar.getTime();
			}
			else if("3".equals(this.state)) // 季计划
			{
				//curr_day为当前记录是第几季度
	        	if(Integer.parseInt(quarter_num)==1)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,0);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==2)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,3);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();        		
	        	}
	        	else if(Integer.parseInt(quarter_num)==3)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,6);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==4)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,9);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();
	        	}
				
			}
			else if("4".equals(this.state)) // 年计划
			{
				Calendar calendar = Calendar.getInstance();	    		
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,0);
				calendar.set(Calendar.DAY_OF_MONTH,1);
	    		date = calendar.getTime();				
			}
			
		}else // 结束时间
		{			
			if("0".equals(this.state)) // 日计划
			{
				Calendar start_calendar=Calendar.getInstance();
	        	start_calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        	start_calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
	        	start_calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(day_num));
	        	start_calendar.set(Calendar.HOUR_OF_DAY,0);
	        	start_calendar.set(Calendar.MINUTE,0);
	        	start_calendar.set(Calendar.SECOND,0);
	            date=start_calendar.getTime();
			}
			else if("1".equals(this.state)) // 周计划
			{
				WeekUtils weekutils = new WeekUtils();
	        	return weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),7);
			}
			else if("2".equals(this.state)) // 月计划
			{
				Calendar calendar=Calendar.getInstance();
				calendar.clear();
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
				calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				date=calendar.getTime();
			}
			else if("3".equals(this.state)) // 季计划
			{
				// curr_day为当前记录是第几季度
	        	if(Integer.parseInt(quarter_num)==1)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,2);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==2)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,5);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();        		
	        	}
	        	else if(Integer.parseInt(quarter_num)==3)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,8);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==4)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,11);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();
	        	}				
			}
			else if("4".equals(this.state)) // 年计划
			{
				Calendar calendar = Calendar.getInstance();	    		
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,11);
				calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	
	    		date = calendar.getTime();				
			}
		}
		return date;
	}
	/**
	 * 取得人的基本信息
	 * @param p0100 计划号
	 * @param type =1展现页面时，=2增加记录
	 * @return
	 */
	public ArrayList getBaseInfo(String p0100,int type,String year,String quarter,String month,String week,String day)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;//“姓名”、“单位”、“部门”、“岗位”、“开始时间”、“结束时间”，
		try
		{			
			ContentDAO dao = new ContentDAO(this.con);
			ArrayList vFieldList=new ArrayList();
			FieldItem item = DataDictionary.getFieldItem("a0101","p01");
			if("1".equals(item.getState())) {
                vFieldList.add(item);
            }
			
			FieldItem item2 = DataDictionary.getFieldItem("b0110","p01");
			if("1".equals(item2.getState())) {
                vFieldList.add(item2);
            }
			
			FieldItem item3 = DataDictionary.getFieldItem("e0122","p01");
			if("1".equals(item3.getState())) {
                vFieldList.add(item3);
            }
			
			FieldItem item4 = DataDictionary.getFieldItem("e01a1","p01");
			if("1".equals(item4.getState())) {
                vFieldList.add(item4);
            }
			
			FieldItem item5 = DataDictionary.getFieldItem("p0104","p01");
			if("1".equals(item5.getState())) {
                vFieldList.add(item5);
            }
			
			FieldItem item6 = DataDictionary.getFieldItem("p0106","p01");
			if("1".equals(item6.getState())) {
                vFieldList.add(item6);
            }
		    
			
		    if(type==2 || ("".equals(p0100) && type==1 && vFieldList.size()>0))
		    {
			   String sql = "select a0101,b0110,e0122,e01a1,a0000 from "+this.nbase+"A01 where a0100='"+this.a0100+"'";
			   rs = dao.search(sql);
			   while(rs.next())
			   {
				   LazyDynaBean bean1 = new LazyDynaBean();
				   bean1.set("itemid","a0101");
				   bean1.set("value",isNull(rs.getString("a0101")));
				   bean1.set("codesetid","0");
				   bean1.set("valuedesc", isNull(rs.getString("a0101")));
				   bean1.set("itemtype","A");
				   bean1.set("itemdesc",isNull(item.getItemdesc()));
				   if((type==1 && "1".equals(item.getState())) || type==2) {
                       list.add(bean1);
                   }
				  
				   bean1 = new LazyDynaBean();
				   bean1.set("itemid","b0110");
				   bean1.set("value",isNull(rs.getString("b0110")));
				   bean1.set("codesetid","UN");
				   bean1.set("valuedesc", isNull(AdminCode.getCodeName("UN",rs.getString("b0110"))));
				   bean1.set("itemtype","A");
				   bean1.set("itemdesc",isNull(item2.getItemdesc()));
				   if((type==1 && "1".equals(item2.getState())) || type==2) {
                       list.add(bean1);
                   }
				   
				   bean1 = new LazyDynaBean();
				   bean1.set("itemid","e0122");
				   bean1.set("value",isNull(rs.getString("e0122")));
				   bean1.set("codesetid","UM");
				   bean1.set("valuedesc", isNull(AdminCode.getCodeName("UM", rs.getString("e0122"))));
				   bean1.set("itemtype","A");
				   bean1.set("itemdesc",isNull(item3.getItemdesc()));
				   if((type==1 && "1".equals(item3.getState())) || type==2) {
                       list.add(bean1);
                   }
				   
				   bean1 = new LazyDynaBean();
				   bean1.set("itemid","e01a1");
				   bean1.set("value",isNull(rs.getString("e01a1")));
				   bean1.set("codesetid","@K");
				   bean1.set("valuedesc", isNull(AdminCode.getCodeName("@K", rs.getString("e01a1"))));
				   bean1.set("itemtype","A");
				   bean1.set("itemdesc",isNull(item4.getItemdesc()));
				   if((type==1 && "1".equals(item4.getState())) || type==2) {
                       list.add(bean1);
                   }
				   if(type==2)
				   {
					   bean1 = new LazyDynaBean();
					   bean1.set("itemid","nbase");
					   bean1.set("value",this.nbase);
					   bean1.set("codesetid","@@");
					   bean1.set("itemdesc",ResourceFactory.getProperty("workbench.info.import.error.havedb"));
					   bean1.set("valuedesc", AdminCode.getCodeName("@@", this.nbase));
					   bean1.set("itemtype","A");
				       list.add(bean1);
				       
				       bean1 = new LazyDynaBean();
					   bean1.set("itemid","a0000");
					   bean1.set("value",isNull(rs.getString("a0000")));
				  //   bean1.set("codesetid","@@");
					   bean1.set("itemdesc",ResourceFactory.getProperty("performance.workplan.workplanview.perSerialNo"));
				  //   bean1.set("valuedesc", AdminCode.getCodeName("@@", this.nbase));
				  //   bean1.set("itemtype","A");
				       list.add(bean1);
				   }
				   if(type==1)
				   {
					    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						if("1".equals(item5.getState()))
						{
							 bean1 = new LazyDynaBean();
							 bean1.set("itemid","p0104");
							 bean1.set("value","");
							 bean1.set("codesetid","0");
							 bean1.set("itemdesc",isNull(item5.getItemdesc()));
							 bean1.set("valuedesc", format.format(this.getStartDateAndEndDate(year, quarter, month, week, day, 1)));
							 bean1.set("itemtype","D");
						     list.add(bean1);
						}
						if("1".equals(item6.getState()))
						{
							 bean1 = new LazyDynaBean();
							 bean1.set("itemid","p0106");
							 bean1.set("value","");
							 bean1.set("codesetid","0");
							 bean1.set("itemdesc",isNull(item6.getItemdesc()));
							 bean1.set("valuedesc", format.format(this.getStartDateAndEndDate(year, quarter, month, week, day, 2)));
							 bean1.set("itemtype","D");
						     list.add(bean1);
						}
				   	}
			   	}
		    }
		    else if(vFieldList.size()>0)
		    {
		    	StringBuffer sql = new StringBuffer("");
		    	if("".equals(p0100))
		    	{
		    		sql.append(" select a0100");
		    		for(int i=0;i<vFieldList.size();i++)
		    		{
		    			if("p0104".equalsIgnoreCase(((FieldItem)vFieldList.get(i)).getItemid())|| "p0106".equalsIgnoreCase(((FieldItem)vFieldList.get(i)).getItemid())) {
                            sql.append(", null as "+((FieldItem)vFieldList.get(i)).getItemid());
                        } else {
                            sql.append(","+((FieldItem)vFieldList.get(i)).getItemid());
                        }
		    		}
		    		sql.append(" from "+this.nbase+"A01 where a0100='"+this.a0100+"'");
		    		
		    	}else
		    	{
		    		sql.append(" select a0100");
		    		for(int i=0;i<vFieldList.size();i++)
		    		{
		    			sql.append(","+((FieldItem)vFieldList.get(i)).getItemid());
		    		}
		    		sql.append(" from p01 where p0100='"+p0100+"'");
		    	}
		    	rs = dao.search(sql.toString());
		    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		    	while(rs.next())
		    	{
		    		for(int i=0;i<vFieldList.size();i++)
		    		{
		    			FieldItem fielditem = (FieldItem)vFieldList.get(i);
		    			LazyDynaBean bean1 = new LazyDynaBean();
		    			bean1.set("itemid",fielditem.getItemid());
		    			if("D".equalsIgnoreCase(fielditem.getItemtype())&&rs.getDate(fielditem.getItemid())!=null)
		    			{
		    				bean1.set("value",format.format(rs.getDate(fielditem.getItemid())));
		    			}else if(rs.getString(fielditem.getItemid())==null) {
                            bean1.set("value", "");
                        } else {
                            bean1.set("value",rs.getString(fielditem.getItemid())==null?"":rs.getString(fielditem.getItemid()));
                        }
		    			bean1.set("codesetid",fielditem.getCodesetid());
		    			if(fielditem.isCode()) {
                            bean1.set("valuedesc", AdminCode.getCodeName(fielditem.getCodesetid(), rs.getString(fielditem.getItemid())));
                        } else if("D".equals(fielditem.getItemtype()))
		    			{
		    				if(rs.getDate(fielditem.getItemid())!=null) {
                                bean1.set("valuedesc",format.format(rs.getDate(fielditem.getItemid())));
                            } else {
                                bean1.set("valuedesc", "");
                            }
		    			}else {
                            bean1.set("valuedesc", rs.getString(fielditem.getItemid())==null?"":rs.getString(fielditem.getItemid()));
                        }
		    			
		    			bean1.set("itemtype",fielditem.getItemtype());
		    			bean1.set("itemdesc",fielditem.getItemdesc());
		    			list.add(bean1);
		    		}
		    	}
		    }		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 取得p01表中，可编辑的指标
	 * @param p0100
	 * @return
	 */
	public ArrayList getEditFieldList(String p0100,String log_type)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			String planTarget = (String)WorkPlanViewBo.workParametersMap.get("planTarget"); // 工作计划定义指标
			String summTarget = (String)WorkPlanViewBo.workParametersMap.get("summTarget"); // 工作总结定义指标
			planTarget = ","+ planTarget +","; 
			summTarget = ","+ summTarget +",";
						
			ArrayList fieldItemList = DataDictionary.getFieldList("p01", Constant.USED_FIELD_SET);
			String noEditStr=",P0100,B0110,E0122,E01A1,A0100,NBASE,A0101,P0101,P0103,P0104,P0106,P0107,P0108,P0115,P0114,P0117,P0116,P0113,";
			
			LazyDynaBean valueBean = new LazyDynaBean();
			if(!"".equals(p0100))
			{
				StringBuffer buf = new StringBuffer();
				buf.append(" select p0100");
				int j=0;
				for(int i=0;i<fieldItemList.size();i++)
				{
					FieldItem fielditem = (FieldItem)fieldItemList.get(i);
					if(noEditStr.indexOf((","+fielditem.getItemid().toUpperCase()+","))!=-1) {
                        continue;
                    }
					if("0".equals(fielditem.getState())) {
                        continue;
                    }
					if("1".equalsIgnoreCase(log_type) && planTarget.indexOf((","+fielditem.getItemid().toUpperCase()+","))==-1) {
                        continue;
                    }
					if("2".equalsIgnoreCase(log_type) && summTarget.indexOf((","+fielditem.getItemid().toUpperCase()+","))==-1) {
                        continue;
                    }
					
					j++;
					buf.append(","+fielditem.getItemid());
				}
				buf.append(" from p01 where p0100="+p0100);
				if(j>0)
				{
					ContentDAO dao = new ContentDAO(this.con);
					rs=dao.search(buf.toString());
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					while(rs.next())
					{
						for(int i=0;i<fieldItemList.size();i++)
						{
							FieldItem fielditem = (FieldItem)fieldItemList.get(i);
							if(noEditStr.indexOf((","+fielditem.getItemid().toUpperCase()+","))!=-1) {
                                continue;
                            }
							if("0".equals(fielditem.getState())) {
                                continue;
                            }
							if("1".equalsIgnoreCase(log_type) && planTarget.indexOf((","+fielditem.getItemid().toUpperCase()+","))==-1) {
                                continue;
                            }
							if("2".equalsIgnoreCase(log_type) && summTarget.indexOf((","+fielditem.getItemid().toUpperCase()+","))==-1) {
                                continue;
                            }
							
							if("A".equalsIgnoreCase(fielditem.getItemtype()))
							{
								if(fielditem.isCode())
								{
									if(rs.getString(fielditem.getItemid())!=null)
									{
										valueBean.set(fielditem.getItemid().toUpperCase(),rs.getString(fielditem.getItemid()));
										valueBean.set(fielditem.getItemid()+"VIEWVALUE",AdminCode.getCodeName(fielditem.getCodesetid(),rs.getString(fielditem.getItemid())));
									}
									else {
										valueBean.set(fielditem.getItemid().toUpperCase(),"");
										valueBean.set(fielditem.getItemid()+"VIEWVALUE","");
									}
								}else{
									valueBean.set(fielditem.getItemid().toUpperCase(),rs.getString(fielditem.getItemid())==null?"":rs.getString(fielditem.getItemid()));
									valueBean.set(fielditem.getItemid()+"VIEWVALUE","");

								}
							}else if("D".equalsIgnoreCase(fielditem.getItemtype()))
							{
								if(rs.getDate(fielditem.getItemid())!=null) {
                                    valueBean.set(fielditem.getItemid().toUpperCase(), format.format(rs.getDate(fielditem.getItemid())));
                                } else {
                                    valueBean.set(fielditem.getItemid().toUpperCase(), "");
                                }
								valueBean.set(fielditem.getItemid()+"VIEWVALUE","");
							}else if("N".equalsIgnoreCase(fielditem.getItemtype()))
							{
								if(rs.getString(fielditem.getItemid())!=null) {
                                    valueBean.set(fielditem.getItemid().toUpperCase(), PubFunc.round(rs.getString(fielditem.getItemid()), fielditem.getDecimalwidth()));
                                } else {
                                    valueBean.set(fielditem.getItemid().toUpperCase(), "");
                                }
								valueBean.set(fielditem.getItemid()+"VIEWVALUE","");
							}else{
								valueBean.set(fielditem.getItemid().toUpperCase(), Sql_switcher.readMemo(rs,fielditem.getItemid()));
								valueBean.set(fielditem.getItemid()+"VIEWVALUE","");
							}
						}
					}
					
				}
				
			}
			for(int i=0;i<fieldItemList.size();i++)
			{
				FieldItem fielditem = (FieldItem)fieldItemList.get(i);
				if(noEditStr.indexOf((","+fielditem.getItemid().toUpperCase()+","))!=-1) {
                    continue;
                }
				if("0".equals(fielditem.getState())) {
                    continue;
                }
				if("1".equalsIgnoreCase(log_type) && planTarget.indexOf((","+fielditem.getItemid().toUpperCase()+","))==-1) {
                    continue;
                }
				if("2".equalsIgnoreCase(log_type) && summTarget.indexOf((","+fielditem.getItemid().toUpperCase()+","))==-1) {
                    continue;
                }
				
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid",fielditem.getItemid());
				bean.set("itemtype", fielditem.getItemtype());
				bean.set("codesetid",fielditem.getCodesetid());
				bean.set("itemlength",fielditem.getItemlength()+"");
				bean.set("itemdesc",fielditem.getItemdesc());
				bean.set("deciwidth", fielditem.getDecimalwidth()+"");
				bean.set("mustwrite", String.valueOf(fielditem.isFillable()).toLowerCase());
			//	boolean tt = fielditem.isFillable();  // 是否必填
				if(!"".equals(p0100))
				{
					if(valueBean.get(fielditem.getItemid().toUpperCase())!=null){
						bean.set("value",valueBean.get(fielditem.getItemid().toUpperCase()));
						bean.set("viewvalue",valueBean.get(fielditem.getItemid()+"VIEWVALUE"));
					}else
					{
						bean.set("value","");
						bean.set("viewvalue","");
					}
				}else{
					bean.set("value","");
					bean.set("viewvalue","");
				}
				list.add(bean);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 保存附件
	 * @param form_file
	 * @param p0100
	 * @param fileName
	 */
	public void saveAttach(FormFile form_file,int p0100,String fileName)
	{
		try{
			String file_max_size="512";
			if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").length()>0)
			{
				file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
				if(file_max_size.toLowerCase().indexOf("k")!=-1) {
                    file_max_size=file_max_size.substring(0,file_max_size.length()-1);
                }
			}
			if (form_file != null && form_file.getFileData().length > 0&& form_file.getFileData().length < Integer.parseInt(file_max_size)*1024) {
				String fname = form_file.getFileName();
				int indexInt = fname.lastIndexOf(".");
				String ext = fname.substring(indexInt + 1, fname.length());
				if(fileName==null|| "".equals(fileName))
				{
					fileName=fname.substring(0,indexInt);
				}
				int file_id=DbNameBo.getPrimaryKey("per_diary_file", "file_id", con);
                ContentDAO dao = new ContentDAO(con);
                dao.update("insert into per_diary_file(file_id) values ("+file_id+")") ;
				String sql = "update per_diary_file set name=?,ext=?,content=?,p0100=? where file_id=?";
				ArrayList list = new ArrayList();
				list.add(fileName);
				// blob字段保存,数据库中差异
				switch (Sql_switcher.searchDbServer()) {
				case Constant.ORACEL:
					Blob blob = PubFunc.getOracleBlob(form_file, "per_diary_file", "content", "file_id", file_id, con);
					list.add(ext);
					list.add(blob);
					list.add(p0100);
					list.add(file_id);
					break;
				default:
					byte[] data = form_file.getFileData();
					// a_vo.setObject("affix",data);
					list.add(ext);
					list.add(data);
					list.add(p0100);
					list.add(file_id);
					break;
				}
				dao.update(sql, list);
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除附件
	 * @param p0100
	 * @param file_id
	 */
	public void deleteAttach(int p0100,int file_id)
	{
		try{
			ContentDAO dao = new ContentDAO(this.con);
			dao.delete("delete from per_diary_file where file_id="+file_id+" and p0100="+p0100,new ArrayList());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getHelpScript() {
		return helpScript;
	}
	public void setHelpScript(String helpScript) {
		this.helpScript = helpScript;
	}
	/**
	 * Description: 获取领导评级
	 * @Version1.0 
	 * Sep 13, 2012 9:17:44 AM Jianghe created
	 * @param p0100
	 * @param opt
	 * @param sp_level
	 * @return
	 */
	public String getRecordGrade(String p0100,String opt,String sp_level){
		RowSet rs = null;
		String value = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			if(sp_level!=null && !"".equals(sp_level) && "2".equals(sp_level)){
				rs = dao.search("select pg_code from per_diary_opinion where p0100="+p0100+" and a0100="+"'"+this.userView.getA0100()+"'");
				if(rs.next()){
					value=rs.getString("pg_code");
				}
			}else{
				rs = dao.search("select pg_code from per_diary_opinion where p0100="+p0100);
				if(rs.next()){
					value=rs.getString("pg_code");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return value;
	}
	/**
	 * 取得领导批示，以后可能会支持多级报批，用list吧(领导填写批示时，也使用这个)
	 * @param p0100
	 * @return
	 */
	public LazyDynaBean getLeaderCommands(String p0100,String opt)
	{
		
		LazyDynaBean abean = new LazyDynaBean();
		String value1 = ""; //所有意见
		String value2 = ""; //自己意见
		abean.set("value1",getCommands("1", opt,p0100));
		if(getCommands("2", opt,p0100)!=null&&!"".equals(getCommands("2", opt,p0100))){
			abean.set("value2",getCommands("2", opt,p0100));
		}else{
			abean.set("value2",ResourceFactory.getProperty("label.agree"));
		}
		abean.set("itemdesc",ResourceFactory.getProperty("workdiary.message.leader.ship"));	
		
		return abean;
	}
	/**
	 * 将选择的抄送人员编号转换成名字
	 * @param personstr
	 * @return
	 */
	public String changeA0100ToName(String personstr)
	{
		String str="";
		RowSet rs = null;
		try{
			StringBuffer buf = new StringBuffer("");
			if(personstr==null|| "".equals(personstr)) {
                return "";
            }
			HashMap map = new HashMap();
			String[] temp=personstr.split("`");
			for(int i=0;i<temp.length;i++)
			{
				if(temp[i]==null|| "".equals(temp[i])) {
                    continue;
                }
				String nbase=temp[i].substring(0,3);
				String a0100=temp[i].substring(3);
				if(map.get(nbase.toUpperCase())!=null)
				{
					String tt=(String)map.get(nbase.toUpperCase());
					tt+=","+a0100;
					map.put(nbase.toUpperCase(),tt);
				}else{
					map.put(nbase.toUpperCase(),a0100);
				}
			}
			Set keySet = map.keySet();
			for(Iterator it= keySet.iterator();it.hasNext();)
			{
				String key = (String)it.next();
				String a0100=((String)map.get(key));
				String sql = " select a0101 from "+key+"A01 where a0100 in ('"+a0100.replaceAll(",", "','")+"')";
				ContentDAO dao = new ContentDAO(this.con);
				rs=dao.search(sql);
				while(rs.next())
				{
					buf.append(","+rs.getString("a0101"));
				}
			}
			if(buf.toString().length()>0) {
                str=buf.toString().substring(1);
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return str;
	}
	/**
	 * 取得抄送人员编号和名字
	 * @param p0100
	 * @return
	 */
	public HashMap getCopyToStr(String p0100)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try{
			if(p0100==null|| "".equals(p0100)) {
                return map;
            }
			String sql = "select a0100,nbase,A0101 from per_diary_actor where p0100='"+p0100+"'";
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(sql);
			StringBuffer id=new StringBuffer("");
			StringBuffer name= new StringBuffer("");
			int i=0;
			while(rs.next())
			{
			    if(i!=0)
			    {
			    	id.append("`");
			    	name.append(",");
			    }
			    id.append(rs.getString("nbase")+rs.getString("a0100"));
			    name.append(rs.getString("a0101"));
				i++;
			}
			map.put("id",id.toString());
			map.put("name", name.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 取得附件列表
	 * @param p0100
	 * @return
	 */
	public ArrayList getAttachFileList(String p0100)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try{
			if(p0100==null|| "".equals(p0100)) {
                return list;
            }
			String sql = "select file_id,name,p0100 from per_diary_file where p0100='"+p0100+"'";
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name",rs.getString("name"));
				bean.set("file_id",rs.getString("file_id"));
				bean.set("p0100",rs.getString("p0100"));
				list.add(bean);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	/**
     * 取得人员登记表列表
     * 
     * @param planid
     * @return
     */
	public RecordVo getPerRnameVo(String tabid)
	{
	
		RecordVo vo = new RecordVo("rname");
		try
		{
		    ContentDAO dao = new ContentDAO(this.con);
		    vo.setInt("tabid", Integer.parseInt(tabid));
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
	}
	/**
     * 取得人员信息 
     * @param planid
     * @return
     */
	public RecordVo getPersonVo(String nbase,String a0100)
	{
	
		RecordVo vo = new RecordVo(nbase+"A01");
		try
		{
		    ContentDAO dao = new ContentDAO(this.con);
		    vo.setString("a0100", a0100);
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 获得同周期的工作计划的p0100
	 * @return
	 */
	public String getSummaryPlanStr(String year_num,String quarter_num,String month_num,String week_num,String day_num)
	{
		String p0100 = "";		
		RowSet rs = null;
		try
		{									
		    ContentDAO dao = new ContentDAO(this.con);
		    StringBuffer sql = new StringBuffer("");
		    sql.append("select p0100 from p01 where state="+ this.state +" ");
		    
		    if("4".equalsIgnoreCase(this.state)) // 年
    		{
		    	sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" ");		    	
    		}
    		else if("3".equalsIgnoreCase(this.state)) // 季
    		{
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" and "+Sql_switcher.quarter("p0104")+"="+ quarter_num +" ");   			
    		}
    		else if("2".equalsIgnoreCase(this.state)) // 月
    		{
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" and "+Sql_switcher.month("p0104")+"="+ month_num +" ");
    		}
    		else if("1".equalsIgnoreCase(this.state)) // 周
    		{
    			WeekUtils weekutils = new WeekUtils();				
				String startime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),1)); // 某年某月的第week_num周星期1的日期(返回字符串格式日期)
				String endtime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),7)); // 某年某月的第week_num周星期日的日期(返回字符串格式日期)
   			   			   			   			
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+year_num);
    		//	sql.append(" and "+Sql_switcher.month("p0104")+"="+month_num);				
    			sql.append(" and p0104 between "+Sql_switcher.dateValue(startime)+ " and " +Sql_switcher.dateValue(endtime));
    		}
    		else if("0".equalsIgnoreCase(this.state)) // 日
    		{
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" and "+Sql_switcher.month("p0104")+"="+ month_num +" and "+Sql_switcher.day("p0104")+"="+ day_num +" ");
    		}
		    sql.append(" and UPPER(nbase)='"+ this.nbase.toUpperCase() +"' ");
	    	sql.append(" and a0100='"+ this.a0100 +"' ");
	    	sql.append(" and log_type = '1' and p0115 = '03' ");
	    	sql.append(" order by p0104 ");
		    	    	
		    rs = dao.search(sql.toString());
		    int num = 1;
		    while(rs.next())
		    {
		    //	if(state.equalsIgnoreCase("1") && String.valueOf(num).equalsIgnoreCase(week_num))		    		
		   // 		p0100 = rs.getString("p0100");
		    //	else
		    		p0100 = rs.getString("p0100");	
		    	num++;
		    }		    			    										
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		return p0100;
	}
	
	/**
	 * 获得同周期的工作计划的内容
	 * @return
	 */
	public String getPlanSumStr(String planP0100)
	{	
		StringBuffer str = new StringBuffer("");
		RowSet rs = null;
		try
		{									
		    ContentDAO dao = new ContentDAO(this.con);		    		    		    	    	
		    rs = dao.search("select Content,Record_num from per_diary_content where p0100='"+planP0100+"' order by Record_num");	    	
	    	int num = 1;
	    	while(rs.next())
	    	{	    		
	    		str.append("\r\n"+ResourceFactory.getProperty("performance.workplan.workplanview.di")+num+ResourceFactory.getProperty("performance.workplan.workplanview.tiao")+Sql_switcher.readMemo(rs, "content"));
	    		num++;
	    	}		    			    										
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		return str.toString();
	}
	
	/** 
     * 工作计划及工作总结，录入的文字能有字数的限制，如：录入的文字低于多少字不允许提交
     * @return
     */
	public String getWorkLength()
	{
		String itemlength = "0";
		try
		{												
			ArrayList fieldItemList = DataDictionary.getFieldList("p01", Constant.USED_FIELD_SET);
			
			for(int i=0;i<fieldItemList.size();i++)
			{
				FieldItem fielditem = (FieldItem)fieldItemList.get(i);				
				if("P0103".equalsIgnoreCase(fielditem.getItemid()))
				{
					itemlength = String.valueOf(fielditem.getItemlength());
					if(itemlength==null || itemlength.trim().length()<=0) {
                        itemlength = "0";
                    }
					break;
				}								
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return itemlength;
	}
	
	/**
     * 获得绩效标准标度
     * @return
     */
    public ArrayList getGradedescList()
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.con);
    		String per_comTable = "per_grade_template"; // 绩效标准标度
    	//	if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1)
    	//		per_comTable = "per_grade_competence"; // 能力素质标准标度
    		RowSet rowSet=dao.search("select * from "+per_comTable+" order by gradevalue desc");
    		CommonData vo = new CommonData("null", ResourceFactory.getProperty("performance.workplan.workplanview.pleaseRate"));
    	    list.add(vo);
    	    while (rowSet.next())
    	    {
    	    	vo = new CommonData(rowSet.getString("grade_template_id"), rowSet.getString("gradedesc"));
	    		list.add(vo);
    	    	//2015/12/24 wangjl 个性化评语等级描述
    	    /*	String gradedesc=null;
    	    	String grade_template_id=rowSet.getString("grade_template_id");
    	    	if("A,B,C,D".indexOf(grade_template_id)!=-1)
    	    	{
	    	    	if("A".equals(grade_template_id)){
	    	    		gradedesc="优";
	    	    	}else if("B".equals(grade_template_id)){
	    	    		gradedesc="良";
	    	    	}else if("C".equals(grade_template_id)){
	    	    		gradedesc="中";
	    	    	}else if("D".equals(grade_template_id)){
	    	    		gradedesc="差";
	    	    	}
		    		vo = new CommonData(grade_template_id, gradedesc);
		    		list.add(vo);
    	    	}*/
    	    }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * Description: 判断是否是审批人
     * @Version1.0 
     * Sep 14, 2012 2:22:49 PM Jianghe created
     * @param a0100
     * @param nbase
     * @param uvA0100
     * @param uvNbase
     * @return
     */
    public boolean isCurrUser(String a0100,String nbase,String uvA0100,String uvNbase){
    	boolean flag = false;
		RowSet rs = null;
		if(   a0100!=null && !"".equals(a0100.trim())
				   && nbase!=null && !"".equals(nbase.trim())
				   && uvA0100!=null && !"".equals(uvA0100.trim())
				   && uvNbase!=null && !"".equals(uvNbase.trim())
				  ){
			try
			{	
				String sql = "";
			    ContentDAO dao = new ContentDAO(this.con);
			    sql = "select * from p01 where a0100='"+a0100+"' and nbase='"+nbase+"' and curr_user='"+this.getUsername(uvNbase+uvA0100)+"'";
			    rs = dao.search(sql);	
		    	if(rs.next())
		    	{	    		
		    		flag=true;
		    	}		    			    										
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
		}
		return flag;
    }
    /**
	 * 获得审批人sql
	 * @param role_property 审批层级
	 * @param relation_id   审批关系ID
	 * @param userView  
	 * @return
	 * @throws GeneralException
	 */
	public String getSuperSql(int role_property,String relation_id,String e01a1,String e0122,String b0110) 
	{
		
		StringBuffer whl=new StringBuffer("");
		whl.append("'@k"+e01a1+"'");
		if(e0122!=null&&e0122.length()>0)
		{
			for(int i=e0122.length();i>0;i--)
			{
				String temp=e0122.substring(0,i);
				if(b0110!=null&&temp.equals(b0110)) {
                    break;
                }
				whl.append(",'um"+temp+"'");
			}
		}
		if(b0110!=null&&b0110.length()>0)
		{
			for(int i=b0110.length();i>0;i--)
			{
				String temp=b0110.substring(0,i);
				whl.append(",'un"+temp+"'");
			}
		}
		
		String sql="select object_id,mainbody_id,a0101  from t_wf_mainbody where Relation_id="+relation_id+"  and lower(Object_id) in ("+whl.toString()+")";
		if(role_property!=13) {
            sql+=" and SP_GRADE="+role_property+"  " ;
        } else {
            sql+=" and SP_GRADE in (9,10,11,12) " ;
        }
		sql+=" order by object_id desc";
		return sql;
	}
    /**
     * Description: 判断当前领导是否在审批关系中定义
     * @Version1.0 
     * Sep 14, 2012 4:30:45 PM Jianghe created
     * @param a0100
     * @param nbase
     * @param sp_relation
     * @param uvA0100
     * @param uvNbase
     * @return
     */
    public boolean isInSpRelation(String a0100,String nbase,String sp_relation,String uvA0100,String uvNbase){
    	boolean flag = false;//true 在，false 不在
    	
		RowSet rs = null;
		if(   a0100!=null && !"".equals(a0100.trim())
		   && nbase!=null && !"".equals(nbase.trim())
		   && uvA0100!=null && !"".equals(uvA0100.trim())
		   && uvNbase!=null && !"".equals(uvNbase.trim())
		  ){
			try
			{	
				String sql = "";
			    ContentDAO dao = new ContentDAO(this.con);
			    RecordVo vo = new RecordVo(nbase+"a01");
				vo.setString("a0100", a0100);
				try {
					vo = dao.findByPrimaryKey(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String b0110 = vo.getString("b0110");
				String e0122 = vo.getString("e0122");
				String e01a1 = vo.getString("e01a1");
			    if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation)){
			    	StringBuffer whl=new StringBuffer("");
					whl.append("'"+nbase.toLowerCase()+a0100+"'");
					
					whl.append(",'@k"+e01a1+"'");
					if(e0122!=null&&e0122.length()>0)
					{
						for(int i=e0122.length();i>0;i--)
						{
							String temp=e0122.substring(0,i);
							if(b0110!=null&&temp.equals(b0110)) {
                                break;
                            }
							whl.append(",'um"+temp+"'");
						}
					}
					if(b0110!=null&&b0110.length()>0)
					{
						for(int i=b0110.length();i>0;i--)
						{
							String temp=b0110.substring(0,i);
							whl.append(",'un"+temp+"'");
						}
					}
					sql="select object_id,mainbody_id,a0101  from t_wf_mainbody where Relation_id="+sp_relation+"  and lower(Object_id) in ("+whl.toString()+")";
					sql+=" and SP_GRADE in (9,10,11,12) " ;
					sql+=" and mainbody_id = '"+uvNbase+uvA0100+"' " ;
			    }else{
			    	sql="select * from per_mainbody_std where object_id='"+a0100+"' and mainbody_id='"+uvA0100+"'";				    	
			    }
			    if(!"".equals(sql)){
			    	rs = dao.search(sql);
			    }    	
		    	if(rs.next())
		    	{	    		
		    		flag=true;
		    	}		    			    										
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
		}
		return flag;
    }
    
    
    /**
     * Description: 是否是最后一级领导
     * @Version1.0 
     * Sep 11, 2012 5:48:22 PM Jianghe created
     * @param a0100
     * @param nbase
     * @param sp_relation
     * @param uvA0100
     * @param uvNbase
     * @return
     */
    public boolean isFinalLeader(String a0100,String nbase,String sp_relation,String uvA0100,String uvNbase){
		boolean flag = false;
		RowSet rs = null;
		RowSet rs1 = null;
		if(   a0100!=null && !"".equals(a0100.trim())
		   && nbase!=null && !"".equals(nbase.trim())
		   && uvA0100!=null && !"".equals(uvA0100.trim())
		   && uvNbase!=null && !"".equals(uvNbase.trim())
		  ){
			try
			{	
				String sql = "";
			    ContentDAO dao = new ContentDAO(this.con);
			    RecordVo vo = new RecordVo(nbase+"a01");
				vo.setString("a0100", a0100);
				try {
					vo = dao.findByPrimaryKey(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String b0110 = vo.getString("b0110");
				String e0122 = vo.getString("e0122");
				String e01a1 = vo.getString("e01a1");
			    if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation)){
			    	sql = "select * from t_wf_mainbody where  relation_id='"+sp_relation+"' and object_id='"+nbase+a0100+"' and mainbody_id='"+uvNbase+uvA0100+"' and sp_grade=(select MAX(sp_grade) from t_wf_mainbody where sp_grade<=12 and object_id='"+nbase+a0100+"' and relation_id='"+sp_relation+"')";
			    	if(!isHaveResults(sql,dao)){
			    		
			    		String[] strArray = getInfo(uvNbase,uvA0100,nbase,a0100,dao,sp_relation);
						if(strArray!=null){
							String objectid = strArray[0];
							int level = Integer.parseInt(strArray[1]);
							int maxlevel = 0;
							rs1 = dao.search("select MAX(sp_grade) sp_grade from t_wf_mainbody where sp_grade<=12 and object_id='"+objectid+"' and relation_id='"+sp_relation+"'");
							if(rs1.next()){
								maxlevel = Integer.parseInt(rs1.getString("sp_grade"));
							}
							if(level == maxlevel) {
                                return true;
                            }
						}
			    	}
			    }else{
			    	//sql="select * from per_mainbody_std where object_id='"+a0100+"' and mainbody_id='"+uvA0100+"' and body_id=(select MIN(body_id) from per_mainbody_std where body_id<>5 and body_id>=-2 and object_id='"+a0100+"') ";
			    	sql = " select * from per_mainbody_std where object_id='"+a0100+"' and mainbody_id='"+uvA0100+"' and body_id in (select body_id from per_mainbodyset where ";
			    	if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						sql+=" level_o";
					}
					else{
						sql+=" level ";
					}
			    	sql+=" =(select min( ";
			    	if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						sql+=" level_o";
					}
					else{
						sql+=" level ";
					}
			    	sql+=" ) from per_mainbodyset where ";
			    	if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						sql+=" level_o";
					}
					else{
						sql+=" level ";
					}
			    	sql+=" <>5 and ";
			    	if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						sql+=" level_o";
					}
					else{
						sql+=" level ";
					}
			    	sql+=" >=-2 and body_id in (select body_id from per_mainbody_std where object_id='"+a0100+"'))) ";
			    	
//					//level=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
			    }
			    if(!"".equals(sql)){
			    	//System.out.println(sql);
			    	rs = dao.search(sql);
			    }    	
		    	if(rs.next())
		    	{	    		
		    		flag=true;
		    	}		    			    										
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
					if(rs1!=null) {
                        rs1.close();
                    }
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
		}
		return flag;
	}
  //获取是岗位还是部门还是单位几级领导
	public String [] getInfo(String uvNbase,String uvA0100,String nbase,String a0100,ContentDAO dao,String sp_relation){
		String sql = "";
		String object_id = "";
		String level = "";
		String []strArray = null;
		{
			sql="select object_id,sp_grade from t_wf_mainbody where relation_id = '"+ sp_relation +"' and mainbody_id = '"+uvNbase+uvA0100+"'";
			sql+=" order by object_id asc";
		}
		RowSet rs = null;
		if(nbase!=null&&!"".equals(nbase.trim())
		   && a0100!=null&&!"".equals(a0100.trim())			
		   && uvNbase!=null&&!"".equals(uvNbase.trim())			
		   && uvA0100!=null&&!"".equals(uvA0100.trim())			
		  ){
			try 
			{
				RecordVo vo = new RecordVo(nbase+"a01");
				vo.setString("a0100", a0100);
				try {
					vo = dao.findByPrimaryKey(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String b0110 = vo.getString("b0110");
				String e0122 = vo.getString("e0122");
				String e01a1 = vo.getString("e01a1");
				rs = dao.search(sql);
				while(rs.next())
				{
					object_id = rs.getString("object_id");
					level = rs.getString("sp_grade");
					if(object_id!=null&&!"".equals(object_id) && level!=null&&!"".equals(level)&&(object_id.substring(2).equals(e01a1) || object_id.substring(2).equals(e0122) || object_id.substring(2).equals(b0110))){
						if(object_id.substring(2).equals(e01a1)){
							strArray = new String[2];
							strArray[0] = object_id;
							strArray[1] = level;
							break;
						}else if(object_id.substring(2).equals(e0122)){
							strArray = new String[2];
							strArray[0] = object_id;
							strArray[1] = level;
							break;
						}else if(object_id.substring(2).equals(b0110)){
							strArray = new String[2];
							strArray[0] = object_id;
							strArray[1] = level;
							break;
						}
						
					}
				}
				
			} catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				if(rs!=null)
				{
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return strArray;
	}
    public boolean isHaveResults(String sql,ContentDAO dao){
		boolean flag = false;
		if(!"".equals(sql)){
			RowSet rs = null;
			try {
				rs = dao.search(sql);
				if(rs.next()){
					flag = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return flag;
		
	}
    public void approveWorkPlan(String p0100,String a0100,String nbase,String sp_relation,String spContent,String sp_level) throws GeneralException{
		try {
			String uvA0100 = userView.getA0100();
			String uvNbase = userView.getDbname();
			String uvA0101 = userView.getUserFullName();
			String uvFullName = userView.getUserFullName();
			String uvB0110 = userView.getUserOrgId();
			String uvE0122 = userView.getUserDeptId();
			String uvE01a1 = userView.getUserPosId();
			ContentDAO dao = new ContentDAO(this.con);						
			RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100",Integer.parseInt(p0100));
			vo = dao.findByPrimaryKey(vo);
			//审批
			
				if(sp_level!=null && "1".equals(sp_level)){
					
					RecordVo revo = new RecordVo("per_diary_opinion");
					int id = getLastEdit(Integer.parseInt(p0100), uvA0100, uvNbase);
					int saveid = id;
					if(id==0){
						saveid = this.getId();
					}
					revo.setInt("id",saveid);
					if(id!=0){
						revo = dao.findByPrimaryKey(revo);
					}
					revo.setString("p0100",p0100);
					revo.setString("b0110", uvB0110);
					revo.setString("e0122", uvE0122);
					revo.setString("e01a1", uvE01a1);
					revo.setString("nbase", uvNbase);
					revo.setString("a0100", uvA0100);
					revo.setString("a0101", uvA0101);
					int uvSpgrade = 9;
					revo.setInt("sp_grade", uvSpgrade);
					if(spContent!=null){
						revo.setString("description", spContent);
					}
					revo.setString("pg_code", null);
					revo.setDate("sp_date", new Date());
					if(id!=0){
						dao.updateValueObject(revo);
					}else{
						dao.addValueObject(revo);
					}
					vo.setString("p0115","03");
					vo.setDate("p0116",new Date());
					vo.setString("p0117",uvFullName);
					vo.setString("curr_user",null);
					dao.updateValueObject(vo);
					
				}else{
					    
						RecordVo revo = new RecordVo("per_diary_opinion");
						int id = getLastEdit(Integer.parseInt(p0100), uvA0100, uvNbase);
						int saveid = id;
						if(id==0){
							saveid = this.getId();
						}
						revo.setInt("id",saveid);
						if(id!=0){
							revo = dao.findByPrimaryKey(revo);
						}
						revo.setString("p0100",p0100);
						revo.setString("b0110", uvB0110);
						revo.setString("e0122", uvE0122);
						revo.setString("e01a1", uvE01a1);
						revo.setString("nbase", uvNbase);
						revo.setString("a0100", uvA0100);
						revo.setString("a0101", uvA0101);
						int uvSpgrade = 0;
						
						uvSpgrade = Integer.parseInt(!"".equals(this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao,sp_relation ))?this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao,sp_relation ):"0");
						if(uvSpgrade==0){
							String [] strArray = this.getInfo(uvNbase, uvA0100, nbase, a0100, dao, sp_relation);
							if(strArray!=null){
								uvSpgrade = Integer.parseInt(strArray[1]);
							}
						}
						revo.setInt("sp_grade", uvSpgrade);
						if(spContent!=null){
							revo.setString("description", spContent);
						}
						revo.setString("pg_code", null);
						revo.setDate("sp_date", new Date());
						if(id!=0){
							dao.updateValueObject(revo);
						}else{
							dao.addValueObject(revo);
						}
						
						
					//if(this.isFinalLeader(a0100, nbase, sp_relation, uvA0100, uvNbase) || (this.isCurrUser(a0100, nbase, uvA0100, uvNbase) && !this.isInSpRelation(a0100,nbase,sp_relation,uvA0100,uvNbase))){
				    if(this.isCurrUser(a0100, nbase, uvA0100, uvNbase) && (this.isFinalLeader(a0100, nbase, sp_relation, uvA0100, uvNbase) ||  !this.isInSpRelation(a0100,nbase,sp_relation,uvA0100,uvNbase))){	
						vo.setString("p0115","03");
						vo.setDate("p0116",new Date());
						vo.setString("p0117",uvFullName);
						vo.setString("curr_user",null);
						dao.updateValueObject(vo);
					}else{
						String sql = "";
						//报批给上级领导
						// 参数设置中设置了审批关系就按设置的走，否则按之前的日志（考核关系）走
						if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation))
						{
							
							//当前是用户
							if((nbase+a0100).equals(uvNbase+uvA0100)){
								//判断是否有直接领导
								if(isHaveLeader(nbase,a0100,sp_relation,dao,"9")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '9' and relation_id = '"+ sp_relation +"' ";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"10")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '10' and relation_id = '"+ sp_relation +"' ";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"11")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '11' and relation_id = '"+ sp_relation +"' ";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
								}
							}
							//当前是直接领导
							if("9".equals(whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
								if(isHaveLeader(nbase,a0100,sp_relation,dao,"10")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '10' and relation_id = '"+ sp_relation +"' ";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"11")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '11' and relation_id = '"+ sp_relation +"' ";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
								}
							}
							//当前是二级领导
							if("10".equals(whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
								if(isHaveLeader(nbase,a0100,sp_relation,dao,"11")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '11' and relation_id = '"+ sp_relation +"' ";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
								}
							}
							//当前是三级领导
							if("11".equals(whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
								if(isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
									sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
								}
							}
						}else
						{	
							//当前是用户
							if((nbase+a0100).equals(uvNbase+uvA0100)){
								//判断是否有直接领导
								if(isHaveLeader(nbase,a0100,sp_relation,dao,"1")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '1'  and pmb.object_id='"+a0100+"'";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"0")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '0'  and pmb.object_id='"+a0100+"'";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"-1")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '-1'  and pmb.object_id='"+a0100+"'";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '-2'  and pmb.object_id='"+a0100+"'";
								}
							}
							//当前是直接领导
							if("9".equals(whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
								if(isHaveLeader(nbase,a0100,sp_relation,dao,"0")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '0'  and pmb.object_id='"+a0100+"'";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"-1")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '-1'  and pmb.object_id='"+a0100+"'";
								}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '-2'  and pmb.object_id='"+a0100+"'";
								}
							}
							//当前是二级领导
							if("10".equals(whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
								sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
								if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                    sql+=" level_o";
                                } else {
                                    sql+=" level ";
                                }
								//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
								sql+="= '-1'  and pmb.object_id='"+a0100+"'";
							}else if(isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
								sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
								if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                    sql+=" level_o";
                                } else {
                                    sql+=" level ";
                                }
								//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
								sql+="= '-2'  and pmb.object_id='"+a0100+"'";
							}
							//当前是三级领导
							if("11".equals(whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
								if(isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
									sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                                        sql+=" level_o";
                                    } else {
                                        sql+=" level ";
                                    }
									//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
									sql+="= '-2'  and pmb.object_id='"+a0100+"'";
								}
							}
						}
								
						RowSet rs = null;
						ArrayList list = new ArrayList();
						try 
						{
							if(!"".equals(sql)){
								rs = dao.search(sql);
								
								while(rs.next())
								{
									LazyDynaBean bean=new LazyDynaBean();
									bean.set("a0101", rs.getString("a0101"));
									if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation)) {
                                        bean.set("a0100", rs.getString("mainbody_id"));
                                    } else {
                                        bean.set("a0100", "Usr"+rs.getString("mainbody_id"));
                                    }
									list.add(bean);
								}
							}
						} catch (SQLException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally
						{
							if(rs!=null)
							{
								try {
									rs.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						String name="";
						String appbody_id ="";
						String a0101 = "";
						for (int i = 0; i < list.size(); i++) {
							LazyDynaBean bean = (LazyDynaBean)list.get(i);
							String theappbody_id = (String)bean.get("a0100");
							String thename = (String)bean.get("a0101");
							
							if(theappbody_id!=null && theappbody_id.trim().length()>0 && thename!=null && thename.trim().length()>0){
								appbody_id = theappbody_id;
								name = thename;
								a0101 = this.getUsername(theappbody_id);
								break;
							}
						}
						if(name!=null && name.trim().length()>0)
						{
						    dao.update("update p01 set p0115='02',curr_user = '"+a0101+"' where p0100="+p0100);  
						}
						else
						{
						    dao.update("update p01 set p0115='02' where p0100="+p0100);	
						}
					}
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
			
	}
	/**
	 * 新建指标计算公式临时表字段
	 */
	public Field getField(String fieldname, String a_type, int length, boolean key)
    {
		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("F".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key) {
            obj.setNullable(false);
        }
		obj.setKeyable(key);	
		return obj;
    }
	public ArrayList getPdoList(int p0100){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{	
			String sql = "";
		    ContentDAO dao = new ContentDAO(this.con);
		    sql = "select * from per_diary_opinion where p0100="+p0100;
		    rs = dao.search(sql);
	    	while(rs.next())
	    	{	
	    		RecordVo vo = new RecordVo("per_diary_opinion");
	    		vo.setInt("p0100",rs.getInt("id"));
				vo = dao.findByPrimaryKey(vo);
	    		list.add(vo);
	    	}		    			    										
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 取id值
	 * @return
	 * @throws SQLException 
	 */
	public int getId() throws SQLException
	{
		int id=1;
		try{
			IDGenerator  idg=new IDGenerator(2,this.con);
			id=Integer.parseInt(idg.getId("per_diary_opinion.id"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
		
		/*
		int maxid=0;
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = dao.search("select max(id) from per_diary_opinion");
	        while(rowSet.next())
	        {
	        	String id = rowSet.getString(1);
	        	if((id!=null) && (id.trim().length()>0))
	        	maxid=Integer.parseInt(id);	
	        }
	    return  ++maxid;*/	
		
	}
	//当前用户是哪级领导
	public String whichCurrentLevel(String uvNbase,String uvA0100,String nbase,String a0100,ContentDAO dao,String sp_relation){
		String level = "";
		String sql = "";
		if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation))
		{
			sql="select sp_grade from t_wf_mainbody where object_id = '"+nbase+a0100+"' and relation_id = '"+ sp_relation +"' and mainbody_id = '"+uvNbase+uvA0100+"'";
		}else{
			sql="select ";		
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sql+=" level_o";
            } else {
                sql+=" level ";
            }
			sql+=" from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and mainbody_id";
			//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
			sql+="="+uvA0100+"  and pmb.object_id='"+a0100+"'";//????有问题
		}
		RowSet rs = null;
		if(nbase!=null&&!"".equals(nbase.trim())
		   && a0100!=null&&!"".equals(a0100.trim())			
		   && uvNbase!=null&&!"".equals(uvNbase.trim())			
		   && uvA0100!=null&&!"".equals(uvA0100.trim())			
		  ){
			try 
			{
				rs = dao.search(sql);
				if(rs.next())
				{
					if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation)){
						level = rs.getString("sp_grade");
					}
					else{
						if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            level = getLevelConvert(rs.getString("level_o"));
                        } else {
                            level = getLevelConvert(rs.getString("level"));
                        }
					}
				     
				}
				
			} catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				if(rs!=null)
				{
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return level;
	}
	//判断是否有哪级领导
	public boolean isHaveLeader(String nbase,String a0100,String sp_relation,ContentDAO dao,String sp_grade){
		boolean flag=false;
		String sql = "";
		if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation))
		{
			sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '"+sp_grade+"' and relation_id = '"+ sp_relation +"' ";
		}else{
			sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sql+=" level_o";
            } else {
                sql+=" level ";
            }
			//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
			String body_id= sp_grade;
			sql+="="+body_id+"  and pmb.object_id='"+a0100+"'";
		}
		RowSet rs = null;
		if(nbase!=null&&!"".equals(nbase.trim())
		   && a0100!=null&&!"".equals(a0100.trim())			
		  ){
			try 
			{
				rs = dao.search(sql);
				if(rs.next())
				{
					flag=true;
				}
				
			} catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				if(rs!=null)
				{
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}	
		return flag;	
	}
	/**
	 * Description: 如果不存在，创建新表
	 * @Version1.0 
	 * Sep 10, 2012 10:10:34 AM Jianghe created
	 * @param tableName
	 */
	public void createNewTable(String tableName){
		try{
			DbWizard dbWizard = new DbWizard(this.con);	
		    //ContentDAO dao = new ContentDAO(this.con);
		    if(!dbWizard.isExistTable(tableName,false))
			{
		    	Table table = new Table(tableName);				
			    table.addField(getField("id", "I", 10, true));
			    table.addField(getField("p0100", "I", 10, false));				
			    table.addField(getField("B0110", "A", 30, false));				
			    table.addField(getField("E0122", "A", 30, false));				
			    table.addField(getField("E01A1", "A", 30, false));				
			    table.addField(getField("NBASE", "A", 3, false));				
			    table.addField(getField("A0100", "A", 30, false));				
			    table.addField(getField("A0101", "A", 30, false));				
			    table.addField(getField("sp_grade", "I", 4, false));				
			    table.addField(getField("Description", "M", 10, false));				
			    table.addField(getField("pg_code", "A", 30, false));				
			    table.addField(getField("sp_date", "D", 10, false));
			    dbWizard.createTable(table);
			}
		  
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String[] getUserInfo(String a0100){
		String []strArray = new String[3];
		RecordVo user_vo = new RecordVo(""+(a0100.substring(0,3)).toUpperCase()+"A01");
		user_vo.setString("a0100",a0100.substring(3));
		String b0110 = "";
		String e0122 = "";
		String e01a1 = "";
		try 
		{
			ContentDAO dao = new ContentDAO(this.con);
			
			if(dao.isExistRecordVo(user_vo))
			{
				user_vo=dao.findByPrimaryKey(user_vo);
				if(user_vo!=null)
				{
					b0110 = user_vo.getString("b0110");					
					e0122 = user_vo.getString("e0122");					
					e01a1 = user_vo.getString("e01a1");	
					strArray[0] = b0110;
					strArray[1] = e0122;
					strArray[2] = e01a1;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strArray;
	}
	public String getCommands(String flag,String opt,String p0100){
		String value="";
		String sp_level = (String)workParametersMap.get("sp_level");
		ArrayList list = new ArrayList();
		RowSet rs = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm");
		try{
			ContentDAO dao = new ContentDAO(this.con);
			String sp_date = "sp_date";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				sp_date = "to_char(sp_date,'yyyy.mm.dd hh24:mi') as sp_date";
			}
				if("1".equals(flag)){
					
					if(opt!=null && !"".equals(opt.trim()) && "3".equals(opt.trim())){
						//如果当前用户是审批用户，则上半部分不显示自己最后一条的意见
						int id = getLastEdit(Integer.parseInt(p0100), this.userView.getA0100(), this.userView.getDbname());
						if(id!=0){
							rs = dao.search("select "+sp_date+",b0110,e0122,e01a1,a0101,description from per_diary_opinion where p0100 ="+p0100+" and id<> "+id+" order by sp_date asc");
						}else{
							rs = dao.search("select "+sp_date+",b0110,e0122,e01a1,a0101,description from per_diary_opinion where p0100 ="+p0100+"order by sp_date asc");
						}
					}else{
						rs = dao.search("select "+sp_date+",b0110,e0122,e01a1,a0101,description from per_diary_opinion where p0100 ="+p0100+"order by sp_date asc");
					}
					while(rs.next()){
						LazyDynaBean bean = new LazyDynaBean();
						
						if(Sql_switcher.searchDbServer()==Constant.ORACEL){
							bean.set("sp_date", rs.getString("sp_date"));
						}else{
							bean.set("sp_date", sdf.format(rs.getDate("sp_date")));
						}
						bean.set("b0110", isNull(rs.getString("b0110")));
						bean.set("e0122", isNull(rs.getString("e0122")));
						bean.set("e01a1", isNull(rs.getString("e01a1")));
						bean.set("a0101", isNull(rs.getString("a0101")));
						bean.set("description", Sql_switcher.readMemo(rs,"description"));
						list.add(bean);
					}
					if(list.size()>0){
						StringBuffer bufString = new StringBuffer();
						for (int i = 0; i < list.size(); i++) {
							StringBuffer buf = new StringBuffer("");
							LazyDynaBean bean = (LazyDynaBean)list.get(i);
							String e01a1 = (String)bean.get("e01a1");
							if(bean.get("sp_date")!=null && !"".equals(bean.get("sp_date"))){
								buf.append(bean.get("sp_date")+"  ");
							}
							//AdminCode.getCodeName("UN",name)
							//AdminCode.getCodeName("UM",name)
							//AdminCode.getCodeName("@K",name)
							if(bean.get("b0110")!=null && !"".equals(bean.get("b0110"))){
								buf.append(AdminCode.getCodeName("UN",(String)bean.get("b0110"))+"/");
							}
							if(bean.get("e0122")!=null && !"".equals(bean.get("e0122"))){
								buf.append(AdminCode.getCodeName("UM",(String)bean.get("e0122"))+"/");
							}
							if(e01a1!=null && e01a1.trim().length()>0){
								buf.append(AdminCode.getCodeName("@K",e01a1)+"/");
							}
							if(bean.get("a0101")!=null && !"".equals(bean.get("a0101"))){
								buf.append(bean.get("a0101")+"/");
							}
							if(buf.indexOf("/")!=-1){
								//buf.substring(0, buf.length() - 1);
								buf.setLength(buf.length() - 1);
								buf.append("  ");
							}
							if(bean.get("description")!=null && !"".equals(bean.get("description"))){
								buf.append("\r\n"+bean.get("description")+";"+"\r\n");
							}
							if(buf.length()>0){
								bufString.append(buf.toString());
							}
						}
						if(bufString.length()>0){
							value = bufString.toString();
						}
					}
				}else{
					if(sp_level!=null && !"".equals(sp_level.trim()) && "2".equals(sp_level)){
						int id = getLastEdit(Integer.parseInt(p0100), this.userView.getA0100(), this.userView.getDbname());
						if(id!=0){
							rs = dao.search("select description from per_diary_opinion where p0100 ="+p0100+" and id= "+id+" order by sp_date asc");
							if(rs.next()){
								value=Sql_switcher.readMemo(rs,"description");
							}
						}	
					}else{
						rs = dao.search("select description from per_diary_opinion where p0100="+p0100+"order by sp_grade asc");
						if(rs.next()){
							value=Sql_switcher.readMemo(rs,"description");
						}
					}
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return value;
		
	}
	/**
	 * Description: 获取指定驳回人
	 * @Version1.0 
	 * Sep 12, 2012 1:41:23 PM Jianghe created
	 * @param p0100
	 * @param a0100
	 * @return
	 */
	public String getRejectTo(int p0100,String a0100,String nbase,String sp_relation){
		RowSet rs = null;
		String personName = null;
		if(this.workParametersMap.get("sp_level")!=null&& "1".equals(this.workParametersMap.get("sp_level"))){
			return personName;
		}
		String uvNbase = this.userView.getDbname();
		String uvA0100 = this.userView.getA0100();
		int currentLevel = -50;
		ContentDAO dao = new ContentDAO(this.con);
		if(this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao, sp_relation)!=null && !"".equals(this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao, sp_relation))){
			currentLevel = Integer.parseInt(this.whichCurrentLevel(uvNbase, uvA0100, nbase, a0100, dao, sp_relation));
		}
		if(currentLevel==-50){
			String [] strArray = this.getInfo(uvNbase, uvA0100, nbase, a0100, dao, sp_relation);
			if(strArray!=null){
				currentLevel = Integer.parseInt(strArray[1]);
			}
		}
		String sql = "select a0100,nbase from per_diary_opinion where sp_date=(select max(sp_date) from per_diary_opinion where p0100="+p0100+" and sp_grade<"+currentLevel+")";
		try{
			rs = dao.search(sql);
			if(rs.next()){
				String personId = rs.getString("a0100");
				String personNbase = rs.getString("nbase");
				personName = this.getUsername(personNbase+personId);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return personName;
	}
	/**
	 * Description: 考核关系级别转换
	 * @Version1.0 
	 * Sep 12, 2012 4:29:57 PM Jianghe created
	 * @param level
	 * @return
	 */
	public String getLevelConvert(String level){
		//-2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
		String sp_grade = "";
		if("1".equals(level)){
			sp_grade = "9";
		}
		if("0".equals(level)){
			sp_grade = "10";
		}
		if("-1".equals(level)){
			sp_grade = "11";
		}
		if("-2".equals(level)){
			sp_grade = "12";
		}
		return sp_grade;
		
	}
	/**
	 * Description: 如果最后一条记录是自己编辑，则返回id，没有则返回0
	 * @Version1.0 
	 * Sep 14, 2012 3:15:41 PM Jianghe created
	 * @param p0100
	 * @param uvA0100
	 * @param uvNbase
	 * @return
	 */
	public int getLastEdit(int p0100,String uvA0100,String uvNbase){
		int id = 0;
		RowSet rs = null;
		if(   uvA0100!=null && !"".equals(uvA0100.trim())
				   && uvNbase!=null && !"".equals(uvNbase.trim())
				  ){
			try
			{	
				String sql = "";
			    ContentDAO dao = new ContentDAO(this.con);
			    sql = "select id from PER_DIARY_OPINION where p0100="+p0100+" and a0100='"+uvA0100+"' and nbase='"+uvNbase+"' and id=(select max(id) from PER_DIARY_OPINION)";
			   
			    rs = dao.search(sql);	
		    	if(rs.next())
		    	{	    		
		    		id = rs.getInt("id");
		    	}		    			    										
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
		}
		return id;
	}
}

