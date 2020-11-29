package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.machine.SaveTurnOverTimeToOverTimeApp;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TunOverTimeToOverTimeApp extends IBusiness {

    /**
     * 休息日刷卡转加班
     */
    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException {
        try {
            ArrayList list = (ArrayList) this.getFormHM().get("emp_list");

            // 判断 是大集中还是分用户
            String mark = getDataprocessing();
            mark = mark != null && mark.length() > 0 ? mark : "0";

            String table = "";
            if ("1".equalsIgnoreCase(mark)) {
                table = "kq_analyse_cardtoovertime";// 休息日刷卡转加班
            } else {
                table = "kt_" + this.userView.getUserName() + "_co";// 休息日刷卡转加班
            }

            String templateId = "";
            String overtimeSpState = "03";
            templateId = (String) this.getFormHM().get("templateid");
            templateId = null == templateId ? "" : templateId;
            
            overtimeSpState = (String)this.getFormHM().get("spstate");
            overtimeSpState = null == overtimeSpState ? "03" : overtimeSpState;

            String overtimeReason = "休息日转加班";
            if (!"03".equals(overtimeSpState))
                overtimeReason = "";
            
            SaveTurnOverTimeToOverTimeApp sa = new SaveTurnOverTimeToOverTimeApp(this.frameconn, this.userView, table);
            //if (sa.saveToOverTimeApp(list, overtimeReason, templateId, overtimeSpState) != list.size()) {
            	sa.saveToOverTimeApp(list, overtimeReason, templateId, overtimeSpState);
                if (sa.getErr_message().length() > 0)
                    throw new GeneralException(sa.getErr_message());
                else if (sa.getErrorMess().length() > 0) 
					throw new GeneralException(sa.getErrorMess());
            //}     
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 判断是大集中 还是分用户
     * 
     * @return
     */
    private String getDataprocessing() {
        String data = "";
        ContentDAO dao = new ContentDAO(this.frameconn);
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select content,status from kq_parameter where ");
        sql.append("name='DATA_PROCESSING' and b0110='UN'");
        try {
            rowSet = dao.search(sql.toString());
            if (rowSet.next()) {
                data = rowSet.getString("content");
            }
            data = data != null && data.length() > 0 ? data : "0";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rowSet != null)
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return data;
    }
}
