package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p> Title: SearchRepeatLoginTrans</p>
 * <p> Description:重复角色展示 </p>
 * <p> Company: hjsj </p>
 * <p> create time 2013-10-30 上午9:20:38 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class SearchRepeatLoginTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try {
			String repeatTable = (String) this.getFormHM().get("repeatTable");
			// 如果repeatTable为OperUser则显示业务用户的数据，否则显示 自助用户的数据
			if (repeatTable == null || repeatTable == "") {
				this.queryIndependentUser();
			} else if ("OperUser".equals(repeatTable)) {
				this.queryBusinessUser(repeatTable);
			} else {
				this.queryIndependentUser();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: getRoleBaseList
	 * @Description:获得应用列表
	 * @param b根据不同的boolean值显示不同的下拉列表
	 * @throws GeneralException
	 * @return ArrayList
	 * @throws
	 */
	private ArrayList getRoleBaseList(boolean b) throws GeneralException {
		/** 登录参数表 */
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		String A01 = login_vo.getString("str_value");
		/** 系统所有存在的数据库列表usr,oth,trs,ret */
		ArrayList dblist = new ArrayList();
		try {
			// 刷新下拉列表
			if (b) {
				// 如果用户已经选择了应用库，则默认使用它。否则默认使用Uer表
				if (A01.length() > 0) {
					CommonData business = new CommonData(A01, "自助用户");
					dblist.add(business);
				} else {
					CommonData business = new CommonData("Usr", "自助用户");
					dblist.add(business);
				}
				CommonData operUser = new CommonData("OperUser", "业务用户");
				dblist.add(operUser);
			} else {
				CommonData operUser = new CommonData("OperUser", "业务用户");
				dblist.add(operUser);
				// 如果用户已经选择了应用库，则默认使用它。否则默认使用Uer表
				if (A01.length() > 0) {
					CommonData business = new CommonData(A01, "自助用户");
					dblist.add(business);
				} else {
					CommonData business = new CommonData("Usr", "自助用户");
					dblist.add(business);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return dblist;
	}

	/**
	 * @param dbpre
	 * 
	 * @Title: queryBusinessUser
	 * @Description:查询业务用户
	 * @param
	 * @throws GeneralException
	 * @return void
	 * @throws
	 */
	private void queryBusinessUser(String repeatTable) throws GeneralException {
		try {
			// 获取动态用户名
			String username = this.getDynamicUserName();
			List repeatList = this.getRoleBaseList(false);
			// 下拉列表
			this.getFormHM().put("repeatList", repeatList);
			// 当前选中的列表
			this.getFormHM().put("repeatTable", repeatTable);

			// 初始化长度，提高效率
			StringBuffer strsql = new StringBuffer(500);
			/** 查询前半句 */
			strsql.append("SELECT a.UserName position,b.UserName name ");
			// 查询SQL语句前部分
			this.getFormHM().put("repeatSql_str", strsql.toString());

			/** 需要显示的字段列表 */
			strsql.setLength(0);
			strsql.append("position,name,");
			// 需要显示的字段列表
			this.getFormHM().put("repeatColumns", strsql.toString());
			
			/** 查询后半句 */
			strsql.setLength(0);
			strsql.append("FROM( SELECT o.UserName,u.GroupID FROM OperUser o,UserGroup u WHERE o.RoleID=1 and o.UserName=u.GroupName) a, ");
			strsql.append("(SELECT o.username,o.groupid FROM operuser o,(  ");
			// 分析需要连接查询的表
			CommonData vo = (CommonData) repeatList.get(1);
			String[] str = vo.getDataValue().split(",");
			strsql.append("SELECT ");
			strsql.append(username);
			strsql.append(" FROM ");
			strsql.append(str[0]);
			strsql.append("A01");
			if (str.length >= 2) {
				for (int i = 1, n = str.length; i < n; i++) {
					strsql.append(" UNION all SELECT ");
					strsql.append(username);
					strsql.append(" FROM ");
					strsql.append(str[i]);
					strsql.append("A01");
				}
			}
			strsql.append(") un WHERE o.roleid = 0 AND o.username=un.");
			strsql.append(username);
			strsql.append(") b");
			// 基础条件
			strsql.append(" WHERE a.GroupId = b.GroupID AND b.UserName");
			// oracle和sqlserver兼容性问题2为oracle
			if (Sql_switcher.searchDbServer() == 2)
				strsql.append(" IS NOT NULL");
			else
				strsql.append(" <>''");
			// 查询SQL语句后半句
			this.getFormHM().put("repeatWhere_str", strsql.toString());

			/** 排序 */
			strsql.setLength(0);
			strsql.append("order by a.GroupID");
			// 查询SQL语句排序
			this.getFormHM().put("repeatOrder_by", strsql.toString());
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: queryIndependentUser
	 * @Description: 查询自助用户
	 * @throws GeneralException
	 * @return void
	 * @throws
	 */
	private void queryIndependentUser() throws GeneralException {
		try {
			List repeatList = this.getRoleBaseList(true);
			// 获取动态用户名
			String username = this.getDynamicUserName();
			boolean repeatFlag = true;
			// 已查询字段a0000,a0100,b0110,e0122,e01a1,a0101，当用户设置这些为登陆名时，sql语句变化
			if ("a0000".equals(username) || "a0100".equals(username)
					|| "b0110".equals(username) || "e0122".equals(username)
					|| "e01a1".equals(username) || "a0101".equals(username))
				repeatFlag = false;
			this.getFormHM().put("repeatFlag", String.valueOf(repeatFlag));
			CommonData vo = (CommonData) repeatList.get(0);
			String repeatTable = vo.getDataValue();
			// 下拉列表
			this.getFormHM().put("repeatList", repeatList);
			// 当前选中的列表
			this.getFormHM().put("repeatTable", repeatTable);
//			StringBuffer strsql1 = new StringBuffer();
			StringBuffer strsql = new StringBuffer();
			/** 查询前半句 */
			strsql.append("select a.A0000 a0000, a.A0100 a0100,a.B0110 b0110,a.E0122 e0122,a.E01A1 e01A1,a.A0101 a0101 ");
			if (repeatFlag) {
				strsql.append(",a.");
				strsql.append(username);
				strsql.append(" username");
			}
			// 查询SQL语句前部分
			this.getFormHM().put("repeatSql_str", strsql.toString());
//			strsql1.append(strsql);
			/** 需要显示的字段列表 */
			strsql.setLength(0);
			strsql.append("a0000,a0100,b0110,e0122,e01a1,a0101,");
			if (repeatFlag) 
				strsql.append("username,");
			// 需要显示的字段列表
			this.getFormHM().put("repeatColumns", strsql.toString());

			/** 查询后半句 */
			strsql.setLength(0);
			strsql.append(" FROM( ");
			// 分析需要连接查询的表
			String[] str = repeatTable.split(",");
			strsql.append("SELECT A0000,'");
			strsql.append(str[0]);
			// oracle和sqlserver兼容性问题
			if (Sql_switcher.searchDbServer() == 2)
				strsql.append("'||");
			else
				strsql.append("'+");
			strsql.append("A0100 A0100,B0110,E0122,E01A1,A0101");
			if (repeatFlag) {
				strsql.append(",");
				strsql.append(username);
			}
			strsql.append(" FROM ");
			strsql.append(str[0]);
			strsql.append("A01");
			if (str.length >= 2) {
				for (int i = 1, n = str.length; i < n; i++) {
					strsql.append(" UNION all SELECT A0000,'");
					strsql.append(str[i]);
					if (Sql_switcher.searchDbServer() == 2)
						strsql.append("'||");
					else
						strsql.append("'+");
					strsql.append("A0100 A0100,B0110,E0122,E01A1,A0101 ");
					if (repeatFlag) {
						strsql.append(",");
						strsql.append(username);
					}
					strsql.append(" FROM ");
					strsql.append(str[i]);
					strsql.append("A01");
				}
			}
			strsql.append(") a,(SELECT ");
			strsql.append(username);
			strsql.append(" FROM(SELECT ");
			strsql.append(username);
			strsql.append(" FROM ");
			strsql.append(str[0]);
			strsql.append("A01");
			if (str.length >= 2) {
				for (int i = 1; i < str.length; i++) {
					strsql.append(" UNION all SELECT ");
					strsql.append(username);
					strsql.append(" FROM ");
					strsql.append(str[i]);
					strsql.append("A01");
				}
			}
			strsql.append(" UNION all select UserName from OperUser where RoleID=0 ) un group by un.");
			strsql.append(username);
			strsql.append(" HAVING count(");
			strsql.append(username);
			strsql.append(")>=2) un where un.");
			strsql.append(username);
			strsql.append(" = a.");
			strsql.append(username);
			strsql.append(" and a.");
			strsql.append(username);
			// oracle和sqlserver兼容性问题2为oracle
			if (Sql_switcher.searchDbServer() == 2)
				strsql.append(" IS NOT NULL");
			else
				strsql.append(" <>''");
			// 查询SQL语句后半句
			this.getFormHM().put("repeatWhere_str", strsql.toString());
//			strsql1.append(strsql);
//			ContentDAO dao = new ContentDAO(this.getFrameconn());
//			this.frowset = dao.search(strsql1.toString());
			/** 排序 */
			strsql.setLength(0);
			strsql.append("order by a.A0000");
			// 查询SQL语句排序
			this.getFormHM().put("repeatOrder_by", strsql.toString());
		} catch (Exception ee) {
			ee.printStackTrace();
		}
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
