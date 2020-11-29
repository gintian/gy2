/**
 * 
 */
package com.hjsj.hrms.interfaces.sys;

/**
 * @author cmq
 * Mar 4, 200910:31:04 AM
 */
public interface SmsProxy {
	/**
	 * 短信接口
	 * @param phone  用户手机号码
	 * @param msg    发送内容
	 * @return
	 */
	public boolean  sendMessage(String phone,String msg);


}
