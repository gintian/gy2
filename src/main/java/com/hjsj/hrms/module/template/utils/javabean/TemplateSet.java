/**
 * 
 */
package com.hjsj.hrms.module.template.utils.javabean;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>Title:TemplateSetBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-11-20 上午10:37:39</p>
 * <p>@version: 7.0</p>
 */
public class TemplateSet implements Serializable, Cloneable {
    /**当前使用的模板号*/
    private int tabId;
    /**当前使用的页签号*/
    private int pageId;    
	/**单元格编号*/
	private int gridno;
	/**以"`"作为单元格内容换行*/	
	private String hz;
	private String setname;
	private String field_name;
	/**
	 * 字段数值类型  D:日期型   A:字符型    N：数值型    M:备注型
	 */
	private String field_type;
	private String field_hz;
	
	/** 
	 * 字段的原始类型，与数据库中相同， 
	 * 如果是取多条记录，此单元格的类型会更改为M 新的存放在field_type
	 */
	private String old_fieldType;

	/**
	 *相关代码类 
	 */
	private String codeid;
	private boolean bcode;

	/**
	 * A：人员库    B：单位库  K:职位库   P：照片     H：文本    C：计算结果   V:临时变量 F:附件
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
     * =4, 条件序号
	 */
	private int hismode;
	/**数据显示格式  1,2,3,4对数值型为数值精度 后面是对时间的控制*/
	private int disformat;
	/**相同指标的序号，目前未用到*/
	private int nsort;
	/**记录数*/
	private int rcount;
	/**
	 * 子集记录控制
	 *=2：倒数...条记录,=1：倒数第...条记录,=4: 正数...条记录,
	 *=3:正数第...条记录,=5.子集记录以查询条件为主
	 *[0,1,2,3]=[最近第,最近,最初第,最初,
	 *  条件:条件最近第,条件最近,条件最初第,条件最初] 
	 * ORACLE库字段名为mode_o
	 */
	private int mode ;
	/**计算公式及条件
	 * ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * */
	private String formula;

	private int nhide;
	
    
	/**变化前，变化后标识 =1变化前 =2变化后 */
	private int chgstate;
	/**宽度及高度,整个表格的*/
	private Rectangle rect=new Rectangle(0,0,0,0);
	/**是否必填*/
	private boolean yneed=false; 	
	/**子集控制位*/
	private boolean subflag=false;
	/**控制参数*/
	private String xml_param;  
	/**多个同名指标的标识*/
	private String sub_domain_id="";	
	/**存储在数据库中的字段名*/
	private String tableFieldName="";		
	/**能标识此单元格唯一的值*/
	private String uniqueId="";	
	
/**其他参数值  */	
	/**子集区域*/
	private SubSetDomain subdomain=null;
	/**子集显示指标列表*/
	private ArrayList subFieldList = new ArrayList();
	/**子集显示指标 逗号分隔*/
	private String subFields="";
	/**临时变量*/
    private RecordVo varVo;

	/**前缀字符串*/
	private String preCon="";
	/**单位、部门、岗位是否按管理范围控制*/
	private boolean bLimitManagePriv=false;
	/**附件类型 ，个人：1 ，    公共： 0*/
	private String attachmentType="0";
	/** 临时变量 只读控制 1:只读 2：可编辑*/
	private String readOnly = "2";//默认可编辑
	/** 联动指标*/
	private String relation_field="";
	/** 默认值*/
	private String defaultValue="";
	private String imppeople="";
	private String isMobile=""; // 0 电脑 1 手机
    public String getImppeople() {
		return imppeople;
	}


	public void setImppeople(String imppeople) {
		this.imppeople = imppeople;
	}


