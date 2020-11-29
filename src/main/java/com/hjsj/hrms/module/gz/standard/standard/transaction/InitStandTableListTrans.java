package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 标准表列表初始化交易类
 * @Author wangz
 * @Date 2019/12/3 11:54
 * @Version V1.0
 **/
public class InitStandTableListTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        ContentDAO contentDAO = new ContentDAO(this.frameconn);
        HashMap return_data = new HashMap();//表格信息
        Map return_priv = new HashMap();//创建标准表权限信息
        String return_msg = "";//错误信息
        String return_code = "success";//运行是否成功的标识
        try {
                String pkg_id = (String) this.getFormHM().get("pkg_id");//历史沿革套序号
                pkg_id=PubFunc.decrypt(pkg_id);
                IStandTableService standTableServiceImpl = new StandTableServiceImpl(this.frameconn ,this.userView);
                //pkg_id为历史沿革列表传入的参数
                String gridConfig = standTableServiceImpl.getStandardTableConfig(pkg_id);
                return_priv = standTableServiceImpl.getStandardCreatePriv(pkg_id);
                return_data.put("gridConfig", gridConfig);
        } catch (GeneralException e) {
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }
        this.formHM.put("return_data", return_data);
        this.formHM.put("return_priv", return_priv);
        this.formHM.put("return_msg", return_msg);
        this.formHM.put("return_code", return_code);
    }
}
