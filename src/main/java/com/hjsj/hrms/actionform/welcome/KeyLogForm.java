package com.hjsj.hrms.actionform.welcome;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

public class KeyLogForm extends FrameForm {

	 private String content_type="";
     private String sql="";
     private String where="";
     private String cloumn="";
     private String select_name;//筛选名字,查询条件的人员名称可以使拼音简码
     private String code;//连接级别
     private String orderby;
     private String kind;
     private String a_code;
     private String userbase="";
     private String dbcond="";
     private String treeCode="";
     private String content="";
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("content_type", this.getContent_type());
        
        this.getFormHM().put("select_name", this.select_name);
        this.getFormHM().put("code", this.code);
        this.getFormHM().put("kind", this.kind);
        this.getFormHM().put("userbase", this.getUserbase());
        this.getFormHM().put("a_code", this.getA_code());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		 this.setContent_type((String)this.getFormHM().get("content_type"));
	     this.setSql((String)this.getFormHM().get("sql"));
	     this.setWhere((String)this.getFormHM().get("where"));
	     this.setCloumn((String)this.getFormHM().get("cloumn"));
	     this.setOrderby((String)this.getFormHM().get("orderby"));
	     this.setDbcond((String)this.getFormHM().get("dbcond"));	     
	     this.setUserbase((String)this.getFormHM().get("userbase"));
	     this.setSelect_name((String)this.getFormHM().get("select_name"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
    	
        if("/selfservice/welcome/keylog".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
        	if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
        }
        return super.validate(arg0, arg1);
	}
	public String getContent_type() {
		return content_type;
	}
	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getCloumn() {
		return cloumn;
	}
	public void setCloumn(String cloumn) {
		this.cloumn = cloumn;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public String getDbcond() {
		return dbcond;
	}
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

}
