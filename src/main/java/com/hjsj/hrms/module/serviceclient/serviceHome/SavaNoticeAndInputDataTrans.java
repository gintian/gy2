package com.hjsj.hrms.module.serviceclient.serviceHome;

import com.hjsj.hrms.module.serviceclient.serviceHome.businessobject.SaveClientInputDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

public class SavaNoticeAndInputDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			MorphDynaBean fieldData = (MorphDynaBean) this.formHM.get("fieldData");
			SaveClientInputDataBo saveInputDataBo = new SaveClientInputDataBo(this.userView, this.frameconn);
			saveInputDataBo.saveClientInputData(fieldData);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
