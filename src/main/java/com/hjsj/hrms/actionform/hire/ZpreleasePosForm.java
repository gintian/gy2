/*
 * Created on 2005-8-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:ZpreleasePosForm</p>
 * <p>Description:招聘发布岗位表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class ZpreleasePosForm extends FrameForm {
	CardTagParamView cardparam=new CardTagParamView();
	/**
	 * 新建及修改功能控制
	 */
    private String flag="0";
    /**当前页*/
    private int current=1;
    
    private String deptparentcode = "";
    
    private String posparentcode = "";
    /**
     * 发布招聘岗位对象
     */
    private RecordVo zpreleasePosvo=new RecordVo("ZP_POSITION");
    /**
     * 发布招聘岗位对象列表
     */
    private PaginationForm zpreleasePosForm=new PaginationForm();
    
    /**
	 *隐藏域及列表框所需属性
	 */
	private String dept_id_value = "";
	
	private String pos_id_value = "";

	@Override
    public void outPutFormHM() {
		
    	this.setDept_id_value((String)this.getFormHM().get("dept_id_value"));
    	this.setPos_id_value((String)this.getFormHM().get("pos_id_value"));
    	this.setFlag((String)this.getFormHM().get("flag"));
        this.setZpreleasePosvo((RecordVo)this.getFormHM().get("zpreleasePosvo"));
        this.getZpreleasePosForm().setList((ArrayList)this.getFormHM().get("zpreleasePoslist"));
        this.setDeptparentcode((String)this.getFormHM().get("deptparentcode"));
        this.setPosparentcode((String)this.getFormHM().get("posparentcode"));
        /**重新定位到当前页*/
	    this.getZpreleasePosForm().getPagination().gotoPage(current);
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		
        this.getFormHM().put("selectedlist",(ArrayList)this.getZpreleasePosForm().getSelectedList());
        this.getFormHM().put("zpreleasePosvo",this.getZpreleasePosvo());
	    this.getFormHM().put("flag",this.getFlag());
	    this.getFormHM().put("dept_id_value",this.getDept_id_value());
	    this.getFormHM().put("pos_id_value",this.getPos_id_value());
	    this.getFormHM().put("deptparentcode",this.getDeptparentcode());
	    this.getFormHM().put("posparentcode",this.getPosparentcode());
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
	         * 新建发布招聘岗位
	         */
	        if("/hire/zp_release_pos/search_release_poslist".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	            this.setFlag("1");
	            this.setDept_id_value("");
	            this.setPos_id_value("");
	            this.getZpreleasePosvo().clearValues();
	        }
	        if("/hire/zp_release_pos/add_zp_pos".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
	        {
	            if(zpreleasePosForm.getPagination()!=null)
	            {
	            	if("1".equals(this.getFlag()))
	            		zpreleasePosForm.getPagination().lastPage();
	                current=zpreleasePosForm.getPagination().getCurrent(); 
	            }
	        }
	        /**
	         * 编辑发布招聘岗位
	         */
	        if("/hire/zp_release_pos/add_zp_pos".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	            this.setFlag("0");
	            if(zpreleasePosForm.getPagination()!=null)
	            {            
	            	current=zpreleasePosForm.getPagination().getCurrent();    
	            }
	        }
	        if("/hire/zp_release_pos/search_release_poslist".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
	        {
	            if(zpreleasePosForm.getPagination()!=null)
	            {
	                current=zpreleasePosForm.getPagination().getCurrent();
	            }
	        }
	        
	        return super.validate(arg0, arg1);
	    }

	/**
	 * @return Returns the dept_id_value.
	 */
	public String getDept_id_value() {
		return dept_id_value;
	}
	/**
	 * @param dept_id_value The dept_id_value to set.
	 */
	public void setDept_id_value(String dept_id_value) {
		this.dept_id_value = dept_id_value;
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
	 * @return Returns the pos_id_value.
	 */
	public String getPos_id_value() {
		return pos_id_value;
	}
	/**
	 * @param pos_id_value The pos_id_value to set.
	 */
	public void setPos_id_value(String pos_id_value) {
		this.pos_id_value = pos_id_value;
	}
	/**
	 * @return Returns the zpreleasePosForm.
	 */
	public PaginationForm getZpreleasePosForm() {
		return zpreleasePosForm;
	}
	/**
	 * @param zpreleasePosForm The zpreleasePosForm to set.
	 */
	public void setZpreleasePosForm(PaginationForm zpreleasePosForm) {
		this.zpreleasePosForm = zpreleasePosForm;
	}
	/**
	 * @return Returns the zpreleasePosvo.
	 */
	public RecordVo getZpreleasePosvo() {
		return zpreleasePosvo;
	}
	/**
	 * @param zpreleasePosvo The zpreleasePosvo to set.
	 */
	public void setZpreleasePosvo(RecordVo zpreleasePosvo) {
		this.zpreleasePosvo = zpreleasePosvo;
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
	 * @return Returns the deptparentcode.
	 */
	public String getDeptparentcode() {
		return deptparentcode;
	}
	/**
	 * @param deptparentcode The deptparentcode to set.
	 */
	public void setDeptparentcode(String deptparentcode) {
		this.deptparentcode = deptparentcode;
	}
	/**
	 * @return Returns the posparentcode.
	 */
	public String getPosparentcode() {
		return posparentcode;
	}
	/**
	 * @param posparentcode The posparentcode to set.
	 */
	public void setPosparentcode(String posparentcode) {
		this.posparentcode = posparentcode;
	}
	/**
	 * @return Returns the cardparam.
	 */
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	/**
	 * @param cardparam The cardparam to set.
	 */
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
}
