package com.hjsj.hrms.actionform.kq.machine;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;

public class KqRuleDataForm extends FrameForm{

  private String rule_id;
  private String tran_flag;
  private RecordVo kq_rule_vo=new RecordVo("kq_data_rule");
  private String rule_name;
  private String status_value;
  private String returnvalue="1";
  public String getRule_name() {
	return rule_name;
}
public void setRule_name(String rule_name) {
	this.rule_name = rule_name;
}
@Override
public void outPutFormHM()
  {
	  this.setKq_rule_vo((RecordVo)this.getFormHM().get("kq_rule_vo"));
	  this.setRule_id((String)this.getFormHM().get("rule_id"));
	  this.setTran_flag((String)this.getFormHM().get("tran_flag"));
	  this.setRule_name((String)this.getFormHM().get("rule_name"));
  }
  @Override
  public void inPutTransHM()
  {
	this.getFormHM().put("rule_id",this.getRule_id());
	this.getFormHM().put("tran_flag",this.getTran_flag());
	this.getFormHM().put("rule_name",this.getRule_name());
	this.getFormHM().put("kq_rule_vo",this.getKq_rule_vo());
	this.getFormHM().put("status_value",this.getStatus_value());
  }

public RecordVo getKq_rule_vo() {
	return kq_rule_vo;
}
public void setKq_rule_vo(RecordVo kq_rule_vo) {
	this.kq_rule_vo = kq_rule_vo;
}
public String getRule_id() {
	return rule_id;
}
public void setRule_id(String rule_id) {
	this.rule_id = rule_id;
}
public String getTran_flag() {
	return tran_flag;
}
public void setTran_flag(String tran_flag) {
	this.tran_flag = tran_flag;
}
public String getStatus_value() {
	return status_value;
}
public void setStatus_value(String status_value) {
	this.status_value = status_value;
}
public String getReturnvalue() {
	return returnvalue;
}
public void setReturnvalue(String returnvalue) {
	this.returnvalue = returnvalue;
}
}
