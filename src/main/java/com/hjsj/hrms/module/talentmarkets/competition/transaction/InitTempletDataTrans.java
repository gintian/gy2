package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 发布申请前获取数据交易类
 * @Author wangz
 * @Date 2019/8/9 11:52
 * @Version V1.0
 **/
public class InitTempletDataTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map<String,Object> returnData = new HashMap<String, Object>();
        String returnCode = "success";
        String returnMsgCode = "";
        List recorsLsit = (List)this.getFormHM().get("records");
        String templateType = (String) this.getFormHM().get("templateType");
        try{
            String tabId = TalentMarketsUtils.initTempTemplateTable(templateType,this.frameconn,this.userView,recorsLsit);
            returnData.put("tabid",tabId);
        }catch (GeneralException ex){
            returnCode = "fail";
            String msg = ex.getErrorDescription();
            returnMsgCode = msg;

        }
        this.getFormHM().put("return_data",returnData);
        this.getFormHM().put("return_code",returnCode);
        this.getFormHM().put("return_msg_code",returnMsgCode);

    }
}
