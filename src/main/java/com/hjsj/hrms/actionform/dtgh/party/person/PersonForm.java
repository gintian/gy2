package com.hjsj.hrms.actionform.dtgh.party.person;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class PersonForm extends FrameForm {

	private String param;//区分是党、团、工会
	private String backdate;
	private String codesetid;
	private String codesetdesc;
	private String codemess;
	private String isShowCondition;
	private String partylike;//显示当前组织单元下所有机构人员 
	private String dbcond;//查询所有人员库sql语句
	private String userbase;//选择的人员库
	private String select_name;//查询的姓名
	private ArrayList queryfieldlist = new ArrayList();//查询时显示的指标
	private String querylike;//是否模糊查询
	private ArrayList browsefields=new ArrayList();//显示的指标
	private String strsql;
	private String cond_str;
	private String fieldstr;
	private String columns;
	private String order_by;
	private String query;//区分是不是点击的查询
	private String politics;//政治面貌按钮
	private String polity;//政治面貌
	private String party;//党员
	private String preparty;//预备党员
	private String important;//重要发展对象
	private String active;//入党积极分子
	private String application;//申请入党
	private String member;//团员
	private String person;//群众
	private String belongparty;//所属党组织指标，人员基本情况子集，关联代码类64的指标
	private String belongmember;//所属团组织指标，人员基本情况子集，关联代码类65的指标
	private String belongmeet;//所属工会组织指标，人员基本情况子集，关联代码类66的指标
	private String add;
	private String up;
	private String leave;
	private String iin;
	private String out;
	private String resumeparty;
	private String resumemember;
	private String a_code;
	private String tabIndex="0";
	private String uplevel="0";
	private String factor;
	private String expr;
	private String likeflag;
	private String returnvalue = "1";

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

		this.getFormHM().put("param", param);
		this.getFormHM().put("partylike", partylike);
		this.getFormHM().put("fieldstr", fieldstr);
		this.getFormHM().put("query", query);
		this.getFormHM().put("querylike", querylike);
		this.getFormHM().put("select_name", select_name);
		this.getFormHM().put("queryfieldlist", queryfieldlist);
		this.getFormHM().put("userbase", userbase);
		this.getFormHM().put("politics", politics);
		if(this.getPagination()!=null)
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		//参数设置
		this.getFormHM().put("belongparty", this.getBelongparty());
		this.getFormHM().put("belongmember", belongmember);
		this.getFormHM().put("polity", polity);
		this.getFormHM().put("belongmeet", belongmeet);
		this.getFormHM().put("party", party);
		this.getFormHM().put("preparty", preparty);
		this.getFormHM().put("active", active);
		this.getFormHM().put("application", application);
		this.getFormHM().put("member", member);
		this.getFormHM().put("person", person);
		this.getFormHM().put("important", important);
		this.getFormHM().put("a_code", a_code);
		this.getFormHM().put("expr", expr);
		this.getFormHM().put("factor", factor);
		this.getFormHM().put("likeflag", likeflag);
	}


	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setCodesetdesc((String)this.getFormHM().get("codesetdesc"));
		this.setCodemess((String)this.getFormHM().get("codemess"));
		this.setPartylike((String)this.getFormHM().get("partylike"));
		this.setSelect_name((String)this.getFormHM().get("select_name"));
		this.setQueryfieldlist((ArrayList)this.getFormHM().get("queryfieldlist"));
		this.setQuerylike((String)this.getFormHM().get("querylike"));
		this.setQuery((String)this.getFormHM().get("query"));
		this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setFieldstr((String)this.getFormHM().get("fieldstr"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setDbcond((String)this.getFormHM().get("cond"));
		this.setUserbase((String)this.getFormHM().get("userbase"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setCond_str((String)this.getFormHM().get("cond_str"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		//参数设置
		this.setPolitics((String)this.getFormHM().get("politics"));
		this.setBelongparty((String)this.getFormHM().get("belongparty"));
		this.setBelongmember((String)this.getFormHM().get("belongmember"));
		this.setBelongmeet((String)this.getFormHM().get("belongmeet"));
		this.setPolity((String)this.getFormHM().get("polity"));
		this.setParty((String)this.getFormHM().get("party"));
		this.setPreparty((String)this.getFormHM().get("preparty"));
		this.setImportant((String)this.getFormHM().get("important"));
		this.setActive((String)this.getFormHM().get("active"));
		this.setApplication((String)this.getFormHM().get("application"));
		this.setMember((String)this.getFormHM().get("member"));
		this.setPerson((String)this.getFormHM().get("person"));
		this.setAdd((String)this.getFormHM().get("add"));
		this.setUp((String)this.getFormHM().get("up"));
		this.setLeave((String)this.getFormHM().get("leave"));
		this.setIin((String)this.getFormHM().get("iin"));
		this.setOut((String)this.getFormHM().get("out"));
		this.setResumeparty((String)this.getFormHM().get("resumeparty"));
		this.setResumemember((String)this.getFormHM().get("resumemember"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setLikeflag((String)this.getFormHM().get("likeflag"));
		this.setFactor((String)this.getFormHM().get("factor"));
		this.setExpr((String)this.getFormHM().get("expr"));
		
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getCodesetdesc() {
		return codesetdesc;
	}

	public void setCodesetdesc(String codesetdesc) {
		this.codesetdesc = codesetdesc;
	}

	public String getCodemess() {
		return codemess;
	}

	public void setCodemess(String codemess) {
		this.codemess = codemess;
	}

	public String getIsShowCondition() {
		return isShowCondition;
	}

	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}

	public String getPartylike() {
		return partylike;
	}

	public void setPartylike(String partylike) {
		this.partylike = partylike;
	}

	public String getDbcond() {
		return dbcond;
	}

	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}

	public String getUserbase() {
		return userbase;
	}

	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}

	public String getSelect_name() {
		return select_name;
	}

	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}

	public ArrayList getQueryfieldlist() {
		return queryfieldlist;
	}

	public void setQueryfieldlist(ArrayList queryfieldlist) {
		this.queryfieldlist = queryfieldlist;
	}

	public String getQuerylike() {
		return querylike;
	}

	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getCond_str() {
		return cond_str;
	}

	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}

	public ArrayList getBrowsefields() {
		return browsefields;
	}

	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getFieldstr() {
		return fieldstr;
	}

	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getPolity() {
		return polity;
	}

	public void setPolity(String polity) {
		this.polity = polity;
	}

	public String getPolitics() {
		return politics;
	}

	public void setPolitics(String politics) {
		this.politics = politics;
	}

	public String getParty() {
		return party;
	}

	public void setParty(String party) {
		this.party = party;
	}

	public String getPreparty() {
		return preparty;
	}

	public void setPreparty(String preparty) {
		this.preparty = preparty;
	}

	public String getImportant() {
		return important;
	}

	public void setImportant(String important) {
		this.important = important;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getBelongparty() {
		return belongparty;
	}

	public void setBelongparty(String belongparty) {
		this.belongparty = belongparty;
	}

	public String getBelongmember() {
		return belongmember;
	}

	public void setBelongmember(String belongmember) {
		this.belongmember = belongmember;
	}

	public String getBelongmeet() {
		return belongmeet;
	}

	public void setBelongmeet(String belongmeet) {
		this.belongmeet = belongmeet;
	}

	public String getAdd() {
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
	}

	public String getLeave() {
		return leave;
	}

	public void setLeave(String leave) {
		this.leave = leave;
	}

	public String getIin() {
		return iin;
	}

	public void setIin(String iin) {
		this.iin = iin;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public String getResumeparty() {
		return resumeparty;
	}

	public void setResumeparty(String resumeparty) {
		this.resumeparty = resumeparty;
	}

	public String getResumemember() {
		return resumemember;
	}

	public void setResumemember(String resumemember) {
		this.resumemember = resumemember;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}


	public String getTabIndex() {
		return tabIndex;
	}


	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}


	public String getUplevel() {
		return uplevel;
	}


	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}


	public String getFactor() {
		return factor;
	}


	public void setFactor(String factor) {
		this.factor = factor;
	}


	public String getExpr() {
		return expr;
	}


	public void setExpr(String expr) {
		this.expr = expr;
	}


	public String getLikeflag() {
		return likeflag;
	}


	public void setLikeflag(String likeflag) {
		this.likeflag = likeflag;
	}


    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }


    public String getReturnvalue() {
        return returnvalue;
    }

}
