/*
 * 创建日期 2005-9-8
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.interfaces.train.BusifieldAbstract;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
/**
 * @author luangaojiong
 *
 *编码类 
 */
public class BusifieldBean extends BusifieldAbstract {
	
	/**
	 * 得到类的实例
	 **/
	String itemdesc="";
	/**
	 * @return 返回 itemdesc。
	 */
	public String getItemdesc() {
		return itemdesc;
	}
	/**
	 * @param itemdesc 要设置的 itemdesc。
	 */
	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}
	String itemtype="";
	String itemlength="0";
	String decimalwidth="0";
	String value="";
	String relTableName="";
	String relFieldDesc="";
	String relFieldId="0";
	String viewvalue="";
	/**
	 * @return 返回 viewvalue。
	 */
	public String getViewvalue() {
		return viewvalue;
	}
	/**
	 * @param viewvalue 要设置的 viewvalue。
	 */
	public void setViewvalue(String viewvalue) {
		this.viewvalue = viewvalue;
	}
	/**
	 * @return 返回 relFieldDesc。
	 */
	public String getRelFieldDesc() {
		return relFieldDesc;
	}
	/**
	 * @param relFieldDesc 要设置的 relFieldDesc。
	 */
	public void setRelFieldDesc(String relFieldDesc) {
		this.relFieldDesc = relFieldDesc;
	}
	/**
	 * @return 返回 relFieldId。
	 */
	public String getRelFieldId() {
		return relFieldId;
	}
	/**
	 * @param relFieldId 要设置的 relFieldId。
	 */
	public void setRelFieldId(String relFieldId) {
		this.relFieldId = relFieldId;
	}
	/**
	 * @return 返回 relTableName。
	 */
	public String getRelTableName() {
		return relTableName;
	}
	/**
	 * @param relTableName 要设置的 relTableName。
	 */
	public void setRelTableName(String relTableName) {
		this.relTableName = relTableName;
	}
	/**
	 * @return 返回 value。
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value 要设置的 value。
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return 返回 decimalwidth。
	 */
	public String getDecimalwidth() {
		return decimalwidth;
	}
	/**
	 * @param decimalwidth 要设置的 decimalwidth。
	 */
	public void setDecimalwidth(String decimalwidth) {
		this.decimalwidth = decimalwidth;
	}
	/**
	 * @return 返回 itemlength。
	 */
	public String getItemlength() {
		return itemlength;
	}
	/**
	 * @param itemlength 要设置的 itemlength。
	 */
	public void setItemlength(String itemlength) {
		this.itemlength = itemlength;
	}
	/**
	 * @return 返回 itemtype。
	 */
	public String getItemtype() {
		return itemtype;
	}
	/**
	 * @param itemtype 要设置的 itemtype。
	 */
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	/**
	 * 得到BusifieldBean的实例
	 * @return
	 */
	public static BusifieldBean InstanceFactory()
	{
		return new BusifieldBean();
	}
	public BusifieldBean()
	{
		//System.out.println("----->train-->BusifieldBean-初始化->"+AdminCode.getCodeName("19","01"));
		//HashMap hm=AdminCode
	}
	
	public String getItemDesc(String codeid, String codeitem )
	{
		 CodeItem item = new CodeItem();
		 String item1="";
		 item1 = AdminCode.getCodeName(codeid, codeitem);
		 if(item1==null)
		 {
		 	//System.out.println("----->train-->BusifieldBean-item-is null-->");
		 	return "";
		 }
		 //System.out.println("----->train-->BusifieldBean-item->"+item);
		 return item1;
	}

}
