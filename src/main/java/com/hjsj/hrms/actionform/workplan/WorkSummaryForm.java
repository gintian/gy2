package com.hjsj.hrms.actionform.workplan;

import com.hrms.struts.action.FrameForm;

import java.util.Date;

public class WorkSummaryForm extends FrameForm {

	// 工作总结编号
	private int workSummaryID;
	// 本周工作总结 （计划完成情况及业绩总结）
	private String thisWorkSummary;
	// 下周工作计划
	private String nextWorkSummary;
	// 是否是业务用户
	private String ishr;
	// 某月中的第几周
	private String whichWeek;
	// 日志类型 （日报/周报/月报。。。）
	private int workSummaryType;
	// 审批标志 （起草/报批/已批。。。）
	private String workSummarySign;
	// 开始时间
	private Date workSummaryStartTime;
	// 结束时间
	private Date workSummaryEndTime;

	// 当前选择的时间 *****************************/
	private String selectdate;
	private String type;
	private String maptype;
	private String week;// 当前周
	private String month;// 当前月
	private String weeknum;// 当前月
	private String cycle; // 总结类型
	private String year; // 总结所在年度
	private String weekStart;
	private String weekEnd;
	// 是否是最高领导（无上级）
	private String isLeader;
	private String p0114;// 提交时间
	private String p0115;// 当前记录状态
	private String p0100;// id
	private String score;
	private String p011503; // 已提交
	private String p011501; // 已提交
	private String e0122; // 部门id
	private String deptdesc; // 部门
	private String b01ps; // 岗位
	private String querypara;
	private String remindNum;// 发邮件的形式，单个 or 群发
	private String nbase;
	private String a0100;
	private String nbaseA0100;
	private String scope;
	// private FormFile summaryFile;
	private String photo;
	private String a0101;
	private String p0113;
	private String can_edit;
	private String isself;
	private String user_a0101;
	private String user_photo;
	private String user_a0100;
	private String user_nbase;
	private String belong_type;
	// 年份范围
	private String yearListStr;
	private String typetitle;
	private String isemail;
	//hr返回链接
	private String returnurl;
	private String zhouzj;// 周总结 ，是否启用周总结chent 20161205 add start
	private String zhouzjpx;// 周总结 ，培训需求字段chent 20161205 add start
	
	private String summaryTypeJson;	//总结区间json串
    //个人及部门人员填报权限
    private String personCycleFunction;
    private String orgCycleFunction;

	public String getSummaryTypeJson() {
		return summaryTypeJson;
	}

	public void setSummaryTypeJson(String summaryTypeJson) {
		this.summaryTypeJson = summaryTypeJson;
	}

	public String getZhouzjpx() {
		return zhouzjpx;
	}

	public void setZhouzjpx(String zhouzjpx) {
		this.zhouzjpx = zhouzjpx;
	}

	public String getZhouzj() {
		return zhouzj;
	}

	public void setZhouzj(String zhouzj) {
		this.zhouzj = zhouzj;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("thisWorkSummary", this.getThisWorkSummary());
		this.getFormHM().put("nextWorkSummary", this.getNextWorkSummary());
		this.getFormHM().put("ishr", this.getIshr());
		/************************/
		this.getFormHM().put("selectdate", this.getSelectdate());
		this.getFormHM().put("week", this.getWeek());
		this.getFormHM().put("weeknum", this.getWeeknum());
		this.getFormHM().put("month", this.getMonth());
		this.getFormHM().put("deptdesc", this.getDeptdesc());
		this.getFormHM().put("cycle", this.getCycle());
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("weekstart", this.getWeekStart());
		this.getFormHM().put("weekend", this.getWeekEnd());

		this.getFormHM().put("p011501", this.getP011501());
		this.getFormHM().put("p011503", this.getP011503());
		this.getFormHM().put("p0115", this.getP0115());
		this.getFormHM().put("p0100", this.getP0100());
		this.getFormHM().put("p0113", this.getP0113());
		this.getFormHM().put("score", this.getScore());
		this.getFormHM().put("e0122", this.getE0122());
		this.getFormHM().put("b01ps", this.getB01ps());
		this.getFormHM().put("user_a0101", this.getUser_a0101());
		this.getFormHM().put("user_photo", this.getUser_photo());
		this.getFormHM().put("a0101", this.getA0101());
		this.getFormHM().put("querypara", this.getQuerypara());
		this.getFormHM().put("can_edit", this.getCan_edit());
		this.getFormHM().put("isself", this.getIsself());
		this.getFormHM().put("maptype", this.getMaptype());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("typetitle", this.getTypetitle());

		this.getFormHM().put("belong_type", this.getBelong_type());
		this.getFormHM().put("photo", this.getPhoto());
		this.getFormHM().put("isLeader", this.getIsLeader());
		this.getFormHM().put("user_nbase", this.getUser_nbase());
		this.getFormHM().put("user_a0100", this.getUser_a0100());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("nbaseA0100", this.getNbaseA0100());
		this.getFormHM().put("isemail", this.getIsemail());
		this.getFormHM().put("returnurl", this.getReturnurl());
		this.getFormHM().put("zhouzj", this.getZhouzj());
		this.getFormHM().put("zhouzjpx", this.getZhouzjpx());
		this.getFormHM().put("summaryTypeJson", this.getSummaryTypeJson());
		this.getFormHM().put("personCycleFunction",this.getPersonCycleFunction());
		this.getFormHM().put("orgCycleFunction",this.getOrgCycleFunction());

	}

