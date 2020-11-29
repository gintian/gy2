package com.hjsj.hrms.taglib.kq;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


public class KqAnalysis extends BodyTagSupport{
private String value;
	
	public int doEndTag() throws JspException
	{
		if(value==null||value.length()<=0)
			return SKIP_BODY;
		try
		{
			String on = value;
			String ss="";
			String out[] =on.split("\\.");
			for(int i=0;i<out.length;i++)
			{
				if(i==0)
				{
					ss = out[i];
//					System.out.println(ss);	
					pageContext.getOut().println(ss);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
