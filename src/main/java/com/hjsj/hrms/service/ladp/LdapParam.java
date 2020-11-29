/**
 * 
 */
package com.hjsj.hrms.service.ladp;

import java.util.HashMap;

/**
 * @author Administrator
 *
 */
public class LdapParam {
	
	//操作标志,1新增，2更新，3删除，0已同步
	public static final int ADD = 1;
	public static final int UPDATA = 2;
	public static final int DELETE = 3;
	public static final int SYNC = 0;
	
	
	// 是否需要修改密码，0为不需要，1为需要
	private String modifyPwd = "0";
	
	// 需要修改密码时，是否采用默认密码,1为采用默认密码，0为采用hr系统的密码
	private String defaultPwd = "0";
	
	// 采用默认密码时，密码的值是什么
	private String defaultPwdValue = "123456";
	
	// 系统代号
	private String ldapCode = "ldap";
	
	// ldap服务器ip地址
	private String ldapIpAddress = "";
	
	// ldap服务器端口
	private String ldapPort = "389";
	
	// ldap服务器用户名
	private String ldapAccount = "";
	
	// ldap服务器密码
	private String ldapPassword = "";
	
	// ldap服务器同步根节点
	private String ldapRoot = "";
	
	// ssl证书路径
	private String certficationPath = "";
	
	// 证书导入密码
	private String certficationPwd = "";
	
	//同步的顶级机构
	private String rootorg = "";
	
	// 是否需要同步人员,true表示需要同步人员，false表示不需要同步人员
	private String syncHr = "";
	
	// type属性，1为以用户名为人员唯一指标对应，2为以unique_id为人员唯一指标对应；3为以工号或其他字段为人员唯一指标对应，
	private String hrType = "";
	
	//hrpk属性值为hr系统人员唯一对应指标，adpk属性值为Ad服务器人员唯一对应指标
	private String hrPk = "";
	
	private String hrAdPk = "";
	
	// 是否需要同步机构，true表示需要同步机构，false表示不需要同步机构
	private String syncOrg = "";
	
	// type属性，1为以HR系统机构编码为唯一指标对应，2以unique_id为机构唯一指标对应，3为其他字段为机构唯一指标对应，
	private String orgType = "";
	
	//定义不同步机构条件，但其子机构如不是控制条件机构还同步，同步后挂到上上级机构下（条件只能为为organization字段和b01中指标） 如：b01d3=1 or codeitemdesc like '%合并%'
	private String orgNosycnode;
	
	// hrpk属性值为hr系统机构唯一对应指标，adpk属性值为Ad服务器机构唯一对应指标
	private String orgPk = "";
	private String orgAdPk = "";
	
	// 是否需要同步岗位,true表示需要同步岗位，false表示不需要同步岗位
	private String syncPost = "";
	
	// type属性，1为以HR系统岗位编码为唯一指标对应，2以unique_id为岗位唯一指标对应，3为其他字段为岗位唯一指标对应，
	private String postType = "";
	
	// hrpk属性值为hr系统岗位唯一对应指标，adpk属性值为Ad服务器岗位唯一对应指标
	private String postPk = "";
	private String postAdPk = "";
	
	// 用户控制,启用：512，禁用：514， 密码永不过期：66048
	private String userControl = "512";
	
	// 是否真删除，只针对人员有效，1为真删除，0为禁用
	private String userDel = "1";
	
	// 禁用用户存放路径
	private String delUserPath = "";
	
	//为假删除时，禁用路径是否在当前节点下
	private String isinner = "";
	
	// 人员字段对应
	private HashMap hrFieldMap = new HashMap();
	// 机构字段对应
	private HashMap orgFieldMap = new HashMap();
	// 岗位字段对应
	private HashMap postFieldMap = new HashMap();
	
	//人员同步条件
	private String hrcondition = "";
	//机构同步条件
	private String orgcondition = "";
	//岗位同步条件
	private String postcondition = "";
	
	//人员转码指标
	private String hrtranscoding = "";
	
	//机构转码指标
	private String orgtranscoding = "";
	
	// 最大同步条数，达到这个同步条数后，本次不再同步，解决因为时间过长导致同步失败的问题（华宇工程出现此问题）
	// 王中君修改  2012-08-25
	private int countNum = -1;
	
	public int getCountNum() {
		return countNum;
	}

	public void setCountNum(int countNum) {
		this.countNum = countNum;
	}

	public String getHrcondition() {
		return hrcondition;
	}

	public void setHrcondition(String hrcondition) {
		this.hrcondition = hrcondition;
	}

	public String getOrgcondition() {
		return orgcondition;
	}

	public void setOrgcondition(String orgcondition) {
		this.orgcondition = orgcondition;
	}

	public String getPostcondition() {
		return postcondition;
	}

	public void setPostcondition(String postcondition) {
		this.postcondition = postcondition;
	}

	public String getHrtranscoding() {
		return hrtranscoding;
	}

	public void setHrtranscoding(String hrtranscoding) {
		this.hrtranscoding = hrtranscoding;
	}

