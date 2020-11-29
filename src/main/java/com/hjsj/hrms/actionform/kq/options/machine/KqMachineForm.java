package com.hjsj.hrms.actionform.kq.options.machine;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class KqMachineForm extends FrameForm 
{
	private String sqlstr;
	private String where;
	private String column;
	private String location_id;
	private String e_flag;
	private ArrayList typelist=new ArrayList();
	private PaginationForm recordListForm=new PaginationForm();  
	private String returnvalue="1";
	private RecordVo machine=new RecordVo("kq_machine_location");
	
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
		this.setMachine((RecordVo)this.getFormHM().get("machine"));
		//this.getRecordListForm().setList((ArrayList)this.getFormHM().get("vo_list"));
	    this.setE_flag((String)this.getFormHM().get("e_flag"));
	    this.setLocation_id((String)this.getFormHM().get("location_id"));
	    this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
	   
	}
	
	@Override
    public void inPutTransHM()
    {
		 this.getFormHM().put("machine",this.getMachine());
		 this.getFormHM().put("e_flag",this.getE_flag());
		 this.getFormHM().put("location_id",this.getLocation_id());
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
	public String getLocation_id() {
		return location_id;
	}
	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}
	public RecordVo getMachine() {
		return machine;
	}
	public void setMachine(RecordVo machine) {
		this.machine = machine;
	}
	public String getE_flag() {
		return e_flag;
	}
	public void setE_flag(String e_flag) {
		this.e_flag = e_flag;
	}
	public ArrayList getTypelist() {
		return typelist;
	}
	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

}
