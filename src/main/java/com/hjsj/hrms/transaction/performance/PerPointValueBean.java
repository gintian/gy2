/*
 * 创建日期 2005-6-24
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author luangaojiong 自评分数相关处理类
 */
public class PerPointValueBean {

	ArrayList perPointlst = new ArrayList();
	ArrayList perPointGradelist = new ArrayList();
	Connection conn=null;
	/**初始化数据*/
	public PerPointValueBean(Connection conn)
	{
		this.conn=conn;
		getPerGradeAllObject();		//得到标度表对象
		getPerPointAllObject();
	}
	/**
	 * 得到考核指标要素表集合及其标度集合
	 *
	 */
	void getPerPointAllObject()
	{
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;
		StringBuffer sb=new StringBuffer();
		sb.append("select per_point.point_id ,");
		sb.append("per_point.seq,");
		sb.append("per_point.pointsetid ,");
		sb.append("per_point.pointname ,");
		sb.append("per_point.pointkind ,");
		sb.append("per_point.formula ,");
		sb.append("per_point.validflag from per_point");
		perPointlst = new ArrayList();
		try 
		{
			rs = dao.search(sb.toString());
			while (rs.next()) 
			{
				PerPointBean ppb=new PerPointBean();
				String pointNum=rs.getString("point_id");
				ppb.setPoint_id(pointNum);
				ppb.setPointkind(rs.getInt("pointkind"));
				ppb.setPerGradelist(getPerGradelstOfPoint(pointNum));
				perPointlst.add(ppb);
			}
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		} 
		finally 
		{
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 得到对应要素的标度集合
	 * @param pointid
	 * @return
	 */
	ArrayList getPerGradelstOfPoint(String pointid)
	{
		ArrayList lst=new ArrayList();
		for(int i=0;i<perPointGradelist.size();i++)
		{
			PerGradeBean pgb=(PerGradeBean)perPointGradelist.get(i);
			if(pgb.getPoint_id().equals(pointid))
			{
				lst.add(pgb);
			}
		}
		return lst;
	}

	/**
	 * 得到标度表中所有集合
	 *  
	 */
	void getPerGradeAllObject() {
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;
		String sqlTemp = "select * from per_grade order by point_id,grade_id";
		perPointGradelist = new ArrayList();
		try 
		{
			rs = dao.search(sqlTemp);
			while (rs.next())
			{
				PerGradeBean pgb=new PerGradeBean();
				pgb.setGrade_id(rs.getInt("grade_id"));
				pgb.setPoint_id(rs.getString("point_id"));
				pgb.setGradevalue(rs.getDouble("gradevalue"));
				pgb.setGradedesc(PubFunc.nullToStr(rs.getString("gradedesc")));
				pgb.setGradecode(PubFunc.nullToStr(rs.getString("gradecode")));
				pgb.setTop_value(rs.getDouble("top_value"));
				pgb.setBottom_value(rs.getDouble("bottom_value"));
				perPointGradelist.add(pgb);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		finally 
		{
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 返回考核指标要素表集合
	 * @return
	 */
	public ArrayList getPerPointValue() {
		
		return perPointlst;

	}
	public ArrayList getPerPointGradelist() {
		return perPointGradelist;
	}

}