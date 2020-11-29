package com.hjsj.hrms.actionform.kq.team;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqScheduleForm extends FrameForm {
	/**用户组或用户树代码*/
	
	/**选择类型check radio世间0、1、2*/
	private String selecttype="0";
	/**加载人0 、1*/
	private String flag="1";
	/**加载用户库
	 * =0 权限范围内的库
	 * =1 权限范围内的登录用户库
	 * */
	private String dbtype="0";
	private String treeCode;
	private String treeGroupCode;
	private String a_code;
	private ArrayList sessionlist=new ArrayList();
	private String session_data;
	private String table_html;
	private String nbase;
	private String group_id;
	private String org_code;
	private String kq_type;
	private String state;
	private String curpage;
	private String code_mess;
	private ArrayList kq_list=new ArrayList();//人员库
    private String select_name;//筛选名字
    private String select_flag;//筛选表示
    private String select_pre;
    private String sql;
    private String columns;
    private String start_date;
    private String end_date;
    private ArrayList nbase_list=new ArrayList();
    private FormFile file;
    private ArrayList session_y_list=new ArrayList();
    private ArrayList duration_list=new ArrayList();
    private String session_y;
    private String session_m;
    private String session_y_old;
    private String dbase;
    private String a0100;
    private String rootdesc;    
    private String action;
    private String target;
    private String treetype;
    private String grnbase;
    private String hidden_name;
    private String returnvalue="1";
	public String getHidden_name() {
		return hidden_name;
	}
	public void setHidden_name(String hidden_name) {
		this.hidden_name = hidden_name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getTreetype() {
		return treetype;
	}
	public void setTreetype(String treetype) {
		this.treetype = treetype;
	}
	public String getRootdesc() {
		return rootdesc;
	}
	public void setRootdesc(String rootdesc) {
		this.rootdesc = rootdesc;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getDbase() {
		return dbase;
	}
	public void setDbase(String dbase) {
		this.dbase = dbase;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public String getCode_mess() {
		return code_mess;
	}
	public void setCode_mess(String code_mess) {
		this.code_mess = code_mess;
	}
	public String getCurpage() {
		return curpage;
	}
	public void setCurpage(String curpage) {
		this.curpage = curpage;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getKq_type() {
		return kq_type;
	}
	public void setKq_type(String kq_type) {
		this.kq_type = kq_type;
	}
	public String getOrg_code() {
		return org_code;
	}
	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}
	public String getTreeGroupCode() {
		return treeGroupCode;
	}
	public void setTreeGroupCode(String treeGroupCode) {
		this.treeGroupCode = treeGroupCode;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	@Override
    public void outPutFormHM()
	{
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setSession_data((String)this.getFormHM().get("session_data"));
		this.setSessionlist((ArrayList)this.getFormHM().get("sessionlist"));
		this.setTable_html((String)this.getFormHM().get("table_html"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setOrg_code((String)this.getFormHM().get("org_code"));
		this.setKq_type((String)this.getFormHM().get("kq_type"));
		this.setTreeGroupCode((String)this.getFormHM().get("treeGroupCode"));
		this.setGroup_id((String)this.getFormHM().get("group_id"));
		this.setState((String)this.getFormHM().get("state"));
		this.setCurpage((String)this.getFormHM().get("curpage"));
		this.setCode_mess((String)this.getFormHM().get("code_mess"));
		this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
	    this.setSelect_name((String)this.getFormHM().get("select_name"));
	    this.setSelect_flag((String)this.getFormHM().get("select_flag"));
	    this.setSelect_pre((String)this.getFormHM().get("select_pre"));
	    this.setStart_date((String)this.getFormHM().get("start_date"));
	    this.setEnd_date((String)this.getFormHM().get("end_date"));
	    this.setSql((String)this.getFormHM().get("sql"));
	    this.setColumns((String)this.getFormHM().get("columns"));	  
	    this.setNbase_list((ArrayList)this.getFormHM().get("nbase_list"));
	    this.setSession_y_list((ArrayList)this.getFormHM().get("session_y_list"));
	    this.setDuration_list((ArrayList)this.getFormHM().get("duration_list"));
	    this.setSession_y((String)this.getFormHM().get("session_y"));
	    this.setSession_m((String)this.getFormHM().get("session_m"));
	    this.setSession_y_old((String)this.getFormHM().get("session_y_old"));
	    this.setA0100((String)this.getFormHM().get("a0100"));
	    this.setDbase((String)this.getFormHM().get("dbase"));
	    this.setRootdesc((String)this.getFormHM().get("rootdesc"));
	    this.setAction((String)this.getFormHM().get("action"));
	    this.setTarget((String)this.getFormHM().get("target"));
	    this.setTreetype((String)this.getFormHM().get("treetype"));
	    this.setGrnbase((String)this.getFormHM().get("grnbase"));
	    this.setHidden_name((String)this.getFormHM().get("hidden_name"));
    }
    @Override
    public void inPutTransHM()
    {
	    this.getFormHM().put("session_data",this.getSession_data());
	    this.getFormHM().put("a_code",this.getA_code());
	    this.getFormHM().put("nabse",this.getNbase());
	    this.getFormHM().put("group_id",this.getGroup_id());
	    this.getFormHM().put("state",this.getState());
	    this.getFormHM().put("curpage",this.getCurpage());
	    this.getFormHM().put("select_name",this.getSelect_name());
	    this.getFormHM().put("select_flag",this.getSelect_flag());
	    this.getFormHM().put("select_pre",this.getSelect_pre());
	    this.getFormHM().put("start_date",this.getStart_date());
	    this.getFormHM().put("end_date",this.getEnd_date());
	    if(this.getPagination()!=null)			
			 this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
	    this.getFormHM().put("file", file);
	    this.getFormHM().put("session_y", this.getSession_y());
	    this.getFormHM().put("session_m", this.getSession_m());
	    this.getFormHM().put("session_y_old", this.getSession_y_old());
	    this.getFormHM().put("dbase", this.getDbase());
	    this.getFormHM().put("a0100", this.getA0100());
	    this.getFormHM().put("grnbase",this.getGrnbase());
    }
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
		 if("/kq/team/schedule/search_array".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
       {
     	   this.setA_code("");
     	   this.setSession_data("");
     	   this.setSessionlist(null);     	
     	  this.getFormHM().put("select_pre","");
     	  this.setSelect_name("");
     	  this.getFormHM().put("select_name","");
       } 		
	   if("/kq/team/schedule/search_array".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	   {
		   if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		        this.getFormHM().clear();    	
		        this.getFormHM().put("select_pre","");
		        this.getFormHM().put("select_name","");
		        this.setSelect_name("");
		        this.getFormHM().put("session_data", "");
				this.setSession_data("");
				this.getFormHM().put("a_code", "");
				this.setA_code("");
	   } 		
	   if("/kq/team/schedule/search_noarray".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	   {
		   if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		   this.setA_code("");
		   this.setSelect_pre("");
		   this.getFormHM().put("select_pre","");
		   this.getFormHM().put("a_code","");
		   this.getFormHM().clear();    	
		   this.getFormHM().put("select_name","");
		   this.setSelect_name("");
		   this.setStart_date("");
		   this.setEnd_date("");
		   this.getFormHM().put("start_date","");
		   this.getFormHM().put("end_date","");		   
	   } 
	   if("/kq/team/historical/search_array".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
       {
     	   this.setA_code("");
     	   this.setSession_data("");
     	   this.setSessionlist(null);     	
     	  this.getFormHM().put("select_pre","");
     	  this.setSelect_name("");
     	  this.getFormHM().put("select_name","");
       } 		
	   if("/kq/team/historical/search_array".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	   {
		   if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		   this.getFormHM().clear();    	
		   this.getFormHM().put("select_pre","");
		   this.getFormHM().put("select_name","");
		   this.setSelect_name("");
		   this.setA_code("");
		   this.setSession_data("");
	   }
       return super.validate(arg0, arg1);
	 }
	public String getSession_data() {
		return session_data;
	}
	public void setSession_data(String session_data) {
		this.session_data = session_data;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public ArrayList getSessionlist() {
		return sessionlist;
	}
	public void setSessionlist(ArrayList sessionlist) {
		this.sessionlist = sessionlist;
	}	
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getSelecttype() {
		return selecttype;
	}
	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getTable_html() {
		return table_html;
	}
	public void setTable_html(String table_html) {
		this.table_html = table_html;
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
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public ArrayList getNbase_list() {
		return nbase_list;
	}
	public void setNbase_list(ArrayList nbase_list) {
		this.nbase_list = nbase_list;
	}
	public ArrayList getDuration_list() {
		return duration_list;
	}
	public void setDuration_list(ArrayList duration_list) {
		this.duration_list = duration_list;
	}
	public String getSession_m() {
		return session_m;
	}
	public void setSession_m(String session_m) {
		this.session_m = session_m;
	}
	public String getSession_y() {
		return session_y;
	}
	public void setSession_y(String session_y) {
		this.session_y = session_y;
	}
	public ArrayList getSession_y_list() {
		return session_y_list;
	}
	public void setSession_y_list(ArrayList session_y_list) {
		this.session_y_list = session_y_list;
	}
	public String getSession_y_old() {
		return session_y_old;
	}
	public void setSession_y_old(String session_y_old) {
		this.session_y_old = session_y_old;
	}
	public String getGrnbase() {
		return grnbase;
	}
	public void setGrnbase(String grnbase) {
		this.grnbase = grnbase;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	
}
