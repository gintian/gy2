package com.hjsj.hrms.module.system.distributedreporting.reportdata;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * 
 * 上报数据界面操作交易类
 * @Titile: OperaReportDataTrans
 * @Description:
 * @Company:hjsj
 * @Create time: 2019年5月30日下午3:25:41
 * @author: Zhiyh
 * @version 1.0
 *
 */
public class OperaReportDataTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String operaType = (String) this.formHM.get("operaType");
            ReportDataBo bo = new ReportDataBo(userView, frameconn);
            if (StringUtils.equals(operaType, "importDataZip")) {//上传数据包
                this.getFormHM().put("return_code", bo.importDataZip(formHM));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
