/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * Title:SearchTrans
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Aug 10, 2006:9:37:18 AM
 * </p>
 * 
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class SearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		String info_flag = (String) this.getFormHM().get("info");
		String dbpre = (String) this.getFormHM().get("dbpre");
		ArrayList list = (ArrayList) this.getFormHM().get("objlist");
		if (list == null || list.size() == 0) {
			return;
		} else {
			String tabldName = "";
			if ("1".equals(info_flag)) { // 人员
				tabldName = userView.getUserName() + dbpre + "result";
			} else if ("2".equals(info_flag)) { // 单位
				tabldName = userView.getUserName() + "B" + "result";
			} else if ("3".equals(info_flag)) { // 部门
				tabldName = userView.getUserName() + "K" + "result";
			}
			//判断表是否存在,不存在则创建表
			this.createTable(this.getFrameconn(), tabldName, info_flag);
			//删除指定表中所有数据
			this.deleteDB(this.getFrameconn(), tabldName);
            StringBuffer buf = new StringBuffer("");
			for (int i = 0; i < list.size(); i++) {
				if ("1".equals(info_flag)) {
					// 人员
					String db = (String) list.get(i);
					db = db.substring(dbpre.length(), db.length());
					buf.append(" or a0100='"+db+"'");
					//String sql = "insert into " + tabldName+ " (A0100) values('" + db + "')";
					//this.insertDB(this.getFrameconn(), sql);
				} else if (info_flag.endsWith("2")) {
					// 单位
					String db = (String) list.get(i);
					buf.append(" or UPPER(codeitemid)='"+db.toUpperCase()+"'");
					//String sql = "insert into " + tabldName + " (B0110) values('"+ db + "')";
					//this.insertDB(this.getFrameconn(), sql);

				} else if ("3".equals(info_flag)) {
					// 职位
					String db = (String) list.get(i);
					buf.append(" or UPPER(codeitemid)='"+db.toUpperCase()+"'");
					//String sql = "insert into " + tabldName+ " (E01A1) values('" + db + "')";
					//this.insertDB(this.getFrameconn(), sql);
				}
			}
			if(buf.toString().length()>0)
			{
				String where=buf.toString().substring(3);
				String sql="";
				if ("1".equals(info_flag)) {
					sql = "insert into " + tabldName+ " (A0100) select a0100 from "+dbpre+"A01 where ("+where+")";
				}
				else if("3".equals(info_flag))
				{
					sql = "insert into " + tabldName + " (B0110) select codeitemid as B0110 from organization where ("+where+") and (UPPER(codesetid)='UN' or UPPER(codesetid)='UM')";
				}
				else if("3".equals(info_flag))
				{
					sql = "insert into " + tabldName + " (E01A1) select codeitemid as E01A1 from organization where ("+where+") and UPPER(codesetid)='@K' ";
				}
				try
				{
		    		ContentDAO dao = new ContentDAO(this.getFrameconn());
		    		dao.insert(sql, new ArrayList());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * 判断特定表是否存在，不存在则创建表
	 * 
	 * @param conn
	 *            DB连接
	 * @param tabldName
	 *            表名
	 * @param info_flag
	 *            标识
	 * @throws GeneralException
	 */
	public void createTable(Connection conn, String tabldName, String info_flag)
			throws GeneralException {
		Table table = new Table(tabldName);
		DbWizard dbWizard = new DbWizard(conn);
		if (!dbWizard.isExistTable(table)) {
			try {
				ArrayList fieldList = this.getTableFields(info_flag);
				for (Iterator t = fieldList.iterator(); t.hasNext();) {
					Field temp = (Field) t.next();
					table.addField(temp);
				}
				dbWizard.createTable(table);
			} catch (GeneralException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
	}

	/**
	 * 获得特定表的字段
	 * 
	 * @param info_flag
	 *            标识
	 * @return
	 */
	public ArrayList getTableFields(String info_flag) {
		ArrayList list = new ArrayList();
		if ("1".equals(info_flag)) {
			// 人员
			Field field = new Field("A0100", "A0100");
			field.setLength(8);
			field.setDatatype("DataType.STRING");

			Field field1 = new Field("B0110", "B0110");
			field1.setDatatype("DataType.STRING");
			field1.setLength(30);
			list.add(field);
			list.add(field1);

		} else if (info_flag.endsWith("2")) {
			// 单位
			/*
			 * if(userView.isSuper_admin()){ Field field = new
			 * Field("A0100","A0100"); field.setDatatype("DataType.STRING");
			 * field.setLength(8); list.add(field); }else{ Field field = new
			 * Field("A0100","A0100"); field.setDatatype("DataType.STRING");
			 * field.setLength(8);
			 */

			Field field1 = new Field("B0110", "B0110");
			field1.setDatatype("DataType.STRING");
			field1.setLength(30);
			list.add(field1);
			// }

		} else if ("3".equals(info_flag)) {
			// 职位
			Field field = new Field("E01A1", "E01A1");
			field.setDatatype("DataType.STRING");
			field.setLength(30);
			list.add(field);
		}
		return list;
	}

	/**
	 * 删除指定表的数据
	 * 
	 * @param conn
	 *            DB连接
	 * @param tabldName
	 *            特定表名
	 * @throws GeneralException
	 */
	public void deleteDB(Connection conn, String tabldName)
			throws GeneralException {
		String sql = "delete from " + tabldName;
		ContentDAO dao = new ContentDAO(conn);
		try {
			dao.delete(sql, new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public void insertDB(Connection conn, String sql) throws GeneralException {
		// System.out.println(sql);
		ContentDAO dao = new ContentDAO(conn);
		try {
			dao.insert(sql, new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 获得特定人员的单位编号
	 * 
	 * @param userID
	 *            人员编号
	 * @return
	 * @throws GeneralException
	 */
	public String getUnit(Connection conn, String userID)
			throws GeneralException {
		String sql = "select b0110 from usrA01 where a0100='" + userID + "'";
		String b0110 = "";
		ContentDAO dao = new ContentDAO(conn);
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				b0110 = this.frowset.getString("b0110");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return b0110;
	}

	public static void main(String[] args) {
		String db = "usr2222222";
		db = db.substring("usr".length(), db.length());
		System.out.println(db);
	}
}
