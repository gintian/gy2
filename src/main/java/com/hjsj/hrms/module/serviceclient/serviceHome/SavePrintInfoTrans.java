package com.hjsj.hrms.module.serviceclient.serviceHome;

import com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject.ServiceSettingBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

@SuppressWarnings("serial")
public class SavePrintInfoTrans extends IBusiness {

    @Override
    @SuppressWarnings("unused")
    public void execute() throws GeneralException {
        Integer printCount = (Integer) this.formHM.get("printCount");//打印份数
        String serviceId = (String) this.formHM.get("serviceId");//服务号
        String ip = (String) this.formHM.get("ip");//获取ip
       //Integer pageCount = (Integer) this.formHM.get("pageCount");//打印份数
       Integer templatePage = (Integer) this.formHM.get("templatePage");//当前服务页数
        int usedPage = printCount * templatePage;  //使用纸张数 = [打印份数] 乘以 [每份页数]
        ServiceSettingBo bo = new ServiceSettingBo(this.frameconn, this.userView);
        boolean service = bo.saveHistoriPrint(serviceId, usedPage, printCount,ip);
        //pageCount = pageCount>usedPage?pageCount-usedPage:0;最新剩余纸张数 = 目前纸张数-使用纸张数；
    }
}
