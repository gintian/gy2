package com.hjsj.hrms.businessobject.sys;

import org.apache.commons.beanutils.LazyDynaBean;

/**
 * <p>Title:IAcceptSMS</p>
 * <p>Description:接收短信接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-05-25</p>
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public interface IAcceptSMS {
	/**
	 * 接收短信的业务类继承IAcceptSMS类并实现acceptSMS方法，即可实时收短信
	 * @param ben LazyDynaBean 保存了(sender、acceptor、text、datetime)四个值
	 */
	public void acceptSMS(LazyDynaBean ben);
}
