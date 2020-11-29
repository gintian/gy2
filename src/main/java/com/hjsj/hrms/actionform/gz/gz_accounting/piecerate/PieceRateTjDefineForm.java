package com.hjsj.hrms.actionform.gz.gz_accounting.piecerate;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;
import java.util.HashMap;

public class PieceRateTjDefineForm extends FrameForm {  
	
	private String model="add";
	private String reportId="-1";
	private String reportName="";
	private String reportKind="";
	private String reportSortId="1";
	private String needClose="false";
	/**已选的显示指标列表*/
    private ArrayList selectedFieldList = new ArrayList();
    private ArrayList leftFieldList = new ArrayList();
    private String[] right_fields = new String[0];
    private String[] left_fields=new String[0];
    private String rightFields;    
	ArrayList setList = new ArrayList();//子集列表
	private String setId = "";
    
    //分组设置
    private String useGroup="0";//启用分组
    private String tjWhere="";//统计条件
	private String groupFlds="";
	private HashMap summaryMapFlds= new HashMap();
	private String summaryFlds="";
	
	private ArrayList cond_setList = new ArrayList();
	private String cond_setId = "";
	private ArrayList cond_itemList = new ArrayList();
	private String cond_itemId = "";
	
	
	//分组指标
	private PaginationForm pageGroupFld = new PaginationForm();	

	//汇总方式
	private PaginationForm pageSummaryFld = new PaginationForm();
	
	//排序方式
	private PaginationForm pageOrderFld = new PaginationForm();
	private HashMap orderMapFlds= new HashMap();
	private String orderFlds="";

	//完成页面
	ArrayList tasktypelist = new ArrayList();



	@Override
    public void inPutTransHM() {
		this.getFormHM().put("rightFields",this.getRightFields());
		this.getFormHM().put("itemid",this.getLeft_fields());
		this.getFormHM().put("selectedFieldList",this.getSelectedFieldList());
		
		
		
		this.getFormHM().put("model",this.getModel());
		this.getFormHM().put("reportId",this.getReportId());		
		this.getFormHM().put("reportKind",this.getReportKind());
		this.getFormHM().put("reportName",this.getReportName());
		this.getFormHM().put("needClose",this.getNeedClose());	
		this.getFormHM().put("useGroup",this.getUseGroup());
		this.getFormHM().put("tjWhere",this.getTjWhere());
		this.getFormHM().put("groupFlds",this.getGroupFlds());
		this.getFormHM().put("summaryFlds",this.getSummaryFlds());
		this.getFormHM().put("summaryMapFlds",this.getSummaryMapFlds());
		this.getFormHM().put("orderFlds",this.getOrderFlds());
		this.getFormHM().put("orderMapFlds",this.getOrderMapFlds());
		
		this.getFormHM().put("cond_setList",this.getCond_setList());
		this.getFormHM().put("cond_itemList",this.getCond_itemList());


	}

	@Override
    public void outPutFormHM() {
		this.setModel((String) this.getFormHM().get("model"));
		this.setReportId((String) this.getFormHM().get("reportId"));
		this.setReportKind((String) this.getFormHM().get("reportKind"));
		this.setReportName((String) this.getFormHM().get("reportName"));
		this.setReportSortId((String) this.getFormHM().get("reportSortId"));
		this.setUseGroup((String) this.getFormHM().get("useGroup"));
		this.setTjWhere((String) this.getFormHM().get("tjWhere"));
		this.setGroupFlds((String) this.getFormHM().get("groupFlds"));
		this.setSummaryFlds((String) this.getFormHM().get("summaryFlds"));
		this.setSummaryMapFlds((HashMap) this.getFormHM().get("summaryMapFlds"));
		this.setOrderFlds((String) this.getFormHM().get("orderFlds"));
		this.setOrderMapFlds((HashMap) this.getFormHM().get("orderMapFlds"));
		this.setNeedClose((String) this.getFormHM().get("needClose"));
		
		this.setCond_setList((ArrayList) this.getFormHM().get("cond_setList"));
		this.setCond_setId((String) this.getFormHM().get("cond_setId"));
		this.setCond_itemList((ArrayList) this.getFormHM().get("cond_itemList"));
		this.setCond_itemId((String) this.getFormHM().get("cond_itemId"));		
		
		this.setSelectedFieldList((ArrayList)this.getFormHM().get("selectedFieldList"));
	    this.setLeftFieldList((ArrayList)this.getFormHM().get("leftFieldList"));
	    this.setRightFields((String)this.getFormHM().get("rightFields"));	    
		this.setSetList((ArrayList) this.getFormHM().get("setList"));
		this.setSetId((String) this.getFormHM().get("setId"));
		this.getPageGroupFld().setList((ArrayList) this.getFormHM().get("groupFldList"));		
		this.getPageSummaryFld().setList((ArrayList) this.getFormHM().get("summaryFldList"));		
		this.getPageOrderFld().setList((ArrayList) this.getFormHM().get("orderFldList"));		
		this.setTasktypelist((ArrayList) this.getFormHM().get("taskTypeList"));
	}

