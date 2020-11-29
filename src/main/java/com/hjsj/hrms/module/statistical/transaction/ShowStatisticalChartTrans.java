package com.hjsj.hrms.module.statistical.transaction;

import com.hjsj.hrms.module.statistical.businessobject.StatisticalService;
import com.hjsj.hrms.module.statistical.businessobject.impl.StatisticalServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Titile: ShowStatisticalChartTrans
 * @Description:移动门户：一维统计页面数据加载；一维统计页面切换统计类型
 * @Company:hjsj
 * @Create time: 2019年4月8日10:43:24
 * @author: wangbs
 * @version 1.0
 *
 */

public class ShowStatisticalChartTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map returnMap = new HashMap();
        Map return_data = new HashMap();

        String statid = (String) this.formHM.get("statid");
        String infokind = (String) this.formHM.get("infokind");
        String type = (String) this.formHM.get("type");
        String org_filter = (String) this.formHM.get("org_filter");
        String filterId = (String) this.formHM.get("filterId");
        String sformula_id = (String) this.formHM.get("sformula_id");
        try {
            StatisticalService statisticalService = new StatisticalServiceImpl(this.frameconn);
            return_data = statisticalService.getStatisicalChartData(this.userView,statid,infokind,sformula_id,org_filter,filterId);
            returnMap.put("return_code", return_code);
            returnMap.put("return_msg", return_msg);
            returnMap.put("return_data", return_data);
            this.formHM.put("returnMap", returnMap);
        } catch (GeneralException e) {
            return_code = "fail";
            return_msg = e.getErrorDescription();

            returnMap.put("return_code",return_code);
            returnMap.put("return_msg",return_msg);
            this.formHM.put("returnMap",returnMap);
        }
    }
}
