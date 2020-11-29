package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>Title:ShowInfoPlanTrans.java</p>
 * <p>Description:引入同期工作计划内容</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-08-01 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class ShowInfoPlanTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	StringBuffer str = new StringBuffer("");
    	RowSet rs = null;
		try
		{
			String nbase = (String)this.getFormHM().get("nbase");
			String a0100 = (String)this.getFormHM().get("a0100");
			String state = (String)this.getFormHM().get("state");
			String year_num = (String)this.getFormHM().get("year_num");
			String quarter_num = (String)this.getFormHM().get("quarter_num");
			String month_num = (String)this.getFormHM().get("month_num");
			String week_num = (String)this.getFormHM().get("week_num");
			String day_num = (String)this.getFormHM().get("day_num");
			String opt=(String)this.getFormHM().get("opt");  //collectSummarize:年总结汇总季总结  季度总结汇总月总结  月总结汇总周报 周汇总日报     
			// 获得同周期的工作计划的p0100
			ContentDAO dao = new ContentDAO(this.frameconn);
			WeekUtils weekUtils = new WeekUtils();
			Date weekfirst = null;
			Date weeklast = null;
			String month = "";
			Calendar c = null;
			StringBuffer zh = null;
			if("collectSummarize".equals(opt))
			{
				String workType=(String)this.getFormHM().get("workType"); //=1个人年工作计划总结。=3个人季度工作计划总结  =5个人月工作计划总结   =9个人周工作计划总结
				String sql = getCollectSql(workType,nbase,a0100,state,year_num,quarter_num,month_num,week_num,day_num); 
				rs = dao.search(sql);	  
		    	while(rs.next())
		    	{	
		    		//2016/2/4 wangjl 工作纪实自动汇总中加入时间段划分
		    		if("9".equals(workType)){
		    			zh = new StringBuffer("周");
		    			c = Calendar.getInstance();
		    			c.setTime(rs.getDate("p0104")); 
		    			switch (c.get(Calendar.DAY_OF_WEEK)) {
						case 1:
							zh.append("日");
							break;
						case 2:
							zh.append("一");
							break;
						case 3:
							zh.append("二");
							break;
						case 4:
							zh.append("三");
							break;
						case 5:
							zh.append("四");
							break;
						case 6:
							zh.append("五");
							break;
						case 7:
							zh.append("六");
							break;
						}
		    			str.append(DateUtils.format(rs.getDate("p0104"), "yyyy-MM-dd"));
		    			str.append("("+zh+")：");
		    		}else if("5".equals(workType)){
		    			int totalWeek = weekUtils.totalWeek(Integer.valueOf(year_num),Integer.valueOf(month_num));
		    			for(int i = 1;i<=totalWeek;i++){
		    				weekfirst = weekUtils.numWeek(Integer.valueOf(year_num), Integer.valueOf(month_num), i, 1);
		    				weeklast = weekUtils.numWeek(Integer.valueOf(year_num), Integer.valueOf(month_num), i, 7);
			    			if(weekfirst.compareTo(rs.getTime("p0104"))<=0&&weeklast.compareTo(rs.getTime("p0104"))>=0){
			    				str.append("\r\n第"+i+"周总结:");
			    			}
		    			}
		    		}else if("3".equals(workType)){
		    			month = Sql_switcher.readMemo(rs, "p0104");
		    			str.append("\r\n第"+month+"月总结:");
		    		}else if("1".equals(workType)){
		    			zh = new StringBuffer();
		    			month = Sql_switcher.readMemo(rs, "p0104");
		    			for(int i=1;i<=4;i++){
		    				if(StringUtils.isNotEmpty(month)&&((i-1)*3+1)<=Integer.valueOf(month)&&Integer.valueOf(month)<(i*3)+1){
		    					switch (i) {
								case 1:
									zh.append("一");
									break;
								case 2:
									zh.append("二");
									break;
								case 3:
									zh.append("三");
									break;
								case 4:
									zh.append("四");
									break;
		    					}
		    					str.append("\r\n第"+zh+"季度总结汇总:");
		    				}
		    			}
		    		}
		    		str.append("\r\n");
		    		str.append(Sql_switcher.readMemo(rs, "p0103")+"\r\n");
		    		 
		    	}
			}
			else
			{ 
				// 获得同周期的工作计划的p0100
				String p0100 = getSummaryStr(nbase,a0100,state,year_num,quarter_num,month_num,week_num,day_num); 
		    	rs = dao.search("select Content,Record_num from per_diary_content where p0100='"+p0100+"' order by Record_num");	    	
		    	int num = 1;
		    	while(rs.next())
		    	{	    		
		    		str.append("\r\n第"+num+"条:"+Sql_switcher.readMemo(rs, "content"));
		    		num++;
		    	}
			}
			PubFunc.closeDbObj(rs);
			
			this.getFormHM().put("content", SafeCode.encode(str.toString()));
			this.getFormHM().put("opt", opt);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		
    }
    
    
    private String getCollectSql(String workType,String nbase,String a0100,String state,String year_num,String quarter_num,String month_num,String week_num,String day_num)
    {
    	StringBuffer sql=new StringBuffer("");
    	
    	if("1".equals(workType)) //年工作总结
    	{
    		sql.append("select "+Sql_switcher.month("p0104")+" p0104,p0103 from p01 where state=3 ");
    		sql.append(" and "+Sql_switcher.year("p0104")+"="+year_num);	 
    		sql.append(" and log_type = '2'  "); //1计划，2总结
    	}
    	else if("3".equals(workType)) //季度工作总结
    	{
    		sql.append("select "+Sql_switcher.month("p0104")+" p0104,p0103 from p01 where state=2 ");
    		sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" and "+Sql_switcher.quarter("p0104")+"="+ quarter_num +" ");   		 
    		sql.append(" and log_type = '2'  "); //1计划，2总结
    	}
    	else if("5".equals(workType)) //月度工作总结
    	{
    		sql.append("select p0104,p0103 from p01 where state=1 ");
    		sql.append(" and (("+Sql_switcher.year("p0104")+"="+year_num+" and "+Sql_switcher.month("p0104")+"="+month_num+" ) ");	
    		sql.append(" or ("+Sql_switcher.year("p0106")+"="+year_num+" and "+Sql_switcher.month("p0106")+"="+month_num+" ) ) ");	
    	} 
    	else if("9".equals(workType))  //=9个人周工作计划总结
    	{ 
    			WeekUtils weekutils = new WeekUtils();				
				String startime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),1)); // 某年某月的第week_num周星期1的日期(返回字符串格式日期)
				String endtime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),7)); // 某年某月的第week_num周星期日的日期(返回字符串格式日期)
   			   			  
				sql.append("select p0104,p0103 from p01 where state=0 ");
    	//		sql.append(" and "+Sql_switcher.year("p0104")+"="+year_num); 		//20151229  dengcan 有时间范围控制无需年份
    			sql.append(" and p0104 between "+Sql_switcher.dateValue(startime)+ " and " +Sql_switcher.dateValue(endtime));
    	 
    	}
    	
    	
    	sql.append(" and UPPER(nbase)='"+ nbase.toUpperCase() +"' ");
    	sql.append(" and a0100='"+ a0100 +"' "); 
    	sql.append(" order by p0104 ");
    	return sql.toString();
    }



    		
    		
    
    /**
	 * 获得同周期的工作计划的p0100
	 * @return
	 */
	public String getSummaryStr(String nbase,String a0100,String state,String year_num,String quarter_num,String month_num,String week_num,String day_num)
	{
		String p0100 = "";		
		RowSet rs = null;
		try
		{									
		    ContentDAO dao = new ContentDAO(this.frameconn);
		    StringBuffer sql = new StringBuffer("");
		    sql.append("select p0100 from p01 where state="+ state +" ");
		    
		    if("4".equalsIgnoreCase(state)) // 年
    		{
		    	sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" ");		    	
    		}
    		else if("3".equalsIgnoreCase(state)) // 季
    		{
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" and "+Sql_switcher.quarter("p0104")+"="+ quarter_num +" ");   			
    		}
    		else if("2".equalsIgnoreCase(state)) // 月
    		{
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" and "+Sql_switcher.month("p0104")+"="+ month_num +" ");
    		}
    		else if("1".equalsIgnoreCase(state)) // 周
    		{
    			WeekUtils weekutils = new WeekUtils();				
				String startime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),1)); // 某年某月的第week_num周星期1的日期(返回字符串格式日期)
				String endtime = weekutils.dateTostr(weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),7)); // 某年某月的第week_num周星期日的日期(返回字符串格式日期)
   			   			   			   			
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+year_num);
    		//	sql.append(" and "+Sql_switcher.month("p0104")+"="+month_num);		//20151229  dengcan 有时间范围控制无需年份		
    			sql.append(" and p0104 between "+Sql_switcher.dateValue(startime)+ " and " +Sql_switcher.dateValue(endtime));
    		}
    		else if("0".equalsIgnoreCase(state)) // 日
    		{
    			sql.append(" and "+Sql_switcher.year("p0104")+"="+ year_num +" and "+Sql_switcher.month("p0104")+"="+ month_num +" and "+Sql_switcher.day("p0104")+"="+ day_num +" ");
    		}
		    sql.append(" and UPPER(nbase)='"+ nbase.toUpperCase() +"' ");
	    	sql.append(" and a0100='"+ a0100 +"' ");
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
				if(rs!=null)
					rs.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		return p0100;
	}
    
}