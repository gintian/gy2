package com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarBo {
	Connection conn = null;
	UserView userView = null;
	String a0100 = "";
	String nbase = "";
	
	public CalendarBo(Connection conn,UserView userView,String a0100,String nbase){
		this.conn = conn;
		this.userView = userView;
		this.a0100 = a0100;//得到人员编码
		this.nbase = nbase;
		
	}
	////////////////////////////////////增加领导痕迹////////////////////////////////////////////
	/**
	 * 插入领导痕迹
	 * 记录存放的格式为：a0100`nbase`unit`depart`name`comparetime`starttime,a0100`nbase`unit`depart`name`comparetime`starttime,
	 * 任何一项内容都不可能为空。如果为空，则显示-1
	 * */
	public void insertLeaderTrace(String leader_a0100,String leader_nbase,String leader_unit,String leader_depart,String leader_name,String comparetime,String starttime,String year,String month,String day){
		//若张三是李四的领导，张三一天之内如果查看了多次，则只记录一次
		RowSet rs = null;
		try{
			String leaderComment = "";//领导意见
			String p0100 = "";//表的主键
			boolean isInsert = true;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sb = new StringBuffer();
			String combinedate = year+"-"+month+"-"+day;
			sb.append("select P0100,p0113 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+getMonthDate2(combinedate)+" and a0100='"+a0100+"' and nbase='"+nbase+"'");
			rs = dao.search(sb.toString());
			if(rs.next()){//有记录，有且仅有一条记录
				p0100 = rs.getString("P0100");
				leaderComment = Sql_switcher.readMemo(rs, "P0113")==null?"":Sql_switcher.readMemo(rs, "P0113");
			}
			String[] outerArray = leaderComment.split(",");
			int count = outerArray.length;
			if(count>0){
				for(int i=0;i<count;i++){
					if("".equals(outerArray[i])){
						continue;
					}
					String[] innerArray = outerArray[i].split("`");
					String a0100 = innerArray[0];
					String nbase = innerArray[1];
					String compare = innerArray[5];
					if(compare.equals(comparetime) && leader_a0100.equals(a0100) && leader_nbase.equals(nbase)){
						//如果这个领导在那一天已经添加过一次记录了
						isInsert = false;
						break;
					}
				}
			}
			if(isInsert){//如果能插入  则按顺序插入  a0100`nbase`unit`depart`name`comparetime`starttime   
				String tempstr = ","+leader_a0100+"`"+leader_nbase+"`"+isEmptyOrNull(leader_unit)+"`"+isEmptyOrNull(leader_depart)+"`"+isEmptyOrNull(leader_name)+"`"+comparetime+"`"+starttime;
				leaderComment+=tempstr;
				RecordVo vo=new RecordVo("p01");
			      vo.setString("p0100",p0100);
			      vo.setString("p0113",leaderComment);
			      dao.updateValueObject(vo);
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
	}
	/**
	 * 显示领导痕迹
	 * 
	 * **/
	public String showLeaderTrace(String year,String month,String day){
		StringBuffer showComment = new StringBuffer("");
		RowSet rs = null;
		try{
			String leaderComment = "";//领导意见
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sb = new StringBuffer();
			String combinedate = year+"-"+month+"-"+day;
			sb.append("select p0113 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+getMonthDate2(combinedate)+" and a0100='"+a0100+"' and nbase='"+nbase+"'");
			rs = dao.search(sb.toString());
			if(rs.next()){
				leaderComment = Sql_switcher.readMemo(rs, "p0113")==null?"":Sql_switcher.readMemo(rs, "p0113");
			}
			String[] outerArray = leaderComment.split(",");
			int count = outerArray.length;
			if(count>0){
				
				for(int i=0;i<count;i++){
					if("".equals(outerArray[i])){
						continue;
					}
					showComment.append("<table width='100%'>");
					showComment.append("<tr><td>");
					String[] innerArray = outerArray[i].split("`");
					//a0100`nbase`unit`depart`name`comparetime`starttime
					String unit = innerArray[2];
					String depart = innerArray[3];
					String name = innerArray[4];
					String starttime = innerArray[6];
					String tmpstr = AdminCode.getCodeName("UM",depart)+"  "+ name+" "+starttime+" 查看。";
					showComment.append(tmpstr);
					showComment.append("</td></tr>\r\n");
				}
				if(showComment.length()>0)
					showComment.append("</table>");
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
		return showComment.toString();
	}
	//////////////////////////////////////日报////////////////////////////////////////////////
	/**输出日历*/
	public String[] getCalendarDayHtml(String year,String month,String day){
		String[] strarray = new String[2];
		StringBuffer html = new StringBuffer();
//		html.append("<input type=\"hidden\" value=\""+year+"\" id=\"fromyear\">");
//		html.append("<input type=\"hidden\" value=\""+month+"\" id=\"frommonth\">");
//		html.append("<input type=\"hidden\" value=\""+day+"\" id=\"fromday\">");
		String jinnian = "";
		String jinyue = "";
		String jintian = "";
		GregorianCalendar ca = new GregorianCalendar();
		jinnian = String.valueOf(ca.get(Calendar.YEAR));
		jinyue = String.valueOf(ca.get(Calendar.MONTH)+1);
		jintian = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
		boolean isJintian = false;
		if(jinnian.equals(year) && jinyue.equals(month)){//是今年今月的今天。
			isJintian = true;
		}
		
		int frontSpaceCount = getWeek(year,month,"1");//开头空几个字符
		String[] temp = computeLinecount(year,month).split("`");
		int lineCount = Integer.parseInt(temp[0]);//得到日历有几行
		int dayCount = Integer.parseInt(temp[1]);//得到日历有几行
		int rearSpacecount = 7*lineCount-frontSpaceCount-dayCount;//后面空几个字符
		int index = 0;//如果七条数据则换行
		
		html.append("<div class=\"epm-index-left-top\">\r\n");
		
		html.append("<dl>\r\n");
		html.append("<dt>");
		html.append(day);
		html.append("</dt>\r\n");
		
		html.append("<dd>");
		String contemporaryweek = this.getStrWeek(year, month, day);
		html.append(year+"年"+month+"月"+day+"日");
		html.append("</dd>\r\n");
		html.append("<dd>");
		html.append(contemporaryweek);
		html.append("</dd>\r\n");
		html.append("</dl>\r\n");
		html.append("<div class=\"epm-rili\">\r\n");
		html.append("<table border=\"0\">\r\n");
		html.append("<tr>\r\n");
		html.append("<td width=\"7%\">周日</td><td width=\"7%\">周一</td><td width=\"7%\">周二</td><td width=\"7%\">周三</td><td width=\"7%\">周四</td><td width=\"7%\">周五</td><td width=\"7%\">周六</td>");
		html.append("</tr>\r\n");
		
		html.append("<tr>");
		//输出前面的空格
		for(int i=0;i<frontSpaceCount;i++){
			html.append("<td>");
			html.append("&nbsp;");
			html.append("</td>");
			index++;
		}
		//输出日期
		int linecount = 1;
		for(int j=1;j<=dayCount;j++){
			html.append("<td>");
			html.append(" <a href='javascript:selectNewDate("+year+","+month+","+j+")'");
			if(day.equals(String.valueOf(j))){//如果正好是传过来的日期
				html.append(" class=\"epm-rili-ersi\"");
			}
			if(jintian.equals(String.valueOf(j)) && isJintian){//让今天变为蓝色
				html.append(" style='color:#2f60dc'");
			}
			html.append(">");
			html.append(j);
			html.append("</a>");
			html.append("</td>");
			index++;
			if(index%7==0){
				linecount++;
				if(j<dayCount){
					html.append("</tr>\r\n");
					html.append("<tr>");
				}else if(j==dayCount){
					html.append("</tr>\r\n");
				}
			}
		}
		//输出后面的空格
		if(rearSpacecount>0){
			for(int k=0;k<rearSpacecount;k++){
				html.append("<td>");
				html.append("&nbsp;");
				html.append("</td>");
			}
			html.append("</tr>");
		}else{
			linecount--;
		}
		html.append("</table>\r\n");
		html.append("</div>\r\n");
		html.append("</div>\r\n");
		strarray[0] = html.toString();
		strarray[1] = linecount+"";
		return strarray;
	}
	
	/**输出记录列表*/
	public String getWorkRecordHtml(String year,String month,String day){
		StringBuffer workRecordHtml = new StringBuffer();
		RowSet rs=null;
		try{
			String jinnian = "";
			String jinyue = "";
			String jintian = "";
			GregorianCalendar ca = new GregorianCalendar();
			jinnian = String.valueOf(ca.get(Calendar.YEAR));
			jinyue = String.valueOf(ca.get(Calendar.MONTH)+1);
			jintian = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
			boolean isJintian = false;
			if(jinnian.equals(year) && jinyue.equals(month)){//是今年今月的今天。
				isJintian = true;
			}
			
			String startMonthDate = getMonthDate(year,month,"1");//当月第一天
			String[] temp = computeLinecount(year,month).split("`");
			String endMonthDate = getMonthDate(year,month,temp[1]);//当月最后一天
			
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("select pdc.p0100,pdc.record_num,pdc.title,pdc.start_time,pdc.end_time,"+Sql_switcher.year("pdc.start_time")+" currentYear,"+Sql_switcher.month("pdc.start_time")+" currentMonth");
			sbQuery.append(" from per_diary_content pdc left join p01 p on pdc.p0100=p.p0100");
			sbQuery.append(" where "+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+"<=");
			sbQuery.append(" "+endMonthDate+" and "+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+">="+startMonthDate+" and pdc.a0100='"+a0100+"' and pdc.nbase='"+nbase+"'");
			sbQuery.append(" and p.state=0");
			sbQuery.append(" order by pdc.start_time,pdc.title");
			//遍历数据库
			ArrayList list = new ArrayList();
			ContentDAO dao=new ContentDAO(this.conn);
			
			rs = dao.search(sbQuery.toString());
			while(rs.next()){
				HashMap map = new HashMap();
				String p0100 = rs.getString("p0100");
				String record_num = rs.getString("record_num");
				String title = rs.getString("title")==null?"":rs.getString("title");
				Date start_time = rs.getDate("start_time");
				Date end_time = rs.getDate("end_time");
				String currentYear = rs.getString("currentYear");
				String currentMonth = rs.getString("currentMonth");
				map.put("p0100", p0100);
				map.put("record_num", record_num);
				map.put("title", title);
				map.put("start_time", start_time);
				map.put("end_time", end_time);
				map.put("currentYear", currentYear);
				map.put("currentMonth", currentMonth);
				list.add(map);
			}
			
			//得到工作记录的数据
			ArrayList recordList = getRecordList(list,year,month);
			
			//生成工作记录的html
			int recordCount = recordList.size();
			
			workRecordHtml.append("<div id='recordlist' class=\"epm-index-left-bottom\">\r\n");
			int grossRecordCount = 1;//总共要显示多少条记录
			if(recordCount==0){//如果没有工作记录,则显示6条空记录
				workRecordHtml.append("<div class=\"fillborder\"></div>\r\n");//就是为了填充div上面的空格
				workRecordHtml.append("无内容");
			}else{
				for (int i=0;i<recordCount;i++){
					//recordList 中依次存放着 几号、星期几、日期、list。list中存放着map，map中存放着记录号的id
					ArrayList dataList = (ArrayList)recordList.get(i);
					String thisday = (String)dataList.get(0);
					String strWeek = (String)dataList.get(1);
					String strTime = (String)dataList.get(2);
					ArrayList tmplist = (ArrayList)dataList.get(3);
					
					workRecordHtml.append("<div class=\"epm-left-bottom-one\">\r\n");
					if(jintian.equals(thisday) && isJintian){
						workRecordHtml.append("<div class=\"epm-left-bottom-yi-hand2\" onclick=\"selectNewDate("+year+","+month+","+thisday+");\"><span>"+strWeek+"</span>"+strTime+"</div>\r\n");
					}else{
						workRecordHtml.append("<div class=\"epm-left-bottom-yi-hand\" onclick=\"selectNewDate("+year+","+month+","+thisday+");\"><span>"+strWeek+"</span>"+strTime+"</div>\r\n");
					}
					workRecordHtml.append("<div class=\"epm-left-bottom-er\">\r\n");
					workRecordHtml.append("<ul>\r\n");
					int tmpcount = tmplist.size();//存放着这一天所有的记录
					for(int j=0;j<tmpcount;j++){
						HashMap tmpmap = (HashMap)tmplist.get(j);
						String p0100 = (String)tmpmap.get("p0100");
						String record_num = (String)tmpmap.get("record_num");
						String title = (String)tmpmap.get("title");
						if(j==tmpcount-1){
							workRecordHtml.append("<li class=\"epm-tshu-li\">"+"<a href=\"javascript:showRecord("+year+","+month+","+thisday+","+p0100+","+record_num+")\">"+title+"</a>"+"</li>\r\n");
						}else{
							workRecordHtml.append("<li>"+"<a href=\"javascript:showRecord("+year+","+month+","+thisday+","+p0100+","+record_num+")\">"+title+"</a>"+"</li>\r\n");
						}
					}
					workRecordHtml.append("</ul>\r\n");
					workRecordHtml.append("</div>\r\n");//end of epm-left-bottom-er
					workRecordHtml.append("</div>\r\n");//end of epm-left-bottom-one
					workRecordHtml.append("<div class=\"bh-clear\"></div>");
				}
//				//如果记录条数不够，则用空行补充
//				for(int k=recordCount;k<grossRecordCount;k++){
//					workRecordHtml.append("<div class=\"epm-left-bottom-one\">\r\n");
//					workRecordHtml.append("<div class=\"epm-left-bottom-yi\"><span>&nbsp;</span></div>\r\n");
//					workRecordHtml.append("<div class=\"epm-left-bottom-er2\">\r\n");
//					workRecordHtml.append("<ul>\r\n");
//					//workRecordHtml.append("<li></li>\r\n");
//					workRecordHtml.append("<li class=\"epm-tshu-li\"></li>\r\n");
//					workRecordHtml.append("</ul>\r\n");
//					workRecordHtml.append("</div>\r\n");
//					workRecordHtml.append("</div>\r\n");
//					workRecordHtml.append("<div class=\"bh-clear\"></div>\r\n");
//				}
			}
			workRecordHtml.append("</div>\r\n");
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
		return workRecordHtml.toString();
	}
	
	/**输出具体的事件**/
	public String[] getRecordShowHtml(String year,String month,String day,String frompage,String fromyear,String frommonth,String fromday,String staff_url,String isOwner,String combineid,String linecount){
		boolean isGw = false;//判断是否是国网 因为时间段的处理事件不同。国网是用列表输出事件。而不是国网时用时间轴输出事件
		String clientName = SystemConfig.getPropertyValue("clientName");
		if(clientName!=null&& "gw".equalsIgnoreCase(clientName)){
			isGw=true;
		}
		String temporarymonth = month;
		String temporaryday = day;
		if(month.length()==1){
			temporarymonth="0"+month;
		}
		if(day.length()==1){
			temporaryday="0"+day;
		}
		String temporarydate = year+"-"+temporarymonth+"-"+temporaryday;//当新增事件时传递的参数
		
		if(isOwner==null)//因为有时候不给我传递isOwner
			isOwner = "0";
		String[] str = new String[5];
		StringBuffer recordShowHtml = new StringBuffer();
		String jsonstr = "";
		String leader = "";//领导批示
		String wholeScroll = "0";
		String axle = "0";
		RowSet rs=null;
		try{
			ArrayList allRecordList = new ArrayList();//allRecordList里存放着许多小list，每个小list存放着record_num,p0100,title,start,end,average,zindex,indent,sequence,lindent
			HashMap stategridmap = new HashMap();//该map用来处理时间段事件。键为起始时间的小时，键值为大list。大list中存放着许多小list，小list中存放着记录的详细信息
			String strDayCount = computeLinecount(year,month).split("`")[1];
			int dayCount = Integer.parseInt(strDayCount);
			
			int jintian = -1;
			GregorianCalendar ca = new GregorianCalendar();
			jintian = ca.get(Calendar.DAY_OF_MONTH);//得到今天的日期
			
			ArrayList diaryWholeList = new ArrayList();//全天的事件
			ContentDAO dao=new ContentDAO(this.conn);
			
			StringBuffer sb_query = new StringBuffer();
			String thisDate = getMonthDate(year,month,day);
			if(Sql_switcher.searchDbServer()==Constant.MSSQL){
				sb_query.append("select pdc.record_num,pdc.p0100,pdc.title,pdc.type,pdc.content,pdc.start_time start_time,pdc.end_time end_time,pdc.start_time start_time2,pdc.end_time end_time2");
			}
			else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				sb_query.append("select pdc.record_num,pdc.p0100,pdc.title,pdc.type,pdc.content,to_char(pdc.start_time,'yyyy-MM-dd hh24:mi:ss') start_time,to_char(pdc.end_time,'yyyy-MM-dd hh24:mi:ss') end_time,pdc.start_time start_time2,pdc.end_time end_time2");
			}
			sb_query.append(" from per_diary_content pdc left join p01 p on pdc.p0100=p.p0100");
			sb_query.append(" where "+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+"<=");
			sb_query.append(" "+thisDate+" and "+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+">="+thisDate+" and pdc.a0100='"+a0100+"' and pdc.nbase='"+nbase+"'");
			sb_query.append(" and p.state=0 ");
			sb_query.append(" order by pdc.start_time,pdc.title");
			rs = dao.search(sb_query.toString());
			while(rs.next()){
				
				String record_num = rs.getString("record_num");
				String p0100 = rs.getString("p0100");
				String title = rs.getString("title")==null?"":rs.getString("title");
				String type = rs.getString("type")==null?"0":rs.getString("type");
				String content = Sql_switcher.readMemo(rs,"content")==null?"":Sql_switcher.readMemo(rs,"content");
				Date startTime = rs.getDate("start_time2");
				Date endTime = rs.getDate("end_time2");
				String start_time = standardDate(startTime);
				String end_time = standardDate(endTime);
				String partContent = "";
				if(content.length()>30){
					partContent = content.substring(0, 30)+"......";
				}else{
					partContent = content;
				}
				
				if("0".equals(type)){//如果是全天事件
					HashMap map = new HashMap();
					map.put("record_num", record_num);
					map.put("p0100", p0100);
					map.put("title", title);
					map.put("type", type);
					map.put("content", content);
					map.put("start_time", start_time);
					map.put("end_time", end_time);
					map.put("partContent", partContent);
					diaryWholeList.add(map);
				}else{//如果是时间段事件
					//如果不是国家电网，则执行以下操作。该操作是为了生成json串
					SimpleDateFormat tempsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date temporarydate1 = tempsdf.parse(rs.getString("start_time"));//start_time和start_time2不同。start_time可以取到时间中的小时和分钟
					Date temporarydate2 = tempsdf.parse(rs.getString("end_time"));
					GregorianCalendar tempca = new GregorianCalendar();
					tempca.setTime(temporarydate1);
					int startHour = tempca.get(Calendar.HOUR_OF_DAY);
					int startMinute = tempca.get(Calendar.MINUTE);//这两个属性是新增的
					float starthourminute = (float)(startHour+startMinute/60.0);
					
					tempca.setTime(temporarydate2);
					int endHour = tempca.get(Calendar.HOUR_OF_DAY);
					int endMinute = tempca.get(Calendar.MINUTE);//这两个属性是新增的
					float endhourminute = (float)(endHour+(endMinute)/60.0);
					if(!isGw){
						ArrayList innerList = new ArrayList();//record_num,p0100,title,start,end,average,zindex,indent,sequence,lindent
						innerList.add(record_num);
						innerList.add(p0100);
						innerList.add(title);
						innerList.add(starthourminute+"");
						innerList.add(endhourminute+"");
						innerList.add("1");//average
						innerList.add("0");//zindex
						innerList.add("0");//indent
						innerList.add("1");//sequence
						innerList.add("0");//lindent
						allRecordList.add(innerList);
					}else{
						//如果是国家电网，则要生成map。map中键是起始时间的小时，键值是大list。大list中存放着许多小list，每个小list存放着事件的详细信息
						String strStartHour = String.valueOf(startHour);
						ArrayList innerlist = new ArrayList();
						innerlist.add(record_num);
						innerlist.add(p0100);
						innerlist.add(title);
						if(stategridmap.get(strStartHour)!=null){
							ArrayList stOuterList = (ArrayList)stategridmap.get(strStartHour);
							stOuterList.add(innerlist);
							stategridmap.put(strStartHour, stOuterList);
						}else{
							ArrayList stOuterList = new ArrayList();
							stOuterList.add(innerlist);
							stategridmap.put(strStartHour, stOuterList);
						}
					}
				}
			}//数据库查询的while终止
			
			//开始输出html
			int diaryWholeCount = diaryWholeList.size();//全天工作记录的条数
			//输出“今天”这个按钮
			recordShowHtml.append("<div class=\"epm-jintian\">\r\n");
			recordShowHtml.append("<table border=\"0\">\r\n");
			recordShowHtml.append("<tr>");
			recordShowHtml.append("<td>");
			if(!"1".equals(isOwner))
				recordShowHtml.append("<input type=\"button\" value=\"新增\" class=\"epm-j-xinz\" onclick=\"showQueryDiv('add');\"/>&nbsp;&nbsp;");
			recordShowHtml.append("<input type=\"button\" value=\"查询\" class=\"epm-j-xinz\" onclick=\"showSearchDiv('"+frompage+"','query');\"/>&nbsp;&nbsp;");
			recordShowHtml.append("<input type=\"button\" value=\"导出\" class=\"epm-j-xinz\" onclick=\"exportDiary('1','"+year+"','"+month+"','"+day+"','"+year+"','"+month+"','"+day+"');\">&nbsp;&nbsp;");
			if(frompage!=null && !"-1".equals(frompage) && !"0".equals(frompage) && !"-2".equals(frompage)){//不是从主页或菜单进去的
				if("4".equals(frompage)){//如果是员工日志
					recordShowHtml.append("<input type=\"button\" value=\"返回\" class=\"epm-j-xinz\" onclick=\"returnStaffDiary('"+staff_url+"');\"/>&nbsp;&nbsp;");
				}else{
					recordShowHtml.append("<input type=\"button\" value=\"返回\" class=\"epm-j-xinz\" onclick=\"returnOriginal("+fromyear+","+frommonth+","+fromday+","+frompage+");\"/>&nbsp;&nbsp;");
				}
			}
			recordShowHtml.append("</td>\r\n");
			
			recordShowHtml.append("<td><input type=\"button\" class=\"epm-zuo\" onclick=\"selectNewDate("+getPrevious(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),1)+");\"/></td>\r\n");
			recordShowHtml.append("<td><a href=\"javascript:selectNewDate(-1,-1,-1)\">今天</a></td>\r\n");
			recordShowHtml.append("<td><input type=\"button\" class=\"epm-you\" onclick=\"selectNewDate("+getNext(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),1)+");\"/></td>\r\n");   
			recordShowHtml.append("</tr>\r\n");  
			recordShowHtml.append("</table>\r\n");
			recordShowHtml.append("</div>\r\n");
			recordShowHtml.append("<div class=\"bh-space\"></div>\r\n");
			
			
			recordShowHtml.append("<div class=\"epm-jilu\">\r\n");
			//先画出全天事件
			recordShowHtml.append("<div id=\"wholeevent\" ");
			if("4".equals(linecount) || "5".equals(linecount)){
				recordShowHtml.append("class=\"epm-jilu-one\">\r\n");
			}else if("6".equals(linecount)){
				recordShowHtml.append("class=\"epm-jilu-one-2\">\r\n");
			}
			recordShowHtml.append("<h2>全天事件</h2>\r\n");
			recordShowHtml.append("<div class=\"epm-gzjl\">\r\n");
			if(diaryWholeCount==0){
				if("4".equals(linecount) || "5".equals(linecount)){
					recordShowHtml.append("<div class=\"epm-jilu-space\"");
				}else if("6".equals(linecount)){
					recordShowHtml.append("<div class=\"epm-jilu-space-2\"");
				}
				recordShowHtml.append(" style=\"cursor:pointer;\" onclick=\"showQueryDiv('add','addwhole','"+temporarydate+"','0','0','"+temporarydate+"','17','59')\">\r\n");
				recordShowHtml.append("</div>\r\n");
			}else{
				for(int i=0;i<diaryWholeCount;i++){
					HashMap tmpMap = (HashMap)diaryWholeList.get(i);
					String p0100_recordnum = (String)tmpMap.get("p0100")+(String)tmpMap.get("record_num");
					if(!"".equals(combineid) && combineid.equals(p0100_recordnum)){
						wholeScroll = ((50*i))+"";
					}
					recordShowHtml.append("<div class=\"epm-all-quantian\" >\r\n");
					recordShowHtml.append("<ul id='"+p0100_recordnum+"' style=\"cursor:pointer;\" onclick=\"tr_click('"+tmpMap.get("p0100")+tmpMap.get("record_num")+"');showDetail("+isOwner+","+tmpMap.get("p0100")+","+tmpMap.get("record_num")+");\">\r\n");
					recordShowHtml.append("<li><span>全天</span>"+tmpMap.get("title")+"</li>\r\n");
					recordShowHtml.append("<li class=\"epm-con-li\">&nbsp;&nbsp;&nbsp;&nbsp;"+tmpMap.get("partContent")+"</li>\r\n");
					recordShowHtml.append("</ul>\r\n");
					recordShowHtml.append("</div>\r\n");
					recordShowHtml.append("<div class=\"bh-clear\"></div>");
				}
				//在全天事件最底部加上这块div，让用户点击可以增加新事件
				recordShowHtml.append("<div class=\"epm-all-quantian\" style=\"cursor:pointer;\" onclick=\"showQueryDiv('add','addwhole','"+temporarydate+"','0','0','"+temporarydate+"','17','59');\">\r\n");
				recordShowHtml.append("</div>\r\n");
			}
			
			recordShowHtml.append("</div>\r\n");//epm-gzjl的结束
			recordShowHtml.append("</div>\r\n");//epm-jilu-one的结束
			recordShowHtml.append("</div>\r\n");//jilu
			
			////再画时间段事件  
			String[] strtemporary = new String[2];
			if(!isGw){//如果不是国家电网的需求
				modifyRecordList(allRecordList);//将start,end,average，average,zindex,indent,sequence,lindent重新整理
				strtemporary = exportJson(allRecordList,combineid);//输出json串
			}else{
				strtemporary = createGwHtml(stategridmap,temporarydate,isOwner,combineid);
			}
			jsonstr = strtemporary[0];
			axle = strtemporary[1];
			//System.out.println(jsonstr);
			str[0] = recordShowHtml.toString();//全天事件
			str[1] = jsonstr;//时间段事件    当客户不是国家电网时，该变量代表json串，在前台生成div。当客户是国家电网时，jsonstr代表时间段事件的html
			str[2] = leader;//领导痕迹
			str[3] = wholeScroll;//全天事件的滚动条位置
			str[4] = axle;//时间段事件的滚动条位置
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
		return str;
	}
	
	/////////////////////////////////////周报////////////////////////////////////////////
	
	/**输出表格**/
	public String[] getTableHtml(String year,String month,String day,String staff_url,String frompage){
		boolean isGw = false;
		String clientName = SystemConfig.getPropertyValue("clientName");
		if(clientName!=null&& "gw".equalsIgnoreCase(clientName)){
			isGw=true;
		}
		String[] str = new String[4];
		StringBuffer tableHtml = new StringBuffer();//用于输出表头
		StringBuffer weekHtml = new StringBuffer();//用于输出星期
		tableHtml.append("<input type=\"hidden\" value=\""+year+"\" id=\"fromyear\">");
		tableHtml.append("<input type=\"hidden\" value=\""+month+"\" id=\"frommonth\">");
		tableHtml.append("<input type=\"hidden\" value=\""+day+"\" id=\"fromday\">");
		String jinnian = "";
		String jinyue = "";
		String jintian = "";
		GregorianCalendar tmpca = new GregorianCalendar();
		jinnian = String.valueOf(tmpca.get(Calendar.YEAR));
		jinyue = String.valueOf(tmpca.get(Calendar.MONTH)+1);
		jintian = String.valueOf(tmpca.get(Calendar.DAY_OF_MONTH));
		boolean isJintian = false;
		if(jinnian.equals(year) && jinyue.equals(month)){//是今年今月的今天。
			isJintian = true;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//先得到顶部日期（strDate  和  beginDate与endDate   二选一），并得到表头数据
		String strDate = "";//存储顶部日期
		String[] array = new String[7];//依次存储     天、周、年、月
		String beginDate = "";//本周的第一天所在的日期
		String endDate = "";//本周的最后一天所在的日期
		int firstDay = -1;//本周的第一天
		int lastDay = -1;//本周的最后一天
		//int totalDay = -1;//总共有多少天
		
		
		int intYear = Integer.parseInt(year);
		int intMonth = Integer.parseInt(month);
		int intDay = Integer.parseInt(day);
		int thisweek = getWeek(year,month,day);//这一天是周几
		
		int minus = intDay-thisweek;
		if(minus<=0){//则到上个月去找本周的第一天
			if(intMonth>1){//如果月份不是1月
				strDate = year+"年"+(intMonth-1)+"月至"+year+"年"+month+"月";
				//先得到上个月有多少天
				String strDayCount = computeLinecount(year,String.valueOf((intMonth-1))).split("`")[1];
				int dayCount = Integer.parseInt(strDayCount);
				beginDate = year+"-"+(intMonth-1)+"-"+(intDay-thisweek+dayCount);
				endDate = year+"-"+month+"-"+(intDay+(6-thisweek));
				firstDay = dayCount-(thisweek-intDay);
				lastDay = intDay+(6-thisweek);
				//totalDay = dayCount;
				int j=0;
				for(int i=firstDay;i<=dayCount;i++){
					array[j]=i+"`"+getStrWeek(year,String.valueOf(intMonth-1),String.valueOf(i))+"`"+year+"`"+(intMonth-1);
					j++;
				}
				for(int k=1;k<=lastDay;k++){
					array[j] = k+"`"+getStrWeek(year,month,String.valueOf(k))+"`"+year+"`"+month;
					j++;
				}
			}else{//如果月份是1月
				strDate = (intYear-1)+"年12月至"+year+"年1月";
				//先得到上个月有多少天
				String strDayCount = computeLinecount(String.valueOf((intYear-1)),"12").split("`")[1];
				int dayCount = Integer.parseInt(strDayCount);
				beginDate = (intYear-1)+"-12-"+(intDay-thisweek+dayCount);
				endDate = year+"-"+month+"-"+(intDay+(6-thisweek));
				firstDay = dayCount-(thisweek-intDay);
				lastDay = intDay+(6-thisweek);
				//totalDay = dayCount;
				int j=0;
				for(int i=firstDay;i<=dayCount;i++){
					array[j]=i+"`"+getStrWeek(String.valueOf(intYear-1),"12",String.valueOf(i))+"`"+(intYear-1)+"`"+12;
					j++;
				}
				for(int k=1;k<=lastDay;k++){
					array[j] = k+"`"+getStrWeek(year,"1",String.valueOf(k))+"`"+year+"`"+1;
					j++;
				}
			}
		}else{//在本月或下个月去找本周的最后一天
			String strDayCount = computeLinecount(year,month).split("`")[1];
			int dayCount = Integer.parseInt(strDayCount);
			//totalDay = dayCount;
			strDate = year+"年"+month+"月";
			beginDate = year+"-"+month+"-"+minus;//minus=intDay-thisweek
			firstDay = minus;
			if((intDay+6-thisweek)>dayCount){//则从下个月找
				if(intMonth==12){
					endDate = (intYear+1)+"-1-"+(intDay+6-thisweek-dayCount);
					lastDay = intDay+6-thisweek-dayCount;
					int j = 0;
					for(int i=firstDay;i<=dayCount;i++){
						array[j]=i+"`"+getStrWeek(year,month,String.valueOf(i))+"`"+year+"`"+month;
						j++;
					}
					for(int k=1;k<=lastDay;k++){
						array[j]=k+"`"+getStrWeek(String.valueOf((intYear+1)),"1",String.valueOf(k))+"`"+(intYear+1)+"`"+1;
						j++;
					}
				}else{
					endDate = year+"-"+(intMonth+1)+"-"+(intDay+6-thisweek-dayCount);
					lastDay = intDay+6-thisweek-dayCount;
					int j = 0;
					for(int i=firstDay;i<=dayCount;i++){
						array[j]=i+"`"+getStrWeek(year,month,String.valueOf(i))+"`"+year+"`"+month;
						j++;
					}
					for(int k=1;k<=lastDay;k++){
						array[j]=k+"`"+getStrWeek(year,String.valueOf((intMonth+1)),String.valueOf(k))+"`"+year+"`"+(intMonth+1);
						j++;
					}
				}
				
			}else{
				endDate = year+"-"+month+"-"+(intDay+6-thisweek);
				lastDay = intDay+6-thisweek;
				int j = 0;
				for(int i=firstDay;i<=lastDay;i++){
					array[j]=i+"`"+getStrWeek(year,month,String.valueOf(i))+"`"+year+"`"+month;
					j++;
				}
			}
		}
		
		//开始输出html
		tableHtml.append("<div class=\"epm-j-h2\">\r\n");//epm-j-h2是别人CSS里的样式。我们的按钮要统一样式，所以调用了别人的CSS。这样维护比较方便
		tableHtml.append("<span style=\"width:6%;float:left;\"\r\n></span><span class='zreed' style='float:left;'>"+exchangeStrDate(beginDate)+"至"+exchangeStrDate(endDate)+"</span>");
		//tableHtml.append("<h2>"+exchangeStrDate(beginDate)+"至"+exchangeStrDate(endDate)+"</h2>\r\n");
		tableHtml.append("<span class=\"epm-jintian\">\r\n");
		tableHtml.append("<table border=\"1\">\r\n");
		tableHtml.append("<tr>");
		tableHtml.append("<td>");
		if(!"1".equals(frompage)){//只有不是从员工日志进去的时候才有新增
			tableHtml.append("<input type=\"button\" value=\"新增\" class=\"epm-j-xinz\" onclick=\"showQueryDiv('add');\"/>&nbsp;&nbsp;");
		}
		tableHtml.append("<input type=\"button\" value=\"查询\" class=\"epm-j-xinz\" onclick=\"showSearchDiv('1','query');\"/>&nbsp;&nbsp;");
		String[] tmpbeginDate=beginDate.split("-");
		String syear=tmpbeginDate[0];
		String smonth=tmpbeginDate[1];
		String sday=tmpbeginDate[2];
		String[] tmpendDate=endDate.split("-");
		String eyear=tmpendDate[0];
		String emonth=tmpendDate[1];
		String eday=tmpendDate[2];
		tableHtml.append("<input type=\"button\" value=\"导出\" class=\"epm-j-xinz\" onclick=\"exportDiary('2','"+syear+"','"+smonth+"','"+sday+"','"+eyear+"','"+emonth+"','"+eday+"');\">&nbsp;&nbsp;");
		if("1".equals(frompage)){//只有从员工日志进去的时候才有返回
			tableHtml.append("<input type=\"button\" value=\"返回\" class=\"epm-j-xinz\" onclick=\"returnStaffDiary('"+staff_url+"')\"/>&nbsp;&nbsp;");
		}
		tableHtml.append("</td>\r\n");
		tableHtml.append("<td><input type=\"button\" class=\"epm-zuo\" onclick=\"changeWeekRecord("+getPrevious(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),7)+");\"/></td>\r\n");
		tableHtml.append("<td><a href=\"javascript:changeWeekRecord(-1,-1,-1)\">今天</a></td>\r\n");
		tableHtml.append("<td><input type=\"button\" class=\"epm-you\" onclick=\"changeWeekRecord("+getNext(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),7)+");\"/></td>\r\n");
		tableHtml.append("</tr>\r\n");
		tableHtml.append("</table>\r\n");
		tableHtml.append("</span>\r\n");
		tableHtml.append("</div>\r\n");//到此为止，便输出了日期和顶部按钮
		
		

		//输出从周日到周一
		weekHtml.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
		weekHtml.append("<tr>");
		if(!isGw)
			weekHtml.append("<th scope='col' width='9%' class='epm-tb-top-th'>全天</th>");
		else
			weekHtml.append("<th scope='col' width='2%' class='epm-tb-top-th'></th>");
		for(int i=0;i<7;i++){
			String[] tmp = array[i].split("`");
			if(tmp[0].equals(jintian) && isJintian){
				if(!isGw){
					weekHtml.append("<th scope='col' width='13%' onclick='selectNewDate2("+tmp[2]+","+tmp[3]+","+tmp[0]+");'");
				}else{
					weekHtml.append("<th scope='col' width='14%' onclick='selectNewDate2("+tmp[2]+","+tmp[3]+","+tmp[0]+");'");
				}
				weekHtml.append(" class='epm-g-si'");
			}else{
				if(!isGw){
					weekHtml.append("<th scope='col' width='13%' onclick='selectNewDate2("+tmp[2]+","+tmp[3]+","+tmp[0]+");'");
				}else{
					weekHtml.append("<th scope='col' width='14%' onclick='selectNewDate2("+tmp[2]+","+tmp[3]+","+tmp[0]+");'");
				}
			}
			if(i==6){
				weekHtml.append(" style='border-right:1px #ccc solid;cursor:pointer;'");
			}else{
				weekHtml.append(" style='cursor:pointer;'");
			}
			weekHtml.append(">");
			weekHtml.append(tmp[0]+tmp[1]);
			weekHtml.append("</th>\r\n");
		}
		weekHtml.append("</tr>\r\n");
		weekHtml.append("<tr class='epm-tr'>");//未完，在后面还要继续添加
		
		
		int index = 0;//为了表示行数。行数为map的键。用于解决全天事件用的。
		int maxindexOfRecord = 0;
		//得到表体数据
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		StringBuffer sb_query = new StringBuffer();//查出来的数据全是符合条件的(查询条件是：start_time与end_time确立的区间 和 beginDate与endDate确立的区间  所形成的交集)
		if(Sql_switcher.searchDbServer()==Constant.MSSQL){
			sb_query.append("select pdc.record_num,pdc.p0100,pdc.title,pdc.start_time start_time,pdc.end_time end_time,pdc.start_time start_time2,pdc.end_time end_time2,pdc.type");
		}else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
			sb_query.append("select pdc.record_num,pdc.p0100,pdc.title,to_char(pdc.start_time,'yyyy-MM-dd hh24:mi:ss') start_time,to_char(pdc.end_time,'yyyy-MM-dd hh24:mi:ss') end_time,pdc.start_time start_time2,pdc.end_time end_time2,pdc.type");
		}
		sb_query.append(" from per_diary_content pdc left join p01 p on pdc.p0100=p.p0100");
		sb_query.append(" where "+Sql_switcher.year("pdc.end_time")+"*10000+"+Sql_switcher.month("pdc.end_time")+"*100+"+Sql_switcher.day("pdc.end_time")+">=");
		sb_query.append(" "+getMonthDate2(beginDate)+" and "+Sql_switcher.year("pdc.start_time")+"*10000+"+Sql_switcher.month("pdc.start_time")+"*100+"+Sql_switcher.day("pdc.start_time")+"<="+getMonthDate2(endDate));
		sb_query.append(" and pdc.a0100='"+a0100+"' and pdc.nbase='"+nbase+"'");
		sb_query.append(" and p.state=0 ");
		sb_query.append(" order by pdc.start_time,pdc.end_time");//暂时按起始时间(包括日)正序排列
		//sb_query.append(" order by Sql_switcher.hour("start_time"),start_time");//暂时按起始时间(不包括日，只包括小时和分钟)正序排列
		try{
			//全天事件的map
			HashMap recordmap = new HashMap();//map的键为第几行，键值为list。list中存放着多个小list.小list存放着:record_num,p0110,第几行,开始位置，结束位置。也就是json需要用到的数据
			//时间段事件的map
			HashMap periodmap = new HashMap();//map的键为第几列。键值为list。list中存放着许多个小list。小list存放着：record_num,p0100,title,start,end,average,zindex,indent,sequence,lindent。其中indent为缩进，为了突出立体效果。它的计算方法是(maxzindex-zindex)*0.5。sequence是当相同起始时间的记录并列显示时控制其位置的。和average搭配使用。lindent是后加的参数，因为左边也要缩进。计算方法是(indent-1)*0.5
			rs = dao.search(sb_query.toString());
			while(rs.next()){
				ArrayList detaillist = new ArrayList();//detaillist为recordmap键值（list）的一个子集
				Date startTime = rs.getDate("start_time2");
				Date endTime = rs.getDate("end_time2");
				GregorianCalendar ca = new GregorianCalendar();
				ca.setTime(startTime);
				int startDay = ca.get(Calendar.DAY_OF_MONTH);//本条工作记录的开始日期
				ca.setTime(endTime);
				int endDay = ca.get(Calendar.DAY_OF_MONTH);//本条工作记录的结束日期
				
				
				SimpleDateFormat tempsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date temporarydate1 = tempsdf.parse(rs.getString("start_time"));
				Date temporarydate2 = tempsdf.parse(rs.getString("end_time"));
				GregorianCalendar tempca = new GregorianCalendar();
				tempca.setTime(temporarydate1);
				int startHour = tempca.get(Calendar.HOUR_OF_DAY);
				int startMinute = tempca.get(Calendar.MINUTE);//这两个属性是新增的
				float starthourminute = (float)(startHour+startMinute/60.0);
				tempca.setTime(temporarydate2);
				int endHour = tempca.get(Calendar.HOUR_OF_DAY);
				int endMinute = tempca.get(Calendar.MINUTE);
				float endhourminute = (float)(endHour+endMinute/60.0);
				
				
				String tmp1 = sdf.format(startTime);
				String tmp2 = sdf.format(endTime);
				int intersectMin = (getMonthDate(tmp1)>getMonthDate(beginDate))?startDay:firstDay;
				int intersectMax = (getMonthDate(tmp2)<getMonthDate(endDate))?endDay:lastDay;
				String record_num = rs.getString("record_num");
				String p0100 = rs.getString("p0100");
				String title = rs.getString("title");
				String totalDayStr = getThisTotalDay(beginDate);//开始日期当月有几天
				int totalDay = Integer.parseInt(totalDayStr);
				String type = rs.getString("type");
				
				//min 为div填充的起始位置    max为div填充的终止位置
				int min = (intersectMin-firstDay+totalDay)%totalDay;//startDay为数据库中最小的天  firstDay为本周中最小的天
				int max = (intersectMax-firstDay+totalDay)%totalDay;
				
				
				//////////////////////////////////////////////////////////////////////////////////////////////////////
				//以下开始处理全天事件和时间段事件
				if(isGw){
					type = "0";
				}
				//如果是全天事件
				if("0".equals(type)){
					//detaillist的顺序依次为lunfinish,runfinish,record_num   p0100   title   min   max   linecount
					if(getMonthDate(tmp1)<getMonthDate(beginDate)){//beginDate是这一周最小的天
						detaillist.add("1");
					}else{
						detaillist.add("0");
					}
					
					if(getMonthDate(tmp2)>getMonthDate(endDate)){
						detaillist.add("1");
					}else{
						detaillist.add("0");
					}
					detaillist.add(record_num);
					detaillist.add(p0100);
					detaillist.add(title);
					detaillist.add(min+"");
					detaillist.add(max+"");
					
					//从第0行开始循环，一直循环到index行
					boolean isExitMap = false;//是否终止map的循环
					for(int i=0;i<index;i++){
						if(isExitMap){
							break;
						}
						ArrayList tmplist = new ArrayList();
						tmplist = (ArrayList)recordmap.get(i+"");
						//tmplist里面存放着的是list,而不是record_num,p0100之类的数据
						int tmplistcount = tmplist.size();
						boolean isInsert = false;//是否满足条件可以在此位置插入数据
						boolean isExitJ = false;//是否终止j的循环。当发现有交集的时候立即终止
						for(int j=0;j<tmplistcount;j++){
							if(isExitJ){
								break;
							}
							ArrayList temporarylist = new ArrayList();
							temporarylist = (ArrayList)tmplist.get(j);
							String startColumn = (String)temporarylist.get(5);
							String endColumn = (String)temporarylist.get(6);
							int intStartColumn = Integer.parseInt(startColumn);
							int intEndColumn = Integer.parseInt(endColumn);
							if(hasIntersect(intStartColumn,intEndColumn,min,max)){//如果有交集
								isInsert = false;
								isExitJ = true;//立即终止j的循环
							}else{
								isInsert = true;
							}
						} // end of j loop
						
						if(isInsert){//如果可以插入，就在第i个位置插入
							detaillist.add(i+"");
							tmplist.add(detaillist);
							recordmap.put(i+"", tmplist);
							isExitMap = true;//并且终止map的循环
						}
					} // end of i loop
					//如果map没有被终止掉，说明它一直没有找到合适的位置插入，那么就把它插入到下一行
					if(!isExitMap){
						ArrayList l = new ArrayList();
						detaillist.add(index+"");
						l.add(detaillist);
						recordmap.put(index+"", l);
						index++;
					}
				}			
				
				
				
				//如果是时间段事件
				else if("1".equals(type)){
					// detaillist依次存放着 record_num,p0100,title,start,end,average,zindex（缩进）。其中start和end就表示具体的时间了。如23点，24点。
					
					int mapbeginhour = min+firstDay;//在这个周时间范围内的第一天
					int mapendhour = max+firstDay;
					
					
					//先把每一条记录做拆分，拆分成符合格式的记录。事件的时间范围在intersectMin与intersectMax之间，并把拆分好的记录存储在map中
					HashMap dividmap = new HashMap();//dividmap的键为列，键值为list。list里存放着start,end
					ArrayList alist = new ArrayList();
					
					//startDay为记录开始的第一天
					
					
					
					
					if(min==max){//如果是没有跨天的事件
						alist.add(starthourminute+"");
						alist.add(endhourminute+"");
						dividmap.put(max+"", alist);
					}else{//如果跨天了
						//先添加第一天的开始时间和结束时间
						if(startDay==mapbeginhour){
							alist.add(starthourminute+"");
						}else{
							alist.add("0");
						}
						alist.add("24");
						dividmap.put(min+"", alist);
						alist = new ArrayList();
						//再处理最后一天的开始时间和结束时间
						alist.add("0");
						if(endDay==mapendhour){
							alist.add(endhourminute+"");
						}else{
							alist.add("24");
						}
						dividmap.put(max+"", alist);
						//再处理中间天
						//把中间天（除第一天和最后一天）的开始时间和结束时间放到dividmap中
						for(int k=(min+1);k<=(max-1);k++){
							alist = new ArrayList();
							alist.add("0");
							alist.add("24");
							dividmap.put(k+"", alist);
						}
					}
					
					
					/**以下的处理逻辑为：对于每一条数据库中的记录（以拆分后的为基本单位。如跨3天，就拆分成三条记录），分别插入到periodmap中。
					暂时先不管每一列中各记录的zindex和average。
					min和max分别是periodmap的第一列和最后一列
					同时，min和max也是本条记录的起始和结束
					从最小的列(min)开始插入。对于每一列，都和该列中所有的记录做比较***/
					for(int i=min;i<=max;i++){
						ArrayList detaillist2 = new ArrayList();
						ArrayList al = new ArrayList();
						al = (ArrayList)periodmap.get(i+"");
						ArrayList t = (ArrayList)dividmap.get(i+"");
						String bh = (String)t.get(0);//数据库中记录的开始时刻
						String fh = (String)t.get(1);//数据库中记录的结束时刻
						detaillist2.add(record_num);
						detaillist2.add(p0100);
						detaillist2.add(title);
						detaillist2.add(bh);
						detaillist2.add(fh);
						detaillist2.add("1");//增加average
						detaillist2.add("0");//增加zindex
						detaillist2.add("0");//增加indent
						detaillist2.add("1");//增加sequence
						detaillist2.add("0");//增加lindent  此参数为后加的
						if(al==null){
							ArrayList f = new ArrayList();
							f.add(detaillist2);
							periodmap.put(i+"", f);
						}else{
							al.add(detaillist2);
							periodmap.put(i+"", al);
						}
						
					}
					
					
				}
				
//				for(int i=0;i<min;i++){}
//				for(int i=min;i<=max;i++){}
//				for(int i=(max+1);i<7;i++){}
				
			} // end of while

			maxindexOfRecord = recordmap.size();
			int maxlineheight = 0;
			if(maxindexOfRecord >0)
				maxlineheight = 35+(maxindexOfRecord-1)*28+3;//下面再空出3像素
			if(isGw){
				if(maxlineheight<400)
					maxlineheight = 400;
			}
			weekHtml.append("<td class='epm-tb-top-td' style='height:"+maxlineheight+"px;'>"+"&nbsp;</td>");
			for(int i=0;i<7;i++)
				weekHtml.append("<td>&nbsp;</td>");
			weekHtml.append("</tr>");
			weekHtml.append("</table>");
			
			//将全天事件的map处理成json串
			String jsonstr = getJsonstr(recordmap);
			//System.out.println("jsonstr is: "+jsonstr);
			
			//先将periodmap每一列中记录的zindex,average,indent,sequence进行整理
			modifyMap(periodmap);
			//然后再将时间段的map处理成json串
			String periodjsonstr = getPeriodJsonstr(periodmap);
			//System.out.println("periodjsonstr is: "+periodjsonstr);
			
			str[0] = tableHtml.toString();
			str[1] = weekHtml.toString();
			str[2] = jsonstr;
			str[3] = periodjsonstr;
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
		return str;
	}
	
	
	//////////////////////辅助函数//////////////////////////////
	/**得到今天是星期几*/
	public int getWeekOfToday(){
		GregorianCalendar ca = new GregorianCalendar();
		int week = ca.get(Calendar.DAY_OF_WEEK)-1;
		return week;
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
	/**得到日历有几行和该月有几天。以`符号分割*/
	public String computeLinecount(String currentYear,String currentMonth){
		String linecount = "-1`-1";
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
		int dayCount = Integer.parseInt(tmp[2]);//该月有几天
		int fullcount = (dayCount-(7-week))/7;
		int surplus = (dayCount-(7-week))%7;
		int tmpint = fullcount+1;
		if(surplus>0){
			tmpint++;
		}
		linecount = tmpint+"`"+dayCount;
//		if(dayCount-((7-week)+1+7*3)>0){//7-week是第一行剩下的个数，
//			linecount = 5+"`"+dayCount;
//		}else{
//			linecount = 4+"`"+dayCount;
//		}
		return linecount;
	}

	/**得到本月总天数**/
	public String getThisTotalDay(String strDate){
		String dayCount = "-1";
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
		dayCount = tmpdayCount+"";
		return dayCount;
	}
	/**得到指定天的日期。方便在sql中比较日期**/
	public String getMonthDate(String year,String month,String day){
		String str = "";
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date=sdf.parse(year+"-"+month+"-"+day); 
			sdf=new SimpleDateFormat("yyyyMMdd");
			str = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return str;
	}
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
	public String getMonthDate2(String strdate){
		String str = "";
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date=sdf.parse(strdate); 
			sdf=new SimpleDateFormat("yyyyMMdd");
			str = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return str;
	}
	/**得到工作记录的数据**/
	public ArrayList getRecordList(ArrayList list,String thisyear,String thismonth){
		ArrayList returnList = new ArrayList();
		
		int n = list.size();
		if(n>0){
			String[] temp = computeLinecount(thisyear,thismonth).split("`");
			int dayCount = Integer.parseInt(temp[1]);//得到当月一共多少天
			//遍历这个月的每一天查找这一天是否有工作记录
			for(int i=1;i<=dayCount;i++){
				String strWeek = getStrWeek(thisyear,thismonth,String.valueOf(i));//得到星期几
				String strTime = thisyear+"年"+thismonth+"月"+i+"日";//得到时间
				ArrayList tempRecordList = new ArrayList();
				ArrayList recordList = new ArrayList();
				for(int j=0;j<n;j++){
					HashMap map = (HashMap)list.get(j);
					//分别得到开始时间和结束时间的天数
					Date start_time = (Date)map.get("start_time");
					Date end_time = (Date)map.get("end_time");
					String compareDate = thisyear+"-"+thismonth+"-"+i;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String st = sdf.format(start_time);
					String et = sdf.format(end_time);
					if(getMonthDate(compareDate)>=getMonthDate(st) && getMonthDate(compareDate)<=getMonthDate(et)){//如果这一天正好在这个范围内，就查出这个工作记录
						String p0100 = (String)map.get("p0100");
						String record_num = (String)map.get("record_num");
						String title = (String)map.get("title");
						HashMap tmp = new HashMap();
						tmp.put("p0100", p0100);
						tmp.put("record_num", record_num);
						tmp.put("title", title);
						tempRecordList.add(tmp);
					}
				}
				if(tempRecordList.size()>0){
					recordList.add(String.valueOf(i));
					recordList.add(strWeek);
					recordList.add(strTime);
					recordList.add(tempRecordList);
					returnList.add(recordList);
				}
			}
		}
		return returnList;
	}
	/**得到星期的字符串**/
	public String getStrWeek(String year,String month,String day){
		String week = "";
		int num = getWeek(year,month,day);
		switch(num){
		case 0:
			week = "周日";
			break;
		case 1:
			week = "周一";
			break;
		case 2:
			week = "周二";
			break;
		case 3:
			week = "周三";
			break;
		case 4:
			week = "周四";
			break;
		case 5:
			week = "周五";
			break;
		case 6:
			week = "周六";
			break;
		}
		return week;
	}
/**日历切换时，得到上    一天(minus=1)/周(minus=7)  的参数**/
	public String getPrevious(int year,int month,int day,int minus){
		//先得到上月的总天数
		String strDayCount = computeLinecount(String.valueOf(year),String.valueOf(month-1)).split("`")[1];
		int dayCount = Integer.parseInt(strDayCount);
		int tmpday = day-minus;
		if(tmpday<=0){
			tmpday = day-minus+dayCount;
			if(month==1){
				year = year-1;
				month = 12;
			}else{
				month = month-1;
			}
		}
		return year+","+month+","+tmpday;
	}
	
	/**日历切换时，得到下    一天(minus=1)/周(minus=7)  的参数**/
	public String getNext(int year,int month,int day,int minus){
		//先得到本月的总天数
		String strDayCount = computeLinecount(String.valueOf(year),String.valueOf(month)).split("`")[1];
		int dayCount = Integer.parseInt(strDayCount);
		int tmpday = day+minus;
		if(tmpday>dayCount){
			tmpday = day+minus-dayCount;
			if(month==12){
				month = 1;
				year = year + 1;
			}else{
				month = month+1;
			}
		}
		return year+","+month+","+tmpday;
	}
	/**转换日期显示方式   把2012-12-10转换为2012年12月10日**/
	public String exchangeStrDate(String date){
		String strDate = "";
		String[] strArray = date.split("-");
		strDate = strArray[0]+"年"+strArray[1]+"月"+strArray[2]+"日";
		return strDate;
	}
	/**将日期转换为标准格式输出   格式为2013年1月4日 12时30分**/
	public String standardDate(Date date){
		String str = "";
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		str = year+"年"+month+"月"+day+"日 "+hour+"时"+minute+"分";
		return str;
	}
	/**判断是否有交集（用于全天的事件）**/
	public boolean hasIntersect(int start1,int end1,int start2,int end2){
		if(start2>end1 || start1>end2){//只有彻底大于，才算没有交集。即使等于，也是有交集的
			return false;
		}
		return true;
	}
	/**判断是否有交集（用于时间段的事件）**/
	public boolean hasIntersect(float start1,float end1,float start2,float end2){
		if((start2-end1)>=1e-6 || (start1-end2)>=1e-6){//只要大于等于，就算没有交集。
			return false;
		}
		return true;
	}
	/**将全天事件的map处理成json串**/
	public String getJsonstr(HashMap recordmap){
		StringBuffer sb = new StringBuffer("[");
		
		Set key = recordmap.keySet();
		//循环map
        for (Iterator it = key.iterator(); it.hasNext();) {
            String s = (String) it.next();
            ArrayList list = (ArrayList)recordmap.get(s);
            int n = list.size();
            for(int i=0;i<n;i++){
            	ArrayList tmplist = new ArrayList();
            	tmplist = (ArrayList)list.get(i);
            	//list的顺序依次为lunfinish,runfinish,record_num   p0100   title   min   max   linecount
            	String lunfinish = (String)tmplist.get(0);
            	String runfinish = (String)tmplist.get(1);
            	String record_num = (String)tmplist.get(2);
            	String p0100 = (String)tmplist.get(3);
            	String title = (String)tmplist.get(4);
            	String min = (String)tmplist.get(5);
            	String max = (String)tmplist.get(6);
            	String linecount = (String)tmplist.get(7);
            	sb.append("{\"lunfinish\":\""+lunfinish+"\",\"runfinish\":\""+runfinish+"\",\"record_num\":\""+record_num+"\",\"p0100\":\""+p0100+"\",\"title\":\""+title+"\",\"min\":\""+min+"\",\"max\":\""+max+"\",\"linecount\":\""+linecount+"\"},");
            }
        }
        if(sb.length()>1)
        	sb.setLength(sb.length()-1);//把逗号去掉
		sb.append("]");
		return sb.toString();
	}
	/**将periodmap中的average,zindex重新整理**/
	public void modifyMap(HashMap periodmap){
		Set key = periodmap.keySet();
        for (Iterator it = key.iterator(); it.hasNext();) {
            String s = (String) it.next();
            ArrayList periodsetlist = (ArrayList)periodmap.get(s);
            int listcount = periodsetlist.size();
            ArrayList executelist = new ArrayList();//此list用于生成处理average的map。
            ArrayList zindexlist = new ArrayList();//此list用于生成处理zindex的map。
            for(int i=0;i<listcount;i++){
            	ArrayList list1 = new ArrayList();
        		list1 = (ArrayList)periodsetlist.get(i);
        		float beginTime1 = Float.parseFloat((String)list1.get(3));
        		float endTime1 = Float.parseFloat((String)list1.get(4));
        		if(!executelist.contains(beginTime1+"")){//因为zindexlist不能包含重复的数据
        			zindexlist.add(beginTime1+"`"+endTime1);
        		}
        		executelist.add(beginTime1+"");
            }
            HashMap zindexmap = new HashMap();
            HashMap averagemap = new HashMap();
            zindexmap = getZindexmap(zindexlist);//生成的map格式为：键：starttime   键值：sort
            averagemap = getAveragemap(executelist);//生成的map格式为：键：starttime。      键值：与starttime相同的值的个数  ` sequence
            for(int i=0;i<listcount;i++){//把每一条记录的average和zindex修改好
            	ArrayList list = new ArrayList();
        		list = (ArrayList)periodsetlist.get(i);
        		float startdate = Float.parseFloat((String)list.get(3));
        		String averageandseq = (String)averagemap.get(startdate+"");
        		String[] averageandseqarray = averageandseq.split("`");
        		int average = Integer.parseInt(averageandseqarray[0]);
        		int sequence = Integer.parseInt(averageandseqarray[1]);
        		averagemap.put(startdate+"", average+"`"+(sequence+1));//重新修改该averagemap
        		String sortandmaxindex = (String)zindexmap.get(startdate+"");
        		String[] sortindexarray = sortandmaxindex.split("`");
        		int zindex = Integer.parseInt(sortindexarray[0]);
        		int maxzindex = Integer.parseInt(sortindexarray[1]);
        		int indent = 0;
        		int lindent = 0;
        		if(zindex!=0){//是0，则只有它一个。不是0时，肯定有重叠
        			indent = maxzindex-zindex;
        			lindent = (zindex-1);
        		}
        		list.remove(5);
        		list.add(5,average+"");
        		list.remove(6);
        		list.add(6,zindex+"");
        		list.remove(7);
        		list.add(7,indent+"");
        		list.remove(8);
        		list.add(8,sequence+"");
        		list.remove(9);
        		list.add(9,lindent+"");
        		//System.out.print("");
            }
        }
	}
	/**处理average。生成的map格式为：键：starttime。      键值：与starttime相同的值的个数 ` 并列时的顺序**/
	public HashMap getAveragemap(ArrayList executelist){
		HashMap map = new HashMap();
		int n = executelist.size();
		for(int i=0;i<n;i++){
			int index = 1;
			float temp1 = Float.parseFloat((String)executelist.get(i));
			if(map.get(temp1+"")!=null){
				continue;
			}
			for(int j=(i+1);j<n;j++){
				float temp2 = Float.parseFloat((String)executelist.get(j));
				if(Math.abs((double)(temp1-temp2))<1e-6){//如果起始时间相同
					index++;
				}
			}
			map.put(temp1+"", index+"`"+"1");
		}
		return map;
	}
	/**处理zindex。生成的map格式为：键：starttime   键值：zindex`maxindex**/
	public HashMap getZindexmap(ArrayList zindexlist){
		HashMap map = new HashMap();
		int n = zindexlist.size();
		//首先生成邻接表，便于图的遍历
		
		  Graph g = new Graph(n);
		  float[] vertices = new float[n];
		  for(int i=0;i<n;i++){
			  String tmp = (String)zindexlist.get(i);
			  String[] array = tmp.split("`");
			  vertices[i] = Float.parseFloat(array[0]);//把所有的顶点都加进去
		  }
		  g.addVertex(vertices);
		  //开始生成邻接表
		  for(int i=0;i<n;i++){
			  String tmp1 = (String)zindexlist.get(i);
			  String[] array1 = tmp1.split("`");
			  float begin1 = Float.parseFloat(array1[0]);
			  float end1 = Float.parseFloat(array1[1]);
			  for(int j=i;j<n;j++){
				  String tmp2 = (String)zindexlist.get(j);
				  String[] array2 = tmp2.split("`");
				  float begin2 = Float.parseFloat(array2[0]);
				  float end2 = Float.parseFloat(array2[1]);
				  if(hasIntersect(begin1,end1,begin2,end2)){//如果有交集
					  g.addEdge(i, j);
				  }
			  }
		  }
		  ArrayList indexlist = new ArrayList();//indexlist里存放着许多list，有几个重叠部分就有几个list。每个list里存着starttime
		  g.depthTraverse(indexlist);//开始深度优先遍历
		  
		  int indexlistcount = indexlist.size();
		  for(int i=0;i<indexlistcount;i++){
			  ArrayList innerlist = (ArrayList)indexlist.get(i);
			  if(innerlist.size()>=1){
				  HashMap innermap = getSortMap(innerlist);//排序
				  int innermapcount = innermap.size();
				  Set key = innermap.keySet();
			        for (Iterator it = key.iterator(); it.hasNext();) {
			            String s = (String) it.next();
			            String strvalue = (String)innermap.get(s);
			            map.put(s, strvalue+"`"+innermapcount);
			        }
			  }

		  }

		return map;
	}
	/**排序（用于处理时间段的事件）**/
	public HashMap getSortMap(ArrayList list){
		HashMap map = new HashMap();
		int n = list.size();
		float[] tmp = new float[n];
		for(int i=0;i<n;i++){
			tmp[i]=Float.parseFloat((String)list.get(i));
		}
		
		if(n==1){//如果只有一个数据
			map.put(tmp[0]+"", "0");
		}else{
			//由小到大排序
			for(int i=0;i<n;i++){//第一趟排序，把最小的数放到最前面
				for(int j=(i+1);j<n;j++){
					if(tmp[i]>tmp[j]){
						float temp = 0;
						temp = tmp[i];
						tmp[i]=tmp[j];
						tmp[j]=temp;
					}
				}
			}
			for(int i=0;i<n;i++){
				map.put(tmp[i]+"", (i+1)+"");
			}
		}
		return map;
	}
	/**将时间段的map处理成json串**/
	public String getPeriodJsonstr(HashMap periodmap){
		StringBuffer sb = new StringBuffer("{");
		Set key = periodmap.keySet();
		//循环map
        for (Iterator it = key.iterator(); it.hasNext();) {
        	StringBuffer sbtemp = new StringBuffer("[");
            String s = (String) it.next();
            ArrayList list = (ArrayList)periodmap.get(s);
            int n = list.size();
            for(int i=0;i<n;i++){
            	ArrayList tmplist = new ArrayList();
            	tmplist = (ArrayList)list.get(i);
            	//list 依次存储 record_num,p0100,title,start,end,average,zindex
            	String record_num = (String)tmplist.get(0);
            	String p0100 = (String)tmplist.get(1);
            	String title = (String)tmplist.get(2);
            	String start = (String)tmplist.get(3);
            	String end = (String)tmplist.get(4);
            	String average = (String)tmplist.get(5);
            	String zindex = (String)tmplist.get(6);
            	String indent = (String)tmplist.get(7);
            	String sequence = (String)tmplist.get(8);
            	String lindent = (String)tmplist.get(9);
            	sbtemp.append("{\"record_num\":\""+record_num+"\",\"p0100\":\""+p0100+"\",\"title\":\""+title+"\",\"start\":\""+start+"\",\"end\":\""+end+"\",\"average\":\""+average+"\",\"zindex\":\""+zindex+"\",\"indent\":\""+indent+"\",\"sequence\":\""+sequence+"\",\"lindent\":\""+lindent+"\"},");
            }
            if(sbtemp.length()>1)
            	sbtemp.setLength(sbtemp.length()-1);
            sbtemp.append("]");
            sb.append("\""+s+"\":"+sbtemp.toString()+",");
        }
        if(sb.length()>1)
        	sb.setLength(sb.length()-1);
        sb.append("}");
		return sb.toString();
	}
	/**日报中修改average,zindex等属性**/
	public void modifyRecordList(ArrayList list){
		HashMap map = new HashMap();
		map.put("0", list);
		modifyMap(map);
	}
	/**日报中输出json串 格式：record_num,p0100,title,start,end,average,zindex,indent,sequence,lindent **/
	public String[] exportJson(ArrayList list,String combineid){
		String[] s = new String[2];
		StringBuffer sb = new StringBuffer("[");
		String scrollvalue = "";
		int listcount = list.size();
		for(int i=0;i<listcount;i++){
			ArrayList innerList = new ArrayList();
			innerList = (ArrayList)list.get(i);
			String record_num = (String)innerList.get(0);
			String p0100 = (String)innerList.get(1);
			String tmp = p0100+record_num;
			String title = (String)innerList.get(2);
			String start = (String)innerList.get(3);
			if(!"".equals(combineid) && combineid.equals(tmp)){
				float startint = Float.parseFloat(start);
				scrollvalue = (1+startint*31)+"";//因为时间轴的间隔都是分配均匀的，所以定位也比较有规律
			}
			String end = (String)innerList.get(4);
			String average = (String)innerList.get(5);
			String zindex = (String)innerList.get(6);
			String indent = (String)innerList.get(7);
			String sequence = (String)innerList.get(8);
			String lindent = (String)innerList.get(9);
			sb.append("{\"record_num\":\""+record_num+"\",\"p0100\":\""+p0100+"\",\"title\":\""+title+"\",\"start\":\""+start+"\",\"end\":\""+end+"\",\"average\":\""+average+"\",\"zindex\":\""+zindex+"\",\"indent\":\""+indent+"\",\"sequence\":\""+sequence+"\",\"lindent\":\""+lindent+"\"},");
		}
		if(sb.length()>1)
			sb.setLength(sb.length()-1);//把逗号去掉
		sb.append("]");
		s[0] = sb.toString();
		s[1] = scrollvalue;
		return s;
	}
	public String isEmptyOrNull(String str){
		if(str==null || "".equals(str))
			return "-1";
		return str;
	}
	/*当客户是国家电网时，输出时间段的Html*/
	public String[] createGwHtml(HashMap stategridmap,String temporarydate,String isOwner,String combineid){
		String[] str = new String[2];
		StringBuffer html = new StringBuffer("");
		HashMap scrollmap = new HashMap();//此map用于生成scrollvalue。map的键为起始时间，map的值为此起始时间下记录的个数
		int scrollvalue = 0;
		//开始输出html
		html.append("<div class=\"gw-index-all\">\r\n");
		int start = 8;//规定，时间轴的起始是上午8点，结束是晚上18点。
		int end = 17;
		for(int i=start;i<=end;i++){
			html.append("<div class=\"gw-in-one\">\r\n");
			html.append("<div class=\"gw-in-one-top\">\r\n");
			String tmphour = i+"";
			if(tmphour.length()==1){
				tmphour = "0"+tmphour;
			}
			html.append("<h2>"+tmphour+":00</h2>\r\n");
        	html.append("<p></p>\r\n");
            html.append("</div>\r\n");
            if(stategridmap.get(i+"")!=null){//当有数据的时候
            	ArrayList outerlist = (ArrayList)stategridmap.get(i+"");
            	int n = outerlist.size();
            	scrollmap.put(i+"", n+"");
            	for(int j=0;j<n;j++){
            		ArrayList innerlist = (ArrayList)outerlist.get(j);//依次存放record_num，p0100,title
            		String record_num = (String)innerlist.get(0);
            		String p0100 = (String)innerlist.get(1);
            		String title = (String)innerlist.get(2);
            		String p0100_recordnum = p0100+record_num;
            		if(combineid!=null && combineid.equals(p0100_recordnum)){//如果正好是要查询的那个div，则生成scrollvalue的值
            			//标准的一个div分三部分 top:12px,middle,30px,middle-two(也就是让用户点击的空白区域)30px
            			int endmapkey = i-1;
            			for(int k=start;k<=endmapkey;k++){
            				int middlecount = Integer.parseInt((String)scrollmap.get(k+""));
            				scrollvalue+=12+30+middlecount*30;
            			}
            			scrollvalue +=j*30;
            		}
            		html.append("<div class=\"gw-in-one-middle\" id=\""+p0100_recordnum+"\" onclick=\"tr_click('"+p0100+record_num+"');showDetail("+isOwner+","+p0100+","+record_num+");\">"+title+"</div>\r\n");
            	}
            }else{
            	scrollmap.put(i+"", "0");
            }
            //html.append("<div class=\"gw-in-one-middle\" >为什么不出现</div>\r\n");//测试用 请见谅
            html.append("<div class=\"gw-in-one-middle-two\" onclick=\"showQueryDiv('add','addperiod','"+temporarydate+"','"+i+"','00','"+temporarydate+"','17','59')\"></div>");//空白区域
            html.append("</div>\r\n");//end of gw-in-one
		}
		//加上最下面的底线
		html.append("<div class=\"gw-in-one\">\r\n");
		html.append("<div class=\"gw-in-one-top\">\r\n");
		html.append("<h2>18:00</h2>\r\n");
    	html.append("<p></p>\r\n");
        html.append("</div>\r\n");
        html.append("</div>\r\n");
        
		html.append("</div>");//最外层div的结束
		str[0] = html.toString();
		str[1] = String.valueOf(scrollvalue);
		return str;
	}
}









