package com.hjsj.hrms.actionform.train;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:培训机构Form</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-17:13:25:23</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class OrganizationForm extends FrameForm {

	/**
	 * 培训机构表id
	 */
    String eduid="0";
       
    /**
     * 声明窗体对象
     */
    
    private PaginationForm organizationForm=new PaginationForm();   
    private ArrayList dynamicCol=new ArrayList();	//动态列名ArrayList
  
	/**
	 * @return 返回 dynamicCol。
	 */
	public ArrayList getDynamicCol() {
		return dynamicCol;
	}
	/**
	 * @param dynamicCol 要设置的 dynamicCol。
	 */
	public void setDynamicCol(ArrayList dynamicCol) {
		this.dynamicCol = dynamicCol;
	}
    @Override
    public void outPutFormHM() {
        this.getOrganizationForm().setList((ArrayList)this.getFormHM().get("organizationlist"));
        this.setEduid(this.getFormHM().get("eduid").toString());
        this.setDynamicCol((ArrayList)this.getFormHM().get("dynamicCol"));
    }

    
    @Override
    public void inPutTransHM() {
    	
    	this.getFormHM().put("eduid",this.getEduid());
    	this.getFormHM().put("dynamicCol",this.getDynamicCol());
	  
    }
    /**
     * 培训机构id属性
     * @param eduid
     */
    public void setEduid(String eduid)
    {
    	this.eduid=eduid;
    }
    
    public String getEduid()
    {
    	return this.eduid;
    }
    /**
     * @return Returns the organizationForm.
     */
    public PaginationForm getOrganizationForm() {
        return organizationForm;
    }
    /**
     * @param organizationForm The organizationForm to set.
     */
    public void setOrganizationForm(PaginationForm organizationForm) {
        this.organizationForm = organizationForm;
    }
    
    /* 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);
    }
    /* 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
              
        return super.validate(arg0, arg1);
    }
  
}
