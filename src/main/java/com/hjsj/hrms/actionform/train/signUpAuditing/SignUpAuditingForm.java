package com.hjsj.hrms.actionform.train.signUpAuditing;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SignUpAuditingForm extends FrameForm {
	private PaginationForm studentListForm=new PaginationForm();
	private String[] selected=null;
	private String a0101="";
	private ArrayList trainMovementList=new ArrayList();
	private String    trainMovementID="";
	
	private ArrayList trainMovementList2=new ArrayList();
	private String    trainMovementID2="";
	private String    priv="";
	private String sp_flag="";
	private ArrayList spList = new ArrayList();
	@Override
    public void outPutFormHM() {
		this.getStudentListForm().setList((ArrayList)this.getFormHM().get("studentList"));
		this.setTrainMovementList((ArrayList)this.getFormHM().get("trainMovementList"));
		this.setTrainMovementID((String)this.getFormHM().get("trainMovementID"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setTrainMovementList2((ArrayList)this.getFormHM().get("trainMovementList2"));
		this.setPriv((String)this.getFormHM().get("priv"));
		this.setSp_flag((String)this.getFormHM().get("sp_flag"));
		this.setSpList((ArrayList)this.getFormHM().get("spList"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selected",this.getSelected());
		this.getFormHM().put("a0101",this.getA0101());
		this.getFormHM().put("trainMovementID",this.getTrainMovementID());
		this.getFormHM().put("trainMovementID2",this.getTrainMovementID2());
		this.getFormHM().put("sp_flag", this.getSp_flag());
	}
	
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(arg1.getParameter("operate")!=null&& "init".equals(arg1.getParameter("operate")))
		{
			if(this.getStudentListForm()!=null)
				this.getStudentListForm().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	

	public PaginationForm getStudentListForm() {
		return studentListForm;
	}

	public void setStudentListForm(PaginationForm studentListForm) {
		this.studentListForm = studentListForm;
	}

	public String[] getSelected() {
		return selected;
	}

	public void setSelected(String[] selected) {
		this.selected = selected;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getTrainMovementID() {
		return trainMovementID;
	}

	public void setTrainMovementID(String trainMovementID) {
		this.trainMovementID = trainMovementID;
	}

	public ArrayList getTrainMovementList() {
		return trainMovementList;
	}

	public void setTrainMovementList(ArrayList trainMovementList) {
		this.trainMovementList = trainMovementList;
	}

	public String getTrainMovementID2() {
		return trainMovementID2;
	}

	public void setTrainMovementID2(String trainMovementID2) {
		this.trainMovementID2 = trainMovementID2;
	}

	public ArrayList getTrainMovementList2() {
		return trainMovementList2;
	}

	public void setTrainMovementList2(ArrayList trainMovementList2) {
		this.trainMovementList2 = trainMovementList2;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public ArrayList getSpList() {
		return spList;
	}

	public void setSpList(ArrayList spList) {
		this.spList = spList;
	}
    
}