	@Override
    public void outPutFormHM() {
		this.setThisWorkSummary((String) this.getFormHM().get("thisWorkSummary"));
		this.setNextWorkSummary((String) this.getFormHM().get("nextWorkSummary"));
		this.setIshr((String) this.getFormHM().get("ishr"));
		/****************************************/
		this.setSelectdate((String) this.getFormHM().get("selectdate"));
		this.setWeek((String) this.getFormHM().get("week"));
		this.setWeeknum((String) this.getFormHM().get("weeknum"));
		this.setMonth((String) this.getFormHM().get("month"));
		this.setDeptdesc((String) this.getFormHM().get("deptdesc"));
		this.setCycle((String) this.getFormHM().get("cycle"));
		this.setYear((String) this.getFormHM().get("year"));
		this.setWeekStart((String) this.getFormHM().get("weekstart"));
		this.setWeekEnd((String) this.getFormHM().get("weekend"));
		this.setMaptype((String) this.getFormHM().get("maptype"));
		this.setType((String) this.getFormHM().get("type"));
		this.setTypetitle((String) this.getFormHM().get("typetitle"));

		this.setP0113((String) this.getFormHM().get("p0113"));
		this.setP0114((String) this.getFormHM().get("p0114"));
		this.setP011501((String) this.getFormHM().get("p011501"));
		this.setP011503((String) this.getFormHM().get("p011503"));
		this.setP0115((String) this.getFormHM().get("p0115"));
		this.setP0100((String) this.getFormHM().get("p0100"));
		this.setScore((String) this.getFormHM().get("score"));
		this.setE0122((String) this.getFormHM().get("e0122"));
		this.setB01ps((String) this.getFormHM().get("b01ps"));
		this.setA0101((String) this.getFormHM().get("a0101"));
		this.setUser_a0101((String) this.getFormHM().get("user_a0101"));
		this.setUser_photo((String) this.getFormHM().get("user_photo"));
		this.setUser_a0100((String) this.getFormHM().get("user_a0100"));
		this.setUser_nbase((String) this.getFormHM().get("user_nbase"));
		this.setQuerypara((String) this.getFormHM().get("querypara"));
		this.setBelong_type((String) this.getFormHM().get("belong_type"));
		this.setPhoto((String) this.getFormHM().get("photo"));
		this.setCan_edit((String) this.getFormHM().get("can_edit"));
		this.setIsself((String) this.getFormHM().get("isself"));
		this.setScope((String) this.getFormHM().get("scope"));
		this.setIsLeader((String) this.getFormHM().get("isLeader"));
		this.setA0100((String) this.getFormHM().get("a0100"));
		this.setNbase((String) this.getFormHM().get("nbase"));
		this.setNbaseA0100((String) this.getFormHM().get("nbaseA0100"));
		this.setIsemail((String) this.getFormHM().get("isemail"));

		this.setReturnurl((String) this.getFormHM().get("returnurl"));
		this.setYearListStr((String) this.getFormHM().get("yearList"));
		this.setZhouzj((String) this.getFormHM().get("zhouzj"));
		this.setZhouzjpx((String) this.getFormHM().get("zhouzjpx"));
		this.setSummaryTypeJson((String) this.getFormHM().get("summaryTypeJson"));
		this.setPersonCycleFunction((String) this.getFormHM().get("personCycleFunction"));
		this.setOrgCycleFunction((String) this.getFormHM().get("orgCycleFunction"));
	}

	public int getWorkSummaryID() {
		return workSummaryID;
	}

	public void setWorkSummaryID(int workSummaryID) {
		this.workSummaryID = workSummaryID;
	}

	public String getThisWorkSummary() {
		return thisWorkSummary;
	}

	public void setThisWorkSummary(String thisWorkSummary) {
		this.thisWorkSummary = thisWorkSummary;
	}

	public String getNextWorkSummary() {
		return nextWorkSummary;
	}

	public void setNextWorkSummary(String nextWorkSummary) {
		this.nextWorkSummary = nextWorkSummary;
	}

	public int getWorkSummaryType() {
		return workSummaryType;
	}

	public void setWorkSummaryType(int workSummaryType) {
		this.workSummaryType = workSummaryType;
	}

	public String getWorkSummarySign() {
		return workSummarySign;
	}

	public void setWorkSummarySign(String workSummarySign) {
		this.workSummarySign = workSummarySign;
	}

	public Date getStartTime() {
		return workSummaryStartTime;
	}

