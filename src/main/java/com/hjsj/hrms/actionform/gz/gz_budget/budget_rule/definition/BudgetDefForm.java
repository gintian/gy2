package com.hjsj.hrms.actionform.gz.gz_budget.budget_rule.definition;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class BudgetDefForm extends FrameForm{
	
	/**地址分页管理器*/
    private PaginationForm budgetlistform=new PaginationForm();
    String isAdd = "0";//用来和current共同控制着显示第几页
    /**当前页*/
    private int current=1;
    LazyDynaBean budgetBean = new LazyDynaBean();
    ArrayList kindList = new ArrayList();
    ArrayList codesetList = new ArrayList();
    String count = "0";//保存总的记录数。用来控制排序上下箭头用的
    //用来控制checkbox
    String analyseFlag = "";//是否执行分析
	String bpFlag = "";//报批标志
	String validFlag = "";//是否生效
	String tabType = ""; // 预算表分类

	@Override
    public void inPutTransHM() {
        this.getFormHM().put("budgetBean",this.getBudgetBean());
        this.getFormHM().put("isAdd", this.getIsAdd());
        this.getFormHM().put("kindList", this.getKindList());
        this.getFormHM().put("codesetList", this.getCodesetList());        
        this.getFormHM().put("count", this.getCount());
        this.getFormHM().put("analyseFlag", this.getAnalyseFlag());
        this.getFormHM().put("bpFlag", this.getBpFlag());
        this.getFormHM().put("validFlag", this.getValidFlag());
        this.getFormHM().put("tabType", this.getTabType());

	}

	@Override
    public void outPutFormHM() {
		this.getBudgetlistform().setList((ArrayList)this.getFormHM().get("budgetList"));
		this.setBudgetBean((LazyDynaBean)this.getFormHM().get("budgetBean"));
		this.setIsAdd((String)this.getFormHM().get("isAdd"));
		this.setKindList((ArrayList)this.getFormHM().get("kindList"));
		this.setCodesetList((ArrayList)this.getFormHM().get("codesetList"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setAnalyseFlag((String)this.getFormHM().get("analyseFlag"));
		this.setBpFlag((String)this.getFormHM().get("bpFlag"));
		this.setValidFlag((String)this.getFormHM().get("validFlag"));
		this.setTabType((String)this.getFormHM().get("tabType"));
		

	    /**重新定位到当前页*/
	    this.getBudgetlistform().getPagination().gotoPage(current);
	}
	 @Override
     public void reset(ActionMapping arg0, HttpServletRequest arg1){
	    	super.reset(arg0, arg1);
	    	this.setAnalyseFlag("0");
	    	this.setBpFlag("0");
	    	this.setValidFlag("0");
	    }
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/gz_budget/budget_rule/definition".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
			if ("link".equals(arg1.getParameter("b_query"))){
				if (this.budgetlistform.getPagination() != null){
					this.budgetlistform.getPagination().firstPage();
				}
			}
        }
		if ((arg1.getParameter("isadd")!=null) && ("1".equals(arg1.getParameter("isadd")))){
				if (this.budgetlistform.getPagination() != null){
			    	   budgetlistform.getPagination().lastPage();
			    	   current = budgetlistform.getPagination().getCurrent();
				}
        }
	else{		
			current=budgetlistform.getPagination().getCurrent();			
	  }

        return super.validate(arg0, arg1);
       
    }

	public PaginationForm getBudgetlistform() {
		return budgetlistform;
	}

	public void setBudgetlistform(PaginationForm budgetlistform) {
		this.budgetlistform = budgetlistform;
	}

	public LazyDynaBean getBudgetBean() {
		return budgetBean;
	}

	public void setBudgetBean(LazyDynaBean budgetBean) {
		this.budgetBean = budgetBean;
	}

	public String getIsAdd() {
		return isAdd;
	}

	public void setIsAdd(String isAdd) {
		this.isAdd = isAdd;
	}

	public ArrayList getKindList() {
		return kindList;
	}

	public void setKindList(ArrayList kindList) {
		this.kindList = kindList;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getAnalyseFlag() {
		return analyseFlag;
	}

	public void setAnalyseFlag(String analyseFlag) {
		this.analyseFlag = analyseFlag;
	}

	public String getBpFlag() {
		return bpFlag;
	}

	public void setBpFlag(String bpFlag) {
		this.bpFlag = bpFlag;
	}

	public String getTabType() {
		return tabType;
	}

	public void setTabType(String tabType) {
		this.tabType = tabType;
	}

	public String getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}

	public ArrayList getCodesetList() {
		return codesetList;
	}

	public void setCodesetList(ArrayList codesetList) {
		this.codesetList = codesetList;
	}


}
