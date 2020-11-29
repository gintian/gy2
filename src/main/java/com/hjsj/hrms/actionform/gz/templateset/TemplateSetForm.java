/**
 * 
 */
package com.hjsj.hrms.actionform.gz.templateset;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *<p>Title:TemplateSetForm</p> 
 *<p>Description:薪资类别</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-29:下午05:34:21</p> 
 *@author cmq
 *@version 4.0
 */
public class TemplateSetForm extends FrameForm {
	/**
	 * 权限范围内的薪资类别列表
	 */
	private ArrayList setlist=new ArrayList();
	private ArrayList setlist2=new ArrayList();
    private PaginationForm setlistform=new PaginationForm();
    
    private String returnvalue="";
    private String salarySetName="";   //工资类别名称
    private String initType="1";	  // 1:全部  2：时间范围
    private String startDate="";
    private String endDate="";
    private ArrayList salaryItemList=new ArrayList();  //薪资项目列表
    private String salaryid="";
    private String[]  salarySetIDs=null;
    private String isrepeat="";
    private FormFile file;
    private String[]  salarySetSort=null;
    private String queryvalue;
    private String length;//新增薪资类别时根据库中字段长度限制薪资类别长度，赵旭光加，2013-01-16
    /**薪资和保险福利标志,默认为工资业务
     *保险福利为1
     *精细报表为3 
     */
    private String gz_module="0";
	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public PaginationForm getSetlistform() {
		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform) {
		this.setlistform = setlistform;
	}
	 /***增加审核公式*/
    /**审核公式列表*/
    private ArrayList spFormulaList = new ArrayList();
    /**审核公式主键*/
    private String spFormulaId;
    /**审核公式名称*/
    private String spFormulaName;
    /**审核公式提示信息*/
    private String spAlert;
    /**审核公式表达式*/
    private String formula;
    private String itemid;
    private String optType;
    private String filename;
    private String isAdd;//判断是否是新增
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("queryvalue", this.getQueryvalue());
		this.getFormHM().put("optType",this.getOptType());
		this.getFormHM().put("formula", this.getFormula());
		this.getFormHM().put("itemid", this.getItemid());
		this.getFormHM().put("spFormulaId", this.getSpFormulaId());
		this.getFormHM().put("spFormulaName", this.getSpFormulaName());
		this.getFormHM().put("spAlert", this.getSpAlert());
		if(this.getSetlistform()!=null)
			this.getFormHM().put("selectedList",this.getSetlistform().getSelectedList());
		this.getFormHM().put("salarySetName",this.getSalarySetName());
		this.getFormHM().put("initType",this.getInitType());
		this.getFormHM().put("startDate",this.getStartDate());
		this.getFormHM().put("endDate",this.getEndDate());
		this.getFormHM().put("salarySetIDs",this.getSalarySetIDs());
		this.getFormHM().put("file", this.getFile());
		/**工资保险标志*/
		this.getFormHM().put("gz_module",this.getGz_module());
		this.getFormHM().put("isrepeat",this.getIsrepeat());
		this.getFormHM().put("salarySetSort",this.getSalarySetSort());
		this.getFormHM().put("filename",this.getFilename());
		
		this.getFormHM().put("isAdd",this.getIsAdd());
	}

	@Override
    public void outPutFormHM() {
		this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
		this.setQueryvalue((String)this.getFormHM().get("queryvalue"));
		this.setOptType((String)this.getFormHM().get("optType"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setSpAlert((String)this.getFormHM().get("spAlert"));
		this.setSpFormulaId((String)this.getFormHM().get("spFormulaId"));
		this.setSpFormulaName((String)this.getFormHM().get("spFormulaName"));
		this.setSpFormulaList((ArrayList)this.getFormHM().get("spFormulaList"));
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.getSetlistform().getCurrent();
		this.setSetlist2((ArrayList)this.getFormHM().get("setlist2"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setSalaryItemList((ArrayList)this.getFormHM().get("salaryItemList"));
		this.setSalarySetName((String)this.getFormHM().get("salarySetName"));
		this.setIsrepeat((String)this.getFormHM().get("isrepeat"));
		this.setSetlist((ArrayList)this.getFormHM().get("salarySetList"));
		this.setLength((String) this.getFormHM().get("length"));
		this.setFilename((String) this.getFormHM().get("filename"));
		this.setIsAdd((String) this.getFormHM().get("isAdd"));
		if(this.getFormHM().get("isAdd")!=null&& "1".equals(this.getFormHM().get("isAdd"))){
	        if(this.getSetlistform().getPagination()!=null)
	        	this.getSetlistform().getPagination().lastPage(); 
		}

	}

	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/templateset/gz_templatelist".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null&& "link0".equals(arg1.getParameter("b_query"))){
            /**定位到首页,*/
            if(this.getSetlistform().getPagination()!=null)
            	this.getSetlistform().getPagination().firstPage();              
        }	
		if("/gz/templateset/gz_templatelist".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null&& "add".equals(arg1.getParameter("b_query"))){//新增完定位到最后一页 zhaoxg add 2014-1-20
            /**定位到最后一页,*/
            if(this.getSetlistform().getPagination()!=null)
            	this.getSetlistform().getPagination().lastPage();             
        }
		return super.validate(arg0, arg1);
	}
	
	
	public String getSalarySetName() {
		return salarySetName;
	}

	public void setSalarySetName(String salarySetName) {
		this.salarySetName = salarySetName;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getInitType() {
		return initType;
	}

	public void setInitType(String initType) {
		this.initType = initType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public ArrayList getSalaryItemList() {
		return salaryItemList;
	}

	public void setSalaryItemList(ArrayList salaryItemList) {
		this.salaryItemList = salaryItemList;
	}

	public String[] getSalarySetIDs() {
		return salarySetIDs;
	}

	public void setSalarySetIDs(String[] salarySetIDs) {
		this.salarySetIDs = salarySetIDs;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getIsrepeat() {
		return isrepeat;
	}

	public void setIsrepeat(String isrepeat) {
		this.isrepeat = isrepeat;
	}

	public String[] getSalarySetSort() {
		return salarySetSort;
	}

	public void setSalarySetSort(String[] salarySetSort) {
		this.salarySetSort = salarySetSort;
	}

	public ArrayList getSetlist2() {
		return setlist2;
	}

	public void setSetlist2(ArrayList setlist2) {
		this.setlist2 = setlist2;
	}

	public ArrayList getSpFormulaList() {
		return spFormulaList;
	}

	public void setSpFormulaList(ArrayList spFormulaList) {
		this.spFormulaList = spFormulaList;
	}

	public String getSpFormulaId() {
		return spFormulaId;
	}

	public void setSpFormulaId(String spFormulaId) {
		this.spFormulaId = spFormulaId;
	}

	public String getSpFormulaName() {
		return spFormulaName;
	}

	public void setSpFormulaName(String spFormulaName) {
		this.spFormulaName = spFormulaName;
	}

	public String getSpAlert() {
		return spAlert;
	}

	public void setSpAlert(String spAlert) {
		this.spAlert = spAlert;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getQueryvalue() {
		return queryvalue;
	}

	public void setQueryvalue(String queryvalue) {
		this.queryvalue = queryvalue;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getIsAdd() {
		return isAdd;
	}

	public void setIsAdd(String isAdd) {
		this.isAdd = isAdd;
	}
}
