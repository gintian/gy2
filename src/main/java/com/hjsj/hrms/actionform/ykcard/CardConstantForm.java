/*
 * Created on 2005-4-28
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.actionform.ykcard;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CardConstantForm extends FrameForm {

	private static final long serialVersionUID = 1L;
	
	//记录方式
	private String constant;
	private String type;
	private String str_value;
	private String describe;
	private RecordVo constant_vo=ConstantParamter.getConstantVo("SS_SETCARD");
	
	//表格方式
	private ArrayList fieldSetList = new ArrayList();  //指标集集合
	private ArrayList fieldItemList = new ArrayList(); //指标项集合
	private ArrayList selectedItemList = new ArrayList(); //选中的指标项集合
	private String setid;    //指标集
	private String itemid; //指标项
	private String title;//指标别名
	private String selectedItemId; //选中的指标项
	private ArrayList dateitemlist=new ArrayList();
	private ArrayList employ_field_list=new ArrayList();
	private ArrayList selected_field_List=new ArrayList();
	private String old_mysalarys;
	private String query_field;
	private String changeflag;
	private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[]; 
    /**考勤表格方式*/
    private ArrayList codenamelist=new ArrayList();
    private ArrayList cardnolist=new ArrayList();
    private ArrayList codesetlist=new ArrayList();
    private String fashion_flag;
    /** 是否设置移动标示符，移动设置true，电脑设置false或null*/
    private String mobapp;
    private String codename;
    private String codeitemid;
    private ArrayList cardnomesslist=new ArrayList();
    /** 按类型显示登记表,手机显示信息*/
    private ArrayList mobcardnomesslist=new ArrayList();
    /** 按关联代码类UN的登记表,电脑端显示信息*/
    private String cardidmess;
    /** 按关联代码类UN的登记表,手机端显示信息*/
    private String cardidmessapp;
    private String code_fields[];
    private String orderid;
    private String codesetname;
    private ArrayList cardfieldlist=new ArrayList();
    /********高级花名册*********/
    private ArrayList mustmesslist=new ArrayList();
    private String mustidmess;
    private ArrayList musteredlist=new ArrayList();
    private String mustflag;
    private ArrayList hmusterlist=new ArrayList();
    private ArrayList mustfieldlist=new ArrayList();
    private String recardconstant;//薪酬纪录方式
    
    private String relating;
    private ArrayList relatinglist=new ArrayList();
    private String year_restrict;
    private ArrayList yearlist=new ArrayList(); 
    private ArrayList yklist=new ArrayList();
    
    /**
     * 输出
     */
    @Override
    public void outPutFormHM() {
		this.setConstant_vo((RecordVo)this.getFormHM().get("constant_vo"));
		this.setStr_value((String)this.getFormHM().get("str_value"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
		this.setFieldItemList((ArrayList)this.getFormHM().get("fielditemlist"));
		this.setSelectedItemList((ArrayList)this.getFormHM().get("selectedList"));
		this.setSelected_field_List((ArrayList)this.getFormHM().get("selected_field_List"));
		this.setEmploy_field_list((ArrayList)this.getFormHM().get("employ_field_list"));
		this.setOld_mysalarys((String)this.getFormHM().get("old_mysalarys"));
		this.setQuery_field((String)this.getFormHM().get("query_field"));
		this.setDateitemlist((ArrayList)this.getFormHM().get("dateitemlist"));
		this.setChangeflag((String)this.getFormHM().get("changeflag"));
		this.setCodename((String)this.getFormHM().get("codename"));
		this.setFashion_flag((String)this.getFormHM().get("fashion_flag"));
		this.setCodenamelist((ArrayList)this.getFormHM().get("codenamelist"));
		this.setCardnolist((ArrayList)this.getFormHM().get("cardnolist"));
		this.setCodesetlist((ArrayList)this.getFormHM().get("codesetlist"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setCardnomesslist((ArrayList)this.getFormHM().get("cardnomesslist"));
		this.setMobcardnomesslist((ArrayList)this.getFormHM().get("mobcardnomesslist"));
		this.setCardidmess((String)this.getFormHM().get("cardidmess"));
		this.setCardidmessapp((String)this.getFormHM().get("cardidmessapp"));
		this.setOrderid((String)this.getFormHM().get("orderid"));
		this.setCardfieldlist((ArrayList)this.getFormHM().get("cardfieldlist"));
		this.setCodesetname((String)this.getFormHM().get("codesetname"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setMusteredlist((ArrayList)this.getFormHM().get("musteredlist"));
		this.setMustmesslist((ArrayList)this.getFormHM().get("mustmesslist"));
		this.setMustidmess((String)this.getFormHM().get("mustidmess"));
		this.setMustflag((String)this.getFormHM().get("mustflag"));
		this.setMustfieldlist((ArrayList)this.getFormHM().get("mustfieldlist"));
		this.setHmusterlist((ArrayList)this.getFormHM().get("hmusterlist"));
		this.setType((String)this.getFormHM().get("type"));
		this.setSetid((String)this.getFormHM().get("setid"));
		this.setRecardconstant((String)this.getFormHM().get("recardconstant"));
		this.setRelatinglist((ArrayList)this.getFormHM().get("relatinglist"));
		this.setRelating((String)this.getFormHM().get("relating"));
		this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		this.setYear_restrict((String)this.getFormHM().get("year_restrict"));
		this.setYklist((ArrayList)this.getFormHM().get("yklist"));
		this.setMobapp((String)this.getFormHM().get("mobapp"));
	}
	
	/**
	 * 输入
	 */
	@Override
    public void inPutTransHM() {
		//this.getFormHM().put("constant_vo",(RecordVo)this.getConstant_vo());
		this.getFormHM().put("str_value",str_value);
		this.getFormHM().put("type",type);
		this.getFormHM().put("old_mysalarys",this.getOld_mysalarys());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("codename",this.getCodename());
		this.getFormHM().put("fashion_flag",this.getFashion_flag());	
		this.getFormHM().put("codeitemid",this.getCodeitemid());
		this.getFormHM().put("code_fields",this.getCode_fields());
		this.getFormHM().put("orderid",this.getOrderid());
		this.getFormHM().put("codesetname",this.getCodesetname());
		this.getFormHM().put("title",this.getTitle());
		this.getFormHM().put("mustflag",this.getMustflag());
		this.getFormHM().put("recardconstant",this.getRecardconstant());
		this.getFormHM().put("relating", this.getRelating());
		this.getFormHM().put("year_restrict", this.getYear_restrict());
		this.getFormHM().put("mobapp", this.getMobapp());
	}
	
    public String getCardidmessapp() {
		return cardidmessapp;
	}
	public void setCardidmessapp(String cardidmessapp) {
		this.cardidmessapp = cardidmessapp;
	}

	public ArrayList getYklist() {
		return yklist;
	}
	public void setYklist(ArrayList yklist) {
		this.yklist = yklist;
	}

	public String getYear_restrict() {
		return year_restrict;
	}
	public void setYear_restrict(String year_restrict) {
		this.year_restrict = year_restrict;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}
	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public String getRelating() {
		return relating;
	}
	public void setRelating(String relating) {
		this.relating = relating;
	}

	public ArrayList getRelatinglist() {
		return relatinglist;
	}
	public void setRelatinglist(ArrayList relatinglist) {
		this.relatinglist = relatinglist;
	}

	public String getRecardconstant() {
		return recardconstant;
	}
	public void setRecardconstant(String recardconstant) {
		this.recardconstant = recardconstant;
	}

	/******* 生成花名册**********/
	public ArrayList getCardfieldlist() {
		return cardfieldlist;
	}
	public void setCardfieldlist(ArrayList cardfieldlist) {
		this.cardfieldlist = cardfieldlist;
	}

	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	
	public ArrayList getCardnolist() {
		return cardnolist;
	}
	public void setCardnolist(ArrayList cardnolist) {
		this.cardnolist = cardnolist;
	}

	public ArrayList getCodenamelist() {
		return codenamelist;
	}
	public void setCodenamelist(ArrayList codenamelist) {
		this.codenamelist = codenamelist;
	}

	public ArrayList getCodesetlist() {
		return codesetlist;
	}
	public void setCodesetlist(ArrayList codesetlist) {
		this.codesetlist = codesetlist;
	}
	
	public String getFashion_flag() {
		return fashion_flag;
	}
	public void setFashion_flag(String fashion_flag) {
		this.fashion_flag = fashion_flag;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getChangeflag() {
		return changeflag;
	}
	public void setChangeflag(String changeflag) {
		this.changeflag = changeflag;
	}

	public String getQuery_field() {
		return query_field;
	}
	public void setQuery_field(String query_field) {
		this.query_field = query_field;
	}

	public String getOld_mysalarys() {
		return old_mysalarys;
	}
	public void setOld_mysalarys(String old_mysalarys) {
		this.old_mysalarys = old_mysalarys;
	}

	public ArrayList getEmploy_field_list() {
		return employ_field_list;
	}
	public void setEmploy_field_list(ArrayList employ_field_list) {
		this.employ_field_list = employ_field_list;
	}

	/**
	 * @return
	 */
	public String getConstant() {
		return constant;
	}

	/**
	 * @return
	 */
	public String getDescribe() {
		return describe;
	}

	/**
	 * @return
	 */
	public String getStr_value() {
		return str_value;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setConstant(String string) {
		constant = string;
	}

	/**
	 * @param string
	 */
	public void setDescribe(String string) {
		describe = string;
	}

	/**
	 * @param string
	 */
	public void setStr_value(String string) {
		str_value = string;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * @return
	 */
	public RecordVo getConstant_vo() {
		return constant_vo;
	}

	/**
	 * @param vo
	 */
	public void setConstant_vo(RecordVo vo) {
		constant_vo = vo;
	}

	public ArrayList getFieldItemList() {
		return fieldItemList;
	}

	public void setFieldItemList(ArrayList fieldItemList) {
		this.fieldItemList = fieldItemList;
	}

	public ArrayList getFieldSetList() {
		return fieldSetList;
	}

	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getSelectedItemId() {
		return selectedItemId;
	}

	public void setSelectedItemId(String selectedItemId) {
		this.selectedItemId = selectedItemId;
	}

	public ArrayList getSelectedItemList() {
		return selectedItemList;
	}

	public void setSelectedItemList(ArrayList selectedItemList) {
		this.selectedItemList = selectedItemList;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public ArrayList getSelected_field_List() {
		return selected_field_List;
	}

	public void setSelected_field_List(ArrayList selected_field_List) {
		this.selected_field_List = selected_field_List;
	}

	public ArrayList getDateitemlist() {
		return dateitemlist;
	}

	public void setDateitemlist(ArrayList dateitemlist) {
		this.dateitemlist = dateitemlist;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public String getCodename() {
		return codename;
	}

	public void setCodename(String codename) {
		this.codename = codename;
	}

	public String getCardidmess() {
		return cardidmess;
	}

	public void setCardidmess(String cardidmess) {
		this.cardidmess = cardidmess;
	}

	public ArrayList getCardnomesslist() {
		return cardnomesslist;
	}

	public void setCardnomesslist(ArrayList cardnomesslist) {
		this.cardnomesslist = cardnomesslist;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public String[] getCode_fields() {
		return code_fields;
	}

	public void setCode_fields(String[] code_fields) {
		this.code_fields = code_fields;
	}

	public String getCodesetname() {
		return codesetname;
	}

	public void setCodesetname(String codesetname) {
		this.codesetname = codesetname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList getMusteredlist() {
		return musteredlist;
	}

	public void setMusteredlist(ArrayList musteredlist) {
		this.musteredlist = musteredlist;
	}

	public String getMustflag() {
		return mustflag;
	}

	public void setMustflag(String mustflag) {
		this.mustflag = mustflag;
	}

	public String getMustidmess() {
		return mustidmess;
	}

	public void setMustidmess(String mustidmess) {
		this.mustidmess = mustidmess;
	}

	public ArrayList getMustmesslist() {
		return mustmesslist;
	}

	public void setMustmesslist(ArrayList mustmesslist) {
		this.mustmesslist = mustmesslist;
	}

	public ArrayList getHmusterlist() {
		return hmusterlist;
	}

	public void setHmusterlist(ArrayList hmusterlist) {
		this.hmusterlist = hmusterlist;
	}

	public ArrayList getMustfieldlist() {
		return mustfieldlist;
	}
	public void setMustfieldlist(ArrayList mustfieldlist) {
		this.mustfieldlist = mustfieldlist;
	}

	public String getMobapp() {
		return mobapp;
	}
	public void setMobapp(String mobapp) {
		this.mobapp = mobapp;
	}
	
	public ArrayList getMobcardnomesslist() {
		return mobcardnomesslist;
	}
	public void setMobcardnomesslist(ArrayList mobcardnomesslist) {
		this.mobcardnomesslist = mobcardnomesslist;
	}
	
}
