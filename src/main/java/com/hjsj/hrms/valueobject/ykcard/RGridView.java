/*
 * Created on 2005-4-29
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.valueobject.ykcard;

import com.hjsj.hrms.businessobject.ykcard.XmlSubdomain;

import java.io.Serializable;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RGridView implements Serializable, Cloneable {
	private String tabid = null; //Key
	private String gridno = null; //单元格索引号(Key)
	private String chz = null; //单元格汉字描述
	private String rleft = null; //位置
	private String rtop = null; //位置	
	private String rwidth = null; //位置
	private String rheight = null; //位置
	private String rleft_0 = null; //实际位置
	private String rtop_0=null;//实际位置高
	private String rwidth_0=null;//实际宽度
	private String rheight_0=null;//实际高度
	private String csetname = null; //指标所在的子集名称
	private String field_name = null; //字段英文名
	private String field_hz = null; //字段汉字名称
	private String codeid = null; //相关代码类
	private int rcount; //子集记录数
	private String mode = null;
	//子集记录控制[0,1,2,3,4,5,6,7,8]=[最近第,最近,最初第,最初,条件, 条件最近第,条件最近,条件最初第,条件最初]
	private String field_type = null; //字段数值类型D:日期型A:字符型N：数值型C：代码型
	private int slope; //1,2,3,4对数值型为数值精度{对日期型而言6 7}
	private String l = null; //单元格左边线是否存在？0：无线1：有线，画图时用虚线代替
	private String t = null; //上边
	private String r = null; //右边
	private String b = null; //下边
	private String sl = null; //0:无斜线1左斜线2右斜线3两斜线
	private String fontsize = null; //单元格字体大小
	private String fontname = null; //单元格字体名称
	private String fonteffect = null; //单元格字体Effect;
	private String querycond = null; //查询条件或者表达式
	private String cexpress = null; //用于保存单元计算公式或历史记录定位表达式1+2*3
	private String align = null; //文字在单元排列方式
	private String lsize = null; //左边线的粗细
	private String rsize = null; //右边线的粗细
	private String tsize = null; //上边线的粗细
	private String bsize = null; //底边线的粗细
	private String flag = null; //控制数据来源A：人员库B：单位库P：照片：H：文本C：计算结果
	private String nhide = null; //控制输出内容//0:打印1:隐藏
	private String strpre = null; //对日期及数值类型增加前缀符号
	private String pageid = null; //页签号(Key)
	private String temp = null;
	private String minrleft=null;
	private String minrtop=null;
	private String Sub_domain=null;//
	private String func;//函数选项：0: 无, 1: 求和
	private String subflag;//1:表示子集
	private String plan_id;
	private String isView;
	private String extflag;
	private String extflag2;
	public String getPlan_id() {
		return plan_id;
	}
	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}
	public String getSubflag() {
		if(subflag==null||subflag.length()<=0)
			subflag="";
		return subflag;
	}
	public void setSubflag(String subflag) {
		this.subflag = subflag;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		if(func==null||func.length()<=0)
			func="0";
		this.func = func;
	}
	public String getSub_domain() {
		return Sub_domain;
	}
	public void setSub_domain(String sub_domain) {
		
		Sub_domain = sub_domain;
		XmlSubdomain xmlSubdomain=new XmlSubdomain(sub_domain);
		String func=xmlSubdomain.getFunc();
		this.setFunc(func);
	}
	public RGridView() {
	}
	public boolean isCode() {
		return codeid != null && !"".equals(codeid) && !"0".equals(codeid);
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
	public String getBsize() {
		return bsize;
	}
	public void setBsize(String bsize) {
		this.bsize = bsize;
	}
	public String getCexpress() {
		return cexpress;
	}
	public void setCexpress(String cexpress) {
		this.cexpress = cexpress;
		if(cexpress != null) {
		    if(cexpress.indexOf("<EXT_FLAG>")!=-1)
	            extflag=cexpress.substring(cexpress.indexOf("<EXT_FLAG>")+"<EXT_FLAG>".length(),cexpress.indexOf("</EXT_FLAG>"));
            if(cexpress.indexOf("<EXT_FLAG2>")!=-1)
                extflag2=cexpress.substring(cexpress.indexOf("<EXT_FLAG2>")+"<EXT_FLAG2>".length(),cexpress.indexOf("</EXT_FLAG2>"));
		}
	}
	public String getCHz() {
		return chz;
	}
	public void setCHz(String chz) {
		this.chz = chz;
	}
	public String getCodeid() {
		return codeid;
	}
	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}
	public String getCSetName() {
		return csetname;
	}
	public void setCSetName(String csetname) {
		this.csetname = csetname;
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
	public String getFonteffect() {
		return fonteffect;
	}
	public void setFonteffect(String fonteffect) {
		this.fonteffect = fonteffect;
	}
	public String getFontName() {
		return fontname;
	}
	public void setFontName(String fontname) {
		this.fontname = fontname;
	}
	public String getFontsize() {
		return fontsize;
	}
	public void setFontsize(String fontsize) {
		this.fontsize = fontsize;
	}
	public String getGridno() {
		return gridno;
	}
	public void setGridno(String gridno) {
		this.gridno = gridno;
	}
	public String getL() {
		return l;
	}
	public void setL(String l) {
		this.l = l;
	}
	public String getLsize() {
		return lsize;
	}
	public void setLsize(String lsize) {
		this.lsize = lsize;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getNHide() {
		return nhide;
	}
	public void setNHide(String nhide) {
		this.nhide = nhide;
	}
	public String getPageId() {
		return pageid;
	}
	public void setPageId(String pageid) {
		this.pageid = pageid;
	}
	public String getQuerycond() {
		return querycond;
	}
	public void setQuerycond(String querycond) {
		this.querycond = querycond;
	}
	public String getR() {
		return r;
	}
	public void setR(String r) {
		this.r = r;
	}
	public int getRcount() {
		return rcount;
	}
	public void setRcount(int rcount) {
		this.rcount = rcount;
	}
	public String getRheight(String distinguish) {
		if (rheight != null && rheight.length() > 0)
			temp =String.valueOf((int) ((Float.parseFloat(rheight) * 800 / 1024) + 1));
		else
			temp = rheight;
		return temp;
	}
	public String getRheight() {
		return rheight;
	}
	public void setRheight(String rheight) {
		this.rheight = rheight;
	}
	public String getRleft(String distinguish) {
		if (rleft != null && rleft.length() > 0)
	      temp = String.valueOf((int) (Float.parseFloat(rleft) * 800 / 1024));
		else
		  temp = rleft;
		return temp;
	}
	public String getRleft() {
		 return rleft;
	}
	public void setRleft(String rleft) {
		this.rleft = rleft;
	}
	public String getRsize() {
		return rsize;
	}
	public void setRsize(String rsize) {
		this.rsize = rsize;
	}
	public String getRtop(String distinguish) {
		if (rtop != null && rtop.length() > 0)
			temp = String.valueOf((int) (Float.parseFloat(rtop) * 800 / 1024));
		else
			temp = rtop;
		return temp;
	}
	public String getRtop() {
	  return rtop;
	}
	public void setRtop(String rtop) {
		this.rtop = rtop;
	}
	public String getRwidth(String distinguish) {
		if (rwidth != null && rwidth.length() > 0)
			temp =String.valueOf(Math.round(Float.parseFloat(rwidth) * 800 / 1024));
		else
			temp = rwidth;
		return temp;
	}
	public String getRwidth() {
		return rwidth;
	}
	public void setRwidth(String rwidth) {
		this.rwidth = rwidth;
	}
	public String getSL() {
		return sl;
	}
	public void setSL(String sl) {
		this.sl = sl;
	}
	public int getSlope() {
		return slope;
	}
	public void setSlope(int slope) {
		this.slope = slope;
	}
	public String getStrPre() {
		return strpre;
	}
	public void setStrPre(String strpre) {
		this.strpre = strpre;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getTsize() {
		return tsize;
	}
	public void setTsize(String tsize) {
		this.tsize = tsize;
	}
	/**
	 * @return Returns the minrleft.
	 */
	public String getMinrleft() {
		return minrleft;
	}
	/**
	 * @param minrleft The minrleft to set.
	 */
	public void setMinrleft(String minrleft) {
		this.minrleft = minrleft;
	}
	/**
	 * @return Returns the minrtop.
	 */
	public String getMinrtop() {
		return minrtop;
	}
	/**
	 * @param minrtop The minrtop to set.
	 */
	public void setMinrtop(String minrtop) {
		this.minrtop = minrtop;
	}
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
	public String getIsView() {
		return isView;
	}
	public void setIsView(String isView) {
		this.isView = isView;
	}
	public String getRleft_0() {
		return rleft_0;
	}
	public void setRleft_0(String rleft_0) {
		this.rleft_0 = rleft_0;
	}
	public String getRtop_0() {
		return rtop_0;
	}
	public void setRtop_0(String rtop_0) {
		this.rtop_0 = rtop_0;
	}
	public String getRwidth_0() {
		return rwidth_0;
	}
	public void setRwidth_0(String rwidth_0) {
		this.rwidth_0 = rwidth_0;
	}
	public String getRheight_0() {
		return rheight_0;
	}
	public void setRheight_0(String rheight_0) {
		this.rheight_0 = rheight_0;
	}
    public String getExtflag() {
        return extflag;
    }
    public void setExtflag(String extflag) {
        this.extflag = extflag;
    }
    public String getExtflag2() {
        return extflag2;
    }
    public void setExtflag2(String extflag2) {
        this.extflag2 = extflag2;
    }	
}
