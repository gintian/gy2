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

public class AgentEmpTreeTag  extends BodyTagSupport {
	/**选中节点相应，弹出的网页*/
	private String action;
	/**弹出网页目标帧*/
	private String target;
	/**不加载组织机构下的人员信息*/
	private String flag="0";
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
	/**登录用户库标识
	 * =0 权限范围内的库
	 * =1 权限范围内的登录用户库
     */
	private String dbtype="0";
	/**是否要加权限*/
	private String priv="1";
	
	private String showDb="0";  //选择人员时是否先显示人员库  0:不显示  1:显示
	
    /**加载选项
     * =0（单位|部门|职位）
     * =1 (单位|部门)
     * =2 (单位)
     * */
    private String loadtype="0";
    /**是否其他的过滤条件*/
    private String isfilter="0";
    /**人员库名,多个人员库，用逗号分隔*/
    private String dbpre;
    /**
     * 加载虚拟节点
     * =0 不加载
     * =1 加载
     */   
    /** 只列本人所在单位节点  0:不显示  1：显示  author:dc*/
    private String showSelfNode="0";
    private String orgcode=""; /**机构代码*/
    private String umlayer="";/** 1,2,3,4,5.... 加载部门的层级，例如定义只加载1层级，当单位下有3层级的部门，树加载到第一层级 author:许建*/ 
	public void setRootaction(String rootaction) {
		if(rootaction==null|| "".equalsIgnoreCase(rootaction))
			rootaction="0";
		
	}
	public String getShowSelfNode() {
		return showSelfNode;
	}

	public void setShowSelfNode(String showSelfNode) {
		this.showSelfNode = showSelfNode;
	}
	public String getLoadtype() {
		return loadtype;
	}

	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	/**
	 * 查找组织机构名称
	 * @return
	 */
	private String findOrgRootDesc()
	{
		Connection conn=null;
		String value=null;
		try
		{
			conn=AdminDb.getConnection();
			Sys_Oth_Parameter sysparam=new Sys_Oth_Parameter(conn);
			value=sysparam.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
			if(value==null|| "".equals(value))
				value=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
		return value;
	}
	private String outTreePanel()
	{
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		pageContext.getSession().setAttribute("Agent_ORG_ACTION",this.action);
		String codeid="UN";
		String codevalue=userview.getUserOrgId();	
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setIcon("/images/root.gif");	
		treeItem.setTarget(this.target);

		String rootdesc=findOrgRootDesc();//ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
		treeItem.setText(rootdesc); 
		treeItem.setTitle(rootdesc);
		//this.loadtype="1";
		this.umlayer = this.umlayer!=null&&this.umlayer.trim().length()>0?this.umlayer:"0";
		String url="/agent/agent_tree?dbpre=" + this.dbpre 
					+ "&isfilter=" + this.isfilter + "&target="+this.target
					+"&flag="+this.flag+"&dbtype="+this.dbtype+"&priv="
					+this.priv+"&loadtype="+this.loadtype+"&first=1"				
					+"&orgcode="+this.orgcode+"&umlayer="+this.umlayer;

		if("0".equals(this.priv))//不加权限过滤
		{
			url=url+"&params=root&id=UN"+codevalue;			
		}
		else
		{
			if(!("UN".equals(orgcode)))
			{
				url=url+"&params=root&id="+codeid+codevalue;
			}
			else
			{
				url=url+"&params=root&id=UN"+codevalue;
			}
		}
		if("1".equals(this.showDb)&& "1".equals(this.flag)&&!(dbpre!=null && !"null".equalsIgnoreCase(dbpre) && dbpre!=null && dbpre.length()>0)) //如果机构下加载人员信息
		{
			url+="&showDbName=1&showDb=1";
		}
		//只列本人所在单位节点		
		if("1".equals(this.showSelfNode))
		{
			url+="&showSelfNode=1";
		}
		
		
	    treeItem.setLoadChieldAction(url);
	    String theaction=null;
	    if(this.action!=null)
	    {
	    	
	    	
	    	
	    }
	    treeItem.setAction("javascript:void(0)");	    
	    return treeItem.toJS();
	}
	
	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		try
		{
			/*
			strhtml.append("<link href=\"/css/xtree.css\" rel=\"stylesheet\" type=\"text/css\" >");
			strhtml.append("\n");
			strhtml.append("<script LANGUAGE=\"javascript\" src=\"/js/xtree.js\"></script>");
			strhtml.append("\n");	
			*/
			strhtml.append("<div id=\"treemenu\">");
			
			strhtml.append("<SCRIPT LANGUAGE=\"javascript\">");
			strhtml.append("\n");			
			strhtml.append("Global.defaultInput=");
			strhtml.append(this.selecttype);
			strhtml.append(";\n");
			if(!this.showroot)
			{
				strhtml.append("\n");			
				strhtml.append("Global.showroot=false;\n");
			}
			strhtml.append("\n");	
			if(!(this.checkvalue==null|| "".equals(this.checkvalue)))
			{
				strhtml.append("Global.checkvalue=\"");
				strhtml.append(this.checkvalue);
				strhtml.append("\";\n");
			}
			/**加载组织机构下的人员信息*/
			if("1".equalsIgnoreCase(this.flag))
			{
				strhtml.append("Global.defaultchecklevel=3;");
				strhtml.append("Global.defaultradiolevel=3;");
				strhtml.append("Global.showorg=1;");	
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

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getIsfilter() {
		return isfilter;
	}

	public void setIsfilter(String isfilter) {
		this.isfilter = isfilter;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	

	public void setRootPriv(String rootPriv) {
		if(rootPriv==null|| "".equalsIgnoreCase(rootPriv))
			rootPriv="1";
		
	}

	public String getShowDb() {
		return showDb;
	}

	public void setShowDb(String showDb) {
		this.showDb = showDb;
	}

	

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	

	public String getUmlayer() {
		return umlayer;
	}

	public void setUmlayer(String umlayer) {
		this.umlayer = umlayer;
	}

	

		

}
