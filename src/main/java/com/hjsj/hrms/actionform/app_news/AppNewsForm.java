package com.hjsj.hrms.actionform.app_news;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.ArrayList;

public class AppNewsForm extends FrameForm {

	private String constant;
	private String inceptname;
	private Date sendtiem;
	private String title;
	private String days;
	private String state; 
	private FormFile newsfile;
	private String disposals="0" ;
	private String type;
	private String sendtime;
	private String news_id;
	private PaginationForm roleListForm=new PaginationForm();
	private PaginationForm roleListForm2=new PaginationForm();
	private PaginationForm affixListForm = new PaginationForm();
	private String sendtimeto;
	private String isdraft;
	private String affixstr;
	private String affixindex;
	private String objecttype;
	private String inceptnameid;
	private String fileName;



	public String getInceptnameid() {
		return inceptnameid;
	}

	public void setInceptnameid(String inceptnameid) {
		this.inceptnameid = inceptnameid;
	}

	public String getObjecttype() {
		return objecttype;
	}

	public void setObjecttype(String objecttype) {
		this.objecttype = objecttype;
	}

	public String getAffixindex() {
		return affixindex;
	}

	public void setAffixindex(String affixindex) {
		this.affixindex = affixindex;
	}

	public String getAffixstr() {
		return affixstr;
	}

	public void setAffixstr(String affixstr) {
		this.affixstr = affixstr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisposals() {
		return disposals;
	}

	public void setDisposals(String disposals) {
		this.disposals = disposals;
	}

	public FormFile getNewsfile() {
		return newsfile;
	}

	public void setNewsfile(FormFile newsfile) {
		this.newsfile = newsfile;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInceptname() {
		return inceptname;
	}

	public void setInceptname(String inceptname) {
		this.inceptname = inceptname;
	}

	public Date getSendtiem() {
		return sendtiem;
	}

	public void setSendtiem(Date sendtiem) {
		this.sendtiem = sendtiem;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getConstant() {
		return constant;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setConstant((String)this.getFormHM().get("constant"));
		this.setDisposals((String)this.getFormHM().get("disposals"));
		this.setInceptname((String)this.getFormHM().get("inceptname"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setDays((String)this.getFormHM().get("days"));
		this.setType((String)this.getFormHM().get("type"));
		this.setSendtime((String)this.getFormHM().get("sendtime"));
		this.setNews_id((String)this.getFormHM().get("news_id"));
		this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.getRoleListForm2().setList((ArrayList)this.getFormHM().get("rolelist2"));
		this.setState((String)this.getFormHM().get("state"));
		this.setSendtimeto((String)this.getFormHM().get("sendtimeto"));
		this.getAffixListForm().setList((ArrayList)this.getFormHM().get("affixlist"));
		this.setIsdraft((String)this.getFormHM().get("isdraft"));
		this.setAffixindex((String)this.getFormHM().get("affixindex"));
		this.setAffixstr((String)this.getFormHM().get("affixstr"));
		this.setObjecttype((String)this.getFormHM().get("objecttype"));
		this.setInceptnameid((String)this.getFormHM().get("inceptnameid"));
		this.setFileName((String)this.getFormHM().get("fileName"));

	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("constant",this.getConstant());
		this.getFormHM().put("inceptname",this.getInceptname());
		this.getFormHM().put("title",this.getTitle());
		this.getFormHM().put("days",this.getDays());
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("newsfile",this.getNewsfile());
		this.getFormHM().put("disposals",this.getDisposals());
		this.getFormHM().put("sendtime",this.getSendtime());
		this.getFormHM().put("news_id",this.getNews_id());
		this.getFormHM().put("selectedlist",this.getRoleListForm().getSelectedList());
		this.getFormHM().put("selectedlist",this.getRoleListForm2().getSelectedList());
		this.getFormHM().put("sendtimeto",this.getSendtimeto());
		this.getFormHM().put("affixsellist",this.getAffixListForm().getSelectedList());
		this.getFormHM().put("isdraft",this.getIsdraft());
		this.getFormHM().put("inceptnameid",this.getInceptnameid());
		this.getFormHM().put("objecttype",this.getObjecttype());
		this.getFormHM().put("fileName",this.getFileName());

	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/selfservice/app_news/appmessage".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.getRoleListForm().getPagination()!=null)
              this.getRoleListForm().getPagination().firstPage();//?
        }
		if("/selfservice/app_news/appmessage2".equals(arg0.getPath())&&arg1.getParameter("b_query2")!=null)
        {
            if(this.getRoleListForm2().getPagination()!=null)
              this.getRoleListForm2().getPagination().firstPage();//?
        }
		return super.validate(arg0, arg1);
	}

	public String getTitle() {
		return title;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public PaginationForm getRoleListForm() {
		return roleListForm;
	}

	public void setRoleListForm(PaginationForm roleListForm) {
		this.roleListForm = roleListForm;
	}

	public String getNews_id() {
		return news_id;
	}

	public void setNews_id(String news_id) {
		this.news_id = news_id;
	}

	public String getSendtimeto() {
		return sendtimeto;
	}

	public void setSendtimeto(String sendtimeto) {
		this.sendtimeto = sendtimeto;
	}

	public PaginationForm getAffixListForm() {
		return affixListForm;
	}

	public void setAffixListForm(PaginationForm affixListForm) {
		this.affixListForm = affixListForm;
	}

	public String getIsdraft() {
		return isdraft;
	}

	public void setIsdraft(String isdraft) {
		this.isdraft = isdraft;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public PaginationForm getRoleListForm2() {
		return roleListForm2;
	}

	public void setRoleListForm2(PaginationForm roleListForm2) {
		this.roleListForm2 = roleListForm2;
	}

}
