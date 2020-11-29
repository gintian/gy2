package com.hjsj.hrms.actionform.sys.options.param;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SysInfosortForm extends FrameForm {

	private String tag = "set_a";
	private PaginationForm roleListForm=new PaginationForm();
	private String sortname;
	private String errmes;
	private String left_fields[];
	private String right_fields[];
	private ArrayList subclasslist = new ArrayList();
	private ArrayList selectsubclass = new ArrayList();
	private String tagname;
	private String mess;
	private ArrayList filesetlist = new ArrayList();
	private PaginationForm filesetListForm=new PaginationForm();
	private String[] tagorder;
	private String reworkname;
	private String reworktag;
	private String reworkoldname;
	
	public PaginationForm getRoleListForm() {
		return roleListForm;
	}

	public void setRoleListForm(PaginationForm roleListForm) {
		this.roleListForm = roleListForm;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTag((String)this.getFormHM().get("tag"));
		this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.setSortname((String)this.getFormHM().get("sortname"));
		this.setErrmes((String)this.getFormHM().get("errmes"));
		this.setSubclasslist((ArrayList)this.getFormHM().get("subclasslist"));
		this.setSelectsubclass((ArrayList)this.getFormHM().get("selectsubclass"));
		this.setMess((String)this.getFormHM().get("mess"));
		this.getFilesetListForm().setList((ArrayList)this.getFormHM().get("filesetlists"));
		this.setFilesetlist((ArrayList)this.getFormHM().get("filesetlist"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist",this.getRoleListForm().getSelectedList());
		this.getFormHM().put("tag",this.getTag());
		this.getFormHM().put("sortname",this.getSortname());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("tagname",this.getTagname());
		this.getFormHM().put("filesetsellist",this.getFilesetListForm().getSelectedList());
		this.getFormHM().put("tagorder",this.getTagorder());
		this.getFormHM().put("reworkname",this.getReworkname());
		this.getFormHM().put("reworktag",this.getReworktag());
		this.getFormHM().put("reworkoldname",this.getReworkoldname());
	}

	public String getSortname() {
		return sortname;
	}

	public void setSortname(String sortname) {
		this.sortname = sortname;
	}

	public String getErrmes() {
		return errmes;
	}

	public void setErrmes(String errmes) {
		this.errmes = errmes;
	}

	public ArrayList getSelectsubclass() {
		return selectsubclass;
	}

	public void setSelectsubclass(ArrayList selectsubclass) {
		this.selectsubclass = selectsubclass;
	}

	public ArrayList getSubclasslist() {
		return subclasslist;
	}

	public void setSubclasslist(ArrayList subclasslist) {
		this.subclasslist = subclasslist;
	}

	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public PaginationForm getFilesetListForm() {
		return filesetListForm;
	}

	public void setFilesetListForm(PaginationForm filesetListForm) {
		this.filesetListForm = filesetListForm;
	}

	public ArrayList getFilesetlist() {
		return filesetlist;
	}

	public void setFilesetlist(ArrayList filesetlist) {
		this.filesetlist = filesetlist;
	}

	public String[] getTagorder() {
		return tagorder;
	}

	public void setTagorder(String[] tagorder) {
		this.tagorder = tagorder;
	}

	public String getReworkname() {
		return reworkname;
	}

	public void setReworkname(String reworkname) {
		this.reworkname = reworkname;
	}

	public String getReworktag() {
		return reworktag;
	}

	public void setReworktag(String reworktag) {
		this.reworktag = reworktag;
	}

	public String getReworkoldname() {
		return reworkoldname;
	}

	public void setReworkoldname(String reworkoldname) {
		this.reworkoldname = reworkoldname;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/system/param/sysinfosort".equals(arg0.getPath())&&arg1.getParameter("b_addsort")!=null){//必须切换成第一页，否则不显示指标     wangb 20190518 bug 44157
			this.getRoleListForm().getPagination().setCurrent(1);
		}
	    this.setErrmes("");
        this.getFormHM().put("errmes","");
	    return super.validate(arg0, arg1);
	}

	
	
}
