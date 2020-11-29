package com.hjsj.hrms.businessobject.report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class MidVariableBo {
	private Connection conn=null;
		
	public MidVariableBo(Connection conn) {
		this.conn=conn;
	}
	
	
	/**
	 * 得到某表的临时变量
	 * @param tabid
	 * @return
	 */
	public ArrayList getMidVariableList2(String tabid)
	{		
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			rset=dao.search("select * from MidVariable where nflag=2 and (TempletID="+tabid+" or cstate='1' ) order by sorting");
			while(rset.next())
			{
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("");// 没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch (rset.getInt("ntype")) {
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4:// 代码型
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				list.add(item);
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
				 if(rset!=null) {
                     rset.close();
                 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}	 			
		return list;		
	}
	
	
	
	/**
	 * 得到某表的临时变量
	 * @param tabid
	 * @return
	 */
	public ArrayList getMidVariableList(String tabid)
	{		
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from MidVariable where nflag=2 ");
			while(recset.next())
			{
				RecordVo vo=new RecordVo("MidVariable");
				vo.setInt("nid",recset.getInt("nid"));
				vo.setString("cname",recset.getString("cname"));
				vo.setString("chz",recset.getString("chz"));
				vo.setInt("ntype",recset.getInt("ntype"));
				vo.setString("cvalue",Sql_switcher.readMemo(recset,"cvalue"));
				vo.setInt("nflag",recset.getInt("nflag"));
				vo.setString("cstate",recset.getString("cstate"));
				vo.setInt("fldlen",recset.getInt("fldlen"));
				vo.setInt("flddec",recset.getInt("flddec"));
				vo.setInt("templetid",recset.getInt("templetid"));
				vo.setString("codesetid",recset.getString("codesetid"));
				list.add(vo);
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
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}	 			
		return list;		
	}
	
	/**
	 * 得到某表的临时变量
	 * @param tabid
	 * @return
	 */
	public ArrayList getMidVariableList3(String tabid)
	{		
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from MidVariable where nflag=2 and (TempletID="+tabid+" or cstate='1' ) order by sorting");
			while(recset.next())
			{
				RecordVo vo=new RecordVo("MidVariable");
				vo.setInt("nid",recset.getInt("nid"));
				vo.setString("cname",recset.getString("cname"));
				vo.setString("chz",recset.getString("chz"));
				vo.setInt("ntype",recset.getInt("ntype"));
				vo.setString("cvalue",Sql_switcher.readMemo(recset,"cvalue"));
				vo.setInt("nflag",recset.getInt("nflag"));
				vo.setString("cstate",recset.getString("cstate"));
				vo.setInt("fldlen",recset.getInt("fldlen"));
				vo.setInt("flddec",recset.getInt("flddec"));
				vo.setInt("templetid",recset.getInt("templetid"));
				vo.setString("codesetid",recset.getString("codesetid"));
				list.add(vo);
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
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}	 			
		return list;		
	}	
	
	
	/**
	 * 往临时表里的临时变量字段插入数据
	 * @param tableName   临时表名称
	 * @param flag		  1:人员库 2：单位库  3：职位库
	 * @param vo		  临时变量对象
	 */
	public void updateTempVariable(String tableName,int flag,RecordVo vo)
	{
		
		
		
		

	}
	
	
	
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
