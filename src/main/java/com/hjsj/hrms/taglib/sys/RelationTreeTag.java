/**
 * 
 */
package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * <p>RelationTreeTag</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jan 26, 2013 4:34:10 PM
 * @author xuj
 * @version 4.0
 */
public class RelationTreeTag extends BodyTagSupport {
	/**选中节点相应，弹出的网页*/
	private String action;
	/**弹出网页目标帧*/
	private String target;
	//action链接参数名称dbpre+a0100组合参数名称
	private String paramkey;
	//action链接参数名称dbpre参数名称
	private String dbnamekey;
	//action链接参数名称a0100参数名称
	private String a0100key;
	
	//action连接中b0110key参数名称
	private String b0110key;
	
	//汇报关系 -1考核汇报关系 1主汇报关系 其它值为辅汇报关系
	private String default_line="1";
	
	private String outTreePanel() throws GeneralException
	{
		action = PubFunc.hireKeyWord_filter_reback(action);
		pageContext.getSession().setAttribute("SYS_LOAD_RELATION_ACTION", action);
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setIcon("/images/man.gif");	
		treeItem.setTarget(this.target);
		String rootdesc="";
		String a0100="";
		String b0110="";
		if(userview.getStatus()==4){
			rootdesc=userview.getUserFullName();
			a0100=userview.getA0100();
			b0110=userview.getUserOrgId();
		} else{
			a0100=userview.getA0100();
			b0110=userview.getUserOrgId();
			if(a0100==null||a0100.length()==0)
				throw new GeneralException("",ResourceFactory.getProperty("selfservice.module.pri"),"","");
			else{
				
				rootdesc=userview.getUserFullName();
			}
		}
		String dbpre = userview.getDbname();
		treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
		treeItem.setText(rootdesc); 
		treeItem.setTitle(rootdesc);
		String url="/system/load_relationtree?target="+target+"&paramkey="+paramkey+"&dbnamekey="+dbnamekey+"&a0100key="+a0100key+"&b0110key="+b0110key+"&mainbody_id="+dbpre+a0100+"&default_line="+default_line;
	    treeItem.setLoadChieldAction(url);
	    String theaction="javascript:void(0);";
	    if(this.action!=null&&this.action.length()>0)
	    {
	    	this.action=this.action.replaceAll("`", "&");
	    	if(this.action.indexOf('?')==-1){
	    		if(this.paramkey!=null&&this.paramkey.length()>0){
	    			if(this.b0110key!=null&&this.b0110key.length()>0)
	    				theaction=this.action+"?"+paramkey+"="+dbpre+a0100;
	    			else
	    				theaction=this.action+"?"+paramkey+"="+dbpre+a0100+"&"+b0110key+"="+b0110;
	    		}else if(this.dbnamekey!=null&&this.dbnamekey.length()>0&&this.a0100key!=null&&this.a0100key.length()>0){
	    			if(this.b0110key!=null&&this.b0110key.length()>0)
	    				theaction=this.action+"?"+this.dbnamekey+"="+dbpre+"&"+this.a0100key+"="+a0100+"&"+b0110key+"="+b0110;
	    			else
	    				theaction=this.action+"?"+this.dbnamekey+"="+dbpre+"&"+this.a0100key+"="+a0100;
	    		}
	    	}else{
	    		if(this.paramkey!=null&&this.paramkey.length()>0){
	    			if(this.b0110key!=null&&this.b0110key.length()>0)
	    				theaction=this.action+"&"+paramkey+"="+dbpre+a0100+"&"+b0110key+"="+b0110;
	    			else
	    				theaction=this.action+"&"+paramkey+"="+dbpre+a0100;
	    		}else if(this.dbnamekey!=null&&this.dbnamekey.length()>0&&this.a0100key!=null&&this.a0100key.length()>0){
	    			if(this.b0110key!=null&&this.b0110key.length()>0)
	    				theaction=this.action+"&"+this.dbnamekey+"="+dbpre+"&"+this.a0100key+"="+a0100+"&"+b0110key+"="+b0110;
	    			else
	    				theaction=this.action+"&"+this.dbnamekey+"="+dbpre+"&"+this.a0100key+"="+a0100;
	    		}
    		}
	    	
	    }
	    treeItem.setAction(theaction);	    
	    return treeItem.toJS();
	}
	
	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		try
		{
			initProperties();
			strhtml.append("<div id=\"treemenu\">");
			strhtml.append("<SCRIPT LANGUAGE=\"javascript\">");
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
	private void initProperties(){
		this.paramkey=this.paramkey==null?"":this.paramkey;
		this.dbnamekey=this.dbnamekey==null?"":this.dbnamekey;
		this.a0100key=this.a0100key==null?"":this.a0100key;
		this.b0110key=this.b0110key==null?"":this.b0110key;
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

	public String getParamkey() {
		return paramkey;
	}

	public void setParamkey(String paramkey) {
		this.paramkey = paramkey;
	}

	public String getDbnamekey() {
		return dbnamekey;
	}

	public void setDbnamekey(String dbnamekey) {
		this.dbnamekey = dbnamekey;
	}

	public String getA0100key() {
		return a0100key;
	}

	public void setA0100key(String a0100key) {
		this.a0100key = a0100key;
	}

	public String getB0110key() {
		return b0110key;
	}

	public void setB0110key(String b0110key) {
		this.b0110key = b0110key;
	}

	public String getDefault_line() {
		return default_line;
	}

	public void setDefault_line(String default_line) {
		this.default_line = default_line;
	}

}
