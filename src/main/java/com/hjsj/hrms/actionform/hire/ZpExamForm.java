/*
 * Created on 2005-9-21
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
 * <p>Title:ZpExamForm</p>
 * <p>Description:考试科目表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 25, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class ZpExamForm extends FrameForm {

	/**
	 * 新建及修改功能控制
	 */
    private String flag="0";
    /**当前页*/
    private int current=1;
    /**
     * 考试科目对象
     */
    private RecordVo zpExamvo=new RecordVo("ZP_EXAM_SUBJECT");
    /**
     * 考试科目对象列表
     */
    private PaginationForm zpExamForm=new PaginationForm(); 

	@Override
    public void outPutFormHM() {
		
    	this.setFlag((String)this.getFormHM().get("flag"));
        this.setZpExamvo((RecordVo)this.getFormHM().get("zpExamvo"));
        this.getZpExamForm().setList((ArrayList)this.getFormHM().get("zpExamlist"));
        /**重新定位到当前页*/
	    this.getZpExamForm().getPagination().gotoPage(current);
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		
        this.getFormHM().put("selectedlist",(ArrayList)this.getZpExamForm().getSelectedList());
        this.getFormHM().put("zpExamvo",this.getZpExamvo());
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
	         * 新建考试科目
	         */
	        if("/hire/zp_exam/search_exam_subject".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	            this.setFlag("1");
	            this.getZpExamvo().clearValues();
	        }
	        if("/hire/zp_exam/add_exam_subject".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
	        {
	            if(zpExamForm.getPagination()!=null)
	            {
	            	if("1".equals(this.getFlag()))
	            		zpExamForm.getPagination().lastPage();
	                current=zpExamForm.getPagination().getCurrent(); 
	            }
	        }
	        /**
	         * 编辑考试科目
	         */
	        if("/hire/zp_exam/add_exam_subject".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	            this.setFlag("0");
	            if(zpExamForm.getPagination()!=null)
	            {            
	            	current=zpExamForm.getPagination().getCurrent();    
	            }
	        }
	        if("/hire/zp_exam/search_exam_subject".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
	        {
	            if(zpExamForm.getPagination()!=null)
	            {
	                current=zpExamForm.getPagination().getCurrent();
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
	/**
	 * @return Returns the zpExamvo.
	 */
	public RecordVo getZpExamvo() {
		return zpExamvo;
	}
	/**
	 * @param zpExamvo The zpExamvo to set.
	 */
	public void setZpExamvo(RecordVo zpExamvo) {
		this.zpExamvo = zpExamvo;
	}
	/**
	 * @return Returns the zpExamForm.
	 */
	public PaginationForm getZpExamForm() {
		return zpExamForm;
	}
	/**
	 * @param zpExamForm The zpExamForm to set.
	 */
	public void setZpExamForm(PaginationForm zpExamForm) {
		this.zpExamForm = zpExamForm;
	}
}
