package com.hjsj.hrms.taglib;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtendsCodeToName extends TagSupport {

	private String codeid;
	private String codeitem;
	private String scope;
	private String name;
	private String codevalue;
	private int uplevel = 0;

	public int getUplevel() {
		return this.uplevel;
	}

	public void setUplevel(int uplevel) {
		this.uplevel = uplevel;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

	public int doStartTag() throws JspException {
		CodeItem item = null;
		Connection conn = null;
		ResultSet rs=null;
		try {
			if (this.scope == null) {
				this.scope = "session";
			}
			Object value = TagUtils.getInstance().lookup(this.pageContext,
					this.name, this.codevalue, this.scope);

			if ((value != null) && (value instanceof String)) {
				if ("UN".equalsIgnoreCase(this.codeid)) {
					String sql="select parentid,codesetid,codeitemid,codeitemdesc from organization where codeitemid='"+value+"'";
					conn= AdminDb.getConnection();
					ContentDAO dao = new ContentDAO(conn);
					rs = dao.search(sql);
					if(rs.next()){
						if(!"UN".equalsIgnoreCase(rs.getString("codesetid"))){
							String pre=rs.getString("codesetid");
							String code=rs.getString("parentid");
							int i=0;
							StringBuffer strsql= new StringBuffer();
							while(!"UN".equalsIgnoreCase(pre)&&i<10)
							{
								strsql.delete(0,strsql.length());
								strsql.append("select codesetid,parentid,codeitemdesc from organization");
								strsql.append(" where codeitemid='");
								strsql.append(code);
								strsql.append("'");					
								rs=dao.search(strsql.toString());	//执行当前查询的sql语句	
								if(rs.next())
								{
									value=code;
									pre=rs.getString("codesetid");
									code=rs.getString("parentid");
								}	
								i++;
							}
						}
					}
				}
				if (("UM".equalsIgnoreCase(this.codeid)) && (this.uplevel > 0)) {
					item = AdminCode.getCode(this.codeid, ((String) value)
							.trim(), this.uplevel);
				} else if (this.uplevel > 0)
					item = AdminCode.getCode(this.codeid, ((String) value)
							.trim(), this.uplevel);
				else
					item = AdminCode.getCode(this.codeid, ((String) value)
							.trim());

			} else {
				item = null;
			}

			if (item == null) {
				item = new CodeItem();
			}
			this.pageContext.setAttribute(this.codeitem, item);
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally{
			
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return super.doStartTag();
	}

	public String getCodeid() {
		return this.codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getCodeitem() {
		return this.codeitem;
	}

	public void setCodeitem(String codeitem) {
		this.codeitem = codeitem;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getCodevalue() {
		return this.codevalue;
	}

	public void setCodevalue(String codevalue) {
		this.codevalue = codevalue;
	}

}
