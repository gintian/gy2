package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

public class AdAuthenticateForm extends FrameForm {
	private String domain_name;
	private String domain;
	private String host;
	private String port;
	private String username;
	private String userpassword;
	private String ldaptype;
    private String ldapset;
	public String getLdaptype() {
		return ldaptype;
	}

	public void setLdaptype(String ldaptype) {
		this.ldaptype = ldaptype;
	}

	public String getLdapset() {
		return ldapset;
	}

	public void setLdapset(String ldapset) {
		this.ldapset = ldapset;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setDomain((String)this.getFormHM().get("domain"));
		this.setDomain_name((String)this.getFormHM().get("domain_name"));
		this.setHost((String)this.getFormHM().get("host"));
		this.setPort((String)this.getFormHM().get("port"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setUserpassword((String)this.getFormHM().get("userpassword"));
		this.setLdaptype((String)this.getFormHM().get("ldaptype"));
        this.setLdapset((String)this.getFormHM().get("ldapset"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("domain_name",domain_name);
		this.getFormHM().put("domain",domain);
		this.getFormHM().put("host",host);
		this.getFormHM().put("port",port);
		this.getFormHM().put("username",username);
		this.getFormHM().put("userpassword",userpassword);
		this.getFormHM().put("ldaptype", ldaptype);
		this.getFormHM().put("ldapset", ldapset);
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
	{
		this.getFormHM().put("servletRequest",arg1);
	}
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain_name() {
		return domain_name;
	}

	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpassword() {
		return userpassword;
	}

	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}

}
