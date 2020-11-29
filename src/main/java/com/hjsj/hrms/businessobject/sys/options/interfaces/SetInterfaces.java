package com.hjsj.hrms.businessobject.sys.options.interfaces;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.service.core.User;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import javax.sql.RowSet;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class SetInterfaces {
	private Connection conn;
	private String xmlContent = "";
	private SetInterfacesXml setInterfacesXml = null;

	public SetInterfaces(Connection conn) {
		this.conn = conn;
		RecordVo vo = new RecordVo("constant");
		vo.setString("constant", "HR_SERVICE");
		String xmlContent = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			vo = dao.findByPrimaryKey(vo);
			if (vo != null) {
                xmlContent = vo.getString("str_value");
            }
			// System.out.println(xmlContent);
			this.setInterfacesXml = new SetInterfacesXml(xmlContent);

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	private HashMap getEmployItem(String str_path) {
		ArrayList list = this.setInterfacesXml.getHrServiceList(str_path);
		HashMap setidMap = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean obean = (LazyDynaBean) list.get(i);
			String src = (String) obean.get("src");
			String itemid = (String) obean.get("dest");
			if (itemid == null || itemid.length() <= 0) {
                continue;
            }
			String codesetid = (String) obean.get("codesetid");
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			fielditem.setViewvalue(src);
			String fieldsetid = fielditem.getFieldsetid();
			ArrayList fieldlist = (ArrayList) setidMap.get(fieldsetid);
			if (fieldlist != null && fieldlist.size() > 0) {
				fieldlist.add(fielditem);
				setidMap.put(fieldsetid, fieldlist);
			} else {
				fieldlist = new ArrayList();
				fieldlist.add(fielditem);
				setidMap.put(fieldsetid, fieldlist);
			}
		}
		/*
		 * Iterator it=hashMap.entrySet().iterator();
		 * 
		 * while(it.hasNext()){ Map.Entry entry=(Map.Entry)it.next(); String
		 * key=(String)entry.getKey(); String value=(String)entry.getValue();
		 */
		return setidMap;
	}

	/**
	 * 增加人员
	 * 
	 * @param user
	 * @param impmode
	 * @return
	 */
	public String createUser(User user) {
		// boolean isCorrect=true;
		String isCorrect = "false";
		String userbase = "Usr";
		String setname = "A01";
		String tableName = userbase + setname;

		String impmode = this.setInterfacesXml.getHrServiceParam("/param",
				"impmode");
		String A0100 = "";
		try {
			A0100 = getUserId(userbase + setname);
			HashMap setidMap = getEmployItem("/param/user/rec");
			Iterator it = setidMap.entrySet().iterator();
			String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
					"keyfield");
			String marker = setInterfacesXml.getHrServiceParam("/param",
					"marker");
			if (marker == null || marker.length() <= 0) {
                marker = "0";
            }
			Object obj = getUserClassValue(user, "userId");
			// System.out.println("orgid="+user.getOrgId());
			String chitemstrvalue = (String) obj;
			boolean isB0110 = false;
			String childid = "";
			;
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				StringBuffer fields = new StringBuffer();
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				tableName = userbase + key;
				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						if ("priority".equalsIgnoreCase(
                                fielditem.getViewvalue())) {
							continue;
						}
						if (chitemid != null
								&& chitemid.equalsIgnoreCase(fielditem
										.getItemid())) {
							continue;
						}
						fields.append(fielditem.getItemid());
						obj = getUserClassValue(user, fielditem.getViewvalue());
						if (obj != null) {
							String strvalue = "";
							if (fielditem.getViewvalue() != null
									&& "onboardTime"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								Date date = (Date) obj;
								strvalue = DateUtils.format(date,
										"yyyy.MM.dd HH:mm:ss");
							} else if (fielditem.getViewvalue() != null
									&& "birthday"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								Date date = (Date) obj;
								strvalue = DateUtils.format(date,
										"yyyy.MM.dd HH:mm:ss");
							} else if (fielditem.getViewvalue() != null
									&& "sex"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								Integer retval = (Integer) obj;
								strvalue = retval.toString();
								if ("1".equals(strvalue)) {
									strvalue = "女";
								} else if ("2".equals(strvalue)) {
									strvalue = "男";
								}
							} else if (fielditem.getViewvalue() != null
									&& "orgId"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								if ("b0110".equalsIgnoreCase(
                                        fielditem.getItemid())) {
                                    isB0110 = true;
                                } else {
                                    childid = (String) obj;
                                }
								strvalue = (String) obj;
								// strvalue=getB0110FromCorcode(strvalue);

							} else {
								strvalue = (String) obj;
							}
							if (strvalue == null || strvalue.length() <= 0) {
								fieldvalues.append("null");
							} else {
								if ("0".equals(fielditem.getCodesetid())
										|| "UN"
												.equals(fielditem.getCodesetid())) {
									if ("D".equals(fielditem.getItemtype())) {
										fieldvalues.append(PubFunc
												.DateStringChange(strvalue));
									} else if ("N".equals(fielditem
											.getItemtype())) {
										fieldvalues.append(strvalue);
									} else {
										fieldvalues
												.append("'" + strvalue + "'");
									}
								} else {
									if (impmode != null && "1".equals(impmode)) {
										String value = getCodeItem(fielditem
												.getCodesetid(), strvalue);
										if (value != null && value.length() > 0) {
											fieldvalues.append("'" + value
													+ "'");
										} else {
											fieldvalues.append("'" + strvalue
													+ "'");
										}

									} else {
										fieldvalues
												.append("'" + strvalue + "'");
									}
								}
							}
						} else {
							fieldvalues.append("null");
						}

						fields.append(",");
						fieldvalues.append("`");
					}
					if ("A01".equalsIgnoreCase(key)) {
						if (!isB0110) {
							String b0110 = getB0110(childid);
							if (b0110 != null && b0110.length() > 0) {
								fields.append("b0110,");
								fieldvalues.append("'" + b0110 + "'`");
							}

						}
						obj = getUserClassValue(user, "priority");
						Integer intprio = (Integer) obj;
						String A0000 = intprio.toString();
						if (chitemid != null && chitemid.length() > 0
								&& chitemstrvalue != null
								&& chitemstrvalue.length() > 0) {
							fields.append(chitemid + ",");
							fieldvalues.append("'" + chitemstrvalue + "'`");
							// A0100=chitemstrvalue;
						}
						if (!maininfoInsertA01(tableName, fields.toString(),
								fieldvalues.toString(), A0100, "", A0000,
								this.conn)) {
                            isCorrect = "false";
                        }
					} else {
						if (!detailinfoInsert("1", tableName,
								fields.toString(), fieldvalues.toString(),
								A0100, "", this.conn)) {
                            isCorrect = "false";
                        }
					}
				}

			}
			if ("1".equals(marker)) {
                return chitemstrvalue;
            } else {
                isCorrect = A0100;
            }
		} catch (Exception e) {
			// isCorrect=false;
			isCorrect = "false";
			e.printStackTrace();
		}

		return isCorrect;
	}

	/**
	 * 修改人员信息
	 * 
	 * @param user
	 * @param userid
	 * @return
	 */
	public boolean updateUser(User user, String userid) {
		boolean isCorrect = true;
		String userbase = "Usr";
		String setname = "A01";
		String tableName = userbase + setname;

		String impmode = this.setInterfacesXml.getHrServiceParam("/param",
				"impmode");
		String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
				"keyfield");
		Object obj = getUserClassValue(user, "userId");
		// System.out.println("orgid="+user.getOrgId());
		String chitemstrvalue = (String) obj;
		String marker = setInterfacesXml.getHrServiceParam("/param", "marker");
		if (marker == null || marker.length() <= 0) {
            marker = "0";
        }
		if ("0".equals(marker)) {
            chitemid = "A0100";
        }
		// if(chitemid==null||chitemid.length()<=0)//普天传送的就是A0100
		// String chitemid="A0100";
		try {

			HashMap setidMap = getEmployItem("/param/user/rec");
			Iterator it = setidMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				StringBuffer fields = new StringBuffer();
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				tableName = userbase + key;
				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						if ("priority".equalsIgnoreCase(
                                fielditem.getViewvalue())) {
							continue;
						}
						if (chitemid != null
								&& chitemid.equalsIgnoreCase(fielditem
										.getItemid())) {
							continue;
						}
						fields.append(fielditem.getItemid());
						obj = getUserClassValue(user, fielditem.getViewvalue());
						if (obj != null) {
							String strvalue = "";
							if (fielditem.getViewvalue() != null
									&& "onboardTime"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								Date date = (Date) obj;
								strvalue = DateUtils.format(date,
										"yyyy.MM.dd HH:mm:ss");
							} else if (fielditem.getViewvalue() != null
									&& "birthday"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								Date date = (Date) obj;
								strvalue = DateUtils.format(date,
										"yyyy.MM.dd HH:mm:ss");
							} else if (fielditem.getViewvalue() != null
									&& "sex"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								Integer retval = (Integer) obj;
								strvalue = retval.toString();
								if ("1".equals(strvalue)) {
									strvalue = "女";
								} else if ("2".equals(strvalue)) {
									strvalue = "男";
								}
							} else if (fielditem.getViewvalue() != null
									&& "orgId"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								strvalue = (String) obj;
								// strvalue=getB0110FromCorcode(strvalue);

							} else {
								strvalue = (String) obj;
							}
							if (strvalue == null || strvalue.length() <= 0) {
								fieldvalues.append("null");
							} else {
								if ("0".equals(fielditem.getCodesetid())
										|| "UN"
												.equals(fielditem.getCodesetid())) {
									if ("D".equals(fielditem.getItemtype())) {
										fieldvalues.append(PubFunc
												.DateStringChange(strvalue));
									} else if ("N".equals(fielditem
											.getItemtype())) {
										fieldvalues.append(strvalue);
									} else {
										fieldvalues
												.append("'" + strvalue + "'");
									}
								} else {

									if (impmode != null && "1".equals(impmode)) {
										String value = getCodeItem(fielditem
												.getCodesetid(), strvalue);
										if (value != null && value.length() > 0) {
											fieldvalues.append("'" + value
													+ "'");
										} else {
											fieldvalues.append("'" + strvalue
													+ "'");
										}
									} else {
										fieldvalues
												.append("'" + strvalue + "'");
									}
								}
							}
						} else {
							fieldvalues.append("null");
						}
						fields.append(",");
						fieldvalues.append(",");
					}
					if ("A01".equalsIgnoreCase(key)) {
						obj = getUserClassValue(user, "priority");
						Integer intprio = (Integer) obj;
						String A0000 = intprio.toString();
						if (chitemid != null && chitemid.length() > 0
								&& chitemstrvalue != null
								&& chitemstrvalue.length() > 0) {
							if (!"A0100".equalsIgnoreCase(chitemid)) {
								fields.append(chitemid + ",");
								fieldvalues.append("'" + chitemstrvalue + "',");
							}
							// A0100=chitemstrvalue;
						}
						isCorrect = maininfoUpdateA01(tableName, fields
								.toString(), fieldvalues.toString(), chitemid,
								userid, "", A0000, this.conn);
					} else {
						String A0100 = getA0100("UsrA01", chitemid, userid);
						isCorrect = detailinfoUpdate("1", tableName, fields
								.toString(), fieldvalues.toString(), "A0100",
								A0100, "", this.conn);
					}
				}
			}
		} catch (Exception e) {
			isCorrect = false;
			e.printStackTrace();
		}

		return isCorrect;
	}

	/**
	 * 删除用户
	 * 
	 * @param UseCode
	 * @return
	 */
	public boolean removeUser(String UseCode) {
		boolean isCorrect = true;
		String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
				"keyfield");
		String marker = setInterfacesXml.getHrServiceParam("/param", "marker");
		if (marker == null || marker.length() <= 0) {
            marker = "0";
        }
		if ("0".equals(marker)) {
            chitemid = "A0100";
        }
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		String loginname = "username";
		String loginpass = "userpassword";
		if (login_vo != null) {
			String login_name = login_vo.getString("str_value");
			int idx = login_name.indexOf(",");
			if (idx > -1) {
				String username = login_name.substring(0, idx);
				String password = login_name.substring(idx + 1);
				if (username != null && username.length() > 0
						&& !"#".equalsIgnoreCase(username)) {
                    loginname = username;
                }
				if (password != null && password.length() > 0
						&& !"#".equalsIgnoreCase(password)) {
                    loginpass = password;
                }
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append("update UsrA01 set " + loginname + "=''," + loginpass
				+ "='' where " + chitemid + "='" + UseCode + "'");
		// System.out.println(sql);
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			isCorrect = false;
		}
		return isCorrect;
	}

	/**
	 * 改变用户的组织机构
	 * 
	 * @param newDeptId
	 * @param OldDeptId
	 * @param UseCode
	 * @return
	 */
	public boolean changeUserOrg(String newDeptId, String OldDeptId,
			String UseCode) {
		boolean isCorrect = true;
		try {
			String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
					"keyfield");
			String marker = setInterfacesXml.getHrServiceParam("/param",
					"marker");
			if (marker == null || marker.length() <= 0) {
                marker = "0";
            }
			if ("0".equals(marker)) {
                chitemid = "A0100";
            }
			String itemid = setInterfacesXml.getHrServiceParam(
					"/param/user/rec", "src", "orgId", "dest");
			if (itemid == null || itemid.length() <= 0) {
                return false;
            }
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			// String b0110=getB0110FromCorcode(newDeptId);
			String b0110 = newDeptId;
			String setid = fielditem.getFieldsetid();
			StringBuffer sql = new StringBuffer();
			if ("A01".equalsIgnoreCase(setid)) {
				sql.append("update UsrA01 set " + itemid + "='" + b0110
						+ "' where " + chitemid + "='" + UseCode + "'");
			} else {
				String A0100 = getA0100("UsrA01", chitemid, UseCode);
				sql.append("update Usr" + setid + " set " + itemid + "='"
						+ b0110 + "' where a0100='" + A0100 + "'");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			// System.out.println(sql.toString());
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			isCorrect = false;
		}
		return isCorrect;
	}

	/**
	 * 验证用户信息
	 * 
	 * @param UseCode
	 * @return
	 */
	public boolean validateUserId(String table, String UseCode) {
		String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
				"keyfield");
		String marker = setInterfacesXml.getHrServiceParam("/param", "marker");
		if (marker == null || marker.length() <= 0) {
            marker = "0";
        }
		if ("0".equals(marker)) {
            chitemid = "A0100";
        }
		String a0100 = getA0100(table, chitemid, UseCode);
		if (a0100 != null && a0100.length() > 0) {
            return true;
        } else {
            return false;
        }
	}

	public User[] getAllUsers() {
		User[] users = null;
		HashMap setidMap = getEmployItem("/param/user/rec");
		HashMap orgMap = getCorcodeMap();
		String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
				"keyfield");
		if (chitemid == null || chitemid.length() <= 0) {
            chitemid = "UsrA01.A0100";
        }
		Iterator it = setidMap.entrySet().iterator();
		String userbase = "Usr";
		String tableName = "";
		StringBuffer fields = new StringBuffer();
		try {

			StringBuffer onJoinstr = new StringBuffer();
			StringBuffer sql_str = new StringBuffer();
			StringBuffer sqlA01 = new StringBuffer();
			int j = 0;
			StringBuffer sql = new StringBuffer();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				tableName = userbase + key;
				StringBuffer itemidfields = new StringBuffer();
				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						String fieldname = fielditem.getViewvalue();
						if ("userName".equalsIgnoreCase(fieldname)) {
                            fieldname = "uName";
                        } else if ("userPassword".equalsIgnoreCase(fieldname)) {
                            fieldname = "uPass";
                        }
						fields.append(fieldname + ",");
						itemidfields.append(fielditem.getItemid() + " "
								+ fieldname + ",");
					}
				}
				if ("A01".equals(key)) {
					sqlA01.append(" left join (select "
							+ itemidfields.toString() + "A.a0100 from "
							+ userbase + key + " A");
					sqlA01.append(") " + userbase + key + "A");
					sqlA01.append(" on ");
					sqlA01.append("");
				} else {
					sql_str.append(" left join (select "
							+ itemidfields.toString() + "A.a0100 from "
							+ userbase + key + " A,");
					sql_str.append("(select a0100,max(i9999) as i9999 from "
							+ userbase + key + " group by a0100)B");
					sql_str
							.append(" where A.a0100=B.a0100 and A.i9999=B.i9999) "
									+ userbase + key);
					sql_str.append(" on ");
				}
				if ("A01".equals(key)) {
					sqlA01
							.append("UsrA01.A0100=" + userbase + key
									+ "A.A0100 ");
				} else {
					// select_str.append(oldItemid+".a0100="+itemid+i+".a0100
					// ");
					sql_str
							.append("UsrA01.A0100=" + userbase + key
									+ ".A0100 ");
				}
				j++;
			}
			sql.append("select " + fields + "" + chitemid
					+ " userid,usrA01.a0100,usrA01.a0000 from usrA01 ");
			sql.append(sqlA01.toString());
			sql.append(sql_str.toString());
			RecordVo login_vo = ConstantParamter
					.getConstantVo("SS_LOGIN_USER_PWD");
			String loginname = "username";
			String loginpass = "userpassword";
			if (login_vo != null) {
				String login_name = login_vo.getString("str_value");
				int idx = login_name.indexOf(",");
				if (idx > -1) {
					String username = login_name.substring(0, idx);
					String password = login_name.substring(idx + 1);
					if (username != null && username.length() > 0
							&& !"#".equalsIgnoreCase(username)) {
                        loginname = username;
                    }
					if (password != null && password.length() > 0
							&& !"#".equalsIgnoreCase(password)) {
                        loginpass = password;
                    }
				}
			}
			sql.append("where " + Sql_switcher.isnull(loginname, "'###'")
					+ "<>'###'");
			List rs = ExecuteSQL.executeMyQuery(sql.toString(), conn);
			if (rs == null || rs.size() <= 0) {
                return null;
            }
			users = new User[rs.size()];
			String value = "";
			for (int i = 0; i < rs.size(); i++) {
				User user = new User();
				LazyDynaBean rec = (LazyDynaBean) rs.get(i);
				value = (String) rec.get("userid");
				value = value != null && value.length() > 0 ? value : "";
				user.setUserId(value);
				value = (String) rec.get("uPass");
				value = value != null && value.length() > 0 ? value : "";
				user.setUserPassword(value);
				value = (String) rec.get("orgId");
				// value=(String)orgMap.get(value);
				value = value != null && value.length() > 0 ? value : "";
				value = getCorcodeFromB0110(value);
				user.setOrgId(value);
				value = (String) rec.get("uname");
				value = value != null && value.length() > 0 ? value : "";

				user.setUserName(value);
				value = (String) rec.get("employeenumber");
				value = value != null && value.length() > 0 ? value : "";
				user.setEmployeeNumber(value);
				value = (String) rec.get("onboardtime");
				if (value != null && value.length() > 0) {
					user.setOnboardTime(DateUtils.getDate(value, "yyyy-MM-dd"));
				} else {
					user.setOnboardTime(null);
				}
				value = (String) rec.get("a0000");
				value = value != null && value.length() > 0 ? value : "0";
				user.setPriority(Integer.parseInt(value));
				value = (String) rec.get("title");
				value = value != null && value.length() > 0 ? value : "";
				user.setTitle(value);
				value = (String) rec.get("sex");
				if (value == null || value.length() <= 0) {
                    user.setSex(0);
                } else if ("1".equals(value)) {
                    user.setSex(2);
                } else if ("2".equals(value)) {
                    user.setSex(1);
                }
				value = (String) rec.get("nation");
				value = value != null && value.length() > 0 ? value : "";
				user.setNation(value);
				value = (String) rec.get("nativeplace");
				value = value != null && value.length() > 0 ? value : "";
				user.setNativePlace(value);
				value = (String) rec.get("graduationschool");
				value = value != null && value.length() > 0 ? value : "";
				user.setGraduationSchool(value);
				value = (String) rec.get("degree");
				value = value != null && value.length() > 0 ? value : "";
				user.setDegree(value);
				value = (String) rec.get("birthday");
				if (value != null && value.length() > 0) {
					user.setBirthday(DateUtils.getDate(value, "yyyy-MM-dd"));
				} else {
					user.setBirthday(null);
				}
				value = (String) rec.get("IDCard");
				value = value != null && value.length() > 0 ? value : "";
				user.setIDCard(value);
				value = (String) rec.get("tel");
				value = value != null && value.length() > 0 ? value : "";
				user.setTel(value);
				value = (String) rec.get("mobile");
				value = value != null && value.length() > 0 ? value : "";
				user.setMobile(value);
				value = (String) rec.get("fax");
				value = value != null && value.length() > 0 ? value : "";
				user.setFax(value);
				value = (String) rec.get("mail");
				value = value != null && value.length() > 0 ? value : "";
				user.setMail(value);
				users[i] = user;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return users;
	}

	public User[] getUsersById(String outerId, boolean withInherit,
			boolean isOrg, boolean isDept) {
		User[] users = null;
		HashMap setidMap = getEmployItem("/param/user/rec");
		HashMap orgMap = getCorcodeMap();
		String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
				"keyfield");
		if (chitemid == null || chitemid.length() <= 0) {
            chitemid = "UsrA01.A0100";
        }
		Iterator it = setidMap.entrySet().iterator();
		String userbase = "Usr";
		String tableName = "";
		StringBuffer fields = new StringBuffer();
		try {

			StringBuffer onJoinstr = new StringBuffer();
			StringBuffer sql_str = new StringBuffer();
			StringBuffer sqlA01 = new StringBuffer();
			int j = 0;
			StringBuffer sql = new StringBuffer();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				tableName = userbase + key;
				StringBuffer itemidfields = new StringBuffer();
				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						String fieldname = fielditem.getViewvalue();
						if ("userName".equalsIgnoreCase(fieldname)) {
                            fieldname = "uName";
                        } else if ("userPassword".equalsIgnoreCase(fieldname)) {
                            fieldname = "uPass";
                        }
						fields.append(fieldname + ",");
						itemidfields.append(fielditem.getItemid() + " "
								+ fieldname + ",");
					}
				}
				if ("A01".equals(key)) {
					sqlA01.append(" left join (select "
							+ itemidfields.toString() + "A.a0100 from "
							+ userbase + key + " A");
					sqlA01.append(") " + userbase + key + "A");
					sqlA01.append(" on ");
					sqlA01.append("");
				} else {
					sql_str.append(" left join (select "
							+ itemidfields.toString() + "A.a0100 from "
							+ userbase + key + " A,");
					sql_str.append("(select a0100,max(i9999) as i9999 from "
							+ userbase + key + " group by a0100)B");
					sql_str
							.append(" where A.a0100=B.a0100 and A.i9999=B.i9999) "
									+ userbase + key);
					sql_str.append(" on ");
				}
				if ("A01".equals(key)) {
					sqlA01
							.append("UsrA01.A0100=" + userbase + key
									+ "A.A0100 ");
				} else {
					// select_str.append(oldItemid+".a0100="+itemid+i+".a0100
					// ");
					sql_str
							.append("UsrA01.A0100=" + userbase + key
									+ ".A0100 ");
				}
				j++;
			}
			sql.append("select " + fields + "" + chitemid
					+ " userid,usrA01.a0100,usrA01.a0000 from usrA01 ");
			sql.append(sqlA01.toString());
			sql.append(sql_str.toString());
			RecordVo login_vo = ConstantParamter
					.getConstantVo("SS_LOGIN_USER_PWD");
			String loginname = "username";
			String loginpass = "userpassword";
			if (login_vo != null) {
				String login_name = login_vo.getString("str_value");
				int idx = login_name.indexOf(",");
				if (idx > -1) {
					String username = login_name.substring(0, idx);
					String password = login_name.substring(idx + 1);
					if (username != null && username.length() > 0
							&& !"#".equalsIgnoreCase(username)) {
                        loginname = username;
                    }
					if (password != null && password.length() > 0
							&& !"#".equalsIgnoreCase(password)) {
                        loginpass = password;
                    }
				}
			}
			sql.append("where " + Sql_switcher.isnull(loginname, "'###'")
					+ "<>'###'");
			if (outerId != null && outerId.length() > 0) {
				if (isOrg) {
					if (withInherit) {
                        sql.append(" and usrA01.b0110 like '" + outerId + "%'");
                    } else {
                        sql.append(" and usrA01.b0110='" + outerId + "'");
                    }
				}
				if (isDept) {
					if (withInherit) {
                        sql.append(" and usrA01.e0122 like '" + outerId + "%'");
                    } else {
                        sql.append(" and usrA01.e0122='" + outerId + "'");
                    }
				}
			}
			// System.out.println(sql.toString());
			List rs = ExecuteSQL.executeMyQuery(sql.toString(), conn);
			if (rs == null || rs.size() <= 0) {
                return null;
            }
			users = new User[rs.size()];
			String value = "";
			for (int i = 0; i < rs.size(); i++) {
				User user = new User();
				LazyDynaBean rec = (LazyDynaBean) rs.get(i);
				value = (String) rec.get("userid");
				value = value != null && value.length() > 0 ? value : "";
				user.setUserId(value);
				value = (String) rec.get("uPass");
				value = value != null && value.length() > 0 ? value : "";
				user.setUserPassword(value);
				value = (String) rec.get("orgId");
				// value=(String)orgMap.get(value);
				value = value != null && value.length() > 0 ? value : "";
				value = getCorcodeFromB0110(value);
				user.setOrgId(value);
				value = (String) rec.get("uname");
				value = value != null && value.length() > 0 ? value : "";

				user.setUserName(value);
				value = (String) rec.get("employeenumber");
				value = value != null && value.length() > 0 ? value : "";
				user.setEmployeeNumber(value);
				value = (String) rec.get("onboardtime");
				if (value != null && value.length() > 0) {
					user.setOnboardTime(DateUtils.getDate(value, "yyyy-MM-dd"));
				} else {
					user.setOnboardTime(null);
				}
				value = (String) rec.get("a0000");
				value = value != null && value.length() > 0 ? value : "0";
				user.setPriority(Integer.parseInt(value));
				value = (String) rec.get("title");
				value = value != null && value.length() > 0 ? value : "";
				user.setTitle(value);
				value = (String) rec.get("sex");
				if (value == null || value.length() <= 0) {
                    user.setSex(0);
                } else if ("1".equals(value)) {
                    user.setSex(2);
                } else if ("2".equals(value)) {
                    user.setSex(1);
                }
				value = (String) rec.get("nation");
				value = value != null && value.length() > 0 ? value : "";
				user.setNation(value);
				value = (String) rec.get("nativeplace");
				value = value != null && value.length() > 0 ? value : "";
				user.setNativePlace(value);
				value = (String) rec.get("graduationschool");
				value = value != null && value.length() > 0 ? value : "";
				user.setGraduationSchool(value);
				value = (String) rec.get("degree");
				value = value != null && value.length() > 0 ? value : "";
				user.setDegree(value);
				value = (String) rec.get("birthday");
				if (value != null && value.length() > 0) {
					user.setBirthday(DateUtils.getDate(value, "yyyy-MM-dd"));
				} else {
					user.setBirthday(null);
				}
				value = (String) rec.get("IDCard");
				value = value != null && value.length() > 0 ? value : "";
				user.setIDCard(value);
				value = (String) rec.get("tel");
				value = value != null && value.length() > 0 ? value : "";
				user.setTel(value);
				value = (String) rec.get("mobile");
				value = value != null && value.length() > 0 ? value : "";
				user.setMobile(value);
				value = (String) rec.get("fax");
				value = value != null && value.length() > 0 ? value : "";
				user.setFax(value);
				value = (String) rec.get("mail");
				value = value != null && value.length() > 0 ? value : "";
				user.setMail(value);
				users[i] = user;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return users;
	}

	/**
	 * 反射机制得到相应get方法值
	 * 
	 * @param user
	 * @param beanname
	 * @return
	 */
	private Object getUserClassValue(User user, String beanname) {
		java.lang.Object obj = null;
		String methodName = "get" + beanname.substring(0, 1).toUpperCase();

		if (beanname.length() > 1) {
            methodName = methodName + beanname.substring(1);
        }
		try {
			Class formclass = user.getClass();
			// cat.debug("------------------>methodName="+methodName);
			Method myMethod1 = formclass.getMethod(methodName);
			obj = myMethod1.invoke(user);
		} catch (Exception ept) {
			ept.printStackTrace();
			return null;
		}
		return obj;
	}

	/**
	 * 得到人员编号a0100
	 * 
	 * @param tableName
	 * @return
	 * @throws GeneralException
	 */
	public synchronized String getUserId(String tableName)
			throws GeneralException {

		return DbNameBo.insertMainSetA0100(tableName, this.conn);
	}

	private String getCodeItem(String codesetid, String codedesc) {
		String sql = "select codeitemid FROM codeitem where codesetid='"
				+ codesetid + "' and codeitemdesc='" + codedesc + "'";
		String codeitemid = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				codeitemid = rs.getString("codeitemid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeitemid;
	}

	/**
	 * 增加子集
	 * 
	 * @param tableName
	 * @param fields
	 * @param fieldValues
	 * @param userid
	 * @param createUserName
	 * @param conn
	 * @return
	 */
	private boolean detailinfoInsert(String insertType, String tableName,
			String fields, String fieldValues, String userid,
			String createUserName, Connection conn) {
		String id = "";
		boolean isCorrect = true;
		String personname = createUserName;// this.getUsername(tableName,userid,conn);
		// chenmengqing added
		StringBuffer strsql = new StringBuffer();
		if (fieldValues != null && fieldValues.length() > 0) {
            fieldValues = fieldValues.replaceAll("`", ",");
        }
		try {
			strsql.append("insert into ");
			strsql.append(tableName);
			strsql.append("(");
			strsql.append(fields);
			if ("1".equals(insertType)) {
                strsql
                        .append("State,CreateTime,ModTime,CreateUserName,ModUserName,A0100,I9999) values(");
            } else if ("2".equals(insertType)) {
                strsql
                        .append("State,CreateTime,ModTime,CreateUserName,ModUserName,B0110,I9999) values(");
            }
			strsql.append(fieldValues);
			if (createUserName.equalsIgnoreCase(personname)) // ?什么意思
            {
                strsql.append("'0',");
            } else {
				strsql.append("'3',");
			}

			strsql.append("");
			strsql.append(PubFunc.DoFormatSystemDate(false));
			strsql.append(",");
			strsql.append(PubFunc.DoFormatSystemDate(false));

			strsql.append(",'");
			strsql.append(createUserName);
			strsql.append("',");
			strsql.append("null");
			strsql.append(",'");
			strsql.append(userid);
			strsql.append("',");
			if ("1".equals(insertType)) {
				id = getUserI9999(tableName, userid, "A0100", conn);
				strsql.append(id);
			} else if ("2".equals(insertType)) {
				id = getUserI9999(tableName, userid, "B0110", conn);
				strsql.append(id);
			}
			strsql.append(")");
			new ExecuteSQL().execUpdate(strsql.toString(), conn);
		} catch (Exception e) {
			isCorrect = false;
			// System.out.println(e.getMessage());
		}
		return isCorrect;
	}

	/**
	 * 修改子集
	 * 
	 * @param tableName
	 * @param fields
	 * @param fieldValues
	 * @param userid
	 * @param createUserName
	 * @param conn
	 * @return
	 */
	private boolean detailinfoUpdate(String updateType, String tableName,
			String fields, String fieldValues, String chitemid, String userid,
			String username, Connection conn) {
		boolean flag = false;
		String personname = username;// this.getUsername(tableName,userid,conn);
		// chenmengqing added
		String[] tempstrs = fields.toString().split(",");
		String[] tempValuestrs = fieldValues.toString().split(",");
		StringBuffer strsql = new StringBuffer();
		strsql.append("update ");
		strsql.append(tableName);
		strsql.append(" set ");
		for (int i = 0; i < tempstrs.length; i++) {
			if (tempstrs[i] != null) {
				strsql.append(tempstrs[i] + "=" + tempValuestrs[i] + ",");
			}
		}
		if (username.equals(personname)) {
            strsql.append("state='0',");
        } else {
            strsql.append("state='3',");
        }
		strsql.append("ModTime=");
		strsql.append(PubFunc.DoFormatSystemDate(false));
		strsql.append(",ModUserName='");
		strsql.append(username);
		strsql.append("'");

		if ("1".equals(updateType)) // 人员
		{
			strsql.append(" where " + chitemid + "='");
			strsql.append(userid);
			strsql.append("' and I9999=");
			String id = getUserI9999(tableName, userid, "A0100", conn);
			int i9999 = Integer.parseInt(id) - 1;
			strsql.append(i9999);
		} else if ("2".equals(updateType)) // 单位
		{
			strsql.append(" where " + chitemid + "='");
			strsql.append(userid);
			strsql.append("' and I9999=");
			String id = getUserI9999(tableName, userid, "B0110", conn);
			int i9999 = Integer.parseInt(id) - 1;
			strsql.append(i9999);
		}
		try {
			// System.out.println(strsql.toString());
			new ExecuteSQL().execUpdate(strsql.toString(), conn);
			flag = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return flag;
	}

	// 获得I9999的最大顺序号
	public synchronized String getUserI9999(String strTableName, String userid,
			String fieldtype, Connection conn) {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select max(I9999) as I9999 from ");
		strsql.append(strTableName);
		strsql.append(" where ");
		strsql.append(fieldtype);
		strsql.append("='");
		strsql.append(userid);
		strsql.append("'");
		int id = 1;
		try {
			List rs = ExecuteSQL.executeMyQuery(strsql.toString(), conn);
			if (rs != null && rs.size() > 0) {
				LazyDynaBean rec = (LazyDynaBean) rs.get(0);
				id = Integer.parseInt(String.valueOf(rec.get("i9999") != null
						&& rec.get("i9999").toString().length() > 0 ? rec
						.get("i9999") : "0")) + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// new ExecuteSQL().freeConn();
		}
		return String.valueOf(id);
	}

	/**
	 * 增加人员主集
	 * 
	 * @param tableName
	 * @param fields
	 * @param fieldValues
	 * @param userid
	 * @param createUserName
	 * @param a0000
	 * @param conn
	 */
	private boolean maininfoInsertA01(String tableName, String fields,
			String fieldValues, String userid, String createUserName,
			String a0000, Connection conn) {
		boolean isCorrect = true;
		String[] tempstrs = fields.toString().split(",");
		String[] tempValuestrs = fieldValues.toString().split("`");
		StringBuffer strsql = new StringBuffer();
		strsql.append("update ");
		strsql.append(tableName + " set ");
		if (tempstrs != null && tempstrs.length > 0) {
			for (int i = 0; i < tempstrs.length; i++) {
				if (tempstrs[i] != null && tempstrs[i].length() > 0) {
                    strsql.append(tempstrs[i] + "=" + tempValuestrs[i] + ",");
                }
			}
		}
		strsql.append("CreateTime=" + PubFunc.DoFormatSystemDate(false) + ",");
		strsql.append("ModTime=" + PubFunc.DoFormatSystemDate(false) + ",");
		strsql.append("CreateUserName='" + createUserName + "',");
		/*
		 * strsql.append("UserName='"+userName+"',");
		 * strsql.append("UserPassword='"+password+"',");
		 */
		// if(a0000==null||a0000.length()<=0)
		strsql.append("A0000='" + getUserA0000(tableName, conn) + "'");
		// else
		// strsql.append("A0000='"+a0000+"'");
		strsql.append(" where a0100='" + userid + "'");
		try {
			ContentDAO dao = new ContentDAO(conn);
			// System.out.println(strsql.toString());
			dao.update(strsql.toString());
		} catch (Exception e) {
			isCorrect = false;
			e.printStackTrace();
		}
		return isCorrect;
	}

	private boolean maininfoUpdateA01(String tableName, String fields,
			String fieldValues, String chitemid, String userid,
			String createUserName, String a0000, Connection conn) {
		boolean isCorrect = false;
		String[] tempstrs = fields.toString().split(",");
		String[] tempValuestrs = fieldValues.toString().split(",");
		StringBuffer strsql = new StringBuffer();
		strsql.append("update ");
		strsql.append(tableName + " set ");
		if (tempstrs != null && tempstrs.length > 0) {
			for (int i = 0; i < tempstrs.length; i++) {
				if (tempstrs[i] != null && tempstrs[i].length() > 0) {
                    strsql.append(tempstrs[i] + "=" + tempValuestrs[i] + ",");
                }
			}
		}
		strsql.append("ModTime=" + PubFunc.DoFormatSystemDate(false) + ",");
		strsql.append("ModUserName='" + createUserName + "'");
		/*
		 * if(a0000!=null&&a0000.length()>0)
		 * strsql.append(",A0000='"+a0000+"'");
		 */
		strsql.append(" where " + chitemid + "='" + userid + "'");
		try {
			// System.out.println(strsql);
			ContentDAO dao = new ContentDAO(conn);
			dao.update(strsql.toString());
			isCorrect = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isCorrect;
	}

	// 获得主集的A0000的最大排序号
	public synchronized String getUserA0000(String strTableName, Connection conn) {
		String strsql = "select max(A0000) as A0000 from " + strTableName;
		int userId = 1;
		try {
			List rs = ExecuteSQL.executeMyQuery(strsql.toString(), conn);
			if (rs != null && rs.size() > 0) {
				LazyDynaBean rec = (LazyDynaBean) rs.get(0);
				userId = Integer
						.parseInt(String
								.valueOf(rec.get("a0000") != null
										&& rec.get("a0000").toString().length() > 0 ? rec
										.get("a0000")
										: "0")) + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// new ExecuteSQL().freeConn();
		}
		return String.valueOf(userId);
	}

	public String getA0100(String table, String chitemid, String userid) {
		String strsql = "select a0100 from " + table + " where " + chitemid
				+ "='" + userid + "'";
		String a0100 = "";
		try {
			List rs = ExecuteSQL.executeMyQuery(strsql.toString(), conn);
			if (rs != null && rs.size() > 0) {
				LazyDynaBean rec = (LazyDynaBean) rs.get(0);
				a0100 = (String) rec.get("a0100");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// new ExecuteSQL().freeConn();
		}
		return a0100;
	}

	private HashMap getCorcodeMap() {
		String strsql = "select codeitemid,corcode from organization  where codesetid<>'@K'";
		HashMap map = new HashMap();
		try {
			// System.out.println(strsql);
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(strsql);
			while (rs.next()) {
				map.put(rs.getString("codeitemid"), rs.getString("corcode"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 通过corcode得到b0110
	 * 
	 * @param corcode
	 * @return
	 */
	private String getB0110FromCorcode(String corcode) {
		String strsql = "select codeitemid,corcode from organization  where corcode='"
				+ corcode + "'";
		String b0110 = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(strsql);
			while (rs.next()) {
				b0110 = rs.getString("codeitemid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b0110;
	}

	/**
	 * 通过b0110得到corcode
	 * 
	 * @param b0110
	 * @return
	 */
	private String getCorcodeFromB0110(String b0110) {
		String strsql = "select codesetid,corcode from organization  where codeitemid='"
				+ b0110 + "'";
		String corcode = "";
		try {
			// System.out.println(strsql);
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(strsql);
			while (rs.next()) {
				b0110 = rs.getString("corcode");
				if (b0110 == null || "".equals(b0110)) {
					b0110 = rs.getString("codeitemid");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corcode;
	}

	private String getB0110(String code) {
		String b0100Code = getB0100Code(code);
		if (b0100Code == null || b0100Code.length() <= 0) {
            return "";
        }
		int s = 100;
		int i = 0;
		while (b0100Code.indexOf("UN") == -1) {
			code = b0100Code.substring(2);
			b0100Code = getB0100Code(code);
			i++;
			if (i > s) {
                break;
            }
		}
		if (b0100Code != null && b0100Code.length() > 2) {
            code = b0100Code.substring(2);
        }
		return code;
	}

	private String getB0100Code(String code) {
		String strsql = "select codesetid,parentid from organization  where codeitemid='"
				+ code + "'";
		String recode = "";
		try {
			// System.out.println(strsql);
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(strsql);
			if (rs.next()) {
				String parentid = rs.getString("parentid");
				String codesetid = rs.getString("codesetid");
				if ("UN".equalsIgnoreCase(codesetid)) {
                    recode = "UN" + code;
                } else {
                    recode = codesetid + parentid;
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recode;
	}

	/**
	 * 人员批量添加
	 * 
	 * @param setid
	 * @param stafftype
	 * @param keyfield
	 * @param columns
	 * @param list
	 * @return
	 */
	public int batchAppendToUser(String createUserName, String setid,
			String stafftype, String keyfield, List columns, List list) {
		if ("A01".equals(setid)) {
			return batchEditorToUser(createUserName, setid, stafftype,
					keyfield, columns, list);
		} else {
			List dataList = getColumnsByXml(list);
			List fieldList = getFieldList(columns);
			ArrayList sqlsList = new ArrayList();
			try {
//				this.conn.setAutoCommit(false);
				ContentDAO dao = new ContentDAO(this.conn);
				String table = stafftype + setid;
				Iterator dataIt = dataList.iterator();
				while (dataIt.hasNext()) {
					StringBuffer sql = new StringBuffer();
					StringBuffer colName = new StringBuffer();
					StringBuffer value = new StringBuffer();
					Map map = (Map) dataIt.next();
					Iterator it = fieldList.iterator();
					String keyValue = (String) map.get(keyfield.toLowerCase());
					if (keyValue == null || keyValue.length() < 1) {
						return -1;
					}
					String A0100 = getA0100(stafftype + "A01", keyfield,
							keyValue);
					if (A0100 == null || A0100.length() < 1) {
						return -1;
					}
					int maxI9999 = this.getMaxI9999(table, A0100);
					colName.append("I9999,");
					value.append( ++maxI9999 + ",");
					colName.append("CreateTime,");
					value.append(PubFunc.DoFormatSystemDate(false) + ",");
					colName.append("ModTime,");
					value.append(PubFunc.DoFormatSystemDate(false) + ",");
					colName.append("CreateUserName,");
					value.append("'"+createUserName+"',");

					while (it.hasNext()) {
						FieldItem field = (FieldItem) it.next();
						String fieldName = field.getItemid();
						String fieldValue = (String) map.get(fieldName);
						if (fieldValue == null) {
							return -1;
						}
						colName.append(fieldName + ",");
						if ("D".equals(field.getItemtype())) {
							value
									.append(" "
											+ Sql_switcher
													.dateValue(fieldValue)
											+ ",");
						} else if ("N".equals(field.getItemtype())) {
							value.append(fieldValue + ",");
						} else {
							value.append("'" + fieldValue + "',");
						}
					}

					colName.append("A0100");
					value.append("'" + A0100 + "'");
					sql.append("INSERT INTO " + table + "(" + colName + ")");
					sql.append("VALUES (" + value + ")");
					//日志
					Category log = Category.getInstance(this.getClass());
					log.error("colName："+colName);
					log.error("value："+value);
					log.error("sql"+sql);
					
					sqlsList.add(sql.toString());
				}
				dao.batchUpdate(sqlsList);
//				this.conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				return -1;
			}
			return dataList.size();
		}
	}

	public int batchEditorToUser(String modUserName, String setid,
			String stafftype, String keyfield, List columns, List list) {
		String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
				"keyfield");
		if (!keyfield.equalsIgnoreCase(chitemid)) {
			return -1;
		}
		List dataList = getColumnsByXml(list);
		List fieldList = getFieldList(columns);
		ArrayList sqlsList = new ArrayList();
		try {
//			this.conn.setAutoCommit(false);
			ContentDAO dao = new ContentDAO(this.conn);
			String table = stafftype + setid;
			Iterator dataIt = dataList.iterator();
			String A0100 = getUserId(table);
			int add = 1;
			while (dataIt.hasNext()) {
				StringBuffer sql = new StringBuffer();
				Map map = (Map) dataIt.next();
				String keyValue = (String) map.get(keyfield.toLowerCase());
				if (keyValue == null || keyValue.length() < 1) {
					return -1;
				}
				if (this.validateUserId(table, keyValue)) {
					StringBuffer cols = new StringBuffer();
					StringBuffer where = new StringBuffer();
					Iterator it = fieldList.iterator();
					cols.append("ModTime=" + PubFunc.DoFormatSystemDate(false)
							+ ",");
					cols.append("modUserName='" + modUserName + "',");
					while (it.hasNext()) {
						FieldItem field = (FieldItem) it.next();
						String fieldName = field.getItemid();
						String fieldValue = (String) map.get(fieldName);
						if (fieldValue == null) {
							return -1;
						}
						if ("D".equals(field.getItemtype())) {
							cols.append(fieldName + "="
									+ Sql_switcher.dateValue(fieldValue) + ",");
						} else if ("N".equals(field.getItemtype())) {
							cols.append(fieldName + "=" + fieldValue + ",");
						} else {
							cols.append(fieldName + "='" + fieldValue + "',");
						}
					}
					cols.deleteCharAt(cols.length()-1);
					where.append("WHERE " + keyfield + "='" + keyValue + "'");
					sql.append("UPDATE " + table + " SET " + cols);
					sql.append(" " + where);
				} else {
					StringBuffer colName = new StringBuffer();
					StringBuffer value = new StringBuffer();
					Iterator it = fieldList.iterator();
					A0100 = addA0100(A0100, add++);
					colName.append("CreateTime,");
					value.append(PubFunc.DoFormatSystemDate(false) + ",");
					colName.append("ModTime,");
					value.append(PubFunc.DoFormatSystemDate(false) + ",");
					colName.append("CreateUserName,");
					value.append("'" + modUserName + "',");
					colName.append("A0000");
					value.append("'" + getUserA0000(table, this.conn) + "'");

					while (it.hasNext()) {
						FieldItem field = (FieldItem) it.next();
						String fieldName = field.getItemid();
						String fieldValue = (String) map.get(fieldName);
						if (fieldValue == null) {
							return -1;
						}
						colName.append(","+fieldName + ",");
						value.append(",");
						if ("D".equals(field.getItemtype())) {
							value
									.append(" "
											+ Sql_switcher
													.dateValue(fieldValue)
											+ " ,");
						} else if ("N".equals(field.getItemtype())) {
							value.append(fieldValue + ",");
						} else {
							value.append("'" + fieldValue + "',");
						}
					}
					colName.append("A0100");
					value.append("'" + A0100 + "'");
					sql.append("INSERT INTO " + table + "(" + colName + ")");
					sql.append("VALUES (" + value + ")");
				}
				sqlsList.add(sql.toString());
			}
			dao.batchUpdate(sqlsList);
//			this.conn.commit();
		} catch (SQLException e) {
			
			e.printStackTrace();
			return -1;
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally {
			//dataList.clear();
		}
		return dataList.size();
	}

	/**
	 * 人员批量更新
	 * 
	 * @param setid
	 * @param stafftype
	 * @param keyfield
	 * @param columns
	 * @param list
	 * @return
	 */
	public int batchUpdateToUser(String modUserName, String setid,
			String stafftype, String keyfield, List columns, List list) {
		if ("A01".equals(setid)) {
			return batchEditorToUser(modUserName, setid, stafftype, keyfield,
					columns, list);
		} else {
			String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
					"keyfield");
			if (!keyfield.equalsIgnoreCase(chitemid)) {
				return -1;
			}
			List dataList = getColumnsByXml(list);
			List fieldList = getFieldList(columns);
			ArrayList sqlList = new ArrayList();
			try {
//				this.conn.setAutoCommit(false);
				ContentDAO dao = new ContentDAO(conn);
				String table = stafftype + setid;
				Iterator dataIt = dataList.iterator();
				while (dataIt.hasNext()) {
					StringBuffer sql = new StringBuffer();
					StringBuffer cols = new StringBuffer();
					StringBuffer where = new StringBuffer();
					Map map = (Map) dataIt.next();
					Iterator it = fieldList.iterator();
					cols.append("ModTime=" + PubFunc.DoFormatSystemDate(false)
							+ ",");
					cols.append("modUserName='" + modUserName + "',");
					while (it.hasNext()) {
						FieldItem field = (FieldItem) it.next();
						String fieldName = field.getItemid();
						String fieldValue = (String) map.get(fieldName);
						if (fieldValue == null || fieldValue.length() < 1) {
							return -1;
						}
						if ("D".equals(field.getItemtype())) {
							cols.append(fieldName + "="
									+ Sql_switcher.dateValue(fieldValue) + ",");
						} else if ("N".equals(field.getItemtype())) {
							cols.append(fieldName + "=" + fieldValue + ",");
						} else {
							cols.append(fieldName + "='" + fieldValue + "',");
						}
					}
					cols.deleteCharAt(cols.length());
					String keyValue = (String) map.get(keyfield.toLowerCase());
					if (keyValue == null || keyValue.length() < 1) {
						return -1;
					}
					String A0100 = getA0100(stafftype + "A01", keyfield,
							keyValue);
					if (A0100 == null || A0100.length() < 1) {
						return -1;
					}
					where.append("WHERE A0100='" + A0100 + "'");
					int maxI9999 = this.getMaxI9999(table, A0100);
					if (maxI9999 != 0) {
						where.append(" AND I9999 = '" + maxI9999 + "'");
					}
					sql.append("UPDATE " + table + " SET " + cols);
					sql.append(" " + where);
					sqlList.add(sql.toString());
				}
				dao.batchUpdate(sqlList);
//				this.conn.commit();
			} catch (SQLException e) {
				
				e.printStackTrace();
				return -1;
			} finally {
				dataList.clear();
			}
			return dataList.size();
		}
	}

	/**
	 * 人员批量删除
	 * 
	 * @param setid
	 * @param stafftype
	 * @param keyfield
	 * @param columns
	 * @param list
	 * @return
	 */
	public int batchDeleteToUser(String modUserName, String setid,
			String stafftype, String keyfield, List columns, List list) {
		String chitemid = setInterfacesXml.getHrServiceParam("/param/user",
				"keyfield");
		if (!keyfield.equalsIgnoreCase(chitemid)) {
			return -1;
		}
		List dataList = getColumnsByXml(list);
		ArrayList sqlList = new ArrayList();
		try {
			
			ContentDAO dao = new ContentDAO(conn);
			String table = stafftype + setid;
			Iterator dataIt = dataList.iterator();
			while (dataIt.hasNext()) {
				StringBuffer sql = new StringBuffer();
				StringBuffer where = new StringBuffer();
				Map map = (Map) dataIt.next();
				String keyValue = (String) map.get(keyfield.toLowerCase());
				if (keyValue == null || keyValue.length() < 1) {
					return -1;
				}
				String A0100 = getA0100(stafftype + "A01", keyfield, keyValue);
				if (A0100 == null || A0100.length() < 1) {
					dataList.remove(map);
					continue;
				}
				where.append("WHERE A0100='" + A0100 + "'");
				if (!"A01".equalsIgnoreCase(setid)) {
					int maxI9999 = this.getMaxI9999(table, A0100);
					if (maxI9999 != 0) {
						where.append(" AND I9999 = " + maxI9999);
					}
					sql.append("DELETE FROM " + table);
				} else {
					RecordVo login_vo = ConstantParamter
							.getConstantVo("SS_LOGIN_USER_PWD");
					String loginname = "username";
					String loginpass = "userpassword";
					if (login_vo != null) {
						String login_name = login_vo.getString("str_value");
						int idx = login_name.indexOf(",");
						if (idx > -1) {
							String username = login_name.substring(0, idx);
							String password = login_name.substring(idx + 1);
							if (username != null && username.length() > 0
									&& !"#".equalsIgnoreCase(username)) {
                                loginname = username;
                            }
							if (password != null && password.length() > 0
									&& !"#".equalsIgnoreCase(password)) {
                                loginpass = password;
                            }
						}
					}
					sql.append("update " + table + " set " + loginname + "='',"
							+ loginpass + "='',ModTime="
							+ PubFunc.DoFormatSystemDate(false)
							+ ",modUserName='" + modUserName + "',");
				}
				sql.append(" " + where);
				sqlList.add(sql.toString());
			}
			dao.batchUpdate(sqlList);
//			this.conn.commit();
		} catch (SQLException e) {
			
			e.printStackTrace();
			return -1;
		} finally {
			dataList.clear();
		}
		return dataList.size();
	}

	/**
	 * 批量操作XML数据集
	 * 
	 * @param list（Element）
	 * @return List（Map（字段名,值）一条记录）
	 */
	private List getColumnsByXml(List list) {
		List dataList = new ArrayList();
		Iterator it = list.iterator();
		Map columns = new HashMap();
		/*while (it.hasNext()) {
			Map columns = new HashMap();
			Element element = (Element) it.next();
			List column = element.elements();
			Iterator cit = column.iterator();
			while (cit.hasNext()) {
				Element field = (Element) cit.next();
				columns.put(field.getName().toLowerCase(), field.getTextTrim());
			}
			dataList.add(columns);
		}*/
		while (it.hasNext()) {
			DefaultElement de =  (DefaultElement) it.next();
			DefaultText dt= (DefaultText) de.content().get(0);
			QName qname = de.getQName();
			String name =qname.getName();
			String text = dt.getText();
			columns.put(name.toLowerCase(),text.trim());
			/*Element element = (Element) it.next();
			List column = element.elements();
			Iterator cit = column.iterator();
			while (cit.hasNext()) {
				Element field = (Element) cit.next();
				columns.put(field.getName().toLowerCase(), field.getTextTrim());
			}*/
		}
		dataList.add(columns);
		return dataList;
	}

	/**
	 * 获取要操作的字段信息 如：字段类型 等
	 * 
	 * @param list（字段名）
	 * @return List（FieldItem）
	 */
	private List getFieldList(List list) {
		List fieldList = new ArrayList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			String fieldName = (String) it.next();
			FieldItem field = DataDictionary.getFieldItem(fieldName);
			fieldList.add(field);
		}
		return fieldList;
	}

	public String addA0100(String a0100, int add) {
		int n_a0100 = Integer.parseInt(a0100) + add;
		a0100 = String.valueOf(n_a0100);
		int a0100Lang = a0100.length();
		for (int i = 0; i < 8 - a0100Lang; i++) {
			a0100 = "0" + a0100;
		}
		return a0100;
	}

	private int getMaxI9999(String table, String a0100) {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAX(I9999)  FROM ");
		sql.append(table);
		sql.append(" WHERE A0100='" + a0100 + "'");
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				//return rs.getInt("MAX(I9999)");
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
}
