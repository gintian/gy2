package com.hjsj.hrms.actionform.sys.setup;

import com.hrms.struts.action.FrameForm;

import java.util.HashMap;

public class ConfigSysForm extends FrameForm {
	/*
	 * 系统参数
	 */
	private String username;
	private String password;
	private String inflag;
	private String sst;
	private String hjserverurl;
	private String hjserverport;
	private String validateflag;
	private String scrollwelcome;
	private String dbtype;
	private String dbname;
	private String dburl;
	private String dbport;
	private String dbuser;
	private String dbpassword;
	private String password1="";
	/*
	 * 其他控制参数
	 */
	private String flag;
	private String selstr;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap formhm = this.getFormHM();
//		this.setSessiontime((String) formhm.get("sessiontime"));
		this.setHjserverport((String) formhm.get("hjserverport"));
		this.setHjserverurl((String) formhm.get("hjserverurl"));
		this.setValidateflag((String)formhm.get("validateflag"));
		this.setScrollwelcome((String)formhm.get("scrollwelcome"));
		this.setDbtype((String)formhm.get("dbtype"));
		this.setDburl((String)formhm.get("dburl"));
		this.setDbport((String)formhm.get("dbport"));
		this.setDbuser((String)formhm.get("dbuser"));
		this.setDbpassword((String)formhm.get("dbpassword"));
		this.setSelstr((String)formhm.get("selstr"));
		this.setInflag((String) formhm.get("inflag"));
		this.setDbname((String)formhm.get("dbname"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap formhm=this.getFormHM();
		formhm.put("sessiontime",this.getSst());
		formhm.put("hjserverport",this.getHjserverport());
		formhm.put("hjserverurl",this.getHjserverurl());
		formhm.put("validateflag",this.getValidateflag());
		formhm.put("scrollwelcome",this.getScrollwelcome());
		formhm.put("dbtype",this.getDbtype());
		formhm.put("dburl",this.getDburl());
		formhm.put("dbport",this.getDbport());
		formhm.put("dbuser",this.getDbuser());
		formhm.put("dbpassword",this.getDbpassword());
		formhm.put("flag",this.getFlag());
		formhm.put("username",this.getUsername());
		if(this.getPassword1()==null||this.getPassword1().length()<1){
			formhm.put("password",this.getPassword());
		}else{
			formhm.put("password",this.getPassword1());
		}
		formhm.put("dbname",this.getDbname());
		
	}

	public String getDbpassword() {
		return dbpassword;
	}

	public void setDbpassword(String dbpassword) {
		this.dbpassword = dbpassword;
	}

	public String getDbport() {
		return dbport;
	}

	public void setDbport(String dbport) {
		this.dbport = dbport;
	}

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public String getDburl() {
		return dburl;
	}

	public void setDburl(String dburl) {
		this.dburl = dburl;
	}

	public String getDbuser() {
		return dbuser;
	}

	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}

	public String getHjserverport() {
		return hjserverport;
	}

	public void setHjserverport(String hjserverport) {
		this.hjserverport = hjserverport;
	}

	public String getHjserverurl() {
		return hjserverurl;
	}

	public void setHjserverurl(String hjserverurl) {
		this.hjserverurl = hjserverurl;
	}

	

	public String getValidateflag() {
		return validateflag;
	}

	public void setValidateflag(String validateflag) {
		this.validateflag = validateflag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getSelstr() {
		return selstr;
	}

	public void setSelstr(String selstr) {
		this.selstr = selstr;
	}

	public String getSst() {
		return sst;
	}

	public void setSst(String sst) {
		this.sst = sst;
	}

	public String getInflag() {
		return inflag;
	}

	public void setInflag(String inflag) {
		this.inflag = inflag;
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

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getScrollwelcome() {
		return scrollwelcome;
	}

	public void setScrollwelcome(String scrollwelcome) {
		this.scrollwelcome = scrollwelcome;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

}
