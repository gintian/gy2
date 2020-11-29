package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * @Description 删除标准表交易类
 * @Author wangz
 * @Date 2019/12/3 12:04
 * @Version V1.0
 **/
public class DeleteStandTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        ContentDAO contentDAO = new ContentDAO(this.frameconn);
        String return_msg = "";//错误信息
        String return_code = "success";//运行是否成功的标识
        String type = (String) this.getFormHM().get("operate_type");//操作类型标识
        try {
            if ("deleteStandList".equalsIgnoreCase(type)) {
                String stand_id=(String) this.getFormHM().get("id");
                String pkg_id=(String) this.getFormHM().get("pkg_id");
                String pkg_id_de = PubFunc.decrypt(pkg_id);//历史沿革套序号脱密
                IStandTableService standTableServiceImpl = new StandTableServiceImpl(this.frameconn, this.userView);
                String[] stand_ids = stand_id.split(",");//薪资标准表编号
                Map checkData = standTableServiceImpl.checkStandDel(stand_ids,pkg_id);
                boolean isDelete = (boolean) checkData.get("delFlag");
                if(isDelete){
                    for (int i = 0; i < stand_ids.length; i++) {
                        String stand_id_de = PubFunc.decrypt(stand_ids[i]);//薪资标准表编号脱密
                        return_code = standTableServiceImpl.deleteStand(pkg_id_de, stand_id_de);
                    }
                }else{
                    return_code = "fail";
                    this.getFormHM().put("noDelStand",checkData.get("msg"));
                }
            }
        } catch (GeneralException e) {
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }
        this.formHM.put("return_code", return_code);
        this.formHM.put("return_msg", return_msg);

    }
}
