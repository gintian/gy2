package com.hjsj.hrms.transaction.kq.register.pigeonhole;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.Pigeonhole;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 考勤归档
 * <p>Title:StartPigeonholeTrans.java</p>
 * <p>Description>:StartPigeonholeTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 26, 2011 3:12:27 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class StartPigeonholeTrans extends IBusiness {

    public void execute() throws GeneralException {
        String pigeonhole_flag = "pige_true";
        String data_table_name = "";
        Pigeonhole pigeonhole = new Pigeonhole(this.getFrameconn(), this.userView);
        try {
            String temp_table_oper = (String) this.getFormHM().get("temp_table");
            ArrayList filedlist = (ArrayList) this.getFormHM().get("list");
            String setlist = (String) this.getFormHM().get("setlist");
            
            //zxj 20141111 归档前检查对应指标是否合法
            String validateMsg = pigeonhole.archiveItemsValidate(filedlist, setlist);
            if (!"".equals(validateMsg))
                throw new GeneralException("", "归档指标对应不正确，请检查：" + validateMsg.toString(), "", "");
            
            String tempCloumn = getTempDate(filedlist, temp_table_oper);
            if (tempCloumn == null || tempCloumn.length() <= 0) {
                throw new GeneralException("", ResourceFactory.getProperty("kq.pigeonhole.error.nofield"), "", "");
            }

            UpdateQ33 updateQ33 = new UpdateQ33(this.userView, this.getFrameconn());
            data_table_name = pigeonhole.getTmpTableName(this.userView.getUserId(), "arch");
            String kq_duration = RegisterDate.getKqDuration(this.getFrameconn());

            /*************得到归档的次数和对应的月**************/
            String month_pigeonhole = RegisterDate.getKqMonth(this.getFrameconn(), kq_duration);
            String num_pigeonhole = RegisterDate.getKqNum(this.getFrameconn(), kq_duration);
            //ArrayList kqPrivDbList=getKqAllPrivDbList(kq_duration);
            ArrayList kqPrivDbList = this.userView.getPrivDbList();
            DbWizard dbWizard = new DbWizard(frameconn);

            for (int i = 0; i < kqPrivDbList.size(); i++) {
                String nbase = kqPrivDbList.get(i).toString();

                pigeonhole.insertInitTempData(data_table_name, kq_duration, nbase, tempCloumn, "");
                pigeonhole.insertInitDestData(data_table_name, setlist, kq_duration, 
                        nbase, month_pigeonhole, this.userView.getUserFullName(), num_pigeonhole);
                pigeonhole.updateI9999(nbase, setlist, month_pigeonhole, num_pigeonhole, true);
                pigeonhole.updateDestData(temp_table_oper, data_table_name, nbase, 
                        setlist, month_pigeonhole, this.userView.getUserFullName(), num_pigeonhole);
                if (dbWizard.isExistTable("Q33", false)) {
                    updateQ33.updateQ33(kq_duration, nbase);//更新调休加班明细表
                }
            }
        } catch (Exception e) {
            pigeonhole_flag = "pige_false";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        KqUtilsClass.dropTable(this.frameconn, data_table_name);
        this.getFormHM().put("pigeonhole_flag", pigeonhole_flag);
    }

    /**
     * 修改归档临时表
     * @param filedlist
     * @param temp_table
     */
    private String getTempDate(ArrayList filedlist, String temp_table) {
        DynaBean dbean = null;
        String srcFldId = null;
        String destFldId = null;
        String destFldName = null;
        ArrayList list = new ArrayList();
        StringBuffer update = new StringBuffer();
        update.append("update " + temp_table + " set");
        update.append(" DestFldId=?,DestFldName=?");
        update.append(" where SrcFldId=?");
        StringBuffer tempCloumn = new StringBuffer();
        try {
            tempCloumn.append("a0100");
            for (int i = 0; i < filedlist.size(); i++) {
                ArrayList one_list = new ArrayList();
                dbean = (LazyDynaBean) filedlist.get(i);
                srcFldId = (String) dbean.get("srcfldid");
                destFldId = (String) dbean.get("destfldid");
                destFldName = (String) dbean.get("destfldname");
                if (destFldId != null && destFldId.length() > 0 && destFldName != null && destFldName.length() > 0) {
                    one_list.add(destFldId);
                    one_list.add(destFldName);
                    one_list.add(srcFldId);
                    list.add(one_list);
                    tempCloumn.append("," + srcFldId);
                }
            }
            //dao.batchUpdate(update.toString(),list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempCloumn.toString();
    }
}
