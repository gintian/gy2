package com.hjsj.hrms.transaction.mobileapp.utils.searchcode;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * <p> Title: SearchCodeBo </p>
 * <p> Description: 代码树查询BO类 </p>
 * <p> Company: hjsj </p>
 * <p> create time 2014-1-15 上午10:28:32 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class SearchCodeBo {
	private Connection conn;
	private UserView userView;

	public UserView getUserView() {
		return userView;
	}

	/**
	 * 构造基本对象
	 */
	public SearchCodeBo() {

	}

	/**
	 * 构造有属性的对象
	 * 
	 * @param conn 数据库
	 * @param userView 用户
	 */
	public SearchCodeBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 
	 * @Title: searchManagerInfoList
	 * @Description:获得业务用户组
	 * @param groupID operuser表中的 groupid
	 * @param flag 查询根节点0，查询子节点1,3便捷查询
	 * @param privflag 业务用户展示控制：0不控制显示数据库中username字段、1先判断a0100看是否关联自助用户，再判断fullname,最后username
	 * @return List
	 * @throws GeneralException
	 */
	public List searchManagerInfoList(String groupID, String flag,
			String privflag) throws GeneralException {
		String sql = this.getManagerSQL(groupID, flag, privflag);
		List personList = this.getManagerList(sql, flag, privflag);
		return personList;
	}

	/**
	 * 
	 * @Title: getManagerList
	 * @Description: 获得业务用户组数据
	 * @param sql sql语句
	 * @param privflag 业务用户展示控制：0不控制显示数据库中username字段、1先判断a0100看是否关联自助用户，再判断fullname,最后username
	 * @param flag 查询根节点0，查询子节点1
	 * @return List
	 * @throws GeneralException
	 */
	private List getManagerList(String sql, String flag, String privflag)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		RowSet rs = null;
		HashMap map = null;
		try {
			rs = dao.search(sql.toString());
			while (rs.next()) {
				map = new HashMap();
				String userName = rs.getString("username");
				String groupName = rs.getString("GroupName");
				String roleID = rs.getString("RoleID");
				// 本节点
				map.put("codeitemid", userName);
				// 父节点
				map.put("parentid", groupName);
				// 标示为业务数组1,还是业务用户0
				map.put("codesetid", roleID);
				// 显示的图标处理
				if ("3".equals(flag)) {// 3为便捷查询，此时所有图片都为文件形式
					map.put("hc", "0");
				} else {// 查询根节点和页节点，根据是否有子节点显示图标
					if ("1".equals(roleID) && this.validationManagerChild(userName)) {// 文件夹图标
						map.put("hc", "1");
					} else {// 文件图标
						map.put("hc", "0");
					}
				}
				// 显示的名称处理
				if ("0".equals(privflag) || "1".equals(roleID)) {// 0不控制，显示username，roleID为1表示用户组，不用查询
					map.put("codeitemdesc", userName);
				} else {// 1走a0100->fullname->username显示
						// 先判断a0100看是否关联自助用户，再判断fullname,最后username
					String a0100 = rs.getString("A0100");
					String nbase = rs.getString("Nbase");
					if (a0100 == null || "".equals(a0100) || nbase == null || "".equals(nbase)) {
						String fullName = rs.getString("FullName");
						if (fullName == null || "".equals(fullName)) {
							map.put("codeitemdesc", userName);
						} else {
							map.put("codeitemdesc", fullName);
						}
					} else {
						String a0101 = this.geta0101(a0100, nbase);
						if (a0101 == null || "".equals(a0101)) {
							map.put("codeitemdesc", userName);
						} else {
							map.put("codeitemdesc", a0101);
						}
					}

				}
				list.add(map);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}

	/**
	 * 
	 * @Title: validationChild
	 * @Description: 查看业务用户组下是否有子节点
	 * @param userGroupID operuser表中的 groupid
	 * @return boolean
	 * @throws GeneralException
	 */
	private boolean validationManagerChild(String groupName)
			throws GeneralException {
		RowSet rs = null;
		StringBuffer resultSql = new StringBuffer();
		try {
			// 先通过groupName查询groupID，再在OperUser表中查询是否有子记录
			String groupID = this.getGroupID(groupName);
			resultSql.append("select * ");
			resultSql.append("from OperUser o ");
			if ("1".equals(groupID)) {// 查询超级用户组,超级用户组下无其他用户组
				resultSql.append("where o.RoleID='0' and o.groupid = '" + groupID + "'");
			} else {// 查询其他用户组
				resultSql.append("where o.groupid = '" + groupID + "'");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(resultSql.toString());
			if (rs.next())
				return true;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return false;
	}

	/**
	 * 
	 * @Title: getManagerSQL
	 * @Description: 获得查询业务用户组的SQL语句
	 * @param groupID operuser表中的 groupid
	 * @param flag 查询根节点0，查询子节点1，3便捷查询
	 * @param privflag 业务用户展示控制：0不控制,显示数据库中username字段、1先判断a0100看是否关联自助用户，再判断fullname,最后username
	 * @return String SQL语句
	 * @throws GeneralException
	 */
	private String getManagerSQL(String groupName, String flag, String privflag)
			throws GeneralException {
		StringBuffer resultSql = new StringBuffer();
		try {
			resultSql.append("select o.UserName, o.RoleID, o.FullName, o.Nbase, o.A0100, u.GroupName ");
			resultSql.append("from OperUser o, UserGroup u ");
			if ("0".equals(flag)) {// 查询根节点,根节点不用考虑人员
				resultSql.append("where o.UserName=u.GroupName and o.RoleID='1' and o.GroupID = '1' ");
			} else if ("1".equals(flag)) {
				String groupID = this.getGroupID(groupName);
				if ("1".equals(groupID)) {// 查询超级用户组,超级用户组下无其他用户组
					resultSql.append("where o.RoleID='0' and o.groupid = '" + groupID + "' and o.GroupID = u.GroupId ");
				} else {// 查询叶节点
					resultSql.append("where o.groupid = '" + groupID + "' and o.GroupID = u.GroupId ");
				}
			} else if ("3".equals(flag)) {// 便捷查询
				resultSql.append("where o.GroupID = u.GroupId and o.RoleID='0' ");
				if ("0".equals(privflag)) {// 0不控制,显示数据库中username字段
					resultSql.append("and (");
					String[] keyword = groupName.split("\n");
					for (int i = 0; i < keyword.length; i++) {
						if ("".equals(keyword[i].trim()))
							continue;
						if (i >= 1)
							resultSql.append("or ");
						resultSql.append("o.UserName like '%" + keyword[i] + "%' ");
					}
					resultSql.append(") ");
				} else { // 走fullname->a0100->username显示
					resultSql.append(this.getManagerKeywordsWhereStr(groupName));
				}
			}
			resultSql.append("order by u.GroupId,o.InGrpOrder");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return resultSql.toString();
	}

	/**
	 * 
	 * @Title: getManagerKeywordsWhereStr
	 * @Description: 业务用户便捷查询
	 * @param keywords 用户输入的内容
	 * @throws GeneralException
	 * @return String
	 */
	private String getManagerKeywordsWhereStr(String keywords)
			throws GeneralException {
		StringBuffer keyWordswhere = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			// 获得人员库
			keyWordswhere.append("select distinct Nbase ");
			keyWordswhere.append("from OperUser ");
			keyWordswhere.append("where Nbase is not null or Nbase <> ''");
			rs = dao.search(keyWordswhere.toString());
			List listNbase = new ArrayList();
			while (rs.next()) {
				listNbase.add(rs.getString("Nbase"));
			}
			// 获得a0100
			keyWordswhere.setLength(0);
			keyWordswhere.append("select distinct o.a0100 ");
			keyWordswhere.append("from (select a0100,Nbase from OperUser) o,");
			keyWordswhere.append("(");
			for (int i = 0; i < listNbase.size(); i++) {
				if (i >= 1)
					keyWordswhere.append(" union all ");
				keyWordswhere.append("select a0100,a0101,'" + listNbase.get(i) + "' Nbase ");
				keyWordswhere.append("from " + listNbase.get(i) + "A01");
			}
			keyWordswhere.append(") s ");
			keyWordswhere.append("where o.nbase = s.nbase and o.a0100 = s.a0100  and (");
			String[] keyword = keywords.split("\n");
			for (int i = 0; i < keyword.length; i++) {
				if ("".equals(keyword[i].trim()))
					continue;
				if (i >= 1)
					keyWordswhere.append("or ");
				keyWordswhere.append("a0101 like '%" + keyword[i] + "%' ");
			}
			keyWordswhere.append(")");
			rs = dao.search(keyWordswhere.toString());
			keyWordswhere.setLength(0);
			while (rs.next()) {
				keyWordswhere.append(",'" + rs.getString("a0100") + "'");
			}
			String a0100;
			if (keyWordswhere.length() == 0) {
				a0100 = "";
			} else {
				a0100 = keyWordswhere.substring(1);
			}
			// 获得条件语句
			keyWordswhere.setLength(0);
			for (int i = 0; i < keyword.length; i++) {
				if ("".equals(keyword[i].trim()))
					continue;
				keyWordswhere.append("or username like '%" + keyword[i] + "%' ");
				keyWordswhere.append("or FullName like '%" + keyword[i] + "%' ");
			}
			if (a0100.length() > 0)
				keyWordswhere.append("or a0100 in (" + a0100 + ")");
			String temporaryStr = keyWordswhere.toString().substring(3);

			// 组装返回便捷查询SQL语句
			keyWordswhere.setLength(0);
			keyWordswhere.append(" and (");
			keyWordswhere.append(temporaryStr);
			keyWordswhere.append(")");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return keyWordswhere.toString();
	}

	/**
	 * 
	 * @Title: getGroupID
	 * @Description: 根据groupName从UserGroup表中查询GroupID
	 * @param groupName
	 * @throws GeneralException
	 * @return String
	 */
	private String getGroupID(String groupName) throws GeneralException {
		RowSet rs = null;
		StringBuffer resultSql = new StringBuffer();
		try {
			resultSql.append("select GroupId from UserGroup ");
			resultSql.append("where GroupName = '" + groupName + "'");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(resultSql.toString());
			if (rs.next())
				return rs.getString("GroupId");
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return null;
	}

	/**
	 * 
	 * @Title: geta0101
	 * @Description:根据a0100和库前缀，查询a0101
	 * @param a0100
	 * @param nbase
	 * @throws GeneralException
	 * @return String
	 */
	private String geta0101(String a0100, String nbase) throws GeneralException {
		StringBuffer sBuffer = new StringBuffer();
		RowSet rs = null;
		try {
			sBuffer.append("select * from " + nbase + "A01 b");
			sBuffer.append(" where b.A0100='" + a0100 + "'");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sBuffer.toString());
			if (rs.next()) {
				return rs.getString("a0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return null;
	}

}
