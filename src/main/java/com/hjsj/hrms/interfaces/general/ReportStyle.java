/**
 * 
 */
package com.hjsj.hrms.interfaces.general;

/**
 * <p>Title:ReportStyle</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-29:12:01:18</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ReportStyle {
	
	/**上下左右边距*/
	private float left_margin=10;
	private float top_margin=10;
	private float right_margin=10;
	private float bottom_margin=10;
	/**纸张类型及大小*/
	private PaperSize paper=new PaperSize(PaperSize.A4);
	/**标题*/
	private String title;
	/**页头尾*/
	private String head;
	private String tile;
	/**正文*/
	private String body;
	/**行高*/
	private int rowheight=20;
	public float getBottom_margin() {
		return bottom_margin;
	}
	public void setBottom_margin(float bottom_margin) {
		this.bottom_margin = bottom_margin;
	}
	public float getLeft_margin() {
		return left_margin;
	}
	public void setLeft_margin(float left_margin) {
		this.left_margin = left_margin;
	}
	public float getRight_margin() {
		return right_margin;
	}
	public void setRight_margin(float right_margin) {
		this.right_margin = right_margin;
	}
	public float getTop_margin() {
		return top_margin;
	}
	public void setTop_margin(float top_margin) {
		this.top_margin = top_margin;
	}
	public PaperSize getPaper() {
		return paper;
	}
	public void setPaper(PaperSize paper) {
		this.paper = paper;
	}
	
	/**
	 * @param xml_format 报表格式定义
	 */
	public ReportStyle(String xml_format) {
		parseXml(xml_format);
	}
	
	/**
	 * 解释报表格式
	 * @param xml
	 */
	private void parseXml(String xml)
	{
		
	}
	
	
}
