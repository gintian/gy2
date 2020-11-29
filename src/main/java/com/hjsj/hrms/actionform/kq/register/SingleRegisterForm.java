package com.hjsj.hrms.actionform.kq.register;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
public class SingleRegisterForm extends FrameForm {
	private String userbase;
	private String code;
	private ArrayList forms=new ArrayList();
	private String a0100;
	private String a0101;
	private String b0110;
	private String e0122;
	private String e01a1;
	private String sqlstr;
	private String strwhere;
	private String orderby;
	private String columns;
	private String registerdate;//登记日期
	private String kind;
	private ArrayList singfielditemlist=new ArrayList();
	private String num;
	private String start_date;
	private String end_date;
	private String onedate;
	private String b0110_value;
	private ArrayList yearlist=new ArrayList();
	private ArrayList durationlist=new ArrayList();	
	private String cur_year;
	private String cur_duration;
	private String condition;//高级花名册打印的条件
    private String returnURL;//返回的连接
    private String returnURL2;//返回的连接2
	private String relatTableid;//高级花名册对应的单表名称
	private String org_name;
	private String rflag;
	private String marker;
	private String up_dailyregister; //修改日明细登记数据
	private String lockedNum;
	public String getRflag() {
		return rflag;
	}
	public void setRflag(String rflag) {
		this.rflag = rflag;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getCur_year() {
		return cur_year;
	}
	public void setCur_year(String cur_year) {
		this.cur_year = cur_year;
	}
	public ArrayList getYearlist() {
		return yearlist;
	}
	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}
	public String getB0110_value() {
		return b0110_value;
	}
	public void setB0110_value(String b0110_value) {
		this.b0110_value = b0110_value;
	}
	public String getOnedate() {
		return onedate;
	}
	public void setOnedate(String onedate) {
		this.onedate = onedate;
	}
	 private String rest_date;
	@Override
    public void outPutFormHM(){
		this.setUserbase((String)this.getFormHM().get("userbase"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setSingfielditemlist((ArrayList)this.getFormHM().get("singfielditemlist"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setE0122((String)this.getFormHM().get("e0122"));
		this.setE01a1((String)this.getFormHM().get("e01a1"));
		this.setNum((String)this.getFormHM().get("num"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setRest_date((String)this.getFormHM().get("rest_date"));
		this.setOnedate((String)this.getFormHM().get("onedate"));
		this.setB0110_value((String)this.getFormHM().get("b0110_value"));
		this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		this.setDurationlist((ArrayList)this.getFormHM().get("durationlist"));
		this.setCur_year((String)this.getFormHM().get("cur_year"));
		this.setCur_duration((String)this.getFormHM().get("cur_duration"));
		this.setRelatTableid((String)this.getFormHM().get("relatTableid"));
		this.setCondition((String)this.getFormHM().get("condition"));
		this.setReturnURL((String)this.getFormHM().get("returnURL"));
		this.setReturnURL2((String)this.getFormHM().get("returnURL2"));
		this.setRegisterdate((String)this.getFormHM().get("registerdate"));
		this.setOrg_name((String)this.getFormHM().get("org_name"));
		this.setRflag((String)this.getFormHM().get("rflag"));
		this.setMarker((String)this.getFormHM().get("marker"));
		this.setUp_dailyregister((String)this.getFormHM().get("up_dailyregister"));
		this.setLockedNum((String)this.getFormHM().get("lockedNum"));
	}
	@Override
    public void inPutTransHM(){
		this.getFormHM().put("userbase",userbase);			
		this.getFormHM().put("code",code);		
		this.getFormHM().put("forms",forms);
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("b0110",this.getB0110());
		this.getFormHM().put("registerdate",this.getRegisterdate());
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
		this.getFormHM().put("cur_year",this.getCur_year());
		this.getFormHM().put("cur_duration", this.getCur_duration());
		this.getFormHM().put("marker", this.getMarker());
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ArrayList getForms() {
		return forms;
	}
	public void setForms(ArrayList forms) {
		this.forms = forms;
	}
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0100) {
		this.b0110 = b0100;
	}
	public String getE0122() {
		return e0122;
	}
	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}
	public String getE01a1() {
		return e01a1;
	}
	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getStrwhere() {
		return strwhere;
	}
	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}
	public String getRegisterdate() {
		return registerdate;
	}
	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}
	public String getA0101() {
		return a0101;
	}
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
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
	public String getRest_date() {
		return rest_date;
	}
	public void setRest_date(String rest_date) {
		this.rest_date = rest_date;
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
	public ArrayList getSingfielditemlist() {
		return singfielditemlist;
	}
	public void setSingfielditemlist(ArrayList singfielditemlist) {
		this.singfielditemlist = singfielditemlist;
	}
	public String getMarker() {
		return marker;
	}
	public void setMarker(String marker) {
		this.marker = marker;
	}
	public String getUp_dailyregister() {
		return up_dailyregister;
	}
	public void setUp_dailyregister(String up_dailyregister) {
		this.up_dailyregister = up_dailyregister;
	}
	public ArrayList getDurationlist() {
		return durationlist;
	}
	public void setDurationlist(ArrayList durationlist) {
		this.durationlist = durationlist;
	}
	public String getCur_duration() {
		return cur_duration;
	}
	public void setCur_duration(String cur_duration) {
		this.cur_duration = cur_duration;
	}
	public String getLockedNum() {
		return lockedNum;
	}
	public void setLockedNum(String lockedNum) {
		this.lockedNum = lockedNum;
	}
	public String getReturnURL2() {
		return returnURL2;
	}
	public void setReturnURL2(String returnURL2) {
		this.returnURL2 = returnURL2;
	}
	
}
