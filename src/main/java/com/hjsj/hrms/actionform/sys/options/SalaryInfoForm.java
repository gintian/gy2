package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SalaryInfoForm extends FrameForm {

  private String a0100; // 员工编号

  private String changeflag;

  private ArrayList columnList = new ArrayList();// 显示字段列表(指标ID描述)

  private String empPre; // 人员库前缀

  private String endDateFlag; // 终止时间

  private String fieldsetid;

  private ArrayList fieldSetList = new ArrayList();// 所选子集

  /** 页面标识 */
  private String flag; // 检索标识(年/季度/月/时间段)

  private ArrayList infoList = new ArrayList();// 薪酬明细显示数据

  private String isMobile;

  private String monthFlag; // 月

  /** 地址分页管理器 */
  private PaginationForm pageListForm = new PaginationForm();

  private String prv_flag;

  private String quarterFlag; // 季度

  private String query_field;// 过滤项

  private String query_name;//

  private String salary;

  private ArrayList showColumnList = new ArrayList();// 显示字段列表(中文描述)

  private String startDateFlag;// 起始时间

  private ArrayList sumColumnList = new ArrayList();// 求和字段列表(指标ID)

  private String title;

  private String year;

  private String year_restrict;

  private String yearFlag; // 年

  private ArrayList yearlist = new ArrayList();

  public SalaryInfoForm() {

  }

  public String getA0100() {
    return this.a0100;
  }

  public String getChangeflag() {
    return this.changeflag;
  }

  public ArrayList getColumnList() {
    return this.columnList;
  }

  public String getEmpPre() {
    return this.empPre;
  }

  public String getEndDateFlag() {
    return this.endDateFlag;
  }

  public String getFieldsetid() {
    return this.fieldsetid;
  }

  public ArrayList getFieldSetList() {
    return this.fieldSetList;
  }

  public String getFlag() {
    return this.flag;
  }

  public ArrayList getInfoList() {
    return this.infoList;
  }

  public String getIsMobile() {
    return this.isMobile;
  }

  public String getMonthFlag() {
    return this.monthFlag;
  }

  public PaginationForm getPageListForm() {
    return this.pageListForm;
  }

  public String getPrv_flag() {
    return this.prv_flag;
  }

  public String getQuarterFlag() {
    return this.quarterFlag;
  }

  public String getQuery_field() {
    return this.query_field;
  }

  public String getQuery_name() {
    return this.query_name;
  }

  public String getSalary() {
    return this.salary;
  }

  public ArrayList getShowColumnList() {
    return this.showColumnList;
  }

  public String getStartDateFlag() {
    return this.startDateFlag;
  }

  public ArrayList getSumColumnList() {
    return this.sumColumnList;
  }

  public String getTitle() {
    return this.title;
  }

  public String getYear() {
    return this.year;
  }

  public String getYear_restrict() {
    return this.year_restrict;
  }

  public String getYearFlag() {
    return this.yearFlag;
  }

  public ArrayList getYearlist() {
    return this.yearlist;
  }

  @Override
  public void inPutTransHM() {
    // System.out.println("进入.....................");
    this.getFormHM().put("a0100", this.getA0100());
    this.getFormHM().put("empPre", this.getEmpPre());
    this.getFormHM().put("fieldsetid", this.getFieldsetid());
    this.getFormHM().put("query_field", this.getQuery_field());
    this.getFormHM().put("title", this.getTitle());
    this.getFormHM().put("salary", this.getSalary());
    this.getFormHM().put("prv_flag", this.getPrv_flag());
  }

  @Override
  public void outPutFormHM() {

    this.getPageListForm()
        .setList((ArrayList) this.getFormHM().get("infoList"));
    this.setShowColumnList((ArrayList) this.getFormHM().get("showcolumnList"));
    this.setColumnList((ArrayList) this.getFormHM().get("columnlist"));

    this.setA0100((String) this.getFormHM().get("a0100"));
    this.setEmpPre((String) this.getFormHM().get("empPre"));

    this.setFlag((String) this.getFormHM().get("flag"));
    this.setYearFlag((String) this.getFormHM().get("yearflag"));
    this.setQuarterFlag((String) this.getFormHM().get("quarterflag"));
    this.setMonthFlag((String) this.getFormHM().get("monthflag"));
    this.setStartDateFlag((String) this.getFormHM().get("startdateflag"));
    this.setEndDateFlag((String) this.getFormHM().get("enddateflag"));

    /*
     * System.out.println("infoList size1 =" + infoList.size());
     * System.out.println("showColumnList size1 = " + showColumnList.size());
     * System.out.println("columnList size1 =" + columnList.size());
     */
    this.setFieldSetList((ArrayList) this.getFormHM().get("fieldSetList"));
    this.setQuery_field((String) this.getFormHM().get("query_field"));
    this.setChangeflag((String) this.getFormHM().get("changeflag"));
    this.setQuery_name((String) this.getFormHM().get("query_name"));
    this.setFieldsetid((String) this.getFormHM().get("fieldsetid"));
    this.setTitle((String) this.getFormHM().get("title"));
    this.setSalary((String) this.getFormHM().get("salary"));
    this.setYearlist((ArrayList) this.getFormHM().get("yearlist"));
    this.setYear_restrict((String) this.getFormHM().get("year_restrict"));
  }

  public void setA0100(String a0100) {
    this.a0100 = a0100;
  }

  public void setChangeflag(String changeflag) {
    this.changeflag = changeflag;
  }

  public void setColumnList(ArrayList columnList) {
    this.columnList = columnList;
  }

  public void setEmpPre(String empPre) {
    this.empPre = empPre;
  }

  public void setEndDateFlag(String endDateFlag) {
    this.endDateFlag = endDateFlag;
  }

  public void setFieldsetid(String fieldsetid) {
    this.fieldsetid = fieldsetid;
  }

  public void setFieldSetList(ArrayList fieldSetList) {
    this.fieldSetList = fieldSetList;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public void setInfoList(ArrayList infoList) {
    this.infoList = infoList;
  }

  public void setIsMobile(String isMobile) {
    this.isMobile = isMobile;
  }

  public void setMonthFlag(String monthFlag) {
    this.monthFlag = monthFlag;
  }

  public void setPageListForm(PaginationForm pageListForm) {
    this.pageListForm = pageListForm;
  }

  public void setPrv_flag(String prv_flag) {
    this.prv_flag = prv_flag;
  }

  public void setQuarterFlag(String quarterFlag) {
    this.quarterFlag = quarterFlag;
  }

  public void setQuery_field(String query_field) {
    this.query_field = query_field;
  }

  public void setQuery_name(String query_name) {
    this.query_name = query_name;
  }

  public void setSalary(String salary) {
    this.salary = salary;
  }

  public void setShowColumnList(ArrayList showColumnList) {
    this.showColumnList = showColumnList;
  }

  public void setStartDateFlag(String startDateFlag) {
    this.startDateFlag = startDateFlag;
  }

  public void setSumColumnList(ArrayList sumColumnList) {
    this.sumColumnList = sumColumnList;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public void setYear_restrict(String year_restrict) {
    this.year_restrict = year_restrict;
  }

  public void setYearFlag(String yearFlag) {
    this.yearFlag = yearFlag;
  }

  public void setYearlist(ArrayList yearlist) {
    this.yearlist = yearlist;
  }

  @Override
  public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

    try {
      if ("/system/options/salaryinfo".equals(arg0.getPath())
          && arg1.getParameter("b_query") != null) {
        if (null != this.pageListForm.getPagination()) {
          this.pageListForm.getPagination().firstPage();
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return super.validate(arg0, arg1);
  }

}
