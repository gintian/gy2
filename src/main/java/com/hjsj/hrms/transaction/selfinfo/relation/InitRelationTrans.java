package com.hjsj.hrms.transaction.selfinfo.relation;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitRelationTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			if(userView.getStatus()!=4){
				String a0100=userView.getA0100();
				if(a0100==null||a0100.length()==0)
					throw new GeneralException("",ResourceFactory.getProperty("selfservice.module.pri"),"","");
			}

		}catch(Exception e){
			e.printStackTrace();
			throw com.hrms.struts.exception.GeneralExceptionHandler.Handle(e);
		}
	}

}
