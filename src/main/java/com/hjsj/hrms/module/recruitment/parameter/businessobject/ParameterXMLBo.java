package com.hjsj.hrms.module.recruitment.parameter.businessobject;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.utils.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;


/*
 * 在常量中增中一个参数ZP_PARAMTER,参数格式如下：
	<?xml version='1.0' encoding="GB2312"?>
	<zp_para>
		<out_fields Fields="A0405`A0441`…">
	    </ out_fields >
	<test_template >1</test_template >
	<pos_card>1</pos_card />
	</zp_para>

 */
public class ParameterXMLBo {
	private Connection conn;
	private String xml;
	private Document doc;
	public static  HashMap  hm;
	private String  flag="0";
	private String smg="";
	private String newTime="";
	DbSecurityImpl dbS = new DbSecurityImpl();
	public String getSmg() {
		return smg;
	}
	private String posCommQueryFieldIDs="";//dml 2011-6-22 10:57:44
	/** 考试成绩模板 **/
	private String scoreCard = "";
	/** 外网已申请职位列表显示指标集  **/
	private String appliedPosItems = "";
	/*“单位”或“单位、部门”进行职位的分组显示*/
	private String unitOrDepart = "";
	
	private String attachCodeset = "";
	
	private String attachHire = "";
	
	 /**渠道授权设置*/
    private String hireChannelPriv;
    
	/*应聘者身份指标*/
	private String candidate_status = "";
	
	/*证件类型指标*/
	private String certificate_type = "";
	
	//入职人员库
	private String destNbase = "";
	
	//注册帐号截止时间
	private String register_endtime = "";
	
	public String getRegister_endtime() {
		return register_endtime;
	}

	public void setRegister_endtime(String register_endtime) {
		this.register_endtime = register_endtime;
	}

	public String getCertificate_type() {
		return certificate_type;
	}

	public void setCertificate_type(String certificate_type) {
		this.certificate_type = certificate_type;
	}

	public String getCandidate_status() {
		return candidate_status;
	}

	public void setCandidate_status(String candidate_status) {
		this.candidate_status = candidate_status;
	}

	public String getUnitOrDepart() {
		return unitOrDepart;
	}

	public void setUnitOrDepart(String unitOrDepart) {
		this.unitOrDepart = unitOrDepart;
	}

	public String getPosCommQueryFieldIDs() {
		return posCommQueryFieldIDs;
	}

	public void setPosCommQueryFieldIDs(String posCommQueryFieldIDs) {
		this.posCommQueryFieldIDs = posCommQueryFieldIDs;
	}

	public void setSmg(String smg) {
		this.smg = smg;
	}

	public ParameterXMLBo(){
	}
	
	public ParameterXMLBo(Connection conn)
	{
		this.conn=conn;
		this.initXML();
	}
	
