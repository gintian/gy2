package com.hjsj.hrms.actionform.train.trainexam.exam;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:MyExamForm.java
 * </p>
 * <p>
 * Description:我的考试
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-05 13:23:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class MyExamForm extends FrameForm {
   
	// sql语句
	private String sql;
	// where条件
	private String where;
	// 列
	private String cols;
	// 顺序
	private String order;
	// 状态
	private String state;
	// 状态列表
	private ArrayList stateList;
	// 答卷方式
	private String responseType;
	// 答卷方式列表
	private ArrayList responseTypeList;
	// 考试计划名称
	private String planName; 
	//0：非门户 5：门户调用
	private String home;
	//1:主页  0:学习评估中的更多调用
	private String type;
	
	private HashMap timesmap;

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("state", this.getState());
		this.getFormHM().put("responseType", this.getResponseType());
		this.getFormHM().put("planName", this.getPlanName());
		this.getFormHM().put("home", this.getHome());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("timesmap", this.getTimesmap());
    }

    @Override
    public void outPutFormHM() {
    	this.setSql((String) this.getFormHM().get("sql"));
    	this.setWhere((String) this.getFormHM().get("where"));
    	this.setCols((String) this.getFormHM().get("cols"));
    	this.setOrder((String) this.getFormHM().get("order"));
    	this.setState((String) this.getFormHM().get("state"));
    	this.setStateList((ArrayList) this.getFormHM().get("stateList"));
    	this.setResponseType((String) this.getFormHM().get("responseType"));
    	this.setResponseTypeList((ArrayList) this.getFormHM().get("responseTypeList"));
    	this.setPlanName((String) this.getFormHM().get("planName"));
    	this.setHome((String) this.getFormHM().get("home"));
    	this.setType((String) this.getFormHM().get("type"));
    	this.setTimesmap((HashMap)this.getFormHM().get("timesmap"));
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
    	if("/train/resource/myexam".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && arg1.getParameter("first") == null){
	        if(this.getPagination() != null) { 
	        	this.getPagination().firstPage();
	        }
    	}
    	return super.validate(arg0, arg1);
    }


	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ArrayList getStateList() {
		return stateList;
	}

	public void setStateList(ArrayList stateList) {
		this.stateList = stateList;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public ArrayList getResponseTypeList() {
		return responseTypeList;
	}

	public void setResponseTypeList(ArrayList responseTypeList) {
		this.responseTypeList = responseTypeList;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public HashMap getTimesmap() {
        return timesmap;
    }

    public void setTimesmap(HashMap timesmap) {
        this.timesmap = timesmap;
    }
 
}
