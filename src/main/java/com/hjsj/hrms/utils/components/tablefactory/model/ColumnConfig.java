package com.hjsj.hrms.utils.components.tablefactory.model;

import java.io.Serializable;

public class ColumnConfig implements Serializable{

	
	private String itemid;
    private String    is_display;
    private int    displaywidth;
    private int    align;
    private String is_order;
    private String is_sum;
    private String displaydesc;
    private String itemdesc;
    private String mergedesc;
    private int displayorder;
    private String itemtype;
    private String is_lock;
    private String is_fromdict;
    private String is_removable;
    private String fieldsetid;
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getIs_display() {
		return is_display;
	}
	public void setIs_display(String is_display) {
		this.is_display = is_display;
	}
	public int getDisplaywidth() {
		return displaywidth;
	}
	public void setDisplaywidth(int displaywidth) {
		this.displaywidth = displaywidth;
	}
	public int getAlign() {
		return align;
	}
	public void setAlign(int align) {
		this.align = align;
	}
	public String getIs_order() {
		return is_order;
	}
	public void setIs_order(String is_order) {
		this.is_order = is_order;
	}
	public String getIs_sum() {
		return is_sum;
	}
	public void setIs_sum(String is_sum) {
		this.is_sum = is_sum;
	}
	public String getDisplaydesc() {
		return displaydesc;
	}
	public void setDisplaydesc(String displaydesc) {
		this.displaydesc = displaydesc;
	}
	public String getItemdesc() {
		return itemdesc;
	}
	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}
	public String getMergedesc() {
		return mergedesc;
	}
	public void setMergedesc(String mergedesc) {
		this.mergedesc = mergedesc;
	}
	
	public int getDisplayorder() {
		return displayorder;
	}
	public void setDisplayorder(int displayorder) {
		this.displayorder = displayorder;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getIs_lock() {
		return is_lock;
	}
	public void setIs_lock(String is_lock) {
		this.is_lock = is_lock;
	}
	@Deprecated
	public String getIs_fromdict() {
		return is_fromdict;
	}
	@Deprecated
	public void setIs_fromdict(String is_fromdict) {
		this.is_fromdict = is_fromdict;
	}
	public String getIs_removable() {
		return is_removable;
	}
	public void setIs_removable(String is_removable) {
		this.is_removable = is_removable;
	}
	public String getFieldsetid() {
		return fieldsetid;
	}
	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}
	
	
}
