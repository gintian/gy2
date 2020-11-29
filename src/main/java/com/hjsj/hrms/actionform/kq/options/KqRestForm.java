/*
 * Created on 2006-12-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.kq.options;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public  class KqRestForm extends FrameForm {

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	
	private String[] rest_weeks;
	private ArrayList fieldList =new ArrayList();
	
		
	@Override
    public void outPutFormHM() {
	    this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setRest_weeks((String[])this.getFormHM().get("rest_weeks"));	
		
		
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
	
	
		this.getFormHM().put("rest_weeks",this.rest_weeks);
		this.getFormHM().put("fieldList",this.fieldList);
		//cat.debug(this.getFormHM().put("rest_weeks",this.rest_weeks)+"ss");
		
		
	}

	/**
	 * @return Returns the b0110.
	 */

	/**
	 * @return Returns the rest_weeks.
	 */
	public String[] getRest_weeks() {
		return rest_weeks;
	}
	/**
	 * @param rest_weeks The rest_weeks to set.
	 */
	public void setRest_weeks(String[] rest_weeks) {
		this.rest_weeks = rest_weeks;
	}
	/**
	 * @return Returns the fieldlist.
	 */
	
	/**
	 * @return Returns the fieldList.
	 */
	public ArrayList getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList The fieldList to set.
	 */
	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}
}
