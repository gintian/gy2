package com.hjsj.hrms.actionform.kq.app_check_in.exchange_class;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ExchangeClassForm extends FrameForm {
	 private String treeCode;
	 private String sql;
	 private String where;
	 private String column;
	 private String code;
	 private String kind;
	 private ArrayList fieldlist=new ArrayList();
	 private ArrayList yearlist=new ArrayList();
	 private ArrayList durationlist=new ArrayList();
	 private String kq_year;
	 private String kq_duration;
	 private String frist_flag;
	 private String id;
	 private String audit_flag;
	 private String result;
	 private String radio;
	 private String table;
	 private String start_date;
	 private String end_date;
	 private ArrayList kq_list=new ArrayList();//人员库
	 private String select_name;//筛选名字
	 private String select_flag;//筛选表示
     private String select_pre;
     private String sp_flag;
     private String approved_delete="1";//已批申请登记数据是否可以删除;0:不删除；1：删除
	 /**被选中的人员信息列表*/
	 private ArrayList infolist=new ArrayList();//
	 private String sortid;
	 private String returnvalue="1";
	 
	 private String Opinionlength;//意见长度
	 
	 public String getAudit_flag() {
		return audit_flag;
	 }
	public void setAudit_flag(String audit_flag) {
		this.audit_flag = audit_flag;
	}
	public String getRadio() {
		return radio;
	}
	public void setRadio(String radio) {
		this.radio = radio;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public ArrayList getDurationlist() {
		return durationlist;
	}
	public void setDurationlist(ArrayList durationlist) {
		this.durationlist = durationlist;
	}
	public String getFrist_flag() {
		return frist_flag;
	}
	public void setFrist_flag(String frist_flag) {
		this.frist_flag = frist_flag;
	}
	public String getKq_duration() {
		return kq_duration;
	}
	public void setKq_duration(String kq_duration) {
		this.kq_duration = kq_duration;
	}
	public String getKq_year() {
		return kq_year;
	}
	public void setKq_year(String kq_year) {
		this.kq_year = kq_year;
	}
	public ArrayList getYearlist() {
		return yearlist;
	}
	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}
	private String relatTableid;//高级花名册对应的单表名称
	private String condition;//高级花名册打印的条件
	private String returnURL;//返回的连接
	@Override
    public void outPutFormHM()
	 {
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		 this.setSql((String)this.getFormHM().get("sql"));
		 this.setWhere((String)this.getFormHM().get("where"));
		 this.setColumn((String)this.getFormHM().get("column"));
		 this.setCode((String)this.getFormHM().get("code"));
		 this.setKind((String)this.getFormHM().get("kind"));
		 this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		 this.setFrist_flag((String)this.getFormHM().get("frist_flag"));
		 this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		 this.setDurationlist((ArrayList)this.getFormHM().get("durationlist"));
		 this.setKq_year((String)this.getFormHM().get("kq_year"));
		 this.setKq_duration((String)this.getFormHM().get("kq_duration"));
		 this.setAudit_flag((String)this.getFormHM().get("audit_flag"));
		 this.setResult((String)this.getFormHM().get("result"));
		 this.setRadio((String)this.getFormHM().get("radio"));
		 this.setInfolist((ArrayList)this.getFormHM().get("infolist"));
		 this.setId((String)this.getFormHM().get("id"));
		 this.setTable((String)this.getFormHM().get("table"));
		 this.setStart_date((String)this.getFormHM().get("start_date"));
		 this.setEnd_date((String)this.getFormHM().get("end_date"));
		 
		 this.setRelatTableid((String)this.getFormHM().get("relatTableid"));
		 this.setCondition((String)this.getFormHM().get("condition"));
		 this.setReturnURL((String)this.getFormHM().get("returnURL"));
		 this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
	     this.setSelect_name((String)this.getFormHM().get("select_name"));
	     this.setSelect_flag((String)this.getFormHM().get("select_flag"));
	     this.setSelect_pre((String)this.getFormHM().get("select_pre"));
	     this.setSortid((String)this.getFormHM().get("sortid"));
	     this.setApproved_delete((String)this.getFormHM().get("approved_delete"));
	     this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
	     this.setOpinionlength((String)this.getFormHM().get("opinionlength"));
	 }
	 @Override
     public void inPutTransHM()
	 { 
		 this.getFormHM().put("code",this.getCode());
		 this.getFormHM().put("kind",this.getKind());
		 this.getFormHM().put("fieldlist",this.getFieldlist());
		 this.getFormHM().put("column",this.getColumn());
		 this.getFormHM().put("frist_flag",this.getFrist_flag());
		 this.getFormHM().put("yearlist",this.getYearlist());
		 this.getFormHM().put("kq_year",this.getKq_year());
		 this.getFormHM().put("kq_duration",this.getKq_duration());
		 this.getFormHM().put("audit_flag",this.getAudit_flag());
		 this.getFormHM().put("result",this.getResult());
		 this.getFormHM().put("radio",this.getRadio());	    
	     this.getFormHM().put("result",this.getResult());
	     this.getFormHM().put("infolist",this.getInfolist());
	     this.getFormHM().put("id",this.getId());
	     this.getFormHM().put("table",this.getTable());
	     this.getFormHM().put("start_date",this.getStart_date());
	     this.getFormHM().put("end_date",this.getEnd_date());
		 if(this.getPagination()!=null)
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		 if(this.getPagination()!=null)
			   this.getFormHM().put("dellist",(ArrayList)this.getPagination().getSelectedList());
		 this.getFormHM().put("select_name",this.getSelect_name());
	     this.getFormHM().put("select_flag",this.getSelect_flag());
	     this.getFormHM().put("select_pre",this.getSelect_pre());
	     this.getFormHM().put("sortid",this.getSortid());
	     this.getFormHM().put("returnvalue", this.getReturnvalue());
	     this.getFormHM().put("opinionlength", this.getOpinionlength());
	 }
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
		 if("/kq/app_check_in/exchange_class/exchange".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
         {
       	   if(this.getPagination()!=null)
 	            this.getPagination().firstPage();
       	   this.getFormHM().clear();
       	   this.setCode("");
       	   this.setKind("");
       	   this.setYearlist(null);
       	   this.setDurationlist(null);
       	   this.setFieldlist(null);
       	   this.setColumn("");
       	   this.setFrist_flag("");
       	   this.setStart_date("");
       	   this.setEnd_date("");
         } 
		 if("/kq/app_check_in/exchange_class/exchangedata".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
         {
       	   if(this.getPagination()!=null)
 	            this.getPagination().firstPage();       	  
         } 
		 if("/kq/kqself/exchange_class/exchangedata".equalsIgnoreCase(arg0.getPath())&&arg1.getParameter("b_approve")!=null)
		 {
				this.getFormHM().put("sp_flag", "02");
		 }	
		 if("/kq/app_check_in/exchange_class/exchangedata".equals(arg0.getPath()) && arg1.getParameter("b_choose")!=null)
         {
			 this.getFormHM().clear();
			 this.setRadio("");
			 this.setResult("");
         }
         return super.validate(arg0, arg1);
	 }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public ArrayList getInfolist() {
		return infolist;
	}
	public void setInfolist(ArrayList infolist) {
		this.infolist = infolist;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
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
	public ArrayList getKq_list() {
		return kq_list;
	}
	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}
	public String getSelect_flag() {
		return select_flag;
	}
	public void setSelect_flag(String select_flag) {
		this.select_flag = select_flag;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public String getSelect_pre() {
		return select_pre;
	}
	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}
	public String getSp_flag() {
		return sp_flag;
	}
	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}
	public String getSortid()
	{
	
	    return sortid;
	}
	public void setSortid(String sortid)
	{
	
	    this.sortid = sortid;
	}
	public String getApproved_delete() {
		return approved_delete;
	}
	public void setApproved_delete(String approved_delete) {
		this.approved_delete = approved_delete;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	
	public String getOpinionlength() {
        return Opinionlength;
    }

    public void setOpinionlength(String opinionlength) {
        Opinionlength = opinionlength;
    }
}
