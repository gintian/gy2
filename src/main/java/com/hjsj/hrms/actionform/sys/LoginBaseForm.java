package com.hjsj.hrms.actionform.sys;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:LoginBaseForm</p>
 * <p>Description:登录用户表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 21, 2005:5:01:37 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class LoginBaseForm extends FrameForm {
    /**人员库前缀列表*/
    private ArrayList dblist=new ArrayList();
    /**选中的库前缀列表*/
    private ArrayList selectedlist=new ArrayList();
    /**选中的库*/
    private String dbArr[];      
    /**用户名及口令指标*/
    private String username;
    private String password;
    /**口令加密解密标识*/
    private String flag;
    /**ip指标**/
    private String ip_addr;
    /**帐号锁定指标 */
    private String lockfield;
    public String getLockfield() {
		return lockfield;
	}

	public void setLockfield(String lockfield) {
		this.lockfield = lockfield;
	}

	public String getIp_addr() {
		return ip_addr;
	}

	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}

	/**
     * 
     */
    public LoginBaseForm() {
        super();
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
        this.setDblist((ArrayList)this.getFormHM().get("dblist"));
        this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
        this.setUsername((String)this.getFormHM().get("username"));
        this.setPassword((String)this.getFormHM().get("password"));
        this.setFlag((String)this.getFormHM().get("flag"));
        this.setIp_addr((String)this.getFormHM().get("ip_addr"));
        this.setLockfield((String)this.getFormHM().get("lockfield"));
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("dbArr",this.getDbArr());
        this.getFormHM().put("dblist",this.getDblist());
        this.getFormHM().put("selectedlist",this.getSelectedlist());
        this.getFormHM().put("username",this.getUsername());
        this.getFormHM().put("password",this.getPassword());
        this.getFormHM().put("ip_addr",this.getIp_addr());
        this.getFormHM().put("lockfield", lockfield);
    }

    public ArrayList getDblist() {
        return dblist;
    }
    public void setDblist(ArrayList dblist) {
        this.dblist = dblist;
    }
    public ArrayList getSelectedlist() {
        return selectedlist;
    }
    public void setSelectedlist(ArrayList selectedlist) {
        this.selectedlist = selectedlist;
    }
    public String[] getDbArr() {
        return dbArr;
    }
    public void setDbArr(String[] dbArr) {
        this.dbArr = dbArr;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.dbArr=new String[0];
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
}
