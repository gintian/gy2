package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveTrainPlan0Trans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        ArrayList list = (ArrayList) hm.get("plan_set_record");
        ContentDAO dao = null;
        try {
            dao = new ContentDAO(this.getFrameconn());
            
            if (!(list == null || list.size() == 0)) {
                String where = "";
                if (!this.userView.isSuper_admin()) {
                    where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
                }

                String cid = "";
                for (int i = 0; i < list.size(); i++) {
                    RecordVo vo = (RecordVo) list.get(i);
                    String r3101 = vo.getString("r3101");
                    cid += r3101;
                }
                
                if (!this.userView.isSuper_admin()) {
                    StringBuffer exper = new StringBuffer("");
                    exper.append(TrainClassBo.checkclass(cid, this.frameconn, where));
                    if (exper.length() > 1) {
                        String mes = exper.toString() + ResourceFactory.getProperty("train.job.class.nopiv");
                        throw GeneralExceptionHandler.Handle(new Exception(mes));
                    }
                }
                
                for (int i = 0; i < list.size(); i++) {

                    RecordVo vo = (RecordVo) list.get(i);
                    String r3130 = vo.getString("r3130");
                    r3130 = r3130.replaceAll("%26lt;", "<").replaceAll("%26gt;", ">").replaceAll("%2526lt;", "<").replaceAll("%2526gt;", ">");
                    vo.setString("r3130", r3130);
                    if ("01".equals(vo.getString("r3127")) || "07".equals(vo.getString("r3127"))) {
                        RecordVo old_vo = new RecordVo("R31");
                        old_vo.setString("r3101", vo.getString("r3101"));
                        old_vo = dao.findByPrimaryKey(old_vo);
                        vo.setString("r3117", old_vo.getString("r3117"));
                        //检测必填项
                        String flag = TrainClassBo.checkIsFillable(vo);
                        if(flag != null && flag.length() > 0)
                            throw new GeneralException("",flag,"","");
                        
                        String r3113 = vo.getString("r3113");
                        String r3114 = vo.getString("r3114");
                        String r3115 = vo.getString("r3115");
                        String r3116 = vo.getString("r3116");
                        String msg = TrainClassBo.checkClassDate(r3130, r3113, r3114, r3115, r3116);
                        if(msg != null && msg.length() > 0 && !"true".equalsIgnoreCase(msg))
                            throw new GeneralException("",msg,"","");
                        
                        dao.updateValueObject(vo);
                    } else {
                        if ("03".equals(vo.getString("r3127"))) {
                            continue;
                        } else if ("04".equals(vo.getString("r3127"))) {
                            continue;
                        } else if ("02".equals(vo.getString("r3127"))) {
                            continue;
                        } else if ("06".equals(vo.getString("r3127"))) {
                            continue;
                        } else if ("08".equals(vo.getString("r3127"))) {
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);

        }

    }

}
