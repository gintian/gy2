package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.utils.SqlDifference;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 保存唯一性
 * <p>
 * Title:SaveHrSyncOnlyFiled.java
 * </p>
 * <p>
 * Description>:SaveHrSyncOnlyFiled.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Jan 28, 2010 2:24:36 PM
 * </p>
 * <p>
 * 
 * @version: 4.0
 *           </p>
 *           <p>
 * @author: s.xin
 */
public class SaveHrSyncOnlyFiled extends IBusiness {

	public void execute() throws GeneralException {
		String onlyfield = (String) this.getFormHM().get("onlyfield");
		if (onlyfield == null || onlyfield.length() <= 0
				|| "#".equals(onlyfield))
			onlyfield = "";
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String sync_mode = hsb.getAttributeValue(HrSyncBo.SYNC_MODE);
		if ("trigger".equalsIgnoreCase(sync_mode)) {
			isOnlyToField(onlyfield);
		}
		hsb.setAttributeValue(HrSyncBo.HR_ONLY_FIELD, onlyfield);
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			hsb.saveParameter(dao);
			this.getFormHM().put("types", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("types", "");
		}
		ArrayList onlylist = hsb.getFields(onlyfield);
		String onlyfieldstr = hsb.getMess(onlylist);
		this.getFormHM().put("onlyfieldstr", onlyfieldstr);
	}

	private void isOnlyToField(String onlyfield) throws GeneralException {
		if (onlyfield == null || onlyfield.length() < 1) {
			return;
		}
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
		if (dbnamestr == null || dbnamestr.length() < 1) {
			return;
		}
		String[] dbnames = dbnamestr.split(",");
		StringBuffer sqlinner = new StringBuffer();
		StringBuffer table = new StringBuffer();
		for (int i = 0; i < dbnames.length; i++) {
			table.append(dbnames[i] + "A01,");
			sqlinner.append("SELECT " + onlyfield + " FROM " + dbnames[i]
					+ "A01 WHERE ");
			sqlinner.append(SqlDifference.isNotNull(onlyfield));
			sqlinner.append(" UNION ALL ");
		}
		table.deleteCharAt(table.length() - 1);
		sqlinner.delete(sqlinner.length() - 10, sqlinner.length() - 1);
		String sql = "SELECT " + onlyfield + " FROM (" + sqlinner.toString()
				+ ") A GROUP BY " + onlyfield + " HAVING COUNT(" + onlyfield
				+ ") > 1";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				throw new GeneralException("设置的唯一字段在表" + table.toString()
						+ "中有的值不唯一。");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

}
