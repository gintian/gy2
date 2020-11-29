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
public class TNameView implements Serializable{
	private String cname;
	public String cvalue;
	public byte ntype;
	boolean bflag;
	public TNameView() {
		cname = "";
		cvalue = "1";
		ntype = 0;
		bflag = false;
	}
	/**
	 * @return
	 */
	public boolean isBflag() {
		return bflag;
	}

	/**
	 * @return
	 */
	public String getCname() {
		return cname;
	}

	/**
	 * @return
	 */
	public String getCvalue() {
		return cvalue;
	}

	/**
	 * @return
	 */
	public byte getNtype() {
		return ntype;
	}

	/**
	 * @param b
	 */
	public void setBflag(boolean bflag) {
		this.bflag = bflag;
	}

	/**
	 * @param string
	 */
	public void setCname(String cname) {
		this.cname = cname;
	}

	/**
	 * @param string
	 */
	public void setCvalue(String cvalue) {
		this.cvalue = cvalue;
	}

	/**
	 * @param b
	 */
	public void setNtype(byte ntype) {
		this.ntype = ntype;
	}

}
