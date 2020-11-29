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
public class StrResultView implements Serializable{
	private int ntype;
    private String strresult;
	private boolean bl;
	public StrResultView(){
	}
	/**
	 * @return
	 */
	public boolean isBl() {
		return bl;
	}

	/**
	 * @return
	 */
	public int getNtype() {
		return ntype;
	}

	/**
	 * @return
	 */
	public String getStrresult() {
		return strresult;
	}

	/**
	 * @param b
	 */
	public void setBl(boolean bl) {
		this.bl = bl;
	}

	/**
	 * @param i
	 */
	public void setNtype(int ntype) {
		this.ntype = ntype;
	}

	/**
	 * @param string
	 */
	public void setStrresult(String strresult) {
		this.strresult = strresult;
	}

}
