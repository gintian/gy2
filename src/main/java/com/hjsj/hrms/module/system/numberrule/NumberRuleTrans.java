/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           2020/5/20 下午3:07
 *  *
 *
 */

package com.hjsj.hrms.module.system.numberrule;

import com.hjsj.hrms.module.system.numberrule.utils.NumberGenTool;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.DEFAULT_SYSTEM_CODE;
import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.DEFAULT_SYSTEM_NAME;

public class NumberRuleTrans extends IBusiness {
    private Logger log = LoggerFactory.getLogger(NumberRuleTrans.class);

    @Override
    public void execute() throws GeneralException {
        String method = (String) this.formHM.get("method");
        NumberRuleBo bo = new NumberRuleBo();

        if ("init".equalsIgnoreCase(method)) {
            String tableConfig = bo.loadNumberRuleList(userView, this.frameconn);
            this.formHM.clear();
            this.formHM.put("numberRuleTable", tableConfig);

        } else if ("delete".equalsIgnoreCase(method)) {
            ArrayList ids = (ArrayList) this.getFormHM().get("deleteIds");
            String username = userView.getUserName();
            bo.deleteNumberRule(ids, username);

        } else if ("toRegister".equalsIgnoreCase(method)) {
            this.formHM.put("currentUsrName", userView.getUserFullName());
            this.formHM.put("currentUsrMobile", userView.getUserTelephone());

        } else if ("toApply".equalsIgnoreCase(method)) {

            this.formHM.put("currentUsrName", userView.getUserFullName());
            this.formHM.put("currentUsrMobile", userView.getUserTelephone());
            List<NumberRuleVo> systemList = bo.getSystemList();
            this.formHM.put("systemList", systemList);

        } else if ("add".equalsIgnoreCase(method)) {
            NumberRuleBean info = new NumberRuleBean();
            info.setId(NumberGenTool.getId());

            String applicant = String.valueOf(this.formHM.get("applicant"));
            String mobile = String.valueOf(this.formHM.get("mobile"));
            Integer count = Integer.valueOf(this.formHM.get("count") == null ? "1" : this.formHM.get("count").toString());
            String remark = String.valueOf(this.formHM.get("remark"));
            String systemName = String.valueOf(this.formHM.get("systemName"));
            String systemCode = String.valueOf(this.formHM.get("systemCode"));

            info.setApplicant(applicant);
            info.setMobile(mobile);
            info.setCount(count);
            info.setRemark(remark);
            info.setSystemName(StringUtils.isBlank(systemName) ? DEFAULT_SYSTEM_NAME : systemName.toUpperCase());
            info.setSystemCode(StringUtils.isBlank(systemCode) ? DEFAULT_SYSTEM_CODE : systemCode.toUpperCase());

            String username = userView.getUserName();
            String updateResult = bo.addNumberRule(info, username);
            if ("true".equalsIgnoreCase(updateResult)) {
                this.getFormHM().put("result", true);
            } else {
                this.getFormHM().put("result", false);
                this.getFormHM().put("desc", updateResult);
                log.warn("申请编号失败， userName:{}, param:{}, desc:{}", username, info.toString(), updateResult);
            }

        } else if ("downloadNumberList".equalsIgnoreCase(method)) {
            String id = (String) this.getFormHM().get("id");
            if (StringUtils.isBlank(id)) {
                this.getFormHM().put("result", false);
            } else {
                String fileNameBaseCode = bo.downloadNumberList(id);
                log.info("file name baseCode={}", fileNameBaseCode);
                if (StringUtils.isBlank(fileNameBaseCode)) {
                    this.getFormHM().put("result", false);
                } else {
                    this.getFormHM().put("result", true);
                    this.getFormHM().put("fileName", fileNameBaseCode);
                }
            }

        } else if ("registerSystem".equalsIgnoreCase(method)) {
            NumberRuleBean info = new NumberRuleBean();
            info.setId(NumberGenTool.getId());

            String systemName = String.valueOf(this.formHM.get("systemName"));
            String systemCode = String.valueOf(this.formHM.get("systemCode"));
            String applicant = String.valueOf(this.formHM.get("applicant"));
            String mobile = String.valueOf(this.formHM.get("mobile"));

            info.setApplicant(applicant);
            info.setMobile(mobile);
            info.setSystemName(systemName);
            info.setSystemCode(systemCode);

            String username = userView.getUserName();
            String result = bo.registerSystem(info, username);
            if ("true".equalsIgnoreCase(result)) {
                this.getFormHM().put("result", true);
            } else {
                this.getFormHM().put("result", false);
                this.getFormHM().put("desc", result);
                log.warn("注册失败， userName:{}, param:{}, desc:{}", username, info.toString(), result);
            }
        }

        //# 输入框查询
        String subModuleId = (String) this.getFormHM().get("subModuleId");
        if (StringUtils.isNotBlank(subModuleId) && "gz_numberRule_query".equalsIgnoreCase(subModuleId)) {
            ArrayList<String> inputValues = (ArrayList<String>) this.getFormHM().get("inputValues");
            String tableConfig = bo.loadNumberRuleList(userView, this.frameconn, inputValues);
            this.formHM.clear();
            this.formHM.put("numberRuleTable", tableConfig);
        }
    }
}
