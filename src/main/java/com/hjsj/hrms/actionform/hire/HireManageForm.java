/*
 * Created on 2005-8-2
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
 * <p>Title:HireManageForm</p>
 * <p>Description:招聘管理临时用工申请表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class HireManageForm extends FrameForm {
	
	/**
	 * 新建及修改功能控制
	 */
    private String flag="0";
    
    private String flag_pos = "0";
    
    private String flag_mid = "0";
    
    private String userAdmin="false";
    /**
     * 用于单位、部门、职位
     */
    private String orgparentcode = "";
    
    private String deptparentcode = "";
    
    private String posparentcode = "";
    /**
     * 用于管理权限
     */
    private String managepriv = "";
    
    /**zpgatherForm当前页*/
    private int current_1=1;
    /**gatherPosForm当前页*/
    private int current_2=1;
    
    /**
     * 临时用工申请对象
     */
    private RecordVo zpgathervo=new RecordVo("ZP_GATHER");
    /**
     * 临时用工申请对象列表
     */
    private PaginationForm zpgatherForm=new PaginationForm(); 
    /**
     * 临时用工申请岗位对象
     */
    private RecordVo gatherPosvo=new RecordVo("ZP_GATHER_POS");
    /**
     * 临时用工申请岗位对象列表
     */
    private PaginationForm gatherPosForm=new PaginationForm();
	
	/**
	 * 隐藏域和下拉列表框属性
	 */
    private String pos_id_value = "";
    private String org_id_value = "";
    private String dept_id_value = "";
    private String gather_id_value = "";
    /**
	 * 用来判断管理权限
	 */
    private String userid = "";
    /*
     * 用工类型
     * */
    private String gather_type;
    
    public HireManageForm() {
    	
    }
    
  
    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM()
    {
    	this.setOrg_id_value((String)this.getFormHM().get("org_id_value"));
    	this.setDept_id_value((String)this.getFormHM().get("dept_id_value"));
    	this.setPos_id_value((String)this.getFormHM().get("pos_id_value"));
    	this.setFlag((String)this.getFormHM().get("flag"));
    	this.setFlag_pos((String)this.getFormHM().get("flag_pos"));
        this.setZpgathervo((RecordVo)this.getFormHM().get("zpgathervo"));
        this.getZpgatherForm().setList((ArrayList)this.getFormHM().get("zpgatherlist"));
        this.setGatherPosvo((RecordVo)this.getFormHM().get("gatherPosvo"));
        this.getGatherPosForm().setList((ArrayList)this.getFormHM().get("gatherPoslist"));
        this.setFlag_mid((String)this.getFormHM().get("flag_mid"));
        this.setGather_id_value((String)this.getFormHM().get("gather_id_value"));
        this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
        this.setDeptparentcode((String)this.getFormHM().get("deptparentcode"));
        this.setPosparentcode((String)this.getFormHM().get("posparentcode"));
        this.setManagepriv((String)this.getFormHM().get("managepriv"));
        this.setUserid((String)this.getFormHM().get("userid"));
        /**重新定位到当前页*/
	    this.getZpgatherForm().getPagination().gotoPage(current_1);
	    this.getGatherPosForm().getPagination().gotoPage(current_2);
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
	    this.getFormHM().put("selectedzpgatherlist",(ArrayList)this.getZpgatherForm().getSelectedList());
        this.getFormHM().put("zpgathervo",this.getZpgathervo());
        this.getFormHM().put("selectedlist",(ArrayList)this.getGatherPosForm().getSelectedList());
        this.getFormHM().put("gatherPosvo",this.getGatherPosvo());
	    this.getFormHM().put("flag",this.getFlag());
	    this.getFormHM().put("flag_pos",this.getFlag_pos());
	    this.getFormHM().put("flag_mid",this.getFlag_mid());
	    this.getFormHM().put("org_id_value",this.getOrg_id_value());
	    this.getFormHM().put("dept_id_value",this.getDept_id_value());
	    this.getFormHM().put("pos_id_value",this.getPos_id_value());
	    this.getFormHM().put("gather_id_value",this.getGather_id_value());
	    this.getFormHM().put("gather_type",this.getGather_type());
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
         * 新建临时用工申请
         */
        if("/hire/staffreq/staffreqquery".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            this.setFlag("1");
            this.setFlag_mid("0");
            this.setGather_id_value("");
            this.getZpgathervo().clearValues();
            this.setOrg_id_value("");
            this.setDept_id_value("");
           
        }
        /**
         * 新建临时用工申请岗位
         */
        if("/hire/staffreq/staffreqadd".equals(arg0.getPath()) && arg1.getParameter("b_pos_add")!=null)
        {
            this.setFlag_pos("1");
            this.setPos_id_value("");
            this.getGatherPosvo().clearValues();
        }
        if("/hire/staffreq/add_pos".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(gatherPosForm.getPagination()!=null)
            {
            	if("1".equals(this.getFlag_pos()))
            		gatherPosForm.getPagination().lastPage();
            	current_2=gatherPosForm.getPagination().getCurrent(); 
            }
        }  
        /**
         * 编辑临时用工申请
         */
        if(("/hire/staffreq/staffreqadd".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null) ||("/hire/staffreq/view_request_pos".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null))
        {
            this.setFlag("0");
            this.setFlag_mid("0");
        }
        /**
         * 编辑临时用工申请岗位
         */
        if("/hire/staffreq/add_pos".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            this.setFlag_pos("0");
            if(gatherPosForm.getPagination()!=null)
            {            
            	current_2 = gatherPosForm.getPagination().getCurrent();    
            }
        }
        
        if("/hire/staffreq/staffreqquery".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(zpgatherForm.getPagination()!=null)
            {
            	current_1=zpgatherForm.getPagination().getCurrent();
            }
        }
        if("/hire/staffreq/staffreqadd".equals(arg0.getPath())&&(arg1.getParameter("b_pos_delete")!=null))
        {
            if(gatherPosForm.getPagination()!=null)
            {
            	current_2=gatherPosForm.getPagination().getCurrent();
            }
        }
        /**
         * 编辑临时用工申请，编辑临时用工申请岗位
         */
        if("/hire/staffreq/staffreqadd".equals(arg0.getPath()) && arg1.getParameter("b_save")!=null)
        {
            this.setFlag_mid("1");
        }
        /**
         * 提交临时用工申请岗位
         */
        if("/hire/staffreq/staffreqadd".equals(arg0.getPath()) && arg1.getParameter("b_submit")!=null)
        {
        	if(zpgatherForm.getPagination()!=null)
            {
        		current_1=zpgatherForm.getPagination().getCurrent();
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
	public PaginationForm getGatherPosForm() {
		return gatherPosForm;
	}
	/**
	 * @param gatherPosForm The gatherPosForm to set.
	 */
	public void setGatherPosForm(PaginationForm gatherPosForm) {
		this.gatherPosForm = gatherPosForm;
	}
	/**
	 * @return Returns the gatherPosvo.
	 */
	public RecordVo getGatherPosvo() {
		return gatherPosvo;
	}
	/**
	 * @param gatherPosvo The gatherPosvo to set.
	 */
	public void setGatherPosvo(RecordVo gatherPosvo) {
		this.gatherPosvo = gatherPosvo;
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
	 * @return Returns the flag_pos.
	 */
	public String getFlag_pos() {
		return flag_pos;
	}
	/**
	 * @param flag_pos The flag_pos to set.
	 */
	public void setFlag_pos(String flag_pos) {
		this.flag_pos = flag_pos;
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
	 * @return Returns the gather_id_value.
	 */
	public String getGather_id_value() {
		return gather_id_value;
	}
	/**
	 * @param gather_id_value The gather_id_value to set.
	 */
	public void setGather_id_value(String gather_id_value) {
		this.gather_id_value = gather_id_value;
	}
	/**
	 * @return Returns the userAdmin.
	 */
	public String getUserAdmin() {
		return userAdmin;
	}
	/**
	 * @param userAdmin The userAdmin to set.
	 */
	public void setUserAdmin(String userAdmin) {
		this.userAdmin = userAdmin;
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
	/**
	 * @return Returns the gather_type.
	 */
	public String getGather_type() {
		return gather_type;
	}
	/**
	 * @param gather_type The gather_type to set.
	 */
	public void setGather_type(String gather_type) {
		this.gather_type = gather_type;
	}
	
}
