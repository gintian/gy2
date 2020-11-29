package com.hjsj.hrms.taglib.kq;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 通过人员编号，人员库，考勤日期得到考勤班次
 * @author Owner
 *
 */
public class KqClassName2  extends BodyTagSupport {
	private String nbase;
	private String a0100;
	private String workdate;
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getWorkdate() {
		return workdate;
	}
	public void setWorkdate(String workdate) {
		this.workdate = workdate;
	}
	public int doEndTag() throws JspException 
	{
		Connection conn=null;		
		RowSet rs =null;
		try{
			conn=AdminDb.getConnection();
			StringBuffer sql=new StringBuffer();
			if(this.a0100==null||this.a0100.length()<=0)
				return SKIP_BODY;
			if(this.nbase==null||this.nbase.length()<=0)
				return SKIP_BODY;
			if(this.workdate==null||this.workdate.length()<=0)
				return SKIP_BODY;
			String classid="";
			ContentDAO dao=new ContentDAO(conn);
			sql.append("select class_id from kq_employ_shift where ");
			sql.append(" a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+workdate+"'");
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				classid=rs.getString("class_id");
			}else
			{
				pageContext.getOut().println("未排班");
				return SKIP_BODY;
			}
			sql.delete(0, sql.length());
			NetSignIn netSignIn=new NetSignIn();
			String columns=netSignIn.kqClassShiftColumns();
			sql.append("select "+columns+" from kq_class where class_id='"+classid+"'");
			
			String name="";
			rs=dao.search(sql.toString());
			StringBuffer buf=new StringBuffer();
			String calss_id="";
			if(rs.next())
			{
				calss_id=rs.getString("class_id");
				buf.append(rs.getString("name"));
				buf.append("");
				buf.append(rs.getString("onduty_1")!=null&&rs.getString("onduty_1").length()>0?"&nbsp;&nbsp;"+rs.getString("onduty_1"):"");
				String off=netSignIn.getOffduty(rs);
				buf.append(off!=null&&off.length()>0?"~"+off:"");
			}
			pageContext.getOut().println(buf.toString());
			pageContext.getOut().println("<html:hidden styleId=\"class_"+a0100+"\" name=\"class_"+a0100+"\"  value=\""+calss_id+"\"/>");
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    
			try{
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return SKIP_BODY;	
	}

}
