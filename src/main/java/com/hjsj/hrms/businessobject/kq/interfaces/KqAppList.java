/**
 * 
 */
package com.hjsj.hrms.businessobject.kq.interfaces;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>
 * Title:KqAppList
 * </p>
 * <p>
 * Description:查询请假、加班、公出列表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-26
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class KqAppList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 获得已报批的公出信息
	 * @return
	 */
	public ArrayList getQ13List(String a0100, String nbase) {
		return getList("q13",a0100, nbase);
	}
	
	/**
	 * 获得已报批的加班信息
	 * @return
	 */
	public ArrayList getQ11List(String a0100, String nbase) {
		return getList("q11", a0100, nbase);
	}
	
	/**
	 * 获得已报批的请假信息
	 * @return
	 */
	public ArrayList getQ15List(String a0100, String nbase) {
		return getList("q15", a0100, nbase);
	}
	
	/**
	 * 获得报批的所有申请
	 * @return List<RecordVo>
	 */
	private ArrayList getList(String table, String a0100, String nbase) {
		if (a0100 == null || a0100.length() == 0) {
			return new ArrayList();
		}
		if (nbase == null || nbase.length() == 0) {
			return new ArrayList();
		}
		table = table.toLowerCase();
		String field = getField();
		if (field == null || field.length() == 0) {
			return  new ArrayList();
		}
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append(table);
		sql.append("01,nbase,a0100,a0101,");
		sql.append(table);
		sql.append("05,");
		sql.append(table);
		sql.append("07 from ");
		sql.append(table);
		sql.append(" where (");
		sql.append(table);
		sql.append("z5='02' or ");
		sql.append(table);
		sql.append("z5='08') and e01a1 in (");
		sql.append("select e01a1 from k01 where ");
		sql.append(field);
		sql.append(" in (select e01a1 from ");
		sql.append(nbase);
		sql.append("a01 where a0100='");
		sql.append(a0100);
		sql.append("'))");
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			while (rs.next()) {
				RecordVo vo = new RecordVo(table);
				vo.setString(table + "01", rs.getString(table + "01"));
				vo.setString("nbase", rs.getString("nbase"));
				vo.setString("a0100", rs.getString("a0100"));
				vo.setString("a0101", rs.getString("a0101"));
				vo.setString(table + "05", rs.getString(table + "05"));
				vo.setString(table + "07", rs.getString(table + "07"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private String getField() {
		String field = "";
		String sql = "select str_value from constant where constant='PS_SUPERIOR'";
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				field = rs.getString("str_value");
			}
		} catch (Exception e) {
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
				e.printStackTrace();
			}
		}
		
		return field;
	}
}
