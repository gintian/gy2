/*
 * 创建日期 2005-7-7
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
/**
 * @author luangaojiong
 * 
 * 图形统计帮助类
 */
public class DrawingHelp {

	Hashtable maxHt = new Hashtable();
	Hashtable argHt=new Hashtable();
	Hashtable allHt=new Hashtable();
	/**
	 * 最大要素的分值
	 * @param con
	 * @param columnlst
	 * @param tbsql
	 * @return
	 */
	public Hashtable getMaxFunction(Connection con, ArrayList columnlst,
			String tbsql) {
		String sqlcol = "";
		String colName = "";
		String colSecName = "";
		ArrayList changeColumn = new ArrayList();
		ContentDAO dao = new ContentDAO(con);
		for (int i = 0; i < columnlst.size(); i++) {
			colName = columnlst.get(i).toString();
			colSecName = colName.substring(2, colName.length());
			changeColumn.add(colSecName);
			if (i == columnlst.size() - 1) {
				sqlcol = sqlcol + " max(" + colName + ") as  a" + colSecName
						+ " ";
			} else {
				sqlcol = sqlcol + "max(" + colName + ") as  a" + colSecName
						+ ", ";
			}

		}
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("select ");
		sbsql.append(sqlcol);
		sbsql.append(" from ");
		sbsql.append(tbsql);
		/*System.out.println("------------>com.hjsj.hrms.transaction.statistic.DrawingHelp-->sql--"
						+ sbsql.toString());*/
	   /**
		 * 得到最大分数
		 */
		try (
			ResultSet rs = dao.search(sbsql.toString());
		){
			while (rs.next()) {
				for(int i=0;i<changeColumn.size();i++)
				{
					if(!maxHt.containsKey(changeColumn.get(i).toString()))
					{
						maxHt.put(changeColumn.get(i).toString(),rs.getString("a"+changeColumn.get(i).toString()));
						//System.out.println("--com.hjsj.hrms.transaction.statistic.DrawingHelp-->"+changeColumn.get(i).toString());
					}
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return maxHt;
	}
	
	/**
	 * 得到平均数Hashtable
	 * @author luangaojiong
	 *
	 */
	public Hashtable getArgHashtable(Connection con, ArrayList columnlst,
	String tbsql,String count)
	{		
		String sqlcol = "";
		String colName = "";
		String colSecName = "";
		ArrayList changeColumn = new ArrayList();
		ContentDAO dao = new ContentDAO(con);
		
		for (int i = 0; i < columnlst.size(); i++) {
			colName = columnlst.get(i).toString();
			colSecName = colName.substring(2, colName.length());
			changeColumn.add(colSecName);
			if (i == columnlst.size() - 1) {
				sqlcol = sqlcol + " sum(" + colName + ")/"+count+" as  a" + colSecName
						+ " ";
			} else {
				sqlcol = sqlcol + "sum(" + colName + ")/"+count+"  as  a" + colSecName
						+ ", ";
			}

		}
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("select ");
		sbsql.append(sqlcol);
		sbsql.append(" from ");
		sbsql.append(tbsql);
		//System.out.println("------------>com.hjsj.hrms.transaction.statistic.DrawingHelp-->sql--"
					//	+ sbsql.toString());
		/**
		 * 得到平均Hashtable
		 */
		ResultSet rs=null;
		try {
			rs = dao.search(sbsql.toString());
			while (rs.next()) {
				for(int i=0;i<changeColumn.size();i++)
				{
					if(!argHt.containsKey(changeColumn.get(i).toString()))
					{
						argHt.put(changeColumn.get(i).toString(),rs.getString("a"+changeColumn.get(i).toString()));
						//System.out.println("--com.hjsj.hrms.transaction.statistic.DrawingHelp-->getargHt");
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
		}

		return argHt;
	}
	/**
	 * 所有要素的id与名称
	 * @author luangaojiong
	 *
	 * 
	 */
	public Hashtable getAllPoint(Connection con)
	{
		ContentDAO dao = new ContentDAO(con);
		try(
			ResultSet rs = dao.search("select * from per_point");
		)
		{

			String pointId="0";
			while(rs.next())
			{
				pointId=rs.getString("point_id");
				if(allHt!=null&&!allHt.containsKey(pointId))
				{
					allHt.put(pointId,Sql_switcher.readMemo(rs,"pointname"));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return allHt;
	}
}