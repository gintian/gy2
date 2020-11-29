package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class InitParameterSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
		//ArrayList postCardList=parameterSetBo.getRnameList();				//登记表列表
		ArrayList testTemplateList=parameterSetBo.getPerTemplateList();		//考评表列表		
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
		HashMap map=parameterXMLBo.getAttributeValues();
		
	    ArrayList hireObjectList=new ArrayList();
		ArrayList hireObjList=parameterSetBo.getCodeValueList();//取得招聘对象集合
		String cardIDs="";
		for(int i=0;i<hireObjList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)hireObjList.get(i);
			String value="";
			String temp_name="testTemplateID_"+(String)abean.get("codeitemid");
			if(map.get(temp_name)!=null&&((String)map.get(temp_name)).trim().length()>0)
				value=(String)map.get(temp_name);
			abean.set("value",value);
			hireObjectList.add(abean);
			String key="CARDTABLE_"+(String)abean.get("codeitemid");
			if(map.get(key)!=null&&((String)map.get(key)).trim().length()>0)
				cardIDs+="`"+(String)map.get(key);
			else
				cardIDs+="`#";
		}
		cardIDs=cardIDs.length()>0?cardIDs.substring(1):cardIDs;
		
		String testTemplateID="";
		//String posCardID="";
		String musterFieldIDs="";
		String musterFieldNames="";
		String hire_emailContext="";
		String orgFieldIDs="";
		String orgFieldNames="";
		String resumeStaticIds="";
		String resumeStaticNames="";
		//简历统计项指标
		if(map.get("resume_static") != null && ((String)map.get("resume_static")).trim().length()>0)
			resumeStaticIds=(String)map.get("resume_static");
		if(resumeStaticIds.trim().length()>0)
			resumeStaticNames=parameterSetBo.getResumeStaticNames(resumeStaticIds);
		
		//if(map.get("posCardID")!=null&&((String)map.get("posCardID")).trim().length()>0)
			//posCardID=(String)map.get("posCardID");
		if(map.get("fields")!=null&&((String)map.get("fields")).trim().length()>0)
			musterFieldIDs=(String)map.get("fields");
		if(musterFieldIDs.trim().length()>0)
		{
			 String eids=parameterSetBo.getExistItem(musterFieldIDs);
			 if(eids.trim().length()>0)
	        	 musterFieldNames=parameterSetBo.getmusterFieldNames(eids,0);
		}
		//单位介绍指标
		if(map.get("org_brief")!=null&&((String)map.get("org_brief")).trim().length()>0)
			orgFieldIDs=(String)map.get("org_brief");
		if(orgFieldIDs.trim().length()>0)
			orgFieldNames=parameterSetBo.getOrgFieldNames(orgFieldIDs);
		//浏览简历指标
		String resumeFieldIds = "";
		String resumeFieldNames = "";
		if(map.get("resume_field") != null && ((String)map.get("resume_field")).trim().length()>0)
			resumeFieldIds=(String)map.get("resume_field");
		if(resumeFieldIds.trim().length()>0)
			resumeFieldNames=parameterSetBo.getResumeFieldNames(resumeFieldIds);
		//简历状态指标
		String resumeStateFieldIds = "#";
		ArrayList resumeStateFieldList = new ArrayList();
		String ids = parameterSetBo.getSelectedA01Ids();
		if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
			resumeStateFieldIds=(String)map.get("resume_state");
	
		resumeStateFieldList=parameterSetBo.getResumeStateFieldList(ids);
		//招聘对象指标
		String hireObjectId ="#";
		ArrayList hireObjectParameterList = new ArrayList();
		if(parameterSetBo.getHireObjectList() != null)
			hireObjectParameterList=parameterSetBo.getHireObjectList();
		if(map.get("hire_object") != null && ((String)map.get("hire_object")).trim().length()>0)
			hireObjectId=(String)map.get("hire_object");