	public String getUseGroup() {
		return useGroup;
	}

	public void setUseGroup(String useGroup) {
		this.useGroup = useGroup;
	}

	public String getTjWhere() {
		return tjWhere;
	}

	public void setTjWhere(String tjWhere) {
		this.tjWhere = tjWhere;
	}

	public PaginationForm getPageGroupFld() {
		return pageGroupFld;
	}

	public void setPageGroupFld(PaginationForm pageGroupFld) {
		this.pageGroupFld = pageGroupFld;
	}

	public PaginationForm getPageSummaryFld() {
		return pageSummaryFld;
	}

	public void setPageSummaryFld(PaginationForm pageSummaryFld) {
		this.pageSummaryFld = pageSummaryFld;
	}

	public PaginationForm getPageOrderFld() {
		return pageOrderFld;
	}

	public void setPageOrderFld(PaginationForm pageOrderFld) {
		this.pageOrderFld = pageOrderFld;
	}


	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportKind() {
		return reportKind;
	}

	public void setReportKind(String reportKind) {
		this.reportKind = reportKind;
	}

	public HashMap getOrderMapFlds() {
		return orderMapFlds;
	}

	public void setOrderMapFlds(HashMap orderMapFlds) {
		this.orderMapFlds = orderMapFlds;
	}

	public String getGroupFlds() {
		return groupFlds;
	}

	public void setGroupFlds(String groupFlds) {
		this.groupFlds = groupFlds;
	}


	public HashMap getSummaryMapFlds() {
		return summaryMapFlds;
	}

	public void setSummaryMapFlds(HashMap summaryMapFlds) {
		this.summaryMapFlds = summaryMapFlds;
	}

	public String getSummaryFlds() {
		return summaryFlds;
	}

	public void setSummaryFlds(String summaryFlds) {
		this.summaryFlds = summaryFlds;
	}

	public String getOrderFlds() {
		return orderFlds;
	}

	public void setOrderFlds(String orderFlds) {
		this.orderFlds = orderFlds;
	}


	public String getNeedClose() {
		return needClose;
	}

	public void setNeedClose(String needClose) {
		this.needClose = needClose;
	}

	public ArrayList getCond_setList() {
		return cond_setList;
	}

	public void setCond_setList(ArrayList cond_setList) {
		this.cond_setList = cond_setList;
	}

	public ArrayList getCond_itemList() {
		return cond_itemList;
	}

	public void setCond_itemList(ArrayList cond_itemList) {
		this.cond_itemList = cond_itemList;
	}

	public String getCond_setId() {
		return cond_setId;
	}

	public void setCond_setId(String cond_setId) {
		this.cond_setId = cond_setId;
	}

	public String getCond_itemId() {
		return cond_itemId;
	}

	public void setCond_itemId(String cond_itemId) {
		this.cond_itemId = cond_itemId;
	}
	public ArrayList getTasktypelist() {
		return tasktypelist;
	}

	public void setTasktypelist(ArrayList tasktypelist) {
		this.tasktypelist = tasktypelist;
	}

	public ArrayList getSetList() {
		return setList;
	}

	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}

	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public ArrayList getSelectedFieldList() {
		return selectedFieldList;
	}

	public void setSelectedFieldList(ArrayList selectedFieldList) {
		this.selectedFieldList = selectedFieldList;
	}

	public ArrayList getLeftFieldList() {
		return leftFieldList;
	}

	public void setLeftFieldList(ArrayList leftFieldList) {
		this.leftFieldList = leftFieldList;
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

	public String getRightFields() {
		return rightFields;
	}

	public void setRightFields(String rightFields) {
		this.rightFields = rightFields;
	}

	public String getReportSortId() {
		return reportSortId;
	}

	public void setReportSortId(String reportSortId) {
		this.reportSortId = reportSortId;
	}
}
