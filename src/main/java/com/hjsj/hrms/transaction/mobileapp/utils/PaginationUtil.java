package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 分页相关工具类
 * <p>Title: PaginationUtil </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-12-17 下午01:36:41</p>
 * @author xuj
 * @version 1.0
 */
public class PaginationUtil {

	/**
	 * 以RowSet集的方式返回当前分页记录
	 * @param sql
	 * @param pageindex
	 * @param pagesize
	 * @return
	 */
	public static RowSet execSQLByPage(String sql,int pageindex,int pagesize){
		Connection conn = null;
		RowSet rs = null;
		StringBuffer sqlstr = new StringBuffer();
		try{
			conn=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
     		sqlstr.append("select * from ( select  ROW_NUMBER() over( ORDER BY ord, A0000 ) numberCode"); 
			sqlstr.append(",A.* from ("+sql+") A) T where numberCode between "+((pageindex-1)*pagesize+1)+" and "+(pagesize*pageindex));
    		rs=dao.search(sqlstr.toString());
		}catch (SQLException sqle){
			sqle.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
					//e.printStackTrace();
				}
			}
		return rs;
	}
	
	/**
	 * 
	 * @Title: execSQLOneByPage   
	 * @Description: 以RowSet集的方式返回当前分页记录    
	 * @param sql sql语句
	 * @param pageindex 第几页
	 * @param pagesize 每页显示的条数
	 * @param orderBY  格式：order by ##
	 * @return RowSet 
	 * @throws
	 */
	public static RowSet execSQLOneByPage(String sql,int pageindex,int pagesize,String orderBY){
		Connection conn = null;
		RowSet rs = null;
		StringBuffer sqlstr = new StringBuffer();
		try{
			conn=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
     		sqlstr.append("select * from ( select  ROW_NUMBER() over("+orderBY+") numberCode"); 
			sqlstr.append(",A.* from ("+sql+") A) T where numberCode between "+((pageindex-1)*pagesize+1)+" and "+(pagesize*pageindex));
    		rs=dao.search(sqlstr.toString());
		}catch (SQLException sqle){
			sqle.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
					//e.printStackTrace();
				}
			}
		return rs;
	}
	
	/**
	 * 以List<Map>集的方式返回当前分页记录
	 * @param sql
	 * @param pageindex
	 * @param pagesize
	 * @return
	 */
	public static ArrayList execSQLByPageList(String sql,int pageindex,int pagesize){
		ArrayList datalist = new ArrayList();
		Connection conn = null;
		RowSet rs = null;
		StringBuffer sqlstr = new StringBuffer();
		try{
			conn=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
     		sqlstr.append("select * from ( select  ROW_NUMBER() over( ORDER BY ord, A0000 ) numberCode"); 
			sqlstr.append(",A.* from ("+sql+") A) T where numberCode between "+((pageindex-1)*pagesize+1)+" and "+(pagesize*pageindex));
			
			rs=dao.search(sqlstr.toString());
		    
			ResultSetMetaData meta = rs.getMetaData();
		   
			ArrayList fieldname=new ArrayList();
			int columnCount = meta.getColumnCount();
		    for(int i=0;i<columnCount;i++)
			{
				fieldname.add(meta.getColumnName(i + 1).toLowerCase());				
			}
			while(rs.next())
			{
				Map rec=new HashMap();
				//读取列 元数据
				for (int i = 0; i < columnCount; i++) {
					String oTemp="";
					if("text".equalsIgnoreCase(meta.getColumnTypeName(i+1)) ||"clob".equalsIgnoreCase(meta.getColumnTypeName(i+1)))					  		
					  	oTemp=Sql_switcher.readMemo(rs,meta.getColumnName(i + 1));
					else
					  	oTemp=rs.getString(i + 1);									   
					if(oTemp!=null)
					{
					  rec.put(fieldname.get(i).toString(),oTemp);					 
					}else
				    {
				    	rec.put(fieldname.get(i).toString(),"");				 
				    }
				}
				datalist.add(rec);				
			}
		   }catch (SQLException sqle){
				sqle.printStackTrace();
			}
			catch (GeneralException ge){
				ge.printStackTrace();
			}finally{
				try{
					if (rs != null){
						rs.close();
					}
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
					//e.printStackTrace();
				}
			}
		return datalist;
	}
}
