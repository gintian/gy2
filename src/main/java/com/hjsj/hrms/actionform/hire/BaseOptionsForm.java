/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
/**
 * <p>Title:BaseOptionsForm</p>
 * <p>Description:设置人才库参数表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class BaseOptionsForm extends FrameForm {
	/**
     * 当前人员库页标识
     */
    private String current_tab="dbpriv";
    /**
     * 生成的html标识串
     */
    private String script_str;
    /**
     * 存放子集或指标权限串
     */
    private String field_set_str;
    //前台列表显示指标
    private String show_field_str;
    //必填项
    private String mustFill_field_str;
    
    
    /**
     * 功能列表
     */
    private String[] func;  
    /**管理范围代码UN0001*/
    private String org;
    
	private String userBase = "Usr";
	
	private String dbpre = "";
	
	private String strsql = "";
	
	private ArrayList subSetList = new ArrayList();
	
	private ArrayList fieldItemList = new ArrayList();
	
	private String[] fieldsetvalue=null;
	
	private String[] fielditemvalue = null;
	/**
	 * @return Returns the fieldsetvalue.
	 */
	public String[] getFieldsetvalue() {
		return fieldsetvalue;
	}
	/**
	 * @param fieldsetvalue The fieldsetvalue to set.
	 */
	public void setFieldsetvalue(String[] fieldsetvalue) {
		this.fieldsetvalue = fieldsetvalue;
	}
	/**
	 * 子集对象
	 */
	private RecordVo subSetvo = new RecordVo("Constant");

	/**
	 *子集对象列表
	 */
	private PaginationForm subSetForm = new PaginationForm();
	/**唯一性校验指标串*/
	private String func_only="";
	@Override
    public void outPutFormHM() {
		this.setFunc_only((String)this.getFormHM().get("func_only"));
		this.setSubSetvo((RecordVo) this.getFormHM().get("subSetvo"));
		this.getSubSetForm().setList((ArrayList) this.getFormHM().get("subSetlist"));
		this.setUserBase((String)this.getFormHM().get("userBase"));
		this.setSubSetList((ArrayList)this.getFormHM().get("subSetList"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setFieldsetvalue((String[])this.getFormHM().get("fieldsetvalue"));
		this.setFieldItemList((ArrayList)this.getFormHM().get("fieldItemList"));
		this.setFielditemvalue((String[])this.getFormHM().get("fielditemvalue"));
		this.setCurrent_tab((String)this.getFormHM().get("tab_name"));
		this.setScript_str((String)this.getFormHM().get("script_str"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("func_only",this.getFunc_only());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getSubSetForm().getSelectedList());
		this.getFormHM().put("subSetvo", this.getSubSetvo());
		this.getFormHM().put("userBase",this.getUserBase());
		this.getFormHM().put("subSetList",this.getSubSetList());
		this.getFormHM().put("fieldsetvalue",fieldsetvalue);
		this.getFormHM().put("fielditemvalue",fielditemvalue);
		this.getFormHM().put("field_set_str",this.getField_set_str());
		this.getFormHM().put("func",this.getFunc());
		this.getFormHM().put("org",this.getOrg());
		
		this.getFormHM().put("show_field_str",this.getShow_field_str());
		this.getFormHM().put("mustFill_field_str",this.getMustFill_field_str());
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
    	
        super.reset(arg0, arg1);
        
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        
        return super.validate(arg0, arg1);
    }

	/**
	 * @return Returns the subSetForm.
	 */
	public PaginationForm getSubSetForm() {
		return subSetForm;
	}
	/**
	 * @param subSetForm The subSetForm to set.
	 */
	public void setSubSetForm(PaginationForm subSetForm) {
		this.subSetForm = subSetForm;
	}
	/**
	 * @return Returns the subSetvo.
	 */
	public RecordVo getSubSetvo() {
		return subSetvo;
	}
	/**
	 * @param subSetvo The subSetvo to set.
	 */
	public void setSubSetvo(RecordVo subSetvo) {
		this.subSetvo = subSetvo;
	}
	/**
	 * @return Returns the userBase.
	 */
	public String getUserBase() {
		return userBase;
	}
	/**
	 * @param userBase The userBase to set.
	 */
	public void setUserBase(String userBase) {
		this.userBase = userBase;
	}
	/**
	 * @return Returns the subSetList.
	 */
	public List getSubSetList() {
		return subSetList;
	}
	/**
	 * @param subSetList The subSetList to set.
	 */
	public void setSubSetList(ArrayList subSetList) {
		this.subSetList = subSetList;
	}
	/**
	 * @return Returns the dbpre.
	 */
	public String getDbpre() {
		return dbpre;
	}
	/**
	 * @param dbpre The dbpre to set.
	 */
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
	/**
	 * @return Returns the strsql.
	 */
	public String getStrsql() {
		return strsql;
	}
	/**
	 * @param strsql The strsql to set.
	 */
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	/**
	 * @return Returns the fieldItemList.
	 */
	public ArrayList getFieldItemList() {
		return fieldItemList;
	}
	/**
	 * @param fieldItemList The fieldItemList to set.
	 */
	public void setFieldItemList(ArrayList fieldItemList) {
		this.fieldItemList = fieldItemList;
	}
	/**
	 * @return Returns the fielditemvalue.
	 */
	public String[] getFielditemvalue() {
		return fielditemvalue;
	}
	/**
	 * @param fielditemvalue The fielditemvalue to set.
	 */
	public void setFielditemvalue(String[] fielditemvalue) {
		this.fielditemvalue = fielditemvalue;
	}
	/**
	 * @return Returns the current_tab.
	 */
	public String getCurrent_tab() {
		return current_tab;
	}
	/**
	 * @param current_tab The current_tab to set.
	 */
	public void setCurrent_tab(String current_tab) {
		this.current_tab = current_tab;
	}
	/**
	 * @return Returns the script_str.
	 */
	public String getScript_str() {
		return script_str;
	}
	/**
	 * @param script_str The script_str to set.
	 */
	public void setScript_str(String script_str) {
		this.script_str = script_str;
	}
	/**
	 * @return Returns the field_set_str.
	 */
	public String getField_set_str() {
		return field_set_str;
	}
	/**
	 * @param field_set_str The field_set_str to set.
	 */
	public void setField_set_str(String field_set_str) {
		this.field_set_str = field_set_str;
	}
	/**
	 * @return Returns the func.
	 */
	public String[] getFunc() {
		return func;
	}
	/**
	 * @param func The func to set.
	 */
	public void setFunc(String[] func) {
		this.func = func;
	}
	/**
	 * @return Returns the org.
	 */
	public String getOrg() {
		return org;
	}
	/**
	 * @param org The org to set.
	 */
	public void setOrg(String org) {
		this.org = org;
	}
	public String getMustFill_field_str() {
		return mustFill_field_str;
	}
	public void setMustFill_field_str(String mustFill_field_str) {
		this.mustFill_field_str = mustFill_field_str;
	}
	public String getShow_field_str() {
		return show_field_str;
	}
	public void setShow_field_str(String show_field_str) {
		this.show_field_str = show_field_str;
	}
	public String getFunc_only() {
		return func_only;
	}
	public void setFunc_only(String func_only) {
		this.func_only = func_only;
	}
}
