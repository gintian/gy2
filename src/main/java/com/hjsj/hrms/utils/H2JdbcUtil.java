package com.hjsj.hrms.utils;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.lang.StringUtils;
import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.List;

public class H2JdbcUtil {

		 private static JdbcConnectionPool cp = null;
		 private static final String JDBC_URL_mssql = "jdbc:h2:mem:hr;MODE=MSSQLServer";  //Oracle
		 private static final String JDBC_URL_oracle = "jdbc:h2:mem:hr;MODE=Oracle"; 
		 private static final String USER = "yksoft";
		 private static final String PASSWORD = "yksoft2019";

		 
		 static{ 
			 try {
				 
				 //创建数据库连接池   
				 if(SystemConfig.getPropertyValue("dbserver")!=null&& "oracle".equalsIgnoreCase(SystemConfig.getPropertyValue("dbserver")))
				 {
					 cp = JdbcConnectionPool.create(JDBC_URL_oracle,USER,PASSWORD);
				 }
				 else
				 {
					 cp = JdbcConnectionPool.create(JDBC_URL_mssql,USER,PASSWORD);
				 }
				 cp.setLoginTimeout(5000);
				 cp.setMaxConnections(100);
			 } catch (Exception e) {
				 System.out.println("连接池初始化异常"); 
				 e.printStackTrace(); 
			 } 
		 } 

	 	/**      
	 	 * @Method: getConnection       
	 	 * @Description:获取数据库连接      
	 	 * @Anthor:dengcan     
	 	 * @return      
	 	 * @throws Exception      
	 	 */ 
		 public static Connection getConnection() throws Exception{
			 return cp.getConnection(); 
		 } 

		 public static JdbcConnectionPool getCp() { 
			 return cp; 
		 }
	 
		 
		 
		 
		 
		 /**
		   * 根据传入table对象创建数据库表结构
		   * @param table Table
		   * @return boolean
		   * for examples SQL语法
		   * create table xxxx (a varchar(20),b int ,c varchar(2),CONSTRAINT xxxx_1 PRIMARY KEY(a,b))
		   */
		  public static boolean createTable(Table table)throws GeneralException
		  {
			    if(table==null)
			      throw new GeneralException("","not defined the table","","");
			    if(table.getName()==null|| "".equals(table.getName()))
			      throw new GeneralException("","not defined the table's name","","");
			    if(table.size()==0)
			      throw new GeneralException("","not defined the table's fields","","");
			    boolean bflag=false; 
			    String strsql=table.getCreateTableSql(Sql_switcher.searchDbServer());
			    Connection conn=null; 
			    try
			    {
			    	  conn=getConnection(); 
			    	  ContentDAO dao=new ContentDAO(conn);
				      /**有表的话先删除*/
				      if(isExistTable(table.getName(),false))
				    	  dropTable(table.getName()); 
				      dao.update(strsql); 
				      bflag=true;
			    }
			    catch(Exception ex)
			    {
			      ex.printStackTrace();
			      throw GeneralExceptionHandler.Handle(ex);
			    }
			    finally
			    { 
			    	try
			        {  
			        	if (conn != null)
			            {
			        		conn.close();
			        		conn=null;
			            }
			        }
			        catch (SQLException se)
			        {
			          se.printStackTrace();
			        }
			    	
			    }
			    return bflag;    
		  }
		 
		 public static int executeUpdate(String sql,List values) throws SQLException
		  {
		    int iRet = 0,nlen; 
		    boolean bflag=false;
		    PreparedStatement pst=null;
		    Connection conn=null; 
		    try
		    { 
		    	  conn=getConnection(); 
			      pst = conn.prepareStatement(sql);
			      for (int i = 0; values != null && i < values.size(); i++)
			      {
			        Object o = values.get(i);
			        bflag=false;
			        if (o == null)
			        { 
			          pst.setNull(i+1,java.sql.Types.VARCHAR);  
			        }
			        else if(o instanceof byte[])
			        {
			          /**image类型字段*/
			          pst.setBytes(i+1,(byte[])o);  //MSSQL字段内容可以保存进去,ORACLE不能保存进去
			        }
			        else if(o instanceof java.sql.Blob)
			        {
			          /**blob类型字段*/
			          pst.setBlob (i+1,(java.sql.Blob)o);  //oracle
			        }
			        else if(o instanceof java.sql.Clob)
			        {
			          /**clob类型字段*/
			          pst.setClob (i+1,(java.sql.Clob)o);  //oracle
			        }  
			        else if(o instanceof Integer)
			        {
			          pst.setInt(i+1, ((Integer)o).intValue());
			        }         
			        else
			        {
			          if(o instanceof String)
			          {
			        	  nlen=o.toString().length();
			    		    	  
			        	  if(nlen>=1000&&Sql_switcher.searchDbServer()==Constant.ORACEL)
			        		  bflag=true;
			          }
			          if(bflag)//解决oracle clob字段内容超过一定长度存不进去的问题
			          {
			        	  //后台出现 java.sql.SQLException:   ORA-01461: 仅可以为插入 LONG 列的 LONG 值赋值。这个错误的特點是，
			        	  //当插入的数据长度小於1000字節或者大於2000字節時都不會報錯，一旦插入的数据长度在1000 ~ 2000之間時就會報錯
			    		  String tmp=o.toString();  
			    		  /**没有找到别的方法，只能后面补空格？*/
			       		  if(tmp.length()<2000)
			        		  tmp=StringUtils.rightPad(tmp, 2001, ' ');
			       		  Reader clobReader = new StringReader(tmp); 
			    		  pst.setCharacterStream(i+1, clobReader, tmp.length());     
			          }
			          else
			        	  pst.setObject(i + 1, o);
			        }
			      }
			      iRet = pst.executeUpdate();
		    }
		    catch (SQLException se)
		    {
		      se.printStackTrace();
		      throw se;
		    }
		    catch(Exception ee)
		    {
		    	ee.printStackTrace();
		    }
		    finally
		    { 
		    	try
		        { 
		        	if (pst != null)
		            {
		        		pst.close();
		        		pst=null;
		            }
		        	if (conn != null)
		            {
		        		conn.close();
		        		conn=null;
		            }
		        }
		        catch (SQLException se)
		        {
		          se.printStackTrace();
		        }
		    	
		    }
		    return iRet;
		  }
		 
