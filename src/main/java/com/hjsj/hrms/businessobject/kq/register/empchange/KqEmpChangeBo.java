package com.hjsj.hrms.businessobject.kq.register.empchange;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.Employ_Change;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KqEmpChangeBo {
    /**
     * 考勤人员变动情况
     * */
    private int status = 1;
    private String bdstatic = "0";
    private KqDBHelper kqDB = null;
    private Connection conn = null;
    private UserView userView = null;
    
    private String empBaseInfoChangeTab = "";
    
    private KqEmpChangeBo() {
        
    }
    
    public KqEmpChangeBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        kqDB = new KqDBHelper(conn);
    }
    
    public void ExecuteKqEmpChange(String startDate, String endDate) {
        try {
            this.CompareEmpChange(startDate, endDate);
            this.UpdateEmpChange(startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 比对考勤人员变动情况
     * @param startDate
     * @param endDate
     * @throws GeneralException
     */
    public HashMap<String, String> CompareEmpChange(String startDate, String endDate) throws GeneralException {
        HashMap<String, String> compareResult = new HashMap<String, String>();
        compareResult.put("ishaveadd", "");
        compareResult.put("ishavecut", "");
        compareResult.put("ishavechange", "");
        compareResult.put("ishaveexce", "");
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
    
            //原维护变动比对表结构的功能，由转库大师负责处理
    
            // 得到一个考勤期间内所有的日期
            // 该考勤期间的开始结束时间
            String kqstart = startDate;
            String kqend = endDate;
            String change_date = "";
            // 得到前一个考勤期间的最后一天
            String last_day = getBeforKq_LastDay(kqstart);
    
            //change_date="2007.08.06";
            compareResult.put("change_date", PubFunc.getStringDate("yyyy.MM.dd"));

            this.setEmpBaseInfoChangeTab(Employ_Change.ceaterEmpBaseManage(this.userView, this.conn));
            KqParameter kq_paramter = new KqParameter(new HashMap(), this.userView, "", this.conn);
            String kq_type = kq_paramter.getKq_type();
            
            KqUtilsClass kqUtils = new KqUtilsClass(this.conn, this.userView);
            ArrayList nbaseList = kqUtils.getKqPreList();
            for (int i = 0; i < nbaseList.size(); i++) {
                String userbase = nbaseList.get(i).toString();
                // 权限条件
                String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, userbase);
                
                // 清空kq_employ_change表中考勤权限内的员工
                DeleteEmpChange(userbase, whereIN);
                
                // 增加的人员
                SelectAddEmp(kqstart, kqend, userbase, whereIN, last_day, kq_type);
                // 减少的人员                                
                SelectLeaveEmp(kqstart, kqend, userbase, whereIN, last_day, kq_type);
                // 暂停考勤的人员
                kqTempLeave(kqstart, kqend, userbase, whereIN, last_day, kq_type);
                // 特殊情况处理
                kqSpecial(kqstart, kqend, userbase, whereIN);
                // 单位、部门、职位、姓名变动的人员
                changeEmpBaseManage(this.empBaseInfoChangeTab, kqstart, kqend, userbase, whereIN);
            }
    
            String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
            String kind = "";
            String code = "";
            if (a_code == null || a_code.length() <= 0) {
                String privcode = RegisterInitInfoData.getKqPrivCode(userView);
                if ("UN".equalsIgnoreCase(privcode)) {
                    kind = "2";
                } else if ("UM".equalsIgnoreCase(privcode)) {
                    kind = "1";
                } else if ("@K".equalsIgnoreCase(privcode)) {
                    kind = "0";
                }
                code = RegisterInitInfoData.getKqPrivCodeValue(userView);
            } else {
                if (a_code.indexOf("UN") != -1) {
                    kind = "2";
                } else if (a_code.indexOf("UM") != -1) {
                    kind = "1";
                } else if (a_code.indexOf("@K") != -1) {
                    kind = "0";
                }
                code = a_code.substring(2);
            }
            
            String tab_name = "kq_employ_change";
            //高级授权
            String whereString = RegisterInitInfoData.getKqEmpPrivWhr(this.conn, this.userView, tab_name);;
            //查看新增是否有人
            String sql = "select 1 from " + tab_name + " where status=1 and " + whereString;
            rs = dao.search(sql);
            if (rs.next()) {
                compareResult.put("ishaveadd", "1");
            }
            PubFunc.closeDbObj(rs);
            
            //查看删除是否有人
            sql = "select 1 from " + tab_name + " where status=0 and " + whereString;
            rs= dao.search(sql);
            if (rs.next()) {
                compareResult.put("ishavecut", "1");
            }
            PubFunc.closeDbObj(rs);
            
            //机构变动
            sql = "select 1 from " + this.empBaseInfoChangeTab + "";
            rs = dao.search(sql);
            if (rs.next()) {
                compareResult.put("ishavechange", "1");
            }
            PubFunc.closeDbObj(rs);
            
            //异常数据
            sql = "select 1 from " + tab_name + " where status=2 or status=3 or status=4 and " + whereString;;
            rs = dao.search(sql);
            if (rs.next()) {
                compareResult.put("ishaveexce", "1");
            }
            
            //组织机构、姓名变动表
            compareResult.put("TabName", this.empBaseInfoChangeTab);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
        return compareResult;
    }
    
    /**
     * 根据比对结果同步日明细
     * @throws GeneralException
     */
    public void UpdateEmpChange(String startDate, String endDate) throws GeneralException {
        RowSet rs = null;
        RecordVo vo = null;
        ArrayList empList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM kq_employ_change A");
        sql.append(" WHERE ").append(RegisterInitInfoData.getKqEmpPrivWhr(this.conn, this.userView, "A"));
        
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString());
            while(rs.next()) {
                vo = new RecordVo("kq_employ_change");
                vo.setString("nbase", rs.getString("nbase"));
                vo.setString("a0100", rs.getString("a0100"));
                vo.setString("a0101", rs.getString("a0101"));
                vo.setString("b0110", rs.getString("b0110"));
                vo.setString("e0122", rs.getString("e0122"));
                vo.setString("e01a1", rs.getString("e01a1"));
                vo.setInt("status", rs.getInt("status"));
                vo.setDate("change_date", rs.getDate("change_date"));
                vo.setDate("change_end_date", rs.getDate("change_end_date"));
                
                empList.add(vo);
            }
            
            //无数据可处理，直接退出
            if(empList.size() <= 0) {
                return;
            }
            
            //减少人员
            change_leave(empList, startDate, endDate);
            
            //新增人员
            change_Add(empList, startDate, endDate);
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
            ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
            kqUtilsClass.leadingInItemToQ03(kq_dbase_list, startDate, endDate,"Q03","");//加入导入项

            // 处理当月走的情况
            ArrayList list = getLeaveList(startDate, endDate, empList);
            if (list.size() > 0) {
                change_leave(list, startDate, endDate);
            }
            
            //基本信息变动（单位、部门、岗位变动）
            String duration = RegisterDate.getDurationFromDate(startDate, this.conn);
            change_base(empList, startDate, endDate, this.empBaseInfoChangeTab, duration);
            
            //异常数据
            handleUnusual(empList, startDate, endDate);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
    }
    
    /**
     * 清空考勤权限内的所有变动比对结果
     * 
     * */
    private void DeleteEmpChange(String nbase, String whereIN) {
        try {
            //zxj 20150731 原sql条件错误删不掉权限内的变动数据
            StringBuffer sql = new StringBuffer();
            sql.append("delete from kq_employ_change  where nbase=? ");
            sql.append(" and EXISTS( SELECT 1 FROM " + nbase + "A01 A WHERE A.A0100=kq_employ_change.A0100");
            sql.append(" and a0100 in(select a0100 " + whereIN + "))");
            
            ArrayList list = new ArrayList();
            list.add(nbase);
            
            ContentDAO dao = new ContentDAO(this.conn);
            dao.delete(sql.toString(), list);
            
            //补充：删除一些已经不在库里的垃圾数据
            sql.setLength(0);
            sql.append("delete from kq_employ_change");
            sql.append(" where nbase=?");
            sql.append(" and not exists(select 1 from " + nbase + "A01 where a0100=kq_employ_change.a0100)");
            dao.delete(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 对比增加的员工
     * @param start_date，，当前考勤期间开始时间
     * @param end_date结束时间
     * @param b0110_one单位编号
     * @param nbase人员库
     * @param whereIN权限
     * @param last_day上一个勤期间的最后一天
     */
    private void SelectAddEmp(String start_date, String end_date, String nbase, String whereIN, String last_day,
            String kq_type) {

        StringBuffer sql = new StringBuffer();
        StringBuffer buff = new StringBuffer();
        String char_start_date = Sql_switcher.dateValue(start_date);
        String kqType_where = "";
        if (kq_type != null && kq_type.length() > 0) {
            kqType_where = " and " + Sql_switcher.isnull(kq_type, "'04'") + "<>'04'";
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                kqType_where += " and " + Sql_switcher.trim(kq_type) + "<>''";
            }
        }
        // 开始时间的字段代码
        String field = KqParam.getInstance().getKqStartDateField();
        // 开始时间关联的表名
        String tableName = kqDB.getTableNameByFieldName(field);
        
        // 结束时间的字段代码
        String endField = KqParam.getInstance().getKqEndDateField();
        // 结束时间关联的表名
        String endFieldTableName = kqDB.getTableNameByFieldName(endField);

        if ("1".equalsIgnoreCase(this.bdstatic) || ("0".equalsIgnoreCase(this.bdstatic) && field.length() == 0)) {//是否
            if (last_day != null && last_day.length() > 0) {
                sql.append("INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
                sql.append(" SELECT '" + nbase + "'");
                sql.append(" ,A0100,B0110," + Sql_switcher.isnull("E0122", "''") + " as E0122,A0101,"
                        + Sql_switcher.isnull("E01A1", "''") + " as E01A1," + char_start_date + ",1,1");
                sql.append(" FROM " + nbase + "A01 A");
                sql.append(" WHERE NOT EXISTS(");
                sql.append(" SELECT * FROM Q03 K WHERE K.nbase='" + nbase + "' AND A.A0100=K.A0100 ");
                sql.append(" and K.Q03Z0 = '" + last_day + "' and " + Sql_switcher.isnull("K.q03z3", "'03'") + "<>'04' )");
                //sql.append(" and b0110='" + b0110_one + "'");
                sql.append(" and a0100 in(select a0100 " + whereIN + ")");
               // sql.append(" and A.b0110='" + b0110_one + "'");
                sql.append(kqType_where);
                if (!"1".equals(this.bdstatic)) {
                    sql.append("and a0100 not in(");
                    sql.append("select Distinct a0100 from q03 where nbase='" + nbase + "'");
                    //sql.append(" and b0110='" + b0110_one + "'");
                    sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                    sql.append(")");
                }
                sql.append(" and not EXISTS(select 1 from kq_employ_change change where upper(change.nbase)='"
                        + nbase.toUpperCase() + "' and change.a0100= A.a0100 and status=1)");
            } else {
                sql.append("INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
                sql.append(" SELECT DISTINCT '" + nbase + "'");
                sql.append(" ,A0100,B0110," + Sql_switcher.isnull("E0122", "''") + " as E0122,A0101,"
                        + Sql_switcher.isnull("E01A1", "''") + " as E01A1," + char_start_date + ",1,1 FROM " + nbase + "A01 A");
                sql.append(" WHERE  NOT EXISTS(");
                sql.append(" SELECT * FROM Q03 K WHERE K.nbase='" + nbase + "' AND A.A0100=K.A0100 ");
                sql.append(" and K.Q03Z0 >= '" + start_date + "' and K.Q03Z0 <= '" + end_date + "'  and "
                        + Sql_switcher.isnull("K.q03z3", "'03'") + "<>'04')");
                //sql.append(" and b0110='" + b0110_one + "'");
                sql.append(" and a0100 in(select a0100 " + whereIN + ")");
                sql.append(kqType_where);
                sql.append(" and not EXISTS(select 1 from kq_employ_change change where upper(change.nbase)='"
                        + nbase.toUpperCase() + "' and change.a0100= A.a0100 and status=1)");
            }
        } else {//考勤参数中有开始时间设置的
            // 没有上一个考勤期间的最后一天，说明q03表中没有上个考勤期间记录
            String startDateFieldExp = "case when "
                    + Sql_switcher.dateToChar(" A." + field, "yyyy-mm-dd") + " < '" + start_date.replaceAll("\\.", "-")
                    + "' then " + Sql_switcher.dateValue(start_date) + " else A." + field + " end";
            
            sql.append("INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
            sql.append(" SELECT DISTINCT '" + nbase + "'");
            sql.append(" ,B.A0100,B.B0110," + Sql_switcher.isnull("B.E0122", "''") + " as E0122,B.A0101,"
                    + Sql_switcher.isnull("B.E01A1", "''") + " as E01A1," + startDateFieldExp + " as m,1,1");
            sql.append(" FROM " + nbase + tableName + " A left join " + nbase + "a01 B");
            sql.append(" on A.a0100 = B.a0100");
            sql.append(" WHERE  NOT EXISTS(");
            sql.append(" SELECT * FROM Q03 K WHERE K.nbase='" + nbase + "' AND A.A0100=K.A0100 ");
            sql.append(" and K.Q03Z0 >= replace("
                    + Sql_switcher.isnull(Sql_switcher.dateToChar(startDateFieldExp, "yyyy.MM.dd"), "'" + start_date + "'")
                    + ",'-','.')");
            //sql.append(" and K.Q03Z0 >= '" + start_date + "'");
            sql.append(" and K.Q03Z0 <= '" + end_date + "'");
            sql.append(" and " + Sql_switcher.isnull("K.q03z3", "'03'") + "<>'04')");
            sql.append(" and (( " + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + " <= '"
                    + end_date.replaceAll("\\.", "-") + "') or A." + field + " is null)");
            //sql.append(" and B.b0110='" + b0110_one + "'");
            sql.append(" and B.a0100 in(select a0100 " + whereIN + ")");

            sql.append(" and " + Sql_switcher.isnull("B." + kq_type, "'04'") + "<>'04'");
            sql.append(" and not EXISTS(select 1 from kq_employ_change change where upper(change.nbase)='" + nbase.toUpperCase()
                    + "' and change.a0100= A.a0100 and status=1)");
            //如果开始考勤日期在子集中，那么取最后一条记录
            if (!"a01".equals(tableName.toLowerCase())) {
                sql.append(" and A.I9999=(select max(i9999) from " + nbase + tableName + " C WHERE C.A0100=A.A0100)");
            }
            
            //考勤结束时间不能在本期间前
            if (null != endField && !"".equals(endField)) {
                sql.append(" AND NOT EXISTS(SELECT 1 FROM ").append(nbase + endFieldTableName).append(" D");
                sql.append(" WHERE D.A0100=B.A0100");
                sql.append(" AND D.").append(endField).append(" IS NOT NULL");
                sql.append(" AND ").append(Sql_switcher.dateToChar(" D." + endField, "yyyy-mm-dd"));
                sql.append("<='").append(start_date.replaceAll("\\.", "-")).append("'");
                if (!"a01".equals(endFieldTableName.toLowerCase()))  {
                    sql.append(" AND D.I9999=(SELECT MAX(I9999) FROM ").append(nbase + endFieldTableName).append(" E");
                    sql.append(" WHERE E.A0100=D.A0100)");
                }
                sql.append(")");
            }
            
            /**
             * ##处理特殊情况
             * 例如当前考勤期间为2011-01-01 到2011-01-31，并且设置了考勤开始时间。
             * 张三，人员主集（或子集）中设置了考勤考勤开始时间，时间为2011-01-10,但日明细的生成时间为2011-01-20，
             * 以前的程序无法处理，现在将张三比对出来
             * 
             **/
            buff.append("INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
            buff.append(" SELECT DISTINCT '" + nbase + "' nbase");
            buff.append(" ,B.A0100,B.B0110," + Sql_switcher.isnull("B.E0122", "''") + " as E0122,B.A0101,"
                    + Sql_switcher.isnull("B.E01A1", "''") + " as E01A1," + "A." + field
                    + " as change_date,1 status,1 flag FROM " + nbase + tableName + " A left join " + nbase
                    + "a01 B on A.a0100 = B.a0100");
            buff.append(" WHERE not EXISTS(select 1 from kq_employ_change change where upper(change.nbase)='"
                    + nbase.toUpperCase() + "' and change.a0100= A.a0100 )");
            buff.append(" and " + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + ">='" + start_date.replaceAll("\\.", "-")
                    + "' ");
            buff.append(" and " + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + "<='" + end_date.replaceAll("\\.", "-")
                    + "' ");
            buff.append(" and " + Sql_switcher.isnull("B." + kq_type, "'04'") + "<>'04'");
            buff.append(" and exists(select 1 from q03 k where upper(k.nbase)='" + nbase.toUpperCase()
                    + "' and k.a0100 = A.a0100 and replace(k.q03z0,'-','.') < replace("
                    + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + ",'-','.') )");
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            ArrayList list = new ArrayList();
            dao.insert(sql.toString(), list);

            // 处理特殊情况
            if (buff.length() > 0) {
                //                   dao.insert(buff.toString(),list);
            }

            // 更新增加的时间
            if (field != null && field.length() > 0) {
                StringBuffer sqlBuf = new StringBuffer();

                if (Sql_switcher.searchDbServer() == 2) {
                    sqlBuf.append("update kq_employ_change set change_date=(select case when max(" + field + ") is null then "
                            + Sql_switcher.charToDate("'" + start_date + "'") + " else max(" + field + ") end from " + nbase
                            + tableName + " A where kq_employ_change.a0100=A.a0100 and (("
                            + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + " >='" + start_date.replaceAll("\\.", "-")
                            + "' and " + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + " <= '"
                            + end_date.replaceAll("\\.", "-") + "')or A." + field
                            + " is null)) where status='1' and upper(nbase)='" + nbase.toUpperCase() + "'");
                } else {
                    sqlBuf.append("update kq_employ_change set change_date=(select case when max(" + field + ") is null or max("
                            + field + ")='' then '" + start_date + "' else max(" + field + ") end from " + nbase + tableName
                            + " A where kq_employ_change.a0100=A.a0100 and (("
                            + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + " >='" + start_date.replaceAll("\\.", "-")
                            + "' and " + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + " <= '"
                            + end_date.replaceAll("\\.", "-") + "')or A." + field
                            + " is null)) where status='1' and upper(nbase)='" + nbase.toUpperCase() + "'");
                }
                dao.update(sqlBuf.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *得到前一个考勤期间的最后一天 
     * */
    private String getBeforKq_LastDay(String kqstart) throws GeneralException {

        StringBuffer sql = new StringBuffer();
        sql.append("select max(Q03Z0) as l_day from Q03 ");
        sql.append(" where Q03Z0<'" + kqstart + "'");
        String last_day = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                last_day = rs.getString("l_day");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return last_day;
    }

    /**
     * 查看人员库中是否有参加考勤的员工离职,本方法只和当前考勤期间里面的数据和人员库比较
     * @param start_date  当前考勤期间的开始时间
     * @param end_date  结束时间
     * @param b0110_one  单位编号
     * @param nbase  人员库
     * @param whereIN  权限
     * @param last_day  上一个考勤期间 的最后一天
     * @throws GeneralException
     */
    private void SelectLeaveEmp(String start_date, String end_date, String nbase, String whereIN,
            String last_day, String kq_type) throws GeneralException {
        String char_start_date = Sql_switcher.dateValue(start_date);
        StringBuffer sql = new StringBuffer();
        // 结束时间的字段代码
        String field = KqParam.getInstance().getKqEndDateField();
        // 结束时间关联的表名
        String tableName = kqDB.getTableNameByFieldName(field);

        if ("1".equalsIgnoreCase(this.bdstatic) || ("0".equalsIgnoreCase(this.bdstatic) && field.length() == 0)) {//是否
            sql.append(" INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
            sql.append(" SELECT DISTINCT  nbase");
            sql.append(" ,A0100,max(B0110),max(" + Sql_switcher.isnull("E0122", "''") + ") as E0122,max(A0101),max("
                    + Sql_switcher.isnull("E01A1", "''") + ") as E01A1," + char_start_date + ",0,1  FROM Q03 K WHERE");
            sql.append("  K.nbase='" + nbase + "'");
            //sql.append(" and b0110='" + b0110_one + "'");
            /*if(last_day!=null&&last_day.length()>0)
            {
             sql.append(" and K.Q03Z0='"+last_day+"'");
            }else
            {*/
            sql.append(" and K.Q03Z0 >='" + start_date + "'");
            sql.append(" and K.Q03Z0 <='" + end_date + "'");
            //}

            sql.append(" and K.e0122 in(select DISTINCT " + Sql_switcher.isnull("e0122", "'ss'") + " " + whereIN + ")");
            sql.append("  and K.a0100 not in(SELECT a0100 FROM " + nbase + "A01 A");
            sql.append(" WHERE A.e0122 in(select DISTINCT e0122 " + whereIN + ")");
            //sql.append(" and A.b0110='" + b0110_one + "'");
            sql.append("  )");
            if ("1".equals(this.bdstatic)) {
                if (last_day != null && last_day.length() >= 0) {
                    sql.append("and a0100 not in(");
                    sql.append("select DISTINCT " + Sql_switcher.isnull("a0100", "'ss'"));
                    sql.append(" from q03");
                    sql.append(" where nbase='" + nbase + "'");
                    //sql.append(" and b0110='" + b0110_one + "'");
                    sql.append(" and q03z0='" + last_day + "'");
                    sql.append(")");
                }
            }
            sql.append(" group by nbase,a0100");
        } else {
            sql.append("INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
            sql.append(" SELECT DISTINCT '" + nbase + "',B.A0100,B.B0110,");
            sql.append(Sql_switcher.isnull("B.E0122", "''") + " as E0122,B.A0101,");
            sql.append(Sql_switcher.isnull("B.E01A1", "''") + " as E01A1," + "A." + field + ",0,1");
            sql.append(" FROM " + nbase + tableName + " A left join " + nbase + "a01 B");
            sql.append(" on A.a0100 = B.a0100");
            sql.append(" WHERE EXISTS(");
            sql.append(" SELECT 1 FROM Q03 K WHERE K.nbase='" + nbase + "' AND A.A0100=K.A0100 ");
            //本期间内的日明细数据
            sql.append(" and K.Q03Z0<='" + end_date);
            sql.append("' and K.Q03Z0>='" + start_date);
            //日明细中有结束考勤日期后的数据
            sql.append("' and K.Q03Z0>=replace(" + Sql_switcher.dateToChar("A." + field, "yyyy.MM.dd") + ",'-','.')");
            //结束考勤日期在本期间结束前
            sql.append(" and " + Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd") + "<='" + end_date.replaceAll("\\.", "-"));
            //本期间内日明细不是是暂停考勤状态
            sql.append("' and " + Sql_switcher.isnull("K.q03z3", "'03'") + "<>'04')");
            //sql.append( " and B.b0110='" + b0110_one + "'");
            sql.append(" and B.a0100 in(select a0100 " + whereIN + ")");
            sql.append(" and not exists(select 1 from kq_employ_change kqch where kqch.a0100=B.a0100 and kqch.nbase='" + nbase + "' and kqch.status=0)");
            //如果结束考勤日期在子集中，那么取最后一条记录
            if (!"a01".equals(tableName.toLowerCase())) {
                sql.append(" and A.I9999=(select max(i9999) from " + nbase + tableName + " C WHERE C.A0100=A.A0100)");
            }
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            ArrayList list = new ArrayList();
            dao.insert(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 暂停考勤
     * @param start_date
     * @param end_date
     * @param b0110_one
     * @param nbase
     * @param whereIN
     * @param last_day
     * @param kq_type
     */
    private void kqTempLeave(String start_date, String end_date, String nbase, String whereIN, String last_day,
            String kq_type) {
        String kqType_where = "";
        // 开始时间的字段代码
        String field = KqParam.getInstance().getKqEndDateField();
        // 开始时间关联的表名
        String tableName = kqDB.getTableNameByFieldName(field);
        if (kq_type != null && kq_type.length() > 0) {
            kqType_where = " and " + Sql_switcher.isnull(kq_type, "'04'") + "='04'";
        }
        String char_start_date = Sql_switcher.dateValue(start_date);
        ArrayList durationList = RegisterDate.getKqDayList(conn);
        String kqEndDate = (String) durationList.get(1);
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        
        sql.append(" INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
        sql.append(" SELECT DISTINCT  '" + nbase + "'");
        sql.append(" ,A0100,B0110," + Sql_switcher.isnull("E0122", "''") + " as E0122,A0101,"
                + Sql_switcher.isnull("E01A1", "''") + " as E01A1," + char_start_date + ",0,1");
        sql.append(" FROM Q03 K");
        sql.append(" WHERE K.nbase='" + nbase + "'");
        //sql.append(" and b0110='" + b0110_one + "'");
        sql.append(" and K.Q03Z0 >='" + start_date + "'");
        sql.append(" and K.Q03Z0 <='" + end_date + "'");
        sql.append(" and EXISTS(SELECT 1 FROM " + nbase + "A01 A WHERE A.A0100=K.A0100  and K.Q03Z0 >='" + start_date
                + "' and K.Q03Z0 <='" + end_date + "'");
        //sql.append(" and A.b0110='" + b0110_one + "'");
        sql.append(" and A.a0100 in(select a0100 " + whereIN + ")");
        sql.append(kqType_where);
        sql.append(" and not exists(select 1 from kq_employ_change C");
        sql.append(" where C.nbase=K.nbase and C.a0100=K.a0100 and status=0)");
        sql.append("  )");
        //针对暂停考勤的人员，判断考勤期间的日明细里有没有最后一天的那条记录，有的话就是正常的，不考虑
        sql.append(" and K.a0100 in (select a0100 from q03");
        sql.append(" where q03.nbase='"+nbase+"'");
        //sql.append(" and b0110='"+b0110_one+"'");
        sql.append(" and q03z0 = '"+kqEndDate+"')");
        
        try {
            dao.insert(sql.toString(), list);
            sql.setLength(0);
            if (field.length() > 0) {
                sql.append(" INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag)");
                sql.append(" select * from ( SELECT DISTINCT  '" + nbase + "' nbase");
                sql.append(",K.A0100,B0110," + Sql_switcher.isnull("E0122", "''") + " as E0122,A0101,"
                        + Sql_switcher.isnull("E01A1", "''") + " as E01A1," + Sql_switcher.isnull("B." + field, char_start_date)
                        + " change_date,0 status,1 flag");
                sql.append("  FROM Q03 K left join (select a0100,'" + nbase + "' nbase,max(" + field + ") " + field + " from "
                        + nbase + tableName);
                sql.append(" where " + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + ">='" + start_date.replaceAll("\\.", "-") + "'");
                sql.append(" and " + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + " <='" + end_date.replaceAll("\\.", "-") + "'");
                sql.append(" group by a0100) B");
                sql.append(" on K.nbase=B.nbase and K.a0100=B.a0100");
                sql.append(" WHERE exists(select 1 from q03 q");
                sql.append(" where q.nbase=k.nbase and q.a0100=k.a0100");
                sql.append(" and q.q03z0>=replace(" + Sql_switcher.dateToChar("B." + field, "yyyy.mm.dd") + ",'-','.')) ");
                sql.append(" and K.nbase='" + nbase + "'");
                //sql.append(" and b0110='" + b0110_one + "'");
                sql.append(" and K.Q03Z0 >='" + start_date + "'");
                sql.append(" and K.Q03Z0 <='" + end_date + "'");
                sql.append(" and EXISTS(SELECT 1 FROM " + nbase + "A01 A WHERE A.A0100=K.A0100  and K.Q03Z0 >='" + start_date
                        + "' and K.Q03Z0 <='" + end_date + "'");
                //sql.append(" and A.b0110='" + b0110_one + "'");
                sql.append(" and A.a0100 in(select a0100 " + whereIN + ")");
                sql.append(kqType_where);
                sql.append("  )) ss");
                sql.append(" where not exists(select a0100 from kq_employ_change kqch");
                sql.append(" where kqch.a0100=ss.a0100 and kqch.nbase=ss.nbase and kqch.status=0)");
                list = new ArrayList();
                dao.insert(sql.toString(), list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 特殊情况的处理
     * 情况1：
     * 考勤期间：2011.1.1-2011.1.31
     * 某员工入职时间：2011.1.19
     * 生成日明细时间：在填写员工入职时间之前
     * 变动比对：(考勤员经过一次变动比对后，确认后发现入职时间不对，修改完入职时间，无法比对出来)此员工2011.1.1-2011.1.19期间的数据不能变动比对出去。那么日明细中的应出勤会多记录19天，工资中要用到应出勤数据就都错了。
     * （此情况的产生的原因是：入职日期填写的比实际入职日期早）
    
     * 情况2：
     * 考勤期间：2011.1.1-2011.1.31
     * 某员工离职时间：2011.1.22
     * 在系统中变动比对，此员工22号之后的数据会减去，但是变动比对之后，发现此员工的离职日期写错了，应该为2011.1.25，那么再变动比对，这个员工22到25号的数据不能被加进去。
     * （此情况实产生的原因是：离职日期填写的比实际离职日期晚）
    
     * 情况3：
     * 考勤期间：2011.1.1-2011.1.31
     * 员工入职时间为：2011.1.7
     * 填写入职时间时错填为2011.1.17，对比后确认，那么该员工的日明细就缺少7-16号的数据
     * （此情况的产生的原因是：入职日期填写的比实际入职日期晚）

     * @param startDate String 考勤期间开始时间
     * @param endDate String 考勤期间结束时间
     * @param b0110_one String 部门编号
     * @param nbase String 人员库
     * @param whereIN String 管理范围sql
     * @param last_day String
     * @param kq_type
     */
    private void kqSpecial(String startDate, String endDate, String nbase, String whereIN) {

        StringBuffer sql = new StringBuffer();

        // 开始时间的字段代码
        String startField = KqParam.getInstance().getKqStartDateField();
        // 开始时间所在子集
        String startSet = kqDB.getTableNameByFieldName(startField);
        // 开始时间关联的表名
        String startTableName = nbase + startSet;
        
        // 结束时间的字段代码
        String endField = KqParam.getInstance().getKqEndDateField();
        // 结束时间所在子集
        String endSet = kqDB.getTableNameByFieldName(endField);
        // 开始时间关联的表名
        String endTableName = nbase + endSet;

        ContentDAO dao = new ContentDAO(this.conn);

        ArrayList list = new ArrayList();
        try {
            if (startField.trim().length() > 0 && endField.trim().length() > 0) {
                // 冗余数据，入职时间前有数据 2
                sql.append(" INSERT INTO kq_employ_change(nbase,A0100,B0110,E0122,A0101,E01A1,change_end_date,status,flag)");
                sql.append(" SELECT DISTINCT '" + nbase + "' nbase,");
                sql.append(" max(K.A0100),max(K.B0110),max(");
                sql.append(Sql_switcher.isnull("K.E0122", "''"));
                sql.append(") as E0122,max(K.A0101),max(");
                sql.append(Sql_switcher.isnull("K.E01A1", "''"));
                sql.append(") as E01A1,max(");
                String yesterday = Sql_switcher.addDays("A." + startField, "-1");
                sql.append(yesterday);
                sql.append(") as change_end_date,2,1 FROM Q03 K left join " + startTableName + " A");
                sql.append(" on k.a0100=a.a0100");
                sql.append("  WHERE upper(K.nbase)='" + nbase.toUpperCase() + "'");
                if (!"A01".equalsIgnoreCase(startSet)) {
                    sql.append(" and A.i9999=(select Max(i9999) from " + startTableName + " C where C.a0100=A.a0100)");
                }
                //sql.append(" and K.b0110='"+ b0110_one + "'");
                sql.append(" and replace(K.Q03Z0,'.','-') >=replace('");
                sql.append(startDate);
                sql.append("','.','-')");
                sql.append(" and replace(K.Q03Z0,'.','-') <=replace('");
                sql.append(endDate);
                sql.append("','.','-')");
                sql.append(" and exists(select a0100 ");
                sql.append(whereIN);

                if (whereIN.toLowerCase().contains("where")) {
                    sql.append(" and ");
                } else {
                    sql.append(" where ");
                }

                sql.append(" a0100=K.a0100) and replace(K.Q03Z0,'.','-')<replace(");
                sql.append(Sql_switcher.dateToChar("A." + startField, "yyyy-mm-dd"));
                sql.append(",'.','-') group by K.nbase,K.a0100");

                dao.insert(sql.toString(), list);

                sql.delete(0, sql.length());

                // 更新冗余数据，添加开始时间
                sql.append(" update kq_employ_change set change_date = (");
                sql.append(" SELECT min(");
                sql.append(Sql_switcher.charToDate("Q03Z0"));
                sql.append(") change_date ");
                sql.append(" FROM Q03 K WHERE upper(K.nbase)=upper(kq_employ_change.nbase) ");
                sql.append(" and K.a0100=kq_employ_change.a0100");
                sql.append(" and replace(K.Q03Z0,'.','-') >=replace('");
                sql.append(startDate);
                sql.append("','.','-')");
                sql.append(" and replace(K.Q03Z0,'.','-') <=replace('");
                sql.append(endDate);
                sql.append("','.','-')");
                sql.append(" and exists(select a0100 ");
                sql.append(whereIN);

                if (whereIN.toLowerCase().contains("where")) {
                    sql.append(" and ");
                } else {
                    sql.append(" where ");
                }

                sql.append(" a0100=K.a0100) and upper(K.nbase)='");
                sql.append(nbase.toUpperCase());
                sql.append("' and q03z0 is not null");
                sql.append(") where status=2 and upper(nbase) ='");
                sql.append(nbase.toUpperCase() + "'");
                //sql.append(" and b0110='" + b0110_one + "'");

                dao.update(sql.toString());

                sql.delete(0, sql.length());

                // 减少的数据，参加考勤后缺少数据 3
                sql.append(" INSERT INTO kq_employ_change(nbase,A0100,");
                sql.append("B0110,E0122,A0101,E01A1,change_end_date,status,flag)");
                sql.append(" SELECT DISTINCT '" + nbase + "' nbase,K.A0100,A.B0110,A.E0122,A.A0101,A.E01A1,A." + endField + " as change_end_date,3,1");
                sql.append(" FROM Q03 K");
                sql.append(" left join ");
                
                if ("A01".equalsIgnoreCase(endSet)) {
                    sql.append(endTableName);
                } else {
                    sql.append(" (SELECT C." + endField + ",C.A0100,C.i9999,B.B0110,B.E0122,B.E01A1,B.A0101");
                    sql.append(" FROM " + endTableName + " C LEFT JOIN " + nbase + "A01 B");
                    sql.append(" on c.a0100=B.a0100)");
                }
                
                sql.append(" A ");
                
                sql.append(" on k.a0100=a.a0100");
                sql.append("  WHERE upper(K.nbase)='" + nbase.toUpperCase() + "'");
                if (!"A01".equalsIgnoreCase(endSet)) {
                    sql.append(" and A.i9999=(select Max(i9999) from " + endTableName + " C where C.a0100=A.a0100)");
                }
                //sql.append(" and K.b0110='" + b0110_one + "'");
                
                sql.append(" and exists(select a0100 ");
                sql.append(whereIN);

                if (whereIN.toLowerCase().contains("where")) {
                    sql.append(" and ");
                } else {
                    sql.append(" where ");
                }

                sql.append(" a0100=K.a0100)");
                
                yesterday = Sql_switcher.addDays("A." + endField, "-1");
                //考勤结束日期为考勤期间第一天的不算异常
                String secondDate = OperateDate.dateToStr(DateUtils.addDays(OperateDate.strToDate(startDate, "yyyy.MM.dd"), 1), "yyyy.MM.dd");
                
                sql.append(" and not exists(select 1 from q03 Q");
                sql.append(" where Q.nbase=K.nbase and Q.a0100=K.a0100");
                sql.append(" and Q.Q03Z0>='" + startDate +"'");
                sql.append(" and Q.Q03Z0<='" + endDate +"'");
                sql.append(" and replace(Q.Q03Z0,'.','-')=replace(");
                sql.append(Sql_switcher.dateToChar(yesterday, "yyyy-mm-dd"));
                sql.append(",'.','-')) and (");
                sql.append(Sql_switcher.dateToChar("A." + endField, "yyyy-mm-dd"));
                sql.append(" between '" + secondDate.replaceAll("\\.", "-") + "' and '" + endDate.replaceAll("\\.", "-") + "')");

                dao.insert(sql.toString(), list);

                sql.delete(0, sql.length());

                // 更新减少数据的开始时间
                // 开始时间为参数设置的考勤开始日期的指标对应的值，wangmj2014-2-12
                sql.append(" update kq_employ_change set change_date = (");
                sql.append(" SELECT " + startField + " change_date");
                sql.append(" FROM "+startTableName+" K WHERE");
                sql.append(" K.a0100=kq_employ_change.a0100");
                sql.append(") where status=3 and upper(nbase) ='" + nbase.toUpperCase() + "'");
                //sql.append(" and b0110='" + b0110_one + "'");

                dao.update(sql.toString());

                sql.delete(0, sql.length());

                // 新增人员，数据缺少 4
                sql.append(" INSERT INTO kq_employ_change(nbase,A0100,");
                sql.append("B0110,E0122,A0101,E01A1,change_date,change_end_date,status,flag)");
                sql.append(" select o.nbase,o.a0100,b0110,e0122,a0101,e01a1,change_date," + Sql_switcher.charToDate("p.q03z0")
                        + ",status,flag from (SELECT DISTINCT  '");
                sql.append(nbase + "' nbase," + "K.A0100,A.B0110,A.E0122,A.A0101,A.E01A1,A." + startField + " as change_date,4 status,1 flag");
                sql.append(" FROM Q03 K left join ");
                
                if ("A01".equalsIgnoreCase(startSet)) {
                    sql.append(startTableName);
                } else {
                    sql.append(" (SELECT B.A0100,C.A0101,C.B0110,C.E0122,C.E01A1,B." + startField);
                    sql.append(" FROM " + startTableName + " B LEFT JOIN " + nbase + "A01 C");
                    sql.append(" ON B.A0100=C.A0100)");
                }
                
                sql.append(" A ");
                
                sql.append(" on k.a0100=a.a0100");
                sql.append("  WHERE upper(K.nbase)='" + nbase.toUpperCase() + "'");
                if (!"A01".equalsIgnoreCase(startSet)) {
                    sql.append(" and A.i9999=(select Max(i9999) from " + startTableName + " C where C.a0100=A.a0100)");
                }
                //sql.append(" and K.b0110='" + b0110_one + "'");
                sql.append(" and replace(K.Q03Z0,'.','-') >=replace('");
                sql.append(startDate);
                sql.append("','.','-')");
                sql.append(" and replace(K.Q03Z0,'.','-') <=replace('");
                sql.append(endDate);
                sql.append("','.','-')");
                sql.append(" and exists(select a0100 ");
                sql.append(whereIN);

                if (whereIN.toLowerCase().contains("where")) {
                    sql.append(" and ");
                } else {
                    sql.append(" where ");
                }

                sql.append(" a0100=K.a0100) ) o left join (select a0100,nbase,min(q03z0) q03z0 from q03 where replace(Q03Z0,'.','-') between '"
                                + startDate.replaceAll("\\.", "-")
                                + "' and '"
                                + endDate.replaceAll("\\.", "-")
                                + "'  group by a0100,nbase) p on o.a0100=p.a0100 and upper(o.nbase)=upper(p.nbase) where p.Q03Z0 is not null and replace(p.Q03Z0,'.','-')>replace(");
                sql.append(Sql_switcher.dateToChar("o.change_date", "yyyy-mm-dd"));
                sql.append(",'.','-') and replace(" + Sql_switcher.dateToChar("o.change_date", "yyyy-mm-dd")
                        + ",'.','-') between '" + startDate.replaceAll("\\.", "-") + "' and '" + endDate.replaceAll("\\.", "-")
                        + "'");

                dao.update(sql.toString());

                sql.delete(0, sql.length());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 单位，部门、职位、姓名变动
     * @param change_date
     * @param nbase
     * @param whereIN
     */
    private void changeEmpBaseManage(String TabName, String start_date, String end_date, String nbase, String whereIN)
            throws GeneralException {
        StringBuffer sql = new StringBuffer();

        // 开始时间的字段代码
        String field = KqParam.getInstance().getDeptChangeDateField();
        // 开始时间关联的表名
        String tableName = kqDB.getTableNameByFieldName(field);
        String update = "";
        String updateChangedate = "";
        String updateNull = "";
        if ("1".equalsIgnoreCase(this.bdstatic) || ("0".equalsIgnoreCase(this.bdstatic) && field.length() == 0)) {//是否
            sql.append("INSERT INTO " + TabName + "(nbase,A0100,B0110,E0122,E01A1,A0101,flag)");
            sql.append(" SELECT DISTINCT '" + nbase + "' as nbase,A0100,B0110,E0122,E01A1,A0101,1 ");
            sql.append(" FROM " + nbase + "A01 a ");
            sql.append(" WHERE EXISTS(SELECT 1 FROM Q03 b WHERE 1=1");
            //sql.append(" AND b.Q03Z0>='"+ start_date+"'");        
            sql.append(" AND b.Q03Z0='" + end_date + "'");
            sql.append(" AND b.nbase='" + nbase + "'");
            sql.append(" AND a.A0100=b.A0100 ");
            sql.append(" AND (" + Sql_switcher.isnull("a.B0110", "'ss'") + "<>" + Sql_switcher.isnull("b.B0110", "'ss'") + "");
            sql.append(" OR " + Sql_switcher.isnull("a.E0122", "'ss'") + " <>" + Sql_switcher.isnull("b.E0122", "'ss'") + "");
            sql.append(" OR " + Sql_switcher.isnull("a.E01A1", "'ss'") + " <>" + Sql_switcher.isnull("b.E01A1", "'ss'") + "");
            sql.append(" OR " + Sql_switcher.isnull("a.A0101", "'ss'") + " <>" + Sql_switcher.isnull("b.A0101", "'ss'") + "");
            sql.append(") and a0100 in(select a0100 " + whereIN + "))");
            String destTab = TabName;//目标表
            String srcTab = "q03";//源表
            String strJoin = "q03.A0100=" + destTab + ".A0100 and q03.nbase=" + destTab + ".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
            String strSet = destTab + ".OB0110=q03.B0110`" + destTab + ".OE0122=q03.E0122`" + destTab + ".OE01A1=q03.E01A1`"
                    + destTab + ".OA0101=q03.A0101";//更新串  xxx.field_name=yyyy.field_namex,....
            String strDWhere = TabName + ".nbase='" + nbase + "'";//更新目标的表过滤条件
            String strSWhere = "q03.a0100 in(select a0100 " + whereIN + ") and q03.q03z0='" + end_date + "'";//源表的过滤条件           
            update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
        } else {
            sql.append("INSERT INTO " + TabName + "(nbase,A0100,B0110,E0122,E01A1,A0101,flag)");
            sql.append(" SELECT DISTINCT '" + nbase + "' as nbase,A0100,B0110,E0122,E01A1,A0101,1 ");
            sql.append(" FROM " + nbase + "A01 a ");
            sql.append(" WHERE EXISTS(SELECT 1 FROM Q03 b WHERE 1=1");
            //sql.append(" AND b.Q03Z0>='"+ start_date+"'");        
            sql.append(" AND b.Q03Z0='" + end_date + "'");
            sql.append(" AND b.nbase='" + nbase + "'");
            sql.append(" AND a.A0100=b.A0100 ");
            sql.append(" AND ( (" + Sql_switcher.isnull("a.B0110", "'ss'") + "<>" + Sql_switcher.isnull("b.B0110", "'ss'")
                    + " and exists(select a0100 from " + nbase + tableName + " d where d.a0100=b.a0100  and "
                    + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + ">='" + start_date.replaceAll("\\.", "-") + "' and "
                    + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + "<='" + end_date.replaceAll("\\.", "-") + "') ) ");
            sql.append(" OR (" + Sql_switcher.isnull("a.E0122", "'ss'") + " <>" + Sql_switcher.isnull("b.E0122", "'ss'")
                    + " and exists(select a0100 from " + nbase + tableName + " d where d.a0100=b.a0100  and "
                    + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + ">='" + start_date.replaceAll("\\.", "-") + "' and "
                    + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + "<='" + end_date.replaceAll("\\.", "-") + "') ) ");
            sql.append(" OR (" + Sql_switcher.isnull("a.E01A1", "'ss'") + " <>" + Sql_switcher.isnull("b.E01A1", "'ss'")
                    + " and exists(select a0100 from " + nbase + tableName + " d where d.a0100=b.a0100  and "
                    + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + ">='" + start_date.replaceAll("\\.", "-") + "' and "
                    + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + "<='" + end_date.replaceAll("\\.", "-") + "') ) ");
            sql.append(" OR (" + Sql_switcher.isnull("a.A0101", "'ss'") + " <>" + Sql_switcher.isnull("b.A0101", "'ss'") + "");
            if (Sql_switcher.searchDbServer() == 1) {
                sql.append(" and not ((a.A0101 is null and b.A0101='') or (b.A0101 is null and a.A0101=''))");
            }
            sql.append(")");
            //如果设置的指标没有值，部门变动后也显示出来
            sql.append(" or (" + Sql_switcher.isnull("a.B0110", "'ss'") + "<>" + Sql_switcher.isnull("b.B0110", "'ss'"));
            if (Sql_switcher.searchDbServer() == 1) {
                sql.append(" and not ((a.B0110 is null and b.B0110='') or (b.B0110 is null and a.B0110=''))");
            }
            sql.append(")");
            sql.append(" or (" + Sql_switcher.isnull("a.E0122", "'ss'") + " <>" + Sql_switcher.isnull("b.E0122", "'ss'"));
            if (Sql_switcher.searchDbServer() == 1) {
                sql.append(" and not ((a.E0122 is null and b.E0122='') or (b.E0122 is null and a.E0122=''))");
            }
            sql.append(")");
            sql.append(" or (" + Sql_switcher.isnull("a.E01A1", "'ss'") + " <>" + Sql_switcher.isnull("b.E01A1", "'ss'"));
            if (Sql_switcher.searchDbServer() == 1) {
                sql.append(" and not ((a.E01A1 is null and b.E01A1='') or (b.E01A1 is null and a.E01A1=''))");
            }
            sql.append(")");

            sql.append(") and a0100 in(select a0100 " + whereIN + ")");
            sql.append(")");
            String destTab = TabName;//目标表
            String srcTab = "q03";//源表
            String strJoin = "q03.A0100=" + destTab + ".A0100 and q03.nbase=" + destTab + ".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
            String strSet = destTab + ".OB0110=q03.B0110`" + destTab + ".OE0122=q03.E0122`" + destTab + ".OE01A1=q03.E01A1`"
                    + destTab + ".OA0101=q03.A0101";//更新串  xxx.field_name=yyyy.field_namex,....
            String strDWhere = TabName + ".nbase='" + nbase + "'";//更新目标的表过滤条件
            String strSWhere = "q03.a0100 in(select a0100 " + whereIN
                    + ") and q03.q03z0=(select max(q03z0) from q03 c where nbase='" + nbase
                    + "' and c.a0100=q03.a0100 and c.q03z0>='" + start_date + "' and c.q03z0 <= '" + end_date + "')";//源表的过滤条件           
            update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
            updateChangedate = "update " + TabName + " set change_date= (select max("
                    + Sql_switcher.dateToChar(field, "yyyy.MM.dd") + ") from " + nbase + tableName + " n where n.a0100 = "
                    + TabName + ".a0100 and " + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + " >='"
                    + start_date.replaceAll("\\.", "-") + "' and " + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + " <='"
                    + end_date.replaceAll("\\.", "-") + "' ) where nbase='" + nbase + "' and a0100 in (select a0100 from "
                    + nbase + tableName + " n where n.a0100 = " + TabName + ".a0100 and "
                    + Sql_switcher.dateToChar(field, "yyyy-dd-mm") + " >='" + start_date.replaceAll("\\.", "-") + "' and "
                    + Sql_switcher.dateToChar(field, "yyyy-mm-dd") + " <='" + end_date.replaceAll("\\.", "-") + "')";
            // 更新更改时间change_date为空的值
            updateNull = "update " + TabName + " set change_date='" + start_date
                    + "' where change_date is null or change_date = ''";

        }
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            ArrayList list = new ArrayList();
            dao.insert(sql.toString(), list);
            dao.update(update);
            if (updateChangedate.length() > 0) {
                dao.update(updateChangedate);
            }
            if (updateNull.length() > 0) {
                dao.update(updateNull);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /** 处理变动比对结果 **/
    
    /**
     * 将当月来当月走的人员加入到比对表中
     * @param start
     * @param end
     * @param empList
     */
    public ArrayList getLeaveList(String start, String end, ArrayList empList) {
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        // 结束时间的字段代码
        String field = KqParam.getInstance().getKqEndDateField();
        // 开始时间关联的表名
        String endSet = kqDB.getTableNameByFieldName(field);
        Map map = new HashMap();
        ArrayList addList = new ArrayList();
        if (field.length() > 0) {
            ArrayList dbList = this.userView.getPrivDbList();
            for (int i = 0; i < dbList.size(); i++) {
                String nbase = (String) dbList.get(i);
                sql.append("select A.a0100,'" + nbase + "' nbase,A." + field + ",B.b0110,B.e0122,B.e01a1,B.a0101 from " + nbase
                        + endSet + " A left join " + nbase + "A01 B on A.a0100=B.a0100 where A." + field
                        + " is not null and B.a0100 is not null and ");
                sql.append(Sql_switcher.dateToChar("A." + field, "yyyy-mm-dd"));
                sql.append(" between '" + start.replaceAll("\\.", "-") + "' and '" + end.replaceAll("\\.", "-") + "' union ");

            }
            ContentDAO dao = new ContentDAO(this.conn);

            try {
                rs = dao.search(sql.substring(0, sql.length() - 6));
                while (rs.next()) {
                    String nbase = rs.getString("nbase");
                    String a0100 = rs.getString("a0100");
                    java.sql.Date date = rs.getDate(field);

                    RecordVo vo = new RecordVo("kq_employ_change");
                    vo.setString("nbase", nbase);
                    vo.setString("a0100", a0100);
                    vo.setString("b0110", rs.getString("B0110"));
                    vo.setString("e0122", rs.getString("E0122"));
                    vo.setString("a0101", rs.getString("A0101"));
                    vo.setInt("flag", 1);
                    vo.setInt("status", 0);
                    vo.setDate("change_date", date);
                    map.put(nbase.toUpperCase() + a0100, vo);
                }

                for (int i = 0; i < empList.size(); i++) {
                    RecordVo vo = (RecordVo) empList.get(i);
                    String nbase = vo.getString("nbase");
                    String a0100 = vo.getString("a0100");

                    if (map.containsKey(nbase.toUpperCase() + a0100)) {
                        addList.add(map.get(nbase.toUpperCase() + a0100));
                    }
                }

                dao.addValueObject(addList);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return addList;
    }

    /**
     * 处理异常数据
     * @param emplist
     * @param start_date
     * @param end_date
     */
    public void handleUnusual(ArrayList emplist, String start_date, String end_date) {
        ArrayList moreList = new ArrayList();
        ArrayList lessList = new ArrayList();
        int status = 0;
        for (int i = 0; i < emplist.size(); i++) {
            RecordVo vo = (RecordVo) emplist.get(i);
            status = vo.getInt("status");
            if (status == 2) {
                moreList.add(vo);
            } else if (status == 3 || status == 4) {
                lessList.add(vo);
            }
        }

        if (moreList.size() > 0) {
            // 删除冗余数据
            deleteMoreDate(moreList);
        }

        if (lessList.size() > 0) {
            // 添加减少的数据
            try {
                change_Add(lessList, start_date, end_date);
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除日明细中冗余的数据
     * @param list
     */
    private void deleteMoreDate(ArrayList list) {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList delParam = new ArrayList();
        ArrayList delChange = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            ArrayList temp = new ArrayList();
            ArrayList temp2 = new ArrayList();
            RecordVo vo = (RecordVo) list.get(i);
            String nbase = vo.getString("nbase");
            String a0100 = vo.getString("a0100");
            java.sql.Date change_date = DateUtils.getSqlDate(vo.getDate("change_date"));
            java.sql.Date chang_end_date = DateUtils.getSqlDate(vo.getDate("change_end_date"));

            temp.add(nbase);
            temp.add(a0100);
            temp.add(change_date);
            temp.add(chang_end_date);

            delParam.add(temp);

            temp2.add(nbase);
            temp2.add(a0100);
            temp2.add(DateUtils.format(change_date, "yyyy-MM-dd"));
            temp2.add(DateUtils.format(chang_end_date, "yyyy-MM-dd"));
            delChange.add(temp2);
        }

        try {
            sql.append("delete from Q03 where");
            sql.append(" nbase=? ");
            sql.append(" and a0100=?");
            sql.append(" and replace(Q03Z0,'.','-') >=?");
            sql.append(" and replace(Q03Z0,'.','-') <=?");

            dao.batchUpdate(sql.toString(), delChange);

            sql.delete(0, sql.length());

            sql.append("delete from kq_employ_change where");
            sql.append(" nbase=? ");
            sql.append(" and a0100=? and change_date=? and change_end_date=? and status=2");

            dao.batchUpdate(sql.toString(), delParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理考勤增加人员，增加到考勤
     * @param emplist
     *               得到处理的人员资料
     */
    public void change_Add(ArrayList emplist, String start_date, String end_date) throws GeneralException {
        change_add2(emplist, start_date, end_date);
        String userid = this.userView.getUserId();
        String kqtmp_tablename = creat_KqTmp_Table(userid);
        ArrayList del_change_list = new ArrayList();
        ArrayList del_onduty_list = new ArrayList();

        StringBuffer delete_kq_change = new StringBuffer();
        delete_kq_change.append("delete from kq_employ_change");
        delete_kq_change.append(" where nbase=? ");
        delete_kq_change.append(" and a0100=? and flag=4 and status in (1,3,4)");
        
        StringBuffer delete_kq_onduty = new StringBuffer();
        delete_kq_onduty.append("delete from Q03");
        delete_kq_onduty.append(" where nbase=? ");
        delete_kq_onduty.append(" and a0100=?");
        delete_kq_onduty.append(" and Q03Z0 >=?");
        delete_kq_onduty.append(" and Q03Z0 <?");
        
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList insertList = new ArrayList();
        ArrayList list = getitmeidlist(); //A01主集与Q03一样的指标
        ArrayList valuelist = new ArrayList();
        try {
            for (int i = 0; i < emplist.size(); i++) {
                ArrayList del_change_one = new ArrayList();//删除员工变动表中的选定纪录
                ArrayList del_onduty_one = new ArrayList();//为避免与kq_emp_onduty的冲突，先删除
                
                RecordVo vo_change = (RecordVo) emplist.get(i);
                
                String status = vo_change.getString("status");
                // 不是各类增加人员
                if (!"1".equals(status) && !"3".equals(status) && !"4".equals(status)) {
                    continue;
                }
                
                String userbase = vo_change.getString("nbase");
                String a0100 = vo_change.getString("a0100");
                String e0122 = vo_change.getString("e0122");
                String emp_start_date = vo_change.getString("change_date");
                emp_start_date = emp_start_date.substring(0, 10);
                emp_start_date = emp_start_date.replaceAll("-", "\\.");
                
                if (if_Refer(userbase, a0100, e0122, emp_start_date, emp_start_date)) {
                    del_onduty_one.add(userbase);
                    del_onduty_one.add(a0100);
                    del_onduty_one.add(start_date);
                    del_onduty_one.add(emp_start_date);
                    //删除
                    del_change_one.add(userbase);
                    del_change_one.add(a0100);
                    del_change_list.add(del_change_one);
                    del_onduty_list.add(del_onduty_one);
                    
                    if ("3".equals(status)) {
                        String emp_end_date = vo_change.getString("change_end_date");
                        if(StringUtils.isNotEmpty(emp_end_date)) {
                            emp_end_date = emp_end_date.substring(0, 10);
                            emp_end_date = emp_end_date.replaceAll("-", "\\.");
                            
                            if (emp_end_date.compareTo(end_date)<=0) {
                                del_onduty_one = new ArrayList();
                                del_onduty_one.add(userbase);
                                del_onduty_one.add(a0100);
                                del_onduty_one.add(emp_end_date);
                                del_onduty_one.add("9999.99.99");
                                del_onduty_list.add(del_onduty_one);
                            }
                        }
                    }
                }
                
                //增加对应主集中的指标
                if (list != null && list.size() > 0) {
                    String q03item = "";
                    for (int u = 0; u < list.size(); u++) {
                        String itm = (String) list.get(u);
                        if (u == 0) {
                            q03item += " q03." + itm + "=(select " + userbase + "A01." + itm + " from " + userbase + "A01 where "
                                    + userbase + "A01.a0100='" + a0100 + "')";
                        } else {
                            q03item += " ,q03." + itm + "=(select " + userbase + "A01." + itm + " from " + userbase
                                    + "A01 where " + userbase + "A01.a0100='" + a0100 + "')";
                        }
                    }
                    
                    StringBuffer sqlb = new StringBuffer();
                    sqlb.append("UPDATE Q03 set " + q03item);
                    sqlb.append(" where Q03z0>='" + start_date + "' and Q03Z0<='" + end_date + "'");
                    sqlb.append(" and nbase='" + userbase + "' and q03.a0100='" + a0100 + "'");
                    dao.update(sqlb.toString(), valuelist);
                }
            }
            
            // 删除变动比对中的信息kq_employ_change
            dao.batchUpdate(delete_kq_onduty.toString(), del_onduty_list);

            for (int i = 0; insertList.size() > i; i++) {
                String insertSQL = insertList.get(i).toString();
                ArrayList inList = new ArrayList();
                dao.insert(insertSQL.toString(), inList);
            }
            //分析
            dao.batchUpdate(delete_kq_change.toString(), del_change_list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        dropTable(kqtmp_tablename, userid);//删除临时表
    }

    private void change_add2(ArrayList emplist, String start_date, String end_date) {
        String dataUpdateType = "0";
        String analyseType = "100";

        String analysBase = "change";
        KqParameter kq_paramter = new KqParameter(new HashMap(), this.userView, "", this.conn);
        String kq_type = kq_paramter.getKq_type();
        String kq_cardno = kq_paramter.getCardno();
        String kq_Gno = kq_paramter.getG_no();
        try {
            ArrayList list = new ArrayList();
            for (int i = 0; i < emplist.size(); i++) {

                ArrayList up_onduty_one = new ArrayList();//为避免与kq_emp_onduty的冲突，先删除
                RecordVo vo_change = (RecordVo) emplist.get(i);
                String userbase = vo_change.getString("nbase");
                String a0100 = vo_change.getString("a0100");
                up_onduty_one.add(userbase);
                up_onduty_one.add(a0100);
                list.add(up_onduty_one);
            }
            String update = "update kq_employ_change set flag=4 where nbase=? and a0100=?";
            ContentDAO dao = new ContentDAO(this.conn);
            dao.batchUpdate(update, list);

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
            ArrayList nbases = kqUtilsClass.getKqPreList();
            
            DataProcedureAnalyse dataProcedureAnalyse = new DataProcedureAnalyse(this.conn, this.userView, analyseType,
                    kq_type, kq_cardno, kq_Gno, dataUpdateType, nbases);
            dataProcedureAnalyse.setInitflag("1");
            dataProcedureAnalyse.setStatus(status);
            dataProcedureAnalyse.dataAnalys("", "2", start_date, end_date, analysBase);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void change_leave(ArrayList emplist, String start_date, String end_date) throws GeneralException {
        ArrayList del_onduty_list = new ArrayList();
        ArrayList del_change_list = new ArrayList();
        
        //删除kq_emp_onduty的记录
        StringBuffer delete_kq_onduty = new StringBuffer();
        delete_kq_onduty.append("delete from Q03 where");
        delete_kq_onduty.append(" nbase=? ");
        delete_kq_onduty.append(" and a0100=?");
        delete_kq_onduty.append(" and Q03Z0 >=?");
        delete_kq_onduty.append(" and Q03Z0 <= ?");
        
        //删除kq_change的纪录
        StringBuffer delete_kq_change = new StringBuffer();
        delete_kq_change.append("delete from kq_employ_change where");
        delete_kq_change.append(" nbase=? ");
        delete_kq_change.append(" and a0100=? and status=0");
        
        int status = 0;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            for (int i = 0; i < emplist.size(); i++) {

                ArrayList del_onduty_one = new ArrayList();
                ArrayList del_change_one = new ArrayList();

                String userbase = "";
                String a0100 = "";
                String b0110 = "";
                String e0122 = "";
                String emp_start_date = "";

                if (emplist.get(i) instanceof RecordVo) {
                    RecordVo vo_change = (RecordVo) emplist.get(i);
                    userbase = vo_change.getString("nbase");
                    a0100 = vo_change.getString("a0100");
                    b0110 = vo_change.getString("b0110") != null ? vo_change.getString("b0110") : "";
                    e0122 = vo_change.getString("e0122");
                    try {
                        status = vo_change.getInt("status");
                    } catch (Exception e) {
                        //日明细变动比对各页面传入的只是人员列表，不一定有状态值
                        status = 0;
                    }
                    emp_start_date = vo_change.getString("change_date");
                } else {
                    LazyDynaBean vo_change = (LazyDynaBean) emplist.get(i);
                    userbase = (String) vo_change.get("nbase");
                    a0100 = (String) vo_change.get("a0100");
                    b0110 = (String) vo_change.get("b0110") != null ? (String) vo_change.get("b0110") : "";
                    e0122 = (String) vo_change.get("e0122");
                    try {
                        status = (Integer)vo_change.get("status");
                    } catch (Exception e) {
                        //日明细变动比对各页面传入的只是人员列表，不一定有状态值
                        status = 0;
                    }
                    emp_start_date = (String) vo_change.get("change_date");
                }
                
                // 不是减少人员
                if (status != 0) {
                    continue;
                }
                
                if (emp_start_date != null && emp_start_date.length() == 10) {
                    emp_start_date = emp_start_date.substring(0, 10);
                } else {
                    emp_start_date = PubFunc.getStringDate("yyyy.MM.dd");
                }
                emp_start_date = emp_start_date.replaceAll("-", "\\.");
                //判断汇总数据是否已在处理中，只有起草和驳回的可以删除
                if (if_Refer(userbase, a0100, e0122, emp_start_date, emp_start_date)) {
                    //删除日考勤表中的相关纪录
                    del_onduty_one.add(userbase);
                    del_onduty_one.add(a0100);
                    del_onduty_one.add(emp_start_date);
                    del_onduty_one.add(end_date);
                    del_onduty_list.add(del_onduty_one);
                    //删除变动比对结果表的纪录
                    del_change_one.add(userbase);
                    del_change_one.add(a0100);
                    del_change_list.add(del_change_one);
                }
            }
            dao.batchUpdate(delete_kq_onduty.toString(), del_onduty_list);
            dao.batchUpdate(delete_kq_change.toString(), del_change_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 基本信息变化 单位、部门、职位、姓名变动
     * @param emplist
     * @param start_date
     * @param end_date
     */
    public void change_base(ArrayList emplist, String start_date, String end_date, String TabName, String duration)
            throws GeneralException {
        String deleteSQL = "delete from " + TabName + " where nbase=? and a0100=?";
        ArrayList deleteList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            for (int i = 0; i < emplist.size(); i++) {
                RecordVo vo_change = (RecordVo) emplist.get(i);
                String nbase = vo_change.getString("nbase");
                String a0100 = vo_change.getString("a0100");
                ArrayList one_list = new ArrayList();
                one_list.add(nbase);
                one_list.add(a0100);
                deleteList.add(one_list);
                String strDWhere_q03 = "";
                // 开始时间的字段代码
                String field = KqParam.getInstance().getDeptChangeDateField();
                if (field.length() > 0) {
                    strDWhere_q03 = "q03.nbase='" + nbase + "' and q03.q03z0>=(select replace(max(change_date),'-','.') from "
                            + TabName + " where a0100='" + a0100 + "' and nbase='" + nbase + "') and q03.q03z0<='"
                            + end_date.replaceAll("\\-", ".") + "'";//更新目标的表过滤条件
                } else {
                    strDWhere_q03 = "q03.nbase='" + nbase + "' and q03.q03z0>='" + start_date + "' and q03.q03z0<='" + end_date
                            + "'";//更新目标的表过滤条件
                }
                String update_q03 = update_change_base_q03("q03", TabName, nbase, a0100, strDWhere_q03);
                String strDWhere_q05 = "q05.nbase='" + nbase + "' and q05.q03z0='" + duration + "'";
                String update_q05 = update_change_base_q03("q05", TabName, nbase, a0100, strDWhere_q05);

                dao.update(update_q03);
                dao.update(update_q05);
            }
            dao.batchUpdate(deleteSQL, deleteList);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    private String update_change_base_q03(String destTab_t, String TabName, String nbase, String a0100, String strDWhere) {
        String destTab = destTab_t;//目标表
        String srcTab = TabName;//源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1="
                + srcTab + ".E01A1`" + destTab + ".A0101=" + srcTab + ".A0101";//更新串  xxx.field_name=yyyy.field_namex,....
        //      String strSWhere=srcTab+".a0100='"+a0100+"'";//源表的过滤条件
        //更改增加人员库，a0100有可能一样这就要区分人员库
        String strSWhere = srcTab + ".a0100='" + a0100 + "' and " + srcTab + ".nbase='" + nbase + "' ";//源表的过滤条件  
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        String othWhereSql = destTab + ".a0100 ='" + a0100 + "'";
        //      update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
        //更改增加人员库，a0100有可能一样这就要区分人员库
        update = KqUtilsClass.repairSqlTwoTable2(srcTab, strJoin, update, strDWhere, othWhereSql, nbase);
        return update;
    }

    /**********判断是否可以重新计算*********
     * 
     * @param userbase  数据库前缀
     * @param collectdate  操作时间
     * @param code 部门   
     * @param userbase  数据库前缀
     * @return 是否可以起草
     *
    * *****/
    private boolean if_Refer(String userbase, String a0100, String e0122, String satet_date, String end_date) {
        boolean isCorrect = false;
        RowSet rs = null;
        
        StringBuffer sql = new StringBuffer();
        sql.append("select q03z5 from Q05 where ");
        sql.append(" nbase='" + userbase + "'");
        sql.append(" and Q03Z0 >='" + satet_date + "'");
        sql.append(" and Q03Z0 <='" + end_date + "'");
        sql.append(" and a0100='" + a0100 + "'");
        //sql.append(" and q03z5 in ('01','07')"); 
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                String q03z5 = rs.getString("q03z5");
                if (q03z5 == null || q03z5.length() <= 0) {
                    q03z5 = "";
                }
                if ("01".equalsIgnoreCase(q03z5) || "07".equalsIgnoreCase(q03z5)) {
                    isCorrect = true;
                }

            } else {
                isCorrect = true;//第一次汇总    
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return isCorrect;
    }

    /**
     * 得到本期间的最大时间，用于离职人员
     * @param nbase
     * @param a0100
     * @param b0110
     * @param start_date
     * @param end_date
     * @return
     */
    private String maxDate_Leave(String nbase, String a0100, String b0110, String start_date, String end_date) {
        RowSet rs = null;
        
        StringBuffer sql = new StringBuffer();
        sql.append("select MAX(q03z0) as q03z0 from q03 ");
        sql.append(" where a0100='" + a0100 + "' and b0110='" + b0110 + "' and nbase='" + nbase + "'");
        sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
        String q03z0 = "";
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                q03z0 = rs.getString("q03z0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return q03z0;
    }

    /**
     * 建立时间临时表
     * **/
    private String creat_KqTmp_Table(String userid) {
        String tablename = getTmpTableName(this.userView.getUserName(), RegisterInitInfoData.getKqPrivCode(userView));
        DbWizard dbWizard = new DbWizard(this.conn);
        Table table = new Table(tablename);
        if (dbWizard.isExistTable(tablename, false)) {
            dropTable(tablename, userid);
        }
        Field temp = new Field("userid", "用户编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("sDate", "考勤日期");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(20);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp2 = new Field("dkind", "标志");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(2);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tablename;
    }

    /**
     * 删除临时表
     * */
    private void dropTable(String tablename, String userid) {
        RowSet rs = null;
        
        String deleteSQL = "delete from " + tablename + " where userid=?";
        ArrayList deletelist = new ArrayList();
        deletelist.add(userid);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT sDate from " + tablename);
        ContentDAO dao = new ContentDAO(this.conn);
        
        try {
            dao.delete(deleteSQL, deletelist);
            rs = dao.search(sql.toString());
            if (!rs.next()) {
                DbWizard dbWizard = new DbWizard(this.conn);
                Table table = new Table(tablename);
                dbWizard.dropTable(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 新建临时表的名字
     * **/
    private String getTmpTableName(String UserId, String PrivCode) {
        StringBuffer tablename = new StringBuffer();
        tablename.append("kqchange");
        tablename.append("_");
        tablename.append(PrivCode);
        tablename.append("_");
        tablename.append(UserId);
        return tablename.toString();
    }

    //  生成初始时间表
    //  初始数据
    private void initializtion_Kqtmp_Table(ArrayList periodlist, String rest_date, String kq_tmp_table, String rest_b0110,
            String userid) throws GeneralException {
        String deleteSQL = "delete from " + kq_tmp_table + " where userid=?";
        ArrayList deletelist = new ArrayList();
        deletelist.add(userid);
        String insertSQL = "insert into " + kq_tmp_table + " (userid,sDate,dkind) values (?,?,?)";
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList insertList = new ArrayList();
        try {
            dao.delete(deleteSQL, deletelist);
            for (int i = 0; i < periodlist.size(); i++) {
                CommonData datevo = (CommonData) periodlist.get(i);
                String cur_date = datevo.getDataValue();
                ArrayList list = new ArrayList();
                String feast_name = IfRestDate.if_Feast(cur_date, this.conn);
                if (feast_name == null || feast_name.length() <= 0) {
                    if (!IfRestDate.if_Rest(cur_date, userView, rest_date)) {
                        String week_date = IfRestDate.getWeek_Date(rest_b0110, cur_date, this.conn);
                        if (week_date != null && week_date.length() > 0) {
                            list.add(userid);
                            list.add(cur_date);
                            list.add("2");
                        } else {

                            list.add(userid);
                            list.add(cur_date);
                            list.add("1");
                        }
                    } else {
                        String turn_date = IfRestDate.getTurn_Date(rest_b0110, cur_date, this.conn);
                        if (turn_date == null || turn_date.length() <= 0) {
                            list.add(userid);
                            list.add(cur_date);
                            list.add("2");
                        } else {
                            list.add(userid);
                            list.add(cur_date);
                            list.add("1");
                        }

                    }

                } else {
                    String turn_date = IfRestDate.getTurn_Date(rest_b0110, cur_date, this.conn);
                    if (turn_date == null || turn_date.length() <= 0) {
                        list.add(userid);
                        list.add(cur_date);
                        list.add("2");
                    } else {
                        list.add(userid);
                        list.add(cur_date);
                        list.add("1");
                    }
                }
                insertList.add(list);
            }
            dao.batchInsert(insertSQL, insertList);
        } catch (Exception e) {
            e.printStackTrace();
            //throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 用于离职人员添加删除的纪录
     * @param a0100
     * @param b0110
     * @param nbase
     * @param maxdate
     * @param delete_date
     * @param kqtmp_table
     * @param kq_type
     */
    private String insert_Emp(String nbase, String a0100, String start_date, String end_date, String kqtmp_table,
            String kq_type, String flag) {

        StringBuffer insertSql = new StringBuffer();
        if (kq_type == null || kq_type.length() <= 0) {
            kq_type = "01";
        }
        
        //zxj 20150304 从主集取人员基本信息并插入q03
        insertSql.append("INSERT INTO Q03(nbase, Q03Z0, A0100, B0110, E0122, E01A1, A0101, Q03Z1, q03z3, q03z5,I9999) ");
        insertSql.append(" SELECT nbase, Q03Z0, A0100, B0110, E0122, E01A1, A0101, Q03Z1, q03z3, q03z5,I9999");
        insertSql.append(" FROM (");
        insertSql.append("SELECT '").append(nbase).append("' AS nbase, DT.sDate AS Q03Z0, A0100, B0110, ");
        insertSql.append("E0122,E01A1,A0101, 1 As Q03Z1, " + kq_type + " As q03z3, '01' As q03z5,a0000 as i9999");
        insertSql.append(" FROM ").append(nbase).append("A01").append(",").append(kqtmp_table + " DT");
        if ("1".equals(flag)) {
            //从第一天开始添加，适于离职人员
            insertSql.append(" WHERE DT.dkind='1' and  DT.sDate>='" + start_date + "' and DT.sDate<'" + end_date + "'");
        } else {
            insertSql.append(" WHERE DT.dkind='1' and  DT.sDate>'" + start_date + "' and DT.sDate<'" + end_date + "'");
        }

        insertSql.append(" and a0100='" + a0100 + "'");
        insertSql.append(") B");
        insertSql.append(" WHERE not exists(select 1 from q03 A where A.a0100=B.a0100 and A.nbase=B.nbase and A.q03z0=B.q03z0 ");
        insertSql.append(" and A.a0100='" + a0100 + "' and A.nbase='" + nbase + "'");
        insertSql.append(" and A.q03z0>='" + start_date + "' and A.q03z0<='" + end_date + "')");

        return insertSql.toString();

    }

    private ArrayList getitmeidlist() {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        RowSet rowSet = null;
        ArrayList listitem = new ArrayList();
        sql.append("select itemid from fielditem where fieldsetid='A01' and useflag='1'");
        try {
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                String itemid = rowSet.getString("itemid");
                listitem.add(itemid);
            }
            
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                String qitemid = fielditem.getItemid();
                if ("nbase".equalsIgnoreCase(qitemid) || "e0122".equalsIgnoreCase(qitemid)
                        || "a0101".equalsIgnoreCase(qitemid) || "b0110".equalsIgnoreCase(qitemid)) {
                    continue;
                }
                
                for (int p = 0; p < listitem.size(); p++) {
                    String it = (String) listitem.get(p);
                    
                    if (!qitemid.equalsIgnoreCase(it)) {
                        continue;
                    }
                    
                    list.add(qitemid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        
        return list;
    }
    
    public String getBdstatic() {
        return bdstatic;
    }
    public void setBdstatic(String bdstatic) {
        this.bdstatic = bdstatic;
    }

    public String getEmpBaseInfoChangeTab() {
        return empBaseInfoChangeTab;
    }

    public void setEmpBaseInfoChangeTab(String empBaseInfoChangeTab) {
        this.empBaseInfoChangeTab = empBaseInfoChangeTab;
    }

}