//		职位查询字段
		String posQueryFieldIDs=""; 
		String posQueryFieldNames="";
		if(map.get("pos_query")!=null&&((String)map.get("pos_query")).trim().length()>0)
			posQueryFieldIDs=(String)map.get("pos_query");
		if(posQueryFieldIDs.trim().length()>0)
			posQueryFieldNames=parameterSetBo.getmusterFieldNames(posQueryFieldIDs,1);
		String posCommQueryFieldIDs="";//dml 2011-6-22 10:53:09
		String posCommQueryFieldNames="";
		if(map.get("pos_com_query")!=null&&((String)map.get("pos_com_query")).trim().length()>0)
			posCommQueryFieldIDs=(String)map.get("pos_com_query");
		if(posCommQueryFieldIDs.trim().length()>0)
			posCommQueryFieldNames=parameterSetBo.getmusterFieldNames(posCommQueryFieldIDs,1);
//外网职位列表显示指标
		String pos_listfield="";
		String pos_listfieldNames="";
		if(map.get("pos_listfield")!=null&&((String)map.get("pos_listfield")).length()>0)
			pos_listfield=(String)map.get("pos_listfield");
		if(pos_listfield.length()>0)
			pos_listfieldNames=parameterSetBo.getmusterFieldNames(pos_listfield,1);
//外网职位列表显示指标排序
		String pos_listfield_sort="";
		String pos_listfield_sortNames="";
		if(map.get("pos_listfield_sort")!=null&&((String)map.get("pos_listfield_sort")).length()>0)
		    pos_listfield_sort=(String)map.get("pos_listfield_sort");
        if(pos_listfield.length()>0)
            pos_listfield_sortNames=parameterSetBo.getPosListFieldSortNames(pos_listfield_sort);
