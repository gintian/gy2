package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitRelationMapTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String relationType=(String)this.getFormHM().get("relationType");
			this.getFormHM().put("relationType",relationType);
			
			//清空a_code，解决进入后加载上次的汇报关系图
			this.getFormHM().put("a_code", null);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
