package com.hjsj.hrms.actionform.sys.export;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ExportForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	 private String sql;
	 private String where;
	 private String column;
	 private String orderby;
	 private PaginationForm pageListForm = new PaginationForm();

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
	
	private String status_flag ;
	private String trigger="" ;
	
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
	
	/**
	 * 实现的作业类对象
	 */
	private RecordVo jobsvo = new RecordVo("t_sys_jobs");
	
	private String code ;
	private String field ;
	private String transcode ;
	private String strtoutf ;
	
	private String path;
	
	@Override
    public void outPutFormHM() {
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setSubSetvo((RecordVo) this.getFormHM().get("subSetvo"));
		this.setJobsvo((RecordVo) this.getFormHM().get("jobsvo"));
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
		this.setCode((String)this.getFormHM().get("code"));
		this.setField((String)this.getFormHM().get("field"));
		this.setStrtoutf((String)this.getFormHM().get("strtoutf"));
		this.setTranscode((String)this.getFormHM().get("transcode"));
		this.setStatus_flag((String)this.getFormHM().get("status_flag"));
		this.setTrigger((String)this.getFormHM().get("trigger"));
		this.setPath((String)this.getFormHM().get("path"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		if(this.getPagination()!=null)
			this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getSubSetForm().getSelectedList());
		this.getFormHM().put("subSetvo", this.getSubSetvo());
		this.getFormHM().put("jobsvo", this.getJobsvo());
		this.getFormHM().put("userBase",this.getUserBase());
		this.getFormHM().put("subSetList",this.getSubSetList());
		this.getFormHM().put("fieldsetvalue",fieldsetvalue);
		this.getFormHM().put("fielditemvalue",fielditemvalue);
		this.getFormHM().put("field_set_str",this.getField_set_str());
		this.getFormHM().put("func",this.getFunc());
		this.getFormHM().put("org",this.getOrg());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("field",this.getField());
		this.getFormHM().put("strtoutf",this.getStrtoutf());
		this.getFormHM().put("transcode",this.getTranscode());
		this.getFormHM().put("status_flag",this.getStatus_flag());
		this.getFormHM().put("path",this.getPath());
		this.getFormHM().put("trigger", this.getTrigger());
	}
	
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
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
	 * @return Returns the jobsVo.
	 */
	public RecordVo getJobsvo() {
		return jobsvo;
	}
	/**
	 * @param jobsVo The jobsVo to set.
	 */
	public void setJobsvo(RecordVo jobsvo) {
		this.jobsvo = jobsvo;
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
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	public String getStatus_flag() {
		return status_flag;
	}

	public void setStatus_flag(String status_flag) {
		this.status_flag = status_flag;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getStrtoutf() {
		return strtoutf;
	}
	public void setStrtoutf(String strtoutf) {
		this.strtoutf = strtoutf;
	}
	public String getTranscode() {
		return transcode;
	}
	public void setTranscode(String transcode) {
		this.transcode = transcode;
	}
}
