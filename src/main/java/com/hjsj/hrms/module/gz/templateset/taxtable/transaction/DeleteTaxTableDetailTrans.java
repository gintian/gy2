package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @Description: 删除税率表明细数据交易类
 * @Author manjg
 * @Date 2019/12/3 14:40
 * @Version V1.0
 **/
public class DeleteTaxTableDetailTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        try {
            ITaxTableService taxTableService = new TaxTableServiceImpl(this.frameconn,this.userView);
            String taxid = PubFunc.decrypt((String) this.getFormHM().get("taxid"));
            int taxitem = (Integer) this.getFormHM().get("taxitem");
            String taxitems = taxid+"`"+taxitem;
            taxTableService.deleteTaxTableDetail(taxitems);
        }catch (GeneralException e){
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }
        this.getFormHM().put("return_code",return_code);
        this.getFormHM().put("return_msg",return_msg);
    }
}