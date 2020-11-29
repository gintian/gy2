/*
 * Created on 2005-5-9
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.module.card.businessobject;

import java.io.Serializable;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RPageView implements Serializable {
	private String tabid = null;
	private String gridno = null; //单元格索引号
	private String hz = null; //单元格汉字描述
	private String rleft = null; //位置
	private String rtop = null; //位置
	private String rwidth = null; //位置
	private String rheight = null; //位置
	private String fontsize = null; //单元格字体大小
	private String fontname = null; //  单元格字体名称
	private String fonteffect = null; //单元格字体Effect
	private int flag = 0; //0:文本描述1：求制表日期2:求制表时间3：制表人4：总页数：5页码6：其他
	private String pageid = null; //页签号
	private String extendAttr;
	public RPageView() {
	}
	/**
	 * @return
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @return
	 */
	public String getFonteffect() {
		return fonteffect;
	}

	/**
	 * @return
	 */
	public String getFontname() {
		return fontname;
	}

	/**
	 * @return
	 */
	public String getFontsize() {
		return fontsize;
	}

	/**
	 * @return
	 */
	public String getGridno() {
		return gridno;
	}

	/**
	 * @return
	 */
	public String getHz() {
		return hz;
	}

	/**
	 * @return
	 */
	public String getPageid() {
		return pageid;
	}

	/**
	 * @return
	 */
	public String getRheight(String pt) {
		String temp=rheight;
		if (rheight != null && rheight.length() > 0)
			temp =String.valueOf((int) ((Float.parseFloat(rheight) * 800 / 1024) + 1));
	   return temp;
	}
	public String getRheight() {
	   return rheight;
	}


	/**
	 * @return
	 */
	public String getRleft() {
		return rleft;
	}
	public String getRleft(String pt) {
		String temp=rleft;
		if (rleft != null && rleft.length() > 0)
			temp =String.valueOf((int) (Float.parseFloat(rleft) * 800 / 1024));
		return temp;
	}

	/**
	 * @return
	 */
	public String getRtop(String pt) {
		String temp=rtop;
		if (rtop != null && rtop.length() > 0)
			temp = String.valueOf((int) (Float.parseFloat(rtop) * 800 / 1024));
		return temp;
	}
	public String getRtop() {
		return rtop;
	}

	/**
	 * @return
	 */
	public String getRwidth() {
		return rwidth;
	}
	public String getRwidth(String pt) {
		String temp=rwidth;
		if (rwidth != null && rwidth.length() > 0)
			temp =String.valueOf(Math.round(Float.parseFloat(rwidth) * 800 / 1024));
		return temp;
	}
	/**
	 * @return
	 */
	public String getTabid() {
		return tabid;
	}

	/**
	 * @param i
	 */
	public void setFlag(int i) {
		flag = i;
	}

	/**
	 * @param string
	 */
	public void setFonteffect(String fonteffect) {
		this.fonteffect = fonteffect;
	}

	/**
	 * @param string
	 */
	public void setFontname(String fontname) {
		this.fontname = fontname;
	}

	/**
	 * @param string
	 */
	public void setFontsize(String fontsize) {
		this.fontsize = fontsize;
	}

	/**
	 * @param string
	 */
	public void setGridno(String gridno) {
		this.gridno = gridno;
	}

	/**
	 * @param string
	 */
	public void setHz(String hz) {
		this.hz = hz;
	}

	/**
	 * @param string
	 */
	public void setPageid(String pageid) {
		this.pageid = pageid;
	}

	/**
	 * @param string
	 */
	public void setRheight(String rheight) {
		this.rheight = rheight;
	}

	/**
	 * @param string
	 */
	public void setRleft(String rleft) {
		this.rleft = rleft;
	}

	/**
	 * @param string
	 */
	public void setRtop(String rtop) {
		this.rtop = rtop;
	}

	/**
	 * @param string
	 */
	public void setRwidth(String rwidth) {
		this.rwidth = rwidth;
	}

	/**
	 * @param string
	 */
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getExtendAttr() {
		return extendAttr;
	}
	public void setExtendAttr(String extendAttr) {
		this.extendAttr = extendAttr;
	}

}
