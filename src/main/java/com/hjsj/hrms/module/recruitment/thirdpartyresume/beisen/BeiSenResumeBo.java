package com.hjsj.hrms.module.recruitment.thirdpartyresume.beisen;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BeiSenResumeBo extends ThirdPartyResumeBase {
    // 北森api基础url
    private String bsUrl = "";
    // 北森招聘租户id
    private String tenantId = "";
    // token(目前固定，不用每次重新获取)
    private String token = "";
    // 状态ID或编码
    private String statusValue = "";
    // 阶段ID或编码
    private String phaseValue = "";
    
    private Document doc;

    private String msg;
    //导入的时间是否带有时分秒
    private boolean dateFlag = false;
    //代码对应关系
    private HashMap<String, ArrayList<LazyDynaBean>> codeitems = new HashMap<String, ArrayList<LazyDynaBean>>();
    private Category cat = Category.getInstance(this.getClass());
    
    @Override
    protected String getThirdPartyName() {
        return "BeiSen";
    }

    @Override
    public ArrayList getResumeFromThirdParty(HashMap params) {
        ArrayList resumeList = new ArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String startdate = (String) params.get("startDate");
            if(StringUtils.isNotEmpty(startdate) && startdate.length() > 10) {
                format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                dateFlag = true;
            }
            this.cat.debug("第三方简历导入开始……");
            Date startDate = format.parse(startdate);
            String enddate = (String) params.get("endDate");
            Date endDate = format.parse(enddate);
            String blacklist_field = (String) params.get("blacklist_field");
            this.cat.debug("第三方简历导入时间范围：" + startdate + "至" + enddate);
            HashMap mapReseme = getResumeParam();
            LazyDynaBean paramBean = (LazyDynaBean) mapReseme.get("thirdPartyParm");
            bsUrl = (String) paramBean.get("apiurl");
            tenantId = (String) paramBean.get("tenantId");
            token = (String) paramBean.get("token");  
            this.codeitems = (HashMap<String, ArrayList<LazyDynaBean>>) mapReseme.get("codeitems");
            
            ArrayList<String> msgList = new ArrayList<String>();
            if(StringUtils.isEmpty(bsUrl)) {
                msgList.add("bsUrl");
                return msgList;
            }
            
            if(StringUtils.isEmpty(tenantId)) {
                msgList.add("tenantId");
                return msgList;
            }
            
            if(StringUtils.isEmpty(token)) {
                msgList.add("token");
                return msgList;
            }
            
            this.cat.debug("第三方简历导入bsUrl：" + bsUrl);
            this.cat.debug("第三方简历导入tenantId：" + tenantId);
            this.cat.debug("第三方简历导入token：" + token);
            
            String imptype = (String) paramBean.get("imptype");
            this.cat.debug("第三方简历导入方式：" + imptype);
            LazyDynaBean ThirdParamBean = (LazyDynaBean) mapReseme.get("thirdPartyParm");
            String mainItem = (String) ThirdParamBean.get("identifyfld");
            this.cat.debug("第三方简历导入关键指标：" + mainItem);
            String secItem = (String) ThirdParamBean.get("sencondfld");
            this.cat.debug("第三方简历导入次关键指标：" + secItem);
            ArrayList<LazyDynaBean> fieldsetList = (ArrayList<LazyDynaBean>) mapReseme.get("fieldset");
            HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem = (HashMap<String, ArrayList<LazyDynaBean>>) mapReseme.get("fielditem");
            
            HashMap<String, String> logMap = new HashMap<String, String>();
            if("4".equalsIgnoreCase(imptype)) {
                String synchronousFlag = (String) paramBean.get("synchronousFlag");
                String dbname = (String) paramBean.get("dbname");
                RecordVo sms_vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
                if(sms_vo==null){
                    this.msg = "请在“系统管理-通讯平台-电话邮箱设置”页面设置移动电话指标！";
                    return resumeList;
                }
                
                String phoneField = sms_vo.getString("str_value");
                if(StringUtils.isEmpty(phoneField)){
                    this.msg = "请在“系统管理-通讯平台-电话邮箱设置”页面设置移动电话指标！";
                    return resumeList;
                }
                
                StringBuffer sql = new StringBuffer();
                sql.append("select distinct ");
                sql.append(phoneField);
                sql.append(" from ");
                sql.append(dbname);
                sql.append("A01 where ");
                sql.append(Sql_switcher.isnull(synchronousFlag, "2"));
                sql.append(" <> 1");
                this.cat.debug("第三方简历导入获取未导入人员的手机号sql：" + sql);
                ContentDAO dao = new ContentDAO(this.conn);
                RowSet rs = null;
                String phones = "";
                try{
                    int num = 0;
                    ArrayList<String> phoneList = new ArrayList<String>();
                    rs = dao.search(sql.toString());
                    while (rs.next()) {
                        if(num == 10) {
                            phones = phones.substring(0, phones.length() - 1);
                            phoneList.add(phones);
                            phones = "";
                            num = 0;
                        }
                        
                        String phone = rs.getString(phoneField);
                        if(StringUtils.isEmpty(phone) || !StringUtils.isNumeric(phone))
                            continue;
                        
                        phones += phone + ",";
                        num ++;
                    }
                    
                    this.cat.debug("第三方简历导入未导入人员的手机号：" + phones);
                    if(StringUtils.isNotEmpty(phones)) {
                        phones = phones.substring(0, phones.length() - 1);
                        phoneList.add(phones);
                    }
                    
                    if(phoneList == null || phoneList.size() < 1){
                        logMap.put("Num", "0");
                        WriteImportDetail(logMap);
                        return resumeList;
                    }
                    
                    ArrayList<Element> ApplicantList = new ArrayList<Element>();
                    for(int i = 0; i < phoneList.size(); i++){
                        phones = phoneList.get(i);
                        String xml = GetApplicantsByMobile(phones);
                        if(StringUtils.isEmpty(xml))
                            continue;
                        
                        this.cat.debug("按手机号获取到的第" + i + "份简历：" + xml);
                        if(!xml.startsWith("<?xml"))
                            xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + xml;
                        
                        doc = DocumentHelper.parseText(xml);
                        ArrayList<Element> applicant = (ArrayList<Element>) this.doc.getRootElement().elements("Applicant");
                        if(applicant == null || applicant.size() < 1)
                            continue;
                        
                        ApplicantList.addAll(applicant);
                    }
//                   String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ArrayOfApplicant xmlns=\"api.beisenapp.com\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><Applicant><ApplicantId>315016768</ApplicantId><ApplyJobSummaries><JobSummary><ApplyInfo><ActiveOfferApplyState>0</ActiveOfferApplyState><ActiveOfferState>0</ActiveOfferState><ApplyDocuments i:nil=\"true\"/><CustomFileds/><ElinkUrl>https://bsurl.cn/JjbXIiKT</ElinkUrl><InitApplyDate>2017-06-26</InitApplyDate><InitApplyKindName>其他</InitApplyKindName><InitApplyMedium><ExtendValue>手动新增</ExtendValue><Value>8192</Value></InitApplyMedium><InitApplySource><ExtendValue>其他</ExtendValue><Value>5</Value></InitApplySource><LastApplyDate>2017-06-26</LastApplyDate><LastApplyKindName>其他</LastApplyKindName><LastApplyMedium><ExtendValue>手动新增</ExtendValue><Value>8192</Value></LastApplyMedium><LastApplySource><ExtendValue>其他</ExtendValue><Value>5</Value></LastApplySource><PhaseChangeDate>2017-06-26 14:30:02</PhaseChangeDate><PhaseInfo><Id>182691</Id><Code>S15</Code><Name>简历初筛</Name></PhaseInfo><R_BelongSource><ExtendValue>其他</ExtendValue><Value>5</Value></R_BelongSource><R_BelongSourceName>其他</R_BelongSourceName><R_InterviewStation><ExtendValue i:nil=\"true\"/><Value>0</Value></R_InterviewStation><Recommender i:nil=\"true\"/><ResumeDownloadUrl i:nil=\"true\"/><StateChangeDate>2017-07-20 11:17:14</StateChangeDate><StatusInfo><Id>176811</Id><Code>U04</Code><Name>本轮淘汰</Name></StatusInfo></ApplyInfo><BusiUserEmail/><CustomFileds xmlns:a=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/><DemandSectorParentTreeNodes><Id>15873</Id><Name> 信息技术部</Name><ParentTree><Id>-1</Id><Name>国金证券</Name><ParentTree i:nil=\"true\"/></ParentTree></DemandSectorParentTreeNodes><DutyUserEmail>liuwq@gjzq.com.cn</DutyUserEmail><DutyUserMobile>139-1838-4913</DutyUserMobile><DutyUserName>刘伟清</DutyUserName><JobCode>J11098</JobCode><JobCustomFields/><JobId>310034113</JobId><JobTitle>公司总部-系统运维岗</JobTitle><Organization><Code>11</Code><Id>15873</Id><Name> 信息技术部</Name><PatentDepartCode>11</PatentDepartCode><PatentDepartName> 信息技术部</PatentDepartName><level>1</level><state>1</state></Organization><RecuitCategory><Code i:nil=\"true\"/><Id>1</Id><Name>社会招聘</Name></RecuitCategory><ShareUserEmails/><SyncId/><WorkLocation>浦东新区</WorkLocation></JobSummary></ApplyJobSummaries><Attachments/><CadidateId>C00174164</CadidateId><Certificate><Description i:nil=\"true\"/><Items/></Certificate><EducationExperience><Description i:nil=\"true\"/><Items/></EducationExperience><ElinkUrl>https://bsurl.cn/QwPFYDJf</ElinkUrl><FamilyList><Description i:nil=\"true\"/><Items i:nil=\"true\"/></FamilyList><InterviewInfoSummaries/><InterviewSummaries/><LanguageAbility><Description i:nil=\"true\"/><Items/></LanguageAbility><Operators/><OtherInfo><Description i:nil=\"true\"/><Items i:nil=\"true\"/></OtherInfo><Profile><Items><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_Remark</PropertyName><PropertyShortName>R_Remark</PropertyShortName><Value i:nil=\"true\"/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_RemarkDate</PropertyName><PropertyShortName>R_RemarkDate</PropertyShortName><Value i:nil=\"true\"/></StringValueContainer><StringValueContainer><Code/><PropertyName>extwoshi_140098_1143753829</PropertyName><PropertyShortName>woshi</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code/><PropertyName>extshifouyou_140098_1487856049</PropertyName><PropertyShortName>shifouyou</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_InitializeApplyDate</PropertyName><PropertyShortName>R_InitializeApplyDate</PropertyShortName><Value>2017-06-26</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_CommonState</PropertyName><PropertyShortName>R_CommonState</PropertyShortName><Value>0</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_ExamCategory4Sort</PropertyName><PropertyShortName>R_ExamCategory4Sort</PropertyShortName><Value>-32767</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_BelongAcquireMannerId</PropertyName><PropertyShortName>R_BelongAcquireMannerId</PropertyShortName><Value>4</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_BelongSource</PropertyName><PropertyShortName>R_BelongSource</PropertyShortName><Value>5</Value></StringValueContainer><StringValueContainer><Code>310000147</Code><PropertyName>R_LastRelationStoreDB</PropertyName><PropertyShortName>R_LastRelationStoreDB</PropertyShortName><Value>人才公海</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_IsTms</PropertyName><PropertyShortName>R_IsTms</PropertyShortName><Value>1</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_Gender4Sort</PropertyName><PropertyShortName>R_Gender4Sort</PropertyShortName><Value>0</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_ResidenceStates_P</PropertyName><PropertyShortName>R_ResidenceStates_P</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_RelationJobIds</PropertyName><PropertyShortName>R_RelationJobIds</PropertyShortName><Value>310034113</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_RelationJobCount</PropertyName><PropertyShortName>R_RelationJobCount</PropertyShortName><Value>1</Value></StringValueContainer><StringValueContainer><Code/><PropertyName>JapaneseLevel</PropertyName><PropertyShortName>JapaneseLevel</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_WorkYear4Sort</PropertyName><PropertyShortName>R_WorkYear4Sort</PropertyShortName><Value>-32767</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>CertificateNumber</PropertyName><PropertyShortName>CertificateNumber</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_NativePlaces_P</PropertyName><PropertyShortName>R_NativePlaces_P</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code/><PropertyName>HighestDegree</PropertyName><PropertyShortName>HighestDegree</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>Mobile</PropertyName><PropertyShortName>Mobile</PropertyShortName><Value>15010723818</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_EducationLevel4Sort</PropertyName><PropertyShortName>R_EducationLevel4Sort</PropertyShortName><Value>-32767</Value></StringValueContainer><StringValueContainer><Code>0</Code><PropertyName>gender</PropertyName><PropertyShortName>gender</PropertyShortName><Value>男</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_RPRs_P</PropertyName><PropertyShortName>R_RPRs_P</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code>8192</Code><PropertyName>R_InitializeMediumId</PropertyName><PropertyShortName>R_InitializeMediumId</PropertyShortName><Value>手动新增</Value></StringValueContainer><StringValueContainer><Code>5</Code><PropertyName>R_LastSource</PropertyName><PropertyShortName>R_LastSource</PropertyShortName><Value>其他</Value></StringValueContainer><StringValueContainer><Code>8192</Code><PropertyName>R_LastMediumId</PropertyName><PropertyShortName>R_LastMediumId</PropertyShortName><Value>手动新增</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_Namepinyin</PropertyName><PropertyShortName>R_Namepinyin</PropertyShortName><Value>熊文祥</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>evaluation</PropertyName><PropertyShortName>evaluation</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_Recent_Store_Time</PropertyName><PropertyShortName>R_Recent_Store_Time</PropertyShortName><Value>2017-07-20</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>telephone</PropertyName><PropertyShortName>telephone</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>CareerGoals</PropertyName><PropertyShortName>CareerGoals</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_AllSources</PropertyName><PropertyShortName>R_AllSources</PropertyShortName><Value>5</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_LastApplyDate</PropertyName><PropertyShortName>R_LastApplyDate</PropertyShortName><Value>2017-06-26</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_JapaneseLevel4Sort</PropertyName><PropertyShortName>R_JapaneseLevel4Sort</PropertyShortName><Value>-32767</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_LastAcquireMannerId</PropertyName><PropertyShortName>R_LastAcquireMannerId</PropertyShortName><Value>4</Value></StringValueContainer><StringValueContainer><Code>310034113</Code><PropertyName>R_LastRelationJob</PropertyName><PropertyShortName>R_LastRelationJob</PropertyShortName><Value>公司总部-系统运维岗</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_InviteTestCount4Sort</PropertyName><PropertyShortName>R_InviteTestCount4Sort</PropertyShortName><Value>0</Value></StringValueContainer><StringValueContainer><Code>5</Code><PropertyName>R_InitializeSource</PropertyName><PropertyShortName>R_InitializeSource</PropertyShortName><Value>其他</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>Name</PropertyName><PropertyShortName>Name</PropertyShortName><Value>熊文祥</Value></StringValueContainer><StringValueContainer><Code>0</Code><PropertyName>R_IsDeleted</PropertyName><PropertyShortName>R_IsDeleted</PropertyShortName><Value>否</Value></StringValueContainer><StringValueContainer><Code/><PropertyName>ExamCategory</PropertyName><PropertyShortName>ExamCategory</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_StudentFroms_P</PropertyName><PropertyShortName>R_StudentFroms_P</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_BelongMediumId</PropertyName><PropertyShortName>R_BelongMediumId</PropertyShortName><Value>8192</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_RelationStoreDbs</PropertyName><PropertyShortName>R_RelationStoreDbs</PropertyShortName><Value>310000147</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>Birthday</PropertyName><PropertyShortName>Birthday</PropertyShortName><Value>2017-06-26</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_InviteTestCount</PropertyName><PropertyShortName>R_InviteTestCount</PropertyShortName><Value/></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_ApplyCount</PropertyName><PropertyShortName>R_ApplyCount</PropertyShortName><Value>1</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_CreateDate</PropertyName><PropertyShortName>R_CreateDate</PropertyShortName><Value>2017-06-26</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>R_UpdateDate</PropertyName><PropertyShortName>R_UpdateDate</PropertyShortName><Value>2017-06-26</Value></StringValueContainer><StringValueContainer><Code i:nil=\"true\"/><PropertyName>email</PropertyName><PropertyShortName>email</PropertyShortName><Value>xiongwx@hjsoft.com.cn</Value></StringValueContainer><StringValueContainer><Code>859b7098-9016-4d9d-a9c9-1f0032d46e03</Code><PropertyName>R_LastSourceType</PropertyName><PropertyShortName>R_LastSourceType</PropertyShortName><Value>其他</Value></StringValueContainer><StringValueContainer><Code>859b7098-9016-4d9d-a9c9-1f0032d46e03</Code><PropertyName>R_InitializeSourceType</PropertyName><PropertyShortName>R_InitializeSourceType</PropertyShortName><Value>其他</Value></StringValueContainer></Items></Profile><ProjectExperience><Description i:nil=\"true\"/><Items/></ProjectExperience><ResumeForAttachments/><TestDetailReports/><TestResults/><WorkExperience><Description i:nil=\"true\"/><Items/></WorkExperience><PracticeExperience><Description i:nil=\"true\"/><Items i:nil=\"true\"/></PracticeExperience><SchoolPractice><Description i:nil=\"true\"/><Items i:nil=\"true\"/></SchoolPractice><Tain><Description i:nil=\"true\"/><Items i:nil=\"true\"/></Tain><AwardsWons><Description i:nil=\"true\"/><Items i:nil=\"true\"/></AwardsWons><Objective><CurrJobCategory i:nil=\"true\"/><CurrSalary i:nil=\"true\"/><CurrWorkCity i:nil=\"true\"/><CustomFields i:nil=\"true\"/><Description i:nil=\"true\"/><EngagedIndustry i:nil=\"true\"/><EntrantDate i:nil=\"true\"/><ExpectAnnualSalary i:nil=\"true\"/><ExpectIndustry i:nil=\"true\"/><ExpectJobCategory i:nil=\"true\"/><ExpectSalary i:nil=\"true\"/><ExpectWorkCity i:nil=\"true\"/><ExpectWorkKind i:nil=\"true\"/><ExptectedArrivalTime i:nil=\"true\"/><IsAbroad>false</IsAbroad><PresentAnnualSalary i:nil=\"true\"/><WorkState i:nil=\"true\"/></Objective><Published><Description i:nil=\"true\"/><Items i:nil=\"true\"/></Published><SchoolOffice><Description i:nil=\"true\"/><Items i:nil=\"true\"/></SchoolOffice><Skills><Description i:nil=\"true\"/><Items i:nil=\"true\"/></Skills><TeamManager><Description i:nil=\"true\"/><Items i:nil=\"true\"/></TeamManager></Applicant></ArrayOfApplicant>";
//                   doc = DocumentHelper.parseText(xml);
//                   ArrayList<Element> applicant = (ArrayList<Element>) this.doc.getRootElement().elements("Applicant");
//                   
//                   ApplicantList.addAll(applicant);
                   
                    int sum = ApplicantList == null ? 0 : ApplicantList.size();
                    this.cat.debug("第三方简历导入一共获取到" + sum + "份简历。");
                    logMap.put("Num",sum + "");
                    for(int i = 0; i < ApplicantList.size(); i++){
                        Element Applicant = ApplicantList.get(i);
                        this.cat.debug("开始解析第" + i + "份简历……");
                        importResunme(Applicant, mainItem, secItem, fieldsetList, blacklist_field, resumeXmlItem);
                    }
                    
                }catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    PubFunc.closeResource(rs);
                }
            } else {
                String statusOrPhase = (String) paramBean.get("statusId");
                ArrayList<String> idlists = new ArrayList<String>();
                String[] statusOrPhases = statusOrPhase.split(";");
                for(int m = 0; m < statusOrPhases.length; m++) {
                    if(StringUtils.isEmpty(statusOrPhases[m]))
                        continue;
                    
                    String[] statusAndPhases = statusOrPhases[m].split(":");
                    phaseValue = statusAndPhases[0];
                    String[] phaseValues = statusAndPhases[1].split(",");
                    for(int n = 0; n < phaseValues.length; n++){
                        statusValue = phaseValues[n];
                        idlists.addAll(getResumeIds(startDate, endDate));
                    }
                }
                int sum = idlists == null ? 0 : idlists.size();
                logMap.put("Num",sum + "");
                
                for (int j = 0; j < idlists.size(); j++) {
                    
                    String xml = getResume(idlists.get(j));
                    if(StringUtils.isEmpty(xml))
                        continue;
                    if(!xml.startsWith("<?xml"))
                        xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + xml;
                    
                    doc = DocumentHelper.parseText(xml);
                    Element Applicant = this.doc.getRootElement().element("Applicant");
                    if(Applicant == null)
                        continue;
                    
                    this.cat.debug("开始解析第" + j + "份简历……");
                    importResunme(Applicant, mainItem, secItem, fieldsetList, blacklist_field, resumeXmlItem);
                }
                
            }
            
            WriteImportDetail(logMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resumeList;
    }

    public ArrayList getResumeIds(Date startDate, Date endDate) {
        ArrayList resumeIds = new ArrayList();

        String ids = GetApplicantIdsByDateAndStatus(startDate, endDate);
        if(StringUtils.isEmpty(ids) || "[]".equalsIgnoreCase(ids))
            return resumeIds;
        
        if(ids.indexOf("<error_code>") > -1)
            return resumeIds;
        
        if(ids.startsWith("["))
            ids = ids.substring(1);
        
        if(ids.endsWith("]"))
            ids = ids.substring(0, ids.length() - 1);
        
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++)
            resumeIds.add(id[i].trim());
        
        return resumeIds;
    }

    public String getResume(String ids) {
        String resume = GetApplicantsById(ids);
        return resume;
    }

    private String GetApplicantIdsByDateAndStatus(Date startDate, Date endDate) {
        String ids = "";
        try {
            String start_time = "";
            String end_time = "";
            if(dateFlag) {
                start_time = DateUtils.format(startDate, "yyyyMMddhhmmss");
                end_time = DateUtils.format(endDate, "yyyyMMddhhmmss");      
            }else {
                start_time = DateUtils.format(startDate, "yyyyMMdd000000");
                end_time = DateUtils.format(endDate, "yyyyMMdd235959");      
            }
                
            String url = this.bsUrl + "/" + this.tenantId + "/applicant/GetApplicantIdsByDateAndStatus";
            if(StringUtils.isEmpty(phaseValue))
                phaseValue = "S12";        	       	
            
            if(StringUtils.isEmpty(statusValue))
                statusValue = "U12";  	
            
            String param = "start_time=" + start_time + "&end_time=" + end_time + "&job_id=&phase_id=" + phaseValue + "&status_id=" + statusValue + "";
            ids = HttpRequest.sendGet(this.token, url, param);
            if(ids.indexOf("<error_code>402</error_code>") > -1) {
                this.token = "Bearer " + this.token;
                ids = HttpRequest.sendGet(this.token, url, param);
            }
            
            if(ids.indexOf("<error_code>") > -1)
                RecordError(ids);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ids;
    }

    private String GetApplicantsById(String applicantid) {
        String url = this.bsUrl + "/" + this.tenantId + "/Applicant/ById/" + applicantid;
        String param = "language=1&photo_base64=0&format=xml";
        String s = HttpRequest.sendGet(this.token, url, param);
        return s;
    }
    
    private void RecordError(String msg){
        try {
            this.doc = DocumentHelper.parseText(msg);
            String errorCode = this.doc.getRootElement().elementText("error_code");
            if("400".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：400，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("401".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：401，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("402".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：402，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("403".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：403，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("404".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：404，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("405".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：405，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("406".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：406，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("407".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：407，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("408".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：408，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("409".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：409，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("410".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：410，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("411".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：411，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("412".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：412，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("413".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：413，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("414".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：414，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("415".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：415，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            else if("500".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历失败:<br>&nbsp;&nbsp;&nbsp;&nbsp;错误代码：500，<br>&nbsp;&nbsp;&nbsp;&nbsp;错误标识："
                        + this.doc.getRootElement().elementText("error_message")
                        + ",<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：Consumer key不能为空！";
            
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        
    }
    /**
     * 返回错误信息
     */
    @Override
    public String getMsg() {
        return msg;
    }
    /**
     * 解析简历并导入人员信息
     * @param Applicant 人员简历
     * @param mainItem 关键指标
     * @param secItem 次关键指标
     * @param fieldsetList 信息集对应
     * @param blacklist_field 黑名单指标
     * @param resumeXmlItem 指标对应
     */
    public void importResunme(Element Applicant,String mainItem, String secItem, ArrayList<LazyDynaBean> fieldsetList,
            String blacklist_field, HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem) {
        String blacklist_value = "";
        String personName = "";
        String mainItemValue = "";
        String secItemValue = "";
        ArrayList<LazyDynaBean> resumeInfoList = new ArrayList<LazyDynaBean>();
        try {
            
            for (int i = 0; i < fieldsetList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) fieldsetList.get(i);
                String resumeSet = (String) bean.get("resumeset");
                String resumesetId = (String) bean.get("resumesetId");
                String ehrset = (String) bean.get("ehrset");
                if (StringUtils.isEmpty(ehrset))
                    continue;
                
                this.cat.debug("解析信息集" + ehrset + "的数据……");
                List child = null;
                ArrayList<LazyDynaBean> itemXmlList = resumeXmlItem.get(resumeSet);
                if ("Profile".equalsIgnoreCase(resumesetId)) {
                    for (int m = 0; m < itemXmlList.size(); m++) {
                        LazyDynaBean itembean = (LazyDynaBean) itemXmlList.get(m);
                        String resumefld = (String) itembean.get("resumefld");
                        String ehrfld = (String) itembean.get("ehrfld");
                        String resumefldid = (String) itembean.get("resumefldid");
                        if (StringUtils.isEmpty(ehrfld))
                            continue;
                        
                        this.cat.debug("解析指标" + ehrfld + "的数据……");
                        child = Applicant.element("Profile").element("Items").elements("StringValueContainer");
                        for (int n = 0; n < child.size(); n++) {
                            Element item = (Element) child.get(n);
                            String itemid = item.elementText("PropertyShortName");
                            if (!resumefldid.equalsIgnoreCase(itemid))
                                continue;
                            
                            FieldItem fi = DataDictionary.getFieldItem(ehrfld);
                            String value = item.elementText("Value");
                            if ("Name".equalsIgnoreCase(resumefldid))
                                personName = value;
                            
                            if (ehrfld.equalsIgnoreCase(mainItem))
                                mainItemValue = value;
                            
                            if (ehrfld.equalsIgnoreCase(secItem))
                                secItemValue = value;
                            this.cat.debug("指标" + ehrfld + "的值0：" + value);
                            value = getTextByHtml(value);
                            this.cat.debug("指标" + ehrfld + "的值1：" + value);
                            value = PubFunc.hireKeyWord_filter(value);
                            LazyDynaBean ResumeBean = new LazyDynaBean();
                            ResumeBean.set("itemtype", fi.getItemtype());
                            ResumeBean.set("itemlength", fi.getItemlength());
                            ResumeBean.set("ehrfld", ehrfld);
                            ResumeBean.set("resumefld", resumefld);
                            ResumeBean.set("resumeset", resumeSet);
                            ResumeBean.set("itemformat", fi.getFormat());
                            ResumeBean.set("setid", fi.getFieldsetid());
                            ResumeBean.set("value", value);// bean中获得的itemid就是resumeFLDXML中定义的itemid,而hm中存放的是才智创新返回来的值
                            // 若指标等于黑名单指标
                            if (ehrfld != null && ehrfld.equalsIgnoreCase(blacklist_field))
                                blacklist_value = value;
                            
                            this.cat.debug("指标" + ehrfld + "的值2：" + value);
                            resumeInfoList.add(ResumeBean);
                            break;
                        }
                    }
                } else if ("Objective".equalsIgnoreCase(resumesetId) 
                        || "Objectiveitem".equalsIgnoreCase(resumesetId.toLowerCase())) {
                    for (int m = 0; m < itemXmlList.size(); m++) {
                        LazyDynaBean itembean = (LazyDynaBean) itemXmlList.get(m);
                        String resumefld = (String) itembean.get("resumefld");
                        String ehrfld = (String) itembean.get("ehrfld");
                        String resumefldid = (String) itembean.get("resumefldid");
                        if (StringUtils.isEmpty(ehrfld))
                            continue;
                        
                        this.cat.debug("解析指标" + ehrfld + "的数据……");
                        FieldItem fi = DataDictionary.getFieldItem(ehrfld);
                        Element item = Applicant.element("Objective");
                        String value = "";
                        if(!"0".equalsIgnoreCase(fi.getCodesetid()))
                            value = item.element(resumefldid).elementText("ExtendValue");
                        else {
                            value = item.elementText(resumefldid);
                            if(StringUtils.isEmpty(value))
                                value = item.element(resumefldid).elementText("ExtendValue");
                        }
                            
                        value = StringUtils.isEmpty(value) ? "" : value;
                        this.cat.debug("指标" + ehrfld + "的值0：" + value);
                        value = getTextByHtml(value);
                        this.cat.debug("指标" + ehrfld + "的值1：" + value);
                        value = PubFunc.hireKeyWord_filter(value);
                        LazyDynaBean ResumeBean = new LazyDynaBean();
                        ResumeBean.set("itemtype", fi.getItemtype());
                        ResumeBean.set("itemlength", fi.getItemlength());
                        ResumeBean.set("ehrfld", ehrfld);
                        ResumeBean.set("resumefld", resumefld);
                        ResumeBean.set("resumeset", resumeSet);
                        ResumeBean.set("itemformat", fi.getFormat());
                        ResumeBean.set("setid", fi.getFieldsetid());
                        ResumeBean.set("value", value);// bean中获得的itemid就是resumeFLDXML中定义的itemid,而hm中存放的是才智创新返回来的值
                        ResumeBean.set("i9999", "0");
                        this.cat.debug("指标" + ehrfld + "的值2：" + value);
                        resumeInfoList.add(ResumeBean);
                    }
                            
                } else if ("InterviewInfoSummaries".equalsIgnoreCase(resumesetId) 
                        || "InterviewSummaries".equalsIgnoreCase(resumesetId)) {
                    if("InterviewInfoSummaries".equalsIgnoreCase(resumesetId))
                        child = Applicant.element("InterviewInfoSummaries").elements("InterviewInfo");
                    else
                        child = Applicant.element("InterviewSummaries").elements("InterviewSummary");
                    
                    for(int n = 0; n < child.size(); n++) {
                        Element item = (Element) child.get(n);
                        for (int m = 0; m < itemXmlList.size(); m++) {
                            LazyDynaBean itembean = (LazyDynaBean) itemXmlList.get(m);
                            String resumefld = (String) itembean.get("resumefld");
                            String ehrfld = (String) itembean.get("ehrfld");
                            String resumefldid = (String) itembean.get("resumefldid");
                            if (StringUtils.isEmpty(ehrfld))
                                continue;
                            
                            this.cat.debug("解析指标" + ehrfld + "的数据……");
                            FieldItem fi = DataDictionary.getFieldItem(ehrfld);
                            String value = "";
                            if(!"0".equalsIgnoreCase(fi.getCodesetid()))
                                value = item.element(resumefldid).elementText("ExtendValue");
                            else
                                value = item.elementText(resumefldid);
                            
                            if("D".equalsIgnoreCase(fi.getItemtype()) && StringUtils.isNotEmpty(value) && !"至今".equalsIgnoreCase(value)) {
                                if(value.length() > 3) {
                                    String year = value.substring(0, 4);
                                    if(("1901".compareToIgnoreCase(year)) > 0)
                                        value = "";
                                } else
                                    value = "";
                            }
                            
                            this.cat.debug("指标" + ehrfld + "的值0：" + value);
                            value = getTextByHtml(value);
                            this.cat.debug("指标" + ehrfld + "的值1：" + value);
                            value = PubFunc.hireKeyWord_filter(value);
                            LazyDynaBean ResumeBean = new LazyDynaBean();
                            ResumeBean.set("itemtype", fi.getItemtype());
                            ResumeBean.set("itemlength", fi.getItemlength());
                            ResumeBean.set("ehrfld", ehrfld);
                            ResumeBean.set("resumefld", resumefld);
                            ResumeBean.set("resumeset", resumeSet);
                            ResumeBean.set("itemformat", fi.getFormat());
                            ResumeBean.set("setid", fi.getFieldsetid());
                            ResumeBean.set("value", value);// bean中获得的itemid就是resumeFLDXML中定义的itemid,而hm中存放的是才智创新返回来的值
                            ResumeBean.set("i9999", n+"");
                            this.cat.debug("指标" + ehrfld + "的值2：" + value);
                            resumeInfoList.add(ResumeBean);
                        }
                    }
                            
                } else {
                    if(resumesetId.endsWith("Item"))
                        resumesetId = resumesetId.substring(0, resumesetId.indexOf("Item"));
                    
                    Element resumese = Applicant.element(resumesetId);
                    if(resumese == null)
                        continue;
                    
                    Element Items = resumese.element("Items");
                    if(Items == null)
                        continue;
                    
                    child = Items.elements(resumesetId + "Item");
                    for (int m = 0; m < child.size(); m++) {
                        Element item = (Element) child.get(m);
                        for (int n = 0; n < itemXmlList.size(); n++) {
                            LazyDynaBean itembean = (LazyDynaBean) itemXmlList.get(n);
                            String resumefld = (String) itembean.get("resumefld");
                            String ehrfld = (String) itembean.get("ehrfld");
                            String resumefldid = (String) itembean.get("resumefldid");
                            if (StringUtils.isEmpty(ehrfld))
                                continue;
                            
                            this.cat.debug("解析指标" + ehrfld + "的数据……");
                            FieldItem fi = DataDictionary.getFieldItem(ehrfld);
                            String value = item.elementText(resumefldid);    
                            if (!"0".equalsIgnoreCase(fi.getCodesetid()) && StringUtils.isEmpty(value)) {
                                Element codeitem = item.element(resumefldid);
                                value = codeitem.elementText("ExtendValue");
                                value = value == null ? "" : value;
                            }
                            
                            if("D".equalsIgnoreCase(fi.getItemtype()) && StringUtils.isNotEmpty(value) && !"至今".equalsIgnoreCase(value)) {
                                if(value.length() > 3) {
                                    String year = value.substring(0, 4);
                                    if(("1901".compareToIgnoreCase(year)) > 0)
                                        value = "";
                                } else
                                    value = "";
                            }
                            
                            if(StringUtils.isEmpty(value))
                                value = "";
                            
                            this.cat.debug("指标" + ehrfld + "的值0：" + value);
                            value = getTextByHtml(value);
                            this.cat.debug("指标" + ehrfld + "的值1：" + value);
                            value = PubFunc.hireKeyWord_filter(value);
                            LazyDynaBean ResumeBean = new LazyDynaBean();
                            ResumeBean.set("itemtype", fi.getItemtype());
                            ResumeBean.set("itemlength", fi.getItemlength());
                            ResumeBean.set("ehrfld", ehrfld);
                            ResumeBean.set("resumefld", resumefld);
                            ResumeBean.set("resumeset", resumeSet);
                            ResumeBean.set("itemformat", fi.getFormat());
                            ResumeBean.set("setid", fi.getFieldsetid());
                            ResumeBean.set("i9999", m + "");
                            ResumeBean.set("value", value);// bean中获得的itemid就是resumeFLDXML中定义的itemid,而hm中存放的是才智创新返回来的值
                            // 若指标等于黑名单指标
                            if (ehrfld != null && ehrfld.equalsIgnoreCase(blacklist_field))
                                blacklist_value = value;
                            
                            this.cat.debug("指标" + ehrfld + "的值2：" + value);
                            resumeInfoList.add(ResumeBean);
                        }
                    }
                }
            }
            
            HashMap resumeMap = new HashMap();
            resumeMap.put("personID", personName);
            this.cat.debug("人员姓名：" + personName);
            resumeMap.put("mainItemValue", mainItemValue);
            this.cat.debug("关键指标的值：" + mainItemValue);
            resumeMap.put("secItemValue", secItemValue);
            this.cat.debug("次关键指标的值：" + secItemValue);
            resumeMap.put("blacklist_value", blacklist_value);
            this.cat.debug("简历信息中黑名单指标对应的值：" + blacklist_value);
            resumeMap.put("ResumeInfoList", resumeInfoList);
            this.cat.debug("开始将简历信息保存到数据库……");
            addResunme(resumeMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    /**
     * 按手机号获取人员简历
     * @param phones 手机号（多个手机号时用逗号隔开）
     * @return
     */
    private String GetApplicantsByMobile(String phones) {
        String xml = "";
        try {
            if(StringUtils.isEmpty(phones))
                return "";
            
            String url = this.bsUrl + "/" + this.tenantId + "/Applicant/ByMobile/" + phones;
            this.cat.debug("获取简历数据的url：" + url);
            String param = "language=1&photo_base64=0&format=xml";
            this.cat.debug("获取简历数据的param：" + param);
            xml = HttpRequest.sendGet(this.token, url, param);
            if(xml.indexOf("<error_code>402</error_code>") > -1) {
                this.token = "Bearer " + this.token;
                xml = HttpRequest.sendGet(this.token, url, param);
            }
            
            if(xml.indexOf("<error_code>") > -1)
                RecordError(xml);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return xml;
    }
}
