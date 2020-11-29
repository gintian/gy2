package com.hjsj.hrms.actionform.performance.batchGrade;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:BatchGrade_SinglePoint_Form.java</p>
 * <p>Description:多人考评单选钮型式</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class BatchGrade_SinglePoint_Form extends FrameForm 
{
	private String batchGradeHtml="";  //打分界面html
	private String point_index="0";
	private ArrayList objectPointValueList=new ArrayList();
	
	private ArrayList objectList=new ArrayList();
	private HashMap object_priv_map=new HashMap();
	
	private String plan_id="";         //计划id
	private String totalNumber="";     //当前模板指标个数
	private String tableWidth="0";     //表宽度
	private String isAllSub="0";       //考核计划下的对象是否全部提交
	
	private String point_id="";
	private String objects_str="";
	private String modelEmail="false";      // 发送邮件标志参数  true:发邮件 false：不发邮件

	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("modelEmail",this.getModelEmail());
		this.getFormHM().put("point_index",this.getPoint_index());
	}


	@Override
    public void outPutFormHM()
	{
		
		this.setModelEmail((String)this.getFormHM().get("modelEmail"));
		this.setObject_priv_map((HashMap)this.getFormHM().get("object_priv_map"));
		this.setObjectList((ArrayList)this.getFormHM().get("objectList"));
		
		this.setIsAllSub((String)this.getFormHM().get("isAllSub"));
		this.setBatchGradeHtml((String)this.getFormHM().get("batchGradeHtml"));
		this.setPoint_index((String)this.getFormHM().get("point_index"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		this.setTotalNumber((String)this.getFormHM().get("totalNumber"));
		this.setTableWidth((String)this.getFormHM().get("tableWidth"));
		
		this.setPoint_id((String)this.getFormHM().get("point_id"));
		this.setObjects_str((String)this.getFormHM().get("objects_str"));
	}

	public String getBatchGradeHtml() {
		return batchGradeHtml;
	}

	public void setBatchGradeHtml(String batchGradeHtml) {
		this.batchGradeHtml = batchGradeHtml;
	}

	public String getPoint_index() {
		return point_index;
	}

	public void setPoint_index(String point_index) {
		this.point_index = point_index;
	}

	public ArrayList getObjectPointValueList() {
		return objectPointValueList;
	}

	public void setObjectPointValueList(ArrayList objectPointValueList) {
		this.objectPointValueList = objectPointValueList;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getTableWidth() {
		return tableWidth;
	}

	public void setTableWidth(String tableWidth) {
		this.tableWidth = tableWidth;
	}

	public String getPoint_id() {
		return point_id;
	}

	public void setPoint_id(String point_id) {
		this.point_id = point_id;
	}

	public String getObjects_str() {
		return objects_str;
	}

	public void setObjects_str(String objects_str) {
		this.objects_str = objects_str;
	}

	public String getIsAllSub() {
		return isAllSub;
	}

	public void setIsAllSub(String isAllSub) {
		this.isAllSub = isAllSub;
	}

	public ArrayList getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList objectList) {
		this.objectList = objectList;
	}

	public HashMap getObject_priv_map() {
		return object_priv_map;
	}

	public void setObject_priv_map(HashMap object_priv_map) {
		this.object_priv_map = object_priv_map;
	}

	public String getModelEmail() {
		return modelEmail;
	}

	public void setModelEmail(String modelEmail) {
		this.modelEmail = modelEmail;
	}

}
