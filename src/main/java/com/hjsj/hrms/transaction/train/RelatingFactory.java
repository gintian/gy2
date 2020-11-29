/*
 * 创建日期 2005-9-8
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author luangaojiong
 *
 * 得到关联编码表所有的对象
 */
public class RelatingFactory  {
	transient Category cat;
	public  ArrayList list=new ArrayList();
	//private Connection conn=null;
	public RelatingFactory()
	{
		getInstance();
	}
	public  ArrayList getInstance()
	{
		ResultSet rs=null;
		PreparedStatement ps=null;
		Connection conn=null;
	
		try
		{
			conn=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sql="select * from t_hr_relatingcode";
			rs=dao.search(sql);
			while(rs.next())
			{
				RelatingcodeBean rcb=new RelatingcodeBean();
				rcb.setCodesetid(PubFunc.nullToStr(rs.getString("codesetid")));
				rcb.setCodetable(PubFunc.nullToStr(rs.getString("codetable")));
				rcb.setCodevalue(PubFunc.nullToStr(rs.getString("codevalue")).toLowerCase());
				rcb.setCodedesc(PubFunc.nullToStr(rs.getString("codedesc")).toLowerCase());
				list.add(rcb);
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(ps!=null)ps.close();
				if(rs!=null)rs.close();
				if(conn!=null)
					conn.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * 得到要关联的对象
	 * @param busb
	 * @return
	 */
	public  RelatingcodeBean getDisplayField(BusifieldBean busb)
	{
		RelatingcodeBean temp=new RelatingcodeBean();
		for(int i=0;i<list.size();i++)
		{
			RelatingcodeBean rcb=(RelatingcodeBean)list.get(i);
			if(rcb.getCodesetid().equals(busb.getCodesetid()))
			{
				temp=rcb;
				break;
			}
		}
		return temp;
	}

}
