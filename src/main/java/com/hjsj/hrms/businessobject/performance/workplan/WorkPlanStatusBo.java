package com.hjsj.hrms.businessobject.performance.workplan;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:WorkPlanStatusBo.java</p>
 * <p>Description:填报状态</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2012-07-10</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WorkPlanStatusBo 
{

	private Connection conn = null;
	private UserView userView = null;
	private HashMap haveCycleMap = new HashMap();  // 统计周期

	public WorkPlanStatusBo(Connection conn, UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
	}
		
	/**
	 * 生成填报状态表头信息
	 * @param cycleDaysList
	 * @return
	 */
	public String getTheadHtml(ArrayList cycleDaysList)
	{
		StringBuffer html = new StringBuffer("");
		html.append("<thead><tr><td  width='150' class='t_cell_locked2' rowspan='2'  nowrap >&nbsp;</td>");
		
		for(int i=0;i<cycleDaysList.size();i++)
		{
			if(i==cycleDaysList.size()-1) {
                html.append("<td  class='t_header_locked1'  width='150' colspan='3' nowrap><br>");
            } else {
                html.append("<td  class='t_header_locked'  width='150' colspan='3' nowrap><br>");
            }
			html.append((String)cycleDaysList.get(i)); 
			html.append("<br>&nbsp;</td>");
		}	
		html.append("</tr><tr>");
		ArrayList list = new ArrayList();
		list.add(ResourceFactory.getProperty("performance.workdiary.worknobao"));
		list.add(ResourceFactory.getProperty("performance.workdiary.workyesbao"));
		list.add(ResourceFactory.getProperty("performance.workdiary.workyespi"));
		for(int i=0;i<cycleDaysList.size();i++)
		{
			for(int j=0;j<list.size();j++)
			{
				if((i==cycleDaysList.size()-1) && j==list.size()-1) {
                    html.append("<td class='t_header_locked1' width='50' nowrap>");
                } else {
                    html.append("<td class='t_header_locked' width='50' nowrap>");
                }
				html.append((String)list.get(j));
				html.append("</td>");
			}
		}
		html.append("</tr></thead>");
		return html.toString();
	}
	
	/**
	 * 取得填报状态表格内容
	 * @param unitStatusList
	 * @param cycleDaysList
	 * @return
	 */
	public String getTableBodyHtml(LinkedHashMap codeitemidMap,ArrayList cycleDaysList)
	{
		StringBuffer bodyHtml = new StringBuffer("");
		ArrayList list = new ArrayList();
		list.add("01");
		list.add("02");
		list.add("03");
		int x = 0;
		
		// 获得organization表里的childid字段为空的所有单位或部门
		HashMap uoreMap = getUnitOrE0122();
		
		Set keySet=codeitemidMap.keySet();
		java.util.Iterator t=keySet.iterator();
		int n = codeitemidMap.size();
		int i=0;
		while(t.hasNext())
		{
			String strKey = (String)t.next();  // 键值	    
			HashMap codeCycleTypeMap = (HashMap)codeitemidMap.get(strKey);   // value值		
			String codeitemid = strKey.substring(0,strKey.indexOf("`"));
			String codeitemdesc = strKey.substring(strKey.indexOf("`")+1,strKey.length());
			
		
			String className = "trDeep";
			if(x%2==0) {
                className = "trShallow";
            }
			String color = "#F3F5FC";
			if("trDeep".equals(className)) {
                color = "#DDEAFE";
            }
			bodyHtml.append("<tr class='"+className+"' onClick='javascript:tr_onclick(this,\""+color+"\")'   >");
			if(n==1){//如果只有一个单位或部门
				bodyHtml.append("<td align='left' class='t_cell_locked' nowrap>&nbsp;&nbsp;");
			}else if(n==2){//如果有两个单位或部门
				if(i==0) {
                    bodyHtml.append("<td align='left' class='t_cell_locked_first' nowrap>&nbsp;&nbsp;");
                } else {
                    bodyHtml.append("<td align='left' class='t_cell_locked_last' nowrap>&nbsp;&nbsp;");
                }
			}else if(n>=3){//如果有三个或以上单位或部门
				if(i==0) {
                    bodyHtml.append("<td align='left' class='t_cell_locked_first' nowrap>&nbsp;&nbsp;");
                } else if(i==n-1) {
                    bodyHtml.append("<td align='left' class='t_cell_locked_last' nowrap>&nbsp;&nbsp;");
                } else {
                    bodyHtml.append("<td align='left' class='t_cell_locked_middle' nowrap>&nbsp;&nbsp;");
                }
			}
			
			
			if(uoreMap.get(codeitemid)==null) {
                bodyHtml.append("<a href='/performance/workplan/workplanstatus.do?b_query=link&opt=2&codeid="+codeitemid+"'  >");
            }
			bodyHtml.append("&nbsp;&nbsp;"+codeitemdesc);
			if(uoreMap.get(codeitemid)==null) {
                bodyHtml.append("</a>");
            }
			bodyHtml.append("</td>");
			for(int j=0;j<cycleDaysList.size();j++)
			{
				String cycled = (String)cycleDaysList.get(j);
				for(int e=0;e<list.size();e++)
				{
					String name = cycled+"`"+(String)list.get(e);
					String state = name.split("`")[0];
					String report_status = name.split("`")[1];
					String value = "";
					if(codeCycleTypeMap.get(name)!=null) {
                        value = (String)codeCycleTypeMap.get(name);
                    }
					if("0".equalsIgnoreCase(value)) {
                        value = "";
                    }
					
					bodyHtml.append("<td align='center' class='RecordRow2' nowrap>");
					bodyHtml.append("<a href='javascript:reverseResult(\""+state+"\",\""+report_status+"\",\""+codeitemid+"\")' >");
					bodyHtml.append(value);
					bodyHtml.append("</a></td>");
				}
			}
			bodyHtml.append("</tr>");
			x++;
			i++;
		}
		

/*		
		bodyHtml.append("<tr class='"+(a_className.equals("trShallow")?"trDeep":"trShallow")+"'>");
		bodyHtml.append("<td align='left' class='t_cell_locked_b' bgColor='#FFFDC5' nowrap>&nbsp;&nbsp;");
		bodyHtml.append("</td>");
		for(int j=0;j<cycleDaysList.size();j++)
		{
			RecordVo vo=(RecordVo)cycleDaysList.get(j);
			String  tsortid=vo.getString("tsortid");
			for(int e=0;e<list.size();e++)
			{
				String name=(String)list.get(e)+"/"+tsortid;
				bodyHtml.append("<td align='center' class='RecordRow' bgColor='#FFFDC5' nowrap>");
				String value=(String)countMap.get(name);
				bodyHtml.append(value.equals("0")?"":value);
				bodyHtml.append("</td>");
			}
		}
		bodyHtml.append("</tr>");
*/		
		
		
		return bodyHtml.toString();
	}
	
	
	/**
	 *  type : 1:工作计划 2:工作总结
	 *  cycle : 0:日报 1:周报 2:月报 3:季报 4:年报
	 *  year : 选择的年度
	 */
	public LinkedHashMap getUnitStatusList(String codeid,String cycle,String year,String quarter,String month,String type)
	{		
		LinkedHashMap codeitemidMap = new LinkedHashMap();  // 统计单位或部门	
		RowSet rowSet = null;
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		try
		{			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqlstr = new StringBuffer();	
			StringBuffer sqlwhl = new StringBuffer();	
			
			if("0".equalsIgnoreCase(cycle)) {
                sqlstr.append("SELECT "+ Sql_switcher.month("p0104") +" cyclemonth,"+ Sql_switcher.day("p0104") +" cycle,O.codesetid,O.codeitemid,O.codeitemdesc, ");
            } else if("1".equalsIgnoreCase(cycle)) {
                sqlstr.append("SELECT "+ Sql_switcher.month("p0104") +" cyclemonth,"+ Sql_switcher.week("p0104")+" cycle,O.codesetid,O.codeitemid,O.codeitemdesc, ");
            } else if("2".equalsIgnoreCase(cycle)) {
                sqlstr.append("SELECT "+ Sql_switcher.month("p0104") +" cycle,O.codesetid,O.codeitemid,O.codeitemdesc, ");
            } else if("3".equalsIgnoreCase(cycle)) {
                sqlstr.append("SELECT "+Sql_switcher.quarter("p0104")+" cycle,O.codesetid,O.codeitemid,O.codeitemdesc, ");
            } else if("4".equalsIgnoreCase(cycle)) {
                sqlstr.append("SELECT "+ Sql_switcher.year("p0104") +" cycle,O.codesetid,O.codeitemid,O.codeitemdesc, ");
            }
			sqlstr.append(" CASE WHEN (p0115='01' or p0115='07') THEN '01' ELSE p0115 END p0115,COUNT(a0100) prenum ");
			sqlstr.append(" from organization O,p01 P where log_type = '"+ type +"' and P.state = '"+ cycle +"' and "+ Sql_switcher.year("p0104") +" = '"+ year +"' ");
			
			sqlwhl.append(" and p.log_type = '"+ type +"' and p.state = '"+ cycle +"' and "+ Sql_switcher.year("p0104") +" = '"+ year +"' ");						
			if("0".equalsIgnoreCase(cycle))
			{
				sqlstr.append(" and "+ Sql_switcher.month("p0104") +" = '"+ month +"' ");
				sqlwhl.append(" and "+ Sql_switcher.month("p0104") +" = '"+ month +"' ");
			}
			else if("1".equalsIgnoreCase(cycle))
			{				
				sqlstr.append(" "+ weekDate(year,month) +" ");	
				sqlwhl.append(" "+ weekDate(year,month) +" ");	
			}
			else if("2".equalsIgnoreCase(cycle) && !"all".equalsIgnoreCase(quarter))
			{
				sqlstr.append(" and "+ Sql_switcher.quarter("p0104") +" = '"+ quarter +"' ");
			}
			if(codeid==null || codeid.trim().length()<=0)
			{		
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sqlstr.append(" and "+ Sql_switcher.left("P.b0110",Sql_switcher.length("O.codeitemid")) +"=O.codeitemid ");
                } else {
                    sqlstr.append(" and "+ Sql_switcher.left("P.b0110",Sql_switcher.length("O.codeitemid")) +"=O.codeitemid ");
                }
				sqlstr.append("	and O.parentid in (select codeitemid from organization where codeitemid = parentid and codesetid<>'@K' ) and O.codeitemid <> O.parentid ");				
			}
			else
			{					
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sqlstr.append(" and ("+ Sql_switcher.left("P.b0110",Sql_switcher.length("O.codeitemid")) +"=O.codeitemid or "+ Sql_switcher.left("P.e0122",Sql_switcher.length("O.codeitemid")) +"=O.codeitemid) ");
                } else {
                    sqlstr.append(" and ("+ Sql_switcher.left("P.b0110",Sql_switcher.length("O.codeitemid")) +"=O.codeitemid or "+ Sql_switcher.left("P.e0122",Sql_switcher.length("O.codeitemid")) +"=O.codeitemid) ");
                }
				sqlstr.append("	and O.parentid<>O.codeitemid and O.parentid='"+ codeid +"'  ");												
			}
			sqlstr.append(" and "+Sql_switcher.dateValue(bosdate)+" between O.start_date and O.end_date ");
			sqlstr.append( 	getUserViewSortWhere(this.userView,"P") );
			sqlstr.append(this.getBaseWhere(this.getIntersection())); // 得到管理库的查询条件
			sqlstr.append(" group by ");
			if("0".equalsIgnoreCase(cycle)) {
                sqlstr.append(" "+ Sql_switcher.month("p0104") +","+ Sql_switcher.day("p0104") +",");
            } else if("1".equalsIgnoreCase(cycle)) {
                sqlstr.append(" "+ Sql_switcher.month("p0104") +","+Sql_switcher.week("p0104")+",");
            } else if("2".equalsIgnoreCase(cycle)) {
                sqlstr.append(" "+ Sql_switcher.month("p0104") +",");
            } else if("3".equalsIgnoreCase(cycle)) {
                sqlstr.append(" "+Sql_switcher.quarter("p0104")+",");
            } else if("4".equalsIgnoreCase(cycle)) {
                sqlstr.append(" "+ Sql_switcher.year("p0104") +",");
            }
			sqlstr.append(" O.codesetid,O.codeitemid,O.codeitemdesc,CASE WHEN (p0115='01' or p0115='07') THEN '01' ELSE p0115 END ");
			sqlstr.append(" order by O.codeitemid,cycle ");
																					
			rowSet = dao.search(sqlstr.toString());					
			while(rowSet.next())
			{	
				String codesetid = isNull(rowSet.getString("codesetid"));		
				String codeitemid = isNull(rowSet.getString("codeitemid"));															
				String codeitemdesc = isNull(rowSet.getString("codeitemdesc"));				
				String cycled = isNull(rowSet.getString("cycle"));
				String p0115 = isNull(rowSet.getString("p0115")); // 审批标志 关联相关代码类23，01，起草 02，报批 03，已批 
				String prenum = isNull(rowSet.getString("prenum"));
				
				// 统计的时候加上未填的人数
				if(p0115!=null && p0115.trim().length()>0 && "01".equals(p0115))
				{
					String str = "";
					if("2".equalsIgnoreCase(cycle))
					{
						str = " and "+ Sql_switcher.month("p0104") +" = '"+ cycled +"' ";						
					}
					else if("3".equalsIgnoreCase(cycle))
					{
						if("1".equalsIgnoreCase(cycled)) {
                            str = " and "+ Sql_switcher.month("p0104") +" in ('1','2','3') ";
                        } else if("2".equalsIgnoreCase(cycled)) {
                            str = " and "+ Sql_switcher.month("p0104") +" in ('4','5','6') ";
                        } else if("3".equalsIgnoreCase(cycled)) {
                            str = " and "+ Sql_switcher.month("p0104") +" in ('7','8','9') ";
                        } else if("4".equalsIgnoreCase(cycled)) {
                            str = " and "+ Sql_switcher.month("p0104") +" in ('10','11','12') ";
                        }
					}
					prenum = String.valueOf(Integer.parseInt(prenum)+ getNowritePersonTotle(codesetid,codeitemid,sqlwhl.toString(),str));
				}								
								
				if(codeitemidMap.get(codeitemid+"`"+codeitemdesc)!=null)
				{
					HashMap map = (HashMap)codeitemidMap.get(codeitemid+"`"+codeitemdesc);
					map.put(cycled+"`"+p0115,prenum);
					codeitemidMap.put(codeitemid+"`"+codeitemdesc, map);	
				}
				else
				{
					HashMap map = new HashMap();
					map.put(cycled+"`"+p0115,prenum);
					codeitemidMap.put(codeitemid+"`"+codeitemdesc,map);
				}				
								
				this.haveCycleMap.put(isNull(rowSet.getString("cycle")), "1");
			}
			
			// 得到管理范围内的单位或部门
			String minTime = getCycleMinTime(cycle); // 最小时间
			String[] time = minTime.split("-");			
			ArrayList unitList = getUnitList(codeid);				
			for(int i=0;i<unitList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)unitList.get(i);
				String codeset_id = (String)abean.get("codesetid");
				String codeitem_id = (String)abean.get("codeitemid");
				String codeitem_desc = (String)abean.get("codeitemdesc");
				
			//	if((codeitemidMap.get(codeitem_id+"`"+codeitem_desc)==null) || ())
				{
					HashMap codeMap = (HashMap)codeitemidMap.get(codeitem_id+"`"+codeitem_desc);
																				
					ArrayList cycleDayList = getCycleDaysList(cycle,year,quarter,month,"2"); // 周期
					for(int k=0;k<cycleDayList.size();k++)
					{
						String cycled = (String)cycleDayList.get(k);
												
						if("0".equalsIgnoreCase(cycle))
						{
							if(Integer.parseInt(cycled)<Integer.parseInt(time[2])) {
                                continue;
                            }
						}
						else if("1".equalsIgnoreCase(cycle))
						{
							if(Integer.parseInt(month)<Integer.parseInt(time[1])) {
                                continue;
                            }
						}	
						else if("2".equalsIgnoreCase(cycle))
						{
							if(Integer.parseInt(cycled)<Integer.parseInt(time[1])) {
                                continue;
                            }
						}	
						else if("3".equalsIgnoreCase(cycle))
						{
							if(Integer.parseInt(cycled)<Integer.parseInt(time[1])) {
                                continue;
                            }
						}	
						else if("4".equalsIgnoreCase(cycle))
						{
							if(Integer.parseInt(cycled)<Integer.parseInt(time[0])) {
                                continue;
                            }
						}
												
						String pernumber = "0";
						if(codeMap!=null && codeMap.size()>0 && (codeMap.get(cycled+"`01")!=null || codeMap.get(cycled+"`02")!=null || codeMap.get(cycled+"`03")!=null))
						{
							String str = "";
							if("1".equals(cycle)){//日志类型为周报
								str = " and "+ Sql_switcher.week("p0104") + " = " + cycled;
							}else if("2".equalsIgnoreCase(cycle))
							{
								str = " and "+ Sql_switcher.month("p0104") +" = '"+ cycled +"' ";						
							}
							else if("3".equalsIgnoreCase(cycle))
							{
								if("1".equalsIgnoreCase(cycled)) {
                                    str = " and "+ Sql_switcher.month("p0104") +" in ('1','2','3') ";
                                } else if("2".equalsIgnoreCase(cycled)) {
                                    str = " and "+ Sql_switcher.month("p0104") +" in ('4','5','6') ";
                                } else if("3".equalsIgnoreCase(cycled)) {
                                    str = " and "+ Sql_switcher.month("p0104") +" in ('7','8','9') ";
                                } else if("4".equalsIgnoreCase(cycled)) {
                                    str = " and "+ Sql_switcher.month("p0104") +" in ('10','11','12') ";
                                }
							}
							pernumber = String.valueOf(getNowritePersonTotle(codeset_id,codeitem_id,sqlwhl.toString(),str));
						}
						else {
                            pernumber = String.valueOf(getNowritePersonTotle(codeset_id,codeitem_id,"",""));
                        }
						
						if((codeMap==null || codeMap.size()<=0) || (codeMap!=null && codeMap.size()>0 && codeMap.get(cycled+"`01")==null))
						{
							if(codeitemidMap.get(codeitem_id+"`"+codeitem_desc)!=null)
							{
								HashMap map = (HashMap)codeitemidMap.get(codeitem_id+"`"+codeitem_desc);
								map.put(cycled+"`"+"01",pernumber);
								codeitemidMap.put(codeitem_id+"`"+codeitem_desc, map);	
							}
							else
							{
								HashMap map = new HashMap();
								map.put(cycled+"`"+"01",pernumber);
								codeitemidMap.put(codeitem_id+"`"+codeitem_desc,map);
							}
						}
					}
				}				
			}			
			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}				
		return codeitemidMap;
	}
	
	/**
	 * 得到管理范围内的除去p01的人员(即未填写纪实的人员数)
	 */
	public int getNowritePersonTotle(String codesetid,String codeitemid,String sqlwhl,String str)
	{
		RowSet rowSet = null;
		int totalcount = 0;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);			
			ArrayList list = this.getIntersection();
						
			if(list!=null && list.size()>0)
			{
				StringBuffer sql = new StringBuffer();
				sql.append("select count(a0100) as total from ("); 						
				for(int i=0;i<list.size();i++)
				{
					sql.append("select a0100 from "+list.get(i)+"a01 u ");
					sql.append(" where 1=1 "+getUserViewPersonWhere(this.userView)+" ");
					if("UN".equalsIgnoreCase(codesetid)) {
                        sql.append(" and b0110 like '"+codeitemid+"%'  ");
                    } else {
                        sql.append(" and e0122 like '"+codeitemid+"%'  ");
                    }
					
					if(sqlwhl!=null && sqlwhl.trim().length()>0)
					{
						sql.append(" and a0100 not in (select a0100 from p01 p where nbase='"+list.get(i)+"'"+getUserViewPersonWhere(this.userView)+" ");
						if("UN".equalsIgnoreCase(codesetid)) {
                            sql.append(" and b0110 like '"+codeitemid+"%' "+ sqlwhl + str +") ");
                        } else {
                            sql.append(" and e0122 like '"+codeitemid+"%' "+ sqlwhl + str +") ");
                        }
					}
					
					if(i!=list.size()-1) {
                        sql.append(" union all ");
                    }
				}
				sql.append(") a"); 
			
				rowSet = dao.search(sql.toString());
				if(rowSet.next()) {
                    totalcount = rowSet.getInt("total");
                }
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
			
		}catch(Exception e)
		{
			e.printStackTrace();  
		}
		return totalcount;
	}
	
	/**
	 * 获得周报日期范围
	 * @return
	 */
	public String weekDate(String year,String month)
    {
		String str = "";
		
		// 得到某年某月有几个星期 
		WeekUtils weekutils = new WeekUtils();
		int totalweek = weekutils.totalWeek(Integer.parseInt(year),Integer.parseInt(month));
		String startime = ""; // 得到某年某月第一个星期的第一天的日期 
		String endtime = ""; // 得到某年某月最后一个星期的最后一天的日期  
		for(int i=1;i<=totalweek;i++)
		{		
			if(i==1)
			{
				Date startdate = weekutils.numWeek(Integer.parseInt(year),Integer.parseInt(month),i,1);					
				startime = weekutils.dateTostr(startdate);
			}
			if(i==totalweek)
			{
				Date enddate = weekutils.numWeek(Integer.parseInt(year),Integer.parseInt(month),i,7);
				endtime = weekutils.dateTostr(enddate);
			}
		}
		StringBuffer strSql = new StringBuffer();
		
		StringBuffer buf = new StringBuffer();
		buf.append(Sql_switcher.year("p0104")+ ">"+ getDatePart(startime,"y") +" or ");
		buf.append("("+Sql_switcher.year("p0104")+ "="+ getDatePart(startime,"y")+" and ");
		buf.append(Sql_switcher.month("p0104")+ ">"+ getDatePart(startime,"m") +") or ");
		buf.append("("+Sql_switcher.year("p0104")+ "="+ getDatePart(startime,"y")+" and ");
		buf.append(Sql_switcher.month("p0104")+ "="+ getDatePart(startime,"m") +" and ");
		buf.append(Sql_switcher.day("p0104")+ ">="+ getDatePart(startime,"d") +")");
		strSql.append(" and ("+buf.toString()+") ");
		
		StringBuffer buff = new StringBuffer();
		buff.append(Sql_switcher.year("p0104")+ "<"+ getDatePart(endtime,"y") +" or ");
		buff.append("("+Sql_switcher.year("p0104")+ "="+ getDatePart(endtime,"y")+" and ");
		buff.append(Sql_switcher.month("p0104")+ "<"+ getDatePart(endtime,"m") +") or ");
		buff.append("("+Sql_switcher.year("p0104")+ "="+ getDatePart(endtime,"y")+" and ");
		buff.append(Sql_switcher.month("p0104")+ "="+ getDatePart(endtime,"m") +" and ");
		buff.append(Sql_switcher.day("p0104")+ "<="+ getDatePart(endtime,"d") +")");
		strSql.append(" and ("+buff.toString()+") ");
		
		str = strSql.toString();
						
		return str;
    }
	
	/**
	 * 分解当前时间
	 */
	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart)) {
            str = mydate.substring(0, 4);
        } else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6))) {
                str = mydate.substring(6, 7);
            } else {
                str = mydate.substring(5, 7);
            }
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9))) {
                str = mydate.substring(9, 10);
            } else {
                str = mydate.substring(8, 10);
            }
		}
		return str;
	}
	
	/**
	 * 获得单位或部门名称
	 * @return
	 */	
	public String getUnitOrE0122Name(String codeid)
	{
		StringBuffer str = new StringBuffer("");
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer buf = new StringBuffer("");
		    buf.append("select parentid,codeitemid,codeitemdesc from organization where 1=1 ");
		    if(codeid==null || codeid.trim().length()<=0) {
                buf.append(" and parentid=codeitemid and codesetid<>'@K' ");
            } else
		    {
		    	buf.append(" and codeitemid='"+ codeid +"'  ");
		   // 	buf.append("(select codeitemid from organization where codeitemid = parentid ) ");
		    }
		    rs = dao.search(buf.toString());		    
		    while(rs.next())
		    {
		    	String parentid  = isNull(rs.getString("parentid"));	
		    	String codeitemid = isNull(rs.getString("codeitemid"));
		    	
		    	if(codeid==null || codeid.trim().length()<=0)
		    	{
		    		if(parentid!=null && parentid.trim().length()>0 && codeitemid!=null && codeitemid.trim().length()>0 && parentid.equalsIgnoreCase(codeitemid)) {
                        str.append("<a href='/performance/workplan/workplanstatus.do?b_query=link&opt=3&codeid=init'  >");
                    } else {
                        str.append("<a href='/performance/workplan/workplanstatus.do?b_query=link&opt=3&codeid="+isNull(rs.getString("codeitemid"))+"'  >");
                    }
			    	str.append("&nbsp;"+isNull(rs.getString("codeitemdesc")));				
					str.append("</a>");	
					
					break;
		    	}
		    	else
		    	{
		    		if(parentid!=null && parentid.trim().length()>0 && codeitemid!=null && codeitemid.trim().length()>0 && !parentid.equalsIgnoreCase(codeitemid))
			    	{
				    	String strCont = getUnitOrE0122Name(parentid);
				    	str.append(strCont+"-->");
			    	}		    	
			    	if(parentid!=null && parentid.trim().length()>0 && codeitemid!=null && codeitemid.trim().length()>0 && parentid.equalsIgnoreCase(codeitemid)) {
                        str.append("<a href='/performance/workplan/workplanstatus.do?b_query=link&opt=3&codeid=init'  >");
                    } else {
                        str.append("<a href='/performance/workplan/workplanstatus.do?b_query=link&opt=3&codeid="+isNull(rs.getString("codeitemid"))+"'  >");
                    }
			    	str.append("&nbsp;"+isNull(rs.getString("codeitemdesc")));				
					str.append("</a>");
		    	}
		    	
		    	/*
		    	if(codeid!=null && codeid.trim().length()>0)
		    	{			    				    	
			    	if(parentid!=null && parentid.trim().length()>0 && codeitemid!=null && codeitemid.trim().length()>0 && !parentid.equalsIgnoreCase(codeitemid))
			    	{
				    	String strCont = getUnitOrE0122Name(parentid);
				    	str.append(strCont+"-->");
			    	}
		    	}
		    	if(parentid!=null && parentid.trim().length()>0 && codeitemid!=null && codeitemid.trim().length()>0 && parentid.equalsIgnoreCase(codeitemid))			    	
		    		str.append("<a href='/performance/workplan/workplanstatus.do?b_query=link&opt=3&codeid=init'  >");
		    	else
		    		str.append("<a href='/performance/workplan/workplanstatus.do?b_query=link&opt=3&codeid="+isNull(rs.getString("codeitemid"))+"'  >");
		    	str.append("&nbsp;"+isNull(rs.getString("codeitemdesc")));				
				str.append("</a>");	
				*/
		    }		    
		    if(rs!=null) {
                rs.close();
            }
		    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return str.toString();
	}
	
	/**
	 * 获得organization表里的childid字段为空的所有单位或部门
	 * @return 
	 */	
	public HashMap getUnitOrE0122()
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer buf = new StringBuffer("");		    
		    buf.append("select codeitemid,codeitemdesc from organization where codesetid<>'@K' ");	
		    buf.append(" and (childid is null or "+ Sql_switcher.isnull("childid", "''") +"='' or childid = codeitemid ");
		    buf.append(" or childid in (select codeitemid from organization where codesetid='@K') ) ");
		    rs = dao.search(buf.toString());		    
		    while(rs.next())
		    {		    	
		    	map.put(isNull(rs.getString("codeitemid")),isNull(rs.getString("codeitemdesc")));
		    }		    
		    if(rs!=null) {
                rs.close();
            }
		    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	
	
	/**
	 * 取得查询年列表
	 * @return
	 */
	public ArrayList getYearTypeList()
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer buf = new StringBuffer("");
		    buf.append("select distinct "+ Sql_switcher.year("p0104") +" as ayear from p01 ");
		    buf.append(" where 1=1 "+ getUserViewPersonWhere(this.userView));
		    buf.append(" order by "+ Sql_switcher.year("p0104") +" desc");		    
		    rs = dao.search(buf.toString());		    		    
		    boolean isAddThisYear = true;
		    String thisYear=Calendar.getInstance().get(Calendar.YEAR)+"";
		    while(rs.next())
		    {
		    	String ayear = isNull(rs.getString("ayear"));
		    	if(ayear.equalsIgnoreCase(thisYear)) {
                    isAddThisYear=false;
                }
		    	list.add(new CommonData(ayear,ayear));
		    }
		    if(isAddThisYear) {
                list.add(new CommonData(thisYear,thisYear));
            }
		    if(rs!=null) {
                rs.close();
            }
		    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得查询季度列表
	 * @return
	 */
	public ArrayList getQuarterTypeList()
	{
		ArrayList list = new ArrayList();
		
    	list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));		
		list.add(new CommonData("1",ResourceFactory.getProperty("log.teamwork.workplan.oneQuarter")));//第1季度
		list.add(new CommonData("2",ResourceFactory.getProperty("log.teamwork.workplan.twoQuarter")));
		list.add(new CommonData("3",ResourceFactory.getProperty("log.teamwork.workplan.threeQuarter")));
		list.add(new CommonData("4",ResourceFactory.getProperty("log.teamwork.workplan.fourQuarter")));
		
		return list;
	}
	/**
	 * 取得查询月列表
	 * @return
	 */
	public ArrayList getMonthTypeList()
	{
		ArrayList monthlist = new ArrayList();
		for(int i=1;i<=12;i++)
		{
			CommonData obj = new CommonData(i+"",i+" 月");
			monthlist.add(obj);
		}
		return monthlist;		
	}
	
	/**
	 * 获得日志类型 cycle 0:日报 1:周报 2:月报 3:季报 4:年报
	 */
	public ArrayList getCycleDaysList(String cycle ,String year, String quarter, String month, String flag)
	{
		HashMap weekOFyearMap = new HashMap();
		int count = 0;
		if("0".equalsIgnoreCase(cycle))
		{
			// 得到某年某月的天数
			Date mDate = null; 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			try
			{ 
				mDate = sdf.parse(year+"-"+month+"-"+"01"); 
			}catch(ParseException pe){
				pe.printStackTrace();
			} 
			GregorianCalendar cal = new GregorianCalendar(); 
			cal.setTime(mDate);
			cal.add(Calendar.MONTH,1);   
			cal.add(Calendar.DAY_OF_MONTH,-1);
			count = cal.get(Calendar.DATE);			
		}
		else if("1".equalsIgnoreCase(cycle))
		{
			// 得到某年某月有几个星期
			WeekUtils weekutils = new WeekUtils();
			count = weekutils.totalWeek(Integer.parseInt(year),Integer.parseInt(month));
			
			for(int i=1;i<=count;i++)
			{						
				Date startdate = weekutils.numWeek(Integer.parseInt(year),Integer.parseInt(month),i,3);	// 每周三的日期				
				String startime = weekutils.dateTostr(startdate);
								
				// 获得当前时间属于一年中第几周          
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        try 
		        {
		            Date d = sdf.parse(startime);
		            Calendar c=Calendar.getInstance();
		            c.setTime(d);
		            c.setFirstDayOfWeek(Calendar.SUNDAY);//设置周日为每周的第一天.Calendar.SUNDAY为1   美国是以周日为每周的第一天
		            
		            weekOFyearMap.put(String.valueOf(i), String.valueOf(c.get(Calendar.WEEK_OF_YEAR)));
		            		            
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
			}
			
		}
		else if("2".equalsIgnoreCase(cycle))
		{
			if("all".equalsIgnoreCase(quarter)) {
                count = 12;
            } else {
                count = 3;
            }
		}
		else if("3".equalsIgnoreCase(cycle)) {
            count = 4;
        } else if("4".equalsIgnoreCase(cycle)) {
            count = 1;
        }
				
		ArrayList cycleList = new ArrayList();		
		try
		{	
			int k = 0;
			for(int i=1;i<=count;i++)
			{
				if(!"all".equalsIgnoreCase(quarter) && "1".equalsIgnoreCase(quarter))
				{
					if(i==1) {
                        k = 1;
                    } else {
                        k++;
                    }
				}
				else if(!"all".equalsIgnoreCase(quarter) && "2".equalsIgnoreCase(quarter))
				{
					if(i==1) {
                        k = 4;
                    } else {
                        k++;
                    }
				}
				else if(!"all".equalsIgnoreCase(quarter) && "3".equalsIgnoreCase(quarter))
				{
					if(i==1) {
                        k = 7;
                    } else {
                        k++;
                    }
				}
				else if(!"all".equalsIgnoreCase(quarter) && "4".equalsIgnoreCase(quarter))
				{
					if(i==1) {
                        k = 10;
                    } else {
                        k++;
                    }
				}
				
				if("1".equalsIgnoreCase(flag))
				{
					if("0".equalsIgnoreCase(cycle)) {
                        cycleList.add(""+ i +" 号");
                    } else if("1".equalsIgnoreCase(cycle)) {
                        cycleList.add("第 "+ i +" 周");
                    } else if("2".equalsIgnoreCase(cycle))
					{
						if(!"all".equalsIgnoreCase(quarter)) {
                            cycleList.add(""+ k +" 月");
                        } else {
                            cycleList.add(""+ i +" 月");
                        }
					}
					else if("3".equalsIgnoreCase(cycle)) {
                        cycleList.add("第 "+ i +" 季度");
                    } else if("4".equalsIgnoreCase(cycle)) {
                        cycleList.add(""+ year +" 年");
                    }
				}else
				{
					if("0".equalsIgnoreCase(cycle)) {
                        cycleList.add(""+ i +"");
                    } else if("1".equalsIgnoreCase(cycle)) {
                        cycleList.add(""+ (String)weekOFyearMap.get(String.valueOf(i)) +"");
                    } else if("2".equalsIgnoreCase(cycle))
					{
						if(!"all".equalsIgnoreCase(quarter)) {
                            cycleList.add(""+ k +"");
                        } else {
                            cycleList.add(""+ i +"");
                        }
					}
					else if("3".equalsIgnoreCase(cycle)) {
                        cycleList.add(""+ i +"");
                    } else if("4".equalsIgnoreCase(cycle)) {
                        cycleList.add(""+ year +"");
                    }
				}
			}										
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return cycleList;				
	}
	
	/**
	 * 获得不同日志类型 cycle 0:日报 1:周报 2:月报 3:季报 4:年报下的系统上线最小时间
	 */
	public String getCycleMinTime(String cycle)
	{
		String minTime = PubFunc.getStringDate("yyyy-MM-dd");
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();		    		    		   		    			
			buf.append(" select min(p0104) as p0104 from p01 where state = '"+ cycle +"' ");											
			rs = dao.search(buf.toString());
			if(rs.next())
			{								
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				if(rs.getDate("p0104")!=null) {
                    minTime = format.format(rs.getDate("p0104"));
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return minTime;				
	}	
	
	/**
	 * Description:  本月是第几季度
	 * @param month
	 * @return
	 */
	public String getSeason(String month)
	{		
        int ynum = Integer.parseInt(month);
		String returnValue="";
		if(ynum==1 || ynum==2 || ynum==3) {
            returnValue = "1";
        } else if(ynum==4 || ynum==5 || ynum==6) {
            returnValue = "2";
        } else if(ynum==7 || ynum==8 || ynum==9) {
            returnValue = "3";
        } else if(ynum==10 || ynum==11 || ynum==12) {
            returnValue = "4";
        }
		
	    return returnValue;
	}
	
	/**
	 * 获得日志类型 0:日报 1:周报 2:月报 3:季报 4:年报
	 */
	public ArrayList getCycleTypeList()
	{
		ArrayList cycleList = new ArrayList();		
		try
		{	
			String valid0=(String)WorkPlanViewBo.workParametersMap.get("valid0");
			String valid11 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid11"));
			String valid21 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid21"));
			String valid12 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid12"));
			String valid22 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid22"));
			String valid13 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid13"));
			String valid23 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid23"));	
			String valid14 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid14"));
			String valid24 = isNull((String)WorkPlanViewBo.workParametersMap.get("valid24"));
						
			if((valid14!=null && valid14.trim().length()>0 && "1".equals(valid14)) || (valid24!=null && valid24.trim().length()>0 && "1".equals(valid24))) {
                cycleList.add(new CommonData("4", ResourceFactory.getProperty("performance.workdiary.workyear")));
            }
			if((valid13!=null && valid13.trim().length()>0 && "1".equals(valid13)) || (valid23!=null && valid23.trim().length()>0 && "1".equals(valid23))) {
                cycleList.add(new CommonData("3", ResourceFactory.getProperty("performance.workdiary.workseason")));
            }
			if((valid12!=null && valid12.trim().length()>0 && "1".equals(valid12)) || (valid22!=null && valid22.trim().length()>0 && "1".equals(valid22))) {
                cycleList.add(new CommonData("2", ResourceFactory.getProperty("performance.workdiary.workmonth")));
            }
			if((valid11!=null && valid11.trim().length()>0 && "1".equals(valid11)) || (valid21!=null && valid21.trim().length()>0 && "1".equals(valid21))) {
                cycleList.add(new CommonData("1", ResourceFactory.getProperty("performance.workdiary.workweek")));
            }
			if(valid0!=null && valid0.trim().length()>0 && "1".equals(valid0)) {
                cycleList.add(new CommonData("0", ResourceFactory.getProperty("performance.workdiary.workday")));
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return cycleList;				
	}
	
	/**
	 * 获得日志类型 1:工作计划 2:工作总结
	 */
	public ArrayList getTypeList()
	{
		ArrayList typeList = new ArrayList();		
		try
		{	
			HashMap<String,String> workParametersMap = WorkPlanViewBo.workParametersMap;
			if(workParametersMap.size()==0){
				WorkPlanViewBo bo = new WorkPlanViewBo(this.userView,this.conn);
				bo.analyseParameter();
			}
			//valid1* =计划；valid2* =总结
			String valid11 = isNull(workParametersMap.get("valid11"));
			String valid21 = isNull(workParametersMap.get("valid21"));
			String valid12 = isNull(workParametersMap.get("valid12"));
			String valid22 = isNull(workParametersMap.get("valid22"));
			String valid13 = isNull(workParametersMap.get("valid13"));
			String valid23 = isNull(workParametersMap.get("valid23"));	
			String valid14 = isNull(workParametersMap.get("valid14"));
			String valid24 = isNull(workParametersMap.get("valid24"));
			if(!("0".equals(valid11)&&"0".equals(valid12)&&"0".equals(valid13)&&"0".equals(valid14))) {
                typeList.add(new CommonData("1", ResourceFactory.getProperty("performance.workdiary.workplan")));
            }
				
			if(!("0".equals(valid21)&&"0".equals(valid22)&&"0".equals(valid23)&&"0".equals(valid24))) {
                typeList.add(new CommonData("2", ResourceFactory.getProperty("performance.workdiary.worksummary")));
            }
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return typeList;				
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 */
	public String getUserViewPersonWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or b0110 like '" + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or e0122 like '" + temp[i].substring(2) + "%'");
                    }
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else if("UN".equalsIgnoreCase(codeid)) {
                        buf.append(" and b0110 like '" + codevalue + "%'");
                    } else if("UM".equalsIgnoreCase(codeid)) {
                        buf.append(" and e0122 like '" + codevalue + "%'");
                    } else if("@K".equalsIgnoreCase(codeid)) {
                        buf.append(" and e01a1 like '" + codevalue + "%'");
                    } else {
                        buf.append(" and b0110 like '" + codevalue + "%'");
                    }
						
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}

		return str;		
	}
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 */
	public String getUserViewSortWhere(UserView userView,String base)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or "+base+".b0110 like '" + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or "+base+".e0122 like '" + temp[i].substring(2) + "%'");
                    }
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else if("UN".equalsIgnoreCase(codeid)) {
                        buf.append(" and "+base+".b0110 like '" + codevalue + "%'");
                    } else if("UM".equalsIgnoreCase(codeid)) {
                        buf.append(" and "+base+".e0122 like '" + codevalue + "%'");
                    } else if("@K".equalsIgnoreCase(codeid)) {
                        buf.append(" and "+base+".e01a1 like '" + codevalue + "%'");
                    } else {
                        buf.append(" and "+base+".b0110 like '" + codevalue + "%'");
                    }
						
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}

		return str;		
	}


	public HashMap getHaveCycleMap() {
		return haveCycleMap;
	}

	public void setHaveCycleMap(HashMap haveCycleMap) {
		this.haveCycleMap = haveCycleMap;
	}
	
	/**
	 * 获取用户所管理的库的前缀的交集
	 * @return  交集list
	 */
	public ArrayList getIntersection()
	{
		ArrayList list = new ArrayList();
		HashMap map = WorkPlanViewBo.workParametersMap;
		if(map.size()==0){
			WorkPlanViewBo bo = new WorkPlanViewBo(this.userView,this.conn);
			bo.analyseParameter();
		}
		String strBase = (String)map.get("nbase"); //这种形式：Usr,Ret,Trs
		if(strBase!=null && strBase.trim().length()>0)
		{
			String []base = strBase.split(",");
			ArrayList listBase = this.userView.getPrivDbList(); // 判断有没有进行人员库授权   Usr,Trs,Oth
			HashMap mapbase = new HashMap();
			for(int i=0;i<base.length;i++)
			{
				mapbase.put(base[i].toLowerCase(), "1");
			}
			for(int j=0;j<listBase.size();j++)
			{
				if(mapbase.get(listBase.get(j).toString().toLowerCase())!=null) {
                    list.add(listBase.get(j));
                }
			}
		}
		return list;
	}
	/**
	 * 得到管理库的查询条件，便于组装sql语句
	 * @param  alist   数据库前缀交集的list
	 * @return sql语句的字段查询的条件
	 */
	public String getBaseWhere(ArrayList alist)
	{
		String strWhere= "";
		if(alist.size()!=0) //说明有管辖的库
		{
			String base = "('";
			int n = alist.size();
			for(int i=0;i<n;i++)
			{
				base += alist.get(i)+"','";
			}
			base = base.substring(0,base.length()-2)+")";
			strWhere = " and NBASE in "+base;
		}else {
            strWhere = " and 1=2 ";
        }
		return strWhere;
	}

	// 得到管理范围内的单位或部门
	public ArrayList getUnitList(String codeid)
	{
		ArrayList alist = new ArrayList();
		RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.conn);
			String bosdate = DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			StringBuffer sbsql = new StringBuffer();
			sbsql.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid<>'@K' ");
			if(codeid==null || codeid.trim().length()<=0)
			{								
				sbsql.append("	and codeitemid in (select codeitemid from organization where 1=1 "+ getCodeOrganizationWhere(this.userView,"1")+" ");				
				sbsql.append("	and parentid in (select codeitemid from organization where codeitemid = parentid and codesetid<>'@K' )) ");
				sbsql.append("	and codeitemid <> parentid ");								
			}
			else {
                sbsql.append("	and parentid<>codeitemid and parentid='"+ codeid +"'  ");
            }
			sbsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
			if(codeid==null || codeid.trim().length()<=0)
			{
			//	sbsql.append(getCodeOrganizationWhere(this.userView,"2"));
			}
			else {
                sbsql.append(getUserOrganizationWhere(this.userView));
            }
			sbsql.append(" order by a0000,codeitemid");
			
			rowSet = dao.search(sbsql.toString());
			while(rowSet.next())
			{				
				LazyDynaBean bean = new LazyDynaBean();		
				bean.set("codesetid",isNull(rowSet.getString("codesetid")));
				bean.set("codeitemid",isNull(rowSet.getString("codeitemid")));
				bean.set("codeitemdesc",isNull(rowSet.getString("codeitemdesc")));								
				alist.add(bean);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return alist;
		
	}
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 */
	public String getCodeOrganizationWhere(UserView userView,String flag)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
					{
						if("1".equalsIgnoreCase(flag)) {
                            tempSql.append(" or "+ Sql_switcher.left("'"+temp[i].substring(2)+"'",Sql_switcher.length("codeitemid")) +"=codeitemid " );
                        } else {
                            tempSql.append(" or codeitemid <> '" + temp[i].substring(2) + "'");
                        }
					}
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
					{
						if("1".equalsIgnoreCase(flag)) {
                            tempSql.append(" or "+ Sql_switcher.left("'"+temp[i].substring(2)+"'",Sql_switcher.length("codeitemid")) +"=codeitemid " );
                        } else {
                            tempSql.append(" or codeitemid <> '" + temp[i].substring(2) + "'");
                        }
					}
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else if("UN".equalsIgnoreCase(codeid))
					{
						if("1".equalsIgnoreCase(flag)) {
                            buf.append(" and "+ Sql_switcher.left("'"+codevalue+"'",Sql_switcher.length("codeitemid")) +"=codeitemid " );
                        } else {
                            buf.append(" and codeitemid <> '" + codevalue + "'");
                        }
					}
					else if("UM".equalsIgnoreCase(codeid))
					{
						if("1".equalsIgnoreCase(flag)) {
                            buf.append(" and "+ Sql_switcher.left("'"+codevalue+"'",Sql_switcher.length("codeitemid")) +"=codeitemid " );
                        } else {
                            buf.append(" and codeitemid <> '" + codevalue + "'");
                        }
					}
					else if("@K".equalsIgnoreCase(codeid))
					{
						if("1".equalsIgnoreCase(flag)) {
                            buf.append(" and "+ Sql_switcher.left("'"+codevalue+"'",Sql_switcher.length("codeitemid")) +"=codeitemid " );
                        } else {
                            buf.append(" and codeitemid <> '" + codevalue + "'");
                        }
					}
					else
					{
						if("1".equalsIgnoreCase(flag)) {
                            buf.append(" and "+ Sql_switcher.left("'"+codevalue+"'",Sql_switcher.length("codeitemid")) +"=codeitemid " );
                        } else {
                            buf.append(" and codeitemid <> '" + codevalue + "'");
                        }
					}
						
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}

		return str;		
	}
	
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 */
	public String getUserOrganizationWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or codeitemid like '" + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or codeitemid like '" + temp[i].substring(2) + "%'");
                    }
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else if("UN".equalsIgnoreCase(codeid)) {
                        buf.append(" and codeitemid like '" + codevalue + "%'");
                    } else if("UM".equalsIgnoreCase(codeid)) {
                        buf.append(" and codeitemid like '" + codevalue + "%'");
                    } else if("@K".equalsIgnoreCase(codeid)) {
                        buf.append(" and codeitemid like '" + codevalue + "%'");
                    } else {
                        buf.append(" and codeitemid like '" + codevalue + "%'");
                    }
						
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}

		return str;		
	}
	
}









