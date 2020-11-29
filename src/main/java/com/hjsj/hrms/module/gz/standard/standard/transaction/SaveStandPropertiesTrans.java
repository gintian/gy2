/*
 * @(#)SaveStandardListOrg.java 2019年12月11日上午10:42:58
 * ehr
 * Copyright 2019 HJSOFT, Inc. All rights reserved.
 * HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

/**
 * @Description 保存标准表属性的交易类
 * @Author linjs
 * @Date 2019/12/11 10:43
 * @Version V1.0
 **/
public class SaveStandPropertiesTrans extends IBusiness{
    @Override
    public void execute() throws GeneralException {
        ContentDAO contentDAO = new ContentDAO(this.frameconn);
        String return_msg = "";//错误信息
        String return_code = "success";//运行是否成功的标识
        try {
                String stand_id = (String) this.getFormHM().get("stand_id");//薪资标准表编号
                String pkg_id = (String) this.getFormHM().get("pkg_id");//历史沿革套序号
                MorphDynaBean dataMap = (MorphDynaBean)this.getFormHM().get("standInfo");
                String standName = (String)dataMap.get("standName");
                String owner_org = (String)dataMap.get("owner_org");//要保存的归属单位的信息
                String stand_id_de = PubFunc.decrypt(stand_id);//历史沿革套序号脱密
                String pkg_id_de = PubFunc.decrypt(pkg_id);//历史沿革套序号脱密
                IStandTableService standTableServiceImpl = new StandTableServiceImpl(this.frameconn, this.userView);
                return_code = standTableServiceImpl.saveStandardListOrg(pkg_id_de, stand_id_de,standName,owner_org);
        } catch (GeneralException e) {
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }
        this.formHM.put("return_code", return_code);
        this.formHM.put("return_msg", return_msg);
    }

}
