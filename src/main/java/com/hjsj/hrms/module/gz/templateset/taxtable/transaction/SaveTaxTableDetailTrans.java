package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 保存税率明细表交易类
 * @Author manjg
 * @Date 2019/12/3 14:40
 * @Version V1.0
 **/
public class SaveTaxTableDetailTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        String taxid = "";
        try {
            ITaxTableService taxTableService = new TaxTableServiceImpl(this.frameconn,this.userView);
            taxid = (String) this.getFormHM().get("taxid");
            ArrayList taxDetail = (ArrayList) this.getFormHM().get("taxDetail");
            List<DynaBean> taxList = (ArrayList<DynaBean>)this.getFormHM().get("taxData");
            boolean saveAll = (Boolean) this.getFormHM().get("saveAll");
            ArrayList<String> ids = (ArrayList) this.getFormHM().get("ids");
            if(saveAll){
                taxTableService.deleteTaxTableDetail(ids);
            }
            if(StringUtils.isNotBlank(taxid)){
                taxTableService.saveTaxTable(taxList);
            }
            taxid = PubFunc.encrypt(taxTableService.saveTaxTableDetail(taxList,taxDetail));
        }catch (GeneralException e){
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }
        this.formHM.put("taxid",taxid);
        this.formHM.put("return_code",return_code);
        this.formHM.put("return_msg",return_msg);
    }
}