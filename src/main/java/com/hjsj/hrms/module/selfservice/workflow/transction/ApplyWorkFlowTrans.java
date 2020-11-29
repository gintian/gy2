package com.hjsj.hrms.module.selfservice.workflow.transction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 获取业务表单数据
 * @Author wangz
 * @Date 2020/5/14 14:19
 * @Version V1.0pply
 **/
public class ApplyWorkFlowTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        int totalCount = 0;
        String objectId = (String) this.getFormHM().get("objectId");
        List tableList = new ArrayList();
        try {
            String tabid = (String) this.getFormHM().get("tabid");
            String ins_id = (String) this.getFormHM().get("ins_id");
            if (StringUtils.isEmpty(ins_id)) {
                ins_id = "0";
            }
            String pageId = (String) this.getFormHM().get("pageId");
            //loadtableData 为只获取列表数据
            String operateType = (String) this.getFormHM().get("operateType");
            String taskid = (String) this.getFormHM().get("task_id");
            if (StringUtils.isEmpty(taskid)) {
                taskid = "0";
            }
            String fromMessage = (String) this.getFormHM().get("fromMessage");
            int pageSize = 10;
            if (this.getFormHM().get("pageSize") != null) {
                pageSize = (Integer) this.getFormHM().get("pageSize");
            }
            int pageNum = 1;
            if (this.getFormHM().get("pageNum") != null) {
                pageNum = (Integer) this.getFormHM().get("pageNum");
            }

            //名称查询框列值
            String searchName = (String) this.getFormHM().get("searchName");
            if (StringUtils.isNotEmpty(taskid) && !StringUtils.equalsIgnoreCase("0", taskid)) {
                taskid = PubFunc.decrypt(taskid);
            }
            String module_id = "9";
            if (StringUtils.equalsIgnoreCase(fromMessage, "1")) {
                module_id = "1";
            }
            String approveFlag = (String) this.getFormHM().get("approveFlag");
            if(StringUtils.isEmpty(approveFlag)){
                approveFlag = "0";
            }
            String return_flag = (String) this.getFormHM().get("return_flag");
            //判断 模板类型
            TemplateDataBo dataBo = new TemplateDataBo(this.frameconn,
                    this.userView, Integer.parseInt(tabid));
            String templateType = "person";
            if (dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3) {
                templateType = "unit";
            }
            TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn,this.userView);
            this.getFormHM().put("templateType", templateType);
            this.getFormHM().put("infor_type", dataBo.getParamBo().getInfor_type()+"");
            HashMap paramMap = new HashMap();
            paramMap.put("tabId", tabid);
            paramMap.put("ins_id", ins_id);
            paramMap.put("taskId", taskid);
            paramMap.put("isEdit", "1");
            paramMap.put("moduleId", module_id);
            paramMap.put("approveFlag", approveFlag);
            // 0 需要重新加载数据  1 不需要
            paramMap.put("needForm", "0");
            //初始化模板数据
            templateSelfServicePlatformBo.autoSyscDataInfo(paramMap);
            //获取左侧列表数据
            Map tableData = templateSelfServicePlatformBo.getApplyTableData(tabid, ins_id, taskid, module_id, pageSize, pageNum, searchName,approveFlag,return_flag);
            totalCount = (int) tableData.get("totalCount");
            tableList = (List) tableData.get("tableList");
            //左侧列表翻页时  不请求表单数据
            if (!StringUtils.equalsIgnoreCase(operateType, "loadTableData")) {
                if(StringUtils.isEmpty(objectId)){
                    if (tableList.size() > 0) {
                        objectId = (String) ((Map) tableList.get(0)).get("objectid");
                    }
                }
                paramMap.put("objectId",objectId );
                paramMap.put("pageId",pageId );
                paramMap.put("return_flag",return_flag );
                return_data = templateSelfServicePlatformBo.getTemplateInfo(paramMap);
                if(StringUtils.equalsIgnoreCase((String) return_data.get("return_code"),"error")){
                    return_msg = (String) return_data.get("return_msg");
                    return_code = "error";
                }
                String state = "";
                //获取  任务状态
                if(!StringUtils.equalsIgnoreCase("0",taskid)){
                    RecordVo vo = new RecordVo("t_wf_task");
                    vo.setString("task_id",taskid);
                    ContentDAO dao = new ContentDAO(this.frameconn);
                    vo = dao.findByPrimaryKey(vo);
                    if(vo!=null){
                        state = vo.getString("state");
                    }
                }
                this.getFormHM().put("state",state);
            }
            //表单数据只有一个人时 默认选中当前记录
            if(tableList.size() == 1){
                templateSelfServicePlatformBo.filterFlag(paramMap);
            }
        }catch (Exception e){
            return_code = "error";
            if(e instanceof GeneralException){
                return_msg = ((GeneralException) e).getErrorDescription();
            }else{
                return_msg = e.getMessage();
            }
        }

        this.getFormHM().put("objectId",objectId);
        this.getFormHM().put("tableList", tableList);
        this.getFormHM().put("totalCount", totalCount);
        this.getFormHM().put("return_data", return_data);
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_msg", return_msg);
    }
}
