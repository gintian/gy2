package com.hjsj.hrms.actionform.performance.data_collect;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class Data_collectForm extends FrameForm {
	
	
	private String chz;//对应变量的中文名称
	private String ntype;//对应变量的类型
	private String fldlen;//对应变量的长度
	private String flddec;//对应变量的小数点后的位数
	private String codesetid;//对应变量的代码类型
	private String fieldsetid;//获取设置计算的子集（需要按年月变化）
	private ArrayList auditList;//涉及审批状态
	private ArrayList dbList;//涉及到人员库列表
	private String[] dbid;//已选人员库ID
	private String audit;//审批状态
	private String personScope;
	private String cexpr;
	
	//---------------zhaoxg start---------------
	private String isHave;//判断参数是否设置 1.有 2.无
	private ArrayList dblist;
	private ArrayList filterList;
	private ArrayList spTypeList;
	private ArrayList fieldlist;
	private String yearnum;
	private String _sql;
	private String filtervalue;
	private String spType;
	private String dbname;
	private String tablename;
	private String theyear;
	private String themonth;
	private FormFile formfile;
    private String info;
    private String returnInfo;
    private String[] right_fields;
    private ArrayList formulalist;
    private PaginationForm formulalistform=new PaginationForm();
    private String zt;
    private String ym;

	public String getCexpr() {
		return cexpr;
	}

	public void setCexpr(String cexpr) {
		this.cexpr = cexpr;
	}

	public String getPersonScope() {
		return personScope;
	}

	public void setPersonScope(String personScope) {
		this.personScope = personScope;
	}

	public String getAudit() {
		return audit;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	public String[] getDbid() {
		return dbid;
	}

	public void setDbid(String[] dbid) {
		this.dbid = dbid;
	}

	public ArrayList getDbList() {
		return dbList;
	}

	public void setDbList(ArrayList dbList) {
		this.dbList = dbList;
	}

	public ArrayList getAuditList() {
		return auditList;
	}

	public void setAuditList(ArrayList auditList) {
		this.auditList = auditList;
	}

	
	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String getChz() {
		return chz;
	}

	public void setChz(String chz) {
		this.chz = chz;
	}

	public String getNtype() {
		return ntype;
	}

	public void setNtype(String ntype) {
		this.ntype = ntype;
	}

	public String getFldlen() {
		return fldlen;
	}

	public void setFldlen(String fldlen) {
		this.fldlen = fldlen;
	}

	public String getFlddec() {
		return flddec;
	}

	public void setFlddec(String flddec) {
		this.flddec = flddec;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getIsHave() {
		return isHave;
	}

	public void setIsHave(String isHave) {
		this.isHave = isHave;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public ArrayList getFilterList() {
		return filterList;
	}

	public void setFilterList(ArrayList filterList) {
		this.filterList = filterList;
	}

	public ArrayList getSpTypeList() {
		return spTypeList;
	}

	public void setSpTypeList(ArrayList spTypeList) {
		this.spTypeList = spTypeList;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getYearnum() {
		return yearnum;
	}

	public void setYearnum(String yearnum) {
		this.yearnum = yearnum;
	}

	public String get_sql() {
		return _sql;
	}

	public void set_sql(String _sql) {
		this._sql = _sql;
	}

	public String getFiltervalue() {
		return filtervalue;
	}

	public void setFiltervalue(String filtervalue) {
		this.filtervalue = filtervalue;
	}

	public String getSpType() {
		return spType;
	}

	public void setSpType(String spType) {
		this.spType = spType;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getTheyear() {
		return theyear;
	}

	public void setTheyear(String theyear) {
		this.theyear = theyear;
	}

	public String getThemonth() {
		return themonth;
	}

	public void setThemonth(String themonth) {
		this.themonth = themonth;
	}

	public FormFile getFormfile() {
		return formfile;
	}

	public void setFormfile(FormFile formfile) {
		this.formfile = formfile;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getFormulalist() {
		return formulalist;
	}

	public void setFormulalist(ArrayList formulalist) {
		this.formulalist = formulalist;
	}

	public PaginationForm getFormulalistform() {
		return formulalistform;
	}

	public void setFormulalistform(PaginationForm formulalistform) {
		this.formulalistform = formulalistform;
	}


	public String getZt() {
		return zt;
	}

	public void setZt(String zt) {
		this.zt = zt;
	}

	public String getYm() {
		return ym;
	}

	public void setYm(String ym) {
		this.ym = ym;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("dbid",this.getDbid());
		this.getFormHM().put("audit",this.getAudit());
		this.getFormHM().put("cexpr",this.getCexpr());
		this.getFormHM().put("personScope", this.getPersonScope());
		this.getFormHM().put("isHave", this.getIsHave());
		this.getFormHM().put("_sql", this.get_sql());
		this.getFormHM().put("yearnum", this.getYearnum());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("spTypeList", this.getSpTypeList());
		this.getFormHM().put("filterList", this.getFilterList());
		this.getFormHM().put("dblist", this.getDblist());
		this.getFormHM().put("tablename", this.getTablename());
		this.getFormHM().put("theyear", this.getTheyear());
		this.getFormHM().put("themonth", this.getThemonth());
		this.getFormHM().put("dbname", this.getDbname());
		this.getFormHM().put("spType", this.getSpType());
		this.getFormHM().put("filtervalue", this.getFiltervalue());
		this.getFormHM().put("formfile",this.getFormfile());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("formulalist", this.getFormulalist());
		this.getFormHM().put("selectedList", this.getFormulalistform().getSelectedList());
		this.getFormHM().put("zt", this.getZt());
		this.getFormHM().put("fieldsetid", this.getFieldsetid());
		this.getFormHM().put("ym", this.getYm());
	}


	@Override
    public void outPutFormHM() {
		this.setChz((String) this.getFormHM().get("chz"));
		this.setCodesetid((String) this.getFormHM().get("codesetid"));
		this.setFlddec((String) this.getFormHM().get("flddec"));
		this.setFldlen((String) this.getFormHM().get("fldlen"));
		this.setNtype((String) this.getFormHM().get("ntype"));
		this.setAuditList((ArrayList) this.getFormHM().get("auditList"));
		this.setDbid((String[]) this.getFormHM().get("dbid"));
		this.setDbList((ArrayList) this.getFormHM().get("dbList"));
		this.setAudit((String) this.getFormHM().get("audit"));
		this.setPersonScope((String) this.getFormHM().get("personScope"));
		this.setCexpr((String) this.getFormHM().get("cexpr"));
		this.setFieldsetid((String) this.getFormHM().get("fieldsetid"));
		this.setIsHave((String) this.getFormHM().get("isHave"));
		this.set_sql((String) this.getFormHM().get("_sql"));
		this.setYearnum((String) this.getFormHM().get("yearnum"));
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setSpTypeList((ArrayList) this.getFormHM().get("spTypeList"));
		this.setFilterList((ArrayList) this.getFormHM().get("filterList"));
		this.setDblist((ArrayList) this.getFormHM().get("dblist"));
		this.setFiltervalue((String) this.getFormHM().get("filtervalue"));
		this.setSpType((String) this.getFormHM().get("spType"));
		this.setTablename((String) this.getFormHM().get("tablename"));
		this.setTheyear((String) this.getFormHM().get("theyear"));
		this.setThemonth((String) this.getFormHM().get("themonth"));
		this.setDbname((String) this.getFormHM().get("dbname"));
		this.setFiltervalue((String) this.getFormHM().get("filtervalue"));
		this.setReturnInfo((String)this.getFormHM().get("returnInfo"));
		this.setInfo((String)this.getFormHM().get("info"));
		this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
		this.setFormulalist((ArrayList) this.getFormHM().get("formulalist"));
		this.getFormulalistform().setList((ArrayList)this.getFormHM().get("formulalist"));
		this.setZt((String)this.getFormHM().get("zt"));
		this.setYm((String) this.getFormHM().get("ym"));
	}
	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		if("/performance/data_collect/data_collect".equals(mapping.getPath())&&request.getParameter("b_add")!=null){
			request.setAttribute("targetWindow", "1");
		}
		if("/performance/data_collect/data_collect".equals(mapping.getPath())&&request.getParameter("b_upload")!=null){
			request.setAttribute("targetWindow", "1");
		}
		return super.validate(mapping, request);
	}
}
