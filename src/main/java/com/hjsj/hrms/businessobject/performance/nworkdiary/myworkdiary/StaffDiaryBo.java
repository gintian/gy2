package com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class StaffDiaryBo {
	private Connection conn;
	private UserView userView;
	private boolean isGw=false;
	
	public StaffDiaryBo(Connection conn,UserView userView){
		this.conn=conn;
		this.userView=userView;
		String clientName = SystemConfig.getPropertyValue("clientName");
		if(clientName!=null&& "gw".equalsIgnoreCase(clientName)){
			isGw=true;
		}
	}
	HashMap weekMap = new HashMap();//weekMap的键为日期所在周范围内的周六，值为：起始日期/结束日期
	public String getPrivSql(){
		StringBuffer buf = new StringBuffer("");
		try{
			if(this.isGw){
				buf.append(" and a0100 in (select object_id from per_mainbody_std where ");
				buf.append(" mainbody_id='"+this.userView.getA0100()+"' ");
				buf.append(" and body_id in (select body_id from per_mainbodyset where ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					buf.append(" level_o ");
				else
					buf.append(" level ");
				buf.append(" in (0,1,-1,-2)");
				buf.append("))");
			}else{
				ArrayList dblist = this.userView.getPrivDbList();
				if(dblist!=null&&dblist.size()>0){
					buf.append(" and (");
					for(int j=0;j<dblist.size();j++){
						String nbase=(String)dblist.get(j);
			    	    String whereA0100In=InfoUtils.getWhereINSql(this.userView,nbase);
			    	    if(j!=0)
			    	    	buf.append(" or ");
			    	    buf.append(" (a0100 in (select "+nbase + "a01.a0100 "+whereA0100In+")) ");
					}
					buf.append(")");
				}else{
					buf.append(" and 1=2 ");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 查询时涉及到多表关联，所以要区分是哪个表的字段。功能和getPrivSql一样
	 * */
	public String getPrivSql2(){
		StringBuffer buf = new StringBuffer("");
		try{
			if(this.isGw){
				buf.append(" and p.a0100 in (select object_id from per_mainbody_std where ");
				buf.append(" mainbody_id='"+this.userView.getA0100()+"' ");
				buf.append(" and body_id in (select body_id from per_mainbodyset where ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					buf.append(" level_o ");
				else
					buf.append(" level ");
				buf.append(" in (0,1,-1,-2)");
				buf.append("))");
			}else{
				ArrayList dblist = this.userView.getPrivDbList();
				if(dblist!=null&&dblist.size()>0){
					buf.append(" and (");
					for(int j=0;j<dblist.size();j++){
						String nbase=(String)dblist.get(j);
			    	    String whereA0100In=InfoUtils.getWhereINSql(this.userView,nbase);
			    	    if(j!=0)
			    	    	buf.append(" or ");
			    	    buf.append(" (p.a0100 in (select "+nbase + "a01.a0100 "+whereA0100In+")) ");
					}
					buf.append(")");
				}else{
					buf.append(" and 1=2 ");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return buf.toString();
	}
	public ArrayList getYearList(){
		RowSet rs = null;
		ArrayList list  = new ArrayList();
		try{
			StringBuffer sql = new StringBuffer("");
			String privSql = this.getPrivSql();
			sql.append(" select distinct "+Sql_switcher.year("start_time")+" as ayear from per_diary_content where 1=1 ");
			sql.append(" "+privSql+" union All ");
			sql.append(" select distinct "+Sql_switcher.year("end_time")+" as ayear from per_diary_content where ");
			sql.append(" 1=1 ");
			sql.append(privSql+" order by ayear desc");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			HashMap map = new HashMap();
			while(rs.next()){
				String year = rs.getString("ayear");
				if(map.get(year)!=null)
					continue;
				map.put(year, year);
				list.add(new CommonData(year,year));
			}
			Calendar calendar= Calendar.getInstance();
			String year = calendar.get(Calendar.YEAR)+"";
			if(!map.containsKey(year)){
				map.put(year, year);
				list.add(0,new CommonData(year,year));
			}
			//遍历map，找到最小的年份和最大的年份
			int minyear = Integer.parseInt((String)map.get(year+""));
			int maxyear = minyear;
			Set key = map.keySet();
			//循环map
	        for (Iterator it = key.iterator(); it.hasNext();) {
	            String s = (String) it.next();
	            int temp = Integer.parseInt(s);
	            if(temp<minyear){
	            	minyear = temp;
	            }
	            if(temp>maxyear){
	            	maxyear = temp;
	            }
	        }
	        int weekofmonth1 = getWeek(minyear+"","1","1");//得到1月1号是星期几
	        if(weekofmonth1>0){//如果1号不是星期天，则把去年也加上
	        	list.add(0,new CommonData((minyear-1)+"",(minyear-1)+""));
	        }
        	int weekofmonth2 = getWeek(maxyear+"","12","31");//得到12月31号是星期几
	        if(weekofmonth2<6){//如果31号不是星期六，则把下年也加上
	        	list.add(new CommonData((maxyear+1)+"",(maxyear+1)+""));
	        }	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	public ArrayList getMonthList(){
		ArrayList list  = new ArrayList();
		list.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
		list.add(new CommonData("1","1"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("2","2"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("3","3"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("4","4"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("5","5"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("6","6"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("7","7"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("8","8"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("9","9"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("10","10"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("11","11"+ResourceFactory.getProperty("datestyle.month")));
		list.add(new CommonData("12","12"+ResourceFactory.getProperty("datestyle.month")));
		return list;
	}
	public ArrayList getDayList (String year,String month,String week){
		ArrayList list = new ArrayList();
		list.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
		if("-1".equals(week)){
			return list;
		}
		//只有周不是空的时候才有具体的日
		String []tmp = week.split("/");
		String []beginTmp = tmp[0].split("-");
		String []endTmp = tmp[1].split("-");
		int intBegin = Integer.parseInt(beginTmp[2]);//本周开始的日
		int intEnd = Integer.parseInt(endTmp[2]);//本周结束的日
		if(intBegin>intEnd){//如果这个周跨月了
			int dayCount = getThisTotalDay(tmp[0]);
			for(int i=intBegin;i<=dayCount;i++){
				String s = beginTmp[0]+"-"+beginTmp[1]+"-"+i;
				list.add(new CommonData(s+"",beginTmp[1]+"月"+i+"日"));
			}
			for(int i=1;i<=intEnd;i++){
				String s = endTmp[0]+"-"+endTmp[1]+"-"+i;
				list.add(new CommonData(s+"",endTmp[1]+"月"+i+"日"));
			}
		}else{
			for(int i=intBegin;i<=intEnd;i++){
				String s = year+"-"+month+"-"+i;
				list.add(new CommonData(s+"",month+"月"+i+"日"));
			}
		}
		return list;
	}
	public ArrayList getWeekList(String year ,String month){
		ArrayList list = new ArrayList();
		try{
			list.add(new CommonData("-1","全部"));
			if("-1".equals(month)){//只有月不是全部的时候，周才有列表
				return list;
			}
			//只有月份不是空的时候才有具体的周
			int intYear = Integer.parseInt(year);
			int intMonth = Integer.parseInt(month);
			int firstdayofmonth = getWeek(year,month,"1");//本月1号是星期几
			int wholeDayCount = getThisTotalDay(year+"-"+month+"-1");//得到本月一共有多少天
			int lastdayofmonth = getWeek(year,month,String.valueOf(wholeDayCount));//本月最后一天是星期几
			String beginDayOfWeek = "";//本月的第一周的日期
			String endDayOfWeek = "";//本月的最后一周的日期
			if(firstdayofmonth==0){
				beginDayOfWeek = year+"-"+month+"-1";
			}else{
				if(intMonth==1){
					int dayCount = getThisTotalDay((intYear-1)+"-12-1");
					beginDayOfWeek = (intYear-1)+"-12-"+(dayCount-firstdayofmonth+1);
				}else{
					int dayCount = getThisTotalDay(intYear+"-"+(intMonth-1)+"-1");
					beginDayOfWeek = intYear+"-"+(intMonth-1)+"-"+(dayCount-firstdayofmonth+1);
				}
			}
			if(lastdayofmonth==6){
				endDayOfWeek = year+"-"+month+"-"+wholeDayCount;
			}else{
				if(intMonth==12){
					endDayOfWeek = (intYear+1)+"-1-"+(6-lastdayofmonth);
				}else{
					endDayOfWeek = year+"-"+(intMonth+1)+"-"+(6-lastdayofmonth);
				}
			}
			
			int firstSunday = getFirstSunday(year,month);
			int lastSaturday = getLastSaturday(year,month);
			
			//开始输出星期列表(分三部分)
			if(firstSunday!=1){
				String strCode = beginDayOfWeek+"/"+year+"-"+month+"-"+(firstSunday-1);
				String []tempArray = beginDayOfWeek.split("-");
				String strValue = tempArray[1]+"月"+tempArray[2]+"日-"+month+"月"+(firstSunday-1)+"日";
				String strtmp = year+"-"+month+"-"+(firstSunday-1);
				weekMap.put(strtmp, strCode);
				list.add(new CommonData(strCode,strValue));
			}
			for(int i=firstSunday;i<=lastSaturday;i=i+7){
				String strCode = year+"-"+month+"-"+i+"/"+year+"-"+month+"-"+(i+6);
				String strValue = month+"月"+i+"日-"+month+"月"+(i+6)+"日";
				String strtmp = year+"-"+month+"-"+(i+6);
				weekMap.put(strtmp, strCode);
				list.add(new CommonData(strCode,strValue));
			}
			if(lastSaturday!=wholeDayCount){
				String strCode = year+"-"+month+"-"+(lastSaturday+1)+"/"+endDayOfWeek;
				String []tempArray = endDayOfWeek.split("-");
				String strValue = month+"月"+(lastSaturday+1)+"日-"+tempArray[1]+"月"+tempArray[2]+"日";
				String strtmp = endDayOfWeek;
				weekMap.put(strtmp, strCode);
				list.add(new CommonData(strCode,strValue));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	/**得到当天所在的周,生成日列表所需参数*/
	public String getWeekStr(String year,String month,String day){
		String str="";
		if("".equals(month) || "".equals(day)){//如果是切换年或切换月，那么周都显示-1
			str = "-1";
			return str;
		}
		int intYear = Integer.parseInt(year);
		int intMonth = Integer.parseInt(month);
		int intDay = -1;
		if(!"-1".equals(day)){
			String[] array = day.split("-");
			intYear = Integer.parseInt(array[0]);
			intMonth = Integer.parseInt(array[1]);
			intDay = Integer.parseInt(array[2]);
		}
		HashMap map = this.getWeekMap();
		int lastSaturday = getLastSaturday(year,month);
		int dayCount = getThisTotalDay(year+"-"+month+"-1");
		
		if(intDay<=lastSaturday){
			int dayOfWeek = getWeek(year,month,intDay+"");//得到当天是星期几
			int temporary = intDay + (6-dayOfWeek);
			str = (String)map.get(year+"-"+month+"-"+temporary);
		}else{
			int dayOfWeek = getWeek(year,month,dayCount+"");//得到最后一天是星期几
			int temporary = 6-dayOfWeek;
			if(intMonth==12){
				str = (String)map.get((intYear+1)+"-1-"+temporary);
			}else{
				str = (String)map.get(intYear+"-"+(intMonth+1)+"-"+temporary);
			}
		}
		return str;
	}
	public String[] getSql_str(String year,String month,String week,String day,String a0101,String a0100,String nbase){
		//这里的参数day是具体的day，如30，而不是2013-2-30
		String[] str = new String[5];
		StringBuffer bufSelect = new StringBuffer("");
		StringBuffer bufWhere = new StringBuffer("");
		StringBuffer bufForA0101 = new StringBuffer("");
		bufSelect.append("select a0100,nbase,b0110,e0122,a0101");
		bufSelect.append(" from (");
		
		bufSelect.append("select a0100,max(nbase) nbase,max(b0110) b0110,max(e0122) e0122,max(a0101) a0101");
		bufSelect.append(" from per_diary_content");
		
		if("-1".equals(month)){//如果没有月，则只对比年
			bufSelect.append(" where ("+Sql_switcher.year("start_time")+"="+year+" or "+Sql_switcher.year("end_time")+"="+year+")");
		}else{
			if("-1".equals(week)){//如果没有周，则只对比年和月
				int dayCount = getThisTotalDay(year+"-"+month+"-1");
				bufSelect.append(" where "+getMonthDate(year+"-"+month+"-"+dayCount)+">="+Sql_switcher.year("start_time")+"*10000+"+Sql_switcher.month("start_time")+"*100+"+Sql_switcher.day("start_time"));
				bufSelect.append(" and "+getMonthDate(year+"-"+month+"-1")+"<="+Sql_switcher.year("end_time")+"*10000+"+Sql_switcher.month("end_time")+"*100+"+Sql_switcher.day("end_time"));
				
			}else{
				if("-1".equals(day)){//如果没有天，则只对比年和月和周
					String []weekdate = week.split("/");//得到周的日期
					String begin_date = weekdate[0];
					String end_date = weekdate[1];
					bufSelect.append(" where "+getMonthDate(end_date)+">="+Sql_switcher.year("start_time")+"*10000+"+Sql_switcher.month("start_time")+"*100+"+Sql_switcher.day("start_time"));
					bufSelect.append(" and "+getMonthDate(begin_date)+"<="+Sql_switcher.year("end_time")+"*10000+"+Sql_switcher.month("end_time")+"*100+"+Sql_switcher.day("end_time"));
					
				}else{//如果有天则对比年、月、天
					bufSelect.append(" where "+getMonthDate(year+"-"+month+"-"+day)+">="+Sql_switcher.year("start_time")+"*10000+"+Sql_switcher.month("start_time")+"*100+"+Sql_switcher.day("start_time"));
					bufSelect.append(" and "+getMonthDate(year+"-"+month+"-"+day)+"<="+Sql_switcher.year("end_time")+"*10000+"+Sql_switcher.month("end_time")+"*100+"+Sql_switcher.day("end_time"));
					
				}
			}
		}
		bufForA0101.append(bufSelect.toString());
		if(!"".equals(a0101)){
			bufSelect.append(" and a0101 like '%"+a0101+"%'");
		}
		if(!"".equals(a0100)){//如果是从部门人员进去的，员工日历只能看他本人的，而不是考核关系的。
			bufSelect.append(" and a0100='"+a0100+"' and nbase='"+nbase+"'");
			bufForA0101.append(" and a0100='"+a0100+"' and nbase='"+nbase+"'");
		}
		bufSelect.append(" "+getPrivSql());
		bufSelect.append(" group by a0100");
		bufSelect.append(") T");
		bufForA0101.append(" "+getPrivSql());
		bufForA0101.append(" group by a0100");
		bufForA0101.append(") T");
		StringBuffer bufColumns = new StringBuffer();
		bufColumns.append("a0100,nbase,b0110,e0122,a0101");
		str[0]=bufSelect.toString();
		str[1]= bufWhere.toString();
		str[2]= bufColumns.toString();
		str[3]= "";
		str[4]=bufForA0101.toString();
		StringBuffer test = new StringBuffer();//把整个sql语句连接起来，便于测试
		test.append(str[0]);
		test.append(str[1]);
		return str;                   
	}
	/**下拉列表框得到姓名列表**/
	public ArrayList getStaff_namelist(String str){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
			StringBuffer sb = new StringBuffer("");
			sb.append(str);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			while(rs.next()){
				String name = rs.getString("a0101")==null?"":rs.getString("a0101");
				if("".equals(name))
					continue;
				list.add(new CommonData(name,name));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	/*
	 * 输入人员姓名时，支持根据名字和拼音简码查询
	 * day的形式是  2013-1-1
	 * */
	public ArrayList getNameListByInput(String name,String a0100,String nbase,String year,String month,String week,String day){
		ArrayList list = new ArrayList();
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);//拼音简码字段
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try
    	{
    		StringBuffer sb = new StringBuffer("");
    		sb.append("select distinct p.a0101 from per_diary_content p left join usra01 u on p.a0100=u.a0100 ");
    	    
    		if("-1".equals(month)){//如果没有月，则只对比年
    			sb.append(" where ("+Sql_switcher.year("p.start_time")+"="+year+" or "+Sql_switcher.year("p.end_time")+"="+year+")");
    		}else{
    			if("-1".equals(week)){//如果没有周，则只对比年和月
    				int dayCount = getThisTotalDay(year+"-"+month+"-1");
    				sb.append(" where "+getMonthDate(year+"-"+month+"-"+dayCount)+">="+Sql_switcher.year("p.start_time")+"*10000+"+Sql_switcher.month("p.start_time")+"*100+"+Sql_switcher.day("p.start_time"));
    				sb.append(" and "+getMonthDate(year+"-"+month+"-1")+"<="+Sql_switcher.year("p.end_time")+"*10000+"+Sql_switcher.month("p.end_time")+"*100+"+Sql_switcher.day("p.end_time"));
    				
    			}else{
    				if("-1".equals(day)){//如果没有天，则只对比年和月和周
    					String []weekdate = week.split("/");//得到周的日期
    					String begin_date = weekdate[0];
    					String end_date = weekdate[1];
    					sb.append(" where "+getMonthDate(end_date)+">="+Sql_switcher.year("p.start_time")+"*10000+"+Sql_switcher.month("p.start_time")+"*100+"+Sql_switcher.day("p.start_time"));
    					sb.append(" and "+getMonthDate(begin_date)+"<="+Sql_switcher.year("p.end_time")+"*10000+"+Sql_switcher.month("p.end_time")+"*100+"+Sql_switcher.day("p.end_time"));
    					
    				}else{//如果有天则对比年、月、天
    					String realday = (day.split("-"))[2];
    					sb.append(" where "+getMonthDate(year+"-"+month+"-"+realday)+">="+Sql_switcher.year("p.start_time")+"*10000+"+Sql_switcher.month("p.start_time")+"*100+"+Sql_switcher.day("p.start_time"));
    					sb.append(" and "+getMonthDate(year+"-"+month+"-"+realday)+"<="+Sql_switcher.year("p.end_time")+"*10000+"+Sql_switcher.month("p.end_time")+"*100+"+Sql_switcher.day("p.end_time"));
    				}
    			}
    		}
    		
    		if(!"".equals(a0100)){//如果是从部门人员进去的，员工日历只能看他本人的，而不是考核关系的。
    			sb.append(" and p.a0100='"+a0100+"' and p.nbase='"+nbase+"'");
    		}
    		if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )){
    			sb.append(" and (p.a0101 like '%"+name+"%' or u."+pinyin_field+" like '%"+name+"%')");
    		}else{
    			sb.append(" and p.a0101 like '%"+name+"%' ");
    		}
    		sb.append(" "+getPrivSql2());
    	   // sb.append(" order by seq");	
    	    
    	    rs = dao.search(sb.toString());
    	    while (rs.next())
    		{
    	    	String tempname = rs.getString("a0101")==null?"":rs.getString("a0101");
				if("".equals(tempname))
					continue;
				list.add(new CommonData(tempname,tempname));
    		}

    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	}

    	return list;
	}
///////////////////////////////////////////辅助函数/////////////////////////////////////////////	
	/**得到本月总天数**/
	public int getThisTotalDay(String strDate){
		String currentYear = strDate.split("-")[0];
		String currentMonth = strDate.split("-")[1];
		GregorianCalendar ca = new GregorianCalendar();
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date=sdf.parse(currentYear+"-"+currentMonth+"-"+1); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		ca.setTime(date);
		int week = ca.get(Calendar.DAY_OF_WEEK)-1;//该月1号是周几
		WeekUtils wu = new WeekUtils();
		String tmp[] = wu.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth)).split("-");
		int tmpdayCount = Integer.parseInt(tmp[2]);//该月有几天
		return tmpdayCount;
	}
	/**得到指定天的日期  方便sql比较*/
	public long getMonthDate(String strDate){
		long str = -1;
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date=sdf.parse(strDate); 
			sdf=new SimpleDateFormat("yyyyMMdd");
			str = Long.parseLong(sdf.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return str;
	}
	/**得到是星期几*/
	public int getWeek(String currentYear,String currentMonth,String currentDay){
		int week = -1;
		GregorianCalendar ca = new GregorianCalendar();
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date=sdf.parse(currentYear+"-"+currentMonth+"-"+currentDay); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		ca.setTime(date);
		week = ca.get(Calendar.DAY_OF_WEEK)-1;
		return week;
	}
	/**得到本月第一个星期天是几号*/
	public int getFirstSunday(String year,String month){
		int week = getWeek(year,month,"1");//得到本月1号是星期几
		return (1+(7-week)%7);
	}
	/**得到本月最后一个星期六是几号*/
	public int getLastSaturday(String year,String month){
		int daycount = getThisTotalDay(year+"-"+month+"-1");//得到本月有几天
		int week = getWeek(year,month,String.valueOf(daycount));//得到本月最后一天是星期几
		return daycount-week-1;
	}
	public HashMap getWeekMap() {
		return weekMap;
	}
	public void setWeekMap(HashMap weekMap) {
		this.weekMap = weekMap;
	}
	
}
