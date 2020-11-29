package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class UpdateSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sortstr = (String)this.getFormHM().get("sortstr");
		try{
			BatchBo batchbo = new BatchBo();
			if(sortstr.length()>0){
				if(batchbo.saveSort(this.frameconn,sortstr)){
					//check="ok";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
