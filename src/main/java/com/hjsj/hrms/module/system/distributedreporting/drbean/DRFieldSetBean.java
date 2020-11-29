package com.hjsj.hrms.module.system.distributedreporting.drbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "fieldSet")
public class DRFieldSetBean {
	@XmlElement(name = "set")
	private List <DRSetBean> setList;

	public List<DRSetBean> getSetList() {
		return setList;
	}

	public void setSetList(List<DRSetBean> setList) {
		this.setList = setList;
	}
}
