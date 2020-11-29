package com.hjsj.hrms.businessobject.report.tt_organization;


import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;



/**
 * 
 * <p>Title:</p>
 * <p>Description:填报单位类</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 12, 2006:4:28:24 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class TTorganization {
	Connection conn=null;
	String backdate="";
	private String validedateflag="";
	public TTorganization(Connection conn)
	{
		this.conn=conn;
	}
	
	public TTorganization()
	{
		
	}
	
	//取得填报单位分配的所有报表类
	public ArrayList getSelfSortList(String userName)
	{
		RecordVo vo=getSelfUnit(userName);
		if(vo==null) {
            return new ArrayList();
        }
		String types=vo.getString("reporttypes");
		ArrayList list=new ArrayList();
		if(types!=null&&!"".equals(types))
		{
			RowSet recset=null;
			try
			{
				
				ContentDAO dao=new ContentDAO(this.conn);
				recset=dao.search("select * from tsort where tsortid in("+types.substring(0,types.lastIndexOf(","))+" ) order by tsortid");
				while(recset.next())
				{
					RecordVo a_vo=new RecordVo("tsort");
					a_vo.setInt("tsortid",recset.getInt("tsortid"));
					a_vo.setString("name",recset.getString("name"));
					a_vo.setString("sdes",recset.getString("sdes"));
					a_vo.setInt("sid",recset.getInt("sid"));
					list.add(a_vo);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return list;
	}
	/*
	 * 查询所有表类
	 */
	public ArrayList getTsortId() {
		ArrayList list =new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs=dao.search("select tsortid from tsort");
			while(rs.next()) {
				list.add(rs.getInt("tsortid")+"");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 取得组织机构的层数，并将每层的node塞入 map
	 * @param laynodemap
	 * @return
	 */
	public int getOrganizationLayNum(HashMap laynodemap)
	{
		int num=0;
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String temp="";
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			//String sql = " where 1=1 "+ext_sql;
			StringBuffer sql = new StringBuffer();
			sql.append(" select t.* from (");
			sql.append(" select a.*,'false' as isleafs  from tt_organization  a where 1=1 " + ext_sql);
			sql.append(" and a.unitcode   in (  select parentid from tt_organization ) ");
			sql.append(" union all");
			sql.append(" select a.*,'true' as isleafs  from tt_organization  a  where 1=1 " + ext_sql);//isleafs 是否是叶子节点   | state :0 不取数 ；：1 取数
			sql.append(" and a.unitcode  not in (  select parentid from tt_organization ) ");
			sql.append(" ) t");
			sql.append(" order by t.grade desc ");
			//RowSet recset=dao.search(" select * from tt_organization "+sql+"  and unitcode  not in (  select parentid from tt_organization ) order by grade desc union select * from tt_organization "+sql+"  and unitcode  not in (  select parentid from tt_organization ) order by grade desc");
			RowSet recset= dao.search(sql.toString());
			ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
			while(recset.next())
			{
				int grade=recset.getInt("grade");
				if(temp.length()==0) {
                    temp=String.valueOf(grade);
                } else
				{
					if(grade!=Integer.parseInt(temp))
					{
						laynodemap.put(temp,list);
						list=new ArrayList<LazyDynaBean>();
						temp=String.valueOf(grade);
					}
				}
				if(num==0) {
                    num=grade;
                }
				
				LazyDynaBean vo=new LazyDynaBean();
				vo.set("unitcode",recset.getString("unitcode"));
				vo.set("unitid",recset.getInt("unitid"));
				vo.set("unitname",recset.getString("unitname"));
				vo.set("parentid",recset.getString("parentid"));
				vo.set("grade",recset.getInt("grade"));
				vo.set("flag",recset.getInt("flag"));
				vo.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.set("report",Sql_switcher.readMemo(recset,"report"));
				vo.set("b0110",recset.getString("b0110"));
				vo.set("isleaf",recset.getString("isleafs"));
				list.add(vo);
			}
			laynodemap.put(temp,list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return num;
	}
	
	/**
	 * 取得各表类的表数
	 * @return
	 */
	public HashMap getSetTabCountMap(String types)
	{
		HashMap map=new HashMap();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer temp_sql=new StringBuffer("select "+Sql_switcher.isnull("count(tabid)","0")+","+Sql_switcher.isnull("tsortid","-1")+" tsortid from tname where tsortid in ("+types+")   ");
			TnameBo tnamebo  = new TnameBo(this.conn);
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0) {
                tabids=tabids.substring(0,tabids.length()-1);
            }
			if(tabids.length()>0) {
                temp_sql.append(" and  tabid not in("+tabids+") ");
            }
			temp_sql.append(" group by ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				temp_sql.append(" rollup (tsortid)");
			}
			else if(Sql_switcher.searchDbServer()==Constant.MSSQL)
			{			
				temp_sql.append(" tsortid with rollup");
			}
			
			 rowSet=dao.search(temp_sql.toString());
			while(rowSet.next())
			{
				map.put(rowSet.getString("tsortid"),rowSet.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return map;
	}
	
	
	/**
	 * 生成报表状态表头信息
	 * @param reportSetList
	 * @return
	 */
	public String getTheadHtml(ArrayList reportSetList,HashMap setTabCountMap)
	{
		StringBuffer html=new StringBuffer("");
		html.append("<thead><tr><td  width='160' class='TableRow' rowspan='2'  nowrap >&nbsp;</td>");//t_cell_locked2
		
		 for(int i=0;i<reportSetList.size();i++){
			RecordVo vo=(RecordVo)reportSetList.get(i);
			String  tsortid=vo.getString("tsortid");
			String name=(String)vo.getString("name");
			if(i==reportSetList.size()-1){
				html.append("<td  class='TableRow' align='center' width='160' colspan='4' nowrap><br>");//t_header_locked1 2013-4-3 赵旭光修改页面表格线不对齐问题  注释的为原来的样式 且原来无居中属性
			}else {
                html.append("<td  class='TableRow' align='center' width='160' colspan='4' nowrap><br>");//t_header_locked
            }
			String count="0";
			if(setTabCountMap.get(tsortid)!=null) {
                count=(String)setTabCountMap.get(tsortid);
            }
			html.append(name); 
			html.append("&nbsp;&nbsp;"+ResourceFactory.getProperty("label.sum")+"&nbsp;<font color='red'>"+count+"</font>&nbsp;"+ResourceFactory.getProperty("muster.label.table"));
			html.append("<br>&nbsp;</td>");
		}	
		html.append("</tr><tr>");
		ArrayList list=new ArrayList();
		list.add(ResourceFactory.getProperty("edit_report.status.wt"));
		list.add(ResourceFactory.getProperty("edit_report.status.bj"));
		list.add(ResourceFactory.getProperty("reportManager.appeal"));
		list.add(ResourceFactory.getProperty("edit_report.status.dh"));
		//list.add(ResourceFactory.getProperty("edit_report.status.fc"));//dml 2011-03-26 封存状态已经不能用
		for(int i=0;i<reportSetList.size();i++){
			for(int j=0;j<list.size();j++)
			{
				if((i==reportSetList.size()-1)&&j==list.size()-1){
					html.append("<td class='TableRow' align='center' width='40' nowrap>");//t_header_locked1
				}else {
                    html.append("<td class='TableRow' align='center' width='40' nowrap>");//t_header_locked
                }
				html.append((String)list.get(j));
				html.append("</td>");
			}
		}
		html.append("</tr></thead>");
		return html.toString();
	}
	
	
	
	/**
	 * 取得表格内容
	 * @param subUnitList
	 * @param tabDataList
	 * @param reportSetList
	 * @return
	 */
	public String getTabBody(ArrayList subUnitList,ArrayList tabDataList,ArrayList reportSetList,String aunitCode)
	{
		StringBuffer bodyHtml=new StringBuffer("");
		ArrayList list=new ArrayList();
		list.add("-1");
		list.add("0");
		list.add("1");
		list.add("2");
		//list.add("3");
		HashMap countMap=new HashMap();
		String a_className="";
		for(int i=0;i<subUnitList.size();i++)
		{
			LazyDynaBean dataBean=(LazyDynaBean)tabDataList.get(i);
			DynaBean bean =(DynaBean)subUnitList.get(i);
			String unitcode=(String)bean.get("unitcode");
			String unitname=(String)bean.get("unitname");
			String className="trDeep";
			if(i%2==0) {
                className="trShallow";
            }
			a_className=className;
			String color="#F3F5FC";
			if("trDeep".equals(className)) {
                color="#DDEAFE";
            }
			bodyHtml.append("<tr class='"+className+"' onClick='javascript:tr_onclick(this,\""+color+"\")'   >");
			// 不再支持列锁定，避免行错位
			bodyHtml.append("<td align='left' class='RecordRow2' nowrap>");  // t_cell_locked
			bodyHtml.append("<a href='/report/report_status.do?b_query=query&encryptParam=" + PubFunc.encrypt("opt="+unitcode)+"' onmouseover='rdl_doClick(\""+unitcode+"\",this)' onmouseout=\"Element.hide('date_panel');\"  >");
			bodyHtml.append(unitname);
			bodyHtml.append("</a>");
			bodyHtml.append("</td>");
			for(int j=0;j<reportSetList.size();j++)
			{
				RecordVo vo=(RecordVo)reportSetList.get(j);
				String  tsortid=vo.getString("tsortid");
				for(int e=0;e<list.size();e++)
				{
					String name=unitcode+"/"+(String)list.get(e)+"/"+tsortid;
					String value=(String)dataBean.get(name);
					if(countMap.get((String)list.get(e)+"/"+tsortid)!=null)
					{
						String avalue=(String)countMap.get((String)list.get(e)+"/"+tsortid);
						String dvalue=!"".equals(value.trim())?value:"0";
						countMap.put((String)list.get(e)+"/"+tsortid,String.valueOf(Integer.parseInt(avalue)+Integer.parseInt(dvalue)));
					}
					else
					{
						countMap.put((String)list.get(e)+"/"+tsortid,!"".equals(value.trim())?value:"0");
					}
					bodyHtml.append("<td align='center' class='RecordRow2' nowrap>");
					bodyHtml.append("<a href='/report/report_collect/reportOrgCollecttree.do?b_lookInfo=look&encryptParam=" + PubFunc.encrypt("unitcode="+unitcode+"&reportSet="+tsortid+"&status="+(String)list.get(e))+"'>");
					bodyHtml.append(value);
					bodyHtml.append("</a></td>");
				}
			}
			bodyHtml.append("</tr>");
		}
		bodyHtml.append("<tr class='"+("trShallow".equals(a_className)?"trDeep":"trShallow")+"'>");
		bodyHtml.append("<td align='left' class='RecordRow' bgColor='#FFFDC5' nowrap>&nbsp;");  // t_cell_locked_b
		bodyHtml.append("</td>");
		for(int j=0;j<reportSetList.size();j++)
		{
			RecordVo vo=(RecordVo)reportSetList.get(j);
			String  tsortid=vo.getString("tsortid");
			for(int e=0;e<list.size();e++)
			{
				String name=(String)list.get(e)+"/"+tsortid;
				bodyHtml.append("<td align='center' class='RecordRow' bgColor='#FFFDC5' nowrap>");
				String value=(String)countMap.get(name);
				bodyHtml.append("0".equals(value)?"":value);
				bodyHtml.append("</td>");
			}
		}
		bodyHtml.append("</tr>");
		return bodyHtml.toString();
	}
	
	
	
	
	
	
	/**
	 * 取得汇总单位上报数据
	 * @param subUnitList
	 * @param reportSetList
	 * @return
	 */
	public ArrayList getUnitAppealData(ArrayList subUnitList,ArrayList reportSetList)
	{
		ArrayList list=new ArrayList();
		try
		{
			//单位负责的表没走权限计算的个数不对
			//解决办法：按照每个单位负责表的
			HashMap cotrolmap = getUnitTable();
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select count(treport_ctrl.tabid) a_count,treport_ctrl.unitcode,treport_ctrl.status, tname.tsortid from treport_ctrl,tname");
			sql.append(" where treport_ctrl.tabid=tname.tabid  ");
			TnameBo tnamebo  = new TnameBo(this.conn);
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0) {
                tabids=tabids.substring(0,tabids.length()-1);
            }
			if(tabids.length()>0) {
                sql.append(" and  tname.tabid not in("+tabids+") ");
            }
			sql.append(" group by treport_ctrl.status, treport_ctrl.unitcode, tname.tsortid  ");
			sql.append(" order by unitcode,tsortid,status ");
			RowSet rowSet=dao.search(sql.toString());
			HashMap map=new HashMap();
			while(rowSet.next())
			{
				String a_count=rowSet.getString("a_count");
				String unitcode=rowSet.getString("unitcode");
				String status=rowSet.getString("status");
				String tsortid=rowSet.getString("tsortid");
				map.put(unitcode+"/"+status+"/"+tsortid,a_count);
			}
			
			DynaBean bean=null;
			RecordVo vo=null;
			LazyDynaBean abean=null;
			for(int i=0;i<subUnitList.size();i++)
			{
				abean=new LazyDynaBean();
				bean =(LazyDynaBean)subUnitList.get(i);
				String unitcode=(String)bean.get("unitcode");
				
				int acount=0;
				int bcount=0;
				int ccount=0;
				int dcount=0;
				int ecount=0;
				for(int j=0;j<reportSetList.size()-1;j++)
				{
					vo=(RecordVo)reportSetList.get(j);
					String tsortid=String.valueOf(vo.getInt("tsortid"));
					for(int e=-1;e<4;e++)
					{
						String name=unitcode+"/"+e+"/"+tsortid;
						if(cotrolmap.get(name)!=null)
						{
							abean.set(name,(String)cotrolmap.get(name));
							if(e==-1) {
                                acount+=Integer.parseInt((String)cotrolmap.get(name));
                            } else if(e==0) {
                                bcount+=Integer.parseInt((String)cotrolmap.get(name));
                            } else if(e==1) {
                                ccount+=Integer.parseInt((String)cotrolmap.get(name));
                            } else if(e==2) {
                                dcount+=Integer.parseInt((String)cotrolmap.get(name));
                            } else if(e==3) {
                                ecount+=Integer.parseInt((String)cotrolmap.get(name));
                            }
						}
						else
						{
							abean.set(name,"");
						}
					}
				}
				
				//汇总
				for(int e=-1;e<4;e++)
				{
					String name=unitcode+"/"+e+"/-1";
					if(e==-1) {
                        abean.set(name,acount!=0?String.valueOf(acount):"");
                    } else if(e==0) {
                        abean.set(name,bcount!=0?String.valueOf(bcount):"");
                    } else if(e==1) {
                        abean.set(name,ccount!=0?String.valueOf(ccount):"");
                    } else if(e==2) {
                        abean.set(name,dcount!=0?String.valueOf(dcount):"");
                    } else if(e==3) {
                        abean.set(name,ecount!=0?String.valueOf(ecount):"");
                    }
				}
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
//	取得填报单位信息
	public RecordVo getSelfUnit2(String unitcode)
	{
		
		RowSet recset=null;
		RecordVo vo=new RecordVo("tt_organization");
		try
		{

			ContentDAO dao=new ContentDAO(this.conn);
			recset=dao.search("select * from tt_organization  where unitcode='"+unitcode+"'  order by a0000");
			if(recset.next())
			{
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("analysereports",Sql_switcher.readMemo(recset,"analysereports"));
				vo.setString("b0110",recset.getString("b0110"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return vo;
	}
	
	
	
	
	//取得本人的填报单位信息
	/*
	 * 
	 */
	public RecordVo getSelfUnit(String userName)
	{
				RowSet recset=null;
		
		RecordVo vo=null;
		try
		{
	
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer ext_sql = new StringBuffer();
			if("1".equals(this.validedateflag)){
				Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				if(this.backdate!=null&&this.backdate.length()!=0){
					d.setTime(Date.valueOf(backdate));
					yy=d.get(Calendar.YEAR);
					mm=d.get(Calendar.MONTH)+1;
					dd=d.get(Calendar.DATE);
				}
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
				
			}
			recset=dao.search("select t.* from tt_organization t,operuser o where o.unitcode=t.unitcode  and o.username='"+userName+"' "+ext_sql+"");
			if(recset.next())
			{
				vo=new RecordVo("tt_organization");
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("b0110",recset.getString("b0110"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return vo;
	}
	
//	取得本人的填报单位信息
	public RecordVo getSelfUnit3(String userName)
	{
		RowSet recset=null;
		Connection selfconn=null;
		RecordVo vo=new RecordVo("tt_organization");
		try
		{
			selfconn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(selfconn);
			StringBuffer ext_sql = new StringBuffer();
			if("1".equals(this.validedateflag)){
				Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				if(this.backdate!=null&&this.backdate.length()!=0){
					d.setTime(Date.valueOf(backdate));
					yy=d.get(Calendar.YEAR);
					mm=d.get(Calendar.MONTH)+1;
					dd=d.get(Calendar.DATE);
				}
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
				
			}
			recset=dao.search("select t.* from tt_organization t,operuser o where o.unitcode=t.unitcode  and o.username='"+userName+"' "+ext_sql+"");
			if(recset.next())
			{
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("b0110",recset.getString("b0110"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
				if(!selfconn.isClosed()) {
                    selfconn.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			
		}
		
		return vo;
	}
	
	
	
	
	
	

	//得到直属单位列表
	public ArrayList getUnderUnitList(String unitcode)
	{
		
		ArrayList unitList=new ArrayList();	
		RowSet recset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			recset=dao.search("select * from tt_organization   where parentid='"+unitcode+"' and parentid<>unitcode "+ext_sql+" order by a0000");
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				bean.set("unitcode",recset.getString("unitcode"));
				bean.set("unitid",recset.getString("unitid"));
				bean.set("unitname",recset.getString("unitname"));
				bean.set("parentid",recset.getString("parentid"));
				bean.set("grade",recset.getString("grade"));
				bean.set("flag",recset.getString("flag"));
				bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				bean.set("report",Sql_switcher.readMemo(recset,"report"));
				bean.set("b0110",recset.getString("b0110"));
				unitList.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return unitList;
	}
	
	
//	得到直属单位列表
	/**
	 * opt : 0:不包括本部  1：包括本部
	 */
	public ArrayList getUnderUnitList(String unitcode,int opt)
	{
		
		ArrayList unitList=new ArrayList();	
		RowSet recset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from tt_organization   where parentid='"+unitcode+"' ";
			if(opt==0) {
                sql+=" and parentid<>unitcode";
            }
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			sql+=ext_sql.toString();
			recset=dao.search(sql+" order by a0000");
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				bean.set("unitcode",recset.getString("unitcode"));
				bean.set("unitid",recset.getString("unitid"));
				String unitname=recset.getString("unitname");
				bean.set("unitname",unitname);
				bean.set("parentid",recset.getString("parentid"));
				bean.set("grade",recset.getString("grade"));
				bean.set("flag",recset.getString("flag"));
				bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				bean.set("b0110",recset.getString("b0110"));
				unitList.add(bean);
			}
			
			if(unitList.size()==0)
			{
				sql="select * from tt_organization   where unitcode='"+unitcode+"'  order by a0000 ";
				recset=dao.search(sql);
				if(recset.next())
				{
					DynaBean bean = new LazyDynaBean();
					bean.set("unitcode",recset.getString("unitcode"));
					bean.set("unitid",recset.getString("unitid"));
					String unitname=recset.getString("unitname");
					bean.set("unitname",unitname);
					bean.set("parentid",recset.getString("parentid"));
					bean.set("grade",recset.getString("grade"));
					bean.set("flag",recset.getString("flag"));
					bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
					bean.set("b0110",recset.getString("b0110"));
					unitList.add(bean);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return unitList;
	}
	
	
	
	
//	得到具有某表类权限的直属单位列表
	public ArrayList getUnderUnitList(String unitcode,String tsortid)
	{
		
		ArrayList unitList=new ArrayList();	
		RowSet recset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			recset=dao.search("select * from tt_organization   where parentid='"+unitcode+"' and reporttypes like '%"+tsortid+"%' and parentid<>unitcode "+ext_sql+" order by a0000");
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				bean.set("unitcode",recset.getString("unitcode"));
				bean.set("unitid",recset.getString("unitid"));
				bean.set("unitname",recset.getString("unitname"));
				bean.set("parentid",recset.getString("parentid"));
				bean.set("grade",recset.getString("grade"));
				bean.set("flag",recset.getString("flag"));
				bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				bean.set("b0110",recset.getString("b0110"));
				unitList.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return unitList;
	}
	
	
	//取得所有填报单位的信息
	public ArrayList getAllUnitInfo(String sortid)
	{ 
		ArrayList list=new ArrayList();
		RowSet recset=null;
		try
		{
			//selfconn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                ext_sql.append(" and to_char(reporttypes) like '%"+sortid+",%' ");
            } else {
                ext_sql.append(" and reporttypes like '%"+sortid+",%' ");
            }
			
			String sql = " where 1=1 "+ext_sql;
			recset=dao.search("select * from tt_organization "+sql+" order by a0000");
			LazyDynaBean bean=null;
			while(recset.next())
			{ 
				bean=new LazyDynaBean();
				bean.set("unitcode",recset.getString("unitcode"));
				bean.set("unitid",recset.getString("unitid"));
				bean.set("unitname",recset.getString("unitname"));
				bean.set("parentid",recset.getString("parentid"));
				bean.set("grade",recset.getString("grade"));
				bean.set("flag",recset.getString("flag"));
				bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				bean.set("report",Sql_switcher.readMemo(recset,"report"));
				bean.set("b0110",recset.getString("b0110"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	//取得所有填报单位的信息
	public ArrayList getAllUnitInfo1(String sortid){
		ArrayList list=new ArrayList();
		RowSet recset=null;
		try
		{
			//selfconn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                ext_sql.append(" and to_char(reporttypes) like '%"+sortid+",%' ");
            } else {
                ext_sql.append(" and reporttypes like '%"+sortid+",%' ");
            }
			
			recset=dao.search("select * from tt_organization  where reporttypes is not null "+ext_sql+"  order by a0000");
			while(recset.next())
			{
				RecordVo vo=new RecordVo("tt_organization");
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("b0110",recset.getString("b0110"));
				list.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	public ArrayList getAllUnitInfo()
	{
		ArrayList list=new ArrayList();
		RowSet recset=null;
		try
		{
			//selfconn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			String sql = " where 1=1 "+ext_sql;
			recset=dao.search("select * from tt_organization "+sql+" order by a0000");
			while(recset.next())
			{
				RecordVo vo=new RecordVo("tt_organization");
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("b0110",recset.getString("b0110"));
				list.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//取得所有填报单位的信息
	public ArrayList getAllUnitInfo2()
	{
		ArrayList list=new ArrayList();
		RowSet recset=null;
		try
		{
			//selfconn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			recset=dao.search("select * from tt_organization  where reporttypes is not null "+ext_sql+"  order by a0000");
			while(recset.next())
			{
				RecordVo vo=new RecordVo("tt_organization");
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("b0110",recset.getString("b0110"));
				list.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	//取得所有填报单位的信息
	public ArrayList getAllUnitInfo(String unitcode,String opt,String sortid)
	{
		ArrayList list=new ArrayList();
		RowSet recset=null;
		try
		{
			//selfconn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                ext_sql.append(" and to_char(reporttypes) like '%"+sortid+",%' ");
            } else {
                ext_sql.append(" and reporttypes like '%"+sortid+",%' ");
            }
			
			//所有填报单位  1:所有单位  2：直属单位  3：基层单位
			if("1".equals(opt))
			{
				list=getAllSubUnit(unitcode,sortid);
			}
			else if("3".equals(opt))
			{
				list=getGrassRootsUnit(unitcode,sortid);
			}
			else
			{
				recset=dao.search("select * from tt_organization  where unitcode like '"+unitcode+"%'  "+ext_sql+"  and parentid='"+unitcode+"' and parentid<>unitcode   order by a0000");
			//	else if(opt.equals("3"))
			//		recset=dao.search("select * from tt_organization  where unitcode like '"+unitcode+"%'  "+ext_sql+"  and unitcode not in (select parentid from tt_organization)   order by a0000");
				LazyDynaBean bean=null;
				while(recset.next())
				{
					 
					bean=new LazyDynaBean();
					bean.set("unitcode",recset.getString("unitcode"));
					bean.set("unitid",recset.getString("unitid"));
					bean.set("unitname",recset.getString("unitname"));
					bean.set("parentid",recset.getString("parentid"));
					bean.set("grade",recset.getString("grade"));
					bean.set("flag",recset.getString("flag"));
					bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
					bean.set("report",Sql_switcher.readMemo(recset,"report"));
					bean.set("b0110",recset.getString("b0110"));
					list.add(bean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	/**
	 * 递归查找子单位的信息，用于逐层汇总
	 * @param allUnitInfoList	所有填报单位信息
	 * @param layerList			层列表
	 * @param unitcode			各层的所有单位
	 * @param subUnitMap		填报单位 对应的子单位列表
	 */
	
	public void findSubUnit_layer(ArrayList allUnitInfoList,ArrayList layerList,ArrayList unitcodeList,HashMap subUnitMap)
	{
		ArrayList  a_layList=new ArrayList();
		int i=0;
		for(Iterator t=unitcodeList.iterator();t.hasNext();)
		{
			RecordVo vo=(RecordVo)t.next();
			ArrayList subList=findSubUnit2(allUnitInfoList,vo.getString("unitcode"),subUnitMap);
			for(Iterator t2=subList.iterator();t2.hasNext();)
			{
				RecordVo subVo=(RecordVo)t2.next();
				a_layList.add(subVo);
				i++;
			}
		}
		if(i!=0)
		{
			layerList.add(a_layList);
			findSubUnit_layer(allUnitInfoList,layerList,a_layList,subUnitMap);
		}
		else {
            layerList.add(unitcodeList);
        }
	}
	
	public ArrayList findSubUnit2(ArrayList allUnitInfoList,String unitcode,HashMap subUnitMap)
	{
		ArrayList  a_layList=new ArrayList();
		ArrayList  subUnitList=new ArrayList();
		for(Iterator t=allUnitInfoList.iterator();t.hasNext();)
		{
			RecordVo vo=(RecordVo)t.next();
			if(vo.getString("parentid").equals(unitcode)&&!vo.getString("parentid").equals(vo.getString("unitcode")))
			{
				a_layList.add(vo);
				subUnitList.add(vo.getString("unitcode")+"§"+vo.getString("unitname"));
			}
		}
		subUnitMap.put(unitcode,subUnitList);
		
		return a_layList;
		
	}
	
	
	
	/**
	 * 得到某单位下的所有子单位
	 * @param unitcode
	 * @return
	 */
	public ArrayList getAllSubUnit(String unitcode)
	{
		ArrayList subUnitList=new ArrayList();
		ArrayList allUnitList=getAllUnitInfo();
		getSubUnit(allUnitList,unitcode,subUnitList);
		
		return subUnitList;
	}
	public ArrayList getAllSubUnit1(String unitcode,String tsortid)
	{
		ArrayList subUnitList=new ArrayList();
		ArrayList allUnitList=getAllUnitInfo1(tsortid);
		getSubUnit(allUnitList,unitcode,subUnitList);
		
		return subUnitList;
	}
	
	public void getSubUnit(ArrayList allUnitInfoList,String unitcode,ArrayList unitList)
	{
		ArrayList subUnitList=new ArrayList();
		
		
		for(Iterator t=allUnitInfoList.iterator();t.hasNext();)
		{
			 RecordVo temp=(RecordVo)t.next();
			 if(temp.getString("parentid").equals(unitcode)&&!temp.getString("parentid").equals(temp.getString("unitcode")))
			 {
				 unitList.add(temp.getString("unitcode")+"§"+temp.getString("unitname"));
				 subUnitList.add(temp);
			 }
		}
	
		if(subUnitList.size()>0)
		{
			for(Iterator t=subUnitList.iterator();t.hasNext();)
			{
				RecordVo temp=(RecordVo)t.next();
				getSubUnit(allUnitInfoList,temp.getString("unitcode"),unitList);
			}
		}
		
	}
	
	
	
	/**
	 * 得到某单位下的所有子单位
	 * @param unitcode
	 * @return
	 */
	public ArrayList getAllSubUnit(String unitcode,String sortid)
	{
		ArrayList subUnitList=new ArrayList();
		ArrayList allUnitList=getAllUnitInfo(sortid);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			RowSet recset=dao.search("select * from tt_organization where unitcode='"+unitcode+"' "+ext_sql+"  order by a0000");
			LazyDynaBean bean=null;
			if(recset.next())
			{ 
				bean=new LazyDynaBean();
				bean.set("unitcode",recset.getString("unitcode"));
				bean.set("unitid",recset.getString("unitid"));
				bean.set("unitname",recset.getString("unitname"));
				bean.set("parentid",recset.getString("parentid"));
				bean.set("grade",recset.getString("grade"));
				bean.set("flag",recset.getString("flag"));
				bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				bean.set("report",Sql_switcher.readMemo(recset,"report"));
				bean.set("b0110",recset.getString("b0110"));
				 
			}
			if(bean!=null)
			{
				subUnitList.add(bean);
				getSubUnitbean(allUnitList,bean,subUnitList);
			}
			if(recset!=null) {
                recset.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return subUnitList;
	}

	
	public void getSubUnitbean(ArrayList allUnitInfoList,LazyDynaBean _bean,ArrayList unitList)
	{
		ArrayList subUnitList=new ArrayList();
		
		LazyDynaBean bean=null;
		for(Iterator t=allUnitInfoList.iterator();t.hasNext();)
		{
			 bean=(LazyDynaBean)t.next();
			 if(((String)bean.get("parentid")).equals(((String)_bean.get("unitcode")))&&!((String)bean.get("parentid")).equals(((String)bean.get("unitcode"))))
			 {
				 subUnitList.add(bean);
			 }
		}
		
		if(subUnitList.size()>0)
		{
			for(Iterator t=subUnitList.iterator();t.hasNext();)
			{
				 bean=(LazyDynaBean)t.next();
				 unitList.add(bean);
				 getSubUnitbean(allUnitInfoList,bean,unitList);
			}
		}
		
	}
	
	
	
	
	/**
	 * 取得某单位的所有基层单位
	 * @param unitcode			填报单位编码
	 * @return
	 */
	public ArrayList getGrassRootsUnit(String unitcode,String sortid)
	{
		ArrayList list=new ArrayList();
		
		RowSet recset=null;
		try
		{
		
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList allUnitlist=getAllUnitInfo(sortid);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			recset=dao.search("select * from tt_organization where unitcode='"+unitcode+"' "+ext_sql+"  order by a0000");
			LazyDynaBean bean=null;
			if(recset.next())
			{ 
				bean=new LazyDynaBean();
				bean.set("unitcode",recset.getString("unitcode"));
				bean.set("unitid",recset.getString("unitid"));
				bean.set("unitname",recset.getString("unitname"));
				bean.set("parentid",recset.getString("parentid"));
				bean.set("grade",recset.getString("grade"));
				bean.set("flag",recset.getString("flag"));
				bean.set("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				bean.set("report",Sql_switcher.readMemo(recset,"report"));
				bean.set("b0110",recset.getString("b0110"));
				 
			}
			getLeafUnit(bean,allUnitlist,list);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	/**
	 * 取得某单位的所有基层单位
	 * @param unitcode			填报单位编码
	 * @return
	 */
	public ArrayList getGrassRootsUnit(String unitcode)
	{
		ArrayList list=new ArrayList();
		
		RowSet recset=null;
		try
		{
		
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList allUnitlist=getAllUnitInfo();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			recset=dao.search("select * from tt_organization where unitcode='"+unitcode+"' "+ext_sql+"  order by a0000");
			RecordVo vo=new RecordVo("tt_organization");
			if(recset.next())
			{
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("b0110",recset.getString("b0110"));
				//list.add(vo);
			}
			getLeafUnit(vo,allUnitlist,list);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * 取得某单位的所有单位包括汇总单位
	 * @param unitcode			填报单位编码
	 * @return
	 */
	public ArrayList getGrassRootsUnit2(String unitcode)
	{
		ArrayList list=new ArrayList();
		
		RowSet recset=null;
		try
		{
		
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList allUnitlist=getAllUnitInfo2();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			recset=dao.search("select * from tt_organization where unitcode='"+unitcode+"'  and reporttypes is not null "+ext_sql+" order by a0000");
			RecordVo vo=new RecordVo("tt_organization");
			if(recset.next())
			{
				vo.setString("unitcode",recset.getString("unitcode"));
				vo.setInt("unitid",recset.getInt("unitid"));
				vo.setString("unitname",recset.getString("unitname"));
				vo.setString("parentid",recset.getString("parentid"));
				vo.setInt("grade",recset.getInt("grade"));
				vo.setInt("flag",recset.getInt("flag"));
				vo.setString("reporttypes",Sql_switcher.readMemo(recset,"reporttypes"));
				vo.setString("report",Sql_switcher.readMemo(recset,"report"));
				vo.setString("b0110",recset.getString("b0110"));
//				list.add(vo);
			}
			getLeafUnit2(vo,allUnitlist,list);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	//递归调用
	public void getLeafUnit2(RecordVo vo,ArrayList list,ArrayList leafList) 
	 { 

		  ArrayList subNodeList=new ArrayList();
		 
		  for (int i=0; i<list.size(); i++) 
		  { 
			  RecordVo atemp=(RecordVo)list.get(i);
			  if(atemp.getString("parentid").equals(vo.getString("unitcode"))&&!atemp.getString("unitcode").equals(atemp.getString("parentid"))) {
                  subNodeList.add(atemp);
              }
		  }
		  if(subNodeList.size()==0)
		  {
			  leafList.add(vo);  
		  }
		  else
		  {
			  leafList.add(vo);  
			  
			  for (int i=0; i<subNodeList.size(); i++) 
			  { 
				  RecordVo temp2=(RecordVo)subNodeList.get(i);
				  getLeafUnit2(temp2,list,leafList);
			  } 
		  }
	 } 
	
	//递归调用
	public void getLeafUnit(RecordVo vo,ArrayList list,ArrayList leafList) 
	 { 

		  ArrayList subNodeList=new ArrayList();
		 
		  for (int i=0; i<list.size(); i++) 
		  { 
			  RecordVo atemp=(RecordVo)list.get(i);
			  if(atemp.getString("parentid").equals(vo.getString("unitcode"))&&!atemp.getString("unitcode").equals(atemp.getString("parentid"))) {
                  subNodeList.add(atemp);
              }
		  }
		  if(subNodeList.size()==0)
		  {
			  leafList.add(vo);  
		  }
		  else
		  {
			  
			  for (int i=0; i<subNodeList.size(); i++) 
			  { 
				  RecordVo temp2=(RecordVo)subNodeList.get(i);
				  getLeafUnit(temp2,list,leafList);
			  } 
		  }
	 } 
	
	
	//递归调用
	public void getLeafUnit(LazyDynaBean abean,ArrayList list,ArrayList leafList) 
	 { 

		  ArrayList subNodeList=new ArrayList();
		  LazyDynaBean _abean=null;
		  for (int i=0; i<list.size(); i++) 
		  { 
			  _abean=(LazyDynaBean)list.get(i);
			  if(((String)_abean.get("parentid")).equals(((String)abean.get("unitcode")))&&!((String)_abean.get("unitcode")).equals(((String)_abean.get("parentid")))) {
                  subNodeList.add(_abean);
              }
		  }
		  if(subNodeList.size()==0)
		  {
			  leafList.add(abean);  
		  }
		  else
		  {
			  
			  for (int i=0; i<subNodeList.size(); i++) 
			  { 
				  _abean=(LazyDynaBean)subNodeList.get(i);
				  getLeafUnit(_abean,list,leafList);
			  } 
		  }
	 } 
	
	
	
	/**
	 * 获得所有汇总单位
	 * @param unitcode
	 * @param tabidList 报表id
	 * @return
	 */
	public ArrayList getAllCollect(ArrayList LeafUnitList)
	{
		RowSet recset=null;
		try
		{

			
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList allUnitlist=getAllUnitInfo2();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			recset=dao.search("select * from tt_organization where   unitcode  in (select parentid from tt_organization) "+ext_sql+" order by  grade  desc ");
			while(recset.next())
			{
				LeafUnitList.add(recset.getString("unitcode"));
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(recset!=null) {
                try {
                    recset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return LeafUnitList;
	}
	/**
	 * 获得单位的直属单位
	 * @param unitcode
	 * @param tabidList 报表id
	 * @return
	 */
	public ArrayList getUnderUnit(String unitcode)
	{
		boolean issuccess=true;
		ArrayList layerUnitList=new ArrayList();
		RowSet recset=null;
		try
		{

			
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			recset=dao.search("select * from tt_organization where   parentid='"+unitcode+"' and unitcode<>parentid "+ext_sql+" ");
			while(recset.next())
			{
				layerUnitList.add(recset.getString("unitcode"));
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
		finally{
			if(recset!=null) {
                try {
                    recset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return layerUnitList;
	}
	/**
	 * 获得单位负责的表
	 * @param unitcode
	 * @param tabidList 报表id
	 * @return
	 * 
	 * xiegh  update  该方法重构 之前运行速度太慢 20170819
	 */
	public HashMap getUnitTable(){
		
		RowSet recset=null;
		RowSet recset2=null;
		HashMap map = new HashMap();
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			String unitSql ="select unitcode from tt_organization where 1=1 "+ext_sql+" ";
			String sqlAll = "select reporttypes,report,unitcode from tt_organization where 1=1 "+ext_sql+" ";
					
			recset=dao.search(sqlAll);//查询所有单位下的报表分类信息
			Map<String,LazyDynaBean> unitMap = new HashMap<String,LazyDynaBean>();
			LazyDynaBean bean = null;
			while(recset.next()){
				bean = new LazyDynaBean();
				String reporttypes=Sql_switcher.readMemo(recset, "reporttypes");
				String report=Sql_switcher.readMemo(recset, "report");
				String unitcode = recset.getString("unitcode");
				bean.set("reporttypes", reporttypes);
				bean.set("report", report);
				unitMap.put(unitcode, bean);
			}
			
			//查询所有单位下的所有报表详细信息	
			recset2=dao.search("select tname.tsortid,treport_ctrl.unitcode,tname.tabid,treport_ctrl.status from tname,treport_ctrl where tname.tabid= treport_ctrl.tabid  and treport_ctrl.unitcode in ("+unitSql+")");
			List<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
			LazyDynaBean statusBean =null;
			while(recset2.next()){
				statusBean = new LazyDynaBean();
				String tabid = recset2.getString("tabid");
				int status = recset2.getInt("status");
				String unitcode = recset2.getString("unitcode");
				String tsortid = recset2.getString("tsortid");
				statusBean.set("tabid", tabid);
				statusBean.set("status", status);
				statusBean.set("unitcode", unitcode);
				statusBean.set("tsortid", tsortid);
				list.add(statusBean);
			}
			
			for (Entry<String, LazyDynaBean> mp : unitMap.entrySet()) {
				String unit = (String) mp.getKey();
				LazyDynaBean bn = (LazyDynaBean) mp.getValue();
				String reporttypes = (String) bn.get("reporttypes");
				String[] reporttypeArray = reporttypes.split(",");
				String report = (String) bn.get("report");
				for (String reportType : reporttypeArray) {
					if(reportType.length()>0){
						int num1 = 0;
						int num2 = 0;
						int num3 = 0;
						int num4 = 0;
						int num5 = 0;
						for (LazyDynaBean beanObj : list) {
							String unitcode = (String) beanObj.get("unitcode");
							String tabid = (String) beanObj.get("tabid");
							int status = (Integer) beanObj.get("status");
							String tsortid = (String) beanObj.get("tsortid");
							if (tsortid.equals(reportType)&&unit.equals(unitcode)) {
								if(report==null||report.indexOf(","+tabid+",")==-1){
									if(status==-1) {
                                        num1++;
                                    }
									if(status==0) {
                                        num2++;
                                    }
									if(status==1){
										num3++;
									}if(status==2) {
                                        num4++;
                                    }
									if(status==3) {
                                        num5++;
                                    }
								}
							}
						}
						if (num1 > 0) {
                            map.put(unit + "/-1/" + reportType.trim(), "" + num1);
                        }
						if (num2 > 0) {
                            map.put(unit + "/0/" + reportType.trim(), "" + num2);
                        }
						if (num3 > 0) {
                            map.put(unit + "/1/" + reportType.trim(), "" + num3);
                        }
						if (num4 > 0) {
                            map.put(unit + "/2/" + reportType.trim(), "" + num4);
                        }
						if (num5 > 0) {
                            map.put(unit + "/3/" + reportType.trim(), "" + num5);
                        }
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(recset);
			PubFunc.closeDbObj(recset2);
		}
		return map;
	}
	//表与表类对应
	public HashMap getReportTsort() throws GeneralException{
		RowSet recset=null;
		HashMap reportMap = new HashMap();
		StringBuffer strsql = new StringBuffer();
		strsql.delete(0,strsql.length());	
		try{
			ContentDAO dao = new ContentDAO(this.conn);		
			//SQL
			strsql.append("select * from  tname ");
			//执行SQL
			recset=dao.search(strsql.toString());
			while(recset.next()){
				reportMap.put(recset.getString("tabid"), recset.getString("tsortid"));
			}	
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			if(recset!=null) {
                try {
                    recset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return reportMap;
	}
	public String getValidedateflag() {
		return validedateflag;
	}

	public void setValidedateflag(String validedateflag) {
		this.validedateflag = validedateflag;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}
}
