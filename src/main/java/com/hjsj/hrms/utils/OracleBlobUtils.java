/**
 * 
 */
package com.hjsj.hrms.utils;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hrms.frame.dao.RecordVo;

import java.io.*;
import java.sql.*;
/**
 * @author chenmengqing
 *
 */
public class OracleBlobUtils {
	
	private Connection conn=null;
	
	/**是否由oracleBlobUtils关闭传入的流 **/
	private boolean closeStream = true;
	
	public OracleBlobUtils(Connection conn) {
		this.conn=conn;
	}	
	
	/**设置是否由oracleBlobUtils关闭传入的流 **/
	public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }
 
    
	public Clob readClob(RecordVo vo,String fieldname,String content,String strWhere)
	{
		PreparedStatement stmt=null;
		ResultSet rs=null;
		Clob clob=null;

		String stab=vo.getModelName();
		StringBuffer buf=new StringBuffer();
		buf.append("select ");
		buf.append(fieldname);
		buf.append(" from ");
		buf.append(stab);
		buf.append(" where ");
		buf.append(strWhere);
		buf.append(" for update");		
		
		StringBuffer insbuf=new StringBuffer();
		insbuf.append("update ");
		insbuf.append(stab);
		insbuf.append(" set ");
		insbuf.append(fieldname);
		insbuf.append("=empty_clob() where ");
		insbuf.append(strWhere);

		DbSecurityImpl dbs = new DbSecurityImpl();
		DbSecurityImpl dbs2 = new DbSecurityImpl();
		try 
		{
			stmt=conn.prepareStatement(insbuf.toString());
//			for(int i=0;i<list.size();i++)
//			{
//				stmt.setObject(i+1,vo.getObject(list.get(i).toString()));
//			}
			dbs.open(conn, insbuf.toString());
			stmt.executeUpdate();
			
			PubFunc.closeResource(stmt);
			stmt=conn.prepareStatement(buf.toString());
//			for(int i=0;i<list.size();i++)
//			{
//				stmt.setObject(i+1,vo.getObject(list.get(i).toString()));
//			}
			dbs2.open(conn, buf.toString());
			rs = stmt.executeQuery();
			
			if(rs.next())
			{
				clob = (oracle.sql.CLOB) rs .getClob(1);
				clob=convertDruidToOracle(clob);
				clob.setString(1, content);
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			dbs.close(conn);
			dbs2.close(conn);
			PubFunc.closeResource(rs);
			PubFunc.closeResource(stmt);
		}
		return clob;		
	}
	/**
	 * 修改CLOB类型
	 * @param stab        表名
	 * @param fieldname   字段名
	 * @param content     修改内容
	 * @param strWhere    定位修改记录的条件 for examples id='aaaa' and status=1
	 */
    public   void   modifyClob(String stab,String fieldname,String content,String strWhere) 
    {   
			StringBuffer insbuf=new StringBuffer();
			insbuf.append("update ");
			insbuf.append(stab);
			insbuf.append(" set ");
			insbuf.append(fieldname);
			insbuf.append("=? where ");
			insbuf.append(strWhere);
			PreparedStatement stmt=null;
			DbSecurityImpl dbs = new DbSecurityImpl();
            try   
            {   
            	    stmt = conn.prepareStatement(insbuf.toString());			
                    Reader clobReader = new StringReader(content); 
                    stmt.setCharacterStream(1, clobReader, content.length());
                    dbs.open(conn, insbuf.toString());
                    int num = stmt.executeUpdate();// 执行SQL
            } catch   (Exception   ex)   {   
            	ex.printStackTrace();
            }   
    		finally
    		{
    		    dbs.close(conn);
    			try
    			{
    				if(stmt!=null)
    					stmt.close();
    			}
    			catch(Exception ggg)
    			{
    				ggg.printStackTrace();
    			}
    		}
    }  	
	/**
	 * strSearch "SELECT thefile FROM announce WHERE ID='222' FOR UPDATE"
	 * strInsert "UPDATE announce SET thefile=EMPTY_BLOB() WHERE ID='xxx'"
	 * @param strsql
	 * @return
	 */
	public Blob readBlob(String strSearch,String strInsert,InputStream stream)
	{
		Statement stmt=null;
		ResultSet rs=null;
		Blob blob=null;
		BufferedOutputStream out=null;
		BufferedInputStream in=null;
		DbSecurityImpl dbs = new DbSecurityImpl();
		try 
		{
			
			dbs.open(conn, strInsert);
			
			stmt=conn.createStatement();
			stmt.executeUpdate(strInsert);
			//System.out.println(strSearch);
			
			rs = stmt.executeQuery(strSearch);
			/*conn.commit();

			conn.setAutoCommit(false);
*/

			if(rs.next())
			{
		/*		if(SystemConfig.getProperty("webserver").equals("weblogic"))
				{
					blob=(Blob)rs.getBlob(1);
					out = new BufferedOutputStream(((OracleThinBlob)blob).getBinaryOutputStream());
		
				}
				else*/
				{  
					//Object oj=rs.getBlob(1);
					//System.out.println(oj.getClass().getName());
					//blob=(BLOB)rs.getBlob(1);
					//out = new BufferedOutputStream(((BLOB)blob).getBinaryOutputStream());
					blob=rs.getBlob(1);
					//参考JDK文档中这里的参数最低传1,传0的话就会出现,调用控制参数出错的问题  xcs @2014-12-11
					//the position in the BLOB value at which to start writing; the first position is 1 这里是JDK文档中的原话
					out = new BufferedOutputStream(blob.setBinaryStream(1));
				}
				in = new BufferedInputStream(stream);
				int c=0;
				while ((c=in.read())!=-1) 
					out.write(c);
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			dbs.close(conn);
			PubFunc.closeResource(out);
			PubFunc.closeResource(in);
			if (this.closeStream)
			    PubFunc.closeResource(stream);
			PubFunc.closeResource(rs);
			PubFunc.closeResource(stmt);
		}
		return blob;
	}
	public static Clob convertDruidToOracle(Object clob){
		if(clob instanceof com.alibaba.druid.proxy.jdbc.ClobProxyImpl){
			com.alibaba.druid.proxy.jdbc.ClobProxyImpl impl = (com.alibaba.druid.proxy.jdbc.ClobProxyImpl)clob;
			clob = impl.getRawClob(); // 获取原生的这个 Clob
		}
		return (Clob) clob;
	}



	/**
	 * strSearch "SELECT thefile FROM announce WHERE ID='222' FOR UPDATE"
	 * strUpdate "UPDATE announce SET thefile=? WHERE ID='xxx'"
	 * @param strsql
	 * @return
	 */
	public Clob readClob(String strSearch,String strUpdate,String context)
	{
		Statement stmt=null; 
		ResultSet rs=null;
		Clob clob=null;
		PreparedStatement pstmt= null;
		DbSecurityImpl dbs = new DbSecurityImpl();
		try 
		{
			stmt=conn.createStatement();

			dbs.open(conn, strSearch);
			rs = stmt.executeQuery(strSearch);
			pstmt=conn.prepareStatement(strUpdate);
			if(rs.next())
			{
				clob=rs.getClob(1);
				clob=convertDruidToOracle(clob);
				clob.setString(1,context);
			}
			pstmt.setClob(1,clob);
			pstmt.executeUpdate();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
		    dbs.close(conn);
		    PubFunc.closeResource(rs);
		    PubFunc.closeResource(stmt);
		    PubFunc.closeResource(pstmt);
		}
		return clob;
	}

}
