package com.hjsj.hrms.interfaces.template;

/**
 * 用于第三方实现类继承，实现业务模板相关接口功能
 * @author luckstar
 *
 */
public interface IPendingTask {

	/**
	 * 业务模板报批
	 * @param jsonstr
	 * @return
	 */
	public String pendingTask(String jsonstr);
	
}
