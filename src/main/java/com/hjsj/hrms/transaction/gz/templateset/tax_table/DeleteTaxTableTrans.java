package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteTaxTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String ids=(String)this.getFormHM().get("deleteIds");
			TaxTableSetBo bo= new TaxTableSetBo(this.getFrameconn());
			String[] idArr= ids.split(",");
			StringBuffer sb= new StringBuffer();
			for(int i=0;i<idArr.length;i++){
				sb.append(",'");
				sb.append(idArr[i]);
				sb.append("'");
			}
			bo.deleteTaxTable(sb.toString().substring(1));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
