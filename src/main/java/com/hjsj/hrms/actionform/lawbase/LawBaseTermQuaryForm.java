package com.hjsj.hrms.actionform.lawbase;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class LawBaseTermQuaryForm extends FrameForm {

	private String name;

	private String title;

	private String type;

	private String content_type;

	private String valid;

	private String note_num;

	private String issue_org;

	private String notes;

	private String issue_date_start;

	private String issue_date_end;

	private String implement_date_start;

	private String implement_date_end;

	private String valid_date_start;

	private String valid_date_end;

	private String a_base_id;

	private String basetype;
	
	private String itemid;
	
	private String itemdesc;
	
	private String itemtype;
	
	private String itemvalue;
	
	private String itemCodeid;
	
	
	private PaginationForm paginationForm = new PaginationForm();

	public PaginationForm getPaginationForm() {
		return paginationForm;
	}

	public void setPaginationForm(PaginationForm paginationForm) {
		this.paginationForm = paginationForm;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("base_id", a_base_id);
		this.getFormHM().put("name", name);
		this.getFormHM().put("title", title);
		this.getFormHM().put("type", type);
		this.getFormHM().put("content_type", content_type);
		this.getFormHM().put("valid", valid);
		this.getFormHM().put("note_num", note_num);
		this.getFormHM().put("issue_org", issue_org);
		this.getFormHM().put("notes", notes);
		this.getFormHM().put("issue_date_start", issue_date_start);
		this.getFormHM().put("issue_date_end", issue_date_end);
		this.getFormHM().put("implement_date_start", implement_date_start);
		this.getFormHM().put("implement_date_end", implement_date_end);
		this.getFormHM().put("valid_date_start", valid_date_start);
		this.getFormHM().put("valid_date_end", valid_date_end);
		this.getFormHM().put("basetype", basetype);
		this.getFormHM().put("itemid", itemid);
		this.getFormHM().put("itemdesc", itemdesc);
		this.getFormHM().put("itemtype", itemtype);
		this.getFormHM().put("itemvalue", itemvalue);
		this.getFormHM().put("itemCodeid", itemCodeid);
	}

	public LawBaseTermQuaryForm() {
		super();
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}

		return super.validate(arg0, arg1);

	}

	@Override
    public void outPutFormHM() {
		getPaginationForm().setList((ArrayList) this.getFormHM().get("myList"));
		/*this.setName("");
		this.setNotes("");
		this.setTitle("");
		this.setType("");
		this.setContent_type("");
		this.setValid("");
		this.setNote_num("");
		this.setIssue_org("");
		this.setNotes("");
		this.setIssue_date_start("");
		this.setIssue_date_end("");
		this.setImplement_date_start("");
		this.setIssue_date_end("");
		this.setValid_date_start("");
		this.setValid_date_end("");*/
	}

	public String getA_base_id() {
		return a_base_id;
	}

	public void setA_base_id(String a_base_id) {
		this.a_base_id = a_base_id;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getImplement_date_end() {
		return implement_date_end;
	}

	public void setImplement_date_end(String implement_date_end) {
		this.implement_date_end = implement_date_end;
	}

	public String getImplement_date_start() {
		return implement_date_start;
	}

	public void setImplement_date_start(String implement_date_start) {
		this.implement_date_start = implement_date_start;
	}

	public String getIssue_date_end() {
		return issue_date_end;
	}

	public void setIssue_date_end(String issue_date_end) {
		this.issue_date_end = issue_date_end;
	}

	public String getIssue_date_start() {
		return issue_date_start;
	}

	public void setIssue_date_start(String issue_date_start) {
		this.issue_date_start = issue_date_start;
	}

	public String getIssue_org() {
		return issue_org;
	}

	public void setIssue_org(String issue_org) {
		this.issue_org = issue_org;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote_num() {
		return note_num;
	}

	public void setNote_num(String note_num) {
		this.note_num = note_num;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getValid_date_end() {
		return valid_date_end;
	}

	public void setValid_date_end(String valid_date_end) {
		this.valid_date_end = valid_date_end;
	}

	public String getValid_date_start() {
		return valid_date_start;
	}

	public void setValid_date_start(String valid_date_start) {
		this.valid_date_start = valid_date_start;
	}

	public String getBasetype() {
		return basetype;
	}

	public void setBasetype(String basetype) {
		this.basetype = basetype;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getItemdesc() {
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getItemvalue() {
		return itemvalue;
	}

	public void setItemvalue(String itemvalue) {
		this.itemvalue = itemvalue;
	}

	public String getItemCodeid() {
		return itemCodeid;
	}

	public void setItemCodeid(String itemCodeid) {
		this.itemCodeid = itemCodeid;
	}
	
	
}