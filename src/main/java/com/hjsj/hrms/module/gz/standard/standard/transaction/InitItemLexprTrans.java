package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

/**
 * @Description 二级指标表达式初始化交易类
 * @Author wangz
 * @Date 2019/12/3 11:57
 * @Version V1.0
 **/
public class InitItemLexprTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String item = (String)this.getFormHM().get("item");
        String item_id = (String)this.getFormHM().get("item_id");
        try {
            IStandTableService getItemLexprServer = new StandTableServiceImpl(this.frameconn,this.userView);
            List return_data = getItemLexprServer.getItemLexpr(item, item_id);
            this.getFormHM().put("return_data",return_data);
        } catch (Exception e) {
            e.printStackTrace();
            return_code = "fail";
            this.getFormHM().put("return_msg",e);
        }
        this.getFormHM().put("return_code",return_code);



    }
}
