package com.hjsj.hrms.actionform.performance.commend.insupportcommend;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>title:InSupportCommendForm.java</p>
 * <p>description:后备干部推荐的form</p>
 * <p>company:HJSJ</p>
 * <p>create time: 2007.05.25 11:00:00 am</p>
 * @author lizhenwei
 * @version 4.0
 */

public class InSupportCommendForm extends FrameForm {
//p02
	/** 序号pk*/
	private String p0201;
	/**名称*/
	private String p0203;
	/**创建日期*/
	private String p0205;
	/**起始日期*/
	private String p0206;
	/**结束日期*/
	private String p0207;
	/**状态标识*/
	private String state;
	/**控制参数*/
	private String ctrl_param;
//p03
	/**人员编号*/
	private String a0100;
	/**应用库前缀*/
	private String nbase;
	/**序号fk*/
	//p0201
	/**单位编码*/
	private String b0110;
	/**部门编码*/
	private String e0122;
	/**人员姓名*/
	private String a0101;
	/**得票数*/
	private int p0304;
	/**推荐范围标识*/
	private String p0305;
	/**推荐范围编码*/
	private String p0307;
//per_talent_vote
	/**推荐人员帐号*/
	private String logon_id;
	/**人员编号fk*/
	//a0100
	/**应用库前缀fk*/
	//nbase
	/**序号fk*/
	//p0201
	/**执行中的后备推荐列表*/
	private ArrayList commendList = new ArrayList();
	/**候选人列表*/
	private ArrayList candidateList = new ArrayList();
	private String year;
	private String sql;
	private String whl_sql;
	private String tabname;
	private String outName;
	
	private ArrayList parameterSetList = new ArrayList();
	private ArrayList analyseVoteList = new ArrayList();
	private ArrayList executeVoteAnalyseList = new ArrayList();
	private ArrayList sysList = new ArrayList();
	private String have;
	private String name;
	/**推荐职务指标列表*/
	private ArrayList commendFieldList = new ArrayList();
	/**推荐职务指标*/
	private String commendField;
	private String p0209;
	/**结束的后备推荐列表*/
	private ArrayList finishCommendList = new ArrayList();
	/**投票状况分析列表*/
	private ArrayList voteStatusList = new ArrayList();
	/**权限内的应用库前缀列表*/
	private ArrayList privDbList = new ArrayList();
	/**应用库前缀*/
	private String dbname;
	/**权限范围内的应用库前缀，多个用,分隔*/
	private String privPre;
	private ArrayList umList = new ArrayList();
	private String um;
	private ArrayList yearList=new ArrayList();
	
	private String disabled;
	/**投票人类型列表*/
	private ArrayList preslist=new ArrayList();
	/**推荐职务列表*/
	private ArrayList codeslist=new ArrayList();
	/**人员库列表*/
	private ArrayList dbprelist=new ArrayList();
	/**人员库状态*/
	private ArrayList dbpre_list=new ArrayList();
	/**投票人类型状态*/
	private ArrayList body_list=new ArrayList();
	/**推荐职务状态*/
	private ArrayList pos_list=new ArrayList();
	/**自动编号列表*/
	private ArrayList autolist=new ArrayList();
	private String footer;//推荐票填写说明
	private String autonum;
	/**扫描票数列表*/
	private String scans;
	
