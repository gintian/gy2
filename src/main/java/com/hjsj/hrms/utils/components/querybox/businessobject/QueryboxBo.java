package com.hjsj.hrms.utils.components.querybox.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询方案业务类
 * 
 * @author Administrator 2015-07-01 16:25:00
 * 
 */
public class QueryboxBo {
	// 用户
	private UserView userView;

	// 数据库连接
	private Connection conn;

	/**
	 * 构造空的构造方法
	 */
	public QueryboxBo() {

	}

	/**
	 * 初始化数据库连接以及用户
	 * 
	 * @param conn
	 * @param userView
	 */
	public QueryboxBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 按照所属模块id查询 方案列表
	 * 
	 * @param subm
	 * @return List<Map> key包括id,name,share,exp,cond
	 */
	public List queryPlanList(String subm) throws GeneralException {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			sql.append("select query_plan_id,plan_name,is_share,expression,conditem from t_sys_table_query_plan where submoduleid=? ");
			// 私有方案,可以删除
			sql.append(" and ((username=? and is_share=0) ");
			// 共享方案，不允许删除
			sql.append(" or is_share=1) order by Query_plan_id asc");

			// 准备查询参数
			list.add(subm);
			list.add(this.userView.getUserName());

			// 查询
			rs = dao.search(sql.toString(), list);

			// 清空list
			list.clear();

			// 组装数据
			while (rs.next()) {
				Map map = new HashMap();
				map.put("id", rs.getInt("query_plan_id"));
				map.put("name", rs.getString("plan_name"));
				map.put("share", rs.getString("is_share"));
				map.put("exp", rs.getString("expression"));
				map.put("cond", rs.getString("conditem"));

				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException(e.getMessage());
		} finally {
			PubFunc.closeResource(rs);
		}

		return list;
	}
	
	
	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
}
