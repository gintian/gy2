package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;

public class UserState extends BodyTagSupport {
	private String tablename;
	private String name;
	private String dbname;
	private String userid;
	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		
		try
		{
			String dbname=this.getDbname();
			String userid=this.getUserid();
			String name=this.getName();
			String sqlstr = "";
			if(!this.getTablename().startsWith("A")){
				sqlstr = "select * from "+this.getTablename()+" where a0100='"+userid+"'" ;
			} else {
				if (StringUtils.isNotBlank(dbname)) {
				    sqlstr = "select * from "+dbname+this.getTablename()+" where a0100='"+userid+"'" ;
				}
			}

            ArrayList mylist = null;
			if (StringUtils.isNotBlank(sqlstr)) {
			    mylist = (ArrayList) ExecuteSQL.executeMyQuery(sqlstr);
			}

			strhtml.append("<input type='hidden' name='"+name+"' value='");
			if("A01".equals(this.getTablename())){
				if(mylist != null && mylist.size() > 0){
					DynaBean dynabean=(DynaBean) mylist.get(0);
					String reltable=(String)dynabean.get("state");
					strhtml.append(reltable);
					strhtml.append("'/>");
				}else{
					strhtml.append("'/>");
				}
			}else{
				strhtml.append("0");
				strhtml.append("'/>");
			}
			pageContext.getOut().println(strhtml.toString());
			return SKIP_BODY;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return SKIP_BODY;			
		}
	}

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	

}
