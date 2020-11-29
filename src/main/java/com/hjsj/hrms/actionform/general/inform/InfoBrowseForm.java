/*
 * Created on 2006-2-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.general.inform;

import com.hrms.struts.action.FrameForm;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfoBrowseForm extends FrameForm {

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private String treeCode;
	private String nid;
	private String infokind;
	private String isinfoself;
	private String pre;
	private String orgtype;
	private String returnvalue="";//返回标识
	private String parentid="";
	private String return_codeid="";
	public String getReturn_codeid() {
		return return_codeid;
	}

	public void setReturn_codeid(String return_codeid) {
		this.return_codeid = return_codeid;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setNid((String)this.getFormHM().get("nid"));
		this.setInfokind((String)this.getFormHM().get("infokind"));
		this.setOrgtype((String)this.getFormHM().get("orgtype"));
		this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
	    this.setParentid((String)this.getFormHM().get("parentid"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		 this.getFormHM().put("returnvalue", this.getReturnvalue());
		 this.getFormHM().put("parentid", this.getParentid());
	}

	/**
	 * @return Returns the infokind.
	 */
	public String getInfokind() {
		return infokind;
	}
	/**
	 * @param infokind The infokind to set.
	 */
	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}
	/**
	 * @return Returns the isinfoself.
	 */
	public String getIsinfoself() {
		return isinfoself;
	}
	/**
	 * @param isinfoself The isinfoself to set.
	 */
	public void setIsinfoself(String isinfoself) {
		this.isinfoself = isinfoself;
	}
	/**
	 * @return Returns the nid.
	 */
	public String getNid() {
		return nid;
	}
	/**
	 * @param nid The nid to set.
	 */
	public void setNid(String nid) {
		this.nid = nid;
	}
	/**
	 * @return Returns the treeCode.
	 */
	public String getTreeCode() {
		return treeCode;
	}
	/**
	 * @param treeCode The treeCode to set.
	 */
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	/**
	 * @return Returns the pre.
	 */
	public String getPre() {
		return pre;
	}
	/**
	 * @param pre The pre to set.
	 */
	public void setPre(String pre) {
		this.pre = pre;
	}
}
