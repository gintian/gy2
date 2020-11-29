package com.hjsj.hrms.actionform.general.deci.browser;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

public class MuchFieldAnalyseForm extends FrameForm {
	
	private String itemid; // 指标编号
	private String itemName;
	private String dbFlag; // 库标识（人员 单位 职位）

	private String analyseType; //分析类型 横向/纵向
	private String dbpre;       //库前缀
	private ArrayList dbList = new ArrayList(); //人员库列表
	private String changeFlag;  //指标是否按月变化标识
	private String changeFlagValue;

	private String startYear;   //其始年     
	private String startMonth;  //起始月
	private String endYear;     //终止年
	private String endMonth;    //终止月
	
	private String chartFlag;    //图表显示标始
	private HashMap chartMap = new HashMap(); //图表显示数据集合
	private String chartTitle;	 //图表显示标题
	private String chartType;    //图表显示类型
	
	private ChartParameter chartParameter = null ; //图形参数
	private String chartSets;
	
	@Override
    public void outPutFormHM() {
		// 单击树子节点
		this.setItemid((String) this.getFormHM().get("itemid"));
		this.setDbFlag((String) this.getFormHM().get("dbflag"));
		//按月变化标识
		this.setChangeFlag((String)this.getFormHM().get("changeFlag"));
		this.setChangeFlagValue((String)this.getFormHM().get("changeFlagValue"));
		
		/*System.out.println("**************form********************");
		System.out.println((String) this.getFormHM().get("itemid"));
		System.out.println((String) this.getFormHM().get("dbflag"));
		System.out.println((String)this.getFormHM().get("changeFlag"));
		System.out.println("**************form********************");*/
		
		//人员库列表
		this.setDbList((ArrayList)this.getFormHM().get("dblist"));
		
		//数据图表显示
		String temp = (String)this.getFormHM().get("chartFlag");
		if(temp == null || "".equals(temp)){
			this.setChartFlag("no");
		}else{
			this.setChartFlag((String)this.getFormHM().get("chartFlag"));
			this.setChartTitle((String)this.getFormHM().get("chartTitle"));
			this.setChartType((String)this.getFormHM().get("chartType"));
			this.setChartMap((HashMap)this.getFormHM().get("chartMap"));
			//System.out.println(((HashMap)this.getFormHM().get("chartMap")).size());
			this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameters"));
		}
		this.setItemName((String)this.getFormHM().get("itemName"));
		this.setAnalyseType((String)this.getFormHM().get("analyseType"));	
		this.setChartSets((String)this.getFormHM().get("chartsets"));
	}

	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("dbflag",this.getDbFlag());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("analyseType",this.getAnalyseType());
		this.getFormHM().put("startYear",this.getStartYear());
		this.getFormHM().put("startMonth",this.getStartMonth());
		this.getFormHM().put("endYear",this.getEndYear());
		this.getFormHM().put("endMonth",this.getEndMonth());
		this.getFormHM().put("changeFlag",this.getChangeFlag());
		this.getFormHM().put("changeFlagValue",this.getChangeFlagValue());
	}



	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getDbFlag() {
		return dbFlag;
	}

	public void setDbFlag(String dbFlag) {
		this.dbFlag = dbFlag;
	}

	public ArrayList getDbList() {
		return dbList;
	}

	public void setDbList(ArrayList dbList) {
		this.dbList = dbList;
	}


	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getAnalyseType() {
		return analyseType;
	}

	public void setAnalyseType(String analyseType) {
		this.analyseType = analyseType;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getChartFlag() {
		return chartFlag;
	}

	public void setChartFlag(String chartFlag) {
		this.chartFlag = chartFlag;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public HashMap getChartMap() {
		return chartMap;
	}

	public void setChartMap(HashMap chartMap) {
		this.chartMap = chartMap;
	}

	public String getChangeFlag() {
		return changeFlag;
	}

	public void setChangeFlag(String changeFlag) {
		this.changeFlag = changeFlag;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getChangeFlagValue() {
		return changeFlagValue;
	}

	public void setChangeFlagValue(String changeFlagValue) {
		this.changeFlagValue = changeFlagValue;
	}

	public ChartParameter getChartParameter() {
		return chartParameter;
	}

	public void setChartParameter(ChartParameter chartParameter) {
		this.chartParameter = chartParameter;
	}

	public String getChartSets() {
		return chartSets;
	}

	public void setChartSets(String chartSets) {
		this.chartSets = chartSets;
	}

	
}
