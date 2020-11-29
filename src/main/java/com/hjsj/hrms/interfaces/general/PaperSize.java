/**
 * 
 */
package com.hjsj.hrms.interfaces.general;

/**
 * <p>Title:PaperSize</p>
 * <p>Description:纸张大小</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-29:15:27:53</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PaperSize {
	public static final int A3=1;
	public static final int A4=2;
	public static final int A5=3;
	public static final int K16=4;
	public static final int K32=5;
	public static final int KB32=6;	
	public static final int B5=7;
	/**自定义*/
	public static final int CU=8;

	private int papertype=PaperSize.A4;
	
	private int width=210;
	private int height=297;
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getPapertype() {
		return papertype;
	}
	public void setPapertype(int papertype) {
		this.papertype = papertype;
	}
	/**
	 * @param papertype
	 */
	public PaperSize(int papertype) {
		switch(papertype)
		{
			case PaperSize.A3:
				this.width=297;
				this.height=420;
				break;
			case PaperSize.A5:
				this.width=148;
				this.height=210;
				break;
			case PaperSize.B5:
				this.width=182;
				this.height=257;
				break;
			case PaperSize.K16:
				this.width=184;
				this.height=260;
				break;
			case PaperSize.K32:
				this.width=130;
				this.height=184;
				break;
			case PaperSize.KB32:
				this.width=140;
				this.height=203;
				break;
			case PaperSize.A4:
				this.width=210;
				this.height=297;	
				break;				
			default:
				this.width=210;
				this.height=297;				
				break;
		}
		this.papertype = papertype;
	}
	
}
