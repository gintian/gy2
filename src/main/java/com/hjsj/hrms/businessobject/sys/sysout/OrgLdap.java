package com.hjsj.hrms.businessobject.sys.sysout;

import com.hjsj.hrms.utils.LDAPTools;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrgLdap {

	private String hr_only_field = "";

	private ContentDAO dao = null;
	private Connection conn = null;
	private String orgcode = "postalcode";// 邮编
	private int atts_cutorgcode_len = "postalcode :".length();
	// private String baseDN="";
	private String LDAPROOT = "";

	public OrgLdap(String hr_only_field, ContentDAO dao) {
		this.hr_only_field = hr_only_field;
		this.dao = dao;
	}

	public OrgLdap(String hr_only_field, Connection connection) {
		this.hr_only_field = hr_only_field;
		this.dao = new ContentDAO(connection);
		this.conn = connection;
	}

	private LDAPTools ld = null;
	private boolean isConn = false;

	/**
	 * 
	 * @param baseDN
	 *            根DN
	 * @param list
	 *            机构信息 可以用这个类的getOrgInfo()方法获得
	 */
	public boolean exeOrg(String baseDN, List orgList, List userList) {
		if (orgList == null || orgList.size() <= 0) {
			if (userList == null || userList.size() <= 0) {
                return true;
            }
		}
		// this.baseDN=baseDN;
		this.ld = getLDAPTools();
		if (!isConn) {
            return false;
        }

		try {
			if (!isExitsByDN(baseDN)) {
				addOrg(baseDN);
			} else {

			}
			String sql = "";
			boolean isSync = false;
			String name = "";
			// ---------------------------机构新增和修改开始----------------------------
			for (int i = 0; i < orgList.size(); i++) {
				isSync = false;
				Map orgMap = (Map) orgList.get(i);
				String flag = (String) orgMap.get("flag");
				String grade = (String) orgMap.get("grade");
				String b0110_0 = (String) orgMap.get("b0110_0");
				String codeitemdesc = (String) orgMap.get("codeitemdesc");
				name = "u" + getNumStr(codeitemdesc);
				String parentid = (String) orgMap.get("parentid");
				if ("1".equals(flag) || "2".equals(flag)) {
					sql = "select where " + this.orgcode + "=" + b0110_0;
					/** *******************新增机构*************************** */
					if (!isExits(sql)) {
						String DN = "";
						if ("1".equals(grade)) {
							DN = baseDN;
						} else {
							DN = getOrgDN(parentid, baseDN);
						}
						if (DN != null && DN.length() > 0) {
							String eDN = "ou=" + name + "," + DN;
							String gDN = "cn=" + name + "," + eDN;
							String gName = name;
							Attributes attrs = ld.findByAccount(eDN);
							if (attrs != null) {
								// 如果DN找到，修改向里面添加orgcode
								if (attrs.get(this.orgcode) != null) {
									String old_orgCode = attrs
											.get(this.orgcode).toString()
											.substring(atts_cutorgcode_len);
									if (old_orgCode != null
											&& old_orgCode.equals(b0110_0)) {
										String attrID[] = { "" + this.orgcode
												+ "" };
										BasicAttribute attr = new BasicAttribute(
												"" + this.orgcode + "");
										attr.add(b0110_0);
										isSync = modOrg(eDN, attrID, attr);
									} else {
										int num = 0;
										while (isExitsByDN(eDN)) {
											num++;
											eDN = "ou= " + name + "_" + num
													+ "," + DN;
											gDN = "cn=" + name + "_" + num
													+ "," + eDN;
											gName = name + "_" + num;
										}
										orgMap.put("DN", eDN);
										isSync = addOrg(orgMap);
									}
								} else {
									String attrID[] = { this.orgcode };
									BasicAttribute attr = new BasicAttribute(
											this.orgcode);
									attr.add(b0110_0);
									isSync = modOrg(eDN, attrID, attr);
								}
								operateGroup(gDN, gName);// 添加组
							} else {
								int num = 0;
								while (isExitsByDN(eDN)) {
									num++;
									eDN = "ou= " + name + "_" + num + "," + DN;
									gDN = "cn=" + name + "_" + num + "," + eDN;
									gName = name + "_" + num;
								}
								// orgMap.put("DN", eDN+",dc=chrdi,dc=com");
								orgMap.put("DN", eDN);
								isSync = addOrg(orgMap);
								operateGroup(gDN, gName);
							}

						}
						/** *******************新增机构*************************** */
					} else {
						/** *******************修改机构*************************** */
						String DN = "";
						if ("1".equals(grade)) {
							DN = baseDN;
						} else {
							DN = getOrgDN(parentid, baseDN);
						}
						String eDN = "";
						String gDN = "";
						String gName = name;
						if (DN != null && DN.length() > 0) {
							eDN = "ou=" + name + "," + DN;
							gDN = "cn=" + name + "," + eDN;
						}
						List list = ld.getAccount(sql);
						String newOrgDN = "";
						if (!list.isEmpty() && list.size() == 1) {
							String oldOrgDN = (String) list.get(0);
							Attributes attrs = ld.findByAccount(oldOrgDN);
							if (oldOrgDN.toLowerCase()
									.indexOf(DN.toLowerCase()) != -1) {

								String oldName = attrs.get("name").toString()
										.substring("name :".length());
								if ((name).equalsIgnoreCase(oldName)) {
									Attribute desc = (Attribute) attrs
											.get("description");
									if (desc == null) {
										String attrID[] = { "description" };
										BasicAttribute attr = new BasicAttribute(
												"description");
										attr.add(codeitemdesc);
										isSync = modOrg(eDN, attrID, attr);
									}
									operateGroup(gDN, gName);
									sycnOrgTableFlag(flag, b0110_0);
									continue;
								}
							}
							newOrgDN = "ou=" + name + "," + DN;

							int num = 0;
							while (isExitsByDN(newOrgDN)) {
								newOrgDN = "ou=" + name + "_" + num + "," + DN;
								num++;
								gDN = "cn=" + name + "_" + num + "," + eDN;
								gName = name + "_" + num;
							}
							orgMap.put("DN", newOrgDN);
							isSync = addOrg(orgMap);
							// ------------------ 修改 ---------------------------
							List dnlist = ld.getExeDN(oldOrgDN);
							String oldDN = "";
							String newDN = "";
							String tempOrgDN = "ou=tempOrg," + baseDN;
							String tempDN = "";
							if (isExitsByDN(tempOrgDN)) {
								isSync = delDN(tempOrgDN);
							}
							isSync = addOrg(tempOrgDN);
							List tempList = new ArrayList();
							List modList = new ArrayList();
							if (!dnlist.isEmpty()) {
								for (int k = dnlist.size() - 1; k > 0; k--) {
									oldDN = (String) dnlist.get(k);
									String repDN = oldOrgDN.toUpperCase() + "&";
									newDN = (oldDN + "&").replace(repDN,
											newOrgDN);
									modList.add(newDN);
									int type = ld.checkDNtype(oldDN);
									LazyDynaBean bean = new LazyDynaBean();
									if (type == ld.ldapDN_Group) {
										String nn = oldDN.split(",")[0];
										String oldGName = oldOrgDN.substring(
												oldOrgDN.toUpperCase().indexOf(
														"OU=") + 3, oldOrgDN
														.indexOf(","));
										String curGName = nn
												.substring(nn.toUpperCase()
														.indexOf("CN=") + 3);
										if (oldGName.equalsIgnoreCase(curGName)) {
											tempDN = "CN=G" + gName + ","
													+ tempOrgDN;
										} else {
											nn = nn.toUpperCase().substring(
													nn.indexOf("CN=") + 3);
											tempDN = "CN=G" + nn + ","
													+ tempOrgDN;
										}

										bean.set("type", "G");
									} else {
										tempDN = oldDN.split(",")[0] + ","
												+ tempOrgDN;
										bean.set("type", "");
									}
									bean.set("dn", tempDN);
									tempList.add(bean);
									ld.modifyDN(oldDN, tempDN);
								}
								if (oldOrgDN.equalsIgnoreCase((String) dnlist
										.get(0))) {
									String delDNname = (String) dnlist.get(0);
									isSync = delDN(delDNname);
									dnlist.clear();
								}
							}

							for (int j = tempList.size() - 1; j >= 0; j--) {
								LazyDynaBean bean = (LazyDynaBean) tempList
										.get(j);
								tempDN = (String) bean.get("dn");
								if (isExitsByDN(tempDN)) {
									isSync = ld.modifyDN(tempDN,
											(String) modList.get(j));
								}
							}
							if (isExitsByDN(tempOrgDN)) {
								isSync = delDN(tempOrgDN);
							}
							// 删除组oldOrgDN；
							if (isSync) {
								gName = oldOrgDN.substring(oldOrgDN
										.toUpperCase().indexOf("OU=") + 3,
										oldOrgDN.indexOf(","));
								gDN = "cn=" + gName + "," + newOrgDN;
								if (isExitsByDN(tempOrgDN)) {
                                    delDN(gDN);
                                }
							}

						}
					}
					if (isSync) {
						sycnOrgTableFlag(flag, b0110_0);
					}

					/** *******************修改机构*************************** */
				}
			}
			// ---------------------------机构新增和修改结束----------------------------

			// -----------------------------人员操作开始------------------------------

			for (int i = 0; i < userList.size(); i++) {
				isSync = false;
				Map userMap = (Map) userList.get(i);
				String flag = (String) userMap.get("flag");
				String c01rn = (String) userMap.get(this.hr_only_field);
				sql = "select where name=" + c01rn;
				if ("1".equals(flag) || "2".equals(flag)) {
					if (!isExits(sql)) {
						isSync = addUser(userMap);// ----新增人员
					} else {
						isSync = modUser(userMap);// ----修改人员
					}
				} else if ("3".equals(flag)) {
					if (isExits(sql)) {// ----人员存在
						// isSync=delUser(c01rn,userMap);// ----删除人员
						isSync = userOfDHCP(c01rn);// 禁用账户
						//将删除人员放到指定路径下
						String LDAP_LeaveJob_URL = SystemConfig.getPropertyValue("LDAP_LeaveJob_URL");
						if(LDAP_LeaveJob_URL!=null&&LDAP_LeaveJob_URL.length()>0)
						{
							String leaveJob = "ou="+LDAP_LeaveJob_URL+"," + baseDN;
							if (!isExitsByDN(leaveJob)) {
								this.addOrg(leaveJob);
							}
							List list = ld.getAccount(sql);
							if (list != null && list.size() == 1) {
								String userDN = (String)list.get(0);
								ld.modifyDN(userDN, "cn=" + c01rn + "," + leaveJob);
							}
						}						
					}
				}
				if (isSync) {
                    sycnUserTableFlag(userMap);
                }
			}
			if (userList != null && userList.size() > 0) {
                delHrUserNOSaveforHrTable(baseDN);
            }
			// -----------------------------人员操作结束------------------------------

			// ------------------------------机构删除开始------------------------------
			boolean isdel = false;
			String SyncDELLDAPDEPT = SystemConfig
					.getPropertyValue("SyncDELLDAPDEPT");
			if (SyncDELLDAPDEPT != null
					&& "true".equalsIgnoreCase(SyncDELLDAPDEPT)) {
				isdel = true;
			}
			for (int i = orgList.size() - 1; i >= 0; i--) {
				isSync = false;
				Map orgMap = (Map) orgList.get(i);
				String flag = (String) orgMap.get("flag");
				if ("3".equals(flag)) {
					String b0110_0 = (String) orgMap.get("b0110_0");

					sql = "select where " + this.orgcode + "=" + b0110_0;
					List list = ld.getAccount(sql);
					if (!list.isEmpty() && list.size() == 1) {
						if (isdel) {
							isSync = delDN((String) list.get(0));
						} else {
							isSync = this.clearOrgcode((String) list.get(0));
						}
					}

					if (isSync) {
                        sycnOrgTableFlag(flag, b0110_0);
                    }
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (this.ld != null) {
                this.ld.getClose();
            }
		}

	}

	/**
	 * 根据机构信息 来创建机构
	 * 
	 * @param orgMap
	 */
	private boolean addOrg(Map orgMap) {

		BasicAttribute attribute = new BasicAttribute("objectClass");
		attribute.add("organizationalUnit");
		// attribute.add("org");

		String b0110_0 = (String) orgMap.get("b0110_0");
		String codeitemdesc = (String) orgMap.get("codeitemdesc");
		if (b0110_0 == null || b0110_0.length() < 1 || codeitemdesc == null
				|| codeitemdesc.length() < 1) {
            return false;
        }
		String dn = (String) orgMap.get("DN");
		BasicAttributes attributes = new BasicAttributes();
		attributes.put(this.orgcode, b0110_0);
		attributes.put("description", codeitemdesc);
		attributes.put(attribute);
		return ld.addUnit(dn, attributes);
	}

	/**
	 * 根据DN 直接创建 机构 为了创建 根DN用
	 * 
	 * @param DN
	 */
	private boolean addOrg(String DN) {
		BasicAttributes attributes = new BasicAttributes();
		BasicAttribute attribute = new BasicAttribute("objectClass");
		attribute.add("organizationalUnit");
		attributes.put(attribute);
		return ld.add(DN, attributes);
	}

	private boolean modOrg(String DN, String attrID[], BasicAttribute attr) {
		BasicAttributes attrs = new BasicAttributes();
		attrs.put(attr);
		return ld.modify(DN, attrID, attrs);
	}

	private boolean addUser(Map userMap) {

		BasicAttributes attributes = new BasicAttributes();
		BasicAttribute attribute = new BasicAttribute("objectClass");
		attribute.add("top");
		attribute.add("person");
		attribute.add("organizationalPerson");
		attribute.add("user");
		String e01a1 = (String) userMap.get("e01a1");// 职位
		String c01rn = (String) userMap.get(this.hr_only_field);// LDAP name属性
		String e0122 = switchUMCode((String) userMap.get("e0122_0"));
		String e0122_0 = ((String) userMap.get("e0122_0"));
		String c01s3 = (String) userMap.get("c01s3");

		if (c01rn == null || c01rn.length() < 1) {
            return false;
        }
		attributes.put("name", c01rn);
		String a0101 = userMap.get("a0101") == null ? "" : (String) userMap
				.get("a0101");
		if (!"".equals(a0101) && e0122 != null && e0122.length() > 0) {
			attributes.put("description", switchPinyin(e0122) + " " + a0101);
		}
		if (!"".equals(a0101)) {
			attributes.put("sn", a0101);
			attributes.put("givenName", a0101);
			attributes.put("displayName", a0101);
		} else {
			return false;
		}
		if (e0122 != null && e0122.length() > 0) {
			attributes.put("department", e0122);
		}
		if (c01s3 != null && c01s3.length() > 0) {
			attributes.put("mail", c01s3);
		}
		if (e01a1 != null && e01a1.length() > 0) {
			attributes.put("title", e01a1);
		}
		attributes.put("sAMAccountName", c01rn);// 用户登陆名称
		attributes.put("userPrincipalName", c01rn + "@chrdi.com");// UPN，完整的用户登陆名
		attributes.put("userAccountControl", "514");
		String supDN = getOrgDN(e0122_0, "");
		if (supDN == null || supDN.length() < 1) {
			Category.getInstance(
					"com.hjsj.hrms.businessobject.sys.sysout.OrgLdap").error(
					"增加人失败，没有找到对应的机构信息！请查看组织机构信息或编号是否正确：名称=" + e0122 + ";编号="
							+ e0122_0);
			return false;
		}
		String dn = "cn=" + c01rn + "," + supDN;
		attributes.put(attribute);
		boolean isCorrect = this.ld.add(dn, attributes);// 新增操作
		if (isCorrect) {
			setMember(e0122_0, dn);// 添加隶属关系
			this.ld.mod_Pwd(dn);
		}
		return isCorrect;
	}

//	private boolean delUser(String c01rn, Map userMap) {
//		String sql = "select where name=" + c01rn;
//		List list = ld.getAccount(sql);
//		if (!list.isEmpty() && list.size() == 1) {
//			String e0122_0 = ((String) userMap.get("e0122_0"));// 部门编码
//			String supDN = getOrgDN(e0122_0, "");
//			if (supDN != null && supDN.length() > 0) {
//				String gName = supDN.substring(supDN.toUpperCase().indexOf(
//						"OU=") + 3, supDN.indexOf(","));
//				String gDN = "cn=" + gName + "," + supDN;
//				if (isExitsByDN(gDN)) {
//					this.ld.removeMember(gDN, (String) list.get(0));
//				}
//			}
//			return ld.delete((String) list.get(0));
//		} else
//			return true;
//	}

	/**
	 * 禁用
	 * 
	 * @param c01rn
	 * @param userMap
	 * @return
	 */
	public boolean userOfDHCP(String c01rn) {
		String sql = "select where name=" + c01rn;
		List list = ld.getAccount(sql);
		if (!list.isEmpty() && list.size() == 1) {
			String account = (String) list.get(0);
			BasicAttributes attributes = new BasicAttributes();
			String attrID[] = { "userAccountControl" };// 启用：512，禁用：514，
			// 密码永不过期：66048
			attributes.put("userAccountControl", "514");
			return this.ld.modify(account, attrID, attributes);
		} else {
            return true;
        }
	}

	/**
	 * 清空orgcode
	 * 
	 * @param c01rn
	 * @return
	 */
	public boolean clearOrgcode(String account) {

		BasicAttributes attributes = new BasicAttributes();
		String attrID[] = { this.orgcode };
		attributes.put(this.orgcode, "");
		return this.ld.modify(account, attrID, attributes);
	}

	public boolean modUser(Map userMap) {

		String e01a1 = (String) userMap.get("e01a1");// 职位
		String c01rn = (String) userMap.get(this.hr_only_field);// LDAP name属性
		String e0122 = switchUMCode((String) userMap.get("e0122_0"));// 部门
		String e0122_0 = ((String) userMap.get("e0122_0"));// 部门编码
		String c01s3 = (String) userMap.get("c01s3");// Email
		/** ********************人员信息变动****************************** */
		BasicAttributes attributes = new BasicAttributes();
		String attrID[] = { "givenName", "displayName", "mail", "department",
				"description", "title", "sAMAccountName", "userPrincipalName",
				"sn"
		// "name",
		};// 需要修改的参数 用于判断
		if (c01rn == null || c01rn.length() < 1) {
            return true;
        }
		attributes.put("name", c01rn);
		String a0101 = userMap.get("a0101") == null ? "" : (String) userMap
				.get("a0101");
		if (!"".equals(a0101) && e0122 != null && e0122.length() > 0) {
			attributes.put("description", switchPinyin(e0122) + " " + a0101);
		}
		if (!"".equals(a0101)) {
			attributes.put("sn", a0101);
			attributes.put("givenName", a0101);
			attributes.put("displayName", a0101);
		} else {
			return true;
		}
		if (e0122 != null && e0122.length() > 0) {
			attributes.put("department", e0122);
		}
		if (c01s3 != null && c01s3.length() > 0) {
			attributes.put("mail", c01s3);
		}
		if (e01a1 != null && e01a1.length() > 0) {
			attributes.put("title", e01a1);
		}
		attributes.put("sAMAccountName", c01rn);// 用户登陆名称
		attributes.put("userPrincipalName", c01rn + "@chrdi.com");// UPN，完整的用户登陆名
		String sql = "select where name=" + c01rn;
		List list = ld.getAccount(sql);
		if (list.isEmpty() || list.size() != 1) {
            return true;
        }
		String account = (String) list.get(0);
		if (e0122 != null && e0122.length() > 0) {
			ArrayList arrls = this.ld.getAttributes(account, "department");
			if (arrls != null && arrls.size() > 0) {
				String ldapDepartment = (String) arrls.get(0);
				if (ldapDepartment != null
						&& !ldapDepartment.equalsIgnoreCase(e0122)) {
					String name = "u" + getNumStr(ldapDepartment);
					String groupDN = "CN=" + name + ","
							+ account.substring(account.indexOf(",") + 1);
					if (isExitsByDN(groupDN)) {
						this.ld.removeMember(groupDN, account);
					}
				}
			}
		}

		boolean isMod = this.ld.modify(account, attrID, attributes);
		if (!isMod) {
            return false;
        }
		/** ********************人员信息变动****************************** */
		/** ********************人员机构变动****************************** */
		String DN = "cn=" + c01rn + "," + getOrgDN(e0122_0, "");
		if (!account.equalsIgnoreCase(DN)) {
			isMod = ld.modifyDN(account, DN);
			if (isMod) {
				setMember(e0122_0, DN);// 添加隶属关系
			}
		}
		return isMod;
		/** ********************人员机构变动****************************** */
	}

	public List getUserInfo() {
		String sql = "select e0122_0,a0101,flag,e01a1," + this.hr_only_field
				+ ",c01s3" + " from t_hr_view where flag<>0 and "
				+ this.hr_only_field + " is not null order by e0122_0";
		List list = new ArrayList();
		if (dao == null) {
            return null;
        }
		try {
			RowSet rs = dao.search(sql);
			while (rs.next()) {
				Map map = new HashMap();
				String e0122_0 = rs.getString("e0122_0");
				String a0101 = rs.getString("a0101");
				String e01a1 = rs.getString("e01a1");
				String c01rn = rs.getString(this.hr_only_field);
				String flag = rs.getString("flag");
				String c01s3 = rs.getString("c01s3");
				map.put("e0122_0", e0122_0);
				map.put("a0101", a0101);
				map.put("e01a1", e01a1);
				map.put(this.hr_only_field, c01rn);
				map.put("c01s3", c01s3);
				map.put("flag", flag);
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据机构信息来删除机构 并且 删除机构下的所以成员
	 * 
	 * @param orgMap
	 */
	public void delAllOrgToId(String b0110_0) {
		if (b0110_0 == null || b0110_0.length() < 1) {
            return;
        }
		String sql = "select where " + this.orgcode + "=" + b0110_0;
		List listDN = ld.getAccount(sql);
		if (!listDN.isEmpty() && listDN.size() == 1) {
			String DN = (String) listDN.get(0);
			String account = "";
			List list = ld.getExeDN(DN);
			while (list != null && !list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					account = (String) list.get(i);
					ld.delete(account);
				}
				list = ld.getExeDN(DN);
			}
		}
	}

	/**
	 * 根据DN来删除机构 不删除机构下的所以成员
	 * 
	 * @param DN
	 */
	public boolean delDN(String DN) {
		if (DN == null || DN.length() < 1) {
            return true;
        }
		;
		if (isExitsByDN(DN)) {
			return ld.delete(DN);
		} else {
            return true;
        }
	}

	/**
	 * 根据机构编号获得DN
	 * 
	 * @param id
	 * @return
	 */
	private String getOrgDN(String e0122, String baseDN) {
		String sql = "select where " + this.orgcode + "=" + e0122;
		List list = ld.getAccount(sql);
		if (list.isEmpty() || list.size() != 1) {
			if (baseDN != null && baseDN.length() > 0) {
                return baseDN;
            } else {
                return null;
            }
		} else {
			return (String) list.get(0);
		}
	}

	/**
	 * 得到需要执行的机构信息
	 * 
	 * @return List
	 */
	public List getOrgInfo() {
		String sql = "select b0110_0,codeitemdesc,parentid,grade,flag"
				+ " from t_org_view where flag<>0 and codeSetId ='UM' order by b0110_0,A0000";
		List list = new ArrayList();
		if (dao == null) {
            return null;
        }
		try {
			RowSet rs = dao.search(sql);
			while (rs.next()) {
				Map map = new HashMap();
				String b0110_0 = rs.getString("b0110_0");
				String codeitemdesc = rs.getString("codeitemdesc");
				String parentid = rs.getString("parentid");
				String grade = rs.getString("grade");
				String flag = rs.getString("flag");

				map.put("b0110_0", b0110_0);
				map.put("codeitemdesc", codeitemdesc);
				map.put("parentid", parentid);
				map.put("grade", grade);
				map.put("flag", flag);
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private LDAPTools getLDAPTools() {
		this.LDAPROOT = SystemConfig.getPropertyValue("LDAPROOT");// 获得配置文件信息
		String LDAPIPADRESS = SystemConfig.getPropertyValue("LDAPIPADRESS");// 获得配置文件信息
		String LDAPPORT = SystemConfig.getPropertyValue("LDAPPORT");// 获得配置文件信息
		String LDAPACCOUNT = SystemConfig.getPropertyValue("LDAPACCOUNT");// 获得配置文件信息
		String LDAPPASSWORD = SystemConfig.getPropertyValue("LDAPPASSWORD");// 获得配置文件信息
		try {
			LDAPTools lDAPTools = new LDAPTools(LDAPROOT, LDAPIPADRESS,
					LDAPPORT, LDAPACCOUNT, LDAPPASSWORD);
			this.isConn = true;
			return lDAPTools;
		} catch (Exception e) {
			isConn = false;
		}
		return null;
	}

	public void getClose() {
		ld.getClose();
	}

	// 判断用户是否存在
	private boolean isExitsByDN(String account) {
		Attributes attributes = this.ld.findByAccount(account);
		if (attributes != null) {
			return true;
		} else {
			return false;
		}
	}

	// 判断用户是否存在
	private boolean isExits(String sql) {
		List list = this.ld.find(sql);
		if (list.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 转换拼音
	 * 
	 * @param value
	 * @return
	 */
	private static String switchPinyin(String value) {
		if (value == null || value.length() <= 0) {
            return "";
        }
		String pinyin = PubFunc.getPinym(value);
		return pinyin;
	}

	/**
	 * 将部门编码转换为汉字
	 * 
	 * @param e0122
	 * @return
	 */
	private String switchUMCode(String e0122) {
		if (e0122 == null || e0122.length() <= 0) {
            return "";
        }
		String e0122_str = AdminCode.getCodeName("UM", e0122);
		return e0122_str;
	}

//	private String getMemberOf(String value, String baseDN) {
//		if (value == null || value.length() <= 0)
//			return "";
//		StringBuffer memberOf = new StringBuffer();
//		memberOf.append("CN=u" + getNumStr(value) + ",");
//		memberOf.append(baseDN + ",DC=chrdi,DC=com");
//		return memberOf.toString();
//	}

	private String getNumStr(String value) {
		if (value == null || value.length() <= 0) {
            return "";
        }
		if (isChinaNum(value)) {
			String str = zhuanHuanNum(value);
			str = PubFunc.getPinym(str);
			Pattern pattern = Pattern.compile("[0-9]*");
			int num = 0;
			for (int i = 0; i < str.length(); i++) {
				char bit = str.charAt(i);
				Matcher isNum = pattern.matcher(bit + "");
				if (isNum.matches()) {
                    num++;
                } else {
                    break;
                }
			}
			if (num == 1) {
                str = "0" + str;
            }
			return str;
		} else {
			return switchPinyin(value);
		}

	}

	public String chn2digit(String chnStr) {
		// init map
		java.util.Map unitMap = new java.util.HashMap();
		unitMap.put("十", "10");
		unitMap.put("百", "100");
		unitMap.put("千", "1000");
		unitMap.put("万", "10000");
		unitMap.put("亿", "100000000");

		java.util.Map numMap = new java.util.HashMap();
		numMap.put("零", "0");
		numMap.put("一", "1");
		numMap.put("二", "2");
		numMap.put("三", "3");
		numMap.put("四", "4");
		numMap.put("五", "5");
		numMap.put("六", "6");
		numMap.put("七", "7");
		numMap.put("八", "8");
		numMap.put("九", "9");

		// 队列
		List queue = new ArrayList();
		long tempNum = 0;
		for (int i = 0; i < chnStr.length(); i++) {
			char bit = chnStr.charAt(i);
			// 数字
			if (numMap.containsKey(bit + "")) {

				tempNum = tempNum
						+ Integer.parseInt((String) numMap.get(bit + ""));

				// 一位数、末位数、亿或万的前一位进队列
				if (chnStr.length() == 1
						|| i == chnStr.length() - 1
						|| (i + 1 < chnStr.length() && (chnStr.charAt(i + 1) == '亿' || chnStr
								.charAt(i + 1) == '万'))) {
					queue.add("" + tempNum);
				}
			}
			// 单位
			else if (unitMap.containsKey(bit + "")) {

				// 遇到十 转换为一十、临时变量进队列
				if (bit == '十') {
					if (tempNum != 0) {
						tempNum = tempNum
								* Integer.parseInt((String) unitMap.get(bit
										+ ""));
					} else {
						tempNum = 1 * Integer.parseInt((String) unitMap.get(bit
								+ ""));
					}
					queue.add("" + tempNum);
					tempNum = 0;
				}

				// 遇到千、百 临时变量进队列
				if (bit == '千' || bit == '百') {
					if (tempNum != 0) {
						tempNum = tempNum
								* Integer.parseInt((String) unitMap.get(bit
										+ ""));
					}
					queue.add("" + tempNum);
					tempNum = 0;
				}

				// 遇到亿、万 队列中各元素依次累加*单位值、清空队列、新结果值进队列
				if (bit == '亿' || bit == '万') {
					long tempSum = 0;
					if (queue.size() != 0) {
						for (int j = 0; j < queue.size(); j++) {
							tempSum += Integer.parseInt((String) queue.get(j));
						}
					} else {
						tempSum = 1;
					}
					tempNum = tempSum
							* Integer.parseInt((String) unitMap.get(bit + ""));
					queue.clear();// 清空队列
					queue.add("" + tempNum);// 新结果值进队列
					tempNum = 0;
				}
			}
		}
		long sum = 0;
		for (int i = 0; i < queue.size(); i++) {
			sum += Integer.parseInt((String) queue.get(i));
		}
		// System.out.println(sum);
		return sum + "";
	}

	public String zhuanHuanNum(String chnStr) {
		java.util.Map numMap = new java.util.HashMap();
		numMap.put("零", "0");
		numMap.put("一", "1");
		numMap.put("二", "2");
		numMap.put("三", "3");
		numMap.put("四", "4");
		numMap.put("五", "5");
		numMap.put("六", "6");
		numMap.put("七", "7");
		numMap.put("八", "8");
		numMap.put("九", "9");
		numMap.put("十", "10");
		// 队列
		// List queue = new ArrayList();
		StringBuffer ss = new StringBuffer();
		StringBuffer buf = new StringBuffer();
		boolean isCorrect = false;
		for (int i = 0; i < chnStr.length(); i++) {
			char bit = chnStr.charAt(i);
			if (numMap.containsKey(bit + "")) {
				ss.append(bit);
				isCorrect = true;
			} else {
				isCorrect = false;
				if (ss == null || ss.length() <= 0) {
                    buf.append(bit);
                }
			}
			if (!isCorrect && ss != null && ss.length() > 0) {
				buf.append(chn2digit(ss.toString()));
				ss.setLength(0);
				buf.append(bit);
			}
		}
		if (buf == null || buf.length() <= 0) {
			buf.append(chn2digit(ss.toString()));
		}
		// output
		return buf.toString();
	}

	/**
	 * 返回同步结果后，将flag更新为已同步
	 * 
	 * @param map
	 * @param dao
	 */
	private void sycnUserTableFlag(Map map) {
		if (map == null) {
            return;
        }
		String pk_value = (String) map.get(this.hr_only_field);
		String flag = (String) map.get("flag");
		if (flag == null || flag.length() <= 0 || "0".equals(flag)) {
            return;
        }
		if (pk_value == null || pk_value.length() <= 0) {
            return;
        }
		String sql = "update t_hr_view set flag=0 where flag=" + flag + " and "
				+ this.hr_only_field + "='" + pk_value + "'";
		try {
			this.dao.update(sql);
		} catch (SQLException e) {
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					"LADP更新后返回数据更新同步表出错：" + pk_value);
			e.printStackTrace();
		}
	}

	private void sycnOrgTableFlag(String flag, String pk_value) {
		if (flag == null || flag.length() <= 0 || "0".equals(flag)) {
            return;
        }
		if (pk_value == null || pk_value.length() <= 0) {
            return;
        }
		String sql = "update t_org_view set flag=0 where flag=" + flag
				+ " and b0110_0='" + pk_value + "'";
		try {
			this.dao.update(sql);
		} catch (SQLException e) {
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					"LADP更新后返回数据更新同步表出错：" + pk_value);
			e.printStackTrace();
		}
	}

	private boolean isChinaNum(String str) {
		if (str.indexOf("零") != -1) {
			return true;
		} else if (str.indexOf("一") != -1) {
			return true;
		} else if (str.indexOf("二") != -1) {
			return true;
		} else if (str.indexOf("三") != -1) {
			return true;
		} else if (str.indexOf("四") != -1) {
			return true;
		} else if (str.indexOf("五") != -1) {
			return true;
		} else if (str.indexOf("六") != -1) {
			return true;
		} else if (str.indexOf("七") != -1) {
			return true;
		} else if (str.indexOf("八") != -1) {
			return true;
		} else if (str.indexOf("九") != -1) {
			return true;
		} else if (str.indexOf("十") != -1) {
			return true;
		} else if (str.indexOf("百") != -1) {
			return true;
		} else if (str.indexOf("千") != -1) {
			return true;
		} else if (str.indexOf("万") != -1) {
			return true;
		} else if (str.indexOf("亿") != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 解决内部bug a0100最大人被删，然后又新加人回到值对应t_hr_view中对应a0100人值做了新增，但不标识对应职工编码的人是删除
	 * 
	 * @param baseDN
	 */
	private void delHrUserNOSaveforHrTable(String baseDN) {
		String hardINTOLDAPUser = SystemConfig
				.getPropertyValue("HARDINTOLDAPUSER");
		if (hardINTOLDAPUser != null
				&& "false".equalsIgnoreCase(hardINTOLDAPUser)) {
			// 不允许手工同步
		} else {
            return;
        }
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		String destTab = "ldap_sync_hr";
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("name", "name");
			field.setDatatype(DataType.STRING);
			field.setLength(200);
			table.addField(field);
			field = new Field("dn", "dn");
			field.setDatatype(DataType.STRING);
			field.setLength(200);
			table.addField(field);
			try {
				dbw.createTable(table);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dbmodel.reloadTableModel(destTab);
		}
		try {
			dao.delete("delete from " + destTab, new ArrayList());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 查找baseDN下所有的人员
		List dnlist = this.ld.getNodes(" select from " + baseDN
				+ " where objectclass =user");
		if (!dnlist.isEmpty()) {

			RowSet rs = null;
			try {
				StringBuffer sql = new StringBuffer();
				sql.append("select count(*) aa from t_hr_view where");
				sql
						.append(" flag=0 and " + this.hr_only_field
								+ " is not null");
				rs = dao.search(sql.toString());
				if (rs.next()) {
					int count = rs.getInt("aa");
					if (count >= dnlist.size()) {
						return;
					}
				}
				String dn = "";
				String name = "";
				ArrayList in_list = new ArrayList();
				for (int i = 0; i < dnlist.size(); i++) {
					dn = (String) dnlist.get(i);
					if (dn.toUpperCase().indexOf("CN=") != -1) {
						int start = dn.toUpperCase().indexOf("CN=") + 3;
						int end = dn.toUpperCase().indexOf(",");
						if (start < end) {
                            name = dn.substring(start, end);
                        }
						ArrayList list = new ArrayList();
						list.add(name);
						list.add(dn);
						in_list.add(list);
					}
				}
				dao.batchInsert("insert into " + destTab
						+ "(name,dn)values(?,?)", in_list);// 将人员数据放入临时表
				sql.setLength(0);
				sql.append("select * from " + destTab + "");
				sql.append(" where NOT EXISTS(");
				;
				sql.append("select 1 from t_hr_view hr where "
						+ this.hr_only_field + " is not null");
				sql.append(" and flag=0");
				sql.append(" and hr." + this.hr_only_field + "=" + destTab
						+ ".name)");
				rs = dao.search(sql.toString());
				while (rs.next()) {
					dn = rs.getString("dn");
					if (dn != null
							&& dn.toUpperCase().indexOf(baseDN.toUpperCase()) != -1) {
                        delDN(dn);
                    }
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
	}

	/**
	 * 操作组织单元
	 * 
	 * @param ldapGroupDN
	 * @param gName
	 * @return
	 */
	private boolean operateGroup(String ldapGroupDN, String gName) {
		Attributes attrs = ld.findByAccount(ldapGroupDN);
		if (attrs == null) {
			BasicAttribute attribute = new BasicAttribute("objectClass");
			attribute.add("group");
			BasicAttributes attributes = new BasicAttributes();
			attributes.put("sAMAccountName", gName);
			attributes.put(attribute);
			return ld.addGourp(ldapGroupDN, attributes);
		}
		return true;
	}

	/**
	 * 给组添加用户
	 * 
	 * @param e0122_0
	 * @param DN
	 * @return
	 */
	private boolean setMember(String e0122_0, String DN) {
		String supDN = getOrgDN(e0122_0, "");
		if (supDN != null && supDN.length() > 0) {
			String gName = supDN.substring(
					supDN.toUpperCase().indexOf("OU=") + 3, supDN.indexOf(","));
			String gDN = "cn=" + gName + "," + supDN;
			operateGroup(gDN, gName);// 用户组
			return this.ld.addMember(gDN, DN);
		}
		return true;
	}
}
