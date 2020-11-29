package com.hjsj.hrms.actionform.performance.objectiveManage.objectiveDecision;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:DecPerformanceForm.java</p>
 * <p>Description>:目标卡制定</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 03, 2010 09:00:57 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class DecPerformanceForm extends FrameForm
{

	private PaginationForm planListForm = new PaginationForm(); //考核计划下的考核对象列表
	private ArrayList personList = new ArrayList();  //考核计划下的考核对象的所有信息列表
	private ArrayList statusList = new ArrayList();  //审批状态列表01起草,02已交办,03已办理,07退回,-1全部
	private ArrayList itemSumList = new ArrayList();  //考核计划信息列表 method=2目标计划 and status in(3,5,8)发布、分发、暂停
	
	private String object_type;     //考核对象状态 2人员  非2团队
	private String plan_id;			//考核计划号
	private String posid;			//
	private String a0100;			//考核对象的编号 object_id
    private String status = "-1";   //审批状态默认为"全部"
    private String entranceType;	//
    /** 导入目标 */
    private FormFile file;
    private String onlyFild="";	//考核对象唯一性指标
    
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("plan_id", this.getPlan_id());
		this.getFormHM().put("object_type",this.getObject_type());
		this.getFormHM().put("posid",this.getPosid());
		this.getFormHM().put("a0100",this.getA0100());
		this.getFormHM().put("status",this.getStatus());		
		this.getFormHM().put("entranceType",this.getEntranceType());
		this.getFormHM().put("statusList",this.getStatusList());
		this.getFormHM().put("personList",this.getPersonList());
		this.getFormHM().put("itemSumList",this.getItemSumList());
		this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("onlyFild",this.getOnlyFild());
//		this.getFormHM().put("selectedList",this.getPlanListForm().getSelectedList());
	}

	@Override
    public void outPutFormHM()
	{
		this.setOnlyFild((String)this.getFormHM().get("onlyFild"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		this.setObject_type((String)this.getFormHM().get("object_type"));
		this.setPosid((String)this.getFormHM().get("posid"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setEntranceType((String)this.getFormHM().get("entranceType"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.setPersonList((ArrayList)this.getFormHM().get("personList"));
		this.setItemSumList((ArrayList)this.getFormHM().get("itemSumList"));
		this.setFile((FormFile)this.getFormHM().get("file"));
		this.getPlanListForm().setList((ArrayList)this.getFormHM().get("personList"));		
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/performance/objectiveManage/objectiveDecision/dec_performance_list".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		/*if(arg0.getPath().equals("/performance/objectiveManage/objectiveDecision/dec_performance_list")&&(arg1.getParameter("b_query")!=null))
		{
			if(this.getPlanListForm()!=null)
				this.getPlanListForm().getPagination().firstPage();
		}*/
		
		if("/performance/objectiveManage/objectiveDecision/dec_performance_list".equals(arg0.getPath())
		        &&arg1.getParameter("b_importData")!=null){
		    arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}

		return super.validate(arg0, arg1);
	}

	public PaginationForm getPlanListForm() {
		return planListForm;
	}

	public void setPlanListForm(PaginationForm planListForm) {
		this.planListForm = planListForm;
	}

	public ArrayList getPersonList() {
		return personList;
	}

	public void setPersonList(ArrayList personList) {
		this.personList = personList;
	}

	public ArrayList getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getPosid() {
		return posid;
	}

	public void setPosid(String posid) {
		this.posid = posid;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEntranceType() {
		return entranceType;
	}

	public void setEntranceType(String entranceType) {
		this.entranceType = entranceType;
	}

	public ArrayList getItemSumList() {
		return itemSumList;
	}

	public void setItemSumList(ArrayList itemSumList) {
		this.itemSumList = itemSumList;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getOnlyFild() {
		return onlyFild;
	}

	public void setOnlyFild(String onlyFild) {
		this.onlyFild = onlyFild;
	}
	
}

