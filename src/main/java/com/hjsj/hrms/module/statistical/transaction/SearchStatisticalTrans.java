package com.hjsj.hrms.module.statistical.transaction;

import com.hjsj.hrms.module.statistical.businessobject.StatisticalService;
import com.hjsj.hrms.module.statistical.businessobject.impl.StatisticalServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @Titile: SearchStatisticalTrans
 * @Description:移动门户：统计分析首页面数据加载；人员详细信息页面参数解密处理
 * @Company:hjsj
 * @Create time: 2019年4月8日10:43:24
 * @author: wangbs
 * @version 1.0
 *
 */

public class SearchStatisticalTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map returnMap = new HashMap();

        try{
            String type = (String) this.formHM.get("type");
            if("main".equalsIgnoreCase(type)){//统计分析首页面数据加载
                StatisticalService statisticalService = new StatisticalServiceImpl(this.frameconn);
                List return_data = statisticalService.listAllStatisial(this.userView);
                returnMap.put("return_data",return_data);
            }else if("decode".equalsIgnoreCase(type)){//列表页穿透到人员详细信息页面，调用的老交易类所需参数不需要加密，故做解密处理
                String a0100 = PubFunc.decrypt((String) this.getFormHM().get("a0100"));
                String nbase = PubFunc.decrypt((String) this.getFormHM().get("nbase"));

                Map return_data = new HashMap();
                return_data.put("a0100", a0100);
                return_data.put("nbase", nbase);
                returnMap.put("return_data",return_data);
            }
            returnMap.put("return_code",return_code);
            returnMap.put("return_msg",return_msg);
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
