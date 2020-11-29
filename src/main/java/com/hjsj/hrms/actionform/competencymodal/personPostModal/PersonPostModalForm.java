package com.hjsj.hrms.actionform.competencymodal.personPostModal;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:PersonPostModalForm.java</p>
 * <p>Description:人岗匹配、岗人匹配</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-01-11 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class PersonPostModalForm extends FrameForm
{
	private String planId;	//考核计划号
    private String flag = "0"; // 机构树默认不加载人员信息
    private String loadtype = "0"; // 机构树默认加载到职位      
    /** 人员唯一性指标 */
    private String onlyFild = "";
    // 库前缀
    private String userbase = "usr";
    // 机构树编号
    private String orgCode = "";       
    // list页面用
    private int current = 1;		
    private PaginationForm personListForm = new PaginationForm();
    private ArrayList setlist = new ArrayList();
    
    private String plan_id;	//考核计划号
    private ArrayList planList = new ArrayList();
    
    private String subSetMenu;	//代码类指标
    private ArrayList subSetMenuList = new ArrayList();
    private String layer = "1";	//代码类指标层级
    private ArrayList codeItemList = new ArrayList(); // 代码类指标层级
    
    private ArrayList dataList = new ArrayList();	
	private String chart_type = "20";   //图形样式20饼图
	private String chartHeight = "550";
	private String chartWidth = "600";
	private String isShow3D = "0";     //是否显示立体图
	private ArrayList perDegreeList = new ArrayList(); // 选中等级分类下的等级
	
	
    @Override
    public void inPutTransHM()
    {  	
    	
    	this.getFormHM().put("onlyFild", this.getOnlyFild());  
    	this.getFormHM().put("perDegreeList", this.getPerDegreeList());   
    	this.getFormHM().put("isShow3D",this.getIsShow3D());
    	this.getFormHM().put("chartWidth",this.getChartWidth());
    	this.getFormHM().put("chartHeight",this.getChartHeight());
    	this.getFormHM().put("chart_type", this.getChart_type());
    	this.getFormHM().put("dataList", this.getDataList());   
    	this.getFormHM().put("plan_id", this.getPlan_id());
    	this.getFormHM().put("planList", this.getPlanList());  
    	this.getFormHM().put("subSetMenu", this.getSubSetMenu());
    	this.getFormHM().put("subSetMenuList", this.getSubSetMenuList());
    	this.getFormHM().put("layer", this.getLayer());
    	this.getFormHM().put("codeItemList", this.getCodeItemList());
    	this.getFormHM().put("orgCode", this.getOrgCode());
    	this.getFormHM().put("userbase", this.getUserbase());
    	this.getFormHM().put("loadtype", this.getLoadtype());
		this.getFormHM().put("flag", this.getFlag());  
		this.getFormHM().put("planId", this.getPlanId());  
    }
    
    @Override
    public void outPutFormHM()
    {	
    	
    	this.setOnlyFild((String) this.getFormHM().get("onlyFild")); 
    	this.setPerDegreeList((ArrayList) this.getFormHM().get("perDegreeList"));   
    	this.setIsShow3D((String)this.getFormHM().get("isShow3D"));
    	this.setChartHeight((String)this.getFormHM().get("chartHeight"));
		this.setChartWidth((String)this.getFormHM().get("chartWidth"));
    	this.setChart_type((String) this.getFormHM().get("chart_type"));  
    	this.setDataList((ArrayList) this.getFormHM().get("dataList"));   
    	this.setPlan_id((String) this.getFormHM().get("plan_id"));  
    	this.setPlanList((ArrayList) this.getFormHM().get("planList"));  
    	this.setSubSetMenu((String) this.getFormHM().get("subSetMenu"));  
    	this.setSubSetMenuList((ArrayList) this.getFormHM().get("subSetMenuList")); 
    	this.setLayer((String) this.getFormHM().get("layer"));
    	this.setCodeItemList((ArrayList) this.getFormHM().get("codeItemList"));
    	this.setOrgCode((String) this.getFormHM().get("orgCode"));  
    	this.getPersonListForm().setList((ArrayList) this.getFormHM().get("setlist"));
    	this.getPersonListForm().getPagination().gotoPage(current);
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setUserbase((String) this.getFormHM().get("userbase"));
		this.setLoadtype((String) this.getFormHM().get("loadtype"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setPlanId((String) this.getFormHM().get("planId"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/competencymodal/personPostModal/reverseResultList".equals(arg0.getPath()) && arg1.getParameter("b_reverse") != null && (!"link".equals(arg1.getParameter("b_reverse"))))
		    {		
				if (this.personListForm.getPagination() != null)
				{
				    this.personListForm.getPagination().firstPage();
				}								
		    }		   
		    
		    if ("/competencymodal/personPostModal/orgTree".equals(arg0.getPath()) && (arg1.getParameter("b_query") != null) && (arg1.getParameter("b_query").trim().length()>0) && (!"link".equals(arg1.getParameter("b_query"))))
		    {
		    //	this.setSubSetMenu("-1");	
		    //	this.setLayer("");	
		    }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getLoadtype() {
		return loadtype;
	}

	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}

	public String getUserbase() {
		return userbase;
	}

	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public ArrayList getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}

	public ArrayList getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}

	public String getChart_type() {
		return chart_type;
	}

	public void setChart_type(String chart_type) {
		this.chart_type = chart_type;
	}

	public String getChartHeight() {
		return chartHeight;
	}

	public void setChartHeight(String chartHeight) {
		this.chartHeight = chartHeight;
	}

	public String getChartWidth() {
		return chartWidth;
	}

	public void setChartWidth(String chartWidth) {
		this.chartWidth = chartWidth;
	}

	public String getIsShow3D() {
		return isShow3D;
	}

	public void setIsShow3D(String isShow3D) {
		this.isShow3D = isShow3D;
	}

	public ArrayList getPerDegreeList() {
		return perDegreeList;
	}

	public void setPerDegreeList(ArrayList perDegreeList) {
		this.perDegreeList = perDegreeList;
	}

	public PaginationForm getPersonListForm() {
		return personListForm;
	}

	public void setPersonListForm(PaginationForm personListForm) {
		this.personListForm = personListForm;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getOnlyFild() {
		return onlyFild;
	}

	public void setOnlyFild(String onlyFild) {
		this.onlyFild = onlyFild;
	}

	public String getSubSetMenu() {
		return subSetMenu;
	}

	public void setSubSetMenu(String subSetMenu) {
		this.subSetMenu = subSetMenu;
	}

	public ArrayList getSubSetMenuList() {
		return subSetMenuList;
	}

	public void setSubSetMenuList(ArrayList subSetMenuList) {
		this.subSetMenuList = subSetMenuList;
	}

	public ArrayList getCodeItemList() {
		return codeItemList;
	}

	public void setCodeItemList(ArrayList codeItemList) {
		this.codeItemList = codeItemList;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}    
    
}