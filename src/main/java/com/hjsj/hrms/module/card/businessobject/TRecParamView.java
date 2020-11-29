/*
 * Created on 2005-5-9
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.module.card.businessobject;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TRecParamView implements Serializable{
	//	用于保存变量
	private boolean bflag;
	private String fvalue;
	private int nid;
	public TRecParamView() {
	}
	/**
	 * @return
	 */
	public boolean isBflag() {
		return this.bflag;
	}

	/**
	 * @return
	 */
	public String getFvalue() {
		return this.fvalue;
	}

	/**
	 * @return
	 */
	public int getNid() {
		return this.nid;
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
	public void setFvalue(String fvalue) {
		this.fvalue = fvalue;
	}

	/**
	 * @param i
	 */
	public void setNid(int nid) {
		this.nid = nid;
	}

}
