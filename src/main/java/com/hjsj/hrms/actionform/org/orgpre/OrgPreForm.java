package com.hjsj.hrms.actionform.org.orgpre;

import com.hjsj.hrms.valueobject.sys.AutoArrayList;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class OrgPreForm extends FrameForm {
	private String ctrl_type;//是否进行部门总额控制
	private String setid; //单位编制子集
	private String sp_flag; //审批状态标识
	private String planitem; //计划人数
	private String realitem; //实有人数
	private String flag; //状态
	private String a_code; 
	private String infor; 
	private String unit_type; 
	private String tablename; 
	private String sqlstr; 
	private ArrayList itemlist = new ArrayList(); 
	private String cloumstr;
	private ArrayList resultlist = new ArrayList(); //包含子集中的指标
	private ArrayList splist = new ArrayList(); 
	private String sql;
	private String wherestr;
	private String columns;
	private String orderby;
	private String dpname;
	private String nextlevel;//是否分级管理
	private String levelnext;//是否控制下一级
	private String view_scan;
	private String level;
	private String b0110;
	
	private String monthnum;
	private String yearnum;
	private String returnvalue;
	private String viewhide;
	private ArrayList statNumItemlist = new ArrayList();
	private ArrayList fieldlist=new ArrayList();
	private String fielditemid;
	private String rb0110;
	private String fromway;
	private String ps_parttime;
	private String ps_workparttime;
	
	
	//编制查询功能
	private ArrayList setList; 
	private ArrayList itemids = new AutoArrayList(String.class);
	private ArrayList logics = new AutoArrayList(String.class);
	private ArrayList factors = new AutoArrayList(String.class);
	private ArrayList itemvalues = new AutoArrayList(String.class);
	private String searchstr;
	private String searchWhere;
	private String searchOrg;
    private String querylike;
    private String searchtype;
    private String privOrg;
	private String levelctrl;
    

	public String getLevelctrl() {
		return levelctrl;
	}

	public void setLevelctrl(String levelctrl) {
		this.levelctrl = levelctrl;
	}

	public String getPrivOrg() {
		return privOrg;
	}

	public void setPrivOrg(String privOrg) {
		this.privOrg = privOrg;
	}

	public String getQuerylike() {
		return querylike;
	}

	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}

	public String getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setCtrl_type((String)this.getFormHM().get("ctrl_type"));
		this.setSetid((String)this.getFormHM().get("setid"));
		this.setSp_flag((String)this.getFormHM().get("sp_flag"));
		this.setPlanitem((String)this.getFormHM().get("planitem"));
		this.setRealitem((String)this.getFormHM().get("realitem"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setInfor((String)this.getFormHM().get("infor"));
		this.setUnit_type((String)this.getFormHM().get("unit_type"));
		this.setCloumstr((String)this.getFormHM().get("cloumstr"));
		this.setSplist((ArrayList)this.getFormHM().get("splist"));
		this.setResultlist((ArrayList)this.getFormHM().get("resultlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setWherestr((String)this.getFormHM().get("wherestr"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setDpname((String)this.getFormHM().get("dpname"));
		this.setNextlevel((String)this.getFormHM().get("nextlevel"));
		this.setView_scan((String)this.getFormHM().get("view_scan"));
		this.setLevel((String)this.getFormHM().get("level"));
		this.setYearnum((String)this.getFormHM().get("yearnum"));
		this.setMonthnum((String)this.getFormHM().get("monthnum"));
		this.setLevelnext((String)this.getFormHM().get("levelnext"));
		this.setViewhide((String)this.getFormHM().get("viewhide"));
		this.setStatNumItemlist((ArrayList)this.getFormHM().get("statNumItemlist"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setPs_workparttime((String)this.getFormHM().get("ps_workparttime"));
		this.setPs_parttime((String)this.getFormHM().get("ps_parttime"));
		this.setSetList((ArrayList)this.getFormHM().get("setList"));
		this.setItemids((ArrayList)this.getFormHM().get("itemids"));
		this.setLogics((ArrayList)this.getFormHM().get("logics"));
		this.setItemvalues((ArrayList)this.getFormHM().get("itemvalues"));
		this.setFactors((ArrayList)this.getFormHM().get("factors"));
		this.searchstr=(String)this.getFormHM().get("searchstr");
		this.searchWhere=(String)this.getFormHM().get("searchWhere");
		this.setSearchOrg((String)this.getFormHM().get("searchOrg"));
		this.getFormHM().remove("searchOrg");
		this.setPrivOrg((String)this.getFormHM().get("privOrg"));
		this.setLevelctrl((String)this.getFormHM().get("levelctrl"));
	}

	public String getSearchOrg() {
		return searchOrg;
	}

	public void setSearchOrg(String searchOrg) {
		this.searchOrg = searchOrg;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("ctrl_type",this.getCtrl_type());
		this.getFormHM().put("setid",this.getSetid());
		this.getFormHM().put("sp_flag",this.getSp_flag());
		this.getFormHM().put("planitem",this.getPlanitem());
		this.getFormHM().put("realitem",this.getRealitem());
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("view_scan", view_scan);
		this.getFormHM().put("monthnum", monthnum);
		this.getFormHM().put("yearnum", yearnum);
		this.getFormHM().put("fielditemid", fielditemid);
		this.getFormHM().put("b0110", b0110);
		this.getFormHM().put("itemids", this.getItemids());
		this.getFormHM().put("logics", logics);
		this.getFormHM().put("factors", factors);
		this.getFormHM().put("searchstr", searchstr);
		this.getFormHM().put("itemvalues", itemvalues);
		this.getFormHM().put("searchWhere", searchWhere);
		this.getFormHM().put("querylike", querylike);
		this.getFormHM().put("searchtype", searchtype);
		this.getFormHM().put("privOrg", privOrg);
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	   try{
		   if("/org/orgpre/orgpretable".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
			   if(this.getPagination()!=null)
				   this.getPagination().firstPage();
			   //提示信息，中不显示返回按钮
			   arg1.setAttribute("targetWindow", "0");
		   }else  if("/org/orgpre/deptable".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
	            /**定位到首页,*/
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();
	      }else  if("/org/orgpre/postable".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
	            /**定位到首页,*/
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();
	      }	else  if("/org/orgpre/showstatnum".equals(arg0.getPath())&&arg1.getParameter("b_showstatnum")!=null){
	            /**定位到首页,*/
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();
	      }	 else  if("/org/orgpre/orgpretable".equals(arg0.getPath())&&arg1.getParameter("b_search2")!=null){
			    arg1.setAttribute("targetWindow", "1");
		  }		
	   }catch(Exception e){
	   	  e.printStackTrace();
	   }
       return super.validate(arg0, arg1);
	}
	public String getCtrl_type() {
		return ctrl_type;
	}

	public void setCtrl_type(String ctrl_type) {
		this.ctrl_type = ctrl_type;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public String getUnit_type() {
		return unit_type;
	}

	public void setUnit_type(String unit_type) {
		this.unit_type = unit_type;
	}

	public String getCloumstr() {
		return cloumstr;
	}

	public void setCloumstr(String cloumstr) {
		this.cloumstr = cloumstr;
	}

	public ArrayList getSplist() {
		return splist;
	}

	public void setSplist(ArrayList splist) {
		this.splist = splist;
	}

	public ArrayList getResultlist() {
		return resultlist;
	}

	public void setResultlist(ArrayList resultlist) {
		this.resultlist = resultlist;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}

	public String getDpname() {
		return dpname;
	}

	public void setDpname(String dpname) {
		this.dpname = dpname;
	}

	public String getNextlevel() {
		return nextlevel;
	}

	public void setNextlevel(String nextlevel) {
		this.nextlevel = nextlevel;
	}

	public String getView_scan() {
		return view_scan;
	}

	public void setView_scan(String view_scan) {
		this.view_scan = view_scan;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMonthnum() {
		return monthnum;
	}

	public void setMonthnum(String monthnum) {
		this.monthnum = monthnum;
	}

	public String getYearnum() {
		return yearnum;
	}

	public void setYearnum(String yearnum) {
		this.yearnum = yearnum;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getLevelnext() {
		return levelnext;
	}

	public void setLevelnext(String levelnext) {
		this.levelnext = levelnext;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getViewhide() {
		return viewhide;
	}

	public void setViewhide(String viewhide) {
		this.viewhide = viewhide;
	}

	public ArrayList getStatNumItemlist() {
		return statNumItemlist;
	}

	public void setStatNumItemlist(ArrayList statNumItemlist) {
		this.statNumItemlist = statNumItemlist;
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

	public String getRb0110() {
		return rb0110;
	}

	public void setRb0110(String rb0110) {
		this.rb0110 = rb0110;
	}

	public String getFromway() {
		return fromway;
	}

	public void setFromway(String fromway) {
		this.fromway = fromway;
	}

	public String getPs_parttime() {
		return ps_parttime;
	}

	public void setPs_parttime(String ps_parttime) {
		this.ps_parttime = ps_parttime;
	}

	public String getPs_workparttime() {
		return ps_workparttime;
	}

	public void setPs_workparttime(String ps_workparttime) {
		this.ps_workparttime = ps_workparttime;
	}

	public ArrayList getSetList() {
		return setList;
	}

	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}
	
	public ArrayList getItemids() {
		return itemids;
	}

	public void setItemids(ArrayList itemids) {
		this.itemids.clear();
		this.itemids.addAll(itemids);
	}

	public ArrayList getLogics() {
		return logics;
	}

	public void setLogics(ArrayList logics) {
		this.logics.clear();
		this.logics.addAll(logics);
	}

	public ArrayList getFactors() {
		return factors;
	}

	public void setFactors(ArrayList factors) {
		this.factors.clear();
		this.factors.addAll(logics);
	}

	public ArrayList getItemvalues() {
		return itemvalues;
	}

	public void setItemvalues(ArrayList itemvalues) {
		this.itemvalues.clear();
		this.itemvalues.addAll(logics);
	}
	
	

}
