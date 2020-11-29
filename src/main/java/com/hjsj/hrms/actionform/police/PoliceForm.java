package com.hjsj.hrms.actionform.police;


import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:PoliceForm</p>
 * <p>Description:设置周期</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-2-7</p>
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class PoliceForm extends FrameForm {
	
	/**狱情动态的周期，0为年，1为月，2为周*/
	//
	private String news;
	
	/**部门工作任务书的周期，0为年，1为月，2为周*/
	private String orgtask;
	
	/**个人工作任务书的周期，0为年，1为月，2为周*/
	private String persontask;
	
	/**周期设置,0为年，1为月，2为周*/
	private String cycle;
	
	/**年度*/
	private String taskyear;
	
	/**年度列表*/
	private ArrayList yearlist = new ArrayList();
	
	/**月*/
	private String taskmonth;
	
	/**月列表*/
	private ArrayList monthlist = new ArrayList();
	
	/**周*/
	private String taskweek;
	
	/**周列表*/
	private ArrayList weeklist = new ArrayList();
	
	/**机构名称*/
	private String orgname;
	
	/**显示部门层次*/
	private String uplevel;
	
	/**人员*/
	private String a0100;
	
	/**人员库*/
	private String userbase;
	/**sql*/
	private String sqlstr;
	
	/**查询的列*/
	private String column;
	
	/**顺序*/
	private String order_by;
	
	/**机构代码*/
	private String a_code;
	
	/**组织机构数代码*/
	private String treeCode;

	/**狱情动态选中的单位岗位代码*/
	private String policeConstant;
	private String flag="";//1:分监区工作流程;2:独立工作环节;3:专项教育活动
	private String type="";//police:监狱警察平台
	private String issave="";
	private String returnvalue="";
	
	private String task_cly_year="";
	private String task_cly_moth="";
	private String task_cly_quar="";
	
	private String fromFlag="";  
	
	
	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getIssave() {
		return issave;
	}

	public void setIssave(String issave) {
		this.issave = issave;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	/**机构标志*/
	private String b0110; 
	
	/**文件标题*/
	private String filetitle;
	
	/**文件*/
	private FormFile mediafile;
	
	/**模块名称*/
	private String cyclename;
	
	/**姓名*/
	private String username;
	private ArrayList userbaselist = new ArrayList();
	
	public ArrayList getUserbaselist() {
		return userbaselist;
	}

	public void setUserbaselist(ArrayList userbaselist) {
		this.userbaselist = userbaselist;
	}

	public String getFiletitle() {
		return filetitle;
	}

	public void setFiletitle(String filetitle) {
		this.filetitle = filetitle;
	}


	public FormFile getMediafile() {
		return mediafile;
	}

	public void setMediafile(FormFile mediafile) {
		this.mediafile = mediafile;
	}


	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public String getTaskyear() {
		return taskyear;
	}

	public void setTaskyear(String taskyear) {
		this.taskyear = taskyear;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public PoliceForm() {
		super();
	}
    
	@Override
    public void outPutFormHM() {
		this.setFromFlag((String)this.getFormHM().get("fromFlag"));
		
		this.setNews((String) this.getFormHM().get("yqdt"));
		this.setOrgtask((String) this.getFormHM().get("dept"));
		this.setPersontask((String) this.getFormHM().get("employ"));
		this.setCycle((String) this.getFormHM().get("cycle"));
		this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		this.setTaskyear((String)this.getFormHM().get("taskyear"));
		this.setMonthlist((ArrayList) this.getFormHM().get("monthlist"));
		this.setTaskmonth((String) this.getFormHM().get("taskmonth"));
		this.setWeeklist((ArrayList)this.getFormHM().get("weeklist"));
		this.setTaskweek((String) this.getFormHM().get("taskweek"));
		this.setUplevel((String) this.getFormHM().get("uplevel"));
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrder_by((String) this.getFormHM().get("order_by"));
		this.setA_code((String) this.getFormHM().get("a_code"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setType((String)this.getFormHM().get("type"));
        this.setIssave((String)this.getFormHM().get("issave"));
		this.setB0110((String) this.getFormHM().get("b0110"));
		this.setOrgname((String) this.getFormHM().get("orgname"));
		this.setCyclename((String) this.getFormHM().get("cyclename"));
		this.setA0100((String) this.getFormHM().get("a0100"));
		this.setUserbase((String) this.getFormHM().get("userbase"));
		this.setUserbaselist((ArrayList) this.getFormHM().get("userbaselist"));
		this.setUsername((String) this.getFormHM().get("username"));
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setPoliceConstant((String) this.getFormHM().get("policeConstant"));
        //this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
		this.setTask_cly_moth((String)this.getFormHM().get("task_cly_moth"));
		this.setTask_cly_year((String)this.getFormHM().get("task_cly_year"));
		this.setTask_cly_quar((String)this.getFormHM().get("task_cly_quar"));
				
	}

	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("news", this.getNews());
		this.getFormHM().put("orgtask", this.getOrgtask());
		this.getFormHM().put("persontask", this.getPersontask());
		this.getFormHM().put("taskyear",this.getTaskyear());
		this.getFormHM().put("taskmonth",this.getTaskmonth());
		this.getFormHM().put("taskweek",this.getTaskweek());
		this.getFormHM().put("orgname", this.getOrgname());
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("b0100", this.getB0110());
		this.getFormHM().put("filetitle",this.getFiletitle());
		this.getFormHM().put("mediafile",this.getMediafile());
		this.getFormHM().put("cyclename", this.getCyclename());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("userbase", this.getUserbase());
		this.getFormHM().put("username", this.getUsername());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("policeConstant", this.getPoliceConstant());
		this.getFormHM().put("task_cly_moth", this.getTask_cly_moth());
		this.getFormHM().put("task_cly_year", this.getTask_cly_year());
		this.getFormHM().put("task_cly_quar", this.getTask_cly_quar());
	}
    
	

	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
		 if("/pos/police/jqdt".equals(arg0.getPath()) && arg1.getParameter("tofirst")!=null){
	           if (this.getPagination() != null) {
	        	   this.getPagination().firstPage();
	           }
	     }
	     if("/pos/police/person".equals(arg0.getPath()) && arg1.getParameter("tofirst")!=null && "yes".equals(arg1.getParameter("tofirst"))){
	           if (this.getPagination() != null) {
	        	   this.getPagination().firstPage();
	           }	           
	     }
	     if("/pos/police/person".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null){
	           if (this.getPagination() != null) {
	        	   this.getPagination().firstPage();
	           }
	           this.setUserbase("");
	           this.setA0100("");
	           this.getFormHM().put("userbase", "");
	           this.getFormHM().put("a0100", "");
	           if(arg1.getParameter("returnvalue")==null)
	           {
	        	   this.getFormHM().put("returnvalue", "");
	        	   this.setReturnvalue("");
	           }else
	           {
	        	   this.setReturnvalue(arg1.getParameter("returnvalue"));
	           }
	     }
	     if("/pos/police/team".equals(arg0.getPath()) && arg1.getParameter("tofirst")!=null && "yes".equals(arg1.getParameter("tofirst"))){
	           if (this.getPagination() != null) {
	        	   this.getPagination().firstPage();
	           }
	     }
	     if("/pos/police/task_file".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null){
	    	   if(arg1.getParameter("returnvalue")==null)
	           {
	        	   this.getFormHM().put("returnvalue", "");
	        	   this.setReturnvalue("");
	           }else
	           {
	        	   this.setReturnvalue(arg1.getParameter("returnvalue"));
	           }
	     }
	     if("/pos/police/jqdt_tree".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null){
	           if (this.getPagination() != null) {
	        	   this.getPagination().firstPage();
	           }
	           this.setUserbase("");
	           this.setA0100("");
	           this.getFormHM().put("userbase", "");
	           this.getFormHM().put("a0100", "");
	           if(arg1.getParameter("returnvalue")==null)
	           {
	        	   this.getFormHM().put("returnvalue", "");
	        	   this.setReturnvalue("");
	           }else
	           {
	        	   this.setReturnvalue(arg1.getParameter("returnvalue"));
	           }
	           
	     }   
	 
       return super.validate(arg0, arg1);
	 }

	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

	public String getOrgtask() {
		return orgtask;
	}

	public void setOrgtask(String orgtask) {
		this.orgtask = orgtask;
	}

	public String getPersontask() {
		return persontask;
	}

	public void setPersontask(String persontask) {
		this.persontask = persontask;
	}

	public String getTaskmonth() {
		return taskmonth;
	}

	public void setTaskmonth(String taskmonth) {
		this.taskmonth = taskmonth;
	}

	public ArrayList getMonthlist() {
		return monthlist;
	}

	public void setMonthlist(ArrayList monthlist) {
		this.monthlist = monthlist;
	}

	public String getTaskweek() {
		return taskweek;
	}

	public void setTaskweek(String taskweek) {
		this.taskweek = taskweek;
	}

	public ArrayList getWeeklist() {
		return weeklist;
	}

	public void setWeeklist(ArrayList weeklist) {
		this.weeklist = weeklist;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getCyclename() {
		return cyclename;
	}

	public void setCyclename(String cyclename) {
		this.cyclename = cyclename;
	}

	public String getUserbase() {
		return userbase;
	}

	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getPoliceConstant() {
		return policeConstant;
	}

	public void setPoliceConstant(String policeConstant) {
		this.policeConstant = policeConstant;
	}

	public String getTask_cly_year() {
		return task_cly_year;
	}

	public void setTask_cly_year(String task_cly_year) {
		this.task_cly_year = task_cly_year;
	}

	public String getTask_cly_moth() {
		return task_cly_moth;
	}

	public void setTask_cly_moth(String task_cly_moth) {
		this.task_cly_moth = task_cly_moth;
	}

	public String getTask_cly_quar() {
		return task_cly_quar;
	}

	public void setTask_cly_quar(String task_cly_quar) {
		this.task_cly_quar = task_cly_quar;
	}

	public String getFromFlag() {
		return fromFlag;
	}

	public void setFromFlag(String fromFlag) {
		this.fromFlag = fromFlag;
	}
	
	
	
}
