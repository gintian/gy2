package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

public class TrainSubTrans extends IBusiness {

    public void execute() throws GeneralException {
        String msg = "true";
        String[] cid = null;
        cat.debug("table name=r31");
        String ids = (String) this.getFormHM().get("ids");
        if (ids != null && ids.length() > 0)
            cid = ids.split(",");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            String where = "";
            if (!this.userView.isSuper_admin()) {
                where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
            }

            StringBuffer exper = new StringBuffer("");
            if (!this.userView.isSuper_admin())
                exper.append(TrainClassBo.checkclass(ids, this.frameconn, where));
            
            if (exper.length() > 1) {
                String mes = ResourceFactory.getProperty("train.job.fail") + "\n" + exper.toString() + ResourceFactory.getProperty("train.job.class.nopiv");
                throw GeneralExceptionHandler.Handle(new Exception(mes));
            }

            if (!(cid == null || cid.length == 0)) {
                StringBuffer sql_whl = new StringBuffer("");
                for (int i = 0; i < cid.length; i++) {
                    String[] cids = cid[i].split(":");
                    RecordVo vo = new RecordVo("r31");
                    vo.setString("r3101", cids[0]);
                    vo = dao.findByPrimaryKey(vo);
                    int state = 0;
                    if ("delete".equalsIgnoreCase(cids[1]))
                        state = 1;
                    else if ("modify".equalsIgnoreCase(cids[1]))
                        state = 2;
                    else if ("insert".equalsIgnoreCase(cids[1]))
                        state = -1;

                    if (state == -1) {
                        vo.setDate("r3118", new Date());
                        if (userView.hasTheFunction("090403") && !userView.hasTheFunction("090404"))
                            vo.setString("r3127", "08");
                        dao.addValueObject(vo);
                    } else {
                        String r3130 = vo.getString("r3130");
                        r3130 = r3130.replaceAll("%26lt;", "<").replaceAll("%26gt;", ">").replaceAll("%2526lt;", "<").replaceAll("%2526gt;", ">");
                        if ("03".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.altrial.approved.error") + "!");
                            continue;
                        } else if ("04".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.altrial.published.error") + "!");
                            continue;
                        } else if ("02".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.altrial.approvalr.error") + "!");
                            continue;
                        } else if ("06".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.altrial.end.error") + "!");
                            continue;
                        } else if ("08".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.altrial.altrial.error") + "!");
                            continue;
                        } else if ("09".equals(vo.getString("r3127"))) {
                            exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.altrial.stop.error") + "!");
                            continue;
                        }
                        sql_whl.append(",'" + vo.getString("r3101") + "'");
                    }
                }

                if (sql_whl.length() > 1)
                    dao.update("update r31 set r3127='08' where ( r3127='01' or r3127='07' )  and r3101 in (" + sql_whl.substring(1) + ") " + where);

                if (exper.length() > 1)
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.job.fail") + "\n" + exper.toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        this.getFormHM().put("msg", msg);
    }

}
