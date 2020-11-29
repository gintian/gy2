package com.hjsj.hrms.actionform.performance.achivement;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>Title:StandardItemForm.java</p>
 * <p>Description>:StandardItemForm.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-9-12 上午09:59:33</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class StandardItemForm extends FrameForm{

	/**画项目表格的html代码*/
	private String tableHtml;
	/**项目id*/
	private String itemid;
	/**项目对应分数*/
	private String score;
	/**是否已有项目=0有=1没有*/
	private String isHaveItem;
	/**是否已经使用=0使用=1未使用*/
	private String isUsed;
	/**项目名称*/
	private String itemdesc;
	/**=0新增项目=1编辑项目*/
	private String type;
	/**指标id*/
	private String point_id;
	/**上限值*/
	private String top_value;
	/**下限值*/
	private String bottom_value;
	/**基本指标规则列表*/
	private ArrayList baseRuleList = new ArrayList();
	private String ruletype;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("ruletype",this.getRuletype());
		this.getFormHM().put("tableHtml",this.getTableHtml());
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("score",this.getScore());
		this.getFormHM().put("isHaveItem",this.getIsHaveItem());
		this.getFormHM().put("isUsed",this.getIsUsed());
		this.getFormHM().put("itemdesc",this.getItemdesc());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("point_id",this.getPoint_id());
	    this.getFormHM().put("top_value",this.getTop_value());
	    this.getFormHM().put("bottom_value",this.getBottom_value());
	}

	@Override
    public void outPutFormHM() {
		this.setBaseRuleList((ArrayList)this.getFormHM().get("baseRuleList"));
		this.setTop_value((String)this.getFormHM().get("top_value"));
		this.setBottom_value((String)this.getFormHM().get("bottom_value"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setIsHaveItem((String)this.getFormHM().get("isHaveItem"));
		this.setIsUsed((String)this.getFormHM().get("isUsed"));
		this.setScore((String)this.getFormHM().get("score"));
		this.setItemdesc((String)this.getFormHM().get("itemdesc"));
		this.setType((String)this.getFormHM().get("type"));
		this.setPoint_id((String)this.getFormHM().get("point_id"));
		this.setRuletype((String)this.getFormHM().get("ruletype"));
	}

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	public String getIsHaveItem() {
		return isHaveItem;
	}

	public void setIsHaveItem(String isHaveItem) {
		this.isHaveItem = isHaveItem;
	}

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getItemdesc() {
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPoint_id() {
		return point_id;
	}

	public void setPoint_id(String point_id) {
		this.point_id = point_id;
	}

	public String getBottom_value() {
		return bottom_value;
	}

	public void setBottom_value(String bottom_value) {
		this.bottom_value = bottom_value;
	}

	public String getTop_value() {
		return top_value;
	}

	public void setTop_value(String top_value) {
		this.top_value = top_value;
	}

	public ArrayList getBaseRuleList() {
		return baseRuleList;
	}

	public void setBaseRuleList(ArrayList baseRuleList) {
		this.baseRuleList = baseRuleList;
	}

	public String getRuletype() {
		return ruletype;
	}

	public void setRuletype(String ruletype) {
		this.ruletype = ruletype;
	}

}
