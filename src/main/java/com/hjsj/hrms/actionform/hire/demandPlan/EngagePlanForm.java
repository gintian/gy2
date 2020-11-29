package com.hjsj.hrms.actionform.hire.demandPlan;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;


public class EngagePlanForm extends FrameForm {
	
	private String str_sql="";
	private String str_whl="";
	
	private ArrayList planFieldList=new ArrayList();
	private ArrayList tableHeadList=new ArrayList();
	private String columnName="";
	
	private String z0101="";
	private String z0103="";
	private String z0105="";	
	private String z0105View="";
	private String z0107="";
	private String z0109="";
	private String z0111="";
	private String z0113="";
	private String z0115="";
	private String z0117="";
	private String z0119="";
	private String z0119View="";
	private String z0121="";
	private String z0123="";
	private String z0125="";
	private String z0127="";
	private String z0129="";
	private String selectID="";
	
	private ArrayList hireObjectList=new ArrayList();
	private String operate="";   //a:新增  b：修改
	
	private String unitID="";
	private String infoflag="1";
	private String info="";
	
	//引入职位需求
	private ArrayList fieldList=new ArrayList();  //表列集合
	private ArrayList tableHeadNameList=new ArrayList(); //表头列名
	private ArrayList dateList=new ArrayList();
	private String[]  selectIDs;
	private String order_by;//order sql
	@Override
    public void outPutFormHM() {
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setColumnName((String)this.getFormHM().get("columnName"));
		this.setPlanFieldList((ArrayList)this.getFormHM().get("planFieldList"));
		this.setTableHeadList((ArrayList)this.getFormHM().get("tableHeadList"));
		
		this.setUnitID((String)this.getFormHM().get("unitID"));
		this.setHireObjectList((ArrayList)this.getFormHM().get("hireObjectList"));
		this.setZ0101((String)this.getFormHM().get("z0101"));
		this.setZ0103((String)this.getFormHM().get("z0103"));
		this.setZ0105((String)this.getFormHM().get("z0105"));
		this.setZ0105View((String)this.getFormHM().get("z0105View"));
		this.setZ0107((String)this.getFormHM().get("z0107"));
		this.setZ0109((String)this.getFormHM().get("z0109"));
		this.setZ0111((String)this.getFormHM().get("z0111"));
		this.setZ0113((String)this.getFormHM().get("z0113"));
		this.setZ0115((String)this.getFormHM().get("z0115"));
		this.setZ0117((String)this.getFormHM().get("z0117"));
		this.setZ0119((String)this.getFormHM().get("z0119"));
		this.setZ0119View((String)this.getFormHM().get("z0119View"));
		this.setZ0121((String)this.getFormHM().get("z0121"));
		this.setZ0123((String)this.getFormHM().get("z0123"));
		this.setZ0125((String)this.getFormHM().get("z0125"));
		this.setZ0127((String)this.getFormHM().get("z0127"));
		this.setZ0129((String)this.getFormHM().get("z0129"));
		this.setInfo((String)this.getFormHM().get("info"));
		this.setInfoflag((String)this.getFormHM().get("infoflag"));
		
		if(this.getFormHM().get("operate")!=null&& "a".equals((String)this.getFormHM().get("operate")))
		{
			initForm();
			this.getFormHM().remove("operate");
		}
		
		//招聘计划
		this.setStr_sql((String)this.getFormHM().get("str_sql"));
		this.setStr_whl((String)this.getFormHM().get("str_whl"));
		
		//引入职位需求
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setTableHeadNameList((ArrayList)this.getFormHM().get("tableHeadNameList"));
		this.setDateList((ArrayList)this.getFormHM().get("dateList"));
		
	}

	@Override
    public void inPutTransHM() {
		if(this.getPagination()!=null)
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("order_by",this.getOrder_by());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("planFieldList",this.getPlanFieldList());
		this.getFormHM().put("selectID",this.getSelectID());
		this.getFormHM().put("z0101",this.getZ0101());
		this.getFormHM().put("z0103",this.getZ0103());
		this.getFormHM().put("z0105",this.getZ0105());
		this.getFormHM().put("z0107",this.getZ0107());
		this.getFormHM().put("z0109",this.getZ0109());
		this.getFormHM().put("z0111",this.getZ0111());
		this.getFormHM().put("z0113",this.getZ0113());
		this.getFormHM().put("z0115",this.getZ0115());
		this.getFormHM().put("z0117",this.getZ0117());
		this.getFormHM().put("z0119",this.getZ0119());
		this.getFormHM().put("z0121",this.getZ0121());
		this.getFormHM().put("z0123",this.getZ0123());
		this.getFormHM().put("z0125",this.getZ0125());
		this.getFormHM().put("z0127",this.getZ0127());
		this.getFormHM().put("z0129",this.getZ0129());
		
		this.getFormHM().put("selectIDs",this.getSelectIDs());
	}
	
	
	public void initForm()
	{
		this.setZ0101("");
		this.setZ0103("");
		this.setZ0105("");
		this.setZ0105View("");
		this.setZ0107("");
		this.setZ0109("");
		this.setZ0111("");
		this.setZ0113("");
		this.setZ0115("");
		this.setZ0117("");
		this.setZ0119("");
		this.setZ0119View("");
		this.setZ0121("");
		this.setZ0123("");
		this.setZ0125("");
		this.setZ0127("");
		this.setZ0129("01");
	}

