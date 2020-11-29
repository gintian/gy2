package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveTrainPlanTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        ArrayList list = (ArrayList) hm.get("plan_set_record");
        ContentDAO dao = null;
        try {

            StringBuffer exper = new StringBuffer("");
            dao = new ContentDAO(this.getFrameconn());
            if (!(list == null || list.size() == 0)) {
                for (int i = 0; i < list.size(); i++) {
                    RecordVo vo = (RecordVo) list.get(i);
                    if ("02".equals(vo.getString("r3127")) || "03".equals(vo.getString("r3127"))) {
                        RecordVo old_vo = new RecordVo("R31");
                        old_vo.setString("r3101", vo.getString("r3101"));
                        old_vo = dao.findByPrimaryKey(old_vo);
                        vo.setString("r3117", old_vo.getString("r3117"));
                        // 培训预算
                        TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
                        if (tbb.getBudget() != null && tbb.getBudget().length() > 0) {
                            if ("03".equals(vo.getString("r3127")))
                                tbb.updateTrainBudget("2", vo.getString("r3101"), vo.getDouble("r3111"), vo.getString("r3125"));
                        }
                        
                        String flag = TrainClassBo.checkIsFillable(vo);
                        if(flag != null && flag.length() > 0)
                            throw new GeneralException("",flag,"","");
                        
                        String r3113 = vo.getString("r3113");
                        String r3114 = vo.getString("r3114");
                        String r3115 = vo.getString("r3115");
                        String r3116 = vo.getString("r3116");
                        String r3130 = vo.getString("r3130");
                        String msg = TrainClassBo.checkClassDate(r3130, r3113, r3114, r3115, r3116);
                        if(msg != null && msg.length() > 0 && !"true".equalsIgnoreCase(msg))
                            throw new GeneralException("",msg,"","");
                        
                        String numFlag = TrainClassBo.CheckNumber(vo);
                        if(!"true".equalsIgnoreCase(numFlag))
                            throw new GeneralException("",numFlag,"","");
                        
                        dao.updateValueObject(vo);
                    } else {
                        String r3130 = vo.getString("r3130");
                        r3130 = r3130.replaceAll("%26lt;", "<").replaceAll("%26gt;", ">").replaceAll("%2526lt;", "<").replaceAll("%2526gt;", ">");
                        if ("03".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.update.error.approved") + "!");
                            continue;
                        } else if ("04".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.update.error.published") + "!");
                            continue;
                        } else if ("01".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.update.error.drafting") + "!");
                            continue;
                        } else if ("06".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.update.error.end") + "!");
                            continue;
                        } else if ("08".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.update.error.altrial") + "!");
                            continue;
                        }
                    }
                }
                if (exper.length() > 1)
                    throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
