package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * QueryObjectsTrans.java
 * Description: 查询考核对象
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Nov 16, 2012 4:36:20 PM Jianghe created
 */
public class QueryObjectsTrans extends IBusiness{
	public void execute() throws GeneralException {
		try {
			BatchGradeBo bo = new BatchGradeBo(this.getFrameconn());
			String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
			String plan_id = (String) this.getFormHM().get("plan_id");
			String mainBodyID = (String) this.getFormHM().get("mainBodyID");
			String model = (String) this.getFormHM().get("model");
			ArrayList userlist = new ArrayList();
			if(model!=null && "2".equals(model)){
				//单人
				userlist  = bo.getUserList(name,Integer.parseInt(plan_id),mainBodyID,model);
			}else if("3".equals(model)){
				//多人
				String current = (String) this.getFormHM().get("current");
				userlist  = bo.getUserList1(name,Integer.parseInt(plan_id),mainBodyID,current);
			}
			else if("4".equals(model)){
				//多人
				String point = (String) this.getFormHM().get("point");
				DataCollectBo bo1 = new DataCollectBo(this.getFrameconn(),plan_id,point,this.userView);
				userlist  = bo1.getUserList(name);
			}
			
			this.getFormHM().put("namelist", userlist);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}	
}
