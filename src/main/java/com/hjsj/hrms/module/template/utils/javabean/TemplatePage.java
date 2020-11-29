/**
 * 
 */
package com.hjsj.hrms.module.template.utils.javabean;

public class TemplatePage {
	private int tabId=-1;
	private int pageId=-1;

	private String title;
	/**是否显示*/
	private boolean isPrint=true;
	//是否是移动页签  默认为0 0代表着不是移动页签 
	private boolean isMobile;
	//打印方向， 0 :默认 同模板打印方向，1：纵向 2：横向 20170410
	private int  paperOrientation=0;
	private boolean isShow = true;
	
	public int getTabId() {
		return tabId;
	}
	public void setTabId(int tabId) {
		this.tabId = tabId;
	}
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isPrint() {
		return isPrint;
	}
	public void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}
	public boolean isMobile() {
		return isMobile;
	}
	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}
	public int getPaperOrientation() {
		return paperOrientation;
	}
	public void setPaperOrientation(int paperOrientation) {
		this.paperOrientation = paperOrientation;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
}
