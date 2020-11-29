package com.hjsj.hrms.actionform.performance.markStatus;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <p>Title:markStatusForm.java</p>
 * <p>Description:展示打分状态</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 09:41:14</p>
 * @author JinChunhai
 * @version 1.0
 */

public class markStatusForm extends FrameForm 
{
	
	 private PaginationForm markStatusListform=new PaginationForm();
	 /**当前页*/
	 private int current=1;								   
	 private ArrayList checkPlanList=new ArrayList();     //考核计划列表
	 private String checkPlanId="";					   //考核计划
	 private String description="";                    //不打分原因
	 private String isNoMark="";                       //是否不打分  1：不打分  0：打分
	 private String status="";                        //状态        1:可以修改状态   0：不可以修改状态
	 private String performanceType="0";			   //考核形式  0：绩效考核  1：民主评测	
	 private String model="1";                       // 0：绩效考核  1：民主评测
	 private String descctrl="";                     //=0(为空或等0)时，匿名 =1记名
	 private String plan_type="1";                   // 计划 0:不记名  1:记名（default）
	 private String method="1";                      //1:360度考核 2：目标考核
	 
	 private String object_type="";					// 1:部门  2:人员
	 private ArrayList fashionList=new ArrayList();
	 private String selectFashion="1";                   // 查询方式 1:按考核主体  2:考核对象
	 private String department="0";					  //部门
	 private ArrayList departmentList=new ArrayList();    //部门列表
	 private String name="";                             //姓名
	 
	 private String isFlag="0";                          //0:无目标,无报告 1:有报告无目标 2有目标 有报告
	 private ArrayList submitList = new ArrayList();
	 private String submitid;
	 private String consoleType;//进入平台标志，=1业务平台，=2自助平台
	 
	 
	 /**考评进度统计表参数*/
	 private LinkedHashMap personScoreMap = new LinkedHashMap(); // 统计数据
	 private String planName="";		// 考核计划名称
	 private String e0122Level="all";		   // 部门层级
	 private ArrayList e0122LevelList=new ArrayList();		// 部门层级List
	 private String scoreType="all";		  // 评分状态
	 private ArrayList scoreTypeList=new ArrayList();		// 评分状态List
	 private PaginationForm personListForm=new PaginationForm(); 
	 private ArrayList reverseResultList=new ArrayList();    //反查结果
	 private String b0110="";		// 单位
	 private String e0122="";		// 部门
	 private String type="";		// 打分状态
	 private String checkall="0";
	 
	 
	 @Override
     public void outPutFormHM()
	 {
		 
		 this.setCheckall((String)this.getFormHM().get("checkall"));
		 this.setB0110((String)this.getFormHM().get("b0110"));
		 this.setE0122((String)this.getFormHM().get("e0122"));
		 this.setType((String)this.getFormHM().get("type"));
		 this.setPersonScoreMap((LinkedHashMap)this.getFormHM().get("personScoreMap"));
		 this.setPlanName((String)this.getFormHM().get("planName"));
		 this.setE0122Level((String)this.getFormHM().get("e0122Level"));
		 this.setE0122LevelList((ArrayList)this.getFormHM().get("e0122LevelList"));
		 this.setScoreType((String)this.getFormHM().get("scoreType"));
		 this.setScoreTypeList((ArrayList)this.getFormHM().get("scoreTypeList"));
		 this.setReverseResultList((ArrayList)this.getFormHM().get("reverseResultList"));
		 this.getPersonListForm().setList((ArrayList)this.getFormHM().get("reverseResultList"));
		 this.getPersonListForm().getPagination().gotoPage(current);
		 
		 this.setConsoleType((String)this.getFormHM().get("consoleType"));
		 this.setPlan_type((String)this.getFormHM().get("plan_type"));
		 this.setSubmitid((String)this.getFormHM().get("submitid"));
		 this.setSubmitList((ArrayList)this.getFormHM().get("submitList"));
		 this.setSelectFashion((String)this.getFormHM().get("selectFashion"));
		 this.setDescctrl((String)this.getFormHM().get("descctrl"));
		 this.setModel((String)this.getFormHM().get("model"));
		 this.setPerformanceType((String)this.getFormHM().get("performanceType"));
		 this.getMarkStatusListform().setList((ArrayList)this.getFormHM().get("markStatusList"));
		 this.getMarkStatusListform().getPagination().gotoPage(current);
		 this.setCheckPlanList((ArrayList)this.getFormHM().get("checkPlanList"));
		 this.setCheckPlanId((String)this.getFormHM().get("checkPlanId"));
		 this.setIsNoMark((String)this.getFormHM().get("isNoMark"));
		 this.setDescription((String)this.getFormHM().get("description"));
		 this.setStatus((String)this.getFormHM().get("status"));		
		 this.setObject_type((String)this.getFormHM().get("object_type"));
		 this.setFashionList((ArrayList)this.getFormHM().get("fashionList"));
		 this.setName((String)this.getFormHM().get("name"));
		 this.setDepartmentList((ArrayList)this.getFormHM().get("departmentList"));		
		 this.setIsFlag((String)this.getFormHM().get("isFlag"));
		 this.setMethod((String)this.getFormHM().get("method"));
	 }

