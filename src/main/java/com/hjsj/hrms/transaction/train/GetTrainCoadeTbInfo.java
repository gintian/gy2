/*
 * 创建日期 2005-8-22
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hrms.frame.dao.ContentDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author luangaojiong
 *
 * 
 */
public class GetTrainCoadeTbInfo {
	
	public static String [] getNumAttribute(Connection con,String tableName,String fieldName)
	{
		String temp [] ={"",""};
		PreparedStatement ps=null;
		ContentDAO dao = new ContentDAO(con);
		try
		{
			StringBuffer sb=new StringBuffer();
			sb.append("select itemlength,decimalwidth from t_hr_busifield where fieldsetid='");
			sb.append(tableName);
			sb.append("' and itemid='");
			sb.append(fieldName);
			sb.append("'");
			ResultSet rs=dao.search(sb.toString());
			if(rs.next())
			{
				temp[0]=rs.getString("itemlength");
				temp[1]=rs.getString("decimalwidth");
			}
			if(rs!=null)
			{
				rs.close();
			}
			if(ps!=null) 
				ps.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return temp;
	}

}
