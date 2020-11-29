package com.hjsj.hrms.actionform.kq.machine;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class RepairKqCardFrom extends FrameForm
{
	private String a_code;
	private String sqlstr;
	private String column;
	private String where;
	private String nbase;
	private String work_date;
	private String repair_flag;
	private String statr_date;
	private String end_date;
	private String class_flag;
	private String cycle_num;
	private String cycle_date;
	private String cycle_hh;
	private String cycle_mm;
	private String temp_emp_table;
	private String cur_session;
	private String causation;//原因
	private String into_flag;//类型
	private String computer_name;
	private String ip_adr;
	private String select_name;
	private String select_type = "0";
	private String repair_fashion="";//申请方式0:简单,1复杂
	private String easy_date;
	private String easy_hh;
	private String easy_mm;
	private String card_causation;
	private String checkEm;
	private String isInout_flag;
	private String end_date12;
	
	private String right_fields[];
	private String left_fields[];
	private ArrayList fieldlist;
	private ArrayList selectedlist;
	private ArrayList dblist;
	private ArrayList factorlist;
	private String like;
	private String sql_where;
	private String noCardFlag = "0";//从未刷卡人员页面进入补刷卡标识=1，其他页面进入=0
	/** 关系操作符 */
	private ArrayList operlist = new ArrayList();
	/** 逻辑操作符 */
	private ArrayList logiclist = new ArrayList();
	
	private String orgparentcode;//补刷卡查询/条件查询按权限范围显示
	private String reflag;//操作完成后返回标示

	public RepairKqCardFrom(){
		CommonData vo = new CommonData("=", "=");
		operlist.add(vo);
		vo = new CommonData(">", ">");
		operlist.add(vo);
		vo = new CommonData(">=", ">=");
		operlist.add(vo);
		vo = new CommonData("<", "<");
		operlist.add(vo);
		vo = new CommonData("<=", "<=");
		operlist.add(vo);
		vo = new CommonData("<>", "<>");
		operlist.add(vo);
		vo = new CommonData("*", "并且");
		logiclist.add(vo);
		vo = new CommonData("+", "或");
		logiclist.add(vo);
	}
	
	
	public String getOrgparentcode() {
		return orgparentcode;
	}


	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}


	public String getIsInout_flag() {
		return isInout_flag;
	}
	public void setIsInout_flag(String isInout_flag) {
		this.isInout_flag = isInout_flag;
	}
	public String getCheckEm() {
		return checkEm;
	}
	public void setCheckEm(String checkEm) {
		this.checkEm = checkEm;
	}
	public String getCard_causation() {
		return card_causation;
	}
	public void setCard_causation(String card_causation) {
		this.card_causation = card_causation;
	}
	public String getCausation() {
		return causation;
	}
	public void setCausation(String causation) {
		this.causation = causation;
	}
	public String getComputer_name() {
		return computer_name;
	}
	public void setComputer_name(String computer_name) {
		this.computer_name = computer_name;
	}
	public String getInto_flag() {
		return into_flag;
	}
	public void setInto_flag(String into_flag) {
		this.into_flag = into_flag;
	}
	public String getIp_adr() {
		return ip_adr;
	}
	public void setIp_adr(String ip_adr) {
		this.ip_adr = ip_adr;
	}
	public String getTemp_emp_table() {
		return temp_emp_table;
	}
	public void setTemp_emp_table(String temp_emp_table) {
		this.temp_emp_table = temp_emp_table;
	}
	@Override
    public void outPutFormHM()
	{
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setWork_date((String)this.getFormHM().get("work_date"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setRepair_flag((String)this.getFormHM().get("repair_flag"));
		this.setStatr_date((String)this.getFormHM().get("statr_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		this.setClass_flag((String)this.getFormHM().get("class_flag"));
		this.setCycle_num((String)this.getFormHM().get("cycle_num"));
		this.setCycle_date((String)this.getFormHM().get("cycle_date"));
		this.setCycle_hh((String)this.getFormHM().get("cycle_hh"));
		this.setCycle_mm((String)this.getFormHM().get("cycle_mm"));
		this.setTemp_emp_table((String)this.getFormHM().get("temp_emp_table"));
		this.setComputer_name((String)this.getFormHM().get("computer_name"));
		this.setInto_flag((String)this.getFormHM().get("into_flag"));
		this.setIp_adr((String)this.getFormHM().get("ip_adr"));
		this.setCausation((String)this.getFormHM().get("causation"));
		this.setCard_causation((String)this.getFormHM().get("card_causation"));
		this.setCheckEm((String)this.getFormHM().get("checkEm"));
		this.setIsInout_flag((String)this.getFormHM().get("isInout_flag"));
//		this.setEasy_date((String)this.getFormHM().get(easy_date));
		this.setEnd_date12((String)this.getFormHM().get("end_date12"));
		this.setSelect_name((String)this.getFormHM().get("select_name"));
		this.setSelect_type((String)this.getFormHM().get("select_type"));
		this.setLeft_fields((String[])this.getFormHM().get("left_fields"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setSql_where((String)this.getFormHM().get("sql_where"));
		this.setNoCardFlag((String)this.getFormHM().get("noCardFlag"));
		this.setEasy_hh((String)this.getFormHM().get("easy_hh"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		this.setReflag((String)this.getFormHM().get("reflag"));
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("nbase",this.getNbase());
		this.getFormHM().put("work_date",this.getWork_date());
		this.getFormHM().put("repair_flag",this.getRepair_flag());
		this.getFormHM().put("statr_date",this.getStatr_date());
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("class_flag",this.getClass_flag());
		this.getFormHM().put("cycle_num",this.getCycle_num());
		this.getFormHM().put("cycle_date",this.getCycle_date());
		this.getFormHM().put("cycle_hh",this.getCycle_hh());
		this.getFormHM().put("cycle_mm",this.getCycle_mm());
		this.getFormHM().put("temp_emp_table",this.getTemp_emp_table());
		this.getFormHM().put("cur_session",this.getCur_session());
		if(this.getPagination()!=null)
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("causation",this.getCausation());
		this.getFormHM().put("ip_adr",this.getIp_adr());
		this.getFormHM().put("into_flag",this.getInto_flag());
		this.getFormHM().put("computer_name",this.getComputer_name());
		this.getFormHM().put("repair_fashion",repair_fashion);
		this.getFormHM().put("easy_date", this.getEasy_date());
		this.getFormHM().put("easy_hh", this.getEasy_hh());
		this.getFormHM().put("easy_mm",this.getEasy_mm());
		this.getFormHM().put("card_causation",this.getCard_causation());
		this.getFormHM().put("checkEm", checkEm);
		this.getFormHM().put("end_date12",this.getEnd_date12());
		this.getFormHM().put("select_name", this.getSelect_name());
		this.getFormHM().put("select_type", this.getSelect_type());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("left_fields", this.getLeft_fields());
		this.getFormHM().put("selectedlist", this.getSelectedlist());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("factorlist", this.getFactorlist());
		this.getFormHM().put("dblist", this.getDblist());
		this.getFormHM().put("like", this.getLike());
		this.getFormHM().put("sql_where", this.getSql_where());
		this.getFormHM().put("noCardFlag", this.getNoCardFlag());
		this.getFormHM().put("orgparentcode", this.getOrgparentcode());
		this.getFormHM().put("reflag", this.getReflag());
	}
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
    	if("/kq/machine/repair_card".equals(arg0.getPath())&&arg1.getParameter("b_save")!=null)
        {
    		 this.getFormHM().put("checkEm", "");
    		 this.setCheckEm("");
    		 arg1.setAttribute("targetWindow", "1");
        }    	
	   return super.validate(arg0, arg1);
	}
	public String getClass_flag() {
		return class_flag;
	}
	public void setClass_flag(String class_flag) {
		this.class_flag = class_flag;
	}
	public String getCycle_date() {
		return cycle_date;
	}
	public void setCycle_date(String cycle_date) {
		this.cycle_date = cycle_date;
	}
	public String getCycle_hh() {
		return cycle_hh;
	}
	public void setCycle_hh(String cycle_hh) {
		this.cycle_hh = cycle_hh;
	}
	public String getCycle_mm() {
		return cycle_mm;
	}
	public void setCycle_mm(String cycle_mm) {
		this.cycle_mm = cycle_mm;
	}
	public String getCycle_num() {
		return cycle_num;
	}
	public void setCycle_num(String cycle_num) {
		this.cycle_num = cycle_num;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getRepair_flag() {
		return repair_flag;
	}
	public void setRepair_flag(String repair_flag) {
		this.repair_flag = repair_flag;
	}
	public String getStatr_date() {
		return statr_date;
	}
	public void setStatr_date(String statr_date) {
		this.statr_date = statr_date;
	}
	public String getWork_date() {
		return work_date;
	}
	public void setWork_date(String work_date) {
		this.work_date = work_date;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getCur_session() {
		return cur_session;
	}
	public void setCur_session(String cur_session) {
		this.cur_session = cur_session;
	}
	public String getRepair_fashion() {
		return repair_fashion;
	}
	public void setRepair_fashion(String repair_fashion) {
		this.repair_fashion = repair_fashion;
	}
	public String getEasy_date() {
		return easy_date;
	}
	public void setEasy_date(String easy_date) {
		this.easy_date = easy_date;
	}
	public String getEasy_hh() {
		return easy_hh;
	}
	public void setEasy_hh(String easy_hh) {
		this.easy_hh = easy_hh;
	}
	public String getEasy_mm() {
		return easy_mm;
	}
	public void setEasy_mm(String easy_mm) {
		this.easy_mm = easy_mm;
	}
	public String getEnd_date12() {
		return end_date12;
	}
	public void setEnd_date12(String end_date12) {
		this.end_date12 = end_date12;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public String getSelect_type() {
		return select_type;
	}
	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String[] getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public ArrayList getSelectedlist() {
		return selectedlist;
	}
	public void setSelectedlist(ArrayList selectedlist) {
		this.selectedlist = selectedlist;
	}
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public ArrayList getFactorlist() {
		return factorlist;
	}
	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}
	public String getLike() {
		return like;
	}
	public void setLike(String like) {
		this.like = like;
	}
	public ArrayList getOperlist() {
		return operlist;
	}
	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}
	public ArrayList getLogiclist() {
		return logiclist;
	}
	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}

	public String getSql_where() {
		return sql_where;
	}

	public void setSql_where(String sql_where) {
		this.sql_where = sql_where;
	}


	public void setReflag(String reflag) {
		this.reflag = reflag;
	}


	public String getReflag() {
		return reflag;
	}


	public void setNoCardFlag(String noCardFlag) {
		this.noCardFlag = noCardFlag;
	}


	public String getNoCardFlag() {
		return noCardFlag;
	}

}
