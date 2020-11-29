package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckExamPlanParamTrans extends IBusiness {

    public void execute() throws GeneralException {
        String r5400s = this.getFormHM().get("r5400s").toString();
        boolean flag = false;
        String[] r5400 = r5400s.split(",");
        TrainExamPlanBo planBo = new TrainExamPlanBo(this.frameconn);
        for (int i = 0; i < r5400.length; i++) {
            planBo.loadMessageParam(PubFunc.decrypt(SafeCode.decode(r5400[i].trim())));
            // 【9680】微信推送：考试计划，通知提醒选择为“微信通知”后，启动考试计划时缺少一个确认窗口 jingq add 2015.05.27
            if (planBo.getEmailEnable().booleanValue() || planBo.getSmsEnable().booleanValue()
                    || planBo.getWeixinEnable().booleanValue() || planBo.getPendingTaskEnable().booleanValue()) {
                flag = true;
                break;
            }
        }

        this.getFormHM().put("flag", new Boolean(flag));
    }
}
