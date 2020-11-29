/**
 * 
 */
package com.hjsj.hrms.businessobject.report.user_defined_reoprt;

/**
 * javacode参数接口
 * <p>
 * Title:IJavaCode.java
 * </p>
 * <p>
 * Description>:自定义表中，所有的javacode参数类必须实现IJavaCode接口
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2011-02-21 09:43:32
 * </p>
 * <p>
 * 
 * @version: 1.0
 *           </p>
 *           <p>
 * @author: wangzhongjun
 */

public interface IJavaCode {
	/**
	 * 获得值
	 * @return String 多个值用逗号隔开
	 */
	public String getValue(String param);
}
