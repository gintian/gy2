/*
 * Created on 2005-5-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.valueobject.common;
import com.hrms.hjsj.sys.FieldItem;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FieldItemView  extends FieldItem{
	private String value;
	private String rowflag;
	private String viewvalue;
	private String fieldvalue;
	private String fieldcode;
	private String rowindex;
	/*xupengyu处理并发修改数据的问题，在加载到修改页面时把原来的值保存在oldvalue中*/
	private String oldvalue;


			
	/**
	 * @return Returns the rowindex.
	 */
	public String getRowindex() {
		return rowindex;
	}
	/**
	 * @param rowindex The rowindex to set.
	 */
	public void setRowindex(String rowindex) {
		this.rowindex = rowindex;
	}
	/**
	 * @return Returns the rowflag.
	 */
	public String getRowflag() {
		return rowflag;
	}
	/**
	 * @param rowflag The rowflag to set.
	 */
	public void setRowflag(String rowflag) {
		this.rowflag = rowflag;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return Returns the viewvalue.
	 */
	public String getViewvalue() {
		return viewvalue;
	}
	/**
	 * @param viewvalue The viewvalue to set.
	 */
	public void setViewvalue(String viewvalue) {
		this.viewvalue = viewvalue;
	}
	/**
	 * @return Returns the fieldcode.
	 */
	public String getFieldcode() {
		return fieldcode;
	}
	/**
	 * @param fieldcode The fieldcode to set.
	 */
	public void setFieldcode(String fieldcode) {
		this.fieldcode = fieldcode;
	}
	/**
	 * @return Returns the fieldvalue.
	 */
	public String getFieldvalue() {
		return fieldvalue;
	}
	/**
	 * @param fieldvalue The fieldvalue to set.
	 */
	public void setFieldvalue(String fieldvalue) {
		this.fieldvalue = fieldvalue;
	}
	public String getOldvalue() {
		return oldvalue;
	}
	public void setOldvalue(String oldvalue) {
		this.oldvalue = oldvalue;
	}
	
}
