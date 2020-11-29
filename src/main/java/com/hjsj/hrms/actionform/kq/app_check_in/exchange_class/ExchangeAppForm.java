package com.hjsj.hrms.actionform.kq.app_check_in.exchange_class;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

public class ExchangeAppForm extends FrameForm {
	/*private String cur_a0100;
	private String cur_data;
	private String cur_class;
	private String cur_nbase;
	private String ex_a0100;
	private String ex_nbase;
	private String ex_date;
	private String ex_reason;*/
	private String userTree;
	private String user_name;
	/**选择类型check radio世间0、1、2*/
	private String selecttype="1";
	/**加载人0 、1*/
	private String flag="1";
	/**加载用户库
	 * =0 权限范围内的库
	 * =1 权限范围内的登录用户库
	 * */
	private String dbtype="0";
	/**权限过滤*/
	private String priv="0"; //不加权限过滤
	private RecordVo ex_vo=new RecordVo("q19");
	private String id;
	private String class_name;
	private String exclass_name;
	private String app_flag;	
	private String flagsturt;
	private String spFlag;
	private String approved_delete="1";//已批申请登记数据是否可以删除;0:不删除；1：删除
	private String returnvalue="1";
    public String getApproved_delete() {
		return approved_delete;
	}
	public void setApproved_delete(String approved_delete) {
		this.approved_delete = approved_delete;
	}
	public String getSpFlag() {
		return spFlag;
	}
	public void setSpFlag(String spFlag) {
		this.spFlag = spFlag;
	}
	public String getFlagsturt() {
		return flagsturt;
	}
	public void setFlagsturt(String flagsturt) {
		this.flagsturt = flagsturt;
	}
	public String getClass_name() {
		return class_name;
	}
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	public String getExclass_name() {
		return exclass_name;
	}
	public void setExclass_name(String exclass_name) {
		this.exclass_name = exclass_name;
	}
	@Override
    public void outPutFormHM()
    {
		this.setEx_vo((RecordVo)this.getFormHM().get("ex_vo"));
		this.setId((String)this.getFormHM().get("id"));
		this.setClass_name((String)this.getFormHM().get("class_name"));
		this.setExclass_name((String)this.getFormHM().get("exclass_name"));
		this.setSpFlag((String)this.getFormHM().get("spFlag"));
		this.setApproved_delete((String)this.getFormHM().get("approved_delete"));
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("ex_co",this.getEx_vo());
		this.getFormHM().put("id",this.getId());
		this.getFormHM().put("app_flag",this.getApp_flag());
		this.getFormHM().put("flagsturt",this.getFlagsturt());
	}
	public RecordVo getEx_vo() {
		return ex_vo;
	}
	public void setEx_vo(RecordVo ex_vo) {
		this.ex_vo = ex_vo;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getPriv() {
		return priv;
	}
	public void setPriv(String priv) {
		this.priv = priv;
	}
	public String getSelecttype() {
		return selecttype;
	}
	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUserTree() {
		return userTree;
	}
	public void setUserTree(String userTree) {
		this.userTree = userTree;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApp_flag() {
		return app_flag;
	}
	public void setApp_flag(String app_flag) {
		this.app_flag = app_flag;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/kq/app_check_in/exchange_class/app_exchange".equals(arg0.getPath()) && arg1.getParameter("b_approve")!=null)
        {
           this.getFormHM().put("spFlag","");
        } 
		return super.validate(arg0, arg1);
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	
}
