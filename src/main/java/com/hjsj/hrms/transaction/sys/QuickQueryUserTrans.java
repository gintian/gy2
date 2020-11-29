package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据输入的信息查询用户名称
 * <p>Title: QuickQueryUserTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-5-12 下午4:16:29</p>
 * @author jingq
 * @version 1.0
 */
public class QuickQueryUserTrans extends IBusiness {

	/** OperUser表UserName字段 */
	private String userName;
	/** OperUser表FullName字段 */
	private String fullName;
	/** 人员库（如：UsrA01）表A0101字段 */
	private String a0101;

	public void execute() throws GeneralException {
		/** 用户输入的数据 */
		String oldname = (String) this.getFormHM().get("oldname");
		/** 显示标识 0:UserName(FullName)、1:UserName(FullName,A0101)，默认为0 */
		String isfilter = (String) this.getFormHM().get("isfilter");
		/** 查询权限控制：0不控制、1模糊查询业务用户所属用户组下的业务用户， 默认为0 */
		String searchFlag = (String) this.getFormHM().get("searchFlag");
		/** 库权限控制：0不控制、1走web设置的认证人员库、2管理范围、3认证人员库和管理范围的交集 */
		String dbpreFlag = (String) this.getFormHM().get("privflag");
		// 安全机制
		oldname = oldname == null || oldname.length() == 0 ? "" : oldname;
		isfilter = isfilter == null || isfilter.length() == 0 ? "0" : isfilter;
		searchFlag = searchFlag == null || searchFlag.length() == 0 ? "0" : searchFlag;
		dbpreFlag = dbpreFlag == null || dbpreFlag.length() == 0 ? "0" : dbpreFlag;
		try {
			// 输入数据处理
			this.dealWithInput(oldname, isfilter);
			// 获取人员库
			List preList = this.getDbpreList(dbpreFlag);
			// 用户组获取
			Map groupMap = this.getGroup();
			// 关联表
			Map relevanceMap = this.getRelevance(preList);
			// 当前用户所在用户组
			String userGroupName = "";
			// 1模糊查询业务用户所属用户组下的业务用户，超级用户组成员拥有全部权限
			if ("1".equals(searchFlag) && !"1".equals(this.userView.getGroupId())) {
				userGroupName = this.getGroupName(this.userView.getGroupId());
				// 转换为详细描述
				userGroupName = (String) groupMap.get(userGroupName);
			}
			
			// 主sql处理业务用户库
			StringBuffer mainSql = new StringBuffer();
			mainSql.append("select o.UserName, o.FullName, Nbase + A0100 na, o.RoleID, u.GroupName");
			mainSql.append(" from OperUser o, usergroup u");	
			// oracle 数据库字段连接用||
			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
				String oracle = mainSql.toString().replace("+", "||");
				mainSql.setLength(0);
				mainSql.append(oracle);
			}
			mainSql.append(" where u.GroupId = o.GroupID ");
			mainSql.append(" and (");
			mainSql.append(" username like '%" + this.userName + "%' escape '\\'");
			mainSql.append(" or fullname like '%" + this.fullName + "%' escape '\\'");	
			mainSql.append(" )");
			
			// 副sql处理自助用户库
			StringBuffer deputySql = new StringBuffer();
			deputySql.append("select o.UserName, o.FullName, o.na, o.RoleID, o.GroupName");
			deputySql.append(" from (");
			// 先连接业务用户库
			deputySql.append(" select o.UserName, o.FullName, Nbase + A0100 na, o.RoleID, u.GroupName");
			deputySql.append(" from OperUser o, usergroup u");
			deputySql.append(" where u.GroupId = o.GroupID) o,");
			// 再连接自助用户表
			StringBuffer tempBuf = new StringBuffer("");
			for (int i = 0; i < preList.size(); i++) {
				tempBuf.append(" union all");
				tempBuf.append(" select '" + preList.get(i) + "' + A0100 ua, A0101");
				tempBuf.append(" from " + preList.get(i) + "A01");
			}
			deputySql.append(" (" + tempBuf.substring(11) + ") u");
			// oracle 数据库字段连接用||
			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
				String oracle = deputySql.toString().replace("+", "||");
				deputySql.setLength(0);
				deputySql.append(oracle);
			}
			deputySql.append(" where o.na = u.ua");
			deputySql.append(" and u.A0101 like '%" + this.fullName + "%' escape '\\'");	
		
