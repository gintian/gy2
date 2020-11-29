package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.lang.reflect.Method;
/**
 * 5.0转向图返回按钮
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class TipwizardButtonTag extends BodyTagSupport {
	private String flag="";
	private String target="";
	private String formname="";
	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		if(formname!=null&&formname.length()>0)
		{
			Object formObject = pageContext.getSession().getAttribute(formname);
	        if(formObject==null)
	        	return SKIP_BODY;
	        Class formclass=formObject.getClass();
	        try {
				Method method = formclass.getMethod("getReturnvalue");
				if(method!=null)
				{
					java.lang.Object obj=method.invoke(formObject);	
					String returnvalue = (String) obj;					
					if(returnvalue==null||!"dxt".equals(returnvalue))
					{
						return SKIP_BODY;
					}
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				try{
				Method method = formclass.getMethod("getReturnflag");
				if(method!=null)
				{
					java.lang.Object obj=method.invoke(formObject);	
					String returnvalue = (String) obj;					
					if(returnvalue==null||!"dxt".equals(returnvalue))
					{
						return SKIP_BODY;
					}
				}
				
				}catch(Exception e){
					
				}
				
				
			} 
		}
		
		if(userview.getBosflag()!=null&&("hl".equals(userview.getBosflag())|| "hcm".equals(userview.getBosflag())))
		{
			
			
			strhtml.append("<input type=\"button\" name=\"b_retrun\"");
			strhtml.append(" value=\""+ResourceFactory.getProperty("button.return")+"\"");
			strhtml.append(" class=\"mybutton\""); 
			strhtml.append(" onclick=\"hrbreturn('"+flag+"','"+target+"','"+formname+"');\">");
			try {
				pageContext.getOut().println(strhtml.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SKIP_BODY;		
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getFormname() {
		return formname;
	}
	public void setFormname(String formname) {
		this.formname = formname;
	}
}
