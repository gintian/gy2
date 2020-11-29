package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.ChartParameterCofig;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SaveParamConfigTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String show_pic = (String)map.get("show_pic");
			map.remove("show_pic");
			ChartParameterCofig cpc = (ChartParameterCofig)this.getFormHM().get("chartParam");
			cpc.setShow_pic(show_pic);
			String relationType=(String)this.getFormHM().get("relationType");
			RelationMapBo rmb= new RelationMapBo(this.getFrameconn(),this.userView,relationType);
			rmb.saveParameterConfig(cpc);
			RelationMapBo.chartParam=null;
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
