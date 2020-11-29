package com.hjsj.hrms.module.serviceclient.serviceSetting;

import com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject.ServiceAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
@SuppressWarnings("serial")
public class ServiceAnalyseTrans extends IBusiness {
	private enum TransType{
		/**初始化*/
		init,
		/**修改终端数据*/
		updateClient,
		/**删除终端数据*/
		deleteClient,
		/**添加终端数据*/
		addClient,
		/**获取分析数据*/
		analyse
	}
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String transType = (String)this.formHM.get("transType");
		if(transType==null || transType.length()<1)
			return;
		ServiceAnalyseBo bo=new ServiceAnalyseBo(this.userView,this.getFrameconn());
		if(transType.equals(TransType.init.toString())){//初始化
		    Map<String,Object> param = bo.getInitData();
			List<Map<String, String>> clientList =  (List<Map<String, String>>) param.get("clientList");
			List<Map<String, String>> serviceList = (List<Map<String, String>>) param.get("serviceList");
			Map<String,Object> analyseData = (Map<String, Object>) param.get("analyseData");
			this.formHM.put("clientList", clientList);
			this.formHM.put("serviceList", serviceList);
			this.formHM.put("analyseData", analyseData);
		}else if(transType.equals(TransType.updateClient.toString())){//更新终端机
			int pageCount = (Integer) this.formHM.get("pageCount");
			int clientId = Integer.parseInt((String)this.formHM.get("clientId"));
			bo.updateClient(pageCount,clientId);
			
		}else if(transType.equals(TransType.deleteClient.toString())){//删除终端机
			int clientId = Integer.parseInt((String)this.formHM.get("clientId"));
			bo.deleteClient(clientId);
			
        } else if (transType.equals(TransType.addClient.toString())) {//增加终端机
            String name = (String) this.formHM.get("name");
            String ip = (String) this.formHM.get("ip_address");
            int clientId = 0;
            Map<String, String> checkMap = bo.check(name, ip);
            if (MapUtils.isEmpty(checkMap)) {
                clientId = bo.addClient(this.formHM);
                this.formHM.put("clientId", clientId);
            } else {
                this.formHM.put("checkMap", checkMap);
            }

        }else if(transType.equals(TransType.analyse.toString())){//获取分析数据
		    String printServices = (String) formHM.get("printServices");//服务id 如果是多个id 格式如下1,2,3
	        String dateType = (String) formHM.get("dateType");//所选日期  week  month  year 
	        String printClients = (String) formHM.get("printClients");//终端id  如果是多个id 格式如下1,2,3
	        Map<String,Object> analyseData = bo.getAnalyseData(printServices,dateType,printClients);
			this.formHM.put("analyseData", analyseData);
		}
	}

}
