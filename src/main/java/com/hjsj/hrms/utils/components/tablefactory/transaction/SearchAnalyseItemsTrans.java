package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchAnalyseItemsTrans extends IBusiness {

	public void execute() throws GeneralException {
          String subModuleId = this.formHM.get("subModuleId").toString();
          TableFactoryBO bo = new TableFactoryBO(subModuleId, userView, this.frameconn);
          ArrayList items = bo.searchAnalysePlanItems();
          this.formHM.clear();
          this.formHM.put("statics", items);
	}

}
