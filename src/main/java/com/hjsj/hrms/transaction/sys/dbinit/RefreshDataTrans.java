package com.hjsj.hrms.transaction.sys.dbinit;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:RefreshDataTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 24, 2008:3:47:55 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class RefreshDataTrans extends IBusiness {
	public void execute() throws GeneralException {
		/** 生成代码表,放到客户端 */
		String path = (String) this.getFormHM().get("path");

		if (PubFunc.isProcessing) {
			throw new GeneralException("业务正在执行,请稍后再试");
		}
		PubFunc.syncRefreshDataDirectory(path, frameconn);
		//发送集群操作处理刷新数据字典 wangb 20170626
		SyncSystemUtilBo.sendSyncCmd(SyncSystemUtilBo.SYNC_TYPE_RELOAD_DATADICTIONARY);
	}
}
