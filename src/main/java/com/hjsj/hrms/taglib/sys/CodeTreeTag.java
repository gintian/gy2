/**
 * 
 */
package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>Title:OrgTreeTag</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 23, 20061:34:10 PM
 * @author chenmengqing
 * @version 4.0
 */
public class CodeTreeTag extends BodyTagSupport {
	/**选中节点相应，弹出的网页*/
	private String action;
	/**弹出网页目标帧*/
	private String target;
	
	/**选择方式
	 * =0,正常方式
	 * =1,checkbox
	 * =2,radio
	 * */
	private String selecttype="0";
	/**是否显示根节点*/
	private boolean showroot=true;
	/**已选择的值*/
	private String checkvalue="";
	
	/**代码类id */
	private String codeSetID="";
	
	
	private String outTreePanel()
	{
		TreeItemView treeItem=new TreeItemView();
		Connection conn=null;	
		try
		{
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		pageContext.getSession().setAttribute("SYS_LOAD_ORG_ACTION",this.action);
		String codeid=userview.getManagePrivCode();
		String codevalue=userview.getManagePrivCodeValue();
		String a_code=codeid+codevalue;	
		if(userview.isSuper_admin())
			codeid="UN";
		treeItem.setName("root");
		treeItem.setIcon("/images/group.gif");	
		treeItem.setTarget(this.target);
		String rootdesc="";
		if(!"UN".equals(this.codeSetID)&&!"UM".equals(this.codeSetID)&&!"@K".equals(this.codeSetID))
			rootdesc="代码项目";
		else{
			//rootdesc="组织机构";
			conn=AdminDb.getConnection();
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
			rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
			if(rootdesc==null||rootdesc.length()<=0)
			{
				rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
			}
		}
		treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
		treeItem.setText(rootdesc); 
		treeItem.setTitle(rootdesc);
		String url="/system/load_tree2?target="+this.target+"&codeSetID="+this.codeSetID+"&id=0&first=1";		
	    treeItem.setLoadChieldAction(url);
	    treeItem.setAction("javascript:void(0)");	
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			    try {
					if(conn!=null)
					  conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}		 
	   
	    return treeItem.toJS();
	}
	
	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		try
		{
			strhtml.append("<div id=\"treemenu\" style=\"height: 330px;width:290px;overflow: auto;border-style:solid ;border-width:1px\">");
			strhtml.append("<SCRIPT LANGUAGE=\"javascript\">");
			strhtml.append("\n");			
			strhtml.append("Global.defaultInput=");
			strhtml.append(this.selecttype);			
			strhtml.append(";\n");
			if(!this.showroot)
			{
				strhtml.append("\n");			
				strhtml.append("Global.showroot=true;\n");
			}
			strhtml.append("\n");	
			if(!(this.checkvalue==null|| "".equals(this.checkvalue)))
			{
				strhtml.append("Global.checkvalue=\"");
				strhtml.append(this.checkvalue);
				strhtml.append("\";\n");
			}
			strhtml.append(outTreePanel());
			
			strhtml.append("</SCRIPT>");			
			strhtml.append("</div>");

			pageContext.getOut().println(strhtml.toString());
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	public String getSelecttype() {
		return selecttype;
	}

	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}

	public String getCheckvalue() {
		return checkvalue;
	}

	public void setCheckvalue(String checkvalue) {
		this.checkvalue = checkvalue;
	}

	public boolean isShowroot() {
		return showroot;
	}

	public void setShowroot(boolean showroot) {
		this.showroot = showroot;
	}

	public String getCodeSetID() {
		return codeSetID;
	}

	public void setCodeSetID(String codeSetID) {
		this.codeSetID = codeSetID;
	}

}
