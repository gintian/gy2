package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAllPosTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String hireChannel=(String)this.getFormHM().get("hireChannel");
			String zpUnitCode=(String)this.getFormHM().get("zpUnitCode");
			hireChannel=PubFunc.getReplaceStr(hireChannel);
			zpUnitCode=PubFunc.getReplaceStr(zpUnitCode);
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			ArrayList conditionFieldList=(ArrayList)this.getFormHM().get("commQueryList");
			employNetPortalBo.setHireChannel(hireChannel);
			
			String lfType="0";
			if(map!=null&&map.get("lftype")!=null)
				lfType=(String)map.get("lftype");
			String positionNumber="7";
			if(map!=null&&map.get("positionNumber")!=null&&!"".equals(((String)map.get("positionNumber")).trim()))
				positionNumber=(String)map.get("positionNumber");
			this.getFormHM().put("positionNumber", positionNumber);
			ArrayList zpPosList=employNetPortalBo.getPositionByUnitCode(conditionFieldList, hireChannel, zpUnitCode,"1");
			this.getFormHM().put("lfType",lfType);
			this.getFormHM().put("hireChannel", hireChannel);
			String hireMajor="-1";
			if(map.get("hireMajor")!=null&&!"".equals((String)map.get("hireMajor")))
				hireMajor=(String)map.get("hireMajor");
			this.getFormHM().put("hireMajor", hireMajor);
			this.getFormHM().put("zpPosList", zpPosList);
			this.getFormHM().put("zpUnitCode", zpUnitCode);
			this.getFormHM().put("posFieldList", employNetPortalBo.getPosListField());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
