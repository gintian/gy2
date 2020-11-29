package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 计划制定
 * 
 * @author Owner
 * 
 */
public class DelTrainPlanTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        String name = (String) hm.get("plan_set_table");
//        ArrayList list = (ArrayList) hm.get("plan_set_record");
        //linbz 22859 20170505 现改为ajax提交方式，参数ids=classid+":"+name+":"+spflag+",";
        String ids = (String) hm.get("ids");//classid+":"+name+":"+spflag+",";
        ContentDAO dao = null;
        try {
            if (StringUtils.isNotEmpty(ids)) {
                String where = "";
                if (!this.userView.isSuper_admin()) {
                    where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
                }

                String[] infos = null;
                String cid = "";
                ArrayList infolist = new ArrayList();
                //ids=classid+":"+name+":"+spflag+",";
                if (StringUtils.isNotEmpty(ids) && ids.indexOf(":") != -1) {
        			infos = ids.split(",");
        			LazyDynaBean ldb = new LazyDynaBean();
        			for (int i = 0; i < infos.length; i++) {
        				String[] cids = infos[i].split(":");
        				cid += cids[0] + ",";
        				ldb = new LazyDynaBean();
        				ldb.set("r3101", cids[0].toString());
                    	ldb.set("r3130", cids[1].toString());
                    	ldb.set("r3127", cids[2].toString());
                    	infolist.add(ldb);
        				
        			}
        		}
                
                StringBuffer exper = new StringBuffer("");
                if (!this.userView.isSuper_admin())
                    exper.append(TrainClassBo.checkclass(cid, this.frameconn, where));
                
                if (exper.length() > 1) {
                    String mes = exper.toString() + ResourceFactory.getProperty("train.job.class.nopiv");
                    throw GeneralExceptionHandler.Handle(new Exception(mes));
                }
                
                StringBuffer sql_whl = new StringBuffer("");
                LazyDynaBean ldb = new LazyDynaBean();
                for (int i = 0; i < infolist.size(); i++) {
	                  ldb = (LazyDynaBean) infolist.get(i);
	                  String r3130 = (String) ldb.get("r3130");
	                  r3130 = r3130.replaceAll("%26lt;", "<").replaceAll("%26gt;", ">").replaceAll("%2526lt;", "<").replaceAll("%2526gt;", ">");
	                  String r3127= (String) ldb.get("r3127");
	                  String r3101= (String) ldb.get("r3101");
	                  if ("02".equals(r3127)) {
	                      exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.delete.submit.approvalr") + "!");
	                  } else if ("07".equals(r3127)) {
	                      sql_whl.append(",'" + r3101 + "'");
	                  } else if ("03".equals(r3127)) {
	                      exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.delete.approved") + "!");
	                  } else if ("04".equals(r3127)) {
	                      exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.delete.published") + "!");
	                  } else if ("01".equals(r3127)) {
	                      sql_whl.append(",'" + r3101 + "'");
	                  } else if ("06".equals(r3127)) {
	                      exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.delete.end") + "!");
	                  } else if ("08".equals(r3127)) {
	                      sql_whl.append(",'" + r3101 + "'");
	                  } else if ("09".equals(r3127)) {
	                      exper.append("\n\n[" + r3130 + "]" + ResourceFactory.getProperty("train.b_plan.delete.stop") + "!");
	                  }
                }
                if (exper.length() > 1){
                	String msg = ResourceFactory.getProperty("train.job.fail") + exper.toString();
                    throw GeneralExceptionHandler.Handle(new Exception(msg));
                }
                if (sql_whl.length() > 1) {
                    dao = new ContentDAO(this.getFrameconn());
                    StringBuffer sqlstr = new StringBuffer();
                    sqlstr.append("delete from " + name);
                    sqlstr.append(" where ( r3127='01' or r3127='08' or r3127='07')");
                    sqlstr.append(" and r3101 in (" + sql_whl.toString().trim().substring(1) + ")");

                    if (!this.userView.isSuper_admin()) {
                        sqlstr.append(where);
                    }
                    dao.delete(sqlstr.toString(), new ArrayList());
                }
                this.getFormHM().put("msg", "0");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

    }

}
