package com.hjsj.hrms.utils.components.emailtemplate.actionform;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;


/**
 * <p>Title:TemplateSetForm</p>
 * <p>Description:通知模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:02:54 PM</p>
 * @author sunming
 * @version 1.0
 */
public class TemplateSetForm extends FrameForm{
	 
	private String str_sql ="";
	private ArrayList columns = new ArrayList();
	private ArrayList columnList = new ArrayList();
	public ArrayList getColumns() {
		return columns;
	}
	public void setColumns(ArrayList columns) {
		this.columns = columns;
	}


	private String orderbystr = "";
	public String getOrderbystr() {
		return orderbystr;
	}
	public void setOrderbystr(String orderbystr) {
		this.orderbystr = orderbystr;
	}
	
	
	 private ArrayList buttonList = new ArrayList();
	 
	public ArrayList getButtonList() {
		return buttonList;
	}
	public void setButtonList(ArrayList buttonList) {
		this.buttonList = buttonList;
	}
	private String id = "";
	private String name="";//模板名称
	private String nModule="";//模板模块 7为招聘管理模块
	private int nInfoclass = 0;//信息集
	private String subject = "";//邮件主题
	private String content  = "";//邮件内容
	private String attach="";//邮件附件
	private String address = "";//邮件地址
	private int sub_module=0;//子模块编号,通知模板编号
	private String return_address = "";//回复地址
	private String b0110 = "";//所属机构
	private String ownflag = "";//系统模板 1系统内置模板 0用户自定义模板
	private int current=1;     //页码
	private int pagesize=20;    //每页显示的条数
	
	private String fieldType = "";
	private String fieldContent = "";
	private int dateFormat = 0;
	private int fieldLen = 0;
	private int nDec = 0;
	private String codeSet = "";
	private String nFlag = "";
	private  String fieldid = "";
	private ArrayList attachList;
	private String fieldtitle = "";
	private FormFile file;
	//是否为上级模板
	private boolean isParent;
	private String opt = "";//判断是什么模块进入的，暂时1：绩效
	private String isShowItem = "";//判断新增是否显示插入指标，1：不显示，其余显示
	private String isShowInsertFormula = "";//判断新增是否显示插入公式，1：不显示，其余显示
	private String isShowModifyFormula = "";//判断新增是否显示修改公式，1：不显示，其余显示
	private String isShowModuleType = "";
	private String isShowAttachId = "";
    
	public void outPutFormHM(){
		
		this.setIsParent(Boolean.valueOf((String) this.getFormHM().get("str_sql")));
		
		this.setStr_sql((String) this.getFormHM().get("str_sql"));
		this.setColumns((ArrayList) this.getFormHM().get("columns"));
		this.setButtonList((ArrayList)this.getFormHM().get("buttonList"));
		this.setOrderbystr((String)this.getFormHM().get("orderbystr"));
		this.setName((String) this.getFormHM().get("name"));
		this.setNModule((String) this.getFormHM().get("nModule"));
		this.setSubject((String) this.getFormHM().get("subject"));
		this.setContent((String) this.getFormHM().get("content"));
		this.setAttach((String) this.getFormHM().get("attach"));
		this.setAddress((String) this.getFormHM().get("address"));
		this.setReturn_address((String) this.getFormHM().get("return_address"));
		this.setB0110((String) this.getFormHM().get("b0110"));
		this.setOwnflag((String) this.getFormHM().get("ownflag"));
		this.setId((String) this.getFormHM().get("id"));
		this.setFieldid((String) this.getFormHM().get("fieldid"));
		this.setAttachList((ArrayList) this.getFormHM().get("attachList"));
		this.setColumnList((ArrayList) this.getFormHM().get("columnList"));
		this.setFile((FormFile) this.getFormHM().get("file"));
		
		this.setOpt((String) this.getFormHM().get("opt"));
		this.setIsShowItem((String) this.getFormHM().get("isShowItem"));
		this.setIsShowInsertFormula((String) this.getFormHM().get("isShowInsertFormula"));
		this.setIsShowModifyFormula((String) this.getFormHM().get("isShowModifyFormula"));
		this.setIsShowModuleType((String) this.getFormHM().get("isShowModuleType"));
		this.setIsShowAttachId((String) this.getFormHM().get("isShowAttachId"));
	}
	
