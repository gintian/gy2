package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class ReconstructionKqField {

    private Connection conn;

    
    private ReconstructionKqField() {
    }

    public ReconstructionKqField(Connection conn, UserView userView) {
        this.conn = conn;
    }
    
    public ReconstructionKqField(Connection conn) {
        this.conn = conn;
    }

    /**
     * @Title: checkFieldSave   
     * @Description: 检查字段是否存在  
     * @param @param table
     * @param @param field_name
     * @param @return 
     * @return boolean    
     * @throws
     */
    public boolean checkFieldSave(String table, String field_name) {
        DbWizard db = new DbWizard(this.conn);
        return db.isExistField(table, field_name, false);
    }

    public boolean checkFieldType(String table, String field_name, String fieldtype) {
        boolean isCorrect = false;
        String sql = "select " + field_name + " from " + table + " where 1=2";
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString());
            ResultSetMetaData rm = rs.getMetaData();
            int column_count = rm.getColumnCount();
            for (int i = 1; i <= column_count; i++) {
                String md_type = getTypeByFieldType(rm, i);
                if (md_type != null && md_type.equalsIgnoreCase(fieldtype)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }

    private String getTypeByFieldType(ResultSetMetaData rsetmd, int j) throws SQLException {
        String temp = "";
        switch (rsetmd.getColumnType(j)) {
        case Types.DATE:
        case Types.TIMESTAMP:
        case Types.TIME:
            temp = "D";
            break;
        case Types.CLOB:
        case Types.LONGVARCHAR:
        case Types.BLOB:
        case Types.LONGVARBINARY:
            temp = "M";
            break;
        case Types.NUMERIC:
        case Types.FLOAT:
        case Types.INTEGER:
            temp = "N";
            break;
        default:
            temp = "A";
            break;
        }
        return temp;
    }

    public boolean ceaterField_originality(ArrayList list, String table_name) {
        boolean isCorrect = true;
        DbWizard dbWizard = new DbWizard(this.conn);
        Table table = new Table(table_name);
        for (int i = 0; i < list.size(); i++) {
            Field temp = (Field) list.get(i);
            table.addField(temp);
        }
        try {
            dbWizard.addColumns(table);
            DBMetaModel dbmodel = new DBMetaModel(this.conn);
            dbmodel.reloadTableModel(table_name);
        } catch (Exception e) {
            //e.printStackTrace();
            isCorrect = false;
        }
        return isCorrect;
    }

    /**
     * 已废弃，不要再调用了。转库大师已处理了这些字段。
     * 系统重构原始数据表
     * @throws GeneralException
     */
    @Deprecated
    public void cheakOriginality_data() throws GeneralException {

        ArrayList list = new ArrayList();
        Field temp = new Field("inout_flag", "出入标志");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        list.add(temp);
        temp = new Field("oper_cause", "补刷原因");
        temp.setDatatype(DataType.STRING);
        temp.setKeyable(false);
        temp.setVisible(false);
        temp.setLength(50);
        list.add(temp);
        temp = new Field("oper_user", "补刷操作员");
        temp.setDatatype(DataType.STRING);
        temp.setKeyable(false);
        temp.setVisible(false);
        temp.setLength(50);
        list.add(temp);
        temp = new Field("oper_time", "补刷时间");
        temp.setDatatype(DataType.DATETIME);
        temp.setKeyable(false);
        temp.setVisible(false);
        list.add(temp);
        temp = new Field("oper_mach", "机器ip或机器名");
        temp.setDatatype(DataType.STRING);
        temp.setKeyable(false);
        temp.setVisible(false);
        temp.setLength(50);
        list.add(temp);
        if (!checkFieldSave("kq_originality_data", "inout_flag")) {
            if (!ceaterField_originality(list, "kq_originality_data")) {
                throw GeneralExceptionHandler.Handle(new GeneralException("", "重构考勤班次表错误", "", ""));
            }
        }
        list = new ArrayList();
        temp = new Field("sp_flag", "审批标志");
        temp.setDatatype(DataType.STRING);
        temp.setLength(2);
        temp.setKeyable(false);
        temp.setVisible(false);
        list.add(temp);
        if (!checkFieldSave("kq_originality_data", "sp_flag")) {
            if (!ceaterField_originality(list, "kq_originality_data")) {
                throw GeneralExceptionHandler.Handle(new GeneralException("", "重构考勤班次表错误", "", ""));
            }
            String upSQL = "update kq_originality_data set sp_flag='03' where sp_flag is null";
            ContentDAO dao = new ContentDAO(this.conn);
            try {
                dao.update(upSQL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
