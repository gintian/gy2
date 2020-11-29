package com.hjsj.hrms.actionform.performance.showkhresult;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectionAnalyseForm extends FrameForm {

	String objectid="";
	String template_id="";
	ArrayList templateList=new ArrayList();	
	HashMap dataMap=new HashMap();
	ChartParameter chartParameter =null; //图形参数
	 
	String    itemLevelID="";
	ArrayList itemLevelList=new ArrayList();   //指标层 集
	String    isTotalScore="0";                //是否包含总分  0：不包含  1：包含
	
	

	
	
	@Override
    public void outPutFormHM() {
		this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameter"));
		this.setItemLevelID((String)this.getFormHM().get("itemLevelID"));
		this.setItemLevelList((ArrayList)this.getFormHM().get("itemLevelList"));
		this.setIsTotalScore((String)this.getFormHM().get("isTotalScore"));
		
		this.setTemplateList((ArrayList)this.getFormHM().get("templateList"));
		this.setTemplate_id((String)this.getFormHM().get("template_id"));
		this.setObjectid((String)this.getFormHM().get("objectid"));
		this.setDataMap((HashMap)this.getFormHM().get("dataMap"));
		
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("isTotalScore",this.getIsTotalScore());
		this.getFormHM().put("itemLevelID",this.getItemLevelID());
		this.getFormHM().put("template_id",this.getTemplate_id());
	}

	public String getObjectid() {
		return objectid;
	}

	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public HashMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}

	public ArrayList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList templateList) {
		this.templateList = templateList;
	}

	public String getIsTotalScore() {
		return isTotalScore;
	}

	public void setIsTotalScore(String isTotalScore) {
		this.isTotalScore = isTotalScore;
	}

	public ArrayList getItemLevelList() {
		return itemLevelList;
	}

	public void setItemLevelList(ArrayList itemLevelList) {
		this.itemLevelList = itemLevelList;
	}

	public String getItemLevelID() {
		return itemLevelID;
	}

	public void setItemLevelID(String itemLevelID) {
		this.itemLevelID = itemLevelID;
	}

	public ChartParameter getChartParameter() {
		return chartParameter;
	}

	public void setChartParameter(ChartParameter chartParameter) {
		this.chartParameter = chartParameter;
	}

}
