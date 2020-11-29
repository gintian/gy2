package com.hjsj.hrms.transaction.kq.options.formula;

import com.hjsj.hrms.businessobject.kq.options.formula.KqFormulaBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

public class EditFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String optType=(String)hm.get("optType");
		String kqAlert ="";
		String kqFormulaName="";
		String chkid="";
		if(!"1".equals(optType))
		{
			chkid=(String)hm.get("chkid");
			KqFormulaBo stb = new KqFormulaBo(this.getFrameconn());
			LazyDynaBean bean=stb.getFormulaInfo(chkid);
			kqAlert = (String)bean.get("information");
			kqFormulaName=(String)bean.get("name");
		}
		this.getFormHM().put("kqFormulaId", chkid);
		this.getFormHM().put("optType", optType);
		this.getFormHM().put("kqFormulaName", kqFormulaName);
		this.getFormHM().put("kqAlert", kqAlert);
	}

}
