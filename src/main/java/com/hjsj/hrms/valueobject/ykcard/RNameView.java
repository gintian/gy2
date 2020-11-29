/*
 * Created on 2005-5-9
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.valueobject.ykcard;

import java.io.Serializable;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RNameView implements Serializable {
	private String tabid = null; //报表号(Key)
	private String name = null; //报表文件名称
	private String tmargin = null; //页上边界
	private String bmargin = null; //页下边界
	private String lmargin = null; //页左边界
	private String rmargin = null; //页右边界
	private String paper = null; //纸张标识1：A3，2：A4…
	private String paperori = null; //纸张方向1：纵向2：横向
	private String paperw = null; //纸宽
	private String paperh = null; //纸高
	private String flag = null; // 数据来源A：人员库 B：单位库
	private String moduleflag = null; //没用
	public RNameView() {
	}
	/**
	 * @return
	 */
	public String getBmargin() {
		return bmargin;
	}

	/**
	 * @return
	 */
	public String getFlag() {
		return flag;
	}

	/**
	 * @return
	 */
	public String getLmargin() {
		return lmargin;
	}

	/**
	 * @return
	 */
	public String getModuleflag() {
		return moduleflag;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getPaper() {
		return paper;
	}

	/**
	 * @return
	 */
	public String getPaperh() {
		return paperh;
	}

	/**
	 * @return
	 */
	public String getPaperori() {
		return paperori;
	}

	/**
	 * @return
	 */
	public String getPaperw() {
		return paperw;
	}

	/**
	 * @return
	 */
	public String getRmargin() {
		return rmargin;
	}

	/**
	 * @return
	 */
	public String getTabid() {
		return tabid;
	}

	/**
	 * @return
	 */
	public String getTmargin() {
		return tmargin;
	}

	/**
	 * @param string
	 */
	public void setBmargin(String bmargin) {
		this.bmargin = bmargin;
	}

	/**
	 * @param string
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * @param string
	 */
	public void setLmargin(String lmargin) {
		this.lmargin = lmargin;
	}

	/**
	 * @param string
	 */
	public void setModuleflag(String moduleflag) {
		this.moduleflag = moduleflag;
	}

	/**
	 * @param string
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param string
	 */
	public void setPaper(String paper) {
		this.paper = paper;
	}

	/**
	 * @param string
	 */
	public void setPaperh(String paperh) {
		this.paperh = paperh;
	}

	/**
	 * @param string
	 */
	public void setPaperori(String paperori) {
		this.paperori = paperori;
	}

	/**
	 * @param string
	 */
	public void setPaperw(String paperw) {
		this.paperw = paperw;
	}

	/**
	 * @param string
	 */
	public void setRmargin(String rmargin) {
		this.rmargin = rmargin;
	}

	/**
	 * @param string
	 */
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	/**
	 * @param string
	 */
	public void setTmargin(String tmargin) {
		this.tmargin = tmargin;
	}

}
