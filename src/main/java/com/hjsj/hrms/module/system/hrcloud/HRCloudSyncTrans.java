package com.hjsj.hrms.module.system.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.SyncDataUtil;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class HRCloudSyncTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		if(!SyncDataUtil.getORG_PARENTGUID_NOT_NULL()) {
			this.formHM.put("flag", "false");
			this.formHM.put("errmessage", "单位视图中没有parentGUIDKEY字段，请初始化单位视图数据");
			return;
		}
		if(!SyncDataUtil.getPOST_PARENTGUID_NOT_NULL()) {
			this.formHM.put("flag", "false");
			this.formHM.put("errmessage", "岗位视图中没有parentGUIDKEY字段，请初始化岗位视图数据");
			return;
		}
		if(!SyncDataUtil.getIS_SYNCING_FLAG()){
			SyncDataUtil syncDataUtil = new SyncDataUtil();
			SyncDataUtil.startSync();
			this.formHM.put("flag", "true");
			this.formHM.put("errmessage", "正在执行同步");
		}else{
			this.formHM.put("flag", "false");
			this.formHM.put("errmessage", "同步正在执行中，请稍后再试");
		}
	}

}
