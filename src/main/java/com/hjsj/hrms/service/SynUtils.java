package com.hjsj.hrms.service;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title:SynUtils
 * </p>
 * <p>
 * Description:同步人员、组织机构信息的方法
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-6
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SynUtils {

	public SynUtils() {
	}

	/**
	 * 解析xml，获得数据库信息
	 * 
	 * @param xmlMessage
	 *            String xml信息
	 * @return LazyDynaBean 数据库信息
	 */
	public LazyDynaBean parseXml(String xmlMessage) {
		LazyDynaBean bean = new LazyDynaBean();
		try {
			Document doc = PubFunc.generateDom(xmlMessage);
			String path = "/hr/recs/rec";
			XPath xpath = XPath.newInstance(path);
			List list = xpath.selectNodes(doc);
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				Element el = (Element) list.get(i);
				if (el.getTextTrim().length() > 0) {
					str.append(",");
					str.append(el.getText());
				}

			}
			if (str.length() > 0) {
				bean.set("rec", str.substring(1));
			} else {
				bean.set("rec", "");
			}
			path = "/hr/jdbc";
			Element eleObj = (Element) xpath.selectSingleNode(doc, path);
			list = eleObj.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element el = (Element) list.get(i);
				String name = el.getName();
				String value = el.getText();

				bean.set(name, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 返回
		return bean;
	}

	/**
	 * 根据bean创建数据库连接
	 * 
	 * @param bean
	 * @return Connection ,如果不能创建成功，将返回null
	 */
	public Connection createConnByBean(LazyDynaBean bean) {
		Connection conn = null;
		// 数据库类型
		String datatype = (String) bean.get("datatype");
		// 用户名
		String username = (String) bean.get("username");
		// 密码
		String pass = (String) bean.get("pass");
		// 数据库url
		StringBuffer url = new StringBuffer();
		// 根据数据库类型创建
		try {
			if ("mssql".equalsIgnoreCase(datatype)) {
				// url
				url.append("jdbc:sqlserver://");
				url.append(bean.get("ip_addr"));
				url.append(":");
				url.append(bean.get("port"));
				url.append(";databaseName=");
				url.append(bean.get("database"));
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				conn = DriverManager.getConnection(url.toString(), username,
						pass);
			} else if ("oracle".equalsIgnoreCase(datatype)) {
				// url
				url.append("jdbc:oracle:thin:@");
				url.append(bean.get("ip_addr"));
				url.append(":");
				url.append(bean.get("port"));
				url.append(":");
				url.append(bean.get("database"));

				Class.forName("oracle.jdbc.OracleDriver");
				conn = DriverManager.getConnection(url.toString(), username,
						pass);
			} else if ("db2".equalsIgnoreCase(datatype)) {
				Class.forName("Com.ibm.db2.jdbc.net.DB2Driver");
				url.append("jdbc:db2://");
				url.append(bean.get("ip_addr"));
				url.append(":");
				url.append(bean.get("port"));
				url.append("/");
				url.append(bean.get("database"));

				conn = DriverManager.getConnection(url.toString(), username,
						pass);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}

	/**
	 * 获得erp的数据库连接
	 * 
	 * @return LazyDynaBean
	 */
	public LazyDynaBean getDBBean() {
		LazyDynaBean bean = new LazyDynaBean();

		bean.set("datatype", SystemConfig.getPropertyValue("erpdbtype"));
		bean.set("database", SystemConfig.getPropertyValue("erpdbname"));
		bean.set("ip_addr", SystemConfig.getPropertyValue("erpdbip"));
		bean.set("username", SystemConfig.getPropertyValue("erpdbusername"));
		bean.set("port", SystemConfig.getPropertyValue("erpdbport"));
		bean.set("pass", SystemConfig.getPropertyValue("erpdbpwd"));

		return bean;
	}

	/**
	 * 同步组织机构
	 * 
	 * @param ehrConn
	 * @param erpConn
	 * @param ehrTable
	 * @return
	 */
	public Map syncOrg(Connection ehrConn, Connection erpConn, String ehrTable,
			String erpCode, String dbType, String condition) {
		Map map = new HashMap();
		StringBuffer xml = new StringBuffer();

		StringBuffer sql = new StringBuffer();

		sql.append("select * from ");
		sql.append(ehrTable);
		sql.append(" where ");
		sql.append(condition);
//		sql.append(" and ");
//		sql.append(Sql_switcher.isnull("corcode", "'-1'"));
//		sql.append("<>'-1'");
//		if ("mssql".equalsIgnoreCase(dbType)) {
//			sql.append(" and corcode<>''");
//		}

		// 更新的记录id
		StringBuffer update = new StringBuffer();
		// 新增的记录id
		StringBuffer insert = new StringBuffer();
		// 删除的记录id
		StringBuffer delete = new StringBuffer();

		// 本该执行更新，当做的是插入操作
		StringBuffer updateToinsert = new StringBuffer();
		// 本该新增，但执行的是更新
		StringBuffer insertToupdate = new StringBuffer();
		// 本该删除，但执行的是插入
		StringBuffer deleteToinsert = new StringBuffer();

		// 本该执行更新，当做的是插入操作
		StringBuffer updateToinsertErr = new StringBuffer();
		// 本该新增，但执行的是更新
		StringBuffer insertToupdateErr = new StringBuffer();
		// 本该删除，但执行的是插入
		StringBuffer deleteToinsertErr = new StringBuffer();

		StringBuffer erros = new StringBuffer();

		ContentDAO ehrDao = new ContentDAO(ehrConn);
		RowSet rs = null;
		try {
			rs = ehrDao.search(sql.toString());
			while (rs.next()) {
				// 获得唯一字段的值
				String id = rs.getString("corcode");
				
				
				
				// 系统代号
				String codeValue = rs.getString(erpCode);
				LazyDynaBean bean = copyToBean(rs);
				if (id == null || id.length() <= 0) {
					erros.append(bean.get("codeitemdesc"));
					erros.append("的部门代码不存在；");
					continue;
				}
				bean.set("COMPANY_CODE".toLowerCase(), "01");
				bean.set("DEPT_CODE".toLowerCase(), id);
				bean.set("DEPT_NAME".toLowerCase(), bean.get("codeitemdesc"));
				bean.set("BUDGET_FLAG".toLowerCase(), "O");
				bean.set("ASSETS_FLAG".toLowerCase(), "Y");
				if ("1".equals(codeValue)) {// 新增
					if (this.isExist(id, "DEPT_CODE", "edm_dept", erpConn)) {
						bean.contains("FLAG".toLowerCase(), "2");
//						if (update(
//								bean,
//								erpConn,
//								"COMPANY_CODE,DEPT_CODE,DEPT_NAME,DEPT_ABV,DEPT_KIND,SHOP_KIND,FLAG",
//								"edm_dept", "DEPT_CODE", id)) {
						if (true) {// 他们不要更新，只要新增
							insertToupdate.append(id);
							insertToupdate.append(",");

							insert.append(id);
							insert.append(",");
						} else {
							insertToupdateErr.append(id);
							insertToupdateErr.append(",");

							erros.append(id);
							erros.append(",");
						}
					} else {
						bean.contains("FLAG".toLowerCase(), "1");
						if (insert(
								bean,
								erpConn,
								"COMPANY_CODE,DEPT_CODE,DEPT_NAME,DEPT_ABV,DEPT_KIND,BUDGET_FLAG,ASSETS_FLAG,SHOP_KIND,FLAG",
								"edm_dept")) {
							insert.append(id);
							insert.append(",");
						} else {
							erros.append(id);
							erros.append(",");
						}
					}
				} else if ("2".equals(codeValue)) { // 更新
					if (this.isExist(id, "DEPT_CODE", "edm_dept", erpConn)) {
						bean.contains("FLAG".toLowerCase(), "2");
//						if (update(
//								bean,
//								erpConn,
//								"COMPANY_CODE,DEPT_CODE,DEPT_NAME,DEPT_ABV,DEPT_KIND,SHOP_KIND,FLAG",
//								"edm_dept", "DEPT_CODE", id)) {
						if(true) {// 他们不要更新
							update.append(id);
							update.append(",");
						} else {
							erros.append(id);
							erros.append(",");
						}
					} else {
						bean.contains("FLAG".toLowerCase(), "1");
						if (insert(
								bean,
								erpConn,
								"COMPANY_CODE,DEPT_CODE,DEPT_NAME,DEPT_ABV,DEPT_KIND,BUDGET_FLAG,ASSETS_FLAG,SHOP_KIND,FLAG",
								"edm_dept")) {
							update.append(id);
							update.append(",");

							updateToinsert.append(id);
							updateToinsert.append(",");
						} else {
							updateToinsertErr.append(id);
							updateToinsertErr.append(",");

							erros.append(id);
							erros.append(",");
						}
					}
				} else if ("3".equals(codeValue)) { // 删除
					if (this.isExist(id, "DEPT_CODE", "edm_dept", erpConn)) {
						bean.contains("FLAG".toLowerCase(), "3");
						if (update(bean, erpConn, "FLAG", "edm_dept",
								"DEPT_CODE", id)) {
							delete.append(id);
							delete.append(",");
						} else {
							erros.append(id);
							erros.append(",");
						}
					} else {
						bean.contains("FLAG".toLowerCase(), "3");
						if (insert(
								bean,
								erpConn,
								"COMPANY_CODE,DEPT_CODE,DEPT_NAME,DEPT_ABV,DEPT_KIND,BUDGET_FLAG,ASSETS_FLAG,SHOP_KIND,FLAG",
								"edm_dept")) {
							delete.append(id);
							delete.append(",");

							deleteToinsert.append(id);
							deleteToinsert.append(",");
						} else {
							deleteToinsertErr.append(id);
							deleteToinsertErr.append(",");

							erros.append(id);
							erros.append(",");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			erros.append(e.getMessage());

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		map.put("xml", xml.toString());
		if (update.length() > 0) {
			map.put("update", update.substring(0, update.length() - 1));
		} else {
			map.put("update", "");
		}

		if (erros.length() > 0) {
			map.put("erro", erros.substring(0, erros.length() - 1));
		} else {
			map.put("erro", "");
		}

		if (insert.length() > 0) {
			map.put("insert", insert.substring(0, insert.length() - 1));
		} else {
			map.put("insert", "");
		}

		if (delete.length() > 0) {
			map.put("delete", delete.substring(0, delete.length() - 1));
		} else {
			map.put("delete", "");
		}

		if (updateToinsert.length() > 0) {
			map.put("updateToinsert", updateToinsert.substring(0,
					updateToinsert.length() - 1));
		} else {
			map.put("updateToinsert", "");
		}

		if (insertToupdate.length() > 0) {
			map.put("insertToupdate", insertToupdate.substring(0,
					insertToupdate.length() - 1));
		} else {
			map.put("insertToupdate", "");
		}

		if (deleteToinsert.length() > 0) {
			map.put("deleteToinsert", deleteToinsert.substring(0,
					deleteToinsert.length() - 1));
		} else {
			map.put("deleteToinsert", "");
		}

		if (updateToinsertErr.length() > 0) {
			map.put("updateToinsertErr", updateToinsertErr.substring(0,
					updateToinsertErr.length() - 1));
		} else {
			map.put("updateToinsertErr", "");
		}

		if (insertToupdateErr.length() > 0) {
			map.put("insertToupdateErr", insertToupdateErr.substring(0,
					insertToupdateErr.length() - 1));
		} else {
			map.put("insertToupdateErr", "");
		}

		if (deleteToinsertErr.length() > 0) {
			map.put("deleteToinsertErr", deleteToinsertErr.substring(0,
					deleteToinsertErr.length() - 1));
		} else {
			map.put("deleteToinsertErr", "");
		}

		return getXml(map, "org");
	}

	/**
	 * 同步人员
	 * 
	 * @param ehrConn
	 * @param erpConn
	 * @param ehrTable
	 * @return
	 */
	public Map syncEmp(Connection ehrConn, Connection erpConn, String ehrTable,
			String erpCode, String dbType, String condition) {
		Map map = new HashMap();
		StringBuffer xml = new StringBuffer();

		StringBuffer sql = new StringBuffer();

		sql.append("select * from ");
		sql.append(ehrTable);
		sql.append(" where ");
		sql.append(condition);
		sql.append(" and ");
		sql.append(Sql_switcher.isnull("e0127", "'-1'"));
		sql.append("<>'-1'");
		if ("mssql".equalsIgnoreCase(dbType)) {
			sql.append(" and e0127<>''");
		}

		// 更新的记录id
		StringBuffer update = new StringBuffer();
		// 新增的记录id
		StringBuffer insert = new StringBuffer();
		// 删除的记录id
		StringBuffer delete = new StringBuffer();

		// 本该执行更新，当做的是插入操作
		StringBuffer updateToinsert = new StringBuffer();
		// 本该新增，但执行的是更新
		StringBuffer insertToupdate = new StringBuffer();
		// 本该删除，但执行的是插入
		StringBuffer deleteToinsert = new StringBuffer();

		// 本该执行更新，当做的是插入操作
		StringBuffer updateToinsertErr = new StringBuffer();
		// 本该新增，但执行的是更新
		StringBuffer insertToupdateErr = new StringBuffer();
		// 本该删除，但执行的是插入
		StringBuffer deleteToinsertErr = new StringBuffer();

		StringBuffer erros = new StringBuffer();

		ContentDAO ehrDao = new ContentDAO(ehrConn);
		RowSet rs = null;
		try {
			rs = ehrDao.search(sql.toString());
			while (rs.next()) {
				// 获得唯一字段的值
				String id = rs.getString("e0127");
				// 系统代号
				String codeValue = rs.getString(erpCode);
				LazyDynaBean idBean = this
						.getBean(
								"select to_char(max(to_number(user_unique_id)) +1) id from edm_address",
								erpConn);
				LazyDynaBean bean = copyToBean(rs);
				bean.set("COMPANY_CODE".toLowerCase(), "01");
				String user_id = (String) idBean.get("id");
				if(idBean.get("id") != null && user_id.length() > 0) {
					bean.set("USER_UNIQUE_ID".toLowerCase(), idBean.get("id"));
				} else {
					bean.set("USER_UNIQUE_ID".toLowerCase(), "1");
				}
				bean.set("ACCOUNT_ID".toLowerCase(), id);
				
				if (bean.get("a0101") == null) {
					bean.set("ACCOUNT_NAME".toLowerCase(), "");
				} else {
					bean.set("ACCOUNT_NAME".toLowerCase(), bean.get("a0101"));
				}
				bean.set("PERSON_CODE".toLowerCase(), id);
				
				if (bean.get("a0101") == null) { 
					bean.set("PERSON_NAME".toLowerCase(), "");
				} else {
					bean.set("PERSON_NAME".toLowerCase(), bean.get("a0101"));
				}

				idBean = this
						.getBean(
								"select corcode,codeitemdesc,dept_abv from t_org_view where b0110_0 in (select e0122_0 from "
										+ ehrTable
										+ " where e0127='"
										+ id
										+ "')", ehrConn);
				if (idBean.get("corcode") == null) {
					erros.append(bean.get("a0101"));
					erros.append("没有部门;");
					continue;
				} else {
					if (!this.isExist((String) idBean.get("corcode"), "DEPT_CODE", "edm_dept", erpConn)){
						erros.append(bean.get("a0101"));
						erros.append("所在部门在erp中不存在;");
						continue;
					}
					bean.set("DEPT_CODE".toLowerCase(), idBean.get("corcode"));
				}
				if (idBean.get("codeitemdesc") == null) {
					bean.set("DEPT_NAME".toLowerCase(), "");
				} else {
					//bean.set("DEPT_NAME".toLowerCase(), idBean.get("codeitemdesc"));
					bean.set("DEPT_NAME".toLowerCase(), idBean.get("dept_abv"));
					
				}
				bean.set("VALID_ID".toLowerCase(), "Y");
				bean.set("STAFF_ID".toLowerCase(), "Y");
				bean.set("GUEST_ID".toLowerCase(), "N");
				bean.set("BUDGET_FLAG".toLowerCase(), "O");

				if ("1".equals(codeValue)) {// 新增
					if (this.isExist(id, "ACCOUNT_ID", "edm_address", erpConn)) {
						bean.contains("FLAG".toLowerCase(), "2");
						erpConn.setAutoCommit(false);
						try {
							if (update(bean, erpConn,
									"ACCOUNT_NAME,DEPT_CODE,DEPT_NAME,FLAG",
									"edm_address", "ACCOUNT_ID", id)
									&& update(
											bean,
											erpConn,
											"PERSON_NAME,DEPT_CODE,DEPT_NAME,FLAG",
											"EDM_STAFF", "PERSON_CODE", id)) {

								insertToupdate.append(id);
								insertToupdate.append(",");

								insert.append(id);
								insert.append(",");
							} else {
								insertToupdateErr.append(id);
								insertToupdateErr.append(",");

								erros.append(id);
								erros.append(",");
							}
							erpConn.commit();
						} catch (Exception e) {
							e.printStackTrace();
							erpConn.rollback();
						} finally {
							erpConn.setAutoCommit(true);
						}
					} else {
						bean.contains("FLAG".toLowerCase(), "1");
						erpConn.setAutoCommit(false);
						try {
							if (insert(
									bean,
									erpConn,
									"COMPANY_CODE,USER_UNIQUE_ID,ACCOUNT_ID,ACCOUNT_NAME,DEPT_CODE,VALID_ID,STAFF_ID,GUEST_ID,DEPT_NAME,BUDGET_FLAG,FLAG",
									"edm_address") && insert(
											bean,
											erpConn,
											"COMPANY_CODE,PERSON_CODE,PERSON_NAME,USER_UNIQUE_ID,DEPT_CODE,DEPT_NAME,FLAG",
											"EDM_STAFF")) {
								insert.append(id);
								insert.append(",");
							} else {
								erros.append(id);
								erros.append(",");
							}
							
							erpConn.commit();
						} catch (Exception e) {
							e.printStackTrace();
							erpConn.rollback();
						} finally {
							erpConn.setAutoCommit(true);
						}
					}
				} else if ("2".equals(codeValue)) { // 更新
					if (this.isExist(id, "ACCOUNT_ID", "edm_address", erpConn)) {
						bean.contains("FLAG".toLowerCase(), "2");
						erpConn.setAutoCommit(false);
						try {
							if (update(bean, erpConn,
									"ACCOUNT_NAME,DEPT_CODE,DEPT_NAME,FLAG",
									"edm_address", "ACCOUNT_ID", id)
									&& update(
											bean,
											erpConn,
											"PERSON_NAME,DEPT_CODE,DEPT_NAME,FLAG",
											"EDM_STAFF", "PERSON_CODE", id)) {
								update.append(id);
								update.append(",");
							} else {
								erros.append(id);
								erros.append(",");
							}
							
							erpConn.commit();
						} catch (Exception e) {
							e.printStackTrace();
							erpConn.rollback();
						} finally {
							erpConn.setAutoCommit(true);
						}
					} else {
						bean.contains("FLAG".toLowerCase(), "1");
						erpConn.setAutoCommit(false);
						try {
							if (insert(
									bean,
									erpConn,
									"COMPANY_CODE,USER_UNIQUE_ID,ACCOUNT_ID,ACCOUNT_NAME,DEPT_CODE,VALID_ID,STAFF_ID,GUEST_ID,DEPT_NAME,BUDGET_FLAG,FLAG",
									"edm_address") && insert(
											bean,
											erpConn,
											"COMPANY_CODE,PERSON_CODE,PERSON_NAME,USER_UNIQUE_ID,DEPT_CODE,DEPT_NAME,FLAG",
											"EDM_STAFF")) {
								update.append(id);
								update.append(",");
	
								updateToinsert.append(id);
								updateToinsert.append(",");
							} else {
								updateToinsertErr.append(id);
								updateToinsertErr.append(",");
	
								erros.append(id);
								erros.append(",");
							}
							
							erpConn.commit();
						} catch (Exception e) {
							e.printStackTrace();
							erpConn.rollback();
						} finally {
							erpConn.setAutoCommit(true);
						}
					}
				} else if ("3".equals(codeValue)) { // 删除
					if (this.isExist(id, "ACCOUNT_ID", "edm_address", erpConn)) {
						bean.contains("FLAG".toLowerCase(), "3");
						erpConn.setAutoCommit(false);
						try {
							if (update(bean, erpConn, "FLAG", "edm_address", "ACCOUNT_ID", id) && update(
									bean,
									erpConn,
									"FLAG",
									"EDM_STAFF", "PERSON_CODE", id)) {
								delete.append(id);
								delete.append(",");
							} else {
								erros.append(id);
								erros.append(",");
							}
							erpConn.commit();
						} catch (Exception e) {
							e.printStackTrace();
							erpConn.rollback();
						} finally {
							erpConn.setAutoCommit(true);
						}
					} else {
						bean.contains("FLAG".toLowerCase(), "3");
						erpConn.setAutoCommit(false);
						try {
							if (insert(
									bean,
									erpConn,
									"COMPANY_CODE,USER_UNIQUE_ID,ACCOUNT_ID,ACCOUNT_NAME,DEPT_CODE,VALID_ID,STAFF_ID,GUEST_ID,DEPT_NAME,BUDGET_FLAG,FLAG",
									"edm_address") && insert(
											bean,
											erpConn,
											"COMPANY_CODE,PERSON_CODE,PERSON_NAME,USER_UNIQUE_ID,DEPT_CODE,DEPT_NAME,FLAG",
											"EDM_STAFF")) {
								delete.append(id);
								delete.append(",");
	
								deleteToinsert.append(id);
								deleteToinsert.append(",");
							} else {
								deleteToinsertErr.append(id);
								deleteToinsertErr.append(",");
	
								erros.append(id);
								erros.append(",");
							}
							
							erpConn.commit();
						} catch (Exception e) {
							e.printStackTrace();
							erpConn.rollback();
						} finally {
							erpConn.setAutoCommit(true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			erros.append(e.getMessage());

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		map.put("xml", xml.toString());
		if (update.length() > 0) {
			map.put("update", update.substring(0, update.length() - 1));
		} else {
			map.put("update", "");
		}

		if (erros.length() > 0) {
			map.put("erro", erros.substring(0, erros.length() - 1));
		} else {
			map.put("erro", "");
		}

		if (insert.length() > 0) {
			map.put("insert", insert.substring(0, insert.length() - 1));
		} else {
			map.put("insert", "");
		}

		if (delete.length() > 0) {
			map.put("delete", delete.substring(0, delete.length() - 1));
		} else {
			map.put("delete", "");
		}

		if (updateToinsert.length() > 0) {
			map.put("updateToinsert", updateToinsert.substring(0,
					updateToinsert.length() - 1));
		} else {
			map.put("updateToinsert", "");
		}

		if (insertToupdate.length() > 0) {
			map.put("insertToupdate", insertToupdate.substring(0,
					insertToupdate.length() - 1));
		} else {
			map.put("insertToupdate", "");
		}

		if (deleteToinsert.length() > 0) {
			map.put("deleteToinsert", deleteToinsert.substring(0,
					deleteToinsert.length() - 1));
		} else {
			map.put("deleteToinsert", "");
		}

		if (updateToinsertErr.length() > 0) {
			map.put("updateToinsertErr", updateToinsertErr.substring(0,
					updateToinsertErr.length() - 1));
		} else {
			map.put("updateToinsertErr", "");
		}

		if (insertToupdateErr.length() > 0) {
			map.put("insertToupdateErr", insertToupdateErr.substring(0,
					insertToupdateErr.length() - 1));
		} else {
			map.put("insertToupdateErr", "");
		}

		if (deleteToinsertErr.length() > 0) {
			map.put("deleteToinsertErr", deleteToinsertErr.substring(0,
					deleteToinsertErr.length() - 1));
		} else {
			map.put("deleteToinsertErr", "");
		}

		return getXml(map, "emp");
	}

	/**
	 * 根据sql获得Bean
	 * 
	 * @param sql
	 * @param conn
	 * @return
	 */
	private LazyDynaBean getBean(String sql, Connection conn) {
		ContentDAO dao = new ContentDAO(conn);
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				bean = this.copyToBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return bean;
	}

	/**
	 * 将map转为xml
	 * 
	 * @param map
	 * @param type
	 * @return
	 */
	private Map getXml(Map map, String type) {
		StringBuffer org = new StringBuffer();
		StringBuffer oth = new StringBuffer();
		Map rMap = new HashMap();
		int count = 0;
		// 新增的id
		String insert = (String) map.get("insert");
		if (insert.length() > 0) {
			org.append("<rec flag='1'>");
			org.append(insert);
			org.append("</rec>");
			count += insert.split(",").length;
		}

		// 更新
		String update = (String) map.get("update");
		if (update.length() > 0) {
			org.append("<rec flag='2'>");
			org.append(update);
			org.append("</rec>");
			count += update.split(",").length;
		}

		// 删除
		String delete = (String) map.get("delete");
		if (delete.length() > 0) {
			org.append("<rec flag='3'>");
			org.append(delete);
			org.append("</rec>");
			count += delete.split(",").length;
		}

		String insertToupdate = (String) map.get("insertToupdate");
		if (insertToupdate.length() > 0) {
			String str[] = insertToupdate.split(",");
			for (int i = 0; i < str.length; i++) {
				oth.append("<elem id='");
				oth.append(str[i]);
				oth.append("' flag='1' pass='1'>");
				if ("emp".equalsIgnoreCase(type)) {
					oth.append("该人员本系统已存在，现执行的是修改操作");
				} else {
					oth.append("该部门或单位系统已存在，现执行的是修改操作");
				}
				oth.append("</elem >");
			}
		}

		String insertToupdateErr = (String) map.get("insertToupdateErr");
		if (insertToupdateErr.length() > 0) {
			String[] str = insertToupdateErr.split(",");
			for (int i = 0; i < str.length; i++) {
				oth.append("<elem id='");
				oth.append(str[i]);
				oth.append("' flag='1' pass='0'>");
				if ("emp".equalsIgnoreCase(type)) {
					oth.append("该人员本系统已存在，现执行的是修改操作,但执行失败");
				} else {
					oth.append("该部门或单位系统已存在，现执行的是修改操作,但执行失败");
				}
				oth.append("</elem >");
			}
		}

		String updateToinsert = (String) map.get("updateToinsert");
		if (updateToinsert.length() > 0) {
			String str[] = updateToinsert.split(",");
			for (int i = 0; i < str.length; i++) {
				oth.append("<elem id='");
				oth.append(str[i]);
				oth.append("' flag='2' pass='1'>");
				if ("emp".equalsIgnoreCase(type)) {
					oth.append("该人员本系统不存在，现执行的是插入操作");
				} else {
					oth.append("该人员本系统不存在，现执行的是插入操作");
				}
				oth.append("</elem >");
			}
		}

		String updateToinsertErr = (String) map.get("updateToinsertErr");
		if (updateToinsertErr.length() > 0) {
			String[] str = updateToinsertErr.split(",");
			for (int i = 0; i < str.length; i++) {
				oth.append("<elem id='");
				oth.append(str[i]);
				oth.append("' flag='2' pass='0'>");
				if ("emp".equalsIgnoreCase(type)) {
					oth.append("该人员本系统不存在，现执行的是插入操作,但执行失败");
				} else {
					oth.append("该人员本系统不存在，现执行的是插入操作,但执行失败");
				}
				oth.append("</elem >");
			}
		}

		String deleteToinsert = (String) map.get("deleteToinsert");
		if (deleteToinsert.length() > 0) {
			String str[] = deleteToinsert.split(",");
			for (int i = 0; i < str.length; i++) {
				oth.append("<elem id='");
				oth.append(str[i]);
				oth.append("' flag='3' pass='1'>");
				if ("emp".equalsIgnoreCase(type)) {
					oth.append("该人员本系统不存在，现执行的是插入操作");
				} else {
					oth.append("该人员本系统不存在，现执行的是插入操作");
				}
				oth.append("</elem >");
			}
		}

		String deleteToinsertErr = (String) map.get("deleteToinsertErr");
		if (deleteToinsertErr.length() > 0) {
			String[] str = deleteToinsertErr.split(",");
			for (int i = 0; i < str.length; i++) {
				oth.append("<elem id='");
				oth.append(str[i]);
				oth.append("' flag='3' pass='0'>");
				if ("emp".equalsIgnoreCase(type)) {
					oth.append("该人员本系统不存在，现执行的是插入操作,但执行失败");
				} else {
					oth.append("该人员本系统不存在，现执行的是插入操作,但执行失败");
				}
				oth.append("</elem >");
			}
		}

		rMap.put("org", org.toString());
		rMap.put("oth", oth.toString());
		rMap.put("count", Integer.valueOf(count));
		rMap.put("erro", (String) map.get("erro")); 
		return rMap;
	}

	/**
	 * 查询唯一值是否存在
	 * 
	 * @param id
	 * @param idField
	 * @param table
	 * @param conn
	 * @return
	 */
	private boolean isExist(String idValue, String idField, String table,
			Connection conn) {

		boolean flag = false;

		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append(idField);
		sql.append(" from ");
		sql.append(table);
		sql.append(" where ");
		sql.append(idField);
		sql.append("='");
		sql.append(idValue);
		sql.append("'");

		ContentDAO dao = new ContentDAO(conn);

		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return flag;

	}

	/**
	 * 将数据封装到bean中
	 * 
	 * @param rs
	 * @return
	 */
	private LazyDynaBean copyToBean(RowSet rs) {
		LazyDynaBean bean = new LazyDynaBean();
		try {
			int count = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= count; i++) {
				String colName = rs.getMetaData().getColumnName(i);
				Object obj = rs.getObject(colName);
				if (obj == null) {
					bean.set(colName.toLowerCase(), "");
				} else {
					bean.set(colName.toLowerCase(), obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bean;
	}

	/**
	 * 新增
	 * 
	 * @param bean
	 * @param conn
	 * @return
	 */
	private boolean insert(LazyDynaBean bean, Connection conn, String col,
			String table) {
		boolean flag = false;

		StringBuffer sql = new StringBuffer();

		ArrayList list = new ArrayList();

		String[] cols = col.split(",");

		sql.append("insert into ");
		sql.append(table);
		sql.append("(");
		sql.append(col);
		sql.append(")");
		sql.append(" values(");

		for (int i = 0; i < cols.length; i++) {
			sql.append("?,");
			list.add(bean.get(cols[i].toLowerCase()));
		}

		sql.deleteCharAt(sql.length() - 1);

		sql.append(")");

		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.insert(sql.toString(), list);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;

	}

	/**
	 * 更新
	 * 
	 * @param bean
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private boolean update(LazyDynaBean bean, Connection conn, String col,
			String table, String idField, String idValue) throws SQLException {
		boolean flag = false;

		StringBuffer sql = new StringBuffer();

		ArrayList list = new ArrayList();

		String[] cols = col.split(",");

		sql.append("update ");
		sql.append(table);
		sql.append(" set ");

		for (int i = 0; i < cols.length; i++) {
			sql.append(cols[i]);
			sql.append("=");
			sql.append("?,");
			list.add(bean.get(cols[i].toLowerCase()));
		}

		sql.deleteCharAt(sql.length() - 1);

		sql.append(" where ");
		sql.append(idField);
		sql.append("=");
		sql.append("?");
		list.add(idValue);

		ContentDAO dao = new ContentDAO(conn);
		dao.update(sql.toString(), list);
		flag = true;

		return flag;

	}

	/**
	 * 删除
	 * 
	 * @param bean
	 * @param conn
	 * @return
	 */
	private boolean delete(Connection conn, String table, String idField,
			String idValue, String statusField) {
		boolean flag = false;

		StringBuffer sql = new StringBuffer();

		ArrayList list = new ArrayList();

		sql.append("update ");
		sql.append(table);
		sql.append(" set ");

		sql.append(statusField);
		sql.append("=");
		sql.append("?");
		list.add("3");

		sql.append(" where ");
		sql.append(idField);
		sql.append("=");
		sql.append("?");
		list.add(idValue);

		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql.toString(), list);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;

	}
	
}
