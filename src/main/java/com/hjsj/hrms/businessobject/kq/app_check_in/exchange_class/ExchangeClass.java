package com.hjsj.hrms.businessobject.kq.app_check_in.exchange_class;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExchangeClass {

	public ArrayList getNewFiledList(ArrayList filedlist)
	{
		
		if(filedlist!=null&&filedlist.size()>0)
		{
			for(int i=0;i<filedlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)filedlist.get(i);
				if("A".equals(fielditem.getItemtype())|| "N".equals(fielditem.getItemtype()))
				{
					if("a0100".equals(fielditem.getItemid())||"i9999".equals(fielditem.getItemid()))
					{
							fielditem.setVisible(false);
					}else if(fielditem.getItemid().indexOf("z5")!=-1||fielditem.getItemid().indexOf("z0")!=-1)
					{
						fielditem.setVisible(true);
					}else
					{
							if("1".equals(fielditem.getState()))
							{
								fielditem.setVisible(true);
							}else
							{
								fielditem.setVisible(false);
							}
					}					
				}													
			}
		}
		return filedlist;
	}
	/**
	 * 
	 * @param fieldsetlist
	 * @return
	 */
	public String getColumn(ArrayList fieldsetlist)
	{
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
			
			   column.append(fielditem.getItemid()+",");
		}
		column.setLength(column.length()-1);
		return column.toString();
	}
	/**
	 * 得到班次id
	 * @param nbase
	 * @param a0100
	 * @param date
	 * @param conn
	 * @return
	 */
	public String getClassId(String nbase,String a0100,String date,Connection conn)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select "+Sql_switcher.isnull("class_id","''")+" as class_id from kq_employ_shift");
		sql.append(" where nbase='"+nbase+"'");
		sql.append(" and a0100='"+a0100+"'");
		sql.append(" and q03z0='"+date+"'");
		ContentDAO dao=new ContentDAO(conn);
		String class_id="";
		RowSet rs=null;
		try
		{
			
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				class_id=rs.getString("class_id");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return class_id;
	}
	/**
	 * 返回班次名称
	 * @param class_id
	 * @param conn
	 * @return
	 */
	public String getClassName(String class_id,Connection conn)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select name from kq_class");
		sql.append(" where class_id='"+class_id+"'");	
		String class_name="";
		RowSet rs=null;
		try
		{
			
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(sql.toString());							
			if(rs.next())
			{
				class_name=rs.getString("name");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return class_name;		
	}
}
