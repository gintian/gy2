package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:KqExceptionHint_ShouAn
 * </p>
 * <p>
 * Description:根据考勤规则向考勤数据异常的人员发送相关邮件的业务类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2013-06-08
 * </p>
 * 
 * @author hd
 * @version 1.0
 * 
 */
public class KqExceptionHint_ShouAn implements Job {

	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		sendLateMail();
		sendOnceMail();
		sendNullMail();
	}

	/**
	 * 按迟到早退情况向人员发送邮件
	 * 
	 * @param
	 * @param
	 */
	public static void sendLateMail() {

		Connection conn = null;
		RowSet rs = null;
		try {

			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String email = ConstantParamter.getEmailField().toLowerCase();
			RecordVo vo = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			String time = sdf.format(new Date());
			String nbase = "";
			String a0100 = "";
			String uname = "";
			String udate = "";
			String content = "";

			UserView userview = new UserView("su", conn);
			KqParameter para = new KqParameter(userview, "", conn);
			HashMap hashmap = para.getKqParamterMap();
			String kq_type = (String) hashmap.get("kq_type");// 考勤方式字段
			String sql = "select distinct t2.nbase,t2.A0100, t2.A0101,t2.Q03Z0 "
					+ "from kq_employ_shift t2,UsrA01 t3  "
					+ "where exists (select t.A0100   "
					+ "from kq_originality_data t   "
					+ "where work_time > '08:35' and work_time < '17:25'   "
					+ "and work_date  = '"
					+ time
					+ "' "
					+ "and t.A0100 = t2.A0100)  "
					+ "and t2.A0100 = t3.A0100  "
					+ "and t3."
					+ kq_type
					+ " = '02'  "
					+ "and t2.Q03Z0 = '"
					+ time + "' " + "and t2.class_id = '1'";
			rs = dao.search(sql);
			while (rs.next()) {
				nbase = rs.getString("nbase");
				a0100 = rs.getString("A0100");
				uname = rs.getString("A0101");
				udate = rs.getString("Q03Z0");
				content = uname
						+ "：\n您好！您在"
						+ udate
						+ "刷卡数据存在异常。\n异常原因：迟到早退。如您有加班换休，请于三个工作日内在OA上补填换休申请单，换休最小\n单位为1小时。若您还有疑问，请发送邮件至kaoqin@sureland.com进行咨询！祝工作顺利！\n\n人力资源部\n"
						+ udate;
				content = content.replaceAll("\r\n", "<br/>");
				content = content.replace("\r", "<br/>");
				content = content.replace("\n", "<br/>");
				vo = new RecordVo(nbase + "A01");
				vo.setString("a0100", a0100);
				if (!dao.isExistRecordVo(vo)) {
					continue;
				}

				String email_address = vo.getString(email);
				if (email_address != null && email_address.length() > 0) {
					try {
						EMailBo mailbo = new EMailBo(conn, true, nbase);
						mailbo.sendEmail("迟到早退", content, "", email_address);
					} catch (GeneralException e) {
						e.printStackTrace();
					}
				}

			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * 按只刷卡一次情况向人员发送邮件
	 * 
	 * @param
	 * @param
	 */
	public static void sendOnceMail() {

		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String email = ConstantParamter.getEmailField().toLowerCase();
			RecordVo vo = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			String time = sdf.format(new Date());
			String nbase = "";
			String a0100 = "";
			String uname = "";
			String udate = "";
			String content = "";
			String sql = "";

			UserView userview = new UserView("su", conn);
			KqParameter para = new KqParameter(userview, "", conn);
			HashMap hashmap = para.getKqParamterMap();
			String kq_type = (String) hashmap.get("kq_type");// 考勤方式字段
			sql = "select distinct t.nbase,t.A0100 ,t.A0101,t.Q03Z0 "
					+ "from kq_employ_shift t ,UsrA01 t5 where exists   "
					+ "(select A0100  from kq_originality_data t2  "
					+ "where t.Q03Z0 = '" + time + "' " + "and "
					+ "work_time >='00:00' and  work_time <='23:59' "
					+ "and work_date  = '" + time
					+ "' " + "and t2.A0100 = t.A0100 GROUP BY A0100   "
					+ "having COUNT(A0100) = 1)  " + "and t.A0100 = t5.A0100  "
					+ "and t5." + kq_type + " = '02' " + "and t.class_id = '1'";

			rs = dao.search(sql);
			while (rs.next()) {
				nbase = rs.getString("nbase");
				a0100 = rs.getString("A0100");
				uname = rs.getString("A0101");
				udate = rs.getString("Q03Z0");
				content = uname
						+ "：\n您好！您在"
						+ udate
						+ "刷卡数据存在异常。\n异常原因：只有一次刷卡记录且无请假、公出单据。如您是请假或出差，请于三个工作日内\n在OA上补填相应单据；如您忘记刷卡、工卡忘带或刷卡未读，请于三个工作日内补填考勤\n异常单，经领导签字后交到人力资源部备案。\n若您还有疑问，请发送邮件至kaoqin@sureland.com进行咨询！祝工作顺利！\n\n人力资源部\n"
						+ udate;
				content = content.replaceAll("\r\n", "<br/>");
				content = content.replace("\r", "<br/>");
				content = content.replace("\n", "<br/>");

				vo = new RecordVo(nbase + "A01");
				vo.setString("a0100", a0100);
				if (!dao.isExistRecordVo(vo)) {
					continue;
				}
				
				String email_address = vo.getString(email);
				if (email_address != null && email_address.length() > 0) {
					try {
						EMailBo mailbo = new EMailBo(conn, true, nbase);
						mailbo.sendEmail("只有一次刷卡记录", content, "", email_address);
					} catch (GeneralException e) {
						e.printStackTrace();
					}
				}

			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * 按无刷卡记录情况向人员发送邮件
	 * 
	 * @param
	 * @param
	 */
	public static void sendNullMail() {

		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String email = ConstantParamter.getEmailField().toLowerCase();
			RecordVo vo = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			String time = sdf.format(new Date());
			String timeFormat = time + " 12:00:00";
			String nbase = "";
			String a0100 = "";
			String uname = "";
			String udate = "";
			String content = "";
			String sql = "";

			UserView userview = new UserView("su", conn);
			KqParameter para = new KqParameter(userview, "", conn);
			HashMap hashmap = para.getKqParamterMap();
			String kq_type = (String) hashmap.get("kq_type");// 考勤方式字段
			sql = "select distinct t.nbase,t.A0100 ,t.A0101,t.Q03Z0 "
					+ "from kq_employ_shift t  ,UsrA01 t6 "
					+ "where not exists( " + "select A0100  "
					+ "from kq_originality_data t2 "
					+ "where t2.A0100 = t.A0100 and  " + "t2.work_date = '"
					+ time
					+ "') "
					+ "and not exists( "
					+ "select A0100 "
					+ "from Q11 t3 "
					+ "where t3.A0100 = t.A0100  "
					+ "and "
					+ Sql_switcher.dateValue(timeFormat)
					+ "  between  t3.Q11Z1 and t3.Q11Z3 )"
					+ "and not exists(select A0100 "
					+ "from Q13 t4 "
					+ "where  "
					+ Sql_switcher.dateValue(timeFormat)
					+ "  between  t4.Q13Z1 and t4.Q13Z3  "
					+ "and not exists (select tt.A0100 "
					+ "from Q13 tt "
					+ "where "
					+ Sql_switcher.dateValue(timeFormat)
					+ "  between  tt.Q13Z1 and tt.Q13Z3 "
					+ "and (tt.Q13Z5 = '07' or tt.Q13Z5 = '10') "
					+ "and t4.A0100 = tt.A0100) "
					+ "and t4.A0100 = t.A0100)  "
					+ "and not exists(select t5.A0100  "
					+ "from Q15 t5  "
					+ "where "
					+ Sql_switcher.dateValue(timeFormat)
					+ "  between t5.Q15Z1  and t5.Q15Z3 "
					+ "and not exists (select tt.A0100  "
					+ "from Q15 tt  "
					+ "where "
					+ Sql_switcher.dateValue(timeFormat)
					+ "  between  tt.Q15Z1  and tt.Q15Z3 "
					+ "and tt.Q1517 = '1' "
					+ "and tt.A0100 = t5.A0100) " 
					+ "and not exists (select tt.A0100  "
					+ "from Q15 tt  "
					+ "where "
					+ Sql_switcher.dateValue(timeFormat)
					+ "  between  tt.Q15Z1  and tt.Q15Z3 "
					+ "and tt.Q15Z5 = '07' "
					+ "and tt.A0100 = t5.A0100) " 
					+ "and t5.A0100 = t.A0100) "
					+ "and t.Q03Z0  = '"
					+ time
					+ "' "
					+ "and t.A0100 = t6.A0100 "
					+ "and t6."
					+ kq_type
					+ " = '02' " + "and t.class_id = '1'";

			rs = dao.search(sql);
			while (rs.next()) {
				nbase = rs.getString("nbase");
				a0100 = rs.getString("A0100");
				uname = rs.getString("A0101");
				udate = rs.getString("Q03Z0");
				content = uname
						+ "：\n您好！您在"
						+ udate
						+ "刷卡数据存在异常。\n异常原因：全天无刷卡记录且无请假、公出单据。如您是请假或出差，请于三个工作日内在\nOA上补填相应单据；如您忘记刷卡、工卡忘带或刷卡未读，请于三个工作日内补填考勤异\n常单，经领导签字后交到人力资源部备案。\n若您还有疑问，请发送邮件至kaoqin@sureland.com进行咨询！祝工作顺利！\n\n人力资源部\n"
						+ udate;
				content = content.replaceAll("\r\n", "<br/>");
				content = content.replace("\r", "<br/>");
				content = content.replace("\n", "<br/>");
				vo = new RecordVo(nbase + "A01");
				vo.setString("a0100", a0100);

				if (!dao.isExistRecordVo(vo)) {
					continue;
				}

				String email_address = vo.getString(email);
				if (email_address != null && email_address.length() > 0) {
					try {
						EMailBo mailbo = new EMailBo(conn, true, nbase);
						mailbo.sendEmail("全天无刷卡记录", content, "", email_address);
					} catch (GeneralException e) {
						e.printStackTrace();
					}
				}

			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
