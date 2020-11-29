package com.hjsj.hrms.module.recruitment.parameter.actionform;

import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * 
 * <p>Title:ParameterForm.java</p>
 * <p>Description:参数设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 2, 2006 9:20:20 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class ParameterForm extends FrameForm {
	private String contentTypeListJson="";//内容形式指标json 
	private String orgFieldListJson="";//单位介绍指标json 
	private String contentJson="";//内容形式json 
	private String str_sql="";//单位指标sql
	private String orderbystr="";
	private ArrayList columns = new ArrayList();//单位指标表头
	private Pageable pageable=new Pageable();//分页信息
	private String[] func; //保存人员库
	private String script_str="";//人员库设置
	private String job_str="";//在职人员库设置
	private String personStore="";//当前人员库
	private String destNbase="";//入职人员库
	/**
	 * 后台参数设置下拉框json
	 */
	private String personTypeListJson="";//json
	private String resumeStateFieldListJson="";//json
	private String hireObjectParameterListJson="";//json
	private String activeFieldListJson="";//json
	private String hireMajorCodeListJson="";//json
	private String previewTableListJson="";//json
	/**
	 * 前台参数设置下拉框json
	 */
	private String workExperienceListJson="";//json
	private String answerSetListJson="";//json
	private String resumeLevelFieldListJson="";//json
	private String cultureListJson="";//json
	private String flag="";//标记是后台参数还是前台参数
	private String testTemplateID="";						//测试考评表
	private ArrayList testTemplateList=new ArrayList();
	private String posCardID="";							//职位说明书
	private ArrayList postCardList=new ArrayList();		
	
	private ArrayList hireObjectList=new ArrayList();  //招聘对象列表
	
	private ArrayList preparedFieldList=new ArrayList();     //备选指标列表
	private ArrayList selectedFieldList=new ArrayList();

	
	private String musterFieldIDs="";
	private String musterFieldNames="";
	
	//职位查询字段
	private String posQueryFieldIDs=""; 
	private String posQueryFieldNames="";// 岗位快速查询指标
	//职位描述参数
	private String viewPosFieldIDs="";
	private String viewPosFieldNames="";
	//单位介绍指标	
	private String orgFieldNames="";
	private String orgFieldIDs="";  //单位介绍指标
	private String contentType="";  //内容形式指标
	private String contentTypeValue ="";//内容指标的值
	private ArrayList orgFieldList=new ArrayList();
	private ArrayList contentTypeList=new ArrayList();
	private PaginationForm orgListform=new PaginationForm();
	private String url;
	private String content;
	private String codeitemid = "";
	private String urlDisplay = "";
	private String contentDisplay = "";
	//浏览简历指标
	private String resumeFieldNames = "";
	private String resumeFieldIds = "";
	private ArrayList resumeFieldsList= new ArrayList();
	private ArrayList selectedResumeFieldsList = new ArrayList();
	private ArrayList resumeFieldsSetList = new ArrayList();
	private String fieldsetid ="";
	private String itemid = "";
	//简历状态指标
	private String resumeStateFieldNames = "";
	private String resumeStateFieldIds = "";
	private ArrayList resumeStateFieldsList = new ArrayList();
	private ArrayList selectedRSFieldsList = new ArrayList();
	private ArrayList resumeStateFieldsSetList = new ArrayList();
	//标识指标参数
	private ArrayList personTypeList = new ArrayList();
	private String personTypeId = "";
	//-------
	private String right_fields="";
	//private String[] right_fields= new String[0];
	private ArrayList selectedList = new ArrayList();
	//简历评语指标
	private ArrayList resumeLevelFieldList = new ArrayList();
	private String resumeLevelIds="";
	//简历统计项指标
	private ArrayList resumeStaticFieldsList = new ArrayList();
	private String resumeStaticIds = "";
	private String resumeStaticNames ="";
	private ArrayList resumeStaticFieldsSetList = new ArrayList();
	private ArrayList selectedStaticFieldsList = new ArrayList();
	private String staticfieldsetid = "";
	private String staticitemid="";
	//招聘对象指标
	private String hireObjectId="";
	private ArrayList hireObjectParameterList = new ArrayList();
	//招聘审批关系指标
	private ArrayList approvelist = new ArrayList();
	private String spRelation = "";
	
	/**打分方式*/
	private String mark_type;
	private ArrayList markList=new ArrayList();
	private ArrayList contentTList = new ArrayList();
	private String type;
	private String isClose;
	private String isVisible;
	private String max_count;
	/**登记表列表*/
	private ArrayList previewTableList = new ArrayList();
	/**登记表id*/
	private String previewTableId;
	/**常用查询条件类表*/
	private ArrayList commonQueryCondlist = new ArrayList();
	/**已经选择的列表*/
	private ArrayList selectedCommonQuery = new ArrayList();
	private String commonQueryIds;
	private String commonQueryNames;
	/**录用业务模板*/
	private String businessTemplateIds;
	private String businessTemplatenames;
	/**简历状态中，不可修改简历的状态*/
	private String resumeCodeValue;
	private String resumeCodeName;
	private String orgName;
	private String orgId;
	private String hiddenOrgId;
	/**项目路径*/
	private String path;
	/**要还原的文件*/
	/*private FormFile formfile;
	private FormFile file;*/
	private FormFile r_file;
	
	private String optType="";
	/**许可协议串*/
	private String licenseAgreementParameter;
	private String licenseAgreement;
	/**是否同意*/
	private String isAgree;
	/**培养方式代码类*/
	private String cultureCode;
	/**培养方式代码值*/
	private String cultureCodeItem;
	/**代码项列表*/
	private ArrayList cultureCodeList=new ArrayList();
	private ArrayList cultureList = new ArrayList();
	/**是否定义培养方式参数*/
	private String isDefinitionCulture;
    /**外网连接*/
	private String netHref="";
	private ArrayList tableList=new ArrayList();
	private ArrayList fieldSetList=new ArrayList();
	private String tableNames;
	private String tableListSize;
	private String fieldSetListSize;
	private ArrayList interviewingRevertItemList = new ArrayList();
	private String interviewingRevertItemid;
	/**招聘需求上报进行编制控制*/
	private String isCtrlReportBZ;
	/**招聘需求上报进行工资总额控制*/
	private String isCtrlReportGZ;
	/**职位最高工资标准*/
	private String positionSalaryStandardItem;
	private ArrayList positionSalaryStandardItemList = new ArrayList();
	/**单位部门预算表*/
	private String orgWillTableId;
	private ArrayList orgWillTableList = new ArrayList();
	/**面试过程是否记录*/
	private String isRemenberExamine;
	/**面试过程记录子集*/
	private String remenberExamineSet;
	private ArrayList remenberExamineSetList=new ArrayList();
	/**招聘需求支持多级审批*/
	private String moreLevelSP;
	/**招聘职位不关联组织机构*/
	private String hirePositionNotUnionOrg;
	/**招聘职位关联代码型指标*/
	private String hirePositionItem;
	private ArrayList hirePositionItemList;
	
	private String titleField;
	private ArrayList titleFieldList=new ArrayList();
	private String contentField;
	private ArrayList contentFieldList = new ArrayList();
	private String levelField;
	private ArrayList levelFieldList = new ArrayList();
	private String commentDateField;
	private ArrayList commentDateFieldList = new ArrayList();
	private String commentUserField;
	private ArrayList commentUserFieldList = new ArrayList();
	/**简历激活状态指标*/
	private String activeField;
	/**简历激活状态指标列表*/
	private ArrayList activeFieldList = new ArrayList();
	private String admissionCard;//打印准考证登记表号
	private String positionNumber;//外网每个单位下显示职位条数
	private String promptContent;//外网登录框下显示的提示内容
	private String promptContentParameter;
	private String l_p_type;//区分是编辑许可协议=l还是编辑外网提示信息=p
	private String scoreCard; //考试成绩登记表
	private String socialCard; //社会招聘模板
	private String schoolCard; //校园招聘模板
	
	/**校园招聘岗位*/
	private String schoolPosition;
	private String schoolPosDesc;
	private ArrayList schoolPositionList = new ArrayList();
	/**工作经验指标*/
	private String workExperience;
	private ArrayList workExperienceList = new ArrayList();
	/**招聘专业指标*/
	private ArrayList hireMajorList = new ArrayList();
	private String hireMajor;
	private String answerSet;
	private ArrayList answerSetList = new ArrayList();
	private String pos_listfield;
	/**外网列表显示指标**/
	private String pos_listfieldNames;
	private String pos_listfield_sort;
    /**外网列表显示指标排序方式**/
	private String pos_listfield_sortNames;
    /**新建校园岗位用参数*/
	private String schoolPositionOrg;
	private String schoolPositionOrgDesc;
	private String schoolPositionDesc;
	private String schoolPositionId;
	private String oldID;
	private String posdesc="";
	private String smg="";			//短信通知标识
	private String newTime="";	//最新职位时间
	private String posCommQueryFieldNames="";
	private String posCommQueryFieldIDs="";// 岗位查询指标 dml2011-6-22 11:00:39 
	private String cardIDs="";
	// 新需求 一个招聘需求多专业问题  郭峰增加
	private String isCharField = "";//判断是否是字符型指标  如果是，才出现"招聘专业代码"
	private ArrayList hireMajorCodeList = new ArrayList();//代码类的list列表
	private String hireMajorCode = "";//招聘专业代码
	private String passwordMinLength="";//密码最小长度 
	private String passwordMaxLength="";//密码最大长度 
	private String failedTime="";//最大失败次数
	private String unlockTime="";//解锁时间间隔
	private String appliedPosItems="";//外网已申请职位列表显示指标集
	private String startResumeAnalysis="";//简历解析服务额  0不启动 1启动
	private String resumeAnalysisName="";//解析服务用户名
	private String resumeAnalysisPassword="";//解析服务密码
	private String resumeAnalysisForeignJob="";//对外应聘职位指标
	private ArrayList foreignJobList=new ArrayList();//对外应聘职位列
	
	/**面试测评初试复试采用不同的测评表增加参数begin**/
	private ArrayList testTemplatAdvance = new ArrayList();//高级测评参数配置
	private ArrayList channelList=new ArrayList();//几种招聘渠道
	private ArrayList modeList=new ArrayList();//测评方式 31：初试32：复试
	private ArrayList itemList = new ArrayList();//涉及到的Z05中的数值类型的字段
	
	private String fieldid;
	private ArrayList fieldlist = new ArrayList();
	private ArrayList itemlist = new ArrayList();
	private String sortitem;
	private String checkflag="";
	private String salaryid;
	private String xuj;
	private String unitLevel;
	private String maxFileSize;
	private String hirePostByLayer="";//只显示本级单位的招聘岗位  
	private String acountBeActived="";//注册帐号是否需要激活
	private String attach;
	/**前台是否显示指标说明*/
	private String explaination;
	/**简历附件分类代码类*/
    private String attachCodeset;
    /**简历附件分渠道分类*/
    private String attachHire;
    /**渠道授权设置*/
    private String hireChannelPriv;
	/**是否上传照片*/
	private String photo;
	private String complexPassword="";//是否使用复杂密码  
	private String hirePostByLayerH="";//只显示本级单位的招聘岗位  
	private String acountBeActivedH="";//注册帐号是否需要激活
	private String attachH;
	/**前台是否显示指标说明*/
	private String explainationH;
	/**是否上传照片*/
	private String photoH;
	private String complexPasswordH="";//是否使用复杂密码  
	private String unitOrDepart="";//“单位”或“单位、部门”进行职位的分组显示
	private String unitOrDepartListJson="";//“单位”或“单位、部门”进行职位的分组显示Json
	private String selectValue="";//简历模版拼接传到后台的默认选中的值
	private String nameValue="";//简历模版拼接传到后台的名称
	private String cardItemIds="";//简历模版拼接传到后台的代码编号
	private String allItemsId;//传到后台选中的id
	private String candidate_status;//应聘人员身份指标
	private String candidate_status_ListJson;//待选应聘人员身份指标
	private String certificate_type;//应聘人员证件类型指标
	private String func_only; //应聘人员证件号码指标
	private String certificate_type_ListJson;//待选证件类型指标
	private String certificate_number_ListJson;//待选证件号码指标
	private String register_endtime;//注册帐号截止时间
	
	
	public String getRegister_endtime() {
		return register_endtime;
	}

	public void setRegister_endtime(String register_endtime) {
		this.register_endtime = register_endtime;
	}

	public String getFunc_only() {
		return func_only;
	}

	public void setFunc_only(String func_only) {
		this.func_only = func_only;
	}
	
	public String getCertificate_type_ListJson() {
		return certificate_type_ListJson;
	}

	public void setCertificate_type_ListJson(String certificate_type_ListJson) {
		this.certificate_type_ListJson = certificate_type_ListJson;
	}

	public String getCertificate_number_ListJson() {
		return certificate_number_ListJson;
	}

	public void setCertificate_number_ListJson(String certificate_number_ListJson) {
		this.certificate_number_ListJson = certificate_number_ListJson;
	}

	public String getCandidate_status() {
		return candidate_status;
	}
	
	public String getCertificate_type() {
		return certificate_type;
	}

	public void setCertificate_type(String certificate_type) {
		this.certificate_type = certificate_type;
	}

	public void setCandidate_status(String candidate_status) {
		this.candidate_status = candidate_status;
	}

	public String getCandidate_status_ListJson() {
		return candidate_status_ListJson;
	}
	
	public void setCandidate_status_ListJson(String candidate_status_ListJson) {
		this.candidate_status_ListJson = candidate_status_ListJson;
	}

	public String getUnitOrDepart() {
		return unitOrDepart;
	}

	public void setUnitOrDepart(String unitOrDepart) {
		this.unitOrDepart = unitOrDepart;
	}

	public String getUnitOrDepartListJson() {
		return unitOrDepartListJson;
	}

	public void setUnitOrDepartListJson(String unitOrDepartListJson) {
		this.unitOrDepartListJson = unitOrDepartListJson;
	}

	/** 
     * @return testTemplatAdvance 
     */
    public ArrayList getTestTemplatAdvance() {
        return testTemplatAdvance;
    }

    /** 
     * @param testTemplatAdvance 要设置的 testTemplatAdvance 
     */
    public void setTestTemplatAdvance(ArrayList testTemplatAdvance) {
        this.testTemplatAdvance = testTemplatAdvance;
    }

    /** 
     * @return channelList 
     */
    public ArrayList getChannelList() {
        return channelList;
    }

    /** 
     * @param channelList 要设置的 channelList 
     */
    public void setChannelList(ArrayList channelList) {
        this.channelList = channelList;
    }

    /** 
     * @return modeList 
     */
    public ArrayList getModeList() {
        return modeList;
    }

    /** 
     * @param modeList 要设置的 modeList 
     */
    public void setModeList(ArrayList modeList) {
        this.modeList = modeList;
    }

    /** 
     * @return itemList 
     */
    public ArrayList getItemList() {
        return itemList;
    }

    /** 
     * @param itemList 要设置的 itemList 
     */
    public void setItemList(ArrayList itemList) {
        this.itemList = itemList;
    }

    /**面试测评初试复试采用不同的测评表增加参数end**/
	/** 
     * @return pos_listfield_sort 
     */
    public String getPos_listfield_sort() {
        return pos_listfield_sort;
    }

    /** 
     * @param posListfieldSort 要设置的 pos_listfield_sort 
     */
    public void setPos_listfield_sort(String posListfieldSort) {
        pos_listfield_sort = posListfieldSort;
    }
	   /** 
     * @return pos_listfield_sortNames 
     */
    public String getPos_listfield_sortNames() {
        return pos_listfield_sortNames;
    }

    /** 
     * @param posListfieldSortNames 要设置的 pos_listfield_sortNames 
     */
    public void setPos_listfield_sortNames(String posListfieldSortNames) {
        pos_listfield_sortNames = posListfieldSortNames;
    }
	public String getComplexPassword() {
		return complexPassword;
	}

	public void setComplexPassword(String complexPassword) {
		this.complexPassword = complexPassword;
	}

	public String getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(String passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	public String getPasswordMaxLength() {
		return passwordMaxLength;
	}

	public void setPasswordMaxLength(String passwordMaxLength) {
		this.passwordMaxLength = passwordMaxLength;
	}

	public String getFailedTime() {
		return failedTime;
	}

	public void setFailedTime(String failedTime) {
		this.failedTime = failedTime;
	}

	public String getUnlockTime() {
		return unlockTime;
	}

	public void setUnlockTime(String unlockTime) {
		this.unlockTime = unlockTime;
	}

	public String getCardIDs() {
		return cardIDs;
	}

	public String getHirePostByLayer() {
		return hirePostByLayer;
	}

	public void setHirePostByLayer(String hirePostByLayer) {
		this.hirePostByLayer = hirePostByLayer;
	}

	public void setCardIDs(String cardIDs) {
		this.cardIDs = cardIDs;
	}

	public ArrayList getCardList() {
		return cardList;
	}

	public void setCardList(ArrayList cardList) {
		this.cardList = cardList;
	}

	private ArrayList cardList=new ArrayList();
	public String getAcountBeActived() {
		return acountBeActived;
	}

	public void setAcountBeActived(String acountBeActived) {
		this.acountBeActived = acountBeActived;
	}

	public String getPosCommQueryFieldIDs() {
		return posCommQueryFieldIDs;
	}

	public void setPosCommQueryFieldIDs(String posCommQueryFields) {
		this.posCommQueryFieldIDs = posCommQueryFields;
	}

	public String getPosCommQueryFieldNames() {
		return posCommQueryFieldNames;
	}

	public void setPosCommQueryFieldNames(String posCommQueryFieldNames) {
		this.posCommQueryFieldNames = posCommQueryFieldNames;
	}

	public String getSmg() {
		return smg;
	}

	public void setSmg(String smg) {
		this.smg = smg;
	}

	public String getPosdesc() {
		return posdesc;
	}

	public void setPosdesc(String posdesc) {
		this.posdesc = posdesc;
	}

	@Override
    public void outPutFormHM() {
		this.setOrgFieldListJson((String)this.getFormHM().get("orgFieldListJson"));
		this.setContentTypeListJson((String)this.getFormHM().get("contentTypeListJson"));
		this.setContentJson((String)this.getFormHM().get("contentJson"));
		this.setOrderbystr((String)this.getFormHM().get("orderbystr"));
		this.setStr_sql((String)this.getFormHM().get("str_sql"));//设置测评方式
		this.setColumns((ArrayList) this.getFormHM().get("columns"));//设置招聘渠道list
	    this.setChannelList((ArrayList) this.getFormHM().get("channelList"));//设置招聘渠道list
	    this.setTestTemplatAdvance((ArrayList) this.getFormHM().get("testTemplatAdvance"));//设置高级测评配置的数据
	    this.setModeList((ArrayList) this.getFormHM().get("modeList"));//设置测评方式
	    this.setItemList((ArrayList) this.getFormHM().get("itemList"));//设置z05中涉及到数值型的指标
		this.setCardList((ArrayList)this.getFormHM().get("cardList"));
		this.setCardIDs((String)this.getFormHM().get("cardIDs"));
		this.setAcountBeActived((String)this.getFormHM().get("acountBeActived"));
		this.setSchoolPosDesc((String)this.getFormHM().get("schoolPosDesc"));
		this.setOldID((String)this.getFormHM().get("oldID"));
		this.setSchoolPositionOrg((String)this.getFormHM().get("schoolPositionOrg"));
		this.setSchoolPositionDesc((String)this.getFormHM().get("schoolPositionDesc"));
		this.setSchoolPositionOrgDesc((String)this.getFormHM().get("schoolPositionOrgDesc"));
		this.setSchoolPositionId((String)this.getFormHM().get("schoolPositionId"));
		this.setPos_listfield((String)this.getFormHM().get("pos_listfield"));
		this.setPos_listfieldNames((String)this.getFormHM().get("pos_listfieldNames"));
		this.setPos_listfield_sort((String) this.getFormHM().get("pos_listfield_sort"));
		this.setPos_listfield_sortNames((String) this.getFormHM().get("pos_listfield_sortNames"));
		this.setAnswerSetList((ArrayList)this.getFormHM().get("answerSetList"));
		this.setAnswerSet((String)this.getFormHM().get("answerSet"));
		this.setHireMajorList((ArrayList)this.getFormHM().get("hireMajorList"));
		this.setHireMajor((String)this.getFormHM().get("hireMajor"));
		this.setWorkExperience((String)this.getFormHM().get("workExperience"));
		this.setWorkExperienceList((ArrayList)this.getFormHM().get("workExperienceList"));
		this.setSchoolPosition((String)this.getFormHM().get("schoolPosition"));
		this.setSchoolPositionList((ArrayList)this.getFormHM().get("schoolPositionList"));
		this.setPromptContentParameter((String)this.getFormHM().get("promptContentParameter"));
		this.setL_p_type((String)this.getFormHM().get("l_p_type"));
		this.setPositionNumber((String)this.getFormHM().get("positionNumber"));
		this.setPromptContent((String)this.getFormHM().get("promptContent"));
		this.setAdmissionCard((String)this.getFormHM().get("admissionCard"));
		this.setScoreCard((String)this.getFormHM().get("scoreCard"));
		this.setSocialCard((String)this.getFormHM().get("socialCard"));
		this.setSchoolCard((String)this.getFormHM().get("schoolCard"));
		this.setActiveField((String)this.getFormHM().get("activeField"));
		this.setActiveFieldList((ArrayList)this.getFormHM().get("activeFieldList"));
		this.setCommentUserFieldList((ArrayList)this.getFormHM().get("commentUserFieldList"));
		this.setCommentUserField((String)this.getFormHM().get("commentUserField"));
		this.setCommentDateFieldList((ArrayList)this.getFormHM().get("commentDateFieldList"));
		this.setCommentDateField((String)this.getFormHM().get("commentDateField"));
		this.setLevelFieldList((ArrayList)this.getFormHM().get("levelFieldList"));
		this.setLevelField((String)this.getFormHM().get("levelField"));
		this.setContentFieldList((ArrayList)this.getFormHM().get("contentFieldList"));
		this.setContentField((String)this.getFormHM().get("contentField"));
		this.setTitleFieldList((ArrayList)this.getFormHM().get("titleFieldList"));
		this.setTitleField((String)this.getFormHM().get("titleField"));
		this.setIsCtrlReportBZ((String)this.getFormHM().get("isCtrlReportBZ"));
		this.setIsCtrlReportGZ((String)this.getFormHM().get("isCtrlReportGZ"));
		this.setPositionSalaryStandardItemList((ArrayList)this.getFormHM().get("positionSalaryStandardItemList"));
		this.setPositionSalaryStandardItem((String)this.getFormHM().get("positionSalaryStandardItem"));
		this.setOrgWillTableList((ArrayList)this.getFormHM().get("orgWillTableList"));
		this.setOrgWillTableId((String)this.getFormHM().get("orgWillTableId"));
		this.setIsRemenberExamine((String)this.getFormHM().get("isRemenberExamine"));
		this.setRemenberExamineSetList((ArrayList)this.getFormHM().get("remenberExamineSetList"));
		this.setRemenberExamineSet((String)this.getFormHM().get("remenberExamineSet"));
		this.setMoreLevelSP((String)this.getFormHM().get("moreLevelSP"));
		this.setHirePositionNotUnionOrg((String)this.getFormHM().get("hirePositionNotUnionOrg"));
		this.setHirePositionItemList((ArrayList)this.getFormHM().get("hirePositionItemList"));
		this.setHirePositionItem((String)this.getFormHM().get("hirePositionItem"));
		this.setInterviewingRevertItemList((ArrayList)this.getFormHM().get("interviewingRevertItemList"));
		this.setInterviewingRevertItemid((String)this.getFormHM().get("interviewingRevertItemid"));
		this.setTableNames((String)this.getFormHM().get("tableNames"));
		this.setFieldSetListSize((String)this.getFormHM().get("fieldSetListSize"));
		this.setTableListSize((String)this.getFormHM().get("tableListSize"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
		this.setTableList((ArrayList)this.getFormHM().get("tableList"));
		this.setNetHref((String)this.getFormHM().get("netHref"));
		this.setLicenseAgreementParameter((String)this.getFormHM().get("licenseAgreementParameter"));
		this.setCultureCodeList((ArrayList)this.getFormHM().get("cultureCodeList"));
		this.setLicenseAgreement((String)this.getFormHM().get("licenseAgreement"));
		this.setCultureList((ArrayList)this.getFormHM().get("cultureList"));
		this.setCultureCodeItem((String)this.getFormHM().get("cultureCodeItem"));
		this.setCultureCode((String)this.getFormHM().get("cultureCode"));
		this.setIsDefinitionCulture((String)this.getFormHM().get("isDefinitionCulture"));
		this.setOptType((String)this.getFormHM().get("optType"));
		this.setResumeCodeName((String)this.getFormHM().get("resumeCodeName"));
		this.setResumeCodeValue((String)this.getFormHM().get("resumeCodeValue"));
	    this.setBusinessTemplateIds((String)this.getFormHM().get("businessTemplateIds"));
	    this.setBusinessTemplatenames((String)this.getFormHM().get("businessTemplatenames"));
		this.setAttach((String)this.getFormHM().get("attach"));
		this.setHiddenOrgId((String)this.getFormHM().get("hiddenOrgId"));
		this.setOrgId((String)this.getFormHM().get("orgId"));
		this.setOrgName((String)this.getFormHM().get("orgName"));
		this.setExplaination((String)this.getFormHM().get("explaination"));
		this.setPhoto((String)this.getFormHM().get("photo"));
		this.setCommonQueryCondlist((ArrayList)this.getFormHM().get("commonQueryCondlist"));
		this.setCommonQueryIds((String)this.getFormHM().get("commonQueryIds"));
		this.setCommonQueryNames((String)this.getFormHM().get("commonQueryNames"));
		this.setSelectedCommonQuery((ArrayList)this.getFormHM().get("selectedCommonQuery"));
		this.setHireObjectList((ArrayList)this.getFormHM().get("hireObjectList"));
		this.setTestTemplateID((String)this.getFormHM().get("testTemplateID"));
		this.setTestTemplateList((ArrayList)this.getFormHM().get("testTemplateList"));
		this.setPosCardID((String)this.getFormHM().get("posCardID"));
		this.setPostCardList((ArrayList)this.getFormHM().get("postCardList"));
		this.setPreparedFieldList((ArrayList)this.getFormHM().get("preparedFieldList"));
		this.setSelectedFieldList((ArrayList)this.getFormHM().get("selectedFieldList"));
		this.setMusterFieldIDs((String)this.getFormHM().get("musterFieldIDs"));
		this.setMusterFieldNames((String)this.getFormHM().get("musterFieldNames"));
		this.setPosQueryFieldIDs((String)this.getFormHM().get("posQueryFieldIDs"));
		this.setPosQueryFieldNames((String)this.getFormHM().get("posQueryFieldNames"));
		this.setViewPosFieldIDs((String)this.getFormHM().get("viewPosFieldIDs"));
		this.setViewPosFieldNames((String)this.getFormHM().get("viewPosFieldNames"));
		this.setOrgFieldIDs((String)this.getFormHM().get("orgFieldIDs"));
		this.setOrgFieldNames((String)this.getFormHM().get("orgFieldNames"));
		this.getOrgListform().setList((ArrayList)this.getFormHM().get("orgList"));
		this.setContentType((String)this.getFormHM().get("contentType"));
		this.setOrgFieldList((ArrayList)this.getFormHM().get("orgFieldList"));
		this.setContentTypeList((ArrayList)this.getFormHM().get("contentTypeList"));
		this.setUrl((String)this.getFormHM().get("url"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setContentTypeValue((String)this.getFormHM().get("contentTypeValue"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setResumeFieldIds((String)this.getFormHM().get("resumeFieldIds"));
		this.setResumeFieldNames((String)this.getFormHM().get("resumeFieldNames"));
		this.setResumeStateFieldIds((String)this.getFormHM().get("resumeStateFieldIds"));
		this.setResumeStateFieldNames((String)this.getFormHM().get("resumeStateFieldNames"));
		this.setUrlDisplay((String)this.getFormHM().get("urlDisplay"));
		this.setContentDisplay((String)this.getFormHM().get("contentDisplay"));
		this.setResumeFieldsList((ArrayList)this.getFormHM().get("resumeFieldsList"));
		this.setResumeStateFieldsList((ArrayList)this.getFormHM().get("resumeStateFieldsList"));
		this.setResumeFieldsSetList((ArrayList)this.getFormHM().get("resumeFieldsSetList"));
		this.setResumeStateFieldsSetList((ArrayList)this.getFormHM().get("resumeStateFieldsSetList"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setResumeStaticFieldsList((ArrayList)this.getFormHM().get("resumeStaticFieldsList"));
		this.setResumeStaticFieldsSetList((ArrayList)this.getFormHM().get("resumeStaticFieldsSetList"));
		this.setSelectedStaticFieldsList((ArrayList)this.getFormHM().get("selectedStaticFieldsList"));
		
		this.setPersonTypeList((ArrayList)this.getFormHM().get("personTypeList"));
		this.setSelectedResumeFieldsList((ArrayList)this.getFormHM().get("selectedResumeFieldsList"));
		this.setSelectedRSFieldsList((ArrayList)this.getFormHM().get("selectedRSFieldsList"));
		this.setSelectedList((ArrayList)this.getFormHM().get("selectedList"));
		this.setPersonTypeId((String)this.getFormHM().get("personTypeId"));
		this.setResumeLevelFieldList((ArrayList)this.getFormHM().get("resumeLevelFieldList"));
		this.setResumeLevelIds((String)this.getFormHM().get("resumeLevelIds"));
		this.setResumeStaticNames((String)this.getFormHM().get("resumeStaticNames"));
		this.setResumeStaticIds((String)this.getFormHM().get("resumeStaticIds"));
		//this.setHireObjectList((ArrayList)this.getFormHM().get("hireObjectList"));
		this.setHireObjectParameterList((ArrayList)this.getFormHM().get("hireObjectParameterList"));
        this.setHireObjectId((String)this.getFormHM().get("hireObjectId"));
		this.setMark_type((String)this.getFormHM().get("mark_type"));
		this.setMarkList((ArrayList)this.getFormHM().get("markList"));
		this.setContentTList((ArrayList)this.getFormHM().get("contentTList"));
		this.setType((String)this.getFormHM().get("type"));
		this.setIsClose((String)this.getFormHM().get("isClose"));
		this.setIsVisible((String)this.getFormHM().get("isVisible"));
		this.setMax_count((String)this.getFormHM().get("max_count"));
		this.setPreviewTableList((ArrayList)this.getFormHM().get("previewTableList"));
		this.setPreviewTableId((String)this.getFormHM().get("previewTableId"));
		this.setPosdesc((String)this.getFormHM().get("posdesc"));
		this.setSmg((String)this.getFormHM().get("smg"));
		this.setNewTime((String)this.getFormHM().get("newTime"));
		this.setPosCommQueryFieldNames((String)this.getFormHM().get("posCommQueryFieldNames"));
		this.setPosCommQueryFieldIDs((String)this.getFormHM().get("posCommQueryFieldIDs"));
		this.setIsCharField((String)this.getFormHM().get("isCharField"));
		this.setHireMajorCodeList((ArrayList)this.getFormHM().get("hireMajorCodeList"));
		this.setHireMajorCode((String)this.getFormHM().get("hireMajorCode"));
		this.setHirePostByLayer((String)this.getFormHM().get("hirePostByLayer"));
		this.setComplexPassword((String)this.getFormHM().get("complexPassword"));
		this.setPasswordMinLength((String)this.getFormHM().get("passwordMinLength"));
		this.setPasswordMaxLength((String)this.getFormHM().get("passwordMaxLength"));
		this.setFailedTime((String)this.getFormHM().get("failedTime"));
		this.setUnlockTime((String)this.getFormHM().get("unlockTime"));
		this.setAppliedPosItems((String)this.getFormHM().get("appliedPosItems"));
		this.setStartResumeAnalysis((String)this.getFormHM().get("startResumeAnalysis"));
		this.setResumeAnalysisName((String)this.getFormHM().get("resumeAnalysisName"));
		this.setResumeAnalysisPassword((String)this.getFormHM().get("resumeAnalysisPassword"));
		this.setResumeAnalysisForeignJob((String)this.getFormHM().get("resumeAnalysisForeignJob"));
		this.setForeignJobList((ArrayList)this.getFormHM().get("foreignJobList"));
		this.setApprovelist((ArrayList)this.getFormHM().get("approvelist"));
		this.setSpRelation((String)this.getFormHM().get("spRelation"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setScript_str((String)this.getFormHM().get("script_str"));
		this.setJob_str((String)this.getFormHM().get("job_str"));
		
		this.setResumeLevelFieldListJson((String)this.getFormHM().get("resumeLevelFieldListJson"));
		this.setCultureListJson((String)this.getFormHM().get("cultureListJson"));
		this.setAnswerSetListJson((String)this.getFormHM().get("answerSetListJson"));
		this.setWorkExperienceListJson((String)this.getFormHM().get("workExperienceListJson"));
		this.setPersonTypeListJson((String)this.getFormHM().get("personTypeListJson"));
		this.setResumeStateFieldListJson((String)this.getFormHM().get("resumeStateFieldListJson"));
		this.setHireObjectParameterListJson((String)this.getFormHM().get("hireObjectParameterListJson"));
		this.setActiveFieldListJson((String)this.getFormHM().get("activeFieldListJson"));
		this.setHireMajorCodeListJson((String)this.getFormHM().get("hireMajorCodeListJson"));
		this.setPreviewTableListJson((String)this.getFormHM().get("previewTableListJson"));
		
		this.setPersonStore((String)this.getFormHM().get("personStore"));
		
		this.setFieldid((String)this.getFormHM().get("fieldid"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setSortitem((String)this.getFormHM().get("sortitem"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.getFormHM().remove("salaryid");
		this.setXuj((String)this.getFormHM().get("xuj"));
		this.getFormHM().remove("xuj");
		
		this.setUnitLevel((String)this.getFormHM().get("unitLevel"));
		this.setMaxFileSize((String)this.getFormHM().get("maxFileSize"));
		this.setUnitOrDepart((String)this.getFormHM().get("unitOrDepart"));
		this.setUnitOrDepartListJson((String)this.getFormHM().get("unitOrDepartListJson"));
		
		this.setSelectValue((String)this.getFormHM().get("selectValue"));
		this.setNameValue((String)this.getFormHM().get("nameValue"));
		this.setCardItemIds((String)this.getFormHM().get("cardItemIds"));
		this.setAllItemsId((String)this.getFormHM().get("allItemsId"));
		this.setAttachCodeset((String)this.getFormHM().get("attachCodeset"));
	 	this.setAttachHire((String)this.getFormHM().get("attachHire"));
	 	this.setHireChannelPriv((String)this.getFormHM().get("hireChannelPriv"));
	 	this.setCandidate_status((String)this.getFormHM().get("candidate_status"));
	 	this.setCandidate_status_ListJson((String)this.getFormHM().get("candidate_status_ListJson"));
	 	this.setCertificate_type((String)this.getFormHM().get("certificate_type"));
	 	this.setCertificate_type_ListJson((String)this.getFormHM().get("certificate_type_ListJson"));
	 	this.setFunc_only((String)this.getFormHM().get("func_only"));
	 	this.setCertificate_number_ListJson((String)this.getFormHM().get("certificate_number_ListJson"));
	 	this.setDestNbase((String)this.getFormHM().get("destNbase"));
	 	this.setRegister_endtime((String)this.getFormHM().get("register_endtime"));
		}
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("personStore",this.getPersonStore());
		this.getFormHM().put("func",this.getFunc());
		this.getFormHM().put("cardIDs",this.getCardIDs());
		this.getFormHM().put("acountBeActived", this.getAcountBeActived());
		this.getFormHM().put("oldID", this.getOldID());
		this.getFormHM().put("schoolPositionOrg", this.getSchoolPositionOrg());
		this.getFormHM().put("schoolPositionOrgDesc", this.getSchoolPositionOrgDesc());
		this.getFormHM().put("schoolPositionDesc", this.getSchoolPositionDesc());
		this.getFormHM().put("schoolPositionId", this.getSchoolPositionId());
		this.getFormHM().put("pos_listfield",this.getPos_listfield());
		this.getFormHM().put("answerSet", this.getAnswerSet());
		this.getFormHM().put("hireMajor", this.getHireMajor());
		this.getFormHM().put("workExperience", this.getWorkExperience());
		this.getFormHM().put("schoolPosition", this.getSchoolPosition());
		this.getFormHM().put("promptContentParameter", this.getPromptContentParameter());
		this.getFormHM().put("l_p_type", this.getL_p_type());
		this.getFormHM().put("promptContent", this.getPromptContent());
		this.getFormHM().put("positionNumber", this.getPositionNumber());
		this.getFormHM().put("admissionCard",this.getAdmissionCard());
		this.getFormHM().put("scoreCard",this.getScoreCard());
		this.getFormHM().put("schoolCard", this.getSchoolCard());
		this.getFormHM().put("socialCard", this.getSocialCard());
		this.getFormHM().put("activeField", this.getActiveField());
		this.getFormHM().put("activeFieldList", this.getActiveFieldList());
		this.getFormHM().put("commentUserField", this.getCommentUserField());
		this.getFormHM().put("commentDateField", this.getCommentDateField());
		this.getFormHM().put("levelField", this.getLevelField());
		this.getFormHM().put("contentField", this.getContentField());
		this.getFormHM().put("titleField", this.getTitleField());
		this.getFormHM().put("isCtrlReportBZ", this.getIsCtrlReportBZ());
		this.getFormHM().put("isCtrlReportGZ", this.getIsCtrlReportGZ());
		this.getFormHM().put("positionSalaryStandardItem",this.getPositionSalaryStandardItem());
	    this.getFormHM().put("orgWillTableId", this.getOrgWillTableId());
		this.getFormHM().put("isRemenberExamine",this.getIsRemenberExamine());
		this.getFormHM().put("remenberExamineSet",this.getRemenberExamineSet());
		this.getFormHM().put("moreLevelSP", this.getMoreLevelSP());
		this.getFormHM().put("hirePositionNotUnionOrg", this.getHirePositionNotUnionOrg());
		this.getFormHM().put("hirePositionItem", this.getHirePositionItem());
		this.getFormHM().put("interviewingRevertItemid", this.getInterviewingRevertItemid());
		this.getFormHM().put("tableListSize",this.getTableListSize());
		this.getFormHM().put("fieldSetListSize",this.getFieldSetListSize());
		this.getFormHM().put("tableNames",this.getTableNames());
	    this.getFormHM().put("netHref", this.getNetHref());
	    this.getFormHM().put("licenseAgreementParameter", this.getLicenseAgreementParameter());
	    this.getFormHM().put("cultureCodeItem", this.getCultureCodeItem());
    	this.getFormHM().put("cultureCode", this.getCultureCode());
	    this.getFormHM().put("licenseAgreement", this.getLicenseAgreement());
    	this.getFormHM().put("optType",this.getOptType());
    	this.getFormHM().put("resumeCodeValue",this.getResumeCodeValue());
	    this.getFormHM().put("resumeCodeName",this.getResumeCodeName());
	    this.getFormHM().put("businessTemplatenames",this.getBusinessTemplatenames());
	    this.getFormHM().put("businessTemplateIds", this.getBusinessTemplateIds());
	    this.getFormHM().put("attach",this.getAttach());
	    this.getFormHM().put("hiddenOrgId",this.getHiddenOrgId());
	    this.getFormHM().put("orgId",this.getOrgId());
	    this.getFormHM().put("orgName",this.getOrgName());
	    this.getFormHM().put("explaination",this.getExplaination());
	    this.getFormHM().put("photo", this.getPhoto());
	    this.getFormHM().put("previewTableId", this.getPreviewTableId());
	    this.getFormHM().put("orgFieldNames",this.getOrgFieldNames());
		this.getFormHM().put("orgFieldIDs",this.getOrgFieldIDs());
		this.getFormHM().put("contentType",this.getContentType());
	    this.getFormHM().put("orgFieldList",this.getOrgFieldList());
	    this.getFormHM().put("contentTypeList",this.getContentTypeList());
	    this.getFormHM().put("selectedList",this.getOrgListform().getSelectedList());
		this.getFormHM().put("testTemplateID",this.getTestTemplateID());
		this.getFormHM().put("posCardID",this.getPosCardID());
		this.getFormHM().put("musterFieldIDs",this.getMusterFieldIDs());
		this.getFormHM().put("posQueryFieldIDs",this.getPosQueryFieldIDs());
		this.getFormHM().put("viewPosFieldIDs",this.getViewPosFieldIDs());
        this.getFormHM().put("url",this.getUrl());
        this.getFormHM().put("content",this.getContent());
        this.getFormHM().put("contentTypeValue",this.getContentTypeValue());
        this.getFormHM().put("codeitemid",this.getCodeitemid());
        this.getFormHM().put("resumeFieldIds",this.getResumeFieldIds());
        this.getFormHM().put("resumeStateFieldIds",this.getResumeStateFieldIds());
        this.getFormHM().put("urlDisplay",this.getUrlDisplay());
        this.getFormHM().put("contentDisplay",this.getContentDisplay());
        this.getFormHM().put("fieldsetid",this.getFieldsetid());
        this.getFormHM().put("personTypeId",this.getPersonTypeId());
        this.getFormHM().put("resumeLevelIds",this.getResumeLevelIds());
        this.getFormHM().put("resumeStaticIds",this.getResumeStaticIds());
        this.getFormHM().put("hireObjectId",this.getHireObjectId());
        this.getFormHM().put("mark_type",this.getMark_type());
        this.getFormHM().put("max_count",this.getMax_count());
        this.getFormHM().put("commonQueryIds",this.getCommonQueryIds());
        this.getFormHM().put("path",this.getPath());
        this.getFormHM().put("r_file",this.getR_file());
        this.getFormHM().put("posdesc", posdesc);
        this.getFormHM().put("smg", this.getSmg());
        this.getFormHM().put("newTime", this.getNewTime());
        this.getFormHM().put("posCommQueryFieldNames", this.getPosCommQueryFieldNames());
        this.getFormHM().put("posCommQueryFieldIDs", this.getPosCommQueryFieldIDs());
        this.getFormHM().put("isCharField", this.getIsCharField());
        this.getFormHM().put("hireMajorCodeList", this.getHireMajorCodeList());
        this.getFormHM().put("hireMajorCode", this.getHireMajorCode());
        this.getFormHM().put("hirePostByLayer", this.getHirePostByLayer());
        this.getFormHM().put("complexPassword", this.getComplexPassword());
        this.getFormHM().put("passwordMinLength", this.getPasswordMinLength());
        this.getFormHM().put("passwordMaxLength", this.getPasswordMaxLength());
        this.getFormHM().put("failedTime", this.getFailedTime());
        this.getFormHM().put("unlockTime", this.getUnlockTime());
        this.getFormHM().put("appliedPosItems", this.getAppliedPosItems());
        this.getFormHM().put("startResumeAnalysis", this.getStartResumeAnalysis());
        this.getFormHM().put("resumeAnalysisName", this.getResumeAnalysisName());
        this.getFormHM().put("resumeAnalysisPassword", this.getResumeAnalysisPassword());
        this.getFormHM().put("resumeAnalysisForeignJob", this.getResumeAnalysisForeignJob());
        this.getFormHM().put("foreignJobList", this.getForeignJobList());
        this.getFormHM().put("approvelist", this.getApprovelist());
        this.getFormHM().put("spRelation", this.getSpRelation());
        this.getFormHM().put("pos_listfield_sort", this.getPos_listfield_sort());
        this.getFormHM().put("unitLevel", this.getUnitLevel());
        this.getFormHM().put("maxFileSize", this.getMaxFileSize());
        this.getFormHM().put("hirePostByLayerH", this.getHirePostByLayerH());
        this.getFormHM().put("acountBeActivedH", this.getAcountBeActivedH());
        this.getFormHM().put("attachH", this.getAttachH());
        this.getFormHM().put("explainationH", this.getExplainationH());
        this.getFormHM().put("photoH", this.getPhotoH());
        this.getFormHM().put("complexPasswordH", this.getComplexPasswordH());
        this.getFormHM().put("unitOrDepart", this.getUnitOrDepart());
        this.getFormHM().put("unitOrDepartListJson", this.getUnitOrDepartListJson());
        this.getFormHM().put("selectValue", this.getSelectValue());
        this.getFormHM().put("nameValue", this.getNameValue());
        this.getFormHM().put("cardItemIds", this.getCardItemIds());
        this.getFormHM().put("allItemsId", this.getAllItemsId());
        this.getFormHM().put("attachCodeset", this.getAttachCodeset());
        this.getFormHM().put("attachHire", this.getAttachHire());
        this.getFormHM().put("hireChannelPriv", this.getHireChannelPriv());
        this.getFormHM().put("candidate_status", this.getCandidate_status());
        this.getFormHM().put("candidate_status_ListJson", this.getCandidate_status_ListJson());
        this.getFormHM().put("certificate_type", this.getCertificate_type());
        this.getFormHM().put("certificate_type_ListJson", this.getCertificate_type_ListJson());
        this.getFormHM().put("func_only", this.getFunc_only());
        this.getFormHM().put("certificate_number_ListJson", this.getCertificate_number_ListJson());
        this.getFormHM().put("destNbase", this.getDestNbase());
        this.getFormHM().put("register_endtime", this.getRegister_endtime());
		}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(!"weblogic".equals(SystemConfig.getPropertyValue("webserver")))
		{
	    	String pajs=arg1.getSession().getServletContext().getRealPath("/UserFiles");
	    	this.setPath(SafeCode.encode(pajs));
		}
		if("/hire/parameterSet/configureParameter".equals(arg0.getPath())&&arg1.getParameter("b_orgIntro")!=null)
		{
            /**定位到首页,*/
            if(this.getPagination()!=null){
            	this.getPagination().firstPage();
            	this.pageable.goFirstPage();
		        this.pageable.setPageSize(20);
            }
            //【8186】系统参数，单位介绍对搜索出来的机构进行编辑的时候，保存后，界面直接关闭了需要重新查找  jingq upd 2015.04.07
            if(arg1.getParameter("type")!=null){
            	if(this.getOrgListform()!=null)
            		this.getOrgListform().getPagination().firstPage();
            }
        }
		if(arg1.getParameter("b_init")!=null&& "init".equals(arg1.getParameter("b_init")))
			if(this.getOrgListform()!=null)
				this.getOrgListform().getPagination().firstPage();
		
		if("/hire/parameterSet/configureParameter/select_reduction_file".equals(arg0.getPath())
		        &&arg1.getParameter("b_reduction")!=null){
		    arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
		
		return super.validate(arg0, arg1);
		
	}

	public String getPosCardID() {
		return posCardID;
	}

	public void setPosCardID(String posCardID) {
		this.posCardID = posCardID;
	}

	public ArrayList getPostCardList() {
		return postCardList;
	}

	public void setPostCardList(ArrayList postCardList) {
		this.postCardList = postCardList;
	}

	public ArrayList getPreparedFieldList() {
		return preparedFieldList;
	}

	public void setPreparedFieldList(ArrayList preparedFieldList) {
		this.preparedFieldList = preparedFieldList;
	}

	public String getTestTemplateID() {
		return testTemplateID;
	}

	public void setTestTemplateID(String testTemplateID) {
		this.testTemplateID = testTemplateID;
	}

	public ArrayList getTestTemplateList() {
		return testTemplateList;
	}

	public void setTestTemplateList(ArrayList testTemplateList) {
		this.testTemplateList = testTemplateList;
	}

	public ArrayList getSelectedFieldList() {
		return selectedFieldList;
	}

	public void setSelectedFieldList(ArrayList selectedFieldList) {
		this.selectedFieldList = selectedFieldList;
	}

	public String getMusterFieldIDs() {
		return musterFieldIDs;
	}

	public void setMusterFieldIDs(String musterFieldIDs) {
		this.musterFieldIDs = musterFieldIDs;
	}

	public String getMusterFieldNames() {
		return musterFieldNames;
	}

	public void setMusterFieldNames(String musterFieldNames) {
		this.musterFieldNames = musterFieldNames;
	}

	public ArrayList getHireObjectList() {
		return hireObjectList;
	}

	public void setHireObjectList(ArrayList hireObjectList) {
		this.hireObjectList = hireObjectList;
	}


	public String getPosQueryFieldIDs() {
		return posQueryFieldIDs;
	}

	public void setPosQueryFieldIDs(String posQueryFieldIDs) {
		this.posQueryFieldIDs = posQueryFieldIDs;
	}

	public String getPosQueryFieldNames() {
		return posQueryFieldNames;
	}

	public void setPosQueryFieldNames(String posQueryFieldNames) {
		this.posQueryFieldNames = posQueryFieldNames;
	}

	public String getViewPosFieldIDs() {
		return viewPosFieldIDs;
	}

	public void setViewPosFieldIDs(String viewPosFieldIDs) {
		this.viewPosFieldIDs = viewPosFieldIDs;
	}

	public String getViewPosFieldNames() {
		return viewPosFieldNames;
	}

	public void setViewPosFieldNames(String viewPosFieldNames) {
		this.viewPosFieldNames = viewPosFieldNames;
	}

	public String getOrgFieldIDs() {
		return orgFieldIDs;
	}

	public void setOrgFieldIDs(String orgFieldIDs) {
		this.orgFieldIDs = orgFieldIDs;
	}

	public String getOrgFieldNames() {
		return orgFieldNames;
	}

	public void setOrgFieldNames(String orgFieldNames) {
		this.orgFieldNames = orgFieldNames;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public ArrayList getContentTypeList() {
		return contentTypeList;
	}

	public void setContentTypeList(ArrayList contentTypeList) {
		this.contentTypeList = contentTypeList;
	}

	public ArrayList getOrgFieldList() {
		return orgFieldList;
	}

	public void setOrgFieldList(ArrayList orgFieldList) {
		this.orgFieldList = orgFieldList;
	}

	public PaginationForm getOrgListform() {
		return orgListform;
	}

	public void setOrgListform(PaginationForm orgListform) {
		this.orgListform = orgListform;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentTypeValue() {
		return contentTypeValue;
	}

	public void setContentTypeValue(String contentTypeValue) {
		this.contentTypeValue = contentTypeValue;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public String getContentDisplay() {
		return contentDisplay;
	}

	public void setContentDisplay(String contentDisplay) {
		this.contentDisplay = contentDisplay;
	}

	public String getUrlDisplay() {
		return urlDisplay;
	}

	public void setUrlDisplay(String urlDisplay) {
		this.urlDisplay = urlDisplay;
	}

	public String getResumeFieldIds() {
		return resumeFieldIds;
	}

	public void setResumeFieldIds(String resumeFieldIds) {
		this.resumeFieldIds = resumeFieldIds;
	}

	public String getResumeFieldNames() {
		return resumeFieldNames;
	}

	public void setResumeFieldNames(String resumeFieldNames) {
		this.resumeFieldNames = resumeFieldNames;
	}

	public String getResumeStateFieldIds() {
		return resumeStateFieldIds;
	}

	public void setResumeStateFieldIds(String resumeStateFieldIds) {
		this.resumeStateFieldIds = resumeStateFieldIds;
	}

	public String getResumeStateFieldNames() {
		return resumeStateFieldNames;
	}

	public void setResumeStateFieldNames(String resumeStateFieldNames) {
		this.resumeStateFieldNames = resumeStateFieldNames;
	}

	public ArrayList getResumeFieldsList() {
		return resumeFieldsList;
	}

	public void setResumeFieldsList(ArrayList resumeFieldsList) {
		this.resumeFieldsList = resumeFieldsList;
	}

	public ArrayList getResumeStateFieldsList() {
		return resumeStateFieldsList;
	}

	public void setResumeStateFieldsList(ArrayList resumeStateFieldsList) {
		this.resumeStateFieldsList = resumeStateFieldsList;
	}

	public ArrayList getResumeFieldsSetList() {
		return resumeFieldsSetList;
	}

	public void setResumeFieldsSetList(ArrayList resumeFieldsSetList) {
		this.resumeFieldsSetList = resumeFieldsSetList;
	}

	public ArrayList getResumeStateFieldsSetList() {
		return resumeStateFieldsSetList;
	}

	public void setResumeStateFieldsSetList(ArrayList resumeStateFieldsSetList) {
		this.resumeStateFieldsSetList = resumeStateFieldsSetList;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getPersonTypeId() {
		return personTypeId;
	}

	public void setPersonTypeId(String personTypeId) {
		this.personTypeId = personTypeId;
	}

	public ArrayList getPersonTypeList() {
		return personTypeList;
	}

	public void setPersonTypeList(ArrayList personTypeList) {
		this.personTypeList = personTypeList;
	}

	public ArrayList getSelectedResumeFieldsList() {
		return selectedResumeFieldsList;
	}

	public void setSelectedResumeFieldsList(ArrayList selectedResumeFieldsList) {
		this.selectedResumeFieldsList = selectedResumeFieldsList;
	}

	public String getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getSelectedRSFieldsList() {
		return selectedRSFieldsList;
	}

	public void setSelectedRSFieldsList(ArrayList selectedRSFieldsList) {
		this.selectedRSFieldsList = selectedRSFieldsList;
	}

	public ArrayList getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList selectedList) {
		this.selectedList = selectedList;
	}

	public ArrayList getResumeLevelFieldList() {
		return resumeLevelFieldList;
	}

	public void setResumeLevelFieldList(ArrayList resumeLevelFieldList) {
		this.resumeLevelFieldList = resumeLevelFieldList;
	}

	public String getResumeLevelIds() {
		return resumeLevelIds;
	}

	public void setResumeLevelIds(String resumeLevelIds) {
		this.resumeLevelIds = resumeLevelIds;
	}

	public ArrayList getResumeStaticFieldsList() {
		return resumeStaticFieldsList;
	}

	public void setResumeStaticFieldsList(ArrayList resumeStaticFieldsList) {
		this.resumeStaticFieldsList = resumeStaticFieldsList;
	}

	public String getResumeStaticIds() {
		return resumeStaticIds;
	}

	public void setResumeStaticIds(String resumeStaticIds) {
		this.resumeStaticIds = resumeStaticIds;
	}

	public String getResumeStaticNames() {
		return resumeStaticNames;
	}

	public void setResumeStaticNames(String resumeStaticNames) {
		this.resumeStaticNames = resumeStaticNames;
	}

	public ArrayList getResumeStaticFieldsSetList() {
		return resumeStaticFieldsSetList;
	}

	public void setResumeStaticFieldsSetList(ArrayList resumeStaticFieldsSetList) {
		this.resumeStaticFieldsSetList = resumeStaticFieldsSetList;
	}

	public ArrayList getSelectedStaticFieldsList() {
		return selectedStaticFieldsList;
	}

	public void setSelectedStaticFieldsList(ArrayList selectedStaticFieldsList) {
		this.selectedStaticFieldsList = selectedStaticFieldsList;
	}

	public String getStaticfieldsetid() {
		return staticfieldsetid;
	}

	public void setStaticfieldsetid(String staticfieldsetid) {
		this.staticfieldsetid = staticfieldsetid;
	}

	public String getStaticitemid() {
		return staticitemid;
	}

	public void setStaticitemid(String staticitemid) {
		this.staticitemid = staticitemid;
	}

	public String getHireObjectId() {
		return hireObjectId;
	}

	public void setHireObjectId(String hireObjectId) {
		this.hireObjectId = hireObjectId;
	}

	public ArrayList getHireObjectParameterList() {
		return hireObjectParameterList;
	}

	public void setHireObjectParameterList(ArrayList hireObjectParameterList) {
		this.hireObjectParameterList = hireObjectParameterList;
	}

	public String getMark_type() {
		return mark_type;
	}

	public void setMark_type(String mark_type) {
		this.mark_type = mark_type;
	}

	public ArrayList getMarkList() {
		return markList;
	}

	public void setMarkList(ArrayList markList) {
		this.markList = markList;
	}

	public ArrayList getContentTList() {
		return contentTList;
	}

	public void setContentTList(ArrayList contentTList) {
		this.contentTList = contentTList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsClose() {
		return isClose;
	}

	public void setIsClose(String isClose) {
		this.isClose = isClose;
	}

	public String getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(String isVisible) {
		this.isVisible = isVisible;
	}

	public String getMax_count() {
		return max_count;
	}

	public void setMax_count(String max_count) {
		this.max_count = max_count;
	}

	public String getPreviewTableId() {
		return previewTableId;
	}

	public void setPreviewTableId(String previewTableId) {
		this.previewTableId = previewTableId;
	}

	public ArrayList getPreviewTableList() {
		return previewTableList;
	}

	public void setPreviewTableList(ArrayList previewTableList) {
		this.previewTableList = previewTableList;
	}

	public ArrayList getCommonQueryCondlist() {
		return commonQueryCondlist;
	}

	public void setCommonQueryCondlist(ArrayList commonQueryCondlist) {
		this.commonQueryCondlist = commonQueryCondlist;
	}

	public String getCommonQueryIds() {
		return commonQueryIds;
	}

	public void setCommonQueryIds(String commonQueryIds) {
		this.commonQueryIds = commonQueryIds;
	}

	public String getCommonQueryNames() {
		return commonQueryNames;
	}

	public void setCommonQueryNames(String commonQueryNames) {
		this.commonQueryNames = commonQueryNames;
	}

	public ArrayList getSelectedCommonQuery() {
		return selectedCommonQuery;
	}

	public void setSelectedCommonQuery(ArrayList selectedCommonQuery) {
		this.selectedCommonQuery = selectedCommonQuery;
	}

	public String getExplaination() {
		return explaination;
	}

	public void setExplaination(String explaination) {
		this.explaination = explaination;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getHiddenOrgId() {
		return hiddenOrgId;
	}

	public void setHiddenOrgId(String hiddenOrgId) {
		this.hiddenOrgId = hiddenOrgId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FormFile getR_file() {
		return r_file;
	}

	public void setR_file(FormFile r_file) {
		this.r_file = r_file;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getBusinessTemplateIds() {
		return businessTemplateIds;
	}

	public void setBusinessTemplateIds(String businessTemplateIds) {
		this.businessTemplateIds = businessTemplateIds;
	}

	public String getBusinessTemplatenames() {
		return businessTemplatenames;
	}

	public void setBusinessTemplatenames(String businessTemplatenames) {
		this.businessTemplatenames = businessTemplatenames;
	}

	public String getResumeCodeValue() {
		return resumeCodeValue;
	}

	public void setResumeCodeValue(String resumeCodeValue) {
		this.resumeCodeValue = resumeCodeValue;
	}

	public String getResumeCodeName() {
		return resumeCodeName;
	}

	public void setResumeCodeName(String resumeCodeName) {
		this.resumeCodeName = resumeCodeName;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getLicenseAgreement() {
		return licenseAgreement;
	}

	public void setLicenseAgreement(String licenseAgreement) {
		this.licenseAgreement = licenseAgreement;
	}

	public String getIsAgree() {
		return isAgree;
	}

	public void setIsAgree(String isAgree) {
		this.isAgree = isAgree;
	}

	public String getCultureCode() {
		return cultureCode;
	}

	public void setCultureCode(String cultureCode) {
		this.cultureCode = cultureCode;
	}

	public String getCultureCodeItem() {
		return cultureCodeItem;
	}

	public void setCultureCodeItem(String cultureCodeItem) {
		this.cultureCodeItem = cultureCodeItem;
	}

	public ArrayList getCultureList() {
		return cultureList;
	}

	public void setCultureList(ArrayList cultureList) {
		this.cultureList = cultureList;
	}

	public String getIsDefinitionCulture() {
		return isDefinitionCulture;
	}

	public void setIsDefinitionCulture(String isDefinitionCulture) {
		this.isDefinitionCulture = isDefinitionCulture;
	}

	public ArrayList getCultureCodeList() {
		return cultureCodeList;
	}

	public void setCultureCodeList(ArrayList cultureCodeList) {
		this.cultureCodeList = cultureCodeList;
	}

	public String getLicenseAgreementParameter() {
		return licenseAgreementParameter;
	}

	public void setLicenseAgreementParameter(String licenseAgreementParameter) {
		this.licenseAgreementParameter = licenseAgreementParameter;
	}

	public String getNetHref() {
		return netHref;
	}

	public void setNetHref(String netHref) {
		this.netHref = netHref;
	}

	public ArrayList getTableList() {
		return tableList;
	}

	public void setTableList(ArrayList tableList) {
		this.tableList = tableList;
	}

	public String getTableNames() {
		return tableNames;
	}

	public void setTableNames(String tableNames) {
		this.tableNames = tableNames;
	}

	public ArrayList getFieldSetList() {
		return fieldSetList;
	}

	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}

	public String getTableListSize() {
		return tableListSize;
	}

	public void setTableListSize(String tableListSize) {
		this.tableListSize = tableListSize;
	}

	public String getFieldSetListSize() {
		return fieldSetListSize;
	}

	public void setFieldSetListSize(String fieldSetListSize) {
		this.fieldSetListSize = fieldSetListSize;
	}

	public ArrayList getInterviewingRevertItemList() {
		return interviewingRevertItemList;
	}

	public void setInterviewingRevertItemList(ArrayList interviewingRevertItemList) {
		this.interviewingRevertItemList = interviewingRevertItemList;
	}

	public String getInterviewingRevertItemid() {
		return interviewingRevertItemid;
	}

	public void setInterviewingRevertItemid(String interviewingRevertItemid) {
		this.interviewingRevertItemid = interviewingRevertItemid;
	}

	public String getIsCtrlReportBZ() {
		return isCtrlReportBZ;
	}

	public void setIsCtrlReportBZ(String isCtrlReportBZ) {
		this.isCtrlReportBZ = isCtrlReportBZ;
	}

	public String getIsCtrlReportGZ() {
		return isCtrlReportGZ;
	}

	public void setIsCtrlReportGZ(String isCtrlReportGZ) {
		this.isCtrlReportGZ = isCtrlReportGZ;
	}

	public String getPositionSalaryStandardItem() {
		return positionSalaryStandardItem;
	}

	public void setPositionSalaryStandardItem(String positionSalaryStandardItem) {
		this.positionSalaryStandardItem = positionSalaryStandardItem;
	}

	public ArrayList getPositionSalaryStandardItemList() {
		return positionSalaryStandardItemList;
	}

	public void setPositionSalaryStandardItemList(
			ArrayList positionSalaryStandardItemList) {
		this.positionSalaryStandardItemList = positionSalaryStandardItemList;
	}

	public String getOrgWillTableId() {
		return orgWillTableId;
	}

	public void setOrgWillTableId(String orgWillTableId) {
		this.orgWillTableId = orgWillTableId;
	}

	public ArrayList getOrgWillTableList() {
		return orgWillTableList;
	}

	public void setOrgWillTableList(ArrayList orgWillTableList) {
		this.orgWillTableList = orgWillTableList;
	}

	public String getIsRemenberExamine() {
		return isRemenberExamine;
	}

	public void setIsRemenberExamine(String isRemenberExamine) {
		this.isRemenberExamine = isRemenberExamine;
	}

	public String getRemenberExamineSet() {
		return remenberExamineSet;
	}

	public void setRemenberExamineSet(String remenberExamineSet) {
		this.remenberExamineSet = remenberExamineSet;
	}

	public ArrayList getRemenberExamineSetList() {
		return remenberExamineSetList;
	}

	public void setRemenberExamineSetList(ArrayList remenberExamineSetList) {
		this.remenberExamineSetList = remenberExamineSetList;
	}

	public String getMoreLevelSP() {
		return moreLevelSP;
	}

	public void setMoreLevelSP(String moreLevelSP) {
		this.moreLevelSP = moreLevelSP;
	}

	public String getHirePositionNotUnionOrg() {
		return hirePositionNotUnionOrg;
	}

	public void setHirePositionNotUnionOrg(String hirePositionNotUnionOrg) {
		this.hirePositionNotUnionOrg = hirePositionNotUnionOrg;
	}

	public String getHirePositionItem() {
		return hirePositionItem;
	}

	public void setHirePositionItem(String hirePositionItem) {
		this.hirePositionItem = hirePositionItem;
	}

	public ArrayList getHirePositionItemList() {
		return hirePositionItemList;
	}

	public void setHirePositionItemList(ArrayList hirePositionItemList) {
		this.hirePositionItemList = hirePositionItemList;
	}

	public String getTitleField() {
		return titleField;
	}

	public void setTitleField(String titleField) {
		this.titleField = titleField;
	}

	public ArrayList getTitleFieldList() {
		return titleFieldList;
	}

	public void setTitleFieldList(ArrayList titleFieldList) {
		this.titleFieldList = titleFieldList;
	}

	public String getContentField() {
		return contentField;
	}

	public void setContentField(String contentField) {
		this.contentField = contentField;
	}

	public ArrayList getContentFieldList() {
		return contentFieldList;
	}

	public void setContentFieldList(ArrayList contentFieldList) {
		this.contentFieldList = contentFieldList;
	}

	public String getLevelField() {
		return levelField;
	}

	public void setLevelField(String levelField) {
		this.levelField = levelField;
	}

	public ArrayList getLevelFieldList() {
		return levelFieldList;
	}

	public void setLevelFieldList(ArrayList levelFieldList) {
		this.levelFieldList = levelFieldList;
	}

	public String getCommentDateField() {
		return commentDateField;
	}

	public void setCommentDateField(String commentDateField) {
		this.commentDateField = commentDateField;
	}

	public ArrayList getCommentDateFieldList() {
		return commentDateFieldList;
	}

	public void setCommentDateFieldList(ArrayList commentDateFieldList) {
		this.commentDateFieldList = commentDateFieldList;
	}

	public String getCommentUserField() {
		return commentUserField;
	}

	public void setCommentUserField(String commentUserField) {
		this.commentUserField = commentUserField;
	}

	public ArrayList getCommentUserFieldList() {
		return commentUserFieldList;
	}

	public void setCommentUserFieldList(ArrayList commentUserFieldList) {
		this.commentUserFieldList = commentUserFieldList;
	}

	public String getActiveField() {
		return activeField;
	}

	public void setActiveField(String activeField) {
		this.activeField = activeField;
	}

	public ArrayList getActiveFieldList() {
		return activeFieldList;
	}

	public void setActiveFieldList(ArrayList activeFieldList) {
		this.activeFieldList = activeFieldList;
	}

	public String getAdmissionCard() {
		return admissionCard;
	}

	public void setAdmissionCard(String admissionCard) {
		this.admissionCard = admissionCard;
	}

	public String getPositionNumber() {
		return positionNumber;
	}

	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}

	public String getPromptContent() {
		return promptContent;
	}

	public void setPromptContent(String promptContent) {
		this.promptContent = promptContent;
	}

	public String getL_p_type() {
		return l_p_type;
	}

	public void setL_p_type(String l_p_type) {
		this.l_p_type = l_p_type;
	}

	public String getPromptContentParameter() {
		return promptContentParameter;
	}

	public void setPromptContentParameter(String promptContentParameter) {
		this.promptContentParameter = promptContentParameter;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

	public ArrayList getSchoolPositionList() {
		return schoolPositionList;
	}

	public void setSchoolPositionList(ArrayList schoolPositionList) {
		this.schoolPositionList = schoolPositionList;
	}

	public String getWorkExperience() {
		return workExperience;
	}

	public void setWorkExperience(String workExperience) {
		this.workExperience = workExperience;
	}

	public ArrayList getWorkExperienceList() {
		return workExperienceList;
	}

	public void setWorkExperienceList(ArrayList workExperienceList) {
		this.workExperienceList = workExperienceList;
	}

	public ArrayList getHireMajorList() {
		return hireMajorList;
	}

	public void setHireMajorList(ArrayList hireMajorList) {
		this.hireMajorList = hireMajorList;
	}

	public String getHireMajor() {
		return hireMajor;
	}

	public void setHireMajor(String hireMajor) {
		this.hireMajor = hireMajor;
	}

	public String getAnswerSet() {
		return answerSet;
	}

	public void setAnswerSet(String answerSet) {
		this.answerSet = answerSet;
	}

	public ArrayList getAnswerSetList() {
		return answerSetList;
	}

	public void setAnswerSetList(ArrayList answerSetList) {
		this.answerSetList = answerSetList;
	}

	public String getPos_listfield() {
		return pos_listfield;
	}

	public void setPos_listfield(String pos_listfield) {
		this.pos_listfield = pos_listfield;
	}

	public String getPos_listfieldNames() {
		return pos_listfieldNames;
	}

	public void setPos_listfieldNames(String pos_listfieldNames) {
		this.pos_listfieldNames = pos_listfieldNames;
	}

	public String getSchoolPositionOrg() {
		return schoolPositionOrg;
	}

	public void setSchoolPositionOrg(String schoolPositionOrg) {
		this.schoolPositionOrg = schoolPositionOrg;
	}

	public String getSchoolPositionOrgDesc() {
		return schoolPositionOrgDesc;
	}

	public void setSchoolPositionOrgDesc(String schoolPositionOrgDesc) {
		this.schoolPositionOrgDesc = schoolPositionOrgDesc;
	}

	public String getSchoolPositionDesc() {
		return schoolPositionDesc;
	}

	public void setSchoolPositionDesc(String schoolPositionDesc) {
		this.schoolPositionDesc = schoolPositionDesc;
	}

	public String getSchoolPositionId() {
		return schoolPositionId;
	}

	public void setSchoolPositionId(String schoolPositionId) {
		this.schoolPositionId = schoolPositionId;
	}

	public String getOldID() {
		return oldID;
	}

	public void setOldID(String oldID) {
		this.oldID = oldID;
	}

	public String getSchoolPosDesc() {
		return schoolPosDesc;
	}

	public void setSchoolPosDesc(String schoolPosDesc) {
		this.schoolPosDesc = schoolPosDesc;
	}

	public String getNewTime() {
		return newTime;
	}

	public void setNewTime(String newTime) {
		this.newTime = newTime;
	}

	public String getIsCharField() {
		return isCharField;
	}

	public void setIsCharField(String isCharField) {
		this.isCharField = isCharField;
	}

	public ArrayList getHireMajorCodeList() {
		return hireMajorCodeList;
	}

	public void setHireMajorCodeList(ArrayList hireMajorCodeList) {
		this.hireMajorCodeList = hireMajorCodeList;
	}

	public String getHireMajorCode() {
		return hireMajorCode;
	}

	public void setHireMajorCode(String hireMajorCode) {
		this.hireMajorCode = hireMajorCode;
	}

	public String getStartResumeAnalysis() {
		return startResumeAnalysis;
	}

	public void setStartResumeAnalysis(String startResumeAnalysis) {
		this.startResumeAnalysis = startResumeAnalysis;
	}

	public String getResumeAnalysisName() {
		return resumeAnalysisName;
	}

	public void setResumeAnalysisName(String resumeAnalysisName) {
		this.resumeAnalysisName = resumeAnalysisName;
	}

	public String getResumeAnalysisPassword() {
		return resumeAnalysisPassword;
	}

	public void setResumeAnalysisPassword(String resumeAnalysisPassword) {
		this.resumeAnalysisPassword = resumeAnalysisPassword;
	}

	public String getResumeAnalysisForeignJob() {
		return resumeAnalysisForeignJob;
	}

	public void setResumeAnalysisForeignJob(String resumeAnalysisForeignJob) {
		this.resumeAnalysisForeignJob = resumeAnalysisForeignJob;
	}

	public ArrayList getForeignJobList() {
		return foreignJobList;
	}

	public void setForeignJobList(ArrayList foreignJobList) {
		this.foreignJobList = foreignJobList;
	}

	public ArrayList getApprovelist() {
		return approvelist;
	}

	public void setApprovelist(ArrayList approvelist) {
		this.approvelist = approvelist;
	}

	public String getSpRelation() {
		return spRelation;
	}

	public void setSpRelation(String spRelation) {
		this.spRelation = spRelation;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPersonTypeListJson() {
		return personTypeListJson;
	}

	public void setPersonTypeListJson(String personTypeListJson) {
		this.personTypeListJson = personTypeListJson;
	}

	public String getResumeStateFieldListJson() {
		return resumeStateFieldListJson;
	}

	public void setResumeStateFieldListJson(String resumeStateFieldListJson) {
		this.resumeStateFieldListJson = resumeStateFieldListJson;
	}

	public String getHireObjectParameterListJson() {
		return hireObjectParameterListJson;
	}

	public void setHireObjectParameterListJson(String hireObjectParameterListJson) {
		this.hireObjectParameterListJson = hireObjectParameterListJson;
	}

	public String getActiveFieldListJson() {
		return activeFieldListJson;
	}

	public void setActiveFieldListJson(String activeFieldListJson) {
		this.activeFieldListJson = activeFieldListJson;
	}

	public String getHireMajorCodeListJson() {
		return hireMajorCodeListJson;
	}

	public void setHireMajorCodeListJson(String hireMajorCodeListJson) {
		this.hireMajorCodeListJson = hireMajorCodeListJson;
	}

	public String getPreviewTableListJson() {
		return previewTableListJson;
	}

	public void setPreviewTableListJson(String previewTableListJson) {
		this.previewTableListJson = previewTableListJson;
	}

	public String getWorkExperienceListJson() {
		return workExperienceListJson;
	}

	public void setWorkExperienceListJson(String workExperienceListJson) {
		this.workExperienceListJson = workExperienceListJson;
	}

	public String getAnswerSetListJson() {
		return answerSetListJson;
	}

	public void setAnswerSetListJson(String answerSetListJson) {
		this.answerSetListJson = answerSetListJson;
	}

	public String getResumeLevelFieldListJson() {
		return resumeLevelFieldListJson;
	}

	public void setResumeLevelFieldListJson(String resumeLevelFieldListJson) {
		this.resumeLevelFieldListJson = resumeLevelFieldListJson;
	}

	public String getCultureListJson() {
		return cultureListJson;
	}

	public void setCultureListJson(String cultureListJson) {
		this.cultureListJson = cultureListJson;
	}

	public String getJob_str() {
		return job_str;
	}

	public void setJob_str(String jobstr) {
		job_str = jobstr;
	}
	
	public String getScript_str() {
		return script_str;
	}

	public void setScript_str(String scriptStr) {
		script_str = scriptStr;
	}

	public String[] getFunc() {
		return func;
	}

	public void setFunc(String[] func) {
		this.func = func;
	}

	public String getPersonStore() {
		return personStore;
	}

	public void setPersonStore(String personStore) {
		this.personStore = personStore;
	}

	public String getStr_sql() {
		return str_sql;
	}

	public void setStr_sql(String strSql) {
		str_sql = strSql;
	}

	public ArrayList getColumns() {
		return columns;
	}

	public void setColumns(ArrayList columns) {
		this.columns = columns;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public String getOrderbystr() {
		return orderbystr;
	}

	public void setOrderbystr(String orderbystr) {
		this.orderbystr = orderbystr;
	}

	public String getContentJson() {
		return contentJson;
	}

	public void setContentJson(String contentJson) {
		this.contentJson = contentJson;
	}

	public String getContentTypeListJson() {
		return contentTypeListJson;
	}

	public void setContentTypeListJson(String contentTypeListJson) {
		this.contentTypeListJson = contentTypeListJson;
	}

	public String getOrgFieldListJson() {
		return orgFieldListJson;
	}

	public void setOrgFieldListJson(String orgFieldListJson) {
		this.orgFieldListJson = orgFieldListJson;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getXuj() {
		return xuj;
	}

	public void setXuj(String xuj) {
		this.xuj = xuj;
	}

    public String getScoreCard() {
        return scoreCard;
    }

    public void setScoreCard(String scoreCard) {
        this.scoreCard = scoreCard;
    }

	public void setSchoolCard(String schoolCard) {
		this.schoolCard = schoolCard;
	}

	public String getSchoolCard() {
		return schoolCard;
	}

	public void setSocialCard(String socialCard) {
		this.socialCard = socialCard;
	}

	public String getSocialCard() {
		return socialCard;
	}

	public void setAppliedPosItems(String appliedPosItems) {
		this.appliedPosItems = appliedPosItems;
	}

	public String getAppliedPosItems() {
		return appliedPosItems;
	}

    public String getUnitLevel() {
        return unitLevel;
    }

    public void setUnitLevel(String unitLevel) {
        this.unitLevel = unitLevel;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

	public String getHirePostByLayerH() {
		return hirePostByLayerH;
	}

	public void setHirePostByLayerH(String hirePostByLayerH) {
		this.hirePostByLayerH = hirePostByLayerH;
	}

	public String getAcountBeActivedH() {
		return acountBeActivedH;
	}

	public void setAcountBeActivedH(String acountBeActivedH) {
		this.acountBeActivedH = acountBeActivedH;
	}

	public String getAttachH() {
		return attachH;
	}

	public void setAttachH(String attachH) {
		this.attachH = attachH;
	}

	public String getExplainationH() {
		return explainationH;
	}

	public void setExplainationH(String explainationH) {
		this.explainationH = explainationH;
	}

	public String getPhotoH() {
		return photoH;
	}

	public void setPhotoH(String photoH) {
		this.photoH = photoH;
	}

	public String getComplexPasswordH() {
		return complexPasswordH;
	}

	public void setComplexPasswordH(String complexPasswordH) {
		this.complexPasswordH = complexPasswordH;
	}

	public String getSelectValue() {
		return selectValue;
	}

	public void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
	}

	public String getNameValue() {
		return nameValue;
	}

	public void setNameValue(String nameValue) {
		this.nameValue = nameValue;
	}

	public String getCardItemIds() {
		return cardItemIds;
	}

	public void setCardItemIds(String cardItemIds) {
		this.cardItemIds = cardItemIds;
	}

	public String getAllItemsId() {
		return allItemsId;
	}

	public void setAllItemsId(String allItemsId) {
		this.allItemsId = allItemsId;
	}

	public String getAttachCodeset() {
        return attachCodeset;
    }

    public void setAttachCodeset(String attachCodeset) {
        this.attachCodeset = attachCodeset;
    }
    
    public String getAttachHire() {
        return attachHire;
    }

    public void setAttachHire(String attachHire) {
        this.attachHire = attachHire;
    }
    
    public String getHireChannelPriv() {
        return hireChannelPriv;
    }

    public void setHireChannelPriv(String hireChannelPriv) {
        this.hireChannelPriv = hireChannelPriv;
    }

    public String getDestNbase() {
        return destNbase;
    }

    public void setDestNbase(String destNbase) {
        this.destNbase = destNbase;
    }
    
}
