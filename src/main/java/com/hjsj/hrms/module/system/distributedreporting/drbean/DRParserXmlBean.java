package com.hjsj.hrms.module.system.distributedreporting.drbean;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @version: 1.0
 * @Description: 解析数据报送规范xmlBean
 * @author: duxl  
 * @date: 2019年3月25日 下午1:50:30
 */
/* <?xml version="1.0" encoding="UTF-8"?><root>
  <param reportPhoto="是否上报照片" createTime="创建时间"/>
  <fieldSet>
    <set desc="主、子集描述" setid="主、子集标识"></set>
  </fieldSet>
  <fieldItem>
    <item setid="子集标识" uniq="是否唯一" mustfill="是否必填" itemdecimal="指标小数位" itemlength="指标长度" itemtype="指标类型" codesetid="关联代码类" itemdesc="指标名称" itemid="指标标识"></item>
  </fieldItem>
  <protectPeople checkbox="true">
    <dbname pre="人员库前缀"></dbname>
    <peopleCondition condition="计算公式内容"></peopleCondition>
  </protectPeople>
  <protectField checkbox="true">
    <dbname pre="人员库前缀"></dbname>
    <peopleCondition condition="计算公式内容"></peopleCondition>
    <fieldCondition condition="指标标识"></fieldCondition>
  </protectField>
  <personStatus>
	<personItemid>人员状态对应指标</personItemid>
	<mapping pre="Usr" personMapping="1"></mapping>
	<mapping pre="Ret" personMapping="2"></mapping>
</personStatus>
</root> 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "scheme")
public class DRParserXmlBean {
	public DRParamBean getParam() {
		return param;
	}

	public void setParam(DRParamBean param) {
		this.param = param;
	}

	public DRFieldSetBean getFieldSet() {
		return fieldSet;
	}

	public void setFieldSet(DRFieldSetBean fieldSet) {
		this.fieldSet = fieldSet;
	}

	public DRFieldItemBean getFieldItem() {
		return fieldItem;
	}

	public void setFieldItem(DRFieldItemBean fieldItem) {
		this.fieldItem = fieldItem;
	}

	public DRProtectPeopleBean getProtectPeople() {
		return protectPeople;
	}

	public void setProtectPeople(DRProtectPeopleBean protectPeople) {
		this.protectPeople = protectPeople;
	}

	public DRProtectFieldBean getProtectField() {
		return protectField;
	}

	public void setProtectField(DRProtectFieldBean protectField) {
		this.protectField = protectField;
	}

	public DRPersonStatusBean getPersonStatus() {
		return personStatus;
	}

	public void setPersonStatus(DRPersonStatusBean personStatus) {
		this.personStatus = personStatus;
	}

	@XmlElement
	public DRParamBean param;
	
	@XmlElement
	public DRFieldSetBean fieldSet;
	
	@XmlElement
	public DRFieldItemBean fieldItem;
	
	@XmlElement 
	public DRProtectPeopleBean protectPeople;
	
	@XmlElement
	public DRProtectFieldBean protectField;
	
	@XmlElement
	public DRPersonStatusBean personStatus;
	
}
