package com.hjsj.hrms.actionform.gz.gz_accounting.piecerate;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PieceRateForm extends FrameForm {
	// 作业单用
	private PaginationForm pagelistform = new PaginationForm();
	private int current = 1;
	ArrayList fielditemlist = new ArrayList();// 字段列表 作业单用
	String count = "0";
	ArrayList tasktypelist = new ArrayList();
	String tasktype = "";
	String zhib1= "";
	String zhib2= "";
	String sp_status = "";
	String yuezb = "";
	String starttime = "";
	String endtime = "";
	ArrayList setUplist = new ArrayList();
	ArrayList setlist = new ArrayList();
	ArrayList setlist1 = new ArrayList();
	ArrayList zhibiaolist = new ArrayList();
	ArrayList zblist = new ArrayList();
	private String formula;//计算公式
	private String[] codesetid_arr;
	String tabid = "";
	String signtable = "";
	String jobtable = "";
	String s0102 = "";
	String managerpriv = "";

	//作业单明细用
	private String taskeditmodel = "";
	private String canEdit = "read";
	private String s0100 = "";


	public String getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(String canEdit) {
		this.canEdit = canEdit;
	}


	public String getS0100() {
		return s0100;
	}

	public void setS0100(String s0100) {
		this.s0100 = s0100;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("s0100", this.getS0100());
		this.getFormHM().put("count", this.getCount());
		this.getFormHM().put("tasktype", this.getTasktype());
		this.getFormHM().put("sp_status", this.getSp_status());
		this.getFormHM().put("starttime", this.getStarttime());
		this.getFormHM().put("endtime", this.getEndtime());
		this.getFormHM().put("pagerows",
				this.getPagerows() == 0 ? "10" : (this.getPagerows() + ""));
		this.getFormHM().put("formula",this.getFormula());
		this.getFormHM().put("zhib1", this.getZhib1());
		this.getFormHM().put("zhib2", this.getZhib2());
		this.getFormHM().put("managerpriv", this.getManagerpriv());
	}

	@Override
    public void outPutFormHM() {
		this.getPagelistform().setList(
				(ArrayList) this.getFormHM().get("datalist"));
		this.setFielditemlist((ArrayList) this.getFormHM().get("fielditemlist"));
		this.setTasktypelist((ArrayList) this.getFormHM().get("tasktypelist"));
		this.setCount((String) this.getFormHM().get("count"));
		/** 重新定位到当前页 */
		this.getPagelistform().getPagination().gotoPage(current);
		
		
		this.setS0100((String) this.getFormHM().get("s0100"));
		this.setTaskeditmodel((String)this.getFormHM().get("taskeditmodel"));
		this.setCanEdit((String) this.getFormHM().get("canEdit"));
		this.setSetUplist((ArrayList) this.getFormHM().get("setUplist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist1((ArrayList) this.getFormHM().get("setlist1"));
		this.setZblist((ArrayList) this.getFormHM().get("zblist"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setTabid((String) this.getFormHM().get("tabid"));
		this.setStarttime((String) this.getFormHM().get("starttime"));
		this.setEndtime((String) this.getFormHM().get("endtime"));
		this.setTasktype((String) this.getFormHM().get("tasktype"));
		this.setSp_status((String) this.getFormHM().get("sp_status"));
		this.setZhibiaolist((ArrayList) this.getFormHM().get("zhibiaolist"));
		this.setZhib1((String) this.getFormHM().get("zhib1"));
		this.setZhib2((String) this.getFormHM().get("zhib2"));
		this.setYuezb((String) this.getFormHM().get("yuezb"));
		this.setSigntable((String) this.getFormHM().get("signtable"));
		this.setJobtable((String) this.getFormHM().get("jobtable"));
		this.setS0102((String) this.getFormHM().get("s0102"));
		this.setManagerpriv((String) this.getFormHM().get("managerpriv"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	
		if("/gz/gz_accounting/piecerate/search_piecerate".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
			if ("link".equals(arg1.getParameter("b_query"))){
				if (this.pagelistform.getPagination() != null){
				    this.pagelistform.getPagination().firstPage();
				}
			}

        }

       if ((!(this.taskeditmodel==null)) && ("add".equals(this.taskeditmodel))){//如果是新增，则显示最后一页
    	   pagelistform.getPagination().firstPage();
    	   current = pagelistform.getPagination().getCurrent();
       }else{
    	   current=pagelistform.getPagination().getCurrent();
       }
        return super.validate(arg0, arg1);
       
    }


	public PaginationForm getPagelistform() {
		return pagelistform;
	}

	public void setPagelistform(PaginationForm pagelistform) {
		this.pagelistform = pagelistform;
	}


	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public ArrayList getTasktypelist() {
		return tasktypelist;
	}

	public void setTasktypelist(ArrayList tasktypeList) {
		this.tasktypelist = tasktypeList;
	}

	public String getTasktype() {
		return tasktype;
	}

	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}

	public String getSp_status() {
		return sp_status;
	}

	public void setSp_status(String sp_status) {
		this.sp_status = sp_status;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}


	public String getTaskeditmodel() {
		return taskeditmodel;
	}

	public void setTaskeditmodel(String taskeditmodel) {
		this.taskeditmodel = taskeditmodel;
	}

	public ArrayList getSetUplist() {
		return setUplist;
	}

	public void setSetUplist(ArrayList setUplist) {
		this.setUplist = setUplist;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public ArrayList getZhibiaolist() {
		return zhibiaolist;
	}

	public void setZhibiaolist(ArrayList zhibiaolist) {
		this.zhibiaolist = zhibiaolist;
	}

	public String getZhib1() {
		return zhib1;
	}

	public void setZhib1(String zhib1) {
		this.zhib1 = zhib1;
	}

	public String getZhib2() {
		return zhib2;
	}

	public void setZhib2(String zhib2) {
		this.zhib2 = zhib2;
	}

	public ArrayList getSetlist1() {
		return setlist1;
	}

	public void setSetlist1(ArrayList setlist1) {
		this.setlist1 = setlist1;
	}

	public ArrayList getZblist() {
		return zblist;
	}

	public void setZblist(ArrayList zblist) {
		this.zblist = zblist;
	}

	public String getYuezb() {
		return yuezb;
	}

	public void setYuezb(String yuezb) {
		this.yuezb = yuezb;
	}

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String getSigntable() {
		return signtable;
	}

	public void setSigntable(String signtable) {
		this.signtable = signtable;
	}

	public String getJobtable() {
		return jobtable;
	}

	public void setJobtable(String jobtable) {
		this.jobtable = jobtable;
	}

	public String getS0102() {
		return s0102;
	}

	public void setS0102(String s0102) {
		this.s0102 = s0102;
	}

	public String getManagerpriv() {
		return managerpriv;
	}

	public void setManagerpriv(String managerpriv) {
		this.managerpriv = managerpriv;
	}

}
