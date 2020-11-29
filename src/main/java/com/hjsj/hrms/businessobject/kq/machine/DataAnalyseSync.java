package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class DataAnalyseSync {

    public synchronized static void insertEmpIntoTmp(String b0110, String analyse_Tmp, String date_table, String nbase,
            String codewhere, String whereIN, String start_date, String end_date, String card_no_temp_field,
            String g_no_temp_field, String kq_dkind, String kq_sDate, String kq_Gno, String kq_card, String kq_type,
            String analyseType, ContentDAO dao, UserView userView, String mainsql, Connection conn) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + analyse_Tmp + "(q03z0,nbase,a0100,b0110,e0122,e01a1,a0101,");
        if (mainsql != null && mainsql.length() > 0) {
            sql.append("" + card_no_temp_field + "," + g_no_temp_field + "," + kq_dkind + ",q03z3,flag,cur_user,isnormal," + mainsql
                            + ")");
            
	        if (codewhere != null && codewhere.length() > 0) {
	            if (codewhere.contains("nbase") && codewhere.contains("a0100")) {
	            	sql.append(" select q03z0,nbase,a0100,b0110,e0122,e01a1,a0101,card_no,g_no,dkind," + kq_type + ",flag,cur_user,isnormal,").append(mainsql); 
	            	sql.append(" from (");
	            }
            }
            
            sql.append(" select " + date_table + "." + kq_sDate + " as q03z0,");
            sql.append("'" + nbase + "' as nbase,");
            sql.append("a0100,b0110,e0122,e01a1,a0101," + kq_card + " as " + card_no_temp_field + ",");
            sql.append(kq_Gno + " as " + g_no_temp_field + ",");
            sql.append(kq_dkind + "," + kq_type + ",'1' as flag,'" + userView.getUserName() + "' as cur_user, 0 as isnormal," + mainsql);
        } else {
            sql.append("" + card_no_temp_field + "," + g_no_temp_field + "," + kq_dkind + ",q03z3,flag,cur_user,isnormal)");
            
            if (codewhere != null && codewhere.length() > 0) {
	            if (codewhere.contains("nbase") && codewhere.contains("a0100")) {
	            	sql.append(" select q03z0,nbase,a0100,b0110,e0122,e01a1,a0101,card_no,g_no,dkind," + kq_type + ",flag,cur_user, isnormal");
	                sql.append(" from (");
	            }
            }
        
            sql.append(" select " + date_table + "." + kq_sDate + " as q03z0,");
            sql.append("'" + nbase + "' as nbase,");
            sql.append("a0100,b0110,e0122,e01a1,a0101," + kq_card + " as " + card_no_temp_field + ",");
            sql.append(kq_Gno + " as " + g_no_temp_field + ",");
            sql.append(kq_dkind + "," + kq_type + ",'1' as flag,'" + userView.getUserName() + "' as cur_user, 0 as isnormal");
        }

        sql.append(" from " + nbase + "A01 A, (select orgid,sdate,dkind from " + date_table);
        
        if (!(codewhere != null && codewhere.length() > 0 && codewhere.contains("nbase") && codewhere.contains("a0100"))) {
            sql.append(" where orgid='" + b0110 + "'");
        }
        
        sql.append(" group by orgid,sdate,dkind) " + date_table);
        sql.append(" where  ");
        sql.append(" NOT EXISTS(SELECT 1 FROM " + analyse_Tmp + " t1 where");
        sql.append(" " + date_table + "." + kq_sDate + "=t1.q03z0 and t1.nbase='" + nbase + "' and A.a0100=t1.a0100");
        sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
        if ("101".equals(analyseType)) {
            //sql.append(" and "+Sql_switcher.isnull("cur_user", "'##'")+"<>'##'");			
        }
        sql.append(")");

        
        if (analyseType != null && ("1".equals(analyseType) || "101".equals(analyseType))) {
            //sql.append(" and ("+kq_card+" is not null or "+kq_card+"<>'') ");//and "+kq_type+"='02'
            sql.append(" and (" + Sql_switcher.isnull(kq_card, "'##'") + "<>'##')");
        }
        
        if (whereIN != null && whereIN.length() > 0) {
            if (!userView.isSuper_admin()) {
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100=A.a0100)");
                } else {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100=A.a0100)");
                }
            }
        }
        
        if (!(codewhere != null && codewhere.length() > 0 && codewhere.contains("nbase") && codewhere.contains("a0100"))) {
            sql.append(" and " + date_table + ".orgid=A.b0110");
        }
        
        sql.append(" and " + date_table + ".sDate>='" + start_date + "' and " + date_table + ".sDate<='" + end_date + "'");
        
        if (codewhere != null && codewhere.length() > 0) {
        	if (codewhere.contains("nbase") && codewhere.contains("a0100")) {
        		sql.append(" ) a ");
        		sql.append(" where " + codewhere);
        	} else {
        	    sql.append(" and " + codewhere);
        	}
        }
        try {
            ArrayList list = new ArrayList();
            dao.insert(sql.toString(), list);
        } catch (Exception e) {
            //System.out.println("初始化考勤处理增加人员信息数据出错！---" + userView.getUserName() + "---〉" + sql.toString());
            String message = e.toString();
            if(Sql_switcher.searchDbServer() == Constant.MSSQL && message.indexOf("最大")!=-1 && message.indexOf("8060")!=-1)
            {
                PubFunc.resolve8060(conn, analyse_Tmp);
                throw GeneralExceptionHandler.Handle(new Exception("操作执行过程中出现问题，系统已尝试自动修复，请重试！"));
            } else {
                e.printStackTrace();
            }
        }
    }

    public synchronized static void update(String sql, ContentDAO dao) {
        try {
            dao.update(sql);
        } catch (Exception e) {
            System.out.println("考勤处理同步修改！---" + sql);
            e.printStackTrace();
        }
    }

    /**
     * 为表添加dbid、a0000两个字段
     * @param tableName
     * @return
     */
    public synchronized static boolean updateOrder(String tableName, String start, String end, String temp, Connection conn) {
        boolean flag = false;

        start = start.replaceAll("-", ".");
        end = end.replaceAll("-", ".");

        StringBuffer sql = new StringBuffer();
        sql.append("select a0100 from ");
        sql.append(tableName);
        sql.append(" where (dbid is null or a0000 is null) and q03z0>='" + start + "' and q03z0<='" + end + "'");

        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                sql.delete(0, sql.length());
                sql.append("select dbid,pre from dbname");
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    String dbid = rs.getString("dbid");
                    String pre = rs.getString("pre");
                    sql.delete(0, sql.length());
                    sql.append("update ");
                    sql.append(tableName);
                    sql.append(" set dbid=");
                    sql.append(dbid);
                    sql.append(",a0000=(select a0000 from ");
                    sql.append(pre);
                    sql.append("a01  where ");
                    sql.append(pre);
                    sql.append("a01.a0100=");
                    sql.append(tableName);
                    sql.append(".a0100");
                    sql.append(") where nbase='");
                    sql.append(pre);
                    sql.append("' and q03z0 between '");
                    sql.append(start);
                    sql.append("' and '");
                    sql.append(end);
                    sql.append("' and exists(select 1 from ");
                    sql.append(temp);
                    sql.append(" c where c.q03z0=");
                    sql.append(tableName);
                    sql.append(".q03z0 and c.a0100=");
                    sql.append(tableName);
                    sql.append(".a0100 and c.nbase=");
                    sql.append(tableName);
                    sql.append(".nbase");
                    sql.append(" and " + tableName + ".q03z0 between '");
                    sql.append(start);
                    sql.append("' and '");
                    sql.append(end);
                    sql.append("' )");
                    //System.out.println(sql.toString());
                    dao.update(sql.toString());
                }
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return flag;
    }

    /* 统一到kqutilsclass中
    public synchronized static void leadingInItemToQ03(ArrayList dblist, String start_date, String end_date, Connection conn,
            UserView userview) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("select other_param,fielditemid from kq_item where ");
        sql.append(Sql_switcher.isnull("fielditemid", "'ttt'") + "<>'tt'");
        // sql.append(" and other_param is not null");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            String other_param = "";
            String fielditemid = "";
            while (rs.next()) {
                other_param = Sql_switcher.readMemo(rs, "other_param");
                if (other_param == null || other_param.length() <= 0)
                    continue;
                
                fielditemid = rs.getString("fielditemid");
                SearchImportBo importBo = new SearchImportBo(other_param);
                String subset = importBo.getValue("subset");
                String setfielditemid = importBo.getValue("field");
                String begindate = importBo.getValue("begindate");
                String enddate = importBo.getValue("enddate");
                
                if (subset == null || subset.length() <= 0)
                    continue;
                
                if (setfielditemid == null || setfielditemid.length() <= 0)
                    continue;
                
                if (begindate == null || begindate.length() <= 0)
                    continue;
                
                if (enddate == null || enddate.length() <= 0)
                    continue;
                
                if (fielditemid == null || fielditemid.length() <= 0)
                    continue;
                
                upLeadingInItemToQ03(fielditemid, subset, setfielditemid, begindate, enddate, dblist, start_date, end_date, conn,
                        userview);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);

        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }

    private static void upLeadingInItemToQ03(String q03fielditemid, String setId, String setfielditemid,
            String begindate_fielditemid, String enddate_fielditemid, ArrayList dblist, String start_date, String end_date,
            Connection conn, UserView userview) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        for (int i = 0; i < dblist.size(); i++) {
            String userbase = dblist.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userview, userbase);
            String destTab = "q03";// 目标表
            String srcTab = userbase + setId;// 源表
            String strJoin = destTab + ".A0100=" + srcTab + ".A0100";// 关联串
            
            // xxx.field_name=yyyy.field_namex,....
//            strJoin = strJoin + " and " + Sql_switcher.charToDate(destTab + ".q03z0") + " between "
//                    + Sql_switcher.isnull(srcTab + "." + begindate_fielditemid, Sql_switcher.dateValue("2010-01-01")) + " and "
//                    + Sql_switcher.isnull(srcTab + "." + enddate_fielditemid, Sql_switcher.dateValue("9999-12-31"));
            
            /*zxj 20140317 changed 不能简单的用日期型进行大小比较，因为日期后可能带有时间。
            改为按格式化后的日期字符串进行比较
           
           strJoin = strJoin 
                   + " and " + destTab + ".q03z0>="
                   + mssqlReplace(Sql_switcher.sqlNull(Sql_switcher.dateToChar(srcTab + "." + begindate_fielditemid, "yyyy.mm.dd"), "2010.01.01"),"-",".")
                   + " and " + destTab + ".q03z0<=" 
                   + mssqlReplace(Sql_switcher.sqlNull(Sql_switcher.dateToChar(srcTab + "." + enddate_fielditemid, "yyyy.mm.dd"), "9999.01.01"),"-",".");

            String strSet = destTab + "." + q03fielditemid + "=" + srcTab + "." + setfielditemid;
            // 更新串 xxx.field_name=yyyy.field_namex,....
            String strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0>='" + start_date + "' and "
                    + destTab + ".q03z0<='" + end_date + "' and " + destTab + ".q03z5='01'";// 更新目标的表过滤条件				
            String strSWhere = "";
            if (userview.isSuper_admin()) {
                if (whereIN.toLowerCase().indexOf("where") != -1) {
                    strSWhere = "exists (select a0100 " + whereIN + " and " + userbase + setId + ".a0100=" + userbase
                            + "a01.a0100)";// 源表的过滤条件
                } else {
                    strSWhere = "exists (select a0100 " + whereIN + " where " + userbase + setId + ".a0100=" + userbase
                            + "a01.a0100)";// 源表的过滤条件
                }
            } else {
                if (whereIN.toLowerCase().indexOf("where") != -1)
                    strSWhere = ("  EXISTS(select a0100 " + whereIN + " and " + userbase + "A01.a0100=" + userbase + setId + ".a0100)");
                else
                    strSWhere = ("  EXISTS(select a0100 " + whereIN + " where " + userbase + "A01.a0100=" + userbase + setId + ".a0100)");
            }
            
            String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
            String othWhereSql = "";
            if (!userview.isSuper_admin()) {
                if (whereIN.toLowerCase().indexOf("where") != -1)
                    othWhereSql = ("  EXISTS(select a0100 " + whereIN + " and " + userbase + "A01.a0100=" + destTab + ".a0100)");
                else
                    othWhereSql = ("  EXISTS(select a0100 " + whereIN + " where " + userbase + "A01.a0100=" + destTab + ".a0100)");
            }
            
            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);

            try {
                dao.update(update);
            } catch (Exception e) {
                e.printStackTrace();
                String error = checkLeadingInItemToQ03ErrorEmp(dao, userbase, whereIN, setId, setfielditemid,
                        begindate_fielditemid, enddate_fielditemid, start_date, end_date, userview);
                
                if (error != null && error.length() > 0)
                    error = "。以下是可能出现的人员<br><br>" + error;
                
                throw GeneralExceptionHandler.Handle(new GeneralException("从子集导入指标失败！<br>" 
                        + e.getMessage() + "<br>" + error));
            }

        }

    }
    
    private static String mssqlReplace(String srcStr, String targetChar, String replaceChar) {
        if (Constant.MSSQL != Sql_switcher.searchDbServer())
            return srcStr;
        
        return srcStr = "replace(" + srcStr + ",'" + targetChar + "','" + replaceChar + "')";
    }

    private static String checkLeadingInItemToQ03ErrorEmp(ContentDAO dao, String nbase, String whereIN, String setId,
            String setfielditemid, String begindate_fielditemid, String enddate_fielditemid, String start_date, String end_date,
            UserView userview) {
        StringBuffer sql = new StringBuffer();
        String srcTab = nbase + setId;// 源表
        String destTab = "q03";
        sql.append("select a0101,b0110,e0122,e01a1,a0100 from (");
        sql.append("select max(q03.a0101) as a0101,max(q03.b0110) as b0110,max(q03.e0122) as e0122,max(q03.e01a1) as e01a1,q03.a0100");
        sql.append(" from " + srcTab + ",q03");
        sql.append(" where " + srcTab + ".A0100=q03.A0100");
        sql.append(" and " + Sql_switcher.charToDate(destTab + ".q03z0") + " between "
                + Sql_switcher.isnull(srcTab + "." + begindate_fielditemid, Sql_switcher.dateValue("2010-01-01")) + " and "
                + Sql_switcher.isnull(srcTab + "." + enddate_fielditemid, Sql_switcher.dateValue("9999-12-31")));
        sql.append(" and " + destTab + ".nbase='" + nbase);
        sql.append("' and " + destTab + ".q03z0>='" + start_date);
        sql.append("' and " + destTab + ".q03z0<='" + end_date + "'");

        if (!userview.isSuper_admin()) {
            sql.append(" and " + destTab + ".a0100 in(select a0100 " + whereIN + ")");
            if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                sql.append(" and exists (select a0100 " + whereIN + " and " + srcTab + ".a0100=" + nbase + "a01.a0100)");// 源表的过滤条件
            } else {
                sql.append(" and exists (select a0100 " + whereIN + " where " + srcTab + ".a0100=" + nbase + "a01.a0100)");// 源表的过滤条件
            }
        }
        sql.append("group by q03.a0100,q03.q03z0 ");
        sql.append(" having count(" + srcTab + ".a0100)>1");
        sql.append(") s group by b0110,e0122,e01a1,a0101,a0100");
        
        RowSet rs = null;
        StringBuffer buff = new StringBuffer();
        String per = AdminCode.getCodeName("@@", nbase);
        try {
            rs = dao.search(sql.toString());

            while (rs.next()) {
                String b0110 = rs.getString("b0110");
                String e0122 = rs.getString("e0122");
                String e01a1 = rs.getString("e01a1");
                b0110 = AdminCode.getCodeName("UN", b0110);
                e0122 = AdminCode.getCodeName("UM", e0122);
                e01a1 = AdminCode.getCodeName("@K", e01a1);
                buff.append(per + " " + b0110 + "  " + e0122 + "  " + e01a1 + "  " + "  " + rs.getString("a0101") + "<br>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return buff.toString();
    }
    */
}
