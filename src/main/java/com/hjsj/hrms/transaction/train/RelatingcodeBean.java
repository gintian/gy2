/*
 * 创建日期 2005-9-8
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

/**
 * @author luangaojiong
 *
 * 关联表处理
 */
public class RelatingcodeBean {
	
	String codesetid=""; //代码
	String codetable=""; //表名
	String codevalue=""; //字段名
	String codedesc="";  //关联字段即要显示的字段
	String upcodevalue="";
	
	

	/**
	 * @return 返回 codedesc。
	 */
	public String getCodedesc() {
		return codedesc;
	}
	/**
	 * @param codedesc 要设置的 codedesc。
	 */
	public void setCodedesc(String codedesc) {
		this.codedesc = codedesc;
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
	 * @return 返回 codetable。
	 */
	public String getCodetable() {
		return codetable;
	}
	/**
	 * @param codetable 要设置的 codetable。
	 */
	public void setCodetable(String codetable) {
		this.codetable = codetable;
	}
	/**
	 * @return 返回 codevalue。
	 */
	public String getCodevalue() {
		return codevalue;
	}
	/**
	 * @param codevalue 要设置的 codevalue。
	 */
	public void setCodevalue(String codevalue) {
		this.codevalue = codevalue;
	}
	/**
	 * @return 返回 upcodevalue。
	 */
	public String getUpcodevalue() {
		return upcodevalue;
	}
	/**
	 * @param upcodevalue 要设置的 upcodevalue。
	 */
	public void setUpcodevalue(String upcodevalue) {
		this.upcodevalue = upcodevalue;
	}
}
