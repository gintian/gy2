package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class EnrollTrans extends IBusiness {

	public void execute() throws GeneralException {
		String dbName = "";
		String a0100 = "";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String person_type=(String)hm.get("person_type");	
			String paramFlag=(String)this.getFormHM().get("paramFlag");
			String txtEmail=(String)this.getFormHM().get("txtEmail");
			String pwd1=(String)this.getFormHM().get("pwd1");
			String txtName=(String)this.getFormHM().get("txtName");
			String isDefinitionActive=(String)this.getFormHM().get("isDefinitionActive");
			//应聘身份指标值，35号代码类里的值
			String candidate_status=(String)this.getFormHM().get("candidate_status");
			candidate_status=PubFunc.getReplaceStr(candidate_status);
			person_type=PubFunc.getReplaceStr(person_type);
			paramFlag=PubFunc.getReplaceStr(paramFlag);
			txtEmail=PubFunc.getReplaceStr(txtEmail);
			pwd1=PubFunc.getReplaceStr(pwd1);
			txtName=PubFunc.getReplaceStr(txtName);
			String isUpPhoto="0";   //是否必须上传照片
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			if(map.get("photo")!=null&&((String)map.get("photo")).length()>0)//照片必须上传=1时为必须上传照片
				isUpPhoto=(String)map.get("photo");
			String isExp="0"; //是否显示指标描述
			if(map.get("explaination")!=null&&((String)map.get("explaination")).length()>0)
				isExp=(String)map.get("explaination");
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			String candidate_status_itemId="#";//应聘身份指标
			if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
				candidate_status_itemId=(String)map.get("candidate_status");
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			dbName=bo.getZpkdbName();
			String belongUnit="";
			boolean flag=false;
			String activeField="";
			if(isDefinitionActive!=null&& "1".equals(isDefinitionActive))
			{
				 belongUnit=(String)this.getFormHM().get("belongUnit");
				 flag=true;
				 activeField=(String)map.get("active_field");
				
			}//应用库
			this.getFormHM().put("userName",txtName);
			this.getFormHM().put("dbName",dbName);
		    this.getFormHM().put("isDefinitionActive", isDefinitionActive);
		    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");
			String blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");
			if(blacklist_field==null|| "".equals(blacklist_field))
				this.getFormHM().put("blackField", "");
			else
				this.getFormHM().put("blackField", blacklist_field);
			if(blacklist_per==null|| "".equals(blacklist_per))
				this.getFormHM().put("blackNbase", "");
			else
				this.getFormHM().put("blackNbase",blacklist_per);
			
			String workExperience=bo.getWorkExperience();
			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
			String value="";
			String onlyname = bo.getOnly_field();
			String onlyValue=(String)this.getFormHM().get("onlyValue");
			//唯一性指标是否需要填写
			boolean onlyFlag = true;
			if(StringUtils.isNotEmpty(onlyname))
				onlyFlag = StringUtils.isNotEmpty(onlyValue);
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId))
				onlyFlag = StringUtils.isNotEmpty(candidate_status);
			//证件类型
			String idType = "";
			String idTypeValue = "";
			if(map.get("id_type")!=null) {
				idType=(String)map.get("id_type");
				idTypeValue=(String)hm.get("idTypeValue");
				//验证是否有效身份证号
				if(!"".equals(map.get("id_type")) && StringUtils.isNotEmpty(onlyname) && RecruitUtilsBo.getIdTypeValue().equals(idTypeValue)&&!PubFunc.idCardValidate(onlyValue)) {
					throw new Exception("请输入正确的身份证号！");
				}
			}
			//账号，唯一性指标，证件类型
			if(StringUtils.isNotEmpty(txtEmail)&&onlyFlag&&!(StringUtils.isEmpty(idType)^StringUtils.isEmpty(idTypeValue))) {
				a0100=DbNameBo.insertMainSetA0100(dbName+"A01",this.getFrameconn());
				String blackFieldValue="";
				if(blacklist_field!=null&&!"".equals(blacklist_field)&&blacklist_per!=null&&!"".equals(blacklist_per))
				{
					blackFieldValue=(String)this.getFormHM().get("blackFieldValue");
				}
				if("1".equals(isDefineWorkExperience))
					value=(String)this.getFormHM().get("workExperience");
				//口令加密处理
				RecordVo encryVo = ConstantParamter.getRealConstantVo("EncryPwd",this.getFrameconn());
				if(encryVo!=null) {
					String encryPwd = encryVo.getString("str_value");
					if("1".equals(encryPwd)){//加密
						Des des=new Des();
						pwd1=des.EncryPwdStr(pwd1);
					}
				}
				HashMap<String, String> paramMap = new HashMap<String, String>();
				FieldItem fieldItem = DataDictionary.getFieldItem(idType, "A01");
				if(fieldItem!=null&&"1".equals(fieldItem.getUseflag()))
					paramMap.put(idType, idTypeValue);
				
				paramMap.put("tableName", dbName+"A01");
				paramMap.put("username", txtEmail);
				paramMap.put("a0101", txtName);
				paramMap.put("userpassword", pwd1);
				paramMap.put("a0100", a0100);
				paramMap.put("person_type", person_type);
				paramMap.put("b0110", belongUnit);
				paramMap.put("activeField", activeField);
				paramMap.put("blackField", blacklist_field);
				paramMap.put("blackFieldValue", blackFieldValue);
				paramMap.put("isDefineWorkExperience", isDefineWorkExperience);
				paramMap.put("workExperience", workExperience);
				paramMap.put("workExperienceValue", value);
				paramMap.put("paramFlag", paramFlag);
				paramMap.put("onlyValue", onlyValue);
				paramMap.put("onlyname", onlyname);
				paramMap.put("candidate_status_itemId", candidate_status_itemId);
				paramMap.put("candidate_status", candidate_status);
				addA01(dao, flag, paramMap);
				try {
					OtherParam param=new OtherParam(this.getFrameconn());
					Map setmap=param.serachAtrr("/param/formual[@name='bycardno']");
					if(setmap==null)
					    return;

					 String srcName = setmap.get("src").toString().toLowerCase();
					 String BirthdayName = setmap.get("birthday").toString().toLowerCase();
					 String ageName = setmap.get("age").toString().toLowerCase();
					 String axName = setmap.get("ax").toString().toLowerCase();
					 
					 if(RecruitUtilsBo.getIdTypeValue().equals(idTypeValue)){
						 this.getFormHM().put("birthdayName",BirthdayName);
		                 this.getFormHM().put("ageName",ageName);
		                 this.getFormHM().put("axName",axName);
					 }else{
						 this.getFormHM().put("birthdayName", "");
		                 this.getFormHM().put("ageName", "");
		                 this.getFormHM().put("axName", "");
					 }
					 this.getFormHM().put("id_type", idType);
					 
					 FieldItem caidItem = DataDictionary.getFieldItem(srcName, "a01");
				     FieldItem birthItem = DataDictionary.getFieldItem(BirthdayName, "a01");
				     FieldItem ageItem = DataDictionary.getFieldItem(ageName, "a01");
				     FieldItem axItem = DataDictionary.getFieldItem(axName, "a01");		  
					 if(PubFunc.idCardValidate(onlyValue)){
		                 RecruitUtilsBo Calculation = new RecruitUtilsBo();
		                 String birthDay = Calculation.getBirthDay(onlyValue);
		                 String age = Calculation.getAge(onlyValue);
		                 String sex = Calculation.getSex(onlyValue);            
		                 SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
		                 Timestamp date = new Timestamp(sdf.parse(birthDay).getTime());
		                 
		                 if(!"".equalsIgnoreCase(BirthdayName) && !"".equalsIgnoreCase(srcName) &&  birthItem!= null && "1".equalsIgnoreCase( birthItem.getUseflag())){                                
		                     StringBuffer sql = new StringBuffer();
		                     sql.append("update " +dbName+"A01 ");
		                     sql.append(" set "+BirthdayName+" = ? ");
		                     sql.append(" where "+srcName+"=?");
		                     ArrayList list = new ArrayList();
		                     list.add(date);                   
		                     list.add(onlyValue);
		                     dao.update(sql.toString(), list);   
		                 }
		                 
		                 if(!"".equalsIgnoreCase(ageName) && !"".equalsIgnoreCase(srcName) && ageItem != null && "1".equalsIgnoreCase(ageItem.getUseflag()) ){                                   
		                     StringBuffer sql = new StringBuffer();
		                     sql.append("update " +dbName+"A01 ");
		                     sql.append(" Set "+ageName+" = ? ");
		                     sql.append(" where "+srcName+"=?");
		                     ArrayList list = new ArrayList();
		                     list.add(age);                    
		                     list.add(onlyValue);
		                     dao.update(sql.toString(), list); 
		                 }
		                 
		                 if(!"".equalsIgnoreCase(axName) && !"".equalsIgnoreCase(srcName) && axItem != null && "1".equalsIgnoreCase(axItem.getUseflag()) ){                   
		                     StringBuffer sql = new StringBuffer();
		                     sql.append("update " +dbName+"A01 ");
		                     sql.append(" Set "+axName+" = ? ");
		                     sql.append(" where "+srcName+"=?");
		                     ArrayList list = new ArrayList();
		                     list.add(sex);                    
		                     list.add(onlyValue);
		                     dao.update(sql.toString(), list);                      
		                 }                
		                                         
		             }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				throw GeneralExceptionHandler.Handle(new Exception("注册信息不完整，无法注册！"));
			}
		
			String resumeStateFieldIds="";
			String status= "";
			
			DbWizard dbWizard = new DbWizard(this.getFrameconn());
			if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0){
				if(dbWizard.isExistField(dbName+"A01", "", false)){
					resumeStateFieldIds=(String)map.get("resume_state");
					status=bo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
				}
			}
			String canPrintAdmissionCardStatus=SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
			canPrintAdmissionCardStatus=","+canPrintAdmissionCardStatus+",";
			if(canPrintAdmissionCardStatus.indexOf((","+status+","))>-1)
				bo.setVisiablSeqField(true);
			
			String acountBeActived=(String)map.get("acountBeActived");
			acountBeActived=acountBeActived==null?"":acountBeActived;
	    	if("1".equals(acountBeActived))//发送激活帐号邮件
	    		this.getFormHM().put("a0100","");
	    	else
	    		this.getFormHM().put("a0100",a0100);
			this.getFormHM().put("person_type",person_type);
			
			ArrayList list=bo.getZpFieldList();
			//招聘渠道，如果注册时候未选择社会还是校园，默认社会
			String tem = (String)this.getFormHM().get("hireChannel");
			String hireChannel = StringUtils.isEmpty(tem) ? "02" : tem;
			//定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
			//headHire、猎头招聘    01、校园招聘   02、社会招聘
			if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
				hireChannel = "02";
			
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId)) {
				String channelName = bo.getChannelName(candidate_status_itemId,a0100,candidate_status);
				this.getFormHM().put("channelName", channelName);
				hireChannel = candidate_status;
			}
            
			list=bo.getSetByWorkExprience(hireChannel);
			ArrayList resumeFieldList=bo.getResumeFieldList((ArrayList)list.get(0),(HashMap)list.get(2),0,(HashMap)list.get(1),a0100,dbName,"1");
			String maxFileSize = "";
			if(map != null && map.get("maxFileSize") != null)
				maxFileSize = (String) map.get("maxFileSize");
			
			maxFileSize = maxFileSize == null || "0".equalsIgnoreCase(maxFileSize) ? "10" : maxFileSize;
			//设置必填的子集
			ArrayList  fieldSetMustList = new ArrayList(); //2016/7/4
			fieldSetMustList = (ArrayList)list.get(4);
			/**简历是否可修改*/
			String writeable=bo.getWriteable(dao, a0100, dbName);
			this.getFormHM().put("hireChannel",hireChannel);
			this.getFormHM().put("fieldSetList",(ArrayList)list.get(0));
			this.getFormHM().put("fieldMap",(HashMap)list.get(1));
			this.getFormHM().put("currentSetID","0");
			this.getFormHM().put("resumeFieldList",resumeFieldList);
			this.getFormHM().put("isPhoto","0");
			this.getFormHM().put("maxFileSize", maxFileSize);
			this.getFormHM().put("fieldSetMustList",fieldSetMustList);
			this.getFormHM().put("writeable", writeable);
			String isOnlyChecked="0";
			String onlyField="";
			if(bo.isOnlyChecked())
			{
				isOnlyChecked="1";
				onlyField=EmployNetPortalBo.isOnlyChecked;
			}
			
			isAttach = bo.getIsAttach(map, hireChannel, isAttach);
			HttpSession session=(HttpSession)this.getFormHM().get("session");
			this.getFormHM().put("onlyField",onlyField);
			this.getFormHM().put("isOnlyCheck", isOnlyChecked);
			this.getFormHM().put("isAttach",isAttach);
			this.getFormHM().put("isUpPhoto",isUpPhoto);
			this.getFormHM().put("isExp",isExp);
			this.getFormHM().put("opt","1");
			this.getFormHM().put("activeValue","1");
			this.getFormHM().put("workExperience", value);
			this.getFormHM().put("onlyName", onlyname==null?"":onlyname);
			this.getFormHM().put("paramFlag", paramFlag);
			this.getFormHM().put("hdtusername",txtEmail);
			if(session!=null){
				UserView userview=new UserView(txtEmail,pwd1,this.getFrameconn());
		        userview.setUserId(a0100);
		        userview.setA0100(a0100);
		        userview.setUserEmail(txtEmail);
		        userview.setDbname(dbName);
		        userview.setUserName(txtEmail);
		        userview.getHm().put("isEmployee","1");
		        session.setAttribute(WebConstant.userView, userview);
		        userview.getHm().put("isHeadhunter", "0");//20150731 xiexd 新用户注册时将其指定是否猎头招聘用户
			}
			this.getFormHM().remove("pwd1"); //20140812 基于安全考虑，避免返回信息中带有password信息
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try {
				//注册发生异常删掉已生成信息
				//在这里要将注册的信息给删除掉，否则再次注册会导致无法注册
				RecordVo vo=new RecordVo(dbName+"A01");
				vo.setString("a0100",a0100);
				dao.deleteValueObject(vo);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	

	/**
	 * 添加人员信息
	 * @param dao
	 * @param flag
	 * @param paramMap
	 * @throws GeneralException
	 */
	private void addA01(ContentDAO dao, boolean flag, HashMap<String, String> paramMap) throws GeneralException {
		RowSet search = null;
		try
		{
			String emailColumn=ConstantParamter.getEmailField().toLowerCase();
			this.getFormHM().put("emailColumn",emailColumn);//注册完成后  使简历邮箱不能改
			RecordVo vo=new RecordVo(paramMap.get("tableName"));
			vo.setString("a0100",paramMap.get("a0100"));
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null) {
	    		vo.setString("username",paramMap.get("username"));
	    		vo.setString("userpassword",paramMap.get("userpassword"));
	    		vo.setString("a0101",paramMap.get("a0101"));
	    		//注册简历时添加guidkey
	    		String sql = "select guidkey from "+paramMap.get("tableName")+" where a0100="+paramMap.get("a0100");
				search = dao.search(sql);
				if(search.next()){
					if(StringUtils.isEmpty(search.getString("guidkey"))){
						UUID uuid = UUID.randomUUID();
						String tmpid = uuid.toString(); 
						vo.setString("guidkey", tmpid.toUpperCase());
					}
				}
			    if(flag) {
			    	vo.setString("b0110",paramMap.get("b0110"));
			    	vo.setString(paramMap.get("activeField").toLowerCase(), "1");
			    }
			    
		    	vo.setDate("createtime",Calendar.getInstance().getTime());
		    	if(emailColumn!=null&&emailColumn.length()>1)
		    		vo.setString(emailColumn,paramMap.get("username"));
			//设置人员状态
		    	ParameterXMLBo bo=new ParameterXMLBo(this.getFrameconn(),"1");
		    	HashMap map=bo.getAttributeValues();
		    	if(map.get("id_type")!=null) {
					String idType=(String)map.get("id_type");
					if(StringUtils.isNotEmpty(idType))
						vo.setString(idType,paramMap.get(idType));
				}
		    	
		    	DbWizard dbWizard = new DbWizard(frameconn);
		    	
		    	String candidate_status_itemId = paramMap.get("candidate_status_itemId");
		    	if(!"#".equals(candidate_status_itemId)&&StringUtils.isNotEmpty(candidate_status_itemId))
		    		if(dbWizard.isExistField(paramMap.get("tableName"), candidate_status_itemId, false))//判断是否已构库，第一个参数表名，第二个字段名，第三个参数false不抛异常
		    			vo.setString(candidate_status_itemId,paramMap.get("candidate_status"));
		    	
		    	String itemDesc = "";
		    	if(map!=null && map.get("person_type")!=null){
		    		itemDesc = ((String)map.get("person_type")).toLowerCase();
		    		if(dbWizard.isExistField(paramMap.get("tableName"), itemDesc, false))//判断是否已构库，第一个参数表名，第二个字段名，第三个参数false不抛异常
		    			vo.setString(itemDesc,paramMap.get("person_type"));
		    	}
			    if(!"3".equals(paramMap.get("paramFlag")) && StringUtils.isNotEmpty(paramMap.get("blackFieldValue"))
			            && StringUtils.isNotEmpty(paramMap.get("blackField")) && !"#".equals(paramMap.get("blackField").trim()))
			    	vo.setString(paramMap.get("blackField").toLowerCase(), paramMap.get("blackFieldValue"));

			    if("1".equals(paramMap.get("isDefineWorkExperience")))
			    	vo.setString(paramMap.get("workExperience").toLowerCase(), paramMap.get("workExperienceValue"));
			    
			    if(!"1".equals(paramMap.get("paramFlag")))
			    	vo.setString(paramMap.get("onlyname").toLowerCase(), paramMap.get("onlyValue"));
			    
			    if(map!=null && map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
			    {
			    	String acountBeActived=(String)map.get("acountBeActived");
			    	if("1".equals(acountBeActived))
			    		vo.setString("state", "0");
			    }
	    		dao.updateValueObject(vo);
	    		String acountBeActived=(String)map.get("acountBeActived");
	    		acountBeActived=acountBeActived==null?"":acountBeActived;
		    	if("1".equals(acountBeActived))//发送激活帐号邮件
		    	{
		    		String why=SystemConfig.getPropertyValue("masterName");
		 			if(why==null|| "".equals(why))
		 				why="";
		 			String str=why;
		    		String url_p=(String)this.getFormHM().get("url_addr");
		    		//解决转发后获取不到正确地址的问题
		    		String hrp_logon_url=SystemConfig.getProperty("hrp_logon_url");
		    		if(StringUtils.isNotEmpty(hrp_logon_url))
		    			url_p = hrp_logon_url;
		    		else
		    			Category.getInstance("com.hrms.frame.dao.ContentDAO").error("system.properties文件中未设置hrp_logon_url！！");
		    		url_p=url_p.replaceAll("／／", "//");
		    		String url_p2=(String)this.getFormHM().get("url_addr40");
		    		url_p2=url_p2.replaceAll("／／", "//");
	    		    EMailBo emb=null;
	    	 	    try
	        	    {
	    	 		   emb= new EMailBo(this.getFrameconn(),true,"");
	        	    }
	        	    catch(Exception e)
	        	    {
	        		   e.printStackTrace();
	        		   //在这里要将注册的信息给删除掉，否则再次注册会导致无法注册
	        		   dao.deleteValueObject(vo);
	        		   throw GeneralExceptionHandler.Handle(new Exception("系统邮件服务器配置或网络连接不正确,请联系系统管理员!"));
	        	    }
		    		 AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.getFrameconn());
		        	 String from_addr=autoSendEMailBo.getFromAddr();
		        	 String title=str+"招聘网帐号激活邮件";
		        	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		        	 StringBuffer context = new StringBuffer();
		        	 Calendar calendar = Calendar.getInstance(); //发送激活邮件的时间
		        	 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        	 String activeDate =  format1.format(calendar.getTime());
		        	 activeDate=PubFunc.encryption(activeDate);
		        	 if("".equals(url_p)){
		        		 context.append(paramMap.get("a0101")+"&nbsp;&nbsp;您好:\r\n");
			        	 context.append("您在"+str+"招聘网的帐号已经注册成功，请点击下面链接激活该帐号。<br><br>");
			        	 context.append("<a href=\""+url_p2+"/hire/employNetPortal/search_zp_position.do?b_activecount=active&activeid="+PubFunc.convertTo64Base(paramMap.get("a0100"))+"&activeDate="+activeDate+"\"");
			        	 context.append(" target=\"_blank\">激活帐号</a><br><br>");
			        	 context.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+format.format(new Date()));
			        	 emb.sendEmail(title,context.toString(),"",from_addr,paramMap.get("username"));
		        	 
		        	 }else{
		        		 context.append(paramMap.get("a0101")+"&nbsp;&nbsp;您好:\r\n");
			        	 context.append("您在"+str+"招聘网的帐号已经注册成功，请点击下面链接激活该帐号。<br><br>");
			        	 context.append("<a href=\""+url_p+"/hire/hireNetPortal/search_zp_position.do?b_activecount=active&activeid="+PubFunc.convertTo64Base(paramMap.get("a0100"))+"&activeDate="+activeDate+"\"");
			        	 context.append(" target=\"_blank\">激活帐号</a><br><br>");
			        	 context.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+format.format(new Date()));
						try
						{
							emb.sendEmail(title,context.toString(),"",from_addr,paramMap.get("username"));
						} catch(Exception e)
						{
							e.printStackTrace();
							//在这里要将注册的信息给删除掉，否则再次注册会导致无法注册
							dao.deleteValueObject(vo);
							throw GeneralExceptionHandler.Handle(new Exception("系统邮件服务器配置或网络连接不正确,请联系系统管理员!"));
						}
		        	 }
		    	}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(search);
		}
	}

}
