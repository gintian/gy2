package com.hjsj.hrms.servlet.performance;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.WebConstant;
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
 * <p>Title:KhTemplateTree.java</p>
 * <p>Description>:KhTemplateTree.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-4-24 下午02:23:57</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class KhTemplateTree extends HttpServlet{
	protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		doPost(req,resp);
	}
	protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		String persionControl=(String)req.getParameter("persionControl"); //是否要权限控制模板树的展现 yes 是 no 否  JinChunhai 2011.03.02
		String templatesetid=(String)req.getParameter("templatesetid");
		String subsys_id=(String)req.getParameter("subsys_id");
		String b0110 = (String)req.getParameter("b0110");
		String isVisible=(String)req.getParameter("isVisible");
		String method=(String)req.getParameter("method");
		UserView userView=(UserView)req.getSession().getAttribute(WebConstant.userView);
		StringBuffer xmlTree = new StringBuffer();
		req.setCharacterEncoding("GBK");
		try
		{
			xmlTree=this.getXmlTree(templatesetid, subsys_id, b0110,isVisible,method,userView,persionControl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(xmlTree.toString());  
	}
	/**
	 * 
	 * @param templatesetid
	 * @param subsys_id
	 * @param b0110
	 * @param isVisible
	 * @param method method=0,直接进入考核模板模块，否则从考核计划中进入模板时，如果360度考核的话，
	 *                        不出现有个性指标的模板参数 method=1是360,=2是目标管理
	 * @return
	 */
	private StringBuffer getXmlTree(String templatesetid,String subsys_id,String b0110,String isVisible,String method,UserView userView,String persionControl)
	{
		Connection con=null;
		RowSet rs = null;
		StringBuffer buf = new StringBuffer();
		try
		{
			con=(Connection)AdminDb.getConnection();
			StringBuffer sql = new StringBuffer();
		    if("-1".equals(templatesetid))
		    {
		    	sql.append(" select * from per_template_set where parent_id is null ");
		    	if(!"0".equals(method))
		    		sql.append(" and validflag='1' ");
		    	sql.append(" and subsys_id='");
		    	sql.append(subsys_id+"' order by template_setid");
		    }
		    else
		    {
		    	sql.append(" select * from per_template_set where parent_id='"+templatesetid+"'  ");
		    	//sql.append(" and (((scope is null || scope = 0) or (scope is not null and b0110='"+userView.getUnit_id()+"')) or b0110='HJSJ')");
		    	if(!"0".equals(method))
		    		sql.append(" and validflag='1' ");
		    	sql.append(" and subsys_id='");
		    	sql.append(subsys_id+"' order by template_setid");
		    }
		    
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(sql.toString());
			Element root = new Element("TreeNode");
			root.setAttribute("id","$$00");
			root.setAttribute("text","root");
			root.setAttribute("title","root");
			Document myDocument = new Document(root);
			while(rs.next())
			{
				String scope = rs.getString("scope");
				String unit = rs.getString("b0110");
				if(!"no".equalsIgnoreCase(persionControl))
				{
					if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()) && !"HJSJ".equalsIgnoreCase(unit))
	    			{
						 
						if(scope!=null && !"0".equals(scope) && !(unit.length()>KhTemplateBo.getyxb0110(userView, con).length()?unit.substring(0, KhTemplateBo.getyxb0110(userView, con).length()):unit).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, con))){
							continue;
						}
//						if(scope!=null && !scope.equals("0") && !unit.equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, con))){
//							
//							System.out.println(KhTemplateBo.getyxb0110(userView, con));
//							continue;
//						}
	        		}
				}
				Element child = new Element("TreeNode");
				child.setAttribute("id",rs.getString("template_setid")+"#"+"0");
				child.setAttribute("text",rs.getString("name"));
				child.setAttribute("title",rs.getString("name"));
				//if(isHaveChild(rs.getString("template_setid"),con,userView)||this.isHaveOtherChild(rs.getString("template_setid"), con)){
	     			child.setAttribute("xml","/servlet/performance/KhTemplateTree?templatesetid="+rs.getInt("template_setid")+"&subsys_id="+subsys_id+"&isVisible="+isVisible+"&b0110="+b0110+"&method="+method+"&persionControl="+persionControl);
				
				//}
	     	    child.setAttribute("target","mil_body");
				child.setAttribute("href","/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid=-1&subsys_id="+subsys_id+"&isVisible="+isVisible+"&t_type=0");
				if(rs.getInt("validflag")==1)
			    	child.setAttribute("icon","/images/open1.png");
				else
					child.setAttribute("icon","/images/open.png");
				root.addContent(child);
			}
			
	    		sql.setLength(0);
	    		sql.append("select * from per_template where UPPER(template_setid)='"+templatesetid.toUpperCase()+"' ");
	    		/**=1显示不包括个性模板=2只显示个性模板，=3显示全部*/
	    		/** kind=1是共性=2是个性*/
	    		if("1".equals(method))
	    			sql.append(" and template_id in (select distinct template_id from per_template_item where kind='1')");
	    		if("2".equals(method))
	    			sql.append(" and template_id in (select distinct template_id from per_template_item where kind='2')");
	    		if(!"0".equals(method))
	    			sql.append(" and validflag='1' ");
	    		sql.append(" order by seq");
	    		rs = dao.search(sql.toString());
	    		while(rs.next())
	    		{
	    			if(!"no".equalsIgnoreCase(persionControl))
					{
		    			if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
		    			{
		        			if(!userView.isRWHaveResource(IResourceConstant.KH_MODULE,rs.getString("template_id")) && !userView.isRWHaveResource(IResourceConstant.KH_MODULE,rs.getString("template_id")+"R"))
		        			{
		        				continue;
		        			}
		        		}
					}
		        		Element child = new Element("TreeNode");
	        			child.setAttribute("id",rs.getString("template_id")+"#"+"1");
			        	child.setAttribute("text","["+rs.getString("template_id")+"]"+rs.getString("name"));
		    	    	child.setAttribute("title","["+rs.getString("template_id")+"]"+rs.getString("name"));
			    	//if(isHaveChild(rs.getString("pointsetid"),con)){
			    		///servlet/performance/KhFieldTree
			    		//child.setAttribute("xml","/servlet/performance/KhFieldTree?pointsetid="+rs.getInt("pointsetid")+"&subsys_id="+subsys_id+"&b0110="+b0110);
		    		//}
		        		child.setAttribute("target","mil_body");

		    	    	child.setAttribute("href","/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+SafeCode.encode("~"+rs.getString("template_id"))+"&subsys_id="+subsys_id+"&isVisible="+isVisible+"&t_type=1");
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
			buf.append(outputter.outputString(myDocument));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs!=null)
				{
					rs.close();
				}
				if(con!=null)
				{
					con.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return buf;
	}
	public boolean isHaveChild(String templatesetid,Connection con,UserView userView)
	{
		boolean flag=false;
		RowSet rs = null;
		try
		{
			String sql ="select template_setid,b0110,scope from per_template_set where parent_id ='"+templatesetid+"'";
//			if(!userView.isAdmin() && !userView.getGroupId().equals("1"))
//			{
//				String unit = KhTemplateBo.getyxb0110(userView, con);
//				sql+=" and ( ((b0110='"+unit+"' or b0110='HJSJ') and scope=1) or scope=0 or scope is null)";
//			}
			ContentDAO dao = new ContentDAO(con);
			
			rs = dao.search(sql);
			while(rs.next())
			{
				String b0110 = (String)rs.getString("b0110");
				int scope = rs.getInt("scope");
 				if(!userView.isAdmin() && !"1".equals(userView.getGroupId()))
 				{
 					if(Integer.valueOf(scope)==null || scope==0){
 						flag = true;
 					}else{
 						if(b0110!=null && !"".equalsIgnoreCase(b0110) && "HJSJ".equalsIgnoreCase(b0110)){
	 						flag=true;
	 					}else{
	 						if(!(b0110.length()>KhTemplateBo.getyxb0110(userView, con).length()?b0110.substring(0, KhTemplateBo.getyxb0110(userView, con).length()):b0110).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, con))){
	 							flag=true;
	 						}
	 					}
 					}	
 				}else{
 					flag=true;
 				}
 				break;
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
				if(rs!=null)
				{
					rs.close();
				}				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}
	public boolean isHaveOtherChild(String templateid,Connection con)
	{
		boolean flag=false;
		RowSet rs = null;
		try
		{
			String sql ="select template_id from per_template where template_setid='"+templateid+"'";
			ContentDAO dao = new ContentDAO(con);
			
			rs = dao.search(sql);
			while(rs.next())
			{
				flag=true;
				break;
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
				if(rs!=null)
				{
					rs.close();
				}				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}

}
