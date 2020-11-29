package com.hjsj.hrms.transaction.train.trainCosts;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:培训费用</p>
 * <p>Description:显示培训费用</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class TrainCostsMenuTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String r2501 = (String)hm.get("r2501");
		r2501=r2501!=null?r2501:"";
		hm.remove("r2501");
		
		this.getFormHM().put("r2501",r2501);
	}

}
