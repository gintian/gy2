package com.hjsj.hrms.actionform.sys.busimaintence;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;
import java.util.HashMap;

public class RelatingcodeForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	  private String sql;
	  private String where;
	  private String column;
	  private String orderby;
	  private PaginationForm pageListForm = new PaginationForm();
	  private RecordVo relatingcodeVo;
	  private String flag;
	  private String codesetid;
	  /**判断是从系统维护进入还是从业务字典维护进入=0系统维护=1业务字典*/
	  private String add_flag;

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSql((String) hm.get("sql"));
		this.setWhere((String) hm.get("where"));
		this.setColumn((String) hm.get("column"));
		this.setOrderby((String)hm.get("orderby"));
		this.setRelatingcodeVo((RecordVo) hm.get("t_hr_relatingcode"));
		this.setFlag((String)hm.get("flag"));
		this.setAdd_flag((String)hm.get("add_flag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		if(this.getPagination()!=null)
			this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
		hm.put("relatingcode",this.getRelatingcodeVo());
		hm.put("codesetid",this.getCodesetid());
		hm.put("add_flag",this.getAdd_flag());
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

	public RecordVo getRelatingcodeVo() {
		return relatingcodeVo;
	}

	public void setRelatingcodeVo(RecordVo relatingcodeVo) {
		this.relatingcodeVo = relatingcodeVo;
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

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getAdd_flag() {
		return add_flag;
	}

	public void setAdd_flag(String add_flag) {
		this.add_flag = add_flag;
	}

}
