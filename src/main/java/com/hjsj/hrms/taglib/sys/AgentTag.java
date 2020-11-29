package com.hjsj.hrms.taglib.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AgentTag extends BodyTagSupport {
	private String curAgenter;
	public int doEndTag() throws JspException{
	    Connection conn=null;
	    RowSet rs=null;
		try{	
			UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			curAgenter=(String)pageContext.getSession().getAttribute("curAgenter");
			curAgenter=curAgenter!=null&&curAgenter.length()>0?curAgenter:"";	
			String isAgenter=(String)pageContext.getSession().getAttribute("isAgenter");
			pageContext.getSession().removeAttribute("isAgenter");
			pageContext.getSession().removeAttribute("curAgenter");
			String agent_id="";//(String)pageContext.getSession().getAttribute("agent_id");
			String agent_fullname="";//(String)pageContext.getSession().getAttribute("agent_fullname");
			if(isAgenter!=null&& "yes".equals(isAgenter)){
				agent_id=(String)pageContext.getSession().getAttribute("agent_id");
				agent_fullname=(String)pageContext.getSession().getAttribute("agent_fullname");
			}
			//v7界面代理人办理下拉选项同显示时间共用一个问题，当即配置显示时间和显示代理人下来框优先显示代理人下拉框
			String view_time=SystemConfig.getPropertyValue("banner_viewTime");
			if(agent_id==null||agent_id.length()<=0){
				if(userview.getA0100()!=null&&userview.getA0100().length()>0&&userview.getDbname()!=null&&userview.getDbname().length()>0)
				{
					agent_id=userview.getDbname()+userview.getA0100()+"`-1";
					pageContext.getSession().setAttribute("agent_id", agent_id);				
					agent_fullname=userview.getUserFullName();
					pageContext.getSession().setAttribute("agent_fullname", agent_fullname);
				}else {
					if("hcm".equals(userview.getBosflag())){
						JspWriter out=pageContext.getOut();
						if(view_time!=null&& "true".equals(view_time)){
							out.append("<INPUT id=\"t\" name=\"time\" type=\"text\" size=\"27\" readonly=\"readonly\">");
						}else{
							out.append("<INPUT id=\"t2\" name=\"time\" type=\"text\" size=\"20\" readonly=\"readonly\">");
						}
					}
					return SKIP_BODY;
				}
					
			}
			conn = AdminDb.getConnection();			
			ArrayList list=new ArrayList();
			
			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");				
				String date=sdf.format(new Date());
				String agent_Arr[]=agent_id.split("`");
				String agent_idd=agent_Arr[0];
				String a0100=agent_idd.substring(3);
				String nbase=agent_idd.substring(0,3);
				StringBuffer sql=new StringBuffer();
				sql.append("select * from agent_set  where a0100='"+a0100+"'");
				sql.append(" and nbase='"+nbase+"'");
				sql.append(" and "+Sql_switcher.dateValue(date)+" between start_date and end_date ");
				ContentDAO dao=new ContentDAO(conn);
				rs=dao.search(sql.toString());
				
				while(rs.next())
				{
					CommonData da=new CommonData();					
					String name=rs.getString("principal_fullname");
					da.setDataName(name);
					da.setDataValue(rs.getString("principal_id")+"`"+rs.getString("id"));//委托人id
					list.add(da);
				}
			
			if(list!=null&&list.size()>0)
			{    String selected="";
				 JspWriter out=pageContext.getOut();
				 if("hcm".equals(userview.getBosflag())){
					 out.append("<div  style=\"position:absolute;top:-6px;left:-5px;z-index:10000;\">");
				 }
				 out.append("<select name=\"agent\" size=\"1\" onchange=\"change_agent(this);\">");
				 if(curAgenter!=null&&curAgenter.equals(agent_id))
					 selected="selected";
				 out.append("<option value=\""+agent_id+"\" "+selected+">(本人)"+agent_fullname+"</option>");
				 selected="";
                 for(int i=0;i<list.size();i++)
                 {
                	 CommonData da=(CommonData)list.get(i);
                	 if(curAgenter!=null&&curAgenter.equals(da.getDataValue()))
    					 selected="selected";
                	 out.append("<option value=\""+da.getDataValue()+"\" "+selected+">"+da.getDataName()+"</option>");
                	 selected="";
                 }
				 out.append("</select>");
				 if("hcm".equals(userview.getBosflag())){
					 out.append("</div>");
				 }
			}else{
				if("hcm".equals(userview.getBosflag())){
					JspWriter out=pageContext.getOut();
					if(view_time!=null&& "true".equals(view_time)){
						out.append("<INPUT id=\"t\" name=\"time\" type=\"text\" size=\"27\" readonly=\"readonly\" >");
					}else{
						out.append("<INPUT id=\"t2\" name=\"time\" type=\"text\" size=\"20\" readonly=\"readonly\">");
					}
				}
			}
			
		}catch(Exception e)
		{
			
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		try{		
    			if (conn != null){
    				conn.close();
    			}
    		}catch (SQLException sql){
    			sql.printStackTrace();
    		}
    	}
		return SKIP_BODY;
	}
	public String getCurAgenter() {
		return curAgenter;
	}
	public void setCurAgenter(String curAgenter) {
		this.curAgenter = curAgenter;
	}
    private String getAgentName(String nbase,String a0100,ContentDAO dao)
    {
    	String sql="select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";
    	RowSet rs1=null;
    	String name="";
    	try
    	{
    		rs1=dao.search(sql);
    		if(rs1.next())
    		{
    			name=rs1.getString("a0101");
    		}
    	}catch(Exception e)
    	{
    		
    	}finally{
    		if(rs1!=null)
				try {
					rs1.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	return name;
    	
    }
}
