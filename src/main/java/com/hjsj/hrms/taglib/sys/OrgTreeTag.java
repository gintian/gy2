/**
 * 
 */
package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;

/**
 * <p>Title:OrgTreeTag</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 23, 20061:34:10 PM
 * @author chenmengqing
 * @version 4.0
 */
public class OrgTreeTag extends BodyTagSupport {
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
    private String lv="0";
    /**组织机构根节点是否加动作*/
    private String rootaction="0";
    /** 组织机构根节点加动作,是否按权限范围控制 */
    private String rootPriv="1";
    
    /**是否使用操作单位单位 1.使用 0.不使用*/
    private String viewunit="0";
    /**是否只显示当前单位的下一级 1.显示 0.不显示**/
    private String nextlevel="0";
    
    /** 只列本人所在单位节点  0:不显示  1：显示  author:dc*/
    private String showSelfNode="0";
   
    private String orgcode=""; /**机构代码*/
    private String chitemid=""; /**是否显示机构人员*/

    private String umlayer="";/** 1,2,3,4,5.... 加载部门的层级，例如定义只加载1层级，当单位下有3层级的部门，树加载到第一层级 author:许建*/ 

    private String privtype="";
    private String divStyle="";
    /**是否按模块划分操作单位，当nmodule有值时，即为模块标志，根据该值取模块的操作单位，lizhenwei20100312*/
    private String nmodule="";
    /**是否有级联的机构权限，=0展现所有=1只展现有权限的，只有父亲的权限看不见子节点*/
    private String cascadingctrl="0";
    /**当selecttype不为0,并且flag为1时，checkbox或radio出现的层级，0=全部出现，1=部门以下都出现,2=职位以下,3=只有人员出现,4=当showDb=1显示人员库时不在人员库上显示checkbox**/
    private String checklevel = "3";
    /**节点上是否需要加action连接，因为碰到这种情况，主页面上有一个组织机构数，是要加连接的，弹出页面中也有一个组织机构树，这个是不需要加连接的，但是当弹出页面关闭后，
     * 在展开主主页上的几点，也没有连接了，为了解决这个问题，加这个参数，在弹出页面的标签中，加入isAddAction=“false”，并且把action的值还写入，这个样就没问题了
     * */
    private String isAddAction="true";
    /**是否只显示自己所在部门内人员*/
    private String isShowSelfDepts="0";
    
	private String dbvalue = "";   //领导班子带有人员库机构树所加
    
    //当目录树节点显示checkbox时，点击check是否级联操作子节点check是否选中状态默认false=不级联|true=级联操作  xuj add 2013-12-4
    private String cascade="false";
	public String getCascade() {
		return cascade;
	}

	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	public String getIsAddAction() {
		return isAddAction;
	}

	public void setIsAddAction(String isAddAction) {
		this.isAddAction = isAddAction;
	}

	public String getPrivtype() {
		return privtype;
	}

	public void setPrivtype(String privtype) {
		this.privtype = privtype;
	}


	public String getRootaction() {
		return rootaction;
	}