		 /**
		  * 批量更新、插入、删除
		  * @param sqlTemplate
		  * @param list
		  */
		 public static void batchUpdate(String sqlTemplate, List<Object[]> list) {
				Connection conn = null;
				PreparedStatement ps = null;
				try {
					conn = getConnection();
					ps = conn.prepareStatement(sqlTemplate);
					conn.setAutoCommit(false);
					int size = list.size();
					Object[] o = null;
					for (int i = 0; i < size; i++) {
						o = list.get(i);
						for (int j = 0; j < o.length; j++) {
							ps.setObject(j + 1, o[j]);
						}
						ps.addBatch();
					}
		 
					ps.executeBatch();
					conn.commit();
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
					try {
						conn.rollback();
						conn.setAutoCommit(true);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}catch (Exception ee) {
					ee.printStackTrace();
				} finally {
					try
			        { 
			        	if (ps != null)
			            {
			        		ps.close();
			        		ps=null;
			            }
			        	if (conn != null)
			            {
			        		conn.close();
			        		conn=null;
			            }
			        }
			        catch (SQLException se)
			        {
			          se.printStackTrace();
			        }
				}
		  }
		 
		 /**
		   * 执行SQL语句
		   * @return boolean
		   */
		  public static boolean execute(String strsql)throws GeneralException
		  {
		   
		    boolean bflag=false;
		    Connection conn=null; 
		    try
		    { 
		    	 conn=getConnection(); 
		    	 ContentDAO dao=new ContentDAO(conn);
		    	 dao.update(strsql);
		    	 bflag=true;
		    }
		    catch(Exception ex)
		    {
		      throw GeneralExceptionHandler.Handle(ex);
		    }
		    finally
		   	{
		        try
		        { 
		        	if (conn != null)
		            {
		        		conn.close();
		        		conn=null;
		            }
		        }
		        catch (SQLException se)
		        {
		          se.printStackTrace();
		        }
		   	} 
		    return bflag;
		  }
		  
		  
		  public static  RowSet search(String sql, List values) throws Exception
		  {
			  Connection conn=null; 
			  String condition = ""; 
			  for (int i = 0; i < values.size(); i++)
			  {
				  condition += values.get(i).toString() + ",";
			  } 
		      CachedRowSet rset = new CachedRowSetImpl(); 
		      PreparedStatement pst=null;
		      ResultSet rs = null;
		      try
		      {
		    	    conn=getConnection(); 
		    	    pst = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			        for (int i = 0; values != null && i < values.size(); i++)
			        {
			          pst.setObject(i + 1, values.get(i));
			        }
			        rs = pst.executeQuery();
			        /**直接返回*/ 
			        rset.populate(rs);//? 有备注字段出错
		      }
		      catch (Exception e)
		      {
		        e.printStackTrace();
		        throw e;
		      }
		      finally
		      {
		    	  PubFunc.closeResource(rs);
		    	  PubFunc.closeResource(pst);
		    	  PubFunc.closeResource(conn);
		      } 
		      return rset;
		 }

		  
		  /**
		   * 删除表
		   * @param table Table
		   * @return boolean
		   * @throws GeneralException
		   */
		  public static boolean dropTable(String tablename)
		  {
		    boolean bflag=false;
		    StringBuffer strsql=new StringBuffer();
		    if(!isExistTable(tablename, false))
		    	return bflag;
		    strsql.append("drop table ");
		    strsql.append(tablename);
		    Connection conn=null; 
		    try
		    {
		    	  conn=getConnection(); 
		    	  ContentDAO dao=new ContentDAO( conn);  
			      dao.update(strsql.toString());
			      bflag=true;
		    }
		    catch(Exception ex)
		    { 
		    	
		    }
		    finally
		    {
			    	try
			    	{
			    		 
			    		if (conn != null)
			            {
			        		conn.close();
			        		conn=null;
			            }
			    	}
			    	catch(Exception ex)
			    	{
			    		ex.printStackTrace();
			    	}
		    }		  
		    return bflag;    
		  }
		  
		  
		  public static boolean isExistTable(String table_name,boolean bexception)
		  { 
				boolean bflag=false;
				Connection conn=null; 
				Statement stmt=null;
				try
				{
						conn=getConnection(); 
					    StringBuffer strsql=new StringBuffer();
					    strsql.append("select * from ");
					    strsql.append(table_name);
					    strsql.append(" where 1=2");			  
					    stmt=conn.createStatement();
					    stmt.executeQuery(strsql.toString());
					    bflag=true;
				 }
				 catch(Exception ex)
				 {
					  ;
				 }
				 finally
				 {
				    	try
				    	{
				    		if(stmt!=null)
				    			stmt.close();
				    		if (conn != null)
				            {
				        		conn.close();
				        		conn=null;
				            }
				    	}
				    	catch(Exception ex)
				    	{
				    		ex.printStackTrace();
				    	}
				  }		  
				  return bflag;
			  
		  }
		 
		 
	
}
