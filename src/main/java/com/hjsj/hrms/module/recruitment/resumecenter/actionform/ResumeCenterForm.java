package com.hjsj.hrms.module.recruitment.resumecenter.actionform;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

public class ResumeCenterForm extends FrameForm {
	
	private String sqlstr;    //查询简历中心的sql语句
	private String orderbystr;    //查询简历中心的排序语句
	private String fields;      //表格列相关字段
	private ArrayList groupcolumns;   //表格列字段列表
	private ArrayList queryscheme;  //查询方案栏
	private String constantxml;   //配置文件名称
	private int current=1;     //页码
	private int pagesize=20;    //每页显示的条数
	private String schemeValues;  //应聘情况栏相应值
	private String pageDesc; //获取页面描述信息（current`schemeValues`pagesize连接成的字符串)
	
	private ArrayList usercolumns;   //用户子集添加的列
	private ArrayList datalist;  //用于展现的list数据
	private ArrayList buttonList=new ArrayList();  //工具栏按钮
	
	private String from="resumeCenter";  // resumeCenter:简历中心  talents:人才库

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("groupcolumns", groupcolumns);
		this.getFormHM().put("usercolumns", usercolumns);
		this.getFormHM().put("schemeValues", schemeValues);
		this.getFormHM().put("pageDesc", pageDesc);
	}

	@Override
    public void outPutFormHM() {
		HashMap hm = this.getFormHM();
		this.setSqlstr((String)hm.get("sqlstr"));
		this.setOrderbystr((String)hm.get("orderbystr"));
		this.setFields((String)hm.get("fields"));
		this.setGroupcolumns((ArrayList)hm.get("groupcolumns"));
		this.setQueryscheme((ArrayList)hm.get("queryscheme"));
		this.setUsercolumns((ArrayList)hm.get("usercolumns"));
		this.setConstantxml((String)hm.get("constantxml"));
		this.setSchemeValues((String)hm.get("schemeValues"));
		this.setDatalist((ArrayList)hm.get("datalist"));
		this.setFrom((String)hm.get("from"));
		this.setButtonList((ArrayList)hm.get("buttonList"));
		hm=null;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getOrderbystr() {
		return orderbystr;
	}

	public void setOrderbystr(String orderbystr) {
		this.orderbystr = orderbystr;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public ArrayList getGroupcolumns() {
		return groupcolumns;
	}

	public void setGroupcolumns(ArrayList groupcolumns) {
		this.groupcolumns = groupcolumns;
	}

	public String getConstantxml() {
		return constantxml;
	}

	public void setConstantxml(String constantxml) {
		this.constantxml = constantxml;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public String getSchemeValues() {
		return schemeValues;
	}

	public void setSchemeValues(String schemeValues) {
		this.schemeValues = schemeValues;
	}

	public String getPageDesc() {
		return pageDesc;
	}

	public void setPageDesc(String pageDesc) {
		this.pageDesc = pageDesc;
	}

	public ArrayList getUsercolumns() {
		return usercolumns;
	}

	public void setUsercolumns(ArrayList usercolumns) {
		this.usercolumns = usercolumns;
	}

	public ArrayList getDatalist() {
		return datalist;
	}

	public void setDatalist(ArrayList datalist) {
		this.datalist = datalist;
	}

	public ArrayList getQueryscheme() {
		return queryscheme;
	}

	public void setQueryscheme(ArrayList queryscheme) {
		this.queryscheme = queryscheme;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public ArrayList getButtonList() {
		return buttonList;
	}

	public void setButtonList(ArrayList buttonList) {
		this.buttonList = buttonList;
	}

}
