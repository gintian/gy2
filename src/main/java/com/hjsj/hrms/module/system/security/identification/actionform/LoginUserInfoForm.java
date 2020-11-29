package com.hjsj.hrms.module.system.security.identification.actionform;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * <p>Title: LoginUserInfoForm </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-7-6 上午10:12:31</p>
 * @author jingq
 * @version 1.0
 */
public class LoginUserInfoForm extends FrameForm{
	
	private static final long serialVersionUID = 1L;

	private String sqlstr;
	
	private ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
	
	private Pageable pageable = new Pageable();
	
	private String manual;
	
	private String orderby;
	
	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getManual() {
		return manual;
	}

	public void setManual(String manual) {
		this.manual = manual;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public ArrayList<ColumnsInfo> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnsInfo> columns) {
		this.columns = columns;
	}

	@Override
    @SuppressWarnings("unchecked")
	public void outPutFormHM() {
		this.setColumns((ArrayList<ColumnsInfo>) this.getFormHM().get("columns"));
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
		this.setPageable((Pageable) this.getFormHM().get("pageable"));
		this.setManual((String) this.getFormHM().get("manual"));
		this.setOrderby((String) this.getFormHM().get("orderby"));
	}

	@Override
    @SuppressWarnings("unchecked")
	public void inPutTransHM() {
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("sqlstr", this.getSqlstr());
		this.getFormHM().put("pageable", this.getPageable());
		this.getFormHM().put("manual", this.getManual());
		this.getFormHM().put("orderby", this.getOrderby());
	}
	
}
