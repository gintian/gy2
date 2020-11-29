package com.hjsj.hrms.transaction.general.sys.validate;

import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;

public interface Dtsms {

	/**作者：郭峰 作用：用大唐提供的第三方短信通道发送短信*/
	public int sendValidateMessage(String content,String phone,UserView userview,Connection conn);
	public String getErrorReason();
}
