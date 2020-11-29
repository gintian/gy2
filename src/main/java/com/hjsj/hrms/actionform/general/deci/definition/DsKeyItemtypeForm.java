package com.hjsj.hrms.actionform.general.deci.definition;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class DsKeyItemtypeForm extends FrameForm {
	
	String sql_select="select typeid,name,status";
	String sql_whl="from ds_key_itemtype ";
	
	String name="";   //类别名称
	String status=""; //是否有效
	String typeid=""; //类别ID
	
	
	@Override
    public void outPutFormHM() {
		this.setTypeid((String)this.getFormHM().get("typeid"));
		this.setName((String)this.getFormHM().get("name"));
		this.setStatus((String)this.getFormHM().get("status"));
	}

	@Override
    public void inPutTransHM() {
		if(this.getPagination()!=null){
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		}
		this.getFormHM().put("name",this.getName());			
		this.getFormHM().put("status",this.getStatus());		
		this.getFormHM().put("typeid",this.getTypeid());
		
	}

	public String getSql_select() {
		return sql_select;
	}

	public void setSql_select(String sql_select) {
		this.sql_select = sql_select;
	}

	public String getSql_whl() {
		return sql_whl;
	}

	public void setSql_whl(String sql_whl) {
		this.sql_whl = sql_whl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {

		super.reset(arg0, arg1);
		//清空name，typeid，status   jingq  add   2014.07.15
		this.setName("");
		this.setTypeid("");
		this.setStatus("");
	}
	
}
