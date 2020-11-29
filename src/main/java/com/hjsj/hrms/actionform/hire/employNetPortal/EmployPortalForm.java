package com.hjsj.hrms.actionform.hire.employNetPortal;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 招聘管理外网入口
 * @author dengcan
 *
 */
public class EmployPortalForm extends FrameForm {
    private String    info="";   //企业介绍内容
    private String    stemp="";
    private String    birthdayName="";
    private String    ageName="";
    private String    axName="";
    private String    person_type="";  // 0:应聘 1：人才库
    private ArrayList unitList=new ArrayList();
    private HashMap   unitPosMap=new HashMap();
    private ArrayList conditionFieldList=new ArrayList();
    private String    isQueryCondition="0";    //是否有查询条件 0:没有 1:有
    
    private String    i9999="0";
    private String    posID="";                             //当前申请职位id
    private String    a0100="";    
    private String    dbName="";                             //应用库
    private String    userName="";                           //用户真实姓名
    private ArrayList posDescFiledList=new ArrayList();  //职位详细信息 指标列表
    private ArrayList applyedPosList=new ArrayList();    //已申请的职位信息列表
    private String    isApplyedPos="0";                  //是否已经申请了某职位  0：未申请 1：已申请
    private String    loginName="";                      //登陆邮箱
    private String    password="";                       //登陆密码
    private String    isPhoto="";                       //是否上传照片  1:有 0：无
    private String    isUpPhoto="1";                    //是否必须上传照片
    private String    isExp="0";                        //显示指标描述
    private String    isAttach="0";                     //上传简历附件，学位证书等
    private String    insideFlag;                       //内部招聘和外部招聘标志=0内部,=1外部
    private ArrayList mediaList = new ArrayList();      //多媒体类别列表
    private ArrayList uploadFileList = new ArrayList();  //附件文件列表
    private String    mediaId;                          //多媒体类别id
    private String opt;
    private String    emailColumn="";                    //电子邮箱子标
    
    private String    txtEmail="";                       //注册邮箱
    private String    pwd1="";                           //注册密码
    private String    txtName="";                        //注册姓名
    
    private ArrayList  resumeFieldList=new ArrayList();   //简历filedList
    private ArrayList  fieldSetList=new ArrayList();      //指标集
    private ArrayList  fieldSetMustList=new ArrayList();      //具有必填项的指标集
    private HashMap    fieldMap=new HashMap();
    private String        currentSetID="0";               //指标集坐标
    private String     flag="1";                          // 1:保存并添加  0：修改 2:保存走下一步
    private FormFile file;                                //照片
//    private FormFile attachFile;                          //简历附件
    private ArrayList showFieldList=new ArrayList(); //前台简历子集 列表需显示的 列指标 集合
    private ArrayList showFieldDataList=new ArrayList(); 
    
    private HashMap   resumeBrowseSetMap=new HashMap();   //应聘者各子集里的信息集合
    private HashMap   setShowFieldMap=new HashMap();      //子集显示 列 map
    
    private String    masterName="";                      //雇主单位名称
    
    private ArrayList zpPosList=new ArrayList();
    private String    zpPosID="";
    private String    cardid=""; //系统身份证指标
    
	private ArrayList remarkList=new ArrayList();        //评语信息列表
    private String previewTableId;//登记表id；
    /**简历是否可修改*/
    private String writeable="0";  ///0可修改  1不可修改
    /**唯一性校验指标串*/
    private String onlyField;
    /**是否要唯一性校验*/
    private String isOnlyCheck="0";
    private ArrayList resumeStateList=new ArrayList();
    /**许可协议串*/
    private String isDefinitinn;
    private String licenseAgreement;
    /**是否同意*/
    private String isAgree;
    /**培养方式代码值(定义可以通过的值)*/
    private String cultureCodeItem;
    private String hiddenCode;
    /**代码项列表*/
    private ArrayList cultureList = new ArrayList();
    /**是否定义培养方式参数*/
    private String isDefinitionCulture;
    private String netHref;
     /*this.getFormHM().put("interviewingCodeValue",value);
       this.getFormHM().put("interviewingRevertItemCodeList", EmployNetPortalBo.interviewingRevertItemCodeList);
       this.getFormHM().put("interviewingRevertItemid",EmployNetPortalBo.interviewingRevertItemid);
*/
    private String interviewingCodeValue;
    private String interviewingRevertItemid;
    private ArrayList interviewingRevertItemCodeList=new ArrayList();
    /**简历状态=1激活状态=2关闭状态*/
    private String resumeActive;
    /**是否定义简历激活状态指标参数=1定义了参数=2没定义*/
    private String isDefinitionActive;
    /**所在单位*/
    private String belongUnit;
    /**简历状态=1激活=2关闭*/
    private String activeValue;
    /**是否设置查看职位说明书参数=1设置=0没设置*/
    private String isConfigExp;
    /**该职位是否有职位说明书=1有=0没有*/
    private String isHaveExp;
    private String positionID;
    /**在简历不可编辑状态下，是否有特殊（可编辑指标）*/
    private String isHaveEditableField;
    /**特殊指标集合*/
    private HashMap editableMap = new HashMap();
    private String admissionCard;//准考证登记表
    private String blackField;//黑名单指标
    private String blackFieldDesc;
    private String blackFieldValue;
    private String blackNbase;//黑名单库
    private String max_count;//简历最大申请数
    private String blackFieldSize;
    private String canPrint;
    private String canPrintExamno;//打印准考证
    private String canQueryScore; //是否可以查询成绩
    private String positionNumber;//外网每个单位下显示职位条数
    private String promptContent;//外网登录框下显示的提示内容
    private String isPrompt;
    private String hireChannel;//是否有招聘渠道参数传递，如果有，按招聘渠道区分
    /**工作经验指标*/
    private String isDefineWorkExperience;//=1设置=0未设置
    private String workExperience;
    private String workExperienceDesc;
    /**工作经验指标关联的代码列表*/
    private ArrayList workExperienceCodeList = new ArrayList();
    /**招聘专业*/
    private String hireMajor;
    private String hireMajorCode;
    private String answerSet;
    /**判断外网用黑名单指标还是用唯一性指标（主要是当二指标相同时候用）*/
    private String paramFlag;
    private String onlyName;
    private String onlyNameDesc;
    private String onlyValue;
    private String onlySize;
    private String hasXiaoYuan;
    private ArrayList runHeaderList = new ArrayList();
    private ArrayList runDataList = new ArrayList();
    private ArrayList posFieldList = new ArrayList();
    private ArrayList id_type_List = new ArrayList();//证件类型待选指标值（招聘对象 AC号代码）
	private String dmlStatus="";
    private String chl_id="";
    private String selunit="";
    private String jobid="";
    private String corcode="";
    private String jobname="";
    private String cer="";
    private String isapply="";
    private String requireId="";//需求id（z0301）
    private String acountBeActived="";//是否需要邮箱激活=0不需要=1需要
    private String acountActivedValue="";
    private String url_addr="";//发送邮件的链接地址 
    private String url_addr40="";//发送邮件的链接地址 
    private String isResumePerfection="1";//判断简历完整性，
    private String complexPassword="";//是否使用复杂密码  
    private String passwordMinLength="";//密码最小长度 
    private String passwordMaxLength="";//密码最大长度 
    private String failedTime="";//最大失败次数
    private String unlockTime="";//解锁时间间隔
    private String appliedPosItems="";//外网已申请职位列表显示指标集
    private String extendFile="";//文件后缀名
    private String validateInfo="";//登录验证信息
    private String encryptA0100 ="";//为招聘外网增加参数,防止能看到其他人员的简历信息
    private String recommendZ0301 ="";//为职位推荐人员时被选中的发布职位的z0301
    private String recommendPosName="";//为职位推荐人员时被选中的发布职位的名称
    private String applyMessage="";//已申请职位消息
    private String candidate_status="";//应聘身份指标
    private String candidate_status_desc="";//应聘身份指标
    private String message;//=1：接受邀请
    private String id_type="";//证件类型指标
    private String id_type_desc;//证件类型指标名称
    private String codeId;;//证件类型代码类
    
