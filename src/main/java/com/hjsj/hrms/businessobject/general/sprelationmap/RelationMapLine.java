package com.hjsj.hrms.businessobject.general.sprelationmap;
/**
 * 审批关系图，节点连线对象
 * @author lizw
 *
 */
public class RelationMapLine {

	/**线的粗细*/
	private String strength="0.35";
	/**起始节点id*/
	 private String from="";
	 /**终止节点id*/
	 private String to="";
	 /**线的颜色*/
	 private String color="33ccff";
	 /**起始处是否有箭头*/
	 private String arrowAtStart="0";
	 /**结束处是否有箭头*/
	 private String arrowAtEnd="1";
	 /**线的标题*/
	 private String label="";
	
	public String toConnectorXml(){
		 StringBuffer xml = new StringBuffer();
		 xml.append("<connector ");
		 xml.append(" strength='"+this.getStrength()+"'");
		 xml.append(" from='"+this.getFrom()+"'");
		 xml.append(" to='"+this.getTo()+"'");
		 xml.append(" color='"+this.getColor()+"'");
		 xml.append(" arrowAtStart='"+this.getArrowAtStart()+"'");
		 xml.append(" arrowAtEnd='"+this.getArrowAtEnd()+"'");
		 xml.append(" label='"+this.getLabel()+"'");
		 xml.append("/>");
		 return xml.toString();
	 }
	 public String getStrength() {
			return strength;
		}
		public void setStrength(String strength) {
			this.strength = strength;
		}
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public String getArrowAtStart() {
			return arrowAtStart;
		}
		public void setArrowAtStart(String arrowAtStart) {
			this.arrowAtStart = arrowAtStart;
		}
		public String getArrowAtEnd() {
			return arrowAtEnd;
		}
		public void setArrowAtEnd(String arrowAtEnd) {
			this.arrowAtEnd = arrowAtEnd;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
}
