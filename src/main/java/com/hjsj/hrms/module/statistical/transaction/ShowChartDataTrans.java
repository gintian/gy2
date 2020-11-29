package com.hjsj.hrms.module.statistical.transaction;

import com.hjsj.hrms.module.statistical.businessobject.StatisticalService;
import com.hjsj.hrms.module.statistical.businessobject.impl.StatisticalServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Titile: ShowChartDataTrans
 * @Description:移动门户：一维、二维统计页面穿透到列表页面数据加载
 * @Company:hjsj
 * @Create time: 2019年4月8日10:43:24
 * @author: wangbs
 * @version 1.0
 *
 */

public class ShowChartDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        Map returnMap = new HashMap();

        try{
            String statid = (String) this.formHM.get("statid");
            String infokind = (String) this.formHM.get("infokind");
            String stattype = (String) this.formHM.get("stattype");
            int pageIndex = (Integer) this.formHM.get("pageIndex");
            int pageSize = (Integer) this.formHM.get("pageSize");
            String filterId = (String) this.formHM.get("filterId");
            filterId = filterId == null? "":filterId;

            StatisticalService statisticalService = new StatisticalServiceImpl(this.frameconn);

            if ("1".equalsIgnoreCase(stattype)) {//一维统计穿透列表
                String showLegend = (String) this.formHM.get("showLegend");
                return_data = statisticalService.getStatisicalPersonList(this.userView, statid, infokind, showLegend, pageIndex, pageSize,filterId);
            } else if ("2".equalsIgnoreCase(stattype)) {//二维统计穿透列表
                String h = (String) this.formHM.get("h");
                String v = (String) this.formHM.get("v");
                return_data = statisticalService.getStatisicalPersonList(this.userView, statid, infokind, v, h, pageIndex, pageSize,filterId);
            } else if ("3".equalsIgnoreCase(stattype)) {//三维统计穿透列表
                String h = (String) this.formHM.get("h");
                String v = (String) this.formHM.get("v");
                String vtotal = (String) this.formHM.get("vtotal");
                String htotal = (String) this.formHM.get("htotal");
                String vnull = (String) this.formHM.get("vnull");
                String hnull = (String) this.formHM.get("hnull");
                String crosswise = (String) this.formHM.get("crosswise");
                String lengthways = (String) this.formHM.get("lengthways");
                return_data = statisticalService.getStatisicalPersonList(this.userView, statid, infokind, v, h, lengthways, crosswise, vtotal, htotal, vnull, hnull, pageIndex, pageSize,filterId);
            }

            returnMap.put("return_code",return_code);
            returnMap.put("return_msg",return_msg);
            returnMap.put("return_data",return_data);
            this.formHM.put("returnMap",returnMap);
        } catch (GeneralException e) {
            return_code = "fail";
            return_msg = e.getErrorDescription();

            returnMap.put("return_code",return_code);
            returnMap.put("return_msg",return_msg);
            this.formHM.put("returnMap",returnMap);
        }
    }
}
