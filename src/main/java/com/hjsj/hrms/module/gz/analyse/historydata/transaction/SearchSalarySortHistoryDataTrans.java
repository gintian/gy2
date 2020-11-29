package com.hjsj.hrms.module.gz.analyse.historydata.transaction;

import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.SalaryHistoryDataService;
import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.impl.SalaryHistoryDataServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title SearchSalarySortHistoryDataTrans
 * @Description 切换薪资类别
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public class SearchSalarySortHistoryDataTrans extends IBusiness{
    @Override
    public void execute() throws GeneralException {
        Map return_data = new HashMap();
        String count = null;
        List dateList = new ArrayList();
        List countList = new ArrayList();
        SalaryHistoryDataService initSalaryHistoryData = new SalaryHistoryDataServiceImpl(frameconn, userView);
        try {
            //薪资类别号
            String salaryId = (String) this.getFormHM().get("salaryId");
            salaryId = PubFunc.decrypt(SafeCode.decode(salaryId));
            // 页面区分 history
            String transType = (String)this.getFormHM().get("transType");
            //业务日期
            String appdate = initSalaryHistoryData.getAppdate(salaryId, transType);
            if (StringUtils.isNotBlank(appdate)) {
                appdate = appdate.substring(0, 10);
                //发放次数
                Map countMap = initSalaryHistoryData.getCount(salaryId, appdate, transType);
                count = (String) countMap.get("maxCount");
                countList = (ArrayList) countMap.get("countList");
                //获取日期组件初始化年月数据
                dateList = initSalaryHistoryData.getDateList(salaryId, appdate,transType);
            }

            //工资报表
            ArrayList<LazyDynaBean> reportList = initSalaryHistoryData.getReportList(salaryId);
            List columnsFieldList = initSalaryHistoryData.getColumnsFieldList(salaryId);
            String querySql = initSalaryHistoryData.getSqldata(columnsFieldList, transType, salaryId, appdate, count,new ArrayList<String>(),"","","");

            //表格主页面
            String tableConfig = initSalaryHistoryData.getSalaryHistoryTableConfig(transType, salaryId, appdate, querySql);

            return_data.put("tableConfig", tableConfig);

            return_data.put("salaryId_encrypt", SafeCode.encode(PubFunc.encrypt(salaryId)));
            return_data.put("appdate_encrypt", SafeCode.encode(PubFunc.encrypt(appdate)));
            return_data.put("count_encrypt", SafeCode.encode(PubFunc.encrypt(count)));
            return_data.put("reportList", reportList);
            return_data.put("dateList", dateList);

            this.formHM.put("appdate", StringUtils.isBlank(appdate) ? "" : appdate);
            this.formHM.put("count", StringUtils.isBlank(count) ? "" : count);
            //次数list
            this.formHM.put("countList", countList);
            //报表输出用到的参数
            this.formHM.put("tablesubModuleId","salary_"+salaryId);
            this.formHM.put("return_code", "success");
            this.formHM.put("return_data", return_data);
        } catch (GeneralException e) {
            e.printStackTrace();
            this.formHM.put("return_code", "fail");
            this.formHM.put("return_msg", e.getErrorDescription());
        }
    }
}
