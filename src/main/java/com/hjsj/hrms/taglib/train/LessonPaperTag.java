/**
 * 
 */
package com.hjsj.hrms.taglib.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
public class LessonPaperTag extends TagSupport {
	private String r5300;
	public int doStartTag() throws JspException {
		Connection conn = null;
		RowSet rs = null;
		try
		{
			conn = com.hrms.frame.utility.AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select r5003 from tr_lesson_paper t left join r50 r on t.r5000=r.r5000 where t.r5300="+r5300);
			StringBuffer sb = new StringBuffer();
			while(rs.next()){
				String tmp = PubFunc.nullToStr(rs.getString("r5003"));
				if(tmp.trim().length()>0)
					sb.append(","+tmp);
			}
			if(sb!=null&&sb.length()>0){
				String r5003 = sb.substring(1).trim();
				String showInfo = r5003;
				if(r5003.getBytes().length>50){
					showInfo = "<div style=\"display:inline;\" onmouseover=\"outContent('" + r5003 + "');\" onmouseout=\"UnTip();\">" 
						  + com.hjsj.hrms.utils.PubFunc.splitString(r5003, 50)
						  + "...</div>" ;
				}
			    pageContext.getOut().println(showInfo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return super.doStartTag();
	}
	public String getR5300() {
		return r5300;
	}
	public void setR5300(String r5300) {
		this.r5300 = r5300;
	}

}
