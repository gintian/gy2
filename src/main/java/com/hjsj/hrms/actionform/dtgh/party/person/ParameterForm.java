package com.hjsj.hrms.actionform.dtgh.party.person;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * 
 * @author xujian
 *Feb 20, 2010
 */
public class ParameterForm extends FrameForm {

	private String polity;//政治面貌
	private ArrayList politylist;
	private String party;//党员
	private String preparty;//预备党员
	private String important;//重要发展对象
	private String active;//入党积极分子
	private String application;//申请入党
	private String member;//团员
	private String person;//群众
	private String belongparty;//所属党组织指标，人员基本情况子集，关联代码类64的指标
	private ArrayList belongpartylist = new ArrayList();
	private String belongmember;//所属团组织指标，人员基本情况子集，关联代码类65的指标
	private ArrayList belongmemberlist = new ArrayList();
	private String belongmeet;//所属工会组织指标，人员基本情况子集，关联代码类66的指标
	private ArrayList belongmeetlist = new ArrayList();
	private String codesetid;
	private String polityview;
	private String personview;
	private String param;//区分设置业务办理模板
	private String add;
	private String addview;
	private String up;
	private String upview;
	private String leave;
	private String leaveview;
	private String iin;
	private String iinview;
	private String out;
	private String outview;
	private String resumeparty;
	private String resumepartyview;
	private String resumemember;
	private String resumememberview;
	private String select_id;
	private String bs_tree;
	private String returnvalue = "1";

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
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
		this.getFormHM().put("add", add);
		this.getFormHM().put("up", up);
		this.getFormHM().put("leave", leave);
		this.getFormHM().put("iin", iin);
		this.getFormHM().put("out", out);
		this.getFormHM().put("resumeparty", resumeparty);
		this.getFormHM().put("resumemember", resumemember);
		this.getFormHM().put("param", param);
	}


	@Override
    public void outPutFormHM() {
		this.setBelongparty((String)this.getFormHM().get("belongparty"));
		this.setBelongpartylist((ArrayList)this.getFormHM().get("belongpartylist"));
		this.setBelongmember((String)this.getFormHM().get("belongmember"));
		this.setBelongmemberlist((ArrayList)this.getFormHM().get("belongmemberlist"));
		this.setBelongmeet((String)this.getFormHM().get("belongmeet"));
		this.setBelongmeetlist((ArrayList)this.getFormHM().get("belongmeetlist"));
		this.setPolity((String)this.getFormHM().get("polity"));
		this.setPolitylist((ArrayList)this.getFormHM().get("politylist"));
		this.setParty((String)this.getFormHM().get("party"));
		this.setPreparty((String)this.getFormHM().get("preparty"));
		this.setImportant((String)this.getFormHM().get("important"));
		this.setActive((String)this.getFormHM().get("active"));
		this.setApplication((String)this.getFormHM().get("application"));
		this.setMember((String)this.getFormHM().get("member"));
		this.setPerson((String)this.getFormHM().get("person"));
		this.setPersonview((String)this.getFormHM().get("personview"));
		this.setPolityview((String)this.getFormHM().get("polityview"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setBs_tree((String)this.getFormHM().get("bs_tree"));
		this.setAdd((String)this.getFormHM().get("add"));
		this.setAddview((String)this.getFormHM().get("addview"));
		this.setUp((String)this.getFormHM().get("up"));
		this.setUpview((String)this.getFormHM().get("upview"));
		this.setLeave((String)this.getFormHM().get("leave"));
		this.setLeaveview((String)this.getFormHM().get("leaveview"));
		this.setIin((String)this.getFormHM().get("iin"));
		this.setIinview((String)this.getFormHM().get("iinview"));
		this.setOut((String)this.getFormHM().get("out"));
		this.setOutview((String)this.getFormHM().get("outview"));
		this.setResumeparty((String)this.getFormHM().get("resumeparty"));
		this.setResumepartyview((String)this.getFormHM().get("resumepartyview"));
		this.setResumemember((String)this.getFormHM().get("resumemember"));
		this.setResumememberview((String)this.getFormHM().get("resumememberview"));
	}

	public String getPolity() {
		return polity;
	}

	public void setPolity(String polity) {
		this.polity = polity;
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

	public ArrayList getBelongpartylist() {
		return belongpartylist;
	}

	public void setBelongpartylist(ArrayList belongpartylist) {
		this.belongpartylist = belongpartylist;
	}

	public ArrayList getBelongmemberlist() {
		return belongmemberlist;
	}

	public void setBelongmemberlist(ArrayList belongmemberlist) {
		this.belongmemberlist = belongmemberlist;
	}

	public ArrayList getBelongmeetlist() {
		return belongmeetlist;
	}

	public void setBelongmeetlist(ArrayList belongmeetlist) {
		this.belongmeetlist = belongmeetlist;
	}

	public ArrayList getPolitylist() {
		return politylist;
	}

	public void setPolitylist(ArrayList politylist) {
		this.politylist = politylist;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getPolityview() {
		return polityview;
	}

	public void setPolityview(String polityview) {
		this.polityview = polityview;
	}

	public String getPersonview() {
		return personview;
	}

	public void setPersonview(String personview) {
		this.personview = personview;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getAdd() {
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
	}

	public String getSelect_id() {
		return select_id;
	}

	public void setSelect_id(String select_id) {
		this.select_id = select_id;
	}

	public String getBs_tree() {
		return bs_tree;
	}

	public void setBs_tree(String bs_tree) {
		this.bs_tree = bs_tree;
	}

	public String getLeave() {
		return leave;
	}

	public void setLeave(String leave) {
		this.leave = leave;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public String getIin() {
		return iin;
	}

	public void setIin(String iin) {
		this.iin = iin;
	}

	public String getAddview() {
		return addview;
	}

	public void setAddview(String addview) {
		this.addview = addview;
	}

	public String getLeaveview() {
		return leaveview;
	}

	public void setLeaveview(String leaveview) {
		this.leaveview = leaveview;
	}

	public String getIinview() {
		return iinview;
	}

	public void setIinview(String iinview) {
		this.iinview = iinview;
	}

	public String getOutview() {
		return outview;
	}

	public void setOutview(String outview) {
		this.outview = outview;
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
	}

	public String getUpview() {
		return upview;
	}

	public void setUpview(String upview) {
		this.upview = upview;
	}

	public String getResumeparty() {
		return resumeparty;
	}

	public void setResumeparty(String resumeparty) {
		this.resumeparty = resumeparty;
	}

	public String getResumepartyview() {
		return resumepartyview;
	}

	public void setResumepartyview(String resumepartyview) {
		this.resumepartyview = resumepartyview;
	}

	public String getResumemember() {
		return resumemember;
	}

	public void setResumemember(String resumemember) {
		this.resumemember = resumemember;
	}

	public String getResumememberview() {
		return resumememberview;
	}

	public void setResumememberview(String resumememberview) {
		this.resumememberview = resumememberview;
	}


    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }


    public String getReturnvalue() {
        return returnvalue;
    }

}
