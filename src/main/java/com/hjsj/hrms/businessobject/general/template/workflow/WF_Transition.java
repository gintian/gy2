package com.hjsj.hrms.businessobject.general.template.workflow;

import java.sql.Connection;

/**
 * <p>Title:WF_Transition</p>
 * <p>Description:变迁</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 20, 20063:31:11 PM
 * @author chenmengqing
 * @version 4.0
 */
public class WF_Transition {
	private int tran_id;
	/**上一个节点*/
	private int pre_nodeid=-1;
	/**下一个节点*/
	private int next_nodeid=-1;
	private String tabid;
	/**变迁条件定义
	 * 采用XML格式保存
	 * */
	private String condition;
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public int getNext_nodeid() {
		return next_nodeid;
	}
	public void setNext_nodeid(int next_nodeid) {
		this.next_nodeid = next_nodeid;
	}
	public int getPre_nodeid() {
		return pre_nodeid;
	}
	public void setPre_nodeid(int pre_nodeid) {
		this.pre_nodeid = pre_nodeid;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public int getTran_id() {
		return tran_id;
	}
	public void setTran_id(int tran_id) {
		this.tran_id = tran_id;
	}

	public WF_Transition() {
	}	

	public WF_Transition(int tran_id,Connection conn) {
		this.tran_id=tran_id;
	}	
}
