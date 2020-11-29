/**
 * 
 */
package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

/**
 *<p>Title:BatchForm</p> 
 *<p>Description:批量处理表单，包括批量计算，批量引入，批量修改</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-28:下午04:30:57</p> 
 *@author cmq
 *@version 4.0
 */
public class BatchForm extends FrameForm {
	/**薪资类别编号*/
	private String salaryid="-1";
	/**计算公式列表*/
	private ArrayList formulalist=new ArrayList();
	/**分页管理器*/
    private PaginationForm formulalistform=new PaginationForm();
    /**数据引入方式
     *=1 同月上次
     *=2 上月同次
     */
    private String importtype="1";
    /**替换目标项目*/
    private String itemid;
    /**参考项目*/
    private String ref_itemid;
    /**替换公式内容*/
    private String formula;
    
    /**目标项目列表*/
    private ArrayList itemlist=new ArrayList();
    /**参考项目列表*/
    private ArrayList ref_itemlist=new ArrayList();
    /**数据更新方式列表*/
    private ArrayList typelist=new ArrayList();   
    /** 更新-高级指标列表  */
    private ArrayList gzItemList=new ArrayList();
    /** 数据提交是否有更新当前纪录方式 */
    private String    isUpdateSet="none";
    
    /**薪资和保险福利标志,默认为工资业务
     *保险福利为1 
     */
    private String gz_module="0";

    private String isHistory="0";
    
    private String bosdate="";
    private String count="";
    
	public BatchForm() {
		super();
  /*      CommonData vo=new CommonData("2",ResourceFactory.getProperty("label.gz.notchange"));
        typelist.add(vo);
        vo=new CommonData("0",ResourceFactory.getProperty("label.gz.update"));
        typelist.add(vo);
        vo=new CommonData("1",ResourceFactory.getProperty("label.gz.append"));
        typelist.add(vo);*/
	}

	public ArrayList getTypelist() {
		return typelist;
	}

	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}

	public ArrayList getFormulalist() {
		return formulalist;
	}

	public void setFormulalist(ArrayList formulalist) {
		this.formulalist = formulalist;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("gzItemList", this.getGzItemList());
		this.getFormHM().put("salaryid", getSalaryid());
		this.getFormHM().put("importtype", getImporttype());
	}

	@Override
    public void outPutFormHM() {
		/**计算公式列表*/
		//this.setFormulalist((ArrayList)this.getFormHM().get("formulalist"));
		this.getFormulalistform().setList((ArrayList)this.getFormHM().get("formulalist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setRef_itemlist((ArrayList)this.getFormHM().get("ref_itemlist"));
		this.setTypelist((ArrayList)this.getFormHM().get("typelist"));	
		this.setGzItemList((ArrayList)this.getFormHM().get("gzItemList"));
		this.setIsUpdateSet((String)this.getFormHM().get("isUpdateSet"));
		
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.setIsHistory((String)this.getFormHM().get("isHistory"));
		
		this.setBosdate((String)this.getFormHM().get("bosdate"));
		this.setCount((String)this.getFormHM().get("count"));
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public PaginationForm getFormulalistform() {
		return formulalistform;
	}

	public void setFormulalistform(PaginationForm formulalistform) {
		this.formulalistform = formulalistform;
	}

	public String getImporttype() {
		return importtype;
	}

	public void setImporttype(String importtype) {
		this.importtype = importtype;
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

	public String getRef_itemid() {
		return ref_itemid;
	}

	public void setRef_itemid(String ref_itemid) {
		this.ref_itemid = ref_itemid;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public ArrayList getRef_itemlist() {
		return ref_itemlist;
	}

	public void setRef_itemlist(ArrayList ref_itemlist) {
		this.ref_itemlist = ref_itemlist;
	}

	public ArrayList getGzItemList() {
		return gzItemList;
	}

	public void setGzItemList(ArrayList gzItemList) {
		this.gzItemList = gzItemList;
	}

	public String getIsUpdateSet() {
		return isUpdateSet;
	}

	public void setIsUpdateSet(String isUpdateSet) {
		this.isUpdateSet = isUpdateSet;
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getIsHistory() {
		return isHistory;
	}

	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
	}

	public String getBosdate() {
		return bosdate;
	}

	public void setBosdate(String bosdate) {
		this.bosdate = bosdate;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

}