			// 拼装整个sql语句
			String sql = mainSql.toString() + " union all " + deputySql.toString();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			// 组装显示的数据
			String groupName;
			List namelist = new ArrayList();
			String name, value;
			ArrayList userNameList = new ArrayList();
			while (this.frowset.next()) {
				this.userName = this.frowset.getString("UserName");
				if(userNameList.contains(this.userName))
					continue;
				else
					userNameList.add(this.userName);
				this.fullName = this.frowset.getString("FullName");
				this.a0101 = this.frowset.getString("na");
				// 业务用户组
				if ("1".equals(this.frowset.getString("RoleID"))) {
					groupName = (String) groupMap.get(this.userName);
					value = groupName;
				} else {
					groupName = (String) groupMap.get(this.frowset.getString("GroupName"));
					value = groupName + "\\\\" + this.userName;
				}
				// 1模糊查询业务用户所属用户组下的业务用户，超级用户组成员拥有全部权限
				if ("1".equals(searchFlag) && !"1".equals(this.userView.getGroupId())) {
					if (groupName.indexOf(userGroupName) == -1)
						continue;
				}
				// 1:UserName(FullName,A0101)
				if ("1".equals(isfilter)) {
					// 关联自助用户
					if (StringUtils.isNotBlank(this.fullName)&&this.a0101 != null && this.a0101.length() > 0) {
						//zxj 20191220 jazz 56649 全名和人员姓名一样，不用重复显示
						String selfName = (String) relevanceMap.get(this.a0101);
						if (!this.fullName.equalsIgnoreCase(selfName))
						    this.fullName = this.fullName + "," + selfName;
					}
				}
				if (this.fullName != null && this.fullName.length() > 0) {
					value = value + "(" + this.fullName + ")";
				}
				name = value;
				// 字符串长度过长时，截取一部分显示
				if (name.length() > 20)
					name = name.substring(0, 8) + "..." + name.substring(name.length() - 10, name.length());
				CommonData temp = new CommonData(SafeCode.encode(value), SafeCode.encode(name));
				namelist.add(temp);
			}
			this.getFormHM().put("namelist", namelist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 
	 * @Title: dealWithInput
	 * @Description:处理用户输入的数据
	 * @param oldname  用户输入
	 * @param isfilter 显示标识 0:UserName(FullName)、88:UserName(FullName,A0101)，默认为0
	 * @return void
	 */
	private void dealWithInput(String oldname, String isfilter) {
		// 处理用户输入特殊字符
		oldname = SafeCode.decode(oldname);
		if (oldname != null && oldname.length() > 0) {
			oldname = oldname.replace("nbspa", "#");
			oldname = PubFunc.keyWord_reback(oldname);
			oldname = oldname.replace("quanjiao;hao", "；");
			// 数据库查询特殊符号
			oldname = oldname.replaceAll("\\\\", "\\\\\\\\");
			oldname = oldname.replaceAll("%", "\\\\%");
			oldname = oldname.replaceAll("_", "\\\\_");
			oldname = oldname.replaceAll("'", "''");
			oldname = oldname.replaceAll("\\[", "\\\\[");
		}
		// 初始化
		this.userName = oldname;
		// 查询预处理
		// 处理UserName(FullName)格式的输入数据
		int length = this.userName.length();
		// 括号在最后，去掉
		if ((length == this.userName.indexOf(")") + 1 || this.userName.indexOf("(") + 1 == length) && length >= 2)
			this.userName = this.userName.substring(0, length - 1);
		int middle = this.userName.indexOf("(");
		if (middle > 0) {
			this.fullName = this.userName.substring(middle + 1);
			this.userName = this.userName.substring(0, middle);
			// 处理UserName(FullName,A0101)格式的输入数据
			if ("1".equals(isfilter)) {
				// 点在最后，去掉
				length = this.fullName.length();
				if (length == this.fullName.indexOf(",") + 1 && length >= 2)
					this.fullName = this.fullName.substring(0, length - 1);
				middle = this.fullName.indexOf(",");
				if (middle > 0) {
					this.a0101 = this.fullName.substring(middle + 1);
					this.fullName = this.fullName.substring(0, middle);
				} else {
					this.a0101 = this.fullName;
				}
			}
		} else {
			this.fullName = this.userName;
			this.a0101 = this.userName;
		}
	}

	/**
	 * 
	 * @Title: getGroup
	 * @Description: 获取用户组数据
	 * @throws GeneralException
	 * @return Map 格式有父节点时：{userName,父节点\\userName}、没有父节点时：{userName,userName}
	 */
	private Map getGroup() throws GeneralException {
		Map map = new HashMap();
		StringBuffer sql = new StringBuffer();
		sql.append("select o.UserName,u.GroupId,o.RoleID,u.GroupName from  OperUser o, usergroup u ");
		sql.append("where o.RoleID = '1' and u.GroupId = o.GroupID ");
		// order by 作用确定根节点比子节点先查询出来
		sql.append("order by u.GroupId");
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql.toString());
			
			/*
			while (this.frowset.next()) {
				userName = this.frowset.getString("UserName");
				// 算法说明：针对根节点和子节点做不同处理，根节点只显示本身，子节点还需显示父节点
				if (this.frowset.getString("GroupId").equals(this.frowset.getString("RoleID"))) {
					// 一级节点
					map.put(userName, userName);
				} else {
					// 子节点
					groupName = this.frowset.getString("GroupName");
					// value中加载父节点
					map.put(userName, map.get(groupName) + "\\\\" + userName);
				}
			}
			*/
			
			/*解决问题 33359：上面算法是按照groupid排序顺序处理的，一般情况下级的id总是比上级id大没问题，但是如果在业务用户树上对分组进行拖拽调整
			 * 后，就会出现问题。使用新算法处理，不依赖排序 guodd 2017-12-22*/
			List groups = ExecuteSQL.executeMyQuery(sql.toString(), this.getFrameconn());
			
			int i=0,max = (groups.size()+1)*2+1;//max为理论最大循环数，防止有脏数据导致死循环
			String userName, groupName;
			//一直循环下去，直到groups的size为0或已达到理论最大循环数结束循环
			while(groups.size()>0&&i<max){
				i++;
				LazyDynaBean lb = (LazyDynaBean)groups.get(0);
				userName = (String)lb.get("username");
				//如果groupid==roleid,是根节点
				if(lb.get("groupid").equals(lb.get("roleid"))){
					//分组名称放入map中
					map.put(userName, userName);
					//此节点处理完毕后从groups集合中移除
					groups.remove(0);
					continue;
				}
				
				groupName = (String)lb.get("groupname");
				//如果map中存在当前分组的父节点，当前分组的名称为 父分组名称+当前分组名词
				if(map.containsKey(groupName)){
					map.put(userName, map.get(groupName) + "\\\\" + userName);
					groups.remove(0);
					continue;
				}
				
				//如果map中没有当前分组的父节点，说明父节点还没循环到。将此分组置于groups集合最后
				groups.remove(0);
				groups.add(lb);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 
	 * @Title: getRelevance
	 * @Description: 获取业务用户关联自助用户的数据
	 * @param prelist 人员库
	 * @throws GeneralException
	 * @return Map 格式{Nbase+A0100,A0101}
	 */
	private Map getRelevance(List preList) throws GeneralException {
		Map map = new HashMap();
		try {
			if (preList.size() == 0)
				return map;
			StringBuffer usrBuf = new StringBuffer("");
			for (int i = 0; i < preList.size(); i++) {
				// select 'Usr'+A0100 from UsrA01 where A0101 like '' escape '\'
				usrBuf.append(" union all");
				usrBuf.append(" select '" + preList.get(i) + "'+A0100 ua, A0101");
				usrBuf.append(" from " + preList.get(i) + "A01");
			}
			// 拼装sql语句
			StringBuffer sql = new StringBuffer();
			sql.append("select ua, A0101 from (" + usrBuf.substring(11) + ") u");
			sql.append(", (select Nbase+A0100 na from OperUser) o");
			sql.append(" where u.ua = o.na");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			// oracle 数据库字段连接用||
			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
				String oracle = sql.toString().replace("+", "||");
				sql.setLength(0);
				sql.append(oracle);
			}
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				map.put(this.frowset.getString("ua"), this.frowset.getString("A0101"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 
	 * @Title: getGroupName
	 * @Description: 根据用户组ID获得用户组名称
	 * @param groupId 用户组ID
	 * @return String
	 * @throws GeneralException 
	 */
	private String getGroupName(String groupId) throws GeneralException {
		String groupName = "";
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search("select GroupName from UserGroup where GroupId = '" + groupId + "'");
			if (this.frowset.next()) {
				groupName = this.frowset.getString("GroupName");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return groupName;
	}

	/**
	 * 
	 * @Title: getDbpreList
	 * @Description: 获取人员库
	 * @param dbpreFlag 库权限控制：0不控制、1走web设置的认证人员库、2管理范围、3认证人员库和管理范围的交集
	 * @return List
	 * @throws GeneralException
	 */
	private List getDbpreList(String dbpreFlag) throws GeneralException {
		List list = new ArrayList();
		try {
			// 0不控制、1认证人员库、2管理范围、3认证人员库和管理范围的交集
			if ("0".equals(dbpreFlag)) {
				list = this.getNbaseList();
			} else if ("1".equals(dbpreFlag)) {
				list = this.getLoginDbpreList();
			} else if ("2".equals(dbpreFlag)) {
				list = this.userView.getPrivDbList();
			} else if ("3".equals(dbpreFlag)) {
				List temporaryList1 = this.getLoginDbpreList();
				List temporaryList2 = this.userView.getPrivDbList();
				list = this.getIntersectionList(temporaryList1, temporaryList2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 
	 * @Title: getLoginDbpreList
	 * @Description:获取认证人员库
	 * @throws GeneralException
	 * @return List
	 * @throws
	 */
	private List getLoginDbpreList() throws GeneralException {
		List list = new ArrayList();
		try {
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			String logindbpre = "";
			if (login_vo != null)
				logindbpre = login_vo.getString("str_value");
			String[] temporary = logindbpre.split(",");
			for (int i = 0, length = temporary.length; i < length; i++)
				list.add(temporary[i]);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 
	 * @Title: getNbaseList
	 * @Description:获得数据库中的人员库，即全部人员库
	 * @return ArrayList
	 * @throws GeneralException
	 */
	private List getNbaseList() throws GeneralException {
		List list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search("select Pre from DBName order by DbId");
			while (this.frowset.next()) {
				list.add(this.frowset.getString("pre"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 
	 * @Title: getIntersectionList
	 * @Description: 取两个数组中的交集
	 * @param dbaseUserlist
	 * @param dbaseIDList
	 * @return List
	 * @throws GeneralException
	 */
	private List getIntersectionList(List dbaseUserList, List dbaseIDList) throws GeneralException {
		// 最差的情况就是两个list完全不相同
		Map map = new HashMap(dbaseUserList.size() + dbaseIDList.size());
		// 返回的相同
		List sameList = new ArrayList();
		// 标识符
		Integer flag = new Integer(1);
		boolean boo = true;
		try {
			// 判定两张表的大小，通过不同的语句执行。先放入大的表，然后使用小的表去查询两个list的区别
			if (dbaseIDList.size() > dbaseUserList.size())
				boo = false;
			if (boo) {
				// 放入大的数据库查询到的集合，以下方法不适合用于jdk的高版本，在高版本使用自动封装，效率更高
				for (int i = 0, n = dbaseUserList.size(); i < n; i++) {
					map.put(dbaseUserList.get(i), flag);
				}
				// 循环遍历小的集合，在大的集合找到是，放入返回的sameList中
				for (int i = 0, n = dbaseIDList.size(); i < n; i++) {
					if (map.containsKey(dbaseIDList.get(i)))
						sameList.add(dbaseIDList.get(i));
				}
			} else {
				// 放入大的list集合
				for (int i = 0, n = dbaseIDList.size(); i < n; i++) {
					map.put(dbaseIDList.get(i), flag);
				}
				// 循环遍历小的集合，在大的集合找到是时，不做任何操作，否则放入返回的list中
				for (int i = 0, n = dbaseUserList.size(); i < n; i++) {
					if (map.containsKey(dbaseUserList.get(i)))
						sameList.add(dbaseUserList.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sameList;
	}
}
