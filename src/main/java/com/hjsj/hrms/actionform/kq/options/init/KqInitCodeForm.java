package com.hjsj.hrms.actionform.kq.options.init;

import com.hrms.struts.action.FrameForm;

public class KqInitCodeForm extends FrameForm {

	private String out;//公出
	private String q19;//调班申请
	private String rest;//请假申请
	private String q21;//替班申请
	private String outime;//加班
	private String shift;//员工排班信息表 
	private String staffl;//员工日明细
	private String txsq;//调休申请表
	private String staffy;//员工月汇总
	private String ypsk;//员工刷卡信息表
	private String deptl;//部门明细表
	private String jqgl;//假期信息表
	private String depty;//部门月汇总
	private String all_init;
	private String scope="1";
	private String kqbz;
	private String bzry;//班组人员
	private String Tstart;
	private String kqorg;  //单位部门排班表
	private String dxjb;//调休加班明细表
	
	private String Tend;
	
	private String mess;
	private String rypb;  //员工排班信息表
	private String ygsk;  //员工刷卡数据表
	private String count_start;
	private String count_end;
	private String erro; // 错误信息
	
	private String struts;
	private String ress;
	private String fest;
	private String daoxiu;
	private String isSu;
	
	private String group;
	
	private String kqCard;  // 考勤卡号
	private String kqType;  // 考勤方式
	
	
	
	public String getKqCard() {
		return kqCard;
	}

	public void setKqCard(String kqCard) {
		this.kqCard = kqCard;
	}

	public String getKqType() {
		return kqType;
	}

	public void setKqType(String kqType) {
		this.kqType = kqType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDxjb()
    {
        return dxjb;
    }

    public void setDxjb(String dxjb)
    {
        this.dxjb = dxjb;
    }

    public String getIsSu() {
		return isSu;
	}

	public void setIsSu(String isSu) {
		this.isSu = isSu;
	}

	public String getStruts() {
		return struts;
	}

	public void setStruts(String struts) {
		this.struts = struts;
	}

	public String getRess() {
		return ress;
	}

	public void setRess(String ress) {
		this.ress = ress;
	}

	public String getFest() {
		return fest;
	}

	public void setFest(String fest) {
		this.fest = fest;
	}

	public String getDaoxiu() {
		return daoxiu;
	}

	public void setDaoxiu(String daoxiu) {
		this.daoxiu = daoxiu;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setScope((String)this.getFormHM().get("scope"));
		this.setMess((String)this.getFormHM().get("mess"));
		this.setTstart((String)this.getFormHM().get("Tstart"));
		this.setTend((String)this.getFormHM().get("Tend"));
		this.setCount_start((String)this.getFormHM().get("count_start"));
		this.setCount_end((String)this.getFormHM().get("count_end"));
		this.setErro((String) this.getFormHM().get("erro"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("bzry", bzry);
		this.getFormHM().put("kqbz", kqbz);
		this.getFormHM().put("out",(String)this.getOut());
		this.getFormHM().put("rest",(String)this.getRest());
		this.getFormHM().put("outime",(String)this.getOutime());
		this.getFormHM().put("staffl",(String)this.getStaffl());
		this.getFormHM().put("staffy",(String)this.getStaffy());
		this.getFormHM().put("deptl",(String)this.getDeptl());
		this.getFormHM().put("depty",(String)this.getDepty());
		this.getFormHM().put("scope",(String)this.getScope());
		this.getFormHM().put("Tstart",(String)this.getTstart());
		this.getFormHM().put("Tend",(String)this.getTend());
		this.getFormHM().put("q19",this.getQ19());
		this.getFormHM().put("q21",this.getQ21());
		this.getFormHM().put("shift",this.getShift());
		this.getFormHM().put("txsq",this.getTxsq());
		this.getFormHM().put("ypsk",this.getYpsk());
		this.getFormHM().put("jqgl",this.getJqgl());
		this.getFormHM().put("all_init",this.getAll_init());
		this.getFormHM().put("kqorg",this.getKqorg());
		this.getFormHM().put("rypb",this.getRypb());
		this.getFormHM().put("ygsk",this.getYgsk());
		this.getFormHM().put("count_start", this.getCount_start());
		this.getFormHM().put("count_end", this.getCount_end());
		this.getFormHM().put("dxjb", this.getDxjb());
		
		this.getFormHM().put("struts", this.getStruts());
		this.getFormHM().put("ress", this.getRess());
		this.getFormHM().put("fest", this.getFest());
		this.getFormHM().put("daoxiu",this.getDaoxiu());
		this.getFormHM().put("isSu", this.getIsSu());
		this.getFormHM().put("group", this.getGroup());
		
		this.getFormHM().put("kqCard", this.getKqCard());
		this.getFormHM().put("kqType", this.getKqType());
	}

	public String getDeptl() {
		return deptl;
	}

	public void setDeptl(String deptl) {
		this.deptl = deptl;
	}

	public String getDepty() {
		return depty;
	}

	public void setDepty(String depty) {
		this.depty = depty;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public String getOutime() {
		return outime;
	}

	public void setOutime(String outime) {
		this.outime = outime;
	}

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

	public String getStaffl() {
		return staffl;
	}

	public void setStaffl(String staffl) {
		this.staffl = staffl;
	}

	public String getStaffy() {
		return staffy;
	}

	public void setStaffy(String staffy) {
		this.staffy = staffy;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getTend() {
		return Tend;
	}

	public void setTend(String tend) {
		Tend = tend;
	}

	public String getTstart() {
		return Tstart;
	}

	public void setTstart(String tstart) {
		Tstart = tstart;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public String getJqgl() {
		return jqgl;
	}

	public void setJqgl(String jqgl) {
		this.jqgl = jqgl;
	}

	public String getQ19() {
		return q19;
	}

	public void setQ19(String q19) {
		this.q19 = q19;
	}

	public String getQ21() {
		return q21;
	}

	public void setQ21(String q21) {
		this.q21 = q21;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getTxsq() {
		return txsq;
	}

	public void setTxsq(String txsq) {
		this.txsq = txsq;
	}

	public String getYpsk() {
		return ypsk;
	}

	public void setYpsk(String ypsk) {
		this.ypsk = ypsk;
	}

	public String getAll_init() {
		return all_init;
	}

	public void setAll_init(String all_init) {
		this.all_init = all_init;
	}

	public String getBzry() {
		return bzry;
	}

	public void setBzry(String bzry) {
		this.bzry = bzry;
	}

	public String getKqbz() {
		return kqbz;
	}

	public void setKqbz(String kqbz) {
		this.kqbz = kqbz;
	}

	public String getKqorg() {
		return kqorg;
	}

	public void setKqorg(String kqorg) {
		this.kqorg = kqorg;
	}

	public String getRypb() {
		return rypb;
	}

	public void setRypb(String rypb) {
		this.rypb = rypb;
	}

	public String getYgsk() {
		return ygsk;
	}

	public void setYgsk(String ygsk) {
		this.ygsk = ygsk;
	}

	public String getCount_start() {
		return count_start;
	}

	public void setCount_start(String count_start) {
		this.count_start = count_start;
	}

	public String getCount_end() {
		return count_end;
	}

	public void setCount_end(String count_end) {
		this.count_end = count_end;
	}

}
