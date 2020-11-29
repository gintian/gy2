/**
 * 
 */
package com.hjsj.hrms.actionform.sys.dataimport;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:DataImportForm
 * </p>
 * <p>
 * Description:数据导入form类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-28
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class DataImportForm extends FrameForm{
	
	// 分页
	private PaginationForm dataImportForm = new PaginationForm(); 
	
	// 参数id
	private String id;
	
	// 名称
	private String name;
	
	// 人员库
	private String nbase;
	private ArrayList nbaseList;
	
	// 数据库类型
	private String dbType;
	private ArrayList dbTypeList;
	
	// 数据库连接
	private String dbUrl;
	
	// 数据库用户名
	private String userName;
	
	// 数据库密码
	private String password;
	
	// 映射关系
	private String mapping;
	
	// 作业类
	private String jobClass;
	
	// 是否启用
	private String enable;
	
	// ehr表名
	private String ehrTable;
	
	//外部表名
	private String extTable;
	
	// hr关联指标
	private String hrRelation;
	
	// 其他系统关联指标
	private String extRelation;
	
	// 外部系统过滤条件
	private String srcTabCond;
	
	// hr数据保护条件	
	private String tagTabCond;
	
	// a01指标
	private ArrayList fieldList;
	private String fieldName;
	
	//最大序号
    private String maxOrder;	
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getEhrTable() {
		return ehrTable;
	}

	public void setEhrTable(String ehrTable) {
		this.ehrTable = ehrTable;
	}

	public String getExtTable() {
		return extTable;
	}

	public void setExtTable(String extTable) {
		this.extTable = extTable;
	}

	public String getHrRelation() {
		return hrRelation;
	}

	public void setHrRelation(String hrRelation) {
		this.hrRelation = hrRelation;
	}

	public String getExtRelation() {
		return extRelation;
	}

	public void setExtRelation(String extRelation) {
		this.extRelation = extRelation;
	}


	public String getSrcTabCond() {
		return srcTabCond;
	}

	public void setSrcTabCond(String srcTabCond) {
		this.srcTabCond = srcTabCond;
	}

	public String getTagTabCond() {
		return tagTabCond;
	}

	public void setTagTabCond(String tagTabCond) {
		this.tagTabCond = tagTabCond;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public ArrayList getDbTypeList() {
		return dbTypeList;
	}

	public void setDbTypeList(ArrayList dbTypeList) {
		this.dbTypeList = dbTypeList;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("id", this.getId());
		this.getFormHM().put("name", this.getName());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("dbType", this.getDbType());
		this.getFormHM().put("dbUrl", this.getDbUrl());
		this.getFormHM().put("userName", this.getUserName());
		this.getFormHM().put("password", this.getPassword());
		this.getFormHM().put("mapping", this.getMapping());
		this.getFormHM().put("jobClass", this.getJobClass());
		this.getFormHM().put("enable", this.getEnable());
		
		this.getFormHM().put("ehrTable", this.getEhrTable());
		this.getFormHM().put("extTable", this.getExtTable());
		this.getFormHM().put("hrRelation", this.getHrRelation());
		this.getFormHM().put("extRelation", this.getExtRelation());
		this.getFormHM().put("srcTabCond", this.getSrcTabCond());
		this.getFormHM().put("tagTabCond", this.getTagTabCond());
	}

	@Override
    public void outPutFormHM() {
		
		this.getDataImportForm().setList((ArrayList) this.getFormHM().get("list"));
		this.setId((String) this.getFormHM().get("id"));
		this.setName((String) this.getFormHM().get("name"));
		this.setNbase((String) this.getFormHM().get("nbase"));
		this.setNbaseList((ArrayList) this.getFormHM().get("nbaseList"));
		this.setDbType((String) this.getFormHM().get("dbType"));
		this.setDbTypeList((ArrayList) this.getFormHM().get("dbTypeList"));
		this.setDbUrl((String) this.getFormHM().get("dbUrl"));
		this.setUserName((String) this.getFormHM().get("userName"));
		this.setPassword((String) this.getFormHM().get("password"));
		this.setMapping((String) this.getFormHM().get("mapping"));
		this.setJobClass((String) this.getFormHM().get("jobClass"));
		this.setEnable((String) this.getFormHM().get("enable"));
		this.setEhrTable((String) this.getFormHM().get("ehrTable"));
		this.setExtTable((String) this.getFormHM().get("extTable"));
		this.setHrRelation((String) this.getFormHM().get("hrRelation"));
		this.setExtRelation((String) this.getFormHM().get("extRelation"));
		this.setSrcTabCond((String) this.getFormHM().get("srcTabCond"));
		this.setTagTabCond((String) this.getFormHM().get("tagTabCond"));
		this.setFieldList((ArrayList) this.getFormHM().get("fieldList"));
		
        String tmpmaxOrder = (String)this.getFormHM().get("maxOrder");
        tmpmaxOrder = tmpmaxOrder==null||tmpmaxOrder.length()<1?"0":tmpmaxOrder;
        this.setMaxOrder(tmpmaxOrder);
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		try {
		   if("/sys/export/SearchEmpSync".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null) {
			   if(this.getPagination()!=null)
				   this.getPagination().firstPage();
		   }	
	          
	   } catch(Exception e) {
	   	  e.printStackTrace();
	   }
	   
	   return super.validate(arg0, arg1);
	}

	public PaginationForm getDataImportForm() {
		return dataImportForm;
	}

	public void setDataImportForm(PaginationForm dataImportForm) {
		this.dataImportForm = dataImportForm;
	}

	public ArrayList getNbaseList() {
		return nbaseList;
	}

	public void setNbaseList(ArrayList nbaseList) {
		this.nbaseList = nbaseList;
	}

    public String getMaxOrder(){
        return maxOrder;
    }

    public void setMaxOrder(String order){
        this.maxOrder = order;
    }
}
