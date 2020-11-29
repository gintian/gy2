/*
 * Created on 2005-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:ZpExamReportForm</p>
 * <p>Description:考试成绩录入表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 27, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class ZpExamReportForm extends FrameForm {

    /**当前页*/
    private int current=1;
    private String count = "";
    ArrayList sortCondList = new ArrayList();
    String[] fieldsetvalue = null;
    ArrayList fieldList = new ArrayList();
    ArrayList nameList = new ArrayList();
    private String sqlstr;
    private String columns;
    private String strwhere;
    private String orderby;
    private String querycondition;
    
    /**
     * 考试科目对象
     */
    private RecordVo zpExamSubjectvo=new RecordVo("ZP_EXAM_REPORT");
    /**
     * 考试科目对象列表
     */
    private PaginationForm zpExamSubjectForm=new PaginationForm(); 
    /**
     * 考试成绩对象
     */
    private RecordVo zpExamReportvo=new RecordVo("ZP_EXAM_REPORT");
    /**
     * 考试成绩对象列表
     */
    private PaginationForm zpExamReportForm=new PaginationForm(); 

	@Override
    public void outPutFormHM() {
		
        this.setZpExamReportvo((RecordVo)this.getFormHM().get("zpExamReportvo"));
        this.getZpExamReportForm().setList((ArrayList)this.getFormHM().get("sortCondList"));
        this.setZpExamSubjectvo((RecordVo)this.getFormHM().get("zpExamSubjectvo"));
        this.getZpExamSubjectForm().setList((ArrayList)this.getFormHM().get("zpExamSubjectlist"));
        this.setCount((String)this.getFormHM().get("count"));
        this.setSortCondList((ArrayList)this.getFormHM().get("sortCondList")); 	     
        this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
        this.setNameList((ArrayList)this.getFormHM().get("nameList"));
        /**重新定位到当前页*/
	    this.getZpExamReportForm().getPagination().gotoPage(current);
	    this.setColumns((String)this.getFormHM().get("columns"));
	    this.setStrwhere((String)this.getFormHM().get("strwhere"));
	    this.setOrderby((String)this.getFormHM().get("orderby"));
	    this.setSqlstr((String)this.getFormHM().get("sqlstr"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		
        this.getFormHM().put("selectedlist",(ArrayList)this.getZpExamReportForm().getSelectedList());
        this.getFormHM().put("zpExamReportvo",this.getZpExamReportvo());
        this.getFormHM().put("sortCondList",this.getSortCondList());
        this.getFormHM().put("fieldsetvalue",fieldsetvalue);
        this.getFormHM().put("fieldList",this.getFieldList());
        this.getFormHM().put("querycondition",this.getQuerycondition());
        this.getFormHM().put("strwhere",this.getStrwhere());
	}
	
	 @Override
     public void reset(ActionMapping arg0, HttpServletRequest arg1)
	 {
	    	
	      super.reset(arg0, arg1);
	        
	  }
	 
	 /* 
	     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	     */
	    @Override
        public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	       
	        return super.validate(arg0, arg1);
	    }
	/**
	 * @return Returns the current.
	 */
	public int getCurrent() {
		return current;
	}
	/**
	 * @param current The current to set.
	 */
	public void setCurrent(int current) {
		this.current = current;
	}

	/**
	 * @return Returns the zpExamReportForm.
	 */
	public PaginationForm getZpExamReportForm() {
		return zpExamReportForm;
	}
	/**
	 * @param zpExamReportForm The zpExamReportForm to set.
	 */
	public void setZpExamReportForm(PaginationForm zpExamReportForm) {
		this.zpExamReportForm = zpExamReportForm;
	}
	/**
	 * @return Returns the zpExamReportvo.
	 */
	public RecordVo getZpExamReportvo() {
		return zpExamReportvo;
	}
	/**
	 * @param zpExamReportvo The zpExamReportvo to set.
	 */
	public void setZpExamReportvo(RecordVo zpExamReportvo) {
		this.zpExamReportvo = zpExamReportvo;
	}
	/**
	 * @return Returns the zpExamSubjectForm.
	 */
	public PaginationForm getZpExamSubjectForm() {
		return zpExamSubjectForm;
	}
	/**
	 * @param zpExamSubjectForm The zpExamSubjectForm to set.
	 */
	public void setZpExamSubjectForm(PaginationForm zpExamSubjectForm) {
		this.zpExamSubjectForm = zpExamSubjectForm;
	}
	/**
	 * @return Returns the zpExamSubjectvo.
	 */
	public RecordVo getZpExamSubjectvo() {
		return zpExamSubjectvo;
	}
	/**
	 * @param zpExamSubjectvo The zpExamSubjectvo to set.
	 */
	public void setZpExamSubjectvo(RecordVo zpExamSubjectvo) {
		this.zpExamSubjectvo = zpExamSubjectvo;
	}
	/**
	 * @return Returns the count.
	 */
	public String getCount() {
		return count;
	}
	/**
	 * @param count The count to set.
	 */
	public void setCount(String count) {
		this.count = count;
	}
	/**
	 * @return Returns the sortCondList.
	 */
	public ArrayList getSortCondList() {
		return sortCondList;
	}
	/**
	 * @param sortCondList The sortCondList to set.
	 */
	public void setSortCondList(ArrayList sortCondList) {
		this.sortCondList = sortCondList;
	}
	/**
	 * @return Returns the fieldsetvalue.
	 */
	public String[] getFieldsetvalue() {
		return fieldsetvalue;
	}
	/**
	 * @param fieldsetvalue The fieldsetvalue to set.
	 */
	public void setFieldsetvalue(String[] fieldsetvalue) {
		this.fieldsetvalue = fieldsetvalue;
	}
	/**
	 * @return Returns the fieldList.
	 */
	public ArrayList getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList The fieldList to set.
	 */
	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}
	/**
	 * @return Returns the nameList.
	 */
	public ArrayList getNameList() {
		return nameList;
	}
	/**
	 * @param nameList The nameList to set.
	 */
	public void setNameList(ArrayList nameList) {
		this.nameList = nameList;
	}
	/**
	 * @return Returns the columns.
	 */
	public String getColumns() {
		return columns;
	}
	/**
	 * @param columns The columns to set.
	 */
	public void setColumns(String columns) {
		this.columns = columns;
	}
	/**
	 * @return Returns the orderby.
	 */
	public String getOrderby() {
		return orderby;
	}
	/**
	 * @param orderby The orderby to set.
	 */
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	/**
	 * @return Returns the sqlstr.
	 */
	public String getSqlstr() {
		return sqlstr;
	}
	/**
	 * @param sqlstr The sqlstr to set.
	 */
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	/**
	 * @return Returns the strwhere.
	 */
	public String getStrwhere() {
		return strwhere;
	}
	/**
	 * @param strwhere The strwhere to set.
	 */
	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}
	/**
	 * @return Returns the querycondition.
	 */
	public String getQuerycondition() {
		return querycondition;
	}
	/**
	 * @param querycondition The querycondition to set.
	 */
	public void setQuerycondition(String querycondition) {
		this.querycondition = querycondition;
	}
}
