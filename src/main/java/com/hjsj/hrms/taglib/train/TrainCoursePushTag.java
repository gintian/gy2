package com.hjsj.hrms.taglib.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 判断培训课程中的培训项目或培训资料中是否有关联在线课程
 * <p>TrainCoursePushParentTag.java</p>
 * <p>Description:TrainCoursePushParentTag.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2012-05-17 14:48</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeiChao
 */
public class TrainCoursePushTag extends TagSupport {
    private String projectid;//培训项目编码
    private String item;
	public int doStartTag() throws JspException {
		
		//UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		Connection conn=null;
		RowSet rs = null;
		if(projectid !=null && projectid.length() > 0)
		    projectid = PubFunc.decrypt(SafeCode.decode(projectid));
		try{
		   conn=AdminDb.getConnection();
		   if(item==null||item.length()<4)
			   return SKIP_BODY;
		   String[] items = item.split(":");
		   String table = items[0];
		   String field = items[1];
		   String code = items[2];
		   String rankid = ""; 
		   if("r13".equalsIgnoreCase(table))
			   rankid = "r4105";
		   else if("r07".equalsIgnoreCase(table))
			   rankid = "r4114";
		   else if("r41".equalsIgnoreCase(table))
			   rankid = "r4101";
		   else
			   return SKIP_BODY;
		   
		   ContentDAO dao = new ContentDAO(conn);
			String sql = "";
			if ("50".equals(code))
				sql = "select b." + field + " from R41 a left join " + table + " b on b." + table + "01=a." + rankid + " where a.r4101='" + projectid + "'";
			else if ("55".equals(code))
				sql = "select r5000 " + field + " from R41 left join r50 on r41." + field + "=r50.codeitemid where r41.r4101='" + projectid + "'";
			rs = dao.search(sql);
		   Object obj = null;
		   if(rs.next())
			   obj = rs.getObject(field);
		   if(obj==null||obj.toString().length()<1)
			   return SKIP_BODY;
		   else{
			   pageContext.setAttribute("r5000", obj);
			   return EVAL_BODY_INCLUDE;
		   }
		   
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			closeed(conn, rs);
		}
		return SKIP_BODY;
	}
	
	private void closeed(Connection conn,RowSet rs){
		try {
			if(rs!=null)
				rs.close();
			if(conn!=null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getProjectid() {
		return projectid;
	}
	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	
}
