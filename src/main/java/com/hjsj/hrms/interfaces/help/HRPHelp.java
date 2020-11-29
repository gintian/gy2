/**
 * 
 */
package com.hjsj.hrms.interfaces.help;

/**
 * <p>Title:HRPHelp.java</p>
 * <p>Description:帮助封装类</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 25, 2006:2:55:10 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class HRPHelp {
	
	private String help_id;			//帮助ID
	private String help_name;       //帮助名称
	private String help_url;        //帮助URL
	private String help_moduleflag; //帮助模块号
	
	/**
	 * 构造器
	 */
	public HRPHelp() {
		super();
	}

	public String getHelp_id() {
		return help_id;
	}

	public void setHelp_id(String help_id) {
		this.help_id = help_id;
	}

	public String getHelp_moduleflag() {
		return help_moduleflag;
	}

	public void setHelp_moduleflag(String help_moduleflag) {
		this.help_moduleflag = help_moduleflag;
	}

	public String getHelp_name() {
		return help_name;
	}

	public void setHelp_name(String help_name) {
		this.help_name = help_name;
	}

	public String getHelp_url() {
		return help_url;
	}

	public void setHelp_url(String help_url) {
		this.help_url = help_url;
	}

	
	
}
