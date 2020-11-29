/**
 * 
 */
package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *<p>Title:变动对比信息</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-2:下午05:06:13</p> 
 *@author cmq
 *@version 4.0
 */
public class ChangeInfoForm extends FrameForm {
	/**薪资类别号*/
	private String salaryid="-1";
	/**分页标签过滤条件与显示字段列表*/
	private String strsql="";
	private String strwhere="";
	private String columns="";
	/**变动类型
	 * =add      人员增加
	 * =del 	 人员减少
	 * =chginfo  信息变动
	 * =chgA01Z0 停发标识
	 * */
	private String chgtype="0";
	/**页面来源*/
	private String fromflag="0";

	private ArrayList changeTabList=new ArrayList();
	private ArrayList fieldItemList = new ArrayList();
		
	private String filterid;
	private String fieldstr;
	private FieldItem onlyitem;
	private String displayE0122;
	private String addcount;
	private String delcount;
	private String chgcount;
	private String stpcount;
	private String checkall;
	private ArrayList add_delList;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("checkall", this.getCheckall());
		this.getFormHM().put("onlyitem", this.getOnlyitem());
		this.getFormHM().put("filterid",this.getFilterid());
		this.getFormHM().put("fieldstr", this.getFieldstr());
		this.getFormHM().put("salaryid", salaryid);
		this.getFormHM().put("chgtype", chgtype);
		this.getFormHM().put("add_delList", this.getAdd_delList());
	}

	@Override
    public void outPutFormHM() {
		this.setCheckall((String)this.getFormHM().get("checkall"));
		this.setAddcount((String)this.getFormHM().get("addcount"));
		this.setDelcount((String)this.getFormHM().get("delcount"));
		this.setChgcount((String)this.getFormHM().get("chgcount"));
		this.setStpcount((String)this.getFormHM().get("stpcount"));
		this.setDisplayE0122((String)this.getFormHM().get("displayE0122"));
		this.setOnlyitem((FieldItem)this.getFormHM().get("onlyitem"));
		this.setFilterid((String)this.getFormHM().get("filterid"));
		this.setFieldstr((String)this.getFormHM().get("fieldstr"));
		this.setFieldItemList((ArrayList)this.getFormHM().get("fieldItemList"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setChangeTabList((ArrayList)this.getFormHM().get("changeTabList"));
		this.setAdd_delList((ArrayList) this.getFormHM().get("add_delList"));
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getChgtype() {
		return chgtype;
	}

	public void setChgtype(String chgtype) {
		this.chgtype = chgtype;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/gz/gz_accounting/addman".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
        if("/gz/gz_accounting/delman".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }  
        if("/gz/gz_accounting/changeinfo".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }  
        if("/gz/gz_accounting/changeA01Z0".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }         
		return super.validate(arg0, arg1);
	}

	public String getFromflag() {
		return fromflag;
	}

	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}

	public ArrayList getChangeTabList() {
		return changeTabList;
	}

	public void setChangeTabList(ArrayList changeTabList) {
		this.changeTabList = changeTabList;
	}

	public ArrayList getFieldItemList() {
		return fieldItemList;
	}

	public void setFieldItemList(ArrayList fieldItemList) {
		this.fieldItemList = fieldItemList;
	}

	public String getFilterid() {
		return filterid;
	}

	public void setFilterid(String filterid) {
		this.filterid = filterid;
	}

	public String getFieldstr() {
		return fieldstr;
	}

	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
	}

	public FieldItem getOnlyitem() {
		return onlyitem;
	}

	public void setOnlyitem(FieldItem onlyitem) {
		this.onlyitem = onlyitem;
	}

	public String getDisplayE0122() {
		return displayE0122;
	}

	public void setDisplayE0122(String displayE0122) {
		this.displayE0122 = displayE0122;
	}

	public String getAddcount() {
		return addcount;
	}

	public void setAddcount(String addcount) {
		this.addcount = addcount;
	}

	public String getDelcount() {
		return delcount;
	}

	public void setDelcount(String delcount) {
		this.delcount = delcount;
	}

	public String getChgcount() {
		return chgcount;
	}

	public void setChgcount(String chgcount) {
		this.chgcount = chgcount;
	}

	public String getStpcount() {
		return stpcount;
	}

	public void setStpcount(String stpcount) {
		this.stpcount = stpcount;
	}

	public String getCheckall() {
		return checkall;
	}

	public void setCheckall(String checkall) {
		this.checkall = checkall;
	}

	public ArrayList getAdd_delList() {
		return add_delList;
	}

	public void setAdd_delList(ArrayList add_delList) {
		this.add_delList = add_delList;
	}



}
