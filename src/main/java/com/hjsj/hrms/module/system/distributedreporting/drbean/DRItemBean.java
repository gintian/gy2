package com.hjsj.hrms.module.system.distributedreporting.drbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item")
public class DRItemBean {
	public String getSetid() {
		return setid;
	}
	public void setSetid(String setid) {
		this.setid = setid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getItemdesc() {
		return itemdesc;
	}
	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getItemlength() {
		return itemlength;
	}
	public void setItemlength(String itemlength) {
		this.itemlength = itemlength;
	}
	public String getItemdecimal() {
		return itemdecimal;
	}
	public void setItemdecimal(String itemdecimal) {
		this.itemdecimal = itemdecimal;
	}
	public String getMustfill() {
		return mustfill;
	}
	public void setMustfill(String mustfill) {
		this.mustfill = mustfill;
	}
	public String getUniq() {
		return uniq;
	}
	public void setUniq(String uniq) {
		this.uniq = uniq;
	}
	@XmlAttribute(name = "setid")
	public String setid;
	@XmlAttribute(name = "itemid")
	public String itemid;
	@XmlAttribute(name = "itemdesc")
	public String itemdesc;
	@XmlAttribute(name = "codesetid")
	public String codesetid;
	@XmlAttribute(name = "itemtype")
	public String itemtype;
	@XmlAttribute(name = "itemlength")
	public String itemlength;
	@XmlAttribute(name = "itemdecimal")
	public String itemdecimal;
	@XmlAttribute(name = "mustfill")
	public String mustfill;
	@XmlAttribute(name = "uniq")
	public String uniq;
}
