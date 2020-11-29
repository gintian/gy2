package com.hjsj.hrms.module.analyse.transcation;

import com.hjsj.hrms.module.analyse.bussinessobject.IAnalyseService;
import com.hjsj.hrms.module.analyse.bussinessobject.impl.IAnalyseServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计分析项数据交易类
 * @author wangbo
 * @category hjsj 2019-12-16
 * @version 1.0
 */
public class AnalyseChartDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        String zhanbi = "zhanbi";
        String tongbi = "tongbi";
        String pingjun = "pingjun";
        String leiji = "leiji";
        String doubleType = "doubleType";
        try {
            IAnalyseService iAnalyseSer = new IAnalyseServiceImpl(this.frameconn, this.userView);
            String showChart = (String) this.formHM.get("show_chart");
            String viewTable = (String) this.formHM.get("view");
            String b0110 = (String) this.formHM.get("b0110");
            String unit = (String) this.formHM.get("unit");
            if (StringUtils.isNotBlank(b0110)) {
                b0110 = PubFunc.decrypt(b0110);
            }
            if(StringUtils.isNotBlank(unit)){
            	unit = "（"+unit+"）";
            }
            String year = (String) this.formHM.get("year");
            String items = (String) this.formHM.get("items");
            if (StringUtils.equalsIgnoreCase(showChart, zhanbi)) {//占比
                return_data = iAnalyseSer.getZhanBiViewData(viewTable, items, b0110, year,unit);

            } else if (StringUtils.equalsIgnoreCase(showChart, tongbi)) {//同比
                return_data = iAnalyseSer.getTongBiViewData(viewTable, items, b0110, year,unit);

            } else if (StringUtils.equalsIgnoreCase(showChart, pingjun)) {
                return_data = iAnalyseSer.getPingJunViewData(viewTable, b0110, year,unit);

            } else if (StringUtils.equalsIgnoreCase(showChart, leiji)) {
                return_data = iAnalyseSer.getLeiJiViewData(viewTable, b0110, year,unit);

            } else if (StringUtils.equalsIgnoreCase(showChart, doubleType)) {
                return_data = iAnalyseSer.getMoreItemAndTypeViewData(viewTable, b0110, year,unit);
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }  finally {
            this.formHM.put("return_code", return_code);
            this.formHM.put("return_msg", return_msg);
            this.formHM.put("return_data", return_data);
        }
    }
}
