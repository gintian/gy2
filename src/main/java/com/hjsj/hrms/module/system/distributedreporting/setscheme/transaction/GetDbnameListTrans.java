package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @version: 1.0
 * @Description: 获得所有的人员库 
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:59:23
 */
public class GetDbnameListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(this.userView, this.frameconn);
			ArrayList<HashMap<String,String>> dbnamelist = bo.getDbnameList();
			this.getFormHM().put("list", dbnamelist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
