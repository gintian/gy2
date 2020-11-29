package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * @Description 导出标准表数据交易类
 * @Author wangz
 * @Date 2019/12/3 12:02
 * @Version V1.0
 **/
public class ExportStandDataExcelTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        Map return_data = null;
        String stand_ids=(String)this.getFormHM().get("stand_ids");
        String pkg_id=(String)this.getFormHM().get("pkg_id");
        try{
            pkg_id = PubFunc.decrypt(SafeCode.decode(pkg_id));
            IStandTableService standTableService = new StandTableServiceImpl(this.frameconn,this.userView);
            return_data = standTableService.exportStandData(pkg_id,stand_ids);
            this.getFormHM().put("return_data",return_data);
        }catch (Exception e){
            return_code = "fail";
            this.getFormHM().put("return_msg",e.toString());
        }
        this.getFormHM().put("return_code",return_code);
    }
}
