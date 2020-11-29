package com.hjsj.hrms.valueobject.database;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-5-11:17:20:58</p>
 * @author Administrator
 * @version 1.0
 * 
 */
public class ExecuteSQL {
	//执行sql语句封装数据到List返回List对象
	public  static List executeMyQuery(String strSQL){
		List resultlist=new ArrayList();
		List datalist=null;
		 Connection conn = null;
		 ResultSet rs = null;
		try{
			conn=AdminDb.getConnection();
			
			ContentDAO dao = new ContentDAO(conn);
    		rs=dao.search(strSQL);
		    
			ResultSetMetaData meta = rs.getMetaData();
		   
			ArrayList fieldname=new ArrayList();
			int columnCount = meta.getColumnCount();
		    for(int i=0;i<columnCount;i++)
			{
				fieldname.add(meta.getColumnName(i + 1).toLowerCase());				
			}
			for(;rs.next();)
			{
				LazyDynaBean rec=new LazyDynaBean();
				//读取列 元数据
				for (int i = 0; i < columnCount; i++) {
				    String oTemp = PubFunc.getValueByFieldType(rs, meta, i+1); 								   
					if(oTemp!=null)
					{
					  rec.set(fieldname.get(i).toString(),oTemp);					 
					}else
				    {
				    	rec.set(fieldname.get(i).toString(),"");				 
				    }
				}
				resultlist.add(rec);				
			}
			//执行当前单元格的查询的sql语句	
			//datalist=new RowSetDynaClass(rs).getRows(); 
		   }catch (SQLException sqle){
				sqle.printStackTrace();
			}
			catch (GeneralException ge){
				ge.printStackTrace();
			}
			finally{
				try{
					//conn.commit();  //chenmengqing changed at 20060720
					if (rs != null){
						rs.close();
					}
					
					if (conn != null){
						conn.close();
					}
				}catch (SQLException sql){
					//sql.printStackTrace();
				}
			}
		return resultlist;
	}
	

    

    public  static List executeMyQuery(String strSQL,Connection conn){
        return executeMyQuery(strSQL,conn,false);
        
    }
    
    /**   
     * @Title: executeMyQuery   
     * @Description:    
     * @param @param strSQL
     * @param @param conn
     * @param @param bReturnEmptyValue 数值型指标为空，则用空字符串表示 2014-01-21 wangrd
     * @param @return 
     * @return List 
     * @author:wangrd   
     * @throws   
    */
    
