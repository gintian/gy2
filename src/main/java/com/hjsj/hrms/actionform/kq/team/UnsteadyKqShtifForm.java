package com.hjsj.hrms.actionform.kq.team;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class UnsteadyKqShtifForm extends FrameForm 
{
	private String treeCode;//树形菜单，在HtmlMenu中
	private String code;//连接级别
	private String kind;
	private PaginationForm recordListForm=new PaginationForm();    
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	@Override
    public void outPutFormHM()
	{
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setCode((String)this.getFormHM().get("code"));
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("vo_list"));
	}
	
	@Override
    public void inPutTransHM()
    {
		 this.getFormHM().put("code",code);
		 this.getFormHM().put("kind",kind);		
		 this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
    }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/kq/team/array/unsteady_shift".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }	   
	    //kq/team/array_set/search_array.do?b_query
	    if("/kq/team/array/unsteady_shift_data".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }	
	    if("/kq/team/array/unsteady_shift".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.setCode("");
	        this.setKind("");
	    }	
	    return super.validate(arg0, arg1);
	}
}
