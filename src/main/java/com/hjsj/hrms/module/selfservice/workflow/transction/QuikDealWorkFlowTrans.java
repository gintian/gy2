package com.hjsj.hrms.module.selfservice.workflow.transction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hjsj.hrms.module.selfservice.taskcenter.businessobject.ITaskCenterService;
import com.hjsj.hrms.module.selfservice.taskcenter.businessobject.impl.ITaskCenterServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 待办快速处理交易类
 * @Author wangz
 * @Date 2020/6/2 11:05
 * @Version V1.0
 **/
public class QuikDealWorkFlowTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        List taskList = (List) this.getFormHM().get("taskList");
        String returnCode = "success";
        String return_msg = "";
        //agree 同意  reject 退回
        try {
            String errorInfo = "";
            String operateType = (String) this.getFormHM().get("operateType");
            ITaskCenterService iTaskCenterService = new ITaskCenterServiceImpl(this.frameconn, this.userView);
            if(StringUtils.equalsIgnoreCase("agree",operateType)||StringUtils.equalsIgnoreCase("reject",operateType)){
                errorInfo = iTaskCenterService.dealTask(taskList, operateType);
            }else if(StringUtils.equalsIgnoreCase("undo",operateType)){
                TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn,this.userView);
                JSONObject paramObject = (JSONObject) taskList.get(0);
                String tab_id=(String)paramObject.get("tabid");
                String task_id=(String)paramObject.get("taskid");
                String infor_type=(String)paramObject.get("infor_type");  //=1时代表人员 =2时代表单位 =3时代表职位

                String isDelMsg=(String)paramObject.get("isDelMsg"); //是否删除消息表中的记录
                String dataStr=(String)paramObject.get("dataStr"); //是否删除消息表中的记录
                HashMap paramMap = new HashMap();
                paramMap.put("tab_id",tab_id);
                paramMap.put("task_id",task_id);
                paramMap.put("infor_type",infor_type);
                paramMap.put("isDelMsg",isDelMsg);
                paramMap.put("dataStr",dataStr);
                Map returnData = templateSelfServicePlatformBo.delTemplate(paramMap);
                errorInfo = (String) returnData.get("return_msg");
            }
            if (StringUtils.isNotEmpty(errorInfo) && !StringUtils.equalsIgnoreCase(errorInfo, "success")) {
                returnCode = "error";
                return_msg = errorInfo;
            }
        }catch (Exception e){
            returnCode = "error";
            e.printStackTrace();
        }
        this.getFormHM().put("return_msg", return_msg);
        this.getFormHM().put("return_code", returnCode);
    }
}
