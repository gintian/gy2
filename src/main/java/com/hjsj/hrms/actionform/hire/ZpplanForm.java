/*
 * Created on 2005-8-8
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
 * <p>Title:ZpplanForm</p>
 * <p>Description:招聘计划表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class ZpplanForm extends FrameForm {

	/**
	 * 新建及修改功能控制
	 */
	private String flag = "0";

	private String flag_detail = "0";
	
	private String flag_mid = "0";
	
	private String flag_release = "0";
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
    /**zppositionForm当前页*/
    private int current_3=1;
	/**
	 * 招聘计划对象
	 */
	private RecordVo zpplanvo = new RecordVo("ZP_PLAN");

	/**
	 * 招聘计划对象列表
	 */
	private PaginationForm zpplanForm = new PaginationForm();

	/**
	 * 招聘计划明细对象
	 */
	private RecordVo zpplanDetailsvo = new RecordVo("ZP_PLAN_DETAILS");

	/**
	 *招聘计划明细对象列表
	 */
	private PaginationForm zpplanDetailsForm = new PaginationForm();
	/**
	 *招聘计划明细对象列表
	 */
	private PaginationForm shortPosForm = new PaginationForm();
	
	
	/**
	 * 引入用工需求对象
	 */
	private RecordVo zpgathervo = new RecordVo("ZP_Gather");

	/**
	 * 引入用工需求对象列表
	 */
	private PaginationForm zpgatherForm = new PaginationForm();
	
	/**
	 * 发布招聘岗位对象
	 */
	private RecordVo zppositionvo = new RecordVo("ZP_POSITION");

	/**
	 * 发布招聘岗位对象列表
	 */
	private PaginationForm zppositionForm = new PaginationForm();
	
	/**
	 *隐藏域及列表框所需属性
	 */
	private String org_id_value = "";
	private String dept_id_value = "";
	private String pos_id_value = "";
	private String dept_pos_id_value = "";
	private String plan_id_value = "";
	/**
	 *空缺职位列表
	 */
	private ArrayList shortPosList = new ArrayList();
	private String[] strE01A1 = null;
	/**
	 * 用来判断管理权限
	 */
    private String userid = "";
    private int current=1;
	@Override
    public void outPutFormHM() {
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setFlag_detail((String) this.getFormHM().get("flag_detail"));
		this.setZpplanvo((RecordVo) this.getFormHM().get("zpplanvo"));
		this.getZpplanForm().setList((ArrayList) this.getFormHM().get("zpplanlist"));
		this.setZpplanDetailsvo((RecordVo) this.getFormHM().get("zpplanDetailsvo"));
		this.getZpplanDetailsForm().setList((ArrayList) this.getFormHM().get("zpplanDetailslist"));
		this.setZpgathervo((RecordVo) this.getFormHM().get("zpgathervo"));
		this.getZpgatherForm().setList((ArrayList) this.getFormHM().get("zpgatherlist"));
		this.setZppositionvo((RecordVo) this.getFormHM().get("zppositionvo"));
		this.getZppositionForm().setList((ArrayList) this.getFormHM().get("zppositionlist"));
		this.setFlag_mid((String) this.getFormHM().get("flag_mid"));
		this.setFlag_release((String) this.getFormHM().get("flag_release"));
		this.setPlan_id_value((String) this.getFormHM().get("plan_id_value"));
		this.setOrg_id_value((String) this.getFormHM().get("org_id_value"));
		this.setDept_id_value((String) this.getFormHM().get("dept_id_value"));
		this.setDept_pos_id_value((String) this.getFormHM().get("dept_pos_id_value"));
        this.setPos_id_value((String) this.getFormHM().get("pos_id_value"));
        this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
        this.setDeptparentcode((String)this.getFormHM().get("deptparentcode"));
        this.setPosparentcode((String)this.getFormHM().get("posparentcode"));
        //this.setShortPosList((ArrayList)this.getFormHM().get("shortPosList"));
        this.getShortPosForm().setList((ArrayList)this.getFormHM().get("shortPosList"));
        this.setManagepriv((String)this.getFormHM().get("managepriv"));
        this.setStrE01A1((String[])this.getFormHM().get("strE01A1"));
        this.setUserid((String)this.getFormHM().get("userid"));
        /**重新定位到当前页*/
	    this.getZpplanForm().getPagination().gotoPage(current_1);
	    this.getZpplanDetailsForm().getPagination().gotoPage(current_2);
	    this.getZppositionForm().getPagination().gotoPage(current_3);
	    this.getZpplanForm().getPagination().gotoPage(current);
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedzpplanlist",(ArrayList) this.getZpplanForm().getSelectedList());
		this.getFormHM().put("zpplanvo", this.getZpplanvo());
		this.getFormHM().put("selectedlist",(ArrayList) this.getZpplanDetailsForm().getSelectedList());
		this.getFormHM().put("zpplanDetailsvo", this.getZpplanDetailsvo());
		this.getFormHM().put("selectedzppositionlist",(ArrayList) this.getZppositionForm().getSelectedList());
		this.getFormHM().put("zppositionvo", this.getZppositionvo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("flag_detail", this.getFlag_detail());
		this.getFormHM().put("flag_mid", this.getFlag_mid());
		this.getFormHM().put("flag_release", this.getFlag_release());
        this.getFormHM().put("plan_id_value", this.getPlan_id_value());
        this.getFormHM().put("org_id_value", this.getOrg_id_value());
        this.getFormHM().put("dept_id_value", this.getDept_id_value());
        this.getFormHM().put("dept_pos_id_value", this.getDept_pos_id_value());
        this.getFormHM().put("pos_id_value", this.getPos_id_value());
	    //this.getFormHM().put("strE01A1",this.getStrE01A1());
	    this.getFormHM().put("selectedShortPosList",(ArrayList)this.getShortPosForm().getSelectedList());
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
         * 新建招聘计划
         */
        if("/hire/zp_plan/search_zp_plan".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag("1");
            this.setFlag_mid("0");
            this.setOrg_id_value("");
            this.setDept_id_value("");
            this.getZpplanvo().clearValues();
        }
        /**
         * 新建招聘计划岗位
         */
        if("/hire/zp_plan/add_zp_plan".equals(arg0.getPath()) && arg1.getParameter("b_detail_add")!=null)
        {
            this.setFlag_detail("1");
            this.setDept_pos_id_value("");
            this.setPos_id_value("");
            this.getZpplanDetailsvo().clearValues();
        }
        if("/hire/zp_plan/add_zp_pos".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(zpplanDetailsForm.getPagination()!=null)
            {
            	if("1".equals(this.getFlag_detail()))
            		zpplanDetailsForm.getPagination().lastPage();
            	current_2=zpplanDetailsForm.getPagination().getCurrent(); 
            }
        }
        if("/hire/zp_plan/add_zp_plan".equals(arg0.getPath())&&(arg1.getParameter("b_detail_delete")!=null))
        {
            if(zpplanDetailsForm.getPagination()!=null)
            {
            	current_2=zpplanDetailsForm.getPagination().getCurrent();
            }
        }
        if("/hire/zp_plan/add_zp_plan".equals(arg0.getPath())&&(arg1.getParameter("b_request")!=null))
        {
            if(zpplanDetailsForm.getPagination()!=null)
            {
            	current_2=zpplanDetailsForm.getPagination().getCurrent();
            }
        }
        /**
         * 新建发布招聘岗位
         */
        if("/hire/zp_plan/search_release_poslist".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag_release("1");
            this.setDept_id_value("");
            this.setPos_id_value("");
            this.getZppositionvo().clearValues();
        }
        if("/hire/zp_plan/add_release_pos".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(zppositionForm.getPagination()!=null)
            {
            	if("1".equals(this.getFlag_release()))
            		zppositionForm.getPagination().lastPage();
            	current_3=zppositionForm.getPagination().getCurrent(); 
            }
        }
        if("/hire/zp_plan/search_release_poslist".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(zppositionForm.getPagination()!=null)
            {
            	current_3=zppositionForm.getPagination().getCurrent();
            }
        }
        /**
         * 编辑招聘计划
         */
        if(("/hire/zp_plan/add_zp_plan".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null) ||("/hire/zp_plan/view_zp_plan".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null))
        {
            this.setFlag("0");
            this.setFlag_mid("0");
        }
        /**
         * 编辑招聘计划岗位
         */
        if("/hire/zp_plan/add_zp_pos".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag_detail("0");
            if(zpplanDetailsForm.getPagination()!=null)
            {            
            	current_2=zpplanDetailsForm.getPagination().getCurrent();    
            }
        }
        /**
         * 编辑发布招聘岗位
         */
        if("/hire/zp_plan/search_release_poslist".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag_release("0");
            if(zppositionForm.getPagination()!=null)
            {            
            	current_3=zppositionForm.getPagination().getCurrent();    
            }
        }
        /**
         * 编辑查询发布招聘岗位
         */
        if("/hire/zp_plan/add_zp_plan".equals(arg0.getPath()) && arg1.getParameter("b_release")!=null)
        {
            this.setFlag_release("2");
        }      
       

        if("/hire/zp_plan/add_zp_plan".equals(arg0.getPath()) && arg1.getParameter("b_save")!=null)
        {
            if(this.zpplanForm.getPagination()!=null)
	        	 this.zpplanForm.getPagination().lastPage();
	        	 current=this.zpplanForm.getPagination().getCurrent();
        }
        
        if("/hire/zp_plan/search_zp_plan".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(zpplanForm.getPagination()!=null)
            {
            	current_1=zpplanForm.getPagination().getCurrent();
      
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
	 * @return Returns the zpgatherForm.
	 */
	public PaginationForm getZpgatherForm() {
		return zpgatherForm;
	}
	/**
	 * @param zpgatherForm The zpgatherForm to set.
	 */
	public void setZpgatherForm(PaginationForm zpgatherForm) {
		this.zpgatherForm = zpgatherForm;
	}
	/**
	 * @return Returns the zpgathervo.
	 */
	public RecordVo getZpgathervo() {
		return zpgathervo;
	}
	/**
	 * @param zpgathervo The zpgathervo to set.
	 */
	public void setZpgathervo(RecordVo zpgathervo) {
		this.zpgathervo = zpgathervo;
	}
	/**
	 * @return Returns the zppositionForm.
	 */
	public PaginationForm getZppositionForm() {
		return zppositionForm;
	}
	/**
	 * @param zppositionForm The zppositionForm to set.
	 */
	public void setZppositionForm(PaginationForm zppositionForm) {
		this.zppositionForm = zppositionForm;
	}
	/**
	 * @return Returns the zppositionvo.
	 */
	public RecordVo getZppositionvo() {
		return zppositionvo;
	}
	/**
	 * @param zppositionvo The zppositionvo to set.
	 */
	public void setZppositionvo(RecordVo zppositionvo) {
		this.zppositionvo = zppositionvo;
	}
	/**
	 * @return Returns the flag_release.
	 */
	public String getFlag_release() {
		return flag_release;
	}
	/**
	 * @param flag_release The flag_release to set.
	 */
	public void setFlag_release(String flag_release) {
		this.flag_release = flag_release;
	}
	/**
	 * @return Returns the dept_pos_id_value.
	 */
	public String getDept_pos_id_value() {
		return dept_pos_id_value;
	}
	/**
	 * @param dept_pos_id_value The dept_pos_id_value to set.
	 */
	public void setDept_pos_id_value(String dept_pos_id_value) {
		this.dept_pos_id_value = dept_pos_id_value;
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
	 * @return Returns the current_3.
	 */
	public int getCurrent_3() {
		return current_3;
	}
	/**
	 * @param current_3 The current_3 to set.
	 */
	public void setCurrent_3(int current_3) {
		this.current_3 = current_3;
	}
	/**
	 * @return Returns the shortPosList.
	 */
	public ArrayList getShortPosList() {
		return shortPosList;
	}
	/**
	 * @param shortPosList The shortPosList to set.
	 */
	public void setShortPosList(ArrayList shortPosList) {
		this.shortPosList = shortPosList;
	}
	/**
	 * @return Returns the strE01A1.
	 */
	public String[] getStrE01A1() {
		return strE01A1;
	}
	/**
	 * @param strE01A1 The strE01A1 to set.
	 */
	public void setStrE01A1(String[] strE01A1) {
		this.strE01A1 = strE01A1;
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
	/**
	 * @return Returns the shortPosForm.
	 */
	public PaginationForm getShortPosForm() {
		return shortPosForm;
	}
	/**
	 * @param shortPosForm The shortPosForm to set.
	 */
	public void setShortPosForm(PaginationForm shortPosForm) {
		this.shortPosForm = shortPosForm;
	}
}
