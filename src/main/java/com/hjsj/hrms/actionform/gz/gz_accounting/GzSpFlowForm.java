package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * 工资审批流程
 * <p>Title:GzSpFlowForm.java</p>
 * <p>Description>:GzSpFlowForm.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 17, 2010  5:18:19 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GzSpFlowForm extends FrameForm{

	/**权限范围内的工资套列表*/
	private ArrayList salaryList = new ArrayList();
	/**工资类别号*/
	private String salaryid;
	/**审批过程数据列表*/
	 private PaginationForm spDataListform=new PaginationForm();
	/**业务日期列表*/
	private ArrayList busiDateList = new ArrayList();
	/**业务日期*/
	private String busiDate;
	/**薪资员列表*/
	private ArrayList usrNameList = new ArrayList();
	/**薪资员*/
	private String usrName;
	/**审批状态列表*/
	private ArrayList spFlagList = new ArrayList();
	/**审批状态*/
	private String spFlag;
	/**当前操作人列表*/
	private ArrayList currList = new ArrayList();
	/**当前操作人*/
	private String curr;
	/**次数列表*/
	private ArrayList countList = new ArrayList();
	/**次数*/
	private String count;
	/**薪资或者保险标志=0工资*/
	private String gz_module;
	private String hasData;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("hasData", this.getHasData());
		this.getFormHM().put("gz_module", this.getGz_module());
		this.getFormHM().put("salaryid", this.getSalaryid());
		this.getFormHM().put("busiDate", this.getBusiDate());
		this.getFormHM().put("count", this.getCount());
		this.getFormHM().put("selectedList",this.getSpDataListform().getSelectedList());
		this.getFormHM().put("spFlag", this.getSpFlag());
		this.getFormHM().put("usrName", this.getUsrName());
		this.getFormHM().put("curr", this.getCurr());
	}

	@Override
    public void outPutFormHM() {
		this.setHasData((String)this.getFormHM().get("hasData"));
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.setBusiDate((String)this.getFormHM().get("busiDate"));
		this.setBusiDateList((ArrayList)this.getFormHM().get("busiDateList"));
		this.setSalaryList((ArrayList)this.getFormHM().get("salaryList"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setCountList((ArrayList)this.getFormHM().get("countList"));
		this.getSpDataListform().setList((ArrayList)this.getFormHM().get("spDataList"));
		this.setSpFlagList((ArrayList) this.getFormHM().get("spFlagList"));
		this.setSpFlag((String) this.getFormHM().get("spFlag"));
		this.setUsrNameList((ArrayList) this.getFormHM().get("usrNameList"));
		this.setUsrName((String) this.getFormHM().get("usrName"));
		this.setCurrList((ArrayList) this.getFormHM().get("currList"));
		this.setCurr((String) this.getFormHM().get("curr"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   try
	   {  
		  if("/gz/gz_accounting/sp_flow/init_sp_flow".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null)
	       {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();
	            if(this.getSpDataListform().getPagination()!=null)
	            	this.getSpDataListform().getPagination().firstPage();
	       }	
	   }catch(Exception e)
	   {
	   	  e.printStackTrace();
	   }
       return super.validate(arg0, arg1);
	}

	public ArrayList getSalaryList() {
		return salaryList;
	}

	public void setSalaryList(ArrayList salaryList) {
		this.salaryList = salaryList;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}
	public ArrayList getBusiDateList() {
		return busiDateList;
	}

	public void setBusiDateList(ArrayList busiDateList) {
		this.busiDateList = busiDateList;
	}

	public ArrayList getCountList() {
		return countList;
	}

	public void setCountList(ArrayList countList) {
		this.countList = countList;
	}

	public String getBusiDate() {
		return busiDate;
	}

	public void setBusiDate(String busiDate) {
		this.busiDate = busiDate;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getHasData() {
		return hasData;
	}

	public void setHasData(String hasData) {
		this.hasData = hasData;
	}

	public PaginationForm getSpDataListform() {
		return spDataListform;
	}

	public void setSpDataListform(PaginationForm spDataListform) {
		this.spDataListform = spDataListform;
	}

	public ArrayList getUsrNameList() {
		return usrNameList;
	}

	public void setUsrNameList(ArrayList usrNameList) {
		this.usrNameList = usrNameList;
	}

	public String getUsrName() {
		return usrName;
	}

	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}

	public ArrayList getSpFlagList() {
		return spFlagList;
	}

	public void setSpFlagList(ArrayList spFlagList) {
		this.spFlagList = spFlagList;
	}

	public String getSpFlag() {
		return spFlag;
	}

	public void setSpFlag(String spFlag) {
		this.spFlag = spFlag;
	}

	public ArrayList getCurrList() {
		return currList;
	}

	public void setCurrList(ArrayList currList) {
		this.currList = currList;
	}

	public String getCurr() {
		return curr;
	}

	public void setCurr(String curr) {
		this.curr = curr;
	}

}
