package com.hjsj.hrms.module.system.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.SyncDataUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;

public class SyncDataThread extends Thread{
	
	@Override
    public void run() {
//		synchronized(this) {
			Connection conn =null;
			SyncDataUtil.setIS_SYNCING_FLAG(true);
			try{
				RecordVo recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
				conn = AdminDb.getConnection();
				//操作类型，加载云平台参数
				if (recordVo != null) {
					String str = recordVo.getString("str_value");
					if(str==null||"".equals(str)){
						return;
					}
					JSONObject json = JSONObject.fromObject(str);
					if(json.get("appId") == null||json.get("tenantId") == null||json.get("appSecret") == null||json.get("tables") == null){
						return ;
					}
					String appId = (String) json.get("appId");
					String tenantId = (String) json.get("tenantId");
					String appSecret = (String) json.get("appSecret");
					JSONArray tables = json.getJSONArray("tables");
					
					//新增机构到云
					SyncDataUtil.syncOrgtoCloud(conn,appId,tenantId,appSecret,1,"t_org_view");
					
					//新增岗位到云
					SyncDataUtil.syncOrgtoCloud(conn,appId,tenantId,appSecret,1,"t_post_view");
					
					//同步人员
					SyncDataUtil.syncEmptoCloud(conn,appId,tenantId,appSecret,tables);
					
					//删除岗位到云
					SyncDataUtil.syncOrgtoCloud(conn,appId,tenantId,appSecret,2,"t_post_view");
					
					//删除机构到云
					SyncDataUtil.syncOrgtoCloud(conn,appId,tenantId,appSecret,2,"t_org_view");
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				SyncDataUtil.setIS_SYNCING_FLAG(false);
				PubFunc.closeDbObj(conn);
			}
//		}
	}
}
