package com.hjsj.hrms.actionform.kq.app_check_in;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class AppRegisterForm extends FrameForm {
	/** *新加复杂申请** */
	private String app_fashion = "";// 申请方式0:简单,1复杂
	private ArrayList class_list = new ArrayList();// 班次的list
	private String class_id = "";
	private String scope_start_time;
	private String scope_end_time;
	private String intricacy_app_start_date;// 复杂申请的开始日期
	private String intricacy_app_end_date;// 复杂申请的结束日期
	private String intricacy_app_start_time_h;// 复杂申请开始时间
	private String intricacy_app_end_time_h;// 复杂申请结束时间
	private String intricacy_app_start_time_m;// 复杂申请开始时间
	private String intricacy_app_end_time_m;// 复杂申请结束时间
	private String intricacy_app_fashion;// 复杂申请方式0:每天一次,1:每公休日一次
	private String easy_app_start_date;// 复杂申请的开始日期
	private String easy_app_end_date;// 复杂申请的结束日期
	private String app_type;// 申请类型
	private ArrayList app_type_list = new ArrayList();
	private String app_reason = "";
	private String table = "";
	private String[] app_dates;
	private String dert_itemid;
	private String dert_value;
	private String date_count;
	private String hr_count;
	private String app_way;
	
	private String appReaCode;//加班原因代码
	private String appReaCodesetid;//加班原因代码项
	private String appReaField;
	
	private String isExistIftoRest;//q11是否存在是否调休字段
	private String IftoRest;//是否调戏

	
	public String getIftoRest() {
		return IftoRest;
	}

	public void setIftoRest(String iftoRest) {
		IftoRest = iftoRest;
	}

	public String getIsExistIftoRest() {
		return isExistIftoRest;
	}

	public void setIsExistIftoRest(String isExistIftoRest) {
		this.isExistIftoRest = isExistIftoRest;
	}

	public String getAppReaCodesetid() {
		return appReaCodesetid;
	}

	public void setAppReaCodesetid(String appReaCodesetid) {
		this.appReaCodesetid = appReaCodesetid;
	}

	public String getAppReaField() {
		return appReaField;
	}

	public void setAppReaField(String appReaField) {
		this.appReaField = appReaField;
	}

	public String getAppReaCode() {
		return appReaCode;
	}

	public void setAppReaCode(String appReaCode) {
		this.appReaCode = appReaCode;
	}

	public String getHr_count() {
		return hr_count;
	}

	public void setHr_count(String hr_count) {
		this.hr_count = hr_count;
	}

	public String getDate_count() {
		return date_count;
	}

	public String getDert_itemid() {
		return dert_itemid;
	}

	public void setDert_itemid(String dert_itemid) {
		this.dert_itemid = dert_itemid;
	}

	public String getDert_value() {
		return dert_value;
	}

	public void setDert_value(String dert_value) {
		this.dert_value = dert_value;
	}

	public String[] getApp_dates() {
		return app_dates;
	}

	public void setApp_dates(String[] app_dates) {
		this.app_dates = app_dates;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		/** *新加复杂申请** */
		this.setApp_fashion((String) this.getFormHM().get("app_fashion"));
		this.setClass_list((ArrayList) this.getFormHM().get("class_list"));
		this.setIntricacy_app_start_date((String) this.getFormHM().get(
				"intricacy_app_start_date"));
		this.setIntricacy_app_end_date((String) this.getFormHM().get(
				"intricacy_app_end_date"));
		this.setIntricacy_app_start_time_h((String) this.getFormHM().get(
				"intricacy_app_start_time_h"));
		this.setIntricacy_app_end_time_h((String) this.getFormHM().get(
				"intricacy_app_end_time_h"));
		this.setIntricacy_app_start_time_m((String) this.getFormHM().get(
				"intricacy_app_start_time_m"));
		this.setIntricacy_app_end_time_m((String) this.getFormHM().get(
				"intricacy_app_end_time_m"));
		this.setIntricacy_app_fashion((String) this.getFormHM().get(
				"intricacy_app_fashion"));
		this.setClass_id((String) this.getFormHM().get("class_id"));
		this.setEasy_app_start_date((String) this.getFormHM().get(
				"easy_app_start_date"));
		this.setEasy_app_end_date((String) this.getFormHM().get(
				"easy_app_end_date"));
		this.setApp_type((String) this.getFormHM().get("app_type"));
		this.setApp_type_list((ArrayList) this.getFormHM().get("app_type_list"));
		this.setApp_reason((String) this.getFormHM().get("app_reason"));
		this.setTable((String) this.getFormHM().get("table"));
		this.setDert_itemid((String) this.getFormHM().get("dert_itemid"));
		this.setScope_start_time((String)this.getFormHM().get("scope_start_time"));
		this.setScope_end_time((String)this.getFormHM().get("scope_end_time"));
		this.setAppReaCode((String)this.getFormHM().get("appReaCode"));
		this.setAppReaCodesetid((String)this.getFormHM().get("appReaCodesetid"));
		this.setAppReaField((String)this.getFormHM().get("appReaField"));
		this.setIsExistIftoRest((String)this.getFormHM().get("isExistIftoRest"));
		this.setIftoRest((String)this.getFormHM().get("IftoRest"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		/** *新加复杂申请** */
		this.getFormHM().put("app_fashion", this.getApp_fashion());
		this.getFormHM().put("intricacy_app_start_date",
				this.getIntricacy_app_start_date());
		this.getFormHM().put("intricacy_app_end_date",
				this.getIntricacy_app_end_date());
		this.getFormHM().put("intricacy_app_start_time_h",
				this.getIntricacy_app_start_time_h());
		this.getFormHM().put("intricacy_app_end_time_h",
				this.getIntricacy_app_end_time_h());
		this.getFormHM().put("intricacy_app_start_time_m",
				this.getIntricacy_app_start_time_m());
		this.getFormHM().put("intricacy_app_end_time_m",
				this.getIntricacy_app_end_time_m());
		this.getFormHM().put("intricacy_app_fashion",
				this.getIntricacy_app_fashion());
		this.getFormHM().put("class_id", this.getClass_id());
		this.getFormHM().put("easy_app_start_date",
				this.getEasy_app_start_date());
		this.getFormHM().put("easy_app_end_date", this.getEasy_app_end_date());
		this.getFormHM().put("app_type", this.getApp_type());
		this.getFormHM().put("app_reason", this.getApp_reason());
		this.getFormHM().put("table", this.getTable());
		this.getFormHM().put("app_dates", this.getApp_dates());
		this.getFormHM().put("dert_value", this.getDert_value());
		this.getFormHM().put("dert_itemid", this.getDert_itemid());
		this.getFormHM().put("scope_start_time", this.getScope_start_time());
		this.getFormHM().put("scope_end_time", this.getScope_end_time());
		this.getFormHM().put("appReaCode",this.appReaCode);
		this.getFormHM().put("appReaCodesetid", this.getAppReaCodesetid());
		this.getFormHM().put("appReaField", this.getAppReaField());
		this.getFormHM().put("isExistIftoRest", this.getIsExistIftoRest());
		this.getFormHM().put("IftoRest", this.getIftoRest());
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setApp_way("0");
	}

	public String getApp_fashion() {
		return app_fashion;
	}

	public void setApp_fashion(String app_fashion) {
		this.app_fashion = app_fashion;
	}

	public String getApp_reason() {
		return app_reason;
	}

	public void setApp_reason(String app_reason) {
		this.app_reason = app_reason;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public ArrayList getApp_type_list() {
		return app_type_list;
	}

	public void setApp_type_list(ArrayList app_type_list) {
		this.app_type_list = app_type_list;
	}

	public String getClass_id() {
		return class_id;
	}

	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}

	public ArrayList getClass_list() {
		return class_list;
	}

	public void setClass_list(ArrayList class_list) {
		this.class_list = class_list;
	}

	public String getEasy_app_end_date() {
		return easy_app_end_date;
	}

	public void setEasy_app_end_date(String easy_app_end_date) {
		this.easy_app_end_date = easy_app_end_date;
	}

	public String getEasy_app_start_date() {
		return easy_app_start_date;
	}

	public void setEasy_app_start_date(String easy_app_start_date) {
		this.easy_app_start_date = easy_app_start_date;
	}

	public String getIntricacy_app_end_date() {
		return intricacy_app_end_date;
	}

	public void setIntricacy_app_end_date(String intricacy_app_end_date) {
		this.intricacy_app_end_date = intricacy_app_end_date;
	}

	public String getIntricacy_app_fashion() {
		return intricacy_app_fashion;
	}

	public void setIntricacy_app_fashion(String intricacy_app_fashion) {
		this.intricacy_app_fashion = intricacy_app_fashion;
	}

	public String getIntricacy_app_start_date() {
		return intricacy_app_start_date;
	}

	public void setIntricacy_app_start_date(String intricacy_app_start_date) {
		this.intricacy_app_start_date = intricacy_app_start_date;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getIntricacy_app_end_time_h() {
		return intricacy_app_end_time_h;
	}

	public void setIntricacy_app_end_time_h(String intricacy_app_end_time_h) {
		this.intricacy_app_end_time_h = intricacy_app_end_time_h;
	}

	public String getIntricacy_app_end_time_m() {
		return intricacy_app_end_time_m;
	}

	public void setIntricacy_app_end_time_m(String intricacy_app_end_time_m) {
		this.intricacy_app_end_time_m = intricacy_app_end_time_m;
	}

	public String getIntricacy_app_start_time_h() {
		return intricacy_app_start_time_h;
	}

	public void setIntricacy_app_start_time_h(String intricacy_app_start_time_h) {
		this.intricacy_app_start_time_h = intricacy_app_start_time_h;
	}

	public String getIntricacy_app_start_time_m() {
		return intricacy_app_start_time_m;
	}

	public void setIntricacy_app_start_time_m(String intricacy_app_start_time_m) {
		this.intricacy_app_start_time_m = intricacy_app_start_time_m;
	}

	public void setDate_count(String date_count) {
		this.date_count = date_count;
	}

	public String getApp_way() {
		return app_way;
	}

	public void setApp_way(String app_way) {
		this.app_way = app_way;
	}

	public String getScope_start_time() {
		return scope_start_time;
	}

	public void setScope_start_time(String scope_start_time) {
		this.scope_start_time = scope_start_time;
	}

	public String getScope_end_time() {
		return scope_end_time;
	}

	public void setScope_end_time(String scope_end_time) {
		this.scope_end_time = scope_end_time;
	}

}
