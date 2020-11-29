/**
 * 
 */
package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 */
public class CollectForm extends FrameForm {

	private String gz_module;
	private String salaryid;
	private String sum_type;//0|1(仅汇总单位|单位或部门都进行汇总)
	private String layer;//针对部门汇总层数
	private String orgid; //归属单位指标
	private String deptid; //归属部门指标
	private String bosdate;//业务日期
	private ArrayList datelist = new ArrayList();
	private String count;//业务次数
	private ArrayList countlist = new ArrayList();
	private String sql;//dataset 所需的sql
	private ArrayList fieldlist = new ArrayList();//dataset 所需的property
	private String sum_fields_str;//汇总指标
	private String approveObject;//报批给、驳回、批准的人
	private String rejectCause;//驳回、批准原因
	private String selectGzRecords;//本次报批、驳回、批准的记录
	private String tempTableName;//表名：mssql:##gz_collect_当前用户名 oracle:gz_collect_当前用户名
	private String verify_ctrl="0";  // //是否按审核条件控制
	 private String isTotalControl="0"; //是否进行总额控制
	 
	private String sp_actor_str="";     //审批领导信息
	private String spActorName="";      //审批人名称
	private String relation_id=""; 
	private String isSendMessage="0";  //当前薪资类别是否发送消息 
	
	private String sendMen="";  //工资批准发送消息对象
	/**
	 * 
	 */
	public CollectForm() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */

	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("sendMen",this.getSendMen());
		
		this.getFormHM().put("salaryid", this.getSalaryid());
		this.getFormHM().put("bosdate", this.getBosdate());
		this.getFormHM().put("count", this.getCount());
		this.getFormHM().put("sum_fields_str", this.getSum_fields_str());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("gz_module", this.getGz_module());
		this.getFormHM().put("approveObject", this.getApproveObject());
		this.getFormHM().put("rejectCause", this.getRejectCause());
		this.getFormHM().put("selectGzRecords", this.getSelectGzRecords());
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		this.setRelation_id((String)this.getFormHM().get("relation_id"));
		this.setSp_actor_str((String)this.getFormHM().get("sp_actor_str"));
		this.setSpActorName((String)this.getFormHM().get("spActorName"));
		
		this.setIsSendMessage((String)this.getFormHM().get("isSendMessage"));
		
		this.setIsTotalControl((String)this.getFormHM().get("isTotalControl"));
		this.setVerify_ctrl((String)this.getFormHM().get("verify_ctrl"));
		this.setSum_type((String)this.getFormHM().get("sum_type"));
		this.setLayer((String)this.getFormHM().get("layer"));
		this.setBosdate((String)this.getFormHM().get("bosdate"));
		this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setCountlist((ArrayList)this.getFormHM().get("countlist"));
		this.setOrgid((String)this.getFormHM().get("orgid"));
		this.setDeptid((String)this.getFormHM().get("deptid"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSum_fields_str((String)this.getFormHM().get("sum_fields_str"));
		this.setTempTableName((String)this.getFormHM().get("tempTableName"));
	}

	public String getSum_type() {
		return sum_type;
	}

	public void setSum_type(String sum_type) {
		this.sum_type = sum_type;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getBosdate() {
		return bosdate;
	}

	public void setBosdate(String bosdate) {
		this.bosdate = bosdate;
	}

	public ArrayList getDatelist() {
		return datelist;
	}

	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public ArrayList getCountlist() {
		return countlist;
	}

	public void setCountlist(ArrayList countlist) {
		this.countlist = countlist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getSum_fields_str() {
		return sum_fields_str;
	}

	public void setSum_fields_str(String sum_fields_str) {
		this.sum_fields_str = sum_fields_str;
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getApproveObject() {
		return approveObject;
	}

	public void setApproveObject(String approveObject) {
		this.approveObject = approveObject;
	}

	public String getRejectCause() {
		return rejectCause;
	}

	public void setRejectCause(String rejectCause) {
		this.rejectCause = rejectCause;
	}

	public String getSelectGzRecords() {
		return selectGzRecords;
	}

	public void setSelectGzRecords(String selectGzRecords) {
		this.selectGzRecords = selectGzRecords;
	}

	public String getTempTableName() {
		return tempTableName;
	}

	public void setTempTableName(String tempTableName) {
		this.tempTableName = tempTableName;
	}

	public String getVerify_ctrl() {
		return verify_ctrl;
	}

	public void setVerify_ctrl(String verify_ctrl) {
		this.verify_ctrl = verify_ctrl;
	}

	public String getIsTotalControl() {
		return isTotalControl;
	}

	public void setIsTotalControl(String isTotalControl) {
		this.isTotalControl = isTotalControl;
	}

	public String getSp_actor_str() {
		return sp_actor_str;
	}

	public void setSp_actor_str(String sp_actor_str) {
		this.sp_actor_str = sp_actor_str;
	}

	public String getSpActorName() {
		return spActorName;
	}

	public void setSpActorName(String spActorName) {
		this.spActorName = spActorName;
	}

	public String getRelation_id() {
		return relation_id;
	}

	public void setRelation_id(String relation_id) {
		this.relation_id = relation_id;
	}

	public String getIsSendMessage() {
		return isSendMessage;
	}

	public void setIsSendMessage(String isSendMessage) {
		this.isSendMessage = isSendMessage;
	}

	public String getSendMen() {
		return sendMen;
	}

	public void setSendMen(String sendMen) {
		this.sendMen = sendMen;
	}

}
