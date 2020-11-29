package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
/**
 * <p>Title: ThemesTag </p>
 * <p>Description:获取系统皮肤样式主题类（输出样式文件） </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-4-26 下午05:03:39</p>
 * @author xuj
 * @version 1.0
 */
public class ThemesTag extends BodyTagSupport {

	//样式文件名称
	private String cssName="content.css";
	
	public String getCssName() {
		return cssName;
	}

	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		try
		{
			UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			if(userView!=null){
				String bosflag = userView.getBosflag();
				if("hcm".equals(bosflag)){
					strhtml.append("<link href='/css/css1.css' rel='stylesheet' type='text/css' >");
					String themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());	
					strhtml.append("<link href=\"/css/hcm/themes/"+themes+"/"+cssName+"\" rel=\"stylesheet\" type=\"text/css\" />");
				}else{
					strhtml.append("<link href='/css/css1.css' rel='stylesheet' type='text/css' >");
				}
				pageContext.getOut().println(strhtml.toString());
			}
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


}
