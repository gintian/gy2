package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TransTool implements IConstant {

	/*
	 * 通用执行sql方法， 支持 删除、插入语句。
	 */
	public static  boolean executeSql(String strSql) {
		boolean ret = false;
		if (strSql == null || strSql.trim().length() < 1) {
			return ret;
		}
		Connection conn = null;
		
		try {

			conn = AdminDb.getConnection();
			// ContentDAO dao = new ContentDAO(conn);
			// ret = dao.delete()?insert()?update()?
			//System.out.println(strSql);
			 ContentDAO dao  = new ContentDAO(conn);
	         dao.update(strSql);
	         ret=true;


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeAll(null, null, conn);
		}
		// System.out.println("执行完毕!");
		return ret;
	}

	/*
	 * 通用查询语句 使用了DynaBean 、LazyDynaBean 返回填充了LazyDynaBean类型的ArrayList
	 */
	public static ArrayList executeQuerySql(String strQuerySql) {
		ArrayList alResult = null;
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			alResult = executeQuerySql(strQuerySql, conn);
		} catch (Exception e) {

		} finally {
			closeAll(null, null, conn);
		}
		return alResult;
	}

	/**
	 * 该方法提供给非trans类调用，需要从AdminDb获取连接
	 * 
	 * @param strQuerySql
	 * @param conn
	 * @return DynaBean 的ArrayList集合
	 * @throws SQLException
	 */
	public static ArrayList executeQuerySql(String strQuerySql, Connection conn)
			throws SQLException {

		ArrayList arrayList = new ArrayList();
		if (strQuerySql == null || "".equals(strQuerySql))
			return arrayList;
		ResultSet rs = null;
		try {
			ContentDAO dao  = new ContentDAO(conn);
	         rs = dao.search(strQuerySql);

			// 获取关于 ResultSet 对象中列的类型和属性信息的对象的列数
			int iCol = rs.getMetaData().getColumnCount();

			String strColNames[] = new String[iCol];
			String strColClassNames[] = new String[iCol];

			for (int i = 0; i < iCol; i++) {

				// java.sql.ResultSetMetaData的bug！！！表列下标从[1]开始（获取指定列的名称）
				strColNames[i] = rs.getMetaData().getColumnName(i + 1);

				// 列表类型目前暂时不使用，默认所有字段都可以转换为String
				// 如果需要扩展时再进行严格的类型检验！！！
				// 列中检索值，则返回构造其实例的 Java 类的完全限定名称。
				strColClassNames[i] = rs.getMetaData().getColumnClassName(
						i + 1);
			}

			String strValue = null;
			while (rs.next()) {
				// 动态Bean封装每条预警记录
				DynaBean dbean = new LazyDynaBean();
				for (int i = 0; i < strColNames.length; i++) {
					// 预警控制字段（XML形式）数据类型为(sql中为text)大字段类型					
					if ("warn_ctrl".equalsIgnoreCase(strColNames[i])) {
						strValue = Sql_switcher.readMemo(rs, "warn_ctrl");
					}else if("csource".equalsIgnoreCase(strColNames[i]))
					{
						strValue = Sql_switcher.readMemo(rs, "csource");
					}else {
						strValue = rs.getString(strColNames[i]);
						if (strValue == null || strValue.trim().length() < 1
								|| "null".equals(strValue.trim().toLowerCase())) {
							strValue = "";
						}
					}
					// 填充动态bean (名称全部为小写)
					//System.out.println(strColNames[i]+"----"+strValue);
					dbean.set(strColNames[i].toLowerCase(), strValue);
				}
				arrayList.add(dbean);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			closeAll(rs, null, null);// conn); // 将连接交由conn申请者关闭
		}
		return arrayList;
	}

	private static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				;
			}
			rs = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				;
			}
			stmt = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				;
			}
			conn = null;
		}
	}

	/**
	 * 根据表名、主键名，生成“最大值加1主键” 注意：如果trans不是线程安全的话，需要同步机制。
	 * 否则，针对同一个表在“同一瞬间”调用该方法时，会得到相同的ID
	 * 
	 * @param strTableName
	 * @param strPKFieldName
	 * @return
	 */
	public static int getNextId(String strTableName, String strPKFieldName) {
		if (strTableName == null || strTableName.trim().length() < 1
				|| strPKFieldName == null || strPKFieldName.trim().length() < 1) {
			return -1;
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String strOrgSql = "select max(" + strPKFieldName + ") as maxid from "
				+ strTableName;
		int iDefault = 1;
		try {
			conn = AdminDb.getConnection();
			ContentDAO db = new ContentDAO(conn);
			rs = db.search(strOrgSql);
			if (rs.next()) {
				int iId = rs.getInt("maxid");
				iDefault = iId + 1;
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null && (!conn.isClosed()))
					conn.close();
			} catch (SQLException sql) {
				sql.printStackTrace();
			}
		}
		return iDefault;
	}

	/*
	 * public static String getSelectResultSql(UserView userview, DynaBean
	 * dbean, String strSelectFlag){
	 * 
	 * String strSelectFields = null; if( strSelectFlag.equals("")){
	 * strSelectFields = "select u.A0100,u.B0110,o1.codeitemdesc as
	 * o1name,u.E0122,o2.codeitemdesc as o2name,u.E01A1,o3.codeitemdesc as
	 * o3name,u.A0101"; }else if( strSelectFlag.equals("")){ strSelectFields =
	 * "select count()"; } // sql语句原形： //select u.A0100,u.B0110,o1.codeitemdesc
	 * as o1name,u.E0122,o2.codeitemdesc as o2name,u.E01A1,o3.codeitemdesc as
	 * o3name,u.A0101 //from Otha01 as u left join organization as o1 on
	 * u.B0110=o1.codeitemid //left join organization as o2 on
	 * u.e0122=o2.codeitemid //left join organization as o3 on
	 * u.e01a1=o3.codeitemid //inner join hrpwarn_result as result on
	 * u.A0100=result.A0100 //where result.wid='13341' result.nbase ='Oth' and
	 * result.a0100 in ( privSql ) String strWid =
	 * (String)dbean.get(Key_HrpWarn_FieldName_ID); ArrayList alPre = null;
	 * 
	 * String strUserPre = userview.getDbname();
	 * if(strUserPre.equals("")||strUserPre.trim().length()<1){ alPre =
	 * DataDictionary.getDbpreList(); }else{ alPre = new ArrayList(); alPre.add(
	 * strUserPre ); } StringBuffer sbSelectResultSql = new StringBuffer(
	 * strSelectFiels );//"select u.A0100,u.B0110,o1.codeitemdesc as
	 * o1name,u.E0122,o2.codeitemdesc as o2name,u.E01A1,o3.codeitemdesc as
	 * o3name,u.A0101"); for(int i=0; i< alPre.size(); i++){
	 * //sbSelectResultSql.append( ",'"+ alPre.get(i)+"' as pre from " +
	 * alPre.get(i) + "a01 as u left join organization as o1 on
	 * u.B0110=o1.codeitemid"); sbSelectResultSql.append( " from " +
	 * alPre.get(i) + "a01 as u left join organization as o1 on
	 * u.B0110=o1.codeitemid"); sbSelectResultSql.append( " left join
	 * organization as o2 on u.e0122=o2.codeitemid"); sbSelectResultSql.append( "
	 * left join organization as o3 on u.e01a1=o3.codeitemid");
	 * 
	 * sbSelectResultSql.append( " inner join hrpwarn_result as result on
	 * u.A0100=result.A0100"); sbSelectResultSql.append( " where result.wid='" +
	 * strWid + "' and result.nbase='"+ alPre.get(i)+"' and result.a0100 in (");
	 * 
	 * sbSelectResultSql.append("select a0100 "); ConfigCtrlInfoVO ctrlVo =
	 * (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO); ArrayList fieldList =
	 * (ArrayList)dbean.get(Key_HrpWarn_Condition_FieldList); String
	 * strWhere=""; try { strWhere = userview.getPrivSQLExpression(
	 * ctrlVo.getStrSimpleExpress(),userview.getDbname(),false, true,fieldList); }
	 * catch (GeneralException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } sbSelectResultSql.append(strWhere);
	 * sbSelectResultSql.append(")");
	 * 
	 * if( i < alPre.size()-1 ){ sbSelectResultSql.append(" union ");
	 * sbSelectResultSql.append(strSelectFiels);//select
	 * u.A0100,u.B0110,o1.codeitemdesc as o1name,u.E0122,o2.codeitemdesc as
	 * o2name,u.E01A1,o3.codeitemdesc as o3name,u.A0101"); } } return
	 * sbSelectResultSql.toString(); }
	 */
}
