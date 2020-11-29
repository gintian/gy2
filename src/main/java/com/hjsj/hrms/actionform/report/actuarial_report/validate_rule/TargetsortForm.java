/**
 * 
 */
package com.hjsj.hrms.actionform.report.actuarial_report.validate_rule;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:报表周期</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:oct 6, 2009:10:46:29 AM</p>
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class TargetsortForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
	
	
	private ArrayList spersonlist = new ArrayList();
	private String sperson;
	private ArrayList subclasslist = new ArrayList();
	private ArrayList selectsubclass = new ArrayList();
	private String left_fields[];
	private String right_fields[];
	protected UserView userView = this.getUserView();
	private String targetsortid;
	private String mess;
	/**
	 * 建议对象列表
	 */

	private PaginationForm targetsortForm = new PaginationForm();

	

	@Override
    public void outPutFormHM() {
	
		  this.getTargetsortForm().setList((ArrayList)this.getFormHM().get("tagetsortlist"));
		  this.setSubclasslist((ArrayList)this.getFormHM().get("subclasslist"));
		  this.setSelectsubclass((ArrayList)this.getFormHM().get("selectsubclass"));
			this.setMess((String)this.getFormHM().get("mess"));
		  //  this.setReportcyclevo((RecordVo) this.getFormHM().get("reportcyclevo2"));
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("right_fields",this.getRight_fields());
		//this.getFormHM().put("reportcyclevo", this.getReportcyclevo());
		
		//System.out.println("getreportcyclevo:"+this.getReportcyclevo());
		//this.getFormHM().put("selectedreportlist",
		//		(ArrayList) this.getReportCycleForm().getSelectedList());
	}

	

	/**
	 * @return Returns the Boardvo.
	
	/*
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		super.reset(arg0, arg1);
	}



	public ArrayList getSpersonlist() {
		return spersonlist;
	}

	public void setSpersonlist(ArrayList spersonlist) {
		this.spersonlist = spersonlist;
	}

	public String getSperson() {
		return sperson;
	}

	public void setSperson(String sperson) {
		this.sperson = sperson;
	}

	@Override
    public UserView getUserView() {
		return userView;
	}

	@Override
    public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public PaginationForm getTargetsortForm() {
		return targetsortForm;
	}

	public void setTargetsortForm(PaginationForm targetsortForm) {
		this.targetsortForm = targetsortForm;
	}

	public ArrayList getSubclasslist() {
		return subclasslist;
	}

	public void setSubclasslist(ArrayList subclasslist) {
		this.subclasslist = subclasslist;
	}

	public ArrayList getSelectsubclass() {
		return selectsubclass;
	}

	public void setSelectsubclass(ArrayList selectsubclass) {
		this.selectsubclass = selectsubclass;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getTargetsortid() {
		return targetsortid;
	}

	public void setTargetsortid(String targetsortid) {
		this.targetsortid = targetsortid;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	


	


 
    //end insert 
  
   
 
}
