/*
 * Created on 2005-8-5
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
 * <p>Title:ResourcePlanForm</p>
 * <p>Description:招聘管理人力规划表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class ResourcePlanForm extends FrameForm {

	/**
	 * 新建及修改功能控制
	 */
	private String flag = "0";

	private String flag_detail = "0";
	
	private String flag_mid = "0";
	/**
	 *用于单位、部门、职位
	 */
	private String orgparentcode = "";
    
    private String deptparentcode = "";
    
    private String posparentcode = "";
    /**
	 *用于管理权限
	 */
    private String managepriv = "";
	/**zpplanForm当前页*/
    private int current_1=1;
    /**zpplanDetailsForm当前页*/
    private int current_2=1;

	/**
	 * 人力规划对象
	 */
	private RecordVo zpplanvo = new RecordVo("ZP_HR_PLAN",1);

	/**
	 * 人力规划对象列表
	 */
	private PaginationForm zpplanForm = new PaginationForm();

	/**
	 * 人力规划明细对象
	 */
	private RecordVo zpplanDetailsvo = new RecordVo("ZP_HR_PLAN_DETAILS",1);

	/**
	 *人力规划明细对象列表
	 */
	private PaginationForm zpplanDetailsForm = new PaginationForm();
	
	/**
	 *隐藏域及列表框所需属性
	 */
	private String org_id_value = "";
	private String dept_id_value = "";
	private String pos_id_value = "";
	private String plan_id_value = "";
	 /**
	 * 用来判断管理权限
	 */
    private String userid = "";
	
	@Override
    public void outPutFormHM() {

		this.setFlag((String) this.getFormHM().get("flag"));
		this.setFlag_detail((String) this.getFormHM().get("flag_detail"));
		this.setZpplanvo((RecordVo) this.getFormHM().get("zpplanvo"));
		this.getZpplanForm().setList(
				(ArrayList) this.getFormHM().get("zpplanlist"));
		this.setZpplanDetailsvo((RecordVo) this.getFormHM().get(
				"zpplanDetailsvo"));
		this.getZpplanDetailsForm().setList(
				(ArrayList) this.getFormHM().get("zpplanDetailslist"));
		this.setPlan_id_value((String) this.getFormHM().get("plan_id_value"));
		this.setFlag_mid((String) this.getFormHM().get("flag_mid"));
		this.setOrg_id_value((String) this.getFormHM().get("org_id_value"));
		this.setDept_id_value((String) this.getFormHM().get("dept_id_value"));
		this.setPos_id_value((String) this.getFormHM().get("pos_id_value"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
        this.setDeptparentcode((String)this.getFormHM().get("deptparentcode"));
        this.setPosparentcode((String)this.getFormHM().get("posparentcode"));
        this.setManagepriv((String)this.getFormHM().get("managepriv"));
        this.setUserid((String)this.getFormHM().get("userid"));
		/**重新定位到当前页*/
	    this.getZpplanForm().getPagination().gotoPage(current_1);
	    this.getZpplanDetailsForm().getPagination().gotoPage(current_2);
        
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {

		this.getFormHM().put("selectedzpplanlist",
				(ArrayList) this.getZpplanForm().getSelectedList());
		this.getFormHM().put("zpplanvo", this.getZpplanvo());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getZpplanDetailsForm().getSelectedList());
		this.getFormHM().put("zpplanDetailsvo", this.getZpplanDetailsvo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("flag_detail", this.getFlag_detail());
		this.getFormHM().put("plan_id_value", this.getPlan_id_value());
		this.getFormHM().put("org_id_value", this.getOrg_id_value());
		this.getFormHM().put("dept_id_value", this.getDept_id_value());
		this.getFormHM().put("pos_id_value", this.getPos_id_value());
		this.getFormHM().put("flag_mid", this.getFlag_mid());

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
	         * 新建人力规划
	         */
	        if("/hire/resource_plan/search_resource_list".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	            this.setFlag("1");
	            this.setFlag_mid("0");
	            this.setPlan_id_value("");
	            this.setOrg_id_value("");
	            this.getZpplanvo().clearValues();
	        }
	        /**
	         * 新建人力规划岗位
	         */
	        if("/hire/resource_plan/resource_plan".equals(arg0.getPath()) && arg1.getParameter("b_detail_add")!=null)
	        {
	            this.setFlag_detail("1");
	            this.setDept_id_value("");
	            this.setPos_id_value("");
	            this.getZpplanDetailsvo().clearValues();
	        }
	        if("/hire/resource_plan/add_plan_pos".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
	        {
	            if(zpplanDetailsForm.getPagination()!=null)
	            {
	            	if("1".equals(this.getFlag_detail()))
	            		zpplanDetailsForm.getPagination().lastPage();
	            	current_2=zpplanDetailsForm.getPagination().getCurrent();
	            }
	        }   
	        /**
	         * 编辑人力规划
	         */
	        if(("/hire/resource_plan/resource_plan".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null) ||("/hire/resource_plan/resource_plan_view".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null))
	        {
	            this.setFlag("0");
	            this.setFlag_mid("0");
	        }
	        /**
	         * 编辑人力规划岗位
	         */
	        if("/hire/resource_plan/add_plan_pos".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	            this.setFlag_detail("0");
	            if(zpplanDetailsForm.getPagination()!=null)
	            {            
	            	current_2=zpplanDetailsForm.getPagination().getCurrent();    
	            }
	        }
	        if("/hire/resource_plan/resource_plan".equals(arg0.getPath()) && arg1.getParameter("b_save")!=null)
	        {
	            this.setFlag_mid("1");
	        }
	        
	        if("/hire/resource_plan/resource_plan".equals(arg0.getPath())&&(arg1.getParameter("b_detail_delete")!=null))
	        {
	          if(zpplanDetailsForm.getPagination()!=null)
	           {
	            	current_2=zpplanDetailsForm.getPagination().getCurrent();
	            }
	        }	       
	        if("/hire/resource_plan/search_resource_list".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
	        {
	            if(zpplanForm.getPagination()!=null)
	            {
	            	current_1=zpplanForm.getPagination().getCurrent();
	            }
	        }
	        /**
	         * 提交人力规划
	         */
	        if("/hire/resource_plan/resource_plan".equals(arg0.getPath()) && arg1.getParameter("b_submit")!=null)
	        {
	        	if(zpplanForm.getPagination()!=null)
	            {
	        		current_1=zpplanForm.getPagination().getCurrent();
	            }
	        }
	       
	      
	        return super.validate(arg0, arg1);
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
	 * @return Returns the zpplanDetailsForm.
	 */
	public PaginationForm getZpplanDetailsForm() {
		return zpplanDetailsForm;
	}

	/**
	 * @param zpplanDetailsForm The zpplanDetailsForm to set.
	 */
	public void setZpplanDetailsForm(PaginationForm zpplanDetailsForm) {
		this.zpplanDetailsForm = zpplanDetailsForm;
	}

	/**
	 * @return Returns the zpplanDetailsvo.
	 */
	public RecordVo getZpplanDetailsvo() {
		return zpplanDetailsvo;
	}

	/**
	 * @param zpplanDetailsvo The zpplanDetailsvo to set.
	 */
	public void setZpplanDetailsvo(RecordVo zpplanDetailsvo) {
		this.zpplanDetailsvo = zpplanDetailsvo;
	}

	/**
	 * @return Returns the zpplanForm.
	 */
	public PaginationForm getZpplanForm() {
		return zpplanForm;
	}

	/**
	 * @param zpplanForm The zpplanForm to set.
	 */
	public void setZpplanForm(PaginationForm zpplanForm) {
		this.zpplanForm = zpplanForm;
	}

	/**
	 * @return Returns the zpplanvo.
	 */
	public RecordVo getZpplanvo() {
		return zpplanvo;
	}

	/**
	 * @param zpplanvo The zpplanvo to set.
	 */
	public void setZpplanvo(RecordVo zpplanvo) {
		this.zpplanvo = zpplanvo;
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
	 * @return Returns the org_id_value.
	 */
	public String getOrg_id_value() {
		return org_id_value;
	}
	/**
	 * @param org_id_value The org_id_value to set.
	 */
	public void setOrg_id_value(String org_id_value) {
		this.org_id_value = org_id_value;
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
	 * @return Returns the plan_id_value.
	 */
	public String getPlan_id_value() {
		return plan_id_value;
	}
	/**
	 * @param plan_id_value The plan_id_value to set.
	 */
	public void setPlan_id_value(String plan_id_value) {
		this.plan_id_value = plan_id_value;
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
	 * @return Returns the orgparentcode.
	 */
	public String getOrgparentcode() {
		return orgparentcode;
	}
	/**
	 * @param orgparentcode The orgparentcode to set.
	 */
	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
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