package com.hjsj.hrms.actionform.performance.workdiary;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkdiaryForm extends FrameForm {
	//2016/1/27 wangjl我的日志查询条件
	private String searchterm;
	private String searchflag;
	public String getSearchterm() {
		return searchterm;
	}

	public void setSearchterm(String searchterm) {
		this.searchterm = searchterm;
	}

	public String getSearchflag() {
		return searchflag;
	}

	public void setSearchflag(String searchflag) {
		this.searchflag = searchflag;
	}

	private RecordVo p01Vo;
	private RecordVo p01Vo_myself; // 我的日志进入编辑页面和报批保存时使用 lium
	private PaginationForm paginationForm=new PaginationForm();//郭峰增加  不用分页标签了
	ArrayList datalist = new ArrayList();//郭峰增加  不用分页标签了
//	分页使用字段
	private String sql;
	private String where;
	private String column;
	private String orderby;
	private PaginationForm pageListForm = new PaginationForm();
//	查询开始时间
	private String startime; 
//	查询结束时间
	private String endtime; 
	private String selradio; 
	private String selprojectdesc; 
//	查询的时间范围
	private String timefield; 
//	查询项目名称
	private String projectname; 
//	分页显示字段
	private ArrayList fieldlist=new ArrayList();
//  控制字段
	/**
	 * =0增加我的日至
	 * =1修改我的日至
	 */
	private String flag;
	private String fileFlag="0";
	public String getFileFlag() {
		return fileFlag;
	}

	public void setFileFlag(String fileFlag) {
		this.fileFlag = fileFlag;
	}

	private ArrayList ymdlist = new ArrayList();
	private String ymd;
	/**=0 当前月
	 * =1 当前周
	 * =2 当前日
	 *  (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private String appflag;
	private String dis;
	/**用来控制叶面各个字段的disabled
	 *  (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	
	private String yearnum;
	private ArrayList yearlist = new ArrayList();
	private String monthnum;
	private ArrayList monthlist = new ArrayList();
	private String tablestr;
	private String appstate;//审批状态
	private ArrayList statelist = new ArrayList();
	private String state;
	private String weeknum;
	private String[] weeknum_arr;
	private ArrayList weeklist = new ArrayList();
	private String name;
	private String timeflag="";
	private FormFile picturefile; //上传文件
	private String filename = "";
	private String returnch = "";
	private String perPlanTable = "";//目标卡
	private String a_code = "";
	private String personid = "";
	private String personname = "";
	private ArrayList columlist = new ArrayList();//按查询方式的指标集 //xuj 2009-11-3 员工日志查询增加按日志内容模糊查询的功能
	private String colum;//按查询方式的指标
	private String name1;//按查询方式的指标对应的值
	private String namevalue;//针对时间类型指标：终止时间，针对代码类型：代码值
	private ArrayList predbnamelist = new ArrayList();//人员库列表

	private String curr_user;//日志优化 添加上级
	private String home;//返回首页标示
	private String csflag;//是否为抄送人 状态
	

	// 填写的日志能否报批 1为能报批,0为不能报批，默认为1
	private String checkApp;
	// 不能报批的理由,默认为ok
	private String checkResion;
	// 是否有附件,1为有，0为没有
	private String existFile;
	
	//外部调用
	private String a0100;
	private String start_date;
	private String end_date;
	private String pendingCode = ""; // 
	private String doneFlag = "0"; // 
	private String times="";//timestr
	private String zxgflag = "";
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setDatalist((ArrayList)this.getFormHM().get("datalist"));
		this.getPaginationForm().setList((ArrayList)this.getFormHM().get("datalist"));//郭峰
		this.setFieldlist((ArrayList) hm.get("fieldlist"));
		this.setP01Vo((RecordVo) hm.get("p01Vo"));
		this.p01Vo_myself = (RecordVo) hm.get("p01Vo_myself");
		this.setFlag((String) hm.get("flag"));
		this.setColumn((String)hm.get("column"));
		this.setOrderby((String) hm.get("orderby"));
		this.setSql((String) hm.get("sql"));
		this.setWhere((String) hm.get("where"));
		this.setStartime((String)hm.get("startime"));
		this.setEndtime((String)hm.get("endtime"));
		this.setYmd((String) hm.get("ymd"));
		this.setAppflag((String)hm.get("appflag"));
		this.setDis((String)hm.get("dis"));
		this.setYearnum((String)hm.get("yearnum"));
		this.setYearlist((ArrayList)hm.get("yearlist"));
		this.setMonthnum((String)hm.get("monthnum"));
		this.setMonthlist((ArrayList)hm.get("monthlist"));
		this.setTablestr((String)hm.get("tablestr"));
		this.setState((String)hm.get("state"));
		this.setWeeknum((String)hm.get("weeknum"));
		this.setName((String)hm.get("name"));
		this.setAppstate((String)hm.get("appstate"));
		this.setWeeknum_arr((String[])hm.get("weeknum_arr"));
		this.setYmdlist((ArrayList)hm.get("ymdlist"));
		this.setStatelist((ArrayList)hm.get("statelist"));
		this.setWeeklist((ArrayList)hm.get("weeklist"));
		this.setTimeflag((String)hm.get("timeflag"));
		this.setReturnch((String)hm.get("returnch"));
		this.setPerPlanTable((String)hm.get("perPlanTable"));
		this.setA_code((String)hm.get("a_code"));
		this.setPersonid((String)hm.get("personid"));
		this.setPersonname((String)hm.get("personname"));
		this.setFilename((String)hm.get("filename"));
		this.setColumlist((ArrayList)hm.get("columlist"));
		//this.setColum((String)hm.get("colum"));
		this.setName1((String)hm.get("name1"));
		this.setNamevalue((String)hm.get("namevalue"));
		this.setPredbnamelist((ArrayList)hm.get("predbnamelist"));
		this.setCurr_user((String)hm.get("curr_user"));
		this.setCsflag((String)hm.get("csflag"));
		this.setStart_date((String)hm.get("start_date"));
		this.setEnd_date((String)hm.get("end_date"));
		this.setPendingCode((String)hm.get("pendingCode"));
		this.setDoneFlag((String)hm.get("doneFlag"));
		this.setTimes((String)hm.get("times"));
		this.setZxgflag((String) hm.get("zxgflag"));
		// 是否有附件
		this.setExistFile((String) this.getFormHM().get("existFile"));
		this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));
		
		this.setFileFlag((String) hm.get("fileFlag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("searchterm",this.getSearchterm());//2016/1/27 wangjl我的日志查询条件
		hm.put("doneFlag",this.getDoneFlag());
		hm.put("ymd",this.getYmd());
		hm.put("p01Vo",this.getP01Vo());
		hm.put("p01Vo_myself",this.p01Vo_myself);
		hm.put("state",this.getState());
		hm.put("weeknum",this.getWeeknum());
		hm.put("monthnum",this.getMonthnum());
		hm.put("yearnum",this.getYearnum());
		hm.put("name",this.getName());
		hm.put("startime",this.getStartime());
		hm.put("appstate",this.getAppstate());
		hm.put("endtime",this.getEndtime());
		hm.put("timeflag",this.getTimeflag());
		hm.put("picturefile", this.getPicturefile());
		hm.put("filename", this.getFilename());
		hm.put("personid", this.getPersonid());
		if(this.getPagination()!=null){//2013.11.28 pjf
			hm.put("seldiary",(ArrayList)this.getPagination().getSelectedList());
		}
		hm.put("seldiary2",this.getPaginationForm().getSelectedList());//适用于员工日志中批准的时候，得到前台的选中记录
		
		hm.put("colum", this.getColum());
	    hm.put("name1", this.getName1());
		hm.put("namevalue", this.getNamevalue());
		hm.put("curr_user", this.getCurr_user());
		hm.put("a0100", this.getA0100());
		hm.put("start_date", this.getStart_date());
		hm.put("end_date", this.getEnd_date());
		hm.put("zxgflag", this.getZxgflag());
		hm.put("fileFlag", this.getFileFlag());
		hm.put("flag", this.getFlag());
		this.getFormHM().put("pagerows", this.getPagerows()==0?"10":(this.getPagerows()+""));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		try{
				if ("/performance/workdiary/workdiaryshow".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && "link".equalsIgnoreCase(arg1.getParameter("b_query"))){
					/**定位到首页,*/
					if(this.getPagination()!=null){
						this.getPagination().firstPage();
					}
					arg1.setAttribute("targetWindow", "1");
				}
		}catch(Exception e){
		   	  e.printStackTrace();
		}
		   //
		   if ("/performance/workdiary/myworkdiaryshow".equals(arg0.getPath()) && arg1.getParameter("b_add") != null && arg1.getParameter("checkApp") != null) {
			   String check = arg1.getParameter("checkApp");
			   if ("ok".equalsIgnoreCase(check)) {
				   this.setCheckApp("1");
				   this.setCheckResion("ok");
			   } else {
				   this.setCheckApp("0");
				   this.setCheckResion(check);
			   }
		   } else if ("/performance/workdiary/myworkdiary".equals(arg0.getPath()) && arg1.getParameter("b_load") != null && arg1.getParameter("checkApp") != null) {
			   String check = arg1.getParameter("checkApp");
			   if ("ok".equalsIgnoreCase(check)) {
				   this.setCheckApp("1");
				   this.setCheckResion("ok");
			   } else {
				   this.setCheckApp("0");
				   this.setCheckResion(check);
			   }
		   } else {
			   this.setCheckApp("1");
			   this.setCheckResion("ok");
		   }
	       return super.validate(arg0, arg1);
		}

	public String getCsflag() {
		return csflag;
	}

	public void setCsflag(String csflag) {
		this.csflag = csflag;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getCurr_user() {
		return curr_user;
	}

	public void setCurr_user(String curr_user) {
		this.curr_user = curr_user;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	
	public RecordVo getP01Vo_myself() {
		return p01Vo_myself;
	}
	
	public void setP01Vo_myself(RecordVo p01Vo_myself) {
		this.p01Vo_myself = p01Vo_myself;
	}

	public RecordVo getP01Vo() {
		return p01Vo;
	}

	public void setP01Vo(RecordVo vo) {
		p01Vo = vo;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public String getSelprojectdesc() {
		return selprojectdesc;
	}

	public void setSelprojectdesc(String selprojectdesc) {
		this.selprojectdesc = selprojectdesc;
	}

	public String getSelradio() {
		return selradio;
	}

	public void setSelradio(String selradio) {
		this.selradio = selradio;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getTimefield() {
		return timefield;
	}

	public void setTimefield(String timefield) {
		this.timefield = timefield;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getYmd() {
		return ymd;
	}

	public void setYmd(String ymd) {
		this.ymd = ymd;
	}

	public String getAppflag() {
		return appflag;
	}

	public void setAppflag(String appflag) {
		this.appflag = appflag;
	}

	public String getDis() {
		return dis;
	}

	public void setDis(String dis) {
		this.dis = dis;
	}

	public String getMonthnum() {
		return monthnum;
	}

	public void setMonthnum(String monthnum) {
		this.monthnum = monthnum;
	}

	public String getYearnum() {
		return yearnum;
	}

	public void setYearnum(String yearnum) {
		this.yearnum = yearnum;
	}

	public String getTablestr() {
		return tablestr;
	}

	public void setTablestr(String tablestr) {
		this.tablestr = tablestr;
	}

	public ArrayList getMonthlist() {
		return monthlist;
	}

	public void setMonthlist(ArrayList monthlist) {
		this.monthlist = monthlist;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getWeeknum() {
		return weeknum;
	}

	public void setWeeknum(String weeknum) {
		this.weeknum = weeknum;
	}

	public String[] getWeeknum_arr() {
		return weeknum_arr;
	}

	public void setWeeknum_arr(String[] weeknum_arr) {
		this.weeknum_arr = weeknum_arr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList getYmdlist() {
		return ymdlist;
	}

	public void setYmdlist(ArrayList ymdlist) {
		this.ymdlist = ymdlist;
	}

	public ArrayList getStatelist() {
		return statelist;
	}

	public void setStatelist(ArrayList statelist) {
		this.statelist = statelist;
	}

	public String getAppstate() {
		return appstate;
	}

	public void setAppstate(String appstate) {
		this.appstate = appstate;
	}

	public ArrayList getWeeklist() {
		return weeklist;
	}

	public void setWeeklist(ArrayList weeklist) {
		this.weeklist = weeklist;
	}

	public String getTimeflag() {
		return timeflag;
	}

	public void setTimeflag(String timeflag) {
		this.timeflag = timeflag;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}

	public String getReturnch() {
		return returnch;
	}

	public void setReturnch(String returnch) {
		this.returnch = returnch;
	}

	public String getPerPlanTable() {
		return perPlanTable;
	}

	public void setPerPlanTable(String perPlanTable) {
		this.perPlanTable = perPlanTable;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getPersonid() {
		return personid;
	}

	public void setPersonid(String personid) {
		this.personid = personid;
	}

	public String getPersonname() {
		return personname;
	}

	public void setPersonname(String personname) {
		this.personname = personname;
	}

	public ArrayList getColumlist() {
		return columlist;
	}

	public void setColumlist(ArrayList columlist) {
		this.columlist = columlist;
	}

	public String getColum() {
		return colum;
	}

	public void setColum(String colum) {
		this.colum = colum;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	

	public String getNamevalue() {
		return namevalue;
	}

	public void setNamevalue(String namevalue) {
		this.namevalue = namevalue;
	}

	public ArrayList getPredbnamelist() {
		return predbnamelist;
	}

	public void setPredbnamelist(ArrayList predbnamelist) {
		this.predbnamelist = predbnamelist;
	}

	public String getCheckApp() {
		return checkApp;
	}

	public void setCheckApp(String checkApp) {
		this.checkApp = checkApp;
	}

	public String getCheckResion() {
		return checkResion;
	}

	public void setCheckResion(String checkResion) {
		this.checkResion = checkResion;
	}
	
	public String getExistFile() {
		return existFile;
	}

	public void setExistFile(String existFile) {
		this.existFile = existFile;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
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
	public String getPendingCode() {
		return pendingCode;
	}

	public void setPendingCode(String pendingCode) {
		this.pendingCode = pendingCode;
	}

	public String getDoneFlag() {
		return doneFlag;
	}

	public void setDoneFlag(String doneFlag) {
		this.doneFlag = doneFlag;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getZxgflag() {
		return zxgflag;
	}

	public void setZxgflag(String zxgflag) {
		this.zxgflag = zxgflag;
	}

	public ArrayList getDatalist() {
		return datalist;
	}

	public void setDatalist(ArrayList datalist) {
		this.datalist = datalist;
	}

	public PaginationForm getPaginationForm() {
		return paginationForm;
	}

	public void setPaginationForm(PaginationForm paginationForm) {
		this.paginationForm = paginationForm;
	}
	
}
