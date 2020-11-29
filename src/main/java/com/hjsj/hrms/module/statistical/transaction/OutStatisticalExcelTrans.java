package com.hjsj.hrms.module.statistical.transaction;

import com.hjsj.hrms.module.statistical.businessobject.StatisticalService;
import com.hjsj.hrms.module.statistical.businessobject.impl.StatisticalServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title OutStatisticalExcelTrans
 * @Description 导出统计图、表到Excel
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/5/12
 * @Version 1.0.0
 */
public class OutStatisticalExcelTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap<>();
        try{
            JSONObject viewAndTableData = (JSONObject) this.formHM.get("viewAndTableData");
            StatisticalService statisticalService = new StatisticalServiceImpl(this.frameconn);
            String fileName = statisticalService.outStatisticalExcel(this.userView, viewAndTableData);
            return_data.put("fileName", fileName);
        }catch(GeneralException e){
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }finally {
            this.formHM.put("return_code", return_code);
            this.formHM.put("return_msg", return_msg);
            this.formHM.put("return_data", return_data);
        }
    }
}
