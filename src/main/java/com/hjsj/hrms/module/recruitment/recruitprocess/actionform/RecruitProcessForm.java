package com.hjsj.hrms.module.recruitment.recruitprocess.actionform;

import com.hrms.struts.action.FrameForm;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class RecruitProcessForm extends FrameForm {

	private String sqlstr="";
	private ArrayList sqlcolumn = new ArrayList();
	private String z0301="";
	private String linkId="";
	private String next_linkId="";
	private String next_nodeId="";
	private ArrayList stageList = new ArrayList();//流程阶段信息集合
	private ArrayList projectList = new ArrayList();//查询方案集合
	private ArrayList operationList = new ArrayList();//操作方案集合
	private LazyDynaBean emailInfo = new LazyDynaBean();//邮件信息
	
	private LazyDynaBean infoBean = new LazyDynaBean();//备用信息
	private String nodeId = "";
	private String pageNum = "0";
	private String pageSize = "20";
	private boolean hasFlowLinkPriv = false; //是否有环节权限
	private String skipFlag="1"; //招聘环节是否必须顺序进行
	private ArrayList skiplist = new ArrayList();//可以操作的流程阶段集合
	ArrayList buttons;
	

	@Override
    public void outPutFormHM() {
		this.setPageNum((String)this.getFormHM().get("pageNum"));
		this.setPageSize((String)this.getFormHM().get("pageSize"));
		this.setLinkId((String)this.getFormHM().get("linkId")); 
		this.setNext_linkId((String)this.getFormHM().get("next_linkId"));
		this.setNext_nodeId((String)this.getFormHM().get("next_nodeId"));
		this.setZ0301((String)this.getFormHM().get("z0301"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setSqlcolumn((ArrayList)this.getFormHM().get("sqlcolumn"));
		this.setStageList((ArrayList)this.getFormHM().get("stageList"));
		this.setProjectList((ArrayList)this.getFormHM().get("projectList"));
		this.setOperationList((ArrayList)this.getFormHM().get("operationList"));
		this.setEmailInfo((LazyDynaBean)this.getFormHM().get("emailInfo"));
		this.setNodeId((String)this.getFormHM().get("nodeId"));
		this.setInfoBean((LazyDynaBean)this.getFormHM().get("infoBean"));
		this.setHasFlowLinkPriv((Boolean)this.getFormHM().get("hasFlowLinkPriv"));
		this.setSkipFlag((String)this.getFormHM().get("skipFlag"));
		this.setSkiplist((ArrayList)this.getFormHM().get("skiplist"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("sqlstr",sqlstr );
		this.getFormHM().put("sqlcolumn",sqlcolumn );
		this.getFormHM().put("z0301", z0301);
		this.getFormHM().put("linkId", linkId);
		this.getFormHM().put("stageList", stageList);
		this.getFormHM().put("projectList", projectList);
		this.getFormHM().put("operationList", operationList);
		this.getFormHM().put("emailInfo", emailInfo);
		this.getFormHM().put("nodeId", nodeId);
		this.getFormHM().put("infoBean", infoBean);
		this.getFormHM().put("pageNum",pageNum );
		this.getFormHM().put("pageSize",pageSize );
		this.getFormHM().put("hasFlowLinkPriv", hasFlowLinkPriv);
		this.getFormHM().put("skipFlag",skipFlag );
		this.getFormHM().put("skiplist",skiplist );
	}

	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getZ0301() {
		return z0301;
	}
	public void setZ0301(String z0301) {
		this.z0301 = z0301;
	}
	public ArrayList getStageList() {
		return stageList;
	}
	
	public void setStageList(ArrayList stageList) {
		this.stageList = stageList;
	}

	public ArrayList getSqlcolumn() {
		return sqlcolumn;
	}

	public void setSqlcolumn(ArrayList sqlcolumn) {
		this.sqlcolumn = sqlcolumn;
	}

	public ArrayList getProjectList() {
		return projectList;
	}

	public void setProjectList(ArrayList projectList) {
		this.projectList = projectList;
	}

	public ArrayList getOperationList() {
		return operationList;
	}

	public void setOperationList(ArrayList operationList) {
		this.operationList = operationList;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getLinkId() {
		return linkId;
	}

	public ArrayList getButtons() {
		return buttons;
	}

	public void setButtons(ArrayList buttons) {
		this.buttons = buttons;
	}

	public void setEmailInfo(LazyDynaBean emailInfo) {
		this.emailInfo = emailInfo;
	}

	public LazyDynaBean getEmailInfo() {
		return emailInfo;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setInfoBean(LazyDynaBean infoBean) {
		this.infoBean = infoBean;
	}

	public LazyDynaBean getInfoBean() {
		return infoBean;
	}
	public String getPageNum() {
		return pageNum;
	}

	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getNext_linkId() {
		return next_linkId;
	}

	public void setNext_linkId(String next_linkId) {
		this.next_linkId = next_linkId;
	}

	public void setNext_nodeId(String next_nodeId) {
		this.next_nodeId = next_nodeId;
	}

	public String getNext_nodeId() {
		return next_nodeId;
	}

	public boolean isHasFlowLinkPriv() {
		return hasFlowLinkPriv;
	}

	public void setHasFlowLinkPriv(boolean hasFlowLinkPriv) {
		this.hasFlowLinkPriv = hasFlowLinkPriv;
	}

	public String getSkipFlag() {
		return skipFlag;
	}

	public void setSkipFlag(String skipFlag) {
		this.skipFlag = skipFlag;
	}

	public ArrayList getSkiplist() {
		return skiplist;
	}

	public void setSkiplist(ArrayList skiplist) {
		this.skiplist = skiplist;
	}
	
}
