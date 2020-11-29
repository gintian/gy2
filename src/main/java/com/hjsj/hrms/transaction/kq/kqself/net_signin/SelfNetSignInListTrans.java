package com.hjsj.hrms.transaction.kq.kqself.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 个人网签明细数据
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Aug 2, 2007:12:56:23 AM
 * </p>
 * 
 * @author dengcan
 * @version 4.0
 */
public class SelfNetSignInListTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            String start_date = (String) this.getFormHM().get("start_date");
            String end_date = (String) this.getFormHM().get("end_date");
            NetSignIn netSignIn = new NetSignIn(this.userView, this.getFrameconn());
            if (start_date == null || start_date.length() <= 0)
                start_date = netSignIn.getWork_date();
            else
                start_date = start_date.replaceAll("-", "\\.");
            if (end_date == null || end_date.length() <= 0)
                end_date = netSignIn.getWork_date();
            else
                end_date = end_date.replaceAll("-", "\\.");
            String nbase = this.userView.getDbname();
            String a0100 = this.userView.getA0100();
            StringBuffer column = new StringBuffer();
            column.append("nbase,a0101,a0100,card_no,work_date,work_time,location,inout_flag,sp_flag");
            String sql = "select " + column;
            StringBuffer where_str = new StringBuffer();
            where_str.append("from kq_originality_data ");
            where_str.append(" where a0100='" + a0100 + "'");
            where_str.append(" and nbase='" + nbase + "'");
            where_str.append(" and work_date>='" + start_date + "'");
            where_str.append(" and work_date<='" + end_date + "'");
            this.getFormHM().put("sql_self", sql);
            this.getFormHM().put("start_date", start_date);
            this.getFormHM().put("end_date", end_date);
            this.getFormHM().put("column_self", column.toString());
            this.getFormHM().put("where_self", where_str.toString());
            this.getFormHM().put("order_self", "order by work_date,work_time");
            KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
            boolean isInout_flag = kqCardData.isViewInout_flag();
            this.getFormHM().put("isInout_flag", isInout_flag + "");
            String net_sign_check_ip = KqParam.getInstance().getNetSignCheckIP();
            this.getFormHM().put("net_sign_check_ip", net_sign_check_ip);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
