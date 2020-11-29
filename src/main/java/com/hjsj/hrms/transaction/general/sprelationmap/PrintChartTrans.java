package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PrintChartTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			
			String xml=(String)this.getFormHM().get("printXmlData");
			RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.userView);
			String xmlData = PubFunc.keyWord_reback(SafeCode.decode(xml));
			HashMap map = bo.parseXml(xmlData);
			this.getFormHM().put("printDataMap", map);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
