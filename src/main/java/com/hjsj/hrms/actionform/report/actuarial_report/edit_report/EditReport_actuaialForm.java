package com.hjsj.hrms.actionform.report.actuarial_report.edit_report;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

public class EditReport_actuaialForm extends FrameForm {

	private PaginationForm  editreportForm = new PaginationForm(); // 显示数据的分页处理
	private String report_id;
	private String flag;
	private String flagSub="0";//为1时表示汇总单位已上报，不具有驳回功能 
	private String unitcode;
	private String selfUnitcode="";
	private String id;
	private String sql;
	private ArrayList fieldlistU02=new ArrayList();
	private ArrayList fieldlsitU01=new ArrayList();
	private ArrayList editlistU02=new ArrayList();
	private FormFile file;
	private String importInfo;
	private ArrayList import_insertList=new ArrayList();
	private ArrayList import_updateList=new ArrayList();
	private String import_insertSql;
	private String import_updateSql;
	private String idstatus;   //当前发布周期的状态
	private String cycleStatus="";  //填报周期状态
	private String isCollectUnit="0"; 
	private String isUnderUnit="0";  //是否是当前单位的直属单位
	private String    isAllSub;
	private String olditemdesc;
	private String like="0";
	private String subquerysql="";
	private String rootUnit="";
	private String theyear="";
	private String u02_3flag="0";
	
	/** 表3 */
	private ArrayList u03DataList=new ArrayList();
	private ArrayList compareDataList=new ArrayList();  //比较数据
	private String    current_values="";
	private ArrayList dataHeadList=new ArrayList();
	private String opt="1";   //1:可操作  0：只读 
	private String opt2="";   //返回时用
	private String from_model="edit";  //collect:报表汇总模块   edit:编辑报表模块
	private String info="";   //提交返回信息
	private String reportStatus="";  //报表状态;
	private String kmethod="";
	private String unitcodes;  //单位s
	private String description;  //状态表的描述
	private ArrayList u04DataList=new ArrayList();
   /** 表5 */	
	private ArrayList dataHeadList_u05=new ArrayList();
	private ArrayList dataList_u05=new ArrayList();
	
	private String t5_desc="";
	private String t3_desc="";
	private String htmlbody="";
	private String htmlbody2="";
	private String paracopy="";
	private String paracopy2="";
	private String isfillpara="";
	private String isfillpara2 ="";
	private ArrayList warninglist=new ArrayList();//警告信息
	private String cancelunit ="0";
	private String updatehistory="";
	public String getIsfillpara() {
		return isfillpara;
	}

	public void setIsfillpara(String isfillpara) {
		this.isfillpara = isfillpara;
	}

	public String getIsfillpara2() {
		return isfillpara2;
	}

	public void setIsfillpara2(String isfillpara2) {
		this.isfillpara2 = isfillpara2;
	}

	public String getParacopy() {
		return paracopy;
	}

	public void setParacopy(String paracopy) {
		this.paracopy = paracopy;
	}

	public String getParacopy2() {
		return paracopy2;
	}

	public void setParacopy2(String paracopy2) {
		this.paracopy2 = paracopy2;
	}

	public ArrayList getFieldlsitU01() {
		return fieldlsitU01;
	}