	public ParameterXMLBo(Connection conn,String aflag)
	{
		this.conn=conn;
		flag=aflag;
	}
	
	
	/**
	 * 查询DB 获取XML文件字符串
	 */
	private void initXML(){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			//常量表中查找rp_param常量
			rs=dao.search("select str_value  from CONSTANT where UPPER(CONSTANT)='ZP_PARAMTER'");
			if(rs.next()){
				//获取XML文件
				xml = Sql_switcher.readMemo(rs,"STR_VALUE");
				//System.out.println(xml);
			}
			else
			{
				StringBuffer strxml=new StringBuffer();
				strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
				strxml.append("<zp_para>");
				strxml.append("</zp_para>");	
				xml=strxml.toString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private void init() throws GeneralException{
		try {
			doc = PubFunc.generateDom(xml);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
	}
	public String getParam()
	{
		String str="";
		try{
			this.initXML();
			this.init();
			String path="/zp_para/demand_post";
			XPath xPath = XPath.newInstance(path);
			Element element=(Element)xPath.selectSingleNode(this.doc);
			if(element!=null)
			{
				str=element.getText();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public void saveOrDelParam(String param)
	{
		PreparedStatement pstmt = null;	
		try
		{
			this.initXML();
			this.init();
			String path="/zp_para/demand_post";
			XPath xPath = XPath.newInstance(path);
			Element element=(Element)xPath.selectSingleNode(this.doc);
			if(element!=null)
			{
				this.doc.getRootElement().removeContent(element);
			}
			element = new Element("demand_post");
			element.setText(param);
			this.doc.getRootElement().addContent(element);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String str_value= outputter.outputString(doc);
			StringBuffer strsql = new StringBuffer("");
			if(isHave())
			{
				strsql.append("update constant set str_value=? where constant='ZP_PARAMTER'");
				pstmt = this.conn.prepareStatement(strsql.toString());	
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, str_value);
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
			   }
			}else
			{
				strsql.append("insert into constant(constant,type,describe,str_value) values(?,?,?,?)");	
				pstmt = this.conn.prepareStatement(strsql.toString());				
				pstmt.setString(1, "ZP_PARAMTER");
				pstmt.setString(2, "A");
				pstmt.setString(3,ResourceFactory.getProperty("hire.parameterSet.parameterSet"));
				pstmt.setString(4,str_value);
			}
			// 打开Wallet
			dbS.open(conn, strsql.toString());
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try
			{
				if(pstmt!=null)
					pstmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**, String posCardID职位说明书被拿掉了*/
	public void insertParam(String fields,String testTemplateID,String posQueryFieldIDs,
			String viewPosFieldIDs,String resumeFieldsIds,String resumeStateFieldsIds,
			String personTypeId,String resumeLevelId,String resumeStaticIds,String hireObjectId,
			String mark_type,String max_count,String previewTableId,String commonQueryIds,String photo,
			String explaination,String attach,String businessTemplateIds,String resumeCodeValue,
			String cultureCode,String cultureCodeItem,String netHref,String interviewingRevertItemid,
			String isCtrlReportGZ,String isCtrlReportBZ,String positionSalaryStandardItem,
			String isRemenberExamine,String remenberExamineSet,String orgWillTableId,HashMap infoMap,String moreLevelSP
			,String hirePositionItem,String hirePositionNotUnionOrg,String activeField,String admissionCard,String positionNumber,HashMap property
			,String hirePostByLayer,String complexPassword,String passwordMinLength,String passwordMaxLength,String failedTime,String unlockTime
			,String startResumeAnalysis,String resumeAnalysisName,String resumeAnalysisPassword,String resumeAnalysisForeignJob,String spRelation,
			String maxFileSize, String unitLevel)
	{
		String orgFieldIDs=this.getBriefParaValue();
		StringBuffer strsql = new StringBuffer();
		String str_value=createParamXML(fields,testTemplateID,posQueryFieldIDs,viewPosFieldIDs,orgFieldIDs,
				resumeFieldsIds,resumeStateFieldsIds,personTypeId,resumeLevelId,resumeStaticIds,hireObjectId,
				mark_type,max_count,previewTableId,commonQueryIds,photo,explaination,attach,
				businessTemplateIds,resumeCodeValue,cultureCode,cultureCodeItem,netHref,
				interviewingRevertItemid,isCtrlReportGZ,isCtrlReportBZ,positionSalaryStandardItem,isRemenberExamine
				,remenberExamineSet,orgWillTableId,infoMap,moreLevelSP,hirePositionItem,hirePositionNotUnionOrg,activeField,admissionCard,positionNumber,property
				,hirePostByLayer,complexPassword,passwordMinLength,passwordMaxLength,failedTime,unlockTime
				,startResumeAnalysis,resumeAnalysisName,resumeAnalysisPassword,resumeAnalysisForeignJob,spRelation, maxFileSize, unitLevel);
		ContentDAO dao=new ContentDAO(this.conn);
		PreparedStatement pstmt = null;	
		try
		{
			//dao.delete("delete from constant where constant='ZP_PARAMTER'",new ArrayList());
			if(isHave())
			{
				strsql.append("update constant set str_value=? where constant='ZP_PARAMTER'");
				pstmt = this.conn.prepareStatement(strsql.toString());	
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, str_value);
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
			   }
			}else
			{
				strsql.append("insert into constant(constant,type,describe,str_value) values(?,?,?,?)");	
				pstmt = this.conn.prepareStatement(strsql.toString());				
				pstmt.setString(1, "ZP_PARAMTER");
				pstmt.setString(2, "A");
				pstmt.setString(3,ResourceFactory.getProperty("hire.parameterSet.parameterSet"));
				pstmt.setString(4,str_value);
			}
			// 打开Wallet
			dbS.open(conn, strsql.toString());
			pstmt.executeUpdate();
			/* RecordVo vo=new RecordVo("constant");
			 vo.setString("constant","ZP_PARAMTER");
			 vo.setString("type","A");
			 vo.setString("describe",ResourceFactory.getProperty("hire.parameterSet.parameterSet"));
			 vo.setString("str_value",str_value);
			 dao.addValueObject(vo);*/
			 
			 hm=null;
			 initXML();
			 getAttributeValues();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException ee) {
				ee.printStackTrace();
			}			
		}
		
		
	}
	
	
//	创建用户相关配置文件
	private String createParamXML(String fields,String testTemplateID,String posQueryFieldIDs,
			String viewPosFieldIDs,String orgFieldIDs,String resumeFieldsIds,String resumeStateFieldsIds,
			String personTypeId,String resumeLevelId,String resumeStaticIds,String hireObjectId,
			String mark_type,String maxcount,String previewTableId,String commonQueryIds,String photo,
			String explaination,String attach,String businessTemplateIds,String resumeCodeValue,
			String cultureCode,String cultureCodeItem,String href,String interviewingRevertItemid,
			String isCtrlReportGZ,String isCtrlReportBZ,String positionSalaryStandardItem
			,String isRemenberExamine
			,String remenberExamineSet,String orgWillTableId,HashMap infoMap,String moreLevelSP
			,String hirePositionItem,String hirePositionNotUnionOrg,String activeField,String admissionCard,String positionNumber,HashMap property
			,String hirePostByLayer,String complexPassword,String passwordMinLength,String passwordMaxLength,String failedTime,String unlockTime
			,String startResumeAnalysis,String resumeAnalysisName,String resumeAnalysisPassword,String resumeAnalysisForeignJob,String spRelation,
			String maxFileSize, String unitLevel)
	{
		String temp = null;
		
		Element zp_para = new Element("zp_para");
		Element out_fields = new Element("out_fields");
		//Element pos_card = new Element("pos_card");
		Element org_brief=new Element("org_brief");
		
		Element a_posQueryFieldIDs=new Element("pos_query");
		Element a_viewPosFieldIDs=new Element("view_pos");
		Element resume_field = new Element("resume_field");
		Element resume_state = new Element("resume_state");
		Element person_type = new Element("person_type");
		Element resume_level = new Element("resume_level");
		Element resume_static = new Element("resume_static");
		Element hire_object = new Element("hire_object");
		Element max_count  = new Element("max_count");
		Element preview_table = new Element("preview_table");
		Element common_query = new Element("common_query");
		Element pho = new Element("photo");
		Element exp = new Element("explaination");
		out_fields.setAttribute("Fields",fields);
		Element att = new Element("attach");
		Element b_t=new Element("business_template");
		Element resume_codevalue=new Element("resume_code");
		Element culture_code=new Element("culture_code");
		culture_code.setAttribute("code", cultureCode);
		culture_code.setAttribute("item",cultureCodeItem);
		Element netHref=new Element("net_href");
		netHref.addContent(href);
		Element interviewing_itemid = new Element("interviewing_itemid");
		interviewing_itemid.addContent(interviewingRevertItemid);
		Element reportGZ = new Element("isCtrlReportGZ");
		Element reportBZ = new Element("isCtrlReportBZ");
		Element active_field = new Element("active_field");
		Element smg1=null;
		if(this.smg!=null&&this.smg.length()!=0){
			smg1 = new Element("sms_notice");
			smg1.addContent(smg);
		}
		Element new_pos_date=null;
		if(this.newTime!=null&&this.newTime.length()!=0){
			new_pos_date = new Element("new_pos_date");
			new_pos_date.addContent(newTime);
		}
		Element pos_com_query=null;//dml 2011-6-22 10:58:19
		if(this.posCommQueryFieldIDs!=null&&this.posCommQueryFieldIDs.length()!=0){
			pos_com_query=new Element("pos_com_query");
			pos_com_query.addContent(this.posCommQueryFieldIDs);
		}
		active_field.addContent(activeField);
		reportGZ.addContent(isCtrlReportGZ);
		reportBZ.addContent(isCtrlReportBZ);
		reportGZ.setAttribute("item", positionSalaryStandardItem);
		Element RemenberExamine = new Element("isRemenberExamine");
		RemenberExamine.addContent(isRemenberExamine);
		RemenberExamine.setAttribute("item", remenberExamineSet);
		
		String title="";
		String content="";
		String commentuser="";
		String level="";
		String commentdate="";
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
		RemenberExamine.setAttribute("title", title);
		RemenberExamine.setAttribute("content", content);
		RemenberExamine.setAttribute("level", level);
		RemenberExamine.setAttribute("comment_date", commentdate);
		RemenberExamine.setAttribute("comment_user", commentuser);
		/*******简历解析服务xml*******/
		Element startResumeAnalysisElement = new Element("startResumeAnalysis");
		startResumeAnalysisElement.addContent(startResumeAnalysis);
		startResumeAnalysisElement.setAttribute("resumeAnalysisName",resumeAnalysisName);
		startResumeAnalysisElement.setAttribute("resumeAnalysisPassword",resumeAnalysisPassword);
		startResumeAnalysisElement.setAttribute("resumeAnalysisForeignJob",resumeAnalysisForeignJob);
		
		Element scoreCardElement = new Element("scoreCard");
		scoreCardElement.addContent(scoreCard);
		
		Element WillTableId=new Element("orgWillTableId");
		WillTableId.addContent(orgWillTableId);
		
		Element AdmissionCard=new Element("admissionCard");
		AdmissionCard.addContent(admissionCard);
		
		Element appliedPosItemsElement = new Element("appliedPosItems");
		appliedPosItemsElement.addContent(appliedPosItems);
		
		Element position_number = new Element("position_number");
		position_number.addContent(positionNumber);
		Element moreLevel = new Element("moreLevelSP");
		moreLevel.addContent(moreLevelSP);
		Element spRelationl = new Element("spRelation");
		spRelationl.addContent(spRelation);
		Element hirePostByLayer1=new Element("hirePostByLayer");
		hirePostByLayer1.addContent(hirePostByLayer);
		Element complexPassword1=new Element("complexPassword");
		complexPassword1.addContent(complexPassword);
		Element passwordMinLength1=new Element("passwordMinLength");
		passwordMinLength1.addContent(passwordMinLength);
		Element passwordMaxLength1=new Element("passwordMaxLength");
		passwordMaxLength1.addContent(passwordMaxLength);
		Element failedTime1=new Element("failedTime");
		failedTime1.addContent(failedTime);
		Element unlockTime1=new Element("unlockTime");
		unlockTime1.addContent(unlockTime);
		Element PositionNotUnionOrg = new Element("hirePositionNotUnionOrg");
		PositionNotUnionOrg.setAttribute("item",hirePositionItem);
		PositionNotUnionOrg.addContent(hirePositionNotUnionOrg);
		//pos_card.addContent(posCardID);
		org_brief.addContent(orgFieldIDs);
		//org_brief.addContent("D0103,D0104");
		a_posQueryFieldIDs.addContent(posQueryFieldIDs);
		a_viewPosFieldIDs.addContent(viewPosFieldIDs);
		zp_para.addContent(out_fields);
		max_count.addContent(maxcount);
		pho.addContent(photo);
		exp.addContent(explaination);
		att.addContent(attach);
		common_query.addContent(commonQueryIds);
		b_t.addContent(businessTemplateIds);
		resume_codevalue.addContent(resumeCodeValue);
		preview_table.addContent(previewTableId);
		resume_field.addContent(resumeFieldsIds);
		resume_state.addContent(resumeStateFieldsIds);
		person_type.addContent(personTypeId);
		resume_level.addContent(resumeLevelId);
		resume_static.addContent(resumeStaticIds);
		hire_object.addContent(hireObjectId);
		
		if(testTemplateID.indexOf("~")==-1)
		{
			Element test_template = new Element("test_template");
			String [] tmp=testTemplateID.split("\\^");
			String aa="";
			if(testTemplateID.split("\\^").length>1){
				aa=tmp[1];
				if("#".equals(aa))
					aa="";
			}
			test_template.setAttribute("hire_obj_code",testTemplateID.split("\\^")[0]);
			test_template.setAttribute("mark_type",mark_type);
			test_template.addContent(aa);
			zp_para.addContent(test_template);
		}
		else
		{
			String[] temps=testTemplateID.split("~");
			String[] temp_mark=mark_type.split("#");
			for(int i=0;i<temps.length;i++)
			{
				Element test_template = new Element("test_template");
				String value="";
				if(temps[i].split("\\^").length>1)
					value= "#".equals(temps[i].split("\\^")[1])?"":temps[i].split("\\^")[1];
				test_template.setAttribute("hire_obj_code",temps[i].split("\\^")[0]);
				if(temp_mark.length>1)
					test_template.setAttribute("mark_type",temp_mark[i]);
				else
					test_template.setAttribute("mark_type","");
				test_template.addContent(value);
				zp_para.addContent(test_template);
			}
		}
		try
		{
	    	HashMap map = this.getAttributeValues();
    		String lftype="0";
	    	String hbtype="0";
           if(map.get("lftype")!=null)
            	lftype=(String)map.get("lftype");
           if(map.get("hbtype")!=null)
           {
             	hbtype=(String)map.get("hbtype");
           }
           Element lftypeE=new Element("lftype");
           lftypeE.setText(lftype);
           zp_para.addContent(lftypeE);
           Element hbtypeE=new Element("hbtype");
           hbtypeE.setText(hbtype);
           zp_para.addContent(hbtypeE);
           /**保存参数设置时会把高级测评方案的数据给冲掉,因此在数据中重新给加上 begin xcs add@2014-7-31,不知道为何当初选择全覆盖的方式？？**/
           if(map.get("testTemplatAdvance")!=null){
               ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
               for(int i=0;i<testTemplatAdvance.size();i++){
                   HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
                   String hire_obj_code=(String) advanceMap.get("hire_obj_code");//得到招聘渠道 01：校园招聘 02：社招 03：内招
                   String interview=(String) advanceMap.get("interview");//得到面试方式 1：初试 2：复试
                   String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
                   String  templateId=(String) advanceMap.get("templateId");//得到模版号
                   String markType =(String) advanceMap.get("mark_type");
                   Element test_template_advanced=new Element("test_template_advanced");
                   test_template_advanced.setAttribute("hire_obj_code", hire_obj_code);//设置招聘方式
                   test_template_advanced.setAttribute("interview", interview);//设置测评阶段
                   test_template_advanced.setAttribute("score_item", score_item);//分值对应指标
                   test_template_advanced.setAttribute("mark_type", markType);//是否采用混合打分
                   test_template_advanced.setText(templateId);//设置的模版名称id
                   zp_para.addContent(test_template_advanced);
               }
           }
           /**保存参数设置时会把高级测评方案的数据给冲掉,因此在数据中重新给加上 end xcs add@2014-7-31**/

		}catch(Exception e)
        {
        	e.printStackTrace();
        }
		Element maxFileSizeEle = new Element("maxFileSize");
		maxFileSizeEle.addContent(maxFileSize);
		Element attachCodesetEle = new Element("attach_codeset");
	    attachCodesetEle.addContent(attachCodeset);
	    Element attachHireEle = new Element("attach_hire_object");
	    attachHireEle.addContent(attachHire);
	    Element hireChannelPrivEle = new Element("hire_object_priv");
	    hireChannelPrivEle.addContent(hireChannelPriv);
		Element unitLevelEle = new Element("unitLevel");
		unitLevelEle.addContent(unitLevel);
		Element unitOrdepartEle = new Element("unitOrDepart");
		unitOrdepartEle.addContent(unitOrDepart);
		Element candidate_statusEle = new Element("candidate_status");
		candidate_statusEle.addContent(candidate_status);
		Element certificate_typeEle = new Element("id_type");
		certificate_typeEle.addContent(certificate_type);
		Element register_endtimeEle = new Element("register_endtime");
		register_endtimeEle.addContent(register_endtime);
		
		
		zp_para.addContent(org_brief);
		//zp_para.addContent(pos_card);
		zp_para.addContent(a_posQueryFieldIDs);
		zp_para.addContent(a_viewPosFieldIDs);
		zp_para.addContent(resume_field);
		zp_para.addContent(resume_state);
		zp_para.addContent(person_type);
		zp_para.addContent(resume_level);
		zp_para.addContent(resume_static);
		zp_para.addContent(hire_object);
		zp_para.addContent(max_count);
		zp_para.addContent(preview_table);
		zp_para.addContent(common_query);
		zp_para.addContent(pho);
		zp_para.addContent(exp);
		zp_para.addContent(att);
		zp_para.addContent(b_t);
		zp_para.addContent(resume_codevalue);
		zp_para.addContent(culture_code);
		zp_para.addContent(netHref);
		zp_para.addContent(interviewing_itemid);
		zp_para.addContent(reportGZ);
		zp_para.addContent(reportBZ);
		zp_para.addContent(RemenberExamine);
		zp_para.addContent(startResumeAnalysisElement);
		zp_para.addContent(WillTableId);
		zp_para.addContent(AdmissionCard);
		zp_para.addContent(scoreCardElement);
		zp_para.addContent(appliedPosItemsElement);
		zp_para.addContent(position_number);
		zp_para.addContent(moreLevel);
		zp_para.addContent(spRelationl);
		zp_para.addContent(hirePostByLayer1);
		zp_para.addContent(complexPassword1);
		zp_para.addContent(passwordMinLength1);
		zp_para.addContent(passwordMaxLength1);
		zp_para.addContent(failedTime1);
		zp_para.addContent(unlockTime1);
		zp_para.addContent(PositionNotUnionOrg);
		zp_para.addContent(active_field);
		if(smg1!=null)
			zp_para.addContent(smg1);
		if(new_pos_date!=null){
			zp_para.addContent(new_pos_date);
		}
		if(pos_com_query!=null){
			zp_para.addContent(pos_com_query);
		}
		
		Set keySet = property.keySet();
		Iterator i=keySet.iterator();
		while(i.hasNext())
		{
			String key=(String)i.next();
			if("cards".equalsIgnoreCase(key))
			{
				String keyvalue=(String)property.get(key);
				if(keyvalue!=null&&!"".equals(keyvalue))
				{
					Element cardP = new Element("cards");
					keyvalue=keyvalue.substring(1);
					String[] tmp=keyvalue.split("~");
					for(int j=0;j<tmp.length;j++)
					{
						String str=tmp[j];
						if(str==null|| "".equals(str))
							continue;
						Element t=new Element("card");
						t.setAttribute("hireObj",str.split("`")[0]);
						t.setAttribute("table",str.split("`")[1]);
						cardP.addContent(t);
					}
					zp_para.addContent(cardP);
				}
			}
			else
			{
				Element element = new Element(key);
				element.addContent((String)property.get(key));
				zp_para.addContent(element);
			}
		}
		
		zp_para.addContent(unitLevelEle);
		zp_para.addContent(unitOrdepartEle);
		zp_para.addContent(maxFileSizeEle);
		zp_para.addContent(attachCodesetEle);
		zp_para.addContent(attachHireEle);
		zp_para.addContent(hireChannelPrivEle);
		zp_para.addContent(candidate_statusEle);
		zp_para.addContent(certificate_typeEle);
		zp_para.addContent(register_endtimeEle);
		Element dest_nbase = new Element("dest_nbase");
		dest_nbase.addContent(destNbase);
		zp_para.addContent(dest_nbase);
		
		Document myDocument = new Document(zp_para);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp= outputter.outputString(myDocument);
		//System.out.println(temp);
		return temp;
	}
	
	public String getTestTemplateIds()
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			this.initXML();
			if(xml == null || "".equals(xml.trim())){
				return "";
			}
			init();
			XPath xPath = XPath.newInstance("/zp_para/test_template");
			List list=xPath.selectNodes(this.doc);
			for(Iterator t=list.iterator();t.hasNext();)
			{
				Element test_template =(Element)t.next(); 
				if (test_template != null) 
				{
					buf.append(test_template.getValue()+"~");
				}
			}
			if(buf.toString().length()>0)
			{
				String str = buf.toString().substring(0,buf.toString().length()-1);
				buf.setLength(0);
				buf.append(str);
			}
			else
			{
				buf.append("#~#~#");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	//获得特定用户XML文件元素的相关属性值集合
	public HashMap getAttributeValues() throws GeneralException {
	
		if(hm==null||hm.size()==0)
		{
			hm=new HashMap();
			if("1".equals(flag))
				this.initXML();
			
			if(xml == null || "".equals(xml.trim())){
				return hm;
			}else{
				init();
				try {
					HashSet tempLateIDSet=new HashSet();
					XPath xPath = XPath.newInstance("/zp_para/out_fields");
					Element out_fields = (Element) xPath.selectSingleNode(this.doc);
					if (out_fields != null) {
						hm.put("fields", out_fields.getAttributeValue("Fields"));
					}
					xPath = XPath.newInstance("/zp_para/test_template");
					ArrayList markList=new ArrayList();
					List list=xPath.selectNodes(this.doc);
					for(Iterator t=list.iterator();t.hasNext();)
					{
						Element test_template =(Element)t.next(); 
						if (test_template != null) 
						{
								hm.put("testTemplateID_"+test_template.getAttributeValue("hire_obj_code"),test_template.getValue());
								if(test_template.getValue()!=null&&test_template.getValue().trim().length()>0)
									tempLateIDSet.add(test_template.getValue());
								markList.add(test_template.getAttributeValue("mark_type")==null?"1":test_template.getAttributeValue("mark_type"));
						}
					}
					
					hm.put("testTemplateID",tempLateIDSet);
					hm.put("mark_type",markList);
					/*
					xPath = XPath.newInstance("/zp_para/pos_card");
					Element pos_card = (Element) xPath.selectSingleNode(this.doc);
					if (pos_card != null) {
						hm.put("posCardID",pos_card.getValue());
					}*/
					/**开始追加面试测评初试复试采用不同的测评表 xcs2014-7-29**/
					xPath = XPath.newInstance("/zp_para/test_template_advanced");
					List advanceList=xPath.selectNodes(this.doc);
					ArrayList testTemplatAdvance=new ArrayList();
					for(int i=0;i<advanceList.size();i++){
					    Element advanceElement=(Element) advanceList.get(i);
					    if(advanceElement!=null){
					        HashMap advanceMap=new HashMap();
					        String hire_obj_code=advanceElement.getAttributeValue("hire_obj_code");//得到招聘渠道 01：校园招聘 02：社招 03：内招
					        String interview=advanceElement.getAttributeValue("interview");//得到面试方式 id 初试    复试 这两种 取之代码类36 简历状态 初试和复试   初试：31 复试：32
					        String  score_item=advanceElement.getAttributeValue("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
					        String  mark_type=advanceElement.getAttributeValue("mark_type");//得到是否复杂打分
					        String  templateId=advanceElement.getValue();//得到模版号
					        advanceMap.put("hire_obj_code", hire_obj_code);
					        advanceMap.put("interview", interview);
					        advanceMap.put("score_item", score_item);
					        advanceMap.put("mark_type", mark_type);//打分方式 1:标度打分 2：混合打分
					        advanceMap.put("templateId", templateId);
					        testTemplatAdvance.add(advanceMap);
					    }
					}
					hm.put("testTemplatAdvance", testTemplatAdvance);
					/**结束追加面试测评初试复试采用不同的测评表 xcs2014-7-29**/
					xPath = XPath.newInstance("/zp_para/email_template");
					Element email_template = (Element) xPath.selectSingleNode(this.doc);
					if (email_template != null) {
						hm.put("email_template",email_template.getValue());
					}
					
					
					xPath = XPath.newInstance("/zp_para/pos_query");
					Element pos_query_element = (Element) xPath.selectSingleNode(this.doc);
					if (pos_query_element != null) {
						hm.put("pos_query",pos_query_element.getValue());
					}
					
					xPath = XPath.newInstance("/zp_para/view_pos");
					Element view_pos_element = (Element) xPath.selectSingleNode(this.doc);
					if (view_pos_element != null) {
						hm.put("view_pos",view_pos_element.getValue());
					}
					
					xPath = XPath.newInstance("/zp_para/org_brief");
					Element org_brief = (Element) xPath.selectSingleNode(this.doc);
					if (org_brief != null) {
						hm.put("org_brief",org_brief.getValue());
					}
					xPath = XPath.newInstance("/zp_para/resume_field");
					Element resume_field = (Element)xPath.selectSingleNode(this.doc);
					if(resume_field != null){
						hm.put("resume_field",resume_field.getValue());
					}
					xPath = XPath.newInstance("/zp_para/resume_state");
					Element resume_state = (Element)xPath.selectSingleNode(this.doc);
					if(resume_state != null){
						if(StringUtils.isEmpty(resume_state.getValue()))
							hm.put("resume_state","C01UE");
						else
							hm.put("resume_state",resume_state.getValue());
					}
					xPath = XPath.newInstance("/zp_para/person_type");
					Element person_type=(Element)xPath.selectSingleNode(this.doc);
					if(person_type != null){
						//String s=person_type.getValue();
						if(StringUtils.isEmpty(person_type.getValue()))
							hm.put("person_type","C01UD");
						else
							hm.put("person_type",person_type.getValue());
					}
					xPath =XPath.newInstance("/zp_para/resume_level");
					Element resume_level=(Element)xPath.selectSingleNode(this.doc);
					if(resume_level != null){
						hm.put("resume_level",resume_level.getValue());
					}
					xPath = XPath.newInstance("/zp_para/resume_static");
					Element resume_static =(Element)xPath.selectSingleNode(this.doc);
					if(resume_static != null){
						hm.put("resume_static",resume_static.getValue());
					}
					xPath = XPath.newInstance("/zp_para/hire_object");
					Element hire_object = (Element)xPath.selectSingleNode(this.doc);
					if(hire_object != null){
						if(StringUtils.isEmpty(hire_object.getValue()))
							hm.put("hire_object","Z0336");
						else
							hm.put("hire_object",hire_object.getValue());
					}
					xPath = XPath.newInstance("/zp_para/max_count");
					Element maxC = (Element)xPath.selectSingleNode(this.doc);
					if(maxC != null)
					{
						hm.put("max_count", maxC.getValue());
					}
					xPath = XPath.newInstance("/zp_para/preview_table");
					Element preview = (Element)xPath.selectSingleNode(this.doc);
					if(preview != null)
					{
						hm.put("preview_table", preview.getValue());
					}
					xPath = XPath.newInstance("/zp_para/common_query");
					Element query = (Element)xPath.selectSingleNode(this.doc);
					if(query != null)
					{
						String v=query.getValue();
						if("".equals(v))
							v="-1";
						String sql ="select id from lexpr where id in("+v+")";
						StringBuffer buf = new StringBuffer("");
						try
						{
				    		ContentDAO dao = new ContentDAO(this.conn);
				    		RowSet rs = dao.search(sql);
				    		int i=0;
				    		while(rs.next())
				    		{
				    			if(i>0)
				    				buf.append(",");
				    			buf.append(rs.getString("id"));
				    			i++;
				    		}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						hm.put("common_query", buf.toString());
					}
					//explaination
					xPath = XPath.newInstance("/zp_para/photo");
					Element photo = (Element)xPath.selectSingleNode(this.doc);
					if(photo != null)
					{
						hm.put("photo", photo.getValue()==null?"":photo.getValue());
					}
					xPath = XPath.newInstance("/zp_para/explaination");
					Element explaination = (Element)xPath.selectSingleNode(this.doc);
					if(explaination != null)
					{
						hm.put("explaination", explaination.getValue()==null?"":explaination.getValue());
					}
					xPath = XPath.newInstance("/zp_para/attach");
					Element att = (Element)xPath.selectSingleNode(this.doc);
					if(att != null)
					{
						hm.put("attach", att.getValue()==null?"":att.getValue());
					}
					
					//外网简历附件分类列表
                    xPath = XPath.newInstance("/zp_para/attach_codeset");
                    Element attachCodesetEle = (Element)xPath.selectSingleNode(this.doc);
                    if(attachCodesetEle != null)
                    {
                        hm.put("attachCodeset", attachCodesetEle.getValue()==null?"":attachCodesetEle.getValue());
                    }
                    
                    
                    //外网简历附件分类列表
                    xPath = XPath.newInstance("/zp_para/attach_hire_object");
                    Element attachHireEle= (Element)xPath.selectSingleNode(this.doc);
                    if(attachHireEle != null)
                    {
                        hm.put("attachHire", attachHireEle.getValue()==null?"":attachHireEle.getValue());
                    }
                    
                    //渠道授权设置
                    xPath = XPath.newInstance("/zp_para/hire_object_priv");
                    Element hireChannelPrivEle= (Element)xPath.selectSingleNode(this.doc);
                    if(hireChannelPrivEle != null)
                    {
                        hm.put("hireChannelPriv", hireChannelPrivEle.getValue()==null?"":hireChannelPrivEle.getValue());
                    }
					
					xPath = XPath.newInstance("/zp_para/business_template");
					Element business_template = (Element)xPath.selectSingleNode(this.doc);
					if(business_template != null)
					{
						hm.put("business_template", business_template.getValue()==null?"":business_template.getValue());
					}
					//resume_code
					xPath = XPath.newInstance("/zp_para/resume_code");
					Element resume_code = (Element)xPath.selectSingleNode(this.doc);
					if(resume_code != null)
					{
						hm.put("resume_code", resume_code.getValue()==null?"":resume_code.getValue());
					}
					//culture_code
					xPath = XPath.newInstance("/zp_para/culture_code");
					Element culture_code = (Element)xPath.selectSingleNode(this.doc);
					if(culture_code != null)
					{
						hm.put("culture_code", culture_code.getAttributeValue("code"));
						hm.put("culture_item", culture_code.getAttributeValue("item"));
					}
					xPath = XPath.newInstance("/zp_para/net_href");
					Element netHref = (Element)xPath.selectSingleNode(this.doc);
					if(netHref != null)
					{
						hm.put("net_href", netHref.getValue());
					}
					xPath = XPath.newInstance("/zp_para/interviewing_itemid");
					Element interviewing_itemid = (Element)xPath.selectSingleNode(this.doc);
					if(interviewing_itemid != null&&!"#".equals(interviewing_itemid.getValue()))
					{
						hm.put("interviewing_itemid", interviewing_itemid.getValue());
					}
					xPath = XPath.newInstance("/zp_para/isCtrlReportGZ");
					Element isCtrlReportGZ = (Element)xPath.selectSingleNode(this.doc);
					if(isCtrlReportGZ != null)
					{
						hm.put("isCtrlReportGZ", isCtrlReportGZ.getValue());
						hm.put("positionSalaryStandardItem", isCtrlReportGZ.getAttributeValue("item"));
					}
					xPath = XPath.newInstance("/zp_para/isCtrlReportBZ");
					Element isCtrlReportBZ = (Element)xPath.selectSingleNode(this.doc);
					if(isCtrlReportBZ != null)
					{
						hm.put("isCtrlReportBZ", isCtrlReportBZ.getValue());
					}
					//isRemenberExamine
					xPath = XPath.newInstance("/zp_para/isRemenberExamine");
					Element isRemenberExamine = (Element)xPath.selectSingleNode(this.doc);
					if(isRemenberExamine != null)
					{
						hm.put("isRemenberExamine", isRemenberExamine.getValue());
						hm.put("remenberExamineSet", isRemenberExamine.getAttributeValue("item"));
						hm.put("comment_user",isRemenberExamine.getAttributeValue("comment_user"));
						HashMap infoMap = new HashMap();
						infoMap.put("title", isRemenberExamine.getAttributeValue("title"));
						infoMap.put("content", isRemenberExamine.getAttributeValue("content"));
						infoMap.put("level", isRemenberExamine.getAttributeValue("level"));
						infoMap.put("comment_date", isRemenberExamine.getAttributeValue("comment_date"));
						infoMap.put("comment_user", isRemenberExamine.getAttributeValue("comment_user"));
						hm.put("infoMap", infoMap);
					}
					//startResumeAnalysis  简历解析服务
					xPath = XPath.newInstance("/zp_para/startResumeAnalysis");
					Element startResumeAnalysis = (Element)xPath.selectSingleNode(this.doc);
					if(startResumeAnalysis != null)
					{
						hm.put("startResumeAnalysis", startResumeAnalysis.getValue());//是否开启简历分析服务的值
						HashMap resumeAnalysisMap = new HashMap();
						resumeAnalysisMap.put("resumeAnalysisName", startResumeAnalysis.getAttributeValue("resumeAnalysisName"));
						resumeAnalysisMap.put("resumeAnalysisPassword", startResumeAnalysis.getAttributeValue("resumeAnalysisPassword"));
						resumeAnalysisMap.put("resumeAnalysisForeignJob", startResumeAnalysis.getAttributeValue("resumeAnalysisForeignJob"));
						hm.put("resumeAnalysisMap", resumeAnalysisMap);
					}
					
					//orgWillTableId
					xPath = XPath.newInstance("/zp_para/orgWillTableId");
					Element orgWillTableId = (Element)xPath.selectSingleNode(this.doc);
					if(orgWillTableId != null)
					{
						hm.put("orgWillTableId", orgWillTableId.getValue());
					}
					xPath = XPath.newInstance("/zp_para/admissionCard");
					Element admissionCard=(Element)xPath.selectSingleNode(this.doc);
					if(admissionCard!=null)
					{
						hm.put("admissionCard", admissionCard.getValue());
					}
					
					xPath = XPath.newInstance("/zp_para/scoreCard");
                    Element scoreCard=(Element)xPath.selectSingleNode(this.doc);
                    if(scoreCard!=null)
                    {
                        hm.put("scoreCard", scoreCard.getValue());
                    }
                    
                    xPath = XPath.newInstance("/zp_para/appliedPosItems");
                    Element appliedPosItems=(Element)xPath.selectSingleNode(this.doc);
                    if(appliedPosItems!=null)
                    {
                        hm.put("appliedPosItems", appliedPosItems.getValue());
                    }
                    
					xPath = XPath.newInstance("/zp_para/moreLevelSP");
					Element moreLevelSP = (Element)xPath.selectSingleNode(this.doc);
					if(moreLevelSP != null)
					{
						hm.put("moreLevelSP", moreLevelSP.getValue());
					}
					xPath = XPath.newInstance("/zp_para/spRelation");
					Element spRelation = (Element)xPath.selectSingleNode(this.doc);
					if(spRelation != null)
					{
						hm.put("spRelation", spRelation.getValue());
					}
					xPath = XPath.newInstance("/zp_para/hirePostByLayer");
					Element hirePostByLayer = (Element)xPath.selectSingleNode(this.doc);
					if(hirePostByLayer != null)
					{
						hm.put("hirePostByLayer", hirePostByLayer.getValue());
					}
					/**密码 最小长度 最大长度 最大次数 时间间隔**/
					xPath = XPath.newInstance("/zp_para/complexPassword");
					Element complexPassword = (Element)xPath.selectSingleNode(this.doc);
					if(complexPassword != null)
					{
						hm.put("complexPassword", complexPassword.getValue());
					}
					xPath = XPath.newInstance("/zp_para/passwordMinLength");
					Element passwordMinLength = (Element)xPath.selectSingleNode(this.doc);
					if(passwordMinLength != null)
					{
						hm.put("passwordMinLength", passwordMinLength.getValue());
					}
					xPath = XPath.newInstance("/zp_para/passwordMaxLength");
					Element passwordMaxLength = (Element)xPath.selectSingleNode(this.doc);
					if(passwordMaxLength != null)
					{
						hm.put("passwordMaxLength", passwordMaxLength.getValue());
					}
					xPath = XPath.newInstance("/zp_para/failedTime");
					Element failedTime = (Element)xPath.selectSingleNode(this.doc);
					if(failedTime != null)
					{
						hm.put("failedTime", failedTime.getValue());
					}
					xPath = XPath.newInstance("/zp_para/unlockTime");
					Element unlockTime = (Element)xPath.selectSingleNode(this.doc);
					if(unlockTime != null)
					{
						hm.put("unlockTime", unlockTime.getValue());
					}
					
					
					/****/
					xPath = XPath.newInstance("/zp_para/position_number");
					Element position_number=(Element)xPath.selectSingleNode(this.doc);
					if(position_number!=null)
					{
						hm.put("positionNumber", position_number.getValue());
					}
					//hirePositionItem,String hirePositionNotUnionOrg
					xPath = XPath.newInstance("/zp_para/hirePositionNotUnionOrg");
					Element hirePositionNotUnionOrg = (Element)xPath.selectSingleNode(this.doc);
					if(hirePositionNotUnionOrg != null)
					{
						hm.put("hirePositionNotUnionOrg", hirePositionNotUnionOrg.getValue());
						hm.put("hirePositionItem", hirePositionNotUnionOrg.getAttributeValue("item"));
					}
					/**active_field 简历激活状态指标**/
					xPath = XPath.newInstance("/zp_para/active_field");
					Element active_field = (Element)xPath.selectSingleNode(this.doc);
					if(active_field != null)
					{
						hm.put("active_field", active_field.getValue());
					}
					xPath = XPath.newInstance("/zp_para/schoolPosition");
					Element schoolPosition = (Element)xPath.selectSingleNode(this.doc);
					if(schoolPosition != null)
					{
						hm.put("schoolPosition", schoolPosition.getValue());
					}
					xPath = XPath.newInstance("/zp_para/workExperience");
					Element workExperience = (Element)xPath.selectSingleNode(this.doc);
					if(workExperience != null)
					{
						hm.put("workExperience", workExperience.getValue());
					}
					
					xPath = XPath.newInstance("/zp_para/hireMajor");
					Element hireMajor = (Element)xPath.selectSingleNode(this.doc);
					if(hireMajor != null)
					{
						hm.put("hireMajor", hireMajor.getValue());
					}
					//招聘专业代码  郭峰增加
					xPath = XPath.newInstance("/zp_para/hireMajorCode");
					Element hireMajorCode = (Element)xPath.selectSingleNode(this.doc);
					if(hireMajorCode != null)
					{
						hm.put("hireMajorCode", hireMajorCode.getValue());
					}
					
					xPath = XPath.newInstance("/zp_para/answerSet");
					Element answerSet = (Element)xPath.selectSingleNode(this.doc);
					if(answerSet != null)
					{
						hm.put("answerSet", answerSet.getValue());
					}
					//外网岗位列表显示指标
					xPath = XPath.newInstance("/zp_para/pos_listfield");
					Element pos_listfield = (Element)xPath.selectSingleNode(this.doc);
					if(pos_listfield != null)
					{
						hm.put("pos_listfield", pos_listfield.getValue());
					}
					//外网岗位列表显示指标排序
					xPath=XPath.newInstance("/zp_para/pos_listfield_sort");
					Element pos_listfield_sort = (Element)xPath.selectSingleNode(this.doc);
                    if(pos_listfield_sort != null)
                    {
                        hm.put("pos_listfield_sort", pos_listfield_sort.getValue());
                    }
					xPath = XPath.newInstance("/zp_para/sms_notice");
					Element sms_notice = (Element) xPath.selectSingleNode(this.doc);
					if (sms_notice != null) {
						hm.put("sms_notice",sms_notice.getValue());
					}
					xPath = XPath.newInstance("/zp_para/new_pos_date");
					Element new_pos_date = (Element) xPath.selectSingleNode(this.doc);
					if (new_pos_date != null) {
						hm.put("new_pos_date",new_pos_date.getValue());
					}
					xPath = XPath.newInstance("/zp_para/lftype");
					Element lftype = (Element) xPath.selectSingleNode(this.doc);
					if (lftype != null) {
						hm.put("lftype",lftype.getValue());
					}
					xPath = XPath.newInstance("/zp_para/hbtype");
					Element hbtype = (Element) xPath.selectSingleNode(this.doc);
					if (hbtype != null) {
						hm.put("hbtype",hbtype.getValue());
					}
					xPath = XPath.newInstance("/zp_para/pos_com_query");// dml 2011-6-22 10:54:02
					Element pos_com_query = (Element) xPath.selectSingleNode(this.doc);
					if (pos_com_query != null) {
						hm.put("pos_com_query",pos_com_query.getValue());
					}
					xPath = XPath.newInstance("/zp_para/acountBeActived");
					Element acountBeActived = (Element) xPath.selectSingleNode(this.doc);
					if (acountBeActived != null) {
						hm.put("acountBeActived",acountBeActived.getValue());
					}
					xPath = XPath.newInstance("/zp_para/cards");
					Element card = (Element) xPath.selectSingleNode(this.doc);
					if (card != null) {
						List tlist=card.getChildren();
						for(int j=0;j<tlist.size();j++)
						{
							Element telement=(Element)tlist.get(j);
							hm.put("CARDTABLE_"+telement.getAttributeValue("hireObj"), telement.getAttributeValue("table"));
						}
					}
					
					xPath = XPath.newInstance("/zp_para/unitLevel");// dml 2011-6-22 10:54:02
                    Element unitLevel = (Element) xPath.selectSingleNode(this.doc);
                    if (unitLevel != null) 
                        hm.put("unitLevel",unitLevel.getValue());
                    
                    xPath = XPath.newInstance("/zp_para/unitOrDepart");
                    Element unitOrDepart = (Element) xPath.selectSingleNode(this.doc);
                    if (unitOrDepart != null) 
                    	hm.put("unitOrDepart",unitOrDepart.getValue());
                    
                    xPath = XPath.newInstance("/zp_para/maxFileSize");// dml 2011-6-22 10:54:02
                    Element maxFileSize = (Element) xPath.selectSingleNode(this.doc);
                    if (maxFileSize != null) 
                        hm.put("maxFileSize",maxFileSize.getValue());
                    
                    xPath = XPath.newInstance("/zp_para/candidate_status");
                    Element candidate_status = (Element) xPath.selectSingleNode(this.doc);
                    if (candidate_status != null) 
                    	hm.put("candidate_status",candidate_status.getValue());
                    
                    xPath = XPath.newInstance("/zp_para/id_type");
                    Element certificate_type = (Element) xPath.selectSingleNode(this.doc);
                    if (certificate_type != null) 
                    	hm.put("id_type",certificate_type.getValue());

                    xPath = XPath.newInstance("/zp_para/dest_nbase");
                    Element dest_nbase = (Element) xPath.selectSingleNode(this.doc);
                    String destNbase = "Usr";
                    if (dest_nbase != null) 
                        destNbase = dest_nbase.getValue();
                    
                    destNbase = StringUtils.isEmpty(destNbase) ? "Usr" : destNbase;
                    hm.put("destNbase", destNbase);
                    
                    xPath = XPath.newInstance("/zp_para/register_endtime");
                    Element register_endtime = (Element) xPath.selectSingleNode(this.doc);
                    if (register_endtime != null) 
                    	hm.put("register_endtime",register_endtime.getValue());
                    
				} catch (JDOMException e) {
					e.printStackTrace();
				}
			}
		
		}
		return hm;
		
	}
	/**
	 * 保存单位介绍指标定义参数
	 * @param orgFieldIDs
	 * @param contentType
	 */
  public void saveBriefPara( String orgFieldIDs,String contentType,String node){
	  ContentDAO dao = new ContentDAO(this.conn);
	  try{
		  init();
		  XPath xPath=XPath.newInstance("/zp_para");
		  Element element=(Element)xPath.selectSingleNode(this.doc);
		  if(element !=null){
			  element.removeChild(node);
		  }else{
			  element=new Element("zp_para");
		  }
		  Element org_brief= new Element("org_brief");
		  if(orgFieldIDs !=null && contentType != null){
			  org_brief.addContent(orgFieldIDs+","+contentType);
		  }
		  element.addContent(org_brief);
		  XMLOutputter outputter = new XMLOutputter();
		  Format format = Format.getPrettyFormat();
		  format.setEncoding("UTF-8");
		  outputter.setFormat(format);
		  ArrayList list = new ArrayList();
		  String sqlsql ="";
		  list.add(outputter.outputString(this.doc));
		  if (Constant.MSSQL == Sql_switcher.searchDbServer())
			    sqlsql = "update constant set str_value = ? where constant = 'ZP_PARAMTER'";
			else if (Constant.ORACEL == Sql_switcher.searchDbServer())
				sqlsql = "declare  content clob; begin  content :=?;  update constant set str_value = content  where constant = 'ZP_PARAMTER'; end;";
		  
		  
		  boolean flag = isHave();
		  if(flag)
			  dao.update(sqlsql, list);
		  else
		  {
			  RecordVo vo = new RecordVo("constant");
			  vo.setString("constant", "ZP_PARAMTER");
			  vo.setString("str_value",outputter.outputString(this.doc));
			  dao.addValueObject(vo);
			  //dao.insert("insert into constant(constant,str_value) values('ZP_PARAMTER','"+outputter.outputString(this.doc)+"')", new ArrayList());
		  }
		  hm=null;
		  initXML();
		  getAttributeValues();
	  }
	  catch (JDOMException e) {
			e.printStackTrace();
	  }
	  catch(Exception e){
		  e.printStackTrace();
	  }
}
  public boolean isHave()
  {
	 boolean flag = false;
	 try
	 {
		 String sql = "select * from constant where constant ='ZP_PARAMTER'";
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rs = null;
		 rs = dao.search(sql);
		 while(rs.next())
		 {
			 flag = true;
		 }
	 }
	 catch(Exception e)
	 {
		 e.printStackTrace();
	 }
	 return flag;
  }
  public String getBriefParaValue(){
	  String str= "";
	  if(xml ==null|| "".equals(xml.trim())){
		  return str;
	  }else{
		  try{
		     init();
		     XPath xPath = XPath.newInstance("/zp_para/org_brief");
		     Element org_brief = (Element)xPath.selectSingleNode(this.doc);
		     if(org_brief != null){
		    	 str=org_brief.getValue();
		     }
		     
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  
	  }
	  
	  return str;
	  
  }
  public String getParaValues(String paraName){
	  String str="";
	  if(xml==null||xml.trim().length()<0){
		  return str;
	  }else{
		  try{
			  init();
			  XPath xPath = XPath.newInstance("/zp_para/"+paraName);
			  Element paraNode =(Element)xPath.selectSingleNode(this.doc);
			  if(paraNode != null){
				  str = paraNode.getValue();
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  return str;
  }
	public String getParaValues(String paraName,String para){
		String str = "";
		if(xml ==null || xml.trim().length()<0){
			return str;
		}else{
			try{
				init();
				XPath xPath = XPath.newInstance("/zp_para/"+paraName);
				Element paraNode = (Element)xPath.selectSingleNode(this.doc);
				if(paraNode != null){
					str = paraNode.getAttributeValue(para);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str;
	}
	/**
	 * 取得各个评测表的打分方式
	 * @return
	 */
	public HashMap getMarkTypebyHireObjectCode(){
		HashMap map = new HashMap();
		try
		{
			this.initXML();
		    if(xml==null||xml.trim().length()<=0)
		    {
		    	return map;
		    }
		    else
		    {
		    	init();
		    	XPath xPath = XPath.newInstance("/zp_para/test_template");
		    	List list=xPath.selectNodes(this.doc);
				for(Iterator t=list.iterator();t.hasNext();)
				{
					Element test_template =(Element)t.next(); 
					if (test_template != null) 
					{
							map.put(test_template.getAttributeValue("hire_obj_code"),test_template.getAttributeValue("mark_type")==null?"1":test_template.getAttributeValue("mark_type"));
					}
				}
		    	
		    }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

   public void setLogoFileType(String lfType,String hbType)
   {
	   PreparedStatement pstmt = null;	
	   
	   try
	   {
	      this.init();
	      Element root = this.doc.getRootElement();
	      XPath xPath = XPath.newInstance("/zp_para/lftype");
		  Element paraNode =(Element)xPath.selectSingleNode(this.doc);
		  if(paraNode==null)
		  {
			  paraNode = new Element("lftype");
			  paraNode.setText(lfType);
			  root.addContent(paraNode);
		  }else{ 
		     paraNode.setText(lfType);
		  }
		  xPath = XPath.newInstance("/zp_para/hbtype");
		   paraNode =(Element)xPath.selectSingleNode(this.doc);
		  if(paraNode==null)
		  {
			  paraNode = new Element("hbtype");
			  paraNode.setText(hbType);
			  root.addContent(paraNode);
		  }else{
			  paraNode.setText(hbType);
		  }
		  StringBuffer strsql = new StringBuffer();
		  XMLOutputter outputter = new XMLOutputter();
		  Format format = Format.getPrettyFormat();
		  format.setEncoding("UTF-8");
		  outputter.setFormat(format);
		  String str_value= outputter.outputString(this.doc);
		  if(isHave())
			{
				strsql.append("update constant set str_value=? where constant='ZP_PARAMTER'");
				pstmt = this.conn.prepareStatement(strsql.toString());	
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, str_value);
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
			   }
			}else
			{
				strsql.append("insert into constant(constant,type,describe,str_value) values(?,?,?,?)");	
				pstmt = this.conn.prepareStatement(strsql.toString());				
				pstmt.setString(1, "ZP_PARAMTER");
				pstmt.setString(2, "A");
				pstmt.setString(3,ResourceFactory.getProperty("hire.parameterSet.parameterSet"));
				pstmt.setString(4,str_value);
			}
		  	// 打开Wallet
			dbS.open(conn, strsql.toString());
			pstmt.executeUpdate();
			if(hm!=null)
			{
				hm.put("lftype",lfType);
				hm.put("hbtype",hbType);
			}
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		   PubFunc.closeResource(pstmt);
	   }
   }
   	public String hasSetParam(UserView view){
   		StringBuffer info=new StringBuffer();
   		boolean flag=false;
   		String tmp = view.getUnitIdByBusi("7");
   		if((tmp==null||tmp.length()==0)&&!view.isSuper_admin()){
   			info.append("您没有设置招聘模块的管理范围");
   			flag=true;
   		}
   	
   		if(hm==null|| hm.size()==0){
   			info.append("招聘管理没有设置任何参数,请到招聘管理/参数设置中设置相关参数！\r\n");
   			flag=true;
   		}
   		String personTypeField="";
		if(hm!=null&&hm.get("person_type")!=null)
			personTypeField=(String)hm.get("person_type");
		if(!flag&&personTypeField==null|| "".equals(personTypeField)){
			info.append("请在参数设置中配置人才库标识指标！");
			flag=true;
		}
		String resume_state="";
		if(hm.get("resume_state")!=null)
			resume_state=(String)hm.get("resume_state");
		if(!flag&& "".equals(resume_state)){
			info.append("请在参数设置中配置简历状态指标！");
			flag=true;
		}
   		if(!flag&&hm==null||hm.get("hire_object")==null||((String)hm.get("hire_object")).length()==0){
   			info.append("请在配置参数模块中设置招聘对象指标！");
   			flag=true;
   		}
   		return info.toString();
   	} 
	public String getNewTime() {
		return newTime;
	}


	public void setNewTime(String newTime) {
		this.newTime = newTime;
	}


    /** 
     * @Title: addAdvance 
     * @Description: 向招聘配置参数中增加高级测评方式的功能
     * @param hireobjcode 招聘渠道
     * @param mode 测评阶段
     * @param testTemplate测评时使用的模版
     * @param markType 打分方式 1：标准打分 2：混合打分
     * @param itemid 分值对应的指标（z05中的数值型指标）   
     * @throws 
    */
    public String addAdvance(String hireobjcode, String mode, String testTemplate, String markType, String itemid) {
        String sucess="ok";
        PreparedStatement pstmt = null;
        try{
            this.init();
            Element root = this.doc.getRootElement();
            Element old_test_template_advanced=null;
            List test_template_advanceds=root.getChildren("test_template_advanced");
            for(int i=0;i<test_template_advanceds.size();i++){
                Element tempElement=(Element) test_template_advanceds.get(i);
                String hire_obj_code=tempElement.getAttributeValue("hire_obj_code")==null?"":tempElement.getAttributeValue("hire_obj_code");
                String interview=tempElement.getAttributeValue("interview")==null?"":tempElement.getAttributeValue("interview");
                if(hire_obj_code.equals(hireobjcode)&&interview.equals(mode)){//如果当前模版中定义了当前招聘方式测评阶段的测评方案，那么就需要提示用户删除原来的
                    old_test_template_advanced=tempElement;
                    break;
                }
            }
            if(old_test_template_advanced!=null){
                sucess="exits";
            }else{
                Element test_template_advanced=new Element("test_template_advanced");
                test_template_advanced.setAttribute("hire_obj_code", hireobjcode);//设置招聘方式
                test_template_advanced.setAttribute("interview", mode);//设置测评阶段
                test_template_advanced.setAttribute("score_item", itemid);//分值对应指标
                test_template_advanced.setAttribute("mark_type", markType);//是否采用混合打分
                test_template_advanced.setText(testTemplate);//设置的模版名称id
                root.addContent(test_template_advanced);
                XMLOutputter outputter = new XMLOutputter();
                Format format = Format.getPrettyFormat();
                format.setEncoding("UTF-8");
                outputter.setFormat(format);
                String str_value= outputter.outputString(root.getDocument());
                StringBuffer strsql=new StringBuffer();
                if(isHave()){
                    strsql.append("update constant set str_value=? where constant='ZP_PARAMTER'");
                    pstmt = this.conn.prepareStatement(strsql.toString());  
                    switch(Sql_switcher.searchDbServer())
                    {
                      case Constant.MSSQL:
                      {
                          pstmt.setString(1, str_value);
                          break;
                      }
                      case Constant.ORACEL:
                      {
                          pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
                                  getBytes())), str_value.length());
                          break;
                      }
                      case Constant.DB2:
                      {
                          pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
                                  getBytes())), str_value.length());
                          break;
                      }
                   }
                }else
                {
                    strsql.append("insert into constant(constant,type,describe,str_value) values(?,?,?,?)");    
                    pstmt = this.conn.prepareStatement(strsql.toString());              
                    pstmt.setString(1, "ZP_PARAMTER");
                    pstmt.setString(2, "A");
                    pstmt.setString(3,ResourceFactory.getProperty("hire.parameterSet.parameterSet"));
                    pstmt.setString(4,str_value);
                }
                // 打开Wallet
            	dbS.open(conn, strsql.toString());

                pstmt.executeUpdate();
                hm=null;
                initXML();
                getAttributeValues(); 
            }
        }
        catch(Exception e){
            sucess="false";
            e.printStackTrace();
        }
        finally{
        	try {
        		// 关闭Wallet
        		dbS.close(conn);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}

            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    sucess="false";
                    e.printStackTrace();
                }
            }
        }
        return sucess;
    }

    /** 
     * @Title: delAdvance 
     * @Description:删除存在的高级测评方案
     * @param hireobjcode 招聘渠道
     * @param mode 测评阶段
     * @param testTemplate  测评表
     * @param markType 打分方式
     * @param itemid 关联指标
     * @return String   
     * @throws 
    */
    public String delAdvance(String hireobjcode, String mode, String testTemplate, String markType, String itemid) {
        String sucess="ok";
        PreparedStatement pstmt = null;
        try{
            this.init();
            Element root = this.doc.getRootElement();
            List test_template_advanceds=root.getChildren("test_template_advanced");
            Element test_template_advanced=null;
            for(int i=0;i<test_template_advanceds.size();i++){
                Element tempElement=(Element) test_template_advanceds.get(i);
                String hire_obj_code=tempElement.getAttributeValue("hire_obj_code")==null?"":tempElement.getAttributeValue("hire_obj_code");
                String interview=tempElement.getAttributeValue("interview")==null?"":tempElement.getAttributeValue("interview");
                String score_item=tempElement.getAttributeValue("score_item")==null?"":tempElement.getAttributeValue("score_item");
                String mark_type=tempElement.getAttributeValue("mark_type")==null?"":tempElement.getAttributeValue("mark_type");
                String testTemplatevalue=tempElement.getText()==null?"":tempElement.getText();
                if(hire_obj_code.equals(hireobjcode)&&interview.equals(mode)&&score_item.equals(itemid)&&mark_type.equals(markType)&&testTemplatevalue.equals(testTemplate)){
                    test_template_advanced=tempElement;
                    break;
                }
            }
            //root.addContent(test_template_advanced);
            if(test_template_advanced!=null){
                root.removeContent(test_template_advanced);
            }
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            String str_value= outputter.outputString(root.getDocument());
            StringBuffer strsql=new StringBuffer();
            if(isHave()){
                strsql.append("update constant set str_value=? where constant='ZP_PARAMTER'");
                pstmt = this.conn.prepareStatement(strsql.toString());  
                switch(Sql_switcher.searchDbServer())
                {
                  case Constant.MSSQL:
                  {
                      pstmt.setString(1, str_value);
                      break;
                  }
                  case Constant.ORACEL:
                  {
                      pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
                              getBytes())), str_value.length());
                      break;
                  }
                  case Constant.DB2:
                  {
                      pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
                              getBytes())), str_value.length());
                      break;
                  }
               }
            }else{
                strsql.append("insert into constant(constant,type,describe,str_value) values(?,?,?,?)");    
                pstmt = this.conn.prepareStatement(strsql.toString());              
                pstmt.setString(1, "ZP_PARAMTER");
                pstmt.setString(2, "A");
                pstmt.setString(3,ResourceFactory.getProperty("hire.parameterSet.parameterSet"));
                pstmt.setString(4,str_value);
            }
            // 打开Wallet
        	dbS.open(conn, strsql.toString());
            pstmt.executeUpdate();
            hm=null;
            initXML();
            getAttributeValues();
        }
        catch(Exception e){
            sucess="false";
            e.printStackTrace();
        }finally{
        	try {
        		// 关闭Wallet
        		dbS.close(conn);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}

            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    sucess="false";
                    e.printStackTrace();
                }
            }
        }
        return sucess;
    }


    /** 
     * @Title: getTestAdvanceTemplateIds 
     * @Description: 得到高级测评模式下的模版
     * @return String   
     * @throws 
    */
    public String getTestAdvanceTemplateIds() {
        StringBuffer buf = new StringBuffer("");
        try
        {
            this.initXML();
            if(xml == null || "".equals(xml.trim())){
                return "";
            }
            init();
            XPath xPath = XPath.newInstance("/zp_para/test_template_advanced");
            List list=xPath.selectNodes(this.doc);
            for(Iterator t=list.iterator();t.hasNext();)
            {
                Element test_template =(Element)t.next(); 
                if (test_template != null) 
                {
                    buf.append(test_template.getValue()+"~");
                }
            }
            if(buf.toString().length()>0)
            {
                String str = buf.toString().substring(0,buf.toString().length()-1);
                buf.setLength(0);
                buf.append(str);
            }
            else
            {
                buf.append("#~#~#");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return buf.toString();
    }


    public String getScoreCard() {
        return scoreCard;
    }


    public void setScoreCard(String scoreCard) {
        this.scoreCard = scoreCard;
    }

	public void setAppliedPosItems(String appliedPosItems) {
		this.appliedPosItems = appliedPosItems;
	}

	public String getAppliedPosItems() {
		return appliedPosItems;
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

    public void setDestNbase(String destNbase) {
        this.destNbase = destNbase;
    }
}
