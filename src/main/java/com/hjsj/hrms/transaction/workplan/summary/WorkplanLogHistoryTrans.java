package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

@SuppressWarnings("all")
public class WorkplanLogHistoryTrans extends IBusiness{

	public void execute() throws GeneralException {
		String type = (String) this.formHM.get("type");
		String objectId = WorkPlanUtil.decryption((String) this.formHM.get("objectId"));
		WorkPlanOperationLogBo oLogBo = new WorkPlanOperationLogBo(frameconn, userView);
		ArrayList resultList = oLogBo.queryAllLog(Integer.parseInt(type), Integer.parseInt(objectId));
		this.formHM.put("loglist", resultList);
		this.formHM.put("logbhr", String.valueOf(this.userView.getA0100().length()<1));
	}

}
