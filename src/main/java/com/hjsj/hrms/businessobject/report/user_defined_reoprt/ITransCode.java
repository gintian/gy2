/**
 * 
 */
package com.hjsj.hrms.businessobject.report.user_defined_reoprt;

import java.util.Map;

/**
 * 自定义转码参数接口
 * <p>
 * Title:ITransCode.java
 * </p>
 * <p>
 * Description>:自定义表中，所有的需要自定义转码的类都必须实现此接口
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2013-05-21 10:46:32
 * </p>
 * <p>
 * 
 * @version: 1.0
 *           </p>
 *           <p>
 * @author: wangzhongjun
 */
public interface ITransCode {
	/**
	 * 转码
	 * @param map 所有自定义表中参数的集合
	 * @param targetKey 需要转码的参数的名称
	 * @return String 转码后的值
	 */
	public String transCode(Map map, String targetKey);
}
