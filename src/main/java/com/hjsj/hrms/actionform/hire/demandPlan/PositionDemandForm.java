package com.hjsj.hrms.actionform.hire.demandPlan;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:PositionDemandForm</p>
 * <p>Description:用工需求接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-10-12</p>
 * @author dengcan
 * @version 1.0
 * 
 */
public class PositionDemandForm extends FrameForm {
    private String           username               = "";

    private String           operateType            = "user";                // user:业务平台 employ：自助平台
    private String           model                  = "";                    //1:用工需求   2：需求审核
    private String           linkDesc               = "";

    /**字段列表*/
    private ArrayList        fieldlist;
    /**数据过滤SQL语句*/
    private String           sql;
    private String           whl_sql;

    private String           fieldSize              = "0";
    private String           extendSql;
    private String           orderSql;
    /**表名*/
    private String           tablename;
    /**空缺职位列表*/
    private ArrayList        sparePositionList;
    private HashMap          sparePositionMap;
    private String[]         sparePositionIDs       = null;
    private String           planNumberFieldName    = "";
    private String           actualNumberFieldName  = "";

    private ArrayList        positionDemandDescList = new ArrayList();       //用工需求 指标列表
    private ArrayList        posConditionList       = new ArrayList();       //
    private ArrayList        mailTemplateList       = new ArrayList();       //邮件模版列表
    private ArrayList        testTemplateList       = new ArrayList();       //测评表下拉列表
    private String           mailTemplateID         = "";                    //邮件模版号
    private String           isRevert               = "0";                   //是否回复 0：不回复  1：回复
    private String           z0301                  = "";

    private String           vague                  = "";                    //是否按模糊查询
    private String           upValue                = "";                    //子集代码型 以上
    private ArrayList        fieldList              = new ArrayList();
    private FormFile         file;                                           //上传附件
    private String           isPosBooklet           = "0";                   //是否有职位说明书
    private String           e01a1                  = "";                    //职位id
    private String           sp_flag;                                         //审批状态标志
    private ArrayList        splist                 = new ArrayList();        //审批状态列表；
    private String           sp;
    private String           rejectCause            = "";                    //驳回原因
    /**简历筛选模板类型*/
    private String           templateType           = "0";
    /**复杂模板列表*/
    private ArrayList        complexTemplateList    = new ArrayList();
    /**模板id*/
    private String           templateid             = "";
    /**判断从招聘需求进入的职位修改还是从招聘职位进入*/
    private String           entertype;
    private String           privCode;
    private String           positionState;
    /**需求审批状态*/
    private String           positionStateDesc;
    /**机构数中机构代码*/
    private String           codeItemId;
    private String           codeSetId;
    private String           testid;
    private String           valid;
    /**单位部门预算表是否定义=0 没定义=1已定义*/
    private String           isOrgWillTableIdDefine;
    private String           orgWillTableId;
    private String           z0321;
    private CardTagParamView cardparam              = new CardTagParamView();
    /**是否多级选人报批*/
    private String           moreLevelSP;
    /**审批过程记录*/
    private String           reasons;
    private String           a0100;
    private String           content;
    private ArrayList        rolelist               = new ArrayList();
    private String           roleid;
    private String           orgUN;
    private String           orgUM;
    private String           showUMCard;
    private String           isCtrl;
    private String           assignType;                                      //派单类型
    private ArrayList        assignTypeList         = new ArrayList();
    private String           isReport;
    private String           isReject;
    private ArrayList        groupFieldList         = new ArrayList();
    private String           groupFieldId;
    private String           schoolPosition         = "";                    //校园招聘待岗名称 
    private String           hireMajor;                                       //招聘专业指标
    private ArrayList        zpNeedsFieldsList      = new ArrayList();
    private String           zpNeedsField           = "";
    private String           lineFields             = "";
    private String           lieFields              = "";
    private String           resultFields           = "";
    private String           isSendMessage          = "";
    private String           reportHtml             = "";
    private ArrayList        chanelList             = new ArrayList();
    private String           zpchanel               = "";
    private String           noexcel                = "";                     //dml 2011-6-21 15:27:06
    //---------------------------------------------------------
    private ArrayList        demandFieldList        = new ArrayList();
    private ArrayList        postFieldSetList       = new ArrayList();
    private ArrayList        postFieldItemList      = new ArrayList();
    private String           demandFieldId;
    private String           postFieldSetId;
    private String           postFieldItemId;
    private String           tableStr;
    private String           param;
    private String           hireMajorCode          = "";                     //专业的代码类
    private String           logTextName            = "";                     //导入招聘需求结果信息日志名
    private String           infor                  = "";

