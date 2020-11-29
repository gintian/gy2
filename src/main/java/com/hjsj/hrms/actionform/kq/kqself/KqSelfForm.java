/*
 * Created on 2006-3-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.kq.kqself;

import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author wxh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class KqSelfForm extends FrameForm {
	/** 表名 kq_overtime,kq_leave,kq_busi_away */
	private String table = "";
	/** 申请记录的字段列表 */
	private String sels;

	private String com;

	private String sql;

	private String where;
	/** 起始终止日期 */
	private String start_date;
	private String end_date;
	private int cols = 0;
	/* s申请时间段 */
	private String start_d; // 开始时间
	private String end_d; // 结束时间
	private String date_count; // 申请的天数
	private String time_count; // 申请的小时数
	private String start_time_h; // 开始时间的 小时
	private String start_time_m; // 开始时间的 分钟
	private String class_id; // 班次id
	private String app_way; // 选择是按照小时还是天 来申请
	private String app_reason;// 申请原因

	private String scope_start_time;
	private String scope_end_time;
	/** 审批标志字段 */
	private String sp_field;
	/** 主键字段 */
	private String key_field;
	private ArrayList flist = new ArrayList();

	private ArrayList fieldlist = new ArrayList();
	private ArrayList yearlist = new ArrayList();
	private ArrayList durationlist = new ArrayList();
	/** 考勤年份 */
	private String kq_year = "";
	/** 考勤区间 */
	private String kq_duration = "";
	private ArrayList selist = new ArrayList();
	private PaginationForm delListForm = new PaginationForm();
	private String seal_date;
	private RecordVo cancelvo = new RecordVo("q15");
	private String rule_day;
	private String order;

	/** *新加复杂申请** */
	private String app_fashion = "";// 申请方式0:简单,1复杂
	private ArrayList class_list = new ArrayList();// 班次的list
	private String intricacy_app_start_date;// 复杂申请的开始日期
	private String intricacy_app_end_date;// 复杂申请的结束日期
	private String intricacy_app_start_time;// 复杂申请开始时间
	private String intricacy_app_end_time;// 复杂申请结束时间
	private String intricacy_app_fashion;// 复杂申请方式0:每天一次,1:每公休日一次
	private String id;
	private HashMap taskMap = new HashMap();
	private String taskurl;
	private String dert_itemid;
	private String dert_value;
	private String isTemplate = "0";
	private ArrayList vo_list;
	private ArrayList vo_list2;
	private String usableTime;
	private String allOverTime;
	private String haveUsedTime;
	private String start_time;
	private String end_time;
	
	private String appReaCode;//加班原因代码
	private String appReaCodesetid;//加班原因代码项
	private String appReaField;
	
	private ArrayList appStatusList;
	private String select_flag;
	private String field;//是否调休字段
	
	private String isExistIftoRest;//q11是否存在是否调休字段
	private String IftoRest;//是否调休
	
	private String kqempcal;//我的考勤日历参数
	
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

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public ArrayList getAppStatusList() {
		return appStatusList;
	}

	public void setAppStatusList(ArrayList appStatusList) {
		this.appStatusList = appStatusList;
	}

	public String getSelect_flag() {
		return select_flag;
	}

	public void setSelect_flag(String selectFlag) {
		select_flag = selectFlag;
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

	public String getAllOverTime()
    {
        return allOverTime;
    }

    public void setAllOverTime(String allOverTime)
    {
        this.allOverTime = allOverTime;
    }

    public String getHaveUsedTime()
    {
        return haveUsedTime;
    }

    public void setHaveUsedTime(String haveUsedTime)
    {
        this.haveUsedTime = haveUsedTime;
    }

    public ArrayList getVo_list2()
    {
        return vo_list2;
    }

    public void setVo_list2(ArrayList vo_list2)
    {
        this.vo_list2 = vo_list2;
    }

    public String getUsableTime()
    {
        return usableTime;
    }

    public void setUsableTime(String usableTime)
    {
        this.usableTime = usableTime;
    }
    
    

    public String getStart_time()
    {
        return start_time;
    }

    public void setStart_time(String start_time)
    {
        this.start_time = start_time;
    }

    public String getEnd_time()
    {
        return end_time;
    }

    public void setEnd_time(String end_time)
    {
        this.end_time = end_time;
    }

    public ArrayList getVo_list()
    {
        return vo_list;
    }

    public void setVo_list(ArrayList vo_list)
    {
        this.vo_list = vo_list;
    }

    public String getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(String isTemplate) {
		this.isTemplate = isTemplate;
	}

	public String getDert_itemid() {
		return dert_itemid;
	}

	public void setDert_itemid(String dert_itemid) {
		this.dert_itemid = dert_itemid;
	}

	public String getDert_value() {
		return dert_value;
	}

	public void setDert_value(String dert_value) {
		this.dert_value = dert_value;
	}

	/** ******结束******** */
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getRule_day() {
		return rule_day;
	}

	public void setRule_day(String rule_day) {
		this.rule_day = rule_day;
	}

	public String getSeal_date() {
		return seal_date;
	}

	public void setSeal_date(String seal_date) {
		this.seal_date = seal_date;
	}

	@Override
    public void outPutFormHM() {
		this.setTable((String) this.getFormHM().get("table"));
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setSelist((ArrayList) this.getFormHM().get("selist"));
		this.setYearlist((ArrayList) this.getFormHM().get("yearlist"));
		this.setDurationlist((ArrayList) this.getFormHM().get("durationlist"));
		this.setKq_year((String) this.getFormHM().get("kq_year"));
		this.setKq_duration((String) this.getFormHM().get("kq_duration"));
		this.setFlist((ArrayList) this.getFormHM().get("flist"));
		this.setCom((String) this.getFormHM().get("com"));
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setSels((String) this.getFormHM().get("sels"));
		this.setSeal_date((String) this.getFormHM().get("seal_date"));
		this.setCancelvo((RecordVo) this.getFormHM().get("cancelvo"));
		this.setRule_day((String) this.getFormHM().get("rule_day"));
		this.setOrder((String) this.getFormHM().get("order"));
		/** *新加复杂申请** */
		this.setApp_fashion((String) this.getFormHM().get("app_fashion"));
		this.setClass_list((ArrayList) this.getFormHM().get("class_list"));
		this.setIntricacy_app_start_date((String) this.getFormHM().get(
				"intricacy_app_start_date"));
		this.setIntricacy_app_end_date((String) this.getFormHM().get(
				"intricacy_app_end_date"));
		this.setIntricacy_app_start_time((String) this.getFormHM().get(
				"intricacy_app_start_time"));
		this.setIntricacy_app_end_time((String) this.getFormHM().get(
				"intricacy_app_end_time"));
		this.setIntricacy_app_fashion((String) this.getFormHM().get(
				"intricacy_app_fashion"));
		this.setId((String) this.getFormHM().get("id"));
		this.setDert_itemid((String) this.getFormHM().get("dert_itemid"));
		this.setIsTemplate((String) this.getFormHM().get("isTemplate"));
		this.setStart_d((String) this.getFormHM().get("start_d"));
		this.setScope_start_time((String) this.getFormHM().get("scope_start_time"));
		this.setVo_list((ArrayList)this.getFormHM().get("vo_list"));
		this.setVo_list2((ArrayList)this.getFormHM().get("vo_list2"));
		this.setStart_time((String)this.getFormHM().get("start_time"));
		this.setEnd_time((String)this.getFormHM().get("end_time"));
		this.setAllOverTime((String)this.getFormHM().get("allOverTime"));
		this.setHaveUsedTime((String)this.getFormHM().get("haveUsedTime"));
		this.setScope_end_time((String) this.getFormHM().get("scope_end_time"));
		this.setAppReaCode((String)this.getFormHM().get("appReaCode"));
		this.setAppReaCodesetid((String)this.getFormHM().get("appReaCodesetid"));
		this.setAppReaField((String)this.getFormHM().get("appReaField"));
		this.setAppStatusList((ArrayList)this.getFormHM().get("appStatusList"));
		this.setSelect_flag((String)this.getFormHM().get("select_flag"));
		this.setField((String)this.getFormHM().get("field"));
		
		this.setIsExistIftoRest((String)this.getFormHM().get("isExistIftoRest"));
		this.setIftoRest((String)this.getFormHM().get("IftoRest"));
		
		this.setKqempcal((String)this.getFormHM().get("kqempcal"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("table", this.getTable());
		this.getFormHM().put("sels", this.getSels());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("kq_year", (String) this.getKq_year());
		this.getFormHM().put("kq_duration", (String) this.getKq_duration());
		if (this.getPagination() != null)
			this.getFormHM().put("dellist",
					(ArrayList) this.getPagination().getSelectedList());
		this.getFormHM().put("start_date", getStart_date());
		this.getFormHM().put("end_date", getEnd_date());
		this.getFormHM().put("seal_date", this.getSeal_date());
		this.getFormHM().put("cancelvo", this.getCancelvo());
		/** *新加复杂申请** */
		this.getFormHM().put("app_fashion", this.getApp_fashion());
		this.getFormHM().put("intricacy_app_start_date",
				this.getIntricacy_app_start_date());
		this.getFormHM().put("intricacy_app_end_date",
				this.getIntricacy_app_end_date());
		this.getFormHM().put("intricacy_app_start_time",
				this.getIntricacy_app_start_time());
		this.getFormHM().put("intricacy_app_end_time",
				this.getIntricacy_app_end_time());
		this.getFormHM().put("intricacy_app_fashion",
				this.getIntricacy_app_fashion());
		this.getFormHM().put("dert_value", this.getDert_value());

		this.getFormHM().put("start_d", this.getStart_d());
		this.getFormHM().put("end_d", this.getEnd_d());
		this.getFormHM().put("date_count", this.getDate_count()); // 申请的天数
		this.getFormHM().put("time_count", this.getTime_count());// 申请的小时数
		this.getFormHM().put("start_time_h", this.getStart_time_h());// 开始时间的
		// 小时
		this.getFormHM().put("start_time_m", this.getStart_time_m());// 开始时间的
		// 分钟
		this.getFormHM().put("class_id", this.getClass_id());// 班次id
		this.getFormHM().put("app_way", this.getApp_way());// 选择是按照小时还是天 来申请
		this.getFormHM().put("app_reason", this.getApp_reason());// 申请原因
		this.getFormHM().put("scope_start_time", this.getScope_start_time());
		this.getFormHM().put("scope_end_time", this.getScope_end_time());
		this.getFormHM().put("vo_list", this.getVo_list());
		this.getFormHM().put("vo_list2", this.getVo_list2());
		this.getFormHM().put("start_time", this.getStart_time());
		this.getFormHM().put("end_time", this.getEnd_time());
		this.getFormHM().put("allOverTime", this.getAllOverTime());
		this.getFormHM().put("haveUsedTime", this.getHaveUsedTime());
		
		this.getFormHM().put("appReaCode",this.appReaCode);
		this.getFormHM().put("appReaCodesetid", this.getAppReaCodesetid());
		this.getFormHM().put("appReaField", this.getAppReaField());
		this.getFormHM().put("select_flag", this.getSelect_flag());
		this.getFormHM().put("appStatusList", this.getAppStatusList());
		
		this.getFormHM().put("isExistIftoRest", this.getIsExistIftoRest());
		this.getFormHM().put("IftoRest", this.getIftoRest());
		
		this.getFormHM().put("kqempcal", this.getKqempcal());
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setStart_d(OperateDate.dateToStr(new Date(), "yyyy-MM-dd"));
		this.setEnd_d("");
		/* s申请时间段 */
		this.setDate_count("1"); // 申请的天数
		this.setTime_count("1"); // 申请的小时数
		this.setStart_time_h("00"); // 开始时间的 小时
		this.setStart_time_m("00"); // 开始时间的 分钟
		this.setClass_id("#");// 班次id
		this.setApp_way(""); // 选择是按照小时还是天 来申请
		this.setApp_reason("");// 申请原因
		this.setApp_way("0");
		//this.setScope_start_time(OperateDate.dateToStr(new Date(),"yyyy-MM-dd HH:mm"));
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
		this.sp_field = table.toLowerCase() + "z5";
		this.key_field = table.toLowerCase() + "01";
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getSelist() {
		return selist;
	}

	public void setSelist(ArrayList selist) {
		this.selist = selist;
	}

	public String getSels() {
		return sels;
	}

	public void setSels(String sels) {
		this.sels = sels;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public ArrayList getFlist() {
		return flist;
	}

	public void setFlist(ArrayList flist) {
		this.flist = flist;
		if(flist!=null)
			this.cols = flist.size() + 2;
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

	public PaginationForm getDelListForm() {
		return delListForm;
	}

	public void setDelListForm(PaginationForm delListForm) {
		this.delListForm = delListForm;
	}

	public ArrayList getDurationlist() {
		return durationlist;
	}

	public void setDurationlist(ArrayList durationlist) {
		this.durationlist = durationlist;
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

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public String getSp_field() {
		return sp_field;
	}

	public void setSp_field(String sp_field) {
		this.sp_field = sp_field;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		String path = arg0.getPath();
		if ("/kq/kqself/addkqself".equalsIgnoreCase(path)
				&& arg1.getParameter("b_save") != null) {
			this.getFormHM().put("sp_flag", "01");
		}
		if ("/kq/kqself/search_kqself".equalsIgnoreCase(path)
				&& arg1.getParameter("b_update") != null) {
			this.getFormHM().put("sp_flag", "01");
		}
		if (("/kq/kqself/addkqself".equalsIgnoreCase(path) || "/kq/kqself/search_kqself"
				.equalsIgnoreCase(path))
				&& arg1.getParameter("b_appeal") != null) {
			this.getFormHM().put("sp_flag", "02");
		}
		if (("/kq/kqself/addkqself".equalsIgnoreCase(path) || "/kq/kqself/search_kqself"
				.equalsIgnoreCase(path))
				&& arg1.getParameter("b_audit") != null) {
			this.getFormHM().put("sp_flag", "08");
		}
		if (("/kq/kqself/addkqself".equalsIgnoreCase(path) || "/kq/kqself/search_kqself"
				.equalsIgnoreCase(path))
				&& arg1.getParameter("b_report") != null) {
			this.getFormHM().put("sp_flag", "08");
		}
		if (("/kq/kqself/addkqself".equalsIgnoreCase(path) || "/kq/kqself/search_kqself"
				.equalsIgnoreCase(path))
				&& arg1.getParameter("b_approve") != null) {
			this.getFormHM().put("sp_flag", "02");
		}
		if ("/kq/kqself/search_kqself".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
			this.setSeal_date("");
		}
		if ("/kq/kqself/search_kqself".equalsIgnoreCase(path)
				&& (arg1.getParameter("b_update") != null || arg1
						.getParameter("b_add") != null)) {
			this.setTaskurl(path + ".do");
			doSelectItem(arg1);
		}
		if ("/kq/kqself/search_kqself".equalsIgnoreCase(path)
				&& arg1.getParameter("b_add") != null) {
			this.getFormHM().put("id", "");
			this.setId("");
		}
		return super.validate(arg0, arg1);
	}

	public String getKey_field() {
		return key_field;
	}

	public void setKey_field(String key_field) {
		this.key_field = key_field;
	}

	public RecordVo getCancelvo() {
		return cancelvo;
	}

	public void setCancelvo(RecordVo cancelvo) {
		this.cancelvo = cancelvo;
	}

	public String getApp_fashion() {
		return app_fashion;
	}

	public void setApp_fashion(String app_fashion) {
		this.app_fashion = app_fashion;
	}

	public ArrayList getClass_list() {
		return class_list;
	}

	public void setClass_list(ArrayList class_list) {
		this.class_list = class_list;
	}

	public String getIntricacy_app_end_date() {
		return intricacy_app_end_date;
	}

	public void setIntricacy_app_end_date(String intricacy_app_end_date) {
		this.intricacy_app_end_date = intricacy_app_end_date;
	}

	public String getIntricacy_app_end_time() {
		return intricacy_app_end_time;
	}

	public void setIntricacy_app_end_time(String intricacy_app_end_time) {
		this.intricacy_app_end_time = intricacy_app_end_time;
	}

	public String getIntricacy_app_fashion() {
		return intricacy_app_fashion;
	}

	public void setIntricacy_app_fashion(String intricacy_app_fashion) {
		this.intricacy_app_fashion = intricacy_app_fashion;
	}

	public String getIntricacy_app_start_date() {
		return intricacy_app_start_date;
	}

	public void setIntricacy_app_start_date(String intricacy_app_start_date) {
		this.intricacy_app_start_date = intricacy_app_start_date;
	}

	public String getIntricacy_app_start_time() {
		return intricacy_app_start_time;
	}

	public void setIntricacy_app_start_time(String intricacy_app_start_time) {
		this.intricacy_app_start_time = intricacy_app_start_time;
	}

	public HashMap getTaskMap() {
		return taskMap;
	}

	/**
	 * 处理值
	 * 
	 * @param request
	 */
	public void doSelectItem(HttpServletRequest request) {
		Map mp = request.getParameterMap();
		Set sk = mp.keySet();
		HashMap hmSave = new HashMap();
		Iterator iterator = sk.iterator();
		while (iterator.hasNext()) {
			String typeKey = iterator.next().toString();
			String typeValue = request.getParameter(typeKey).toString();
			hmSave.put(typeKey, typeValue);
		}
		this.setTaskMap(hmSave);
	}

	public void setTaskMap(HashMap taskMap) {
		this.taskMap = taskMap;
	}

	public String getTaskurl() {
		return taskurl;
	}

	public void setTaskurl(String taskurl) {
		this.taskurl = taskurl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getClass_id() {
		return class_id;
	}

	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}

	public String getApp_way() {
		return app_way;
	}

	public void setApp_way(String app_way) {
		this.app_way = app_way;
	}

	public String getApp_reason() {
		return app_reason;
	}

	public void setApp_reason(String app_reason) {
		this.app_reason = app_reason;
	}

	public String getStart_d() {
		return start_d;
	}

	public void setStart_d(String start_d) {
		this.start_d = start_d;
	}

	public String getEnd_d() {
		return end_d;
	}

	public void setEnd_d(String end_d) {
		this.end_d = end_d;
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

	public void setKqempcal(String kqempcal) {
		this.kqempcal = kqempcal;
	}

	public String getKqempcal() {
		return kqempcal;
	}

}
