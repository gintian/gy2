package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;

/**
 * <p>Title:FieldTemplateTreeServlet.java</p>
 * <p>Description>:FieldTemplateTreeServlet.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 18, 2009 4:19:23 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei/system/fieldortemplateservlet
 */

public class FieldTemplateTreeServlet extends HttpServlet
{
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException 
	{
		doPost(req, resp);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException 
	{
		UserView userView = (UserView) req.getSession().getAttribute(WebConstant.userView);	
		String res_flag=req.getParameter("res_flag");
		String flag=req.getParameter("flag");
		String roleid=req.getParameter("roleid");
		String node=req.getParameter("node");
		String JSONTree=this.getJSONTree(node,res_flag, flag, roleid, userView);
		resp.setHeader("cache-control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        resp.setContentType("text/html;charset=UTF-8");
		resp.getWriter().write(JSONTree);
		resp.getWriter().close();
	}
	
	private String getJSONTree(String node,String res_flag,String flag,String roleid,UserView view)
	{
		StringBuffer buf = new StringBuffer();
		Connection con=null;
		try
		{ 
			/**模板*/
			if("22".equals(res_flag))
			{
				con=(Connection)AdminDb.getConnection();
				SysPrivBo privbo=new SysPrivBo(roleid,flag,con,"warnpriv");
				String res_str=privbo.getWarn_str();
				int res_type=Integer.parseInt(res_flag);
				ResourceParser parser=new ResourceParser(res_str,res_type);
				/**1,2,3*/
				String str_content=","+parser.getContent()+",";
				StringBuffer temp=new StringBuffer();
				if("root".equalsIgnoreCase(node))
				{
			    	temp.append("{id:'-1',text:'绩效模板',");
			    	if(str_content.indexOf(",-1,")!=-1)
			    		temp.append("checked:true,");
			    	else
			     		temp.append("checked:false,");		    		
		 	    	temp.append("icon:'/images/spread_all.gif',");
			    	String child=this.getChildJSONTree(view, "-1", res_flag, con, str_content, "33");
			    	if(child.length()>0)
			    	{
				    	temp.append("children:");
			     		temp.append(child);
			    	}
			    	else
			    		temp.append("leaf:true");		    		
			    	temp.append("},");
			    				    	
			    	
			    	temp.append("{id:'-0',text:'培训模板',");
			    	if(str_content.indexOf(",-0,")!=-1)
			    		temp.append("checked:true,");
			    	else
			    		temp.append("checked:false,");			    	
			    	temp.append("icon:'/images/spread_all.gif',");
			    	String child2=this.getChildJSONTree(view, "-0", res_flag, con, str_content, "20");
			    	if(child2.length()>0)
			    	{
			    		temp.append("children:");
			    		temp.append(child2);
			    	}
			    	else
			    		temp.append("leaf:true");
			    	temp.append("},");
			    	
			    	
			    	temp.append("{id:'-4',text:'能力素质模板',");
			    	if(str_content.indexOf(",-4,")!=-1)
			    		temp.append("checked:true,");
			    	else
			    		temp.append("checked:false,");			    	
			    	temp.append("icon:'/images/spread_all.gif',");
			    	String child3=this.getChildJSONTree(view, "-4", res_flag, con, str_content, "35");
			    	if(child3.length()>0)
			    	{
			    		temp.append("children:");
			    		temp.append(child3);
			    	}
			    	else
			    		temp.append("leaf:true");
			    	temp.append("},");
			    	
			    	
			    	if(temp.length()>0)
			    	{
			    		temp.setLength(temp.length()-1);
			    		buf.append("["+temp+"]");
			    	}
				}
				else
				{
					String child2=this.getChildJSONTree(view, node, res_flag, con, str_content, "0");
					buf.append(child2);
				}
			}
			/**指标*/
			else if("23".equals(res_flag))
			{
				con=(Connection)AdminDb.getConnection();
				SysPrivBo privbo=new SysPrivBo(roleid,flag,con,"warnpriv");
				String res_str=privbo.getWarn_str();
				int res_type=Integer.parseInt(res_flag);
				ResourceParser parser=new ResourceParser(res_str,res_type);
				/**1,2,3*/
				//parser.reSetContent(res_str);
				//String str_content=","+parser.outResourceContent()+",";
				String str_content=","+parser.getContent()+",";
				StringBuffer temp=new StringBuffer();
				if("root".equalsIgnoreCase(node))
				{
		    		temp.append("{id:'-2',text:'绩效指标',");
		     		if(str_content.indexOf(",-2,")!=-1)
		    			temp.append("checked:true,");
		    		else
		    			temp.append("checked:false,");
		    		temp.append("icon:'/images/spread_all.gif',");
		    		String child=this.getChildJSONTree(view, "-2", res_flag, con, str_content, "33");
		    		if(child.length()>0)
		    		{
		    			temp.append("children:");
		    			temp.append(child);
	    			}
		    		else
	    				temp.append("leaf:true");
		    		temp.append("},");
		    		
		    		
	    			temp.append("{id:'-3',text:'培训指标',");
	    			if(str_content.indexOf(",-3,")!=-1)
	    				temp.append("checked:true,");
		    		else
		    			temp.append("checked:false,");
		    		temp.append("icon:'/images/spread_all.gif',");
		    		String child2=this.getChildJSONTree(view, "-3", res_flag, con, str_content, "20");
		    		if(child2.length()>0)
		    		{
		    			temp.append("children:");
		     			temp.append(child2);
		    		}
		    		else
			    		temp.append("leaf:true");
		    		temp.append("},");
		    		
		    		
		    		temp.append("{id:'-5',text:'能力素质指标',");
			    	if(str_content.indexOf(",-5,")!=-1)
			    		temp.append("checked:true,");
			    	else
			    		temp.append("checked:false,");			    	
			    	temp.append("icon:'/images/spread_all.gif',");
			    	String child3=this.getChildJSONTree(view, "-5", res_flag, con, str_content, "35");
			    	if(child3.length()>0)
			    	{
			    		temp.append("children:");
			    		temp.append(child3);
			    	}
			    	else
			    		temp.append("leaf:true");
			    	temp.append("},");			    	
		    		
		    		if(temp.length()>0)
			    	{
		     			temp.setLength(temp.length()-1);
		    			buf.append("["+temp+"]");
		    		}
				}
				else
				{
					String child2=this.getChildJSONTree(view, node, res_flag, con, str_content, "0");
					buf.append(child2);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(con!=null)
			{
				try
				{
					if (con != null) {
						con.close();
					}
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
			}
		}
		return buf.toString();
	}
	private String getChildJSONTree(UserView view,String parent_id,String res_flag,Connection con,String str_content,String subsys_id)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			/**模板*/
			if("22".equals(res_flag))
			{
				String sql="select template_setid,name,validflag,child_id from per_template_set where 1=1 ";
				if(!"0".equals(subsys_id))
					sql+=" and subsys_id='"+subsys_id+"'";
				if("-1".equals(parent_id) || "-0".equals(parent_id) || "-4".equals(parent_id))
				{
					sql+=" and parent_id is null";
				}
				else
				{
					sql+=" and parent_id='"+parent_id+"'";
				}
				sql+=" order by template_setid";
				RowSet rs = null;
				ContentDAO dao = new ContentDAO(con);
				rs = dao.search(sql);
				StringBuffer temp = new StringBuffer("");
				while(rs.next())
				{
					String template_setid=rs.getString("template_setid");
					if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
					{
						if(!view.isHaveResource(IResourceConstant.KH_MODULE, template_setid))
						{
							continue;
						}
					}
					String name=rs.getString("name").replaceAll("'", "\"").replaceAll("‘", "\"").replaceAll("’", "\"");
					String validflag=rs.getString("validflag");
					temp.append("{id:'"+template_setid+"',");
					temp.append("text:'"+name+"',");
					if(str_content.indexOf(","+template_setid+",")!=-1)
					{
						temp.append("checked:true,");
					}
					else
					{
						temp.append("checked:false,");
					}
					if("1".equals(validflag))
					{
						temp.append("icon:'/images/open1.png',");
					}
					else
					{
						temp.append("icon:'/images/open.png',");
					}
					String setchild=rs.getString("child_id");
					//String fieldchild=this.getPointOrTemplate(res_flag, template_setid, con, str_content, view);
				    boolean flag=this.isHavePointOrTemplate(res_flag, template_setid, con);
					if((setchild==null|| "".equals(setchild))&&!flag)
				    	temp.append("leaf:true");
				    else
				    {
				    	temp.append("leaf:false");
				    }
					temp.append("},");
					
				}
				String fieldchild=this.getPointOrTemplate(res_flag, parent_id, con, str_content, view);
				temp.append(fieldchild);
				if(temp.length()>0)
				{
					temp.setLength(temp.length()-1);
					buf.append("["+temp+"]");
				}
			}
			/**指标*/
			else if("23".equals(res_flag))
			{
				String sql="select pointsetid,pointsetname,validflag,child_id from per_pointset where 1=1 ";
				if(!"0".equals(subsys_id))
					sql+=" and subsys_id='"+subsys_id+"'";
				if("-2".equals(parent_id) || "-3".equals(parent_id) || "-5".equals(parent_id))
				{
					sql+=" and parent_id is null";
				}
				else
				{
					sql+=" and parent_id='"+parent_id+"'";
				}
				sql+=" order by seq";
				RowSet rs = null;
				ContentDAO dao = new ContentDAO(con);
				rs = dao.search(sql);
				StringBuffer temp = new StringBuffer("");
				while(rs.next())
				{
					String pointsetid=rs.getString("pointsetid");
					if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
					{
						if(!view.isHaveResource(IResourceConstant.KH_FIELD, pointsetid))
						{
							continue;
						}
					}
					String pointsetname=rs.getString("pointsetname").replaceAll("'", "\"").replaceAll("‘", "\"").replaceAll("’", "\"");
					String validflag=rs.getString("validflag");
					temp.append("{id:'"+pointsetid+"',");
					temp.append("text:'"+pointsetname+"',");
					if(str_content.indexOf(","+pointsetid+",")!=-1)
					{
						temp.append("checked:true,");
					}
					else
					{
						temp.append("checked:false,");
					}
					if(validflag!=null&& "1".equals(validflag))
					{
						temp.append("icon:'/images/open1.png',");
					}
					else
					{
						temp.append("icon:'/images/open.png',");
					}
					String setchild=rs.getString("child_id");//this.getChildJSONTree(view, pointsetid, res_flag, con, str_content, subsys_id);
					//String fieldchild=this.getPointOrTemplate(res_flag, pointsetid, con, str_content, view);
				    boolean flag=this.isHavePointOrTemplate(res_flag, pointsetid, con);
				    if((setchild==null|| "".equals(setchild))&&!flag)
				    	temp.append("leaf:true");
				    else
				    {
				    	temp.append("leaf:false");
				    }
					temp.append("},");
				}
				
				String fieldchild=this.getPointOrTemplate(res_flag, parent_id, con, str_content, view);
				temp.append(fieldchild);
				if(temp.length()>0)
				{
					temp.setLength(temp.length()-1);
					buf.append("["+temp+"]");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 得到指标或者模板
	 * @param res_falg
	 * @param setid
	 * @param con
	 * @return
	 */
	private String getPointOrTemplate(String res_flag,String setid,Connection con,String str_content,UserView view)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			StringBuffer sql = new StringBuffer();
			/**模板*/
			if("22".equals(res_flag))
			{
				sql.append("select template_id,name,validflag from per_template where template_setid='"+setid+"' order by seq");
				RowSet rs = null;
				ContentDAO dao = new ContentDAO(con);
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
					{
						if(!view.isHaveResource(IResourceConstant.KH_MODULE, rs.getString("template_id")))
						{
							continue;
						}
					}
					buf.append("{id:'"+rs.getString("template_id")+"',");
					buf.append("text:'"+rs.getString("name").replaceAll("'", "\"").replaceAll("‘", "\"").replaceAll("’", "\"")+"',");
					if(str_content.indexOf(","+rs.getString("template_id")+",")!=-1)
					{
						buf.append("checked:true,");
					}
					else
					{
						buf.append("checked:false,");		
					}
					if("1".equals(rs.getString("validflag")))
					{
						buf.append("icon:'/images/lock_co_1.gif',");
					}
					else
					{
						buf.append("icon:'/images/lock_co.gif',");
					}
					buf.append("leaf:true},");
				}
			}
			else if("23".equals(res_flag))
			{
				sql.append("select point_id,pointname,validflag from per_point where pointsetid='"+setid+"' order by seq");
				RowSet rs = null;
				ContentDAO dao = new ContentDAO(con);
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					if(!view.isSuper_admin()&&!"1".equals(view.getGroupId()))
					{
						if(!view.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
						{
							continue;
						}
					}
					buf.append("{id:'"+rs.getString("point_id")+"',");
					buf.append("text:'"+rs.getString("pointname").replaceAll("'", "\"").replaceAll("‘", "\"").replaceAll("’", "\"")+"',");
					if(str_content.indexOf(","+rs.getString("point_id")+",")!=-1)
					{
						buf.append("checked:true,");
					}
					else
					{
						buf.append("checked:false,");		
					}
					if("1".equals(rs.getString("validflag")))
					{
						buf.append("icon:'/images/lock_co_1.gif',");
					}
					else
					{
						buf.append("icon:'/images/lock_co.gif',");
					}
					buf.append("leaf:true},");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 判断是否有模板或者指标
	 * @param res_flag
	 * @param setid
	 * @param con
	 * @return
	 */
	private boolean isHavePointOrTemplate(String res_flag,String setid,Connection con)
	{
		boolean flag=false;
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			if("22".equals(res_flag))
			{
				sql.append("select template_id,name,validflag from per_template where template_setid='"+setid+"' order by seq");
				ContentDAO dao = new ContentDAO(con);
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					flag=true;
					break;
				}
				if(!flag)
				{
					sql.setLength(0);
					sql.append("select template_setid from per_template_set where parent_id='"+setid+"'");
					rs=dao.search(sql.toString());
					while(rs.next())
					{
						flag=true;
						break;
					}
				}
			}else if("23".equals(res_flag))
			{
				sql.append("select point_id,pointname,validflag from per_point where pointsetid='"+setid+"' order by seq");
				ContentDAO dao = new ContentDAO(con);
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					flag=true;
					break;
				}
				if(!flag)
				{
					sql.setLength(0);
					sql.append("select pointsetid from per_pointset where parent_id='"+setid+"'");
					rs=dao.search(sql.toString());
					while(rs.next())
					{
						flag=true;
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}
}
