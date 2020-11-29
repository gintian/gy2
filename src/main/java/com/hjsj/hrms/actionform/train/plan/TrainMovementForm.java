package com.hjsj.hrms.actionform.train.plan;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class TrainMovementForm extends FrameForm {
	private String extendSql;
	private String orderSql;
	
	private String username="";
	private String model="";   //1:计划制定   2：计划审核
	private String linkDesc="";
	private String codeID=""; 
	private String codeSet="";
	private String buttonNames="";
	/**字段列表*/
	private ArrayList fieldlist;	
	/**数据过滤SQL语句*/
	private String sql;
	private String fieldSize="0";
	
	private ArrayList timeConditionList=new ArrayList();
	private String timeFlag="";    //显示时间条件  1：全部   2：本年度   3：本季度  4：本月份 5.某时间段
	private String startTime="";
	private String endTime="";
	
	private ArrayList planStateList=new ArrayList();
	private String stateFlag="";  // 显示状态条件 00：所有状态  01：起草状态 02:以报批  03:已批状态  05:执行中状态  06：结束状态 07：驳回
	/**表名*/
	private String tablename="r31";
	
	private ArrayList trainPlanList=new ArrayList();  //培训计划列表
	private ArrayList ratifyTrainPlanList=new ArrayList(); //已批培训计划列表
	private ArrayList selectedList=new ArrayList();
	private String    trainPlanID="";
	
	
	private String  selectIDs="";
	private ArrayList planFieldList=new ArrayList();
	private String  editFlag="true";  //计划制定模块，操作权限标记
	//单位权限码
	private String orgparentcode;

	//菜单选择状态
	private String timeAll="false";
	private String timeYear="false";
	private String timeQuarter="false";
	private String timeMonth="false";
	private String timeArea="false";
	private String stateAll="false";
	private String stateInception="false";
	private String stateAppeal="false";
	private String stateRatify="false";
	private String stateOngoing="false";
	private String stateFinish="false";
	private String stateOverrule="false";
	private String stateIssue="false";
	private String stateDiscussion="false";
	
	private String contentEv="";
	private String contentid="";
	private String wheresql="";
	private String readonly="";
	
	private String oldmodel="";
	
	private void setEditFlag()
	{
		model=model!=null?model:"";
		if("1".equals(model)&&("01".equals(stateFlag)|| "07".equals(stateFlag)))
			editFlag="true";
		else if("2".equals(model)&&("02".equals(stateFlag)|| "07".equals(stateFlag)))
			editFlag="true";
		else
			editFlag="false";
		
	}
	
	
	private void setState()
	{
		timeAll="false";
		timeYear="false";
		timeQuarter="false";
		timeMonth="false";
		timeArea="false";
		stateAll="false";
		stateInception="false";
		stateRatify="false";
		stateOngoing="false";
		stateFinish="false";
		stateAppeal="false";
		stateOverrule="false";
		stateIssue="false";
		stateDiscussion="false";
		
		if("1".equals(timeFlag))
		{
			timeAll="true";
		}
		else if("2".equals(timeFlag))
		{
			timeYear="true";
		}
		else if("3".equals(timeFlag))
		{
			timeQuarter="true";
		}
		else if("4".equals(timeFlag))
		{
			timeMonth="true";
		}
		else if("5".equals(timeFlag))
		{
			timeArea="true";
		}
		
		if("0".equals(stateFlag))
		{
			stateAll="true";
		}
		else if("01".equals(stateFlag))
		{
			stateInception="true";
		}
		else if("02".equals(stateFlag))
		{
			stateAppeal="true";
		}
		else if("03".equals(stateFlag))
		{
			stateRatify="true";
		}
		else if("04".equals(stateFlag))
		{
			stateIssue="true";
		}
		else if("05".equals(stateFlag))
		{
			stateOngoing="true";
		}
		else if("06".equals(stateFlag))
		{
			stateFinish="true";
		}
		else if("07".equals(stateFlag))
		{
			stateOverrule="true";
		}
		else if("08".equals(stateFlag))
		{
			stateDiscussion="true";
		}	
	}
	
	
	
	@Override
    public void outPutFormHM() {
		
		this.setButtonNames((String)this.getFormHM().get("buttonNames"));
		this.setPlanFieldList((ArrayList)this.getFormHM().get("planFieldList"));
		this.setSelectIDs((String)this.getFormHM().get("selectIDs"));
		this.setSelectedList((ArrayList)this.getFormHM().get("selectedList"));
		
		this.setTrainPlanID((String)this.getFormHM().get("trainPlanID"));
		this.setTrainPlanList((ArrayList)this.getFormHM().get("trainPlanList"));
		this.setRatifyTrainPlanList((ArrayList)this.getFormHM().get("ratifyTrainPlanList"));
		this.setFieldSize((String)this.getFormHM().get("fieldSize"));
		this.setUsername((String)this.getFormHM().get("userName"));
		this.setTimeConditionList((ArrayList)this.getFormHM().get("timeConditionList"));
		this.setPlanStateList((ArrayList)this.getFormHM().get("planStateList"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setLinkDesc((String)this.getFormHM().get("linkDesc"));
		this.setCodeID((String)this.getFormHM().get("codeID"));
		this.setCodeSet((String)this.getFormHM().get("codeSet"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTimeFlag((String)this.getFormHM().get("timeFlag"));
		this.setStateFlag((String)this.getFormHM().get("stateFlag"));
		this.setStartTime((String)this.getFormHM().get("startTime"));
		this.setEndTime((String)this.getFormHM().get("endTime"));
		
		this.setExtendSql((String)this.getFormHM().get("extendSql"));
		this.setOrderSql((String)this.getFormHM().get("orderSql"));
		this.setContentEv((String)this.getFormHM().get("contentEv"));
		this.setContentid((String)this.getFormHM().get("contentid"));
		this.setWheresql((String)this.getFormHM().get("wheresql"));
		this.setReadonly((String)this.getFormHM().get("readonly"));
		
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		
		this.setOldmodel((String)this.getFormHM().get("oldmodel"));
		
		setState();
		setEditFlag();
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("extendSql",this.getExtendSql());
		this.getFormHM().put("orderSql",this.getOrderSql());
		
		this.getFormHM().put("selectIDs",this.getSelectIDs());
		this.getFormHM().put("planFieldList",this.getPlanFieldList());
	
		this.getFormHM().put("trainPlanID",this.getTrainPlanID());
		this.getFormHM().put("timeFlag",this.getTimeFlag());
		this.getFormHM().put("startTime",this.getStartTime());
		this.getFormHM().put("endTime",this.getEndTime());
		this.getFormHM().put("stateFlag",this.getStateFlag());
		this.getFormHM().put("contentEv",this.getContentEv());
		
		this.getFormHM().put("orgparentcode",this.getOrgparentcode());
		this.getFormHM().put("oldmodel", this.getOldmodel());
	}

	public String getCodeID() {
		return codeID;
	}

	public void setCodeID(String codeID) {
		this.codeID = codeID;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getLinkDesc() {
		return linkDesc;
	}

	public void setLinkDesc(String linkDesc) {
		this.linkDesc = linkDesc;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStateFlag() {
		return stateFlag;
	}

	public void setStateFlag(String stateFlag) {
		this.stateFlag = stateFlag;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getTimeFlag() {
		return timeFlag;
	}

	public void setTimeFlag(String timeFlag) {
		this.timeFlag = timeFlag;
	}

	public String getCodeSet() {
		return codeSet;
	}

	public void setCodeSet(String codeSet) {
		this.codeSet = codeSet;
	}

	public ArrayList getPlanStateList() {
		return planStateList;
	}

	public void setPlanStateList(ArrayList planStateList) {
		this.planStateList = planStateList;
	}

	public ArrayList getTimeConditionList() {
		return timeConditionList;
	}

	public void setTimeConditionList(ArrayList timeConditionList) {
		this.timeConditionList = timeConditionList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFieldSize() {
		return fieldSize;
	}

	public void setFieldSize(String fieldSize) {
		this.fieldSize = fieldSize;
	}

	public String getTrainPlanID() {
		return trainPlanID;
	}

	public void setTrainPlanID(String trainPlanID) {
		this.trainPlanID = trainPlanID;
	}

	public ArrayList getTrainPlanList() {
		return trainPlanList;
	}

	public void setTrainPlanList(ArrayList trainPlanList) {
		this.trainPlanList = trainPlanList;
	}

	public ArrayList getPlanFieldList() {
		return planFieldList;
	}

	public void setPlanFieldList(ArrayList planFieldList) {
		this.planFieldList = planFieldList;
	}

	public String getSelectIDs() {
		return selectIDs;
	}

	public void setSelectIDs(String selectIDs) {
		this.selectIDs = selectIDs;
	}

	public String getStateAll() {
		return stateAll;
	}

	public void setStateAll(String stateAll) {
		this.stateAll = stateAll;
	}

	public String getStateFinish() {
		return stateFinish;
	}

	public void setStateFinish(String stateFinish) {
		this.stateFinish = stateFinish;
	}

	public String getStateInception() {
		return stateInception;
	}

	public void setStateInception(String stateInception) {
		this.stateInception = stateInception;
	}

	public String getStateOngoing() {
		return stateOngoing;
	}

	public void setStateOngoing(String stateOngoing) {
		this.stateOngoing = stateOngoing;
	}

	public String getStateRatify() {
		return stateRatify;
	}

	public void setStateRatify(String stateRatify) {
		this.stateRatify = stateRatify;
	}

	public String getTimeAll() {
		return timeAll;
	}

	public void setTimeAll(String timeAll) {
		this.timeAll = timeAll;
	}

	public String getTimeArea() {
		return timeArea;
	}

	public void setTimeArea(String timeArea) {
		this.timeArea = timeArea;
	}

	public String getTimeMonth() {
		return timeMonth;
	}

	public void setTimeMonth(String timeMonth) {
		this.timeMonth = timeMonth;
	}

	public String getTimeQuarter() {
		return timeQuarter;
	}

	public void setTimeQuarter(String timeQuarter) {
		this.timeQuarter = timeQuarter;
	}

	public String getTimeYear() {
		return timeYear;
	}

	public void setTimeYear(String timeYear) {
		this.timeYear = timeYear;
	}


	public String getStateAppeal() {
		return stateAppeal;
	}


	public void setStateAppeal(String stateAppeal) {
		this.stateAppeal = stateAppeal;
	}


	public String getStateOverrule() {
		return stateOverrule;
	}


	public void setStateOverrule(String stateOverrule) {
		this.stateOverrule = stateOverrule;
	}


	public String getEditFlag() {
		return editFlag;
	}


	public void setEditFlag(String editFlag) {
		this.editFlag = editFlag;
	}


	public String getStateIssue() {
		return stateIssue;
	}


	public void setStateIssue(String stateIssue) {
		this.stateIssue = stateIssue;
	}


	public ArrayList getRatifyTrainPlanList() {
		return ratifyTrainPlanList;
	}


	public void setRatifyTrainPlanList(ArrayList ratifyTrainPlanList) {
		this.ratifyTrainPlanList = ratifyTrainPlanList;
	}


	public String getButtonNames() {
		return buttonNames;
	}


	public void setButtonNames(String buttonNames) {
		this.buttonNames = buttonNames;
	}


	public ArrayList getSelectedList() {
		return selectedList;
	}


	public void setSelectedList(ArrayList selectedList) {
		this.selectedList = selectedList;
	}


	public String getStateDiscussion() {
		return stateDiscussion;
	}


	public void setStateDiscussion(String stateDiscussion) {
		this.stateDiscussion = stateDiscussion;
	}


	public String getExtendSql() {
		return extendSql;
	}


	public void setExtendSql(String extendSql) {
		this.extendSql = extendSql;
	}


	public String getOrderSql() {
		return orderSql;
	}


	public void setOrderSql(String orderSql) {
		this.orderSql = orderSql;
	}


	public String getContentEv() {
		return contentEv;
	}


	public void setContentEv(String contentEv) {
		this.contentEv = contentEv;
	}


	public String getContentid() {
		return contentid;
	}


	public void setContentid(String contentid) {
		this.contentid = contentid;
	}


	public String getWheresql() {
		return wheresql;
	}


	public void setWheresql(String wheresql) {
		this.wheresql = wheresql;
	}


	public String getReadonly() {
		return readonly;
	}


	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}


	public String getOrgparentcode() {
		return orgparentcode;
	}


	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}


    public String getOldmodel() {
        return oldmodel;
    }


    public void setOldmodel(String oldmodel) {
        this.oldmodel = oldmodel;
    }
	
}
