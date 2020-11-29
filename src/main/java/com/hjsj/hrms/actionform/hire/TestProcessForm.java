/*
 * Created on 2005-8-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:TestProcessForm</p>
 * <p>Description:面试环节定义表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 11, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class TestProcessForm extends FrameForm {

	/**
	 * 新建及修改功能控制
	 */
    private String flag="0";  
    /**
     * 当前页
     */
    private int current=1;
    /**
     * 发布招聘岗位对象
     */
    private RecordVo testProcessvo=new RecordVo("ZP_TACHE");
    /**
     * 发布招聘岗位对象列表
     */
    private PaginationForm testProcessForm=new PaginationForm(); 

	@Override
    public void outPutFormHM() {
		
    	this.setFlag((String)this.getFormHM().get("flag"));
        this.setTestProcessvo((RecordVo)this.getFormHM().get("testProcessvo"));
        this.getTestProcessForm().setList((ArrayList)this.getFormHM().get("testProcesslist"));
        /**重新定位到当前页*/
	    this.getTestProcessForm().getPagination().gotoPage(current);
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		
        this.getFormHM().put("selectedlist",(ArrayList)this.getTestProcessForm().getSelectedList());
        this.getFormHM().put("testProcessvo",this.getTestProcessvo());
	    this.getFormHM().put("flag",this.getFlag());

	}
	
	 @Override
     public void reset(ActionMapping arg0, HttpServletRequest arg1)
	 {
	    	
	      super.reset(arg0, arg1);
	        
	  }
	 
	 /* 
	     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	     */
	    @Override
        public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	        /**
	         * 新建面试环节
	         */
	        if("/hire/zp_options/test_process".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	            this.setFlag("1");
	            this.getTestProcessvo().clearValues();
	        }
	        /**
	         * 编辑面试环节
	         */
	        if("/hire/zp_options/add_change".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	            this.setFlag("0");
	            if(testProcessForm.getPagination()!=null)
	            {            
	            	current=testProcessForm.getPagination().getCurrent();    
	            }
	        }
	        if("/hire/zp_options/add_change".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
	        {
	            if(testProcessForm.getPagination()!=null)
	            {
	            	if("1".equals(this.getFlag()))
	            		testProcessForm.getPagination().lastPage();
	            	current=testProcessForm.getPagination().getCurrent();
	            }
	        }
	        if("/hire/zp_options/test_process".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
	        {
	            if(testProcessForm.getPagination()!=null)
	            {
	            	current=testProcessForm.getPagination().getCurrent();
	            }
	        }
	        return super.validate(arg0, arg1);
	    }

	/**
	 * @return Returns the flag.
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag The flag to set.
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @return Returns the testProcessForm.
	 */
	public PaginationForm getTestProcessForm() {
		return testProcessForm;
	}
	/**
	 * @param testProcessForm The testProcessForm to set.
	 */
	public void setTestProcessForm(PaginationForm testProcessForm) {
		this.testProcessForm = testProcessForm;
	}
	/**
	 * @return Returns the testProcessvo.
	 */
	public RecordVo getTestProcessvo() {
		return testProcessvo;
	}
	/**
	 * @param testProcessvo The testProcessvo to set.
	 */
	public void setTestProcessvo(RecordVo testProcessvo) {
		this.testProcessvo = testProcessvo;
	}
	/**
	 * @return Returns the current.
	 */
	public int getCurrent() {
		return current;
	}
	/**
	 * @param current The current to set.
	 */
	public void setCurrent(int current) {
		this.current = current;
	}
}
