/**
 * 
 */
package com.hjsj.hrms.businessobject.general.muster;

import java.io.Serializable;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-15:14:34:02</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class MusterParamterVo implements Serializable {

	/**
	 * 
	 */
	public MusterParamterVo() {
		super();
	}
	/**花名册名称*/
	private String mustername;
	/**公私用标志*/
	private String used_flag;
	/**排序指标*/
	private String sortfields[];
	/**花名册指标*/
	private String musterfields[];
	/**用户名称*/
	private String username;
	/**花名册分类*/
	private String mustertype;
	/**信息群种类
	 * 1:人员
	 * 2:单位
	 * 3:职位
	 * */
	
	/**包含历史记录*/
	private String history="0";
	private String infor_kind;
	private String sortitem;
	
	public String getInfor_kind() {
		return infor_kind;
	}
	public void setInfor_kind(String infor_kind) {
		this.infor_kind = infor_kind;
	}
	public String[] getMusterfields() {
		return musterfields;
	}
	public void setMusterfields(String[] musterfields) {
		this.musterfields = musterfields;
	}
	public String getMustername() {
		return mustername;
	}
	public void setMustername(String mustername) {
		this.mustername = mustername;
	}
	public String[] getSortfields() {
		return sortfields;
	}
	public void setSortfields(String[] sortfields) {
		this.sortfields = sortfields;
	}
	public String getUsed_flag() {
		return used_flag;
	}
	public void setUsed_flag(String used_flag) {
		if(used_flag==null||(!"0".equals(used_flag))) {
            used_flag="1";
        }
		this.used_flag = used_flag;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMustertype() {
		return mustertype;
	}
	public void setMustertype(String mustertype) {
		this.mustertype = mustertype;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	public String getSortitem() {
		return sortitem;
	}
	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}
	
}
