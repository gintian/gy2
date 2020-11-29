package com.hjsj.hrms.actionform.hire.employActualize;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class BatchSendMailForm extends FrameForm {
	private String type="0";           // 0：发送邮件  1：群发邮件
	private String status="";          //简历状态
	private String a0100s="";
	private ArrayList mailTempList=new ArrayList();
	private String mailTempID = "";//摸板id
	private String title = "";//邮件标题
	private String content = "";//摸板内容
	private ArrayList zbj_list = new ArrayList();//指标集列表
	private ArrayList zb_list = new ArrayList();//指标列表
	private String zbj_id = "";//指标集id
	private String zb_id = "";//指标id
	private String id="";
	private String isMailField = "";
	private String dbname="";
	private String falg="tt";
	private String str_whl="";
	private String a0100;
	private String zploop;
    private String zp_pos_id="";
    private String rovkeName="";
    private String zpbatch="";
    private String codeid="";
    private String extendWhereSql="";
	@Override
    public void outPutFormHM() {
		this.setZp_pos_id((String)this.getFormHM().get("zp_pos_id"));
		this.setZploop((String)this.getFormHM().get("zploop"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setType((String)this.getFormHM().get("type"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setA0100s((String)this.getFormHM().get("a0100s"));
		this.setMailTempList((ArrayList)this.getFormHM().get("mailTempList"));
		this.setMailTempID((String)this.getFormHM().get("mailTempID"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setZbj_id((String)this.getFormHM().get("zbj_id"));
		this.setZbj_list((ArrayList)this.getFormHM().get("zbj_list"));
		this.setZb_id((String)this.getFormHM().get("zb_id"));
		this.setZb_list((ArrayList)this.getFormHM().get("zb_list"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setIsMailField((String)this.getFormHM().get("isMailField"));
		this.setId((String)this.getFormHM().get("id"));
		this.setFalg((String)this.getFormHM().get("falg"));
		this.setRovkeName((String)this.getFormHM().get("rovkeName"));
		this.setCodeid((String)this.getFormHM().get("codeid"));
		this.setExtendWhereSql((String)this.getFormHM().get("extendWhereSql"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("zp_pos_id", this.getZp_pos_id());
		this.getFormHM().put("mailTempID",this.getMailTempID());
		this.getFormHM().put("title",this.getTitle());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("id",this.getId());
		this.getFormHM().put("dbname",this.getDbname());
		this.getFormHM().put("isMailField",this.getIsMailField());
		this.getFormHM().put("str_whl",this.getStr_whl());
		this.getFormHM().put("a0100",this.getA0100());
		this.getFormHM().put("zploop",this.getZploop());
		this.getFormHM().put("zpbatch",this.getZpbatch());
		this.getFormHM().put("codeid", this.getCodeid());
		this.getFormHM().put("extendWhereSql", this.getExtendWhereSql());
	}

	public String getA0100s() {
		return a0100s;
	}

	public void setA0100s(String a0100s) {
		this.a0100s = a0100s;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMailTempID() {
		return mailTempID;
	}

	public void setMailTempID(String mailTempID) {
		this.mailTempID = mailTempID;
	}

	public ArrayList getMailTempList() {
		return mailTempList;
	}

	public void setMailTempList(ArrayList mailTempList) {
		this.mailTempList = mailTempList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getZb_id() {
		return zb_id;
	}

	public void setZb_id(String zb_id) {
		this.zb_id = zb_id;
	}

	public ArrayList getZb_list() {
		return zb_list;
	}

	public void setZb_list(ArrayList zb_list) {
		this.zb_list = zb_list;
	}

	public String getZbj_id() {
		return zbj_id;
	}

	public void setZbj_id(String zbj_id) {
		this.zbj_id = zbj_id;
	}

	public ArrayList getZbj_list() {
		return zbj_list;
	}

	public void setZbj_list(ArrayList zbj_list) {
		this.zbj_list = zbj_list;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsMailField() {
		return isMailField;
	}

	public void setIsMailField(String isMailField) {
		this.isMailField = isMailField;
	}

	public String getFalg() {
		return falg;
	}

	public void setFalg(String falg) {
		this.falg = falg;
	}

	public String getStr_whl() {
		return str_whl;
	}

	public void setStr_whl(String str_whl) {
		this.str_whl = str_whl;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getZploop() {
		return zploop;
	}

	public void setZploop(String zploop) {
		this.zploop = zploop;
	}

	public String getZp_pos_id() {
		return zp_pos_id;
	}

	public void setZp_pos_id(String zp_pos_id) {
		this.zp_pos_id = zp_pos_id;
	}
	public String getRovkeName() {
		return rovkeName;
	}

	public void setRovkeName(String rovkeName) {
		this.rovkeName = rovkeName;
	}

	public String getZpbatch() {
		return zpbatch;
	}

	public void setZpbatch(String zpbatch) {
		this.zpbatch = zpbatch;
	}
	public String getExtendWhereSql() {
		return extendWhereSql;
	}

	public void setExtendWhereSql(String extendWhereSql) {
		this.extendWhereSql = extendWhereSql;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}
}
