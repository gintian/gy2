package com.hjsj.hrms.actionform.kq.register;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class BrowseRegisterForm extends FrameForm{
	  private String treeCode;//树形菜单，在HtmlMenu中
	  private String userbase; //**应用库表前缀*/		  
	  private String coursedate;
	  private String code;//连接级别
	  private String dbcond;//	  
	  private String operatedate;
	  private String columns;	 
	  private ArrayList courselist=new ArrayList();//日期list
	  private ArrayList fielditemlist=new ArrayList();	 
	  private String sqlstr;
	  private String strwhere;
	  private String orderby; 
	  private String A0100;
	  private String dbase;
	  private String orgvali;	
	  private String kind;
	  private String codesetid;	 
	  private String orgsumvali;
	  private String kq_period;
	  private String start_date;
	  private String end_date;
	  private String kq_duration;
	  private ArrayList notapp_list = new ArrayList();
	  private ArrayList datelist = new ArrayList();
	  private ArrayList kq_dbase_list= new ArrayList();
	  private PaginationForm recordListForm=new PaginationForm();
	  private PaginationForm notAppListForm=new PaginationForm();
	  private PaginationForm notQ07ListForm=new PaginationForm();
	  private PaginationForm notQ09ListForm=new PaginationForm();
	  private ArrayList notQ07_list=new ArrayList();
	  private ArrayList notQ09_list=new ArrayList();
	  private ArrayList selectfieldlist=new ArrayList();
	  private RecordVo one_vo = new RecordVo("Q05",1);
	  private String notapptag;
	  private String overrule;
	  private ArrayList fieldlist=new ArrayList();
	  private String s_strsql;
	  private String s_columns;
	  private String infor_Flag="1";  
	  private String po_wherestr;
	  private String po_column;
	  private String po_sqlstr;
	  private String destfld;
	  private String temp_table;
	  private String bytesid;
	  private String setlist;
	  private String pigeonhole_flag;
	  private String flag;
	  private String error_message;
	  private String error_flag;
	  private String error_return;
	  private String error_stuts;
	  private ArrayList kq_list=new ArrayList();//人员库
	  private String select_name;//筛选名字
	  private String select_flag;//筛选表示
	  private String select_type="0";
	  private String select_pre;
	  private String re_url;
	  private String sp_result;
	  private String selectResult;//
	  private String selectsturs;//查询标记
	  private String selectys;//转换小时 1=默认；2=HH:MM
	  private String returnvalue="1";
	  /** 考勤规则**/
	  private HashMap kqItem_hash=new HashMap();
	  // 部门层级
	  private String uplevel;
	  public String getSelectResult() {
		return selectResult;
	}
	public void setSelectResult(String selectResult) {
		this.selectResult = selectResult;
	}
	public String getSelectsturs() {
		return selectsturs;
	}
	public void setSelectsturs(String selectsturs) {
		this.selectsturs = selectsturs;
	}
	public String getSp_result() {
		return sp_result;
	}
	public void setSp_result(String sp_result) {
		this.sp_result = sp_result;
	}
	public String getBytesid() {
		return bytesid;
	}
	public void setBytesid(String bytesid) {
		this.bytesid = bytesid;
	}
	public String getDestfld() {
		return destfld;
	}
	public void setDestfld(String destfld) {
		this.destfld = destfld;
	}
	public String getInfor_Flag() {
		return infor_Flag;
	}
	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}
	public String getPigeonhole_flag() {
		return pigeonhole_flag;
	}
	public void setPigeonhole_flag(String pigeonhole_flag) {
		this.pigeonhole_flag = pigeonhole_flag;
	}
	public String getPo_column() {
		return po_column;
	}
	public void setPo_column(String po_column) {
		this.po_column = po_column;
	}
	public String getPo_sqlstr() {
		return po_sqlstr;
	}
	public void setPo_sqlstr(String po_sqlstr) {
		this.po_sqlstr = po_sqlstr;
	}
	public String getPo_wherestr() {
		return po_wherestr;
	}
	public void setPo_wherestr(String po_wherestr) {
		this.po_wherestr = po_wherestr;
	}
	public String getSetlist() {
		return setlist;
	}
	public void setSetlist(String setlist) {
		this.setlist = setlist;
	}
	public String getTemp_table() {
		return temp_table;
	}
	public void setTemp_table(String temp_table) {
		this.temp_table = temp_table;
	}
	public PaginationForm getRecordListForm() {
		return recordListForm;
	  }
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}
	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}
	public String getOrgsumvali() {
		return orgsumvali;
	}
	public void setOrgsumvali(String orgsumvali) {
		this.orgsumvali = orgsumvali;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	@Override
    public void outPutFormHM(){
    	this.setTreeCode((String)this.getFormHM().get("treeCode"));	 
 	   this.setDbcond((String)this.getFormHM().get("dbcond"));
 	   this.setDbase((String)this.getFormHM().get("dbase"));
 	   this.setUserbase((String)this.getFormHM().get("userbase"));
 	   this.setCode((String)this.getFormHM().get("code"));
 	   this.setCourselist((ArrayList)this.getFormHM().get("courselist")); 	   
 	   this.setSqlstr((String)this.getFormHM().get("sqlstr"));
 	   this.setColumns((String)this.getFormHM().get("columns"));
 	   this.setStrwhere((String)this.getFormHM().get("strwhere"));
 	   this.setOrderby((String)this.getFormHM().get("orderby"));
 	   this.setKind((String)this.getFormHM().get("kind"));
 	   this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist")); 	   
 	   this.setOrgvali((String)this.getFormHM().get("orgvali"));
 	   this.setCoursedate((String)this.getFormHM().get("coursedate"));
       this.setCodesetid((String)this.getFormHM().get("codesetid"));  
       this.setOrgsumvali((String)this.getFormHM().get("orgsumvali"));
       this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
       this.setKq_period((String)this.getFormHM().get("kq_period"));
       this.setStart_date((String)this.getFormHM().get("start_date"));
       this.setEnd_date((String)this.getFormHM().get("end_date"));
       this.setKq_duration((String)this.getFormHM().get("kq_duration"));
       if(this.getFormHM().get("selectfieldlist")!=null)
           this.setSelectfieldlist((ArrayList)this.getFormHM().get("selectfieldlist"));
       this.setOne_vo((RecordVo)this.getFormHM().get("one_vo"));
       this.setKq_dbase_list((ArrayList)this.getFormHM().get("kq_dbase_list"));
       this.getNotAppListForm().setList((ArrayList)this.getFormHM().get("notapp_list"));
       this.setNotapptag((String)this.getFormHM().get("notapptag"));
       this.getNotQ07ListForm().setList((ArrayList)this.getFormHM().get("notQ07_list"));
       this.getNotQ09ListForm().setList((ArrayList)this.getFormHM().get("notQ09_list"));
       this.setNotapp_list((ArrayList)this.getFormHM().get("notapp_list"));
       this.setNotQ07_list((ArrayList)this.getFormHM().get("notQ07_list"));
       this.setNotQ09_list((ArrayList)this.getFormHM().get("notQ09_list"));
       this.setOverrule((String)this.getFormHM().get("overrule"));
       this.setS_columns((String)this.getFormHM().get("s_columns"));
       this.setS_strsql((String)this.getFormHM().get("s_strsql"));
       this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
       /**个人归档处理**/
       this.setPo_sqlstr((String)this.getFormHM().get("po_sqlstr"));
	   this.setPo_wherestr((String)this.getFormHM().get("po_wherestr"));
	   this.setPo_column((String)this.getFormHM().get("po_column"));
	   this.setDestfld((String)this.getFormHM().get("destfld"));
	   this.setTemp_table((String)this.getFormHM().get("temp_table"));
	   this.setBytesid((String)this.getFormHM().get("bytesid"));
       this.setPigeonhole_flag((String)this.getFormHM().get("pigeonhole_flag")); 
       this.setFlag((String)this.getFormHM().get("flag"));
       this.setError_flag((String)this.getFormHM().get("error_flag"));
	   this.setError_message((String)this.getFormHM().get("error_message"));
	   this.setError_return((String)this.getFormHM().get("error_return"));
	   this.setError_stuts((String)this.getFormHM().get("error_stuts"));
	   this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
       this.setSelect_name((String)this.getFormHM().get("select_name"));
       this.setSelect_flag((String)this.getFormHM().get("select_flag"));
       this.setSelect_pre((String)this.getFormHM().get("select_pre"));
       this.setSp_result((String)this.getFormHM().get("sp_result"));
       this.setRe_url((String)this.getFormHM().get("re_url"));
       this.setSelectsturs((String)this.getFormHM().get("selectsturs"));
       this.setSelectResult((String)this.getFormHM().get("selectResult"));
       this.setSelectys((String)this.getFormHM().get("selectys"));
       this.setKqItem_hash((HashMap)this.getFormHM().get("kqItem_hash"));
       this.setUplevel((String) this.getFormHM().get("uplevel"));
       this.setSelect_type((String)this.getFormHM().get("select_type"));
	}
	@Override
    public void inPutTransHM(){
		this.getFormHM().put("userbase",userbase);		
		this.getFormHM().put("code",code);
		this.getFormHM().put("coursedate",coursedate);
		this.getFormHM().put("codesetid",codesetid);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("datelist",datelist);
		this.getFormHM().put("start_date",start_date);		
		this.getFormHM().put("end_date",end_date);
		this.getFormHM().put("one_vo",this.getOne_vo());
		if(this.getPagination()!=null)			
		   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("kq_dbase_list",kq_dbase_list); 
		this.getFormHM().put("overrule",this.getOverrule());
		/**个人归档处理**/
		this.getFormHM().put("destfld",this.getDestfld());
		this.getFormHM().put("temp_table",this.getTemp_table());
		this.getFormHM().put("bytesid",this.getBytesid());
		if(this.getPagination()!=null)        
		      this.getFormHM().put("list",this.getPagination().getAllList());
		this.getFormHM().put("setlist",this.getSetlist());
		this.getFormHM().put("error_stuts",this.getError_stuts());
		this.getFormHM().put("error_flag",this.getError_flag());
		this.getFormHM().put("error_return",this.getError_return());
		this.getFormHM().put("select_name",this.getSelect_name());
	    this.getFormHM().put("select_flag",this.getSelect_flag());
	    this.getFormHM().put("select_pre",this.getSelect_pre());
	    this.getFormHM().put("sp_result",this.getSp_result());
	    this.getFormHM().put("selectsturs", this.getSelectsturs());
	    this.getFormHM().put("selectResult", this.getSelectResult());
	    this.getFormHM().put("selectys", this.getSelectys());
	    this.getFormHM().put("select_type", this.getSelect_type());
    }
	public String getA0100() {
		return A0100;
	}
	public void setA0100(String a0100) {
		A0100 = a0100;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public ArrayList getCourselist() {
		return courselist;
	}
	public void setCourselist(ArrayList courselist) {
		this.courselist = courselist;
	}
	public String getDbase() {
		return dbase;
	}
	public void setDbase(String dbase) {
		this.dbase = dbase;
	}
	public String getDbcond() {
		return dbcond;
	}
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
	public ArrayList getFielditemlist() {
		return fielditemlist;
	}
	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}
	public String getOperatedate() {
		return operatedate;
	}
	public void setOperatedate(String operatedate) {
		this.operatedate = operatedate;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getStrwhere() {
		return strwhere;
	}
	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public String getOrgvali() {
		return orgvali;
	}
	public void setOrgvali(String orgvali) {
		this.orgvali = orgvali;
	}	
	public String getCoursedate() {
		return coursedate;
	}
	public void setCoursedate(String coursedate) {
		this.coursedate = coursedate;
	}
	public ArrayList getDatelist() {
		return datelist;
	}
	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}
	public String getKq_period() {
		return kq_period;
	}
	public void setKq_period(String kq_period) {
		this.kq_period = kq_period;
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
	public String getKq_duration() {
		return kq_duration;
	}
	public void setKq_duration(String kq_duration) {
		this.kq_duration = kq_duration;
	}
	public RecordVo getOne_vo() {
		return one_vo;
	}
	public void setOne_vo(RecordVo one_vo) {
		this.one_vo = one_vo;
	}
	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}
	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}
	public ArrayList getNotapp_list() {
		return notapp_list;
	}
	public void setNotapp_list(ArrayList notapp_list) {
		this.notapp_list = notapp_list;
	}
	public PaginationForm getNotAppListForm() {
		return notAppListForm;
	}
	public void setNotAppListForm(PaginationForm notAppListForm) {
		this.notAppListForm = notAppListForm;
	}
	public String getNotapptag() {
		return notapptag;
	}
	public void setNotapptag(String notapptag) {
		this.notapptag = notapptag;
	}
	public ArrayList getNotQ07_list() {
		return notQ07_list;
	}
	public void setNotQ07_list(ArrayList notQ07_list) {
		this.notQ07_list = notQ07_list;
	}
	public PaginationForm getNotQ07ListForm() {
		return notQ07ListForm;
	}
	public void setNotQ07ListForm(PaginationForm notQ07ListForm) {
		this.notQ07ListForm = notQ07ListForm;
	}
	public ArrayList getNotQ09_list() {
		return notQ09_list;
	}
	public void setNotQ09_list(ArrayList notQ09_list) {
		this.notQ09_list = notQ09_list;
	}
	public PaginationForm getNotQ09ListForm() {
		return notQ09ListForm;
	}
	public void setNotQ09ListForm(PaginationForm notQ09ListForm) {
		this.notQ09ListForm = notQ09ListForm;
	}
	public String getOverrule() {
		return overrule;
	}
	public void setOverrule(String overrule) {
		this.overrule = overrule;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getS_columns() {
		return s_columns;
	}
	public void setS_columns(String s_columns) {
		this.s_columns = s_columns;
	}
	public String getS_strsql() {
		return s_strsql;
	}
	public void setS_strsql(String s_strsql) {
		this.s_strsql = s_strsql;
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/kq/register/browse_registerdatar".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		   {
			  this.pigeonhole_flag="";
		   }
 	    if(this.getPagination()!=null)
 	    {
 	    	this.getPagination().unSelectedAll();
 	    }		
		super.reset(arg0, arg1);
	}	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/kq/register/browse_register".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.getFormHM().put("kq_dbase_list",null);
	        this.setSelectResult("");
	    	this.getFormHM().put("selectResult", "");
	    }
	    if("/kq/register/audit_register".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.setSelectResult("");
	    	this.getFormHM().put("selectResult", "");
	    	this.select_name = "";
	    	this.select_flag = "0";
	        this.getFormHM().clear();
	    }
	    if("/kq/register/daily_registerdata".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    if("/kq/register/search_registerdata".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.setSp_result("");
	        this.getFormHM().put("sp_result","");
	    }
	   if("/kq/register/pigeonhole/sing_pigeonhole".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	   if("/kq/register/audit_register".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.getFormHM().put("kq_dbase_list",null);
	        this.setKq_dbase_list(null);
	        this.getFormHM().put("state_flag", "0");
	        this.getFormHM().clear();
	    }
	   if("/kq/register/audit_registerdata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?	       
	        this.getFormHM().put("state_flag", "0");
	        this.getFormHM().clear();
	    }
	   if("/kq/register/audit_registerdata".equals(arg0.getPath())&&arg1.getParameter("br_saveover")!=null)
	    {
		   this.setOverrule("");
		   this.getFormHM().put("overrule", "");
	    }
	   if("/kq/register/audit_registerdata".equals(arg0.getPath())&&arg1.getParameter("b_approve")!=null)
	    {
		   this.getFormHM().put("re_url","/kq/register/audit_registerdata.do?b_query=link");
	    }
	   if("/kq/register/audit_registerdata".equals(arg0.getPath())&&arg1.getParameter("b_audit")!=null)
	    {
		   this.getFormHM().put("re_url","/kq/register/audit_registerdata.do?b_query=link");
	    }
	   if("/kq/register/audit_registerdata".equals(arg0.getPath())&&arg1.getParameter("b_overrule")!=null)
	    {
		   this.getFormHM().put("re_url","/kq/register/audit_registerdata.do?b_query=link");
	    }
	    return super.validate(arg0, arg1);
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getError_flag() {
		return error_flag;
	}
	public void setError_flag(String error_flag) {
		if(error_flag==null||error_flag.length()<=0)
			error_flag="0";
		this.error_flag = error_flag;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	public String getError_return() {
		return error_return;
	}
	public void setError_return(String error_return) {
		this.error_return = error_return;
	}
	public String getError_stuts() {
		return error_stuts;
	}
	public void setError_stuts(String error_stuts) {
		this.error_stuts = error_stuts;
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
	public String getRe_url() {
		return re_url;
	}
	public void setRe_url(String re_url) {
		this.re_url = re_url;
	}
	public String getSelectys() {
		return selectys;
	}
	public void setSelectys(String selectys) {
		this.selectys = selectys;
	}
	public HashMap getKqItem_hash() {
		return kqItem_hash;
	}
	public void setKqItem_hash(HashMap kqItem_hash) {
		this.kqItem_hash = kqItem_hash;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getSelect_type() {
		return select_type;
	}
	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}
	
}
