package com.hjsj.hrms.actionform.pos;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class PosCodeParameterForm extends FrameForm {

	private String ps_code;
	private String ps_superior;
	private String ps_job;
	private String ps_c_job;
	private String ps_set;
	private String ps_workfixed;
	private String ps_workexist;
	private String ps_parttime;
	private String ps_workparttime;
	private String sqlstr;
	private String sqlstrc;
	private String sp_flag;
	private String zw_set;
	private String zwvalid;
	private String mode;
	/*
	 * 单位编制子集 
	 */
	private String unit_set;
	/*
	 * 单位编制设置
	 * B07|B0XXX,B0YYY
	 * 单位编制，第一个指标为单位编制数，第二个指标为实有人员
	 * 单位编制
	 */
	private String plan_num;
	/*
	 * 单位实有人数
	 */
	private String true_num;
	/*
	 * 是否进行单位编制控制
	 * =0不进行
	 * =1进行控制
	 */
	private String UNITValid;
	/*
	 * 是否进行职位编制控制
	 * =0不进行
	 * =1进行
	 */
	private String PSValid;
	/*
	 * flag=null =all全部 
	 * =unit 单位
	 * =pos 职位
	 */
	private String nextlevel;
	/**
	 * 是否分级管理
	 */
	private String flag;
	private ArrayList fieldsetlist  = new ArrayList();
	private ArrayList spflaglist = new ArrayList();
	private String table;
	private String expr;
	private String planitem;
	private String realitem;
	private String staticitem;
	private String flagitem;
	private String methoditem;
	private String conditem;
	private String messitem;
	private String ctrlorgitem;
	private String nextorgitem;
	

	private ArrayList list = new ArrayList();
	private String org_flag;
	private ArrayList dbprelist = new ArrayList();
	private String dbpre;
	private String numitemid;
	private String ps_level_code;//职位级别代码类 2009-11-18 许建
	private String ps_c_code;//岗/职位代码类
	private String ps_c_codeflag="N";//岗位代码类可修改标示  Y：可修改， N： 不可修改
	
	private String controlitemid; //启用编制控制指标
	private ArrayList controlitemids = new ArrayList(); //关联代码类45的指标
	private String controlOrgDesc;
	
	public String getControlOrgDesc() {
		return controlOrgDesc;
	}

	public void setControlOrgDesc(String controlOrgDesc) {
		this.controlOrgDesc = controlOrgDesc;
	}

	public ArrayList getControlitemids() {
		return controlitemids;
	}

	public void setControlitemids(ArrayList controlitemids) {
		this.controlitemids = controlitemids;
	}

	public String getControlitemid() {
		return controlitemid;
	}

	public void setControlitemid(String controlitemid) {
		this.controlitemid = controlitemid;
	}

	public String getPs_c_codeflag() {
		return ps_c_codeflag;
	}

	public void setPs_c_codeflag(String ps_c_codeflag) {
		this.ps_c_codeflag = ps_c_codeflag;
	}
    
	public String getMessitem() {
		return messitem;
	}

	public void setMessitem(String messitem) {
		this.messitem = messitem;
	}

	
	public String getCtrlorgitem() {
		return ctrlorgitem;
	}

	public void setCtrlorgitem(String ctrlorgitem) {
		this.ctrlorgitem = ctrlorgitem;
	}

	public String getNextorgitem() {
		return nextorgitem;
	}

	public void setNextorgitem(String nextorgitem) {
		this.nextorgitem = nextorgitem;
	}


	private String ps_c_level_code;//岗/职位级别代码类
	private String unit_code_field;
	private String pos_code_field;
	private String returnvalue;
	/*原来设置单位转换代码的字段*/
	private String oldUnits;
	/*原来设置岗位转换代码的字段*/
	private String oldPosts;
	
	private ArrayList unit_code_fieldlist= new ArrayList();
	private ArrayList pos_code_fieldlist= new ArrayList();
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

	public String getStaticitem() {
		return staticitem;
	}

	public void setStaticitem(String staticitem) {
		this.staticitem = staticitem;
	}

	@Override
    public void outPutFormHM() {
		 // TODO Auto-generated method stub
		this.setPs_code((String)this.getFormHM().get("ps_code"));
		this.setPs_job((String)this.getFormHM().get("ps_job"));
		this.setPs_c_job((String)this.getFormHM().get("ps_c_job"));
		this.setPs_superior((String)this.getFormHM().get("ps_superior"));
		this.setPs_workexist((String)this.getFormHM().get("ps_workexist"));
		this.setPs_workfixed((String)this.getFormHM().get("ps_workfixed"));
		this.setPs_set((String)this.getFormHM().get("ps_set"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setSqlstrc((String)this.getFormHM().get("sqlstrc"));
		this.setUnit_set((String) this.getFormHM().get("unit_set"));
		this.setUNITValid((String) this.getFormHM().get("unitvalid"));
		this.setPSValid((String) this.getFormHM().get("psvalid"));
		this.setPlan_num((String) this.getFormHM().get("plan_num"));
		this.setTrue_num((String) this.getFormHM().get("true_num"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setFieldsetlist((ArrayList)this.getFormHM().get("fieldsetlist"));
		this.setSpflaglist((ArrayList)this.getFormHM().get("spflaglist"));
		this.setSp_flag((String)this.getFormHM().get("sp_flag"));
		this.setTable((String)this.getFormHM().get("table"));
		this.setExpr((String)this.getFormHM().get("expr"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setZw_set((String)this.getFormHM().get("zw_set"));
		this.setZwvalid((String)this.getFormHM().get("zwvalid"));
		this.setMode((String)this.getFormHM().get("mode"));
		this.setOrg_flag((String)this.getFormHM().get("org_flag"));
		this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setNextlevel((String)this.getFormHM().get("nextlevel"));
		this.setNumitemid((String)this.getFormHM().get("numitemid"));
		this.setPs_level_code((String)this.getFormHM().get("ps_level_code"));
		this.setPs_c_code((String)this.getFormHM().get("ps_c_code"));
		this.setPs_c_level_code((String)this.getFormHM().get("ps_c_level_code"));
		this.setUnit_code_field((String)this.getFormHM().get("unit_code_field"));
		this.setPos_code_field((String)this.getFormHM().get("pos_code_field"));
		this.setUnit_code_fieldlist((ArrayList)this.getFormHM().get("unit_code_fieldlist"));
		this.setPos_code_fieldlist((ArrayList)this.getFormHM().get("pos_code_fieldlist"));
		this.setPs_workparttime((String)this.getFormHM().get("ps_workparttime"));
		this.setPs_parttime((String)this.getFormHM().get("ps_parttime"));
		this.setPs_c_codeflag((String)this.getFormHM().get("ps_c_codeflag"));
		this.setControlitemid((String)this.getFormHM().get("controlitemid"));
		this.setControlitemids((ArrayList)this.getFormHM().get("controlitemids"));
		this.setControlOrgDesc((String)this.getFormHM().get("controlOrgDesc"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("ps_code",this.ps_code);
		this.getFormHM().put("ps_job",this.ps_job);
		this.getFormHM().put("ps_c_job",this.ps_c_job);
		this.getFormHM().put("ps_superior",this.ps_superior);
		this.getFormHM().put("ps_workexist",this.ps_workexist);
		this.getFormHM().put("ps_workfixed",this.ps_workfixed);
		this.getFormHM().put("ps_set",this.ps_set);
		this.getFormHM().put("unit_set",this.unit_set);
		this.getFormHM().put("unitvalid",this.UNITValid);
		this.getFormHM().put("psvalid",this.PSValid);
		this.getFormHM().put("plan_num",this.plan_num);
		this.getFormHM().put("true_num",this.true_num);
		this.getFormHM().put("sp_flag",sp_flag);
		this.getFormHM().put("planitem",planitem);
		this.getFormHM().put("realitem",realitem);
		this.getFormHM().put("staticitem",staticitem);
		this.getFormHM().put("flagitem",flagitem);
		this.getFormHM().put("methoditem",methoditem);
		this.getFormHM().put("conditem",conditem);
		this.getFormHM().put("messitem", messitem);
		this.getFormHM().put("ctrlorgitem", ctrlorgitem);
		this.getFormHM().put("nextorgitem", nextorgitem);
		this.getFormHM().put("zw_set",zw_set);
		this.getFormHM().put("zwvalid",zwvalid);
		this.getFormHM().put("mode", mode);
		this.getFormHM().put("dbpre",dbpre);
		this.getFormHM().put("nextlevel",this.nextlevel);
		this.getFormHM().put("ps_level_code", ps_level_code);
		this.getFormHM().put("ps_c_code", ps_c_code);
		this.getFormHM().put("ps_c_level_code", ps_c_level_code);
		this.getFormHM().put("unit_code_field", unit_code_field);
		this.getFormHM().put("pos_code_field", pos_code_field);
		this.getFormHM().put("oldUnits", this.getOldUnits());
		this.getFormHM().put("oldPosts", this.getOldPosts());
		this.getFormHM().put("ps_workparttime", ps_workparttime);
		this.getFormHM().put("controlitemid", this.getControlitemid());
	}

	public String getPs_code() {
		return ps_code;
	}

	public void setPs_code(String ps_code) {
		this.ps_code = ps_code;
	}

	public String getPs_job() {
		return ps_job;
	}

	public void setPs_job(String ps_job) {
		this.ps_job = ps_job;
	}

	public String getPs_superior() {
		return ps_superior;
	}

	public void setPs_superior(String ps_superior) {
		this.ps_superior = ps_superior;
	}

	public String getPs_workexist() {
		return ps_workexist;
	}

	public void setPs_workexist(String ps_workexist) {
		this.ps_workexist = ps_workexist;
	}

	public String getPs_workfixed() {
		return ps_workfixed;
	}

	public void setPs_workfixed(String ps_workfixed) {
		this.ps_workfixed = ps_workfixed;
	}

	public String getPs_set() {
		return ps_set;
	}

	public void setPs_set(String ps_set) {
		this.ps_set = ps_set;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getUnit_set() {
		return unit_set;
	}

	public void setUnit_set(String unit_set) {
		this.unit_set = unit_set;
	}

	public String getPlan_num() {
		return plan_num;
	}

	public void setPlan_num(String plan_num) {
		this.plan_num = plan_num;
	}

	public String getPSValid() {
		return PSValid;
	}

	public void setPSValid(String valid) {
		PSValid = valid;
	}

	public String getTrue_num() {
		return true_num;
	}

	public void setTrue_num(String true_num) {
		this.true_num = true_num;
	}

	public String getUNITValid() {
		return UNITValid;
	}

	public void setUNITValid(String valid) {
		UNITValid = valid;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
	}

	public ArrayList getSpflaglist() {
		return spflaglist;
	}

	public void setSpflaglist(ArrayList spflaglist) {
		this.spflaglist = spflaglist;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getFlagitem() {
		return flagitem;
	}

	public void setFlagitem(String flagitem) {
		this.flagitem = flagitem;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String getZw_set() {
		return zw_set;
	}

	public void setZw_set(String zw_set) {
		this.zw_set = zw_set;
	}

	public String getZwvalid() {
		return zwvalid;
	}

	public void setZwvalid(String zwvalid) {
		this.zwvalid = zwvalid;
	}

	public String getOrg_flag() {
		return org_flag;
	}

	public void setOrg_flag(String org_flag) {
		this.org_flag = org_flag;
	}

	public ArrayList getDbprelist() {
		return dbprelist;
	}

	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getNextlevel() {
		return nextlevel;
	}

	public void setNextlevel(String nextlevel) {
		this.nextlevel = nextlevel;
	}

	public String getMethoditem() {
		return methoditem;
	}

	public void setMethoditem(String methoditem) {
		this.methoditem = methoditem;
	}

	public String getConditem() {
		return conditem;
	}

	public void setConditem(String conditem) {
		this.conditem = conditem;
	}

	public String getNumitemid() {
		return numitemid;
	}

	public void setNumitemid(String numitemid) {
		this.numitemid = numitemid;
	}

	public String getPs_level_code() {
		return ps_level_code;
	}

	public void setPs_level_code(String ps_level_code) {
		this.ps_level_code = ps_level_code;
	}

	public String getPs_c_code() {
		return ps_c_code;
	}

	public void setPs_c_code(String ps_c_code) {
		this.ps_c_code = ps_c_code;
	}

	public String getPs_c_level_code() {
		return ps_c_level_code;
	}

	public void setPs_c_level_code(String ps_c_level_code) {
		this.ps_c_level_code = ps_c_level_code;
	}

	public String getUnit_code_field() {
		return unit_code_field;
	}

	public void setUnit_code_field(String unit_code_field) {
		this.unit_code_field = unit_code_field;
	}

	public String getPos_code_field() {
		return pos_code_field;
	}

	public void setPos_code_field(String pos_code_field) {
		this.pos_code_field = pos_code_field;
	}

	public ArrayList getUnit_code_fieldlist() {
		return unit_code_fieldlist;
	}

	
	public void setUnit_code_fieldlist(ArrayList unit_code_fieldlist) {
		this.unit_code_fieldlist = unit_code_fieldlist;
	}

	public ArrayList getPos_code_fieldlist() {
		return pos_code_fieldlist;
	}

	public void setPos_code_fieldlist(ArrayList pos_code_fieldlist) {
		this.pos_code_fieldlist = pos_code_fieldlist;
	}

	public String getOldUnits() {
		return oldUnits;
	}

	public void setOldUnits(String oldUnits) {
		this.oldUnits = oldUnits;
	}

	public String getOldPosts() {
		return oldPosts;
	}

	public void setOldPosts(String oldPosts) {
		this.oldPosts = oldPosts;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getPs_c_job() {
		return ps_c_job;
	}

	public void setPs_c_job(String ps_c_job) {
		this.ps_c_job = ps_c_job;
	}

	public String getSqlstrc() {
		return sqlstrc;
	}

	public void setSqlstrc(String sqlstrc) {
		this.sqlstrc = sqlstrc;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPs_workparttime() {
		return ps_workparttime;
	}

	public void setPs_workparttime(String ps_workparttime) {
		this.ps_workparttime = ps_workparttime;
	}

	public String getPs_parttime() {
		return ps_parttime;
	}

	public void setPs_parttime(String ps_parttime) {
		this.ps_parttime = ps_parttime;
	}

}
