package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @Description 二级指标表达式删除交易类
 * @Author wangz
 * @Date 2019/12/3 11:57
 * @Version V1.0
 **/
public class DeleteItemLexprTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String item = (String)this.getFormHM().get("item");
        String item_id = (String)this.getFormHM().get("item_id");
        try {
            StandTableServiceImpl deleteItemLexprServer = new StandTableServiceImpl(this.frameconn,this.userView);
            return_code = deleteItemLexprServer.deleteItemLexpr(item, item_id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return_code = "fail";
            this.getFormHM().put("return_msg",e);
        }
        this.getFormHM().put("return_code",return_code);
    }
}