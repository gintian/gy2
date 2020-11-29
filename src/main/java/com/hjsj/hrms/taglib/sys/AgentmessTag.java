package com.hjsj.hrms.taglib.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;

public class AgentmessTag extends BodyTagSupport {
	private String id="";	
	private String b0110="";
	private String e0122="";
	private String e01a1="";
	private String a0101="";
	public int doEndTag() throws JspException 
	{
		if(id==null||id.length()<=0)
		{
			 pageContext.setAttribute(b0110, "");
			 pageContext.setAttribute(e0122, "");
			 pageContext.setAttribute(e01a1, "");
			 pageContext.setAttribute(a0101, "");
			 return SKIP_BODY;
		} 
		Connection conn=null;
		RowSet rs=null;
		try{
			String sql="select a0100,agent_id,nbase,agent_status from agent_set where id="+id;
			conn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(sql);
			String agent_status="";
			String a0100="";
			String nbase="";
			String agent_id="";
			if(rs.next())
			{
				agent_status=rs.getString("agent_status");
				a0100=rs.getString("a0100");
				nbase=rs.getString("nbase");
				agent_id=rs.getString("agent_id");
			}
			if("4".equals(agent_status))
			{
				sql="select a0101,b0110,e0122,e01a1 from "+nbase+"A01 where a0100='"+a0100+"'";
				rs=dao.search(sql);
				if(rs.next())
				{
					String a0101_str=rs.getString("a0101");
					String b0110_str=rs.getString("b0110");
					String e0122_str=rs.getString("e0122");
					String e01a1_str=rs.getString("e01a1");
				    pageContext.setAttribute(a0101, a0101_str);
				    b0110_str = AdminCode.getCodeName("UN", b0110_str);
				    e0122_str = AdminCode.getCodeName("UM", e0122_str);
				    e01a1_str = AdminCode.getCodeName("@K", e01a1_str);
				    pageContext.setAttribute(b0110, b0110_str);
				    pageContext.setAttribute(e0122, e0122_str);
				    pageContext.setAttribute(e01a1, e01a1_str);
				}		
				return SKIP_BODY;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try{
			 if (conn != null)
	             conn.close();
			 if(rs!=null)
				 rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}   
		}
		
		return SKIP_BODY;
	}	
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public String getE0122() {
		return e0122;
	}
	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}
	public String getE01a1() {
		return e01a1;
	}
	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}
	public String getA0101() {
		return a0101;
	}
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
