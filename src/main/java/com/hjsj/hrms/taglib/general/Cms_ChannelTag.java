/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.sys.cms.Cms_ChannelBo;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;

/**
 *<p>Title:Cms_ChannelTag频道标签</p> 
 *<p>Description:根据输入的频道号，显示其对应的一级和二级频道</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-7:下午01:50:26</p> 
 *@author cmq
 *@version 4.0
 */
public class Cms_ChannelTag extends BodyTagSupport {
	/**显示的频道，按此频显示菜单*/
	private String chl_no;
    private String type;
    private String chl_id;
    private String showtye;
	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		Connection conn=null;
		try
		{
			conn=AdminDb.getConnection();//?会不会存取效率的问题
			Cms_ChannelBo chlbo=new Cms_ChannelBo(conn);
			chlbo.setType(type);
			chlbo.setChl_id(chl_id);
			chlbo.setShowtye(showtye);
			strhtml.append(chlbo.outCmsHtml(chl_no));
			pageContext.getOut().print(strhtml.toString());
			return SKIP_BODY;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return SKIP_BODY;
		}
		finally
		{
			try
			{
				if(conn!=null)
					conn.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}


	public String getChl_no() {
		return chl_no;
	}

	public void setChl_no(String chl_no) {
		this.chl_no = chl_no;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getChl_id() {
		return chl_id;
	}


	public void setChl_id(String chl_id) {
		this.chl_id = chl_id;
	}


	public String getShowtye() {
		return showtye;
	}


	public void setShowtye(String showtye) {
		this.showtye = showtye;
	}
}
