package com.hjsj.hrms.actionform.hire.interviewEvaluating;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class InterviewArrangeForm extends FrameForm {
	private String operateType="user";   // user:业务平台 employ：自助平台
	private PaginationForm interviewArrangeListform=new PaginationForm();
	private String linkDesc="";
	private String codeID="";
	private String isPhoneField="#";  //是否设置了电话指标
	private String isMailField="#";	  //是否设置了email指标
	private String dbName="";         //应用库前缀
	private ArrayList columnsList=new ArrayList();
	private String extendWhereSql="";
	private String orderSql="";//" order by Z05.state asc,Z05.z0509 asc";默认的sql 的话在交易类里面给上吧,这里不要加了 xcs 2014-10-15
	private String username="";
	
	private String selectIDs="";
	private String zpdd="";
	private String zykg="";
	private String wykg="";
	private String mmsj="";
	private String a0100="";
	private String posid="";
	private ArrayList interviewingRevertItemCodeList=new ArrayList();
	private String interviewingRevertItemid="";
	private String interviewingCodeValue="";
	private PaginationForm interviewListForm=new PaginationForm();
	
	private String select_sql;
	private String where_sql;
	private String order_sql;
	private String cloumns;
	private String codesetid;
	private String dbpre_str;
	private String start_date;
	private String end_date;
	private String extendWhereSql1="";
	private String state="";
	@Override
    public void outPutFormHM() {
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setDbpre_str((String)this.getFormHM().get("dbpre_str"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setCloumns((String)this.getFormHM().get("cloumns"));
		this.setOrder_sql((String)this.getFormHM().get("order_sql"));
		this.setWhere_sql((String)this.getFormHM().get("where_sql"));
		this.setSelect_sql((String)this.getFormHM().get("select_sql"));
		this.setInterviewingCodeValue((String)this.getFormHM().get("interviewingCodeValue"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setPosid((String)this.getFormHM().get("posid"));
		this.setInterviewingRevertItemCodeList((ArrayList)this.getFormHM().get("interviewingRevertItemCodeList"));
		this.setInterviewingRevertItemid((String)this.getFormHM().get("interviewingRevertItemid"));
		this.setOperateType((String)this.getFormHM().get("operateType"));
		this.setLinkDesc((String)this.getFormHM().get("linkDesc"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.getInterviewListForm().setList((ArrayList)this.getFormHM().get("interviewList"));
		this.getInterviewArrangeListform().setList((ArrayList)this.getFormHM().get("interviewArrangeList"));
		this.setCodeID((String)this.getFormHM().get("codeid"));
		this.setIsMailField((String)this.getFormHM().get("isMailField"));
		this.setIsPhoneField((String)this.getFormHM().get("isPhoneField"));
		this.setDbName((String)this.getFormHM().get("dbName"));
		this.setColumnsList((ArrayList)this.getFormHM().get("columnsList"));
		this.setExtendWhereSql((String)this.getFormHM().get("extendWhereSql"));
		this.setOrderSql((String)this.getFormHM().get("orderSql"));
		this.setExtendWhereSql1((String)this.getFormHM().get("extendWhereSql1"));
		this.setState((String)this.getFormHM().get("state"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("start_date", this.getStart_date());
		this.getFormHM().put("end_date", this.getEnd_date());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("dbpre_str",this.getDbpre_str());
		this.getFormHM().put("interviewingCodeValue", this.getInterviewingCodeValue());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("posid",this.getPosid());
		this.getFormHM().put("interviewingRevertItemid", this.getInterviewingRevertItemid());
		this.getFormHM().put("operateType",this.getOperateType());
		this.getFormHM().put("extendWhereSql",extendWhereSql);
		this.getFormHM().put("orderSql",orderSql);
		
		this.getFormHM().put("selectIDs",this.getSelectIDs());
		this.getFormHM().put("zpdd",this.getZpdd());
		this.getFormHM().put("zykg",this.getZykg());
		this.getFormHM().put("wykg",this.getWykg());
		this.getFormHM().put("mmsj",this.getMmsj());
		this.getFormHM().put("state", this.getState());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/hire/interviewEvaluating/interviewRevert".equals(arg0.getPath())&&(arg1.getParameter("b_interview")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/hire/interviewEvaluating/interviewArrange".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{	
			String a =arg1.getParameter("opt");
			if(arg1.getParameter("opt")!=null&& "firstPage".equals(arg1.getParameter("opt")))
            this.getInterviewArrangeListform().getPagination().firstPage();
        }	
		if("/hire/interviewEvaluating/interviewArrange".equals(arg0.getPath())&&(arg1.getParameter("br_query")!=null))
		{
            this.getInterviewArrangeListform().getPagination().firstPage();
        }
		if("/hire/interviewEvaluating/interviewAnnounce".equals(arg0.getPath())&&(arg1.getParameter("br_query")!=null))
		{   
			this.getInterviewArrangeListform().getPagination().firstPage();          
        }
		return super.validate(arg0, arg1);
	}
	public PaginationForm getInterviewArrangeListform() {
		return interviewArrangeListform;
	}

	public void setInterviewArrangeListform(PaginationForm interviewArrangeListform) {
		this.interviewArrangeListform = interviewArrangeListform;
	}

	public String getCodeID() {
		return codeID;
	}

	public void setCodeID(String codeID) {
		this.codeID = codeID;
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

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public ArrayList getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(ArrayList columnsList) {
		this.columnsList = columnsList;
	}

	public String getExtendWhereSql() {
		return extendWhereSql;
	}

	public void setExtendWhereSql(String extendWhereSql) {
		this.extendWhereSql = extendWhereSql;
	}

	public String getOrderSql() {
		return orderSql;
	}

	public void setOrderSql(String orderSql) {
		this.orderSql = orderSql;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLinkDesc() {
		return linkDesc;
	}

	public void setLinkDesc(String linkDesc) {
		this.linkDesc = linkDesc;
	}

	public String getMmsj() {
		return mmsj;
	}

	public void setMmsj(String mmsj) {
		this.mmsj = mmsj;
	}

	public String getSelectIDs() {
		return selectIDs;
	}

	public void setSelectIDs(String selectIDs) {
		this.selectIDs = selectIDs;
	}

	public String getWykg() {
		return wykg;
	}

	public void setWykg(String wykg) {
		this.wykg = wykg;
	}

	public String getZpdd() {
		return zpdd;
	}

	public void setZpdd(String zpdd) {
		this.zpdd = zpdd;
	}

	public String getZykg() {
		return zykg;
	}

	public void setZykg(String zykg) {
		this.zykg = zykg;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getPosid() {
		return posid;
	}

	public void setPosid(String posid) {
		this.posid = posid;
	}

	public ArrayList getInterviewingRevertItemCodeList() {
		return interviewingRevertItemCodeList;
	}

	public void setInterviewingRevertItemCodeList(
			ArrayList interviewingRevertItemCodeList) {
		this.interviewingRevertItemCodeList = interviewingRevertItemCodeList;
	}

	public String getInterviewingRevertItemid() {
		return interviewingRevertItemid;
	}

	public void setInterviewingRevertItemid(String interviewingRevertItemid) {
		this.interviewingRevertItemid = interviewingRevertItemid;
	}

	public String getInterviewingCodeValue() {
		return interviewingCodeValue;
	}

	public void setInterviewingCodeValue(String interviewingCodeValue) {
		this.interviewingCodeValue = interviewingCodeValue;
	}

	public PaginationForm getInterviewListForm() {
		return interviewListForm;
	}

	public void setInterviewListForm(PaginationForm interviewListForm) {
		this.interviewListForm = interviewListForm;
	}

	public String getSelect_sql() {
		return select_sql;
	}

	public void setSelect_sql(String select_sql) {
		this.select_sql = select_sql;
	}

	public String getWhere_sql() {
		return where_sql;
	}

	public void setWhere_sql(String where_sql) {
		this.where_sql = where_sql;
	}

	public String getOrder_sql() {
		return order_sql;
	}

	public void setOrder_sql(String order_sql) {
		this.order_sql = order_sql;
	}

	public String getCloumns() {
		return cloumns;
	}

	public void setCloumns(String cloumns) {
		this.cloumns = cloumns;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getDbpre_str() {
		return dbpre_str;
	}

	public void setDbpre_str(String dbpre_str) {
		this.dbpre_str = dbpre_str;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getExtendWhereSql1() {
		return extendWhereSql1;
	}

	public void setExtendWhereSql1(String extendWhereSql1) {
		this.extendWhereSql1 = extendWhereSql1;
	}
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
