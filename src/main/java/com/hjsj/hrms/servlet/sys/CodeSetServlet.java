/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
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
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 3, 2008:3:55:32 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CodeSetServlet extends HttpServlet {
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	/**
	 * 
	 * @param flag =1　
	 * @param status=2 用户｜系统
	 * 
	 * @return
	 */
	private String loadUserNodes(String flag,String status,String codesetid,String parentid,String cate,String fromflag)
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
	        	Element child = new Element("TreeNode");
	            child.setAttribute("id", "00");
	            child.setAttribute("text", ResourceFactory.getProperty("codemaintence.code.sys"));
            	child.setAttribute("xml","/servlet/codesettree?flag=2&status=1&fromflag="+fromflag);
	            child.setAttribute("target", "mil_body");			            
            	child.setAttribute("href", "/system/codemaintence/codetree.do?b_search=link&status=2&categories=");
            	child.setAttribute("icon","/images/close.png");
 	            root.addContent(child);
 	            
	        	child = new Element("TreeNode");
	            child.setAttribute("id", "02");
	            child.setAttribute("text", ResourceFactory.getProperty("codemaintence.code.user"));
            	child.setAttribute("xml","/servlet/codesettree?flag=2&status=2&fromflag="+fromflag);
	            child.setAttribute("target", "mil_body");			            
            	child.setAttribute("href", "/system/codemaintence/codetree.do?b_search=link&status=1&categories=");
            	child.setAttribute("icon","/images/close.png");
	            root.addContent(child);	 
	        }
	        else if("2".equalsIgnoreCase(flag))
			{
	        	ArrayList grouplist = new ArrayList();
	        	//if(status.equalsIgnoreCase("2"))//用户代码
//	        		strsql.append("select codesetid,codesetdesc from codeset  where codesetid not in('@K','UN','UM') and status<>2 order by codesetid");
	        		//strsql.append("select codesetid,codesetdesc,categories from codeset  where codesetid not in('@K','UN','UM') and (status not in ('1','2') or status is null) order by codesetid");
	        	//else//系统代码
//	        		strsql.append("select codesetid,codesetdesc from codeset  where status=2 order by codesetid");
	        		strsql.append("select codesetid,codesetdesc,categories from codeset where codesetid not in('@K','UN','UM','@@') order by codesetid");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					String categories = rset.getString("categories");
					if(categories==null||categories.trim().length()==0){
			            Element child = new Element("TreeNode");
			            child.setAttribute("id", rset.getString("codesetid"));
			            child.setAttribute("text", rset.getString("codesetid")+" "+rset.getString("codesetdesc"));
		            	child.setAttribute("xml","/servlet/codesettree?flag=3&codesetid="+rset.getString("codesetid")+"&parentid=-1&fromflag="+fromflag);
			            child.setAttribute("target", "mil_body");			            
		            	child.setAttribute("href", "/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+rset.getString("codesetid")+"&param=CORCODE&fromflag="+fromflag);//param.indexOf("LEVEL")!=-1//当属于级别设置时控制在jsp不显示职务代码列
		            	child.setAttribute("icon","/images/prop_ps.gif");
		    			//child.setAttribute("icon","/images/groups.gif");	 
			            root.addContent(child);	 
					}else{
						categories = categories.trim();
						if(!grouplist.contains(categories)){
							grouplist.add(categories);
							//String encodeCategories = SafeCode.encode(categories);
							Element child = new Element("TreeNode");
				            child.setAttribute("id", categories);
				            child.setAttribute("text", categories);
			            	//child.setAttribute("xml","/servlet/codesettree?flag=c&status="+status+"&categories="+encodeCategories);
				            String nodeParam = "flag=c&categories="+categories+"&fromflag="+fromflag;
				            child.setAttribute("xml","/servlet/codesettree?encryptParam="+PubFunc.encrypt(nodeParam));
				            child.setAttribute("target", "mil_body");
				            /*if("1".equals(status)){
				            	child.setAttribute("href", "/system/codemaintence/codetree.do?b_search=link&status=2&categories="+encodeCategories);
				            }else{//用户代码
				            	child.setAttribute("href", "/system/codemaintence/codetree.do?b_search=link&status=1&categories="+encodeCategories);
				            }*/
				            String urlParam = "b_search=link&categories="+categories;
				            child.setAttribute("href", "/system/codemaintence/codetree.do?encryptParam="+PubFunc.encrypt(urlParam));
			            	child.setAttribute("icon","/images/close.png");
			 	            root.addContent(child);	
						}
					}
				}
			}else if("c".equals(flag)){
				//if(status.equalsIgnoreCase("2"))
	        		//strsql.append("select codesetid,codesetdesc from codeset  where codesetid not in('@K','UN','UM') and (status not in ('1','2') or status is null) and categories='"+cate+"' order by codesetid");
	        	//else
	        		strsql.append("select codesetid,codesetdesc from codeset  where categories='"+cate+"' and codesetid not in('@K','UN','UM','@@') order by codesetid");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
		            Element child = new Element("TreeNode");
		            child.setAttribute("id", rset.getString("codesetid"));
		            child.setAttribute("text", rset.getString("codesetid")+" "+rset.getString("codesetdesc"));
	            	child.setAttribute("xml","/servlet/codesettree?flag=3&codesetid="+rset.getString("codesetid")+"&parentid=-1&fromflag="+fromflag);
		            child.setAttribute("target", "mil_body");			            
	            	child.setAttribute("href", "/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+rset.getString("codesetid")+"&param=CORCODE&fromflag="+fromflag);//param.indexOf("LEVEL")!=-1//当属于级别设置时控制在jsp不显示职务代码列
	            	child.setAttribute("icon","/images/prop_ps.gif");
	    			//child.setAttribute("icon","/images/groups.gif");	 
		            root.addContent(child);	            	
				}
			}
			else//查找代码项
			{
				strsql.append("select codesetid,codeitemid,codeitemdesc, childid from codeitem where codesetid='"+codesetid+"'");
				if("-1".equalsIgnoreCase(parentid))
				{
					strsql.append(" and codeitemid=parentid");
				}
				else
				{
					strsql.append(" and parentid='");
					strsql.append(parentid);
					strsql.append("' and codeitemid<>parentid");
				}
				strsql.append(" order by a0000,codeitemid");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
		            Element child = new Element("TreeNode");
		            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
		            child.setAttribute("text", rset.getString("codeitemdesc"));
		            child.setAttribute("target", "mil_body");	
		            child.setAttribute("icon","/images/icon_wsx.gif");
		            if(!(rset.getString("codeitemid").equalsIgnoreCase(rset.getString("childid"))))
		            	child.setAttribute("xml","/servlet/codesettree?flag=3&codesetid="+rset.getString("codesetid")+"&parentid="+rset.getString("codeitemid")+"&fromflag="+fromflag);	
		            else
		            {
		            	child.setAttribute("icon","/images/icon_wsx.gif");	
		            }
	            	child.setAttribute("href", "/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+rset.getString("codesetid")+rset.getString("codeitemid")+"&param=CORCODE&fromflag="+fromflag);	
		            
//	            	if(!(rset.getString("useflag").equals("0")))
//	            		child.setAttribute("icon","/images/open1.png");	
//	            	else
//	            		child.setAttribute("icon","/images/close.png");	            		
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
    	String status=(String)req.getParameter("status");
    	String codesetid=(String)req.getParameter("codesetid");
    	String parentid=(String)req.getParameter("parentid"); 
    	String categories = (String)req.getParameter("categories");
    	String fromflag=(String)req.getParameter("fromflag");
    	categories = SafeCode.decode(categories);
		try 
		{
			treexml.append(loadUserNodes(flag,status,codesetid,parentid,categories,fromflag));				
		} catch (Exception e) {
			e.printStackTrace();
		}			
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(treexml.toString());        
	}
	
}