	public void setStartTime(Date startTime) {
		this.workSummaryStartTime = startTime;
	}

	public Date getEndTime() {
		return workSummaryEndTime;
	}

	public void setEndTime(Date endTime) {
		this.workSummaryEndTime = endTime;
	}

	public String getWhichWeek() {
		return whichWeek;
	}

	public void setWhichWeek(String whichWeek) {
		this.whichWeek = whichWeek;
	}

	public String getSelectdate() {
		return selectdate;
	}

	public void setSelectdate(String selectdate) {
		this.selectdate = selectdate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getWeeknum() {
		return weeknum;
	}

	public void setWeeknum(String weeknum) {
		this.weeknum = weeknum;
	}

	public String getP0115() {
		return p0115;
	}

	public void setP0115(String p0115) {
		this.p0115 = p0115;
	}

	public String getP0100() {
		return p0100;
	}

	public void setP0100(String p0100) {
		this.p0100 = p0100;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getP0114() {
		return p0114;
	}

	public void setP0114(String p0114) {
		this.p0114 = p0114;
	}

	public String getP011503() {
		return p011503;
	}

	public void setP011503(String p011503) {
		this.p011503 = p011503;
	}

	public String getP011501() {
		return p011501;
	}

	public void setP011501(String p011501) {
		this.p011501 = p011501;
	}

	public String getE0122() {
		return e0122;
	}

	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}

	public String getRemindNum() {
		return remindNum;
	}

	public void setRemindNum(String remindNum) {
		this.remindNum = remindNum;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getQuerypara() {
		return querypara;
	}

	public void setQuerypara(String querypara) {
		this.querypara = querypara;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getDeptdesc() {
		return deptdesc;
	}

	public void setDeptdesc(String deptdesc) {
		this.deptdesc = deptdesc;
	}

	public String getP0113() {
		return p0113;
	}

	public void setP0113(String p0113) {
		this.p0113 = p0113;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getCycle() {
		return cycle;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYear() {
		return year;
	}

	public String getCan_edit() {
		return can_edit;
	}

	public void setCan_edit(String canEdit) {
		can_edit = canEdit;
	}

	public String getIsself() {
		return isself;
	}

	public void setIsself(String isself) {
		this.isself = isself;
	}

	public void setWeekStart(String weekStart) {
		this.weekStart = weekStart;
	}

	public String getWeekStart() {
		return weekStart;
	}

	public void setWeekEnd(String weekEnd) {
		this.weekEnd = weekEnd;
	}

	public String getWeekEnd() {
		return weekEnd;
	}

	public String getUser_a0101() {
		return user_a0101;
	}

	public void setUser_a0101(String userA0101) {
		user_a0101 = userA0101;
	}

	public String getUser_photo() {
		return user_photo;
	}

	public void setUser_photo(String userPhoto) {
		user_photo = userPhoto;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getYearListStr() {
		return yearListStr;
	}

	public void setYearListStr(String yearList) {
		this.yearListStr = yearList;
	}

	public String getMaptype() {
		return maptype;
	}

	public void setMaptype(String maptype) {
		this.maptype = maptype;
	}

	public String getTypetitle() {
		return typetitle;
	}

	public void setTypetitle(String typetitle) {
		this.typetitle = typetitle;
	}

	public String getB01ps() {
		return b01ps;
	}

	public void setB01ps(String b01ps) {
		this.b01ps = b01ps;
	}

	public String getNbaseA0100() {
		return nbaseA0100;
	}

	public void setNbaseA0100(String nbaseA0100) {
		this.nbaseA0100 = nbaseA0100;
	}

	public String getIsLeader() {
		return isLeader;
	}

	public void setIsLeader(String isLeader) {
		this.isLeader = isLeader;
	}

	public String getUser_a0100() {
		return user_a0100;
	}

	public void setUser_a0100(String userA0100) {
		user_a0100 = userA0100;
	}

	public String getUser_nbase() {
		return user_nbase;
	}

	public void setUser_nbase(String userNbase) {
		user_nbase = userNbase;
	}

	public String getIshr() {
		return ishr;
	}

	public void setIshr(String ishr) {
		this.ishr = ishr;
	}

	public String getBelong_type() {
		return belong_type;
	}

	public void setBelong_type(String belongType) {
		belong_type = belongType;
	}

	public String getIsemail() {
		return isemail;
	}

	public void setIsemail(String isemail) {
		this.isemail = isemail;
	}

	public String getReturnurl() {
		return returnurl;
	}

	public void setReturnurl(String returnurl) {
		this.returnurl = returnurl;
	}

    public String getPersonCycleFunction() {
        return personCycleFunction;
    }

    public void setPersonCycleFunction(String personCycleFunction) {
        this.personCycleFunction = personCycleFunction;
    }

    public String getOrgCycleFunction() {
        return orgCycleFunction;
    }

    public void setOrgCycleFunction(String orgCycleFunction) {
        this.orgCycleFunction = orgCycleFunction;
    }
}
