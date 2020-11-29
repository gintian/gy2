package com.hjsj.hrms.module.selfservice.workflow.transction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempSyncDataFromArchiveTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map return_data = new HashMap();
        String return_code = "success";
        //flag = 2 刷新子集数据 = 1导入子集数据
        String flag = (String) this.getFormHM().get("flag");
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn, this.userView);
        try {
            if (StringUtils.equalsIgnoreCase(flag, "2")) {
                HashMap paramMap = new HashMap<>();
                String tabId = (String) this.getFormHM().get("tabId");
                String infor_type = (String) this.getFormHM().get("infor_type");
                String objectId = (String) this.getFormHM().get("objectId");
                String moduleId = (String) this.getFormHM().get("moduleId");
                String columnName = (String) this.getFormHM().get("columnName");
                String taskId = (String) this.getFormHM().get("taskId");
                int columnindex = columnName.lastIndexOf("_");
                String chgstate = columnName.substring(columnindex + 1);
                if ("2".equals(chgstate)) {
                    JSONObject data = (JSONObject) this.getFormHM().get("data");
                    HashMap dataMap = (HashMap) JSONObject.toBean(data,HashMap.class);
                    HashMap dataClone = (HashMap) dataMap.clone();
                    List dataList = new ArrayList();
                    dataList.add(dataClone);
                    dataMap.put("savedata", dataList);
                    templateSelfServicePlatformBo.saveTempInfo(dataMap);
                }
                boolean isSysData = false;
                paramMap.put("tabId", tabId);
                paramMap.put("infor_type", infor_type);
                paramMap.put("objectId", objectId);
                paramMap.put("moduleId", moduleId);
                paramMap.put("isSysData", isSysData);
                paramMap.put("columnName", columnName);
                paramMap.put("taskId", taskId);
                return_data = templateSelfServicePlatformBo.syncSubsetInfo(paramMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return_code = "error";
        }
        this.getFormHM().put("return_data", return_data);
        this.getFormHM().put("return_code", return_code);
    }
}
