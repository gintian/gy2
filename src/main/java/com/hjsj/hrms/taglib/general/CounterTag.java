/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.Counter;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.sql.Connection;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-3-13:15:48:25</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CounterTag extends TagSupport {
	private String name="";
	private String value="";
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int doStartTag() throws JspException {
		
        Connection conn = null;
        try
        {
            UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
            conn=AdminDb.getConnection();
        	Counter counter=new Counter(conn,userview);
        	int count=0;
            String conuterforcont=(String) pageContext.getSession().getAttribute("conuterforcont");
            String b_quary=(String)pageContext.getRequest().getParameter(name);
            if(conuterforcont!=null&&conuterforcont.trim().length()!=0&& "true".equalsIgnoreCase(conuterforcont.trim())){
            	 count=counter.getCount();
            }else{
            	if(b_quary!=null&&value!=null&&b_quary.equalsIgnoreCase(value)){
             	   HttpSession se=(HttpSession)pageContext.getSession();
             	   se.setAttribute("conuterforcont", "true");
             	   count=counter.getCount();
             	   counter.saveCount();
                 }else{
                	  count=counter.getCount();
                 }
            }
            pageContext.getOut().println(ResourceFactory.getProperty("label.sys.count")+":"+count);           	
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	try
        	{
        		if(conn!=null)
        			conn.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}
        }
		return super.doStartTag();
	}

}
