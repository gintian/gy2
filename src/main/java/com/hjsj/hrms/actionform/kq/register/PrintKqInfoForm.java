package com.hjsj.hrms.actionform.kq.register;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class PrintKqInfoForm extends FrameForm{
	private String userbase;
	private String treeCode;//树形菜单，在HtmlMenu中
	private ArrayList pagelist= new ArrayList();
	private String code ;
	private String  curpage;
	private String coursedate;	
	private String kind;
	private ArrayList kq_report_lsit = new ArrayList(); 
	private String report_id;	
    private String pageRows;    
    private ReportParseVo parsevo=new ReportParseVo();
    private PaginationForm printKqInfoForm=new PaginationForm();  
    private String  report_name;
    private String tableHtml;    
    private String turnTableHtml;
    private String url;
    private ArrayList musterlist=new ArrayList();
    private String tabid;
    private String flaginfo;
    private String relatTableid;//高级花名册对应的单表名称
    private String condition;//高级花名册打印的条件
    private String returnURL;//返回的连接
    private String self_flag;
    private String wherestr_s;
    //个人考勤表页面设置
    private String tableUnitHtml;    
    private String turnUnitTableHtml;
    private String report_unitid;
    private String userbaseunit;
    private String unita0100;
    private String username;
    private String codeValue;
    //人员库
    private String dbtype;
	public String getWherestr_s() {
		return wherestr_s;
	}
	public void setWherestr_s(String wherestr_s) {
		this.wherestr_s = wherestr_s;
	}
	public String getSelf_flag() {
		return self_flag;
	}
	public void setSelf_flag(String self_flag) {
		this.self_flag = self_flag;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReport_name() {
		return report_name;
	}
	public void setReport_name(String report_name) {
		this.report_name = report_name;
	}
	public PrintKqInfoForm(){
    	
    }
	public String getPageRows() {
		return pageRows;
	}
	public void setPageRows(String pageRows) {
		this.pageRows = pageRows;
	}
	@Override
    public void outPutFormHM()
	{
		this.setUserbase((String)this.getFormHM().get("userbase"));
	 	this.setCode((String)this.getFormHM().get("code"));
	 	this.setKind((String)this.getFormHM().get("kind"));
	 	this.getPrintKqInfoForm().setList((ArrayList)this.getFormHM().get("kq_report_lsit"));	 	
	 	this.setParsevo((ReportParseVo) this.getFormHM().get("parsevo"));
	 	this.setReport_id((String)this.getFormHM().get("report_id"));
	 	this.setReport_name((String)this.getFormHM().get("report_name"));
	 	this.setTableHtml((String)this.getFormHM().get("tableHtml"));
	 	this.setPagelist((ArrayList)this.getFormHM().get("pagelist"));
	 	this.setCurpage((String)this.getFormHM().get("curpage"));
	 	this.setTurnTableHtml((String)this.getFormHM().get("turnTableHtml"));
	 	this.setUrl((String)this.getFormHM().get("url"));
	 	this.setCoursedate((String)this.getFormHM().get("coursedate"));
	 	this.setMusterlist((ArrayList)this.getFormHM().get("musterlist"));
	 	this.setFlaginfo((String)this.getFormHM().get("flaginfo"));
	 	this.setTabid((String)this.getFormHM().get("tabid"));
	 	this.setRelatTableid((String)this.getFormHM().get("relatTableid"));
	    this.setCondition((String)this.getFormHM().get("condition"));
	    this.setReturnURL((String)this.getFormHM().get("returnURL"));
	    this.setTreeCode((String)this.getFormHM().get("treeCode"));	 
	    this.setSelf_flag((String)this.getFormHM().get("self_flag"));
	    this.setWherestr_s((String)this.getFormHM().get("wherestr_s"));
	    this.setTableUnitHtml((String)this.getFormHM().get("tableUnitHtml"));
	    this.setTurnUnitTableHtml((String)this.getFormHM().get("turnUnitTableHtml"));
	    this.setReport_unitid((String)this.getFormHM().get("report_unitid"));
	    this.setUserbaseunit((String)this.getFormHM().get("userbaseunit"));
	    this.setUnita0100((String)this.getFormHM().get("unita0100"));
	    this.setUsername((String)this.getFormHM().get("username"));
	    this.setCodeValue((String)this.getFormHM().get("codeValue"));
	    this.setDbtype((String)this.getFormHM().get("dbtype"));
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("selectedlist",(ArrayList)this.getPrintKqInfoForm().getSelectedList());
		this.getFormHM().put("userbase",userbase);		
		this.getFormHM().put("code",code);
		this.getFormHM().put("coursedate",coursedate);		
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("report_id",report_id);
		this.getFormHM().put("report_name",report_name);		
		this.getFormHM().put("pageRows",pageRows);		
		this.getFormHM().put("parsevo",this.parsevo);	
		this.getFormHM().put("curpage",curpage);
		this.getFormHM().put("pagelist",pagelist);
		this.getFormHM().put("tabid",this.getTabid());
		this.getFormHM().put("flaginfo",this.getFlaginfo());	
		this.getFormHM().put("relatTableid",this.getRelatTableid());
		this.getFormHM().put("condition",this.getCondition());
		this.getFormHM().put("returnURL",this.getReturnURL());
		this.getFormHM().put("self_flag",this.getSelf_flag());
		this.getFormHM().put("wherestr_s",this.getWherestr_s());
		this.getFormHM().put("report_unitid",report_unitid);
		this.getFormHM().put("userbaseunit",userbaseunit);
		this.getFormHM().put("unita0100",unita0100);
		this.getFormHM().put("username",username);
		this.getFormHM().put("dbtype", dbtype);
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCurpage() {
		return curpage;
	}
	public void setCurpage(String curpage) {
		this.curpage = curpage;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public ArrayList getKq_report_lsit() {
		return kq_report_lsit;
	}
	public void setKq_report_lsit(ArrayList kq_report_lsit) {
		this.kq_report_lsit = kq_report_lsit;
	}	
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public String getCoursedate() {
		return coursedate;
	}
	public void setCoursedate(String coursedate) {
		this.coursedate = coursedate;
	}
	public PaginationForm getPrintKqInfoForm() {
		return printKqInfoForm;
	}
	public void setPrintKqInfoForm(PaginationForm printKqInfoForm) {
		this.printKqInfoForm = printKqInfoForm;
	}
	public String getReport_id() {
		return report_id;
	}
	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}
	
	public ReportParseVo getParsevo() {
		return parsevo;
	}
	public void setParsevo(ReportParseVo parsevo) {
		this.parsevo = parsevo;
	}
	public String getTableHtml() {
		return tableHtml;
	}
	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}
	public ArrayList getPagelist() {
		return pagelist;
	}
	public void setPagelist(ArrayList pagelist) {
		this.pagelist = pagelist;
	}
	public String getTurnTableHtml() {
		return turnTableHtml;
	}
	public void setTurnTableHtml(String turnTableHtml) {
		this.turnTableHtml = turnTableHtml;
	}
	public ArrayList getMusterlist() {
		return musterlist;
	}
	public void setMusterlist(ArrayList musterlist) {
		this.musterlist = musterlist;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getFlaginfo() {
		return flaginfo;
	}
	public void setFlaginfo(String flaginfo) {
		this.flaginfo = flaginfo;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getRelatTableid() {
		return relatTableid;
	}
	public void setRelatTableid(String relatTableid) {
		this.relatTableid = relatTableid;
	}
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getTableUnitHtml() {
		return tableUnitHtml;
	}
	public void setTableUnitHtml(String tableUnitHtml) {
		this.tableUnitHtml = tableUnitHtml;
	}
	public String getTurnUnitTableHtml() {
		return turnUnitTableHtml;
	}
	public void setTurnUnitTableHtml(String turnUnitTableHtml) {
		this.turnUnitTableHtml = turnUnitTableHtml;
	}
	public String getReport_unitid() {
		return report_unitid;
	}
	public void setReport_unitid(String report_unitid) {
		this.report_unitid = report_unitid;
	}
	public String getUserbaseunit() {
		return userbaseunit;
	}
	public void setUserbaseunit(String userbaseunit) {
		this.userbaseunit = userbaseunit;
	}
	public String getUnita0100() {
		return unita0100;
	}
	public void setUnita0100(String unita0100) {
		this.unita0100 = unita0100;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	
}
