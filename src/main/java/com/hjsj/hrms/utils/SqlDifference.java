package com.hjsj.hrms.utils;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;

public class SqlDifference {
	public static String getJoinSymbol() {
		String symbol = "+";
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			symbol = "+";
			break;
		}
		case Constant.DB2: {
			symbol = "+";
			break;
		}
		case Constant.ORACEL: {
			symbol = "||";
			break;
		}
		}
		return symbol;
	}

	public static String getSqlYear(String fieldname) {
		StringBuffer sql = new StringBuffer();
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			sql.append("year(");
			sql.append(fieldname);
			sql.append(")");
			break;
		}
		case Constant.DB2: {
			sql.append("year(");
			sql.append(fieldname);
			sql.append(")");
			break;
		}
		case Constant.ORACEL: {
			sql.append("TO_NUMBER(TO_CHAR(");
			sql.append(fieldname);
			sql.append(",'YYYY'))");
			break;
		}
		}
		return sql.toString();
	}

	public static String getSqlMonth(String fieldname) {
		StringBuffer sql = new StringBuffer();
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			sql.append("MONTH(");
			sql.append(fieldname);
			sql.append(")");
			break;
		}
		case Constant.DB2: {
			sql.append("MONTH(");
			sql.append(fieldname);
			sql.append(")");
			break;
		}
		case Constant.ORACEL: {
			sql.append("TO_NUMBER(TO_CHAR(");
			sql.append(fieldname);
			sql.append(",'MM'))");
			break;
		}
		}
		return sql.toString();
	}

	public static String isNotNull(String fieldname) {
		StringBuffer sql = new StringBuffer();
		switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL: {
				sql.append("ISNULL(");
				sql.append(fieldname);
				sql.append(",'')<>''");
				break;
			}
			case Constant.DB2: {
				break;
			}
			case Constant.ORACEL: {
				sql.append(fieldname);
				sql.append(" IS NOT NULL");
				break;
			}
		}
		return sql.toString();
	}
	
	
	public static String isNull(String fieldname) {
		StringBuffer sql = new StringBuffer();
		switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL: {
				sql.append("ISNULL(");
				sql.append(fieldname);
				sql.append(",'')=''");
				break;
			}
			case Constant.DB2: {
				break;
			}
			case Constant.ORACEL: {
				sql.append(fieldname);
				sql.append(" IS NULL");
				break;
			}
		}
		return sql.toString();
	}
}
