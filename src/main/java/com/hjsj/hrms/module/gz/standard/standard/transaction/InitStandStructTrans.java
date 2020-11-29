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
 * @Description 标准表结构初始化交易类
 * @Author wangz
 * @Date 2019/12/3 11:56
 * @Version V1.0
 **/
public class InitStandStructTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map returnData = new HashMap();
        String returnCode = "success";
        String returnMsgCode = "";
        IStandTableService standTableServiceImpl = new StandTableServiceImpl(this.frameconn,this.userView);
        try{
            String pkg_id = (String) this.getFormHM().get("pkg_id");
            pkg_id = PubFunc.decrypt(pkg_id);
            String stand_id = (String) this.getFormHM().get("stand_id");
            stand_id = PubFunc.decrypt(stand_id);
            String init_type = (String)this.getFormHM().get("init_type");
            Map standStructInforFormat = new HashMap();
            Map standStructInfor = new HashMap();
            //数据初始化
            if(StringUtils.equalsIgnoreCase(init_type,"init")){
                standStructInfor = standTableServiceImpl.getStandStructInfor(pkg_id, stand_id);

            }else if(StringUtils.equalsIgnoreCase(init_type,"transform")){
                MorphDynaBean standStructInforBean = (MorphDynaBean) this.getFormHM().get("standStructInfor");
                standStructInfor  = PubFunc.DynaBean2Map(standStructInforBean);
            }
            standStructInforFormat = standTableServiceImpl.getFormatStandStructInfor(standStructInfor);
            returnData.put("standStructInfor",standStructInforFormat);
        }catch (GeneralException e){
            returnCode = "fail";
            returnMsgCode = "initStandStructError";
        }
        this.getFormHM().put("returnData",returnData);
        this.getFormHM().put("returnCode",returnCode);
        this.getFormHM().put("returnMsgCode",returnMsgCode);
    }
}
