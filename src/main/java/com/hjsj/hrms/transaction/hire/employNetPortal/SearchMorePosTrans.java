package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchMorePosTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String chl_id=(String)this.getFormHM().get("chl_id");
			String hireChannelParam=(String) hm.get("hireChannel");
			String hireChannel=(String)this.getFormHM().get("hireChannel")!=null?(String)this.getFormHM().get("hireChannel"):hireChannelParam; 
			String zpUnitCode=(String)this.getFormHM().get("zpUnitCode");
			hireChannel=PubFunc.getReplaceStr(hireChannel);
			zpUnitCode=PubFunc.getReplaceStr(zpUnitCode);
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			ArrayList conditionFieldList=null;
			employNetPortalBo.setHireChannel(hireChannel);
			conditionFieldList=employNetPortalBo.getPosQueryConditionList(hireChannel,"pos_com_query");
			String lfType="0";
			String unitLevel = "";
	        if(map != null && map.get("unitLevel") != null)
	            unitLevel = (String) map.get("unitLevel");
			
			ArrayList unitList  = new ArrayList();
			unitList  =employNetPortalBo.getAllZpUnitList(hireChannel, unitLevel);
			if(map!=null&&map.get("lftype")!=null)
				lfType=(String)map.get("lftype");
			if((zpUnitCode==null|| "".equals(zpUnitCode))&&unitList.size()>0)
			{
				LazyDynaBean bean = (LazyDynaBean)unitList.get(0);
				zpUnitCode=(String)bean.get("codeitemid");
			}
			String isDefinitinn="0";
			ParameterSetBo psb=new ParameterSetBo(this.getFrameconn());
			String licenseAgreement=psb.getLicense_agreement();
			/**当预览简历时，是否可以使用打印和卡片功能，默认是可以的*/
			String canPrint="1";
			String resumeStateFieldIds="";
			if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
				resumeStateFieldIds=(String)map.get("resume_state");
			String a0100=(String)this.getFormHM().get("a0100");
			if(a0100!=null&&a0100.trim().length()!=0){
				String dbName=employNetPortalBo.getZpkdbName();	
				ContentDAO dao  = new ContentDAO(this.getFrameconn());
				String canPrintResumeStatus=SystemConfig.getPropertyValue("canPrintResumeStatus");
				String status=employNetPortalBo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
				if(canPrintResumeStatus!=null&&!"".equals(canPrintResumeStatus))
				{
					canPrintResumeStatus=","+canPrintResumeStatus+",";
					if(canPrintResumeStatus.indexOf((","+status+","))!=-1)
						canPrint="1";
					else
						canPrint="0";
				}
				this.getFormHM().put("canPrint", canPrint);
				String admissionCard="#";
				if(map.get("admissionCard")!=null&&!"".equals((String)map.get("admissionCard")))
				{
					admissionCard=(String)map.get("admissionCard");
				}
				/*String canPrintAdmissionCardStatus=SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
				canPrintAdmissionCardStatus=","+canPrintAdmissionCardStatus+",";
				if(canPrintAdmissionCardStatus.indexOf((","+status+","))==-1)
				{
					admissionCard="#";
				}*/
				this.getFormHM().put("admissionCard",admissionCard);
			}
			if(licenseAgreement!=null&&!"".equals(licenseAgreement))
				isDefinitinn="1";
			this.getFormHM().put("unitList", unitList);
			this.getFormHM().put("zpUnitCode", zpUnitCode);
			this.getFormHM().put("commQueryList",conditionFieldList);
			this.getFormHM().put("hireChannel", hireChannel);
			this.getFormHM().put("commQueryList", conditionFieldList);
			this.getFormHM().put("chl_id",chl_id);
			this.getFormHM().put("isDefinitinn", isDefinitinn);
			this.getFormHM().put("licenseAgreement", licenseAgreement);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	

}
