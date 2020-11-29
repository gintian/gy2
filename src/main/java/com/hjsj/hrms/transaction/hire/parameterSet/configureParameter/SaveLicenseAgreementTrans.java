package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveLicenseAgreementTrans extends IBusiness{

	public void execute() throws GeneralException {
	
		try
		{
			String l_p_type=(String)this.getFormHM().get("l_p_type");
			String licenseAgreement=(String)this.getFormHM().get("licenseAgreement");
			if(licenseAgreement!=null&&licenseAgreement.trim().length()!=0){
				licenseAgreement=PubFunc.keyWord_reback(licenseAgreement);
			}
			String parameterName="";
			String paramChanese="招聘许可协议";
			if(l_p_type==null|| "l".equalsIgnoreCase(l_p_type))
			{
				parameterName="ZP_LICENSE_AGREEMENT";
			}
			else{
				parameterName="ZP_PROMPT_CONTENT";
				paramChanese="招聘外网提示信息";
			}
			String sql="select * from constant where UPPER(constant)='"+parameterName+"'";
			ContentDAO dao =new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			boolean flag=false;
			while(this.frowset.next())
			{
				flag=true;
			}
			if(flag)
			{
				String del="delete from constant where UPPER(constant)='"+parameterName+"'";
				dao.delete(del,new ArrayList());
			}
			RecordVo vo = new RecordVo("constant");
			vo.setString("constant", parameterName);
			vo.setString("type", "A");
			vo.setString("describe", paramChanese);
			vo.setString("str_value", licenseAgreement.trim());
			dao.addValueObject(vo);
			 ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			if(l_p_type==null|| "l".equalsIgnoreCase(l_p_type))
			{
			   ParameterSetBo.license_agreement=null;
			   bo.getLicense_agreement();
			}else
			{
				ParameterSetBo.prompt_content=null;
				bo.getPrompt_content();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