    public String getInfor() {
        return infor;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }

    public String getLogTextName() {
        return logTextName;
    }

    public void setLogTextName(String logTextName) {
        this.logTextName = logTextName;
    }

    public String getNoexcel() {
        return noexcel;
    }

    public void setNoexcel(String noexcel) {
        this.noexcel = noexcel;
    }

    private String    spRelation = "";
    private String    spcount    = "";              //直接领导的个数
    private ArrayList zparrplist = new ArrayList();
    private String    actortype  = "";
    private String    appname    = "";
    private String    zpappfalg  = "";

    @Override
    public void outPutFormHM() {
        this.setParam((String) this.getFormHM().get("param"));
        this.setDemandFieldList((ArrayList) this.getFormHM().get("demandFieldList"));
        this.setDemandFieldId((String) this.getFormHM().get("demandFieldId"));
        this.setPostFieldItemId((String) this.getFormHM().get("postFieldItemId"));
        this.setPostFieldItemList((ArrayList) this.getFormHM().get("postFieldItemList"));
        this.setPostFieldSetId((String) this.getFormHM().get("postFieldSetId"));
        this.setPostFieldSetList((ArrayList) this.getFormHM().get("postFieldSetList"));
        this.setTableStr((String) this.getFormHM().get("tableStr"));
        this.setHireMajor((String) this.getFormHM().get("hireMajor"));
        this.setSchoolPosition((String) this.getFormHM().get("schoolPosition"));
        this.setReturnflag((String) this.getFormHM().get("returnflag"));
        this.setGroupFieldId((String) this.getFormHM().get("groupFieldId"));
        this.setGroupFieldList((ArrayList) this.getFormHM().get("groupFieldList"));
        this.setIsReject((String) this.getFormHM().get("isReject"));
        this.setIsReport((String) this.getFormHM().get("isReport"));
        this.setAssignType((String) this.getFormHM().get("assignType"));
        this.setAssignTypeList((ArrayList) this.getFormHM().get("assignTypeList"));
        this.setIsCtrl((String) this.getFormHM().get("isCtrl"));
        this.setShowUMCard((String) this.getFormHM().get("showUMCard"));
        this.setOrgUN((String) this.getFormHM().get("orgUN"));
        this.setOrgUM((String) this.getFormHM().get("orgUM"));
        this.setRoleid((String) this.getFormHM().get("roleid"));
        this.setRolelist((ArrayList) this.getFormHM().get("rolelist"));
        this.setContent((String) this.getFormHM().get("content"));
        this.setA0100((String) this.getFormHM().get("a0100"));
        this.setReasons((String) this.getFormHM().get("reasons"));
        this.setMoreLevelSP((String) this.getFormHM().get("moreLevelSP"));
        this.setZ0321((String) this.getFormHM().get("z0321"));
        this.setOrgWillTableId((String) this.getFormHM().get("orgWillTableId"));
        this.setIsOrgWillTableIdDefine((String) this.getFormHM().get("isOrgWillTableIdDefine"));
        this.setCodeItemId((String) this.getFormHM().get("codeItemId"));
        this.setCodeSetId((String) this.getFormHM().get("codeSetId"));
        this.setPositionStateDesc((String) this.getFormHM().get("positionStateDesc"));
        this.setPositionState((String) this.getFormHM().get("positionState"));
        this.setPrivCode((String) this.getFormHM().get("privCode"));
        this.setEntertype((String) this.getFormHM().get("entertype"));
        this.setComplexTemplateList((ArrayList) this.getFormHM().get("complexTemplateList"));
        this.setTemplateType((String) this.getFormHM().get("templateType"));
        this.setTemplateid((String) this.getFormHM().get("templateid"));
        this.setSp((String) this.getFormHM().get("sp"));
        this.setSp_flag((String) this.getFormHM().get("sp_flag"));
        this.setOperateType((String) this.getFormHM().get("operateType"));
        this.setFieldList((ArrayList) this.getFormHM().get("fieldList"));
        this.setUpValue((String) this.getFormHM().get("upValue"));
        this.setPlanNumberFieldName((String) this.getFormHM().get("planNumberFieldName"));
        this.setActualNumberFieldName((String) this.getFormHM().get("actualNumberFieldName"));
        this.setLinkDesc((String) this.getFormHM().get("linkDesc"));
        this.setWhl_sql((String) this.getFormHM().get("whl_sql"));
        this.setSql((String) this.getFormHM().get("sql"));
        this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
        this.setTablename((String) this.getFormHM().get("tablename"));
        this.setExtendSql((String) this.getFormHM().get("extendSql"));
        this.setOrderSql((String) this.getFormHM().get("orderSql"));
        this.setSparePositionList((ArrayList) this.getFormHM().get("sparePositionList"));
        this.setSparePositionMap((HashMap) this.getFormHM().get("sparePositionMap"));
        this.setFieldSize((String) this.getFormHM().get("fieldSize"));
        this.setModel((String) this.getFormHM().get("model"));
        this.setUsername((String) this.getFormHM().get("username"));
        this.setSparePositionIDs((String[]) this.getFormHM().get("sparePositionIDs"));
        this.setPositionDemandDescList((ArrayList) this.getFormHM().get("positionDemandDescList"));
        this.setMailTemplateList((ArrayList) this.getFormHM().get("mailTemplateList"));
        this.setMailTemplateID((String) this.getFormHM().get("mailTemplateID"));
        this.setIsRevert((String) this.getFormHM().get("isRevert"));
        this.setPosConditionList((ArrayList) this.getFormHM().get("posConditionList"));
        this.setZ0301((String) this.getFormHM().get("z0301"));
        this.setIsPosBooklet((String) this.getFormHM().get("isPosBooklet"));
        this.setE01a1((String) this.getFormHM().get("e01a1"));
        this.setSplist((ArrayList) this.getFormHM().get("splist"));
        this.setZpNeedsFieldsList((ArrayList) this.getFormHM().get("zpNeedsFielsList"));
        this.setIsSendMessage((String) this.getFormHM().get("isSendMessage"));
        this.setReportHtml((String) this.getFormHM().get("reportHtml"));
        this.setChanelList((ArrayList) this.getFormHM().get("chanelList"));
        this.setZpchanel((String) this.getFormHM().get("zpchanel"));
        this.setNoexcel((String) this.getFormHM().get("noexcel"));
        this.setTestTemplateList((ArrayList) this.getFormHM().get("testTemplateList"));
        this.setTestid((String) this.getFormHM().get("testid"));
        this.setValid((String) this.getFormHM().get("valid"));
        this.setHireMajorCode((String) this.getFormHM().get("hireMajorCode"));
        this.setPagerows(Integer.parseInt(((String) this.getFormHM().get("pagerows"))));//控制每页显示多少条数据
        this.setLogTextName((String) this.getFormHM().get("logTextName"));
        this.setInfor((String) this.getFormHM().get("infor"));
        this.setSpRelation((String) this.getFormHM().get("spRelation"));
        this.setSpcount((String) this.getFormHM().get("spcount"));
        this.setZparrplist((ArrayList) this.getFormHM().get("zparrplist"));
        this.setActortype((String) this.getFormHM().get("actortype"));
        this.setAppname((String) this.getFormHM().get("appname"));
        this.setZpappfalg((String) this.getFormHM().get("zpappfalg"));

    }

