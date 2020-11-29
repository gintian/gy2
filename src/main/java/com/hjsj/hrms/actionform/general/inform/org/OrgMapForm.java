/*
 * Created on 2006-3-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.general.inform.org;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OrgMapForm extends FrameForm {

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private String treeCode;
	private String code;
	private String kind;
	private String catalog_id;
	private String org_id;                     /*反查详细信息的ID*/
	private String infokind;                   /*表示人员机构职位*/
	private String dbnames;
	private String dbcond;
	private String isupright;                  /*垂直*/
	/*单元格*/
	private String cellletteralignleft;        /*水平居左*/
	private String cellletteralignright;       /*水平居右*/
	private String cellletteraligncenter;      /*水平居中*/
	private String celllettervaligncenter;     /*上下居中*/ 
	private String cellletterfitsize;          /*自动适应*/
	private String cellletterfitline;          /*自动换行*/
	/*字体*/
	private String fontfamily;                 /*字体*/
    private String fontstyle;                  /*字形*/
	private String fontsize;                   /*大小*/
	private String fontcolor;                  /*颜色*/
	
	/*单元格属性*/
	private String cellhspacewidth;           /*左右间距*/
	private String cellvspacewidth;           /*上下间距*/
	private String celllinestrokewidth;       /*线宽*/
	private String cellshape;                 /*单元格形状1矩形2....*/
	private String cellwidth;                 /*单元格宽*/
	private String cellheight;                /*单元格高*/
	private String cellcolor;                 /*单元格颜色*/
	private String cellaspect;                /*单元格水平方向单元格垂直方向*/
	private String isshoworgconut;				//当前机构child机构个数
	
	private String maptheme;
	private String isshowshadow;     //阴影效果
	private String transitcolor; //过渡色
	private String bordercolor;
	private String borderwidth;
	private String linewidth;
	private String linecolor;
	private String isshowphoto;
	
	/*人员*/
	private String isshowpersonconut;         /*显示人数*/
	private String isshowpersonname;          /*显示姓名*/
	private String namesinglecell;            /*同级人员姓名是否显示在一个单元格中*/
	/*图形*/
    private String graph3d;                   /*图形效果*/
    private String graphaspect;               /*图形水平方向图形垂直方向*/
    
    
    private String isyfiles;// 新旧机构图标示  1  yfiles新机构图   0 旧机构图
    
    /*是否是历史机构*/
    private String ishistory;
	private String catalog_name; 
    private String description;//归档说明
    
    /*汇报关系常量*/
    private String constant;
    
    /*是否是汇报关系*/
    private String report_relations;// 汇报关系
    private String showtype="1";//显示类型1:组织机构2，汇报关系
    private String isshowposname;//是否显示职位
    private String isshowdeptname;//是否显示部门
    private String deptlevel;//显示部门层级数
    private String unitlevel;//显示单位层级数
    private String orgtype;
    private String link;//暂时只标示是否历史机构传来的链接
    private String backdate;
    private String isshowposup;//汇报关系显示岗位所属单位和部门 2014-3-14 gdd
    private String seprartor;//显示单位和部门时之间的分割符

    private String isshowpartjobperson;//显示兼职人员 chent 20170515
    private String partjobpersoncolor;//兼职人员颜色 chent 20170515

    public String getIsshowpartjobperson() {
		return isshowpartjobperson;
	}
	public void setIsshowpartjobperson(String isshowpartjobperson) {
		this.isshowpartjobperson = isshowpartjobperson;
	}
	public String getPartjobpersoncolor() {
		return partjobpersoncolor;
	}
	public void setPartjobpersoncolor(String partjobpersoncolor) {
		this.partjobpersoncolor = partjobpersoncolor;
	}
    
	public String getBackdate() {
		return backdate;
	}
	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}
	private String returnvalue;
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	public String getIshistory() {
		return ishistory;
	}
	public void setIshistory(String ishistory) {
		this.ishistory = ishistory;
	}
	@Override
    public void outPutFormHM() {
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setCatalog_id((String)this.getFormHM().get("catalog_id"));
		this.setDbcond((String)this.getFormHM().get("dbcond"));
		this.setDbnames((String)this.getFormHM().get("dbnames"));
		this.setIsupright((String)this.getFormHM().get("isupright"));
	    this.setCellhspacewidth((String)this.getFormHM().get("cellhspacewidth"));
	    this.setCellletteraligncenter((String)this.getFormHM().get("cellletteraligncenter"));
	    this.setCellletteralignleft((String)this.getFormHM().get("cellletteralignleft"));
	    this.setCellletteralignright((String)this.getFormHM().get("cellletteralignright"));
	    this.setCellletterfitline((String)this.getFormHM().get("cellletterfitline"));
	    this.setCellletterfitsize((String)this.getFormHM().get("cellletterfitsize"));
	    this.setCelllettervaligncenter((String)this.getFormHM().get("celllettervaligncenter"));
	    this.setCelllinestrokewidth((String)this.getFormHM().get("celllinestrokewidth"));
	    this.setCellvspacewidth((String)this.getFormHM().get("cellvspacewidth"));
	    this.setCellheight((String)this.getFormHM().get("cellheight"));
	    this.setCellwidth((String)this.getFormHM().get("cellwidth"));
	    this.setCellshape((String)this.getFormHM().get("cellshape"));
	    this.setFontcolor((String)this.getFormHM().get("fontcolor"));
	    this.setFontfamily((String)this.getFormHM().get("fontfamily"));
	    this.setFontsize((String)this.getFormHM().get("fontsize"));
	    this.setFontstyle((String)this.getFormHM().get("fontstyle"));
	    this.setIsshowpersonconut((String)this.getFormHM().get("isshowpersonconut"));
	    this.setIsshowpersonname((String)this.getFormHM().get("isshowpersonname"));
	    this.setIsshowposname((String)this.getFormHM().get("isshowposname"));
	    this.setNamesinglecell((String)this.getFormHM().get("namesinglecell"));
	    this.setCellcolor((String)this.getFormHM().get("cellcolor"));
	    this.setCellaspect((String)this.getFormHM().get("cellaspect"));
	    this.setGraph3d((String)this.getFormHM().get("graph3d"));
	    this.setGraphaspect((String)this.getFormHM().get("graphaspect"));
	    this.setDescription((String)this.getFormHM().get("description"));
	    this.setReport_relations((String)this.getFormHM().get("report_relations"));
	    this.setIsshowposup((String)this.getFormHM().get("isshowposup"));
	    this.setSeprartor((String)this.getFormHM().get("seprartor"));
	    this.setConstant((String)this.getFormHM().get("constant"));
	    this.setOrgtype((String)this.getFormHM().get("orgtype"));
	    this.setCode((String)this.getFormHM().get("code"));
	    this.setKind((String)this.getFormHM().get("kind"));
	    this.setShowtype((String)this.getFormHM().get("showtype"));
	    this.setIsshowdeptname((String)this.getFormHM().get("isshowdeptname"));
	    this.setDeptlevel((String)this.getFormHM().get("deptlevel"));
	    this.setUnitlevel((String)this.getFormHM().get("unitlevel"));
	    this.setLink((String)this.getFormHM().get("link"));
	    this.getFormHM().remove("link");
	    this.setBackdate((String)this.getFormHM().get("backdate"));
	    this.setIsshoworgconut((String)this.getFormHM().get("isshoworgconut"));
	    this.setIsshowshadow((String)this.getFormHM().get("isshowshadow"));
	    this.setTransitcolor((String)this.getFormHM().get("transitcolor"));
	    this.setBordercolor((String)this.getFormHM().get("bordercolor"));
	    this.setBorderwidth((String)this.getFormHM().get("borderwidth"));
	    this.setLinecolor((String)this.getFormHM().get("linecolor"));
	    this.setLinewidth((String)this.getFormHM().get("linewidth"));
	    this.setIsshowphoto((String)this.getFormHM().get("isshowphoto"));
	    this.setIshistory((String)this.getFormHM().get("ishistory"));
	    this.setMaptheme((String)this.getFormHM().get("maptheme"));
	    this.setIsshowpartjobperson((String)this.getFormHM().get("isshowpartjobperson"));
	    this.setPartjobpersoncolor((String)this.getFormHM().get("partjobpersoncolor"));
	}
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("org_id", this.getOrg_id());
		this.getFormHM().put("cellletteralignleft",cellletteralignleft);
		this.getFormHM().put("cellletteralignright",cellletteralignright);
		this.getFormHM().put("cellletteraligncenter",cellletteraligncenter);
		this.getFormHM().put("celllettervaligncenter",celllettervaligncenter);
		this.getFormHM().put("cellletterfitsize",cellletterfitsize);
		this.getFormHM().put("cellletterfitline",cellletterfitline);
		this.getFormHM().put("fontfamily",fontfamily);
		this.getFormHM().put("fontstyle",fontstyle);
		this.getFormHM().put("fontsize",fontsize);
		this.getFormHM().put("fontcolor",fontcolor);
		this.getFormHM().put("cellhspacewidth",cellhspacewidth);
		this.getFormHM().put("cellvspacewidth",cellvspacewidth);
		this.getFormHM().put("celllinestrokewidth",celllinestrokewidth);
		this.getFormHM().put("cellshape",cellshape);
		this.getFormHM().put("cellwidth",cellwidth);
		this.getFormHM().put("cellheight",cellheight);
		this.getFormHM().put("isshowpersonconut",isshowpersonconut);
		this.getFormHM().put("isshowpersonname",isshowpersonname);
		this.getFormHM().put("namesinglecell",namesinglecell);
		this.getFormHM().put("cellcolor",cellcolor);
		this.getFormHM().put("cellaspect",cellaspect);
		this.getFormHM().put("graph3d",graph3d);
		this.getFormHM().put("graphaspect",graphaspect);
		this.getFormHM().put("dbnames",dbnames);
		this.getFormHM().put("ishistory",ishistory);
		this.getFormHM().put("report_relations",this.getReport_relations());
		this.getFormHM().put("constant",this.getConstant());
		this.getFormHM().put("code", code);
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("catalog_id", catalog_id);
		this.getFormHM().put("orgtype", orgtype);
		this.getFormHM().put("showtype", showtype);
		this.getFormHM().put("isshowposname", isshowposname);
		this.getFormHM().put("deptlevel", this.deptlevel);
		this.getFormHM().put("unitlevel", unitlevel);
		this.getFormHM().put("isshowdeptname",this.isshowdeptname);
		this.getFormHM().put("backdate", this.getBackdate());
		this.getFormHM().put("isshoworgconut", isshoworgconut);
		HashMap fm = this.getFormHM();
		fm.put("isyfiles", this.getIsyfiles());
		fm.put("isshowposup", this.getIsshowposup());
		fm.put("isshowshadow", this.getIsshowshadow());
		fm.put("transitcolor", this.getTransitcolor());
		fm.put("bordercolor", this.getBordercolor());
		fm.put("borderwidth", this.getBorderwidth());
		fm.put("linecolor", this.getLinecolor());
		fm.put("linewidth", this.getLinewidth());
		fm.put("isshowphoto", this.getIsshowphoto());
		fm.put("maptheme", this.getMaptheme());
		fm.put("isshowpartjobperson", this.getIsshowpartjobperson());
		fm.put("partjobpersoncolor", this.getPartjobpersoncolor());
	}
	/**
	 * @return Returns the treeCode.
	 */
	public String getTreeCode() {
		return treeCode;
	}
	/**
	 * @param treeCode The treeCode to set.
	 */
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return Returns the catalog_id.
	 */
	public String getCatalog_id() {
		return catalog_id;
	}
	/**
	 * @param catalog_id The catalog_id to set.
	 */
	public void setCatalog_id(String catalog_id) {
		this.catalog_id = catalog_id;
	}
	/**
	 * @return Returns the dbname.
	 */
	public String getDbnames() {
		return dbnames;
	}
	/**
	 * @param dbname The dbname to set.
	 */
	public void setDbnames(String dbnames) {
		this.dbnames = dbnames;
	}
	/**
	 * @return Returns the org_id.
	 */
	public String getOrg_id() {
		return org_id;
	}
	/**
	 * @param org_id The org_id to set.
	 */
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	/**
	 * @return Returns the infokind.
	 */
	public String getInfokind() {
		return infokind;
	}
	/**
	 * @param infokind The infokind to set.
	 */
	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}
	/**
	 * @return Returns the dbcond.
	 */
	public String getDbcond() {
		return dbcond;
	}
	/**
	 * @param dbcond The dbcond to set.
	 */
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
	/**
	 * @return Returns the isupright.
	 */
	public String getIsupright() {
		return isupright;
	}
	/**
	 * @param isupright The isupright to set.
	 */
	public void setIsupright(String isupright) {
		this.isupright = isupright;
	}
	/**
	 * @return Returns the cellhspacewidth.
	 */
	public String getCellhspacewidth() {
		return cellhspacewidth;
	}
	/**
	 * @param cellhspacewidth The cellhspacewidth to set.
	 */
	public void setCellhspacewidth(String cellhspacewidth) {
		this.cellhspacewidth = cellhspacewidth;
	}
	/**
	 * @return Returns the cellletteraligncenter.
	 */
	public String getCellletteraligncenter() {
		return cellletteraligncenter;
	}
	/**
	 * @param cellletteraligncenter The cellletteraligncenter to set.
	 */
	public void setCellletteraligncenter(String cellletteraligncenter) {
		this.cellletteraligncenter = cellletteraligncenter;
	}
	/**
	 * @return Returns the cellletteralignleft.
	 */
	public String getCellletteralignleft() {
		return cellletteralignleft;
	}
	/**
	 * @param cellletteralignleft The cellletteralignleft to set.
	 */
	public void setCellletteralignleft(String cellletteralignleft) {
		this.cellletteralignleft = cellletteralignleft;
	}
	/**
	 * @return Returns the cellletteralignright.
	 */
	public String getCellletteralignright() {
		return cellletteralignright;
	}
	/**
	 * @param cellletteralignright The cellletteralignright to set.
	 */
	public void setCellletteralignright(String cellletteralignright) {
		this.cellletteralignright = cellletteralignright;
	}
	/**
	 * @return Returns the cellletterfitline.
	 */
	public String getCellletterfitline() {
		return cellletterfitline;
	}
	/**
	 * @param cellletterfitline The cellletterfitline to set.
	 */
	public void setCellletterfitline(String cellletterfitline) {
		this.cellletterfitline = cellletterfitline;
	}
	/**
	 * @return Returns the cellletterfitsize.
	 */
	public String getCellletterfitsize() {
		return cellletterfitsize;
	}
	/**
	 * @param cellletterfitsize The cellletterfitsize to set.
	 */
	public void setCellletterfitsize(String cellletterfitsize) {
		this.cellletterfitsize = cellletterfitsize;
	}
	/**
	 * @return Returns the celllettervaligncenter.
	 */
	public String getCelllettervaligncenter() {
		return celllettervaligncenter;
	}
	/**
	 * @param celllettervaligncenter The celllettervaligncenter to set.
	 */
	public void setCelllettervaligncenter(String celllettervaligncenter) {
		this.celllettervaligncenter = celllettervaligncenter;
	}
	/**
	 * @return Returns the celllinestrokewidth.
	 */
	public String getCelllinestrokewidth() {
		return celllinestrokewidth;
	}
	/**
	 * @param celllinestrokewidth The celllinestrokewidth to set.
	 */
	public void setCelllinestrokewidth(String celllinestrokewidth) {
		this.celllinestrokewidth = celllinestrokewidth;
	}
	/**
	 * @return Returns the cellvspacewidth.
	 */
	public String getCellvspacewidth() {
		return cellvspacewidth;
	}
	/**
	 * @param cellvspacewidth The cellvspacewidth to set.
	 */
	public void setCellvspacewidth(String cellvspacewidth) {
		this.cellvspacewidth = cellvspacewidth;
	}
	/**
	 * @return Returns the fontcolor.
	 */
	public String getFontcolor() {
		return fontcolor;
	}
	/**
	 * @param fontcolor The fontcolor to set.
	 */
	public void setFontcolor(String fontcolor) {
		this.fontcolor = fontcolor;
	}
	/**
	 * @return Returns the fontfamily.
	 */
	public String getFontfamily() {
		return fontfamily;
	}
	/**
	 * @param fontfamily The fontfamily to set.
	 */
	public void setFontfamily(String fontfamily) {
		this.fontfamily = fontfamily;
	}
	/**
	 * @return Returns the fontsize.
	 */
	public String getFontsize() {
		return fontsize;
	}
	/**
	 * @param fontsize The fontsize to set.
	 */
	public void setFontsize(String fontsize) {
		this.fontsize = fontsize;
	}
	/**
	 * @return Returns the fontstyle.
	 */
	public String getFontstyle() {
		return fontstyle;
	}
	/**
	 * @param fontstyle The fontstyle to set.
	 */
	public void setFontstyle(String fontstyle) {
		this.fontstyle = fontstyle;
	}
	/**
	 * @return Returns the isshowpersonconut.
	 */
	public String getIsshowpersonconut() {
		return isshowpersonconut;
	}
	/**
	 * @param isshowpersonconut The isshowpersonconut to set.
	 */
	public void setIsshowpersonconut(String isshowpersonconut) {
		this.isshowpersonconut = isshowpersonconut;
	}
	/**
	 * @return Returns the isshowpersonname.
	 */
	public String getIsshowpersonname() {
		return isshowpersonname;
	}
	/**
	 * @param isshowpersonname The isshowpersonname to set.
	 */
	public void setIsshowpersonname(String isshowpersonname) {
		this.isshowpersonname = isshowpersonname;
	}
	/**
	 * @return Returns the namesinglecell.
	 */
	public String getNamesinglecell() {
		return namesinglecell;
	}
	/**
	 * @param namesinglecell The namesinglecell to set.
	 */
	public void setNamesinglecell(String namesinglecell) {
		this.namesinglecell = namesinglecell;
	}
	/**
	 * @return Returns the cellheight.
	 */
	public String getCellheight() {
		return cellheight;
	}
	/**
	 * @param cellheight The cellheight to set.
	 */
	public void setCellheight(String cellheight) {
		this.cellheight = cellheight;
	}
	/**
	 * @return Returns the cellshape.
	 */
	public String getCellshape() {
		return cellshape;
	}
	/**
	 * @param cellshape The cellshape to set.
	 */
	public void setCellshape(String cellshape) {
		this.cellshape = cellshape;
	}
	/**
	 * @return Returns the cellwidth.
	 */
	public String getCellwidth() {
		return cellwidth;
	}
	/**
	 * @param cellwidth The cellwidth to set.
	 */
	public void setCellwidth(String cellwidth) {
		this.cellwidth = cellwidth;
	}
	/**
	 * @return Returns the cellcolor.
	 */
	public String getCellcolor() {
		return cellcolor;
	}
	/**
	 * @param cellcolor The cellcolor to set.
	 */
	public void setCellcolor(String cellcolor) {
		this.cellcolor = cellcolor;
	}
	/**
	 * @return Returns the graph3d.
	 */
	public String getGraph3d() {
		return graph3d;
	}
	/**
	 * @param graph3d The graph3d to set.
	 */
	public void setGraph3d(String graph3d) {
		this.graph3d = graph3d;
	}	
	/**
	 * @return Returns the cellaspect.
	 */
	public String getCellaspect() {
		return cellaspect;
	}
	/**
	 * @param cellaspect The cellaspect to set.
	 */
	public void setCellaspect(String cellaspect) {
		this.cellaspect = cellaspect;
	}
	/**
	 * @return Returns the graphaspect.
	 */
	public String getGraphaspect() {
		return graphaspect;
	}
	/**
	 * @param graphaspect The graphaspect to set.
	 */
	public void setGraphaspect(String graphaspect) {
		this.graphaspect = graphaspect;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getReport_relations() {
		return report_relations;
	}
	public void setReport_relations(String report_relations) {
		this.report_relations = report_relations;
	}
	public String getConstant() {
		return constant;
	}
	public void setConstant(String constant) {
		this.constant = constant;
	}
	public String getIsshowposname() {
		return isshowposname;
	}
	public void setIsshowposname(String isshowposname) {
		this.isshowposname = isshowposname;
	}
	public String getShowtype() {
		return showtype;
	}
	public void setShowtype(String showtype) {
		this.showtype = showtype;
	}
	
	public String getDeptlevel() {
		return deptlevel;
	}
	public void setDeptlevel(String deptlevel) {
		this.deptlevel = deptlevel;
	}
	public String getIsshowdeptname() {
		return isshowdeptname;
	}
	public void setIsshowdeptname(String isshowdeptname) {
		this.isshowdeptname = isshowdeptname;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getUnitlevel() {
		return unitlevel;
	}
	public void setUnitlevel(String unitlevel) {
		this.unitlevel = unitlevel;
	}
	public String getIsshoworgconut() {
		return isshoworgconut;
	}
	public void setIsshoworgconut(String isshoworgconut) {
		this.isshoworgconut = isshoworgconut;
	}
	public String getIsshowposup() {
		return isshowposup;
	}
	public void setIsshowposup(String isshowposup) {
		this.isshowposup = isshowposup;
	}
	public String getIsshowshadow() {
		return isshowshadow;
	}
	public void setIsshowshadow(String isshowshadow) {
		this.isshowshadow = isshowshadow;
	}
	public String getTransitcolor() {
		return transitcolor;
	}
	public void setTransitcolor(String transitcolor) {
		this.transitcolor = transitcolor;
	}
	public String getBorderwidth() {
		return borderwidth;
	}
	public void setBorderwidth(String borderwidth) {
		this.borderwidth = borderwidth;
	}
	public String getLinewidth() {
		return linewidth;
	}
	public void setLinewidth(String linewidth) {
		this.linewidth = linewidth;
	}
	public String getLinecolor() {
		return linecolor;
	}
	public void setLinecolor(String linecolor) {
		this.linecolor = linecolor;
	}
	public String getIsshowphoto() {
		return isshowphoto;
	}
	public void setIsshowphoto(String isshowphoto) {
		this.isshowphoto = isshowphoto;
	}
	public String getBordercolor() {
		return bordercolor;
	}
	public void setBordercolor(String bordercolor) {
		this.bordercolor = bordercolor;
	}
	public String getIsyfiles() {
		return isyfiles;
	}
	public void setIsyfiles(String isyfiles) {
		this.isyfiles = isyfiles;
	}
	public String getMaptheme() {
		return maptheme;
	}
	public void setMaptheme(String maptheme) {
		this.maptheme = maptheme;
	}
	
	public String getCatalog_name() {
		return catalog_name;
	}
	public void setCatalog_name(String catalog_name) {
		this.catalog_name = catalog_name;
	}
	
	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		
		//判断是否通过 yfiles 机构图连接进入的
		String path = mapping.getPath();
		if(path.indexOf("/general/inform/org/map/searchOrgTree") != -1)
			this.setIsyfiles("1");
		else if(path.indexOf("/general/inform/org/map/searchhistoryOrgTree") != -1)
			this.setIsyfiles("1");
		else if(path.indexOf("/general/inform/org/map/showyFilesOrgMap") != -1)
			this.setIsyfiles("1");
		else
			this.setIsyfiles("0");
		
		return super.validate(mapping, request);
	}
	public String getSeprartor() {
		return seprartor;
	}
	public void setSeprartor(String seprartor) {
		this.seprartor = seprartor;
	}
	
	
	
}
