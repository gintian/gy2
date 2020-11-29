package com.hjsj.hrms.taglib.sys;

import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 根据不同的锁 引入 不同的Ext 版本
 * @author guodd 2015-06-23
 * 解决ie8及一下浏览器 使用高版本ext 加载、渲染太慢的问题
 */
public class LinkExtJsTag extends BodyTagSupport{

	String frameDegradeId;
	public int doEndTag() throws JspException {
		StringBuffer extLinkStr=new StringBuffer();
		try
		{ 
			UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			if(userView!=null){
				//获取锁版本
				EncryptLockClient lockclient=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
				int version = lockclient.getVersion();
				if(version<50){//低于 50  则引入ext
					return SKIP_BODY;
				}
				String framedegrade = SystemConfig.getPropertyValue("framedegrade");
				framedegrade = "true".equals(framedegrade)&&version<70?"true":"false";
				if("true".equals(framedegrade)){
					extLinkStr.append("<script type=\"text/javascript\" src=\"/ext/adapter/ext/ext-base.js\"></script> \n");
					extLinkStr.append("<script type=\"text/javascript\" src=\"/ext/ext-all-old.js\"></script> \n");
					extLinkStr.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/ext/resources/css/ext-all-old.css\" /> \n");
				}else{
					extLinkStr.append("<script type=\"text/javascript\" src=\"/ext/ext-all.js\"></script> \n");
					extLinkStr.append("<script type=\"text/javascript\" src=\"/ext/ext-lang-zh_CN.js\"></script> \n");
					extLinkStr.append("<link href=\"/ext/resources/css/ext-all.css\" rel=\"stylesheet\" type=\"text/css\" /> \n");
				}
				if(frameDegradeId!=null && frameDegradeId.length()>0)
					pageContext.setAttribute(frameDegradeId, framedegrade, 1);
				pageContext.getOut().println(extLinkStr.toString());
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

	public String getFrameDegradeId() {
		return frameDegradeId;
	}

	public void setFrameDegradeId(String frameDegradeId) {
		this.frameDegradeId = frameDegradeId;
	}
}