//		职位描述参数
		String viewPosFieldIDs="";
		String viewPosFieldNames="";
		
		if(map.get("view_pos")!=null&&((String)map.get("view_pos")).trim().length()>0)
			viewPosFieldIDs=(String)map.get("view_pos");
		if(viewPosFieldIDs.trim().length()>0)
			viewPosFieldNames=parameterSetBo.getmusterFieldNames(viewPosFieldIDs,1);
		
		// 单位介绍指标 || 内容形式指标
		//getOrgBrief(map);
		
		/*if(map.get("email_template")!=null&&((String)map.get("email_template")).trim().length()>0)
			hire_emailContext=(String)map.get("email_template");
		else
			hire_emailContext="(~姓名~)：\r\n    您好！\r\n   很高兴您的简历符合我们(~应聘职位~)职位的要求，如果有时间的话，请于(~面试时间~) 来我公司(~面试地点~) 面试\r\n\r\n                               世纪软件有限公司\r\n                                  (~系统时间~)";
		*/	
		//人才标识指标
		ArrayList personTypeList = new ArrayList();
		personTypeList = parameterSetBo.getPersonTypeList();
		String personTypeId ="#";
		if(map.get("person_type") != null && ((String)map.get("person_type")).trim().length()>0)
			personTypeId=(String)map.get("person_type");
		//简历评语指标
		ArrayList resumeLevelFieldList= new ArrayList();
		resumeLevelFieldList=parameterSetBo.getResumeLevelFieldList();
		String resumeLevelIds="#";
		if(map.get("resume_level") != null && ((String)map.get("resume_level")).trim().length()>0)
			resumeLevelIds=(String)map.get("resume_level");
		ArrayList markList=new ArrayList();
		if(map!=null)
			markList=(ArrayList)map.get("mark_type");
		String max_count = "";
		if(map.get("max_count")!=null)
			max_count = (String)map.get("max_count");
		ArrayList previewTableList = parameterSetBo.getAllPreviewTable();
		String previewTableId = "#";
		if(map!=null&&map.get("preview_table")!=null)
		{
			previewTableId = (String)map.get("preview_table");
		}
		String interviewingRevertItemid="#";
		if(map!=null&&map.get("interviewing_itemid")!=null)
		{
			interviewingRevertItemid=(String)map.get("interviewing_itemid");
		} 
		ArrayList getInterviewingRevertItemList=parameterSetBo.getInterviewingRevertItemList(ids);//面试回复指标
		
		String commonQueryIds = "";
		if(map!=null&&map.get("common_query")!=null)
			commonQueryIds = (String)map.get("common_query");
		String activeField="";
		if(map!=null&&map.get("active_field")!=null)
			activeField=(String)map.get("active_field");
		ArrayList activeFieldList = parameterSetBo.getCodeFieldList("A01", "45");
		this.getFormHM().put("activeField", activeField);
		this.getFormHM().put("activeFieldList", activeFieldList);
		ArrayList selectedCommonQuery = parameterSetBo.getSelectedCommonQueryCondList(commonQueryIds,"1");
		String commonQueryNames = parameterSetBo.getCommonQueryNames(selectedCommonQuery);
		String businessTemplateIds="";
		String businessTemplatenames="";
		if(map!=null&&map.get("business_template")!=null);
		{
			businessTemplateIds=(String)map.get("business_template");
		}  
		if(businessTemplateIds!=null&&businessTemplateIds.length()>0)
			businessTemplatenames=parameterSetBo.getBusniessTemplateInfo(businessTemplateIds);
		String resumeCodeValue="";
		String resumeCodeName="";
		if(map!=null&&map.get("resume_code")!=null)
		{
			resumeCodeValue=(String)map.get("resume_code");	
		}
		if(resumeCodeValue!=null&&resumeCodeValue.length()>0)
			resumeCodeName=parameterSetBo.getResumeCodeInfo(resumeCodeValue);
		String photo = "";
		String explaination="";
		String attach="";
		if(map!=null&&map.get("photo")!=null)
		    photo=(String)map.get("photo");
		if(map!=null&&map.get("explaination")!=null)
			explaination=(String)map.get("explaination");
		if(map!=null&&map.get("attach")!=null)
		{
			attach=(String)map.get("attach");
		}
		
		String cultureCode="#";
		String cultureCodeItem="#";
		if(map!=null&&map.get("culture_code")!=null)
		{
			cultureCode=(String)map.get("culture_code");
		}
		if(map!=null&&map.get("culture_item")!=null)
		{
			cultureCodeItem=(String)map.get("culture_item");
		}
		String netHref="";
		if(map!=null&&map.get("net_href")!=null)
		{
			netHref=(String)map.get("net_href");
		}
		String sms_notice="";
		if(map!=null&&map.get("sms_notice")!=null)
		{
			sms_notice=(String)map.get("sms_notice");
		}
		String new_pos_date="";
		if(map!=null&&map.get("new_pos_date")!=null)
		{
			new_pos_date=(String)map.get("new_pos_date");
		}
		ArrayList cultureList=parameterSetBo.getCodeItem(cultureCode);
		String licenseAgreementParameter=parameterSetBo.getLicenseAgreementParameter();
		String promptContentParameter=parameterSetBo.getPromptContentParameter();
		/**招聘需求上报进行工资总额控制*/
		String isCtrlReportGZ="0";
		/**招聘需求上报进行编制控制*/
		String isCtrlReportBZ="0";
		/**职位最高工资标准*/
		 String positionSalaryStandardItem="";
		 ArrayList positionSalaryStandardItemList =parameterSetBo.getPositionSalaryStandardItemList();
		
		/**面试过程是否记录*/
		 String isRemenberExamine="0";
		/**面试过程记录子集*/
		 String remenberExamineSet="";
		 ArrayList remenberExamineSetList=parameterSetBo.getRemenberExamineSetList();
		/**招聘需求支持多级审批*/
		 String moreLevelSP="0";
		 /**招聘需求审批关系*/
		 String spRelation = "";
		 /**是否只显示本级单位的招聘岗位**/
		 String hirePostByLayer="0";
		 /**是否使用复杂密码**/
	     String complexPassword="0";
	     /**密码最小长度:**/
	     String passwordMinLength="6";
	     /**密码最大长度**/
	     String passwordMaxLength="12";
	     /**最大登录失败次数**/
	     String failedTime="3";
	     /**解锁时间间隔**/
	     String unlockTime="240";
	     /**是否启动简历解析服务 0不启动 1启动**/
	     String startResumeAnalysis="0";
	     /**简历解析服务 用户名**/
	     String resumeAnalysisName="";
	     /**简历解析服务 密码**/
	     String resumeAnalysisPassword="";
	     /**简历解析服务 对外应聘职位**/
	     String resumeAnalysisForeignJob=""; 
	     
		/**招聘职位不关联组织机构*//*
		 String hirePositionNotUnionOrg="0";
		*//**招聘职位关联代码型指标*//*
		 String hirePositionItem="";
		 ArrayList hirePositionItemList=parameterSetBo.getZ03CharFieldList();
		 if(map!=null&&map.get("hirePositionNotUnionOrg")!=null)
		 {
			 hirePositionNotUnionOrg=(String)map.get("hirePositionNotUnionOrg");
		 }
		 if(map!=null&&map.get("hirePositionItem")!=null)
		 {
			 hirePositionItem=(String)map.get("hirePositionItem");
		 }*/
	     
			String title="";
			String content="";
			String commentuser="";
			String level="";
			String commentdate="";
			HashMap infoMap=null;
			if(map!=null&&map.get("remenberExamineSet")!=null)
			{
				remenberExamineSet=(String)map.get("remenberExamineSet");
			}
			if(map!=null)
			{
				infoMap=(HashMap)map.get("infoMap");
				if(infoMap!=null&&infoMap.get("title")!=null)
					title=(String)infoMap.get("title");
				if(infoMap!=null&&infoMap.get("content")!=null)
					content=(String)infoMap.get("content");
				if(infoMap!=null&&infoMap.get("level")!=null)
					level=(String)infoMap.get("level");
				if(infoMap!=null&&infoMap.get("comment_user")!=null)
					commentuser=(String)infoMap.get("comment_user");
				if(infoMap!=null&&infoMap.get("comment_date")!=null)
					commentdate=(String)infoMap.get("comment_date");
				if(remenberExamineSet!=null&&!"".equals(remenberExamineSet.trim()))
				{
					if(title!=null&&title.trim().length()>0)
					{
						FieldItem item = DataDictionary.getFieldItem(title.toLowerCase());
						if(item==null||!item.getFieldsetid().equalsIgnoreCase(remenberExamineSet))
							title="";
					}
					if(content!=null&&content.trim().length()>0)
					{
						FieldItem item = DataDictionary.getFieldItem(content.toLowerCase());
						if(item==null||!item.getFieldsetid().equalsIgnoreCase(remenberExamineSet))
							content="";
					}
					if(commentuser!=null&&commentuser.trim().length()>0)
					{
						FieldItem item = DataDictionary.getFieldItem(commentuser.toLowerCase());
						if(item==null||!item.getFieldsetid().equalsIgnoreCase(remenberExamineSet))
							commentuser="";
					}
					if(level!=null&&level.trim().length()>0)
					{
						FieldItem item = DataDictionary.getFieldItem(level.toLowerCase());
						if(item==null||!item.getFieldsetid().equalsIgnoreCase(remenberExamineSet))
							level="";
					}
					if(commentdate!=null&&commentdate.trim().length()>0)
					{
						FieldItem item = DataDictionary.getFieldItem(commentdate.toLowerCase());
						if(item==null||!item.getFieldsetid().equalsIgnoreCase(remenberExamineSet))
							commentdate="";
					}
				}else
				{
					title="";
					content="";
					commentuser="";
					level="";
					commentdate="";
				}
			}
		    //简历解析属性
			HashMap resumeAnalysisMap=null;
		    if(map!=null)
			{
		    	resumeAnalysisMap=(HashMap)map.get("resumeAnalysisMap");
				if(resumeAnalysisMap!=null&&resumeAnalysisMap.get("resumeAnalysisName")!=null)
					resumeAnalysisName=(String)resumeAnalysisMap.get("resumeAnalysisName");
				if(resumeAnalysisMap!=null&&resumeAnalysisMap.get("resumeAnalysisPassword")!=null)
					resumeAnalysisPassword=(String)resumeAnalysisMap.get("resumeAnalysisPassword");
				if(resumeAnalysisMap!=null&&resumeAnalysisMap.get("resumeAnalysisForeignJob")!=null)
					resumeAnalysisForeignJob=(String)resumeAnalysisMap.get("resumeAnalysisForeignJob");

			}
		    
		 if(map!=null&&map.get("hirePostByLayer")!=null)
		 {
			 hirePostByLayer=(String)map.get("hirePostByLayer");
		 }
		 if(map!=null&&map.get("complexPassword")!=null)
		 {
			 complexPassword=(String)map.get("complexPassword");
		 }
		 if(map!=null&&map.get("passwordMinLength")!=null)
		 {
			 passwordMinLength=(String)map.get("passwordMinLength");
		 }
		 if(map!=null&&map.get("passwordMaxLength")!=null)
		 {
			 passwordMaxLength=(String)map.get("passwordMaxLength");
		 }
		 if(map!=null&&map.get("failedTime")!=null)
		 {
			 failedTime=(String)map.get("failedTime");
		 }
		 if(map!=null&&map.get("unlockTime")!=null)
		 {
			 unlockTime=(String)map.get("unlockTime");
		 }
		 if(map!=null&&map.get("moreLevelSP")!=null)
		 {
			 moreLevelSP=(String)map.get("moreLevelSP");
		 }
		 if(map!=null&&map.get("spRelation")!=null)
		 {
			 spRelation=(String)map.get("spRelation");
		 }
		if(map!=null&&map.get("isCtrlReportGZ")!=null)
		{
			isCtrlReportGZ=(String)map.get("isCtrlReportGZ");
		}
		if(map!=null&&map.get("isCtrlReportBZ")!=null)
		{
			isCtrlReportBZ=(String)map.get("isCtrlReportBZ");
		}
		if(map!=null&&map.get("positionSalaryStandardItem")!=null)
		{
			positionSalaryStandardItem=(String)map.get("positionSalaryStandardItem");
		}
		if(map!=null&&map.get("isRemenberExamine")!=null)
		{
			isRemenberExamine=(String)map.get("isRemenberExamine");
		}
		if(map!=null&&map.get("startResumeAnalysis")!=null)
		{
			startResumeAnalysis=(String)map.get("startResumeAnalysis");
		}
		/**单位部门预算表*/
		 String orgWillTableId="#";
		 ArrayList orgWillTableList =parameterSetBo.getOrgRegisterTable();
		 if(map!=null&&map.get("orgWillTableId")!=null)
		 {
			 orgWillTableId=(String)map.get("orgWillTableId");
		 }
		//准考证登记表
		String admissionCard="#";
		 if(map!=null&&map.get("admissionCard")!=null)
		 {
			 admissionCard=(String)map.get("admissionCard");
		 }
		 this.getFormHM().put("admissionCard", admissionCard);
		 String positionNumber="";//外网每个单位下显示职位条数
		 if(map!=null&&map.get("positionNumber")!=null)
		 {
			 positionNumber=(String)map.get("positionNumber");
		 }
		
		 
		 String schoolPosition="";
		 if(map!=null&&map.get("schoolPosition")!=null)
			 schoolPosition=(String)map.get("schoolPosition");
		 String schoolPosDesc = "";
		 if(schoolPosition!=null&&schoolPosition.length()>0)
			 schoolPosDesc=parameterSetBo.getSchoolPositionDesc(schoolPosition);
		 String workExperience="";
		 if(map!=null&&map.get("workExperience")!=null)
			 workExperience=(String)map.get("workExperience");
		 ArrayList workExperienceList = parameterSetBo.getCodeFieldList("A01", "45");
		 String hireMajor="";
		 if(map!=null&&map.get("hireMajor")!=null)
		 {
			 hireMajor=(String)map.get("hireMajor");
		 }
		 String hireMajorCode="";
		 if(map!=null&&map.get("hireMajorCode")!=null)
		 {
			 hireMajorCode=(String)map.get("hireMajorCode");
		 }
		 ArrayList hireMajorList = parameterSetBo.getZ03CharFieldList2();
		 ArrayList hireMajorCodeList = parameterSetBo.getHireMajorCodeList();
		 String acountBeActived="0";
		 if(map!=null&&map.get("acountBeActived")!=null)
			 acountBeActived=(String)map.get("acountBeActived");
	     String answerSet="";
		 if(map!=null&&map.get("answerSet")!=null)
	    	 answerSet=(String)map.get("answerSet");
		 ArrayList answerSetList2 = parameterSetBo.getRemenberExamineSetList();
		 //开放行问答下的子集 start
		 ArrayList answerSetList = new ArrayList();
		 answerSetList.add(new CommonData("","请选择..."));
		 for(int i=0;i<answerSetList2.size();i++)
	     {
			 CommonData  comdate = (CommonData)answerSetList2.get(i);
			 String fieldsetid =comdate.getDataValue();
			 RecordVo vo= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
			 if(vo != null){
		           String setlist=vo.getString("str_value");
		          
		           ArrayList infoSetList=splitField(setlist);
		           for(int j=0;j<infoSetList.size();j++){
		        	   String set=(String)infoSetList.get(j);
		        	   String id="";
		        	   if(set.indexOf("[")!=-1)
		        		   id=set.substring(0,set.indexOf("["));
		        	   else
		        		   id=set;
		        	   if(id.equals(fieldsetid)){
		        		   answerSetList.add(comdate);
		        	   }
		           }
		          
		        }
	      }
		 
		 ArrayList foreignJobList=new ArrayList();
		 foreignJobList=parameterSetBo.getForeignJobList();
		 ArrayList approvelist = approveList();
		 String isCharField = parameterSetBo.getIsCharField(hireMajor);//招聘专业是否是字符型字段
		//开放行问答下的子集end
		 this.getFormHM().put("hirePostByLayer", hirePostByLayer);
		 this.getFormHM().put("complexPassword", complexPassword);
		 this.getFormHM().put("passwordMinLength", passwordMinLength);
		 this.getFormHM().put("passwordMaxLength", passwordMaxLength);
		 this.getFormHM().put("failedTime", failedTime);
		 this.getFormHM().put("unlockTime", unlockTime);
		 this.getFormHM().put("answerSet", answerSet);
		 this.getFormHM().put("answerSetList", answerSetList);
		 this.getFormHM().put("foreignJobList", foreignJobList);
		 this.getFormHM().put("hireMajor", hireMajor);
		 this.getFormHM().put("hireMajorCode", hireMajorCode);//招聘专业代码 郭峰增加
		 this.getFormHM().put("hireMajorList", hireMajorList);
		 this.getFormHM().put("hireMajorCodeList", hireMajorCodeList);
		 this.getFormHM().put("workExperience", workExperience);
		 this.getFormHM().put("workExperienceList", workExperienceList);
		 this.getFormHM().put("schoolPosition", schoolPosition);
		 this.getFormHM().put("schoolPosDesc", schoolPosDesc);
		this.getFormHM().put("isCtrlReportBZ", isCtrlReportBZ);
		this.getFormHM().put("isCtrlReportGZ", isCtrlReportGZ);
		this.getFormHM().put("moreLevelSP",moreLevelSP);
		this.getFormHM().put("titleField", title);
		this.getFormHM().put("contentField", content);
		this.getFormHM().put("levelField", level);
		this.getFormHM().put("commentDateField", commentdate);
		this.getFormHM().put("commentUserField", commentuser);
		this.getFormHM().put("orgWillTableList", orgWillTableList);
		this.getFormHM().put("orgWillTableId", orgWillTableId);
        this.getFormHM().put("startResumeAnalysis", startResumeAnalysis);
        this.getFormHM().put("resumeAnalysisName", resumeAnalysisName);
        this.getFormHM().put("resumeAnalysisPassword", resumeAnalysisPassword);
        this.getFormHM().put("resumeAnalysisForeignJob", resumeAnalysisForeignJob);
        this.getFormHM().put("isRemenberExamine", isRemenberExamine);
		this.getFormHM().put("remenberExamineSet", remenberExamineSet);
		this.getFormHM().put("remenberExamineSetList", remenberExamineSetList);
		this.getFormHM().put("positionSalaryStandardItemList", positionSalaryStandardItemList);
		this.getFormHM().put("positionSalaryStandardItem",positionSalaryStandardItem);
		this.getFormHM().put("interviewingRevertItemList", getInterviewingRevertItemList);
		this.getFormHM().put("interviewingRevertItemid", interviewingRevertItemid);
		this.getFormHM().put("licenseAgreementParameter", licenseAgreementParameter);
		this.getFormHM().put("cultureCodeList", resumeLevelFieldList);
		this.getFormHM().put("cultureList", cultureList);
		this.getFormHM().put("cultureCode", cultureCode);
		this.getFormHM().put("cultureCodeItem", cultureCodeItem);
		this.getFormHM().put("resumeCodeValue", resumeCodeValue);
		this.getFormHM().put("resumeCodeName", resumeCodeName);
		this.getFormHM().put("attach",attach);
		this.getFormHM().put("explaination",explaination);
		this.getFormHM().put("photo",photo);
		this.getFormHM().put("commonQueryIds", commonQueryIds);
		this.getFormHM().put("commonQueryNames", commonQueryNames);
		
		this.getFormHM().put("previewTableList",previewTableList);
		this.getFormHM().put("previewTableId", previewTableId);
		this.getFormHM().put("markList",markList);
		this.getFormHM().put("max_count",max_count);
		this.getFormHM().put("hire_emailContext",hire_emailContext);
		this.getFormHM().put("hireObjectList",hireObjectList);
		this.getFormHM().put("testTemplateID",testTemplateID);
		this.getFormHM().put("testTemplateList",testTemplateList);
		//this.getFormHM().put("posCardID",posCardID);
		//this.getFormHM().put("postCardList",postCardList);
		this.getFormHM().put("musterFieldIDs",musterFieldIDs);
		this.getFormHM().put("musterFieldNames",musterFieldNames);
		this.getFormHM().put("posQueryFieldIDs",posQueryFieldIDs);
		this.getFormHM().put("posQueryFieldNames",posQueryFieldNames);
		
		this.getFormHM().put("posCommQueryFieldIDs",posCommQueryFieldIDs);
		this.getFormHM().put("posCommQueryFieldNames",posCommQueryFieldNames);
		
		this.getFormHM().put("viewPosFieldIDs",viewPosFieldIDs);
		this.getFormHM().put("viewPosFieldNames",viewPosFieldNames);
		
		this.getFormHM().put("orgFieldNames",orgFieldNames);
		this.getFormHM().put("orgFieldIDs",orgFieldIDs);
		
		this.getFormHM().put("resumeFieldNames",resumeFieldNames);
		//this.getFormHM().put("resumeStateFieldNames",resumeStateFieldNames);
		this.getFormHM().put("resumeStateFieldsList",resumeStateFieldList);
		this.getFormHM().put("resumeFieldIds",resumeFieldIds);
		this.getFormHM().put("resumeStateFieldIds",resumeStateFieldIds);
		this.getFormHM().put("personTypeList",personTypeList);
		this.getFormHM().put("personTypeId",personTypeId);
		this.getFormHM().put("resumeLevelFieldList",resumeLevelFieldList);
		this.getFormHM().put("resumeLevelIds",resumeLevelIds);
		this.getFormHM().put("resumeStaticIds",resumeStaticIds);
		this.getFormHM().put("resumeStaticNames",resumeStaticNames);
		this.getFormHM().put("hireObjectId",hireObjectId);
		this.getFormHM().put("hireObjectParameterList",hireObjectParameterList);
		this.getFormHM().put("businessTemplateIds", businessTemplateIds);
		this.getFormHM().put("businessTemplatenames", businessTemplatenames);
		this.getFormHM().put("netHref", netHref);
		this.getFormHM().put("promptContentParameter", promptContentParameter);
		this.getFormHM().put("positionNumber", positionNumber);
		this.getFormHM().put("pos_listfield", pos_listfield);
		this.getFormHM().put("pos_listfield_sort", pos_listfield_sort);
		this.getFormHM().put("pos_listfieldNames", pos_listfieldNames);
		this.getFormHM().put("pos_listfield_sortNames", pos_listfield_sortNames);
		this.getFormHM().put("smg", sms_notice);
		this.getFormHM().put("newTime", new_pos_date);
		this.getFormHM().put("acountBeActived", acountBeActived);
		this.getFormHM().put("cardIDs", cardIDs);
		this.getFormHM().put("isCharField", isCharField);//招聘专业是否是字符型字段
		this.getFormHM().put("approvelist", approvelist);
		this.getFormHM().put("spRelation", spRelation);
	}
	
	
	//单位介绍指标 || 内容形式指标
	public void getOrgBrief(HashMap map)
	{
		try
		{
			if(map.get("org_brief")!=null&&((String)map.get("org_brief")).trim().length()>0)
			{
			
				String temp=(String)map.get("org_brief");
				String[] temps=temp.split(",");
				this.getFormHM().put("orgFieldIDs",temps[0]);
				this.getFormHM().put("contentType",temps[1]);
				
					String sql="select itemdesc,itemid from fielditem where fieldsetid='B01' where itemid='"+temps[0]+"' or itemid='"+temps[1]+"'";
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					this.frowset=dao.search(sql);
					while(this.frowset.next());
					{
						String itemid=this.frowset.getString("itemid");
						String itemdesc=this.frowset.getString("itemdesc");
						if(itemid.equalsIgnoreCase(temps[0]))
						{
							temps[0]=itemdesc;
						}
						if(itemid.equalsIgnoreCase(temps[1]))
						{
							temps[1]=itemdesc;
						}
					}
					this.getFormHM().put("orgFieldNames","单位介绍指标:"+temps[0]+"  内容形式指标:"+temps[1]);
			}
			else
			{
				this.getFormHM().put("orgFieldIDs","");
				this.getFormHM().put("contentType","");
				this.getFormHM().put("orgFieldNames","");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	/**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        if(!",".equals(strfields.substring(strfields.length()))){
        	strfields=strfields+",";
        } 
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();
            list.add(fieldname);
        }
        return list;
    }
    /**
     * 获取审批关系列表
     * @return
     */
	private ArrayList approveList() {

		ArrayList approvelist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "select relation_id,cname from t_wf_relation where validflag = '1'";
		try {
			CommonData data = new CommonData("", ResourceFactory.getProperty("label.select.dot"));
			approvelist.add(data);
			
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				data = new CommonData(this.frowset.getString("relation_id"), 
						this.frowset.getString("cname"));
				approvelist.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return approvelist;
	}
}
