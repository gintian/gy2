package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
public class SaveFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemid = (String)this.getFormHM().get("itemid");
			String formula = (String)this.getFormHM().get("formula");
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
			bo.setFormula(itemid, PubFunc.keyWord_reback(SafeCode.decode(formula)));
			bo.saveParameters();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
