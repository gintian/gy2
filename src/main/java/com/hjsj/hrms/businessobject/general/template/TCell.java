/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import java.awt.*;
import java.io.Serializable;

/**
 * <p>Title:TCell</p>
 * <p>Description:单元格</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 18, 20066:09:18 PM
 * @author chenmengqing
 * @version 4.0
 */
public class TCell implements Serializable, Cloneable {
	/**单元格编号*/
	private int gridno;
	/**以"`"作为单元格内容换行*/	
	private String hz;
	private String setname;
	private String field_name;
	/**
	 * 字段数值类型D:日期型A:字符型N：数值型M:备注型
	 */
	private String field_type;
	/** 
	 * 字段的原始类型，与数据库中相同， 
	 * 如果是取多条记录，此单元格的类型会更改为M 新的存放在field_type
	 */
	private String old_fieldType;
	private String field_hz;
	/**
	 *相关代码类 
	 */
	private String codeid;
	private boolean bcode;

	/**
	 * A：人员库B：单位库K:职位库P：照片H：文本C：计算结果V:临时变量
	 */
	private String flag="H";
	private int rtop;
	private int rleft;
	private int rwidth;
	private int rheight;
	private String fontname;
	private int fontsize;
	/**
	 * =1,正常
	 * =2,粗体
	 * =3,斜体
	 * =4,粗斜体
	 */
	private int fonteffect;
	//0：无线1：有线，画图时用虚线代替
	private int l;
	private int t;
	private int r;
	private int b;
	/**
	 * 0:无斜线1左斜线2右斜线3两斜线
	 */
	private int sl;
	/**
	 * 文字在单元排列方式
	 =0上左=1上中=2上右=3下左=4下中=5下右=6中左=7中中=8中右
	 */
	private int align;
	/**
	 * =1,一条记录(最近一条记录)
	 * =2,多条记录(根据记录数\子集记录定位方法两个字段进行记录定位输出)
     * =3,条件记录(根据计算公式里定义条件定位)
	 */
	private int hismode;
	/**数据显示格式*/
	private int disformat;
	/**相同指标的序号，目前未用到*/
	private int nsort;
	/**记录数*/
	private int rcount;
	/**
	 * 子集记录控制
	 *=2：倒数...条记录,=1：倒数第...条记录,=4: 正数...条记录,
	 *=3:正数第...条记录,=5.子集记录以查询条件为主
	 */
	private int mode ;
	/**计算公式及条件
	 * ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * */
	private String formula;

	private int nhide;
	
	private String getCellCss()
	{
		String css="";		
		if(r==1&&b==0&&l==0&&t==0) {
			css="r_line";
		} else if(r==0&&b==1&&l==0&&t==0) {
			css="b_line";
		} else if(r==0&&b==0&&l==1&&t==0) {
			css="l_line";
		} else if(r==0&&b==0&&l==0&&t==1) {
			css="t_line";
		} else if(r==1&&b==1&&l==0&&t==0) {
			css="rb_line";
		} else if(r==1&&b==0&&l==1&&t==0) {
			css="lr_line";
		} else if(r==1&&b==0&&l==0&&t==1) {
			css="rt_line";
		} else if(r==0&&b==1&&l==1&&t==0) {
			css="lb_line";
		} else if(r==0&&b==1&&l==0&&t==1) {
			css="tb_line";
		} else if(r==0&&b==0&&l==1&&t==1) {
			css="lt_line";
		} else if(r==1&&b==1&&l==1&&t==0) {
			css="lrb_line";
		} else if(r==0&&b==1&&l==1&&t==1) {
			css="ltb_line";
		} else if(r==1&&b==0&&l==1&&t==1) {
			css="lrt_line";
		} else if(r==1&&b==1&&l==0&&t==1) {
			css="rtb_line";
		} else if(r==1&&b==1&&l==1&&t==1) {
			css="lrtb_line";
		} else {
			css="no_line";
		}
		return css;
	}
	 
	private String getBottomCss()
	{
		String css="";		
		if(b==1&&l==0&&t==0) {
			css="b_line";
		} else if(b==0&&l==1&&t==0) {
			css="l_line";
		} else if(b==0&&l==0&&t==1) {
			css="t_line";
		} else if(b==1&&l==1&&t==0) {
			css="lb_line";
		} else if(b==1&&l==0&&t==1) {
			css="tb_line";
		} else if(b==0&&l==1&&t==1) {
			css="lt_line";
		} else if(b==1&&l==1&&t==1) {
			css="ltb_line";
		} else {
			css="no_line";
		}
		return css;
	}

	
	private String getRightCellCss()
	{
		String css="";		
		if(r==1&&l==0&&t==0) {
			css="r_line";
		} else if(r==0&&l==1&&t==0) {
			css="l_line";
		} else if(r==0&&l==0&&t==1) {
			css="t_line";
		} else if(r==1&&l==1&&t==0) {
			css="lr_line";
		} else if(r==1&&l==0&&t==1) {
			css="rt_line";
		} else if(r==0&&l==1&&t==1) {
			css="lt_line";
		} else if(r==1&&l==1&&t==1) {
			css="lrt_line";
		} else {
			css="no_line";
		}
		return css;
	}
	
