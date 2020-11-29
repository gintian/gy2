package com.hjsj.hrms.utils;

import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class LDAPTools {

	private String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

	private String ROOT;// 基础DN

	private String IPADRESS;// IP地址

	private String PORT;// 端口号

	private String ACCOUNT;// 账户名称

	private String PASSWORD;// 密码

	private DirContext ctx;
	private DirContext ssl_ctx;
	private String initUserPassword = "";
	public int ldapDN_User = 1;
	public int ldap_Unit = 2;
	public int ldapDN_Group = 3;

	public LDAPTools(String ROOT, String IPADRESS, String PORT, String ACCOUNT,
			String PASSWORD) {
		this.ROOT = ROOT;
		this.IPADRESS = IPADRESS;
		this.PORT = PORT;
		this.ACCOUNT = ACCOUNT;
		this.PASSWORD = PASSWORD;
		ctx = getConn();
		this.ssl_ctx = sslDirContext();
	}

	/**
	 * 组装连接配置信息
	 * 
	 * @return Hashtable
	 */
	private Hashtable getConfig() {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, this.FACTORY);
		env.put(Context.PROVIDER_URL, "ldap://" + this.IPADRESS + ":"
				+ this.PORT + "/" + this.ROOT);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, this.ACCOUNT);
		env.put(Context.SECURITY_CREDENTIALS, this.PASSWORD);
		return env;
	}

	/**
	 * 建立连接
	 * 
	 * @return DirContext
	 */
	public DirContext getConn() {
		Hashtable env = getConfig();
		try {
			this.ctx = new InitialDirContext(env);
		} catch (javax.naming.AuthenticationException e) {
			System.out.println("认证失败");
			System.out.println("--dn=" + env.get(Context.SECURITY_PRINCIPAL));
			System.out.println("--url=" + env.get(Context.PROVIDER_URL));
			e.printStackTrace();
			System.out.println(e);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 连接失败！" + env.get(Context.SECURITY_PRINCIPAL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 连接失败！" + env.get(Context.PROVIDER_URL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 连接失败！", e);
		} catch (Exception e) {
			System.out.println("认证出错：");
			System.out.println("--dn=" + env.get(Context.SECURITY_PRINCIPAL));
			System.out.println("--url=" + env.get(Context.PROVIDER_URL));
			e.printStackTrace();
			System.out.println(e);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 连接失败！" + env.get(Context.SECURITY_PRINCIPAL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 连接失败！" + env.get(Context.PROVIDER_URL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 连接失败！", e);
		}
		return ctx;
	}

	/**
	 * 通过ssl开通LDAP
	 */
	private DirContext sslDirContext() {
		Hashtable env = new Hashtable();
		String LDAP_INIT_USERPASSWORD = SystemConfig
				.getPropertyValue("LDAP_INIT_USERPASSWORD");// 用户出示密码
		if (LDAP_INIT_USERPASSWORD == null
				|| LDAP_INIT_USERPASSWORD.length() <= 0)
			return null;
		this.initUserPassword = LDAP_INIT_USERPASSWORD;
		String certficationPath = SystemConfig
				.getPropertyValue("certficationPath");
		String certficationPwd = SystemConfig
				.getPropertyValue("certficationPwd");
		String certficationPost = SystemConfig
				.getPropertyValue("certficationPost");
		try {
			// 设置系统属性中ssl连接的证书和密码
			// System.setProperty("javax.net.ssl.trustStore",
			// "d:/sxin-ad77e821fd.chrdi.com_chrdi.crt");
			System.setProperty("javax.net.ssl.trustStore", certficationPath);
			System.setProperty("javax.net.ssl.trustStorePassword",
					certficationPwd);
			env.put(Context.INITIAL_CONTEXT_FACTORY, this.FACTORY);
			env.put(Context.PROVIDER_URL, "ldap://" + this.IPADRESS + ":"
					+ certficationPost + "/" + this.ROOT);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, this.ACCOUNT);
			env.put(Context.SECURITY_CREDENTIALS, this.PASSWORD);
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			// 通过参数连接LDAP/AD
			this.ssl_ctx = new InitialDirContext(env);

		} catch (javax.naming.AuthenticationException e) {

			System.out.println("ssl连接失败");
			System.out.println("--dn=" + env.get(Context.SECURITY_PRINCIPAL));
			System.out.println("--url=" + env.get(Context.PROVIDER_URL));
			e.printStackTrace();
			System.out.println(e);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP ssl连接失败！" + env.get(Context.SECURITY_PRINCIPAL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP ssl连接失败！" + env.get(Context.PROVIDER_URL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP ssl连接失败！", e);
			this.ssl_ctx = null;
		} catch (Exception e) {
			System.out.println("ssl连接失败：");
			System.out.println("--dn=" + env.get(Context.SECURITY_PRINCIPAL));
			System.out.println("--url=" + env.get(Context.PROVIDER_URL));
			e.printStackTrace();
			System.out.println(e);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP ssl连接失败！" + env.get(Context.SECURITY_PRINCIPAL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP ssl连接失败！" + env.get(Context.PROVIDER_URL));
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP ssl连接失败！", e);
			this.ssl_ctx = null;
		}
		return this.ssl_ctx;
	}

	/**
	 * 根据账户查找所以属性
	 * 
	 * @param account
	 *            账户名称
	 * @return Attributes 属性集合
	 */
	public Attributes findByAccount(String account) {
		Attributes attrs = null;
		if (isConn()) {
			try {
				attrs = this.ctx.getAttributes(account);
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP findByAccount 错误" + account);
				return null;
			}
		}
		return attrs;
	}

	// 根据账户或属性查找所以属性 account-账户名称 attribute 属性 BasicAttribute
	public ArrayList find(String sql) {

		ArrayList list = new ArrayList();
		if (isConn()) {
			Map mapSet = this.getFilter(sql);
			String filter = (String) mapSet.get("filter");
			String account = (String) mapSet.get("account");
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置要
			try {
				NamingEnumeration ne = this.ctx.search(account, filter,
						constraints);
				while (ne != null && ne.hasMoreElements()) {
					Object obj = ne.nextElement();
					if (obj instanceof SearchResult) {
						SearchResult is = (SearchResult) obj;
						list.add(is.getAttributes());
					}
				}
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 查询用户信息失败find " + sql);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 查询用户信息失败！", e);
			}
		}
		return list;
	}

	public ArrayList getNodes(String sql) {

		ArrayList list = new ArrayList();
		if (isConn()) {
			Map mapSet = this.getFilter(sql);
			String filter = (String) mapSet.get("filter");
			String account = (String) mapSet.get("account");
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置要
			try {
				NamingEnumeration ne = this.ctx.search(account, filter,
						constraints);
				while (ne != null && ne.hasMoreElements()) {
					Object obj = ne.nextElement();
					if (obj instanceof SearchResult) {
						SearchResult is = (SearchResult) obj;
						String db = is.getName();
						if ("".equals(db.trim()))
							list.add(account);
						else
							list.add(db + "," + account);

					}
				}
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 查询用户信息失败getNodes " + sql);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 查询用户信息失败！", e);
			}
		}
		return list;
	}

	// 根据账户或属性查找所以属性 account-账户名称 attribute 属性 BasicAttribute
	public ArrayList find(String account, Attributes attributes) {
		ArrayList list = new ArrayList();
		if (isConn()) {
			try {
				NamingEnumeration ne = ctx.search(account, attributes);
				while (ne != null && ne.hasMoreElements()) {
					Object obj = ne.nextElement();
					if (obj instanceof SearchResult) {
						SearchResult is = (SearchResult) obj;
						list.add(is.getAttributes());
					}
				}
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 查询用户信息失败find " + account);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 查询用户信息失败！", e);
			}
		}
		return list;
	}

	// 
	/**
	 * 根据属性得到账户名称 attribute 属性 BasicAttribute 查询范围定义：
	 * SearchControls.OBJECT_SCOPE SearchControls.ONELEVEL_SCOPE
	 * SearchControls.SUBTREE_SCOPE
	 * 
	 * @param sql
	 *            查询sql语句
	 * @return ArrayList 得到账户名称列表
	 */
	public List getAccount(String sql) {
		ArrayList list = new ArrayList();
		if (isConn()) {
			Map mapSet = this.getFilter(sql);
			String filter = (String) mapSet.get("filter");
			// String account = (String) mapSet.get("account");

			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置范围
			try {
				NamingEnumeration ne = ctx.search("", filter, constraints);
				while (ne != null && ne.hasMoreElements()) {
					Object obj = ne.nextElement();
					if (obj instanceof SearchResult) {
						SearchResult is = (SearchResult) obj;
						list.add(is.getName());
					}
				}
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 获得用户账户名称失败getAccount " + sql);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"获得用户账户名称失败！", e);
			}

		}
		return list;
	}

	//
	/**
	 * 根据已给属性集 创建新的目录（数据）
	 * 
	 * @param account
	 *            定义的账户名称
	 * @param attributes
	 *            新增属性
	 */
	public boolean add(String account, Attributes attributes) {
		if (isConn()) {
			try {
				this.ctx.createSubcontext(account, attributes);// 创建新的用户
				return true;
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 创建用户失败add " + account);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"创建用户失败！", e);
				return false;
			}
		} else
			return false;
	}

	public boolean addUnit(String account, Attributes attributes) {
		if (isConn()) {
			try {
				this.ctx.bind(account, null, attributes);
				return true;
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 创建组织单元失败addUnit " + account);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"创建组织单元失败！", e);
				return false;
			}
		} else
			return false;
	}

	public boolean addGourp(String account, Attributes attributes) {
		if (isConn()) {
			try {
				this.ctx.bind(account, null, attributes);
				return true;
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 创建用户组失败 addGourp" + account);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"创建用户组失败！", e);
				return false;
			}
		} else
			return false;
	}

	/**
	 * 根据账户名称删除这个用户
	 * 
	 * @param account
	 *            账户名称
	 */
	public boolean delete(String account) {
		if (isConn()) {
			try {
				this.ctx.destroySubcontext(account); // 删除用户
				return true;
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 删除用户失败 delete" + account);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"删除用户失败！", e);
				return false;
			}
		} else
			return false;
	}

	/**
	 * 根据账户名称来修改这个账户的属性 attributes 属性集合
	 * 
	 * @param account
	 *            帐号名称
	 * @param operationType
	 *            属性定义 DirContext.ADD_ATTRIBUTE 新增属性
	 *            DirContext.REPLACE_ATTRIBUTE 替换属性 DirContext.REMOVE_ATTRIBUTE
	 *            移除属性
	 * @param attributes
	 *            操作的属性集
	 */

	public boolean modify(String account, String attrID[], Attributes attributes) {
		if (isConn()) {
			BasicAttributes updateAttrs = new BasicAttributes();
			BasicAttributes addAttrs = new BasicAttributes();
			BasicAttributes delAttrs = new BasicAttributes();
			Attributes searchAttrs = findByAccount(account);
			for (int i = 0; i < attrID.length; i++) {
				Attribute getAttr = searchAttrs.get(attrID[i]);
				if (attributes.get(attrID[i]) == null && getAttr != null) {
					delAttrs.put(getAttr);
				} else if (getAttr == null && attributes.get(attrID[i]) != null) {
					addAttrs.put(attributes.get(attrID[i]));
				} else if (getAttr != null && attributes.get(attrID[i]) != null) {
					updateAttrs.put(attributes.get(attrID[i]));
				}
			}
			try {
				this.ctx.modifyAttributes(account, DirContext.REMOVE_ATTRIBUTE,
						delAttrs);// 删除属性
				this.ctx.modifyAttributes(account, DirContext.ADD_ATTRIBUTE,
						addAttrs); // 添加属性
				this.ctx.modifyAttributes(account,
						DirContext.REPLACE_ATTRIBUTE, updateAttrs); // 修改属性
				return true;
			} catch (NamingException e) {
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 修改用户信息失败 modify" + account);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"修改用户信息失败！", e);
				return false;
			}
		} else
			return false;
	}

	/**
	 * 根据给出DN 得到 这个DN下的所有可操作的DN列表
	 * 
	 * @param DN
	 * @return
	 */
	public List getExeDN(String DN) {
		if (findByAccount(DN) != null) {
			// String sql = "select from " + DN + " where
			// hasSubordinates=false";
			String sql = "select from "
					+ DN
					+ " where objectclass =user or objectclass =organizationalUnit or objectclass=group";
			List list = getNodes(sql);
			return list;
		}
		return null;
	}

	public boolean modifyDN(String oldDN, String newDN) {

		if (isConn()) {
			try {
				this.ctx.rename(oldDN, newDN);
				return true;
			} catch (NamingException e) {
				System.out.println(oldDN + "----" + newDN);
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 修改DN失败 modifyDN  oldDN=" + oldDN + ";newDN="
								+ newDN);
				Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
						"LADP 修改DN失败！", e);
				return false;
			}
		} else
			return false;
	}

	public boolean isConn() {
		if (this.ctx == null) {
			System.out.println("LADP：连接已经断开！");
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 连接已经断开！");
			return false;
		}
		return true;
	}

	public void getClose() {
		try {
			if (this.ctx != null)
				this.ctx.close();
			if (this.ssl_ctx != null)
				this.ssl_ctx.close();
		} catch (NamingException e) {

			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					"LADP 连接关闭失败！");
		}
	}

	/**
	 * 把sql语句解析为 “账户名称，过滤器” 字符串
	 * 
	 * @param sql
	 * @return
	 */
	private Map getFilter(String sql) {
		sql = sql.toUpperCase();
		String wheres[] = sql.split(" WHERE ");
		String filter = "";
		if (sql.indexOf("WHERE") != -1) {
			filter = wheres[wheres.length - 1].trim();
			String ands[] = filter.split(" AND ");
			filter = "";
			for (int i = 0; i < ands.length; i++) {
				if (ands[i].indexOf(" OR ") != -1) {
					String ors[] = ands[i].split(" OR ");
					ands[i] = "";
					for (int j = 0; j < ors.length; j++) {
						if (ors[j].indexOf("<>") != -1) {
							ors[j] = "(!(" + ors[j].replaceAll("<>", "=")
									+ "))";
						} else if (ors[j].indexOf("!=") != -1) {
							ors[j] = "(!(" + ors[j].replaceAll("!=", "=")
									+ "))";
						} else if (ors[j].indexOf(" NOT ") != -1) {
							ors[j] = "(!(" + ors[j].replaceAll(" NOT ", "")
									+ "))";
							ors[j] = ors[j].replaceAll(" LIKE ", "~=");
						} else if (ors[j].indexOf(" LIKE ") != -1) {
							ors[j] = "(" + ors[j].replaceAll(" LIKE ", "~=")
									+ ")";
						} else {
							ors[j] = "(" + ors[j] + ")";
						}
						ands[i] += ors[j];
					}
					ands[i] = "|" + ands[i];
				} else {
					if (ands[i].indexOf("<>") != -1) {
						ands[i] = "!(" + ands[i].replaceAll("<>", "=") + ")";
					} else if (ands[i].indexOf("!=") != -1) {
						ands[i] = "!(" + ands[i].replaceAll("!=", "=") + ")";
					} else if (ands[i].indexOf(" NOT ") != -1) {
						ands[i] = "!(" + ands[i].replaceAll(" NOT ", "") + ")";
						ands[i] = ands[i].replaceAll(" LIKE ", "~=");
					} else if (ands[i].indexOf(" LIKE ") != -1) {
						ands[i] = "" + ands[i].replaceAll(" LIKE ", "~=") + "";
					}
				}
				filter += "(" + ands[i] + ")";
			}
			filter = "(&" + filter + ")";
			String spaces[] = filter.split(" ");
			filter = "";
			for (int i = 0; i < spaces.length; i++) {
				filter += spaces[i];
			}
		}
		String account = wheres[0];
		if (account.indexOf(" FROM ") != -1) {
			account = account.substring(
					account.indexOf(" FROM ") + " FROM ".length()).trim();
		} else {
			account = "";
		}
		Map mapSet = new HashMap();
		mapSet.put("account", account);// 用户名
		mapSet.put("filter", filter);// 过滤器
		return mapSet;
	}

	/**
	 * 给组添加用户
	 * 
	 * @param account
	 * @param member
	 * @return
	 */
	public boolean addMember(String account, String member) {
		BasicAttribute attr = new BasicAttribute("member");
		if (member == null || member.length() <= 0)
			return true;
		member = member + "," + this.ROOT;
		attr.add(member);
		BasicAttributes attributes = new BasicAttributes();
		attributes.put(attr);
		String attrID[] = { "member" };
		BasicAttributes addAttrs = new BasicAttributes();
		Attributes searchAttrs = findByAccount(account);

		try {
			for (int i = 0; i < attrID.length; i++) {
				Attribute getAttr = searchAttrs.get(attrID[i]);
				if (getAttr == null && attributes.get(attrID[i]) != null) {
					addAttrs.put(attributes.get(attrID[i]));
				} else if (getAttr != null && attributes.get(attrID[i]) != null) {
					Enumeration ent = getAttr.getAll();
					while (ent.hasMoreElements()) {
						if (ent.nextElement().toString().equalsIgnoreCase(
								member))
							return true;
					}
					addAttrs.put(attributes.get(attrID[i]));
				}
			}
			this.ctx.modifyAttributes(account, DirContext.ADD_ATTRIBUTE,
					addAttrs); // 添加属性
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 添加用户组信息失败 addMember  account=" + account + ";member="
							+ member);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"添加用户组信息失败！", e);
			return false;
		}
	}

	/**
	 * 人员部门变动时，删除以前部门的隶属关系
	 * 
	 * @param account
	 * @param member
	 * @return
	 */
	public boolean removeMember(String account, String member) {
		BasicAttribute attr = new BasicAttribute("member");
		if (member == null || member.length() <= 0)
			return true;
		member = member + "," + this.ROOT;
		BasicAttributes attributes = new BasicAttributes();
		attributes.put(attr);
		BasicAttributes addAttrs = new BasicAttributes();
		Attributes searchAttrs = findByAccount(account);
		BasicAttributes delAttrs = new BasicAttributes();
		try {

			Attribute getAttr = searchAttrs.get("member");
			if (getAttr != null) {
				Enumeration ent = getAttr.getAll();
				while (ent.hasMoreElements()) {
					String valeu = ent.nextElement().toString();
					if (valeu.equalsIgnoreCase(member))
						continue;
					attr.add(valeu);
				}

			}

			addAttrs.put(attr);
			delAttrs.put(getAttr);
			this.ctx.modifyAttributes(account, DirContext.REMOVE_ATTRIBUTE,
					delAttrs);// 删除属性
			this.ctx.modifyAttributes(account, DirContext.ADD_ATTRIBUTE,
					addAttrs); // 添加属性
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 删除用户组信息失败 removeMember  account=" + account
							+ ";member=" + member);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"删除用户组信息失败！", e);
			return false;
		}
	}

	/**
	 * 该方法需要ca证书，而且是SSL端口是636
	 * 
	 * @param account
	 * @param password
	 * @return
	 */
	public boolean mod_Pwd(String account) {
		if (this.ssl_ctx == null)
			return true;
		if (this.initUserPassword == null
				|| this.initUserPassword.length() <= 0)
			return true;
		ModificationItem[] mods = new ModificationItem[1];
		String newQuotedPassword = "\"" + this.initUserPassword + "\"";
		try {

			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			this.ssl_ctx.modifyAttributes(account, mods);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Problem mod_Pwd UnsupportedEncodingException: "
					+ e);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 添加密码出错 mod_Pwd  account=" + account, e);
			return false;
		} catch (NamingException e) {
			System.out.println("Problem mod_Pwd NamingException: " + e);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 添加密码出错 mod_Pwd  account=" + account, e);
			return false;
		}

		return true;
	}

	/**
	 * 查找某个dN项下的某个属性的值
	 * 
	 * @param account
	 * @param attrName
	 * @return
	 */
	public ArrayList getAttributes(String account, String attrName) {
		Attributes searchAttrs = findByAccount(account);
		ArrayList list = new ArrayList();
		try {
			if (searchAttrs != null) {
				Attribute getAttr = searchAttrs.get(attrName);
				if (getAttr != null) {
					Enumeration ent = getAttr.getAll();
					while (ent.hasMoreElements()) {
						list.add(ent.nextElement().toString());

					}

				}
			}

		} catch (NamingException e) {
			e.printStackTrace();
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"LADP 查找信息失败！ getAttributes  account=" + account
							+ ";attrName=" + attrName);
			Category.getInstance("com.hjsj.hrms.utils.LDAPTools").error(
					"查找信息失败！", e);
		}
		return list;
	}

	public int checkDNtype(String ldapDN) {

		Attributes searchAttrs = findByAccount(ldapDN);
		if (searchAttrs != null) {
			Attribute getAttr = searchAttrs.get("objectclass");
			if (getAttr != null) {
				Enumeration ent;
				try {
					ent = getAttr.getAll();
					String type = "";
					while (ent.hasMoreElements()) {
						type = ent.nextElement().toString();
						if ("organizationalUnit".equalsIgnoreCase(type))
							return this.ldap_Unit;
						else if ("organizationalPerson".equalsIgnoreCase(type))
							return this.ldapDN_User;
						else if ("person".equalsIgnoreCase(type))
							return this.ldapDN_User;
						else if ("user".equalsIgnoreCase(type))
							return this.ldapDN_User;
						else if ("group".equalsIgnoreCase(type))
							return this.ldapDN_Group;
					}
				} catch (NamingException e) {
					e.printStackTrace();
				}

			}
		}
		return 0;
	}

	public static void main(String[] args) {
		// String LDAPBASEDN="dc=chrdi,dc=com";
		// String LDAPIPADRESS="192.168.1.106";
		// String LDAPPORT="389";
		// String LDAPACCOUNT="cn=Administrator,cn=Users,dc=chrdi,dc=com";
		// LDAPACCOUNT="user10@chrdi.com";
		// LDAPACCOUNT="cn=user10,ou=ujcb,ou=602users,dc=chrdi,dc=com";
		// String LDAPPASSWORD="1";
		// LDAPPASSWORD="Abcd123456";
		// LDAPTools lDAPTools= new LDAPTools(LDAPBASEDN, LDAPIPADRESS,
		// LDAPPORT, LDAPACCOUNT,
		// LDAPPASSWORD);
		System.out.println("kaishi");
		// Attributes matchingAttributes= new BasicAttributes();
		// lDAPTools.createOrganizationUnit("ou=u01s,ou=602users","u18s");
		//
		// lDAPTools.searchByAttribute("cn=u05s,ou=u05s,ou=602users",matchingAttributes);
		// lDAPTools.createOrganizationUnit("ou=ujcb2s,ou=602users","ujcb2s");
		// lDAPTools.checkDNtype("OU=uzhglb,OU=ujtld,OU=602users");
		// lDAPTools.addMember("cn=u05s,ou=u05s,ou=602users","CN=xxx,OU=u05s,OU=602users");
		// String supDN="OU=u05s,OU=602users,DC=chrdi,DC=com";
		// String
		// gName=supDN.substring(supDN.toUpperCase().indexOf("OU=")+3,supDN.indexOf(","));
		// System.out.println(gName);
		// List dnlist = lDAPTools.getExeDN("ou=u01s,ou=602users");
		// List list=lDAPTools.getNodes(" select from "+"ou=602users where
		// objectclass =user");
		// for(int i=0;i<list.size();i++)
		{
			// System.out.println(list.get(i));
		}
		// lDAPTools.getAccount("select where orgcode=0106");
		// lDAPTools.removeMember("CN=u11s,OU=u11s,OU=602users","CN=yys000057,OU=u11s,OU=602users");
		/*
		 * String sql = "select where name=zml000052" ; List list =
		 * lDAPTools.getAccount(sql); BasicAttributes attributes = new
		 * BasicAttributes(); String attrID[] =
		 * {"userAccountControl"};//启用：512，禁用：514， 密码永不过期：66048
		 * attributes.put("userAccountControl","514");
		 * lDAPTools.modify("CN=zml000052,OU=u10s,OU=602users", attrID,
		 * attributes);
		 */
	}

}
