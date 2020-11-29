package com.hjsj.hrms.module.system.distributedreporting.drbean;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "protectField")
public class DRProtectFieldBean {
	@XmlAttribute
	public String checkbox;
	@XmlElement(name = "dbname")
	public DRDbnameBean dbname;
	@XmlElement(name = "peopleCondition")
	public DRPeopleConditionBean peopleCondition;
	@XmlElement(name = "fieldCondition")
	public DRFieldConditionBean fieldCondition;
	public String getCheckbox() {
		return checkbox;
	}
	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}
	public DRDbnameBean getDbname() {
		return dbname;
	}
	public void setDbname(DRDbnameBean dbname) {
		this.dbname = dbname;
	}
	public DRPeopleConditionBean getPeopleCondition() {
		return peopleCondition;
	}
	public void setPeopleCondition(DRPeopleConditionBean peopleCondition) {
		this.peopleCondition = peopleCondition;
	}
	public DRFieldConditionBean getFieldCondition() {
		return fieldCondition;
	}
	public void setFieldCondition(DRFieldConditionBean fieldCondition) {
		this.fieldCondition = fieldCondition;
	}

}