	/**
	 * 取得样式
	 * @param rect 表格外边框区域
	 * @return
	 */
	protected String getBorderLineCss(Rectangle rect)
	{
		String css="";
		int right=rleft+rwidth;
		int bottom=rtop+rheight;
		int maxb=rect.y+rect.height;
		int maxr=rect.x+rect.width;
//		if((right==maxr&&bottom==maxb)||(rleft==rect.x&&right==maxr))
//		{
//			css=getCellCss();
//		}
//		else if(right==maxr&&bottom!=maxb)
//		{
//			css=getRightCellCss();
//		}	
//		else if(right!=maxr&&bottom==maxb)
//		{
//			css=getBottomCss();
//		}			
//		else //if(right<maxr)
//		{
//			if(l==0&&t==0)
//				css="no_line";
//			else if(l==0&&t==1)
//				css="t_line";
//			else if(l==1&&t==0)
//				css="l_line";		
//			else
//				css="lt_line";
//		}
		css=getCellCss();
		return css;
	}	
	
	/**
	 * 求得单元格字体信息
	 * @return
	 */
	protected String getFontStyle()
	{
		StringBuffer style=new StringBuffer();
		style.append("font-size:");
		style.append((this.getFontsize()));
		style.append("pt");
		switch(this.getFonteffect())
		{
		case 2:
			style.append(";font-weight:");
			style.append("bold");
			break;
		case 3:
			style.append(";font-style:");
			style.append("italic");			
			break;
		case 4:
			style.append(";font-weight:");
			style.append("bold");
			style.append(";font-style:");
			style.append("italic");				
			break;
		}
		return style.toString();
	}	

	/**
	 * 排列方式
	 * @param ali
	 * @return
	 */
	protected String[] getHValign(int ali) {
		String[] align = new String[2];
		switch(ali)
		{
		case 0:
			align[0] = "left";
			align[1] = "top";			
			break;
		case 1:
			align[0] = "center";
			align[1] = "top";		
			break;	
		case 2:
			align[0] = "right";
			align[1] = "top";			
			break;
		case 3:
			align[0] = "left";
			align[1] = "bottom";			
			break;
		case 4:
			align[0] = "center";
			align[1] = "bottom";			
			break;
		case 5:
			align[0] = "right";
			align[1] = "bottom";			
			break;
		case 6:
			align[0] = "left";
			align[1] = "middle";
			break;
		case 7:
			align[0] = "center";
			align[1] = "middle";
			break;
		case 8:
			align[0] = "right";
			align[1] = "middle";			
			break;
		}
		return align;
	}	

	
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public int getAlign() {
		return align;
	}
	public void setAlign(int align) {
		this.align = align;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public String getCodeid() {
		return codeid;
	}
	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}
	public int getDisformat() {
		return disformat;
	}
	public void setDisformat(int disformat) {
		this.disformat = disformat;
	}
	public String getField_hz() {
		return field_hz;
	}
	public void setField_hz(String field_hz) {
		this.field_hz = field_hz;
	}
	public String getField_name() {
		return field_name;
	}
	public void setField_name(String field_name) {
		this.field_name = field_name;
	}
	public String getField_type() {
		return field_type;
	}
	public void setField_type(String field_type) {
		this.field_type = field_type;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public int getFonteffect() {
		return fonteffect;
	}
	public void setFonteffect(int fonteffect) {
		this.fonteffect = fonteffect;
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
	public int getHismode() {
		return hismode;
	}
	public void setHismode(int hismode) {
		this.hismode = hismode;
	}
	public String getHz() {
		return hz;
	}
	public void setHz(String hz) {
		this.hz = hz;
	}
	public int getL() {
		return l;
	}
	public void setL(int l) {
		this.l = l;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getNsort() {
		return nsort;
	}
	public void setNsort(int nsort) {
		this.nsort = nsort;
	}
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public int getRcount() {
		return rcount;
	}
	public void setRcount(int rcount) {
		this.rcount = rcount;
	}
	public int getRheight() {
		return rheight;
	}
	public void setRheight(int rheight) {
		this.rheight = rheight;
	}
	public int getRleft() {
		return rleft;
	}
	public void setRleft(int rleft) {
		this.rleft = rleft;
	}
	public int getRtop() {
		return rtop;
	}
	public void setRtop(int rtop) {
		this.rtop = rtop;
	}
	public int getRwidth() {
		return rwidth;
	}
	public void setRwidth(int rwidth) {
		this.rwidth = rwidth;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public int getSl() {
		return sl;
	}
	public void setSl(int sl) {
		this.sl = sl;
	}
	public int getT() {
		return t;
	}
	public void setT(int t) {
		this.t = t;
	}
	public TCell() {
	}

	public boolean isBcode() {
		if(codeid==null|| "".equals(codeid)) {
			return false;
		}
		return true;
	}

	public int getGridno() {
		return gridno;
	}

	public void setGridno(int gridno) {
		this.gridno = gridno;
	}
	@Override
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            return null;
        }
    }

	public int getNhide() {
		return nhide;
	}

	public void setNhide(int nhide) {
		this.nhide = nhide;
	}

	public String getOld_fieldType() {
		return old_fieldType;
	}

	public void setOld_fieldType(String old_fieldType) {
		if (old_fieldType==null) {
			old_fieldType="";
		}
		this.old_fieldType = old_fieldType;
	}
	
}
