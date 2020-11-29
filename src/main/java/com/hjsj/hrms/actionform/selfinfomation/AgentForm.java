package com.hjsj.hrms.actionform.selfinfomation;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * 代理人设置
 * <p>Title:DeputyForm.java</p>
 * <p>Description>:AgentForm.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 6, 2010 10:18:57 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class AgentForm extends FrameForm {
    private String sql="";
    private String column="";
    private String where="";
    private String orderby="";
    private String principal_fullname="";
    private String start_date="";
    private String end_date="";
    private String agent_id="";//代理人id
    private String agent_fullname="";
    private String principal_id="";
    private String id="";
    private String editflag="";
    private String agent_status="";
    private String a0100="";
    private String nbase="";
    private String role_id="";
    private String user_flag="";
    /**不加载组织机构下的人员信息*/
	private String flag="0";
	/**选择方式
	 * =0,正常方式
	 * =1,checkbox
	 * =2,radio
	 * */
	private String selecttype="0";
	private String loadtype="0";
	/**登录用户库标识
	 * =0 权限范围内的库
	 * =1 权限范围内的登录用户库
     */
	private String dbtype="0";
	/**是否要加权限*/
	private String priv="1";
	/**是否其他的过滤条件*/
    private String isfilter="0";
    /**展示的树**/
	private String template_tree;
	/**资源类型*/
	private String res_flag;
	/**规章制度目录号,资源串序列，包括规章制度、模板等*/
	private String law_dir;
	/**模板类型*/
	private String type;
	private String operate;
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("res_flag",this.getRes_flag());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("start_date", this.getStart_date());
		this.getFormHM().put("end_date", this.getEnd_date());
		this.getFormHM().put("agent_id", this.getAgent_id());
		this.getFormHM().put("principal_fullname", this.getPrincipal_fullname());
		this.getFormHM().put("id", this.getId());
		this.getFormHM().put("editflag", this.getEditflag());
		this.getFormHM().put("agent_status", this.getAgent_status());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
		if(this.getPagination()!=null)
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		
	}


	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setLaw_dir((String)this.getFormHM().get("law_dir"));
		this.setTemplate_tree((String)this.getFormHM().get("bs_tree"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setOrderby((String)this.getFormHM().get("orderby"));		
		this.setPrincipal_fullname((String)this.getFormHM().get("principal_fullname"));
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		this.setAgent_id((String)this.getFormHM().get("agent_id"));
		this.setId((String)this.getFormHM().get("id"));
		this.setAgent_fullname((String)this.getFormHM().get("agent_fullname"));
		this.setPrincipal_id((String)this.getFormHM().get("principal_id"));
		this.setAgent_status((String)this.getFormHM().get("agent_status"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setUser_flag((String)this.getFormHM().get("user_flag"));
		this.setRole_id((String)this.getFormHM().get("role_id"));
		this.setOperate((String)this.getFormHM().get("operate"));
				
				
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/selfservice/selfinfo/agent/agentinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null){
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/selfservice/selfinfo/agent/agentinfo".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null)
		{
			this.setAgent_id("");
			this.setPrincipal_fullname("");
			this.setStart_date("");
			this.setEnd_date("");
			this.getFormHM().put("statr_data", "");
			this.getFormHM().put("end_data","");
			this.getFormHM().put("agent_id", "");
			this.getFormHM().put("principal_fullname","");
		}
		return super.validate(arg0, arg1);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getPrincipal_fullname() {
		return principal_fullname;
	}

	public void setPrincipal_fullname(String principal_fullname) {
		this.principal_fullname = principal_fullname;
	}

	

	public String getPrincipal_id() {
		return principal_id;
	}

	public void setPrincipal_id(String principal_id) {
		this.principal_id = principal_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEditflag() {
		return editflag;
	}

	public void setEditflag(String editflag) {
		this.editflag = editflag;
	}

	public String getAgent_fullname() {
		return agent_fullname;
	}

	public void setAgent_fullname(String agent_fullname) {
		this.agent_fullname = agent_fullname;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}


	public String getAgent_status() {
		return agent_status;
	}


	public void setAgent_status(String agent_status) {
		this.agent_status = agent_status;
	}


	public String getA0100() {
		return a0100;
	}


	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}


	public String getNbase() {
		return nbase;
	}


	public void setNbase(String nbase) {
		this.nbase = nbase;
	}


	public String getRole_id() {
		return role_id;
	}


	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}


	public String getUser_flag() {
		return user_flag;
	}


	public void setUser_flag(String user_flag) {
		this.user_flag = user_flag;
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


	public String getTemplate_tree() {
		return template_tree;
	}


	public void setTemplate_tree(String template_tree) {
		this.template_tree = template_tree;
	}


	public String getRes_flag() {
		return res_flag;
	}


	public void setRes_flag(String res_flag) {
		this.res_flag = res_flag;
	}


	public String getLaw_dir() {
		return law_dir;
	}


	public void setLaw_dir(String law_dir) {
		this.law_dir = law_dir;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getOperate() {
		return operate;
	}


	public void setOperate(String operate) {
		this.operate = operate;
	}

	

}
