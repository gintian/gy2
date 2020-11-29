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
public class TconstView implements Serializable{
	private String cvalue;
	private byte ntype;
	public TconstView() {
		//nValue:=0;
		ntype = 0;
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
