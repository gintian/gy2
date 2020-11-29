package com.hjsj.hrms.transaction.hire.innerEmployNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitInnerEmployPosTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			
			if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
			ArrayList unitList=new ArrayList();
			HashMap unitPosMap=employNetPortalBo.getPositionInterviewMap(new ArrayList(),unitList,"03");	
		//	HashMap unitPosMap=employNetPortalBo.getPositionInterviewMap(new ArrayList(),unitList,"out");	
			this.getFormHM().put("unitList",employNetPortalBo.getUnitList(unitPosMap,unitList, "3"));
			this.getFormHM().put("unitPosMap",unitPosMap);
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String positionNumber="7";
			if(map!=null&&map.get("positionNumber")!=null&&!"".equals(((String)map.get("positionNumber")).trim()))
				positionNumber=(String)map.get("positionNumber");
			this.getFormHM().put("posCount", positionNumber);
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	

	}

}
