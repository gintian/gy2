package com.hjsj.hrms.taglib.sys;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
public class CTreeTag extends BodyTagSupport {
	private String id;
	private String cid;
	private String divname;
	public int doEndTag() throws JspException{
		JspWriter out=pageContext.getOut();
		try {
			out.println("<SCRIPT LANGUAGE=javascript>");
			out.println("var root=new xtreeItem(\"root\",\"代码维护\",\"/system/codemaintence/codetree.do?b_search=link\",\"mil_body\",\"代码维护\",\"/images/unit.gif\",\"/maintence/codetree?params=root&parentid=00&target=mil_body&cid="+this.getCid()+"\");");			
			out.println("root.setup(document.getElementById(\""+this.getDivname()+"\"));");
			out.println("</script>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.doEndTag();
		
	}
	public int doStartTag() throws JspException{
		return super.doStartTag();
	}

	
	public String getDivname() {
		return divname;
	}
	public void setDivname(String divname) {
		this.divname = divname;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
