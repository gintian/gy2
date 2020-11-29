package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title GetOrgDescTrans
 * @Description 获取组织描述
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/9
 * @Version 1.0.0
 */
public class GetOrgDescTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try{
            StringBuffer orgDesc = new StringBuffer();
            Map returnData = new HashMap();
            //以逗号分割的组织机构id
            String ordIds = (String) this.getFormHM().get("ordIds");
            String[] itemIdArray = ordIds.split(",");
            for (String itemId : itemIdArray) {
                if(StringUtils.isNotBlank(itemId)){
                    String itemDesc = AdminCode.getCodeName("UN", itemId);
                    if(StringUtils.isBlank(itemDesc)){
                        itemDesc = AdminCode.getCodeName("UM", itemId);
                    }
                    orgDesc.append(itemDesc);
                    orgDesc.append(",");
                }
            }
            if (orgDesc.length() > 0) {
                orgDesc.setLength(orgDesc.length() - 1);
            }
            returnData.put("orgDesc", orgDesc.toString());
            this.formHM.put("return_code", "success");
            this.formHM.put("return_data", returnData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
