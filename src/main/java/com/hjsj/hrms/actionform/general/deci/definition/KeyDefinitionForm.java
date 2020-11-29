package com.hjsj.hrms.actionform.general.deci.definition;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KeyDefinitionForm extends FrameForm {

	private String nam; //

	private String type;//

	private String sel;//

	private String object; //

	private String subset;//

	private String code;//

	private String one;//

	private String two;//

	private String box;//

	private String seb;//

	private String dialog;//

	private String typeid;//

	private String keyid;//

	private String codeItemValue;//

	/** 当前页 */
	private int current = 1;

	// 信息群列表
	private ArrayList olist = new ArrayList();

	// 关键指标分类信息列表
	private ArrayList tlist = new ArrayList();

	//
	private RecordVo factor = new RecordVo("ds_key_factor");

	// 关键指标分页
	private PaginationForm keyDefinitionForm = new PaginationForm();

	// 关键指标分类信息分页
	private PaginationForm keyDefinition = new PaginationForm();

	// 新增关键指标
	private String name; // 关键指标名称
	private String desc; // 指标描述
	private String standartValue;// 标准值
	private String controlValue; // 控制值

	// 代码型指标集
	private ArrayList setList = new ArrayList();
	// 代码型指标项
	private ArrayList itemList = new ArrayList();

	private String fieldSet; // 统计指标集
	private String fieldName;// 统计指标名称
	private String staticMethod; // 统计方法
	private String formula; // 计算公式
	private String codeItemValues;// 代码值
	private String codeItemDescs;// 代码值描述

	private ArrayList allFieldItemList = new ArrayList();
	private String oneFieldItem;
	private String twoFieldItem;
	private String oneFieldItemValue;
	private String twoFieldItemValue;

	private String operateFlag; // 操作标识（增加1、修改2）
	private String factorid;

	// 计算公式弹出窗体用
	private ArrayList fieldSetList = new ArrayList(); // 指标集集合
	private ArrayList fieldItemList = new ArrayList(); // 指标项集合
	private String set; // 指标集
	private String itemid; // 指标项

	private String party;// 如果是党团工会设置界面调用则不需验证 xuj 2010-2-5

	@Override
    public void outPutFormHM() {

		// 关键指标信息显示列表
		this.getKeyDefinitionForm().setList(
				(ArrayList) this.getFormHM().get("factorlist"));
		this.getKeyDefinitionForm().getPagination().gotoPage(current);
		this.getKeyDefinition().getPagination().gotoPage(current);

		// 关键指标分页显示
		this.getKeyDefinition().setList(
				(ArrayList) this.getFormHM().get("keylist"));

		this.setFactor((RecordVo) this.getFormHM().get("factor"));
		this.setOlist((ArrayList) this.getFormHM().get("olist"));// 信息群列表
		this.setTlist((ArrayList) this.getFormHM().get("tlist"));// 关键指标分类信息列表
		this.setNam((String) this.getFormHM().get("nam"));//

		this.setTypeid((String) this.getFormHM().get("typeid"));
		this.setKeyid((String) this.getFormHM().get("keyid"));
		this.setType((String) this.getFormHM().get("type"));
		this.setSel((String) this.getFormHM().get("sel"));
		this.setDialog((String) this.getFormHM().get("dialog"));
		this.setBox((String) this.getFormHM().get("box"));

		this.setObject((String) this.getFormHM().get("object"));// 当前信息群标识

		this.setCodeItemValue((String) this.getFormHM().get("codeItemValue"));
		this.setSeb((String) this.getFormHM().get("seb"));

		this.setOne((String) this.getFormHM().get("one"));
		this.setTwo((String) this.getFormHM().get("two"));

		// ///////////////////////////////////////////////////////////////

		this.setSetList((ArrayList) this.getFormHM().get("fieldsetlist"));
		this.setItemList((ArrayList) this.getFormHM().get("fielditemlist"));

		this.setName((String) this.getFormHM().get("name"));
		this.setDesc((String) this.getFormHM().get("desc"));
		this.setStandartValue((String) this.getFormHM().get("standartvalue"));
		this.setControlValue((String) this.getFormHM().get("controlvalue"));

		this.setBox((String) this.getFormHM().get("box"));
		this.setOneFieldItem((String) this.getFormHM().get("onefielditem"));
		this.setOneFieldItemValue((String) this.getFormHM().get(
				"onefielditemvalue"));
		this.setTwoFieldItem((String) this.getFormHM().get("twofielditem"));
		this.setTwoFieldItemValue((String) this.getFormHM().get(
				"twofielditemvalue"));

		this.setStaticMethod((String) this.getFormHM().get("staticmethod"));
		this.setCodeItemDescs((String) this.getFormHM().get("codeitemdescs"));
		this.setCodeItemValues((String) this.getFormHM().get("codeitemvalues"));

		this.setOperateFlag((String) this.getFormHM().get("operateflag"));
		this.setFieldName((String) this.getFormHM().get("fieldname"));
		this.setFieldSet((String) this.getFormHM().get("fieldset"));

		this.setFactorid((String) this.getFormHM().get("factorid"));

		// ////////////////////////////////////////////
		this.setFieldSetList((ArrayList) this.getFormHM().get("setlist"));
		this.setFieldItemList((ArrayList) this.getFormHM().get("itemlist"));
		this.setParty((String) this.getFormHM().get("party"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

		// 删除关键指标列表选中信息
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getKeyDefinitionForm().getSelectedList());

		// 删除关键指标分类列表中选中的信息
		this.getFormHM().put("sellist",
				(ArrayList) this.getKeyDefinition().getSelectedList());

		this.getFormHM().put("factor", (RecordVo) this.getFactor());
		this.getFormHM().put("object", (String) this.getObject());
		this.getFormHM().put("nam", (String) this.getNam());
		this.getFormHM().put("type", (String) this.getType());
		this.getFormHM().put("sel", (String) this.getSel());
		this.getFormHM().put("code", (String) this.getCode());
		this.getFormHM().put("seb", (String) this.getSeb());
		this.getFormHM().put("two", (String) this.getTwo());
		this.getFormHM().put("one", (String) this.getOne());
		this.getFormHM().put("subset", (String) this.getSubset());
		this.getFormHM().put("keyid", (String) this.getKeyid());
		this.getFormHM().put("typeid", (String) this.getTypeid());
		this.getFormHM().put("codeItemValue", (String) this.getCodeItemValue());

		this.getFormHM().put("dialog", (String) this.getDialog());
		this.getFormHM().put("box", (String) this.getBox());

		// //////////////////////////////////////////////////////////
		this.getFormHM().put("name", this.getName());
		this.getFormHM().put("desc", this.getDesc());
		this.getFormHM().put("standartValue", this.getStandartValue());
		this.getFormHM().put("controlValue", this.getControlValue());
		this.getFormHM().put("fieldName", this.getFieldName());
		this.getFormHM().put("codeItemValues", this.getCodeItemValues());
		this.getFormHM().put("staticMethod", this.getStaticMethod());
		this.getFormHM().put("box", this.getBox());
		this.getFormHM().put("oneFieldItemValue", this.getOneFieldItemValue());
		this.getFormHM().put("twoFieldItemValue", this.getTwoFieldItemValue());
		this.getFormHM().put("object", this.getObject());
		this.getFormHM().put("typeid", this.getTypeid());
		this.getFormHM().put("operateflag", this.getOperateFlag());
		this.getFormHM().put("factorid", this.getFactorid());
	}

	public RecordVo getFactor() {
		return factor;
	}

	public void setFactor(RecordVo factor) {
		this.factor = factor;
	}

	public String getNam() {
		return nam;
	}

	public void setNam(String nam) {
		this.nam = nam;
	}

	public ArrayList getOlist() {
		return olist;
	}

	public void setOlist(ArrayList olist) {
		this.olist = olist;
	}

	public ArrayList getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
	}

	public PaginationForm getKeyDefinitionForm() {
		return keyDefinitionForm;
	}

	public void setKeyDefinitionForm(PaginationForm keyDefinitionForm) {
		this.keyDefinitionForm = keyDefinitionForm;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public PaginationForm getKeyDefinition() {
		return keyDefinition;
	}

	public void setKeyDefinition(PaginationForm keyDefinition) {
		this.keyDefinition = keyDefinition;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSel() {
		return sel;
	}

	public void setSel(String sel) {
		this.sel = sel;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubset() {
		return subset;
	}

	public void setSubset(String subset) {
		this.subset = subset;
	}

	public String getSeb() {
		return seb;
	}

	public void setSeb(String seb) {
		this.seb = seb;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getKeyid() {
		return keyid;
	}

	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getDialog() {
		return dialog;
	}

	public void setDialog(String dialog) {
		this.dialog = dialog;
	}

	public String getBox() {
		return box;
	}

	public void setBox(String box) {
		this.box = box;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getCodeItemValue() {
		return codeItemValue;
	}

	public void setCodeItemValue(String codeItemValue) {
		this.codeItemValue = codeItemValue;
	}

	// ////////////////////////////////////////////////////////////////////

	public String getCodeItemValues() {
		return codeItemValues;
	}

	public void setCodeItemValues(String codeItemValues) {
		this.codeItemValues = codeItemValues;
	}

	public String getControlValue() {
		return controlValue;
	}

	public void setControlValue(String controlValue) {
		this.controlValue = controlValue;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandartValue() {
		return standartValue;
	}

	public void setStandartValue(String standartValue) {
		this.standartValue = standartValue;
	}

	public String getStaticMethod() {
		return staticMethod;
	}

	public void setStaticMethod(String staticMethod) {
		this.staticMethod = staticMethod;
	}

	public String getFieldSet() {
		return fieldSet;
	}

	public void setFieldSet(String fieldSet) {
		this.fieldSet = fieldSet;
	}

	public ArrayList getAllFieldItemList() {
		return allFieldItemList;
	}

	public void setAllFieldItemList(ArrayList allFieldItemList) {
		this.allFieldItemList = allFieldItemList;
	}

	public String getOneFieldItem() {
		return oneFieldItem;
	}

	public void setOneFieldItem(String oneFieldItem) {
		this.oneFieldItem = oneFieldItem;
	}

	public String getTwoFieldItem() {
		return twoFieldItem;
	}

	public void setTwoFieldItem(String twoFieldItem) {
		this.twoFieldItem = twoFieldItem;
	}

	public ArrayList getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}

	public ArrayList getSetList() {
		return setList;
	}

	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}

	public String getCodeItemDescs() {
		return codeItemDescs;
	}

	public void setCodeItemDescs(String codeItemDescs) {
		this.codeItemDescs = codeItemDescs;
	}

	// /////////////////////////////////////////////////////////

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

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getOneFieldItemValue() {
		return oneFieldItemValue;
	}

	public void setOneFieldItemValue(String oneFieldItemValue) {
		this.oneFieldItemValue = oneFieldItemValue;
	}

	public String getTwoFieldItemValue() {
		return twoFieldItemValue;
	}

	public void setTwoFieldItemValue(String twoFieldItemValue) {
		this.twoFieldItemValue = twoFieldItemValue;
	}

	public String getOperateFlag() {
		return operateFlag;
	}

	public void setOperateFlag(String operateFlag) {
		this.operateFlag = operateFlag;
	}

	public String getFactorid() {
		return factorid;
	}

	public void setFactorid(String factorid) {
		this.factorid = factorid;
	}

	public String getParty() {
		return party;
	}

	public void setParty(String party) {
		this.party = party;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {

		super.reset(arg0, arg1);
		this.setBox("");
		//清空type，typeid，sel   jingq  add   2014.07.15
		this.setType("");
		this.setTypeid("");
		this.setSel("");
		this.getFormHM().put("lock", arg1.getSession().getServletContext().getAttribute("lock"));
	}

}
