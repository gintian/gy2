package com.hjsj.hrms.taglib.flow;

import com.hjsj.hrms.businessobject.kq.FixFlowButton;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 固定表单按钮
 * @author Owner
 *
 */
public class FixFlowButtonTag extends BodyTagSupport {
	private String name="";
	private String url="";
	private HashMap parammap=new HashMap();//request.getParameterMap();
	private String formname="";//form的文件名
	private String js_flag="";//是否现实js
	public String getFormname() {
		return formname;
	}
	public void setFormname(String formname) {
		this.formname = formname;
	}
	public int doStartTag() throws JspException
	{
		return super.doStartTag();  
		
	}
	public int doEndTag() throws JspException 
	{
		if(this.url==null||this.url.length()<=0)
			return SKIP_BODY;
		if(this.name==null||this.name.length()<=0)
			return SKIP_BODY;		
		
		Connection conn=null;
		try
		{
			 conn= AdminDb.getConnection();
			 UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			 FixFlowButton fixFlowButton=new FixFlowButton(conn,userview);
			 ArrayList tabid_list=fixFlowButton.getT_wf_define_Tabid_list(this.name);
			 String tabid=fixFlowButton.getT_wf_define_Tabid(tabid_list,"edit_form",this.url);
			 if(tabid!=null&&tabid.length()>0)
			 {
				 if(fixFlowButton.isEditUrl(tabid,this.url,parammap))
				 {
					 
					 pageContext.getOut().println(fixFlowButton.showEditButton(tabid));
					 if(js_flag!=null&& "1".equals(js_flag))
					 {
						 Object formObject = pageContext.getSession().getAttribute(formname);
						 if(formObject==null)
					            return SKIP_BODY;  
						 HashMap formMap=((FrameForm)formObject).getFormHM();	
						 pageContext.getOut().println(fixFlowButton.showJavaScript(tabid,formMap));
					 }
				 }
			 }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			   try {
				  if(conn!=null)
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return SKIP_BODY;
	}
	
	
	public HashMap getParammap() {
		return parammap;
	}
	public void setParammap(HashMap parammap) {
		this.parammap = parammap;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getJs_flag() {
		return js_flag;
	}
	public void setJs_flag(String js_flag) {
		this.js_flag = js_flag;
	}
}
