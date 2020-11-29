package com.hjsj.hrms.actionform.train.attendance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:TrainAtteForm.java
 * </p>
 * <p>
 * Description:培训考勤
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2011-03-03 下午01:07:55
 * </p>
 * 
 * @author LiWeichao
 * @version 5.0
 */
public class TrainAtteForm extends FrameForm {

	/** 培训考勤设置 */
	private ArrayList attendancelist = new ArrayList();// 人员主集非代码的字符型指标
	private String card_no;// 卡号指标
	private String card_num;// 卡号
	private String late_for;// 下课后几分钟签退算早退
	private String leave_early;// 上课后几分钟签到算迟到
    private String a_code;
	private String type;
	/** 排课 */
	private String id;
	private PaginationForm trainAtteForm = new PaginationForm();
	private ArrayList classplanlist = new ArrayList();//
	private ArrayList fielditemlist=new ArrayList();
	private String sql_str;
	private String where_str;
	private String order_str;
	private String cond_str;
	private String columns;
	private String classplan;// 培训班编号
	private String courseplan;// 培训课程编号
	private String classplanName;// 培训班名称
	private String courseplanName;// 培训课程名称
	private String r4101;
	private String start_date;//开始时间
	private String stop_date;//停止时间
	private String begin_time;//开始时间
	private String end_time;//结束时间
	private String class_len;//课时
	private String begin_card;//上课签到标记
	private String end_card;//下课签退标记
	private String minute;

	private String uplevel;// 部门层级
	private String emp_name;// 查询用的人员名称

	/** 条件查询 */
	private String isOk;//判断返回是否成功
	private ArrayList fieldlist=new ArrayList();
	private ArrayList tjtimelist=new ArrayList();
	private ArrayList timelist=new ArrayList();
	private String item_field;
	private String search="";//查询条件
    //################考勤签到汇总
	private String sort="";//分类 1：人员：2：课程；3：班组
	private ArrayList sortlist=new ArrayList();
	private String loadclass;
    private String view_record;
	private String nowDate;// 现在日期
	private String nowTime;// 现在时间
	private String regFlag;// 签到标记 1：签到 2：签退 3：补签到 4：补签退
	private String reg_state;//0：正常 1：迟到 2：早退
	private String nowHours;//小时
	private String nowMinutes;//分钟
	private String retReason;//补签原因
    private String timeflag;
    private String startime;
    private String endtime;
    private String classpanSpflag;
    private String regType;
    
    private String query;
    
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getTimeflag() {
		return timeflag;
	}

	public void setTimeflag(String timeflag) {
		this.timeflag = timeflag;
	}

	public String getNowHours() {
		return nowHours;
	}

	public void setNowHours(String nowHours) {
		this.nowHours = nowHours;
	}

	public String getNowMinutes() {
		return nowMinutes;
	}

	public void setNowMinutes(String nowMinutes) {
		this.nowMinutes = nowMinutes;
	}

