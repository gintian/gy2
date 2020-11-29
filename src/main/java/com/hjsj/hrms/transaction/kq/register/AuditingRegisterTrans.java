package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class AuditingRegisterTrans extends IBusiness {
    /***************************************************************************
     * 考勤员提交考勤数据审核
     **************************************************************************/
    private String error_return = "/kq/register/select_collectdata.do?b_search=link";
    private String error_flag   = "2";

    public void execute() throws GeneralException {
        try {
            ArrayList datelist = (ArrayList) this.getFormHM().get("datelist");
            CommonData vo_date = (CommonData) datelist.get(0);
            String start_date = vo_date.getDataValue();
            vo_date = (CommonData) datelist.get(datelist.size() - 1);
            String end_date = vo_date.getDataValue();
            String code = (String) this.getFormHM().get("code");
            String kind = (String) this.getFormHM().get("kind");
            String kq_duration = RegisterDate.getKqDuration(this.frameconn);
            String overrule = (String) this.getFormHM().get("overrule");

            code = code.trim();

            String validate = "false";
            String select_pre = (String) this.getFormHM().get("select_pre");
            ArrayList kq_dbase_list = new ArrayList();
            if (select_pre != null && !"all".equalsIgnoreCase(select_pre))
                kq_dbase_list.add(select_pre);
            else
                kq_dbase_list = userView.getPrivDbList();

            // 考勤部门
            String field = KqParam.getInstance().getKqDepartment();
            //考勤管理范围机构编码
            String kqDeptCode = RegisterInitInfoData.getKqPrivCodeValue(userView);
            boolean isCorrect = false;
            for (int r = 0; r < kq_dbase_list.size(); r++) {
                String dbase = kq_dbase_list.get(r).toString();
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, dbase);
                if (whereIN.indexOf("WHERE") != -1) {
	                  whereIN = whereIN.replace("WHERE", "WHERE (");
	                  if (field != null && field.length() > 0 && !"".equals(kqDeptCode))
	                      whereIN += " OR " + dbase + "A01." + field + " like '"
	                              + kqDeptCode + "%'";
	                  whereIN += ")";
                }
                isCorrect = updateSumSql(whereIN, kind, dbase, code, kq_duration, overrule);
                if (!isCorrect)
                    break;
                isCorrect = updateSql(whereIN, kind, dbase, code, start_date, end_date, kq_duration);
                if (!isCorrect)
                    break;
            }
            validate = "ture";
            if (isCorrect) {
                this.getFormHM().put("sp_result", "数据报审成功！");
            } else {
                this.getFormHM().put("sp_result", "数据报审失败！");
            }
            this.getFormHM().put("validate", validate);
            this.getFormHM().put("error_flag", "0");
            this.getFormHM().put("re_url", this.error_return);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /***************************************************************************
     * @param whereIN
     *            select in子句
     * @param tablename
     *            表名
     * @return 返回？号的update的SQL语句
     * 
     **************************************************************************/
    private boolean updateSql(String whereIN, String kind, String dbase, String code, String start_date, String end_date,
            String kq_duration) throws GeneralException {
        boolean isCorrect = false;
        String codewhere = "";
        if ("1".equals(kind)) {
            codewhere = " and q03.e0122 like '" + code + "%' ";
        } else if ("0".equals(kind)) {
            codewhere = " and q03.e01a1 like '" + code + "%' ";
        } else {
            codewhere = " and q03.b0110 like '" + code + "%' ";
        }

        String destTab = "q03";// 目标表
        String srcTab = "q05";// 源表
        String strJoin = "q03.A0100=" + srcTab + ".A0100 and q03.nbase=" + srcTab + ".nbase";// 关联串 xxx.field_name=yyyy.field_namex,....
        String strSet = "q03.q03z5='08'";// 更新串
        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "q03.nbase='" + dbase + "' and q03.a0100 in(select a0100 " + whereIN
                + ") and q03.Q03Z5 in ('01','07','08')";// 更新目标的表过滤条件
        strDWhere = strDWhere + " and q03.Q03Z0 >='" + start_date + "' and q03.Q03Z0 <='" + end_date + "' " + codewhere;
        String strSWhere = srcTab + ".a0100 in(select a0100 " + whereIN + ") and q05.nbase='" + dbase + "' and q05.q03z0='"
                + kq_duration + "' and q05.q03z5='08'";// 源表的过滤条件
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ")";
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.update(update);
            isCorrect = true;
        } catch (Exception e) {
            e.printStackTrace();
            String error_message = ResourceFactory.getProperty("kq.register.refer.lost");
            this.getFormHM().put("error_message", error_message);
            this.getFormHM().put("error_return", this.error_return);
            this.getFormHM().put("error_flag", this.error_flag);
            return false;
        }
        return isCorrect;
    }

    /***************************************************************************
     * @param whereIN
     *            select in子句
     * @param tablename
     *            表名
     * @return 返回？号的update的SQL语句
     * 
     **************************************************************************/
    private boolean updateSumSql(String whereIN, String kind, String dbase, String code, String kq_duration, String overrule)
            throws GeneralException {
        boolean isCorrect = false;
        RegisterInitInfoData registerInitInfoData = new RegisterInitInfoData();
        overrule = registerInitInfoData.getOverruleFormat(overrule, "08", this.userView.getUserFullName());

        StringBuffer sql = new StringBuffer();
        sql.append("update Q05");
        sql.append(" set Q03Z5='08',");
        sql.append("overrule='" + overrule + "'" + Sql_switcher.concat() + Sql_switcher.sqlToChar(Sql_switcher.isnull("overrule", "''")));
        sql.append(" where nbase='" + dbase + "' ");
        if ("1".equals(kind)) {
            sql.append(" and e0122 like '" + code + "%'");
        } else if ("0".equals(kind)) {
            sql.append(" and e01a1 like '" + code + "%'");
        } else {
            sql.append(" and b0110 like '" + code + "%'");
        }
        sql.append(" and Q03Z0 ='" + kq_duration + "'");
        sql.append(" and a0100 in (select a0100 " + whereIN + ")");
        sql.append(" and Q03Z5 in ('01','07')");

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.update(sql.toString());
            isCorrect = true;
        } catch (SQLException e) {
            e.printStackTrace();
            String error_message = ResourceFactory.getProperty("kq.register.refer.lost");
            this.getFormHM().put("error_message", error_message);
            this.getFormHM().put("error_return", this.error_return);
            this.getFormHM().put("error_flag", this.error_flag);
            return false;
        }
        return isCorrect;
    }
}
