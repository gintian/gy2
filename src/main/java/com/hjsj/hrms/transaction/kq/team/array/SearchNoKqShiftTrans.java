package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SearchNoKqShiftTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String select_flag = (String) this.getFormHM().get("select_flag");
            String select_name = (String) this.getFormHM().get("select_name");
            this.getFormHM().put("select_flag", select_flag);
            this.getFormHM().put("select_name", select_name);

            String start_date = (String) this.getFormHM().get("start_date");
            if (start_date != null && start_date.length() > 0)
                start_date = start_date.replaceAll("\\.", "-");

            String end_date = (String) this.getFormHM().get("end_date");
            if (end_date != null && end_date.length() > 0)
                end_date = end_date.replaceAll("\\.", "-");

            if (!(KqUtilsClass.validateDate(start_date) && KqUtilsClass.validateDate(end_date))) {
                ArrayList datelist = RegisterDate.getKqDayList(this.getFrameconn());
                if (datelist != null && datelist.size() > 0) {
                    start_date = datelist.get(0).toString();
                    end_date = datelist.get(datelist.size() - 1).toString();
                    if (start_date != null && start_date.length() > 0)
                        start_date = start_date.replaceAll("-", "\\.");

                    if (end_date != null && end_date.length() > 0)
                        end_date = end_date.replaceAll("-", "\\.");
                } else {
                    start_date = DateStyle.dateformat(new java.util.Date(), "yyyy.MM.dd");
                    end_date = start_date;
                }
                //当天假单
            }

            if (start_date != null && start_date.length() > 0)
                start_date = start_date.replaceAll("-", "\\.");

            if (end_date != null && end_date.length() > 0)
                end_date = end_date.replaceAll("-", "\\.");

            String a_code = (String) this.getFormHM().get("a_code");
            if (a_code == null || a_code.length() <= 0) {
                a_code = "UN";
            }

            String kind = "2";
            String code = "";
            if (a_code != null && a_code.length() > 0) {
                String codesetid = a_code.substring(0, 2);
                if ("UN".equalsIgnoreCase(codesetid)) {
                    kind = "2";
                } else if ("UM".equalsIgnoreCase(codesetid)) {
                    kind = "1";
                } else if ("@K".equalsIgnoreCase(codesetid)) {
                    kind = "0";
                }

                if (a_code.length() >= 3 || "UN".equals(a_code)) {
                    code = a_code.substring(2);
                } else {
                    code = this.userView.getUserOrgId();
                }
            }

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            
            ArrayList kq_dbase_list = kqUtilsClass.setKqPerList(code, kind);
            this.getFormHM().put("kq_dbase_list", kq_dbase_list);
            this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
            
            String select_pre = (String) this.getFormHM().get("select_pre");
            if (select_pre == null || select_pre.length() <= 0)
                select_pre = "all";

            if (select_pre == null || select_pre.length() <= 0) {
                if (kq_dbase_list != null && kq_dbase_list.size() > 0)
                    select_pre = kq_dbase_list.get(0).toString();
            }

            ArrayList sql_db_list = new ArrayList();
            if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre) && !"0".equals(select_pre)) {
                sql_db_list.add(select_pre);
            } else {
                sql_db_list = kq_dbase_list;
            }

            
            String where_c = kqUtilsClass.getWhere_C("1", "a0101", select_name);
            StringBuffer cond_str = new StringBuffer();
            if ("1".equals(kind)) {
                cond_str.append(" e0122 like '" + code + "%'");
            } else if ("0".equals(kind)) {
                cond_str.append(" e01a1 like '" + code + "%'");
            } else {
                cond_str.append(" b0110 like '" + code + "%'");
            }
            cond_str.append(where_c);

            String kqTypeWhr = kqUtilsClass.getKqTypeWhere(KqConstant.KqType.STOP, true);
            
            KqDBHelper kqDB = new KqDBHelper(this.getFrameconn());
            // 开始时间的字段代码
            String startField = KqParam.getInstance().getKqStartDateField();
            // 开始时间所在子集
            String startSet = kqDB.getTableNameByFieldName(startField);
            boolean startbool = StringUtils.isNotBlank(startSet);

            // 结束时间的字段代码
            String endField = KqParam.getInstance().getKqEndDateField();
            // 结束时间所在子集
            String endSet = kqDB.getTableNameByFieldName(endField);
            boolean endbool = StringUtils.isNotBlank(endSet);
            
            StringBuffer sql = new StringBuffer();
            for (int i = 0; i < sql_db_list.size(); i++) {
                String nbase = sql_db_list.get(i).toString();
                sql.append("select " + i + " as i,'" + nbase + "' as nbase,a0100,a0101,b0110,e0122,e01a1,a0000 ");
                sql.append(" from ");
                sql.append(nbase + "A01 where ");
                sql.append(cond_str.toString());
                sql.append(kqTypeWhr);
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                sql.append(" and a0100 in(select " + nbase + "A01.a0100 " + whereIN + ") ");
                sql.append(" and NOT EXISTS (");
                sql.append("select 1 from " + KqClassArrayConstant.kq_employ_shift_table + " A where A.a0100=" + nbase + "A01.a0100");
                sql.append(" and " + cond_str.toString());
                sql.append(" and nbase='" + nbase + "'");
                //zxj 有记录但没排班（<不排班>)也算未排班
                sql.append(" and A.class_id IS NOT NULL");
                sql.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + ">='" + start_date + "'");
                sql.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + "<='" + end_date + "'");
                sql.append(" and a0100 in(select " + nbase + "A01.a0100 " + whereIN + ")");
                sql.append(")");
                
                // 61561 考勤开始结束时间校验
                if(startbool || endbool) {
                    sql.append(" and "+nbase+"A01.A0100 not in (");
                    if(startbool) {
                        sql.append("(select C.A0100 from " +nbase+startSet+ " C"
                                + " WHERE "+ Sql_switcher.dateToChar("C."+startField, "yyyy.MM.dd")+ ">'"+end_date+"'");
                        if (!"a01".equalsIgnoreCase(startSet))
                            sql.append(" and C.I9999=(select max(i9999) from " + nbase+startSet + " A WHERE C.A0100=A.A0100)");
                        sql.append(")");
                    }
                    if(startbool && endbool)
                        sql.append(" UNION ");
                    if(endbool) {
                        sql.append("(select D.A0100 from " +nbase+endSet+ " D"
                                + " WHERE "+Sql_switcher.dateToChar("D."+endField, "yyyy.MM.dd")+ "<'"+start_date+"'");
                        if (!"a01".equalsIgnoreCase(endSet))
                            sql.append(" and D.I9999=(select max(i9999) from " + nbase+endSet + " A WHERE D.A0100=A.A0100)");
                        sql.append(")");
                    }
                    sql.append(")");
                }
                
                sql.append(" union ");
            }
            sql.setLength(sql.length() - 7);
            this.getFormHM().put("sql", sql.toString());
            this.getFormHM().put("columns", "nbase,a0100,a0101,b0110,e0122,e01a1,a0000");

            /*// 返回时将时间改为默认考勤期间的时间
            ArrayList courselist = RegisterDate.sessionDate(this.frameconn);
            CommonData vo = (CommonData) courselist.get(0);
            String cur_course = vo.getDataValue();
            ArrayList datelist = RegisterDate.getKqDate(this.frameconn, cur_course);
            String cur_course_start = datelist.get(0).toString();
            String cur_course_end = datelist.get(1).toString();*/
            this.getFormHM().put("start_date", start_date);
            this.getFormHM().put("end_date", end_date);
            KqParameter para = new KqParameter();
            this.getFormHM().put("isPost", para.getKq_orgView_post());
            //显示部门层数
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
