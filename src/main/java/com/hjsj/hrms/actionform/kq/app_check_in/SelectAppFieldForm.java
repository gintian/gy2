package com.hjsj.hrms.actionform.kq.app_check_in;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SelectAppFieldForm extends FrameForm{

	private ArrayList fieldlist=new ArrayList();
	 /**关系操作符*/
    private ArrayList operlist=new ArrayList();
    /**逻辑操作符*/
    private ArrayList logiclist=new ArrayList(); 
    
    private ArrayList selectedlist = new ArrayList();
    /**factor list*/
    private ArrayList factorlist=new ArrayList(); 
    /**权限范围内的人员库*/
    private ArrayList dblist=new ArrayList();
    private String expression;
    private String sqlstr_s;
    private String wherestr_s;
    private String ordeby_s;
    private String columnstr_s;
    private ArrayList fielditemlist= new ArrayList();
    private String code;
    private String kind;
    private String treeCode;//树形菜单，在HtmlMenu中
    private String table;
    private String group_id;
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getColumnstr_s() {
		return columnstr_s;
	}
	public void setColumnstr_s(String columnstr_s) {
		this.columnstr_s = columnstr_s;
	}
	public String getOrdeby_s() {
		return ordeby_s;
	}
	public void setOrdeby_s(String ordeby_s) {
		this.ordeby_s = ordeby_s;
	}
	public String getSqlstr_s() {
		return sqlstr_s;
	}
	public void setSqlstr_s(String sqlstr_s) {
		this.sqlstr_s = sqlstr_s;
	}
	public String getWherestr_s() {
		return wherestr_s;
	}
	public void setWherestr_s(String wherestr_s) {
		this.wherestr_s = wherestr_s;
	}
	public ArrayList getSelectedlist() {
		return selectedlist;
	}
	public void setSelectedlist(ArrayList selectedlist) {
		if(selectedlist==null)
			selectedlist=new ArrayList();
		this.selectedlist = selectedlist;
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
	public SelectAppFieldForm() {
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
//        vo=new CommonData("like","包含");
//        operlist.add(vo);        
        vo=new CommonData("*","并且");
        logiclist.add(vo);
        vo=new CommonData("+","或");  
        logiclist.add(vo);
	}
	@Override
    public void outPutFormHM()
	{
	   this.setTreeCode((String)this.getFormHM().get("treeCode"));
	   this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	   this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
	   this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
	   this.setDblist((ArrayList)this.getFormHM().get("dblist"));
	   this.setSqlstr_s((String)this.getFormHM().get("sqlstr_s"));
	   this.setWherestr_s((String)this.getFormHM().get("wherestr_s"));
	   this.setOrdeby_s((String)this.getFormHM().get("ordeby_s"));
	   this.setColumnstr_s((String)this.getFormHM().get("columnstr_s"));
	   this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist")); 
	   this. setTable((String)this.getFormHM().get("table"));
	   this.setGroup_id((String)this.getFormHM().get("group_id"));
	}
	private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  
    /**能用查询的表达式:!(1+2*3),!非，＋或，*且*/
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("table",this.getTable());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("expression",this.getExpression());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("kind",this.getKind());
		this.getFormHM().put("group_id",this.getGroup_id());
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String[] getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
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
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public ArrayList getFielditemlist() {
		return fielditemlist;
	}
	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   
	    return super.validate(arg0, arg1);
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
}

