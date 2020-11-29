package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ImportPersonnelForm extends FrameForm {
	private String    salaryid="";
	private ArrayList fieldSetList=new ArrayList();
	private String    fieldSetId="";

	private ArrayList fieldItemList=new ArrayList();
	private String    fieldItemId="";
	private String    p_value="";
	private String    p_viewvalue="";
	private String    n_value="";
	private String[]    right_fields;
	private String    fieldItems="";
	private PaginationForm tableDataListForm=new PaginationForm();
	private ArrayList tableHeadList=new ArrayList();
	//private ArrayList tableDataList=new ArrayList();
	private String expr;
	private String factor;
	private String allRightField;
	private String isSalaryManager;
	private String mangerCodeValue;
	/**是否查历史记录*/
	private String isHistory;
	/**普通查询还是高级查询*/
	private String querytype;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("querytype", this.getQuerytype());
		this.getFormHM().put("isHistory", this.getIsHistory());
		this.getFormHM().put("mangerCodeValue", this.getMangerCodeValue());
		this.getFormHM().put("isSalaryManager", isSalaryManager);
		this.getFormHM().put("allRightField", this.getAllRightField());
		this.getFormHM().put("expr",this.getExpr());
		this.getFormHM().put("factor",this.getFactor());
		this.getFormHM().put("selectedlist",(ArrayList)this.getTableDataListForm().getSelectedList());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("fieldSetId", this.getFieldSetId());
		this.getFormHM().put("fieldItemList",this.getFieldItemList());
		this.getFormHM().put("p_value",this.getP_value());
		this.getFormHM().put("n_value",this.getN_value());
		this.getFormHM().put("fieldItemId",this.getFieldItemId());
		this.getFormHM().put("p_viewvalue",this.getP_viewvalue());
		this.getFormHM().put("fieldItems",this.getFieldItems());
	}

	@Override
    public void outPutFormHM() {
		this.setQuerytype((String)this.getFormHM().get("querytype"));
		this.setIsHistory((String)this.getFormHM().get("isHistory"));
		this.setMangerCodeValue((String)this.getFormHM().get("mangerCodeValue"));
		this.setIsSalaryManager((String)this.getFormHM().get("isSalaryManager"));
		this.setAllRightField((String)this.getFormHM().get("allRightField"));
		this.getTableDataListForm().setList((ArrayList)this.getFormHM().get("tableDataList"));
		//this..getPagination().gotoPage(current);
		this.setFieldItems((String)this.getFormHM().get("fieldItems"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
		this.setFieldSetId((String)this.getFormHM().get("fieldSetId"));
		this.setFieldItemList((ArrayList)this.getFormHM().get("fieldItemList"));
		this.setFieldItemId((String)this.getFormHM().get("fieldItemId"));
		this.setTableHeadList((ArrayList)this.getFormHM().get("tableHeadList"));
		//this.setTableDataList((ArrayList)this.getFormHM().get("tableDataList"));
		this.setP_value((String)this.getFormHM().get("p_value"));
		this.setN_value((String)this.getFormHM().get("n_value"));
		this.setP_viewvalue((String)this.getFormHM().get("p_viewvalue"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   try
	   {
		   if("/gz/gz_accounting/importMen".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	       {
	            if(this.getTableDataListForm()!=null)
	              this.getTableDataListForm().getPagination().firstPage();
	       }	
	   }catch(Exception e)
	   {
	   	  e.printStackTrace();
	   }
       return super.validate(arg0, arg1);
	}

	public String getFieldItemId() {
		return fieldItemId;
	}

	public void setFieldItemId(String fieldItemId) {
		this.fieldItemId = fieldItemId;
	}

	public ArrayList getFieldItemList() {
		return fieldItemList;
	}

	public void setFieldItemList(ArrayList fieldItemList) {
		this.fieldItemList = fieldItemList;
	}


	public String getFieldSetId() {
		return fieldSetId;
	}

	public void setFieldSetId(String fieldSetId) {
		this.fieldSetId = fieldSetId;
	}

	public ArrayList getFieldSetList() {
		return fieldSetList;
	}

	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}

	public String getN_value() {
		return n_value;
	}

	public void setN_value(String n_value) {
		this.n_value = n_value;
	}

	public String getP_value() {
		return p_value;
	}

	public void setP_value(String p_value) {
		this.p_value = p_value;
	}



	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	/*public ArrayList getTableDataList() {
		return tableDataList;
	}

	public void setTableDataList(ArrayList tableDataList) {
		this.tableDataList = tableDataList;
	}
*/
	public ArrayList getTableHeadList() {
		return tableHeadList;
	}

	public void setTableHeadList(ArrayList tableHeadList) {
		this.tableHeadList = tableHeadList;
	}

	public String getP_viewvalue() {
		return p_viewvalue;
	}

	public void setP_viewvalue(String p_viewvalue) {
		this.p_viewvalue = p_viewvalue;
	}

	

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getFieldItems() {
		return fieldItems;
	}

	public void setFieldItems(String fieldItems) {
		this.fieldItems = fieldItems;
	}

	public PaginationForm getTableDataListForm() {
		return tableDataListForm;
	}

	public void setTableDataListForm(PaginationForm tableDataListForm) {
		this.tableDataListForm = tableDataListForm;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getFactor() {
		return factor;
	}

	public void setFactor(String factor) {
		this.factor = factor;
	}

	public String getAllRightField() {
		return allRightField;
	}

	public void setAllRightField(String allRightField) {
		this.allRightField = allRightField;
	}

	public String getIsSalaryManager() {
		return isSalaryManager;
	}

	public void setIsSalaryManager(String isSalaryManager) {
		this.isSalaryManager = isSalaryManager;
	}

	public String getMangerCodeValue() {
		return mangerCodeValue;
	}

	public void setMangerCodeValue(String mangerCodeValue) {
		this.mangerCodeValue = mangerCodeValue;
	}

	public String getIsHistory() {
		return isHistory;
	}

	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
	}

	public String getQuerytype() {
		return querytype;
	}

	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}

}
