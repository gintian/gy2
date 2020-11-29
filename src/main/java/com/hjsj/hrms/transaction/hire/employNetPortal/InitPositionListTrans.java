package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitPositionListTrans extends IBusiness{

	public void execute() throws GeneralException {
		
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				ArrayList conditionFieldList= new ArrayList();
				ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
				HashMap map=xmlBo.getAttributeValues();
				String isAttach="0";
				if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
					isAttach=(String)map.get("attach");
				EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
				ArrayList unitList=new ArrayList();
				HashMap unitPosMap=employNetPortalBo.getPositionInterviewMap(conditionFieldList,unitList,"out");				
				this.getFormHM().put("unitList",employNetPortalBo.getUnitList(unitPosMap,unitList, "3"));
				this.getFormHM().put("unitPosMap",unitPosMap);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
