package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.LoadDynamicParametersBo;
import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 加载动态参数交易类30200710242
 * <p>Title:LoadDynamicParametersTrans.java</p>
 * <p>Description>:LoadDynamicParametersTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 28, 2009 9:15:59 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class LoadDynamicParametersTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			LoadDynamicParametersBo ldpb = new LoadDynamicParametersBo(this.getFrameconn());
			ldpb.reloadAllParam();
			
			//发送集群操作处理加载动态参数  wangb 20170626
			SyncSystemUtilBo.sendSyncCmd(SyncSystemUtilBo.SYNC_TYPE_RELOAD_PARAM);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
