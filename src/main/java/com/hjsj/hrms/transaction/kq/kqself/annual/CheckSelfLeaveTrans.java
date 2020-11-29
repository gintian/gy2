package com.hjsj.hrms.transaction.kq.kqself.annual;

import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.FuncVersion;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckSelfLeaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		/**增加调休假,参数设置调休假有效期不为空才显示此页签**/
		DbWizard dbWizard = new DbWizard(frameconn);
		String isshow = "0";//是否显示调休假,1：显示,0：不显示
		
        FuncVersion fv = new FuncVersion(this.userView);
        
        //专业版才有调休假功能
        if (fv.haveKqLeaveTypeUsedOverTimeFunc()) {
            KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo(this.getFrameconn(), this.userView);
            if(kqOverTimeForLeaveBo.validOverTimeForLeaveFunc())
        			isshow = "1";
		}
		this.getFormHM().put("isshow", isshow);
	}
}
