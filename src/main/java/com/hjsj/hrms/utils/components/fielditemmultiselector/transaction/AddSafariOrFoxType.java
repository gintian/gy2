package com.hjsj.hrms.utils.components.fielditemmultiselector.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
/***
 * 
 * 文件上传下载组件 火狐和Safari 浏览器 上传文件 session 为空 
 * changxy 20170323
 * */
public class AddSafariOrFoxType extends IBusiness{
	public void execute() throws GeneralException {
		if(this.userView!=null){
			 this.getFormHM().put("safariORFiox",PubFunc.encrypt("true"));
			 this.getFormHM().put("datems",new Date().getTime()+"");
		}
	}
}

