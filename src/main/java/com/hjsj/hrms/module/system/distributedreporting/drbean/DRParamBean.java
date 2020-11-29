package com.hjsj.hrms.module.system.distributedreporting.drbean;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "param")
public class DRParamBean {
	@XmlAttribute
	public String createTime;
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getReportPhoto() {
		return reportPhoto;
	}

	public void setReportPhoto(String reportPhoto) {
		this.reportPhoto = reportPhoto;
	}
	@XmlAttribute
	public String reportPhoto;
}
