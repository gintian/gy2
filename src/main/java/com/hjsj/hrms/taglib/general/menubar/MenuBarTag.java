/**
 * 
 */
package com.hjsj.hrms.taglib.general.menubar;

import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>Title:MenuBarTag</p>
 * <p>Description:菜单条件标签,根据menu.xml文件定义的菜单结构在前台生成菜单</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-18:17:59:25</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class MenuBarTag extends TagSupport {
	/**模块菜单名称*/
	private String module_name;
	/**菜单条名称*/
	private String name;
	/**菜单条标识号*/
	private String id;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 
	 */
	public MenuBarTag() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int doEndTag() throws JspException {
		StringBuffer strbar=new StringBuffer();
		try
		{
            UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
            
			Menu menu=new Menu(this.module_name,this.name,userview);
			String strmenu=menu.outMenuxml();
			if(strmenu==null|| "".equals(strmenu))
				return SKIP_BODY;
			/**生成菜单条MenuBar*/
//          <table extra="menubar"  id="menubar1"  menu="menu1"  cellspacing="1"  cellpadding="0" ></table>			
			strbar.append("<table extra=\"menubar\" id=\"");
			strbar.append(id);
			strbar.append("\" menu=\"");
			strbar.append(name);
			strbar.append("\" cellspacing=\"1\"  cellpadding=\"0\"></table>");
			pageContext.getOut().print(strbar.toString());

			pageContext.getOut().print(strmenu);
			strbar.setLength(0);
			strbar.append("<script language=\"javascript\">");
			strbar.append("var ");
			strbar.append(this.name);
			strbar.append("=createMenu(\"");
			strbar.append(name);
			strbar.append("\"); var __t=");
			strbar.append(name);
			strbar.append("; initMenu(__t);");
			//strbar.append("  initDocument();");
			strbar.append("</script>");
			pageContext.getOut().print(strbar.toString());
//			<script language="javascript">
//			  var menu1=createMenu("menu1");
//			  var __t=menu1;
//			  initMenu(__t);
//			</script>
			return SKIP_BODY;			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return SKIP_BODY;
		}
	}
	
	public String getModule_name() {
		return module_name;
	}
	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}
}
