/*
 * Created on 2006-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.general.orgmap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ParameterBo {

	
	private int pageheight;
	private int pagewidth;
	private int pagespaceheight;                /*页边距高*/
	private int pagespacewidth;                 /*页边距宽*/
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
	/*人员*/
	private String isshowpersonconut;         /*显示人数*/
	private String isshowpersonname;          /*显示姓名*/
	private String isshowposname;             /*显示部门*/
	private String isshowdeptname;//是否显示部门
    private String deptlevel;//显示部门层级数
    private String unitlevel;//显示单位层级
	private String namesinglecell;            /*同级人员姓名是否显示在一个单元格中*/
	/*图形*/
    private String graph3d;                   /*图形效果*/
    private String graphaspect;               /*图形水平方向图形垂直方向*/
    private String dbname;
	 
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
	 * @return Returns the pageheight.
	 */
	public int getPageheight() {
		return pageheight;
	}
	/**
	 * @param pageheight The pageheight to set.
	 */
	public void setPageheight(int pageheight) {
		this.pageheight = pageheight;
	}
	/**
	 * @return Returns the pagespaceheight.
	 */
	public int getPagespaceheight() {
		return pagespaceheight;
	}
	/**
	 * @param pagespaceheight The pagespaceheight to set.
	 */
	public void setPagespaceheight(int pagespaceheight) {
		this.pagespaceheight = pagespaceheight;
	}
	/**
	 * @return Returns the pagespacewidth.
	 */
	public int getPagespacewidth() {
		return pagespacewidth;
	}
	/**
	 * @param pagespacewidth The pagespacewidth to set.
	 */
	public void setPagespacewidth(int pagespacewidth) {
		this.pagespacewidth = pagespacewidth;
	}
	/**
	 * @return Returns the pagewidth.
	 */
	public int getPagewidth() {
		return pagewidth;
	}
	/**
	 * @param pagewidth The pagewidth to set.
	 */
	public void setPagewidth(int pagewidth) {
		this.pagewidth = pagewidth;
	}
	/**
	 * @return Returns the dbname.
	 */
	public String getDbname() {
		return dbname;
	}
	/**
	 * @param dbname The dbname to set.
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getIsshowposname() {
		return isshowposname;
	}
	public void setIsshowposname(String isshowposname) {
		this.isshowposname = isshowposname;
	}
	public String getIsshowdeptname() {
		return isshowdeptname;
	}
	public void setIsshowdeptname(String isshowdeptname) {
		this.isshowdeptname = isshowdeptname;
	}
	public String getDeptlevel() {
		return deptlevel;
	}
	public void setDeptlevel(String deptlevel) {
		this.deptlevel = deptlevel;
	}
	public String getUnitlevel() {
		return unitlevel;
	}
	public void setUnitlevel(String unitlevel) {
		this.unitlevel = unitlevel;
	}
}