	public void setRootaction(String rootaction) {
		if(rootaction==null|| "".equalsIgnoreCase(rootaction))
			rootaction="0";
		this.rootaction = rootaction;
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
	
	public String getIsShowSelfDepts() {
		return isShowSelfDepts;
	}

	public void setIsShowSelfDepts(String isShowSelfDepts) {
		this.isShowSelfDepts = isShowSelfDepts;
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
		//unit_id 没有'`'说明没有授权操作单位
		if(userview.getUnit_id().indexOf("`")<0)
			userview.setUnit_id("");
		if("true".equalsIgnoreCase(this.isAddAction))
	    	pageContext.getSession().setAttribute("SYS_LOAD_ORG_ACTION",this.action);
		String codeid=userview.getManagePrivCode();
		String codevalue=userview.getManagePrivCodeValue();
		if(privtype!=null&& "kq".equals(privtype))
	    {
			codeid=RegisterInitInfoData.getKqPrivCode(userview);
			codevalue=RegisterInitInfoData.getKqPrivCodeValue(userview);
	    }
		String a_code=codeid+codevalue;	
		if(userview.isSuper_admin()){
			codeid="UN";
		}
		if(this.orgcode!=null&&this.orgcode.trim().length()>1){
			a_code = this.orgcode;
			codeid = this.orgcode.substring(0,2);
			codevalue = this.orgcode.substring(2);
		}
		String codeall = userview.getUnit_id();
		codeall = PubFunc.getTopOrgDept(codeall);
		String unitarr[] = codeall.split("`"); 
		if(this.viewunit!=null&& "1".equals(this.viewunit)){
			if(codeall.length()>2){//xuj 2010-6-7 add 针对oracle非超级业务用户未授权任何操作单位时的情况 UN
				for(int i=0;i<unitarr.length;i++){
					if(unitarr[i]!=null&& "UN".equalsIgnoreCase(unitarr[i])){
						a_code="UN";
						break;
					}else{
						a_code="";
					}
				}
			}else{
				a_code="";
				codeid="";
			}
			codeid="";
		}
		if(this.nmodule!=null&&!"".equals(this.nmodule))
		{
			if(userview.isSuper_admin())
				a_code="UN";
			else{
				String nviewunit=userview.getUnitIdByBusi(this.nmodule);
				nviewunit = PubFunc.getTopOrgDept(nviewunit);
				String nunitarr[] = nviewunit.split("`"); 
				for(int i=0;i<nunitarr.length;i++){
					if(nunitarr[i]!=null&& "UN".equalsIgnoreCase(nunitarr[i])){
						a_code="UN";
						break;
					}else{
						a_code="";
					}
				}
			}
			codeid="";
		}
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
		String url="/system/load_tree?dbpre=" + this.dbpre 
					+ "&isfilter=" + this.isfilter + "&target="+this.target
					+"&flag="+this.flag+"&dbtype="+this.dbtype+"&priv="
					+this.priv+"&loadtype="+this.loadtype+"&first=1"
					+"&lv="+this.lv+"&viewunit="+this.viewunit+"&nextlevel="+this.nextlevel
					+"&chitemid="+this.chitemid+"&orgcode="+this.orgcode+"&umlayer="+this.umlayer+"&privtype="+this.privtype+"&nmodule="+this.nmodule+"&cascadingctrl="+cascadingctrl+"&isShowSelfDepts="+isShowSelfDepts+"&dbvalue="+this.dbvalue;

		if("0".equals(this.priv))//不加权限过滤
		{
			url=url+"&params=codeitemid%3Dparentid&id=UN";
			a_code="";
		}
		else
		{
			if(!("UN".equals(a_code)))
			{
				url=url+"&params=codeitemid%3D'"+codevalue+"'&id="+codeid+codevalue;
			}
			else
			{
				url=url+"&params=codeitemid%3Dparentid&id=UN";
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
		
		url+="&cascadingctrl="+this.cascadingctrl;
		url+="&isAddAction="+this.isAddAction;
		
	    treeItem.setLoadChieldAction(url);
	    String theaction=null;
	    if(this.action!=null)
	    {
	    	if("1".equals(this.rootaction)&& "0".equals(this.rootPriv))
	    		a_code="UN";
	    	
	    	if(this.viewunit!=null&& "0".equals(this.viewunit)){
	    		if(this.action.indexOf('?')==0){
	    			theaction=this.action+"?a_code="+a_code;
	    		}else{
	    			theaction=this.action+"&a_code="+a_code;
	    		}
	    	}else{
	    		if(this.action.indexOf('?')==0){
	    			theaction=this.action+"?a_code="+a_code;
	    		}else{
	    			theaction=this.action+"&a_code="+a_code;
	    		}
	    	}
	    }
	    if("1".equalsIgnoreCase(this.rootaction))
	    	treeItem.setAction(theaction);
	    else
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
			if(this.divStyle==null||divStyle.length()<=0)
			  strhtml.append("<div id=\"treemenu\">");
			else
				strhtml.append("<div id=\"treemenu\" style=\""+this.divStyle+"\"  class=\"complex_border_color\">");	
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
				strhtml.append("Global.defaultchecklevel="+this.checklevel+";");
				strhtml.append("Global.defaultradiolevel="+this.checklevel+";");
				strhtml.append("Global.showorg=1;");	
			} 
			if("1".equals(selecttype)){
				strhtml.append("Global.cascade="+this.cascade+";");
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

	public String getLv() {
		return lv;
	}

	public void setLv(String lv) {
		this.lv = lv;
	}

	public String getNextlevel() {
		return nextlevel;
	}

	public void setNextlevel(String nextlevel) {
		this.nextlevel = nextlevel;
	}

	public String getViewunit() {
		return viewunit;
	}

	public void setViewunit(String viewunit) {
		this.viewunit = viewunit;
	}

	public String getRootPriv() {
		return rootPriv;
	}

	public void setRootPriv(String rootPriv) {
		if(rootPriv==null|| "".equalsIgnoreCase(rootPriv))
			rootPriv="1";
		this.rootPriv = rootPriv;
	}

	public String getShowDb() {
		return showDb;
	}

	public void setShowDb(String showDb) {
		this.showDb = showDb;
	}

	public String getShowSelfNode() {
		return showSelfNode;
	}

	public void setShowSelfNode(String showSelfNode) {
		this.showSelfNode = showSelfNode;
	}

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getChitemid() {
		return chitemid;
	}

	public void setChitemid(String chitemid) {
		this.chitemid = chitemid;
	}

	public String getUmlayer() {
		return umlayer;
	}

	public void setUmlayer(String umlayer) {
		this.umlayer = umlayer;
	}

	public String getDivStyle() {
		return divStyle;
	}

	public void setDivStyle(String divStyle) {
		this.divStyle = divStyle;
	}
	
	 public String getNmodule() {
			return nmodule;
		}

		public void setNmodule(String nmodule) {
			this.nmodule = nmodule;
		}

		public String getCascadingctrl() {
			return cascadingctrl;
		}

		public void setCascadingctrl(String cascadingctrl) {
			this.cascadingctrl = cascadingctrl;
		}

		public String getChecklevel() {
			return checklevel;
		}

		public void setChecklevel(String checklevel) {
			this.checklevel = checklevel;
		}

		public String getDbvalue() {
			return dbvalue;
		}

		public void setDbvalue(String dbvalue) {
			this.dbvalue = dbvalue;
		}


}
