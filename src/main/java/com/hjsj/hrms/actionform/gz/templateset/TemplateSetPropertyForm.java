package com.hjsj.hrms.actionform.gz.templateset;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class TemplateSetPropertyForm extends FrameForm {
	private String salaryid="";
	private ArrayList dbList=new ArrayList();  //适应范围（人员库列表）   
	private String[]  dbValue=null;
	
	private String    personScope="1";         //人员范围（1：简单条件  2：复杂条件）
	private String    condStr="";     
	private String    cexpr="";
	private String    flow_ctrl="0";          //0不用审批 =1需要审批
	
	private String    reject_mode="1";        //驳回方式  1:逐级驳回  2：驳回到发起人
	private String    verify_ctrl="0";        //是否按审核条件控制
	private String    verify_ctrl_ff="";     //控制薪资发放
	private String    verify_ctrl_sp="";      //控制薪资审批
	private String    verify_item="";         //审核项目
	
	private ArrayList piecerateList=new ArrayList();
	private String    piecerate="";
	
	private ArrayList moneyTypeList=new ArrayList();  //货币类型
	private String    moneyType="";
	
	private ArrayList varyModelList=new ArrayList(); //变动模板列表
	private String[]  varyModelValue=null;
	
	private ArrayList calculateTaxTimeList=new ArrayList();  //计税时间
	private String    calculateTaxTime="";
	
	private ArrayList appealTaxTimeList=new ArrayList();    //报税时间
	private String    appealTaxTime="";
	
	private ArrayList sendSalaryItemList=new ArrayList();   //发薪标识
	private String    sendSalaryItem="";
	
	private ArrayList taxTypeList=new ArrayList();          //计税方式
	private String    taxType="";
	
	private ArrayList  ratepayingDeclareList=new ArrayList();  //纳税项目说明
	private String     ratepayingDecalre="";
	private String amount_ctrl="";//是否进行总额控制 =0不控制 =2需要控制 
	
	private String amount_ctrl_ff="";  //控制薪资发放
	private String amount_ctrl_sp="";  //控制薪资审批
	 /**工资总额控制方式提示和强制*/
    private String ctrlType;//=0仅提示，=1强制控制，默认强制控制
	
    private String priv_mode="";//人员范围是否进行权限过滤=0不加过滤，=1加过滤
    
    private String isShare="0"; //是否共享类别
    private String manager="";  //工资管理员，对共享类别有效
    
	private String    gz_module="";
	private String mailTemplateId;//邮件模板ID号
	private ArrayList mailTemplateList =new ArrayList();//邮件模板列表
	private String mailNotice;//邮件通知
	private String msNotice;//短信通知
	
	private String a01z0Flag="0";  //#是否显示停发标识 0: 不启用 1：启用
	
	private String bonusItemFld;//奖金项目指标
	private ArrayList bonusItemFldList =new ArrayList();
	/**汇总归属单位*/
    private String orgid;
    private ArrayList orgList = new ArrayList();
    /**汇总归属部门*/
    private String deptid;
    private ArrayList deptList = new ArrayList();
    /** 部门汇总层级 */
    private ArrayList contrlLevelList=new ArrayList();
    private String contrlLevelId="";
    private String sum_type="0";//0|1(仅汇总单位|单位或部门都进行汇总)
    
  
    /**分页管理器*/
    private PaginationForm fieldsetlistform=new PaginationForm();
    private String    isUpdateSet="";
    private ArrayList typelist=new ArrayList();
    private String    type="";
    
    private String subNoShowUpdateFashion="0"; // 发放确认时不显示数据操作方式设置
    private String subNoPriv="0";   //数据提交入库不判断子集及指标权限
    private String allowEditSubdata="0";   //允许修改发放结束已提交数据 默认不允许
    private String item_str="";
    private String type_str="";
    private String set_str="";
    private String typestr="";
    /**隶属部门指标列表*/
    private String lsDept;
    private ArrayList lsDeptList = new ArrayList();
    /**时候显示隶属部门指标*/
    private String islsDept;
   /** 非写权限指标参与计算*/
    private String field_priv;
    
    /**汇总审批金额指标*/
    private String collect_je_field="";
    private ArrayList number_field_list=new ArrayList();
    /**读权限指标允许重新引入*/
   private String read_field;
   
   
   /** 审批关系列表 */
   private ArrayList spRelationList=new ArrayList();
   private String sp_relation_id="";
   
   /** 默认审批项目 */
   private ArrayList spDefaultFilterList=new ArrayList();
   private String sp_default_filter_id="";
   
   /** 提成薪资 */
   private String royalty_valid="0"; //启用 （0|1）
   private String royalty_setid="";   
   private String royalty_date=""; 
   private String royalty_period="";  
   private String royalty_relation_fields="";   
   private ArrayList setList=new ArrayList();
   private ArrayList periodList=new ArrayList();
   private ArrayList dateList=new ArrayList();
   private ArrayList fieldList=new ArrayList();
   private String strExpression="";
   
   /**计件薪资 赵旭光增加*/
   private String priecerate_valid="0"; //启用 （0|1）
   private String priecerate_expression_str="";//数据范围
   private String priecerate_zhouq1="";//周期
   private String priecerate_str="";//月份
   private String priecerate_zhibiao="";//引入指标
   
	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public ArrayList getOrgList() {
		return orgList;
	}

	public void setOrgList(ArrayList orgList) {
		this.orgList = orgList;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public ArrayList getDeptList() {
		return deptList;
	}

	public void setDeptList(ArrayList deptList) {
		this.deptList = deptList;
	}

	public ArrayList getContrlLevelList() {
		return contrlLevelList;
	}

	public void setContrlLevelList(ArrayList contrlLevelList) {
		this.contrlLevelList = contrlLevelList;
	}

	public String getContrlLevelId() {
		return contrlLevelId;
	}

	public void setContrlLevelId(String contrlLevelId) {
		this.contrlLevelId = contrlLevelId;
	}



	public String getPriecerate_valid() {
		return priecerate_valid;
	}

	public void setPriecerate_valid(String priecerate_valid) {
		this.priecerate_valid = priecerate_valid;
	}

	public String getPriecerate_expression_str() {
		return priecerate_expression_str;
	}

	public void setPriecerate_expression_str(String priecerate_expression_str) {
		this.priecerate_expression_str = priecerate_expression_str;
	}

	public String getPriecerate_zhouq1() {
		return priecerate_zhouq1;
	}

	public void setPriecerate_zhouq1(String priecerate_zhouq1) {
		this.priecerate_zhouq1 = priecerate_zhouq1;
	}

	public String getPriecerate_str() {
		return priecerate_str;
	}

	public void setPriecerate_str(String priecerate_str) {
		this.priecerate_str = priecerate_str;
	}

	public String getPriecerate_zhibiao() {
		return priecerate_zhibiao;
	}

	public void setPriecerate_zhibiao(String priecerate_zhibiao) {
		this.priecerate_zhibiao = priecerate_zhibiao;
	}

	@Override
    public void outPutFormHM() {
		this.setStrExpression((String)this.getFormHM().get("strExpression"));
		this.setPeriodList((ArrayList)this.getFormHM().get("periodList"));
		this.setDateList((ArrayList)this.getFormHM().get("dateList"));
		this.setSetList((ArrayList)this.getFormHM().get("setList"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setRoyalty_valid((String)this.getFormHM().get("royalty_valid"));
		this.setRoyalty_setid((String)this.getFormHM().get("royalty_setid"));
		this.setRoyalty_date((String)this.getFormHM().get("royalty_date"));
		this.setRoyalty_period((String)this.getFormHM().get("royalty_period"));
		this.setRoyalty_relation_fields((String)this.getFormHM().get("royalty_relation_fields"));
		
		this.setPriecerate_expression_str((String) this.getFormHM().get("priecerate_expression_str"));
		this.setPriecerate_zhouq1((String) this.getFormHM().get("priecerate_zhouq1"));
		this.setPriecerate_zhibiao((String) this.getFormHM().get("priecerate_zhibiao"));
		this.setPriecerate_str((String) this.getFormHM().get("priecerate_str"));
		this.setPriecerate_valid((String) this.getFormHM().get("priecerate_valid"));
		
		this.setSpRelationList((ArrayList)this.getFormHM().get("spRelationList"));
		this.setSp_relation_id((String)this.getFormHM().get("sp_relation_id"));
		this.setSpDefaultFilterList((ArrayList)this.getFormHM().get("spDefaultFilterList"));
		this.setSp_default_filter_id((String)this.getFormHM().get("sp_default_filter_id"));
		
		this.setCtrlType((String)this.getFormHM().get("ctrlType"));
		this.setSubNoPriv((String)this.getFormHM().get("subNoPriv"));
		this.setAllowEditSubdata((String)this.getFormHM().get("allowEditSubdata"));
		this.setNumber_field_list((ArrayList)this.getFormHM().get("number_field_list"));
		this.setCollect_je_field((String)this.getFormHM().get("collect_je_field"));
		this.setField_priv((String)this.getFormHM().get("field_priv"));
		this.setLsDept((String)this.getFormHM().get("lsDept"));
		this.setLsDeptList((ArrayList)this.getFormHM().get("lsDeptList"));
		this.setIslsDept((String)this.getFormHM().get("islsDept"));
		
		this.setVerify_item((String)this.getFormHM().get("verify_item"));
		this.setVerify_ctrl_ff((String)this.getFormHM().get("verify_ctrl_ff"));
		this.setVerify_ctrl_sp((String)this.getFormHM().get("verify_ctrl_sp"));
		this.setAmount_ctrl_ff((String)this.getFormHM().get("amount_ctrl_ff"));
		this.setAmount_ctrl_sp((String)this.getFormHM().get("amount_ctrl_sp"));
		
		this.setSubNoShowUpdateFashion((String)this.getFormHM().get("subNoShowUpdateFashion"));
		this.getFieldsetlistform().setList((ArrayList)this.getFormHM().get("fieldsetlist"));
		this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
		this.setIsUpdateSet((String)this.getFormHM().get("isUpdateSet"));
		this.setType((String)this.getFormHM().get("type"));
		
		
		this.setVerify_ctrl((String)this.getFormHM().get("verify_ctrl"));
		this.setReject_mode((String)this.getFormHM().get("reject_mode"));
		this.setMsNotice((String)this.getFormHM().get("msNotice"));
		this.setMailNotice((String)this.getFormHM().get("mailNotice"));
		this.setMailTemplateList((ArrayList)this.getFormHM().get("mailTemplateList"));
		this.setMailTemplateId((String)this.getFormHM().get("mailTemplateId"));
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setAmount_ctrl((String)this.getFormHM().get("amount_ctrl"));
		this.setDbList((ArrayList)this.getFormHM().get("dbList"));
		this.setPersonScope((String)this.getFormHM().get("personScope"));
		this.setPriv_mode((String)this.getFormHM().get("priv_mode"));
		this.setCondStr((String)this.getFormHM().get("condStr"));
		this.setCexpr((String)this.getFormHM().get("cexpr"));
		this.setFlow_ctrl((String)this.getFormHM().get("flow_ctrl"));
		this.setPiecerate((String)this.getFormHM().get("piecerate"));
		this.setPiecerateList((ArrayList)this.getFormHM().get("piecerateList"));
		
		this.setMoneyTypeList((ArrayList)this.getFormHM().get("moneyTypeList"));
		this.setMoneyType((String)this.getFormHM().get("moneyType"));
		this.setVaryModelList((ArrayList)this.getFormHM().get("varyModelList"));
		this.setCalculateTaxTimeList((ArrayList)this.getFormHM().get("calculateTaxTimeList"));
		this.setCalculateTaxTime((String)this.getFormHM().get("calculateTaxTime"));
		this.setAppealTaxTimeList((ArrayList)this.getFormHM().get("appealTaxTimeList"));
		this.setAppealTaxTime((String)this.getFormHM().get("appealTaxTime"));
		this.setSendSalaryItemList((ArrayList)this.getFormHM().get("sendSalaryItemList"));
		this.setSendSalaryItem((String)this.getFormHM().get("sendSalaryItem"));
		this.setTaxTypeList((ArrayList)this.getFormHM().get("taxTypeList"));
		this.setTaxType((String)this.getFormHM().get("taxType"));
		this.setRatepayingDeclareList((ArrayList)this.getFormHM().get("ratepayingDeclareList"));
		this.setRatepayingDecalre((String)this.getFormHM().get("ratepayingDecalre"));
		this.setManager((String)this.getFormHM().get("manager"));
		this.setIsShare((String)this.getFormHM().get("isShare"));
		
		this.setA01z0Flag((String)this.getFormHM().get("a01z0Flag"));
		this.setBonusItemFld((String)this.getFormHM().get("bonusItemFld"));
		this.setBonusItemFldList((ArrayList)this.getFormHM().get("bonusItemFldList"));
		this.setOrgid((String)this.getFormHM().get("orgid"));
		this.setOrgList((ArrayList)this.getFormHM().get("orgList"));
		this.setDeptid((String)this.getFormHM().get("deptid"));
		this.setDeptList((ArrayList)this.getFormHM().get("deptList"));
		this.setContrlLevelId((String)this.getFormHM().get("contrlLevelId"));
		this.setContrlLevelList((ArrayList)this.getFormHM().get("contrlLevelList"));
		this.setSum_type((String)this.getFormHM().get("sum_type"));
		this.setRead_field((String)this.getFormHM().get("read_field"));
	}

	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("strExpression",this.getStrExpression());
		this.getFormHM().put("royalty_valid",this.getRoyalty_valid());
		this.getFormHM().put("royalty_setid",this.getRoyalty_setid());
		this.getFormHM().put("royalty_date",this.getRoyalty_date());
		this.getFormHM().put("royalty_period",this.getRoyalty_period());
		this.getFormHM().put("royalty_relation_fields",this.getRoyalty_relation_fields());
		
		this.getFormHM().put("priecerate_expression_str", this.getPriecerate_expression_str());
		this.getFormHM().put("priecerate_zhouq1", this.getPriecerate_zhouq1());
		this.getFormHM().put("priecerate_zhibiao", this.getPriecerate_zhibiao());
		this.getFormHM().put("priecerate_str", this.getPriecerate_str());
		this.getFormHM().put("priecerate_valid", this.getPriecerate_valid());
		 
		this.getFormHM().put("sp_relation_id",this.getSp_relation_id());
		this.getFormHM().put("sp_default_filter_id",this.getSp_default_filter_id());
		this.getFormHM().put("ctrlType", this.getCtrlType());
		this.getFormHM().put("subNoPriv",this.getSubNoPriv());
		this.getFormHM().put("allowEditSubdata",this.getAllowEditSubdata());		
		this.getFormHM().put("collect_je_field",this.getCollect_je_field());
		this.getFormHM().put("field_priv", this.getField_priv());
		this.getFormHM().put("islsDept", this.getIslsDept());
		this.getFormHM().put("lsDept", this.getLsDept());
		this.getFormHM().put("lsDeptList", this.getLsDeptList());
		
		this.getFormHM().put("verify_item",this.getVerify_item());
		this.getFormHM().put("verify_ctrl_ff",this.getVerify_ctrl_ff());
		this.getFormHM().put("verify_ctrl_sp",this.getVerify_ctrl_sp());
		this.getFormHM().put("amount_ctrl_ff",this.getAmount_ctrl_ff());
		this.getFormHM().put("amount_ctrl_sp",this.getAmount_ctrl_sp());
		
		this.getFormHM().put("subNoShowUpdateFashion",this.getSubNoShowUpdateFashion());
		this.getFormHM().put("item_str", this.getItem_str());
		this.getFormHM().put("type_str", this.getType_str());
		this.getFormHM().put("set_str", this.getSet_str());
		this.getFormHM().put("typestr", this.getTypestr());
		
		this.getFormHM().put("verify_ctrl",this.getVerify_ctrl());
		this.getFormHM().put("reject_mode", this.getReject_mode());
		this.getFormHM().put("msNotice", this.getMsNotice());
		this.getFormHM().put("mailNotice", this.getMailNotice());
		this.getFormHM().put("mailTemplateId", this.getMailTemplateId());
		this.getFormHM().put("isShare",this.isShare);
		this.getFormHM().put("manager",this.getManager());
		this.getFormHM().put("flow_ctrl",this.getFlow_ctrl());
		this.getFormHM().put("piecerate",this.getPiecerate());
		this.getFormHM().put("priv_mode", this.getPriv_mode());
		this.getFormHM().put("condStr",this.getCondStr());
		this.getFormHM().put("cexpr",this.getCexpr());
		this.getFormHM().put("amount_ctrl",this.getAmount_ctrl());
		this.getFormHM().put("dbValue",this.getDbValue());
		this.getFormHM().put("personScope",this.getPersonScope());
		this.getFormHM().put("moneyType",this.getMoneyType());
		this.getFormHM().put("varyModelValue",this.getVaryModelValue());
		this.getFormHM().put("calculateTaxTime",this.getCalculateTaxTime());
		this.getFormHM().put("appealTaxTime",this.getAppealTaxTime());
		this.getFormHM().put("sendSalaryItem",this.getSendSalaryItem());
		this.getFormHM().put("taxType",this.getTaxType());
		this.getFormHM().put("ratepayingDecalre",this.getRatepayingDecalre());
		this.getFormHM().put("a01z0Flag",this.getA01z0Flag());
		this.getFormHM().put("bonusItemFld",this.getBonusItemFld());
		this.getFormHM().put("bonusItemFldList",this.getBonusItemFldList());
		this.getFormHM().put("orgid", this.getOrgid());
		this.getFormHM().put("deptid", this.getDeptid());
		this.getFormHM().put("contrlLevelId", this.getContrlLevelId());
		this.getFormHM().put("sum_type", this.getSum_type());
		this.getFormHM().put("read_field", this.getRead_field());
	}

	public String getAppealTaxTime() {
		return appealTaxTime;
	}

	public void setAppealTaxTime(String appealTaxTime) {
		this.appealTaxTime = appealTaxTime;
	}

	public ArrayList getAppealTaxTimeList() {
		return appealTaxTimeList;
	}

	public void setAppealTaxTimeList(ArrayList appealTaxTimeList) {
		this.appealTaxTimeList = appealTaxTimeList;
	}

	public String getCalculateTaxTime() {
		return calculateTaxTime;
	}

	public void setCalculateTaxTime(String calculateTaxTime) {
		this.calculateTaxTime = calculateTaxTime;
	}

	public ArrayList getCalculateTaxTimeList() {
		return calculateTaxTimeList;
	}

	public void setCalculateTaxTimeList(ArrayList calculateTaxTimeList) {
		this.calculateTaxTimeList = calculateTaxTimeList;
	}

	public ArrayList getDbList() {
		return dbList;
	}

	public void setDbList(ArrayList dbList) {
		this.dbList = dbList;
	}

	public String[] getDbValue() {
		return dbValue;
	}

	public void setDbValue(String[] dbValue) {
		this.dbValue = dbValue;
	}

	public String getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	public ArrayList getMoneyTypeList() {
		return moneyTypeList;
	}

	public void setMoneyTypeList(ArrayList moneyTypeList) {
		this.moneyTypeList = moneyTypeList;
	}

	public String getPersonScope() {
		return personScope;
	}

	public void setPersonScope(String personScope) {
		this.personScope = personScope;
	}

	public String getRatepayingDecalre() {
		return ratepayingDecalre;
	}

	public void setRatepayingDecalre(String ratepayingDecalre) {
		this.ratepayingDecalre = ratepayingDecalre;
	}

	public ArrayList getRatepayingDeclareList() {
		return ratepayingDeclareList;
	}

	public void setRatepayingDeclareList(ArrayList ratepayingDeclareList) {
		this.ratepayingDeclareList = ratepayingDeclareList;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getSendSalaryItem() {
		return sendSalaryItem;
	}

	public void setSendSalaryItem(String sendSalaryItem) {
		this.sendSalaryItem = sendSalaryItem;
	}

	public ArrayList getSendSalaryItemList() {
		return sendSalaryItemList;
	}

	public void setSendSalaryItemList(ArrayList sendSalaryItemList) {
		this.sendSalaryItemList = sendSalaryItemList;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public ArrayList getTaxTypeList() {
		return taxTypeList;
	}

	public void setTaxTypeList(ArrayList taxTypeList) {
		this.taxTypeList = taxTypeList;
	}

	public ArrayList getVaryModelList() {
		return varyModelList;
	}

	public void setVaryModelList(ArrayList varyModelList) {
		this.varyModelList = varyModelList;
	}

	public String[] getVaryModelValue() {
		return varyModelValue;
	}

	public void setVaryModelValue(String[] varyModelValue) {
		this.varyModelValue = varyModelValue;
	}

	public String getCondStr() {
		return condStr;
	}

	public void setCondStr(String condStr) {
		this.condStr = condStr;
	}

	public String getCexpr() {
		return cexpr;
	}

	public void setCexpr(String cexpr) {
		this.cexpr = cexpr;
	}

	public String getFlow_ctrl() {
		return flow_ctrl;
	}

	public void setFlow_ctrl(String flow_ctrl) {
		this.flow_ctrl = flow_ctrl;
	}

	public String getPiecerate() {
		return piecerate;
	}

	public void setPiecerate(String piecerate) {
		this.piecerate = piecerate;
	}

	public ArrayList getPiecerateList() {
		return piecerateList;
	}

	public void setPiecerateList(ArrayList piecerateList) {
		this.piecerateList = piecerateList;
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getAmount_ctrl() {
		return amount_ctrl;
	}

	public void setAmount_ctrl(String amount_ctrl) {
		this.amount_ctrl = amount_ctrl;
	}

	public String getPriv_mode() {
		return priv_mode;
	}

	public void setPriv_mode(String priv_mode) {
		this.priv_mode = priv_mode;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getIsShare() {
		return isShare;
	}

	public void setIsShare(String isShare) {
		this.isShare = isShare;
	}

	public String getMailTemplateId() {
		return mailTemplateId;
	}

	public void setMailTemplateId(String mailTemplateId) {
		this.mailTemplateId = mailTemplateId;
	}

	public ArrayList getMailTemplateList() {
		return mailTemplateList;
	}

	public void setMailTemplateList(ArrayList mailTemplateList) {
		this.mailTemplateList = mailTemplateList;
	}

	public String getMailNotice() {
		return mailNotice;
	}

	public void setMailNotice(String mailNotice) {
		this.mailNotice = mailNotice;
	}

	public String getMsNotice() {
		return msNotice;
	}

	public void setMsNotice(String msNotice) {
		this.msNotice = msNotice;
	}

	public String getA01z0Flag() {
		return a01z0Flag;
	}

	public void setA01z0Flag(String flag) {
		a01z0Flag = flag;
	}

	public String getBonusItemFld()
	{
	
	    return bonusItemFld;
	}

	public void setBonusItemFld(String bonusItemFld)
	{
	
	    this.bonusItemFld = bonusItemFld;
	}

	public ArrayList getBonusItemFldList()
	{
	
	    return bonusItemFldList;
	}

	public void setBonusItemFldList(ArrayList bonusItemFldList)
	{
	
	    this.bonusItemFldList = bonusItemFldList;
	}

	public String getSum_type() {
		return sum_type;
	}

	public void setSum_type(String sum_type) {
		this.sum_type = sum_type;
	}

	public String getReject_mode() {
		return reject_mode;
	}

	public void setReject_mode(String reject_mode) {
		this.reject_mode = reject_mode;
	}

	public String getVerify_ctrl() {
		return verify_ctrl;
	}

	public void setVerify_ctrl(String verify_ctrl) {
		this.verify_ctrl = verify_ctrl;
	}


	public String getIsUpdateSet() {
		return isUpdateSet;
	}

	public void setIsUpdateSet(String isUpdateSet) {
		this.isUpdateSet = isUpdateSet;
	}

	public ArrayList getTypelist() {
		return typelist;
	}

	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PaginationForm getFieldsetlistform() {
		return fieldsetlistform;
	}

	public void setFieldsetlistform(PaginationForm fieldsetlistform) {
		this.fieldsetlistform = fieldsetlistform;
	}

	public String getItem_str() {
		return item_str;
	}

	public void setItem_str(String item_str) {
		this.item_str = item_str;
	}

	public String getType_str() {
		return type_str;
	}

	public void setType_str(String type_str) {
		this.type_str = type_str;
	}

	public String getSet_str() {
		return set_str;
	}

	public void setSet_str(String set_str) {
		this.set_str = set_str;
	}

	public String getTypestr() {
		return typestr;
	}

	public void setTypestr(String typestr) {
		this.typestr = typestr;
	}

	public String getSubNoShowUpdateFashion() {
		return subNoShowUpdateFashion;
	}

	public void setSubNoShowUpdateFashion(String subNoShowUpdateFashion) {
		this.subNoShowUpdateFashion = subNoShowUpdateFashion;
	}

	public String getVerify_ctrl_ff() {
		return verify_ctrl_ff;
	}

	public void setVerify_ctrl_ff(String verify_ctrl_ff) {
		this.verify_ctrl_ff = verify_ctrl_ff;
	}

	public String getVerify_ctrl_sp() {
		return verify_ctrl_sp;
	}

	public void setVerify_ctrl_sp(String verify_ctrl_sp) {
		this.verify_ctrl_sp = verify_ctrl_sp;
	}

	public String getAmount_ctrl_ff() {
		return amount_ctrl_ff;
	}

	public void setAmount_ctrl_ff(String amount_ctrl_ff) {
		this.amount_ctrl_ff = amount_ctrl_ff;
	}

	public String getAmount_ctrl_sp() {
		return amount_ctrl_sp;
	}

	public void setAmount_ctrl_sp(String amount_ctrl_sp) {
		this.amount_ctrl_sp = amount_ctrl_sp;
	}

	public String getLsDept() {
		return lsDept;
	}

	public void setLsDept(String lsDept) {
		this.lsDept = lsDept;
	}

	public ArrayList getLsDeptList() {
		return lsDeptList;
	}

	public void setLsDeptList(ArrayList lsDeptList) {
		this.lsDeptList = lsDeptList;
	}

	public String getIslsDept() {
		return islsDept;
	}

	public void setIslsDept(String islsDept) {
		this.islsDept = islsDept;
	}

	public String getField_priv() {
		return field_priv;
	}

	public void setField_priv(String field_priv) {
		this.field_priv = field_priv;
	}

	public String getCollect_je_field() {
		return collect_je_field;
	}

	public void setCollect_je_field(String collect_je_field) {
		this.collect_je_field = collect_je_field;
	}

	public ArrayList getNumber_field_list() {
		return number_field_list;
	}

	public void setNumber_field_list(ArrayList number_field_list) {
		this.number_field_list = number_field_list;
	}

	public String getSubNoPriv() {
		return subNoPriv;
	}

	public void setSubNoPriv(String subNoPriv) {
		this.subNoPriv = subNoPriv;
	}

	public String getCtrlType() {
		return ctrlType;
	}

	public void setCtrlType(String ctrlType) {
		this.ctrlType = ctrlType;
	}

	public String getRead_field() {
		return read_field;
	}

	public void setRead_field(String read_field) {
		this.read_field = read_field;
	}

	public ArrayList getSpRelationList() {
		return spRelationList;
	}

	public void setSpRelationList(ArrayList spRelationList) {
		this.spRelationList = spRelationList;
	}

	public String getSp_relation_id() {
		return sp_relation_id;
	}

	public void setSp_relation_id(String sp_relation_id) {
		this.sp_relation_id = sp_relation_id;
	}

	public String getRoyalty_valid() {
		return royalty_valid;
	}

	public void setRoyalty_valid(String royalty_valid) {
		this.royalty_valid = royalty_valid;
	}

	public String getRoyalty_setid() {
		return royalty_setid;
	}

	public void setRoyalty_setid(String royalty_setid) {
		this.royalty_setid = royalty_setid;
	}

	public String getRoyalty_date() {
		return royalty_date;
	}

	public void setRoyalty_date(String royalty_date) {
		this.royalty_date = royalty_date;
	}

	public String getRoyalty_period() {
		return royalty_period;
	}

	public void setRoyalty_period(String royalty_period) {
		this.royalty_period = royalty_period;
	}

	public String getRoyalty_relation_fields() {
		return royalty_relation_fields;
	}

	public void setRoyalty_relation_fields(String royalty_relation_fields) {
		this.royalty_relation_fields = royalty_relation_fields;
	}

	public ArrayList getSetList() {
		return setList;
	}

	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}

	public ArrayList getDateList() {
		return dateList;
	}

	public void setDateList(ArrayList dateList) {
		this.dateList = dateList;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public ArrayList getPeriodList() {
		return periodList;
	}

	public void setPeriodList(ArrayList periodList) {
		this.periodList = periodList;
	}

	public String getStrExpression() {
		return strExpression;
	}

	public void setStrExpression(String strExpression) {
		this.strExpression = strExpression;
	}

	public String getVerify_item() {
		return verify_item;
	}

	public void setVerify_item(String verify_item) {
		this.verify_item = verify_item;
	}

    public String getAllowEditSubdata() {
        return allowEditSubdata;
    }

    public void setAllowEditSubdata(String allowEditSubdata) {
        this.allowEditSubdata = allowEditSubdata;
    }

    public ArrayList getSpDefaultFilterList() {
        return spDefaultFilterList;
    }

    public void setSpDefaultFilterList(ArrayList spDefaultFilterList) {
        this.spDefaultFilterList = spDefaultFilterList;
    }

    public String getSp_default_filter_id() {
        return sp_default_filter_id;
    }

    public void setSp_default_filter_id(String sp_default_filter_id) {
        this.sp_default_filter_id = sp_default_filter_id;
    }
	
}
