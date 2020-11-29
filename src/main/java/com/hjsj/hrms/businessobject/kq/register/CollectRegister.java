package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CollectRegister {
    private Connection conn;

    public CollectRegister() {
        super();
    }

    public CollectRegister(Connection conn) {
        super();
        this.conn = conn;
    }

    public static String selcet_kq_emp(String userbase, String start_date, String end_date, String code, String whereIN) {
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select distinct a0100 from Q03");
        sqlstr.append(" where nbase='" + userbase + "'");
        sqlstr.append(" and Q03Z0 >= '" + start_date + "'");
        sqlstr.append(" and Q03Z0 <= '" + end_date + "'");
        sqlstr.append(" and b0110 ='" + code + "'");
        sqlstr.append(" and a0100 in(select a0100 " + whereIN + ")");
        sqlstr.append(" and Q03Z5 in ('01','07')");
        return sqlstr.toString();
    }

    public static ArrayList getStatListSql(ArrayList a0100list, String wherestr, String statcolumnstr) {
        ArrayList statlist = new ArrayList();
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select nbase,a0100, ");
        sqlstr.append(Sql_switcher.isnull("e01a1", "''") + " as e01a1,");
        sqlstr.append(Sql_switcher.isnull("b0110", "''") + " as b0110,");
        sqlstr.append(Sql_switcher.isnull("e0122", "''") + " as e0122,");
        sqlstr.append(Sql_switcher.isnull("a0101", "''") + " as a0101,");
        sqlstr.append(statcolumnstr);
        sqlstr.append(wherestr);
        for (int i = 0; i < a0100list.size(); i++) {
            String sql = sqlstr.toString() + " and a0100='" + a0100list.get(i).toString() + "'";
            sql = sql + " group by nbase,a0100," + Sql_switcher.isnull("e01a1", "''") + "," + Sql_switcher.isnull("b0110", "''")
                    + ",";
            sql = sql + Sql_switcher.isnull("e0122", "''") + "," + Sql_switcher.isnull("a0101", "''");
            statlist.add(sql);
        }
        return statlist;
    }

    public static String insertSQL(String insertcolumn) {
        StringBuffer insertsql = new StringBuffer();
        StringBuffer valuesql = new StringBuffer();
        int i = 0;
        int r = 0;
        insertcolumn = insertcolumn + ",";
        insertsql.append("insert into Q05 (");
        insertsql.append("nbase,a0100,e01a1,b0110,e0122,a0101,");
        valuesql.append(" values(");
        while (i != -1) {
            i = insertcolumn.indexOf(",", r);
            if (i != -1) {
                String str = insertcolumn.substring(r, i);
                str = str.trim();
                insertsql.append(str + ",");
                valuesql.append("?,");
            }
            r = i + 1;
        }
        insertsql.append("Q03Z0,Q03Z5,scope,Q03Z3,i9999)");
        valuesql.append("?,?,?,?,?,?,?,?,?,?,?)");
        String sqlstr = insertsql.toString() + valuesql.toString();
        return sqlstr;
    }

    public static String getWhereSQL(String userbase, String code, String start_date, String end_date, String whereIN,
            String tablename) {
        StringBuffer wheresql = new StringBuffer();
        wheresql.append(" from " + tablename + " ");
        wheresql.append(" where Q03Z0 >= '" + start_date + "'");
        wheresql.append(" and Q03Z0 <= '" + end_date + "%'");
        wheresql.append(" and b0110 ='" + code + "'");
        wheresql.append(" and nbase='" + userbase + "'");
        wheresql.append("and a0100 in(select a0100 " + whereIN + ")");
        wheresql.append(" and Q03Z5 in ('01','07')");
        return wheresql.toString();
    }

    public static ArrayList getSqlstr(ArrayList fieldsetlist, String userbase, String kq_duration, String code, String whereIN,
            String kind, String tablename) {

        StringBuffer wheresql = new StringBuffer();

        //生成没有高级条件的from后的sql语句
        StringBuffer column = new StringBuffer();
        for (int i = 0; i < fieldsetlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fieldsetlist.get(i);
            column.append(fielditem.getItemid() + ",");
        }
        int l = column.toString().length() - 1;
        String columnstr = column.toString().substring(0, l);
        String sqlstr = "select " + columnstr + " ";
        wheresql.append(" from " + tablename + " ");
        wheresql.append(" where Q03Z0 = '" + kq_duration + "'");
        if ("1".equals(kind)) {
            wheresql.append(" and e0122 like '" + code + "%'");
        } else if ("0".equals(kind)) {
            wheresql.append(" and e01a1 like '" + code + "%'");
        } else {
            wheresql.append(" and b0110 like '" + code + "%'");
        }

        wheresql.append(" and nbase='" + userbase + "'");
        wheresql.append(" and a0100 in(select a0100 " + whereIN + ")");
        String ordeby = " order by Q03Z5 DESC,b0110,e0122,e01a1";
        ArrayList list = new ArrayList();
        list.add(0, sqlstr);
        list.add(1, wheresql.toString());
        list.add(2, ordeby);
        list.add(3, columnstr);
        return list;
    }

    public static ArrayList reState(ArrayList fielditemlist) {
        ArrayList fieldlist = new ArrayList();
        for (int i = 0; i < fielditemlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            if (!"state".equals(fielditem.getItemid())) {
                fieldlist.add(fielditemlist.get(i));
            }
        }
        return fieldlist;
    }

    /******
     * 是否进行月汇总
     * @param fielditemid
     *       考勤指标项
     * @return
     *     want_sum=0;不汇总
     *     want_sum=1；汇总
     * */
    public static int getWant_Sum(String fielditemid, Connection conn) throws GeneralException {
        String kq_item_sql = "select want_sum from kq_item where UPPER(fielditemid)='" + fielditemid.toUpperCase() + "'";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(conn);
        int want_sum = 0;
        try {
            rowSet = dao.search(kq_item_sql.toString());
            if (rowSet.next()) {
                want_sum = rowSet.getInt("want_sum");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return want_sum;
    }

    public static String getWant_Sum(Connection conn) throws GeneralException {
        StringBuffer str = new StringBuffer();
        String kq_item_sql = "select lower(fielditemid) fielditemid from kq_item where want_sum=1";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(conn);
        try {
            rowSet = dao.search(kq_item_sql.toString());
            while (rowSet.next()) {
                str.append("," + rowSet.getString("fielditemid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        
        return str.substring(1);
    }

    public static String selcet_kq_one_emp(String userbase, String a0100, String start_date, String end_date, String code,
            String kind, String column) {
        StringBuffer sqlstr = new StringBuffer();
        int l = column.toString().length() - 1;
        String columnstr = column.toString().substring(0, l);
        sqlstr.append("select " + columnstr + " from Q03");
        sqlstr.append(" where nbase='" + userbase + "'");
        sqlstr.append(" and Q03Z0 >= '" + start_date + "'");
        sqlstr.append(" and Q03Z0 <= '" + end_date + "%'");
        if ("1".equals(kind)) {
            sqlstr.append(" and e0122 like '");
        } else if ("0".equals(kind)) {
            sqlstr.append(" and e01a1 like '");
        } else {
            sqlstr.append(" and b0110 like '");
        }
        sqlstr.append(code);
        sqlstr.append("%'");
        sqlstr.append(" and a0100='" + a0100 + "'");

        return sqlstr.toString();
    }

    /***************检索月考勤期间******************/
    public static String getMonthRegisterDate(String start_date, String end_date) {
        String kq_period = "";
        start_date = start_date.replaceAll("-", "\\.");
        end_date = end_date.replaceAll("-", "\\.");
        kq_period = start_date + "-" + end_date;
        return kq_period;
    }

    /**
     * 不定期汇总统计
     * **/
    public static ArrayList ambiFieldItemList(ArrayList fielditemlist) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < fielditemlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            if (!"i9999".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid())
                    && !"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid())) {
                if ("a0100".equals(fielditem.getItemid()) || "q03z0".equals(fielditem.getItemid())
                        || "scope".equals(fielditem.getItemid())
                        || "modtime".equalsIgnoreCase(fielditem.getItemid())
                        || "modusername".equalsIgnoreCase(fielditem.getItemid())) {
                    fielditem.setVisible(false);
                } else {
                    if ("1".equals(fielditem.getState())) {

                        fielditem.setVisible(true);
                    } else {
                        fielditem.setVisible(false);
                    }
                }
                list.add(fielditem.clone());
            }
        }
        return list;
    }

    public static ArrayList getSqlstr2(ArrayList fieldsetlist, ArrayList kq_dbase_list, String kq_duration, String code,
            String kind, String tablename, UserView userView, String showtype, String where_c) {

        StringBuffer wheresql = new StringBuffer();
        StringBuffer condition = new StringBuffer();//打印高级花名册的条件
        //生成没有高级条件的from后的sql语句
        StringBuffer column = new StringBuffer();
        for (int i = 0; i < fieldsetlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fieldsetlist.get(i);

            column.append(fielditem.getItemid() + ",");
        }
        //int l=column.toString().length()-1;		
        //String columnstr=column.toString().substring(0,l);
        column.append("state");
        String columnstr = column.toString();
        String sqlstr = "select " + columnstr + " ";
        wheresql.append(" from " + tablename + " where");
        condition.append("  Q03Z0 = '" + kq_duration + "'");
        if (code == null || code.length() <= 0) {
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        }
        // ------获得考勤部门的人员------start
        /*if(kind.equals("1"))
        {
        	condition.append(" and e0122 like '"+code+"%'");
        }else if(kind.equals("0"))
        {
        	condition.append(" and e01a1 like '"+code+"%'");	
        }else
        {
        	//更改
        	if(userView.isSuper_admin())
        	{
        		condition.append(" and b0110 like '"+code+"%'");
        	}else
        	{
        		condition.append(" and e0122 like '"+code+"%'");
        	}
        }*/
        if (!userView.isSuper_admin()) {
            if ("1".equals(kind)) {
                condition.append(" and (e0122 like '" + code + "%'");
            } else if ("0".equals(kind)) {
                condition.append(" and (e01a1 like '" + code + "%'");
            } else {
                //更改
                if (userView.isSuper_admin()) {
                    condition.append(" and (b0110 like '" + code + "%'");
                } else {
                    condition.append(" and (e0122 like '" + code + "%'");
                }
            }

            for (int i = 0; i < kq_dbase_list.size(); i++) {
                condition.append(RegisterInitInfoData.getKQ_DEPT_SQL_OR(code, kq_dbase_list.get(i).toString(), userView,
                        tablename));

            }
            condition.append(")");
        } else {
            if ("1".equals(kind)) {
                condition.append(" and e0122 like '" + code + "%'");
            } else if ("0".equals(kind)) {
                condition.append(" and e01a1 like '" + code + "%'");
            } else {
                //更改
                if (userView.isSuper_admin()) {
                    condition.append(" and b0110 like '" + code + "%'");
                } else {
                    condition.append(" and e0122 like '" + code + "%'");
                }
            }

        }
        // ------获得考勤部门的人员------end

        if (!"all".equals(showtype)) {
            condition.append(" and q03z5='" + showtype + "'");
        }
        if (where_c != null && where_c.length() > 0) {
            condition.append(" " + where_c + "");
        }
        for (int i = 0; i < kq_dbase_list.size(); i++) {
            String userbase = kq_dbase_list.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, userbase);
            if (i > 0) {
                condition.append(" or ");
            } else {
                condition.append(" and ( ");
            }
            condition.append(" (UPPER(nbase)='" + kq_dbase_list.get(i).toString().toUpperCase() + "'");
            // ------获得考勤部门的人员------start
            // condition.append(" and a0100 in(select a0100 "+whereIN+") "); 
            String acode = RegisterInitInfoData.getKqPrivCodeValue(userView);
            if ((acode == null || acode.length() <= 0) && !userView.isSuper_admin()) {
                condition.append(" and a0100 in(select " + kq_dbase_list.get(i).toString() + "A01.a0100 " + whereIN + ") ");
            } else {
                condition.append(" and a0100 in(select " + kq_dbase_list.get(i).toString() + "A01.a0100 " + whereIN
                        + RegisterInitInfoData.getUnionSql(userView, userbase) + ") ");
            }
            // ------获得考勤部门的人员------end
            condition.append(")");
            if (i == kq_dbase_list.size() - 1) {
                condition.append(")");
            }
        }

        //		String ordeby=" order by b0110,e0122,e01a1,a0100";
        String ordeby = " order by dbid,a0000";
        wheresql.append(" " + condition.toString());
        ArrayList list = new ArrayList();
        list.add(0, sqlstr);
        //System.out.println(wheresql.toString());
        list.add(1, wheresql.toString());
        list.add(2, ordeby);
        list.add(3, columnstr);
        list.add(4, condition.toString());
        return list;
    }

    public static ArrayList getSqlstr5(ArrayList fieldsetlist, ArrayList kq_dbase_list, String kq_duration, String code,
            String kind, String tablename, UserView userView, String showtype, String where_c, Connection conn) {

        StringBuffer wheresql = new StringBuffer();
        StringBuffer condition = new StringBuffer();//打印高级花名册的条件
        KqParameter para = new KqParameter(userView, "", conn);
        HashMap hashmap = para.getKqParamterMap();
        String g_no = (String) hashmap.get("g_no");
        String cardno = (String) hashmap.get("cardno");
        
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        //生成没有高级条件的from后的sql语句
        StringBuffer column = new StringBuffer();
        StringBuffer columnJoin = new StringBuffer();
        for (int i = 0; i < fieldsetlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fieldsetlist.get(i);
            String itemid = fielditem.getItemid();
            if ("A0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid) || "e0122".equalsIgnoreCase(itemid)) {
                columnJoin.append("Q." + fielditem.getItemid() + ",");
            } else if (g_no.equalsIgnoreCase(itemid) || cardno.equalsIgnoreCase(itemid)) {
                columnJoin.append("A." + fielditem.getItemid() + ",");
            } else {
                columnJoin.append(fielditem.getItemid() + ",");
            }
            column.append(fielditem.getItemid() + ",");
        }
        //int l=column.toString().length()-1;		
        //String columnstr=column.toString().substring(0,l);
        column.append("state");
        columnJoin.append("state,dbid,A0000");
        
        String field = KqParam.getInstance().getKqDepartment();
        //考勤部门指标是否在考勤日明细或月汇总中
        boolean kqDepartmentFieldInKqTab = ("," + columnJoin.toString().toLowerCase() + ",").contains("," + field.toLowerCase() + ",");
        //考勤部门指标不在日明细或月汇总中，拼接该指标
        if (field != null && !"".equals(field) && !kqDepartmentFieldInKqTab) {
            columnJoin.append(" ," + field);
        }
        
        String columnstr = column.toString();
        String sqlstr = "select " + columnstr + " ";
        wheresql.append(" from ");
        StringBuffer joinTable = new StringBuffer();

        String str = "";
        if ("1".equals(kind)) {
            str = "E0122";
        } else if ("0".equals(kind)) {
            str = "E01A1";
        } else {
            str = "B0110";
        }
        if (code == null || code.length() <= 0) {
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        }

        for (Iterator it = kq_dbase_list.iterator(); it.hasNext();) {
            String nbase = (String) it.next();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            if (!"".equalsIgnoreCase(field)) {
                if (code == null || code.length() <= 0) {//走管理范围
                    code = userView.getManagePrivCodeValue();
                    if (whereIN.toLowerCase().indexOf("where") != -1) {
                        whereIN += " OR " + field + " like '" + code + "%'";
                    } else {
                        whereIN += " where 1=1 OR " + field + " like '" + code + "%'";
                    }
                } else {// 考勤范围
                    if (whereIN.toLowerCase().indexOf("where") != -1) {
                        whereIN += " OR " + field + " like '" + code + "%'";
                    } else {
                        whereIN += " where 1=1 OR " + field + " like '" + code + "%'";
                    }
                }
            }
            if (joinTable.length() < 1) {
                if ("".equals(field) || field == null || kqDepartmentFieldInKqTab) {
                    joinTable.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + whereIN);
                } else {
                    joinTable.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + "," + field + whereIN);
                }
            } else {
                if ("".equals(field) || field == null || kqDepartmentFieldInKqTab) {
                    joinTable.append(" UNION SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + whereIN);
                } else {
                    joinTable.append(" UNION SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + "," + field + whereIN);
                }
            }
        }
        wheresql.append("(SELECT " + columnJoin + " FROM " + tablename + " Q INNER JOIN (" + joinTable
                + ") A ON Q.A0100=A.A0100 AND Q.nbase=A.nbase) B");
        wheresql.append(" where");
        condition.append("  Q03Z0 = '" + kq_duration + "'");
        // ------获得考勤部门的人员------start
        if (!"".equalsIgnoreCase(field)) {
            condition.append(" AND (" + str + " LIKE '" + code + "%' OR B." + field + " LIKE '" + code + "%')");
        } else {
            condition.append(" AND " + str + " LIKE '" + code + "%'");
        }

        // ------获得考勤部门的人员------end

        if (!"all".equals(showtype)) {
            condition.append(" and q03z5='" + showtype + "'");
        }
        if (where_c != null && where_c.length() > 0) {
            condition.append(" " + where_c + "");
        }
        wheresql.append(" " + condition.toString());
        for (int i = 0; i < kq_dbase_list.size(); i++) {
            String userbase = kq_dbase_list.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, userbase);
            if (i > 0) {
                condition.append(" or ");
            } else {
                condition.append(" and ( ");
            }
            condition.append(" (nbase='" + userbase + "'");
            // ------获得考勤部门的人员------start
            // condition.append(" and a0100 in(select a0100 "+whereIN+") ");
            condition.append(" and a0100 in(select " + userbase + "A01.a0100 " + whereIN
                    + RegisterInitInfoData.getUnionSql(userView, userbase) + ") ");
            // ------获得考勤部门的人员------end
            condition.append(")");
            if (i == kq_dbase_list.size() - 1) {
                condition.append(")");
            }
        }
        String ordeby = " order by dbid,a0000";
        ArrayList list = new ArrayList();
        list.add(0, sqlstr);
        list.add(1, wheresql.toString());
        list.add(2, ordeby);
        list.add(3, columnstr);
        list.add(4, condition.toString());
        return list;
    }

    public static ArrayList newFieldItemList(ArrayList fielditemlist, Connection conn) {
        DbWizard db = new DbWizard(conn);
        
        ArrayList list = new ArrayList();
        for (int i = 0; i < fielditemlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            if ("A".equals(fielditem.getItemtype()) || "N".equals(fielditem.getItemtype()) || "D".equals(fielditem.getItemtype())) {
                if ("1".equals(fielditem.getState())) {
                    fielditem.setVisible(true);
                } else {
                    fielditem.setVisible(false);
                }
                
                if (!"state".equalsIgnoreCase(fielditem.getItemid())) {
                    list.add(fielditem.clone());
                }
                
                if ("a0101".equalsIgnoreCase(fielditem.getItemid())) {
                    if (db.isExistField("Q03", "modtime", false) && !RegisterInitInfoData.itemExist(fielditemlist, "modtime")) {
                        FieldItem fielditem1 = new FieldItem("Q05", "modtime");
                        fielditem1.setItemtype("D");
                        fielditem1.setCodesetid("0");
                        fielditem1.setItemdesc("操作时间");
                        fielditem1.setVisible(true);
                        list.add(fielditem1);
                    }
                    
                    if (db.isExistField("Q03", "modusername", false) && !RegisterInitInfoData.itemExist(fielditemlist, "modusername")) {
                        FieldItem fielditem2 = new FieldItem("Q05", "modusername");
                        fielditem2.setItemtype("A");
                        fielditem2.setCodesetid("0");
                        fielditem2.setItemdesc("操作用户");
                        fielditem2.setVisible(true);
                        list.add(fielditem2);
                    }
                }
            }

        }

        return list;
    }

    /**
     * 断Q03中那些指标是从A01主集中取得的
     * @param itemtype
     * @param itemid
     * @param itemdesc
     * @return
     * @deprecated 无用的方法，不要再调用了
     */
    public boolean getindexA01(String itemtype, String itemid, String itemdesc, Connection conn) {
        boolean field = false;
        itemtype = itemtype.toUpperCase();
        itemid = itemid.toUpperCase();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        String sql = "select itemid from fielditem where fieldsetid='A01' and itemid='" + itemid + "' and itemtype='" + itemtype
                + "' and itemdesc='" + itemdesc + "'";
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                field = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return field;
    }

    /******************************************/

    /**
     * 月汇总
     * @param conn
     * @param start_date
     * @param end_date
     * @param dblist
     * @param userView
     * @throws GeneralException
     */
    public void collectData(Connection conn, UserView userView, String start_date, String end_date, ArrayList dblist)
            throws GeneralException {
        String kq_duration = RegisterDate.getDurationFromDate(start_date, conn);
        ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);

        ContentDAO dao = new ContentDAO(conn);
        KqParameter para = new KqParameter(userView, "", conn);
        HashMap hashmap = para.getKqParamterMap();
        String kq_type = (String) hashmap.get("kq_type");//考勤方式字段
        StringBuffer statcolumn = new StringBuffer();
        StringBuffer insertcolumn = new StringBuffer();
        StringBuffer un_statcolumn = new StringBuffer();
        StringBuffer un_insertcolumn = new StringBuffer();
        DbWizard dbWizard = new DbWizard(conn);
        String sdao_count_field = SystemConfig.getPropertyValue("sdao_count_field"); //得到上岛标识 对应的字段
        //		   String retud=gettichu(sdao_count_field,dao);
        /*
         * 首钢 上岛标识 不在考勤规则里，但是月统计还需要计算进来；这里过滤一下
         */
        if ("".equals(sdao_count_field) || sdao_count_field.length() < 0) {
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                //类型为N的时候，如果指标为主集中的指标也不能计算
                boolean booindex = getindexA01(fielditem.getItemtype(), fielditem.getItemid(), fielditem.getItemdesc(), dao);
                {
                    if ("N".equals(fielditem.getItemtype())) {

                        if (!"i9999".equals(fielditem.getItemid())) {
                            int want_sum = getWant_Sum(fielditem.getItemid(), conn);

                            if (want_sum == 1) {
                                statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                insertcolumn.append("" + fielditem.getItemid() + ",");

                            }
                            un_statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                            un_insertcolumn.append("" + fielditem.getItemid() + ",");
                        }
                    }
                }

            }
        } else {
            if (dbWizard.isExistField("Q03", sdao_count_field.toLowerCase())) {
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    //类型为N的时候，如果指标为主集中的指标也不能计算
                    boolean booindex = getindexA01(fielditem.getItemtype(), fielditem.getItemid(), fielditem.getItemdesc(), dao);
                    {
                        if ("N".equals(fielditem.getItemtype())) {

                            if (!"i9999".equals(fielditem.getItemid())) {
                                int want_sum = getWant_Sum(fielditem.getItemid(), conn);

                                if (want_sum == 1 || sdao_count_field.equalsIgnoreCase(fielditem.getItemid())) {
                                    statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                    insertcolumn.append("" + fielditem.getItemid() + ",");

                                }
                                un_statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                un_insertcolumn.append("" + fielditem.getItemid() + ",");
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    //类型为N的时候，如果指标为主集中的指标也不能计算
                    boolean booindex = getindexA01(fielditem.getItemtype(), fielditem.getItemid(), fielditem.getItemdesc(), dao);
                    {
                        if ("N".equals(fielditem.getItemtype())) {

                            if (!"i9999".equals(fielditem.getItemid())) {
                                int want_sum = getWant_Sum(fielditem.getItemid(), conn);

                                if (want_sum == 1) {
                                    statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                    insertcolumn.append("" + fielditem.getItemid() + ",");

                                }
                                un_statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                un_insertcolumn.append("" + fielditem.getItemid() + ",");
                            }
                        }
                    }
                }
            }
        }
        String statcolumnstr = "";
        String insertcolumnstr = "";
        if (statcolumn.toString() != null & statcolumn.toString().length() > 0) {
            int l = statcolumn.toString().length() - 1;
            statcolumnstr = statcolumn.toString().substring(0, l);
            l = insertcolumn.toString().length() - 1;
            insertcolumnstr = insertcolumn.toString().substring(0, l);
        } else {
            int l = un_statcolumn.toString().length() - 1;
            statcolumnstr = un_statcolumn.toString().substring(0, l);
            l = un_insertcolumn.toString().length() - 1;
            insertcolumnstr = un_insertcolumn.toString().substring(0, l);
        }
        String kq_period = getMonthRegisterDate(start_date, end_date);
        String mainindex = getMainSQL(dao, false);
        String mainindex1 = getMainSQL(dao, true);
        for (int r = 0; r < dblist.size(); r++) {
            String base = dblist.get(r).toString();

            delCollectRecordNotInQ03(base, kq_duration, start_date, end_date);
            
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, base);
            //if (!userView.isSuper_admin()) {

                //判断是否可以统计（起草，还是执行中）
                //String whereB0110 = RegisterInitInfoData.selcet_OrgId(base, "b0110", whereIN);
                //ArrayList orgidb0110List = OrgRegister.getQrgE0122List(conn, whereB0110, "b0110");
                //for (int t = 0; t < orgidb0110List.size(); t++) {
                //    String b0110_one = orgidb0110List.get(t).toString();
                //    String nbase = RegisterInitInfoData.getOneB0110Dase(new HashMap(), userView, base, b0110_one, conn);
                //    if (nbase != null && nbase.length() > 0) {
                        synchronizationInitQ05(base, "", kq_duration, start_date, end_date, whereIN, userView, conn);
                        boolean if_delete = delRecord(base, "", kq_duration, start_date, end_date, whereIN, conn);
                        if (if_delete) {
                            collectRecord2(dao, base, start_date, end_date, "", null, null, fielditemlist, whereIN,
                                    kq_duration, kq_type, insertcolumnstr, statcolumnstr, kq_period, mainindex, mainindex1);
                //        }
                //    }
                //}

            //} else {
                //ArrayList b0100list = RegisterInitInfoData.getAllBaseOrgid(base, "b0110", whereIN, conn);
                //for (int n = 0; n < b0100list.size(); n++) {
                //    String b0110_one = b0100list.get(n).toString();
                //    String nbase = RegisterInitInfoData.getOneB0110Dase(new HashMap(), userView, base, b0110_one, conn);
                    /********按照该单位的人员库的操作*********/
                //    if (nbase != null && nbase.length() > 0) {

                //        synchronizationInitQ05(nbase, b0110_one, kq_duration, start_date, end_date, whereIN, userView, conn);
                 //       boolean if_delete = delRecord(nbase, b0110_one, kq_duration, start_date, end_date, whereIN, conn); //超级用户用下面的	   
                 //       if (if_delete) {
                 //           collectRecord2(dao, nbase, start_date, end_date, b0110_one, null, null, fielditemlist, whereIN,
                 //                   kq_duration, kq_type, insertcolumnstr, statcolumnstr, kq_period, mainindex, mainindex1);
                        }
               //     }
               // }

           // }
        }
        KqUtilsClass kqUtilsClass = new KqUtilsClass(conn, userView);
        kqUtilsClass.leadingInItemToQ05(dblist, start_date, end_date, "", "", kq_duration);//加入导入项
        //对月汇总进行计算
        CountMoInfo countMoInfo = new CountMoInfo(userView, conn);
        countMoInfo.countKQInfo(kq_duration);
    }

    private boolean getindexA01(String itemtype, String itemid, String itemdesc, ContentDAO dao) {
        boolean field = true;
        itemtype = itemtype.toUpperCase();
        itemid = itemid.toUpperCase();
        RowSet rs = null;
        String sql = "select itemid from fielditem where fieldsetid='A01' and itemid='" + itemid + "' and itemtype='" + itemtype
                + "' and itemdesc='" + itemdesc + "'";
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String itemi = rs.getString("itemid");
                if (!"A0101".equals(itemi) && !"E0122".equals(itemi)) {
                    if (itemi != null && itemi.length() > 0) {
                        field = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return field;
    }

    /**
     * 判断Q03是否从主集中导入指标
     * destTab=Q03
     * srcTab=Q05
     * @return
     */
    private String getMainSQL(ContentDAO dao, boolean withMax) {
        String selectSQL = "";
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        sql.append("select itemid,itemtype,itemdesc from fielditem where fieldsetid='A01' and useflag='1'");
        RowSet rowSet = null;
        try {
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                ArrayList noblist = new ArrayList();
                String itemid = rowSet.getString("itemid");
                String itemtype = rowSet.getString("itemtype");
                String itemdesc = rowSet.getString("itemdesc");
                noblist.add(itemid);
                noblist.add(itemtype);
                noblist.add(itemdesc);
                list.add(noblist);
            }
            
            for (int i = 0; i < list.size(); i++) {
                ArrayList lists = (ArrayList) list.get(i);
                String nobitemid = (String) lists.get(0);
                String nobitemtype = (String) lists.get(1);
                String nobitemdesc = (String) lists.get(2);
                sql.setLength(0);
                sql.append("select itemid from t_hr_busifield where fieldsetid='Q03' and useflag='1' ");
                sql.append("and itemid='" + nobitemid);
                sql.append("' and itemtype='" + nobitemtype);
                sql.append("' and itemdesc='" + nobitemdesc + "'");
                rowSet = dao.search(sql.toString());
                while (rowSet.next()) {
                    String itemid = rowSet.getString("itemid");
                    if ("A0101".equals(itemid) || "A0100".equals(itemid) || "B0110".equals(itemid)
                            || "E0122".equals(itemid) || "E01A1".equals(itemid)) {
                        continue;
                    }
                    
                    if (withMax) {
                        selectSQL += "MAX(" + itemid + "),";
                    } else {
                        selectSQL += itemid + ",";
                    }
                     
                }
            }
            
            if (selectSQL.length() > 0) {
                selectSQL = selectSQL.substring(0, selectSQL.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return selectSQL;
    }

    public void synchronizationInitQ05(String nbase, String b0110_one, String kq_duration, String start_date, String end_date,
            String whereIN, UserView userView, Connection conn) throws GeneralException {

        String destTab = "q05";//目标表
        String srcTab = "q03";//源表

        String strJoin = "q05.A0100=" + srcTab + ".A0100 and q05.nbase=" + srcTab + ".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
        String strSet = "q05.q03z5=" + srcTab + ".q03z5";//更新串  xxx.field_name=yyyy.field_namex,....
        String strDWhere = "q05.nbase='" + nbase + "' and q05.q03z0='" + kq_duration + "'";//更新目标的表过滤条件
        String strSWhere = " q03.nbase='" + nbase + "'  and " + srcTab + ".q03z0='" + end_date + "'";//源表的过滤条件  
        if (!userView.isSuper_admin()) {
            if (whereIN != null && (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)) {
                strSWhere = strSWhere
                        + (" and EXISTS(select a0100 " + whereIN + " and " + srcTab + ".a0100=" + nbase + "A01.a0100)");
                strDWhere = strDWhere
                        + (" and EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + nbase + "A01.a0100)");
            } else {
                strSWhere = strSWhere
                        + (" and EXISTS(select a0100 " + whereIN + " where " + srcTab + ".a0100=" + nbase + "A01.a0100)");
                strDWhere = strDWhere
                        + (" and EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + nbase + "A01.a0100)");
            }

        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        String othWhereSql = "";
        if (!userView.isSuper_admin()) {
            if (whereIN != null && (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)) {
                othWhereSql = (" and EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + nbase + "A01.a0100)");
            } else {
                othWhereSql = (" and EXISTS(select a0100 " + whereIN + " where " + destTab + ".a0100=" + nbase + "A01.a0100)");
            }

        }
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        ContentDAO dao = new ContentDAO(conn);
        try {
            dao.update(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除没有日明细记录的月汇总数据
     * @Title: delCollectRecordNotInQ03   
     * @Description: 删除没有日明细记录的月汇总数据   
     * @param nbase  人员库
     * @param duration 考勤期间  如： 2014-09
     * @param durStart 考勤期间开始日期   如： 2014.09.01
     * @param durEnd   考勤期间结束日期   如：2014.09.29
     * @return
     */
    public boolean delCollectRecordNotInQ03(String nbase, String duration, String durStart, String durEnd) {
        boolean iscorrect = false;
        try {
            ContentDAO dao = new ContentDAO(conn);
            //判断是否已经汇总过
            StringBuffer sql = new StringBuffer();
            sql.append("delete from Q05 where");
            sql.append(" nbase=?");
            sql.append(" and Q03Z0=?");
            sql.append(" and not exists(select 1 from Q03 where Q03.nbase=?");
            sql.append(" and Q03.q03z0>=?");
            sql.append(" and Q03.q03z0<=?");
            sql.append(" and Q03.nbase=Q05.nbase and Q03.a0100=Q05.a0100)");
            
            ArrayList sqlParams = new ArrayList();
            sqlParams.add(nbase);
            sqlParams.add(duration);
            sqlParams.add(nbase);
            sqlParams.add(durStart);
            sqlParams.add(durEnd);

            dao.delete(sql.toString(), sqlParams);
            iscorrect = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iscorrect;
    }
    
    public boolean delRecord(String userbase, String code, String kq_duration, String start_date, String end_date,
            String whereIN, Connection conn) {
        boolean iscorrect = false;
        try {
            ContentDAO dao = new ContentDAO(conn);
            //判断是否已经汇总过
            StringBuffer delete_kq_Sum = new StringBuffer();
            delete_kq_Sum.append("delete from Q05 where");
            delete_kq_Sum.append(" nbase='" + userbase + "'");
            delete_kq_Sum.append(" and Q03Z0='" + kq_duration + "'");
//            delete_kq_Sum.append(" and a0100 in(select a0100 from q03");
//            delete_kq_Sum.append(" where nbase='" + userbase + "'");
//            delete_kq_Sum.append(" and Q03Z0 >= '" + start_date + "'");
//            delete_kq_Sum.append(" and Q03Z0 <= '" + end_date + "'");
            delete_kq_Sum.append(" and b0110 like '" + code + "%'");
            delete_kq_Sum.append(" and a0100 in(select a0100 " + whereIN + ")");
            delete_kq_Sum.append(" and Q03Z5 in ('01','07')");
//            delete_kq_Sum.append(")");

            dao.delete(delete_kq_Sum.toString(), new ArrayList());
            iscorrect = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iscorrect;
    }

    /**************汇总纪录****************
     * * @param fieldsetlist 操作表的子集
     * @param userbase  数据库前缀
     * @param collectdate  操作时间
     * @param code 部门	 * 
    
     * 
     * */

    public boolean collectRecord2(ContentDAO dao, String userbase, String start_date, String end_date, String code, String kind,
            String analyseTable, ArrayList fielditemlist, String whereIN, String kq_duration, String kq_typeField,
            String insertcolumnstr, String statcolumnstr, String kq_period, String mainindex, String mainindex1)
            throws GeneralException {
        boolean isCorrect = true;
        String table_name = "q05";
        //建立一张临时表
        String kindField = "";
        String EPNbase = "";
        if ("1".equals(kind)) {
            kindField = "e0122";
        } else if ("0".equals(kind)) {
            kindField = "e01a1";
        } else if ("-1".equals(kind)) {
            EPNbase = code.substring(0, 3);
            code = code.substring(3);
        } else {
            kindField = "b0110";
        }

        //插入汇总人员记录，修改：Q03Z3为从Q03表查询出来不为常量值‘0’
        StringBuffer sql = new StringBuffer();

        //主集中的指标
        if (!"".equals(mainindex) || mainindex.length() > 0) {
            sql.append("insert into " + table_name + "(a0100,nbase,q03z0," + insertcolumnstr + ", scope, Q03Z5," + mainindex
                    + ")");
            sql.append("select  a0100,'" + userbase + "','" + kq_duration + "'," + statcolumnstr + ",'" + kq_period + "','01',"
                    + mainindex1 + " from Q03");
        } else {
            sql.append("insert into " + table_name + "(a0100,nbase,q03z0," + insertcolumnstr + ", scope, Q03Z5)");
            sql.append(" select  a0100,'" + userbase + "','" + kq_duration + "'," + statcolumnstr + ",'" + kq_period
                    + "','01' from Q03");
        }
        sql.append(" where nbase = '" + userbase + "'");
        sql.append(" and Q03Z0 >= '" + start_date + "'");
        sql.append(" and Q03Z0 <= '" + end_date + "'");

        //if月汇总else数据处理确认后的月汇总
        if (analyseTable == null || "".equals(analyseTable)) {
            sql.append(" and "+Sql_switcher.isnull(kindField, "'#'")+" like '" + code + "%'");
        } else {
            if (!"-1".equals(kind)) {
                sql.append(" and EXISTS(select distinct a0100 from " + analyseTable);
                sql.append(" where " + analyseTable + ".a0100 = q03.a0100 and " + analyseTable + ".nbase = q03.nbase");
                sql.append(" and " + analyseTable + ".nbase = '" + userbase + "'");
                sql.append(" and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'") + " like '" + code + "%')");
                sql.append(" and " + Sql_switcher.isnull(kindField, "'#'") + " like '" + code + "%'");
            } else {
                sql.append(" and Q03.a0100 = '" + code + "' and Q03.nbase = '" + EPNbase + "'");
                sql.append(" and EXISTS(select distinct a0100 from " + analyseTable);
                sql.append(" where " + analyseTable + ".a0100 = q03.a0100 and " + analyseTable + ".nbase = q03.nbase");
                sql.append(" and " + analyseTable + ".nbase = '" + userbase + "')");
            }
        }

        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
            sql.append(" and EXISTS(select a0100 " + whereIN + " and " + userbase + "A01.a0100=q03.a0100)");
        } else {
            sql.append(" and EXISTS(select a0100 " + whereIN + " where " + userbase + "A01.a0100=q03.a0100)");
        }
        
        //zxj 增加判断Q05已有的不重新汇总
        sql.append(" and NOT EXISTS(SELECT 1 FROM ").append(table_name).append(" A");
        sql.append(" WHERE A.nbase=Q03.nbase and A.a0100=Q03.a0100");
        sql.append(" AND A.Q03Z0='").append(kq_duration).append("')");
        
        //zxj 20170606 不能只汇总起草或驳回的，如果出现部分数据是其它状态，那么汇总值就不完整
        //sql.append(" and ").append(Sql_switcher.isnull("Q03Z5", "'01'")).append(" in ('01','07')");
        sql.append(" GROUP BY  A0100 ");

        try {
            dao.insert(sql.toString(), new ArrayList());//插入汇总人员记录

            sql.delete(0, sql.length());
            String destTab = table_name;//目标表xxxxxxxxxxx
            String srcTab = userbase + "A01";//源表
            if (kq_typeField != null && kq_typeField.length() > 0) {
                String strJoin = destTab + ".A0100=" + srcTab + ".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
                String strSet = "" + destTab + ".q03z3=" + srcTab + "." + kq_typeField + "`" + destTab + ".a0101=" + srcTab
                        + ".a0101";
                String strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration + "' and "
                        + destTab + ".q03z5='01'  and " + destTab + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
                strDWhere = strDWhere + " and (" + destTab + ".a0101 is null or " + destTab + ".a0101 = '')";

                //if月汇总else数据处理确认后的月汇总
                String strSWhere = "";
                if (analyseTable == null || "".equals(analyseTable)) {
                    strSWhere = Sql_switcher.isnull(srcTab + "." + kindField, "'#'") + " like '" + code + "%'";//源表的过滤条件
                } else {
                    if (!"-1".equals(kind)) {
                        strDWhere = strDWhere + " and EXISTS(select a0100 from " + analyseTable;
                        strDWhere = strDWhere + " where " + destTab + ".a0100 = " + analyseTable + ".a0100 and " + destTab
                                + ".nbase = " + analyseTable + ".nbase";
                        strDWhere = strDWhere + " and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'") + " like '" + code + "%')";
                        strSWhere = Sql_switcher.isnull(srcTab + "." + kindField, "'#'") + " like '" + code + "%'";
                    } else {
                        strDWhere = strDWhere + " and " + destTab + ".a0100 = '" + code + "' and " + destTab + ".nbase = '"
                                + EPNbase + "'";
                    }
                }

                String othWhereSql = "";
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    othWhereSql = "EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + userbase + "A01.a0100)";
                } else {
                    othWhereSql = "EXISTS(select a0100 " + whereIN + " where " + destTab + ".a0100=" + userbase + "A01.a0100)";
                }
                String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
                //strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".b0110 ='"+code+"'";
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
                dao.update(update);
            }
            //同步日明细单位部门到月汇总
            destTab = table_name;//目标表xxxxxxxxxxx
            srcTab = "q03";//源表
            String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
            String strSet = destTab + ".b0110=" + srcTab + ".b0110`" + destTab + ".e0122=" + srcTab + ".e0122`" + destTab
                    + ".e01a1=" + srcTab + ".e01a1";//更新串  xxx.field_name=yyyy.field_namex,....
            String strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration + "'  and scope='"
                    + kq_period + "' and " + destTab + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
            strDWhere = strDWhere + " and (" + destTab + ".b0110 is null or " + destTab + ".b0110 = '')";
            //String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件 
            
            //zxj 应更新每个人在日明细中的最后一条记录的组织机构，而不是考勤期间结束日期的组织机构
            String strSWhere = srcTab + ".nbase='" + userbase 
                             + "'  and " + srcTab + ".Q03Z0=(select max(Q.Q03z0) from q03 Q" 
                             + " where Q.A0100=Q03.A0100  and Q.nbase='" + userbase
                             + "' and Q.Q03Z0>='" + start_date + "' and Q.Q03Z0<='" + end_date 
                             + "')  and " + srcTab + ".a0100 in(select a0100 " + whereIN + ")";//源表过滤条件

            //if月汇总else数据处理确认后的月汇总
            if (analyseTable == null || "".equals(analyseTable)) {
                strSWhere = strSWhere + " and " + Sql_switcher.isnull(srcTab + "." + kindField, "'#'") + " like '" + code + "%'";//源表的过滤条件
            } else {
                if (!"-1".equals(kind)) {
                    strDWhere = strDWhere + " and EXISTS(select a0100 from " + analyseTable;
                    strDWhere = strDWhere + " where " + destTab + ".a0100 = " + analyseTable + ".a0100 and " + destTab
                            + ".nbase = " + analyseTable + ".nbase";
                    strDWhere = strDWhere + " and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'") + " like '" + code + "%')";
                    strSWhere = strSWhere + " and " + Sql_switcher.isnull(srcTab + "." + kindField, "'#'") + " like '" + code + "%'";

                } else {
                    strDWhere = strDWhere + " and " + destTab + ".a0100 = '" + code + "' and " + destTab + ".nbase = '" + EPNbase
                            + "'";
                }
            }

            String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
            strJoin = strJoin + " and " + destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration
                    + "' and " + strSWhere;
            String othWhereSql = "";
            if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                othWhereSql = "EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + userbase + "A01.a0100)";
            } else {
                othWhereSql = "EXISTS(select a0100 " + whereIN + " where " + destTab + ".a0100=" + userbase + "A01.a0100)";
            }
            //update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
            //System.out.println(update);
            dao.update(update);
            //如果同步最后一天的单位部门没有,就同步最大部门的
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                srcTab = userbase + "A01";//源表
                strSet = destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab
                        + ".E01A1=" + srcTab + ".E01A1";//更新串  xxx.field_name=yyyy.field_namex,....
                strJoin = destTab + ".A0100=" + srcTab + ".A0100";
                strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration + "'and scope='"
                        + kq_period + "'  and " + destTab + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".b0110", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".e0122", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".e01a1", "'kong'") + "='kong'";
                //数据处理确认后的月汇总
                if (analyseTable != null && analyseTable.length() > 0) {
                    if (!"-1".equals(kind)) {
                        strDWhere = strDWhere + " and EXISTS(select a0100 from " + analyseTable;
                        strDWhere = strDWhere + " where " + destTab + ".a0100 = " + analyseTable + ".a0100 and " + destTab
                                + ".nbase = " + analyseTable + ".nbase";
                        strDWhere = strDWhere + " and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'") + " like '" + code + "%')";
                    } else {
                        strDWhere = strDWhere + " and " + destTab + ".a0100 = '" + code + "' and " + destTab + ".nbase = '"
                                + EPNbase + "'";
                    }
                }
                //strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    strSWhere = "EXISTS(select a0100 " + whereIN + " and " + srcTab + ".a0100=" + destTab + ".a0100)";
                } else {
                    strSWhere = "EXISTS(select a0100 " + whereIN + " where " + srcTab + ".a0100=" + destTab + ".a0100)";
                }
                update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
                //System.out.println(update);
                dao.update(update);
                break;
            }
            case Constant.ORACEL: {
                strSet = destTab + ".b0110=Max(" + srcTab + ".b0110)`" + destTab + ".e0122=Max(" + srcTab + ".e0122)`" + destTab
                        + ".e01a1=Max(" + srcTab + ".e01a1)`" + destTab + ".a0101=Max(" + srcTab + ".a0101)";//更新串  xxx.field_name=yyyy.field_namex,....
                strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
                strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration + "' and scope='"
                        + kq_period + "' and " + destTab + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".b0110", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".e0122", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".e01a1", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".a0101", "'kong'") + "='kong'";
                //数据处理确认后的月汇总
                if (analyseTable != null && analyseTable.length() > 0) {
                    if (!"-1".equals(kind)) {
                        strDWhere = strDWhere + " and EXISTS(select a0100 from " + analyseTable;
                        strDWhere = strDWhere + " where " + destTab + ".a0100 = " + analyseTable + ".a0100 and " + destTab
                                + ".nbase = " + analyseTable + ".nbase";
                        strDWhere = strDWhere + " and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'") + " like '" + code + "%')";
                    } else {
                        strDWhere = strDWhere + " and " + destTab + ".a0100 = '" + code + "' and " + destTab + ".nbase = '"
                                + EPNbase + "'";
                    }
                }
                //String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
                strSWhere = srcTab + ".nbase='" + userbase + "'  and " + srcTab + ".Q03Z0 >= '" + start_date + "' and " + srcTab
                        + ".Q03Z0<='" + end_date + "' and " + srcTab + ".b0110 like '" + code + "%' and " + srcTab
                        + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
                update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
                strJoin = strJoin + " and " + destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration
                        + "'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".b0110", "'kong'") + "='kong'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".e0122", "'kong'") + "='kong'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".e01a1", "'kong'") + "='kong'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".a0101", "'kong'") + "='kong'";
                //othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    othWhereSql = "EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + userbase + "A01.a0100)";
                } else {
                    othWhereSql = "EXISTS(select a0100 " + whereIN + " where " + destTab + ".a0100=" + userbase + "A01.a0100)";
                }
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
                dao.update(update);
                break;
            }
            case Constant.DB2: {
                strSet = destTab + ".b0110=Max(" + srcTab + ".b0110)`" + destTab + ".e0122=Max(" + srcTab + ".e0122)`" + destTab
                        + ".e01a1=Max(" + srcTab + ".e01a1)`" + destTab + ".a0101=Max(" + srcTab + ".a0101)";//更新串  xxx.field_name=yyyy.field_namex,....
                strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
                strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration + "' and scope='"
                        + kq_period + "'  and " + destTab + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".b0110", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".e0122", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".e01a1", "'kong'") + "='kong'";
                strDWhere = strDWhere + " and " + Sql_switcher.isnull(destTab + ".a0101", "'kong'") + "='kong'";
                //数据处理确认后的月汇总
                if (analyseTable != null && analyseTable.length() > 0) {
                    if (!"-1".equals(kind)) {
                        strDWhere = strDWhere + " and EXISTS(select a0100 from " + analyseTable;
                        strDWhere = strDWhere + " where " + destTab + ".a0100 = " + analyseTable + ".a0100 and " + destTab
                                + ".nbase = " + analyseTable + ".nbase";
                        strDWhere = strDWhere + " and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'") + " like '" + code + "%' and "
                                + analyseTable + ".nbase = '" + EPNbase + "')";
                    } else {
                        strDWhere = strDWhere + " and " + destTab + ".a0100 = '" + code + "' and " + destTab + ".nbase = '"
                                + EPNbase + "'";
                    }
                }
                //String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
                strSWhere = srcTab + ".nbase='" + userbase + "'  and " + srcTab + ".Q03Z0 >= '" + start_date + "' and " + srcTab
                        + ".Q03Z0<='" + end_date + "' and " + srcTab + ".b0110 like '" + code + "%' and " + srcTab
                        + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
                update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
                strJoin = strJoin + " and " + destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_duration
                        + "'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".b0110", "'kong'") + "='kong'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".e0122", "'kong'") + "='kong'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".e01a1", "'kong'") + "='kong'";
                strJoin = strJoin + " and " + Sql_switcher.isnull(destTab + ".a0101", "'kong'") + "='kong'";
                //othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    othWhereSql = "EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + userbase + "A01.a0100)";
                } else {
                    othWhereSql = "EXISTS(select a0100 " + whereIN + " where " + destTab + ".a0100=" + userbase + "A01.a0100)";
                }
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
                dao.update(update);
                break;
            }

            }

            // 月汇总时，将dbid及a0000一块添加到q05表中
            KqUtilsClass utils = new KqUtilsClass(conn);
            if (utils.addColumnToKq("q05")) {
                StringBuffer where = new StringBuffer();
                where.append(" where nbase='" + userbase + "'");
                //if月汇总else数据处理确认后的月汇总
                if (analyseTable == null || analyseTable.length() <= 0) {
                    where.append(" and "+Sql_switcher.isnull(kindField, "'#'")+" like '" + code + "%'");
                } else {
                    if (!"-1".equals(kind)) {
                        where.append(" and " + Sql_switcher.isnull(kindField, "'#'") + " like '" + code + "%'");
                    } else {
                        where.append(" and a0100 = '" + code + "' and nbase = '" + EPNbase + "'");
                    }
                }
                where.append(" and q03z0 ='" + kq_duration + "'");
                where.append(" and a0100 in(select a0100 " + whereIN + ")");
                utils.updateQ05(start_date, where.toString());
            }

            //同步q03
            sql.delete(0, sql.length());
            destTab = "q03";//目标表xxxxxxxxxxx
            srcTab = "q05";//源表
            strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
            strSet = destTab + ".q03z5=" + srcTab + ".q03z5";//更新串  xxx.field_name=yyyy.field_namex,....
            strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".Q03Z0 >= '" + start_date + "' and " + destTab
                    + ".Q03Z0 <= '" + end_date + "' and " + destTab + ".a0100 in(select a0100 " + whereIN + ")";//更新目标的表过滤条件
            //if月汇总else数据处理确认后的月汇总
            if (analyseTable == null || "".equals(analyseTable)) {
                strDWhere = strDWhere + "  and " + Sql_switcher.isnull("q03." + kindField, "'#'")+" like '" + code + "%'";//增加目标表过滤条件
            } else {
                if (!"-1".equals(kind)) {
                    strDWhere = strDWhere + " and " + Sql_switcher.isnull("q03." + kindField, "'#'")+ " like '" + code + "%'";
                    strDWhere = strDWhere + " and EXISTS(select a0100 from " + analyseTable;
                    strDWhere = strDWhere + " where " + destTab + ".a0100 = " + analyseTable + ".a0100 and " + destTab
                            + ".nbase = " + analyseTable + ".nbase";
                    strDWhere = strDWhere + " and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'") + " like '" + code + "%')";
                } else {
                    strDWhere = strDWhere + " and " + destTab + ".a0100 = '" + code + "' and " + destTab + ".nbase = '" + EPNbase
                            + "'";
                }
            }
            //strSWhere=srcTab+".nbase='"+userbase+"' and "+srcTab+".q03z0='"+kq_duration+"' and "+srcTab+".q03z5='01'  and "+srcTab+".a0100 in(select a0100 "+whereIN+")";//源表的过滤条件  
            strSWhere = srcTab + ".nbase='" + userbase + "' and " + srcTab + ".q03z0='" + kq_duration + "' and " + srcTab
                    + ".a0100 in(select a0100 " + whereIN + ")";//源表的过滤条件  去掉"+srcTab+".q03z5='01'
            //strSWhere="";
            update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
            //othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
            if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                othWhereSql = "EXISTS(select a0100 " + whereIN + " and " + destTab + ".a0100=" + userbase + "A01.a0100)";
            } else {
                othWhereSql = "EXISTS(select a0100 " + whereIN + " where " + destTab + ".a0100=" + userbase + "A01.a0100)";
            }
            //strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".b0110 ='"+code+"'";
            
            //zxj 20140420 此条件多余，并严重影响速度（hkyh 880人汇总 下面执行一次30秒，总共需执行60次） 
            //update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
            
            //System.out.println("---"+update);
            dao.update(update);
        } catch (Exception e) {
            isCorrect = false;
	    	StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);			    	
	    	if (sw.toString().indexOf("算术溢出错误") != -1 
                    || sw.toString().toLowerCase().indexOf("arithmetic overflow") != -1
	    			|| sw.toString().indexOf("允许精度") != -1 
	    			|| sw.toString().indexOf("数据类型") != -1) {
                throw new GeneralException("汇总失败！请前往系统管理-库结构-业务字典中，检查“员工考勤日明细表”是否有需要汇总的数值型指标长度太短，建议其整数部分至少定义为8位。");
            }
        }
        return isCorrect;

    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
