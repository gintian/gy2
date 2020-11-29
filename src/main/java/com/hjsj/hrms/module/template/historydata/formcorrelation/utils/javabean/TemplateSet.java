/**
 * 
 */
package com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 模板指标属性
* @Title: TemplateSet
* @Description:
* @author: hej
* @date 2019年11月19日 下午5:15:03
* @version
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
	private String attachmentXml="";//保存附件设置，获取附件显示哪个分类。	
	private String display_e0122="0";
	/**业务类型
	 * 对人员调入的业务单独处理
	 * =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3调动,
	 * =10其它不作特殊处理的业务
	 * 如果目标库未指定的话，则按源库进行处理
	 */	
	private int operationtype=10;
	private Connection conn;
    public String getImppeople() {
		return imppeople;
	}


	public void setImppeople(String imppeople) {
		this.imppeople = imppeople;
	}


	public TemplateSet() {
	
	}
	
	
	public TemplateSet(Connection conn) {
		this.conn=conn;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
			display_e0122="0";
	}

	
	
	public TemplateSet(Connection conn,String _display_e0122) {
		this.conn=conn;
		display_e0122=_display_e0122;
		if(_display_e0122==null|| "00".equals(_display_e0122)|| "".equals(_display_e0122))
			display_e0122="0";
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


	public String getAttachmentXml() {
		return attachmentXml;
	}


	public void setAttachmentXml(String attachmentXml) {
		this.attachmentXml = attachmentXml;
	}


	public void setOperationtype(int operationtype) {
		this.operationtype = operationtype;
	}


	public void setDisplay_e0122(String display_e0122) {
		this.display_e0122 = display_e0122;
	}


	/**
	 * 实际取数
	 * A：人员库B：单位库K:职位库P：照片H：文本C：计算结果V:临时变量
	 * @param dbpre 库前缀
	 * @param a0100 人员编号
	 * @param archMap 业务模板对应的此人的数据
	 * @param userview 登录用户对象
	 * @return
	 */
	public String getCellContent(String dbpre,String a0100,HashMap archMap,UserView userview)
	{
		String strc="";
		if(this.getNhide()==0){
			boolean bdr=false;
			if(dbpre==null|| "".equals(dbpre))
				bdr=true;
			if(this.getFlag()==null|| "".equalsIgnoreCase(this.getFlag()))
				this.setFlag("H");
			if(this.getHz()==null)
				this.setHz("");
			char cflag=this.getFlag().charAt(0);
			ArrayList strlist=new ArrayList();
			StringBuffer buf=new StringBuffer();
			int i=0;
			switch(cflag){
				case 'A': //人员库
				case 'B'://单位库
				case 'K'://职位库
					if((this.getHismode()==2||this.getHismode()==3||this.getHismode()==4)&&(!bdr)&&this.getChgstate()==1){
						 strlist=getTemplateFieldValue(archMap);
						 if(strlist.size()>0){
							 String[] str = strlist.get(0).toString().split("`");
							 strlist.clear();
							 for(int n =0;n<str.length;n++){
								 if(str[n].length()>0)
								 strlist.add(str[n]);
							 }
						 }
					}
					else
						strlist=getTemplateFieldValue(archMap);
					for(i=0;i<strlist.size();i++){
						buf.append(strlist.get(i));
						if(i<strlist.size())
							buf.append("\n");
					}			
					strc=buf.toString();
					break;				
				case 'C'://计算公式
					break;
				case 'V'://变量
					strlist=getTemplateVarFieldValue(archMap);
					for(i=0;i<strlist.size();i++){
						buf.append(strlist.get(i));
						if(i<strlist.size())
							buf.append("\n");
					}
					strc=buf.toString();
					break;
				case 'P'://照片
					if(this.operationtype!=0){
						strc=createPhotoFile(archMap);
					}else{
						strc=createPhotoFile(archMap);
					}
					break;
				case 'S'://电子签章
					break;
				default://'H'
					strc=this.getHz().replaceAll("`","\n");
					break;
			}
		}
		return strc;
	}
	/**
	 * 从模板表中取数
	 * @param archMap
	 * @return
	 */
	private ArrayList getTemplateFieldValue(HashMap archMap)
	{
		ArrayList list=new ArrayList();
		String field_type=this.getField_type();
		if(field_type==null|| "".equals(field_type))
			return list;
		String field_name=null;	
		String format=this.getFormula();
		try{	
			if(format!=null&&format.indexOf("<EXPR>")!=-1){
				int f=format.indexOf("<EXPR>");
				int t=format.indexOf("</FACTOR>"); 
				String _temp=format.substring(0,f);
				String _temp2=format.substring(t+9);
				format=_temp+_temp2; 
			} 

			if(this.getChgstate()==0)
				field_name=this.getField_name();
			else{
				field_name=this.getField_name()+"_"+this.getChgstate();
				if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
				field_name=this.getField_name()+"_"+this.sub_domain_id+"_"+this.getChgstate();
				}
			}
			if("M".equalsIgnoreCase(field_type)){
				//判断数据字典里的指标类型
				FieldItem item=DataDictionary.getFieldItem(this.getField_name());
				if(item!=null&&item.getItemtype()!=null){
					if("M".equalsIgnoreCase(item.getItemtype())){
						list.add(archMap.get(field_name.toLowerCase()));
					}	
					else if("D".equalsIgnoreCase(item.getItemtype())){
						/**yyyy-MM-dd*/
						String str = (String) archMap.get(field_name.toLowerCase());
						String values ="";
						if(str.indexOf("`")!=-1){
							String[] strs =str.split("`");
							for(int i=0;i<strs.length;i++){
								if(strs[i].trim().length()>0){
									values += formatDateValue(strs[i],format);
									if(i<strs.length-1){
										values+="`";
									}
								}
							}
						}else{
							values = formatDateValue(str,format);
						}
						list.add(values);
					}
					else if("N".equalsIgnoreCase(item.getItemtype())){
						int ndec=this.getDisformat();//小数点位数
						String prefix=((format==null||"null".equalsIgnoreCase(format))?"":format);
						String str = (String) archMap.get(field_name.toLowerCase());
						String values ="";
						if(str.indexOf("`")!=-1){
							String[] strs =str.split("`");
							for(int i=0;i<strs.length;i++){
								if(strs[i].trim().length()>0){
									values += prefix+PubFunc.DoFormatDecimal(strs[i],ndec);
									if(i<strs.length-1){
										values+="`";
									}
								}
							}
						}else{
							values = prefix+PubFunc.DoFormatDecimal(str,ndec);
						}
						list.add(values);
						
					}else{
						String str = (String) archMap.get(field_name.toLowerCase());
						String values ="";
						if(str.indexOf("`")!=-1){
							String[] strs =str.split("`");
							for(int i=0;i<strs.length;i++){
								if(strs[i].trim().length()>0){
									if(this.getCodeid()!=null&&!"0".equals(this.getCodeid())){
									values += AdminCode.getCodeName(this.getCodeid(),strs[i]);
									}else
										values += strs[i];
									if(i<strs.length-1){
										values+="`";
									}
								}
							}
						}else{
							if(this.getCodeid()!=null&&!"0".equals(this.getCodeid())){
							values = AdminCode.getCodeName(this.getCodeid(),str);
							}else
								values = str;	
						}
						list.add(values);
					}
				}
			}
			else if("D".equalsIgnoreCase(field_type)){
				/**yyyy-MM-dd*/
				String datevalue=(String) archMap.get(field_name.toLowerCase());
				list.add(formatDateValue(datevalue,format));
			}
			else if("N".equalsIgnoreCase(field_type)){
				int ndec=this.getDisformat();//小数点位数
				String prefix=((format==null||"null".equalsIgnoreCase(format))?"":format);
				list.add(prefix+PubFunc.DoFormatDecimal((String) archMap.get(field_name.toLowerCase()),ndec));
			}
			else{
				String codevalue=(String) archMap.get(field_name.toLowerCase());
				codevalue=((codevalue==null)?"":codevalue.trim());					
				if(!this.isBcode()){
					if(field_name!=null&&field_name.toLowerCase().startsWith("codesetid_")){
						if("UM".equalsIgnoreCase(codevalue))
							codevalue="部门";
						else if("UN".equalsIgnoreCase(codevalue))
							codevalue="单位";
					}
						
					list.add(codevalue);
				}
				else{
					if("UM".equalsIgnoreCase(this.getCodeid())&&AdminCode.getCodeName(this.getCodeid(),codevalue).trim().length()==0){
						list.add(AdminCode.getCodeName("UN",codevalue));
					}
					else{
						if("UM".equalsIgnoreCase(this.getCodeid())&&Integer.parseInt(display_e0122)>0){
							CodeItem item=AdminCode.getCode("UM",codevalue,Integer.parseInt(display_e0122));
							if(item!=null){
			    	    		list.add(item.getCodename());
			        		}
			    	    	else{
			    	    		list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
			    	    	}
						}
						else
							list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
		   
		}
		return list;
	}
	private ArrayList getTemplateVarFieldValue(HashMap archMap){
		ArrayList list=new ArrayList();
		String field_type=this.getField_type();
		if(field_type==null|| "".equals(field_type))
			return list;
		String field_name=null;	
		String format=this.getFormula();
		try{
			field_name=this.getField_name().toLowerCase();
			if("M".equalsIgnoreCase(field_type)){
				list.add((String) archMap.get(field_name));
			}
			else if("D".equalsIgnoreCase(field_type)){
				String datevalue=(String) archMap.get(field_name);
				list.add(formatDateValue(datevalue,format));
			}
			else if("N".equalsIgnoreCase(field_type)){
				int ndec=this.getDisformat();//小数点位数
				String prefix=((format==null)?"":format);
				list.add(prefix+PubFunc.DoFormatDecimal((String) archMap.get(field_name),ndec));
			}
			else{
				String codevalue=(String) archMap.get(field_name);
				codevalue=((codevalue==null)?"":codevalue);					
				if(!this.isBcode())
					list.add(codevalue);
				else
				{
					list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 格式化日期字符串
	 * @param value 日期字段值 yyyy-mm-dd
	 * @param ext 扩展
	 * @return
	 */
	private String formatDateValue(String value,String ext)
	{
		StringBuffer buf=new StringBuffer();
		if(ext!=null&&ext.indexOf("<EXPR>")!=-1){
			int f=ext.indexOf("<EXPR>");
			int t=ext.indexOf("</FACTOR>"); 
			String _temp=ext.substring(0,f);
			String _temp2=ext.substring(t+9);
			ext=_temp+_temp2; 
		}
		
		int idx=ext.indexOf(",");  //-,至今
		String prefix="",strext="";
		if(idx==-1){
			String[] preCond=getPrefixCond(ext);
			prefix=preCond[0];
		}
		else{
			prefix=ext.substring(0,idx);
			strext=ext.substring(idx+1);
		}
		if(StringUtils.isBlank(value)){
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		}
		else{
			buf.append(prefix);
		}
		value=value.replaceAll("[^(0-9)]", "-");//使用正则强制将非数字替换成-。bugUnparseable date: "2017.05.11"
		if(value.endsWith("-")){
			value=value.substring(0,value.length()-1);
		}
		String fomart="yyyy-MM-dd";//有些日期格式不带日期，导致转换错误
		if(value.split("-").length==2){
			fomart="yyyy-MM";
		}
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(fomart);
	    try {
	        format.setLenient(false);
	        format.parse(value);
	        date = DateUtils.getDate(value,fomart);
	    } catch (Exception e) {
	        return buf.toString();
	    }
		int year=DateUtils.getYear(date);
		int month=DateUtils.getMonth(date);
		int day=DateUtils.getDay(date);
		String[] strv =exchangNumToCn(year,month,day);
		value=value.replaceAll("-",".");
		switch(this.getDisformat())
		{
		case 6: //1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 7: //91.12.3
			if(year>=2000)
				buf.append(year);
			else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 8://1991.2
			buf.append(year);
			buf.append(".");
			buf.append(month);			
			break;
		case 9://1992.02
			buf.append(value.substring(0,7));
			break;
		case 10://92.2
			if(year>=2000)
				buf.append(year);
			else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			break;
		case 11://98.02
			if(year>=2000)
				buf.append(year);
			else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			if(month>=10)
				buf.append(month);
			else
			{
				buf.append("0");
				buf.append(month);
			}
			break;
		case 12://一九九一年一月二日

			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");
			buf.append(strv[2]);
			buf.append("日");
			break;
		case 13://一九九一年一月
			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");			
			break;
		case 14://1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 15://1991年1月
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			break;
		case 16://91年1月2日
			if(year>=2000)
				buf.append(year);
			else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 17://91年1月
			if(year>=2000)
				buf.append(year);
			else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");			
			break;
		case 18://年龄
			buf.append(getAge(year,month,day));
			break;
		case 19://1991（年）
			buf.append(year);
			break;
		case 20://1 （月）
			buf.append(month);
			break;
		case 21://23 （日）
			buf.append(day);
			break;
		case 22://1999年02月
			buf.append(year);
			buf.append("年");
			if(month>=10)
				buf.append(month);
			else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		case 23://1999年02月03日
			buf.append(year);
			buf.append("年");
			if(month>=10)
				buf.append(month);
			else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if(day>=10)
				buf.append(day);
			else
			{
				buf.append("0");
				buf.append(day);
			}		
			buf.append("日");
			break;
		case 24://1992.02.01
			buf.append(year);
			buf.append(".");
			if(month>=10)
				buf.append(month);
			else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10)
				buf.append(day);
			else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);			
			break;
		}
		return buf.toString();
	}
	/**
	 * 创建照片
	 * @param userTable
	 * @param userNumber
	 * @param flag
	 * @return
	 * @throws Exception
	 */
    public  String createPhotoFile(String a00tab, String a0100, String flag) {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;  
        PreparedStatement pstmt=null;
        InputStream in = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ext,Ole from ");
            strsql.append(a00tab);
            strsql.append(" where A0100='");
            strsql.append(a0100);
            strsql.append("' and Flag='");
            strsql.append(flag);
            strsql.append("'");

            pstmt=conn.prepareStatement(strsql.toString());
            rs=pstmt.executeQuery();   
            if (rs.next()) {
                java.io.FileOutputStream fout = null;
                try {
                	tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
                			new File(System.getProperty("java.io.tmpdir")));             
                	in = rs.getBinaryStream("Ole");                
                	fout = new java.io.FileOutputStream(tempFile);                
                	int len;
                	byte[] buf = new byte[1024];
                	
                	while ((len = in.read(buf, 0, 1024)) != -1) {
                		fout.write(buf, 0, len);
                		
                	}
                } finally {
                	PubFunc.closeDbObj(fout);
                }
               
                filename= tempFile.getName();                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeIoResource(in);
        	PubFunc.closeResource(pstmt);
        	PubFunc.closeResource(rs);
        }
        return filename;
    }
    /**
	 * 创建照片
	 * @param userTable
	 * @param userNumber
	 * @param flag
	 * @return
	 * @throws Exception
	 */
    public  String createPhotoFile(HashMap archMap) {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        InputStream in =null;
        FileOutputStream fout =null;
        try {
            String ext=(String) archMap.get("ext");
            if(ext==null|| "".equalsIgnoreCase(ext))
            {
            	return "";
            }
            tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, (String) archMap.get("ext"),
                    new File(System.getProperty("java.io.tmpdir")));             
            String content = (String) archMap.get("photo");  
            byte [] markbytes = Base64.decodeBase64(content);
			in = new ByteArrayInputStream(markbytes);
            fout = new FileOutputStream(tempFile);                
            int len;
            byte[] buf = new byte[1024];
        
            while ((len = in.read(buf, 0, 1024)) != -1) {
                fout.write(buf, 0, len);
            }
           
            filename= tempFile.getName();
               
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	PubFunc.closeIoResource(in);
        	PubFunc.closeIoResource(fout);
        }
        return filename;
    }
    /**
	 * 解释Formula字段的内容
	 * for example
	 * ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * @return
	 */
	  private String[] getPrefixCond(String formula)
	  {
		   String[] preCond=new String[3];
		   int idx=formula.indexOf("<");
		   if(idx==-1)
		   {
			   preCond[0]=formula; 
		   }
		   else
		   {
			   preCond[0]=formula.substring(0, idx);
			   preCond[2]=getPattern("FACTOR",formula)+",";
			   preCond[2]=preCond[2].replaceAll(",", "`");
			   preCond[1]=getPattern("EXPR",formula);
		   }
		   return preCond;
	  }	
	  private String getPattern(String strPattern,String formula){
			int iS,iE;
			String result="";
			String sSP="<"+strPattern+">";
			iS=formula.indexOf(sSP);
			String sEP="</"+strPattern+">";
			iE=formula.indexOf(sEP);
			if(iS>=0 && iS<iE)
			{
				result=formula.substring(iS+sSP.length(), iE);
			}
			return result;
	   }
	   /**
		 * 数字换算
		 * @param strV
		 * @param flag
		 * @return
		 */
		private String[] exchangNumToCn(int year,int month,int day){
			String[] strarr=new String[3];
			StringBuffer buf=new StringBuffer();
			String value=String.valueOf(year);
			for(int i=0;i<value.length();i++){
				switch(value.charAt(i))
				{
				case '1':
					buf.append("一");
					break;
				case '2':
					buf.append("二");
					break;
				case '3':
					buf.append("三");
					break;
				case '4':
					buf.append("四");
					break;
				case '5':
					buf.append("五");
					break;
				case '6':
					buf.append("六");
					break;
				case '7':
					buf.append("七");
					break;
				case '8':
					buf.append("八");
					break;
				case '9':
					buf.append("九");
					break;
				case '0':
					buf.append("零");
					break;
				}
			}
			strarr[0]=buf.toString();
			buf.setLength(0);
			switch(month)
			{
			case 1:
				buf.append("一");
				break;
			case 2:
				buf.append("二");
				break;
			case 3:
				buf.append("三");
				break;
			case 4:
				buf.append("四");
				break;
			case 5:
				buf.append("五");
				break;
			case 6:
				buf.append("六");
				break;
			case 7:
				buf.append("七");
				break;
			case 8:
				buf.append("八");
				break;
			case 9:
				buf.append("九");
				break;
			case 10:
				buf.append("十");
				break;			
			case 11:
				buf.append("十一");
				break;
			case 12:
				buf.append("十二");
				break;
			}
			strarr[1]=buf.toString();
			buf.setLength(0);
			switch(day)
			{
			case 1:
				buf.append("一");
				break;
			case 2:
				buf.append("二");
				break;
			case 3:
				buf.append("三");
				break;
			case 4:
				buf.append("四");
				break;
			case 5:
				buf.append("五");
				break;
			case 6:
				buf.append("六");
				break;
			case 7:
				buf.append("七");
				break;
			case 8:
				buf.append("八");
				break;
			case 9:
				buf.append("九");
				break;
			case 10:
				buf.append("十");
				break;			
			case 11:
				buf.append("十一");
				break;
			case 12:
				buf.append("十二");
				break;			
			case 13:
				buf.append("十三");
				break;			
			case 14:
				buf.append("十四");
				break;			
			case 15:
				buf.append("十五");
				break;			
			case 16:
				buf.append("十六");
				break;			
			case 17:
				buf.append("十七");
				break;			
			case 18:
				buf.append("十八");
				break;			
			case 19:
				buf.append("十九");
				break;			
			case 20:
				buf.append("二十");	
				break;			
			case 21:
				buf.append("二十一");
				break;			
			case 22:
				buf.append("二十二");	
				break;			
			case 23:
				buf.append("二十三");
				break;			
			case 24:
				buf.append("二十四");	
				break;			
			case 25:
				buf.append("二十五");
				break;			
			case 26:
				buf.append("二十六");	
				break;			
			case 27:
				buf.append("二十七");
				break;			
			case 28:
				buf.append("二十八");	
				break;			
			case 29:
				buf.append("二十九");
				break;			
			case 30:
				buf.append("三十");	
				break;			
			case 31:
				buf.append("三十一");				
				break;
			}		
			strarr[2]=buf.toString();
			return strarr;
		}
		/**
		 * 计算年龄
		 * @param nyear
		 * @param nmonth
		 * @param nday
		 * @return
		 */
		private String getAge(int nyear,int nmonth,int nday){
			int ncyear,ncmonth,ncday;
			Date curdate=new Date();
			ncyear=DateUtils.getYear(curdate);
			ncmonth=DateUtils.getMonth(curdate);
			ncday=DateUtils.getDay(curdate);
			StringBuffer buf=new StringBuffer();
			int result =ncyear-nyear;   
	        if   (nmonth>ncmonth)   {   
	            result = result-1;   
	        }   
	        else 
	        {
	            if   (nmonth==ncmonth)  {   
	                if   (nday >ncday)   {   
	                    result   =   result   -   1;   
	                }   
	            }   
	        }
			buf.append(result);
			return buf.toString();
		}
}
