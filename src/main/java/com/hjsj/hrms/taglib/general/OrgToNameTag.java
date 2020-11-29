/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hrms.frame.utility.AdminCode;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>Title:</p>
 * <p>Description:根据机构代码输出多层次的代码描述</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 3, 2008:10:28:37 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class OrgToNameTag extends TagSupport {
	/**组织机构当前号*/
	private String codeitemid;
	/**输出层级数*/
	private int    level=0;
	public int doStartTag() throws JspException {
		try
		{
			String codedesc=AdminCode.getOrgUpCodeDesc(codeitemid, level, 0);
			pageContext.getOut().println(codedesc);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return super.doStartTag();
	}
	public String getCodeitemid() {
		return codeitemid;
	}
	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

}