	@Override
    public void outPutFormHM() {
		this.setAttendancelist((ArrayList) this.getFormHM().get("attendancelist"));
		this.setCard_no((String) this.getFormHM().get("card_no"));
		this.setLate_for((String) this.getFormHM().get("late_for"));
		this.setLeave_early((String) this.getFormHM().get("leave_early"));
		this.setType((String) this.getFormHM().get("type"));
		this.setSql_str((String) this.getFormHM().get("sql_str"));
		this.setWhere_str((String)this.getFormHM().get("where_str"));
		this.setCond_str((String) this.getFormHM().get("cond_str"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setClassplanlist((ArrayList) this.getFormHM().get(
						"classplanlist"));
		this.setClassplan((String) this.getFormHM().get("classplan"));
		this.setCourseplan((String) this.getFormHM().get("courseplan"));
		this.setId((String) this.getFormHM().get("id"));
		this.setStart_date((String) this.getFormHM().get("start_date"));
		this.setStop_date((String) this.getFormHM().get("stop_date"));
		this.setBegin_time((String) this.getFormHM().get("begin_time"));
		this.setEnd_time((String) this.getFormHM().get("end_time"));
		this.setClass_len((String) this.getFormHM().get("class_len"));
		this.setBegin_card((String) this.getFormHM().get("begin_card"));
		this.setEnd_card((String) this.getFormHM().get("end_card"));
		this.setR4101((String) this.getFormHM().get("r4101"));
		this.setEmp_name((String) this.getFormHM().get("emp_name"));
		this.setUplevel((String) this.getFormHM().get("uplevel"));
		this.setMinute((String)this.getFormHM().get("minute"));

		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setTimelist((ArrayList)this.getFormHM().get("timelist"));
		this.setTjtimelist((ArrayList)this.getFormHM().get("tjtimelist"));
		this.setSearch((String)this.getFormHM().get("search"));
		//@@@@@@@@@@@@考勤签到汇总@@@@@@@@@@@@@//
		this.setSort((String)this.getFormHM().get("sort"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));		
        this.setView_record((String)this.getFormHM().get("view_record"));
        this.setClasspanSpflag((String)this.getFormHM().get("classpanSpflag"));
		this.setNowDate((String) this.getFormHM().get("nowDate"));
		this.setNowTime((String) this.getFormHM().get("nowTime"));
		this.setRegFlag((String) this.getFormHM().get("regFlag"));
		this.setClassplanName((String) this.getFormHM().get("classplanName"));
		this.setCourseplanName((String) this.getFormHM().get("courseplanName"));
		this.setReg_state((String)this.getFormHM().get("reg_state"));
		this.setNowHours((String)this.getFormHM().get("nowHours"));
//		this.setRetReason((String)this.getFormHM().get("retReason"));
		this.setNowMinutes((String)this.getFormHM().get("nowMinutes"));
		this.setOrder_str((String)this.getFormHM().get("order_str"));
		this.setLoadclass((String)this.getFormHM().get("loadclass"));
        this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist"));
        this.setIsOk((String)this.getFormHM().get("isOk"));
        this.setTimeflag((String)this.getFormHM().get("timeflag"));
        this.setRegType((String)this.getFormHM().get("regType"));
        this.setQuery((String)this.getFormHM().get("query"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("classplan", this.getClassplan());
		this.getFormHM().put("courseplan", this.getCourseplan());
		this.getFormHM().put("emp_name", this.getEmp_name());
		this.getFormHM().put("search", this.getSearch());
		this.getFormHM().put("nowDate", this.getNowDate());
		this.getFormHM().put("nowHours", this.getNowHours());
		this.getFormHM().put("nowMinutes", this.getNowMinutes());
		this.getFormHM().put("regFlag", this.getRegFlag());
		this.getFormHM().put("retReason", this.getRetReason());
		this.getFormHM().put("timeflag", this.getTimeflag());
		this.getFormHM().put("endtime", this.getEndtime());//培训班按时间段结束时间
		this.getFormHM().put("startime", this.getStartime());//培训班按时间开始束时间
		this.getFormHM().put("regType", this.getRegType());//培训班按时间开始束时间
		this.getFormHM().put("query", this.getQuery());
		this.getFormHM().put("minute", this.getMinute());
		//考勤签到汇总
		this.getFormHM().put("sort", this.getSort());	
		if(this.getPagination()!= null)
			this.getFormHM().put("selected", this.getPagination().getSelectedList());

	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/train/signCollect/signcollect".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
			if(this.getPagination()!=null)
            	this.getPagination().firstPage();//?    
        }else if("/train/attendance/registration".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
        	if(this.getPagination()!=null)
            	this.getPagination().firstPage();//?    
        }else if("/train/attendance/pageregistration".equals(arg0.getPath())&&arg1.getParameter("b_ret")!=null){
        	if(this.getPagination()!=null)
            	this.getPagination().firstPage();//?  
        }else if("/train/attendance/orgAtteTree".equals(arg0.getPath())){
        	if(this.getPagination()!=null)
            	this.getPagination().firstPage();//?
        }
        else if("/train/attendance/trainAtteCourse".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
        	if(this.getPagination()!=null)
            	this.getPagination().firstPage();//?
        }
		return super.validate(arg0, arg1);
	}
	public ArrayList getAttendancelist() {
		return attendancelist;
	}

	public void setAttendancelist(ArrayList attendancelist) {
		this.attendancelist = attendancelist;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getLate_for() {
		return late_for;
	}

	public void setLate_for(String late_for) {
		this.late_for = late_for;
	}

	public String getLeave_early() {
		return leave_early;
	}

	public void setLeave_early(String leave_early) {
		this.leave_early = leave_early;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PaginationForm getTrainAtteForm() {
		return trainAtteForm;
	}

	public void setTrainAtteForm(PaginationForm trainAtteForm) {
		this.trainAtteForm = trainAtteForm;
	}

	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}

	public String getCond_str() {
		return cond_str;
	}

	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public ArrayList getClassplanlist() {
		return classplanlist;
	}

	public void setClassplanlist(ArrayList classplanlist) {
		this.classplanlist = classplanlist;
	}

	public String getClassplan() {
		return classplan;
	}

	public void setClassplan(String classplan) {
		this.classplan = classplan;
	}

	public String getCourseplan() {
		return courseplan;
	}

	public void setCourseplan(String courseplan) {
		this.courseplan = courseplan;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getStop_date() {
		return stop_date;
	}

	public void setStop_date(String stop_date) {
		this.stop_date = stop_date;
	}

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getClass_len() {
		return class_len;
	}

	public void setClass_len(String class_len) {
		this.class_len = class_len;
	}

	public String getBegin_card() {
		return begin_card;
	}

	public void setBegin_card(String begin_card) {
		this.begin_card = begin_card;
	}

	public String getEnd_card() {
		return end_card;
	}

	public void setEnd_card(String end_card) {
		this.end_card = end_card;
	}

	public String getR4101() {
		return r4101;
	}

	public void setR4101(String r4101) {
		this.r4101 = r4101;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}


	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}


	public String getCard_num() {
		return card_num;
	}

	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}

	public String getNowDate() {
		return nowDate;
	}

	public void setNowDate(String nowDate) {
		this.nowDate = nowDate;
	}

	public String getNowTime() {
		return nowTime;
	}

	public void setNowTime(String nowTime) {
		this.nowTime = nowTime;
	}

	public String getRegFlag() {
		return regFlag;
	}

	public void setRegFlag(String regFlag) {
		this.regFlag = regFlag;
	}

	public String getClassplanName() {
		return classplanName;
	}

	public void setClassplanName(String classplanName) {
		this.classplanName = classplanName;
	}

	public String getCourseplanName() {
		return courseplanName;
	}

	public void setCourseplanName(String courseplanName) {
		this.courseplanName = courseplanName;
	}

	public String getReg_state() {
		return reg_state;
	}

	public void setReg_state(String reg_state) {
		this.reg_state = reg_state;
	}

	public String getRetReason() {
		return retReason;
	}

	public void setRetReason(String retReason) {
		this.retReason = retReason;
	}

	public String getLoadclass() {
		return loadclass;
	}

	public void setLoadclass(String loadclass) {
		this.loadclass = loadclass;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getItem_field() {
		return item_field;
	}

	public void setItem_field(String item_field) {
		this.item_field = item_field;
	}

	public String getWhere_str() {
		return where_str;
	}

	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}

	public String getOrder_str() {
		return order_str;
	}

	public void setOrder_str(String order_str) {
		this.order_str = order_str;
	}

	public String getView_record() {
		return view_record;
	}

	public void setView_record(String view_record) {
		this.view_record = view_record;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getIsOk() {
		return isOk;
	}

	public void setIsOk(String isOk) {
		this.isOk = isOk;
	}

	public ArrayList getTimelist() {
		return timelist;
	}

	public void setTimelist(ArrayList timelist) {
		this.timelist = timelist;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getClasspanSpflag() {
		return classpanSpflag;
	}

	public void setClasspanSpflag(String classpanSpflag) {
		this.classpanSpflag = classpanSpflag;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public ArrayList getTjtimelist() {
		return tjtimelist;
	}

	public void setTjtimelist(ArrayList tjtimelist) {
		this.tjtimelist = tjtimelist;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

}