	public  static List executeMyQuery(String strSQL,Connection conn,boolean bReturnEmptyValue){
		List resultlist=new ArrayList();
		 RowSet rs=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(strSQL);
			//
			//rs=stmt.executeQuery(strSQL);
			ResultSetMetaData meta = rs.getMetaData();
			ArrayList fieldname=new ArrayList();
			int columnCount = meta.getColumnCount();
			for(int i=0;i<columnCount;i++)
			{
				fieldname.add(meta.getColumnName(i + 1).toLowerCase());				
			}
			for(;rs.next();)
			{
				LazyDynaBean rec=new LazyDynaBean();
				//读取列 元数据
				for (int i = 0; i < columnCount; i++) 
				{
					String oTemp="";
					oTemp = PubFunc.getValueByFieldType(rs, meta, i+1,bReturnEmptyValue);
					/*
					switch(Sql_switcher.searchDbServer())
					{
					  case Constant.MSSQL:
					  {
						    if("text".equalsIgnoreCase(meta.getColumnTypeName(i+1)))
								 oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
							 else
						 	     oTemp=rs.getString(i + 1);
						  break;
					  }
					  case Constant.DB2:
					  {
						 if("text".equalsIgnoreCase(meta.getColumnTypeName(i+1)))
							 oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
						 else
					 	     oTemp=rs.getString(i + 1);
					  	break;
					  }
					  case Constant.ORACEL:
					  {
					  	if("clob".equalsIgnoreCase(meta.getColumnTypeName(i+1)))					  		
					  	  oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
					  	else
					  	  oTemp=rs.getString(i + 1);
					  }
					}	*/  //chenmengqing remove them	at 20070616		   
					if(oTemp!=null)
					{
						if("i9999".equalsIgnoreCase(fieldname.get(i).toString()))
						{
							if(oTemp.indexOf(".")!=-1)
							{
								oTemp=oTemp.substring(0,oTemp.indexOf("."));
							}
							rec.set(fieldname.get(i).toString(),oTemp);	
						}else
					       rec.set(fieldname.get(i).toString(),oTemp);					 
					}else
				    {
				    	rec.set(fieldname.get(i).toString(),"");				 
				    }
				}
				resultlist.add(rec);				
			}
			//执行当前单元格的查询的sql语句	
			//datalist=new RowSetDynaClass(rs).getRows(); 
		   }catch (Exception  ex){
				ex.printStackTrace();
			}
		   finally
		   {
			try{
				if (rs != null){
					rs.close();
				}
//				if (stmt != null){
//					stmt.close();
//				}
			}catch (SQLException sql){
				sql.printStackTrace();  //chenmengqing removed at 20070616
			}
		}
		return resultlist;
	}
	
	public  static List executePreMyQuery(String strSQL,ArrayList values,Connection conn){
		List resultlist=new ArrayList();
		 RowSet rs=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(strSQL,values);
			//
			//rs=stmt.executeQuery(strSQL);
			ResultSetMetaData meta = rs.getMetaData();
			ArrayList fieldname=new ArrayList();
			int columnCount = meta.getColumnCount();
			for(int i=0;i<columnCount;i++)
			{
				fieldname.add(meta.getColumnName(i + 1).toLowerCase());				
			}
			for(;rs.next();)
			{
				LazyDynaBean rec=new LazyDynaBean();
				//读取列 元数据
				for (int i = 0; i < columnCount; i++) 
				{
					String oTemp="";
					oTemp = PubFunc.getValueByFieldType(rs, meta, i+1);
					/*
					switch(Sql_switcher.searchDbServer())
					{
					  case Constant.MSSQL:
					  {
						    if("text".equalsIgnoreCase(meta.getColumnTypeName(i+1)))
								 oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
							 else
						 	     oTemp=rs.getString(i + 1);
						  break;
					  }
					  case Constant.DB2:
					  {
						 if("text".equalsIgnoreCase(meta.getColumnTypeName(i+1)))
							 oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
						 else
					 	     oTemp=rs.getString(i + 1);
					  	break;
					  }
					  case Constant.ORACEL:
					  {
					  	if("clob".equalsIgnoreCase(meta.getColumnTypeName(i+1)))					  		
					  	  oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
					  	else
					  	  oTemp=rs.getString(i + 1);
					  }
					}	*/  //chenmengqing remove them	at 20070616		   
					if(oTemp!=null)
					{
						if("i9999".equalsIgnoreCase(fieldname.get(i).toString()))
						{
							if(oTemp.indexOf(".")!=-1)
							{
								oTemp=oTemp.substring(0,oTemp.indexOf("."));
							}
							rec.set(fieldname.get(i).toString(),oTemp);	
						}else
					       rec.set(fieldname.get(i).toString(),oTemp);					 
					}else
				    {
				    	rec.set(fieldname.get(i).toString(),"");				 
				    }
				}
				resultlist.add(rec);				
			}
			//执行当前单元格的查询的sql语句	
			//datalist=new RowSetDynaClass(rs).getRows(); 
		   }catch (Exception  ex){
				ex.printStackTrace();
			}
		   finally
		   {
//			try{
//				if (rs != null){
//					rs.close();
//				}
//				if (stmt != null){
//					stmt.close();
//				}
//			}catch (SQLException sql){
//				sql.printStackTrace();  //chenmengqing removed at 20070616
//			}
		}
		return resultlist;
	}
	
