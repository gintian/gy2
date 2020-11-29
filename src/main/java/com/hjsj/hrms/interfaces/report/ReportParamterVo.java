package com.hjsj.hrms.interfaces.report;

public class ReportParamterVo {
	/**考勤报表**/
    //报表名称
    private String name;
    //报表纸张
    private String pagetype;
    //报表长度单位
    private String unit;
    //纸张方向
    private String orientation;
    //报表头边距
    private String top;
    //报表尾边距
    private String bottom;
    //报表左边距
    private String left;
    //报表右边距
    private String right;
    //值
    private String value;
    //纸的长宽
    private String width;
    private String height;
    /***报表页眉***/   
     
    /**报表标题**/
    
    private String title_c;   
    
   
    /**报表表头**/
    private String head_c;  
    
    private String tile_c;
    /**表体**/    
    private String body_c;  
      
	public String getBody_c() {
		return body_c;
	}
	public void setBody_c(String body_c) {
		this.body_c = body_c;
	}
	
	public String getBottom() {
		return bottom;
	}
	public void setBottom(String bottom) {
		this.bottom = bottom;
	}
	public String getHead_c() {
		return head_c;
	}
	public void setHead_c(String head_c) {
		this.head_c = head_c;
	}
	
	
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public String getPagetype() {
		return pagetype;
	}
	public void setPagetype(String pagetype) {
		this.pagetype = pagetype;
	}
	public String getRight() {
		return right;
	}
	public void setRight(String right) {
		this.right = right;
	}
	public String getTile_c() {
		return tile_c;
	}
	public void setTile_c(String tile_c) {
		this.tile_c = tile_c;
	}
	
	
	public String getTitle_c() {
		return title_c;
	}
	public void setTitle_c(String title_c) {
		this.title_c = title_c;
	}
	
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}	
	
	
}