	@Override
    public void outPutFormHM() {
		this.setP0201((String)this.getFormHM().get("p0201"));
		this.setP0209((String)this.getFormHM().get("p0209"));
		this.setCommendList((ArrayList)this.getFormHM().get("commendList"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTabname((String)this.getFormHM().get("tabname"));
		this.setP0203((String)this.getFormHM().get("p0203"));
		this.setP0205((String)this.getFormHM().get("p0205"));
		this.setP0206((String)this.getFormHM().get("p0206"));
		this.setP0207((String)this.getFormHM().get("p0207"));
		this.setUm((String)this.getFormHM().get("um"));
		this.setState((String)this.getFormHM().get("state"));
		this.setCtrl_param((String)this.getFormHM().get("ctrl_param"));
		this.setParameterSetList((ArrayList)this.getFormHM().get("parameterSetList"));
		this.setAnalyseVoteList((ArrayList)this.getFormHM().get("analyseVoteList"));
		this.setExecuteVoteAnalyseList((ArrayList)this.getFormHM().get("executeVoteAnalyseList"));
		this.setCandidateList((ArrayList)this.getFormHM().get("candidateList"));
		this.setWhl_sql((String)this.getFormHM().get("whl_sql"));
		this.setOutName((String)this.getFormHM().get("outName"));
		this.setSysList((ArrayList)this.getFormHM().get("sysList"));
		this.setName((String)this.getFormHM().get("name"));
		this.setHave((String)this.getFormHM().get("have"));
		this.setCommendFieldList((ArrayList)this.getFormHM().get("commendFieldList"));
		this.setFinishCommendList((ArrayList)this.getFormHM().get("finishCommendList"));
		this.setVoteStatusList((ArrayList)this.getFormHM().get("voteStatusList"));
		this.setPrivDbList((ArrayList)this.getFormHM().get("privDbList"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setPrivPre((String)this.getFormHM().get("privPre"));
		this.setUmList((ArrayList)this.getFormHM().get("umList"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setE0122((String)this.getFormHM().get("e0122"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setPreslist((ArrayList)this.getFormHM().get("preslist"));
		this.setCodeslist((ArrayList)this.getFormHM().get("codeslist"));
		this.setBody_list((ArrayList)this.getFormHM().get("body_list"));
		this.setPos_list((ArrayList)this.getFormHM().get("pos_list"));
		this.setAutolist((ArrayList)this.getFormHM().get("autolist"));
		this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
		this.setDbpre_list((ArrayList)this.getFormHM().get("dbpre_list"));
		this.setDisabled((String)this.getFormHM().get("disabled"));
		this.setScans((String)this.getFormHM().get("scans"));
		this.setAutonum((String)this.getFormHM().get("autonum"));
		this.setFooter((String)this.getFormHM().get("footer"));
	}
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("p0201",this.getP0201());
		this.getFormHM().put("p0203",this.getP0203());
		this.getFormHM().put("p0206",this.getP0206());
		this.getFormHM().put("p0207",this.getP0207());
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("ctrl_param",this.getCtrl_param());
		this.getFormHM().put("sql",this.getSql());
		this.getFormHM().put("tabname",this.getTabname());
		this.getFormHM().put("outName",this.getOutName());
		this.getFormHM().put("commendField",this.getCommendField());
        this.getFormHM().put("um",this.getUm());
        this.getFormHM().put("a0100", this.getA0100());
        this.getFormHM().put("nbase", this.getNbase());
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getA0101() {
		return a0101;
	}
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public String getCtrl_param() {
		return ctrl_param;
	}
	public void setCtrl_param(String ctrl_param) {
		this.ctrl_param = ctrl_param;
	}
	public String getE0122() {
		return e0122;
	}
	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}
	public String getLogon_id() {
		return logon_id;
	}
	public void setLogon_id(String logon_id) {
		this.logon_id = logon_id;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getP0203() {
		return p0203;
	}
	public void setP0203(String p0203) {
		this.p0203 = p0203;
	}
	public String getP0205() {
		return p0205;
	}
	public void setP0205(String p0205) {
		this.p0205 = p0205;
	}
	public String getP0206() {
		return p0206;
	}
	public void setP0206(String p0206) {
		this.p0206 = p0206;
	}
	public String getP0207() {
		return p0207;
	}
	public void setP0207(String p0207) {
		this.p0207 = p0207;
	}
	public int getP0304() {
		return p0304;
	}
	public void setP0304(int p0304) {
		this.p0304 = p0304;
	}
	public String getP0305() {
		return p0305;
	}
	public void setP0305(String p0305) {
		this.p0305 = p0305;
	}
	public String getP0307() {
		return p0307;
	}
	public void setP0307(String p0307) {
		this.p0307 = p0307;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public ArrayList getCommendList() {
		return commendList;
	}
	public void setCommendList(ArrayList commendList) {
		this.commendList = commendList;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getTabname() {
		return tabname;
	}
	public void setTabname(String tabname) {
		this.tabname = tabname;
	}
	public ArrayList getParameterSetList() {
		return parameterSetList;
	}
	public void setParameterSetList(ArrayList parameterSetList) {
		this.parameterSetList = parameterSetList;
	}
	public ArrayList getAnalyseVoteList() {
		return analyseVoteList;
	}
	public void setAnalyseVoteList(ArrayList analyseVoteList) {
		this.analyseVoteList = analyseVoteList;
	}
	public ArrayList getExecuteVoteAnalyseList() {
		return executeVoteAnalyseList;
	}
	public void setExecuteVoteAnalyseList(ArrayList executeVoteAnalyseList) {
		this.executeVoteAnalyseList = executeVoteAnalyseList;
	}
	public ArrayList getCandidateList() {
		return candidateList;
	}
	public void setCandidateList(ArrayList candidateList) {
		this.candidateList = candidateList;
	}
	public String getWhl_sql() {
		return whl_sql;
	}
	public void setWhl_sql(String whl_sql) {
		this.whl_sql = whl_sql;
	}
	public String getOutName() {
		return outName;
	}
	public void setOutName(String outName) {
		this.outName = outName;
	}
	public ArrayList getSysList() {
		return sysList;
	}
	public void setSysList(ArrayList sysList) {
		this.sysList = sysList;
	}
	public String getHave() {
		return have;
	}
	public void setHave(String have) {
		this.have = have;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList getCommendFieldList() {
		return commendFieldList;
	}
	public void setCommendFieldList(ArrayList commendFieldList) {
		this.commendFieldList = commendFieldList;
	}
	public String getCommendField() {
		return commendField;
	}
	public void setCommendField(String commendField) {
		this.commendField = commendField;
	}
	public String getP0209() {
		return p0209;
	}
	public void setP0209(String p0209) {
		this.p0209 = p0209;
	}
	public ArrayList getFinishCommendList() {
		return finishCommendList;
	}
	public void setFinishCommendList(ArrayList finishCommendList) {
		this.finishCommendList = finishCommendList;
	}
	public ArrayList getVoteStatusList() {
		return voteStatusList;
	}
	public void setVoteStatusList(ArrayList voteStatusList) {
		this.voteStatusList = voteStatusList;
	}
	public String getP0201() {
		return p0201;
	}
	public void setP0201(String p0201) {
		this.p0201 = p0201;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public ArrayList getPrivDbList() {
		return privDbList;
	}
	public void setPrivDbList(ArrayList privDbList) {
		this.privDbList = privDbList;
	}
	public String getPrivPre() {
		return privPre;
	}
	public void setPrivPre(String privPre) {
		this.privPre = privPre;
	}
	public ArrayList getUmList() {
		return umList;
	}
	public void setUmList(ArrayList umList) {
		this.umList = umList;
	}
	public String getUm() {
		return um;
	}
	public void setUm(String um) {
		this.um = um;
	}
	public ArrayList getYearList() {
		return yearList;
	}
	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public ArrayList getPreslist() {
		return preslist;
	}
	public void setPreslist(ArrayList preslist) {
		this.preslist = preslist;
	}
	public ArrayList getCodeslist() {
		return codeslist;
	}
	public void setCodeslist(ArrayList codeslist) {
		this.codeslist = codeslist;
	}
	public ArrayList getBody_list() {
		return body_list;
	}
	public void setBody_list(ArrayList body_list) {
		this.body_list = body_list;
	}
	public ArrayList getPos_list() {
		return pos_list;
	}
	public void setPos_list(ArrayList pos_list) {
		this.pos_list = pos_list;
	}
	public ArrayList getAutolist() {
		return autolist;
	}
	public void setAutolist(ArrayList autolist) {
		this.autolist = autolist;
	}
	public String getAutonum() {
		return autonum;
	}
	public void setAutonum(String autonum) {
		this.autonum = autonum;
	}
	public String getScans() {
		return scans;
	}
	public void setScans(String scans) {
		this.scans = scans;
	}
	public ArrayList getDbprelist() {
		return dbprelist;
	}
	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}
	public ArrayList getDbpre_list() {
		return dbpre_list;
	}
	public void setDbpre_list(ArrayList dbpre_list) {
		this.dbpre_list = dbpre_list;
	}
	public String getDisabled() {
		return disabled;
	}
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}

}
