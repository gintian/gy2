package com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:SetUnderlingObjectiveForm.java</p>
 * <p>Description:目标卡状态</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-04-25 14:15:22</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SetUnderlingObjectiveForm extends FrameForm
{

    private String tree;
    private String plan_id;
    private String p_id;
    private PaginationForm personListForm = new PaginationForm();
    private ArrayList statusList  = new ArrayList();
    private String status ;
    private String posid;
    private String a0100;
    private ArrayList planList=new ArrayList();
    private String a0101;
    private String itemid;
    private String itemdesc;
    private ArrayList leaderList = new ArrayList();
    private String level;
    private String objectSpDetailInfo;
    private ArrayList mainbodyList = new ArrayList();
    /**考核方式0-考核关系 1-汇报关系*/
    private String khType;
    /**=1自定义=0考核关系或者汇报关系*/
    private String market;
    private String object_id;
    private String maxLevel;
    private String planStatus;
    private String entranceType;//进入入口：=0为正常的从目标管理进入，=4为首页
    private String object_type;   	

    private ArrayList reverseList = new ArrayList();    // 目标执行情况回顾反查 结果集   
    private String objMainbodys = "";    // 考核对象对应的已回顾的考核主体   
    
    /** 转换页面入口：
     *  =1为正常的从 "目标卡状态页面" 进入，
     *  =2为从 "MBO目标设定及审批统计表" 页面进入，
     *  =3为从 "MBO目标总结考评进度统计表" 页面进入
     */
    private String convertPageEntry="1"; 
    
    private ArrayList mboTableList=new ArrayList();    // "MBO目标设定及审批统计表" 信息List   
    private String checkCycle = "all";                    // 考核周期
    private ArrayList checkCycleList=new ArrayList();  // 考核周期List
    private String changeCycle = "all";                    // 考核周期下的数据
    private ArrayList changeCycleList=new ArrayList();  // 考核周期下的数据List
    private String noYearCycle = "all";                    // 考核周期下非年度的数据
    private ArrayList noYearCycleList=new ArrayList();  // 考核周期下非年度的数据List
	   
    private String startDate;  // 开始时间   
    private String endDate;    // 结束时间
    
    private String sqlStr;    // sql 语句控制条件
    private String orgItemid;    // 节点控制条件	

    private String content="";
    private String subject="";
    private ArrayList deptList = new ArrayList();//部门列表；
    private String deptid;
    private String isTargetCardTemp="false";
    
	/**
     * @return the isTargetCardTemp
     */
    public String getIsTargetCardTemp() {
        return isTargetCardTemp;
    }

    /**
     * @param isTargetCardTemp the isTargetCardTemp to set
     */
    public void setIsTargetCardTemp(String isTargetCardTemp) {
        this.isTargetCardTemp = isTargetCardTemp;
    }

    @Override
    public void inPutTransHM()
	{
		this.getFormHM().put("objMainbodys", this.getObjMainbodys());
		this.getFormHM().put("reverseList", this.getReverseList());
		this.getFormHM().put("deptList", this.getDeptList());
		this.getFormHM().put("deptid", this.getDeptid());
		this.getFormHM().put("orgItemid", this.getOrgItemid());
		this.getFormHM().put("sqlStr", this.getSqlStr());
		this.getFormHM().put("startDate", this.getStartDate());
		this.getFormHM().put("endDate", this.getEndDate());		
		this.getFormHM().put("noYearCycle", this.getNoYearCycle());
		this.getFormHM().put("noYearCycleList", this.getNoYearCycleList());
		this.getFormHM().put("changeCycle", this.getChangeCycle());
		this.getFormHM().put("changeCycleList", this.getChangeCycleList());
		this.getFormHM().put("checkCycle", this.getCheckCycle());
		this.getFormHM().put("checkCycleList", this.getCheckCycleList());
		this.getFormHM().put("mboTableList", this.getMboTableList());		
		this.getFormHM().put("convertPageEntry", this.getConvertPageEntry());
		this.getFormHM().put("entranceType", this.getEntranceType());
		this.getFormHM().put("p_id", this.getP_id());
		this.getFormHM().put("planStatus", this.getPlanStatus());
		this.getFormHM().put("maxLevel", this.getMaxLevel());
		this.getFormHM().put("object_id",this.getObject_id());
		this.getFormHM().put("khType",this.getKhType());
		this.getFormHM().put("market", this.getMarket());
		this.getFormHM().put("level",this.getLevel());
		this.getFormHM().put("itemid", this.getItemid());
		this.getFormHM().put("itemdesc",this.getItemdesc());
		this.getFormHM().put("a0101",this.getA0101());
		this.getFormHM().put("tree",this.getTree());
		this.getFormHM().put("plan_id",this.getPlan_id());
		this.getFormHM().put("selectedList",this.getPersonListForm().getSelectedList());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("posid",this.getPosid());
		this.getFormHM().put("a0100",this.getA0100());
		this.getFormHM().put("isTargetCardTemp", this.getIsTargetCardTemp());
	}
	
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM()
	{
		this.setObjMainbodys((String)this.getFormHM().get("objMainbodys"));
		this.setReverseList((ArrayList)this.getFormHM().get("reverseList"));
		this.setDeptid((String)this.getFormHM().get("deptid"));
		this.setDeptList((ArrayList)this.getFormHM().get("deptList"));
		this.setSubject((String)this.getFormHM().get("subject"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setOrgItemid((String)this.getFormHM().get("orgItemid"));	
		this.setSqlStr((String)this.getFormHM().get("sqlStr"));	
		this.setStartDate((String)this.getFormHM().get("startDate"));	
		this.setEndDate((String)this.getFormHM().get("endDate"));			
		this.setNoYearCycleList((ArrayList)this.getFormHM().get("noYearCycleList"));
		this.setNoYearCycle((String)this.getFormHM().get("noYearCycle"));	
		this.setChangeCycleList((ArrayList)this.getFormHM().get("changeCycleList"));
		this.setChangeCycle((String)this.getFormHM().get("changeCycle"));	
		this.setCheckCycleList((ArrayList)this.getFormHM().get("checkCycleList"));
		this.setCheckCycle((String)this.getFormHM().get("checkCycle"));		
		this.setMboTableList((ArrayList)this.getFormHM().get("mboTableList"));
		this.setConvertPageEntry((String)this.getFormHM().get("convertPageEntry"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setEntranceType((String)this.getFormHM().get("entranceType"));
		this.setP_id((String)this.getFormHM().get("p_id"));
		this.setPlanStatus((String)this.getFormHM().get("planStatus"));
		this.setMaxLevel((String)this.getFormHM().get("maxLevel"));
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setMarket((String)this.getFormHM().get("market"));
	    this.setKhType((String)this.getFormHM().get("khType"));
	    this.setMainbodyList((ArrayList)this.getFormHM().get("mainbodyList"));
		this.setObjectSpDetailInfo((String)this.getFormHM().get("objectSpDetailInfo"));
		this.setLevel((String)this.getFormHM().get("level"));
		this.setItemdesc((String)this.getFormHM().get("itemdesc"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setLeaderList((ArrayList)this.getFormHM().get("leaderList"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		this.setTree((String)this.getFormHM().get("tree"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		this.getPersonListForm().setList((ArrayList)this.getFormHM().get("personList"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setPosid((String)this.getFormHM().get("posid"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setObject_type((String)this.getFormHM().get("object_type"));
		this.setIsTargetCardTemp((String)this.getFormHM().get("isTargetCardTemp"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		//performance/objectiveManage/myObjective/my_objective_list.do?b_init=init&opt=2

		return super.validate(arg0, arg1);
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		if("/performance/objectiveManage/setUnderlingObjective/underling_objective_list".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null)&& "init".equalsIgnoreCase(arg1.getParameter("b_init")))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();      
            if(this.getPersonListForm()!=null)
			{
				this.getPersonListForm().getPagination().firstPage();
			}
        }
		if((arg1.getParameter("b_init")!=null&&("init".equals(arg1.getParameter("b_init"))|| "link".equals(arg1.getParameter("b_init")))))
		{
			if(this.getPersonListForm()!=null)
			{
				this.getPersonListForm().getPagination().firstPage();
			}
		}
		/*if(arg0.getPath().equals("/performance/objectiveManage/setUnderlingObjective/underling_objective_view_list")&&(arg1.getParameter("b_view")!=null)){
			if(this.getPersonListForm()!=null)
			{
				this.getPersonListForm().getPagination().firstPage();
			}
		}*/
		super.reset(arg0, arg1);
	}

	public String getPlan_id()
	{
		return plan_id;
	}

	public void setPlan_id(String plan_id)
	{
		this.plan_id = plan_id;
	}

	public String getTree() 
	{
		return tree;
	}

	public void setTree(String tree) 
	{
		this.tree = tree;
	}

	public PaginationForm getPersonListForm() 
	{
		return personListForm;
	}

	public void setPersonListForm(PaginationForm personListForm)
	{
		this.personListForm = personListForm;
	}

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}

	public ArrayList getStatusList() 
	{
		return statusList;
	}

	public void setStatusList(ArrayList statusList) 
	{
		this.statusList = statusList;
	}

	public String getA0100() 
	{
		return a0100;
	}

	public void setA0100(String a0100) 
	{
		this.a0100 = a0100;
	}

	public String getPosid() 
	{
		return posid;
	}
	
	public void setPosid(String posid) 
	{
		this.posid = posid;
	}

	public ArrayList getPlanList() 
	{
		return planList;
	}

	public void setPlanList(ArrayList planList) 
	{
		this.planList = planList;
	}

	public String getA0101() 
	{
		return a0101;
	}

	public void setA0101(String a0101) 
	{
		this.a0101 = a0101;
	}

	public String getItemid() 
	{
		return itemid;
	}

	public void setItemid(String itemid) 
	{
		this.itemid = itemid;
	}

	public String getItemdesc() 
	{
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) 
	{
		this.itemdesc = itemdesc;
	}

	public ArrayList getLeaderList() 
	{
		return leaderList;
	}

	public void setLeaderList(ArrayList leaderList)
	{
		this.leaderList = leaderList;
	}

	public String getLevel() 
	{
		return level;
	}

	public void setLevel(String level) 
	{
		this.level = level;
	}

	public String getObjectSpDetailInfo() 
	{
		return objectSpDetailInfo;
	}

	public void setObjectSpDetailInfo(String objectSpDetailInfo) 
	{
		this.objectSpDetailInfo = objectSpDetailInfo;
	}

	public ArrayList getMainbodyList() 
	{
		return mainbodyList;
	}

	public void setMainbodyList(ArrayList mainbodyList) 
	{
		this.mainbodyList = mainbodyList;
	}

	public String getKhType() 
	{
		return khType;
	}

	public void setKhType(String khType) 
	{
		this.khType = khType;
	}

	public String getMarket() 
	{
		return market;
	}

	public void setMarket(String market) 
	{
		this.market = market;
	}

	public String getObject_id() 
	{
		return object_id;
	}

	public void setObject_id(String object_id) 
	{
		this.object_id = object_id;
	}

	public String getMaxLevel() 
	{
		return maxLevel;
	}

	public void setMaxLevel(String maxLevel) 
	{
		this.maxLevel = maxLevel;
	}

	public String getPlanStatus() 
	{
		return planStatus;
	}

	public void setPlanStatus(String planStatus) 
	{
		this.planStatus = planStatus;
	}

	public String getP_id() 
	{
		return p_id;
	}

	public void setP_id(String p_id)
	{
		this.p_id = p_id;
	}

	public String getEntranceType() 
	{
		return entranceType;
	}

	public void setEntranceType(String entranceType) 
	{
		this.entranceType = entranceType;
	}
	
	public String getObject_type() 
	{
		return object_type;
	}

	public void setObject_type(String object_type) 
	{
		this.object_type = object_type;
	}
	
	public String getConvertPageEntry() 
	{
		return convertPageEntry;
	}

	public void setConvertPageEntry(String convertPageEntry) 
	{
		this.convertPageEntry = convertPageEntry;
	}

	public ArrayList getMboTableList() 
	{
		return mboTableList;
	}

	public void setMboTableList(ArrayList mboTableList) 
	{
		this.mboTableList = mboTableList;
	}	
	
	public String getCheckCycle() 
	{
		return checkCycle;
	}

	public void setCheckCycle(String checkCycle) 
	{
		this.checkCycle = checkCycle;
	}

	public ArrayList getCheckCycleList() 
	{
		return checkCycleList;
	}

	public void setCheckCycleList(ArrayList checkCycleList) 
	{
		this.checkCycleList = checkCycleList;
	}
	
	public String getChangeCycle() 
	{
		return changeCycle;
	}

	public void setChangeCycle(String changeCycle)
	{
		this.changeCycle = changeCycle;
	}

	public ArrayList getChangeCycleList() 
	{
		return changeCycleList;
	}

	public void setChangeCycleList(ArrayList changeCycleList) 
	{
		this.changeCycleList = changeCycleList;
	}
	
	public String getNoYearCycle() 
	{
		return noYearCycle;
	}

	public void setNoYearCycle(String noYearCycle) 
	{
		this.noYearCycle = noYearCycle;
	}

	public ArrayList getNoYearCycleList() 
	{
		return noYearCycleList;
	}

	public void setNoYearCycleList(ArrayList noYearCycleList) 
	{
		this.noYearCycleList = noYearCycleList;
	}

	public String getStartDate() 
	{
		return startDate;
	}

	public void setStartDate(String startDate) 
	{
		this.startDate = startDate;
	}

	public String getEndDate() 
	{
		return endDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	
	public String getSqlStr() 
	{
		return sqlStr;
	}

	public void setSqlStr(String sqlStr) 
	{
		this.sqlStr = sqlStr;
	}

	public String getOrgItemid() 
	{
		return orgItemid;
	}

	public void setOrgItemid(String orgItemid) 
	{
		this.orgItemid = orgItemid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ArrayList getDeptList() {
		return deptList;
	}

	public void setDeptList(ArrayList deptList) {
		this.deptList = deptList;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public ArrayList getReverseList() {
		return reverseList;
	}

	public void setReverseList(ArrayList reverseList) {
		this.reverseList = reverseList;
	}

	public String getObjMainbodys() {
		return objMainbodys;
	}

	public void setObjMainbodys(String objMainbodys) {
		this.objMainbodys = objMainbodys;
	}
	
}
