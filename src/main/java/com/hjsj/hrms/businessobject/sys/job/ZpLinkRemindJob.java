package com.hjsj.hrms.businessobject.sys.job;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 招聘环节到期提醒
 * @author pancs
 *
 */
public class ZpLinkRemindJob implements Job{

	private static final String String = null;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn=null;
		try {
			conn = (Connection) AdminDb.getConnection();
			List<List<Object>> values = new ArrayList<List<Object>>(); // 用于更新待办的容器

			// 拿到招聘未处理的待办
			ArrayList<LazyDynaBean> pendingTasks = getPendingTask(conn);
			String ids = "";// 流程id
			// 循环拼接流程的ids
			for (int i = 0; i < pendingTasks.size(); i++) {
				LazyDynaBean bean = pendingTasks.get(i);
				String str = (String) bean.get("ext_flag");
				String id = str.substring(str.lastIndexOf("_")+1);
				// 获取招聘的提醒时长
				ArrayList<LazyDynaBean> processRemind = getProcessRemindLength(conn, id);
				int remindHour = !"".equals(processRemind.get(0).get("remindHour"))?Integer.parseInt((String) processRemind.get(0).get("remindHour")):0;
				if (remindHour != 0) { // 0时未设置提醒时长
					int mostBigLength = getMostBigLength(conn, id);
					float hours = mostBigLength/60F;
					if (hours>remindHour) { // 需要设为紧急
						List<Object> list = new ArrayList<Object>();
						String pending_title = (String) bean.get("pending_title");
						if("(紧急)".equals(pending_title.substring(pending_title.length()-4))) {
							list.add(pending_title);
						}else {
							System.out.println(pending_title);
							list.add(pending_title+"(紧急)");
						}
						list.add((String) bean.get("pending_id"));
						values.add(list);
					}
				}
			}
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE t_hr_pendingtask SET pending_title = ?,lasttime = GETDATE() WHERE pending_id = ?");
			dao.batchUpdate(sql.toString(), values);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取处于本阶段时间最长的时间数（小时）
	 * @param conn
	 * @return int(分钟数)
	 * @throws SQLException
	 */
	private int getMostBigLength(Connection conn,String id) throws SQLException {
		// 用分钟数更加准确
		StringBuffer sql = new StringBuffer("SELECT TOP 1 DATEDIFF(mi,entertime, GETDATE()) AS bigtime FROM zp_pos_tache WHERE Link_id = ");
		sql.append(id);
		sql.append(" ORDER BY bigtime DESC");
		ContentDAO dao = new ContentDAO(conn);
		RowSet search = dao.search(sql.toString());
		String bigtime = "";
		if(search.next()) {
			bigtime = search.getString("bigtime");
		}
		if("".equals(bigtime)) {
			System.out.println("有候选人没有进入流程时间");
			return -1;
		}

		return Integer.parseInt(bigtime);
	}

	/**
	 * 获取招聘未处理的待办
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private ArrayList<LazyDynaBean> getPendingTask(Connection conn) throws SQLException {
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		StringBuffer sql = new StringBuffer("SELECT pending_id,pending_title,ext_flag FROM t_hr_pendingtask WHERE pending_type = 32 AND pending_status = 0");
		ContentDAO dao = new ContentDAO(conn);
		RowSet search = dao.search(sql.toString());
		while (search.next()) {
			LazyDynaBean bean = new LazyDynaBean();
			String pending_id = search.getString("pending_id");
			bean.set("pending_id", pending_id);
			String pending_title = search.getString("pending_title");
			bean.set("pending_title", pending_title);
			String ext_flag = search.getString("ext_flag");
			bean.set("ext_flag", ext_flag);
			list.add(bean);
		}
		return list;
	}

	/**
	 * 获取流程提醒时长
	 * @param conn
	 * @param ids
	 * @throws SQLException
	 */
	private ArrayList<LazyDynaBean> getProcessRemindLength(Connection conn,String ids) throws SQLException {
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		StringBuffer sql = new StringBuffer("SELECT id,remindHour FROM zp_flow_links WHERE id in(");
		String[] idArray = StringUtils.split(ids, ",");
		for (String string : idArray) {
			sql.append(string);
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		ContentDAO dao = new ContentDAO(conn);
		RowSet search = dao.search(sql.toString());
		while(search.next()) {
			LazyDynaBean bean = new LazyDynaBean();
			String id = search.getString("id");
			bean.set("id", id);
			String remindHour = search.getString("remindHour")==null?"":search.getString("remindHour");
			bean.set("remindHour", remindHour);
			list.add(bean);
		}
		return list;
	}

}
