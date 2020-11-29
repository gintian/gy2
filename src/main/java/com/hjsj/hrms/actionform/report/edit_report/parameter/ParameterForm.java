package com.hjsj.hrms.actionform.report.edit_report.parameter;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class ParameterForm extends FrameForm {
    int num = 0;
    
	String tabid;
	
	String status;

	String paramscope;

	String paramname;
	
	String paramename;

	String paramtype;
	
	String tsortid;
	
	String operateObject;
	
	String unitcode;
	String flag="";

	private RecordVo tparam_vo = new RecordVo("tparam");

	private PaginationForm paramForm = new PaginationForm();

	public PaginationForm getParamForm() {
		return paramForm;
	}

	public void setParamForm(PaginationForm paramForm) {
		this.paramForm = paramForm;
	}

	public String getParamname() {
		return paramname;
	}

	public void setParamname(String paramname) {
		this.paramname = paramname;
	}

	public String getParamtype() {
		return paramtype;
	}

	public void setParamtype(String paramtype) {
		this.paramtype = paramtype;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public ParameterForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
    public void outPutFormHM() {
		this.getParamForm().setList(
				(ArrayList) this.getFormHM().get("mylist"));
		
		this.setTabid((String) getFormHM().get("tabid"));
		this.setParamscope((String) getFormHM().get("paramscope"));
		Integer i = (Integer)getFormHM().get("num");
		this.setNum(i.intValue());
		this.setTsortid((String) getFormHM().get("tsortid"));
		this.setFlag((String)this.getFormHM().get("flag"));
	}

	@Override
    public void inPutTransHM() {
		if (paramscope == null) {
			paramscope = "0";
		}
		getFormHM().put("operateObject", operateObject);
		getFormHM().put("tabid", tabid);
		getFormHM().put("paramscope", paramscope);
		getFormHM().put("unitcode", unitcode);
		this.getFormHM().put("flag", this.getFlag());
	}

	public String getParamscope() {
		return paramscope;
	}

	public void setParamscope(String paramscope) {
		this.paramscope = paramscope;
	}

	public RecordVo getTparam_vo() {
		return tparam_vo;
	}

	public void setTparam_vo(RecordVo tparam_vo) {
		this.tparam_vo = tparam_vo;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getParamename() {
		return paramename;
	}

	public void setParamename(String paramename) {
		this.paramename = paramename;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTsortid() {
		return tsortid;
	}

	public void setTsortid(String tsortid) {
		this.tsortid = tsortid;
	}

	public String getOperateObject() {
		return operateObject;
	}

	public void setOperateObject(String operateObject) {
		this.operateObject = operateObject;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
