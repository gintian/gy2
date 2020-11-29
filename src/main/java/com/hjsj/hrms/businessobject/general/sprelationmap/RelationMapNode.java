package com.hjsj.hrms.businessobject.general.sprelationmap;

import java.util.ArrayList;

/**
 * 审批关系图节点对象
 * @author lizw
 *
 */
public class RelationMapNode {
	
	/**每个节点不同*/
	/**节点左上角X坐标*/
	private String x="";
	/**节点左上角y坐标*/
	private String y="";
	/**节点宽度*/
	private String width="140"; 
	/**节点高度*/
	private String height="120";
	/**节点上汉字*/
	private String name="";
	/***/
	private String alpha="100";
	/**节点颜色*/
	private String color="f3f3f2";
	/**title显示内容*/
	private String toolText="";
	/**节点id*/
	private String id=""; 
	/**是否带图片*/
	private String imageNode="0";
	/**图片地址*/
	private String imageurl="";
	/**图片水平排列方式*/
	private String imageAlign="middle";
	/**图片高度*/
	private String imageHeight="95";
	/**图片宽度*/
	private String imageWidth="120";
	/**节点上连接*/
	private String link="";
	private String radius="";
	private String shape="rectangle";//circle

	private String borderColor="";
	private String numSides="";
	/**节点层级*/
	private int layer=0;
	/**节点的父节点*/
	private RelationMapNode parentNode;
	/**节点的孩子节点*/
	private ArrayList childrenList = new ArrayList();
	/**节点类型=1人员，=2职位，=3用户（业务）*/
	private String relationType="";
	
	public RelationMapNode(){
		
	}
	public String toNodeXml(){
		StringBuffer xml = new StringBuffer();
		xml.append("<set ");
		xml.append(" x='"+this.getX()+"'");
		xml.append(" y='"+this.getY()+"'");
		xml.append(" width='"+this.getWidth()+"'");
		xml.append(" height='"+this.getHeight()+"'");
		xml.append(" name='"+this.getName().replaceAll("<", "&lt;").replaceAll(">","&gt;")+"'");
		xml.append(" alpha='"+this.getAlpha()+"'");
		String color = this.getColor().replace("＃","#");
		xml.append(" color='"+color+"'");
		xml.append(" toolText='"+this.getToolText().replaceAll("<", "&lt;").replaceAll(">","&gt;")+"'");
		xml.append(" id='"+this.getId()+"'");
		if("1".equals(this.getImageNode())){
		    xml.append(" imageNode='"+this.getImageNode()+"'");
		    xml.append(" imageurl='"+this.getImageurl()+"'");
		    xml.append(" imageAlign='"+this.getImageAlign()+"'");
		    if("rectangle".equalsIgnoreCase(this.getShape()))
		    {
		        xml.append(" imageHeight='"+RelationMapBo.getValue(this.getHeight(), "0.75", "*").toString()+"'");
		        xml.append(" imageWidth='"+RelationMapBo.getValue(this.getHeight(), "1", "*").toString()+"'");
		    }else{
		    	xml.append(" imageHeight='"+RelationMapBo.getValue(RelationMapBo.getValue(this.getRadius(), "2", "*").toString(),"0.60","*").toString()+"'");
		        xml.append(" imageWidth='"+RelationMapBo.getValue(RelationMapBo.getValue(this.getRadius(), "2", "*").toString(),"0.65","*").toString()+"'");
		    }
		}else{
			xml.append(" imageNode='"+this.getImageNode()+"'");
		}
	    xml.append(" link='"+this.getLink()+"'");
	    xml.append(" radius='"+this.getRadius()+"'");
	    xml.append(" shape='"+this.getShape()+"'");
	    xml.append(" BorderColor='"+this.getBorderColor()+"'");
	    xml.append(" numSides='"+this.getNumSides()+"'");
	    xml.append("/>");
		return xml.toString();
		
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		if(Double.parseDouble(x)<Double.parseDouble(ChartParameterCofig.CONSTANT_LEFT)){
			x=ChartParameterCofig.CONSTANT_LEFT;
		}
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlpha() {
		return alpha;
	}
	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getToolText() {
		return toolText;
	}
	public void setToolText(String toolText) {
		this.toolText = toolText;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImageNode() {
		return imageNode;
	}
	public void setImageNode(String imageNode) {
		this.imageNode = imageNode;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	public String getImageAlign() {
		return imageAlign;
	}
	public void setImageAlign(String imageAlign) {
		this.imageAlign = imageAlign;
	}
	public String getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(String imageHeight) {
		this.imageHeight = imageHeight;
	}
	public String getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public RelationMapNode getParentNode() {
		return parentNode;
	}
	public void setParentNode(RelationMapNode parentNode) {
		this.parentNode = parentNode;
	}
	public ArrayList getChildrenList() {
		return childrenList;
	}
	public void setChildrenList(ArrayList childrenList) {
		this.childrenList = childrenList;
	}
	public RelationMapNode(String relationType){
		this.relationType=relationType;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public String getNumSides() {
		return numSides;
	}
	public void setNumSides(String numSides) {
		this.numSides = numSides;
	}

}
