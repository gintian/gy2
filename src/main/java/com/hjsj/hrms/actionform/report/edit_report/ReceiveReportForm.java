package com.hjsj.hrms.actionform.report.edit_report;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;

public class ReceiveReportForm extends FrameForm {

	private static final long serialVersionUID = 1L;

	private FormFile file;
	private FormFile file2;

	// 如果flg="1"代表用户选择了XML文件需要返回填报单位的unitcode进入接收页面
	// 如果flg="2"代表用户触发了接收功能
	private String flg;

	private String b_query;

	private String unitname = "";

	private String unitcode = "";

	// 提示信息内容
	private String clew = "";

	private String b_save;

	private String scope;
	private String  editflag=""; 
	private String  editvalide="";
	
	
	public String getEditvalide() {
		return editvalide;
	}

	public void setEditvalide(String editvalide) {
		this.editvalide = editvalide;
	}

	@Override
    public void outPutFormHM() {
		
		clew = (String) this.getFormHM().get("clew");
		if (b_query!=null&&!"".equals(b_query.trim())) {
			clew = "";
		}
		b_save = "";
		b_query = "";
		unitcode = (String) this.getFormHM().get("unitcode");
		unitname = (String) this.getFormHM().get("unitname");
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setEditvalide((String)this.getFormHM().get("editvalide"));
		this.setFile2((FormFile)this.getFormHM().get("file2"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("b_query", this.getB_query());
		this.getFormHM().put("b_save", this.getB_save());
		this.getFormHM().put("scope",this.getScope());
		
		this.getFormHM().put("unitcode", unitcode);
		this.getFormHM().put("unitname", unitname);
		if (b_query!=null&&!"".equals(b_query.trim())) {
			clew = "";
		}
		this.getFormHM().put("editflag", this.getEditflag());
	}

	
	
	public String getB_save() {
		return b_save;
	}

	public void setB_save(String b_save) {
		this.b_save = b_save;
	}

	public ReceiveReportForm() {
		super();
	}

	
	public FormFile getFile() {
		return file;
	}

	public FormFile getFile2() {
		return file2;
	}

	public void setFile2(FormFile file2) {
		this.file2 = file2;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getB_query() {
		return b_query;
	}

	public void setB_query(String b_query) {
		this.b_query = b_query;
	}

	public String getClew() {
		return clew;
	}

	public void setClew(String clew) {
		this.clew = clew;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getEditflag() {
		return editflag;
	}

	public void setEditflag(String editflag) {
		this.editflag = editflag;
	}
	
	

}
