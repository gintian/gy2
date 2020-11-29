package com.hjsj.hrms.businessobject.sys.sysout;

import com.hjsj.hrms.utils.LDAPTools;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExeLdap {

	private LDAPTools ld;

	public ExeLdap(LDAPTools ld) {
		this.ld = ld;
	}

	public Map eResult(LazyDynaBean bean, String nameKey) {
		// ld = getLDAPTools();
		if (ld == null) {
			System.out.println("连接LADP失败");
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(
					"连接LADP失败");
			return null;
		}
		Map result = new HashMap();
		String flag = (String) bean.get("flag");// 执行标记 1、新增 2、修改 3、删除
		String c01NR = (String) bean.get(nameKey);// 获得 key 的 值
		if (c01NR == null || c01NR.length() <= 0) {
            return null;
        }
		String sql = "select where name = " + c01NR;
		if ("1".equals(flag) || "2".equals(flag)) {// 新增或修改
			// -------------------------------配置参数--------------------------------------------------
			BasicAttributes attributes = new BasicAttributes();
			BasicAttribute attribute = new BasicAttribute("objectClass");
			attribute.add("top");
			attribute.add("person");
			attribute.add("organizationalPerson");
			attribute.add("user");

			String e0122 = switchUMCode((String) bean.get("e0122_0"));
			String a0101 = bean.get("a0101") == null ? "" : (String) bean
					.get("a0101");
			if (!"".equals(a0101) && e0122 != null && e0122.length() > 0) {
				attributes
						.put("description", switchPinyin(e0122) + " " + a0101);
			}
			if (!"".equals(a0101)) {
				attributes.put("sn", a0101);
				attributes.put("givenName", a0101);
				attributes.put("displayName", a0101);
			} else {
				return null;
			}
			if (e0122 != null && e0122.length() > 0) {
				attributes.put("department", e0122);
			}
			if (bean.get("c01s3") != null
					&& ((String) bean.get("c01s3")).length() > 0) {
				attributes.put("mail", bean.get("c01s3") == null ? "" : bean
						.get("c01s3"));
			}
			attributes.put("sAMAccountName", c01NR);// 用户登陆名称
			attributes.put("userPrincipalName", c01NR + "@chrdi.com");// UPN，完整的用户登陆名
			String dn = "cn=" + c01NR + ",cn=Users";
			attributes.put("name", c01NR);
			// ------------------------------配置参数----------------------------------------------
			if (findIsExits(sql)) {// 修改
				String attrID[] = { "sn", "givenName", "displayName", "mail",
						"department", "description", "name", "sAMAccountName",
						"userPrincipalName", "memberOf" };// 需要修改的参数 用于判断
				// 这些参数是更新还是添加
				String account = (String) (this.ld.getAccount(sql)).get(0);// 获得用户账户
				Attribute attrMemberOf = (BasicAttribute) this.ld
						.findByAccount(account).get("memberOf");
				if (attrMemberOf == null) {
					attrMemberOf = new BasicAttribute("memberOf");
					attrMemberOf.add(getMemberOf(e0122));// 新增加
				} else {
					String MemberOf = getMemberOf(e0122);
					attrMemberOf.remove(MemberOf);
					attrMemberOf.add(MemberOf);
				}
				attributes.put(attrMemberOf);
				this.ld.modify(account, attrID, attributes);// 修改操作
			} else {// 新增
				attributes.put(attribute);// 添加objectClass属性
				BasicAttribute attrMemberOf = new BasicAttribute("memberOf");
				attrMemberOf.add(getMemberOf(e0122));// 新增加
				this.ld.add(dn, attributes);// 新增操作
			}
			result.put("flag", flag);
		} else if ("3".equals(flag)) {// 删除
			if (findIsExits(sql)) {
				String account = (String) (this.ld.getAccount(sql)).get(0);// 获得用户账户
				this.ld.delete(account);// 删除
			}
			result.put("flag", flag);
		} else {
			result.put("flag", "0");
		}
		result.put(nameKey, c01NR);
		return result;
	}

	private String getMemberOf(String value) {
		if (value == null || value.length() <= 0) {
            return "";
        }
		StringBuffer memberOf = new StringBuffer();
		memberOf.append("CN=u" + getNumStr(value) + ",");
		memberOf.append("CN=Users,DC=chrdi,DC=com");
		return memberOf.toString();
	}

	private String getNumStr(String value) {
		if (value == null || value.length() <= 0) {
            return "";
        }
		String str = zhuanHuanNum(value);
		return PubFunc.getPinym(str);
	}

	// 判断用户是否存在
	private boolean findIsExits(String sql) {
		List list = this.ld.find(sql);
		if (list != null) {
			int i = list.size();
			if (i >= 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
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

	private static LDAPTools getLDAPTools() {
		String LDAPBASEDN = SystemConfig.getPropertyValue("LDAPROOT");// 获得配置文件信息
		String LDAPIPADRESS = SystemConfig.getPropertyValue("LDAPIPADRESS");// 获得配置文件信息
		String LDAPPORT = SystemConfig.getPropertyValue("LDAPPORT");// 获得配置文件信息
		String LDAPACCOUNT = SystemConfig.getPropertyValue("LDAPACCOUNT");// 获得配置文件信息
		String LDAPPASSWORD = SystemConfig.getPropertyValue("LDAPPASSWORD");// 获得配置文件信息
		return new LDAPTools(LDAPBASEDN, LDAPIPADRESS, LDAPPORT, LDAPACCOUNT,
				LDAPPASSWORD);
	}

	public static void main(String[] args) {
		LDAPTools ld1 = new LDAPTools("cd=kowloon,cd=com", "192.192.100.168",
				"389", "cn=Manager,dc=kowloon,dc=com", "password");
		List list = ld1.getAccount("select where name = *");
		for (int i = 0; i < list.size(); i++) {
			ld1.delete((String) list.get(i));
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
						| i == chnStr.length() - 1
						| (i + 1 < chnStr.length() && (chnStr.charAt(i + 1) == '亿' | chnStr
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
				if (bit == '千' | bit == '百') {
					if (tempNum != 0) {
						tempNum = tempNum
								* Integer.parseInt((String) unitMap.get(bit
										+ ""));
					}
					queue.add("" + tempNum);
					tempNum = 0;
				}

				// 遇到亿、万 队列中各元素依次累加*单位值、清空队列、新结果值进队列
				if (bit == '亿' | bit == '万') {
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
}
