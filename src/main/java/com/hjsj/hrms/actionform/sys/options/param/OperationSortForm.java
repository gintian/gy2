package com.hjsj.hrms.actionform.sys.options.param;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class OperationSortForm extends FrameForm {
	private PaginationForm roleListForm=new PaginationForm();
	private String sortname;
	private String operationid;
	private String errmes;
	private String modulevalue;
	
	private String[] typestr;

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.setSortname((String)this.getFormHM().get("sortname"));
		this.setErrmes((String)this.getFormHM().get("errmes"));
		this.setModulevalue((String)this.getFormHM().get("modulevalue"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist",this.getRoleListForm().getSelectedList());
		this.getFormHM().put("sortname",this.getSortname());
		this.getFormHM().put("operationid",this.getOperationid());
		this.getFormHM().put("typestr",this.getTypestr());
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.typestr=new String[0];
	}

	public String getSortname() {
		return sortname;
	}

	public void setSortname(String sortname) {
		this.sortname = sortname;
	}

	public PaginationForm getRoleListForm() {
		return roleListForm;
	}

	public void setRoleListForm(PaginationForm roleListForm) {
		this.roleListForm = roleListForm;
	}

	public String getOperationid() {
		return operationid;
	}

	public void setOperationid(String operationid) {
		this.operationid = operationid;
	}

	public String getErrmes() {
		return errmes;
	}

	public void setErrmes(String errmes) {
		this.errmes = errmes;
	}

	public String getModulevalue() {
		return modulevalue;
	}

	public void setModulevalue(String modulevalue) {
		this.modulevalue = modulevalue;
	}

	public String[] getTypestr() {
		return typestr;
	}

	public void setTypestr(String[] typestr) {
		this.typestr = typestr;
	}

}
