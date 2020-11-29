package com.hjsj.hrms.actionform.hire.zp_options.cond;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:ZpCondTemplateConstantForm</p> 
 *<p>Description:简历筛选模板的form</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 25, 2007:9:15:15 AM</p> 
 *@author Lizhenwei
 *@version 4.0
 */
public class ZpCondTemplateConstantForm extends FrameForm{
	public ZpCondTemplateConstantForm()
	{
		 CommonData vo=new CommonData("=","=");
	        operlist.add(vo);
	        vo=new CommonData(">",">");
	        operlist.add(vo);  
	        vo=new CommonData(">=",">=");
	        operlist.add(vo); 
	        vo=new CommonData("<","<");
	        operlist.add(vo);
	        vo=new CommonData("<=","<=");
	        operlist.add(vo);   
	        vo=new CommonData("<>","<>");
	        operlist.add(vo);
	        vo=new CommonData("*","并且");
	        logiclist.add(vo);
	        vo=new CommonData("+","或");  
	        logiclist.add(vo);
	}
	private ArrayList operlist=new ArrayList();
	private ArrayList logiclist=new ArrayList();
	private String constant;
	private String type;
	private String str_value;
	private String describe;
	private String zp_cond_template_type;//条件模板类型，0：简单，1：复杂。
	private RecordVo constant_vo=ConstantParamter.getConstantVo("ZP_COND_TEMPLATE");
	private ArrayList zpFieldList = new ArrayList();//指标项list
	private ArrayList zpFieldSetList = new ArrayList();//指标集list
	private ArrayList selectedFieldsList = new ArrayList();
	private String fieldsetid;
	private String itemid;
	private ArrayList fieldSetList = new ArrayList();
	private String fielditemname;
	private String s_value;
	private String e_value;
	private String flag;
	private String itemtype;
	private String codesetid;
	private String itemdesc;
	//private String right_fields;
	private String itemlength;
	private ArrayList complexTemplateList=new ArrayList();
	private ArrayList factorlist = new ArrayList();
	private String expression="";
	private String templateid="";
	private String templateName="";
	 private String[] right_fields = new String[0];
	@Override
    public void outPutFormHM() {
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setComplexTemplateList((ArrayList)this.getFormHM().get("complexTemplateList"));
		this.setTemplateName((String)this.getFormHM().get("templateName"));
		this.setOperlist((ArrayList)this.getFormHM().get("operlist"));
		this.setTemplateid((String)this.getFormHM().get("templateid"));
		this.setExpression((String)this.getFormHM().get("expression"));
		this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
		this.setItemdesc((String)this.getFormHM().get("itemdesc"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setItemtype((String)this.getFormHM().get("itemtype"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setStr_value((String)this.getFormHM().get("s_value"));
		this.setE_value((String)this.getFormHM().get("e_value"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
		this.setFielditemname((String)this.getFormHM().get("fielditemname"));
		this.setZp_cond_template_type((String)this.getFormHM().get("zp_cond_template_type"));
		this.setZpFieldSetList((ArrayList)this.getFormHM().get("zpFieldSetList"));
		this.setZpFieldList((ArrayList)this.getFormHM().get("zpFieldList"));
		this.setConstant_vo((RecordVo)this.getFormHM().get("constant_vo"));
		this.setStr_value((String)this.getFormHM().get("str_value"));
		this.setSelectedFieldsList((ArrayList)this.getFormHM().get("selectedFieldsList"));
		this.setItemlength((String)this.getFormHM().get("itemlength"));
	}
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedFieldsList",this.getSelectedFieldsList());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("complexTemplateList", this.getComplexTemplateList());
		this.getFormHM().put("templateName",this.getTemplateName());
		this.getFormHM().put("operlist", this.getOperlist());
		this.getFormHM().put("templateid",this.getTemplateid());
		this.getFormHM().put("factorlist", this.getFactorlist());
		this.getFormHM().put("expression",this.getExpression());
		this.getFormHM().put("itemdesc",this.getItemdesc());
		this.getFormHM().put("codesetid",this.getCodesetid());
		this.getFormHM().put("itemtype",this.getItemtype());
		this.getFormHM().put("fielditemname",this.getFielditemname());
		this.getFormHM().put("fieldSetList",this.getFieldSetList());
		this.getFormHM().put("zp_cond_template_type",this.getZp_cond_template_type());
		this.getFormHM().put("fieldsetid",this.getFieldsetid());
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("zpFieldSetList",this.getZpFieldSetList());
		this.getFormHM().put("zpFieldList",this.getZpFieldList());
		this.getFormHM().put("constant_vo",this.getConstant_vo());
	    this.getFormHM().put("str_value",this.getStr_value());	
	}
	public String getConstant() {
		return constant;
	}
	public void setConstant(String constant) {
		this.constant = constant;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getStr_value() {
		return str_value;
	}
	public void setStr_value(String str_value) {
		this.str_value = str_value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public RecordVo getConstant_vo() {
		return constant_vo;
	}
	public void setConstant_vo(RecordVo constant_vo) {
		this.constant_vo = constant_vo;
	}
	public String getZp_cond_template_type() {
		return zp_cond_template_type;
	}
	public void setZp_cond_template_type(String zp_cond_template_type) {
		this.zp_cond_template_type = zp_cond_template_type;
	}
	public ArrayList getZpFieldList() {
		return zpFieldList;
	}
	public void setZpFieldList(ArrayList zpFieldList) {
		this.zpFieldList = zpFieldList;
	}
	public ArrayList getZpFieldSetList() {
		return zpFieldSetList;
	}
	public void setZpFieldSetList(ArrayList zpFieldSetList) {
		this.zpFieldSetList = zpFieldSetList;
	}
	public String getFieldsetid() {
		return fieldsetid;
	}
	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public ArrayList getFieldSetList() {
		return fieldSetList;
	}
	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}
	public String getFielditemname() {
		return fielditemname;
	}
	public void setFielditemname(String fielditemname) {
		this.fielditemname = fielditemname;
	}
	public String getE_value() {
		return e_value;
	}
	public void setE_value(String e_value) {
		this.e_value = e_value;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getS_value() {
		return s_value;
	}
	public void setS_value(String s_value) {
		this.s_value = s_value;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getItemdesc() {
		return itemdesc;
	}
	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}
	public ArrayList getSelectedFieldsList() {
		return selectedFieldsList;
	}
	public void setSelectedFieldsList(ArrayList selectedFieldsList) {
		this.selectedFieldsList = selectedFieldsList;
	}
	public String getItemlength() {
		return itemlength;
	}
	public void setItemlength(String itemlength) {
		this.itemlength = itemlength;
	}
	public ArrayList getComplexTemplateList() {
		return complexTemplateList;
	}
	public void setComplexTemplateList(ArrayList complexTemplateList) {
		this.complexTemplateList = complexTemplateList;
	}
	public ArrayList getLogiclist() {
		return logiclist;
	}
	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}
	public ArrayList getOperlist() {
		return operlist;
	}
	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public ArrayList getFactorlist() {
		return factorlist;
	}
	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}
	public String getTemplateid() {
		return templateid;
	}
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

}