    public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
	}
	
    
	public String getId_type_desc() {
		return id_type_desc;
	}

	public void setId_type_desc(String id_type_desc) {
		this.id_type_desc = id_type_desc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    public String getCandidate_status_desc() {
		return candidate_status_desc;
	}
	public void setCandidate_status_desc(String candidate_status_desc) {
		this.candidate_status_desc = candidate_status_desc;
	}
	private ArrayList candidate_status_List = new ArrayList();//应聘身份待选指标值（招聘对象 35号代码）
    public String getCandidate_status() {
		return candidate_status;
	}
	public void setCandidate_status(String candidate_status) {
		this.candidate_status = candidate_status;
	}
	public ArrayList getCandidate_status_List() {
		return candidate_status_List;
	}
	public void setCandidate_status_List(ArrayList candidate_status_List) {
		this.candidate_status_List = candidate_status_List;
	}
	
    public ArrayList getId_type_List() {
		return id_type_List;
	}

	public void setId_type_List(ArrayList id_type_List) {
		this.id_type_List = id_type_List;
	}
	
	private HashMap<Integer, FormFile> attachFiles = new HashMap<Integer, FormFile>();  // 用于保存不定数量的FormFile对象
    
	public HashMap<Integer, FormFile> getAttachFiles() {
		return attachFiles;
	}
	public void setAttachFiles(HashMap<Integer, FormFile> attachFiles) {
		this.attachFiles = attachFiles;
	}
	public FormFile getAttachFile(int i)  // 索引属性
    {
        return attachFiles.get(i);
    }
    public void setAttachFile(int i, FormFile myFile)  // 索引属性
    {
    	attachFiles.put(i, myFile);
    }
    
	public String getRecommendZ0301() {
        return recommendZ0301;
    }
    public void setRecommendZ0301(String recommendZ0301) {
        this.recommendZ0301 = recommendZ0301;
    }
    public String getRecommendPosName() {
        return recommendPosName;
    }
    public void setRecommendPosName(String recommendPosName) {
        this.recommendPosName = recommendPosName;
    }
    public String getEncryptA0100() {
        return encryptA0100;
    }
    public void setEncryptA0100(String encryptA0100) {
        this.encryptA0100 = encryptA0100;
    }
    public String getExtendFile() {
        return extendFile;
    }
    public void setExtendFile(String extendFile) {
        this.extendFile = extendFile;
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
    public String getIsResumePerfection() {
        return isResumePerfection;
    }
    public void setIsResumePerfection(String isResumePerfection) {
        this.isResumePerfection = isResumePerfection;
    }
    public String getUrl_addr() {
        return url_addr;
    }
    public void setUrl_addr(String url_addr) {
        this.url_addr = url_addr;
    }
    public String getAcountActivedValue() {
        return acountActivedValue;
    }
    public void setAcountActivedValue(String acountActivedValue) {
        this.acountActivedValue = acountActivedValue;
    }
    public String getAcountBeActived() {
        return acountBeActived;
    }
    public void setAcountBeActived(String acountBeActived) {
        this.acountBeActived = acountBeActived;
    }
    public String getJobid() {
        return jobid;
    }
    public void setJobid(String jobid) {
        this.jobid = jobid;
    }
    public String getCorcode() {
        return corcode;
    }
    public void setCorcode(String corcode) {
        this.corcode = corcode;
    }
    public String getJobname() {
        return jobname;
    }
    public void setJobname(String jobname) {
        this.jobname = jobname;
    }
    public String getSelunit() {
        return selunit;
    }
    public void setSelunit(String selunit) {
        this.selunit = selunit;
    }
    public String getChl_id() {
        return chl_id;
    }
    public void setChl_id(String chl_id) {
        this.chl_id = chl_id;
    }
    public String getDmlStatus() {
        return dmlStatus;
    }
    public void setDmlStatus(String dmlStatus) {
        this.dmlStatus = dmlStatus;
    }
    public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
    private String hasMessage;//是否定义了公告信息
    private String notice_message;//公告信息内容
    private String zpUnitCode;//招聘单位代码
    private String unitIntroduce;//单位介绍
    private String introduceType;//单位介绍形式
    private String introducelink;//单位介绍为连接时，链接内容
    private String isHasNewDate;//是否设置最新职位参数
    private String isAllPos;//显示所有职位还是显示最新职位
    private String sy_message;
    private String lfType;//logo文件类型，
    private String hbType;//首页背景图片文件类型
    private PaginationForm recommendUserListForm = new PaginationForm(); // 猎头推荐简历的循环Form
    private String recommendA0100s ="";//当前选中的要推荐的人员的A0100
    private String recommendUserNames = "";//当前选中的要推荐的人员姓名
    private String maxFileSize;
    //是否显示 忘记帐号 =false：不显示；=true：显示；默认为false 
    private String accountFlag;
    public String getRecommendA0100s() {
        return recommendA0100s;
    }
    public void setRecommendA0100s(String recommendA0100s) {
        this.recommendA0100s = recommendA0100s;
    }
    public String getRecommendUserNames() {
        return recommendUserNames;
    }
    public void setRecommendUserNames(String recommendUserNames) {
        this.recommendUserNames = recommendUserNames;
    }
    
    public PaginationForm getRecommendUserListForm() {
        return recommendUserListForm;
    }
    public void setRecommendUserListForm(PaginationForm recommendUserListForm) {
        this.recommendUserListForm = recommendUserListForm;
    }
    public ArrayList getRecommendUserList() {
        return recommendUserList;
    }
    public void setRecommendUserList(ArrayList recommendUserList) {
        this.recommendUserList = recommendUserList;
    }
    public ArrayList getRecommendTbaleList() {
        return recommendTbaleList;
    }
    public void setRecommendTbaleList(ArrayList recommendTbaleList) {
        this.recommendTbaleList = recommendTbaleList;
    }
    private String cms_chl_no;//频道号,菜单将显示该频道号的子频道
    private String menuType;
    private ArrayList commQueryList =new ArrayList();
    private ArrayList sunitlist=new ArrayList();
    private String posDesc="";
    private ArrayList boardlist=new ArrayList();//所有公告内容
    private ArrayList pageBoardList=new ArrayList();//分页公告内容
	private String hdtusername="";
    private ArrayList recommendUserList=new ArrayList();//用来存放推荐简历的基本信息
    private ArrayList recommendTbaleList=new ArrayList();//用来存放推荐简历基本信息列的表头
    private ArrayList publicityList=new ArrayList();//用于存放招聘公式的集合
    private int pageNum = 0;//公告页码
    private int pageCount = 0;//公告总页数
    public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
    public ArrayList getPageBoardList() {
    	return pageBoardList;
    }
    
    public void setPageBoardList(ArrayList pageBoardList) {
    	this.pageBoardList = pageBoardList;
    }
    public String getHdtusername() {
        return hdtusername;
    }
    public void setHdtusername(String hdtusername) {
        this.hdtusername = hdtusername;
    }
    @Override
    public void outPutFormHM() {
        this.recommendUserListForm.setList((ArrayList) this.getFormHM().get("recommendUserList"));
        this.recommendUserListForm.getPagination().gotoPage(this.recommendUserListForm.getPagination().getCurrent());
        this.setRecommendTbaleList((ArrayList) this.getFormHM().get("recommendTbaleList"));
        this.setRecommendUserList((ArrayList) this.getFormHM().get("recommendUserList"));
        
        this.setRecommendA0100s((String) this.getFormHM().get("recommendA0100s"));
        this.setRecommendUserNames((String) this.getFormHM().get("recommendUserNames"));
        this.setHdtusername((String)this.getFormHM().get("hdtusername"));
        this.setIsResumePerfection((String)this.getFormHM().get("isResumePerfection"));
        this.setAcountActivedValue((String)this.getFormHM().get("acountActivedValue"));
        this.setAcountBeActived((String)this.getFormHM().get("acountBeActived"));
        this.setPosDesc((String)this.getFormHM().get("posDesc"));
        this.setRequireId((String)this.getFormHM().get("requireId"));
        this.setCer((String)this.getFormHM().get("cer"));
        this.setIsapply((String)this.getFormHM().get("isapply"));
        this.setJobid((String)this.getFormHM().get("jobid"));
        this.setJobname((String)this.getFormHM().get("jobname"));
        this.setCorcode((String)this.getFormHM().get("corcode"));
        this.setSelunit((String)this.getFormHM().get("selunit"));
        this.setChl_id((String)this.getFormHM().get("chl_id"));
        this.setSunitlist((ArrayList)this.getFormHM().get("sunitlist"));
        this.setCommQueryList((ArrayList)this.getFormHM().get("commQueryList"));
        this.setMenuType((String)this.getFormHM().get("menuType"));
        this.setCms_chl_no((String)this.getFormHM().get("cms_chl_no"));
        this.setLfType((String)this.getFormHM().get("lfType"));
        this.setHbType((String)this.getFormHM().get("hbType"));
        this.setSy_message((String)this.getFormHM().get("sy_message"));
        this.setIsHasNewDate((String)this.getFormHM().get("isHasNewDate"));
        this.setIsAllPos((String)this.getFormHM().get("isAllPos"));
        this.setIntroducelink((String)this.getFormHM().get("introducelink"));
        this.setIntroduceType((String)this.getFormHM().get("introduceType"));
        this.setHasMessage((String)this.getFormHM().get("hasMessage"));
        this.setNotice_message((String)this.getFormHM().get("notice_message"));
        this.setZpUnitCode((String)this.getFormHM().get("zpUnitCode"));
        this.setUnitIntroduce((String)this.getFormHM().get("unitIntroduce"));
        this.setPosFieldList((ArrayList)this.getFormHM().get("posFieldList"));
        this.setRunDataList((ArrayList)this.getFormHM().get("runDataList"));
        this.setRunHeaderList((ArrayList)this.getFormHM().get("runHeaderList"));
        this.setHasXiaoYuan((String)this.getFormHM().get("hasXiaoYuan"));
        this.setOnlyValue((String)this.getFormHM().get("onlyValue"));
        this.setOnlySize((String)this.getFormHM().get("onlySize"));
        this.setOnlyNameDesc((String)this.getFormHM().get("onlyNameDesc"));
        this.setOnlyName((String)this.getFormHM().get("onlyName"));
        this.setParamFlag((String)this.getFormHM().get("paramFlag"));
        this.setHireMajor((String)this.getFormHM().get("hireMajor"));
        this.setHireMajorCode((String)this.getFormHM().get("hireMajorCode"));
        this.setIsDefineWorkExperience((String)this.getFormHM().get("isDefineWorkExperience"));
        this.setWorkExperience((String)this.getFormHM().get("workExperience"));
        this.setWorkExperienceDesc((String)this.getFormHM().get("workExperienceDesc"));
        this.setWorkExperienceCodeList((ArrayList)this.getFormHM().get("workExperienceCodeList"));
        this.setHireChannel((String)this.getFormHM().get("hireChannel"));
        this.setIsPrompt((String)this.getFormHM().get("isPrompt"));
        this.setPositionNumber((String)this.getFormHM().get("positionNumber"));
        this.setPromptContent((String)this.getFormHM().get("promptContent"));
        this.setCanPrint((String)this.getFormHM().get("canPrint"));
        this.setBlackFieldSize((String)this.getFormHM().get("blackFieldSize"));
        this.setBlackFieldValue((String)this.getFormHM().get("blackFieldValue"));
        this.setBlackFieldDesc((String)this.getFormHM().get("blackFieldDesc"));
        this.setMax_count((String)this.getFormHM().get("max_count"));
        this.setBlackNbase((String)this.getFormHM().get("blackNbase"));
        this.setBlackField((String)this.getFormHM().get("blackField"));
        this.setAdmissionCard((String)this.getFormHM().get("admissionCard"));
        this.setIsHaveEditableField((String)this.getFormHM().get("isHaveEditableField"));
        this.setEditableMap((HashMap)this.getFormHM().get("editableMap"));
        this.setPositionID((String)this.getFormHM().get("positionID"));
        this.setIsHaveExp((String)this.getFormHM().get("isHaveExp"));
        this.setIsConfigExp((String)this.getFormHM().get("isConfigExp"));
        this.setActiveValue((String)this.getFormHM().get("activeValue"));
        this.setBelongUnit((String)this.getFormHM().get("belongUnit"));
        this.setIsDefinitionActive((String)this.getFormHM().get("isDefinitionActive"));
        this.setResumeActive((String)this.getFormHM().get("resumeActive"));
        this.setInterviewingCodeValue((String)this.getFormHM().get("interviewingCodeValue"));
        this.setInterviewingRevertItemCodeList((ArrayList)this.getFormHM().get("interviewingRevertItemCodeList"));
        this.setInterviewingRevertItemid((String)this.getFormHM().get("interviewingRevertItemid"));
        this.setNetHref((String)this.getFormHM().get("netHref"));
        this.setHiddenCode((String)this.getFormHM().get("hiddenCode"));
        this.setIsDefinitionCulture((String)this.getFormHM().get("isDefinitionCulture"));
        this.setLicenseAgreement((String)this.getFormHM().get("licenseAgreement"));
        this.setIsAgree((String)this.getFormHM().get("isAgree"));
        this.setCultureCodeItem((String)this.getFormHM().get("cultureCodeItem"));
        this.setCultureList((ArrayList)this.getFormHM().get("cultureList"));
        this.setIsDefinitinn((String)this.getFormHM().get("isDefinitinn"));
        this.setResumeStateList((ArrayList)this.getFormHM().get("resumeStateList"));
        this.setIsOnlyCheck((String)this.getFormHM().get("isOnlyCheck"));
        this.setWriteable((String)this.getFormHM().get("writeable"));
        this.setOnlyField((String)this.getFormHM().get("onlyField"));
        this.setInsideFlag((String)this.getFormHM().get("insideFlag"));
        this.setOpt((String)this.getFormHM().get("opt"));
        this.setUploadFileList((ArrayList)this.getFormHM().get("uploadFileList"));
        this.setMediaList((ArrayList)this.getFormHM().get("mediaList"));
        this.setMediaId((String)this.getFormHM().get("mediaId"));
        this.setIsAttach((String)this.getFormHM().get("isAttach"));
        this.setIsExp((String)this.getFormHM().get("isExp"));
        this.setIsUpPhoto((String)this.getFormHM().get("isUpPhoto"));
        this.setPreviewTableId((String)this.getFormHM().get("previewTableId"));
        this.setIsPhoto((String)this.getFormHM().get("isPhoto"));
        this.setRemarkList((ArrayList)this.getFormHM().get("remarkList"));
        this.setInfo((String)this.getFormHM().get("info"));
        this.setPerson_type((String)this.getFormHM().get("person_type"));
        this.setCanPrintExamno((String) this.getFormHM().get("canPrintExamno"));
        
        this.setZpPosID((String)this.getFormHM().get("zpPosID"));
        this.setZpPosList((ArrayList)this.getFormHM().get("zpPosList"));
        
        this.setEmailColumn((String)this.getFormHM().get("emailColumn"));
        this.setResumeBrowseSetMap((HashMap)this.getFormHM().get("resumeBrowseSetMap"));
        this.setSetShowFieldMap((HashMap)this.getFormHM().get("setShowFieldMap"));
        this.setConditionFieldList((ArrayList)this.getFormHM().get("conditionFieldList"));
        this.setUnitList((ArrayList)this.getFormHM().get("unitList"));
        this.setUnitPosMap((HashMap)this.getFormHM().get("unitPosMap"));
        this.setIsQueryCondition(getIsQueryCondition((ArrayList)this.getFormHM().get("conditionFieldList")));
        this.setPosDescFiledList((ArrayList)this.getFormHM().get("posDescFiledList"));
        this.setApplyedPosList((ArrayList)this.getFormHM().get("applyedPosList"));
        this.setA0100((String)this.getFormHM().get("a0100"));
        this.setDbName((String)this.getFormHM().get("dbName"));
        this.setUserName((String)this.getFormHM().get("userName"));
        this.setPosID((String)this.getFormHM().get("posID"));
        this.setIsApplyedPos((String)this.getFormHM().get("isApplyedPos"));
        
        this.setI9999((String)this.getFormHM().get("i9999"));
        this.setFieldMap((HashMap)this.getFormHM().get("fieldMap"));
        this.setResumeFieldList((ArrayList)this.getFormHM().get("resumeFieldList"));        
        this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
        this.setFieldSetMustList((ArrayList)this.getFormHM().get("fieldSetMustList"));
        this.setCurrentSetID((String)this.getFormHM().get("currentSetID"));
        
        this.setShowFieldDataList((ArrayList)this.getFormHM().get("showFieldDataList"));
        this.setShowFieldList((ArrayList)this.getFormHM().get("showFieldList"));    
        this.setMasterName((String)this.getFormHM().get("masterName"));
        this.setAnswerSet((String)this.getFormHM().get("answerSet"));
        this.setDmlStatus((String)this.getFormHM().get("dmlStatus"));
        this.setBoardlist((ArrayList)this.getFormHM().get("boardlist"));
        this.setPageBoardList((ArrayList) this.getFormHM().get("pageBoardList"));
        this.setLoginName((String)this.getFormHM().get("loginName"));
        this.setPassword((String)this.getFormHM().get("password"));
        this.setComplexPassword((String)this.getFormHM().get("complexPassword"));
        this.setPasswordMinLength((String)this.getFormHM().get("passwordMinLength"));
        this.setPasswordMaxLength((String)this.getFormHM().get("passwordMaxLength"));
        this.setFailedTime((String)this.getFormHM().get("failedTime"));
        this.setUnlockTime((String)this.getFormHM().get("unlockTime"));
        this.setAppliedPosItems((String)this.getFormHM().get("appliedPosItems"));
        this.setExtendFile((String)this.getFormHM().get("extendFile"));
        this.setValidateInfo((String)this.getFormHM().get("validateInfo"));
        /**为招聘外网设置加密后的A0100,防止能看到其他人的信息**/
        this.setEncryptA0100((String) this.getFormHM().get("encryptA0100"));
        this.setRecommendZ0301((String) this.getFormHM().get("recommendZ0301"));
        this.setRecommendPosName((String) this.getFormHM().get("recommendPosName"));
        this.setCanQueryScore((String)this.getFormHM().get("canQueryScore"));
        this.setMaxFileSize((String)this.getFormHM().get("maxFileSize"));
        this.setAccountFlag((String)this.getFormHM().get("accountFlag"));
        this.setCardid((String) this.getFormHM().get("cardid"));
        this.setAttachFiles((HashMap<Integer, FormFile>) this.getFormHM().get("attachFiles"));
        this.setPublicityList((ArrayList)this.getFormHM().get("publicityList"));
        this.setPageNum((Integer) this.getFormHM().get("pageNum"));
        this.setPageCount((Integer) this.getFormHM().get("pageCount"));
        this.setCandidate_status((String) this.getFormHM().get("candidate_status"));
        this.setCandidate_status_List((ArrayList) this.getFormHM().get("candidate_status_List"));
        this.setCandidate_status_desc((String) this.getFormHM().get("candidate_status_desc"));
        this.setMessage((String) this.getFormHM().get("message"));
        this.setAgeName((String)this.getFormHM().get("ageName"));
        this.setAxName((String)this.getFormHM().get("axName"));
        this.setBirthdayName((String)this.getFormHM().get("birthdayName"));
        this.setId_type_desc((String)this.getFormHM().get("id_type_desc"));
        this.setId_type_List((ArrayList)this.getFormHM().get("id_type_List"));
        this.setId_type((String)this.getFormHM().get("id_type"));
        this.setCodeId((String)this.getFormHM().get("codeId"));
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        HttpSession session = arg1.getSession();

        if("/hire/interviewEvaluating/interviewRevert".equals(arg0.getPath())&&arg1.getParameter("b_interview")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            {
                this.getPagination().firstPage();   
            }
        }
        //非登录状态查询时 清空session信息
        if("/hire/hireNetPortal/search_zp_position".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null&&(this.getA0100()==null||"".equals(this.getA0100())))
        {
            HashMap map=new HashMap();
            Enumeration e=session.getAttributeNames();
            String name="";
            while(e.hasMoreElements()){
                name=(String) e.nextElement();
                map.put(name, session.getAttribute(name));
            }
            session.invalidate();
            session=arg1.getSession();
            for (Iterator i = map.keySet().iterator(); i.hasNext();) {
                Object key = i.next();
                Object value = map.get(key);
                if("employPortalForm".equals(key)){
                    session.setAttribute((String) key, value);
                }
                
            }
            
        }
        if("/hire/hireNetPortal/recommend_positionResume".equals(arg0.getPath())&&arg1.getParameter("b_recommendResume")!=null
                && "recommend".equals(arg1.getParameter("b_recommendResume"))){
            String z0301 = arg1.getParameter("z0301");
            String posName =arg1.getParameter("posName");
            this.setRecommendZ0301(z0301);
            this.setRecommendPosName(posName);
        }
        if("/hire/hireNetPortal/recommend_resume".equals(arg0.getPath())&&arg1.getParameter("b_recommendResume")!=null
                && "query".equals(arg1.getParameter("b_recommendResume"))){
            if(this.getRecommendUserListForm().getPagination()!=null){
                this.getRecommendUserListForm().getPagination().firstPage();
            }
        }
        //重复激活账号关闭页面
        if("/hire/hireNetPortal/search_zp_position".equals(arg0.getPath())&&arg1.getParameter("b_activecount")!=null)
        {
            arg1.setAttribute("targetWindow", "1");
        }
        this.getFormHM().put("session",session);
        return super.validate(arg0, arg1);
    }
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("hdtusername",this.getHdtusername());
        this.getFormHM().put("isResumePerfection",this.getIsResumePerfection());
        this.getFormHM().put("url_addr",this.getUrl_addr());
        this.getFormHM().put("url_addr40",this.getUrl_addr40());
        this.getFormHM().put("acountActivedValue", this.getAcountActivedValue());
        this.getFormHM().put("acountBeActived", this.getAcountBeActived());
        this.getFormHM().put("requireId", this.getRequireId());
        this.getFormHM().put("selunit", this.getSelunit());
        this.getFormHM().put("chl_id", this.getChl_id());
        this.getFormHM().put("commQueryList", this.getCommQueryList());
        this.getFormHM().put("menuType", this.getMenuType());
        this.getFormHM().put("cms_chl_no", this.getCms_chl_no());
        this.getFormHM().put("lfType", this.getLfType());
        this.getFormHM().put("hbType", this.getHbType());
        this.getFormHM().put("isHasNewDate", this.getIsHasNewDate());
        this.getFormHM().put("isAllPos", this.getIsAllPos());
        this.getFormHM().put("introducelink", this.getIntroducelink());
        this.getFormHM().put("introduceType", this.getIntroduceType());
        this.getFormHM().put("hasMessage", this.getHasMessage());
        this.getFormHM().put("notice_message", this.getNotice_message());
        this.getFormHM().put("zpUnitCode", this.getZpUnitCode());
        this.getFormHM().put("unitIntroduce", this.getUnitIntroduce());
        this.getFormHM().put("posFieldList", this.getPosFieldList());
        this.getFormHM().put("hasXiaoYuan", this.getHasXiaoYuan());
        this.getFormHM().put("onlyValue", this.getOnlyValue());
        this.getFormHM().put("onlyName", this.getOnlyName());
        this.getFormHM().put("paramFlag", this.getParamFlag());
        this.getFormHM().put("hireMajor", this.getHireMajor());
        this.getFormHM().put("hireMajorCode", this.getHireMajorCode());
        this.getFormHM().put("isDefineWorkExperience", this.getIsDefineWorkExperience());
        this.getFormHM().put("workExperience", this.getWorkExperience());
        this.getFormHM().put("workExperienceDesc", this.getWorkExperienceDesc());
        this.getFormHM().put("hireChannel", this.getHireChannel());
        this.getFormHM().put("isPrompt", this.getIsPrompt());
        this.getFormHM().put("promptContent", this.getPromptContent());
        this.getFormHM().put("positionNumber", this.getPositionNumber());
        this.getFormHM().put("canPrint", this.getCanPrint());
        this.getFormHM().put("blackFieldSize", this.getBlackFieldSize());
        this.getFormHM().put("blackFieldValue", this.getBlackFieldValue());
        this.getFormHM().put("blackFieldDesc", this.getBlackFieldDesc());
        this.getFormHM().put("max_count", this.getMax_count());
        this.getFormHM().put("blackNbase", this.getBlackNbase());
        this.getFormHM().put("blackField",this.getBlackField());
        this.getFormHM().put("admissionCard", this.getAdmissionCard());
        this.getFormHM().put("isHaveEditableField", this.getIsHaveEditableField());
        this.getFormHM().put("editableMap", this.getEditableMap());
        this.getFormHM().put("positionID", this.getPositionID());
        this.getFormHM().put("isHaveExp",this.getIsHaveExp());
        this.getFormHM().put("isConfigExp", this.getIsConfigExp());
        this.getFormHM().put("activeValue", this.getActiveValue());
        this.getFormHM().put("belongUnit", this.getBelongUnit());
        this.getFormHM().put("isDefinitionActive", this.getIsDefinitionActive());
        this.getFormHM().put("resumeActive", this.getResumeActive());
        this.getFormHM().put("interviewingCodeValue", this.getInterviewingCodeValue());
        this.getFormHM().put("interviewingRevertItemid", this.getInterviewingRevertItemid());
        this.getFormHM().put("netHref", this.getNetHref());
        this.getFormHM().put("resumeStateList", this.getResumeStateList());
        this.getFormHM().put("isOnlyCheck",this.getIsOnlyCheck());
        this.getFormHM().put("writeable", this.getWriteable());
        this.getFormHM().put("onlyField", this.getOnlyField());
        this.getFormHM().put("opt", this.getOpt());
//        this.getFormHM().put("attachFile",this.getAttachFile());
        this.getFormHM().put("mediaId", this.getMediaId());
        this.getFormHM().put("isAttach",this.getIsAttach());
        this.getFormHM().put("isPhoto",this.getIsPhoto());
        this.getFormHM().put("txtEmail",this.getTxtEmail());
        this.getFormHM().put("pwd1",this.getPwd1());
        this.getFormHM().put("txtName",this.getTxtName());
        this.getFormHM().put("currentSetID", this.getCurrentSetID());
        this.getFormHM().put("conditionFieldList",this.getConditionFieldList());
        this.getFormHM().put("loginName",this.getLoginName());
        this.getFormHM().put("password",this.getPassword());
        this.getFormHM().put("resumeFieldList",this.getResumeFieldList());
        this.getFormHM().put("file", this.getFile());
        this.getFormHM().put("flag",this.getFlag());
        this.getFormHM().put("insideFlag",this.getInsideFlag());
        this.getFormHM().put("dbName", this.getDbName());
        this.getFormHM().put("complexPassword", this.getComplexPassword());
        this.getFormHM().put("passwordMinLength", this.getPasswordMinLength());
        this.getFormHM().put("passwordMaxLength", this.getPasswordMaxLength());
        this.getFormHM().put("failedTime", this.getFailedTime());
        this.getFormHM().put("unlockTime", this.getUnlockTime());
        this.getFormHM().put("appliedPosItems",this.getAppliedPosItems());
        this.getFormHM().put("extendFile", this.getExtendFile());
        this.getFormHM().put("validateInfo", this.getValidateInfo());
        this.getFormHM().put("selectedlist",this.getRecommendUserListForm().getSelectedList());
        this.getFormHM().put("encryptA0100", this.getEncryptA0100());
        this.getFormHM().put("recommendZ0301", this.getRecommendZ0301());
        this.getFormHM().put("recommendPosName", this.getRecommendPosName());
        this.getFormHM().put("maxFileSize", this.getMaxFileSize());
        this.getFormHM().put("accountFlag", this.getAccountFlag());
        this.getFormHM().put("cardid", this.getCardid());
        this.getFormHM().put("attachFiles", this.getAttachFiles());
        this.getFormHM().put("candidate_status", this.getCandidate_status());
        this.getFormHM().put("candidate_status_List", this.getCandidate_status_List());
        this.getFormHM().put("candidate_status_desc", this.getCandidate_status_desc());
        this.getFormHM().put("message", this.getMessage());
        this.getFormHM().put("pageNum", this.getPageNum());
        this.getFormHM().put("pageCount", this.getPageCount());
        this.getFormHM().put("id_type_desc", this.getId_type_desc());
        this.getFormHM().put("id_type_List", this.getId_type_List());
        this.getFormHM().put("id_type", this.getId_type());
        this.getFormHM().put("codeId", this.getCodeId());
    }
    private String getIsQueryCondition(ArrayList conditionFieldList)
    {
        String a_isQueryCondition="0";
        for(int i=0;i<conditionFieldList.size();i++)
        {
            LazyDynaBean abean=(LazyDynaBean)conditionFieldList.get(i);
            String value=(String)abean.get("value");
            if(value!=null&&value.trim().length()>0)
            {
                a_isQueryCondition="1";
                break;
            }
        }
        return a_isQueryCondition;
    }
    
    

    public ArrayList getUnitList() {
        return unitList;
    }

    public void setUnitList(ArrayList unitList) {
        this.unitList = unitList;
    }

    public HashMap getUnitPosMap() {
        return unitPosMap;
    }

    public void setUnitPosMap(HashMap unitPosMap) {
        this.unitPosMap = unitPosMap;
    }

    public ArrayList getConditionFieldList() {
        return conditionFieldList;
    }

    public void setConditionFieldList(ArrayList conditionFieldList) {
        this.conditionFieldList = conditionFieldList;
    }

    public String getIsQueryCondition() {
        return isQueryCondition;
    }

    public void setIsQueryCondition(String isQueryCondition) {
        this.isQueryCondition = isQueryCondition;
    }

    public String getA0100() {
        return a0100;
    }

    public void setA0100(String a0100) {
        this.a0100 = a0100;
    }

    public ArrayList getApplyedPosList() {
        return applyedPosList;
    }

    public void setApplyedPosList(ArrayList applyedPosList) {
        this.applyedPosList = applyedPosList;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public ArrayList getPosDescFiledList() {
        return posDescFiledList;
    }

    public void setPosDescFiledList(ArrayList posDescFiledList) {
        this.posDescFiledList = posDescFiledList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIsApplyedPos() {
        return isApplyedPos;
    }

    public void setIsApplyedPos(String isApplyedPos) {
        this.isApplyedPos = isApplyedPos;
    }

    public String getPosID() {
        return posID;
    }

    public void setPosID(String posID) {
        this.posID = posID;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrentSetID() {
        return currentSetID;
    }

    public void setCurrentSetID(String currentSetID) {
        this.currentSetID = currentSetID;
    }

    public ArrayList getFieldSetList() {
        return fieldSetList;
    }

    public void setFieldSetList(ArrayList fieldSetList) {
        this.fieldSetList = fieldSetList;
    }

    public ArrayList getResumeFieldList() {
        return resumeFieldList;
    }

    public void setResumeFieldList(ArrayList resumeFieldList) {
        this.resumeFieldList = resumeFieldList;
    }

    public HashMap getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(HashMap fieldMap) {
        this.fieldMap = fieldMap;
    }

    public String getPwd1() {
        return pwd1;
    }

    public void setPwd1(String pwd1) {
        this.pwd1 = pwd1;
    }

    public String getTxtEmail() {
        return txtEmail;
    }

    public void setTxtEmail(String txtEmail) {
        this.txtEmail = txtEmail;
    }

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public FormFile getFile() {
        return file;
    }

    public void setFile(FormFile file) {
        this.file = file;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ArrayList getShowFieldDataList() {
        return showFieldDataList;
    }

    public void setShowFieldDataList(ArrayList showFieldDataList) {
        this.showFieldDataList = showFieldDataList;
    }

    public ArrayList getShowFieldList() {
        return showFieldList;
    }

    public void setShowFieldList(ArrayList showFieldList) {
        this.showFieldList = showFieldList;
    }

    public String getI9999() {
        return i9999;
    }

    public void setI9999(String i9999) {
        this.i9999 = i9999;
    }

    public HashMap getResumeBrowseSetMap() {
        return resumeBrowseSetMap;
    }

    public void setResumeBrowseSetMap(HashMap resumeBrowseSetMap) {
        this.resumeBrowseSetMap = resumeBrowseSetMap;
    }

    public HashMap getSetShowFieldMap() {
        return setShowFieldMap;
    }

    public void setSetShowFieldMap(HashMap setShowFieldMap) {
        this.setShowFieldMap = setShowFieldMap;
    }

    public String getEmailColumn() {
        return emailColumn;
    }

    public void setEmailColumn(String emailColumn) {
        this.emailColumn = emailColumn;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getZpPosID() {
        return zpPosID;
    }

    public void setZpPosID(String zpPosID) {
        this.zpPosID = zpPosID;
    }

    public ArrayList getZpPosList() {
        return zpPosList;
    }

    public void setZpPosList(ArrayList zpPosList) {
        this.zpPosList = zpPosList;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPerson_type() {
        return person_type;
    }

    public void setPerson_type(String person_type) {
        this.person_type = person_type;
    }

    public ArrayList getRemarkList() {
        return remarkList;
    }

    public void setRemarkList(ArrayList remarkList) {
        this.remarkList = remarkList;
    }

    public String getIsPhoto() {
        return isPhoto;
    }

    public void setIsPhoto(String isPhoto) {
        this.isPhoto = isPhoto;
    }

    public String getPreviewTableId() {
        return previewTableId;
    }

    public void setPreviewTableId(String previewTableId) {
        this.previewTableId = previewTableId;
    }

    public String getIsUpPhoto() {
        return isUpPhoto;
    }

    public void setIsUpPhoto(String isUpPhoto) {
        this.isUpPhoto = isUpPhoto;
    }

    public String getIsExp() {
        return isExp;
    }

    public void setIsExp(String isExp) {
        this.isExp = isExp;
    }

    public String getIsAttach() {
        return isAttach;
    }

    public void setIsAttach(String isAttach) {
        this.isAttach = isAttach;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public ArrayList getMediaList() {
        return mediaList;
    }

    public void setMediaList(ArrayList mediaList) {
        this.mediaList = mediaList;
    }

    /*public FormFile getAttachFile() {
        return attachFile;
    }

    public void setAttachFile(FormFile attachFile) {
        this.attachFile = attachFile;
    }*/

    public ArrayList getUploadFileList() {
        return uploadFileList;
    }

    public void setUploadFileList(ArrayList uploadFileList) {
        this.uploadFileList = uploadFileList;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getInsideFlag() {
        return insideFlag;
    }

    public void setInsideFlag(String insideFlag) {
        this.insideFlag = insideFlag;
    }

    public String getWriteable() {
        return writeable;
    }

    public void setWriteable(String writeable) {
        this.writeable = writeable;
    }

    public String getOnlyField() {
        return onlyField;
    }

    public void setOnlyField(String onlyField) {
        this.onlyField = onlyField;
    }

    public String getIsOnlyCheck() {
        return isOnlyCheck;
    }

    public void setIsOnlyCheck(String isOnlyCheck) {
        this.isOnlyCheck = isOnlyCheck;
    }

    public ArrayList getResumeStateList() {
        return resumeStateList;
    }

    public void setResumeStateList(ArrayList resumeStateList) {
        this.resumeStateList = resumeStateList;
    }

    public String getIsDefinitinn() {
        return isDefinitinn;
    }

    public void setIsDefinitinn(String isDefinitinn) {
        this.isDefinitinn = isDefinitinn;
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

    public String getHiddenCode() {
        return hiddenCode;
    }

    public void setHiddenCode(String hiddenCode) {
        this.hiddenCode = hiddenCode;
    }

    public String getNetHref() {
        return netHref;
    }

    public void setNetHref(String netHref) {
        this.netHref = netHref;
    }

    public String getInterviewingCodeValue() {
        return interviewingCodeValue;
    }

    public void setInterviewingCodeValue(String interviewingCodeValue) {
        this.interviewingCodeValue = interviewingCodeValue;
    }

    public String getInterviewingRevertItemid() {
        return interviewingRevertItemid;
    }

    public void setInterviewingRevertItemid(String interviewingRevertItemid) {
        this.interviewingRevertItemid = interviewingRevertItemid;
    }

    public ArrayList getInterviewingRevertItemCodeList() {
        return interviewingRevertItemCodeList;
    }

    public void setInterviewingRevertItemCodeList(
            ArrayList interviewingRevertItemCodeList) {
        this.interviewingRevertItemCodeList = interviewingRevertItemCodeList;
    }

    public String getResumeActive() {
        return resumeActive;
    }

    public void setResumeActive(String resumeActive) {
        this.resumeActive = resumeActive;
    }

    public String getIsDefinitionActive() {
        return isDefinitionActive;
    }

    public void setIsDefinitionActive(String isDefinitionActive) {
        this.isDefinitionActive = isDefinitionActive;
    }

    public String getBelongUnit() {
        return belongUnit;
    }

    public void setBelongUnit(String belongUnit) {
        this.belongUnit = belongUnit;
    }

    public String getActiveValue() {
        return activeValue;
    }

    public void setActiveValue(String activeValue) {
        this.activeValue = activeValue;
    }

    public String getIsConfigExp() {
        return isConfigExp;
    }

    public void setIsConfigExp(String isConfigExp) {
        this.isConfigExp = isConfigExp;
    }

    public String getIsHaveExp() {
        return isHaveExp;
    }

    public void setIsHaveExp(String isHaveExp) {
        this.isHaveExp = isHaveExp;
    }

    public String getPositionID() {
        return positionID;
    }

    public void setPositionID(String positionID) {
        this.positionID = positionID;
    }

    public String getIsHaveEditableField() {
        return isHaveEditableField;
    }

    public void setIsHaveEditableField(String isHaveEditableField) {
        this.isHaveEditableField = isHaveEditableField;
    }

    public HashMap getEditableMap() {
        return editableMap;
    }

    public void setEditableMap(HashMap editableMap) {
        this.editableMap = editableMap;
    }

    public String getAdmissionCard() {
        return admissionCard;
    }

    public void setAdmissionCard(String admissionCard) {
        this.admissionCard = admissionCard;
    }

    public String getBlackField() {
        return blackField;
    }

    public void setBlackField(String blackField) {
        this.blackField = blackField;
    }

    public String getBlackNbase() {
        return blackNbase;
    }

    public void setBlackNbase(String blackNbase) {
        this.blackNbase = blackNbase;
    }

    public String getMax_count() {
        return max_count;
    }

    public void setMax_count(String max_count) {
        this.max_count = max_count;
    }

    public String getBlackFieldDesc() {
        return blackFieldDesc;
    }

    public void setBlackFieldDesc(String blackFieldDesc) {
        this.blackFieldDesc = blackFieldDesc;
    }

    public String getBlackFieldValue() {
        return blackFieldValue;
    }

    public void setBlackFieldValue(String blackFieldValue) {
        this.blackFieldValue = blackFieldValue;
    }

    public String getBlackFieldSize() {
        return blackFieldSize;
    }

    public void setBlackFieldSize(String blackFieldSize) {
        this.blackFieldSize = blackFieldSize;
    }

    public String getCanPrint() {
        return canPrint;
    }

    public void setCanPrint(String canPrint) {
        this.canPrint = canPrint;
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

    public String getIsPrompt() {
        return isPrompt;
    }

    public void setIsPrompt(String isPrompt) {
        this.isPrompt = isPrompt;
    }

    public String getHireChannel() {
        return hireChannel;
    }

    public void setHireChannel(String hireChannel) {
        this.hireChannel = hireChannel;
    }

    public String getIsDefineWorkExperience() {
        return isDefineWorkExperience;
    }

    public void setIsDefineWorkExperience(String isDefineWorkExperience) {
        this.isDefineWorkExperience = isDefineWorkExperience;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public ArrayList getWorkExperienceCodeList() {
        return workExperienceCodeList;
    }

    public void setWorkExperienceCodeList(ArrayList workExperienceCodeList) {
        this.workExperienceCodeList = workExperienceCodeList;
    }

    public String getWorkExperienceDesc() {
        return workExperienceDesc;
    }

    public void setWorkExperienceDesc(String workExperienceDesc) {
        this.workExperienceDesc = workExperienceDesc;
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

    public String getParamFlag() {
        return paramFlag;
    }

    public void setParamFlag(String paramFlag) {
        this.paramFlag = paramFlag;
    }

    public String getOnlyName() {
        return onlyName;
    }

    public void setOnlyName(String onlyName) {
        this.onlyName = onlyName;
    }

    public String getOnlyNameDesc() {
        return onlyNameDesc;
    }

    public void setOnlyNameDesc(String onlyNameDesc) {
        this.onlyNameDesc = onlyNameDesc;
    }

    public String getOnlyValue() {
        return onlyValue;
    }

    public void setOnlyValue(String onlyValue) {
        this.onlyValue = onlyValue;
    }

    public String getOnlySize() {
        return onlySize;
    }

    public void setOnlySize(String onlySize) {
        this.onlySize = onlySize;
    }

    public String getHasXiaoYuan() {
        return hasXiaoYuan;
    }

    public void setHasXiaoYuan(String hasXiaoYuan) {
        this.hasXiaoYuan = hasXiaoYuan;
    }

    public ArrayList getRunHeaderList() {
        return runHeaderList;
    }

    public void setRunHeaderList(ArrayList runHeaderList) {
        this.runHeaderList = runHeaderList;
    }

    public ArrayList getRunDataList() {
        return runDataList;
    }

    public void setRunDataList(ArrayList runDataList) {
        this.runDataList = runDataList;
    }

    public ArrayList getPosFieldList() {
        return posFieldList;
    }

    public void setPosFieldList(ArrayList posFieldList) {
        this.posFieldList = posFieldList;
    }
    public String getHasMessage() {
        return hasMessage;
    }
    public void setHasMessage(String hasMessage) {
        this.hasMessage = hasMessage;
    }
    public String getNotice_message() {
        return notice_message;
    }
    public void setNotice_message(String notice_message) {
        this.notice_message = notice_message;
    }
    public String getZpUnitCode() {
        return zpUnitCode;
    }
    public void setZpUnitCode(String zpUnitCode) {
        this.zpUnitCode = zpUnitCode;
    }
    public String getUnitIntroduce() {
        return unitIntroduce;
    }
    public void setUnitIntroduce(String unitIntroduce) {
        this.unitIntroduce = unitIntroduce;
    }
    public String getIntroduceType() {
        return introduceType;
    }
    public void setIntroduceType(String introduceType) {
        this.introduceType = introduceType;
    }
    public String getIntroducelink() {
        return introducelink;
    }
    public void setIntroducelink(String introducelink) {
        this.introducelink = introducelink;
    }
    public String getIsHasNewDate() {
        return isHasNewDate;
    }
    public void setIsHasNewDate(String isHasNewDate) {
        this.isHasNewDate = isHasNewDate;
    }
    public String getIsAllPos() {
        return isAllPos;
    }
    public void setIsAllPos(String isAllPos) {
        this.isAllPos = isAllPos;
    }
    public String getSy_message() {
        return sy_message;
    }
    public void setSy_message(String sy_message) {
        this.sy_message = sy_message;
    }
    public String getLfType() {
        return lfType;
    }
    public void setLfType(String lfType) {
        this.lfType = lfType;
    }
    public String getHbType() {
        return hbType;
    }
    public void setHbType(String hbType) {
        this.hbType = hbType;
    }
    public String getCms_chl_no() {
        return cms_chl_no;
    }
    public void setCms_chl_no(String cms_chl_no) {
        this.cms_chl_no = cms_chl_no;
    }
    public String getMenuType() {
        return menuType;
    }
    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }
    public ArrayList getCommQueryList() {
        return commQueryList;
    }
    public void setCommQueryList(ArrayList commQueryList) {
        this.commQueryList = commQueryList;
    }
    public ArrayList getSunitlist() {
        return sunitlist;
    }
    public void setSunitlist(ArrayList sunitlist) {
        this.sunitlist = sunitlist;
    }
    public String getCer() {
        return cer;
    }
    public void setCer(String cer) {
        this.cer = cer;
    }
    public String getIsapply() {
        return isapply;
    }
    public void setIsapply(String isapply) {
        this.isapply = isapply;
    }
    public String getRequireId() {
        return requireId;
    }
    public void setRequireId(String requireId) {
        this.requireId = requireId;
    }
    public String getPosDesc() {
        return posDesc;
    }
    public void setPosDesc(String posDesc) {
        this.posDesc = posDesc;
    }
    public ArrayList getBoardlist() {
        return boardlist;
    }
    public void setBoardlist(ArrayList boardlist) {
        this.boardlist = boardlist;
    }
    public String getUrl_addr40() {
        return url_addr40;
    }
    public void setUrl_addr40(String url_addr40) {
        this.url_addr40 = url_addr40;
    }
    public String getHireMajorCode() {
        return hireMajorCode;
    }
    public void setHireMajorCode(String hireMajorCode) {
        this.hireMajorCode = hireMajorCode;
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
    public String getValidateInfo() {
        return validateInfo;
    }
    public void setValidateInfo(String validateInfo) {
        this.validateInfo = validateInfo;
    }
    public String getCanPrintExamno() {
        return canPrintExamno;
    }
    public void setCanPrintExamno(String canPrintExamno) {
        this.canPrintExamno = canPrintExamno;
    }
    public void setCanQueryScore(String canQueryScore) {
        this.canQueryScore = canQueryScore;
    }
    public String getCanQueryScore() {
        return canQueryScore;
    }
    public void setFieldSetMustList(ArrayList fieldSetMustList) {
        this.fieldSetMustList = fieldSetMustList;
    }
    public ArrayList getFieldSetMustList() {
        return fieldSetMustList;
    }
	public void setAppliedPosItems(String appliedPosItems) {
		this.appliedPosItems = appliedPosItems;
	}
	public String getAppliedPosItems() {
		return appliedPosItems;
	}
	
    public String getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public String getAccountFlag() {
        return accountFlag;
    }
    public void setAccountFlag(String accountFlag) {
        this.accountFlag = accountFlag;
    }
	public ArrayList getPublicityList() {
		return publicityList;
	}
	public void setPublicityList(ArrayList publicityList) {
		this.publicityList = publicityList;
	}
	public String getApplyMessage() {
		return applyMessage;
	}
	public void setApplyMessage(String applyMessage) {
		this.applyMessage = applyMessage;
	}
	public String getBirthdayName() {
        return birthdayName;
    }
    public void setBirthdayName(String birthdayName) {
        this.birthdayName = birthdayName;
    }
    public String getAgeName() {
        return ageName;
    }
    public void setAgeName(String ageName) {
        this.ageName = ageName;
    }
    public String getAxName() {
        return axName;
    }
    public void setAxName(String axName) {
        this.axName = axName;
    }
	
	    
}
