package com.hjsj.hrms.module.system.distributedreporting.drbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "personStatus")
public class DRPersonStatusBean {
	@XmlElement
	public String personItemid;
	
	@XmlElement(name = "mapping")
	private List <DRMappingBean> mapping;
	
	public String getPersonItemid() {
		return personItemid;
	}
	public void setPersonItemid(String personItemid) {
		this.personItemid = personItemid;
	}
	public List<DRMappingBean> getMapping() {
		return mapping;
	}
	public void setMapping(List<DRMappingBean> mapping) {
		this.mapping = mapping;
	}
	
}
