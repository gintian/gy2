package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InitRegisterTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			EmployNetPortalBo enpb=new EmployNetPortalBo(this.getFrameconn());
			String onlyname = enpb.getOnly_field();
			String paramFlag="1";
			if(onlyname!=null&&!"".equals(onlyname))
				paramFlag="2";
			
			String codeId = "";
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			String complexPassword="";//是否使用复杂密码 0未使用 1使用
			String passwordMinLength="";//密码最小长度
			String passwordMaxLength="";//密码最大长度
			String cultureCode="#";
			String cultureCodeItem="#";
			String candidate_status="#";//应聘身份指标
			if(map!=null) {
				if(map.get("complexPassword")!=null)
					complexPassword=(String)map.get("complexPassword");
				if(map.get("passwordMinLength")!=null)
					passwordMinLength=(String)map.get("passwordMinLength");
				if(map.get("passwordMaxLength")!=null)
					passwordMaxLength=(String)map.get("passwordMaxLength");
				if(map.get("culture_code")!=null)
					cultureCode=(String)map.get("culture_code");
				if(map.get("culture_item")!=null)
					cultureCodeItem=(String)map.get("culture_item");
				if(StringUtils.isEmpty(cultureCode)|| "#".equals(cultureCode)
						||StringUtils.isEmpty(cultureCodeItem)|| "#".equals(cultureCodeItem))
					this.getFormHM().put("isDefinitionCulture", "0");
				else
					this.getFormHM().put("isDefinitionCulture","1");
				
				if(map.get("candidate_status")!=null) {
					candidate_status=(String)map.get("candidate_status");
					if(StringUtils.isNotEmpty(candidate_status)&&!"#".equals(candidate_status)) {
						FieldItem fieldItem = DataDictionary.getFieldItem(candidate_status,"A01");
						ArrayList<CommonData> candidate_status_List = new ArrayList<CommonData>();
						Map<String,String> channelMap = parameterSetBo.getHireChannelList();
						for (Map.Entry<String,String> entry : channelMap.entrySet()) {  
							CommonData data = new CommonData(entry.getKey(), entry.getValue());
							candidate_status_List.add(data);
						}
						if(fieldItem==null)
							throw GeneralExceptionHandler.Handle(new Exception("应聘身份指标未构库"));
						this.getFormHM().put("candidate_status_desc", fieldItem.getItemdesc());
						this.getFormHM().put("candidate_status_List", candidate_status_List);
					}else {
						this.getFormHM().put("candidate_status_List", null);
					}
				}
				
				if(map.get("id_type")!=null) {
					String id_type=(String)map.get("id_type");
					if(StringUtils.isNotEmpty(id_type)&&!"#".equals(id_type)) {
						FieldItem fieldItem = DataDictionary.getFieldItem(id_type,"A01");
					    codeId= fieldItem.getCodesetid();
						ArrayList<CommonData> id_type_List = new ArrayList<CommonData>();
						RecruitUtilsBo utilsBo = new RecruitUtilsBo(this.frameconn);
						ArrayList codeItemList = utilsBo.getCodeItemMap(codeId, "1");
			            for(int i = 0; i<codeItemList.size();i++) {
			            	CodeItem item= (CodeItem) codeItemList.get(i);
			            	CommonData data = new CommonData(item.getCodeitem(),item.getCodename());
			            	id_type_List.add(data);
			            }
						
						if(fieldItem==null)
							throw GeneralExceptionHandler.Handle(new Exception("证件类型指标未构库"));
						this.getFormHM().put("id_type_desc", fieldItem.getItemdesc());
						this.getFormHM().put("id_type_List", id_type_List);
					}else {
						this.getFormHM().put("id_type_List", null);
					}
				}
				
			}
			
			
			String isDefinitionActive="2";
			//简历激活状态指标参数 7x中没有这个参数
