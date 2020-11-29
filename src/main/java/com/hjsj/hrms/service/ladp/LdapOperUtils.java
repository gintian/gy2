package com.hjsj.hrms.service.ladp;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

/**
 * 
 * <p>
 * 功能描述：ldap的处理类，提供了各种操作ldap的方法。
 * </p>
 * 
 * @author liaowufeng
 * @version 1.0
 */
public class LdapOperUtils {

	// 调用log4j的日志，用于输出
	private static Category log = Category.getInstance(Env.class.getName());

	/**
	 * 根据连接Env信息，取得Ldap DirContext
	 * 
	 * @param env
	 *            连接Env的连接信息
	 * @return Ldap连接的DirContext
	 * @throws BaseException
	 */
	public static DirContext getLdapDirContext(Env env)
			throws GeneralException {
		// 参数为空
		if (env == null) {
			String[] args = { "env" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter env NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));

		}
		// 定义DirContext
		DirContext dirContext = null;
		// 从Ldap连接工厂中，取得Ldap连接
		dirContext = LdapConnectionFactory.getDirContext(env);
		return dirContext;
	}

	/**
	 * 关闭LDAP连接
	 * 
	 * @param dirContext
	 *            DirContext
	 * @throws BaseException
	 */
	public static void closeEnvLdapDirContext(DirContext dirContext)
			throws GeneralException {
		// 关闭LDAP连接
		closeLdapDirContext(dirContext);
	}

	/**
	 * 关闭Ldap 的DirContext
	 * 
	 * @param dirContext
	 *            连接Ldap的DirContext
	 * @throws BaseException
	 */
	private static void closeLdapDirContext(DirContext dirContext)
			throws GeneralException {
		// 如果参数为NULL
		if (dirContext == null) {
			String[] args = { "dirContext" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter conn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		try {
			// 关闭
			dirContext.close();
		} catch (NamingException ex) {
			// 关闭不成功，再次关闭
			if (log.isDebugEnabled()) {
				log.debug("Not close DirContext " + ex);
			}
			// 记录日志
			log.error("Not close DirContext " + ex);
			ex.printStackTrace();
			try {
				// 再次关闭
				dirContext.close();
			} catch (NamingException ex1) {
				// 再次关闭失败
				if (log.isDebugEnabled()) {
					log.debug("Not again close DirContext " + ex);
				}
				// 记录日志
				log.error("Not again close DirContext " + ex);
				ex.printStackTrace();
				// 抛出异常
				throw GeneralExceptionHandler.Handle(new GeneralException(
						"Not again close DirContext " + ex));
			}
		}
	}

	/**
	 * 构造函数私有，防止实例化
	 */
	private LdapOperUtils() {
	}

	/**
	 * 在当前的Context 添加一个子Context
	 * 
	 * @param context
	 *            连接DirContext
	 * @param cn
	 *            创建的子Context
	 * @param attMap
	 *            Context 的属性，Map 包含 List ,key = 属性名,
	 *            当属性值为多值时，为list,为单值时，为String
	 * @throws NamingException
	 * @throws BaseException
	 */
	public static void addContext(DirContext context, String cn, Map attMap)
			throws NamingException, GeneralException {

		// 参数为空
		if (context == null) {
			String[] args = { "context" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter context NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (StringUtils.isEmpty(cn)) {
			String[] args = { "cn" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter cn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (attMap == null) {
			String[] args = { "attMap" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter attMap NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 为空，则退出
		if (attMap.isEmpty()) {
			return;
		}

		// 取所有的属性key
		Set keySet = attMap.keySet();
		Iterator keyIterator = keySet.iterator();
		Attributes attrs = new BasicAttributes();
		// 迭代所有的属性key
		while (keyIterator.hasNext()) {

			// 取下一个属性
			String key = (String) keyIterator.next();
			Attribute att = null;
			Object valueObj = attMap.get(key);
			// 判断属性类型
			if (valueObj instanceof String) {
				// 为字符串，为单值属性
				att = new BasicAttribute(key, valueObj);
			} else if (valueObj instanceof List) {
				// 为List ,为多值属性
				att = new BasicAttribute(key);
				List valueList = (List) valueObj;
				// 加入多值属性
				for (int i = 0; i < valueList.size(); i++) {
					att.add(valueList.get(i));
				}
			} else {
				// 其它类型，都加入，如字节类型 （密码）
				att = new BasicAttribute(key, valueObj);
			}
			// 加入
			attrs.put(att);
		}
		// 创建子Context
		context.createSubcontext(cn, attrs);
		// context.close();
	}

	/**
	 * 在当前的Context 删除一个子Context
	 * 
	 * @param context
	 *            连接的DirContext
	 * @param cn
	 *            要删除的Context的名称
	 * @throws NamingException
	 * @throws BaseException
	 */
	public static void deleteContext(DirContext context, String cn)
			throws NamingException, GeneralException {

		// 参数为空
		if (context == null) {
			String[] args = { "context" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter context NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (StringUtils.isEmpty(cn)) {
			String[] args = { "cn" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter cn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 删除一个子Context
		context.destroySubcontext(cn);
		// context.close();

	}

	/**
	 * 根据当前的连接DirContext 重命名Context
	 * 
	 * @param context
	 *            连接后的DirContext
	 * @param cn
	 *            原Context的名称
	 * @param newCn
	 *            新的Context名称
	 * @throws NamingException
	 * @throws BaseException
	 */
	public static void reNameContext(DirContext context, String cn, String newCn)
			throws NamingException, GeneralException {

		// 参数为空
		if (context == null) {
			String[] args = { "context" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter context NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (StringUtils.isEmpty(cn)) {
			String[] args = { "cn" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter cn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (context == null) {
			String[] args = { "context" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter context NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (StringUtils.isEmpty(newCn)) {
			String[] args = { "newCn" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter newCn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}
		context.rename(cn, newCn);
		// context.close();
	}

	/**
	 * 在当前连接的DirContext 指定的Context 添加一个 / 多个属性
	 * 
	 * @param context
	 *            连接的DirContext
	 * @param cn
	 *            指定的Context
	 * @param attMap
	 *            Map 包含 List ,key为属性名称， value 属性值, 当为多值时，存为List,当为单值时，为String类型
	 * @throws BaseException
	 * @throws NamingException
	 */
	public static void addAttributes(DirContext context, String cn, Map attMap)
			throws GeneralException, NamingException {

		// 参数为空
		if (context == null) {
			String[] args = { "context" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter context NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (attMap == null) {
			String[] args = { "attMap" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter attMap NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}
		// 参数为空
		if (StringUtils.isEmpty(cn)) {
			String[] args = { "cn" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter cn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 为空，退出
		if (attMap.isEmpty()) {
			return;
		}

		// 取所有的属性key
		Set keySet = attMap.keySet();
		Iterator keyIterator = keySet.iterator();
		Attributes attrs = new BasicAttributes();
		// 迭代所有的属性key
		while (keyIterator.hasNext()) {

			// 取下一个属性
			String key = (String) keyIterator.next();
			Attribute att = null;
			Object valueObj = attMap.get(key);
			// 判断属性类型
			if (valueObj instanceof String) {
				// 为字符串，为单值属性
				att = new BasicAttribute(key, valueObj);
			} else if (valueObj instanceof List) {
				// 为List ,为多值属性
				att = new BasicAttribute(key);
				List valueList = (List) valueObj;
				// 加入多值属性
				for (int i = 0; i < valueList.size(); i++) {
					att.add(valueList.get(i));
				}
			}else {
				att = new BasicAttribute(key, valueObj);
			}
			// 加入
			attrs.put(att);
		}

		context.modifyAttributes(cn, DirContext.ADD_ATTRIBUTE, attrs);
		// context.close();
	}

	/**
	 * 在当前的连接DirContext 删除 指定Context 下的 一个 / 多个属性
	 * 
	 * @param context
	 *            连接后的DirContext
	 * @param cn
	 *            指定Context的名称
	 * @param attList
	 *            包含要删除的属性的名称,为List类型
	 * @throws BaseException
	 * @throws NamingException
	 */
	public static void deleteAttributes(DirContext context, String cn,
			List attList) throws GeneralException, NamingException {
		// 参数为空
		if (context == null) {
			String[] args = { "context" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter context NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (attList == null) {
			String[] args = { "attList" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter attList NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}
		// 参数为空
		if (StringUtils.isEmpty(cn)) {
			String[] args = { "cn" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter cn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 为空，退出
		if (attList.isEmpty()) {
			return;
		}

		Attributes attrs = new BasicAttributes();

		for (int i = 0; i < attList.size(); i++) {
			Attribute att = null;
			att = new BasicAttribute((String) attList.get(i));
			// 加入
			attrs.put(att);
		}
		context.modifyAttributes(cn, DirContext.REMOVE_ATTRIBUTE, attrs);
		// context.close();
	}

	/**
	 * 在当前连接的DirContext 修改指定Context下的一个 或 多个属性
	 * 
	 * @param context
	 *            连接的DirContext
	 * @param cn
	 *            指定Context下的名字
	 * @param attMap
	 *            包含List key为属性名称，当属性为多值时 value 为包含多值的List,为单值时，为包含单值的String类型
	 * @throws BaseException
	 * @throws NamingException
	 */
	public static void modifyAttributes(DirContext context, String cn,
			Map attMap) throws GeneralException, NamingException {

		// 参数为空
		if (context == null) {
			String[] args = { "context" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter context NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 参数为空
		if (attMap == null) {
			String[] args = { "attMap" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter attMap NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}
		// 参数为空
		if (StringUtils.isEmpty(cn)) {
			String[] args = { "cn" };
			// 打印错误日志
			StringBuffer msglog = new StringBuffer(
					"empty invoke parameter cn NULL ");
			log.error(msglog.toString());
			throw GeneralExceptionHandler.Handle(new GeneralException(msglog
					.toString()));
		}

		// 为空，退出
		if (attMap.isEmpty()) {
			return;
		}
		// 取所有的属性key
		Set keySet = attMap.keySet();
		Iterator keyIterator = keySet.iterator();
		Attributes attrs = new BasicAttributes();
		// 迭代所有的属性key
		while (keyIterator.hasNext()) {
			// 取下一个属笥
			String key = (String) keyIterator.next();
			Attribute att = null;
			Object valueObj = attMap.get(key);

			if (valueObj instanceof List) {
				// 为List ,为多值属性
				att = new BasicAttribute(key);
				List valueList = (List) valueObj;
				// 加入多值属性
				for (int i = 0; i < valueList.size(); i++) {
					att.add(valueList.get(i));
				}
			} else if (valueObj instanceof String) {
				att = new BasicAttribute(key, valueObj);
			} else {
				att = new BasicAttribute(key, valueObj);
			}
			// 加入
			attrs.put(att);
		}
		context.modifyAttributes(cn, DirContext.REPLACE_ATTRIBUTE, attrs);
		// context.close();
	}

	// 
	/**
	 * 获取连接的DirContext中指定Context下的指定属性
	 * 
	 * @param context
	 *            连接的DirContext
	 * @param cn
	 *            指定Context的名称
	 * @param attNameList
	 *            要取的属性的名称List
	 * @return Map包含List ,key 为属性的名称,当属性值为多值时，Value为List类型， 否则，value 为String 类型
	 * @throws NamingException
	 */
	public static Map getAttributes(DirContext context, String cn,
			List attNameList) throws NamingException {
		Map attsMap = new HashMap();
		Attributes results = null;
		List attValList = null;
		String attrId = null;

		if (attNameList == null) {
			results = context.getAttributes(cn);
		} else {
			if (!attNameList.isEmpty()) {
				// results = context.getAttributes(cn);
				String[] stTemp = new String[attNameList.size()];
				// ///////////////////////////////////////// 以下方法性能太低
				// ////////////////////////////////
				// for (int i = 0; i < attNameList.size(); i++) {
				// stTemp[i] = (String) attNameList.get(i);
				// }
				// results = context.getAttributes(cn,
				// stTemp);
				// /////////////////////////////////////////////////////////////////////////////////////////
				// 比较高性能的List 转为 数组的方法
				results = context.getAttributes(cn, (String[]) (attNameList
						.toArray(stTemp)));
			}
		}
		for (int i = 0; i < attNameList.size(); i++) {
			Attribute attr = results.get((String) attNameList.get(i));
			attrId = (String) attNameList.get(i);
			if (attr != null) {
				if (attr.size() > 0) {
					NamingEnumeration vals = attr.getAll();
					if (vals == null) {
						continue;
					}
					Object obj1 = vals.nextElement();
					if (obj1 == null) {
						continue;
					}
					// 迭代这个属性的所有属性值
					while (vals.hasMoreElements()) {
						if (attValList == null) {
							attValList = new ArrayList();
							attValList.add(obj1);
						}
						attValList.add(vals.nextElement());
					}
					// 当属性为单值域时，存为字符串
					// 当属性为多值域时，存为包含多值域的List
					if (attValList != null) {
						attsMap.put(attrId, attValList);
						// 清空
						attValList = null;
					} else {
						attsMap.put(attrId, obj1);
					}
				}
			}
		}
		// context.close();
		return attsMap;
	}

	/**
	 * 在当前连接的DirContext 获取指定Context下的指定属性名称的所有属性值（一个或多个值）
	 * 
	 * @param context
	 *            连接的DirContext
	 * @param cn
	 *            指定Context的cn名
	 * @param attName
	 *            属性名称
	 * @return 返回包括属性值的List 注意，当属性只有一个值时，返回的List长度为1，当属性 是多值属性时，返回List长度为属性值的数目
	 * @throws NamingException
	 */
	public static List getAttributeValues(DirContext context, String cn,
			String attName) throws NamingException {
		List attValList = new ArrayList();
		List attNameList = new ArrayList();
		attNameList.add(attName);
		Map attMap = null;
		attMap = getAttributes(context, cn, attNameList);

		if (attMap != null) {
			Object attValObj = attMap.get(attName);
			if (attValObj instanceof String) {
				attValList.add((String) attValObj);
			} else if (attValObj instanceof List) {
				attValList = ((List) attValObj);
			}
		}
		// context.close();
		return attValList;
	}

	/**
	 * 获取角色的相关信息
	 * 
	 * @param context
	 *            DirContext
	 * @param cn
	 *            String
	 * @param attName
	 *            String
	 * @return String
	 * @throws NamingException
	 */
	public static String getRoleAttributeValues(DirContext context, String cn,
			String attName) throws NamingException {
		String result = "";
		List attNameList = new ArrayList();
		attNameList.add(attName);
		Map attMap = null;
		attMap = getAttributes(context, cn, attNameList);

		if (attMap != null) {
			Object attValObj = attMap.get(attName);
			result = (String) attValObj;
		}
		return result;
	}

	/**
	 * 根据条件查找指定CN的Context下的一层所有属性
	 * 
	 * @param context
	 *            连接了的DirContext
	 * @param cn
	 *            要查询的BaseCN名称
	 * @param filter
	 *            要查询的过滤字符串
	 * @return 符合查询结果的List
	 * @throws NamingException
	 */
	public static List searchContextOne(DirContext context, String cn,
			String filter) throws NamingException {
		List resultList = new ArrayList();
		Map resultRowMap = null;
		List attValList = null;
		String attValStr = null;
		// 实例化一个搜索器
		SearchControls constraints = new SearchControls();
		// 设置搜索器的搜索范围
		constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		// 在基目录中搜索条件为Env.MY_FILTER的所有属性 注意：这里返回是的所有的条目集合
		NamingEnumeration results = context.search(cn, filter, constraints);

		// 打印条目的识别名(DN)及其所有的属性名，值
		while (results != null && results.hasMore()) {
			// 取一个条目
			SearchResult si = (SearchResult) results.next();

			// 获取条目的所有属性集合
			Attributes attrs = si.getAttributes();
			if (attrs != null) {
				String attrId = null;
				// 一行数据
				resultRowMap = new HashMap();
				// 打印所有属性
				for (NamingEnumeration ae = attrs.getAll(); ae
						.hasMoreElements();) {
					// 获取一个属性
					Attribute attr = (Attribute) ae.next();
					attrId = attr.getID();
					Enumeration vals = attr.getAll();
					if (vals == null) {
						continue;
					}
					Object obj1 = vals.nextElement();
					if (obj1 == null) {
						continue;
					}
					// 迭代这个属性的所有属性值
					while (vals.hasMoreElements()) {
						if (attValList == null) {
							attValList = new ArrayList();
							attValList.add(obj1);
						}
						attValList.add(vals.nextElement());
					}
					// 当属性为单值域时，存为字符串
					// 当属性为多值域时，存为包含多值域的List
					if (attValList != null) {
						resultRowMap.put(attrId, attValList);
						// 清空
						attValList = null;
					} else {
						resultRowMap.put(attrId, obj1);
					}

				}
			}
			resultList.add(resultRowMap);
		}
		return resultList;
	}

	/**
	 * 根所条件查找指定CN的Context下的子树下的所有属性
	 * 
	 * @param context
	 *            连接了的DirContext
	 * @param cn
	 *            要查询的BaseCN名称
	 * @param filter
	 *            要查询的过滤字符串
	 * @return 符合查询结果的List
	 * @throws NamingException
	 */
	public static List searchContextSub(DirContext context, String cn,
			String filter) throws NamingException {
		List resultList = new ArrayList();
		Map resultRowMap = null;
		List attValList = null;
		// 实例化一个搜索器
		SearchControls constraints = new SearchControls();
		// 设置搜索器的搜索范围
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// 在基目录中搜索条件为Env.MY_FILTER的所有属性 注意：这里返回是的所有的条目集合
		NamingEnumeration results = context.search(cn, filter, constraints);

		// 打印条目的识别名(DN)及其所有的属性名，值
		while (results != null && results.hasMore()) {
			// 取一个条目
			SearchResult si = (SearchResult) results.next();

			// 获取条目的所有属性集合
			Attributes attrs = si.getAttributes();
			if (attrs != null) {
				String attrId = null;
				// 一行数据
				resultRowMap = new HashMap();
				// 打印所有属性值
				for (NamingEnumeration ae = attrs.getAll(); ae
						.hasMoreElements();) {
					// 获取一个属性
					Attribute attr = (Attribute) ae.next();
					attrId = attr.getID();
					Enumeration vals = attr.getAll();
					if (vals == null) {
						continue;
					}
					Object obj1 = vals.nextElement();
					if (obj1 == null) {
						continue;
					}
					// 迭代这个属性的所有属性值
					while (vals.hasMoreElements()) {
						if (attValList == null) {
							attValList = new ArrayList();
							attValList.add(obj1);
						}
						attValList.add(vals.nextElement());
					}
					// 当属性为单值域时，存为字符串
					// 当属性为多值域时，存为包含多值域的List
					if (attValList != null) {
						resultRowMap.put(attrId, attValList);
						// 清空
						attValList = null;
					} else {
						resultRowMap.put(attrId, obj1);
					}
				}
			}
			resultList.add(resultRowMap);
		}
		return resultList;
	}

	/**
	 * 查找指定CN的Context下的子树下的指定属性
	 * 
	 * @param context
	 *            DirContext
	 * @param cn
	 *            String
	 * @param filter
	 *            String
	 * @param returnedAtts
	 *            String[] 属性名字数组
	 * @return List
	 * @throws NamingException
	 */
	public static List searchContextSub(DirContext context, String cn,
			String filter, String[] returnedAtts) throws NamingException {
		List resultList = new ArrayList();
		String attrId = null;
		List attValList = null;
		Map resultRowMap = null;
		// 实例化一个搜索器
		SearchControls constraints = new SearchControls();
		// 设置搜索器的搜索范围
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// String[] returnedAtts = {"uniquemember"};
		constraints.setReturningAttributes(returnedAtts);
		// 条目
		NamingEnumeration results = context.search(cn, filter, constraints);

		// 迭代所有的条目
		while (results != null && results.hasMore()) {
			// 取一个条目
			SearchResult si = (SearchResult) results.next();
			resultRowMap = new HashMap();
			// 获取条目的指定返回的属性
			Attributes attrs = si.getAttributes();
			if (attrs != null) {
				// 迭代所有属性值
				for (NamingEnumeration ae = attrs.getAll(); ae
						.hasMoreElements();) {

					// 获取一个属性
					Attribute attr = (Attribute) ae.next();
					attrId = attr.getID();
					Enumeration vals = attr.getAll();
					if (vals == null) {
						continue;
					}
					// 迭代这个属性的所有属性值
					while (vals.hasMoreElements()) {
						if (attValList == null) {
							attValList = new ArrayList();
						}
						attValList.add(vals.nextElement());
					}
					// 当属性为单值域时，存为字符串
					// 当属性为多值域时，存为包含多值域的List
					if (attValList != null) {
						resultRowMap.put(attrId, attValList);
						// 清空
						attValList = null;
					}
				}
			}
			resultList.add(resultRowMap);
		}
		return resultList;
	}

	/**
	 * 查找指定CN的Context下的一层指定属性
	 * 
	 * @param context
	 *            DirContext
	 * @param cn
	 *            String
	 * @param filter
	 *            String
	 * @param returnedAtts
	 *            String[] 属性名字数组
	 * @return List
	 * @throws NamingException
	 */
	public static List searchContextOne(DirContext context, String cn,
			String filter, String[] returnedAtts) throws NamingException {
		List resultList = new ArrayList();
		String attrId = null;
		List attValList = null;
		Map resultRowMap = null;
		// 实例化一个搜索器
		SearchControls constraints = new SearchControls();
		// 设置搜索器的搜索范围
		constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		// String[] returnedAtts = {"uniquemember"};
		constraints.setReturningAttributes(returnedAtts);
		// 条目
		NamingEnumeration results = context.search(cn, filter, constraints);

		// 迭代所有的条目
		while (results != null && results.hasMore()) {
			// 取一个条目
			SearchResult si = (SearchResult) results.next();
			resultRowMap = new HashMap();
			// 获取条目的指定返回的属性
			Attributes attrs = si.getAttributes();
			if (attrs != null) {
				// 迭代所有属性值
				for (NamingEnumeration ae = attrs.getAll(); ae
						.hasMoreElements();) {

					// 获取一个属性
					Attribute attr = (Attribute) ae.next();
					attrId = attr.getID();
					Enumeration vals = attr.getAll();
					if (vals == null) {
						continue;
					}
					Object obj1 = vals.nextElement();
					if (obj1 == null) {
						continue;
					}
					// 迭代这个属性的所有属性值
					while (vals.hasMoreElements()) {
						if (attValList == null) {
							attValList = new ArrayList();
							attValList.add(obj1);
						}
						attValList.add(vals.nextElement());
					}
					// 当属性为单值域时，存为字符串
					// 当属性为多值域时，存为包含多值域的List
					if (attValList != null) {
						resultRowMap.put(attrId, attValList);
						// 清空
						attValList = null;
					} else {
						resultRowMap.put(attrId, obj1);
					}
				}
			}
			resultList.add(resultRowMap);
		}
		return resultList;
	}

	/**
	 * 在当前的连接DirContext 删除 指定Context 下的 一个属性里面包含的子属性
	 * 
	 * @param context
	 *            连接后的DirContext
	 * @param cn
	 *            指定Context的名称
	 * @param attList
	 *            包含要删除的属性的名称
	 * @throws BaseException
	 * @throws NamingException
	 */
	public static void deleteInAttributes(DirContext ctx, String userDN,
			List attList, String flag) throws NamingException {
		if (attList == null || attList.size() == 0) {
			return;
		} else {
			int size = attList.size();
			ModificationItem[] mods = new ModificationItem[size];
			for (int i = 0; i < size; i++) {
				Attribute att = null;
				mods[i] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
						new BasicAttribute(flag, (String) attList.get(i)));
			}
			ctx.modifyAttributes(userDN, mods);
		}
	}
	
	
	public static List getName(DirContext ctx, String name,String filter) throws NamingException {
		ArrayList list = new ArrayList();
		// String account = (String) mapSet.get("account");
		SearchControls constraints = new SearchControls();
		if(name == null){
			name = "";
		}
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置范围
		NamingEnumeration ne = ctx.search(name, filter, constraints);
		while (ne != null && ne.hasMoreElements()) {
			Object obj = ne.nextElement();
			if (obj instanceof SearchResult) {
				SearchResult is = (SearchResult) obj;
				list.add(is.getName());
			}
		}
		return list;
	}
	
	/**
	 * 获取单一DN
	 * @param ctx
	 * @param name
	 * @param filter
	 * @return
	 * @throws NamingException
	 */
	public static String getSingleName(DirContext ctx, String name,String filter) throws NamingException {
		ArrayList list = new ArrayList();
		// String account = (String) mapSet.get("account");
		SearchControls constraints = new SearchControls();
		if(name == null){
			name = "";
		}
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置范围
		NamingEnumeration ne = ctx.search(name, filter, constraints);
		while (ne != null && ne.hasMoreElements()) {
			Object obj = ne.nextElement();
			if (obj instanceof SearchResult) {
				SearchResult is = (SearchResult) obj;
				list.add(is.getName());
			}
		}
		
		if (list.size() > 0) {
			return list.get(0).toString();
		} else {
			return "";
		}
	}
	
	/**
	 * 是否存在相应过滤条件的条目
	 * @param ctx
	 * @param filter
	 * @return
	 * @throws NamingException
	 */
	public static boolean isExist(DirContext ctx, String filter) throws NamingException {
		
		boolean flag = false;
		try {
			List list = getName(ctx, "",filter);
			
			if (list.size() > 0) {
				flag = true;
			} 
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return flag;
	}
	
	public static void main(String []args) {
		// LDAP的IP地址
		String ipAddress = "192.168.1.3";
		// 端口
		String port = "636";
		// LDAP服务器管理员账户名称
		String username = "Administrator@sanjiaolong.local";
		// LDAP服务器管理员密码
		String password = "1234";
		// 根节点
		String root = "DC=sanjiaolong,DC=local";
		// 证书根节点
		String certficationPath = "C:/Java/jdk1.6.0_23/jre/lib/security/cacerts";
		// 证书导入密码
		String certficationPwd = "changeit";
		
		String isModifypwd = "1";
		Env env = null;
		
		// Ldap工厂类
		String factory = "com.sun.jndi.ldap.LdapCtxFactory";
		// ldap连接
		String url = "ldap://" + ipAddress + ":" + port + "/" + root;
		
		//ConnFactory factory = new ConnFactory(ipAddress, port, username, password, root, certficationPath, certficationPwd);
		
		// 根据是否修改密码创建不同的ldap连接，1表示修改密码，0表示不修改密码
		if ("1".equals(isModifypwd)) {
			env = new Env(factory, url, username, password, certficationPath, true, certficationPwd);
			
		} else {
			env = new Env(factory, url, username, password);
		}
		
		DirContext adConn = null;
		try {
			adConn = LdapOperUtils.getLdapDirContext(env);
			
//			String cn = root;
//			Map map = new HashMap();
//			map.put("cn", "张三5");
//			map.put("sn", "张");
//			map.put("objectClass", "user");
//			map.put("description", "描述");
//			map.put("telephoneNumber", "01062210089");
//			map.put("givenName", "张三");
//			map.put("initials", "zs");
//			map.put("displayName", "张三");
//			map.put("name", "张三");
//			map.put("sAMAccountName", "zhangs5");
//			map.put("userPrincipalName", "zhangs2@sanjiaolong.local");
//			map.put("mail", "zhangs@hjsoft.com.cn");
//			// 用户控制，
//			map.put("userAccountControl", "66048");
//			
//			List list = getName(adConn, "" , "ou=中烟");
//			System.out.println(list.get(0).toString());
//			addContext(adConn, "CN=张三5," + list.get(0).toString(), map);
//			BasicAttributes attrs = new BasicAttributes();
//			attrs.put("userAccountControl", "66048");// 启用：512，禁用：514，// 密码永不过期：66048
//			adConn.modifyAttributes("CN=张三5," + list.get(0).toString(), DirContext.REPLACE_ATTRIBUTE, attrs);
			
			BasicAttributes attrs = new BasicAttributes();
			attrs.put("ou", "某集团公司");
			
			adConn.modifyAttributes("ou=某集团公司" , DirContext.REPLACE_ATTRIBUTE, attrs);
			System.out.println(adConn.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (adConn != null) {
				try {
					closeEnvLdapDirContext(adConn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