	public void inPutTransHM() {
		this.getFormHM().put("id", "id");
		this.getFormHM().put("name", "name");
		this.getFormHM().put("subject", "subject");
		this.getFormHM().put("content", "content");
		this.getFormHM().put("attach", "attach");
		this.getFormHM().put("address", "address");
		this.getFormHM().put("sub_module", "sub_module");
		this.getFormHM().put("return_address", "return_address");
		this.getFormHM().put("b0110", "b0110");
		this.getFormHM().put("ownflag", "ownflag");
		this.getFormHM().put("fieldid", "fieldid");
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("attachList", this.getAttachList());
		this.getFormHM().put("isParent", this.getIsParent());
		
		this.getFormHM().put("opt", this.getOpt());
		this.getFormHM().put("isShowItem", this.getIsShowItem());
		this.getFormHM().put("isShowInsertFormula", this.getIsShowInsertFormula());
		this.getFormHM().put("isShowModifyFormula", this.getIsShowModifyFormula());
		this.getFormHM().put("isShowModuleType", this.getIsShowModuleType());
		this.getFormHM().put("isShowAttachId", this.getIsShowAttachId());
	}
	public String getStr_sql() {
		return str_sql;
	}
	public void setStr_sql(String str_sql) {
		this.str_sql = str_sql;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNModule() {
		return nModule;
	}
	public void setNModule(String module) {
		nModule = module;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getReturn_address() {
		return return_address;
	}
	public void setReturn_address(String return_address) {
		this.return_address = return_address;
	}
	public String getOwnflag() {
		return ownflag;
	}
	public void setOwnflag(String ownflag) {
		this.ownflag = ownflag;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getNInfoclass() {
		return nInfoclass;
	}
	public void setNInfoclass(int infoclass) {
		nInfoclass = infoclass;
	}
	public int getSub_module() {
		return sub_module;
	}
	public void setSub_module(int sub_module) {
		this.sub_module = sub_module;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldContent() {
		return fieldContent;
	}
	public void setFieldContent(String fieldContent) {
		this.fieldContent = fieldContent;
	}
	public int getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(int dateFormat) {
		this.dateFormat = dateFormat;
	}
	public int getFieldLen() {
		return fieldLen;
	}
	public void setFieldLen(int fieldLen) {
		this.fieldLen = fieldLen;
	}
	public int getNDec() {
		return nDec;
	}
	public void setNDec(int dec) {
		nDec = dec;
	}
	public String getCodeSet() {
		return codeSet;
	}
	public void setCodeSet(String codeSet) {
		this.codeSet = codeSet;
	}
	public String getNFlag() {
		return nFlag;
	}
	public void setNFlag(String flag) {
		nFlag = flag;
	}
	public String getFieldtitle() {
		return fieldtitle;
	}
	public void setFieldtitle(String fieldtitle) {
		this.fieldtitle = fieldtitle;
	}
	public String getFieldid() {
		return fieldid;
	}
	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public ArrayList getAttachList() {
		return attachList;
	}
	public void setAttachList(ArrayList attachList) {
		this.attachList = attachList;
	}
	public ArrayList getColumnList() {
		return columnList;
	}
	public void setColumnList(ArrayList columnList) {
		this.columnList = columnList;
	}
	public boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public String getIsShowItem() {
		return isShowItem;
	}
	public void setIsShowItem(String isShowItem) {
		this.isShowItem = isShowItem;
	}
	public String getIsShowInsertFormula() {
		return isShowInsertFormula;
	}
	public void setIsShowInsertFormula(String isShowInsertFormula) {
		this.isShowInsertFormula = isShowInsertFormula;
	}
	public String getIsShowModifyFormula() {
		return isShowModifyFormula;
	}
	public void setIsShowModifyFormula(String isShowModifyFormula) {
		this.isShowModifyFormula = isShowModifyFormula;
	}
	public String getIsShowModuleType() {
		return isShowModuleType;
	}
	public void setIsShowModuleType(String isShowModuleType) {
		this.isShowModuleType = isShowModuleType;
	}
	public String getIsShowAttachId() {
		return isShowAttachId;
	}
	public void setIsShowAttachId(String isShowAttachId) {
		this.isShowAttachId = isShowAttachId;
	}

}