package com.hjsj.hrms.actionform.gz.templateset.standard;


import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

public class SalaryStandardDataForm extends FrameForm {
	String item="";
	String item_id="";
	String description="";
	String type="N";  // N,D
	
	String lowerValue="";
	String lowerOperate="";
	ArrayList lowerOperateList=new ArrayList();
	
	String heightValue="";
	String heightOperate="";
	ArrayList heightOperateList=new ArrayList();
	
	String middleValue="";
	ArrayList middleValueList=new ArrayList();
	
	String isAccuratelyDay="0";  //是否精确到天
	
	@Override
    public void outPutFormHM() {
		this.setItem((String)this.getFormHM().get("item"));
		this.setItem_id((String)this.getFormHM().get("item_id"));
		this.setDescription((String)this.getFormHM().get("description"));
		this.setType((String)this.getFormHM().get("type"));
		
		this.setLowerValue((String)this.getFormHM().get("lowerValue"));
		this.setLowerOperate((String)this.getFormHM().get("lowerOperate"));
		this.setLowerOperateList((ArrayList)this.getFormHM().get("lowerOperateList"));
		
		this.setHeightValue((String)this.getFormHM().get("heightValue"));
		this.setHeightOperate((String)this.getFormHM().get("heightOperate"));
		this.setHeightOperateList((ArrayList)this.getFormHM().get("heightOperateList"));
		
		this.setMiddleValue((String)this.getFormHM().get("middleValue"));
		this.setMiddleValueList((ArrayList)this.getFormHM().get("middleValueList"));
		
		this.setIsAccuratelyDay((String)this.getFormHM().get("isAccuratelyDay"));
		
		
	}
	
	@Override
    public void inPutTransHM() {
		HashMap map=this.getFormHM();
		
		map.put("item",this.getItem());
		map.put("item_id",this.getItem_id());
		map.put("description",this.getDescription());
		map.put("type",this.getType());
		map.put("lowerValue",this.getLowerValue());
		map.put("lowerOperate",this.getLowerOperate());
		map.put("heightValue",this.getHeightValue());
		map.put("heightOperate",this.getHeightOperate());
		map.put("middleValue",this.getMiddleValue());
		map.put("isAccuratelyDay",this.getIsAccuratelyDay());		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHeightOperate() {
		return heightOperate;
	}

	public void setHeightOperate(String heightOperate) {
		this.heightOperate = heightOperate;
	}

	public ArrayList getHeightOperateList() {
		return heightOperateList;
	}

	public void setHeightOperateList(ArrayList heightOperateList) {
		this.heightOperateList = heightOperateList;
	}

	public String getHeightValue() {
		return heightValue;
	}

	public void setHeightValue(String heightValue) {
		this.heightValue = heightValue;
	}

	public String getIsAccuratelyDay() {
		return isAccuratelyDay;
	}

	public void setIsAccuratelyDay(String isAccuratelyDay) {
		this.isAccuratelyDay = isAccuratelyDay;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getLowerOperate() {
		return lowerOperate;
	}

	public void setLowerOperate(String lowerOperate) {
		this.lowerOperate = lowerOperate;
	}

	public ArrayList getLowerOperateList() {
		return lowerOperateList;
	}

	public void setLowerOperateList(ArrayList lowerOperateList) {
		this.lowerOperateList = lowerOperateList;
	}

	public String getLowerValue() {
		return lowerValue;
	}

	public void setLowerValue(String lowerValue) {
		this.lowerValue = lowerValue;
	}

	public String getMiddleValue() {
		return middleValue;
	}

	public void setMiddleValue(String middleValue) {
		this.middleValue = middleValue;
	}

	public ArrayList getMiddleValueList() {
		return middleValueList;
	}

	public void setMiddleValueList(ArrayList middleValueList) {
		this.middleValueList = middleValueList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
