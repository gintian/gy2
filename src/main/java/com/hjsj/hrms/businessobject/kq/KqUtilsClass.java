package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.machine.UnKqClassBean;
import com.hjsj.hrms.businessobject.kq.options.imports.SearchImportBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.KqClassArray;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 突然发现很多方法太重复，以后重复的方法都放到这个里面，只为考勤考虑
 * <p>
 * Title:KqUtilsClass.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Dec 7, 2006 5:03:08 PM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class KqUtilsClass {
    private Connection conn;
    private UserView userview;
    private static boolean includeA01ForLeadingInItem = true;

     public static final String WEEKNAMES[] = { 
            ResourceFactory.getProperty("kq.kq_rest.sunday"),
            ResourceFactory.getProperty("kq.kq_rest.monday"), 
            ResourceFactory.getProperty("kq.kq_rest.tuesday"),
            ResourceFactory.getProperty("kq.kq_rest.wednesday"), 
            ResourceFactory.getProperty("kq.kq_rest.thursday"),
            ResourceFactory.getProperty("kq.kq_rest.firday"), 
            ResourceFactory.getProperty("kq.kq_rest.Saturday") 
            };
    public KqUtilsClass() {

    }

    public KqUtilsClass(Connection conn) {
        this.conn = conn;
    }

    public KqUtilsClass(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }

    public static String getWeekName(Date aDate) {
        String weekName = "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weekName = WEEKNAMES[dayOfWeek - 1];

        return weekName;
    }

    /**
     * 
     * @param SrcTab
     *            数据源,
     * @param DestTab
     *            临时表名
     * @param StrFldlst
     *            字段列表
     * @param strWhere
     *            条件
     * @param strGroupBy
     *            分组
     * @return
     */
    public String createTempTable(String srcTab, String destTab, String strFldlst, String strWhere, String strGroupBy) {
        StringBuffer strSql = new StringBuffer();
        dropTable(this.conn, destTab);
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                strSql.append("select " + strFldlst + " Into " + destTab + " from " + srcTab);
                if (strWhere != null && strWhere.length() > 0) {
                    strSql.append(" where " + strWhere);
                }

                if (strGroupBy != null && strGroupBy.length() > 0) {
                    strSql.append(" group by " + strGroupBy);
                }
                dao.update(strSql.toString());
                break;
            }
            case Constant.ORACEL: {
                strSql.append("Create Table " + destTab + " as select " + strFldlst + " from " + srcTab);
                if (strWhere != null && strWhere.length() > 0) {
                    strSql.append(" where " + strWhere);
                }
                if (strGroupBy != null && strGroupBy.length() > 0) {
                    strSql.append(" group by " + strGroupBy);
                }
                dao.update(strSql.toString());
                break;
            }
            case Constant.DB2: {
                strSql.append("SELECT " + strFldlst + " from " + srcTab);
                if (strWhere != null && strWhere.length() > 0) {
                    strSql.append(" where " + strWhere);
                }

                if (strGroupBy != null && strGroupBy.length() > 0) {
                    strSql.append(" group by " + strGroupBy);
                }

                dao.update("Create Table " + destTab + " AS (" + strSql + " ) DEFINITION ONLY");
                dao.update("INSERT INTO " + destTab + strSql);
                break;
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBMetaModel dbmodel = new DBMetaModel(conn);
        dbmodel.reloadTableModel(destTab);
        return destTab;
    }

    /**
     * 为表添加dbid、a0000两个字段
     * 
     * @param tableName
     * @return
     */
    public boolean addColumnToKq(String tableName) {
        boolean flag = false;
        DbWizard wiz = new DbWizard(conn);
        try {
            // 为q03添加dbid字段
            if (!wiz.isExistField(tableName, "dbid", false)) {
                Table table = new Table(tableName);
                Field field = new Field("dbid", "人员库顺序");
                field.setDatatype(DataType.INT);
                field.setKeyable(false);
                table.addField(field);
                wiz.addColumns(table);
            }
            // 为q03添加a0000字段
            if (!wiz.isExistField(tableName, "a0000", false)) {
                Table table = new Table(tableName);
                Field field = new Field("a0000", "人员所在人员库排序顺序");
                field.setDatatype(DataType.INT);
                field.setKeyable(false);
                table.addField(field);
                wiz.addColumns(table);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 为表添加dbid、a0000两个字段
     * 
     * @param tableName
     * @return
     */
    public boolean updateOrder(String tableName, String start, String end, String temp) {
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
                    dao.update(sql.toString());
                }
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return flag;
    }

    /**
     * 为表添加dbid、a0000两个字段
     * 
     * @param tableName
     * @return
     */
    public boolean updateQ05(String start, String where) {
        boolean flag = false;

        start = start.replaceAll("-", ".");

        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            sql.append("update q05 set dbid=(select dbid from q03 where q03.a0100=q05.a0100 and q03.nbase=q05.nbase and q03.q03z0='");
            sql.append(start);
            sql.append("'),a0000=(select a0000 from q03 where q03.a0100=q05.a0100 and q03.nbase=q05.nbase and q03.q03z0='");
            sql.append(start);
            sql.append("') ");
            sql.append(where);

            dao.update(sql.toString());
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    public boolean updateQ05all(String kq_duration) {
        boolean flag = false;
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            sql.append("select dbid,pre from dbname");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String dbid = rs.getString("dbid");
                String pre = rs.getString("pre");
                sql.delete(0, sql.length());
                sql.append("update q05 set dbid='" + dbid + "',");
                sql.append("a0000=(select a0000 from " + pre + "a01 a where a.a0100=q05.a0100 )");
                sql.append(" where nbase='" + pre + "' and q03z0 = '" + kq_duration + "'");
                dao.update(sql.toString());
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return flag;
    }

    /**
     * 为表添加dbid、a0000两个字段
     * 
     * @param tableName
     * @return
     */
    public boolean updateQ03(String nbase, String where) {
        boolean flag = false;

        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            sql.append("update q03 set dbid=(select dbid from dbname where pre='");
            sql.append(nbase);
            sql.append("'),a0000=(select a0000 from " + nbase + "a01 a where a.a0100=q03.a0100 )");
            sql.append(where);

            dao.update(sql.toString());
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    public boolean updateQ03all(String start, String end) {
        boolean flag = false;
        start = start.replaceAll("-", ".");
        end = end.replaceAll("-", ".");
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            sql.append("select dbid,pre from dbname");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String pre = rs.getString("pre");
                sql.delete(0, sql.length());
                sql.append("update q03 set dbid=(select dbid from dbname where pre=q03.nbase");
                sql.append("),a0000=(select a0000 from " + pre + "a01 a where a.a0100=q03.a0100 )");
                sql.append(" where nbase='" + pre + "' and q03z0 between '" + start + "' and '" + end + "'");

                dao.update(sql.toString());
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return flag;
    }

    /**
     * 将源表的数据按照条件拷贝到目标表中
     * 
     * @param srcTab
     *            Sting 源表
     * @param destTab
     *            String 目标表
     * @param strFldlst
     *            String 列名，多列以逗号分隔，全部列可用*
     * @param strWhere
     *            String 条件，多个条件用`分开，没有条件为null或空字符窜
     * @param strWhere
     *            String 其他条件，如排序、分组条件，没有条件为null或空字符窜
     * @return boolean 是否成功
     */
    public String copyContent(String srcTab, String destTab, String strFldlst, String strWhere, String oth) {

        StringBuffer erro = new StringBuffer();
        StringBuffer strSql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);

        // 判断目标表是否存在
        boolean flag = false;
        DbWizard db = new DbWizard(this.conn);
        if (db.isExistTable(destTab, false)) {
            // 同步表结构
            syncTableStuts(srcTab, destTab);
            createPrimaryKey(srcTab, destTab);
            flag = true;
        }
        try {
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                // 创建目标表
                if (!flag) {
                    strSql.append("select * Into ");
                    strSql.append(destTab);
                    strSql.append(" from ");
                    strSql.append(srcTab);
                    strSql.append(" where 1=2");
                }
                break;
            }

            case Constant.ORACEL: {
                // 创建目标表
                if (!flag) {
                    strSql.append("Create Table ");
                    strSql.append(destTab);
                    strSql.append(" as select ");
                    strSql.append(strFldlst);
                    strSql.append(" from ");
                    strSql.append(srcTab);
                    strSql.append(" where 1=2");
                }

                break;
            }

            case Constant.DB2: {
                // 创建目标表
                if (!flag) {
                    strSql.append("Create Table ");
                    strSql.append(destTab);
                    strSql.append(" as ( SELECT " + strFldlst + " from " + srcTab);
                    strSql.append(" where 1=2 ) DEFINITION ONLY");
                }
                break;
            }
            }

            // 创建表
            try {
                if (!flag) {// 不存在则创建表
                    dao.update(strSql.toString());
                    // 创建主键
                    createPrimaryKey(srcTab, destTab);
                } else {// 存在则同步表结构

                }
            } catch (Exception e) {
                e.printStackTrace();
                erro.append("源表不存在或数据库错误！\r\n");
            }
            // 将内容复制到表中
            strSql.delete(0, strSql.length());
            strSql.append("insert into ");
            strSql.append(destTab);
            strSql.append("(");
            if ("*".equalsIgnoreCase(strFldlst)) {
                strSql.append(getAllColums(destTab));
            } else {
                strSql.append(strFldlst);
            }
            strSql.append(") select ");
            if ("*".equalsIgnoreCase(strFldlst)) {
                strSql.append(getAllColums(destTab));
            } else {
                strSql.append(strFldlst);
            }
            strSql.append(" from ");
            strSql.append(srcTab);
            if (strWhere != null && strWhere.length() > 0) {
                String[] str = strWhere.split("`");
                for (int i = 0; i < str.length; i++) {
                    if (i == 0) {
                        strSql.append(" where 1=1 ");
                        if (str[i].trim().length() > 0) {
                            strSql.append(" and ");
                            strSql.append(str[i]);
                        }
                    } else {
                        if (str[i].trim().length() > 0) {
                            strSql.append(" and ");
                            strSql.append(str[i]);
                        }
                    }
                }
            }

            if (oth != null && oth.trim().length() > 0) {
                strSql.append(" ");
                strSql.append(oth);
            }

            // 如果存在，则不添加（可能由于上次错误造成的）
            ArrayList list = getPrimaryKey(srcTab);
            strSql.append(" and not exists(select 1 from ");
            strSql.append(destTab);
            strSql.append(" where 1=1 ");
            for (int i = 0; i < list.size(); i++) {
                strSql.append(" and ");
                strSql.append(destTab);
                strSql.append(".");
                strSql.append(list.get(i).toString());
                strSql.append("=");
                strSql.append(srcTab);
                strSql.append(".");
                strSql.append(list.get(i).toString());
            }
            strSql.append(")");

            try {
                dao.update(strSql.toString());
            } catch (Exception e) {
                e.printStackTrace();
                erro.append("源表不存在或sql条件错误！\r\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return erro.toString();
    }

    /**
     * 获得表的主键
     * 
     * @param table
     * @return
     */
    private ArrayList getPrimaryKey(String table) {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            rs = dao.search(getSql(table));
            while (rs.next()) {
                list.add(rs.getString("column_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return list;
    }

    private void createPrimaryKey(String srcTable, String destTab) {
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer keyCol = new StringBuffer();
        String srcKeyName = "";
        String destKeyName = "";
        try {
            String srcSql = getSql(srcTable);
            rs = dao.search(srcSql);

            while (rs.next()) {
                srcKeyName = rs.getString("constraint_name");
                keyCol.append(",");
                keyCol.append(rs.getString("column_name"));
            }

            // 源表没有主键
            if ("".equals(srcKeyName)) {
                return;
            }

            // 目标表
            String destSql = getSql(destTab);
            rs = dao.search(destSql);
            if (rs.next()) {
                destKeyName = rs.getString("constraint_name");

                // 目标表主键名与要建的名称不同，将主键去除
                if (!destKeyName.equalsIgnoreCase(srcKeyName + "_ARC")) {
                    dao.update("alter table " + destTab + " drop constraint " + destKeyName);
                    destKeyName = "";
                }
            }

            // 目标表无主键（或已被清除），建立新主键
            if ("".equals(destKeyName)) {
                destKeyName = srcKeyName + "_ARC";
                if (keyCol.length() > 0) {
                    dao.update("alter table " + destTab + " add constraint " + destKeyName + " primary key("
                            + keyCol.substring(1) + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
    }

    private String getSql(String table) {
        StringBuffer sql = new StringBuffer();
        try {
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                sql.append("SELECT A.column_name,A.constraint_name");
                sql.append(" FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE A LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B");
                sql.append(" ON A.TABLE_CATALOG=B.TABLE_CATALOG");
                sql.append(" AND A.CONSTRAINT_NAME=B.CONSTRAINT_NAME");
                sql.append(" WHERE UPPER(A.TABLE_NAME)=UPPER('" + table + "')");
                sql.append(" AND B.CONSTRAINT_TYPE='PRIMARY KEY'");
                sql.append(" AND A.TABLE_CATALOG='" + this.conn.getCatalog() + "'");
                sql.append(" order by ordinal_position");
            } else {
                sql.append("select A.constraint_name,A.column_name");
                sql.append(" from dba_cons_columns A LEFT JOIN dba_constraints B");
                sql.append(" ON A.table_name=B.table_name");
                sql.append(" and A.owner=b.owner");
                sql.append(" and A.constraint_name=B.constraint_name");
                sql.append(" where A.table_name=upper('" + table + "')");
                sql.append(" and A.owner=upper('" + this.conn.getMetaData().getUserName() + "')");
                sql.append(" AND B.constraint_type='P'");
                sql.append(" order by position");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql.toString();
    }

    /**
     * 根据源表的机构同步目标表的表结构
     * 
     * @param srcTableName
     *            String 源表
     * @param targetTableName
     *            String 目标表
     */
    private void syncTableStuts(String srcTableName, String targetTableName) {
        // 获得源表的表结构信息
        Map srcMap = getColumnInfo(srcTableName);
        // 获得目标表的彼岸结构信息
        Map tarMap = getColumnInfo(targetTableName);
        Iterator it = tarMap.entrySet().iterator();
        Iterator srcIt = (Iterator) srcMap.entrySet().iterator();
        // 需要删除的列
        ArrayList delList = new ArrayList();
        // 需要更新的列
        ArrayList updList = new ArrayList();
        // 需要添加的列
        ArrayList addList = new ArrayList();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            // 目标表字段信息
            LazyDynaBean tarBean = (LazyDynaBean) entry.getValue();
            int type = Integer.parseInt((String) tarBean.get("type"));
            int scale = Integer.parseInt((String) tarBean.get("scale"));
            int size = Integer.parseInt((String) tarBean.get("size"));

            // 源表字段信息
            LazyDynaBean srcBean = (LazyDynaBean) srcMap.get(name.toLowerCase());

            if (srcMap.containsKey(name.toLowerCase())) {
                int srcType = Integer.parseInt((String) srcBean.get("type"));
                int srcScale = Integer.parseInt((String) srcBean.get("scale"));
                int srcSize = Integer.parseInt((String) srcBean.get("size"));
                if (DataType.sqlTypeToType(type) != DataType.sqlTypeToType(srcType) || scale != srcScale
                        || size != srcSize) {
                    Field field = new Field(name, name);
                    field.setDatatype(DataType.sqlTypeToType(srcType));
                    field.setLength(srcSize);
                    field.setDecimalDigits(srcScale);
                    if ("A0100".equalsIgnoreCase(name) || "nbase".equalsIgnoreCase(name)
                            || " Q03Z0".equalsIgnoreCase(name)) {
                        continue;
                    }
                    updList.add(field);
                }

            } else {// 应该删除的字段
                Field field = new Field(name, name);

                field.setDatatype(DataType.sqlTypeToType(type));
                field.setLength(size);
                field.setDecimalDigits(scale);
                if ("A0100".equalsIgnoreCase(name) || "nbase".equalsIgnoreCase(name) || " Q03Z0".equalsIgnoreCase(name)) {
                    continue;
                }
                delList.add(field);
            }
        }

        while (srcIt.hasNext()) {
            Map.Entry entry = (Map.Entry) srcIt.next();
            String name = (String) entry.getKey();
            LazyDynaBean bean = (LazyDynaBean) entry.getValue();
            if (!tarMap.containsKey(name)) {
                Field field = new Field(name, name);
                int srcType = Integer.parseInt((String) bean.get("type"));
                int srcScale = Integer.parseInt((String) bean.get("scale"));
                int srcSize = Integer.parseInt((String) bean.get("size"));
                field.setLength(srcSize);
                field.setDecimalDigits(srcScale);
                field.setDatatype(DataType.sqlTypeToType(srcType));
                addList.add(field);
            }
        }

        // 更新表
        DbWizard dbw = new DbWizard(this.conn);
        Table table = new Table(targetTableName.toLowerCase());
        try {
            // 添加列
            if (addList.size() > 0) {
                for (int i = 0; i < addList.size(); i++) {
                    table.addField((Field) addList.get(i));
                }
                dbw.addColumns(table);
                table.clear();
            }

            // 更新列
            if (updList.size() > 0) {
                for (int i = 0; i < updList.size(); i++) {
                    table.addField((Field) updList.get(i));
                }
                dbw.alterColumns(table);
                table.clear();
            }

            // 删除列
            if (delList.size() > 0) {
                for (int i = 0; i < delList.size(); i++) {
                    table.addField((Field) delList.get(i));
                }
                dbw.dropColumns(table);
                table.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得某表的字段信息
     * 
     * @param tableName
     *            表名称
     * @return HashMap<String,LazyDynaBean>
     */
    private HashMap getColumnInfo(String tableName) {
        // 查询源表的表结构
        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("select * from ");
        sqlBuff.append(tableName);
        sqlBuff.append(" where 1=2");

        // 保存源表的结构信息
        HashMap srcMap = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sqlBuff.toString());
            ResultSetMetaData data = rs.getMetaData();
            for (int i = 0; i < data.getColumnCount(); i++) {
                LazyDynaBean bean = new LazyDynaBean();
                // 字段名称
                String name = data.getColumnName(i + 1);
                // 字段的最大长度
                int size = data.getColumnDisplaySize(i + 1);
                // 列类型
                int type = data.getColumnType(i + 1);
                // 小数点右边的位数
                int scale = data.getScale(i + 1);

                // 将字段信息保存到map中
                bean.set("name", name);
                bean.set("type", type + "");
                bean.set("scale", scale + "");
                bean.set("size", size + "");

                srcMap.put(name.toLowerCase(), bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return srcMap;
    }

    /**
     * 
     * @param start
     * @param startField
     * @param end
     * @param endField
     * @param scope
     * @param type
     *            字段的类型，1为时间类型,2为字符类型，3为考勤期间判断
     * @return
     */
    public String dealTime(String start, String startField, String end, String endField, String scope, String type) {
        StringBuffer where = new StringBuffer();
        // 44959 20190226 linbz 特殊处理 请假公出加班 的时间范围条件
        if(!startField.equalsIgnoreCase(endField)) {
        	where.append(dealAppTime(start, startField, end, endField, scope, type));
        	return where.toString();
        }
        LazyDynaBean bean = this.getMaxDuration();
        String timeEnd = (String) bean.get("kq_start");
        timeEnd = timeEnd.replaceAll("\\.", "-");
        if (!"1".equalsIgnoreCase(scope)) {
            if (start != null && start.trim().length() > 0) {
                start = start.replaceAll("\\.", "-");
                if ("1".equalsIgnoreCase(type)) {
                    where.append("`replace(");
                    where.append(Sql_switcher.dateToChar(startField, "yyyy-mm-dd"));
                    where.append(",'.','-')>=");
                    where.append("'");
                    where.append(start);
                    where.append("'");
                }

                if ("2".equalsIgnoreCase(type)) {
                    where.append("`replace(");
                    where.append(startField);
                    where.append(",'.','-')>=");
                    where.append("'");
                    where.append(start);
                    where.append("'");
                }

                if ("3".equalsIgnoreCase(type)) {// 只有年月
                    List list = getDuration(start, end);
                    if (list.size() == 0) {
                        where.append("`1=2");
                    } else {
                        where.append("`");
                        where.append(startField);
                        where.append(" in (");
                        for (int i = 0; i < list.size(); i++) {
                            if (i != 0) {
                                where.append(",");
                            }
                            where.append("'");
                            where.append(list.get(i));
                            where.append("'");
                        }
                        where.append(")");
                    }
                }
            }

            if (end != null && end.trim().length() > 0) {
                if ("1".equalsIgnoreCase(type)) {
                    start = start.replaceAll("\\.", "-");
                    where.append("`replace(");
                    where.append(Sql_switcher.dateToChar(endField, "yyyy-mm-dd"));
                    where.append(",'.','-')<=");
                    where.append("'");
                    where.append(end);
                    where.append("'");
                }

                if ("2".equalsIgnoreCase(type)) {
                    where.append("`replace(");
                    where.append(endField);
                    where.append(",'.','-')<=");
                    where.append("'");
                    where.append(end);
                    where.append("'");
                }

                if ("3".equalsIgnoreCase(type)) {// 只有年月
                    List list = getDuration(start, end);
                    if (list.size() == 0) {
                        where.append("`1=2");
                    } else {
                        where.append("`");
                        where.append(endField);
                        where.append(" in (");
                        for (int i = 0; i < list.size(); i++) {
                            if (i != 0) {
                                where.append(",");
                            }
                            where.append("'");
                            where.append(list.get(i));
                            where.append("'");
                        }
                        where.append(")");
                    }
                }
            }
        }

        if ("1".equalsIgnoreCase(type)) {
            timeEnd = timeEnd.replaceAll("\\.", "-");
            where.append("`replace(");
            where.append(Sql_switcher.dateToChar(endField, "yyyy-mm-dd"));
            where.append(",'.','-')<");
            where.append("'");
            where.append(timeEnd);
            where.append("'");
        }

        if ("2".equalsIgnoreCase(type)) {
            where.append("`replace(");
            where.append(endField);
            where.append(",'.','-')<");
            where.append("'");
            where.append(timeEnd);
            where.append("'");
        }

        if ("3".equalsIgnoreCase(type)) {// 只有年月
            List list = getDuration(start, timeEnd);
            if (list.size() == 0) {
                where.append("`1=2");
            } else {
                where.append("`");
                where.append(endField);
                where.append(" in (");
                for (int i = 0; i < list.size(); i++) {
                    if (i != 0) {
                        where.append(",");
                    }
                    where.append("'");
                    where.append(list.get(i));
                    where.append("'");
                }
                where.append(")");
            }

        }
        if (where.length() > 0) {
            return where.substring(1);
        }
        return "";
    }
    /**
     * 特殊处理 请假公出加班 的时间范围条件
     * @param start
     * @param startField
     * @param end
     * @param endField
     * @param scope
     * @param type
     * @return
     */
    public String dealAppTime(String start, String startField, String end, String endField, String scope, String type) {
        StringBuffer where = new StringBuffer();
        LazyDynaBean bean = this.getMaxDuration();
        String timeEnd = (String) bean.get("kq_start");
        timeEnd = timeEnd.replaceAll("\\.", "-");
        if (!"1".equalsIgnoreCase(scope)) {
            if (start != null && start.trim().length() > 0 && end != null && end.trim().length() > 0) {
                start = start.replaceAll("\\.", "-");
                end = end.replaceAll("\\.", "-");
                if ("1".equalsIgnoreCase(type)) {
                	where.append("`(");
                    where.append("(replace(");
                    where.append(Sql_switcher.dateToChar(startField, "yyyy-mm-dd")).append(",'.','-')>=").append("'").append(start).append("'");
                    where.append(" and ");
                    where.append("replace(");
                    where.append(Sql_switcher.dateToChar(startField, "yyyy-mm-dd")).append(",'.','-')<=").append("'").append(end).append("')");
                    where.append(" or (replace(");
                    where.append(Sql_switcher.dateToChar(endField, "yyyy-mm-dd")).append(",'.','-')>=").append("'").append(start).append("'");
                    where.append(" and ");
                    where.append("replace(");
                    where.append(Sql_switcher.dateToChar(endField, "yyyy-mm-dd")).append(",'.','-')<=").append("'").append(end).append("')");
                    where.append(" or (replace(");
                    where.append(Sql_switcher.dateToChar(startField, "yyyy-mm-dd")).append(",'.','-')>").append("'").append(start).append("'");
                    where.append(" and ");
                    where.append("replace(");
                    where.append(Sql_switcher.dateToChar(endField, "yyyy-mm-dd")).append(",'.','-')<").append("'").append(end).append("')");
                    where.append(")");
                }

                if ("2".equalsIgnoreCase(type)) {
                    
                    where.append("`(");
                    where.append("(replace(").append(startField).append(",'.','-')>=").append("'").append(start).append("'");
                    where.append(" and ");
                    where.append("replace(").append(startField).append(",'.','-')<=").append("'").append(end).append("')");
                    where.append(" or ");
                    where.append("(replace(").append(endField).append(",'.','-')>=").append("'").append(start).append("'");
                    where.append(" and ");
                    where.append("replace(").append(endField).append(",'.','-')<=").append("'").append(end).append("')");
                    where.append(" or ");
                    where.append("(replace(").append(startField).append(",'.','-')>").append("'").append(start).append("'");
                    where.append(" and ");
                    where.append("replace(").append(endField).append(",'.','-')<").append("'").append(end).append("')");
                    where.append(")");
                }
                // 只有年月 暂时不考虑两个日期指标
//                if ("3".equalsIgnoreCase(type)) {
//                }
            }
        }

        if ("1".equalsIgnoreCase(type)) {
            timeEnd = timeEnd.replaceAll("\\.", "-");
            where.append("`replace(");
            where.append(Sql_switcher.dateToChar(endField, "yyyy-mm-dd"));
            where.append(",'.','-')<");
            where.append("'");
            where.append(timeEnd);
            where.append("'");
        }

        if ("2".equalsIgnoreCase(type)) {
            where.append("`replace(");
            where.append(endField);
            where.append(",'.','-')<");
            where.append("'");
            where.append(timeEnd);
            where.append("'");
        }
        
        if (where.length() > 0) {
            return where.substring(1);
        }
        return "";
    }

    /**
     * 根据开始时间和结束时间获得考勤期间信息
     * 
     * @param start
     * @param end
     * @return ArryList<String>
     */
    private ArrayList getDuration(String start, String end) {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select kq_year,kq_duration from kq_duration where 1=1 ");
        if (start != null && start.length() > 0) {
            sql.append(" and replace(");
            sql.append(Sql_switcher.dateToChar("kq_start", "yyyy-mm-dd"));
            sql.append(",'.','-')>='");
            sql.append(start.replaceAll("\\.", "-"));
            sql.append("'");
        }

        if (end != null && end.length() > 0) {
            sql.append(" and replace(");
            sql.append(Sql_switcher.dateToChar("kq_end", "yyyy-mm-dd"));
            sql.append(",'.','-')<='");
            sql.append(end.replaceAll("\\.", "-"));
            sql.append("'");
        }

        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String year = rs.getString("kq_year");
                String month = rs.getString("kq_duration");
                list.add(year + "-" + month);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return list;
    }

    /**
     * 获取已封存的最后一个考勤期间信息
     * @return
     */
    public LazyDynaBean getMaxDuration() {
        LazyDynaBean bean = new LazyDynaBean();
        StringBuffer sql = new StringBuffer();
        sql.append("select * from kq_duration where");
        sql.append(" kq_start = (select max(kq_start)");
        sql.append(" kq_start from kq_duration where finished=1 )");

        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                bean.set("kq_year", rs.getString("kq_year"));
                bean.set("kq_duration", rs.getString("kq_duration"));
                bean.set("kq_start", DateUtils.format(rs.getDate("kq_start"), "yyyy-MM-dd"));
                bean.set("kq_end", DateUtils.format(rs.getDate("kq_end"), "yyyy-MM-dd"));
                bean.set("finished", rs.getString("finished"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return bean;
    }

    /**
     * 获得表中的所有列
     * 
     * @param tableName
     *            String 表名
     * @return String 列名，多列逗号分隔
     */
    public String getAllColums(String tableName) {
        // 列名
        StringBuffer col = new StringBuffer();
        // 查询列名的sql语句
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ");
        sql.append(tableName);
        sql.append(" where 1=2");
        ResultSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (int i = 0; i < count; i++) {
                String column = meta.getColumnName(i + 1);
                col.append(",");
                col.append(column);
            }

            if (col.length() > 0) {
                return col.substring(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return "";
    }

    /**
     * 删除临时表
     * 
     * @param tablename
     */
    public static void dropTable(Connection conn, String tablename) {
        DbWizard dbWizard = new DbWizard(conn);
        dbWizard.dropTable(tablename);
    }

    public void dropTable(String tablename) {
        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(tablename);
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * 
     * @param v
     *            需要四舍五入的数字
     * @param scale
     *            小数点后保留几位
     * @return 四舍五入后的结果
     */
    public float round(String v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 修改Sql_switcher.getUpdateSqlTwoTable后的不足
     * 
     * @param srcTab
     *            //原表
     * @param strJoin
     *            //关联条件
     * @param update
     *            //Sql_switcher.getUpdateSqlTwoTable得到的语句
     * @return
     */
    public static String repairSqlTwoTable(String srcTab, String strJoin, String update, String strDWhere,
            String whereIN) {
        String falgS = "";
        if (strDWhere == null || strDWhere.length() <= 0) {
            falgS = "where";
        } else {
            falgS = "and";
        }
        String strSWhere = "";
        if (whereIN == null || whereIN.length() <= 0) {
            strSWhere = "";
        } else {
            strSWhere = " and " + whereIN;
        }
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL: {

            break;
        }
        case Constant.ORACEL: {
            String where2 = " " + falgS + "  EXISTS(SELECT 1 FROM " + srcTab + "  WHERE " + strJoin + ")";
            where2 = where2 + strSWhere;
            update = update + where2;
            break;
        }
        case Constant.DB2: {
            String where2 = " " + falgS + " EXISTS(SELECT 1 FROM " + srcTab + "  WHERE " + strJoin + ")";
            where2 = where2 + strSWhere;
            update = update + where2;
            break;
        }
        }
        return update;
    }

    /**
     * 修改Sql_switcher.getUpdateSqlTwoTable后的不足
     * 
     * @param srcTab
     *            //原表
     * @param strJoin
     *            //关联条件
     * @param update
     *            //Sql_switcher.getUpdateSqlTwoTable得到的语句
     * @return wangyao A0100有一样的，这就要区分人员库
     */
    public static String repairSqlTwoTable2(String srcTab, String strJoin, String update, String strDWhere,
            String whereIN, String nbase) {
        String falgS = "";
        if (strDWhere == null || strDWhere.length() <= 0) {
            falgS = "where";
        } else {
            falgS = "and";
        }
        String strSWhere = "";
        if (whereIN == null || whereIN.length() <= 0) {
            strSWhere = "";
        } else {
            strSWhere = " and " + whereIN;
        }
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL: {

            break;
        }
        case Constant.ORACEL: {
            String where2 = " " + falgS + "  EXISTS(SELECT 1 FROM " + srcTab + "  WHERE " + strJoin + " and " + srcTab
                    + ".nbase='" + nbase + "')";
            where2 = where2 + strSWhere;
            update = update + where2;
            break;
        }
        case Constant.DB2: {
            String where2 = " " + falgS + " EXISTS(SELECT 1 FROM " + srcTab + "  WHERE " + strJoin + " and " + srcTab
                    + ".nbase='" + nbase + "')";
            where2 = where2 + strSWhere;
            update = update + where2;
            break;
        }
        }
        return update;
    }

    public long getPartMinute(Date start_date, Date end_date) {
        int sY = DateUtils.getYear(start_date);
        int sM = DateUtils.getMonth(start_date);
        int sD = DateUtils.getDay(start_date);
        int sH = DateUtils.getHour(start_date);
        int smm = DateUtils.getMinute(start_date);

        int eY = DateUtils.getYear(end_date);
        int eM = DateUtils.getMonth(end_date);
        int eD = DateUtils.getDay(end_date);
        int eH = DateUtils.getHour(end_date);
        int emm = DateUtils.getMinute(end_date);
        GregorianCalendar d1 = new GregorianCalendar(sY, sM, sD, sH, smm, 00);
        GregorianCalendar d2 = new GregorianCalendar(eY, eM, eD, eH, emm, 00);
        Date date1 = d1.getTime();
        Date date2 = d2.getTime();
        long l1 = date1.getTime();
        long l2 = date2.getTime();
        long part = (l2 - l1) / (60 * 1000L);
        return part;
    }

    /**
     * 根据人员库前缀列表取得人员库CommonData列表
     * 
     * @Title: getKqNbaseList
     * @Description: 多个人员库时，增加个“全部人员库”
     * @param list
     *            人员库前缀列表
     * @return ArrayList 人员库CommonData列表
     */
    public ArrayList getKqNbaseList(ArrayList list) {
        ArrayList kq_list = new ArrayList();
        if (list == null || list.size() <= 0) {
            return kq_list;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < list.size(); i++) {
            buf.append(" Upper(pre)='" + list.get(i).toString().toUpperCase() + "'");
            if (i != list.size() - 1) {
                buf.append(" or ");
            }
        }
        buf.append(")");
        StringBuffer sql = new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1 and ");
        if (buf != null && buf.toString().length() > 0) {
            sql.append(buf.toString());
        }
        sql.append(" ORDER BY dbid");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            CommonData da = new CommonData();
            while (rs.next()) {
                da = new CommonData();
                da.setDataName(rs.getString("dbname"));
                da.setDataValue(rs.getString("pre"));
                kq_list.add(da);
            }
            if (kq_list.size() != 1) {
                da = new CommonData();
                da.setDataName("全部人员库");
                da.setDataValue("all");
                kq_list.add(0, da);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return kq_list;
    }

    public ArrayList getKqNbaseListNullAll(ArrayList list) {
        ArrayList kq_list = new ArrayList();
        if (list == null || list.size() <= 0) {
            return kq_list;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < list.size(); i++) {
            buf.append(" Upper(pre)='" + list.get(i).toString().toUpperCase() + "'");
            if (i != list.size() - 1) {
                buf.append(" or ");
            }
        }
        buf.append(")");
        StringBuffer sql = new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1 and ");
        if (buf != null && buf.toString().length() > 0) {
            sql.append(buf.toString());
        }
        sql.append("ORDER BY dbid");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            CommonData da = new CommonData();
            while (rs.next()) {
                da = new CommonData();
                da.setDataName(rs.getString("dbname"));
                da.setDataValue(rs.getString("pre"));
                kq_list.add(da);
            }
            if (kq_list.size() != 1) {
                da = new CommonData();
                da.setDataName("全部人员库");
                da.setDataValue("all");
                kq_list.add(0, da);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return kq_list;
    }

    public String getWhere_C(String select_flag, String field, String name) {
        StringBuffer where_c = new StringBuffer();
        if (name == null || name.length() <= 0 || "null".equals(name)) {
            return "";
        }

        if (select_flag != null && "1".equals(select_flag)) {
            name = PubFunc.getStr(name);
            where_c.append(" and " + field + " like '%" + name + "%'");
        }

        return where_c.toString();
    }

    public String getSelect_WhereResult(String select_flag, String selectResult) {
        String where_c = "";
        if (select_flag != null && "0".equals(select_flag)) {
            where_c = "";
        } else if (selectResult != null && selectResult.length() > 0) {
            where_c = "and " + selectResult;
        }
        return where_c;
    }

    /**
     * 排序—显示隐藏
     * 
     * @param sort
     * @return
     */
    public HashMap getSortMap(String sort) {
        HashMap map = new HashMap();
        if (sort == null || sort.length() <= 0) {
            return map;
        }

        String[] sorts = sort.split("`");
        for (int i = 0; i < sorts.length; i++) {
            String element = sorts[i];
            String[] contents = element.split(":");
            if (contents != null && contents.length == 3) {
                map.put(contents[0], contents);
            }
        }
        return map;
    }

    public String getSortOrderBY(String sort) {
        if (sort == null || sort.length() <= 0) {
            return "";
        }

        StringBuffer orderby = new StringBuffer();
        orderby.append("order by i,");
        String[] sorts = sort.split("`");
        for (int i = 0; i < sorts.length; i++) {
            String element = sorts[i];
            String[] contents = element.split(":");
            if (contents != null && contents.length == 3) {
                if ("1".equalsIgnoreCase(contents[2])) {
                    orderby.append(contents[0] + " asc,");
                } else {
                    orderby.append(contents[0] + " desc,");
                }
            }

        }
        orderby.setLength(orderby.length() - 1);
        return orderby.toString();
    }

    public ArrayList getKqClassListByRight(UserView userView) {

        ArrayList class_list = new ArrayList();
        RowSet rs = null;
        try {
            CommonData da = new CommonData();
            da.setDataName("<无>");
            da.setDataValue("#");
            class_list.add(da);

            String onduty = "";
            String offduty = "";

            String sql = "select * from kq_class where class_id<>'0'";
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            while (rs.next()) {
                // 班次权限检查
                if (null != userView
                        && !userView.isHaveResource(IResourceConstant.KQ_BASE_CLASS, rs.getString("class_id"))) {
                    continue;
                }

                da = new CommonData();
                onduty = rs.getString("onduty_1");
                for (int i = 3; i > 0; i--) {
                    offduty = rs.getString("offduty_" + i);
                    if (offduty != null && offduty.length() == 5) {
                        break;
                    }
                }
                if (onduty != null && onduty.length() > 0 && offduty != null && offduty.length() > 0) {
                    da.setDataName(rs.getString("name") + "(" + onduty + "~" + offduty + ")");
                    da.setDataValue(rs.getString("class_id"));
                    class_list.add(da);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return class_list;
    }

    public ArrayList getKqClassList() {
        return getKqClassListByRight(null);
    }

    /**
     * 
     * @param class_id
     *            班次表id 字段名class_id
     * @return Map key "startTime" 上班时间 key "endTime" 下班时间 "onduty_2"下午上班时间
     *         offduty_1上午下班时间 conday是否跨天
     * @throws SQLException
     */
    public Map getTimeAreaInclassById(String class_id) {
        String sql = "select * from kq_class where class_id=" + class_id;

        ContentDAO dao = new ContentDAO(this.conn);
        Map data = new HashMap();
        boolean conday = false;
        Integer crossZeroIndex = Integer.valueOf(0);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                String onduty = "";
                String onduty_2 = "";
                String offduty = "";
                String offduty_1 = "";
                onduty = rs.getString("onduty_1");
                offduty_1 = rs.getString("offduty_1");
                onduty_2 = rs.getString("onduty_2");
                int endtime = 1;
                for (int i = 3; i > 0; i--) {
                    offduty = rs.getString("offduty_" + i);
                    if (offduty != null && offduty.length() == 5) {
                        endtime = i;
                        break;
                    }
                }
                String onDutyTime = "";
                String offDutyTime = "";
                String preTime = "";
                for (int i = 1; i <= endtime; i++) {
                    onDutyTime = rs.getString("onduty_" + i);
                    onDutyTime = PubFunc.nullToStr(onDutyTime).trim();

                    offDutyTime = rs.getString("offduty_" + i);
                    offDutyTime = PubFunc.nullToStr(offDutyTime).trim();

                    // 上班点或下班点未定义，直接退出
                    if ("".equals(onDutyTime) || "".equals(offDutyTime)) {
                        break;
                    }
                    // 本段上班点与上段下班点间是否跨零点
                    if (i > 1) {
                        conday = 0 <= preTime.compareTo(onDutyTime);
                        if (conday) {
                            crossZeroIndex = Integer.valueOf(i * 10 - 5);
                            break;
                        }
                    }

                    // 本段上班点和下班点间是否跨零点
                    conday = 0 <= onDutyTime.compareTo(offDutyTime);
                    if (conday) {
                        crossZeroIndex = Integer.valueOf(i * 10);
                        break;
                    }

                    preTime = offDutyTime;
                }

                String work_hours = rs.getString("work_hours");
                data.put("conday", new Boolean(conday));
                data.put("startTime", onduty);
                data.put("endTime", offduty);
                data.put("onduty_2", onduty_2);
                data.put("offduty_1", offduty_1);
                data.put("work_hours", work_hours);
                data.put("crossZeroIndex", crossZeroIndex);
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return null;
    }

    public String getStartTimeclassById(String classID) {
        String sql = "select onduty_1 from kq_class where class_id=" + classID;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                return rs.getString("onduty_1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return null;
    }

    /**
     * 得到Map; key 是class_id， value是startTime和endTime 的Map
     * 
     * @param startTime
     *            开始时间 Map.get("startTime")
     * @param endTime
     *            结束时间 Map.get("endTime")
     * @return
     */
    public Map getClassTimeMap() {
        String sql = "select * from kq_class where class_id <> '0'";
        ContentDAO dao = new ContentDAO(this.conn);
        Map data = new HashMap();
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String onduty = "";
                String offduty = "";
                onduty = rs.getString("onduty_1");
                for (int i = 3; i > 0; i--) {
                    offduty = rs.getString("offduty_" + i);
                    if (offduty != null && offduty.length() == 5) {
                        break;
                    }
                }
                boolean conday = false;
                if (GetValiateEndDate.isBigToTime(onduty, rs.getString("offduty_1"))) {
                    conday = true;
                } else if ((rs.getString("onduty_2") != null && rs.getString("onduty_2").length() > 1)
                        && (rs.getString("offduty_2") != null && rs.getString("offduty_2").length() > 1)) {
                    if (GetValiateEndDate.isBigToTime(onduty, rs.getString("onduty_2"))
                            || GetValiateEndDate.isBigToTime(onduty, rs.getString("offduty_2"))) {
                        conday = true;
                    } else if ((rs.getString("onduty_3") != null && rs.getString("onduty_3").length() > 1)
                            && (rs.getString("offduty_3") != null && rs.getString("offduty_3").length() > 1)) {
                        if (GetValiateEndDate.isBigToTime(onduty, rs.getString("onduty_3"))
                                || GetValiateEndDate.isBigToTime(onduty, rs.getString("offduty_3"))) {
                            conday = true;
                        }
                    }
                }
                if (conday) {
                    data.put(rs.getString("class_id"), onduty + "~" + "1" + offduty);
                } else {
                    data.put(rs.getString("class_id"), onduty + "~" + "0" + offduty);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return data;
    }

    public Map getClassDescMap() {
        String sql = "select class_id,name from kq_class";
        ContentDAO dao = new ContentDAO(this.conn);
        Map data = new HashMap();
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                data.put(rs.getString("class_id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return data;
    }

    /**
     * 
     * @param a_code
     * @param nbase
     * @return
     */
    public String getACodeDesc(String a_code, String nbase) {
        if (a_code == null || a_code.length() <= 2) {
            return "";
        }

        String codesetid = a_code.substring(0, 2);
        String codeitemid = a_code.substring(2);
        String codedesc = "";
        ContentDAO dao = new ContentDAO(this.conn);
        if ("UN".equalsIgnoreCase(codesetid)) {
            codedesc = AdminCode.getCodeName("UN", codeitemid);
            codedesc = codedesc + "(单位)";
        } else if ("UM".equalsIgnoreCase(codesetid)) {
            codedesc = AdminCode.getCodeName("UM", codeitemid);
            codedesc = codedesc + "(部门)";
        } else if ("@K".equalsIgnoreCase(codesetid)) {
            codedesc = AdminCode.getCodeName("@K", codeitemid);
            codedesc = codedesc + "(岗位)";
        } else if ("GP".equalsIgnoreCase(codesetid)) {
            codedesc = getGroupName(codeitemid, dao);
        } else if ("EP".equalsIgnoreCase(codesetid)) {
            codedesc = getUserName(codeitemid, nbase, dao);
        }
        return codedesc;
    }

    private String getGroupName(String group_id, ContentDAO dao) {
        StringBuffer sql = new StringBuffer();
        sql.append("select name from kq_shift_group");
        sql.append(" where group_id='" + group_id + "'");
        String name = "";
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return name;
    }

    private String getUserName(String a0100, String nbase, ContentDAO dao) {
        if (a0100 == null || a0100.length() <= 0 || nbase == null || nbase.length() <= 0) {
            return "";
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select b0110,e0122,a0101 from " + nbase + "A01");
        sql.append(" where a0100='" + a0100 + "'");
        StringBuffer name = new StringBuffer();
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                String b0100 = rs.getString("b0110") != null && rs.getString("b0110").length() > 0 ? rs
                        .getString("b0110") : "";
                String e0122 = rs.getString("e0122") != null && rs.getString("e0122").length() > 0 ? rs
                        .getString("e0122") : "";
                String a0101 = rs.getString("a0101");
                name.append(" " + a0101);
                name.append("(单位:" + AdminCode.getCodeName("UN", b0100));
                name.append(" 部门:" + AdminCode.getCodeName("UM", e0122) + "");
                /** 增加班组信息 wangy* */
                sql.setLength(0);
                sql.append("select group_id from kq_group_emp where ");
                sql.append(" a0100='" + a0100 + "' and nbase='" + nbase + "' and b0110='" + b0100 + "' and e0122='"
                        + e0122 + "'");
                rs = dao.search(sql.toString());
                String group_id = "";
                if (rs.next()) {
                    group_id = rs.getString("group_id");
                }
                sql.setLength(0);
                sql.append("select name from kq_shift_group where group_id='" + group_id + "'");
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    name.append(" 班组:" + rs.getString("name") + ")");
                } else {
                    name.append(")");
                }
                /** 结束* */
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return name.toString();
    }

    /**
     * 建立人员临时表
     * 
     * @return
     * @throws GeneralException
     */
    public String createContrastTemp() throws GeneralException {
        String table_name = "t#" + this.userview.getUserName() + "_kq_tp";
        table_name = table_name.toLowerCase();

        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(table_name);

        Table table = new Table(table_name);
        Field temp = new Field("nbase", "组织编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("a0100", "人员编号");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(50);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp2 = new Field("a0101", "人员姓名");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(50);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        temp2 = new Field("b0110", "单位");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(50);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        temp2 = new Field("e0122", "部门");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(50);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        temp2 = new Field("e01a1", "职位");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(50);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return table_name;
    }

    public String getCodeFormA_code(String a_code) {
        if (a_code == null || a_code.length() <= 0) {
            a_code = "UN";
        }

        String code = "";
        if (a_code.indexOf("UN") != -1 || a_code.indexOf("UM") != -1 || a_code.indexOf("@K") != -1) {
            if (a_code.length() > 2) {
                code = a_code.substring(2);
            }
        }
        return code;
    }

    public String getKindFormA_code(String a_code) {
        if (a_code == null || a_code.length() <= 0) {
            a_code = "UN";
        }
        String kind = "";
        if (a_code.indexOf("UN") != -1) {
            kind = "2";
        } else if (a_code.indexOf("UM") != -1) {
            kind = "1";
        } else if (a_code.indexOf("@K") != -1) {
            kind = "0";
        }
        return kind;
    }

    /**
     * 取
     * 
     * @param dateString
     *            ， 某年某月某天
     * @param afterNum
     *            天数
     * @return string 返回相加后得到新的某年某月某天
     */
    public String getDateByAfter(java.util.Date date, int afterNum) throws GeneralException {
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
     * 得到考勤人员库(方法名太违反人性，该方法已废除，请使用getKqPreList代替)
     * 
     * @deprecated
     * @param code
     * @param kind
     * @return
     * @throws GeneralException
     */
    public ArrayList setKqPerList(String code, String kind) throws GeneralException {
        return getKqPreList();
    }

    /**
     * 取得考勤人员库列表(当前用户人员库权限与考勤人员库交集)
     * 
     * @Title: setKqPerList
     * @Description: 取得考勤人员库列表
     * @return ArrayList
     * @throws GeneralException
     */
    public ArrayList getKqPreList() throws GeneralException {
        // zxj changed 20140214 人员库不再分单位设置
        HashMap map = new HashMap();
        return RegisterInitInfoData.getB0110Dase(map, this.userview, this.conn, "");
    }

    public String getSafeCode(String code) {
        if (code == null || code.length() <= 0 || "null".equals(code)) {
            return "";
        }

        code = SafeCode.decode(code);
        code = PubFunc.getStr(code);
        return code;
    }

    /**
     * 查询考勤申请的高级花名册的
     * 
     * @param sortid
     * @param dao
     * @param tableId
     * @return
     */
    public ArrayList selectMuster(String nModule, ContentDAO dao, String tableId) {
        String sql = "select tabid,cname from Muster_Name where nModule='" + nModule + "'";
        if (!"".equals(PubFunc.DotstrNull(tableId))) {
            sql = sql + " and nPrint = '" + tableId + "'";
        }

        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            CommonData da = null;
            rs = dao.search(sql);
            while (rs.next()) {
                da = new CommonData();
                da.setDataName(rs.getString("cname"));
                da.setDataValue(rs.getString("tabid"));
                list.add(da);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return list;
    }

    /**
     * 查询高级花名册的
     * 
     * @param sortid
     * @param dao
     * @return
     */
    public ArrayList selectMuster(String nModule, ContentDAO dao) {
        return selectMuster(nModule, dao, null);
    }

    public float getAppDateTimeLen(Connection conn, Date date0, Date date1) {
        String class_id = KqParam.getInstance().getDefault_rest_kqclass();

        float timeLen = 0;
        float time_sum = 0;
        float timeValue = 0;
        if (class_id != null && !"#".equals(class_id) && !"0".equals(class_id) && class_id.length() > 0) {
            KqClassArray kqClassArray = new KqClassArray(conn);
            RecordVo vo = kqClassArray.getClassMessage(class_id);
            if (vo == null) {
                return 0;
            }

            int dd = DateUtils.dayDiff(date0, date1);
            AnnualApply annualApply = new AnnualApply();
            String curD = DateUtils.format(date0, "yyyy.MM.dd");
            for (int i = 0; i <= dd; i++) {
                Date d_curDate = DateUtils.getDate(curD, "yyyy.MM.dd");
                HashMap hash = annualApply.getCurDateTime(vo, d_curDate, date0, date1);
                Float timeLenF = (Float) hash.get("timeLen");
                timeLen = timeLenF.floatValue();
                Float timeSumF = (Float) hash.get("time_sum");
                time_sum = timeSumF.floatValue();
                timeValue = timeValue + timeLen / time_sum;
                Date curdd = DateUtils.addDays(DateUtils.getDate(curD, "yyyy.MM.dd"), 1);
                curD = DateUtils.format(curdd, "yyyy.MM.dd");
            }
        }
        return timeValue;
    }

    public float calcTimSpan(Date FDT, Date TDT, Date s_app_date, Date e_app_date) {
        float timeLen = 0;
        float time_1 = getPartMinute(FDT, s_app_date);
        float time_2 = getPartMinute(TDT, e_app_date);

        // 完全包含在申请时间内
        if (time_1 <= 0 && time_2 >= 0) {
            timeLen = getPartMinute(FDT, TDT);
            return timeLen;
        }

        // 申请时间完全包含在工作时段内
        if (time_1 >= 0 && time_2 <= 0) {
            timeLen = getPartMinute(s_app_date, e_app_date);
            return timeLen;
        }

        // 只包含前一部分
        float time_3 = getPartMinute(FDT, e_app_date);
        if (time_1 <= 0 && time_3 > 0) {
            timeLen = getPartMinute(FDT, e_app_date);
            return timeLen;
        }

        // 只包含后一部分
        float time_4 = getPartMinute(TDT, s_app_date);
        if (time_4 < 0 && time_2 >= 0) {
            timeLen = getPartMinute(s_app_date, TDT);
            return timeLen;
        }

        return timeLen;
    }

    public boolean isSpanForKqClass(String class_id) {
        if (class_id == null || class_id.length() <= 0) {
            return false;
        }

        StringBuffer sql = new StringBuffer();
        sql.append("select * from kq_class where class_id=" + class_id + "");
        String onduty = "";
        String offduty = "";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                onduty = rs.getString("onduty_1");
                for (int i = 3; i > 0; i--) {
                    offduty = rs.getString("offduty_" + i);
                    if (offduty != null && offduty.length() == 5) {
                        break;
                    }
                }
            }

            if (onduty == null || onduty.length() != 5 || offduty == null || offduty.length() != 5) {
                return false;
            }

            return onduty.compareTo(offduty) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return false;
    }

    public boolean isSpanForTimeStr(String stard_T, String end_T) {
        if (stard_T == null || stard_T.length() <= 0 || end_T == null || end_T.length() <= 0) {
            return false;
        }

        return stard_T.compareTo(end_T) > 0;
    }

    /**
     * 考勤日明细导入项,如:岗位指标
     * 
     * @param dblist
     * @param start_date
     * @param end_date
     */
    public void leadingInItemToQ03(ArrayList dblist, String start_date, String end_date, String dest_table,
            String codeWhere) throws GeneralException {
        leadingInItemToQ03(dblist, start_date, end_date, this.conn, this.userview, dest_table, codeWhere);
    }

    /**
     * 导入业务字典Q03中从主集子集引入的指标值到考勤日明细或月汇总中
     * 
     * @param dblist
     *            人员库
     * @param start_date
     *            开始日期
     * @param end_date
     *            结束日期
     * @param conn
     *            数据库链接
     * @param userview
     *            用户
     * @param kq_month
     *            考勤月度（月汇总时用，日明细不传值）
     * @throws GeneralException
     */
    public synchronized static void leadingInItemToKqTab(ArrayList dblist, String start_date, String end_date,
            Connection conn, UserView userview, String dest_table, String codeWhere, String kq_month)
            throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("select A.ItemId as fielditemid,B.other_param");
        sql.append(" from t_hr_busifield A left join kq_item B");
        sql.append(" on A.ItemId=UPPER(B.fielditemid)");
        sql.append(" where A.FieldSetId='Q03'");
    	sql.append(" and A.useflag='1'");
    	// 从申请单统计的数据不需要导入
    	sql.append(" and (sdata_src is null or sdata_src='')");
    	// 一些基本指标不需要导入
        sql.append(" and UPPER(A.itemId) not in ('A0100','A0101','B0110','E0122','E01A1')");
        // 人员子集中已构库指标导入
        sql.append(" and (UPPER(A.ItemId) in (SELECT ItemId FROM fielditem WHERE FieldSetId LIKE 'A%' and useflag='1')");
        // 明确定义了导入规则的需要导入
        sql.append(" or B.other_param is not null)");          
                
        ContentDAO dao = new ContentDAO(conn);
        KqUtilsClass kqUtilsClass = new KqUtilsClass(conn, userview);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String other_param = "";
                String fielditemid = "";
                String subset = "";
                String setfielditemid = "";
                String begindate = "";
                String enddate = "";

                fielditemid = rs.getString("fielditemid");
                if (fielditemid == null || fielditemid.length() <= 0) {
                    continue;
                }

                other_param = Sql_switcher.readMemo(rs, "other_param");

                // 定义了导入规则的指标
                if (other_param != null && other_param.length() > 0) {
                    SearchImportBo importBo = new SearchImportBo(other_param);
                    subset = importBo.getValue("subset");
                    setfielditemid = importBo.getValue("field");
                    begindate = importBo.getValue("begindate");
                    enddate = importBo.getValue("enddate");

                    // 没定义完整的，不处理
                    if (begindate == null || begindate.length() <= 0) {
                        continue;
                    }

                    if (enddate == null || enddate.length() <= 0) {
                        continue;
                    }
                } else {
                    // 主集或子集引入指标
                    FieldItem item = DataDictionary.getFieldItem(fielditemid, 0);
                    if (item == null) {
                        continue;
                    }
    				
    				if (!"1".equals(item.getUseflag())) {
                        continue;
                    }
    				
                    subset = item.getFieldsetid();
                    if (subset == null || !subset.startsWith("A")) {
                        continue;
                    }
    				
    		        if (!includeA01ForLeadingInItem && "A01".equalsIgnoreCase(subset)) {
                        continue;
                    }
    				
                    setfielditemid = fielditemid;
                }

                if (subset == null || subset.length() <= 0) {
                    continue;
                }

                if (setfielditemid == null || setfielditemid.length() <= 0) {
                    continue;
                }

                if (kq_month == null || "".equals(kq_month)) {
                    kqUtilsClass.upLeadingInItemToQ03(fielditemid, subset, setfielditemid, begindate, enddate, dblist,
                            start_date, end_date, dest_table, codeWhere);
                } else {
                    kqUtilsClass.upLeadingInItemToQ05(fielditemid, dblist, start_date, end_date, kq_month);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            closeDBResource(rs);
        }
    }

    public synchronized static void leadingInItemToQ03(ArrayList dblist, String start_date, String end_date,
            Connection conn, UserView userview, String dest_table, String codeWhere) throws GeneralException {
        leadingInItemToKqTab(dblist, start_date, end_date, conn, userview, dest_table, codeWhere, null);
    }

    private void upLeadingInItemToQ03(String q03fielditemid, String setId, String setfielditemid,
            String begindate_fielditemid, String enddate_fielditemid, ArrayList dblist, String start_date,
            String end_date, String dest_table, String codeWhere) throws GeneralException {
        //zxj 20161222 检查指标是否构库
        FieldItem q03Item = DataDictionary.getFieldItem(q03fielditemid, "q03");
        if (q03Item == null || !"1".equals(q03Item.getUseflag())) {
            return;
        }
        
        ContentDAO dao = new ContentDAO(this.conn);
        for (int i = 0; i < dblist.size(); i++) {
            String userbase = dblist.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(this.userview, userbase);
            String strSet = "";
            String strJoin = "";
            String destTab = dest_table;// 目标表
            String srcTab = userbase + setId;// 源表
            String factSrcTab = srcTab; // 最终使用的源表名，简单引入子集指标时会用到临时表名tmp
            if (!"".equals(begindate_fielditemid)) {
                strJoin = destTab + ".A0100=" + srcTab + ".A0100";// 关联串
                /*
                 * zxj 20140317 changed 不能简单的用日期型进行大小比较，因为日期后可能带有时间。
                 * 改为按格式化后的日期字符串进行比较
                 */
                strJoin = strJoin
                        + " and "
                        + destTab
                        + ".q03z0>="
                        + mssqlReplace(Sql_switcher.sqlNull(
                                Sql_switcher.dateToChar(srcTab + "." + begindate_fielditemid, "yyyy.mm.dd"),
                                "2010.01.01"), "-", ".")
                        + " and "
                        + destTab
                        + ".q03z0<="
                        + mssqlReplace(
                                Sql_switcher.sqlNull(
                                        Sql_switcher.dateToChar(srcTab + "." + enddate_fielditemid, "yyyy.mm.dd"),
                                        "9999.01.01"), "-", ".");

                strSet = destTab + "." + q03fielditemid + "=" + srcTab + "." + setfielditemid;

                // if ("kq_analyse_result".equals(destTab) && codeWhere != null
                // && codeWhere.length() > 0) {
                // strJoin = strJoin + " and " + codeWhere;
            } else {
                if (!"A01".equalsIgnoreCase(setId)) {
                    srcTab = "(select a0100,max(" + setfielditemid + ") as aValue" + " from " + srcTab
                            + " where I9999=(select max(I9999)" + " from " + srcTab + " aaa" + " where a0100=" + srcTab
                            + ".a0100" + " group by a0100" + ") group by a0100) tmp";
                    strJoin = "tmp.A0100=" + destTab + ".A0100";
                    strSet = destTab + "." + q03fielditemid + "=" + "tmp.aValue";
                    factSrcTab = "tmp";
                } else {
                    strJoin = srcTab + ".A0100=" + destTab + ".A0100";
                    strSet = destTab + "." + q03fielditemid + "=" + srcTab + "." + setfielditemid;
                }
            }

            String strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0>='" + start_date
                    + "' and " + destTab + ".q03z0<='" + end_date + "' ";// 更新目标的表过滤条件

            if ("Q03".equalsIgnoreCase(destTab) || "Q05".equalsIgnoreCase(destTab)) {
                strDWhere += " and " + Sql_switcher.isnull(destTab + ".q03z5", "'01'") + "='01'";
            }

            String strSWhere = "";
            if (!this.userview.isSuper_admin()) {
                strSWhere = "exists (select a0100 " + whereIN + " and " + factSrcTab + ".a0100=" + userbase
                        + "a01.a0100";// 源表的过滤条件
            } else {
                strSWhere = "exists (select a0100 " + whereIN + " where " + factSrcTab + ".a0100=" + userbase
                        + "a01.a0100";// 源表的过滤条件
            }

            if (codeWhere != null && codeWhere.length() > 0) {
                strSWhere = strSWhere + " and (" + codeWhere + ")";
                strDWhere = strDWhere + " and (" + codeWhere + ")";
            }

            strSWhere = strSWhere + ")";

            String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
            String othWhereSql = "";
            if (!this.userview.isSuper_admin()) {
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    othWhereSql = ("  EXISTS(select a0100 " + whereIN + " and " + userbase + "A01.a0100=" + destTab + ".a0100)");
                } else {
                    othWhereSql = ("  EXISTS(select a0100 " + whereIN + " where " + userbase + "A01.a0100=" + destTab + ".a0100)");
                }
            }
            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);

            try {
                dao.update(update);
            } catch (Exception e) {
                e.printStackTrace();
                String error = checkLeadingInItemToQ03ErrorEmp(dao, userbase, whereIN, setId, setfielditemid,
                        begindate_fielditemid, enddate_fielditemid, start_date, end_date);
                if (error != null && error.length() > 0) {
                    error = "以下是可能出现的人员<br><br>" + error;
                }

                throw GeneralExceptionHandler.Handle(new GeneralException("从子集导入指标失败！<br>" + e.getMessage() + "<br>"
                        + error));
            }
        }
    }

    private String mssqlReplace(String srcStr, String targetChar, String replaceChar) {
        if (Constant.MSSQL != Sql_switcher.searchDbServer()) {
            return srcStr;
        }

        return srcStr = "replace(" + srcStr + ",'" + targetChar + "','" + replaceChar + "')";
    }

    private String checkLeadingInItemToQ03ErrorEmp(ContentDAO dao, String nbase, String whereIN, String setId,
            String setfielditemid, String begindate_fielditemid, String enddate_fielditemid, String start_date,
            String end_date) {
        StringBuffer sql = new StringBuffer();
        String srcTab = nbase + setId;// 源表
        String destTab = "q03";
        sql.append("select a0101,b0110,e0122,e01a1,a0100 from (");
        sql.append("select max(q03.a0101) as a0101,max(q03.b0110) as b0110,max(q03.e0122) as e0122,max(q03.e01a1) as e01a1,q03.a0100");
        sql.append(" from " + srcTab + ",q03");
        sql.append(" where " + srcTab + ".A0100=q03.A0100");
        sql.append(" and " + Sql_switcher.charToDate(destTab + ".q03z0"));
        sql.append(" between ");
        sql.append(Sql_switcher.isnull(srcTab + "." + begindate_fielditemid, Sql_switcher.dateValue("2010-01-01")));
        sql.append(" and ");
        sql.append(Sql_switcher.isnull(srcTab + "." + enddate_fielditemid, Sql_switcher.dateValue("9999-12-31")));
        sql.append(" and " + destTab + ".nbase='" + nbase);
        sql.append("' and " + destTab + ".q03z0>='" + start_date);
        sql.append("' and " + destTab + ".q03z0<='" + end_date + "'");

        if (!this.userview.isSuper_admin()) {
            sql.append(" and " + destTab + ".a0100 in(select a0100 " + whereIN + ")");
            // 源表的过滤条件
            sql.append(" and exists (select a0100 " + whereIN);
            if (whereIN.toUpperCase().indexOf("WHERE") != -1) {
                sql.append(" and ");
                ;
            } else {
                sql.append(" where ");
            }
            sql.append(srcTab + ".a0100=" + nbase + "a01.a0100)");
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
                buff.append(per + " " + b0110 + "  " + e0122 + "  " + e01a1 + "  " + "  " + rs.getString("a0101")
                        + "<br>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return buff.toString();
    }

    /**
     * 考月汇总导入项,如:岗位指标
     * 
     * @param dblist
     * @param start_date
     * @param end_date
     */
    public void leadingInItemToQ05(ArrayList dblist, String start_date, String end_date, String dest_table,
            String codeWhere, String kq_month) throws GeneralException {
        leadingInItemToKqTab(dblist, start_date, end_date, conn, userview, dest_table, codeWhere, kq_month);
    }

    private void upLeadingInItemToQ05(String q03fielditemid, ArrayList dblist, String start_date, String end_date,
            String kq_month) throws GeneralException {
        //zxj 20161222 检查指标是否构库
        FieldItem q03Item = DataDictionary.getFieldItem(q03fielditemid, "q03");
        if (q03Item == null || !"1".equals(q03Item.getUseflag())) {
            return;
        }
        
        ContentDAO dao = new ContentDAO(this.conn);
        for (int i = 0; i < dblist.size(); i++) {
            String userbase = dblist.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(this.userview, userbase);
            String destTab = "q05";// 目标表
            String srcTab = "q03";// 源表
            String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase";// 关联串
            // xxx.field_name=yyyy.field_namex,....
            String strSet = destTab + "." + q03fielditemid + "=" + srcTab + "." + q03fielditemid;
            ;// 更新串 xxx.field_name=yyyy.field_namex,....
            String strDWhere = destTab + ".nbase='" + userbase + "' and " + destTab + ".q03z0='" + kq_month + "' and "
                    + destTab + ".q03z5='01'";// 更新目标的表过滤条件

            // String strSWhere=srcTab+".a0100 in(select a0100
            // "+whereIN+")";//源表的过滤条件
            // String strSWhere="exists (select a0100 "+whereIN+" and
            // q05.a0100="+nbase+"a01.a0100)";//源表的过滤条件 以前
            String strSWhere = srcTab + ".nbase='" + userbase + "' and " + srcTab + ".q03z0='" + end_date + "'";
            ;
            if (!this.userview.isSuper_admin()) {
                strSWhere = strSWhere + " and exists (select a0100 " + whereIN + " and " + srcTab + ".a0100="
                        + userbase + "a01.a0100)";// 源表的过滤条件
                strDWhere = strDWhere + " and exists (select a0100 " + whereIN + " and " + destTab + ".a0100="
                        + userbase + "a01.a0100)";// 目标表的过滤条件
            } else {
                strSWhere = strSWhere + " and exists (select a0100 " + whereIN + " where " + srcTab + ".a0100="
                        + userbase + "a01.a0100)";// 源表的过滤条件
                strDWhere = strDWhere + " and exists (select a0100 " + whereIN + " where " + destTab + ".a0100="
                        + userbase + "a01.a0100)";// 源表的过滤条件
            }
            String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
            String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ") ";
            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
            // System.out.println(update);
            try {
                //zxj 20170419 这一步从q03期间最后一天数据里更新Q05的逻辑是错的，也上多余的，因为后边按正确的逻辑又更新了一遍
                //dao.update(update);
            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {

                StringBuffer sql = new StringBuffer();
                sql.append("update q05 set q05." + q03fielditemid + " = qq." + q03fielditemid + " from");
                sql.append(" q05 left join (");
                sql.append(" select a0100,q03z0,nbase,max(" + q03fielditemid + ") " + q03fielditemid + " from q03");
                sql.append(" where q03.nbase='" + userbase + "' and q03.q03z0>='" + start_date + "' and q03.q03z0<='"
                        + end_date + "'");
                if (!this.userview.isSuper_admin()) {
                    sql.append(" and exists (select a0100 " + whereIN + " and q03.a0100=" + userbase + "a01.a0100)");
                    ;// 源表的过滤条件
                } else {
                    sql.append(" and exists (select a0100 " + whereIN + " where q03.a0100=" + userbase + "a01.a0100)");
                    ;// 源表的过滤条件
                }

                sql.append(" group by a0100,q03z0,nbase");
                sql.append(") qq");
                sql.append(" on q05.A0100=qq.A0100 and q05.nbase=qq.nbase ");
                sql.append(" where q05.nbase='" + userbase + "' and q05.q03z0='" + kq_month + "' and q05.q03z5='01'");
                if (!this.userview.isSuper_admin()) {
                    sql.append(" and exists (select a0100 " + whereIN + " and q05.a0100=" + userbase + "a01.a0100)");
                } else {
                    sql.append(" and exists (select a0100 " + whereIN + " where q05.a0100=" + userbase + "a01.a0100)");
                }
                // System.out.println(sql);
                try {
                    dao.update(sql.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw GeneralExceptionHandler.Handle(e);
                }

                break;
            }
            case Constant.ORACEL: {

                strSet = destTab + "." + q03fielditemid + "=MAX(" + srcTab + "." + q03fielditemid + ")";
                strSWhere = srcTab + ".nbase='" + userbase + "' and " + srcTab + ".q03z0>='" + start_date + "' and "
                        + srcTab + ".q03z0<='" + end_date + "'";
                if (!this.userview.isSuper_admin()) {
                    strSWhere = strSWhere + " and exists (select a0100 " + whereIN + " and " + srcTab + ".a0100="
                            + userbase + "a01.a0100)";// 源表的过滤条件
                } else {
                    strSWhere = strSWhere + " and exists (select a0100 " + whereIN + " where " + srcTab + ".a0100="
                            + userbase + "a01.a0100)";// 源表的过滤条件
                }
                update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
                othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ") ";
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
                // System.out.println(update);
                try {
                    dao.update(update);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw GeneralExceptionHandler.Handle(e);
                }
                break;
            }
            }

        }

    }

    /**
     * 获得当前考勤期间的开始一天和最后一天
     * 
     * @return ArrayList 0开始时间，1结束时间
     */
    public static ArrayList getcurrKq_duration() {
        ArrayList list = new ArrayList();

        StringBuffer sbu = new StringBuffer();
        sbu.append("select  min(kq_start) kq_start,");
        sbu.append("min(kq_end) kq_end from kq_duration ");
        sbu.append(" where finished='0'");

        Connection conn = null;
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sbu.toString());
            if (rs.next()) {
                Date d1 = rs.getDate("kq_start");
                Date d2 = rs.getDate("kq_end");
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
                list.add(format1.format(d1));
                list.add(format1.format(d2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
            closeDBResource(conn);
        }

        return list;
    }

    /**
     * 获得上一个考勤期间
     * 
     * @return
     */
    public static ArrayList getMaxArchiveDuration() {
        ArrayList list = new ArrayList();

        StringBuffer sbu = new StringBuffer();
        sbu.append("select  max(kq_start) kq_start,");
        sbu.append("max(kq_end) kq_end from kq_duration ");
        sbu.append(" where finished='1'");

        Connection conn = null;
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sbu.toString());
            if (rs.next()) {
                Date d1 = rs.getDate("kq_start");
                Date d2 = rs.getDate("kq_end");
                if (d1 == null || d2 == null) {
                    return list;
                }
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                list.add(format1.format(d1));
                list.add(format1.format(d2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
            closeDBResource(conn);
        }

        return list;
    }

    /**
     * 获得当前考勤 信息（开始时间，结束时间，年，月）
     * 
     * @return
     */
    public static Map getCurrKqInfo() {
        Map map = new HashMap();
        Connection conn = null;
        StringBuffer sbu = new StringBuffer();
        sbu.append("select  min(kq_start) kq_start, min(kq_end) kq_end,");
        sbu.append("min(kq_duration) kq_duration,min(kq_year) kq_year ");
        sbu.append("from kq_duration  where finished='0'");
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sbu.toString());
            if (rs.next()) {
                Date d1 = rs.getDate("kq_start");
                Date d2 = rs.getDate("kq_end");
                String month = rs.getString("kq_duration");
                String year = rs.getString("kq_year");
                if (d1 == null && d2 == null && month == null && year == null) {
                    return map;
                }
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
                map.put("kq_year", year);
                map.put("kq_month", month);
                map.put("kq_start", format1.format(d1));
                map.put("kq_end", format1.format(d2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
            closeDBResource(conn);
        }
        return map;
    }

    /**
     * 判断开始时间是否在当前考勤时间之后
     * 
     * @param start_date
     *            String 开始时间
     * @param end_date
     *            String 结束时间
     * @return boolean 在当前考勤期间为true，否则为false
     */
    public static boolean comparentWithKqDuration(String start_date) {
        boolean flag = false;
        // start_date = start_date.substring(0, 10).replaceAll("\\-", ".");
        start_date = start_date.replaceAll("\\-", ".");
        Date date = DateUtils.getDate(start_date, "yyyy.MM.dd");
        start_date = DateUtils.format(date, "yyyy.MM.dd");
        ArrayList list = getcurrKq_duration();
        String duration_start = list.get(0).toString();
        if (start_date.compareTo(duration_start) < 0) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 时间在本考勤期间范围内
     * 
     * @param s_date
     * @param e_date
     * @param userView
     * @return
     * @throws GeneralException
     */
    public boolean isExcuseCurKqDate(String s_date, String e_date) throws GeneralException {
        ArrayList datelist = RegisterDate.getKqDayList(conn);
        if (datelist == null || datelist.size() <= 0) {
            return false;
        }

        String cS_date = (String) datelist.get(0);
        String cE_date = (String) datelist.get(1);
        Date cS_D = DateUtils.getDate(cS_date, "yyyy.MM.dd");
        Date cE_D = DateUtils.getDate(cE_date, "yyyy.MM.dd");
        Date nS_D = DateUtils.getDate(s_date, "yyyy.MM.dd");
        Date nE_D = DateUtils.getDate(e_date, "yyyy.MM.dd");
        int time_1 = DateUtils.dayDiff(cS_D, nS_D);
        int time_2 = DateUtils.dayDiff(cE_D, nE_D);

        if (time_1 >= 0 && time_2 <= 0)// 完全包含在申请时间内
        {
            return true;
        }
        return false;
    }

    /**
     * 获得结余截止日期的字段代码
     * 
     * @return 结余截止日期的字段代码，不存在返回空字符
     */
    public static String getBalanceEnd() {
        // 获得年假结余的列名
        return getFieldByDesc("q17", "结余截止日期");
    }

    /**
     * 根据字段描述获得字段代码
     * 
     * @param table
     *            String 表名
     * @param desc
     *            String 字段描述
     * @return 字段代码，没有构库则返回空字符窜
     */
    public static String getFieldByDesc(String table, String desc) {
        // 获得年假结余的列名
        String balance = "";
        ArrayList fieldList = DataDictionary.getFieldList(table.toLowerCase(), Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem item = (FieldItem) fieldList.get(i);
            if (desc.equalsIgnoreCase(item.getItemdesc())) {
                balance = item.getItemid();
                break;
            }
        }
        return balance;
    }

    /**
     * 获得最后考勤期间的最后一天
     * 
     * @return String
     */
    public static String getDurationLastDay() {
        Connection conn = null;
        String last = "";
        StringBuffer sbu = new StringBuffer();
        sbu.append("select max(kq_end) kq_end ");
        sbu.append(" from kq_duration");
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sbu.toString());
            if (rs.next()) {
                Date d2 = rs.getDate("kq_end");
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
                last = format1.format(d2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
            closeDBResource(conn);

        }
        return last;
    }

    public boolean isIntoField(String table, String fieldName) {
        String sql = "SELECT ITEMID,ITEMTYPE,ITEMDESC FROM t_hr_busifield WHERE FIELDSETID='" + table
                + "' AND ITEMID = '" + fieldName + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        RowSet rrs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                String itemid = rs.getString("itemid");
                String itemtype = rs.getString("itemtype");
                String itemdesc = rs.getString("itemdesc");
                sql = "SELECT 1 FROM FIELDITEM WHERE ITEMID='" + itemid + "' AND ITEMTYPE = '" + itemtype
                        + "' AND ITEMDESC = '" + itemdesc + "'";
                rrs = dao.search(sql);
                if (rrs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
            closeDBResource(rrs);
        }
        return false;
    }

    /**
     * 对 日期 处理 在 SQL语句中
     * 
     * @param sDate
     *            开始日期字符
     * @param dbSDate
     *            开始日期字段
     * @param eDate
     *            结束日期字符
     * @param dbEDate
     *            结束日期字段
     * @param flag
     * @return
     */
    public String procWhere(String sDate, String dbSDate, String eDate, String dbEDate, int flag) {
        if (isEmpty(sDate) || isEmpty(eDate)) {
            return null;
        }
        String where = "";
        switch (flag) {
        case 1: // 数据库日期型
            if (dbSDate.equalsIgnoreCase(dbEDate)) {
                where = "(" + dbSDate + " BETWEEN " + Sql_switcher.dateValue(sDate) + " AND "
                        + Sql_switcher.dateValue(eDate) + ")";
            } else {
                where = "(" + dbSDate + ">=" + Sql_switcher.dateValue(sDate) + " AND " + dbEDate + "<="
                        + Sql_switcher.dateValue(eDate) + ")";
            }
            break;
        case 2: // 数据库日期字符型 格式 (yyyy.MM.dd 或 yyyy-MM-dd)
            sDate = sDate.substring(0, 10);
            eDate = eDate.substring(0, 10);
            if (dbSDate.equalsIgnoreCase(dbEDate)) {
                where = "(" + Sql_switcher.charToDate(dbSDate) + " BETWEEN " + Sql_switcher.dateValue(sDate) + " AND "
                        + Sql_switcher.dateValue(eDate) + ")";
            } else {
                where = "(" + Sql_switcher.charToDate(dbSDate) + ">=" + Sql_switcher.dateValue(sDate) + " AND "
                        + Sql_switcher.charToDate(dbEDate) + "<=" + Sql_switcher.dateValue(eDate) + ")";
            }
            break;
        case 3: // 数据库日期字符型 格式 (yyyy.MM 或 yyyy-MM)
            sDate = sDate.substring(0, 7).replaceAll("\\.", "-");
            eDate = eDate.substring(0, 7).replaceAll("\\.", "-");
            if (dbSDate.equalsIgnoreCase(dbEDate)) {
                where = "(REPLACE(" + dbSDate + ",'.','-') BETWEEN '" + sDate + "' AND '" + eDate + "')";
            } else {
                where = "(REPLACE(" + dbSDate + ",'.','-')>='" + sDate + "' AND REPLACE(" + dbEDate + ",'.','-')<='"
                        + eDate + "')";
            }
            break;
        case 4: // 数据库日期字符型 格式 (yyyy.MM.dd 或 yyyy-MM-dd)
            sDate = sDate.substring(0, 10).replaceAll("\\.", "-");
            eDate = eDate.substring(0, 10).replaceAll("\\.", "-");
            if (dbSDate.equalsIgnoreCase(dbEDate)) {
                where = "(REPLACE(" + dbSDate + ",'.','-') BETWEEN '" + sDate + "' AND '" + eDate + "')";
            } else {
                where = "(REPLACE(" + dbSDate + ",'.','-')>='" + sDate + "' AND REPLACE(" + dbEDate + ",'.','-')<='"
                        + eDate + "')";
            }
            break;
        default:
            return where;
        }

        return where;
    }

    /**
     * 判断字符串是否为空
     * 
     * @param pramt
     * @return 结果为 null 时 返回 true 反之 为 false
     */
    private boolean isEmpty(String pramt) {
        return pramt == null || pramt.trim().length() <= 0;
    }

    /**
     * 加班原因字段
     * 
     * @param dao
     * @return
     */
    public String getAppReaField(ContentDAO dao) {
        return getFieldByDesc("q11", "加班原因");
    }

    /**
     * 获取班次信息
     * 
     * @param classid
     * @return
     */
    public HashMap classDetails(String classid) {
        String sql = "select * from kq_class where class_id = '" + classid + "'";
        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        HashMap map = new HashMap();
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                for (int i = 1; i < 4; i++) {
                    map.put("onduty_card_" + i, rs.getString("onduty_card_" + i));
                    map.put("onduty_start_" + i, rs.getString("onduty_start_" + i));
                    map.put("onduty_end_" + i, rs.getString("onduty_end_" + i));
                    map.put("onduty_" + i, rs.getString("onduty_" + i));
                    map.put("offduty_card_" + i, rs.getString("offduty_card_" + i));
                    map.put("offduty_start_" + i, rs.getString("offduty_start_" + i));
                    map.put("offduty_end_" + i, rs.getString("offduty_end_" + i));
                    map.put("offduty_" + i, rs.getString("offduty_" + i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return map;
    }

    /**
     * 判断是否需要打卡或签到
     * 
     * @param a0100
     * @param nbase
     * @param work_date
     * @param cardStart
     * @param cardEnd
     * @param classTime
     * @return
     */
    public boolean needCard(String a0100, String nbase, String work_date, String cardStart, String cardEnd,
            String classTime) {
        boolean noCorrect = false;
        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        sb.append("select 1 from kq_originality_data");
        sb.append(" where nbase = '" + nbase + "'");
        sb.append(" and a0100 = '" + a0100 + "'");
        sb.append(" and work_date = '" + work_date.replace("-", ".") + "'");
        if (null == cardStart || cardStart.length() <= 0) {
            sb.append(" and work_time >= '" + classTime + "'");
        } else {
            sb.append(" and work_time >= '" + cardStart + "'");
        }
        if (null == cardEnd || cardEnd.length() <= 0) {
            sb.append(" and work_time <= '" + classTime + "'");
        } else {
            sb.append(" and work_time <= '" + cardEnd + "'");
        }
        sb.append(" and sp_flag <> '07'");
        try {

            rs = dao.search(sb.toString());
            if (!rs.next()) {
                noCorrect = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }

        return noCorrect;
    }

    /**
     * 得到一个基本班次的类
     * 
     * @param nbase
     * @param a0100
     * @param d_date
     * @return
     */
    public UnKqClassBean getKqClassShiftFromClassID(String class_id) {
        UnKqClassBean unKqClassBean = new UnKqClassBean();
        StringBuffer sql = new StringBuffer();
        sql.append("select class_id,");
        sql.append("onduty_card_1,offduty_card_1,onduty_card_2,offduty_card_2,");
        sql.append("onduty_card_3,offduty_card_3,onduty_card_4,offduty_card_4,");
        sql.append("onduty_start_1,onduty_1,onduty_flextime_1,be_late_for_1,absent_work_1,onduty_end_1,");
        sql.append("rest_start_1,rest_end_1,offduty_start_1,leave_early_absent_1,leave_early_1,");
        sql.append("offduty_1,offduty_flextime_1,offduty_end_1,");
        // 2
        sql.append("onduty_start_2,onduty_2,onduty_flextime_2,be_late_for_2,absent_work_2,onduty_end_2,");
        sql.append("rest_start_2,rest_end_2,offduty_start_2,leave_early_absent_2,leave_early_2,");
        sql.append("offduty_2,offduty_flextime_2,offduty_end_2,");
        // 3
        sql.append("onduty_start_3,onduty_3,onduty_flextime_3,be_late_for_3,absent_work_3,onduty_end_3,");
        sql.append("rest_start_3,rest_end_3,offduty_start_3,leave_early_absent_3,leave_early_3,");
        sql.append("offduty_3,offduty_flextime_3,offduty_end_3,");
        // 4
        sql.append("onduty_start_4,onduty_4,onduty_flextime_4,be_late_for_4,absent_work_4,onduty_end_4,");
        sql.append("rest_start_4,rest_end_4,offduty_start_4,leave_early_absent_4,leave_early_4,");
        sql.append("offduty_4,offduty_flextime_4,offduty_end_4,");
        // other
        sql.append("night_shift_start,night_shift_end,zeroflag,domain_count,work_hours,zero_absent,one_absent");
        sql.append(" FROM kq_class");
        sql.append(" where class_id='" + class_id + "'");
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                unKqClassBean.getUnKqClassBean(rs);
                unKqClassBean.setClass_id(rs.getString("class_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return unKqClassBean;
    }

    /**
     * @Title: validateDate
     * @Description: 验证字符串是否为日期
     * @param @param strDate 日期字符串(yyyy-MM-dd)
     * @return boolean
     */
    public static boolean validateDate(String strDate) {
        boolean bflag = true;

        if (strDate == null || "".equals(strDate.trim())) {
            return false;
        }

        try {
            java.util.Date date = DateStyle.parseDate(strDate);
            if (date == null) {
                bflag = false;
            }
        } catch (Exception ex) {
            bflag = false;
        }

        return bflag;
    }

    /**
     * @Title: getKqTypeWhere
     * @Description: 得到考勤类型条件
     * @param @param kqType 考勤类型代码
     * @param @param isNot 条件取反（true:"<>",false:"=")
     * @return String 带" AND "的sql条件
     * @throws
     */
    public String getKqTypeWhere(String kqType, boolean isNot) {

        KqParameter para = new KqParameter(this.userview, this.conn);
        String kqTypeFld = para.getKq_type();
        if (null == kqTypeFld || "".endsWith(kqTypeFld.trim())) {
            return " AND 1=1";
        }

        String equalFlag = "=";
        if (isNot) {
            equalFlag = "<>";
        }

        StringBuffer kqTypeWhr = new StringBuffer();
        // 考勤方式条件：不包括“暂停考勤”人员
        kqTypeWhr.append(" AND (");
        kqTypeWhr.append(Sql_switcher.sqlNull(kqTypeFld, KqConstant.KqType.STOP));
        kqTypeWhr.append(equalFlag);
        kqTypeWhr.append("'" + kqType + "'");

        // 为空时默认是暂停考勤，sqlserver需要加此条件
        if (Sql_switcher.searchDbServer() == Constant.MSSQL && kqType.equals(KqConstant.KqType.STOP)) {
            if (isNot) {
                kqTypeWhr.append(" AND ");
            } else {
                kqTypeWhr.append(" OR ");
            }
            kqTypeWhr.append(kqTypeFld + equalFlag + "''");
        }

        kqTypeWhr.append(")");
        return kqTypeWhr.toString();
    }

    /**
     * 
     * @Title: getPinYinFld
     * @Description: 取拼音简码指标
     * @return String 指标名称或空
     * @throws
     */
    public String getPinYinFld() {
        Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.conn);
        String pinyinFld = sysoth.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
        if (pinyinFld != null && pinyinFld.length() > 0 && !"#".equals(pinyinFld)) {
            DbWizard dbWizard = new DbWizard(this.conn);
            if (dbWizard.isExistField("UsrA01", pinyinFld, false)) {
                return pinyinFld;
            }
        }

        return "";
    }

    /**
     * @Title: closeDBResource
     * @Description: 关闭数据库资源（RowSet,Connection)
     * @param @param dbResource 需关闭的资源
     * @return void
     */
    public static void closeDBResource(Object dbResource) {
        PubFunc.closeResource(dbResource);
    }

    /**
     * 获取当前登录用户的登录账号名称
     * 
     * @return
     */
    public String getLogonUsername() {
        String account = "";

        if (!userview.isSuper_admin()) {
            String a0100 = userview.getA0100();
            String nbase = userview.getDbname();
            DbNameBo dbbo = new DbNameBo(this.conn);
            String username = dbbo.getLogonUserNameField().toUpperCase();
            StringBuffer sql = new StringBuffer();
            sql.append("select " + username + " from " + nbase + "A01 where A0100='" + a0100 + "'");
            ContentDAO cd = new ContentDAO(this.conn);
            RowSet rs = null;
            try {
                if ("".equals(a0100) || "".equals(nbase)) {
                    account = "";
                } else {
                    rs = cd.search(sql.toString());
                    while (rs.next()) {
                        account = rs.getString(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeDBResource(rs);
            }
        }

        return account;
    }

    /**
     * 通过工号获取人员库
     * 
     * @return
     */
    public String getNbaseByA0100(String a0100) {
        // 得到全部人员库
        List nbaseList = DataDictionary.getDbpreList();
        String nbase = "";
        RowSet rs = null;
        try {
            for (int i = 0; i < nbaseList.size(); i++) {
                nbase = (String) nbaseList.get(i);
                StringBuffer sql = new StringBuffer();
                sql.append("select 1 from " + nbase + "A01 where a0100 = '" + a0100 + "'");
                ContentDAO dao = new ContentDAO(this.conn);
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rs);
        }
        return nbase;

    }
    
    /**
     * 按所属组织权限取班次信息列表
     * @param classId 班次id,为空则取权限内所有的班次
     * @return
     */
    public ArrayList getKqClassList(String classId) {
        ArrayList res = new ArrayList();
        
        /*
         * 业务用户：0；自助：4 
         * 1、业务用户：所属部门在考勤管理范围内的班次+上级机构班次+公共班次
         * 2、自助用户：所在部门班次+上级机构班次+公共班次,有考勤部门的人员，还要加上归属于考勤部门的班次 classType
         * classType 0:公共班次,1：上级班次，2：其他班次
         */
        // 取操作用户管理班次时的权限部门
        String kqScope = getKqClassManageCode();
        RowSet rs = null;
        try {
        	ContentDAO dao = new ContentDAO(this.conn);
        	
	        StringBuffer sqlAll = new StringBuffer("");
	        sqlAll.append("select name,class_id,org_id,onduty_1,onduty_2,onduty_3,onduty_4,offduty_1,offduty_2,offduty_3,offduty_4 ");
	        sqlAll.append(" from kq_class where 1=1 ");
	        // 增加班次排序
	        sqlAll.append(" order by displayorder");
	        
	        rs = dao.search(sqlAll.toString());
            String onduty = "";
            String offduty = "";
            while (rs.next()) {
            	LazyDynaBean ldb = new LazyDynaBean();
            	ldb.set("name", rs.getString("name"));
            	String cId = (String) rs.getString("class_id");
            	ldb.set("classId", cId);
	        	String orgId = "";
	        	if(rs.getString("org_id") instanceof String){
	        		orgId = (String) rs.getString("org_id");
	        	}
	        	ldb.set("orgId", orgId);
	        	
	        	boolean bool = false;
	        	//超级用户 或者 授权最顶级节点 UN 权限全部可编辑
	        	if("-1".equals(kqScope) || "UN".equalsIgnoreCase(kqScope)){
	        		ldb.set("classType", "2");
   					bool = true;
	        	}else{
		        	if(StringUtils.isEmpty(orgId) || StringUtils.isEmpty(kqScope) 
		        			|| "UN".equalsIgnoreCase(orgId) || "null".equalsIgnoreCase(orgId)){
		        		//org_id等于空或UN 的班次为公共班次，不可编辑
		        		ldb.set("classType", "0");
		        		bool = true;
		        	}else{
		            	String[] orglist = StringUtils.split(orgId, ",");
		            	//默认可编辑
		            	ldb.set("classType", "2");
		       			for(int j=0;j<orglist.length;j++){
		       				 String orgO = orglist[j];
		       				 if(orgO.substring(2).startsWith(kqScope.substring(2))){
		       					 //该用户权限是本部门或者本部门的下属部门可以编辑
		       					 bool = true;
		       				 }else if(kqScope.substring(2).startsWith(orgO.substring(2))){
		       					 //上级部门不可编辑
		       					 ldb.set("classType", "1");
		       					 bool = true;
		       					 break;
		       				 }else{
		       					 //该班次是上级设置的多个同级部门不可编辑
	       						 ldb.set("classType", "1");
		       				 }
		       			}
		        	}
	        	}
	        	if(!bool){
	        		continue;
	        	}
	        	for (int i = 1; i <= 4; i++) {
                  onduty = PubFunc.nullToStr(rs.getString("onduty_" + i)).trim();
                  offduty = PubFunc.nullToStr(rs.getString("offduty_" + i)).trim();
                  ldb.set("onduty_" + i, onduty);
                  ldb.set("offduty_" + i, offduty);
              }
              res.add(ldb);
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return res;
    }

    /**
     * 读取班次列表
     * 
     * @return
     */
    public ArrayList getKqClassListInPriv() {
        return getKqClassList("");
    }
    
    /**
     * 是否有班次权限
     * @param classId 班次id
     * @return
     */
    public boolean classInPriv(String classId) {
        ArrayList classes = getKqClassList(classId);
        return classes != null && classes.size() > 0;
    }

    /**
     * 取操作用户管理班次时的默认所属部门
     * 
     * @Title:getKqClassManageCode
     * @Description：
     * @author liuyang
     * @return
     */
    public String getKqClassManageCode() {
        String manageCode = "UN";
        if (this.userview.isSuper_admin()) {
            return manageCode;
        }

        /*
         * 业务用户：0；自助：4 
         * 1、业务用户：所属部门在考勤管理范围内的班次+上级机构班次+公共班次
         * 2、自助用户：所在部门班次+上级机构班次+公共班次,有考勤部门的人员，还要加上归属于考勤部门的班次 
         */
        int status = userview.getStatus();
        // 业务用户获取考勤管理范围代码
        String codeid = RegisterInitInfoData.getKqPrivCode(this.userview);
        String codevalue = RegisterInitInfoData.getKqPrivCodeValue(this.userview);
        if (status != 4) {
            if (StringUtils.isEmpty(codeid) || StringUtils.isEmpty(codevalue)) {
                return manageCode;
            }

            manageCode = codeid + codevalue;
        } else {
        	// 自助用户优先走高级权限
        	if (StringUtils.isEmpty(codeid) || StringUtils.isEmpty(codevalue)) {
        		// 其次考勤部门
        		String kqDepart =KqParam.getInstance().getKqDepartment();
                if(StringUtils.isNotEmpty(kqDepart)){
                	FieldItem item = DataDictionary.getFieldItem(kqDepart);
                	String useflag = item.getUseflag();//  1 已够库，0  未够库
                	if("1".equals(useflag)){
                		String departCode =this.getKqDepartCode(kqDepart, this.userview.getA0100(), this.userview.getDbname());
                		if(StringUtils.isNotEmpty(departCode)){
                			manageCode = departCode;
                		}else {
                			// 若都没有  自助用户取所在单位或部门
                            if (StringUtils.isEmpty(userview.getUserDeptId())) {
                                manageCode = "UN" + userview.getUserOrgId();
                            } else {
                                manageCode = "UM" + userview.getUserDeptId();
                            }
                		}
                	}
                }
        	}else {
                manageCode = codeid + codevalue;
            }
        }

        return manageCode;
    }

    /**
     * 获取考勤部门编码
     * 
     * @param kqDepart
     *            考勤部门字段
     * @param a0100
     * @param nbase
     * @return
     */
    public String getKqDepartCode(String kqDepart, String a0100, String nbase) {
    	//linbz 29359 sql语句错误
    	StringBuffer sql = new StringBuffer("");
    	sql.append("select (codesetid+").append(kqDepart).append(") departCode ");
    	sql.append(" from ").append(nbase).append("A01,organization ");
    	sql.append(" where codeitemid=? and a0100=? ");
    	
    	ArrayList list = new ArrayList();
    	list.add(kqDepart);
    	list.add(a0100);
    	
//        String sql = "select (codesetid+" + kqDepart + ") departCode from " + nbase
//                + "A01,organization where codeitemid=H01SI and a0100='" + a0100 + "'";
    	
        String departCode = "";
        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                departCode = rs.getString("departCode");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return departCode;
    }

    /**
     * 获取指定考勤期间的考勤开始和结束日期
     * 
     * @param kq_duration
     *            考勤期间 格式 yyyy-MM
     * @return
     */
    public String[] getDurationStartEndDate(String kq_duration){
    	String[] res = null;   
    	if(StringUtils.isEmpty(kq_duration)) {
            return res;
        }
    	
    	String sql = "select * from kq_duration where kq_year=? and kq_duration=?";
    	ArrayList values = new ArrayList();
    	values.add(kq_duration.split("-")[0]);
    	values.add(kq_duration.split("-")[1]);
    	
    	
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql, values);
    		if(rs.next()){
    			res = new String[2];
    			res[0]  = rs.getDate("kq_start").toString().substring(0, 10);
                res[1]  = rs.getDate("kq_end").toString().substring(0, 10);
                //res[0] = rs.getString("kq_start").substring(0, 10);
                //res[1] = rs.getString("kq_end").substring(0, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取指定日期有几周
     * 
     * @param rq
     *            格式为yyyy-MM
     * @return
     * @throws Exception
     */
    public ArrayList getWeekOrder(String rq) {
        ArrayList res = new ArrayList();
        if (rq != null && !"".equals(rq)) {
            String[] kqDurations = this.getDurationStartEndDate(rq);
            if (kqDurations == null) {
                return res;
            }

            try {
                Date startDate = DateUtils.getDate(kqDurations[0], "yyyy-MM-dd");
                Date endDate = DateUtils.getDate(kqDurations[1], "yyyy-MM-dd");
                // 获取天数
                int days = DateUtils.dayDiff(startDate, endDate);

                // 当前日期所在周
                int currentWeek = 0;
                Date now = new Date();
                // 如果当前日期处于开始结束范围内
                if (this.isBelongScope(startDate, endDate, now)) {
                    currentWeek = this.getWeekOfMonth(now);
                }

                // 指定日期这月共有多少周

                int totalWeek = this.getDiffWeeks(startDate, endDate);

                String tem = "";
                CommonData vo = null;

                for (int i = 1; i <= totalWeek; i++) {
                    vo = new CommonData();
                    tem = this.turnChinese(i);

                    if (currentWeek == i) {
                        vo.setDataName("本周");
                    } else {
                        vo.setDataName("第" + tem + "周");
                    }
                    vo.setDataValue("第" + tem + "周");
                    res.add(vo);
                }
                vo = new CommonData();
                vo.setDataName("全月");
                vo.setDataValue("全月");
                res.add(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }
    /**
     * 
     * 获取指定日期处于当月的第几周
     * @Title:getWeekOfMonth
     * @Description：
     * @author liuyang
     * @param date
     * @return
     */
    public int getWeekOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }
    /**
     * 
     * @param start
     * @param end
     * @return
     */
    public int getDiffWeeks(Date start, Date end) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        c1.setFirstDayOfWeek(Calendar.MONDAY);
        c2.setFirstDayOfWeek(Calendar.MONDAY);
        
        c1.setMinimalDaysInFirstWeek(1);
        c2.setMinimalDaysInFirstWeek(1);
        
        int weekNum = 0;
        int startWeek = c1.get(Calendar.WEEK_OF_YEAR);
        int endWeek = c2.get(Calendar.WEEK_OF_YEAR);
        if (endWeek >= startWeek) {
            weekNum = endWeek - startWeek + 1;
        } else {
            weekNum = (52-startWeek+1) + endWeek;
        }
        //int res = Math.abs(c1.get(Calendar.WEEK_OF_YEAR) - c2.get(Calendar.WEEK_OF_YEAR)) + 1;
        //if (c1.get(Calendar.DAY_OF_WEEK) == 1 || c2.get(Calendar.DAY_OF_WEEK) != 1)
        //    res += 1;
        return weekNum;
    }

    /**
     * 判断指定日期是否处于给定范围内
     * 
     * @param start
     * @param end
     * @param param
     *            指定日期
     * @return
     */
    public boolean isBelongScope(Date start, Date end, Date param) {
        String formatter = "yyyy-MM-dd";
        String startDate = DateUtils.format(start, formatter);
        String endDate = DateUtils.format(end, formatter);
        String paramDate = DateUtils.format(param, formatter);
        
        return (startDate.compareTo(paramDate)<=0) && (paramDate.compareTo(endDate)<=0);
        
//        Calendar c1 = Calendar.getInstance();
//        c1.se(start);
//        Calendar c2 = Calendar.getInstance();
//        c2.setTime(end);
//        Calendar c3 = Calendar.getInstance();
//        c3.setTime(param);
//        return c3.after(c1) && c3.before(c2);
    }

    /**
     * 判断是否为同一月或同一天
     * 
     * @param date1
     * @param date2
     * @param flag
     *            month:判断是否为同一月 day:判断是否为同一天
     * @return
     */
    public boolean isSampleDate(Date date1, Date date2, String flag) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        boolean tem = c1.get(c1.YEAR) == c2.get(c2.YEAR) && c1.get(c1.MONTH) == c2.get(c2.MONTH);
        // 年月都相同就是同一月或同一天
        if (("month".equalsIgnoreCase(flag) && tem)
                || ("day".equalsIgnoreCase(flag) && tem && c1.get(c1.DAY_OF_WEEK_IN_MONTH) == c2
                        .get(c2.DAY_OF_WEEK_IN_MONTH))) {
            return true;
        }
        return false;
    }

    /**
     * 获取指定日期中每周的开始和结束日期
     * 
     * @param rq
     *            格式 yyyy-MM
     * @return
     */
    public HashMap getStartAndEndDay(String rq) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        HashMap res = new HashMap();
        try {
            String[] kqDurations = this.getDurationStartEndDate(rq);
            if (kqDurations == null) {
                return res;
            }

            Date startDate = DateUtils.getDate(kqDurations[0], "yyyy-MM-dd");
            Date endDate = DateUtils.getDate(kqDurations[1], "yyyy-MM-dd");
            Calendar c1 = Calendar.getInstance();
            c1.setFirstDayOfWeek(Calendar.MONDAY);
            // 获取rq当月的总天数
            int days = DateUtils.dayDiff(startDate, endDate);

            // 开始日期
            String start = kqDurations[0];
            // 结束日期
            String end = start;
            // 周几 即 某一天是星期几
            int dayinweek;
            int index = 1;
            String tem = "";

            for (int i = 1; i <= days; i++) {
                c1.setTime(DateUtils.getDate(end, "yyyy-MM-dd"));
                // 当周日的时候说明是一周
                if (c1.get(Calendar.DAY_OF_WEEK) == 1) {
                    tem = this.turnChinese(index);
                    res.put("第" + tem + "周", start + "至" + end);

                    start = DateUtils.format(DateUtils.addDays(startDate, i), "yyyy-MM-dd");
                    index++;
                }
                // 将格式转为yyyy-MM-dd
                end = DateUtils.format(DateUtils.addDays(startDate, i), "yyyy-MM-dd");

            }
            // 最后一周
            tem = this.turnChinese(index);
            res.put("第" + tem + "周", start + "至" + end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * getKqClassManageCode 将数字周转为汉字
     * 
     * @param i
     * @return
     */
    public String turnChinese(int i) {
        String tem = "";
        switch (i) {
        case 1:
            tem = "一";
            break;
        case 2:
            tem = "二";
            break;
        case 3:
            tem = "三";
            break;
        case 4:
            tem = "四";
            break;
        case 5:
            tem = "五";
            break;
        case 6:
            tem = "六";
            break;
        }
        return tem;
    }

    /**
     * 获取指定日期所在周的周一或周末对应日期（若rq是月初，则获取周一，若是月末，则获取周末）
     * 
     * @param rq
     *            格式yyyy-MM-dd
     * @param alreadyExits
     *            rq所在周在该月已存在的天数
     * @return
     */
    public String getRestDate(String rq, int alreadyExits) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String res = "";
        try {
            Date date = sdf.parse(rq);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            // 月末
            if (c.get(Calendar.DAY_OF_MONTH) == c.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                c.add(Calendar.DAY_OF_MONTH, 7 - alreadyExits);
                date = c.getTime();
                res = sdf.format(date);
            } else if (c.get(Calendar.DAY_OF_MONTH) == c.getActualMinimum(Calendar.DAY_OF_MONTH)) {// 月初
                c.add(Calendar.DAY_OF_MONTH, alreadyExits - 7);
                date = c.getTime();
                res = sdf.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取两个日期相差的天数
     * 
     * @param rq1
     * @param rq2
     * @param format
     *            日期格式（rq1和rq2的格式）
     * @return
     */
    public int getTimeDifDay(String rq1, String rq2, String format) {
        int res = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date1 = sdf.parse(rq1);
            Date date2 = sdf.parse(rq2);
            res = DateUtils.dayDiff(date1, date2) + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 判断给定是否为当月的月初或月末
     * 
     * @param rq
     * @param flag
     *            last:月末； first:月初；
     * @param format
     *            rq格式
     * @return
     */
    public boolean isFirstOrLastDay(String rq, String flag, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        boolean res = false;
        try {
            Date date = sdf.parse(rq);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            // 月末
            if (c.get(Calendar.DAY_OF_MONTH) == c.getActualMaximum(Calendar.DAY_OF_MONTH)
                    && "last".equalsIgnoreCase(flag)) {
                res = true;
            } else if (c.get(Calendar.DAY_OF_MONTH) == c.getActualMinimum(Calendar.DAY_OF_MONTH)
                    && "first".equalsIgnoreCase(flag))// 月初
            {
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 
     * @Title:topInstitutions
     * @Description：获取顶级部门
     * @author liuyang
     * @return 顶级部门明细
     */
    public ArrayList topInstitutions() {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();

        try {
            String sqlStr = "select codesetid,codeitemid from  organization  where   parentid = codeitemid  ";
            RowSet rs = dao.search(sqlStr);
            while (rs.next()) {
                String codesetid = rs.getString("codesetid");
                String codeitemid = rs.getString("codeitemid");
                list.add(codesetid + codeitemid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
    /**
     * 
     * @Title:saveMemo
     * @Description：二开 排班管理添加备注列 保存/修改方法
     * @author liuyang
     * @param timeInterval 时间期间
     * @param selectA0100 所选的A0100
     * @param selectNbase 所选的 nbase
     * @param selectValues 填写的内容
     * @throws Exception
     */
    public void saveMemo(String timeInterval, String selectA0100, String selectNbase, String selectValues)
            throws Exception {
        // 创建一个日期实例
        Calendar ca = Calendar.getInstance();
        // 实例化一个当前日期
        ca.setTime(new Date());
        //设置周起始日期
        ca.setFirstDayOfWeek(Calendar.MONDAY);
        //本周所在年份的第 kqWeek 周
        int kqWeek = ca.get(Calendar.WEEK_OF_YEAR);
        //判断本年共多少周
        SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        String year = timeInterval.split("-")[0];
        c1.setTime(sdfDateFormat.parse(year + "-12-31 23:59:59"));
        c1.setFirstDayOfWeek(Calendar.MONDAY);
        c1.setMinimalDaysInFirstWeek(7);
        
        String nextMemoYear = year;
        int nextKqWeek = kqWeek + 1;
        //过滤下周跨年情况
        if (kqWeek == c1.get(Calendar.WEEK_OF_YEAR)) {
            nextMemoYear = Integer.parseInt(year) + 1 + "";
            nextKqWeek = 1;
        }
        //判断本周的备注是否存在
        boolean isExistMemo = isExistMemo(selectA0100, selectNbase, year, kqWeek);
        //判断下周的备注是否村在
        boolean isNextExistMemo = isExistMemo(selectA0100, selectNbase, nextMemoYear, nextKqWeek);

        if (isExistMemo) {
            updateMemo(selectA0100, selectNbase, year, kqWeek, selectValues);
        } else {
            insertMemo(selectA0100, selectNbase, year, kqWeek, selectValues);
        }
        if (isNextExistMemo) {
            updateMemo(selectA0100, selectNbase, nextMemoYear, nextKqWeek, selectValues);
        } else {
            insertMemo(selectA0100, selectNbase, nextMemoYear, nextKqWeek, selectValues);
        }
    }
    
    /**
     * 
     * @Title:insertMemo
     * @Description：二开 排班管理添加备注列 添加
     * @author liuyang
     * @param selectA0100
     * @param selectNbase
     * @param year
     * @param kqWeek
     * @param selectValues
     * @throws Exception
     */
    private void insertMemo(String selectA0100, String selectNbase, String year, int kqWeek, String selectValues) throws Exception {
        StringBuffer stbf = new StringBuffer("");
        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            stbf.append(" insert  into ");
            stbf.append(" kq_shift_memo ");
            stbf.append(" ( nbase,a0100,kq_year,kq_week,shift_memo )");
            stbf.append(" values ");
            stbf.append(" (?,?,?,?,?)");
            
            values.add(selectNbase);
            values.add(selectA0100);
            values.add(year);
            values.add(kqWeek);
            values.add(selectValues);
            
            dao.insert(stbf.toString(), values);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @Title:updateMemo
     * @Description：二开 排班管理添加备注列 修改
     * @author liuyang
     * @param selectA0100
     * @param selectNbase
     * @param year
     * @param kqWeek
     * @param selectValues
     * @throws Exception
     */
    private void updateMemo(String selectA0100, String selectNbase, String year, int kqWeek, String selectValues) throws Exception {
        StringBuffer stbf = new StringBuffer("");
        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            stbf.append(" update  ");
            stbf.append(" kq_shift_memo ");
            stbf.append(" set ");
            stbf.append(" shift_memo=? ");
            stbf.append(" where ");
            stbf.append(" nbase=?");
            stbf.append(" and ").append(" a0100=? ");
            stbf.append(" and ").append(" kq_year=? ");
            stbf.append(" and ").append(" kq_week=? ");
            
            values.add(selectValues);
            values.add(selectNbase);
            values.add(selectA0100);
            values.add(year);
            values.add(kqWeek);
            
            dao.update(stbf.toString(), values);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * 
     * @Title:isExistMemo
     * @Description：二开 排班管理添加备注列  判断此备注，是否已经存在。
     * @author liuyang
     * @param selectA0100
     * @param selectNbase
     * @param year
     * @param kqWeek
     * @return
     * @throws GeneralException
     */
    private boolean isExistMemo(String selectA0100, String selectNbase, String year, int kqWeek) throws GeneralException {
        boolean isExistMemo = false;
        StringBuffer stbf = new StringBuffer("");
        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            values.add(selectA0100);
            values.add(selectNbase);
            values.add(year);
            values.add(kqWeek);
            stbf.append(" select nbase ");
            stbf.append(" from kq_shift_memo ");
            stbf.append(" where ");
            stbf.append(" a0100=? ");
            stbf.append("and").append(" nbase=? ");
            stbf.append("and").append(" kq_year=? ");
            stbf.append("and").append(" kq_week=? ");
            RowSet rs = dao.search(stbf.toString(), values);
            if(rs.next()) {
                isExistMemo = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return isExistMemo;
    }

    public static void setIncludeA01ForLeadingInItem(boolean includeA01ForLeadingInItem) {
        KqUtilsClass.includeA01ForLeadingInItem = includeA01ForLeadingInItem;
    }

    public static boolean isIncludeA01ForLeadingInItem() {
        return includeA01ForLeadingInItem;
    }
}
