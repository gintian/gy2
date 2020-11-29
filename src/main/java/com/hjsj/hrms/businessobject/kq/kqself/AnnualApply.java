package com.hjsj.hrms.businessobject.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqClassArray;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.module.kq.interfaces.KqAppCaculator;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AnnualApply {
    private Connection conn;
    private UserView   userView;
    private String     appLeavedMess;           // 已经申请假期请假的信息
    private String     holiday_Minus_Rule = "";
    private boolean validateUsedTimeLenError = true;
    
    private KqVer kqVer;
    private HashMap<String, String> CurDuration = null;

    public String getHoliday_Minus_Rule() {
        return holiday_Minus_Rule;
    }

    public void setHoliday_Minus_Rule(String holiday_Minus_Rule) {
        this.holiday_Minus_Rule = holiday_Minus_Rule;
    }

    public AnnualApply() {
        this.kqVer = new KqVer();
    }

    public AnnualApply(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
        
        this.kqVer = new KqVer();
    }

    public ArrayList getPlanList(UserView userView, Connection conn) {
        String my_e0122 = userView.getUserDeptId() != null ? userView.getUserDeptId() : "";
        ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.conn);
        String my_b0110 = managePrivCode.getPrivOrgId();
        ArrayList volist = new ArrayList();
        String b0110s = getb0110s(my_e0122, my_b0110, conn);
        if (b0110s == null || b0110s.length() <= 0) {
            return volist;
        }
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select q2901,q2905 from q29");
        sqlstr.append(" where  q29z5=? ");

        if (!userView.isSuper_admin()) {
            sqlstr.append(" and e0122 in(" + b0110s + ",'UM') ");
        }
        ArrayList sqllist = new ArrayList();
        sqllist.add("04");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(sqlstr.toString(), sqllist);
            while (rs.next()) {
                CommonData vo = new CommonData();
                vo.setDataName(rs.getString("q2905"));
                vo.setDataValue(rs.getString("q2901"));
                volist.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return volist;
    }

    /**
     * 得到上级部门的id
     * 
     * @param codeitemid
     * @return
     */
    public String getUpDeptId(String codeitemid, Connection conn) {
        String orgSql = "SELECT parentid,codeitemid from organization where codeitemid='" + codeitemid + "'";
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        String parentid = "";
        try {
            rs = dao.search(orgSql);
            if (rs.next()) {
                parentid = rs.getString("parentid");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return parentid;
    }

    public String getb0110s(String my_e0122, String my_b0110, Connection conn) {
        ArrayList list = new ArrayList();
        if (my_e0122 == null) {
            return "";
        } else if (my_e0122.length() <= 0) {
            return "''";
        }
        // do
        // {
        // list.add(my_e0122);
        // my_e0122=getUpDeptId(my_e0122,conn);
        // }while(!my_b0110.equals(my_e0122));

        list.add(my_e0122);
        while (my_b0110.length() > 0 && !my_b0110.equals(my_e0122)) {
            list.add(my_e0122);
            my_e0122 = getUpDeptId(my_e0122, conn);
        }

        StringBuffer e0122Str = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            e0122Str.append("'" + list.get(i).toString() + "',");
        }
        String b0110s = e0122Str.toString().substring(0, e0122Str.length() - 1);
        return b0110s;
    }

    public String getCodeitemdesc(String codesetid, String codeitemid, Connection conn) throws GeneralException {
        String codeitemdesc = "";
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowSet = null;
        try {
            sql.append("SELECT codeitemid, codeitemdesc  FROM codeitem  where codesetid ='" + codesetid + "' and codeitemid = '");
            sql.append(codeitemid);
            sql.append("'");
            rowSet = dao.search(sql.toString());
            if (rowSet.next()) {
                codeitemdesc = rowSet.getString("codeitemdesc");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return codeitemdesc;
    }

    /**
     * 判断申请是否与请假、加班、公出时间重复
     * 规则：加班、请假申请时不与公出比较；公出申请不与加班和请假比较；
     * @Title: isRepeatedAllAppType   
     * @Description:    
     * @param appTab 申请表（q11:加班  q15：请假  q13：公出）
     * @param nbase
     * @param a0100
     * @param a0101
     * @param z1
     * @param z3
     * @param conn
     * @param id
     * @param id2
     * @return
     * @throws GeneralException
     */
    public boolean isRepeatedAllAppType(String appTab, String nbase, String a0100, String a0101, String z1, String z3, Connection conn,
            String id, String id2) throws GeneralException {
        boolean isRepeated = false;

        //公出期间允许请假、加班
        if("1".equals(KqParam.getInstance().getOFFICELEAVE_ENABLE_LEAVE_OVERTIME())) {
            if ("q11".equalsIgnoreCase(appTab) || "q15".equalsIgnoreCase(appTab)) {
                isRepeated = isRepeatedApp(nbase, a0100, z1, z3, "q11", conn, id, id2);
                if(isRepeated) {
                    throw new GeneralException("", a0101 + " " + ResourceFactory.getProperty("error.kq.overtime.exist"), "", "");
                }
                
                isRepeated = isRepeatedApp(nbase, a0100, z1, z3, "q15", conn, id, id2);
                if(isRepeated) {
                    throw new GeneralException("", a0101 + " " + ResourceFactory.getProperty("error.kq.leave.exist"), "", "");
                }
            } else {
                isRepeated = isRepeatedApp(nbase, a0100, z1, z3, "q13", conn, id, id2);
                if(isRepeated) {
                    throw new GeneralException("", a0101 + " " + ResourceFactory.getProperty("error.kq.officeleave.exist"), "", "");
                }
            }
        } else {
            isRepeated = isRepeatedApp(nbase, a0100, z1, z3, "q11", conn, id, id2);
            if(isRepeated) {
                throw new GeneralException("", a0101 + " " + ResourceFactory.getProperty("error.kq.overtime.exist"), "", "");
            }
            
            isRepeated = isRepeatedApp(nbase, a0100, z1, z3, "q15", conn, id, id2);
            if(isRepeated) {
                throw new GeneralException("", a0101 + " " + ResourceFactory.getProperty("error.kq.leave.exist"), "", "");
            }
            
            isRepeated = isRepeatedApp(nbase, a0100, z1, z3, "q13", conn, id, id2);
            if(isRepeated) {
                throw new GeneralException("", a0101 + " " + ResourceFactory.getProperty("error.kq.officeleave.exist"), "", "");
            }
        }

        return isRepeated;
    }
    /**
     * @Title: isRepeatedAllAppType   
     * @Description: 判断申请是否与请假、加班、公出时间重复   
     * @param nbase 
     * @param a0100
     * @param a0101
     * @param z1 申请开始时间
     * @param z3 申请结束时间
     * @param conn
     * @param id 申请单号（报批报审修改等操作，检查时需排除本单）
     * @param id2 销假单号 （销假时，除原申请单，还需排除销假单本身）
     * @param @return
     * @param @throws GeneralException 如果重复则抛出异常信息
     * @return boolean 如果不重复则返回true
     * @throws
     */
    public boolean isRepeatedAllAppType(String nbase, String a0100, String a0101, String z1, String z3, Connection conn,
            String id, String id2) throws GeneralException {
        boolean isRepeated = false;

        if (isRepeatedApp(nbase, a0100, z1, z3, "q11", conn, id, id2)
                || isRepeatedApp(nbase, a0100, z1, z3, "q13", conn, id, id2)
                || isRepeatedApp(nbase, a0100, z1, z3, "q15", conn, id, id2)) {
            isRepeated = true;
            throw new GeneralException("", a0101 + "，" + ResourceFactory.getProperty("error.kq.existdate"), "", "");
        }

        return isRepeated;
    }

    /**
     * 判断申请记录是否重复
     * 
     * @param nbase
     * @param a0100
     * @param z1
     * @param z3
     * @param table 要判断记录重复的申请表（q11、q13、q15、q31）
     * @return
     * @throws GeneralException
     */
    public boolean isRepeatedApp(String nbase, String a0100, String z1, String z3, String table, Connection conn, String id,
            String id2) {
        boolean isRepeated = false;

        String column_z1 = table + "z1";
        String column_z3 = table + "z3";

        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append("select * from " + table);
        selectSQL.append(" where UPPER(nbase)='" + nbase.toUpperCase() + "'");
        selectSQL.append(" and a0100='" + a0100 + "'");

        if (id != null && id.length() > 0) {
            selectSQL.append(" and (" + table + "01<>'" + id + "'");
            if (id2 != null && id2.length() > 0) {
                selectSQL.append(" and " + table + "01<>'" + id2 + "'");
            }
            selectSQL.append(")");
        }

        //废除的申请不用考虑
        selectSQL.append(" and " + table + "z5<>'10'");
        //驳回的申请不用考虑
        selectSQL.append(" AND " + table + "z5<>'07'");
        selectSQL.append(" and " + column_z1 + "<" + Sql_switcher.dateValue(z3));
        selectSQL.append(" and " + column_z3 + ">" + Sql_switcher.dateValue(z1));

        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(selectSQL.toString());
            if (rs.next()) {
                isRepeated = true;

                StringBuffer mess = new StringBuffer();
                Date z1d = rs.getTimestamp(column_z1);
                Date z3d = rs.getTimestamp(column_z3);
                mess.append(DateUtils.format(z1d, "yyyy.MM.dd HH:mm"));
                mess.append("---");
                mess.append(DateUtils.format(z3d, "yyyy.MM.dd HH:mm"));

                this.setAppLeavedMess(mess.toString());
            }
            //	44245 由于公出与加班已增加销假功能故取消只校验请假的校验//如果请假记录有重复，那么需进一步检查，是否重复部分已销假
            if (isRepeated) { 
                selectSQL.setLength(0);
                selectSQL.append("select " + Sql_switcher.isnull(table+"19", "''") + " as table19");
                selectSQL.append(" from " + table);
                selectSQL.append(" where UPPER(nbase)='" + nbase.toUpperCase() + "'");
                selectSQL.append(" and a0100='" + a0100);
                selectSQL.append("' and "+table+"17=1");
                selectSQL.append(" and " + Sql_switcher.isnull(table+"19", "'kkk'") + " <>'kkk'");
                selectSQL.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3));
                selectSQL.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z1));
                selectSQL.append(" and "+table+"z5='03'");
                rs = dao.search(selectSQL.toString());
                if (rs.next()) {
                    isRepeated = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return isRepeated;
    }

    public boolean getKqType(String kq_type, Connection conn, UserView userView) {
        boolean isCorrect = true;
        ContentDAO dao = new ContentDAO(conn);
        StringBuffer sql = new StringBuffer();
        sql.append("select " + kq_type + " from " + userView.getDbname() + "A01");
        sql.append(" where a0100='" + userView.getA0100() + "'");
        sql.append(" and " + kq_type + "='03'");
        RowSet rs = null;
        try {

            rs = dao.search(sql.toString());
            if (rs.next()) {
                isCorrect = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }

    /**
     * 判断一时间段是否有公休日
     * 
     * @param usr,tab,
     *            star,endtime
     * 
     * @return boolean 返回真 和假
     * @throws SQLException
     * @throws GeneralException
     */
    public boolean is_Rest(Date da1, Date da2, String code, UserView userView, Connection conn) throws GeneralException {
        boolean ret = false;

        if (code == null || code.length() <= 0) {
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.conn);
            code = managePrivCode.getPrivOrgId();
        }
        String b0110 = "UN" + code;
        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, conn);
        String rest_date = restList.get(0).toString();
        String rest_b0110 = restList.get(1).toString();
        int num = RegisterDate.diffDate(da1, da2);
        for (int m = 0; m <= num; m++) {
            String op_date_to = getDateByAfter(da1, m);
            if (IfRestDate.if_Rest(op_date_to, userView, rest_date)) {
                String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, conn);
                if (turn_date == null || turn_date.length() <= 0) {
                    ret = true;
                    continue;
                } else {
                    return false;
                }
            } else {
                String week_date = IfRestDate.getWeek_Date(rest_b0110, op_date_to, conn);
                if (week_date != null && week_date.length() > 0) {
                    ret = true;
                    continue;
                } else {
                    return false;
                }
            }
        }
        return ret;
    }

    /**
     * 判断一时间段是否有节假日
     * 
     * @param usr,tab,
     *            star,endtime
     * 
     * @return boolean 返回真 和假
     * @throws GeneralException
     */
    public boolean is_Feast(Date da1, Date da2, String code, UserView userView, Connection conn) throws GeneralException {
        boolean ret = false;
        try {
            if (code == null || code.length() <= 0) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.conn);
                code = managePrivCode.getPrivOrgId();
            }
            String b0110 = "UN" + code;
            ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, conn);
            String rest_b0110 = restList.get(1).toString();
            int num = RegisterDate.diffDate(da1, da2);
            for (int m = 0; m <= num; m++) {
                String op_date_to = getDateByAfter(da1, m);
                String feast_name = IfRestDate.if_Feast(op_date_to, conn);
                if (feast_name != null && feast_name.length() > 0) {
                    String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, conn);
                    if ((turn_date == null || turn_date.length() <= 0)) {
                        ret = true;
                        continue;
                    } else {
                        return false;
                    }
                } else {
                    String week_date = IfRestDate.getWeek_Date(rest_b0110, op_date_to, conn);
                    if (week_date != null && week_date.length() > 0) {
                        ret = true;
                        continue;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception se) {
            se.printStackTrace();
            throw GeneralExceptionHandler.Handle(se);
        }

        return ret;

    }

    /**
     * 取
     * 
     * @param dateString，
     *            某年某月某天
     * @param afterNum
     *            天数
     * @return string 返回相加后得到新的某年某月某天
     */
    public static String getDateByAfter(java.util.Date date, int afterNum) throws GeneralException {

        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        calendar.add(GregorianCalendar.DAY_OF_YEAR, afterNum);

        return new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime());
    }

    /**
     * 返回考勤年
     * 
     * @param conn
     * @return
     */
    public ArrayList getYearList(Connection conn) {
        ArrayList yearlist = new ArrayList();
        ContentDAO year_dao = new ContentDAO(conn);
        StringBuffer year_str = new StringBuffer();
        RowSet rs = null;
        year_str.append("select distinct kq_year from kq_duration");
        try {
            rs = year_dao.search(year_str.toString());
            while (rs.next()) {
                CommonData yearvo = new CommonData(rs.getString("kq_year"), rs.getString("kq_year"));
                yearlist.add(yearvo);
            }
        } catch (Exception sqle) {
            sqle.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return yearlist;
    }

    /**
     * 返回考勤期间list
     * 
     * @param conn
     * @param kq_year
     * @return
     */
    public ArrayList getDurationlist(Connection conn, String kq_year) {
        ArrayList durationlist = new ArrayList();
        if (kq_year != null && kq_year.length() > 0) {

            StringBuffer duration_str = new StringBuffer();
            ContentDAO duration_dao = new ContentDAO(conn);
            duration_str.append("select  kq_duration,kq_start,kq_end from kq_duration where kq_year='");
            duration_str.append(kq_year);
            duration_str.append("'");
            RowSet rs = null;
            try {
                rs = duration_dao.search(duration_str.toString());
                while (rs.next()) {
                    CommonData durationvo = new CommonData(rs.getString("kq_duration"), 
                            rs.getString("kq_duration") + '('
                            + PubFunc.FormatDate(rs.getDate("kq_start")).replaceAll("-", "\\.") + "-"
                            + PubFunc.FormatDate(rs.getDate("kq_end")).replaceAll("-", "\\.") + ')');
                    durationlist.add(durationvo);
                }
            } catch (Exception sqle) {
                sqle.printStackTrace();

            } finally {
                KqUtilsClass.closeDBResource(rs);
            }
        }
        return durationlist;
    }

    /**
     * 返回给定年和期间的第一天和最后一天
     * 
     * @param conn
     *            数据库连接
     * @param kq_year
     *            考勤年
     * @param kq_duration
     *            考勤期
     * @return list(只包含第一天和最后一天)
     */
    public String getKqDayWhere(String table, Connection conn, String kq_year, String kq_duration) throws GeneralException {

        RowSet rowSet = null;
        StringBuffer cond_str = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        strsql.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
        strsql.append(" where kq_year ='" + kq_year + "'");
        strsql.append(" and kq_duration ='" + kq_duration + "'");
        ContentDAO dao = new ContentDAO(conn);
        try {
            rowSet = dao.search(strsql.toString());
            if (rowSet.next()) {
                Date d1 = rowSet.getDate("kq_start");
                Date d2 = rowSet.getDate("kq_end");
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String kq_start = format1.format(d1);
                String kq_end = format1.format(d2);
                cond_str.append(table + "z1");
                cond_str.append(">=");
                cond_str.append(Sql_switcher.dateValue(kq_start + " 00:00:00"));
                cond_str.append(" and ");
                cond_str.append(table + "z1");
                cond_str.append("<=");
                cond_str.append(Sql_switcher.dateValue(kq_end + " 23:59:59"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(new
            // GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }

        return cond_str.toString();
    }

    /**
     * 得到权限下所有的部门编号
     * 
     * @param userView
     * @param whereIN
     * @param org_id
     * @param conn
     * @return
     */
    public String selcet_kq_OrgId(UserView userView, String whereIN, String org_id, Connection conn) {
        StringBuffer sql = new StringBuffer();
        String nbase = userView.getDbname();
        sql.append("select distinct " + org_id + " from " + nbase + "A01");
        sql.append(" where b0110='" + userView.getUserOrgId() + "'");
        sql.append(" and a0100 in(select a0100 " + whereIN + ")");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowSet = null;
        StringBuffer e0122s = new StringBuffer();
        try {
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                e0122s.append("'" + rowSet.getString(org_id) + "',");
            }
            e0122s.setLength(e0122s.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return e0122s.toString();
    }

    /**
     * 返回请假申请的时间
     * 
     * @param start_date
     * @param end_date
     * @param a0100
     * @param nbase
     * @param b0110
     * @param kqItem_hash
     * @return
     */
    public float getLeaveTime(Date start_date, Date end_date, String a0100, String nbase, String b0110, HashMap kqItem_hash)
            throws GeneralException {
        KqClassArray kqClassArray = new KqClassArray(this.conn);
        int num = RegisterDate.diffDate(start_date, end_date);
        String class_id = "";
        float timeLen = 0;
        float time_sum = 0;
        float timeValue = 0;
        try {
            for (int m = 0; m <= num; m++) {
                String op_date_to = getDateByAfter(start_date, m);
                class_id = getClassId(op_date_to, a0100, nbase);
                if (class_id == null || class_id.length() <= 0) {
                    /*
                     * String message="该假期为管理假期类型,'"+op_date_to+"'没排班,请先排班！";
                     * throw GeneralExceptionHandler.Handle(new
                     * GeneralException("",message,"",""));
                     */
                    timeValue = timeValue + 1;
                    continue;
                }
                RecordVo vo = kqClassArray.getClassMessage(class_id);
                if (vo == null) {
                    continue;
                }
                
                Date d_curDate = DateUtils.getDate(op_date_to, "yyyy.MM.dd");
                if ("0".equals(class_id)) {
                    String has_rest = (String) kqItem_hash.get("has_rest");
                    if (has_rest == null || has_rest.length() <= 0) {
                        has_rest = "0";
                    }
                    if ("0".equals(has_rest)) {
                        continue;
                    } else if ("1".equals(has_rest)) {
                        String op_date = getDateByAfter(start_date, m - 1);
                        String class_id_1 = getClassId(op_date, a0100, nbase);
                        RecordVo vo_1 = kqClassArray.getClassMessage(class_id_1);
                        if (vo_1 != null) {
                            Date op_uDate = DateUtils.getDate(op_date, "yyyy.MM.dd");
                            boolean isCorrect = endTimeBH(vo_1, op_uDate, end_date);
                            if (!isCorrect) {
                                timeValue = timeValue + 1;
                            }
                        } else {
                            timeValue = timeValue + 1;
                        }
                        continue;
                    }
                }
                if (class_id == null || class_id.length() <= 0 || "0".equals(class_id)) {
                    ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);
                    if (validateAppOper.is_Feast(d_curDate, d_curDate, b0110)) {
                        String has_feast = (String) kqItem_hash.get("has_feast");
                        if (has_feast == null || has_feast.length() <= 0) {
                            has_feast = "0";
                        }
                        if ("0".equals(has_feast)) {
                            continue;
                        } else if ("1".equals(has_feast)) {
                            String op_date = getDateByAfter(start_date, m - 1);
                            String class_id_1 = getClassId(op_date_to, a0100, nbase);
                            RecordVo vo_1 = kqClassArray.getClassMessage(class_id_1);
                            if (vo_1 != null) {
                                Date op_uDate = DateUtils.getDate(op_date, "yyyy.MM.dd");
                                boolean isCorrect = endTimeBH(vo_1, op_uDate, end_date);
                                if (!isCorrect) {
                                    timeValue = timeValue + 1;
                                }
                            } else {
                                timeValue = timeValue + 1;
                            }
                            continue;
                        }
                    }
                }
                HashMap hash = getCurDateTime(vo, d_curDate, start_date, end_date);
                Float timeLenF = (Float) hash.get("timeLen");
                timeLen = timeLenF.floatValue();
                Float timeSumF = (Float) hash.get("time_sum");
                time_sum = timeSumF.floatValue();
                timeValue = timeValue + timeLen / time_sum;
                String lenS = "0";
                if (timeLen != 0) {
                    lenS = PubFunc.round(timeValue + "", 2);
                    timeValue = Float.parseFloat(lenS);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return timeValue;
    }

    /**
     * 计算假期管理扣除天数（包含假期规则）
     * 
     * @param start_date
     * @param end_date
     * @param a0100
     * @param nbase
     * @param b0110
     * @param kqItem_hash
     * @param history_rule
     * @return
     * @throws GeneralException
     */
    public float getHistoryLeaveTime(Date start_date, Date end_date, String a0100, String nbase, String b0110,
            HashMap kqItem_hash, float history_rule[]) throws GeneralException {
        // 高校医院班考勤计算时长
        if (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL) {
            LazyDynaBean appInfo = new LazyDynaBean();
            appInfo.set("nbase", nbase);
            appInfo.set("a0100", a0100);
            appInfo.set("type", kqItem_hash.get("item_id"));
            appInfo.set("starttime", start_date);
            appInfo.set("endtime", end_date);
            
            KqAppCaculator kqAppCalculator = new KqAppCaculator(conn);
            return (float)kqAppCalculator.calcAppTimeLen(appInfo);
        }
        
        //假期都按天算
        kqItem_hash.put("item_unit", KqConstant.Unit.DAY);
        float days = calcLeaveAppTimeLen(nbase, a0100, b0110, start_date, end_date, kqItem_hash, history_rule, Integer.MAX_VALUE);
        return roundNumByItemDecimalWidth(kqItem_hash, days);
    }

    /**
     * 返回请假申请的时间（天）
     * 
     * @param start_date
     * @param end_date
     * @param a0100
     * @param nbase
     * @param b0110
     * @param kqItem_hash
     * @return
     */
    public float getLeaveTime_2(Date start_date, Date end_date, String a0100, String nbase, String b0110, HashMap kqItem_hash,
            float mayLeaveTime, float[] holiday_rules) throws GeneralException {
       float timeValue = 0;
       try {
           // 高校医院班考勤计算时长
           if (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL) {
               LazyDynaBean appInfo = new LazyDynaBean();
               appInfo.set("nbase", nbase);
               appInfo.set("a0100", a0100);
               appInfo.set("type", kqItem_hash.get("item_id"));
               appInfo.set("starttime", start_date);
               appInfo.set("endtime", end_date);
               
               KqAppCaculator kqAppCalculator = new KqAppCaculator(conn);
               timeValue = (float)kqAppCalculator.calcAppTimeLen(appInfo);
           } else {
               timeValue = calcLeaveAppTimeLen(nbase, a0100, b0110, start_date, end_date, kqItem_hash, holiday_rules, Integer.MAX_VALUE);
           }
           
    	   if (timeValue >= mayLeaveTime) {
               return timeValue;
           }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return timeValue;
    }

    /**
     * 得到一天的申请时间（分钟）
     * 
     * @param vo
     * @param d_curDate
     * @param start_date
     * @param end_date
     * @return
     */
    public HashMap getCurDateTime(RecordVo vo, Date d_curDate, Date start_date, Date end_date) {
        HashMap<String, String> classMap = new HashMap<String, String>();
        
        String strFTime = "";// 班次开始时间
        String strTTime = "";// 班次结束时间
        String strOffFlextime = "";// 班次弹性结束时间
        String strOnFlextime = "";// 班次弹性开始时间
        
        for (int i = 0; i < 3; i++) {
            strFTime = vo.getString("onduty_" + (i + 1));
            strTTime = vo.getString("offduty_" + (i + 1));
            strOffFlextime = vo.getString("offduty_flextime_" + (i + 1));
            strOnFlextime = vo.getString("onduty_flextime_" + (i + 1));
            
            classMap.put("onduty_" + (i + 1), strFTime);
            classMap.put("offduty_" + (i + 1), strTTime);
            classMap.put("offduty_flextime_" + (i + 1), strOffFlextime);
            classMap.put("onduty_flextime_" + (i + 1), strOnFlextime);
        }
        return getCurDateTime(classMap, d_curDate, start_date, end_date);
    }
    
    /**
     * 得到一天的申请时间（分钟）
     * 
     * @param vo
     * @param d_curDate
     * @param start_date
     * @param end_date
     * @return
     */
    public HashMap getCurDateTime(HashMap<String, String> classMap, Date d_curDate, Date start_date, Date end_date) {
        //解决计算差一分钟的问题
        //end_date = DateUtils.getDate(DateUtils.format(end_date, "yyyy.MM.dd HH:mm") + ":59", "yyyy.MM.dd HH:mm:ss");
        
        Date perOffTime = null;
        String perOffTime_str = "";
        String strFTime = "";// 班次开始时间
        String strTTime = "";// 班次结束时间
        String strOffFlextime = "";// 班次弹性结束时间
        String strOnFlextime = "";// 班次弹性开始时间
        
        Date FTime = null;
        Date TTime = null;
        Date FDT = null;
        Date TDT = null;
        
        String strDate = DateUtils.format(d_curDate, "yyyy.MM.dd");
        
        float time_f = 0;
        float timeLen = 0;
        float time_sum = 0;
        boolean isFlextime = false;
        long timeLenFactFlextime = 0;
        boolean isOffDutyFlextime = false;
        
        for (int i = 0; i < 3; i++) {
            strFTime = classMap.get("onduty_" + (i + 1));
            strTTime = classMap.get("offduty_" + (i + 1));
            strOffFlextime = classMap.get("offduty_flextime_" + (i + 1));
            strOnFlextime = classMap.get("onduty_flextime_" + (i + 1));
            if ((strFTime == null || strFTime.length() <= 0) || (strTTime == null || strTTime.length() <= 0)) {
                break;
            }

            FTime = DateUtils.getDate(strFTime, "HH:mm");
            TTime = DateUtils.getDate(strTTime, "HH:mm");
            if (strOnFlextime != null && strOnFlextime.length() > 0)// 弹性延时班,是否用开始时间
            {
                Date TFlexTime = DateUtils.getDate(strOnFlextime, "HH:mm");
                Date DT2 = DateUtils.getDate(strDate + " " + strOnFlextime, "yyyy.MM.dd HH:mm");
               // Date DTT2 = DateUtils.getDate(strDate + " " + strTTime, "yyyy.MM.dd HH:mm");
                if (TFlexTime.before(FTime))// 跨天
                {
                    DT2 = DateUtils.addDays(DT2, 1);
                }
                
//                float time = KQRestOper.getPartMinute(DT2, start_date);// 如果开始时间在弹性开始时间以后或等于开始时间，那么就用弹性开始时间
//                float time2 = KQRestOper.getPartMinute(start_date, DTT2);
//                if (time >= 0 && time2 >= 0) {//
//                    FTime = DateUtils.getDate(strOnFlextime, "HH:mm");
//                    strFTime = strOnFlextime;
//                    isFlextime = true;
//                }
                
                FDT = DateUtils.getDate(strDate + " " + strFTime + ":00", "yyyy.MM.dd HH:mm:ss");
                if (0 >= DT2.compareTo(start_date)) {
                    timeLenFactFlextime = KQRestOper.getPartMinute(FDT, DT2);
                    FTime = DateUtils.getDate(strOnFlextime, "HH:mm");
                    strFTime = strOnFlextime;
                    isFlextime = true;
                } else if (0 < DT2.compareTo(start_date) && 0 > FDT.compareTo(start_date)) {
                    FTime = DateUtils.getDate(DateUtils.format(start_date, "HH:mm"), "HH:mm");
                    strFTime = DateUtils.format(start_date, "HH:mm");
                    isFlextime = true;
                    timeLenFactFlextime = KQRestOper.getPartMinute(FDT, start_date);
                }
                    
            }
            
            if (strOffFlextime != null && strOffFlextime.length() > 0 && isFlextime)// 弹性延时班,是否用结束时间
            {
                //strTTime = strOffFlextime;
                isOffDutyFlextime = true;
                /*
                Date TFlexTime = DateUtils.getDate(strOffFlextime, "HH:mm");
                Date DT1 = DateUtils.getDate(strDate + " " + strTTime, "yyyy.MM.dd HH:mm");
                Date DT2 = DateUtils.getDate(strDate + " " + strOffFlextime, "yyyy.MM.dd HH:mm");
                if (TFlexTime.before(TTime))// 跨天
                {
                    DT2 = DateUtils.addDays(DT2, 1);
                }
                float time = KQRestOper.getPartMinute(DT1, end_date);
                if (time >= 0) {
                    TTime = DateUtils.getDate(strOffFlextime, "HH:mm");
                    strTTime = strOffFlextime;
                }*/
            }
            
            if (perOffTime_str != null && perOffTime_str.length() > 0) {
                perOffTime = DateUtils.getDate(perOffTime_str, "HH:mm");
                time_f = KQRestOper.getPartMinute(perOffTime, FTime);
                if (time_f < 0) {
                    strDate = DateUtils.format(DateUtils.addDays(d_curDate, 1), "yyyy.MM.dd");
                }
            }
            
            perOffTime_str = strTTime;
            time_f = KQRestOper.getPartMinute(FTime, TTime);
            if (time_f > 0) {
                FDT = DateUtils.getDate(strDate + " " + strFTime + ":00", "yyyy.MM.dd HH:mm:ss");
                TDT = DateUtils.getDate(strDate + " " + strTTime + ":59", "yyyy.MM.dd HH:mm:ss");
            } else {
                FDT = DateUtils.getDate(strDate + " " + strFTime + ":00", "yyyy.MM.dd HH:mm:ss");
                strDate = DateUtils.format(DateUtils.addDays(d_curDate, 1), "yyyy.MM.dd");
                TDT = DateUtils.getDate(strDate + " " + strTTime + ":59", "yyyy.MM.dd HH:mm:ss");
            }
            
            if (isOffDutyFlextime) {
                TDT = new Date(TDT.getTime() + timeLenFactFlextime * 60000);
            }

            time_sum = time_sum + KQRestOper.getPartMinute(FDT, TDT);
            timeLen = timeLen + calcTimSpan(FDT, TDT, start_date, end_date);
        }
        HashMap hash = new HashMap();
        hash.put("time_sum", new Float(time_sum));
        hash.put("timeLen", new Float(timeLen));
        return hash;
    }
    /**
     * 得到一天的申请时间（分钟）
     * 
     * @param vo
     * @param d_curDate
     * @param start_date
     * @param end_date
     * @param m
     * @param num 
     * @return
     */
    private HashMap getCurDateOverTime(RecordVo vo, Date d_curDate, Date start_date, Date end_date, 
    		int m, int num, boolean isOverDay, String yesterday, Date dayFrom, Date dayTo) {
        
        Date perOffTime = null;
        String perOffTime_str = "";
        String strFTime = "";// 班次开始时间
        String strTTime = "";// 班次结束时间
        String strOffFlextime = "";// 班次弹性结束时间
        String strOnFlextime = "";// 班次弹性开始时间
        Date FTime = null;
        Date TTime = null;
        Date FDT = null;
        Date TDT = null;
        String strDate = DateUtils.format(d_curDate, "yyyy.MM.dd");
        float time_f = 0;
        float timeLen = 0;
        float time_sum = 0;
        boolean isFlextime = false;
        for (int i = 0; i < 3; i++) {
            strFTime = vo.getString("onduty_" + (i + 1));
            strTTime = vo.getString("offduty_" + (i + 1));
            strOffFlextime = vo.getString("offduty_flextime_" + (i + 1));
            strOnFlextime = vo.getString("onduty_flextime_" + (i + 1));
            if ((strFTime == null || strFTime.length() <= 0) || (strTTime == null || strTTime.length() <= 0)) {
                break;
            }

            FTime = DateUtils.getDate(strFTime, "HH:mm");
            TTime = DateUtils.getDate(strTTime, "HH:mm");
            if (strOnFlextime != null && strOnFlextime.length() > 0)// 弹性延时班,是否用开始时间
            {
                Date TFlexTime = DateUtils.getDate(strOnFlextime, "HH:mm");
                Date DT2 = DateUtils.getDate(strDate + " " + strOnFlextime, "yyyy.MM.dd HH:mm");
                Date DTT2 = DateUtils.getDate(strDate + " " + strTTime, "yyyy.MM.dd HH:mm");
                if (TFlexTime.before(FTime))// 跨天
                {
                    DT2 = DateUtils.addDays(DT2, 1);
                }
                float time = KQRestOper.getPartMinute(DT2, start_date);// 如果开始时间在弹性开始时间以后或等于开始时间，那么就用弹性开始时间
                float time2 = KQRestOper.getPartMinute(start_date, DTT2);
                if (time >= 0 && time2 >= 0) {
                    FTime = DateUtils.getDate(strOnFlextime, "HH:mm");
                    strFTime = strOnFlextime;
                    isFlextime = true;
                }
            }
            if (strOffFlextime != null && strOffFlextime.length() > 0 && isFlextime)// 弹性延时班,是否用结束时间
            {
                strTTime = strOffFlextime;
                /*
                Date TFlexTime = DateUtils.getDate(strOffFlextime, "HH:mm");
                Date DT1 = DateUtils.getDate(strDate + " " + strTTime, "yyyy.MM.dd HH:mm");
                Date DT2 = DateUtils.getDate(strDate + " " + strOffFlextime, "yyyy.MM.dd HH:mm");
                if (TFlexTime.before(TTime))// 跨天
                {
                    DT2 = DateUtils.addDays(DT2, 1);
                }
                float time = KQRestOper.getPartMinute(DT1, end_date);
                if (time >= 0) {
                    TTime = DateUtils.getDate(strOffFlextime, "HH:mm");
                    strTTime = strOffFlextime;
                }
                */
            }
            if (perOffTime_str != null && perOffTime_str.length() > 0) {
                perOffTime = DateUtils.getDate(perOffTime_str, "HH:mm");
                time_f = KQRestOper.getPartMinute(perOffTime, FTime);
                if (time_f < 0) {
                    strDate = DateUtils.format(DateUtils.addDays(d_curDate, 1), "yyyy.MM.dd");
                }
            }
            perOffTime_str = strTTime;
            time_f = KQRestOper.getPartMinute(FTime, TTime);
            if (time_f > 0) {
                FDT = DateUtils.getDate(strDate + " " + strFTime + ":00", "yyyy.MM.dd HH:mm:ss");
                TDT = DateUtils.getDate(strDate + " " + strTTime + ":00", "yyyy.MM.dd HH:mm:ss");
            } else {
                FDT = DateUtils.getDate(strDate + " " + strFTime + ":00", "yyyy.MM.dd HH:mm:ss");
                strDate = DateUtils.format(DateUtils.addDays(d_curDate, 1), "yyyy.MM.dd");
                TDT = DateUtils.getDate(strDate + " " + strTTime + ":00", "yyyy.MM.dd HH:mm:ss");
            }
            
            time_sum = time_sum + KQRestOper.getPartMinute(FDT, TDT);
            
            if (FDT.getTime() <= end_date.getTime() && TDT.getTime() >= start_date.getTime())
			{
            	if (start_date.getTime() >= dayFrom.getTime() && end_date.getTime() <= dayTo.getTime()) {
                    timeLen = timeLen + calcTimSpan(FDT, TDT, start_date, end_date);
                } else if (start_date.getTime() <= dayFrom.getTime() && end_date.getTime() >=dayTo.getTime()) {
                    timeLen = timeLen + calcTimSpan(FDT, TDT, dayFrom, dayTo);
                } else if (start_date.getTime() <= dayFrom.getTime() && end_date.getTime() >=dayFrom.getTime() && end_date.getTime() <= dayTo.getTime()) {
                    timeLen = timeLen + calcTimSpan(FDT, TDT, dayFrom, end_date);
                } else if (start_date.getTime() <= dayTo.getTime() && start_date.getTime() >=dayFrom.getTime() && end_date.getTime() >=dayTo.getTime()) {
                    timeLen = timeLen + calcTimSpan(FDT, TDT, start_date, dayTo);
                }
            	//timeLen = timeLen + calcTimSpan(FDT, TDT, start_date, end_date);//计算申请时间段和班次时间段的交集
			}
            
            
        }
        //float totleTime = calcTimSpan(start_date, DateUtils.getDate(strDate + " 24:00:00", "yyyy.MM.dd HH:mm:ss"), start_date, end_date);
        //timeLen = totleTime - timeLen;
        Date yesterDate = new Date();
        if (!"".equals(yesterday)) 
		{
        	yesterDate = OperateDate.strToDate(yesterday, "yyyy.MM.dd HH:mm");
		}
        float totleTime = 0;
        /*
        if (m == 0) //申请的前一天
		{
			if (isOverDay && start_date.getTime() <= TDT.getTime()) //申请的前一天跨天并且班次的结束时间大于申请的开始时间
			{
		        totleTime = KQRestOper.getPartMinute(start_date, TDT);
			}
		}else if (m > 0 && m < num)//非第一天和最后一天 
		{
			if (m == 1) //申请的第一天
			{
				if (isOverDay) //班次跨天
				{
					//申请开始时间小于等于前一天的班次的结束时间
					if (!"".equals(yesterday) && start_date.getTime() <= yesterDate.getTime()) 
					{
						totleTime = KQRestOper.getPartMinute(yesterDate, TDT);
					}else 
					{
						totleTime = KQRestOper.getPartMinute(start_date, dayTo); //TDT
					}
				}else //班次不跨天
				{
					//申请开始时间小于等于前一天的班次的结束时间
					if (!"".equals(yesterday) && start_date.getTime() <= yesterDate.getTime()) 
					{
						totleTime = KQRestOper.getPartMinute(yesterDate, dayTo);
					}else 
					{
						totleTime = KQRestOper.getPartMinute(start_date, dayTo);
					}
				}
			}else 
			{
				if (isOverDay) //班次跨天
				{
					if (!"".equals(yesterday) && yesterDate.getTime() >= dayFrom.getTime())//前一天班次跨天 
					{
						totleTime = KQRestOper.getPartMinute(yesterDate, TDT);
					}else 
					{
						totleTime = KQRestOper.getPartMinute(dayFrom, TDT);
					}
				}else //班次不跨天
				{
					if (!"".equals(yesterday) && yesterDate.getTime() >= dayFrom.getTime())//前一天班次跨天 
					{
						totleTime = KQRestOper.getPartMinute(yesterDate, dayTo);
					}else 
					{
						totleTime = KQRestOper.getPartMinute(dayFrom, dayTo);
					}
				}
			}
		}else if (m == num) //申请的最后一天
		{
			if (!"".equals(yesterday) && yesterDate.getTime() >= dayFrom.getTime())//前一天班次跨天 
			{
				if (m == 1) 
				{
					//申请结束时间小于等于前一天的班次的结束时间
					if (start_date.getTime() <= yesterDate.getTime()) 
					{
						totleTime = KQRestOper.getPartMinute(yesterDate, end_date);
					}else 
					{
						totleTime = KQRestOper.getPartMinute(start_date, end_date);
					}
				}else 
				{
					//申请结束时间大于等于前一天的班次的结束时间
					if (end_date.getTime() >= yesterDate.getTime()) 
					{
						totleTime = KQRestOper.getPartMinute(yesterDate, end_date);
					}
				}
			}else 
			{
				if (m == 1) 
				{
					totleTime = KQRestOper.getPartMinute(start_date, end_date);
				}else 
				{
					totleTime = KQRestOper.getPartMinute(dayFrom, end_date);
				}
			}
		}
        */
        if (start_date.getTime() >= dayFrom.getTime() && end_date.getTime() <= dayTo.getTime()) {
            totleTime = KQRestOper.getPartMinute(start_date, end_date);
        } else if (start_date.getTime() <= dayFrom.getTime() && end_date.getTime() >=dayTo.getTime()) {
            totleTime = KQRestOper.getPartMinute(dayFrom, dayTo);
        } else if (start_date.getTime() <= dayFrom.getTime() && end_date.getTime() >=dayFrom.getTime() && end_date.getTime() <= dayTo.getTime()) {
            totleTime = KQRestOper.getPartMinute(dayFrom, end_date);
        } else if (start_date.getTime() <= dayTo.getTime() && start_date.getTime() >=dayFrom.getTime() && end_date.getTime() >=dayTo.getTime()) {
            totleTime = KQRestOper.getPartMinute(start_date, dayTo);
        }
        timeLen = totleTime - timeLen;
        
        HashMap hash = new HashMap();
        hash.put("time_sum", new Float(time_sum));
        hash.put("timeLen", new Float(timeLen));
        return hash;
    }
    /**
     * 判断一个结束时间是否在一个班次的时间段内
     * 
     * @param vo
     * @param d_curDate
     * @param end_date
     * @return
     */
    public boolean endTimeBH(RecordVo vo, Date d_curDate, Date end_date) {
        boolean isCorrect = true;
        String strFTime = "";// 班次开始时间
        String strTTime = "";// 班次结束时间
        Date FTime = null;
        Date TTime = null;
        Date TDT = null;
        String strDate = DateUtils.format(d_curDate, "yyyy.MM.dd");
        strFTime = vo.getString("onduty_1");
        if (strFTime == null || strFTime.length() <= 0) {
            return false;
        }
        FTime = DateUtils.getDate(strFTime, "HH:mm");
        for (int i = 3; i > 0; i--) {

            strTTime = vo.getString("offduty_" + (i));
            if (strTTime == null || strTTime.length() <= 0) {
                continue;
            } else {
                TTime = DateUtils.getDate(strTTime, "HH:mm");
                break;
            }
        }
        if (FTime == null || TTime == null) {
            return false;
        }
        float time_f = KQRestOper.getPartMinute(FTime, TTime);
        if (time_f > 0) {

            TDT = DateUtils.getDate(strDate + " " + strTTime, "yyyy.MM.dd HH:mm");
        } else {
            strDate = DateUtils.format(DateUtils.addDays(d_curDate, 1), "yyyy.MM.dd");
            TDT = DateUtils.getDate(strDate + " " + strTTime, "yyyy.MM.dd HH:mm");
        }
        time_f = KQRestOper.getPartMinute(end_date, TDT);
        if (time_f >= 0) {
            isCorrect = true;
        } else {
            isCorrect = false;
        }
        return isCorrect;
    }

    /**
     * 计算时间长度
     * 
     * @param FDT
     * @param TDT
     * @param s_app_date
     * @param e_app_date
     * @return
     */
    private float calcTimSpan(Date FDT, Date TDT, Date s_app_date, Date e_app_date) {
        float timeLen = 0;
        float time_1 = KQRestOper.getPartMinute(FDT, s_app_date);
        float time_2 = KQRestOper.getPartMinute(TDT, e_app_date);

        if (time_1 <= 0 && time_2 >= 0)// 完全包含在申请时间内
        {
            timeLen = KQRestOper.getPartMinute(FDT, TDT);
            return timeLen;
        } else if (time_1 >= 0 && time_2 <= 0) // 申请时间完全包含在工作时段内
        {
            timeLen = KQRestOper.getPartMinute(s_app_date, e_app_date);
            return timeLen;
        } else {
            float time_3 = KQRestOper.getPartMinute(FDT, e_app_date);
            if (time_1 <= 0 && time_3 > 0) // 只包含前一部分
            {
                timeLen = KQRestOper.getPartMinute(FDT, e_app_date);
                return timeLen;
            } else {
                float time_4 = KQRestOper.getPartMinute(TDT, s_app_date);// 只包含后一部分
                if (time_4 < 0 && time_2 >= 0) {
                    timeLen = KQRestOper.getPartMinute(s_app_date, TDT);
                    return timeLen;
                }
            }
        }
        return timeLen;
    }

    /**
     * 考勤规则的一个hashmap集
     * 此方法已废弃，请调用  KqItem.java文件中的getKqItem()方法
     * 
     * @return
     * @throws GeneralException
     */
    @Deprecated 
    public HashMap count_Leave(String item_id) throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,item_name,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";
        kq_item_sql = kq_item_sql + " where item_id='" + item_id + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap hashm_one = new HashMap();
        try {
            rs = dao.search(kq_item_sql);
            if (rs.next()) {

                hashm_one.put("fielditemid", rs.getString("fielditemid"));
                hashm_one.put("has_rest", PubFunc.DotstrNull(rs.getString("has_rest")));
                hashm_one.put("has_feast", PubFunc.DotstrNull(rs.getString("has_feast")));
                hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
                hashm_one.put("item_id", item_id);
                hashm_one.put("item_name", rs.getString("item_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return hashm_one;
    }

    /**
     * 得到可休时间
     * 
     * @param q1709
     * @param a0100
     * @param nbase
     * @param start
     * @param end
     * @return
     */
    public float getMy_Time(String q1709, String a0100, String nbase, String start, String end, String b0110, HashMap kqItem_hash)
            throws GeneralException {
        return this.getCanUseHoliday(q1709, a0100, nbase, start, end, b0110, kqItem_hash, "0");
    }
    
    
    private float getCanUseHoliday(String q1709, String a0100, String nbase, String start, String end, 
            String b0110, HashMap kqItem_hash, String flag) throws GeneralException {
        //切换成system参数中映射的假期类型（需求来源：汉口银行）
        q1709 = KqAppInterface.switchTypeIdFromHolidayMap(q1709);
        
        String column_z1 = "q17z1";
        String column_z3 = "q17z3";
        Date start_D;
        if (start.length() == 10 && end.length() == 10) {
            start_D = DateUtils.getDate(start, "yyyy.MM.dd");
        } else {
            start_D = DateUtils.getDate(start, "yyyy.MM.dd HH:mm");
        }
        String stD = DateUtils.format(DateUtils.getDate(start, "yyyy.MM.dd"), "yyyy.MM.dd");
        String edD = DateUtils.format(DateUtils.getDate(end, "yyyy.MM.dd"), "yyyy.MM.dd");
        StringBuffer sb = new StringBuffer();
        StringBuffer wb = new StringBuffer();
        String last_balance = KqUtilsClass.getFieldByDesc("q17", "上年结余");
        String last_balance_Time = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");
        String last_spare = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
        float time = 0;
        RowSet rs = null;
        float tale_off_time = 0;
        try {
            String q1701_1 = "";
            String q1701_2 = "";
            ContentDAO dao = new ContentDAO(this.conn);
            // 判断假期申请是否跨年
            sb.append("select * from Q17 where a0100='" + a0100 + "'");
            sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            sb.append(" and q1709='" + q1709 + "'");
            wb.append(sb);
            wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD));
            wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(stD));
            List listS = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);

            LazyDynaBean recS = null;
            LazyDynaBean recE = null;
            if (listS == null || listS.size() <= 0) {
                throwHolidyNotFoundException(stD.replace(".", "-"));
            }
                
            recS = (LazyDynaBean) listS.get(0);
            q1701_1 = (String) recS.get("q1701");
            
            //只取当年可休天数
            if ("1".equalsIgnoreCase(flag)) {
                return Float.parseFloat((String) recS.get("q1707"));
            } 
            
            //只取上年结余天数
            if ("2".equalsIgnoreCase(flag)) {
                if (last_spare != null && !"".equalsIgnoreCase(last_spare)) {
                    return Float.parseFloat((String) recS.get(last_spare));
                } else {
                    return 0;
                }
            }
            
            String holidayEndDate = (String)recS.get(column_z3);
            holidayEndDate = holidayEndDate.substring(0, 10).replace("-", ".");
            if (0 >= edD.compareTo(holidayEndDate)) {
                q1701_2 = q1701_1;
            } else {
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD));
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                List listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
                if (listE != null && listE.size() > 0) {
                    recE = (LazyDynaBean) listE.get(0);
                    q1701_2 = (String) recE.get("q1701");
                } else {
                    throwHolidyNotFoundException(edD);
                }
            }
            
            // 比较是否是同一年
            if (q1701_1.equals(q1701_2)) {
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD) + "");
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                rs = dao.search(wb.toString());
                if (rs.next()) {
                	// 已休天数
                    float td = rs.getFloat("q1705");
                    if (td<0 && this.getValidateUsedTimeLenError()) {
                        throw new GeneralException(rs.getString("A0101") + "-假期数据异常：" + q1701_1 + "年度已休天数为负数！请联系考勤管理员处理。");
                    }
                    
                    // 判断上年结余
                    if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                            && last_balance_Time.length() > 0) {
                        Date balance_D = rs.getTimestamp(last_balance_Time);
                        if (balance_D != null) {

                            balance_D = DateUtils.getDate(DateUtils.format(balance_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                            if (start_D.before(balance_D))// 开始时间在结余截止时间以前
                            {
                                float bf = rs.getFloat(last_spare);
                                int diffDay = DateUtils.dayDiff(start_D, balance_D);
                                float diff = 0;
                                if (diffDay < 20) {
                                    diff = getLeaveTime_2(start_D, balance_D, a0100, nbase, b0110, kqItem_hash, bf, null);
                                } else {
                                    diff = diffDay;
                                }
                                // float diff=DateUtils.dayDiff(, )+1;
                                if (bf > 0 && diff > 0) {
                                    if (diff - bf >= 0) {
                                        time = time + bf;
                                    } else {
                                        time = time + diff;
                                    }
                                }
                            }
                        }
                    }
                    if (time > 0) {
                        tale_off_time = time;
                    }
                    float q1707 = rs.getFloat("q1707");
                    time = time + q1707;
                }
            } else {
                float balanceF = 0;
                String q17z3_s = (String) recS.get("q17z3");// 第一年截止日期
                Date q17z3D_s = DateUtils.getDate(q17z3_s, "yyyy-MM-dd");
                q17z3D_s = DateUtils.getDate(DateUtils.format(q17z3D_s, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                // int
                // diffTopHalf=DateUtils.dayDiff(start_D,q17z3D_s)+1;//跨年时，得到开始时间到当前假期结束时间差了多少天
                time = 0;
                if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                        && last_balance_Time.length() > 0) {
                    String balance_S = (String) recS.get(last_balance_Time);// 第一年结余截止日期
                    if (balance_S != null && balance_S.length() > 0) {
                        Date balanceS_D = DateUtils.getDate(balance_S, "yyyy-MM-dd");
                        balanceS_D = DateUtils.getDate(DateUtils.format(balanceS_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                        if (start_D.before(balanceS_D))// 开始时间在结余截止时间以前
                        {
                            int diffDay = DateUtils.dayDiff(start_D, balanceS_D);
                            float diffFor_s_b = 0;
                            if (kqVer.getVersion() == KqConstant.Version.STANDARD && diffDay < 20) {
                                diffFor_s_b = getLeaveTime(start_D, balanceS_D, a0100, nbase, b0110, kqItem_hash);
                            } else {
                                diffFor_s_b = diffDay;
                            }
                            balanceF = Float.parseFloat((String) recS.get(last_spare));// 得到结余天数
                            if (diffFor_s_b > 0) {
                                if (balanceF > 0) {
                                    if (diffFor_s_b - balanceF >= 0) {
                                        time = time + balanceF;
                                    } else {
                                        time = time + diffFor_s_b;
                                    }
                                }
                            }
                        }
                    }
                }
                if (time > 0) {
                    tale_off_time = time;
                }
                float q1707_s = Float.parseFloat((String) recS.get("q1707"));
                int diffDay = DateUtils.dayDiff(start_D, q17z3D_s);
                float diffTopHalf = 0;
                if (kqVer.getVersion() == KqConstant.Version.STANDARD && diffDay < 20) {
                    diffTopHalf = getLeaveTime_2(start_D, q17z3D_s, a0100, nbase, b0110, kqItem_hash, q1707_s + tale_off_time,
                        null) - tale_off_time;
                } else {
                    diffTopHalf = diffDay;
                }
                
                if (diffTopHalf > 0) {
                    if (diffTopHalf <= q1707_s) {
                        time = time + diffTopHalf;
                    } else {
                        // time=time+q1707_s;
                        throw new GeneralException(q1701_1 + "年可休天数为" + q1707_s
                                + "天，您的当前申请超出了可休天数" + (diffTopHalf - q1707_s) + "天，请修改！");
                    }
                }
                float q1707_e = Float.parseFloat((String) recE.get("q1707"));
                time = time + q1707_e;
                if (diffTopHalf <= q1707_s) {
                    if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                            && last_balance_Time.length() > 0) {
                        String last_balance_E = (String) recE.get(last_balance);// 第一年结余截止日期
                        if (last_balance_E != null && last_balance_E.length() > 0) {
                            if (Float.parseFloat(last_balance_E) >= diffTopHalf) {
                                time = time + (Float.parseFloat(last_balance_E) - diffTopHalf);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return time;
    }
    
    /**
     * 取当年可休天数
     * @param q1709
     * @param a0100
     * @param nbase
     * @param start
     * @param end
     * @param b0110
     * @param kqItem_hash
     * @return
     * @throws GeneralException
     */
    public float getCurYearCanUseDays(String q1709, String a0100, String nbase, String start, String end, 
            String b0110, HashMap kqItem_hash) throws GeneralException {
        return this.getCanUseHoliday(q1709, a0100, nbase, start, end, b0110, kqItem_hash, "1");        
    }
    
    /**
     * 取上年结余剩余天数
     * @param q1709
     * @param a0100
     * @param nbase
     * @param start
     * @param end
     * @param b0110
     * @param kqItem_hash
     * @return
     * @throws GeneralException
     */
    public float getPreYearCanUseDays(String q1709, String a0100, String nbase, String start, String end, 
            String b0110, HashMap kqItem_hash) throws GeneralException {
        return this.getCanUseHoliday(q1709, a0100, nbase, start, end, b0110, kqItem_hash, "2");        
    }

    /**
     * 获得上年结余的字段名称
     * 
     * @return
     */
    public String getBalance() {
        // 获得年假结余的列名
        String balance = "";

        ArrayList fieldList = DataDictionary.getFieldList("q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem item = (FieldItem) fieldList.get(i);
            if ("上年结余".equalsIgnoreCase(item.getItemdesc())) {
                balance = item.getItemid();
            }
        }

        return balance;
    }

    /**
     * 得到假期管理的时间段
     * 
     * @param q1709
     * @param a0100
     * @param nbase
     * @param start
     * @param end
     * @return
     * @throws GeneralException
     */
    public HashMap getHols_Time(String q1709, String a0100, String nbase, String start, String end) throws GeneralException {
        //切换成system参数中映射的假期类型（需求来源：汉口银行）
        q1709 = KqAppInterface.switchTypeIdFromHolidayMap(q1709);
        
        String column_z1 = "q17z1";
        String column_z3 = "q17z3";
        start = start.replaceAll("-", ".");
        end = end.replaceAll("-", ".");
        Date start_D = DateUtils.getDate(start, "yyyy.MM.dd HH:mm");
        Date end_D = DateUtils.getDate(end, "yyyy.MM.dd HH:mm");
        String stD = DateUtils.format(start_D, "yyyy.MM.dd");
        String edD = DateUtils.format(end_D, "yyyy.MM.dd");

        StringBuffer sb = new StringBuffer();
        StringBuffer wb = new StringBuffer();
        sb.append("select * from Q17 where a0100='" + a0100 + "'");
        sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        sb.append(" and q1709='" + q1709 + "'");
        wb.append(sb);
        wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD) + "");
        wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(stD));
        List listS = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
        LazyDynaBean recS = null;
        LazyDynaBean recE = null;
        String q1701_1 = "";
        String q1701_2 = "";
        if (listS != null && listS.size() > 0) {
            recS = (LazyDynaBean) listS.get(0);
            q1701_1 = (String) recS.get("q1701");
        } else {
            throwHolidyNotFoundException(stD);
        }

        wb.setLength(0);
        wb.append(sb);
        wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD) + "");
        wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
        List listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
        if (listE != null && listE.size() > 0) {
            recE = (LazyDynaBean) listE.get(0);
            q1701_2 = (String) recE.get("q1701");
        } else {
            throwHolidyNotFoundException(edD);
        }

        /*zxj 20141220 取年假范围时需带上时分秒，否则后续使用中会出现请假刚好在最后一天的记录查不到的情况
         * 原因：2014-12-31>=2014-12-31 17:00:00不成立
         */
        HashMap map = new HashMap();
        map.put("q17z1", DateUtils.getDate(((String) recS.get(column_z1)).substring(0, 11) + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        if (q1701_1.equals(q1701_2)) {
            map.put("q17z3", DateUtils.getDate(((String) recS.get(column_z3)).substring(0, 11) + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
        } else {            
            map.put("q17z3", DateUtils.getDate(((String) recE.get(column_z3)).substring(0, 11) + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
        }
        return map;
    }

    /**
     * 加班申请Q11
     * 
     * @param vo
     */
    public String overTimeApp(String oper, RecordVo vo, String sels, Date kq_start, Date kq_end, boolean isCorrect, String sp)
            throws GeneralException {
        ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);
        String return_id = "";
        String app_class_id = vo.getString("q1104");
        if ("2".equalsIgnoreCase(sp))// 驳回
        {
            return_id = saveApp(oper, "q11", "q11", vo, isCorrect);
            return return_id;
        }

        if (validateAppOper.is_OVERTIME_TYPE()) {
            if (sels.startsWith("10")) {
                // 判断是否是公休日
                if (!validateAppOper.is_Rest(kq_start, kq_end, vo.getString("a0100"), vo.getString("nbase"), app_class_id)) {
                    throw new GeneralException(vo.getString("a0101") + "," + validateAppOper.getNo_Rest_mess());
                }

                if (validateAppOper.is_Feast(kq_start, kq_end, vo.getString("b0110"))) {
                    throw new GeneralException( vo.getString("a0101")
							+ ResourceFactory.getProperty("error.kq.nofeast"));
                }
            }

            /** 判断是否是节假日 */
            if (sels.startsWith("11")) {
                if (!validateAppOper.is_Feast(kq_start, kq_end, vo.getString("b0110"), app_class_id)) {
                    throw new GeneralException(ResourceFactory.getProperty("error.kq.nfeast"));
                }
            }

            if (sels.startsWith("12")) {
                // 平时加班
                if (!validateAppOper.if_Peacetime(kq_start, kq_end, vo.getString("nbase"), vo.getString("a0100"))) {
                    throw new GeneralException(vo.getString("a0101") + "，" + validateAppOper.getRest_Peacetime_mess());
                }
            }
        }
        
        String overtime_rule = KqParam.getInstance().getOvertimeRule(this.conn, vo.getString("b0110"));
        String overtime_rule_status = KqParam.getInstance().getOvertimeRuleStatus(this.conn, userView);
        if (!"1".equals(overtime_rule_status) || overtime_rule == null || overtime_rule.length() <= 0) {
            overtime_rule = "0";
        }
        
        int num = Integer.parseInt(overtime_rule);
        if (0 == num) {
            return_id = saveApp(oper, "q11", "q11", vo, isCorrect);
            return return_id;
        }
        
        Date app_time = vo.getDate("q1105");
        Date start_time = vo.getDate("q11z1");
        float days = KQRestOper.getPartMinute(start_time, app_time) / 60.0f /24;
        int redays = validateAppOper.is_RestDays(start_time, app_time, vo.getString("a0100"), vo.getString("nbase"));

        num = num + redays;// 加班最迟登记天数去除了休息日
        if (num < days) {
            String message = getOvertimeRuleHintInfo(num);
            isCorrect = false;
            throw new GeneralException(message);
        }
        return_id = saveApp(oper, "q11", "q11", vo, isCorrect);

        return return_id;
    }

    /**
     * 公出申请Q13
     * 
     * @param vo
     * @param sels
     * @param kq_start
     * @param kq_end
     * @param isCorrect
     */
    public String awayTimeApp(String oper, RecordVo vo, String sels, Date kq_start, Date kq_end, boolean isCorrect, String sp)
            throws GeneralException {
        return saveApp(oper, "q13", "q13", vo, isCorrect);
    }

    /**
     * 请假申请
     * 
     * @param vo
     * @param sels
     * @param kq_start
     * @param kq_end
     * @param isCorrect
     */
    public String leaveTimeApp(String oper, RecordVo vo, String sels, Date kq_start, Date kq_end, boolean isCorrect, String sp)
            throws GeneralException {
        String return_id = "";
        ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);
        try {
            if ("2".equalsIgnoreCase(sp)) {
                return_id = saveApp(oper, "q15", "q15", vo, isCorrect);
            } else {
                String leavetime_rule = KqParam.getInstance().getLeavetimeRule(this.conn, vo.getString("b0110")); 
                if (leavetime_rule != null && leavetime_rule.length() > 0) {
                    long num = Integer.parseInt(leavetime_rule);
                    Date app_time = vo.getDate("q1505");
                    Date start_time = vo.getDate("q15z1");
                    /** 起始时间不能大于申请时间 */
                    /*
                     * float days=KQRestOper.getPartMinute(app_time,start_time);
                     * days=days/60; days=days/24;
                     */
                    int redays = validateAppOper.is_RestDays(app_time, start_time, vo.getString("a0100"), vo.getString("nbase"));
                    float days = DateUtils.dayDiff(app_time, start_time);
                    days += 1;
                    num = num + redays;
                    if (num > days) {
                        StringBuffer message = new StringBuffer();
                        message.append(ResourceFactory.getProperty("kq.Leavetime_rule.excess"));
                        message.append(" " + leavetime_rule + " ");
                        message.append(ResourceFactory.getProperty("kq.Overtime_rule.day"));
                        isCorrect = false;
                        throw new GeneralException("", message.toString(), "", "");
                    }
                } else {
                    String late_leavetime_rule = KqParam.getInstance().getLateLeavetimeRule(this.conn, vo.getString("b0110"));
                    if (late_leavetime_rule != null && late_leavetime_rule.length() > 0) {
                        long num = Integer.parseInt(late_leavetime_rule);
                        Date app_time = vo.getDate("q1505");
                        Date start_time = vo.getDate("q15z1");
                        float days = DateUtils.dayDiff(start_time, app_time);
                        int redays = validateAppOper.is_RestDays(start_time, app_time, vo.getString("a0100"), 
                                vo.getString("nbase"));
                        num = num + redays;
                        days += 1;
                        if (num < days) {
                            StringBuffer message = new StringBuffer();
                            message.append(ResourceFactory.getProperty("kq.Late_Leavetime_rule.excess"));
                            message.append(" " + late_leavetime_rule + " ");
                            message.append(ResourceFactory.getProperty("kq.Overtime_rule.day"));
                            isCorrect = false;
                            throw new GeneralException("", message.toString(), "", "");
                        }
                    }
                }
                
                if (KqParam.getInstance().isHoliday(this.conn, vo.getString("b0110"), sels)) {
                    //检查年假是否超额
                    String checkMsg = validateAppOper.checkHoliday(kq_start, kq_end, vo, sels, oper);
                    
                    if (!"".equals(checkMsg)) {
                        checkMsg = checkMsg + "<br>" + PubFunc.nullToStr(this.getAppLeavedMess());
                        throw new GeneralException("", checkMsg, "", "");
                    } else {
                        //如果不超额，并且是批准操作，那么进行年假的扣减
                        if ("1".equals(sp)) {
                            String z0 = PubFunc.nullToStr(vo.getString("q15z0"));
                            String z5 = PubFunc.nullToStr(vo.getString("q15z5"));
                            String history = "";
                            if ("01".equals(z0) && "03".equals(z5)) {
                                HashMap kqItem_hash = count_Leave(sels);
                                float[] holiday_rules = getHoliday_minus_rule();// 年假假期规则
                                float leave_time = getHistoryLeaveTime(kq_start, kq_end, 
                                        vo.getString("a0100"), vo.getString("nbase"), 
                                        vo.getString("b0110"), kqItem_hash, holiday_rules);
                                
                                String start = DateUtils.format(kq_start, "yyyy.MM.dd HH:mm:ss");
                                String end = DateUtils.format(kq_end, "yyyy.MM.dd HH:mm:ss");
                                
                                history = upLeaveManage(vo.getString("a0100"), vo.getString("nbase"), sels, start, end,
                                        leave_time, sp, vo.getString("b0110"), kqItem_hash, holiday_rules);
                            }
                            vo.setString("history", history);
                        }
                        return_id = saveApp(oper, "q15", "q15", vo, isCorrect);
                    }
                } else {
                    /** 如果请调休假 检查调休假可用时长是否够用*/
                    int hr_count = 0;
                    GetValiateEndDate ve = new GetValiateEndDate(this.userView, this.conn);
                    String leavetime_type_used_overtime = KqParam.getInstance().getLeaveTimeTypeUsedOverTime();
                    Map infoMap = ve.getInfoMap(vo.getString("nbase"), vo.getString("a0100"));
                    String error = "";
                    
                    //考勤规则应取改假类自己的规则
                    HashMap kqItemHash = count_Leave(sels);
                    kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
                    //假期时长扣减规则参数
                    float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
                    if (KqParam.getInstance().isHoliday(this.conn, vo.getString("b0110"), sels)) {
                        holidayRules = getHoliday_minus_rule();
                    }
                    
                    float timeLen = calcLeaveAppTimeLen(vo.getString("nbase"), vo.getString("a0100"), "", vo.getDate("q15z1"), vo.getDate("q15z3"), kqItemHash, holidayRules, Integer.MAX_VALUE);
                    hr_count = (hr_count + (int) (timeLen * 60));

                    if (sels.equalsIgnoreCase(leavetime_type_used_overtime)) {
                        error = ve.checkUsableTime(kq_start, infoMap, vo.getString("q1503"), vo.getString("nbase"), "", String
                                .valueOf(hr_count));
                    }
                    if (error.length() > 0) {
                        throw GeneralExceptionHandler.Handle(new GeneralException(error));
                    }

                    return_id = saveApp(oper, "q15", "q15", vo, isCorrect);

                    /** 成功批准申请并保存 更新调休加班明细表 */
                    if (return_id.length() > 0 && "01".equals(vo.getString("q15z0")) && "03".equals(vo.getString("q15z5"))) {
                        if (sels.equalsIgnoreCase(leavetime_type_used_overtime)) {
                            //zxj 四舍五入后取整
                            int timeCount = new BigDecimal(timeLen * 60).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                            if(timeCount > 0) {
                            	UpdateQ33 updateq33 = new UpdateQ33(this.userView, this.conn);
                            	// 48612
                            	updateq33.setStartDate(vo.getDate("q15z1"));
                            	updateq33.upQ33(vo.getString("nbase"), vo.getString("a0100"), timeCount);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
        return return_id;

    }

    /**
     * 考勤年度内未批准的请假天数
     * @Title: othenSealTime   
     * @Description:    
     * @param sela
     * @param start_date
     * @param end_date
     * @param a0100
     * @param nbase
     * @param b0110
     * @param q1501
     * @param kqItem_hash
     * @param oper
     * @param q1519
     * @return
     * @throws GeneralException
     */
    public float othenSealTime(String sela, Date start_date, Date end_date, String a0100, String nbase, String b0110,
            String q1501, HashMap kqItem_hash, String oper, String q1519) throws GeneralException {
        float time = 0;
        
        HashMap hash = getHols_Time(sela, a0100, nbase, 
                                    DateUtils.format(start_date, "yyyy-MM-dd HH:mm:ss"), 
                                    DateUtils.format(end_date, "yyyy-MM-dd HH:mm:ss"));
        Date q17z1 = (Date) hash.get("q17z1");
        Date q17z3 = (Date) hash.get("q17z3");
        if (q17z1 == null || q17z3 == null) {
            return 0;
        }
        
        StringBuffer sql = new StringBuffer();
        String column_z1 = "q15z1";
        String column_z3 = "q15z3";
        sql.append("select " + column_z1 + "," + column_z3 + " from q15 where ");
        sql.append("a0100='" + a0100 + "'");
        sql.append(" and " + column_z1 + ">=" + Sql_switcher.dateValue(DateUtils.format(q17z1, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(DateUtils.format(q17z3, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        //sql.append(" and q15z0='01'");
        sql.append(" and q15z5 in ('02','08')");
        sql.append(" and q1503='" + sela + "'");
        if (q1501 != null && q1501.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1501 + "'");
        }
        
        if (q1519 != null && q1519.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1519 + "'");
        }
        
        sql.append(" and (q1519 is null" );
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            sql.append(" OR q1519=''");
        }
        sql.append(")");

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        StringBuffer message = new StringBuffer();
        try {
            rs = dao.search(sql.toString());
            Date s_date = null;
            Date e_date = null;
            float leave_time = 0;
            while (rs.next()) {
                s_date = rs.getTimestamp(column_z1);
                e_date = rs.getTimestamp(column_z3);
                leave_time = getLeaveTime(s_date, e_date, a0100, nbase, b0110, kqItem_hash);
                if (leave_time > 0) {
                    message.append(s_date + "---" + e_date + "<br>");
                }
                time = time + leave_time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        this.setAppLeavedMess(message.toString());
        return time;
    }

    /**
     * 带假期规则的
     * 
     * @param sela
     * @param start_date
     * @param end_date
     * @param a0100
     * @param nbase
     * @param b0110
     * @param q1501
     * @param kqItem_hash
     * @param oper
     * @param q1519
     * @param history_rule
     * @return
     * @throws GeneralException
     */
    public float othenSealTime(String sela, Date start_date, Date end_date, String a0100, String nbase, String b0110,
            String q1501, HashMap kqItem_hash, String oper, String q1519, float history_rule[]) throws GeneralException {
        float time = 0;
        HashMap hash = getHols_Time(sela, a0100, nbase, 
                                    DateUtils.format(start_date, "yyyy-MM-dd HH:mm:ss"), 
                                    DateUtils.format(end_date, "yyyy-MM-dd HH:mm:ss"));
        Date q17z1 = (Date) hash.get("q17z1");
        Date q17z3 = (Date) hash.get("q17z3");
        if (q17z1 == null || q17z3 == null) {
            return 0;
        }
        StringBuffer sql = new StringBuffer();
        String column_z1 = "q15z1";
        String column_z3 = "q15z3";
        sql.append("select " + column_z1 + "," + column_z3 + " from q15 where ");
        sql.append("a0100='" + a0100 + "'");
        sql.append(" and " + column_z1 + ">=" + Sql_switcher.dateValue(DateUtils.format(q17z1, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(DateUtils.format(q17z3, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        //sql.append(" and q15z0='01'");
        sql.append(" and q15z5 in ('02','08')");//zxj 2014.07.18 查待批和待审的即可，其它状态的不考虑
        sql.append(" and q1503 IN (" + KqAppInterface.getMapTypeIdsFromHolidayMap(sela) + ")");
            
        if (q1501 != null && q1501.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1501 + "'");
        }
        if (q1519 != null && q1519.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1519 + "'");
        }
        
        sql.append(" and (q1519 is null" );
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            sql.append(" OR q1519=''");
        }
        sql.append(")");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        StringBuffer message = new StringBuffer();
        try {
            rs = dao.search(sql.toString());
            Date s_date = null;
            Date e_date = null;
            float leave_time = 0;
            while (rs.next()) {
                s_date = rs.getTimestamp(column_z1);
                e_date = rs.getTimestamp(column_z3);
                leave_time = getHistoryLeaveTime(s_date, e_date, a0100, nbase, b0110, kqItem_hash, history_rule);
                if (leave_time > 0) {
                    message.append(s_date + "---" + e_date + "<br>");
                }
                time = time + leave_time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        this.setAppLeavedMess(message.toString());
        return time;
    }
    
    
    public float getHolidaySealTime(String sela, Date start_date, Date end_date, String a0100, String nbase, String b0110,
            String q1501, HashMap kqItem_hash, String oper, String q1519, float history_rule[], String flag) throws GeneralException {
        float time = 0;
        
        if (this.kqVer.getVersion() != KqConstant.Version.STANDARD) {
            return 0;
        }
        
        HashMap hash = getHols_Time(sela, a0100, nbase, 
                                    DateUtils.format(start_date, "yyyy-MM-dd HH:mm:ss"), 
                                    DateUtils.format(end_date, "yyyy-MM-dd HH:mm:ss"));
        Date q17z1 = (Date) hash.get("q17z1");
        Date q17z3 = (Date) hash.get("q17z3");
        if (q17z1 == null || q17z3 == null) {
            return 0;
        }
        
        StringBuffer sql = new StringBuffer();
        String column_z1 = "q15z1";
        String column_z3 = "q15z3";
        sql.append("select " + column_z1 + "," + column_z3 + " from q15 where ");
        sql.append("a0100='" + a0100 + "'");
        sql.append(" and " + column_z1 + ">=" + Sql_switcher.dateValue(DateUtils.format(q17z1, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(DateUtils.format(q17z3, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        //sql.append(" and q15z0='01'");
        sql.append(" and q15z5 in ('02','08')");//zxj 2014.07.18 查待批和待审的即可，其它状态的不考虑
        sql.append(" and q1503 IN (" + KqAppInterface.getMapTypeIdsFromHolidayMap(sela) + ")");
            
        if (q1501 != null && q1501.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1501 + "'");
        }
        if (q1519 != null && q1519.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1519 + "'");
        }
        
        sql.append(" and (q1519 is null" );
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            sql.append(" OR q1519=''");
        }
        sql.append(")");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        StringBuffer message = new StringBuffer();
        try {
            rs = dao.search(sql.toString());
            Date s_date = null;
            Date e_date = null;
            float leave_time = 0;
            while (rs.next()) {
                s_date = rs.getTimestamp(column_z1);
                e_date = rs.getTimestamp(column_z3);
                if ("1".equals(flag)) {
                    leave_time = this.getCurYearHolidayDays(sela, s_date, e_date, a0100, nbase, b0110, kqItem_hash, history_rule);
                } else if ("2".equals(flag)) {
                    leave_time = this.getPreYearHolidayDays(sela, s_date, e_date, a0100, nbase, b0110, kqItem_hash, history_rule);
                }
                time = time + leave_time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        this.setAppLeavedMess(message.toString());
        return time;
    }
    
    private float getCurYearHolidayDays(String sela, Date start_date, Date end_date, String a0100, String nbase, String b0110,
            HashMap kqItem_hash, float history_rule[]) throws GeneralException {
        float leave_time = this.getHistoryLeaveTime(start_date,end_date,a0100,nbase,b0110,kqItem_hash,history_rule);
        String daysHistory = this.getLeaveManage(a0100, nbase, sela, 
                DateUtils.format(start_date, "yyyy.MM.dd HH:mm"), DateUtils.format(end_date, "yyyy.MM.dd HH:mm"), 
                leave_time, "", b0110, kqItem_hash, history_rule);
        
        String[] days = daysHistory.split(";");
        if (days.length <= 0) {
            return 0;
        }
        
        String[] curYearDays = days[0].split(",");
        if (curYearDays.length <=0) {
            return 0;
        }
        
        if (curYearDays[0].length()<=0) {
            return 0;
        }
        
        return Float.valueOf(curYearDays[0]);
    }
    
    private float getPreYearHolidayDays(String sela, Date start_date, Date end_date, String a0100, String nbase, String b0110,
            HashMap kqItem_hash, float history_rule[]) throws GeneralException {
        float leave_time = this.getHistoryLeaveTime(start_date,end_date,a0100,nbase,b0110,kqItem_hash,history_rule);
        String daysHistory = this.getLeaveManage(a0100, nbase, sela, 
                DateUtils.format(start_date, "yyyy.MM.dd HH:mm"), DateUtils.format(end_date, "yyyy.MM.dd HH:mm"), 
                leave_time, "", b0110, kqItem_hash, history_rule);
        
        String[] days = daysHistory.split(";");
        if (days.length <= 0) {
            return 0;
        }
        
        String[] curYearDays = days[0].split(",");
        if (curYearDays.length <=1) {
            return 0;
        }
        
        if (curYearDays[1].length()<=0) {
            return 0;
        }
        
        return Float.valueOf(curYearDays[1]);
    }
    /**
     * 计算在途的调休假时长
     * @Title: calcAppendingLeaveUsedOverTimeLen   
     * @Description:    
     * @param leaveTypeId
     * @param a0100
     * @param nbase
     * @param b0110
     * @param q1501
     * @param kqItem_hash
     * @param oper
     * @param q1519
     * @param startDate		开始时间
     * @return 时长 （考勤规则中定义的时长单位）
     * @throws GeneralException
     */
    public float calcAppendingLeaveUsedOverTimeLen(String leaveTypeId, String a0100, String nbase, String b0110,
            String q1501, HashMap kqItem_hash, String oper, String q1519, Date startDate) throws GeneralException {
        float time = 0;

        //String usable = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();
        //if("".equals(usable))
        //    return 0;
        
        //Date toDate = new Date();//结束时间改为当前系统时间
        //int validityTime = Integer.parseInt(usable);
        //Date fromDate = OperateDate.addDay(toDate, 0-validityTime);
        
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(this.conn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod(startDate);
        String strFrom = (String)period.get("from");
        String strTo = (String)period.get("to");
        Date fromDate = DateUtils.getDate(strFrom, "yyyy-MM-dd");
        Date toDate = DateUtils.getDate(strTo, "yyyy-MM-dd");
        
        if (fromDate == null || toDate == null) {
            return 0;
        }
        
        StringBuffer sql = new StringBuffer();
        String column_z1 = "q15z1";
        String column_z3 = "q15z3";
        sql.append("select " + column_z1 + "," + column_z3);
        sql.append(" from q15");
        sql.append(" where a0100='" + a0100 + "'");
        sql.append(" and " + column_z1 + ">=" + Sql_switcher.dateValue(DateUtils.format(fromDate, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(DateUtils.format(toDate, "yyyy-MM-dd 23:59:59")));
        sql.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        //sql.append(" and q15z0='01'");
        sql.append(" and q15z5 in ('02','08')");
        sql.append(" and q1503 IN ('" + leaveTypeId + "')");
        if (q1501 != null && q1501.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1501 + "'");
        }
        
        if (q1519 != null && q1519.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q1501<>'" + q1519 + "'");
        }
        
        sql.append(" and (q1519 is null" );
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            sql.append(" OR q1519=''");
        }
        sql.append(")");

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        StringBuffer message = new StringBuffer();
        try {
            rs = dao.search(sql.toString());
            Date s_date = null;
            Date e_date = null;
            float leave_time = 0;
            while (rs.next()) {
                s_date = rs.getTimestamp(column_z1);
                e_date = rs.getTimestamp(column_z3);
                leave_time = calcLeaveAppTimeLen(nbase, a0100, b0110, s_date, e_date,kqItem_hash, null, Integer.MAX_VALUE);
                if (leave_time > 0) {
                    message.append(s_date + "---" + e_date + "<br>");
                }
                time = time + leave_time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        this.setAppLeavedMess(message.toString());
        return time;
    }

    /**
     * 保存申请
     * 
     * @param table
     * @param ta
     * @param vo
     */
    public String saveApp(String oper, String table, String ta, RecordVo vo, boolean isCorrect) throws GeneralException {
        String id = "";
        if (isCorrect) {
            ContentDAO dao = new ContentDAO(this.conn);
            try {
                if (table != null && "q15".equalsIgnoreCase(table)) {
                    vo.setString("q1517", "0");
                }
                if ("add".equalsIgnoreCase(oper)) {
                    /*
                     * IDGenerator idg=new IDGenerator(2,this.conn); String
                     * insertid=idg.getId((table+"."+ta+"01").toUpperCase());
                     */
                    id = checkAppkeyid(table, ta);
                    vo.setString(ta + "01", id);
                    // System.out.println(insertid);
                    dao.addValueObject(vo);
                } else if ("up".equalsIgnoreCase(oper)) {
                    dao.updateValueObject(vo);
                    id = vo.getString(table + "01");
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
        }
        return id;
    }

    /**
     * 更改假期管理
     * 
     * @param a0100
     * @param nbase
     * @param q1709
     * @param start
     * @param end
     * @param leave_time
     * @param sp
     * @return
     * @throws GeneralException 
     */
    public String upLeaveManage(String a0100, String nbase, String q1709, String start, String end, float leave_time, String sp,
            String b0110, HashMap kqItem_hash, float[] holiday_rules) throws GeneralException {
        q1709 = KqAppInterface.switchTypeIdFromHolidayMap(q1709);
        if (!KqParam.getInstance().isHoliday(conn, b0110, q1709)) {
            return "";
        }
        
        String column_z1 = "q17z1";
        String column_z3 = "q17z3";
        start = start.replaceAll("-", ".");
        end = end.replaceAll("-", ".");
        Date start_D = DateUtils.getDate(start, "yyyy.MM.dd HH:mm");
        Date end_D = DateUtils.getDate(end, "yyyy.MM.dd HH:mm");
        String stD = DateUtils.format(start_D, "yyyy.MM.dd");
        String edD = DateUtils.format(end_D, "yyyy.MM.dd");
        StringBuffer sb = new StringBuffer();
        StringBuffer wb = new StringBuffer();
        String last_balance = KqUtilsClass.getFieldByDesc("q17", "上年结余");
        String last_balance_Time = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");
        String last_spare = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
        float time = 0;
        RowSet rs = null;
        StringBuffer upl = new StringBuffer();
        float[] array = new float[4];
        try {
            String q1701_1 = "";
            String q1701_2 = "";
            ContentDAO dao = new ContentDAO(this.conn);
            // 判断假期申请是否跨年
            sb.append("select * from Q17 where a0100='" + a0100 + "'");
            sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            sb.append(" and q1709='" + q1709 + "'");
            wb.append(sb);
            wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD));
            wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(stD));
            List listS = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);

            LazyDynaBean recS = null;
            LazyDynaBean recE = null;
            if (listS != null && listS.size() > 0) {
                recS = (LazyDynaBean) listS.get(0);
                q1701_1 = (String) recS.get("q1701");
            } else {
                throwHolidyNotFoundException(stD);
            }

            wb.setLength(0);
            wb.append(sb);
            wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD));
            wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
            List listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
            if (listE != null && listE.size() > 0) {
                recE = (LazyDynaBean) listE.get(0);
                q1701_2 = (String) recE.get("q1701");
            } else {
                throwHolidyNotFoundException(edD);
            }
            // 比较是否是同一年
            ArrayList uplist = new ArrayList();
            if (q1701_1.equals(q1701_2)) {
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD) + "");
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                rs = dao.search(wb.toString());
                float sf = 0;
                if (rs.next()) {
                    String q1701 = rs.getString("q1701");
                    // 已休天数
                    float td = rs.getFloat("q1705");
                    if (td<0) {
                        throw new GeneralException(rs.getString("A0101") + "-假期数据异常：" + q1701 + "年度已休天数为负数！请联系考勤管理员处理。");
                    }
                    
                    // 可休天数
                    float dd = rs.getFloat("q1707");
                    // 判断上年结余
                    if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                            && last_balance_Time.length() > 0) {
                        Date balance_D = rs.getTimestamp(last_balance_Time); // 结余截止日期
                        if (balance_D != null) {
                            balance_D = DateUtils.getDate(DateUtils.format(balance_D, "yyyy.MM.dd") + " 23:59",
                                    "yyyy.MM.dd HH:mm");
                            // float diff=DateUtils.dayDiff(start_D,
                            // balance_D)+1;
                            if (start_D.before(balance_D))// 开始时间在结余截止时间以前
                            {
                                float diff = 0;
                                if (end_D.before(balance_D)) {
                                    diff = getHistoryLeaveTime(start_D, end_D, a0100, nbase, b0110, kqItem_hash, holiday_rules);
                                } else {
                                    diff = getHistoryLeaveTime(start_D, balance_D, a0100, nbase, b0110, kqItem_hash,
                                            holiday_rules);
                                }

                                float last_sf = rs.getFloat(last_spare);// 结余剩余
                                if (diff > 0 && last_sf > 0) {// 在结余截止日期范围内并且结余天数大于0
                                    if (diff - last_sf >= 0)// 判断
                                    {
                                        time = last_sf;
                                    } else {
                                        time = diff;
                                    }

                                }
                                if (leave_time > time) {
                                    leave_time = leave_time - time;
                                    sf = last_sf - time > 0 ? last_sf - time : 0;
                                    array[1] = time;
                                } else {
                                    sf = last_sf - leave_time > 0 ? last_sf - leave_time : 0;
                                    array[1] = leave_time;
                                    leave_time = 0;
                                }
                                if (time > 0) {
                                    upl.setLength(0);
                                    upl.append("update Q17 set " + last_spare + "=");
                                    upl.append(sf);
                                    // 不再更新上年结余字段
                                    upl.append(" where a0100='");
                                    upl.append(a0100);
                                    upl.append("'");
                                    upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                                    upl.append(" and q1709='" + q1709 + "'");
                                    upl.append(" and q1701='" + q1701 + "'");
                                    uplist.add(upl.toString());
                                }
                            }
                        }

                    }
                    if (leave_time > 0) {
                        /*
                         * if(time>0){ float mm=(time*24*60); String
                         * ms=PubFunc.DoFormatDecimal(mm+"",0);
                         * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms)); }
                         */
                        float diffLastHalf = getLeaveTime_2(start_D, end_D, a0100, nbase, b0110, kqItem_hash, leave_time + time,
                                holiday_rules);// 剩下的时间
                        
                        //zxj jazz 32136浮点数判断改进，避免出现1.999999<2.0的情况
                        if ((diffLastHalf - time - leave_time) >= -0.001) {
                            int currYear = Integer.parseInt(q1701);
                            String nextYear = String.valueOf(currYear + 1);
                            if (last_balance != null && last_balance.length() > 0) {
                                String sql = "select * from q17 where q1701='" + nextYear + "' and a0100='" + a0100
                                        + "' and nbase='" + nbase + "' and q1709='" + q1709 + "'";
                                List list_bean = ExecuteSQL.executeMyQuery(sql, this.conn);
                                if (!list_bean.isEmpty() && list_bean.size() > 0) {
                                    LazyDynaBean bean = (LazyDynaBean) list_bean.get(0);
                                    String l_b_s = (String) bean.get(last_balance);
                                    String l_s_s = (String) bean.get(last_spare);
                                    if (l_b_s != null && l_b_s.length() > 0 && l_s_s != null && l_s_s.length() > 0) {
                                        float l_b_f = Float.parseFloat(l_b_s);// 下一年结余天
                                        float l_s_f = Float.parseFloat(l_s_s);// 下一年结余剩余天
                                        if (l_b_f > l_s_f && leave_time > l_s_f) {
                                            throw new GeneralException("当前申请额度已在" + nextYear
                                                    + "年中作为上年结余使用了，不能申请该记录！");
                                        }
                                    }
                                }
                            }

                            if (sp != null && !"5".equals(sp)) {
                                if (td < 0 || dd <= 0) {
                                    return array[0] + "," + array[1];
                                }
                            }
                            float value1 = td + leave_time;
                            float value2 = dd - leave_time;
                            //szk查出姓名
                            RecordVo vo = new RecordVo(nbase+"A01");
                            vo.setString("a0100", a0100);
                            vo = dao.findByPrimaryKey(vo);
                            if(value2 < 0) {
                                throw new GeneralException(vo.getString("a0101")+"的可休天数不足，请调整申请时间！本次申请天数为" + leave_time + "天， 当前剩余可休天数为" + dd + "天！");
                            }
                            upl.setLength(0);
                            upl.append("update Q17 set q1705=");
                            upl.append(value1);
                            upl.append(", q1707=");
                            upl.append(value2);
                            // 不再更新上年结余字段
                            upl.append(" where a0100='");
                            upl.append(a0100);
                            upl.append("'");
                            upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                            upl.append(" and q1709='" + q1709 + "'");
                            upl.append(" and q1701='" + q1701 + "'");
                            // dao.update(upl.toString());
                            uplist.add(upl.toString());
                            array[0] = leave_time;

                            if (last_balance != null && last_balance.length() > 0) {
                                String sql = "select " + last_balance + " from q17 where q1701='" + nextYear + "' and a0100='"
                                        + a0100 + "' and nbase='" + nbase + "' and q1709='" + q1709 + "'";// 重新计算第二年上年结余
                                float last_balance_e = 0;
                                rs = dao.search(sql);
                                if (rs.next()) {
                                    last_balance_e = rs.getFloat(last_balance);
                                }
                                if (last_balance_e > 0) {
                                    // 上年剩余
                                    upl.setLength(0);
                                    upl.append("update q17 set " + last_balance);
                                    upl.append("=(select ");
                                    upl.append("  q1707 from q17 m where ");
                                    upl.append("m.nbase='" + nbase + "' and m.a0100=q17.a0100 and m.q1701='");
                                    upl.append(q1701);
                                    upl.append("' and m.q1709='");
                                    upl.append(q1709);
                                    upl.append("') where nbase='" + nbase + "' ");
                                    upl.append(" and q1701='" + nextYear + "'");
                                    upl.append(" and q1709='" + q1709 + "'");
                                    upl.append(" and a0100='" + a0100 + "'");
                                    // dao.update(upl.toString());
                                    uplist.add(upl.toString());
                                    upl.setLength(0);
                                    // 结余
                                    upl.append("update q17 set " + last_spare);
                                    upl.append("=" + last_spare + "-" + leave_time);
                                    upl.append(" where nbase='" + nbase + "' ");
                                    upl.append(" and q1701='" + nextYear + "'");
                                    upl.append(" and q1709='" + q1709 + "'");
                                    upl.append(" and a0100='" + a0100 + "'");
                                    // dao.update(upl.toString());
                                    uplist.add(upl.toString());
                                }
                            }
                        } else {
                            throw new GeneralException("假期申请数据保存错误，请检查申请时间时长是否符合管理时长！");
                        }
                    }
                    // String mess=array[0]+","+array[1];
                    // System.out.println(array[0]+","+array[1]+";"+array[2]+","+array[3]);
                    // return mess;
                }
            } else {

                String q17z3_s = (String) recS.get("q17z3");// 第一年截止日期
                String q1701_s = (String) recS.get("q1701");// 第一年所属年
                String q1701_e = (String) recE.get("q1701");// 第二年所属年
                float take_off_time = 0;
                // 分情况
                // 第一年的结余在申请天以后，要先减去结余数
                if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                        && last_balance_Time.length() > 0) {
                    String balance_S = (String) recS.get(last_balance_Time);// 第一年结余截止日期
                    if (balance_S != null && balance_S.length() > 0)// 第一年开始时间在第一年结余日期之前
                    {
                        Date balanceS_D = DateUtils.getDate(balance_S, "yyyy-MM-dd");
                        balanceS_D = DateUtils.getDate(DateUtils.format(balanceS_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                        if (balanceS_D.before(start_D))// 开始时间在结余截止时间以前
                        {
                            float diffFor_s_b = getHistoryLeaveTime(start_D, balanceS_D, a0100, nbase, b0110, kqItem_hash,
                                    holiday_rules);
                            float last_sf = Float.parseFloat((String) recS.get(last_spare));// 得到结余剩余天数
                            if (last_sf > 0 && diffFor_s_b > 0)// 结余截止日期和申请开始日期之后就加上剩余天数
                            {
                                if (diffFor_s_b - last_sf >= 0) {
                                    time = last_sf;
                                } else {
                                    time = diffFor_s_b;
                                }
                            }
                            if (time > 0) {

                                float sf = 0;
                                if (leave_time > time) {
                                    leave_time = leave_time - time;
                                    sf = last_sf - diffFor_s_b > 0 ? last_sf - diffFor_s_b : 0;
                                    array[1] = time;
                                } else {
                                    sf = time - leave_time;
                                    array[1] = leave_time;
                                    leave_time = 0;

                                }
                                upl.setLength(0);
                                upl.append("update Q17 set " + last_spare + "=");
                                upl.append(sf);
                                // 不再更新上年结余字段
                                upl.append(" where a0100='");
                                upl.append(a0100);
                                upl.append("'");
                                upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                                upl.append(" and q1709='" + q1709 + "'");
                                upl.append(" and q1701='" + q1701_s + "'");
                                // dao.update(upl.toString());
                                uplist.add(upl.toString());

                            }
                        }
                    }

                }
                if (leave_time <= 0) {
                    return "0," + array[1] + "";
                }
                // 第一年结余的扣除完毕，如果剩余天数leave_time>0继续向下
                float q1707_s = Float.parseFloat((String) recS.get("q1707")); // 剩余天数
                Date q17z3D_s = DateUtils.getDate(q17z3_s, "yyyy-MM-dd");
                q17z3D_s = DateUtils.getDate(DateUtils.format(q17z3D_s, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");

                if (time > 0) {
                    /*
                     * float mm=(time*24*60); String
                     * ms=PubFunc.DoFormatDecimal(mm+"",0);
                     * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms));
                     */
                    take_off_time = time;
                }
                time = 0;
                float diffTopHalf = getLeaveTime_2(start_D, q17z3D_s, a0100, nbase, b0110, kqItem_hash, q1707_s + take_off_time,
                        holiday_rules)
                        - take_off_time;// 跨年时，得到开始时间到当前假期结束时间差了多少天
                if (diffTopHalf > 0) {
                    if (diffTopHalf <= q1707_s)// 第一年申请开始到第一年结束时间间隔小于剩余天数，用时间间隔天数
                    {
                        if (diffTopHalf > leave_time)// 如果时间间隔时间大于剩余需要申请的时间
                        {
                            time = leave_time;// 就用申请剩余时间
                            leave_time = 0;
                        } else {
                            time = diffTopHalf;// 否则用时间间隔时间
                            leave_time = leave_time - diffTopHalf;
                        }

                    } else {
                        // time=leave_time-q1707_s;
                        throw new GeneralException(q1701_1 + "年可休天数为" + q1707_s
                                + "天，您的当前申请超出了可休天数" + (diffTopHalf - q1707_s) + "天，请修改！");
                    }
                    if (time > 0) {
                        // 已休天数
                        if (last_balance != null && last_balance.length() > 0) {
                            String sql = "select * from q17 where q1701='" + q1701_e + "' and a0100='" + a0100 + "' and nbase='"
                                    + nbase + "' and q1709='" + q1709 + "'";
                            List list_bean = ExecuteSQL.executeMyQuery(sql, this.conn);
                            if (!list_bean.isEmpty() && list_bean.size() > 0) {
                                LazyDynaBean bean = (LazyDynaBean) list_bean.get(0);
                                String l_b_s = (String) bean.get(last_balance);
                                String l_s_s = (String) bean.get(last_spare);
                                if (l_b_s != null && l_b_s.length() > 0 && l_s_s != null && l_s_s.length() > 0) {
                                    float l_b_f = Float.parseFloat(l_b_s);// 下一年结余天
                                    float l_s_f = Float.parseFloat(l_s_s);// 下一年结余剩余天
                                    if (l_b_f > l_s_f && leave_time > l_s_f) {
                                        throw new GeneralException("当前申请额度已被在" + q1701_e
                                                + "年申请占用了，不能申请该记录！");
                                    }
                                }
                            }
                        }
                        float td = Float.parseFloat((String) recS.get("q1705"));
                        // 可休天数
                        float dd = Float.parseFloat((String) recS.get("q1707"));
                        float value1 = td + time;
                        float value2 = dd - time;
                        upl.setLength(0);
                        upl.append("update Q17 set q1705=");
                        upl.append(value1);
                        upl.append(", q1707=");
                        upl.append(value2);
                        // 不再更新上年结余字段
                        upl.append(" where a0100='");
                        upl.append(a0100);
                        upl.append("'");
                        upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                        upl.append(" and q1709='" + q1709 + "'");
                        upl.append(" and q1701='" + q1701_s + "'");
                        // dao.update(upl.toString());
                        uplist.add(upl.toString());
                        if (last_balance != null && last_balance.length() > 0) {
                            String last_balance_e = (String) recE.get(last_balance);// 重新计算第二年上年结余
                            if (last_balance_e != null && Float.parseFloat(last_balance_e) > 0) {
                                upl.setLength(0);
                                upl.append("update q17 set " + last_balance);
                                upl.append("=" + last_balance + "-" + time);
                                upl.append(" where nbase='" + nbase + "' ");
                                upl.append(" and q1701='" + q1701_e + "'");
                                upl.append(" and q1709='" + q1709 + "'");
                                upl.append(" and a0100='" + a0100 + "'");
                                uplist.add(upl.toString());
                                // dao.update(upl.toString());
                                // 上年剩余
                                upl.setLength(0);
                                upl.append("update q17 set " + last_spare);
                                upl.append("=" + last_spare + "-" + time);
                                upl.append(" where nbase='" + nbase + "' ");
                                upl.append(" and q1701='" + q1701_e + "'");
                                upl.append(" and q1709='" + q1709 + "'");
                                upl.append(" and a0100='" + a0100 + "'");
                                // dao.update(upl.toString());
                                uplist.add(upl.toString());

                            }

                        }
                        array[0] = time;
                    }
                }
                for (int i = 0; i < uplist.size(); i++) {
                    dao.update(uplist.get(i).toString());
                }
                uplist.clear();
                // 计算第二年扣除天数
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD));
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
                if (listE != null && listE.size() > 0) {
                    recE = (LazyDynaBean) listE.get(0);
                }
                if (time > 0) {
                    /*
                     * float mm=(time*24*60); String
                     * ms=PubFunc.DoFormatDecimal(mm+"",0);
                     * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms));
                     */
                    take_off_time = take_off_time + time;
                }
                time = 0;
                String q17z3_e = (String) recE.get("q17z3");// 第一年截止日期
                Date q17z3D_e = DateUtils.getDate(q17z3_e, "yyyy-MM-dd");
                q17z3D_e = DateUtils.getDate(DateUtils.format(q17z3D_e, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                        && last_balance_Time.length() > 0) {
                    String balance_e = (String) recE.get(last_balance_Time);
                    float last_sf = Float.parseFloat((String) recE.get(last_spare));// 得到结余剩余天数
                    if (balance_e != null && balance_e.length() > 0 && last_sf > 0) {
                        float sf = 0;
                        Date balance_D = DateUtils.getDate(balance_e, "yyyy-MM-dd");
                        balance_D = DateUtils.getDate(DateUtils.format(balance_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                        float diff = 0;
                        if (end_D.before(balance_D)) {
                            diff = getHistoryLeaveTime(start_D, end_D, a0100, nbase, b0110, kqItem_hash, holiday_rules)
                                    - take_off_time;// 结束时间在结余截止时间以前
                        } else {
                            diff = getHistoryLeaveTime(start_D, balance_D, a0100, nbase, b0110, kqItem_hash, holiday_rules)
                                    - take_off_time;
                        }

                        // 结余剩余
                        if (diff > 0 && last_sf > 0) {// 在结余截止日期范围内并且结余天数大于0
                            if (diff - last_sf >= 0)// 判断
                            {
                                time = last_sf;
                            } else {
                                time = diff;
                            }
                        }
                        if (leave_time > time) {
                            leave_time = leave_time - time;
                            sf = last_sf - time > 0 ? last_sf - time : 0;
                            array[3] = time;
                        } else {
                            sf = last_sf - leave_time > 0 ? last_sf - leave_time : 0;
                            array[3] = leave_time;
                            leave_time = 0;

                        }
                        if (time > 0) {
                            upl.setLength(0);
                            upl.append("update Q17 set " + last_spare + "=");
                            upl.append(sf);
                            // 不再更新上年结余字段
                            upl.append(" where a0100='");
                            upl.append(a0100);
                            upl.append("'");
                            upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                            upl.append(" and q1709='" + q1709 + "'");
                            upl.append(" and q1701='" + q1701_e + "'");
                            // dao.update(upl.toString());
                            uplist.add(upl.toString());

                        }
                    }

                }
                if (leave_time > 0) {
                    // 已休天数
                    float td = Float.parseFloat((String) recE.get("q1705"));
                    if (td<0) {
                        throw new GeneralException(rs.getString("A0101") + "-假期数据异常：" + q1701_e + "年度已休天数为负数！请联系考勤管理员处理。");
                    }
                    
                    // 可休天数
                    float dd = Float.parseFloat((String) recE.get("q1707"));
                    if (sp != null && !"5".equals(sp)) {
                        if (td < 0 || dd <= 0) {
                            return array[0] + "," + array[1] + ";" + array[2] + "," + array[3];
                        }
                    }
                    float value1 = td + leave_time;
                    float value2 = dd - leave_time;
                    upl.setLength(0);
                    upl.append("update Q17 set q1705=");
                    upl.append(value1);
                    upl.append(", q1707=");
                    upl.append(value2);
                    // 不再更新上年结余字段
                    upl.append(" where a0100='");
                    upl.append(a0100);
                    upl.append("'");
                    upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                    upl.append(" and q1709='" + q1709 + "'");
                    upl.append(" and q1701='" + q1701_e + "'");
                    uplist.add(upl.toString());
                    array[2] = leave_time;
                }

            }
            for (int i = 0; i < uplist.size(); i++) {
                dao.update(uplist.get(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return array[0] + "," + array[1] + ";" + array[2] + "," + array[3];
    }

    /**
     * 更改假期管理
     * 
     * @param a0100
     * @param nbase
     * @param q1709
     * @param start
     * @param end
     * @param leave_time
     * @param sp
     * @return
     * @throws GeneralException
     */
    public String getLeaveManage(String a0100, String nbase, String q1709, String start, String end, float leave_time, String sp,
            String b0110, HashMap kqItem_hash, float[] holiday_rules) throws GeneralException {
        if (!KqParam.getInstance().isHoliday(conn, b0110, q1709)) {
            return "";
        }
        
        String column_z1 = "q17z1";
        String column_z3 = "q17z3";
        start = start.replaceAll("-", ".");
        end = end.replaceAll("-", ".");
        Date start_D = DateUtils.getDate(start, "yyyy.MM.dd HH:mm");
        Date end_D = DateUtils.getDate(end, "yyyy.MM.dd HH:mm");
        String stD = DateUtils.format(start_D, "yyyy.MM.dd");
        String edD = DateUtils.format(end_D, "yyyy.MM.dd");
        StringBuffer sb = new StringBuffer();
        StringBuffer wb = new StringBuffer();
        String last_balance = KqUtilsClass.getFieldByDesc("q17", "上年结余");// getBalance();
        String last_balance_Time = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");// getBalance();
        String last_spare = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
        float time = 0;
        RowSet rs = null;
        float[] array = new float[4];
        try {
            String q1701_1 = "";
            String q1701_2 = "";
            ContentDAO dao = new ContentDAO(this.conn);
            // 判断假期申请是否跨年
            sb.append("select * from Q17 where a0100='" + a0100 + "'");
            sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            sb.append(" and q1709='" + q1709 + "'");
            wb.append(sb);
            wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD));
            wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(stD));
            List listS = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);

            LazyDynaBean recS = null;
            LazyDynaBean recE = null;
            if (listS != null && listS.size() > 0) {
                recS = (LazyDynaBean) listS.get(0);
                q1701_1 = (String) recS.get("q1701");
            } else {
                throwHolidyNotFoundException(stD);
            }

            wb.setLength(0);
            wb.append(sb);
            wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD) + "");
            wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
            List listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
            if (listE != null && listE.size() > 0) {
                recE = (LazyDynaBean) listE.get(0);
                q1701_2 = (String) recE.get("q1701");
            } else {
                throwHolidyNotFoundException(edD);
            }
            // 比较是否是同一年
            if (q1701_1.equals(q1701_2)) {
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD));
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                rs = dao.search(wb.toString());
                if (rs.next()) {
                    String q1701 = rs.getString("q1701");
                    // 已休天数
                    float td = rs.getFloat("q1705");
                    // 可休天数
                    float dd = rs.getFloat("q1707");
                    // 判断上年结余
                    if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                            && last_balance_Time.length() > 0) {
                        Date balance_D = rs.getTimestamp(last_balance_Time); // 结余截止日期
                        if (balance_D != null) {
                            balance_D = DateUtils.getDate(DateUtils.format(balance_D, "yyyy.MM.dd") + " 23:59",
                                    "yyyy.MM.dd HH:mm");
                            // float diff=DateUtils.dayDiff(start_D,
                            // balance_D)+1;
                            if (start_D.before(balance_D))// 开始时间在结余截止时间以前
                            {
                                float diff = 0;
                                if (end_D.before(balance_D)) {
                                    diff = getHistoryLeaveTime(start_D, end_D, a0100, nbase, b0110, kqItem_hash, holiday_rules);
                                } else {
                                    diff = getHistoryLeaveTime(start_D, balance_D, a0100, nbase, b0110, kqItem_hash,
                                            holiday_rules);
                                }

                                float last_sf = rs.getFloat(last_spare);// 结余剩余
                                if (diff > 0 && last_sf > 0) {// 在结余截止日期范围内并且结余天数大于0
                                    if (diff - last_sf >= 0)// 判断
                                    {
                                        time = last_sf;
                                    } else {
                                        time = diff;
                                    }

                                }
                                if (leave_time > time) {
                                    leave_time = leave_time - time;
                                    array[1] = time;
                                } else {
                                    array[1] = leave_time;
                                    leave_time = 0;
                                }
                            }
                        }

                    }
                    if (leave_time > 0) {
                        /*
                         * if(time>0){ float mm=(time*24*60); String
                         * ms=PubFunc.DoFormatDecimal(mm+"",0);
                         * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms)); }
                         */
                        float diffLastHalf = getLeaveTime_2(start_D, end_D, a0100, nbase, b0110, kqItem_hash, leave_time + time,
                                holiday_rules);// 剩下的时间
                        
                        //zxj jazz 32136浮点数判断改进，避免出现1.999999<2.0的情况
                        if ((diffLastHalf - time - leave_time) >= -0.001) {
                            int currYear = Integer.parseInt(q1701);
                            String nextYear = String.valueOf(currYear + 1);
                            if (last_balance != null && last_balance.length() > 0) {
                                String sql = "select * from q17 where q1701='" + nextYear + "' and a0100='" + a0100
                                        + "' and nbase='" + nbase + "'";
                                List list_bean = ExecuteSQL.executeMyQuery(sql, this.conn);
                                if (!list_bean.isEmpty() && list_bean.size() > 0) {
                                    LazyDynaBean bean = (LazyDynaBean) list_bean.get(0);
                                    String l_b_s = (String) bean.get(last_balance);
                                    String l_s_s = (String) bean.get(last_spare);
                                    if (l_b_s != null && l_b_s.length() > 0 && l_s_s != null && l_s_s.length() > 0) {
                                        float l_b_f = Float.parseFloat(l_b_s);// 下一年结余天
                                        float l_s_f = Float.parseFloat(l_s_s);// 下一年结余剩余天
                                        if (l_b_f > l_s_f && leave_time > l_s_f) {
                                            throw GeneralExceptionHandler.Handle(new GeneralException("", "当前申请额度已被在" + nextYear
                                                    + "年申请占用了，不能申请该记录！", "", ""));
                                        }
                                    }
                                }
                            }

                            if (sp != null && !"5".equals(sp)) {
                                if (td < 0 || dd <= 0) {
                                    return array[0] + "," + array[1];
                                }
                            }
                            array[0] = leave_time;
                        } else {
                            throw GeneralExceptionHandler
                                    .Handle(new GeneralException("", "假期申请数据保存错误，请检查申请时间时长是否符合管理时长！", "", ""));
                        }

                    }
                }
            } else {

                String q17z3_s = (String) recS.get("q17z3");// 第一年截止日期
                String q1701_e = (String) recE.get("q1701");// 第二年所属年
                float take_off_time = 0;
                // 分情况
                // 第一年的结余在申请天以后，要先减去结余数
                if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                        && last_balance_Time.length() > 0) {
                    String balance_S = (String) recS.get(last_balance_Time);// 第一年结余截止日期
                    if (balance_S != null && balance_S.length() > 0)// 第一年开始时间在第一年结余日期之前
                    {
                        Date balanceS_D = DateUtils.getDate(balance_S, "yyyy-MM-dd");
                        balanceS_D = DateUtils.getDate(DateUtils.format(balanceS_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                        if (start_D.before(balanceS_D))// 开始时间在结余截止时间以前
                        {
                            float diffFor_s_b = getHistoryLeaveTime(start_D, balanceS_D, a0100, nbase, b0110, kqItem_hash,
                                    holiday_rules);
                            float last_sf = Float.parseFloat((String) recS.get(last_spare));// 得到结余剩余天数
                            if (last_sf > 0 && diffFor_s_b > 0)// 结余截止日期和申请开始日期之后就加上剩余天数
                            {
                                if (diffFor_s_b - last_sf >= 0) {
                                    time = last_sf;
                                } else {
                                    time = diffFor_s_b;
                                }
                            }
                            if (time > 0) {

                                if (leave_time > time) {
                                    leave_time = leave_time - time;
                                    array[1] = time;
                                } else {
                                    array[1] = leave_time;
                                    leave_time = 0;

                                }

                            }
                        }
                    }

                }
                if (leave_time <= 0) {
                    return "0," + array[1] + "";
                }
                // 第一年结余的扣除完毕，如果剩余天数leave_time>0继续向下
                float q1707_s = Float.parseFloat((String) recS.get("q1707")); // 剩余天数
                Date q17z3D_s = DateUtils.getDate(q17z3_s, "yyyy-MM-dd");
                q17z3D_s = DateUtils.getDate(DateUtils.format(q17z3D_s, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");

                if (time > 0) {
                    /*
                     * float mm=(time*24*60); String
                     * ms=PubFunc.DoFormatDecimal(mm+"",0);
                     * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms));
                     */
                    take_off_time = time;
                }
                time = 0;
                float diffTopHalf = getLeaveTime_2(start_D, q17z3D_s, a0100, nbase, b0110, kqItem_hash, q1707_s + take_off_time,
                        holiday_rules)
                        - take_off_time;// 跨年时，得到开始时间到当前假期结束时间差了多少天
                if (diffTopHalf > 0) {
                    if (diffTopHalf <= q1707_s)// 第一年申请开始到第一年结束时间间隔小于剩余天数，用时间间隔天数
                    {
                        if (diffTopHalf > leave_time)// 如果时间间隔时间大于剩余需要申请的时间
                        {
                            time = leave_time;// 就用申请剩余时间
                            leave_time = 0;
                        } else {
                            time = diffTopHalf;// 否则用时间间隔时间
                            leave_time = leave_time - diffTopHalf;
                        }

                    } else {
                        // time=leave_time-q1707_s;
                        throw GeneralExceptionHandler.Handle(new GeneralException(q1701_1 + "年可休天数为" + q1707_s
                                + "天，您的当前申请超出了可休天数" + (diffTopHalf - q1707_s) + "天，请修改！"));
                    }
                    if (time > 0) {
                        // 已休天数
                        if (last_balance != null && last_balance.length() > 0) {
                            String sql = "select * from q17 where q1701='" + q1701_e + "' and a0100='" + a0100 + "' and nbase='"
                                    + nbase + "'";
                            List list_bean = ExecuteSQL.executeMyQuery(sql, this.conn);
                            if (!list_bean.isEmpty() && list_bean.size() > 0) {
                                LazyDynaBean bean = (LazyDynaBean) list_bean.get(0);
                                String l_b_s = (String) bean.get(last_balance);
                                String l_s_s = (String) bean.get(last_spare);
                                if (l_b_s != null && l_b_s.length() > 0 && l_s_s != null && l_s_s.length() > 0) {
                                    float l_b_f = Float.parseFloat(l_b_s);// 下一年结余天
                                    float l_s_f = Float.parseFloat(l_s_s);// 下一年结余剩余天
                                    if (l_b_f > l_s_f && leave_time > l_s_f) {
                                        throw GeneralExceptionHandler.Handle(new GeneralException("", "当前申请额度已被在" + q1701_e
                                                + "年申请占用了，不能申请该记录！", "", ""));
                                    }
                                }
                            }
                        }
                        array[0] = time;
                    }
                }
                // 计算第二年扣除天数
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD) + "");
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
                if (listE != null && listE.size() > 0) {
                    recE = (LazyDynaBean) listE.get(0);
                }
                if (time > 0) {
                    /*
                     * float mm=(time*24*60); String
                     * ms=PubFunc.DoFormatDecimal(mm+"",0);
                     * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms));
                     */
                    take_off_time = take_off_time + time;
                }
                time = 0;
                String q17z3_e = (String) recE.get("q17z3");// 第一年截止日期
                Date q17z3D_e = DateUtils.getDate(q17z3_e, "yyyy-MM-dd");
                q17z3D_e = DateUtils.getDate(DateUtils.format(q17z3D_e, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                        && last_balance_Time.length() > 0) {
                    String balance_e = (String) recE.get(last_balance_Time);
                    float last_sf = Float.parseFloat((String) recE.get(last_spare));// 得到结余剩余天数
                    if (balance_e != null && balance_e.length() > 0 && last_sf > 0) {
                        Date balance_D = DateUtils.getDate(balance_e, "yyyy-MM-dd");
                        balance_D = DateUtils.getDate(DateUtils.format(balance_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                        float diff = 0;
                        if (end_D.before(balance_D)) {
                            diff = getHistoryLeaveTime(start_D, end_D, a0100, nbase, b0110, kqItem_hash, holiday_rules)
                                    - take_off_time;// 结束时间在结余截止时间以前
                        } else {
                            diff = getHistoryLeaveTime(start_D, balance_D, a0100, nbase, b0110, kqItem_hash, holiday_rules)
                                    - take_off_time;
                        }

                        // 结余剩余
                        if (diff > 0 && last_sf > 0) {// 在结余截止日期范围内并且结余天数大于0
                            if (diff - last_sf >= 0)// 判断
                            {
                                time = last_sf;
                            } else {
                                time = diff;
                            }
                        }
                        if (leave_time > time) {
                            leave_time = leave_time - time;
                            array[3] = time;
                        } else {
                            array[3] = leave_time;
                            leave_time = 0;

                        }
                    }

                }
                if (leave_time > 0) {
                    // 已休天数
                    float td = Float.parseFloat((String) recE.get("q1705"));
                    // 可休天数
                    float dd = Float.parseFloat((String) recE.get("q1707"));
                    if (sp != null && !"5".equals(sp)) {
                        if (td < 0 || dd <= 0) {
                            return array[0] + "," + array[1] + ";" + array[2] + "," + array[3];
                        }
                    }
                    array[2] = leave_time;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return array[0] + "," + array[1] + ";" + array[2] + "," + array[3];
    }

    /**
     * 销假得到时间段
     * 
     * @param a0100
     * @param nbase
     * @param q1709
     * @param start
     * @param end
     * @param leave_time
     * @return
     * @throws GeneralException
     */
    public float[] getCancelHolsTimeManage(String a0100, String nbase, String q1709, String start, String end, float leave_time,
            String b0110, HashMap kqItem_hash, String src_z1D, String src_z3D, float[] holiday_rules) throws GeneralException {
        
        q1709 = KqAppInterface.switchTypeIdFromHolidayMap(q1709);
        
        String column_z1 = "q17z1";
        String column_z3 = "q17z3";
        // 判断被销假记录是否申请跨年度

        String year_1 = "";
        float tale_off_time = 0;
        // 判断假期申请是否跨年
        StringBuffer sb = new StringBuffer();
        StringBuffer wb = new StringBuffer();
        sb.append("select * from Q17 where a0100='" + a0100 + "'");
        sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        sb.append(" and q1709='" + q1709 + "'");
        wb.append(sb);
        wb.append(" and q17z1<=" + Sql_switcher.dateValue(src_z1D) + "");
        wb.append(" and q17z3>=" + Sql_switcher.dateValue(src_z1D));
        List listS = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
        LazyDynaBean recS = null;
        LazyDynaBean recE = null;
        if (listS != null && listS.size() > 0) {
            recS = (LazyDynaBean) listS.get(0);
            year_1 = (String) recS.get("q1701");
        } else {
            throwHolidyNotFoundException(src_z1D);
        }

        wb.setLength(0);
        wb.append(sb);
        wb.append(" and q17z1<=" + Sql_switcher.dateValue(src_z3D) + "");
        wb.append(" and q17z3>=" + Sql_switcher.dateValue(src_z3D));
        List listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
        if (listE != null && listE.size() > 0) {
            recE = (LazyDynaBean) listE.get(0);
        } else {
            throwHolidyNotFoundException(src_z3D);
        }

        start = start.replaceAll("-", ".");
        end = end.replaceAll("-", ".");
        Date start_D = DateUtils.getDate(start, "yyyy.MM.dd HH:mm");
        Date end_D = DateUtils.getDate(end, "yyyy.MM.dd HH:mm");
        String stD = DateUtils.format(start_D, "yyyy.MM.dd");
        String edD = DateUtils.format(end_D, "yyyy.MM.dd");
        sb = new StringBuffer();
        wb = new StringBuffer();
        String last_balance = KqUtilsClass.getFieldByDesc("q17", "上年结余");// getBalance();
        String last_balance_Time = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");// getBalance();
        float time = 0;
        RowSet rs = null;
        float[] array = new float[4];
        try {
            String q1701_1 = "";
            String q1701_2 = "";
            ContentDAO dao = new ContentDAO(this.conn);
            // 判断假期申请是否跨年
            sb.append("select * from Q17 where a0100='" + a0100 + "'");
            sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            sb.append(" and q1709='" + q1709 + "'");
            wb.append(sb);
            wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD) + "");
            wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(stD));
            listS = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);

            recS = null;
            recE = null;
            if (listS != null && listS.size() > 0) {
                recS = (LazyDynaBean) listS.get(0);
                q1701_1 = (String) recS.get("q1701");
            } else {
                throwHolidyNotFoundException(stD);
            }

            wb.setLength(0);
            wb.append(sb);
            wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD) + "");
            wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
            listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
            if (listE != null && listE.size() > 0) {
                recE = (LazyDynaBean) listE.get(0);
                q1701_2 = (String) recE.get("q1701");
            } else {
                throwHolidyNotFoundException(edD);
            }
            // 比较是否是同一年

            if (q1701_1.equals(q1701_2))// 夸年头
            {
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(stD) + "");
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                rs = dao.search(wb.toString());
                if (rs.next()) {
                    float qf = 0;// 可休
                    float lf = 0;// 结余
                    // 判断上年结余
                    if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                            && last_balance_Time.length() > 0) {
                        Date balance_D = rs.getTimestamp(last_balance_Time); // 结余截止日期
                        if (balance_D != null) {
                            balance_D = DateUtils.getDate(DateUtils.format(balance_D, "yyyy.MM.dd") + " 23:59",
                                    "yyyy.MM.dd HH:mm");
                            // float diff=DateUtils.dayDiff(start_D,
                            // balance_D)+1;

                            if (start_D.before(balance_D))// 开始时间在结余截止时间以前
                            {
                                float diff = 0;
                                if (end_D.before(balance_D)) {
                                    diff = getHistoryLeaveTime(start_D, end_D, a0100, nbase, b0110, kqItem_hash, holiday_rules);
                                } else {
                                    diff = getHistoryLeaveTime(start_D, balance_D, a0100, nbase, b0110, kqItem_hash,
                                            holiday_rules);
                                }

                                float last_sf = rs.getFloat(last_balance);// 得到上年结余数
                                if (diff > 0 && last_sf > 0) {// 在结余截止日期范围内并且结余天数大于0
                                    if (last_sf < diff) {
                                        time = last_sf;
                                    } else {
                                        time = diff;
                                    }
                                }
                                if (leave_time > time) {
                                    leave_time = leave_time - time;
                                    lf = time;
                                } else {
                                    lf = leave_time;
                                    leave_time = 0;
                                }
                            }
                        }

                    }
                    if (leave_time > 0) {
                        qf = leave_time;
                    }
                    if (q1701_1.equals(year_1)) {
                        array[0] = qf;
                        array[1] = lf;
                    } else {
                        array[2] = qf;
                        array[3] = lf;
                    }
                }
            } else {

                String q17z3_s = (String) recS.get("q17z3");// 第一年截止日期
                // 分情况
                // 第一年的结余在申请天以后，要先减去结余数
                if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                        && last_balance_Time.length() > 0) {
                    String balance_S = (String) recS.get(last_balance_Time);// 第一年结余截止日期
                    if (balance_S != null && balance_S.length() > 0)// 第一年开始时间在第一年结余日期之前
                    {
                        Date balanceS_D = DateUtils.getDate(balance_S, "yyyy-MM-dd");
                        balanceS_D = DateUtils.getDate(DateUtils.format(balanceS_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                        if (start_D.before(balanceS_D))// 开始时间在结余截止时间以前
                        {
                            float diffFor_s_b = getHistoryLeaveTime(start_D, balanceS_D, a0100, nbase, b0110, kqItem_hash,
                                    holiday_rules);
                            float last_sf = Float.parseFloat((String) recS.get(last_balance));// 得到剩余天数
                            if (last_sf > 0 && diffFor_s_b > 0)// 结余截止日期和申请开始日期之后就加上剩余天数
                            {
                                if (diffFor_s_b > last_sf) {
                                    time = last_sf;
                                } else {
                                    time = diffFor_s_b;
                                }
                            }
                            if (time > 0) {
                                leave_time = leave_time - time;
                                array[1] = time;

                            }
                        }
                    }

                }
                // 第一年结余的扣除完毕，如果剩余天数leave_time>0继续向下
                Date q17z3D_s = DateUtils.getDate(q17z3_s, "yyyy-MM-dd");
                q17z3D_s = DateUtils.getDate(DateUtils.format(q17z3D_s, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");

                if (time > 0) {
                    /*
                     * float mm=(time*24*60); String
                     * ms=PubFunc.DoFormatDecimal(mm+"",0);
                     * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms));
                     */
                    tale_off_time = tale_off_time + time;
                }
                time = 0;
                float diffTopHalf = getHistoryLeaveTime(start_D, q17z3D_s, a0100, nbase, b0110, kqItem_hash, holiday_rules)
                        - tale_off_time;// 跨年时，得到开始时间到当前假期结束时间差了多少天
                if (diffTopHalf > 0) {
                    leave_time = leave_time - diffTopHalf;
                    time = diffTopHalf;
                    array[0] = time;
                }
                // 计算第二年扣除天数
                wb.setLength(0);
                wb.append(sb);
                wb.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(edD) + "");
                wb.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(edD));
                listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
                if (listE != null && listE.size() > 0) {
                    recE = (LazyDynaBean) listE.get(0);
                }
                if (time > 0) {
                    /*
                     * float mm=(time*24*60); String
                     * ms=PubFunc.DoFormatDecimal(mm+"",0);
                     * start_D=KqUtilsClass.addMinute(start_D,Integer.parseInt(ms));
                     */
                    tale_off_time = tale_off_time + time;
                }
                time = 0;
                String q17z3_e = (String) recE.get("q17z3");// 第一年截止日期
                Date q17z3D_e = DateUtils.getDate(q17z3_e, "yyyy-MM-dd");
                q17z3D_e = DateUtils.getDate(DateUtils.format(q17z3D_e, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                if (last_balance != null && last_balance.length() > 0 && last_balance_Time != null
                        && last_balance_Time.length() > 0) {
                    String balance_e = (String) recE.get(last_balance_Time);
                    if (balance_e != null && balance_e.length() > 0) {
                        Date balance_D = DateUtils.getDate(balance_e, "yyyy-MM-dd");
                        balance_D = DateUtils.getDate(DateUtils.format(balance_D, "yyyy.MM.dd") + " 23:59", "yyyy.MM.dd HH:mm");
                        float diff = 0;
                        if (end_D.before(balance_D)) {
                            diff = getHistoryLeaveTime(start_D, end_D, a0100, nbase, b0110, kqItem_hash, holiday_rules)
                                    - tale_off_time;// 结束时间在结余截止时间以前
                        } else {
                            diff = getHistoryLeaveTime(start_D, balance_D, a0100, nbase, b0110, kqItem_hash, holiday_rules)
                                    - tale_off_time;
                        }

                        float last_sf = Float.parseFloat((String) recE.get(last_balance));// 得到结余天数

                        if (diff > 0 && last_sf > 0) {// 在结余截止日期范围内并且结余天数大于0
                            if (diff > last_sf) {
                                time = last_sf;
                            } else {
                                time = diff;
                            }
                            array[3] = time;
                            leave_time = leave_time - time;
                        }
                    }
                }
                if (leave_time > 0) {

                    array[2] = leave_time;
                }

                return array;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return array;
    }

    /**
     * 得到班次id
     * 
     * @param conn
     * @param start_date
     * @param end_date
     * @param a0100
     * @param nbase
     * @return
     */
    private String getClassId(String op_date, String a0100, String nbase) throws GeneralException {
        String class_id = "";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select class_id from kq_employ_shift ");
            sql.append(" where a0100='" + a0100 + "' and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            sql.append(" and q03z0 ='" + op_date.toString() + "'");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                class_id = rs.getString("class_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        if (class_id == null || class_id.length() <= 0) {
            return "";
        }
        return class_id;
    }

    /**
     * 起始日期是否在封存期间内
     * @param state_date <date> 起始日期
     * @param end_date <date> 结束日期
     * @return <boolean> 在封存期间：true
     */
    public boolean inSealDuration(Date start_date) {
        boolean inSealDuration = false;

        String z1 = DateUtils.format(start_date, "yyyy-MM-dd");
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append("select 1 from kq_duration");
        selectSQL.append(" where finished=1 ");
        selectSQL.append(" and kq_end>=" + Sql_switcher.dateValue(z1));

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(selectSQL.toString());
            inSealDuration = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return inSealDuration;
    }

    /**
     * 申请起始日期是否在封存考勤期间，如在封存期间内则抛出异常
     * @return
     */
    public void checkAppInSealDuration(Date startDate) throws GeneralException {
        checkAppInSealDuration(startDate, "");
    }

    /**
     * 申请起始日期是否在封存考勤期间，如在封存期间内则抛出异常
     * @param startDate
     * @param endDate
     * @param appInfo
     * @throws GeneralException
     */
    public void checkAppInSealDuration(Date startDate, String appInfo) throws GeneralException {
        if (inSealDuration(startDate)) {
            throw new GeneralException(appInfo + ResourceFactory.getProperty("kq.app.in.seal.duration.hint"));
        }
    }

    public String getAppLeavedMess() {
        return appLeavedMess;
    }

    public void setAppLeavedMess(String appLeavedMess) {
        this.appLeavedMess = appLeavedMess;
    }

    public float othenPlanTime(Date start_date, Date end_date, String a0100, String nbase, String b0110, String q3101,
            HashMap kqItem_hash, String oper) throws GeneralException {
        float time = 0;
        HashMap hash = getHols_Time("06", a0100, nbase, DateUtils.format(start_date, "yyyy-MM-dd HH:mm:ss"), DateUtils.format(
                end_date, "yyyy-MM-dd HH:mm:ss"));
        Date q17z1 = (Date) hash.get("q17z1");
        Date q17z3 = (Date) hash.get("q17z3");
        if (q17z1 == null || q17z3 == null) {
            return 0;
        }
        String has_feast = (String) kqItem_hash.get("has_feast");
        StringBuffer sql = new StringBuffer();
        String column_z1 = "q31z1";
        String column_z3 = "q31z3";
        sql.append("select " + column_z1 + "," + column_z3 + " from q31 where ");
        sql.append("a0100='" + a0100 + "'");
        sql.append(" and " + column_z1 + ">=" + Sql_switcher.dateValue(DateUtils.format(q17z1, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(DateUtils.format(q17z3, "yyyy-MM-dd HH:mm:ss")));
        sql.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        // sql.append(" and q31z0='01'");
        sql.append(" and q31z5<>'02'");
        if (q3101 != null && q3101.length() > 0 && "up".equalsIgnoreCase(oper)) {
            sql.append(" and q3101<>'" + q3101 + "'");
        }
        // System.out.println(sql.toString());
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String has_rest = (String) kqItem_hash.get("has_rest");
        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.conn);
        String rest_date = restList.get(0).toString();
        String rest_b0110 = restList.get(1).toString();
        StringBuffer message = new StringBuffer();
        try {
            rs = dao.search(sql.toString());
            Date s_date = null;
            Date e_date = null;
            float num = 0;
            while (rs.next()) {
                s_date = rs.getTimestamp(column_z1);
                e_date = rs.getTimestamp(column_z3);
                float leave_time = 0;
                s_date = rs.getTimestamp(column_z1);
                e_date = rs.getTimestamp(column_z3);
                num = DateUtils.dayDiff(s_date, e_date);
                /*
                 * if(has_feast!=null&&has_feast.equals("1")) {
                 * leave_time=num+1; }else { for(int m=0;m<=num;m++) { String
                 * op_date_to=getDateByAfter(s_date,m); String
                 * feast_name=IfRestDate.if_Feast(op_date_to,this.conn);
                 * if(feast_name!=null&&feast_name.length()>0) { String
                 * turn_date=IfRestDate.getTurn_Date(this.userView.getUserOrgId(),op_date_to,this.conn);
                 * if((turn_date!=null&&turn_date.length()>0)) {
                 * leave_time=leave_time+1; } }else { leave_time=leave_time+1; } } }
                 */
                for (int m = 0; m <= num; m++) {
                    String op_date_to = getDateByAfter(s_date, m);
                    String feast_name = IfRestDate.if_Feast(op_date_to, this.conn);
                    String week_date = IfRestDate.getWeek_Date(rest_b0110, op_date_to, this.conn);
                    if (has_feast != null && "1".equals(has_feast)) {
                        if (feast_name != null && feast_name.length() > 0) {
                            String turn_date = IfRestDate.getTurn_Date(this.userView.getUserOrgId(), op_date_to, this.conn);
                            if ((turn_date == null || turn_date.length() <= 0)) {
                                leave_time = leave_time + 1;
                                continue;
                            }
                        } else {
                            leave_time = leave_time + 1;
                            continue;
                        }
                    } else {
                        if (feast_name != null && feast_name.length() > 0) {
                            String turn_date = IfRestDate.getTurn_Date(this.userView.getUserOrgId(), op_date_to, this.conn);
                            if ((turn_date == null || turn_date.length() <= 0)) {
                                continue;
                            }
                        }
                    }
                    if (has_rest != null && "1".equals(has_rest)) {
                        if (!IfRestDate.if_Rest(op_date_to, userView, rest_date)) {
                            if (week_date != null && week_date.length() > 0) {
                                leave_time = leave_time + 1;
                                continue;
                            }
                        } else {
                            String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, this.conn);
                            if (turn_date == null || turn_date.length() <= 0) {
                                leave_time = leave_time + 1;
                                continue;
                            }
                        }
                    } else {
                        if (!IfRestDate.if_Rest(op_date_to, userView, rest_date)) {
                            if (week_date != null && week_date.length() > 0) {
                                continue;
                            }
                        } else {
                            String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, this.conn);
                            if (turn_date == null || turn_date.length() <= 0) {
                                continue;
                            }
                        }
                    }
                    leave_time = leave_time + 1;
                }
                if (leave_time > 0) {
                    message.append( DateUtils.format(s_date, "yyyy-MM-dd HH:mm:ss") + " --- " + DateUtils.format(e_date, "yyyy-MM-dd HH:mm:ss") + "<br>");
                }
                time = time + leave_time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        this.setAppLeavedMess(message.toString());
        return time;
    }

    public float planDays(Date s_date, Date e_date, HashMap kqItem_hash) throws GeneralException {
        float num = DateUtils.dayDiff(s_date, e_date);
        float time = 0;
        String has_feast = (String) kqItem_hash.get("has_feast");
        String has_rest = (String) kqItem_hash.get("has_rest");
        String b0110 = this.userView.getUserOrgId();
        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.conn);
        String rest_date = restList.get(0).toString();
        String rest_b0110 = restList.get(1).toString();
        for (int m = 0; m <= num; m++) {
            String op_date_to = getDateByAfter(s_date, m);
            String feast_name = IfRestDate.if_Feast(op_date_to, this.conn);
            String week_date = IfRestDate.getWeek_Date(rest_b0110, op_date_to, this.conn);
            if (has_feast != null && "1".equals(has_feast)) {
                if (feast_name != null && feast_name.length() > 0) {
                    String turn_date = IfRestDate.getTurn_Date(this.userView.getUserOrgId(), op_date_to, this.conn);
                    if ((turn_date == null || turn_date.length() <= 0)) {
                        time = time + 1;
                        continue;
                    }
                } else {
                    time = time + 1;
                    continue;
                }
            } else {
                if (feast_name != null && feast_name.length() > 0) {
                    String turn_date = IfRestDate.getTurn_Date(this.userView.getUserOrgId(), op_date_to, this.conn);
                    if ((turn_date == null || turn_date.length() <= 0)) {
                        continue;
                    }
                }
            }
            if (has_rest != null && "1".equals(has_rest)) {
                if (!IfRestDate.if_Rest(op_date_to, userView, rest_date)) {
                    if (week_date != null && week_date.length() > 0) {
                        time = time + 1;
                        continue;
                    }
                } else {
                    String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, this.conn);
                    if (turn_date == null || turn_date.length() <= 0) {
                        time = time + 1;
                        continue;
                    }
                }
            } else {
                if (!IfRestDate.if_Rest(op_date_to, userView, rest_date)) {
                    if (week_date != null && week_date.length() > 0) {
                        continue;
                    }
                } else {
                    String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, this.conn);
                    if (turn_date == null || turn_date.length() <= 0) {
                        continue;
                    }
                }
            }
            time = time + 1;
        }

        return time;
    }

    /**
     * 判断申请记录是否重复
     * @param q1101
     * @param nbase
     * @param a0100
     * @param z1
     * @param z3
     * @param table
     * @return
     * @throws GeneralException
     */
    private boolean ifSaveRestLeisure(String q1101,String nbase, String a0100, Date z1, Date z3, String table, Connection conn,
            String where_child) throws GeneralException {
        boolean isCorrect = true;
        StringBuffer selectSQL = new StringBuffer();
        ContentDAO dao = new ContentDAO(conn);
        String column_z1 = table + "z1";
        selectSQL.append("select * from " + table + " where UPPER(nbase)='" + nbase.toUpperCase() + "'");
        selectSQL.append(" and a0100='" + a0100 + "'");
        selectSQL.append(" and q1101 <>'" + q1101 + "'");
        selectSQL.append(" and " + where_child);
        selectSQL.append(" and " + table + "z5<>'10'");
        //selectSQL.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(end_date));
       // selectSQL.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(start_date));
        selectSQL.append(" and " + Sql_switcher.dateToChar(table + "Z1") + " in (");
        selectSQL.append("'" + OperateDate.dateToStr(z1, "yyyy-MM-dd") + "')");
        RowSet rs = null;
        try {
            rs = dao.search(selectSQL.toString());
            if (rs.next()) {
                isCorrect = false;
                StringBuffer mess = new StringBuffer();
                mess.append(DateUtils.format(rs.getTimestamp(column_z1), "yyyy.MM.dd"));
                this.setAppLeavedMess(mess.toString());
                return isCorrect;
            }
        } catch (Exception e) {
            isCorrect = false;
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }

    /**
     * 判断申请时间是不是不是封存
     * 
     * @param z1D
     * @param z3D
     * @return
     */
    public boolean isSessionSearl(Date z1D, Date z3D) {
        String z1 = DateUtils.format(z1D, "yyyy.MM.dd");
        String z3 = DateUtils.format(z3D, "yyyy.MM.dd");
        StringBuffer sql = new StringBuffer();
        sql.append("select finished from kq_duration where ");
        sql.append("(kq_start>=" + Sql_switcher.dateValue(z1));
        sql.append(" and kq_start<=" + Sql_switcher.dateValue(z3) + ")");
        sql.append(" or (kq_end>" + Sql_switcher.dateValue(z1));
        sql.append(" and kq_end<" + Sql_switcher.dateValue(z3) + ")");
        sql.append(" or (kq_start<=" + Sql_switcher.dateValue(z1));
        sql.append(" and kq_end>=" + Sql_switcher.dateValue(z3) + ")");
        sql.append(" order by kq_year,kq_duration");
        boolean isCorrect = false;
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                String finished = rs.getString("finished") != null && rs.getString("finished").length() > 0 ? rs
                        .getString("finished") : "";
                if ("0".equals(finished)) {
                    isCorrect = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }

    /**
     * 排班判断考勤数据状态
     * @param codesetid
     * @param codeitemid
     * @param nbase
     * @param z1D
     * @param z3D
     * @return
     */
    public boolean KqDailyDataValidate(String codesetid, String codeitemid, String nbase, Date z1D, Date z3D, ArrayList infoList) {
        boolean isCorrect = true;
        RowSet rs = null;
        try {
            String z1 = DateUtils.format(z1D, "yyyy.MM.dd HH:mm:ss");
            String z3 = DateUtils.format(z3D, "yyyy.MM.dd HH:mm:ss");
            StringBuffer sql = new StringBuffer();
            sql.append("select * from kq_duration where ");
            sql.append(" (kq_start>" + Sql_switcher.dateValue(z1));
            sql.append(" and kq_start<" + Sql_switcher.dateValue(z3) + ")");
            sql.append(" or (kq_end>" + Sql_switcher.dateValue(z1));
            sql.append(" and kq_end<" + Sql_switcher.dateValue(z3) + ")");
            sql.append(" or (kq_start<=" + Sql_switcher.dateValue(z1));
            sql.append(" and kq_end>=" + Sql_switcher.dateValue(z3) + ")");
            sql.append(" order by kq_year,kq_duration");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                //start = rs.getDate("kq_start");
                //end = rs.getDate("kq_end");
                z1 = z1.substring(0, 10);
                z3 = z3.substring(0, 10);
                if (codesetid.indexOf("EP") != -1) //人员、
                {
                    StringBuffer sqlWhr = new StringBuffer();
                    sqlWhr.append("nbase='" + nbase + "' and a0100='" + codeitemid + "'");
                    sqlWhr.append(" and q03z0>='" + z1 + "' and q03z0<='" + z3 + "'");
                    sqlWhr.append(" and q03z5 not in('01','07')");
                    
                    KqDBHelper kqDB = new KqDBHelper(this.conn);
                    isCorrect = !kqDB.isRecordExist("q03", sqlWhr.toString());
                } else if (infoList != null && infoList.size() > 0) //infoList 为排班对象
                {
                    isCorrect = getGroupDailyDataState(infoList, z1, z3);
                } else {
                    isCorrect = getOrgKqDailyDataState(codesetid, codeitemid, z1, z3);//单位、部门、职位、
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }

    /**
     * 判断人员的考勤数据状态
     * @param codeitemid
     * @param startD
     * @param endD
     * @return
     */
    private boolean getOrgKqDailyDataState(String codesetid, String codeitemid, String startD, String endD) {
        String org_str = "";
        if (codesetid.indexOf("UN") != -1) {
            org_str = "b0110";
        } else if (codesetid.indexOf("UM") != -1) {
            org_str = "e0122";
        } else if (codesetid.indexOf("@K") != -1) {
            org_str = "e01a1";
        }
        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.conn);
        HashMap hashMap = null;
        StringBuffer sb = null;
        boolean isCorrect = true;
        ContentDAO dao = new ContentDAO(conn);
        ResultSet rSet = null;
        try {
            ArrayList org_list = baseClassShift.getOrgid_listFrom(codeitemid, codesetid);//编码
            for (int i = 0; i < org_list.size(); i++) {
                hashMap = new HashMap();
                sb = new StringBuffer();
                String org_id = org_list.get(i).toString();
                ArrayList nbaselist = RegisterInitInfoData.getB0110Dase(hashMap, this.userView, this.conn, org_id);//人员库
                for (int j = 0; j < nbaselist.size(); j++) {
                    String nbase = (String) nbaselist.get(j);
                    String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
                    sb.setLength(0);
                    //日明细
                    sb.append("select a0101");
                    sb.append(" from Q03");
                    sb.append(" where " + org_str + " like '" + org_id + "%'");
                    sb.append(" and nbase = '" + nbase + "'");
                    sb.append(" and q03z0 >= '" + startD + "' and q03z0 <= '" + endD + "'");
                    sb.append(" and q03z5 not in('01','07')");
                    sb.append(" and exists (");
                    sb.append(" select a0100 " + whereIN);
                    if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                        sb.append(" and " + nbase + "A01.a0100 = Q03.a0100 )");
                    } else {
                        sb.append("where " + nbase + "A01.a0100 = Q03.a0100 )");
                    }
                    rSet = dao.search(sb.toString());
                    if (rSet.next()) {
                        isCorrect = false;
                        return isCorrect;
                    }
                    /*//申请表
                    if (isCorrect) 
                    {
                    	String [] tables = {"q11","q13","q15","q19","q25"};
                    	for (int k = 0; k < tables.length; k++) 
                    	{
                    		sb.setLength(0);
                    		String tableName = tables[k];
                    		sb.append("select a0101");
                    		sb.append(" from ");
                    		sb.append(tableName);
                    		sb.append(" where " + org_str + "= '" + org_id + "'");
                    		sb.append(" and nbase = '" + nbase + "'");
                    		
                    		Date startDate = OperateDate.strToDate(startD.replace(".","-"), "yyyy-MM-dd");
                    		Date endDate = OperateDate.strToDate(endD.replace(".", "-"), "yyyy-MM-dd");
                    		String z1 = DateUtils.format(startDate, "yyyy.MM.dd HH:mm:ss");
                    		String z3 = DateUtils.format(endDate, "yyyy.MM.dd HH:mm:ss");
                    		sb.append(" and(( " + tableName + "z1>= " + Sql_switcher.dateValue(z1));
                    		sb.append(" and " + tableName + "z1>= " + Sql_switcher.dateValue(z3) + ")");
                    		sb.append(" or( " + tableName + "z3>= " + Sql_switcher.dateValue(z1));
                    		sb.append(" and " + tableName + "z3>= " + Sql_switcher.dateValue(z3) + "))");
                    		
                    		sb.append(" and " + tableName + "z5 not in('01','07')");

                    		sb.append(" and exists (");
                    		sb.append(" select a0100 " + whereIN);
                    		if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where")!= -1) 
                    		{
                    			sb.append(" and " + nbase + "A01.a0100 = " + tableName + ".a0100 )");
                    		}else 
                    		{
                    			sb.append("where " + nbase + "A01.a0100 = " + tableName + ".a0100 )");
                    		}
                    		System.err.println(sb.toString());
                    		rSet = dao.search(sb.toString());
                    		if (rSet.next()) 
                    		{
                    			isCorrect = false;
                    			return isCorrect;
                    		}
                    	}
                    	
                    }*/

                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rSet);
        }
        return isCorrect;
    }

    /**
     * 周期排班、个人排班 针对排班对象的考勤数据的判断
     * @param infoList
     * @param startD
     * @param endD
     * @return
     */
    public boolean getGroupDailyDataState(ArrayList infoList, String startD, String endD) {
        boolean isC = true;
        ResultSet rs = null;
        StringBuffer sb;

        try {
	        Object oString = infoList.get(0);
	        ContentDAO dao = new ContentDAO(conn);
	        if (oString instanceof String && ((String)oString).indexOf("`") == -1)//班组 
	        {
	            for (int i = 0; i < infoList.size(); i++) {
	                sb = new StringBuffer();
	                String groupId = (String) infoList.get(i);
	                sb.append("select 1 from Q03 Q");
	                sb.append(" WHERE Q.q03z0 >= '" + startD + "' and Q.q03z0 <= '" + endD + "'");
	                sb.append(" AND Q.q03z5 not in('01','07')");
	                sb.append(" AND EXISTS(SELECT 1 FROM kq_group_emp A");
	                sb.append(" WHERE A.group_id = '").append(groupId).append("'");
	                sb.append(" AND Q.a0100 = A.a0100 and Q.nbase = A.nbase)");
                    rs = dao.search(sb.toString());
                    if (rs.next()) {
                        isC = false;
                        return isC;
                    }
	            }
	        } else {
	            ArrayList list;
	            ArrayList lists = new ArrayList();
	            sb = new StringBuffer();
	            sb.append("select * from q03");
	            sb.append(" where nbase = ? and a0100 = ?");
	            sb.append(" and q03z0 >= '" + startD + "' and q03z0 <= '" + endD + "'");
	            sb.append(" and q03z5 not in('01','07')");
	            for (int i = 0; i < infoList.size(); i++) {
	            	String nbase = "";
	            	String a0100 = "";
	                Object info = infoList.get(i);
	                if (info instanceof String) {
	                	String[] arr = ((String)info).split("`");
	                	nbase = arr[0];
	                	a0100 = arr[1];
					}else if (info instanceof LazyDynaBean) {
						nbase = (String) ((LazyDynaBean)info).get("nbase");
						a0100 = (String) ((LazyDynaBean)info).get("a0100");
					}
	                list = new ArrayList();
	                list.add(nbase);
	                list.add(a0100);
	                lists.add(list);
                    rs = dao.search(sb.toString(), list);
                    if (rs.next()) {
                        isC = false;
                        return isC;
                    }
	            }
	        }
        } catch (SQLException e) {
        	e.printStackTrace();
        } finally {
        	KqUtilsClass.closeDBResource(rs);
        }
        return isC;
    }

    /**
     * 判断申请人的考勤数据状态
     * 
     * @param nbase
     * @param a0100
     * @param z1D
     * @param z3D
     * @return
     */
    public boolean getKqDataState(String nbase, String a0100, Date z1D, Date z3D) {
        return true;
        /*
        String z1 = DateUtils.format(z1D, "yyyy.MM.dd HH:mm:ss");
        String z3 = DateUtils.format(z3D, "yyyy.MM.dd HH:mm:ss");
        StringBuffer sql = new StringBuffer();
        sql.append("select * from kq_duration where ");
        sql.append(" (kq_start>" + Sql_switcher.dateValue(z1));
        sql.append(" and kq_start<" + Sql_switcher.dateValue(z3) + ")");
        sql.append(" or (kq_end>" + Sql_switcher.dateValue(z1));
        sql.append(" and kq_end<" + Sql_switcher.dateValue(z3) + ")");
        sql.append(" or (kq_start<=" + Sql_switcher.dateValue(z1));
        sql.append(" and kq_end>=" + Sql_switcher.dateValue(z3) + ")");
        sql.append(" order by kq_year,kq_duration");
        Date start = null;
        Date end = null;
        boolean isCorrect = true;
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                start = rs.getDate("kq_start");
                end = rs.getDate("kq_end");
                z1 = DateUtils.format(start, "yyyy.MM.dd");
                z3 = DateUtils.format(end, "yyyy.MM.dd");
                sql = new StringBuffer();
                sql.append("select * from q03 where nbase='" + nbase + "' and a0100='" + a0100 + "'");
                sql.append(" and q03z0>='" + z1 + "' and q03z0<='" + z3 + "'");
                sql.append(" and q03z5 not in('01','07')");
                rs = dao.search(sql.toString());
                if (rs.next())
                    isCorrect = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect; 
        */
    }

    /**
     * 返回请假申请的时间
     * 
     * @param start_date
     * @param end_date
     * @param a0100
     * @param nbase
     * @param b0110
     * @param kqItem_hash
     * @return
     */
    public float getLeaveTimeHours(Date start_date, Date end_date, String a0100, String nbase, String b0110, HashMap kqItem_hash)
            throws GeneralException {
        KqClassArray kqClassArray = new KqClassArray(this.conn);
        int num = RegisterDate.diffDate(start_date, end_date);
        String class_id = "";
        float timeLen = 0;
        float timeValue = 0;
        try {
            for (int m = 0; m <= num; m++) {
                String op_date_to = getDateByAfter(start_date, m);
                class_id = getClassId(op_date_to, a0100, nbase);
                if (class_id == null || class_id.length() <= 0) {
                    /*
                     * String message="该假期为管理假期类型,'"+op_date_to+"'没排班,请先排班！";
                     * throw GeneralExceptionHandler.Handle(new
                     * GeneralException("",message,"",""));
                     */
                    timeValue = timeValue + 8;// 8小时
                    continue;
                }
                RecordVo vo = kqClassArray.getClassMessage(class_id);
                if (vo == null) {
                    continue;
                }
                Date d_curDate = DateUtils.getDate(op_date_to, "yyyy.MM.dd");
                if ("0".equals(class_id)) {
                    String has_rest = (String) kqItem_hash.get("has_rest");
                    if (has_rest == null || has_rest.length() <= 0) {
                        has_rest = "0";
                    }
                    if ("0".equals(has_rest)) {
                        continue;
                    } else if ("1".equals(has_rest)) {
                        String op_date = getDateByAfter(start_date, m - 1);
                        String class_id_1 = getClassId(op_date, a0100, nbase);
                        RecordVo vo_1 = kqClassArray.getClassMessage(class_id_1);
                        Date op_uDate = DateUtils.getDate(op_date, "yyyy.MM.dd");
                        boolean isCorrect = endTimeBH(vo_1, op_uDate, end_date);
                        if (!isCorrect) {
                            timeValue = timeValue + 8;// 8小时
                        }
                        continue;
                    }
                }
                ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);
                if (validateAppOper.is_Feast(d_curDate, d_curDate, b0110)) {
                    String has_feast = (String) kqItem_hash.get("has_feast");
                    if (has_feast == null || has_feast.length() <= 0) {
                        has_feast = "0";
                    }
                    if ("0".equals(has_feast)) {
                        continue;
                    } else if ("1".equals(has_feast)) {
                        String op_date = getDateByAfter(start_date, m - 1);
                        String class_id_1 = getClassId(op_date_to, a0100, nbase);
                        RecordVo vo_1 = kqClassArray.getClassMessage(class_id_1);
                        Date op_uDate = DateUtils.getDate(op_date, "yyyy.MM.dd");
                        boolean isCorrect = endTimeBH(vo_1, op_uDate, end_date);
                        if (!isCorrect) {
                            timeValue = timeValue + 8;// 8小时
                        }
                        continue;
                    }
                }
                HashMap hash = getCurDateTime(vo, d_curDate, start_date, end_date);
                Float timeLenF = (Float) hash.get("timeLen");
                timeLen = timeLenF.floatValue();
                timeValue = timeValue + timeLen / 60;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return timeValue;
    }

    private String checkAppkeyid(String table, String ta) {
        IDGenerator idg = new IDGenerator(2, this.conn);
        String insertid = "";
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            boolean iscorrect = false;
            while (!iscorrect) {
                insertid = idg.getId((table + "." + ta + "01").toUpperCase());
                iscorrect = checkAppkeyid2(table, ta, insertid, dao);
            }
        } catch (GeneralException e) {
        }
        return insertid;
    }

    private boolean checkAppkeyid2(String table, String ta, String id, ContentDAO dao) {
        boolean iscorrect = true;
        RowSet rs = null;
        try {
            String sql = "select 1 from " + table + " where " + ta + "01='" + id + "'";
            rs = dao.search(sql);
            if (rs.next()) {
                iscorrect = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return iscorrect;
    }

    /**
     * 假期反算
     * 
     * @param start
     * @param end
     * @param a0100
     * @param nbase
     * @param q1709
     * @param history
     * @throws GeneralException
     */
    public boolean holsBackfill(String start, String end, String a0100, String nbase, String q1709, String history,
            float leave_time) throws GeneralException {
        q1709 = KqAppInterface.switchTypeIdFromHolidayMap(q1709);
        
        if (history == null || history.length() <= 0) {
            oldHolsBackfill(a0100, nbase, q1709, start, end, leave_time);
            return true;
        }
        boolean isCorrect = true;
        String q1701_1 = "";
        String q1701_2 = "";
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sb = new StringBuffer();
        StringBuffer wb = new StringBuffer();
        start = start.replaceAll("-", ".");
        end = end.replaceAll("-", ".");
        Date start_D = DateUtils.getDate(start, "yyyy.MM.dd HH:mm");
        Date end_D = DateUtils.getDate(end, "yyyy.MM.dd HH:mm");
        String stD = DateUtils.format(start_D, "yyyy.MM.dd");
        String edD = DateUtils.format(end_D, "yyyy.MM.dd");
        String last_balance = KqUtilsClass.getFieldByDesc("q17", "上年结余");// getBalance();
        String last_spare = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
        // 判断假期申请是否跨年
        sb.append("select * from Q17 where a0100='" + a0100 + "'");
        sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
        sb.append(" and q1709='" + q1709 + "'");
        wb.append(sb);
        wb.append(" and q17z1<=" + Sql_switcher.dateValue(stD) + "");
        wb.append(" and q17z3>=" + Sql_switcher.dateValue(stD));
        List listS = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);

        LazyDynaBean recS = null;
        LazyDynaBean recE = null;
        if (listS != null && listS.size() > 0) {
            recS = (LazyDynaBean) listS.get(0);
            q1701_1 = (String) recS.get("q1701");
        } else {
            throwHolidyNotFoundException(stD);
        }

        wb.setLength(0);
        wb.append(sb);
        wb.append(" and q17z1<=" + Sql_switcher.dateValue(edD) + "");
        wb.append(" and q17z3>=" + Sql_switcher.dateValue(edD));
        List listE = ExecuteSQL.executeMyQuery(wb.toString(), this.conn);
        if (listE != null && listE.size() > 0) {
            recE = (LazyDynaBean) listE.get(0);
            q1701_2 = (String) recE.get("q1701");
        } else {
            throwHolidyNotFoundException(edD);
        }
        // 比较是否是同一年
        String historyS[] = history.split(";");
        String array_TOP[] = null;
        String array_LAST[] = null;
        if (historyS != null && historyS.length > 0) {
            array_TOP = historyS[0].split(",");
            if (historyS.length == 2) {
                array_LAST = historyS[1].split(",");
            }
        }
        StringBuffer upl = new StringBuffer();
        if (array_TOP != null && array_TOP.length == 2) {
            String value = array_TOP[0];// 扣除的可休假
            String balance_value = array_TOP[1];// 扣除的上年结余假
            if (value != null && value.length() > 0) {
                float vf = Float.parseFloat(value);
                if (vf > 0) {
                    upl.setLength(0);
                    upl.append("update Q17 set q1705=q1705-" + vf);// 已休
                    upl.append(", q1707=q1707+" + vf);// 可休的
                    upl.append(" where a0100='");
                    upl.append(a0100);
                    upl.append("'");
                    upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                    upl.append(" and q1709='" + q1709 + "'");
                    upl.append(" and q1701='" + q1701_1 + "'");
                    try {
                        dao.update(upl.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                    if (last_balance != null && last_balance.length() > 0) {
                        // 上年剩余
                        int currYear = Integer.parseInt(q1701_1);
                        String nextYear = String.valueOf(currYear + 1);
                        upl.setLength(0);
                        upl.append("update q17 set " + last_balance);
                        upl.append("=(select ");
                        upl.append("  q1707 from q17 m where ");
                        upl.append("m.nbase='" + nbase + "' and m.a0100=q17.a0100 and m.q1701='");
                        upl.append(q1701_1);
                        upl.append("' and m.q1709='");
                        upl.append(q1709);
                        upl.append("') where nbase='" + nbase + "' ");
                        upl.append(" and q1701='" + nextYear + "'");
                        upl.append(" and q1709='" + q1709 + "'");
                        upl.append(" and a0100='" + a0100 + "'");
                        // dao.update(upl.toString());
                        try {
                            dao.update(upl.toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return false;
                        }
                        upl.setLength(0);
                        // 结余
                        upl.append("update q17 set " + last_spare);
                        upl.append("=" + last_spare + "+" + vf);
                        upl.append(" where nbase='" + nbase + "' ");
                        upl.append(" and q1701='" + nextYear + "'");
                        upl.append(" and q1709='" + q1709 + "'");
                        upl.append(" and a0100='" + a0100 + "'");
                        try {
                            dao.update(upl.toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            }
            if (balance_value != null && balance_value.length() > 0 && last_spare != null && last_spare.length() > 0) {
                float vf = Float.parseFloat(balance_value);
                if (vf > 0) {
                    // 更新上年结余字段
                    upl.setLength(0);
                    upl.append("update Q17 set " + last_spare + "=" + last_spare + "+" + vf);
                    upl.append(" where a0100='");
                    upl.append(a0100);
                    upl.append("'");
                    upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                    upl.append(" and q1709='" + q1709 + "'");
                    upl.append(" and q1701='" + q1701_1 + "'");
                    try {
                        dao.update(upl.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        if (!q1701_1.equals(q1701_2) && array_LAST != null && array_LAST.length == 2) {
            String value = array_LAST[0];// 扣除的可休假
            String balance_value = array_LAST[1];// 扣除的上年结余假
            if (value != null && value.length() > 0) {
                float vf = Float.parseFloat(value);
                if (vf > 0) {
                    upl.setLength(0);
                    upl.append("update Q17 set q1705=q1705-" + vf);
                    upl.append(", q1707=q1707+" + vf);
                    upl.append(" where a0100='");
                    upl.append(a0100);
                    upl.append("'");
                    upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                    upl.append(" and q1709='" + q1709 + "'");
                    upl.append(" and q1701='" + q1701_2 + "'");
                    try {
                        dao.update(upl.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                    if (last_balance != null && last_balance.length() > 0) {
                        // 上年剩余
                        int currYear = Integer.parseInt(q1701_2);
                        String nextYear = String.valueOf(currYear + 1);
                        upl.setLength(0);
                        upl.append("update q17 set " + last_balance);
                        upl.append("=(select ");
                        upl.append("  q1707 from q17 m where ");
                        upl.append("m.nbase='" + nbase + "' and m.a0100=q17.a0100 and m.q1701='");
                        upl.append(q1701_2);
                        upl.append("' and m.q1709='");
                        upl.append(q1709);
                        upl.append("') where nbase='" + nbase + "' ");
                        upl.append(" and q1701='" + nextYear + "'");
                        upl.append(" and q1709='" + q1709 + "'");
                        upl.append(" and a0100='" + a0100 + "'");
                        // dao.update(upl.toString());
                        try {
                            dao.update(upl.toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return false;
                        }
                        upl.setLength(0);
                        // 结余
                        upl.append("update q17 set " + last_spare);
                        upl.append("=" + last_spare + "+" + vf);
                        upl.append(" where nbase='" + nbase + "' ");
                        upl.append(" and q1701='" + nextYear + "'");
                        upl.append(" and q1709='" + q1709 + "'");
                        upl.append(" and a0100='" + a0100 + "'");
                        try {
                            dao.update(upl.toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            }
            if (balance_value != null && balance_value.length() > 0) {
                float vf = Float.parseFloat(balance_value);
                if (vf > 0) {
                    upl.setLength(0);
                    upl.append("update Q17 set " + last_spare + "=" + last_spare + "+" + vf);
                    upl.append(" where a0100='");
                    upl.append(a0100);
                    upl.append("'");
                    upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
                    upl.append(" and q1709='" + q1709 + "'");
                    upl.append(" and q1701='" + q1701_2 + "'");
                    try {
                        dao.update(upl.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return isCorrect;
    }

    /**
     * 老版本驳回已批假期管理申请
     * 
     * @param a0100
     * @param nbase
     * @param q1709
     * @param start
     * @param end
     * @param leave_time
     * @throws GeneralException
     */
    public boolean oldHolsBackfill(String a0100, String nbase, String q1709, String start, String end, float leave_time)
            throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer upl = new StringBuffer();
        float dd = 0;
        float td = 0;
        StringBuffer sb = new StringBuffer();
        RowSet rs = null;
        boolean isCorrect = true;
        try {
            sb.append("select * from Q17 where a0100='");
            sb.append(a0100);
            sb.append("'");
            sb.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            sb.append(" and q17z1<=" + Sql_switcher.dateValue(start) + "");
            sb.append(" and q17z3>=" + Sql_switcher.dateValue(end));
            sb.append(" and q1709='" + q1709 + "'");
            String q1701 = "";
            rs = dao.search(sb.toString());

            if (!rs.next()) {
                return isCorrect;
            }

            td = rs.getFloat("q1705");// 已休
            dd = rs.getFloat("q1707");// 可休
            q1701 = rs.getString("q1701");

            float value1 = td - leave_time;
            float value2 = dd + leave_time;
            upl.append("update Q17 set q1705=");
            upl.append(value1);
            upl.append(", q1707=");
            upl.append(value2);
            upl.append(" where a0100='");
            upl.append(a0100);
            upl.append("'");
            upl.append(" and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            upl.append(" and q1709='" + q1709 + "'");
            upl.append(" and q17z1<=" + Sql_switcher.dateValue(start) + "");
            upl.append(" and q17z3>=" + Sql_switcher.dateValue(end));
            dao.update(upl.toString());
            String last_balance = KqUtilsClass.getFieldByDesc("q17", "上年结余");// getBalance();
            String last_spare = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
            if (last_balance != null && last_balance.length() > 0) {
                // 上年剩余
                int currYear = Integer.parseInt(q1701);
                String nextYear = String.valueOf(currYear + 1);
                upl.setLength(0);
                upl.append("update q17 set " + last_balance);
                upl.append("=(select ");
                upl.append("  q1707 from q17 m where ");
                upl.append("m.nbase='" + nbase + "' and m.a0100=q17.a0100 and m.q1701='");
                upl.append(q1701);
                upl.append("' and m.q1709='");
                upl.append(q1709);
                upl.append("') where nbase='" + nbase + "' ");
                upl.append(" and q1701='" + nextYear + "'");
                upl.append(" and q1709='" + q1709 + "'");
                upl.append(" and a0100='" + a0100 + "'");
                dao.update(upl.toString());
                upl.setLength(0);
                // 结余
                upl.append("update q17 set " + last_spare);
                upl.append("=" + last_spare + "+" + leave_time);
                upl.append(" where nbase='" + nbase + "' ");
                upl.append(" and q1701='" + nextYear + "'");
                upl.append(" and q1709='" + q1709 + "'");
                upl.append(" and a0100='" + a0100 + "'");
                dao.update(upl.toString());
            }
            // System.out.println(upl.toString());
        } catch (Exception sqle) {
            sqle.printStackTrace();
            isCorrect = false;
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }

    /**
     * 批量反算
     * 
     * @param time//审批时间
     * @param a0100
     * @param nbase
     * @param q1709
     */
    public void bachReStatHols(String time, String a0100, String b0110, String nbase, String q1709, HashMap kqItem_hash)
            throws GeneralException {
        q1709 = KqAppInterface.switchTypeIdFromHolidayMap(q1709);
        
        StringBuffer sql = new StringBuffer();
        float[] holiday_rules = getHoliday_minus_rule();// 年假假期规则
        sql.append("select b0110,q15z1,q15z3,history,q1501 from q15");
        sql.append(" where a0100='" + a0100 + "'");
        sql.append(" and nbase='" + nbase + "'");
        //zxj 20150326 当q15z7有毫秒时，会把自己也查出来，导致年假多补。特改一种比较方法。        
        sql.append(" and " + Sql_switcher.dateToChar("q15z7", "yyyy-mm-dd HH24:mi:ss") + ">'" + time + "'"); 
        sql.append(" and q15z0='01' and q15z5='03'");
        sql.append(" and q1517<>1");
        sql.append(" and q1503='" + q1709 + "'");
        sql.append(" order by q15z1");
        List list = ExecuteSQL.executeMyQuery(sql.toString(), this.conn);// 得到大于删除或销假的记录的其他该类型假期记录
        
        //第一步：回补本次时间节点起此人所有已扣年假，扣减记录在请假申请history中
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean rs = (LazyDynaBean) list.get(i);
                String history = (String) rs.get("history");
                if (history == null || history.length() <= 0) {
                    continue;
                }
                
                String start = (String) rs.get("q15z1");
                String end = (String) rs.get("q15z3");
                holsBackfill(start, end, a0100, nbase, q1709, history, 0);
            }
        }

        //第二步：重新扣减本次时间节点起此人所有年假（包括销假），具体扣减时数重新计算并保存到请假申请history中
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowset = null;
        try {
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    LazyDynaBean rs = (LazyDynaBean) list.get(i);
                    
                    //String history = (String) rs.get("history");
                    //if (history == null || history.length() <= 0)
                    //    continue;
                    
                    //申请单号
                    String q1501 = (String) rs.get("q1501");
                    
                    //申请起始时间
                    String start = (String) rs.get("q15z1");
                    start = start.replaceAll("-", ".");
                    Date startDate = DateUtils.getDate(start, "yyyy.MM.dd HH:mm");
                    
                    //申请结束时间
                    String end = (String) rs.get("q15z3");                    
                    end = end.replaceAll("-", ".");                    
                    Date endDate = DateUtils.getDate(end, "yyyy.MM.dd HH:mm");
                    
                    
                    //float leave_time = getHistoryLeaveTime(start_D, end_D, a0100, nbase, b0110, kqItem_hash, holiday_rules);
                    //history = upLeaveManage(a0100, nbase, q1709, start, end, leave_time, "", b0110, kqItem_hash, holiday_rules);// 减去假期管理可修改天数
                    
                    String history = "0.0,0.0;0.0,0.0";
                    
                    //有销假的，需要重新组合申请时间进行计算扣减
                    rowset = dao.search("select q15z1,q15z3 from q15 where q1519='" + q1501
                            + "' and q15z0='01' and q15z5='03' and q1517=1");
                    
                    // 没有销假，按申请单计算扣减时数
                    if (!rowset.next()) {
                    	//按规则计算申请假期时长
                        float leave_time = calcLeaveAppTimeLen(nbase, a0100, b0110, startDate, endDate, kqItem_hash, holiday_rules, Integer.MAX_VALUE);
                    	history = upLeaveManage(a0100, nbase, q1709, start, end, leave_time, "", b0110, kqItem_hash, holiday_rules);
                    } else {
                        Date start_d = rowset.getTimestamp("q15z1");
                        if (startDate.after(start_d)) {
                            start_d = startDate;
                        }
                        String c_start = DateUtils.format(start_d, "yyyy.MM.dd HH:mm:ss");
                        
                        Date end_d = rowset.getTimestamp("q15z3");
                        if (endDate.before(end_d)) {
                            end_d = endDate;
                        }
                        String c_end = DateUtils.format(end_d, "yyyy.MM.dd HH:mm:ss");
                        
                        //有销假时，由于销假有三种情况，销头、中、尾三段
                        //只需计算申请单开始时间~销假开始时间，销假结束时间~申请单结束时间两段的时长即可
                        
                        //销假结束时间~申请单结束时间
                        String historyHead = "0.0,0.0;0.0,0.0";
                        if (!startDate.equals(start_d)) {
                        	float leave_time = calcLeaveAppTimeLen(nbase, a0100, b0110, startDate, start_d, kqItem_hash, holiday_rules, Integer.MAX_VALUE);
                        	historyHead = upLeaveManage(a0100, nbase, q1709, start, c_start, leave_time, "", b0110, kqItem_hash, holiday_rules);
                        }
                        
                        //申请单开始时间~销假开始时间
                        String historyTail = "0.0,0.0;0.0,0.0";
                        if (!endDate.equals(end_d)) {
                        	float leave_time = calcLeaveAppTimeLen(nbase, a0100, b0110, end_d, endDate, kqItem_hash, holiday_rules, Integer.MAX_VALUE);
                        	historyTail = upLeaveManage(a0100, nbase, q1709, c_end, end, leave_time, "", b0110, kqItem_hash, holiday_rules);
                        }
                        
                        //合并头尾两段扣减情况
                        float curYearValue = 0.0f;
                        float curYearBalance = 0.0f;
                        float preYearValue = 0.0f;
                        float preYearBalance = 0.0f;
                        
                        if(historyHead != null && !"".equals(historyHead)) {
                            String historyS[] = historyHead.split(";");
                            
                            if (historyS != null && historyS.length == 2) {
	                            String curYearHistory[] = historyS[0].split(",");	                            
	                            if (curYearHistory != null && curYearHistory.length == 2) {
		                            curYearValue = Float.parseFloat(curYearHistory[0]);
		                            curYearBalance = Float.parseFloat(curYearHistory[1]);
	                            }
	                            
	                            String preYearHistory[] = historyS[1].split(",");
	                            if (preYearHistory != null && preYearHistory.length == 2) {
		                            preYearValue = Float.parseFloat(preYearHistory[0]);
		                            preYearBalance = Float.parseFloat(preYearHistory[1]);
	                            }
                            }
                        }
                        
                        float curYearValueTail = 0.0f;
                        float curYearBalanceTail = 0.0f;
                        float preYearValueTail = 0.0f;
                        float preYearBalanceTail = 0.0f;
                        
                        if(historyTail != null && !"".equals(historyTail)) {
                            String historyS[] = historyTail.split(";");
                            
                            String curYearHistory[] = historyS[0].split(",");
                            if (curYearHistory != null && curYearHistory.length == 2) {
	                            curYearValueTail = Float.parseFloat(curYearHistory[0]);
	                            curYearBalanceTail = Float.parseFloat(curYearHistory[1]);
                            }
                            
                            String preYearHistory[] = historyS[1].split(",");
                            if (preYearHistory != null && preYearHistory.length == 2) {
	                            preYearValueTail = Float.parseFloat(preYearHistory[0]);
	                            preYearBalanceTail = Float.parseFloat(preYearHistory[1]);
                            }
                        }
                        
                        history = "" + (curYearValue +  curYearValueTail) + "," + (curYearBalance + curYearBalanceTail)
                                + ";" + (preYearValue +  preYearValueTail) + "," + (preYearBalance + preYearBalanceTail);
                        
                        /*
                        float cancel_leave_time = getHistoryLeaveTime(start_d, end_d, a0100, nbase, b0110, kqItem_hash,
                                holiday_rules);
                        if (cancel_leave_time > 0) {
                            float history_CancelF[] = getCancelHolsTimeManage(a0100, nbase, q1709, c_start, c_end,
                                    cancel_leave_time, b0110, kqItem_hash, start, end, holiday_rules);
                            String history_CancelS = history_CancelF[0] + "," + history_CancelF[1] + ";" + history_CancelF[2]
                                    + "," + history_CancelF[3];
                            String array_TOP[] = null;
                            String array_LAST[] = null;
                            String historyS[] = history.split(";");
                            if (historyS != null && historyS.length > 0) {
                                array_TOP = historyS[0].split(",");
                                if (historyS.length == 2)
                                    array_LAST = historyS[1].split(",");
                            }
                            float vf_top = 0;
                            float bvf_top = 0;
                            if (array_TOP != null && array_TOP.length == 2) {
                                String value = array_TOP[0];// 扣除的可休假
                                String balance_value = array_TOP[1];// 扣除的上年结余假
                                vf_top = Float.parseFloat(value);
                                bvf_top = Float.parseFloat(balance_value);
                                if (vf_top > 0 && history_CancelF[1] > 0)
                                    vf_top = vf_top - history_CancelF[0];
                                if (bvf_top > 0 && history_CancelF[1] > 0)
                                    bvf_top = bvf_top - history_CancelF[1];
                            }
                            float vf_last = 0;
                            float bvf_last = 0;
                            if (array_LAST != null && array_LAST.length == 2) {
                                String value = array_LAST[0];// 扣除的可休假
                                String balance_value = array_LAST[1];// 扣除的上年结余假
                                vf_last = Float.parseFloat(value);
                                bvf_top = Float.parseFloat(balance_value);
                                if (vf_last > 0 && history_CancelF[0] > 0)
                                    vf_last = vf_last - history_CancelF[0];
                                if (bvf_last > 0 && history_CancelF[1] > 0)
                                    bvf_last = bvf_last - history_CancelF[1];
                            }
                            // 反算
                            holsBackfill(DateUtils.format(start_d, "yyyy.MM.dd HH:mm"), DateUtils.format(end_d,
                                    "yyyy.MM.dd HH:mm"), a0100, nbase, q1709, history_CancelS, 0);
                            history = vf_top + "," + bvf_top + ";" + vf_last + "," + bvf_last;
                        }
                        */
                    }
                    dao.update("update q15 set history='" + history + "' where q1501='" + q1501 + "'");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowset);
        }
    }

    /**
     * 按考勤假期扣减规则，扣减时间
     * 
     * @param time
     * @return
     */
    public float getHoliday_Minus_Rule(float time) {
        if (holiday_Minus_Rule == null || holiday_Minus_Rule.length() < 3) {
            String rule = KqParam.getInstance().getHolidayMinusRule();
            if (rule == null || rule.length() < 3) {
                return time;
            }
            this.holiday_Minus_Rule = rule;
        }
        String ruleArr[] = this.holiday_Minus_Rule.split(";");// 4.6,0.5；4.6,8,1；8,1
        if (ruleArr.length != 3) {
            return time;
        }
        try {
            String less_Arr[] = ruleArr[0].split(",");// 小于
            String equal_Arr[] = ruleArr[1].split(",");// 大于等于小于等于
            String greater_Arr[] = ruleArr[2].split(",");// 大于
            float less_index = 0;// 小于
            float less_value = 0;
            if (less_Arr.length == 2) {
                less_index = Float.parseFloat(less_Arr[0]);
                less_value = Float.parseFloat(less_Arr[1]);
            } else {
                return time;
            }
            // 等于
            float less_equal_index = 0;
            float greater_equal_index = 0;
            float equal_value = 0;
            if (equal_Arr.length == 3) {
                less_equal_index = Float.parseFloat(equal_Arr[0]);
                greater_equal_index = Float.parseFloat(equal_Arr[1]);
                equal_value = Float.parseFloat(equal_Arr[2]);
            } else {
                return time;
            }
            // 大于
            float greater_index = 0;
            float greater_value = 0;
            if (equal_Arr.length == 2) {
                greater_index = Float.parseFloat(greater_Arr[0]);
                greater_value = Float.parseFloat(greater_Arr[1]);
            } else {
                return time;
            }
            if (less_index > time)// 当time小于参数时
            {
                time = less_value;
            } else if (less_equal_index <= time && greater_equal_index >= time)// 当time大于等于参数1，小于等于参数2时
            {
                time = equal_value;
            } else if (greater_index < time)// 当time大于时
            {
                time = greater_value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return time;
    }

    /**
     * 根据可休天数，反算中间隔了多少天
     * 
     * @param start_date
     * @param time
     * @param a0100
     * @param nbase
     * @param b0110
     * @param kqItem_hash
     * @return
     * @throws GeneralException
     */
    public Date getNextTime(Date start_date, float time, String a0100, String nbase, String b0110, HashMap kqItem_hash)
            throws GeneralException {
        KqClassArray kqClassArray = new KqClassArray(this.conn);
        int day = 0;
        String class_id = "";
        Date dateD = (Date) start_date.clone();
        try {
            while (day > 0) {
                String op_date_to = getDateByAfter(dateD, 0);
                class_id = getClassId(op_date_to, a0100, nbase);
                if (class_id == null || class_id.length() <= 0) {
                    dateD = DateUtils.addDays(dateD, 1);
                    continue;
                }
                RecordVo vo = kqClassArray.getClassMessage(class_id);
                if (vo == null) {
                    dateD = DateUtils.addDays(dateD, 1);
                    continue;
                }
                Date d_curDate = DateUtils.getDate(op_date_to, "yyyy.MM.dd");
                if (class_id == null || class_id.length() <= 0 || "0".equals(class_id)) {
                    ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);
                    if (validateAppOper.is_Feast(d_curDate, d_curDate, b0110)) {
                        String has_feast = (String) kqItem_hash.get("has_feast");
                        if (has_feast == null || has_feast.length() <= 0) {
                            has_feast = "0";
                        }
                        if ("0".equals(has_feast)) {
                            dateD = DateUtils.addDays(dateD, 1);
                            continue;
                        } else if ("1".equals(has_feast)) {
                            dateD = DateUtils.addDays(dateD, 1);
                            day--;
                            continue;
                        }
                    } else {
                        String has_rest = (String) kqItem_hash.get("has_rest");
                        if (has_rest == null || has_rest.length() <= 0) {
                            has_rest = "0";
                        }
                        if ("0".equals(has_rest)) {
                            dateD = DateUtils.addDays(dateD, 1);
                            continue;
                        } else if ("1".equals(has_rest)) {
                            dateD = DateUtils.addDays(dateD, 1);
                            day--;
                            continue;
                        }
                    }
                } else {
                    dateD = DateUtils.addDays(dateD, 1);
                    day--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return dateD;
    }

    /**
     * 得到假期扣减规则 小于 str[0] 小时 按 str[1] 天计 大于等于 str[2] 且 小于 str[3] 小时 按 str[4] 天计
     * 大于等于 str[5] 小时 按 str[6] 天计
     * 考勤减扣规则holiday_minus_rule “1”为标准工时，“0”为按实际时长
     * @return
     */
    public float[] getHoliday_minus_rule() {
    	
        String holiday_minus_rule = KqParam.getInstance().getHolidayMinusRule();// 考勤减扣规则“1”为标准工时，“0”为按实际时长
        if (holiday_minus_rule == null || holiday_minus_rule.length() <= 0) {
            return null;
        }
        holiday_minus_rule = PubFunc.keyWord_reback(holiday_minus_rule);
        if ("1".equals(holiday_minus_rule) || "0".equals(holiday_minus_rule))
		{
        	 float[] rule_f = new float[1];
        	 rule_f[0] = Float.valueOf(holiday_minus_rule).floatValue();
        	 return rule_f;
		}
		else
		{
	        String holid = "";
	        if (holiday_minus_rule != null) {
	            String[] str = holiday_minus_rule.split(";");
	            for (int i = 0; i < str.length; i++) {
	                if (str[i] == null || str[i].length() <= 0) {
                        return null;
                    }
	                holid += "," + str[i];
	            }
	            holid = holid.substring(1);
	        }
	        String[] str = holid.split(",");
	        if (str != null && str.length == 7) {
	
	            float[] rule_f = new float[7];
	            for (int i = 0; i < str.length; i++) {
	                rule_f[i] = Float.parseFloat(str[i]);
	            }
	            return rule_f;
	        } else {
                return null;
            }

		}
    }

    /**
     * rule[] 规则 按假期扣减规则算数 小于 str[0] 小时 按 str[1] 天计 大于等于 str[2] 且 小于 str[3] 小时 按
     * str[4] 天计 大于等于 str[5] 小时 按 str[6] 天计 time 一天申请的时间长度 time_sum 当天的排班的班时
     * 
     * @return
     */
    public float re_Holiday_minus_rule_value(float[] rule, float time, float time_sum) {
        if (time <= 0) {
            return 0;
        }
        
        if (rule == null || rule.length != 7) {
            return time / time_sum;
        }
        
        //zxj 20150210 定义了扣减规则的应该按规则计算，与CS保持一致
        //if (time / time_sum >= 1)
        //    return time / time_sum;
        
        time = time / 60;
        if (rule[0] > time) {
            return rule[1];
        } else if (rule[2] <= time && rule[3] > time) {
            return rule[4];
        } else if (rule[5] <= time) {
            return rule[6];
        }
        return time;
    }
    
    private void throwHolidyNotFoundException(String strDate) throws GeneralException {
        throw new GeneralException("没有找到包含时间点\"" + strDate + "\"的假期信息，请检查用户是否享受该假期类型！");
    }
    
    /**
     * 计算请假或公出申请时长（按申请类型在考勤规则中定义的单位）  
     * @Title: calcLeaveAppTimeLen   
     * @Description: 计算请假或公出申请时长（按申请类型在考勤规则中定义的单位）   
     * @param @param nbase 人员库
     * @param @param a0100 人员编号
     * @param @param b0110 人员所在单位
     * @param @param startTime 申请起始时间
     * @param @param endTime   申请结束时间
     * @param @param kqItemHash 申请类型考勤规则
     * @param @param holidayRules 年假扣减规则
     * @param @param timeLimit 申请时长限制
     * @param @return
     * @param @throws GeneralException 
     * @return float    
     * @throws
     */
    public float calcLeaveAppTimeLen(String nbase, String a0100, String b0110, 
            Date startTime, Date endTime,  
            HashMap kqItemHash, float[] holidayRules,
            float timeLimit) throws GeneralException {
        
        // 高校医院班考勤计算时长
        if (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL) {
            LazyDynaBean appInfo = new LazyDynaBean();
            appInfo.set("nbase", nbase);
            appInfo.set("a0100", a0100);
            appInfo.set("type", kqItemHash.get("item_id"));
            appInfo.set("starttime", startTime);
            appInfo.set("endtime", endTime);
            
            KqAppCaculator kqAppCalculator = new KqAppCaculator(conn);
            return (float)kqAppCalculator.calcAppTimeLen(appInfo);
        }
        
        KqClassArray kqClassArray = new KqClassArray(this.conn);
        //开始日期应从前一天算起，前一天可能是跨天班
        Date factStartDate = DateUtils.addDays(startTime, -1); 
        int num = RegisterDate.diffDate(factStartDate, endTime);
        
        String class_id = "";
        String yesterdayClassEndTime = ""; 
        float timeLen = 0;
        float time_sum = 0;
        float timeValue = 0;
        try {
            KqUtilsClass kqUtil = new KqUtilsClass(this.conn);
            //申请类型规则单位
            String itemUnit = PubFunc.DotstrNull((String)kqItemHash.get("item_unit"));
            itemUnit = "".equals(itemUnit) ? KqConstant.Unit.DAY : itemUnit;
            
            // 是否包含公休日
            String has_rest = (String) kqItemHash.get("has_rest");
            if (has_rest == null || has_rest.length() <= 0) {
                has_rest = "0";
            }
            
            String has_feast = (String) kqItemHash.get("has_feast");
            if (has_feast == null || has_feast.length() <= 0) {
                has_feast = "0";
            }
            
            ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);             
            for (int m = 0; m <= num; m++) {
                String op_date_to = getDateByAfter(factStartDate, m);
                Date dayFrom = OperateDate.strToDate(op_date_to + " 00:00", "yyyy.MM.dd HH:mm");
                Date d_curDate = DateUtils.getDate(op_date_to, "yyyy.MM.dd");
                class_id = getClassId(op_date_to, a0100, nbase);
                if (class_id == null || class_id.length() <= 0 || "0".equals(class_id)) {
                	if(validateAppOper.is_Feast(d_curDate)){
            			//不包含节假日，直接跳过
	                    if ("0".equals(has_feast)) {
	                        yesterdayClassEndTime = "";
	                        continue;
	                    }
                	}
            		else {
            			//不包含公休日，直接跳过
	                    if ("0".equals(has_rest)) {
	                        yesterdayClassEndTime = "";
	                        continue;
	                    }
					}
                    
                	Date yesterDate = OperateDate.strToDate(yesterdayClassEndTime, "yyyy.MM.dd HH:mm");
                    //如果申请开始时间在前一天内，那么算1天
                    if ((m == 0) 
                            && (-1 == DateUtils.format(startTime, "yyyy.MM.dd HH:mm").compareTo(DateUtils.format(factStartDate, "yyyy.MM.dd 23:59")))) {
                        timeValue = timeValue + tranMinuteValueByUnit(480.0f, itemUnit, 480.0f);
                    } else if (m > 0 && m < num) { //申请中间包含的公休日，算1天,24小时
                        timeValue = timeValue + tranMinuteValueByUnit(24f*60.0f, itemUnit, 24f*60.0f);
                    } else if (m == num //最后一天 如果前一天没有排班，算1天；如果申请结束时间在前一天下班时间后，也算1天
                            && ("".equals(yesterdayClassEndTime) 
                            		// 33990 linbz 申请结束时间大于或等于前一天下班时间，也算1天
                                    || endTime.getTime() >= yesterDate.getTime())){
                    	
                    	float actualLen = 0;
						if ("".equals(yesterdayClassEndTime)) {
							if (itemUnit.equals(KqConstant.Unit.DAY)) {
                                timeValue = timeValue + 1;
                            } else if (num == 1) {
                    			actualLen = actualLen + KQRestOper.getPartMinute(startTime, endTime);
                    			timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);                    			
                    		} else {
								actualLen = actualLen + KQRestOper.getPartMinute(dayFrom, endTime);
								timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);//累计								
							}
						}else if (!"".equals(yesterdayClassEndTime))
						{
							// 33990 linbz 申请结束时间大于或等于前一天下班时间，也算1天
							if (endTime.getTime() >= yesterDate.getTime() && itemUnit.equals(KqConstant.Unit.DAY)) {
                                timeValue = timeValue + 1;
                            } else if (yesterDate.getTime() < dayFrom.getTime()) {
                    			if(num == 1) {
                                    actualLen = actualLen + KQRestOper.getPartMinute(startTime, endTime);
                                } else {
                                    actualLen = actualLen + KQRestOper.getPartMinute(dayFrom, endTime);
                                }
                    			timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);//累计
                    		}
							else {
								if(num == 1){
									if (yesterDate.getTime() < startTime.getTime()) 
									{
										actualLen = actualLen + KQRestOper.getPartMinute(startTime, endTime);
									}else 
									{
										actualLen = actualLen + KQRestOper.getPartMinute(yesterDate, endTime);
									}
								}else if(endTime.getTime() >= yesterDate.getTime())
								{
									actualLen = actualLen + KQRestOper.getPartMinute(yesterDate, endTime);
								}
								timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);//累计
							}
						} 
                    }
                    
                    yesterdayClassEndTime = "";
                    continue;
                }
                
                RecordVo vo = kqClassArray.getClassMessage(class_id);
                if (vo == null) {
                    yesterdayClassEndTime = "";
                    continue;
                }
                
                Map classTime = kqUtil.getTimeAreaInclassById(class_id);
                if (classTime != null) {
                    String classEndTime = (String)classTime.get("endTime");
                    Boolean isOverDay = (Boolean)classTime.get("conday");
                    
                    if (isOverDay.booleanValue()) {
                        yesterdayClassEndTime = DateUtils.FormatDate(DateUtils.addDays(DateUtils.getDate(op_date_to, "yyyy.MM.dd"), 1), "yyyy.MM.dd");
                    } else {
                        yesterdayClassEndTime = op_date_to;
                    }
                        
                    yesterdayClassEndTime = yesterdayClassEndTime + " " + classEndTime;
                }
                
               
                HashMap hash = getCurDateTime(vo, d_curDate, startTime, endTime);
                Float timeLenF = (Float) hash.get("timeLen");
                timeLen = timeLenF.floatValue();
                Float timeSumF = (Float) hash.get("time_sum");
                time_sum = timeSumF.floatValue();
                
                float curtime = 0;
                // rule[] 规则 按假期扣减规则算数 小于 str[0] 小时 按 str[1] 天计 大于等于 str[2] 且 小于 str[3] 小时 按
                // str[4] 天计 大于等于 str[5] 小时 按 str[6] 天计 time 一天申请的时间长度 time_sum 当天的排班的班时
                if (holidayRules != null && holidayRules.length == 7 ) {
                    curtime = re_Holiday_minus_rule_value(holidayRules, timeLen, time_sum);
                    //自定义规则时单位都为天
                    kqItemHash.put("item_unit", KqConstant.Unit.DAY);
                } 
                else if(holidayRules != null && holidayRules.length == 1 && holidayRules[0]== 1){
                	//获取标准工时
                	 time_sum = Float.parseFloat(KqParam.getInstance().getSTANDARD_HOURS()) * 60.0f ;
                	 curtime = tranMinuteValueByUnit(timeLen, itemUnit, time_sum); 
                }
                else {
                	//按实际小时扣减
                    curtime = tranMinuteValueByUnit(timeLen, itemUnit, time_sum);
                }
                curtime = roundNumByItemDecimalWidth(kqItemHash, curtime);
                timeValue = roundNumByItemDecimalWidth(kqItemHash,timeValue + curtime);
               
                if (timeLimit<=0 || timeValue >= timeLimit) {
                    return timeValue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return timeValue;
    }
    
    /**
     *  计算加班申请时长（按申请类型在考勤规则中定义的单位） 
     *    
     * @param @param nbase 人员库
     * @param @param a0100 人员编号
     * @param @param startTime 申请起始时间
     * @param @param endTime   申请结束时间
     * @param @param kqItemHash 申请类型考勤规则
     * @param @param timeLimit 申请时长限制
     * @param @return
     * @param @throws GeneralException 
     * @return float    
     * @throws
     */
    public float calcOverAppTimeLen(String nbase, String a0100, Date startTime, Date endTime,  
            HashMap kqItemHash, float timeLimit) throws GeneralException {
        
        KqClassArray kqClassArray = new KqClassArray(this.conn);
        
        //开始日期应从前一天算起，前一天可能是跨天班
        Date factStartDate = DateUtils.addDays(startTime, -1); 
        int num = RegisterDate.diffDate(factStartDate, endTime);
        //加班类型
        String apptype = (String) kqItemHash.get("item_id");
        String class_id = "";
        String yesterdayClassEndTime = ""; 
        float timeLen = 0;
        float timeValue = 0;
        float time_sum = 0;
        try {
            KqUtilsClass kqUtil = new KqUtilsClass(this.conn);
            
            //申请类型规则单位
            String itemUnit = PubFunc.DotstrNull((String)kqItemHash.get("item_unit"));
            itemUnit = "".equals(itemUnit) ? KqConstant.Unit.DAY : itemUnit;
            
            String has_rest = (String) kqItemHash.get("has_rest");
            if (has_rest == null || has_rest.length() <= 0) {
                has_rest = "0";
            }
            //节假日
            String has_feast = (String) kqItemHash.get("has_feast");
            if (has_feast == null || has_feast.length() <= 0) {
                has_feast = "0";
            }
            ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);      
            for (int m = 0; m <= num; m++) {
                String op_date_to = getDateByAfter(factStartDate, m);
                Date d_curDate = DateUtils.getDate(op_date_to, "yyyy.MM.dd");
                class_id = getClassId(op_date_to, a0100, nbase);
                Date dayFrom = OperateDate.strToDate(op_date_to + " 00:00", "yyyy.MM.dd HH:mm");
                Date dayTo = OperateDate.strToDate(op_date_to + " 24:00", "yyyy.MM.dd HH:mm");
                if (class_id == null || class_id.length() <= 0 || "0".equals(class_id)) {
                    /*
                	//如果是节假日加班
                	if(KqAppInterface.isFeastOvertime(apptype)){
	                	//szk判断是否是节假日
	                	if(!validateAppOper.is_Feast(d_curDate)){
		                	//不包含公休日，直接跳过
		                    if (has_rest.equals("0")) {
		                        yesterdayClassEndTime = "";
		                        continue;
		                    }
	                	}
                	}
                	//如果是公休日加班
                	else if(KqAppInterface.isRestOvertime(apptype)){
	                	//判断是否是公休日
	                	if(validateAppOper.is_Feast(d_curDate)){
		                	//不包含节假日，直接跳过
		                    if (has_feast.equals("0")) {
		                        yesterdayClassEndTime = "";
		                        continue;
		                    }
	                	}
                	}
                	else 
                	{
                		if(validateAppOper.is_Feast(d_curDate)){
                			//不包含节假日，直接跳过
		                    if (has_feast.equals("0")) {
		                        yesterdayClassEndTime = "";
		                        continue;
		                    }
	                	}
                		else {
                			//不包含公休日，直接跳过
		                    if (has_rest.equals("0")) {
		                        yesterdayClassEndTime = "";
		                        continue;
		                    }
						}
                	}
                	*/
                	
                    //如果申请开始时间在前一天内，那么算1天
                    Date yesterDate = OperateDate.strToDate(yesterdayClassEndTime, "yyyy.MM.dd HH:mm");
                    
                    float actualLen = 0;
                    
                    if (m > 0 && m < num) { //申请中间包含的公休日，算1天
                    	if (itemUnit.equals(KqConstant.Unit.DAY)) {
                            timeValue = timeValue + 1;//单位是天，按1天算
                        } else if (startTime.getTime() >= dayFrom.getTime() && startTime.getTime() <= dayTo.getTime()) //起始时间在这一天
						{
							if ("".equals(yesterdayClassEndTime) || startTime.getTime() > yesterDate.getTime()) {
                                actualLen = KQRestOper.getPartMinute(startTime, dayTo);
                            } else {
                                actualLen = KQRestOper.getPartMinute(yesterDate, dayTo);
                            }
							timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);
						}else {
                            timeValue = timeValue + tranMinuteValueByUnit((60.0f * 24f), itemUnit, (60.0f * 24f));//其他的按照一天24小时来算
                        }
                    } else if (m == num){ //最后一天 如果前一天没有排班，算1天；如果申请结束时间在前一天下班时间后，也算1天
                    	
                    	if ("".equals(yesterdayClassEndTime)) 
						{
                    		if (itemUnit.equals(KqConstant.Unit.DAY)) {
                                timeValue = timeValue + 1;
                            } else if (num == 1) {
                    			actualLen = actualLen + KQRestOper.getPartMinute(startTime, endTime);
                    			timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);                    			
                    		} else {
								actualLen = actualLen + KQRestOper.getPartMinute(dayFrom, endTime);
								timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);//累计								
							}
						}else if (!"".equals(yesterdayClassEndTime))
						{
							// 33990 linbz 申请结束时间大于或等于前一天下班时间，也算1天
							if (endTime.getTime() >= yesterDate.getTime() && itemUnit.equals(KqConstant.Unit.DAY)) {
                                timeValue = timeValue + 1;
                            } else if (yesterDate.getTime() < dayFrom.getTime()) {
                    			if(num == 1) {
                                    actualLen = actualLen + KQRestOper.getPartMinute(startTime, endTime);
                                } else {
                                    actualLen = actualLen + KQRestOper.getPartMinute(dayFrom, endTime);
                                }
                    			timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);//累计
                    		}
							else {
								if(num == 1){
									if (yesterDate.getTime() < startTime.getTime()) 
									{
										actualLen = actualLen + KQRestOper.getPartMinute(startTime, endTime);
									}else 
									{
										actualLen = actualLen + KQRestOper.getPartMinute(yesterDate, endTime);
									}
								}else if(endTime.getTime() >= yesterDate.getTime())
								{
									actualLen = actualLen + KQRestOper.getPartMinute(yesterDate, endTime);
								}
								timeValue = timeValue + tranMinuteValueByUnit(actualLen, itemUnit, actualLen);//累计
							}
						} 
                    }
                    
                    yesterdayClassEndTime = "";
                    continue;
                }
                
                RecordVo vo = kqClassArray.getClassMessage(class_id);
                if (vo == null) {
                    yesterdayClassEndTime = "";
                    continue;
                }
                
                Map classTime = kqUtil.getTimeAreaInclassById(class_id);
                boolean isOverDay = false;
                if (classTime != null) {
                    String classEndTime = (String)classTime.get("endTime");
                    isOverDay = ((Boolean)classTime.get("conday")).booleanValue();
                    
                    if (isOverDay) {
                        yesterdayClassEndTime = DateUtils.FormatDate(DateUtils.addDays(DateUtils.getDate(op_date_to, "yyyy.MM.dd"), 1), "yyyy.MM.dd");
                    } else {
                        yesterdayClassEndTime = op_date_to;
                    }
                        
                    yesterdayClassEndTime = yesterdayClassEndTime + " " + classEndTime;
                }
                
               
                String yesterstr = "";
                if (m > 0) 
				{
                	op_date_to = DateUtils.format(OperateDate.addDay(d_curDate,  - 1), "yyyy.MM.dd");
                    class_id = getClassId(op_date_to, a0100, nbase);
                    if("0".equals(class_id) || "".equals(class_id) || class_id == null){
                    	yesterstr = "";
                    }else 
					{
                    	
                    	classTime = kqUtil.getTimeAreaInclassById(class_id);
                    	isOverDay = false;
                    	if (classTime != null) {
                    		String classEndTime = (String)classTime.get("endTime");
                    		isOverDay = ((Boolean)classTime.get("conday")).booleanValue();
                    		
                    		if (isOverDay) {
                    			yesterstr = DateUtils.FormatDate(DateUtils.addDays(DateUtils.getDate(op_date_to, "yyyy.MM.dd"), 1), "yyyy.MM.dd");
                    		} else {
                    			yesterstr = op_date_to;
                    		}
                    		yesterstr = yesterstr + " " + classEndTime;
                    	}
					}
				}
                HashMap hash = getCurDateOverTime(vo, d_curDate, startTime, endTime, m, num, isOverDay, yesterstr, dayFrom, dayTo);
                Float timeLenF = (Float) hash.get("timeLen");
                timeLen = timeLenF.floatValue();
                Float timeSumF = (Float) hash.get("time_sum");
                time_sum = timeSumF.floatValue();
                
                float curtime = 0;
                curtime = tranMinuteValueByUnit(timeLen, itemUnit, time_sum);// timeLen / time_sum;
                curtime = roundNumByItemDecimalWidth(kqItemHash, curtime);
                timeValue = timeValue + curtime;
             
                
                if (timeLimit<=0 || timeValue >= timeLimit) {
                    return timeValue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return timeValue;
    }
    
    /**
     * @Title: tranValueByUnit   
     * @Description: 将传入的分钟值转换为目标单位值   
     * @param minuteValue 分钟值
     * @param targetUnit  目标单位
     * @param baseValue   转换基准
     * @param @return 
     * @return float    
     * @throws
     */
    public float tranMinuteValueByUnit(float minuteValue, String targetUnit, float baseValue) {
    	//szk前一天是跨天班是minuteValue为负数，不能改成0
//        if (minuteValue <= 0)
//            return 0;
 
        //zxj 20140730 时长为0，直接返回0
        if (0 == minuteValue) {
            return 0;
        }
        
        //转换为分钟
        if (KqConstant.Unit.MINUTE.equals(targetUnit)) {
            return minuteValue;
        }
        
        //转换为小时
        if (KqConstant.Unit.HOUR.equals(targetUnit)) {
            return minuteValue / 60;
        }
        
        //转换为天
        if (KqConstant.Unit.DAY.equals(targetUnit)) {
            if (baseValue > 0) {
                return minuteValue / baseValue;
            } else {
                return 1;
            }
        }
        
        //转换为次
        if (KqConstant.Unit.TIMES.equals(targetUnit)) {
            return 1;
        }
        
        return minuteValue;            
    }

    public String getAppTimeLenDesc(String nbase, String a0100, String b0110, 
            Date startTime, Date endTime, String appType){
        String timeLenDesc = "";
        
        //计算请假或公出时长
        try {
            //考勤规则应取改假类自己的规则
            HashMap kqItemHash = count_Leave(appType);
    
            //假期时长扣减规则参数
            float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
            if (KqParam.getInstance().isHoliday(this.conn, b0110, appType)) {
                holidayRules = getHoliday_minus_rule();
            }
            
            float timeLen = 0;
            if("1".equals(appType.substring(0, 1)))//加班
            {
                timeLen = calcOverAppTimeLen(nbase, a0100, startTime, endTime, kqItemHash,  Integer.MAX_VALUE);
            } else//请假、公出
            {
                timeLen = calcLeaveAppTimeLen(nbase, a0100, "", startTime, endTime, kqItemHash, holidayRules, Integer.MAX_VALUE);
            }
            
            //szk20131114判断位数
            timeLen = roundNumByItemDecimalWidth(kqItemHash,timeLen);
            timeLenDesc = timeLen + getTimeLenDesc((String)kqItemHash.get("item_unit"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return timeLenDesc;
    }
    
    
    /**
     * 获取保留位数
     * @param kqItemHash 
     * @param timeLen  时长
     * @return
     */
    public float roundNumByItemDecimalWidth(HashMap kqItemHash,float timeLen)
    {
    	String fielditemid = (String) kqItemHash.get("fielditemid");
    	if (StringUtils.isEmpty(fielditemid)) {
            return timeLen;
        }
    	
    	String tab = this.kqVer.getVersion() == KqConstant.Version.STANDARD ? "Q03" : "Q35";
    	
    	FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid, tab);
    	if (null == fieldItem) {
            return timeLen;
        }
    	
    	int len = fieldItem.getDecimalwidth();
    	return Float.parseFloat(PubFunc.round(timeLen + "", len));
    }
    
    
    /**
     * 获取加班时长（单位小时）
     * @param overType 申请类型
     * @param q1104 参考班次
     * @param nbase 人员库
     * @param a0100 姓名
     * @param startTime 申请开始时间
     * @param endTime 申请结束时间
     * @return
     */
	public float getOvertimeLen(String overType, String q1104, String nbase, String a0100, Date startTime, Date endTime) {
		float timeLen = 0;
		
		try {
			//考勤规则应取改假类自己的规则
			HashMap kqItemHash;
			kqItemHash = count_Leave(overType);
			kqItemHash.put("item_unit", KqConstant.Unit.HOUR);//单位为小时												
	        
	        //有参考班次的加班
	        if (!"".equals(q1104) && q1104 != null) {
                KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn);
                HashMap classMap = (HashMap) kqUtilsClass.getTimeAreaInclassById(q1104);
                boolean conday = ((Boolean)classMap.get("conday")).booleanValue();
                float classTimelen = new Float((String)classMap.get("work_hours")).floatValue();
                classTimelen = roundNumByItemDecimalWidth(kqItemHash, classTimelen/60);
                
                //非跨天班，有一天算一天，跨天班，跨一次零点算一天
                int days = DateUtils.dayDiff(startTime, endTime) + 1;
                if (!conday || classTimelen < 24) {
                    timeLen = classTimelen * days;
                } else {
                	timeLen = (classTimelen) * (days - 1);
                }
	        } else {
                timeLen = calcOverAppTimeLen(nbase, a0100, startTime, endTime, kqItemHash,  Integer.MAX_VALUE);
	        }
        
		} catch (GeneralException e) {
			e.printStackTrace();
		}
        
        return timeLen;
	}
	
    public String getTimeLenDesc(String unit) {
        String unitDesc = "";
        
        if (unit.equals(KqConstant.Unit.DAY)) {
            unitDesc = KqConstant.Unit.DAY_DESC;
        } else if (unit.equals(KqConstant.Unit.HOUR)) {
            unitDesc = KqConstant.Unit.HOUR_DESC;
        } else if (unit.equals(KqConstant.Unit.MINUTE)) {
            unitDesc = KqConstant.Unit.MINUTE_DESC;
        } else if (unit.equals(KqConstant.Unit.TIMES)) {
            unitDesc = KqConstant.Unit.TIMES_DESC;
        }
        
        return unitDesc;
            
    }
    
    /*
     * 加班最迟登记天数提示信息
     */
    public String getOvertimeRuleHintInfo(int overtimeRule) {
        return ResourceFactory.getProperty("kq.Overtime_rule.excess")
              + " " + overtimeRule + " "
              + ResourceFactory.getProperty("kq.Overtime_rule.excess1");
    }
    
    public static void main(String[] args) {

        String strDate = "2011.02.18";
        String strTTime = "23:00";
        String strOffFlextime = "01:00";
        Date end_date = DateUtils.getDate("2011.02.18 23:30", "yyyy.MM.dd HH:mm");
        Date TTime = DateUtils.getDate(strTTime, "HH:mm");
        Date TFlexTime = DateUtils.getDate(strOffFlextime, "HH:mm");
        Date DT1 = DateUtils.getDate(strDate + " " + strTTime, "yyyy.MM.dd HH:mm");
        Date DT2 = DateUtils.getDate(strDate + " " + strOffFlextime, "yyyy.MM.dd HH:mm");
        if (TFlexTime.before(TTime))// 跨天
        {
            DT2 = DateUtils.addDays(DT2, 1);
        }
        float time_1 = KQRestOper.getPartMinute(DT1, end_date);
        float time_2 = KQRestOper.getPartMinute(DT2, end_date);
        if (time_1 >= 0 && time_2 <= 0) {
            System.out.println(time_1);
        }
    }
    
    /**
     * 累计考勤期间的加班时长
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param stateFlag "1" 累计报审、报批、已批 ；
     * 					"2" 累计起草、驳回、报审、报批、已批（转加班）
     * 					"3" 累计已批（数据处理）
     * @return
     */
    public float getKqdurationOverTimelen(String nbase, String a0100, String stateFlag){
    	return getKqdurationOverTimelen(nbase, a0100, stateFlag, "");
    }
    
    /**
     * 累计考勤期间的加班时长
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param stateFlag "1" 累计报审、报批、已批 ；
     *                  "2" 累计起草、驳回、报审、报批、已批（转加班）
     *                  "3" 累计已批（数据处理）
     * @param notContainAppId 不包含的申请单号(暂时只支持一个号码)
     * @return
     */
    public float getKqdurationOverTimelen(String nbase, String a0100, String stateFlag, String notContainAppId){
        float overtimeLen = 0;
        try {
            //是否调休
            String field = KqUtilsClass.getFieldByDesc("Q11", ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
            
            Date kqStart = new Date();
            Date kqEnd = new Date();
            String kq_start = "";
            String kq_end = "";

            if (this.CurDuration != null) {
                kq_start = this.CurDuration.get("start").replace(".", "-") + " 00:00";
                kq_end = this.CurDuration.get("end").replace(".", "-") + " 23:59";

                kqStart = OperateDate.strToDate(kq_start, "yyyy-MM-dd HH:mm");
                kqEnd = OperateDate.strToDate(kq_end, "yyyy-MM-dd HH:mm");
            } else {
                ArrayList kqDuration = RegisterDate.getKqDayList(conn);
                if (kqDuration != null && kqDuration.size() > 0)
                {
                    kqStart = OperateDate.strToDate(((String)kqDuration.get(0)).replace(".", "-") + " 00:00", "yyyy-MM-dd HH:mm");
                    kqEnd = OperateDate.strToDate(((String)kqDuration.get(1)).replace(".", "-") + " 23:59", "yyyy-MM-dd HH:mm");
                    kq_start = OperateDate.dateToStr(kqStart, "yyyy-MM-dd HH:mm");
                    kq_end = OperateDate.dateToStr(kqEnd,  "yyyy-MM-dd HH:mm");
                }
            }

            StringBuffer sql = new StringBuffer();
            ContentDAO dao = new ContentDAO(conn);
            ResultSet rs = null;
            
            sql.append("select q11z1, q11z3, q1103, q1104 from q11");
            sql.append(" where nbase = '" + nbase + "' and a0100 = '" + a0100 + "'");
            sql.append(" and q11z1 >= " + Sql_switcher.dateValue(kq_start));
            sql.append(" and q11z1 <= " + Sql_switcher.dateValue(kq_end));
            if ("1".equals(stateFlag)) 
            {
                sql.append(" and q11z5 in ('03','02','08')");//自助报批
            }else if ("2".equals(stateFlag)) 
            {
                sql.append(" and q11z5 in ('01','02','03','07','08')");//转加班
            }else if ("3".equals(stateFlag)) 
            {
                sql.append(" and q11z5 = '03'");// 数据处理、业务平台
            }
            
            if(field != null && field.length() > 0) {
                sql.append(" and (" + Sql_switcher.isnull(field, "'2'") + " = '2'");//只算是否调休为“否”的
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sql.append(" or ").append(field).append("=''");
                }
                sql.append(")");
            }
            
            if (null != notContainAppId && !"".equals(notContainAppId.trim())) {
                sql.append(" and q1101<>'").append(notContainAppId).append("'");
            }
        
            rs = dao.search(sql.toString());
            Date start_d = new Date();
            Date end_d = new Date();
            while (rs.next()) {
                start_d = rs.getTimestamp("q11z1");
                end_d = rs.getTimestamp("q11z3");
                
                //加班限额累计的加班时长只累计当前考勤期间内的时长
                if (start_d.getTime() < kqStart.getTime() && end_d.getTime() > kqStart.getTime()) 
                {
                    start_d = kqStart;
                }else if (start_d.getTime() < kqEnd.getTime() && end_d.getTime() > kqEnd.getTime()) 
                {
                    end_d = kqEnd;
                }
                
                String q1103 = rs.getString("q1103");//加班类型
                String q1104 = rs.getString("q1104");//参考班次
                
                float count = getOvertimeLen(q1103, q1104, nbase, a0100, start_d, end_d);
                overtimeLen = overtimeLen + count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return overtimeLen;
    }
    
    /**
     * 获取一条加班申请的时长:此方法主要是计算在考勤期间内的有效时长
     * @param recordVo
     * @return
     */
    public float getOneOverTimelen(RecordVo recordVo){
    	float apptimeLen = 0;
    	try {
	        Date kqStart = new Date();
	        Date kqEnd = new Date();
	        String kqStartstr = "";
	        String kqEndStr = "";

	        // 申请所在考勤期间
	        if (this.CurDuration != null) {
                kqStartstr = ((String) this.CurDuration.get("start")).replace(".", "-") + " 00:00";
                kqEndStr = ((String) this.CurDuration.get("end")).replace(".", "-") + " 23:59";

                kqStart = OperateDate.strToDate(kqStartstr, "yyyy-MM-dd HH:mm");
                kqEnd = OperateDate.strToDate(kqEndStr, "yyyy-MM-dd HH:mm");
            } else {
                ArrayList kqDuration = RegisterDate.getKqDayList(conn);
                if (kqDuration != null && kqDuration.size() > 0)
                {
                    kqStart = OperateDate.strToDate(((String)kqDuration.get(0)).replace(".", "-") + " 00:00", "yyyy-MM-dd HH:mm");
                    kqEnd = OperateDate.strToDate(((String)kqDuration.get(1)).replace(".", "-") + " 23:59", "yyyy-MM-dd HH:mm");
                    kqStartstr = ((String) kqDuration.get(0)).replace(".", "-") + " 00:00";
                    kqEndStr = ((String) kqDuration.get(1)).replace(".", "-") + " 23:59";
                }
            }

	    	String start_d = OperateDate.dateToStr(recordVo.getDate("q11z1"), "yyyy-MM-dd HH:mm");
	        String end_d = OperateDate.dateToStr(recordVo.getDate("q11z3"), "yyyy-MM-dd HH:mm");
	        String q1104 = (String) recordVo.getString("q1104");
	        String q1103 = (String) recordVo.getString("q1103");
	     	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date startTime;
			startTime = sdf.parse(start_d);//申请开始日期
	        Date endTime = sdf.parse(end_d);//申请结束日期
	         
	        //加班限额累计的加班时长只累计当前考勤期间内的时长
	        if (startTime.getTime() < kqStart.getTime() && endTime.getTime() > kqStart.getTime()) 
			{
	        	startTime = kqStart;
			}else if (startTime.getTime() < kqEnd.getTime() && endTime.getTime() > kqEnd.getTime()) 
			{
				endTime = kqEnd;
			}
	        
	        //累计当前考勤期间内的申请单
	        if (recordVo.getDate("q11z1").getTime() >= OperateDate.strToDate(kqStartstr, "yyyy-MM-dd HH:mm").getTime()
	        		&& recordVo.getDate("q11z1").getTime() <= OperateDate.strToDate(kqEndStr, "yyyy-MM-dd HH:mm").getTime()) 
	        {
	         	float count = getOvertimeLen(q1103, q1104, recordVo.getString("nbase"), recordVo.getString("a0100"), startTime, endTime);
	            apptimeLen = apptimeLen + count;
	        }
    	} catch (ParseException e) {
    		e.printStackTrace();
    	}
        
        return apptimeLen;
    }
    
    /**
     * 审批时检查申请单是否超出时长限额
     * @param vo
     * @return
     */
    public String checkOverTimelenMorethanLimit(RecordVo vo, String stateFlag){
    	StringBuilder mess = new StringBuilder();
    	
		String iftoRestField = KqUtilsClass.getFieldByDesc("Q11", ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
		 
		String para = KqParam.getInstance().getDURATION_OVERTIME_MAX_LIMIT();
		if (para == null || para.length() <= 0) {
            para = "-1";
        }
		int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
		 
		if (overtimeLimit <= 0) {
            return mess.toString();
        }
		
	 	String IftoRest = "";
	 	if(iftoRestField != null && iftoRestField.length() > 0) {
            IftoRest = vo.getString(iftoRestField);
        }
	 	//调休的加班不计算
		if("1".equals(IftoRest)) {
            return mess.toString();
        }

        String startDate = OperateDate.dateToStr(vo.getDate("q11z1"), "yyyy-MM-dd HH:mm");
        String endDate = OperateDate.dateToStr(vo.getDate("q11z3"), "yyyy-MM-dd HH:mm");
		HashMap<String, HashMap<String, String>> durations = RegisterDate.getDurations(this.conn, startDate, endDate);

        Iterator<Map.Entry<String, HashMap<String, String>>> iterator = durations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HashMap<String, String>> entry = iterator.next();
            String duraionName = entry.getKey();
            this.CurDuration = entry.getValue();

            float apptimeLen = getOneOverTimelen(vo);

            //总是会计算本次申请，所以取期间内所有申请时排除掉本次申请
            String appId = (String)vo.getString("q1101");
            float overtimeLen = getKqdurationOverTimelen(vo.getString("nbase"), vo.getString("a0100"), stateFlag, appId);

            if ((overtimeLen + apptimeLen > overtimeLimit) && apptimeLen > 0) {
                mess.append(vo.getString("a0101")).append("申请加班时长为");
                mess.append(PubFunc.round(apptimeLen+"", 2)).append("小时，");
                mess.append("期间(").append(this.CurDuration.get("start")).append("~").append(this.CurDuration.get("end"));
                mess.append(")已申请的加班时长为").append(PubFunc.round(overtimeLen+"", 2)).append("小时，");
                mess.append("合计已超出加班限额规定的").append(PubFunc.round(""+overtimeLimit, 2)).append("小时。");

                break;
            }
        }

        this.CurDuration = null;

		return mess.toString();
    }

    public String CheckAppTypeIsToLeave(String app_type){
    	String returnString = "";
    	if (app_type == null || app_type.length() <= 0) {
            return returnString;
        }
    	
		if (!"1".equals(app_type.subSequence(0, 1))) {
            return returnString;
        }
		
		String txjb = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME();
        txjb = txjb == null ? "" : txjb;
        if ((("," + txjb).indexOf("," + app_type + ",") == -1)){
        	StringBuffer sql = new StringBuffer();
        	sql.append("select item_name from kq_item where item_id = '" + app_type + "'");
        	ContentDAO dao = new ContentDAO(conn);
        	ResultSet rs = null;
        	try {
        		rs = dao.search(sql.toString());
        		if(rs.next()){
        			String itemName = rs.getString(1);
        			returnString = itemName + "不允许调休！";
        		}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				KqUtilsClass.closeDBResource(rs);
			}
        }
    	
    	return returnString;
    }
    
    /**
     * @Title: checkOvertimeRepeat   
     * @Description: 检查同一个公休日或节假日内加班是否多次申请   
     * @param @param appType
     * @param @param kqStart
     * @param @param kqEnd
     * @param @param vo
     * @param @throws GeneralException 
     * @return void    
     * @throws
     */
    public void checkOvertimeRepeat(String appType, java.util.Date kqStart, java.util.Date kqEnd,
             RecordVo vo) throws GeneralException {
        if (!"1".equals(KqParam.getInstance().getRestOvertimeTimes())) {
            return;
        }

        if (KqAppInterface.isFeastOvertime(appType) || KqAppInterface.isRestOvertime(appType)) {
            if (!ifSaveRestLeisure(vo.getString("q1101"),vo.getString("nbase"), vo.getString("a0100"), kqStart, kqEnd, "Q11",
                    this.conn, "q1103='" + appType + "'")) {
                String mess = "";
                
                if (KqAppInterface.isRestOvertime(appType)) {
                    mess = "这个公休日已经申请了加班，不允许再次申请！";
                }
                
                if (KqAppInterface.isFeastOvertime(appType)) {
                    mess = "这个节假日已经申请了加班，不允许再次申请！";
                }
                
                throw new GeneralException(vo.getString("a0101") + " 【" + getAppLeavedMess()+ "】，" + mess );
            }
        }
    }

	public void setValidateUsedTimeLenError(boolean validateUsedTimeLenError) {
		this.validateUsedTimeLenError = validateUsedTimeLenError;
	}

	public boolean getValidateUsedTimeLenError() {
		return validateUsedTimeLenError;
	}
}
