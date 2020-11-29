package com.hjsj.hrms.interfaces.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;

/**
 * 给考核指标和考核模板授权页面
 * <p>Title:PrivTemplateAndField.java</p>
 * <p>Description>:PrivTemplateAndField.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-10-28 下午02:58:50</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class PrivTemplateAndField {

	/**=23 指标,=22模板*/
	private String res_flag;
	private String rootdesc="";
	private String parent_id="";
	/**不区分子系统*/
	private String sybsys_id="";
	private UserView view=null;
	private String level="";
	public  PrivTemplateAndField(String res_flag,String parent_id,UserView view,String level)
	{
		this.res_flag=res_flag;
		//this.rootdesc=rootdesc;
		this.parent_id=parent_id;
		this.view=view;
		this.level=level;
		//this.sybsys_id=subsys_id;
	}
	public String GetXMLString() throws GeneralException
	{
		//  生成的XML文件
	 		StringBuffer xmls = new StringBuffer();
	 		// 创建xml文件的根元素
	 		Element root = new Element("TreeNode");
	 		// 设置根元素属性
	 		root.setAttribute("id", "00");
	 		root.setAttribute("text", "root");
	 		root.setAttribute("title", "organization");
	 		// 创建xml文档自身
	 		Document myDocument = new Document(root);
	 		StringBuffer sql = new StringBuffer("");
	 		Connection con=null;
			RowSet rs = null;
			/**模板*/
	 		if("22".equals(res_flag))
	 		{
	 			if("0".equals(this.level))
	 			{
	 				Element child = new Element("TreeNode");
					child.setAttribute("id","-1");
					child.setAttribute("text","绩效模板");
					child.setAttribute("title","绩效模板");
		     		child.setAttribute("xml","/performance/kh_system/kh_template/priv_tree.jsp?parent_id=-1&level=33&res_flag="+this.res_flag);
		     		child.setAttribute("icon","/images/group.gif");
		     		root.addContent(child);
				    child = new Element("TreeNode");
					child.setAttribute("id","-1");
					child.setAttribute("text","培训模板");
					child.setAttribute("title","培训模板");
		     		child.setAttribute("xml","/performance/kh_system/kh_template/priv_tree.jsp?parent_id=-1&level=20&res_flag="+this.res_flag);
		     		child.setAttribute("icon","/images/group.gif");
		     		root.addContent(child);
					XMLOutputter outputter = new XMLOutputter();
					// 格式化输出类
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					// 将生成的XML文件作为字符串形式
					xmls.append(outputter.outputString(myDocument));
			 		return xmls.toString();
	 			}
	 			if("-1".equals(parent_id))
	 			{
	 				sql.append(" select * from per_template_set where parent_id is null and subsys_id='"+this.level+"' order by template_setid");
	 			}
	 			else
	 			{
	 				sql.append(" select * from per_template_set where UPPER(parent_id)='"+parent_id.toUpperCase()+"' and subsys_id='"+this.level+"' order by template_setid");
	 			}
	 			try
	 			{
	 	     		con=(Connection)AdminDb.getConnection();
		    		ContentDAO dao = new ContentDAO(con);
		    		rs = dao.search(sql.toString());
		    		while(rs.next())
		    		{
		    			if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
		    			{
		        			if(!view.isHaveResource(IResourceConstant.KH_MODULE,rs.getString("template_setid")))
		        			{
		        				continue;
		        			}
		        		}
		    			Element child = new Element("TreeNode");
						child.setAttribute("id",rs.getString("template_setid"));
						child.setAttribute("text",rs.getString("name"));
						child.setAttribute("title",rs.getString("name"));
						//String child_id=rs.getString("child_id")!=null?rs.getString("child_id"):"";
	 					//if(child_id.length()>0)
			     		child.setAttribute("xml","/performance/kh_system/kh_template/priv_tree.jsp?parent_id="+rs.getInt("template_setid")+"&res_flag="+this.res_flag+"&level="+this.level);
			     	    //child.setAttribute("target","mil_body");

						//child.setAttribute("href","/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid=-1&subsys_id="+subsys_id+"&isVisible="+isVisible+"&t_type=0");
						/**有效和无效的图标不同*/
			     	   if(rs.getInt("validflag")==1)
					    	child.setAttribute("icon","/images/open1.png");
						else
							child.setAttribute("icon","/images/open.png");
						root.addContent(child);
		    		}
		    		sql.setLength(0);
		    		sql.append("select * from per_template where UPPER(template_setid)='"+parent_id.toUpperCase()+"' order by seq");
		    		rs=dao.search(sql.toString());
		    		while(rs.next())
		    		{
		    			if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
		    			{
		        			if(!view.isHaveResource(IResourceConstant.KH_MODULE,rs.getString("template_id")))
		        			{
		        				continue;
		        			}
		        		}
		    			Element child = new Element("TreeNode");
		    			child.setAttribute("id",rs.getString("template_id"));
				    	child.setAttribute("text","["+rs.getString("template_id")+"]"+rs.getString("name"));
				    	child.setAttribute("title","["+rs.getString("template_id")+"]"+rs.getString("name"));
				    	
			    		child.setAttribute("target","mil_body");

			    		//child.setAttribute("href","/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+rs.getString("template_id")+"&subsys_id="+subsys_id+"&isVisible="+isVisible+"&t_type=1");
				    	if(rs.getInt("validflag")==1)
				         	child.setAttribute("icon","/images/lock_co_1.gif");
				    	else
				    		child.setAttribute("icon","/images/lock_co.gif");
				    	root.addContent(child);
		    		}
	 			}
	 			catch(Exception e)
	 			{
	 				e.printStackTrace();
	 			}
	 		}
	 		/**指标*/
	 		else if("23".equals(res_flag))
	 		{
	 			if("0".equals(this.level))
	 			{
	 				Element child = new Element("TreeNode");
					child.setAttribute("id","-1");
					child.setAttribute("text","绩效指标");
					child.setAttribute("title","绩效指标");
		     		child.setAttribute("xml","/performance/kh_system/kh_template/priv_tree.jsp?parent_id=-1&level=33&res_flag="+this.res_flag);
		     		child.setAttribute("icon","/images/group.gif");
		     		root.addContent(child);
				    child = new Element("TreeNode");
					child.setAttribute("id","-1");
					child.setAttribute("text","培训指标");
					child.setAttribute("title","培训指标");
		     		child.setAttribute("xml","/performance/kh_system/kh_template/priv_tree.jsp?parent_id=-1&level=20&res_flag="+this.res_flag);
		     		child.setAttribute("icon","/images/group.gif");
		     		root.addContent(child);
					XMLOutputter outputter = new XMLOutputter();
					// 格式化输出类
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					// 将生成的XML文件作为字符串形式
					xmls.append(outputter.outputString(myDocument));
			 		return xmls.toString();
	 			}
	 			if("-1".equals(parent_id))
	 			{
	 				sql.append("select * from per_pointset  where parent_id is null and subsys_id='"+this.level+"' order by seq");
	 			}
	 			else
	 			{
	 				sql.append("select * from per_pointset  where UPPER(parent_id)='"+parent_id.toUpperCase()+"' and subsys_id='"+this.level+"' order by seq");
	 			}
	 			try
	 			{
	     			con=(Connection)AdminDb.getConnection();
	        		ContentDAO dao = new ContentDAO(con);
	         		rs = dao.search(sql.toString());
	         		 while(rs.next())
	         		 {
	         			/*lazyDynaBean.set("codeitemid",rs.getString("pointsetid"));
	 					lazyDynaBean.set("codeitemdesc",rs.getString("pointsetname"));*/
	         			if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
		    			{
		        			if(!view.isHaveResource(IResourceConstant.KH_FIELD,rs.getString("pointsetid")))
		        			{
		        				continue;
		        			}
		        		}
	         			Element child = new Element("TreeNode");
						child.setAttribute("id",rs.getString("pointsetid"));
						child.setAttribute("text",rs.getString("pointsetname"));
						child.setAttribute("title",rs.getString("pointsetname"));
						//String child_id=rs.getString("child_id")!=null?rs.getString("child_id"):"";
	 					
			     		child.setAttribute("xml","/performance/kh_system/kh_template/priv_tree.jsp?parent_id="+rs.getInt("pointsetid")+"&res_flag="+this.res_flag+"&level="+this.level);
	 				    if(rs.getInt("validflag")==1)
	 				    	child.setAttribute("icon","/images/open1.png");
						else
							child.setAttribute("icon","/images/open.png");
						root.addContent(child);
	         		 }
	         		 sql.setLength(0);
	         		 sql.append("select * from per_point where UPPER(pointsetid)='"+parent_id.toUpperCase()+"'  order by seq");
	         		rs = dao.search(sql.toString());
	         		while(rs.next())
	         		{
	         			/*lazyDynaBean.set("codeitemid",rs.getString("point_id"));
	 					lazyDynaBean.set("codeitemdesc",rs.getString("pointname"));*/
	         			if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
		    			{
		        			if(!view.isHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id")))
		        			{
		        				continue;
		        			}
		        		}
	         			Element child = new Element("TreeNode");
						child.setAttribute("id",rs.getString("point_id"));
						child.setAttribute("text",rs.getString("pointname"));
						child.setAttribute("title",rs.getString("pointname"));
						if(rs.getInt("validflag")==1)
		 				    child.setAttribute("icon","/images/lock_co_1.gif");
						else
							child.setAttribute("icon","/images/lock_co.gif");
						root.addContent(child);
	         		}
	 			}
	 			catch(Exception e)
	 			{
	 				e.printStackTrace();
	 			}
	 			finally
	 			{
	 				if(rs!=null)
	 				{
	 					try
	 					{
	 						rs.close();
	 					}
	 					catch(Exception e)
	 					{
	 						e.printStackTrace();
	 					}
	 				}
	 				if(con!=null)
	 				{
	 					try
	 					{
	 						con.close();
	 					}catch(Exception e)
	 					{
	 						e.printStackTrace();
	 					}
	 				}
	 			}
	 		}
	 		XMLOutputter outputter = new XMLOutputter();
			// 格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			// 将生成的XML文件作为字符串形式
			xmls.append(outputter.outputString(myDocument));
	 		return xmls.toString();
	}
}