	public void setFieldlsitU01(ArrayList fieldlsitU01) {
		this.fieldlsitU01 = fieldlsitU01;
	}
	

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getEditreportForm().setList((ArrayList)this.getFormHM().get("repotlist"));
		this.setFieldlsitU01((ArrayList)this.getFormHM().get("fieldlsitU01"));
		this.setFieldlistU02((ArrayList)this.getFormHM().get("fieldlistU02"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setEditlistU02((ArrayList)this.getFormHM().get("editlistU02"));
		this.setIsCollectUnit((String)this.getFormHM().get("isCollectUnit"));
		
		this.setIsUnderUnit((String)this.getFormHM().get("isUnderUnit"));
		this.setSelfUnitcode((String)this.getFormHM().get("selfUnitcode"));
		this.setCycleStatus((String)this.getFormHM().get("cycleStatus"));
		
		this.setFrom_model((String)this.getFormHM().get("from_model"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setU03DataList((ArrayList)this.getFormHM().get("u03DataList"));
		this.setCompareDataList((ArrayList)this.getFormHM().get("compareDataList"));
		this.setDataHeadList((ArrayList)this.getFormHM().get("dataHeadList"));
		this.setUnitcode((String)this.getFormHM().get("unitcode"));
		this.setOpt((String)this.getFormHM().get("opt"));
		this.setOpt2((String)this.getFormHM().get("opt2"));
		this.setId((String)this.getFormHM().get("id"));
		this.setInfo((String)this.getFormHM().get("info"));
		this.setReportStatus((String)this.getFormHM().get("reportStatus"));
		this.setKmethod((String)this.getFormHM().get("kmethod"));
		this.setImportInfo((String)this.getFormHM().get("importInfo"));
		this.setDataHeadList_u05((ArrayList)this.getFormHM().get("dataHeadList_u05"));
		this.setDataList_u05((ArrayList)this.getFormHM().get("dataList_u05"));
		this.setU04DataList((ArrayList)this.getFormHM().get("u04DataList"));
		this.setImport_insertList((ArrayList)this.getFormHM().get("import_insertList"));
		this.setImport_updateList((ArrayList)this.getFormHM().get("import_updateList"));
		this.setImport_insertSql((String)this.getFormHM().get("import_insertSql"));
		this.setImport_updateSql((String)this.getFormHM().get("import_updateSql"));
		
		this.setT3_desc((String)this.getFormHM().get("t3_desc"));
		this.setT5_desc((String)this.getFormHM().get("t5_desc"));
		this.setUnitcodes((String)this.getFormHM().get("unitcodes"));
		this.setDescription((String)this.getFormHM().get("description"));
		this.setIdstatus((String)this.getFormHM().get("idstatus"));
		this.setIsAllSub((String)this.getFormHM().get("isAllSub"));
		this.setReport_id((String)this.getFormHM().get("report_id"));
		this.setOlditemdesc((String)this.getFormHM().get("olditemdesc"));
		this.setHtmlbody((String)this.getFormHM().get("htmlbody"));
		this.setHtmlbody2((String)this.getFormHM().get("htmlbody2"));
		this.setParacopy((String)this.getFormHM().get("paracopy"));
		this.setParacopy2((String)this.getFormHM().get("paracopy2"));
		this.setIsfillpara((String)this.getFormHM().get("isfillpara"));
		this.setIsfillpara2((String)this.getFormHM().get("isfillpara2"));
		this.setLike((String)this.getFormHM().get("like"));
		this.setSubquerysql((String)this.getFormHM().get("subquerysql"));
		this.setRootUnit((String)this.getFormHM().get("rootUnit"));
		this.setFlagSub((String)this.getFormHM().get("flagSub"));
		this.setWarninglist((ArrayList)this.getFormHM().get("warninglist"));
		this.setTheyear((String)this.getFormHM().get("theyear"));
		this.setU02_3flag((String)this.getFormHM().get("u02_3flag"));
		this.setCancelunit((String)this.getFormHM().get("cancelunit"));
		this.setFile((FormFile)this.getFormHM().get("file"));
		this.setUpdatehistory((String)this.getFormHM().get("updatehistory"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("report_id", this.getReport_id());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("unitcode", this.getUnitcode());
		this.getFormHM().put("id", this.getId());
		this.getFormHM().put("fieldlsitU01", this.getFieldlsitU01());
        this.getFormHM().put("editlistU02", this.getEditlistU02());
        this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("opt",this.opt);
		this.getFormHM().put("import_insertList", this.getImport_insertList());
		this.getFormHM().put("import_updateList", this.getImport_updateList());
		this.getFormHM().put("import_insertSql", this.getImport_insertSql());
		this.getFormHM().put("import_updateSql", this.getImport_updateSql());
		this.getFormHM().put("current_values",this.getCurrent_values());
		this.getFormHM().put("kmethod", this.getKmethod());
		this.getFormHM().put("t3_desc", this.getT3_desc());
		this.getFormHM().put("t5_desc",this.getT5_desc());
		this.getFormHM().put("description", this.description);
		  this.getFormHM().put("like",this.getLike());
	}


	public PaginationForm getEditreportForm() {
		return editreportForm;
	}

	public void setEditreportForm(PaginationForm editreportForm) {
		this.editreportForm = editreportForm;
	}

	public ArrayList getCompareDataList() {
		return compareDataList;
	}

	public void setCompareDataList(ArrayList compareDataList) {
		this.compareDataList = compareDataList;
	}

	public String getCurrent_values() {
		return current_values;
	}

	public void setCurrent_values(String current_values) {
		this.current_values = current_values;
	}

	public ArrayList getDataHeadList() {
		return dataHeadList;
	}

	public void setDataHeadList(ArrayList dataHeadList) {
		this.dataHeadList = dataHeadList;
	}

	public ArrayList getU03DataList() {
		return u03DataList;
	}

	public void setU03DataList(ArrayList dataList) {
		u03DataList = dataList;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getReport_id() {
		return report_id;
	}

	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getFieldlistU02() {
		return fieldlistU02;
	}

	public void setFieldlistU02(ArrayList fieldlistU02) {
		this.fieldlistU02 = fieldlistU02;
	}

	public ArrayList getEditlistU02() {
		return editlistU02;
	}

	public void setEditlistU02(ArrayList editlistU02) {
		this.editlistU02 = editlistU02;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getKmethod() {
		return kmethod;
	}

	public void setKmethod(String kmethod) {
		this.kmethod = kmethod;
	}

	public ArrayList getDataHeadList_u05() {
		return dataHeadList_u05;
	}

	public void setDataHeadList_u05(ArrayList dataHeadList_u05) {
		this.dataHeadList_u05 = dataHeadList_u05;
	}

	public ArrayList getDataList_u05() {
		return dataList_u05;
	}

	public void setDataList_u05(ArrayList dataList_u05) {
		this.dataList_u05 = dataList_u05;
	}

	public ArrayList getU04DataList() {
		return u04DataList;
	}

	public void setU04DataList(ArrayList dataList) {
		u04DataList = dataList;
	}

	public String getImportInfo() {
		return importInfo;
	}

	public void setImportInfo(String importInfo) {
		this.importInfo = importInfo;
	}

	public ArrayList getImport_insertList() {
		return import_insertList;
	}

	public void setImport_insertList(ArrayList import_insertList) {
		this.import_insertList = import_insertList;
	}

	public ArrayList getImport_updateList() {
		return import_updateList;
	}

	public void setImport_updateList(ArrayList import_updateList) {
		this.import_updateList = import_updateList;
	}

	public String getImport_insertSql() {
		return import_insertSql;
	}

	public void setImport_insertSql(String import_insertSql) {
		this.import_insertSql = import_insertSql;
	}

	public String getImport_updateSql() {
		return import_updateSql;
	}

	public void setImport_updateSql(String import_updateSql) {
		this.import_updateSql = import_updateSql;
	}

	public String getT3_desc() {
		return t3_desc;
	}

	public void setT3_desc(String t3_desc) {
		this.t3_desc = t3_desc;
	}

	public String getT5_desc() {
		return t5_desc;
	}

	public void setT5_desc(String t5_desc) {
		this.t5_desc = t5_desc;
	}

	public String getFrom_model() {
		return from_model;
	}

	public void setFrom_model(String from_model) {
		this.from_model = from_model;
	}

	public String getIsCollectUnit() {
		return isCollectUnit;
	}

	public void setIsCollectUnit(String isCollectUnit) {
		this.isCollectUnit = isCollectUnit;
	}

	public String getIsUnderUnit() {
		return isUnderUnit;
	}

	public void setIsUnderUnit(String isUnderUnit) {
		this.isUnderUnit = isUnderUnit;
	}

	public String getSelfUnitcode() {
		return selfUnitcode;
	}

	public void setSelfUnitcode(String selfUnitcode) {
		this.selfUnitcode = selfUnitcode;
	}

	public String getCycleStatus() {
		return cycleStatus;
	}

	public void setCycleStatus(String cycleStatus) {
		this.cycleStatus = cycleStatus;
	}

	public String getUnitcodes() {
		return unitcodes;
	}

	public void setUnitcodes(String unitcodes) {
		this.unitcodes = unitcodes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIdstatus() {
		return idstatus;
	}

	public void setIdstatus(String idstatus) {
		this.idstatus = idstatus;
	}

	public String getIsAllSub() {
		return isAllSub;
	}

	public void setIsAllSub(String isAllSub) {
		this.isAllSub = isAllSub;
	}

	public String getOlditemdesc() {
		return olditemdesc;
	}

	public void setOlditemdesc(String olditemdesc) {
		this.olditemdesc = olditemdesc;
	}

	public String getHtmlbody() {
		return htmlbody;
	}

	public void setHtmlbody(String htmlbody) {
		this.htmlbody = htmlbody;
	}

	public String getHtmlbody2() {
		return htmlbody2;
	}

	public void setHtmlbody2(String htmlbody2) {
		this.htmlbody2 = htmlbody2;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getSubquerysql() {
		return subquerysql;
	}

	public void setSubquerysql(String subquerysql) {
		this.subquerysql = subquerysql;
	}

	public String getRootUnit() {
		return rootUnit;
	}

	public void setRootUnit(String rootUnit) {
		this.rootUnit = rootUnit;
	}

	public String getOpt2() {
		return opt2;
	}

	public void setOpt2(String opt2) {
		this.opt2 = opt2;
	}

	public String getFlagSub() {
		return flagSub;
	}

	public void setFlagSub(String flagSub) {
		this.flagSub = flagSub;
	}

	public ArrayList getWarninglist() {
		return warninglist;
	}

	public void setWarninglist(ArrayList warninglist) {
		this.warninglist = warninglist;
	}

	public String getTheyear() {
		return theyear;
	}

	public void setTheyear(String theyear) {
		this.theyear = theyear;
	}

	public String getU02_3flag() {
		return u02_3flag;
	}

	public void setU02_3flag(String u02_3flag) {
		this.u02_3flag = u02_3flag;
	}

	public String getUpdatehistory() {
		return updatehistory;
	}

	public void setUpdatehistory(String updatehistory) {
		this.updatehistory = updatehistory;
	}

	public String getCancelunit() {
		return cancelunit;
	}

	public void setCancelunit(String cancelunit) {
		this.cancelunit = cancelunit;
	}




}
