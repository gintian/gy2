package com.hjsj.hrms.actionform.kq.register;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class AmbiquityFrom extends FrameForm{
	private ArrayList courselist=new ArrayList();
	private String treeCode;//树形菜单，在HtmlMenu中
	private String sqlstr;
    private String strwhere;
    private String orderby;
    private String columns;
    private String stat_type;
    private String stat_start;
    private String stat_end;
    private String coursedate;
    private String code;
    private String kind;
    private String dbcond;
    private String userbase;
    private String kq_period;
    private ArrayList kq_dbase_list= new ArrayList();
    private ArrayList fielditemlist = new ArrayList();
    private String duration;
    private String error_message;
    private String error_flag;
    private String error_return;
    private String count_duration;
    private String relatTableid;//高级花名册对应的单表名称
    private String condition;//高级花名册打印的条件
    private String returnURL;//返回的连接
    private ArrayList kq_list=new ArrayList();//人员库
    private String select_name;//筛选名字
    private String select_flag;//筛选表示
    private String select_type="0";
    private String select_pre;
    private String flag;  //0:日明细;1:月汇总；2：不定期
    private String uplevel;//部门层级
    private String returnvalue="1";
    private String kq_end;
    
    
	public String getKq_end() {
		return kq_end;
	}
	public void setKq_end(String kqEnd) {
		kq_end = kqEnd;
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
	public String getCount_duration() {
		return count_duration;
	}
	public void setCount_duration(String count_duration) {
		this.count_duration = count_duration;
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getCoursedate() {
		return coursedate;
	}
	public void setCoursedate(String coursedate) {
		this.coursedate = coursedate;
	}
	public String getStat_end() {
		return stat_end;
	}
	public void setStat_end(String stat_end) {
		this.stat_end = stat_end;
	}
	public String getStat_start() {
		return stat_start;
	}
	public void setStat_start(String stat_start) {
		this.stat_start = stat_start;
	}
	public String getStat_type() {
		return stat_type;
	}
	public void setStat_type(String stat_type) {
		this.stat_type = stat_type;
	}
	@Override
    public void outPutFormHM()
	{
		   this.setTreeCode((String)this.getFormHM().get("treeCode"));	 
		   this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		   this.setStrwhere((String)this.getFormHM().get("strwhere"));
		   this.setOrderby((String)this.getFormHM().get("orderby"));
		   this.setColumns((String)this.getFormHM().get("columns"));
		   this.setCourselist((ArrayList)this.getFormHM().get("courselist"));
		   this.setStat_start((String)this.getFormHM().get("stat_start"));
		   this.setStat_end((String)this.getFormHM().get("stat_end"));
		   this.setStat_type((String)this.getFormHM().get("stat_type"));
		   this.setCoursedate((String)this.getFormHM().get("coursedate"));
		   this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist")); 
		   this.setDbcond((String)this.getFormHM().get("dbcond"));	 	   
	 	   this.setUserbase((String)this.getFormHM().get("userbase"));
	 	   this.setCode((String)this.getFormHM().get("code"));
	 	   this.setKind((String)this.getFormHM().get("kind"));
	 	   this.setKq_dbase_list((ArrayList)this.getFormHM().get("kq_dbase_list"));
	 	   this.setDuration((String)this.getFormHM().get("duration"));
	 	   this.setKq_period((String)this.getFormHM().get("kq_period"));
	 	   this.setError_flag((String)this.getFormHM().get("error_flag"));
		   this.setError_message((String)this.getFormHM().get("error_message"));
		   this.setError_return((String)this.getFormHM().get("error_return"));
		   this.setCount_duration((String)this.getFormHM().get("count_duration"));
		   this.setCondition((String)this.getFormHM().get("condition"));
		   this.setReturnURL((String)this.getFormHM().get("returnURL"));
		   this.setRelatTableid((String)this.getFormHM().get("relatTableid"));
		   this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
	       this.setSelect_name((String)this.getFormHM().get("select_name"));
	       this.setSelect_flag((String)this.getFormHM().get("select_flag"));
	       this.setSelect_pre((String)this.getFormHM().get("select_pre"));
	       this.setFlag((String)this.getFormHM().get("flag"));
	       this.setUplevel((String)this.getFormHM().get("uplevel"));
	       this.setSelect_type((String)this.getFormHM().get("select_type"));
	       this.setKq_end((String)this.getFormHM().get("kq_end"));
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("stat_type",this.getStat_type());
		this.getFormHM().put("stat_start",this.getStat_start());
		this.getFormHM().put("stat_end",this.getStat_end());
		this.getFormHM().put("coursedate",this.getCoursedate());
		this.getFormHM().put("userbase",this.getUserbase());		
		this.getFormHM().put("code",this.getCode());		
		this.getFormHM().put("kind",this.getKind());
		this.getFormHM().put("kq_dbase_list",this.getKq_dbase_list()); 
		this.getFormHM().put("duration",this.getDuration());
		this.getFormHM().put("count_duration",this.getCount_duration());
		this.getFormHM().put("relatTableid",this.getRelatTableid());
		this.getFormHM().put("select_name",this.getSelect_name());
	    this.getFormHM().put("select_flag",this.getSelect_flag());
	    this.getFormHM().put("select_pre",this.getSelect_pre());
	    this.getFormHM().put("select_type", this.getSelect_type());
	}
	
	public ArrayList getCourselist() {
		return courselist;
	}
	public void setCourselist(ArrayList courselist) {
		this.courselist = courselist;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}
	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getKq_period() {
		return kq_period;
	}
	public void setKq_period(String kq_period) {
		this.kq_period = kq_period;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
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
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//不定期 之首页
	    if("/kq/register/ambiquity/select_ambiquitydata".equals(arg0.getPath())){
	    	if(this.getPagination()!=null)
	            this.getPagination().firstPage();//?
	    }
	    return super.validate(arg0, arg1);
	}
}
