package com.hjsj.hrms.utils.components.tablefactory.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 表格控件工具栏按钮对象
 * @author guodd
 * @Description:TODO
 * @date 2015-3-23
 */
public class ButtonInfo implements Serializable{

	/**
	 * 分割符
	 */
	public static final String BUTTON_SPLIT = "-";
	/**
	 * 占位符
	 */
	public static final String BUTTON_SPACE = "->";
	
	
	public static final String FNTYPE_INSERT = "insert";
	public static final String FNTYPE_SAVE = "save";
	public static final String FNTYPE_DELETE = "delete";
	public static final String FNTYPE_EXPORT = "export";
	//页面设置
	public static final String FNTYPE_EXPORT_SETTING = "export_Setting";
	public static final String FNTYPE_ANALYSE = "analyse";
	public static final String FNTYPE_SCHEME = "scheme";
	
	
	public static final String TYPE_BUTTON = "button";
	public static final String TYPE_DATE = "date";
	public static final String TYPE_INPUT = "input";
	public static final String TYPE_SPLIT = "split";
	public static final String TYPE_SPACE = "space";
	public static final String TYPE_QUERYBOX = "querybox";
	
	
	
	//组件id
	String id;
	//组件描述
	String text="";
	//图标
	String icon;
    //组件类型(默认为button)
	String type = TYPE_BUTTON;
	// html代码
	String innerHTML;
	//事件触发时是否获取选中数据
	boolean getData=false;
	//事件
	String handler="";
	//是否不可用
	boolean isDisabled = false;
	//参数集合，事件触发时会将此集合中的参数传入handler方法中
	HashMap parameters = new HashMap();
	
	
	String functype;
	String functionId;
	
	boolean showPlanBox = true;
	
	/**
	 * 创建 html 
	 * @param innerHTML
	 */
	public ButtonInfo(String innerHTML){
		this.innerHTML = innerHTML;
	}
	/**
	 * 创建普通button
	 * @param text  button label
 	 * @param handler 监听函数
	 */
	public ButtonInfo(String text,String handler){
		this.text    = text;
		this.handler = handler;
	}
	
    /**
     * 创建功能button 
     * @param text   
     * @param functype   button类型
     * @param functionId 后台交易类号
     */
	public ButtonInfo(String text,String functype,String functionId){
		this.text = text;
		this.functype = functype;
		this.functionId = functionId;
	}
	
	public ButtonInfo(){
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public boolean isGetData() {
		return getData;
	}
	public void setGetData(boolean getData) {
		this.getData = getData;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	
	public boolean isDisabled() {
		return isDisabled;
	}
	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
	public void setParameter(String key,String value){
		this.parameters.put(key, value);
	}
	
	public HashMap getParameterMap(){
		return this.parameters;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInnerHTML() {
		return innerHTML;
	}
	public void setInnerHTML(String innerHTML) {
		this.innerHTML = innerHTML;
	}

	public String getFunctype() {
		return functype;
	}

	public void setFunctype(String functype) {
		this.functype = functype;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}
	
	public boolean isShowPlanBox() {
		return showPlanBox;
	}
	public void setShowPlanBox(boolean showPlanBox) {
		this.showPlanBox = showPlanBox;
	}
	

	
}
