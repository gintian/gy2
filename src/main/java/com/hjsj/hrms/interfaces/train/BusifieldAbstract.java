/*
 * 创建日期 2005-9-8
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.interfaces.train;

/**
 * @author luangaojiong
 *
 * 业务编码虚基类
 */
public abstract class BusifieldAbstract {
	
	public  String fieldsetid="";  //表名字段
	public String itemid="";		//列字段
	public String codesetid="0";		//类型编码
	public String codeflag="";		//关联代码标识 1为关联表代码

	/**
	 * @return 返回 codeflag。
	 */
	public String getCodeflag() {
		return codeflag;
	}
	/**
	 * @param codeflag 要设置的 codeflag。
	 */
	public void setCodeflag(String codeflag) {
		this.codeflag = codeflag;
	}
	/**
	 * @return 返回 codesetid。
	 */
	public String getCodesetid() {
		return codesetid;
	}
	/**
	 * @param codesetid 要设置的 codesetid。
	 */
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	/**
	 * @return 返回 fieldsetid。
	 */
	public String getFieldsetid() {
		return fieldsetid;
	}
	/**
	 * @param fieldsetid 要设置的 fieldsetid。
	 */
	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}
	/**
	 * @return 返回 itemid。
	 */
	public String getItemid() {
		return itemid;
	}
	/**
	 * @param itemid 要设置的 itemid。
	 */
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
}