	public String getOrgtranscoding() {
		return orgtranscoding;
	}

	public void setOrgtranscoding(String orgtranscoding) {
		this.orgtranscoding = orgtranscoding;
	}

	public String getPosttranscoding() {
		return posttranscoding;
	}

	public void setPosttranscoding(String posttranscoding) {
		this.posttranscoding = posttranscoding;
	}

	//岗位转码指标
	private String posttranscoding = "";

	public String getIsinner() {
		return isinner;
	}
	
	public void setIsinner(String isinner) {
		this.isinner = isinner;
	}
	
	public String getModifyPwd() {
		return modifyPwd;
	}

	public void setModifyPwd(String modifyPwd) {
		this.modifyPwd = modifyPwd;
	}

	public String getDefaultPwd() {
		return defaultPwd;
	}

	public void setDefaultPwd(String defaultPwd) {
		this.defaultPwd = defaultPwd;
	}

	public String getDefaultPwdValue() {
		return defaultPwdValue;
	}

	public void setDefaultPwdValue(String defaultPwdValue) {
		this.defaultPwdValue = defaultPwdValue;
	}

	public String getLdapCode() {
		return ldapCode;
	}

	public void setLdapCode(String ldapCode) {
		this.ldapCode = ldapCode;
	}

	public String getLdapIpAddress() {
		return ldapIpAddress;
	}

	public void setLdapIpAddress(String ldapIpAddress) {
		this.ldapIpAddress = ldapIpAddress;
	}

	public String getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(String ldapPort) {
		this.ldapPort = ldapPort;
	}

	public String getLdapAccount() {
		return ldapAccount;
	}

	public void setLdapAccount(String ldapAccount) {
		this.ldapAccount = ldapAccount;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getLdapRoot() {
		return ldapRoot;
	}

	public void setLdapRoot(String ldapRoot) {
		this.ldapRoot = ldapRoot;
	}

	public String getCertficationPath() {
		return certficationPath;
	}

	public void setCertficationPath(String certficationPath) {
		this.certficationPath = certficationPath;
	}

	public String getCertficationPwd() {
		return certficationPwd;
	}

	public void setCertficationPwd(String certficationPwd) {
		this.certficationPwd = certficationPwd;
	}

	public String getSyncHr() {
		return syncHr;
	}

	public void setSyncHr(String syncHr) {
		this.syncHr = syncHr;
	}

	public String getHrType() {
		return hrType;
	}

	public void setHrType(String hrType) {
		this.hrType = hrType;
	}

	public String getHrPk() {
		return hrPk;
	}

	public void setHrPk(String hrPk) {
		this.hrPk = hrPk;
	}

	public String getHrAdPk() {
		return hrAdPk;
	}

	public void setHrAdPk(String hrAdPk) {
		this.hrAdPk = hrAdPk;
	}

	public String getSyncOrg() {
		return syncOrg;
	}

	public void setSyncOrg(String syncOrg) {
		this.syncOrg = syncOrg;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getOrgPk() {
		return orgPk;
	}

	public void setOrgPk(String orgPk) {
		this.orgPk = orgPk;
	}

	public String getOrgAdPk() {
		return orgAdPk;
	}

	public void setOrgAdPk(String orgAdPk) {
		this.orgAdPk = orgAdPk;
	}

	public String getSyncPost() {
		return syncPost;
	}

	public void setSyncPost(String syncPost) {
		this.syncPost = syncPost;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getPostPk() {
		return postPk;
	}

	public void setPostPk(String postPk) {
		this.postPk = postPk;
	}

	public String getPostAdPk() {
		return postAdPk;
	}

	public void setPostAdPk(String postAdPk) {
		this.postAdPk = postAdPk;
	}

	public HashMap getHrFieldMap() {
		return hrFieldMap;
	}

	public void setHrFieldMap(HashMap hrFieldMap) {
		this.hrFieldMap = hrFieldMap;
	}

	public String getUserControl() {
		return userControl;
	}

	public void setUserControl(String userControl) {
		this.userControl = userControl;
	}

	public HashMap getOrgFieldMap() {
		return orgFieldMap;
	}

	public void setOrgFieldMap(HashMap orgFieldMap) {
		this.orgFieldMap = orgFieldMap;
	}

	public HashMap getPostFieldMap() {
		return postFieldMap;
	}

	public void setPostFieldMap(HashMap postFieldMap) {
		this.postFieldMap = postFieldMap;
	}

	public String getUserDel() {
		return userDel;
	}

	public void setUserDel(String userDel) {
		this.userDel = userDel;
	}

	public String getOrgNosycnode() {
		return orgNosycnode;
	}

	public void setOrgNosycnode(String orgNosycnode) {
		this.orgNosycnode = orgNosycnode;
	}

	public String getDelUserPath() {
		return delUserPath;
	}

	public void setDelUserPath(String delUserPath) {
		this.delUserPath = delUserPath;
	}

	public String getRootorg() {
		return rootorg;
	}

	public void setRootorg(String rootorg) {
		this.rootorg = rootorg;
	}
	
	
	
}
