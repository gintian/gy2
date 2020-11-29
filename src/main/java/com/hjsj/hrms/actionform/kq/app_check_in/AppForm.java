/*
 * Created on 2006-2-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.kq.app_check_in;

import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author wxh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AppForm extends FrameForm {

	/** 新增属性 */
	private String Opinionlength;//意见长度
	public String getOpinionlength() {
		return Opinionlength;
	}

	public void setOpinionlength(String opinionlength) {
		Opinionlength = opinionlength;
	}

	private String date_count;// 天数
	private String time_count;
	private String hr_count;// 小时数
	private String start_time_h;// --小时
	private String start_time_m;// --分钟
	private String infoStr;
	private String scope_start_time;
	private String scope_end_time;

	/** 起始终止日期 */

	private String start_date;
	private String end_date;
	
	private String app_start_date;
	private String app_end_date;
	
	private int cols = 0; // 前台table列数
	/** 查询类型，1.考勤项目2.时间范围 */
	private String query_type = "1";
	
	private String select_type = "0";
	/** 审批状态 */
	private String sp_flag;
	/** 审批状态列表 */
	private ArrayList splist = new ArrayList();
	/** 审批标志字段 */
	private String sp_field;
	/** 主键字段 */
	private String key_field;
	/** 查询条件串 */
	private String cond_str = "";
	private String orderby = "";
	private String treeCode;
	private String code;
	private String kind;
	/** 查询串 */
	private String sql_str;
	private String sql_excle;//导出excle
	/** 需显示的指标串 */
	private String cond_order;
	private String relatTableid;// 高级花名册对应的单表名称
	private String condition;// 高级花名册打印的条件
	private String returnURL;// 返回的连接
	private String columns;

	private String radio;

	private String mess;
	private String mess1;// Q1515
	private String mess2;// q1511

	private String sign;

	private String result;

	private String visi;

	private int current = 1;

	private String message;

	private ArrayList salist;
	private ArrayList salistko;
	private ArrayList salist11;

	private String dbpre = "";
	/** 考勤年份 */
	private String kq_year = "";
	/** 考勤区间 */
	private String kq_duration = "";
	private ArrayList yearlist = new ArrayList();
	private ArrayList durationlist = new ArrayList();
	private ArrayList dblist = new ArrayList();
	/** 表名 kq_overtime,kq_leave,kq_busi_away */
	private String table = "";
	/** 申请记录的字段列表 */
	private ArrayList fieldlist = new ArrayList();
	/** 人员信息的字段列表 */
	private PaginationForm appForm = new PaginationForm();
	/** 查阅，修改 */
	private ArrayList viewlist = new ArrayList();//
	/** 被选中的人员信息列表 */
	private ArrayList infolist = new ArrayList();//
	private String flag = "0";
	/** 手工选人、条件选人标志 */
	private String history;
	private String like;
	private String audit_flag;
	private String selectflag = "1";
	/** 高级查询用属性 **/
	//szk加班汇总
	private ArrayList addtypenamelist = new ArrayList();
	private ArrayList addtypeidlist = new ArrayList();
	String applytime; //q11z4
	
	/* 选中的字段值对列表 */
	private ArrayList selectfieldlist = new ArrayList();

	private ArrayList selectedinfolist = new ArrayList();
	/* 关系操作符 */
	private ArrayList operlist = new ArrayList();
	/* 逻辑操作符 */
	private ArrayList logiclist = new ArrayList();

	private ArrayList selectedlist = new ArrayList();
	/* factor list */
	private ArrayList factorlist = new ArrayList();
	
	private ArrayList selectFieldList = new ArrayList();
	
	private ArrayList excelFieldList = new ArrayList();
	
	/** 高级查询用属性 **/
	
	/** 能用查询的表达式:!(1+2*3),!非，＋或，*且 */
	private String expression;
	private String left_fields[];
	private String right_fields[];
	private RecordVo cancelvo = new RecordVo("q15");
	private ArrayList selist = new ArrayList();
	private String spFlag;
	private ArrayList kq_list = new ArrayList();// 人员库
	private String select_name;// 筛选名字
	private String select_flag;// 筛选表示
	private String full = "0";// 全部显示
	private String select_pre;
	private String z5;
	private ArrayList searchfieldlist = new ArrayList();
	private String select_time_type;// 时间类型，0，按起止时间查询，1按申请日期查询
	private String sp_result;
	private ArrayList class_list = new ArrayList();// 班次的list
	private String dert_itemid;
	private String dert_value;
	private String sortid;
	private String wherestr_s;
	private String ordeby_s;
	private String columnstr_s;
	private ArrayList fielditemlist = new ArrayList();
	private String group_id;
	private String dotflag = "0"; // 为0的时显示全部申请
	private String isLeave = "";
	private String bflag; // M备注型；A代码型
	private ArrayList group11; // XX11 审核 部门
	private ArrayList group15; // XX15 审批 单位
	private String unit11;// Q1511
	private String unit15;// q1515
	private ArrayList leaveTimeList = new ArrayList();
	private String approved_delete = "1";// 已批申请登记数据是否可以删除;0:不删除；1：删除
	private String uplevel; // 显示层级
	private String returnvalue = "1";
	private String isAllow="true";
	//考勤期间开始时间
	private String duration_start;
	//考勤期间结束时间
	private String duration_end;
	private String sub_page;
	private String viewPost;
	private String leftdays;
	
	private String appReaCode;//加班原因代码
	private String appReaCodesetid;//加班原因代码项
	private String appReaField;
	
	private String isExistIftoRest;//q11是否存在是否调休字段
	private String IftoRest;//是否调休
	
	
	private FormFile file;
	private String importMsg;
	private String desc;
	
	/*新增属性*/
	private ArrayList msglist = new ArrayList();
	private PaginationForm msgPageForm=new PaginationForm();
	private String outName;
	
	public String getOutName() {
		return outName;
	}
	public void setOutName(String outName) {
		this.outName = outName;
	}
	public PaginationForm getMsgPageForm() {
		return msgPageForm;
	}
	public void setMsgPageForm(PaginationForm msgPageForm) {
		this.msgPageForm = msgPageForm;
	}
	public ArrayList getMsglist()
	{
		return msglist;
	}

	public void setMsglist(ArrayList msglist)
	{
		this.msglist = msglist;
	}
	public ArrayList getAddtypenamelist()
	{
		return addtypenamelist;
	}

	public void setAddtypenamelist(ArrayList addtypenamelist)
	{
		this.addtypenamelist = addtypenamelist;
	}

	public ArrayList getAddtypeidlist()
	{
		return addtypeidlist;
	}

	public void setAddtypeidlist(ArrayList addtypeidlist)
	{
		this.addtypeidlist = addtypeidlist;
	}

	public String getIsExistIftoRest() {
		return isExistIftoRest;
	}

	public void setIsExistIftoRest(String isExistIftoRest) {
		this.isExistIftoRest = isExistIftoRest;
	}

	public String getIftoRest() {
		return IftoRest;
	}

	public void setIftoRest(String iftoRest) {
		IftoRest = iftoRest;
	}

	public String getImportMsg() {
		return importMsg;
	}

	public void setImportMsg(String importMsg) {
		this.importMsg = importMsg;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getAppReaCode() {
		return appReaCode;
	}

	public void setAppReaCode(String appReaCode) {
		this.appReaCode = appReaCode;
	}

	public String getAppReaCodesetid() {
		return appReaCodesetid;
	}

	public void setAppReaCodesetid(String appReaCodesetid) {
		this.appReaCodesetid = appReaCodesetid;
	}

	public String getAppReaField() {
		return appReaField;
	}

	public void setAppReaField(String appReaField) {
		this.appReaField = appReaField;
	}

	public String getLeftdays() {
		return leftdays;
	}

	public void setLeftdays(String leftdays) {
		this.leftdays = leftdays;
	}

	public String getSub_page() {
		return sub_page;
	}

	public void setSub_page(String sub_page) {
		this.sub_page = sub_page;
	}

	public String getDuration_start() {
		return duration_start;
	}

	public void setDuration_start(String duration_start) {
		this.duration_start = duration_start;
	}

	public String getDuration_end() {
		return duration_end;
	}

	public void setDuration_end(String duration_end) {
		this.duration_end = duration_end;
	}

	public String getIsLeave() {
		return isLeave;
	}

	public void setIsLeave(String isLeave) {
		this.isLeave = isLeave;
	}

	public ArrayList getLeaveTimeList() {
		return leaveTimeList;
	}

	public void setLeaveTimeList(ArrayList leaveTimeList) {
		this.leaveTimeList = leaveTimeList;
	}

	public String getSortid() {
		return sortid;
	}

	public void setSortid(String sortid) {
		this.sortid = sortid;
	}

	public String getDert_value() {
		return dert_value;
	}

	public void setDert_value(String dert_value) {
		this.dert_value = dert_value;
	}

	public String getDert_itemid() {
		return dert_itemid;
	}

	public void setDert_itemid(String dert_itemid) {
		this.dert_itemid = dert_itemid;
	}

	public ArrayList getClass_list() {
		return class_list;
	}

	public void setClass_list(ArrayList class_list) {
		this.class_list = class_list;
	}

	public String getSp_result() {
		return sp_result;
	}

	public void setSp_result(String sp_result) {
		this.sp_result = sp_result;
	}

	public ArrayList getSearchfieldlist() {
		return searchfieldlist;
	}

	public void setSearchfieldlist(ArrayList searchfieldlist) {
		this.searchfieldlist = searchfieldlist;
		if(this.searchfieldlist!=null)
			this.cols = searchfieldlist.size() + 5;
	}

	public String getSpFlag() {
		return spFlag;
	}

	public void setSpFlag(String spFlag) {
		this.spFlag = spFlag;
	}

	public ArrayList getSelist() {
		return selist;
	}

	public void setSelist(ArrayList selist) {
		this.selist = selist;
	}

	public RecordVo getCancelvo() {
		return cancelvo;
	}

	public void setCancelvo(RecordVo cancelvo) {
		this.cancelvo = cancelvo;
	}

	public AppForm() {
		CommonData vo = new CommonData("=", "=");
		operlist.add(vo);
		vo = new CommonData(">", ">");
		operlist.add(vo);
		vo = new CommonData(">=", ">=");
		operlist.add(vo);
		vo = new CommonData("<", "<");
		operlist.add(vo);
		vo = new CommonData("<=", "<=");
		operlist.add(vo);
		vo = new CommonData("<>", "<>");
		operlist.add(vo);
		vo = new CommonData("*", "并且");
		logiclist.add(vo);
		vo = new CommonData("+", "或");
		logiclist.add(vo);
	}

	private String a_code;
	private String showtype;
	private ArrayList showtypelist = new ArrayList();
	private String seal_date;

	public String getSeal_date() {
		return seal_date;
	}

	public void setSeal_date(String seal_date) {
		this.seal_date = seal_date;
	}

	@Override
    public void outPutFormHM() {
		this.setOutName((String)this.getFormHM().get("outName"));
		this.getMsgPageForm().setList((ArrayList)this.getFormHM().get("msglist"));
		this.setMsglist((ArrayList) this.getFormHM().get("msglist"));
		this.setOpinionlength((String) this.getFormHM().get("opinionlength"));
		this.getAppForm().getPagination().gotoPage(current);
		this.setDblist((ArrayList) this.getFormHM().get("dblist"));
		this.setYearlist((ArrayList) this.getFormHM().get("yearlist"));
		this.setDurationlist((ArrayList) this.getFormHM().get("durationlist"));
		this.setApplytime((String) this.getFormHM().get("applytime"));
		this.setTable((String) this.getFormHM().get("table"));
		this.setDbpre((String) this.getFormHM().get("dbpre"));
		this.setKq_year((String) this.getFormHM().get("kq_year"));
		this.setKq_duration((String) this.getFormHM().get("kq_duration"));
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setCond_str((String) this.getFormHM().get("cond_str"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setSql_str((String) this.getFormHM().get("sql_str"));
		this.setSql_excle((String)this.getFormHM().get("sql_excle"));
		this.setOrderby((String) this.getFormHM().get("orderby"));
		if (this.getFormHM().get("selectfieldlist") != null)
			this.setSelectfieldlist((ArrayList) this.getFormHM().get(
					"selectfieldlist"));
		this.setAddtypeidlist((ArrayList) this.getFormHM().get("addtypeidlist"));
		this.setAddtypenamelist((ArrayList) this.getFormHM().get("addtypenamelist"));
		this.setViewlist((ArrayList) this.getFormHM().get("viewlist"));
		this.setInfolist((ArrayList) this.getFormHM().get("infolist"));
		this.setSelectflag((String) this.getFormHM().get("selectflag"));
		this.setExpression((String) this.getFormHM().get("expression"));
		this.setFactorlist((ArrayList) this.getFormHM().get("factorlist"));
		this.setSelectedinfolist((ArrayList) this.getFormHM().get(
				"selectedinfolist"));
		this.setSalist((ArrayList) this.getFormHM().get("salist"));
		this.setTreeCode((String) this.getFormHM().get("treeCode"));

		this.setResult((String) this.getFormHM().get("result"));
		this.setRadio((String) this.getFormHM().get("radio"));
		this.setSign((String) this.getFormHM().get("sign"));
		this.setMessage((String) this.getFormHM().get("message"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setVisi((String) this.getFormHM().get("visi"));
		this.like = "0";
		this.history = "0";
		this.setCond_order((String) this.getFormHM().get("cond_order"));
		this.setShowtype((String) this.getFormHM().get("showtype"));
		this.setShowtypelist((ArrayList) this.getFormHM().get("showtypelist"));
		this.setRelatTableid((String) this.getFormHM().get("relatTableid"));
		this.setCondition((String) this.getFormHM().get("condition"));
		this.setReturnURL((String) this.getFormHM().get("returnURL"));
		this.setAudit_flag((String) this.getFormHM().get("audit_flag"));

		/** chenmengqing added 20070116 */
		this.setSplist((ArrayList) this.getFormHM().get("splist"));
		this.setMess((String) this.getFormHM().get("mess"));
		/** end. */
		this.setSeal_date((String) this.getFormHM().get("seal_date"));
		this.setCancelvo((RecordVo) this.getFormHM().get("cancelvo"));
		this.setSelist((ArrayList) this.getFormHM().get("selist"));
		this.setSpFlag((String) this.getFormHM().get("spFlag"));
		this.setSearchfieldlist((ArrayList) this.getFormHM().get(
				"searchfieldlist"));
		this.setSp_flag((String) this.getFormHM().get("sp_flag"));
		this.setShowtype((String) this.getFormHM().get("showtype"));

		this.setKq_list((ArrayList) this.getFormHM().get("kq_list"));
		this.setSelect_name((String) this.getFormHM().get("select_name"));
		this.setSelect_flag((String) this.getFormHM().get("select_flag"));
		this.setFull((String) this.getFormHM().get("full"));
		this.setSelect_pre((String) this.getFormHM().get("select_pre"));
		this.setStart_date((String) this.getFormHM().get("start_date"));
		this.setEnd_date((String) this.getFormHM().get("end_date"));
		this.setZ5((String) this.getFormHM().get("z5"));
		this.setSelect_time_type((String) this.getFormHM().get(
				"select_time_type"));
		this.setClass_list((ArrayList) this.getFormHM().get("class_list"));
		this.setDert_itemid((String) this.getFormHM().get("dert_itemid"));
		this.setSortid((String) this.getFormHM().get("sortid"));
		this.setWherestr_s((String) this.getFormHM().get("wherestr_s"));
		this.setOrdeby_s((String) this.getFormHM().get("ordeby_s"));
		this.setColumnstr_s((String) this.getFormHM().get("columnstr_s"));
		this
				.setFielditemlist((ArrayList) this.getFormHM().get(
						"fielditemlist"));
		this.setGroup_id((String) this.getFormHM().get("group_id"));
		this.setSelectedlist((ArrayList) this.getFormHM().get("selectedlist"));
		this.setDotflag((String) this.getFormHM().get("dotflag"));
		this.setIsLeave((String) this.getFormHM().get("isLeave"));
		this
				.setLeaveTimeList((ArrayList) this.getFormHM().get(
						"leaveTimeList"));
		this.setSalistko((ArrayList) this.getFormHM().get("salistko"));
		this.setSalist11((ArrayList) this.getFormHM().get("salist11"));
		this.setMess1((String) this.getFormHM().get("mess1"));
		this.setMess2((String) this.getFormHM().get("mess2"));
		this.setBflag((String) this.getFormHM().get("bflag"));
		this.setGroup11((ArrayList) this.getFormHM().get("group11"));
		this.setGroup15((ArrayList) this.getFormHM().get("group15"));
		this.setUnit11((String) this.getFormHM().get("unit11"));
		this.setUnit15((String) this.getFormHM().get("unit15"));
		this.setUplevel((String) this.getFormHM().get("uplevel"));
		this.setHr_count((String) this.getFormHM().get("hr_count"));
		this.setApproved_delete((String) this.getFormHM()
				.get("approved_delete"));
		this.setStart_time_h((String) this.getFormHM().get("start_time_h"));
		this.setStart_time_m((String) this.getFormHM().get("start_time_m"));
		this.setScope_start_time((String) this.getFormHM().get("scope_start_time"));
		this.setScope_end_time((String) this.getFormHM().get("scope_end_time"));
		this.setInfoStr((String) this.getFormHM().get("infoStr"));
		this.setExcelFieldList((ArrayList)this.getFormHM().get("excelFieldList"));
		this.setSelectFieldList((ArrayList)this.getFormHM().get("selectFieldList"));
		
		this.setIsAllow((String)this.getFormHM().get("isAllow"));
		this.setSub_page((String)this.getFormHM().get("sub_page"));
		this.setViewPost((String)this.getFormHM().get("viewPost"));
		this.setApp_start_date((String)this.getFormHM().get("app_start_date"));
		this.setApp_end_date((String)this.getFormHM().get("app_end_date"));
		this.setSelect_type((String)this.getFormHM().get("select_type"));
		this.setLeftdays((String)this.getFormHM().get("leftdays"));
		
		this.setAppReaCode((String)this.getFormHM().get("appReaCode"));
		this.setAppReaCodesetid((String)this.getFormHM().get("appReaCodesetid"));
		this.setAppReaField((String)this.getFormHM().get("appReaField"));
		this.setImportMsg((String)this.getFormHM().get("importMsg"));
		this.setDesc((String)this.getFormHM().get("desc"));
		
		this.setIsExistIftoRest((String)this.getFormHM().get("isExistIftoRest"));
		this.setIftoRest((String)this.getFormHM().get("IftoRest"));
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("msglist", this.getMsglist());
		this.getFormHM().put("opinionlength", this.getOpinionlength());
		this.getFormHM().put("dbpre", this.getDbpre());
		this.getFormHM().put("kq_year", (String) this.getKq_year());
		this.getFormHM().put("kq_duration", (String) this.getKq_duration());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("yearlist", this.getYearlist());
		this.getFormHM().put("durationlist", this.getDurationlist());
		if (this.getPagination() != null)
			this.getFormHM().put("selectedinfolist",
					(ArrayList) this.getPagination().getSelectedList());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("viewlist", this.getViewlist());
		this.getFormHM().put("applytime", this.getApplytime());
		this.getFormHM().put("table", this.getTable());
		this.getFormHM().put("selectflag", this.getSelectflag());
		this.getFormHM().put("history", this.getHistory());
		this.getFormHM().put("like", this.getLike());
		this.getFormHM().put("factorlist", this.getFactorlist());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("selectfieldlist", this.getSelectfieldlist());
		this.getFormHM().put("setAddtypenamelist", this.getAddtypenamelist());
		this.getFormHM().put("setAddtypeidlist", this.getAddtypeidlist());
		this.getFormHM().put("infolist", this.getSelectedinfolist());
		this.getFormHM().put("mess", this.getMess());
		this.getFormHM().put("radio", this.getRadio());
		this.getFormHM().put("sign", this.getSign());
		this.getFormHM().put("result", this.getResult());
		this.getFormHM().put("showtype", this.getShowtype());
		this.getFormHM().put("code", code);
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("audit_flag", this.getAudit_flag());
		/** chenmengqing added at 20070214 */
		this.getFormHM().put("start_date", this.start_date);
		this.getFormHM().put("end_date", this.end_date);
		this.getFormHM().put("query_type", this.query_type);
		this.getFormHM().put("sp_flag", this.sp_flag);
		this.getFormHM().put("a_code", this.getA_code());
		/** chen end. */
		this.getFormHM().put("seal_date", this.getSeal_date());
		this.getFormHM().put("cancelvo", this.getCancelvo());
		this.getFormHM().put("relatTableid", this.getRelatTableid());
		this.getFormHM().put("condition", this.getCondition());
		this.getFormHM().put("returnUrl", this.getReturnURL());
		this.getFormHM().put("select_name", this.getSelect_name());
		this.getFormHM().put("select_flag", this.getSelect_flag());
		this.getFormHM().put("full", this.getFull());
		this.getFormHM().put("select_pre", this.getSelect_pre());
		this.getFormHM().put("select_time_type", this.getSelect_time_type());
		this.getFormHM().put("sp_result", this.getSp_result());
		this.getFormHM().put("dert_value", this.getDert_value());
		this.getFormHM().put("dotflag", this.getDotflag());
		this.getFormHM().put("mess1", this.getMess1());
		this.getFormHM().put("mess2", this.getMess2());
		this.getFormHM().put("unit11", this.getUnit11());
		this.getFormHM().put("unit15", this.getUnit15());
		this.getFormHM().put("approved_delete", this.getApproved_delete());
		this.getFormHM().put("date_count", this.getDate_count());
		this.getFormHM().put("time_count", this.getTime_count());
		this.getFormHM().put("hr_count", this.getHr_count());
		this.getFormHM().put("start_time_h", this.getStart_time_h());
		this.getFormHM().put("start_time_m", this.getStart_time_m());
		this.getFormHM().put("infoStr", this.getInfoStr());

		this.getFormHM().put("message", this.getMessage());		

		this.getFormHM().put("message", this.getMessage());
		this.getFormHM().put("scope_start_time", this.getScope_start_time());
		this.getFormHM().put("scope_end_time", this.getScope_end_time());
		this.getFormHM().put("sub_page", this.getSub_page());
		this.getFormHM().put("app_start_date", this.getApp_start_date());
		this.getFormHM().put("app_end_date", this.getApp_end_date());
		this.getFormHM().put("select_type", this.getSelect_type());
		this.getFormHM().put("leftdays", this.getLeftdays());
		
		this.getFormHM().put("appReaCode",this.appReaCode);
		this.getFormHM().put("appReaCodesetid", this.getAppReaCodesetid());
		this.getFormHM().put("appReaField", this.getAppReaField());
		
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("importMsg", this.getImportMsg());
		this.getFormHM().put("desc", this.getDesc());
		
		this.getFormHM().put("isExistIftoRest", this.getIsExistIftoRest());
		this.getFormHM().put("IftoRest", this.getIftoRest());
	}
	private void resetPagination() {
        if (this.getPagination() != null) {
            this.getPagination().firstPage();
            this.pagerows = 21;
        }
    }

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
		if ("/kq/app_check_in/sumapproval".equals(arg0.getPath()) && arg1.getParameter("b_query") != null) {
            resetPagination();
        }
		if ("/kq/app_check_in/all_app".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			resetPagination();
			this.setSeal_date("");
			this.setFlag("5");
			this.getFormHM().put("flag", this.getFlag());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			if (hm != null) {
				hm.put("code", "");
				hm.put("kind", "");
				hm.remove("code");
				hm.remove("kind");
			}
			this.getFormHM().put("code", "");
			this.getFormHM().put("kind", "");
			this.setCode("");
			this.setKind("");
			this.getFormHM().put("start_date", "");
			this.getFormHM().put("end_date", "");
			this.getFormHM().put("select_name", "");
			this.getFormHM().put("start_date", "");
			this.getFormHM().put("select_flag", "");
			this.getFormHM().put("select_pre", "");
			this.getFormHM().put("showtype", "");
			this.getFormHM().put("sp_flag", "");
			this.setSp_flag("");
			this.setShowtype("");
			this.setStart_date("");
			this.setEnd_date("");
			this.setSelect_name("");
			this.setSelect_flag("");
			this.setSelect_pre("");
		}
		if ("/kq/app_check_in/add_app".equals(arg0.getPath())
				&& arg1.getParameter("b_save") != null) {
			resetPagination();
		}
		if ("/kq/app_check_in/all_app".equals(arg0.getPath())
				&& arg1.getParameter("b_delete") != null) {
			this.setFlag("4");
			this.getFormHM().put("flag", this.getFlag());
		}
		/**
		 * 新建
		 */
		if ("/kq/app_check_in/add_app".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setFlag("1");
			this.getFormHM().put("flag", this.getFlag());
		}
		if ("/kq/app_check_in/add_app".equals(arg0.getPath())
				&& arg1.getParameter("b_change") != null) {
			this.setFlag("2");
			this.getFormHM().put("flag", this.getFlag());
		}
		/**
		 * 新建(条件选择)
		 */
		if ("/kq/app_check_in/manuselect".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.setFlag("1");
			this.getFormHM().put("flag", this.getFlag());
			resetPagination();
		}
		/**
		 * 查阅
		 */
		if ("/kq/app_check_in/view_app".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setFlag("0");
			this.getFormHM().put("flag", this.getFlag());
		}
		/**
		 * 修改
		 */
		if ("/kq/app_check_in/change_app".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setFlag("0");
			this.getFormHM().put("spFlag", "");
			this.getFormHM().put("flag", this.getFlag());
		}
		if ("/kq/app_check_in/change_app".equals(arg0.getPath())
				&& arg1.getParameter("b_update") != null) {
			this.getFormHM().put("sp", "1");
		}
		if ("/kq/app_check_in/change_app".equals(arg0.getPath())
				&& arg1.getParameter("b_reject") != null) {
			this.getFormHM().put("sp", "2");
		}
		if ("/kq/app_check_in/change_app".equals(arg0.getPath())
				&& arg1.getParameter("b_report") != null) {
			this.getFormHM().put("sp", "3");
		}
		/**
		 * 手工方式
		 */
		if ("/kq/app_check_in/manuselect".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setSelectflag("0");
			this.getFormHM().put("selectflag", this.getSelectflag());
			resetPagination();
		}
		/**
		 * 条件方式
		 */
		if ("/kq/app_check_in/querycon".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setSelectflag("1");
			this.getFormHM().put("selectflag", this.getSelectflag());
		}
		if ("/kq/app_check_in/all_app_data".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			/*
			 * if(this.getPagination()!=null) this.getPagination().firstPage();
			 */
			resetPagination();
		}
		if ("/kq/app_check_in/all_app_data".equals(arg0.getPath())
				&& arg1.getParameter("b_manu") != null) {
			resetPagination();
			this.setA_code("");
		}
		//废除
		if ("/kq/app_check_in/all_app_data".equals(arg0.getPath())
				&& arg1.getParameter("b_abate") != null) {
			resetPagination();
			//System.out.println(getTable()); bug7261
			arg1.setAttribute("formpath", "/kq/app_check_in/all_app.jsp");
		}

		// 加班申请、请假申请、公出申请，点击组织机构树时页签定位到第一页
		if ("/kq/app_check_in/all_app_data".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null
				&& arg1.getParameter("jump") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}

		// 调班申请，点击组织机构树时页签定位到第一页
		if ("/kq/app_check_in/exchange_class/exchange".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null
				&& arg1.getParameter("jump") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}

		// 调休申请，点击组织机构树时页签定位到第一页
		if ("/kq/app_check_in/redeploy_rest/redeploy".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null
				&& arg1.getParameter("jump") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}
		if("/kq/app_check_in/manuselect".equals(arg0.getPath())){
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}
		if("/kq/app_check_in/querycon".equals(arg0.getPath())){
			resetPagination();
		}
		if("/kq/app_check_in/conselect".equals(arg0.getPath())
				&& arg1.getParameter("b_pres") != null){
			resetPagination();
		}
		// 业务历史数据 查询
		if("/kq/register/history/app_check".equals(arg0.getPath())){
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}
		// 业务历史数据 查询
		if("/kq/register/history/tab_check".equals(arg0.getPath())
				&& arg1.getParameter("b_tab") != null){
			resetPagination();
		}
		
		return super.validate(arg0, arg1);
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
		this.sp_field = table.toLowerCase() + "z5";
		this.key_field = table.toLowerCase() + "01";
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;

	}

	public ArrayList getViewlist() {
		return viewlist;
	}

	public void setViewlist(ArrayList viewlist) {
		this.viewlist = viewlist;
	}

	public ArrayList getDurationlist() {
		return durationlist;
	}

	public void setDurationlist(ArrayList durationlist) {
		this.durationlist = durationlist;
	}

	public ArrayList getInfolist() {
		return infolist;
	}

	public void setInfolist(ArrayList infolist) {
		this.infolist = infolist;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getSelectflag() {
		return selectflag;
	}

	public void setSelectflag(String selectflag) {
		this.selectflag = selectflag;
	}

	public ArrayList getFactorlist() {
		return factorlist;
	}

	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public ArrayList getLogiclist() {
		return logiclist;
	}

	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}

	public ArrayList getOperlist() {
		return operlist;
	}

	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
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

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getCond_str() {
		return cond_str;
	}

	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}

	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}
	
	public String getSql_excle()
    {
        return sql_excle;
    }

    public void setSql_excle(String sql_excle)
    {
        this.sql_excle = sql_excle;
    }

    public int getCurrent()
    {
        return current;
    }

    public void setCurrent(int current)
    {
        this.current = current;
    }

    public PaginationForm getAppForm() {
		return appForm;
	}

	public void setAppForm(PaginationForm appForm) {
		this.appForm = appForm;
	}

	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}

	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public ArrayList getSalist() {
		return salist;
	}

	public void setSalist(ArrayList salist) {
		this.salist = salist;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public ArrayList getSelectedinfolist() {
		return selectedinfolist;
	}

	public void setSelectedinfolist(ArrayList selectedinfolist) {
		this.selectedinfolist = selectedinfolist;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getVisi() {
		return visi;
	}

	public void setVisi(String visi) {
		this.visi = visi;
	}

	public String getCond_order() {
		return cond_order;
	}

	public void setCond_order(String cond_order) {
		this.cond_order = cond_order;
	}

	public String getShowtype() {
		return showtype;
	}

	public void setShowtype(String showtype) {
		this.showtype = showtype;
	}

	public ArrayList getShowtypelist() {
		return showtypelist;
	}

	public void setShowtypelist(ArrayList showtypelist) {
		this.showtypelist = showtypelist;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getAudit_flag() {
		return audit_flag;
	}

	public void setAudit_flag(String audit_flag) {
		this.audit_flag = audit_flag;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
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

	public String getQuery_type() {
		return query_type;
	}

	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public ArrayList getSplist() {
		return splist;
	}

	public void setSplist(ArrayList splist) {
		this.splist = splist;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		if (this.getSp_flag() == null || this.getSp_flag().length() <= 0)
			this.setSp_flag("all");
		if (this.getShowtype() == null || this.getShowtype().length() <= 0)
			this.setShowtype("all");
		super.reset(arg0, arg1);
		setDate_count("1");// 天数
		setHr_count("1");// 小时数
		setInfoStr("");
		setMess1("");
		setMessage("");
		setRadio("0");
		setScope_start_time(OperateDate.dateToStr(new Date(), "yyyy-MM-dd HH:mm"));
	}

	public String getKey_field() {
		return key_field;
	}

	public void setKey_field(String key_field) {
		this.key_field = key_field;
	}

	public String getSp_field() {
		return sp_field;
	}

	public void setSp_field(String sp_field) {
		this.sp_field = sp_field;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
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

	public String getSelect_pre() {
		return select_pre;
	}

	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}

	public String getSelect_name() {
		return select_name;
	}

	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}

	public String getZ5() {
		return z5;
	}

	public void setZ5(String z5) {
		this.z5 = z5;
	}

	public String getSelect_time_type() {
		return select_time_type;
	}

	public void setSelect_time_type(String select_time_type) {
		this.select_time_type = select_time_type;
	}

	public String getColumnstr_s() {
		return columnstr_s;
	}

	public void setColumnstr_s(String columnstr_s) {
		this.columnstr_s = columnstr_s;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getOrdeby_s() {
		return ordeby_s;
	}

	public void setOrdeby_s(String ordeby_s) {
		this.ordeby_s = ordeby_s;
	}

	public String getWherestr_s() {
		return wherestr_s;
	}

	public void setWherestr_s(String wherestr_s) {
		this.wherestr_s = wherestr_s;
	}

	public ArrayList getSelectedlist() {
		return selectedlist;
	}

	public void setSelectedlist(ArrayList selectedlist) {
		if (selectedlist == null)
			selectedlist = new ArrayList();
		this.selectedlist = selectedlist;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String getFull() {
		return full;
	}

	public void setFull(String full) {
		this.full = full;
	}

	public String getDotflag() {
		return dotflag;
	}

	public void setDotflag(String dotflag) {
		this.dotflag = dotflag;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getMess1() {
		return mess1;
	}

	public void setMess1(String mess1) {
		this.mess1 = mess1;
	}

	public String getMess2() {
		return mess2;
	}

	public void setMess2(String mess2) {
		this.mess2 = mess2;
	}

	public ArrayList getSalistko() {
		return salistko;
	}

	public void setSalistko(ArrayList salistko) {
		this.salistko = salistko;
	}

	public ArrayList getSalist11() {
		return salist11;
	}

	public void setSalist11(ArrayList salist11) {
		this.salist11 = salist11;
	}

	public String getBflag() {
		return bflag;
	}

	public void setBflag(String bflag) {
		this.bflag = bflag;
	}

	public ArrayList getGroup11() {
		return group11;
	}

	public void setGroup11(ArrayList group11) {
		this.group11 = group11;
	}

	public ArrayList getGroup15() {
		return group15;
	}

	public void setGroup15(ArrayList group15) {
		this.group15 = group15;
	}

	public String getUnit11() {
		return unit11;
	}

	public void setUnit11(String unit11) {
		this.unit11 = unit11;
	}

	public String getUnit15() {
		return unit15;
	}

	public void setUnit15(String unit15) {
		this.unit15 = unit15;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
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

	public String getDate_count() {
		return date_count;
	}

	public void setDate_count(String date_count) {
		this.date_count = date_count;
	}

	public String getTime_count() {
		return time_count;
	}

	public void setTime_count(String time_count) {
		this.time_count = time_count;
	}

	public String getHr_count() {
		return hr_count;
	}

	public void setHr_count(String hr_count) {
		this.hr_count = hr_count;
	}

	public String getStart_time_h() {
		return start_time_h;
	}

	public void setStart_time_h(String start_time_h) {
		this.start_time_h = start_time_h;
	}

	public String getStart_time_m() {
		return start_time_m;
	}

	public void setStart_time_m(String start_time_m) {
		this.start_time_m = start_time_m;
	}

	public String getInfoStr() {
		return infoStr;
	}

	public void setInfoStr(String infoStr) {
		this.infoStr = infoStr;
	}


	public String getIsAllow() {
		return isAllow;
	}

	public void setIsAllow(String isAllow) {
		this.isAllow = isAllow;
	}


	public String getScope_start_time() {
		return scope_start_time;
	}

	public void setScope_start_time(String scope_start_time) {
		this.scope_start_time = scope_start_time;
	}

	public String getScope_end_time() {
		return scope_end_time;
	}

	public void setScope_end_time(String scope_end_time) {
		this.scope_end_time = scope_end_time;
	}

	public ArrayList getSelectFieldList() {
		return selectFieldList;
	}

	public void setSelectFieldList(ArrayList selectFieldList) {
		this.selectFieldList = selectFieldList;
	}

	public ArrayList getExcelFieldList() {
		return excelFieldList;
	}

	public void setExcelFieldList(ArrayList excelFieldList) {
		this.excelFieldList = excelFieldList;
	}

	public String getViewPost() {
		return viewPost;
	}

	public void setViewPost(String viewPost) {
		this.viewPost = viewPost;
	}

	public String getApp_start_date() {
		return app_start_date;
	}

	public void setApp_start_date(String app_start_date) {
		this.app_start_date = app_start_date;
	}

	public String getApp_end_date() {
		return app_end_date;
	}

	public void setApp_end_date(String app_end_date) {
		this.app_end_date = app_end_date;
	}

	public String getSelect_type() {
		return select_type;
	}

	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}

	public String getApplytime()
	{
		return applytime;
	}

	public void setApplytime(String applytime)
	{
		this.applytime = applytime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
