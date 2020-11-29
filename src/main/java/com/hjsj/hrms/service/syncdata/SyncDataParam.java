package com.hjsj.hrms.service.syncdata;


import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SyncDataParam {	

/**
 * 以下参数由参数通过xml方式传入
 */	
	//同步系统的id，也就是需要同步的系统代号
	private String destSysId="";
	
	//是否同步机构
	private boolean isSyncOrg=false;
	
	//是否同步岗位
	private boolean isSyncPost=false;	
	
	//是否同步人员
	private boolean isSyncEmp=false;
	
	//机构的过滤条件
	private String orgWhereSql="";	
	
	//岗位的过滤条件
	private String postWhereSql="";	
	
	//人员的过滤条件
	private String empWhereSql="";	
	
/**
 * 以下参数由参数通过解析目标系统配置的xml获取， 如OA.xml
 */	
	
	//定义的个性发送类，
	private String sendClassName="";	
	
	//HR机构表的唯一性指标 默认为unique_id
	private String hrOrgUniqueFld="";	
	//对方系统的机构表的唯一性指标
	private String destOrgUniqueFld="";
	
	//HR岗位表的唯一性指标 默认为unique_id
	private String hrPostUniqueFld="";	
	//对方系统的岗位表的唯一性指标
	private String destPostUniqueFld="";
	
	//HR人员表的唯一性指标 默认为unique_id
	private String hrEmpUniqueFld="";	
	//对方系统的人员表的唯一性指标
	private String destEmpUniqueFld="";
	
	//机构指标对应关系	
	private ArrayList<FieldRefBean> orgFieldRefList=new ArrayList<FieldRefBean>();
	//岗位指标对应关系
	private ArrayList<FieldRefBean> postFieldRefList=new ArrayList<FieldRefBean>();
	//人员指标对应关系
	private ArrayList<FieldRefBean> empFieldRefList=new ArrayList<FieldRefBean>();
	private SyncDataUtil syncDataUtil = new SyncDataUtil();
	
	private Boolean isComplete = false;
	
	
	public Boolean getIsComplete() {
		return isComplete;
	}


	public void setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
	}


	public  SyncDataParam(String xmlMessage) {
		LazyDynaBean bean = syncDataUtil.parseXml(xmlMessage);
		this.setDestSysId((String) bean.get("sysid"));
		this.setEmpWhereSql((String) bean.get("emp_where"));
		this.setOrgWhereSql((String) bean.get("org_where"));
		this.setPostWhereSql((String) bean.get("post_where"));
		// 添加kafka全量更新变量开始
		String kafka = (String)bean.get("kafka")==null?"":(String)bean.get("kafka");
		if ("complete".equals(kafka)) {
			this.setIsComplete(true);
		}else {
			this.setIsComplete(false);
		}
		
		// 添加kafka全量更新变量结束
		String rec = (String) bean.get("rec");
		if(rec.contains("emp")){
			this.setSyncEmp(true);
		}
		if(rec.contains("org")){
			this.setSyncOrg(true);
		}
		if(rec.contains("post")){
			this.setSyncPost(true);
		}
		String fileName = this.getDestSysId()+".xml";
		
		//解析xml 转为java对象
		parserXml(fileName);
		
	}
	

	/**解析xml 转为java对象
	 * @param xmlData
	 */
	public void parserXml(String fileName)
	{
		try
		{
			File file = syncDataUtil.getFile(fileName);
			if (file == null) {
				throw GeneralExceptionHandler.Handle(new Exception(
						"未找到"+fileName));
			}
			SAXBuilder saxbuilder = new SAXBuilder();
			org.jdom.Document document = saxbuilder.build(file);
			String path = "/sync/params/sendclass";
			XPath xpath = XPath.newInstance(path);
			List paramslist = xpath.selectNodes(document);
			Element element = (Element) paramslist.get(0);
			this.setSendClassName(element.getAttributeValue("class"));
			path = "/sync/params/empkey";
			xpath = XPath.newInstance(path);
			paramslist = xpath.selectNodes(document);
			element = (Element) paramslist.get(0);
			this.setHrEmpUniqueFld(element.getAttributeValue("hrkey"));
			this.setDestEmpUniqueFld(element.getAttributeValue("destkey"));
			path = "/sync/params/orgkey";
			xpath = XPath.newInstance(path);
			paramslist = xpath.selectNodes(document);
			element = (Element) paramslist.get(0);
			this.setHrOrgUniqueFld(element.getAttributeValue("hrkey"));
			this.setDestOrgUniqueFld(element.getAttributeValue("destkey"));
			path = "/sync/params/orgkey";
			xpath = XPath.newInstance(path);
			paramslist = xpath.selectNodes(document);
			element = (Element) paramslist.get(0);
			this.setHrPostUniqueFld(element.getAttributeValue("hrkey"));
			this.setDestPostUniqueFld(element.getAttributeValue("destkey"));
			path = "/sync/fields_ref/empfield/field_ref";
			xpath = XPath.newInstance(path);
			paramslist = xpath.selectNodes(document);
			Iterator it = paramslist.iterator();
			while (it.hasNext()) {
				element = (Element) it.next();
				FieldRefBean fieldrefbean = new FieldRefBean();
				fieldrefbean.setHrField(element.getAttributeValue("hrfield"));
				fieldrefbean.setDestField(element.getAttributeValue("destfield"));
				fieldrefbean.setFlddesc(element.getAttributeValue("desc"));
				empFieldRefList.add(fieldrefbean);
			}
			path = "/sync/fields_ref/orgfield/field_ref";
			xpath = XPath.newInstance(path);
			paramslist = xpath.selectNodes(document);
			it = paramslist.iterator();
			while (it.hasNext()) {
				element = (Element) it.next();
				FieldRefBean fieldrefbean = new FieldRefBean();
				fieldrefbean.setHrField(element.getAttributeValue("hrfield"));
				fieldrefbean.setDestField(element.getAttributeValue("destfield"));
				fieldrefbean.setFlddesc(element.getAttributeValue("desc"));
				orgFieldRefList.add(fieldrefbean);
				
			}
			path = "/sync/fields_ref/postfield/field_ref";
			xpath = XPath.newInstance(path);
			paramslist = xpath.selectNodes(document);
			it = paramslist.iterator();
			while (it.hasNext()) {
				element = (Element) it.next();
				FieldRefBean fieldrefbean = new FieldRefBean();
				fieldrefbean.setHrField(element.getAttributeValue("hrfield"));
				fieldrefbean.setDestField(element.getAttributeValue("destfield"));
				fieldrefbean.setFlddesc(element.getAttributeValue("desc"));
				postFieldRefList.add(fieldrefbean);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	

	public String getDestSysId() {
		return destSysId;
	}


	public void setDestSysId(String destSysId) {
		this.destSysId = destSysId;
	}


	public boolean isSyncOrg() {
		return isSyncOrg;
	}


	public void setSyncOrg(boolean isSyncOrg) {
		this.isSyncOrg = isSyncOrg;
	}


	public boolean isSyncPost() {
		return isSyncPost;
	}


	public void setSyncPost(boolean isSyncPost) {
		this.isSyncPost = isSyncPost;
	}


	public boolean isSyncEmp() {
		return isSyncEmp;
	}


	public void setSyncEmp(boolean isSyncEmp) {
		this.isSyncEmp = isSyncEmp;
	}


	public String getOrgWhereSql() {
		return orgWhereSql;
	}


	public void setOrgWhereSql(String orgWhereSql) {
	    if(StringUtils.isBlank(orgWhereSql)){
	        this.orgWhereSql = " 1=1";
	    }else{
	        this.orgWhereSql = orgWhereSql;
	    }
	}


	public String getPostWhereSql() {
		return postWhereSql;
	}


	public void setPostWhereSql(String postWhereSql) {
	    if(StringUtils.isBlank(postWhereSql)){
	        this.postWhereSql = " 1=1";
	    }else{
	        this.postWhereSql = postWhereSql;
	    }
	}


	public String getEmpWhereSql() {
		return empWhereSql;
	}


	public void setEmpWhereSql(String empWhereSql) {
	    if(StringUtils.isBlank(empWhereSql)){
	        this.empWhereSql=" 1=1";
	    }else{
	        this.empWhereSql = empWhereSql;
	    }
		
	}

	public String getSendClassName() {
		return sendClassName;
	}


	public void setSendClassName(String sendClassName) {
		this.sendClassName = sendClassName;
	}

	public String getHrOrgUniqueFld() {
		return hrOrgUniqueFld;
	}


	public void setHrOrgUniqueFld(String hrOrgUniqueFld) {
		this.hrOrgUniqueFld = hrOrgUniqueFld;
	}


	public String getDestOrgUniqueFld() {
		return destOrgUniqueFld;
	}


	public void setDestOrgUniqueFld(String destOrgUniqueFld) {
		this.destOrgUniqueFld = destOrgUniqueFld;
	}


	public String getHrPostUniqueFld() {
		return hrPostUniqueFld;
	}


	public void setHrPostUniqueFld(String hrPostUniqueFld) {
		this.hrPostUniqueFld = hrPostUniqueFld;
	}


	public String getDestPostUniqueFld() {
		return destPostUniqueFld;
	}


	public void setDestPostUniqueFld(String destPostUniqueFld) {
		this.destPostUniqueFld = destPostUniqueFld;
	}


	public String getHrEmpUniqueFld() {
		return hrEmpUniqueFld;
	}


	public void setHrEmpUniqueFld(String hrEmpUniqueFld) {
		this.hrEmpUniqueFld = hrEmpUniqueFld;
	}


	public String getDestEmpUniqueFld() {
		return destEmpUniqueFld;
	}


	public void setDestEmpUniqueFld(String destEmpUniqueFld) {
		this.destEmpUniqueFld = destEmpUniqueFld;
	}


	public ArrayList<FieldRefBean> getOrgFieldRefList() {
		return orgFieldRefList;
	}


	public void setOrgFieldRefList(ArrayList<FieldRefBean> orgFieldRefList) {
		this.orgFieldRefList = orgFieldRefList;
	}


	public ArrayList<FieldRefBean> getPostFieldRefList() {
		return postFieldRefList;
	}


	public void setPostFieldRefList(ArrayList<FieldRefBean> postFieldRefList) {
		this.postFieldRefList = postFieldRefList;
	}


	public ArrayList<FieldRefBean> getEmpFieldRefList() {
		return empFieldRefList;
	}


	public void setEmpFieldRefList(ArrayList<FieldRefBean> empFieldRefList) {
		this.empFieldRefList = empFieldRefList;
	}
}
