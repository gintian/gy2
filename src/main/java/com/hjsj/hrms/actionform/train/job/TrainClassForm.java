package com.hjsj.hrms.actionform.train.job;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TrainClassForm extends FrameForm {
	private PaginationForm trainClassListForm=new PaginationForm();
	private String a0100="";
	private String dbname="";
	private LazyDynaBean trainClassDesc=new LazyDynaBean();
	private ArrayList  trainResourceDesc=new ArrayList();//培训资料详情
	private ArrayList  r41list=new ArrayList();
	private String titleName="";
	private ArrayList  myTrainClassList=new ArrayList();      //我的培训班
	private String operator="1";               //1:浏览培训班  2：我的培训班
	
	private String sql="";
	private String wherestr="";
	private String columns="";
	
	private String lesson;
	
	private ArrayList yearList = new ArrayList();
	private ArrayList list = new ArrayList();
	private String year;
	private String classname;
	private String orderBy;
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(arg1.getParameter("operate")!=null&& "init".equals(arg1.getParameter("operate")))
		{
			if(this.getTrainClassListForm()!=null)
				this.getTrainClassListForm().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	
	@Override
    public void outPutFormHM() {
		this.setTitleName((String)this.getFormHM().get("titleName"));
		this.getTrainClassListForm().setList((ArrayList)this.getFormHM().get("trainClassList"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setTrainClassDesc((LazyDynaBean)this.getFormHM().get("trainClassDesc"));		
		this.setTrainResourceDesc((ArrayList)this.getFormHM().get("trainResourceDesc"));
		this.setMyTrainClassList((ArrayList)this.getFormHM().get("myTrainClassList"));
		this.setOperator((String)this.getFormHM().get("operator"));
		
		this.setR41list((ArrayList)this.getFormHM().get("r41list"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setWherestr((String)this.getFormHM().get("wherestr"));
		this.setColumns((String)this.getFormHM().get("columns"));
		
		this.setLesson((String)this.getFormHM().get("lesson"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setClassname((String)this.getFormHM().get("classname"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setOrderBy((String)this.getFormHM().get("orderBy"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("yearList", this.getYearList());
		this.getFormHM().put("classname", this.getClassname());
		this.getFormHM().put("list", this.getList());
		this.getFormHM().put("orderBy", this.getOrderBy());
		this.getFormHM().put("selectedList",this.getTrainClassListForm().getSelectedList());
	}

	public PaginationForm getTrainClassListForm() {
		return trainClassListForm;
	}

	public void setTrainClassListForm(PaginationForm trainClassListForm) {
		this.trainClassListForm = trainClassListForm;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public LazyDynaBean getTrainClassDesc() {
		return trainClassDesc;
	}

	public void setTrainClassDesc(LazyDynaBean trainClassDesc) {
		this.trainClassDesc = trainClassDesc;
	}



	public ArrayList getTrainResourceDesc() {
		return trainResourceDesc;
	}

	public void setTrainResourceDesc(ArrayList trainResourceDesc) {
		this.trainResourceDesc = trainResourceDesc;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public ArrayList getMyTrainClassList() {
		return myTrainClassList;
	}

	public void setMyTrainClassList(ArrayList myTrainClassList) {
		this.myTrainClassList = myTrainClassList;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public ArrayList getR41list() {
		return r41list;
	}

	public void setR41list(ArrayList r41list) {
		this.r41list = r41list;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getLesson() {
		return lesson;
	}

	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	public ArrayList getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

}
