package com.hjsj.hrms.module.selfservice.usercenter.transction;


import com.hjsj.hrms.module.selfservice.usercenter.businessobject.IUserCenterService;
import com.hjsj.hrms.module.selfservice.usercenter.businessobject.impl.UserCenterServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
/**
 * @Description 下载pdf版本说明书
 * @Author sheny
 * @Date 2020/4/10 13:32
 * @Version V1.0
 **/
public class ViewCardTrans extends IBusiness {
    private Logger log = LoggerFactory.getLogger(ViewCardTrans.class);
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        try {
            //说明书类型 dept:部门职责说明书 post：岗位职责说明书 employee:人员基本信息表
            String cardType = (String) this.getFormHM().get("cardType");
            //获取人员库
            String nbase = (String) this.getFormHM().get("nbase");
            //获取人员编号
            String a0100  = (String) this.getFormHM().get("a0100");

            if (StringUtils.isNotEmpty(nbase)&&StringUtils.isNotEmpty(a0100)){
                nbase = PubFunc.decrypt(nbase);//解密
                a0100 = PubFunc.decrypt(a0100);//解密
            }
            log.info("ViewCardTrans::cardType:{},nbase:{},a0100:{}",cardType,nbase,a0100);
            IUserCenterService IUserCenter = new UserCenterServiceImpl(this.frameconn,this.userView);
            Map fileNameMap = IUserCenter.getFileNameMap(cardType,a0100,nbase);
            Map btnPrivMap = IUserCenter.getBtnFunction(cardType);

            if (MapUtils.isEmpty(fileNameMap)){
                return_code = "fail";
                return_msg = "haveNoTabid";
            } else {
                return_data.put("pdfFileName", fileNameMap.get("pdfFileName"));
                return_data.put("wordFileName", fileNameMap.get("wordFileName"));
                return_data.put("btnPrivMap", btnPrivMap);
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        } finally {
            this.getFormHM().put("return_code",return_code);
            this.getFormHM().put("return_msg",return_msg);
            this.getFormHM().put("return_data",return_data);
        }
    }
}
