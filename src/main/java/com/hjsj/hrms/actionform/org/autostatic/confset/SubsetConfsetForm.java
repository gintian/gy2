package com.hjsj.hrms.actionform.org.autostatic.confset;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SubsetConfsetForm extends FrameForm{

	private String changeflag;
	private String changeflagstr;
	private List subsetlist;
	private List confsetlist;
	private String subset;
	private String subsetstr;
	private String monthnum="0";
	private String yearnum;
	private String view_hide;
	private ArrayList fielitemlist;
	private ArrayList levellist; //级别list
	private String level;
	private String hideitemid; //显示指标id字符串
	private String areavalue; //显示部门
	
	private String scan_table; 
	private String view_scan; //扫描库
	
	private String included_table; //生成选择自动载入上期数据table表
	
	private String fielditemid;
	private String selectsql;
	private String wheresql;
	private String column;
	private List retlist;
	
	private String tablename;
	
	private ArrayList fieldlist;
	private ArrayList sortfieldlist = new ArrayList();
	private String sort_fields="";
	
	private FormFile picturefile;
	private String checkClose="";
	private String checkflag="";
	private String inforflag="";
    private String returnvalue;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSubsetlist((List) hm.get("subsetlist"));
		this.setConfsetlist((List) hm.get("confsetlist"));
		this.setMonthnum((String) hm.get("monthnum"));
		this.setYearnum((String) hm.get("yearnum"));
		this.setSubset((String) hm.get("subset"));
		this.setView_hide((String) hm.get("view_hide"));
		this.setSelectsql((String) hm.get("selectsql"));
		this.setWheresql((String) hm.get("wheresql"));
		this.setColumn((String) hm.get("column"));
		this.setRetlist((List) hm.get("retlist"));
		this.setFielitemlist((ArrayList) hm.get("fielitemlist"));
		this.setLevellist((ArrayList) hm.get("levellist"));
		this.setLevel((String) hm.get("level"));
		this.setHideitemid((String) hm.get("hideitemid"));
		this.setAreavalue((String) hm.get("areavalue"));
		this.setScan_table((String) hm.get("scan_table"));
		this.setView_scan((String) hm.get("view_scan"));
		this.setFieldlist((ArrayList) hm.get("fieldlist"));
		this.setTablename((String) hm.get("tablename"));
		this.setChangeflagstr((String) hm.get("changeflagstr"));
		this.setIncluded_table((String) hm.get("included_table"));
		this.setSort_fields((String) hm.get("sort_fields"));
		this.setSortfieldlist((ArrayList) hm.get("sortfieldlist"));
		this.setPicturefile((FormFile)this.getFormHM().get("picturefile"));
		this.setCheckClose((String)this.getFormHM().get("checkClose"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setInforflag((String)this.getFormHM().get("inforflag"));

	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("subset",this.getSubset());
		hm.put("subsetstr",this.getSubsetstr());
		hm.put("monthnum",this.getMonthnum());
		hm.put("yearnum",this.getYearnum());
		hm.put("level",this.getLevel());
		hm.put("fielditemid",this.getFielditemid());
		hm.put("hideitemid",this.getHideitemid());
		hm.put("areavalue",this.getAreavalue());
		hm.put("view_scan",this.getView_scan());
		hm.put("changeflagstr",this.getChangeflagstr());
		this.getFormHM().put("picturefile",this.getPicturefile());

	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
		return super.validate(arg0, arg1);
	}
	public String getChangeflag() {
		return changeflag;
	}

	public void setChangeflag(String changeflag) {
		this.changeflag = changeflag;
	}
	public List getSubsetlist() {
		return subsetlist;
	}

	public void setSubsetlist(List subsetlist) {
		this.subsetlist = subsetlist;
	}

	public List getConfsetlist() {
		return confsetlist;
	}

	public void setConfsetlist(List confsetlist) {
		this.confsetlist = confsetlist;
	}

	public String getSubset() {
		return subset;
	}

	public void setSubset(String subset) {
		this.subset = subset;
	}

	public String getSubsetstr() {
		return subsetstr;
	}

	public void setSubsetstr(String subsetstr) {
		this.subsetstr = subsetstr;
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

	public String getView_hide() {
		return view_hide;
	}

	public void setView_hide(String view_hide) {
		this.view_hide = view_hide;
	}

	public String getFielditemid() {
		return fielditemid;
	}

	public void setFielditemid(String fielditemid) {
		this.fielditemid = fielditemid;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getSelectsql() {
		return selectsql;
	}

	public void setSelectsql(String selectsql) {
		this.selectsql = selectsql;
	}

	public String getWheresql() {
		return wheresql;
	}

	public void setWheresql(String wheresql) {
		this.wheresql = wheresql;
	}

	public List getRetlist() {
		return retlist;
	}

	public void setRetlist(List retlist) {
		this.retlist = retlist;
	}

	public ArrayList getFielitemlist() {
		return fielitemlist;
	}

	public void setFielitemlist(ArrayList fielitemlist) {
		this.fielitemlist = fielitemlist;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public ArrayList getLevellist() {
		return levellist;
	}

	public void setLevellist(ArrayList levellist) {
		this.levellist = levellist;
	}

	public String getHideitemid() {
		return hideitemid;
	}

	public void setHideitemid(String hideitemid) {
		this.hideitemid = hideitemid;
	}

	public String getAreavalue() {
		return areavalue;
	}

	public void setAreavalue(String areavalue) {
		this.areavalue = areavalue;
	}

	public String getScan_table() {
		return scan_table;
	}

	public void setScan_table(String scan_table) {
		this.scan_table = scan_table;
	}

	public String getView_scan() {
		return view_scan;
	}

	public void setView_scan(String view_scan) {
		this.view_scan = view_scan;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getChangeflagstr() {
		return changeflagstr;
	}

	public void setChangeflagstr(String changeflagstr) {
		this.changeflagstr = changeflagstr;
	}

	public String getIncluded_table() {
		return included_table;
	}

	public void setIncluded_table(String included_table) {
		this.included_table = included_table;
	}

	public String getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String sort_fields) {
		this.sort_fields = sort_fields;
	}

	public ArrayList getSortfieldlist() {
		return sortfieldlist;
	}

	public void setSortfieldlist(ArrayList sortfieldlist) {
		this.sortfieldlist = sortfieldlist;
	}

	public String getCheckClose() {
		return checkClose;
	}

	public void setCheckClose(String checkClose) {
		this.checkClose = checkClose;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}

	public String getInforflag() {
		return inforflag;
	}

	public void setInforflag(String inforflag) {
		this.inforflag = inforflag;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}






}
