package com.hjsj.hrms.actionform.train.trainexam.question.type;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class QuestionTypeForm extends FrameForm 
{
	private String sqlstr;
	private String where;
	private String column;
	private String type_id;
	private String e_flag;
	private int maxOrder;
	private PaginationForm recordListForm=new PaginationForm();  
	private String returnvalue="1";
	private RecordVo quesType=new RecordVo("tr_question_type");
	
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	@Override
    public void outPutFormHM()
	{
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setQuesType((RecordVo)this.getFormHM().get("quesType"));
	    this.setE_flag((String)this.getFormHM().get("e_flag"));
	    this.setType_id((String)this.getFormHM().get("type_id"));
	    this.setMaxOrder(Integer.parseInt(this.getFormHM().get("maxOrder").toString()));
	   
	}
	
	@Override
    public void inPutTransHM()
    {
		 this.getFormHM().put("quesType",this.getQuesType());
		 this.getFormHM().put("e_flag",this.getE_flag());
		 this.getFormHM().put("type_id",this.getType_id());		
		 if(this.getPagination()!=null)			
			 this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		
		
    }
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getType_id() {
		return type_id;
	}
	public void setType_id(String type_id) {
		this.type_id = type_id;
	}
	public RecordVo getQuesType() {
		return quesType;
	}
	public void setQuesType(RecordVo quesType) {
		this.quesType = quesType;
	}
	public String getE_flag() {
		return e_flag;
	}
	public void setE_flag(String e_flag) {
		this.e_flag = e_flag;
	}
	public int getMaxOrder(){
		return maxOrder;
	}
	public void setMaxOrder(int order){
		this.maxOrder = order;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

}
