package com.hjsj.hrms.actionform.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:LoginpwdForm</p>
 * <p>Description:用户修改口令</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 23, 2005:11:50:53 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class LoginpwdForm extends FrameForm {

    /**原口令*/
    private String oldpwd;
    /**新口令*/
    private String newpwd;
    /**口令确定*/
    private String newokpwd;
    /**用户列表分页*/
    private PaginationForm userListForm=new PaginationForm();
    /**口令长度*/
    private String pwdlen="20"; 
    
    public String getPwdlen() {
		return pwdlen;
	}
	public void setPwdlen(String pwdlen) {
		this.pwdlen = pwdlen;
	}
	public String getNewokpwd() {
        return newokpwd;
    }
    public void setNewokpwd(String newokpwd) {
        this.newokpwd = newokpwd;
    }
    public String getNewpwd() {
        return newpwd;
    }
    public void setNewpwd(String newpwd) {
        this.newpwd = newpwd;
    }
    public String getOldpwd() {
        return oldpwd;
    }
    public void setOldpwd(String oldpwd) {
        this.oldpwd = oldpwd;
    }
    /**
     * 
     */
    public LoginpwdForm() {
        super();
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
        this.getUserListForm().setList((ArrayList)this.getFormHM().get("userlist"));
        this.setOldpwd((String)this.getFormHM().get("oldpwd"));
        this.setNewokpwd("");
        this.setNewpwd("");
        this.setOldpwd("");
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("oldpwd",this.getOldpwd());
        this.getFormHM().put("newpwd",this.getNewpwd());
        this.getFormHM().put("newokpwd",this.getNewokpwd());
    }

    
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		if("/system/security/resetup_password".equals(mapping.getPath())&&request.getParameter("b_edit")==null&&request.getParameter("b_query")==null&&request.getParameter("b_save")==null)
        {
			UserView userView = (UserView)request.getSession().getAttribute("userView");
			if(0==userView.getStatus()){//业务
				this.setPwdlen("20");
			}else if(4==userView.getStatus()){//自助
				RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
				if(login_vo==null)
		        {
					this.setPwdlen("20");
		        }
		        else
		        {
		            String login_name = login_vo.getString("str_value").toLowerCase();
		            int idx=login_name.indexOf(",");
		            if(idx==-1)
		            {
		            	this.setPwdlen("20");
		            }
		            else
		            {
		                String password=login_name.substring(idx+1);  
		                if("#".equals(password)|| "".equals(password))
		                	this.setPwdlen("20");
		                else
		                {
		                	FieldItem item=DataDictionary.getFieldItem(password);
		                	this.getFormHM().put("pwdlen",Integer.toString(item.getItemlength()));                	
		                }
		            }
		        }
			}
        }else{
        	this.setPwdlen("20");
        }
	}
	public PaginationForm getUserListForm() {
        return userListForm;
    }
    public void setUserListForm(PaginationForm userListForm) {
        this.userListForm = userListForm;
    }
}
