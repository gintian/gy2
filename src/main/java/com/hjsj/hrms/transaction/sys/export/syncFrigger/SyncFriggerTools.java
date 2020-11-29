package com.hjsj.hrms.transaction.sys.export.syncFrigger;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * <p>
 * Title:SyncFriggerTools.java
 * </p>
 * <p>
 * Description>:SyncFriggerTools.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Jan 14, 2011 9:56:21 AM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: 郑文龙
 */
public class SyncFriggerTools {

	public static final int M_MARK = 1;

	public static final int O_MARK = 2;

	/**
	 * Oracle
	 * 
	 * @param whereSql
	 * @param table
	 * @param columns
	 * @param tranfields
	 * @param conn
	 * @return
	 */
	public static String UpData(int t_flag, List columns, List tranfields,
			String rowType, String whereSql, int dbMark, Connection conn) {
		if (columns == null || columns.isEmpty())
			return "";
		StringBuffer frigger = new StringBuffer();
		HrSyncBo hsb = new HrSyncBo(conn);
		if (t_flag == HrSyncBo.A) {
			frigger.append("UPDATE t_hr_view SET ");
		} else if (t_flag == HrSyncBo.B) {
			frigger.append("UPDATE t_org_view SET ");
		} else if (t_flag == HrSyncBo.K) {
			frigger.append("UPDATE t_post_view SET ");
		}

		Iterator it = columns.iterator();
		if (dbMark == O_MARK) {
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					String curColumn = hsb.getAppAttributeValue(t_flag, column);
					if (tranfields != null && tranfields.indexOf(column) != -1) {
						frigger.append(curColumn + "=" + column + "Desc,");
					} else {
						frigger.append(curColumn + "=" + rowType + "." + column
								+ ",");
					}
				}
			}
		} else if (dbMark == M_MARK) {
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					String curColumn = hsb.getAppAttributeValue(t_flag, column);
					if (tranfields != null && tranfields.indexOf(column) != -1) {
						frigger.append(curColumn + "=@" + column + "Desc,");
					} else {
						frigger.append(curColumn + "=" + rowType + "." + column
								+ ",");
					}
				}
			}
		}
		frigger.deleteCharAt(frigger.length() - 1);
		frigger.append(" " + whereSql);
		return frigger.toString();
	}

	/**
	 * 对 表t_hr_view t_org_view t_post_view 的 字符赋NULL
	 * 
	 * @param table
	 * @param columns
	 * @param whereSql
	 * @return
	 */
	public static String emptySubRecord(String table, List columns,
			String whereSql, Connection conn, int t_flag) {
		if (columns == null)
			return "";
		HrSyncBo hsb = new HrSyncBo(conn);

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE " + table + " SET ");
		Iterator it = columns.iterator();
		while (it.hasNext()) {
			String column = (String) it.next();
			String curColumn = hsb.getAppAttributeValue(t_flag, column);
			if (curColumn != null && curColumn.length() > 0) {
				sql.append(curColumn + "=NULL,");
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" " + whereSql);
		return sql.toString();
	}

	/**
	 * 获得最大子集字段
	 * 
	 * @param subTable
	 * @param columns
	 * @param rowType
	 * @param whereSql
	 * @param dbMark
	 * @return
	 */
	public static String getMaxSubRecord(String subTable, List columns,
			String rowType, String whereSql, int dbMark) {
		StringBuffer sql = new StringBuffer();
		StringBuffer into = new StringBuffer();
		if (columns == null)
			return "";
		sql.append("SELECT ");
		Iterator it = columns.iterator();
		while (it.hasNext()) {
			String column = (String) it.next();
			if (column != null && column.length() > 0) {
				switch (dbMark) {
				case O_MARK:
					sql.append(column + ",");
					into.append(rowType + "." + column + ",");
					break;
				case M_MARK:
					sql.append("@" + column + "ID=" + column + ",");
					break;
				}
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		switch (dbMark) {
		case O_MARK:
			into.deleteCharAt(into.length() - 1);
			sql.append(" INTO " + into);
			break;
		}
		sql.append(" FROM " + subTable);
		sql.append(" " + whereSql);
		return sql.toString();
	}

	public static String getDbName(String nbaseName, String dbName, int dbMark) {
		String sql = "";
		switch (dbMark) {
		case O_MARK:
			sql = "SELECT dbname INTO " + nbaseName
					+ " FROM dbname WHERE UPPER(pre)='" + dbName.toUpperCase()
					+ "'";
			break;
		case M_MARK:
			sql = "select " + nbaseName
					+ "=dbname FROM dbname WHERE UPPER(pre)='"
					+ dbName.toUpperCase() + "'";
			break;
		}

		return sql;
	}

	/**
	 * 查询职位 部门SQL 语句
	 * 
	 * @param E01A1Desc
	 * @param fieldValue
	 * @param uk
	 * @param dbMark
	 * @return
	 */
	public static String getORGDesc(String E01A1Desc, String fieldValue,
			String uk, int dbMark) {
		String sql = "";
		switch (dbMark) {
		case O_MARK:
			sql = "SELECT codeitemdesc INTO " + E01A1Desc
					+ " FROM organization WHERE CODESETID='" + uk
					+ "' AND CODEITEMID=" + fieldValue;
			break;
		case M_MARK:
			sql = "SELECT  " + E01A1Desc
					+ " =codeitemdesc FROM organization WHERE CODESETID='" + uk
					+ "' AND CODEITEMID=" + fieldValue;
			break;
		}
		return sql;
	}

	/**
	 * 获得要触发的字段字符串 返回样式 字段名,字段名
	 * 
	 * @param columns
	 * @return
	 */
	public static String toFieldFrigger(List columns, int dbMark) {
		if (columns == null || columns.isEmpty()) {
			return "";
		}
		StringBuffer frigger = new StringBuffer();
		Iterator it = columns.iterator();
		if (dbMark == M_MARK) {
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0)

					frigger.append("UPDATE(" + column + ") OR ");// -----针对的字段}
			}
			frigger.delete(frigger.length() - 4, frigger.length());
		} else if (dbMark == O_MARK) {
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0)

					frigger.append(column + ",");// -----针对的字段}
			}
			frigger.deleteCharAt(frigger.length() - 1);
		}
		return frigger.toString();
	}

	public static String isExistsToRecord(String outVar, String table,
			String whereSql, int dbMark) {
		String sql = "";
		switch (dbMark) {
		case O_MARK:
			sql = "SELECT COUNT(1) INTO " + outVar + " FROM " + table + " "
					+ whereSql;
			break;
		case M_MARK:
			sql = "SELECT " + outVar + " = COUNT(1) FROM " + table + " "
					+ whereSql;
			break;
		}
		return sql;
	}

	public static String getStr(String prefix, List columns) {
		StringBuffer sql = new StringBuffer();
		Iterator it = columns.iterator();
		if (prefix != null && prefix.length() > 0) {
			prefix += ".";
		} else {
			prefix = "";
		}
		while (it.hasNext()) {
			String column = (String) it.next();
			if (column != null && column.length() > 0) {
				sql.append(prefix + column + ",");
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		return sql.toString();
	}

	public static String insertA01(String dbName, List columns,
			List tranfields, Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		StringBuffer frigger = new StringBuffer();
		StringBuffer sql_into = new StringBuffer();
		StringBuffer sql_value = new StringBuffer();
		DbNameBo dbbo = new DbNameBo(conn);
		String username = dbbo.getLogonUserNameField().toUpperCase();
		String password = dbbo.getLogonPassWordField().toUpperCase();
		sql_into
				.append("A0000,unique_id,UserName,UserPassword,A0100,nbase_0,B0110_0,E01A1_0,E0122_0,nbase,A0101,");
		sql_value
				.append("empRow.A0000,syncKey,empRow."
						+ username
						+ ",empRow."
						+ password
						+ ",empRow.A0100,'"
						+ dbName
						+ "',empRow.B0110,empRow.E01A1,empRow.E0122,nbaseName,empRow.A0101,");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.A, column);
				if (CusColumn != null && "A0101".equalsIgnoreCase(CusColumn)) {

				} else if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						sql_into.append(CusColumn + ",");
						sql_value.append(column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						sql_into.append(CusColumn + ",");
						sql_value.append("empRow." + column + ",");
					}
				}
			}
		}
		sql_into.deleteCharAt(sql_into.length() - 1);
		sql_value.deleteCharAt(sql_value.length() - 1);
		frigger.append("INSERT INTO t_hr_view(" + sql_into + ") ");
		frigger.append("VALUES( " + sql_value + " )");
		return frigger.toString();
	}

	public static String insertA01toM(String dbName, List columns,
			List tranfields, Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		StringBuffer frigger = new StringBuffer();
		StringBuffer sql_into = new StringBuffer();
		StringBuffer sql_value = new StringBuffer();
		DbNameBo dbbo = new DbNameBo(conn);
		String username = dbbo.getLogonUserNameField();
		String password = dbbo.getLogonPassWordField();

		sql_into.append("A0000,unique_id,UserName,UserPassword,A0100,nbase_0,B0110_0,E01A1_0,E0122_0,nbase,A0101,");
		sql_value.append("A0000,@syncKey," + username + "," + password + ",A0100,'"
				+ dbName + "',B0110,E01A1,E0122,@nbaseName,A0101,");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.A, column);
				if (CusColumn != null && "A0101".equalsIgnoreCase(CusColumn)) {

				} else if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						sql_into.append(CusColumn + ",");
						sql_value.append("@" + column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						sql_into.append(CusColumn + ",");
						sql_value.append(column + ",");
					}
				}
			}
		}
		sql_into.deleteCharAt(sql_into.length() - 1);
		sql_value.deleteCharAt(sql_value.length() - 1);
		frigger.append("INSERT INTO t_hr_view(" + sql_into + ") ");
		frigger.append("SELECT " + sql_value + " FROM  " + dbName
				+ "A01 WHERE A0100=@A0100");
		return frigger.toString();
	}

	public static String updateA01(String dbName, List columns,
			List tranfields, String whereSql, Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		DbNameBo dbbo = new DbNameBo(conn);
		String username = dbbo.getLogonUserNameField().toUpperCase();
		String password = dbbo.getLogonPassWordField().toUpperCase();
		StringBuffer frigger = new StringBuffer();
		frigger.append("UPDATE t_hr_view SET ");
		frigger.append("A0100=empRow.A0100,nbase_0='"
						+ dbName
						+ "',UserName=empRow." + username + ",UserPassword=empRow." + password + ",B0110_0=empRow.B0110,E01A1_0=empRow.E01A1,E0122_0=empRow.E0122,nbase=nbaseName,A0101=empRow.A0101,A0000=empRow.A0000,");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.A, column);
				if (CusColumn != null && "A0101".equalsIgnoreCase(CusColumn)) {

				} else if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=" + column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=empRow." + column + ",");
					}
				}
			}
		}
		frigger.deleteCharAt(frigger.length() - 1);
		frigger.append(" " + whereSql);
		return frigger.toString();
	}

	public static String updateA01toM(String dbName, List columns,
			List tranfields, String whereSql, Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		StringBuffer frigger = new StringBuffer();
		DbNameBo dbbo = new DbNameBo(conn);
		String username = dbbo.getLogonUserNameField().toUpperCase();
		String password = dbbo.getLogonPassWordField().toUpperCase();
		frigger.append("UPDATE t_hr_view SET ");
		frigger
				.append("A0100=T.A0100,nbase_0='"
						+ dbName
						+ "',UserName=T." + username + ",UserPassword=T." + password + ",B0110_0=T.B0110,E01A1_0=T.E01A1,E0122_0=T.E0122,nbase=@nbaseName,A0101=T.A0101,A0000=T.A0000,");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.A, column);
				if (CusColumn != null && "A0101".equalsIgnoreCase(CusColumn)) {

				} else if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=@" + column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=T." + column + ",");
					}
				}
			}
		}
		frigger.deleteCharAt(frigger.length() - 1);
		frigger.append(" " + whereSql);
		return frigger.toString();
	}

	public static String updateB01(List columns, List tranfields,
			String whereSql, Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		StringBuffer frigger = new StringBuffer();
		frigger.append("UPDATE t_org_view SET ");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.B, column);
				if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=" + column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=B01Row." + column + ",");
					}
				}
			}
		}
		frigger.deleteCharAt(frigger.length() - 1);
		frigger.append(" " + whereSql);
		return frigger.toString();
	}

	public static String updateB01toM(List columns, List tranfields,
			Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		StringBuffer frigger = new StringBuffer();
		frigger.append("UPDATE t_org_view SET ");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.B, column);
				if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=@" + column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=B01." + column + ",");
					}
				}
			}
		}
		frigger.deleteCharAt(frigger.length() - 1);
		String whereSql = "FROM t_org_view INNER JOIN B01 ON t_org_view.B0110_0=B01.B0110 ";
		whereSql += "WHERE t_org_view.UNIQUE_ID = @syncKey";
		frigger.append(" " + whereSql);
		return frigger.toString();
	}

	public static String updateK01(List columns, List tranfields,
			String whereSql, Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		StringBuffer frigger = new StringBuffer();
		frigger.append("UPDATE t_post_view SET ");
		frigger.append("E0122_0=K01Row.E0122,");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.K, column);
				if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=" + column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=K01Row." + column + ",");
					}
				}
			}
		}
		frigger.deleteCharAt(frigger.length() - 1);
		frigger.append(" " + whereSql);
		return frigger.toString();
	}

	public static String updateK01toM(List columns, List tranfields,
			Connection conn) {
		HrSyncBo hsb = new HrSyncBo(conn);
		StringBuffer frigger = new StringBuffer();
		frigger.append("UPDATE t_post_view SET E0122_0=@E0122,");
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				String CusColumn = hsb.getAppAttributeValue(HrSyncBo.K, column);
				if (tranfields != null
						&& tranfields.indexOf(column) != -1) {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=@" + column + "Desc,");
					}
				} else {
					if (CusColumn != null && CusColumn.length() > 0) {
						frigger.append(CusColumn + "=K01." + column + ",");
					}
				}
			}
		}
		frigger.deleteCharAt(frigger.length() - 1);
		String whereSql = "FROM t_post_view INNER JOIN K01 ON t_post_view.E01A1_0=K01.E01A1 ";
		whereSql += "WHERE t_post_view.UNIQUE_ID = @syncKey";
		frigger.append(" " + whereSql);
		return frigger.toString();
	}

	public static String defCodeVar(List list, int dbMark) {
		if (list == null || list.isEmpty())
			return "";
		StringBuffer frigger = new StringBuffer();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			String codeID = (String) it.next();
			if (codeID != null && codeID.length() > 0) {
				switch (dbMark) {
				case O_MARK:
					if ("E0122".equalsIgnoreCase(codeID) || "B0110".equalsIgnoreCase(codeID)) {
						// bug 33981 wangb add goracle 10g 触发器 声明参数 分号后面不允许有空格符号
						frigger.append("  " + codeID + "Desc varchar2(1000);\n");
					}else{
						// bug 33981 wangb add goracle 10g 触发器 声明参数 分号后面不允许有空格符号
						frigger.append("  " + codeID + "Desc varchar2(100);\n");
					}
					break;
				case M_MARK:
					if ("E0122".equalsIgnoreCase(codeID) || "B0110".equalsIgnoreCase(codeID)) {
						frigger.append("  @" + codeID + "Desc varchar(1000),\n");
					} else {
						frigger.append("  @" + codeID + "Desc varchar(100),\n");
					}
					break;
				}
			}
		}
		return frigger.toString();
	}
	
	public static String emptyAllRecord(String whereSql, Connection conn, int t_flag) {
		HrSyncBo hsb = new HrSyncBo(conn);
//		String allColumns = "";//获得已选的人员字段
		StringBuffer sql = new StringBuffer();
		switch (t_flag){
		case HrSyncBo.A :
//			allColumns = hsb.getTextValue(HrSyncBo.FIELDS);//获得已选的人员字段
			sql.append("UPDATE t_hr_view SET B0110_0=NULL,E0122_0=NULL,E01A1_0=NULL,");
			String onlyfield = hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
			if(onlyfield == null || onlyfield.length() < 1)
				sql.append("A0100=NULL,");
			break;
		case HrSyncBo.B :
//			allColumns = hsb.getTextValue(HrSyncBo.ORG_FIELDS);//获得已选的机构字段
			sql.append("UPDATE t_org_view SET B0110_0=NULL,PARENTID=NULL,");
			break;
		case HrSyncBo.K :
//			allColumns = hsb.getTextValue(HrSyncBo.POST_FIELDS);
			sql.append("UPDATE t_post_view SET E01A1_0=NULL,E0122_0=NULL,PARENTID=NULL,");
			break;
		default :
			return "";
		}
		sql.deleteCharAt(sql.length() -  1);
		sql.append(" " + whereSql);
		return sql.toString();
	}
}
