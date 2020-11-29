package com.hjsj.hrms.actionform.sys;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class RoledetailForm extends FrameForm {

	/**
     * 显示角色关联人员列表
     */
	private String rolename;
	private String roleid;
    private PaginationForm detailListForm= new PaginationForm();
	@Override
    public void inPutTransHM() {
		
	}

	@Override
    public void outPutFormHM() {
		 this.getDetailListForm().setList((ArrayList)this.getFormHM().get("detailList"));
		 this.getDetailListForm().getPagination().setCurrent(1);
	}

	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		if("/system/security/roledetail".equals(mapping.getPath())&&request.getParameter("b_detailed")!=null){
			request.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
        }
		return super.validate(mapping, request);
	}

	public PaginationForm getDetailListForm() {
		return detailListForm;
	}

	public void setDetailListForm(PaginationForm detailListForm) {
		this.detailListForm = detailListForm;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		rolename=com.hrms.frame.codec.SafeCode.decode(rolename);
		this.rolename = rolename;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

}
