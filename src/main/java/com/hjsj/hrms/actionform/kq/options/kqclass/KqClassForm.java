package com.hjsj.hrms.actionform.kq.options.kqclass;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqClassForm extends FrameForm 
{
	private String class_id;
	
	private RecordVo kq_class=new RecordVo("kq_class");
	private String class_name;
	private String class_flag;
	private String onduty_card_1;
	private ArrayList overlist=new ArrayList();
	private PaginationForm kqClassForm=new PaginationForm();
	private String save_flag;
	private String[] kqlist;
	private String priv_selected="";//班次授权
	private String returnvalue="1";
	private String orgId;
	private String orgName;
	private String classType;
	private String changePublicRight;
	public String getSave_flag() {
		return save_flag;
	}
	public void setSave_flag(String save_flag) {
		this.save_flag = save_flag;
	}
	@Override
    public void outPutFormHM()
	{
		this.setClass_id((String)this.getFormHM().get("class_id"));
		this.setOrgId((String)this.getFormHM().get("orgId"));
		this.setOrgName((String)this.getFormHM().get("orgName"));
		this.setClass_name((String)this.getFormHM().get("class_name"));
		this.setClass_flag((String)this.getFormHM().get("class_flag"));
		this.setKq_class((RecordVo)this.getFormHM().get("class_vo"));
		this.setSave_flag((String)this.getFormHM().get("save_flag"));
	    this.setOverlist((ArrayList)this.getFormHM().get("overlist"));
	    this.setKqlist((String[])this.getFormHM().get("kqlist"));
	    this.setPriv_selected((String)this.getFormHM().get("priv_selected"));
	    this.setClassType((String)this.getFormHM().get("classType"));
	    this.setChangePublicRight((String)this.getFormHM().get("changePublicRight"));
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
	    if ("/kq/options/class/kq_class_data".equals(arg0.getPath()) && StringUtils.isEmpty(arg1.getParameter("class_id"))) {
	        this.setClass_id("");
	    }
	}
	
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("orgId",this.getOrgId());
		this.getFormHM().put("class_id",this.getClass_id());
		this.getFormHM().put("orgName",this.getOrgName());
		this.getFormHM().put("class_vo",(RecordVo)this.getKq_class());
		this.getFormHM().put("class_name",this.getClass_name());
		this.getFormHM().put("onduty_card_1",this.getOnduty_card_1());
		this.getFormHM().put("kqlist",this.getKqlist());
		this.getFormHM().put("class_flag", this.getClass_flag());
		this.getFormHM().put("classType", this.getClassType());
		this.getFormHM().put("changePublicRight", this.getChangePublicRight());
    }
	public String getClass_id() {
		return class_id;
	}
	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}	

	public PaginationForm getKqClassForm() {
		return kqClassForm;
	}
	public void setKqClassForm(PaginationForm kqClassForm) {
		this.kqClassForm = kqClassForm;
	}
	public RecordVo getKq_class() {
		return kq_class;
	}
	public void setKq_class(RecordVo kq_class) {
		this.kq_class = kq_class;
	}
	public String getClass_flag() {
		return class_flag;
	}
	public void setClass_flag(String class_flag) {
		this.class_flag = class_flag;
	}
	public String getClass_name() {
		return class_name;
	}
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	public String getOnduty_card_1() {
		return onduty_card_1;
	}
	public void setOnduty_card_1(String onduty_card_1) {
		this.onduty_card_1 = onduty_card_1;
	}
	public ArrayList getOverlist() {
		return overlist;
	}
	public void setOverlist(ArrayList overlist) {
		this.overlist = overlist;
	}
	public String[] getKqlist() {
		return kqlist;
	}
	public void setKqlist(String[] kqlist) {
		this.kqlist = kqlist;
	}
	public String getPriv_selected() {
		return priv_selected;
	}
	public void setPriv_selected(String priv_selected) {
		this.priv_selected = priv_selected;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
    public String getOrgId() {
        return orgId;
    }
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getClassType() {
        return classType;
    }
    public void setClassType(String classType) {
        this.classType = classType;
    }
    public String getChangePublicRight() {
        return changePublicRight;
    }
    public void setChangePublicRight(String changePublicRight) {
        this.changePublicRight = changePublicRight;
    }
	

}
