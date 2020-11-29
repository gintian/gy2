package com.hjsj.hrms.actionform.train.setparam;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>Title:GzAmountXMLBo.java</p>
 * <p>Description:常量表xml参数解析</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class SetParamForm extends FrameForm {
	private ArrayList itemlist = new ArrayList(); //需求采集指标
	private ArrayList selectlist = new ArrayList(); //需求采集指标
	private String[] left_fields;
	private String[] right_fields;
	
	// 流媒体服务器类型
	private String mediaServerType;
	
	// 流媒体服务器地址
	private String mediaServerAddress;
	
	// 流媒体服务器端口
	private String mediaServerPort;
	
	// ftp服务器地址
	private String ftpServerAddress;
	
	// ftp服务器端口
	private String ftpServerPort;
	
	// ftp服务器用户名
	private String ftpServerUserName;
	
	// ftp服务器密码
	private String ftpServerPwd;
	
	// 文件路径
	private String filePath;
	
	// 文件大小限制
	private String fileSize;
	
	// 是否允许下载
	private String isDownload;
	
	// openoffice地址
	private String openOfficeAdd;
	
	// openoffice端口
	private String openOfficePort;
	
	// 发布点
	private String mediaServerPubRoot;
	
	private String budget;//预算
	
	public String getMediaServerPubRoot() {
		return mediaServerPubRoot;
	}

	public void setMediaServerPubRoot(String mediaServerPubRoot) {
		this.mediaServerPubRoot = mediaServerPubRoot;
	}

	public String getOpenOfficeAdd() {
		return openOfficeAdd;
	}

	public void setOpenOfficeAdd(String openOfficeAdd) {
		this.openOfficeAdd = openOfficeAdd;
	}

	public String getOpenOfficePort() {
		return openOfficePort;
	}

	public void setOpenOfficePort(String openOfficePort) {
		this.openOfficePort = openOfficePort;
	}

	public String getMediaServerType() {
		return mediaServerType;
	}

	public void setMediaServerType(String mediaServerType) {
		this.mediaServerType = mediaServerType;
	}

	public String getMediaServerAddress() {
		return mediaServerAddress;
	}

	public void setMediaServerAddress(String mediaServerAddress) {
		this.mediaServerAddress = mediaServerAddress;
	}

	public String getMediaServerPort() {
		return mediaServerPort;
	}

	public void setMediaServerPort(String mediaServerPort) {
		this.mediaServerPort = mediaServerPort;
	}

	public String getFtpServerAddress() {
		return ftpServerAddress;
	}

	public void setFtpServerAddress(String ftpServerAddress) {
		this.ftpServerAddress = ftpServerAddress;
	}

	public String getFtpServerPort() {
		return ftpServerPort;
	}

	public void setFtpServerPort(String ftpServerPort) {
		this.ftpServerPort = ftpServerPort;
	}

	public String getFtpServerUserName() {
		return ftpServerUserName;
	}

	public void setFtpServerUserName(String ftpServerUserName) {
		this.ftpServerUserName = ftpServerUserName;
	}

	public String getFtpServerPwd() {
		return ftpServerPwd;
	}

	public void setFtpServerPwd(String ftpServerPwd) {
		this.ftpServerPwd = ftpServerPwd;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getIsDownload() {
		return isDownload;
	}

	public void setIsDownload(String isDownload) {
		this.isDownload = isDownload;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setSelectlist((ArrayList)this.getFormHM().get("selectlist"));
		
		this.setFilePath((String) this.getFormHM().get("filePath"));
		this.setFileSize((String) this.getFormHM().get("fileSize"));
		this.setFtpServerAddress((String) this.getFormHM().get("ftpServerAddress"));
		this.setFtpServerPort((String) this.getFormHM().get("ftpServerPort"));
		this.setFtpServerPwd((String) this.getFormHM().get("ftpServerPwd"));
		this.setFtpServerUserName((String) this.getFormHM().get("ftpServerUserName"));
		this.setIsDownload((String) this.getFormHM().get("isDownload"));
		this.setMediaServerAddress((String) this.getFormHM().get("mediaServerAddress"));
		this.setMediaServerPort((String) this.getFormHM().get("mediaServerPort"));
		this.setMediaServerType((String) this.getFormHM().get("mediaServerType"));
		this.setOpenOfficeAdd((String) this.getFormHM().get("openOfficeAdd"));
		this.setOpenOfficePort((String) this.getFormHM().get("openOfficePort"));
		this.setMediaServerPubRoot((String) this.getFormHM().get("mediaServerPubRoot"));
		this.setBudget((String)this.getFormHM().get("budget"));
		
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("left_fields",this.getLeft_fields());
		this.getFormHM().put("right_fileds",this.getRight_fields());
		
		this.getFormHM().put("filePath", this.getFilePath());
		this.getFormHM().put("fileSize", this.getFileSize());
		this.getFormHM().put("ftpServerAddress", this.getFtpServerAddress());
		this.getFormHM().put("ftpServerPort", this.getFtpServerPort());
		this.getFormHM().put("ftpServerPwd", this.getFtpServerPwd());
		this.getFormHM().put("ftpServerUserName", this.getFtpServerUserName());
		this.getFormHM().put("isDownload", this.getIsDownload());
		this.getFormHM().put("mediaServerAddress", this.getMediaServerAddress());
		this.getFormHM().put("mediaServerPort", this.getMediaServerPort());
		this.getFormHM().put("mediaServerType", this.getMediaServerType());
		this.getFormHM().put("openOfficeAdd", this.getOpenOfficeAdd());
		this.getFormHM().put("openOfficePort", this.getOpenOfficePort());
		this.getFormHM().put("mediaServerPubRoot", this.getMediaServerPubRoot());
		this.getFormHM().put("budget", this.getBudget());
		
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public ArrayList getSelectlist() {
		return selectlist;
	}

	public void setSelectlist(ArrayList selectlist) {
		this.selectlist = selectlist;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getBudget() {
		return budget;
	}

	public void setBudget(String budget) {
		this.budget = budget;
	}

}
