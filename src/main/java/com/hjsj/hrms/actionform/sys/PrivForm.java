package com.hjsj.hrms.actionform.sys;


import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:权限表单</p>
 * <p>Description:主要用于保存权限分配表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 9, 2005:3:36:37 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PrivForm extends FrameForm {
    /**
     * 当前功能页标识，功能授权
     */
    private String current_tab="funcpriv";
    /**
     * 生成的html标识串
     */
    private String script_str;
    /**
     * 功能列表
     */
    private String[] func;
    /**
     * 角色号
     */
    private String role_id;

    //角色名 或者用户名
    private String role_name;

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    /**
     * 用户标志，标位role_id是用户还是的角色等其它的内容
     */
    private String user_flag;
    /**
     * 存放子集或指标权限串
     */
    private String field_set_str;
    
    /**管理范围代码UN0001*/
    private String org;
    
    /**管理范围标识，页签是否可见*/
    private String viewflag="0";
    /**角色属性*/
    private String rp="-1";
    /**功能树*/
    private String functree;
    
    private String tabtitle;


    
    private PaginationForm peggingListForm=new PaginationForm(); 
    private String flag;
    private String name;
    private String id;
    private ArrayList codelist=new ArrayList();
    private String privcode="";
    private String ishaveprivcode="";
    private String privcodename="";
	public String getTabtitle() {
		return tabtitle;
	}
	public void setTabtitle(String tabtitle) {
		this.tabtitle = tabtitle;
	}
	public String[] getFunc() {
        return func;
    }
    /**
     * @param func The func to set.
     */
    public void setFunc(String[] func) {
    	//System.out.println("func="+func.toString());
        this.func = func;
    }
    /**
     * @return Returns the script_str.
     */
    public String getScript_str() {
        return script_str;
    }
    /**
     * @param script_str The script_str to set.
     */
    public void setScript_str(String script_str) {
        this.script_str = script_str;
    }
    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
        this.setScript_str((String)this.getFormHM().get("script_str"));
        this.setRole_id((String)this.getFormHM().get("role_id"));
        this.setCurrent_tab((String)this.getFormHM().get("tab_name"));
        this.setUser_flag((String)this.getFormHM().get("user_flag"));
        this.setOrg((String)this.getFormHM().get("manage"));
        this.setViewflag((String)this.getFormHM().get("viewflag"));
        this.setFunctree((String)this.getFormHM().get("functree"));
        this.setTabtitle((String)this.getFormHM().get("tabtitle"));
        this.getPeggingListForm().setList((ArrayList)this.getFormHM().get("pegginglist"));
        this.setCodelist((ArrayList)this.getFormHM().get("codelist"));
        this.setPrivcode((String)this.getFormHM().get("privcode"));
        this.setIshaveprivcode((String)this.getFormHM().get("ishaveprivcode"));
        this.setPrivcodename((String)this.getFormHM().get("privcodename"));
        this.setRole_name((String)this.getFormHM().get("role_name"));
        
     }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("func",this.getFunc());
        this.getFormHM().put("role_id",this.getRole_id());
        this.getFormHM().put("tab_name",this.getCurrent_tab());
        this.getFormHM().put("user_flag",this.getUser_flag());
        this.getFormHM().put("field_set_str",this.getField_set_str());
        this.getFormHM().put("org",this.getOrg());
        EncryptLockClient lock=(EncryptLockClient)this.getServlet().getServletContext().getAttribute("lock");
        this.getFormHM().put("lock",lock);        
        this.getFormHM().put("id", id);
        this.getFormHM().put("flag", flag);
        this.getFormHM().put("privcode", this.getPrivcode());
        this.getFormHM().put("ishaveprivcode", this.getIshaveprivcode());
        this.getFormHM().put("role_name", this.getRole_name());
    }

    /**
     * @return Returns the current_tab.
     */
    public String getCurrent_tab() {
        return current_tab;
    }
    /**
     * @param current_tab The current_tab to set.
     */
    public void setCurrent_tab(String current_tab) {
        this.current_tab = current_tab;
    }
    /**
     * @return Returns the role_id.
     */
    public String getRole_id() {
        return role_id;
    }
    /**
     * @param role_id The role_id to set.
     */
    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }
    /**
     * @return Returns the field_set_str.
     */
    public String getField_set_str() {
        return field_set_str;
    }
    /**
     * @param field_set_str The field_set_str to set.
     */
    public void setField_set_str(String field_set_str) {
        this.field_set_str = field_set_str;
    }
    /**
     * @return Returns the org.
     */
    public String getOrg() {
        return org;
    }
    /**
     * @param org The org to set.
     */
    public void setOrg(String org) {
        this.org = org;
    }
    /**
     * @return Returns the user_flag.
     */
    public String getUser_flag() {
        return user_flag;
    }
    /**
     * @param user_flag The user_flag to set.
     */
    public void setUser_flag(String user_flag) {
        this.user_flag = user_flag;
    }
	public String getViewflag() {
		return viewflag;
	}
	public void setViewflag(String viewflag) {
		this.viewflag = viewflag;
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.func=new String[0];
		 if("/system/security/pegging".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	        {
	            if(peggingListForm.getPagination()!=null)
	            {            
	            	peggingListForm.getPagination().firstPage();   
	            }
	        } 
		
	}
	public String getFunctree() {
		return functree;
	}
	public void setFunctree(String functree) {
		this.functree = functree;
	}

	public void setRoleproperty(String roleproperty) {

	}
	public String getRp() {
		return rp;
	}
	public void setRp(String rp) {
		this.rp = rp;
		if("1".equalsIgnoreCase(rp)|| "5".equalsIgnoreCase(rp)|| "6".equalsIgnoreCase(rp)|| "7".equalsIgnoreCase(rp)){
			this.setViewflag("1");
		}
		else
		{
			this.setViewflag("0");
		}		
	}
	public PaginationForm getPeggingListForm() {
		return peggingListForm;
	}
	public void setPeggingListForm(PaginationForm peggingListForm) {
		this.peggingListForm = peggingListForm;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public ArrayList getCodelist() {
		return codelist;
	}
	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}
	public String getPrivcode() {
		return privcode;
	}
	public void setPrivcode(String privcode) {
		this.privcode = privcode;
	}
	public String getIshaveprivcode() {
		return ishaveprivcode;
	}
	public void setIshaveprivcode(String ishaveprivcode) {
		this.ishaveprivcode = ishaveprivcode;
	}
	public String getPrivcodename() {
		return privcodename;
	}
	public void setPrivcodename(String privcodename) {
		this.privcodename = privcodename;
	}
}