	public String getZ0101() {
		return z0101;
	}

	public void setZ0101(String z0101) {
		this.z0101 = z0101;
	}

	public String getZ0103() {
		return z0103;
	}

	public void setZ0103(String z0103) {
		this.z0103 = z0103;
	}

	public String getZ0105() {
		return z0105;
	}

	public void setZ0105(String z0105) {
		this.z0105 = z0105;
	}

	public String getZ0107() {
		return z0107;
	}

	public void setZ0107(String z0107) {
		this.z0107 = z0107;
	}

	public String getZ0109() {
		return z0109;
	}

	public void setZ0109(String z0109) {
		this.z0109 = z0109;
	}

	public String getZ0111() {
		return z0111;
	}

	public void setZ0111(String z0111) {
		this.z0111 = z0111;
	}

	public String getZ0113() {
		return z0113;
	}

	public void setZ0113(String z0113) {
		this.z0113 = z0113;
	}

	public String getZ0115() {
		return z0115;
	}

	public void setZ0115(String z0115) {
		this.z0115 = z0115;
	}

	public String getZ0117() {
		return z0117;
	}

	public void setZ0117(String z0117) {
		this.z0117 = z0117;
	}

	public String getZ0119() {
		return z0119;
	}

	public void setZ0119(String z0119) {
		this.z0119 = z0119;
	}

	public String getZ0121() {
		return z0121;
	}

	public void setZ0121(String z0121) {
		this.z0121 = z0121;
	}

	public String getZ0123() {
		return z0123;
	}

	public void setZ0123(String z0123) {
		this.z0123 = z0123;
	}

	public String getZ0125() {
		return z0125;
	}

	public void setZ0125(String z0125) {
		this.z0125 = z0125;
	}

	public String getZ0127() {
		return z0127;
	}

	public void setZ0127(String z0127) {
		this.z0127 = z0127;
	}

	public String getZ0129() {
		return z0129;
	}

	public void setZ0129(String z0129) {
		this.z0129 = z0129;
	}

	public String getZ0105View() {
		return z0105View;
	}

	public void setZ0105View(String view) {
		z0105View = view;
	}

	public String getZ0119View() {
		return z0119View;
	}

	public void setZ0119View(String view) {
		z0119View = view;
	}

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

	public ArrayList getDateList() {
		return dateList;
	}

	public void setDateList(ArrayList dateList) {
		this.dateList = dateList;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public ArrayList getTableHeadNameList() {
		return tableHeadNameList;
	}

	public void setTableHeadNameList(ArrayList tableHeadNameList) {
		this.tableHeadNameList = tableHeadNameList;
	}

	public String[] getSelectIDs() {
		return selectIDs;
	}

	public void setSelectIDs(String[] selectIDs) {
		this.selectIDs = selectIDs;
	}

	public String getSelectID() {
		return selectID;
	}

	public void setSelectID(String selectID) {
		this.selectID = selectID;
	}

	public ArrayList getHireObjectList() {
		return hireObjectList;
	}

	public void setHireObjectList(ArrayList hireObjectList) {
		this.hireObjectList = hireObjectList;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getUnitID() {
		return unitID;
	}

	public void setUnitID(String unitID) {
		this.unitID = unitID;
	}

	public ArrayList getPlanFieldList() {
		return planFieldList;
	}

	public void setPlanFieldList(ArrayList planFieldList) {
		this.planFieldList = planFieldList;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public ArrayList getTableHeadList() {
		return tableHeadList;
	}

	public void setTableHeadList(ArrayList tableHeadList) {
		this.tableHeadList = tableHeadList;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getInfoflag() {
		return infoflag;
	}

	public void setInfoflag(String infoflag) {
		this.infoflag = infoflag;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
