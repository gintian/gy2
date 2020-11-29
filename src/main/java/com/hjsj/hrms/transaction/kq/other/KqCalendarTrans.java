package com.hjsj.hrms.transaction.kq.other;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 企业考勤日历
 * 
 * @author Owner
 * 
 */
public class KqCalendarTrans extends IBusiness {

	public void execute() throws GeneralException {
		ManagePrivCode managePrivCode = new ManagePrivCode(userView, this
				.getFrameconn());
		String userOrgId = managePrivCode.getPrivOrgId();
		// String weeks=getWeekends(userOrgId);
		ArrayList restList = IfRestDate.search_RestOfWeek(userOrgId,
				this.userView, this.getFrameconn());
		String weeks = restList.get(0).toString();
		String orgid = restList.get(1).toString();
		weeks = KQRestOper.getRestStrTurn(weeks);
		weeks = weeks.replaceAll(",", "`");
		String feasts = getFeastDates();
		String turn_dates = getTurn_date(orgid);
		String week_dates = getWeek_Date(orgid);
		String kq_duration = getKqDuration();
		String strDate = RegisterDate.getDefaultDay(this.frameconn);
		this.getFormHM().put("kq_duration", kq_duration);
		this.getFormHM().put("weeks", weeks);
		this.getFormHM().put("feasts", feasts);
		this.getFormHM().put("turn_dates", turn_dates);
		this.getFormHM().put("week_dates", week_dates);
		this.getFormHM().put("easy_app_start_date", strDate);
	}

	public String getWeekends(String userOrgId) throws GeneralException {
		String rest_date = "";
		try {
			ArrayList restList = IfRestDate.search_RestOfWeek(userOrgId,
					this.userView, this.getFrameconn());
			rest_date = restList.get(0).toString();
			rest_date = KQRestOper.getRestStrTurn(rest_date);
			rest_date = rest_date.replaceAll(",", "`");

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return rest_date;
	}

	public String getFeastDates() throws GeneralException {

		StringBuffer feast_strs = new StringBuffer();
		RowSet rowSet = null;

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT feast_name,feast_dates from kq_feast ");
		try {
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				String feast_name = rowSet.getString("feast_name");
				String feast_dates = rowSet.getString("feast_dates");
				if (feast_dates == null || feast_dates.length() <= 0)
					continue;
				int lr = feast_dates.lastIndexOf(",");
				if (lr != feast_dates.length()) {
					feast_dates = feast_dates + ",";
				}
				feast_strs.append(feast_dates);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rowSet != null) {
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String strs = feast_strs.toString().replaceAll(",", "`");

		return strs;
	}

	public String getTurn_date(String userOrgId) throws GeneralException {

		StringBuffer turn_date = new StringBuffer();
		StringBuffer dateSQL = new StringBuffer();

		dateSQL.append("SELECT ");
		dateSQL.append(" b0110,turn_date");
		dateSQL.append(" from kq_turn_rest ");
		dateSQL.append(" where b0110='" + userOrgId + "'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet = null;
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
		try {
			rowSet = dao.search(dateSQL.toString());
			while (rowSet.next()) {

				Date d1 = rowSet.getDate("turn_date");
				if (d1 == null)
					continue;
				turn_date.append(format1.format(d1) + "`");
			}
			if((turn_date == null || turn_date.length() <= 0) && !"UN".equals(userOrgId))
			{
				dateSQL.setLength(0);
				dateSQL.append("select parentid");
				dateSQL.append(" from organization");
				dateSQL.append(" where codeitemid = '" + userOrgId + "'");
				rowSet = dao.search(dateSQL.toString());
				if (rowSet.next()) 
				{
					String parent = rowSet.getString(1);
					return getTurn_date(parent);
				}else 
				{
					return getTurn_date("UN");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rowSet != null) {
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return turn_date.toString();
	}

	public String getWeek_Date(String userOrgId) throws GeneralException {

		StringBuffer week_date = new StringBuffer();
		StringBuffer dateSQL = new StringBuffer();
		RowSet rowSet = null;
		try {
			dateSQL.append("SELECT ");
			dateSQL.append(" b0110,week_date");
			dateSQL.append(" from kq_turn_rest ");
			dateSQL.append(" where b0110='" + userOrgId + "'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			rowSet = dao.search(dateSQL.toString());
			while (rowSet.next()) {

				Date d1 = rowSet.getDate("week_date");
				if (d1 == null)
					continue;
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				week_date.append(format1.format(d1) + "`");
			}
			if((week_date == null || week_date.length() <= 0) && !"UN".equals(userOrgId))
			{
				dateSQL.setLength(0);
				dateSQL.append("select parentid");
				dateSQL.append(" from organization");
				dateSQL.append(" where codeitemid = '" + userOrgId + "'");
				rowSet = dao.search(dateSQL.toString());
				if (rowSet.next()) 
				{
					String parent = rowSet.getString(1);
					return getWeek_Date(parent);
				}else 
				{
					return getWeek_Date("UN");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rowSet != null) {
				try {
					rowSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return week_date.toString();
	}
	
	private String getKqDuration(){
		ArrayList dayList = RegisterDate.getKqDayList(frameconn);
		String returnStr = "";
		if (dayList != null && dayList.size() >= 2) {
			returnStr = dayList.get(0) + "`" + dayList.get(1);
		}
		return returnStr;
	}
}
