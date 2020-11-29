package com.hjsj.hrms.transaction.train.request;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class TrainMenuTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String r3101 = (String)hm.get("r3101");
		r3101=r3101!=null?r3101:"";
		hm.remove("r3101");
		
		String r3127 = (String)hm.get("r3127");
		r3127=r3127!=null?r3127:"";
		hm.remove("r3127");
		
		this.getFormHM().put("r3101",r3101);
		this.getFormHM().put("r3127",r3127);
	}

}
