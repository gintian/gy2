package com.hjsj.hrms.actionform.gz.gz_accounting.bankdisk;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class BankDiskForm extends FrameForm{
	public BankDiskForm()
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
	/**代发银行编号*/
	private String bank_id;
	/**代发银行名称*/
	private String bank_name;
	/**首末行输出串*/
	private String bankFormat;
	/**首末行标识*/
    private String  bankCheck;//是否在生成文件中加说明行,0:不加,1:首行,2:末行
    /**代发银行项目名称列表*/
    private ArrayList columnsList = new ArrayList();
    /**数据列表*/
    private ArrayList dataList = new ArrayList();
    /**代发银行列表*/
    private ArrayList bankList = new ArrayList();
    private ArrayList column = new ArrayList();
    private String bankListSize;
    private String columnListSize;
    /**薪资表名称*/
    private String tableName;
    /**人员过滤UN/UM+VALUE*/
    private String code;
    /**薪资类别*/
    private String salaryid;
    /**已选的显示指标列表*/
    private ArrayList selectedFieldList = new ArrayList();
    private ArrayList allList = new ArrayList();
    private String[] itemidArray=new String[0];
    private String[] right_fields = new String[0];
    private String[] itemid=new String[0];
    private String rightFields;
    
    private String bankFormatValue;
    /**人员筛选指标条件的逻辑符列表*/
    private ArrayList logicList=new ArrayList();
    /**人员筛选指标条件的关系符列表*/
	private ArrayList connectionList = new ArrayList();
	/**人员筛选条件列表*/
	private ArrayList personFilterList=new ArrayList();
	/**分页*/
	private PaginationForm bankDiskListForm = new PaginationForm();
	private String sql;
	private String tabname;
	private String filterSql;
	private ArrayList filterCondList = new ArrayList();
	private String filterCondId;
	private String condName;
	   /**关系操作符*/
    private ArrayList operlist=new ArrayList();
    /**逻辑操作符*/
    private ArrayList logiclist=new ArrayList(); 
    private String isclose;
    private String issave;
    private ArrayList condbeanlist= new ArrayList();
    private String condsize;
    private String count;
    private String condfield="";
    private String expr;
    private String beforeSql;
    private ArrayList sortFieldList = new ArrayList();
    private String model;//=0从工资发放进入，=1从工资审批进入
    private String boscount;
    private String bosdate;
    private String scope="";
    private String username="";
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
    public void outPutFormHM() {
		this.setBoscount((String)this.getFormHM().get("boscount"));
		this.setBosdate((String)this.getFormHM().get("bosdate"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setSortFieldList((ArrayList)this.getFormHM().get("sortFieldList"));
		this.setBeforeSql((String)this.getFormHM().get("beforeSql"));
		this.setBank_id((String)this.getFormHM().get("bank_id"));
		this.setColumnsList((ArrayList)this.getFormHM().get("columnsList"));
		this.setDataList((ArrayList)this.getFormHM().get("dataList"));
		this.setBankFormat((String)this.getFormHM().get("bankFormat"));
		this.setBankCheck((String)this.getFormHM().get("bankCheck"));
		this.setBankList((ArrayList)this.getFormHM().get("bankList"));
		this.setColumn((ArrayList)this.getFormHM().get("column"));
		this.setBankListSize((String)this.getFormHM().get("bankListSize"));
		this.setTableName((String)this.getFormHM().get("tableName"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setSelectedFieldList((ArrayList)this.getFormHM().get("selectedFieldList"));
	    this.setItemidArray((String[])this.getFormHM().get("itemidArray"));
	    this.setAllList((ArrayList)this.getFormHM().get("allList"));
	    this.setLogicList((ArrayList)this.getFormHM().get("logicList"));
	    this.setConnectionList((ArrayList)this.getFormHM().get("connectionList"));
	    this.setPersonFilterList((ArrayList)this.getFormHM().get("personFilterList"));
	    this.setColumnListSize((String)this.getFormHM().get("columnListSize"));
	    this.getBankDiskListForm().setList((ArrayList)this.getFormHM().get("dataList"));
	    this.setRightFields((String)this.getFormHM().get("rightFields"));
	    this.setTabname((String)this.getFormHM().get("tabname"));
	    this.setSql((String)this.getFormHM().get("sql"));
	    this.setFilterSql((String)this.getFormHM().get("filterSql"));
	    this.setFilterCondList((ArrayList)this.getFormHM().get("filterCondList"));
	    this.setFilterCondId((String)this.getFormHM().get("filterCondId"));
	    this.setIsclose((String)this.getFormHM().get("isclose"));
	    this.setIssave((String)this.getFormHM().get("issave"));
	    this.setCondbeanlist((ArrayList)this.getFormHM().get("condbeanlist"));
	    this.setCondsize((String)this.getFormHM().get("condsize"));
	    this.setCount((String)this.getFormHM().get("count"));
	    this.setCondfield((String)this.getFormHM().get("condfield"));
	    this.setCondName((String)this.getFormHM().get("condName"));
	    this.setExpr((String)this.getFormHM().get("expr"));
	    this.setScope((String)this.getFormHM().get("scope"));
	    this.setUsername((String)this.getFormHM().get("username"));
	    this.setBank_name((String)this.getFormHM().get("bank_name"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("boscount",this.getBoscount());
		this.getFormHM().put("bosdate",this.getBosdate());
		this.getFormHM().put("model",this.getModel());
		this.getFormHM().put("bank_id",this.getBank_id());
		this.getFormHM().put("bank_name",this.getBank_name());
		this.getFormHM().put("bankFormat",this.getBankFormat());
		this.getFormHM().put("bankCheck",this.getBankCheck());
		this.getFormHM().put("tableName",this.getTableName());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("salaryid",this.getSalaryid());
		this.getFormHM().put("itemidArray",this.getItemidArray());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("rightFields",this.getRightFields());
		this.getFormHM().put("selectedFieldList",this.getSelectedFieldList());
		this.getFormHM().put("bankFormatValue",this.getBankFormatValue());
		this.getFormHM().put("personFilterList",this.getPersonFilterList());
		this.getFormHM().put("selectedList",this.getBankDiskListForm().getSelectedList());
		this.getFormHM().put("filterCondId",this.getFilterCondId());
		this.getFormHM().put("condName",this.getCondName());
		this.getFormHM().put("filterSql",this.getFilterSql());
		this.getFormHM().put("condfield",this.getCondfield());
		this.getFormHM().put("expr",this.getExpr());
		this.getFormHM().put("beforeSql", this.getBeforeSql());
		this.getFormHM().put("scope", this.getScope());
		this.getFormHM().put("usernmae", this.getUsername());
	}

	public String getBank_id() {
		return bank_id;
	}

	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getBankCheck() {
		return bankCheck;
	}

	public void setBankCheck(String bankCheck) {
		this.bankCheck = bankCheck;
	}

	public String getBankFormat() {
		return bankFormat;
	}

	public void setBankFormat(String bankFormat) {
		this.bankFormat = bankFormat;
	}

	public ArrayList getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(ArrayList columnsList) {
		this.columnsList = columnsList;
	}

	public ArrayList getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}

	public ArrayList getBankList() {
		return bankList;
	}

	public void setBankList(ArrayList bankList) {
		this.bankList = bankList;
	}

	public ArrayList getColumn() {
		return column;
	}

	public void setColumn(ArrayList column) {
		this.column = column;
	}

	public String getBankListSize() {
		return bankListSize;
	}

	public void setBankListSize(String bankListSize) {
		this.bankListSize = bankListSize;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList getSelectedFieldList() {
		return selectedFieldList;
	}

	public void setSelectedFieldList(ArrayList selectedFieldList) {
		this.selectedFieldList = selectedFieldList;
	}

	public String[] getItemidArray() {
		return itemidArray;
	}

	public void setItemidArray(String[] itemidArray) {
		this.itemidArray = itemidArray;
	}

	public ArrayList getAllList() {
		return allList;
	}

	public void setAllList(ArrayList allList) {
		this.allList = allList;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String[] getItemid() {
		return itemid;
	}

	public void setItemid(String[] itemid) {
		this.itemid = itemid;
	}

	public String getRightFields() {
		return rightFields;
	}

	public void setRightFields(String rightFields) {
		this.rightFields = rightFields;
	}

	public String getBankFormatValue() {
		return bankFormatValue;
	}

	public void setBankFormatValue(String bankFormatValue) {
		this.bankFormatValue = bankFormatValue;
	}

	public ArrayList getConnectionList() {
		return connectionList;
	}

	public void setConnectionList(ArrayList connectionList) {
		this.connectionList = connectionList;
	}

	public ArrayList getLogicList() {
		return logicList;
	}

	public void setLogicList(ArrayList logicList) {
		this.logicList = logicList;
	}

	public ArrayList getPersonFilterList() {
		return personFilterList;
	}

	public void setPersonFilterList(ArrayList personFilterList) {
		this.personFilterList = personFilterList;
	}

	public String getColumnListSize() {
		return columnListSize;
	}

	public void setColumnListSize(String columnListSize) {
		this.columnListSize = columnListSize;
	}

	public PaginationForm getBankDiskListForm() {
		return bankDiskListForm;
	}

	public void setBankDiskListForm(PaginationForm bankDiskListForm) {
		this.bankDiskListForm = bankDiskListForm;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTabname() {
		return tabname;
	}

	public void setTabname(String tabname) {
		this.tabname = tabname;
	}

	public String getFilterSql() {
		return filterSql;
	}

	public void setFilterSql(String filterSql) {
		this.filterSql = filterSql;
	}

	public ArrayList getFilterCondList() {
		return filterCondList;
	}

	public void setFilterCondList(ArrayList filterCondList) {
		this.filterCondList = filterCondList;
	}

	public String getFilterCondId() {
		return filterCondId;
	}

	public void setFilterCondId(String filterCondId) {
		this.filterCondId = filterCondId;
	}

	public String getCondName() {
		return condName;
	}

	public void setCondName(String condName) {
		this.condName = condName;
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

	public String getIsclose() {
		return isclose;
	}

	public void setIsclose(String isclose) {
		this.isclose = isclose;
	}

	public String getIssave() {
		return issave;
	}

	public void setIssave(String issave) {
		this.issave = issave;
	}

	public ArrayList getCondbeanlist() {
		return condbeanlist;
	}

	public void setCondbeanlist(ArrayList condbeanlist) {
		this.condbeanlist = condbeanlist;
	}

	public String getCondsize() {
		return condsize;
	}

	public void setCondsize(String condsize) {
		this.condsize = condsize;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getCondfield() {
		return condfield;
	}

	public void setCondfield(String condfield) {
		this.condfield = condfield;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getBeforeSql() {
		return beforeSql;
	}

	public void setBeforeSql(String beforeSql) {
		this.beforeSql = beforeSql;
	}

	public ArrayList getSortFieldList() {
		return sortFieldList;
	}

	public void setSortFieldList(ArrayList sortFieldList) {
		this.sortFieldList = sortFieldList;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBoscount() {
		return boscount;
	}

	public void setBoscount(String boscount) {
		this.boscount = boscount;
	}

	public String getBosdate() {
		return bosdate;
	}

	public void setBosdate(String bosdate) {
		this.bosdate = bosdate;
	}

}
