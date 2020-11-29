/*
 * Created on 2005-8-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.valueobject.common;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StationPosView implements Serializable{
   private String item;
   private String itemvalue;
   private String itemviewvalue;
/**
 * @return Returns the item.
 */
public String getItem() {
	return item;
}
/**
 * @param item The item to set.
 */
public void setItem(String item) {
	this.item = item;
}
/**
 * @return Returns the itemvalue.
 */
public String getItemvalue() {
	return itemvalue;
}
/**
 * @param itemvalue The itemvalue to set.
 */
public void setItemvalue(String itemvalue) {
	this.itemvalue = itemvalue;
}
/**
 * @return Returns the itemviewvalue.
 */
public String getItemviewvalue() {
	return itemviewvalue;
}
/**
 * @param itemviewvalue The itemviewvalue to set.
 */
public void setItemviewvalue(String itemviewvalue) {
	this.itemviewvalue = itemviewvalue;
}
}
