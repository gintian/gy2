package com.hjsj.hrms.actionform.sys.options;

import com.hjsj.hrms.businessobject.sys.options.UserPopedom;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.action.FrameForm;

public class UserPopedomForm extends FrameForm {

	private UserPopedom up ;
	private String flag;
	
	private String role_id;
	/**
	 * 自助用户或账号
	 */
	private String name;
	/**
	 * 返回按钮参数   wangb 20171204 32858
	 */
	private String btnBack;
	
	public String getBtnBack() {
		return btnBack;
	}

	public void setBtnBack(String btnBack) {
		this.btnBack = btnBack;
	}

	public String getRole_id() {
		return role_id;
	}

	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	@Override
    public void outPutFormHM() {
		this.setUp((UserPopedom)this.getFormHM().get("up"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setBtnBack((String)this.getFormHM().get("btnBack"));
	}

	@Override
    public void inPutTransHM() {
		this.setUp(new UserPopedom());
		this.getFormHM().put("role_id",this.getRole_id());
		this.getFormHM().put("name",this.getName());
		EncryptLockClient lock=(EncryptLockClient)this.getServlet().getServletContext().getAttribute("lock");
        this.getFormHM().put("lock",lock); 
	}

	
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public UserPopedom getUp() {
		return up;
	}

	public void setUp(UserPopedom up) {
		this.up = up;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
