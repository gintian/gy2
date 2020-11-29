package com.hjsj.hrms.module.gz.mytax.transaction;

import com.hjsj.hrms.module.gz.mytax.businessobject.MyTaxService;
import com.hjsj.hrms.module.gz.mytax.businessobject.impl.MyTaxServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTaxMainTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        MyTaxService myTaxService = new MyTaxServiceImpl(this.frameconn);
        String operateType = (String) this.getFormHM().get("type");
        Map return_data = new HashMap();
        String return_code = "success";
        String return_msg = "";
        try {
            if (StringUtils.equals(operateType, "main")) {
                if(StringUtils.isEmpty(userView.getA0100())){//如果a0100没有值 则代表是业务用户并且没有关联自助用户  不允许查看我的薪酬
                    return_data.put("isSelfServiceUser",'0');
                }else{
                    String queryYear = (String) this.getFormHM().get("year");//查询年份
                    if (StringUtils.isEmpty(queryYear)) {
                        return_data = myTaxService.initMyTaxData(this.userView);
                        String maxYear = (String) return_data.get("year");
                        if (StringUtils.isNotEmpty(maxYear)) {
                            List valueList = myTaxService.getMyTaxData(maxYear, userView);
                            return_data.put("values", valueList);
                            Map<String, String> monthSdsMap = myTaxService.getMonthSdsOfYear(userView, maxYear);
                            String ljse = monthSdsMap.get("ljse");//从中把累计税额取出来
                            monthSdsMap.remove("ljse");//移除出去，这数据不传到前台
                            return_data.put("ljse", ljse);
                            return_data.put("monthSds", monthSdsMap);
                        }
                    } else {
                        List valuesList = myTaxService.getMyTaxData(queryYear, this.userView);
                        return_data.put("values", valuesList);
                        Map<String, String> monthSdsMap = myTaxService.getMonthSdsOfYear(userView, queryYear);
                        String ljse = monthSdsMap.get("ljse");//从中把累计税额取出来
                        monthSdsMap.remove("ljse");//移除出去，这数据不传到前台
                        return_data.put("ljse", ljse);
                        return_data.put("monthSds", monthSdsMap);
                    }
                }
            }
        } catch (GeneralException e) {
            return_code = "fail";
            e.printStackTrace();
            return_msg = e.getErrorDescription();
        }
        this.getFormHM().put("return_data", return_data);
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_msg", return_msg);
    }
}
