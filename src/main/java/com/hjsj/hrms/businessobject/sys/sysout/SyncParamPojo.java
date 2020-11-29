/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.sysout;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:SynToADService
 * </p>
 * <p>
 * Description:读取系统代号.xml中的参数，保存到此类中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-08-08
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SyncParamPojo {
	
	// 是否同步人员
	private boolean syncHR = false;
	
	// 是否同步机构
	private boolean syncOrg = false;
	
	// 是否同步岗位
	private boolean syncPost = false;
	
	// 是否同步变动日志
	private boolean isLog = false;
	
	// 人员同步条件
	private String hrCond = "";
	
	// 机构同步条件
	private String orgCond = "";
	
	// 岗位同步条件
	private String postCond = "";
	
	
	/** 同步人员的webservice信息 **/
	// webservice的用户名
	private String hr_webservice_username = "";	
	// webservice的密码
	private String hr_webservice_password = "";
	// webservice的url
	private String hr_webservice_url = "";
	// webservice的方法名称
	private String hr_webservice_function = "";
	// webservice的命名空间名称
	private String hr_webservice_namespace = "";
	// webservice的参数名称
	private String hr_webservice_paramname = "";
	// webservice 的style
	private String hr_webservice_style = "";
	// webservice 的前缀
	private String hr_webservice_prefix = "";
	// webservice 调用方式
	private String hr_webservice_type = "";
	// webservice soap字符串
	private String hr_webservice_soapstr = "";
	
	/** 同步机构的webservice信息 **/
	// webservice的用户名
	private String org_webservice_username = "";	
	// webservice的密码
	private String org_webservice_password = "";
	// webservice的url
	private String org_webservice_url = "";
	// webservice的方法名称
	private String org_webservice_function = "";
	// webservice的命名空间名称
	private String org_webservice_namespace = "";
	// webservice的参数名称
	private String org_webservice_paramname = "";
	// webservice 的style
	private String org_webservice_style = "";
	// webservice 的前缀
	private String org_webservice_prefix = "";
	// webservice 调用方式
	private String org_webservice_type = "";
	// webservice soap字符串
	private String org_webservice_soapstr = "";
	
	
	/** 同步岗位的webservice信息 **/
	// webservice的用户名
	private String post_webservice_username = "";	
	// webservice的密码
	private String post_webservice_password = "";
	// webservice的url
	private String post_webservice_url = "";
	// webservice的方法名称
	private String post_webservice_function = "";
	// webservice的命名空间名称
	private String post_webservice_namespace = "";
	// webservice的参数名称
	private String post_webservice_paramname = "";
	// webservice 的style
	private String post_webservice_style = "";
	// webservice 的前缀
	private String post_webservice_prefix = "";
	// webservice 调用方式
	private String post_webservice_type = "";
	// webservice soap字符串
	private String post_webservice_soapstr = "";
	
	
	public String getHr_webservice_soapstr() {
		return hr_webservice_soapstr;
	}
	public void setHr_webservice_soapstr(String hr_webservice_soapstr) {
		this.hr_webservice_soapstr = hr_webservice_soapstr;
	}
	public String getOrg_webservice_soapstr() {
		return org_webservice_soapstr;
	}
	public void setOrg_webservice_soapstr(String org_webservice_soapstr) {
		this.org_webservice_soapstr = org_webservice_soapstr;
	}
	public String getPost_webservice_soapstr() {
		return post_webservice_soapstr;
	}
	public void setPost_webservice_soapstr(String post_webservice_soapstr) {
		this.post_webservice_soapstr = post_webservice_soapstr;
	}
	public String getHr_webservice_prefix() {
		return hr_webservice_prefix;
	}
	public void setHr_webservice_prefix(String hrWebservicePrefix) {
		hr_webservice_prefix = hrWebservicePrefix;
	}
	public String getHr_webservice_type() {
		return hr_webservice_type;
	}
	public void setHr_webservice_type(String hrWebserviceType) {
		hr_webservice_type = hrWebserviceType;
	}
	public String getOrg_webservice_prefix() {
		return org_webservice_prefix;
	}
	public void setOrg_webservice_prefix(String orgWebservicePrefix) {
		org_webservice_prefix = orgWebservicePrefix;
	}
	public String getOrg_webservice_type() {
		return org_webservice_type;
	}
	public void setOrg_webservice_type(String orgWebserviceType) {
		org_webservice_type = orgWebserviceType;
	}
	public String getPost_webservice_prefix() {
		return post_webservice_prefix;
	}
	public void setPost_webservice_prefix(String postWebservicePrefix) {
		post_webservice_prefix = postWebservicePrefix;
	}
	public String getPost_webservice_type() {
		return post_webservice_type;
	}
	public void setPost_webservice_type(String postWebserviceType) {
		post_webservice_type = postWebserviceType;
	}
	public String getHr_webservice_style() {
		return hr_webservice_style;
	}
	public void setHr_webservice_style(String hrWebserviceStyle) {
		hr_webservice_style = hrWebserviceStyle;
	}
	public String getOrg_webservice_style() {
		return org_webservice_style;
	}
	public void setOrg_webservice_style(String orgWebserviceStyle) {
		org_webservice_style = orgWebserviceStyle;
	}
	public String getPost_webservice_style() {
		return post_webservice_style;
	}
	public void setPost_webservice_style(String postWebserviceStyle) {
		post_webservice_style = postWebserviceStyle;
	}
	// 机构主键在xml中的名称
	private String orgXmlKey = "";
	
	// 机构主键在数据库中的名称
	private String orgDbKey = "";
	
	// 人员主键在xml中的名称
	private String hrXmlKey = "";
	
	// 人员主键在数据库中的名称
	private String hrDbKey = "";
	
	// 岗位主键在xml中的名称
	private String postXmlKey = "";
	
	// 岗位主键在数据库中的名称
	private String postDbKey = "";
	
	
	// 机构记录标志在xml中的名称
	private String orgXmlFlag = "";
	
	// 机构记录标志在数据库中的名称
	private String orgDbFlag = "";
	
	// 人员记录标志在xml中的名称
	private String hrXmlFlag = "";
	
	// 人员记录标志在数据库中的名称
	private String hrDbFlag = "";
	
	// 岗位记录标志在xml中的名称
	private String postXmlFlag = "";
	
	// 岗位记录标志在数据库中的名称
	private String postDbFlag = "";
	
	// xml编码
	private String xmlcode = "GBK";
	
	// 每次发送的最大记录条数
	private int maxnum = 200;
	
	// 人员根节点名称
	private String hrRootName = "userinfo";
	// 机构根节点名称
	private String orgRootName = "orginfo";
	// 岗位根节点名称
	private String postRootName = "postinfo";
	
	//人员节点名称
	private String hrNodeName = "info";
	//机构节点名称
	private String orgNodeName = "info";
	//岗位节点名称
	private String postNodeName = "info";
	
	// 人员指标对应集合
	private Map hrFieldRefMap = new HashMap();
	// 机构指标对应集合
	private Map orgFieldRefMap = new HashMap();
	// 岗位指标对应集合
	private Map postFieldRefMap = new HashMap();
	
	// 人员固定内容父节点
	private String hrfixParent = "";
	// 人员固定内容
	private String hrfix = "";
	
	// 机构固定内容父节点
	private String orgfixParent = "";
	// 机构固定内容
	private String orgfix = "";
	
	// 岗位固定内容父节点
	private String postfixParent = "";
	// 岗位固定内容
	private String postfix = "";
	
	// 人员无论指标变化与否，都将显示此列，只针对发送指标变动前后信息时有效
	private String hrfixfield = "";
	
	// 机构无论指标变化与否，都将显示此列，只针对发送指标变动前后信息时有效
	private String orgfixfield = "";
	
	// 岗位无论指标变化与否，都将显示此列，只针对发送指标变动前后信息时有效
	private String postfixfield = "";
	
	// 人员这些指标变化 后，才发送变化前后信息，否则不发送
	private String hrfieldchange = "";
	
	// 机构这些指标变化 后，才发送变化前后信息，否则不发送
	private String orgfieldchange = "";
	
	// 岗位这些指标变化 后，才发送变化前后信息，否则不发送
	private String postfieldchange = "";
	
	private Map dateFormateField = new HashMap();
	
	
	public String getHrfieldchange() {
		return hrfieldchange;
	}
	public void setHrfieldchange(String hrfieldchange) {
		this.hrfieldchange = hrfieldchange;
	}
	public String getOrgfieldchange() {
		return orgfieldchange;
	}
	public void setOrgfieldchange(String orgfieldchange) {
		this.orgfieldchange = orgfieldchange;
	}
	public String getPostfieldchange() {
		return postfieldchange;
	}
	public void setPostfieldchange(String postfieldchange) {
		this.postfieldchange = postfieldchange;
	}
	public Map getDateFormateField() {
		return dateFormateField;
	}
	public void setDateFormateField(Map dateFormateField) {
		this.dateFormateField = dateFormateField;
	}
	public String getHrReturnRootName() {
		return hrReturnRootName;
	}
	public void setHrReturnRootName(String hrReturnRootName) {
		this.hrReturnRootName = hrReturnRootName;
	}
	public String getOrgReturnRootName() {
		return orgReturnRootName;
	}
	public void setOrgReturnRootName(String orgReturnRootName) {
		this.orgReturnRootName = orgReturnRootName;
	}
	public String getPostReturnRootName() {
		return postReturnRootName;
	}
	public void setPostReturnRootName(String postReturnRootName) {
		this.postReturnRootName = postReturnRootName;
	}
	public String getHrReturnNodeName() {
		return hrReturnNodeName;
	}
	public void setHrReturnNodeName(String hrReturnNodeName) {
		this.hrReturnNodeName = hrReturnNodeName;
	}
	public String getOrgReturnNodeName() {
		return orgReturnNodeName;
	}
	public void setOrgReturnNodeName(String orgReturnNodeName) {
		this.orgReturnNodeName = orgReturnNodeName;
	}
	public String getPostReturnNodeName() {
		return postReturnNodeName;
	}
	public void setPostReturnNodeName(String postReturnNodeName) {
		this.postReturnNodeName = postReturnNodeName;
	}
	public String getHrReturnKey() {
		return hrReturnKey;
	}
	public void setHrReturnKey(String hrReturnKey) {
		this.hrReturnKey = hrReturnKey;
	}
	public String getOrgReturnKey() {
		return orgReturnKey;
	}
	public void setOrgReturnKey(String orgReturnKey) {
		this.orgReturnKey = orgReturnKey;
	}
	public String getPostReturnKey() {
		return postReturnKey;
	}
	public void setPostReturnKey(String postReturnKey) {
		this.postReturnKey = postReturnKey;
	}
	public String getHrReturnHrFlag() {
		return hrReturnHrFlag;
	}
	public void setHrReturnHrFlag(String hrReturnHrFlag) {
		this.hrReturnHrFlag = hrReturnHrFlag;
	}
	public String getOrgReturnHrFlag() {
		return orgReturnHrFlag;
	}
	public void setOrgReturnHrFlag(String orgReturnHrFlag) {
		this.orgReturnHrFlag = orgReturnHrFlag;
	}
	public String getPostReturnHrFlag() {
		return postReturnHrFlag;
	}
	public void setPostReturnHrFlag(String postReturnHrFlag) {
		this.postReturnHrFlag = postReturnHrFlag;
	}
	public String getHrReturnFlag() {
		return hrReturnFlag;
	}
	public void setHrReturnFlag(String hrReturnFlag) {
		this.hrReturnFlag = hrReturnFlag;
	}
	public String getOrgReturnFlag() {
		return orgReturnFlag;
	}
	public void setOrgReturnFlag(String orgReturnFlag) {
		this.orgReturnFlag = orgReturnFlag;
	}
	public String getPostReturnFlag() {
		return postReturnFlag;
	}
	public void setPostReturnFlag(String postReturnFlag) {
		this.postReturnFlag = postReturnFlag;
	}
	// 人员返回值的根节点名称
	private String hrReturnRootName = "userinfo";
	// 机构返回值的根节点名称
	private String orgReturnRootName = "orginfo";
	// 岗位返回值的根节点名称
	private String postReturnRootName = "postinfo";
		
	// 人员返回值的节点名称
	private String hrReturnNodeName = "info";
	// 机构返回值的节点名称
	private String orgReturnNodeName = "info";
	// 岗位返回值的节点名称
	private String postReturnNodeName = "info";
	
	
	// 人员返回值的id
	private String hrReturnKey = "hr_id";
	// 机构返回值的id
	private String orgReturnKey = "org_id";
	// 岗位返回值的id
	private String postReturnKey = "post_id";
	
	// 人员返回值的记录标志
	private String hrReturnHrFlag = "hr_flag";
	// 机构返回值的记录标志
	private String orgReturnHrFlag = "hr_flag";
	// 岗位返回值的记录标志
	private String postReturnHrFlag = "hr_flag";
	
	// 人员返回值的是否操作成功
	private String hrReturnFlag = "flag";
	// 机构返回值的是否操作成功
	private String orgReturnFlag = "flag";
	// 岗位返回值的是否操作成功
	private String postReturnFlag = "flag";
	
	// 返回值中bom头的开始位置
	private int bomStart = 0;
	public int getBomStart() {
		return bomStart;
	}
	public void setBomStart(int bomStart) {
		this.bomStart = bomStart;
	}
	// 返回值中的bom头的结束位置
	private int bomEnd = 0;
	
	
	
		
	public int getBomEnd() {
		return bomEnd;
	}
	public void setBomEnd(int bomEnd) {
		this.bomEnd = bomEnd;
	}
	public boolean isSyncHR() {
		return syncHR;
	}
	public void setSyncHR(boolean syncHR) {
		this.syncHR = syncHR;
	}
	public boolean isSyncOrg() {
		return syncOrg;
	}
	public void setSyncOrg(boolean syncOrg) {
		this.syncOrg = syncOrg;
	}
	public boolean isSyncPost() {
		return syncPost;
	}
	public void setSyncPost(boolean syncPost) {
		this.syncPost = syncPost;
	}
	public boolean isLog() {
		return isLog;
	}
	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}
	public String getHrCond() {
		return hrCond;
	}
	public void setHrCond(String hrCond) {
		this.hrCond = hrCond;
	}
	public String getOrgCond() {
		return orgCond;
	}
	public void setOrgCond(String orgCond) {
		this.orgCond = orgCond;
	}
	public String getPostCond() {
		return postCond;
	}
	public void setPostCond(String postCond) {
		this.postCond = postCond;
	}
	public String getHr_webservice_username() {
		return hr_webservice_username;
	}
	public void setHr_webservice_username(String hrWebserviceUsername) {
		hr_webservice_username = hrWebserviceUsername;
	}
	public String getHr_webservice_password() {
		return hr_webservice_password;
	}
	public void setHr_webservice_password(String hrWebservicePassword) {
		hr_webservice_password = hrWebservicePassword;
	}
	public String getHr_webservice_url() {
		return hr_webservice_url;
	}
	public void setHr_webservice_url(String hrWebserviceUrl) {
		hr_webservice_url = hrWebserviceUrl;
	}
	public String getHr_webservice_function() {
		return hr_webservice_function;
	}
	public void setHr_webservice_function(String hrWebserviceFunction) {
		hr_webservice_function = hrWebserviceFunction;
	}
	public String getHr_webservice_namespace() {
		return hr_webservice_namespace;
	}
	public void setHr_webservice_namespace(String hrWebserviceNamespace) {
		hr_webservice_namespace = hrWebserviceNamespace;
	}
	public String getOrg_webservice_username() {
		return org_webservice_username;
	}
	public void setOrg_webservice_username(String orgWebserviceUsername) {
		org_webservice_username = orgWebserviceUsername;
	}
	public String getOrg_webservice_password() {
		return org_webservice_password;
	}
	public void setOrg_webservice_password(String orgWebservicePassword) {
		org_webservice_password = orgWebservicePassword;
	}
	public String getOrg_webservice_url() {
		return org_webservice_url;
	}
	public void setOrg_webservice_url(String orgWebserviceUrl) {
		org_webservice_url = orgWebserviceUrl;
	}
	public String getOrg_webservice_function() {
		return org_webservice_function;
	}
	public void setOrg_webservice_function(String orgWebserviceFunction) {
		org_webservice_function = orgWebserviceFunction;
	}
	public String getOrg_webservice_namespace() {
		return org_webservice_namespace;
	}
	public void setOrg_webservice_namespace(String orgWebserviceNamespace) {
		org_webservice_namespace = orgWebserviceNamespace;
	}
	public String getPost_webservice_username() {
		return post_webservice_username;
	}
	public void setPost_webservice_username(String postWebserviceUsername) {
		post_webservice_username = postWebserviceUsername;
	}
	public String getPost_webservice_password() {
		return post_webservice_password;
	}
	public void setPost_webservice_password(String postWebservicePassword) {
		post_webservice_password = postWebservicePassword;
	}
	public String getPost_webservice_url() {
		return post_webservice_url;
	}
	public void setPost_webservice_url(String postWebserviceUrl) {
		post_webservice_url = postWebserviceUrl;
	}
	public String getPost_webservice_function() {
		return post_webservice_function;
	}
	public void setPost_webservice_function(String postWebserviceFunction) {
		post_webservice_function = postWebserviceFunction;
	}
	public String getPost_webservice_namespace() {
		return post_webservice_namespace;
	}
	public void setPost_webservice_namespace(String postWebserviceNamespace) {
		post_webservice_namespace = postWebserviceNamespace;
	}
	public String getOrgXmlKey() {
		return orgXmlKey;
	}
	public void setOrgXmlKey(String orgXmlKey) {
		this.orgXmlKey = orgXmlKey;
	}
	public String getOrgDbKey() {
		return orgDbKey;
	}
	public void setOrgDbKey(String orgDbKey) {
		this.orgDbKey = orgDbKey;
	}
	public String getHrXmlKey() {
		return hrXmlKey;
	}
	public void setHrXmlKey(String hrXmlKey) {
		this.hrXmlKey = hrXmlKey;
	}
	public String getHrDbKey() {
		return hrDbKey;
	}
	public void setHrDbKey(String hrDbKey) {
		this.hrDbKey = hrDbKey;
	}
	public String getPostXmlKey() {
		return postXmlKey;
	}
	public void setPostXmlKey(String postXmlKey) {
		this.postXmlKey = postXmlKey;
	}
	public String getPostDbKey() {
		return postDbKey;
	}
	public void setPostDbKey(String postDbKey) {
		this.postDbKey = postDbKey;
	}
	public String getOrgXmlFlag() {
		return orgXmlFlag;
	}
	public void setOrgXmlFlag(String orgXmlFlag) {
		this.orgXmlFlag = orgXmlFlag;
	}
	public String getOrgDbFlag() {
		return orgDbFlag;
	}
	public void setOrgDbFlag(String orgDbFlag) {
		this.orgDbFlag = orgDbFlag;
	}
	public String getHrXmlFlag() {
		return hrXmlFlag;
	}
	public void setHrXmlFlag(String hrXmlFlag) {
		this.hrXmlFlag = hrXmlFlag;
	}
	public String getHrDbFlag() {
		return hrDbFlag;
	}
	public void setHrDbFlag(String hrDbFlag) {
		this.hrDbFlag = hrDbFlag;
	}
	public String getPostXmlFlag() {
		return postXmlFlag;
	}
	public void setPostXmlFlag(String postXmlFlag) {
		this.postXmlFlag = postXmlFlag;
	}
	public String getPostDbFlag() {
		return postDbFlag;
	}
	public void setPostDbFlag(String postDbFlag) {
		this.postDbFlag = postDbFlag;
	}
	public String getXmlcode() {
		return xmlcode;
	}
	public void setXmlcode(String xmlcode) {
		this.xmlcode = xmlcode;
	}
	public int getMaxnum() {
		return maxnum;
	}
	public void setMaxnum(int maxnum) {
		this.maxnum = maxnum;
	}
	public String getHrRootName() {
		return hrRootName;
	}
	public void setHrRootName(String hrRootName) {
		this.hrRootName = hrRootName;
	}
	public String getOrgRootName() {
		return orgRootName;
	}
	public void setOrgRootName(String orgRootName) {
		this.orgRootName = orgRootName;
	}
	public String getPostRootName() {
		return postRootName;
	}
	public void setPostRootName(String postRootName) {
		this.postRootName = postRootName;
	}
	public String getHrNodeName() {
		return hrNodeName;
	}
	public void setHrNodeName(String hrNodeName) {
		this.hrNodeName = hrNodeName;
	}
	public String getOrgNodeName() {
		return orgNodeName;
	}
	public void setOrgNodeName(String orgNodeName) {
		this.orgNodeName = orgNodeName;
	}
	public String getPostNodeName() {
		return postNodeName;
	}
	public void setPostNodeName(String postNodeName) {
		this.postNodeName = postNodeName;
	}
	public Map getHrFieldRefMap() {
		return hrFieldRefMap;
	}
	public void setHrFieldRefMap(Map hrFieldRefMap) {
		this.hrFieldRefMap = hrFieldRefMap;
	}
	public Map getOrgFieldRefMap() {
		return orgFieldRefMap;
	}
	public void setOrgFieldRefMap(Map orgFieldRefMap) {
		this.orgFieldRefMap = orgFieldRefMap;
	}
	public Map getPostFieldRefMap() {
		return postFieldRefMap;
	}
	public void setPostFieldRefMap(Map postFieldRefMap) {
		this.postFieldRefMap = postFieldRefMap;
	}
	public String getHr_webservice_paramname() {
		return hr_webservice_paramname;
	}
	public void setHr_webservice_paramname(String hrWebserviceParamname) {
		hr_webservice_paramname = hrWebserviceParamname;
	}
	public String getOrg_webservice_paramname() {
		return org_webservice_paramname;
	}
	public void setOrg_webservice_paramname(String orgWebserviceParamname) {
		org_webservice_paramname = orgWebserviceParamname;
	}
	public String getPost_webservice_paramname() {
		return post_webservice_paramname;
	}
	public void setPost_webservice_paramname(String postWebserviceParamname) {
		post_webservice_paramname = postWebserviceParamname;
	}
	public String getHrfixParent() {
		return hrfixParent;
	}
	public void setHrfixParent(String hrfixParent) {
		this.hrfixParent = hrfixParent;
	}
	public String getHrfix() {
		return hrfix;
	}
	public void setHrfix(String hrfix) {
		this.hrfix = hrfix;
	}
	public String getOrgfixParent() {
		return orgfixParent;
	}
	public void setOrgfixParent(String orgfixParent) {
		this.orgfixParent = orgfixParent;
	}
	public String getOrgfix() {
		return orgfix;
	}
	public void setOrgfix(String orgfix) {
		this.orgfix = orgfix;
	}
	public String getPostfixParent() {
		return postfixParent;
	}
	public void setPostfixParent(String postfixParent) {
		this.postfixParent = postfixParent;
	}
	public String getPostfix() {
		return postfix;
	}
	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}
	public String getHrfixfield() {
		return hrfixfield;
	}
	public void setHrfixfield(String hrfixfield) {
		this.hrfixfield = hrfixfield;
	}
	public String getOrgfixfield() {
		return orgfixfield;
	}
	public void setOrgfixfield(String orgfixfield) {
		this.orgfixfield = orgfixfield;
	}
	public String getPostfixfield() {
		return postfixfield;
	}
	public void setPostfixfield(String postfixfield) {
		this.postfixfield = postfixfield;
	}
	
	
	
}