    @Override
    public void inPutTransHM() {
        this.getFormHM().put("param", this.getParam());
        this.getFormHM().put("hireMajor", this.getHireMajor());
        this.getFormHM().put("returnflag", this.getReturnflag());
        this.getFormHM().put("isReject", this.getIsReject());
        this.getFormHM().put("isReport", this.getIsReport());
        this.getFormHM().put("assignType", this.getAssignType());
        this.getFormHM().put("assignTypeList", this.getAssignTypeList());
        this.getFormHM().put("showUMCard", this.getShowUMCard());
        this.getFormHM().put("orgUN", this.getOrgUN());
        this.getFormHM().put("orgUM", this.getOrgUM());
        this.getFormHM().put("roleid", this.getRoleid());
        this.getFormHM().put("rolelist", this.getRolelist());
        this.getFormHM().put("content", this.getContent());
        this.getFormHM().put("a0100", this.getA0100());
        this.getFormHM().put("reasons", this.getReasons());
        this.getFormHM().put("moreLevelSP", this.getMoreLevelSP());
        this.getFormHM().put("z0321", this.getZ0321());
        this.getFormHM().put("orgWillTableId", this.getOrgWillTableId());
        this.getFormHM().put("isOrgWillTableIdDefine", this.getIsOrgWillTableIdDefine());
        this.getFormHM().put("codeItemId", this.getCodeItemId());
        this.getFormHM().put("codeSetId", this.getCodeSetId());
        this.getFormHM().put("positionState", this.getPositionState());
        this.getFormHM().put("privCode", this.getPrivCode());
        this.getFormHM().put("entertype", this.getEntertype());
        this.getFormHM().put("complexTemplateList", this.getComplexTemplateList());
        this.getFormHM().put("templateType", this.getTemplateType());
        this.getFormHM().put("templateid", this.getTemplateid());
        this.getFormHM().put("sp", this.getSp());
        this.getFormHM().put("sp_flag", this.getSp_flag());
        this.getFormHM().put("operateType", this.getOperateType());
        this.getFormHM().put("posConditionList", this.getPosConditionList());
        this.getFormHM().put("positionDemandDescList", this.getPositionDemandDescList());
        this.getFormHM().put("isRevert", this.getIsRevert());
        this.getFormHM().put("mailTemplateID", this.getMailTemplateID());
        this.getFormHM().put("rejectCause", this.getRejectCause());
        this.getFormHM().put("extendSql", this.getExtendSql());
        this.getFormHM().put("orderSql", this.getOrderSql());
        this.getFormHM().put("sparePositionIDs", this.getSparePositionIDs());
        this.getFormHM().put("z0301", this.getZ0301());
        this.getFormHM().put("file", this.getFile());
        this.getFormHM().put("upValue", this.getUpValue());
        this.getFormHM().put("vague", this.getVague());
        this.getFormHM().put("isCtrl", this.getIsCtrl());
        this.getFormHM().put("lineFields", this.getLineFields());
        this.getFormHM().put("lieFields", this.getLieFields());
        this.getFormHM().put("resultFields", this.getResultFields());
        this.getFormHM().put("zpchanel", this.getZpchanel());
        this.getFormHM().put("testTemplateList", this.getTestTemplateList());
        this.getFormHM().put("testid", this.getTestid());
        this.getFormHM().put("valid", this.getValid());
        this.getFormHM().put("hireMajorCode", this.getHireMajorCode());

        this.getFormHM().put("pagerows", this.getPagerows() == 0 ? "10" : (this.getPagerows() + ""));
        this.getFormHM().put("logTextName", this.getLogTextName());
        this.getFormHM().put("infor", this.getInfor());

        this.getFormHM().put("spRelation", this.getSpRelation());
        this.getFormHM().put("spcount", this.getSpcount());
        this.getFormHM().put("zparrplist", this.getZparrplist());
        this.getFormHM().put("actortype", this.getActortype());
        this.getFormHM().put("appname", this.getAppname());
        this.getFormHM().put("zpappfalg", this.getZpappfalg());
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if ("/hire/demandPlan/positionDemand/init_card".equals(arg0.getPath()) && arg1.getParameter("b_query") != null) {
            cardparam.setPageid(0);
        } else if ("/hire/demandPlan/positionDemand/unit_card".equals(arg0.getPath())
                &&arg1.getParameter("b_query") != null){
            arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
        }else if("/hire/demandPlan/positionDemand/positionDemandTree".equals(arg0.getPath())&&arg1.getParameter("b_upFile")!=null){
        	//需求报批上传岗位说明书错误时
        	arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
        }else if("/hire/demandPlan/positionDemand/positionDemandTree".equals(arg0.getPath())&&"del".equals(arg1.getParameter("b_del"))){
        	//删除报错时返回
        	arg1.setAttribute("formpath", "/hire/demandPlan/positionDemand/positionDemandTree.do?b_query=1");//为了 failure_02.jsp的返回走herf
        }
	    else if("/hire/demandPlan/positionDemand/positionDemandTree".equals(arg0.getPath())&&arg1.getParameter("b_importdemandzip")!=null){
	    	//上传报错时返回
	    	arg1.setAttribute("formpath", "/hire/demandPlan/positionDemand/positionDemandTree.do?br_importdemand=link&flag=1");//为了 failure_02.jsp的返回走herf
	    }

        return super.validate(arg0, arg1);
    }

