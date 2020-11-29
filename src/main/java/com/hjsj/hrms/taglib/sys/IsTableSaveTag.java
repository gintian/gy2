package com.hjsj.hrms.taglib.sys;

import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
/**
 * 判断数据表是否存在
 * @author Owner
 *
 */
public class IsTableSaveTag extends BodyTagSupport{
    private String tablename="";
	public int doStartTag() throws JspException {
		//if(this.flag!=null&&this.flag.equals("0"))//自助			
		if(this.tablename!=null&&this.tablename.length()>0)
		{
			Connection conn=null;		
			try{
				conn=AdminDb.getConnection();
				Table table=new Table(this.tablename);		
				DbWizard dbWizard =new DbWizard(conn);
				if(dbWizard.isExistTable(tablename,false))
				{
					return  (EVAL_BODY_INCLUDE);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
				
			}
			finally
			{
				try{
				 if (conn != null)
		             conn.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
		          
			}
			
		}
		return (SKIP_BODY); 	
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
}
