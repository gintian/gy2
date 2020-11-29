package com.hjsj.hrms.utils.sendmessage.email;

import org.apache.commons.beanutils.LazyDynaBean;

/**
 * 异步发送邮件时，更新发送状态接口。
 * 实现此接口，将实例化对象在AsyncEmailBo构造方法中传入。
 * @author zhanghua 2016-08-08 
 * @version v75
 */
public interface IAsyncEmailIsSuccessIF {
	/**
	 * 更新发送状态发放，可在此方法中独立获取数据库连接，进行数据更新操作。
	 * @param emailContent  调用AsyncEmailBo.java send方法时传入的bean
	 * @param isSuccess 若成功返回值为""，若失败，返回值为错误信息。
	 */
	void sendEmailIsSuccess(LazyDynaBean emailContent, String isSuccess);
}
