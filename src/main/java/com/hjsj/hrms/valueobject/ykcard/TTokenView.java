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
public class TTokenView implements Serializable{
	private int nval; //符号名称
    private int nstyle; //符号类别 Class
	public TTokenView() {
		nval = 0;
		nstyle = 0;
	}
	/**
	 * @return
	 */
	public int getNstyle() {
		return nstyle;
	}

	/**
	 * @return
	 */
	public int getNval() {
		return nval;
	}

	/**
	 * @param i
	 */
	public void setNstyle(int nstyle) {
		this.nstyle = nstyle;
	}

	/**
	 * @param i
	 */
	public void setNval(int nval) {
		this.nval = nval;
	}

}
