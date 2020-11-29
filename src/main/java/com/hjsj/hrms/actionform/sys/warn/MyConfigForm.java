package com.hjsj.hrms.actionform.sys.warn;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class MyConfigForm extends FrameForm {
	
	private int current=1;
    private PaginationForm pageListForm = new PaginationForm();// 地址分页管理器
    
  
    
	public MyConfigForm() {
		super();
	}

	@Override
    public void outPutFormHM() {
		this.getPageListForm().setList((ArrayList)this.getFormHM().get("warnList"));
		this.getPageListForm().getPagination().gotoPage(current);	
		
	}

	@Override
    public void inPutTransHM() {

	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	
	
}
