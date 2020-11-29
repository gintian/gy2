package com.hjsj.hrms.actionform.hire.employSummarise;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PersonnelEmployForm extends FrameForm {
	private PaginationForm personnelEmployListform=new PaginationForm();
	private String codeID="";
	private String isPhoneField="#";  //是否设置了电话指标
	private String isMailField="#";	  //是否设置了email指标
	private String dbName="";         //应用库前缀
	private ArrayList columnsList=new ArrayList();
	private String extendWhereSql="";
	private String orderSql="";
	private ArrayList dbnameList=new ArrayList();
	private String username="";
	/**录用员工的业务模板列表*/
	private ArrayList templateList = new ArrayList();
	/**模板id*/
	private String templateid="";
	/**区别用业务模板录用还是用人员库录用*/
	private String type="";
	
	@Override
    public void outPutFormHM() {
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setType((String)this.getFormHM().get("type"));
		this.setTemplateList((ArrayList)this.getFormHM().get("templateList"));
		this.setTemplateid((String)this.getFormHM().get("templateid"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.getPersonnelEmployListform().setList((ArrayList)this.getFormHM().get("personnelEmployList"));
		this.setCodeID((String)this.getFormHM().get("codeid"));
		this.setIsMailField((String)this.getFormHM().get("isMailField"));
		this.setIsPhoneField((String)this.getFormHM().get("isPhoneField"));
		this.setDbName((String)this.getFormHM().get("dbName"));
		this.setColumnsList((ArrayList)this.getFormHM().get("columnsList"));
		this.setExtendWhereSql((String)this.getFormHM().get("extendWhereSql"));
		this.setOrderSql((String)this.getFormHM().get("orderSql"));
	
		this.setDbnameList((ArrayList)this.getFormHM().get("dbnameList"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("templateid",this.getTemplateid());
		this.getFormHM().put("extendWhereSql",extendWhereSql);
		this.getFormHM().put("orderSql",orderSql);
		this.getFormHM().put("selectedlist",this.getPersonnelEmployListform().getSelectedList());
		this.getFormHM().put("pagerows", this.getPagerows()==0?"21":(this.getPagerows()+""));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/hire/employSummarise/personnelEmploy".equals(arg0.getPath())&&(arg1.getParameter("br_query")!=null))
		{
            /**定位到首页,*/
			this.getPersonnelEmployListform().getPagination().firstPage();                
        }
		return super.validate(arg0, arg1);
	}	

	public String getCodeID() {
		return codeID;
	}

	public void setCodeID(String codeID) {
		this.codeID = codeID;
	}

	public ArrayList getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(ArrayList columnsList) {
		this.columnsList = columnsList;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getExtendWhereSql() {
		return extendWhereSql;
	}

	public void setExtendWhereSql(String extendWhereSql) {
		this.extendWhereSql = extendWhereSql;
	}

	public String getIsMailField() {
		return isMailField;
	}

	public void setIsMailField(String isMailField) {
		this.isMailField = isMailField;
	}

	public String getIsPhoneField() {
		return isPhoneField;
	}

	public void setIsPhoneField(String isPhoneField) {
		this.isPhoneField = isPhoneField;
	}

	public String getOrderSql() {
		return orderSql;
	}

	public void setOrderSql(String orderSql) {
		this.orderSql = orderSql;
	}

	public PaginationForm getPersonnelEmployListform() {
		return personnelEmployListform;
	}

	public void setPersonnelEmployListform(PaginationForm personnelEmployListform) {
		this.personnelEmployListform = personnelEmployListform;
	}

	public ArrayList getDbnameList() {
		return dbnameList;
	}

	public void setDbnameList(ArrayList dbnameList) {
		this.dbnameList = dbnameList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ArrayList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList templateList) {
		this.templateList = templateList;
	}

	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



}
