package com.hjsj.hrms.actionform.train.resource.course;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.List;

public class CourseStudentForm extends FrameForm {
	private String searchstr;
	private List sortlist=new ArrayList();
	private String id;
	private String a_code;

	
	private String strsql;
	private String columns;
	private String strwhere;
	 private String primaryField;
	 
	 
	// 培训公共代码树的代码类 
	private String trainsetid;
	
	private String codeSetId;
	public String getCodeSetId() {
		return codeSetId;
	}

	public void setCodeSetId(String codeSetId) {
		this.codeSetId = codeSetId;
	}

	 
	private String order_by; 
	
	

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("searchstr", this.getSearchstr());
		this.setSearchstr(null);
		this.getFormHM().put("id", this.getId());
		this.setId(null);
		this.getFormHM().put("strsql", this.getStrsql());
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("strwhere", this.getStrwhere());
		this.getFormHM().put("primaryField", this.getPrimaryField());
		this.getFormHM().put("isP", "");
		this.getFormHM().put("a_code", this.getA_code());
		
		if(this.getPagination()!=null)
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());

	}

	@Override
    public void outPutFormHM() {
		
		this.setSortlist((List)this.getFormHM().get("sortlist"));
		this.setId((String)this.getFormHM().get("id"));
		
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));

		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setPrimaryField((String)this.getFormHM().get("primaryField"));

		this.setTrainsetid((String) this.getFormHM().get("trainsetid"));
		this.setCodeSetId((String) this.getFormHM().get("codesetid"));
		

		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		if(this.getFormHM().containsKey("initPage")){
			initPage();
			this.getFormHM().remove("initPage");
		}

	}
	



	public String getSearchstr() {
		return searchstr;
	}

	public void setSearchstr(String searchstr) {
		this.searchstr = searchstr;
	}

	public List getSortlist() {
		return sortlist;
	}

	public void setSortlist(List sortlist) {
		this.sortlist = sortlist;
	}

	public String getId() {
		String idtemp = this.id;
		this.id = null;
		return idtemp;
	}

	public void setId(String id) {
		this.id = id;
	}



	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getPrimaryField() {
		return primaryField;
	}

	public void setPrimaryField(String primaryField) {
		this.primaryField = primaryField;
	}


	public String getTrainsetid() {
		return trainsetid;
	}

	public void setTrainsetid(String trainsetid) {
		this.trainsetid = trainsetid;
	}


	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getA_code() {
		return a_code;
	}

    /**
	 * 解决多个页面公用一个form翻页缓存问题，重置分页
	 */
	public void initPage(){
		if(this.getPagination()!=null){
		    this.getPagination().gotoPage(1);
		    this.setPagerows(0);
		}
	}
}