    public ArrayList getFieldlist() {
        return fieldlist;
    }

    public void setFieldlist(ArrayList fieldlist) {
        this.fieldlist = fieldlist;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getExtendSql() {
        return extendSql;
    }

    public void setExtendSql(String extendSql) {
        this.extendSql = extendSql;
    }

    public String getOrderSql() {
        return orderSql;
    }

    public void setOrderSql(String orderSql) {
        this.orderSql = orderSql;
    }

    public ArrayList getSparePositionList() {
        return sparePositionList;
    }

    public void setSparePositionList(ArrayList sparePositionList) {
        this.sparePositionList = sparePositionList;
    }

    public String[] getSparePositionIDs() {
        return sparePositionIDs;
    }

    public void setSparePositionIDs(String[] sparePositionIDs) {
        this.sparePositionIDs = sparePositionIDs;
    }

    public HashMap getSparePositionMap() {
        return sparePositionMap;
    }

    public void setSparePositionMap(HashMap sparePositionMap) {
        this.sparePositionMap = sparePositionMap;
    }

    public String getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(String fieldSize) {
        this.fieldSize = fieldSize;
    }

    public String getWhl_sql() {
        return whl_sql;
    }

    public void setWhl_sql(String whl_sql) {
        this.whl_sql = whl_sql;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLinkDesc() {
        return linkDesc;
    }

    public void setLinkDesc(String linkDesc) {
        this.linkDesc = linkDesc;
    }

    public String getActualNumberFieldName() {
        return actualNumberFieldName;
    }

    public void setActualNumberFieldName(String actualNumberFieldName) {
        this.actualNumberFieldName = actualNumberFieldName;
    }

    public String getPlanNumberFieldName() {
        return planNumberFieldName;
    }

    public void setPlanNumberFieldName(String planNumberFieldName) {
        this.planNumberFieldName = planNumberFieldName;
    }

    public ArrayList getPositionDemandDescList() {
        return positionDemandDescList;
    }

    public void setPositionDemandDescList(ArrayList positionDemandDescList) {
        this.positionDemandDescList = positionDemandDescList;
    }

    public String getIsRevert() {
        return isRevert;
    }

    public void setIsRevert(String isRevert) {
        this.isRevert = isRevert;
    }

    public ArrayList getMailTemplateList() {
        return mailTemplateList;
    }

    public void setMailTemplateList(ArrayList mailTemplateList) {
        this.mailTemplateList = mailTemplateList;
    }

    public String getMailTemplateID() {
        return mailTemplateID;
    }

    public void setMailTemplateID(String mailTemplateID) {
        this.mailTemplateID = mailTemplateID;
    }

    public ArrayList getPosConditionList() {
        return posConditionList;
    }

    public void setPosConditionList(ArrayList posConditionList) {
        this.posConditionList = posConditionList;
    }

    public String getZ0301() {
        return z0301;
    }

    public void setZ0301(String z0301) {
        this.z0301 = z0301;
    }

    public String getUpValue() {
        return upValue;
    }

    public void setUpValue(String upValue) {
        this.upValue = upValue;
    }

    public String getVague() {
        return vague;
    }

    public void setVague(String vague) {
        this.vague = vague;
    }

    public ArrayList getFieldList() {
        return fieldList;
    }

    public void setFieldList(ArrayList fieldList) {
        this.fieldList = fieldList;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public FormFile getFile() {
        return file;
    }

    public void setFile(FormFile file) {
        this.file = file;
    }

    public String getE01a1() {
        return e01a1;
    }

    public void setE01a1(String e01a1) {
        this.e01a1 = e01a1;
    }

    public String getIsPosBooklet() {
        return isPosBooklet;
    }

    public void setIsPosBooklet(String isPosBooklet) {
        this.isPosBooklet = isPosBooklet;
    }

    public String getSp_flag() {
        return sp_flag;
    }

    public void setSp_flag(String sp_flag) {
        this.sp_flag = sp_flag;
    }

    public ArrayList getSplist() {
        return splist;
    }

    public void setSplist(ArrayList splist) {
        this.splist = splist;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getRejectCause() {
        return rejectCause;
    }

    public void setRejectCause(String rejectCause) {
        this.rejectCause = rejectCause;
    }

    public ArrayList getComplexTemplateList() {
        return complexTemplateList;
    }

    public void setComplexTemplateList(ArrayList complexTemplateList) {
        this.complexTemplateList = complexTemplateList;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getEntertype() {
        return entertype;
    }

    public void setEntertype(String entertype) {
        this.entertype = entertype;
    }

    public String getPrivCode() {
        return privCode;
    }

    public void setPrivCode(String privCode) {
        this.privCode = privCode;
    }

    public String getPositionState() {
        return positionState;
    }

    public void setPositionState(String positionState) {
        this.positionState = positionState;
    }

    public String getPositionStateDesc() {
        return positionStateDesc;
    }

    public void setPositionStateDesc(String positionStateDesc) {
        this.positionStateDesc = positionStateDesc;
    }

    public String getCodeItemId() {
        return codeItemId;
    }

    public void setCodeItemId(String codeItemId) {
        this.codeItemId = codeItemId;
    }

    public String getCodeSetId() {
        return codeSetId;
    }

    public void setCodeSetId(String codeSetId) {
        this.codeSetId = codeSetId;
    }

    public String getIsOrgWillTableIdDefine() {
        return isOrgWillTableIdDefine;
    }

    public void setIsOrgWillTableIdDefine(String isOrgWillTableIdDefine) {
        this.isOrgWillTableIdDefine = isOrgWillTableIdDefine;
    }

    public String getOrgWillTableId() {
        return orgWillTableId;
    }

    public void setOrgWillTableId(String orgWillTableId) {
        this.orgWillTableId = orgWillTableId;
    }

    public CardTagParamView getCardparam() {
        return cardparam;
    }

    public void setCardparam(CardTagParamView cardparam) {
        this.cardparam = cardparam;
    }

    public String getZ0321() {
        return z0321;
    }

    public void setZ0321(String z0321) {
        this.z0321 = z0321;
    }

    public String getMoreLevelSP() {
        return moreLevelSP;
    }

    public void setMoreLevelSP(String moreLevelSP) {
        this.moreLevelSP = moreLevelSP;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getA0100() {
        return a0100;
    }

    public void setA0100(String a0100) {
        this.a0100 = a0100;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList getRolelist() {
        return rolelist;
    }

    public void setRolelist(ArrayList rolelist) {
        this.rolelist = rolelist;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getOrgUN() {
        return orgUN;
    }

    public void setOrgUN(String orgUN) {
        this.orgUN = orgUN;
    }

    public String getOrgUM() {
        return orgUM;
    }

    public void setOrgUM(String orgUM) {
        this.orgUM = orgUM;
    }

    public String getShowUMCard() {
        return showUMCard;
    }

    public void setShowUMCard(String showUMCard) {
        this.showUMCard = showUMCard;
    }

    public String getIsCtrl() {
        return isCtrl;
    }

    public void setIsCtrl(String isCtrl) {
        this.isCtrl = isCtrl;
    }

    public String getAssignType() {

        return assignType;
    }

    public void setAssignType(String assignType) {

        this.assignType = assignType;
    }

    public ArrayList getAssignTypeList() {

        return assignTypeList;
    }

    public void setAssignTypeList(ArrayList assignTypeList) {

        this.assignTypeList = assignTypeList;
    }

    public String getIsReport() {
        return isReport;
    }

    public void setIsReport(String isReport) {
        this.isReport = isReport;
    }

    public String getIsReject() {
        return isReject;
    }

    public void setIsReject(String isReject) {
        this.isReject = isReject;
    }

    public ArrayList getGroupFieldList() {
        return groupFieldList;
    }

    public void setGroupFieldList(ArrayList groupFieldList) {
        this.groupFieldList = groupFieldList;
    }

    public String getGroupFieldId() {
        return groupFieldId;
    }

    public void setGroupFieldId(String groupFieldId) {
        this.groupFieldId = groupFieldId;
    }

    public String getSchoolPosition() {
        return schoolPosition;
    }

    public void setSchoolPosition(String schoolPosition) {
        this.schoolPosition = schoolPosition;
    }

    public String getHireMajor() {
        return hireMajor;
    }

    public void setHireMajor(String hireMajor) {
        this.hireMajor = hireMajor;
    }

    public String getLineFields() {
        return lineFields;
    }

    public void setLineFields(String lineFields) {
        this.lineFields = lineFields;
    }

    public String getLieFields() {
        return lieFields;
    }

    public void setLieFields(String lieFields) {
        this.lieFields = lieFields;
    }

    public String getResultFields() {
        return resultFields;
    }

    public void setResultFields(String resultFields) {
        this.resultFields = resultFields;
    }

    public String getZpNeedsField() {
        return zpNeedsField;
    }

    public void setZpNeedsField(String zpNeedsField) {
        this.zpNeedsField = zpNeedsField;
    }

    public ArrayList getZpNeedsFieldsList() {
        return zpNeedsFieldsList;
    }

    public void setZpNeedsFieldsList(ArrayList zpNeedsFielsList) {
        this.zpNeedsFieldsList = zpNeedsFielsList;
    }

    public String getIsSendMessage() {
        return isSendMessage;
    }

    public void setIsSendMessage(String isSendMessage) {
        this.isSendMessage = isSendMessage;
    }

    public String getReportHtml() {
        return reportHtml;
    }

    public void setReportHtml(String reportHtml) {
        this.reportHtml = reportHtml;
    }

    public ArrayList getChanelList() {
        return chanelList;
    }

    public void setChanelList(ArrayList chanelList) {
        this.chanelList = chanelList;
    }

    public String getZpchanel() {
        return zpchanel;
    }

    public void setZpchanel(String zpchanel) {
        this.zpchanel = zpchanel;
    }

    public String getTableStr() {
        return tableStr;
    }

    public void setTableStr(String tableStr) {
        this.tableStr = tableStr;
    }

    public ArrayList getDemandFieldList() {
        return demandFieldList;
    }

    public void setDemandFieldList(ArrayList demandFieldList) {
        this.demandFieldList = demandFieldList;
    }

    public ArrayList getPostFieldSetList() {
        return postFieldSetList;
    }

    public void setPostFieldSetList(ArrayList postFieldSetList) {
        this.postFieldSetList = postFieldSetList;
    }

    public ArrayList getPostFieldItemList() {
        return postFieldItemList;
    }

    public void setPostFieldItemList(ArrayList postFieldItemList) {
        this.postFieldItemList = postFieldItemList;
    }

    public String getDemandFieldId() {
        return demandFieldId;
    }

    public void setDemandFieldId(String demandFieldId) {
        this.demandFieldId = demandFieldId;
    }

    public String getPostFieldSetId() {
        return postFieldSetId;
    }

    public void setPostFieldSetId(String postFieldSetId) {
        this.postFieldSetId = postFieldSetId;
    }

    public String getPostFieldItemId() {
        return postFieldItemId;
    }

    public void setPostFieldItemId(String postFieldItemId) {
        this.postFieldItemId = postFieldItemId;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public ArrayList getTestTemplateList() {
        return testTemplateList;
    }

    public void setTestTemplateList(ArrayList testTemplateList) {
        this.testTemplateList = testTemplateList;
    }

    public String getTestid() {
        return testid;
    }

    public void setTestid(String testid) {
        this.testid = testid;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getHireMajorCode() {
        return hireMajorCode;
    }

    public void setHireMajorCode(String hireMajorCode) {
        this.hireMajorCode = hireMajorCode;
    }

    public String getSpRelation() {
        return spRelation;
    }

    public void setSpRelation(String spRelation) {
        this.spRelation = spRelation;
    }

    public String getSpcount() {
        return spcount;
    }

    public void setSpcount(String spcount) {
        this.spcount = spcount;
    }

    public ArrayList getZparrplist() {
        return zparrplist;
    }

    public void setZparrplist(ArrayList zpArrplist) {
        zparrplist = zpArrplist;
    }

    public String getActortype() {
        return actortype;
    }

    public void setActortype(String actorType) {
        actortype = actorType;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getZpappfalg() {
        return zpappfalg;
    }

    public void setZpappfalg(String zpappfalg) {
        this.zpappfalg = zpappfalg;
    }

}
