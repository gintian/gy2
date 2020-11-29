package com.hjsj.hrms.module.recruitment.headhuntermanage.actionform;

import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class HeadHunterGroupForm extends FrameForm {
	
	String sqlstr;
	String fields;
	ArrayList groupcolumns;
	String constantxml;
	
	String huntergroupid;
	
	ArrayList usercolumns;
	ArrayList datalist;
	String subType;
	/*猎头渠道分页信息*/
	private Pageable pageable=new Pageable();
	/**猎头账号分页信息*/
	private Pageable counterpageable=new Pageable();
	
	ArrayList buttons;
	
	String showPublicPlan;
	
	String isAnalyse;

	
	ArrayList editColumns;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("huntergroupid", huntergroupid);
		this.getFormHM().put("groupcolumns", groupcolumns);
		this.getFormHM().put("usercolumns", usercolumns);
		this.getFormHM().put("subType", subType);
	}

	@Override
    public void outPutFormHM() {
		HashMap hm = this.getFormHM();
		this.setSqlstr((String)hm.get("sqlstr"));
		this.setFields((String)hm.get("fields"));
		this.setGroupcolumns((ArrayList)hm.get("groupcolumns"));
		this.setUsercolumns((ArrayList)hm.get("usercolumns"));
		this.setConstantxml((String)hm.get("constantxml"));
		this.setDatalist((ArrayList)hm.get("datalist"));
		this.setSubType((String)hm.get("subType"));
		this.setButtons((ArrayList)hm.get("buttons"));
		this.setShowPublicPlan((String)hm.get("showPublicPlan"));
		this.setIsAnalyse((String)hm.get("isAnalyse"));
		this.setEditColumns((ArrayList)hm.get("editColumns"));
		hm=null;
	}
	
	

	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		 if("/recruitment/headhuntermanage/searchheadhuntergroup".equals(mapping.getPath())&&"insert".equals(request.getParameter("save$Type")))
		    {
			 //定位到末页
			          this.pageable.goEndPage();
		    }
		 if("/recruitment/headhuntermanage/searchheadhuntergroup".equals(mapping.getPath())&&"1".equals(request.getParameter("init")))
		    {
			 //menu菜单进入定位到首页
			          this.pageable.goFirstPage();
			          this.pageable.setPageSize(20);
		    }
		return super.validate(mapping, request);
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}


	public ArrayList getGroupcolumns() {
		return groupcolumns;
	}

	public void setGroupcolumns(ArrayList groupcolumns) {
		this.groupcolumns = groupcolumns;
	}

	public ArrayList getUsercolumns() {
		return usercolumns;
	}

	public void setUsercolumns(ArrayList usercolumns) {
		this.usercolumns = usercolumns;
	}

	public String getConstantxml() {
		return constantxml;
	}

	public void setConstantxml(String constantxml) {
		this.constantxml = constantxml;
	}

	public String getHuntergroupid() {
		return huntergroupid;
	}

	public void setHuntergroupid(String huntergroupid) {
		this.huntergroupid = huntergroupid;
	}

	public ArrayList getDatalist() {
		return datalist;
	}

	public void setDatalist(ArrayList datalist) {
		this.datalist = datalist;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public Pageable getCounterpageable() {
		return counterpageable;
	}

	public void setCounterpageable(Pageable counterpageable) {
		this.counterpageable = counterpageable;
	}

	public ArrayList getButtons() {
		return buttons;
	}

	public void setButtons(ArrayList buttons) {
		this.buttons = buttons;
	}

	public String getShowPublicPlan() {
		return showPublicPlan;
	}

	public void setShowPublicPlan(String showPublicPlan) {
		this.showPublicPlan = showPublicPlan;
	}

	public String getIsAnalyse() {
		return isAnalyse;
	}

	public void setIsAnalyse(String isAnalyse) {
		this.isAnalyse = isAnalyse;
	}

	public ArrayList getEditColumns() {
		return editColumns;
	}

	public void setEditColumns(ArrayList editColumns) {
		this.editColumns = editColumns;
	}

	
	
}
