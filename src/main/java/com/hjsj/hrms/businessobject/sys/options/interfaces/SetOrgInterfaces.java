package com.hjsj.hrms.businessobject.sys.options.interfaces;

import com.hjsj.hrms.service.core.Organization;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.dom4j.Element;

import javax.sql.RowSet;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SetOrgInterfaces {
	private Connection conn;
	private String xmlContent = "";
	private SetInterfacesXml setInterfacesXml = null;
	private RowSet frowset;

	public SetOrgInterfaces(Connection conn) {
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
			if (itemid == null || itemid.length() <= 0) // 为空的也有
            {
                continue;
            }
			String codesetid = (String) obean.get("codesetid");
			FieldItem fielditem = DataDictionary.getFieldItem(itemid
					.toUpperCase());
			fielditem.setViewvalue(src);
			String fieldsetid = fielditem.getFieldsetid();
			if ("A01".equalsIgnoreCase(fieldsetid)) {
				fieldsetid = "B01";
			}
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

	// public void ttd()
	// {
	// Organization org = new Organization();
	// getAllOrganizations();
	// // createOrganization(org);
	// }
	/**
	 * 创建组织机构
	 * 
	 * @param org
	 * @param impmode
	 * @return
	 */
	public String createOrganization(Organization org) {
		String isCorrect = "false";
		String orgtableName = "Organization";
		String unitTableName = "B01";
		String impmode = this.setInterfacesXml.getHrServiceParam("/param",
				"impmode");
		try {
			Object obj = getOrgClassValue(org, "parent"); // 直属上级机构Id
			String parent = (String) obj;
			Object objorgId = getOrgClassValue(org, "orgId");// 组织机构Id
			String orgId = (String) objorgId;
			Object objname = getOrgClassValue(org, "name");// 机构全称
			String name = (String) objname;
			Object objstyle = getOrgClassValue(org, "style");// UN 单位；UM 部门
			String style = (String) objstyle;
			// System.out.println(parent+"---"+orgId);
			String codeitemid = getorgcodeitemid(parent, orgId, name, style,
					orgtableName, this.conn); // 得到机构id，写入org表
			// System.out.println(codeitemid);
			String B0110 = getb0110Id(unitTableName, codeitemid, this.conn); // 同时把codeitemid
			// 写入到b01表中
			HashMap setidMap = getEmployItem("/param/org/rec");
			Iterator it = setidMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				StringBuffer fields = new StringBuffer();
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				if ("A01".equalsIgnoreCase(key)) {
					unitTableName = "B01";
					key = "B01";
				} else {
					unitTableName = key;
				}

				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						if ("priority".equalsIgnoreCase(
                                fielditem.getViewvalue())) {
							continue;
						}
						fields.append(fielditem.getItemid());
						obj = getOrgClassValue(org, fielditem.getViewvalue()); // 得到get，set值
						if (obj != null) {
							String strvalue = "";
							if (fielditem.getViewvalue() != null
									&& "orgId"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								strvalue = (String) obj;
								strvalue = getB0110FromCorcode(strvalue); // 得到组织机构id
							} else {
								strvalue = (String) obj;
							}
							if (strvalue == null || strvalue.length() <= 0) {
								fieldvalues.append("null");
							} else {
								if ("0".equals(fielditem.getCodesetid())
										|| "UN"
												.equals(fielditem.getCodesetid())
										|| "UM"
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
										fieldvalues.append("'"
												+ getCodeItem(fielditem
														.getCodesetid(),
														strvalue) + "'");
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
					if ("B01".equalsIgnoreCase(key)) {
						obj = getOrgClassValue(org, "priority");
						Integer intprio = (Integer) obj;
						String b0110S = intprio.toString();
						maininfoInsertB01(unitTableName, fields.toString(),
								fieldvalues.toString(), B0110, "", b0110S,
								this.conn);
					} else {
						OrgdetailinfoInsert("2", unitTableName, fields
								.toString(), fieldvalues.toString(), B0110, "",
								this.conn);
					}

				}
			}
			isCorrect = codeitemid;

		} catch (Exception e) {
			isCorrect = "false";
			// Category.getInstance("com.hrms.frame.dao.ContentDAO").error("Exception="+e.toString());
			e.printStackTrace();
		}
		return isCorrect;
	}

	/**
	 * 删除组织机构 根据outerOrgId=corCode条件，
	 * 
	 * @param outerOrgId
	 * @return
	 */
	public boolean removeOrganization(String outerOrgId) {
		boolean isCorrect = true;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			StringBuffer delsql = new StringBuffer();
			String codeitemid = null;
			// 普天：删除传入的 outerOrgId 也= HR平台 codeitemid
			// buf.append("select
			// codesetid,codeitemdesc,parentid,childid,codeitemid,grade from
			// organization where corcode= ");
			buf
					.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from organization where codeitemid= ");
			buf.append("'" + outerOrgId + "'");
			RowSet rowset = dao.search(buf.toString());
			if (rowset.next()) {
				codeitemid = rowset.getString("codeitemid");
			} else {
				isCorrect = false;
			}
			if (isCorrect) {
				delsql
						.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from organization where codeitemid like '");
				delsql.append(codeitemid);
				delsql.append("%'");
				this.frowset = dao.search(delsql.toString());
				while (this.frowset.next()) {
					CodeItem item = new CodeItem();
					item.setCodeid(this.frowset.getString("codesetid"));
					item.setCodename(this.frowset.getString("codeitemdesc"));
					item.setPcodeitem(this.frowset.getString("parentid"));
					item.setCcodeitem(this.frowset.getString("childid"));
					item.setCodeitem(this.frowset.getString("codeitemid"));
					item.setCodelevel(String.valueOf(this.frowset
							.getInt("grade")));
					AdminCode.removeCodeItem(item);
				}
				delsql.delete(0, delsql.length());
				delsql
						.append("delete from organization where codeitemid like '");
				delsql.append(codeitemid);
				delsql.append("%'");
				// cat.debug(delsql.toString());
				dao.delete(delsql.toString(), new ArrayList());
				checkorg();
				// BXX 单位信息删除
				List infoSetList = DataDictionary.getFieldSetList(
						Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
				for (int k = 0; k < infoSetList.size(); k++) {
					FieldSet fieldset = (FieldSet) infoSetList.get(k);
					delsql.delete(0, delsql.length());
					delsql.append("delete from ");
					delsql.append(fieldset.getFieldsetid());
					delsql.append(" where b0110 like '");
					delsql.append(codeitemid);
					delsql.append("%'");
					dao.delete(delsql.toString(), new ArrayList());
				}
				// KXX 部门信息 删除
				List infoSetListPos = DataDictionary.getFieldSetList(
						Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);
				for (int k = 0; k < infoSetListPos.size(); k++) {
					FieldSet fieldset = (FieldSet) infoSetListPos.get(k);
					delsql.delete(0, delsql.length());
					delsql.append("delete from ");
					delsql.append(fieldset.getFieldsetid());
					delsql.append(" where e01a1 like '");
					delsql.append(codeitemid);
					delsql.append("%'");
					dao.delete(delsql.toString(), new ArrayList());
				}

				// 用到了 DeleteOrgTrans 仿照

			}
		} catch (Exception e) {
			e.printStackTrace();
			isCorrect = false;
		}

		return isCorrect;
	}

	/**
	 * 更新组织机构 根据outerOrgId值=corcode值查询条件
	 * 
	 * @param org
	 * @param outerOrgId
	 * @return
	 */
	public boolean updateOrganization(Organization org, String outerOrgId) {
		boolean isCorrect = true;
		String orgtableName = "Organization";
		String unitTableName = "B01";
		String impmode = this.setInterfacesXml.getHrServiceParam("/param",
				"impmode");
		try {
			Object objname = getOrgClassValue(org, "name");// 机构全称
			String name = (String) objname;
			String b0110 = uporgname(orgtableName, outerOrgId, name, this.conn);
			HashMap setidMap = getEmployItem("/param/org/rec");
			Iterator it = setidMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				StringBuffer fields = new StringBuffer();
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				// unitTableName=key;
				if ("A01".equalsIgnoreCase(key)) {
					unitTableName = "B01";
					key = "B01";
				} else {
					unitTableName = key;
				}
				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						if ("priority".equalsIgnoreCase(
                                fielditem.getViewvalue())) {
							continue;
						}
						fields.append(fielditem.getItemid());
						Object obj = getOrgClassValue(org, fielditem
								.getViewvalue()); // 得到get，set值
						if (obj != null) {
							String strvalue = "";
							if (fielditem.getViewvalue() != null
									&& "orgId"
											.equalsIgnoreCase(fielditem.getViewvalue())) {
								strvalue = (String) obj;
								strvalue = getB0110FromCorcodes(strvalue); // 得到组织机构id
							} else {
								strvalue = (String) obj;
							}
							if (strvalue == null || strvalue.length() <= 0) {
								fieldvalues.append("null");
							} else {
								if ("0".equals(fielditem.getCodesetid())
										|| "UN"
												.equals(fielditem.getCodesetid())
										|| "UM"
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
										fieldvalues.append("'"
												+ getCodeItem(fielditem
														.getCodesetid(),
														strvalue) + "'");
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
					if ("B01".equalsIgnoreCase(key)) {
						isCorrect = maininfoUpdateB01(unitTableName, fields
								.toString(), fieldvalues.toString(), b0110, "",
								this.conn);
					} else {
						isCorrect = detailinfoUpdate("2", unitTableName, fields
								.toString(), fieldvalues.toString(), "B0110",
								b0110, "", this.conn);
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
	 * 获得业务系统所有的组织机构列表
	 * 
	 * @return
	 */
	public Organization[] getAllOrganizations() {
		Organization[] orgs = null;
		HashMap setidMap = getEmployItem("/param/org/rec"); // 把B0110默认主集是A01，强制改为B01，因为这里需要查询B01中
		HashMap orgMap = getCorcodeMap(); // <> @K
		Iterator it = setidMap.entrySet().iterator();
		String tableName = "";
		StringBuffer fields = new StringBuffer();
		try {
			StringBuffer sqlB01 = new StringBuffer();
			StringBuffer sql_str = new StringBuffer();
			int j = 0;
			StringBuffer sql = new StringBuffer();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey(); // 主集
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				if ("A01".equalsIgnoreCase(key)) {
					key = "B01";
				}
				tableName = key;
				StringBuffer itemidfields = new StringBuffer();
				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						String fieldname = fielditem.getViewvalue();

						fields.append(fieldname + ",");
						itemidfields.append(fielditem.getItemid() + " "
								+ fieldname + ",");
					}
				}
				if ("B01".equals(key)) {
					sqlB01.append(" left join (select "
							+ itemidfields.toString() + "A.b0110 from " + key
							+ " A");
					sqlB01.append(") " + key + "A");
					sqlB01.append(" on ");
					sqlB01.append("");
				} else {
					sql_str.append(" left join (select "
							+ itemidfields.toString() + "A.B0110 from " + key
							+ " A,");
					sql_str.append("(select b0110,max(i9999) as i9999 from "
							+ key + " group by b0110)B");
					sql_str
							.append(" where A.b0110=B.b0110 and A.i9999=B.i9999) "
									+ key);
					sql_str.append(" on ");
				}
				if ("B01".equals(key)) {
					sqlB01
							.append("Organization.codeitemid=" + key
									+ "A.B0110 ");
				} else {
					// select_str.append(oldItemid+".a0100="+itemid+i+".a0100
					// ");
					sql_str
							.append("Organization.codeitemid=" + key
									+ ".B0110 ");
				}
				j++;
			}
			sql
					.append("select "
							+ fields
							+ " Organization.codeitemid,Organization.a0000,Organization.codesetid from Organization ");
			sql.append(sqlB01.toString());
			sql.append(sql_str.toString());
			// sql.append("where "+Sql_switcher.isnull("corcode","''")+"<>''");
			sql.append(" where codesetid<>'@K'");
			List rs = ExecuteSQL.executeMyQuery(sql.toString(), conn); // ----
			// > sql
			// A01
			// 或者
			// AXXX
			if (rs == null || rs.size() <= 0) {
                return null;
            }
			orgs = new Organization[rs.size()];
			String value = "";
			for (int i = 0; i < rs.size(); i++) {
				Organization org = new Organization();
				LazyDynaBean rec = (LazyDynaBean) rs.get(i);
				value = (String) rec.get("orgid");
				if (value == null || value == "") {
					value = (String) rec.get("codeitemid");
				}
				// value=(String)orgMap.get(value);
				value = value != null && value.length() > 0 ? value : "";
				value = getCorcodeFromB0110(value);
				org.setOrgId(value);

				value = (String) rec.get("parent");
				value = value != null && value.length() > 0 ? value : "";
				value = getcordparent(value);
				org.setParent(value);

				value = (String) rec.get("displayname");
				value = value != null && value.length() > 0 ? value : "";
				org.setDisplayName(value);

				value = (String) rec.get("name");
				if (value == null || value == "") {
					value = (String) rec.get("codeitemid");
					value = getorgname(value);
				}
				value = value != null && value.length() > 0 ? value : "";
				org.setName(value);

				value = (String) rec.get("officiallevel");
				value = value != null && value.length() > 0 ? value : "";
				org.setOfficialLevel(value);

				value = (String) rec.get("a0000");
				value = value != null && value.length() > 0 ? value : "0";
				org.setPriority(Integer.parseInt(value));

				value = (String) rec.get("linkman");
				value = value != null && value.length() > 0 ? value : "";
				org.setLinkMan(value);

				value = (String) rec.get("tel");
				value = value != null && value.length() > 0 ? value : "";
				org.setTel(value);

				value = (String) rec.get("fax");
				value = value != null && value.length() > 0 ? value : "";
				org.setFax(value);

				value = (String) rec.get("mail");
				value = value != null && value.length() > 0 ? value : "";
				org.setMail(value);

				value = (String) rec.get("codesetid");
				value = value != null && value.length() > 0 ? value : "";
				org.setStyle(value);
				orgs[i] = org;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orgs;
	}

	/**
	 * 返回指定组织节点下的机构列表 withInherit=true 返回 outerDeptId 的下面所有的孩子 withInherit
	 * =false 返回 outerDeptId 的信息
	 */
	public Organization[] getAllOrganizations(String outerDeptId,
			boolean withInherit) {
		Organization[] orgs = null;
		HashMap setidMap = getEmployItem("/param/org/rec"); // 把B0110默认主集是A01，强制改为B01，因为这里需要查询B01中
		Iterator it = setidMap.entrySet().iterator();
		String tableName = "";
		StringBuffer fields = new StringBuffer();
		try {
			StringBuffer sqlB01 = new StringBuffer();
			StringBuffer sql_str = new StringBuffer();
			int j = 0;
			StringBuffer sql = new StringBuffer();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey(); // 主集
				StringBuffer fieldvalues = new StringBuffer();
				ArrayList fieldlist = (ArrayList) entry.getValue();
				if ("A01".equalsIgnoreCase(key)) {
					key = "B01";
				}
				tableName = key;
				StringBuffer itemidfields = new StringBuffer();
				if (fieldlist != null && fieldlist.size() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fielditem = (FieldItem) fieldlist.get(i);
						String fieldname = fielditem.getViewvalue();

						fields.append(fieldname + ",");
						itemidfields.append(fielditem.getItemid() + " "
								+ fieldname + ",");
					}
				}
				if ("B01".equals(key)) {
					sqlB01.append(" left join (select "
							+ itemidfields.toString() + "A.b0110 from " + key
							+ " A");
					sqlB01.append(") " + key + "A");
					sqlB01.append(" on ");
					sqlB01.append("");
				} else {
					sql_str.append(" left join (select "
							+ itemidfields.toString() + "A.B0110 from " + key
							+ " A,");
					sql_str.append("(select b0110,max(i9999) as i9999 from "
							+ key + " group by b0110)B");
					sql_str
							.append(" where A.b0110=B.b0110 and A.i9999=B.i9999) "
									+ key);
					sql_str.append(" on ");
				}
				if ("B01".equals(key)) {
					sqlB01
							.append("Organization.codeitemid=" + key
									+ "A.B0110 ");
				} else {
					// select_str.append(oldItemid+".a0100="+itemid+i+".a0100
					// ");
					sql_str
							.append("Organization.codeitemid=" + key
									+ ".B0110 ");
				}
				j++;
			}
			sql
					.append("select "
							+ fields
							+ " Organization.codeitemid,Organization.a0000,Organization.codesetid from Organization ");
			sql.append(sqlB01.toString());
			sql.append(sql_str.toString());
			sql.append(" where codesetid<>'@K'");
			if (!withInherit) {
				sql
						.append(" and Organization.codeitemid='" + outerDeptId
								+ "'");
			} else {
				sql.append(" and Organization.codeitemid like '" + outerDeptId
						+ "%' order by codeitemid,A0000");
			}
			List rs = ExecuteSQL.executeMyQuery(sql.toString(), conn); // ----
			// > sql
			// A01
			// 或者
			// AXXX
			if (rs == null || rs.size() <= 0) {
                return null;
            }
			orgs = new Organization[rs.size()];
			String value = "";
			for (int i = 0; i < rs.size(); i++) {
				Organization org = new Organization();
				LazyDynaBean rec = (LazyDynaBean) rs.get(i);
				value = (String) rec.get("orgid");
				if (value == null || value == "") {
					value = (String) rec.get("codeitemid");
				}
				// value=(String)orgMap.get(value);
				value = value != null && value.length() > 0 ? value : "";
				value = getCorcodeFromB0110(value);
				org.setOrgId(value);

				value = (String) rec.get("parent");
				value = value != null && value.length() > 0 ? value : "";
				value = getcordparent(value);
				org.setParent(value);

				value = (String) rec.get("displayname");
				value = value != null && value.length() > 0 ? value : "";
				org.setDisplayName(value);

				value = (String) rec.get("name");
				if (value == null || value == "") {
					value = (String) rec.get("codeitemid");
					value = getorgname(value);
				}
				value = value != null && value.length() > 0 ? value : "";
				org.setName(value);

				value = (String) rec.get("officiallevel");
				value = value != null && value.length() > 0 ? value : "";
				org.setOfficialLevel(value);

				value = (String) rec.get("a0000");
				value = value != null && value.length() > 0 ? value : "0";
				org.setPriority(Integer.parseInt(value));

				value = (String) rec.get("linkman");
				value = value != null && value.length() > 0 ? value : "";
				org.setLinkMan(value);

				value = (String) rec.get("tel");
				value = value != null && value.length() > 0 ? value : "";
				org.setTel(value);

				value = (String) rec.get("fax");
				value = value != null && value.length() > 0 ? value : "";
				org.setFax(value);

				value = (String) rec.get("mail");
				value = value != null && value.length() > 0 ? value : "";
				org.setMail(value);

				value = (String) rec.get("codesetid");
				value = value != null && value.length() > 0 ? value : "";
				org.setStyle(value);
				orgs[i] = org;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orgs;
	}

	/**
	 * 得到组织机构id，同时新增id
	 * 
	 * @param parent
	 *            直属上级机构Id 是hr返回的id
	 * @param orgId
	 *            组织机构Id
	 * @param name
	 *            机构全称
	 * @param orgtableName
	 *            表名
	 * @param dbconn
	 * @param style
	 *            UM 部门；UN单位
	 * @return
	 */
	synchronized static public String getorgcodeitemid(String parent,
			String orgId, String name, String style, String orgtableName,
			Connection dbconn) {
		String strcodeitemid = "001";
		int grade = 1; // 等级
		int A0000 = 1; // 排序号
		int itemid = 1;
		String parentid = null; // 上层组织机构id
		Category.getInstance("com.hrms.frame.dao.ContentDAO").error("创建组织机构");
		Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
				"orgId==" + orgId);
		Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
				"parent==" + parent);
		Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
				"name==" + name);
		Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
				"style==" + style);
		try {
			StringBuffer buf = new StringBuffer();
			StringBuffer A000sql = new StringBuffer();
			StringBuffer sh = new StringBuffer();
			ContentDAO dao = new ContentDAO(dbconn);
			// 取得最大值 用于排序
			A000sql.append("select max(A0000) as a0000 from ");
			A000sql.append(orgtableName);
			RowSet rsetA = dao.search(A000sql.toString());
			if (rsetA.next()) {
				A0000 = rsetA.getInt("a0000") + 1;
			}
			// 本级组织机构id
			buf
					.append("select codeitemid,grade from Organization where codeitemid= ");
			buf.append("'" + parent + "'");
			RowSet rset = dao.search(buf.toString());
			if (rset.next()) {
				strcodeitemid = rset.getString("codeitemid");
				grade = rset.getInt("grade") + 1;
				String srtitemid = strcodeitemid + "001";
				strcodeitemid = getReal(srtitemid, dbconn);
			} else {
				strcodeitemid = getReal(strcodeitemid, dbconn);
			}
			// 上层id
			buf.setLength(0);
			buf
					.append("select codeitemid,childid from Organization where codeitemid=");
			buf.append("'" + parent + "'");
			RowSet rsetshang = dao.search(buf.toString());
			if (rsetshang.next()) {
				parentid = rsetshang.getString("codeitemid");
				String childid = rsetshang.getString("childid");
				if (parentid.equalsIgnoreCase(childid)) {
					sh.append("update  Organization set childid= '");
					sh.append(strcodeitemid);
					sh.append("' where codeitemid= '");
					sh.append(parentid);
					sh.append("' ");
					dao.update(sh.toString());
				}
			} else {
				parentid = strcodeitemid;
			}
			// 查询codesetid,UN;UM 为空查询是否有父ID有父ID =UM；没有 = UN

			if (style == null || "".equals(style)) {
				String codeitemid = "";
				String parentids = "";
				String sql = "select codeitemid,parentid from organization where codeitemid ='"
						+ orgId + "'";
				RowSet rse = dao.search(sql);
				if (rse.next()) {
					codeitemid = rse.getString("codeitemid");
					parentids = rse.getString("parentid");
				}
				if (codeitemid.equals(parentids)) {
					style = "UN";
				} else {
					style = "UM";
				}
			}
			// 写入机构名称
			buf.setLength(0);
			buf.append("insert into ");
			buf.append(orgtableName);
			// 增加两个时间指标 End_Date,Start_Date
			buf
					.append(" (codesetid,codeitemid,codeitemdesc,parentid,childid,grade,A0000,corcode,start_date,end_date) values (?,?,?,?,?,?,?,?,?,?)");
			// buf.append("
			// (codesetid,codeitemid,codeitemdesc,parentid,childid,grade,A0000,corcode)
			// values (?,?,?,?,?,?,?,?)");
			ArrayList sqlvalue = new ArrayList();
			// if(style==null||style=="")
			// {
			// style="UN";
			// }
			sqlvalue.add(style.toUpperCase());
			sqlvalue.add(strcodeitemid.toUpperCase());
			sqlvalue.add(PubFunc.splitString(name, 50));
			// cat.debug("-------code------------>" + parentid);
			if (parentid != null && parentid.trim().length() > 0) {
                sqlvalue.add(parentid.toUpperCase());
            } else {
                sqlvalue.add((parentid + strcodeitemid).toUpperCase());
            }
			sqlvalue.add((strcodeitemid).toUpperCase());
			sqlvalue.add(new Integer(grade));
			sqlvalue.add(new Integer(A0000));
			sqlvalue.add(orgId.toUpperCase());
			// 当前日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String start_date_str = sdf.format(new java.util.Date());
			java.sql.Date start_date = new java.sql.Date(sdf.parse(
					start_date_str).getTime());
			java.sql.Date end_date = new java.sql.Date(sdf.parse("9999-12-31")
					.getTime());
			sqlvalue.add(start_date);// 开始时间
			sqlvalue.add(end_date); // 结束时间
			dao.insert(buf.toString(), sqlvalue);

		} catch (Exception e) {
			e.printStackTrace();
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					e.toString());

		}
		return strcodeitemid;
	}

	/**
	 * 找到下一级 codeitemid
	 * 
	 * @param srtitemid
	 * @param dbconn
	 * @return
	 */
	public static String getReal(String srtitemid, Connection dbconn) {
		String ritemid = null;
		try {
			ContentDAO dao = new ContentDAO(dbconn);
			String sql = "select codeitemid as acodeitemid,parentid as aparentid from Organization where codeitemid = '"
					+ srtitemid + "'";
			RowSet rset = dao.search(sql);
			String tt = null;
			String aparentid = null;
			if (rset.next()) {
				tt = rset.getString("acodeitemid");
				aparentid = rset.getString("aparentid");
			}

			if (tt == null && aparentid == null) {
				ritemid = srtitemid;
				return ritemid;
			} else {
				if (srtitemid.length() == 3) {
					String d = GetNext(tt, aparentid);
					ritemid = getReal(d, dbconn);

				} else if (srtitemid.length() > 3) {
					String dd = tt.substring(0, tt.length() - 3);
					String mid = GetNext(tt, aparentid);
					String smid = dd + mid;
					ritemid = getReal(smid, dbconn);
				}
				// int s =Integer.parseInt(tt)+1;
				// String strings =String.valueOf(s);
				// ritemid=getReal(strings, dbconn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ritemid;
	}

	/**
	 * 反射机制得到相应get方法值
	 * 
	 * @param user
	 * @param beanname
	 * @return
	 */
	private Object getOrgClassValue(Organization org, String beanname) {
		java.lang.Object obj = null;
		String methodName = "get" + beanname.substring(0, 1).toUpperCase();
		// cat.debug("---->"+methodName);
		if (beanname.length() > 1) {
            methodName = methodName + beanname.substring(1);
        }
		try {
			Class formclass = org.getClass();
			// cat.debug("------------------>methodName="+methodName);
			Method myMethod1 = formclass.getMethod(methodName);
			obj = myMethod1.invoke(org);
		} catch (Exception ept) {
			ept.printStackTrace();
			return null;
		}
		return obj;
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
	 * 更新组织结构 通过corcode得到b0110
	 * 
	 * @param corcode
	 * @return
	 */
	private String getB0110FromCorcodes(String corcode) {
		// 普天修改
		// String strsql = "select codeitemid,corcode from organization where
		// corcode='"+corcode+"'";
		String strsql = "select codeitemid,corcode from organization  where codeitemid='"
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
	 * 定义 impmode=1；进行代码转换
	 * 
	 * @param codesetid
	 * @param codedesc
	 * @return
	 */
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
	 * B01表
	 * 
	 * @param unitTableName
	 * @param codeitemid
	 * @param dbconn
	 * @return
	 */
	synchronized static public String getb0110Id(String unitTableName,
			String codeitemid, Connection dbconn) {
		String b0110 = null;
		try {
			StringBuffer buf = new StringBuffer();
			ContentDAO dao = new ContentDAO(dbconn);
			buf.append("insert into ");
			buf.append(unitTableName);
			buf.append("(B0110) values(?)");
			ArrayList list = new ArrayList();
			list.add(codeitemid);
			dao.insert(buf.toString(), list);
			b0110 = codeitemid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b0110;
	}

	/**
	 * 新增组织机构 b01 表增加信息
	 * 
	 * @param tableName
	 * @param fields
	 * @param fieldValues
	 * @param B0110id
	 * @param createUserName
	 * @param a0000s
	 * @param conn
	 */
	private void maininfoInsertB01(String tableName, String fields,
			String fieldValues, String B0110id, String createUserName,
			String a0000s, Connection conn) {
		String[] tempstrs = fields.toString().split(",");
		String[] tempValuestrs = fieldValues.toString().split(",");
		StringBuffer strsql = new StringBuffer();
		strsql.append("update ");
		strsql.append(tableName + " set ");
		if (tempstrs != null && tempstrs.length > 0) {
			for (int i = 0; i < tempstrs.length; i++) {
				if (tempstrs[i] != null && tempstrs[i].length() > 0) {
					if (!"B0110".equalsIgnoreCase(tempstrs[i])) {
						strsql.append(tempstrs[i] + "=" + tempValuestrs[i]
								+ ",");
					}
				}

			}
		}
		strsql.append("CreateTime=" + PubFunc.DoFormatSystemDate(false) + ",");
		strsql.append("ModTime=" + PubFunc.DoFormatSystemDate(false) + ",");
		strsql.append("CreateUserName='" + createUserName + "'");
		strsql.append(" where B0110='" + B0110id + "'");
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.update(strsql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * org表删除检查
	 */
	private void checkorg() {
		StringBuffer sql = new StringBuffer();
		// ContentDAO dao=new ContentDAO(this.getFrameconn());
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			// 消除掉有子节点childid不正确的
			sql.delete(0, sql.length());
			sql.append("UPDATE ");
			sql
					.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
			sql.append("organization d");
			sql.append(" WHERE d.parentid = ");
			sql
					.append("organization.codeitemid AND d.parentid <> d.codeitemid and d.codesetid=organization.codesetid)");
			sql.append(" WHERE  EXISTS (SELECT * FROM ");
			sql.append("organization c");
			sql.append(" WHERE c.parentid = ");
			sql
					.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
			// System.out.println(sql.toString());
			dao.update(sql.toString());
			// 清除掉没有子节点childid不正确的
			/*
			 * sql.delete(0,sql.length()); sql.append("UPDATE ");
			 * sql.append("organization SET childid =codeitemid "); sql.append("
			 * WHERE not EXISTS (SELECT * FROM "); sql.append("organization c");
			 * sql.append(" WHERE c.parentid = ");
			 * sql.append("organization.childid AND organization.childid <>
			 * organization.codeitemid)");
			 */
			// System.out.println(sql.toString());
			// 清除掉没有子节点childid不正确的
			StringBuffer updateParentcode = new StringBuffer();
			updateParentcode.delete(0, updateParentcode.length());
			updateParentcode.append("UPDATE ");
			updateParentcode.append("organization SET childid =codeitemid  ");
			updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
			updateParentcode.append("organization c");
			updateParentcode.append(" WHERE c.parentid = ");
			updateParentcode
					.append("organization.codeitemid and c.parentid<>c.codeitemid ) and organization.childid <> organization.codeitemid");
			// System.out.println(updateParentcode.toString());
			dao.update(updateParentcode.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * organization 表计算codeitemid
	 * 
	 * @param src
	 *            本级id
	 * @param des
	 *            上级id
	 * @return
	 */
	public static String GetNext(String src, String des) {
		int nI, nTag, dd;
		String ch;
		String result = "";
		nTag = 1; // 进位为1
		src = src.toUpperCase();
		if (src.length() == des.length()) {
			for (int i = 0; i < des.length(); i++) {
				if (nTag == 1) {
					String t = src.substring(src.length() - 1, src.length());
					String d = src.substring(0, src.length() - 1); // 前
					ch = GetNextChar(t);
					result = d + ch;
					nTag = 0;
				}

			}
		} else {
			for (nI = src.length(); nI > des.length(); nI--) {
				ch = src.substring(nI - 1, nI);
				if (nTag == 1) {
                    ch = GetNextChar(ch);
                }
				result = ch + result;
				if ("0".equals(ch) && !"0".equals(src.subSequence(nI - 1, nI))) {
					nTag = 1;
				} else {
					nTag = 0;
				}

			}
		}

		return result;
	}

	public static String GetNextChar(String ch) // 获得下一个进位
	{
		String result = "";
		switch (ch.charAt(0)) {
		case '0': {
			result = "1";
			break;
		}
		case '1': {
			result = "2";
			break;
		}
		case '2': {
			result = "3";
			break;
		}
		case '3': {
			result = "4";
			break;
		}
		case '4': {
			result = "5";
			break;
		}
		case '5': {
			result = "6";
			break;
		}
		case '6': {
			result = "7";
			break;
		}
		case '7': {
			result = "8";
			break;
		}
		case '8': {
			result = "9";
			break;
		}
		case '9': {
			result = "A";
			break;
		}
		case 'A': {
			result = "B";
			break;
		}
		case 'B': {
			result = "C";
			break;
		}
		case 'C': {
			result = "D";
			break;
		}
		case 'D': {
			result = "E";
			break;
		}
		case 'E': {
			result = "F";
			break;
		}
		case 'F': {
			result = "G";
			break;
		}
		case 'G': {
			result = "H";
			break;
		}
		case 'H': {
			result = "I";
			break;
		}
		case 'I': {
			result = "J";
			break;
		}
		case 'J': {
			result = "K";
			break;
		}
		case 'K': {
			result = "L";
			break;
		}
		case 'L': {
			result = "M";
			break;
		}
		case 'M': {
			result = "N";
			break;
		}
		case 'N': {
			result = "O";
			break;
		}
		case 'O': {
			result = "P";
			break;
		}
		case 'P': {
			result = "Q";
			break;
		}
		case 'Q': {
			result = "R";
			break;
		}
		case 'R': {
			result = "S";
			break;
		}
		case 'S': {
			result = "T";
			break;
		}
		case 'T': {
			result = "U";
			break;
		}
		case 'U': {
			result = "V";
			break;
		}
		case 'V': {
			result = "W";
			break;
		}
		case 'W': {
			result = "X";
			break;
		}
		case 'X': {
			result = "Y";
			break;
		}
		case 'Y': {
			result = "Z";
			break;
		}
		case 'Z': {
			result = "0";
			break;
		}
		}
		return result;
	}

	private String OrgdetailinfoInsert(String insertType, String tableName,
			String fields, String fieldValues, String userid,
			String createUserName, Connection conn) {
		String id = "";
		String personname = createUserName;// this.getUsername(tableName,userid,conn);
		// chenmengqing added
		StringBuffer strsql = new StringBuffer();
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

			System.out.println(e.getMessage());
		}
		return id;
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
	 * 根据 outerOrgId=corcode 更改组织机构的 codeitemdesc
	 * 
	 * @param orgtableName
	 * @param outerOrgId
	 * @param name
	 * @param dbconn
	 * @return
	 */
	public String uporgname(String orgtableName, String outerOrgId,
			String name, Connection dbconn) {
		String codeitemid = null;
		String start_date = null;
		try {
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					"修改组织机构");
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					"outerOrgId==" + outerOrgId);
			ContentDAO dao = new ContentDAO(conn);
			// 当前时间
			StringBuffer buf = new StringBuffer();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String mDateTime = sdf.format(new java.util.Date());

			buf
					.append("select codeitemid,start_date from Organization where codeitemid = '");
			buf.append(outerOrgId);
			buf.append("' ");

			RowSet rs = dao.search(buf.toString());
			if (rs.next()) {
				codeitemid = rs.getString("codeitemid");
				start_date = rs.getString("start_date");
			}
			if (name != null) {
				if (start_date == null) {
					buf.delete(0, buf.length());
					buf.append("UPDATE ");
					buf.append("organization set codeitemdesc= '");
					buf.append(PubFunc.splitString(name, 50));
					// buf.append("' WHERE corcode = '"); 普天修改
					// 跟新创建时间
					buf.append("' ,Start_Date="
							+ Sql_switcher.dateValue(mDateTime));
					buf.append(",end_date="
							+ Sql_switcher.dateValue("9999-12-31"));
					buf.append(" WHERE codeitemid = '");
					buf.append(outerOrgId);
					buf.append("' ");
					dao.update(buf.toString());
				} else if ("".equals(start_date)) {
					buf.delete(0, buf.length());
					buf.append("UPDATE ");
					buf.append("organization set codeitemdesc= '");
					buf.append(PubFunc.splitString(name, 50));
					// buf.append("' WHERE corcode = '"); 普天修改
					// 跟新创建时间
					buf.append("' ,Start_Date="
							+ Sql_switcher.dateValue(mDateTime));

					buf.append(",end_date="
							+ Sql_switcher.dateValue("9999-12-31") + "");
					buf.append(" WHERE codeitemid = '");
					buf.append(outerOrgId);
					buf.append("' ");
					dao.update(buf.toString());
				} else {
					buf.delete(0, buf.length());
					buf.append("UPDATE ");
					buf.append("organization set codeitemdesc= '");
					buf.append(PubFunc.splitString(name, 50));
					// buf.append("' WHERE corcode = '"); 普天修改
					// 跟新创建时间
					// buf.append("' ,Start_Date='"+mDateTime);
					buf.append("' WHERE codeitemid = '");
					buf.append(outerOrgId);
					buf.append("' ");
					dao.update(buf.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					e.toString());
		}
		return codeitemid;
	}

	/**
	 * 更新组织机构 更新B01表
	 * 
	 * @param tablename
	 * @param fields
	 * @param fieldValues
	 * @param b0110
	 * @param createUserName
	 * @param conn
	 */
	public boolean maininfoUpdateB01(String tablename, String fields,
			String fieldValues, String b0110, String createUserName,
			Connection conn) {
		boolean isCorrect = false;
		String[] tempstrs = fields.toString().split(",");
		String[] tempValuestrs = fieldValues.toString().split("`");
		StringBuffer strsql = new StringBuffer();
		strsql.append("update ");
		strsql.append(tablename + " set ");
		if (tempstrs != null && tempstrs.length > 0) {
			for (int i = 0; i < tempstrs.length; i++) {
				if (tempstrs[i] != null && tempstrs[i].length() > 0) {
                    if (!"B0110".equalsIgnoreCase(tempstrs[i])) {
                        strsql.append(tempstrs[i] + "=" + tempValuestrs[i]
                                + ",");
                    }
                }
			}
		}
		strsql.append("ModTime=" + PubFunc.DoFormatSystemDate(false) + ",");
		strsql.append("ModUserName='" + createUserName + "'");
		strsql.append(" where b0110 = '");
		strsql.append(b0110);
		strsql.append("' ");
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
		String[] tempstrs = fields.toString().split(","); // 名
		String[] tempValuestrs = fieldValues.toString().split("`"); // 值
		StringBuffer strsql = new StringBuffer();
		strsql.append("update ");
		strsql.append(tableName);
		strsql.append(" set ");
		for (int i = 0; i < tempstrs.length; i++) {
			if (tempstrs[i] != null) {
				if (!"B0110".equalsIgnoreCase(tempstrs[i])) {
					strsql.append(tempstrs[i] + "=" + tempValuestrs[i] + ",");
				}
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
	 * 通过b0110得到corcode
	 * 
	 * @param b0110
	 * @return
	 */
	private String getCorcodeFromB0110(String b0110) {
		String strsql = "select codeitemid,corcode from organization  where codeitemid='"
				+ b0110 + "'";
		String corcode = "";
		String si = "";
		try {
			// System.out.println(strsql);
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(strsql);
			while (rs.next()) {
				// 普天修改：返回hr平台的 codeitemid
				// si=rs.getString("corcode");
				// if(si==null||si==""){
				si = rs.getString("codeitemid");
				// }
			}
			corcode = si;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corcode;
	}

	private String getcordparent(String value) {
		String parent = "";
		String sql = "select codeitemid,corcode from Organization where codeitemid= '"
				+ value + "'";
		try {
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sql);
			while (rs.next()) {
				// 普天修改 返回 hr平台 codeitemid
				// parent=rs.getString("corcode");
				// if(parent==null||parent=="")
				// {
				parent = rs.getString("codeitemid");
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parent;
	}

	private String getorgname(String value) {
		String codeitemdesc = "";
		String sql = "select codeitemdesc from Organization where codeitemid = '"
				+ value + "'";
		try {
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sql);
			while (rs.next()) {
				codeitemdesc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeitemdesc;
	}

	/**
	 * 机构批量新增
	 * 
	 * @param setid
	 * @param stafftype
	 * @param keyfield
	 * @param columns
	 * @param list
	 * @return
	 */
	public int batchAppendToOrg(String createUserName, String inforType,
			String setid, String keyfield, List columns, List list) {
		if ("B01".equalsIgnoreCase(setid) || "K01".equalsIgnoreCase(setid)) {
			return batchEditorToOrg(createUserName, inforType, setid, keyfield,
					columns, list);
		} else {
			List dataList = getColumnsByXml(list);
			List fieldList = getFieldList(columns);
			String table = setid.toUpperCase();
			
			try {
				
				ContentDAO dao = new ContentDAO(this.conn);
				ArrayList sqlList = new ArrayList();
				Iterator dataIt = dataList.iterator();
				while (dataIt.hasNext()) {
					StringBuffer sql = new StringBuffer();
					StringBuffer colName = new StringBuffer();
					StringBuffer value = new StringBuffer();
					Map map = (Map) dataIt.next();
					Iterator it = fieldList.iterator();
					String keyValue = (String) map.get(keyfield.toLowerCase());
					if (keyValue == null) {
						return -1;
					}
					String keyFieldName = "";
					keyValue = (String) map.get(keyfield.toLowerCase());
					if("B".equalsIgnoreCase(inforType)){
						keyFieldName = "B0110";
					} else {
						keyFieldName = "E01A1";
					}
					if("B0110".equalsIgnoreCase(keyfield) || "E01A1".equalsIgnoreCase(keyfield)){
						keyValue = this.getB0110FromCorcodes(keyValue);
					} else {
						keyValue = this.getB0110FromCorcode(keyValue);
					}
					if (keyValue == null) {
						return -1;
					}
					int maxI9999 = getMaxI9999(table, keyFieldName, keyValue);
					colName.append("I9999,");
					value.append(++maxI9999 + ",");
					colName.append("CreateTime,");
					value.append(PubFunc.DoFormatSystemDate(false) + ",");
					colName.append("ModTime,");
					value.append(PubFunc.DoFormatSystemDate(false) + ",");
					colName.append("CreateUserName");
					value.append("'" + createUserName + "',");
					while (it.hasNext()) {
						FieldItem field = (FieldItem) it.next();
						String fieldName = field.getItemid();
						String fieldValue = (String) map.get(fieldName);
						if (fieldValue == null) {
							return -1;
						}
						colName.append(fieldName + ",");
						if ("D".equals(field.getItemtype())) {
							value.append("'" + Sql_switcher.dateValue(fieldValue)
									+ "',");
						} else if ("N".equals(field.getItemtype())) {
							value.append(fieldValue + ",");
						} else {
							value.append("'" + fieldValue + "',");
						}
					}
					colName.append(keyFieldName);
					value.append("'" + keyValue + "'");
					sql.append("INSERT INTO " + table + "(" + colName + ")");
					sql.append("VALUES (" + value + ")");
					sqlList.add(sql.toString());
				}
				dao.batchUpdate(sqlList);
			} catch (Exception e) {
				
				e.printStackTrace();
				return -1;
			}
			return dataList.size();
		}
	}

	private int batchEditorToOrg(String createUserName, String inforType,
			String setid, String keyfield, List columns, List list) {
		List dataList = getColumnsByXml(list);
		List fieldList = getFieldList(columns);
		String table = setid.toString();
	
		int A0000 = 0;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String A000sql = "select max(A0000) as a0000 from organization";
			RowSet rsetA = dao.search(A000sql.toString());
			if (rsetA.next()) {
				A0000 = rsetA.getInt("a0000");
			}
			ArrayList sqlList = new ArrayList();
			Iterator dataIt = dataList.iterator();
			while (dataIt.hasNext()) {
				StringBuffer sql = new StringBuffer();
				Map map = (Map) dataIt.next();
				String codeitemid = "";
				String keyValue = "";
				if ("B0110".equalsIgnoreCase(keyfield)
						|| "E01A1".equalsIgnoreCase(keyfield)) {
					codeitemid = (String) map.get(keyfield);
					if (codeitemid != null && codeitemid.length() > 0) {
						codeitemid = getB0110FromCorcodes(codeitemid);
						if (codeitemid == null || codeitemid.length() > 0) {
							return -1;
						}
					}
				} else if ("corcode".equalsIgnoreCase(keyfield)) {
					keyValue = (String) map.get(keyfield);
					codeitemid = this.getB0110FromCorcode(keyValue);
				} else {
					return -1;
				}
				String codesetid = (String) map.get("codesetid");
				String parentid = (String) map.get("parentid");
				String codeitemdesc = (String) map.get("codeitemdesc");
				String corcode = keyValue;
				if (codeitemid == null || codeitemid.length() < 1) {// 新增
					StringBuffer colName = new StringBuffer();
					StringBuffer value = new StringBuffer();
					A0000++;
					Map sqlMap = createOrgSql(codesetid, parentid,
							codeitemdesc, A0000, corcode);
					codeitemid = (String) sqlMap.get("codeitemid");
					sqlList.add((String) sqlMap.get("sql"));
					Iterator it = fieldList.iterator();
					while (it.hasNext()) {
						FieldItem field = (FieldItem) it.next();
						String fieldName = field.getItemid();
						String fieldValue = (String) map.get(fieldName);
						if (fieldValue == null) {
							return -1;
						}
						colName.append("CreateTime,");
						value.append(PubFunc.DoFormatSystemDate(false) + ",");
						colName.append("ModTime,");
						value.append(PubFunc.DoFormatSystemDate(false) + ",");
						colName.append("CreateUserName");
						value.append("'" + createUserName + "',");

						colName.append(fieldName + ",");
						if ("D".equals(field.getItemtype())) {
							value.append(Sql_switcher.dateValue(fieldValue)
									+ ",");
						} else if ("N".equals(field.getItemtype())) {
							value.append(fieldValue + ",");
						} else {
							value.append("'" + fieldValue + "',");
						}
					}
					if ("B01".equalsIgnoreCase(table)) {
						sql.append("INSERT INTO B01( B0110," + colName
								+ ") VALUES('" + codeitemid + "'," + value
								+ ")");
					} else if ("K01".equalsIgnoreCase(table)) {
						sql.append("INSERT INTO K01( E01A1," + colName
								+ ") VALUES('" + codeitemid + "'," + value
								+ ")");
					} else {
						return -1;
					}
				} else {// 修改
					StringBuffer cols = new StringBuffer();
					String upSql = this.updateOrgSql(codeitemid, codeitemdesc);
					if (upSql.length() > 0) {
						sqlList.add(upSql);
					}
					Iterator it = fieldList.iterator();
					while (it.hasNext()) {
						FieldItem field = (FieldItem) it.next();
						String fieldName = field.getItemid();
						String fieldValue = (String) map.get(fieldName);
						if (fieldValue == null) {
							return -1;
						}
						cols.append("ModTime="
								+ PubFunc.DoFormatSystemDate(false) + ",");
						cols.append("modUserName='" + createUserName + "',");
						if ("D".equals(field.getItemtype())) {
							cols.append(fieldName + "="
									+ Sql_switcher.dateValue(fieldValue) + ",");
						} else if ("N".equals(field.getItemtype())) {
							cols.append(fieldName + "=" + fieldValue + ",");
						} else {
							cols.append(fieldName + "='" + fieldValue + "',");
						}
					}
					if ("B01".equalsIgnoreCase(table)) {
						sql.append("UPDATE B01 SET " + cols + " WHERE B0110='"
								+ codeitemid + "'");
					} else if ("K01".equalsIgnoreCase(table)) {
						sql.append("UPDATE K01 SET " + cols + " WHERE E01A1='"
								+ codeitemid + "'");
					} else {
						return -1;
					}
					sqlList.add(sql.toString());
				}
			}

			dao.batchUpdate(sqlList);

			
		} catch (Exception e) {
			
			e.printStackTrace();
			return -1;
		}
		return dataList.size();
	}

	/**
	 * 机构批量更新
	 * 
	 * @param setid
	 * @param stafftype
	 * @param keyfield
	 * @param columns
	 * @param list
	 * @return
	 */
	public int batchUpdateToOrg(String modUserName, String inforType,
			String setid, String keyfield, List columns, List list) {

		if ("K01".equalsIgnoreCase(setid) || "B01".equalsIgnoreCase(setid)) {
			return batchEditorToOrg(modUserName, inforType, setid, keyfield,
					columns, list);
		} else {
			List dataList = getColumnsByXml(list);
			List fieldList = getFieldList(columns);
			String table = setid.toUpperCase();
			
			try {
				ArrayList sqlList = new ArrayList();
				ContentDAO dao = new ContentDAO(this.conn);
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
					cols.deleteCharAt(cols.length());
					String keyValue = "";
					String keyFieldName = "";
					keyValue = (String) map.get(keyfield.toLowerCase());
					if("B".equalsIgnoreCase(inforType)){
						keyFieldName = "B0110";
					} else {
						keyFieldName = "E01A1";
					}
					if("B0110".equalsIgnoreCase(keyfield) || "E01A1".equalsIgnoreCase(keyfield)){
						keyValue = this.getB0110FromCorcodes(keyValue);
					} else {
						keyValue = this.getB0110FromCorcode(keyValue);
					}
					if (keyValue == null) {
						return -1;
					}
					where.append("WHERE " + keyFieldName + "='" + keyValue + "'");
					int maxI9999 = getMaxI9999(table, keyFieldName, keyValue);
					if (maxI9999 != 0) {
						where.append(" AND I9999 = " + maxI9999);
					} else {
						return -1;
					}
					sql.append("UPDATE " + table + " SET " + cols);
					sql.append(" " + where);
					sqlList.add(sql.toString());
				}
				dao.batchUpdate(sqlList);

			} catch (Exception e) {
				
				e.printStackTrace();
				return -1;
			}
			return dataList.size();
		}
	}

	/**
	 * 机构批量删除
	 * 
	 * @param setid
	 * @param stafftype
	 * @param keyfield
	 * @param columns
	 * @param list
	 * @return
	 */
	public int batchDeleteToOrg(String modUserName, String inforType,
			String setid, String keyfield, List columns, List list) {
		List dataList = getColumnsByXml(list);
		//List fieldList = getFieldList(columns);
		String table = setid.toUpperCase();

		try {
			ArrayList sqlList = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			
			Iterator dataIt = dataList.iterator();
			while (dataIt.hasNext()) {
				StringBuffer where = new StringBuffer();
				Map map = (Map) dataIt.next();
				String keyValue = "";
				String keyFieldName = "";
				String codeitemid = "";
				keyValue = (String) map.get(keyfield.toLowerCase());
				if("B".equalsIgnoreCase(inforType)){
					keyFieldName = "B0110";
				} else {
					keyFieldName = "E01A1";
				}
				if("B0110".equalsIgnoreCase(keyfield) || "E01A1".equalsIgnoreCase(keyfield)){
					codeitemid = this.getB0110FromCorcodes(keyValue);
				} else {
					codeitemid = this.getB0110FromCorcode(keyValue);
				}
				if (codeitemid == null || codeitemid.length() < 1) {
					return -1;
				}
				where.append("WHERE " + keyFieldName + "='" + codeitemid + "'");
				if (!"B01".equalsIgnoreCase(table) && !"K01".equalsIgnoreCase(table)) {
					int maxI9999 = getMaxI9999(table, keyFieldName, codeitemid);
					where.append(" AND I9999=" + maxI9999);
				}
				sqlList.add("DELETE FROM " + table + " " + where);
				if ("B01".equalsIgnoreCase(table) || "K01".equalsIgnoreCase(table)) {
					sqlList.add(this.deleteOrgSql(codeitemid));
				}
			}
			dao.batchUpdate(sqlList);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return -1;
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
		while (it.hasNext()) {
			Map columns = new HashMap();
			Element element = (Element) it.next();
			List column = element.elements();
			Iterator cit = column.iterator();
			while (cit.hasNext()) {
				Element field = (Element) cit.next();
				columns.put(field.getName().toLowerCase(), field.getTextTrim());
			}
			dataList.add(columns);
		}
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

	private String getMaxChildID(String codeItemid) {
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "";
		if (codeItemid != null || codeItemid.length() > 0) {
			sql = "SELECT MAX(CODEITEMID) FROM ORGANIZATION WHERE PARENTID ='"
					+ codeItemid + "'";
		} else {
			sql = "SELECT MAX(CODEITEMID) FROM ORGANIZATION WHERE PARENTID = CODEITEMID";
		}
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				return rs.getString("CODEITEMID");
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
		return null;
	}

	/**
	 * 根据机构编码得到该机构的记录
	 * 
	 * @param codeItemid
	 * @return
	 */
	private RecordVo getOrgVo(String codeItemid) {
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "SELECT * FROM ORGANIZATION WHERE CODEITEMID ='"
				+ codeItemid + "'";
		RowSet rs = null;
		RecordVo vo = new RecordVo("ORGANIZATION");
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				vo.setString("codesetid", rs.getString("codesetid"));
				vo.setString("codeitemid", rs.getString("codeitemid"));
				vo.setString("codeitemdesc", rs.getString("codeitemdesc"));
				vo.setString("childid", rs.getString("childid"));
				vo.setInt("grade", rs.getInt("grade"));
				vo.setInt("layer", rs.getInt("layer"));
				return vo;
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
		return null;
	}

	public Map createOrgSql(String codesetid, String parentid,
			String codeitemdesc, int a0000, String corcode) {
		StringBuffer sql = new StringBuffer();
		String codeitemid = "";
		Map map = new HashMap();
		int grade = 1;
		int layer = 1;
		if (parentid != null || parentid.length() > 0) {
			RecordVo vo = getOrgVo(parentid);
			if (vo != null) {
				grade = vo.getInt("grade") + 1;
				layer = vo.getInt("layer");
				String pcodesetid = vo.getString("codesetid");
				if (pcodesetid.equalsIgnoreCase(codesetid)) {
					layer += 1;
				} else {
					layer = 1;
				}
				codeitemid = getMaxChildID(parentid);
				if (codeitemid != null && codeitemid.length() > 0) {
					codeitemid = GetNext(codeitemid, parentid);
				} else {
					codeitemid = parentid + "01";
					sql.append("UPDATE SET childid=" + codeitemid
							+ "WHERE codeitemid='" + parentid + "';");
				}
			} else {
				return null;
			}
		} else {// 顶级机构
			codeitemid = getMaxChildID(parentid);
			if (codeitemid != null && codeitemid.length() > 0) {
				codeitemid = GetNext(codeitemid, codeitemid);
			} else {
				codeitemid = parentid + "01";
			}
			parentid = codeitemid;
		}
		sql
				.append("INSERT INTO ORGANIZATION(codesetid,codeitemid,codeitemdesc,parentid,childid,grade,a0000,layer,corcode,end_date,start_date) VALUES(");
		sql.append("'" + codesetid + "',");
		sql.append("'" + codeitemid + "',");
		sql.append("'" + codeitemdesc + "',");
		sql.append("'" + parentid + "',");
		sql.append("'" + codeitemid + "',");
		sql.append(grade + ",");
		sql.append(a0000 + ",");
		sql.append(layer + ",");
		sql.append("'" + corcode + "',");
		sql.append(Sql_switcher.dateValue("9999-12-31") + ",");
		sql.append(Sql_switcher.sysDay() + ")");
		map.put("sql", sql.toString());
		map.put("codeitemid", codeitemid);
		return map;
	}

	public String updateOrgSql(String codeitemid, String codeitemdesc) {
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao
					.search("SELECT 1 FROM ORGANIZATION WHERE WHERE codeitemid='"
							+ codeitemid
							+ "' AND codeitemdesc = '"
							+ codeitemdesc + "'");
			if (rs.next()) {
				return sql.toString();
			} else {
				sql.append("UPDATE ORGANIZATION SET codeitemdesc = '"
						+ codeitemdesc + "' WHERE codeitemid='" + codeitemid
						+ "'");
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
		return sql.toString();
	}

	public String deleteOrgSql(String codeitemid) {
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao
					.search("SELECT 1 FROM ORGANIZATION WHERE WHERE codeitemid='"
							+ codeitemid + "'");
			if (rs.next()) {
				sql.append("DELETE FROM ORGANIZATION WHERE codeitemid='"
						+ codeitemid + "'");
			} else {
				return sql.toString();
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
		return sql.toString();
	}

	private int getMaxI9999(String table, String fieldName, String fieldValue) {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAX(I9999) FROM ");
		sql.append(table);
		sql.append(" WHERE " + fieldName + "='" + fieldValue + "'");
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				return rs.getInt("I9999");
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
