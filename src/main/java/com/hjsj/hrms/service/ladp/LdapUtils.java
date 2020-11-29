package com.hjsj.hrms.service.ladp;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.jdom.Element;

import javax.naming.directory.DirContext;
import javax.sql.RowSet;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class LdapUtils {

	//配置文件名
	private String xmlName = "AD";
	// 是否存在AD.xml文件
	private boolean isExistXML = false;
	// Ad.xml文件的路径
	private String xmlPath = "";
	// ad参数
	private LdapParam LdapParam = new LdapParam();
	
	private String tempTable;
	private String org_table;
	private String post_table;
	private String hr_table;
	// 调用log4j的日志，用于输出
	private static Category log = Category.getInstance(Env.class.getName());
	
	// 最大记录
	private int maxCout = 0;

	public LdapUtils() {
		this(null);
	}
	
	/**
	 * 初始化类
	 * @param xmlName 文件名
	 */
	public LdapUtils(String xmlName) {
		if(xmlName!=null)
			this.xmlName = xmlName;
		File file = getFile();
		if (this.isExistXML) {
			// 读取并设置参数，将xml中的参数保存到LdapParam对象中
			setParam(file);
		} else {
			log.error("LDAP连接创建失败，原因：未找到"+xmlName+".xml文件");
		}
	}

	
	private File getFile() {
		// 类路径，Ad.xml文件放到该路径下
		String classPath = System.getProperty("java.class.path");
		// 路径分割符号
		String sep = System.getProperty("path.separator");

		String[] path = classPath.split(sep);
		File file = null;
		for (int i = 0; i < path.length; i++) {
			file = new File(path[i], xmlName+".xml");
			if (file.exists()) {
				xmlPath = path[i];
				isExistXML = true;
				break;
			}
		}
		return file;
	}
	/**
	 * 获取AD服务器连接，如果存在AD.xml文件，则读取xml文件配置; 如果没有，则读取properties文件中的配置
	 * 
	 * @return
	 */
	public DirContext getADConn(String root) {
		DirContext adConn = null;
		
		File file = getFile();
		if (this.isExistXML) {
			
			PareXmlUtils utils = new PareXmlUtils(file);
			// 是否需要修改密码
			String isModifypwd = utils.getAttributeValue(
					"/sync/params/modifypwd", "is");
			String ipAddress = utils.getTextValue("/sync/params/ldapipadress");// ip地址
			String port = utils.getTextValue("/sync/params/ldapport");// 端口
			String username = utils.getTextValue("/sync/params/ldapaccount");// 用户名
			String password = utils.getTextValue("/sync/params/ldappassword"); // 密码
			if (root == null || root.length() <= 0) {
				root = utils.getTextValue("/sync/params/ldaproot"); // 根节点
			}
			String certficationPath = utils
					.getTextValue("/sync/params/certficationpath");// 证书路径
			String certficationPwd = utils
					.getTextValue("/sync/params/certficationpwd"); // 导入密码

			// Ldap工厂类
			String factory = "com.sun.jndi.ldap.LdapCtxFactory";
			// ldap连接
			String url = "ldap://" + ipAddress + ":" + port + "/" + root;

			Env env = null;
			// ConnFactory factory = new ConnFactory(ipAddress, port, username,
			// password, root, certficationPath, certficationPwd);

			// 根据是否修改密码创建不同的ldap连接，1表示修改密码，0表示不修改密码
			if ("1".equals(isModifypwd)) {
				env = new Env(factory, url, username, password,
						certficationPath, true, certficationPwd);

			} else {
				env = new Env(factory, url, username, password);
			}

			try {
				adConn = LdapOperUtils.getLdapDirContext(env);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("LDAP连接创建失败，失败原因：" + e.getMessage());
			}		

		} else {
			log.error("LDAP连接创建失败，原因：未找到"+xmlName+".xml文件");
		}

		return adConn;
	}

	/**
	 * 设置参数
	 * 
	 * @param file
	 */
	private void setParam(File file) {
		PareXmlUtils utils = new PareXmlUtils(file);

		// 是否同步人员
		String isSyncHr = utils.getAttributeValue("/sync/params/hrpk_ref",
				"sync");
		// 是否同步机构
		String isSyncOrg = utils.getAttributeValue("/sync/params/orgpk_ref",
				"sync");
		// 是否同步岗位
		String isSyncPost = utils.getAttributeValue("/sync/params/postpk_ref",
				"sync");

		this.LdapParam.setSyncHr(isSyncHr);
		this.LdapParam.setSyncOrg(isSyncOrg);
		this.LdapParam.setSyncPost(isSyncPost);
		this.LdapParam.setRootorg(utils.getTextValue("/sync/params/rootorg"));
		this.LdapParam.setCertficationPath(utils
				.getTextValue("/sync/params/certficationpath"));
		this.LdapParam.setCertficationPwd(utils
				.getTextValue("/sync/params/certficationpwd"));
		this.LdapParam.setDefaultPwd(utils.getAttributeValue(
				"/sync/params/modifypwd", "default"));
		this.LdapParam.setDefaultPwdValue(utils.getAttributeValue(
				"/sync/params/modifypwd", "value"));
		//禁用是否在当前节点下
		this.LdapParam.setIsinner(utils.getAttributeValue(
				"/sync/params/del", "isinner"));
		
		//人员同步条件
		this.LdapParam.setHrcondition(utils.getTextValue("/sync/params/hrcondition"));
		//机构同步条件
		this.LdapParam.setOrgcondition(utils.getTextValue("/sync/params/orgcondition"));
		//岗位同步条件
		this.LdapParam.setPostcondition(utils.getTextValue("/sync/params/postcondition"));
		
		//人员转码指标codesetid
		this.LdapParam.setHrtranscoding(utils.getTextValue("/sync/params/hrtranscoding"));
		
		//机构转码指标codeitemid
		this.LdapParam.setOrgtranscoding(utils.getTextValue("/sync/params/orgtranscoding"));
		
		//岗位转码指标codeitemid
		this.LdapParam.setPosttranscoding(utils.getTextValue("/sync/params/hrtranscoding"));

		// 人员指标对应关系
		if ("true".equalsIgnoreCase(isSyncHr)) {
			List fieldList = utils
					.getNodes("/sync/fields_ref/hrfield/field_ref");
			HashMap hrFieldMap = new HashMap();
			for (int i = 0; i < fieldList.size(); i++) {
				Element el = (Element) fieldList.get(i);
				//hrFieldMap.put(el.getAttributeValue("hrfield").toLowerCase(),
						//el.getAttributeValue("adfield"));
				hrFieldMap.put(el.getAttributeValue("adfield").toLowerCase(), el.getAttributeValue("hrfield").toLowerCase());
			}
			this.LdapParam.setHrFieldMap(hrFieldMap);
		}

		// 机构指标对应
		if ("true".equalsIgnoreCase(isSyncOrg)) {
			List fieldList = utils
					.getNodes("/sync/fields_ref/orgfield/field_ref");
			HashMap orgFieldMap = new HashMap();
			for (int i = 0; i < fieldList.size(); i++) {
				Element el = (Element) fieldList.get(i);
				//orgFieldMap.put(el.getAttributeValue("hrfield").toLowerCase(),
						//el.getAttributeValue("adfield"));
				orgFieldMap.put(el.getAttributeValue("adfield").toLowerCase(),
						el.getAttributeValue("hrfield").toLowerCase());
			}
			this.LdapParam.setOrgFieldMap(orgFieldMap);
		}

		// 岗位指标对应
		if ("true".equalsIgnoreCase(isSyncPost)) {
			List fieldList = utils
					.getNodes("/sync/fields_ref/postfield/field_ref");
			HashMap postFieldMap = new HashMap();
			for (int i = 0; i < fieldList.size(); i++) {
				Element el = (Element) fieldList.get(i);
				//postFieldMap.put(el.getAttributeValue("hrfield").toLowerCase(),
						//el.getAttributeValue("adfield"));
				postFieldMap.put(el.getAttributeValue("adfield").toLowerCase(),
						el.getAttributeValue("hrfield").toLowerCase());
			}
			this.LdapParam.setPostFieldMap(postFieldMap);
		}

		this.LdapParam.setHrAdPk(utils.getAttributeValue(
				"/sync/params/hrpk_ref", "adpk"));
		this.LdapParam.setHrPk(utils.getAttributeValue("/sync/params/hrpk_ref",
				"hrpk").toLowerCase());
		this.LdapParam.setHrType(utils.getAttributeValue(
				"/sync/params/hrpk_ref", "type"));
		this.LdapParam.setLdapAccount(utils
				.getTextValue("/sync/params/ldapaccount"));
		this.LdapParam.setLdapCode(utils.getTextValue("/sync/params/ldapcode"));
		this.LdapParam.setLdapPassword(utils
				.getTextValue("/sync/params/ldappassword"));
		this.LdapParam.setLdapIpAddress(utils
				.getTextValue("/sync/params/ldapipadress"));
		this.LdapParam.setLdapPort(utils.getTextValue("/sync/params/ldapport"));
		this.LdapParam.setLdapRoot(utils.getTextValue("/sync/params/ldaproot"));
		this.LdapParam.setModifyPwd(utils.getAttributeValue(
				"/sync/params/modifypwd", "is"));
		this.LdapParam.setOrgAdPk(utils.getAttributeValue(
				"/sync/params/orgpk_ref", "adpk"));
		this.LdapParam.setOrgPk(utils.getAttributeValue(
				"/sync/params/orgpk_ref", "hrpk").toLowerCase());
		this.LdapParam.setOrgType(utils.getAttributeValue(
				"/sync/params/orgpk_ref", "type"));
		this.LdapParam.setOrgNosycnode(utils.getTextValue("/sync/params/orgpk_ref"));

		this.LdapParam.setPostAdPk(utils.getAttributeValue(
				"/sync/params/postpk_ref", "adpk"));
		this.LdapParam.setPostPk(utils.getAttributeValue(
				"/sync/params/postpk_ref", "hrpk").toLowerCase());
		this.LdapParam.setPostType(utils.getAttributeValue(
				"/sync/params/postpk_ref", "type"));

		this.LdapParam.setUserControl(utils
				.getTextValue("/sync/params/usercontrol"));
		this.LdapParam.setUserDel(utils.getTextValue("/sync/params/del"));
		this.LdapParam.setDelUserPath(utils.getAttributeValue("/sync/params/del", "ou"));
		
		String maxnum = utils.getTextValue("/sync/params/maxnum");
		
		if (maxnum == null || maxnum.trim().length() <= 0) {
			maxnum = "-1";
		}
		this.LdapParam.setCountNum(Integer.parseInt(maxnum));
	}


	public Map syncOrg(Connection ehrConn, DirContext adConn, String ehrTable,
			String ldapCode, String dbType, String cond, int flag) {
		ContentDAO ehrDao = new ContentDAO(ehrConn);
		RowSet rs = null;
		// 返回值
		Map returnMap = new HashMap();
		//机构需要转码的指标
		String orgtranscoding = this.LdapParam.getOrgtranscoding();
		String field = this.getFields("B");
		orgtranscoding = getTranscoding(orgtranscoding, field);
		StringBuffer sql = new StringBuffer();
		
		try {
			String rootorg = this.LdapParam.getRootorg();
			String rootSql = "";
			String rootKeys = ",";
			if(rootorg.length()>0){
				String[] orgs = rootorg.split(",");
				rootSql+=" and (1=2 ";
				for(int i=0;i<orgs.length;i++){
					rootSql+=" or b0110_0 like '"+orgs[i]+"%' ";
				}
				rootSql+=") ";
				
				sql.append("select ").append(this.LdapParam.getOrgPk()).append(" pk from ");
				sql.append(ehrTable).append(" where b0110_0 in (");
				for(int i=0;i<orgs.length;i++){
					sql.append("'"+orgs[i]+"',");
				}
				sql.deleteCharAt(sql.length()-1);
				sql.append(" ) ");
				rs = ehrDao.search(sql.toString());
				while(rs.next())
					rootKeys+= rs.getString("pk")+",";
			}
			
		
			sql.setLength(0);
			sql.append("select ");
			sql.append(this.getFields("B"));
			sql.append(",");
			sql.append(ldapCode);
			if (!"unique_id".equalsIgnoreCase(this.LdapParam.getOrgPk())) {
				sql.append(",");
				sql.append("unique_id");
			}
			sql.append(" from ");
			sql.append(ehrTable);
			sql.append(" where ");
			// sql.append(condition);
			if (flag == this.LdapParam.DELETE) {
				sql.append(ldapCode);
				sql.append("=3 ");
	
				// 添加设置的过滤条件
				if (cond != null && cond.trim().length() > 0) {
					sql.append(" and (");
					sql.append(cond);
					sql.append(") ");
				}
				
				//添加根节点验证
				sql.append(rootSql);
				
				sql.append(" order by grade desc ");
			} else {
				sql.append(ldapCode);
				sql.append("<>0 and ");
				sql.append(ldapCode);
				sql.append("<>3 ");
	
				// 添加设置的过滤条件
				if (cond != null && cond.trim().length() > 0) {
					sql.append(" and (");
					sql.append(cond);
					sql.append(") ");
				}
				
				//添加根节点验证
				sql.append(rootSql);
				
				sql.append(" order by b0110_0,a0000");
			}
	
			// 更新的记录id
			ArrayList update = new ArrayList();
			// 新增的记录id
			ArrayList insert = new ArrayList();
			// 删除的记录id
			ArrayList delete = new ArrayList();
	
			// 本该执行更新，但执行的是插入操作
			ArrayList updateToinsert = new ArrayList();
			// 本该新增，但执行的是更新
			ArrayList insertToupdate = new ArrayList();
			// 本该删除，但执行的是插入
			ArrayList deleteToinsert = new ArrayList();
	
			ArrayList errosList = new ArrayList();
	
			// 父节点
			Map map = getOrgParentKey(ldapCode, ehrConn,ehrTable);
		
			rs = ehrDao.search(sql.toString());
			while (rs.next()) {
				
				maxCout ++;
				
				if (this.LdapParam.getCountNum() != -1 && maxCout > this.LdapParam.getCountNum()) {
					break;
				}
				
				String unique_id = "";
				String name = "";
				try {
					
					/*if (!"unique_id".equalsIgnoreCase(this.LdapParam.getPostPk()) {
						unique_id = rs.getString("unique_id");
					} else {
						unique_id = rs.getString(this.LdapParam.getPostAdPk());
					}*/
					unique_id = rs.getString(this.LdapParam.getOrgAdPk());
					// 获得唯一字段的值
					String id = rs.getString(this.LdapParam.getOrgAdPk());
					String parentId = (String) map.get(id);
					name = rs.getString("ou");
					if (id == null || id.length() <= 0) {
						continue;
					}

					// 父节点是否存在，如果不存在，认为是根节点
					if (!isExistOrg(parentId, this.LdapParam.getOrgAdPk(),
							adConn)) {
						//如果是设置的根节点，parentid为自己 guodd
						if(rootKeys.indexOf(","+id+",")!=-1)
							parentId = id;
						
						if (id.equals(parentId)) {
							Map attMap = getAttMap(ehrConn, ehrTable, "B",
									this.LdapParam.getOrgPk() + "=" + "'"
											+ parentId + "'");
							String ldap = (String) attMap.get(this.LdapParam.getLdapCode().toLowerCase());
							
								if (addContext(adConn, "B", "OU=" + name,attMap)) {
									if ("1".equals(ldap)) {
										insert.add(unique_id);
									} else {
										updateToinsert.add(unique_id);
									}
								} else {
									log.error("机构添加失败----名称：" + name + "ID:" + unique_id);
									errosList.add(id);
								}
//							LdapOperUtils.addContext(adConn, "OU=" + name,
//									attMap);
						} else {
							log.error("父节点不存在，无法进行操作！");
							errosList.add(id);
						}

						continue;
					}

					// 更新标志
					String ldapFlag = rs.getString(ldapCode);
					Map attMap = getAttMapByRS(rs, "B", orgtranscoding);
					if ("1".equals(ldapFlag)) {// 新增
						if (isExistOrg(id, this.LdapParam.getOrgAdPk(), adConn)) {// 存在此机构，需要更新
							if (updateContext(adConn, "B", attMap, parentId,
									"B")) {
								insertToupdate.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						} else {
							if (addContext(adConn, "B", getNewName(adConn, "B",
									attMap, parentId, "B"), attMap)) {
								insert.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						}

					} else if ("2".equals(ldapFlag)) {// 更新
						if (isExistOrg(id, this.LdapParam.getOrgAdPk(), adConn)) {// 存在此机构，需要更新
							if (updateContext(adConn, "B", attMap, parentId,
									"B")) {
								update.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						} else {
							if (addContext(adConn, "B", getNewName(adConn, "B",
									attMap, parentId, "B"), attMap)) {
								updateToinsert.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						}
					} else if ("3".equals(ldapFlag)) {// 删除
						if (isExistOrg(id, this.LdapParam.getOrgAdPk(), adConn)) {// 存在此机构，需要删除
							if (deleteContext(adConn, id, "B")) {
								delete.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						} else {
							delete.add(unique_id);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("机构删除失败-----名称：" + name + "---ID：" + unique_id);
				}
			}

			// 保存处理的id
			returnMap.put("insertToupdate", insertToupdate);
			returnMap.put("insert", insert);
			returnMap.put("update", update);
			returnMap.put("updateToinsert", updateToinsert);
			returnMap.put("delete", delete);
			returnMap.put("errosList", errosList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnMap;
	}

	/**
	 * 同步岗位
	 * 
	 * @param ehrConn
	 * @param adConn
	 * @param ehrTable
	 * @param ldapCode
	 * @param dbType
	 * @param cond
	 * @param flag
	 * @return
	 */
	public Map syncPost(Connection ehrConn, DirContext adConn, String ehrTable,
			String ldapCode, String dbType, String cond, int flag) {
		// 返回值
		Map returnMap = new HashMap();
		//岗位需要转码的指标
		String posttranscoding = this.LdapParam.getPosttranscoding();
		String field = this.getFields("B");
		posttranscoding = getTranscoding(posttranscoding, field);
		StringBuffer sql = new StringBuffer();

		sql.append("select ");
		sql.append(this.getFields("K"));
		sql.append(",");
		sql.append(ldapCode);
		if (!"unique_id".equalsIgnoreCase(this.LdapParam.getPostPk())) {
			sql.append(",");
			sql.append("unique_id");
		}
		sql.append(" from ");
		sql.append(ehrTable);
		sql.append(" where ");
		// sql.append(condition);
		if (flag == this.LdapParam.DELETE) {
			sql.append(ldapCode);
			sql.append("=3 ");

			// 添加设置的过滤条件
			if (cond != null && cond.trim().length() > 0) {
				sql.append(" and (");
				sql.append(cond);
				sql.append(") ");
			}
			sql.append(" order by grade desc ");
		} else {
			sql.append(ldapCode);
			sql.append("<>0 and ");
			sql.append(ldapCode);
			sql.append("<>3 ");

			// 添加设置的过滤条件
			if (cond != null && cond.trim().length() > 0) {
				sql.append(" and (");
				sql.append(cond);
				sql.append(") ");
			}
			sql.append(" order by e01a1_0 ");
		}

		// 更新的记录id
		ArrayList update = new ArrayList();
		// 新增的记录id
		ArrayList insert = new ArrayList();
		// 删除的记录id
		ArrayList delete = new ArrayList();

		// 本该执行更新，但执行的是插入操作
		ArrayList updateToinsert = new ArrayList();
		// 本该新增，但执行的是更新
		ArrayList insertToupdate = new ArrayList();
		// 本该删除，但执行的是插入
		ArrayList deleteToinsert = new ArrayList();

		ArrayList errosList = new ArrayList();

		// 父节点
		Map map = getPostParentKey(ldapCode, ehrConn,ehrTable);
		ContentDAO ehrDao = new ContentDAO(ehrConn);
		RowSet rs = null;
		try {
			rs = ehrDao.search(sql.toString());
			while (rs.next()) {
				
				maxCout ++;
				
				if (this.LdapParam.getCountNum() != -1 && maxCout > this.LdapParam.getCountNum()) {
					break;
				}
				
				String unique_id = "";
				String name = "";
				try {
			
					/*if (!"unique_id".equalsIgnoreCase(this.LdapParam.getPostPk())) {
						unique_id = rs.getString("unique_id");
					} else {
						unique_id = rs.getString(this.LdapParam.getPostAdPk());
					}*/
					unique_id = rs.getString(this.LdapParam.getPostAdPk());	
				// 获得唯一字段的值
				String id = rs.getString(this.LdapParam.getPostAdPk());
				String parentId = (String) map.get(id);
				name = rs.getString("ou");
				if (id == null || id.length() <= 0) {
					continue;
				}

				// 父节点是否存在，如果不存在，认为是根节点
				if (!isExistOrg(parentId, this.LdapParam.getOrgAdPk(), adConn)) {

					log.error("父节点不存在，无法进行操作！");
					errosList.add(id);

					continue;
				}

				// 更新标志
				String ldapFlag = rs.getString(ldapCode);
				// 属性集合
				Map attMap = getAttMapByRS(rs, "K" , posttranscoding);

				if ("1".equals(ldapFlag)) {// 新增
					if (isExistOrg(id, this.LdapParam.getPostAdPk(), adConn)) {// 存在此机构，需要更新
						if (updateContext(adConn, "K", attMap, parentId, "B")) {
							insertToupdate.add(unique_id);
						} else {
							errosList.add(unique_id);
						}
					} else {
						if (addContext(adConn, "K", getNewName(adConn, "K",
								attMap, parentId, "B"), attMap)) {
							insert.add(unique_id);
						} else {
							errosList.add(unique_id);
						}
					}

				} else if ("2".equals(ldapFlag)) {// 更新
					if (isExistOrg(id, this.LdapParam.getPostAdPk(), adConn)) {// 存在此机构，需要更新
						if (updateContext(adConn, "K", attMap, parentId, "B")) {
							update.add(unique_id);
						} else {
							errosList.add(unique_id);
						}
					} else {
						if (addContext(adConn, "K", getNewName(adConn, "K",
								attMap, parentId, "B"), attMap)) {
							updateToinsert.add(unique_id);
						} else {
							errosList.add(unique_id);
						}
					}
				} else if ("3".equals(ldapFlag)) {// 删除
					if (isExistOrg(id, this.LdapParam.getPostAdPk(), adConn)) {// 存在此机构，需要删除
						if (deleteContext(adConn, id, "K")) {
							delete.add(unique_id);
						} else {
							errosList.add(unique_id);
						}
					} else {
						delete.add(unique_id);
					}
				}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("岗位同步失败----名称:" + name + "---ID：" + unique_id);
				}
				
			}

			// 保存处理的id
			returnMap.put("insertToupdate", insertToupdate);
			returnMap.put("insert", insert);
			returnMap.put("update", update);
			returnMap.put("updateToinsert", updateToinsert);
			returnMap.put("delete", delete);
			returnMap.put("errosList", errosList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnMap;
	}

	/**
	 * 同步人员
	 * 
	 * @param ehrConn
	 * @param adConn
	 * @param ehrTable
	 * @param ldapCode
	 * @param dbType
	 * @param cond
	 * @param flag
	 * @return
	 */
	public Map syncEmp(Connection ehrConn, DirContext adConn, String ehrTable,
			String ldapCode, String dbType, String cond, int flag) {
		// 返回值
		Map returnMap = new HashMap();
		//人员需要转码的指标
		String hrtranscoding = this.LdapParam.getHrtranscoding();
		String field = this.getFields("A");
		hrtranscoding = getTranscoding(hrtranscoding, field);
		ContentDAO ehrDao = new ContentDAO(ehrConn);
		RowSet rs = null;
		DirContext delUser = null;
		StringBuffer sql = new StringBuffer();

		try{
			String rootorg = this.LdapParam.getRootorg();
			String rootSql = "";
			if(rootorg.length()>0){
				String[] orgs = rootorg.split(",");
				rootSql+=" and (1=2 ";
				for(int i=0;i<orgs.length;i++){
					rootSql+=" or (b0110_0 like '"+orgs[i]+"%' or e0122_0 like '"+orgs[i]+"%' or e01a1_0 like '"+orgs[i]+"%') ";
				}
				rootSql+=") ";
			}
			
			
			sql.setLength(0);
			sql.append("select ");
			sql.append(this.getFields("A"));
			sql.append(",");
			sql.append(ldapCode);
			if (!"unique_id".equalsIgnoreCase(this.LdapParam.getHrPk())) {
				sql.append(",");
				sql.append("unique_id");
			}
			sql.append(" from ");
			sql.append(ehrTable);
			sql.append(" where ");
			// sql.append(condition);
	//		if(!"".equalsIgnoreCase(hrcondition) && hrcondition != null){
	//			sql.append(ldapCode);
	//			sql.append(hrcondition);
	//		}
			if (flag == this.LdapParam.DELETE) {
				sql.append(ldapCode);
				sql.append("=3 ");
	
				// sql.append(" order by grade desc ");
			} else {
				sql.append(ldapCode);
				sql.append("<>0 and ");
				sql.append(ldapCode);
				sql.append("<>3 ");
	
				
				// sql.append(" order by e01a1_0 ");
			}
			
			// 添加设置的过滤条件
			if (cond != null && cond.trim().length() > 0) {
				sql.append(" and (");
				sql.append(cond);
				sql.append(") ");
			}
			
			sql.append(rootSql);
			
	
			// 更新的记录id
			ArrayList update = new ArrayList();
			// 新增的记录id
			ArrayList insert = new ArrayList();
			// 删除的记录id
			ArrayList delete = new ArrayList();
	
			// 本该执行更新，但执行的是插入操作
			ArrayList updateToinsert = new ArrayList();
			// 本该新增，但执行的是更新
			ArrayList insertToupdate = new ArrayList();
			// 本该删除，但执行的是插入
			ArrayList deleteToinsert = new ArrayList();
	
			ArrayList errosList = new ArrayList();
	
			// 父节点
			Map map = getHrParentKey(ldapCode, ehrConn,ehrTable);
	//		PareXmlUtils utils = new PareXmlUtils(getFile());
			// 是否是当前连接外的OU
			String isinner = this.LdapParam.getIsinner();

			String delUserPath = this.LdapParam.getDelUserPath();
			if("true".equalsIgnoreCase(isinner) && delUserPath != null && delUserPath.length() > 0){
				String regEx = "[\\u4e00-\\u9fa5]";
				Pattern p = Pattern.compile(regEx);
				String ou = "";
				for (int i = 0; i < delUserPath.length(); i++) {
					String s = delUserPath.substring(i, i + 1);
					Matcher m1 = p.matcher(s);
					if(s.getBytes().length == 1)
						ou += s;
					while (m1.find()) {
						for (int j = 0; j <= m1.groupCount(); j++) {
							try {
								ou += URLEncoder.encode(s, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					}
				}
				delUserPath = ou;
			}
			if (flag == this.LdapParam.DELETE && "true".equalsIgnoreCase(isinner)){
				delUser = getADConn(delUserPath);
			}
			rs = ehrDao.search(sql.toString());
			while (rs.next()) {
				
				maxCout ++;
				
				if (this.LdapParam.getCountNum() != -1 && maxCout > this.LdapParam.getCountNum()) {
					break;
				}
				
				String unique_id = "";
				String name = "";
				try {
					
					/*if (!"unique_id".equalsIgnoreCase(this.LdapParam.getHrPk())) {
						unique_id = rs.getString("unique_id");
					} else {
						unique_id = rs.getString(this.LdapParam.getHrAdPk());
					}*/
					unique_id = rs.getString(this.LdapParam.getHrAdPk());
					// 获得唯一字段的值
					String id = rs.getString(this.LdapParam.getHrAdPk());
					ArrayList parentIdList = (ArrayList) map.get(id);
					name = rs.getString("cn");
					if (id == null || id.length() <= 0) {
						continue;
					}

					String parentId = "";
					String parentType = "";
					String parentAdPk = "";
					if (parentIdList != null && parentIdList.size() > 0) {
						parentType = parentIdList.get(0).toString();
						parentId = parentIdList.get(1).toString();
					} else {
						log.error("父节点不存在，无法进行操作！");
						errosList.add(id);
						continue;
					}

					if ("K".equals(parentType.toUpperCase())) {
						parentAdPk = this.LdapParam.getPostAdPk();
					} else if ("B".equals(parentType.toUpperCase())) {
						parentAdPk = this.LdapParam.getOrgAdPk();
					}

					// 父节点是否存在，如果不存在，认为是根节点
					if (!isExistOrg(parentId, parentAdPk, adConn)) {

						log.error("父节点不存在，无法进行操作！");
						errosList.add(id);

						continue;
					}

					// 更新标志
					String ldapFlag = rs.getString(ldapCode);
					// 属性集合
					Map attMap = getAttMapByRS(rs, "A", hrtranscoding);

					if ("1".equals(ldapFlag)) {// 新增
						if (isExistOrg(id, this.LdapParam.getHrAdPk(), adConn)) {// 存在此机构，需要更新
							if (updateContext(adConn, "A", attMap, parentId,
									parentType)) {
								insertToupdate.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						} else {
							if (addContext(adConn, "A", getNewName(adConn, "A",
									attMap, parentId, parentType), attMap)) {
								insert.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						}

					} else if ("2".equals(ldapFlag)) {// 更新
						if (isExistOrg(id, this.LdapParam.getHrAdPk(), adConn)) {// 存在此机构，需要更新
							if (updateContext(adConn, "A", attMap, parentId,
									parentType)) {
								update.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						} else {
							if (addContext(adConn, "A", getNewName(adConn, "A",
									attMap, parentId, parentType), attMap)) {
								updateToinsert.add(unique_id);
							} else {
								errosList.add(unique_id);
							}
						}
					} else if ("3".equals(ldapFlag)) {// 删除
						if ("1".equals(this.LdapParam.getUserDel())) {// 删除用户
							if (isExistOrg(id, this.LdapParam.getHrAdPk(),
									adConn)) {// 存在此机构，需要删除
								if (deleteContext(adConn, id, "A")) {
									delete.add(unique_id);
								} else {
									errosList.add(unique_id);
								}
							} else {
								delete.add(unique_id);
							}
						} else {//禁用用户
							
							attMap.put("userAccountControl", "514");
								if (isExistOrg(id, this.LdapParam.getHrAdPk(),
										adConn)) {// 禁用用户
									if("false".equalsIgnoreCase(isinner)){
										//将需要删除的人员调入指定的OU下,并禁用
										if(updateContext(adConn, "A", attMap, parentId, parentType,delUserPath))
											delete.add(unique_id);
										else
											errosList.add(unique_id);
									}else{
										// 删除原来的用户
										deleteContext(adConn, id, "A");
										// 添加禁用用户
										addContext(delUser, "A", "cn=" + name,attMap);
										delete.add(unique_id);
									}
								} else {
									if (addContext(adConn, "A", getNewName(adConn,
											"A", attMap, parentId, parentType),
											attMap)) {
										deleteToinsert.add(unique_id);
									} else {
										errosList.add(unique_id);
									}
								}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("人员同步失败----姓名:" + name + "---ID：" + unique_id);
				}
			}

			// 保存处理的id
			returnMap.put("insertToupdate", insertToupdate);
			returnMap.put("insert", insert);
			returnMap.put("update", update);
			returnMap.put("updateToinsert", updateToinsert);
			returnMap.put("delete", delete);
			returnMap.put("errosList", errosList);
			returnMap.put("deleteToinsert", deleteToinsert);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			if (delUser != null) {
				delUser.close();
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnMap;
	}

	/**
	 * 添加
	 * 
	 * @param adConn
	 * @param name
	 * @param attMap
	 * @return
	 */
	private boolean addContext(DirContext adConn, String type, String name,
			Map attMap) {
		boolean flag = false;
		try {
			if ("A".equals(type.toUpperCase())) {
				if ("1".equals(this.LdapParam.getDefaultPwd())) {
					String password = this.LdapParam.getDefaultPwdValue();
					attMap.put("unicodePwd", this.handlerPwd(password));
//					attMap.put("UserAccountControl", "512");
				} else {
					if (attMap.containsKey("unicodepwd")) {
						if (attMap.get("unicodepwd") instanceof String ) {
						String password = (String) attMap.get("unicodepwd");
						// 加密的密码需要脱密
						if (ConstantParamter.isEncPwd()) {
							Des des = new Des();
							password = des.DecryPwdStr(password);
						}
						attMap.put("unicodepwd", this.handlerPwd(password));
						}
					}
				}

				// 添加用户控制
				if (! attMap.containsKey("userAccountControl")) {
					String userControl = this.LdapParam.getUserControl();
					if (userControl != null && userControl.trim().length() > 0) {
						attMap.put("userAccountControl", userControl);
					}
				}
			}
			attMap.remove(this.LdapParam.getLdapCode().toLowerCase());
			LdapOperUtils.addContext(adConn, name, attMap);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return flag;
	}

	/**
	 * 删除
	 * 
	 * @param adConn
	 * @param id
	 * @param type
	 */
	private boolean deleteContext(DirContext adConn, String id, String type) {
		boolean flag = false;

		String name = "";
		try {
			// ad主键
			String adPk = "";
			if ("A".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getHrAdPk();
			} else if ("B".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getOrgAdPk();
			} else if ("K".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getPostAdPk();
			}

			name = LdapOperUtils.getSingleName(adConn, "", adPk + "=" + id);
			LdapOperUtils.deleteContext(adConn, name);

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(name + "--条目删除失败");
		}

		return flag;
	}

	/**
	 * 获取条目名称
	 * 
	 * @param adConn
	 * @param type
	 * @param attMap
	 * @param parentId
	 * @return
	 */
	private String getNewName(DirContext adConn, String type, Map attMap,
			String parentId, String parentType) {
		String newName = "";
		try {
			// ad主键
			String adPk = "ou";
			if ("A".equals(parentType.toUpperCase())) {
				adPk = this.LdapParam.getHrAdPk();
			} else if ("B".equals(parentType.toUpperCase())) {
				adPk = this.LdapParam.getOrgAdPk();
			} else if ("K".equals(parentType.toUpperCase())) {
				adPk = this.LdapParam.getPostAdPk();
			}

			// 修改名称
			String parentName = LdapOperUtils.getSingleName(adConn, "", adPk
					+ "=" + parentId);

			if ("A".equals(type.toUpperCase())) {
				newName = "cn=" + attMap.get("cn").toString() + ","
						+ parentName;
			} else if ("B".equals(type.toUpperCase())) {
				newName = "ou=" + attMap.get("ou").toString() + ","
						+ parentName;
			} else if ("K".equals(type.toUpperCase())) {
				newName = "ou=" + attMap.get("ou").toString() + ","
						+ parentName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newName;
	}

	/**
	 * 更新条目
	 * 
	 * @param adConn
	 * @param type
	 * @param attMap
	 */
	private boolean updateContext(DirContext adConn, String type, Map attMap,
			String parentId, String parentType) {
		boolean flag = false;
		try {
			// ad主键
			String adPk = "";
			if ("A".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getHrAdPk().toLowerCase();
			} else if ("B".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getOrgAdPk().toLowerCase();
			} else if ("K".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getPostAdPk().toLowerCase();
			}

			// 修改名称

			String newName = "";
			String oldName = LdapOperUtils.getSingleName(adConn, "", adPk + "="
					+ attMap.get(adPk).toString());

			if (parentId.equals(attMap.get(adPk).toString())) {
				int equal = oldName.indexOf("=");
				int douh = oldName.indexOf(",");
				if (douh == -1) {
					if ("A".equals(type.toUpperCase())) {
						newName = oldName.substring(0, equal + 1)
								+ attMap.get("cn").toString();
					} else if ("B".equals(type.toUpperCase())) {
						newName = oldName.substring(0, equal + 1)
								+ attMap.get("ou").toString();
					} else if ("K".equals(type.toUpperCase())) {
						newName = oldName.substring(0, equal + 1)
								+ attMap.get("ou").toString();
					}

				} else {
					if ("A".equals(type.toUpperCase())) {
						newName = oldName.substring(0, equal + 1)
								+ attMap.get("cn").toString()
								+ oldName.substring(douh);
					} else if ("B".equals(type.toUpperCase())) {
						newName = oldName.substring(0, equal + 1)
								+ attMap.get("ou").toString()
								+ oldName.substring(douh);
					} else if ("K".equals(type.toUpperCase())) {
						newName = oldName.substring(0, equal + 1)
								+ attMap.get("ou").toString()
								+ oldName.substring(douh);
					}
				}
			} else {
				newName = getNewName(adConn, type, attMap, parentId, parentType);
			}

			try {
				LdapOperUtils.reNameContext(adConn, oldName, newName);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(newName + "条目重命名失败");
			}

			if ("A".equals(type.toUpperCase())) {

				if (attMap.containsKey("unicodepwd")) {
					if (attMap.get("unicodepwd") instanceof String ) {
						String password = (String) attMap.get("unicodepwd");
	
						// 加密的密码需要脱密
						if (ConstantParamter.isEncPwd()) {
							Des des = new Des();
							password = des.DecryPwdStr(password);
						}
	
						attMap.put("unicodepwd", this.handlerPwd(password));
					}
				}

			}

			// 获取ad属性列表
			ArrayList attNameList = getAdFieldList(type);

			Map attsMap = LdapOperUtils.getAttributes(adConn, newName,
					attNameList);

			Map updateAttrs = new HashMap();
			Map addAttrs = new HashMap();
			ArrayList delList = new ArrayList();

			for (int i = 0; i < attNameList.size(); i++) {
				String attName = attNameList.get(i).toString();

				if ("ou".equalsIgnoreCase(attName)
						|| "cn".equalsIgnoreCase(attName)
						|| "name".equalsIgnoreCase(attName)) {
					continue;
				}

				if (attsMap.get(attName) != null && attMap.get(attName) == null) {
					// Attribute att = new BasicAttribute(attName,
					// attsMap.get(attName));
					delList.add(attName);
				} else if (attsMap.get(attName) == null
						&& attMap.get(attName) != null) {
					// Attribute att = new BasicAttribute(attName,
					// attMap.get(attName));
					
					if("unicodepwd".equalsIgnoreCase(attName)){
						updateAttrs.put(attName, attMap.get(attName));
					} else {
						addAttrs.put(attName, attMap.get(attName));
					}
					
//					if("unicodepwd".equalsIgnoreCase(attName)){
//						if(attMap.get("userpassword")==null){
//							updateAttrs.put(attName, attMap.get(attName));
//						}else{
//							addAttrs.put(attName, attMap.get(attName));
//						}
//					}else{
//						addAttrs.put(attName, attMap.get(attName));
//					}
				} else if (attsMap.get(attName) != null
						&& attMap.get(attName) != null) {
					// Attribute att = new BasicAttribute(attName,
					// attMap.get(attName));
					updateAttrs.put(attName, attMap.get(attName));
				}
			}
			if("A".equalsIgnoreCase(type)){
				String userControl = this.LdapParam.getUserControl();
				if (userControl != null && userControl.trim().length() > 0) {
					updateAttrs.put("userAccountControl", userControl);
				}
			}
			LdapOperUtils.deleteAttributes(adConn, newName, delList);
			LdapOperUtils.modifyAttributes(adConn, newName, updateAttrs);
			LdapOperUtils.addAttributes(adConn, newName, addAttrs);
			// delAttr(adConn, newName,delAttrs);//删除属性
			// modAttr(adConn, newName,updateAttrs);//修改属性
			// addAttr(adConn, newName,addAttrs);//新增属性

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return flag;
	}
	
	/**
	 * 修改人员OU,并禁用
	 * <p>Create Time:2012-12-12 上午10:48:01</p>
	 * <p>@author:jianc</p>
	 */
	private boolean updateContext(DirContext adConn, String type, Map attMap,
			String parentId, String parentType,String newName) {
		boolean flag = true;
		try {
			// ad主键
			String adPk = "";
			if ("A".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getHrAdPk().toLowerCase();
			} else if ("B".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getOrgAdPk().toLowerCase();
			} else if ("K".equals(type.toUpperCase())) {
				adPk = this.LdapParam.getPostAdPk().toLowerCase();
			}

			// 修改名称

			String oldName = LdapOperUtils.getSingleName(adConn, "", adPk + "="
					+ attMap.get(adPk).toString());
			
			/*int douh = newName.indexOf(",");
			String ouName = newName.substring(3, douh).toLowerCase();
			
			newName = getNewName(adConn, type, attMap, ouName, "");*/
			
			int count = oldName.indexOf(",") + 1 ;
			if(newName == null || newName.length() == 0)
				newName = oldName;
			else{
				newName = oldName.substring(0,count) + newName;
				LdapOperUtils.reNameContext(adConn, oldName, newName);
			}
			
			Map addAttrs = new HashMap();
			addAttrs.put("UserAccountControl", "514");
			LdapOperUtils.modifyAttributes(adConn, newName, addAttrs);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			log.error(newName + "条目重命名失败");
		}

		return flag;
	}

	// private void delAttr(DirContext adConn,String name,Attributes attrs)
	// throws NamingException{
	// adConn.modifyAttributes(name, DirContext.REMOVE_ATTRIBUTE, attrs);
	// }
	//	
	// private void modAttr(DirContext adConn, String name,Attributes attrs)
	// throws NamingException{
	// adConn.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
	// }
	//	
	// private void addAttr(DirContext adConn, String name,Attributes attrs)
	// throws NamingException{
	// adConn.modifyAttributes(name, DirContext.ADD_ATTRIBUTE, attrs);
	// }

	/**
	 * 获取ad属性map
	 * 
	 * @param type
	 * @return
	 */
	private ArrayList getAdFieldList(String type) {
		ArrayList list = new ArrayList();
		Map fields = null;
		try {
			if ("A".equals(type.toUpperCase())) {
				fields = this.LdapParam.getHrFieldMap();
			} else if ("B".equals(type.toUpperCase())) {
				fields = this.LdapParam.getOrgFieldMap();
			} else if ("K".equals(type.toUpperCase())) {
				fields = this.LdapParam.getPostFieldMap();
			}

			Iterator it = fields.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				//list.add(entry.getValue());
				list.add(entry.getKey());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 通过RowSEt获取属性map
	 * 
	 * @param rs
	 * @param type
	 * @return
	 */
	private Map getAttMapByRS(RowSet rs, String type, String transcoding) {
		Map attMap = new HashMap();
		Map fieldMap = getFieldMap(type);
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String name = metaData.getColumnName(i).toLowerCase();
				String field = (String)fieldMap.get(name);
				String itemtype="A";
				if(field!=null){
					FieldItem item = DataDictionary.getFieldItem(field.toUpperCase());
					if(item!=null){
						itemtype = item.getItemtype();
					}
				}
				String value = null;
				if("A".equalsIgnoreCase(itemtype)){
					value = rs.getString(name);
				}else if("N".equalsIgnoreCase(itemtype)){
					value = String.valueOf(rs.getInt(name));
				}else if("D".equalsIgnoreCase(itemtype)){
					//sdf.format(date);
					Date dd = rs.getDate(name);
					if(dd==null){
						value=null;
					}else{
						value = dd.toString();
					}
				}
				if (value == null || value.trim().length() <= 0) {
					continue;
				}
				if (name.equalsIgnoreCase(this.getLdapParam().getLdapCode())) {
					continue;
				}
				if("department".equalsIgnoreCase(name)&&value.startsWith("/")){
					value=value.substring(1);
				}

				if ("A".equals(type.toUpperCase()) && "unicodePwd".equalsIgnoreCase(name)) {
					// 加密的密码需要脱密
					if (ConstantParamter.isEncPwd()) {
						Des des = new Des();
						value = des.DecryPwdStr(value);
					}
					attMap.put(name, handlerPwd(value));
				} else {
					value = getValue(name, value, transcoding);
					attMap.put(name, value);
				}
			}

			if ("B".equals(type.toUpperCase()) ) {
				ArrayList list = new ArrayList();
				list.add("organizationalUnit");
				list.add("top");
				attMap.put("objectClass", list);
				if (!"unique_id".equalsIgnoreCase(this.LdapParam.getOrgPk())) {
					attMap.remove("unique_id");
				}
			} else if ("A".equals(type.toUpperCase())) {
				ArrayList list = new ArrayList();
				list.add("organizationalPerson");
				list.add("person");
				list.add("top");
				list.add("user");
				attMap.put("objectClass", list);
				if (!"unique_id".equalsIgnoreCase(this.LdapParam.getHrPk())) {
					attMap.remove("unique_id");
				}
			} else if ("K".equals(type.toUpperCase())) {
				ArrayList list = new ArrayList();
				list.add("organizationalUnit");
				list.add("top");
				attMap.put("objectClass", list);
				if (!"unique_id".equalsIgnoreCase(this.LdapParam.getPostPk())) {
					attMap.remove("unique_id");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return attMap;
	}

	private Map getFieldMap(String type) {
		Map fieldMap = null;
		if ("A".equals(type.toUpperCase())) {
			fieldMap = this.LdapParam.getHrFieldMap();
		} else if ("B".equals(type.toUpperCase())) {
			fieldMap = this.LdapParam.getOrgFieldMap();
		} else if ("K".equals(type.toUpperCase())) {
			fieldMap = this.LdapParam.getPostFieldMap();
		}
		return fieldMap;
	}
	/**
	 * 获取字段字符窜
	 * 
	 * @param type
	 * @return
	 */
	private String getFields(String type) {
		Map fieldMap = null;
		if ("A".equals(type.toUpperCase())) {
			fieldMap = this.LdapParam.getHrFieldMap();
		} else if ("B".equals(type.toUpperCase())) {
			fieldMap = this.LdapParam.getOrgFieldMap();
		} else if ("K".equals(type.toUpperCase())) {
			fieldMap = this.LdapParam.getPostFieldMap();
		}

		StringBuffer field = new StringBuffer();
		Iterator it = fieldMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			/*field.append(entry.getKey());
			field.append(" ");
			field.append(entry.getValue());*/
			
			String val = (String) entry.getValue();
			if (val.contains("+") || val.contains("+")) {
				if (Sql_switcher.searchDbServer() == 1) {
					field.append(val);
				} else {
					field.append(val.replaceAll("\\+", "||"));
				}
			} else {
				field.append(entry.getValue());
			}
			
			field.append(" ");
			field.append(entry.getKey());
			field.append(",");
		}

		if ("A".equals(type.toUpperCase())) {
			field.append(this.LdapParam.getHrPk());
			field.append(" ");
			field.append(this.LdapParam.getHrAdPk());
		} else if ("B".equals(type.toUpperCase())) {
			field.append(this.LdapParam.getOrgPk());
			field.append(" ");
			field.append(this.LdapParam.getOrgAdPk());
		} else if ("K".equals(type.toUpperCase())) {
			field.append(this.LdapParam.getPostPk());
			field.append(" ");
			field.append(this.LdapParam.getPostAdPk());
		}

		return field.toString();

	}

	/**
	 * 获取属性map
	 * 
	 * @param conn
	 * @param tableName
	 * @param type
	 * @param sqlCond
	 * @return
	 */
	private Map getAttMap(Connection conn, String tableName, String type,
			String sqlCond) {
		Map map = new HashMap();

		StringBuffer sql = new StringBuffer();
		sql.append(" select ");
		sql.append(getFields(type));
		sql.append(",");
		sql.append(this.LdapParam.getLdapCode());

		sql.append(" from ");
		sql.append(tableName);
		if (sqlCond != null && sqlCond.length() > 0) {
			sql.append(" where ");
			sql.append(sqlCond);
		}

		RowSet rs = null;
		Map fieldMap = getFieldMap(type);
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			ResultSetMetaData metaData = rs.getMetaData();
			if (rs.next()) {
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String name = metaData.getColumnName(i).toLowerCase();
					String field = (String)fieldMap.get(name);
					String itemtype="A";
					if(field!=null){
						FieldItem item = DataDictionary.getFieldItem(field.toUpperCase());
						if(item!=null){
							itemtype = item.getItemtype();
						}
					}
					String value = null;
					if("A".equalsIgnoreCase(itemtype)){
						value = rs.getString(name);
					}else if("N".equalsIgnoreCase(itemtype)){
						value = String.valueOf(rs.getInt(name));
					}else if("D".equalsIgnoreCase(itemtype)){
						Date dd = rs.getDate(name);
						if(dd==null){
							value=null;
						}else{
							value = dd.toString();
						}
					}
					if (value == null || value.trim().length() <= 0) {
						continue;
					}
					if ("A".equals(type.toUpperCase())
							&& "unicodepwd".equalsIgnoreCase(name)) {
						map.put(name, handlerPwd(value));
					} else {
						map.put(name, value);
					}
				}
			}

			if ("B".equals(type.toUpperCase())
					|| "K".equals(type.toUpperCase())) {
				ArrayList list = new ArrayList();
				list.add("organizationalUnit");
				list.add("top");
				map.put("objectClass", list);
			} else if ("A".equals(type.toUpperCase())) {
				ArrayList list = new ArrayList();
				list.add("organizationalPerson");
				list.add("person");
				list.add("top");
				list.add("user");
				map.put("objectClass", list);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询父节点的id错误！");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 处理密码
	 * 
	 * @param password
	 * @return
	 */
	private byte[] handlerPwd(String password) {
		String newQuotedPassword = "\"" + password + "\"";
		byte[] newUnicodePassword = null;
		try {
			newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newUnicodePassword;
	}

	/**
	 * 获得父节点的id
	 * 
	 * @param ldapCode
	 * @return
	 */
	private Map getOrgParentKey(String ldapCode, Connection ehrConn,String ehrTable) {
		Map map = new HashMap();
		StringBuffer sql = new StringBuffer();
		sql.append(" select t.");
		sql.append(this.LdapParam.getOrgPk());
		sql.append(" subId,v.");
		sql.append(this.LdapParam.getOrgPk());
		sql.append(" parentId from (select * from "+ehrTable+" where ");
		sql.append(ldapCode);
		sql.append(" <> 0) t left join "+ehrTable+" v on t.parentid=v.b0110_0");

		RowSet rs = null;

		try {
			ContentDAO dao = new ContentDAO(ehrConn);
			rs = dao.search(sql.toString());

			while (rs.next()) {
				String subId = rs.getString("subId");
				String parentId = rs.getString("parentId");
				if (parentId != null && parentId.trim().length() > 0) {
					map.put(subId, parentId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询父节点的id错误！");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 获得父节点的id
	 * 
	 * @param ldapCode
	 * @return
	 */
	private Map getPostParentKey(String ldapCode, Connection ehrConn,String ehrTable) {
		Map map = new HashMap();
		StringBuffer sql = new StringBuffer();
		sql.append(" select t.");
		sql.append(this.LdapParam.getPostPk());
		sql.append(" subId,v.");
		sql.append(this.LdapParam.getOrgPk());
		sql.append(" parentId from (select * from "+ehrTable+" where ");
		sql.append(ldapCode);
		if(this.tempTable!=null&&this.tempTable.length()>1)
			sql.append(" <> 0) t left join "+this.tempTable+" v on t.parentid=v.b0110_0");
		else
			sql.append(" <> 0) t left join "+this.org_table+" v on t.parentid=v.b0110_0");

		RowSet rs = null;

		try {
			ContentDAO dao = new ContentDAO(ehrConn);
			rs = dao.search(sql.toString());

			while (rs.next()) {
				String subId = rs.getString("subId");
				String parentId = rs.getString("parentId");
				if (parentId != null && parentId.trim().length() > 0) {
					map.put(subId, parentId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询父节点的id错误！");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 获得父节点的id
	 * 
	 * @param ldapCode
	 * @return
	 */
	private Map getHrParentKey(String ldapCode, Connection ehrConn,String ehrTable) {
		Map map = new HashMap();
		StringBuffer sql = new StringBuffer();
		if(this.post_table!=null&&this.post_table.length()>1&&this.org_table!=null&&this.org_table.length()>1){
			sql.append(" select t.");
			sql.append(this.LdapParam.getHrPk());
			sql.append(" subId,b.");
			sql.append(this.LdapParam.getOrgPk());
			sql.append(" b0110,e.");
			sql.append(this.LdapParam.getOrgPk());
			sql.append(" e0122,p.");
			sql.append(this.LdapParam.getPostPk());
			sql.append(" e01a1 ");
			sql.append(" from (select * from "+ehrTable+" where ");
			sql.append(ldapCode);
			if(this.tempTable!=null&&this.tempTable.length()>1)
				sql.append(" <> 0) t left join "+this.tempTable+" b on t.b0110_0=b.b0110_0 left join "+this.tempTable+" e on t.e0122_0=e.b0110_0 left join "+this.post_table+" p on t.e01a1_0=p.e01a1_0");
			else
				sql.append(" <> 0) t left join "+this.org_table+" b on t.b0110_0=b.b0110_0 left join "+this.org_table+" e on t.e0122_0=e.b0110_0 left join "+this.post_table+" p on t.e01a1_0=p.e01a1_0");
		}else if(this.org_table!=null&&this.org_table.length()>1){
			sql.append(" select t.");
			sql.append(this.LdapParam.getHrPk());
			sql.append(" subId,b.");
			sql.append(this.LdapParam.getOrgPk());
			sql.append(" b0110,e.");
			sql.append(this.LdapParam.getOrgPk());
			sql.append(" e0122,");
			sql.append("''");
			sql.append(" e01a1 ");
			sql.append(" from (select * from "+ehrTable+" where ");
			sql.append(ldapCode);
			if(this.tempTable!=null&&this.tempTable.length()>1)
				sql.append(" <> 0) t left join "+this.tempTable+" b on t.b0110_0=b.b0110_0 left join "+this.tempTable+" e on t.e0122_0=e.b0110_0");
			else
				sql.append(" <> 0) t left join "+this.org_table+" b on t.b0110_0=b.b0110_0 left join "+this.org_table+" e on t.e0122_0=e.b0110_0");
		}else{
			//暂不支持只同步用户到ad服务器
			log.error("暂不支持只同步用户到ad服务器！");
		}
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(ehrConn);
			rs = dao.search(sql.toString());

			while (rs.next()) {
				ArrayList list = new ArrayList();
				String subId = rs.getString("subId");
				String b0110 = rs.getString("b0110");
				String e0122 = rs.getString("e0122");
				String e01a1 = rs.getString("e01a1");
				
				if ("true".equalsIgnoreCase(this.LdapParam.getSyncPost())) {
					if (e01a1 != null && e01a1.trim().length() > 0) {
						list.add("K");
						list.add(e01a1);
						map.put(subId, list);
					} else if (e0122 != null && e0122.trim().length() > 0) {
						list.add("B");
						list.add(e0122);
						map.put(subId, list);
					} else if (b0110 != null && b0110.trim().length() > 0) {
						list.add("B");
						list.add(b0110);
						map.put(subId, list);
					}
				} else if ("true".equalsIgnoreCase(this.LdapParam.getSyncOrg())) {
					if (e0122 != null && e0122.trim().length() > 0) {
						list.add("B");
						list.add(e0122);
						map.put(subId, list);
					} else if (b0110 != null && b0110.trim().length() > 0) {
						list.add("B");
						list.add(b0110);
						map.put(subId, list);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询父节点的id错误！");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 是否存在该机构
	 * 
	 * @param id
	 * @param adPk
	 * @param adConn
	 * @return
	 */
	private boolean isExistOrg(String id, String adPk, DirContext adConn) {
		boolean flag = false;
		try {
			flag = LdapOperUtils.isExist(adConn, adPk + "=" + id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	public String initOrgNosycnode(String orgNosycnode,Connection ehrConn,String view,String cond){
		String tempTable; // 临时表
		StringBuffer sql = new StringBuffer();
		tempTable = "t#"+view;
		sql.delete(0, sql.length());
		sql.append("drop table ");
		sql.append(tempTable);
		try {
			ContentDAO dao = new ContentDAO(ehrConn);
			DbWizard dbw = new DbWizard(ehrConn);
			if (dbw.isExistTable(tempTable, false)) {
					dbw.dropTable(tempTable);
			}
			//ExecuteSQL.createTable(sql.toString(), ehrConn);
			sql.delete(0, sql.length());
			switch (Sql_switcher.searchDbServer()) {//复制数据
				case Constant.MSSQL: {
					sql.append("select * into "+tempTable+" from "+view);
					// 添加设置的过滤条件
					if (cond != null && cond.trim().length() > 0) {
						sql.append(" where ");
						sql.append(cond);
					}
					break;
				}
				case Constant.DB2: {
					
					break;
				}
				case Constant.ORACEL: {
					sql.append("create table "+tempTable);
					sql.append(" as select * from "+view);
					// 添加设置的过滤条件
					if (cond != null && cond.trim().length() > 0) {
						sql.append(" where ");
						sql.append(cond);
					}
					break;
				}
			}
			ExecuteSQL.createTable(sql.toString(), ehrConn);
		
			sql.setLength(0);
			sql.append("select codeitemid,parentid from organization inner join b01 on b0110=codeitemid where "+orgNosycnode+" order by grade");
			List list = ExecuteSQL.executeMyQuery(sql.toString(), ehrConn);
			HashMap map = new HashMap();
			ArrayList maplist=new ArrayList();
			ArrayList toplist =new ArrayList();
			ArrayList values = new ArrayList();
			for(int i=0;i<list.size();i++){
				LazyDynaBean rec=(LazyDynaBean)list.get(i);
				String codeitemid=(String)rec.get("codeitemid");
				String parentid=(String)rec.get("parentid");
				ArrayList value = new ArrayList();
				value.add(codeitemid);
				values.add(value);
				if(codeitemid.equals(parentid)){
					toplist.add(codeitemid);
				} 
				
				if(map.containsKey(parentid)){
					maplist.add(parentid);
				    map.put(codeitemid, (String)map.get(parentid));
				}else{
					map.put(codeitemid, parentid);
				}
			}
			/*for(int i=toplist.size()-1;i>=0;i--){
				String keyid = (String)toplist.get(i);
				map.remove(keyid);
			}*/
			ArrayList newtoplist = new ArrayList();
			HashMap newMap = new HashMap();
			for(int i=toplist.size()-1;i>=0;i--){
				String valueid = (String)toplist.get(i);
				for(Iterator it=map.keySet().iterator();it.hasNext();){
					String keyid=(String)it.next();
					if(map.get(keyid).equals(valueid)){
						newtoplist.add(keyid);
					}else{
						newMap.put(keyid, map.get(keyid));
					}
				}
			}			
			for(int i=0;i<newtoplist.size();i++){
				String codeitemid=(String)newtoplist.get(i);
				sql.setLength(0);
				sql.append("update "+tempTable+" set parentid=b0110_0,parentdesc=codeitemdesc where parentid='"+codeitemid+"'");
				ExecuteSQL.createTable(sql.toString(), ehrConn);
			}
			for(Iterator it=newMap.keySet().iterator();it.hasNext();){
				String codeitemid=(String)it.next();
				String parentid=(String)newMap.get(codeitemid);
				sql.setLength(0);
				sql.append("update "+tempTable+" set parentid='"+parentid+"',parentdesc=(select codeitemdesc from organization where codeitemid='"+parentid+"') where parentid='"+codeitemid+"'");
				ExecuteSQL.createTable(sql.toString(), ehrConn);
			}
			sql.setLength(0);
			sql.append("delete from "+tempTable+" where b0110_0=?");
			dao.batchUpdate(sql.toString(), values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.tempTable=tempTable;
		return tempTable;
	}
	
	/**
	 * 获取需要转码的，对应AD中的指标
	 * @param transcoding
	 * @param field
	 * @author:jianc
	 * @return
	 */
	private String getTranscoding(String transcoding,String field){
		if(transcoding == null || transcoding.length() == 0)
			return "";
		String[] transcodings = transcoding.split(",");
		String[] fields = field.split(",");
		String newtranscoding = "";
		for (int i = 0; i < transcodings.length; i++) {
			String[] str = transcodings[i].split(":");
			String codeitem = str[0];
			String codeset = str[1];
			for (int j = 0; j < fields.length; j++) {
				String[] norm = fields[j].split(" ");
				String hr = norm[0];
				if(hr.equalsIgnoreCase(codeitem)){
					String ad = norm[1];
					newtranscoding += ad + ":" + codeset + ",";
				}
			}
		}
		newtranscoding = newtranscoding.substring(0,newtranscoding.length()-1);
		return newtranscoding;
	}
	
	/**
	 * 获取需要转码的值
	 * @param name
	 * @param value
	 * @param transcoding
	 * @author:jianc
	 * @return
	 */
	public String getValue(String name, String value, String transcoding){
		if(transcoding == null || transcoding.length() == 0)
			return value;
		String[] transcodings = transcoding.split(",");
		for (int i = 0; i < transcodings.length; i++) {
			String[] str = transcodings[i].split(":");
			String ad = str[0];
			String codeset = str[1];
			if(name.equalsIgnoreCase(ad)){
				return AdminCode.getCodeName(codeset,value);
			}
				
		}
		return value;
	}
	
	public String getXmlPath() {
		return xmlPath;
	}

	public boolean isExistXML() {
		return isExistXML;
	}

	public LdapParam getLdapParam() {
		return LdapParam;
	}

	public String getTempTable() {
		return tempTable;
	}

	public void setTempTable(String tempTable) {
		this.tempTable = tempTable;
	}

	public String getOrg_table() {
		return org_table;
	}

	public void setOrg_table(String org_table) {
		this.org_table = org_table;
	}

	public String getPost_table() {
		return post_table;
	}

	public void setPost_table(String post_table) {
		this.post_table = post_table;
	}

	public String getHr_table() {
		return hr_table;
	}

	public void setHr_table(String hr_table) {
		this.hr_table = hr_table;
	}

}
