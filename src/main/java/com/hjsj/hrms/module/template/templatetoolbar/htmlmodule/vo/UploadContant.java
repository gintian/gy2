package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.vo;
/**
 * 定义常量
 * @author GH
 *
 */
public interface UploadContant {
	
	public String type_divider="divider";//分割线  
	public String type_collapse="collapse";//收缩分割线  
	public String type_text="text";//文本 
	public String type_input="input";//输入框 
	public String type_textarea="textarea";//大文本输入框 
	public String type_radio="radio";//单选框  
	public String type_checkbox="checkbox";//多选框   
	public String type_select="select";//选择器 
	public String type_dateTimePicker="timePicker";//时间选择器  
	public String type_datePicker="datePicker";//日期选择器 
	public String type_table="table";//表格  
	public String type_avatar="avatar";//头像 
	public String type_link="link";//文字链接 
	public String type_upload="upload";//上传附件
	public String type_describe="describe";//describe描述
	public String type_opinion="opinion";//审批意见描述
	public String type_editor="editor";//富文本编辑器
	public String type_describe_blank="describeBlank";//describe描述  无样式
	
	public String htmlMoudle_fileName="htmlmoudle.xls";//定义上传文件名都是htmlmoudle.xls
	//文件路径为：D:\test\multimedia\subdomain\template_4\excelTemp
	//多媒体文件路径\multimedia\subdomain\template_表号\定义的常量excelTempDir
	public String excelTempDir="excelMoudle";
	
	/**人事异动大文本输入框行数 默认值*/
	public int textarea_rows = 20;
	
	//radio 单选框 三种展现形式
	public String radio_show_type_normal = "normal";
	public String radio_show_type_button = "button";
	public String radio_show_type_border = "border";
}