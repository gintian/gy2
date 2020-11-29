package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.KqEmpMonthDataBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 检查当前期间申请单据的状态是否全部审批通过了
 *<p>Title:CheckOperationApplyStateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 25, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class CheckOperationApplyStateTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            ArrayList datelist = RegisterDate.getKqDayList(this.getFrameconn());
            String flag = (String) this.getFormHM().get("flag");
            this.getFormHM().put("flag", flag);
            String select_pre = (String) this.getFormHM().get("select_pre");
            ArrayList kq_dbase_list = new ArrayList();
            if (select_pre != null && !"all".equalsIgnoreCase(select_pre))
                kq_dbase_list.add(select_pre);
            else
                kq_dbase_list = userView.getPrivDbList();
            String z1 = datelist.get(0).toString();
            String z3 = datelist.get(datelist.size() - 1).toString();
            z1 = z1 + " 00:00:00";
            z3 = z3 + " 23:59:59";
            String code = (String) this.getFormHM().get("code");
            String kind = (String) this.getFormHM().get("kind");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String mess = "";
            try {
                String status = KqParam.getInstance().getQ05ControlStatus();
                if (null == status || "".equals(status)) {
                    status = "0";
                }
                this.getFormHM().put("status", status);
                if ("1".equals(status)) {//员工月汇总设置数据审核控制
                    KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
                    ArrayList dblist = kqUtilsClass.setKqPerList(code, kind);
                    KqEmpMonthDataBo bo = new KqEmpMonthDataBo(frameconn, userView);
                    String ids = bo.getFormula();
                    String msg = "";//0：没有指定审核公式	no：审核通过	  否则 审核不通过 导出审核报告

                    if (ids == null || "".equals(ids)) {
                        msg = "0";
                        this.getFormHM().put("msg", msg);
                        return;
                    }
                    HashMap hm = new HashMap();
                    hm = bo.auditEmpMonthData(ids, code, kind, dblist);
                    String fileName = (String) hm.get("fileName");
                    this.getFormHM().put("fileName", fileName);
                    msg = (String) hm.get("msg");
                    this.getFormHM().put("msg", msg);

                    if ("yes".equals(msg)) {
                        KqParam kqParam = KqParam.getInstance();
                        String content = kqParam.getQ05_control();
                        this.getFormHM().put("content", content);
                    }
                }

                String table = "q11";//加班表
                String sql = getSQL(table, kind, code, z1, z3, kq_dbase_list);
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    mess = "加班申请表还有没有批准的数据,";
                    this.getFormHM().put("mess", mess);
                    return;
                }
                
                table = "q13";//公出
                sql = getSQL(table, kind, code, z1, z3, kq_dbase_list);
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    mess = "公出申请表还有没有批准的数据,";
                    this.getFormHM().put("mess", mess);
                    return;
                }
                
                table = "q15";//请假
                sql = getSQL(table, kind, code, z1, z3, kq_dbase_list);
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    mess = "请假申请表还有没有批准的数据,";
                    this.getFormHM().put("mess", mess);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.getFormHM().put("mess", mess);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private String getSQL(String table, String kind, String code, String z1, String z3, ArrayList nbaselist) {
        String column_z1 = table + "z1";
        String column_z3 = table + "z3";
        StringBuffer selectSQL = new StringBuffer();
        for (int i = 0; i < nbaselist.size(); i++) {
            String nbase = nbaselist.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            selectSQL.append("select * from " + table + " where 1=1");
            if ("1".equals(kind)) {
                selectSQL.append(" and e0122 like '" + code + "%' ");
            } else {
                selectSQL.append(" and b0110 like '" + code + "%' ");
            }
            selectSQL.append(" and upper(nbase)='" + nbase.toUpperCase() + "'");
            selectSQL.append(" and ((" + column_z1 + ">" + Sql_switcher.dateValue(z1));
            selectSQL.append(" and " + column_z1 + "<" + Sql_switcher.dateValue(z3) + ")");
            selectSQL.append(" or (" + column_z3 + ">" + Sql_switcher.dateValue(z1));
            selectSQL.append(" and " + column_z3 + "<" + Sql_switcher.dateValue(z3) + ")");
            selectSQL.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
            selectSQL.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
            selectSQL.append(")");
            selectSQL.append(" and a0100 in(select a0100 " + whereIN + ")");
            selectSQL.append(" and " + table + "Z5 <>'03'");
            selectSQL.append(" union ");
        }
        if (selectSQL.length() > 7)
            selectSQL.setLength(selectSQL.length() - 7);
        return selectSQL.toString();
    }
}
