package com.hjsj.hrms.transaction.performance.workplan.workplanteam;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;


/**
 * BatApproveWorkPlanTrans.java
 * Description: 批量审批或驳回工作纪实
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Sep 7, 2012 1:24:00 PM Jianghe created
 */
public class BatApproveWorkPlanTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
        {
			String msg="error";
			String strids = (String)this.getFormHM().get("strids");
			String spContent = (String)this.getFormHM().get("spContent");
			
			strids = strids.substring(0, strids.length() - 1);
			//System.out.println(strids);
			//System.out.println(optflag);
			WorkPlanViewBo bo = new WorkPlanViewBo(this.getUserView(),this.getFrameconn());
			bo.analyseParameter();
			UserView uv = this.getUserView();
			String sp_level = (String)WorkPlanViewBo.workParametersMap.get("sp_level");
			String sp_relation = (String)WorkPlanViewBo.workParametersMap.get("sp_relation");
			String[] parametersArray = strids.replaceAll("／", "/").split("/");
			for (int i = 0; i < parametersArray.length; i++) {
				String[] strArray = parametersArray[i].split("@");
					String p0100 = strArray[0];
					String log_type = strArray[1];
					String a0100 = strArray[2];
					String nbase = strArray[3];
					if(a0100!=null&&!"".equals(a0100.trim())&&p0100!=null&&!"".equals(p0100.trim())&&nbase!=null&&!"".equals(nbase.trim())&&log_type!=null&&!"".equals(log_type.trim())){
							//批准和报批
						bo.approveWorkPlan(p0100,a0100,nbase,sp_relation,spContent,sp_level);
					}
			}
			msg="ok";
			this.getFormHM().put("msg",SafeCode.encode(msg));
        }
			
        catch(Exception sqle)
        {
            sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);             
        }
	}
	
}
