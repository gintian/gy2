package com.hjsj.hrms.module.system.distributedreporting.drbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mapping")
public class DRMappingBean {
	@XmlAttribute
	public String pre;
	@XmlAttribute
	public String personMapping;
	
	public String getPre() {
		return pre;
	}
	public void setPre(String pre) {
		this.pre = pre;
	}
	public String getPersonMapping() {
		return personMapping;
	}
	public void setPersonMapping(String personMapping) {
		this.personMapping = personMapping;
	}
	
}
