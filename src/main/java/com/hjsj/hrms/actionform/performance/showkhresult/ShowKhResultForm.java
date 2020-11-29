package com.hjsj.hrms.actionform.performance.showkhresult;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowKhResultForm extends FrameForm {
	/*
	 * 分页显示
	 */
	private String sql;
	private String where;
	private String column;
	private PaginationForm pageListForm = new PaginationForm();
	private String orderby;
	private ArrayList fieldlist;
	private ArrayList objectList;
	private ArrayList mainbodyList;
	private String orm;
	private String sod;
	/*
	 * 传入Arraylist用于显示各项考核指标
	 * 及其他控制字段
	 * 计划id
	 * 标示flag
	 */
	private ArrayList guildlist=new ArrayList();
	private String  plan_id;
	private String flag;
	/*
	 * 考核计划select 
	 */
	private String objectname,ocname,odname,mainbodyname,mcname,mdname;
	private String selstr;
	/*
	 * 考核指标名称,id
	 */
	private ArrayList MOlist;
	private ArrayList pointname;
	private String sbpoint;
	private String header;
	/**是否显示扣分原因列=0不显示=1显示*/
	private String isShowDeductMark="0";
	/**=ALL全部计划，=UN单位，=UM部门，=@K个人*/
	private String modelType;
	private ArrayList deductMarkReasonsList = new ArrayList();
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap formhm=this.getFormHM();
		this.setDeductMarkReasonsList((ArrayList)this.getFormHM().get("deductMarkReasonsList"));
		this.setPlan_id((String)formhm.get("plan_id"));
		this.setModelType((String)formhm.get("modelType"));
		this.setIsShowDeductMark((String)formhm.get("isShowDeductMark"));
		this.setSql((String) formhm.get("sql"));
		this.setColumn((String)formhm.get("column"));
		this.setWhere((String) formhm.get("where"));
		this.setSelstr((String) formhm.get("selstr"));
		this.setOrderby((String)formhm.get("orderby"));
		this.setPointname((ArrayList)formhm.get("pointname"));
		this.setFieldlist((ArrayList) formhm.get("fieldlist"));
		this.setGuildlist((ArrayList)formhm.get("guildlist"));
		this.setObjectList((ArrayList)formhm.get("objectList"));
		this.setMainbodyList((ArrayList)formhm.get("mainbodyList"));
		this.setSod((String)formhm.get("sod"));
		this.setOrm((String)formhm.get("orm"));
		this.setObjectname((String) formhm.get("objectname"));
		this.setOcname((String) formhm.get("ocname"));
		this.setOdname((String) formhm.get("odname"));
		this.setMainbodyname((String) formhm.get("mainbodyname"));
		this.setMcname((String) formhm.get("mcname"));
		this.setMdname((String) formhm.get("mdname"));
		this.setMOlist((ArrayList) formhm.get("molist"));
		this.setHeader((String)formhm.get("header"));
		this.setFlag((String)formhm.get("flag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap formhm=this.getFormHM();
		formhm.put("deductMarkReasonsList", this.getDeductMarkReasonsList());
		formhm.put("modelType", this.getModelType());
		formhm.put("isShowDeductMark", this.getIsShowDeductMark());
		formhm.put("guildlist",this.getGuildlist());
		formhm.put("flag",this.getFlag());
		formhm.put("plan_id",this.getPlan_id());
		formhm.put("orm",this.getOrm());

	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/performance/showkhresult/objectkh".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/performance/showkhresult/mainbodykh".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		/*if(arg0.getPath().equals("/gz/templateset/tax_table/initTaxTable")&&(arg1.getParameter("b_init")!=null))
		{
			if(this.getTaxListForm()!=null)
				this.getTaxListForm().getPagination().firstPage();
		}*/
		return super.validate(arg0, arg1);
	}
	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public ArrayList getGuildlist() {
		return guildlist;
	}

	public void setGuildlist(ArrayList guildlist) {
		this.guildlist = guildlist;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getSelstr() {
		return selstr;
	}

	public void setSelstr(String selstr) {
		this.selstr = selstr;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public ArrayList getPointname() {
		return pointname;
	}

	public void setPointname(ArrayList pointname) {
		this.pointname = pointname;
	}

	public String getSbpoint() {
		return sbpoint;
	}

	public void setSbpoint(String sbpoint) {
		this.sbpoint = sbpoint;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getMainbodyList() {
		return mainbodyList;
	}

	public void setMainbodyList(ArrayList mainbodyList) {
		this.mainbodyList = mainbodyList;
	}

	public ArrayList getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList objectList) {
		this.objectList = objectList;
	}

	public String getOrm() {
		return orm;
	}

	public void setOrm(String orm) {
		this.orm = orm;
	}

	public String getSod() {
		return sod;
	}

	public void setSod(String sod) {
		this.sod = sod;
	}

	public String getMainbodyname() {
		return mainbodyname;
	}

	public void setMainbodyname(String mainbodyname) {
		this.mainbodyname = mainbodyname;
	}

	public String getMcname() {
		return mcname;
	}

	public void setMcname(String mcname) {
		this.mcname = mcname;
	}

	public String getMdname() {
		return mdname;
	}

	public void setMdname(String mdname) {
		this.mdname = mdname;
	}

	public String getObjectname() {
		return objectname;
	}

	public void setObjectname(String objectname) {
		this.objectname = objectname;
	}

	public String getOcname() {
		return ocname;
	}

	public void setOcname(String ocname) {
		this.ocname = ocname;
	}

	public String getOdname() {
		return odname;
	}

	public void setOdname(String odname) {
		this.odname = odname;
	}

	public ArrayList getMOlist() {
		return MOlist;
	}

	public void setMOlist(ArrayList olist) {
		MOlist = olist;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getIsShowDeductMark() {
		return isShowDeductMark;
	}

	public void setIsShowDeductMark(String isShowDeductMark) {
		this.isShowDeductMark = isShowDeductMark;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public ArrayList getDeductMarkReasonsList() {
		return deductMarkReasonsList;
	}

	public void setDeductMarkReasonsList(ArrayList deductMarkReasonsList) {
		this.deductMarkReasonsList = deductMarkReasonsList;
	}

}