	 @Override
     public void inPutTransHM()
	 {
		 
		 this.getFormHM().put("checkall", this.getCheckall());
		 this.getFormHM().put("b0110", this.getB0110());
		 this.getFormHM().put("e0122", this.getE0122());
		 this.getFormHM().put("type", this.getType());
		 this.getFormHM().put("personScoreMap", this.getPersonScoreMap());
		 this.getFormHM().put("planName", this.getPlanName());
		 this.getFormHM().put("e0122Level", this.getE0122Level());
		 this.getFormHM().put("e0122LevelList", this.getE0122LevelList());
		 this.getFormHM().put("scoreType", this.getScoreType());
		 this.getFormHM().put("scoreTypeList", this.getScoreTypeList());
		 this.getFormHM().put("reverseResultList", this.getReverseResultList());
		 
		 this.getFormHM().put("consoleType", this.getConsoleType());
		 this.getFormHM().put("submitid", this.getSubmitid());
		 this.getFormHM().put("selectFashion",this.getSelectFashion());
		 this.getFormHM().put("department",this.getDepartment());
		 this.getFormHM().put("name",this.getName());		
		 this.getFormHM().put("description",this.getDescription());
		 this.getFormHM().put("isNoMark",this.getIsNoMark());
		 this.getFormHM().put("checkPlanId",this.getCheckPlanId());
	 }
	
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
		 if("/performance/markStatus/markStatusList".equals(arg0.getPath()) && (arg1.getParameter("b_search")!=null) && arg1.getParameter("firstpage")!=null)
		 {
			 /**定位到首页,*/
			 if(this.getPagination()!=null)
				 this.getPagination().firstPage();              
		 }
		 if("/performance/markStatus/markStatusList".equals(arg0.getPath()) && (arg1.getParameter("b_search")!=null) && arg1.getParameter("firstpage")!=null)
		 {
			 if(this.getMarkStatusListform()!=null)
				 this.getMarkStatusListform().getPagination().firstPage();
		 }
		 
		 if("/performance/markStatus/reverseResultList".equals(arg0.getPath()) && (arg1.getParameter("b_reverse")!=null) && arg1.getParameter("firstpage")!=null)
		 {
			 /**定位到首页,*/
			 if(this.getPagination()!=null)
				 this.getPagination().firstPage();              
		 }
		 if("/performance/markStatus/reverseResultList".equals(arg0.getPath()) && (arg1.getParameter("b_reverse")!=null) && arg1.getParameter("firstpage")!=null)
		 {
			 if(this.getPersonListForm()!=null)
				 this.getPersonListForm().getPagination().firstPage();
		 }
		 return super.validate(arg0, arg1);
	 }
	

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public PaginationForm getMarkStatusListform() {
		return markStatusListform;
	}

	public void setMarkStatusListform(PaginationForm markStatusListform) {
		this.markStatusListform = markStatusListform;
	}

	public String getCheckPlanId() {
		return checkPlanId;
	}

	public void setCheckPlanId(String checkPlanId) {
		this.checkPlanId = checkPlanId;
	}

	public ArrayList getCheckPlanList() {
		return checkPlanList;
	}

	public void setCheckPlanList(ArrayList checkPlanList) {
		this.checkPlanList = checkPlanList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsNoMark() {
		return isNoMark;
	}

	public void setIsNoMark(String isNoMark) {
		this.isNoMark = isNoMark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPerformanceType() {
		return performanceType;
	}

	public void setPerformanceType(String performanceType) {
		this.performanceType = performanceType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public ArrayList getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(ArrayList departmentList) {
		this.departmentList = departmentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSelectFashion() {
		return selectFashion;
	}

	public void setSelectFashion(String selectFashion) {
		this.selectFashion = selectFashion;
	}

	public void setFashionList(ArrayList fashionList) {
		this.fashionList = fashionList;
	}

	public ArrayList getFashionList() {
		return fashionList;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getDescctrl() {
		return descctrl;
	}

	public void setDescctrl(String descctrl) {
		this.descctrl = descctrl;
	}

	public String getIsFlag() {
		return isFlag;
	}

	public void setIsFlag(String isFlag) {
		this.isFlag = isFlag;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ArrayList getSubmitList() {
		return submitList;
	}

	public void setSubmitList(ArrayList submitList) {
		this.submitList = submitList;
	}

	public String getSubmitid() {
		return submitid;
	}

	public void setSubmitid(String submitid) {
		this.submitid = submitid;
	}

	public String getPlan_type() {
		return plan_type;
	}

	public void setPlan_type(String plan_type) {
		this.plan_type = plan_type;
	}

	public String getConsoleType() {
		return consoleType;
	}

	public void setConsoleType(String consoleType) {
		this.consoleType = consoleType;
	}	

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getE0122Level() {
		return e0122Level;
	}

	public void setE0122Level(String level) {
		e0122Level = level;
	}

	public ArrayList getE0122LevelList() {
		return e0122LevelList;
	}

	public void setE0122LevelList(ArrayList levelList) {
		e0122LevelList = levelList;
	}

	public String getScoreType() {
		return scoreType;
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}

	public ArrayList getScoreTypeList() {
		return scoreTypeList;
	}

	public void setScoreTypeList(ArrayList scoreTypeList) {
		this.scoreTypeList = scoreTypeList;
	}

	public ArrayList getReverseResultList() {
		return reverseResultList;
	}

	public void setReverseResultList(ArrayList reverseResultList) {
		this.reverseResultList = reverseResultList;
	}

	public PaginationForm getPersonListForm() {
		return personListForm;
	}

	public void setPersonListForm(PaginationForm personListForm) {
		this.personListForm = personListForm;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getE0122() {
		return e0122;
	}

	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCheckall() {
		return checkall;
	}

	public void setCheckall(String checkall) {
		this.checkall = checkall;
	}

	public LinkedHashMap getPersonScoreMap() {
		return personScoreMap;
	}

	public void setPersonScoreMap(LinkedHashMap personScoreMap) {
		this.personScoreMap = personScoreMap;
	}

}
