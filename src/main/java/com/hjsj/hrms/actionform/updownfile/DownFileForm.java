/*
 * Created on 2005-5-30
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
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownFileForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        private String fromPage="";  //front:首页
        
	    /**
	     * 建议对象
	     */
	    private RecordVo downFilevo=new RecordVo("resource_list");
	    
	    /**
	     * 建议对象列表
	     */
	    
	    private PaginationForm downFileForm=new PaginationForm();     
	    
	    @Override
        public void outPutFormHM() {
	       
	        this.getDownFileForm().setList((ArrayList)this.getFormHM().get("downFilelist"));
	        this.setFromPage((String)this.getFormHM().get("fromPage"));
	        
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
	    	HashMap reqMap = (HashMap)this.getFormHM().get("requestPamaHM");
	    	//【45372 】点击菜单时初始化页签
	    	if(reqMap!=null&&reqMap.get("b_query")!=null)
	    		downFileForm=new PaginationForm();
		    this.getFormHM().put("selectedlist",(ArrayList)this.getDownFileForm().getSelectedList());
	       
	        this.getFormHM().put("flag",this.getFlag());      
	    }

	    /**
	     * @return Returns the proposeForm.
	     */
	    public PaginationForm getDownFileForm() {
	        return downFileForm;
	    }
	    /**
	     * @param proposeForm The proposeForm to set.
	     */
	    public void setDownFileForm(PaginationForm downFileForm) {
	        this.downFileForm = downFileForm;
	    }
	    /**
	     * @return Returns the downFilevo.
	     */
	    public RecordVo getDownFilevo() {
	        return downFilevo;
	    }
	    /**
	     * @param proposevo The downFilevo to set.
	     */
	    public void setDownFilevo(RecordVo downFilevo) {
	        this.downFilevo = downFilevo;
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
	    	
	    	String fileflag=arg1.getParameter("fileflag");
	    	if(fileflag==null)
	    	{
	    		
	    	}
	    	else
	    	{
	    		this.setFlag(fileflag);
	    		
	    	}
	       
	        return super.validate(arg0, arg1);
	    }
	  
	    public String getFlag() {
	        return flag;
	    }
	    
	    /**
	     * @param flag The flag to set.
	     */
	   
	    public void setFlag(String flag) {
	        this.flag = flag;
	    }

		public String getFromPage() {
			return fromPage;
		}

		public void setFromPage(String fromPage) {
			this.fromPage = fromPage;
		}
	    
	    /**
	     * @return Returns the approve.
	     */
	   
	  

}
