package com.hjsj.hrms.businessobject.performance.solarterms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;

public class SolarTermsBo {

	UserView userViwe = null;
	Connection conn = null;
	public SolarTermsBo(UserView userView,Connection conn){
		this.userViwe = userView;
		this.conn = conn;
	}
	/**
	 * 得到部门号集合
	 * **/
	public ArrayList getDepartList(String currentdepart){
		ArrayList list = new ArrayList();
		list.add(currentdepart);
		getParentId(list,currentdepart);
		return list;
	}
	/**
	 * 查询parentid，并放在list中
	 * **/
	public void getParentId(ArrayList list,String codeitemId){
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sb = new StringBuffer("");
			sb.append("select codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemId+"') and codesetid='UM'");
			rs = dao.search(sb.toString());
			if(rs.next()){
				String parentid = rs.getString("codeitemid");
				if(parentid!=null && !"".equals(parentid)){
					list.add(parentid);
					getParentId(list,parentid);
				}
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
	 * 得到部门下拉列表  显示规则：越向上部门越大
	 * **/
	public ArrayList getOptionsList(ArrayList departlist){
		ArrayList list = new ArrayList();
		int count = departlist.size()-1;
		for(int i=count;i>=0;i--){
			String tmpid = (String)departlist.get(i);
			String tmpdesc = AdminCode.getCodeName("UM", tmpid);
			list.add(new CommonData(tmpid,tmpdesc));
		}
		return list;
	}
	
	/**
	 * 得到年份列表(暂时假设起始时间和结束时间的年份相同，即一个任务不会跨年)
	 * **/
	public ArrayList getYearList(String currentyear,ArrayList departlist,String departDutySet){
		ArrayList list = new ArrayList();//存放下拉列表
		ArrayList tmplist = new ArrayList();
		int count = departlist.size();
		StringBuffer sb = new StringBuffer("");//拼接成sql适用的字符串
		StringBuffer sql = new StringBuffer("");
		if(count>0){
			sb.append("(");
		}
		for(int i=0;i<count;i++){
			String tmpid = (String)departlist.get(i);
			sb.append(tmpid+",");
		}
		if(sb.length()>0){
			sb.setLength(sb.length()-1);
			sb.append(")");
		}
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			sql.append("select distinct "+Sql_switcher.year(departDutySet+"Z0")+" year from "+departDutySet+" where b0110 in "+sb.toString());
			sql.append(" order by year desc");
			rs = dao.search(sql.toString());
			if(rs.next()){
				String year = rs.getString("year");
				if(year!=null && !"".equals(year)){
					tmplist.add(year);
					list.add(new CommonData(year,year));
				}
			}
			if(!tmplist.contains(currentyear)){//如果没有包含当前年，则要加到适当的位置
				list.add(0,new CommonData(currentyear,currentyear));//把当年加在最上面
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
	

	/**
	 * 得到主页的html（任务维度）
	 * **/
	public String getIndexHtmlByTask(String year,String departid,String departDutySet){
		StringBuffer sbHtml = new StringBuffer("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
		//先输出标题
		sbHtml.append("<tr height=\"100\" class=\"epm-li-tr\">\r\n");
		sbHtml.append("<td width=\"24%\" class=\"epm-li-td-yi\"><input type=\"text\" readonly=\"true\" value=\"工作名称\" class=\"epm-li-gong\" /></td>\r\n");
		sbHtml.append("<td width=\"28%\"><input type=\"text\" readonly=\"true\" value=\"工作内容\" class=\"epm-li-gong\" /></td>\r\n");
		sbHtml.append("<td width=\"24%\"><input type=\"text\" readonly=\"true\" value=\"牵头处室\" class=\"epm-li-gong\" /></td>\r\n");
		sbHtml.append("<td width=\"24%\"><input type=\"text\" readonly=\"true\" value=\"配合部门\" class=\"epm-li-gong\" /></td>\r\n");
		sbHtml.append("</tr>\r\n");
		RowSet rs = null;
		try{
			int totalCount = 0;//工作内容的总条数
			HashMap tmpmap = new HashMap();//键为工作名称，键值为工作内容的个数
			HashMap contentmap = new HashMap();//键为工作内容，键值为对应的次数的串
			StringBuffer sb = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.conn);
			//一个工作名称包含几个工作内容
			sb.append("select "+departDutySet+"01 BA501,"+departDutySet+"02 BA502 from "+departDutySet);
			sb.append(" where b0110 ='"+departid+"' and "+Sql_switcher.year(departDutySet+"Z0")+"="+year);
			sb.append(" order by BA501");
			HashMap ba501map = new HashMap();//键为ba501，键值为list.list为该工作名称下的工作内容
			rs = dao.search(sb.toString());
			while(rs.next()){
				String ba501 = rs.getString("BA501");
				if(ba501==null && "".equals(ba501))
					continue;
				String ba502 = Sql_switcher.readMemo(rs,"BA502");
				if(ba501map.get(ba501)!=null){//如果已经存放了该工作名称了,就检验工作内容是否重复
					ArrayList innerlist = (ArrayList)ba501map.get(ba501);
					if(!innerlist.contains(ba502)){//如果工作内容不重复
						innerlist.add(ba502);
						int contentcount = Integer.parseInt((String)tmpmap.get(ba501));
						contentcount++;
						tmpmap.put(ba501, contentcount+"");
					}
					
				}else{//如果还没有存放该工作名称
					tmpmap.put(ba501, "1");
					ArrayList tlist = new ArrayList();
					tlist.add(ba502);
					ba501map.put(ba501, tlist);
				}
			}
			//一个工作内容包含的工作任务的次数所组成的串 
			sb.setLength(0);
			sb.append("select "+departDutySet+"02 BA502,I9999 from "+departDutySet);
			sb.append(" where b0110 ='"+departid+"' and "+Sql_switcher.year(departDutySet+"Z0")+"="+year);
			//sb.append(" order by BA502");//BA502被改成备注型的了
			rs = dao.search(sb.toString());
			ArrayList ba502list = new ArrayList();
			while(rs.next()){
				String ba502 = Sql_switcher.readMemo(rs, "BA502");
				if(ba502==null || "".equals(ba502))
					continue;
				String i9999 = rs.getString("I9999");
				if(!ba502list.contains(ba502)){//一个新的工作内容
					String tmp = i9999;
					contentmap.put(ba502, tmp);
				}else{
					String tmp = (String)contentmap.get(ba502);
					tmp +="`"+i9999;
					contentmap.put(ba502, tmp);
				}
				ba502list.add(ba502);
			}
			//输出html
			sb.setLength(0);
			sb.append("select B0110,"+departDutySet+"01 BA501,"+departDutySet+"02 BA502,"+departDutySet+"03 BA503,"+departDutySet+"04 BA504,"+Sql_switcher.year(departDutySet+"Z0")+" BA5Z0 from "+departDutySet);
			sb.append(" where b0110 = '"+departid+"' and "+Sql_switcher.year(departDutySet+"Z0")+"="+year);
			sb.append(" order by BA501");
			rs = dao.search(sb.toString());
			ArrayList lastba501list = new ArrayList();
			ArrayList lastba502list = new ArrayList();
			int index = 1;
			while(rs.next()){
				String ba501 = rs.getString("BA501");
				if(ba501==null || "".equals(ba501))
					continue;
				String ba502 = Sql_switcher.readMemo(rs, "BA502");
				if(ba502==null || "".equals(ba502))
					continue;
				if(lastba502list.contains(ba502)){//如果还是上一个工作内容
					continue;
				}
				String ba503 = rs.getString("BA503")==null?"&nbsp;":rs.getString("BA503");
				String ba504 = rs.getString("BA504")==null?"&nbsp;":rs.getString("BA504");
				String b0110 = rs.getString("B0110");//单位
				String ba5z0 = rs.getString("BA5Z0");//年
				String i9999 = (String)contentmap.get(ba502);//多个i999组成的串
				sbHtml.append("<tr>\r\n");
				if(!lastba501list.contains(ba501)){//如果是一个新的工作名称
					int count = Integer.parseInt((String)tmpmap.get(ba501));//一个工作名称包含多少个工作内容
//					if(index==totalCount){//如果是最后一行
//						sbHtml.append("<td class=\"epm-li-td-san\" ");
//					}else{
						sbHtml.append("<td class=\"epm-li-td-yi\" ");
//					}
					if(count==1){//如果工作名称只包含一个工作内容
						sbHtml.append("><a href=\"javascript:checkContent('"+b0110+"','"+i9999+"','"+ba5z0+"')\">"+ba501+"</a>");
					}else{
						sbHtml.append(" rowspan=\""+count+"\">");
						sbHtml.append("<p>"+ba501+"</p>");
					}
					sbHtml.append("</td>\r\n");
				}
//				if(index==totalCount){//输出ba502,ba503等内容
//					sbHtml.append("<td class=\"epm-li-td-er\" onclick=\"checkContent('"+b0110+"','"+i9999+"','"+ba5z0+"');\"><p><a href=\"#\">"+ba502+"</a></p></td>\r\n");
//					sbHtml.append("<td class=\"epm-li-td-er\"><p>"+ba503+"</p></td>\r\n");
//					sbHtml.append("<td class=\"epm-li-td-er\"><p>"+ba504+"</p></td>\r\n");
//				}else{
					sbHtml.append("<td><a href=\"javascript:checkContent('"+b0110+"','"+i9999+"','"+ba5z0+"');\">"+ba502+"</a></td>\r\n");
					sbHtml.append("<td><p>"+ba503+"</p></td>\r\n");
					sbHtml.append("<td><p>"+ba504+"</p></td>\r\n");
//				}
				sbHtml.append("</tr>\r\n");
				lastba501list.add(ba501);
				lastba502list.add(ba502);
				index++;
			}
			sbHtml.append("</table>");
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
		return sbHtml.toString();
	}
	/**
	 * 主页Html(时间维度)
	 * **/
	public String getIndexHtmlByTime(String year,String depart,String departDutySet){
		StringBuffer sbhtml = new StringBuffer("");
		//首先查出每个月有几条数据，并存放在montharray中
		int[] montharray = new int[12];
		for(int i=0;i<montharray.length;i++){
			montharray[i] = 0;
		}
		StringBuffer sql = new StringBuffer("");
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			//该sql语句的作用是生成填充montharray
			sql.append("select "+Sql_switcher.month(departDutySet+"06")+" monthstart,"+Sql_switcher.month(departDutySet+"07")+" monthend");
			sql.append(" from "+departDutySet+" where b0110 = '"+depart+"' and "+Sql_switcher.year(departDutySet+"Z0")+"="+year);
			sql.append(" order by BA506");//按起始时间排序
			rs = dao.search(sql.toString());
			while(rs.next()){
				String monthstart = rs.getString("monthstart");
				String monthend = rs.getString("monthend");
				if(monthstart == null || "".equals(monthstart)){
					continue;
				}
				if(monthend == null || "".equals(monthend)){
					continue;
				}
				int intmonthstart = Integer.parseInt(monthstart)-1;
				int intmonthend = Integer.parseInt(monthend)-1;
				for(int i=intmonthstart;i<=intmonthend;i++){
					int taskcount = montharray[i];
					taskcount++;
					montharray[i] = taskcount;
				}
			}
			//开始画出html页面
			sbhtml.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
            sbhtml.append("<tr>\r\n");
            
            sbhtml.append("<td width=\"25%\" class=\"epm-li-td-yi\"><a href=\"javascript:checkMonthContent('"+depart+"','"+year+"','"+1+"')\">1月</a><span class=\"monthspan\" >"+montharray[0]+"</span></td>\r\n");
            for(int i=2;i<=4;i++){
            	sbhtml.append("<td width=\"25%\" class=\"epm-li-td-yi-dinwei\"><a href=\"javascript:checkMonthContent('"+depart+"','"+year+"','"+i+"')\">"+i+"月</a><span class=\"monthspan\" >"+montharray[i-1]+"</span></td>\r\n");
            }
            sbhtml.append("</tr>\r\n");
            sbhtml.append("<tr>\r\n");
            
            sbhtml.append("<td class=\"epm-li-td-yi\"><a href=\"javascript:checkMonthContent('"+depart+"','"+year+"','"+5+"')\">5月</a><span class=\"monthspan\" >"+montharray[4]+"</span></td>\r\n");
            for(int i=6;i<=8;i++){
            	sbhtml.append("<td class=\"epm-li-td-yi-dinwei\"><a href=\"javascript:checkMonthContent('"+depart+"','"+year+"','"+i+"')\">"+i+"月</a><span class=\"monthspan\" >"+montharray[i-1]+"</span></td>\r\n");
            }
            sbhtml.append("</tr>\r\n");
            sbhtml.append("<tr>\r\n");
            sbhtml.append("<td class=\"epm-li-td-san epm-li-td-yi-dinwei\"><a href=\"javascript:checkMonthContent('"+depart+"','"+year+"','"+9+"')\">9月</a><span class=\"monthspan\" >"+montharray[8]+"</span></td>\r\n");
            for(int i=10;i<=12;i++){
            	sbhtml.append("<td class=\"epm-li-td-er epm-li-td-yi-dinwei\"><a href=\"javascript:checkMonthContent('"+depart+"','"+year+"','"+i+"')\">"+i+"月</a><span class=\"monthspan\" >"+montharray[i-1]+"</span></td>\r\n");
            }
            sbhtml.append("</tr>\r\n");
            sbhtml.append("</table>\r\n");

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
		return sbhtml.toString();
	}
	/**
	 * 任务维度下，显示某一工作内容的具体的任务
	 * 下面的算法同周报全天事件的算法一样。recordmap的键为行号，键值为list。每个list里包含着许多小list。每个小list的第一个值为
	 * 时间，如1-8，说明1月至8月。第二个值为该区间段的具体任务。跳出while循环后将recordmap解析成json串，然后java通过json串输出html。
	 * **/
	public String getTaskHtmlByTask(String depart,String times,String year,String departDutySet,int eachtdwidth){
		StringBuffer taskHtml = new StringBuffer("");
		RowSet rs = null;
		try{
			String strtimes = "(";//先把times处理成sql识别的方式
			String[] array = times.split("`");
			int arraycount = array.length;
			for(int i=0;i<arraycount;i++){
				if("".equals(array[i]))
					continue;
				strtimes+=array[i]+",";
			}
			strtimes = strtimes.substring(0,strtimes.length()-1);
			strtimes+=")";
			ArrayList monthlist = new ArrayList();
			ArrayList fixedlist = new ArrayList();//存储头四列的内容，分别为工作名称，工作内容，牵头处室，配合部门
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sb = new StringBuffer("");
			sb.append("select "+departDutySet+"01 BA501,"+departDutySet+"02 BA502,"+departDutySet+"03 BA503,"+departDutySet+"04 BA504,"+departDutySet+"05 BA505,"+Sql_switcher.month(departDutySet+"06")+" BA506,"+Sql_switcher.month(departDutySet+"07")+" BA507");
			sb.append(" from "+departDutySet+" where b0110 = '"+depart+"' and "+Sql_switcher.year(departDutySet+"Z0")+"="+year+" and I9999 in"+strtimes);
			sb.append(" order by BA506");//按起始时间排序
			rs = dao.search(sb.toString());
			int executecount = 0;//因为fixedlist只需要add一次就行了
			HashMap recordmap = new HashMap();
			int index = 0;//为了控制recordmap的第几行 原理同周报的全天事件
			while(rs.next()){
				ArrayList detaillist = new ArrayList();//detaillist为recordmap键值（list）的一个子集
				String ba501 = rs.getString("BA501");
				if(ba501==null || "".equals(ba501))
					continue;
				String ba502 = Sql_switcher.readMemo(rs, "BA502");
				if(ba502==null || "".equals(ba502))
					continue;
				String ba503 = rs.getString("BA503")==null?"&nbsp;":rs.getString("BA503");
				String ba504 = rs.getString("BA504")==null?"&nbsp;":rs.getString("BA504");
				String ba505 = Sql_switcher.readMemo(rs, "BA505");
				if(executecount==0){
					fixedlist.add(ba501);
					fixedlist.add(ba502);
					fixedlist.add(ba503);
					fixedlist.add(ba504);
				}
				int ba506 = Integer.parseInt(rs.getString("BA506"));
				int ba507 = Integer.parseInt(rs.getString("BA507"));
				for(int i=ba506;i<=ba507;i++){
					if(!monthlist.contains(i+"")){
						monthlist.add(i+"");
					}
				}
				/**
				 * recordmap的键为行号，键值为list。每个list里包含着许多小list。每个小list有两个值。第一个值为
				 * 时间，如1-8，说明1月至8月。第二个值为该区间段的具体任务。跳出while循环后将recordmap解析成json串，然后java通过json串输出html。
				 * **/

				//detaillist的顺序依次为time,task,linecount
				String tmptime = ba506+"-"+ba507;
				detaillist.add(tmptime);
				detaillist.add(ba505);
				
				//从第0行开始循环，一直循环到index行
				boolean isExitMap = false;//是否终止map的循环
				for(int i=0;i<index;i++){
					if(isExitMap){
						break;
					}
					ArrayList tmplist = new ArrayList();
					tmplist = (ArrayList)recordmap.get(i+"");//tmplist里面存放着的是list,而不是time,task之类的数据
					int tmplistcount = tmplist.size();
					boolean isInsert = false;//是否满足条件可以在此位置插入数据
					boolean isExitJ = false;//是否终止j的循环。当发现有交集的时候立即终止
					for(int j=0;j<tmplistcount;j++){
						if(isExitJ){
							break;
						}
						ArrayList temporarylist = new ArrayList();
						temporarylist = (ArrayList)tmplist.get(j);
						String compare1 = (String)temporarylist.get(0);
						if(hasIntersect(compare1,tmptime)){//如果有交集
							isInsert = false;
							isExitJ = true;//立即终止j的循环
						}else{
							isInsert = true;
						}
					} // end of j loop
					
					if(isInsert){//如果可以插入，就在第i个位置插入
						//detaillist.add(i+"");
						tmplist.add(detaillist);
						recordmap.put(i+"", tmplist);
						isExitMap = true;//并且终止map的循环
					}
				} // end of i loop
				//如果map没有被终止掉，说明它一直没有找到合适的位置插入，那么就把它插入到下一行
				if(!isExitMap){
					ArrayList l = new ArrayList();
					//detaillist.add(index+"");
					l.add(detaillist);
					recordmap.put(index+"", l);
					index++;
				}
				
				executecount++;
			}// end of sql's while
			getSortList(monthlist);//让monthlist从小到大排一下序
			modifyMap(monthlist,recordmap);//见modifyMap的函数说明
			String jsonstr = outputJson(recordmap);
			//System.out.println(jsonstr);
			int linecount = recordmap.size();
			taskHtml = getHtml(fixedlist,monthlist,jsonstr,linecount,eachtdwidth);
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
		return taskHtml.toString();
	}
	/**
	 * 时间维度中获取某个月份的所有任务
	 * **/
	public String getTaskHtmlByTime(String depart,String year,String month,String departDutySet){
		StringBuffer monthtaskhtml = new StringBuffer("");
		monthtaskhtml.append("<div class=\"epm-li-yue-top\">\r\n");
		monthtaskhtml.append("<h2>"+month+"月份</h2>\r\n");
		monthtaskhtml.append("</div>\r\n");
		monthtaskhtml.append("<div class=\"epm-li-yue-bottom\">\r\n");
		monthtaskhtml.append("<div class=\"epm-li-bottom-logo\">\r\n");
		monthtaskhtml.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
		//先添加标题
		monthtaskhtml.append("<tr height=\"100\" class=\"epm-li-tr\">\r\n");
		String[] taskarray = new String[5];
		taskarray[0] = "工作名称";
		taskarray[1] = "工作内容";
		taskarray[2] = "牵头处室";
		taskarray[3] = "配合部门";
		taskarray[4] = "具体工作";
		for(int i=0;i<5;i++){
			monthtaskhtml.append("<td width=\"20%\" ");
			if(i==0){
				monthtaskhtml.append("class=\"epm-li-td-yi\"");
			}
			monthtaskhtml.append("><input type=\"text\" readonly=\"true\" value=\""+taskarray[i]+"\" class=\"epm-li-gong\" /></td>\r\n");
		}
		monthtaskhtml.append("</tr>\r\n");
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("");
			sql.append("select "+departDutySet+"01 BA501,"+departDutySet+"02 BA502,"+departDutySet+"03 BA503,"+departDutySet+"04 BA504,"+departDutySet+"05 BA505 from BA5");
			sql.append(" where b0110='"+depart+"' and "+Sql_switcher.year(departDutySet+"Z0")+"="+year+" and "+Sql_switcher.month(departDutySet+"06")+"<="+month+" and "+Sql_switcher.month(departDutySet+"07")+">="+month);
			sql.append(" order by BA501");//按工作名称排序
			rs = dao.search(sql.toString());
			rs.last();
			int totalcount = rs.getRow();
			rs.beforeFirst();
			int index = 1;
			while(rs.next()){
				String ba501 = rs.getString("BA501");
				if(ba501==null || "".equals(ba501))
					continue;
				String ba502 = Sql_switcher.readMemo(rs, "BA502");
				if(ba502==null || "".equals(ba502))
					continue;
				String ba503 = rs.getString("BA503")==null?"&nbsp;":rs.getString("BA503");
				String ba504 = rs.getString("BA504")==null?"&nbsp;":rs.getString("BA504");
				String ba505 = Sql_switcher.readMemo(rs, "BA505");
//				if(index==totalcount){//如果是最后一行，则单元格底部没有线
//					monthtaskhtml.append("<tr>\r\n");
//					monthtaskhtml.append("<td class=\"epm-li-td-san\"><p>"+ba501+"</p></td>\r\n");
//					monthtaskhtml.append("<td class=\"epm-li-td-er\"><p>"+ba502+"</p></td>\r\n");
//					monthtaskhtml.append("<td class=\"epm-li-td-er\"><p>"+ba503+"</p></td>\r\n");
//					monthtaskhtml.append("<td class=\"epm-li-td-er\"><p>"+ba504+"</p></td>\r\n");
//					monthtaskhtml.append("<td class=\"epm-li-td-er\"><p>"+ba505+"</p></td>\r\n");
//                    monthtaskhtml.append("</tr>\r\n");
//				}else{
					monthtaskhtml.append("<tr>\r\n");
					monthtaskhtml.append("<td class=\"epm-li-td-yi\"><p>"+ba501+"</p></td>\r\n");
					monthtaskhtml.append("<td><p>"+ba502+"</p></td>\r\n");
					monthtaskhtml.append("<td><p>"+ba503+"</p></td>\r\n");
					monthtaskhtml.append("<td><p>"+ba504+"</p></td>\r\n");
					monthtaskhtml.append("<td><p>"+ba505+"</p></td>\r\n");
					monthtaskhtml.append("</tr>\r\n");
//				}
				index++;
			}
			monthtaskhtml.append("</table>\r\n");
			monthtaskhtml.append("</div>\r\n");
			monthtaskhtml.append("</div>\r\n");
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
		return monthtaskhtml.toString();
	}
	
	//////////////////////辅助函数/////////////////////////////
	
	/**对ArrayList进行排序   由小到大**/
	public void getSortList(ArrayList list){
		int n = list.size();
		int[] tmp = new int[n];
		for(int i=0;i<n;i++){
			tmp[i]=Integer.parseInt((String)list.get(i));
		}
		for(int i=0;i<n;i++){//第一趟排序，把最小的数放到最前面
			for(int j=(i+1);j<n;j++){
				if(tmp[i]>tmp[j]){
					int temp = 0;
					temp = tmp[i];
					tmp[i]=tmp[j];
					tmp[j]=temp;
				}
			}
		}
		for(int i=0;i<n;i++){
			list.remove(i);
			list.add(i,tmp[i]+"");
		}
	}
	
	/**
	 * 作用：将map解析成json串
	 * 目的：方便调试。方便扩展。
	 * 字段："starttime":"2","endtime":"3","task":"领导班子整顿","changecolor":"1"   
	 * 格式：{"1":[{"starttime":"2","endtime":"3","task":"领导班子整顿","changecolor":"1"},{}],"2":[]}
	 * 字段解释：因为当前月的任务要变成红色，所以增加changecolor字段
	 * **/
	public String outputJson(HashMap recordmap){
		StringBuffer sbjson = new StringBuffer("{");
		GregorianCalendar ca = new GregorianCalendar();
		int month = ca.get(Calendar.MONTH)+1;//因为当前月还要显示红色字
		int mapcount = recordmap.size();
		for(int i=0;i<mapcount;i++){// 循环map    map的键为行号。从0开始
			String keyname = String.valueOf(i);
			sbjson.append("\""+keyname+"\":[");
			ArrayList outerlist = (ArrayList)recordmap.get(keyname);//大list
			int listcount = outerlist.size();
			for(int j=0;j<listcount;j++){
				String changecolor = "0";
				ArrayList innerlist = (ArrayList)outerlist.get(j);
				String starttime = (String)innerlist.get(0);//时间
				String strtask = (String)innerlist.get(1);//任务
				String[] timearray = starttime.split("-");
				int startmonth = Integer.parseInt(timearray[0]);
				int endmonth = Integer.parseInt(timearray[1]);
				if(month >=startmonth && month<=endmonth){
					changecolor = "1";
				}
				sbjson.append("{\"starttime\":\""+startmonth+"\",\"endtime\":\""+endmonth+"\",\"task\":\""+strtask+"\",\"changecolor\":\""+changecolor+"\"},");
			}
			sbjson.setLength(sbjson.length()-1);
			sbjson.append("],");
		}
		if(sbjson.length()>1){
			sbjson.setLength(sbjson.length()-1);
		}
		sbjson.append("}");
		return sbjson.toString();
	}
	
	/**
	 * 将json串输出为html
	 * json串格式为：{"1":[{"starttime":"2","endtime":"3","task":"领导班子整顿","changecolor":"1"},{}],"2":[]}
	 * **/
	public StringBuffer getHtml(ArrayList fixedlist,ArrayList monthlist,String jsonstr,int linecount,int eachtdwidth){
		StringBuffer sbHtml = new StringBuffer("");
		String widthsymbol = "";
		int eachwidth = eachtdwidth;//如果月份超过了3个，就显示像素，而不显示百分比
		int monthcount = monthlist.size();
		if(monthcount<=3){
			eachwidth = 100/(4+monthcount);
			widthsymbol = "%";
		}
		int totalwidth = (4+monthcount)*eachwidth;
		GregorianCalendar ca = new GregorianCalendar();
		String titlemonth = String.valueOf((ca.get(Calendar.MONTH)+1));//因为当前月还要显示红色
		if(monthcount<=3){
			sbHtml.append("<div class=\"epm-li-two-bottom-logo\" style=\"width:99%;\">");
		}else{
			sbHtml.append("<div class=\"epm-li-two-bottom-logo\" style=\"width:"+totalwidth+"px;\">");
		}
		sbHtml.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
		JSONObject jsonObject = JSONObject.fromObject(jsonstr);
		//先输出标题
		sbHtml.append("<tr height=\"40\" class=\"epm-li-two-tr\">\r\n");
		sbHtml.append("<td width=\""+eachwidth+widthsymbol+"\" class=\"epm-li-two-td-yi\">");
		sbHtml.append("<input type=\"text\" readonly=\"true\" value=\"工作名称\" class=\"epm-li-two-gong\" />");
		sbHtml.append("</td>\r\n");
		sbHtml.append("<td width='"+eachwidth+widthsymbol+"'>");
		sbHtml.append("<input type=\"text\" readonly=\"true\" value=\"工作内容\" class=\"epm-li-two-gong\" />");
		sbHtml.append("</td>\r\n");
		sbHtml.append("<td width='"+eachwidth+widthsymbol+"'>");
		sbHtml.append("<input type=\"text\" readonly=\"true\" value=\"牵头处室\" class=\"epm-li-two-gong\" />");
		sbHtml.append("</td>\r\n");
		sbHtml.append("<td width='"+eachwidth+widthsymbol+"'>");
		sbHtml.append("<input type=\"text\" readonly=\"true\" value=\"配合部门\" class=\"epm-li-two-gong\" />");
		sbHtml.append("</td>\r\n");
		for(int j=0;j<monthcount;j++){//遍历后面的月份
			String month = (String)monthlist.get(j);
			sbHtml.append("<td width='"+eachwidth+widthsymbol+"'>");
			if(month.equals(titlemonth)){
				sbHtml.append("<input type=\"text\" readonly=\"true\" value=\""+month+"月\" class=\"epm-li-two-gong-two\" />");
			}else{
				sbHtml.append("<input type=\"text\" readonly=\"true\" value=\""+month+"月\" class=\"epm-li-two-gong\" />");
			}
			sbHtml.append("</td>\r\n");
		}
		sbHtml.append("</tr>\r\n");
		//标题输出完毕。再输出下面的所有行
		for(int i=0;i<linecount;i++){//遍历所有的行
			sbHtml.append("<tr>\r\n");
			if(i==0){
				for(int j=0;j<4;j++){//遍历前面那几个固定列
					if(j==0)
						sbHtml.append("<td class=\"epm-li-two-td-yi\" rowspan='"+linecount+"'>");
					else
						sbHtml.append("<td rowspan='"+linecount+"'>");
					String fieldcontent = (String)fixedlist.get(j);
					sbHtml.append("<p>"+fieldcontent+"</p>");
					sbHtml.append("</td>\r\n");
				}
			}
			String key = String.valueOf(i);
			Object jsonarray = jsonObject.get(key);//对象数组
			JSONArray jsonArray = JSONArray.fromObject(jsonarray);
			Object[] stringarray = jsonArray.toArray();
			for(int j=0;j<stringarray.length;j++){
				JSONObject tmpjsonObject = JSONObject.fromObject(stringarray[j]);//得到具体的数组的一个值
				int startmonth = Integer.parseInt((String)tmpjsonObject.get("starttime"));
				int endmonth = Integer.parseInt((String)tmpjsonObject.get("endtime"));
				String task = (String)tmpjsonObject.get("task");
				String changecolor = (String)tmpjsonObject.get("changecolor");
				int minus = (endmonth-startmonth)+1;
				sbHtml.append("<td colspan='"+minus+"'>");
				if("1".equals(changecolor)){
					sbHtml.append("<div class='currentmonth'>"+task+"</div>");
				}else{
					sbHtml.append("<p>"+task+"</p");
				}
				sbHtml.append("</td>\r\n");
			} // end of each line of html 
			sbHtml.append("</tr>\r\n");
		}
		
		sbHtml.append("</table>\r\n");
		sbHtml.append("</div>\r\n");
		return sbHtml;
	}
	
	/**
	 * 判断是否有交集
	 * **/
	public boolean hasIntersect(String str1,String str2){
		String[] array1 = str1.split("-");
		String[] array2 = str2.split("-");
		int start1 = Integer.parseInt(array1[0]);
		int end1 = Integer.parseInt(array1[1]);
		int start2 = Integer.parseInt(array2[0]);
		int end2 = Integer.parseInt(array2[1]);
		if(start2>end1 || end2<start1){
			return false;
		}
		return true;
	}
	
	/**
	 * 对map进行重新包装
	 * map的值为大list,大list存放着许多小list,让所有的小list都按月份排序，并且没有涉及到的月份也补充完整。如所有的月份是1,2,3,6,7
	 * 第一行只涉及到了1,3,6,7月份。所以还要把2月份也补充上，并且按大小排序。使生成的json串是最简单的串。时间复杂度：n的3次方
	 * 在这里采取的规则是：先补充，后排序
	 * **/
	public void modifyMap(ArrayList monthlist,HashMap recordmap){
		//首先把没有涉及到的月份添加上
		HashMap tmpmap = new HashMap();
		int n = monthlist.size();
		for(int i=0;i<n;i++){
			String month = (String)monthlist.get(i);
			tmpmap.put(month, "1");//用map不用List是因为map可以remove。
		}
		Set key = recordmap.keySet();
        for (Iterator it = key.iterator(); it.hasNext();) {//循环map
        	String key_name = (String) it.next();
        	ArrayList outerlist = (ArrayList)recordmap.get(key_name);//map里的大list
        	int count = outerlist.size();
        	for(int i=0;i<count;i++){
        		ArrayList innerlist = (ArrayList)outerlist.get(i);
        		String tmpmonthstr = (String)innerlist.get(0);
        		String[] tmparray = tmpmonthstr.split("-");
        		int startmonth = Integer.parseInt(tmparray[0]);
        		int endmonth = Integer.parseInt(tmparray[1]);
        		for(int j=startmonth;j<=endmonth;j++){
        			if(tmpmap.get(j+"")!=null){
        				tmpmap.remove(j+"");
        			}
        		}
        	} // end of inner loop
        	if(tmpmap.size()>0){//向大list中增加小list
        		Set key2 = tmpmap.keySet();
                for (Iterator it2 = key2.iterator(); it2.hasNext();) {//循环map
                	String keyName = (String) it2.next();
                	ArrayList l = new ArrayList();
                	l.add(keyName+"-"+keyName);
                	l.add("&nbsp;");
                	outerlist.add(l);
                }
        	}
        	for(int i=0;i<n;i++){//重新给tmpmap赋值
    			String month = (String)monthlist.get(i);
    			tmpmap.put(month, "1");
    		}
        } // end of outer loop
        
        //再把大list中的小list按时间从小到大排序
        for (Iterator it = key.iterator(); it.hasNext();) {//循环map
        	String key_name = (String) it.next();
        	ArrayList outerlist = (ArrayList)recordmap.get(key_name);//map里的大list
        	ArrayList ilist = sortInnerList(outerlist);
        	recordmap.put(key_name, ilist);
        }
	} // end of the function
	/**
	 * 对大list里面的所有小list排序
	 * **/
	public ArrayList sortInnerList(ArrayList list){
		ArrayList l = new ArrayList();
		int listcount = list.size();
		for(int i=0;i<listcount;i++){
			l.add("1");
		}
		int[] array = new int[listcount];//存放所有的起始时间
		int[] sortarray = new int[listcount];//存放所有的顺序
		for(int i=0;i<listcount;i++){//填充array
			ArrayList innerlist = (ArrayList)list.get(i);
			String timestr = (String)innerlist.get(0);
			String[] innerarray = timestr.split("-");
			array[i] = Integer.parseInt(innerarray[0]);//开始时间
		}
		//对所有的起始时间找出他们的大小顺序来（只不过顺序是从0开始的，为了和list相衔接）
		for(int i=0;i<listcount;i++){
			int index = 0;
			for(int j=0;j<listcount;j++){
				if(i==j)
					continue;
				if(array[j]<array[i]){
					index++;
				}
			}
			sortarray[i] = index;
		}
		for(int i=0;i<listcount;i++){
			ArrayList innerlist = (ArrayList)list.get(i);
			int insertPosition = sortarray[i];
			l.remove(insertPosition);
			l.add(insertPosition,innerlist);
		}
		return l;
	}
	
} // end of the class
