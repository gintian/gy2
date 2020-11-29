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
public class RTitleView implements Serializable {
	private String tabid = null; //登记表号（Key）
	private String pageid = null; //页签号
	private String title = null; //页签
	private String flag = null; //
	private String isprn = null; //是否打印(0,1)=(不打印，打印)
	public RTitleView() {
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
	public String getIsprn() {
		return isprn;
	}

	/**
	 * @return
	 */
	public String getPageid() {
		return pageid;
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
	public String getTitle() {
		return title;
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
	public void setIsprn(String isprn) {
		this.isprn = isprn;
	}

	/**
	 * @param string
	 */
	public void setPageid(String pageid) {
		this.pageid = pageid;
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
	public void setTitle(String title) {
		this.title = title;
	}

}
