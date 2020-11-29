package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

public class EditTrainFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String optType=(String)hm.get("optType");
		String trainAlert ="";
		String trainFormulaName="";
		String chkid="";
		if(!"1".equals(optType))
		{
			chkid=(String)hm.get("chkid");
			TrainClassBo stb = new TrainClassBo(this.getFrameconn());
			LazyDynaBean bean=stb.getFormulaInfo(chkid);
			trainAlert = (String)bean.get("information");
			trainFormulaName=(String)bean.get("name");
		}
		this.getFormHM().put("trainFormulaId", chkid);
		this.getFormHM().put("optType", optType);
		this.getFormHM().put("trainFormulaName", trainFormulaName);
		this.getFormHM().put("trainAlert", trainAlert);
	}

}
