package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.machine.ReconstructionKqField;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 网上签到 个人网签明细数据
 * @author wangyao
 * @author Owner
 *
 */
public class SigninListTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            cheakOriginality_data();//系统重构

            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String nbase = (String) hm.get("dbsign"); //库前缀
            nbase = PubFunc.decrypt(nbase);
            String a0100 = (String) hm.get("a0100sign"); //人员编号
            a0100 = PubFunc.decrypt(a0100);
            String filg = (String) hm.get("filg");
            String start_date;
            String end_date;

            if ("1".equalsIgnoreCase(filg)) {
                String registerdate = (String) this.getFormHM().get("registerdate"); //时间
                start_date = registerdate.replaceAll("-", "\\."); //开始
                end_date = registerdate.replaceAll("-", "\\."); //结束
            } else {
                start_date = (String) this.getFormHM().get("start_date");
                end_date = (String) this.getFormHM().get("end_date");
                start_date = start_date.replaceAll("-", "\\."); //开始
                end_date = end_date.replaceAll("-", "\\."); //结束
            }

            NetSignIn netSignIn = new NetSignIn(this.userView, this.getFrameconn());
            if (start_date == null || start_date.length() <= 0)
                start_date = netSignIn.getWork_date();
            else
                start_date = start_date.replaceAll("-", "\\.");

            if (end_date == null || end_date.length() <= 0)
                end_date = netSignIn.getWork_date();
            else
                end_date = end_date.replaceAll("-", "\\.");

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
            //a0100 = PubFunc.encrypt(a0100);
            //nbase = PubFunc.encrypt(nbase);
            this.getFormHM().put("dbsign", nbase);
            this.getFormHM().put("a0100sign", a0100);
            this.getFormHM().put("column_self", column.toString());
            this.getFormHM().put("where_self", where_str.toString());
            this.getFormHM().put("order_self", "order by work_date,work_time");

            KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
            boolean isInout_flag = kqCardData.isViewInout_flag();

            this.getFormHM().put("isInout_flag", isInout_flag + "");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void cheakOriginality_data() throws GeneralException {
        ArrayList list = new ArrayList();
        Field temp = null;
        ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.getFrameconn());

        if (!reconstructionKqField.checkFieldSave("kq_originality_data", "inout_flag")) {
            temp = new Field("inout_flag", "出入标志");
            temp.setDatatype(DataType.INT);
            temp.setKeyable(false);
            temp.setVisible(false);
            list.add(temp);
            temp = new Field("oper_cause", "补刷原因");
            temp.setDatatype(DataType.STRING);
            temp.setKeyable(false);
            temp.setVisible(false);
            temp.setLength(50);
            list.add(temp);
            temp = new Field("oper_user", "补刷操作员");
            temp.setDatatype(DataType.STRING);
            temp.setKeyable(false);
            temp.setVisible(false);
            temp.setLength(50);
            list.add(temp);
            temp = new Field("oper_time", "补刷时间");
            temp.setDatatype(DataType.DATETIME);
            temp.setKeyable(false);
            temp.setVisible(false);
            list.add(temp);
            temp = new Field("oper_mach", "机器ip或机器名");
            temp.setDatatype(DataType.STRING);
            temp.setKeyable(false);
            temp.setVisible(false);
            temp.setLength(50);
            list.add(temp);

            if (!reconstructionKqField.ceaterField_originality(list, "kq_originality_data"))
                throw new GeneralException("重构考勤原始刷卡数据表错误");
        }

        list = new ArrayList();

        if (!reconstructionKqField.checkFieldSave("kq_originality_data", "sp_flag")) {
            temp = new Field("sp_flag", "审批标志");
            temp.setDatatype(DataType.STRING);
            temp.setLength(2);
            temp.setKeyable(false);
            temp.setVisible(false);
            list.add(temp);
            
            if (!reconstructionKqField.ceaterField_originality(list, "kq_originality_data"))
                throw new GeneralException("重构考勤原始刷卡数据表错误");

            String upSQL = "update kq_originality_data set sp_flag='03' where sp_flag is null";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            try {
                dao.update(upSQL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
