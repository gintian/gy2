package com.hjsj.hrms.module.gz.analyse.historydata.transaction;

import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.SalaryHistoryDataService;
import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.impl.SalaryHistoryDataServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title SearchSalaryTemplateTrans
 * @Description 获取薪资类别window数据
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public class SearchSalaryTemplateTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map<String, Object> returnStr = new HashMap<String, Object>();
        Map<String, Object> returnData = new HashMap<String, Object>();
        try {
            SalaryHistoryDataService sds = new SalaryHistoryDataServiceImpl(this.frameconn, this.userView);
            String type = (String) this.getFormHM().get("type");
            List<String> valuesList = new ArrayList<String>();
            if (StringUtils.isNotBlank(type)) {
                //查询组件返回条件集合
                valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
            }
            String tableConfig = sds.getHistoryTemplateConfig(type, valuesList);
            returnData.put("getTableConfig", tableConfig);
            returnStr.put("return_code", "success");
            returnStr.put("return_data", returnData);
            this.formHM.put("returnStr", returnStr);
        } catch (GeneralException e) {
            e.printStackTrace();
            returnStr.put("return_code", "fail");
            returnStr.put("return_msg", e.getErrorDescription());
            this.formHM.put("returnStr", returnStr);
        }
    }
}
