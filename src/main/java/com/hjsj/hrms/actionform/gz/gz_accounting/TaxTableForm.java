/**
 * 
 */
package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *<p>Title:个人所得税表单</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-7-5:下午03:24:37</p> 
 *@author cmq
 *@version 4.0
 */
public class TaxTableForm extends FrameForm {
	/**税表明细指标列表*/
    private ArrayList fieldlist=new ArrayList();
    /**税表名称*/
    private String tax_tablename;
    /**查询语名*/
    private String sql;
    /**组织机构代码,选中树形节点对应的组织机构代码值*/
    private String a_code;    
    /**报税时间*/
    private String declaredate;
    /**报税明细表不同的报税时间列表*/
    private ArrayList datelist=new ArrayList();
    // 指标维护
    /**薪资类别*/
    private ArrayList gzmxtypelist;
    /**薪资类别*/
    private String gzmxtypesetname;   
    /**薪资项目*/
    private ArrayList gzmxprolist;
    /**薪资类别id*/
    private String salaryid;
    /**薪资项目字段*/
    private String itemid[] ;  
    /**选中的字段名数组*/
    private String[] right_fields;
    /**常量表字段*/
    private ArrayList rightlist;   
    // 排序
    /**个人名细排序字段*/
    private ArrayList alltaxmxfieldlist;
    /**个人名细排序数组*/
    private String sort_fields[];  
    // 隐藏显示
    /**隐藏字段*/
    private ArrayList hidefieldlist; 
    /**隐藏字段字符串*/
    private String hidefield;  
    // 设置查询条件
    /**薪资类别*/
    private ArrayList congzmxtypelist; 
    /**薪资项目*/
    private ArrayList congzmxprolist;
    /**薪资类别id*/
    private String consalaryid;
    /**薪资项目字段*/
    private String conitemid[]; 
    /**选中的字段名数组*/
    private String conright_fields[];     
    private String expression;   
    private ArrayList factorlist = new ArrayList();   
    private String expre; 
    private ArrayList operlist = new ArrayList();   
    private ArrayList logiclist = new ArrayList();
    /**模糊查询*/
    private String like;
    /**查询条件的SQL*/
    private String condtionsql;
    // 导入文件
    private FormFile importfile;   
    private ArrayList excelDataFiledList;   
    private ArrayList taxMxField;    
    private ArrayList nbaseList;   
    private ArrayList oppositeItemList;
    private String[] oppositeItem;                  
    private String[] nbaseItem;         
    private String is_back;
    // 汇总模板
    /**项目路径*/
	private String path;
	private FormFile tempalefile;  
	private String error = "0";
	/**分页*/
    private int current=1;
	private PaginationForm recordListForm=new PaginationForm();
	private ArrayList fileNameList;
	private ArrayList templateList;
	private String templateName;
    private String importInfo;
    private String returnFlag="0";//返回按钮的走向 0：返回薪资发放的类别界面 1：返回部门月奖金界面
    private String returnvalue="";
    /**新的处理的业务日期-年份*/
    private String theyear;
    /**新的处理业务日期-月份*/
    private String themonth;  
    private String operOrg;
    /**是否支持按隶属部门进行所得税管理*/
    private String deptid;
    /**是否按模块操作单位来进行权限过滤=0按管理范围，=1按模块的操作单位*/
    private String filterByMdule;
    private String rowNums;
    
    private String fromTable="gz_tax_mx";
    private ArrayList fromTableList=new ArrayList();
    
