package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 标准表数据编辑界面数据初始化交易类
 * @Author wangz
 * @Date 2019/12/3 12:01
 * @Version V1.0
 **/
public class InitStandDataTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map returnData = new HashMap();
        String returnCode = "success";
        String returnMsgCode = "";
        try {
            //标准表包id
            String pkg_id = (String) this.getFormHM().get("pkg_id");
            pkg_id = PubFunc.decrypt(pkg_id);
            //标准表id
            String stand_id = (String)this.getFormHM().get("stand_id");
            stand_id = PubFunc.decrypt(stand_id);
            //操作类型
            String initType = (String)this.getFormHM().get("initType");
            IStandTableService standTableServiceImpl = new StandTableServiceImpl(this.frameconn,this.userView);
            if(StringUtils.equalsIgnoreCase(initType,"edit")){
                Map standData = standTableServiceImpl.getStandData(pkg_id,stand_id);
                if (StringUtils.isEmpty((String)standData.get("returnMsgCode"))){
                    returnData.put("standData",standData);
                } else {
                    returnCode = "fail";
                    returnData.put("returnMsgCode",(String) standData.get("returnMsgCode"));
                    returnMsgCode = ("verifFieldItemError");
                }
            }else if(StringUtils.equalsIgnoreCase(initType,"create")){
                MorphDynaBean standStructInfor = (MorphDynaBean)this.getFormHM().get("paramsInfor");
                Map standStructInforMap = PubFunc.DynaBean2Map(standStructInfor);
                Map standData = standTableServiceImpl.getStandData(pkg_id,"",standStructInforMap);
                returnData.put("standData",standData);
            }else if(StringUtils.equalsIgnoreCase(initType,"struct")){
                MorphDynaBean standStructInfor = (MorphDynaBean)this.getFormHM().get("paramsInfor");
                Map standStructInforMap = PubFunc.DynaBean2Map(standStructInfor);
                Map standData = standTableServiceImpl.getStandData(pkg_id,stand_id,standStructInforMap);
                returnData.put("standData",standData);
            }
        }catch (GeneralException e){
            returnCode = "fail";
            returnMsgCode = e.getErrorDescription();
        }
        this.getFormHM().put("returnData",returnData);
        this.getFormHM().put("returnCode",returnCode);
        this.getFormHM().put("returnMsgCode",returnMsgCode);

    }
}
