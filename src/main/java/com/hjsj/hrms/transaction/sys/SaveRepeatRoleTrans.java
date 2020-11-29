package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>Title: SaveRepeatRoleTrans</p>
 * <p> Description:查询用户名并保存</p>
 * <p>Company: hjsj</p>
 * <p>create time 2013-10-31 下午6:03:00</p>
 * 
 * @author yangj
 * @version 1.0
 */
public class SaveRepeatRoleTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/**
	 * 默认方法
	 */
	public void execute() throws GeneralException {
		try {
			// 从页面获取用户名
			String username = (String) this.getFormHM().get("username");
			// 从页面获取需要修改的用户id
			String repeatID = (String) this.getFormHM().get("repeatID");
			// 如果页面传输过来的数据为空直接返回
			if (username == null || repeatID == null)
				return;
			// 验证用户名是否存在，存在则向页面输送exist标识符，否则修改用户名，并返回ok标识
			if (this.validationRole(username)) {
				String table = repeatID.substring(0, 3) + "A01";
				String a0100 = repeatID.substring(3);
				// 进入修改用户名方法
				this.update(table, a0100, username);
				this.getFormHM().put("flag", "ok");
			} else
				this.getFormHM().put("flag", "exist");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * 
	 * @Title: update
	 * @Description: 修改用户名
	 * @param table要修改的表
	 * @param a0100
	 *            修改的ID
	 * @param username
	 *            修改的用户名
	 * @throws GeneralException
	 * @return void
	 * @throws
	 */
	private void update(String table, String a0100, String username)
			throws GeneralException {
		// sql语句，使用StringBuffer类
		StringBuffer sqlstr = new StringBuffer();
		// 获取动态用户名
				String name = this.getDynamicUserName();
		try {
			// 修改用户名的sql语句
			sqlstr.append("update ");
			sqlstr.append(table);
			sqlstr.append(" set ");
			sqlstr.append(name);
			sqlstr.append(" = '");
			sqlstr.append(username);
			sqlstr.append("' where A0100 = '");
			sqlstr.append(a0100);
			sqlstr.append("'");
			// 数据库连接
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			// 修改该用户名
			dao.update(sqlstr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	private boolean validationRole(String username) throws GeneralException {
		boolean flag = true;
		// 获取动态用户名
		String name = this.getDynamicUserName();
		// sql查询语句，使用StringBuffer类
		StringBuffer sqlstr = new StringBuffer();
		try {
			// 数据库连接
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			// 先从OperUser表中查询数据，然后在复合查询，提高效率
			sqlstr.append("select UserName from OperUser where RoleID=0 and UserName='");
			sqlstr.append(username);
			sqlstr.append("'");
			// 查询
			this.frowset = dao.search(sqlstr.toString());
			// 如果查询到就直接返回，否则进入多表连接查询
			if (this.frowset.next())
				flag = false;
			else {
				// 获得需要查询的表
				List tableList = this.getTableList();
				// 重置sql查询语句
				sqlstr.setLength(0);
				sqlstr.append("SELECT ");
				sqlstr.append(name);
				sqlstr.append(" FROM( ");
				// 分析需要连接查询的表
				sqlstr.append("SELECT ");
				sqlstr.append(name);
				sqlstr.append(" FROM ");
				sqlstr.append(tableList.get(0));
				sqlstr.append("A01");
				// 循环连接
				if (tableList.size() >= 2) {
					for (int i = 1, n = tableList.size(); i < n; i++) {
						sqlstr.append(" UNION all SELECT ");
						sqlstr.append(name);
						sqlstr.append(" FROM ");
						sqlstr.append(tableList.get(i));
						sqlstr.append("A01");
					}
				}
				sqlstr.append(") un where un.");
				sqlstr.append(name);
				sqlstr.append(" = '");
				sqlstr.append(username);
				sqlstr.append("'");
				this.frowset = dao.search(sqlstr.toString());
				// 根据查询结果返回是否有该用户
				if (this.frowset.next())
					flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

	/**
	 * 
	 * @Title: getRoleBaseList
	 * @Description:获得需要查询的表
	 * @throws GeneralException
	 * @return List
	 * @throws
	 */
	private List getTableList() throws GeneralException {
		/** 登录参数表 */
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		String strTable = login_vo.getString("str_value");
		/** 系统所有存在的数据库列表usr,oth,trs,ret */
		List RepeatTablelist = new ArrayList();
		try {
			// 如果用户已经选择了应用库，则默认使用它。否则默认使用Uer表
			if (strTable.length() > 0) {
				// 分割字符串
				String[] str = strTable.split(",");
				for (int i = 0, n = str.length; i < n; i++)
					RepeatTablelist.add(str[i]);
			} else
				RepeatTablelist.add("Usr");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return RepeatTablelist;
	}

	/**
	 * 
	 * @Title: getDynamicUserName
	 * @Description:获得动态用户名
	 * @return String
	 * @throws
	 */
	private String getDynamicUserName() {
		/** 登录参数表,登录用户指定默认username */
		String username = "UserName";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		// 获得后台设定的用户口令长度，并从后台得到用户名的字段
		if (login_vo != null) {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx > 1) {
				username = login_name.substring(0, idx);
				if ("#".equals(username) || "".equals(username))
					username = "UserName";
			}
		}
		return username;
	}
}
