package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateFormulaBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectFormulaListTrans extends IBusiness {

	public void execute() throws GeneralException {				
		try
		{
			HashMap requestPamaHM = (HashMap)this.getFormHM().get("requestPamaHM");
			String busiid=(String)requestPamaHM.get("busiid");
			String s0100=(String)requestPamaHM.get("s0100");
			ArrayList list=new ArrayList();
			if(busiid==null|| "".equalsIgnoreCase(busiid))
				return;
	
			PieceRateFormulaBo formulabo = new PieceRateFormulaBo(this.getFrameconn(),"",this.userView);
			list=formulabo.getFormulaList(busiid,"");
			ArrayList formulaList=new ArrayList();
			
			for(int i=0;i<list.size();i++)
			{
				DynaBean abean=(DynaBean)list.get(i);
		        formulaList.add(abean);

			}
			this.getFormHM().put("formulalist", formulaList);
			this.getFormHM().put("s0100", s0100);
			this.getFormHM().put("busiid", busiid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

		


	}

}
