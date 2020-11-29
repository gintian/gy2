/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.valueobject.UserView;
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

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 2, 2008:2:22:10 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class FieldSetServlet extends HttpServlet {
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	/**
	 * 
	 * @param flag =1
	 * @param infor =A人员  =B单位 =K职位 
	 * @return
	 */
	private String loadUserNodes(String flag,String infor,HttpServletRequest req)
	{
		StringBuffer buf=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		Connection conn = null;	
		RowSet rset=null;
		try
		{
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","root");
	        Document myDocument = new Document(root);
	        
			if("1".equalsIgnoreCase(flag))
			{
				EncryptLockClient lockclient = (EncryptLockClient)req.getSession().getServletContext().getAttribute("lock");
				UserView userView = (UserView)req.getSession().getAttribute("userView");
				VersionControl ver_ctrl=new VersionControl();	
				strsql.append("select inforid,Classname,Classpre from informationclass order by InforID");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					String classpre = rset.getString("Classpre");
					if("Y".equalsIgnoreCase(classpre)||"V".equalsIgnoreCase(classpre)||"W".equalsIgnoreCase(classpre)){
						if(!lockclient.isHaveBM(31))
							continue;
						if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
							continue;
					}
					if("H".equalsIgnoreCase(classpre)&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
						continue;
					Element child = new Element("TreeNode");
		            String name=rset.getString("Classname")+"指标集";
		            child.setAttribute("id", /*rset.getString("inforid")*/rset.getString("Classpre"));
		            child.setAttribute("text", name);
		            child.setAttribute("title", name);
	            	child.setAttribute("xml","/servlet/fieldsettree?flag=2&infor="+classpre);
		            child.setAttribute("target", "mil_body");			            
	            	child.setAttribute("href", "/system/dbinit/fieldsetlist.do?b_query=link&infor="+rset.getString("Classpre"));	
	    			//child.setAttribute("icon","/images/groups.gif");	 
		            root.addContent(child);	            	
				}
			}
			else//查子集列表
			{
				strsql.append("select fieldsetid,customdesc,useflag from fieldset where fieldsetid like '"+infor+"%' order by displayorder");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
		            Element child = new Element("TreeNode");
		            child.setAttribute("id", rset.getString("fieldsetid"));
		            child.setAttribute("text", rset.getString("fieldsetid")+"."+rset.getString("customdesc"));
		            child.setAttribute("title", rset.getString("customdesc"));
		            child.setAttribute("target", "mil_body");			            
	            	child.setAttribute("href", "/system/dbinit/fielditemlist.do?b_query=link&setid="+rset.getString("fieldsetid"));	
	            	if(!("0".equals(rset.getString("useflag"))))
	            		child.setAttribute("icon","/images/open1.png");	
	            	else
	            		child.setAttribute("icon","/images/close.png");	            		
	    			//child.setAttribute("icon","/images/groups.gif");	 
		            root.addContent(child);	            	
				}				
				
			}

	        
	        
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(myDocument));		        
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}		
		return buf.toString();
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	StringBuffer treexml = new StringBuffer();	
    	req.setCharacterEncoding("GBK");
    	String flag=(String)req.getParameter("flag");
    	/**信息群标识*/
        String infor=(String)req.getParameter("infor");
		try 
		{
			treexml.append(loadUserNodes(flag,infor,req));				
		} catch (Exception e) {
			e.printStackTrace();
		}			
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(treexml.toString());        
	}	
}
