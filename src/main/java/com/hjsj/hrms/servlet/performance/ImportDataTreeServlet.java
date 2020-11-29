package com.hjsj.hrms.servlet.performance;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

/**
 * <p>Title:ImportDataTreeServlet.java</p>
 * <p>Description:绩效管理导入考核指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ImportDataTreeServlet extends HttpServlet
{
	
	public void init()
	{
		try
		{
	    	super.init();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		doPost(req,resp);
	}
	protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		String parentid=req.getParameter("parentid");
		String flag=req.getParameter("flag");
		req.setCharacterEncoding("GBK");
		StringBuffer XMLTREE = new StringBuffer();
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(XMLTREE.append(this.getXML(parentid,flag)).toString());  
	}
	public String getXML(String parentid,String flag)
	{
		Connection con=null;
		RowSet rs = null;
		RowSet rs_sub=null;
		StringBuffer xml = new StringBuffer("");
		try
		{
			con=(Connection)AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			/**模板*/
			if("1".equals(flag))
			{
	    		KhTemplateBo bo = new KhTemplateBo(con);
	    		HashMap map = this.getAllTemplate(dao, bo);
		    	StringBuffer sql = new StringBuffer("");
		    	String setTableName="##per_template_set_temp";
		    	String tableName="##per_template_temp";
		    	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    	{
		    		setTableName="per_template_set_temp";
		    		tableName="per_template_temp";
	    		}
	     		if("-1".equalsIgnoreCase(parentid))
	    		{
		    		sql.append("select * from "+setTableName+" where parent_id is null order by template_setid");
		    	}
		    	else
		    	{
		    		sql.append("select * from "+setTableName+" where parent_id="+parentid+" order by template_setid");
		    	}
	    		rs=dao.search(sql.toString());
	    		Element root = new Element("TreeNode");
		    	root.setAttribute("id","$$00");
		    	root.setAttribute("text","root");
		    	root.setAttribute("title","root");
		    	Document myDocument = new Document(root);
	    		while(rs.next())
		    	{
		    		Element child = new Element("TreeNode");
	    			child.setAttribute("id",rs.getString("template_setid")+"#"+"0");
		    		child.setAttribute("text",rs.getString("name"));
		    		child.setAttribute("title",rs.getString("name"));
		    		if(isHaveChild("template_setid",rs.getString("template_setid"),con,setTableName)||this.isHaveOtherChild("template_id",rs.getString("template_setid"), con,tableName,"template_setid"))
		    		{
	         			child.setAttribute("xml","/performance/kh_system/kh_template/kh_template_tree/ImportDataTreeServlet?parentid="+rs.getInt("template_setid")+"&flag="+flag);
		    		}
		    		if(rs.getInt("validflag")==1)
		    	    	child.setAttribute("icon","/images/open1.png");
		    		else
		    			child.setAttribute("icon","/images/open.png");
		    		root.addContent(child);
		    	}
		    	sql.setLength(0);
    	    	sql.append("select * from "+tableName+" where UPPER(template_setid)='"+parentid.toUpperCase()+"' ");
    	    	sql.append(" order by seq");
    	    	rs = dao.search(sql.toString());
    	    	while(rs.next())
    	      	{
    	    		Element child = new Element("TreeNode");
    	    		child.setAttribute("id",rs.getString("template_id")+"#"+"1");
    	    		if(map.get(rs.getString("template_id").toUpperCase())!=null)
    	    		{
    	    			if("1".equals((String)map.get(rs.getString("template_id").toUpperCase())))
    		    		{
    		    			child.setAttribute("text"," * ["+rs.getString("template_id")+"]"+rs.getString("name"));
    	    			}
    		    		if("2".equals((String)map.get(rs.getString("template_id").toUpperCase())))
    	    			{
    	    				child.setAttribute("text"," ! ["+rs.getString("template_id")+"]"+rs.getString("name"));
    	    			}
    	     		}
    	     		else
    	    		{
	                  	child.setAttribute("text","["+rs.getString("template_id")+"]"+rs.getString("name"));
    	    		}
    	        	child.setAttribute("title","["+rs.getString("template_id")+"]"+rs.getString("name"));
    	        	if(rs.getInt("validflag")==1)
	                 	child.setAttribute("icon","/images/lock_co_1.gif");
	            	else
	            		child.setAttribute("icon","/images/lock_co.gif");
	              	root.addContent(child);
    	    	}
    	    	XMLOutputter outputter = new XMLOutputter();
    			Format format=Format.getPrettyFormat();
    			format.setEncoding("UTF-8");
    			outputter.setFormat(format);
    			xml.append(outputter.outputString(myDocument));
			}
			else
			{
				KhFieldBo bo = new KhFieldBo(con);
	    		HashMap map = bo.getAllPoint();
		    	StringBuffer sql = new StringBuffer("");
		    	String setTableName="##per_pointset_temp";
		    	String tableName="##per_point_temp";
		    	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    	{
		    		setTableName="per_pointset_temp";
		    		tableName="per_point_temp";
	    		}
	     		if("-1".equalsIgnoreCase(parentid))
	    		{	     			
	     			// 判断导入的是全部还是单个考核指标分类 JinChunhai 2012.03.20
	     			if(isSumOrPip(setTableName,con))	     				     				     			
	     				sql.append("select * from "+setTableName+" where parent_id is null order by pointsetid");
	     			else
	     				sql.append("select * from "+setTableName+" where parent_id=(select min(parent_id) from "+setTableName+") ");
		    	}
		    	else
		    	{
		    		sql.append("select * from "+setTableName+" where parent_id="+parentid+" order by pointsetid");
		    	}
	    		rs=dao.search(sql.toString());
	    		Element root = new Element("TreeNode");
		    	root.setAttribute("id","$$00");
		    	root.setAttribute("text","root");
		    	root.setAttribute("title","root");
		    	Document myDocument = new Document(root);
	    		while(rs.next())
		    	{
		    		Element child = new Element("TreeNode");
	    			child.setAttribute("id",rs.getString("pointsetid")+"#"+"0");
		    		child.setAttribute("text",rs.getString("pointsetname"));
		    		child.setAttribute("title",rs.getString("pointsetname"));
		    		if(isHaveChild("pointsetid",rs.getString("pointsetid"),con,setTableName)||this.isHaveOtherChild("point_id",rs.getString("pointsetid"), con,tableName,"pointsetid"))
		    		{
	         			child.setAttribute("xml","/performance/kh_system/kh_template/kh_template_tree/ImportDataTreeServlet?parentid="+rs.getInt("pointsetid")+"&flag="+flag);
		    		}
		    		if(rs.getInt("validflag")==1)
		    	    	child.setAttribute("icon","/images/open1.png");
		    		else
		    			child.setAttribute("icon","/images/open.png");
		    		root.addContent(child);
		    	}
	    		
		    	sql.setLength(0);		    			    	
		    	if("-1".equalsIgnoreCase(parentid))
	    		{	     			
	     			// 判断导入的是全部还是单个考核指标分类 JinChunhai 2012.03.20
	     			if(isSumOrPip(setTableName,con))	     				     				     			
	     				sql.append("select * from "+tableName+" where UPPER(pointsetid)='"+parentid.toUpperCase()+"' order by seq ");
	     			else
	     				sql.append("select * from "+tableName+" where UPPER(pointsetid)=(select min(parent_id) from "+setTableName+") ");
		    	}else		    			    	
		    		sql.append("select * from "+tableName+" where UPPER(pointsetid)='"+parentid.toUpperCase()+"' order by seq ");

    	    	rs = dao.search(sql.toString());
    	    	while(rs.next())
    	      	{
    	    		Element child = new Element("TreeNode");
    	    		child.setAttribute("id",rs.getString("point_id")+"#"+"1");
    	    		if(map.get(rs.getString("point_id").toUpperCase())!=null)
    	    		{
    	    			/*if(((String)map.get(rs.getString("template_id").toUpperCase())).equals("1"))
    		    		{
    		    			child.setAttribute("text"," * ["+rs.getString("template_id")+"]"+rs.getString("name"));
    	    			}
    		    		if(((String)map.get(rs.getString("template_id").toUpperCase())).equals("2"))
    	    			{
    	    				child.setAttribute("text"," ! ["+rs.getString("template_id")+"]"+rs.getString("name"));
    	    			}*/
    	    			child.setAttribute("text"," * ["+rs.getString("point_id")+"]"+rs.getString("pointname"));
    	     		}
    	     		else
    	    		{
	                  	child.setAttribute("text","["+rs.getString("point_id")+"]"+rs.getString("pointname"));
    	    		}
    	        	child.setAttribute("title","["+rs.getString("point_id")+"]"+rs.getString("pointname"));
    	        	if(rs.getInt("validflag")==1)
	                 	child.setAttribute("icon","/images/lock_co_1.gif");
	            	else
	            		child.setAttribute("icon","/images/lock_co.gif");
	              	root.addContent(child);
    	    	}
    	    	XMLOutputter outputter = new XMLOutputter();
    			Format format=Format.getPrettyFormat();
    			format.setEncoding("UTF-8");
    			outputter.setFormat(format);
    			xml.append(outputter.outputString(myDocument));
			}
    		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con!=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(rs_sub!=null)
			{
				try
				{
					rs_sub.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return xml.toString();
	}
	
	
	public boolean isHaveChild(String column,String templatesetid,Connection con,String tableName)
	{
		boolean flag=false;
		RowSet rs = null;
		try
		{
			String sql ="select "+column+" from "+tableName+" where parent_id ='"+templatesetid+"'";
			ContentDAO dao = new ContentDAO(con);
			
			rs = dao.search(sql);
			while(rs.next())
			{
				flag=true;
				break;
			}
			
			if(rs!=null)
				rs.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
		return flag;
	}
	public boolean isHaveOtherChild(String column,String templateid,Connection con,String tableName,String whereColumn)
	{
		boolean flag=false;
		RowSet rs = null;
		try
		{
			String sql ="select "+column+" from "+tableName+" where UPPER("+whereColumn+")='"+templateid.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(con);
			
			rs = dao.search(sql);
			while(rs.next())
			{
				flag=true;
				break;
			}
			
			if(rs!=null)
				rs.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
		return flag;
	}
	/**
	 * 取得所有模板，使用的和未使用的分别作出标记
	 * @param dao
	 * @return
	 */
	public HashMap getAllTemplate(ContentDAO dao,KhTemplateBo bo)
	{
		HashMap map = new HashMap();
		try
		{
			String sql ="select template_id from per_template ";
			RowSet rs = dao.search(sql);
			HashMap useMap = bo.getAllUsedTemplate(dao);
			while(rs.next())
			{
				if(useMap.get(rs.getString("template_id").toUpperCase())!=null)
				{
					map.put(rs.getString("template_id").toUpperCase(), "2");
				}
				else
	    			map.put(rs.getString("template_id").toUpperCase(), "1");
			}
			
			if(rs!=null)
				rs.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	// 判断导入的是全部还是单个考核指标分类
	public boolean isSumOrPip(String setTableName,Connection con)
	{
		boolean flag=false;
		RowSet rs = null;
		try
		{
			String sql = "select * from "+setTableName+" where parent_id is null order by pointsetid ";
			ContentDAO dao = new ContentDAO(con);
			
			rs = dao.search(sql);
			while(rs.next())
			{
				flag=true;
				break;
			}
			
			if(rs!=null)
				rs.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
		return flag;
	}

}
