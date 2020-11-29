package com.hjsj.hrms.taglib;

import com.hjsj.hrms.utils.PubFunc;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


public class FormatDecimalsTag  extends BodyTagSupport {

	private String value;
	private String length;
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	public int doEndTag() throws JspException {
		try
		{
			double v=Double.parseDouble(value);
			int l = Integer.parseInt(length);
			pageContext.getOut().println(PubFunc.formatDecimals(v, l));
			return SKIP_BODY;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return SKIP_BODY;			
		}
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}

}
