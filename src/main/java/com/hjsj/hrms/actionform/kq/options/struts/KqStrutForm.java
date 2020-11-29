package com.hjsj.hrms.actionform.kq.options.struts;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class KqStrutForm extends FrameForm {
	
	private String kq_cardno;
	
	private	String kq_type;
	
	private	String kq_g_no;
	
	private String kq_base;
	
	private String messi[];
	
	private String sige;
	
	private String one;
	
	private String ones;
	
	private String two;
	
	private String twos;
	
	private String thre;
	
	private String thres;
	
	private String four;
	
	private String fours;
	
	private ArrayList strutList =new ArrayList();
	private ArrayList nlist =new ArrayList();
	private ArrayList tlist =new ArrayList();
	
	private ArrayList selist =new ArrayList();
	
	private ArrayList slist =new ArrayList();
	
	private ArrayList par_list=new ArrayList();
	private ArrayList par_select_list=new ArrayList();
	private String par_mes[];
	private ArrayList holi_select_list=new ArrayList();
	private ArrayList holi_list=new ArrayList();
	private String[] holi_mes;
	private String over_rule;
	private String over_status;
	private String leave_rule;//提前请假天数
	private String leave_status;
	private PaginationForm kqStrutForm=new PaginationForm();
	private String restleave_type;
	private String flextime_ruler;//弹性班规则
	private String rest_kqclass;
	private String opinion_overtime_type;
	private String rest_overtime_time;//节假日或公休日加班申请是否允许申请多次
	private ArrayList class_list=new ArrayList();
	private String min_mid_leave_time;//单次进出小于等于X分钟不计离岗时间
	private String card_interval;//重复刷卡间隔X分钟
	private String cardearly;//提前多少分钟算作早到
	private String min_overtime;//加班时间不少于X分钟
	private String overtime_max_limit; //期间最大加班限额
	private String need_busicompare;//业务申请是否与实际刷卡作比对
	private String busi_cardbegin;//刷卡开始时间最早从申请起始时间前X分钟起
	private String busi_cardend;//刷卡结束时间最迟到申请结束时间后X分钟止：   
    private String busifact_diff;//6、申请时长小于刷卡时长超过X分钟计为异常 BUSIFACT_DIFF
    private String busi_morethan_fact;//7、申请时长大于刷卡时长超过X分钟计为异常BUSI_MORETHAN_FACT
    private String card_causation;//补刷卡原因
    private String repair_card_status;
    private String repair_card_num;//补刷卡次数
    private String leave_rule_late;//请假登记最迟XX天
    private String leave_rule_late_status;
    private String pigeonhole_type;//归档方式是否审批
    private String up_dailyregister;//修改日明细登记数据
    private String magcard_setid;//读取数据人员子集id    
    private ArrayList setList=new ArrayList();//人员子集
    private String magcard_flag;
    private String magcard_cardid;
    private String magcard_com;
    private String checkControl_content;//月审核方式   0：强制控制；1：预警提示
    private String checkControl_status;//月审核  0：不控制；1：控制
    private String self_accept_month_data;//需要员工确认月汇总数据 0：不需要 1：需要(默认)
    private String officeleave_enable_leave_overtime; //公出期间允许请假加班
    
    public String getCheckControl_content() {
		return checkControl_content;
	}

	public void setCheckControl_content(String checkControlContent) {
		checkControl_content = checkControlContent;
	}

	public String getCheckControl_status() {
		return checkControl_status;
	}

	public void setCheckControl_status(String checkControlStatus) {
		checkControl_status = checkControlStatus;
	}

	private ArrayList cardlist=new ArrayList();
    private ArrayList relationlist = new ArrayList();
    private ArrayList templateList=new ArrayList();
    private PaginationForm templateListForm=new PaginationForm();   
    private FormFile importfile;   
    private String template_type;
    private String path="";
    private String tab_name="";
    private String check_inout_match="";
    private String net_sign_check_ip;    //网上签到是否限制IP
    private String net_sign_approve; //网上签到 签到签退数据需审批 0：无需审批 1：需要审批
    private String logon_kq_hint;
    /**统计项目 Q03 **/
    private ArrayList statq03_list=new ArrayList();  //统计项目 Q03
    private ArrayList par_statq03_list=new ArrayList();
    private String stat_q03[];
    /**首钢考勤薄 从Q03取出**/
    private ArrayList timecard03_list=new ArrayList();  //考勤薄  Q03
    private ArrayList kq_timecard03_list=new ArrayList();
    private String kqcard_q03[];
    /**班组指标**/
    private ArrayList bzlist =new ArrayList();
    private String kq_bzindex;
    private String data_processing;  //数据处理模式 0:分用户处理 1:集中处理
    private String quick_analyse_mode; //启用精简处理方式
    private ArrayList thlist =new ArrayList();  //首钢调换班组，为网上签到;
    private String kq_thbzindex; //首钢调换班组，为网上签到;
    private String overtime_hol; //节假日有排班算作加班;0:不算加班；1：算加班
    private String approved_delete; //已批申请登记数据是否可以删除; 0:不删除；1：删除
    private String returnvalue="1";
    /**首钢考勤参数**/
    private ArrayList kq_datelist=new ArrayList();//考勤日期参数
    private String kq_startdate_field;//考勤开始日期
    private String kq_enddate_field;//考勤结束日期
    private String dept_changedate_field;//部门变动日期
    private String holiday_minus_rule;
    private String kq_orgview_post;//机构不显示岗位
    private String approve_relation;//审批关系
    
    public String getHoliday_minus_rule() {
		return holiday_minus_rule;
	}

	public void setHoliday_minus_rule(String holiday_minus_rule) {
		this.holiday_minus_rule = holiday_minus_rule;
	}

    private String sync_carddata="false";//同步刷卡数据
    private String sync_base="";//同步数据库
    private String sync_url="";//同步数据库地址
    private String sync_post="";//同步数据库端口
    private String sync_table="";//同步表
    private String sync_pass="";//密码
    private String sync_user="";//用户名
    private String sync_basetype="";//同步类型
    private ArrayList sync_list=new ArrayList();
    
    /**修改核二三同步数据 liwc*/
    private String syncxml_id="";//同步对数据源id
    private String syncxml_desc="";//同步对数据源描述
    private String syncxml_dbtype="";//同步数据库类型
    private String syncxml_ip="";//同步数据库ip地址
    private String syncxml_port="";//同步数据库端口
    private String syncxml_dbname="";//同步数据库名称
    private String syncxml_space="";//同步表空间
    private String syncxml_user="";//同步用户名
    private String syncxml_pwd="";//同步数据库密码
    private String syncxml_status="";//同步是否启用
    private String syncxml_related="";//同步关联指标
    private String syncxml_options="";//同步表
    private ArrayList syncxml_related_list;//同步关联指标列表
    private ArrayList syncxml_dbtype_list;//数据库列表
    private String syncxml_source;//数据源表
    private List connStrList;
    private String type;// 更新类型，1为保存，2为更新

    private String standard_hours="8"; //标准工时（小时） 默认8小时
    /*
     * 休息日刷卡转加班
     */
    private String turn_enable="0"; //启用标识 0 不启用 1 启用
    private String turn_charge="0"; //0 需要进出匹配 1 有刷卡即加班
    private String turn_tlong="0"; //刷卡时长  2 默认时长 1 参考班次时长 0 实际刷卡时长
    private String turn_time="8"; //刷卡时长 默认8小时
    private String turn_classid="#"; //被选班次
    private ArrayList turn_classlist = new ArrayList(); //班次列表
    private String turn_appdoc="0"; //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认
    /*
     *  调休加班
     */
    private ArrayList vacationTypeList;
    private String overtimeToOff;
    private String vacationToOff;
    private String validityTime;
    private ArrayList vacation_list;
    private ArrayList vacation_select_list;
    private String overtimeForLeaveCycle;
    private String overtimeForLeaveMaxHour;
    private ArrayList overtimeForLeaveCycleList;
    
    /*
     * 申请比对
     */
    private String leave_need_check;
    private String leave_compare_rule;
    private String leave_updata_data;
    
    private String overtime_need_check;
    private String overtime_compare_rule;
    private String overtime_updata_data;
    
    private String officeleave_need_check;
    private String officeleave_compare_rule;
    private String officeleave_updata_data;
    
    
	public String getLogon_kq_hint() {
		return logon_kq_hint;
	}

	public void setLogon_kq_hint(String logonKqHint) {
		logon_kq_hint = logonKqHint;
	}

	public String getLeave_updata_data() {
		return leave_updata_data;
	}

	public void setLeave_updata_data(String leaveUpdataData) {
		leave_updata_data = leaveUpdataData;
	}

	public String getOvertime_updata_data() {
		return overtime_updata_data;
	}

	public void setOvertime_updata_data(String overtimeUpdataData) {
		overtime_updata_data = overtimeUpdataData;
	}

	public String getOfficeleave_updata_data() {
		return officeleave_updata_data;
	}

	public void setOfficeleave_updata_data(String officeleaveUpdataData) {
		officeleave_updata_data = officeleaveUpdataData;
	}

	public String getLeave_need_check() {
		return leave_need_check;
	}

	public void setLeave_need_check(String leaveNeedCheck) {
		leave_need_check = leaveNeedCheck;
	}

	public String getLeave_compare_rule() {
		return leave_compare_rule;
	}

	public void setLeave_compare_rule(String leaveCompareRule) {
		leave_compare_rule = leaveCompareRule;
	}

	public String getOvertime_need_check() {
		return overtime_need_check;
	}

	public void setOvertime_need_check(String overtimeNeedCheck) {
		overtime_need_check = overtimeNeedCheck;
	}

	public String getOvertime_compare_rule() {
		return overtime_compare_rule;
	}

	public void setOvertime_compare_rule(String overtimeCompareRule) {
		overtime_compare_rule = overtimeCompareRule;
	}

	public String getOfficeleave_need_check() {
		return officeleave_need_check;
	}

	public void setOfficeleave_need_check(String officeleaveNeedCheck) {
		officeleave_need_check = officeleaveNeedCheck;
	}

	public String getOfficeleave_compare_rule() {
		return officeleave_compare_rule;
	}

	public void setOfficeleave_compare_rule(String officeleaveCompareRule) {
		officeleave_compare_rule = officeleaveCompareRule;
	}

	public ArrayList getVacation_list()
    {
        return vacation_list;
    }

    public void setVacation_list(ArrayList vacation_list)
    {
        this.vacation_list = vacation_list;
    }

    public ArrayList getVacation_select_list()
    {
        return vacation_select_list;
    }

    public void setVacation_select_list(ArrayList vacation_select_list)
    {
        this.vacation_select_list = vacation_select_list;
    }

    public ArrayList getVacationTypeList()
    {
        return vacationTypeList;
    }

    public void setVacationTypeList(ArrayList vacationTypeList)
    {
        this.vacationTypeList = vacationTypeList;
    }

    public String getOvertimeToOff()
    {
        return overtimeToOff;
    }

    public void setOvertimeToOff(String overtimeToOff)
    {
        this.overtimeToOff = overtimeToOff;
    }

    public String getVacationToOff()
    {
        return vacationToOff;
    }

    public void setVacationToOff(String vacationToOff)
    {
        this.vacationToOff = vacationToOff;
    }

    public String getValidityTime()
    {
        return validityTime;
    }

    public void setValidityTime(String validityTime)
    {
        this.validityTime = validityTime;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List getConnStrList() {
		return connStrList;
	}

	public void setConnStrList(List connStrList) {
		this.connStrList = connStrList;
	}

	public ArrayList getSyncxml_dbtype_list() {
		return syncxml_dbtype_list;
	}

	public void setSyncxml_dbtype_list(ArrayList syncxml_dbtype_list) {
		this.syncxml_dbtype_list = syncxml_dbtype_list;
	}

	public String getSyncxml_options() {
		return syncxml_options;
	}

	public void setSyncxml_options(String syncxml_options) {
		this.syncxml_options = syncxml_options;
	}

	public String getSyncxml_desc() {
		return syncxml_desc;
	}

	public void setSyncxml_desc(String syncxml_desc) {
		this.syncxml_desc = syncxml_desc;
	}

	public String getSyncxml_dbtype() {
		return syncxml_dbtype;
	}

	public void setSyncxml_dbtype(String syncxml_dbtype) {
		this.syncxml_dbtype = syncxml_dbtype;
	}

	public String getSyncxml_ip() {
		return syncxml_ip;
	}

	public void setSyncxml_ip(String syncxml_ip) {
		this.syncxml_ip = syncxml_ip;
	}

	public String getSyncxml_port() {
		return syncxml_port;
	}

	public void setSyncxml_port(String syncxml_port) {
		this.syncxml_port = syncxml_port;
	}

	public String getSyncxml_dbname() {
		return syncxml_dbname;
	}

	public void setSyncxml_dbname(String syncxml_dbname) {
		this.syncxml_dbname = syncxml_dbname;
	}

	public String getSyncxml_space() {
		return syncxml_space;
	}

	public void setSyncxml_space(String syncxml_space) {
		this.syncxml_space = syncxml_space;
	}

	public String getSyncxml_user() {
		return syncxml_user;
	}

	public void setSyncxml_user(String syncxml_user) {
		this.syncxml_user = syncxml_user;
	}

	public String getSyncxml_pwd() {
		return syncxml_pwd;
	}

	public void setSyncxml_pwd(String syncxml_pwd) {
		this.syncxml_pwd = syncxml_pwd;
	}

	public String getSyncxml_status() {
		return syncxml_status;
	}

	public void setSyncxml_status(String syncxml_status) {
		this.syncxml_status = syncxml_status;
	}

	public String getSyncxml_related() {
		return syncxml_related;
	}

	public void setSyncxml_related(String syncxml_related) {
		this.syncxml_related = syncxml_related;
	}

	public String getSync_carddata() {
		return sync_carddata;
	}

	public void setSync_carddata(String sync_carddata) {
		this.sync_carddata = sync_carddata;
	}

	public String getSync_base() {
		return sync_base;
	}

	public void setSync_base(String sync_base) {
		this.sync_base = sync_base;
	}

	public String getSync_url() {
		return sync_url;
	}

	public void setSync_url(String sync_url) {
		this.sync_url = sync_url;
	}

	public String getSync_post() {
		return sync_post;
	}

	public void setSync_post(String sync_post) {
		this.sync_post = sync_post;
	}

	public String getSync_table() {
		return sync_table;
	}

	public void setSync_table(String sync_table) {
		this.sync_table = sync_table;
	}

	public ArrayList getKq_datelist() {
		return kq_datelist;
	}

	public void setKq_datelist(ArrayList kq_datelist) {
		this.kq_datelist = kq_datelist;
	}

	public String getKq_startdate_field() {
		return kq_startdate_field;
	}

	public void setKq_startdate_field(String kq_startdate_field) {
		this.kq_startdate_field = kq_startdate_field;
	}

	public String getKq_enddate_field() {
		return kq_enddate_field;
	}

	public void setKq_enddate_field(String kq_enddate_field) {
		this.kq_enddate_field = kq_enddate_field;
	}

	public String getDept_changedate_field() {
		return dept_changedate_field;
	}

	public void setDept_changedate_field(String dept_changedate_field) {
		this.dept_changedate_field = dept_changedate_field;
	}

	public String getCheck_inout_match() {
		return check_inout_match;
	}

	public void setCheck_inout_match(String check_inout_match) {
		this.check_inout_match = check_inout_match;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTemplate_type() {
		return template_type;
	}

	public void setTemplate_type(String template_type) {
		this.template_type = template_type;
	}

	public FormFile getImportfile() {
		return importfile;
	}

	public void setImportfile(FormFile importfile) {
		this.importfile = importfile;
	}

	public ArrayList getCardlist() {
		return cardlist;
	}

	public void setCardlist(ArrayList cardlist) {
		this.cardlist = cardlist;
	}

	public ArrayList getRelationlist() {
		return relationlist;
	}

	public void setRelationlist(ArrayList relationlist) {
		this.relationlist = relationlist;
	}

	public String getMagcard_cardid() {
		return magcard_cardid;
	}

	public void setMagcard_cardid(String magcard_cardid) {
		this.magcard_cardid = magcard_cardid;
	}

	public String getMagcard_flag() {
		return magcard_flag;
	}

	public void setMagcard_flag(String magcard_flag) {
		this.magcard_flag = magcard_flag;
	}

	public String getUp_dailyregister() {
		return up_dailyregister;
	}

	public void setUp_dailyregister(String up_dailyregister) {
		this.up_dailyregister = up_dailyregister;
	}

	public String getPigeonhole_type() {
		return pigeonhole_type;
	}

	public void setPigeonhole_type(String pigeonhole_type) {
		this.pigeonhole_type = pigeonhole_type;
	}

	public ArrayList getClass_list() {
		return class_list;
	}

	public void setClass_list(ArrayList class_list) {
		this.class_list = class_list;
	}

	public String getRest_kqclass() {
		return rest_kqclass;
	}

	public void setRest_kqclass(String rest_kqclass) {
		this.rest_kqclass = rest_kqclass;
	}

	public String getRestleave_type() {
		return restleave_type;
	}

	public void setRestleave_type(String restleave_type) {
		this.restleave_type = restleave_type;
	}
	
	
	
	public String getFlextime_ruler() {
		return flextime_ruler;
	}

	public void setFlextime_ruler(String flextime_ruler) {
		this.flextime_ruler = flextime_ruler;
	}
	public String getApprove_relation() {
		return approve_relation;
	}

	public void setApprove_relation(String approve_relation) {
		this.approve_relation = approve_relation;
	}

	@Override
    public void outPutFormHM() {
		//this.setStrutList((ArrayList)this.getFormHM().get("strutList"));
		this.getKqStrutForm().setList((ArrayList)this.getFormHM().get("strutList"));
		this.setNlist((ArrayList)this.getFormHM().get("nlist"));
		this.setTlist((ArrayList)this.getFormHM().get("tlist"));
		this.setKq_type((String)this.getFormHM().get("kq_type"));
		this.setKq_g_no((String)this.getFormHM().get("kq_g_no"));
		this.setKq_cardno((String)this.getFormHM().get("kq_cardno"));
		this.setSlist((ArrayList)this.getFormHM().get("slist"));
		this.setSelist((ArrayList)this.getFormHM().get("selist"));
		
		this.setSige((String)this.getFormHM().get("sige"));
		
		this.setOne((String)this.getFormHM().get("one"));
		this.setOnes((String)this.getFormHM().get("ones"));
		this.setTwo((String)this.getFormHM().get("two"));
		this.setTwos((String)this.getFormHM().get("twos"));
		this.setThre((String)this.getFormHM().get("thre"));
		this.setThres((String)this.getFormHM().get("thres"));
		this.setFour((String)this.getFormHM().get("four"));
		this.setFours((String)this.getFormHM().get("fours"));		
		this.setMessi(null);
		this.setPar_list((ArrayList)this.getFormHM().get("par_list"));
		this.setPar_select_list((ArrayList)this.getFormHM().get("par_select_list"));
        this.setPar_mes((String[])this.getFormHM().get("par_mes"));
        this.setHoli_list((ArrayList)this.getFormHM().get("holi_list"));
        this.setHoli_select_list((ArrayList)this.getFormHM().get("holi_select_list"));
        this.setHoli_mes((String[])this.getFormHM().get("holi_mes"));
        this.setOver_rule((String)this.getFormHM().get("over_rule"));
        this.setOver_status((String)this.getFormHM().get("over_status"));
        this.setLeave_rule((String)this.getFormHM().get("leave_rule"));
        this.setLeave_status((String)this.getFormHM().get("leave_status"));
        this.setRest_kqclass((String)this.getFormHM().get("rest_kqclass"));
        this.setRestleave_type((String)this.getFormHM().get("restleave_type"));
        this.setFlextime_ruler((String)this.getFormHM().get("flextime_ruler"));
        this.setClass_list((ArrayList)this.getFormHM().get("class_list"));
        this.setOpinion_overtime_type((String)this.getFormHM().get("opinion_overtime_type"));
        this.setCard_interval((String)this.getFormHM().get("card_interval"));
        this.setMin_mid_leave_time((String)this.getFormHM().get("min_mid_leave_time"));
		this.setMin_overtime((String)this.getFormHM().get("min_overtime"));
		this.setOvertime_max_limit((String)this.getFormHM().get("DURATION_OVERTIME_MAX_LIMIT"));
		this.setNeed_busicompare((String)this.getFormHM().get("need_busicompare"));	
		this.setBusi_cardbegin((String)this.getFormHM().get("busi_cardbegin"));	
		this.setBusi_cardend((String)this.getFormHM().get("busi_cardend"));		 
		this.setBusifact_diff((String)this.getFormHM().get("busifact_diff"));		
		this.setBusi_morethan_fact((String)this.getFormHM().get("busi_morethan_fact"));
		this.setRest_overtime_time((String)this.getFormHM().get("rest_overtime_time"));
		this.setCard_causation((String)this.getFormHM().get("card_causation"));
		this.setRepair_card_status((String)this.getFormHM().get("repair_card_status"));
		this.setRepair_card_num((String)this.getFormHM().get("repair_card_num"));
		this.setLeave_rule_late((String)this.getFormHM().get("leave_rule_late"));
		this.setLeave_rule_late_status((String)this.getFormHM().get("leave_rule_late_status"));
		this.setPigeonhole_type((String)this.getFormHM().get("pigeonhole_type"));
		this.setUp_dailyregister((String)this.getFormHM().get("up_dailyregister"));
		this.setSetList((ArrayList)this.getFormHM().get("setList"));
		this.setMagcard_setid((String)this.getFormHM().get("magcard_setid"));
		this.setMagcard_flag((String)this.getFormHM().get("magcard_flag"));
		this.setCardlist((ArrayList)this.getFormHM().get("cardlist"));
		this.setRelationlist((ArrayList)this.getFormHM().get("relationlist"));
		this.setMagcard_cardid((String)this.getFormHM().get("magcard_cardid"));
		this.setMagcard_com((String)this.getFormHM().get("magcard_com"));
		this.setTab_name((String)this.getFormHM().get("tab_name"));
		
		this.getTemplateListForm().setList((ArrayList)this.getFormHM().get("templateList"));
		this.setCardearly((String)this.getFormHM().get("cardearly"));
		this.setCheck_inout_match((String)this.getFormHM().get("check_inout_match"));
		this.setStatq03_list((ArrayList)this.getFormHM().get("statq03_list"));
		this.setPar_statq03_list((ArrayList)this.getFormHM().get("par_statq03_list"));
		this.setStat_q03((String[])this.getFormHM().get("stat_q03"));
		this.setNet_sign_check_ip((String)this.getFormHM().get("net_sign_check_ip"));
		this.setNet_sign_approve((String)this.getFormHM().get("net_sign_approve"));
		this.setTimecard03_list((ArrayList)this.getFormHM().get("timecard03_list"));
		this.setKq_timecard03_list((ArrayList)this.getFormHM().get("kq_timecard03_list"));
		this.setKqcard_q03((String[])this.getFormHM().get("kqcard_q03"));
		this.setBzlist((ArrayList)this.getFormHM().get("bzlist"));
		this.setKq_bzindex((String)this.getFormHM().get("kq_bzindex"));
		this.setData_processing((String)this.getFormHM().get("data_processing"));
		this.setQuick_analyse_mode((String)this.getFormHM().get("quick_analyse_mode"));
		this.setThlist((ArrayList)this.getFormHM().get("thlist"));
		this.setKq_thbzindex((String)this.getFormHM().get("kq_thbzindex"));
		this.setOvertime_hol((String)this.getFormHM().get("overtime_hol"));
		this.setApproved_delete((String)this.getFormHM().get("approved_delete"));
		this.setCheckControl_status(this.getFormHM().get("checkControl_status").toString());
		this.setCheckControl_content(this.getFormHM().get("checkControl_content").toString());
		this.setSelf_accept_month_data((String)this.getFormHM().get("self_accept_month_data"));
		this.setOfficeleave_enable_leave_overtime((String)this.getFormHM().get("officeleave_enable_leave_overtime"));
		
		//为首钢添加 考勤参数
		this.setKq_datelist((ArrayList)this.getFormHM().get("kq_datelist"));
		this.setKq_startdate_field((String)this.getFormHM().get("kq_startdate_field"));
		this.setKq_enddate_field((String)this.getFormHM().get("kq_enddate_field"));
		this.setDept_changedate_field((String)this.getFormHM().get("dept_changedate_field"));
		
		// 假期扣减规则
		this.setHoliday_minus_rule((String)this.getFormHM().get("holiday_minus_rule"));
		//考勤同步参数
		this.setSync_carddata((String)this.getFormHM().get("sync_carddata"));//同步刷卡数据
		this.setSync_base((String)this.getFormHM().get("sync_base"));//同步数据库
		this.setSync_post((String)this.getFormHM().get("sync_post"));//同步数据库端口
		this.setSync_url((String)this.getFormHM().get("sync_url"));//同步数据库地址
	    this.setSync_table((String)this.getFormHM().get("sync_table"));//同步表	 
	    this.setSync_basetype((String)this.getFormHM().get("sync_basetype"));//数据库类型
	    this.setSync_pass((String)this.getFormHM().get("sync_pass"));//密码
	    this.setSync_user((String)this.getFormHM().get("sync_user"));//用户名
	    this.setSync_list((ArrayList)this.getFormHM().get("sync_list"));
	    
	    // 考勤同步配制核二三
	    this.setSyncxml_related_list((ArrayList) this.getFormHM().get("syncxml_related_list"));// 关联指标集合
	    this.setSyncxml_dbtype_list((ArrayList) this.getFormHM().get("syncxml_dbtype_list"));//数据库类型列表
	    this.setConnStrList((List) this.getFormHM().get("connStrList")); 
	    this.setType((String) this.getFormHM().get("type"));
	    this.setSyncxml_id( (String)this.getFormHM().get("syncxml_id"));// id
	    this.setSyncxml_desc((String)this.getFormHM().get("syncxml_desc"));//同步对数据源描述
	    this.setSyncxml_dbtype((String)this.getFormHM().get("syncxml_dbtype"));//同步数据库类型
	    this.setSyncxml_ip((String)this.getFormHM().get("syncxml_ip"));//同步数据库ip地址
	    this.setSyncxml_port((String)this.getFormHM().get("syncxml_port"));//同步数据库端口
	    this.setSyncxml_dbname((String)this.getFormHM().get("syncxml_dbname"));//同步数据库名称
	    this.setSyncxml_space((String)this.getFormHM().get("syncxml_space"));//同步表空间
	    this.setSyncxml_user((String)this.getFormHM().get("syncxml_user"));//同步用户名
	    this.setSyncxml_pwd((String)this.getFormHM().get("syncxml_pwd"));//同步数据库密码
	    this.setSyncxml_status((String)this.getFormHM().get("syncxml_status"));//同步是否启用
	    this.setSyncxml_related((String)this.getFormHM().get("syncxml_related"));//同步关联指标
	    this.setSyncxml_options((String)this.getFormHM().get("syncxml_options"));//需要同步的表
	    this.setSyncxml_source((String) this.getFormHM().get("syncxml_source"));
	    this.setKq_orgview_post((String) this.getFormHM().get("kq_orgview_post"));
	    
	    this.setStandard_hours((String)this.getFormHM().get("standard_hours")); //标准工时 默认8小时
	    this.setTurn_enable((String)this.getFormHM().get("turn_enable")); //启用标识 0 不启用 1 启用
	    this.setTurn_charge((String)this.getFormHM().get("turn_charge")); //0 需要进出匹配 1 有刷卡即加班
	    this.setTurn_tlong((String)this.getFormHM().get("turn_tlong")); //刷卡时长  2 默认时长 1 参考班次时长 0 实际刷卡时长
	    this.setTurn_time((String)this.getFormHM().get("turn_time")); //刷卡时长 默认8小时
	    this.setTurn_classid((String)this.getFormHM().get("turn_classid")); //被选班次
	    this.setTurn_classlist((ArrayList)this.getFormHM().get("turn_classlist")); //班次列表
	    this.setTurn_appdoc((String)this.getFormHM().get("turn_appdoc")); //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认
	    this.setApprove_relation((String)this.getFormHM().get("approve_relation"));//审批关系
	    
	    this.setVacationTypeList((ArrayList)this.getFormHM().get("vacationTypeList"));
	    this.setOvertimeToOff((String)this.getFormHM().get("overtimeToOff"));
	    this.setVacationToOff((String)this.getFormHM().get("vacationToOff"));
	    this.setValidityTime((String)this.getFormHM().get("validityTime"));
	    this.setVacation_list((ArrayList)this.getFormHM().get("vacation_list"));
	    this.setVacation_select_list((ArrayList)this.getFormHM().get("vacation_select_list"));
	    this.setOvertimeForLeaveCycle((String)this.getFormHM().get("overtimeForLeaveCycle"));
	    this.setOvertimeForLeaveMaxHour((String)this.getFormHM().get("overtimeForLeaveMaxHour"));
	    this.setOvertimeForLeaveCycleList((ArrayList)this.getFormHM().get("overtimeForLeaveCycleList"));
	    
	    /*申请比对*/
	    this.setLeave_compare_rule((String)this.getFormHM().get("leave_compare_rule"));
	    this.setLeave_need_check((String)this.getFormHM().get("leave_need_check"));
	    this.setLeave_updata_data((String)this.getFormHM().get("leave_updata_data"));
	    this.setOvertime_compare_rule((String)this.getFormHM().get("overtime_compare_rule"));
	    this.setOvertime_need_check((String)this.getFormHM().get("overtime_need_check"));
	    this.setOvertime_updata_data((String)this.getFormHM().get("overtime_updata_data"));
	    this.setOfficeleave_compare_rule((String)this.getFormHM().get("officeleave_compare_rule"));
	    this.setOfficeleave_need_check((String)this.getFormHM().get("officeleave_need_check"));
	    this.setOfficeleave_updata_data((String)this.getFormHM().get("officeleave_updata_data"));
	    
	    this.setLogon_kq_hint((String)this.getFormHM().get("logon_kq_hint"));
	}
	

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedlist",(ArrayList)this.getKqStrutForm().getSelectedList());
        this.getFormHM().put("kq_cardno",(String)this.getKq_cardno());
        this.getFormHM().put("kq_type",(String)this.getKq_type());
        this.getFormHM().put("kq_g_no",(String)this.getKq_g_no());
        this.getFormHM().put("kq_base",(String)this.getKq_base());
        this.getFormHM().put("messi",this.getMessi());
        
        this.getFormHM().put("nlist",(ArrayList)this.getNlist());
        this.getFormHM().put("tlist",(ArrayList)this.getTlist());
        this.getFormHM().put("slist",(ArrayList)this.getSlist());
        this.getFormHM().put("selist",(ArrayList)this.getSelist());
        
        this.getFormHM().put("one",(String)this.getOne());
        this.getFormHM().put("ones",(String)this.getOnes());
        this.getFormHM().put("two",(String)this.getTwo());
        this.getFormHM().put("twos",(String)this.getTwos());
        this.getFormHM().put("thre",(String)this.getThre());
        this.getFormHM().put("thres",(String)this.getThres());
        this.getFormHM().put("four",(String)this.getFour());
        this.getFormHM().put("fours",(String)this.getFours());
        
        this.getFormHM().put("sige",(String)this.getSige());
        this.getFormHM().put("par_list",this.getPar_list());
        this.getFormHM().put("par_select_list",this.par_select_list);
        this.getFormHM().put("par_mes",this.getPar_mes());
        this.getFormHM().put("holi_select_list",this.getHoli_select_list());
        this.getFormHM().put("holi_list",this.getHoli_list());
        this.getFormHM().put("holi_mes",this.getHoli_mes());
        this.getFormHM().put("over_status",this.getOver_status());
        this.getFormHM().put("over_rule",this.getOver_rule());
        this.getFormHM().put("leave_rule",this.getLeave_rule());
        this.getFormHM().put("leave_status",this.getLeave_status());
        this.getFormHM().put("restleave_type",this.getRestleave_type());
        this.getFormHM().put("flextime_ruler",this.getFlextime_ruler());
        this.getFormHM().put("rest_kqclass",this.getRest_kqclass());
        this.getFormHM().put("magcard_setid", this.getMagcard_setid());
        this.getFormHM().put("magcard_flag", this.getMagcard_flag());
        this.getFormHM().put("magcard_cardid", this.getMagcard_cardid());
        this.getFormHM().put("template_type", this.getTemplate_type());
        this.getFormHM().put("importfile",getImportfile());
        this.getFormHM().put("path", this.getPath());
        this.getFormHM().put("tab_name", this.getTab_name());
        this.getFormHM().put("statq03_list",this.getStatq03_list());
        this.getFormHM().put("par_statq03_list",this.getPar_statq03_list());
        this.getFormHM().put("stat_q03",this.getStat_q03());
        this.getFormHM().put("net_sign_check_ip",this.getNet_sign_check_ip());
        this.getFormHM().put("timecard03_list",this.getTimecard03_list());
        this.getFormHM().put("kq_timecard03_list",this.getKq_timecard03_list());
        this.getFormHM().put("kqcard_q03",this.getKqcard_q03());
        this.getFormHM().put("bzlist",(ArrayList)this.getBzlist());
        this.getFormHM().put("kq_bzindex",(String)this.getKq_bzindex());
        this.getFormHM().put("thlist",(ArrayList)this.getThlist());
        this.getFormHM().put("kq_thbzindex",(String)this.getKq_thbzindex());
        
         this.getFormHM().put("sync_carddata",this.getSync_carddata());//同步刷卡数据
         this.getFormHM().put("sync_base",this.getSync_base());//同步数据库
         this.getFormHM().put("sync_post",this.getSync_post());//同步数据库端口
         this.getFormHM().put("sync_url",this.getSync_url());//同步数据库地址
         this.getFormHM().put("sync_table",this.getSync_table());//同步表	 
         this.getFormHM().put("sync_basetype",this.getSync_basetype());//数据库类型
         this.getFormHM().put("sync_pass",this.getSync_pass());//密码
         this.getFormHM().put("sync_user",this.getSync_user());//用户名
   
         this.getFormHM().put("syncxml_id", this.getSyncxml_id());// id
         this.getFormHM().put("syncxml_desc",this.getSyncxml_desc());//同步对数据源描述
        this.getFormHM().put("syncxml_dbtype",this.getSyncxml_dbtype());//同步数据库类型
        this.getFormHM().put("syncxml_ip",this.getSyncxml_ip());//同步数据库ip地址
        this.getFormHM().put("syncxml_port",this.getSyncxml_port());//同步数据库端口
        this.getFormHM().put("syncxml_dbname", this.getSyncxml_dbname());//同步数据库名称
        this.getFormHM().put("syncxml_space", this.getSyncxml_space());//同步表空间
        this.getFormHM().put("syncxml_user", this.getSyncxml_user());//同步用户名
        this.getFormHM().put("syncxml_pwd", this.getSyncxml_pwd());//同步数据库密码
        if (this.getSyncxml_status() == null || this.syncxml_status.length() == 0) {
        	this.setSyncxml_status("0");
        }
        this.getFormHM().put("syncxml_status",this.getSyncxml_status());//同步是否启用
        this.getFormHM().put("syncxml_related", this.getSyncxml_related());//同步关联指标
        this.getFormHM().put("syncxml_options", this.getSyncxml_options());//同步需要的表
        this.getFormHM().put("syncxml_source", this.getSyncxml_source());//数据源表
        this.getFormHM().put("kq_orgview_post", this.getKq_orgview_post());//机构不显示岗位
        
        this.getFormHM().put("standard_hours", this.getStandard_hours());
        /* 休息日刷卡转加班 */
	    this.getFormHM().put("turn_enable",this.getTurn_enable()); //启用标识 0 不启用 1 启用
	    this.getFormHM().put("turn_charge",this.getTurn_charge()); //0 需要进出匹配 1 有刷卡即加班
	    this.getFormHM().put("turn_tlong",this.getTurn_tlong()); //刷卡时长  2 默认时长 1 参考班次时长 0 实际刷卡时长
	    this.getFormHM().put("turn_time",this.getTurn_time()); //刷卡时长 默认8小时
	    this.getFormHM().put("turn_classid",this.getTurn_classid()); //被选班次
	    this.getFormHM().put("turn_classlist",this.getTurn_classlist()); //班次列表
	    this.getFormHM().put("turn_appdoc",this.getTurn_appdoc()); //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认
        this.getFormHM().put("approve_relation",this.getApprove_relation());//审批关系
        
        this.getFormHM().put("vacationTypeList", this.getVacationTypeList());
        this.getFormHM().put("overtimeToOff", this.getOvertimeToOff());
        this.getFormHM().put("vacationToOff", this.getVacationToOff());
        this.getFormHM().put("validityTime", this.getValidityTime());
        this.getFormHM().put("vacation_list", this.getVacation_list());
        this.getFormHM().put("vacation_select_list", this.getVacation_select_list());
        this.getFormHM().put("overtimeForLeaveCycle", this.getOvertimeForLeaveCycle());
        this.getFormHM().put("overtimeForLeaveMaxHour", this.getOvertimeForLeaveMaxHour());
        this.getFormHM().put("overtimeForLeaveCycleList", this.getOvertimeForLeaveCycleList());
        
        /*申请比对*/
        this.getFormHM().put("leave_compare_rule", this.getLeave_compare_rule());
        this.getFormHM().put("leave_need_check", this.getLeave_need_check());
        this.getFormHM().put("leave_updata_data", this.getLeave_updata_data());
        this.getFormHM().put("overtime_compare_rule", this.getOvertime_compare_rule());
        this.getFormHM().put("overtime_need_check", this.getOvertime_need_check());
        this.getFormHM().put("overtime_updata_data", this.getOvertime_updata_data());
        this.getFormHM().put("officeleave_compare_rule", this.getOfficeleave_compare_rule());
        this.getFormHM().put("officeleave_need_check", this.getOfficeleave_need_check());
        this.getFormHM().put("officeleave_updata_data", this.getOfficeleave_updata_data());
        
        this.getFormHM().put("logon_kq_hint", this.getLogon_kq_hint());
        this.getFormHM().put("officeleave_enable_leave_overtime", this.getOfficeleave_enable_leave_overtime());
	}

	public String getKq_base() {
		return kq_base;
	}

	public void setKq_base(String kq_base) {
		this.kq_base = kq_base;
	}
	
	public String[] getMessi() {
		return messi;
	}

	public void setMessi(String[] messi) {
		this.messi = messi;
	}

	public String getKq_cardno() {
		return kq_cardno;
	}

	public void setKq_cardno(String kq_cardno) {
		this.kq_cardno = kq_cardno;
	}

	public String getKq_g_no() {
		return kq_g_no;
	}

	public void setKq_g_no(String kq_g_no) {
		this.kq_g_no = kq_g_no;
	}

	public String getKq_type() {
		return kq_type;
	}

	public void setKq_type(String kq_type) {
		this.kq_type = kq_type;
	}

	public PaginationForm getKqStrutForm() {
		return kqStrutForm;
	}

	public void setKqStrutForm(PaginationForm kqStrutForm) {
		this.kqStrutForm = kqStrutForm;
	}

	public ArrayList getStrutList() {
		return strutList;
	}

	public void setStrutList(ArrayList strutList) {
		this.strutList = strutList;
	}

	public ArrayList getNlist() {
		return nlist;
	}

	public void setNlist(ArrayList nlist) {
		this.nlist = nlist;
	}

	public ArrayList getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
	}
	
	public ArrayList getSlist() {
		return slist;
	}

	public void setSlist(ArrayList slist) {
		this.slist = slist;
	}
	public ArrayList getSelist() {
		return selist;
	}

	public void setSelist(ArrayList selist) {
		this.selist = selist;
	}

	public String getFour() {
		return four;
	}

	public void setFour(String four) {
		this.four = four;
	}

	public String getFours() {
		return fours;
	}

	public void setFours(String fours) {
		this.fours = fours;
	}

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getOnes() {
		return ones;
	}

	public void setOnes(String ones) {
		this.ones = ones;
	}

	public String getThre() {
		return thre;
	}

	public void setThre(String thre) {
		this.thre = thre;
	}

	public String getThres() {
		return thres;
	}

	public void setThres(String thres) {
		this.thres = thres;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	public String getTwos() {
		return twos;
	}

	public void setTwos(String twos) {
		this.twos = twos;
	}

	public String getSige() {
		return sige;
	}

	public void setSige(String sige) {
		this.sige = sige;
	}

	public ArrayList getPar_list() {
		return par_list;
	}

	public void setPar_list(ArrayList par_list) {
		this.par_list = par_list;
	}

	public ArrayList getPar_select_list() {
		return par_select_list;
	}

	public void setPar_select_list(ArrayList par_select_list) {
		this.par_select_list = par_select_list;
	}

	public String[] getPar_mes() {
		return par_mes;
	}

	public void setPar_mes(String[] par_mes) {
		this.par_mes = par_mes;
	}

	public ArrayList getHoli_list() {
		return holi_list;
	}

	public void setHoli_list(ArrayList holi_list) {
		this.holi_list = holi_list;
	}

	public String[] getHoli_mes() {
		return holi_mes;
	}

	public void setHoli_mes(String[] holi_mes) {
		this.holi_mes = holi_mes;
	}

	public ArrayList getHoli_select_list() {
		return holi_select_list;
	}

	public void setHoli_select_list(ArrayList holi_select_list) {
		this.holi_select_list = holi_select_list;
	}

	public String getOver_rule() {
		return over_rule;
	}

	public void setOver_rule(String over_rule) {
		this.over_rule = over_rule;
	}

	public String getOver_status() {
		return over_status;
	}

	public void setOver_status(String over_status) {
		this.over_status = over_status;
	}

	public String getLeave_rule() {
		return leave_rule;
	}

	public void setLeave_rule(String leave_rule) {
		this.leave_rule = leave_rule;
	}

	public String getLeave_status() {
		return leave_status;
	}

	public void setLeave_status(String leave_status) {
		this.leave_status = leave_status;
	}

	public String getOpinion_overtime_type() {
		return opinion_overtime_type;
	}

	public void setOpinion_overtime_type(String opinion_overtime_type) {
		this.opinion_overtime_type = opinion_overtime_type;
	}

	public String getBusi_cardbegin() {
		return busi_cardbegin;
	}

	public void setBusi_cardbegin(String busi_cardbegin) {
		this.busi_cardbegin = busi_cardbegin;
	}

	public String getBusi_cardend() {
		return busi_cardend;
	}

	public void setBusi_cardend(String busi_cardend) {
		this.busi_cardend = busi_cardend;
	}

	public String getBusi_morethan_fact() {
		return busi_morethan_fact;
	}

	public void setBusi_morethan_fact(String busi_morethan_fact) {
		this.busi_morethan_fact = busi_morethan_fact;
	}

	public String getBusifact_diff() {
		return busifact_diff;
	}

	public void setBusifact_diff(String busifact_diff) {
		this.busifact_diff = busifact_diff;
	}

	public String getCard_interval() {
		return card_interval;
	}

	public void setCard_interval(String card_interval) {
		this.card_interval = card_interval;
	}
	
	
	public String getMin_mid_leave_time() {
		return min_mid_leave_time;
	}

	public void setMin_mid_leave_time(String min_mid_leave_time) {
		this.min_mid_leave_time = min_mid_leave_time;
	}

	public String getCardearly() {
		return cardearly;
	}

	public void setCardearly(String cardearly) {
		this.cardearly = cardearly;
	}

	public String getMin_overtime() {
		return min_overtime;
	}

	public void setMin_overtime(String min_overtime) {
		this.min_overtime = min_overtime;
	}

	public String getNeed_busicompare() {
		return need_busicompare;
	}

	public void setNeed_busicompare(String need_busicompare) {
		this.need_busicompare = need_busicompare;
	}

	public String getRest_overtime_time() {
		return rest_overtime_time;
	}

	public void setRest_overtime_time(String rest_overtime_time) {
		this.rest_overtime_time = rest_overtime_time;
	}

	public String getCard_causation() {
		return card_causation;
	}

	public void setCard_causation(String card_causation) {
		this.card_causation = card_causation;
	}

	public String getLeave_rule_late() {
		return leave_rule_late;
	}

	public void setLeave_rule_late(String leave_rule_late) {
		this.leave_rule_late = leave_rule_late;
	}

	public String getLeave_rule_late_status() {
		return leave_rule_late_status;
	}

	public void setLeave_rule_late_status(String leave_rule_late_status) {
		this.leave_rule_late_status = leave_rule_late_status;
	}

	public String getRepair_card_num() {
		return repair_card_num;
	}

	public void setRepair_card_num(String repair_card_num) {
		this.repair_card_num = repair_card_num;
	}

	public String getRepair_card_status() {
		return repair_card_status;
	}

	public void setRepair_card_status(String repair_card_status) {
		this.repair_card_status = repair_card_status;
	}

	public String getMagcard_setid() {
		return magcard_setid;
	}

	public void setMagcard_setid(String magcard_setid) {
		this.magcard_setid = magcard_setid;
	}

	public ArrayList getSetList() {
		return setList;
	}

	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}

	public String getMagcard_com() {
		return magcard_com;
	}

	public void setMagcard_com(String magcard_com) {
		this.magcard_com = magcard_com;
	}

	public ArrayList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList templateList) {
		this.templateList = templateList;
	}

	public PaginationForm getTemplateListForm() {
		return templateListForm;
	}

	public void setTemplateListForm(PaginationForm templateListForm) {
		this.templateListForm = templateListForm;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		String pajs=arg1.getSession().getServletContext().getRealPath("");
		this.setPath(pajs);
		if("/kq/options/struts/select_parameter".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
			this.setTab_name("");
			this.getFormHM().put("tab_name", "");
	    }
		return super.validate(arg0, arg1);
	}

	public ArrayList getStatq03_list() {
		return statq03_list;
	}

	public void setStatq03_list(ArrayList statq03_list) {
		this.statq03_list = statq03_list;
	}

	public ArrayList getPar_statq03_list() {
		return par_statq03_list;
	}

	public void setPar_statq03_list(ArrayList par_statq03_list) {
		this.par_statq03_list = par_statq03_list;
	}

	public String[] getStat_q03() {
		return stat_q03;
	}

	public void setStat_q03(String[] stat_q03) {
		this.stat_q03 = stat_q03;
	}

	public String getNet_sign_check_ip() {
		return net_sign_check_ip;
	}

	public void setNet_sign_check_ip(String net_sign_check_ip) {
		this.net_sign_check_ip = net_sign_check_ip;
	}

	public String getNet_sign_approve() {
		return net_sign_approve;
	}

	public void setNet_sign_approve(String net_sign_approve) {
		this.net_sign_approve = net_sign_approve;
	}

	public ArrayList getTimecard03_list() {
		return timecard03_list;
	}

	public void setTimecard03_list(ArrayList timecard03_list) {
		this.timecard03_list = timecard03_list;
	}

	public ArrayList getKq_timecard03_list() {
		return kq_timecard03_list;
	}

	public void setKq_timecard03_list(ArrayList kq_timecard03_list) {
		this.kq_timecard03_list = kq_timecard03_list;
	}

	public String[] getKqcard_q03() {
		return kqcard_q03;
	}

	public void setKqcard_q03(String[] kqcard_q03) {
		this.kqcard_q03 = kqcard_q03;
	}

	public ArrayList getBzlist() {
		return bzlist;
	}

	public void setBzlist(ArrayList bzlist) {
		this.bzlist = bzlist;
	}

	public String getKq_bzindex() {
		return kq_bzindex;
	}

	public void setKq_bzindex(String kq_bzindex) {
		this.kq_bzindex = kq_bzindex;
	}

	public String getQuick_analyse_mode() {
		return quick_analyse_mode;
	}

	public void setQuick_analyse_mode(String quick_analyse_mode) {
		this.quick_analyse_mode = quick_analyse_mode;
	}

	public String getData_processing() {
		return data_processing;
	}

	public void setData_processing(String data_processing) {
		this.data_processing = data_processing;
	}

	public ArrayList getThlist() {
		return thlist;
	}

	public void setThlist(ArrayList thlist) {
		this.thlist = thlist;
	}

	public String getKq_thbzindex() {
		return kq_thbzindex;
	}

	public void setKq_thbzindex(String kq_thbzindex) {
		this.kq_thbzindex = kq_thbzindex;
	}

	public String getOvertime_hol() {
		return overtime_hol;
	}

	public void setOvertime_hol(String overtime_hol) {
		this.overtime_hol = overtime_hol;
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

	public String getSync_pass() {
		return sync_pass;
	}

	public void setSync_pass(String sync_pass) {
		this.sync_pass = sync_pass;
	}

	public String getSync_user() {
		return sync_user;
	}

	public void setSync_user(String sync_user) {
		this.sync_user = sync_user;
	}

	public String getSync_basetype() {
		return sync_basetype;
	}

	public void setSync_basetype(String sync_basetype) {
		this.sync_basetype = sync_basetype;
	}

	public ArrayList getSync_list() {
		return sync_list;
	}

	public void setSync_list(ArrayList sync_list) {
		this.sync_list = sync_list;
	}

	public ArrayList getSyncxml_related_list() {
		return syncxml_related_list;
	}

	public void setSyncxml_related_list(ArrayList syncxml_related_list) {
		this.syncxml_related_list = syncxml_related_list;
	}

	public String getSyncxml_id() {
		return syncxml_id;
	}

	public void setSyncxml_id(String syncxml_id) {
		this.syncxml_id = syncxml_id;
	}

	public String getSyncxml_source() {
		return syncxml_source;
	}

	public void setSyncxml_source(String syncxml_source) {
		this.syncxml_source = syncxml_source;
	}

	public String getKq_orgview_post() {
		return kq_orgview_post;
	}

	public void setKq_orgview_post(String kq_orgview_post) {
		this.kq_orgview_post = kq_orgview_post;
	}

	public String getTurn_enable() {
		return turn_enable;
	}

	public void setTurn_enable(String turn_enable) {
		this.turn_enable = turn_enable;
	}

	public String getTurn_charge() {
		return turn_charge;
	}

	public void setTurn_charge(String turn_charge) {
		this.turn_charge = turn_charge;
	}

	public String getTurn_tlong() {
		return turn_tlong;
	}

	public void setTurn_tlong(String turn_tlong) {
		this.turn_tlong = turn_tlong;
	}

	public String getTurn_time() {
		return turn_time;
	}

	public void setTurn_time(String turn_time) {
		this.turn_time = turn_time;
	}

	public String getTurn_classid() {
		return turn_classid;
	}

	public void setTurn_classid(String turn_classid) {
		this.turn_classid = turn_classid;
	}

	public ArrayList getTurn_classlist() {
		return turn_classlist;
	}

	public void setTurn_classlist(ArrayList turn_classlist) {
		this.turn_classlist = turn_classlist;
	}

	public String getTurn_appdoc() {
		return turn_appdoc;
	}

	public void setTurn_appdoc(String turn_appdoc) {
		this.turn_appdoc = turn_appdoc;
	}

    public void setOvertime_max_limit(String overtime_max_limit) {
        this.overtime_max_limit = overtime_max_limit;
    }

    public String getOvertime_max_limit() {
        return overtime_max_limit;
    }

	public String getStandard_hours()
	{
		return standard_hours;
	}

	public void setStandard_hours(String standardHours)
	{
		standard_hours = standardHours;
	}

	public void setSelf_accept_month_data(String self_accept_month_data) {
		this.self_accept_month_data = self_accept_month_data;
	}

	public String getSelf_accept_month_data() {
		return self_accept_month_data;
	}

    public String getOfficeleave_enable_leave_overtime() {
        return officeleave_enable_leave_overtime;
    }

    public void setOfficeleave_enable_leave_overtime(String officeleave_enable_leave_overtime) {
        this.officeleave_enable_leave_overtime = officeleave_enable_leave_overtime;
    }

    public String getOvertimeForLeaveCycle() {
        return overtimeForLeaveCycle;
    }

    public void setOvertimeForLeaveCycle(String overtimeForLeaveCycle) {
        this.overtimeForLeaveCycle = overtimeForLeaveCycle;
    }

    public String getOvertimeForLeaveMaxHour() {
        return overtimeForLeaveMaxHour;
    }

    public void setOvertimeForLeaveMaxHour(String overtimeForLeaveMaxHour) {
        this.overtimeForLeaveMaxHour = overtimeForLeaveMaxHour;
    }

    public ArrayList getOvertimeForLeaveCycleList() {
        return overtimeForLeaveCycleList;
    }

    public void setOvertimeForLeaveCycleList(ArrayList overtimeForLeaveCycleList) {
        this.overtimeForLeaveCycleList = overtimeForLeaveCycleList;
    }
}
