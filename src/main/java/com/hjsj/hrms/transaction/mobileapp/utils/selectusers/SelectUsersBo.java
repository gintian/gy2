package com.hjsj.hrms.transaction.mobileapp.utils.selectusers;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.transaction.mobileapp.utils.PaginationUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p> Title: SelectUsersBo </p>
 * <p> Description:选择自助用户或业务用户BO类 </p>
 * <p> Company: hjsj </p>
 * <p> create time 2014-1-15 下午3:57:48 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class SelectUsersBo {
	
	private Connection conn;
	private UserView userView;

	/**
	 * 基本对象
	 */
	public SelectUsersBo() {

	}

	/**
	 * 有属性对象
	 * 
	 * @param conn
	 * @param userView
	 */
	public SelectUsersBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 
	 * @Title: searchEmpInfoList
	 * @Description: 获取人员列表
	 * @param unitID 代码树查询
	 * @param keywords 模糊查询
	 * @param url http地址
	 * @param pageIndex 页数
	 * @param pageSize 每页显示的数目
	 * @param privflag 库权限控制：0不控制、1走web设置的认证人员库、2管理范围、3认证人员库和管理范围的交集
	 * @return List
	 * @throws GeneralException
	 */
	public List searchEmpInfoList(String unitID, String keywords, String url,
			String pageIndex, String pageSize, String privflag) throws GeneralException {
		List personList = null;
		String sql = this.getEmpSQL(unitID, keywords, pageIndex, pageSize, privflag);
		personList = this.getEmpPersonList(sql, url);
		return personList;
	}

	/**
	 * 
	 * @Title: searchManagerInfoList
	 * @Description: 获取业务用户List
	 * @param unitID 代码树查询
	 * @param keywords 模糊查询
	 * @param url http地址
	 * @param pageIndex 页数
	 * @param pageSize 每页显示的数目
	 * @return List
	 * @throws GeneralException
	 */
	public List searchManagerInfoList(String unitID, String keywords,
			String url, String pageIndex, String pageSize) throws GeneralException {
		List managerList = new ArrayList();
		StringBuffer resultSql = new StringBuffer();
		try {
			resultSql.append("select a.username userName,a.FullName,a.a0100,a.nbase,b.GroupName groupName,b.GroupId groupId ");
			resultSql.append("from operuser a left join usergroup b  on a.GroupID = b.GroupId ");
			resultSql.append("where roleid=0");
			// 业务用户组判断
			if (unitID.length() > 0) {
				String[] temporary = unitID.split("`");
				resultSql.append(" and a.groupid = '" + temporary[1] + "'");
			}
			// 快速查询判断
			if (keywords.length() > 0) {
				resultSql.append(this.getManagerKeywordsWhereStr(keywords));
			}
			int pageindex = Integer.parseInt(pageIndex);
			int pagesize = Integer.parseInt(pageSize);
			String orderBy = "order by groupId";
			RowSet rs = PaginationUtil.execSQLOneByPage(resultSql.toString(), pageindex, pagesize, orderBy);
			HashMap map = null;
			while (rs.next()) {
				map = new HashMap();
				String userName = rs.getString("username");
				if (userName != null && userName.length() > 0) {
					map.put("a0100", userName);
					map.put("groupName", rs.getString("groupName"));
					String fullname = rs.getString("FullName");
					//先判断fullname 再判断a0100,最后username
					if(fullname == null || "".equals(fullname)){
						String a0100 = rs.getString("a0100");
						String nbase = rs.getString("nbase");
						if(a0100 == null || "".equals(a0100) || nbase == null || "".equals(nbase)){
							map.put("name", userName);							
						}else{							
							String a0101 = this.geta0101(a0100, nbase);
							if(a0101 == null || "".equals(a0101)){
								map.put("name", userName);
							}else{
								map.put("name", a0101);								
							}							
						}						
					}else{
						map.put("name", fullname);
					}
					managerList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return managerList;
	}

	/**
	 * 
	 * @Title: getManagerKeywordsWhereStr   
	 * @Description: 便捷查询   
	 * @param keywords 用户输入的内容
	 * @throws GeneralException 
	 * @return String
	 */
	private String getManagerKeywordsWhereStr(String keywords) throws GeneralException {
		StringBuffer keyWordswhere = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			// 获得人员库
			keyWordswhere.append("select distinct Nbase from OperUser ");
			keyWordswhere.append("where Nbase is not null or Nbase<>'' ");
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
			if(keyWordswhere.length()==0 ){
				a0100 = "";
			}else{
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
			if(a0100.length()>0)
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
	 * @Title: geta0101   
	 * @Description:根据a0100和库前缀，查询a0101    
	 * @param a0100 
	 * @param nbase
	 * @throws GeneralException 
	 * @return String
	 */
	private String geta0101(String a0100, String nbase) throws GeneralException {
		// TODO Auto-generated method stub
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("select * from "+nbase+"A01 b" );
		sBuffer.append(" where b.A0100='"+a0100+"'");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
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

	/**
	 * 
	 * @Title: getEmpPersonList
	 * @Description: 根据SQl语句获取自助用户List
	 * @param sql SQL语句
	 * @param url http地址
	 * @return List
	 * @throws GeneralException
	 */
	private List getEmpPersonList(String sql, String url)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		RowSet rs = null;
		HashMap map = null;
		try {
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			display_e0122 = display_e0122 == null || display_e0122.length() == 0 ? "0" : display_e0122;
			String seprartor = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122,"sep");
			seprartor = seprartor != null && seprartor.length() > 0 ? seprartor : "/";
			rs = dao.search(sql.toString());
			while (rs.next()) {
				map = new HashMap();
				String a0100 = rs.getString("a0100");
				String dbpre = rs.getString("dbpre");
				map.put("dbpre", dbpre);
				map.put("a0100", a0100);
				map.put("name", rs.getString("a0101"));
				String b0110 = rs.getString("b0110");
				String e0122 = rs.getString("e0122");
				String e01a1 = rs.getString("e01a1");
				b0110 = AdminCode.getCodeName("UN", b0110);
				b0110 = b0110 == null ? "" : b0110;
				CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
				if (itemid != null)
					e0122 = itemid.getCodename();
				e0122 = e0122 == null ? "" : e0122;
				e01a1 = AdminCode.getCodeName("@K", e01a1);
				map.put("pos", e01a1);
				e01a1 = e01a1 == null ? "" : e01a1;
				map.put("org", b0110 + (b0110.length() > 0 && e0122.length() > 0 ? seprartor : "") + e0122);
				StringBuffer photourl = new StringBuffer();
				String filename = ServletUtilities.createPhotoFile(dbpre + "A00", rs.getString("a0100"), "P", null);
				if (!"".equals(filename)) {
					photourl.append(url);
					photourl.append("/servlet/DisplayOleContent?mobile=1&filename=");
					photourl.append(filename);
				} else {
					photourl.append(url);
					photourl.append("/images/photo.jpg");
				}
				map.put("photo", photourl.toString());
				list.add(map);
			}
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}

	/**
	 * 
	 * @Title: getEmpSQL
	 * @Description:
	 * @param unitID 组织机构ID
	 * @param keywords 模糊查询
	 * @param pageIndex 第几页
	 * @param pageSize 每页显示条数
	 * @param dbpreFlag 库权限控制：0不控制、1走web设置的认证人员库、2管理范围、3认证人员库和管理范围的交集
	 * @return String
	 * @throws GeneralException
	 */
	private String getEmpSQL(String unitID, String keywords, String pageIndex,
			String pageSize, String dbpreFlag) throws GeneralException {
		int index = Integer.parseInt(pageIndex);
		int size = Integer.parseInt(pageSize);
		StringBuffer resultSql = new StringBuffer();
		try {
			// 获取人员库
			List list = this.getDbpreList(dbpreFlag);
			String dbpre;
			StringBuffer unitIDSql = new StringBuffer();
			// 组织机构树判断 (如果是UN`说明是有顶级机构权限，不进行权限校验 guodd 2017-11-14)
			if (unitID.length() > 0 && !"UN`".equals(unitID.toUpperCase())) {
				// um是e0122单位,un是b0100部门
				String[] temporary = unitID.split("`");
				String codesetid = temporary[0];
				String id = temporary[1];
				if ("UN".equals(codesetid) || "un".equals(codesetid))
					unitIDSql.append(" and b0110 like '" + id + "%'");
				else if ("UM".equals(codesetid) || "um".equals(codesetid))
					unitIDSql.append(" and e0122 like '" + id + "%'");
			}
			// 快速查询判断
			StringBuffer keywordsSql = new StringBuffer();
			if (keywords.length() > 0)
				keywordsSql.append(this.getKeywordsWhereStr(keywords));
			for (int i = 0, length = list.size(); i < length; i++) {
				dbpre = (String) list.get(i);
				resultSql.append(" union all ");
				resultSql.append("select distinct " + dbpre + "a01.a0100,'" + (i + 1) + "' ord,'" + dbpre + "' dbpre," + dbpre
						+ "a01.b0110," + dbpre + "a01.e01a1," + dbpre + "a01.e0122,a0101,a0000");
				resultSql.append(" from " + dbpre + "A01");
				resultSql.append(" where 1=1");
				resultSql.append(unitIDSql);
				resultSql.append(keywordsSql);
			}
			dbpre = resultSql.toString().substring(11);
			resultSql.setLength(0);
			resultSql.append("select * from (select ROW_NUMBER() over(ORDER BY ord, A0000) numberCode, A.* from (");
			resultSql.append(dbpre + ") A");
			resultSql.append(") T where numberCode between " + ((index - 1) * size + 1) + " and " + (size * index));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return resultSql.toString();
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
				list = userView.getPrivDbList();
			} else if ("3".equals(dbpreFlag)) {
				List temporaryList1 = this.getLoginDbpreList();
				List temporaryList2 = userView.getPrivDbList();
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
	 * @Title: getWhereStrByBaseInfo
	 * @Description: 获得基本信息（姓名，拼音简码，工号）条件语句
	 * @param keywords 模糊查询
	 * @return String
	 * @throws GeneralException
	 */
	private String getKeywordsWhereStr(String keywords) throws GeneralException {
		StringBuffer where = new StringBuffer();
		try {
			String keyword[] = keywords.split("\n");
			where.append(" and (  ");// 姓名
			for (int i = 0; i < keyword.length; i++) {
				if ("".equals(keyword[i].trim()))
					continue;
				if (i == 0) {
					where.append(" a0101 like '%" + keyword[i] + "%' ");
				} else {
					where.append(" or a0101 like '%" + keyword[i] + "%' ");
				}
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(
					Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			if (item != null && !"a0101".equalsIgnoreCase(onlyname) && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
				for (int i = 0; i < keyword.length; i++) {
					if ("".equals(keyword[i].trim()))
						continue;
					where.append(" or " + onlyname + " like '%" + keyword[i] + "%' ");
				}

			}
			String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
			item = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
			if (!(pinyin_field == null || "".equals(pinyin_field) || "#".equals(pinyin_field) || item == null
					|| "0".equals(item.getUseflag())) && !"a0101".equalsIgnoreCase(pinyin_field)
					&& !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
				for (int i = 0; i < keyword.length; i++) {
					if ("".equals(keyword[i].trim()))
						continue;
					where.append(" or " + pinyin_field + " like '%" + keyword[i] + "%' ");
				}
			}
			where.append(")");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return where.toString();
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
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("select * from dbname order by DbId");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			rs = dao.search(sBuffer.toString());
			sBuffer.setLength(0);
			while (rs.next()) {
				list.add(rs.getString("pre"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
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
	 * @Title: getIntersectionList
	 * @Description: 取两个数组中的交集
	 * @param dbaseUserlist
	 * @param dbaseIDList
	 * @return List
	 * @throws GeneralException
	 */
	private List getIntersectionList(List dbaseUserList, List dbaseIDList)
			throws GeneralException {
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
