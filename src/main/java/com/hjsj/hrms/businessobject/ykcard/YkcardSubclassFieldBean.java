package com.hjsj.hrms.businessobject.ykcard;

public class YkcardSubclassFieldBean {

	private String name;
	private String fieldsetid;
	private String itemtype;
	private String itemlength;
	private String codesetid;
	private String pre;
	private String defaultt;
	private String slop;
	private String need;
	private String title;
	private String align;
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getDefaultt() {
		return defaultt;
	}
	public void setDefaultt(String defaultt) {
		this.defaultt = defaultt;
	}
	public String getFieldsetid() {
		return fieldsetid;
	}
	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}
	public String getItemlength() {
		return itemlength;
	}
	public void setItemlength(String itemlength) {
		this.itemlength = itemlength;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNeed() {
		return need;
	}
	public void setNeed(String need) {
		this.need = need;
	}
	public String getPre() {
		return pre;
	}
	public void setPre(String pre) {
		this.pre = pre;
	}
	public String getSlop() {
		if(slop==null||slop.length()<=0) {
            slop="0";
        }
		return slop;
	}
	public void setSlop(String slop) {
		this.slop = slop;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

}
