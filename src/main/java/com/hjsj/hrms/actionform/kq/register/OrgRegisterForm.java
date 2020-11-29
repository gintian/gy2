package com.hjsj.hrms.actionform.kq.register;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;


public class OrgRegisterForm extends FrameForm{

	private String treeCode;//树形菜单，在HtmlMenu中
	  private String userbase; //**应用库表前缀*/	
	  private String code;//连接级别
	  private String coursedate;
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
	  private ArrayList datelist = new ArrayList();
	  private ArrayList kq_dbase_list= new ArrayList();
	  private String registerdate;
	  private String workcalendar;
	  private ArrayList vo_datelist=new ArrayList();
	  private String selectys;//转换小时 1=默认；2=HH:MM
	  /** 考勤规则**/
	  private HashMap kqItem_hash=new HashMap();
	  // 部门层级
	  private String uplevel;
	  // 高级花名册返回页面的url
	  private String returnURL;
	  // 高级花名册 查询条件
	  private String condition;
	  // 高级花名册 nprint
	  private String nprint;
	  
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getNprint() {
		return nprint;
	}
	public void setNprint(String nprint) {
		this.nprint = nprint;
	}
	public String getRegisterdate() {
		return registerdate;
	}
	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}
	public ArrayList getVo_datelist() {
		return vo_datelist;
	}
	public void setVo_datelist(ArrayList vo_datelist) {
		this.vo_datelist = vo_datelist;
	}
	public String getWorkcalendar() {
		return workcalendar;
	}
	public void setWorkcalendar(String workcalendar) {
		this.workcalendar = workcalendar;
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
     
     this.setKq_dbase_list((ArrayList)this.getFormHM().get("kq_dbase_list"));
     this.setRegisterdate((String)this.getFormHM().get("registerdate"));
     this.setWorkcalendar((String)this.getFormHM().get("workcalendar"));
     this.setVo_datelist((ArrayList)this.getFormHM().get("vo_datelist"));
     this.setSelectys((String)this.getFormHM().get("selectys"));
     this.setKqItem_hash((HashMap)this.getFormHM().get("kqItem_hash"));
     this.setUplevel((String) this.getFormHM().get("uplevel"));
     
     // 高级花名册的参数
     this.setNprint((String) this.getFormHM().get("nprint"));
     this.setCondition((String) this.getFormHM().get("condition"));
     this.setReturnURL((String) this.getFormHM().get("returnURL"));
  }
	@Override
    public void inPutTransHM(){
		this.getFormHM().put("userbase",userbase);		
		this.getFormHM().put("code",code);		
		this.getFormHM().put("codesetid",codesetid);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("datelist",datelist);
		this.getFormHM().put("start_date",start_date);		
		this.getFormHM().put("end_date",end_date);		
		this.getFormHM().put("kq_dbase_list",kq_dbase_list); 
		this.getFormHM().put("coursedate",coursedate);
		this.getFormHM().put("courselist",courselist);
		this.getFormHM().put("registerdate",this.getRegisterdate());
		this.getFormHM().put("vo_datelist",this.getVo_datelist());
		this.getFormHM().put("selectys", this.getSelectys());
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
	
	public ArrayList getFielditemlist() {
		return fielditemlist;
	}
	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
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
	
	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}
	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}
	public String getCoursedate() {
		return coursedate;
	}
	public void setCoursedate(String coursedate) {
		this.coursedate = coursedate;
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
}
