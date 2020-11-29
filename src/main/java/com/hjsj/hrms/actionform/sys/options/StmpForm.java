/**
 * 
 */
package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title:StmpForm</p>
 * <p>Description:邮件发送服务器配置参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-13:14:30:43</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class StmpForm extends FrameForm {

	/**邮件发送服务器*/
	private String stmp_addr;
	/**用户名*/
	private String log_user;
	/**口令*/
	private String log_pwd;
	/**确认口令*/
	private String log_repwd;
	/**身份认证*/
	private String authy="1";
	/**系统发送邮件的地址*/
	private String from_addr;
	/**最大发送量*/
	private String maxsend;
	/**邮件发送端口，默认25*/
	private String port;
	/**发件人昵称*/
	private String sendername;

	private String encryption;
	@Override
    public void outPutFormHM() {
		this.setPort((String)this.getFormHM().get("port"));
		this.setStmp_addr((String)this.getFormHM().get("stmp_addr"));
		this.setLog_user((String)this.getFormHM().get("log_user"));
		this.setLog_pwd((String)this.getFormHM().get("log_pwd"));
		this.setLog_repwd((String)this.getFormHM().get("log_repwd"));//添加 确认密码 属性防止前台报错   wangb 20170916  31639
		this.setAuthy((String)this.getFormHM().get("authy"));
		this.setFrom_addr((String)this.getFormHM().get("from_addr"));
		this.setMaxsend((String)this.getFormHM().get("maxsend"));
		this.setEncryption((String)this.getFormHM().get("encryption"));
		this.setSendername((String)this.getFormHM().get("sendername"));

	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("port", this.getPort());
		this.getFormHM().put("stmp_addr",this.stmp_addr);
		this.getFormHM().put("log_user",this.log_user);
		this.getFormHM().put("log_pwd",this.log_pwd);
		this.getFormHM().put("log_repwd",this.log_repwd);//添加 确认密码属性 防止前台报错  wangb 20170916  31639
		this.getFormHM().put("authy",this.authy);
		this.getFormHM().put("from_addr",this.from_addr);
		this.getFormHM().put("maxsend",this.maxsend);
		this.getFormHM().put("encryption",this.encryption);
		this.getFormHM().put("sendername",this.getSendername());
	}

	public String getAuthy() {
		return authy;
	}

	public void setAuthy(String authy) {
		this.authy = authy;
	}

	public String getLog_pwd() {
		return log_pwd;
	}

	public void setLog_pwd(String log_pwd) {
		this.log_pwd = log_pwd;
	}

	public String getLog_user() {
		return log_user;
	}

	public void setLog_user(String log_user) {
		this.log_user = log_user;
	}

	public String getStmp_addr() {
		return stmp_addr;
	}

	public void setStmp_addr(String stmp_addr) {
		this.stmp_addr = stmp_addr;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setAuthy("0");
		super.reset(arg0, arg1);
	}

	public String getFrom_addr() {
		return from_addr;
	}

	public void setFrom_addr(String from_addr) {
		this.from_addr = from_addr;
	}

	public String getMaxsend() {
		return maxsend;
	}

	public void setMaxsend(String maxsend) {
		this.maxsend = maxsend;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public String getLog_repwd() {
		return log_repwd;
	}

	public void setLog_repwd(String log_repwd) {
		this.log_repwd = log_repwd;
	}
	public String getSendername() {
		return sendername;
	}

	public void setSendername(String sendername) {
		this.sendername = sendername;
	}

}
