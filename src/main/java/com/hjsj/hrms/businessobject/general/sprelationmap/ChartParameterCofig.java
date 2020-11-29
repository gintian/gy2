package com.hjsj.hrms.businessobject.general.sprelationmap;

import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class ChartParameterCofig {
	
	
	/**通用参数**/
	/**第一个节点（最顶层），距离顶部的距离*/
	public static final String Y_TOP="30.0";
	/**最后一个节点，距离底部的距离*/
	public static final String Y_BOTTOM="10.0";
	/**第一列节点，距离左边的空隙*/
	public static final String LEFT_SPACE="20";
	
	public static final String CONSTANT_LEFT="73.0";
	
	public static final String CONSTANT_TOP="33.0";
	/**最顶层节点的颜色*/
	public static final String TOP_COLOR="FFD700";
	/**当前对象的节点的颜色*/
    public static final String CURROBJECT_COLOR="7FFF00";	
    /**坐标小数位精度*/
    public static final int SCALE=8;

//	(  constant='ReportRelationChart' )
	//
//		<?xml version="1.0" encoding="GB2312"?>
//		<graph_param>
//		   //direction:图形方向  1：纵向 2：横向  shape:图形  1：矩形 2：圆形  
//		   < graph direction=’1|2’  shape=’1|2’  >   
//		   // lr_spacing:左右间距   tb_spacing:上下间距  width:节点宽   height:节点高  radius:半 border_widht:边宽    
//		   <set  lr_spacing=’xxx’  tb_spacing=’xxx’  width=’xxx’ height=’xxx’  radius=’xxx’   />
//		<font name=’xxx’  size=’xxx’   />
//		//  desc_items:描述信息指标   hint_items:提示信息指标  show_pic:显示人员照片
//		<personnel  desc_items=’a0103,a0105,a0402’  hint_items=’xxxx,xxxx,xxxx,xxxxx’  show_pic=’true’  > 
//		</ graph_param >
	private String direction="1";
	private String shape="rectangle";
	private String lr_spacing="60";
	private String tb_spacing="30";
	private String width="140";
	private String height="120";
	private String radius="60";
	private String border_width="2";
	
	private String fontName="仿宋";
	private ArrayList fontNameList = new ArrayList();
	
	private String fontSize="12";
	private String desc_items="";
	private String desc_items_desc="";
	private String hint_items="";
	private String hint_items_desc="";
	private String show_pic="false";
	
	private String bgColor="";
	
	//新汇报关系图新加节点
	//边框颜色
	private String border_color="";
	//过渡色
	String transitcolor="";
	
	String linecolor="";
	String linewidth="";
	
	String isshowshadow="";
	String theme="";
	
	String fontcolor="";
	
	String fontstyle="";
	
	
	public String getBgColor() {
		return bgColor.replace("＃","#");
	}
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}
	public ChartParameterCofig(){
		this.getFontNameList();
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getLr_spacing() {
		return lr_spacing;
	}
	public void setLr_spacing(String lr_spacing) {
		this.lr_spacing = lr_spacing;
	}
	public String getTb_spacing() {
		return tb_spacing;
	}
	public void setTb_spacing(String tb_spacing) {
		this.tb_spacing = tb_spacing;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	public String getFontSize() {
		return fontSize;
	}
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
	public String getDesc_items() {
		return desc_items;
	}
	public void setDesc_items(String desc_items) {
		this.desc_items = desc_items;
	}
	public String getHint_items() {
		return hint_items;
	}
	public void setHint_items(String hint_items) {
		this.hint_items = hint_items;
	}
	public String getShow_pic() {
		return show_pic;
	}
	public void setShow_pic(String show_pic) {
		this.show_pic = show_pic;
	}
	public String getDesc_items_desc() {
		return desc_items_desc;
	}
	public void setDesc_items_desc(String desc_items_desc) {
		this.desc_items_desc = desc_items_desc;
	}
	public String getHint_items_desc() {
		return hint_items_desc;
	}
	public void setHint_items_desc(String hint_items_desc) {
		this.hint_items_desc = hint_items_desc;
	}
	public String getBorder_width() {
		return border_width;
	}
	public void setBorder_width(String border_width) {
		this.border_width = border_width;
	}
	public ArrayList getFontNameList() {
		
		fontNameList=new ArrayList();
		CommonData vo = new CommonData();		
		vo.setDataName("请选择字体");
		vo.setDataValue("");
		fontNameList.add(vo);
		vo = new CommonData();		
		vo.setDataName("楷体_GB2312");
		vo.setDataValue("楷体_GB2312");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("方正舒体");
		vo.setDataValue("方正舒体");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("仿宋体");
		vo.setDataValue("仿宋体");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("华文彩云");
		vo.setDataValue("华文彩云");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("华文仿宋");
		vo.setDataValue("华文仿宋");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("华文细黑");
		vo.setDataValue("华文细黑");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("华文行楷");
		vo.setDataValue("华文行楷");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("华文中宋");
		vo.setDataValue("华文中宋");
		fontNameList.add(vo);
		vo=new CommonData();
		vo.setDataName("隶书");
		vo.setDataValue("隶书");
		fontNameList.add(vo);	
		vo=new CommonData();
		vo.setDataName("幼圆");
		vo.setDataValue("幼圆");
		fontNameList.add(vo);
		return fontNameList;
	}
	public void setFontNameList(ArrayList fontNameList) {
		this.fontNameList = fontNameList;
	}
	public String getCircle(){
		return RelationMapBo.getValue(this.radius,"2","*").toString();
	}
	public String getBorder_color() {
		return border_color.replace("＃","#");
	}
	public void setBorder_color(String border_color) {
		this.border_color = border_color;
	}
	public String getTransitcolor() {
		return transitcolor.replace("＃","#");
	}
	public void setTransitcolor(String transitcolor) {
		this.transitcolor = transitcolor;
	}
	public String getLinecolor() {
		return linecolor.replace("＃","#");
	}
	public void setLinecolor(String linecolor) {
		this.linecolor = linecolor;
	}
	public String getLinewidth() {
		return linewidth;
	}
	public void setLinewidth(String linewidth) {
		this.linewidth = linewidth;
	}
	public String getIsshowshadow() {
		return isshowshadow;
	}
	public void setIsshowshadow(String isshowshadow) {
		this.isshowshadow = isshowshadow;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getFontcolor() {
		return fontcolor.replace("＃","#");
	}
	public void setFontcolor(String fontcolor) {
		this.fontcolor = fontcolor;
	}
	public String getFontstyle() {
		return fontstyle;
	}
	public void setFontstyle(String fontstyle) {
		this.fontstyle = fontstyle;
	}
	
	
}
