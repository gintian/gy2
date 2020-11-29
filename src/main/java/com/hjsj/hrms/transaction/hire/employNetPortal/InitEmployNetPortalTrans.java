package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitEmployNetPortalTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String hireChannel="out";
			if(hm.get("hireChannel")!=null)
			{
				hireChannel=(String)hm.get("hireChannel");
				hm.remove("hireChannel");
			}else
			{
				if(this.getFormHM().get("hireChannel")!=null)
				{
					hireChannel=(String)this.getFormHM().get("hireChannel");
				}
			}
			hireChannel=PubFunc.getReplaceStr(hireChannel);
			this.getFormHM().put("hireChannel", hireChannel);
			/*
			
			String operate=null;
			String abcd="0";
			if(hm.get("abcd")!=null)
			{
				abcd=(String)hm.get("abcd");
				hm.remove("abcd");
			}
			
			if(hm!=null)
			{
				operate=(String)hm.get("operate");
				String b_query=(String)hm.get("b_query");
				if(b_query==null||b_query.equals("link0"))
				{
					operate="init";
					Counter counter=new Counter(this.getFrameconn(),this.userView);
					counter.saveCount();
				}
				hm.remove("operate");
			}
			String hireChannel="out";
			if(hm.get("hireChannel")!=null)
			{
				hireChannel=(String)hm.get("hireChannel");
				hm.remove("hireChannel");
			}else
			{
				if(this.getFormHM().get("hireChannel")!=null)
				{
					hireChannel=(String)this.getFormHM().get("hireChannel");
				}
			}
			hireChannel=PubFunc.getReplaceStr(hireChannel);
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
			if(abcd.equals("1"))
			{
				String workExperience=employNetPortalBo.getWorkExperience();
				
				String value="";
				if(isDefineWorkExperience.equals("1"))
					value=(String)this.getFormHM().get("workExperience");
				if(value!=null&&!value.trim().equals(""))
				{
					if(value.equals("2"))
						hireChannel="01";
					else
						hireChannel="02";
						
				}
			}
			ArrayList conditionFieldList=null;
			String hire_object=(String)map.get("hire_object");
			if(hire_object==null||hire_object.length()==0)
			{
				throw new GeneralException("对不起,系统正在维护中...");
				
			}

			if(operate==null)
			{
				conditionFieldList=(ArrayList)this.getFormHM().get("conditionFieldList");
			}
			if(operate!=null||conditionFieldList==null||conditionFieldList.size()==0){
				conditionFieldList=employNetPortalBo.getPosQueryConditionList(hireChannel,"pos_query");
			}
			String dbName=employNetPortalBo.getZpkdbName();	
			this.getFormHM().put("dbName",dbName);
			ArrayList unitList=new ArrayList();
			HashMap unitPosMap=employNetPortalBo.getPositionInterviewMap(conditionFieldList,unitList,hireChannel);				
			this.getFormHM().put("unitList",employNetPortalBo.getUnitList(unitPosMap,unitList));
			this.getFormHM().put("unitPosMap",unitPosMap);
			this.getFormHM().put("conditionFieldList",conditionFieldList);
			this.getFormHM().put("isAttach",isAttach);
			String resumeActive="0";
			if(map!=null&&map.get("active_field")!=null&&!((String)map.get("active_field")).equals(""))
				resumeActive="1";
			this.getFormHM().put("resumeActive", resumeActive);
			String masterName="";
			String tempName=SystemConfig.getPropertyValue("masterName");   //招聘雇主名称
			if(tempName!=null)
				masterName=new String(tempName.getBytes("ISO8859_1"),"GB2312"); 
			this.getFormHM().put("masterName",masterName);
			String isDefinitinn="0";
			ParameterSetBo psb=new ParameterSetBo(this.getFrameconn());
			String licenseAgreement=psb.getLicense_agreement();
			if(licenseAgreement!=null&&!licenseAgreement.equals(""))
				isDefinitinn="1";
			String hireMajor="-1";
			if(map.get("hireMajor")!=null&&!((String)map.get("hireMajor")).equals(""))
				hireMajor=(String)map.get("hireMajor");
			this.getFormHM().put("hireMajor", hireMajor);
			String promptContent=psb.getPrompt_content();
			this.getFormHM().put("promptContent",promptContent==null?"":promptContent);
			String positionNumber="7";
			if(map!=null&&map.get("positionNumber")!=null&&!((String)map.get("positionNumber")).trim().equals(""))
				positionNumber=(String)map.get("positionNumber");
			this.getFormHM().put("positionNumber", positionNumber);
			this.getFormHM().put("isDefinitinn", isDefinitinn);
			this.getFormHM().put("licenseAgreement", licenseAgreement);
			if(EmployNetPortalBo.netHref==null||EmployNetPortalBo.netHref.equals(""))
			{
				if(map.get("net_href")!=null)
				{
					this.getFormHM().put("netHref", (String)map.get("net_href"));
					EmployNetPortalBo.netHref=(String)map.get("net_href");
				}
			}
			else
			{
				this.getFormHM().put("netHref", EmployNetPortalBo.netHref);
			}
			String isDefinitionActive="2";
			if(map.get("active_field")!=null&&!((String)map.get("active_field")).equals(""))
				isDefinitionActive="1";
			this.getFormHM().put("isDefinitionActive", isDefinitionActive);
			if(this.getFormHM().get("a0100")!=null&&!((String)this.getFormHM().get("a0100")).equals(""))
			{
				
				if(isDefinitionActive.equals("1"))
		    	{
					String field=(String)map.get("active_field");
					String a0100=(String)this.getFormHM().get("a0100");
					a0100=PubFunc.getReplaceStr(a0100);
				    String sql = "select "+field+" from "+dbName+"a01 where a0100='"+a0100+"'";
				    this.frowset=dao.search(sql);
				    while(this.frowset.next())
				    {
		        		this.getFormHM().put("activeValue", this.frowset.getString(field)==null?"1":this.frowset.getString(field));
				    }
		    	}
				String a0100=(String)this.getFormHM().get("a0100");
				String canPrintResumeStatus=SystemConfig.getPropertyValue("canPrintResumeStatus");
				//当预览简历时，是否可以使用打印和卡片功能，默认是可以的 
				String canPrint="1";
				String resumeStateFieldIds="";
				if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
					resumeStateFieldIds=(String)map.get("resume_state");
				String status=employNetPortalBo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
				if(canPrintResumeStatus!=null&&!canPrintResumeStatus.equals(""))
				{
					canPrintResumeStatus=","+canPrintResumeStatus+",";
					if(canPrintResumeStatus.indexOf((","+status+","))!=-1)
						canPrint="1";
					else
						canPrint="0";
				}
				this.getFormHM().put("canPrint", canPrint);
				String admissionCard="#";
				if(map.get("admissionCard")!=null&&!((String)map.get("admissionCard")).equals(""))
				{
					admissionCard=(String)map.get("admissionCard");
				}
				String canPrintAdmissionCardStatus=SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
				canPrintAdmissionCardStatus=","+canPrintAdmissionCardStatus+",";
				if(canPrintAdmissionCardStatus.indexOf((","+status+","))==-1||admissionCard.trim().length()==0)
				{
					admissionCard="#";
				}
				String previewTableId = "";
				if(map!=null&&map.get("preview_table")!=null)
				{
					previewTableId = (String)map.get("preview_table");
				}else{
					previewTableId="";
					
				}
				if(previewTableId.trim().length()==0){
					canPrint="0";
					this.getFormHM().put("canPrint", canPrint);
				}
				this.getFormHM().put("admissionCard",admissionCard);
			}
			String max_count = "";
			if(map.get("max_count")!=null)
				max_count = (String)map.get("max_count");
			this.getFormHM().put("max_count", max_count);
			this.getFormHM().put("hireChannel", hireChannel);
			HashMap runMap = employNetPortalBo.getRunHeaderList(dbName);
			ArrayList runHeaderList = new ArrayList();
			ArrayList runDataList = new ArrayList();
			if(runMap.size()>0)
			{
				runHeaderList=(ArrayList)runMap.get("list");
				runDataList = (ArrayList)runMap.get("dataList");
			}
			this.getFormHM().put("runHeaderList", runHeaderList);
			this.getFormHM().put("runDataList", runDataList);
			this.getFormHM().put("posFieldList", employNetPortalBo.getPosListField());
			
			*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	
	
	
	
	}
	
	
	

}
