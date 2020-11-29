package com.hjsj.hrms.actionform.performance.objectiveManage;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:DesignateTaskForm.java</p>
 * <p>Description>:任务下达</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 27, 2011  4:02:55 PM </p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class DesignateTaskForm extends FrameForm
{
	
	private ArrayList planList = new ArrayList();//考核计划列表
	private ArrayList itemList = new ArrayList();//项目列表
	private PaginationForm objectListForm = new PaginationForm();//考核对象列表
	private ArrayList kpiList = new ArrayList();//下达任务主页面，任务/kpi列表
    private String to_a0100;//下达给某人的人员编号
    private String to_p0407;//下达任务的任务名
    private String p0407;
    private ArrayList myTaskList = new ArrayList();//我的下达任务列表；
    private String queryType;//查询方式=0：按最近天，=1按时间段
    private String laterlyDays;//查询最近天数,默认30天
    private String startDate;//查询时间段的起始日期
    private String endDate;//查询时间段的结束日期
    private String plan_id;//下达任务所在的计划，
    private String objectid;
    private String to_plan_id;
    private String to_itemid;
    private String itemdesc;//任务的表头描述内容，会有不同。
    private String p0400;
    private String taskid;
    private String hiddenStr;
    private String qzfp;//强制分配
    private String fromflag;
    private String p0401;
    private String type;
    private String group_id;
    private String task_type;
    private String returnURL;
	private HashMap leafItemLinkMap = new HashMap();     // 叶子项目对应的继承关系
	private String lay = "0";						// 项目层级
	private HashMap itemPointNum = new HashMap(); // 取得项目拥有的节点数
    
	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("leafItemLinkMap", this.getLeafItemLinkMap());
		this.getFormHM().put("itemPointNum", this.getItemPointNum());
		this.getFormHM().put("lay", this.getLay());		
		this.getFormHM().put("returnURL", this.getReturnURL());
		this.getFormHM().put("task_type", this.getTask_type());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("group_id", this.getGroup_id());
		this.getFormHM().put("fromflag", this.getFromflag());
		this.getFormHM().put("p0401",this.getP0401());
		this.getFormHM().put("qzfp", this.getQzfp());
		this.getFormHM().put("hiddenStr", this.getHiddenStr());
		this.getFormHM().put("to_plan_id", this.getTo_plan_id());
		this.getFormHM().put("to_itemid", this.getTo_itemid());
		this.getFormHM().put("taskid", this.getTaskid());
		this.getFormHM().put("p0407", this.getP0407());
		this.getFormHM().put("plan_id", this.getPlan_id());
		this.getFormHM().put("objectid", this.getObjectid());
		this.getFormHM().put("startDate", this.getStartDate());
		this.getFormHM().put("endDate", this.getEndDate());
		this.getFormHM().put("queryType", this.getQueryType());
		this.getFormHM().put("laterlyDays", this.getLaterlyDays());
		this.getFormHM().put("p0400", this.getP0400());
		this.getFormHM().put("selectedList",this.getObjectListForm().getSelectedList());
	}

	@Override
    public void outPutFormHM()
	{
		
		this.setLeafItemLinkMap((HashMap)this.getFormHM().get("leafItemLinkMap"));
		this.setItemPointNum((HashMap)this.getFormHM().get("itemPointNum"));
		this.setLay((String)this.getFormHM().get("lay"));		
		this.setReturnURL((String)this.getFormHM().get("returnURL"));
		this.setTask_type((String)this.getFormHM().get("task_type"));
		this.setType((String)this.getFormHM().get("type"));
		this.setGroup_id((String)this.getFormHM().get("group_id"));
		this.setFromflag((String)this.getFormHM().get("fromflag"));
		this.setP0401((String)this.getFormHM().get("p0401"));
		this.getObjectListForm().setList((ArrayList)this.getFormHM().get("objectList"));
		this.setItemdesc((String)this.getFormHM().get("itemdesc"));
		this.setKpiList((ArrayList)this.getFormHM().get("kpiList"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		this.setObjectid((String)this.getFormHM().get("objectid"));
		this.setEndDate((String)this.getFormHM().get("endDate"));
		this.setQueryType((String)this.getFormHM().get("queryType"));
		this.setStartDate((String)this.getFormHM().get("startDate"));
		this.setP0407((String)this.getFormHM().get("p0407"));
		this.setTaskid((String)this.getFormHM().get("taskid"));
		this.setTo_plan_id((String)this.getFormHM().get("to_plan_id"));
		this.setTo_itemid((String)this.getFormHM().get("to_itemid"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setP0400((String)this.getFormHM().get("p0400"));
		this.setQzfp((String)this.getFormHM().get("qzfp"));
		
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/performance/objectiveManage/select_object".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null)
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if(arg1.getParameter("b_init")!=null&& "init".equals(arg1.getParameter("b_init")))
			if(this.getObjectListForm()!=null)
				this.getObjectListForm().getPagination().firstPage();
		return super.validate(arg0, arg1);
	}	

	public ArrayList getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}

	public ArrayList getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}

	public PaginationForm getObjectListForm() {
		return objectListForm;
	}

	public void setObjectListForm(PaginationForm objectListForm) {
		this.objectListForm = objectListForm;
	}

	public ArrayList getKpiList() {
		return kpiList;
	}

	public void setKpiList(ArrayList kpiList) {
		this.kpiList = kpiList;
	}

	public String getTo_a0100() {
		return to_a0100;
	}

	public void setTo_a0100(String to_a0100) {
		this.to_a0100 = to_a0100;
	}

	public String getTo_p0407() {
		return to_p0407;
	}

	public void setTo_p0407(String to_p0407) {
		this.to_p0407 = to_p0407;
	}

	public ArrayList getMyTaskList() {
		return myTaskList;
	}

	public void setMyTaskList(ArrayList myTaskList) {
		this.myTaskList = myTaskList;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getLaterlyDays() {
		return laterlyDays;
	}

	public void setLaterlyDays(String laterlyDays) {
		this.laterlyDays = laterlyDays;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getTo_plan_id() {
		return to_plan_id;
	}

	public void setTo_plan_id(String to_plan_id) {
		this.to_plan_id = to_plan_id;
	}

	public String getTo_itemid() {
		return to_itemid;
	}

	public void setTo_itemid(String to_itemid) {
		this.to_itemid = to_itemid;
	}

	public String getItemdesc() {
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}

	public String getObjectid() {
		return objectid;
	}

	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}

	public String getP0407() {
		return p0407;
	}

	public void setP0407(String p0407) {
		this.p0407 = p0407;
	}

	public String getP0400() {
		return p0400;
	}

	public void setP0400(String p0400) {
		this.p0400 = p0400;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getHiddenStr() {
		return hiddenStr;
	}

	public void setHiddenStr(String hiddenStr) {
		this.hiddenStr = hiddenStr;
	}

	public String getQzfp() {
		return qzfp;
	}

	public void setQzfp(String qzfp) {
		this.qzfp = qzfp;
	}

	public String getFromflag() {
		return fromflag;
	}

	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}

	public String getP0401() {
		return p0401;
	}

	public void setP0401(String p0401) {
		this.p0401 = p0401;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getTask_type() {
		return task_type;
	}

	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public HashMap getLeafItemLinkMap() {
		return leafItemLinkMap;
	}

	public void setLeafItemLinkMap(HashMap leafItemLinkMap) {
		this.leafItemLinkMap = leafItemLinkMap;
	}

	public String getLay() {
		return lay;
	}

	public void setLay(String lay) {
		this.lay = lay;
	}

	public HashMap getItemPointNum() {
		return itemPointNum;
	}

	public void setItemPointNum(HashMap itemPointNum) {
		this.itemPointNum = itemPointNum;
	}

}
