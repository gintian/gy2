package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.ChartParameterCofig;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitParamConfigTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String relationType=(String)this.getFormHM().get("relationType");
			RelationMapBo.chartParam=null;
			RelationMapBo bo=new RelationMapBo(this.getFrameconn(),this.userView,relationType); 
			ChartParameterCofig cpc=RelationMapBo.chartParam;
			this.getFormHM().put("chartParam", cpc);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
