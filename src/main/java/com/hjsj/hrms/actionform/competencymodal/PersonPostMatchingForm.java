package com.hjsj.hrms.actionform.competencymodal;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;



public class PersonPostMatchingForm extends FrameForm
{
	
	private String returnURL = "";   //返回路径
	private String postScope;
	private String postScopeDesc;
	private String matchingDegree;
	private ArrayList macthingDegreeList = new ArrayList();
	private ArrayList planList = new ArrayList();	
	private PaginationForm matchingListForm=new PaginationForm();
	
	private String objType;  //  1:岗人匹配 or 2:人岗匹配
	private String objE01A1;  //  岗位编码
	private String planId;
	private String postCode;
	private String object_id;
    private HashMap dataMap = new HashMap();
    private ChartParameter chartParam=new ChartParameter();
    private String degreeflag;//等级标识
    private String degreeGradeId;
    private ArrayList degreeGradeList = new ArrayList();
    
    private String isShowPercentVal="2";//是否按百分比显示分值  0不按百分比，1按百分比，2按等级
    
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("degreeflag", this.getDegreeflag());
		this.getFormHM().put("degreeGradeId", this.getDegreeGradeId());
		this.getFormHM().put("chartParam", this.getChartParam());
		this.getFormHM().put("dataMap", this.getDataMap());
		this.getFormHM().put("postScope", this.getPostScope());
		this.getFormHM().put("matchingDegree", this.getMatchingDegree());
		this.getFormHM().put("planId", this.getPlanId());
		this.getFormHM().put("selectedList",this.getMatchingListForm().getSelectedList());
		this.getFormHM().put("postCode",this.getPostCode());
		this.getFormHM().put("object_id", this.getObject_id());
		this.getFormHM().put("postScopeDesc", this.getPostScopeDesc());
		this.getFormHM().put("objType", this.getObjType());
		this.getFormHM().put("objE01A1", this.getObjE01A1());
		this.getFormHM().put("isShowPercentVal", this.getIsShowPercentVal());
	}

	
	@Override
    public void outPutFormHM()
	{
		this.setDegreeflag((String)this.getFormHM().get("degreeflag"));
		this.setDegreeGradeId((String)this.getFormHM().get("degreeGradeId"));
		this.setDegreeGradeList((ArrayList)this.getFormHM().get("degreeGradeList"));
		this.setPostScopeDesc((String)this.getFormHM().get("postScopeDesc"));
		this.setChartParam((ChartParameter)this.getFormHM().get("chartParam"));
		this.setDataMap((HashMap)this.getFormHM().get("dataMap"));
		this.setPostCode((String)this.getFormHM().get("postCode"));
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setPostScope((String)this.getFormHM().get("postScope"));
		this.setMatchingDegree((String)this.getFormHM().get("matchingDegree"));
		this.setMacthingDegreeList((ArrayList)this.getFormHM().get("macthingDegreeList"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		this.setPlanId((String)this.getFormHM().get("planId"));
		this.getMatchingListForm().setList((ArrayList)this.getFormHM().get("matchingList"));
		this.setObjType((String)this.getFormHM().get("objType"));
		this.setObjE01A1((String)this.getFormHM().get("objE01A1"));
		this.setReturnURL((String)this.getFormHM().get("returnURL"));//add by wangchaoqun on 2014-9-10
		this.setIsShowPercentVal((String)this.getFormHM().get("isShowPercentVal"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		try
		{
			if ("/competencymodal/person_post_matching/person_post_matching".equals(arg0.getPath()) && (arg1.getParameter("b_query") != null ||arg1.getParameter("b_init")!=null))
			{		
				if (this.getMatchingListForm().getPagination() != null)
				{
					this.getMatchingListForm().getPagination().firstPage();
				}								
			}		 
			  
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	}
	 
	 
	public String getPostScope() {
		return postScope;
	}

	public void setPostScope(String postScope) {
		this.postScope = postScope;
	}

	public String getMatchingDegree() {
		return matchingDegree;
	}

	public void setMatchingDegree(String matchingDegree) {
		this.matchingDegree = matchingDegree;
	}

	public ArrayList getMacthingDegreeList() {
		return macthingDegreeList;
	}

	public void setMacthingDegreeList(ArrayList macthingDegreeList) {
		this.macthingDegreeList = macthingDegreeList;
	}

	public ArrayList getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public PaginationForm getMatchingListForm() {
		return matchingListForm;
	}

	public void setMatchingListForm(PaginationForm matchingListForm) {
		this.matchingListForm = matchingListForm;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public HashMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}

	public ChartParameter getChartParam() {
		return chartParam;
	}

	public void setChartParam(ChartParameter chartParam) {
		this.chartParam = chartParam;
	}

	public String getPostScopeDesc() {
		return postScopeDesc;
	}

	public void setPostScopeDesc(String postScopeDesc) {
		this.postScopeDesc = postScopeDesc;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	
	public String getDegreeflag() {
		return degreeflag;
	}

	public void setDegreeflag(String degreeflag) {
		this.degreeflag = degreeflag;
	}

	public String getDegreeGradeId() {
		return degreeGradeId;
	}

	public void setDegreeGradeId(String degreeGradeId) {
		this.degreeGradeId = degreeGradeId;
	}

	public ArrayList getDegreeGradeList() {
		return degreeGradeList;
	}

	public void setDegreeGradeList(ArrayList degreeGradeList) {
		this.degreeGradeList = degreeGradeList;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getObjE01A1() {
		return objE01A1;
	}

	public void setObjE01A1(String objE01A1) {
		this.objE01A1 = objE01A1;
	}

	public String getIsShowPercentVal() {
		return isShowPercentVal;
	}

	public void setIsShowPercentVal(String isShowPercentVal) {
		this.isShowPercentVal = isShowPercentVal;
	}
	
}
