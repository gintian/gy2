package com.hjsj.hrms.module.dashboard.display.transaction;

import com.hjsj.hrms.module.dashboard.display.businessobject.PlanService;
import com.hjsj.hrms.module.dashboard.display.businessobject.impl.PlanServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadPlanDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String action = (String)this.getFormHM().get("action");

        PlanService planService = new PlanServiceImpl(this.getFrameconn());
        try {
            if ("loadPlans".equals(action)) {
                String menuid = (String) this.getFormHM().get("menuid");
                ArrayList cc = planService.getPlanListByMenu(this.getUserView(), menuid);
                HashMap plan = (HashMap) cc.get(0);
                String planid = PubFunc.decrypt((String) plan.get("planid"));
                ArrayList widgets = planService.getPlanWidgetList(planid);
                this.getFormHM().put("planList", cc);
                this.getFormHM().put("widgets", widgets);
            } else if ("loadWidgets".equals(action)) {
                String planid = (String) this.getFormHM().get("planid");
                planid = PubFunc.decrypt(planid);
                ArrayList widgets = planService.getPlanWidgetList(planid);
                this.getFormHM().put("widgets", widgets);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
