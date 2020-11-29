package com.hjsj.hrms.actionform.gz.gz_budget;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

public class BudgetingForm extends FrameForm{

	private String treeJs;
	private String tab_id;
	private String tab_type;
	private String sql;
	private ArrayList fieldlist=new ArrayList();
	private ArrayList fieldList=new ArrayList();
	private String tableName;
	private String zhuangtai;
	private String status;
	private String canshu;
	private String unitSpflag;
	private String zeItemid;
	
	private FormFile templateFile;

	private String infoStr;
	
	private String canImport;
	
    private PaginationForm formulalistform=new PaginationForm();
    private PaginationForm itemlistform=new PaginationForm();
    private String selectformula_tabid="0";
    private ArrayList selectformula_tablist;
    

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tab_id",this.getTab_id());
		this.getFormHM().put("selectformula_tabid",this.getSelectformula_tabid());
		this.getFormHM().put("templateFile", this.getTemplateFile());
		this.getFormHM().put("tableName", this.getTableName());
		this.getFormHM().put("pagerows", this.getPagerows()==0?"10":(this.getPagerows()+""));
	}
	@Override
    public void outPutFormHM() {
		this.getFormulalistform().setList(
				(ArrayList) this.getFormHM().get("selectformulalist"));
		this.getItemlistform().setList(
				(ArrayList) this.getFormHM().get("itemlist"));
		this.setSelectformula_tabid((String)this.getFormHM().get("selectformula_tabid"));
		this.setSelectformula_tablist((ArrayList)this.getFormHM().get("selectformula_tablist"));
		this.setCanImport((String)this.getFormHM().get("canImport"));
		this.setInfoStr((String)this.getFormHM().get("infoStr"));
		this.setTreeJs((String)this.getFormHM().get("treeJs"));
		this.setTab_id((String)this.getFormHM().get("tab_id"));
		this.setTab_type((String)this.getFormHM().get("tab_type"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTableName((String)this.getFormHM().get("tableName"));
		
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setZhuangtai((String)this.getFormHM().get("zhuangtai"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));//控制每页显示多少条数据
		this.setUnitSpflag((String)this.getFormHM().get("unitSpflag"));
		this.setZeItemid((String)this.getFormHM().get("zeItemid"));
		
	}
	public String getInfoStr() {
		return infoStr;
	}

	public void setInfoStr(String infoStr) {
		this.infoStr = infoStr;
	}

	public String getTreeJs() {
		return treeJs;
	}

	public void setTreeJs(String treeJs) {
		this.treeJs = treeJs;
	}
	public String getTab_id() {
		return tab_id;
	}

	public void setTab_id(String tab_id) {
		this.tab_id = tab_id;
	}

	public String getTab_type() {
		return tab_type;
	}

	public void setTab_type(String tab_type) {
		this.tab_type = tab_type;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public FormFile getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(FormFile templateFile) {
		this.templateFile = templateFile;
	}
	public ArrayList getFieldList() {
		return fieldList;
	}
	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}
	public String getZhuangtai() {
		return zhuangtai;
	}
	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCanshu() {
		return canshu;
	}
	public void setCanshu(String canshu) {
		this.canshu = canshu;
	}
	public String getUnitSpflag() {
		return unitSpflag;
	}
	public void setUnitSpflag(String unitSpflag) {
		this.unitSpflag = unitSpflag;
	}
	
	public PaginationForm getFormulalistform() {
		return formulalistform;
	}
	public String getSelectformula_tabid() {
		return selectformula_tabid;
	}
	public void setSelectformula_tabid(String selectformula_tabid) {
		this.selectformula_tabid = selectformula_tabid;
	}
	public ArrayList getSelectformula_tablist() {
		return selectformula_tablist;
	}
	public void setSelectformula_tablist(ArrayList selectformula_tablist) {
		this.selectformula_tablist = selectformula_tablist;
	}
	public String getCanImport() {
		return canImport;
	}
	public void setCanImport(String canImport) {
		this.canImport = canImport;
	}
	public String getZeItemid() {
		return zeItemid;
	}
	public void setZeItemid(String zeItemid) {
		this.zeItemid = zeItemid;
	}
	public PaginationForm getItemlistform() {
		return itemlistform;
	}
	
}
