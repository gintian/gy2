package com.hjsj.hrms.businessobject.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

public class KqClassObject {
    private Connection conn;

    public KqClassObject() {

    }

    public KqClassObject(Connection conn) {
        this.conn = conn;
    }

    /**
     * 判断班次对应表是否更新，没有更新则更新
     * @throws GeneralException
     */
    public void checkKqClassTable() throws GeneralException {
        if (!isExistFieldInKqClassTable("onduty_flextime_1")) {
            if (!ceaterFlextimeField()) {
                throw GeneralExceptionHandler.Handle(new GeneralException("", "重构考勤班次表错误", "", ""));
            }
        }
        
        if (!isExistFieldInKqClassTable("check_tran_overtime")) {
            if (!ceaterOverTimeField()) {
                throw GeneralExceptionHandler.Handle(new GeneralException("", "重构考勤班次表错误", "", ""));
            }
        }
    }

    private boolean isExistFieldInKqClassTable(String fieldName) {
        DbWizard db = new DbWizard(this.conn);
        return db.isExistField("kq_class", fieldName, false);
    }
    

    private boolean ceaterFlextimeField() {
        boolean isCorrect = true;
        
        StringBuffer sql = new StringBuffer();
        sql.append("select * from kq_class where 1=2");
        
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap hashM = new HashMap();
        try {
            rs = dao.search(sql.toString());
            ResultSetMetaData rm = rs.getMetaData();
            int column_count = rm.getColumnCount();
            for (int i = 1; i <= column_count; i++) {
                String column_name = rm.getColumnName(i);
                if (column_name == null || column_name.length() <= 0) {
                    continue;
                }
                
                hashM.put(column_name, column_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        DbWizard dbWizard = new DbWizard(this.conn);
        Table table = new Table("kq_class");
        Field temp = null;
        if (hashM.get("onduty_flextime_1") == null) {
            temp = new Field("onduty_flextime_1", "弹性上班时间1");
            temp.setDatatype(DataType.STRING);
            temp.setLength(5);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("offduty_flextime_1") == null) {
            temp = new Field("offduty_flextime_1", "弹性下班时间1");
            temp.setDatatype(DataType.STRING);
            temp.setKeyable(false);
            temp.setVisible(false);
            temp.setLength(5);
            table.addField(temp);
        }
        if (hashM.get("onduty_flextime_2") == null) {
            temp = new Field("onduty_flextime_2", "弹性上班时间2");
            temp.setDatatype(DataType.STRING);
            temp.setLength(5);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("offduty_flextime_2") == null) {
            temp = new Field("offduty_flextime_2", "弹性下班时间2");
            temp.setDatatype(DataType.STRING);
            temp.setLength(5);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("onduty_flextime_3") == null) {
            temp = new Field("onduty_flextime_3", "弹性上班时间3");
            temp.setDatatype(DataType.STRING);
            temp.setLength(5);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("offduty_flextime_3") == null) {
            temp = new Field("offduty_flextime_3", "弹性下班时间3");
            temp.setDatatype(DataType.STRING);
            temp.setLength(5);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("onduty_flextime_4") == null) {
            temp = new Field("onduty_flextime_4", "弹性上班时间4");
            temp.setDatatype(DataType.STRING);
            temp.setLength(5);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("offduty_flextime_4") == null) {
            temp = new Field("offduty_flextime_4", "弹性下班时间4");
            temp.setDatatype(DataType.STRING);
            temp.setLength(5);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }

        /*1、是否检测延时加班情况：check_tran_overtime，Vachar(1) 
        2、下班多久后开始计加班：overtime_from，int
        3、延时加班默认加班类型：overtime_type，varchar(30)*/
        if (hashM.get("check_tran_overtime") == null) {
            temp = new Field("check_tran_overtime", "是否检测延时加班情况");
            temp.setDatatype(DataType.STRING);
            temp.setLength(2);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("overtime_from") == null) {
            temp = new Field("overtime_from", "下班多久后开始计加班");
            temp.setDatatype(DataType.INT);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        if (hashM.get("overtime_type") == null) {
            temp = new Field("overtime_type", "延时加班默认加班类型");
            temp.setDatatype(DataType.STRING);
            temp.setLength(30);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }

        try {
            dbWizard.addColumns(table);
            DBMetaModel dbmodel = new DBMetaModel(this.conn);
            dbmodel.reloadTableModel("kq_class");
            DataDictionary.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            isCorrect = false;
        }
        return isCorrect;
    }

    /**
     * 得到班次的属性值
     * @param class_id
     * @param filedname
     * @return
     */
    public String getClassFiledValue(String class_id, String filedname) {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + filedname + " from kq_class where class_id='" + class_id + "'");
        
        ContentDAO dao = new ContentDAO(this.conn);
        String fieldvalue = "";
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                fieldvalue = rs.getString(filedname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return fieldvalue;
    }

    private boolean ceaterOverTimeField() {
        boolean isCorrect = true;
        
        DbWizard dbWizard = new DbWizard(this.conn);
        Table table = new Table("kq_class");
        
        Field temp = new Field("check_tran_overtime", "需检测延时加班情况");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("overtime_from", "下班多久后开始算加班");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        temp = new Field("overtime_type", "延时加班默认加班类型");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        
        try {
            dbWizard.addColumns(table);
            DBMetaModel dbmodel = new DBMetaModel(this.conn);
            dbmodel.reloadTableModel("kq_class");
            DataDictionary.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            isCorrect = false;
        }
        
        return isCorrect;
    }
}
