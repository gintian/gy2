package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionJobsService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionJobsServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title PortalDashboardServiceImpl
 * @Description 竞聘分析交易类
 * @Company hjsj
 * @Author wangbs、hanqh
 * @Date 2019/8/15
 * @Version 1.0.0
 */

/**
 * 〈类功能描述〉<br> 
 * 〈竞聘分析交易类〉
 *
 * @Author xuchangshun
 * @Date 2019/8/15
 * @since 1.0.0
 */
public class CompetitionAnalyseTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        CompetitionJobsService competitionJobsService = new CompetitionJobsServiceImpl(this.frameconn, this.userView);
        try{
            // 类型判断
            String operaType = (String)this.getFormHM().get("operaType");
            // 开始时间
            String startDate = (String) this.getFormHM().get("startDate");
            // 结束时间
            String endDate = (String) this.getFormHM().get("endDate");
            //封装前台传递的数据
            Map<String,String> paramMap = new HashMap<String, String>();
            paramMap.put("operaType",operaType);
            paramMap.put("startDate",startDate);
            paramMap.put("endDate",endDate);
            // 若点击查询所选机构的数据,存入选择的结点
            if("orgIdData".equals(operaType)){
                // 获取节点值
                String orgId = (String)this.getFormHM().get("orgId");
                paramMap.put("orgId",orgId);
            }
            Map returnMap = competitionJobsService.getChartsData("4", userView,paramMap);
            this.formHM.put("return_data",returnMap);
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
