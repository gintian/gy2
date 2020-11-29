package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * @author wangjh
 * 2013-10-27
 * 业务用户查询结果表可能不存在，本类检查并自动创建
 */
public class AutoCreateQueryResultTable {

	/**
	 * @param conn
	 * @param tablename
	 * @param type： 1,2,3=  人员，单位，职位
	 * @throws GeneralException
	 */
	public static void execute(Connection conn, String tablename, String type) throws GeneralException{
    	// 检查查询表，不存在自动创建
		if (conn==null || tablename==null || type==null){
			return;
		}
		
		try {
		    DbWizard dbWizard = new DbWizard(conn);
		    if (!dbWizard.isExistTable(tablename, false)) {
		        Table table = new Table(tablename);	
		        if("1".equals(type)){
		            FieldItem a0100item = DataDictionary.getFieldItem("A0100");
		            FieldItem b0110item = DataDictionary.getFieldItem("B0110");
		            table.addField(a0100item);
		            table.addField(b0110item);
		        }else if("2".equals(type)){
		            FieldItem b0110item = DataDictionary.getFieldItem("B0110");
		            table.addField(b0110item);
		        }else if("3".equals(type)){
		            FieldItem e01a1item = DataDictionary.getFieldItem("E01A1");
		            table.addField(e01a1item);
		        }else{
		            FieldItem a0100item = DataDictionary.getFieldItem("A0100");
		            FieldItem b0110item = DataDictionary.getFieldItem("B0110");
		            table.addField(a0100item);
		            table.addField(b0110item);
		        }
		        dbWizard.createTable(table);
		        addPrimaryKey(conn, tablename, type);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
        }
		
	}
	/**
	 * 检测结果集是否存在主键如果不存在则添加主键
	 * @param conn 数据库链接
	 * @param tablename 结果集表名
	 * @param type 类别  1,2,3=  人员，单位，职位
	 * @throws GeneralException
	 */
	public static void addPrimaryKey(Connection conn, String tablename, String type) throws GeneralException{
	    // 检查查询表，不存在自动创建
	    if (conn==null || tablename==null || type==null){
	        return;
	    }
	    
	    PreparedStatement stm = null;
	    RowSet rs = null;
	    try {
	        
	        DbWizard dbWizard = new DbWizard(conn);
	        if (!dbWizard.isExistTable(tablename, false))
	            return;
	        
	        ContentDAO dao = new ContentDAO(conn);
	        String sql = "SELECT 1 from sys.key_constraints where parent_object_id=object_id(?) and type='PK'";
	        if(Sql_switcher.searchDbServer() == Constant.ORACEL)
	            sql = "select 1 from user_constraints t where t.table_name = upper(?) and t.constraint_type = 'P'";
	        
	        ArrayList<String> value = new ArrayList<String>();
	        value.add(tablename);
	        rs = dao.search(sql, value);
	        if(rs.next())
	            return;
	        
	        String colname = "";
	        Table table = new Table(tablename);	
	        if("1".equals(type))
	            colname = "A0100";
	        else if("2".equals(type))
	            colname = "B0110";
	        else if("3".equals(type))
	            colname = "E01A1";
	        else
	            colname = "A0100";
	        
	        if(!dbWizard.isExistField(tablename, colname))
	            return;
	        
	        FieldItem fi = DataDictionary.getFieldItem(colname);
	        if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
	            sql = "select nullable from user_tab_cols where table_name=? and COLUMN_NAME=?";
	            ArrayList<String> values = new ArrayList<String>();
	            values.add(tablename.toUpperCase());
	            values.add(colname.toUpperCase());
	            rs = dao.search(sql, values);
	            String nullAble = "N";
	            if(rs.next())
	                nullAble = rs.getString("nullable");
	            
	            if(!"N".equalsIgnoreCase(nullAble)) {
	                sql = "ALTER TABLE " + tablename + " modify " + colname + " varchar(" + fi.getItemlength() + ") NOT NULL";
	                dao.update(sql);
	            }
	        } else {
	            sql = "ALTER TABLE " + tablename + " ALTER COLUMN " + colname + " varchar(" + fi.getItemlength() + ") NOT NULL";
	            dao.update(sql);
	        }
	        
	        Field f = new Field(colname);
	        f.setKeyable(true);
	        table.addField(f);
	        dbWizard.addPrimaryKey(table);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        PubFunc.closeResource(rs);
	    }
	    
	}
}
