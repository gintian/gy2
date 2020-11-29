/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.updownfile;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HtmlFileListForm extends FrameForm {

	private String flag="0";
    
  
    private RecordVo filelistvo=new RecordVo("resource_list");
    /**
     * 建议对象列表
     */
    private PaginationForm filelistForm=new PaginationForm();     
    
   
   
    @Override
    public void outPutFormHM() {
        this.setFilelistvo((RecordVo)this.getFormHM().get("filelistvo"));
        this.getFilelistForm().setList((ArrayList)this.getFormHM().get("filelistlist"));
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
	    this.getFormHM().put("selectedlist",(ArrayList)this.getFilelistForm().getSelectedList());
        this.getFormHM().put("filelistvo",this.getFilelistvo());
	    this.getFormHM().put("flag",this.getFlag());        
    }

    /**
     * @return Returns the filelistForm.
     */
    public PaginationForm getFilelistForm() {
        return filelistForm;
    }
    /**
     * @param filelistForm The filelistForm to set.
     */
    public void setFilelistForm(PaginationForm filelistForm) {
        this.filelistForm = filelistForm;
    }
    /**
     * @return Returns the filelistvo.
     */
    public RecordVo getFilelistvo() {
        return filelistvo;
    }
    /**
     * @param filelistvo The filelistvo to set.
     */
    public void setFilelistvo(RecordVo filelistvo) {
        this.filelistvo = filelistvo;
    }
    /* 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);
    }
    /* 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        /**
         * 新建
         */
        if("/selfservice/propose/searchfilelist".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag("1");
            this.getFilelistvo().clearValues();
        }
        /**
         * 编辑
         */
        if("/selfservice/filelist/addfilelist".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
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

}
