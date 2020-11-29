/*
 * Created on 2005-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.tree;

import com.hrms.struts.action.FrameForm;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeForm extends FrameForm {
	
	private String treeCode;
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTreeCode((String)this.getFormHM().get("treeCode"));

	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("treeCode",treeCode);

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
}