//			if(map.get("active_field")!=null&&!((String)map.get("active_field")).equals(""))
//				isDefinitionActive="1";
			
			this.getFormHM().put("codeId", codeId);
			this.getFormHM().put("isDefinitionActive", isDefinitionActive);
			ArrayList cultureList=parameterSetBo.getCodeItemNet(cultureCode);
			this.getFormHM().put("cultureCodeItem",cultureCodeItem);
			this.getFormHM().put("cultureList", cultureList);
			this.getFormHM().put("hiddenCode","#");
			this.getFormHM().put("complexPassword",complexPassword);
			this.getFormHM().put("passwordMinLength",passwordMinLength);
			this.getFormHM().put("passwordMaxLength",passwordMaxLength);
			/**黑名单处理*/
			String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");
			String blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");
			String emailColumn=ConstantParamter.getEmailField().toLowerCase();
			FieldItem item=null;
			String fieldDesc="";
			String fieldSize="0";
			if(blacklist_field==null|| "".equals(blacklist_field)||blacklist_per==null|| "".equals(blacklist_per))
				this.getFormHM().put("blackField", "-1");
			else
			{
				if("2".equals(paramFlag))
				{
					if(onlyname.equalsIgnoreCase(blacklist_field) || emailColumn.equalsIgnoreCase(blacklist_field)
							|| "a0101".equalsIgnoreCase(blacklist_field)
							|| candidate_status.equalsIgnoreCase(blacklist_field)){
						paramFlag="3";
					}
				}
				
				item=DataDictionary.getFieldItem(blacklist_field);
				fieldDesc=item.getItemdesc();
				fieldSize=item.getItemlength()+"";
				this.getFormHM().put("blackField", blacklist_field);
			}
			if(blacklist_per==null|| "".equals(blacklist_per))
				this.getFormHM().put("blackNbase", "");
			else
				this.getFormHM().put("blackNbase",blacklist_per);
			this.getFormHM().put("blackFieldDesc", fieldDesc);
			this.getFormHM().put("blackFieldSize", fieldSize);
			String cilentName=SystemConfig.getPropertyValue("clientName");
			String promptContent=parameterSetBo.getPrompt_content();
			if("zjz".equals(cilentName)&&promptContent!=null&&!"".equals(promptContent.trim()))
				this.getFormHM().put("isPrompt", "1");
			else
				this.getFormHM().put("isPrompt", "0");
			String workExperience=enpb.getWorkExperience();
            if("1".equals(EmployNetPortalBo.isDefineWorkExperience))
            {
            	this.getFormHM().put("workExperienceDesc", EmployNetPortalBo.workExperienceDesc);
            	this.getFormHM().put("workExperienceCodeList", EmployNetPortalBo.workExperienceCodeList);
            }
            else
            {
            	
            	this.getFormHM().put("workExperienceDesc", "");
            	this.getFormHM().put("workExperienceCodeList", new ArrayList());
            }
            this.getFormHM().put("workExperience", "");
            this.getFormHM().put("isDefineWorkExperience", EmployNetPortalBo.isDefineWorkExperience);
            if("2".equals(paramFlag)|| "3".equals(paramFlag))
            {
            	item=DataDictionary.getFieldItem(onlyname.toLowerCase());
            	this.getFormHM().put("onlyName", onlyname.toLowerCase());
            	this.getFormHM().put("onlyNameDesc", item.getItemdesc());
            	this.getFormHM().put("onlyValue", "");
            	this.getFormHM().put("onlySize", item.getItemlength()+"");
            }
            else
            {
            	this.getFormHM().put("onlyName", item==null?"":item.getItemid().toLowerCase());
            	this.getFormHM().put("onlyNameDesc", item==null?"":item.getItemdesc());
            	this.getFormHM().put("onlyValue", "");
            	this.getFormHM().put("onlySize", item==null?"":item.getItemlength()+"");
            }
            String acountBeActived="0";
            if(map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
		    {
		    	acountBeActived=(String)map.get("acountBeActived");
		    }
            this.getFormHM().put("acountBeActived", acountBeActived);
            this.getFormHM().put("acountActivedValue", "0");
            this.getFormHM().put("paramFlag", paramFlag);
            
            //是否显示 忘记帐号 
            String accountFlag = enpb.checkAccount();
            this.getFormHM().put("accountFlag", accountFlag);
            
            /**得到系统的身份证指标**/
            String cardid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");
            this.getFormHM().put("cardid", cardid.toLowerCase());
			/**清除无用内容 防止内存溢出**/
//			this.getFormHM().put("posFieldList", null);
//			this.getFormHM().put("zpPosList", null);
//			this.getFormHM().put("unitPosMap", null);
//			this.getFormHM().put("uploadFileList", null);
//			this.getFormHM().put("unitIntroduce", null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
