package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

public class ImportDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String tab_id=(String)this.getFormHM().get("tab_id");
			FormFile file=(FormFile)this.getFormHM().get("templateFile");
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,tab_id);
			String info = bo.importDataFactory(file);
			if(!"0".equals(info)){
				throw new GeneralException("",info,"","");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
