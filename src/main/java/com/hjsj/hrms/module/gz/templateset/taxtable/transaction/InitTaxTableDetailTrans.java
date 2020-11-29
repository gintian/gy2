package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 初始化税率表明细数据交易类
 * @Author manjg
 * @Date 2019/12/3 14:40
 * @Version V1.0
 **/
public class InitTaxTableDetailTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        HashMap return_data = new HashMap();
        List taxModeData = new ArrayList();//获取计税方式数据
        String return_code = "success";
        String return_msg = "";
        String gridConfig = "";
        try {
            String operateType = (String) this.getFormHM().get("operateType");
            ITaxTableService taxTableService = new TaxTableServiceImpl(this.frameconn,this.userView);
            if(StringUtils.equalsIgnoreCase(operateType,"getTaxMode")){
                taxModeData = taxTableService.getTaxModeCodeItem();
                this.formHM.put("taxModeData",taxModeData);
            }else if (StringUtils.equalsIgnoreCase(operateType,"reloadData")){
                String taxid = (String) this.getFormHM().get("taxid");
                taxTableService.getTaxTableDetailConfig(PubFunc.decrypt(taxid));
            }else {
                String postNum = (String) this.getFormHM().get("postNum");
                gridConfig = taxTableService.getTaxTableDetailConfig(PubFunc.decrypt(postNum));
                return_data.put("gridConfig",gridConfig);
            }
        }catch (GeneralException e){
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }
        this.formHM.put("return_data",return_data);
        this.formHM.put("return_code",return_code);
        this.formHM.put("return_msg",return_msg);
    }
}
