/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
/**
 * @author chenmengqing
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SysForm extends FrameForm {

    /* 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
	private String agentId;
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
         * 新建角色
         */
    	if("/system/security/rolesearch".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null)
        {
            this.getRolevo().clearValues();            
            this.setFlag("1");
        } 
    	/**
    	 * 修改角色
    	 */
        if("/system/security/viewrole".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
        }
        
        if("/system/security/viewrole".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(roleListForm.getPagination()!=null)
            {
            	if("1".equals(this.flag))
            		roleListForm.getPagination().lastPage();
                current=roleListForm.getPagination().getCurrent(); 
            }
        }   
        if("/system/security/rolesearch".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(roleListForm.getPagination()!=null)
            {
                current=roleListForm.getPagination().getCurrent();
            }
        }         
                
        if("/system/security/rolesearch".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
            if(roleListForm.getPagination()!=null)
            {            
            	current=roleListForm.getPagination().getCurrent();    
            }
        }        
        /**加密锁*/
        this.getFormHM().put("lock",arg1.getSession().getServletContext().getAttribute("lock"));
        if("/templates/menu/kq_employee_menu".equals(arg0.getPath())&&arg1.getParameter("sign")!=null)
        {
        	String sign=(String)arg1.getParameter("sign");        	
    	    if(sign!=null&&("in".equalsIgnoreCase(sign)||sign!=null&& "out".equalsIgnoreCase(sign)))
    	    {
    	    	arg1.getSession().getServletContext().setAttribute("sign",sign);
    	    }else 
    	    {
    	    	arg1.getSession().getServletContext().setAttribute("sign","");
    	    }
        }
        /**代理人**/
        if("/selfservice/selfinfo/agent/agent".equals(arg0.getPath())&&(arg1.getParameter("b_agent")!=null||arg1.getParameter("b_agent_hcm")!=null))
        {
        	this.getFormHM().put("session",arg1.getSession());
        }
        if("/selfservice/selfinfo/agent/agent4".equals(arg0.getPath())&&arg1.getParameter("b_agent")!=null)
        {
        	this.getFormHM().put("session",arg1.getSession());
        }
        if(roleListForm.getPagination()!=null)
        {
            current=roleListForm.getPagination().getCurrent();
        }
        return super.validate(arg0, arg1);
    }
    /**
     * 新增或编辑控制符，０００=0编辑，=1新增
     */
    private String flag="0";
    /**当前页*/
    private int current=1;
    /**模块标志*/
    private String module;
    
    private String[] module_ctrl={"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
    /** 业务日期*/
    private String appDate="";
    /**连接CS应用字符串*/
    private String cs_app_str;
    /**按并发处理，模块标识＝0没有权限,=1有权限*/
    private String license="0";
    /**CS应用模块控制*/
    private String cs_module="";
    private String inputchinfor;
    /** 人员信息是否需要审批     */
    private String approveflag;
    private String browse_photo;
    
    String fromUrl = "";//这两个属性用于在submainpanel中得到链接地址    郭峰  2012-1-26
	 String fromModid = "";
	 
	 String unit="";
    /**
     * 
     * 角色对象
     */
    private RecordVo rolevo=new RecordVo("T_SYS_ROLE");
    
    /**
     * 角色对象列表
     */
    private PaginationForm roleListForm=new PaginationForm(); 
  //【7105】角色管理中，按照角色特称排序后，调整顺序界面显示不对。 jingq upd 2015.01.29
    private ArrayList orderList = new ArrayList();
    
    public ArrayList getOrderList() {
		return orderList;
	}
	public void setOrderList(ArrayList orderList) {
		this.orderList = orderList;
	}
	/**
     * @return Returns the roleListForm.
     */
    public PaginationForm getRoleListForm() {
        return roleListForm;
    }
    /**
     * @param roleListForm The roleListForm to set.
     */
    public void setRoleListForm(PaginationForm roleListForm) {
        this.roleListForm = roleListForm;

    }
    
    private ArrayList propertylist=new ArrayList();    
    public ArrayList getPropertylist() {
		return propertylist;
	}
	public void setPropertylist(ArrayList propertylist) {
		this.propertylist = propertylist;
	}
	
	private List menuList = new ArrayList();
	public List getMenuList() {
		return menuList;
	}
	public void setMenuList(List menuList) {
		this.menuList = menuList;
	}
	/**
     * @return Returns the rolevo.
     */
    public RecordVo getRolevo() {
        return rolevo;
    }
    /**
     * @param rolevo The rolevo to set.
     */
    public void setRolevo(RecordVo rolevo) {
        this.rolevo = rolevo;

    }
    
    /**
     * 导入文件目录
     */
    private String dir;
    private FormFile importfile;
    private String info;
    private String returnInfo;
    private String other_name="";
    /**
     * 排序
     */
    private String order_name;
    private String order_type;
    private String role_list[];
    
    private String qname;
    private String qroleproperty;
    private String oqname;
    private String oqroleproperty;
    public String getOrder_name() {
		return order_name;
	}
	public void setOrder_name(String order_name) {
		this.order_name = order_name;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	/**
     * 表单对象输出对HTML
     */
	@Override
    public void outPutFormHM() {
	    this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
	    this.setRolevo((RecordVo)this.getFormHM().get("rolevo"));
	    /**重新定位到当前页*/
	    this.getRoleListForm().getPagination().gotoPage(current);	
	    this.setPropertylist((ArrayList)this.getFormHM().get("propertylist"));	   
	    this.setModule_ctrl((String[])this.getFormHM().get("module_ctrl"));
	    this.setAppDate((String)this.getFormHM().get("appDate"));
	    this.setCs_app_str((String)this.getFormHM().get("cs_app_str"));
	    this.setLicense((String)this.getFormHM().get("license"));
	    String sign=(String)this.getFormHM().get("sign");
	    this.setInputchinfor((String)this.getFormHM().get("inputchinfor"));
	    this.setApproveflag((String)this.getFormHM().get("approveflag"));
	    this.setBrowse_photo((String)this.getFormHM().get("browse_photo"));
	    this.setInfo((String)this.getFormHM().get("info"));
	    this.setReturnInfo((String)this.getFormHM().get("returnInfo"));
	    this.setRole_list((String[])this.getFormHM().get("role_list"));
	    this.setQname((String)this.getFormHM().get("qname"));
	    this.setQroleproperty((String)this.getFormHM().get("qroleproperty"));
	    this.setOqname((String)this.getFormHM().get("oqname"));
	    this.setOqroleproperty((String)this.getFormHM().get("oqroleproperty"));
	    
	    this.setFromUrl((String)this.getFormHM().get("fromUrl"));
	    this.setFromModid((String)this.getFormHM().get("fromModid"));
	    
	    this.setUnit((String)this.getFormHM().get("unit"));
	    this.setMenuList((List)this.getFormHM().get("menulist"));
	    this.setOrderList((ArrayList)this.getFormHM().get("orderList"));
	    
	    this.setOrder_name((String)this.getFormHM().get("order_name"));
	    this.setOrder_type((String)this.getFormHM().get("order_type"));
	}	

	/**表单对角参数输出到交易*/
	@Override
    public void inPutTransHM() {
	    /**选中的角色对象*/
		this.getFormHM().put("selectedlist",(ArrayList)this.getRoleListForm().getSelectedList());
	    this.getFormHM().put("rolevo",this.getRolevo());
	    this.getFormHM().put("flag",this.getFlag());
	    this.getFormHM().put("module",this.getModule());
	    this.getFormHM().put("module_ctrl",this.getModule_ctrl());
	    this.getFormHM().put("importfile",this.getImportfile());
	    this.getFormHM().put("dir",this.getDir());
	    this.getFormHM().put("order_type",this.getOrder_type());
	    this.getFormHM().put("order_name",this.getOrder_name());
	    this.getFormHM().put("role_list",this.getRole_list());
	    this.getFormHM().put("other_name", this.getOther_name());
	    this.getFormHM().put("agentId", this.getAgentId());
	    this.getFormHM().put("qname", qname);
	    this.getFormHM().put("qroleproperty", qroleproperty);
	    this.getFormHM().put("oqname", oqname);
	    this.getFormHM().put("oqroleproperty", oqroleproperty);
	    
	    this.getFormHM().put("fromUrl", this.getFromUrl());
	    this.getFormHM().put("fromModid", this.getFromModid());
	    
	    this.getFormHM().put("unit", this.getUnit());
	    this.getFormHM().put("orderList", this.getOrderList());
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
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String[] getModule_ctrl() {
		return module_ctrl;
	}
	public void setModule_ctrl(String[] module_ctrl) {
		this.module_ctrl = module_ctrl;
	}
	public String getCs_app_str() {
		return cs_app_str;
	}
	public void setCs_app_str(String cs_app_str) {
		this.cs_app_str = cs_app_str;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getCs_module() {
		return cs_module;
	}
	public void setCs_module(String cs_module) {
		this.cs_module = cs_module;
	}
	public String getApproveflag() {
		return approveflag;
	}
	public void setApproveflag(String approveflag) {
		this.approveflag = approveflag;
	}
	public String getInputchinfor() {
		return inputchinfor;
	}
	public void setInputchinfor(String inputchinfor) {
		this.inputchinfor = inputchinfor;
	}
	public String getBrowse_photo() {
		return browse_photo;
	}
	public void setBrowse_photo(String browse_photo) {
		this.browse_photo = browse_photo;
	}
	public FormFile getImportfile() {
		return importfile;
	}

	public void setImportfile(FormFile importfile) {
		this.importfile = importfile;
	}
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
	public String[] getRole_list() {
		return role_list;
	}
	public void setRole_list(String[] role_list) {
		this.role_list = role_list;
	}
	public String getOther_name() {
		return other_name;
	}
	public void setOther_name(String other_name) {
		this.other_name = other_name;
	}
	
	public String getAppDate() {
		return appDate;
	}
	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getQname() {
		return qname;
	}
	public void setQname(String qname) {
		this.qname = qname;
	}
	public String getQroleproperty() {
		return qroleproperty;
	}
	public void setQroleproperty(String qroleproperty) {
		this.qroleproperty = qroleproperty;
	}
	public String getOqname() {
		return oqname;
	}
	public void setOqname(String oqname) {
		this.oqname = oqname;
	}
	public String getOqroleproperty() {
		return oqroleproperty;
	}
	public void setOqroleproperty(String oqroleproperty) {
		this.oqroleproperty = oqroleproperty;
	}
	public String getFromUrl() {
		return fromUrl;
	}
	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}
	public String getFromModid() {
		return fromModid;
	}
	public void setFromModid(String fromModid) {
		this.fromModid = fromModid;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}
