package com.hjsj.hrms.transaction.report.edit_report.send_receive_report;

public /**
 * 该类是一个数据存取结构
 * 
 * @author lzy
 * 
 */
class ReportData {
	// 报表id
	private String tabid;

	// 报表名称
	private String tabName;

	// 用来标志当前记录是否和数据库中的记录重复
	private boolean isRepeat;

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}
}