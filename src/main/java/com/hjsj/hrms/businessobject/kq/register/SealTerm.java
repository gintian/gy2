package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 判断是否可以考勤封存
 * */
public class SealTerm {
    private Connection conn;
    private UserView   userView;

    private SealTerm() {

    }

    public SealTerm(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     * 
     * @Title: getAllBaseOrgid   
     * @Description:    
     * @param @param nbase
     * @param @param orgid
     * @param @param whereIN
     * @param @param conn
     * @param @param start_date
     * @param @param end_date
     * @param @return 
     * @return ArrayList    
     * @throws
     */
    public ArrayList getAllBaseOrgid(String nbase, String orgid, String whereIN, Connection conn, String start_date,
            String end_date) {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select distinct " + orgid + " from q03 ");
        sql.append(" where nbase='" + nbase + "'");
        sql.append(" and Q03Z0 >= '" + start_date + "'");
        sql.append(" and Q03Z0 <= '" + end_date + "'");
        sql.append(" and Q03Z5 IN ('01','02','07','08')");
        if (!this.userView.isSuper_admin()) {
            sql.append(" and exists(select " + nbase + "A01.a0100 " + whereIN + " and q03.a0100=" + nbase + "a01.a0100)");// 源表的过滤条件
        }
        ContentDAO dao = new ContentDAO(conn);
        try {
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                String e0122 = rowSet.getString(orgid);
                if (e0122 != null && e0122.length() > 0) {
                    list.add(e0122);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return list;
    }

    /**
     *判断是否有部门没有审批
     * Q03Z5=‘01，02，07’
     * 标示没有审批
     * @param baseE0122_list  包含库前缀和部门编号String[]的ArrayList
     * @return
     *        list 没有审批的部门
     */
    public ArrayList getNotApproveQ03(ArrayList baseE0122_list, String start_date, String end_date) {
        ArrayList notApp_list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            for (int i = 0; i < baseE0122_list.size(); i++) {
                String[] base_e0122 = (String[]) baseE0122_list.get(i);
                String nbase = base_e0122[0];
                String e0122 = base_e0122[1];
                String b0110 = base_e0122[2];
                //String dbase=getDBname(nbase);    	

                sql = new StringBuffer();
                sql.append("select distinct b0110 from Q03");
                sql.append(" where nbase='" + nbase + "' and b0110='" + b0110 + "' and e0122='" + e0122 + "'");
                sql.append(" and Q03Z0 >= '" + start_date + "'");
                sql.append(" and Q03Z0 <= '" + end_date + "'");
                sql.append(" and (Q03Z5='01' or  Q03Z5='02' or  Q03Z5='07' or  Q03Z5='08')");
                if (!this.userView.isSuper_admin()) {
                    String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                    sql.append(" and exists(select " + nbase + "A01.a0100 " + whereIN + " and q03.a0100=" + nbase + "a01.a0100)");// 源表的过滤条件
                }
                rowSet = dao.search(sql.toString());
                if (rowSet.next()) {
                    RecordVo one_vo = new RecordVo("Q05", 1);
                    one_vo.setString("nbase", "");
                    one_vo.setString("b0110", rowSet.getString("b0110"));
                    one_vo.setString("e0122", e0122);
                    notApp_list.add(one_vo);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }

        return notApp_list;
    }

    /**
     *判断是否有部门没有日汇总
     * Q03Z5=‘01，02，07’
     * 表示没有审批
     * @param baseE0122_list  包含库前缀和部门编号String[]的ArrayList
     * @return
     *        list 没有生成部门日汇总
     * @deprecated zxj 2014.02.15 此方法效率太差，作废。请使用haveNotDailyCollect方法。
     */
    public ArrayList getNotDailyCollect(ArrayList baseE0122_list, String start_date, String end_date) {
        ArrayList notDC_list = new ArrayList();
        for (int i = 0; i < baseE0122_list.size(); i++) {
            String[] base_e0122 = (String[]) baseE0122_list.get(i);
            String nbase = base_e0122[0];
            String e0122 = base_e0122[1];
            String b0110 = base_e0122[2];
            String dbase = getDBname(nbase);
            StringBuffer sql = new StringBuffer();
            sql.append("select 1 from Q07");
            sql.append(" where b0110='" + e0122 + "'");
            sql.append(" and Q03Z0 >= '" + start_date + "'");
            sql.append(" and Q03Z0 <= '" + end_date + "'");
            RowSet rowSet = null;
            ContentDAO dao = new ContentDAO(this.conn);
            try {
                rowSet = dao.search(sql.toString());
                if (!rowSet.next()) {
                    RecordVo one_vo = new RecordVo("Q03", 1);
                    one_vo.setString("nbase", dbase);
                    one_vo.setString("b0110", b0110);
                    one_vo.setString("e0122", e0122);
                    notDC_list.add(one_vo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                KqUtilsClass.closeDBResource(rowSet);
            }
        }
        return notDC_list;
    }

    /**
     *判断是否有部门没有成月汇总
     * Q03Z5=‘01，02，07’
     * 标示没有审批
     * @param baseE0122_list  包含库前缀和部门编号String[]的ArrayList
     * @return
     *        list 没有生成部门日汇总
     * @deprecated zxj 2014.02.15 此方法效率太差，作废。请使用haveNotDailyCollect方法。
     */
    public ArrayList getNotSumCollect(ArrayList baseE0122_list, String kq_duration) {
        ArrayList notDC_list = new ArrayList();
        for (int i = 0; i < baseE0122_list.size(); i++) {
            String[] base_e0122 = (String[]) baseE0122_list.get(i);
            String nbase = base_e0122[0];
            String e0122 = base_e0122[1];
            String b0110 = base_e0122[2];
            String dbase = getDBname(nbase);
            StringBuffer sql = new StringBuffer();
            sql.append("select 1 from Q09");
            sql.append(" where b0110='" + e0122 + "'");
            sql.append(" and q03z0='" + kq_duration + "'");
            RowSet rowSet = null;
            ContentDAO dao = new ContentDAO(this.conn);
            try {
                rowSet = dao.search(sql.toString());
                if (!rowSet.next()) {
                    RecordVo one_vo = new RecordVo("Q03", 1);
                    one_vo.setString("nbase", dbase);
                    one_vo.setString("b0110", b0110);
                    one_vo.setString("e0122", e0122);
                    notDC_list.add(one_vo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                KqUtilsClass.closeDBResource(rowSet);
            }
        }
        return notDC_list;
    }
    
    /**
     * 判断是否有部门日汇总没有生成
     * @Title: haveNotDailyCollect   
     * @Description:   
     * @param start_date 考勤期间起始日期
     * @param end_date   考勤期间结束日期
     * @return true:有没生成的 false:都生成了
     */
    public boolean haveNotDailyCollect(String startDate, String endDate) {
        return haveDepartNotCollect(null, startDate, endDate);
    }
    
    /**
     * 判断是否有部门月汇总没有生成
     * @Title: haveNotSumCollect   
     * @Description:    
     * @param kqDuration 考勤期间
     * @param startDate  考勤期间开始日期
     * @param endDate    考勤期间借宿日期
     * @return true:有没生成的 false:都生成了
     */
    public boolean haveNotSumCollect(String kqDuration, String startDate, String endDate) {
        return haveDepartNotCollect(kqDuration, startDate, endDate);
    }
    
    /**
     * 判断是否有部门数据（日明细或月汇总）没有生成
     * @Title: haveDepartNotCollect   
     * @Description: 日明细：考勤期间输入null或“”
     * @param kqDuration 考勤期间   
     * @param startDate  考勤起始日期
     * @param endDate    考勤结束结束日期
     * @return true:有没生成的 false:都生成了
     */
    private boolean haveDepartNotCollect(String kqDuration, String startDate, String endDate) {
        boolean have = false;
        
        String empPrivWhr = "(";
        String nbaseWhr = "nbase IN (";
        
        KqUtilsClass kqUtils = new KqUtilsClass(this.conn, this.userView);        
        try {
            ArrayList privDBList = kqUtils.getKqPreList();
            if (0 >= privDBList.size()) {
                return have;
            }
            
            String nbaseTemp = "nbase='#NBASE#'";
            String empTemp = RegisterInitInfoData.getWhereINSql(userView, "#NBASE#");
            for (int i=0; i<privDBList.size(); i++) {
                String nbase = (String)privDBList.get(i);
                
                if (i>0) {
                    nbaseWhr = nbaseWhr + ",";
                    empPrivWhr = empPrivWhr + " OR ";
                }
                
                nbaseWhr = nbaseWhr + "'" + nbase + "'";
                
                String nbasePriv = nbaseTemp.replace("#NBASE#", nbase);
                String empPriv = empTemp.replace("#NBASE#", nbase);
                empPrivWhr = empPrivWhr + "(" + nbasePriv + " AND EXISTS(SELECT 1 " + empPriv + " and q03.a0100=" + nbase + "a01.a0100))";
            }
            empPrivWhr = empPrivWhr + ")";
            nbaseWhr = nbaseWhr + ")";
        } catch (Exception e) {
            e.printStackTrace();
            return have;
        }
        
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(1) reccount from q03");
        sql.append(" WHERE Q03Z0 >= '" + startDate + "'");
        sql.append(" AND Q03Z0 <= '" + endDate + "'");
        sql.append(" AND " + nbaseWhr);
        if (!userView.isSuper_admin()) {
            sql.append(" AND " + empPrivWhr);
        }
        sql.append(" AND NOT EXISTS(SELECT 1 FROM");
        
        if (null==kqDuration || "".equals(kqDuration)) {
            sql.append(" Q07 A");
            sql.append(" WHERE A.Q03Z0 >= '" + startDate + "'");
            sql.append(" and A.Q03Z0 <= '" + endDate + "'");
        } else {
            sql.append(" Q09 A");
            sql.append(" WHERE A.Q03Z0 = '" + kqDuration + "'");
        }        
        
        sql.append(" AND A.B0110=Q03.E0122)");
        
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rowSet = dao.search(sql.toString());
            have = rowSet.next() && 0<rowSet.getInt("reccount");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        
        return have;
    }
    
    /**
     * 得到给定标示的人员库的名称
     */
    public String getDBname(String pre) {
        String sql = "select dbname,pre from dbname where pre ='" + pre + "'";
        RowSet rowSet = null;
        String dbname = "";
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rowSet = dao.search(sql.toString());
            if (rowSet.next()) {
                dbname = rowSet.getString("dbname");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return dbname;
    }

    /**
     * 得到管理范围内参加考勤的部门编号
     * @param kq_dbase_list  参加考勤的人员库前缀
     * @return  list 里面是个长度3得数组，0：库前缀；1：部门编号;2:单位
     */
    public ArrayList getPrivBaseE0122(String nbase, String b0110, String start_date, String end_date, String whereIN) {
        RowSet rowSet = null;
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select distinct e0122 from q03");
        sql.append(" where nbase='" + nbase + "'");
        sql.append(" and Q03Z0 >= '" + start_date + "'");
        sql.append(" and Q03Z0 <= '" + end_date + "'");
        sql.append(" and Q03Z5 IN ('01','02','07','08')");
        
        if (null != b0110 && !"".equals(b0110)) {
            sql.append(" and b0110='" + b0110 + "'");
        }
        
        if (!this.userView.isSuper_admin() && !"".equals(whereIN)) {
            sql.append(" and exists(select " + nbase + "A01.a0100 " + whereIN + " and q03.a0100=" + nbase + "a01.a0100)");// 源表的过滤条件
        }
        
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                String e0122 = rowSet.getString("e0122");
                if (e0122 != null && e0122.length() > 0) {
                    String[] base_e0122 = new String[3];
                    base_e0122[0] = nbase;
                    base_e0122[1] = e0122;
                    base_e0122[2] = b0110;
                    list.add(base_e0122);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }

        return list;
    }

    public ArrayList getNotPigeonhole(ArrayList kq_dbase_list, String kq_duration, String setID, String month_pigeonhole) {
        ArrayList notDC_list = new ArrayList();
        for (int i = 0; i < kq_dbase_list.size(); i++) {
            String nbase = (String) kq_dbase_list.get(i);
            StringBuffer sql = new StringBuffer();
            sql.append("select distinct e0122 from Q05");
            sql.append(" where ");
            sql.append(" q03z0='" + kq_duration + "' and nbase='" + nbase + "'");
            sql.append(" and not exists(select a0100 from " + nbase + setID + " where  " + setID + "z0 ="
                    + Sql_switcher.charToDate("'" + month_pigeonhole + "'") + "");
            sql.append(" and " + nbase + setID + ".a0100=q05.a0100)");
            RowSet rowSet = null;
            ContentDAO dao = new ContentDAO(this.conn);
            try {
                rowSet = dao.search(sql.toString());
                if (rowSet.next()) {
                    RecordVo one_vo = new RecordVo("Q05", 1);
                    one_vo.setString("nbase", nbase);
                    one_vo.setString("e0122", rowSet.getString("e0122"));
                    notDC_list.add(one_vo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                KqUtilsClass.closeDBResource(rowSet);
            }
        }
        return notDC_list;
    }
    
    public boolean haveNoApprovedDataInQ03(String nbase, String startDate, String endDate, String whereIN, String orgWhr) {
        KqDBHelper kqDB = new KqDBHelper(this.conn);
        
        StringBuffer whr = new StringBuffer();
        whr.append(" Q03Z0 >= '" + startDate + "'");
        whr.append(" and Q03Z0 <= '" + endDate + "'");
        whr.append(" and Q03Z5 IN ('01','02','07','08')");
        if (!"".equals(nbase)) {
            whr.append(" AND nbase='" + nbase + "'");
        }
        
        if (!"".equals(orgWhr)) {
            whr.append(" AND " + orgWhr);
        }
        
        if (!this.userView.isSuper_admin() && !"".equals(whereIN)) {
            whr.append(" and exists(select " + nbase + "A01.a0100 " + whereIN + " and q03.a0100=" + nbase + "a01.a0100)");
        }
        
        return kqDB.isRecordExist("Q03", whr.toString());
    }
}
