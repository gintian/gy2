package com.hjsj.hrms.actionform.general.deci.definition;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class StatCutlineForm extends FrameForm {
	
	 /**当前页*/
    private int current=1;
    /**统计图例管理器*/
    private PaginationForm statCutlinelistform=new PaginationForm();
    
	private String object="A";  //对象|信息群 A人员 B单位  K职位
	private String typeid="";   //图例分类
	
	private ArrayList typeList=new ArrayList();  //图例分类信息集
	private ArrayList objectList=new ArrayList();  //图例分类信息集
	
	private ArrayList fieldSetList=new ArrayList();
	private ArrayList fieldItemList=new ArrayList();
	private String    fieldSetID="";
	private String    fieldItemID="";
	private String    codeValues="";
	private String    codeItemValue="";
	private ArrayList keyFactorList=new ArrayList();
	private String    aa_keyFactors="";
	private String    a_typeid="";
	private String    a_itemid="";
	private String    a_itemname="";
	private String    a_keyFactors="";
	private String    a_flag="";
	private String    defaultCodeSetID="";
	

	
	
	@Override
    public void outPutFormHM() {
		
		this.getStatCutlinelistform().setList((ArrayList)this.getFormHM().get("statCutlinelist"));
		this.getStatCutlinelistform().getPagination().gotoPage(current);
		
		this.setObjectList((ArrayList)this.getFormHM().get("objectList")); //信息群集合
		this.setTypeList((ArrayList)this.getFormHM().get("typeList"));  //图例类别集合
		this.setTypeid((String)this.getFormHM().get("typeid"));   //当前图例类别
		this.setObject((String)this.getFormHM().get("object"));   //当前信息群
		
		this.setFieldItemList((ArrayList)this.getFormHM().get("fieldItemList"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
		this.setFieldItemID((String)this.getFormHM().get("fieldItemID"));
		this.setFieldSetID((String)this.getFormHM().get("fieldSetID"));
		this.setCodeValues((String)this.getFormHM().get("codeValues"));
		this.setCodeItemValue((String)this.getFormHM().get("codeItemValue"));
		this.setKeyFactorList((ArrayList)this.getFormHM().get("keyFactorList"));
		this.setDefaultCodeSetID((String)this.getFormHM().get("defaultCodeSetID"));
		this.setA_itemid((String)this.getFormHM().get("a_itemid"));
		this.setA_itemname((String)this.getFormHM().get("a_itemname"));
		this.setA_keyFactors((String)this.getFormHM().get("a_keyFactors"));
		this.setA_typeid((String)this.getFormHM().get("a_typeid"));
		this.setA_flag((String)this.getFormHM().get("a_flag"));
		this.setAa_keyFactors((String)this.getFormHM().get("aa_keyFactors"));
	}

	
	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("selectedList",this.getStatCutlinelistform().getSelectedList());
		this.getFormHM().put("typeid",this.getTypeid());
		this.getFormHM().put("object",this.getObject());
		
		this.getFormHM().put("a_itemid",this.getA_itemid());
		this.getFormHM().put("a_itemname",this.getA_itemname());
		this.getFormHM().put("fieldItemID",this.getFieldItemID());
		this.getFormHM().put("codeItemValue",this.getCodeItemValue());
		this.getFormHM().put("a_keyFactors",this.getA_keyFactors());
		this.getFormHM().put("aa_keyFactors",this.getAa_keyFactors());
		
		
	}

	
	public String getObject() {
		return object;
	}

	
	public void setObject(String object) {
		this.object = object;
	}


	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public ArrayList getTypeList() {
		return typeList;
	}

	public void setTypeList(ArrayList typeList) {
		this.typeList = typeList;
	}


	public ArrayList getObjectList() {
		return objectList;
	}


	public void setObjectList(ArrayList objectList) {
		this.objectList = objectList;
	}


	public int getCurrent() {
		return current;
	}


	public void setCurrent(int current) {
		this.current = current;
	}


	public PaginationForm getStatCutlinelistform() {
		return statCutlinelistform;
	}


	public void setStatCutlinelistform(PaginationForm statCutlinelistform) {
		this.statCutlinelistform = statCutlinelistform;
	}


	public String getCodeValues() {
		return codeValues;
	}


	public void setCodeValues(String codeValues) {
		this.codeValues = codeValues;
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


	public ArrayList getKeyFactorList() {
		return keyFactorList;
	}


	public void setKeyFactorList(ArrayList keyFactorList) {
		this.keyFactorList = keyFactorList;
	}


	public String getFieldItemID() {
		return fieldItemID;
	}


	public void setFieldItemID(String fieldItemID) {
		this.fieldItemID = fieldItemID;
	}


	public String getFieldSetID() {
		return fieldSetID;
	}


	public void setFieldSetID(String fieldSetID) {
		this.fieldSetID = fieldSetID;
	}


	public String getA_itemid() {
		return a_itemid;
	}


	public void setA_itemid(String a_itemid) {
		this.a_itemid = a_itemid;
	}


	public String getA_itemname() {
		return a_itemname;
	}


	public void setA_itemname(String a_itemname) {
		this.a_itemname = a_itemname;
	}


	public String getA_keyFactors() {
		return a_keyFactors;
	}


	public void setA_keyFactors(String factors) {
		a_keyFactors = factors;
	}


	public String getA_typeid() {
		return a_typeid;
	}


	public void setA_typeid(String a_typeid) {
		this.a_typeid = a_typeid;
	}


	public String getA_flag() {
		return a_flag;
	}


	public void setA_flag(String a_flag) {
		this.a_flag = a_flag;
	}


	public String getCodeItemValue() {
		return codeItemValue;
	}


	public void setCodeItemValue(String codeItemValue) {
		this.codeItemValue = codeItemValue;
	}


	public String getDefaultCodeSetID() {
		return defaultCodeSetID;
	}


	public void setDefaultCodeSetID(String defaultCodeSetID) {
		this.defaultCodeSetID = defaultCodeSetID;
	}


	public String getAa_keyFactors() {
		return aa_keyFactors;
	}


	public void setAa_keyFactors(String aa_keyFactors) {
		this.aa_keyFactors = aa_keyFactors;
	}

}
