package com.hjsj.hrms.actionform.general.deci.leader;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class CandiLeaderForm  extends FrameForm{

	private String a_code;
	private String code;
	private String kind;
	private String select_str;
	private String columns;
	private ArrayList fieldlist=new ArrayList();
	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getSelect_str() {
		return select_str;
	}

	public void setSelect_str(String select_str) {
		this.select_str = select_str;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setKind((String)this.getFormHM().get("kind"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("kind",this.getKind());
	}

}
