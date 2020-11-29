package com.hjsj.hrms.businessobject.attestation.mobile;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserPhone {

	public UserPhone() {
		
	}
	public String getPhone(String username) {
		Connection conn = null;
		ResultSet rs = null;
		String phone = "";
		PreparedStatement psmt = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select phone from operuser where username=?";
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, username);
			rs = psmt.executeQuery();
			if (rs.next()) {
				phone = rs.getString("phone");
				if (phone == null) {
					phone = "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(psmt);
			PubFunc.closeResource(conn);
		}
		return phone;
	}
}
