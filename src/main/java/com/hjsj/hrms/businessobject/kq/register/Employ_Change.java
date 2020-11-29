package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;

public class Employ_Change {
    /**
       * 得到考勤日期员工增减表操作的sql
       * */
    public static String getDateSql(String userbase, String start_date, String end_date, String whereIN, int status) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append("select nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag ");
        selectSQL.append(" from kq_employ_change K where status=" + status + " ");
        //selectSQL.append(" and change_date>="+Sql_switcher.dateValue(start_date)+" ");
        //selectSQL.append(" and change_date<="+Sql_switcher.dateValue(end_date)+" ");	
        selectSQL.append(" and nbase='" + userbase + "' ");
        selectSQL.append(" and a0100 in (select a0100 " + whereIN + ")");
        /*selectSQL.append(" and NOT EXISTS( SELECT * FROM "+userbase+"A01 A WHERE A.A0100=K.A0100 ");
        
        selectSQL.append(")");*/
        return selectSQL.toString();
    }

    public static String getDateSqlAdd(String userbase, String start_date, String end_date, String whereIN, int status) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append("select nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag ");
        selectSQL.append(" from kq_employ_change K where status=" + status + " ");
        //selectSQL.append(" and change_date>="+Sql_switcher.dateValue(start_date)+" ");
        //selectSQL.append(" and change_date<="+Sql_switcher.dateValue(end_date)+" ");	
        selectSQL.append(" and nbase='" + userbase + "' ");
        selectSQL.append(" and EXISTS( SELECT * FROM " + userbase + "A01 A WHERE A.A0100=K.A0100 ");
        selectSQL.append(" and a0100 in(select a0100 " + whereIN + "))");
        return selectSQL.toString();
    }

    public static String getDateSqlAdd(String userbase, String whereIN, int status) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append("select nbase,A0100,B0110,E0122,A0101,E01A1,change_date,status,flag,change_end_date ");
        selectSQL.append(" from kq_employ_change K where status=" + status + " ");
        //selectSQL.append(" and change_date>="+Sql_switcher.dateValue(start_date)+" ");
        //selectSQL.append(" and change_date<="+Sql_switcher.dateValue(end_date)+" ");	
        selectSQL.append(" and nbase='" + userbase + "' ");
        selectSQL.append(" and EXISTS( SELECT * FROM " + userbase + "A01 A WHERE A.A0100=K.A0100 ");
        selectSQL.append(" and a0100 in(select a0100 " + whereIN + "))");
        return selectSQL.toString();
    }

    public static String getChangeDateWheree() {
        StringBuffer selectSQL = new StringBuffer();
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL: {
            selectSQL.append(" and change_date>=? ");
            selectSQL.append(" and change_date<=? ");
            break;
        }
        case Constant.DB2: {
            selectSQL.append(" and change_date>=TO_Date(?,'YYYY-MM-DD') ");
            selectSQL.append(" and change_date<=TO_Date(?,'YYYY-MM-DD') ");
            break;
        }
        case Constant.ORACEL: {
            selectSQL.append(" and change_date>=TO_Date(?,'YYYY-MM-DD HH24:MI:SS') ");
            selectSQL.append(" and change_date<=TO_Date(?,'YYYY-MM-DD HH24:MI:SS') ");
            break;
        }
        }
        return selectSQL.toString();
    }

    public static ArrayList getSql(String start_date, String end_date, ArrayList kq_dbase_list, UserView userView) {
        ArrayList list = new ArrayList();
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append("select nbase,A0100,B0110,E0122,A0101,change_date,status ");
        list.add(selectSQL.toString());
        StringBuffer whereSQL = new StringBuffer();
        whereSQL.append(" from kq_employ_change where status=1 ");
        start_date = start_date.replaceAll("-", "\\.");
        end_date = end_date.replaceAll("-", "\\.");
        whereSQL.append(" and change_date>=" + Sql_switcher.dateValue(start_date) + " ");
        whereSQL.append(" and change_date<=" + Sql_switcher.dateValue(end_date) + " ");
        if (kq_dbase_list != null && kq_dbase_list.size() > 0) {
            String userbase = kq_dbase_list.get(0).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, userbase);
            whereSQL.append(" and a0100 in(select a0100 " + whereIN + ")");
            for (int i = 1; i < kq_dbase_list.size(); i++) {
                userbase = kq_dbase_list.get(i).toString();
                whereIN = RegisterInitInfoData.getWhereINSql(userView, userbase);
                whereSQL.append(" or a0100 in(select a0100 " + whereIN + ")");
            }
        }
        list.add(whereSQL.toString());
        String orderby = "order by nbase";
        String column = "nbase,A0100,B0110,E0122,A0101,change_date,status";
        list.add(orderby);
        list.add(column);
        return list;
    }

    public static String ceaterEmpBaseManage(UserView userView, Connection conn) {
        String table_name = "t#" + userView.getUserName() + "_emp_ch";
        table_name = table_name.toLowerCase();
        DbWizard dbWizard = new DbWizard(conn);
        Table table = new Table(table_name);
        if (dbWizard.isExistTable(table_name, false)) {
            dbWizard.dropTable(table_name);
        }
        
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("A0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("B0110", "单位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("OB0110", "原单位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);

        // 添加变动时间
        if (KqParam.getInstance().getDeptChangeDateField().length() > 0) {
            temp = new Field("change_date", "变更时间");
            temp.setDatatype(DataType.STRING);
            temp.setLength(20);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }

        temp = new Field("E0122", "部门");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("OE0122", "原部门");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("E01A1", "职位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("OE01A1", "原职位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("A0101", "姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("OA0101", "曾用名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("flag", "标志");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);

        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**重新加载数据模型*/

        DBMetaModel dbmodel = new DBMetaModel(conn);
        dbmodel.reloadTableModel(table_name);
        return table_name;
    }
}
