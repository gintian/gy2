/*
 * Created on 2005-10-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ZppersondbForm extends FrameForm {


	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private String actiontype;
	private String setname;
	private String a0100;
	private String i9999;
	private String userbase;
	private String okpassword;
	private String password;
	private String username;
	private PaginationForm zppersondbForm=new PaginationForm();   
	private String useraccount;
	private String usermaessage;
	private String existusermessage;
	private String usermaxlenth;
	private String filesort;  /*文件分类*/
	private String filetitle; /*文件标题*/
	private String isHandWork=""; //判断是否是后台手工添加用户信息  1：是
	/**
	 * @return Returns the userMaxlenth.
	 */
	public String getUsermaxlenth() {
		return usermaxlenth;
	}
	/**
	 * @param userMaxlenth The userMaxlenth to set.
	 */
	public void setUsermaxlenth(String usermaxlenth) {
		this.usermaxlenth = usermaxlenth;
	}
	/**
	 * @return Returns the existusermessage.
	 */
	public String getExistusermessage() {
		return existusermessage;
	}
	/**
	 * @param existusermessage The existusermessage to set.
	 */
	public void setExistusermessage(String existusermessage) {
		this.existusermessage = existusermessage;
	}
	/**
	 * @return Returns the usermaessage.
	 */
	public String getUsermaessage() {
		return usermaessage;
	}
	/**
	 * @param usermaessage The usermaessage to set.
	 */
	public void setUsermaessage(String usermaessage) {
		this.usermaessage = usermaessage;
	}
	/**
	 * @return Returns the picturefile.
	 */
	public FormFile getPicturefile() {
		return picturefile;
	}
	/**
	 * @param picturefile The picturefile to set.
	 */
	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}
	private FormFile picturefile;
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	private ArrayList zpfieldlist=new ArrayList();
	private ArrayList zpsetlist=new ArrayList();
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	/**
	 * @return Returns the actiontype.
	 */
	public String getActiontype() {
		return actiontype;
	}
	/**
	 * @param actiontype The actiontype to set.
	 */
	public void setActiontype(String actiontype) {
		this.actiontype = actiontype;
	}
	/**
	 * @return Returns the okpassword.
	 */
	public String getOkpassword() {
		return okpassword;
	}
	/**
	 * @param okpassword The okpassword to set.
	 */
	public void setOkpassword(String okpassword) {
		this.okpassword = okpassword;
	}
	/**
	 * @return Returns the i9999.
	 */
	public String getI9999() {
		return i9999;
	}
	/**
	 * @param i9999 The i9999 to set.
	 */
	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}
	/**
	 * @return Returns the setname.
	 */
	public String getSetname() {
		return setname;
	}
	/**
	 * @param setname The setname to set.
	 */
	public void setSetname(String setname) {
		this.setname = setname;
	}
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setZpfieldlist((ArrayList)this.getFormHM().get("zpfieldlist"));
		this.setZpsetlist((ArrayList)this.getFormHM().get("zpsetlist"));
		this.setUserbase((String)this.getFormHM().get("userbase"));
		this.setActiontype((String)this.getFormHM().get("actiontype"));
		this.getZppersondbForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setUseraccount((String)this.getFormHM().get("useraccount"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setOkpassword((String)this.getFormHM().get("okpassword"));
	    this.setUsername((String)this.getFormHM().get("username"));
	    this.setUsermaessage((String)this.getFormHM().get("usermessage"));
	    this.setExistusermessage((String)this.getFormHM().get("existusermessage"));
	    this.setUsermaxlenth((String)this.getFormHM().get("usermaxlenth"));
	    this.setIsHandWork((String)this.getFormHM().get("isHandWork"));

	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist",(ArrayList)this.getZppersondbForm().getSelectedList());
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("i9999",i9999);
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("actiontype",actiontype);
		this.getFormHM().put("picturefile",this.getPicturefile());
		this.getFormHM().put("okpassword",okpassword);
		this.getFormHM().put("username",username);
		this.getFormHM().put("password",password);
		this.getFormHM().put("filesort",this.getFilesort());
		this.getFormHM().put("filetitle",this.getFiletitle());
		this.getFormHM().put("userbase",this.getUserbase());
		

	}

	/**
	 * @return Returns the zpfieldlist.
	 */
	public ArrayList getZpfieldlist() {
		return zpfieldlist;
	}
	/**
	 * @param zpfieldlist The zpfieldlist to set.
	 */
	public void setZpfieldlist(ArrayList zpfieldlist) {
		this.zpfieldlist = zpfieldlist;
	}
	/**
	 * @return Returns the zpsetlist.
	 */
	public ArrayList getZpsetlist() {
		return zpsetlist;
	}
	/**
	 * @param zpsetlist The zpsetlist to set.
	 */
	public void setZpsetlist(ArrayList zpsetlist) {
		this.zpsetlist = zpsetlist;
	}
	/**
	 * @return Returns the zppersondbForm.
	 */
	public PaginationForm getZppersondbForm() {
		return zppersondbForm;
	}
	/**
	 * @param zppersondbForm The zppersondbForm to set.
	 */
	public void setZppersondbForm(PaginationForm zppersondbForm) {
		this.zppersondbForm = zppersondbForm;
	}
	/**
	 * @return Returns the useraccount.
	 */
	public String getUseraccount() {
		return useraccount;
	}
	/**
	 * @param useraccount The useraccount to set.
	 */
	public void setUseraccount(String useraccount) {
		this.useraccount = useraccount;
	}
	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return Returns the filesort.
	 */
	public String getFilesort() {
		return filesort;
	}
	/**
	 * @param filesort The filesort to set.
	 */
	public void setFilesort(String filesort) {
		this.filesort = filesort;
	}
	/**
	 * @return Returns the filetitle.
	 */
	public String getFiletitle() {
		return filetitle;
	}
	/**
	 * @param filetitle The filetitle to set.
	 */
	public void setFiletitle(String filetitle) {
		this.filetitle = filetitle;
	}
	public String getIsHandWork() {
		return isHandWork;
	}
	public void setIsHandWork(String isHandWork) {
		this.isHandWork = isHandWork;
	}
}
