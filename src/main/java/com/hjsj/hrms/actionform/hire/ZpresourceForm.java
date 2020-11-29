/*
 * Created on 2005-8-9
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
 * <p>Title:ZpresourceForm</p>
 * <p>Description:招聘资源表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class ZpresourceForm extends FrameForm {

	/**
	 * 新建及修改功能控制
	 */
	private String flag_set = "0";

	private String flag = "0";

	/**zpresourceForm当前页*/
    private int current_1=1;
    /**zpresourceSetForm当前页*/
    private int current_2=1;
    
	/**
	 * 招聘资源分类对象
	 */
	private RecordVo zpresourceSetvo = new RecordVo("ZP_RESOURCE_SET");

	/**
	 * 招聘资源分类对象列表
	 */
	private PaginationForm zpresourceSetForm = new PaginationForm();

	/**
	 * 招聘资源对象
	 */
	private RecordVo zpresourcevo = new RecordVo("ZP_RESOURCE");

	/**
	 *招聘资源对象列表
	 */
	private PaginationForm zpresourceForm = new PaginationForm();
	
	@Override
    public void outPutFormHM() {
		
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setFlag_set((String) this.getFormHM().get("flag_set"));
		this.setZpresourceSetvo((RecordVo) this.getFormHM().get("zpresourceSetvo"));
		this.getZpresourceSetForm().setList((ArrayList) this.getFormHM().get("zpresourceSetlist"));
		this.setZpresourcevo((RecordVo) this.getFormHM().get("zpresourcevo"));
		this.getZpresourceForm().setList((ArrayList) this.getFormHM().get("zpresourcelist"));
	    /**重新定位到当前页*/
	    this.getZpresourceForm().getPagination().gotoPage(current_1);
	    this.getZpresourceSetForm().getPagination().gotoPage(current_2);
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedzpresourceSetlist",
				(ArrayList) this.getZpresourceSetForm().getSelectedList());
		this.getFormHM().put("zpresourceSetvo", this.getZpresourceSetvo());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getZpresourceForm().getSelectedList());
		this.getFormHM().put("zpresourcevo", this.getZpresourcevo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("flag_set", this.getFlag_set());
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
    	
        super.reset(arg0, arg1);
        
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        /**
         * 新建招聘资源分类
         */
        if("/hire/zp_resource/zp_resource_set".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag_set("1");
            this.getZpresourceSetvo().clearValues();
        }
        /**
         * 新建招聘资源
         */
        if("/hire/zp_resource/search_zp_resource".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag("1");
            this.getZpresourcevo().clearValues();
        }
        /**
         * 编辑招聘资源分类
         */
        if("/hire/zp_resource/add_resource_type".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag_set("0");
            if(zpresourceSetForm.getPagination()!=null)
            {            
            	current_2=zpresourceSetForm.getPagination().getCurrent();    
            }
        }
        /**
         * 编辑招聘资源
         */
        if("/hire/zp_resource/zp_resource".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
            if(zpresourceForm.getPagination()!=null)
            {            
            	current_1=zpresourceForm.getPagination().getCurrent();    
            }
        }
        /**
         * 分页设置
         */
        if("/hire/zp_resource/zp_resource".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(zpresourceForm.getPagination()!=null)
            {
            	if("1".equals(this.flag))
            		zpresourceForm.getPagination().lastPage();
            	current_1=zpresourceForm.getPagination().getCurrent(); 
            }
        }   
        if("/hire/zp_resource/search_zp_resource".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(zpresourceForm.getPagination()!=null)
            {
            	current_1=zpresourceForm.getPagination().getCurrent();
            }
        }  
        if("/hire/zp_resource/zp_resource_set".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(zpresourceSetForm.getPagination()!=null)
            {
            	current_2=zpresourceSetForm.getPagination().getCurrent();
            }
        }  
        /**
         * 分页设置
         */
        if("/hire/zp_resource/add_resource_type".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(zpresourceSetForm.getPagination()!=null)
            {
            	if("1".equals(this.flag_set))
            		zpresourceSetForm.getPagination().lastPage();
            	current_2=zpresourceSetForm.getPagination().getCurrent(); 
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
	 * @return Returns the zpresourceForm.
	 */
	public PaginationForm getZpresourceForm() {
		return zpresourceForm;
	}
	/**
	 * @param zpresourceForm The zpresourceForm to set.
	 */
	public void setZpresourceForm(PaginationForm zpresourceForm) {
		this.zpresourceForm = zpresourceForm;
	}
	/**
	 * @return Returns the zpresourceSetForm.
	 */
	public PaginationForm getZpresourceSetForm() {
		return zpresourceSetForm;
	}
	/**
	 * @param zpresourceSetForm The zpresourceSetForm to set.
	 */
	public void setZpresourceSetForm(PaginationForm zpresourceSetForm) {
		this.zpresourceSetForm = zpresourceSetForm;
	}
	/**
	 * @return Returns the zpresourceSetvo.
	 */
	public RecordVo getZpresourceSetvo() {
		return zpresourceSetvo;
	}
	/**
	 * @param zpresourceSetvo The zpresourceSetvo to set.
	 */
	public void setZpresourceSetvo(RecordVo zpresourceSetvo) {
		this.zpresourceSetvo = zpresourceSetvo;
	}
	/**
	 * @return Returns the zpresourcevo.
	 */
	public RecordVo getZpresourcevo() {
		return zpresourcevo;
	}
	/**
	 * @param zpresourcevo The zpresourcevo to set.
	 */
	public void setZpresourcevo(RecordVo zpresourcevo) {
		this.zpresourcevo = zpresourcevo;
	}
	/**
	 * @return Returns the flag_set.
	 */
	public String getFlag_set() {
		return flag_set;
	}
	/**
	 * @param flag_set The flag_set to set.
	 */
	public void setFlag_set(String flag_set) {
		this.flag_set = flag_set;
	}
	/**
	 * @return Returns the current_1.
	 */
	public int getCurrent_1() {
		return current_1;
	}
	/**
	 * @param current_1 The current_1 to set.
	 */
	public void setCurrent_1(int current_1) {
		this.current_1 = current_1;
	}
	/**
	 * @return Returns the current_2.
	 */
	public int getCurrent_2() {
		return current_2;
	}
	/**
	 * @param current_2 The current_2 to set.
	 */
	public void setCurrent_2(int current_2) {
		this.current_2 = current_2;
	}
}
