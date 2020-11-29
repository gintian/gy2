/**
 * 
 */
package com.hjsj.hrms.actionform.general.muster;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Aug 15, 20062:43:33 PM
 * @author chenmengqing
 * @version 4.0
 */
public class FindForm extends FrameForm {
	/**
	 * 查找对话框来源
	 * =0,常用花名册
	 * =1信息录入
	 * =2工资管理
	 * */
	private String type="0";
	/**查找值*/
	private String find_value;
	
	private String find_viewvalue;
	/**代码值*/
	private String codeid;
	/**查找项目*/
	private String finditem;
	private String tabid;
	private String inform_kind;
	private ArrayList list=new ArrayList();
	
	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getFind_value() {
		return find_value;
	}

	public void setFind_value(String find_value) {
		this.find_value = find_value;
	}

	public String getFind_viewvalue() {
		return find_viewvalue;
	}

	public void setFind_viewvalue(String find_viewvalue) {
		this.find_viewvalue = find_viewvalue;
	}

	public String getFinditem() {
		return finditem;
	}

	public void setFinditem(String finditem) {
		this.finditem = finditem;
	}

	@Override
    public void outPutFormHM() {
		this.setList((ArrayList)this.getFormHM().get("list"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("inform_kind",this.getInform_kind());
		this.getFormHM().put("tabid",this.getTabid());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getInform_kind() {
		return inform_kind;
	}

	public void setInform_kind(String inform_kind) {
		this.inform_kind = inform_kind;
	}

}
