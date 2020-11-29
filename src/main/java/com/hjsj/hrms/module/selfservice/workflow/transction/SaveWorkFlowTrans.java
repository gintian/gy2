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

public class SaveWorkFlowTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        HashMap returnData = new HashMap();
        String return_code = "success";
        String return_msg = "";
        String return_signpage = "";
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn, this.userView);
        JSONObject dataObject = (JSONObject) this.getFormHM().get("data");
        HashMap data = (HashMap) JSONObject.toBean(dataObject, HashMap.class);
        try {
            HashMap dataClone = (HashMap) data.clone();
            List dataList = new ArrayList();
            dataList.add(dataClone);
            data.put("savedata", dataList);
            templateSelfServicePlatformBo.saveTempInfo(data);
            //重新获取模板数据 主要因为 自动计算指标
            String moduleId = (String)data.get("moduleId");
            String tabId = (String)data.get("tabId");
            String taskId = (String)data.get("taskId");
            String ins_id = (String)data.get("insId");
            HashMap paramMap = new HashMap();
            paramMap.put("tabId", tabId);
            paramMap.put("ins_id", ins_id);
            paramMap.put("taskId", taskId);
            paramMap.put("isEdit", "1");
            paramMap.put("moduleId", moduleId);
            String approveFlag = (String) this.getFormHM().get("approveFlag");
            String currentObjectId = (String) this.getFormHM().get("currentObjectId");
            if(StringUtils.isEmpty(approveFlag)){
                approveFlag = "0";
            }
            paramMap.put("approveFlag", approveFlag);
            paramMap.put("objectId", currentObjectId);
            // 0 需要重新加载数据  1 不需要
            paramMap.put("needForm", "1");
            Map tempalteInfo = templateSelfServicePlatformBo.getTemplateInfo(paramMap);
            String dataJson = (String) tempalteInfo.get("data");
            returnData.put("data",dataJson);
        } catch (Exception e) {
            e.printStackTrace();
            return_msg = e.getMessage();
            return_code = "error";
            return_signpage = (String) data.get("pageId");
        }
        this.getFormHM().put("return_msg", return_msg);
        this.getFormHM().put("return_signpage", return_signpage);
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_data", returnData);
    }
}
