package com.hjsj.hrms.actionform.hire.employActualize;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class EmployPositionForm extends FrameForm {
	private String s_startDate="";
	private String e_startDate="";
	private String s_endDate="";
	private String e_endDate="";
	private String value="";
	private String viewvalue="";
	private String pos_state="04";     //04 已发布 09暂停  06结束
	private String posID="";         //招聘职位id
	private ArrayList posIDList=new ArrayList();
	private ArrayList hirePathList=new ArrayList();
	private String hirePath="";
	
	
	private String isShowCondition="none";  //不显示查询条件  block：显示
	
	private ArrayList orderItemList=new ArrayList();
	private ArrayList orderDescList=new ArrayList();
	private String order_item="z0311";    //排序字段
	private String order_desc="asc";    // asc:升序  desc:降序
	private String posCount="0";        //招聘职位个数
//	private String str_sql="";
//	private String str_whl="";
//	private String order_str="";
	private PaginationForm posDemandListform=new PaginationForm();
	
	private String schoolPosition;
	private String professional="";
	private String isCode="";
	private ArrayList pflist=new ArrayList();
	private ArrayList laborDemandList=new ArrayList();//用工需求下拉表中内容
	private String fielditem1="";
	private String fielditem2="";
	
	private String canshow="";

	@Override
    public void outPutFormHM() {
		this.setSchoolPosition((String)this.getFormHM().get("schoolPosition"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setPosIDList((ArrayList)this.getFormHM().get("posIDList"));
		this.setHirePath((String)this.getFormHM().get("hirePath"));
		this.setLaborDemandList((ArrayList)this.getFormHM().get("laborDemandList"));
		this.setHirePathList((ArrayList)this.getFormHM().get("hirePathList"));
		this.setOrderDescList((ArrayList)this.getFormHM().get("orderDescList"));
		this.setOrderItemList((ArrayList)this.getFormHM().get("orderItemList"));
		this.setPosCount((String)this.getFormHM().get("posCount"));
	//	this.setStr_sql((String)this.getFormHM().get("str_sql"));
	//	this.setStr_whl((String)this.getFormHM().get("str_whl"));
	//	this.setOrder_str((String)this.getFormHM().get("order_str"));
		this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
		this.getPosDemandListform().setList((ArrayList)this.getFormHM().get("posDemandList"));
		
		this.setPosID((String)this.getFormHM().get("posID"));
		this.setValue((String)this.getFormHM().get("value"));
		this.setViewvalue((String)this.getFormHM().get("viewValue"));
		this.setPos_state((String)this.getFormHM().get("pos_state"));
		this.setS_startDate((String)this.getFormHM().get("s_startDate"));
		this.setE_startDate((String)this.getFormHM().get("e_startDate"));
		this.setS_endDate((String)this.getFormHM().get("s_endDate"));
		this.setE_endDate((String)this.getFormHM().get("e_endDate"));
		this.setIsCode((String)this.getFormHM().get("isCode"));
		this.setPflist((ArrayList)this.getFormHM().get("pflist"));
		this.setCanshow((String)this.getFormHM().get("canshow"));
		this.setFielditem1((String)this.getFormHM().get("fielditem1"));
		this.setFielditem2((String)this.getFormHM().get("fielditem2"));
	}

	@Override
    public void inPutTransHM() {
	//	if(this.getPagination()!=null&&this.getPagination().getSelectedList()!=null)
	//		this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("schoolPosition", this.getSchoolPosition());
		this.getFormHM().put("selectedlist",
				this.getPosDemandListform().getSelectedList());	
		this.getFormHM().put("hirePath", this.getHirePath());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("s_startDate",this.getS_startDate());
		this.getFormHM().put("e_startDate",this.getE_startDate());
		this.getFormHM().put("s_endDate",this.getS_endDate());
		this.getFormHM().put("e_endDate",this.getE_endDate());
		this.getFormHM().put("value",this.getValue());
		this.getFormHM().put("viewvalue",this.getViewvalue());
		this.getFormHM().put("pos_state",this.getPos_state());
		this.getFormHM().put("posID",this.getPosID());
		this.getFormHM().put("order_item",this.getOrder_item());
		this.getFormHM().put("order_desc",this.getOrder_desc());
		
		this.getFormHM().put("isShowCondition",this.getIsShowCondition());
	    this.getFormHM().put("professional", this.getProfessional());	
	    this.getFormHM().put("fielditem1", this.getFielditem1());	
	    this.getFormHM().put("fielditem2", this.getFielditem2());	
	    
	}

	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(arg1.getParameter("operate")!=null&& "init".equals(arg1.getParameter("operate")))
		{
			if(this.getPosDemandListform()!=null)
				this.getPosDemandListform().getPagination().firstPage();
		}
		if(arg1.getParameter("isStart")!=null&& "init".equals(arg1.getParameter("isStart")))
		{
			if(this.getPosDemandListform()!=null)
				this.getPosDemandListform().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	
	public String getE_endDate() {
		return e_endDate;
	}

	public void setE_endDate(String date) {
		e_endDate = date;
	}

	public String getCanshow() {
		return canshow;
	}

	public void setCanshow(String canshow) {
		this.canshow = canshow;
	}

	public String getE_startDate() {
		return e_startDate;
	}

	public void setE_startDate(String date) {
		e_startDate = date;
	}

	public String getOrder_desc() {
		return order_desc;
	}

	public void setOrder_desc(String order_desc) {
		this.order_desc = order_desc;
	}

	public String getOrder_item() {
		return order_item;
	}

	public void setOrder_item(String order_item) {
		this.order_item = order_item;
	}

	
	public String getPos_state() {
		return pos_state;
	}

	public void setPos_state(String pos_state) {
		this.pos_state = pos_state;
	}

	public String getPosID() {
		return posID;
	}

	public void setPosID(String posID) {
		this.posID = posID;
	}

	public String getS_endDate() {
		return s_endDate;
	}

	public void setS_endDate(String date) {
		s_endDate = date;
	}

	public String getS_startDate() {
		return s_startDate;
	}

	public void setS_startDate(String date) {
		s_startDate = date;
	}
/*
	public String getStr_sql() {
		return str_sql;
	}

	public void setStr_sql(String str_sql) {
		this.str_sql = str_sql;
	}

	public String getStr_whl() {
		return str_whl;
	}

	public void setStr_whl(String str_whl) {
		this.str_whl = str_whl;
	}
*/
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getViewvalue() {
		return viewvalue;
	}

	public void setViewvalue(String viewvalue) {
		this.viewvalue = viewvalue;
	}

	public String getIsShowCondition() {
		return isShowCondition;
	}

	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}
/*
	public String getOrder_str() {
		return order_str;
	}

	public void setOrder_str(String order_str) {
		this.order_str = order_str;
	}
*/
	public String getPosCount() {
		return posCount;
	}

	public void setPosCount(String posCount) {
		this.posCount = posCount;
	}

	public ArrayList getOrderDescList() {
		return orderDescList;
	}

	public void setOrderDescList(ArrayList orderDescList) {
		this.orderDescList = orderDescList;
	}

	public ArrayList getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(ArrayList orderItemList) {
		this.orderItemList = orderItemList;
	}

	public PaginationForm getPosDemandListform() {
		return posDemandListform;
	}

	public void setPosDemandListform(PaginationForm posDemandListform) {
		this.posDemandListform = posDemandListform;
	}

	public ArrayList getPosIDList() {
		return posIDList;
	}

	public void setPosIDList(ArrayList posIDList) {
		this.posIDList = posIDList;
	}

	public ArrayList getHirePathList() {
		return hirePathList;
	}

	public void setHirePathList(ArrayList hirePathList) {
		this.hirePathList = hirePathList;
	}

	public String getHirePath() {
		return hirePath;
	}

	public void setHirePath(String hirePath) {
		this.hirePath = hirePath;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

	public String getProfessional() {
		return professional;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	public String getIsCode() {
		return isCode;
	}

	public void setIsCode(String isCode) {
		this.isCode = isCode;
	}

	public ArrayList getPflist() {
		return pflist;
	}

	public void setPflist(ArrayList pflist) {
		this.pflist = pflist;
	}

	public ArrayList getLaborDemandList() {
		return laborDemandList;
	}

	public void setLaborDemandList(ArrayList laborDemandList) {
		this.laborDemandList = laborDemandList;
	}

	public String getFielditem1() {
		return fielditem1;
	}

	public void setFielditem1(String fielditem1) {
		this.fielditem1 = fielditem1;
	}

	public String getFielditem2() {
		return fielditem2;
	}

	public void setFielditem2(String fielditem2) {
		this.fielditem2 = fielditem2;
	}

}
