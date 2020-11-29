package com.hjsj.hrms.actionform.gz.gz_amount;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *<p>Title:CroPayMentForm.java</p> 
 *<p>Description:薪资总额</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class CroPayMentForm extends FrameForm 
{
	/**数据过滤语句*/
    private String sqlstr="";
    /**薪资总额项目名称*/
    private String gz_grossname="";
    /**薪资总额项目列表*/
    private ArrayList fieldlist=new ArrayList();
    /**单位薪资总额*/
    private String fieldsetid="";
    private ArrayList fieldsetlist=new ArrayList();
    
    /**薪资总额指标*/
    private String fielditemid="";
    private ArrayList fielditemlist=new ArrayList();
    
    /**剩余额*/
    private String nid="";
    private ArrayList nlist=new ArrayList();
    
    /**生成薪资总额项目列表table*/
    private String relation_table="";
    
    /**子标id*/
    private String hiddenitemid="";
    /**公式值*/
    private String hiddendest="";
    
    /**年度选择*/
    private String yearnum="";
    
    /**公司部门id*/
    private String codeitemid="";
    
    /**审批状态指标指标*/
    private String spflagid="";
    private ArrayList spflaglist=new ArrayList();
    //---------------------
    private String table;
    private String ctrl_type;
    private ArrayList dataList = new ArrayList();
    private String strArr ;
    private String planitem;
    private String realitem ;
    private String balanceitem;
    private String flagitem;
    private String formularStr;
    private String code;
    /**年月控制标识*/
    private String ctrl_peroid;//=1按年，=0按月
    /**核算单位*/
    private String orgid;
    private ArrayList orgList = new ArrayList();
    /**核算部门*/
    private String deptid;
    private ArrayList deptList = new ArrayList();
    /**总额明细*/
    private ArrayList columnList = new ArrayList();
    private ArrayList infoList = new ArrayList();
    private String UnitName;
    private String oldctrl_peroid;
    private ArrayList list = new ArrayList(); 
    /**按季度或者月份过滤列表*/
    private ArrayList filterList = new ArrayList();
    private String filtervalue;
    // 审批状态
    private ArrayList spTypeList = new ArrayList();
    private String spType;
    private ArrayList contrlLevelList=new ArrayList();
    private String contrlLevelId="";
    private FormFile picturefile;
    private String checkClose="";
    private String checkflag="";
    private String ctrl_by_level;//是否按层级控制=0不按=1按层级控制
    private ArrayList salarySetList = new ArrayList();
    private String salarySet;
    /**是否按操作单位来控制权限=0不=1按*/
    private String viewUnit;
    /**是否级联显示机构=1不级联显示=0级联显示*/
    private String cascadingctrl;
    private String hasParam;
    private String tableStr;
    private String sortStr;
    private String results;
    private String history;
    private String unit_type;
    private String year;
    private String classitem;
    /**总额调整子集*/
    private String amountAdjustSet;
    private ArrayList amountAdjustSetList = new ArrayList();
    /**项目或分类名称*/
    private String amountPlanitemDescField;
    private ArrayList amountPlanitemDescFieldList = new ArrayList();
    private String isHasAdjustSet;
    private ArrayList fieldList = new ArrayList();
    private PaginationForm adjustListform=new PaginationForm();
    private String orgDesc;
    private ArrayList tableHeaderList = new ArrayList();
    private String optType;
    private String  surplus_compute;//结余参于计算，默认值为0（不参于）
    private  String fc_flag ;//封存状态指标
    private ArrayList fc_flag_list = new ArrayList();
    private String fcVisible;//是否显示封存，解封按钮
    private String hasFc="";
    private String ctrlAmountField;//启用总额控制指标
    private ArrayList ctrlAmountFieldList=new ArrayList();
    private String hasCtrlField;//是否设置启用总额指标
    private String createType;//新建总额记录，是否关联下级一起
    private String isCanCreate;
    
    
	@Override
    public void outPutFormHM()
	{
		
		this.setSpType((String)this.getFormHM().get("spType"));
		this.setSpTypeList((ArrayList)this.getFormHM().get("spTypeList"));
		this.setIsCanCreate((String)this.getFormHM().get("isCanCreate"));
		this.setCtrlAmountFieldList((ArrayList)this.getFormHM().get("ctrlAmountFieldList"));
		this.setCtrlAmountField((String)this.getFormHM().get("ctrlAmountField"));
		this.setHasCtrlField((String)this.getFormHM().get("hasCtrlField"));
		this.setCreateType((String)this.getFormHM().get("createType"));
		this.setHasFc((String)this.getFormHM().get("hasFc"));
		this.setFcVisible((String)this.getFormHM().get("fcVisible"));
		this.setSurplus_compute((String)this.getFormHM().get("surplus_compute"));
		this.setFc_flag((String)this.getFormHM().get("fc_flag"));
		this.setFc_flag_list((ArrayList)this.getFormHM().get("fc_flag_list"));
		this.setOptType((String)this.getFormHM().get("optType"));
		this.setTableHeaderList((ArrayList)this.getFormHM().get("tableHeaderList"));
		this.setOrgDesc((String)this.getFormHM().get("orgDesc"));
		this.setIsHasAdjustSet((String)this.getFormHM().get("isHasAdjustSet"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.getAdjustListform().setList((ArrayList)this.getFormHM().get("adjustList"));
		this.setAmountAdjustSet((String)this.getFormHM().get("amountAdjustSet"));
		this.setAmountAdjustSetList((ArrayList)this.getFormHM().get("amountAdjustSetList"));
		this.setAmountPlanitemDescField((String)this.getFormHM().get("amountPlanitemDescField"));
		this.setAmountPlanitemDescFieldList((ArrayList)this.getFormHM().get("amountPlanitemDescFieldList"));
		this.setClassitem((String)this.getFormHM().get("classitem"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setUnit_type((String)this.getFormHM().get("unit_type"));
		this.setHistory((String)this.getFormHM().get("history"));
		this.setResults((String)this.getFormHM().get("results"));
		this.setTableStr((String)this.getFormHM().get("tableStr"));
		this.setSortStr((String)this.getFormHM().get("sortStr"));
		this.setHasParam((String)this.getFormHM().get("hasParam"));
		this.setCascadingctrl((String)this.getFormHM().get("cascadingctrl"));
		this.setViewUnit((String)this.getFormHM().get("viewUnit"));
		this.setSalarySet((String)this.getFormHM().get("salarySet"));
		this.setSalarySetList((ArrayList)this.getFormHM().get("salarySetList"));
		this.setCtrl_by_level((String)this.getFormHM().get("ctrl_by_level"));
		this.setContrlLevelId((String)this.getFormHM().get("contrlLevelId"));
		this.setContrlLevelList((ArrayList)this.getFormHM().get("contrlLevelList"));
		this.setFiltervalue((String)this.getFormHM().get("filtervalue"));
		this.setFilterList((ArrayList)this.getFormHM().get("filterList"));
		this.setOldctrl_peroid((String)this.getFormHM().get("oldctrl_peroid"));
		this.setOrgList((ArrayList)this.getFormHM().get("orgList"));
		this.setDeptList((ArrayList)this.getFormHM().get("deptList"));
		this.setCtrl_peroid((String)this.getFormHM().get("ctrl_peroid"));
		this.setOrgid((String)this.getFormHM().get("orgid"));
		this.setDeptid((String)this.getFormHM().get("deptid"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setGz_grossname((String)this.getFormHM().get("gz_grossname"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setFieldsetlist((ArrayList)this.getFormHM().get("fieldsetlist"));
		this.setFielditemid((String)this.getFormHM().get("fielditemid"));
		this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist"));
		this.setNid((String)this.getFormHM().get("nid"));
		this.setNlist((ArrayList)this.getFormHM().get("nlist"));
		this.setRelation_table((String)this.getFormHM().get("relation_table"));
		this.setHiddendest((String)this.getFormHM().get("hiddendest"));
		this.setHiddenitemid((String)this.getFormHM().get("hiddenitemid"));
		this.setYearnum((String)this.getFormHM().get("yearnum"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setSpflagid((String)this.getFormHM().get("spflagid"));
		this.setSpflaglist((ArrayList)this.getFormHM().get("spflaglist"));
		this.setTable((String)this.getFormHM().get("table"));
		this.setCtrl_type((String)this.getFormHM().get("ctrl_type"));
		this.setDataList((ArrayList)this.getFormHM().get("dataList"));
		this.setStrArr((String)this.getFormHM().get("strArr"));
		this.setBalanceitem((String)this.getFormHM().get("balanceitem"));
		this.setRealitem((String)this.getFormHM().get("realitem"));
		this.setPlanitem((String)this.getFormHM().get("planitem"));
		this.setFlagitem((String)this.getFormHM().get("flagitem"));
		this.setFormularStr((String)this.getFormHM().get("formularStr"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setColumnList((ArrayList)this.getFormHM().get("columnList"));
		this.setInfoList((ArrayList)this.getFormHM().get("infoList"));
		this.setUnitName((String)this.getFormHM().get("unitName"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setPicturefile((FormFile)this.getFormHM().get("picturefile"));
		this.setCheckClose((String)this.getFormHM().get("checkClose"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
	}

	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("spType", this.getSpType());
		this.getFormHM().put("spTypeList", this.getSpTypeList());
		this.getFormHM().put("isCanCreate", this.getIsCanCreate());
		this.getFormHM().put("ctrlAmountField", this.getCtrlAmountField());
		this.getFormHM().put("hasCtrlField", this.getHasCtrlField());
		this.getFormHM().put("createType", this.getCreateType());
		this.getFormHM().put("fcVisible", this.getFcVisible());
		this.getFormHM().put("surplus_compute", this.getSurplus_compute());
		this.getFormHM().put("fc_flag", this.getFc_flag());
		this.getFormHM().put("optType", this.getOptType());
		this.getFormHM().put("isHasAdjustSet", this.getIsHasAdjustSet());
		this.getFormHM().put("selectedList",this.getAdjustListform().getSelectedList());
		this.getFormHM().put("amountAdjustSet", this.getAmountAdjustSet());
		this.getFormHM().put("amountPlanitemDescField", this.getAmountPlanitemDescField());
		this.getFormHM().put("classitem", this.getClassitem());
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("history", this.getHistory());
		this.getFormHM().put("results", this.getResults());
		this.getFormHM().put("unit_type", this.getUnit_type());
		this.getFormHM().put("tableStr", this.getTableStr());
		this.getFormHM().put("sortStr", this.getSortStr());
		this.getFormHM().put("cascadingctrl", this.getCascadingctrl());
		this.getFormHM().put("viewUnit", this.getViewUnit());
		this.getFormHM().put("salarySet", this.getSalarySet());
		this.getFormHM().put("ctrl_by_level",this.getCtrl_by_level());
		this.getFormHM().put("contrlLevelId",this.getContrlLevelId());
		this.getFormHM().put("filtervalue", this.getFiltervalue());
		this.getFormHM().put("oldctrl_peroid",this.getOldctrl_peroid());
		this.getFormHM().put("ctrl_peroid", this.getCtrl_peroid());
		this.getFormHM().put("orgid",this.getOrgid());
		this.getFormHM().put("deptid", this.getDeptid());
		this.getFormHM().put("fieldsetid", getFieldsetid());
		this.getFormHM().put("fielditemid", getFielditemid());
		this.getFormHM().put("nid", getNid());
		this.getFormHM().put("hiddenitemid", getHiddenitemid());
		this.getFormHM().put("hiddendest", getHiddendest());
		this.getFormHM().put("yearnum", getYearnum());
		this.getFormHM().put("codeitemid", getCodeitemid());
		this.getFormHM().put("spflagid", getSpflagid());
		this.getFormHM().put("ctrl_type",this.getCtrl_type());
		this.getFormHM().put("strArr",this.getStrArr());
		this.getFormHM().put("planitem", this.getPlanitem());
		this.getFormHM().put("realitem",this.getRealitem());
		this.getFormHM().put("balanceitem",this.getBalanceitem());
		this.getFormHM().put("flagitem",this.getFlagitem());
		this.getFormHM().put("formularStr",this.getFormularStr());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("columnList",this.getColumnList());
		this.getFormHM().put("infoList",this.getInfoList());
		this.getFormHM().put("unitName",this.getUnitName());
		this.getFormHM().put("list",this.getList());
		this.getFormHM().put("picturefile",this.getPicturefile());
	}
	
	@Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)	{
		/* 安全问题 文件下载 薪资总额-导入数据 xiaoyun 2014-9-13 start */
		if("/gz/gz_amount/gropayment".equals(mapping.getPath())&&request.getParameter("isclose")!=null){
			request.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
		/* 安全问题 文件下载 薪资总额-导入数据 xiaoyun 2014-9-13 end */
      	return super.validate(mapping, request);
	}
	public String getGz_grossname() {
		return gz_grossname;
	}

	public void setGz_grossname(String gz_grossname) {
		this.gz_grossname = gz_grossname;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getFielditemid() {
		return fielditemid;
	}

	public void setFielditemid(String fielditemid) {
		this.fielditemid = fielditemid;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public ArrayList getNlist() {
		return nlist;
	}

	public void setNlist(ArrayList nlist) {
		this.nlist = nlist;
	}

	public String getRelation_table() {
		return relation_table;
	}

	public void setRelation_table(String relation_table) {
		this.relation_table = relation_table;
	}

	public String getHiddendest() {
		return hiddendest;
	}

	public void setHiddendest(String hiddendest) {
		this.hiddendest = hiddendest;
	}

	public String getHiddenitemid() {
		return hiddenitemid;
	}

	public void setHiddenitemid(String hiddenitemid) {
		this.hiddenitemid = hiddenitemid;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getYearnum() {
		return yearnum;
	}

	public void setYearnum(String yearnum) {
		this.yearnum = yearnum;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public String getSpflagid() {
		return spflagid;
	}

	public void setSpflagid(String spflagid) {
		this.spflagid = spflagid;
	}

	public ArrayList getSpflaglist() {
		return spflaglist;
	}

	public void setSpflaglist(ArrayList spflaglist) {
		this.spflaglist = spflaglist;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getCtrl_type() {
		return ctrl_type;
	}

	public void setCtrl_type(String ctrl_type) {
		this.ctrl_type = ctrl_type;
	}

	public ArrayList getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}

	public String getStrArr() {
		return strArr;
	}

	public void setStrArr(String strArr) {
		this.strArr = strArr;
	}

	public String getBalanceitem() {
		return balanceitem;
	}

	public void setBalanceitem(String balanceitem) {
		this.balanceitem = balanceitem;
	}

	public String getPlanitem() {
		return planitem;
	}

	public void setPlanitem(String planitem) {
		this.planitem = planitem;
	}

	public String getRealitem() {
		return realitem;
	}

	public void setRealitem(String realitem) {
		this.realitem = realitem;
	}

	public String getFlagitem() {
		return flagitem;
	}

	public void setFlagitem(String flagitem) {
		this.flagitem = flagitem;
	}

	public String getFormularStr() {
		return formularStr;
	}

	public void setFormularStr(String formularStr) {
		this.formularStr = formularStr;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCtrl_peroid() {
		return ctrl_peroid;
	}

	public void setCtrl_peroid(String ctrl_peroid) {
		this.ctrl_peroid = ctrl_peroid;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public ArrayList getOrgList() {
		return orgList;
	}

	public void setOrgList(ArrayList orgList) {
		this.orgList = orgList;
	}

	public ArrayList getDeptList() {
		return deptList;
	}

	public void setDeptList(ArrayList deptList) {
		this.deptList = deptList;
	}

	public ArrayList getColumnList() {
		return columnList;
	}

	public void setColumnList(ArrayList columnList) {
		this.columnList = columnList;
	}

	public ArrayList getInfoList() {
		return infoList;
	}

	public void setInfoList(ArrayList infoList) {
		this.infoList = infoList;
	}

	public String getUnitName() {
		return UnitName;
	}

	public void setUnitName(String unitName) {
		UnitName = unitName;
	}

	public String getOldctrl_peroid() {
		return oldctrl_peroid;
	}

	public void setOldctrl_peroid(String oldctrl_peroid) {
		this.oldctrl_peroid = oldctrl_peroid;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public ArrayList getFilterList() {
		return filterList;
	}

	public void setFilterList(ArrayList filterList) {
		this.filterList = filterList;
	}

	public String getFiltervalue() {
		return filtervalue;
	}

	public void setFiltervalue(String filtervalue) {
		this.filtervalue = filtervalue;
	}

	public ArrayList getContrlLevelList() {
		return contrlLevelList;
	}

	public void setContrlLevelList(ArrayList contrlLevelList) {
		this.contrlLevelList = contrlLevelList;
	}

	public String getContrlLevelId() {
		return contrlLevelId;
	}

	public void setContrlLevelId(String contrlLevelId) {
		this.contrlLevelId = contrlLevelId;
	}

	public String getCheckClose() {
		return checkClose;
	}

	public void setCheckClose(String checkClose) {
		this.checkClose = checkClose;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getCtrl_by_level() {
		return ctrl_by_level;
	}

	public void setCtrl_by_level(String ctrl_by_level) {
		this.ctrl_by_level = ctrl_by_level;
	}

	public ArrayList getSalarySetList() {
		return salarySetList;
	}

	public void setSalarySetList(ArrayList salarySetList) {
		this.salarySetList = salarySetList;
	}

	public String getSalarySet() {
		return salarySet;
	}

	public void setSalarySet(String salarySet) {
		this.salarySet = salarySet;
	}

	public String getViewUnit() {
		return viewUnit;
	}

	public void setViewUnit(String viewUnit) {
		this.viewUnit = viewUnit;
	}

	public String getCascadingctrl() {
		return cascadingctrl;
	}

	public void setCascadingctrl(String cascadingctrl) {
		this.cascadingctrl = cascadingctrl;
	}

	public String getHasParam() {
		return hasParam;
	}

	public void setHasParam(String hasParam) {
		this.hasParam = hasParam;
	}

	public String getTableStr() {
		return tableStr;
	}

	public void setTableStr(String tableStr) {
		this.tableStr = tableStr;
	}

	public String getSortStr() {
		return sortStr;
	}

	public void setSortStr(String sortStr) {
		this.sortStr = sortStr;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getUnit_type() {
		return unit_type;
	}

	public void setUnit_type(String unit_type) {
		this.unit_type = unit_type;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getClassitem() {
		return classitem;
	}

	public void setClassitem(String classitem) {
		this.classitem = classitem;
	}

	public String getAmountAdjustSet() {
		return amountAdjustSet;
	}

	public void setAmountAdjustSet(String amountAdjustSet) {
		this.amountAdjustSet = amountAdjustSet;
	}

	public ArrayList getAmountAdjustSetList() {
		return amountAdjustSetList;
	}

	public void setAmountAdjustSetList(ArrayList amountAdjustSetList) {
		this.amountAdjustSetList = amountAdjustSetList;
	}

	public String getAmountPlanitemDescField() {
		return amountPlanitemDescField;
	}

	public void setAmountPlanitemDescField(String amountPlanitemDescField) {
		this.amountPlanitemDescField = amountPlanitemDescField;
	}

	public ArrayList getAmountPlanitemDescFieldList() {
		return amountPlanitemDescFieldList;
	}

	public void setAmountPlanitemDescFieldList(ArrayList amountPlanitemDescFieldList) {
		this.amountPlanitemDescFieldList = amountPlanitemDescFieldList;
	}

	public String getIsHasAdjustSet() {
		return isHasAdjustSet;
	}

	public void setIsHasAdjustSet(String isHasAdjustSet) {
		this.isHasAdjustSet = isHasAdjustSet;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public PaginationForm getAdjustListform() {
		return adjustListform;
	}

	public void setAdjustListform(PaginationForm adjustListform) {
		this.adjustListform = adjustListform;
	}

	public String getOrgDesc() {
		return orgDesc;
	}

	public void setOrgDesc(String orgDesc) {
		this.orgDesc = orgDesc;
	}

	public ArrayList getTableHeaderList() {
		return tableHeaderList;
	}

	public void setTableHeaderList(ArrayList tableHeaderList) {
		this.tableHeaderList = tableHeaderList;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getSurplus_compute() {
		return surplus_compute;
	}

	public void setSurplus_compute(String surplus_compute) {
		this.surplus_compute = surplus_compute;
	}

	public String getFc_flag() {
		return fc_flag;
	}

	public void setFc_flag(String fc_flag) {
		this.fc_flag = fc_flag;
	}

	public ArrayList getFc_flag_list() {
		return fc_flag_list;
	}

	public void setFc_flag_list(ArrayList fc_flag_list) {
		this.fc_flag_list = fc_flag_list;
	}

	public String getFcVisible() {
		return fcVisible;
	}

	public void setFcVisible(String fcVisible) {
		this.fcVisible = fcVisible;
	}
	public String getHasFc() {
		return hasFc;
	}

	public void setHasFc(String hasFc) {
		this.hasFc = hasFc;
	}

	public String getCtrlAmountField() {
		return ctrlAmountField;
	}

	public void setCtrlAmountField(String ctrlAmountField) {
		this.ctrlAmountField = ctrlAmountField;
	}

	public String getHasCtrlField() {
		return hasCtrlField;
	}

	public void setHasCtrlField(String hasCtrlField) {
		this.hasCtrlField = hasCtrlField;
	}

	public String getCreateType() {
		return createType;
	}

	public void setCreateType(String createType) {
		this.createType = createType;
	}

	public ArrayList getCtrlAmountFieldList() {
		return ctrlAmountFieldList;
	}

	public void setCtrlAmountFieldList(ArrayList ctrlAmountFieldList) {
		this.ctrlAmountFieldList = ctrlAmountFieldList;
	}

	public String getIsCanCreate() {
		return isCanCreate;
	}

	public void setIsCanCreate(String isCanCreate) {
		this.isCanCreate = isCanCreate;
	}

	public ArrayList getSpTypeList() {
		return spTypeList;
	}

	public void setSpTypeList(ArrayList spTypeList) {
		this.spTypeList = spTypeList;
	}

	public String getSpType() {
		return spType;
	}

	public void setSpType(String spType) {
		this.spType = spType;
	}
}
