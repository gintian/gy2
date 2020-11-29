package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class TrainAppealTrans extends IBusiness {

    public void execute() throws GeneralException {
        String msg = "true";
        String[] cid = null;
        String ids = (String) this.getFormHM().get("ids");
        if (ids != null & ids.length() > 0)
            cid = ids.split(",");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            if (!(cid == null || cid.length == 0)) {
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

                StringBuffer sql_whl = new StringBuffer("");
                for (int i = 0; i < cid.length; i++) {
                    RecordVo vo = new RecordVo("r31");
                    vo.setString("r3101", cid[i]);
                    vo = dao.findByPrimaryKey(vo);
                    String r3130 = vo.getString("r3130");
                    r3130 = r3130.replaceAll("%26lt;", "<").replaceAll("%26gt;", ">").replaceAll("%2526lt;", "<").replaceAll("%2526gt;", ">");
                    if ("03".equals(vo.getString("r3127"))) {
                        exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.app.approval.approved") + "!");
                        continue;
                    } else if ("07".equals(vo.getString("r3127"))) {
                        sql_whl.append(",'" + vo.getString("r3101") + "'");
                        continue;
                    } else if ("02".equals(vo.getString("r3127"))) {
                        exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.app.approval.approval") + "!");
                        continue;
                    } else if ("04".equals(vo.getString("r3127"))) {
                        exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.app.approval.published") + "!");
                        continue;
                    } else if ("01".equals(vo.getString("r3127"))) {
                        sql_whl.append(",'" + vo.getString("r3101") + "'");
                        continue;
                    } else if ("06".equals(vo.getString("r3127"))) {
                        exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.app.approval.end") + "!");
                        continue;
                    } else if ("08".equals(vo.getString("r3127"))) {
                        sql_whl.append(",'" + vo.getString("r3101") + "'");
                        continue;
                    } else if ("09".equals(vo.getString("r3127"))) {
                        exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.app.approval.suspended") + "!");
                        continue;
                    }
                }
                if (exper.length() > 1)
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.job.fail") + "\n" + exper.toString()));

                if (sql_whl.length() > 1) {
                    dao.update("update r31 set r3127='02'   where (r3127='08' or r3127='01' or r3127='07' )  and r3101 in (" + sql_whl.substring(1) + ") " + where);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        this.getFormHM().put("msg", msg);
    }

}
