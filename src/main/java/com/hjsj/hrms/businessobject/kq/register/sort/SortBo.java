package com.hjsj.hrms.businessobject.kq.register.sort;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;


/**
 * 考勤日明细排序业务类
 * <p>
 * Title:SortBo.java
 * </p>
 * <p>
 * Description>: 操作考勤排序的业务类
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2011-07-20 09:28:32
 * </p>
 * <p>
 * 
 * @version: 1.0
 *           </p>
 *           <p>
 * @author: wangzhongjun
 */
public class SortBo {
	// 数据库连接
	private Connection conn;
	
	// 当前用户对象
	private UserView userView;
	
	public SortBo() {
		
	}
	
	public SortBo(Connection conn) {
		this.conn = conn;
	}
	
	public SortBo(Connection conn, UserView userView) {
		this.userView = userView;
		this.conn = conn;
	}
	
	/**
	 * 查询考勤排序指标
	 * @return
	 */
	public String querrySort() {
		ContentDAO dao = new ContentDAO(this.conn);
		return KqParam.getInstance().getContent(dao, "KQ_ORDER_" + this.userView.getUserName().toUpperCase(), "UN", "");
	}
	
	public String getSortSql(String sort, String table) {
		return getSortSql(sort, table , true);
	}
	/**
	 * 获得排序的sql
	 * @param sort
	 * @return
	 */
	public String getSortSql(String sort, String table ,boolean isAsc) {
		if (sort == null || sort.length() <= 0) {
			return "";
		}
		
		sort = sort.replaceAll("`", ",");
		String []str = sort.split(",");
		StringBuffer order = new StringBuffer();
		order.append(" order by ");
		for (int i = 0; i < str.length; i++) {
			String []arr = str[i].split(":");
			if (table != null && table.length() > 0) {
				order.append(table);
				order.append(".");
			}
			order.append(arr[0]);
			order.append(" ");
			if (isAsc) {
				if ("0".equals(arr[2])) {
					order.append("desc");
				}
			} else {
				if (!"0".equals(arr[2])) {
					order.append("desc");
				}
			}
			order.append(",");
		}
		
		if (str.length > 0) {
			return order.substring(0,order.length() - 1);
		} else {
			return "";
		}
	}
	public String getSortSql(String sort) {
		return getSortSql(sort, "");
	}
	public String getSortSql() {
		return this.getSortSql(this.querrySort());
	}
	public String getSortSql(boolean isAsc) {
		return this.getSortSql(this.querrySort(),"",isAsc);
	}
	public String getSortSqlTable(String table) {
		return this.getSortSql(this.querrySort(),table,true);
	}
	public String getSortSqlTable(String table, boolean isAsc) {
		return getSortSql(this.querrySort(),table,isAsc);
	}
	
	/**
	 * 是否保存了排序
	 * @return
	 */
	public boolean isExistSort() {
		boolean flag = false;
		String sort = this.querrySort();
		if (sort != null && sort.length() > 0) {
			flag = true;
		}
		
		return flag;
		
	}
	
	/**
	 *  保存或更新排序
	 * @param sort
	 * @return
	 */
	public boolean saveOrUpdateSort(String sort) {
		return KqParam.getInstance().setContent(this.conn, "UN", 
		        "KQ_ORDER_" + this.userView.getUserName().toUpperCase(), 
		        sort, "考勤排序");
	}
	
}
