/*
 * Created on 2005-12-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.valueobject.common;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OrganizationView implements Serializable{
	private String codesetid;
	private String codeitemid;
	private String codeitemdesc;
	private String parentid;
	private String childid;
	private String state;
	private String grade;
	private String a0000;
	private String groupid;
	private String pos_cond;
	/**
	 * @return Returns the a0000.
	 */
	public String getA0000() {
		return a0000;
	}
	/**
	 * @param a0000 The a0000 to set.
	 */
	public void setA0000(String a0000) {
		this.a0000 = a0000;
	}
	/**
	 * @return Returns the childid.
	 */
	public String getChildid() {
		return childid;
	}
	/**
	 * @param childid The childid to set.
	 */
	public void setChildid(String childid) {
		this.childid = childid;
	}
	/**
	 * @return Returns the codeitemdesc.
	 */
	public String getCodeitemdesc() {
		return codeitemdesc;
	}
	/**
	 * @param codeitemdesc The codeitemdesc to set.
	 */
	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}
	/**
	 * @return Returns the codeitemid.
	 */
	public String getCodeitemid() {
		return codeitemid;
	}
	/**
	 * @param codeitemid The codeitemid to set.
	 */
	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	/**
	 * @return Returns the codesetid.
	 */
	public String getCodesetid() {
		return codesetid;
	}
	/**
	 * @param codesetid The codesetid to set.
	 */
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	/**
	 * @return Returns the grade.
	 */
	public String getGrade() {
		return grade;
	}
	/**
	 * @param grade The grade to set.
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}
	/**
	 * @return Returns the groupid.
	 */
	public String getGroupid() {
		return groupid;
	}
	/**
	 * @param groupid The groupid to set.
	 */
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	/**
	 * @return Returns the parentid.
	 */
	public String getParentid() {
		return parentid;
	}
	/**
	 * @param parentid The parentid to set.
	 */
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	/**
	 * @return Returns the pos_cond.
	 */
	public String getPos_cond() {
		return pos_cond;
	}
	/**
	 * @param pos_cond The pos_cond to set.
	 */
	public void setPos_cond(String pos_cond) {
		this.pos_cond = pos_cond;
	}
	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}
}