    //gby,2015-01-20, 保存查询时输入的值
    private String queryValue = "";
    
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("fromTable",this.getFromTable());
		this.getFormHM().put("filterByMdule",this.getFilterByMdule());
		this.getFormHM().put("deptid", this.getDeptid());
		this.getFormHM().put("a_code", getA_code());
		this.getFormHM().put("declaredate", getDeclaredate());
		this.getFormHM().put("salaryid", getSalaryid());
		this.getFormHM().put("right_fields", getRight_fields());
		this.getFormHM().put("hidefield", getHidefield());
		this.getFormHM().put("consalaryid", getConsalaryid());
		this.getFormHM().put("conright_fields", getConright_fields());
		this.getFormHM().put("operlist",getOperlist() );
		this.getFormHM().put("factorlist", getFactorlist());
		this.getFormHM().put("sort_fields", getSort_fields());
		this.getFormHM().put("like", getLike());
		this.getFormHM().put("condtionsql", getCondtionsql());
		this.getFormHM().put("importfile",getImportfile());
		this.getFormHM().put("excelDataFiledList",getExcelDataFiledList());
		this.getFormHM().put("taxMxField",getTaxMxField());
		this.getFormHM().put("nbaseList",getNbaseList());
		this.getFormHM().put("oppositeItemList",getOppositeItemList());
		this.getFormHM().put("oppositeItem",getOppositeItem());
		this.getFormHM().put("nbaseItem",getNbaseItem());
		this.getFormHM().put("is_back",this.getIs_back());
		this.getFormHM().put("path",this.getPath());
		this.getFormHM().put("tempalefile",this.getTempalefile());
		this.getFormHM().put("error",this.getError());
		this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
		this.getFormHM().put("templateName",this.getTemplateName());
		this.getFormHM().put("returnFlag", this.getReturnFlag());
		this.getFormHM().put("theyear", this.getTheyear());
		this.getFormHM().put("themonth", this.getThemonth());
		this.getFormHM().put("operOrg", this.getOperOrg());
		this.getFormHM().put("queryValue", this.getQueryValue());
	}
	
	@Override
    public void outPutFormHM() {
		this.setDeclaredate((String)this.getFormHM().get("declaredate"));
		this.setFromTable((String)this.getFormHM().get("fromTable"));
		this.setFromTableList((ArrayList)this.getFormHM().get("fromTableList"));
		this.setRowNums((String)this.getFormHM().get("rowNums"));
		this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
		this.setFilterByMdule((String)this.getFormHM().get("filterByMdule"));
		this.setDeptid((String)this.getFormHM().get("deptid"));
		this.setReturnFlag((String)this.getFormHM().get("returnFlag"));
		this.setThemonth((String)this.getFormHM().get("themonth"));
		this.setTheyear((String)this.getFormHM().get("theyear"));
		this.setOperOrg((String)this.getFormHM().get("operOrg"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setImportInfo((String)this.getFormHM().get("importInfo"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTax_tablename((String)this.getFormHM().get("tablename"));
		this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
		this.setGzmxtypelist((ArrayList)this.getFormHM().get("gzmxtypelist"));
		this.setGzmxprolist((ArrayList)this.getFormHM().get("gzmxprolist"));
		this.setAlltaxmxfieldlist((ArrayList)this.getFormHM().get("alltaxmxfieldlist"));
		this.setHidefieldlist((ArrayList)this.getFormHM().get("hidefieldlist"));
		this.setCongzmxtypelist((ArrayList)this.getFormHM().get("congzmxtypelist"));
		this.setCongzmxprolist((ArrayList)this.getFormHM().get("congzmxprolist"));
		this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
		this.setOperlist((ArrayList)this.getFormHM().get("operlist"));
		this.setExpression((String)this.getFormHM().get("expression"));
		this.setExpre((String)this.getFormHM().get("expre"));
		this.setConright_fields((String[])this.getFormHM().get("conright_fields"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setHidefield((String)this.getFormHM().get("hidefield"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setLogiclist((ArrayList)this.getFormHM().get("logiclist"));
		this.setRightlist((ArrayList)this.getFormHM().get("rightlist"));
		this.setLike((String)this.getFormHM().get("like"));
		this.setCondtionsql((String)this.getFormHM().get("condtionsql"));
		this.setExcelDataFiledList((ArrayList)this.getFormHM().get("excelDataFiledList"));
		this.setTaxMxField((ArrayList)this.getFormHM().get("taxMxField"));
		this.setNbaseList((ArrayList)this.getFormHM().get("nbaseList"));
		this.setOppositeItemList((ArrayList)this.getFormHM().get("oppositeItemList"));
		this.setIs_back((String)this.getFormHM().get("is_back"));
		this.setPath((String)this.getFormHM().get("path"));
		this.setError((String)this.getFormHM().get("error"));
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("fileNameList"));
		this.getRecordListForm().getPagination().gotoPage(current);
//		this.setFileNameList((ArrayList)this.getFormHM().get("fileNameList"));
		this.setTemplateList((ArrayList)this.getFormHM().get("templateList"));
		this.setQueryValue((String)this.getFormHM().get("queryValue"));
	}
	
	public TaxTableForm() {
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
    }  

	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   try
	   {
		   String pajs=arg1.getSession().getServletContext().getRealPath("/templatefile/gz/tax");
           if ("weblogic".equals(SystemConfig.getPropertyValue("webserver"))) {
        	   pajs = arg1.getSession().getServletContext().getResource("/templates/template_ajax_info.jsp").getPath();//.substring(0);
               if (pajs.indexOf(':') != -1) {
            	   pajs = pajs.substring(1);
               } else {
            	   pajs = pajs.substring(0);
               }
               int nlen = pajs.length();
               StringBuffer buf = new StringBuffer();
               buf.append(pajs);
               buf.setLength(nlen - 1);
               pajs = buf.toString();
               pajs = pajs.replace("/templates/template_ajax_info.jsp", "/templatefile/gz/tax");
           }
           this.setPath(SafeCode.encode(PubFunc.encrypt(pajs)));
		   
		   if("/gz/gz_accounting/tax/tax_export_template".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	       {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();
	       }	
		   if("/gz/gz_accounting/tax/tax_export_template".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	       {
	        	if(this.recordListForm.getPagination()!=null)
	        	 current=this.recordListForm.getPagination().getCurrent();
	       }  
       
	   }catch(Exception e)
	   {
	   	  e.printStackTrace();
	   }
       return super.validate(arg0, arg1);
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTax_tablename() {
		return tax_tablename;
	}

	public void setTax_tablename(String tax_tablename) {
		this.tax_tablename = tax_tablename;
	}

	public ArrayList getDatelist() {
		return datelist;
	}

	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}

	public String getDeclaredate() {
		return declaredate;
	}

	public void setDeclaredate(String declaredate) {
		this.declaredate = declaredate;
	}

	public ArrayList getGzmxtypelist() {
		return gzmxtypelist;
	}

	public void setGzmxtypelist(ArrayList gzmxtypelist) {
		this.gzmxtypelist = gzmxtypelist;
	}

	public String getGzmxtypesetname() {
		return gzmxtypesetname;
	}

	public void setGzmxtypesetname(String gzmxtypesetname) {
		this.gzmxtypesetname = gzmxtypesetname;
	}

	public ArrayList getGzmxprolist() {
		return gzmxprolist;
	}

	public void setGzmxprolist(ArrayList gzmxprolist) {
		this.gzmxprolist = gzmxprolist;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String[] getItemid() {
		return itemid;
	}

	public void setItemid(String[] itemid) {
		this.itemid = itemid;
	}


	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public ArrayList getAlltaxmxfieldlist() {
		return alltaxmxfieldlist;
	}

	public void setAlltaxmxfieldlist(ArrayList alltaxmxfieldlist) {
		this.alltaxmxfieldlist = alltaxmxfieldlist;
	}

	public ArrayList getHidefieldlist() {
		return hidefieldlist;
	}

	public void setHidefieldlist(ArrayList hidefieldlist) {
		this.hidefieldlist = hidefieldlist;
	}

	public String getHidefield() {
		return hidefield;
	}

	public void setHidefield(String hidefield) {
		this.hidefield = hidefield;
	}

	public String[] getConitemid() {
		return conitemid;
	}

	public void setConitemid(String[] conitemid) {
		this.conitemid = conitemid;
	}

	public String[] getConright_fields() {
		return conright_fields;
	}

	public void setConright_fields(String[] conright_fields) {
		this.conright_fields = conright_fields;
	}

	public String getConsalaryid() {
		return consalaryid;
	}

	public void setConsalaryid(String consalaryid) {
		this.consalaryid = consalaryid;
	}

	public ArrayList getCongzmxprolist() {
		return congzmxprolist;
	}

	public void setCongzmxprolist(ArrayList congzmxprolist) {
		this.congzmxprolist = congzmxprolist;
	}

	public ArrayList getCongzmxtypelist() {
		return congzmxtypelist;
	}

	public void setCongzmxtypelist(ArrayList congzmxtypelist) {
		this.congzmxtypelist = congzmxtypelist;
	}

	public String getExpre() {
		return expre;
	}

	public void setExpre(String expre) {
		this.expre = expre;
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

	public ArrayList getOperlist() {
		return operlist;
	}

	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}

	public ArrayList getLogiclist() {
		return logiclist;
	}

	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}

	public ArrayList getRightlist() {
		return rightlist;
	}

	public void setRightlist(ArrayList rightlist) {
		this.rightlist = rightlist;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getCondtionsql() {
		return condtionsql;
	}

	public void setCondtionsql(String condtionsql) {
		this.condtionsql = condtionsql;
	}

	public FormFile getImportfile() {
		return importfile;
	}

	public void setImportfile(FormFile importfile) {
		this.importfile = importfile;
	}

	public ArrayList getExcelDataFiledList() {
		return excelDataFiledList;
	}

	public void setExcelDataFiledList(ArrayList excelDataFiledList) {
		this.excelDataFiledList = excelDataFiledList;
	}

	public ArrayList getNbaseList() {
		return nbaseList;
	}

	public void setNbaseList(ArrayList nbaseList) {
		this.nbaseList = nbaseList;
	}

	public ArrayList getOppositeItemList() {
		return oppositeItemList;
	}

	public void setOppositeItemList(ArrayList oppositeItemList) {
		this.oppositeItemList = oppositeItemList;
	}

	public ArrayList getTaxMxField() {
		return taxMxField;
	}

	public void setTaxMxField(ArrayList taxMxField) {
		this.taxMxField = taxMxField;
	}

	public String[] getNbaseItem() {
		return nbaseItem;
	}

	public void setNbaseItem(String[] nbaseItem) {
		this.nbaseItem = nbaseItem;
	}

	public String[] getOppositeItem() {
		return oppositeItem;
	}

	public void setOppositeItem(String[] oppositeItem) {
		this.oppositeItem = oppositeItem;
	}

	public String getIs_back() {
		return is_back;
	}

	public void setIs_back(String is_back) {
		this.is_back = is_back;
	}

	 public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public FormFile getTempalefile() {
		return tempalefile;
	}

	public void setTempalefile(FormFile tempalefile) {
		this.tempalefile = tempalefile;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public ArrayList getFileNameList() {
		return fileNameList;
	}

	public void setFileNameList(ArrayList fileNameList) {
		this.fileNameList = fileNameList;
	}

	public ArrayList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList templateList) {
		this.templateList = templateList;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getImportInfo() {
		return importInfo;
	}

	public void setImportInfo(String importInfo) {
		this.importInfo = importInfo;
	}

	public String getOperOrg()
	{
		return operOrg;
	}

	public void setOperOrg(String operOrg)
	{
		this.operOrg = operOrg;
	}

	public String getReturnFlag()
	{
		return returnFlag;
	}

	public void setReturnFlag(String returnFlag)
	{
		this.returnFlag = returnFlag;
	}

	public String getThemonth()
	{
		return themonth;
	}

	public void setThemonth(String themonth)
	{
		this.themonth = themonth;
	}

	public String getTheyear()
	{
		return theyear;
	}

	public void setTheyear(String theyear)
	{
		this.theyear = theyear;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getFilterByMdule() {
		return filterByMdule;
	}

	public void setFilterByMdule(String filterByMdule) {
		this.filterByMdule = filterByMdule;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getRowNums() {
		return rowNums;
	}

	public void setRowNums(String rowNums) {
		this.rowNums = rowNums;
	}

	public String getFromTable() {
		return fromTable;
	}

	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}

	public ArrayList getFromTableList() {
		return fromTableList;
	}

	public void setFromTableList(ArrayList fromTableList) {
		this.fromTableList = fromTableList;
	}

	public String getQueryValue() {
		return queryValue;
	}

	public void setQueryValue(String queryValue) {
		this.queryValue = queryValue;
	}
	
	
}
