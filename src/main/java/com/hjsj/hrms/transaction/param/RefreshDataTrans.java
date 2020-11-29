package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class RefreshDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			/**生成代码表,放到客户端*/
			String path=(String)this.getFormHM().get("path");

			if (PubFunc.isProcessing) {
				throw new GeneralException("业务正在执行,请稍后再试");
			}
			PubFunc.syncRefreshDataDirectory(PubFunc.keyWord_reback(path), frameconn);  //20140901  dengcan
			//发送集群操作处理刷新数据字典 wangb 20170626
			SyncSystemUtilBo.sendSyncCmd(SyncSystemUtilBo.SYNC_TYPE_RELOAD_DATADICTIONARY);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);  			
		}
	}

}
