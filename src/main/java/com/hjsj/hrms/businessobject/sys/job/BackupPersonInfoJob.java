package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BackupPersonInfoJob implements Job {
    private Logger log = LoggerFactory.getLogger(BackupPersonInfoJob.class);

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        log.info("[人员数据快照生成后台作业]任务开始");
        long start = System.currentTimeMillis();
        Connection conn = null;
        RowSet rs = null;
        try {
            conn = (Connection) AdminDb.getConnection();
			/*OrgPreBo org = new OrgPreBo(conn);
			org.condRealPerson();

			ContentDAO dao = new ContentDAO(conn);
			String password = "";
			rs = dao
					.search("select Password from operuser where UserName='su'");
			if (rs.next()) {
				password = rs.getString("Password");
				password = password != null ? password : "";
			}
			UserView uv = new UserView("su", password, conn);
			uv.canLogin();
			org.doCount(conn, uv);*/
            this.execute(conn, rs);
            log.info("[人员数据快照生成后台作业]任务结束===[consume time is {} ms]===", (System.currentTimeMillis() - start));
        } catch (GeneralException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void execute(Connection conn, RowSet rs) throws GeneralException {
        // TODO Auto-generated method stub
        String msg = "ok";
        String creat_date = "";
        String description = "";
        description = com.hrms.frame.codec.SafeCode.decode(description);
        try {
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            creat_date = sdf.format(new java.util.Date());
            sql.append("select id from hr_hisdata_list where create_date="
                    + Sql_switcher.dateValue(creat_date));
            rs = dao.search(sql.toString());
            if (!rs.next()) {
                Date date = new Date(sdf.parse(creat_date).getTime());
                String id = getMaxId(dao, rs);
                String struct = "";
                String query = "";
                String base = "";
                String HzMenus = "";
                rs = dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
                if (rs.next()) {
                    ConstantXml xml = new ConstantXml(conn, "HISPOINT_PARAMETER", "Emp_HisPoint");
                    struct = xml.getTextValue("/Emp_HisPoint/Struct");
                    query = xml.getTextValue("/Emp_HisPoint/Query");
                    base = xml.getTextValue("/Emp_HisPoint/Base");
                    HzMenus = xml.getTextValue("/Emp_HisPoint/HzMenus");
                }
                String str_value = "";
                if (struct.length() == 0) {
                    sql.setLength(0);
                    sql
                            .append("select str_value from constant where upper(constant)='EMP_HISDATA_STRUCT'");
                    rs = dao.search(sql.toString());

                    if (rs.next()) {
                        str_value = Sql_switcher.readMemo(rs, "str_value");
                    }
                } else {
                    str_value = struct;
                }

                if (!str_value.endsWith(",")) {
                    str_value = str_value + ",";
                }
                String[] str_values = str_value.split(",");
                for (int i = 0; i < str_values.length; i++) {
                    if (str_values[i].length() != 5) {
                        continue;
                    }
                    FieldItem fielditem = DataDictionary.getFieldItem(str_values[i].toLowerCase());
                    if (fielditem == null || !"1".equals(fielditem.getUseflag())) {
                        str_value = str_value.replaceAll(str_values[i] + ",", "");
                    }
                }

                ArrayList paralist = new ArrayList();
                sql.setLength(0);
                sql
                        .append("insert into hr_hisdata_list(Id,create_date,description,snap_fields)values(?,?,?,?)");
                paralist.add(new Integer(id));
                paralist.add(date);
                paralist.add(description);
                paralist.add(str_value);

                dao.insert(sql.toString(), paralist);

                String year = getYear(creat_date);
                String month = getMonth(creat_date);
                sql.setLength(0);
                sql.append("select * from hr_emp_hisdata where 1=2");
                rs = dao.search(sql.toString());
                ResultSetMetaData rsmd = rs.getMetaData();
                int size = rsmd.getColumnCount();
                ArrayList dropcolumssql = new ArrayList();
                DbWizard dbw = new DbWizard(conn);
                Table table = new Table("hr_emp_hisdata");
                //指标集合  fm<key:fieldsetid ,value:ArrayList<itemid>>
                HashMap fm = new HashMap();
                //主要指标
                String maincolumn = "";

                StringBuffer itemidStr = new StringBuffer();
                //解析指标，按照fieldset 分类
                for (int i = 1; i <= size; i++) {
                    String itemid = rsmd.getColumnName(i);
                    /*判断长度*/
                    FieldItem item = null;
                    if (!"id".equalsIgnoreCase(itemid) && !"nbase".equalsIgnoreCase(itemid)) {
                        item = DataDictionary.getFieldItem(itemid.toLowerCase());
                    }
                    int itemLength = rsmd.getColumnDisplaySize(i);
                    if (item != null && "A".equals(item.getItemtype())) {
                        if (itemLength < item.getItemlength()) {
                            if (!"A0100".equalsIgnoreCase(item.getItemid())) {
                                Field item_o = item.cloneField();
                                table.addField(item_o);
                            }
                        }
                    }


                    if ("id".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid)
                            || "b0110".equalsIgnoreCase(itemid)
                            || "e0122".equalsIgnoreCase(itemid)
                            || "e01a1".equalsIgnoreCase(itemid)
                            || "a0101".equalsIgnoreCase(itemid)
                            || "a0100".equalsIgnoreCase(itemid)
                            || "a0000".equalsIgnoreCase(itemid)) {
                        maincolumn += "," + itemid;
                        continue;
                    }
                    if (item == null || !"1".equals(item.getUseflag())) {
                        dropcolumssql.add("alter table hr_emp_hisdata  drop column " + itemid);
                        continue;
                    }
                    String setid = item.getFieldsetid();
                    if (fm.containsKey(setid)) {
                        ArrayList fl = (ArrayList) fm.get(setid);
                        fl.add(itemid);
                    } else {
                        ArrayList fl = new ArrayList();
                        fl.add(itemid);
                        fm.put(setid, fl);
                    }

                    itemidStr.append(itemid + ",");
                }
                if (table.getCount() != 0) {
                    dbw.alterColumns(table);
                }
                deleteColums(dropcolumssql);


                //如果表中不存在唯一标识，则添加进去
                String uniqueItem = addUniqueItem(itemidStr.toString(), conn);
                if (uniqueItem != null) {
                    FieldItem unifi = DataDictionary.getFieldItem(uniqueItem);
                    if (fm.containsKey(unifi.getFieldsetid())) {
                        ArrayList fl = (ArrayList) fm.get(unifi.getFieldsetid());
                        fl.add(uniqueItem);
                    } else {
                        ArrayList fl = new ArrayList();
                        fl.add(uniqueItem);
                        fm.put(unifi.getFieldsetid(), fl);
                    }
                }


                if (base.length() == 0) {
                    sql.setLength(0);
                    sql.append("select str_value from constant where upper(constant)='EMP_HISDATA_BASE'");
                    rs = dao.search(sql.toString());
                    if (rs.next()) {
                        str_value = Sql_switcher.readMemo(rs, "str_value");
                    } else {
                        str_value = "";
                    }
                } else {
                    str_value = base;
                }
                String[] fields = str_value.split(",");
                ArrayList dblist = new ArrayList();
                for (int i = 0; i < fields.length; i++) {
                    String pre = fields[i];
                    if (pre.length() == 3) {
                        dblist.add(pre);
                    }
                }
                if (dblist.size() == 0) {
                    dblist.add("Usr");
                }
                size = dblist.size();
                for (int i = 0; i < size; i++) {
                    String nbase = (String) dblist.get(i);
                    sql.setLength(0);

                    //先插入关键字段
                    sql.append(" insert into hr_emp_hisdata(" + maincolumn.substring(1) + ")");
                    String valuecolumn = maincolumn.toString().toUpperCase();
                    valuecolumn = valuecolumn.replaceAll(",ID", "," + id);
                    valuecolumn = valuecolumn.replaceAll(",NBASE", ",'" + nbase + "'");
                    sql.append(" select " + valuecolumn.substring(1) + " from " + nbase + "A01 ");
                    dao.update(sql.toString());

                    //逐个子集字段更新
                    for (Iterator ite = fm.keySet().iterator(); ite.hasNext(); ) {
                        String fieldsetid = ite.next().toString();
                        FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
                        String changeflag = fieldset.getChangeflag();
                        ArrayList fl = (ArrayList) fm.get(fieldsetid);
                        String sqlstr = createSql(id, fieldsetid, changeflag, nbase, fl, year, month);
                        dao.update(sqlstr);
                    }
                }
                if (HzMenus.length() > 8) {
                    this.countHzMenus(dao, id, dblist, HzMenus, year, month);
                }
            } else {
                System.out.print("同一天内不能快照人员信息多次!");
            }
        } catch (Exception e) {
            log.error("人员数据快照后台作业出错!,desc:{}", e);
            msg = "error";
            e.printStackTrace();
        } finally {
            if ("ok".equals(msg)) {

            } else {

            }
        }
    }

    private String getMaxId(ContentDAO dao, RowSet rs) throws GeneralException {
        int nid = 1;
        StringBuffer sql = new StringBuffer(
                "select max(id)+1 as nmax from hr_hisdata_list");
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                nid = rs.getInt("nmax");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return String.valueOf(nid);
    }

    private String getYear(String date) {
        date = date.substring(0, 4) + "-01-01";
        date = Sql_switcher.dateValue(date);
        return date;
    }

    private String getMonth(String date) {
        date = date.substring(0, 7) + "-01";
        date = Sql_switcher.dateValue(date);
        return date;
    }

    private String addUniqueItem(String haveItem, Connection connection) throws GeneralException,
            SQLException {
        String uniqueitem = null;
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(connection);
        String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");//省份证
        String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");//唯一性指标
        String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "valid");
        String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
        if (chkvalid == null) {
            chkvalid = "0";
        }
        if (uniquenessvalid == null) {
            chkvalid = "0";
        }
        if (uniquenessvalid == null) {
            uniquenessvalid = "";
        }
        String chkcheck = "", uniquenesscheck = "";

        if ("0".equalsIgnoreCase(chkvalid) || "".equalsIgnoreCase(chkvalid)) {
            chkcheck = "";
        } else {
            chkcheck = "checked";
        }
        if ("0".equalsIgnoreCase(uniquenessvalid) || "".equalsIgnoreCase(uniquenessvalid)) {
            uniquenesscheck = "";
        } else {
            uniquenesscheck = "checked";
        }
        StringBuffer setdb = new StringBuffer();
        if (chk == null) {
            chk = "";
        }
        if (onlyname == null) {
            onlyname = "";
        }
        if (chk.length() > 0 && "checked".equals(chkcheck)) {
            uniqueitem = chk;
        } else if (onlyname.length() > 0 && "checked".equals(uniquenesscheck)) {
            uniqueitem = onlyname;
        } else {
            uniqueitem = "a0100";
        }
        uniqueitem = uniqueitem.toUpperCase();
        if (haveItem.toUpperCase().indexOf(uniqueitem.toUpperCase()) == -1) {
            Connection conn = AdminDb.getConnection();
            try {
                DbWizard dbw = new DbWizard(conn);
                Table table = new Table("hr_emp_hisdata");
                FieldItem item = DataDictionary.getFieldItem(uniqueitem);
                if (item != null) {
                    table.addField(item);
                }
                if (table.getCount() > 0) {
                    dbw.addColumns(table);
                    haveItem = haveItem + "," + uniqueitem;
                }
            } finally {
            	PubFunc.closeResource(conn);
            }
            return uniqueitem;
        }
        return null;
    }

    private void countHzMenus(ContentDAO dao, String id, ArrayList dblist, String HzMenus, String year, String month) throws SQLException {
        String[] HzMenuss = HzMenus.split(",");
        StringBuffer sql = new StringBuffer();
        sql.append("update hr_emp_hisdata set ");
        for (int i = 0; i < HzMenuss.length; i++) {
            String menu = HzMenuss[i];
            String[] menus = menu.split(":");
            if (menus.length == 2) {
                String itemid = menus[0];
                String type = menus[1];
                FieldItem fielditem = DataDictionary.getFieldItem(itemid);
                if (fielditem != null) {
                    String fieldsetid = fielditem.getFieldsetid();
                    FieldSet fieldset = DataDictionary
                            .getFieldSetVo(fieldsetid);
                    String changeflag = fieldset.getChangeflag();
                    if ("2".equals(changeflag)) {
                        if (i != 0) {
                            sql.append(",");
                        }
                        sql.append(itemid + "=(select " + type + "(" + itemid + ") from Usr"
                                + fieldsetid
                                + " t" + i + " where t" + i
                                + "." + fieldsetid + "Z0=" + year
                                + " and t" + i
                                + ".a0100=hr_emp_hisdata.a0100)");

                    } else if ("1".equals(changeflag)) {
                        if (i != 0) {
                            sql.append(",");
                        }
                        sql.append(itemid + "=(select " + type + "(" + itemid + ") from Usr"
                                + fieldsetid
                                + " t" + i + " where t" + i
                                + "." + fieldsetid + "Z0=" + month
                                + " and t" + i
                                + ".a0100=hr_emp_hisdata.a0100)");
                    }
                }
            }
        }
        sql.append(" where id=" + id + " and nbase='Usr'");
        for (int i = 0; i < dblist.size(); i++) {
            String dbname = (String) dblist.get(i);
            String sqlstr = sql.toString().replaceAll("Usr", dbname);
            dao.update(sqlstr);
        }
    }

    private String createSql(String hisid, String fieldsetid, String changeflag, String nbase, ArrayList fl, String year, String month) {

        StringBuffer sql = new StringBuffer();
        String tablename = nbase + fieldsetid;
        boolean mainSetFlag = "A01".equalsIgnoreCase(fieldsetid) ? true : false;
        switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                sql.append(" update hr_emp_hisdata set ");
                for (int k = 0; k < fl.size(); k++) {
                    String itemid = fl.get(k).toString();
                    sql.append(itemid + "=b." + itemid + " ,");
                }
                sql.deleteCharAt(sql.length() - 1);
                sql.append(" from " + tablename + " b where hr_emp_hisdata.a0100=b.a0100 and hr_emp_hisdata.nbase='" + nbase + "' and hr_emp_hisdata.ID=" + hisid);
                break;
            }
            case Constant.ORACEL: {
                sql.append(" update hr_emp_hisdata set (");
                String itemstr = "";
                for (int k = 0; k < fl.size(); k++) {
                    String itemid = fl.get(k).toString();
                    //sql.append(itemid+" ,");
                    itemstr += itemid + " ,";
                }
                itemstr = itemstr.substring(0, itemstr.length() - 1);
                //sql.deleteCharAt(sql.length()-1);
                sql.append(itemstr + ")=(select " + itemstr);
                sql.append(" from " + tablename + " b where hr_emp_hisdata.a0100=b.a0100 ");
                break;
            }
        }

        if (!mainSetFlag) {
            if ("2".equals(changeflag)) {
                sql.append(" and b"
                        + ".i9999=(select max(i9999) from "
                        + tablename + " t" + " where t"
                        + "." + fieldsetid + "Z0=" + year
                        + " and t" + "." + fieldsetid
                        + "Z1=(select max(" + fieldsetid
                        + "Z1) from " + tablename + " tt"
                        + " where tt" + "."
                        + fieldsetid + "Z0=" + year
                        + "  and tt"
                        + ".a0100=b.a0100) and t"
                        + ".a0100=b.a0100 group by t.a0100) ");

            } else if ("1".equals(changeflag)) {
                sql.append(" and b"
                        //+ tablename
                        + ".i9999=(select max(i9999) from "
                        + tablename + " t" + " where t"
                        + "." + fieldsetid + "Z0=" + month
                        + " and t" + "." + fieldsetid
                        + "Z1=(select max(" + fieldsetid
                        + "Z1) from " + tablename + " tt"
                        + " where tt" + "."
                        + fieldsetid + "Z0=" + month
                        + " and tt"
                        + ".a0100=b.a0100) and t"
                        + ".a0100=b.a0100 group by t.a0100) ");

            } else {
                sql.append(" and b"
                        //+ tablename
                        + ".i9999=(select max(i9999) from "
                        + tablename + " t" + " where t"
                        + ".a0100=b.a0100) ");
            }
        }

        if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
            sql.append(") where nbase='" + nbase + "' and ID=" + hisid);
        }
        return sql.toString();
    }


    private void deleteColums(ArrayList dropcolumssql) {
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            if (dropcolumssql.size() > 0) {
                ContentDAO dao = new ContentDAO(conn);
                for (int i = 0; i < dropcolumssql.size(); i++) {
                    dao.update((String) dropcolumssql.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
