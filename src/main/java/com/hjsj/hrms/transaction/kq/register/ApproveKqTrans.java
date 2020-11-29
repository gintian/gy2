package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class ApproveKqTrans extends IBusiness {
    /**
     * 考勤审批
     */
    public void execute() throws GeneralException {
        try {
            ArrayList kqDate_list = new ArrayList();
            String kq_duration = RegisterDate.getKqDuration(this.frameconn);
            String select_pre = (String) this.getFormHM().get("select_pre");
            // 执行操作： check: 检查是否有需要批准的数据； 其它值：批准
            String action = (String) this.getFormHM().get("action");

            //页面中当前所选组织机构条件
            String code = (String) this.getFormHM().get("code");
            String kind = (String) this.getFormHM().get("kind");
            String codeWhr = "1=1";
            if (code != null && !"".equals(code.trim()) && kind != null && !"".equals(kind.trim())) {
                if ("1".equals(kind))
                    codeWhr = "E0122 LIKE '" + code + "%'";
                else if ("0".equals(kind))
                    codeWhr = "E01A1 LIKE '" + code + "%'";
                else
                    codeWhr = "B0110 LIKE '" + code + "%'";
            }

            String overrule = (String) this.getFormHM().get("overrule");

            ArrayList kq_dbase_list = new ArrayList();

            if (select_pre != null && !"all".equalsIgnoreCase(select_pre))
                kq_dbase_list.add(select_pre);
            else
                kq_dbase_list = userView.getPrivDbList();

            if (kq_duration != null && kq_duration.length() > 0) {
                kqDate_list = RegisterDate.getKqDate(this.getFrameconn(), kq_duration);
            } else {
                kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
                kqDate_list = RegisterDate.getKqDayList(this.getFrameconn());
            }

            String start_date = kqDate_list.get(0).toString();
            String end_date = kqDate_list.get(1).toString();
            boolean isCorrect = false;
            // 考勤部门
            String field = KqParam.getInstance().getKqDepartment();
            //考勤管理范围机构编码
            String kqDeptCode = RegisterInitInfoData.getKqPrivCodeValue(userView);

            boolean needApprove = false;
            for (int i = 0; i < kq_dbase_list.size(); i++) {
                String nbase = kq_dbase_list.get(i).toString();
                String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
                
                if (!whereIN.toUpperCase().contains("WHERE"))
                    whereIN = whereIN + " WHERE ";
                else
                    whereIN = whereIN + " AND ";
                whereIN = whereIN + codeWhr;
                
                if (!userView.isSuper_admin()) {
                	if (whereIN.indexOf("WHERE") != -1) {
  	                  whereIN = whereIN.replace("WHERE", "WHERE (");
  	                  if (field != null && field.length() > 0 && !"".equals(kqDeptCode))
  	                      whereIN += " OR " + nbase + "A01." + field + " like '"
  	                              + kqDeptCode + "%'";
  	                  whereIN += ")";
                	}

                    if ("check".equalsIgnoreCase(action)) {
                        if (!needApprove)
                            needApprove = haveCanApproveData(whereIN, nbase, kq_duration);
                    } else {
                        String whereB0110 = RegisterInitInfoData.selcet_OrgId(nbase, "b0110", whereIN);
                        ArrayList orgidb0110List = OrgRegister.getQrgE0122List(this.getFrameconn(), whereB0110, "b0110");
                        try {
                            ContentDAO dao = new ContentDAO(this.getFrameconn());
                            for (int s = 0; s < orgidb0110List.size(); s++) {
                                String b0110_one = orgidb0110List.get(s).toString();
                                updateSumSql(whereIN, nbase, b0110_one, kq_duration, overrule);

                                String destTab = "q03";// 目标表
                                String srcTab = "q05";// 源表
                                String strJoin = "q03.A0100=" + srcTab + ".A0100 and q03.nbase=" + srcTab + ".nbase";// 关联串
                                // xxx.field_name=yyyy.field_namex,....
                                String strSet = "q03.q03z5='03'";// 更新串
                                // xxx.field_name=yyyy.field_namex,....
                                String strDWhere = "q03.nbase='" + nbase + "' and q03.a0100 in(select a0100 " + whereIN
                                        + ") and q03.Q03Z5='02'";// 更新目标的表过滤条件
                                strDWhere = strDWhere + " and q03.Q03Z0 >='" + start_date + "' and q03.Q03Z0 <='" + end_date
                                        + "' and q03.b0110='" + b0110_one + "'";
                                String strSWhere = srcTab + ".a0100 in(select a0100 " + whereIN + ") and q05.nbase='" + nbase
                                        + "' and q05.q03z0='" + kq_duration + "' and q05.q03z5='03'";// 源表的过滤条件
                                String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,
                                        strSWhere);
                                String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ")";
                                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
                                dao.update(update);
                            }
                            isCorrect = true;
                        } catch (Exception e) {
                            isCorrect = false;
                            e.printStackTrace();
                        }
                    }
                } else {
                    if ("check".equalsIgnoreCase(action)) {
                        if (!needApprove)
                            needApprove = haveCanApproveData(whereIN, nbase, kq_duration);
                    } else {
                        try {
                            ArrayList b0100list = RegisterInitInfoData.getAllBaseOrgid(nbase, "b0110", whereIN, this.getFrameconn());
                            ContentDAO dao = new ContentDAO(this.getFrameconn());
                            for (int t = 0; t < b0100list.size(); t++) {
                                String b0110_noe = b0100list.get(t).toString();
                                updateSumSql(whereIN, nbase, b0110_noe, kq_duration, overrule);

                                String destTab = "q03";// 目标表
                                String srcTab = "q05";// 源表
                                String strJoin = "q03.A0100=" + srcTab + ".A0100 and q03.nbase=" + srcTab + ".nbase";// 关联串
                                // xxx.field_name=yyyy.field_namex,....
                                String strSet = "q03.q03z5='03'";// 更新串
                                // xxx.field_name=yyyy.field_namex,....
                                String strDWhere = "q03.nbase='" + nbase + "' and q03.a0100 in(select a0100 " + whereIN
                                        + ") and q03.Q03Z5='02'";// 更新目标的表过滤条件
                                strDWhere = strDWhere + " and q03.Q03Z0 >='" + start_date + "' and q03.Q03Z0 <='" + end_date
                                        + "' and q03.b0110='" + b0110_noe + "'";
                                String strSWhere = srcTab + ".a0100 in(select a0100 " + whereIN + ") and q05.nbase='" + nbase
                                        + "' and q05.q03z0='" + kq_duration + "' and q05.q03z5='03'";// 源表的过滤条件
                                String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,
                                        strSWhere);
                                String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ")";
                                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
                                dao.update(update);

                            }
                            isCorrect = true;
                        } catch (Exception e) {
                            isCorrect = false;
                            e.printStackTrace();
                        }
                    }
                }
            }

            if ("check".equalsIgnoreCase(action)) {
                this.getFormHM().put("check_result", needApprove);
            } else {
                if (isCorrect) {
                    this.getFormHM().put("sp_result", "数据批准成功！");
                } else {
                    this.getFormHM().put("sp_result", "数据批准失败！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private boolean updateSumSql(String whereIN, String dbase, String code, String kq_duration, String overrule)
            throws GeneralException {
        boolean isCorrect = false;
        RegisterInitInfoData registerInitInfoData = new RegisterInitInfoData();
        overrule = registerInitInfoData.getOverruleFormat(overrule, "03", this.userView.getUserFullName());
		
        StringBuffer sql = new StringBuffer();
        sql.append("update Q05");
        sql.append(" set Q03Z5='03',");
        sql.append("overrule='" + overrule + "'" + Sql_switcher.concat() + Sql_switcher.sqlToChar(Sql_switcher.isnull("overrule", "''")));
        sql.append(" where nbase='" + dbase + "' ");
        sql.append(" and b0110 = '" + code + "'");
        sql.append(" and Q03Z0 ='" + kq_duration + "'");
        sql.append(" and a0100 in (select a0100 " + whereIN + ")");
        sql.append(" and Q03Z5 ='02'");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
        	
            dao.update(sql.toString());
            isCorrect = true;
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return isCorrect;
    }

    private boolean haveCanApproveData(String whereIN, String dbase, String kq_duration) {
        boolean have = false;

        StringBuilder sql = new StringBuilder();
        sql.append("select 1 from Q05");
        sql.append(" where nbase=?");
        sql.append(" and Q03Z0 =?");
        sql.append(" and a0100 in (select a0100 ").append(whereIN).append(")");
        sql.append(" and Q03Z5 ='02'");

        ArrayList<String> sqlParams = new ArrayList<String> ();
        sqlParams.add(dbase);
        sqlParams.add(kq_duration);

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql.toString(), sqlParams);
            have = this.frowset.next();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return have;
    }
}
