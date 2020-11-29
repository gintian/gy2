package com.hjsj.hrms.actionform.train.trainexam.exam;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TrainExamStudentForm extends FrameForm 
{
	private String a_code;
	private String sqlstr;
	private String where;
	private String column;
	
	//考试计划编号
	private String r5400;
	
	//计划名称
	private String studentName;
	
	//计划状态
	private String planStatus;
	
	//界面操作标记
	private String e_flag;
	
	//试卷状态
	private String paperStatus;
	//阅卷状态
	private String checkStatus;
	
	//试卷状态列表
	private ArrayList paperStatusList;
	
	//阅卷状态列表
	private ArrayList checkStatusList;
	
	//考试人员库前缀（人员库权限与培训人员库的交集）
	private String examDBPres;
	
	//分页
	private PaginationForm recordListForm = new PaginationForm();
	
    /** 表的所有字段集合 */
    private ArrayList fieldlist = new ArrayList();
    
    /** 前台新增或者修改页面的标题 */
    private String titlename = "";

    /** 单位权限码 */
    private String orgparentcode;

    /** 部门权限码 */
    private String deptparentcode;

    private String uplevel;
	@Override
    public void outPutFormHM()
	{
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setColumn((String)this.getFormHM().get("column"));
		
	    this.setE_flag((String)this.getFormHM().get("e_flag"));
	    this.setR5400((String)this.getFormHM().get("r5400"));
	    this.setPlanStatus((String)this.getFormHM().get("planStatus"));
	    
	    this.setPaperStatusList((ArrayList)this.getFormHM().get("paperStatusList"));
	    this.setCheckStatusList((ArrayList)this.getFormHM().get("checkStatusList"));
	    this.setStudentName((String)this.getFormHM().get("studentName"));
	    this.setPaperStatus((String)this.getFormHM().get("paperStatus"));
	    this.setCheckStatus((String)this.getFormHM().get("checkStatus"));
	   
	    this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setOrgparentcode((String) this.getFormHM().get("orgparentcode"));
		this.setDeptparentcode((String) this.getFormHM().get("deptparentcode"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setExamDBPres((String)this.getFormHM().get("examDBPres"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
	}
	
	@Override
    public void inPutTransHM()
    {
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("studentName", this.getStudentName());		
		this.getFormHM().put("e_flag",this.getE_flag());
		this.getFormHM().put("r5400",this.getR5400());
		if(this.getPagination()!=null)			
		{
			this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		}
		this.getFormHM().put("paperStatusList", this.getPaperStatusList());
		this.getFormHM().put("checkStatusList", this.getCheckStatusList());
		this.getFormHM().put("paperStatus", this.getPaperStatus());
		this.getFormHM().put("checkStatus", this.getCheckStatus());
		
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("titlename", this.getTitlename());
		this.getFormHM().put("orgparentcode", this.getOrgparentcode());
		this.getFormHM().put("deptparentcode", this.getDeptparentcode());
		this.getFormHM().put("examDBPres", this.getExamDBPres());
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/train/trainexam/exam/student".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {
		        if (this.getPagination() != null && "link".equals(arg1.getParameter("b_query"))) {
    		    	if (this.getPagination() != null)
    		    		this.getPagination().firstPage();
		        }
		    } 
		    else if ("/train/trainexam/exam/student".equals(arg0.getPath()) && arg1.getParameter("b_org") != null)
		    {
		    	if (this.getPagination() != null)
		    		this.getPagination().firstPage();
		    } 
		    
		} 
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return super.validate(arg0, arg1);
    }
	
	public String getSqlstr() {
		return sqlstr;
	}
	
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	
	public String getWhere() {
		return where;
	}
	
	public void setWhere(String where) {
		this.where = where;
	}
	
	public String getColumn() {
		return column;
	}
	
	public void setColumn(String column) {
		this.column = column;
	}
	
	public String getR5400() {
		return r5400;
	}

	public void setR5400(String R5400) {
		this.r5400 = R5400;
	}
	
	public String getE_flag() {
		return e_flag;
	}
	
	public void setE_flag(String e_flag) {
		this.e_flag = e_flag;
	}
	
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getTitlename() {
		return titlename;
	}

	public void setTitlename(String titlename) {
		this.titlename = titlename;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}

	public String getDeptparentcode() {
		return deptparentcode;
	}

	public void setDeptparentcode(String deptparentcode) {
		this.deptparentcode = deptparentcode;
	}

	public String getPaperStatus() {
		return paperStatus;
	}

	public void setPaperStatus(String paperStatus) {
		this.paperStatus = paperStatus;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public ArrayList getPaperStatusList() {
		return paperStatusList;
	}

	public void setPaperStatusList(ArrayList paperStatusList) {
		this.paperStatusList = paperStatusList;
	}

	public ArrayList getCheckStatusList() {
		return checkStatusList;
	}

	public void setCheckStatusList(ArrayList checkStatusList) {
		this.checkStatusList = checkStatusList;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getExamDBPres() {
		return examDBPres;
	}

	public void setExamDBPres(String examDBPres) {
		this.examDBPres = examDBPres;
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
}
