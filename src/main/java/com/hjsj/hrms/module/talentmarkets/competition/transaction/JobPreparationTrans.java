package com.hjsj.hrms.module.talentmarkets.competition.transaction;


import com.hjsj.hrms.module.talentmarkets.competition.businessobject.JobPreparationService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.JobPreparationServiceImpl;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @Description 岗位编制交易类
 * @Author manjg
 * @Date 2019/7/24 10:40
 * @Version V1.0
 **/
public class JobPreparationTrans extends IBusiness {
    @Override
    public void execute() {
        String return_msg = "";
        String return_code = "success";
        String operateType = (String) this.getFormHM().get("operateType");
        String queryMethodStr = (String) this.getFormHM().get("queryMethod");
        int queryMethod = 0;
        if(StringUtils.isNotEmpty(queryMethodStr)){
            queryMethod = Integer.parseInt(queryMethodStr);
        }

        //表格唯一id
        String subModuleId = "jobPreparationTable";
        try {
            JobPreparationService postEstablishmentService = new JobPreparationServiceImpl(subModuleId,this.frameconn,this.userView);
            //获取配置编制子集信息项
            Map<String, String> subsetConfig = TalentMarketsUtils.getSubsetConfig(this.frameconn);
            //岗位编制子集
            String subset = subsetConfig.get("psSet");
            //定员人数
            String workFixed = subsetConfig.get("psWorkfixed");
            //实有人数
            String workExist = subsetConfig.get("psWorkexist");
            //岗位编制子集\定员人数\实有人数 是否配置如果其中一项不配置，则不能进行岗位编制的使用
            //boolean notConfig = StringUtils.isEmpty(subset) || StringUtils.isEmpty(workFixed) || StringUtils.isEmpty(workExist);
            boolean notConfig = StringUtils.equalsIgnoreCase(subset,"＃") || StringUtils.equalsIgnoreCase(workFixed,"＃") || StringUtils.equalsIgnoreCase(workExist,"＃");
            //三项都配置了，则可以使用岗位编制
            if(!notConfig){
                if(StringUtils.equalsIgnoreCase(operateType,"search")){
                    postEstablishmentService.refreshTableData(queryMethod);
                }else{
                    //获取config
                    String config = postEstablishmentService.getTableConfig();
                    this.getFormHM().put("gridConfig",config);
                }
            }else{
                return_msg = "tm.contendPos.noconfig";
                return_code = "fail";
            }
            //检验岗位发布申请的岗位状态
            /*if(StringUtils.equalsIgnoreCase(operateType,"checkPostStatus")){
                ArrayList<String> postList = (ArrayList<String>) this.formHM.get("e01a1Arr");
                postEstablishmentService.checkPostStatus(postList);
            }*/
        }catch (GeneralException e){
            return_code = "fail";
            return_msg = e.getErrorDescription();
            e.printStackTrace();
        }
        this.getFormHM().put("return_msg",return_msg);
        this.getFormHM().put("return_code",return_code);

    }
}
