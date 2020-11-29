package com.hjsj.hrms.taglib.sys;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class TextnewlineTab extends BodyTagSupport {
private String text;
private String len;
public String getLen() {
	return len;
}
public void setLen(String len) {
	this.len = len;
}
public String getText() {
	return text;
}
public void setText(String text) {
	this.text = text;
}
public int doStartTag() throws JspException
{
	return super.doStartTag();  
	
}
public int doEndTag() throws JspException 
{
	int st_len=10;
	if(len!=null&&len.length()>0)
		st_len=Integer.parseInt(len);
	if(text==null||text.length()<=0)
		return SKIP_BODY;	
	text=text.trim();
	try
	{
		for(int i=0;i<text.length();i++)
		{
			pageContext.getOut().println(text.charAt(i));	
            if(i+1==text.length())
            	break;
            if(text.charAt(i+1)==')')
            	continue;
			if((i+1)%st_len==0)
				pageContext.getOut().println("<br>");
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	
	return SKIP_BODY;	
}

}