//	执行sql语句返回ResultSet对象
	public ResultSet execQuery(String query,Connection conn)
	     throws SQLException, Exception {
		//Connection conn=null;

		 ResultSet rs = null;
	try {
		//conn = AdminDb.getConnection();
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(query);		
	} catch (SQLException sqle) {
		System.out.println("SQLException: Could not execute the query." + query);
	} catch (Exception e) {
		String error = "An exception occured while executing the query.";
		throw new Exception(error);
	} finally {
		
	}
	return rs;
  }
	
	
	private  Connection conn = null;
	private  Statement stmt = null;
	private  ResultSet rs = null;
	//释放连接
	public void freeConn(){
	try {
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		if (conn != null)
			conn.close();		
	} catch (SQLException cnfe) {
		System.out.println("SQLException:Exception in freeConn() ");
	} catch (Exception e) {
		e.printStackTrace();
	}
  }
	
	
	
	//执行sql语句返回ResultSet对象
 public ResultSet execQuery(String query)
	     throws SQLException, Exception {
	 ResultSet rs = null;
	try {
		conn = AdminDb.getConnection();
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(query);		
	} catch (SQLException sqle) {
		System.out.println("SQLException: Could not execute the query." + query);
	} catch (Exception e) {
		String error = "An exception occured while executing the query.";
		throw new Exception(error);
	} finally {
		
		if(conn!=null)
			conn.close();
	}
	return rs;
  }
	
	/*public void execUpdate(String sql) throws SQLException, Exception {
		try {
			if(conn!=null)
			{
			  conn.setAutoCommit(true);
			  stmt =
			}else
			{
			
			  conn = AdminDb.getConnection();
			  stmt = 
			}			
			stmt.executeUpdate(sql);
			conn.commit();
		} catch (SQLException sqle) {
			String error = "SQLException: Could not execute update ." + sql;
			throw new SQLException(error);
		} catch (Exception e) {
			String error = "An exception occured while executing update.";
			throw new Exception(error);
		} finally {
			freeConn();
		}

	}*/
	public void execUpdate(String sql,Connection conn) throws SQLException, Exception {
		try 
		{
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql);
			//conn.commit();
		} catch (SQLException sqle) {
			//String error = "SQLException: Could not execute update ." + sql;
			sqle.printStackTrace();
			//throw new SQLException(error);
		} catch (Exception e) {
			String error = "An exception occured while executing update.";
			throw new Exception(error);
		} 
		finally 
		{
			
		}

	}
	public static void createTable(String sql,Connection conn) throws SQLException, Exception {
		
		try 
		{			
			ContentDAO dao = new ContentDAO(conn);
			DbWizard db = new DbWizard(conn);
			String[] split = sql.split(" ");
			if("drop".equalsIgnoreCase(split[0])){
				boolean existTable = db.isExistTable(split[2], false);
				if(existTable)
					dao.update(sql);
			}else{
				dao.update(sql);
			}
			//conn.commit();
		} catch (SQLException sqle) {
			String error = "SQLException: Could not execute update ." + sql;
			throw new SQLException(error);
		} catch (Exception e) {
			String error = "An exception occured while executing update.";
			throw new Exception(error);
		} 
		finally 
		{
		}

	}
	public void execUpdate(String sql) throws SQLException, Exception {
		Connection conn=null;

		try {

			if(conn!=null)
			{
			  //conn.setAutoCommit(true);
			
			}else
			{
			
			  conn = AdminDb.getConnection();

			}	
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql);
			//conn.commit();
		} catch (SQLException sqle) {
			String error = "SQLException: Could not execute update ." + sql;
			throw new SQLException(error);
		} catch (Exception e) {
			String error = "An exception occured while executing update.";
			throw new Exception(error);
		} finally {
			try{
				if(conn!=null)
					conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			//freeConn();
		}
	}
	public  static List executeMyQueryReplaceChar(String strSQL,String oleReplaceChar,String newReplaceChar){
		List resultlist=new ArrayList();
		 Connection conn = null;
		 ResultSet rs = null;
		try{
			conn=AdminDb.getConnection();
			
			ContentDAO dao = new ContentDAO(conn);
    		rs=dao.search(strSQL);
		    
			ResultSetMetaData meta = rs.getMetaData();
		   
			ArrayList fieldname=new ArrayList();
			int columnCount = meta.getColumnCount();
		    for(int i=0;i<columnCount;i++)
			{
				fieldname.add(meta.getColumnName(i + 1).toLowerCase());				
			}
			for(;rs.next();)
			{
				LazyDynaBean rec=new LazyDynaBean();
				//读取列 元数据
				for (int i = 0; i < columnCount; i++) {
					String oTemp="";
					if("text".equalsIgnoreCase(meta.getColumnTypeName(i+1)) ||"clob".equalsIgnoreCase(meta.getColumnTypeName(i+1)))					  		
					  	oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
					else
					  	oTemp=rs.getString(i + 1);									   
					if(oTemp!=null)
					{
					  rec.set(fieldname.get(i).toString(),oTemp.replaceAll(oleReplaceChar, newReplaceChar));					 
					}else
				    {
				    	rec.set(fieldname.get(i).toString(),"");				 
				    }
				}
				resultlist.add(rec);				
			}
			//执行当前单元格的查询的sql语句	
			//datalist=new RowSetDynaClass(rs).getRows(); 
		   }catch (SQLException sqle){
				sqle.printStackTrace();
			}
			catch (GeneralException ge){
				ge.printStackTrace();
			}
			finally{
				try{
					//conn.commit();  //chenmengqing changed at 20060720
					if (rs != null){
						rs.close();
					}
					
					if (conn != null){
						conn.close();
					}
				}catch (SQLException sql){
					//sql.printStackTrace();
				}
			}
		return resultlist;
	}
}
