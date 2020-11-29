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
 * <p>Title:ZpjobForm</p>
 * <p>Description:招聘活动表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class ZpjobForm extends FrameForm {

	/**
	 * 新建及修改功能控制
	 */
	private String flag = "0";

	private String flag_detail = "0";
	
	private String flag_mid = "0";
	
	/**zpjobForm当前页*/
    private int current_1=1;
    /**zpjobDetailsForm当前页*/
    private int current_2=1;

	/**
	 * 招聘活动对象
	 */
	private RecordVo zpjobvo = new RecordVo("ZP_JOB");

	/**
	 * 招聘活动对象列表
	 */
	private PaginationForm zpjobForm = new PaginationForm();

	/**
	 * 招聘活动明细对象
	 */
	private RecordVo zpjobDetailsvo = new RecordVo("ZP_JOB_DETAILS");

	/**
	 *招聘活动明细对象列表
	 */
	private PaginationForm zpjobDetailsForm = new PaginationForm();
	
	/**
	 *隐藏域及列表框所需属性
	 */
	
	private String status = "";
	
	private String zp_job_id_value = "";
	/**
	 *用于管理权限
	 */
	private String managepriv = "";
	private String manageprivvalue = "";
	/**
	 * 用来判断管理权限
	 */
    private String userid = "";
	
	@Override
    public void outPutFormHM() {
		
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setFlag_detail((String) this.getFormHM().get("flag_detail"));
		this.setZpjobvo((RecordVo) this.getFormHM().get("zpjobvo"));
		this.getZpjobForm().setList(
				(ArrayList) this.getFormHM().get("zpjoblist"));
		this.setZpjobDetailsvo((RecordVo) this.getFormHM().get(
				"zpjobDetailsvo"));
		this.getZpjobDetailsForm().setList(
				(ArrayList) this.getFormHM().get("zpjobDetailslist"));
		this.setStatus((String) this.getFormHM().get("status"));
		this.setZp_job_id_value((String) this.getFormHM().get("zp_job_id_value"));
		this.setFlag_mid((String) this.getFormHM().get("flag_mid"));
		this.setManagepriv((String) this.getFormHM().get("managepriv"));
		this.setManageprivvalue((String) this.getFormHM().get("manageprivvalue"));
		this.setUserid((String)this.getFormHM().get("userid"));
		/**重新定位到当前页*/
	    this.getZpjobForm().getPagination().gotoPage(current_1);
	    this.getZpjobDetailsForm().getPagination().gotoPage(current_2);
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("selectedzpjoblist",
				(ArrayList) this.getZpjobForm().getSelectedList());
		this.getFormHM().put("zpjobvo", this.getZpjobvo());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getZpjobDetailsForm().getSelectedList());
		this.getFormHM().put("zpjobDetailsvo", this.getZpjobDetailsvo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("flag_detail", this.getFlag_detail());
		this.getFormHM().put("status", this.getStatus());
		this.getFormHM().put("zp_job_id_value", this.getZp_job_id_value());
		this.getFormHM().put("flag_mid", this.getFlag_mid());

	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
    	
        super.reset(arg0, arg1);
        
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        /**
         * 新建招聘活动
         */
        if("/hire/zp_job/search_zp_joblist".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag("1");
            this.setFlag_mid("0");
            this.getZpjobvo().clearValues();
        }
        /**
         * 新建招聘活动明细
         */
        if("/hire/zp_job/add_zp_job".equals(arg0.getPath()) && arg1.getParameter("b_detail_add")!=null)
        {
            this.setFlag_detail("1");
            this.getZpjobDetailsvo().clearValues();
        }
        if("/hire/zp_job/add_zp_job_detail".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(zpjobDetailsForm.getPagination()!=null)
            {
            	if("1".equals(this.getFlag_detail()))
            		zpjobDetailsForm.getPagination().lastPage();
            	current_2=zpjobDetailsForm.getPagination().getCurrent(); 
            }
        } 
        /**
         * 编辑招聘活动
         */
        if(("/hire/zp_job/add_zp_job".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null) ||("/hire/zp_job/view_zp_job".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null))
        {
            this.setFlag("0");
            this.setFlag_mid("0");
        }
        /**
         * 编辑招聘活动明细
         */
        if("/hire/zp_job/add_zp_job_detail".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag_detail("0");
            if(zpjobDetailsForm.getPagination()!=null)
            {            
            	current_2=zpjobDetailsForm.getPagination().getCurrent();    
            }
        }
        
        if("/hire/zp_job/add_zp_job".equals(arg0.getPath()) && arg1.getParameter("b_save")!=null)
        {
            this.setFlag_mid("1");
        }
        
        if("/hire/zp_job/add_zp_job".equals(arg0.getPath())&&(arg1.getParameter("b_detail_delete")!=null))
        {
            if(zpjobDetailsForm.getPagination()!=null)
            {
            	current_2=zpjobDetailsForm.getPagination().getCurrent();
            }
        }
        if("/hire/zp_job/search_zp_joblist".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(zpjobForm.getPagination()!=null)
            {
            	current_1=zpjobForm.getPagination().getCurrent();
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
	 * @return Returns the flag_detail.
	 */
	public String getFlag_detail() {
		return flag_detail;
	}
	/**
	 * @param flag_detail The flag_detail to set.
	 */
	public void setFlag_detail(String flag_detail) {
		this.flag_detail = flag_detail;
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return Returns the zpjobDetailsForm.
	 */
	public PaginationForm getZpjobDetailsForm() {
		return zpjobDetailsForm;
	}
	/**
	 * @param zpjobDetailsForm The zpjobDetailsForm to set.
	 */
	public void setZpjobDetailsForm(PaginationForm zpjobDetailsForm) {
		this.zpjobDetailsForm = zpjobDetailsForm;
	}
	/**
	 * @return Returns the zpjobDetailsvo.
	 */
	public RecordVo getZpjobDetailsvo() {
		return zpjobDetailsvo;
	}
	/**
	 * @param zpjobDetailsvo The zpjobDetailsvo to set.
	 */
	public void setZpjobDetailsvo(RecordVo zpjobDetailsvo) {
		this.zpjobDetailsvo = zpjobDetailsvo;
	}
	/**
	 * @return Returns the zpjobForm.
	 */
	public PaginationForm getZpjobForm() {
		return zpjobForm;
	}
	/**
	 * @param zpjobForm The zpjobForm to set.
	 */
	public void setZpjobForm(PaginationForm zpjobForm) {
		this.zpjobForm = zpjobForm;
	}
	/**
	 * @return Returns the zpjobvo.
	 */
	public RecordVo getZpjobvo() {
		return zpjobvo;
	}
	/**
	 * @param zpjobvo The zpjobvo to set.
	 */
	public void setZpjobvo(RecordVo zpjobvo) {
		this.zpjobvo = zpjobvo;
	}
	/**
	 * @return Returns the zp_job_id_value.
	 */
	public String getZp_job_id_value() {
		return zp_job_id_value;
	}
	/**
	 * @param zp_job_id_value The zp_job_id_value to set.
	 */
	public void setZp_job_id_value(String zp_job_id_value) {
		this.zp_job_id_value = zp_job_id_value;
	}
	/**
	 * @return Returns the flag_mid.
	 */
	public String getFlag_mid() {
		return flag_mid;
	}
	/**
	 * @param flag_mid The flag_mid to set.
	 */
	public void setFlag_mid(String flag_mid) {
		this.flag_mid = flag_mid;
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
	/**
	 * @return Returns the managepriv.
	 */
	public String getManagepriv() {
		return managepriv;
	}
	/**
	 * @param managepriv The managepriv to set.
	 */
	public void setManagepriv(String managepriv) {
		this.managepriv = managepriv;
	}
	/**
	 * @return Returns the manageprivvalue.
	 */
	public String getManageprivvalue() {
		return manageprivvalue;
	}
	/**
	 * @param manageprivvalue The manageprivvalue to set.
	 */
	public void setManageprivvalue(String manageprivvalue) {
		this.manageprivvalue = manageprivvalue;
	}
	/**
	 * @return Returns the userid.
	 */
	public String getUserid() {
		return userid;
	}
	/**
	 * @param userid The userid to set.
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}
}
