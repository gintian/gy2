/**
 * 
 */
package com.hjsj.hrms.businessobject.general.muster;

import com.hjsj.hrms.utils.ResourceFactory;

/**
 * <p>Title:</p>
 * <p>Description:字体属性对象类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-19:16:50:35</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class FontBeanVo {
	private String fontname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
	private int fontsize=9;
	private boolean fsbold=false;
	private boolean fsItalic=false;
	private boolean fsUnderline=false;
	private boolean fsStrikeOut=false;
	public boolean isFsStrikeOut() {
		return fsStrikeOut;
	}
	public void setFsStrikeOut(boolean fsStrikeOut) {
		this.fsStrikeOut = fsStrikeOut;
	}
	/**
	 * 
	 */
	public FontBeanVo() {
		this(ResourceFactory.getProperty("gz.gz_acounting.m.font"),9);
	}
	public String getFontname() {
		return fontname;
	}
	public void setFontname(String fontname) {
		this.fontname = fontname;
	}
	public int getFontsize() {
		return fontsize;
	}
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}
	public boolean isFsbold() {
		return fsbold;
	}
	public void setFsbold(boolean fsbold) {
		this.fsbold = fsbold;
	}
	public boolean isFsItalic() {
		return fsItalic;
	}
	public void setFsItalic(boolean fsItalic) {
		this.fsItalic = fsItalic;
	}
	public boolean isFsUnderline() {
		return fsUnderline;
	}
	public void setFsUnderline(boolean fsUnderline) {
		this.fsUnderline = fsUnderline;
	}
	/**
	 * @param fontname
	 * @param fontsize
	 */
	public FontBeanVo(String fontname, int fontsize) {
		super();
		this.fontname = fontname;
		this.fontsize = fontsize;
	}
	
}
