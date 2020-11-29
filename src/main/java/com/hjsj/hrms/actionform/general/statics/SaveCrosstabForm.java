package com.hjsj.hrms.actionform.general.statics;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

/**
 * 
 * 
 * <p>Title: SaveCrosstabForm </p>
 * <p>Company: hjsj</p>
 * <p>create time  Sep 11, 2014 3:38:13 PM</p>
 * @author liuy
 * @version 1.0
 */
public class SaveCrosstabForm extends FrameForm{

	private String crossname="";//名称
	private String type="";//分类名
	private String hideType="";
	private ArrayList typeList=new ArrayList();//分类列表
	private String dbname="";
	private ArrayList dbnamelist=new ArrayList();//人员库列表
	private String condition="";//分类统计条件编号（常用查询编号）
	private ArrayList condList=new ArrayList();//分类统计条件集合
	private String hiderow="";//隐藏空行
	private String hidecol="";//隐藏空列
	private String crosswiseTotal="";//横向合计
	private String lengthwaysTotal="";//纵向合计
	private String showChart="";//显示统计图
	private ArrayList tempCondList;//分类统计条件集合
	private String lengthways="";//纵向维度
	private String crosswise="";//横向维度
	private String flag;//常用条件设置完成后的标志
	private String html;//选择框的html代码
	private PaginationForm SaveCrosstabForm = new PaginationForm();
	private PaginationForm setsaveCrosstabForm = new PaginationForm();
	

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("crossname", this.getCrossname());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("hideType", this.getHideType());
		this.getFormHM().put("typeList",this.getTypeList());
		this.getFormHM().put("dbname", this.getDbname());
		this.getFormHM().put("dbnamelist",this.getDbnamelist());
		this.getFormHM().put("condition",this.getCondition());		
		this.getFormHM().put("condList",this.getCondList());		
		this.getFormHM().put("hiderow",this.getHiderow());		
		this.getFormHM().put("hidecol",this.getHidecol());		
		this.getFormHM().put("crosswiseTotal",this.getCrosswiseTotal());		
		this.getFormHM().put("lengthwaysTotal",this.getLengthwaysTotal());		
		this.getFormHM().put("showChart",this.getShowChart());
		this.getFormHM().put("selectList", this.getSetsaveCrosstabForm().getSelectedList());
		this.getFormHM().put("html", this.getHtml());
		this.getFormHM().put("tempCondList", this.getTempCondList());
		this.getFormHM().put("lengthways", this.getLengthways());
		this.getFormHM().put("crosswise", this.getCrosswise());
		this.setFlag("0");
		
	}

	@Override
    public void outPutFormHM() {
		this.setCrossname((String)this.getFormHM().get("crossname"));
		this.setType((String)this.getFormHM().get("type"));
		this.setHideType((String)this.getFormHM().get("hideType"));
		this.setTypeList((ArrayList)this.getFormHM().get("typeList"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setDbnamelist((ArrayList)this.getFormHM().get("dbnamelist"));
		this.setCondition((String)this.getFormHM().get("condition"));
		this.setCondList((ArrayList)this.getFormHM().get("condList"));
		this.setHiderow((String)this.getFormHM().get("hiderow"));
		this.setHidecol((String)this.getFormHM().get("hidecol"));
		this.setCrosswiseTotal((String)this.getFormHM().get("crosswiseTotal"));
		this.setLengthwaysTotal((String)this.getFormHM().get("lengthwaysTotal"));
		this.setShowChart((String)this.getFormHM().get("showChart"));
		this.setLengthways((String)this.getFormHM().get("lengthways"));
		this.setCrosswise((String)this.getFormHM().get("crosswise"));
		if (this.getFormHM().get("tempCondList") == null) {
			this.setTempCondList(new ArrayList());
		} else {
			this.setTempCondList((ArrayList) this.getFormHM().get("tempCondList"));
		}
		this.setFlag((String) this.getFormHM().get("flag"));
		this.getFormHM().remove("flag");
		this.setHtml((String) this.getFormHM().get("html"));
		this.getSaveCrosstabForm().setList((ArrayList)this.getFormHM().get("list"));
		this.getSetsaveCrosstabForm().setList(this.getCondList());
	}

	public String getCrossname() {
		return crossname;
	}

	public void setCrossname(String crossname) {
		this.crossname = crossname;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getHideType() {
		return hideType;
	}

	public void setHideType(String hideType) {
		this.hideType = hideType;
	}

	public ArrayList getTypeList() {
		return typeList;
	}

	public void setTypeList(ArrayList typeList) {
		this.typeList = typeList;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public ArrayList getDbnamelist() {
		return dbnamelist;
	}

	public void setDbnamelist(ArrayList dbnamelist) {
		this.dbnamelist = dbnamelist;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public ArrayList getCondList() {
		return condList;
	}

	public void setCondList(ArrayList condList) {
		this.condList = condList;
	}

	public String getHiderow() {
		return hiderow;
	}

	public void setHiderow(String hiderow) {
		this.hiderow = hiderow;
	}

	public String getHidecol() {
		return hidecol;
	}

	public void setHidecol(String hidecol) {
		this.hidecol = hidecol;
	}

	public String getCrosswiseTotal() {
		return crosswiseTotal;
	}

	public void setCrosswiseTotal(String crosswiseTotal) {
		this.crosswiseTotal = crosswiseTotal;
	}

	public String getLengthwaysTotal() {
		return lengthwaysTotal;
	}

	public void setLengthwaysTotal(String lengthwaysTotal) {
		this.lengthwaysTotal = lengthwaysTotal;
	}

	public String getShowChart() {
		return showChart;
	}

	public void setShowChart(String showChart) {
		this.showChart = showChart;
	}

	public ArrayList getTempCondList() {
		return tempCondList;
	}

	public void setTempCondList(ArrayList tempCondList) {
		this.tempCondList = tempCondList;
	}

	public String getLengthways() {
		return lengthways;
	}

	public void setLengthways(String lengthways) {
		this.lengthways = lengthways;
	}

	public String getCrosswise() {
		return crosswise;
	}

	public void setCrosswise(String crosswise) {
		this.crosswise = crosswise;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public PaginationForm getSaveCrosstabForm() {
		return SaveCrosstabForm;
	}

	public void setSaveCrosstabForm(PaginationForm saveCrosstabForm) {
		SaveCrosstabForm = saveCrosstabForm;
	}

	public PaginationForm getSetsaveCrosstabForm() {
		return setsaveCrosstabForm;
	}

	public void setSetsaveCrosstabForm(PaginationForm setsaveCrosstabForm) {
		this.setsaveCrosstabForm = setsaveCrosstabForm;
	}
}
