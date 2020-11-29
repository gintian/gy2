package com.hjsj.hrms.taglib.sys.warn;

import com.hjsj.hrms.valueobject.tree.TreeItemView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class IsRoleTreeTag extends BodyTagSupport {
	/**不加载组织机构下的人员信息*/
	private String flag="0";
	private String select_id="";
	public String getSelect_id() {
		return select_id;
	}
	public void setSelect_id(String select_id) {
		this.select_id = select_id;
	}
	/**选择方式
	 * =0,正常方式
	 * =1,checkbox
	 * =2,radio
	 * */
	public int doEndTag() throws JspException{
		JspWriter out=pageContext.getOut();
		StringBuffer strhtml=new StringBuffer();
		try {
			strhtml.append("Global.defaultInput=");
			strhtml.append(1);
			strhtml.append(";\n");			
			strhtml.append("\n");			
			strhtml.append("Global.showroot=false;\n");		
			strhtml.append("\n");	
			if(select_id!=null&&select_id.length()>0)
			{
				strhtml.append("Global.checkvalue=\",");
				strhtml.append(this.select_id);
				strhtml.append(",\";\n");
			}
			TreeItemView treeItem=new TreeItemView();		
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("");
			String rootdesc="预警角色";
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/sys/warn/warnroletree?flag="+flag+"");
		    treeItem.setAction("javascript:void(0)");	
		    strhtml.append(treeItem.toJS());
		    
			out.println(strhtml.toString());			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.doEndTag();
		
	}
	public int doStartTag() throws JspException{
		return super.doStartTag();
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
