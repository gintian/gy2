package com.hjsj.hrms.module.selfservice.workflow.transction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 保存 模板附件交易类
 */
public class saveAttachTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn, this.userView);
        String return_msg = "";
        String return_code = "success";
        String moduleId = (String) this.getFormHM().get("moduleId");
        //flag = 1 保存 =2 删除
        String flag = (String) this.getFormHM().get("flag");
        String fileid = (String) this.getFormHM().get("fileId");
        HashMap paramBean = new HashMap();
        try {
            if (StringUtils.equalsIgnoreCase(flag, "1")) {
                String ins_id = (String) this.getFormHM().get("insId");
                String tabid = (String) this.getFormHM().get("tabId");
                String infor_type = (String) this.getFormHM().get("infor_type");
                String object_id = (String) this.getFormHM().get("object_id");
                String attachmenttype = (String) this.getFormHM().get("attachmenttype");
                String filetype = (String) this.getFormHM().get("filetype");
                String taskId = (String) this.getFormHM().get("taskId");
                paramBean.put("ins_id", ins_id);
                //模板号
                paramBean.put("tabId", tabid);
                //module_id 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
                paramBean.put("moduleId", moduleId != null ? moduleId : "");
                //1是人员模板，2是单位模板，3是岗位模板
                paramBean.put("infor_type", infor_type);
                paramBean.put("taskId", taskId);
                //当前选中人员：Usr`0000001  可以为空
                paramBean.put("object_id", object_id);
                //VFS fileid
                paramBean.put("fileid", fileid);
                //附件类型 =0公共附件 =1 个人附件
                paramBean.put("attachmenttype", attachmenttype);
                //文件类型： 如奖励文件
                paramBean.put("filetype", filetype);
                templateSelfServicePlatformBo.saveAttachment(paramBean);
            } else if (StringUtils.equalsIgnoreCase(flag, "2")) {
                paramBean.put("moduleId", moduleId != null ? moduleId : "");
                paramBean.put("fileIds", fileid);
                templateSelfServicePlatformBo.deleteAttachment(paramBean);
            }

        } catch (Exception e) {
            return_code = "error";
            if (e instanceof GeneralException) {
                return_msg = ((GeneralException) e).getErrorDescription();
            } else {
                return_msg = e.getMessage();
            }
        }
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_msg", return_msg);

    }
}