	public TemplateSet() {
	
	}
   
	
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
	    if (formula==null) formula="";
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
		if (this.codeid==null || "".equals(this.codeid)){
		    this.codeid="0";
		}
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
	    if (field_hz==null) field_hz="";
		this.field_hz = field_hz;
	}
	public String getField_name() {
	    if (field_name==null) field_name="";
		return field_name;
	}
	public void setField_name(String field_name) {
	    if (field_name==null) field_name="";
		this.field_name = field_name;
	}
	public String getField_type() {
		return field_type;
	}
	public void setField_type(String field_type) {
	    if (field_type==null) field_type="";
		this.field_type = field_type;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
	    if (flag==null) flag="";
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
	    if (hz==null) hz="";
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
        catch(Exception e)
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
	
	
	public int getChgstate() {
	   if (chgstate==0 && !"V".equals(this.getFlag()) ){//临时变量为0
            chgstate=1;
        }
		return chgstate;
	}

	public void setChgstate(int chgstate) {
		this.chgstate = chgstate;
	}
	
	/**
	 * 是否为代码
	 * @return
	 */
	public boolean isBcode() {
		String temp=this.getCodeid();
		if(temp==null|| "0".equals(temp)|| "".equals(temp))
			return false;
		else
			return true;
	}

	public boolean isSubflag() {
		return subflag;
	}

	public void setSubflag(boolean subflag) {
		this.subflag = subflag;
	}

	public String getXml_param() {
		return xml_param;
	}

	public void setXml_param(String xml_param) {
		this.xml_param = xml_param;
		parserSubDomain(xml_param);
	}

	public boolean isYneed() {
		return yneed;
	}

	public void setYneed(boolean yneed) {
		this.yneed = yneed;
	}
	
	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	public String getSub_domain_id() {
		return sub_domain_id;
	}
	public void setSub_domain_id(String sub_domain_id) {
		this.sub_domain_id = sub_domain_id;
	}

	public String getPreCon() {
		return preCon;
	}
	public void setPreCon(String preCon) {
		this.preCon = preCon;
	}
	public RecordVo getVarVo() {
		return varVo;
	}
	public ArrayList getSubFieldList() {
		return subFieldList;
	}
	public void setSubFieldList(ArrayList suTableList) {
		this.subFieldList = suTableList;
	}
	public void setVarVo(RecordVo varVo) {
		this.varVo = varVo;
	}
	public String getSubFields() {
		return subFields;
	}
	public int getTabId() {
		return tabId;
	}
	public void setTabId(int tabId) {
		this.tabId = tabId;
	}
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	/**获取单元格的字段名 */
	public String getTableFieldName() {
		if (tableFieldName == null || "".equals(tableFieldName)) {
			tableFieldName="";
			if (!"H".equals(this.getFlag())) {
				if ("V".equals(this.getFlag())){//临时变量
					tableFieldName = this.getField_name();
				}
				else if ("P".equals(this.getFlag())){//照片
					tableFieldName = "photo";
				}
				else if ("F".equals(this.getFlag())){//附件(个人、公共)
					if("1".equals(this.getAttachmentType()))//个人
						tableFieldName = "attachment_1";
					if("0".equals(this.getAttachmentType()))//公共
						tableFieldName = "attachment_0";
				}
				else if ("S".equals(this.getFlag())){//电子签章
					tableFieldName = "signature";
				}
				else if ("C".equals(this.getFlag())){//计算项 其实在数据库中无字段 为了前台显示数据，设置一个
                    tableFieldName = "calc_"+this.getGridno();
                }
				else {
					if (this.isSubflag()) {
						tableFieldName = "t_" + this.getSetname() + "_";
					} else {
						tableFieldName = this.getField_name() + "_";
					}
					
					if (this.getSub_domain_id() != null && this.getSub_domain_id().length() > 0) {
						tableFieldName = tableFieldName + this.getSub_domain_id()+ "_";
					}
					tableFieldName = tableFieldName + this.getChgstate();
				}
			}
			tableFieldName=tableFieldName.toLowerCase();
		}
		return tableFieldName;
	}
	

	/**是否是ABK指标，即是档案库指标*/
	public boolean isABKItem() {
		boolean b=false;
		if ("A".equalsIgnoreCase(this.getFlag())
				||"B".equalsIgnoreCase(this.getFlag())
				||"K".equalsIgnoreCase(this.getFlag())){
			b=true;
		}
		return b;
		
	}
	/**是否是特殊指标 即codesetid parentid codeitemdesc corcode to_id*/
	public boolean isSpecialItem() {
		boolean b=false;
		if (this.getField_name().length()>0){
			if (",start_date,codesetid,parentid,codeitemdesc,corcode,to_id,".indexOf(this.getField_name())>-1){
				b=true;
			}
		}
		return b;
	}
	/**能标识此单元格唯一的值*/
	public String getUniqueId() {
		this.uniqueId="fld_"+this.pageId+"_"+this.getGridno();
		return uniqueId;
	}
	
	/**解析子集区域  由templatepagebo类挪过来*/
	public void parserSubDomain(String sub_domain) {
		SubSetDomain subdomain=new SubSetDomain(this.xml_param);
		this.sub_domain_id =subdomain.getSubDomainId();
		this.subFields = subdomain.getSubFields();
		this.subFieldList =subdomain.getSubFieldList();
		this.bLimitManagePriv = subdomain.isBLimitManagePriv();
		this.attachmentType = subdomain.getAttachmentType();
		this.readOnly = subdomain.getReadOnly();
		this.imppeople=subdomain.getImppeople();
		this.defaultValue=subdomain.getDefault_value();
		this.relation_field=subdomain.getRelation_field();
	}


	public String getOld_fieldType() {
		return old_fieldType;
	}


	public void setOld_fieldType(String oldFieldType) {
	    if (oldFieldType==null) oldFieldType="";
		old_fieldType = oldFieldType;
	}
	/**是否需要更改字段类型，取多条记录等都需要更改为M*/
	public boolean isNeedChangeFieldType() {
		boolean b=false;
		if (!this.isSubflag() && (this.chgstate==1) 
	      		 &&!"H".equals(flag.toUpperCase())&&!"F".equals(flag.toUpperCase())&&!"S".equals(flag.toUpperCase())) {
			 if (this.getHismode()==2 || this.getHismode()==3 || this.getHismode()==4) {// (序号定位&&(最近||最初))  
				 b=true;
				 if((this.getHismode()==2||this.getHismode()==4)&&(this.getMode()==0||this.getMode()==2)) {//多条记录或者条件序号---选择最近第或者最初第，字段类型不变
					 b=false;
				 }
			 }
		}
		return b ;  
	}
	
	   /**是否是临时变量*/
    public boolean isVarItem() {
        boolean b=false;
        if ("V".equals(this.getFlag())){
            b=true;
        }
        return b;
    }
    /**是否已构库*/
    public boolean isExistsThisField() {
        boolean b=false;
        if ("V".equals(this.getFlag())){
            if (this.varVo!=null)
                b=true;
        }else if (this.isSubflag()){
            FieldSet fieldset=DataDictionary.getFieldSetVo(this.getSetname());
            if(fieldset!=null){
                b=true;
            } 
        }else if (this.isABKItem()){
            FieldItem item = DataDictionary.getFieldItem(this.getField_name());
            if (item != null) {
                b=true;
            }
        }
        if (this.isSpecialItem()) 
            b=true;
        return b;
    }
    
    public boolean isBLimitManagePriv() {
        return bLimitManagePriv;
    }


    public void setBLimitManagePriv(boolean limitManagePriv) {
        bLimitManagePriv = limitManagePriv;
    }


	public String getAttachmentType() {
		return attachmentType;
	}


	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}


	public String getReadOnly() {
		return readOnly;
	}


	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}
	
	public String getRelation_field() {
		return relation_field;
	}


	public void setRelation_field(String relation_field) {
		this.relation_field = relation_field;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public String getIsMobile() {
		return isMobile;
	}


	public void setIsMobile(String isMobile) {
		this.isMobile = isMobile;
	}
	
}
