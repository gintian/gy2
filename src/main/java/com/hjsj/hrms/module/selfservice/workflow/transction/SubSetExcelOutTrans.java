package com.hjsj.hrms.module.selfservice.workflow.transction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hjsj.hrms.module.template.templatesubset.businessobject.TemplateSubsetBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 人事异动子集导入导出交易类
 *
 * @author wangz
 */
public class SubSetExcelOutTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map returnData = new HashMap();
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn, this.userView);
        try {
            String tabId = (String) this.getFormHM().get("tabId");
            String flag = (String) this.getFormHM().get("flag");
            String columnName = (String) this.getFormHM().get("columnName");
            JSONArray subFieldArray = (JSONArray) this.getFormHM().get("columnsList");
            TemplateSubsetBo templateSubset = new TemplateSubsetBo(this.frameconn, this.userView, tabId, columnName);
            SubSetDomain subSetDomain = templateSubset.getSubDomain();
            String setName = subSetDomain.getSetName();
            subSetDomain.getSubFieldList();
            ArrayList subFieldList = (ArrayList) JSONArray.toCollection(subFieldArray);
            //flag = 1 导出子集模板  else 导入子集数据
            if ("1".equals(flag)) {
                HashMap param = new HashMap();
                param.put("fieldSet", setName);
                param.put("column", subFieldList);
                String fileId = templateSelfServicePlatformBo.outExcel(param);
                returnData.put("fileId", fileId);
            } else {
                String taskId = (String) this.getFormHM().get("taskId");
                String insId = (String) this.getFormHM().get("insId");
                String fileId = (String) this.getFormHM().get("fileId");
                String moduleId = (String) this.getFormHM().get("moduleId");
                String objectId = (String) this.getFormHM().get("objectId");
                JSONArray xmlData = (JSONArray) this.getFormHM().get("xmlData");
                ArrayList xmlDataList = (ArrayList) JSONArray.toCollection(xmlData);
                TemplateDataBo dataBo = new TemplateDataBo(this.frameconn,
                        this.userView, Integer.parseInt(tabId));
                String dataTabName = dataBo.getUtilBo().getTableName(moduleId,
                        Integer.valueOf(tabId), taskId);
                HashMap param = new HashMap();
                param.put("tabid", tabId);
                param.put("ins_id", insId);
                param.put("fieldSet", setName);
                param.put("xmlData", xmlDataList);
                param.put("fieldid", fileId);
                param.put("objectId", PubFunc.encrypt(objectId));
                param.put("column", subFieldList);
                param.put("table_name", PubFunc.encrypt(dataTabName));
                param.put("columnName", columnName);
                returnData = templateSelfServicePlatformBo.importExcelInfo(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return_code = "error";
            return_msg = e.getMessage();
        }
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_msg", return_msg);
        this.getFormHM().put("return_data", returnData);
    }
}
